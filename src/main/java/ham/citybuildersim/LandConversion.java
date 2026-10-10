package ham.citybuildersim;

/**
 * A saved city's land put on the block grid, and a city's land drawn again to hold a new figure: a format-31 save's lanes snapped to whole blocks, an older save's one figure drawn as a centre of blocks to the plot round the site found for it, its iron as its save had it, and twenty-four offers listed round it.
 *
 * WHY THIS EXISTS (0.7.57, batch J1b; the project's spec-land.md 2.9). A save
 * of format 30 or older carries the land as one figure, the square feet owned,
 * a pool of iron sites and tonnes, and nine parcels with no place. Loaded as
 * it was, its pooled ore and its listing would be read wrongly by a build
 * whose ground is real (hence SAVE_FORMAT 31), so the load converts it, once.
 *
 * ON THE BLOCK GRID SINCE 0.7.67 (batch M3, SAVE_FORMAT 32; the project's
 * spec-grid.md 2.6). The land is whole blocks now (CityLand, LandGrid), and
 * every save written before is put on them once, at load, by GridConversion:
 *
 *   format 31   (0.7.57 to 0.7.66: a centre and forty lanes; convertLanes())
 *               its ground read as it was (LegacyLand), snapped one level
 *               finer than its offers with its fields deciding, so no field
 *               changes hands and a field it held only part of keeps exactly
 *               its sites; one converted holding, its books the plots drawn,
 *               its sites and amounts the save's to the bit, what it took out
 *               (E) as saved; its purchase records kept as history, no money
 *               moving (spec-grid stars 11 and 12);
 *   format 30   (convert()) J1b's site search, a coastal cell at a time,
 *   and older   with a fifth test: the L-infinity square holding the city's
 *               dry ground is at least CENTRE_DRY_MIN dry, with the sea within
 *               its half-side and CENTRE_SEA_KM (if none of the nearest
 *               SITE_CELLS passes, the driest found); round it, rings of
 *               blocks of the city's level, the last split down to the plot,
 *               holding the save's dry ground to within a plot; its iron the
 *               save's sites, raised to cover the mines standing and on
 *               order, and its tonnes, never more (spec-land star 9), with a
 *               legacy field when the world laid no iron on the ground drawn;
 *               nothing taken out yet.
 *
 * Either way the city's square feet become the converted ground's dry plots
 * (the books follow the map), and twenty-four offers are listed afresh round
 * it, once - the one exception to "never rerolled" - after the load has put
 * back the prices, the buildings and the allocation (Game): at the prices the
 * save last struck, as a load strikes none; the next month strikes them on
 * the converted ground.
 *
 * Restating (restate()) draws a city's land again round the same site, the
 * centre holding the whole of a new figure as an older save's is drawn: what
 * LandManager.setOwnedSqFt() does - a harness's ground by fiat, and the load
 * of a save whose square feet disagree with its ground by more than a plot.
 * The city's iron and what it has taken out are kept as they were; the other
 * resources are the world's fields centred on the ground drawn; the figure is
 * the one asked for, the ground holding it to within a plot.
 */
public final class LandConversion {

    private LandConversion() { }

    /** The least share of a converted city's centre that is dry ground: 80% (spec-land 2.9), so a played city is not put on a peninsula two-thirds sea. */
    public static final double CENTRE_DRY_MIN = 0.8;

    /** ...with the sea within its half-side and this many kilometres: 2, a city on a coast. */
    public static final double CENTRE_SEA_KM = 2;

    /** The last save format whose land is one figure, converted from it: 30, the format before the land was on the world. */
    public static final int LAST_FORMAT_BEFORE = 30;

    /** The last save format whose land is lanes, snapped to blocks at load: 31 (0.7.57 to 0.7.66; GameVersion.SAVE_FORMAT 32 holds blocks). */
    public static final int LAST_LANES_FORMAT = 31;

    /** How near a save's square feet must be to its land's dry ground to be the same ground: within one plot of it (0.7.67; a part in a billion before, when a centre held its figure exactly) - a figure set by hand is held by whole plots, to within one. */
    public static final double SAME_GROUND_PLOTS = 1;

    /** How many coastal cells' sites are tried, nearest the world's middle first: 64. */
    public static final int SITE_CELLS = 64;

    /** A legacy iron field stands at least this far from the site: 1 km (spec-land 2.4). */
    public static final double LEGACY_FIELD_KM = 1;

    /** Draws for the legacy field's dry plot: 64. */
    static final int LEGACY_DRAWS = 64;

    /** The stream the legacy field's plot is drawn from. */
    private static final long LEGACY_STREAM = 0x1E6AC7L;

    /** Whether a save's figure of dry ground, in square feet, is its land's: within SAME_GROUND_PLOTS plots of it. */
    public static boolean sameGround(double ownedSqFt, double landDrySqFt) {
        return Math.abs(ownedSqFt - landDrySqFt) <= SAME_GROUND_PLOTS * LandManager.sqFt(World.KM2_PER_PLOT) * (1 + 1e-12);
    }

    /**
     * Where an older city's centre goes: the first site of the founding
     * search, a coastal cell at a time, whose square holding dryKm2 passes the
     * fifth test; or, when none of the nearest SITE_CELLS does, the one whose
     * square out to the test's reach is driest. {x, y} as plots. J1b's search,
     * on J1b's profile of the ground (LegacyLand.within(), sizeCentre()).
     */
    public static long[] site(World world, double dryKm2) {
        double reach = LegacyLand.idealHalf(dryKm2) / Math.sqrt(CENTRE_DRY_MIN);
        double sea = CENTRE_SEA_KM * 1000 / World.PLOT_M;
        long[][] driest = { null };
        double[] driestShare = { -1 };
        long[] found = world.searchSites(SITE_CELLS, s -> {
            double[] within = LegacyLand.within(world, s[0], s[1], dryKm2, reach);
            double share = within[CityLand.TOTAL] > 0 ? within[CityLand.DRY] / within[CityLand.TOTAL] : 0;
            if (share > driestShare[0]) {
                driestShare[0] = share;
                driest[0] = s;
            }
            if (within[CityLand.DRY] < dryKm2) return false;
            double half = LegacyLand.sizeCentre(world, s[0], s[1], dryKm2).half();
            return LegacyLand.within(world, s[0], s[1], dryKm2, half + sea)[CityLand.SEA] > 0;
        });
        if (found != null) return found;
        if (driest[0] != null) return driest[0];
        return new long[] { world.foundingX(), world.foundingY() };
    }

    /**
     * Converts an older city's land (format 30 and before; spec-grid 2.6): on
     * the world its seed makes, at the site site() finds, a centre of blocks
     * holding drySqFt of dry ground to within a plot (GridConversion.
     * fromFigure()), its iron the save's - at least the mines standing and on
     * order - and its tonnes; nothing taken out; the city's figure its dry
     * plots. The offers are the load's to list (Game), at the prices it puts
     * back.
     */
    public static void convert(LandManager lm, long seed, double drySqFt, int ironSites, double ironTonnes,
                               int minesCommitted) {
        World world = World.of(seed);
        double dryKm2 = LandManager.km2(drySqFt);
        long[] at = site(world, dryKm2);
        GridConversion.Result r = GridConversion.fromFigure(world, at[0], at[1], dryKm2, ironSites, ironTonnes, minesCommitted);
        CityLand land = CityLand.converted(seed, r, new double[0][]);
        lm.install(land, new double[CityLand.KINDS], world.totals(), world.seaTheta());
        lm.restoreOwnedSqFt(lm.getLandDrySqFt());
    }

    /**
     * Converts a format-31 save's land (spec-grid 2.6, star 11): its centre,
     * lanes and purchases as saved, snapped (GridConversion.fromLanes()) by
     * the build that wrote it (gameVersion: whether it held fields site by
     * site), with what it took out (depletion) as saved and the world's totals
     * and sea level it stored; its purchase records kept as history; the
     * city's figure its dry plots. False, and nothing installed, when the
     * centre's record is missing or the wrong width. The offers are the
     * load's to list (Game).
     */
    public static boolean convertLanes(LandManager lm, long seed, double[] centre, double[] lanes, double[][] purchases,
                                       String gameVersion, double[] depletion, double[] worldTotals, double seaTheta) {
        LegacyLand old = LegacyLand.restore(seed, centre, lanes, purchases);
        if (old == null) return false;
        GridConversion.Result r = GridConversion.fromLanes(old, gameVersion, depletion);
        double[][] history = new double[old.purchases().size()][];
        int at = 0;
        if (purchases != null) for (double[] row : purchases) if (LegacyLand.read(row) != null) history[at++] = row.clone();
        CityLand land = CityLand.converted(seed, r, history);
        double[] e = new double[CityLand.KINDS];
        for (Resource res : Resource.values()) e[res.ordinal()] = r.extracted(res);
        lm.install(land, e, worldTotals, seaTheta);
        lm.restoreOwnedSqFt(lm.getLandDrySqFt());
        return true;
    }

    /**
     * The legacy field's plot round the site (sx, sy) of ground reaching
     * `reach` plots from it (L-infinity): the first of LEGACY_DRAWS draws from
     * the seed within the larger of `reach` and LEGACY_FIELD_KM each way, at
     * least LEGACY_FIELD_KM from the site, on dry ground that `onGround`
     * accepts; the plot LEGACY_FIELD_KM east of the site when none is. J1b's
     * centre accepted every draw; the block grid's accepts its own plots
     * (GridConversion.fromFigure(), 0.7.66).
     */
    static long[] legacyPlot(World world, long seed, long sx, long sy, double reach,
                             java.util.function.BiPredicate<Long, Long> onGround) {
        double near = LEGACY_FIELD_KM * 1000 / World.PLOT_M;
        double half = Math.max(reach, near);
        long h = World.mix(seed ^ LEGACY_STREAM);
        long fx = sx + Math.round(near), fy = sy;
        for (int k = 0; k < LEGACY_DRAWS; k++) {
            h = World.mix(h);
            double dx = (2 * World.unit(h) - 1) * half;
            h = World.mix(h);
            double dy = (2 * World.unit(h) - 1) * half;
            if (dx * dx + dy * dy < near * near) continue;
            long x = sx + Math.round(dx), y = sy + Math.round(dy);
            byte c = world.terrainAt(x, y);
            if (c == World.SALT || c == World.FRESH) continue;
            if (!onGround.test(x, y)) continue;
            fx = x;
            fy = y;
            break;
        }
        return new long[] { fx, fy };
    }

    /**
     * Draws a city's land again to hold drySqFt of dry ground (spec-grid 2.6:
     * restate() draws as a format-30 save is drawn): the centre round the
     * same site, rings of blocks of the level the figure makes, the last split
     * down to the plot, holding the figure to within a plot; the purchases
     * folded in; the city's iron sites and tonnes and what it has taken out of
     * every resource kept as they were, a legacy field standing for its iron
     * when the world laid none on the ground drawn; the other resources what
     * the world holds on the new ground; the figure the one asked for; and
     * twenty-four offers listed round it at the prices the office last struck.
     */
    public static void restate(LandManager lm, double drySqFt) {
        CityLand land = lm.land();
        World world = World.of(land.seed());
        int ironSites = (int) Math.min(Integer.MAX_VALUE, land.totalSites(Resource.IRON));
        double ironTonnes = land.totalAmount(Resource.IRON);
        // On the old world's fields when the city's centre holds them (0.7.99, CityLand.oldWorldHoldings()), else the world's.
        GridConversion.Result r = GridConversion.fromFigure(world, land.siteX(), land.siteY(),
                Math.max(0, LandManager.km2(drySqFt)), ironSites, ironTonnes, 0, land.oldWorldHoldings() > 0);
        land.redraw(r);
        lm.restoreOwnedSqFt(drySqFt);
        lm.getMarket().attach(land);
        lm.getMarket().listMissing();
    }
}
