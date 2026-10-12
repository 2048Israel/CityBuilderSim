package ham.citybuildersim;

/**
 * The Earth's map as a globe, without the toolkit: where the view looks on the planet, how close and which way is up, how a notch of the wheel, a drag and Fit move it, which of three pictures it draws - the planet's picture, the ground through the globe, or the city's plane - and where a pixel is on the planet; what the land office's map on Earth will draw by (E9) and GlobeCheck holds.
 *
 * WHY THIS EXISTS (0.7.132, batch E8; docs/studies/spec-globe.md 3, Jerus's
 * answers of 2026-10-11, "yes"). The map on Earth drew the whole planet on
 * the city's plane, Lambert's equal-area projection round the spot: cut by
 * the world's square, and the far side smeared round the rim (a quarter of
 * the planet in the outer 13% of the disc). The founding screen's globe
 * (E4) already drew it as a globe looks, but its arithmetic lived in the
 * screen where no harness could hold it. This is that arithmetic in the
 * model, for both screens: a view centre in latitude and longitude, a scale
 * in km a pixel and a roll, orthographic (EarthProj.Ortho) while it is wide
 * and the city's own plane, MapFrame's, closer than the handoff.
 *
 * THE TIERS (spec-globe 3.1), by the scale:
 *   - PICTURE, from the whole planet (FIT_SHARE of the view's shorter side)
 *     down to PICTURE_FROM_KM, the planet's picture's texel: each pixel's
 *     orthographic inverse, then its texel (GlobePicture.frame());
 *   - GROUND, from there down to the handoff, HANDOFF_PLOTS a pixel: the far
 *     rule through the globe's camera (EarthTerrain.globe(), groundPiece());
 *   - PLANE, closer than the handoff: today's map, the city's plane, a
 *     MapFrame at planeX(), planeY() and planeScale().
 * At the handoff the plane and the globe facing the city part by a third of
 * a pixel on the expanded map (Q6), so the swap cannot be seen.
 *
 * WHICH WAY IS UP (spec-globe 3.3, Q4). Wide, the globe is north up. The
 * plane's up is the city's north, which away from the city is turned from a
 * place's own north (2.7 degrees 300 km east of Halifax); so over the last
 * ROLL_NOTCHES notches before the handoff, and only while the view's centre
 * is within the reach, the roll eases from north to the plane's up there
 * (planeRollAt()). A globe looking farther away never hands off and stays
 * north up.
 *
 * WHERE CLOSE VIEWS GO (spec-globe 3.4, Q3). Closer than the handoff the
 * view's centre stays within the reach of the city - REACH_KM, or the city's
 * own ground and its offers and REACH_MARGIN_KM round them where they reach
 * farther (reachKm()): a pan at that scale stops at it, and away from the
 * city the wheel stops at the handoff.
 *
 * A zoom keeps the point under the pointer under it, and a drag the point
 * grabbed under the pointer, on the globe as on the plane (spec-globe 4's
 * E8 row): the view centre is solved from the point, its place on the
 * screen and the roll (centreFor()). Pure: nothing in the model reads it.
 */
public final class GlobeFrame {

    /** The tiers, by the scale (spec-globe 3.1): the planet's picture, the ground through the globe, the city's plane. */
    public static final int PICTURE = 0, GROUND = 1, PLANE = 2;

    /** The handoff from the globe to the city's plane, in plots a pixel: 16 (spec-globe 3.2, Jerus's Q6) - 0.48 km a pixel, where the two part by a third of a pixel on the expanded map, a power of two the far nodes already use (MapFrame.nodePlots()). */
    public static final int HANDOFF_PLOTS = 16;

    /** ...in km a pixel: HANDOFF_PLOTS x World.PLOT_M, 0.48. */
    public static final double HANDOFF_KM = HANDOFF_PLOTS * World.PLOT_M / 1000;

    /** Closer than the handoff the view's centre stays within this of the city, in km: 300 (spec-globe 3.4, Jerus's Q3) - 1.16 px apart at the swap there with the roll eased (spec-globe 3.3). */
    public static final double REACH_KM = 300;

    /** ...or within this of the city's own ground and its offers, in km, where they reach farther than REACH_KM: 50 (spec-globe 3.4's est., ★E8 - the ground the land office shows round what it sells). */
    public static final double REACH_MARGIN_KM = 50;

    /** The roll eases from north up to the plane's up over this many notches of the wheel before the handoff: 2 (spec-globe 3.3's est., ★E8) - at most 2.7 degrees within REACH_KM of a city at Halifax's latitude, a degree and a third a notch. */
    public static final int ROLL_NOTCHES = 2;

    /** A notch of the wheel zooms by this much, at the pointer: the map's, MapFrame.ZOOM_STEP. */
    public static final double ZOOM_STEP = MapFrame.ZOOM_STEP;

    /** Fit shows the whole planet across this share of the view's shorter side: 0.9, as the founding globe's widest view (spec-globe 3.1: 17.6 km a pixel on the expanded map). */
    public static final double FIT_SHARE = 0.9;

    /** The view's centre is turned no nearer a pole than this, in degrees: 89, the founding globe's (FoundingScreen's turn()) - at the pole every longitude is the centre's and north has no direction. */
    public static final double POLE_LAT = 89;

    /** The PICTURE tier draws down to the planet's picture's texel, in km a pixel: a GlobePicture.ARCMIN texel's height on the ground, 7.41 (spec-globe 3.1). */
    public static final double PICTURE_FROM_KM = GlobePicture.texelKm(GlobePicture.ARCMIN);

    private double width, height;
    private final double cityLat, cityLon;
    private final EarthProj plane;
    private double lat, lon, kmPx, roll, reachKm = REACH_KM;
    private EarthProj.Ortho ortho;

    /** A view of width x height pixels round a city on the real Earth at (cityLat, cityLon), the spot its plane is centred on: fitted, the whole planet facing the city, north up. */
    public GlobeFrame(double width, double height, double cityLat, double cityLon) {
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
        this.cityLat = cityLat;
        this.cityLon = EarthProj.normaliseLon(cityLon);
        this.plane = new EarthProj(cityLat, cityLon);
        fit();
    }

    public double width()   { return width; }
    public double height()  { return height; }
    /** Where it looks, degrees. */
    public double centreLat() { return lat; }
    public double centreLon() { return lon; }
    /** How close: km a pixel at the view's centre. */
    public double kmPx()    { return kmPx; }
    /** Which way is up: the angle, degrees clockwise, from the view's up to north at its centre (0, north up, wide). */
    public double roll()    { return roll; }
    /** The city's spot, degrees: its plane's centre. */
    public double cityLat() { return cityLat; }
    public double cityLon() { return cityLon; }
    /** The city's plane (Lambert's, round the spot). */
    public EarthProj plane() { return plane; }

    /** The widest view: the whole planet across FIT_SHARE of the shorter side, km a pixel. */
    public double mostKmPx() { return 2 * EarthProj.RADIUS_M / 1000 / (FIT_SHARE * Math.min(width, height)); }

    /** The closest: the plane's, MapFrame.MOST_PX_A_PLOT, km a pixel. */
    public static double leastKmPx() { return World.PLOT_M / MapFrame.MOST_PX_A_PLOT / 1000; }

    /** The tier a scale draws: PICTURE from PICTURE_FROM_KM, GROUND from HANDOFF_KM, PLANE closer. */
    public static int tierOf(double kmPx) {
        return kmPx >= PICTURE_FROM_KM ? PICTURE : kmPx >= HANDOFF_KM ? GROUND : PLANE;
    }

    /** The tier this view draws. */
    public int tier() { return tierOf(kmPx); }

    /** The view's orthographic projection: round its centre, turned by its roll. */
    public EarthProj.Ortho ortho() { return ortho; }

    /* ----- the reach (spec-globe 3.4) ----- */

    /** How far from the city close views go, km. */
    public double reach() { return reachKm; }

    /** The reach set (reachKm()): at least REACH_KM. */
    public void setReach(double km) {
        reachKm = Math.max(REACH_KM, km);
        clampPan();
    }

    /** The reach a city's map gives close views, km (★E8, spec-globe 3.4): REACH_KM, or the farthest corner of its own ground and of its offers standing from its spot, along the ground, and REACH_MARGIN_KM - whichever is farther. */
    public static double reachKm(CityLand land, LandMarket market) {
        double far = 0, c = World.SIDE / 2.0;
        LandGrid g = land.grid();
        if (g.ownedPlots() > 0) far = corner(g.minX(), g.minY(), g.maxX(), g.maxY(), c);
        if (market != null) {
            for (LandParcel p : market.getListing()) far = Math.max(far, corner(p.getX0(), p.getY0(), p.getX1(), p.getY1(), c));
        }
        return reachKm(far);
    }

    /** ...from the farthest of them, in plots from the spot on its plane: REACH_KM, or that along the ground and REACH_MARGIN_KM if farther. */
    public static double reachKm(double farthestPlots) {
        if (!(farthestPlots > 0)) return REACH_KM;
        double ground = 2 * EarthProj.RADIUS_M * StrictMath.asin(Math.min(1, farthestPlots * World.PLOT_M / EarthProj.REACH_M)) / 1000;
        return Math.max(REACH_KM, ground + REACH_MARGIN_KM);
    }

    /** The farthest corner of a box of plots from plot (c, c), in plots. */
    private static double corner(double x0, double y0, double x1, double y1, double c) {
        double dx = Math.max(Math.abs(x0 - c), Math.abs(x1 - c)), dy = Math.max(Math.abs(y0 - c), Math.abs(y1 - c));
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** How far a point is from the city, km along the ground. */
    public double cityKm(double atLat, double atLon) {
        return EarthProj.distanceM(cityLat, cityLon, atLat, atLon) / 1000;
    }

    /** Whether a point is within the reach. */
    public boolean withinReach(double atLat, double atLon) {
        return cityKm(atLat, atLon) <= reachKm;
    }

    /* ----- which way is up (spec-globe 3.3) ----- */

    /**
     * The plane's up at a place against its north: the angle, degrees
     * clockwise, from the plane's up to north there - what a north-up globe
     * looking there is turned from the plane by. Nought at the city; the
     * derivative of EarthProj.forward() along the meridian, in closed form.
     */
    public double planeRollAt(double atLat, double atLon) {
        double p0 = StrictMath.toRadians(cityLat), p = StrictMath.toRadians(atLat);
        double sin0 = StrictMath.sin(p0), cos0 = StrictMath.cos(p0), sinP = StrictMath.sin(p), cosP = StrictMath.cos(p);
        double dl = StrictMath.toRadians(EarthProj.normaliseLon(atLon - cityLon));
        double sinDl = StrictMath.sin(dl), cosDl = StrictMath.cos(dl);
        double cosc = sin0 * sinP + cos0 * cosP * cosDl;
        if (!(1 + cosc > 0)) return 0;
        double k = EarthProj.RADIUS_M * StrictMath.sqrt(2 / (1 + cosc));
        double dk = -k / (2 * (1 + cosc)) * (sin0 * cosP - cos0 * sinP * cosDl);
        double dx = dk * cosP * sinDl - k * sinP * sinDl;
        double dy = dk * (cos0 * sinP - sin0 * cosP * cosDl) + k * (cos0 * cosP + sin0 * sinP * cosDl);
        return StrictMath.toDegrees(StrictMath.atan2(dx, dy));
    }

    /** How far the roll has eased to the plane's up at a scale: 0 wider than ROLL_NOTCHES notches before the handoff, 1 at it and closer, by notches between. */
    public static double ease(double kmPx) {
        double t = StrictMath.log(HANDOFF_KM * StrictMath.pow(ZOOM_STEP, ROLL_NOTCHES) / kmPx) / (ROLL_NOTCHES * StrictMath.log(ZOOM_STEP));
        return t <= 0 ? 0 : t >= 1 ? 1 : t;
    }

    /** The roll a view centred at (atLat, atLon) at a scale takes: the eased share of the plane's up there within the reach, north up beyond it. */
    public double rollFor(double atLat, double atLon, double atKmPx) {
        double e = ease(atKmPx);
        if (e <= 0 || !withinReach(atLat, atLon)) return 0;
        return e * planeRollAt(atLat, atLon);
    }

    /* ----- moving it ----- */

    /** Looks at (lat, lon) at a scale, both clamped; the roll follows. */
    public void set(double atLat, double atLon, double atKmPx) {
        lat = atLat;
        lon = EarthProj.normaliseLon(atLon);
        kmPx = atKmPx;
        clampZoom();
    }

    /** A new size, the centre and scale kept (clamped). */
    public void resize(double w, double h) {
        width = Math.max(1, w);
        height = Math.max(1, h);
        clampZoom();
    }

    /** Fit (spec-globe 3.7): the whole planet, facing the city, north up. */
    public void fit() {
        lat = Math.max(-POLE_LAT, Math.min(POLE_LAT, cityLat));
        lon = cityLon;
        kmPx = mostKmPx();
        update();
    }

    /** Zooms about a screen point, the km a pixel times a factor (under one closer): the point on the planet under it stays under it - on the globe, on the plane, and across the handoff to within their parting - unless a clamp stops it; away from the city the wheel stops at the handoff. */
    public void zoomAt(double sx, double sy, double factor) {
        double[] p = latLonAt(sx, sy);
        double to = Math.max(leastKmPx(), Math.min(mostKmPx(), kmPx * factor));
        if (p == null) {
            kmPx = to;
            clampZoom();
            return;
        }
        put(p[0], p[1], sx, sy, to);
        if (to < HANDOFF_KM && !withinReach(lat, lon)) put(p[0], p[1], sx, sy, HANDOFF_KM);
        clampZoom();
    }

    /** ...by notches of the wheel, ZOOM_STEP each, in (positive) or out. */
    public void zoomNotches(double sx, double sy, double notches) {
        zoomAt(sx, sy, Math.pow(ZOOM_STEP, -notches));
    }

    /** A drag from (sx0, sy0) to (sx1, sy1): the point grabbed stays under the pointer - the globe turns, the plane slides - and at the plane's scales the centre stops at the reach. A grab off the planet turns the globe as a drag of its centre would. */
    public void drag(double sx0, double sy0, double sx1, double sy1) {
        double[] p = latLonAt(sx0, sy0);
        if (p == null) {
            p = new double[] { lat, lon };
            sx1 = width / 2 + (sx1 - sx0);
            sy1 = height / 2 + (sy1 - sy0);
        }
        put(p[0], p[1], sx1, sy1, kmPx);
        clampPan();
    }

    /** A drag of (dx, dy) pixels from the centre: what a held key moves (spec-globe 3.11). */
    public void pan(double dx, double dy) {
        drag(width / 2, height / 2, width / 2 + dx, height / 2 + dy);
    }

    /** The view at scale `to` with (pLat, pLon) at screen point (sx, sy): on the plane by its plots, on the globe by centreFor() with the roll it will take there. */
    private void put(double pLat, double pLon, double sx, double sy, double to) {
        kmPx = to;
        if (tierOf(to) == PLANE) {
            double[] xy = new double[2];
            if (!plane.plotOf(pLat, pLon, xy)) return;
            double s = World.PLOT_M / (to * 1000);
            double[] ll = new double[2];
            if (plane.latLonOfPlot(xy[0] - (sx - width / 2) / s, xy[1] - (sy - height / 2) / s, ll)) {
                lat = ll[0];
                lon = ll[1];
            }
            update();
            return;
        }
        double m = to * 1000, x = (sx - width / 2) * m, y = (height / 2 - sy) * m;
        double r = rollFor(lat, lon, to);
        double[] c = new double[2];
        for (int i = 0; i < 8; i++) {
            double rr = StrictMath.toRadians(r), cr = StrictMath.cos(rr), sr = StrictMath.sin(rr);
            centreFor(pLat, pLon, x * cr - y * sr, x * sr + y * cr, lat, c);
            double next = rollFor(c[0], c[1], to);
            boolean done = Math.abs(next - r) < 1e-12;
            r = next;
            if (done) break;
        }
        lat = c[0];
        lon = c[1];
        update();
    }

    /**
     * The view centre from which (pLat, pLon) lies `east` and `north` metres
     * out on the orthographic plane, north up: at the angle c = asin(rho / R)
     * from the centre and the azimuth a = atan2(east, north) at it. The
     * spherical triangle of the pole, the centre and the point gives
     * sin pLat = sin lat cos c + cos lat sin c cos a, solved for lat - the
     * root nearest `nearLat` - and the longitude from the destination
     * formula. Off the disc the point is taken at its limb; past what the
     * triangle allows (a point near a pole put where it cannot be, north
     * up), the nearest that can.
     */
    static void centreFor(double pLat, double pLon, double east, double north, double nearLat, double[] out) {
        double rho = Math.min(EarthProj.RADIUS_M, Math.sqrt(east * east + north * north));
        double c = StrictMath.asin(rho / EarthProj.RADIUS_M), a = StrictMath.atan2(east, north);
        double sc = StrictMath.sin(c), cc = StrictMath.cos(c), sp = StrictMath.sin(StrictMath.toRadians(pLat));
        double big = cc, small = sc * StrictMath.cos(a), m = Math.sqrt(big * big + small * small), d = StrictMath.atan2(small, big);
        double s = StrictMath.asin(Math.max(-1, Math.min(1, sp / m)));
        double best = Double.NaN, near = StrictMath.toRadians(nearLat);
        for (double root : new double[] { s - d, Math.PI - s - d }) {
            double f = StrictMath.IEEEremainder(root, 2 * Math.PI);
            if (f > Math.PI / 2 + 1e-12 || f < -Math.PI / 2 - 1e-12) continue;
            f = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, f));
            if (Double.isNaN(best) || Math.abs(f - near) < Math.abs(best - near)) best = f;
        }
        if (Double.isNaN(best)) best = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, s - d));
        double dl = StrictMath.atan2(StrictMath.sin(a) * sc * StrictMath.cos(best), cc - StrictMath.sin(best) * sp);
        out[0] = StrictMath.toDegrees(best);
        out[1] = EarthProj.normaliseLon(pLon - StrictMath.toDegrees(dl));
    }

    /** The scale within its bounds; closer than the handoff away from the city, the handoff (the wheel's stop); the centre off the poles; the roll. */
    private void clampZoom() {
        if (!(kmPx > 0)) kmPx = mostKmPx();
        kmPx = Math.max(leastKmPx(), Math.min(mostKmPx(), kmPx));
        lat = Math.max(-POLE_LAT, Math.min(POLE_LAT, lat));
        if (kmPx < HANDOFF_KM && !withinReach(lat, lon)) kmPx = HANDOFF_KM;
        update();
    }

    /** ...and at the plane's scales the centre pulled back to the reach along its line from the city (a pan's stop). */
    private void clampPan() {
        kmPx = Math.max(leastKmPx(), Math.min(mostKmPx(), kmPx));
        lat = Math.max(-POLE_LAT, Math.min(POLE_LAT, lat));
        if (kmPx < HANDOFF_KM && !withinReach(lat, lon)) {
            double b = bearing(cityLat, cityLon, lat, lon);
            double[] p = destination(cityLat, cityLon, b, reachKm * (1 - 1e-9));
            lat = p[0];
            lon = p[1];
        }
        update();
    }

    private void update() {
        roll = rollFor(lat, lon, kmPx);
        ortho = EarthProj.ortho(lat, lon, roll);
    }

    /** The initial bearing from one point to another, degrees clockwise from north. */
    public static double bearing(double lat1, double lon1, double lat2, double lon2) {
        double f1 = StrictMath.toRadians(lat1), f2 = StrictMath.toRadians(lat2), dl = StrictMath.toRadians(EarthProj.normaliseLon(lon2 - lon1));
        return StrictMath.toDegrees(StrictMath.atan2(StrictMath.sin(dl) * StrictMath.cos(f2),
                StrictMath.cos(f1) * StrictMath.sin(f2) - StrictMath.sin(f1) * StrictMath.cos(f2) * StrictMath.cos(dl)));
    }

    /** The point `km` from (lat, lon) along a bearing (degrees clockwise from north), on the sphere. */
    public static double[] destination(double atLat, double atLon, double bearingDeg, double km) {
        double f = StrictMath.toRadians(atLat), b = StrictMath.toRadians(bearingDeg), d = km * 1000 / EarthProj.RADIUS_M;
        double f2 = StrictMath.asin(StrictMath.sin(f) * StrictMath.cos(d) + StrictMath.cos(f) * StrictMath.sin(d) * StrictMath.cos(b));
        double l2 = StrictMath.atan2(StrictMath.sin(b) * StrictMath.sin(d) * StrictMath.cos(f), StrictMath.cos(d) - StrictMath.sin(f) * StrictMath.sin(f2));
        return new double[] { StrictMath.toDegrees(f2), EarthProj.normaliseLon(atLon + StrictMath.toDegrees(l2)) };
    }

    /* ----- a pixel ----- */

    /** The latitude and longitude under a screen point, in the tier this view draws - on the globe, round its centre with its roll; on the plane, its plot's; null off the planet. */
    public double[] latLonAt(double sx, double sy) {
        double[] ll = new double[2];
        if (tier() == PLANE) {
            double s = planeScale();
            return plane.latLonOfPlot(planeX() + (sx - width / 2) / s, planeY() + (sy - height / 2) / s, ll) ? ll : null;
        }
        double m = kmPx * 1000;
        return ortho.inverse((sx - width / 2) * m, (height / 2 - sy) * m, ll) ? ll : null;
    }

    /** Where a latitude and longitude lie on the screen, {x, y} in pixels, in the tier this view draws: null on the globe's far side (or the plane's antipode). */
    public double[] at(double atLat, double atLon) {
        double[] xy = new double[2];
        if (tier() == PLANE) {
            if (!plane.plotOf(atLat, atLon, xy)) return null;
            double s = planeScale();
            return new double[] { (xy[0] - planeX()) * s + width / 2, (xy[1] - planeY()) * s + height / 2 };
        }
        if (!ortho.forward(atLat, atLon, xy)) return null;
        double m = kmPx * 1000;
        return new double[] { width / 2 + xy[0] / m, height / 2 - xy[1] / m };
    }

    /** Whether a screen point lies on the planet as this view draws it: the globe's disc, or on the plane within its reach (EarthProj.onPlanet()). */
    public boolean onPlanet(double sx, double sy) {
        if (tier() == PLANE) {
            double s = planeScale();
            return EarthProj.onPlanet(EarthProj.metresX(planeX() + (sx - width / 2) / s), EarthProj.metresY(planeY() + (sy - height / 2) / s));
        }
        double m = kmPx * 1000;
        return EarthProj.Ortho.onDisc((sx - width / 2) * m, (height / 2 - sy) * m);
    }

    /* ----- the handoff to the city's plane (spec-globe 3.2) ----- */

    /** The plane's view this one hands to: its centre's plot, across... */
    public double planeX() {
        double[] xy = new double[2];
        return plane.plotOf(lat, lon, xy) ? xy[0] : Double.NaN;
    }

    /** ...and down... */
    public double planeY() {
        double[] xy = new double[2];
        return plane.plotOf(lat, lon, xy) ? xy[1] : Double.NaN;
    }

    /** ...and its scale, screen pixels a plot (MapFrame's): World.PLOT_M over the metres a pixel. */
    public double planeScale() { return World.PLOT_M / (kmPx * 1000); }

    /** The plane's view taken back: centred on plot (x, y) at `pxAPlot` screen pixels a plot. */
    public void fromPlane(double x, double y, double pxAPlot) {
        double[] ll = new double[2];
        if (plane.latLonOfPlot(x, y, ll)) {
            lat = ll[0];
            lon = ll[1];
        }
        kmPx = World.PLOT_M / pxAPlot / 1000;
        clampZoom();
    }

    /* ----- the GROUND tier's pieces ----- */

    /**
     * A piece of the GROUND tier: the screen's pixels sx0 to sx0 + w - 1
     * across and sy0 to sy0 + h - 1 down, a World class each, drawn by the
     * far rule through the globe's camera (EarthTerrain.globe()) on the
     * Earth world a key names - the city's. Sea off the disc (onPlanet()
     * says where). On the view's worker, as the far nodes are.
     */
    public byte[] groundPiece(World.Key key, int sx0, int sy0, int w, int h) {
        World world = World.of(key);
        if (!world.isEarth()) throw new IllegalArgumentException("the globe draws an Earth world's ground: " + key);
        EarthTerrain e = world.earthTerrain();
        byte[] out = new byte[w * h];
        e.farTerrain(e.globe(ortho, kmPx, width, height, sx0, sy0, w, h), out);
        return out;
    }
}
