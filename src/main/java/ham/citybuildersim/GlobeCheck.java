package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.CRC32;

/**
 * The Earth's map as a globe, its model half (0.7.132, batch E8; docs/studies/spec-globe.md 3 and 4's E8 row): EarthProj's orthographic projection, GlobeFrame's view and its handoff to the city's plane, the far rule's three cameras, and the planet's picture with a frame drawn from it - on screen since 0.7.135 (batch E9), which MapCheck 13 holds.
 *
 * WHY. The globe is drawn on a canvas, which cannot be drawn headless in
 * the cloud loop, so its look is checked by eye on the PC; the half of it
 * that is arithmetic is in the model, as MapFrame's is for the plane, and
 * this holds what the eye cannot: that a zoom keeps the point under the
 * pointer, that the swap from the globe to the plane cannot be seen where
 * the study said, that the plane's far nodes are what they were before the
 * camera came between them and the far rule, and that the planet's picture
 * is the data's land.
 *
 * What this has to prove (spec-globe 4's E8 row):
 *   1. THE ORTHOGRAPHIC ROUND TRIP: a point on the disc goes to the planet
 *      and back, and one within LIMB_DEG of the centre to the disc and back,
 *      within ROUND_TRIP_M; the far side is refused; the centre is the
 *      view's middle and north is the roll clockwise from up; north up it is
 *      Snyder's textbook projection, the study's prototype's.
 *   2. THE VIEW: a zoom keeps the point under the pointer within POINTER_PX
 *      - wide, through the GROUND tier and on the plane - and across the
 *      handoff within the parting; a drag keeps the point grabbed; Fit is the
 *      whole planet facing the city, north up; the tiers change at
 *      PICTURE_FROM_KM and HANDOFF_KM; the roll is nought wide, the plane's up
 *      at the handoff and eased by notches between, and nought beyond the
 *      reach (since 0.7.135 eased back over GlobeFrame.ROLL_EASE_KM past it,
 *      so held at twice REACH_KM); the plane's up in closed form is EarthProj.forward()'s own;
 *      the reach stops nothing (MS1, Jerus 2026-10-26, superseding spec-globe
 *      3.4 Q3's "away from the city the wheel stops at the handoff, and on
 *      the plane a pan stops at the reach"): the wheel carries the view on
 *      to the closest scale anywhere and a pan carries its centre past the
 *      reach, which a city's own ground extends and which decides only the
 *      picture closer than the handoff - section 2b.
 *  2b. THE NEAR VIEW ANYWHERE AND NO WALL (MS1): far from the city, another
 *      continent and across the antimeridian from it, the wheel reaches
 *      leastKmPx() in the GROUND tier with the point under the pointer
 *      staying under it; drags outward at a near scale carry the centre past
 *      the reach - PLANE inside, GROUND past it, the point grabbed staying
 *      under the pointer across the crossing; the plane's ground and the
 *      GROUND tier's far rule agree over a patch at the reach's edge to a
 *      measured share (the thousandth of section 5 at the handoff); a view
 *      straddling longitude 180 is continuous, over Fiji's coast on the
 *      fixture and Chukotka's on the full file.
 *  2c. THE NEAR VIEW PAST THE REACH IS THE PLANE'S GROUND (MS2, Jerus
 *      2026-10-26: "look at the wierd resolution by just panning a few more
 *      kms"): closer than the handoff the plane's far nodes are drawn through
 *      the camera wherever the world's square holds the ground
 *      (GlobeFrame.planeDrawnPastReach(), drawsHold(), planeHolds()) - the
 *      pixels the square holds within handoverKm() of the city (MS3: farther,
 *      and off the square, the charts draw both sides of its edge) - and the
 *      legend is the plane's; over views straddling the reach on the fixture's three
 *      windows and, named, Cape Breton north of Baddeck, at his scale, 2, 4 and
 *      8 plots a pixel and the closest, the classes the nodes give are the
 *      plane's own plots' (exactly at one plot a pixel, within NEAR_PAST_FOLDED
 *      and NEAR_PAST_CLASSES at 2 to 8) on both sides of the reach, no more
 *      dithered than the plane's own (the far rule's speckle printed beside
 *      them), and a small lake is the plane's frayed lake, not the far rule's
 *      disc; a frame of the camera over the nodes held within FRAME_MS.
 *  2d. THE SLANTED GROUND, MEASURED (MS3, Jerus 2026-10-26: over Goose Bay from
 *      a Singapore city, at the 2 km scale, every patch of cover is a thin
 *      diagonal streak): streak() reads a ground's elongation and direction
 *      from its change rates along four offsets, and response() what it reads
 *      of a field of known stretch (and turn) made of the rule's own noise at the same area,
 *      STREAK_SEEDS fields averaged - none reads 1, a stretch is read between
 *      STREAK_READ_FLOOR and 1 + STREAK_READ_TOL of itself (low as it grows) in
 *      its direction within STREAK_PHI_DEG; with the city placed 300 to 12,000
 *      km from a view at the 2 km scale (on the full file Goose Bay from 42 km
 *      north of Happy Valley, the city on the way to Singapore and at it), the
 *      plane's noise under each pixel is stretched as Lambert's projection
 *      stretches it - its E over its E at the reach within STREAK_TRACKS of
 *      response(2 / (1 + cos c)) / response(1) - and the drawn ground streaks
 *      along the line to the city from a distortion of 1.5, more as it grows,
 *      and never more than the noise (the cells are true in shape on the
 *      sphere), to STREAK_SHOWN at the farthest placement and at 12,000 km on
 *      a window; the camera draws the plane's plots at one plot a pixel within
 *      the hand-over (so it is not the pixel lookup) and the roll is 0 from
 *      2,000 km (so it is not the turn). The measure came first (slice 1).
 *      THE FIX (MS3, slice 2): past GlobeFrame.handoverKm() the near view is
 *      drawn from charts, true in shape - the drawn ground's E / E_ref is within
 *      STREAK_READ_TOL of 1 at every placement past the hand-over and its band
 *      and never over SHAPE_HANDOVER x (1 + STREAK_TRACKS); no seam at either end
 *      of the band (SEAM_TOL, SHARE_TOL); the sphere's noise keeps the plane's
 *      spread (SPREAD_TOL, RANK_TOL); the world's square's edge is drawn by the
 *      charts on both sides; a chart node and a frame over held chart nodes
 *      are within MapCheck.SCREEN_MS and FRAME_MS.
 *   3. THE HANDOFF: the plane and the globe facing the city part by under
 *      half a pixel on the expanded map at HANDOFF_PLOTS, and by more at twice
 *      it (the dial is the widest power of two that passes); at REACH_KM with
 *      the roll eased by under REACH_PARTING_PX, north up by far more; the
 *      study's tables printed beside its own measure.
 *   4. THE PLANE'S CAMERA IS TODAY'S FAR NODES: every stride's node over the
 *      fixture's four windows and the world's, and on the full file eight
 *      spots' (when it is named), the CRC32 of 0.7.131's.
 *   5. THE GLOBE'S CAMERA: at the handoff facing the city it draws the
 *      plane's node but for the forest's dither, its forest within
 *      SHARE_POINTS of the plane's and its rivers where the plane's are; its
 *      dither cells at least a pixel tall and under two at every scale of the
 *      GROUND tier, so a turn of TURN_PX flips no more than that share of the
 *      dry pixels' forest.
 *   6. THE PLANET'S PICTURE, the grid's camera: the same made twice and on
 *      two threads as on one; its land per 1-degree cell the far layers'
 *      within SHARE_POINTS in the mean over the cells with a coast and each
 *      within its own rounding (the half-contour's most), the planet's within
 *      EarthTerrainCheck.LAND_WITHIN; every texel's sea the far layers'
 *      half-contour, its ice and desert their bits, its forest the data's
 *      within SHARE_POINTS; ice, desert, forest and rivers drawn; its cost and
 *      memory printed - on the fixture's bands, and the planet on the full
 *      file, FIRST_ARCMIN then ARCMIN.
 *   7. A FRAME of the expanded map from the picture in no more than FRAME_MS
 *      of the main thread's CPU (Jerus's A28; MapCheck's clock), the wall
 *      clock beside it, and on two threads - since ET1 slice D by the colours
 *      of its codes, a class and its relief.
 *   8. THE RELIEF IN THE PICTURE (ET1 slice D; spec-earth2-terrain 9.3): a
 *      texel's code is its class and its relief and the globe's colour for
 *      it the map's; no texel of water or ice has a slope (snow stays white);
 *      Medellin's degree is the far rule's, texel for texel, through another
 *      band, and its relief shares the data's within RELIEF_SIGMAS binomial
 *      spreads, foothill the most; a frame over it colours each pixel by its
 *      texel's code, the three greys and the shaded forest among them, and
 *      by a table of the classes alone as before; on the full file the
 *      Himalaya and Tibet's shares are the data's with all three greys and
 *      their snow white. The frame's colours are the only thing the
 *      picture's relief changes in the prints above: the CRC32s of the far
 *      nodes (FIXTURE_PRINTS, FULL_PRINTS) hold the cover, which it never touches.
 *
 * The fixture (EarthCheck's) causes the camera's conditions: its windows
 * hold coasts, rivers and small lakes and Fiji's the date line; its bands
 * cross three continents' coasts. The full file's sections run where it is
 * named (-Dcbs.earth) and say so in one line where it is not.
 */
public class GlobeCheck {

    /** A zoom keeps the point under the pointer under it to within this, in pixels: a thousandth (★E8) - the solver's rounding, nothing a player sees. */
    static final double POINTER_PX = 1e-3;

    /** The orthographic round trip holds a point to within this, in metres: a micrometre (★E8) - at the limb the inverse loses digits, so the planet's points are held within LIMB_DEG of the centre. */
    static final double ROUND_TRIP_M = 1e-6;

    /** ...a point on the planet within this many degrees of the view's centre: 89, the limb's last degree left out, where the disc squeezes 111 km of ground into 17 m. */
    static final double LIMB_DEG = 89;

    /** Random views and points the projection is tried on: 100,000. */
    static final int ROUNDS = 100_000;

    /** At REACH_KM with the roll eased, the plane and the globe part by under this, in pixels: 1.25 (★E8) - the study's 1.16 px, measured with the globe scaled to the plane's mean scale there (globe-prototype.py), 1.21 as the swap is made, the plane at exactly HANDOFF_PLOTS a pixel; a wrong roll or scale parts by 10 px and more. */
    static final double REACH_PARTING_PX = 1.25;

    /** The bearings a view away from the city is looked at, degrees: the study's five. */
    static final double[] BEARINGS = { 0, 45, 90, 135, 180 };

    /** The distances the study's table measured the swap at, km (spec-globe 3.3). */
    static final double[] TABLE_KM = { 100, 300, 500, 1000 };

    /** The study's cities, its boards' (spec-globe 1): Halifax and Manila. */
    static final double[][] CITIES = { { 44.65, -63.57 }, { 14.60, 120.98 } };

    /** The founding globe's canvas, pixels: 560 x 270 (FoundingScreen's GLOBE_W and GLOBE_H; spec-globe 3.2's second column). */
    static final double FOUNDING_W = 560, FOUNDING_H = 270;

    /** A turn of the globe of this many pixels flips the forest on no more than this share of the dry pixels: a tenth (★E8) - the dither's cells are at least a pixel each way, so a point moving a tenth of a pixel crosses a cell's edge on at most a tenth of them; a dither drawn afresh each frame flips 2f(1 - f) of them. */
    static final double TURN_PX = 0.1;

    /** A frame of the expanded map from the planet's picture in no more than this of the main thread's CPU, ms: 100 (spec-globe 3.6 measured 96 to 102 ms on one cloud thread, 50 on two, and called that live). */
    static final double FRAME_MS = 100;

    /** Frames timed, the least taken, after as many to warm. */
    static final int FRAME_ROUNDS = 10;

    /**
     * The CRC32 of every stride's far node over the fixture's four windows and the world's (fingerprint()), as 0.7.131 drew them before the camera
     * (probe E8Fingerprint on its build, 19f1cad), but for what the earth-2 fixture and the shallow sea changed: re-struck by ET1 (2026-10-24) -
     * the fixture's own tiles moved them at slice A (earth-2's fixture holds Halifax's Narrows), and the shallow sea (World.SHALLOW) is a class the
     * far nodes now hold. Folding SHALLOW back to SALT gives slice A's figures (0b83f8c6, c5ba11b7, 85a23609, 1835aa85).
     */
    static final Map<String, Long> FIXTURE_PRINTS = Map.of("Rotterdam", 0xed2ff708L, "Perth", 0x0b1d7f68L, "Paris", 0xc3979824L, "Fiji", 0x4a0874dfL);

    /** The full file's spots: Rotterdam, Halifax, Manila, Greenland's ice, McMurdo past EarthProj.BLEND_MAX_LAT, Chukotka on the 180th meridian, Finland's lakes and the Sahara. */
    static final double[][] FULL_SPOTS = { { 51.90625, 4.48125 }, { 44.65, -63.57 }, { 14.60, 120.98 }, { 72, -40 }, { -77.85, 166.67 }, { 66, 180 }, { 62, 26 }, { 23, 12 } };

    /**
     * ...and their nodes' CRC32, as 0.7.131 drew them but for the shallow sea (ET1, 2026-10-24: World.SHALLOW is now a class the far nodes hold, so the
     * figures moved). Folding SHALLOW back to SALT gives 0.7.131's eight exactly (53186882, dcf6b91e, 4b92b699, 34df4490, 663a5196, 7e38f4f5, 45cc7052,
     * 2b58c5a8): nothing but the shallow sea changed in them.
     */
    static final long[] FULL_PRINTS = { 0xf291ea65L, 0x208e9728L, 0xd85a163cL, 0x734fdb04L, 0xf5f461adL, 0xbdc3b5b7L, 0x864aa160L, 0xf0f527ccL };

    static int fails = 0;
    /** Where the harness prints: the console. */
    static final PrintStream out = System.out;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-110s %s%n", label, Timing.verdict(ok, label));
    }

    /** The planet's radius in metres (EarthProj.RADIUS_M), which the sections measure arcs and the disc by. */
    static final double R = EarthProj.RADIUS_M;

    public static void main(String[] args) throws Exception {
        ortho();
        view();
        handoff();
        out.println("\n--- the fixture ---");
        byte[] fixture = EarthCheck.resource(EarthCheck.FIXTURE), expect = EarthCheck.resource(EarthCheck.EXPECT);
        check("EarthCheck's fixture and its expected figures are beside the classes (" + EarthCheck.FIXTURE + ")", fixture != null && expect != null);
        if (fixture == null || expect == null) {
            out.println("\n" + fails + " FAILED");
            System.exit(1);
        }
        EarthData data = EarthData.of(fixture);
        String id = EarthTerrain.use(data);
        Map<String, EarthTerrainCheck.Spot> spots = new LinkedHashMap<>();
        for (String line : new String(expect, StandardCharsets.UTF_8).split("\n")) {
            String[] f = line.split("\t", -1);
            if (f.length > 4 && f[1].equals("window")) spots.put(f[2], new EarthTerrainCheck.Spot(f[2], Double.parseDouble(f[3]), Double.parseDouble(f[4])));
        }
        Path file = EarthData.locate();
        EarthData full = file != null && Files.isRegularFile(file) ? EarthData.open(file) : null;
        planeCamera(spots, id, full);
        cameras(spots, id, data);
        nearAnywhere(spots, id, full);
        nearPastReach(spots, id, data, full);
        slanted(spots, id, full);
        GlobePicture pic = picture(data, full);
        reliefInPicture(data, id, full, pic);
        frame(pic);
        out.println();
        if (fails == 0) out.println("All checks passed.");
        else out.println(fails + " check(s) failed.");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       1. THE ORTHOGRAPHIC PROJECTION
       ===================================================================== */

    static void ortho() {
        out.println("\n--- 1. the orthographic projection: forward and inverse round a view centre, with a roll ---");
        Random r = new Random(1);
        double[] ll = new double[2], xy = new double[2], back = new double[2];
        double screenWorst = 0, planetWorst = 0, snyderWorst = 0, northWorst = 0, middleWorst = 0;
        int far = 0, refused = 0, lost = 0;
        for (int k = 0; k < ROUNDS; k++) {
            double la0 = Math.toDegrees(Math.asin(2 * r.nextDouble() - 1)), lo0 = 360 * r.nextDouble() - 180, roll = 360 * r.nextDouble() - 180;
            EarthProj.Ortho o = EarthProj.ortho(la0, lo0, roll);
            // the disc to the planet and back
            double rho = R * Math.sqrt(r.nextDouble()), th = 2 * Math.PI * r.nextDouble(), x = rho * Math.cos(th), y = rho * Math.sin(th);
            if (!o.inverse(x, y, ll) || !o.forward(ll[0], ll[1], back)) lost++;
            else screenWorst = Math.max(screenWorst, Math.hypot(back[0] - x, back[1] - y));
            // the planet to the disc and back, or refused on the far side
            double la = Math.toDegrees(Math.asin(2 * r.nextDouble() - 1)), lo = 360 * r.nextDouble() - 180;
            double c = EarthProj.distanceM(la0, lo0, la, lo) / R;
            if (c > Math.PI / 2 + 1e-9) {
                far++;
                if (!o.forward(la, lo, xy)) refused++;
            } else if (Math.toDegrees(c) <= LIMB_DEG) {
                if (!o.forward(la, lo, xy) || !o.inverse(xy[0], xy[1], back)) lost++;
                else planetWorst = Math.max(planetWorst, EarthProj.distanceM(la, lo, back[0], back[1]));
                // north up against the textbook's (Snyder 1987, (20-3) and (20-4)), turned by the roll
                double f0 = Math.toRadians(la0), f = Math.toRadians(la), dl = Math.toRadians(lo - lo0);
                double e = R * Math.cos(f) * Math.sin(dl), n = R * (Math.cos(f0) * Math.sin(f) - Math.sin(f0) * Math.cos(f) * Math.cos(dl));
                double g = Math.toRadians(roll);
                snyderWorst = Math.max(snyderWorst, Math.hypot(xy[0] - (e * Math.cos(g) + n * Math.sin(g)), xy[1] - (n * Math.cos(g) - e * Math.sin(g))));
            }
            // the centre at the middle, and north there turned by the roll
            if (Math.abs(la0) < 80) {
                o.forward(la0, lo0, xy);
                middleWorst = Math.max(middleWorst, Math.hypot(xy[0], xy[1]));
                o.forward(la0 + 1e-3, lo0, xy);
                double a = Math.toDegrees(Math.atan2(xy[0], xy[1]));
                northWorst = Math.max(northWorst, Math.abs(Math.IEEEremainder(a - roll, 360)));
            }
        }
        out.printf("      %,d views: the disc to the planet and back %.2e m at worst; the planet within %.0f degrees of the centre to the disc and back %.2e m;"
                + " %,d points on the far side; against Snyder's %.2e m%n", ROUNDS, screenWorst, LIMB_DEG, planetWorst, far, snyderWorst);
        check("a point on the disc goes to the planet and back to within ROUND_TRIP_M", lost == 0 && screenWorst <= ROUND_TRIP_M);
        check("...and a point on the planet within LIMB_DEG of the centre to the disc and back", planetWorst <= ROUND_TRIP_M);
        double[] off = new double[2];
        check("every point on the far side is refused - the globe hides it - and nothing past the disc is on the planet",
                far > 0 && refused == far && !EarthProj.ortho(10, 20, 30).inverse(R * (1 + 1e-9), 0, off) && !EarthProj.Ortho.onDisc(R * (1 + 1e-9), 0));
        check("the view's centre is its middle, and north there is the roll clockwise from up (to a millionth of a degree)", middleWorst < 1e-6 && northWorst < 1e-6);
        check("...and it is Snyder's textbook projection (the study's prototype's) turned by the roll, to a millimetre", snyderWorst < 1e-3);
    }

    /* =====================================================================
       2. THE VIEW
       ===================================================================== */

    static double[] pointer(Random r, GlobeFrame f) {
        for (int k = 0; k < 1000; k++) {
            double sx = f.width() * r.nextDouble(), sy = f.height() * r.nextDouble();
            double[] ll = f.latLonAt(sx, sy);
            if (ll != null && Math.abs(ll[0]) < 80) return new double[] { sx, sy };
        }
        return new double[] { f.width() / 2, f.height() / 2 };
    }

    /** How far, in the view's pixels, (lat, lon) lies from screen point (sx, sy): null where it is not seen. */
    static double offPx(GlobeFrame f, double[] ll, double sx, double sy) {
        double[] p = f.at(ll[0], ll[1]);
        return p == null ? Double.POSITIVE_INFINITY : Math.hypot(p[0] - sx, p[1] - sy);
    }

    /** Whether north up can put (lat) at screen point (sx, sy) of a wide view at kmPx: the spherical triangle of the pole, the centre and the point allows it - |sin lat| no more than the root of cos^2 c + sin^2 c cos^2 a, c and a the point's angle and azimuth from the centre. */
    static boolean northUpCan(double lat, double sx, double sy, double w, double h, double kmPx) {
        double x = (sx - w / 2) * kmPx * 1000, y = (h / 2 - sy) * kmPx * 1000, rho = Math.hypot(x, y);
        if (rho > R) return false;
        double c = Math.asin(rho / R), a = Math.atan2(x, y);
        return Math.abs(Math.sin(Math.toRadians(lat))) <= Math.sqrt(Math.pow(Math.cos(c), 2) + Math.pow(Math.sin(c) * Math.cos(a), 2)) - 1e-9;
    }

    static void view() {
        out.println("\n--- 2. the view: zoom at the pointer, a drag, Fit, the tiers, the roll and the reach ---");
        double w = MapTiles.DESIGN_W, h = MapTiles.DESIGN_H, hk = GlobeFrame.HANDOFF_KM;
        Random r = new Random(2);
        double wide = 0, ground = 0, onPlane = 0, across = 0, dragged = 0, wideOut = 0;
        int zooms = 0, outs = 0, cannot = 0, drags = 0, crossed = 0;
        for (double[] c : CITIES) {
            GlobeFrame f = new GlobeFrame(w, h, c[0], c[1]);
            // Fit: the whole planet facing the city, north up.
            double disc = R / (f.kmPx() * 1000);
            check(String.format("Fit round %.2f, %.2f: the whole planet facing the city, north up, its disc FIT_SHARE of the view's shorter side", c[0], c[1]),
                    f.centreLat() == c[0] && f.centreLon() == c[1] && f.roll() == 0 && f.tier() == GlobeFrame.PICTURE
                            && Math.abs(2 * disc - GlobeFrame.FIT_SHARE * Math.min(w, h)) < 1e-9);
            // In from the whole planet notch by notch at pointers on the planet, wide and through the GROUND tier.
            for (int k = 0; k < 40; k++) {
                double[] p = pointer(r, f), ll = f.latLonAt(p[0], p[1]);
                int before = f.tier();
                f.zoomNotches(p[0], p[1], 1);
                double d = offPx(f, ll, p[0], p[1]);
                if (before == f.tier() && f.tier() == GlobeFrame.PICTURE) wide = Math.max(wide, d);
                else if (before == f.tier() && f.tier() == GlobeFrame.GROUND) ground = Math.max(ground, d);
                zooms++;
                if (f.kmPx() <= hk * 1.0001) break;
            }
            // Near the city, in across the handoff and on the plane, then out again to the picture's texel, at pointers on the middle half.
            f.set(c[0], c[1], hk * GlobeFrame.ZOOM_STEP);
            for (int k = -12; k < 30; k++) {
                double sx = w / 2 + (r.nextDouble() - 0.5) * w / 2, sy = h / 2 + (r.nextDouble() - 0.5) * h / 2;
                double[] ll = f.latLonAt(sx, sy);
                int before = f.tier();
                f.zoomNotches(sx, sy, k < 0 ? 1 : -1);
                double d = offPx(f, ll, sx, sy);
                if (before != f.tier() && (before == GlobeFrame.PLANE || f.tier() == GlobeFrame.PLANE)) {
                    across = Math.max(across, d);
                    crossed++;
                }
                else if (f.tier() == GlobeFrame.PLANE) onPlane = Math.max(onPlane, d);
                else if (f.tier() == GlobeFrame.GROUND && before == GlobeFrame.GROUND) ground = Math.max(ground, d);
                zooms++;
                if (f.kmPx() >= GlobeFrame.PICTURE_FROM_KM / GlobeFrame.ZOOM_STEP) break;
            }
            // Out from the picture's texel to the whole planet, wide: north up cannot put every point back under the pointer.
            for (int k = 0; k < 20; k++) {
                double[] p = pointer(r, f), ll = f.latLonAt(p[0], p[1]);
                double to = Math.min(f.mostKmPx(), f.kmPx() * GlobeFrame.ZOOM_STEP);
                boolean can = northUpCan(ll[0], p[0], p[1], w, h, to);
                f.zoomNotches(p[0], p[1], -1);
                outs++;
                if (can && Math.abs(f.centreLat()) < GlobeFrame.POLE_LAT) wideOut = Math.max(wideOut, offPx(f, ll, p[0], p[1]));
                else cannot++;
                zooms++;
            }
            // Drags in the GROUND tier and on the plane, where north up can put every point the view holds.
            for (double k : new double[] { 2, hk, hk / 4 }) {
                f.set(c[0] + 0.3, c[1] + 0.5, k);
                for (int i = 0; i < 10; i++) {
                    double[] p = pointer(r, f), ll = f.latLonAt(p[0], p[1]);
                    double dx = (r.nextDouble() - 0.5) * 200, dy = (r.nextDouble() - 0.5) * 200;
                    f.drag(p[0], p[1], p[0] + dx, p[1] + dy);
                    dragged = Math.max(dragged, offPx(f, ll, p[0] + dx, p[1] + dy));
                    drags++;
                }
            }
        }
        out.printf("      %d zooms: the point under the pointer moved %.1e px at worst wide, %.1e in the GROUND tier, %.1e on the plane,"
                + " %.1e across the handoff (%d times); of %d zooms out wide %.1e where north up can put it (%d it cannot); %d drags %.1e%n",
                zooms, wide, ground, onPlane, across, crossed, outs, wideOut, cannot, drags, dragged);
        check("a zoom in keeps the point under the pointer within POINTER_PX, wide and through the GROUND tier; in and out on the plane and near the handoff",
                wide <= POINTER_PX && ground <= POINTER_PX && onPlane <= POINTER_PX && zooms > 0);
        check("...and across the handoff, both ways, the point put in the new tier's own projection", crossed >= 2 * CITIES.length && across <= POINTER_PX);
        check("...and a zoom out wide wherever north up can put the point there (the spherical triangle allows it)", wideOut <= POINTER_PX && outs > cannot);
        check("a drag keeps the point grabbed under the pointer within POINTER_PX, on the GROUND tier and the plane", dragged <= POINTER_PX && drags > 0);

        // The tiers.
        double k = GlobeFrame.PICTURE_FROM_KM;
        check("the tiers change at the dials: PICTURE from PICTURE_FROM_KM (the picture's texel), GROUND to HANDOFF_KM, PLANE closer",
                GlobeFrame.tierOf(k) == GlobeFrame.PICTURE && GlobeFrame.tierOf(Math.nextDown(k)) == GlobeFrame.GROUND
                        && GlobeFrame.tierOf(hk) == GlobeFrame.GROUND && GlobeFrame.tierOf(Math.nextDown(hk)) == GlobeFrame.PLANE
                        && hk == GlobeFrame.HANDOFF_PLOTS * World.PLOT_M / 1000 && Math.abs(k - GlobePicture.texelKm(GlobePicture.ARCMIN)) < 1e-12);

        // The roll, at the reach's edge east of Halifax.
        double[] c = CITIES[0];
        GlobeFrame f = new GlobeFrame(w, h, c[0], c[1]);
        double[] east = atReach(c, 90);
        double up = f.planeRollAt(east[0], east[1]);
        double numeric = numericRoll(f.plane(), east[0], east[1]);
        f.set(east[0], east[1], hk * Math.pow(GlobeFrame.ZOOM_STEP, GlobeFrame.ROLL_NOTCHES));
        double wideRoll = f.roll();
        f.set(east[0], east[1], hk * Math.pow(GlobeFrame.ZOOM_STEP, GlobeFrame.ROLL_NOTCHES / 2.0));
        double halfRoll = f.roll();
        f.set(east[0], east[1], hk);
        double atHandoff = f.roll();
        double[] beyond = GlobeFrame.destination(c[0], c[1], 90, 2 * GlobeFrame.REACH_KM);
        f.set(beyond[0], beyond[1], hk);
        double beyondRoll = f.roll();
        out.printf("      REACH_KM east of Halifax: the plane's up %.3f degrees from north (by its derivative %.3f); the roll %.3f wide, %.3f halfway,"
                + " %.3f at the handoff, %.3f twice as far%n", up, numeric, wideRoll, halfRoll, atHandoff, beyondRoll);
        check("the plane's up at a place, in closed form, is EarthProj.forward()'s own along the meridian, and nought at the city",
                Math.abs(up - numeric) < 1e-6 && Math.abs(f.planeRollAt(c[0], c[1])) < 1e-9);
        check("the roll is nought ROLL_NOTCHES notches before the handoff, the plane's up at it, eased by notches between, and nought beyond the reach",
                wideRoll == 0 && Math.abs(atHandoff - up) < 1e-12 && Math.abs(halfRoll - up / 2) < 1e-9 && beyondRoll == 0 && Math.abs(up) > 1);

        // The reach decides the picture and stops nothing (MS1): the wheel goes on closer than the handoff away from the city, in the GROUND
        // tier; a pan at the plane's scales carries the centre past the reach (section 2b holds both at length).
        double[] away = GlobeFrame.destination(c[0], c[1], 45, 3 * GlobeFrame.REACH_KM);
        f.set(away[0], away[1], hk * GlobeFrame.ZOOM_STEP);
        for (int i = 0; i < 5; i++) f.zoomNotches(w / 2, h / 2, 1);
        double wentTo = f.kmPx();
        boolean wentOn = Math.abs(wentTo - hk / Math.pow(GlobeFrame.ZOOM_STEP, 4)) < 1e-9 * hk && f.tier() == GlobeFrame.GROUND;
        f.set(c[0], c[1], hk / GlobeFrame.ZOOM_STEP);
        for (int i = 0; i < 200; i++) f.pan(-300, -170);
        double reached = f.cityKm(f.centreLat(), f.centreLon());
        boolean groundPast = f.tier() == GlobeFrame.GROUND;
        out.printf("      %.0f km from the city five notches in went to %.3f km a pixel, the GROUND tier; 200 drags from the city on the plane's scale took the centre %.0f km out%n",
                3 * GlobeFrame.REACH_KM, wentTo, reached);
        check("away from the city the wheel goes on closer than the handoff, five notches in, the GROUND tier (MS1: it stopped at the handoff)", wentOn);
        check("...and a pan at the plane's scales carries the centre past the reach, the GROUND tier past it (MS1: it stopped at the reach)", groundPast && reached > GlobeFrame.REACH_KM);
        double plots = 600_000 / World.PLOT_M;
        check("a city's ground reaching farther extends the reach by REACH_MARGIN_KM round it; a small city's is REACH_KM",
                GlobeFrame.reachKm(0) == GlobeFrame.REACH_KM && GlobeFrame.reachKm(100_000 / World.PLOT_M) == GlobeFrame.REACH_KM
                        && Math.abs(GlobeFrame.reachKm(plots) - (2 * R * Math.asin(600_000 / (2 * R)) / 1000 + GlobeFrame.REACH_MARGIN_KM)) < 1e-9);

        // The plane taken and given back.
        f.set(c[0] + 1, c[1] + 1, hk / 2);
        double x = f.planeX(), y = f.planeY(), s = f.planeScale(), la = f.centreLat(), lo = f.centreLon();
        f.fromPlane(x, y, s);
        check("the plane's view handed over and taken back leaves the view where it was",
                Math.abs(f.centreLat() - la) < 1e-9 && Math.abs(f.centreLon() - lo) < 1e-9 && Math.abs(f.kmPx() - hk / 2) < 1e-12
                        && Math.abs(s - World.PLOT_M / (hk / 2 * 1000)) < 1e-15);
    }

    /** The point at the reach's edge from a city along a bearing: REACH_KM out, a hair inside it as a pan's stop leaves the view. */
    static double[] atReach(double[] city, double bearing) {
        return GlobeFrame.destination(city[0], city[1], bearing, GlobeFrame.REACH_KM * (1 - 1e-9));
    }

    /** The plane's up at (lat, lon) against north, degrees clockwise, by a small step north through EarthProj.forward(). */
    static double numericRoll(EarthProj p, double lat, double lon) {
        double d = 1e-5;
        double[] a = new double[2], b = new double[2];
        p.forward(lat - d, lon, a);
        p.forward(lat + d, lon, b);
        return Math.toDegrees(Math.atan2(b[0] - a[0], b[1] - a[1]));
    }

    /* =====================================================================
       2b. THE NEAR VIEW ANYWHERE AND NO WALL (MS1)
       ===================================================================== */

    /** A near scale, km a pixel: a quarter of the handoff's, four plots a pixel (MS1's views closer than the handoff, on the plane and past its reach). */
    static final double NEAR_KM = GlobeFrame.HANDOFF_KM / 4;

    /** The pixels a drag outward takes at a time in section 2b: 150 - at NEAR_KM, 18 km, so the reach is crossed in about seventeen. */
    static final int STEP_PX = 150;

    /** The most drags outward section 2b takes: 100, far past twice the reach at NEAR_KM. */
    static final int STEPS_MOST = 100;

    /** The spots section 2b zooms in on far from either city, {lat, lon}: Manila, Perth, and a spot each side of the antimeridian (the date line) at 17 degrees south and Manila's latitude. */
    static final double[][] FAR_SPOTS = { { 14.60, 120.98 }, { -31.95, 115.86 }, { -17.0, 179.9 }, { -17.0, -179.9 }, { 14.60, -179.9 } };

    /**
     * The share of a patch's pixels whose class the plane and the GROUND tier's far rule may draw differently at the handoff, over a patch at the reach's
     * edge: 1 percent (measured 0.15 to 0.65 percent over six patches - the fixture's three windows and the full file's Rotterdam, Halifax and Manila - Rotterdam's the most coast). Section 5 holds the same two to a
     * thousandth facing the city, where the camera's pixels fall on the plane's far node's, unturned; at the reach's edge the roll has turned the camera
     * against the plane's grid by up to its eased degrees and the two projections part by up to REACH_PARTING_PX, so a coast's or a river's pixel
     * samples the next cell of 16 plots. A wrong class table, scale or roll parts them by a quarter of the patch and more.
     */
    static final double CROSSING_HANDOFF_SHARE = 0.01;

    /**
     * ...and at NEAR_KM, where the plane draws a far node of four plots a pixel and the camera the far rule at its least stride, DITHER_STRIDE (16
     * plots), so a coast or a river parts by up to a pixel and four: 6 percent (measured 0.56 to 3.32 percent over the same six patches, Halifax's islanded coast on the full file the most; twice that).
     */
    static final double CROSSING_NEAR_SHARE = 0.06;

    /** The class the city's plane draws at plot (x, y), at `plots` plots a pixel: the far node holding it (MapTiles.nodeTerrain()), its pixel under the plot. */
    static byte planeClass(World.Key key, long plots, double x, double y, Map<Long, byte[]> nodes) {
        long span = (long) MapFrame.NODE_PX * plots, px = (long) Math.floor(x), py = (long) Math.floor(y);
        long nx = Math.floorDiv(px, span), ny = Math.floorDiv(py, span);
        byte[] node = nodes.computeIfAbsent(MapTiles.key(nx, ny), k -> MapTiles.nodeTerrain(key, nx * span, ny * span, plots));
        return node[(int) ((py - ny * span) / plots) * MapFrame.NODE_PX + (int) ((px - nx * span) / plots)];
    }

    /**
     * The share of a n x n view's pixels whose class the city's plane draws differently from the GROUND tier's far rule through the globe's camera,
     * the forest folded into the dry ground it stands on (its dither is a hash): the view centred at (lat, lon), at `kmPx`, the city (cityLat, cityLon).
     */
    static double crossingShare(World.Key key, double cityLat, double cityLon, double lat, double lon, double kmPx, int n) {
        GlobeFrame f = new GlobeFrame(n, n, cityLat, cityLon);
        f.set(lat, lon, kmPx);
        long plots = MapFrame.nodePlotsAt(f.planeScale());
        byte[] globe = f.groundPiece(key, 0, 0, n, n);
        Map<Long, byte[]> nodes = new java.util.HashMap<>();
        double[] xy = new double[2];
        int differ = 0, seen = 0;
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                if (!f.planeAt(i + 0.5, j + 0.5, xy)) continue;
                seen++;
                if (folded(planeClass(key, plots, xy[0], xy[1], nodes)) != folded(globe[j * n + i])) differ++;
            }
        }
        return seen == 0 ? 1 : differ / (double) seen;
    }

    /** How the share of land and of water among a column's pixels mixes: the lesser of the two, 0 for a column all one or the other. */
    static double mixed(byte[] piece, int w, int col, int h) {
        int land = 0;
        for (int j = 0; j < h; j++) if (World.dry(piece[j * w + col])) land++;
        return Math.min(land, h - land) / (double) h;
    }

    static void nearAnywhere(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full) {
        out.println("\n--- 2b. the near view anywhere and no wall (MS1): the wheel, a drag and a pan go anywhere at every scale; the reach only decides the picture ---");
        double w = MapTiles.DESIGN_W, h = MapTiles.DESIGN_H, hk = GlobeFrame.HANDOFF_KM, least = GlobeFrame.leastKmPx();
        Random r = new Random(11);

        // The tiers: the scale's, and the view's - GROUND where the plane's scale meets a centre past the reach.
        check("tierOf(kmPx, withinReach): the plane's scales are the PLANE within the reach and the GROUND tier past it; the wider scales are the scale's either way",
                GlobeFrame.tierOf(hk / 2, true) == GlobeFrame.PLANE && GlobeFrame.tierOf(hk / 2, false) == GlobeFrame.GROUND
                        && GlobeFrame.tierOf(hk, false) == GlobeFrame.GROUND && GlobeFrame.tierOf(hk, true) == GlobeFrame.GROUND
                        && GlobeFrame.tierOf(GlobeFrame.PICTURE_FROM_KM, false) == GlobeFrame.PICTURE && GlobeFrame.tierOf(GlobeFrame.PICTURE_FROM_KM, true) == GlobeFrame.PICTURE
                        && GlobeFrame.tierOf(least, true) == GlobeFrame.PLANE && GlobeFrame.tierOf(least, false) == GlobeFrame.GROUND
                        && GlobeFrame.tierOf(hk / 2) == GlobeFrame.PLANE);

        // The wheel reaches leastKmPx() anywhere: far from each city, in notch by notch, the point under the pointer staying under it.
        int notches = 0, stuck = 0, spotsTried = 0;
        double worstIn = 0, worstOut = 0;
        boolean reached = true, ground = true, farStill = true;
        for (double[] c : CITIES) {
            for (double[] spot : FAR_SPOTS) {
                GlobeFrame f = new GlobeFrame(w, h, c[0], c[1]);
                f.set(spot[0], spot[1], GlobeFrame.PICTURE_FROM_KM / GlobeFrame.ZOOM_STEP);
                if (f.withinReach(spot[0], spot[1])) continue;
                spotsTried++;
                while (f.kmPx() > least) {
                    double sx = w / 2 + (r.nextDouble() - 0.5) * w / 2, sy = h / 2 + (r.nextDouble() - 0.5) * h / 2;
                    double[] ll = f.latLonAt(sx, sy);
                    double was = f.kmPx();
                    f.zoomNotches(sx, sy, 1);
                    worstIn = Math.max(worstIn, offPx(f, ll, sx, sy));
                    if (!(f.kmPx() < was)) {
                        stuck++;
                        break;
                    }
                    notches++;
                    ground &= f.tier() == GlobeFrame.GROUND;
                }
                reached &= f.kmPx() == least;
                farStill &= !f.withinReach(f.centreLat(), f.centreLon());
                // ...and out again a few notches, from the closest.
                for (int k = 0; k < 10; k++) {
                    double sx = w / 2 + (r.nextDouble() - 0.5) * w / 2, sy = h / 2 + (r.nextDouble() - 0.5) * h / 2;
                    double[] ll = f.latLonAt(sx, sy);
                    f.zoomNotches(sx, sy, -1);
                    worstOut = Math.max(worstOut, offPx(f, ll, sx, sy));
                    ground &= f.tier() == GlobeFrame.GROUND;
                }
            }
        }
        out.printf("      %d spots far from the cities (Manila and Perth, and each side of the antimeridian), %d notches in to %.5f km a pixel: the point under the pointer moved %.1e px in, %.1e out; %d stuck%n",
                spotsTried, notches, least, worstIn, worstOut, stuck);
        check("far from the city (another continent, and across the antimeridian from it) the wheel reaches leastKmPx(), nothing stopping it at the handoff",
                spotsTried >= 6 && stuck == 0 && reached && notches > spotsTried * 20);
        check("...the tier there is GROUND, in and out, and the view still far from the city", ground && farStill);
        check("...and the point under the pointer stays under it within POINTER_PX, in and out", worstIn <= POINTER_PX && worstOut <= POINTER_PX);

        // A drag, a pan and a held key go anywhere at every scale: at the closest, far away, the centre moves as the pixels say.
        double[] far = FAR_SPOTS[3];
        GlobeFrame g = new GlobeFrame(w, h, CITIES[0][0], CITIES[0][1]);
        g.set(far[0], far[1], least);
        double[] at0 = { g.centreLat(), g.centreLon() };
        int moved = 0;
        double dragWorst = 0;
        for (int k = 0; k < 40; k++) {
            double[] p = pointer(r, g), ll = g.latLonAt(p[0], p[1]);
            double dx = (r.nextDouble() - 0.5) * 300, dy = (r.nextDouble() - 0.5) * 300;
            double[] before = { g.centreLat(), g.centreLon() };
            g.drag(p[0], p[1], p[0] + dx, p[1] + dy);
            dragWorst = Math.max(dragWorst, offPx(g, ll, p[0] + dx, p[1] + dy));
            if (g.centreLat() != before[0] || g.centreLon() != before[1]) moved++;
        }
        double[] beforeKeys = { g.centreLat(), g.centreLon() };
        for (int k = 0; k < 30; k++) g.keyPan(new double[] { MapFrame.KEY_PX_A_SECOND, 0 }, MapFrame.KEY_STEP_MOST_S);
        double keyedKm = EarthProj.distanceM(beforeKeys[0], beforeKeys[1], g.centreLat(), g.centreLon()) / 1000;
        double keyedWant = 30 * MapFrame.KEY_PX_A_SECOND * MapFrame.KEY_STEP_MOST_S * least;
        out.printf("      at %.5f km a pixel %.0f km from the city: 40 drags moved the centre %d times, the grabbed point %.1e px off; 30 held-key frames moved it %.2f km (%.2f asked)%n",
                least, g.cityKm(g.centreLat(), g.centreLon()), moved, dragWorst, keyedKm, keyedWant);
        check("at the closest scale far from the city a drag moves the centre every time, keeping the point grabbed under the pointer (POINTER_PX)", moved == 40 && dragWorst <= POINTER_PX && at0[0] != g.centreLat());
        check("...and a held key's frames turn the globe as the pixels say, the GROUND tier still", Math.abs(keyedKm - keyedWant) < 0.02 * keyedWant && g.tier() == GlobeFrame.GROUND);

        // Drags outward from the city at a near scale carry the centre past the reach with no stop: PLANE inside it, GROUND past it.
        boolean grows = true, tiers = true, stays = true, crossedAll = true, rollsFollow = true;
        double offWorst = 0, rollJump = 0, atCross = 0, maxUp = 0;
        int dragsMade = 0;
        for (double[] c : CITIES) {
            for (double[] step : new double[][] { { -STEP_PX, 0 }, { 0, STEP_PX }, { STEP_PX, STEP_PX } }) {
                GlobeFrame f = new GlobeFrame(w, h, c[0], c[1]);
                f.set(c[0], c[1], NEAR_KM);
                double last = 0, lastRoll = f.roll();
                int inside = 0, past = 0;
                boolean crossed = false;
                for (int k = 0; k < STEPS_MOST && f.cityKm(f.centreLat(), f.centreLon()) < 2 * f.reach(); k++) {
                    double[] ll = f.latLonAt(w / 2, h / 2);
                    boolean wasWithin = f.withinReach(f.centreLat(), f.centreLon());
                    f.drag(w / 2, h / 2, w / 2 + step[0], h / 2 + step[1]);
                    dragsMade++;
                    double d = f.cityKm(f.centreLat(), f.centreLon());
                    grows &= d > last;
                    last = d;
                    boolean within = d <= f.reach();
                    tiers &= f.tier() == (within ? GlobeFrame.PLANE : GlobeFrame.GROUND) && f.tier() == GlobeFrame.tierOf(f.kmPx(), within);
                    stays &= f.kmPx() == NEAR_KM;
                    double off = offPx(f, ll, w / 2 + step[0], h / 2 + step[1]);
                    offWorst = Math.max(offWorst, off);
                    if (wasWithin && !within) {
                        crossed = true;
                        atCross = Math.max(atCross, off);
                    }
                    rollJump = Math.max(rollJump, Math.abs(f.roll() - lastRoll));
                    maxUp = Math.max(maxUp, Math.abs(f.planeRollAt(f.centreLat(), f.centreLon())));
                    lastRoll = f.roll();
                    rollsFollow &= f.roll() == f.rollFor(f.centreLat(), f.centreLon(), f.kmPx());
                    if (within) inside++;
                    else past++;
                }
                crossedAll &= crossed && inside > 2 && past > 2;
            }
        }
        // One drag moves the centre by at most stepKm: the roll eases from the plane's up to north over ROLL_EASE_KM past the reach, and the plane's up itself
        // drifts over REACH_KM by its own degrees - so a drag turns the view by no more than the share of each its step covers.
        double stepKm = Math.hypot(STEP_PX, STEP_PX) * NEAR_KM, rollBound = maxUp * stepKm / GlobeFrame.ROLL_EASE_KM + maxUp * stepKm / GlobeFrame.REACH_KM;
        out.printf("      %d drags of %d px outward from two cities (three directions each) at %.3f km a pixel: the distance grew every time, the reach (%.0f km) crossed in each,"
                + " the grabbed point %.1e px off at worst (%.1e across the crossing), the roll %.2f degrees at most from one drag to the next (the plane's up %.2f at most, so %.2f allowed)%n",
                dragsMade, STEP_PX, NEAR_KM, GlobeFrame.REACH_KM, offWorst, atCross, rollJump, maxUp, rollBound);
        check("drags outward at a near scale carry the centre past the reach with no stop: its distance from the city grows every drag, through the reach and beyond it", grows && crossedAll);
        check("...the tier is PLANE inside the reach and GROUND past it, at the same scale", tiers && stays);
        check("...and the point grabbed stays under the pointer within POINTER_PX at every step, across the crossing too", offWorst <= POINTER_PX && atCross <= POINTER_PX);
        check("...the roll the view takes is rollFor()'s, and one drag turns it by no more than the share of ROLL_EASE_KM and of REACH_KM its step covers, of the plane's up (it eases)",
                rollsFollow && rollJump <= rollBound);

        // The reach holds a view's centre to nothing, and a set view is where it was set.
        GlobeFrame s = new GlobeFrame(w, h, CITIES[0][0], CITIES[0][1]);
        double[] out3 = GlobeFrame.destination(CITIES[0][0], CITIES[0][1], 70, 3 * GlobeFrame.REACH_KM);
        s.set(out3[0], out3[1], least);
        s.setReach(GlobeFrame.REACH_KM);
        check("a view set at the closest scale three reaches from the city is where it was set, GROUND, and a reach set again moves nothing",
                s.kmPx() == least && s.tier() == GlobeFrame.GROUND && Math.abs(s.cityKm(s.centreLat(), s.centreLon()) - 3 * GlobeFrame.REACH_KM) < 1e-6 && s.crossfade() == 1);
        GlobeFrame inReach = new GlobeFrame(w, h, CITIES[0][0], CITIES[0][1]);
        inReach.set(CITIES[0][0], CITIES[0][1], hk / 1.1);
        check("the view's own crossfade: the globe's share by notches inside the handoff while the plane is drawn, whole where there is no plane under it",
                inReach.tier() == GlobeFrame.PLANE && inReach.crossfade() == GlobeFrame.crossfade(hk / 1.1) && inReach.crossfade() > 0 && inReach.crossfade() < 1);

        // The antimeridian: a view straddling longitude 180 is continuous in its arithmetic (a step across it the same as a step elsewhere)...
        GlobeFrame d = new GlobeFrame(w, h, CITIES[0][0], CITIES[0][1]);
        d.set(-17.0, 180.0, NEAR_KM);
        double[] lon = new double[(int) w];
        for (int i = 0; i < w; i++) lon[i] = d.latLonAt(i + 0.5, h / 2)[1];
        double[] step = new double[(int) w - 1];
        int seams = 0, seamAt0 = -1;
        for (int i = 0; i + 1 < w; i++) {
            step[i] = EarthProj.normaliseLon(lon[i + 1] - lon[i]);
            if (lon[i] > 0 && lon[i + 1] < 0) {
                seams++;
                seamAt0 = i;
            }
        }
        // ...a longitude's step across the seam is the neighbouring step's to within what any step differs from the next by (the parallel's curve).
        double seamJump = seamAt0 > 0 ? Math.abs(step[seamAt0] - step[seamAt0 - 1]) : Double.NaN, stepsWorst = 0;
        for (int i = 1; i + 1 < step.length; i++) if (i != seamAt0 && i - 1 != seamAt0) stepsWorst = Math.max(stepsWorst, Math.abs(step[i] - step[i - 1]));
        double[] eastOf = d.at(-17.0, -179.9995), westOf = d.at(-17.0, 179.9995);
        double apart = eastOf == null || westOf == null ? Double.NaN : Math.hypot(eastOf[0] - westOf[0], eastOf[1] - westOf[1]);
        double wantApart = 0.001 * (EarthProj.RADIUS_M * Math.PI / 180 * Math.cos(Math.toRadians(-17.0))) / (NEAR_KM * 1000);
        out.printf("      a view at %.3f km a pixel centred on longitude 180 at 17 S: %d seam, the longitude's step across it differing %.1e degrees from its neighbour's (%.1e at most elsewhere, of a step %.2e);"
                + " 0.001 degree across it %.3f px apart (%.3f asked)%n", NEAR_KM, seams, seamJump, stepsWorst, step[0], apart, wantApart);
        check("a view straddling the antimeridian draws it as longitude continuous: one seam, a pixel's longitude step across it as its neighbour's is elsewhere, two points a thousandth of a degree apart across it their pixel apart",
                seams == 1 && seamJump <= stepsWorst + 1e-12 && Math.abs(apart - wantApart) < 0.01 * wantApart);

        // The crossing drawn: the plane's ground and the GROUND tier's far rule over the same patch at the reach's edge.
        int n = MapFrame.NODE_PX;
        long seed = EarthTerrainCheck.SEED;
        double worstHandoff = 0, worstNear = 0;
        StringBuilder words = new StringBuilder();
        for (String name : List.of("Rotterdam", "Paris", "Perth")) {
            EarthTerrainCheck.Spot spot = spots.get(name);
            double[] city = GlobeFrame.destination(spot.lat(), spot.lon(), 0, GlobeFrame.REACH_KM * (1 + 1e-6));
            World.Key key = World.Key.earth(seed, city[0], city[1], id);
            double atHandoff = crossingShare(key, city[0], city[1], spot.lat(), spot.lon(), hk, n);
            double atNear = crossingShare(key, city[0], city[1], spot.lat(), spot.lon(), NEAR_KM, n);
            worstHandoff = Math.max(worstHandoff, atHandoff);
            worstNear = Math.max(worstNear, atNear);
            words.append(String.format("%n      %s's window REACH_KM from the city: %.2f%% of the %d x %d pixels differ at the handoff, %.2f%% at %.3f km a pixel (the forest folded into the grass)",
                    name, 100 * atHandoff, n, n, 100 * atNear, NEAR_KM));
        }
        if (full != null) {
            String fid = EarthTerrain.use(full);
            for (double[] at : new double[][] { { 51.90625, 4.48125 }, { 44.65, -63.57 }, { 14.60, 120.98 } }) {
                double[] city = GlobeFrame.destination(at[0], at[1], 0, GlobeFrame.REACH_KM * (1 + 1e-6));
                World.Key fkey = World.Key.earth(seed, city[0], city[1], fid);
                double atHandoff = crossingShare(fkey, city[0], city[1], at[0], at[1], hk, n);
                double atNear = crossingShare(fkey, city[0], city[1], at[0], at[1], NEAR_KM, n);
                worstHandoff = Math.max(worstHandoff, atHandoff);
                worstNear = Math.max(worstNear, atNear);
                words.append(String.format("%n      the full file at %.2f, %.2f, REACH_KM from the city: %.2f%% differ at the handoff, %.2f%% at %.3f km a pixel",
                        at[0], at[1], 100 * atHandoff, 100 * atNear, NEAR_KM));
            }
        } else {
            words.append(String.format("%n      earth-2.bin not named (-D%s=<path>) nor at %s: the full file's patches were not compared", EarthData.PROPERTY, EarthData.DATA_FILE));
        }
        out.println("      the ground the plane draws and the GROUND tier's far rule draws over one patch at the reach's edge:" + words);
        check("the crossing drawn at the handoff: the plane's ground and the GROUND tier's far rule part by under a thousandth of a patch's pixels, at the reach's edge as at the city (section 5)",
                worstHandoff <= CROSSING_HANDOFF_SHARE);
        check("...and at a near scale, where the plane draws a finer far node than the camera's least stride, under CROSSING_NEAR_SHARE of them - the coasts and rivers parting by a pixel or four, nothing more",
                worstNear <= CROSSING_NEAR_SHARE);

        // ...and the antimeridian over ground with a coast: the date line crosses Fiji's window.
        EarthTerrainCheck.Spot fiji = spots.get("Fiji");
        World.Key key = World.Key.earth(seed, CITIES[0][0], CITIES[0][1], id);
        double bestMix = -1, bestLat = fiji.lat();
        int vw = 256, vh = 128;
        for (double dl = -0.2; dl <= 0.2001; dl += 0.02) {
            GlobeFrame t = new GlobeFrame(vw, vh, CITIES[0][0], CITIES[0][1]);
            t.set(fiji.lat() + dl, 180.0, NEAR_KM);
            byte[] piece = t.groundPiece(key, 0, 0, vw, vh);
            double mix = mixed(piece, vw, vw / 2, vh);
            if (mix > bestMix) {
                bestMix = mix;
                bestLat = fiji.lat() + dl;
            }
        }
        GlobeFrame t = new GlobeFrame(vw, vh, CITIES[0][0], CITIES[0][1]);
        t.set(bestLat, 180.0, NEAR_KM);
        byte[] piece = t.groundPiece(key, 0, 0, vw, vh);
        int seamAt = -1;
        for (int i = 0; i + 1 < vw; i++) {
            double a = t.latLonAt(i + 0.5, vh / 2)[1], b = t.latLonAt(i + 1.5, vh / 2)[1];
            if (a > 0 && b < 0) seamAt = i;
        }
        double[] shareAt = new double[vw - 1];
        double otherWorst = 0, otherSum = 0;
        int others = 0;
        for (int i = 0; i + 1 < vw; i++) {
            int differ = 0;
            for (int j = 0; j < vh; j++) if (folded(piece[j * vw + i]) != folded(piece[j * vw + i + 1])) differ++;
            shareAt[i] = differ / (double) vh;
            if (i != seamAt) {
                otherWorst = Math.max(otherWorst, shareAt[i]);
                otherSum += shareAt[i];
                others++;
            }
        }
        int landLeft = 0, landRight = 0;
        for (int j = 0; j < vh; j++) {
            if (World.dry(piece[j * vw + seamAt])) landLeft++;
            if (World.dry(piece[j * vw + seamAt + 1])) landRight++;
        }
        out.printf("      Fiji's window at %.3f km a pixel centred on longitude 180 at %.3f S (the best of its coast): the seam between columns %d and %d, %d and %d of %d pixels land there;"
                + " the columns' classes differ %.1f%% across it, %.1f%% on average and %.1f%% at worst elsewhere%n",
                NEAR_KM, -bestLat, seamAt, seamAt + 1, landLeft, landRight, vh, 100 * shareAt[Math.max(0, seamAt)], 100 * otherSum / Math.max(1, others), 100 * otherWorst);
        check("fixture: the view straddles longitude 180 over ground with a coast - the seam's two columns each hold land and water", seamAt >= 0
                && landLeft > 0 && landLeft < vh && landRight > 0 && landRight < vh);
        check("the GROUND tier across the antimeridian, over that coast: the two pixel columns either side of the seam differ no more than adjacent columns elsewhere in the view",
                seamAt >= 0 && shareAt[seamAt] <= otherWorst);
        if (full != null) {
            String fid = EarthTerrain.use(full);
            World.Key fkey = World.Key.earth(seed, CITIES[0][0], CITIES[0][1], fid);
            // Chukotka (66 N, 180): the best latitude within a degree of it for a coast on the seam.
            double bestL = 66, bestM = -1;
            for (double dl = -1.0; dl <= 1.0001; dl += 0.05) {
                GlobeFrame u = new GlobeFrame(vw, vh, CITIES[0][0], CITIES[0][1]);
                u.set(66 + dl, 180.0, NEAR_KM);
                double mix = mixed(u.groundPiece(fkey, 0, 0, vw, vh), vw, vw / 2, vh);
                if (mix > bestM) {
                    bestM = mix;
                    bestL = 66 + dl;
                }
            }
            GlobeFrame u = new GlobeFrame(vw, vh, CITIES[0][0], CITIES[0][1]);
            u.set(bestL, 180.0, NEAR_KM);
            byte[] fp = u.groundPiece(fkey, 0, 0, vw, vh);
            int at = -1;
            for (int i = 0; i + 1 < vw; i++) {
                if (u.latLonAt(i + 0.5, vh / 2)[1] > 0 && u.latLonAt(i + 1.5, vh / 2)[1] < 0) at = i;
            }
            double seam = 0, elsewhere = 0;
            for (int i = 0; i + 1 < vw; i++) {
                int differ = 0;
                for (int j = 0; j < vh; j++) if (folded(fp[j * vw + i]) != folded(fp[j * vw + i + 1])) differ++;
                double share = differ / (double) vh;
                if (i == at) seam = share;
                else elsewhere = Math.max(elsewhere, share);
            }
            out.printf("      the full file, Chukotka at %.2f N on longitude 180: the columns' classes differ %.1f%% across the seam, %.1f%% at worst elsewhere (the seam's columns' mix %.2f)%n",
                    bestL, 100 * seam, 100 * elsewhere, bestM);
            check("the full file: the GROUND tier across the antimeridian over Chukotka's coast, the seam's columns differ no more than adjacent columns elsewhere", at >= 0 && bestM > 0 && seam <= elsewhere);
        } else {
            out.println("      earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": Chukotka's coast on the antimeridian was not read");
        }
    }

    /* =====================================================================
       2c. THE NEAR VIEW PAST THE REACH IS THE PLANE'S GROUND (MS2)
       ===================================================================== */

    /** Jerus's near scale, km a pixel: 2 km in the scale bar's 120 px (ui/MapView.SCALE_BAR_PX), 1.8 px a plot - the view of his two screenshots. */
    static final double JERUS_KM = 2.0 / 120;

    /**
     * At one plot a pixel (his scale and the closest) the classes the plane's nodes give through the camera are the plane's own, plot for plot: the share
     * of a view's pixels, either side of the reach, whose class is the class of the plot under them within 1e-5 (measured 1.0000000 on 1.1 million pixels
     * a view - the camera's pixel centres fall where the plane's plots are, and a centre exactly on a plot's edge may fall to the next).
     */
    static final double NEAR_PAST_SAME = 1 - 1e-5;

    /**
     * At 2, 4 and 8 plots a pixel - where the plane draws a far node a pixel of which is one sample of its 2, 4 or 8 plots, and the camera reads that node -
     * the share of pixels whose class is the class of the plot under their centre, the forest folded into the dry ground it stands on (its ranking at the
     * node's stride is not the plot's own): 98 percent (measured 98.6 to 99.8% over the fixture's windows and the full file's Baddeck, either side
     * of the reach - a coast's or a river's pixel samples the next plot of 2 to 8).
     */
    static final double NEAR_PAST_FOLDED = 0.98;

    /** ...and with the forest and the grass told apart: 90 percent (measured 91.6 to 98.3% on the fixture, 96.7 to 97.6% on the full file). */
    static final double NEAR_PAST_CLASSES = 0.90;

    /** The share of pixels whose class differs from both horizontal neighbours the reprojected nodes may draw over the plane's own: a tenth of a percent - the camera reads the same node pixels the plane does, turned by a few degrees. */
    static final double NEAR_PAST_SPECKLE = 0.001;

    /** The far rule's speckle on a forested window the fixture must cause, for the comparison to mean anything: 5 percent of the pixels (measured 15.2% over Rotterdam and 11.9% over Paris at Jerus's scale; the plane's 0.04%). */
    static final double FAR_SPECKLE_AT_LEAST = 0.05;

    /** A small lake the far rule draws as a disc is one whose pixels part from the equal-area disc about their centroid by no more than this share of their number: 2 percent (the far rule's discs measured 0.000 at 1.6 to 7.4 px a plot; a frayed lake's 0.07 to 0.5 and more). */
    static final double LAKE_IS_A_DISC = 0.02;

    /** ...and the plane's frayed one parts by at least this: 10 percent (its fray is BLOB_FRAY of its own noise: measured 0.10 to 0.23 over the lakes picked). */
    static final double LAKE_IS_FRAYED = 0.10;

    /** The reprojected lake is the plane's: their pixels' intersection over their union at one plot a pixel, within a thousandth of 1 (the share of section 2c's first figure). */
    static final double LAKE_SAME_IOU = 1 - 1e-3;

    /** ...and the far rule's disc is not the plane's lake: at most this (measured 0.55 to 0.85 over the lakes picked). */
    static final double LAKE_DISC_IOU = 0.9;

    /** Small lakes at least this many plots across the radius are tried (smaller are a few pixels at his scale), the least of them picked in each source: 12 plots (0.4 km2), four of them. */
    static final double LAKE_LEAST_PLOTS = 12;
    static final int LAKES_PICKED = 4;

    /** A lake view's side, pixels: 256, the lake's radius a quarter of it. */
    static final int LAKE_VIEW_PX = 256;

    /** The scales the views straddling the reach are drawn at, km a pixel: Jerus's (1.8 px a plot), then 2, 4 and 8 plots a pixel - NEAR_KM among them - and the closest, 16 px a plot. */
    static final double[] NEAR_SCALES = { JERUS_KM, NEAR_KM / 2, NEAR_KM, NEAR_KM * 2, GlobeFrame.leastKmPx() };

    /** A class as an int in the nodes ground() reads: the node's classes widened, so the camera's pixels are classes. */
    static int[] widen(byte[] classes) {
        int[] out = new int[classes.length];
        for (int i = 0; i < out.length; i++) out[i] = classes[i];
        return out;
    }

    /** What the camera draws of the plane's nodes (GlobeFrame.ground()) over a w x h view, a class a pixel (MapTiles.nodeTerrain()), NO_NODE where it drew none, OFF where it drew the far rule's place (beyond()) - and how many pixels found no node. */
    static final int NO_NODE = -1;
    static int[] cameraClasses(World.Key key, GlobeFrame f, int w, int h, int[] missing) {
        return cameraClasses(key, f, w, h, missing, false);
    }

    /** ...and, where `charts`, the view's chart nodes past the hand-over too (MS3: MapTiles.chartTerrain() at the view's chart and plots), so every pixel on the disc is drawn but for the nodes missing. */
    static int[] cameraClasses(World.Key key, GlobeFrame f, int w, int h, int[] missing, boolean charts) {
        long plots = MapFrame.nodePlotsAt(f.planeScale());
        GlobeFrame.Chart chart = f.chart();
        GlobeFrame.NodePixels made = (nx, ny) -> widen(MapTiles.chartTerrain(key, chart, plots, nx, ny, null));
        return cameraClassesWith(key, f, w, h, missing, charts ? made : null);
    }

    /** ...with the chart nodes made by `charts` (a node's classes widened; each made once), or none where it is null. */
    static int[] cameraClassesWith(World.Key key, GlobeFrame f, int w, int h, int[] missing, GlobeFrame.NodePixels charts) {
        long plots = MapFrame.nodePlotsAt(f.planeScale()), span = (long) MapFrame.NODE_PX * plots;
        Map<Long, int[]> nodes = new java.util.HashMap<>(), chartNodes = new java.util.HashMap<>();
        int[] px = new int[w * h];
        GlobeFrame.NodePixels held = charts == null ? null : (nx, ny) -> chartNodes.computeIfAbsent(MapTiles.key(nx, ny), k -> charts.at(nx, ny));
        missing[0] = f.ground(plots, (nx, ny) -> nodes.computeIfAbsent(MapTiles.key(nx, ny), k -> widen(MapTiles.nodeTerrain(key, nx * span, ny * span, plots))), held,
                (i, j, vx, vy, vz) -> NO_NODE, -2, px, w, 0, w, 0, h);
        return px;
    }

    /** The class of the plot under each pixel, as the plane's own tiles hold it (World.terrainAt(), stride one) - -2 off the planet. */
    static int[] plotClasses(World world, GlobeFrame f, int w, int h) {
        int[] out = new int[w * h];
        double[] xy = new double[2];
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) out[j * w + i] = f.planeAt(i + 0.5, j + 0.5, xy) ? world.terrainAt((long) Math.floor(xy[0]), (long) Math.floor(xy[1])) : -2;
        }
        return out;
    }

    /** What the plane draws over the same view at the same scale, its own frame unturned: each pixel the class of its far node's pixel at `plots` plots (planeClass()). */
    static int[] planeDraws(World.Key key, GlobeFrame f, int w, int h) {
        MapFrame mf = new MapFrame(w, h);
        mf.set(f.planeX(), f.planeY(), f.planeScale());
        long plots = MapFrame.nodePlotsAt(f.planeScale());
        Map<Long, byte[]> nodes = new java.util.HashMap<>();
        int[] out = new int[w * h];
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) out[j * w + i] = planeClass(key, plots, mf.plotX(i + 0.5), mf.plotY(j + 0.5), nodes);
        }
        return out;
    }

    /** The far rule's classes through the camera over the same view (GlobeFrame.groundPiece(): what the GROUND tier drew past the reach before MS2). */
    static int[] farRuleDraws(World.Key key, GlobeFrame f, int w, int h) {
        return widen(f.groundPiece(key, 0, 0, w, h));
    }

    /** The share of drawn pixels (a class, not NO_NODE or off the planet) whose class differs from both horizontal neighbours: the speckle of a dithered ground. */
    static double speckle(int[] a, int w, int h) {
        int isolated = 0, all = 0;
        for (int j = 0; j < h; j++) {
            for (int i = 1; i + 1 < w; i++) {
                int c = a[j * w + i];
                if (c < 0) continue;
                all++;
                if (c != a[j * w + i - 1] && c != a[j * w + i + 1]) isolated++;
            }
        }
        return all == 0 ? 0 : isolated / (double) all;
    }

    /** The share of pixels drawn in both (a class each) whose classes agree, the forest and the desert folded into the grass if `fold`; `inside` restricts them to those within the reach (true), past it (false), or all (null). */
    static double agree(int[] a, int[] b, boolean fold, GlobeFrame f, int w, Boolean inside) {
        long same = 0, all = 0;
        for (int k = 0; k < a.length; k++) {
            if (a[k] < 0 || b[k] < 0) continue;
            if (inside != null && f.reachHolds(k % w + 0.5, k / w + 0.5) != inside) continue;
            all++;
            if (fold ? folded((byte) a[k]) == folded((byte) b[k]) : a[k] == b[k]) same++;
        }
        return all == 0 ? Double.NaN : same / (double) all;
    }

    /** A lake's pixels - fresh water in `a` - their count, and how far they part from the equal-area disc about their centroid, as a share of their count: 0 for a disc, 2 at the most. */
    static double[] discParting(int[] a, int w, int h) {
        double n = 0, cx = 0, cy = 0;
        for (int j = 0; j < h; j++) for (int i = 0; i < w; i++) if (a[j * w + i] == World.FRESH) { n++; cx += i + 0.5; cy += j + 0.5; }
        if (n == 0) return new double[] { 0, Double.NaN };
        cx /= n;
        cy /= n;
        double r = Math.sqrt(n / Math.PI), parted = 0;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) if ((Math.hypot(i + 0.5 - cx, j + 0.5 - cy) <= r) != (a[j * w + i] == World.FRESH)) parted++;
        }
        return new double[] { n, parted / n };
    }

    /** Two views' fresh water, intersection over union. */
    static double freshIoU(int[] a, int[] b) {
        long both = 0, either = 0;
        for (int k = 0; k < a.length; k++) {
            boolean x = a[k] == World.FRESH, y = b[k] == World.FRESH;
            if (x && y) both++;
            if (x || y) either++;
        }
        return either == 0 ? 1 : both / (double) either;
    }

    /** One small lake picked: its place, its radius in plots, and what each rule drew of it. */
    record LakePick(String where, double lat, double lon, double plots, double[] plane, double[] camera, double[] far, double cameraIoU, double farIoU, boolean inside) { }

    /**
     * The small lakes of the 1-degree tiles round (lat, lon) within `withinKm` of it, at least LAKE_LEAST_PLOTS across: each viewed through the camera at the
     * scale that makes its radius a quarter of LAKE_VIEW_PX, the plane's own tiles, the camera's reprojected nodes and the far rule's disc drawn over the same
     * pixels. The picks are the first LAKES_PICKED whose far disc is a disc (LAKE_IS_A_DISC) and whose plane lake is frayed (LAKE_IS_FRAYED): a lake with another
     * water within its view is neither.
     */
    static List<LakePick> lakes(String where, EarthData data, World.Key key, double cityLat, double cityLon, double lat, double lon, double withinKm) {
        World world = World.of(key);
        EarthProj proj = world.earthTerrain().projection();
        double[] xy = new double[2];
        List<LakePick> picked = new java.util.ArrayList<>();
        for (int dl = -1; dl <= 1; dl++) {
            for (int dn = -1; dn <= 1; dn++) {
                EarthData.SmallLakes sl = data.smallLakes(EarthData.tile((int) Math.floor(lat) + dl, (int) Math.floor(lon) + dn));
                for (int k = 0; k < sl.size() && picked.size() < LAKES_PICKED; k++) {
                    double plots = Math.sqrt(sl.areaKm2(k) * 1e6 / Math.PI) / World.PLOT_M;
                    if (plots < LAKE_LEAST_PLOTS || EarthProj.distanceM(lat, lon, sl.lat(k), sl.lon(k)) / 1000 > withinKm || !proj.plotOf(sl.lat(k), sl.lon(k), xy)) continue;
                    GlobeFrame f = new GlobeFrame(LAKE_VIEW_PX, LAKE_VIEW_PX, cityLat, cityLon);
                    f.set(sl.lat(k), sl.lon(k), World.PLOT_M / (LAKE_VIEW_PX / 4.0 / plots) / 1000);
                    int[] none = new int[1];
                    int[] plane = plotClasses(world, f, LAKE_VIEW_PX, LAKE_VIEW_PX), camera = cameraClasses(key, f, LAKE_VIEW_PX, LAKE_VIEW_PX, none),
                            far = farRuleDraws(key, f, LAKE_VIEW_PX, LAKE_VIEW_PX);
                    double[] p = discParting(plane, LAKE_VIEW_PX, LAKE_VIEW_PX), c = discParting(camera, LAKE_VIEW_PX, LAKE_VIEW_PX), g = discParting(far, LAKE_VIEW_PX, LAKE_VIEW_PX);
                    if (!(g[1] <= LAKE_IS_A_DISC) || !(p[1] >= LAKE_IS_FRAYED) || none[0] != 0) continue;
                    picked.add(new LakePick(where, sl.lat(k), sl.lon(k), plots, p, c, g, freshIoU(camera, plane), freshIoU(far, plane), f.withinReach(sl.lat(k), sl.lon(k))));
                }
            }
        }
        return picked;
    }

    /** What the views straddling the reach drew, the worst of each figure (the lines print them). */
    static final class Near {
        /** The least share of pixels at one plot a pixel whose class is the plot's; at 2 to 8 plots a pixel the least folded, and the least told apart; the largest the camera's speckle goes over the plane's; the far rule's most at Jerus's scale; the widest the two sides of the reach part. */
        double same = 1, folded = 1, classes = 1, speckleOver = -1, farMost = 0, gap = 0;
        final List<String[]> lines = new java.util.ArrayList<>();

        /** The view centred at `at` at `km`, the city at `city` on the world `key`: its classes through the camera against the plane's own plots, the plane's own picture and the far rule's. */
        void view(String name, World.Key key, double[] city, double[] at, double km, int n, int m) {
            World world = World.of(key);
            GlobeFrame f = new GlobeFrame(n, m, city[0], city[1]);
            f.set(at[0], at[1], km);
            long plots = MapFrame.nodePlotsAt(f.planeScale());
            int[] none = new int[1];
            int[] cam = cameraClasses(key, f, n, m, none), own = plotClasses(world, f, n, m), pl = planeDraws(key, f, n, m), fr = farRuleDraws(key, f, n, m);
            double within = agree(cam, own, true, f, n, true), beyond = agree(cam, own, true, f, n, false), exact = agree(cam, own, false, f, n, null);
            double sCam = speckle(cam, n, m), sPlane = speckle(pl, n, m), sFar = speckle(fr, n, m);
            if (plots == 1) same = Math.min(same, Math.min(agree(cam, own, false, f, n, true), agree(cam, own, false, f, n, false)));
            else {
                folded = Math.min(folded, Math.min(within, beyond));
                classes = Math.min(classes, exact);
            }
            if (none[0] != 0) same = 0;
            gap = Math.max(gap, Math.abs(within - beyond));
            speckleOver = Math.max(speckleOver, sCam - sPlane);
            if (km == JERUS_KM) farMost = Math.max(farMost, sFar);
            lines.add(new String[] { name, String.format("%.5f", km), String.valueOf(plots), String.valueOf(none[0]), String.format("%.4f%%", 100 * within), String.format("%.4f%%", 100 * beyond),
                    String.format("%.3f%%", 100 * sPlane), String.format("%.3f%%", 100 * sCam), String.format("%.2f%%", 100 * sFar) });
        }
    }

    static void nearPastReach(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData data, EarthData full) {
        out.println("\n--- 2c. the near view past the reach is the plane's ground (MS2): the plane's own far nodes at the scale's stride through the camera wherever the square holds the ground,"
                + " not the far rule's dither and discs ---");
        double w = MapTiles.DESIGN_W, h = MapTiles.DESIGN_H, hk = GlobeFrame.HANDOFF_KM, least = GlobeFrame.leastKmPx();
        int n = (int) w, m = (int) h;

        // The rule: every scale closer than the handoff, the plane's nodes are drawn wherever the square holds the ground; the handoff and wider keep planeDrawsKm().
        GlobeFrame past = new GlobeFrame(w, h, CITIES[0][0], CITIES[0][1]);
        double[] far2000 = GlobeFrame.destination(CITIES[0][0], CITIES[0][1], 135, 2000);
        past.set(far2000[0], far2000[1], JERUS_KM);
        GlobeFrame wide = new GlobeFrame(w, h, CITIES[0][0], CITIES[0][1]);
        wide.set(far2000[0], far2000[1], hk);
        int onPlanet = 0, drawn = 0, holds = 0, wideHolds = 0;
        for (double sy = 0.5; sy < h; sy += 7) {
            for (double sx = 0.5; sx < w; sx += 7) {
                if (past.onPlanet(sx, sy)) onPlanet++;
                if (past.drawsHold(sx, sy)) drawn++;
                if (past.planeHolds(sx, sy)) holds++;
                if (wide.drawsHold(sx, sy) || wide.planeHolds(sx, sy)) wideHolds++;
            }
        }
        out.printf("      2,000 km from the city at %.4f km a pixel: %,d points on the planet, %,d drawn from the plane's nodes, %,d the plane's to hover and click; at the handoff %,d%n", JERUS_KM, onPlanet, drawn, holds, wideHolds);
        check("closer than the handoff - his scale, a quarter of the handoff and the closest - the plane's nodes are drawn wherever the square holds the ground, the reach and planeDrawsKm() or not;"
                + " the handoff keeps planeDrawsKm()",
                GlobeFrame.planeDrawnPastReach(JERUS_KM) && GlobeFrame.planeDrawnPastReach(NEAR_KM) && GlobeFrame.planeDrawnPastReach(least) && GlobeFrame.planeDrawnPastReach(hk / 1.0001)
                        && !GlobeFrame.planeDrawnPastReach(hk) && !GlobeFrame.planeDrawnPastReach(hk * 4) && past.planeDrawnPastReach() && !wide.planeDrawnPastReach()
                        && past.tier() == GlobeFrame.GROUND && !past.withinReach(far2000[0], far2000[1]) && onPlanet > 0 && drawn == onPlanet && holds == onPlanet && wideHolds == 0
                        && past.planeDrawsKm() == wide.planeDrawsKm());
        check("...a half-resolution copy of a view keeps its rule (the frame a moving view is drawn at is 2 x the km a pixel: past the handoff on its own)",
                GlobeFrame.planeDrawnPastReach(hk / 1.5) && !GlobeFrame.planeDrawnPastReach(hk / 1.5 * 2) && past.copy(w / 2, h / 2, hk / 1.5 * 2).planeDrawnPastReach()
                        && past.copy(w / 2, h / 2, hk / 1.5 * 2).drawsHold(w / 4, h / 4));
        check("...and the legend is the plane's at every scale closer than the handoff, past the reach too, the GROUND tier's from the handoff and the picture's wide",
                past.tier() == GlobeFrame.GROUND && past.legendTier() == GlobeFrame.PLANE && wide.legendTier() == GlobeFrame.GROUND
                        && new GlobeFrame(w, h, CITIES[0][0], CITIES[0][1]).legendTier() == GlobeFrame.PICTURE);

        // The square's edge (MS3): the north edge of the world's square is about 124 degrees from the city, past the hand-over, so the plane draws neither side of it and the charts draw both - the far rule's dither
        // that MS2 left off the square is gone at the plane's scales. A view straddling it.
        GlobeFrame edge = new GlobeFrame(256, 256, CITIES[0][0], CITIES[0][1]);
        double[] ll = new double[2];
        edge.plane().latLonOfPlot(World.SIDE / 2.0, 3, ll);
        edge.set(ll[0], ll[1], GlobeFrame.HANDOFF_KM / 8);
        int[] px = new int[256 * 256];
        int held = 0, farOnes = 0, mismatch = 0, planeAsked = 0, chartAsked = 0, unheld = 0;
        double[] xy = new double[2];
        final int chartMark = 77, planeMark = 33;
        int[] planeNode = new int[MapFrame.NODE_PX * MapFrame.NODE_PX], chartNode = new int[MapFrame.NODE_PX * MapFrame.NODE_PX];
        Arrays.fill(planeNode, planeMark);
        Arrays.fill(chartNode, chartMark);
        int[] asked = new int[2];
        int missed = edge.ground(MapFrame.nodePlotsAt(edge.planeScale()), (nx, ny) -> { asked[0]++; return planeNode; }, (nx, ny) -> { asked[1]++; return chartNode; }, (i, j, vx, vy, vz) -> NO_NODE, -2, px, 256, 0, 256, 0, 256);
        planeAsked = asked[0];
        chartAsked = asked[1];
        double cityKm = edge.cityKm(ll[0], ll[1]);
        for (int j = 0; j < 256; j++) {
            for (int i = 0; i < 256; i++) {
                boolean square = edge.plotAt(i + 0.5, j + 0.5, xy) && xy[0] >= 0 && xy[1] >= 0 && xy[0] < World.SIDE && xy[1] < World.SIDE, planet = edge.onPlanet(i + 0.5, j + 0.5);
                if (planet && (edge.drawsHold(i + 0.5, j + 0.5) || edge.planeHolds(i + 0.5, j + 0.5) || !edge.chartHolds(i + 0.5, j + 0.5) || px[j * 256 + i] != chartMark)) mismatch++;
                if (planet && px[j * 256 + i] == NO_NODE) unheld++;
                if (planet && square) held++;
                else if (planet) farOnes++;
            }
        }
        out.printf("      the world's square's north edge at %.3f km a pixel, %.0f degrees from the city (the hand-over is %.1f): the charts draw %,d pixels the square holds and %,d past it; %d disagreements, %d far-rule pixels, %d pixels found no node;"
                + " the plane was asked for %d nodes, the chart for %d%n", edge.kmPx(), Math.toDegrees(cityKm * 1000 / EarthProj.RADIUS_M), Math.toDegrees(StrictMath.acos(GlobeFrame.handoverUp())), held, farOnes, mismatch, unheld, missed, planeAsked, chartAsked);
        check("at the world's square's edge, past the hand-over, the charts draw both sides of it: chartHolds() and ground() agree, the plane draws neither side (drawsHold(), planeHolds(), no plane node asked for), no pixel is the far rule's"
                + " and none unheld", held > 1000 && farOnes > 1000 && mismatch == 0 && unheld == 0 && missed == 0 && planeAsked == 0 && chartAsked > 0 && cityKm > GlobeFrame.handoverKm());

        // The classes through the camera over a view straddling the reach: each of the fixture's windows, with the city its reach north of it - and on the
        // full file Cape Breton north of Baddeck, Jerus's two screenshots, the city at Halifax.
        Near near = new Near();
        List<LakePick> lakes = new java.util.ArrayList<>();
        for (String name : List.of("Rotterdam", "Paris", "Perth")) {
            EarthTerrainCheck.Spot spot = spots.get(name);
            double[] city = GlobeFrame.destination(spot.lat(), spot.lon(), 0, GlobeFrame.REACH_KM * (1 + 1e-6));
            World.Key key = World.Key.earth(EarthTerrainCheck.SEED, city[0], city[1], id);
            for (double km : NEAR_SCALES) near.view(name, key, city, new double[] { spot.lat(), spot.lon() }, km, n, m);
            lakes.addAll(lakes(name, data, key, city[0], city[1], spot.lat(), spot.lon(), 25));
        }
        if (full != null) {
            String fid = EarthTerrain.use(full);
            double[] halifax = CITIES[0], baddeck = { 46.10 + 37 / 111.2, -60.75 };
            double[] at = GlobeFrame.destination(halifax[0], halifax[1], GlobeFrame.bearing(halifax[0], halifax[1], baddeck[0], baddeck[1]), GlobeFrame.REACH_KM * (1 + 1e-6));
            World.Key key = World.Key.earth(EarthTerrainCheck.SEED, halifax[0], halifax[1], fid);
            for (double km : NEAR_SCALES) near.view("Baddeck", key, halifax, at, km, n, m);
            lakes.addAll(lakes("Baddeck", full, key, halifax[0], halifax[1], at[0], at[1], 40));
        } else {
            out.println("      earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": Cape Breton north of Baddeck was not drawn");
        }
        double worstSame = near.same, worstFolded = near.folded, worstClasses = near.classes, worstSpeckle = near.speckleOver, mostFar = near.farMost, worstGap = near.gap;
        out.printf("      %-9s %-8s %5s %5s  %-9s %-9s  %s%n", "window", "km/px", "plots", "none", "within", "past", "isolated pixels: the plane's, the reprojected nodes', the far rule's");
        for (String[] l : near.lines) out.printf("      %-9s %-8s %5s %5s  %-9s %-9s  %s %s %s%n", (Object[]) l);
        check("one plot a pixel (his scale and the closest): the classes the nodes give through the camera are the plane's own, plot for plot, on both sides of the reach - and no pixel without a node",
                worstSame >= NEAR_PAST_SAME);
        check("two, four and eight plots a pixel: within NEAR_PAST_FOLDED of the plane's own plots' classes (the forest folded), and within NEAR_PAST_CLASSES with it told apart",
                worstFolded >= NEAR_PAST_FOLDED && worstClasses >= NEAR_PAST_CLASSES);
        check("...and the two sides of the reach agree with the plane's plots as well as each other: no seam where the camera crosses it", worstGap < 0.01);
        check("not dithered: the reprojected nodes' pixels that differ from both horizontal neighbours are no more of them than the plane's own, to NEAR_PAST_SPECKLE",
                worstSpeckle <= NEAR_PAST_SPECKLE);
        check("...where the far rule's own speckle - what Jerus's second screenshot shows - is at least FAR_SPECKLE_AT_LEAST of the pixels over a forested window, so the fixture causes it", mostFar >= FAR_SPECKLE_AT_LEAST);

        // The small lakes: the plane's frayed lake, not the far rule's disc.
        out.printf("      small lakes where the far rule draws a disc and the plane a frayed lake (%d picked):%n", lakes.size());
        boolean same = !lakes.isEmpty(), notDisc = !lakes.isEmpty(), plane = !lakes.isEmpty(), disc = !lakes.isEmpty();
        for (LakePick l : lakes) {
            out.printf("      %-8s %.4f, %.4f (%s the reach), %.0f plots across: plane %,.0f px parted %.2f from a disc, camera %,.0f px parted %.2f (IoU with the plane's %.4f), far rule %,.0f px parted %.3f (IoU %.2f)%n",
                    l.where(), l.lat(), l.lon(), l.inside() ? "within" : "past", l.plots(), l.plane()[0], l.plane()[1], l.camera()[0], l.camera()[1], l.cameraIoU(), l.far()[0], l.far()[1], l.farIoU());
            same &= l.cameraIoU() >= LAKE_SAME_IOU;
            notDisc &= l.camera()[1] >= LAKE_IS_FRAYED;
            plane &= l.farIoU() <= LAKE_DISC_IOU;
            disc &= l.far()[1] <= LAKE_IS_A_DISC;
        }
        check("fixture: lakes where the far rule draws a perfect disc and the plane's lake is frayed are found - at least LAKES_PICKED of them in the windows", lakes.size() >= LAKES_PICKED && disc);
        check("a small lake through the camera is the plane's own lake, pixel for pixel at one plot a pixel (LAKE_SAME_IOU), frayed by its own noise - not the far rule's disc", same && notDisc && plane);

        // The cost: a frame of the camera over nodes held, the whole design view at his scale, on the main thread's CPU.
        EarthTerrainCheck.Spot rot = spots.get("Rotterdam");
        double[] city = GlobeFrame.destination(rot.lat(), rot.lon(), 0, GlobeFrame.REACH_KM * (1 + 1e-6));
        World.Key key = World.Key.earth(EarthTerrainCheck.SEED, city[0], city[1], id);
        GlobeFrame f = new GlobeFrame(w, h, city[0], city[1]);
        f.set(rot.lat(), rot.lon(), JERUS_KM);
        long plots = MapFrame.nodePlotsAt(f.planeScale()), span = (long) MapFrame.NODE_PX * plots;
        Map<Long, int[]> nodes = new java.util.HashMap<>();
        int[] frame = new int[n * m];
        double cpu = Double.MAX_VALUE, wall = Double.MAX_VALUE;
        for (int k = 0; k < 2 * FRAME_ROUNDS; k++) {
            long c0 = MapCheck.monthClock(), w0 = System.nanoTime();
            f.ground(plots, (nx, ny) -> nodes.computeIfAbsent(MapTiles.key(nx, ny), q -> widen(MapTiles.nodeTerrain(key, nx * span, ny * span, plots))), (i, j, vx, vy, vz) -> NO_NODE, -2, frame, n, 0, n, 0, m);
            long c1 = MapCheck.monthClock(), w1 = System.nanoTime();
            if (k >= FRAME_ROUNDS) {
                cpu = Math.min(cpu, (c1 - c0) / 1e6);
                wall = Math.min(wall, (w1 - w0) / 1e6);
            }
        }
        out.printf("      a frame %d x %d at %.4f km a pixel, %d nodes held: %.1f ms of %s (%.1f ms wall) on one thread%n", n, m, JERUS_KM, nodes.size(), cpu,
                MapCheck.CPU_TIMED ? "the main thread's CPU" : "the wall clock", wall);
        check("a frame of the plane's nodes through the camera at his scale is drawn in no more than FRAME_MS of " + (MapCheck.CPU_TIMED ? "the main thread's CPU" : "the wall clock"),
                Timing.within(cpu, FRAME_MS) && nodes.size() > 4);
    }

    /* =====================================================================
       2d. THE SLANTED GROUND, MEASURED, AND FIXED BY THE CHARTS (MS3)
       ===================================================================== */

    /** The offset the ground's change rates are read at, in pixels: 4 - short enough that the share of pixel pairs that differ grows with the offset's length across a patch of the 2 km view's 20 to 60 px. */
    static final int STREAK_LAG_PX = 4;

    /** The side of the views the ground's streak is read over, in pixels: 512 (the fixture's windows hold the 2c view of 1,345 x 806 at his scale, and this is a fraction of it). */
    static final int STREAK_VIEW_PX = 512;

    /** The distances the city is placed from the spot, in km: just past the reach (the spot's reference, where Lambert's plane is true to a thousandth) and 2,000 to 12,000 km, where it squashes the ground by 1.03 to 2.9 to 1. */
    static final double[] STREAK_KM = { GlobeFrame.REACH_KM * (1 + 1e-6), 2000, 4000, 6000, 8000, 10000, 12000 };

    /** The measure reads a field of no stretch as 1 within this, and a stretched one at most this above its stretch: 0.10 - set before the first run. */
    static final double STREAK_READ_TOL = 0.10;

    /** ...and a stretched field at least this share of its stretch: 0.65 - the measure's own reading, set from the calibration and not to pass a case: it reads 4 to 1 at 0.683 of it on the equal-area fields (lag saturation at STREAK_LAG_PX against patches 3.6 to 29 px), rounded down to the next 0.05, and it reads a stretch lower as it grows, which is why the cause is read against response(), not sigma; a floor there only guards against a blind measure. */
    static final double STREAK_READ_FLOOR = 0.65;

    /** ...and the field's direction within this many degrees of the stretch's, for a stretch of at least 2: 5. */
    static final double STREAK_PHI_DEG = 5;

    /** The synthetic fields a stretch's response is averaged over, and the fields of the plane's noise a placement's: 8 (one field's E varies by a tenth). */
    static final int STREAK_SEEDS = 8;

    /** The plane's noise under the pixels is stretched as the measure reads Lambert's projection stretch it: its E over its E at the reach is within this share of response(sigma) / response(1), at every placement; and the drawn ground's E / E_ref is at most this share above it - 0.15, set before the first run. */
    static final double STREAK_TRACKS = 0.15;

    /** Where the distortion is at least STREAK_FROM_SIGMA the drawn ground's streak runs along the line from the spot to the city to within this many degrees: 10. */
    static final double STREAK_BEARING_DEG = 10;

    /** ...the shape distortion from which the streak's direction and its rise are asserted: 1.5 (the patches half again as long one way as the other). */
    static final double STREAK_FROM_SIGMA = 1.5;

    /** A ground read at this E / E_ref or more is visibly streaked: 1.5 - the measure reads Jerus's screenshot, Goose Bay from Singapore, at 2.4, and a window at 12,000 km at 1.6 to 2.0. */
    static final double STREAK_SHOWN = 1.5;

    /** The stretches the measure is calibrated on: 1, 2 and 4 to 1. */
    static final double[] STREAK_STRETCHES = { 1, 2, 4 };

    /** ...and once a stretch of STREAK_TURNED_S to 1 turned STREAK_TURN_DEG degrees on the screen: 3 to 1, 30 degrees. */
    static final double STREAK_TURNED_S = 3, STREAK_TURN_DEG = 30;

    /** The synthetic fields' noise seed. */
    static final long STREAK_SEED = 0x5EED_2DL;

    /** Singapore (Jerus's city, the brief's) and Happy Valley-Goose Bay, Labrador, whose place Goose Bay (GOOSE_BAY_NORTH_KM due north of it) is. */
    static final double[] SINGAPORE = { 1.2903, 103.8519 }, HAPPY_VALLEY = { 53.3017, -60.3261 };

    /** ...the distance due north of Happy Valley-Goose Bay Jerus's hover named, in km: 42. */
    static final double GOOSE_BAY_NORTH_KM = 42;

    /** A city where the plane's up is some degrees from north a little way out, for the roll's sign: Tromso (the view is put the reach's edge due east of it). */
    static final double[] TROMSO = { 69.65, 18.96 };

    /** An angle folded into [0, 180). */
    static double fold180(double deg) { return ((deg % 180) + 180) % 180; }

    /** The angle between two directions given as angles folded into [0, 180), 0 to 90. */
    static double apart180(double a, double b) {
        double d = Math.abs(fold180(a) - fold180(b));
        return Math.min(d, 180 - d);
    }

    /**
     * The ground's elongation from its change rates: for four offsets A = (lag, 0), B = (0, lag), C = (k, k), D = (k, -k), k = round(lag / sqrt 2) (screen x
     * right, y down) the share p of pixel pairs - both drawn, class >= 0 - whose classes differ, and r = p over the offset's length. A field stretched s to 1
     * changes at r(phi) = r0 sqrt(cos^2(phi) / s^2 + sin^2(phi)) about the stretch's axis, so r(phi)^2 = a + b cos 2phi + c sin 2phi, with
     * a = the mean of the four r^2, b = (rA^2 - rB^2) / 2, c = (rC^2 - rD^2) / 2 and g = sqrt(b^2 + c^2); then E = sqrt((a + g) / (a - g)) (infinite where a <= g)
     * and the streak runs where the change is least, phi = 0.5 atan2(-c, -b) in degrees from +x toward +y, folded into [0, 180).
     * Returns { E, phi, pairs counted in A }.
     */
    static double[] streak(int[] cls, int w, int h, int lag) {
        int k = (int) Math.round(lag / Math.sqrt(2));
        int[][] offsets = { { lag, 0 }, { 0, lag }, { k, k }, { k, -k } };
        double[] r = new double[4];
        long pairsInA = 0;
        for (int q = 0; q < 4; q++) {
            int dx = offsets[q][0], dy = offsets[q][1];
            long pairs = 0, differ = 0;
            for (int j = Math.max(0, -dy); j < Math.min(h, h - dy); j++) {
                for (int i = Math.max(0, -dx); i < Math.min(w, w - dx); i++) {
                    int x = cls[j * w + i], y = cls[(j + dy) * w + i + dx];
                    if (x < 0 || y < 0) continue;
                    pairs++;
                    if (x != y) differ++;
                }
            }
            if (q == 0) pairsInA = pairs;
            r[q] = pairs == 0 ? Double.NaN : differ / (double) pairs / Math.hypot(dx, dy);
        }
        double mean = (r[0] * r[0] + r[1] * r[1] + r[2] * r[2] + r[3] * r[3]) / 4, b = (r[0] * r[0] - r[1] * r[1]) / 2, c = (r[2] * r[2] - r[3] * r[3]) / 2, g = Math.hypot(b, c);
        double e = mean > 0 ? (mean <= g ? Double.POSITIVE_INFINITY : Math.sqrt((mean + g) / (mean - g))) : Double.NaN;
        return new double[] { e, fold180(Math.toDegrees(0.5 * Math.atan2(-c, -b))), pairsInA };
    }

    /** The streak's direction on the planet, degrees clockwise from north folded into [0, 180): a screen direction beta clockwise from up has the bearing beta - roll (GlobeFrame.roll() is the angle clockwise from the view's up to north), and phi from +x toward +y is 90 + phi clockwise from up. */
    static double streakBearing(double phi, double roll) { return fold180(90 + phi - roll); }

    /** Snyder's shape distortion of Lambert's azimuthal equal-area projection at `km` along the great circle from its centre, k / h = (1 / cos(c / 2)) / cos(c / 2) = 2 / (1 + cos c) - an independent textbook formula, as 1's "Snyder's textbook projection" is. */
    static double planeDistortion(double km) { return 2 / (1 + Math.cos(km * 1000 / EarthProj.RADIUS_M)); }

    /** The octave streams of one synthetic field's noise, EarthTerrain.noiseAt()'s `os`: the harness's own seed, the field's number and the octave (NOISE_LO to NOISE_HI, as the rule's). */
    static long[] fieldSeeds(int field) {
        long[] os = new long[EarthTerrain.NOISE_HI + 1];
        for (int k = EarthTerrain.NOISE_LO; k <= EarthTerrain.NOISE_HI; k++) os[k] = World.mix(STREAK_SEED ^ (field * 0x9E3779B97F4A7C15L) ^ (k * 0x632BE59BD9B4E019L));
        return os;
    }

    /** The class of a value: 1 where it is below the median of `values` (the pixels in `used`), 0 above, -2 where unused - the rule's patches ("noise below its threshold"), half the pixels each. */
    static int[] belowMedian(double[] values, boolean[] used) {
        int n = 0;
        for (boolean u : used) if (u) n++;
        double[] sorted = new double[n];
        for (int k = 0, q = 0; k < values.length; k++) if (used[k]) sorted[q++] = values[k];
        Arrays.sort(sorted);
        double median = n == 0 ? 0 : sorted[n / 2];
        int[] cls = new int[values.length];
        for (int k = 0; k < cls.length; k++) cls[k] = !used[k] ? -2 : values[k] < median ? 1 : 0;
        return cls;
    }

    /** A class field of side x side pixels at JERUS_KM from the rule's own noise (EarthTerrain.noiseAt() at a stride of one, its octaves a plot to 16 plots): its patches s to 1 at the same area, as an equal-area projection makes round patches (Lambert's does: along its stretch the noise is read at 1 / sqrt(s) of its pixel scale, across at sqrt(s) of it - x / sqrt(s), y x sqrt(s) before the noise is read), turned `turnDeg` degrees clockwise on the screen, thresholded at its median. */
    static int[] stretchedField(long[] os, int side, double s, double turnDeg) {
        double plotsPx = 1000 * JERUS_KM / World.PLOT_M, t = Math.toRadians(turnDeg), ct = Math.cos(t), st = Math.sin(t);
        double[] v = new double[side * side];
        boolean[] used = new boolean[v.length];
        Arrays.fill(used, true);
        for (int j = 0; j < side; j++) {
            for (int i = 0; i < side; i++) {
                double x = i + 0.5, y = j + 0.5, u = x * ct + y * st, w = -x * st + y * ct;
                v[j * side + i] = EarthTerrain.noiseAt(os, u * plotsPx / Math.sqrt(s), w * plotsPx * Math.sqrt(s), 1);
            }
        }
        return belowMedian(v, used);
    }

    /** What response() has read, by stretch and turn. */
    private static final Map<String, double[]> RESPONSES = new java.util.HashMap<>();

    /** The measure's reading of a stretch of s to 1 at the same area (stretchedField(): the equal-area form Lambert's projection gives round patches) turned `turnDeg` degrees: { the mean E of STREAK_SEEDS synthetic fields of the rule's noise, their mean direction (folded into [0, 180)) } - a stretch is read low as it grows, so the cause is read against this, not against sigma. */
    static double[] response(double s, double turnDeg) {
        String key = Math.round(s * 1000) + "/" + turnDeg;
        double[] got = RESPONSES.get(key);
        if (got != null) return got;
        double sum = 0, cs = 0, sn = 0;
        for (int k = 0; k < STREAK_SEEDS; k++) {
            double[] m = streak(stretchedField(fieldSeeds(k), STREAK_VIEW_PX, s, turnDeg), STREAK_VIEW_PX, STREAK_VIEW_PX, STREAK_LAG_PX);
            sum += m[0];
            cs += Math.cos(Math.toRadians(2 * m[1]));
            sn += Math.sin(Math.toRadians(2 * m[1]));
        }
        got = new double[] { sum / STREAK_SEEDS, fold180(Math.toDegrees(0.5 * Math.atan2(sn, cs))) };
        RESPONSES.put(key, got);
        return got;
    }

    /** ...a stretch along x: the mean E alone. */
    static double response(double s) { return response(s, 0)[0]; }

    /**
     * One placement of the city against a spot: what the plane's own plots show of the ground there (ePlots, the cause), what ground() draws with the charts
     * (eDrawn, the fix), and the noise under the pixels - the plane's (eNoise) and the sphere's, which the charts read (eSphere); `same` is the share of the pixels the plane
     * draws (`plane` of them) that are the plane's own plots, NaN where it draws none.
     */
    record Slant(String spot, double km, double sigma, double ePlots, double phiPlots, double eDrawn, double bearing, double toCity, double roll, double same, double held, int missing,
                 double eNoise, double eLakes, double eSphere, double plane) { }

    /** The synthetic fields of the sphere's noise a placement's E is averaged over: 4 of the STREAK_SEEDS (its noise costs twice the plane's a pixel). */
    static final int SPHERE_SEEDS = 4;

    /** The city at `city`, the view at `at` at JERUS_KM, STREAK_VIEW_PX square: the streak of the plane's own plots under each pixel and of what ground() draws with the charts (every pixel on the planet drawn), the lakes' alone (fresh water against the rest); and the mean E of STREAK_SEEDS fields of the rule's noise read at the plot under each pixel, and of SPHERE_SEEDS fields of the sphere's noise at the pixel's point (what the charts read). */
    static Slant slantAt(String spot, String dataId, double[] at, double[] city) {
        int n = STREAK_VIEW_PX;
        World.Key key = World.Key.earth(EarthTerrainCheck.SEED, city[0], city[1], dataId);
        GlobeFrame f = new GlobeFrame(n, n, city[0], city[1]);
        f.set(at[0], at[1], JERUS_KM);
        int[] none = new int[1];
        int[] cam = cameraClasses(key, f, n, n, none, true), own = plotClasses(World.of(key), f, n, n);
        long held = 0, planeDrawn = 0, planeSame = 0;
        boolean[] used = new boolean[own.length];
        double[] px = new double[own.length], py = new double[own.length], xy = new double[2];
        double[] sx = new double[own.length], sy = new double[own.length], sz = new double[own.length];
        double rPlots = EarthProj.RADIUS_M / World.PLOT_M;
        int[] lakes = new int[own.length];
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                int k = j * n + i;
                if (cam[k] < 0) own[k] = -2;
                else {
                    held++;
                    if (f.drawsHold(i + 0.5, j + 0.5)) {
                        planeDrawn++;
                        if (cam[k] == own[k]) planeSame++;
                    }
                }
                if (own[k] >= 0 && f.planeAt(i + 0.5, j + 0.5, xy)) {
                    used[k] = true;
                    px[k] = Math.floor(xy[0]) + 0.5;
                    py[k] = Math.floor(xy[1]) + 0.5;
                    double[] ll = f.latLonAt(i + 0.5, j + 0.5);
                    double phi = Math.toRadians(ll[0]), lam = Math.toRadians(ll[1]);
                    sx[k] = Math.cos(phi) * Math.cos(lam) * rPlots;
                    sy[k] = Math.cos(phi) * Math.sin(lam) * rPlots;
                    sz[k] = Math.sin(phi) * rPlots;
                }
                lakes[k] = own[k] < 0 ? own[k] : own[k] == World.FRESH ? 1 : 0;
            }
        }
        double noise = 0, sphere = 0;
        double[] v = new double[own.length];
        for (int q = 0; q < STREAK_SEEDS; q++) {
            long[] os = fieldSeeds(q);
            for (int k = 0; k < v.length; k++) if (used[k]) v[k] = EarthTerrain.noiseAt(os, px[k], py[k], 1);
            noise += streak(belowMedian(v, used), n, n, STREAK_LAG_PX)[0];
            if (q < SPHERE_SEEDS) {
                for (int k = 0; k < v.length; k++) if (used[k]) v[k] = EarthTerrain.sphereNoise(os, sx[k], sy[k], sz[k], 1);
                sphere += streak(belowMedian(v, used), n, n, STREAK_LAG_PX)[0];
            }
        }
        double[] p = streak(own, n, n, STREAK_LAG_PX), c = streak(cam, n, n, STREAK_LAG_PX);
        double km = EarthProj.distanceM(city[0], city[1], at[0], at[1]) / 1000;
        return new Slant(spot, km, planeDistortion(km), p[0], p[1], c[0], streakBearing(p[1], f.roll()), fold180(GlobeFrame.bearing(at[0], at[1], city[0], city[1])), f.roll(),
                planeDrawn == 0 ? Double.NaN : planeSame / (double) planeDrawn, held / (double) own.length, none[0], noise / STREAK_SEEDS, streak(lakes, n, n, STREAK_LAG_PX)[0], sphere / SPHERE_SEEDS,
                planeDrawn / (double) own.length);
    }

    static void slanted(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full) {
        out.println("\n--- 2d. the slanted ground (MS3): the streak Jerus's screenshot shows, as a number: the plane's shape distortion makes it (not the turn, not the node lookup), and the charts past the hand-over draw it true in shape ---");
        int side = STREAK_VIEW_PX;

        // The measure calibrated on fields of known stretch: the rule's own noise at this scale, STREAK_SEEDS fields a stretch.
        StringBuilder read = new StringBuilder();
        double none = response(1);
        boolean reads = Math.abs(none - 1) <= STREAK_READ_TOL;
        read.append(String.format("%n      no stretch: E %.3f (within %.2f of 1: %s)", none, STREAK_READ_TOL, reads ? "read" : "MISSED"));
        for (double s : STREAK_STRETCHES) {
            if (s == 1) continue;
            double[] m = response(s, 0);
            boolean ok = m[0] >= STREAK_READ_FLOOR * s && m[0] <= (1 + STREAK_READ_TOL) * s && apart180(m[1], 0) <= STREAK_PHI_DEG;
            reads &= ok;
            read.append(String.format("%n      stretched %.0f to 1 along x: E %.3f (%.2f to %.2f of it), direction %.1f degrees from +x (%s)", s, m[0], STREAK_READ_FLOOR * s, (1 + STREAK_READ_TOL) * s, m[1], ok ? "read" : "MISSED"));
        }
        double[] mt = response(STREAK_TURNED_S, STREAK_TURN_DEG);
        boolean turnedOk = mt[0] >= STREAK_READ_FLOOR * STREAK_TURNED_S && mt[0] <= (1 + STREAK_READ_TOL) * STREAK_TURNED_S && apart180(mt[1], STREAK_TURN_DEG) <= STREAK_PHI_DEG;
        reads &= turnedOk;
        read.append(String.format("%n      stretched %.0f to 1, turned %.0f degrees: E %.3f (%.2f to %.2f of it), direction %.1f degrees from +x (%s)", STREAK_TURNED_S, STREAK_TURN_DEG, mt[0],
                STREAK_READ_FLOOR * STREAK_TURNED_S, (1 + STREAK_READ_TOL) * STREAK_TURNED_S, mt[1], turnedOk ? "read" : "MISSED"));
        // A field stretched along y (screen up-down) on a north-up view runs north-south; a rolled view turns the bearing by the roll.
        double[] my = response(STREAK_TURNED_S, 90);
        GlobeFrame northUp = new GlobeFrame(side, side, CITIES[0][0], CITIES[0][1]);
        northUp.set(CITIES[0][0], CITIES[0][1], GlobeFrame.HANDOFF_KM * 8);
        double northSouth = apart180(streakBearing(my[1], northUp.roll()), 0);
        GlobeFrame rolled = new GlobeFrame(side, side, TROMSO[0], TROMSO[1]);
        double[] edge = GlobeFrame.destination(TROMSO[0], TROMSO[1], 90, GlobeFrame.REACH_KM + 1);
        rolled.set(edge[0], edge[1], JERUS_KM);
        double rollWorst = 0;
        for (double beta : new double[] { 0, 40, 90, 135 }) {
            double[] centre = rolled.latLonAt(side / 2.0, side / 2.0), along = rolled.latLonAt(side / 2.0 + 20 * Math.cos(Math.toRadians(beta)), side / 2.0 + 20 * Math.sin(Math.toRadians(beta)));
            rollWorst = Math.max(rollWorst, apart180(GlobeFrame.bearing(centre[0], centre[1], along[0], along[1]), streakBearing(beta, rolled.roll())));
        }
        out.printf("      the measure on the rule's own noise (EarthTerrain.noiseAt() at %.4f km a pixel, the mean of %d fields a stretch), lag %d px, %d x %d:%s%n", JERUS_KM, STREAK_SEEDS, STREAK_LAG_PX, side, side, read);
        out.printf("      a field stretched along y on a north-up view (roll %.1f): bearing %.2f degrees from north-south; a view of roll %.2f degrees (Tromso, the reach east): the bearing of a screen direction"
                + " beta is beta - roll within %.4f degrees%n", northUp.roll(), northSouth, rolled.roll(), rollWorst);
        check("the measure, calibrated on the rule's own noise: no stretch reads 1 within STREAK_READ_TOL, a stretch of 2 or 4 (and 3 turned 30 degrees) between STREAK_READ_FLOOR and 1 + STREAK_READ_TOL of it,"
                + " in its direction within STREAK_PHI_DEG - it reads a stretch low as it grows (lag saturation), so the cause below is read against response(), not sigma",
                reads);
        check("...and a north-up view's bearing of a field stretched along y is north-south, and a rolled view's bearing is the screen direction less the roll (streakBearing()'s sign)",
                northUp.roll() == 0 && northSouth <= STREAK_PHI_DEG && Math.abs(rolled.roll()) > 1 && rollWorst < 0.05);

        // The plane's ground along a line from the city out: the windows, the city due south of the spot, and on the full file Goose Bay, the city on the way to Singapore.
        List<List<Slant>> placed = new java.util.ArrayList<>();
        for (String name : List.of("Rotterdam", "Paris", "Perth")) {
            EarthTerrainCheck.Spot spot = spots.get(name);
            double[] at = { spot.lat(), spot.lon() };
            List<Slant> row = new java.util.ArrayList<>();
            for (double km : STREAK_KM) row.add(slantAt(name, id, at, GlobeFrame.destination(at[0], at[1], 180, km)));
            placed.add(row);
        }
        List<Slant> goose = null;
        if (full != null) {
            String fid = EarthTerrain.use(full);
            double[] at = GlobeFrame.destination(HAPPY_VALLEY[0], HAPPY_VALLEY[1], 0, GOOSE_BAY_NORTH_KM);
            double toward = GlobeFrame.bearing(at[0], at[1], SINGAPORE[0], SINGAPORE[1]);
            goose = new java.util.ArrayList<>();
            for (double km : STREAK_KM) goose.add(slantAt("Goose Bay", fid, at, GlobeFrame.destination(at[0], at[1], toward, km)));
            goose.add(slantAt("Goose Bay", fid, at, SINGAPORE));
            placed.add(goose);
        } else {
            out.println("      earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": Goose Bay from Singapore was not drawn; the windows' 12,000 km is the farthest");
        }
        // The view's corner is this far from its centre, km: a placement is "past" a ring only by more than it.
        double viewReachKm = STREAK_VIEW_PX * JERUS_KM * Math.sqrt(2) / 2, handKm = GlobeFrame.handoverKm(), bandKm = GlobeFrame.SHAPE_BAND_KM;
        out.printf("      %-9s %8s %6s %8s %7s %8s %7s  %-6s %-6s %6s  %-8s %-6s %-6s  %7s %7s %7s  (E of the plane's plots under each pixel, E / E_ref, E of what ground() draws, charts included, E / E_ref, the plots' streak's bearing,"
                + " the bearing to the city, the roll, the share of the pixels the plane draws that are its own plots, the share of the pixels drawn, the share the plane draws, the plane's noise's E over its E at the reach,"
                + " response(sigma) / response(1), the sphere's noise's E over its E at the reach)%n",
                "spot", "city km", "sigma", "E plots", "E/Eref", "E drawn", "d/dref", "streak", "city", "roll", "same", "held", "plane", "noise", "resp", "sphere");
        double worstNoise = 0, worstAbove = -1, worstBearing = 0, worstSame = 1, worstRoll = 0, fixtureShown = 0, gooseShown = Double.POSITIVE_INFINITY;
        double worstTrue = 0, mostDrawn = 0, gooseTrue = Double.NaN;
        int missing = 0, bearingsRead = 0, falls = 0, pastBand = 0, withinHand = 0;
        for (List<Slant> row : placed) {
            double eRef = row.get(0).ePlots(), dRef = row.get(0).eDrawn(), nRef = row.get(0).eNoise(), sRef = row.get(0).eSphere(), rRef = response(1), last = 0;
            for (Slant s : row) {
                double ratio = s.ePlots() / eRef, drawn = s.eDrawn() / dRef, noise = s.eNoise() / nRef, resp = response(s.sigma()) / rRef;
                out.printf("      %-9s %8.0f %6.3f %8.3f %7.3f %8.3f %7.3f  %6.1f %6.1f %6.2f  %-8s %.4f %.4f  %7.3f %7.3f %7.3f%n", s.spot(), s.km(), s.sigma(), s.ePlots(), ratio, s.eDrawn(), drawn, s.bearing(), s.toCity(), s.roll(),
                        Double.isNaN(s.same()) ? "-" : String.format("%.6f", s.same()), s.held(), s.plane(), noise, resp, s.eSphere() / sRef);
                worstNoise = Math.max(worstNoise, Math.abs(noise / resp - 1));
                worstAbove = Math.max(worstAbove, ratio / resp - 1);
                if (s.sigma() >= STREAK_FROM_SIGMA) {
                    worstBearing = Math.max(worstBearing, apart180(s.bearing(), s.toCity()));
                    bearingsRead++;
                    if (!(ratio > last)) falls++;
                    last = ratio;
                }
                if (s.km() + viewReachKm <= handKm) {
                    withinHand++;
                    worstSame = Math.min(worstSame, s.same());
                }
                if (s.km() - viewReachKm > handKm + bandKm) {
                    pastBand++;
                    worstTrue = Math.max(worstTrue, Math.abs(drawn - 1));
                    if (row == goose) gooseTrue = Math.max(Double.isNaN(gooseTrue) ? 0 : gooseTrue, Math.abs(drawn - 1));
                }
                mostDrawn = Math.max(mostDrawn, drawn);
                missing += s.missing();
                if (s.km() >= 2000) worstRoll = Math.max(worstRoll, Math.abs(s.roll()));
            }
            Slant far = row.get(row.size() - 1);
            if (row == goose) gooseShown = far.ePlots() / eRef;
            else if (far.km() > STREAK_KM[STREAK_KM.length - 1] - 1) fixtureShown = Math.max(fixtureShown, far.ePlots() / eRef);
        }
        out.printf("      the plane's noise off response(sigma) / response(1) by %.3f of it at worst; the plane's own plots %.3f above it at most; their streak %.1f degrees off the line to the city at the %d placements of sigma %.1f and more,"
                + " %d of them not above the last; farthest placement E / E_ref %.3f on the fixture's best window, %s;"
                + " roll %.3g at 2,000 km and farther%n", worstNoise, worstAbove, worstBearing, bearingsRead, STREAK_FROM_SIGMA, falls, fixtureShown,
                goose != null ? String.format("%.3f at Goose Bay from Singapore", gooseShown) : "Goose Bay from Singapore not drawn", worstRoll);
        out.printf("      the fix: %d placements within the hand-over (%.0f km) draw the plane's own plots, %.7f of the pixels the plane draws at worst; %d past it and its %.0f km band: the drawn ground's E / E_ref off 1 by %.3f at worst%s,"
                + " %.3f at most over all placements; %d pixels without a node%n", withinHand, handKm, worstSame, pastBand, bandKm, worstTrue, goose != null ? String.format(" (%.3f at Goose Bay)", gooseTrue) : "", mostDrawn, missing);
        if (goose != null) {
            Slant reach = goose.get(0), farthest = goose.get(goose.size() - 1);
            out.printf("      Goose Bay from Singapore, lakes only (fresh water against the rest): E %.3f at the reach, %.3f at Singapore, %.3f over it; all the classes' plots %.3f at the reach, %.3f at Singapore, %.3f over it;"
                    + " all the classes drawn %.3f at the reach, %.3f at Singapore, %.3f over it%n",
                    reach.eLakes(), farthest.eLakes(), farthest.eLakes() / reach.eLakes(), reach.ePlots(), farthest.ePlots(), farthest.ePlots() / reach.ePlots(), reach.eDrawn(), farthest.eDrawn(), farthest.eDrawn() / reach.eDrawn());
        }

        check("the cause - the plane's own noise: the noise under each pixel is stretched exactly as Lambert's projection stretches it - at every placement its E over its E at the reach is within STREAK_TRACKS of"
                + " response(sigma) / response(1)", worstNoise <= STREAK_TRACKS);
        check("...the plane's own plots streak along the line to the city, and more as sigma grows: from sigma 1.5 their streak is within STREAK_BEARING_DEG of the line, their E / E_ref rises at each such placement over the last,"
                + " and it is never more than STREAK_TRACKS above response(sigma) / response(1) (the cells are true in shape on the sphere, only the noise stretches)",
                bearingsRead > 0 && worstBearing <= STREAK_BEARING_DEG && falls == 0 && worstAbove <= STREAK_TRACKS);
        check("...the fixture causes it: the plane's plots' E / E_ref is at least STREAK_SHOWN at 12,000 km on at least one fixture window" + (goose != null ? " and at Goose Bay from Singapore" : ""),
                fixtureShown >= STREAK_SHOWN && (goose == null || gooseShown >= STREAK_SHOWN));
        check("not the pixel lookup (MS3: restricted to the plane's own ground, within the hand-over): at every placement within GlobeFrame.handoverKm() of the city the plane's nodes through the camera draw the plane's own plots"
                + " at one plot a pixel, untouched by the charts - no axis mixed, no level misread - and no pixel at any placement without a node",
                withinHand >= 3 && worstSame >= NEAR_PAST_SAME && missing == 0);
        check("not the turn: the turn is done - the roll is 0 (north up) at every placement 2,000 km and farther; and a turn cannot stretch anyway (E is the same under a rotation, the turned field above)",
                worstRoll == 0);
        check("the fix - past the hand-over the drawn ground is true in shape: its E / E_ref is within STREAK_READ_TOL of 1 at every placement past GlobeFrame.SHAPE_HANDOVER and the band (SHAPE_BAND_KM)"
                + (goose != null ? ", Goose Bay from Singapore among them" : "") + "; and no placement's drawn E / E_ref is over SHAPE_HANDOVER x (1 + STREAK_TRACKS)",
                pastBand >= 3 && worstTrue <= STREAK_READ_TOL && (goose == null || (!Double.isNaN(gooseTrue) && gooseTrue <= STREAK_READ_TOL)) && mostDrawn <= GlobeFrame.SHAPE_HANDOVER * (1 + STREAK_TRACKS));

        seams(spots, id, full);
        spread();
        chartTiming(spots, id, full);
    }

    /** The change rate of pixel pairs across the hand-over's line (or the band's far edge), over the rate on one side, may exceed the same figure of the same pixels drawn by the chart alone by this share: 0.15 - the most the brief allowed; the measure reads 1.04 to 1.11 on the fixture windows and near Goose Bay, and 2.5 to 3.5 for a seam made on purpose (SEAM_SHOWN). */
    static final double SEAM_TOL = 0.15;

    /** The class shares of the ground either side of the hand-over's line (water, forest, grass, the rest), pooled over the views, differ by at most this: 0.05. */
    static final double SHARE_TOL = 0.05;

    /** The bearings the city is put at from the view's centre, degrees, to pool a seam over: the four screen directions - a pixel is on the plane's side in half of them and the chart's in the other, so the ground each side holds is the same ground. */
    static final double[] SEAM_BEARINGS = { 0, 90, 180, 270 };

    /** The group a class counts in for the shares: water (salt, shallow, fresh), forest, grass, the rest. */
    static int shareGroup(int cls) {
        return cls == World.SALT || cls == World.SHALLOW || cls == World.FRESH ? 0 : cls == World.FOREST ? 1 : cls == World.GRASS ? 2 : 3;
    }

    /** What a seam view reads: the class changes across the line and away from it, in the drawn ground and in its twin - the same pixels drawn by the chart alone with one noise across the line, so continuous across it - and the shares each side of the drawn ground. */
    static final class Seam {
        /** Pairs at the offset that cross the line [0] and that lie on one side [1], and how many of them change class: [0] the drawn ground, [1] the twin. */
        final long[][] pairs = new long[2][2], differ = new long[2][2];
        /** Pixels each side by group, [side][group]: side 0 nearer the city. */
        final long[][] shares = new long[2][4];

        /** One view at `at` shifted `shiftKm` along the line, the city `d` km from it on `bearing`: the line is the circle d km from the city. The charts are blended in as GlobeFrame blends them, or (`abrupt`) not at all. Adds to the counts. */
        void add(String dataId, double[] at, double d, double bearing, double shiftKm, boolean abrupt) {
            int n = STREAK_VIEW_PX;
            double[] centre = shiftKm == 0 ? at : GlobeFrame.destination(at[0], at[1], bearing + 90, shiftKm);
            double[] city = GlobeFrame.destination(centre[0], centre[1], bearing, d);
            World.Key key = World.Key.earth(EarthTerrainCheck.SEED, city[0], city[1], dataId);
            GlobeFrame f = new GlobeFrame(n, n, city[0], city[1]);
            f.set(centre[0], centre[1], JERUS_KM);
            long plots = MapFrame.nodePlotsAt(f.planeScale());
            GlobeFrame.Chart chart = f.chart();
            int[] none = new int[1];
            GlobeFrame.NodePixels real = (nx, ny) -> widen(MapTiles.chartTerrain(key, chart, plots, nx, ny, null)), blunt = (nx, ny) -> widen(chartNode(key, chart, plots, nx, ny, GlobeFrame.handoverUp(), 0));
            int[] cam = cameraClassesWith(key, f, n, n, none, abrupt ? blunt : real);
            // The twin: the chart alone over every pixel, its noise the plane's everywhere at the hand-over (t = 0), the sphere's everywhere at the band's far edge (t = 1).
            boolean near = d <= GlobeFrame.handoverKm() + 1e-6;
            double twinUp = near ? -1 : 2;
            int[] ref = chartOnly(f, n, n, (nx, ny) -> widen(chartNode(key, chart, plots, nx, ny, twinUp, 0)));
            // The screen direction to the city, as an integer offset STREAK_LAG_PX long.
            double beta = Math.toRadians(GlobeFrame.bearing(centre[0], centre[1], city[0], city[1]) + f.roll());
            int dx = (int) Math.round(STREAK_LAG_PX * Math.sin(beta)), dy = (int) Math.round(-STREAK_LAG_PX * Math.cos(beta));
            int[] side = new int[cam.length];
            for (int j = 0; j < n; j++) {
                for (int i = 0; i < n; i++) {
                    double[] ll = f.latLonAt(i + 0.5, j + 0.5);
                    side[j * n + i] = ll == null || cam[j * n + i] < 0 || ref[j * n + i] < 0 ? -1 : f.cityKm(ll[0], ll[1]) < d ? 0 : 1;
                    if (side[j * n + i] >= 0) shares[side[j * n + i]][shareGroup(cam[j * n + i])]++;
                }
            }
            for (int j = Math.max(0, -dy); j < Math.min(n, n - dy); j++) {
                for (int i = Math.max(0, -dx); i < Math.min(n, n - dx); i++) {
                    int p = j * n + i, q = (j + dy) * n + i + dx;
                    if (side[p] < 0 || side[q] < 0) continue;
                    int kind = side[p] != side[q] ? 0 : 1;
                    pairs[0][kind]++;
                    pairs[1][kind]++;
                    if (cam[p] != cam[q]) differ[0][kind]++;
                    if (ref[p] != ref[q]) differ[1][kind]++;
                }
            }
        }

        /** The change rate across the line over the rate on one side, of the drawn ground (which 0) or the twin (1). */
        double rate(int which) { return (differ[which][0] / (double) pairs[which][0]) / (differ[which][1] / (double) pairs[which][1]); }

        /** The drawn ground's ratio over the twin's: the same pixels, so the ground's own lines (a coast or river along the line) cancel. */
        double ratio() { return rate(0) / rate(1); }

        /** The most any group's share differs between the sides. */
        double shareGap() {
            double gap = 0, a = 0, b = 0;
            for (int g = 0; g < 4; g++) {
                a += shares[0][g];
                b += shares[1][g];
            }
            for (int g = 0; g < 4; g++) gap = Math.max(gap, Math.abs(shares[0][g] / a - shares[1][g] / b));
            return gap;
        }

        String sharesWords() {
            double a = 0, b = 0;
            for (int g = 0; g < 4; g++) {
                a += shares[0][g];
                b += shares[1][g];
            }
            StringBuilder s = new StringBuilder();
            for (int g = 0; g < 4; g++) s.append(String.format("%s %.3f/%.3f", new String[] { "water", "forest", "grass", "rest" }[g], shares[0][g] / a, shares[1][g] / b)).append(g < 3 ? ", " : "");
            return s.toString();
        }
    }

    /** A chart node with its blend set by hand (MapTiles.chartTerrain() sets it by GlobeFrame's dials): the sphere's noise from the angle whose cosine is `fromUp`, over a band of `bandRad` - nought for none, a seam made on purpose; fromUp -1 for the plane's noise everywhere, 2 for the sphere's. */
    static byte[] chartNode(World.Key key, GlobeFrame.Chart chart, long plots, long nx, long ny, double fromUp, double bandRad) {
        byte[] out = new byte[MapFrame.NODE_PX * MapFrame.NODE_PX];
        EarthTerrain e = World.of(key).earthTerrain();
        e.farTerrain(e.chart(chart.ortho(), (int) plots, nx, ny, fromUp, bandRad), out, null);
        return out;
    }

    /** A view's pixels all read from its chart's nodes (GlobeFrame.chartAt()), as ground() reads them where the chart draws: the plane's nodes not used. -2 off the planet. */
    static int[] chartOnly(GlobeFrame f, int w, int h, GlobeFrame.NodePixels nodes) {
        Map<Long, int[]> held = new java.util.HashMap<>();
        double[] xy = new double[2];
        int n = MapFrame.NODE_PX;
        int[] out = new int[w * h];
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                if (!f.chartAt(i + 0.5, j + 0.5, xy)) {
                    out[j * w + i] = -2;
                    continue;
                }
                long nx = (long) Math.floor(xy[0] / n), ny = (long) Math.floor(xy[1] / n);
                int[] node = held.computeIfAbsent(MapTiles.key(nx, ny), k -> nodes.at(nx, ny));
                int u = (int) (xy[0] - nx * n), v = (int) (xy[1] - ny * n);
                out[j * w + i] = node[(v < n ? v : n - 1) * n + (u < n ? u : n - 1)];
            }
        }
        return out;
    }

    /** How far north of Jerus's hover (GOOSE_BAY_NORTH_KM from Happy Valley) the seam views near Goose Bay are centred, km: 60 - his own spot lies at the edge of Hamilton Inlet's 40 km wide "river" run of the data, which both rules paint, the plane in its own plots and the chart in the planet's metric: at the hand-over (k / h = 1.25) the plane's edge of a run that wide lies up to a quarter of its 20 km half-width off the true one, so a circle through it shows the edge jump, a limit MS3 names (its notes, 2) and does not measure as a seam; 60 km north the ground is the Labrador forest and lakes the screenshot shows. */
    static final double SEAM_NORTH_KM = 60;

    /** The shifts along the line the views are taken at, as shares of the view's width: either side of the centre, so the ground the views hold is more than the one window's centre. */
    static final double[] SEAM_SHIFTS = { -0.5, 0.5 };

    /** A seam made on purpose (the sphere's noise switched on at the hand-over with no band) reads at least this: 1.5 - so the measure can see one. Set from the measure. */
    static final double SEAM_SHOWN = 1.5;

    /** The seams (MS3): no line where the plane's ground hands over to the chart's, nor where the sphere's noise has been blended in whole - at the hand-over's circle and the band's far edge, on each fixture window and near Goose Bay - read against the same pixels drawn by the plane's own plots, and against a seam made on purpose. */
    static void seams(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full) {
        out.println("      no seam, at both ends of the band (" + STREAK_VIEW_PX + " px views centred on the circle, the city at " + SEAM_BEARINGS.length + " bearings and " + SEAM_SHIFTS.length + " places along the line, pooled): the change rate of pixel pairs STREAK_LAG_PX apart"
                + " across the line over the rate on one side, the drawn ground's over its twin's - the same pixels drawn by the chart alone with one noise across the line (so a coast along it cancels), and the class shares each side (nearer the city / farther):");
        double worstRatio = 0, worstGap = 0, leastControl = Double.POSITIVE_INFINITY;
        int views = 0;
        List<String[]> spotsAt = new java.util.ArrayList<>();
        for (String name : List.of("Rotterdam", "Paris", "Perth")) spotsAt.add(new String[] { name, id, String.valueOf(spots.get(name).lat()), String.valueOf(spots.get(name).lon()) });
        if (full != null) {
            double[] at = GlobeFrame.destination(HAPPY_VALLEY[0], HAPPY_VALLEY[1], 0, GOOSE_BAY_NORTH_KM + SEAM_NORTH_KM);
            spotsAt.add(new String[] { "Goose Bay", EarthTerrain.use(full), String.valueOf(at[0]), String.valueOf(at[1]) });
        }
        double width = STREAK_VIEW_PX * JERUS_KM;
        for (String[] sp : spotsAt) {
            double[] at = { Double.parseDouble(sp[2]), Double.parseDouble(sp[3]) };
            for (double d : new double[] { GlobeFrame.handoverKm(), GlobeFrame.handoverKm() + GlobeFrame.SHAPE_BAND_KM }) {
                Seam s = new Seam();
                for (double b : SEAM_BEARINGS) for (double sh : SEAM_SHIFTS) s.add(sp[1], at, d, b, sh * width, false);
                views++;
                worstRatio = Math.max(worstRatio, s.ratio());
                worstGap = Math.max(worstGap, s.shareGap());
                out.printf("      %-9s %s of the band (%.0f km): across/one side %.3f drawn, %.3f twin, = %.3f (%,d pairs across); shares %s%n", sp[0], d == GlobeFrame.handoverKm() ? "near edge" : "far edge", d,
                        s.rate(0), s.rate(1), s.ratio(), s.pairs[0][0], s.sharesWords());
            }
            Seam control = new Seam();
            for (double b : SEAM_BEARINGS) for (double sh : SEAM_SHIFTS) control.add(sp[1], at, GlobeFrame.handoverKm(), b, sh * width, true);
            leastControl = Math.min(leastControl, control.ratio());
            out.printf("      %-9s a seam made on purpose at the hand-over (no band): %.3f drawn, %.3f twin, = %.3f%n", sp[0], control.rate(0), control.rate(1), control.ratio());
        }
        check("no seam where the plane hands over to the charts, nor where the sphere's noise is blended in whole: on a fixture window and near Goose Bay, at both ends of the band, the class changes across the line are at most"
                + " 1 + SEAM_TOL of the rate on one side, read against the same pixels drawn by the chart alone", views >= 6 && worstRatio <= 1 + SEAM_TOL);
        check("...the measure sees one: the sphere's noise switched on at the hand-over with no band reads at least SEAM_SHOWN on every window", leastControl >= SEAM_SHOWN);
        check("...and the class shares (water, forest, grass, the rest) either side of the line are within SHARE_TOL of each other", views >= 6 && worstGap <= SHARE_TOL);
    }

    /** The points the sphere's noise spread is read over: 2^16. */
    static final int SPREAD_POINTS = 1 << 16;

    /** The sphere's noise's standard deviation is within this share of the plane's: 0.02. */
    static final double SPREAD_TOL = 0.02;

    /** ...and the share of its points under the ranking table's value for p (EarthTerrain.below()) is within this of p, and of the plane's: 0.01. */
    static final double RANK_TOL = 0.01;

    /** The ranks the shares are read at. */
    static final double[] RANK_P = { 0.05, 0.25, 0.5, 0.75, 0.95 };

    /** The sphere's noise keeps the plane's spread (MS3): the ranking table and the frays' rules, written for noiseAt(), hold for it. */
    static void spread() {
        double rPlots = EarthProj.RADIUS_M / World.PLOT_M;
        int n = SPREAD_POINTS;
        double worstSd = 0, worstRank = 0, worstApart = 0;
        for (int q = 0; q < STREAK_SEEDS; q++) {
            long[] os = fieldSeeds(q);
            double[] plane = new double[n], sphere = new double[n];
            for (int k = 0; k < n; k++) {
                long h = World.mix(STREAK_SEED ^ (q * 0x2545F4914F6CDD1DL) ^ (k * 0x9E3779B97F4A7C15L));
                long hx = World.mix(h ^ 1), hy = World.mix(h ^ 2);
                plane[k] = EarthTerrain.noiseAt(os, (hx >>> 41) + 0.5, (hy >>> 41) + 0.5, 1);
                double z = 2 * World.unit(World.mix(h ^ 3)) - 1, lam = 2 * Math.PI * World.unit(World.mix(h ^ 4)), r = Math.sqrt(1 - z * z);
                sphere[k] = EarthTerrain.sphereNoise(os, r * Math.cos(lam) * rPlots, r * Math.sin(lam) * rPlots, z * rPlots, 1);
            }
            double sdPlane = sd(plane), sdSphere = sd(sphere);
            worstSd = Math.max(worstSd, Math.abs(sdSphere / sdPlane - 1));
            for (double p : RANK_P) {
                double t = EarthTerrain.below(p), a = share(plane, t), b = share(sphere, t);
                worstRank = Math.max(worstRank, Math.max(Math.abs(a - p), Math.abs(b - p)));
                worstApart = Math.max(worstApart, Math.abs(a - b));
            }
        }
        out.printf("      the sphere's noise over %,d points a field (%d fields) against the plane's over %,d plot centres: standard deviation off the plane's by %.4f of it at worst (SPHERE_GAIN %.4f); the share under the ranking table's value for each of %s:"
                + " %.4f off p at worst, the two fields %.4f apart%n", n, STREAK_SEEDS, n, worstSd, EarthTerrain.SPHERE_GAIN, Arrays.toString(RANK_P), worstRank, worstApart);
        check("the sphere's noise keeps the plane's spread: over 2^16 sampled points its standard deviation is within SPREAD_TOL of the plane's noiseAt() over 2^16 plot centres, and each field's ranks give the same shares (the ranking table's"
                + " quantiles) within RANK_TOL", worstSd <= SPREAD_TOL && worstApart <= RANK_TOL && worstRank <= RANK_TOL);
    }

    static double sd(double[] v) {
        double m = 0, s = 0;
        for (double x : v) m += x;
        m /= v.length;
        for (double x : v) s += (x - m) * (x - m);
        return Math.sqrt(s / v.length);
    }

    /** The share of `v` under t. */
    static double share(double[] v, double t) {
        int k = 0;
        for (double x : v) if (x < t) k++;
        return k / (double) v.length;
    }

    /** The cost of the charts (MS3): a chart node (classes, relief, colours) at Goose Bay at one plot a pixel within MapCheck.SCREEN_MS, and a frame of 2d's view over held chart nodes within FRAME_MS - the main thread's CPU, as section 7 reads it. */
    static void chartTiming(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full) {
        String where = full != null ? "Goose Bay" : "Rotterdam";
        double[] at, city;
        String dataId;
        if (full != null) {
            at = GlobeFrame.destination(HAPPY_VALLEY[0], HAPPY_VALLEY[1], 0, GOOSE_BAY_NORTH_KM);
            city = SINGAPORE;
            dataId = EarthTerrain.use(full);
        } else {
            at = new double[] { spots.get("Rotterdam").lat(), spots.get("Rotterdam").lon() };
            city = GlobeFrame.destination(at[0], at[1], 180, 12000);
            dataId = id;
        }
        World.Key key = World.Key.earth(EarthTerrainCheck.SEED, city[0], city[1], dataId);
        int n = STREAK_VIEW_PX;
        GlobeFrame f = new GlobeFrame(n, n, city[0], city[1]);
        f.set(at[0], at[1], JERUS_KM);
        long plots = MapFrame.nodePlotsAt(f.planeScale());
        GlobeFrame.Chart chart = f.chart();
        double[] cxy = new double[2];
        f.chartAt(n / 2.0, n / 2.0, cxy);
        long nx = (long) Math.floor(cxy[0] / MapFrame.NODE_PX), ny = (long) Math.floor(cxy[1] / MapFrame.NODE_PX);
        byte[] relief = new byte[MapFrame.NODE_PX * MapFrame.NODE_PX];
        int[] colours = new int[MapFrame.NODE_PX * MapFrame.NODE_PX];
        double nodeCpu = Double.MAX_VALUE;
        for (int k = 0; k < 2 * FRAME_ROUNDS; k++) {
            long c0 = MapCheck.monthClock();
            byte[] g = MapTiles.chartTerrain(key, chart, plots, nx + k % 2, ny, relief);
            MapTiles.chartPixels(g, relief, colours);
            long c1 = MapCheck.monthClock();
            if (k >= FRAME_ROUNDS) nodeCpu = Math.min(nodeCpu, (c1 - c0) / 1e6);
        }
        out.printf("      a chart node at %s at %d plot a pixel, %s the city (chart %d at %.0f, %.0f): classes, relief and colours in %.1f ms of %s%n", where, plots, full != null ? "Singapore" : "12,000 km from", chart.id(), chart.lat(), chart.lon(),
                nodeCpu, MapCheck.CPU_TIMED ? "the main thread's CPU" : "the wall clock");
        check("a chart node (classes, relief, colours) at " + where + " at one plot a pixel is made in no more than MapCheck.SCREEN_MS of " + (MapCheck.CPU_TIMED ? "the main thread's CPU" : "the wall clock"),
                Timing.within(nodeCpu, MapCheck.SCREEN_MS) && plots == 1);
        // A frame of ground() over the chart nodes held: every node the view needs made once, then the frame drawn over them.
        Map<Long, int[]> held = new java.util.HashMap<>();
        int[] frame = new int[n * n];
        double cpu = Double.MAX_VALUE, wall = Double.MAX_VALUE;
        for (int k = 0; k < 2 * FRAME_ROUNDS; k++) {
            long c0 = MapCheck.monthClock(), w0 = System.nanoTime();
            f.ground(plots, (cx, cy) -> null, (cx, cy) -> held.computeIfAbsent(MapTiles.key(cx, cy), q -> {
                int[] px = new int[MapFrame.NODE_PX * MapFrame.NODE_PX];
                MapTiles.chartPixels(MapTiles.chartTerrain(key, chart, plots, cx, cy, null), null, px);
                return px;
            }), (i, j, vx, vy, vz) -> NO_NODE, -2, frame, n, 0, n, 0, n);
            long c1 = MapCheck.monthClock(), w1 = System.nanoTime();
            if (k >= FRAME_ROUNDS) {
                cpu = Math.min(cpu, (c1 - c0) / 1e6);
                wall = Math.min(wall, (w1 - w0) / 1e6);
            }
        }
        out.printf("      a frame %d x %d of ground() over %d chart nodes held: %.1f ms of %s (%.1f ms wall) on one thread%n", n, n, held.size(), cpu, MapCheck.CPU_TIMED ? "the main thread's CPU" : "the wall clock", wall);
        check("a ground() frame of 2d's view over held chart nodes is drawn in no more than FRAME_MS of " + (MapCheck.CPU_TIMED ? "the main thread's CPU" : "the wall clock"), Timing.within(cpu, FRAME_MS) && held.size() > 1);
    }

    /* =====================================================================
       3. THE HANDOFF
       ===================================================================== */

    /** The most the view and the plane it hands to part, in pixels, every `step` pixels across it and its edges. */
    static double parting(GlobeFrame f, int step) {
        EarthProj p = f.plane();
        double s = f.planeScale(), x0 = f.planeX(), y0 = f.planeY(), worst = 0;
        double[] xy = new double[2];
        int w = (int) f.width(), h = (int) f.height();
        for (int j = 0; j <= h; j += step) {
            for (int i = 0; i <= w; i += step) {
                double[] ll = f.latLonAt(i, j);
                if (ll == null || !p.plotOf(ll[0], ll[1], xy)) continue;
                worst = Math.max(worst, Math.hypot((xy[0] - x0) * s + f.width() / 2 - i, (xy[1] - y0) * s + f.height() / 2 - j));
            }
        }
        return worst;
    }

    /** The study's measure (globe-prototype.py's reach_px): the globe north up turned by the plane's up and scaled to the plane's mean scale against the plane round the view centre, px. */
    static double studyParting(GlobeFrame f, double lat, double lon, double kmPx) {
        double w = f.width(), h = f.height(), m = kmPx * 1000, g = Math.toRadians(f.planeRollAt(lat, lon));
        EarthProj.Ortho o = EarthProj.ortho(lat, lon, 0);
        EarthProj p = f.plane();
        double[] cv = new double[2], xy = new double[2], ll = new double[2];
        p.forward(lat, lon, cv);
        int n = (int) w * (int) h, k = 0;
        double[] px = new double[n], py = new double[n], gx = new double[n], gy = new double[n];
        double sp = 0, sg = 0;
        for (int j = 0; j < (int) h; j++) {
            for (int i = 0; i < (int) w; i++) {
                double x = (i + 0.5 - w / 2) * m, y = (h / 2 - j - 0.5) * m;
                if (!o.inverse(x, y, ll) || !p.forward(ll[0], ll[1], xy)) continue;
                px[k] = xy[0] - cv[0];
                py[k] = xy[1] - cv[1];
                gx[k] = x * Math.cos(g) + y * Math.sin(g);
                gy[k] = y * Math.cos(g) - x * Math.sin(g);
                sp += Math.hypot(px[k], py[k]);
                sg += Math.hypot(gx[k], gy[k]);
                k++;
            }
        }
        double s = sp / sg, worst = 0;
        for (int q = 0; q < k; q++) worst = Math.max(worst, Math.hypot(px[q] - s * gx[q], py[q] - s * gy[q]));
        return worst / m;
    }

    static void handoff() {
        out.println("\n--- 3. the handoff: the globe and the city's plane at the swap, facing the city and at the reach ---");
        double w = MapTiles.DESIGN_W, h = MapTiles.DESIGN_H, hk = GlobeFrame.HANDOFF_KM;
        boolean under = true, overAtTwice = true;
        StringBuilder table = new StringBuilder();
        for (double[] c : CITIES) {
            GlobeFrame f = new GlobeFrame(w, h, c[0], c[1]), g = new GlobeFrame(FOUNDING_W, FOUNDING_H, c[0], c[1]);
            for (int doubling = 0; doubling <= 4; doubling++) {
                double k = hk * (1 << doubling);
                f.set(c[0], c[1], k);
                g.set(c[0], c[1], k);
                double pf = parting(f, 1), pg = parting(g, 1);
                if (doubling == 0) under &= pf < 0.5;
                if (doubling == 1) overAtTwice &= pf > 0.5;
                if (c == CITIES[0]) table.append(String.format("%s%.2f km %.2f px (%.2f)", table.length() == 0 ? "" : ", ", k, pf, pg));
            }
        }
        out.printf("      facing the city, the expanded map %d x %d (the founding globe %d x %d): %s - the study 0.34 (0.02), 1.37 (0.08), 5.59 (0.34)%n",
                (int) w, (int) h, (int) FOUNDING_W, (int) FOUNDING_H, table);
        check("facing the city the plane and the globe part by under half a pixel on the expanded map at HANDOFF_PLOTS", under);
        check("...and by more at twice it: the handoff is the widest power of two under half a pixel", overAtTwice);

        double atReach = 0, northUp = 0, studyAtReach = 0;
        for (double[] c : CITIES) {
            GlobeFrame f = new GlobeFrame(w, h, c[0], c[1]);
            f.setReach(2 * TABLE_KM[TABLE_KM.length - 1]);
            StringBuilder line = new StringBuilder();
            for (double km : TABLE_KM) {
                double built = 0, study = 0;
                for (double b : BEARINGS) {
                    double[] v = GlobeFrame.destination(c[0], c[1], b, km);
                    f.set(v[0], v[1], hk);
                    built = Math.max(built, parting(f, 2));
                    study = Math.max(study, studyParting(f, v[0], v[1], hk));
                }
                line.append(String.format("%s%.0f km %.2f px (the study's measure %.2f)", line.length() == 0 ? "" : ", ", km, built, study));
            }
            out.printf("      round %.2f, %.2f at the handoff with the roll eased: %s - the study 0.36, 1.16, 2.3, 6.7%n", c[0], c[1], line);
            GlobeFrame g = new GlobeFrame(w, h, c[0], c[1]);
            for (double b : BEARINGS) {
                double[] v = atReach(c, b);
                g.set(v[0], v[1], hk);
                atReach = Math.max(atReach, parting(g, 2));
                studyAtReach = Math.max(studyAtReach, studyParting(g, v[0], v[1], hk));
                // the same view north up: the globe not turned to the plane
                northUp = Math.max(northUp, northUpParting(g, v[0], v[1]));
            }
        }
        out.printf("      at REACH_KM (%.0f km) with the roll eased %.3f px at worst (the study's measure %.3f); north up %.1f px%n",
                GlobeFrame.REACH_KM, atReach, studyAtReach, northUp);
        check("at REACH_KM, turned to the plane's up, the swap parts by under REACH_PARTING_PX", atReach < REACH_PARTING_PX);
        check("...where north up it parts by more than ten times that: the roll is what makes it fit", northUp > 10 * REACH_PARTING_PX);
    }

    /** The view at (lat, lon) north up against the plane, px. */
    static double northUpParting(GlobeFrame f, double lat, double lon) {
        EarthProj.Ortho o = EarthProj.ortho(lat, lon, 0);
        EarthProj p = f.plane();
        double s = f.planeScale(), x0 = f.planeX(), y0 = f.planeY(), m = f.kmPx() * 1000, worst = 0;
        double[] xy = new double[2], ll = new double[2];
        for (int j = 0; j <= (int) f.height(); j += 4) {
            for (int i = 0; i <= (int) f.width(); i += 4) {
                if (!o.inverse((i - f.width() / 2) * m, (f.height() / 2 - j) * m, ll) || !p.plotOf(ll[0], ll[1], xy)) continue;
                worst = Math.max(worst, Math.hypot((xy[0] - x0) * s + f.width() / 2 - i, (xy[1] - y0) * s + f.height() / 2 - j));
            }
        }
        return worst;
    }

    /* =====================================================================
       4. THE PLANE'S CAMERA IS TODAY'S FAR NODES
       ===================================================================== */

    /** Every stride from `from` to the widest the expanded map draws, a far node aligned on the spot's and one centred on it; then, `world`, the whole world's nodes at the widest - their CRC32. */
    static long fingerprint(World.Key key, int from, boolean world) {
        CRC32 crc = new CRC32();
        int n = MapFrame.NODE_PX;
        int widest = (int) new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H).nodePlots();
        long spot = EarthTerrain.spotPlot();
        for (int s = from; s <= widest; s *= 2) {
            long span = (long) n * s, aligned = Math.floorDiv(spot, span) * span, centred = spot - span / 2;
            crc.update(MapTiles.nodeTerrain(key, aligned, aligned, s));
            crc.update(MapTiles.nodeTerrain(key, centred, centred, s));
        }
        if (world) {
            long span = (long) n * widest;
            int across = (int) ((World.SIDE + span - 1) / span);
            for (int j = 0; j < across; j++) for (int i = 0; i < across; i++) crc.update(MapTiles.nodeTerrain(key, i * span, j * span, widest));
        }
        return crc.getValue();
    }

    static void planeCamera(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full) {
        out.println("\n--- 4. the plane's camera is today's far nodes: every stride, the windows and the world, as 0.7.131 drew them ---");
        long seed = EarthTerrainCheck.SEED;
        boolean same = spots.keySet().equals(FIXTURE_PRINTS.keySet());
        StringBuilder words = new StringBuilder();
        for (EarthTerrainCheck.Spot s : spots.values()) {
            long t0 = System.nanoTime(), c = fingerprint(World.Key.earth(seed, s.lat(), s.lon(), id), 1, true);
            same &= FIXTURE_PRINTS.containsKey(s.name()) && FIXTURE_PRINTS.get(s.name()) == c;
            words.append(String.format("%s%s %08x (%.0f ms)", words.length() == 0 ? "" : ", ", s.name(), c, (System.nanoTime() - t0) / 1e6));
        }
        out.println("      the fixture: " + words);
        check("on the fixture's four windows every stride's far node, aligned and centred, and the whole world's, are 0.7.131's byte for byte", same);
        if (full == null) {
            out.println("      earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": the full file's far nodes were not fingerprinted");
            return;
        }
        String fid = EarthTerrain.use(full);
        boolean all = true;
        words.setLength(0);
        for (int k = 0; k < FULL_SPOTS.length; k++) {
            long t0 = System.nanoTime(), c = fingerprint(World.Key.earth(seed, FULL_SPOTS[k][0], FULL_SPOTS[k][1], fid), 2, true);
            all &= c == FULL_PRINTS[k];
            words.append(String.format("%s%s,%s %08x (%.0f ms)", words.length() == 0 ? "" : ", ", FULL_SPOTS[k][0], FULL_SPOTS[k][1], c, (System.nanoTime() - t0) / 1e6));
        }
        out.println("      the full file: " + words);
        check("on the full file, eight spots' far nodes at every stride and their worlds' are 0.7.131's byte for byte", all);
    }

    /* =====================================================================
       5. THE GLOBE'S CAMERAS
       ===================================================================== */

    /** A class with the forest folded into the dry ground it stands on, for comparing two dithers. */
    static int folded(byte b) { return b == World.FOREST || b == World.GRASS || b == World.DESERT ? World.GRASS : b; }

    static void cameras(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData data) {
        out.println("\n--- 5. the globe's camera: its ground the plane's at the handoff, its dither on the ground ---");
        long seed = EarthTerrainCheck.SEED;
        // The globe's camera at the handoff facing the city: a piece the window's size, against the plane's node over the window.
        int n = MapFrame.NODE_PX, s = GlobeFrame.HANDOFF_PLOTS;
        boolean agree = true, forestClose = true, rivers = true;
        StringBuilder words = new StringBuilder();
        double flipsWorst = 0, freshFlips = 0;
        for (String name : List.of("Rotterdam", "Paris", "Perth")) {
            EarthTerrainCheck.Spot spot = spots.get(name);
            World.Key key = World.Key.earth(seed, spot.lat(), spot.lon(), id);
            GlobeFrame f = new GlobeFrame(n, n, spot.lat(), spot.lon());
            f.set(spot.lat(), spot.lon(), GlobeFrame.HANDOFF_KM);
            long x0 = EarthTerrain.spotPlot() - (long) n * s / 2;
            byte[] plane = MapTiles.nodeTerrain(key, x0, x0, s);
            long t0 = System.nanoTime();
            byte[] globe = f.groundPiece(key, 0, 0, n, n);
            double ms = (System.nanoTime() - t0) / 1e6;
            int differ = 0, dryP = 0, dryG = 0, forP = 0, forG = 0, fresh = 0;
            for (int k = 0; k < plane.length; k++) {
                if (folded(plane[k]) != folded(globe[k])) differ++;
                if (World.dry(plane[k])) { dryP++; if (plane[k] == World.FOREST) forP++; }
                if (World.dry(globe[k])) { dryG++; if (globe[k] == World.FOREST) forG++; }
                if (globe[k] == World.FRESH && plane[k] == World.FRESH) fresh++;
            }
            double pts = 100.0 * Math.abs(forG / (double) Math.max(1, dryG) - forP / (double) Math.max(1, dryP));
            agree &= differ <= plane.length / 1000;
            forestClose &= pts <= EarthTerrainCheck.SHARE_POINTS;
            rivers &= fresh > 0;
            // A turn of TURN_PX: the forest moves with the ground.
            f.pan(TURN_PX, 0);
            byte[] turned = f.groundPiece(key, 0, 0, n, n);
            int flips = 0, dry = 0, again = 0;
            Random r = new Random(3);
            for (int k = 0; k < globe.length; k++) {
                if (!World.dry(globe[k])) continue;
                dry++;
                if ((globe[k] == World.FOREST) != (turned[k] == World.FOREST)) flips++;
                if ((globe[k] == World.FOREST) != (r.nextDouble() < forG / (double) Math.max(1, dryG))) again++;
            }
            flipsWorst = Math.max(flipsWorst, flips / (double) Math.max(1, dry));
            freshFlips = Math.max(freshFlips, again / (double) Math.max(1, dry));
            words.append(String.format("%n      %s: %d of %,d pixels differ but for the forest; forest %.1f%% of the dry against the plane's %.1f%%;"
                    + " %,d fresh on both; drawn in %.1f ms; a turn of %.1f px flips %.2f%% of the dry pixels' forest", name, differ, plane.length,
                    100.0 * forG / Math.max(1, dryG), 100.0 * forP / Math.max(1, dryP), fresh, ms, TURN_PX, 100.0 * flips / Math.max(1, dry)));
        }
        out.println("      the globe's camera at the handoff facing the city, a piece the window's size, against the plane's far node over it:" + words);
        check("facing the city at the handoff the globe's camera draws the plane's node but for the forest's dither (a pixel in a thousand at most)", agree);
        check("...its forest of the dry ground within SHARE_POINTS of the plane's, and its rivers where the plane's are", forestClose && rivers);
        out.printf("      a dither drawn afresh would flip %.1f%% of them%n", 100 * freshFlips);
        check("a turn of TURN_PX flips the forest on no more than that share of the dry pixels: the dither moves with the ground", flipsWorst <= TURN_PX && freshFlips > TURN_PX);

        // The dither's cells at least a pixel each way, at every scale of the GROUND tier and at the picture's texel.
        boolean cells = true;
        for (double k = GlobeFrame.HANDOFF_KM; k < GlobeFrame.PICTURE_FROM_KM; k *= GlobeFrame.ZOOM_STEP) {
            double deg = k * 1000 / EarthTerrain.M_A_DEGREE, cell = Math.scalb(EarthTerrain.GLOBE_DITHER_DEG, EarthTerrain.ditherLevel(deg));
            cells &= cell >= deg * (1 - 1e-12) && cell < 2 * deg;
        }
        cells &= EarthTerrain.ditherLevel(GlobePicture.ARCMIN / 60.0) == 0;
        check("the globe's dither cell is at least a pixel tall and under two at every scale of the GROUND tier, and the picture's texel at its own", cells);
    }

    /* =====================================================================
       6. THE PLANET'S PICTURE
       ===================================================================== */

    static GlobePicture picture(EarthData fixture, EarthData full) {
        out.println("\n--- 6. the planet's picture: a class a texel through the grid's camera, its land and its rule the far layers' ---");
        GlobePicture one = GlobePicture.make(fixture, GlobePicture.ARCMIN, 1), two = GlobePicture.make(fixture, GlobePicture.ARCMIN, 2);
        out.printf("      on the fixture: %,d x %,d texels, %,d bytes, made in %.0f ms on one thread and %.0f on two%n", one.cols(), one.rows(), one.bytes(), one.madeMs(), two.madeMs());
        check("the picture is the same made twice, on two threads as on one", Arrays.equals(one.texels(), two.texels()));
        Land land = land(fixture, one);
        out.printf("      the fixture's bands: %s%n", land);
        check("on the fixture each 1-degree cell's land is the far layers' within its own rounding, and within SHARE_POINTS in the mean over the cells with a coast",
                land.mixed > 0 && land.past == 0 && land.mean <= EarthTerrainCheck.SHARE_POINTS);
        Rule rule = rule(fixture, one);
        out.printf("      the fixture's texels: %s%n", rule);
        check("...every texel's sea the far layers' half-contour, its ice and desert the bits' and its forest the data's within SHARE_POINTS", rule.ok());
        if (full == null) {
            out.println("      earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": the planet's picture was not made");
            return one;
        }
        GlobePicture first = GlobePicture.make(full, GlobePicture.FIRST_ARCMIN, 2);
        GlobePicture pic = GlobePicture.make(full, GlobePicture.ARCMIN, 2);
        long[] count = new long[World.CLASSES];
        for (byte b : pic.texels()) count[GlobePicture.classOf(b)]++;
        out.printf("      on the full file, two threads: %d' in %.0f ms (%,d bytes), then %d' in %.0f ms (%,d bytes, %.1f MB); the 4' texels: sea %,d (%,d of it shallow), grass %,d,"
                        + " forest %,d, fresh %,d, ice %,d, desert %,d, beach %,d%n", first.arcmin(), first.madeMs(), first.bytes(), pic.arcmin(), pic.madeMs(), pic.bytes(),
                pic.bytes() / 1e6, count[World.SALT] + count[World.SHALLOW], count[World.SHALLOW], count[World.GRASS], count[World.FOREST], count[World.FRESH], count[World.ICE], count[World.DESERT], count[World.SAND]);
        land = land(full, pic);
        out.printf("      the planet: %s%n", land);
        check("on the full file each 1-degree cell's land is the far layers' within its own rounding, and within SHARE_POINTS in the mean over the cells with a coast",
                land.mixed > 0 && land.past == 0 && land.mean <= EarthTerrainCheck.SHARE_POINTS);
        check("...and the planet's land, by area, is theirs within EarthTerrainCheck.LAND_WITHIN",
                Math.abs(land.pictureLand - land.dataLand) <= EarthTerrainCheck.LAND_WITHIN * land.dataLand);
        rule = rule(full, pic);
        out.printf("      the planet's texels: %s%n", rule);
        check("...every texel's sea the far layers' half-contour, its ice and desert the bits' and its forest the data's within SHARE_POINTS", rule.ok());
        check("...and it draws ice (Greenland's sheet), desert (the Sahara), forest and rivers (the Amazon's)",
                pic.at(72, -40) == World.ICE && pic.at(23, 12) == World.DESERT && count[World.FOREST] > 0 && freshNear(pic, -3.1, -60.0));
        return pic;
    }

    /** Whether the picture draws fresh water within a degree of (lat, lon). */
    static boolean freshNear(GlobePicture p, double lat, double lon) {
        for (double a = -1; a <= 1; a += 1.0 / 15) for (double b = -1; b <= 1; b += 1.0 / 15) if (p.at(lat + a, lon + b) == World.FRESH) return true;
        return false;
    }

    /** A picture's land per 1-degree cell against the far layers': cells with a coast, their mean and worst in points, how many past their own rounding, and the planet's land by area. */
    static final class Land {
        int mixed, past;
        double mean, worst, pictureLand, dataLand;
        double[] diffs = new double[0];

        @Override public String toString() {
            double p99 = diffs.length == 0 ? 0 : diffs[(int) (diffs.length * 0.99)];
            return String.format("%,d cells with a coast, land %.2f points off the far layers' in the mean, %.2f at the 99th centile, %.2f at worst, %d past"
                    + " their own rounding; land %.4f%% of the planet against %.4f%%", mixed, mean, p99, worst, past, 100 * pictureLand, 100 * dataLand);
        }
    }

    static Land land(EarthData d, GlobePicture p) {
        Land l = new Land();
        int per = 60 / p.arcmin(), cols = p.cols();
        double[] diffs = new double[180 * 360];
        double sum = 0, pic = 0, dat = 0, area = 0;
        for (int band = 179; band >= 0; band--) {
            byte[] land = d.grid("far.land", band), lake = d.grid("far.lake", band);
            int r0 = (179 - band) * per, fcols = 360 * 15;
            double cos = Math.cos(Math.toRadians(band - 90 + 0.5));
            for (int lon = 0; lon < 360; lon++) {
                double drawn = 0, data = 0, round = 0;
                for (int rr = 0; rr < per; rr++) {
                    for (int cc = 0; cc < per; cc++) {
                        int k = rr * fcols + lon * per + cc;
                        double share = Math.min(1, ((land[k] & 0xFF) + (lake[k] & 0xFF)) / (double) EarthData.FAR_LEVELS);
                        data += share;
                        round += Math.min(share, 1 - share);
                        if (!World.salt(p.texel(r0 + rr, lon * per + cc))) drawn++;
                    }
                }
                double cells = per * per, diff = 100 * Math.abs(drawn - data) / cells;
                pic += drawn / cells * cos;
                dat += data / cells * cos;
                area += cos;
                if (Math.abs(drawn - data) > round + 1e-9) l.past++;
                if (round > 0) {
                    diffs[l.mixed++] = diff;
                    sum += diff;
                    l.worst = Math.max(l.worst, diff);
                }
            }
        }
        l.mean = l.mixed > 0 ? sum / l.mixed : 0;
        l.diffs = Arrays.copyOf(diffs, l.mixed);
        Arrays.sort(l.diffs);
        l.pictureLand = pic / area;
        l.dataLand = dat / area;
        return l;
    }

    /** The far rule at each texel, read back from the data: sea, ice and desert by the rule, the forest's share. */
    static final class Rule {
        long texels, seaWrong, iceWrong, desertWrong, dry, forest;
        double forestData;

        boolean ok() { return texels > 0 && seaWrong == 0 && iceWrong == 0 && desertWrong == 0 && forestPoints() <= EarthTerrainCheck.SHARE_POINTS; }

        double forestPoints() { return dry == 0 ? 0 : 100 * Math.abs(forest / (double) dry - forestData / dry); }

        @Override public String toString() {
            return String.format("%,d texels, %d off the sea's half-contour, %d off the ice's bit, %d off the desert's; forest %.2f%% of the dry against the data's %.2f%%",
                    texels, seaWrong, iceWrong, desertWrong, 100.0 * forest / Math.max(1, dry), 100 * forestData / Math.max(1, dry));
        }
    }

    static Rule rule(EarthData d, GlobePicture p) {
        Rule r = new Rule();
        int per = 60 / p.arcmin(), step = 4 / p.arcmin(), fcols = 360 * 15;
        for (int band = 179; band >= 0; band--) {
            byte[] land = d.grid("far.land", band), lake = d.grid("far.lake", band);
            int r0 = (179 - band) * per;
            for (int rr = 0; rr < per; rr++) {
                int row = r0 + rr;
                double lat = 90 - (row + 0.5) / per;
                for (int col = 0; col < p.cols(); col++) {
                    double lon = (col + 0.5) / per - 180;
                    int k = (int) Math.floor((90 - lat) * 15) % 15 * fcols + (int) Math.floor((lon + 180) * 15);
                    double dry = (land[k] & 0xFF) / (double) EarthData.FAR_LEVELS, wet = (lake[k] & 0xFF) / (double) EarthData.FAR_LEVELS;
                    double sea = Math.max(0, 1 - dry - wet);
                    byte b = p.texel(row, col);
                    r.texels++;
                    if (World.salt(b) != (sea > 0 && sea >= EarthTerrain.SEA_LEVEL)) r.seaWrong++;
                    if (World.salt(b) || b == World.FRESH) continue;
                    int lc = d.at("landcover", lat, lon);
                    boolean ice = (lc & EarthData.LC_ICE) != 0, desert = (lc & EarthData.LC_DESERT) != 0;
                    if ((b == World.ICE) != ice) r.iceWrong++;
                    if (ice) continue;
                    if (b == World.DESERT && !desert) r.desertWrong++;
                    if (desert && b != World.DESERT && b != World.FOREST) r.desertWrong++;
                    r.dry++;
                    if (b == World.FOREST) r.forest++;
                    r.forestData += (lc & EarthData.LC_FOREST) / (double) EarthData.SHARE_LEVELS;
                }
            }
        }
        return r;
    }

    /* =====================================================================
       7. A FRAME
       ===================================================================== */

    static void frame(GlobePicture pic) {
        out.println("\n--- 7. a frame of the expanded map from the picture, timed on the main thread's CPU ---");
        double w = MapTiles.DESIGN_W, h = MapTiles.DESIGN_H;
        GlobeFrame f = new GlobeFrame(w, h, CITIES[1][0], CITIES[1][1]);
        int[] px = new int[(int) w * (int) h];
        int[] colours = GlobePicture.colours(false);
        for (int k = 0; k < FRAME_ROUNDS; k++) pic.frame(f, colours, TileRaster.VOID, px, 1);
        double cpu = Double.MAX_VALUE, wall = Double.MAX_VALUE, two = Double.MAX_VALUE;
        int space = 0;
        for (int k = 0; k < FRAME_ROUNDS; k++) {
            // the whole planet, then the picture's texel, round the globe
            f.set(CITIES[1][0] + 7 * k - 30, CITIES[1][1] + 31 * k, k % 2 == 0 ? f.mostKmPx() : GlobeFrame.PICTURE_FROM_KM);
            long c0 = MapCheck.monthClock(), w0 = System.nanoTime();
            pic.frame(f, colours, TileRaster.VOID, px, 1);
            long c1 = MapCheck.monthClock(), w1 = System.nanoTime();
            cpu = Math.min(cpu, (c1 - c0) / 1e6);
            wall = Math.min(wall, (w1 - w0) / 1e6);
            if (k == 0) for (int v : px) if (v == TileRaster.VOID) space++;
            w0 = System.nanoTime();
            pic.frame(f, colours, TileRaster.VOID, px, 2);
            two = Math.min(two, (System.nanoTime() - w0) / 1e6);
        }
        String clock = MapCheck.CPU_TIMED ? "the main thread's CPU" : "the wall clock (this JVM measures no thread's CPU)";
        out.printf("      a frame %d x %d: %.1f ms of %s (%.1f ms wall) on one thread, %.1f ms wall on two - the study 96 to 102 ms on one, 50 on two;"
                + " the whole planet leaves %,d pixels to space%n", (int) w, (int) h, cpu, clock, wall, two, space);
        check("a frame from the planet's picture is drawn in no more than FRAME_MS of " + (MapCheck.CPU_TIMED ? "the main thread's CPU" : "the wall clock"),
                Timing.within(cpu, FRAME_MS) && space > 0);
    }

    /* =====================================================================
       8. THE RELIEF IN THE PICTURE (ET1 slice D; spec-earth2-terrain 9.3)
       ===================================================================== */

    /** The picture's relief shares are the data's within this many binomial spreads of the dry texels drawn (a texel is a hash's draw against the 4' block's share, as a far node's pixel is) and a rounding of the slope layer's seven levels, 0.03: 4, as MapCheck 11's pictures. */
    static final double RELIEF_SIGMAS = 4, RELIEF_ROUNDING = 0.03;

    /** The dry texels (not ice, not water) in a box of the picture, `rows` x `cols` of them from (row0 from the north, col0 from 180 W), counted by relief. */
    static long[] dryTexelsByRelief(GlobePicture p, int row0, int col0, int rows, int cols) {
        long[] n = new long[World.RELIEFS];
        for (int r = row0; r < row0 + rows; r++) {
            for (int c = col0; c < col0 + cols; c++) {
                byte code = p.texelCode(r, c);
                if (World.dry(GlobePicture.classOf(code))) n[GlobePicture.reliefOf(code)]++;
            }
        }
        return n;
    }

    /** The data's relief shares over a box of 30" cells (the slope layer's bytes unpacked: flat is under foothill's share, foothill what is not steep, and so on): the mean over every cell. */
    static double[] dataReliefShares(EarthData d, double lat0, double lat1, double lon0, double lon1) {
        double[] s = new double[World.RELIEFS];
        long n = 0;
        for (double lat = lat0 + 1.0 / 240; lat < lat1; lat += 1.0 / 120) {
            for (double lon = lon0 + 1.0 / 240; lon < lon1; lon += 1.0 / 120) {
                int b = d.slope(lat, lon);
                double f = EarthData.slopeFoothill(b), st = EarthData.slopeSteep(b), v = EarthData.slopeVerySteep(b);
                s[World.FLAT] += 1 - f;
                s[World.FOOTHILL] += f - st;
                s[World.STEEP] += st - v;
                s[World.VERY_STEEP] += v;
                n++;
            }
        }
        for (int k = 0; k < s.length; k++) s[k] /= Math.max(1, n);
        return s;
    }

    /** Whether the texels' shares (counts) are the data's within RELIEF_SIGMAS spreads and the rounding, each relief. */
    static boolean sharesMatch(long[] counts, double[] data) {
        long n = Arrays.stream(counts).sum();
        boolean ok = n > 0;
        for (int r = 0; r < World.RELIEFS && ok; r++) {
            double p = data[r], spread = Math.sqrt(Math.max(1e-9, p * (1 - p)) / n);
            ok &= Math.abs(counts[r] / (double) n - p) <= RELIEF_SIGMAS * spread + RELIEF_ROUNDING;
        }
        return ok;
    }

    static String sharesWords(long[] counts) {
        long n = Math.max(1, Arrays.stream(counts).sum());
        return String.format("flat %.1f%%, foothill %.1f%%, steep %.1f%%, very steep %.1f%% of %,d dry texels", 100.0 * counts[0] / n, 100.0 * counts[1] / n, 100.0 * counts[2] / n, 100.0 * counts[3] / n, n);
    }

    static void reliefInPicture(EarthData fixture, String id, EarthData full, GlobePicture fullPicture) {
        out.println("\n--- 8. the planet's picture holds each texel's relief beside its class: mountains grey from orbit, snow white, the classes as they were ---");
        // The code: a class and its relief, 32 of them, the colour table the map's own.
        int[] colours = GlobePicture.colours(false);
        boolean codes = GlobePicture.CODES == World.RELIEFS * World.CLASSES && colours.length == GlobePicture.CODES && GlobePicture.CLASS_MASK == World.CLASSES - 1;
        for (int r = 0; r < World.RELIEFS; r++) {
            for (int c = 0; c < World.CLASSES; c++) {
                byte code = GlobePicture.code(c, r);
                codes &= code >= 0 && code < GlobePicture.CODES && GlobePicture.classOf(code) == c && GlobePicture.reliefOf(code) == r && colours[code] == TileRaster.colour(c, r)
                        && (r != World.FLAT || (code == c && colours[c] == TileRaster.GROUND[c]));
            }
        }
        check("a texel's code is its class in the low bits and its relief above, CODES of them, FLAT's the class itself; the globe's colour for each is the map's (TileRaster.colour())", codes);
        // The fixture's picture: every texel a code whose relief is on dry ground alone, ice and water flat; its classes as they were (a class table of the old length reads them).
        GlobePicture fp = GlobePicture.of(fixture, GlobePicture.ARCMIN);
        long bad = 0, sloped = 0, ice = 0, iceSloped = 0;
        for (byte b : fp.texels()) {
            int cls = GlobePicture.classOf(b), rel = GlobePicture.reliefOf(b);
            if (b < 0 || b >= GlobePicture.CODES || cls >= World.CLASSES || rel >= World.RELIEFS) bad++;
            else {
                if (rel != World.FLAT && !World.dry(cls)) bad++;
                if (rel != World.FLAT) sloped++;
                if (cls == World.ICE) { ice++; if (rel != World.FLAT) iceSloped++; }
            }
        }
        out.printf("      the fixture's picture: %,d texels, %,d of them on a slope, %,d of ice (%,d sloped), %,d codes outside the rule%n", fp.texels().length, sloped, ice, iceSloped, bad);
        check("every texel's relief is on dry ground alone (no slope on water or ice: snow stays white), and the picture has slopes", bad == 0 && iceSloped == 0 && sloped > 0);
        // The fixture's hilliest degree (its far layers are the coasts' bands, so Medellin's is not among them): the picture's texels are the far rule's
        // through a camera of one degree's rows, texel for texel, and a frame over it colours each pixel by its texel's code.
        int per = 60 / GlobePicture.ARCMIN;
        int[] sloping = new int[180 * 360];
        for (int r = 0; r < fp.rows(); r++) for (int c = 0; c < fp.cols(); c++) if (GlobePicture.reliefOf(fp.texelCode(r, c)) != World.FLAT) sloping[(r / per) * 360 + c / per]++;
        int hill = 0;
        for (int k = 1; k < sloping.length; k++) if (sloping[k] > sloping[hill]) hill = k;
        int dr = hill / 360, dc = hill % 360, row0 = dr * per, col0 = dc * per;
        double lat0 = 90 - dr - 1, lon0 = dc - 180;
        EarthTerrain e = new EarthTerrain(World.Key.earth(0, 0, 0, id));
        byte[] cover = new byte[per * 360 * per], relief = new byte[per * 360 * per];
        e.farTerrain(e.grid(GlobePicture.ARCMIN, row0, per), cover, relief);
        boolean same = true;
        for (int r = 0; r < per; r++) for (int c = 0; c < per; c++) same &= fp.texelCode(row0 + r, col0 + c) == GlobePicture.code(cover[r * 360 * per + col0 + c], relief[r * 360 * per + col0 + c]);
        out.printf("      the fixture's hilliest degree (%.0f to %.0f N, %.0f to %.0f E): %s%n", lat0, lat0 + 1, lon0, lon0 + 1, sharesWords(dryTexelsByRelief(fp, row0, col0, per, per)));
        check("the fixture's hilliest degree in the picture is the far rule's, texel for texel, through a band of one degree's rows instead of ten", same && sloping[hill] > 0);
        GlobeFrame f = new GlobeFrame(400, 300, lat0 + 0.5, lon0 + 0.5);
        f.set(lat0 + 0.5, lon0 + 0.5, 1.5);
        int[] px = new int[400 * 300], plain = new int[400 * 300];
        fp.frame(f, colours, GlobePicture.SPACE, px, 1);
        fp.frame(f, TileRaster.GROUND, GlobePicture.SPACE, plain, 1);
        int slopedPixels = 0;
        boolean frameOk = true, classTable = true;
        for (int j = 0; j < 300; j++) {
            for (int i = 0; i < 400; i++) {
                double[] ll = f.latLonAt(i + 0.5, j + 0.5);
                if (ll == null) {
                    frameOk &= px[j * 400 + i] == GlobePicture.SPACE;
                    classTable &= plain[j * 400 + i] == GlobePicture.SPACE;
                    continue;
                }
                byte code = fp.codeAt(ll[0], ll[1]);
                frameOk &= px[j * 400 + i] == colours[code];
                classTable &= plain[j * 400 + i] == TileRaster.GROUND[GlobePicture.classOf(code)];
                if (GlobePicture.reliefOf(code) != World.FLAT) slopedPixels++;
            }
        }
        check("a frame over it colours each pixel by its texel's code - a grey on open ground, forest shaded - and from a table of the classes alone, as before", frameOk && classTable && slopedPixels > 0);
        // The full file, when it is named: the Himalaya and Tibet, where the range is grey and the snow white.
        if (full == null) {
            out.println("      earth-2.bin not named: the Himalaya's texels were not counted");
            return;
        }
        int hr0 = (int) Math.round((90 - 36) * per), hc0 = (int) Math.round((180 + 76) * per), hrows = 10 * per, hcols = 20 * per;
        long[] him = dryTexelsByRelief(fullPicture, hr0, hc0, hrows, hcols);
        double[] hdata = dataReliefShares(full, 26, 36, 76, 96);
        long snow = 0, snowSloped = 0;
        for (int r = hr0; r < hr0 + hrows; r++) for (int c = hc0; c < hc0 + hcols; c++) {
            byte code = fullPicture.texelCode(r, c);
            if (GlobePicture.classOf(code) == World.ICE) { snow++; if (GlobePicture.reliefOf(code) != World.FLAT) snowSloped++; }
        }
        out.printf("      the Himalaya and Tibet (26 to 36 N, 76 to 96 E): the picture %s; the data's %.1f%% / %.1f%% / %.1f%% / %.1f%%; %,d texels of snow, %,d sloped%n", sharesWords(him),
                100 * hdata[0], 100 * hdata[1], 100 * hdata[2], 100 * hdata[3], snow, snowSloped);
        check("on the full file the Himalaya and Tibet's relief shares are the data's within RELIEF_SIGMAS spreads, with grey in all three classes, and its snow all white", sharesMatch(him, hdata)
                && him[World.FOOTHILL] > 0 && him[World.STEEP] > 0 && him[World.VERY_STEEP] > 0 && snow > 0 && snowSloped == 0);
    }
}
