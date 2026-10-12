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
 *      away from the city the wheel stops at the handoff, and at the plane's
 *      scales a pan stops at the reach, which a city's own ground extends.
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
 *      clock beside it, and on two threads.
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

    /** The CRC32 of every stride's far node over the fixture's four windows and the world's (fingerprint()), as 0.7.131 drew them before the camera (probe E8Fingerprint on its build, 19f1cad). */
    static final Map<String, Long> FIXTURE_PRINTS = Map.of("Rotterdam", 0x5de8b72aL, "Perth", 0x2bd933ffL, "Paris", 0x7016955fL, "Fiji", 0xd15c60f1L);

    /** The full file's spots: Rotterdam, Halifax, Manila, Greenland's ice, McMurdo past EarthProj.BLEND_MAX_LAT, Chukotka on the 180th meridian, Finland's lakes and the Sahara. */
    static final double[][] FULL_SPOTS = { { 51.90625, 4.48125 }, { 44.65, -63.57 }, { 14.60, 120.98 }, { 72, -40 }, { -77.85, 166.67 }, { 66, 180 }, { 62, 26 }, { 23, 12 } };

    /** ...and their nodes' CRC32, as 0.7.131 drew them. */
    static final long[] FULL_PRINTS = { 0x53186882L, 0xdcf6b91eL, 0x4b92b699L, 0x34df4490L, 0x663a5196L, 0x7e38f4f5L, 0x45cc7052L, 0x2b58c5a8L };

    static int fails = 0;
    /** Where the harness prints: the console. */
    static final PrintStream out = System.out;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-110s %s%n", label, ok ? "OK" : "FAIL");
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
        GlobePicture pic = picture(data, full);
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

        // The reach: the wheel stops at the handoff away from the city; a pan at the plane's scales stops at it.
        double[] away = GlobeFrame.destination(c[0], c[1], 45, 3 * GlobeFrame.REACH_KM);
        f.set(away[0], away[1], hk * GlobeFrame.ZOOM_STEP);
        for (int i = 0; i < 5; i++) f.zoomNotches(w / 2, h / 2, 1);
        double stoppedAt = f.kmPx();
        boolean stopped = stoppedAt == hk && f.tier() == GlobeFrame.GROUND;
        f.set(c[0], c[1], hk / GlobeFrame.ZOOM_STEP);
        for (int i = 0; i < 200; i++) f.pan(-300, -170);
        double reached = f.cityKm(f.centreLat(), f.centreLon());
        boolean planeStill = f.tier() == GlobeFrame.PLANE;
        out.printf("      %.0f km from the city five notches in stopped at %.3f km a pixel; 200 drags on the plane stopped %.3f km from the city%n",
                3 * GlobeFrame.REACH_KM, stoppedAt, reached);
        check("away from the city the wheel stops at the handoff, the GROUND tier", stopped);
        check("...and on the plane a pan stops at the reach, the view still the plane's", planeStill && reached <= GlobeFrame.REACH_KM && reached > GlobeFrame.REACH_KM - 1);
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
            out.println("      earth-1.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": the full file's far nodes were not fingerprinted");
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
            out.println("      earth-1.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": the planet's picture was not made");
            return one;
        }
        GlobePicture first = GlobePicture.make(full, GlobePicture.FIRST_ARCMIN, 2);
        GlobePicture pic = GlobePicture.make(full, GlobePicture.ARCMIN, 2);
        long[] count = new long[World.CLASSES];
        for (byte b : pic.texels()) count[b]++;
        out.printf("      on the full file, two threads: %d' in %.0f ms (%,d bytes), then %d' in %.0f ms (%,d bytes, %.1f MB); the 4' texels: sea %,d, grass %,d,"
                        + " forest %,d, fresh %,d, ice %,d, desert %,d, beach %,d%n", first.arcmin(), first.madeMs(), first.bytes(), pic.arcmin(), pic.madeMs(), pic.bytes(),
                pic.bytes() / 1e6, count[World.SALT], count[World.GRASS], count[World.FOREST], count[World.FRESH], count[World.ICE], count[World.DESERT], count[World.SAND]);
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
                        if (p.texel(r0 + rr, lon * per + cc) != World.SALT) drawn++;
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
                    if ((b == World.SALT) != (sea > 0 && sea >= EarthTerrain.SEA_LEVEL)) r.seaWrong++;
                    if (b == World.SALT || b == World.FRESH) continue;
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
        for (int k = 0; k < FRAME_ROUNDS; k++) pic.frame(f, TileRaster.GROUND, TileRaster.VOID, px, 1);
        double cpu = Double.MAX_VALUE, wall = Double.MAX_VALUE, two = Double.MAX_VALUE;
        int space = 0;
        for (int k = 0; k < FRAME_ROUNDS; k++) {
            // the whole planet, then the picture's texel, round the globe
            f.set(CITIES[1][0] + 7 * k - 30, CITIES[1][1] + 31 * k, k % 2 == 0 ? f.mostKmPx() : GlobeFrame.PICTURE_FROM_KM);
            long c0 = MapCheck.monthClock(), w0 = System.nanoTime();
            pic.frame(f, TileRaster.GROUND, TileRaster.VOID, px, 1);
            long c1 = MapCheck.monthClock(), w1 = System.nanoTime();
            cpu = Math.min(cpu, (c1 - c0) / 1e6);
            wall = Math.min(wall, (w1 - w0) / 1e6);
            if (k == 0) for (int v : px) if (v == TileRaster.VOID) space++;
            w0 = System.nanoTime();
            pic.frame(f, TileRaster.GROUND, TileRaster.VOID, px, 2);
            two = Math.min(two, (System.nanoTime() - w0) / 1e6);
        }
        String clock = MapCheck.CPU_TIMED ? "the main thread's CPU" : "the wall clock (this JVM measures no thread's CPU)";
        out.printf("      a frame %d x %d: %.1f ms of %s (%.1f ms wall) on one thread, %.1f ms wall on two - the study 96 to 102 ms on one, 50 on two;"
                + " the whole planet leaves %,d pixels to space%n", (int) w, (int) h, cpu, clock, wall, two, space);
        check("a frame from the planet's picture is drawn in no more than FRAME_MS of " + (MapCheck.CPU_TIMED ? "the main thread's CPU" : "the wall clock"),
                cpu <= FRAME_MS && space > 0);
    }
}
