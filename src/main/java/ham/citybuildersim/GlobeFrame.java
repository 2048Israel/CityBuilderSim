package ham.citybuildersim;

/**
 * The Earth's map as a globe, without the toolkit: where the view looks on the planet, how close and which way is up, how a notch of the wheel, a drag, a held key and Fit move it, which of three pictures it draws - the planet's picture, the ground through the globe, or the city's plane - and where a pixel is on the planet and on the city's plane; what the land office's map on Earth and the founding screen's globe draw by (since 0.7.135) and GlobeCheck and MapCheck 13 hold.
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
 *     MapFrame at planeX(), planeY() and planeScale() - while the view's
 *     centre is within the reach of the city; past it the GROUND tier
 *     goes on closer than the handoff (MS1, below), drawing the plane's own
 *     ground through the camera (MS2, below).
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
 * WHERE CLOSE VIEWS GO (MS1, Jerus 2026-10-26: "you cant zoom in on areas of
 * the map far away from the city, and if zoomed in there is a wall that you
 * can cross"; spec-globe 3.4 Q3's "away from the city the wheel stops at the
 * handoff, and on the plane a pan stops at the reach" is superseded). The
 * wheel reaches leastKmPx() anywhere and a drag, a pan and a held key go
 * anywhere at every scale: the reach no longer stops anything. It decides
 * which picture a view closer than the handoff draws - the city's PLANE
 * while its centre is within the reach (REACH_KM, or the city's own ground
 * and its offers and REACH_MARGIN_KM round them where they reach farther:
 * reachKm()), the GROUND tier beyond it, the far rule through the globe's
 * camera at any scale (EarthTerrain.globe() takes any), as the founding
 * screen draws it. tierOf(kmPx) is still the scale's tier and tier() the
 * view's. The wall was the old stop: clampPan() pulled a centre back to the
 * reach, 300 km from the city, at the plane's scales.
 *
 * THE SAME GROUND PAST THE REACH (MS2, Jerus 2026-10-26: "look at the wierd
 * resolution by just panning a few more kms": the far rule through the
 * globe's camera has no plot under a pixel, so its forest is a dither and a
 * small lake a disc, where the plane's are smooth patches and frayed lakes).
 * Closer than the handoff the GROUND tier past the reach draws the plane's
 * own far nodes through the camera at the scale's stride - 8, 4, 2 or 1
 * plots a pixel, the nodes the plane draws inside the reach - wherever the
 * world's square holds the ground (planeDrawnPastReach(), drawsHold(),
 * planeHolds()); the far rule (groundPiece()) is left to what the square does
 * not hold, the fifth of the planet farthest from the city. The legend and
 * the hover are the plane's at those scales (legendTier(), planeHolds()).
 * planeDrawsKm() is as it was from the handoff wider.
 *
 * THE GROUND PAST THE HAND-OVER, TRUE IN SHAPE (MS3, Jerus 2026-10-27: the
 * near view of Goose Bay from a Singapore city is drawn in streaks along the
 * line to the city, GlobeCheck 2d). The city's plane is Lambert's equal-area
 * projection, whose shape distortion 2 / (1 + cos c) at an angle c from the
 * city stretches the plane's noise into those streaks. Closer than the handoff
 * the plane's own nodes are drawn where that distortion is no more than
 * SHAPE_HANDOVER (handoverKm(), 5,908 km; ground(), drawsHold(), planeHolds());
 * everywhere else on the planet, the world's square or not, a chart draws it:
 * the north-up orthographic piece of the planet about the nearest anchor of a
 * lattice fixed on it (chart(), chartFor(), CHART_DEG), its noise the planet's
 * (EarthTerrain.chart()), cut into nodes as the plane's are (chartAt() says
 * where a pixel lies on it). Nothing the model reads changes.
 *
 * A zoom keeps the point under the pointer under it, and a drag the point
 * grabbed under the pointer, on the globe as on the plane (spec-globe 4's
 * E8 row): the view centre is solved from the point, its place on the
 * screen and the roll (centreFor()). Pure: nothing in the model reads it.
 *
 * ON SCREEN (0.7.135, batch E9; spec-globe 3.2 to 3.8 and 3.11). ui/MapView
 * draws the expanded map on Earth by it and ui/FoundingScreen its wide
 * views: W, A, S and D turn it as a drag of MapFrame.keyStep()'s pixels
 * (keyPan()); the GROUND tier near the city is the plane's own far nodes
 * through the camera (ground(), ★E9: the two projections composed as one
 * matrix on the point's unit vector), out to planeDrawsKm() - the reach
 * and the view's half-diagonal at the handoff, so a view at the swap is the
 * plane's own ground to its corners - and past it the far rule through the
 * camera (groundPiece()), the last picture carried to its own pixels
 * meanwhile (Carry); the swap crossfades over CROSSFADE_NOTCHES (crossfade());
 * the roll eases back to north up over ROLL_EASE_KM past the reach, not in
 * a step at its edge (E8's ★3); the scale bar is true at the centre and
 * says so; the plot and whose it is under a pixel (plotAt(), planeHolds())
 * are the hover's and a click's; and the founding globe, with no city and
 * no reach, hands off at half a pixel of its canvas (FOUNDING_HANDOFF_KM,
 * halfPixelKm()).
 */
public final class GlobeFrame {

    /** The tiers, by the scale (spec-globe 3.1): the planet's picture, the ground through the globe, the city's plane. */
    public static final int PICTURE = 0, GROUND = 1, PLANE = 2;

    /** The handoff from the globe to the city's plane, in plots a pixel: 16 (spec-globe 3.2, Jerus's Q6) - 0.48 km a pixel, where the two part by a third of a pixel on the expanded map, a power of two the far nodes already use (MapFrame.nodePlots()). */
    public static final int HANDOFF_PLOTS = 16;

    /** ...in km a pixel: HANDOFF_PLOTS x World.PLOT_M, 0.48. */
    public static final double HANDOFF_KM = HANDOFF_PLOTS * World.PLOT_M / 1000;

    /** Closer than the handoff the view draws the city's plane while its centre is within this of the city, in km, and the GROUND tier beyond it - it stops no wheel and no pan since MS1 (spec-globe 3.4's Q3 stop is superseded): 300 (spec-globe 3.4, Jerus's Q3) - 1.16 px apart at the swap there with the roll eased (spec-globe 3.3). */
    public static final double REACH_KM = 300;

    /** ...or within this of the city's own ground and its offers, in km, where they reach farther than REACH_KM: 50 (spec-globe 3.4's est., ★E8 - the ground the land office shows round what it sells; where the plane is drawn, since MS1, and no longer where the view may go). */
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

    /** The swap crossfades over this many notches of the wheel inside the handoff: 1 (spec-globe 3.2's est., ★E9) - the globe laid over the plane from whole at the handoff to gone a notch in, hiding the third of a pixel they part by and the change of drawing. */
    public static final int CROSSFADE_NOTCHES = 1;

    /** Past the reach the roll eases back to north up over this many km (★E9; E8's ★3): REACH_MARGIN_KM, the margin the reach keeps round the city's ground - so a pan across the reach's edge at the plane's scales turns the view by degrees over 50 km instead of in one step. */
    public static final double ROLL_EASE_KM = REACH_MARGIN_KM;

    /** The city's edge is drawn through the globe's camera once the city is at least this many pixels across: 4 (spec-globe 3.7's est.) - under it the ring alone marks it. */
    public static final double EDGE_FROM_PX = 4;

    /** The founding screen's globe, in pixels: 560 x 270 (ui/FoundingScreen's right column, 0.7.121; GlobeFrame's since 0.7.135, so a harness draws what it draws). */
    public static final double FOUNDING_W = 560, FOUNDING_H = 270;

    /**
     * The scale at which a view of w x h pixels and the plane centred where it
     * looks part by half a pixel at its corners, km a pixel (spec-globe 3.2):
     * a point rho out from the middle is the angle c = 2 asin(rho / 2R) from
     * the centre on Lambert's plane, and R sin c = rho sqrt(1 - rho^2/4R^2)
     * out on the globe, so at the corner, D pixels out at k km a pixel, the
     * two part by D (1 - sqrt(1 - (Dk / 2R)^2)) pixels - half a pixel at
     * k = (2R / D) sqrt(1 - (1 - 1/2D)^2), the study's 2R / D^1.5 to first
     * order: 0.58 on the expanded map, 2.32 on the founding globe.
     */
    public static double halfPixelKm(double w, double h) {
        double d = Math.hypot(w, h) / 2, q = 1 - 1 / (2 * d);
        return 2 * EarthProj.RADIUS_M / 1000 / d * Math.sqrt(1 - q * q);
    }

    /** The founding globe hands its view to the ground drawn round its centre at this, km a pixel (★E9, spec-globe 3.2's choice): halfPixelKm() of its canvas, 2.32 - under half a pixel at the swap, as the map's 16 plots is, where E4's 4 km parted by 1.5. */
    public static final double FOUNDING_HANDOFF_KM = halfPixelKm(FOUNDING_W, FOUNDING_H);

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

    /** The tier a view draws at a scale, its centre within the city's reach or not (MS1): tierOf(), except GROUND where that says PLANE and the centre is past the reach - the plane is drawn only round the city. */
    public static int tierOf(double kmPx, boolean withinReach) {
        int t = tierOf(kmPx);
        return t == PLANE && !withinReach ? GROUND : t;
    }

    /** The tier this view draws: its scale's (tierOf()), the GROUND tier in place of the PLANE where its centre is past the reach (MS1). */
    public int tier() {
        int t = tierOf(kmPx);
        return t == PLANE && !withinReach(lat, lon) ? GROUND : t;
    }

    /**
     * The tier the legend is drawn for (MS2): the scale's own, tierOf() - the plane's at every scale closer than the handoff, past the
     * reach too, where the ground drawn is the plane's own (planeDrawnPastReach()) and so is what the legend says of it - not tier(),
     * which is the GROUND tier there for the camera it is drawn through.
     */
    public int legendTier() { return tierOf(kmPx); }

    /** The view's orthographic projection: round its centre, turned by its roll. */
    public EarthProj.Ortho ortho() { return ortho; }

    /* ----- the reach (spec-globe 3.4) ----- */

    /** How far from the city the view's centre is drawn on the city's plane when it is closer than the handoff, km (it stops nothing since MS1). */
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

    /** The roll a view centred at (atLat, atLon) at a scale takes: the eased share of the plane's up there within the reach, north up beyond it - since 0.7.135 (★E9) back to north up over ROLL_EASE_KM past the reach rather than in one step at its edge. */
    public double rollFor(double atLat, double atLon, double atKmPx) {
        double e = ease(atKmPx);
        if (e <= 0) return 0;
        double past = cityKm(atLat, atLon) - reachKm;
        if (past >= ROLL_EASE_KM) return 0;
        if (past > 0) e *= 1 - past / ROLL_EASE_KM;
        return e * planeRollAt(atLat, atLon);
    }

    /** The globe's share of the picture at a scale, over the plane's: 1 at the handoff and wider, 0 CROSSFADE_NOTCHES notches inside it and closer, by notches between (spec-globe 3.2's crossfade; within the reach, where the swap is). */
    public static double crossfade(double kmPx) {
        if (kmPx >= HANDOFF_KM) return 1;
        double t = StrictMath.log(kmPx / HANDOFF_KM) / (CROSSFADE_NOTCHES * StrictMath.log(ZOOM_STEP)) + 1;
        return t <= 0 ? 0 : t >= 1 ? 1 : t;
    }

    /** The globe's share of the picture in this view (MS1): crossfade() of its scale on the plane's tier, whole in the others - past the reach the GROUND tier is all there is, with no plane under it. */
    public double crossfade() { return tier() == PLANE ? crossfade(kmPx) : 1; }

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

    /** Zooms about a screen point, the km a pixel times a factor (under one closer): the point on the planet under it stays under it - on the globe, on the plane, and across the handoff to within their parting - unless a clamp stops it. The wheel reaches leastKmPx() anywhere (MS1: it stopped at the handoff away from the city). */
    public void zoomAt(double sx, double sy, double factor) {
        double[] p = latLonAt(sx, sy);
        double to = Math.max(leastKmPx(), Math.min(mostKmPx(), kmPx * factor));
        if (p == null) {
            kmPx = to;
            clampZoom();
            return;
        }
        put(p[0], p[1], sx, sy, to);
        clampZoom();
    }

    /** ...by notches of the wheel, ZOOM_STEP each, in (positive) or out. */
    public void zoomNotches(double sx, double sy, double notches) {
        zoomAt(sx, sy, Math.pow(ZOOM_STEP, -notches));
    }

    /** A drag from (sx0, sy0) to (sx1, sy1): the point grabbed stays under the pointer - the globe turns, the plane slides - at every scale and anywhere on the planet, the centre stopping at no reach (MS1: at the plane's scales it stopped there, the wall). A grab off the planet turns the globe as a drag of its centre would. */
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

    /** A frame of the keys held (0.7.135, spec-globe 3.11): the globe turned as a drag of MapFrame.keyStep()'s pixels from the centre would turn it, anywhere at every scale (MS1). */
    public void keyPan(double[] velocity, double seconds) {
        double[] d = MapFrame.keyStep(velocity, seconds);
        if (d[0] != 0 || d[1] != 0) pan(d[0], d[1]);
    }

    /** This view at another size and scale - its centre and its roll as they are (the half-resolution frame a moving view is drawn at): w x h pixels at kmPx. */
    public GlobeFrame copy(double w, double h, double atKmPx) {
        return new GlobeFrame(this, w, h, atKmPx);
    }

    private GlobeFrame(GlobeFrame f, double w, double h, double atKmPx) {
        this.width = Math.max(1, w);
        this.height = Math.max(1, h);
        this.cityLat = f.cityLat;
        this.cityLon = f.cityLon;
        this.plane = f.plane;
        this.lat = f.lat;
        this.lon = f.lon;
        this.kmPx = atKmPx;
        this.roll = f.roll;
        this.reachKm = f.reachKm;
        this.ortho = f.ortho;
        this.drawsKm = f.planeDrawsKm();
        this.pastReach = f.planeDrawnPastReach();
    }

    /** Whether two views draw the same picture: their size, centre, scale, roll and reach - which between them decide the tier (MS1), so two views of different tiers are never the same. */
    public boolean sameView(GlobeFrame o) {
        return o != null && o.width == width && o.height == height && o.lat == lat && o.lon == lon && o.kmPx == kmPx && o.roll == roll
                && o.reachKm == reachKm && o.cityLat == cityLat && o.cityLon == cityLon;
    }

    /**
     * The view at scale `to` with (pLat, pLon) at screen point (sx, sy), in the tier it lands in (MS1): at the plane's scales on the plane by its
     * plots, where that puts the centre within the reach; otherwise - a wider scale, or a centre the plane would put past the reach - on the globe
     * by centreFor() with the roll it will take there, so a zoom or a drag keeps its point under the pointer in the tier the view comes to.
     */
    private void put(double pLat, double pLon, double sx, double sy, double to) {
        kmPx = to;
        if (tierOf(to) == PLANE) {
            double[] xy = new double[2], ll = new double[2];
            if (plane.plotOf(pLat, pLon, xy)) {
                double s = World.PLOT_M / (to * 1000);
                if (plane.latLonOfPlot(xy[0] - (sx - width / 2) / s, xy[1] - (sy - height / 2) / s, ll) && cityKm(ll[0], ll[1]) <= reachKm) {
                    lat = ll[0];
                    lon = ll[1];
                    update();
                    return;
                }
            }
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

    /** The scale within its bounds (the wheel stops at leastKmPx() and mostKmPx(), and nowhere else since MS1); the centre off the poles; the roll. */
    private void clampZoom() {
        if (!(kmPx > 0)) kmPx = mostKmPx();
        kmPx = Math.max(leastKmPx(), Math.min(mostKmPx(), kmPx));
        lat = Math.max(-POLE_LAT, Math.min(POLE_LAT, lat));
        update();
    }

    /** ...for a pan: the scale and the poles' clamps, and no pull back to the reach (MS1: it was the wall - the centre stopped 300 km from the city at the plane's scales). */
    private void clampPan() {
        kmPx = Math.max(leastKmPx(), Math.min(mostKmPx(), kmPx));
        lat = Math.max(-POLE_LAT, Math.min(POLE_LAT, lat));
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

    /** Whether a screen point lies on the planet as this view draws it: the globe's disc, or the plane's own (EarthProj.onPlanet()) where this view draws the plane. */
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

    /**
     * ...a piece's codes (ET1 slice D; spec-earth2-terrain 9.3): each pixel's class and relief as the planet's
     * picture holds a texel's (GlobePicture.code()) - the far rule's relief through the same camera - so the
     * GROUND tier draws the three greys and the shaded forest as the picture does. Its class is
     * groundPiece()'s, byte for byte; an earth-1 file's and every flat pixel's code is its class.
     */
    public byte[] groundCodes(World.Key key, int sx0, int sy0, int w, int h) {
        World world = World.of(key);
        if (!world.isEarth()) throw new IllegalArgumentException("the globe draws an Earth world's ground: " + key);
        EarthTerrain e = world.earthTerrain();
        byte[] out = new byte[w * h], relief = new byte[w * h];
        e.farTerrain(e.globe(ortho, kmPx, width, height, sx0, sy0, w, h), out, relief);
        for (int k = 0; k < out.length; k++) out[k] = GlobePicture.code(out[k], relief[k]);
        return out;
    }

    /* =====================================================================
       THE GLOBE ON SCREEN (0.7.135, batch E9; docs/studies/spec-globe.md 3.5
       to 3.7): the city's plane under the globe's pixels, the GROUND tier
       near the city from the plane's own far nodes, the overlay through the
       camera, the scale bar
       ===================================================================== */

    /** The plot under a screen point in the tier this view draws, into xy: on the plane its own; on the globe through the camera to the city's plane (EarthProj.plotOf()). False off the planet, or at the plane's antipode. */
    public boolean plotAt(double sx, double sy, double[] xy) {
        if (tier() == PLANE) {
            double s = planeScale();
            xy[0] = planeX() + (sx - width / 2) / s;
            xy[1] = planeY() + (sy - height / 2) / s;
            return EarthProj.onPlanet(EarthProj.metresX(xy[0]), EarthProj.metresY(xy[1]));
        }
        double[] ll = latLonAt(sx, sy);
        return ll != null && plane.plotOf(ll[0], ll[1], xy);
    }

    /** Where a plot of the city's plane lies on the screen, {x, y} in pixels, in the tier this view draws: null on the globe's far side or off the planet. */
    public double[] screenOfPlot(double x, double y) {
        if (tier() == PLANE) {
            double s = planeScale();
            return new double[] { (x - planeX()) * s + width / 2, (y - planeY()) * s + height / 2 };
        }
        double[] ll = new double[2];
        if (!plane.latLonOfPlot(x, y, ll)) return null;
        return at(ll[0], ll[1]);
    }

    /** Whether a screen point is drawn from the city's plane - within planeDrawsKm() of the city and the world's square, at the plane's scales (MS2: planeDrawnPastReach()) on the world's square within handoverKm() of the city (MS3: farther the charts draw it), or anywhere on the plane's own tier: the plane's answer is the one there (the hover's, a click's). */
    public boolean planeHolds(double sx, double sy) {
        double[] ll = latLonAt(sx, sy);
        if (ll == null || (tier() != PLANE && !planeDrawnPastReach() && cityKm(ll[0], ll[1]) > planeDrawsKm())) return false;
        if (tier() != PLANE && planeDrawnPastReach() && !(cityUp(sx, sy) >= composed().cosDraws)) return false;
        double[] xy = new double[2];
        return plotAt(sx, sy, xy) && xy[0] >= 0 && xy[1] >= 0 && xy[0] < World.SIDE && xy[1] < World.SIDE;
    }

    /**
     * How far from the city the globe draws the city's plane's own far nodes,
     * km, at the handoff and wider (★E9; at the plane's scales wherever the
     * square holds the ground, planeDrawnPastReach(), MS2): the reach and the
     * view's half-diagonal at the handoff, so a
     * view at the handoff with its centre within the reach - every view the
     * wheel can take across it - is the plane's own ground to its corners,
     * and the swap shows only the projections' parting (within 1,000 km of
     * the city Lambert's plane is true to a third of a percent). Past it the
     * globe draws its own ground (groundPiece()). A copy keeps its view's.
     */
    public double planeDrawsKm() {
        return Double.isNaN(drawsKm) ? reachKm + Math.hypot(width, height) / 2 * HANDOFF_KM : drawsKm;
    }

    /** A copy's planeDrawsKm(), its view's; NaN in a view of its own. */
    private double drawsKm = Double.NaN;

    /**
     * Whether the city's plane's own far nodes are drawn wherever the world's square holds the ground within handoverKm() (MS3; the charts
     * beyond) at a scale, the reach and planeDrawsKm() or not (MS2, Jerus 2026-10-26: "look at the wierd resolution by just panning a few more kms"): every scale closer
     * than the handoff, the plane's own - tierOf() says PLANE. There the GROUND tier past the reach draws the plane's ground at the
     * scale's own stride (MapFrame.nodePlotsAt(): 8, 4, 2 or 1 plots a pixel) through the camera, as the plane draws it inside the
     * reach, and the far rule (groundPiece()) only where the square does not hold the ground - the fifth of the planet farthest from
     * the city. At the handoff and wider the plane's nodes are drawn out to planeDrawsKm() alone, as they were.
     */
    public static boolean planeDrawnPastReach(double kmPx) { return tierOf(kmPx) == PLANE; }

    /** ...for this view: its scale's, or a copy's view's (the half-resolution frame a moving view is drawn at keeps the rule of the view it is a copy of). */
    public boolean planeDrawnPastReach() { return pastReach == null ? planeDrawnPastReach(kmPx) : pastReach; }

    /** A copy's planeDrawnPastReach(), its view's; null in a view of its own. */
    private Boolean pastReach;

    /* ----- the charts: the near view far from the city, true in shape (MS3, Jerus 2026-10-27: Goose Bay from a Singapore city drawn in streaks) ----- */

    /**
     * The city's plane's shape distortion, k / h, past which the near view is drawn from the charts instead: 1.25 (MS3). The
     * plane is Lambert's equal-area projection about the city, which at an angle c from it squashes the ground along the
     * line to the city by h = cos(c / 2) and stretches it across by k = 1 / cos(c / 2), so the round patches of the plane's noise
     * are ellipses k / h = 2 / (1 + cos c) to 1 on the planet, long along the line to the city (GlobeCheck 2d measures it:
     * 4.5 to 1 at Goose Bay from Singapore). A quarter is under what the eye reads in irregular patches, and the plane's own
     * ground, which the hover reads plot for plot, still reaches every neighbouring continent: 53.1 degrees, 5,908 km.
     */
    public static final double SHAPE_HANDOVER = 1.25;

    /** The band past the hand-over over which the sphere's noise is blended in from the plane's, km along the ground: 100 (MS3) - the noise's widest octave is 480 m (the relief's 1.9 km), so over 100 km the pattern becomes the sphere's with no place to see it change. */
    public static final double SHAPE_BAND_KM = 100;

    /** The charts' anchors' lattice, degrees of latitude between rows (and about as many of longitude on the equator): 10 (MS3) - the view's chart is the anchor nearest its centre, within 1,300 km of every pixel at the plane's widest scale, so true in shape to 2%. */
    public static final double CHART_DEG = 10;

    /** The plane's shape distortion at an angle from the city whose cosine is `up`: 2 / (1 + up) - what ground()'s k^2 / R^2 is, Snyder's k / h. */
    public static double distortion(double up) { return 2 / (1 + up); }

    /** The cosine of the angle from the city at which the plane's shape distortion is SHAPE_HANDOVER: 2 / SHAPE_HANDOVER - 1, 0.6. */
    public static double handoverUp() { return 2 / SHAPE_HANDOVER - 1; }

    /** ...and the distance along the ground, km: 5,908. */
    public static double handoverKm() { return EarthProj.RADIUS_M * StrictMath.acos(handoverUp()) / 1000; }

    /** One chart's anchor: where its orthographic plane touches the planet (lat, lon, degrees), and the number it is kept under (a row and a column of the lattice packed: row << 8 | column). */
    public record Chart(int id, double lat, double lon) {
        /** The chart's projection: the globe about the anchor, north up. */
        public EarthProj.Ortho ortho() { return EarthProj.ortho(lat, lon, 0); }
    }

    /** The anchors in row `r` of the lattice (rows run from -90 in steps of CHART_DEG): as many as the row's circle is long in CHART_DEG's, at least one. */
    private static int chartsInRow(int r) {
        return Math.max(1, (int) Math.round(360 * StrictMath.cos(StrictMath.toRadians(-90 + r * CHART_DEG)) / CHART_DEG));
    }

    /**
     * The chart a point is drawn from: the nearest anchor of a lattice fixed on the planet - rows every CHART_DEG of
     * latitude from -90 to 90, row i holding max(1, round(360 cos(lat_i) / CHART_DEG)) anchors evenly from longitude -180 -
     * found among the two rows round the point, by great-circle distance. A function of the point alone, so a view that
     * pans across into another anchor's cell changes chart (about once in 1,000 km of panning).
     */
    public static Chart chartFor(double lat, double lon) {
        double l = EarthProj.normaliseLon(lon);
        int rows = (int) Math.round(180 / CHART_DEG) + 1;
        int r0 = Math.max(0, Math.min(rows - 1, (int) Math.floor((lat + 90) / CHART_DEG)));
        Chart best = null;
        double bestM = Double.POSITIVE_INFINITY;
        for (int r = r0; r <= Math.min(rows - 1, r0 + 1); r++) {
            int count = chartsInRow(r);
            double across = 360.0 / count, anchorLat = -90 + r * CHART_DEG;
            int col = Math.floorMod(Math.round((l + 180) / across), count);
            double anchorLon = EarthProj.normaliseLon(-180 + col * across);
            double m = EarthProj.distanceM(lat, l, anchorLat, anchorLon);
            if (m < bestM) {
                bestM = m;
                best = new Chart(r << 8 | col, anchorLat, anchorLon);
            }
        }
        return best;
    }

    /** The chart this view's ground is drawn from past the hand-over: its centre's (chartFor()). */
    public Chart chart() { return chartFor(lat, lon); }

    /** The pixels of a far node of the city's plane, its colours (MapTiles.nodePixels()) - null where it is not drawn yet. */
    public interface NodePixels { int[] at(long nx, long ny); }

    /** A pixel's colour beyond the reach, or where the plane's node is not drawn: by its place on the screen, and its point on the planet as a unit vector in the camera's frame (vx, vy, vz: Ortho.inverse()'s), which latOf() and lonOf() turn into degrees when it is wanted. */
    public interface Beyond { int colour(int i, int j, double vx, double vy, double vz); }

    /** The latitude of a point given as Beyond's unit vector, degrees: Ortho.inverse()'s. */
    public static double latOf(double vx, double vy, double vz) {
        return StrictMath.toDegrees(StrictMath.atan2(vz, Math.sqrt(vx * vx + vy * vy)));
    }

    /** ...and its longitude, in [-180, 180). */
    public double lonOf(double vx, double vy) {
        return EarthProj.normaliseLon(lon + StrictMath.toDegrees(StrictMath.atan2(vy, vx)));
    }

    /**
     * The GROUND tier near the city (★E9; spec-globe 3.5's "cheaper GROUND
     * tier"): the screen's pixels sx in [i0, i1), sy in [j0, j1) into out
     * (outW a row), each pixel's centre through the globe to the city's
     * plane and, within planeDrawsKm() of the city - or, at the plane's
     * scales (planeDrawnPastReach(), MS2), wherever the world's square holds
     * the pixel and the plane's shape distortion is no more than SHAPE_HANDOVER
     * (MS3) - the colour of the plane's far node pixel under it at
     * `plots` plots a pixel (MapFrame.nodePlots()'s level) - exactly the
     * plane's ground, its city and edge with it, so the swap shows only the
     * projections' parting; beyond it, off the square, and where a node is
     * not drawn yet, `beyond`'s colour; off the disc
     * `space`. The two projections composed as one matrix on the point's
     * unit vector, not through its latitude and longitude (planeAt(), which
     * a harness holds to the camera's and the plane's own). Returns how many
     * pixels the plane draws found no node.
     */
    public int ground(long plots, NodePixels nodes, Beyond beyond, int space, int[] out, int outW, int i0, int i1, int j0, int j1) {
        return ground(plots, nodes, null, beyond, space, out, outW, i0, i1, j0, j1);
    }

    /**
     * ...and, at the plane's scales (MS3), where the plane does not draw (past handoverKm() of the city, or off the world's
     * square) the view's chart (chart()): `charts` gives the chart node (nx, ny)'s pixels, `plots` plots a pixel and
     * MapFrame.NODE_PX a side, as `nodes` gives the plane's - the node (floor(cx / NODE_PX), floor(cy / NODE_PX)) of the
     * pixel's place (cx, cy) on the chart in chart pixels (chartAt()), the pixel within it. Where `charts` is null, or
     * the node is not in, `beyond`'s colour; the pixels the chart drew found no node count in the return with the plane's.
     */
    public int ground(long plots, NodePixels nodes, NodePixels charts, Beyond beyond, int space, int[] out, int outW, int i0, int i1, int j0, int j1) {
        Composed c = composed();
        int n = MapFrame.NODE_PX;
        double span = (double) n * plots, perSpan = 1 / span;
        double half = World.SIDE / 2.0, rPlots = EarthProj.RADIUS_M / World.PLOT_M;
        boolean drawCharts = charts != null && c.wholeSquare;
        // The camera's east and north on the unit sphere step by these a pixel across.
        double step = c.m / EarthProj.RADIUS_M, de = step * c.cosR, dn = step * c.sinR;
        long lastNx = Long.MIN_VALUE, lastNy = Long.MIN_VALUE, lastCx = Long.MIN_VALUE, lastCy = Long.MIN_VALUE;
        int[] node = null, chartNode = null;
        int missing = 0;
        for (int j = j0; j < j1; j++) {
            double y = (height / 2 - j - 0.5) * step, x0 = (i0 + 0.5 - width / 2) * step;
            double e0 = x0 * c.cosR - y * c.sinR, n0 = x0 * c.sinR + y * c.cosR;
            int row = j * outW;
            for (int i = i0; i < i1; i++) {
                double e = e0 + (i - i0) * de, nn = n0 + (i - i0) * dn;
                double s2 = e * e + nn * nn;
                if (s2 > 1) {
                    out[row + i] = space;
                    continue;
                }
                double a = Math.sqrt(1 - s2);
                double up = c.ue * e + c.un * nn + c.ua * a;
                boolean planeDraws = false;
                if (up >= c.cosDraws && 1 + up > 0) {
                    double k = rPlots * Math.sqrt(2 / (1 + up));
                    double px = half + k * (c.ee * e + c.en * nn + c.ea * a), py = half - k * (c.ne * e + c.nn * nn + c.na * a);
                    // The world's square holds the pixel (MS2: where the plane is drawn past planeDrawsKm() the square is all there is to hold it).
                    if (px >= 0 && py >= 0 && px < World.SIDE && py < World.SIDE) {
                        planeDraws = true;
                        double fx = px * perSpan, fy = py * perSpan;
                        long nx = (long) Math.floor(fx), ny = (long) Math.floor(fy);
                        if (nx != lastNx || ny != lastNy) {
                            lastNx = nx;
                            lastNy = ny;
                            node = nodes.at(nx, ny);
                        }
                        if (node != null) {
                            int u = (int) ((fx - nx) * n), v = (int) ((fy - ny) * n);
                            out[row + i] = node[(v < n ? v : n - 1) * n + (u < n ? u : n - 1)];
                            continue;
                        }
                        missing++;
                    }
                }
                if (drawCharts && !planeDraws) {
                    // The chart's pixel under this one: the anchor's east and north on the unit sphere, in chart pixels (north is up, rows run south).
                    double fx = (c.xe * e + c.xn * nn + c.xa * a) * rPlots * perSpan, fy = -(c.ye * e + c.yn * nn + c.ya * a) * rPlots * perSpan;
                    long nx = (long) Math.floor(fx), ny = (long) Math.floor(fy);
                    if (nx != lastCx || ny != lastCy) {
                        lastCx = nx;
                        lastCy = ny;
                        chartNode = charts.at(nx, ny);
                    }
                    if (chartNode != null) {
                        int u = (int) ((fx - nx) * n), v = (int) ((fy - ny) * n);
                        out[row + i] = chartNode[(v < n ? v : n - 1) * n + (u < n ? u : n - 1)];
                        continue;
                    }
                    missing++;
                }
                out[row + i] = beyond.colour(i, j, a * c.cosL0 - nn * c.sinL0, e, a * c.sinL0 + nn * c.cosL0);
            }
        }
        return missing;
    }

    /** The plot of the city's plane under a screen point by ground()'s matrix, into xy: false off the disc or at the plane's antipode - what a harness holds to the camera and the plane's own projections (plotAt()). */
    public boolean planeAt(double sx, double sy, double[] xy) {
        Composed c = composed();
        double x = (sx - width / 2) * c.m, y = (height / 2 - sy) * c.m;
        double e = (x * c.cosR - y * c.sinR) / EarthProj.RADIUS_M, nn = (x * c.sinR + y * c.cosR) / EarthProj.RADIUS_M;
        double s2 = e * e + nn * nn;
        if (s2 > 1) return false;
        double a = Math.sqrt(1 - s2);
        double up = c.ue * e + c.un * nn + c.ua * a;
        if (!(1 + up > 0)) return false;
        double k = EarthProj.RADIUS_M * Math.sqrt(2 / (1 + up));
        xy[0] = k * (c.ee * e + c.en * nn + c.ea * a) / World.PLOT_M + World.SIDE / 2.0;
        xy[1] = World.SIDE / 2.0 - k * (c.ne * e + c.nn * nn + c.na * a) / World.PLOT_M;
        return true;
    }

    /** Whether a screen point lies within the reach, by ground()'s matrix: the angle from the city no wider than the reach's. */
    public boolean reachHolds(double sx, double sy) {
        Composed c = composed();
        double x = (sx - width / 2) * c.m, y = (height / 2 - sy) * c.m;
        double e = (x * c.cosR - y * c.sinR) / EarthProj.RADIUS_M, nn = (x * c.sinR + y * c.cosR) / EarthProj.RADIUS_M;
        double s2 = e * e + nn * nn;
        return s2 <= 1 && c.ue * e + c.un * nn + c.ua * Math.sqrt(1 - s2) >= c.cosReach;
    }

    /**
     * ...and within planeDrawsKm() - or, at the plane's scales (planeDrawnPastReach(), MS2), anywhere the world's square holds it within
     * handoverKm() of the city (MS3: past it the charts draw): where ground() draws the city's plane.
     */
    public boolean drawsHold(double sx, double sy) {
        Composed c = composed();
        double x = (sx - width / 2) * c.m, y = (height / 2 - sy) * c.m;
        double e = (x * c.cosR - y * c.sinR) / EarthProj.RADIUS_M, nn = (x * c.sinR + y * c.cosR) / EarthProj.RADIUS_M;
        double s2 = e * e + nn * nn;
        if (s2 > 1) return false;
        double a = Math.sqrt(1 - s2), up = c.ue * e + c.un * nn + c.ua * a;
        if (up < c.cosDraws) return false;
        if (!c.wholeSquare) return true;
        if (!(1 + up > 0)) return false;
        double k = EarthProj.RADIUS_M * Math.sqrt(2 / (1 + up)) / World.PLOT_M, half = World.SIDE / 2.0;
        double px = half + k * (c.ee * e + c.en * nn + c.ea * a), py = half - k * (c.ne * e + c.nn * nn + c.na * a);
        return px >= 0 && py >= 0 && px < World.SIDE && py < World.SIDE;
    }

    /** The cosine of the angle from the city of a screen point, by ground()'s matrix: NaN off the disc. */
    private double cityUp(double sx, double sy) {
        Composed c = composed();
        double x = (sx - width / 2) * c.m, y = (height / 2 - sy) * c.m;
        double e = (x * c.cosR - y * c.sinR) / EarthProj.RADIUS_M, nn = (x * c.sinR + y * c.cosR) / EarthProj.RADIUS_M;
        double s2 = e * e + nn * nn;
        return s2 > 1 ? Double.NaN : c.ue * e + c.un * nn + c.ua * Math.sqrt(1 - s2);
    }

    /** Whether a screen point is drawn from the view's chart (MS3): on the planet at the plane's scales, and not where drawsHold() has the plane draw it - past handoverKm() of the city, or off the world's square. Where ground() given charts draws the chart. */
    public boolean chartHolds(double sx, double sy) {
        return composed().wholeSquare && !Double.isNaN(cityUp(sx, sy)) && !drawsHold(sx, sy);
    }

    /** A screen point's place on the view's chart, into xy, in chart pixels at the view's plots (MapFrame.nodePlotsAt(planeScale())): the anchor's orthographic east over plots x PLOT_M and minus its north over it - what ground() reads the chart's node and pixel from, and the hover's. False off the disc. */
    public boolean chartAt(double sx, double sy, double[] xy) {
        Composed c = composed();
        double x = (sx - width / 2) * c.m, y = (height / 2 - sy) * c.m;
        double e = (x * c.cosR - y * c.sinR) / EarthProj.RADIUS_M, nn = (x * c.sinR + y * c.cosR) / EarthProj.RADIUS_M;
        double s2 = e * e + nn * nn;
        if (s2 > 1) return false;
        double a = Math.sqrt(1 - s2), per = EarthProj.RADIUS_M / (MapFrame.nodePlotsAt(planeScale()) * World.PLOT_M);
        xy[0] = (c.xe * e + c.xn * nn + c.xa * a) * per;
        xy[1] = -(c.ye * e + c.yn * nn + c.ya * a) * per;
        return true;
    }

    /**
     * The view's two projections composed: the screen's metres turned by the
     * roll into the camera's east and north (e, n) on the unit sphere, its
     * third coordinate a = sqrt(1 - e^2 - n^2); the point's unit vector in
     * the camera's frame (Ortho.inverse()'s) turned about the axis by the
     * longitudes' difference into the city's, then into its east, north and
     * up (EarthProj.forward()'s) - each a sum of e, n and a, the matrix here.
     */
    private static final class Composed {
        final double lat, lon, kmPx, roll, reach, draws;
        /** The plane's nodes are drawn wherever the world's square holds the pixel (planeDrawnPastReach()) within the hand-over, so cosDraws is handoverUp(): every pixel at least that near the city. */
        final boolean wholeSquare;
        final double m, cosR, sinR, cosL0, sinL0, cosReach, cosDraws;
        final double ee, en, ea, ne, nn, na, ue, un, ua;
        /** The view's chart's east and north rows (MS3): the same matrix as the city's, about the chart's anchor. */
        final double xe, xn, xa, ye, yn, ya;

        Composed(GlobeFrame f) {
            lat = f.lat;
            lon = f.lon;
            kmPx = f.kmPx;
            roll = f.roll;
            reach = f.reachKm;
            draws = f.planeDrawsKm();
            wholeSquare = f.planeDrawnPastReach();
            double r = StrictMath.toRadians(roll), l0 = StrictMath.toRadians(lat), lc = StrictMath.toRadians(f.cityLat);
            double d = StrictMath.toRadians(lon - f.cityLon);
            m = kmPx * 1000;
            cosR = StrictMath.cos(r);
            sinR = StrictMath.sin(r);
            cosL0 = StrictMath.cos(l0);
            sinL0 = StrictMath.sin(l0);
            double cosLc = StrictMath.cos(lc), sinLc = StrictMath.sin(lc), cosD = StrictMath.cos(d), sinD = StrictMath.sin(d);
            // The camera's frame (px = a cos l0 - n sin l0, py = e, pz = a sin l0 + n cos l0), turned by the longitudes' difference:
            // qx = cosD px - sinD py, qy = sinD px + cosD py, qz = pz; then east = qy, north = cos lc qz - sin lc qx, up = sin lc qz + cos lc qx.
            ee = cosD;
            en = -sinD * sinL0;
            ea = sinD * cosL0;
            ne = sinLc * sinD;
            nn = cosLc * cosL0 + sinLc * cosD * sinL0;
            na = cosLc * sinL0 - sinLc * cosD * cosL0;
            ue = -cosLc * sinD;
            un = sinLc * cosL0 - cosLc * cosD * sinL0;
            ua = sinLc * sinL0 + cosLc * cosD * cosL0;
            cosReach = StrictMath.cos(Math.min(Math.PI, reach * 1000 / EarthProj.RADIUS_M));
            cosDraws = wholeSquare ? handoverUp() : StrictMath.cos(Math.min(Math.PI, draws * 1000 / EarthProj.RADIUS_M));
            Chart chart = f.chart();
            double lcc = StrictMath.toRadians(chart.lat()), dd = StrictMath.toRadians(lon - chart.lon());
            double cosLcc = StrictMath.cos(lcc), sinLcc = StrictMath.sin(lcc), cosDd = StrictMath.cos(dd), sinDd = StrictMath.sin(dd);
            xe = cosDd;
            xn = -sinDd * sinL0;
            xa = sinDd * cosL0;
            ye = sinLcc * sinDd;
            yn = cosLcc * cosL0 + sinLcc * cosDd * sinL0;
            ya = cosLcc * sinL0 - sinLcc * cosDd * cosL0;
        }

        boolean of(GlobeFrame f) {
            return lat == f.lat && lon == f.lon && kmPx == f.kmPx && roll == f.roll && reach == f.reachKm && draws == f.planeDrawsKm() && wholeSquare == f.planeDrawnPastReach();
        }
    }

    /** The matrix for the view as it is, made when it moved; its fields final, so the bands' threads read it whole. */
    private Composed composed;

    private Composed composed() {
        Composed c = composed;
        if (c != null && c.of(this)) return c;
        c = new Composed(this);
        composed = c;
        return c;
    }

    /**
     * Where the points of this view's pixels lie on an older view's screen
     * (★E9): the GROUND tier is drawn again in pieces as a view comes to
     * rest, and until a piece is in, its pixels are the last whole picture's
     * at their own points - the founding globe's "last picture slid", pixel
     * by pixel. Takes Beyond's unit vector, so no latitude or longitude is
     * worked out: the camera's frame turned into the older camera's, its
     * east, north and up there, its roll and scale.
     */
    public static final class Carry {
        private final double ax, ay, az, bx, by, bz, cx, cy, cz, w2, h2;
        private final int w, h;

        private Carry(GlobeFrame now, GlobeFrame old) {
            double d = StrictMath.toRadians(now.lon - old.lon), l = StrictMath.toRadians(old.lat), r = StrictMath.toRadians(old.roll);
            double cosD = StrictMath.cos(d), sinD = StrictMath.sin(d), cosL = StrictMath.cos(l), sinL = StrictMath.sin(l);
            double cR = StrictMath.cos(r), sR = StrictMath.sin(r), k = EarthProj.RADIUS_M / (old.kmPx * 1000);
            // east = sinD vx + cosD vy; north = -sinL cosD vx + sinL sinD vy + cosL vz; up = cosL cosD vx - cosL sinD vy + sinL vz.
            double ex = sinD, ey = cosD, ez = 0, nx = -sinL * cosD, ny = sinL * sinD, nz = cosL;
            ax = k * (ex * cR + nx * sR);
            ay = k * (ey * cR + ny * sR);
            az = k * (ez * cR + nz * sR);
            bx = -k * (nx * cR - ex * sR);
            by = -k * (ny * cR - ey * sR);
            bz = -k * (nz * cR - ez * sR);
            cx = cosL * cosD;
            cy = -cosL * sinD;
            cz = sinL;
            w2 = old.width / 2;
            h2 = old.height / 2;
            w = (int) old.width;
            h = (int) old.height;
        }

        /** The older view's screen point of Beyond's unit vector, into out: false on its far side. */
        public boolean screen(double vx, double vy, double vz, double[] out) {
            if (cx * vx + cy * vy + cz * vz < 0) return false;
            out[0] = w2 + ax * vx + ay * vy + az * vz;
            out[1] = h2 + bx * vx + by * vy + bz * vz;
            return true;
        }

        /** ...as the older view's pixel, its row times its width and its column: -1 on its far side or off its screen. */
        public int pixel(double vx, double vy, double vz) {
            if (cx * vx + cy * vy + cz * vz < 0) return -1;
            double x = w2 + ax * vx + ay * vy + az * vz, y = h2 + bx * vx + by * vy + bz * vz;
            if (!(x >= 0 && y >= 0 && x < w && y < h)) return -1;
            return (int) y * w + (int) x;
        }
    }

    /** ...this view's points on `old`'s screen. */
    public Carry carryFrom(GlobeFrame old) { return new Carry(this, old); }

    /** The scale bar on the globe (spec-globe 3.7): MapFrame.scaleBar()'s rule at the view's centre, where alone it is true - {metres, pixels}. */
    public double[] scaleBar(double mostPx) {
        double perPx = kmPx * 1000, most = mostPx * perPx, metres = 1;
        while (metres * 10 <= most) metres *= 10;
        if (metres * 5 <= most) metres *= 5;
        else if (metres * 2 <= most) metres *= 2;
        return new double[] { metres, metres / perPx };
    }

    /** ...its words: the plane's, true at the centre only and saying so - "500 km at the centre". */
    public static String scaleWords(double metres) {
        return MapFrame.scaleWords(metres) + AT_THE_CENTRE;
    }

    /** What the globe's scale bar adds to its length (spec-globe 3.7: "true at the view's centre only, and says so"). */
    public static final String AT_THE_CENTRE = " at the centre";
}
