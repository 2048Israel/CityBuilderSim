package ham.citybuildersim;

import java.util.Arrays;

/**
 * A painted tile as pixels: an int[] of 0xAARRGGBB, a row at a time, at a whole number of pixels a plot - what the map view hands PixelWriter.setPixels() for a tile's image.
 *
 * WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.6). The
 * tile painter says what each plot is; the map draws a tile as one image,
 * kept in a cache, at 4 px a plot for the near view (8 px above 6 px a plot)
 * and, as blocks, at 2 px for the middle view and 1 px for the far (MapFrame,
 * 0.7.61). This turns a painted tile into that image, in the mockup's
 * colours: its ground (the legend's grass, forest, fresh water, sea and
 * beach), ground the city does not own dimmed toward the
 * map's background, a resource's sites tinted with its colour - half grey
 * where its holding is being worked, grey where it is worked out (the
 * mockup's star 7) - the roads by kind, and every building in its class's
 * fill, edged from 6 px a plot (the mockup's), the flats darker; a mine or
 * well grey on its site with a mark of its resource's colour at its centre.
 * Pure: the same tile gives the same pixels. The design measured 24 to 31 us
 * a tile at 4 px, 81 to 134 at 8, 8 at 1.
 *
 * THE MOCKUP'S WIDTHS AND SIZES (J3b). J3 filled a road's whole plot and drew
 * every building a pixel in from its plot, so at 4 px a plot a home was a
 * 2 x 2 speck on the grass. From 4 px a road was the mockup's line - gravel
 * 0.3 of its plot, paved 0.52 with a dark casing, a highway 0.86 with its
 * own and a dashed centre line from 6 px - joined to each road beside it at
 * the narrower one's width, with its bridge's deck under it (a highway's
 * still is; a street's since 0.7.88 is its verge and surface, below); a one-plot
 * building is drawn at its own land's side (0.7.64: a House 0.91 of its
 * plot; the mockup's 0.6 to 0.92 by a hash before), set against the road it
 * faces, so a street's homes line it; and
 * below 4 px a paved road is drawn in its casing's grey, which a pixel of
 * stands out from the grass where its own does not. BLOCKS (blocks()) are
 * the mockup's middle and far views: each 4 or 8 plots square in its
 * dominant kind's colour at an opacity by how much of it is built, the roads
 * over them - what the whole city looks like at the land office's zoom.
 *
 * TRACK (0.7.72, batch N3; redrawn as rail, 0.8.1, batch FR0). The railway's
 * lines are drawn plot by plot as track (TilePainter.RAIL): from 4 px a plot
 * a ballast bed with one steel centre line and a sleeper tick a plot, and
 * from 6 px two steel rails on sleepers (double track where the main line is
 * marked), joined to the track beside it, on a deck over fresh water; a road
 * crossing it drawn over the bed with the rails showing through and level
 * crossing marks, or bridging it where an arterial or boulevard crosses the
 * main line; below 4 px and in the blocks, its plots in RAIL_DARK with
 * cross-ticks every block from 2 px. The mockup drew no railway, so the
 * colours are a map's convention (star N3-9, spec-freight 4.3). Since FX5
 * (0.8.18; Jerus, 2026-10-20: "do trains make 90 degree turns?") a corner of
 * the railway is an arc, not an L: the turns of the trains' routes
 * (CityMap.trainRoutes()) are TrackCurves' bends, drawn by the arcs of
 * bends() over the plots they replace (Input.curved), and a yard's ladders
 * are the S curves the routes run along (ladderRails()), so the drawing and
 * the trains share one geometry. The highways' joints are drawn so too (FX5
 * slice D; Jerus: "once it finishes its still a tad wierd"): where a
 * 45-degree stretch meets straight highway the carriageway turns along an arc
 * (TrackCurves.HIGHWAY_RADIUS, highwayBends()) in place of the plots it
 * replaces, and a stretch that ends on its diagonal ends squared across its
 * heading (diagEndOpen()).
 *
 * YARDS AND JUNCTIONS (0.8.1, batch FR0; spec-freight 4.2, 4.3 and 4.6,
 * D15 and D19). From 4 px a plot a Rail Terminal (and since FR7 a Rail
 * Depot, with fewer sidings) is drawn as a yard
 * (drawYard()): a fan of sidings closing to the line at each end, a freight
 * shed, the freight it handles - ore stockpiles, or at the freight station
 * of a railway carrying goods rows of boxes and two gantries - and wagons
 * standing on as many sidings as the fleet's share of its wagon sets gives.
 * Since FX5 (Y2) a yard as long as a unit train, which spans two or three
 * tiles, is drawn whole across them (drawWholeYard(): Input.yards).
 * From JUNCTIONS_FROM the junctions are drawn by rule, none of them a
 * building: signals where arterials or boulevards cross - since FX5 (Jerus,
 * 2026-10-20, "Mostly signals") one box over the whole crossing and zebras
 * only outside it, and a roundabout only where five or more roads meet, one
 * ring about the crossing's middle (Crossings; it was one at every crossing of
 * boulevards, and a wide crossing drew nine) - diamonds where a street passes
 * beneath a ramp, stacks where highways cross; and from 4 px a highway's open
 * end comes down to the ground. (Batch FR7, slice 4) Where a crossing has kept its ground
 * (InterchangeGround: a 19-plot reserve for a cloverleaf, an 11-plot one for a
 * trumpet) the stack gives way to the loops drawn on it (loops()); and a +
 * of streets, a street's T on an arterial, are drawn with kerb corners, wide
 * where an estate's trucks turn (kerbs()).
 *
 * STREETS AS VERGE AND SURFACE (0.7.88, batch RD2; the project's
 * spec-roads-and-ports.md 2.1 and 5, its prototype's render()). A street's
 * plot is its right of way: drawn as VERGE, with its surface down its middle
 * - gravel a speckled light grey (tan until FR7), paved a smooth grey, a band half the plot wide for a street of half
 * width (15 m) and the whole plot for one of full width (30 m) - joined to
 * the street beside it each way it runs; a TRACK, a street the city has
 * bought no road for, a dashed brown line on the verge; an arterial and a
 * boulevard's two rows drawn as the streets they are; a bridge's deck under
 * its surface. A highway is drawn over a street that passes beneath it, dark
 * with its dashed centre line. Ground under a building is drawn cleared
 * (forest as grass; spec 2.6, the zoning study's Z7), and a building packed
 * at the city's edge without a street (R7) a shade darker.
 *
 * THE AVENUES (batch CL4; Jerus, 2026-10-20: "having some slanted roads and
 * curves"). The four 45-degree avenues the district plans lay out from the
 * founding site (DistrictPlan, AVENUES) are staircases of arterial street
 * plots, which this drew as a chain of crosses; from LINES_FROM px a plot each
 * arm is one smooth band along its line through the steps' middles, in its
 * street's colour and width, as a highway's 45-degree stretch is - the
 * staircase's plots and the outer plots between its steps verge outside the
 * band, a street that crosses it drawn through, its ends squared across the
 * heading, its bridge a deck along the band, and where it meets another street
 * a crossing counted by its roads, the avenue's two directions among them
 * (Crossings; avenuePlot(), avenueNotch(), avenueEnds(), avenueOrigin()).
 *
 * THE CURVES (batch CL4, slice D; Jerus, 2026-10-20: "All three"). The two
 * kinds of street the district plans give a smooth line - a crescent, the
 * cross street of a long-block homes cell far from the founding site, a
 * quarter of a circle between its rings, and the street a cut lays along
 * water, a path of plots with its corners rounded (DistrictPlan, CURVES;
 * StreetCurves) - are, from LINES_FROM px a plot, one band along each line
 * (curves(): curveLine(), drawCurve()), in the street's colour and width,
 * over the curve's own plots and the plots its band reaches that the plan keeps
 * free of buildings, never over a building, the railway or a bridge's deck; the
 * plots are verge where the band does not cover them, a street that crosses the
 * curve is drawn across the band, and the dead end a curve leaves from and the
 * street it meets keep their bars, the band starting at their middles
 * (curveDrawn(), curveArm(), curveLink(), curveLooks()).
 */
public final class TileRaster {

    private TileRaster() { }

    /**
     * Each ground class's colour, by World's class (GRASS, FOREST, FRESH,
     * SALT, SAND): the mockup's legend; and since 0.7.127 (batch E7;
     * spec-globe 3.9) ICE, the founding globe's ice, 0xdfe7ee, and DESERT,
     * CSS's tan (#d2b48c), warmer and darker than the beach's sand; and
     * since ET1 SHALLOW, the sea no deeper than 30 m, 0x4f87b5 (the study's
     * mockup, earth2/mockups.py): a lighter blue than the deep sea's, darker
     * than the lakes'.
     */
    public static final int[] GROUND = { 0xffa8c47e, 0xff527c45, 0xff78b4dc, 0xff2f5d88, 0xffe6daaa, 0xffdfe7ee, 0xffd2b48c, 0xff4f87b5 };

    /**
     * The grey each relief paints on open dry ground (ET1 slice D; spec-earth2-terrain 9.3; Jerus 2026-10-12, D4),
     * by World's relief (FLAT, FOOTHILL, STEEP, VERY_STEEP): FLAT none - flat ground keeps its cover's colour
     * - and foothill the lightest, 0xc8c3b8 (buildable ground), steep 0x9b968c, very steep the darkest,
     * 0x6c6862 (the study's mockup, earth2/mockups.py). Drawn on grass, the beach and the desert; ice is white
     * whatever the slope (it has no relief), and water none.
     */
    public static final int[] RELIEF_GREY = { 0, 0xffc8c3b8, 0xff9b968c, 0xff6c6862 };

    /**
     * How far forest on a slope is shaded toward its slope's grey (D5, the study's way B; the mockup's
     * mixes, estimated by eye on Medellin): FLAT none, foothill and steep half the way, very steep
     * 0.60 - forest stays forest green, so the forest the law and Forestry count reads, and the slope
     * under it still does: sage on a foothill, greyer on steep ground, darkest on very steep.
     */
    public static final double[] FOREST_MIX = { 0, 0.50, 0.50, 0.60 };

    /** Every cover's colour at every relief, by relief x CLASSES + cover: the table colour() reads - GROUND on the FLAT row, built once. */
    private static final int[] COVER_RELIEF = new int[World.RELIEFS * World.CLASSES];
    static {
        for (int r = 0; r < World.RELIEFS; r++) {
            for (int c = 0; c < World.CLASSES; c++) {
                int k = GROUND[c];
                if (r != World.FLAT && World.dry(c)) k = c == World.FOREST ? blend(k, RELIEF_GREY[r], FOREST_MIX[r]) : RELIEF_GREY[r];
                COVER_RELIEF[r * World.CLASSES + c] = k;
            }
        }
    }

    /**
     * A plot's ground colour from its cover (a World class) and its relief (World.FLAT to VERY_STEEP): GROUND[cover]
     * on flat ground, on water, on ice and for a generated world (which has no relief); on dry open ground (grass, the
     * beach, the desert) its slope's RELIEF_GREY; on forest its green shaded toward that grey by FOREST_MIX. A cover or
     * relief outside the classes is GROUND's (the cover's, or 0 for a cover that is not one).
     */
    public static int colour(int cover, int relief) {
        if (cover < 0 || cover >= World.CLASSES) return 0;
        return relief <= 0 || relief >= World.RELIEFS ? GROUND[cover] : COVER_RELIEF[relief * World.CLASSES + cover];
    }

    /**
     * Every cover-and-relief code's colour, a copy of the table colour() reads: code = relief x World.CLASSES + cover
     * (World.RELIEFS x World.CLASSES = 32 entries, the FLAT row GROUND). What the globe draws a texel by: its picture
     * holds a code a texel (GlobePicture.code()).
     */
    public static int[] codeColours() { return COVER_RELIEF.clone(); }

    /**
     * Each road kind's colour (gravel, paved, highway): the mockup's MAP, but
     * gravel's. GRAVEL IS GREY (FR7 2.3, 2026-10-18; Jerus: "i think gravel isnt
     * brown, it should be grey"): the mockup's tan #cdb07c read as earth, and
     * 70% of US crushed stone is limestone or dolomite and 14% granite (USGS
     * 2025), so a gravel road is a light warm grey of loose stone, #bdb8ae, told
     * from paved's smooth #a4aab0 by its lightness and its speckle (GRAVEL_GRAIN).
     * The browns stay for what is earth: the track's dashes and its ballast.
     */
    public static final int[] ROAD = { 0, 0xffbdb8ae, 0xffa4aab0, 0xff3b4048 };

    /** The loose stone's speckle on a gravel street from 4 px a plot (the near view, L0): a shade darker than its grey. */
    public static final int GRAVEL_GRAIN = 0xff9a958b;

    /** The share of a gravel street's pixels speckled: 18%. */
    public static final double GRAVEL_GRAIN_SHARE = .18;

    /** The railway's line, and its casing from 4 px a plot (0.7.72) until 0.8.1 drew the track as rail; since, a track plot's colour in raster() below LINES_FROM px a plot alone (plotColour()): openstreetmap-carto's rail grey, #707070 (the mockup drew no railway; star N3-9)... */
    public static final int RAIL_LINE = 0xff707070;

    /** ...and its dashes, white on every other plot along it until 0.8.1: carto's rail dash. Nothing draws it since. */
    public static final int RAIL_DASH = 0xffffffff;

    /* ===== RAIL DRAWN AS RAIL (0.8.1, batch FR0; spec-freight 4.3, D15, D18) ===== */

    /** The track's ballast bed (spec-freight 4.3's grey-brown #6f675c). */
    public static final int BALLAST = 0xff6f675c;
    /** A sleeper across the bed (spec-freight 4.3's #4a3f35). */
    public static final int SLEEPER = 0xff4a3f35;
    /** The rails' steel (spec-freight 4.3's #c9ced3). */
    public static final int STEEL = 0xffc9ced3;
    /** The line the track is at the middle and far views (spec-freight 4.3's dark #3a3632). */
    public static final int RAIL_DARK = 0xff3a3632;

    /** Pixels a plot from which the track is two rails on sleepers (the 8-px raster). */
    public static final int RAILS_FROM = 6;
    /** A single track's bed, a share of the plot: the paved road's width, as the track has today. */
    public static final double BED = 0.52;
    /** Double track's bed, a share of the plot: the highway's. */
    public static final double BED_DOUBLE = 0.86;
    /** Between a track's two rails, a share of the plot (at 8 px: the rails a pixel either side of the track's middle). */
    public static final double GAUGE = 0.24;
    /** Between double track's two tracks, middle to middle. */
    public static final double TRACKS_APART = 0.44;
    /** A sleeper every quarter plot (spec-freight 4.3: "sleepers across it every 0.25 plot"). */
    public static final double SLEEPERS_EVERY = 0.25;
    /** A level crossing's white crossbuck (spec-freight 4.3 "Crossings"). */
    public static final int CROSSBUCK = 0xffffffff;
    /** ...and its barrier's red (spec-freight 4.3 "Crossings"). */
    public static final int BARRIER = 0xffd04a3c;

    /* ===== JUNCTIONS DRAWN BY RULE (0.8.1, batch FR0; spec-freight 4.6, D19) ===== */

    /** Pixels a plot from which junctions are drawn (the 8-px raster): signals, roundabouts, diamonds and stacks, and since FR7 kerb corners and interchanges' loops; a highway's end comes down to the ground wherever the ways are drawn, from LINES_FROM. */
    public static final int JUNCTIONS_FROM = 6;
    /** A signalled junction's box, a shade darker than paved. */
    public static final int JUNCTION_BOX = 0xff8e949a;
    /** A crossing's stripes and stop lines. */
    public static final int ZEBRA = 0xfff2f2f2;
    /** Plots: a zebra crossing's depth along its arm. */
    public static final double ZEBRA_DEPTH = 0.3;
    /** Plots: a roundabout's outer radius, spec: "a circle of 1.5 plots" - about a crossing one plot wide; since FX5 a wider crossing's ring is that and half of the rectangle's longer side less a plot more. */
    public static final double ROUNDABOUT_RADIUS = 0.75;
    /** Plots: its planted middle's radius, widened as the ring is. */
    public static final double ROUNDABOUT_ISLAND = 0.35;
    /** The planted middle. */
    public static final int ISLAND = 0xff6f9a4f;
    /** Plots: a diamond's slip road along the verge each side of its street. */
    public static final double SLIP_LENGTH = 2;
    /** Plots: the least run of junction streets through a plot along an axis for a road to cross there (FX3): a road up to three plots wide - a cell's boulevard, two deep, beside the next cell's road, as a tile sees it past its edge - is one road, not a junction on every plot; the runs are read over the tile and its ring of REACH plots (Crossings). */
    static final int JUNCTION_RUN = 4;
    /** Roads: the fewest that leave a crossing (counted by Crossings.roadsLeaving(): each run of streets along a side of its rectangle, each slanted street off a corner) for it to be a roundabout, not a signal (FX5; Jerus, 2026-10-20, "Mostly signals"). */
    static final int ROUNDABOUT_ROADS = 5;

    /** Pixels a plot from which the blocks' view draws the track's cross-ticks (the L1 raster). */
    public static final int TICKS_FROM = 2;

    /** A bridge's deck, edging a road over fresh water from 4 px a plot: the mockup's. */
    public static final int DECK = 0xff5d4c3c;

    /* ===== TUNNELS AND VIADUCTS (ET4, 2026-10-25; spec-earth2-terrain 4.3, D10) ===== */

    /** A tunnel's portal (ET4): the near black band across a highway or a railway where it goes into the hill or comes out, on the last plot of the tunnel at its edge, a little wider than the run (its cross-section and a pixel either side, at most the plot) and TUNNEL_PORTAL_DEPTH of a plot deep. */
    public static final int TUNNEL_PORTAL = 0xff090b0d;

    /** The share of a plot a tunnel's dashed line fills along the run, from the plot's start: 0.6 (the highway's centre line is .55), in the run's own colour - the highway's surface, the track's steel - a pixel wide at the near view. */
    public static final double TUNNEL_DASH = 0.6;

    /** A portal's depth along the run, a share of a plot: 0.25, a pixel at least - 1 px at 4 px a plot, 2 at 8. */
    public static final double TUNNEL_PORTAL_DEPTH = 0.25;

    /** Whether tunnel plot i is a dash where the view draws no lines (the blocks' views, below LINES_FROM): every other plot along the run, the plots whose x + y is even, the rest its bare ground. */
    static boolean tunnelDash(int i) {
        int tile = TilePainter.TILE;
        return (((i % tile) + (i / tile)) & 1) == 0;
    }

    /** Ground no one in the city owns is drawn this much of the way to the map's background: 40%. */
    public static final double UNOWNED_DIM = 0.4;

    /** The map's background, which unowned ground is dimmed toward: the mockup's --map-void. */
    public static final int VOID = 0xff16222c;

    /** A site's tint over its ground, its resource's colour this much of the way: 36% (the mockup's most for a deposit's body). */
    public static final double SITE_TINT = 0.36;

    /** ...and on the plots its own hash speckles, 75% (the mockup's ore showing through). */
    public static final double SITE_SPECKLE = 0.75;

    /** The share of a site's plots speckled: 15%. */
    public static final double SPECKLE_SHARE = 0.15;

    /** A worked-out site's grey: the mockup's mined grey. */
    public static final int WORKED_GREY = 0xff929496;

    /** A mine's ring and centre mark: the mockup's ring. */
    public static final int RING = 0xff2b2d31;

    /** Pixels a plot from which a building of more than one plot is inset a pixel: 3 (a one-plot building takes its own land's side: SMALL_MOST). */
    public static final int INSET_FROM = 3;

    /** ...and from which it is edged: 6 (the mockup's outlines). */
    public static final int EDGE_FROM = 6;

    /** A road's width as a share of its plot, by kind (gravel, paved, highway): the mockup's 0.3, 0.52 and 0.86 - since 0.7.88 the highway's and (as a paved road's) the track's; a street's surface is its width's (SURFACE). */
    public static final double[] ROAD_WIDTH = { 0, 0.30, 0.52, 0.86 };

    /** ...and its least, in pixels, at the near view: 1, 2 and 3 (the mockup's 1, 1.5 and 2.5, whole). */
    public static final int[] ROAD_LEAST_PX = { 0, 1, 2, 3 };

    /** A highway's casing, a pixel either side from 6 px (the mockup's highwayCase)... */
    public static final int HIGHWAY_CASE = 0xff1d2126;

    /** ...and its dashed centre line from 6 px a plot, a pixel wide, on the first 55% of each plot along it (the mockup's centre, its dash 0.7 on and 0.55 off). */
    public static final int CENTRE_LINE = 0xfff0cf5a;

    /** Pixels a plot from which a road is drawn as a line of its own width on its ground - a street as verge and surface (0.7.88) - and below, the whole plot in its colour: 4, the near view's image. */
    public static final int LINES_FROM = 4;

    /* ===== YARDS DRAWN AS YARDS (0.8.1, batch FR0; spec-freight 4.3, D15) =====
       Since FX5 (Y2) a yard is drawn whole, however many tiles its box spans:
       CityRuns lays a yard YARD_LONG (48) plots along its line, a unit train
       and its two ladders, and a tile is 32 plots, so most yards run on into
       the next tile or two. A tile is given every yard its box reaches, each
       whole - its box in the tile's plots and the layout of its fan, the very
       one the trains' routes lay (Input.yards, CityMap.fillInput()) - and
       drawWholeYard() paints the part of the one picture that falls on it, so
       the fan has its two ladders at the box's own ends and never at a seam,
       and one shed and one set of heaps stand where they stand, not one a tile.
       A yard with no entry (a hand-built one) is drawn as before, from the box
       it has on the tile (drawYard()). ===== */

    /** A yard's ground, light ballast (spec-freight 4.3's yard). */
    public static final int YARD_GROUND = 0xff8c8478;
    /** The fan's sidings (spec-freight 4.3: 6 to 10). */
    public static final int YARD_TRACKS = 8;
    /** A Rail Depot's sidings (FR7 slice 3, "2 to 3 sidings and a shed"): 3 - its box is 8 plots by 7, a Rail Terminal's 24 by 24. */
    public static final int DEPOT_TRACKS = 3;

    /** The sidings of a yard of type t: a Rail Depot's DEPOT_TRACKS, else YARD_TRACKS (FR7 slice 3). */
    public static int tracksOf(BuildingVisual.Type t) {
        return BuildingVisual.depot(t) ? DEPOT_TRACKS : YARD_TRACKS;
    }

    /**
     * The sidings of a yard of `tracks` that wagons stand on, from the shed's side, when the railway's picture gives `wagonTracks` of a
     * Terminal's YARD_TRACKS (CityMap.wagonTracksOf()): scaled to the yard's own sidings, and never all of them - a yard keeps one siding
     * clear of standing wagons (FX5, slice T; FX3's star 6), which the routes lay no train past (CityMap.trainRoutes() reads this, as the
     * tiles do) and the trains go to.
     */
    public static int wagonTracksOn(int wagonTracks, int tracks) {
        int scaled = tracks == YARD_TRACKS ? wagonTracks : (int) Math.round(wagonTracks * (double) tracks / YARD_TRACKS);
        return Math.max(0, Math.min(tracks - 1, scaled));
    }
    /** Plots between the fan's tracks, middle to middle. */
    public static final double YARD_TRACK_APART = 1.5;
    /** Plots at each end over which the fan closes to the line. */
    public static final int YARD_LADDER = 3;
    /** The freight shed: plots deep, and its share of the fan's length. */
    public static final double SHED_DEPTH = 2.5, SHED_LENGTH = 0.6;
    /** A bulk yard's stockpiles, the spec's sand colour. */
    public static final int ORE_HEAP = 0xffc9b68f;
    /** A container yard's boxes, the boats' white. */
    public static final int CONTAINER = 0xffd6dde4;
    /** Its two cranes. */
    public static final int GANTRY = 0xff2b2f36;
    /** A wagon standing. */
    public static final int WAGON = 0xff7b4b3a;
    /** Plots: a wagon about 15 m. */
    public static final double WAGON_LENGTH = 0.5;

    /** A one-plot building's side as a share of its plot: its own land's (BuildingVisual.plotSide(), 0.7.64 - a House 0.91 of the plot, a Convenience Store 0.72; the mockup's 0.6 to 0.92 by a hash before), never more than 0.92, so a street's buildings stay apart (the mockup's)... */
    public static final double SMALL_MOST = 0.92;

    /** ...and set this far from the road it faces: 0.07 of a plot, so it hugs its street (the mockup's). */
    public static final double SET_BACK = 0.07;


    /** A street's verge, its right of way either side of its surface (0.7.88): the prototype's VERGE (spec-roads-and-ports.md 5, roads_proto.py). */
    public static final int VERGE = 0xffbfd39a;

    /** A track's dashes, a street the city has bought no road for (0.7.88): the prototype's brown (spec 2.5, 5: "a dashed brown line on the verge"). */
    public static final int TRACK_DASH = 0xff8a6a3c;

    /** A street's surface across its plot, by its width (Painted.width: 0, half, full): none, half the plot (15 m), the whole plot (30 m) - the right of way is 30 m (spec 2.1; the prototype's W_MIN and W_FULL). */
    public static final double[] SURFACE = { 0, DistrictPlan.HALF, DistrictPlan.FULL };

    /** A building packed at the city's edge without a street (R7) is drawn this much of the way to the map's background: 35% (star RD2-2) - a shade darker, so it reads as standing apart from the streets, at its own size and place. */
    public static final double PACKED_DIM = 0.35;

    /* =====================================================================
       THE NEAR VIEW (J3: a tile at 4 or 8 px a plot, MapFrame.L0)
       ===================================================================== */

    /**
     * Fills img with the tile at px pixels a plot: (32 px) squared pixels,
     * row by row. Below LINES_FROM a road plot is its colour; from it, the
     * streets are drawn as verge and surface, the track and the highways as
     * lines, joined across the tile's edge where the plans have them go on
     * (ways()). A one-plot building is drawn the mockup's size, hugging the
     * street it faces; a larger one inset a pixel from 3 px a plot; all
     * edged from 6; one packed without a street a shade darker; and from
     * LINES_FROM a Rail Terminal as its yard (drawYard(), 0.8.1; whole across
     * the tiles it spans since FX5, drawWholeYard()).
     */
    public static void raster(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        boolean lines = px >= LINES_FROM;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            int c = lines ? groundColour(in, p, i) : plotColour(in, p, i);
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            for (int y = 0; y < px; y++) {
                int o = (y0 + y) * w + x0;
                for (int x = 0; x < px; x++) img[o + x] = c;
            }
        }
        if (lines) ways(in, p, px, img);
        for (int b = 0; b < p.buildings; b++) {
            BuildingVisual.Type t = p.btype[b] >= 0 && p.btype[b] < in.types.length ? in.types[p.btype[b]] : null;
            // Its type's fill and edge: its class's, the flats darker, and (0.7.97) the refinery's units, a terminal and a tank farm their own.
            int fill = t == null ? BuildingVisual.FILL[BuildingVisual.INDUSTRY] : t.fill();
            int edge = t == null ? BuildingVisual.EDGE[BuildingVisual.INDUSTRY] : t.edge();
            if (p.bpacked[b]) {
                fill = blend(fill, VOID, PACKED_DIM);
                edge = blend(edge, VOID, PACKED_DIM);
            }
            int x0, y0, x1, y1;
            int site = p.bsite[b];
            // A yard the tile is given whole (FX5 Y2): its entry in Input.yards, else -1 (a hand-built yard, drawn from its box here).
            int whole = t != null && t.terminal() && !p.bpacked[b] ? yardOf(in, p, b) : -1;
            if (whole < 0 && p.bw[b] == 1 && p.bh[b] == 1 && site < 0) {
                // One plot: its own land's side (BuildingVisual.plotSide(), 0.7.64), hugging the street it faces.
                int i = p.by[b] * tile + p.bx[b];
                int sw = Math.max(1, Math.min(px, (int) Math.round(px * Math.min(SMALL_MOST, BuildingVisual.plotSide(t)))));
                int sh = sw;
                int ox = (px - sw) / 2, oy = (px - sh) / 2;
                int set = (int) Math.round(SET_BACK * px);
                int face = faceOf(p, i);
                if (face == 0) oy = set; else if (face == 2) oy = px - set - sh;
                else if (face == 3) ox = set; else if (face == 1) ox = px - set - sw;
                ox = Math.max(0, Math.min(px - sw, ox));
                oy = Math.max(0, Math.min(px - sh, oy));
                x0 = p.bx[b] * px + ox;
                y0 = p.by[b] * px + oy;
                x1 = x0 + sw;
                y1 = y0 + sh;
            } else {
                int inset = px >= INSET_FROM ? 1 : 0;
                // ...a site's works inset only where its site ends (0.8.0): a site across tiles, a coal mine's 33 plots, one box.
                int il = site >= 0 && p.bx[b] > in.siteFX0[site] ? 0 : inset, it = site >= 0 && p.by[b] > in.siteFY0[site] ? 0 : inset;
                int ir = site >= 0 && p.bx[b] + p.bw[b] - 1 < in.siteFX1[site] ? 0 : inset, ib = site >= 0 && p.by[b] + p.bh[b] - 1 < in.siteFY1[site] ? 0 : inset;
                x0 = p.bx[b] * px + il;
                y0 = p.by[b] * px + it;
                x1 = Math.min(tile, p.bx[b] + p.bw[b]) * px - ir;
                y1 = Math.min(tile, p.by[b] + p.bh[b]) * px - ib;
                if (whole >= 0) {
                    // ...a yard across a seam is inset only where its box ends, not where the tile cuts it (FX5 Y2).
                    int o = whole * TilePainter.Input.YARD_INTS, ex0 = in.yard[o], ey0 = in.yard[o + 1];
                    x0 = Math.max(0, ex0) * px + (ex0 < 0 ? 0 : inset);
                    y0 = Math.max(0, ey0) * px + (ey0 < 0 ? 0 : inset);
                    x1 = Math.min(tile, ex0 + in.yard[o + 2]) * px - (ex0 + in.yard[o + 2] > tile ? 0 : inset);
                    y1 = Math.min(tile, ey0 + in.yard[o + 3]) * px - (ey0 + in.yard[o + 3] > tile ? 0 : inset);
                }
            }
            if (px >= LINES_FROM && t != null && t.terminal() && !p.bpacked[b]) {
                if (whole >= 0) drawWholeYard(in, p, whole, t, px, img, w, x0, y0, x1, y1);
                else drawYard(in, p, b, t, px, img, w, x0, y0, x1, y1);
                // The bends of the track in the yard's head, over its ground (FX5).
                bends(in, p, px, img, x0, y0, x1, y1, false);
                continue;
            }
            boolean edged = px >= EDGE_FROM;
            int look = t == null ? BuildingVisual.PLAIN : t.look();
            if (look != BuildingVisual.PLAIN && px >= LOOK_FROM && !p.bpacked[b] && (p.bw[b] > 1 || p.bh[b] > 1 || site >= 0)) {
                // A materials building as itself (0.8.0, batch I14): its pit, quarry, heaps, works, camp or forest.
                drawLook(in, t, site, px, edged, img, w, x0, y0, x1, y1);
            } else {
                for (int y = y0; y < y1; y++) {
                    for (int x = x0; x < x1; x++) {
                        boolean onEdge = edged && (y == y0 || y == y1 - 1 || x == x0 || x == x1 - 1);
                        img[y * w + x] = onEdge ? edge : fill;
                    }
                }
            }
            if (site >= 0 && px >= 4) {
                // A mine's mark: its resource's colour at its centre, ringed - since 0.8.0 the whole site's centre, so a site
                // across tiles is marked once.
                int cx = (in.siteFX0[site] + in.siteFX1[site] + 1) * px / 2, cy = (in.siteFY0[site] + in.siteFY1[site] + 1) * px / 2;
                int r = Math.max(1, px / 2);
                int colour = 0xff000000 | Resource.values()[in.siteKind[site]].colour();
                for (int y = cy - r; y <= cy + r; y++) {
                    for (int x = cx - r; x <= cx + r; x++) {
                        if (x < x0 || y < y0 || x >= x1 || y >= y1) continue;
                        boolean ring = y == cy - r || y == cy + r || x == cx - r || x == cx + r;
                        img[y * w + x] = ring ? RING : colour;
                    }
                }
            }
        }
        overBuildings(in, p, px, img);
        atSea(in, p, px, img);
    }

    /**
     * Where a yard's fan lies in its box, from the box's track (FX3, 0.8.4):
     * rail[y * w + x] is whether the box's plot (x, y) holds track, the box w
     * plots across and h down. Returns {ew, line, a, b}: ew is 1 for an
     * east-west fan and 0 for a north-south one, line the plot row (east-west)
     * or column (north-south) it closes to, in plots from the box's north-west
     * corner, and [a, b) its span along the line, in plots from the box's
     * west edge (east-west) or north edge. No track in the box: {1, -1, 0, w},
     * east-west on the box's middle. WHY: drawYard() took the line as the
     * middle of all the box's track, which put the fan off its line when
     * another line crossed the box (Jerus's second yard, stacked on a
     * corridor with the spine across its head: the fan closed 4.5 plots east
     * of the corridor). Now each column holding track is a north-south
     * candidate and each row an east-west one; a row (for a column) is blocked
     * when it holds track in any other column, and the candidate's runs are
     * the stretches of rows left clear, each scoring the rows of it where the
     * candidate is track. The best run wins: the most track, then the longest,
     * then north-south, then the lowest line, then the earliest. A run shorter
     * than 2 * YARD_LADDER + 2 plots has no room for a fan between crossings:
     * its line and direction stand and the span is the whole box. One straight
     * line through the box wins with the whole box, as it always was. Both
     * drawYard() and CityMap.trainRoutesInto() lay the fan from this, so a
     * route's ladder starts where the drawing's does.
     */
    static int[] yardLayout(boolean[] rail, int w, int h) {
        int[] colN = new int[w], rowN = new int[h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (!rail[y * w + x]) continue;
                colN[x]++;
                rowN[y]++;
            }
        }
        int bestScore = -1, bestLen = 0, bestEw = 1, bestLine = -1, bestA = 0, bestB = 0;
        // North-south first, then east-west; each by index and run in order, so a tie keeps the earlier.
        for (int pass = 0; pass < 2; pass++) {
            boolean ns = pass == 0;
            int lines = ns ? w : h, along = ns ? h : w;
            for (int c = 0; c < lines; c++) {
                if ((ns ? colN[c] : rowN[c]) == 0) continue;
                int start = 0;
                while (start <= along) {
                    int score = 0;
                    int pos = start;
                    while (pos < along) {
                        boolean on = ns ? rail[pos * w + c] : rail[c * w + pos];
                        int others = ns ? rowN[pos] : colN[pos];
                        if (others - (on ? 1 : 0) > 0) break;
                        if (on) score++;
                        pos++;
                    }
                    int len = pos - start;
                    if (len > 0 && (score > bestScore || score == bestScore && len > bestLen)) {
                        bestScore = score;
                        bestLen = len;
                        bestEw = ns ? 0 : 1;
                        bestLine = c;
                        bestA = start;
                        bestB = pos;
                    }
                    start = pos + 1;
                }
            }
        }
        if (bestLine < 0) return new int[] { 1, -1, 0, w };
        if (bestLen < 2 * YARD_LADDER + 2) {
            bestA = 0;
            bestB = bestEw == 1 ? w : h;
        }
        return new int[] { bestEw, bestLine, bestA, bestB };
    }

    /**
     * A Rail Terminal's yard as a yard, not its class's plain box (0.8.1,
     * batch FR0; spec-freight 4.2 and 4.3, D15), and since FR7 (slice 3) a
     * Rail Depot's: ballast ground, a fan of tracksOf() sidings (a Terminal's
     * YARD_TRACKS, a Depot's DEPOT_TRACKS) closing to the line at each end over YARD_LADDER
     * plots, a freight shed on one side of the fan, and on the other the
     * freight it handles - a container yard's boxes and gantry cranes where
     * the freight station carries goods, every other yard's ore stockpiles -
     * with wagons standing on the first wagonTracks tracks from the shed
     * (never on all of them: wagonTracksOn() keeps one siding clear, FX5 T).
     * Since FX3 (0.8.4) the fan lies where yardLayout() puts it: on the line
     * with the longest stretch clear of crossing track, the shed, the heaps
     * and the boxes kept to that stretch, and the track that crosses the
     * line drawn over the yard's ground through the head the stretch leaves,
     * so a line crossing the yard meets the fan at its throat. Since FX5
     * (0.8.18) each ladder from the fan's apex to its siding's start is
     * TrackCurves.ladder()'s S, tangent to the line and to the siding, drawn
     * as the siding's rails (ladderRails()) - the points the routes run on.
     * Since FX5 Y2 a yard longer than a tile (CityRuns.YARD_LONG, a unit
     * train's length, spans two or three) is drawn whole across the tiles it
     * reaches (drawWholeYard()): the tile is given every such yard, its box and
     * its layout (Input.yards), and paints the part that falls on it of one
     * picture, so the fan has one pair of ladders at the box's own ends, one
     * shed and one set of heaps, never a set a tile. This is the drawing of a
     * yard with no entry in Input.yards - a hand-built one, or one that lies
     * inside its tile - from the box on the tile and the track on it alone.
     */
    private static void drawYard(TilePainter.Input in, TilePainter.Painted p, int b, BuildingVisual.Type t, int px,
                                 int[] img, int w, int x0, int y0, int x1, int y1) {
        int tile = TilePainter.TILE;
        int bx = p.bx[b], by = p.by[b], bw = p.bw[b], bh = p.bh[b];
        int boxW = Math.min(tile, bx + bw) - bx, boxH = Math.min(tile, by + bh) - by;
        boolean[] rail = new boolean[boxW * boxH];
        for (int y = 0; y < boxH; y++) {
            for (int x = 0; x < boxW; x++) rail[y * boxW + x] = p.run[(by + y) * tile + bx + x] == TilePainter.RAIL;
        }
        int[] layout = yardLayout(rail, boxW, boxH);
        boolean station = in.tx * tile + bx == in.stationX && in.ty * tile + by == in.stationY;
        yardPicture(in, t, px, img, w, x0, y0, x1, y1, bx, by, bw, bh, boxW, boxH, layout, station);
        headTrack(in, p, px, img, bx, by, boxW, boxH, layout);
    }

    /**
     * A yard the tile is given whole (FX5 Y2), drawn clipped to the tile: its
     * picture - ground, fan, ladders, shed, heaps or boxes and cranes, wagons -
     * is laid from the entry's whole box and layout (Input.yards, the very
     * layout CityMap.trainRoutesInto() lays the routes on) on a canvas of the
     * whole box, and the rectangle [x0, x1) x [y0, y1) of the tile's pixels,
     * the box's part on the tile, is copied from it; so a pixel is the same
     * from whichever tile it is painted and the yard has no seam. The track
     * that crosses its head is the tile's own (headTrack()).
     */
    private static void drawWholeYard(TilePainter.Input in, TilePainter.Painted p, int e, BuildingVisual.Type t, int px,
                                      int[] img, int w, int x0, int y0, int x1, int y1) {
        int o = e * TilePainter.Input.YARD_INTS;
        int ex = in.yard[o], ey = in.yard[o + 1], ew = in.yard[o + 2], eh = in.yard[o + 3];
        int[] layout = { in.yard[o + 5], in.yard[o + 6], in.yard[o + 7], in.yard[o + 8] };
        int cw = ew * px, ch = eh * px, inset = px >= INSET_FROM ? 1 : 0;
        int[] canvas = yardCanvas(cw * ch);
        yardPicture(in, t, px, canvas, cw, inset, inset, cw - inset, ch - inset, 0, 0, ew, eh, ew, eh, layout, in.yard[o + 9] != 0);
        for (int y = y0; y < y1; y++) System.arraycopy(canvas, (y - ey * px) * cw + (x0 - ex * px), img, y * w + x0, x1 - x0);
        headTrack(in, p, px, img, ex, ey, ew, eh, layout);
    }

    /** The canvas a whole yard is drawn on: this thread's, at least `size` pixels (the raster runs on the map's workers and the screen's thread alike). */
    private static int[] yardCanvas(int size) {
        int[] got = YARD_CANVAS.get();
        if (got.length < size) {
            got = new int[size];
            YARD_CANVAS.set(got);
        }
        return got;
    }

    private static final ThreadLocal<int[]> YARD_CANVAS = ThreadLocal.withInitial(() -> new int[0]);

    /**
     * The entry of Input.yards that is the yard of building b (FX5 Y2): the
     * building's part on the tile is the entry's whole box clipped to it,
     * of the entry's type; -1 for none (a yard with no entry is a hand-built
     * one, drawn by drawYard()).
     */
    static int yardOf(TilePainter.Input in, TilePainter.Painted p, int b) {
        int tile = TilePainter.TILE;
        for (int e = 0; e < in.yards; e++) {
            int o = e * TilePainter.Input.YARD_INTS;
            int x0 = Math.max(0, in.yard[o]), y0 = Math.max(0, in.yard[o + 1]);
            int x1 = Math.min(tile, in.yard[o] + in.yard[o + 2]), y1 = Math.min(tile, in.yard[o + 1] + in.yard[o + 3]);
            if (p.bx[b] == x0 && p.by[b] == y0 && p.bw[b] == x1 - x0 && p.bh[b] == y1 - y0 && p.btype[b] == in.yard[o + 4]) return e;
        }
        return -1;
    }

    /**
     * A yard's picture into img (w pixels across) inside [x0, x1) x [y0, y1):
     * the box at plots (bx, by), bw across and bh down, in img's plots, its
     * layout (yardLayout()) laid for a box of boxW x boxH plots - the part of
     * the box the layout was made from. The ground, the fan's sidings and
     * their two ladders at the ends of the span, the shed, the heaps or the
     * boxes and cranes, and the wagons standing.
     */
    private static void yardPicture(TilePainter.Input in, BuildingVisual.Type t, int px, int[] img, int w,
                                    int x0, int y0, int x1, int y1, int bx, int by, int bw, int bh, int boxW, int boxH,
                                    int[] layout, boolean station) {
        fillRect(img, w, x0, y0, x1, y1, YARD_GROUND);
        boolean ew = layout[0] == 1;
        int spanA = layout[2], spanB = layout[3], boxLong = ew ? boxW : boxH;
        int bed = bedWidth(px, false);
        int off = px >= RAILS_FROM ? Math.max(1, (int) Math.round(GAUGE / 2.0 * px)) : 0;
        // The line the fan closes to: the yard's own line, else the box's middle.
        double line = layout[1] < 0
                ? (ew ? (by + bh / 2.0) * px : (bx + bw / 2.0) * px)
                : ((ew ? by : bx) + layout[1] + 0.5) * px;
        // The fan's span along, in pixels: the stretch of the line clear of crossing track, else the box's whole length.
        int start = spanA == 0 ? (ew ? x0 : y0) : ((ew ? bx : by) + spanA) * px;
        int end = spanB >= boxLong ? (ew ? x1 : y1) : ((ew ? bx : by) + spanB) * px;
        // The box as the shed, the heaps and the boxes see it: the span along, the box across.
        int sx0 = ew ? start : x0, sx1 = ew ? end : x1, sy0 = ew ? y0 : start, sy1 = ew ? y1 : end;
        int n = tracksOf(t);
        double[] at = new double[n];
        double apart = YARD_TRACK_APART * px;
        for (int i = 0; i < n; i++) at[i] = line + (i - (n - 1) / 2.0) * apart;
        double min = at[0], max = at[n - 1];
        // Keep the fan's whole bed inside the box.
        if (ew) {
            double lo = min - bed / 2.0, hi = max + bed / 2.0;
            if (lo < y0) { double shift = y0 - lo; for (int i = 0; i < n; i++) at[i] += shift; }
            else if (hi > y1) { double shift = y1 - hi; for (int i = 0; i < n; i++) at[i] += shift; }
        } else {
            double lo = min - bed / 2.0, hi = max + bed / 2.0;
            if (lo < x0) { double shift = x0 - lo; for (int i = 0; i < n; i++) at[i] += shift; }
            else if (hi > x1) { double shift = x1 - hi; for (int i = 0; i < n; i++) at[i] += shift; }
        }
        min = at[0]; max = at[n - 1];
        int fanA = start + (int) Math.round(YARD_LADDER * px);
        int fanB = end - (int) Math.round(YARD_LADDER * px);
        int linePx = (int) Math.round(line);
        int steel = STEEL;
        for (int i = 0; i < n; i++) {
            int mid = (int) Math.round(at[i]);
            int a = mid - bed / 2, z = a + bed;
            if (ew) {
                fillRect(img, w, fanA, a, fanB, z, BALLAST);
                if (off == 0) fillRect(img, w, fanA, mid, fanB, mid + 1, steel);
                else {
                    fillRect(img, w, fanA, mid - off, fanB, mid - off + 1, steel);
                    fillRect(img, w, fanA, mid + off, fanB, mid + off + 1, steel);
                }
                ladderRails(img, w, px, true, start + 0.5, linePx + 0.5, fanA + 0.5, mid + 0.5, steel);
                ladderRails(img, w, px, true, end - 0.5, linePx + 0.5, fanB - 0.5, mid + 0.5, steel);
            } else {
                fillRect(img, w, a, fanA, z, fanB, BALLAST);
                if (off == 0) fillRect(img, w, mid, fanA, mid + 1, fanB, steel);
                else {
                    fillRect(img, w, mid - off, fanA, mid - off + 1, fanB, steel);
                    fillRect(img, w, mid + off, fanA, mid + off + 1, fanB, steel);
                }
                ladderRails(img, w, px, false, start + 0.5, linePx + 0.5, fanA + 0.5, mid + 0.5, steel);
                ladderRails(img, w, px, false, end - 0.5, linePx + 0.5, fanB - 0.5, mid + 0.5, steel);
            }
        }
        // The freight shed, between the fan and the box's edge.
        int fanNear = (int) Math.round((ew ? min : min) - bed / 2.0);
        int fanFar = (int) Math.round((ew ? max : max) + bed / 2.0);
        int depth = ew
                ? Math.min((int) Math.round(SHED_DEPTH * px), Math.max(0, fanNear - y0))
                : Math.min((int) Math.round(SHED_DEPTH * px), Math.max(0, fanNear - x0));
        int len = Math.max(1, (int) Math.round(SHED_LENGTH * (fanB - fanA)));
        if (depth > 0) {
            int along = (fanA + fanB) / 2;
            int s0 = along - len / 2, s1 = s0 + len;
            if (ew) {
                s0 = Math.max(sx0, s0); s1 = Math.min(sx1, s1);
                int shedTop = fanNear - depth;
                fillRect(img, w, s0, shedTop, s1, fanNear, t.fill());
                if (px >= EDGE_FROM) {
                    for (int x = s0; x < s1; x++) { img[shedTop * w + x] = t.edge(); img[(fanNear - 1) * w + x] = t.edge(); }
                    for (int y = shedTop; y < fanNear; y++) { img[y * w + s0] = t.edge(); img[y * w + s1 - 1] = t.edge(); }
                }
            } else {
                s0 = Math.max(sy0, s0); s1 = Math.min(sy1, s1);
                int shedLeft = fanNear - depth;
                fillRect(img, w, shedLeft, s0, fanNear, s1, t.fill());
                if (px >= EDGE_FROM) {
                    for (int y = s0; y < s1; y++) { img[y * w + shedLeft] = t.edge(); img[y * w + fanNear - 1] = t.edge(); }
                    for (int x = shedLeft; x < fanNear; x++) { img[s0 * w + x] = t.edge(); img[(s1 - 1) * w + x] = t.edge(); }
                }
            }
        }
        boolean container = station && in.goodsByRail;
        // What it handles, on the other side of the fan from the shed.
        if (container) containers(px, img, w, sx0, sy0, sx1, sy1, ew, fanA, fanB, fanNear, fanFar);
        else stockpiles(px, img, w, sx0, sy0, sx1, sy1, ew, fanA, fanB, fanFar);
        // Wagons standing on the first wagonTracks tracks from the shed's side, never on all of them (a siding stays clear for a train).
        int wagonTracks = wagonTracksOn(in.wagonTracks, n);
        for (int k = 0; k < wagonTracks && k < n; k++) {
            int mid = (int) Math.round(at[k]);
            int wide = px >= RAILS_FROM ? bed : 1;
            int wagonLen = Math.max(1, (int) Math.round(WAGON_LENGTH * px));
            int top = mid - wide / 2;
            for (int s = fanA; s + wagonLen <= fanB; s += wagonLen + 1) {
                if (ew) {
                    fillRect(img, w, s, top, s + wagonLen, top + wide, WAGON);
                    if (container) {
                        int cw = Math.max(1, wagonLen * 2 / 3), ch = Math.max(1, wide - 2);
                        fillRect(img, w, s + (wagonLen - cw) / 2, mid - ch / 2, s + (wagonLen - cw) / 2 + cw, mid - ch / 2 + ch, CONTAINER);
                    }
                } else {
                    fillRect(img, w, top, s, top + wide, s + wagonLen, WAGON);
                    if (container) {
                        int cw = Math.max(1, wide - 2), ch = Math.max(1, wagonLen * 2 / 3);
                        fillRect(img, w, mid - cw / 2, s + (wagonLen - ch) / 2, mid - cw / 2 + cw, s + (wagonLen - ch) / 2 + ch, CONTAINER);
                    }
                }
            }
        }
    }

    /**
     * The track the fan's span leaves out, over the yard's ground (FX3): a
     * line crossing the box runs through the head and meets the fan at its
     * throat. The tile's plots of the yard's box, which starts at plot
     * (boxX, boxY) of the tile (it may lie off it) and is boxW x boxH plots,
     * that hold track (p.run) and lie outside the layout's span along the
     * fan, a plot a bend replaces left to its arc.
     */
    private static void headTrack(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int boxX, int boxY, int boxW, int boxH, int[] layout) {
        int tile = TilePainter.TILE;
        boolean ew = layout[0] == 1;
        int spanA = layout[2], spanB = layout[3], boxLong = ew ? boxW : boxH;
        if (spanA <= 0 && spanB >= boxLong) return;
        for (int y = Math.max(0, boxY); y < Math.min(tile, boxY + boxH); y++) {
            for (int x = Math.max(0, boxX); x < Math.min(tile, boxX + boxW); x++) {
                if (p.run[y * tile + x] != TilePainter.RAIL) continue;
                int along = ew ? x - boxX : y - boxY;
                if (along >= spanA && along < spanB) continue;
                if (in.curved[y * tile + x]) continue;
                yardTrack(in, p, y * tile + x, px, img);
            }
        }
    }

    /**
     * One plot of the track that crosses a yard's head, drawn over the yard's
     * ground (FX3, 0.8.4): drawTrackLook()'s look, with the track beside it
     * read from the runs (p.run), because a building stands on every plot of
     * the yard's box and p.use does not say RAIL there.
     */
    private static void yardTrack(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img) {
        int tile = TilePainter.TILE, arms = 0;
        for (int d = 0; d < 4; d++) {
            int x = i % tile + TilePainter.DX[d], y = i / tile + TilePainter.DY[d];
            byte f = x >= 0 && y >= 0 && x < tile && y < tile ? p.run[y * tile + x]
                    : in.fixedBeyond[d * tile + (d == 0 || d == 2 ? i % tile : i / tile)];
            if (f == TilePainter.RAIL || f == TilePainter.RAIL_OVER) arms |= 1 << d;
        }
        drawTrackLook(in, p, i, px, img, arms);
    }

    /** A container yard's boxes, in rows, and its two gantry cranes across the fan. */
    private static void containers(int px, int[] img, int w,
                                   int x0, int y0, int x1, int y1, boolean ew, int fanA, int fanB, int fanNear, int fanFar) {
        int boxLong = px, boxDeep = Math.max(1, (int) Math.round(0.4 * px));
        int left = ew ? x0 : fanFar, right = ew ? x1 : x1;
        int top = ew ? fanFar : y0, bottom = ew ? y1 : y1;
        int alongStep = ew ? boxLong + 1 : boxDeep + 1;
        int acrossStep = ew ? boxDeep + 1 : boxLong + 1;
        for (int a = top; a + (ew ? boxDeep : boxLong) <= bottom; a += acrossStep) {
            for (int s = left; s + (ew ? boxLong : boxDeep) <= right; s += alongStep) {
                if (ew) fillRect(img, w, s, a, s + boxLong, a + boxDeep, CONTAINER);
                else fillRect(img, w, s, a, s + boxDeep, a + boxLong, CONTAINER);
            }
        }
        // The cranes, across the whole fan at a third and two thirds of its length.
        int one = fanA + (fanB - fanA) / 3, two = fanA + 2 * (fanB - fanA) / 3;
        for (int g : new int[] { one, two }) {
            if (ew) {
                fillRect(img, w, g, fanNear, g + 1, fanFar, GANTRY);
                fillRect(img, w, g - 1, fanNear, g + 2, fanNear + 1, GANTRY);
                fillRect(img, w, g - 1, fanFar - 1, g + 2, fanFar, GANTRY);
            } else {
                fillRect(img, w, fanNear, g, fanFar, g + 1, GANTRY);
                fillRect(img, w, fanNear, g - 1, fanNear + 1, g + 2, GANTRY);
                fillRect(img, w, fanFar - 1, g - 1, fanFar, g + 2, GANTRY);
            }
        }
    }

    /**
     * A bulk yard's three stockpiles, side by side along the fan. A heap whose share of the ground past the fan is under
     * HEAP_LEAST pixels either way is not drawn, and none is drawn outside its share (CL4: a depot drawn at its own 8 x 7
     * footprint at 8 px has its fan reaching the box's far side, and FX5's heaps, their radius held to at least a pixel,
     * were drawn past the yard's picture - the near view threw on the default playtest's city).
     */
    private static void stockpiles(int px, int[] img, int w, int x0, int y0, int x1, int y1,
                                   boolean ew, int fanA, int fanB, int fanFar) {
        int edge = blend(ORE_HEAP, 0xff000000, 0.25);
        for (int k = 0; k < 3; k++) {
            int cx, cy, rx, ry;
            if (ew) {
                int a0 = x0 + k * (x1 - x0) / 3, a1 = x0 + (k + 1) * (x1 - x0) / 3;
                int b0 = fanFar, b1 = y1;
                cx = (a0 + a1) / 2; cy = (b0 + b1) / 2;
                if (a1 - a0 < HEAP_LEAST || b1 - b0 < HEAP_LEAST) continue;
                rx = Math.max(1, (a1 - a0) / 2 - 1); ry = Math.max(1, (b1 - b0) / 2 - 1);
                fillEllipse(img, w, cx, cy, rx, ry, ORE_HEAP, edge, a0, b0, a1, b1);
            } else {
                int a0 = fanFar, a1 = x1;
                int b0 = y0 + k * (y1 - y0) / 3, b1 = y0 + (k + 1) * (y1 - y0) / 3;
                cx = (a0 + a1) / 2; cy = (b0 + b1) / 2;
                if (a1 - a0 < HEAP_LEAST || b1 - b0 < HEAP_LEAST) continue;
                rx = Math.max(1, (a1 - a0) / 2 - 1); ry = Math.max(1, (b1 - b0) / 2 - 1);
                fillEllipse(img, w, cx, cy, rx, ry, ORE_HEAP, edge, a0, b0, a1, b1);
            }
        }
    }

    /** The least share of the ground past a bulk yard's fan, in pixels either way, a heap is drawn on: 2. */
    static final int HEAP_LEAST = 2;

    /** A filled ellipse, its edge a pixel darker, kept to [clipX0, clipX1) x [clipY0, clipY1). */
    private static void fillEllipse(int[] img, int w, int cx, int cy, int rx, int ry, int fill, int edge, int clipX0, int clipY0, int clipX1, int clipY1) {
        for (int y = Math.max(cy - ry, clipY0); y <= Math.min(cy + ry, clipY1 - 1); y++) {
            for (int x = Math.max(cx - rx, clipX0); x <= Math.min(cx + rx, clipX1 - 1); x++) {
                double dx = (x - cx) / (double) rx, dy = (y - cy) / (double) ry;
                if (dx * dx + dy * dy > 1) continue;
                boolean rim = outside(x - 1, y, cx, cy, rx, ry) || outside(x + 1, y, cx, cy, rx, ry)
                        || outside(x, y - 1, cx, cy, rx, ry) || outside(x, y + 1, cx, cy, rx, ry);
                img[y * w + x] = rim ? edge : fill;
            }
        }
    }

    /** Whether (x, y) lies outside the ellipse centred at (cx, cy). */
    private static boolean outside(int x, int y, int cx, int cy, int rx, int ry) {
        double dx = (x - cx) / (double) rx, dy = (y - cy) / (double) ry;
        return dx * dx + dy * dy > 1;
    }

    /* =====================================================================
       THE MATERIALS AS THEMSELVES (0.8.0, batch I14; spec-materials 5's I14
       row). Each look of BuildingVisual's drawn in its box from LOOK_FROM px
       a plot: an open pit's benches stepping down to its floor and a
       quarry's straight ones, measured from the whole site's square so a
       site across tiles is one pit (TilePainter.Input.siteFX0); a shaft's
       yard and headframe; a leach plant's heaps in rows and its pond; the
       wash plant's conveyor; a camp's log stacks; planted forest's trees in
       rows; a works' roof ridges in its industry's colours. Below LOOK_FROM
       a box of the look's fill, as every building is.
       ===================================================================== */

    /** Pixels a plot from which a materials building is drawn as its look rather than a box of its colour: 3 (TileRaster's INSET_FROM; the L0 image is 4). */
    public static final int LOOK_FROM = 3;

    /** The benches a pit or a quarry steps down by from its rim to its middle: 6. */
    public static final int BENCHES = 6;

    /** A leach plant's pond: #4f8fbf. */
    static final int POND = 0xff4f8fbf;

    /** A shaft's headframe: #2b2622, and the winding house in it #8a7f72. */
    static final int HEADFRAME = 0xff2b2622, WINDING = 0xff8a7f72;

    /** The wash plant's conveyor: #9a9aa2. */
    static final int CONVEYOR = 0xff9a9aa2;

    /** A camp's log stacks: #5a3e22. */
    static final int LOGS = 0xff5a3e22;

    /** Planted forest's crowns, in rows over its darker floor: #6a9a55. */
    static final int CROWN = 0xff6a9a55;

    /** How much darker a works' roof ridges are than its fill: 18%. */
    static final double RIDGE_DARK = 0.18;

    /** One materials building's look over its box [x0, x1) x [y0, y1) in pixels: a site's measured from the site's whole square. */
    static void drawLook(TilePainter.Input in, BuildingVisual.Type t, int site, int px, boolean edged, int[] img, int w,
                         int x0, int y0, int x1, int y1) {
        int look = t.look();
        int fill = t.fill(), edge = t.edge();
        // The square the pattern is measured from: the whole site's, or the box.
        int fx0 = x0, fy0 = y0, fx1 = x1 - 1, fy1 = y1 - 1;
        if (site >= 0) {
            fx0 = in.siteFX0[site] * px; fy0 = in.siteFY0[site] * px;
            fx1 = (in.siteFX1[site] + 1) * px - 1; fy1 = (in.siteFY1[site] + 1) * px - 1;
        }
        int sw = fx1 - fx0 + 1, sh = fy1 - fy0 + 1;
        int half = Math.max(1, Math.min(sw, sh) / 2);
        int step = Math.max(2, half / BENCHES);
        int[] pit = t.site() != null ? BuildingVisual.pitColours(t.site()) : null;
        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) {
                int depth = Math.min(Math.min(x - fx0, fx1 - x), Math.min(y - fy0, fy1 - y));
                boolean onEdge = edged && (y == y0 || y == y1 - 1 || x == x0 || x == x1 - 1);
                int c;
                switch (look) {
                    case BuildingVisual.PIT: {
                        int j = depth / step;
                        c = j <= 0 ? pit[2] : depth >= half - step ? pit[0] : (j & 1) == 1 ? pit[1] : blend(pit[1], pit[0], 0.5);
                        break;
                    }
                    case BuildingVisual.QUARRY: {
                        int j = (y - fy0) / step;
                        c = depth < Math.max(1, step / 2) ? edge : (j & 1) == 0 ? fill : blend(fill, edge, 0.35);
                        break;
                    }
                    case BuildingVisual.LEACH: {
                        boolean pond = x - fx0 < sw / 3 && y - fy0 < sh / 3 && depth >= step / 2;
                        int j = (x - fx0) / step;
                        c = pond ? POND : depth < Math.max(1, step / 2) ? edge : (j & 1) == 0 ? fill : blend(fill, edge, 0.4);
                        break;
                    }
                    case BuildingVisual.SHAFT: {
                        int cx = (fx0 + fx1) / 2, cy = (fy0 + fy1) / 2, r = Math.max(1, Math.min(sw, sh) / 10);
                        int dx = Math.abs(x - cx), dy = Math.abs(y - cy);
                        c = dx <= r && dy <= r ? (dx < r / 2 + 1 && dy < r / 2 + 1 && r > 2 ? WINDING : HEADFRAME)
                                : depth < Math.max(1, step / 2) ? edge : fill;
                        break;
                    }
                    case BuildingVisual.WASH: {
                        int band = Math.max(1, px / 4), mid = (y0 + y1) / 2;
                        c = Math.abs(y - mid) < band && x < (x0 + x1) / 2 ? CONVEYOR : onEdge ? edge : fill;
                        break;
                    }
                    case BuildingVisual.CAMP: {
                        int band = Math.max(1, px / 4), h = y1 - y0;
                        boolean log = (Math.abs(y - (y0 + h / 3)) < band || Math.abs(y - (y0 + 2 * h / 3)) < band)
                                && x > x0 + (x1 - x0) / 5 && x < x1 - (x1 - x0) / 5;
                        c = log ? LOGS : onEdge ? edge : fill;
                        break;
                    }
                    case BuildingVisual.STAND: {
                        int g = Math.max(2, px / 2), row = (y - y0) / g;
                        int ox = (x - x0 + ((row & 1) == 1 ? g / 2 : 0)) % g - g / 2, oy = (y - y0) % g - g / 2;
                        int rr = Math.max(0, g / 3);
                        c = ox * ox + oy * oy <= rr * rr ? CROWN : onEdge ? edge : fill;
                        break;
                    }
                    case BuildingVisual.WORKS: {
                        int g = Math.max(3, px);
                        c = onEdge ? edge : (y - y0) % g == g - 1 ? blend(fill, 0xff000000, RIDGE_DARK) : fill;
                        break;
                    }
                    default:
                        c = onEdge ? edge : fill;
                }
                img[y * w + x] = c;
            }
        }
    }

    /* =====================================================================
       AT SEA (0.7.97, batch O13; TilePainter's AT SEA; mockup 3's colours)
       ===================================================================== */

    /** A quay's deck: mockup 3's jetty, #3a4655... */
    public static final int QUAY_DECK = 0xff3a4655;

    /** ...and its edge from EDGE_FROM px a plot: the mockup's #5a6676. */
    public static final int QUAY_EDGE = 0xff5a6676;

    /** A platform's jacket: mockup 3's #2a3540... */
    public static final int JACKET_FILL = 0xff2a3540;

    /** ...edged in the mockup's tan, #c9b68f - its ring's and the pipe's colour too (the ore's). */
    public static final int OIL_TAN = 0xffc9b68f;

    /** A platform's 500 m ring over the water: the tan at the mockup's 25%... */
    public static final double RING_ALPHA = 0.25;

    /** ...dashed 3 px on, 3 off along its arc (the mockup's "3 3"). */
    public static final double RING_DASH_PX = 3;

    /** A crude pipeline: the tan at the mockup's 50%, dashed 2 px on and 4 off (its "2 4"), 1.5 px wide (drawn 1 below 4 px a plot, 2 from it). */
    public static final double PIPE_ALPHA = 0.5;
    /** ...its dash, 2 px on and 4 off along the pipe (the mockup's "2 4"). */
    public static final double PIPE_ON_PX = 2, PIPE_OFF_PX = 4;

    /**
     * The works at sea over the tile's picture: each quay plot the deck,
     * edged from EDGE_FROM; a jacket the mockup's platform, edged; a well its
     * tan dot; then every pixel of a plot a ring or a pipe crosses that lies
     * on its line (within half its width) and on a dash - the dashes measured
     * from the ring's east and along the pipe from its field, so they run on
     * across a tile's edge.
     */
    static void atSea(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        boolean edged = px >= EDGE_FROM;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            int s = p.sea[i] & TilePainter.SEA_WORK;
            if (s == 0) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            if (s == TilePainter.WELL) {
                int r = Math.max(1, px / 4), cx = x0 + px / 2, cy = y0 + px / 2;
                fillRect(img, w, Math.max(x0, cx - r), Math.max(y0, cy - r), Math.min(x0 + px, cx + r), Math.min(y0 + px, cy + r), OIL_TAN);
                continue;
            }
            int fill = s == TilePainter.QUAY ? QUAY_DECK : JACKET_FILL, edge = s == TilePainter.QUAY ? QUAY_EDGE : OIL_TAN;
            fillRect(img, w, x0, y0, x0 + px, y0 + px, fill);
            if (!edged) continue;
            // Its edge where the next plot is not the same work: a jetty's sides, a jacket's outline.
            for (int dir = 0; dir < 4; dir++) {
                int nx = i % tile + TilePainter.DX[dir], ny = i / tile + TilePainter.DY[dir];
                boolean same = nx >= 0 && ny >= 0 && nx < tile && ny < tile && (p.sea[ny * tile + nx] & TilePainter.SEA_WORK) == s;
                if (same) continue;
                if (dir == 0) fillRect(img, w, x0, y0, x0 + px, y0 + 1, edge);
                else if (dir == 2) fillRect(img, w, x0, y0 + px - 1, x0 + px, y0 + px, edge);
                else if (dir == 3) fillRect(img, w, x0, y0, x0 + 1, y0 + px, edge);
                else fillRect(img, w, x0 + px - 1, y0, x0 + px, y0 + px, edge);
            }
        }
        if (in.rings == 0 && in.pipes == 0) return;
        double pipeHalf = (px >= LINES_FROM ? 2 : 1) / 2.0, ringHalf = 0.5;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            int s = p.sea[i];
            if ((s & (TilePainter.SEA_RING | TilePainter.SEA_PIPE)) == 0) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            for (int y = y0; y < y0 + px; y++) {
                for (int x = x0; x < x0 + px; x++) {
                    double fx = (x + 0.5) / px, fy = (y + 0.5) / px;
                    boolean on = false;
                    double alpha = 0;
                    if ((s & TilePainter.SEA_RING) != 0) {
                        for (int r = 0; r < in.rings && !on; r++) {
                            double dx = fx - in.ringX[r], dy = fy - in.ringY[r], d = Math.hypot(dx, dy);
                            if (Math.abs(d - TilePainter.RING_PLOTS) * px > ringHalf) continue;
                            double arc = (Math.atan2(dy, dx) + Math.PI) * TilePainter.RING_PLOTS * px;
                            if (((long) Math.floor(arc / RING_DASH_PX)) % 2 == 0) { on = true; alpha = RING_ALPHA; }
                        }
                    }
                    if ((s & TilePainter.SEA_PIPE) != 0) {
                        for (int q = 0; q < in.pipes; q++) {
                            double ax = in.pipeAX[q], ay = in.pipeAY[q], bx = in.pipeBX[q], by = in.pipeBY[q];
                            double vx = bx - ax, vy = by - ay, len = Math.hypot(vx, vy);
                            if (!(len > 0)) continue;
                            double t = ((fx - ax) * vx + (fy - ay) * vy) / (len * len);
                            if (t < 0 || t > 1) continue;
                            double off = Math.abs((fx - ax) * vy - (fy - ay) * vx) / len;
                            if (off * px > pipeHalf) continue;
                            double along = t * len * px;
                            if (along % (PIPE_ON_PX + PIPE_OFF_PX) < PIPE_ON_PX) { on = true; alpha = Math.max(alpha, PIPE_ALPHA); }
                        }
                    }
                    if (on) img[y * w + x] = blend(img[y * w + x], OIL_TAN, alpha);
                }
            }
        }
    }

    /** Whether plot i of the runs is a highway's (its own, or the track bridging it), under a building or not (0.7.89). */
    static boolean runHighway(TilePainter.Painted p, int i) {
        return p.run[i] == BuildingVisual.HIGHWAY || p.run[i] == TilePainter.RAIL_OVER;
    }

    /**
     * A highway passing over a building (0.7.89): a mine standing on its site
     * where the elevated highway crosses it - drawn over the building, its
     * arms to the highway beside it (spec 2.7: elevated).
     */
    private static void overBuildings(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        int[] cs = crossSection(BuildingVisual.HIGHWAY, px);
        int width = px >= LINES_FROM ? cs[0] : px, o = (px - width) / 2;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (p.use[i] != TilePainter.BUILDING || !runHighway(p, i)) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            int c = in.owned[i] ? ROAD[BuildingVisual.HIGHWAY] : blend(ROAD[BuildingVisual.HIGHWAY], VOID, UNOWNED_DIM);
            int a = o, z = o + width;
            fillRect(img, w, x0 + a, y0 + a, x0 + z, y0 + z, c);
            if (runHighwayAt(in, p, i, 0)) fillRect(img, w, x0 + a, y0, x0 + z, y0 + a, c);
            if (runHighwayAt(in, p, i, 2)) fillRect(img, w, x0 + a, y0 + z, x0 + z, y0 + px, c);
            if (runHighwayAt(in, p, i, 3)) fillRect(img, w, x0, y0 + a, x0 + a, y0 + z, c);
            if (runHighwayAt(in, p, i, 1)) fillRect(img, w, x0 + z, y0 + a, x0 + px, y0 + z, c);
        }
    }

    /** Whether the plot a step from plot i, across the tile's edge too, is a highway's of the runs (under a building or not). */
    private static boolean runHighwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir) {
        int tile = TilePainter.TILE, x = i % tile + TilePainter.DX[dir], y = i / tile + TilePainter.DY[dir];
        if (x >= 0 && y >= 0 && x < tile && y < tile) return runHighway(p, y * tile + x);
        int along = dir == 0 || dir == 2 ? i % tile : i / tile;
        byte f = in.fixedBeyond[dir * tile + along];
        return f == BuildingVisual.HIGHWAY || f == TilePainter.RAIL_OVER;
    }

    /* =====================================================================
       THE MIDDLE AND FAR VIEWS AS BLOCKS (J3b: the mockup's levels 1 and 2)
       ===================================================================== */

    /** A block's opacity over its ground by its share built: 40%, 60%, 80% and 96% (the mockup's ALPHA)... */
    public static final double[] BLOCK_ALPHA = { 0.4, 0.6, 0.8, 0.96 };

    /** ...at a share built under 12%, under 28%, under 50%, and above (the mockup's). */
    public static final double[] BLOCK_FILLS = { 0.12, 0.28, 0.5 };

    /** Blocks the mockup drew at its middle view, in plots a side: 4 (120 m), from 1.4 to 3.2 px a plot... */
    public static final int BLOCK_MIDDLE = 4;

    /** ...and at its far view: 8 (240 m), below 1.4 px a plot, with gravel hidden. */
    public static final int BLOCK_FAR = 8;

    /**
     * A tile at px pixels a plot as the mockup drew its middle and far
     * views: its ground, then blocks of `block` plots a side, each in its
     * dominant kind's colour - by its buildings' plots, the homes' houses and
     * flats together and drawn as flats where flats are the more or the
     * block is the densest - over its ground at BLOCK_ALPHA by its share
     * built, then the roads in their colours over them, gravel and tracks
     * only when asked. Pure: the same tile gives the same pixels.
     */
    public static void blocks(TilePainter.Input in, TilePainter.Painted p, int px, int block, boolean gravel, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            int c = groundColour(in, p, i);
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            fillRect(img, w, x0, y0, x0 + px, y0 + px, c);
        }
        int kinds = BuildingVisual.CLASSES + 1, flatsKey = BuildingVisual.CLASSES;
        int[] key = new int[p.buildings];
        for (int b = 0; b < p.buildings; b++) {
            BuildingVisual.Type t = p.btype[b] >= 0 && p.btype[b] < in.types.length ? in.types[p.btype[b]] : null;
            int cls = t == null ? BuildingVisual.INDUSTRY : t.cls();
            key[b] = t != null && t.flats() ? flatsKey : cls;
        }
        int[] plots = new int[kinds];
        for (int by = 0; by < tile; by += block) {
            for (int bx = 0; bx < tile; bx += block) {
                Arrays.fill(plots, 0);
                int built = 0, area = 0;
                for (int y = by; y < Math.min(tile, by + block); y++) {
                    for (int x = bx; x < Math.min(tile, bx + block); x++) {
                        int i = y * tile + x;
                        area++;
                        if (p.use[i] != TilePainter.BUILDING || p.bld[i] == 0) continue;
                        plots[key[p.bld[i] - 1]]++;
                        built++;
                    }
                }
                if (built == 0) continue;
                int homes = plots[BuildingVisual.HOME] + plots[flatsKey], best = BuildingVisual.HOME, most = homes;
                for (int k = 1; k < BuildingVisual.CLASSES; k++) if (plots[k] > most) { most = plots[k]; best = k; }
                double f = built / (double) area;
                int lvl = f < BLOCK_FILLS[0] ? 0 : f < BLOCK_FILLS[1] ? 1 : f < BLOCK_FILLS[2] ? 2 : 3;
                int colour = best == BuildingVisual.HOME
                        ? (plots[flatsKey] >= plots[BuildingVisual.HOME] || lvl == 3 ? BuildingVisual.FLATS_FILL : BuildingVisual.FILL[BuildingVisual.HOME])
                        : BuildingVisual.FILL[best];
                for (int y = by; y < Math.min(tile, by + block); y++) {
                    for (int x = bx; x < Math.min(tile, bx + block); x++) {
                        int o = (y * px) * w + x * px;
                        int c = blend(img[o], colour, BLOCK_ALPHA[lvl]);
                        fillRect(img, w, x * px, y * px, x * px + px, y * px + px, c);
                    }
                }
            }
        }
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            // A tunnel's plot (ET4) is its bare ground but for every other plot along the run, the run's line dashed (tunnelDash()).
            if (p.tunnel[i] && !tunnelDash(i)) continue;
            int kind = roadKind(p, i);
            boolean hidden = !gravel && (kind == BuildingVisual.GRAVEL || kind == TilePainter.TRACK);
            if (p.use[i] == TilePainter.RAIL && (kind == 0 || hidden)) {
                // Track (0.7.72), and where a hidden street crosses it; drawn as rail (0.8.1) - a plot a bend replaces is the bend's (FX5).
                if (in.curved[i]) continue;
                int x0 = (i % tile) * px, y0 = (i / tile) * px;
                fillRect(img, w, x0, y0, x0 + px, y0 + px, in.owned[i] ? RAIL_DARK : blend(RAIL_DARK, VOID, UNOWNED_DIM));
                continue;
            }
            if (kind == 0 || hidden) continue;
            int c = kind == TilePainter.TRACK ? TRACK_DASH : ROAD[kind];
            if (!in.owned[i]) c = blend(c, VOID, UNOWNED_DIM);
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            fillRect(img, w, x0, y0, x0 + px, y0 + px, c);
            // A highway at least HIGHWAY_LEAST_PX wide: the next plot across it too (the mockup's 1.8 px at its far view).
            if (isHighway(p, i) && px < HIGHWAY_LEAST_PX) {
                int x = i % tile, y = i / tile;
                boolean ns = (y > 0 && isHighway(p, i - tile)) || (y < tile - 1 && isHighway(p, i + tile));
                if (ns && x < tile - 1) fillRect(img, w, x0 + px, y0, x0 + 2 * px, y0 + px, c);
                if (!ns && y < tile - 1) fillRect(img, w, x0, y0 + px, x0 + px, y0 + 2 * px, c);
            }
        }
        // The railway's bends, in RAIL_DARK where the plots they replace are not (FX5).
        blockBends(in, p, px, img);
        // The track's cross-ticks (0.8.1, spec-freight 4.3): from 2 px a plot, a pixel beside the line at each block
        // boundary, so the dark line reads as rail and not a road.
        if (px >= TICKS_FROM) {
            for (int i = 0; i < TilePainter.PLOTS; i++) {
                int kind = roadKind(p, i);
                boolean hidden = !gravel && (kind == BuildingVisual.GRAVEL || kind == TilePainter.TRACK);
                if (p.use[i] != TilePainter.RAIL || (kind != 0 && !hidden) || diagonal(in, i) || in.curved[i] || p.tunnel[i]) continue;
                int x = i % tile, y = i / tile, x0 = x * px, y0 = y * px;
                boolean ns = railAt(in, p, i, 0) || railAt(in, p, i, 2);
                boolean ew = railAt(in, p, i, 1) || railAt(in, p, i, 3);
                if (!ns && !ew) ew = true;
                int c = in.owned[i] ? RAIL_DARK : blend(RAIL_DARK, VOID, UNOWNED_DIM);
                if (ew) {
                    if (x % block != 0) continue;
                    if (y > 0) img[(y0 - 1) * w + x0] = c;
                    if (y < tile - 1) img[(y0 + px) * w + x0] = c;
                } else {
                    if (y % block != 0) continue;
                    if (x > 0) img[y0 * w + (x0 - 1)] = c;
                    if (x < tile - 1) img[y0 * w + (x0 + px)] = c;
                }
            }
        }
        // The works at sea (0.7.97): the quays and jackets in their colours, the rings and pipes as lines.
        atSea(in, p, px, img);
    }

    /** The least a highway is drawn across, in pixels, in the blocks' views: 2 (the mockup's 1.8 at its far view, 2 at its middle). */
    public static final int HIGHWAY_LEAST_PX = 2;

    /** Whether plot i is a highway's own (a run's, not a street of highway plots). */
    static boolean isHighway(TilePainter.Painted p, int i) {
        return p.use[i] == TilePainter.ROAD && p.road[i] == BuildingVisual.HIGHWAY && p.role[i] == 0;
    }

    /** Whether plot i is a street (a track among them), or a highway's plot with a street beneath. */
    static boolean isStreet(TilePainter.Painted p, int i) {
        return (p.use[i] == TilePainter.ROAD || p.use[i] == TilePainter.RAIL) && (p.role[i] != 0 || p.beneath[i]);
    }

    /** The kind of road on plot i - GRAVEL, PAVED, HIGHWAY or (0.7.88) TilePainter.TRACK - a road's plot, or where a street crosses the track - or 0. */
    static int roadKind(TilePainter.Painted p, int i) {
        byte u = p.use[i];
        return u == TilePainter.ROAD || u == TilePainter.RAIL ? p.road[i] : 0;
    }

    /** The side of plot i a street lies on, north, east, south, west, or -1 for none. */
    static int faceOf(TilePainter.Painted p, int i) {
        int tile = TilePainter.TILE, x = i % tile, y = i / tile;
        if (y > 0 && roadKind(p, i - tile) != 0) return 0;
        if (x < tile - 1 && roadKind(p, i + 1) != 0) return 1;
        if (y < tile - 1 && roadKind(p, i + tile) != 0) return 2;
        if (x > 0 && roadKind(p, i - 1) != 0) return 3;
        return -1;
    }

    /** A road's line across its plot, by kind at px pixels a plot: {its whole width, its casing either side} - the highway's and (as a paved road's) the track's. */
    static int[] crossSection(int kind, int px) {
        if (kind == BuildingVisual.GRAVEL) return new int[] { Math.min(px, Math.max(ROAD_LEAST_PX[1], (int) Math.round(ROAD_WIDTH[1] * px))), 0 };
        int pavedFill = Math.max(ROAD_LEAST_PX[2], (int) Math.round(ROAD_WIDTH[2] * px));
        int pavedCase = px >= 4 ? 1 : 0;
        int paved = Math.min(px, pavedFill + 2 * pavedCase);
        if (kind == BuildingVisual.PAVED) return new int[] { paved, pavedCase };
        int fill = Math.max(ROAD_LEAST_PX[3], (int) Math.round(ROAD_WIDTH[3] * px));
        int cas = px >= 6 ? 1 : 0;
        return new int[] { Math.max(paved, Math.min(px, fill + 2 * cas)), cas };
    }

    /** Whether the plot a step from plot i - across the tile's edge too, as the plans have it (Input.beyond) - is a way of the street at i: a street, or a highway's plot a street passes beneath. */
    private static boolean streetAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir) {
        int tile = TilePainter.TILE, x = i % tile + TilePainter.DX[dir], y = i / tile + TilePainter.DY[dir];
        boolean street;
        if (x >= 0 && y >= 0 && x < tile && y < tile) street = isStreet(p, y * tile + x);
        else {
            int along = dir == 0 || dir == 2 ? i % tile : i / tile;
            street = (in.beyond[dir * tile + along] & TilePainter.S_KIND) != DistrictPlan.NONE;
        }
        if (!street) return false;
        if (in.avenueMarks != 0) {
            // An avenue's plots (CL4): the staircase's own plots are streets beside each other all along, and the band joins them, so no
            // way runs from one to the next; a street beside the avenue joins it only where it crosses (axisArm()), from either side.
            boolean here = in.avenue[i] != 0, there = in.avenueAt(x, y) != 0;
            if (here && there) return false;
            if ((here || there) && !(here ? axisArm(in, p, i % tile, i / tile, dir) : axisArm(in, p, x, y, (dir + 2) & 3))) return false;
        }
        // A curve's plots (CL4, slice D), likewise: the plots the band draws have no bars between them, and a street joins one where it crosses it.
        return in.curveMarks == 0 || curveLink(in, p, i % tile, i / tile, x, y, dir);
    }

    /** ...and a highway's own plot, a step from plot i. */
    private static boolean highwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir) {
        int tile = TilePainter.TILE, x = i % tile + TilePainter.DX[dir], y = i / tile + TilePainter.DY[dir];
        if (x >= 0 && y >= 0 && x < tile && y < tile) return isHighway(p, y * tile + x);
        int along = dir == 0 || dir == 2 ? i % tile : i / tile;
        byte f = in.fixedBeyond[dir * tile + along];
        return f == BuildingVisual.HIGHWAY || f == TilePainter.RAIL_OVER;
    }

    /** ...and a straight highway's, a step from plot i: not a 45-degree stretch's plot, whose marks (across the tile's edge too: marksBeyond) say it is no highway beside a straight one for a stack's test. */
    private static boolean straightHighwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir) {
        if (!highwayAt(in, p, i, dir)) return false;
        int tile = TilePainter.TILE, x = i % tile + TilePainter.DX[dir], y = i / tile + TilePainter.DY[dir];
        byte mark;
        if (x >= 0 && y >= 0 && x < tile && y < tile) {
            mark = in.marks[y * tile + x];
        } else {
            int along = dir == 0 || dir == 2 ? i % tile : i / tile;
            mark = in.marksBeyond[dir * tile + along];
        }
        return (mark & CityRuns.M_DIAG) == 0;
    }

    /** ...and the railway's track (since 0.7.89 where it bridges a highway too: the runs' RAIL_OVER). */
    private static boolean railAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir) {
        int tile = TilePainter.TILE, x = i % tile + TilePainter.DX[dir], y = i / tile + TilePainter.DY[dir];
        if (x >= 0 && y >= 0 && x < tile && y < tile) return p.use[y * tile + x] == TilePainter.RAIL || p.run[y * tile + x] == TilePainter.RAIL_OVER;
        int along = dir == 0 || dir == 2 ? i % tile : i / tile;
        byte f = in.fixedBeyond[dir * tile + along];
        return f == TilePainter.RAIL || f == TilePainter.RAIL_OVER;
    }

    /**
     * Whether the plot a step from plot i, on this tile, is a plot of a yard's
     * box that carries the runs' track (FX5 Y2): the line the yard stands on,
     * which runs on under its ground into the fan. The track outside the box
     * that meets it is not a dead end, so it is drawn to the box's edge and the
     * vehicles that run the line into the yard are on the rail.
     */
    private static boolean yardLineAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir) {
        int tile = TilePainter.TILE, x = i % tile + TilePainter.DX[dir], y = i / tile + TilePainter.DY[dir];
        if (x < 0 || y < 0 || x >= tile || y >= tile) return false;
        int at = y * tile + x;
        if (p.use[at] != TilePainter.BUILDING || p.run[at] != TilePainter.RAIL || p.bld[at] == 0) return false;
        int type = p.btype[p.bld[at] - 1];
        return type >= 0 && type < in.types.length && in.types[type] != null && in.types[type].terminal();
    }

    /** The track beside plot i, north, east, south and west, one bit each (bit d for direction d): its neighbours' track, and a yard's line running on under the yard's ground. */
    private static int armsOf(TilePainter.Input in, TilePainter.Painted p, int i) {
        int arms = 0;
        for (int d = 0; d < 4; d++) if (railAt(in, p, i, d) || yardLineAt(in, p, i, d)) arms |= 1 << d;
        return arms;
    }

    /** Whether plot i is a 45-degree stretch's (the runs' M_DIAG): drawn as a band along its line. */
    static boolean diagonal(TilePainter.Input in, int i) {
        return (in.marks[i] & CityRuns.M_DIAG) != 0;
    }

    /**
     * A 45-degree stretch's band across plot i (spec 2.7: "a curve is drawn
     * smooth; in plots it is a staircase"): the pixels within `half` pixels
     * of the stretch's line - through the plot's middle, or for a
     * staircase's corner (CityRuns.M_CORNER) the middle of the plot before it
     * - in colour c.
     */
    static void diagBand(int[] img, int w, int i, int px, byte mark, double half, int c) {
        int code = (mark >> CityRuns.M_DIAG_SHIFT) & 3;
        int dx = code == 0 || code == 3 ? 1 : -1;
        double pxl = (mark & CityRuns.M_CORNER) != 0 ? (0.5 - dx) * px : 0.5 * px, pyl = 0.5 * px;
        diagLine(img, w, i, px, code, pxl, pyl, half, Double.POSITIVE_INFINITY, c);
    }

    /**
     * The pixels of plot i within `half` pixels of a line at a 45-degree
     * heading's code through the point (pxl, pyl) of the plot's pixels, and
     * no farther along the line than `along` pixels either side of that
     * point (FX3: diagBand() for a line that passes through another plot's
     * middle, and for the dashes), in colour c.
     */
    private static void diagLine(int[] img, int w, int i, int px, int code, double pxl, double pyl, double half, double along, int c) {
        int tile = TilePainter.TILE, x0 = (i % tile) * px, y0 = (i / tile) * px;
        int dx = code == 0 || code == 3 ? 1 : -1, dy = code == 0 || code == 1 ? 1 : -1;
        for (int v = 0; v < px; v++) {
            for (int u = 0; u < px; u++) {
                double qx = u + 0.5 - pxl, qy = v + 0.5 - pyl;
                if (Math.abs(qx * dy - qy * dx) / Math.sqrt(2) > half) continue;
                if (Math.abs(qx * dx + qy * dy) / Math.sqrt(2) > along) continue;
                img[(y0 + v) * w + x0 + u] = c;
            }
        }
    }

    /** The marks of the 45-degree stretch's step plot at (x, y) - in the tile, or just across one of its edges (marksBeyond) - or -1 where that plot is no step: a corner, a straight highway, other ground. */
    private static int stepMarkAt(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int tile = TilePainter.TILE, mark;
        if (x >= 0 && y >= 0 && x < tile && y < tile) {
            int j = y * tile + x;
            if (!isHighway(p, j) || p.tunnel[j]) return -1;
            mark = in.marks[j];
        } else {
            if ((x < 0 || x >= tile) && (y < 0 || y >= tile)) return -1;
            int dir = y < 0 ? 0 : y >= tile ? 2 : x < 0 ? 3 : 1, along = dir == 0 || dir == 2 ? x : y;
            byte f = in.fixedBeyond[dir * tile + along];
            if (f != BuildingVisual.HIGHWAY && f != TilePainter.RAIL_OVER) return -1;
            mark = in.marksBeyond[dir * tile + along];
        }
        return (mark & CityRuns.M_DIAG) != 0 && (mark & CityRuns.M_CORNER) == 0 ? mark & 0xff : -1;
    }

    /**
     * Where a 45-degree stretch passes between two of its steps the line
     * clips the corner of the plot that is not the run's - the staircase's
     * corners are on the other side of it - and the band would leave a notch
     * there (FX3, Jerus's eye check of 0.8.3). Plot i, when it is open
     * ground (not a highway, not a building), has a step on one axis and the
     * next step of the same stretch on the other: the band of the stretch's
     * line, through the step's middle, over plot i's pixels, `half` out in
     * colour c. A stretch's end adds nothing here: there is no next step (an arm's last step ends squared, diagEndOpen(); a joint with straight highway is an arc, highwayBends()).
     */
    private static void diagOpen(TilePainter.Input in, TilePainter.Painted p, int i, int px, double half, int c, int[] img) {
        if (p.use[i] == TilePainter.BUILDING || diagonal(in, i)) return;
        int tile = TilePainter.TILE, x = i % tile, y = i / tile;
        int west = stepMarkAt(in, p, x - 1, y), east = stepMarkAt(in, p, x + 1, y);
        if (west < 0 && east < 0) return;
        int north = stepMarkAt(in, p, x, y - 1), south = stepMarkAt(in, p, x, y + 1);
        if (north < 0 && south < 0) return;
        for (int ex = -1; ex <= 1; ex += 2) {
            for (int ey = -1; ey <= 1; ey += 2) {
                int a = ex < 0 ? west : east, b = ey < 0 ? north : south;
                if (a < 0 || b < 0) continue;
                int code = (a >> CityRuns.M_DIAG_SHIFT) & 3;
                if (((b >> CityRuns.M_DIAG_SHIFT) & 3) != code) continue;
                int dx = code == 0 || code == 3 ? 1 : -1, dy = code == 0 || code == 1 ? 1 : -1;
                // The two steps are diagonal neighbours of each other along the heading: b - a = (-ex, ey) = +-(dx, dy).
                if (!(ex == -dx && ey == dy) && !(ex == dx && ey == -dy)) continue;
                diagLine(img, tile * px, i, px, code, (0.5 + ex) * px, 0.5 * px, half, Double.POSITIVE_INFINITY, c);
            }
        }
    }

    /**
     * Whether a highway bend draws plot i instead of its cell (FX5 slice D):
     * the plot is one Input.curved names and no bridge's - a bridge's deck is
     * straight and the arc is never drawn over water, so a curved plot over
     * fresh water is left as the plain staircase or straight it was.
     */
    private static boolean bendReplaces(TilePainter.Input in, TilePainter.Painted p, int i) {
        return in.curved[i] && !p.bridge[i] && !p.tunnel[i];
    }

    /** Whether plot (x, y), in the tile or just across one of its edges on an axis, is a highway's own. */
    private static boolean highwayPlot(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int tile = TilePainter.TILE;
        if (x >= 0 && y >= 0 && x < tile && y < tile) return isHighway(p, y * tile + x);
        if ((x < 0 || x >= tile) && (y < 0 || y >= tile)) return false;
        int dir = y < 0 ? 0 : y >= tile ? 2 : x < 0 ? 3 : 1, along = dir == 0 || dir == 2 ? x : y;
        byte f = in.fixedBeyond[dir * tile + along];
        return f == BuildingVisual.HIGHWAY || f == TilePainter.RAIL_OVER;
    }

    /**
     * An arm that ends on its diagonal (FX5 slice D) ends squared across its
     * heading at its last step's far edge. The last step's band (diagBand())
     * stops at its plot's edges, the staircase's, but the band's width runs on
     * past them into the two plots beside the step on the side the stretch
     * was heading: plot i, when it is open ground, is one, and the line's band
     * - through the end step's middle, no farther along it than that plot's
     * far corner (0.5 sqrt(2) plots) - is laid over it in colour c, `half` out
     * as the passes lay every band. The step is the arm's last (the runs' M_RAMP
     * on a step) with no highway beyond it, else the highway there draws the
     * join as any other.
     */
    private static void diagEndOpen(TilePainter.Input in, TilePainter.Painted p, int i, int px, double half, int c, int[] img) {
        if (p.use[i] == TilePainter.BUILDING || diagonal(in, i)) return;
        int tile = TilePainter.TILE, x = i % tile, y = i / tile;
        for (int d = 0; d < 4; d++) {
            int sx = x + TilePainter.DX[d], sy = y + TilePainter.DY[d];
            int mark = stepMarkAt(in, p, sx, sy);
            if (mark < 0 || (mark & CityRuns.M_RAMP) == 0) continue;
            int code = (mark >> CityRuns.M_DIAG_SHIFT) & 3;
            int dx = code == 0 || code == 3 ? 1 : -1, dy = code == 0 || code == 1 ? 1 : -1;
            // Plot i is one of the two beside the step on the heading's side: (sx + dx, sy) or (sx, sy + dy).
            if (!((x == sx + dx && y == sy) || (x == sx && y == sy + dy))) continue;
            if (highwayPlot(in, p, sx + dx, sy + dy) || highwayPlot(in, p, sx + dx, sy) || highwayPlot(in, p, sx, sy + dy)) continue;
            diagLine(img, tile * px, i, px, code, (0.5 + sx - x) * px, (0.5 + sy - y) * px, half, 0.5 * Math.sqrt(2) * px, c);
        }
    }

    /* =====================================================================
       THE AVENUES (batch CL4; the study spec-city-layout 5.2)
       ===================================================================== */

    /*
     * WHY. The district plans lay four avenues at 45 degrees out from the founding site as staircases of arterial street
     * plots (DistrictPlan, AVENUES), and drawn as the plan has them - each plot a bar across it toward the street plots
     * beside it - a staircase is a jagged chain of crosses. From LINES_FROM px a plot they are drawn as the highways'
     * 45-degree stretches are: one smooth band along each arm's line through the steps' middles (diagLine(), avLine()),
     * `half` the street's surface wide (SURFACE by its width, as a straight street's bar), in its kind's colour. The
     * staircase's plots are verge outside the band, and so are the outer plots, the fourth plot of each 2 x 2 block the
     * line clips (O_k = S_(k-1) + (0, dy), where no building stands): open ground the band crosses, filled in as the
     * highway's notch is (diagOpen()). The marks are TilePainter.Input.avenue (and avenueRing, for a step across the tile's
     * edge): CityRuns' M_DIAG, the arm's heading from M_DIAG_SHIFT, M_CORNER on a corner. A street that crosses an avenue
     * keeps its bars through it (an AXIS ARM: a street plot beside the avenue's, no avenue's own, with a street plot
     * beyond it), so it reads as one straight line across the band; none of the staircase's own plots draws a bar. Below
     * LINES_FROM nothing changes: a plot is its colour, and the staircase reads as a diagonal of pixels.
     */

    /** The heading of an avenue's arm, by CityRuns.diagCode(): east-south, west-south, west-north, east-north (as DistrictPlan.AVENUE_DX and AVENUE_DY): +1 or -1 along x... */
    private static int armDx(int code) { return code == 0 || code == 3 ? 1 : -1; }

    /** ...and along y. */
    private static int armDy(int code) { return code == 0 || code == 1 ? 1 : -1; }

    /** The street code on plot (x, y) of the tile or within REACH of it (Input.street, Input.ring): 0 for none or a plot farther out. */
    private static int codeAt(TilePainter.Input in, int x, int y) {
        int tile = TilePainter.TILE, r = TilePainter.REACH;
        if (x >= 0 && y >= 0 && x < tile && y < tile) return in.street[y * tile + x] & 0xff;
        if (x < -r || y < -r || x >= tile + r || y >= tile + r) return 0;
        return in.ring[TilePainter.ringIndex(x, y)] & 0xff;
    }

    /** Whether plot (x, y), in the tile or within REACH of it, is a street (as streetAt() reads it: a track, and a highway's plot a street passes beneath, among them). */
    private static boolean ringStreet(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int tile = TilePainter.TILE;
        if (x >= 0 && y >= 0 && x < tile && y < tile) return isStreet(p, y * tile + x);
        return (codeAt(in, x, y) & TilePainter.S_KIND) != DistrictPlan.NONE;
    }

    /**
     * Whether avenue plot (x, y) of the tile has an AXIS ARM in direction d (north, east, south, west): a street running into it
     * that is none of the avenue's - the plot beside it a street plot and no avenue's, and the plot beyond that another (a
     * street that goes on out, not a stub beside the staircase). It is how a street the avenue crosses is told from the
     * staircase's own plots, which are streets beside each other all along.
     */
    private static boolean axisArm(TilePainter.Input in, TilePainter.Painted p, int x, int y, int d) {
        int nx = x + TilePainter.DX[d], ny = y + TilePainter.DY[d];
        if (in.avenueAt(nx, ny) != 0 || !ringStreet(in, p, nx, ny)) return false;
        int bx = nx + TilePainter.DX[d], by = ny + TilePainter.DY[d];
        return in.avenueAt(bx, by) == 0 && ringStreet(in, p, bx, by);
    }

    /** The mark of the avenue's step (a plot of the line, no corner) at (x, y) of the tile or its ring, or -1. */
    private static int stepAt(TilePainter.Input in, int x, int y) {
        int m = in.avenueAt(x, y);
        return m != 0 && (m & CityRuns.M_CORNER) == 0 ? m : -1;
    }

    /** Whether plot (x, y) of the tile or its ring bridges fresh water: its street's bridge, or the deck the painter laid. */
    private static boolean bridgeAt(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int tile = TilePainter.TILE;
        if (x >= 0 && y >= 0 && x < tile && y < tile) return p.bridge[y * tile + x];
        return (codeAt(in, x, y) & TilePainter.S_BRIDGE) != 0;
    }

    /** What marks an outer plot in avenueNotches(): bit 24, over the arm's code (low two bits) and the codes of the two steps either side of it (bits 8 to 15 and 16 to 23). */
    private static final int NOTCH = 1 << 24;

    /**
     * The OUTER PLOTS of the tile's avenues (O_k, the fourth plot of the 2 x 2 block S_(k-1), C_k, S_k, O_k): by plot, 0 for none,
     * else NOTCH, the arm's code, and the street codes of the two steps either side of it, S_(k-1) in its column and S_k in
     * its row - one plot of open ground, a street's, or water a bridge crosses, the band passes through and a building never
     * stands on. null when the tile has none.
     */
    private static int[] avenueNotches(TilePainter.Input in, TilePainter.Painted p) {
        int tile = TilePainter.TILE;
        int[] out = null;
        for (int y = 0; y < tile; y++) {
            for (int x = 0; x < tile; x++) {
                int i = y * tile + x;
                if (in.avenue[i] != 0 || p.use[i] == TilePainter.BUILDING || p.use[i] == TilePainter.RAIL || in.terrain[i] == World.SALT) continue;
                if (p.use[i] == TilePainter.ROAD && !isStreet(p, i)) continue; // a highway's own plot
                // A step beside it on each axis, or none of its four combinations holds.
                if (in.avenueAt(x - 1, y) == 0 && in.avenueAt(x + 1, y) == 0 || in.avenueAt(x, y - 1) == 0 && in.avenueAt(x, y + 1) == 0) continue;
                for (int sy = -1; sy <= 1; sy += 2) {
                    for (int sx = -1; sx <= 1; sx += 2) {
                        int a = stepAt(in, x + sx, y), b = stepAt(in, x, y - sy);
                        if (a < 0 || b < 0) continue;
                        int code = sx > 0 ? (sy > 0 ? 0 : 3) : (sy > 0 ? 1 : 2);
                        if (((a >> CityRuns.M_DIAG_SHIFT) & 3) != code || ((b >> CityRuns.M_DIAG_SHIFT) & 3) != code) continue;
                        if (out == null) out = new int[TilePainter.PLOTS];
                        out[i] = NOTCH | code | codeAt(in, x + sx, y) << 8 | codeAt(in, x, y - sy) << 16;
                    }
                }
            }
        }
        return out;
    }

    /** Whether outer plot i (avenueNotches() says it is) is open ground that takes a verge: nothing on it, dry. */
    private static boolean notchVerge(TilePainter.Input in, TilePainter.Painted p, int i) {
        return p.use[i] == TilePainter.EMPTY && in.terrain[i] != World.FRESH && !World.salt(in.terrain[i]);
    }

    /**
     * The pixels of plot i within `half` pixels of the line at the heading `code` through the point (pxl, pyl) of the plot's
     * pixels, no farther along the heading than sMax pixels from that point and no farther behind it than sMin (negative), in
     * colour c - and, with `grain` set, GRAVEL_GRAIN_SHARE of them by the hash a gravel street's grain is, so the same street
     * speckles the same at every paint. With dashEvery > 0 only the first dashLen pixels of every dashEvery along the line,
     * counted from the world's pixel origin, so a track's dashes run on across the plots and the tiles. diagLine() with
     * what the avenues need: both ends, the grain and the dashes.
     */
    private static void avLine(TilePainter.Input in, int[] img, int w, int i, int px, int code, double pxl, double pyl, double half,
                               double sMin, double sMax, int c, int grain, int dashEvery, int dashLen) {
        int tile = TilePainter.TILE, x0 = (i % tile) * px, y0 = (i / tile) * px;
        int dx = armDx(code), dy = armDy(code);
        double root2 = Math.sqrt(2);
        for (int v = 0; v < px; v++) {
            for (int u = 0; u < px; u++) {
                double qx = u + 0.5 - pxl, qy = v + 0.5 - pyl;
                if (Math.abs(qx * dy - qy * dx) / root2 > half) continue;
                double s = (qx * dx + qy * dy) / root2;
                if (s < sMin || s > sMax) continue;
                if (dashEvery > 0) {
                    long gx = (in.tx * tile + i % tile) * px + u, gy = (in.ty * tile + i / tile) * px + v;
                    double along = ((gx + 0.5) * dx + (gy + 0.5) * dy) / root2;
                    if (Math.floorMod((long) Math.floor(along), (long) dashEvery) >= dashLen) continue;
                }
                int col = c;
                if (grain != 0) {
                    int X = x0 + u, Y = y0 + v;
                    double h = World.unit(World.mix((in.tx * 8191L + X) * 0x9E3779B97F4A7C15L
                            ^ (in.ty * 8191L + Y) * 0xC2B2AE3D27D4EB4FL ^ in.seed ^ 0x47524156L));
                    if (h < GRAVEL_GRAIN_SHARE) col = grain;
                }
                img[(y0 + v) * w + x0 + u] = col;
            }
        }
    }

    /** A street's surface across its plot, in pixels at px a plot, as a straight street's bar is (SURFACE by its width code, 1 or 2). */
    private static int surfaceOf(int width, int px) {
        return Math.max(1, (int) Math.round(SURFACE[Math.max(1, Math.min(2, width))] * px));
    }

    /**
     * Avenue plot i drawn (from LINES_FROM px a plot): the bar across it for each street that crosses the avenue through it
     * (axisArm()), in that street's own kind and width - so the street reads as one line across the band - over the plot's
     * verge, and the band along the avenue's line, `half` its surface wide, in the avenue's kind's colour, with gravel's grain,
     * or a track's dashes. With `box`, the same in JUNCTION_BOX: the plot's part of a signalled crossing's one box. Over the
     * main line (D18) the band has a deck a pixel wider either side, as a bridge has. A plot beneath a highway is drawn by the
     * highway.
     */
    private static void avenuePlot(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, boolean box) {
        int tile = TilePainter.TILE, x = i % tile, y = i / tile;
        int kind = p.road[i];
        if (p.beneath[i] || kind < BuildingVisual.GRAVEL || kind > TilePainter.TRACK) return;
        int mark = in.avenue[i] & 0xff, code = (mark >> CityRuns.M_DIAG_SHIFT) & 3, dx = armDx(code);
        double pxl = (mark & CityRuns.M_CORNER) != 0 ? (0.5 - dx) * px : 0.5 * px, pyl = 0.5 * px;
        boolean owned = in.owned[i];
        int w = tile * px;
        // The streets that cross it: the code of the first of each axis's arms, 0 for none.
        int vCode = avenueArm(in, p, x, y, 0, 2), hCode = avenueArm(in, p, x, y, 1, 3);
        if (kind == TilePainter.TRACK) {
            // A track: dashes on the verge, along the line and down each street that crosses it.
            if (box) return;
            int dashT = Math.max(2, px / 5), dashStep = Math.max(2, px / 2), dashLen = Math.max(2, px / 3);
            int c = owned ? TRACK_DASH : blend(TRACK_DASH, VOID, UNOWNED_DIM);
            if (hCode != 0) axisBar(in, i, px, img, true, hCode, false);
            if (vCode != 0) axisBar(in, i, px, img, false, vCode, false);
            avLine(in, img, w, i, px, code, pxl, pyl, dashT / 2.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, c, 0, dashStep, dashLen);
            return;
        }
        int t = surfaceOf(p.width[i], px);
        int c = box ? JUNCTION_BOX : ROAD[kind];
        if (!owned) c = blend(c, VOID, UNOWNED_DIM);
        int grain = !box && kind == BuildingVisual.GRAVEL ? (owned ? GRAVEL_GRAIN : blend(GRAVEL_GRAIN, VOID, UNOWNED_DIM)) : 0;
        boolean main = (in.marks[i] & TilePainter.M_MAIN) != 0;
        if (!box && p.use[i] == TilePainter.RAIL && main) {
            int deck = owned ? DECK : blend(DECK, VOID, UNOWNED_DIM);
            avLine(in, img, w, i, px, code, pxl, pyl, t / 2.0 + 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, deck, 0, 0, 0);
        }
        if (hCode != 0) axisBar(in, i, px, img, true, box ? in.street[i] & 0xff : hCode, box);
        if (vCode != 0) axisBar(in, i, px, img, false, box ? in.street[i] & 0xff : vCode, box);
        avLine(in, img, w, i, px, code, pxl, pyl, t / 2.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, c, grain, 0, 0);
    }

    /** The street code of the first of the two arms (directions d0 and d1, the axis's two ways) of avenue plot (x, y) that is an axis arm: the code of the plot beside it; 0 for none. */
    private static int avenueArm(TilePainter.Input in, TilePainter.Painted p, int x, int y, int d0, int d1) {
        int d = axisArm(in, p, x, y, d0) ? d0 : axisArm(in, p, x, y, d1) ? d1 : -1;
        return d < 0 ? 0 : codeAt(in, x + TilePainter.DX[d], y + TilePainter.DY[d]);
    }

    /**
     * The bar of a street that crosses an avenue, across avenue plot i along one axis (horizontal or not), edge to edge, in the
     * kind and width of its code `arm` - gravel's grain, a track's dashes, a highway's none - or, boxed, in JUNCTION_BOX
     * at the width of `arm`, then the code of the plot's own street.
     */
    private static void axisBar(TilePainter.Input in, int i, int px, int[] img, boolean horizontal, int arm, boolean box) {
        int tile = TilePainter.TILE, w = tile * px, x0 = (i % tile) * px, y0 = (i / tile) * px;
        boolean owned = in.owned[i];
        int kind = box ? BuildingVisual.PAVED : TilePainter.kindOf(arm);
        if (kind < BuildingVisual.GRAVEL || kind > TilePainter.TRACK) return;
        if (kind == TilePainter.TRACK) {
            int dashT = Math.max(2, px / 5), dashStep = Math.max(2, px / 2), dashLen = Math.max(2, px / 3), mid = (px - dashT) / 2;
            int c = owned ? TRACK_DASH : blend(TRACK_DASH, VOID, UNOWNED_DIM);
            for (int s = 0; s < px; s += dashStep) {
                int e = Math.min(px, s + dashLen);
                if (horizontal) fillRect(img, w, x0 + s, y0 + mid, x0 + e, y0 + mid + dashT, c);
                else fillRect(img, w, x0 + mid, y0 + s, x0 + mid + dashT, y0 + e, c);
            }
            return;
        }
        int t = surfaceOf((arm >> TilePainter.S_WIDTH_SHIFT) & 3, px), o = (px - t) / 2;
        int c = box ? JUNCTION_BOX : ROAD[kind];
        if (!owned) c = blend(c, VOID, UNOWNED_DIM);
        int grain = !box && kind == BuildingVisual.GRAVEL ? (owned ? GRAVEL_GRAIN : blend(GRAVEL_GRAIN, VOID, UNOWNED_DIM)) : 0;
        int bx0 = horizontal ? x0 : x0 + o, by0 = horizontal ? y0 + o : y0, bx1 = horizontal ? x0 + px : x0 + o + t, by1 = horizontal ? y0 + o + t : y0 + px;
        fillRect(img, w, bx0, by0, bx1, by1, c);
        if (grain != 0) grain(in, img, w, bx0, by0, bx1, by1, grain);
    }

    /** The kind, in BuildingVisual's numbering or TilePainter.TRACK, an outer plot between two steps is drawn as: the lesser of theirs, a track if either is one; 0 where a step has none (beneath a highway). */
    private static int notchKind(int codeA, int codeB) {
        int a = TilePainter.kindOf(codeA), b = TilePainter.kindOf(codeB);
        if (a == 0 || b == 0) return 0;
        if (a == TilePainter.TRACK || b == TilePainter.TRACK) return TilePainter.TRACK;
        return Math.min(Math.min(a, b), BuildingVisual.PAVED);
    }

    /**
     * Outer plot i (avenueNotches() entry `info`) drawn: the band of its arm's line through the middle of the step beside it
     * across its pixels - the notch the staircase's plots would leave in the band - in the kind and width of the two steps
     * either side, the narrower and the lesser. Where both steps bridge fresh water the plot is a bridge's too: a deck first
     * (deck = true, drawn with the decks), then the surface. `box` as avenuePlot().
     */
    private static void avenueNotch(TilePainter.Input in, TilePainter.Painted p, int i, int info, int px, int[] img, boolean deck, boolean box) {
        int codeA = (info >> 8) & 0xff, codeB = (info >> 16) & 0xff, code = info & 3;
        int kind = notchKind(codeA, codeB);
        int tile = TilePainter.TILE, w = tile * px, x = i % tile, y = i / tile;
        if (kind == 0 || (box && kind == TilePainter.TRACK)) return;
        boolean wet = World.bridgeable(in.terrain[i]);
        int dx = armDx(code), dy = armDy(code);
        boolean bridged = bridgeAt(in, p, x + dx, y) && bridgeAt(in, p, x, y - dy);
        if (wet && !bridged) return;
        if (deck && !bridged) return;
        int width = Math.min(Math.max(1, (codeA >> TilePainter.S_WIDTH_SHIFT) & 3), Math.max(1, (codeB >> TilePainter.S_WIDTH_SHIFT) & 3));
        // The line through the step beside it in its row (S_k, at (dx, 0) from this plot's middle).
        double pxl = (0.5 + dx) * px, pyl = 0.5 * px;
        boolean owned = in.owned[i];
        if (kind == TilePainter.TRACK) {
            if (deck) return;
            int dashT = Math.max(2, px / 5), dashStep = Math.max(2, px / 2), dashLen = Math.max(2, px / 3);
            int c = owned ? TRACK_DASH : blend(TRACK_DASH, VOID, UNOWNED_DIM);
            avLine(in, img, w, i, px, code, pxl, pyl, dashT / 2.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, c, 0, dashStep, dashLen);
            return;
        }
        double half = surfaceOf(width, px) / 2.0;
        if (deck) {
            int c = owned ? DECK : blend(DECK, VOID, UNOWNED_DIM);
            avLine(in, img, w, i, px, code, pxl, pyl, half + 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, c, 0, 0, 0);
            return;
        }
        int c = box ? JUNCTION_BOX : ROAD[kind];
        if (!owned) c = blend(c, VOID, UNOWNED_DIM);
        int grain = !box && kind == BuildingVisual.GRAVEL ? (owned ? GRAVEL_GRAIN : blend(GRAVEL_GRAIN, VOID, UNOWNED_DIM)) : 0;
        avLine(in, img, w, i, px, code, pxl, pyl, half, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, c, grain, 0, 0);
    }

    /**
     * A piece of avenue's end: where a step's successor corner is not laid (its head: the avenue ends there, at a yard, at water too
     * wide, at the edge of the city's ground) or its predecessor's is not (its tail), the band ends squared across its
     * heading at the step's far edge (the highway arm's end, diagEndOpen()): the open ground beside the step on that side -
     * (x + dx, y) and (x, y + dy) at a head, (x - dx, y) and (x, y - dy) at a tail - gets the line's band through the step's
     * middle, no farther along than 0.5 sqrt 2 plots from it. A street beside the step is no open ground: the bar of the
     * street the avenue meets takes it, so the avenue meets the street without a stub or a notch.
     */
    private static void avenueEnds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (in.avenue[i] != 0 || (p.use[i] != TilePainter.EMPTY && p.use[i] != TilePainter.FIELD) || in.terrain[i] == World.FRESH || World.salt(in.terrain[i])) continue;
            int x = i % tile, y = i / tile;
            if (in.avenueAt(x - 1, y) == 0 && in.avenueAt(x + 1, y) == 0 && in.avenueAt(x, y - 1) == 0 && in.avenueAt(x, y + 1) == 0) continue;
            for (int d = 0; d < 4; d++) {
                int sx = x + TilePainter.DX[d], sy = y + TilePainter.DY[d];
                int m = stepAt(in, sx, sy);
                if (m < 0) continue;
                int code = (m >> CityRuns.M_DIAG_SHIFT) & 3, dx = armDx(code), dy = armDy(code);
                int sc = codeAt(in, sx, sy), kind = TilePainter.kindOf(sc);
                if (kind < BuildingVisual.GRAVEL || kind > TilePainter.TRACK) continue;
                boolean head = in.avenueAt(sx + dx, sy) == 0 && ((x == sx + dx && y == sy) || (x == sx && y == sy + dy));
                boolean tail = in.avenueAt(sx, sy - dy) == 0 && ((x == sx - dx && y == sy) || (x == sx && y == sy - dy));
                if (!head && !tail) continue;
                double pxl = (0.5 + sx - x) * px, pyl = (0.5 + sy - y) * px, far = 0.5 * Math.sqrt(2) * px;
                boolean owned = in.owned[i];
                if (kind == TilePainter.TRACK) {
                    int dashT = Math.max(2, px / 5), dashStep = Math.max(2, px / 2), dashLen = Math.max(2, px / 3);
                    int c = owned ? TRACK_DASH : blend(TRACK_DASH, VOID, UNOWNED_DIM);
                    avLine(in, img, w, i, px, code, pxl, pyl, dashT / 2.0, head ? Double.NEGATIVE_INFINITY : -far, head ? far : Double.POSITIVE_INFINITY, c, 0, dashStep, dashLen);
                    continue;
                }
                double half = surfaceOf((sc >> TilePainter.S_WIDTH_SHIFT) & 3, px) / 2.0;
                int c = owned ? ROAD[kind] : blend(ROAD[kind], VOID, UNOWNED_DIM);
                int grain = kind == BuildingVisual.GRAVEL ? (owned ? GRAVEL_GRAIN : blend(GRAVEL_GRAIN, VOID, UNOWNED_DIM)) : 0;
                // A head ends at its far edge, a tail at the near: one end's pass at a time (a one-step piece has both).
                if (head) avLine(in, img, w, i, px, code, pxl, pyl, half, Double.NEGATIVE_INFINITY, far, c, grain, 0, 0);
                if (tail) avLine(in, img, w, i, px, code, pxl, pyl, half, -far, Double.POSITIVE_INFINITY, c, grain, 0, 0);
            }
        }
    }

    /**
     * The origin's neighbourhood (the 3 x 3 plots about Input.avenueOx/avenueOy, the lattice node the four arms run out from):
     * a plot two arms share carries the first arm's mark only (DistrictPlan.avenue), so the marks draw one line through the
     * origin and the second line - the arms' other diagonal - comes from the geometry: each arm laid (its first step marked)
     * gets its ray from the origin's middle outward, over the nine plots, a half-width behind the origin so the four meet in
     * one crossing and a missing arm leaves a squared end. In the kind and width of the origin's own street. With a
     * Crossings given, in JUNCTION_BOX on the plots of a signalled crossing alone.
     */
    private static void avenueOrigin(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, Crossings cr) {
        int ox = in.avenueOx, oy = in.avenueOy, tile = TilePainter.TILE, w = tile * px;
        if (ox == Integer.MIN_VALUE || in.avenueAt(ox, oy) == 0) return;
        int arms = 0;
        for (int a = 0; a < 4; a++) if (in.avenueAt(ox + armDx(a), oy + armDy(a)) != 0) arms |= 1 << a;
        if (arms == 0) return;
        int sc = codeAt(in, ox, oy), kind = TilePainter.kindOf(sc);
        if (kind < BuildingVisual.GRAVEL || kind > TilePainter.TRACK) return;
        boolean box = cr != null;
        if (box && kind == TilePainter.TRACK) return;
        int sw = surfaceOf((sc >> TilePainter.S_WIDTH_SHIFT) & 3, px);
        double half = (kind == TilePainter.TRACK ? Math.max(2, px / 5) : sw) / 2.0;
        for (int y = Math.max(0, oy - 1); y <= Math.min(tile - 1, oy + 1); y++) {
            for (int x = Math.max(0, ox - 1); x <= Math.min(tile - 1, ox + 1); x++) {
                int i = y * tile + x;
                if (p.beneath[i] || p.use[i] == TilePainter.BUILDING || p.use[i] == TilePainter.RAIL) continue;
                if (p.use[i] == TilePainter.ROAD && !isStreet(p, i)) continue;
                if (in.terrain[i] == World.SALT || (World.bridgeable(in.terrain[i]) && p.use[i] != TilePainter.ROAD)) continue;
                if (box && !cr.signalAt(x, y)) continue;
                boolean owned = in.owned[i];
                int c = box ? JUNCTION_BOX : kind == TilePainter.TRACK ? TRACK_DASH : ROAD[kind];
                if (!owned) c = blend(c, VOID, UNOWNED_DIM);
                int grain = !box && kind == BuildingVisual.GRAVEL ? (owned ? GRAVEL_GRAIN : blend(GRAVEL_GRAIN, VOID, UNOWNED_DIM)) : 0;
                int dashStep = Math.max(2, px / 2), dashLen = Math.max(2, px / 3);
                for (int a = 0; a < 4; a++) {
                    if ((arms & 1 << a) == 0) continue;
                    avLine(in, img, w, i, px, a, (0.5 + ox - x) * px, (0.5 + oy - y) * px, half, -half, Double.POSITIVE_INFINITY, c, grain,
                            kind == TilePainter.TRACK ? dashStep : 0, dashLen);
                }
            }
        }
    }

    /** Avenue plot i over fresh water (the decks' pass of ways()): its deck - the band a pixel wider either side - and a bar of deck for each street that crosses it; the surface comes with avenuePlot(). */
    private static void avenueDeck(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px, x = i % tile, y = i / tile, x0 = x * px, y0 = y * px;
        int mark = in.avenue[i] & 0xff, code = (mark >> CityRuns.M_DIAG_SHIFT) & 3, dx = armDx(code);
        double pxl = (mark & CityRuns.M_CORNER) != 0 ? (0.5 - dx) * px : 0.5 * px, pyl = 0.5 * px;
        int t = surfaceOf(p.width[i], px);
        int deck = in.owned[i] ? DECK : blend(DECK, VOID, UNOWNED_DIM);
        avLine(in, img, w, i, px, code, pxl, pyl, t / 2.0 + 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, deck, 0, 0, 0);
        int bw = Math.min(px, t + 2), o = (px - bw) / 2;
        if (axisArm(in, p, x, y, 0) || axisArm(in, p, x, y, 2)) fillRect(img, w, x0 + o, y0, x0 + o + bw, y0 + px, deck);
        if (axisArm(in, p, x, y, 1) || axisArm(in, p, x, y, 3)) fillRect(img, w, x0, y0 + o, x0 + px, y0 + o + bw, deck);
    }

    /** The avenues' part of every signalled crossing's one box (CL4): the avenue plots, the outer plots and the origin's rays that lie in a junction of a signal, redrawn in JUNCTION_BOX. */
    private static void avenueBoxes(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, Crossings cr) {
        int tile = TilePainter.TILE;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (in.avenue[i] != 0 && cr.signalAt(i % tile, i / tile)) avenuePlot(in, p, i, px, img, true);
        }
        int[] notches = avenueNotches(in, p);
        if (notches != null) {
            for (int i = 0; i < TilePainter.PLOTS; i++) {
                if (notches[i] != 0 && cr.signalAt(i % tile, i / tile)) avenueNotch(in, p, i, notches[i], px, img, false, true);
            }
        }
        avenueOrigin(in, p, px, img, cr);
    }

    /* =====================================================================
       THE CURVES (batch CL4, slice D; the study spec-city-layout 5.2)
       ===================================================================== */

    /*
     * WHY. The district plans give two kinds of street a smooth line (DistrictPlan, CURVES; StreetCurves): a crescent, the cross
     * street of a long-block homes cell far from the founding site, a quarter of a circle between its rings, and the street a cut
     * lays along water, a path of plots from a dead end to the street it meets whose simplified line has its corners rounded.
     * Drawn plot by plot - a bar across each toward the street plots beside it - each is a staircase of crosses, as the avenues
     * were. From LINES_FROM px a plot each is one smooth band along its line (curveLine(): the arc sampled, the rounded polyline
     * StreetCurves.line() makes - the line the plan kept the ground free for), in its street's colour and width (SURFACE by the
     * plot's width, ROAD[kind], gravel's grain, a track's dashes counted along the line): the pixels within half the width of the
     * line, over the plots of the curve - its own, laid as streets, and the plots its band reaches that the plan keeps free of
     * buildings (Input.curve's CV_OWN and CV_BAND) - and nowhere else, so never over a building, a highway, the railway or a bridge's deck; the
     * band crosses the corner of a plot of water the simplified line cuts across (bandable()), as the line is, rather than leave a gap in the street.
     * The plots are verge where the band does not cover them (the own plots with the streets', the band's open ground in
     * ways()). An own plot the band draws (curveDrawn(): a street plot of the tile or its ring, not an end of the curve, not a
     * bridge, not beneath a highway, not the railway's) has no bars of its own: a street that meets the curve through it - one
     * plot beside it that is none of the curve's, with a street plot beyond (curveArm()) - is drawn as a bar across the plot, so
     * a long street crossing a crescent runs across the band and a street the curve meets reads as one network; the curve's
     * ends, the dead end's last plot and the street it meets (CV_END), are drawn as the streets they are (a street that comes from one side
     * alone, the dead end's, to the plot's middle: curveEndBars()), the band starting
     * and ending at their middles. A bridge's plots stay the staircase (FX5 kept a curve on a deck as its staircase) and the
     * band stops at their edges. Below LINES_FROM nothing changes: a plot is its colour.
     */

    /** Whether plot (x, y), of the tile or within REACH of it, is a street plot a curve draws in place of its bars (see THE CURVES). */
    private static boolean curveDrawn(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int m = in.curveAt(x, y);
        if ((m & TilePainter.Input.CV_OWN) == 0 || (m & TilePainter.Input.CV_END) != 0) return false;
        int tile = TilePainter.TILE;
        if (x >= 0 && y >= 0 && x < tile && y < tile) {
            int i = y * tile + x;
            return p.use[i] == TilePainter.ROAD && isStreet(p, i) && !p.beneath[i] && !p.bridge[i];
        }
        int code = codeAt(in, x, y), kind = TilePainter.kindOf(code);
        return kind >= BuildingVisual.GRAVEL && kind <= TilePainter.TRACK && (code & TilePainter.S_BRIDGE) == 0;
    }

    /**
     * Whether curve-drawn plot (x, y) has an ARM in direction d (north, east, south, west): a street running into it that is none of
     * the curve's - the plot beside it a street plot and no curve's own, and the plot beyond that another (a street that goes on
     * out, not a stub beside the curve). It is how the street a crescent crosses is told from the staircase's own plots.
     */
    private static boolean curveArm(TilePainter.Input in, TilePainter.Painted p, int x, int y, int d) {
        int nx = x + TilePainter.DX[d], ny = y + TilePainter.DY[d];
        if ((in.curveAt(nx, ny) & TilePainter.Input.CV_OWN) != 0 || !ringStreet(in, p, nx, ny)) return false;
        int bx = nx + TilePainter.DX[d], by = ny + TilePainter.DY[d];
        return (in.curveAt(bx, by) & TilePainter.Input.CV_OWN) == 0 && ringStreet(in, p, bx, by);
    }

    /**
     * Whether plot A (ax, ay) of the tile, beside the street plot B (bx, by) in direction d, has a bar toward it, where a curve is
     * about (streetAt()): two plots the curve draws have none between them; a plot the curve draws has one toward each arm; a plot
     * beside one the curve draws has one toward it when it is the curve's own, a bridge's or a plot beneath a highway (not an end:
     * the band starts at an end's middle), else (a street that is none of the curve's) when B has it as an arm.
     */
    private static boolean curveLink(TilePainter.Input in, TilePainter.Painted p, int ax, int ay, int bx, int by, int d) {
        boolean da = curveDrawn(in, p, ax, ay), db = curveDrawn(in, p, bx, by);
        if (!da && !db) return true;
        if (da && db) return false;
        if (da) return curveArm(in, p, ax, ay, d);
        int ma = in.curveAt(ax, ay);
        if ((ma & TilePainter.Input.CV_OWN) != 0) return (ma & TilePainter.Input.CV_END) == 0;
        return curveArm(in, p, bx, by, (d + 2) & 3);
    }

    /** The bars of curve-drawn plot i: the street that crosses it along each axis, across the plot in that street's kind and width (axisBar()). */
    private static void curveArms(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img) {
        int tile = TilePainter.TILE, x = i % tile, y = i / tile;
        int vCode = curveArmCode(in, p, x, y, 0, 2), hCode = curveArmCode(in, p, x, y, 1, 3);
        if (hCode != 0) axisBar(in, i, px, img, true, hCode, false);
        if (vCode != 0) axisBar(in, i, px, img, false, vCode, false);
    }

    /** The street code of the first of the two arms (directions d0 and d1) of curve-drawn plot (x, y); 0 for none. */
    private static int curveArmCode(TilePainter.Input in, TilePainter.Painted p, int x, int y, int d0, int d1) {
        int d = curveArm(in, p, x, y, d0) ? d0 : curveArm(in, p, x, y, d1) ? d1 : -1;
        return d < 0 ? 0 : codeAt(in, x + TilePainter.DX[d], y + TilePainter.DY[d]);
    }

    /**
     * The bars of an end of a curve (CV_END), where the street runs into it from one side alone on an axis - the dead end the street
     * along a cut leaves from, whose street stops there - so the bar is drawn from that side's edge to the plot's middle, where the
     * curve's band starts (no stub of the street beyond the join, whichever way the curve leaves). An axis with a street on both sides
     * is drawn whole as ever (a crescent's ring, the street a shore street meets). False, drawing nothing, where no axis is
     * one-sided or the plot is no surfaced street, so the plot is drawn as any other.
     */
    private static boolean curveEndBars(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int dashT, int dashStep, int dashLen) {
        int kind = p.road[i];
        if (p.use[i] != TilePainter.ROAD || p.beneath[i] || kind < BuildingVisual.GRAVEL || kind > TilePainter.TRACK) return false;
        boolean n = streetAt(in, p, i, 0), e = streetAt(in, p, i, 1), s = streetAt(in, p, i, 2), wst = streetAt(in, p, i, 3);
        boolean oneH = e != wst, oneV = n != s;
        if (!oneH && !oneV) return false;
        int tile = TilePainter.TILE, w = tile * px, x0 = (i % tile) * px, y0 = (i / tile) * px;
        // Along each axis the bar's span in pixels across the plot: whole, or from the side the street comes from to the middle.
        int hLo = e && !wst ? px / 2 : 0, hHi = wst && !e ? px / 2 : px, vLo = s && !n ? px / 2 : 0, vHi = n && !s ? px / 2 : px;
        boolean h = e || wst, v = n || s;
        if (kind == TilePainter.TRACK) {
            int c = in.owned[i] ? TRACK_DASH : blend(TRACK_DASH, VOID, UNOWNED_DIM), mid = (px - dashT) / 2;
            for (int a = 0; a < px; a += dashStep) {
                int b = Math.min(px, a + dashLen);
                if (h && b > hLo && a < hHi) fillRect(img, w, x0 + Math.max(a, hLo), y0 + mid, x0 + Math.min(b, hHi), y0 + mid + dashT, c);
                if (v && b > vLo && a < vHi) fillRect(img, w, x0 + mid, y0 + Math.max(a, vLo), x0 + mid + dashT, y0 + Math.min(b, vHi), c);
            }
            return true;
        }
        int t = surfaceOf(p.width[i], px), o = (px - t) / 2;
        int c = in.owned[i] ? ROAD[Math.min(kind, BuildingVisual.HIGHWAY)] : blend(ROAD[Math.min(kind, BuildingVisual.HIGHWAY)], VOID, UNOWNED_DIM);
        if (h) fillRect(img, w, x0 + hLo, y0 + o, x0 + hHi, y0 + o + t, c);
        if (v) fillRect(img, w, x0 + o, y0 + vLo, x0 + o + t, y0 + vHi, c);
        if (kind == BuildingVisual.GRAVEL) {
            int grain = in.owned[i] ? GRAVEL_GRAIN : blend(GRAVEL_GRAIN, VOID, UNOWNED_DIM);
            if (h) grain(in, img, w, x0 + hLo, y0 + o, x0 + hHi, y0 + o + t, grain);
            if (v) grain(in, img, w, x0 + o, y0 + vLo, x0 + o + t, y0 + vHi, grain);
        }
        return true;
    }

    /** Whether the band may be drawn on plot i of the tile: a street's plot (no bridge's, none beneath a highway, no highway's own) or open ground, the water the line cuts across a corner of among it - never a building, the railway, a bridge's deck. */
    private static boolean bandable(TilePainter.Painted p, int i) {
        if (p.beneath[i] || p.bridge[i]) return false;
        if (p.use[i] == TilePainter.ROAD) return isStreet(p, i);
        return p.use[i] == TilePainter.EMPTY;
    }

    /** What the band is drawn as on own plot (x, y) of the tile or its ring: (kind << 4 | width) + 1 of its street, 0 where it is none the band is drawn as (a highway's, one beneath a highway, a bridge's). */
    private static int curveLookOf(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int tile = TilePainter.TILE;
        if (x >= 0 && y >= 0 && x < tile && y < tile) {
            int j = y * tile + x;
            if (p.use[j] != TilePainter.ROAD || !isStreet(p, j) || p.beneath[j] || p.road[j] < BuildingVisual.GRAVEL || p.road[j] > TilePainter.TRACK) return 0;
            return 1 + (p.road[j] << 4 | p.width[j]);
        }
        int code = codeAt(in, x, y), kind = TilePainter.kindOf(code);
        if (kind < BuildingVisual.GRAVEL || kind > TilePainter.TRACK) return 0;
        return 1 + (kind << 4 | (kind == TilePainter.TRACK ? 0 : (code >> TilePainter.S_WIDTH_SHIFT) & 3));
    }

    /** What the band is drawn as on the plots of the tile: per plot, 0 for none or (kind << 4 | width) + 1 - an own street plot's own kind and width, a band plot's those of the nearest own street plot beside it (the four sides first, then the corners; an end's street, the arterial a crescent meets, last: it is no part of the curve's own street). */
    private static int[] curveLooks(TilePainter.Input in, TilePainter.Painted p) {
        int tile = TilePainter.TILE;
        int[] look = new int[TilePainter.PLOTS];
        for (int y = 0; y < tile; y++) {
            for (int x = 0; x < tile; x++) {
                int i = y * tile + x, m = in.curve[i] & 0xff;
                if (m == 0 || !bandable(p, i)) continue;
                if ((m & TilePainter.Input.CV_OWN) != 0 && p.use[i] == TilePainter.ROAD) {
                    look[i] = curveLookOf(in, p, x, y);
                    continue;
                }
                for (int pass = 0; pass < 2 && look[i] == 0; pass++) {
                    for (int s = 0; s < 8 && look[i] == 0; s++) {
                        int nx = x + (s < 4 ? TilePainter.DX[s] : (s & 1) == 0 ? -1 : 1), ny = y + (s < 4 ? TilePainter.DY[s] : (s & 2) == 0 ? -1 : 1);
                        int nm = in.curveAt(nx, ny);
                        if ((nm & TilePainter.Input.CV_OWN) == 0 || ((nm & TilePainter.Input.CV_END) != 0) != (pass == 1)) continue;
                        look[i] = curveLookOf(in, p, nx, ny);
                    }
                }
            }
        }
        return look;
    }

    /**
     * A curve's line, in the plots of the tile, from the numbers Input.curveGeom holds (n of them from `at`): {xs, ys}. A crescent's
     * arc (its circle: centre, radius, the angle to the start, the signed sweep) is sampled every TrackCurves.STEP of its length, its
     * ends the arc's; a shore's is StreetCurves.line() of its simplified vertices, the line the plan kept the ground free for.
     */
    static double[][] curveLine(int kind, double[] g, int at, int n) {
        if (kind == DistrictPlan.Curve.CRESCENT) {
            double cx = g[at], cy = g[at + 1], r = g[at + 2], a0 = g[at + 3], sweep = g[at + 4];
            int steps = Math.max(1, (int) Math.ceil(r * Math.abs(sweep) / TrackCurves.STEP - 1e-9));
            double[] xs = new double[steps + 1], ys = new double[steps + 1];
            for (int k = 0; k <= steps; k++) {
                double a = a0 + sweep * k / steps;
                xs[k] = cx + r * StrictMath.cos(a);
                ys[k] = cy + r * StrictMath.sin(a);
            }
            return new double[][] { xs, ys };
        }
        double[] vx = new double[n / 2], vy = new double[n / 2];
        for (int k = 0; k < vx.length; k++) {
            vx[k] = g[at + 2 * k];
            vy[k] = g[at + 2 * k + 1];
        }
        return StreetCurves.line(vx, vy);
    }

    /** Every curve of the tile drawn (THE CURVES): the band along each line over the plots it is drawn on. */
    private static void curves(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        if (in.curves == 0) return;
        int[] look = curveLooks(in, p);
        int at = 0;
        for (int k = 0; k < in.curves; k++) {
            int kind = (int) in.curveGeom[at], n = (int) in.curveGeom[at + 2];
            double[][] line = curveLine(kind, in.curveGeom, at + TilePainter.Input.CURVE_HEAD, n);
            at += TilePainter.Input.CURVE_HEAD + n;
            drawCurve(in, px, img, look, line[0], line[1]);
        }
    }

    /**
     * One curve's band: for each plot the curve is drawn on (look), the pixels of it whose centres lie within half the street's width
     * of the line (xs, ys, in plots) - a track's width, the dashes counted along the line from its start, so they run on across the
     * plots and the tiles - in the street's colour, gravel's grain over it, ground the city does not own dimmed.
     */
    private static void drawCurve(TilePainter.Input in, int px, int[] img, int[] look, double[] xs, double[] ys) {
        int tile = TilePainter.TILE, w = tile * px, m = xs.length;
        if (m < 2) return;
        double[] lx = new double[m], ly = new double[m], cum = new double[m], len = new double[m];
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (int s = 0; s < m; s++) {
            lx[s] = xs[s] * px;
            ly[s] = ys[s] * px;
            minX = Math.min(minX, lx[s]);
            maxX = Math.max(maxX, lx[s]);
            minY = Math.min(minY, ly[s]);
            maxY = Math.max(maxY, ly[s]);
            if (s > 0) {
                len[s - 1] = Math.hypot(lx[s] - lx[s - 1], ly[s] - ly[s - 1]);
                cum[s] = cum[s - 1] + len[s - 1];
            }
        }
        double reach = px / 2.0 + 1;
        int gx0 = (int) Math.max(0, Math.floor((minX - reach) / px)), gx1 = (int) Math.min(tile - 1, Math.floor((maxX + reach) / px));
        int gy0 = (int) Math.max(0, Math.floor((minY - reach) / px)), gy1 = (int) Math.min(tile - 1, Math.floor((maxY + reach) / px));
        int[] near = new int[m];
        int dashT = Math.max(2, px / 5), dashStep = Math.max(2, px / 2), dashLen = Math.max(2, px / 3);
        for (int gy = gy0; gy <= gy1; gy++) {
            for (int gx = gx0; gx <= gx1; gx++) {
                int i = gy * tile + gx, a = look[i];
                if (a == 0) continue;
                int kind = (a - 1) >> 4, width = (a - 1) & 15;
                boolean track = kind == TilePainter.TRACK;
                double half = track ? dashT / 2.0 : surfaceOf(width, px) / 2.0;
                // The segments of the line that come within `half` of the plot.
                double bx0 = gx * px - half, by0 = gy * px - half, bx1 = (gx + 1) * px + half, by1 = (gy + 1) * px + half;
                int nn = 0;
                for (int s = 0; s + 1 < m; s++) {
                    if (Math.max(lx[s], lx[s + 1]) < bx0 || Math.min(lx[s], lx[s + 1]) > bx1 || Math.max(ly[s], ly[s + 1]) < by0 || Math.min(ly[s], ly[s + 1]) > by1) continue;
                    near[nn++] = s;
                }
                if (nn == 0) continue;
                boolean owned = in.owned[i];
                int c = track ? TRACK_DASH : ROAD[Math.min(kind, BuildingVisual.HIGHWAY)];
                if (!owned) c = blend(c, VOID, UNOWNED_DIM);
                int grain = !track && kind == BuildingVisual.GRAVEL ? (owned ? GRAVEL_GRAIN : blend(GRAVEL_GRAIN, VOID, UNOWNED_DIM)) : 0;
                double half2 = half * half;
                for (int v = 0; v < px; v++) {
                    for (int u = 0; u < px; u++) {
                        double qx = gx * px + u + 0.5, qy = gy * px + v + 0.5, best = Double.MAX_VALUE, along = 0;
                        for (int q = 0; q < nn; q++) {
                            int s = near[q];
                            double ax = lx[s], ay = ly[s], dx = lx[s + 1] - ax, dy = ly[s + 1] - ay, l2 = dx * dx + dy * dy;
                            double t = l2 < 1e-18 ? 0 : Math.max(0, Math.min(1, ((qx - ax) * dx + (qy - ay) * dy) / l2));
                            double ex = ax + t * dx - qx, ey = ay + t * dy - qy, d2 = ex * ex + ey * ey;
                            if (d2 < best) { best = d2; along = cum[s] + t * len[s]; }
                        }
                        if (best >= half2) continue;
                        if (track && Math.floorMod((long) Math.floor(along), (long) dashStep) >= dashLen) continue;
                        int col = c;
                        if (grain != 0) {
                            int X = gx * px + u, Y = gy * px + v;
                            double h = World.unit(World.mix((in.tx * 8191L + X) * 0x9E3779B97F4A7C15L
                                    ^ (in.ty * 8191L + Y) * 0xC2B2AE3D27D4EB4FL ^ in.seed ^ 0x47524156L));
                            if (h < GRAVEL_GRAIN_SHARE) col = grain;
                        }
                        img[(gy * px + v) * w + gx * px + u] = col;
                    }
                }
            }
        }
    }

    /* =====================================================================
       THE WAYS AS LINES (0.7.88, batch RD2; from LINES_FROM px a plot)
       ===================================================================== */

    /**
     * The ways as lines (from LINES_FROM px a plot; spec 5): each street's
     * verge, its whole plot on dry ground; the decks of bridges; the track
     * (as rail since 0.8.1); each street's surface down its middle along
     * each way it runs - half the plot for a half-width street, all of it
     * for a full one - in its kind's colour, or a track's dashes; (0.8.1)
     * the railway's crossings (railCrossings()) and from JUNCTIONS_FROM the
     * signals, roundabouts and diamonds, and (FR7) the kerb corners (kerbs());
     * then the highways over all, cased, their open ends brought down to the
     * ground (highwayEnds()) and from JUNCTIONS_FROM their stacks and (FR7)
     * the reserves' loops (loops()); the railway bridging a highway; the
     * ramps below JUNCTIONS_FROM; and a dashed centre line from 6 px. A
     * 45-degree stretch is one carriageway along its line (FX3, Jerus's eye
     * check of 0.8.3): never a stack, its band filled in on the open plot
     * between two steps, and a dash on every step. Where it meets straight
     * highway, at either end, the carriageway turns along an arc (FX5 slice D;
     * Jerus, 2026-10-20: "once it finishes its still a tad wierd"): the
     * highway's bends (Input.bends of net HIGHWAY, TrackCurves' arcs of
     * HIGHWAY_RADIUS plots) are drawn by highwayBends() - casing and surface in
     * the passes of the highways, the centre line's dashes along the arc - in
     * place of the plots they replace (Input.curved: the corner's plot and the
     * steps and corners round it, each skipped by the passes, the stacks and
     * the centre line), so the straight's last plot no longer ends square on a
     * band that leaves at 45 degrees, leaving a stub on one side and a notch
     * on the other. A bridge's plot is never replaced (its deck is straight),
     * and a stretch that ends on its diagonal (an arm's last step) ends
     * squared across its heading at the step's far edge (diagEndOpen() fills
     * the open plots beside it), with no ramp's narrowing across the stair. The
     * avenues of the district plans (CL4, THE AVENUES above) are drawn in the
     * same passes: their plots' verges with the streets', their outer plots'
     * beside, their decks with the bridges', the bars of the streets that cross
     * them and their bands with the streets' surfaces, their boxes with the signals'. The
     * curves (CL4, slice D, THE CURVES above) likewise: the open ground their bands
     * reach has its verge with the streets', a plot they draw only the bars of the
     * streets that cross it, and their bands come after the streets' surfaces.
     *
     * VIADUCTS AND TUNNELS (ET4, 2026-10-25; spec-earth2-terrain 4.3, D10: "all rec"). The runs cross any relief, and the
     * model prices the works at the city's mix of ground (RunWorks); the map draws where the run actually lies
     * (TilePainter's loop of the runs). A run's plot on steep ground (Painted.bridge, beside the bridges over fresh water)
     * is a viaduct, drawn as a bridge is: the deck pass lays its deck, and the bends, which never replace a bridge's plot,
     * leave it straight. The deck under a highway is hidden by the highway's own width, which fills its plot, so a viaduct's
     * highway is cased in the deck's colour instead, a pixel at least (its casing otherwise is HIGHWAY_CASE, and from 6 px a plot alone).
     * A run's plot on very steep dry ground (Painted.tunnel) is a tunnel: the passes leave it as its ground, and tunnels()
     * draws a dashed line along the run in its own colour and a TUNNEL_PORTAL band across it where the run comes out. In the
     * blocks' views and below LINES_FROM a tunnel is its bare ground but for every other plot along the run, whose line
     * is drawn as before (tunnelDash()); no portal there.
     */
    private static void ways(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        // The avenues about the tile (CL4): their outer plots, found once; none, and every pass below is as it was.
        boolean avenues = in.avenueMarks != 0;
        int[] notches = avenues ? avenueNotches(in, p) : null;
        // The verges.
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!isStreet(p, i) || p.bridge[i]) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            fillRect(img, w, x0, y0, x0 + px, y0 + px, in.owned[i] ? VERGE : blend(VERGE, VOID, UNOWNED_DIM));
        }
        // ...and an avenue's outer plot, the open ground the band crosses, outside the band.
        if (notches != null) {
            for (int i = 0; i < TilePainter.PLOTS; i++) {
                if (notches[i] == 0 || !notchVerge(in, p, i)) continue;
                int x0 = (i % tile) * px, y0 = (i / tile) * px;
                fillRect(img, w, x0, y0, x0 + px, y0 + px, in.owned[i] ? VERGE : blend(VERGE, VOID, UNOWNED_DIM));
            }
        }
        // ...and the open ground a curve's band reaches (CL4, slice D), the plots the plan keeps free beside it, outside the band.
        boolean curves = in.curveMarks != 0;
        if (curves) {
            for (int i = 0; i < TilePainter.PLOTS; i++) {
                if (in.curve[i] == 0 || p.use[i] != TilePainter.EMPTY || in.terrain[i] == World.FRESH || World.salt(in.terrain[i])) continue;
                int x0 = (i % tile) * px, y0 = (i / tile) * px;
                fillRect(img, w, x0, y0, x0 + px, y0 + px, in.owned[i] ? VERGE : blend(VERGE, VOID, UNOWNED_DIM));
            }
        }
        // Bridge decks: a pixel wider than the surface (or the track, or a highway) either side, along it.
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!p.bridge[i] || (p.use[i] != TilePainter.ROAD && p.use[i] != TilePainter.RAIL)) continue;
            int across;
            boolean ns, ew;
            if (avenues && in.avenue[i] != 0 && isStreet(p, i)) {
                // An avenue's plot: its deck is the band's (a diagonal deck, not the staircase's bars), and a bar of deck for each street that crosses it.
                avenueDeck(in, p, i, px, img);
                continue;
            } else if (isHighway(p, i)) {
                across = crossSection(BuildingVisual.HIGHWAY, px)[0];
                ns = highwayAt(in, p, i, 0) || highwayAt(in, p, i, 2);
                ew = !ns;
            } else if (p.use[i] == TilePainter.RAIL && p.road[i] == 0) {
                across = crossSection(BuildingVisual.PAVED, px)[0];
                ns = railAt(in, p, i, 0) || railAt(in, p, i, 2);
                ew = !ns;
            } else {
                across = Math.max(1, (int) Math.round(SURFACE[Math.max(1, (int) p.width[i])] * px));
                ns = streetAt(in, p, i, 0) || streetAt(in, p, i, 2);
                ew = streetAt(in, p, i, 1) || streetAt(in, p, i, 3) || !ns;
            }
            int width = Math.min(px, across + 2), o = (px - width) / 2;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            int deck = in.owned[i] ? DECK : blend(DECK, VOID, UNOWNED_DIM);
            if (ns) fillRect(img, w, x0 + o, y0, x0 + o + width, y0 + px, deck);
            if (ew) fillRect(img, w, x0, y0 + o, x0 + px, y0 + o + width, deck);
        }
        // ...and an avenue's outer plot over water between two bridging steps.
        if (notches != null) {
            for (int i = 0; i < TilePainter.PLOTS; i++) if (notches[i] != 0) avenueNotch(in, p, i, notches[i], px, img, true, false);
        }
        track(in, p, px, img);
        sidingSheds(in, p, px, img);
        // The bends of the railway (FX5): the arcs where the plots they replace are not drawn as cells.
        bends(in, p, px, img, 0, 0, w, w, false);
        // The streets' surfaces, and the tracks' dashes.
        int dashT = Math.max(2, px / 5), dashStep = Math.max(2, px / 2), dashLen = Math.max(2, px / 3);
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!isStreet(p, i) || p.beneath[i]) continue;
            // An avenue's plot: the bars of the streets that cross it and the band along its line (CL4).
            if (avenues && in.avenue[i] != 0) {
                avenuePlot(in, p, i, px, img, false);
                continue;
            }
            // A plot a curve draws (CL4, slice D): only the bars of the streets that cross it; the band along its line comes below.
            if (curves && curveDrawn(in, p, i % tile, i / tile)) {
                curveArms(in, p, i, px, img);
                continue;
            }
            // An end of a curve with a street to one side alone on an axis (the dead end the street along a cut leaves from): the bar runs from that edge to the middle, where the band starts.
            if (curves && (in.curve[i] & (TilePainter.Input.CV_OWN | TilePainter.Input.CV_END)) == (TilePainter.Input.CV_OWN | TilePainter.Input.CV_END) && curveEndBars(in, p, i, px, img, dashT, dashStep, dashLen)) continue;
            int kind = p.road[i];
            boolean h = streetAt(in, p, i, 1) || streetAt(in, p, i, 3), v = streetAt(in, p, i, 0) || streetAt(in, p, i, 2);
            if (!h && !v) h = true;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            if (kind == TilePainter.TRACK) {
                int c = in.owned[i] ? TRACK_DASH : blend(TRACK_DASH, VOID, UNOWNED_DIM);
                int mid = (px - dashT) / 2;
                for (int s = 0; s < px; s += dashStep) {
                    int e = Math.min(px, s + dashLen);
                    if (h) fillRect(img, w, x0 + s, y0 + mid, x0 + e, y0 + mid + dashT, c);
                    if (v) fillRect(img, w, x0 + mid, y0 + s, x0 + mid + dashT, y0 + e, c);
                }
                continue;
            }
            if (kind < BuildingVisual.GRAVEL || kind > BuildingVisual.HIGHWAY) continue;
            int t = Math.max(1, (int) Math.round(SURFACE[Math.max(1, Math.min(2, (int) p.width[i]))] * px)), o = (px - t) / 2;
            int c = in.owned[i] ? ROAD[kind] : blend(ROAD[kind], VOID, UNOWNED_DIM);
            if (h) fillRect(img, w, x0, y0 + o, x0 + px, y0 + o + t, c);
            if (v) fillRect(img, w, x0 + o, y0, x0 + o + t, y0 + px, c);
            // ...and gravel's loose stone, a fine speckle by each pixel's own hash (FR7 2.3).
            if (kind == BuildingVisual.GRAVEL) {
                int grain = in.owned[i] ? GRAVEL_GRAIN : blend(GRAVEL_GRAIN, VOID, UNOWNED_DIM);
                if (h) grain(in, img, w, x0, y0 + o, x0 + px, y0 + o + t, grain);
                if (v) grain(in, img, w, x0 + o, y0, x0 + o + t, y0 + px, grain);
            }
        }
        // The curves' bands (CL4, slice D), over the bars of the plots they cross and the ends of the streets they start from.
        if (curves) curves(in, p, px, img);
        // The avenues' outer plots, the origin's other line and the squared ends of the pieces (CL4), then the railway's crossings.
        if (avenues) {
            if (notches != null) {
                for (int i = 0; i < TilePainter.PLOTS; i++) if (notches[i] != 0) avenueNotch(in, p, i, notches[i], px, img, false, false);
            }
            avenueOrigin(in, p, px, img, null);
            avenueEnds(in, p, px, img);
        }
        // The railway's crossings: rails through a level crossing with its marks, an arterial or boulevard over the main line as a bridge.
        railCrossings(in, p, px, img);
        // Junctions on the streets, before the highways are drawn over them.
        if (px >= JUNCTIONS_FROM) {
            Crossings crossings = Crossings.of(in, p);
            signals(in, p, px, img, crossings);
            roundabouts(in, p, px, img, crossings);
            diamonds(in, p, px, img);
            kerbs(in, p, px, img);
        }
        // The highways: cased, their middles and their arms to the highway beside them, across the tile's edge too.
        int[] cs = crossSection(BuildingVisual.HIGHWAY, px);
        int width = cs[0], cas = cs[1], o = (px - width) / 2;
        for (int pass = 0; pass < 2; pass++) {
            int in0 = pass == 0 ? 0 : cas;
            for (int i = 0; i < TilePainter.PLOTS; i++) {
                int x0 = (i % tile) * px, y0 = (i / tile) * px;
                // A viaduct (ET4: a highway's plot on steep ground, a bridge over dry land) is cased in the deck's colour, a pixel at
                // least even where the highway has none: it fills its plot's width, so its deck could not otherwise be seen.
                boolean viaduct = p.bridge[i] && !World.bridgeable(in.terrain[i]) && isHighway(p, i);
                int ci = viaduct ? Math.max(1, cas) : cas, inI = pass == 0 ? 0 : ci;
                if (pass == 0 && ci == 0) continue;
                int c = pass == 0 ? (viaduct ? DECK : HIGHWAY_CASE) : ROAD[BuildingVisual.HIGHWAY];
                if (!in.owned[i]) c = blend(c, VOID, UNOWNED_DIM);
                if (!isHighway(p, i)) {
                    // Open ground between two steps of a 45-degree stretch: the band the corners leave out (FX3).
                    diagOpen(in, p, i, px, (width - 2 * inI) / 2.0, c, img);
                    diagEndOpen(in, p, i, px, (width - 2 * inI) / 2.0, c, img);
                    continue;
                }
                // A tunnel's plot (ET4) is its ground: tunnels() draws its dashed line and portals.
                if (p.tunnel[i]) continue;
                // A plot a bend replaces is the arc's (FX5 slice D), drawn after the plots; a bridge's or a tunnel's plot never is.
                if (bendReplaces(in, p, i)) continue;
                if (diagonal(in, i)) {
                    diagBand(img, w, i, px, in.marks[i], (width - 2 * inI) / 2.0, c);
                    continue;
                }
                int a = o + inI, z = o + width - inI;
                fillRect(img, w, x0 + a, y0 + a, x0 + z, y0 + z, c);
                if (highwayAt(in, p, i, 0)) fillRect(img, w, x0 + a, y0, x0 + z, y0 + a, c);
                if (highwayAt(in, p, i, 2)) fillRect(img, w, x0 + a, y0 + z, x0 + z, y0 + px, c);
                if (highwayAt(in, p, i, 3)) fillRect(img, w, x0, y0 + a, x0 + a, y0 + z, c);
                if (highwayAt(in, p, i, 1)) fillRect(img, w, x0 + z, y0 + a, x0 + px, y0 + z, c);
            }
            // ...and the bends' arcs over them, the casing and then the surface (FX5 slice D).
            if (pass == 1 || cas > 0) highwayBends(in, p, px, img, (width - 2 * in0) / 2.0, pass == 0 ? HIGHWAY_CASE : ROAD[BuildingVisual.HIGHWAY]);
        }
        // A highway arm's end, from 4 px a plot as every way is (spec 2.7's ramp at an arm's end, which no street need pass
        // beneath), and a stack, drawn over the highways.
        highwayEnds(in, p, px, img);
        if (px >= JUNCTIONS_FROM) {
            stack(in, p, px, img);
            loops(in, p, px, img);
        }
        // The railway bridging a highway (0.7.89; spec 2.8): its deck and, drawn as rail (0.8.1), the track across the highway.
        int[] rs = crossSection(BuildingVisual.PAVED, px);
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (p.run[i] != TilePainter.RAIL_OVER || p.use[i] == TilePainter.BUILDING || p.tunnel[i]) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            boolean ns = railAt(in, p, i, 0) || railAt(in, p, i, 2);
            int deck = Math.min(px, rs[0] + 2), od = (px - deck) / 2;
            int dc = in.owned[i] ? DECK : blend(DECK, VOID, UNOWNED_DIM);
            if (ns) {
                fillRect(img, w, x0 + od, y0, x0 + od + deck, y0 + px, dc);
            } else {
                fillRect(img, w, x0, y0 + od, x0 + px, y0 + od + deck, dc);
            }
            drawTrackLook(in, p, i, px, img);
        }
        ramps(in, p, px, img);
        // The tunnels (ET4): their dashed lines and portals, over the ground the passes above left bare.
        tunnels(in, p, px, img);
        // The highways' dashed centre line, from 6 px a plot.
        if (px < 6) return;
        int dash = (int) Math.round(0.55 * px), mid = px / 2;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!isHighway(p, i) || p.tunnel[i] || p.run[i] == TilePainter.RAIL_OVER || bendReplaces(in, p, i)) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            int c = in.owned[i] ? CENTRE_LINE : blend(CENTRE_LINE, VOID, UNOWNED_DIM);
            if (diagonal(in, i)) {
                // A dash on every step, over the middle 55% of its diagonal, as the straight highway has one a plot (FX3: the old every-other-plot rule gave a stretch, whose steps all share a parity, all or none).
                if ((in.marks[i] & CityRuns.M_CORNER) == 0) {
                    int code = (in.marks[i] >> CityRuns.M_DIAG_SHIFT) & 3;
                    diagLine(img, w, i, px, code, 0.5 * px, 0.5 * px, 0.5, 0.275 * Math.sqrt(2) * px, c);
                }
                continue;
            }
            boolean ns = highwayAt(in, p, i, 0) || highwayAt(in, p, i, 2);
            if (ns) fillRect(img, w, x0 + mid, y0, x0 + mid + 1, y0 + dash, c);
            else fillRect(img, w, x0, y0 + mid, x0 + dash, y0 + mid + 1, c);
        }
        // ...along the bends' arcs (FX5 slice D).
        highwayBendDashes(in, p, px, img, dash);
    }

    /** A ramp's slip road's width, as a share of a plot: a gravel street's, 0.3 (ROAD_WIDTH). */
    public static final double RAMP_WIDTH = ROAD_WIDTH[BuildingVisual.GRAVEL];

    /**
     * The ramps (0.7.89; spec 2.7: "ramps where an arterial crosses, every
     * other one, and at an arm's end"): at a highway's plot the runs mark a
     * ramp, where a street passes beneath it, a slip road on each verge plot
     * at its corners - from the street beside that plot to the highway beside
     * it, across the plot's corner nearest the ramp - paved. A verge plot
     * across the tile's edge from its ramp is drawn by the tile it is on.
     * Since 0.8.1 below JUNCTIONS_FROM px a plot alone: from it the
     * diamond's slips are drawn instead (diamonds()).
     */
    private static void ramps(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        if (px >= JUNCTIONS_FROM) return; // The diamond draws these from 6 px a plot; below it, the corner strokes stay.
        int tile = TilePainter.TILE, w = tile * px;
        double half = Math.max(0.6, RAMP_WIDTH * px / 2);
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (p.use[i] != TilePainter.EMPTY && p.use[i] != TilePainter.FIELD) continue;
            int vx = i % tile, vy = i / tile;
            for (int oy = -1; oy <= 1; oy += 2) {
                for (int ox = -1; ox <= 1; ox += 2) {
                    int rx = vx + ox, ry = vy + oy;
                    // The ramp's plot: in the tile, or across one edge of it (not a corner).
                    boolean inX = rx >= 0 && rx < tile, inY = ry >= 0 && ry < tile;
                    if (!inX && !inY) continue;
                    byte mark, fx;
                    if (inX && inY) { mark = in.marks[ry * tile + rx]; fx = p.run[ry * tile + rx]; }
                    else {
                        int dir = !inY ? (ry < 0 ? 0 : 2) : (rx < 0 ? 3 : 1), along = dir == 0 || dir == 2 ? rx : ry;
                        mark = in.marksBeyond[dir * tile + along];
                        fx = in.fixedBeyond[dir * tile + along];
                    }
                    if ((mark & CityRuns.M_RAMP) == 0 || (fx != BuildingVisual.HIGHWAY && fx != TilePainter.RAIL_OVER)) continue;
                    // The highway runs along the ramp's row (its plot beside this one, across) or column; the street beneath runs the other way.
                    boolean rowHw = isRunHighway(in, p, vx, ry), colHw = isRunHighway(in, p, rx, vy);
                    if (rowHw == colHw) continue;
                    // The street: beside this plot toward the ramp's column (a highway along a row) or row.
                    boolean street = rowHw ? isStreetAt(in, p, rx, vy) : isStreetAt(in, p, vx, ry);
                    if (!street) continue;
                    // Across the corner nearest the ramp: from the middle of the side the street is on to the middle of the side the highway is on.
                    double ax, ay, bx, by;
                    if (rowHw) { ax = ox < 0 ? 0 : px; ay = px / 2.0; bx = px / 2.0; by = oy < 0 ? 0 : px; }
                    else { ax = px / 2.0; ay = oy < 0 ? 0 : px; bx = ox < 0 ? 0 : px; by = px / 2.0; }
                    int c = in.owned[i] ? ROAD[BuildingVisual.PAVED] : blend(ROAD[BuildingVisual.PAVED], VOID, UNOWNED_DIM);
                    segment(img, w, vx * px, vy * px, px, ax, ay, bx, by, half, c);
                }
            }
        }
    }

    /** Whether plot (x, y) - in the tile, or just across one of its edges - is a highway's of the runs. */
    private static boolean isRunHighway(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int tile = TilePainter.TILE;
        if (x >= 0 && y >= 0 && x < tile && y < tile) return runHighway(p, y * tile + x);
        if ((x < 0 || x >= tile) && (y < 0 || y >= tile)) return false;
        int dir = y < 0 ? 0 : y >= tile ? 2 : x < 0 ? 3 : 1, along = dir == 0 || dir == 2 ? x : y;
        byte f = in.fixedBeyond[dir * tile + along];
        return f == BuildingVisual.HIGHWAY || f == TilePainter.RAIL_OVER;
    }

    /** Whether plot (x, y) - in the tile, or just across one of its edges - is a street's. */
    private static boolean isStreetAt(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int tile = TilePainter.TILE;
        if (x >= 0 && y >= 0 && x < tile && y < tile) return isStreet(p, y * tile + x);
        if ((x < 0 || x >= tile) && (y < 0 || y >= tile)) return false;
        int dir = y < 0 ? 0 : y >= tile ? 2 : x < 0 ? 3 : 1, along = dir == 0 || dir == 2 ? x : y;
        return (in.beyond[dir * tile + along] & TilePainter.S_KIND) != DistrictPlan.NONE;
    }

    /** The pixels of a plot (its corner at x0, y0, px a side) within `half` of the segment from (ax, ay) to (bx, by), in the plot's pixels: colour c. */
    private static void segment(int[] img, int w, int x0, int y0, int px, double ax, double ay, double bx, double by, double half, int c) {
        double vx = bx - ax, vy = by - ay, len2 = vx * vx + vy * vy;
        for (int v = 0; v < px; v++) {
            for (int u = 0; u < px; u++) {
                double qx = u + 0.5 - ax, qy = v + 0.5 - ay;
                double t = len2 > 0 ? Math.max(0, Math.min(1, (qx * vx + qy * vy) / len2)) : 0;
                double ex = qx - t * vx, ey = qy - t * vy;
                if (ex * ex + ey * ey <= half * half) img[(y0 + v) * w + x0 + u] = c;
            }
        }
    }

    /* ----- junctions' helpers ----- */

    /**
     * The role of the street at plot (x, y) - ARTERIAL, BOULEVARD, ACCESS or
     * STREET: in the tile, from its painted plots; the plot just across one of
     * its edges, from Input.beyond; and (FX5) any other plot within
     * TilePainter.REACH of the tile, corners too, from Input.ring, so a
     * crossing on a tile's corner is read whole. 0 for none, a track, a street
     * beneath a highway, or a plot farther out.
     */
    private static byte streetRoleAt(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int tile = TilePainter.TILE, reach = TilePainter.REACH;
        if (x >= 0 && y >= 0 && x < tile && y < tile) {
            int i = y * tile + x;
            return isStreet(p, i) ? p.role[i] : 0;
        }
        int code;
        if (x >= 0 && x < tile && (y == -1 || y == tile)) code = in.beyond[(y < 0 ? 0 : 2) * tile + x];
        else if (y >= 0 && y < tile && (x == -1 || x == tile)) code = in.beyond[(x < 0 ? 3 : 1) * tile + y];
        else if (x >= -reach && y >= -reach && x < tile + reach && y < tile + reach) code = in.ring[TilePainter.ringIndex(x, y)];
        else return 0;
        return roleOfCode(code & 0xff);
    }

    /** The role of a street code (the S_ codes) as streetRoleAt() answers it: 0 for none or a street beneath a highway. */
    private static byte roleOfCode(int code) {
        int kind = code & TilePainter.S_KIND;
        if (kind == DistrictPlan.NONE || kind == DistrictPlan.UNDER) return 0;
        return TilePainter.roleOf(code);
    }

    /** Whether a street role is one a junction is drawn for. */
    private static boolean junctionRole(byte role) {
        return role == TilePainter.ARTERIAL || role == TilePainter.BOULEVARD || role == TilePainter.ACCESS;
    }

    /**
     * The crossings of a tile's streets (FX5; Jerus, 2026-10-20, "Mostly
     * signals"), read once for the tile over the plots about it - the tile and
     * TilePainter.REACH more on every side, which Input.ring gives - so a
     * crossing on a tile's corner is read whole by each of the four. A junction
     * plot is a plot of a junction role with a run of JUNCTION_RUN or more
     * junction streets through it on each axis (a road two plots wide is one
     * road, three too); a crossing is a 4-connected group of junction plots,
     * and its rectangle their bounding one. A crossing is a signal - one box
     * over the rectangle's streets, zebras and stop lines on the street plots
     * just outside it - unless ROUNDABOUT_ROADS or more roads leave it, when it
     * is a roundabout, one ring about the rectangle's middle.
     *
     * WHY. FX3 decided junctions plot by plot, so boulevards three plots wide
     * crossing drew nine rings, each centred on its own plot, with zebras
     * between them, and every boulevard crossing was a roundabout.
     */
    private static final class Crossings {
        /** The side of Input.ring and how far it reaches past the tile on every side, in plots: TilePainter.RING and TilePainter.REACH. */
        static final int N = TilePainter.RING, R = TilePainter.REACH;
        /** A tile with no junction street about it: no crossings. */
        static final Crossings NONE = new Crossings();

        /** The role of the street on each plot of the ring, by TilePainter.ringIndex(); null with no junction street about the tile. */
        final byte[] role;
        /** The crossing each plot belongs to, or -1; null with none. */
        final int[] group;
        /** The avenue's mark on each plot of the ring, by ringIndex() (CL4); null with no avenue about the tile. */
        byte[] av;
        /** The crossings: each one's rectangle (first and last column and row, in the tile's plots, which may lie off the tile), the roads that leave it, and whether it is a roundabout. */
        int groups;
        int[] x0 = new int[8], y0 = new int[8], x1 = new int[8], y1 = new int[8], roads = new int[8];
        boolean[] round = new boolean[8];

        private Crossings() {
            role = null;
            group = null;
        }

        private Crossings(byte[] role) {
            this.role = role;
            group = new int[N * N];
            Arrays.fill(group, -1);
        }

        /** The crossings of the tile in/p draw. */
        static Crossings of(TilePainter.Input in, TilePainter.Painted p) {
            byte[] role = new byte[N * N];
            byte[] avm = in.avenueMarks != 0 ? new byte[N * N] : null;
            boolean any = false;
            for (int y = -R; y < TilePainter.TILE + R; y++) {
                for (int x = -R; x < TilePainter.TILE + R; x++) {
                    byte r = streetRoleAt(in, p, x, y);
                    role[(y + R) * N + x + R] = r;
                    if (avm != null) avm[(y + R) * N + x + R] = (byte) in.avenueAt(x, y);
                    any |= junctionRole(r);
                }
            }
            if (!any) return NONE;
            // The runs of junction streets along each row and column, each plot with its run's length. An avenue's plots are no part of
            // a run (CL4): its staircase beside an arterial would lengthen the arterial's run into a crossing along its whole length; the
            // avenue's own junctions are found apart (avenueJunctions()).
            int[] hrun = new int[N * N], vrun = new int[N * N];
            for (int y = 0; y < N; y++) {
                for (int x = 0; x < N; ) {
                    if (!runRole(role, avm, y * N + x)) { x++; continue; }
                    int s = x;
                    while (x < N && runRole(role, avm, y * N + x)) x++;
                    for (int k = s; k < x; k++) hrun[y * N + k] = x - s;
                }
            }
            for (int x = 0; x < N; x++) {
                for (int y = 0; y < N; ) {
                    if (!runRole(role, avm, y * N + x)) { y++; continue; }
                    int s = y;
                    while (y < N && runRole(role, avm, y * N + x)) y++;
                    for (int k = s; k < y; k++) vrun[k * N + x] = y - s;
                }
            }
            // The junction plots: a plot of a junction role with a run of JUNCTION_RUN or more on each axis - and (CL4) an avenue's plot
            // a street of a junction role crosses, and the origin's 3 x 3 where the arms meet (avenueJunctions()).
            boolean[] jp = new boolean[N * N];
            for (int i = 0; i < N * N; i++) jp[i] = hrun[i] >= JUNCTION_RUN && vrun[i] >= JUNCTION_RUN;
            if (avm != null) avenueJunctions(in, role, avm, jp);
            Crossings c = new Crossings(role);
            c.av = avm;
            int[] stack = new int[N * N];
            for (int i = 0; i < N * N; i++) {
                if (c.group[i] >= 0 || !jp[i]) continue;
                int g = c.open(i % N - R, i / N - R);
                int top = 0;
                stack[top++] = i;
                c.group[i] = g;
                while (top > 0) {
                    int j = stack[--top], jx = j % N, jy = j / N;
                    c.grow(g, jx - R, jy - R);
                    for (int d = 0; d < 4; d++) {
                        int nx = jx + TilePainter.DX[d], ny = jy + TilePainter.DY[d];
                        if (nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
                        int k = ny * N + nx;
                        if (c.group[k] >= 0 || !jp[k]) continue;
                        c.group[k] = g;
                        stack[top++] = k;
                    }
                }
            }
            for (int g = 0; g < c.groups; g++) {
                c.roads[g] = c.roadsLeaving(g);
                c.round[g] = c.roads[g] >= ROUNDABOUT_ROADS;
            }
            return c;
        }

        /**
         * The junction plots the avenues make (CL4), into jp: an avenue's plot of a junction role (they are arterials) that a street of a
         * junction role crosses - one of its sides a plot of such a street that is no avenue's with a street plot beyond it (an axis
         * arm, axisArm()) - so the arterial and the avenue are one crossing, and a staircase plot alone is none; and the 3 x 3 plots
         * about the origin (Input.avenueOx), where three or more arms meet.
         */
        private static void avenueJunctions(TilePainter.Input in, byte[] role, byte[] avm, boolean[] jp) {
            for (int y = 0; y < N; y++) {
                for (int x = 0; x < N; x++) {
                    int k = y * N + x;
                    if (avm[k] == 0 || jp[k] || !junctionRole(role[k])) continue;
                    for (int d = 0; d < 4; d++) {
                        int nx = x + TilePainter.DX[d], ny = y + TilePainter.DY[d], bx = nx + TilePainter.DX[d], by = ny + TilePainter.DY[d];
                        if (bx < 0 || by < 0 || bx >= N || by >= N) continue;
                        int n = ny * N + nx, b = by * N + bx;
                        if (avm[n] == 0 && junctionRole(role[n]) && avm[b] == 0 && role[b] != 0) { jp[k] = true; break; }
                    }
                }
            }
            if (in.avenueOx == Integer.MIN_VALUE) return;
            int ox = in.avenueOx + R, oy = in.avenueOy + R;
            if (ox < 1 || oy < 1 || ox >= N - 1 || oy >= N - 1 || avm[oy * N + ox] == 0) return;
            int arms = 0;
            for (int a = 0; a < 4; a++) if (avm[(oy + armDy(a)) * N + ox + armDx(a)] != 0) arms++;
            if (arms < 3) return;
            for (int y = oy - 1; y <= oy + 1; y++) for (int x = ox - 1; x <= ox + 1; x++) jp[y * N + x] = true;
        }

        /** The avenue's mark on plot (x, y) of the ring; 0 for none or a plot off it. */
        int avAt(int x, int y) {
            if (av == null || x < -R || y < -R || x >= TilePainter.TILE + R || y >= TilePainter.TILE + R) return 0;
            return av[(y + R) * N + x + R] & 0xff;
        }

        /**
         * Whether plot (x, y), just outside a crossing's rectangle, is a road leaving it (CL4): a street plot that is no avenue's, or an
         * avenue's plot that is also a street the avenue crosses - one that goes on out, in direction d away from the rectangle, over a
         * street plot with another beyond it. The avenue's own branches are counted apart (branches()); the staircase beside a side road
         * is not that road.
         */
        private boolean streetOut(int x, int y, int d) {
            if (roleAt(x, y) == 0) return false;
            if (avAt(x, y) == 0) return true;
            int nx = x + TilePainter.DX[d], ny = y + TilePainter.DY[d], bx = nx + TilePainter.DX[d], by = ny + TilePainter.DY[d];
            return avAt(nx, ny) == 0 && roleAt(nx, ny) != 0 && avAt(bx, by) == 0 && roleAt(bx, by) != 0;
        }

        /**
         * The avenues' branches that leave crossing g (CL4): of the avenue's plots in its rectangle each plot the staircase runs on to
         * (a step's are the corner before it, at (x, y - dy), and after it, at (x + dx, y); a corner's the step before, at (x - dx, y),
         * and after, at (x, y + dy)) that lies outside it and is an avenue's too is one branch - so an arm that runs along a side
         * road is still its own road, and an avenue through a crossing is two roads.
         */
        private int branches(int g) {
            int n = 0;
            for (int y = y0[g]; y <= y1[g]; y++) {
                for (int x = x0[g]; x <= x1[g]; x++) {
                    int m = avAt(x, y);
                    if (m == 0) continue;
                    int code = (m >> CityRuns.M_DIAG_SHIFT) & 3, dx = armDx(code), dy = armDy(code);
                    boolean corner = (m & CityRuns.M_CORNER) != 0;
                    int ax = corner ? x - dx : x, ay = corner ? y : y - dy, bx = corner ? x : x + dx, by = corner ? y + dy : y;
                    if (!inRect(g, ax, ay) && avAt(ax, ay) != 0) n++;
                    if (!inRect(g, bx, by) && avAt(bx, by) != 0) n++;
                }
            }
            return n;
        }

        /** Whether plot k of the ring is a plot of a run of junction streets: a junction role, and no avenue's plot (avm null with no avenue about the tile). */
        private static boolean runRole(byte[] role, byte[] avm, int k) {
            return junctionRole(role[k]) && (avm == null || avm[k] == 0);
        }

        /** Starts a crossing at plot (x, y); its number. */
        private int open(int x, int y) {
            if (groups == x0.length) {
                int n = groups * 2;
                x0 = Arrays.copyOf(x0, n); y0 = Arrays.copyOf(y0, n); x1 = Arrays.copyOf(x1, n); y1 = Arrays.copyOf(y1, n);
                roads = Arrays.copyOf(roads, n); round = Arrays.copyOf(round, n);
            }
            x0[groups] = x1[groups] = x;
            y0[groups] = y1[groups] = y;
            return groups++;
        }

        /** Adds plot (x, y) to crossing g's rectangle. */
        private void grow(int g, int x, int y) {
            x0[g] = Math.min(x0[g], x); x1[g] = Math.max(x1[g], x);
            y0[g] = Math.min(y0[g], y); y1[g] = Math.max(y1[g], y);
        }

        /** The role of the street at plot (x, y) of the ring; 0 for none or for a plot off it. */
        byte roleAt(int x, int y) {
            if (x < -R || y < -R || x >= TilePainter.TILE + R || y >= TilePainter.TILE + R) return 0;
            return role[(y + R) * N + x + R];
        }

        /** The crossing plot (x, y) is a junction plot of, or -1. */
        int at(int x, int y) {
            if (group == null || x < -R || y < -R || x >= TilePainter.TILE + R || y >= TilePainter.TILE + R) return -1;
            return group[(y + R) * N + x + R];
        }

        /** Whether plot (x, y) is a junction plot of a signal. */
        boolean signalAt(int x, int y) {
            int g = at(x, y);
            return g >= 0 && !round[g];
        }

        /** Whether plot (x, y) lies in crossing g's rectangle. */
        boolean inRect(int g, int x, int y) {
            return x >= x0[g] && x <= x1[g] && y >= y0[g] && y <= y1[g];
        }

        /**
         * The roads that leave crossing g: on each of the rectangle's four sides
         * each maximal run of street plots just outside it is one road, and a
         * street plot diagonally off a corner is one more where the street goes
         * on out along the diagonal (the plot two off the corner a street too) -
         * a slanted avenue; a street that only touches a side road, running on
         * beside it, is part of it.
         */
        private int roadsLeaving(int g) {
            int n = 0;
            for (int side = 0; side < 4; side++) {
                boolean row = side < 2; // North and south are rows of plots along x, west and east columns along y.
                int from = row ? x0[g] : y0[g], to = row ? x1[g] : y1[g];
                int fixed = side == 0 ? y0[g] - 1 : side == 1 ? y1[g] + 1 : side == 2 ? x0[g] - 1 : x1[g] + 1;
                int away = side == 0 ? 0 : side == 1 ? 2 : side == 2 ? 3 : 1; // The direction out of the rectangle: north, south, west, east.
                boolean inRun = false;
                for (int k = from; k <= to; k++) {
                    boolean street = row ? streetOut(k, fixed, away) : streetOut(fixed, k, away);
                    if (street && !inRun) n++;
                    inRun = street;
                }
            }
            for (int sy = -1; sy <= 1; sy += 2) {
                for (int sx = -1; sx <= 1; sx += 2) {
                    int cx = sx < 0 ? x0[g] - 1 : x1[g] + 1, cy = sy < 0 ? y0[g] - 1 : y1[g] + 1;
                    if (roleAt(cx, cy) != 0 && roleAt(cx + sx, cy + sy) != 0 && avAt(cx, cy) == 0 && avAt(cx + sx, cy + sy) == 0) n++;
                }
            }
            // ...and the avenues' own branches (CL4), which the runs along the sides leave out: an avenue is two roads through a crossing.
            return av == null ? n : n + branches(g);
        }
    }

    /** A street's surface across its plot at px pixels a plot: {its width, its offset from the plot's edge}. */
    private static int[] surfaceAt(TilePainter.Painted p, int i, int px) {
        int t = Math.max(1, (int) Math.round(SURFACE[Math.max(1, Math.min(2, (int) p.width[i]))] * px));
        return new int[] { t, (px - t) / 2 };
    }

    /* ----- 1. signals: where arterials, boulevards and access roads cross (FX5: one box a crossing) ----- */

    private static void signals(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, Crossings cr) {
        int tile = TilePainter.TILE;
        // Each junction plot's surface is the dark box; a wide crossing's are one box.
        boolean avenues = in.avenueMarks != 0;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!cr.signalAt(i % tile, i / tile)) continue;
            if (avenues && in.avenue[i] != 0) continue; // An avenue's plot: its part of the box is the band's, below.
            int[] s = surfaceAt(p, i, px);
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            int c = in.owned[i] ? JUNCTION_BOX : blend(JUNCTION_BOX, VOID, UNOWNED_DIM);
            boolean h = streetAt(in, p, i, 1) || streetAt(in, p, i, 3);
            boolean v = streetAt(in, p, i, 0) || streetAt(in, p, i, 2);
            if (h) fillRect(img, tile * px, x0, y0 + s[1], x0 + px, y0 + s[1] + s[0], c);
            if (v) fillRect(img, tile * px, x0 + s[1], y0, x0 + s[1] + s[0], y0 + px, c);
        }
        // The avenues' part of the box (CL4): the band of every avenue plot, outer plot and ray of the origin that lies in a signalled crossing.
        if (avenues) avenueBoxes(in, p, px, img, cr);
        // Each arm, drawn by the plot it is on (so the tiles draw their parts of a junction across an edge): only the street plots just outside
        // a crossing's rectangle, facing it - none inside the crossing.
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!isStreet(p, i) || p.beneath[i] || p.road[i] == TilePainter.TRACK) continue;
            if (avenues && in.avenue[i] != 0) continue; // An avenue's arm has no zebra: its edge is slanted.
            if (in.curveMarks != 0 && curveDrawn(in, p, i % tile, i / tile)) continue; // ...nor has a curve's (CL4, slice D).
            int x = i % tile, y = i / tile;
            if (cr.at(x, y) >= 0) continue;
            for (int dir = 0; dir < 4; dir++) {
                int g = cr.at(x + TilePainter.DX[dir], y + TilePainter.DY[dir]);
                if (g < 0 || cr.round[g] || cr.inRect(g, x, y)) continue;
                drawSignalArm(in, p, i, px, img, dir);
            }
        }
    }

    /** A zebra crossing and its stop line on one arm of a signal, at the edge facing the junction. */
    private static void drawSignalArm(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int dir) {
        int tile = TilePainter.TILE, w = tile * px;
        int[] s = surfaceAt(p, i, px);
        int x0 = (i % tile) * px, y0 = (i / tile) * px;
        int depth = Math.max(1, (int) Math.round(ZEBRA_DEPTH * px));
        int zebra = in.owned[i] ? ZEBRA : blend(ZEBRA, VOID, UNOWNED_DIM);
        int half = s[0] / 2;
        if (dir == 0 || dir == 2) { // A north-south arm: stripes run along the arm, across its width.
            int y = dir == 0 ? 0 : px - depth;
            for (int dx = s[1]; dx < s[1] + s[0]; dx++) {
                if ((dx - s[1]) % 2 == 0) fillRect(img, w, x0 + dx, y0 + y, x0 + dx + 1, y0 + y + depth, zebra);
            }
            int stopY = dir == 0 ? depth : px - depth - 1;
            int startX = dir == 0 ? s[1] + s[0] - half : s[1];
            fillRect(img, w, x0 + startX, y0 + stopY, x0 + startX + half, y0 + stopY + 1, zebra);
        } else { // An east-west arm.
            int x = dir == 1 ? px - depth : 0;
            for (int dy = s[1]; dy < s[1] + s[0]; dy++) {
                if ((dy - s[1]) % 2 == 0) fillRect(img, w, x0 + x, y0 + dy, x0 + x + depth, y0 + dy + 1, zebra);
            }
            int stopX = dir == 1 ? px - depth - 1 : depth;
            int startY = dir == 1 ? s[1] + s[0] - half : s[1];
            fillRect(img, w, x0 + stopX, y0 + startY, x0 + stopX + 1, y0 + startY + half, zebra);
        }
    }

    /* ----- 2. roundabouts: where ROUNDABOUT_ROADS or more roads meet (FX5; before, wherever boulevards crossed) ----- */

    private static void roundabouts(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, Crossings cr) {
        int tile = TilePainter.TILE;
        for (int g = 0; g < cr.groups; g++) {
            if (!cr.round[g]) continue;
            // One ring about the rectangle's middle, a lane wider than the crossing's half: drawn on the plots it covers, from each tile they lie in.
            double span = Math.max(cr.x1[g] - cr.x0[g], cr.y1[g] - cr.y0[g]) / 2.0; // (max(w, h) - 1) / 2
            double outer = ROUNDABOUT_RADIUS + span, inner = ROUNDABOUT_ISLAND + span;
            double mx = (cr.x0[g] + cr.x1[g] + 1) / 2.0, my = (cr.y0[g] + cr.y1[g] + 1) / 2.0;
            int xa = Math.max(0, (int) Math.floor(mx - outer)), xb = Math.min(tile - 1, (int) Math.floor(mx + outer));
            int ya = Math.max(0, (int) Math.floor(my - outer)), yb = Math.min(tile - 1, (int) Math.floor(my + outer));
            for (int y = ya; y <= yb; y++) {
                for (int x = xa; x <= xb; x++) {
                    int i = y * tile + x;
                    if (roundaboutTarget(in, p, i)) drawRoundaboutOn(in, i, px, img, mx * px, my * px, outer * px, inner * px);
                }
            }
        }
    }

    /** Whether a roundabout's ring or island may be drawn on plot i: a street or empty ground, never a building, the track, a highway or water. */
    private static boolean roundaboutTarget(TilePainter.Input in, TilePainter.Painted p, int i) {
        if (p.use[i] == TilePainter.BUILDING || p.use[i] == TilePainter.RAIL || isHighway(p, i)) return false;
        if (World.salt(in.terrain[i]) || in.terrain[i] == World.FRESH) return false;
        return p.use[i] == TilePainter.EMPTY || p.use[i] == TilePainter.FIELD || isStreet(p, i);
    }

    /** The ring's and island's pixels of a roundabout centred on (centreX, centreY), in the tile's pixels, with an outer and an island radius in pixels, that fall on plot i. */
    private static void drawRoundaboutOn(TilePainter.Input in, int i, int px, int[] img, double centreX, double centreY, double outer, double inner) {
        int tile = TilePainter.TILE, w = tile * px;
        int x0 = (i % tile) * px, y0 = (i / tile) * px;
        int paved = in.owned[i] ? ROAD[BuildingVisual.PAVED] : blend(ROAD[BuildingVisual.PAVED], VOID, UNOWNED_DIM);
        int island = in.owned[i] ? ISLAND : blend(ISLAND, VOID, UNOWNED_DIM);
        for (int v = 0; v < px; v++) {
            for (int u = 0; u < px; u++) {
                double qx = x0 + u + 0.5 - centreX, qy = y0 + v + 0.5 - centreY;
                double d = Math.sqrt(qx * qx + qy * qy);
                if (d <= inner) img[(y0 + v) * w + x0 + u] = island;
                else if (d <= outer) img[(y0 + v) * w + x0 + u] = paved;
            }
        }
    }

    /* ----- 3. diamonds: a street passes beneath a highway ----- */

    private static void diamonds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE;
        // The ramps in the tile, and those just across its edges: an arterial lies on a cell's ring, the tile's first row or
        // column, so a diamond's slips on one side of its street are the next tile's to draw.
        for (int y = -1; y <= tile; y++) {
            for (int x = -1; x <= tile; x++) {
                boolean inside = x >= 0 && y >= 0 && x < tile && y < tile;
                if (!inside && (x < 0 || x >= tile) && (y < 0 || y >= tile)) continue;
                if (inside) {
                    int i = y * tile + x;
                    if (!isHighway(p, i) || (in.marks[i] & CityRuns.M_RAMP) == 0 || !p.beneath[i]) continue;
                } else {
                    int dir = y < 0 ? 0 : y >= tile ? 2 : x < 0 ? 3 : 1, along = dir == 0 || dir == 2 ? x : y;
                    if (in.fixedBeyond[dir * tile + along] != BuildingVisual.HIGHWAY || (in.marksBeyond[dir * tile + along] & CityRuns.M_RAMP) == 0
                            || !isStreetAt(in, p, x, y)) continue;
                }
                boolean rowHw = isRunHighway(in, p, x - 1, y) || isRunHighway(in, p, x + 1, y);
                boolean colHw = isRunHighway(in, p, x, y - 1) || isRunHighway(in, p, x, y + 1);
                if (rowHw == colHw) continue;
                boolean streetAcross = rowHw
                        ? isStreetAt(in, p, x, y - 1) || isStreetAt(in, p, x, y + 1)
                        : isStreetAt(in, p, x - 1, y) || isStreetAt(in, p, x + 1, y);
                if (!streetAcross) continue;
                for (int side = -1; side <= 1; side += 2) {
                    for (int dir = -1; dir <= 1; dir += 2) drawSlip(in, p, px, img, x, y, rowHw, side, dir);
                }
            }
        }
    }


    /** One diamond slip along the verge beside a highway, from the street out for SLIP_LENGTH plots. */
    private static void drawSlip(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int hx, int hy, boolean rowHw, int side, int dir) {
        int tile = TilePainter.TILE, w = tile * px;
        double half = Math.max(0.6, RAMP_WIDTH * px / 2.0);
        for (int k = 1; k <= (int) SLIP_LENGTH; k++) {
            int tx = rowHw ? hx + dir * k : hx + side;
            int ty = rowHw ? hy + side : hy + dir * k;
            if (tx < 0 || ty < 0 || tx >= tile || ty >= tile) return;
            int i = ty * tile + tx;
            if (p.use[i] != TilePainter.EMPTY && p.use[i] != TilePainter.FIELD) return;
            int x0 = tx * px, y0 = ty * px;
            int c = in.owned[i] ? ROAD[BuildingVisual.PAVED] : blend(ROAD[BuildingVisual.PAVED], VOID, UNOWNED_DIM);
            if (rowHw) {
                segment(img, w, x0, y0, px, 0, px / 2.0, px, px / 2.0, half, c);
                if (k == SLIP_LENGTH) segment(img, w, x0, y0, px, px / 2.0, px / 2.0, px / 2.0, side < 0 ? px : 0, half, c);
            } else {
                segment(img, w, x0, y0, px, px / 2.0, 0, px / 2.0, px, half, c);
                if (k == SLIP_LENGTH) segment(img, w, x0, y0, px, px / 2.0, px / 2.0, side < 0 ? px : 0, px / 2.0, half, c);
            }
        }
    }

    /* ----- 4. a highway arm's end ----- */

    /**
     * A highway arm's end (spec 2.7's ramp at an arm's end): a highway plot the runs mark as a ramp with no street beneath,
     * where the highway runs on exactly one of its four sides - the band narrows to a paved road's and a paved apron is
     * laid on the next plot. A ramp where the district plan laid no street beneath, in the middle of a straight highway
     * (the highway on two sides along it) or at a crossing, is not the highway's end and stays the plain highway (FX2,
     * Jerus's eye check of 0.8.2). A 45-degree stretch's last step is not drawn so (FX5 slice D): the narrowing was across the
     * staircase, a notch and a stub on its diagonal, and the band ends squared across its heading instead (diagEndOpen()).
     */
    private static void highwayEnds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, stride = tile * px;
        int[] fullCs = crossSection(BuildingVisual.HIGHWAY, px);
        int full = fullCs[0], cas = fullCs[1];
        int paved = crossSection(BuildingVisual.PAVED, px)[0];
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!isHighway(p, i) || p.tunnel[i] || (in.marks[i] & CityRuns.M_RAMP) == 0 || p.beneath[i] || diagonal(in, i)) continue;
            boolean n = runHighwayAt(in, p, i, 0), s = runHighwayAt(in, p, i, 2);
            boolean e = runHighwayAt(in, p, i, 1), west = runHighwayAt(in, p, i, 3);
            boolean ns = n || s;
            // An end has the highway on exactly one side: a lone plot (none), a bend or crossing, and the middle of a straight
            // highway (two sides, a ramp the plan laid no street beneath) are not ends and stay the plain highway.
            if ((n ? 1 : 0) + (s ? 1 : 0) + (e ? 1 : 0) + (west ? 1 : 0) != 1) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            int ground = groundColour(in, p, i);
            fillRect(img, stride, x0, y0, x0 + px, y0 + px, ground);
            int fill = in.owned[i] ? ROAD[BuildingVisual.HIGHWAY] : blend(ROAD[BuildingVisual.HIGHWAY], VOID, UNOWNED_DIM);
            int caseC = in.owned[i] ? HIGHWAY_CASE : blend(HIGHWAY_CASE, VOID, UNOWNED_DIM);
            int apron = in.owned[i] ? ROAD[BuildingVisual.PAVED] : blend(ROAD[BuildingVisual.PAVED], VOID, UNOWNED_DIM);
            boolean fromTop = n, fromLeft = west;
            if (ns) {
                for (int y = 0; y < px; y++) {
                    double f = (fromTop ? y : px - 1 - y) / (double) (px - 1);
                    int band = Math.max(paved, Math.min(px, (int) Math.round(full + (paved - full) * f)));
                    int left = (px - band) / 2;
                    fillRect(img, stride, x0 + left, y0 + y, x0 + left + band, y0 + y + 1, fill);
                    if (cas > 0 && band > 0) {
                        fillRect(img, stride, x0 + left, y0 + y, x0 + left + 1, y0 + y + 1, caseC);
                        fillRect(img, stride, x0 + left + band - 1, y0 + y, x0 + left + band, y0 + y + 1, caseC);
                    }
                }
                // Where the highway is no wider than a paved road (4 px a plot), the half toward the open end is the paved road's.
                if (full <= paved) {
                    int left = (px - paved) / 2;
                    fillRect(img, stride, x0 + left, fromTop ? y0 + px / 2 : y0, x0 + left + paved, fromTop ? y0 + px : y0 + px / 2, apron);
                }
                // The paved apron on the next plot, from the open end to its far edge.
                int ay = fromTop ? 1 : -1;
                drawHighwayApron(in, p, px, img, i % tile, i / tile + ay, true, paved, apron);
            } else {
                for (int x = 0; x < px; x++) {
                    double f = (fromLeft ? x : px - 1 - x) / (double) (px - 1);
                    int band = Math.max(paved, Math.min(px, (int) Math.round(full + (paved - full) * f)));
                    int top = (px - band) / 2;
                    fillRect(img, stride, x0 + x, y0 + top, x0 + x + 1, y0 + top + band, fill);
                    if (cas > 0 && band > 0) {
                        fillRect(img, stride, x0 + x, y0 + top, x0 + x + 1, y0 + top + 1, caseC);
                        fillRect(img, stride, x0 + x, y0 + top + band - 1, x0 + x + 1, y0 + top + band, caseC);
                    }
                }
                if (full <= paved) {
                    int top = (px - paved) / 2;
                    fillRect(img, stride, fromLeft ? x0 + px / 2 : x0, y0 + top, fromLeft ? x0 + px : x0 + px / 2, y0 + top + paved, apron);
                }
                int ax = fromLeft ? 1 : -1;
                drawHighwayApron(in, p, px, img, i % tile + ax, i / tile, true, paved, apron);
            }
        }
    }

    /** A paved apron the paved road's width across the plot just past a highway's open end, when that plot is empty ground. */
    private static void drawHighwayApron(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int tx, int ty, boolean vertical, int paved, int apron) {
        int tile = TilePainter.TILE, w = tile * px;
        if (tx < 0 || ty < 0 || tx >= tile || ty >= tile) return;
        int i = ty * tile + tx;
        if (p.use[i] != TilePainter.EMPTY && p.use[i] != TilePainter.FIELD) return;
        int x0 = tx * px, y0 = ty * px;
        double half = paved / 2.0;
        if (vertical) segment(img, w, x0, y0, px, px / 2.0, 0, px / 2.0, px, half, apron);
        else segment(img, w, x0, y0, px, 0, px / 2.0, px, px / 2.0, half, apron);
    }

    /* ----- 5. a stack: highways cross ----- */

    /**
     * A stack where two straight highways cross: a highway plot with another
     * beside it on both axes. A 45-degree stretch's plot is never a stack,
     * nor does it count as the highway beside a straight one: its steps and
     * corners have a neighbour on both axes by the staircase's shape, and were
     * taken for crossings - Jerus's "staircase of separate dark blocks" (FX3,
     * his eye check of 0.8.3).
     */
    private static void stack(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        int[] cs = crossSection(BuildingVisual.HIGHWAY, px);
        int width = cs[0], cas = cs[1], o = (px - width) / 2;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!isHighway(p, i) || p.tunnel[i] || diagonal(in, i) || in.curved[i]) continue;
            boolean ns = straightHighwayAt(in, p, i, 0) || straightHighwayAt(in, p, i, 2);
            boolean ew = straightHighwayAt(in, p, i, 1) || straightHighwayAt(in, p, i, 3);
            if (!ns || !ew) continue;
            int hx = i % tile, hy = i / tile;
            int x0 = hx * px, y0 = hy * px;
            int fill = in.owned[i] ? ROAD[BuildingVisual.HIGHWAY] : blend(ROAD[BuildingVisual.HIGHWAY], VOID, UNOWNED_DIM);
            int caseC = in.owned[i] ? HIGHWAY_CASE : blend(HIGHWAY_CASE, VOID, UNOWNED_DIM);
            // The row's deck over the column's, its casing a pixel either side across the lower.
            fillRect(img, w, x0 + o, y0 + o, x0 + o + width, y0 + o + width, fill);
            if (cas > 0) {
                fillRect(img, w, x0 + o, y0 + o, x0 + o + width, y0 + o + cas, caseC);
                fillRect(img, w, x0 + o, y0 + o + width - cas, x0 + o + width, y0 + o + width, caseC);
            }
            // The four corner slips, when the diagonal plot is open ground - and no reserve's loops replace them (batch FR7).
            if (reserveJunction(in, hx, hy)) continue;
            for (int dy = -1; dy <= 1; dy += 2) {
                for (int dx = -1; dx <= 1; dx += 2) drawStackSlip(in, p, px, img, hx + dx, hy + dy, dx, dy);
            }
        }
    }

    /** One corner slip of a stack, across the plot diagonal to the crossing. */
    private static void drawStackSlip(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int cx, int cy, int dx, int dy) {
        int tile = TilePainter.TILE, w = tile * px;
        if (cx < 0 || cy < 0 || cx >= tile || cy >= tile) return;
        int i = cy * tile + cx;
        if (p.use[i] != TilePainter.EMPTY && p.use[i] != TilePainter.FIELD) return;
        int x0 = cx * px, y0 = cy * px;
        double half = Math.max(0.6, RAMP_WIDTH * px / 2.0);
        int c = in.owned[i] ? ROAD[BuildingVisual.HIGHWAY] : blend(ROAD[BuildingVisual.HIGHWAY], VOID, UNOWNED_DIM);
        double ax = dx < 0 ? px : 0, ay = px / 2.0, bx = px / 2.0, by = dy < 0 ? px : 0;
        segment(img, w, x0, y0, px, ax, ay, bx, by, half, c);
    }

    /* ----- 6. interchanges' loops: a cloverleaf's and a trumpet's reserve (batch FR7; spec-freight 4.6, D19) ----- */

    /** A loop's radius, in plots, and how far from the crossing's middle along each axis a cloverleaf's loop is centred (batch FR7): 2.5 and 5.0, so a loop fills its quadrant of the 19-plot reserve. */
    public static final double LOOP_RADIUS = 2.5, LOOP_AT = 5.0;

    /** A trumpet's loop: its radius and how far out along its stub and across it its centre lies, in plots - 2.0, and 5.5 and 3.0, inside the 11-plot reserve. */
    public static final double TRUMPET_RADIUS = 2.0, TRUMPET_OUT = 5.5, TRUMPET_ACROSS = 3.0;

    /** A loop's and a slip's road across, as a share of a plot: 0.34, a pixel or two wider than a gravel street's, so it reads as a ramp at 8 px a plot and not as a street. */
    public static final double LOOP_WIDTH = 0.34;

    /** Where a slip leaves its highway, in plots from the junction's middle: just off the highway's band (0.45) and (along it) 9.0 out for a cloverleaf, 10.0 for a trumpet's stub. */
    public static final double SLIP_OFF = 0.45, SLIP_OUT = 9.0, TRUMPET_SLIP_OUT = 10.0;

    /**
     * The loops of every reserve that reaches the tile, from JUNCTIONS_FROM px
     * a plot: a cloverleaf's four loops, each with a slip to either highway; a
     * trumpet's one loop with a slip to its stub and to the highway it ends
     * at - all in the highway's colour and casing, only on the reserve's own
     * open plots (never over a highway, a street, a building or a field's
     * plot of the tile that is not the reserve's).
     */
    private static void loops(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px, ri = TilePainter.RESERVE_INTS;
        for (int r = 0; r < in.reserves; r++) {
            int o = r * ri, kind = in.reserve[o], stub = in.reserve[o + 1], jx = in.reserve[o + 2], jy = in.reserve[o + 3];
            int bx0 = in.reserve[o + 4], by0 = in.reserve[o + 5], bw = in.reserve[o + 6], bh = in.reserve[o + 7];
            double[][] circles = new double[kind == InterchangeGround.CLOVERLEAF ? 4 : 1][];
            double[][] slips = new double[kind == InterchangeGround.CLOVERLEAF ? 8 : 2][];
            if (kind == InterchangeGround.CLOVERLEAF) {
                int k = 0;
                for (int sy = -1; sy <= 1; sy += 2) {
                    for (int sx = -1; sx <= 1; sx += 2) {
                        circles[k] = new double[] { sx * LOOP_AT, sy * LOOP_AT, LOOP_RADIUS };
                        slips[2 * k] = spoke(sx * SLIP_OUT, sy * SLIP_OFF, sx * LOOP_AT, sy * LOOP_AT, LOOP_RADIUS);
                        slips[2 * k + 1] = spoke(sx * SLIP_OFF, sy * SLIP_OUT, sx * LOOP_AT, sy * LOOP_AT, LOOP_RADIUS);
                        k++;
                    }
                }
            } else {
                // The stub's way out is the axis a; across it, to its right, b.
                double ax = TilePainter.DX[stub], ay = TilePainter.DY[stub], bx = -ay, by = ax;
                double cx = TRUMPET_OUT * ax + TRUMPET_ACROSS * bx, cy = TRUMPET_OUT * ay + TRUMPET_ACROSS * by;
                circles[0] = new double[] { cx, cy, TRUMPET_RADIUS };
                slips[0] = spoke(TRUMPET_SLIP_OUT * ax + SLIP_OFF * bx, TRUMPET_SLIP_OUT * ay + SLIP_OFF * by, cx, cy, TRUMPET_RADIUS);
                slips[1] = spoke(SLIP_OFF * ax + (TRUMPET_ACROSS + TRUMPET_RADIUS) * bx, SLIP_OFF * ay + (TRUMPET_ACROSS + TRUMPET_RADIUS) * by, cx, cy, TRUMPET_RADIUS);
            }
            double half = LOOP_WIDTH / 2;
            int cas = px >= 6 ? 1 : 0;
            for (int y = Math.max(0, by0); y < Math.min(tile, by0 + bh); y++) {
                for (int x = Math.max(0, bx0); x < Math.min(tile, bx0 + bw); x++) {
                    int i = y * tile + x;
                    if (p.run[i] != 0 || (p.use[i] != TilePainter.EMPTY && p.use[i] != TilePainter.FIELD)) continue;
                    int fill = in.owned[i] ? ROAD[BuildingVisual.HIGHWAY] : blend(ROAD[BuildingVisual.HIGHWAY], VOID, UNOWNED_DIM);
                    int caseC = in.owned[i] ? HIGHWAY_CASE : blend(HIGHWAY_CASE, VOID, UNOWNED_DIM);
                    for (int dy = 0; dy < px; dy++) {
                        for (int dx = 0; dx < px; dx++) {
                            // The pixel's middle, in plots from the junction plot's middle.
                            double u = x + (dx + 0.5) / px - (jx + 0.5), v = y + (dy + 0.5) / px - (jy + 0.5);
                            double d = Double.MAX_VALUE;
                            for (double[] c : circles) d = Math.min(d, Math.abs(Math.hypot(u - c[0], v - c[1]) - c[2]));
                            for (double[] s : slips) d = Math.min(d, segmentDistance(u, v, s[0], s[1], s[2], s[3]));
                            if (d <= half) img[(y * px + dy) * w + x * px + dx] = fill;
                            else if (cas > 0 && d <= half + (double) cas / px) img[(y * px + dy) * w + x * px + dx] = caseC;
                        }
                    }
                }
            }
        }
    }

    /** A slip from (ax, ay) to the ring about (cx, cy) of radius r: the straight line toward the ring's middle, ending on it. */
    private static double[] spoke(double ax, double ay, double cx, double cy, double r) {
        double dx = cx - ax, dy = cy - ay, d = Math.hypot(dx, dy);
        return new double[] { ax, ay, cx - dx / d * r, cy - dy / d * r };
    }

    /** The distance from (x, y) to the segment from (ax, ay) to (bx, by). */
    private static double segmentDistance(double x, double y, double ax, double ay, double bx, double by) {
        double dx = bx - ax, dy = by - ay, l2 = dx * dx + dy * dy;
        double t = l2 == 0 ? 0 : Math.max(0, Math.min(1, ((x - ax) * dx + (y - ay) * dy) / l2));
        return Math.hypot(x - (ax + t * dx), y - (ay + t * dy));
    }

    /** Whether plot (x, y) of the tile is the junction of a reserve that reaches it: where loops replace a stack's corner slips. */
    private static boolean reserveJunction(TilePainter.Input in, int x, int y) {
        for (int r = 0; r < in.reserves; r++) {
            int o = r * TilePainter.RESERVE_INTS;
            if (in.reserve[o + 2] == x && in.reserve[o + 3] == y) return true;
        }
        return false;
    }

    /* ----- 7. kerb corners and estate T's (batch FR7; spec-freight 4.6's table) ----- */

    /** A kerb corner's radius, in plots: 0.25 (7.5 m), the corner of a + of two streets in a homes cell - and of a street's T on an arterial there. */
    public static final double KERB_RADIUS = 0.25;

    /** An estate T's corner radius, in plots: 0.5 (15 m) - wide, where trucks turn (spec-freight 4.6: "a T with wide radius corners"). */
    public static final double ESTATE_T_RADIUS = 0.5;

    /**
     * Kerb corners and estate T's, from JUNCTIONS_FROM px a plot, over the
     * streets' surfaces and their verges: at a + of two streets (a cell's
     * streets, role STREET, on all four sides) each of its four inner corners
     * is rounded by KERB_RADIUS; at a T where a street ends on an arterial,
     * boulevard or access road that runs on both sides of it, the two corners
     * beside the street are rounded by KERB_RADIUS in a homes cell and
     * ESTATE_T_RADIUS in an estate, which the painter reads from what stands
     * beside the T - a building of an estate's classes within two plots
     * (estateAt()). A rounding is the road's surface over the verge in the corner
     * between the two bars, outside the circle of the radius tangent to both
     * (fillet()), on street plots alone.
     */
    private static void kerbs(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!isStreet(p, i) || p.beneath[i] || p.bridge[i] || p.use[i] != TilePainter.ROAD) continue;
            if (in.avenueMarks != 0 && in.avenue[i] != 0) continue; // An avenue's plot is a band, no corner of two bars (CL4).
            int kind = p.road[i];
            if (kind != BuildingVisual.GRAVEL && kind != BuildingVisual.PAVED) continue;
            int x = i % tile, y = i / tile;
            if (in.curveMarks != 0 && curveDrawn(in, p, x, y)) continue; // ...nor a curve's (CL4, slice D).
            byte role = p.role[i];
            int t = Math.max(1, (int) Math.round(SURFACE[Math.max(1, Math.min(2, (int) p.width[i]))] * px)), o = (px - t) / 2;
            if (role == TilePainter.STREET) {
                boolean plus = true;
                for (int d = 0; d < 4; d++) {
                    plus &= streetRoleAt(in, p, x + TilePainter.DX[d], y + TilePainter.DY[d]) == TilePainter.STREET;
                    // A curve's plot beside it is no side street: its band meets this one's at its own angle (CL4, slice D).
                    if (in.curveMarks != 0) plus &= !curveDrawn(in, p, x + TilePainter.DX[d], y + TilePainter.DY[d]);
                }
                if (!plus) continue;
                int r = Math.max(1, (int) Math.round(KERB_RADIUS * px));
                for (int sy = -1; sy <= 1; sy += 2) {
                    for (int sx = -1; sx <= 1; sx += 2) fillet(in, p, px, img, x, y, sx < 0 ? o : o + t, sy < 0 ? o : o + t, sx, sy, r);
                }
            } else if (junctionRole(role)) {
                int stem = -1, stems = 0;
                for (int d = 0; d < 4; d++) {
                    if (streetRoleAt(in, p, x + TilePainter.DX[d], y + TilePainter.DY[d]) != TilePainter.STREET) continue;
                    stem = d;
                    stems++;
                }
                if (stems != 1) continue;
                int back = (stem + 2) % 4, left = (stem + 3) % 4, right = (stem + 1) % 4;
                // A stem a curve draws meets the arterial at the band's angle, not at a right angle (CL4, slice D).
                if (in.curveMarks != 0 && curveDrawn(in, p, x + TilePainter.DX[stem], y + TilePainter.DY[stem])) continue;
                if (streetRoleAt(in, p, x + TilePainter.DX[back], y + TilePainter.DY[back]) != 0) continue;
                if (!junctionRole(streetRoleAt(in, p, x + TilePainter.DX[left], y + TilePainter.DY[left]))
                        || !junctionRole(streetRoleAt(in, p, x + TilePainter.DX[right], y + TilePainter.DY[right]))) continue;
                // The stem's own bar, from the plot it stands on beside this one: its edges across, in this plot's pixels.
                int sx0 = x + TilePainter.DX[stem], sy0 = y + TilePainter.DY[stem];
                int stemT = t, stemO = o;
                if (sx0 >= 0 && sy0 >= 0 && sx0 < tile && sy0 < tile && isStreet(p, sy0 * tile + sx0)) {
                    stemT = Math.max(1, (int) Math.round(SURFACE[Math.max(1, Math.min(2, (int) p.width[sy0 * tile + sx0]))] * px));
                    stemO = (px - stemT) / 2;
                }
                int r = Math.max(1, (int) Math.round((estateAt(in, p, x, y) ? ESTATE_T_RADIUS : KERB_RADIUS) * px));
                boolean ns = stem == 0 || stem == 2;
                // The arterial's near edge: the side of its bar the stem is on.
                int near = stem == 0 || stem == 3 ? o : o + t;
                for (int side = -1; side <= 1; side += 2) {
                    int across = side < 0 ? stemO : stemO + stemT;
                    if (ns) fillet(in, p, px, img, x, y, across, near, side, stem == 0 ? -1 : 1, r);
                    else fillet(in, p, px, img, x, y, near, across, stem == 3 ? -1 : 1, side, r);
                }
            }
        }
    }

    /** Whether a building of an estate's classes - industry, farms, utilities, mines and wells, transport; a homes cell holds the rest - stands within two plots of plot (x, y): the painter's reading of an estate. */
    private static boolean estateAt(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int tile = TilePainter.TILE;
        for (int yy = Math.max(0, y - 2); yy <= Math.min(tile - 1, y + 2); yy++) {
            for (int xx = Math.max(0, x - 2); xx <= Math.min(tile - 1, x + 2); xx++) {
                int i = yy * tile + xx;
                if (p.use[i] != TilePainter.BUILDING) continue;
                int b = p.bld[i] - 1, t = b >= 0 && b < p.buildings ? p.btype[b] : -1;
                if (t < 0 || t >= in.types.length || in.types[t] == null) continue;
                int cls = in.types[t].cls();
                if (cls == BuildingVisual.INDUSTRY || cls == BuildingVisual.FARM || cls == BuildingVisual.UTILITY || cls == BuildingVisual.MINE || cls == BuildingVisual.TRANSPORT) return true;
            }
        }
        return false;
    }

    /**
     * One rounded corner: the corner point (cx, cy), in pixels from plot
     * (jx, jy)'s corner, where two bars meet; (sx, sy) the way out of it,
     * toward the verge. The pixels of the square of `r` a side out from it,
     * outside the circle of radius r centred r out along both axes, are set to
     * their plot's road colour - on a street plot of the tile alone, and only
     * where the verge is (nothing already drawn over it).
     */
    private static void fillet(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int jx, int jy, int cx, int cy, int sx, int sy, int r) {
        int tile = TilePainter.TILE, w = tile * px;
        double dcx = cx + sx * (double) r, dcy = cy + sy * (double) r;
        for (int dv = 0; dv < r; dv++) {
            for (int du = 0; du < r; du++) {
                double u = cx + sx * (du + 0.5), v = cy + sy * (dv + 0.5);
                if (Math.hypot(u - dcx, v - dcy) <= r) continue;
                int X = jx * px + (int) Math.floor(u), Y = jy * px + (int) Math.floor(v);
                if (X < 0 || Y < 0 || X >= w || Y >= w) continue;
                int i = (Y / px) * tile + X / px;
                if (!isStreet(p, i) || p.beneath[i] || p.use[i] != TilePainter.ROAD) continue;
                int kind = p.road[i];
                if (kind != BuildingVisual.GRAVEL && kind != BuildingVisual.PAVED) continue;
                int verge = in.owned[i] ? VERGE : blend(VERGE, VOID, UNOWNED_DIM);
                if (img[Y * w + X] != verge) continue;
                img[Y * w + X] = in.owned[i] ? ROAD[kind] : blend(ROAD[kind], VOID, UNOWNED_DIM);
            }
        }
    }

    /* ----- the track as rail, and its crossings (0.8.1, batch FR0; spec-freight 4.3) ----- */

    /**
     * The track (0.7.72; redrawn as rail, 0.8.1, batch FR0, spec-freight 4.3):
     * each plot's middle and its arms to the track beside it - past the tile's
     * edge too, where the line runs on - as a ballast bed, and from 4 px a
     * plot its rails as a centre line and a sleeper tick a plot, from 6 px two
     * rails on sleepers every quarter plot; double track where the main line
     * is marked. A road crossing it is drawn over the bed; from RAILS_FROM px
     * a plot the rails are then drawn through the street with its crossing
     * marks (railCrossings()). A plot a bend replaces (Input.curved, FX5) is
     * not drawn as its cell: bends() draws the arc over it.
     */
    private static void track(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (p.use[i] != TilePainter.RAIL || in.curved[i] || p.tunnel[i]) continue;
            drawTrackLook(in, p, i, px, img);
        }
    }

    /**
     * The tunnels (ET4, 2026-10-25; spec-earth2-terrain 4.3: "a run on very steep ground is drawn as a tunnel (portals, a
     * dashed line)"): each plot of a run that lies on very steep dry ground (Painted.tunnel) is its ground, the highway's
     * casing and surface and the track's bed and rails left out by the passes above, and here a dashed line along the run's
     * way in the run's own colour (the highway's surface, the track's steel; TUNNEL_DASH of every plot from its start, so
     * it runs on across plots) and, at each end of the tunnel, a portal: a TUNNEL_PORTAL band across the way on the last
     * plot's edge. An end is the edge toward a plot of the run that is no tunnel's, or where the run ends - a plot with one
     * neighbour ends opposite it, a lone plot at both ends of its east-west way. Across the tile's edge the neighbour's
     * relief is not in the tile's input, so the tunnel is taken to go on and the tile draws no portal there. The track over
     * a highway (RAIL_OVER) in a tunnel draws both lines; a 45-degree stretch only its dash, no portal.
     */
    private static void tunnels(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int highway = crossSection(BuildingVisual.HIGHWAY, px)[0], rail = crossSection(BuildingVisual.PAVED, px)[0];
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!p.tunnel[i] || (p.use[i] != TilePainter.ROAD && p.use[i] != TilePainter.RAIL)) continue;
            int arms = 0;
            if (isHighway(p, i)) {
                for (int d = 0; d < 4; d++) if (highwayAt(in, p, i, d)) arms |= 1 << d;
                tunnelWay(in, p, i, px, img, arms, ROAD[BuildingVisual.HIGHWAY], highway);
                if (p.run[i] != TilePainter.RAIL_OVER) continue;
                // The track bridging the highway runs across it (as the deck pass above reads it: the other axis).
                int across = (arms & 5) != 0 ? 10 : 5, track = 0;
                for (int d = 0; d < 4; d++) if (railAt(in, p, i, d)) track |= 1 << d;
                tunnelWay(in, p, i, px, img, track & across, STEEL, rail);
            } else if (p.use[i] == TilePainter.RAIL) {
                for (int d = 0; d < 4; d++) if (railAt(in, p, i, d)) arms |= 1 << d;
                tunnelWay(in, p, i, px, img, arms, STEEL, rail);
            }
        }
    }

    /** One run's way through tunnel plot i, `arms` its neighbours (bit d for direction d) and `cross` its cross-section in pixels: the dashed line and the portals of tunnels(). */
    private static void tunnelWay(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int arms, int colour, int cross) {
        int tile = TilePainter.TILE, w = tile * px, x0 = (i % tile) * px, y0 = (i / tile) * px;
        int line = in.owned[i] ? colour : blend(colour, VOID, UNOWNED_DIM);
        int portal = in.owned[i] ? TUNNEL_PORTAL : blend(TUNNEL_PORTAL, VOID, UNOWNED_DIM);
        int mid = px / 2, thick = Math.max(1, px / 8), lo = mid - thick / 2;
        int dash = Math.max(1, (int) Math.round(TUNNEL_DASH * px)), depth = Math.max(1, (int) Math.round(TUNNEL_PORTAL_DEPTH * px));
        if (diagonal(in, i)) {
            // A 45-degree stretch: the centre line's dash along its diagonal, as the highway's own is drawn (a staircase's corner none).
            if ((in.marks[i] & CityRuns.M_CORNER) == 0) {
                int code = (in.marks[i] >> CityRuns.M_DIAG_SHIFT) & 3;
                diagLine(img, w, i, px, code, 0.5 * px, 0.5 * px, 0.5, 0.275 * Math.sqrt(2) * px, line);
            }
            return;
        }
        // The ends: an arm's edge whose plot is no tunnel's (not known across the tile's edge); the opposite edge of a lone arm; both of a lone plot's.
        int ways = arms, ends = 0, n = Integer.bitCount(arms);
        for (int d = 0; d < 4; d++) {
            if ((arms >> d & 1) == 0) continue;
            int x = i % tile + TilePainter.DX[d], y = i / tile + TilePainter.DY[d];
            if (x >= 0 && y >= 0 && x < tile && y < tile && !p.tunnel[y * tile + x]) ends |= 1 << d;
        }
        if (n == 1) {
            ways |= 1 << ((Integer.numberOfTrailingZeros(arms) + 2) & 3);
            ends |= ways & ~arms;
        } else if (n == 0) {
            ways = ends = 10;
        }
        // The line: from the plot's middle to the edge of each way, a dash on the first TUNNEL_DASH of the plot along the run.
        for (int d = 0; d < 4; d++) {
            if ((ways >> d & 1) == 0) continue;
            boolean vertical = d == 0 || d == 2, first = d == 0 || d == 3;
            for (int u = first ? 0 : mid; u < (first ? mid : px); u++) {
                if (u >= dash) continue;
                if (vertical) fillRect(img, w, x0 + lo, y0 + u, x0 + lo + thick, y0 + u + 1, line);
                else fillRect(img, w, x0 + u, y0 + lo, x0 + u + 1, y0 + lo + thick, line);
            }
        }
        // The portals: across the way at the edge, a little wider than the run (its cross-section and a pixel either side, at most the plot).
        int across = Math.min(px, cross + 2), oa = (px - across) / 2;
        if ((ends & 1) != 0) fillRect(img, w, x0 + oa, y0, x0 + oa + across, y0 + depth, portal);
        if ((ends & 4) != 0) fillRect(img, w, x0 + oa, y0 + px - depth, x0 + oa + across, y0 + px, portal);
        if ((ends & 8) != 0) fillRect(img, w, x0, y0 + oa, x0 + depth, y0 + oa + across, portal);
        if ((ends & 2) != 0) fillRect(img, w, x0 + px - depth, y0 + oa, x0 + px, y0 + oa + across, portal);
    }

    /** A siding's loading shed, the share of a plot it fills (FR7 slice 3): 0.7, a small block at the siding's end beside the works. */
    public static final double SIDING_SHED = 0.7;

    /**
     * A siding's loading shed (FR7 slice 3): on the plot marked M_SHED, the
     * last of a siding beside its works' box, a small block in TRANSPORT's
     * colours over the track's end, edged from EDGE_FROM.
     */
    private static void sidingSheds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        if (px < LINES_FROM) return;
        int tile = TilePainter.TILE, w = tile * px;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if ((in.marks[i] & TilePainter.M_SHED) == 0 || p.use[i] != TilePainter.RAIL) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            int pad = (int) Math.round(px * (1 - SIDING_SHED) / 2.0), side = Math.max(1, px - 2 * pad);
            int fill = BuildingVisual.FILL[BuildingVisual.TRANSPORT], edge = BuildingVisual.EDGE[BuildingVisual.TRANSPORT];
            if (!in.owned[i]) { fill = blend(fill, VOID, UNOWNED_DIM); edge = blend(edge, VOID, UNOWNED_DIM); }
            fillRect(img, w, x0 + pad, y0 + pad, x0 + pad + side, y0 + pad + side, fill);
            if (px >= EDGE_FROM) {
                for (int k = 0; k < side; k++) {
                    img[(y0 + pad) * w + x0 + pad + k] = edge;
                    img[(y0 + pad + side - 1) * w + x0 + pad + k] = edge;
                    img[(y0 + pad + k) * w + x0 + pad] = edge;
                    img[(y0 + pad + k) * w + x0 + pad + side - 1] = edge;
                }
            }
        }
    }

    /** The ballast bed's width, whole pixels: BED, or BED_DOUBLE where the plot is marked double track. */
    private static int bedWidth(int px, boolean doubled) {
        return Math.min(px, Math.max(1, (int) Math.round((doubled ? BED_DOUBLE : BED) * px)));
    }

    /** The track's look on one plot that carries rail: its ballast bed, sleepers and rails (a 45-degree stretch's just a bed and one rail). */
    private static void drawTrackLook(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img) {
        // The track beside it, north, east, south and west, read once: bit d for direction d.
        drawTrackLook(in, p, i, px, img, armsOf(in, p, i));
    }

    /** ...with the track beside it given, bit d for direction d (a yard's head reads it from the runs: yardTrack()). */
    private static void drawTrackLook(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int arms) {
        boolean doubled = (in.marks[i] & TilePainter.M_DOUBLE) != 0;
        int bed = bedWidth(px, doubled);
        if (diagonal(in, i)) {
            int ballast = in.owned[i] ? BALLAST : blend(BALLAST, VOID, UNOWNED_DIM);
            diagBand(img, TilePainter.TILE * px, i, px, in.marks[i], bed / 2.0, ballast);
            int steel = in.owned[i] ? STEEL : blend(STEEL, VOID, UNOWNED_DIM);
            diagBand(img, TilePainter.TILE * px, i, px, in.marks[i], 0.5, steel);
            return;
        }
        boolean ns = (arms & 5) != 0;
        boolean ew = (arms & 10) != 0;
        if (!ns && !ew) ew = true;
        int a = (px - bed) / 2, z = a + bed;
        drawBed(in, p, i, px, img, a, z, arms);
        if (px >= RAILS_FROM) {
            int step = Math.max(2, (int) Math.round(SLEEPERS_EVERY * px));
            drawSleepers(in, p, i, px, img, a, z, ns, ew, step, arms);
        } else {
            drawSleeperTicks(in, p, i, px, img, a, z, ns, ew, arms);
        }
        drawRails(in, p, i, px, img, a, z, ns, ew, doubled, arms);
    }

    /** The track's ballast bed: the plot's middle and its arms to the track beside it, as the bed's whole width. */
    private static void drawBed(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int arms) {
        int tile = TilePainter.TILE, w = tile * px;
        int x0 = (i % tile) * px, y0 = (i / tile) * px;
        int c = in.owned[i] ? BALLAST : blend(BALLAST, VOID, UNOWNED_DIM);
        fillRect(img, w, x0 + a, y0 + a, x0 + z, y0 + z, c);
        if (((arms >> 0) & 1) != 0) fillRect(img, w, x0 + a, y0, x0 + z, y0 + a, c);
        if (((arms >> 2) & 1) != 0) fillRect(img, w, x0 + a, y0 + z, x0 + z, y0 + px, c);
        if (((arms >> 3) & 1) != 0) fillRect(img, w, x0, y0 + a, x0 + a, y0 + z, c);
        if (((arms >> 1) & 1) != 0) fillRect(img, w, x0 + z, y0 + a, x0 + px, y0 + z, c);
    }

    /** The sleepers, a pixel thick across the bed, every `step` pixels along the way the track runs. */
    private static void drawSleepers(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z,
                                     boolean ns, boolean ew, int step, int arms) {
        int tile = TilePainter.TILE, w = tile * px;
        int x0 = (i % tile) * px, y0 = (i / tile) * px;
        int c = in.owned[i] ? SLEEPER : blend(SLEEPER, VOID, UNOWNED_DIM);
        if (ew) {
            for (int sx = x0; sx < x0 + px; sx += step) {
                if (!(sx >= x0 + a && sx < x0 + z)
                        && !(((arms >> 3) & 1) != 0 && sx >= x0 && sx < x0 + a)
                        && !(((arms >> 1) & 1) != 0 && sx >= x0 + z && sx < x0 + px)) continue;
                fillRect(img, w, sx, y0 + a, sx + 1, y0 + z, c);
            }
        }
        if (ns) {
            for (int sy = y0; sy < y0 + px; sy += step) {
                if (!(sy >= y0 + a && sy < y0 + z)
                        && !(((arms >> 0) & 1) != 0 && sy >= y0 && sy < y0 + a)
                        && !(((arms >> 2) & 1) != 0 && sy >= y0 + z && sy < y0 + px)) continue;
                fillRect(img, w, x0 + a, sy, x0 + z, sy + 1, c);
            }
        }
    }

    /** Below RAILS_FROM px a plot, one sleeper tick across the bed at the start of each plot along the track. */
    private static void drawSleeperTicks(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z,
                                         boolean ns, boolean ew, int arms) {
        int tile = TilePainter.TILE, w = tile * px;
        int x0 = (i % tile) * px, y0 = (i / tile) * px;
        int c = in.owned[i] ? SLEEPER : blend(SLEEPER, VOID, UNOWNED_DIM);
        if (ew) fillRect(img, w, x0, y0 + a, x0 + 1, y0 + z, c);
        if (ns) fillRect(img, w, x0 + a, y0, x0 + z, y0 + 1, c);
    }

    /** The rails, a pixel each, along the way the track runs. */
    private static void drawRails(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z,
                                  boolean ns, boolean ew, boolean doubled, int arms) {
        if (px >= RAILS_FROM) drawRailsFull(in, p, i, px, img, a, z, ns, ew, doubled, arms);
        else drawRailsLow(in, p, i, px, img, a, z, ns, ew, doubled, arms);
    }

    /** From RAILS_FROM px a plot: two rails a track, double track's two tracks at the plot's middle +/- TRACKS_APART. */
    private static void drawRailsFull(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z,
                                      boolean ns, boolean ew, boolean doubled, int arms) {
        int mid = px / 2;
        int off = Math.max(1, (int) Math.round(GAUGE / 2.0 * px));
        int sep = doubled ? Math.max(1, (int) Math.round(TRACKS_APART / 2.0 * px)) : 0;
        int c = in.owned[i] ? STEEL : blend(STEEL, VOID, UNOWNED_DIM);
        if (ew) {
            if (doubled) {
                railH(in, p, i, px, img, a, z, mid - sep - off, c, arms);
                railH(in, p, i, px, img, a, z, mid - sep + off, c, arms);
                railH(in, p, i, px, img, a, z, mid + sep - off, c, arms);
                railH(in, p, i, px, img, a, z, mid + sep + off, c, arms);
            } else {
                railH(in, p, i, px, img, a, z, mid - off, c, arms);
                railH(in, p, i, px, img, a, z, mid + off, c, arms);
            }
        }
        if (ns) {
            if (doubled) {
                railV(in, p, i, px, img, a, z, mid - sep - off, c, arms);
                railV(in, p, i, px, img, a, z, mid - sep + off, c, arms);
                railV(in, p, i, px, img, a, z, mid + sep - off, c, arms);
                railV(in, p, i, px, img, a, z, mid + sep + off, c, arms);
            } else {
                railV(in, p, i, px, img, a, z, mid - off, c, arms);
                railV(in, p, i, px, img, a, z, mid + off, c, arms);
            }
        }
    }

    /** Below RAILS_FROM px a plot: one STEEL line down the track's middle, two at double track's tracks. */
    private static void drawRailsLow(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z,
                                     boolean ns, boolean ew, boolean doubled, int arms) {
        int mid = px / 2;
        int sep = doubled ? Math.max(1, (int) Math.round(TRACKS_APART / 2.0 * px)) : 0;
        int c = in.owned[i] ? STEEL : blend(STEEL, VOID, UNOWNED_DIM);
        if (ew) {
            if (doubled) {
                railH(in, p, i, px, img, a, z, mid - sep, c, arms);
                railH(in, p, i, px, img, a, z, mid + sep, c, arms);
            } else {
                railH(in, p, i, px, img, a, z, mid, c, arms);
            }
        }
        if (ns) {
            if (doubled) {
                railV(in, p, i, px, img, a, z, mid - sep, c, arms);
                railV(in, p, i, px, img, a, z, mid + sep, c, arms);
            } else {
                railV(in, p, i, px, img, a, z, mid, c, arms);
            }
        }
    }

    /** A rail a pixel wide along an east-west track at row `row`, its middle and its arms. */
    private static void railH(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int row, int c, int arms) {
        int tile = TilePainter.TILE, w = tile * px;
        int x0 = (i % tile) * px, y0 = (i / tile) * px;
        fillRect(img, w, x0 + a, y0 + row, x0 + z, y0 + row + 1, c);
        if (((arms >> 3) & 1) != 0) fillRect(img, w, x0, y0 + row, x0 + a, y0 + row + 1, c);
        if (((arms >> 1) & 1) != 0) fillRect(img, w, x0 + z, y0 + row, x0 + px, y0 + row + 1, c);
    }

    /** ...and a north-south track's, at column `col`. */
    private static void railV(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int col, int c, int arms) {
        int tile = TilePainter.TILE, w = tile * px;
        int x0 = (i % tile) * px, y0 = (i / tile) * px;
        fillRect(img, w, x0 + col, y0 + a, x0 + col + 1, y0 + z, c);
        if (((arms >> 0) & 1) != 0) fillRect(img, w, x0 + col, y0, x0 + col + 1, y0 + a, c);
        if (((arms >> 2) & 1) != 0) fillRect(img, w, x0 + col, y0 + z, x0 + col + 1, y0 + px, c);
    }

    /* ----- the curves (FX5) ----- */

    /**
     * The railway's bends (Input.bends, TrackCurves), drawn on the pixels of
     * the tile within the box [cx0, cx1) x [cy0, cy1): for each, its arc
     * between its tangent points and its two legs' straights from each
     * tangent point out to the far edge of the last plot it replaces
     * (TrackCurves.stub()), so nothing is left between the arc and the next
     * straight plot. From RAILS_FROM px a plot the ballast bed (bedWidth()'s),
     * two rails a pixel each at +/- GAUGE / 2 (four at TRACKS_APART on
     * double track) and sleepers radial every SLEEPERS_EVERY of the arc's
     * length; below it the bed, one steel line (two on double track) and a
     * sleeper tick a plot; ground the city does not own dimmed as the
     * straight track is. The straights' pixels are the straight track's own
     * (the bed, the rails and the sleepers are laid on the same rows and
     * columns), so a straight plot runs on into the arc without a step.
     * `crossingsOnly` draws the rails alone, on the replaced plots a street
     * crosses at level: railCrossings() draws the rails through the street,
     * and for a curved plot they are the arc's.
     */
    private static void bends(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int cx0, int cy0, int cx1, int cy1, boolean crossingsOnly) {
        if (in.bends == 0) return;
        for (int k = 0; k < in.bends; k++) {
            TrackCurves.Bend b = in.bendAt(k);
            if (b.net() != TrackCurves.RAILWAY) continue;
            drawBend(in, p, px, img, b, in.bendDoubled(k), cx0, cy0, cx1, cy1, crossingsOnly);
        }
    }

    /** The bend scaled to pixels a plot: the same arc, its corner, radius and tangent length times px. */
    private static TrackCurves.Bend inPixels(TrackCurves.Bend b, int px) {
        return TrackCurves.Bend.of(b.net(), b.x() * px, b.y() * px, b.ux(), b.uy(), b.vx(), b.vy(), b.r() * px, b.t() * px);
    }

    /**
     * How far point (x, y) lies from the path of bend s (in pixels): its arc,
     * the straight of `back1` before its start along the incoming leg, and
     * the straight of `back2` after its end along the outgoing one - the
     * least of those it lies beside, Double.POSITIVE_INFINITY for none. out
     * receives the distance along the path from the arc's start (negative on
     * the incoming straight) and 0, 1 or 2 for the arc or the straight.
     */
    private static double pathLateral(TrackCurves.Bend s, double back1, double back2, double x, double y, double[] out) {
        double best = Double.POSITIVE_INFINITY;
        double d = TrackCurves.arcDistance(s, x, y, out);
        if (d < best) { best = d; out[1] = 0; }
        double qx = x - s.startX(), qy = y - s.startY(), q = qx * s.ux() + qy * s.uy();
        if (q <= 1 && q >= -back1) {
            double lat = Math.abs(qx * s.uy() - qy * s.ux());
            if (lat < best) { best = lat; out[0] = q; out[1] = 1; }
        }
        qx = x - s.endX();
        qy = y - s.endY();
        q = qx * s.vx() + qy * s.vy();
        if (q >= -1 && q <= back2) {
            double lat = Math.abs(qx * s.vy() - qy * s.vx());
            if (lat < best) { best = lat; out[0] = s.length() + q; out[1] = 2; }
        }
        return best;
    }

    private static void drawBend(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, TrackCurves.Bend b, boolean doubled,
                                 int cx0, int cy0, int cx1, int cy1, boolean crossingsOnly) {
        int tile = TilePainter.TILE, w = tile * px;
        TrackCurves.Bend s = inPixels(b, px);
        double back = TrackCurves.stub(b) * px;
        boolean grid = TrackCurves.onGrid(b);
        // The pixels the path and its bed may touch.
        double reach = bedWidth(px, doubled) / 2.0 + 3;
        double minX = Math.min(Math.min(s.startX() - s.ux() * back, s.endX() + s.vx() * back), s.x()), maxX = Math.max(Math.max(s.startX() - s.ux() * back, s.endX() + s.vx() * back), s.x());
        double minY = Math.min(Math.min(s.startY() - s.uy() * back, s.endY() + s.vy() * back), s.y()), maxY = Math.max(Math.max(s.startY() - s.uy() * back, s.endY() + s.vy() * back), s.y());
        int x0 = Math.max(Math.max(0, cx0), (int) Math.floor(minX - reach)), x1 = Math.min(Math.min(w, cx1), (int) Math.ceil(maxX + reach));
        int y0 = Math.max(Math.max(0, cy0), (int) Math.floor(minY - reach)), y1 = Math.min(Math.min(w, cy1), (int) Math.ceil(maxY + reach));
        if (x0 >= x1 || y0 >= y1) return;
        int bed = bedWidth(px, doubled);
        int a = (px - bed) / 2;
        double bedShift = a + bed / 2.0 - px / 2.0;
        boolean full = px >= RAILS_FROM;
        int off = Math.max(1, (int) Math.round(GAUGE / 2.0 * px));
        int sep = doubled ? Math.max(1, (int) Math.round(TRACKS_APART / 2.0 * px)) : 0;
        // The rails' lanes: their distances from the path, one pair a track.
        double[] lanes = full
                ? (doubled ? new double[] { sep + off, Math.abs(sep - off) } : new double[] { off })
                : (doubled ? new double[] { sep } : new double[] { 0 });
        int step = Math.max(2, (int) Math.round(SLEEPERS_EVERY * px));
        boolean ew = Math.abs(b.ux()) > 0.5;
        boolean ewOut = Math.abs(b.vx()) > 0.5;
        double[] at = new double[2];
        for (int Y = y0; Y < y1; Y++) {
            for (int X = x0; X < x1; X++) {
                int i = (Y / px) * tile + X / px;
                // A tunnel's plot (ET4) is never drawn over: its dashed line is tunnels()'s.
                if (p.tunnel[i]) continue;
                // A plot of straight track draws itself: the arc of a bend on the plot grid and its straights lie on the plots they replace and on ground, not over a plot that runs on. (A 45-degree turn's arc is drawn over its staircase.)
                if (grid && !in.curved[i] && (p.use[i] == TilePainter.RAIL || p.run[i] == TilePainter.RAIL || p.run[i] == TilePainter.RAIL_OVER)) continue;
                if (crossingsOnly) {
                    // Only the rails, and only on a replaced plot a street crosses at level (railCrossings()).
                    int kind = p.road[i];
                    if (!in.curved[i] || p.use[i] != TilePainter.RAIL || (kind != BuildingVisual.GRAVEL && kind != BuildingVisual.PAVED)) continue;
                    boolean arterial = p.role[i] == TilePainter.ARTERIAL || p.role[i] == TilePainter.BOULEVARD || p.role[i] == TilePainter.ACCESS;
                    if ((in.marks[i] & TilePainter.M_MAIN) != 0 && arterial) continue;
                }
                double lat = pathLateral(s, back, back, X + 0.5 - 0.5, Y + 0.5 - 0.5, at);
                boolean rail = false;
                for (double lane : lanes) rail |= Math.abs(lat - lane) < 0.5;
                int c = 0;
                if (rail) {
                    c = STEEL;
                } else if (!crossingsOnly) {
                    double latBed = pathLateral(s, back, back, X + 0.5 - bedShift, Y + 0.5 - bedShift, at);
                    if (latBed >= bed / 2.0) continue;
                    c = BALLAST;
                    // A sleeper across the bed: radial on the arc, on the straights' own rows or columns where the leg is straight.
                    boolean sleeper;
                    if (at[1] == 0) sleeper = full ? Math.floorMod((long) Math.floor(at[0]), step) < 1 : Math.floorMod((long) Math.floor(at[0]), px) < 1;
                    else {
                        boolean legEw = at[1] == 1 ? ew : ewOut;
                        int local = legEw ? X % px : Y % px;
                        sleeper = full ? local % step == 0 : local == 0;
                    }
                    if (sleeper) c = SLEEPER;
                } else {
                    continue;
                }
                img[Y * w + X] = in.owned[i] ? c : blend(c, VOID, UNOWNED_DIM);
            }
        }
    }

    /**
     * The bends in the blocks' views (FX5): each bend's arc and straights in
     * RAIL_DARK at the width the blocks draw track (a plot, px pixels), in
     * place of the cells of the plots it replaces.
     */
    private static void blockBends(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        if (in.bends == 0) return;
        int tile = TilePainter.TILE, w = tile * px;
        double[] at = new double[2];
        for (int k = 0; k < in.bends; k++) {
            TrackCurves.Bend b = in.bendAt(k);
            if (b.net() != TrackCurves.RAILWAY) continue;
            TrackCurves.Bend s = inPixels(b, px);
            double back = TrackCurves.stub(b) * px, reach = px + 2;
            double ax = s.startX() - s.ux() * back, ay = s.startY() - s.uy() * back, bx = s.endX() + s.vx() * back, by = s.endY() + s.vy() * back;
            int x0 = Math.max(0, (int) Math.floor(Math.min(Math.min(ax, bx), s.x()) - reach)), x1 = Math.min(w, (int) Math.ceil(Math.max(Math.max(ax, bx), s.x()) + reach));
            int y0 = Math.max(0, (int) Math.floor(Math.min(Math.min(ay, by), s.y()) - reach)), y1 = Math.min(w, (int) Math.ceil(Math.max(Math.max(ay, by), s.y()) + reach));
            // A pixel a plot (the far view) is a line of pixels, so the arc is a little fatter than the cell to stay joined.
            double half = px / 2.0 + (px == 1 ? 0.35 : 0);
            for (int Y = y0; Y < y1; Y++) {
                for (int X = x0; X < x1; X++) {
                    if (pathLateral(s, back, back, X + 0.5, Y + 0.5, at) >= half) continue;
                    int i = (Y / px) * tile + X / px;
                    if (p.tunnel[i] && !tunnelDash(i)) continue;
                    img[Y * w + X] = in.owned[i] ? RAIL_DARK : blend(RAIL_DARK, VOID, UNOWNED_DIM);
                }
            }
        }
    }

    /** How far the straight legs of a highway bend are drawn past its tangent points, in plots: two - past the last plot the bend replaces (a plot and a half out), where the plots' own bands take over. */
    private static final double HIGHWAY_LEG = 2;

    /**
     * The highways' bends (Input.bends of net HIGHWAY, FX5 slice D), drawn as
     * the carriageway along the arc: the pixels within `half` pixels of each
     * bend's path in colour c, one pass of the highways' casing or surface
     * (ways() calls it with the casing's half-width, then the surface's). The
     * path is the arc between the tangent points and, on the plots the bend
     * replaces (Input.curved), its two legs' straights on through them - the
     * straight's row or column, the stretch's line - so nothing is left
     * between the arc and the plots that run on, which draw their own bands to
     * their edges, level with the legs'. The arc itself is drawn wherever it
     * passes, over the open plot it cuts across the corner of, and is laid
     * over a straight plot's band where it first leaves it; but never over a
     * bridge's plot.
     */
    private static void highwayBends(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, double half, int c) {
        if (in.bends == 0) return;
        int tile = TilePainter.TILE, w = tile * px;
        double[] at = new double[2];
        for (int k = 0; k < in.bends; k++) {
            TrackCurves.Bend b = in.bendAt(k);
            if (b.net() != TrackCurves.HIGHWAY) continue;
            TrackCurves.Bend s = inPixels(b, px);
            double back = HIGHWAY_LEG * px, reach = half + 2;
            double ax = s.startX() - s.ux() * back, ay = s.startY() - s.uy() * back, bx = s.endX() + s.vx() * back, by = s.endY() + s.vy() * back;
            int x0 = Math.max(0, (int) Math.floor(Math.min(Math.min(ax, bx), s.x()) - reach)), x1 = Math.min(w, (int) Math.ceil(Math.max(Math.max(ax, bx), s.x()) + reach));
            int y0 = Math.max(0, (int) Math.floor(Math.min(Math.min(ay, by), s.y()) - reach)), y1 = Math.min(w, (int) Math.ceil(Math.max(Math.max(ay, by), s.y()) + reach));
            for (int Y = y0; Y < y1; Y++) {
                for (int X = x0; X < x1; X++) {
                    int i = (Y / px) * tile + X / px;
                    if (p.bridge[i] || p.tunnel[i]) continue;
                    if (pathLateral(s, back, back, X + 0.5, Y + 0.5, at) >= half) continue;
                    // The legs' straights only on the plots the bend replaces: the others draw their own.
                    if (at[1] != 0 && !in.curved[i]) continue;
                    img[Y * w + X] = in.owned[i] ? c : blend(c, VOID, UNOWNED_DIM);
                }
            }
        }
    }

    /**
     * The highways' bends' centre line (FX5 slice D): a dash over the first 55%
     * of each plot's length of the path from the arc's start (`dash` pixels of
     * every px), a pixel wide on the row or column the straight's own dash
     * takes, along the arc and the legs' straights on the plots the bend
     * replaces. A plot's length of path always holds a dash, so the line
     * goes on through the joint.
     */
    private static void highwayBendDashes(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int dash) {
        if (in.bends == 0) return;
        int tile = TilePainter.TILE, w = tile * px;
        // The straight's dash is the pixel row or column px / 2: the pixel whose middle is half a pixel past the plot's middle where px is even.
        double shift = (px & 1) == 0 ? 0 : 0.5;
        double[] at = new double[2];
        for (int k = 0; k < in.bends; k++) {
            TrackCurves.Bend b = in.bendAt(k);
            if (b.net() != TrackCurves.HIGHWAY) continue;
            TrackCurves.Bend s = inPixels(b, px);
            double back = HIGHWAY_LEG * px, reach = 3;
            double ax = s.startX() - s.ux() * back, ay = s.startY() - s.uy() * back, bx = s.endX() + s.vx() * back, by = s.endY() + s.vy() * back;
            int x0 = Math.max(0, (int) Math.floor(Math.min(Math.min(ax, bx), s.x()) - reach)), x1 = Math.min(w, (int) Math.ceil(Math.max(Math.max(ax, bx), s.x()) + reach));
            int y0 = Math.max(0, (int) Math.floor(Math.min(Math.min(ay, by), s.y()) - reach)), y1 = Math.min(w, (int) Math.ceil(Math.max(Math.max(ay, by), s.y()) + reach));
            for (int Y = y0; Y < y1; Y++) {
                for (int X = x0; X < x1; X++) {
                    int i = (Y / px) * tile + X / px;
                    if (p.bridge[i] || p.tunnel[i] || p.run[i] == TilePainter.RAIL_OVER) continue;
                    if (pathLateral(s, back, back, X + shift, Y + shift, at) >= 0.5) continue;
                    if (at[1] != 0 && !in.curved[i]) continue;
                    if (Math.floorMod((long) Math.floor(at[0]), px) >= dash) continue;
                    img[Y * w + X] = in.owned[i] ? CENTRE_LINE : blend(CENTRE_LINE, VOID, UNOWNED_DIM);
                }
            }
        }
    }

    /**
     * One of a yard's ladders (FX5), from the fan's apex on the line to its
     * siding's start, as TrackCurves.ladder() lays it - an S tangent to the
     * line and to the siding, the points the trains' routes run along - drawn
     * as the siding's rails: two at +/- GAUGE / 2 from RAILS_FROM px a plot,
     * else one. The coordinates are the pixels' centres along the fan and
     * across it: the apex's along and the line's across, the siding's start
     * along and the siding's across.
     */
    private static void ladderRails(int[] img, int w, int px, boolean ew, double apexAlong, double lineAcross, double sidingAlong, double sidingAcross, int c) {
        double dir = Math.signum(sidingAlong - apexAlong);
        double[][] pts = TrackCurves.ladder(ew ? apexAlong / px : lineAcross / px, ew ? lineAcross / px : apexAlong / px,
                ew ? dir : 0, ew ? 0 : dir, ew ? 0 : 1, ew ? 1 : 0, Math.abs(sidingAlong - apexAlong) / px, (sidingAcross - lineAcross) / px, 0.5 / px);
        int off = px >= RAILS_FROM ? Math.max(1, (int) Math.round(GAUGE / 2.0 * px)) : 0;
        int n = pts[0].length;
        for (int k = 0; k < n; k++) {
            int a = Math.max(0, k - 1), z = Math.min(n - 1, k + 1);
            double tx = pts[0][z] - pts[0][a], ty = pts[1][z] - pts[1][a], len = Math.hypot(tx, ty);
            if (len < 1e-12) continue;
            double nx = -ty / len, ny = tx / len;
            double x = pts[0][k] * px, y = pts[1][k] * px;
            for (int side = off == 0 ? 0 : -1; side <= (off == 0 ? 0 : 1); side += 2) {
                int X = (int) Math.floor(x + nx * side * off), Y = (int) Math.floor(y + ny * side * off);
                if (X >= 0 && Y >= 0 && X < w && Y * w + X < img.length) img[Y * w + X] = c;
            }
        }
    }

    /**
     * The railway's crossings (0.8.1, spec-freight 4.3, D18): the street's
     * surface already lies over the bed; from RAILS_FROM px a plot, draw the
     * rails through a level crossing with its crossbucks and half barriers,
     * and an arterial or boulevard over the main line as a road bridge's deck
     * edges, with no rails or crossing marks on its surface.
     */
    private static void railCrossings(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        if (px < RAILS_FROM) return;
        int tile = TilePainter.TILE, w = tile * px;
        // A street over a plot a bend replaces: the arc's rails through it (FX5), before the marks are drawn over them.
        bends(in, p, px, img, 0, 0, w, w, true);
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (p.use[i] != TilePainter.RAIL) continue;
            int kind = p.road[i];
            if (kind != BuildingVisual.GRAVEL && kind != BuildingVisual.PAVED) continue;
            int x = i % tile, y = i / tile, x0 = x * px, y0 = y * px;
            boolean h = streetAt(in, p, i, 1) || streetAt(in, p, i, 3);
            boolean v = streetAt(in, p, i, 0) || streetAt(in, p, i, 2);
            if (!h && !v) h = true;
            int t = Math.max(1, (int) Math.round(SURFACE[Math.max(1, Math.min(2, (int) p.width[i]))] * px));
            int o = (px - t) / 2;
            boolean main = (in.marks[i] & TilePainter.M_MAIN) != 0;
            boolean arterial = p.role[i] == TilePainter.ARTERIAL || p.role[i] == TilePainter.BOULEVARD || p.role[i] == TilePainter.ACCESS;
            if (main && arterial) {
                // An avenue's plot has its deck along the band (avenuePlot()): no deck lines at the staircase's bars.
                if (in.avenueMarks != 0 && in.avenue[i] != 0) continue;
                // An arterial or boulevard over the main line: a road bridge, never a level crossing (D18).
                int deck = in.owned[i] ? DECK : blend(DECK, VOID, UNOWNED_DIM);
                if (h) {
                    if (o > 0) fillRect(img, w, x0, y0 + o - 1, x0 + px, y0 + o, deck);
                    if (o + t < px) fillRect(img, w, x0, y0 + o + t, x0 + px, y0 + o + t + 1, deck);
                }
                if (v) {
                    if (o > 0) fillRect(img, w, x0 + o - 1, y0, x0 + o, y0 + px, deck);
                    if (o + t < px) fillRect(img, w, x0 + o + t, y0, x0 + o + t + 1, y0 + px, deck);
                }
                continue;
            }
            boolean doubled = (in.marks[i] & TilePainter.M_DOUBLE) != 0;
            if (in.curved[i]) {
                // The rails through a curved plot were drawn with its arc, above.
            } else if (diagonal(in, i)) {
                int steel = in.owned[i] ? STEEL : blend(STEEL, VOID, UNOWNED_DIM);
                diagBand(img, w, i, px, in.marks[i], 0.5, steel);
            } else {
                int bed = bedWidth(px, doubled);
                int a = (px - bed) / 2, z = a + bed;
                int arms = armsOf(in, p, i);
                boolean ns = (arms & 5) != 0;
                boolean ew = (arms & 10) != 0;
                if (!ns && !ew) ew = true;
                drawRails(in, p, i, px, img, a, z, ns, ew, doubled, arms);
            }
            drawCrossbucks(px, img, w, x0, y0);
            drawBarriers(in, p, i, px, img, w, x0, y0, t, o, h, v);
        }
    }

    /** A level crossing's two crossbucks: a white X in the plot's north-west and south-east corners. */
    private static void drawCrossbucks(int px, int[] img, int w, int x0, int y0) {
        int s = Math.max(3, (int) Math.round(3 * px / 8.0));
        drawX(img, w, x0, y0, s);
        drawX(img, w, x0 + px - s, y0 + px - s, s);
    }

    /** An X of side `s` in CROSSBUCK, its corner at (x0, y0). */
    private static void drawX(int[] img, int w, int x0, int y0, int s) {
        for (int k = 0; k < s; k++) {
            img[(y0 + k) * w + x0 + k] = CROSSBUCK;
            img[(y0 + k) * w + x0 + s - 1 - k] = CROSSBUCK;
        }
    }

    /** A level crossing's half barriers: a bar across the lane coming in at each edge where the street enters the plot. */
    private static void drawBarriers(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int w,
                                     int x0, int y0, int t, int o, boolean h, boolean v) {
        int half = t / 2;
        if (half <= 0) return;
        if (h) {
            if (streetAt(in, p, i, 3)) {
                int y = y0 + o + (t - half);
                for (int k = 0; k < half; k++) img[(y + k) * w + x0] = (k & 1) == 0 ? BARRIER : CROSSBUCK;
            }
            if (streetAt(in, p, i, 1)) {
                int y = y0 + o;
                for (int k = 0; k < half; k++) img[(y + k) * w + x0 + px - 1] = (k & 1) == 0 ? BARRIER : CROSSBUCK;
            }
        }
        if (v) {
            if (streetAt(in, p, i, 0)) {
                int x = x0 + o;
                for (int k = 0; k < half; k++) img[y0 * w + x + k] = (k & 1) == 0 ? BARRIER : CROSSBUCK;
            }
            if (streetAt(in, p, i, 2)) {
                int x = x0 + o + (t - half);
                for (int k = 0; k < half; k++) img[(y0 + px - 1) * w + x + k] = (k & 1) == 0 ? BARRIER : CROSSBUCK;
            }
        }
    }

    /* =====================================================================
       A RECTANGLE OF PIXELS, AND THE PLOTS' COLOURS
       ===================================================================== */

    /** Speckles GRAVEL_GRAIN_SHARE of the box's pixels with c, each by a hash of its pixel in the tile and the tile's place, so the same street speckles the same at every paint. */
    private static void grain(TilePainter.Input in, int[] img, int w, int x0, int y0, int x1, int y1, int c) {
        for (int y = y0; y < y1; y++) {
            int o = y * w;
            for (int x = x0; x < x1; x++) {
                double u = World.unit(World.mix((in.tx * 8191L + x) * 0x9E3779B97F4A7C15L
                        ^ (in.ty * 8191L + y) * 0xC2B2AE3D27D4EB4FL ^ in.seed ^ 0x47524156L));
                if (u < GRAVEL_GRAIN_SHARE) img[o + x] = c;
            }
        }
    }

    private static void fillRect(int[] img, int w, int x0, int y0, int x1, int y1, int c) {
        for (int y = y0; y < y1; y++) {
            int o = y * w;
            for (int x = x0; x < x1; x++) img[o + x] = c;
        }
    }

    /** A plot's ground: its terrain - forest under a building cleared (0.7.88; spec 2.6) - its site's tint or grey, dimmed when unowned: what a road is drawn on from LINES_FROM px. */
    static int groundColour(TilePainter.Input in, TilePainter.Painted p, int i) {
        byte ground = in.terrain[i];
        if (ground == World.FOREST && p.use[i] == TilePainter.BUILDING) ground = World.GRASS;
        int c = colour(ground, in.relief[i]);
        int s = p.site[i] - 1;
        boolean wet = World.salt(ground) || ground == World.FRESH;
        if (s >= 0 && (!wet || in.siteKind[s] == Resource.OIL.ordinal())) c = siteTint(in, i, s, c);
        if (!in.owned[i]) c = blend(c, VOID, UNOWNED_DIM);
        return c;
    }

    /** A site's plot over its ground: its resource's colour, speckled, half grey while its holding is worked, grey worked out. */
    static int siteTint(TilePainter.Input in, int i, int s, int c) {
        int state = in.siteState[s];
        int colour = 0xff000000 | Resource.values()[in.siteKind[s]].colour();
        double speckle = World.unit(World.mix((in.tx * TilePainter.TILE + i % TilePainter.TILE) * 0x9E3779B97F4A7C15L
                ^ (in.ty * TilePainter.TILE + i / TilePainter.TILE) * 0xC2B2AE3D27D4EB4FL ^ in.seed));
        int tinted = blend(c, colour, speckle < SPECKLE_SHARE ? SITE_SPECKLE : SITE_TINT);
        return state == TilePainter.WORKED_OUT ? WORKED_GREY
                : state == TilePainter.WORKING ? blend(tinted, WORKED_GREY, 0.5) : tinted;
    }

    /** A road plot's colour below LINES_FROM px a plot, where it fills its plot, by kind: gravel's and the highway's own, a paved road's casing - its own grey is the grass's brightness, and a pixel of it vanishes among the homes. */
    public static final int[] ROAD_SMALL = { 0, 0xffbdb8ae, 0xff6a7077, 0xff3b4048 };

    /** A plot's colour before its building: its ground, its site's tint or grey, its road's colour - a track's brown (0.7.88) - or (0.7.72) its track's, where no street crosses it - and dimmed when unowned. */
    static int plotColour(TilePainter.Input in, TilePainter.Painted p, int i) {
        // A tunnel's plot (ET4): its ground, but for every other plot along the run, which is the run's colour - the line dashed.
        if (p.tunnel[i] && !tunnelDash(i)) return groundColour(in, p, i);
        int kind = roadKind(p, i);
        if (kind == 0 && p.use[i] != TilePainter.RAIL) return groundColour(in, p, i);
        int c = kind == 0 ? RAIL_LINE : kind == TilePainter.TRACK ? TRACK_DASH : ROAD_SMALL[kind];
        if (!in.owned[i]) c = blend(c, VOID, UNOWNED_DIM);
        return c;
    }

    /** a toward b by share f, channel by channel, opaque. */
    static int blend(int a, int b, double f) {
        int r = (int) Math.round(((a >> 16) & 255) * (1 - f) + ((b >> 16) & 255) * f);
        int g = (int) Math.round(((a >> 8) & 255) * (1 - f) + ((b >> 8) & 255) * f);
        int bl = (int) Math.round((a & 255) * (1 - f) + (b & 255) * f);
        return 0xff000000 | (r << 16) | (g << 8) | bl;
    }
}
