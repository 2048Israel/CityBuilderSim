package ham.citybuildersim;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A city's gateway is the way the world is.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. A STRIP OF LAND RUNNING ONE KNOWN WAY gives the gateway on that
 *      bearing, at the city's radius, with every returned bearing a multiple
 *      of the dial's step.
 *
 *   2. AN ISLAND FOUNDING has no gateway: land only within a small disc round
 *      the site is short of the reach.
 *
 *   3. TWO EQUAL ARMS, east and south, tie; the gateway is one of the two,
 *      chosen by the founding site's hash (two sites in the same cell that pick
 *      differently are shown when the offsets find them, else the site's own
 *      pick is asserted one of the two).
 *
 *   4. A HALF-PLANE OF LAND WEST ties every westward bearing, and the gateway
 *      is the middle of that wide arc (180), not the first westward bearing.
 *
 *   5. THE THRESHOLD is exact: a strip one cell short of the reach has no
 *      gateway, and a strip that just reaches has one.
 *
 *   6. THE DEFAULT WORLD is deterministic. If a gateway is found its point
 *      lies on the edge and on land; if none is found, the best land run is
 *      really short of the reach.
 *
 *   7. TWELVE REAL GENERATED WORLDS fire the mechanic at least once, so the
 *      rule is not only a fixture's.
 *
 *   8. A FULL TIE IS NOT DUE EAST FOR EVERYONE: an all-land ground, 24
 *      founding sites, gives every bearing a multiple of the step and at
 *      least 6 distinct bearings among them.
 *
 *   9. THE EARTH'S RULE: one larger place due north wins the north bearing;
 *      a nearer place whose way is sea is passed over for a farther clear
 *      one; an empty or null list falls back to the land rule.
 *
 *  10. THE SPINE IS LAID TOWARD THE GATEWAY: with gateway 90 (south) the
 *      first plots run down the hub's column, then the opposite arm; trimming
 *      takes the opposite arm first. With gateway 0 (east) the same on the
 *      row.
 *
 *  11. NO GATEWAY LAYS AS BEFORE: the row's two arms alternate first.
 *
 *  12. THE SIDECAR KEEPS IT: FORMAT 8 writes and reads the gateway back; a
 *      FORMAT 7 sidecar reads it unknown, and the next layRuns finds it
 *      again.
 *
 *  13. WHAT FINDING ONE COSTS: one real generated world, timed and printed.
 *
 *  14. THE WORLD LINE: a square city with a gateway and both nets laid has a
 *      world line for each net whose gate arm is laid; each line's point is
 *      the first plot the city does not own along the gate arm's heading, and
 *      its bearing is the gateway. With NO_GATEWAY there are none.
 *
 *  15. THE CACHE: two calls with nothing changed return equal lists; after a
 *      month's runs move changes(), the lines are recomputed equal to a fresh
 *      map's, not the same list.
 *
 *  16. THE LINE TO THE WORLD LEAVES ON LAND, ON A GENERATED COAST (FX2): a
 *      railway whose gate arm waits at the sea, with the city's own sea
 *      beyond it (FR0's walk crosses it), has its line start where an arm's
 *      track ends at the city's edge on dry ground and run out over dry
 *      ground, and every train's route lies on dry ground or on track. And
 *      over its months: the same city laid month by month from nothing to
 *      those counts over MapCheck.MONTHS months, each month's map holds no
 *      line's point and no train position on water (MapCheck.worldWaysOk()),
 *      and some months have a line and trains, so the run exercised it.
 *
 *  17. HALIFAX ON THE EARTH, when earth-2.bin is named: the same, on
 *      Jerus's founding site, where FR0's line lay across the harbour.
 *      Since ET6 the railway bridges the harbour's shallow water itself (a
 *      short crossing, TilePainter.RAIL_BRIDGE), so here its gate arm no
 *      longer waits at the sea but runs on to the city's edge: the fixture
 *      holds that, and section 16's generated coast keeps the arm that waits.
 *
 *  18. THE LABEL STAYS IN VIEW (LandMap.worldLabelAt): a line leaving
 *      through the right, top, bottom or top right corner of the view has its
 *      label whole inside the view, on the line; a part too short to hold it
 *      anywhere still has it in view; a part of no length has none.
 *
 *  19. A HIGHWAY THAT REACHES NO STREET IS NOT DRAWN (FX2): one Elevated
 *      Highway is laid and not drawn, the least count that reaches an
 *      arterial's ramp is found from both sides, and from there every
 *      highway plot the map draws lies in a piece with a ramp on a cell line.
 *
 *  20. TWO LINES LEAVING TOGETHER GET LABELS APART, OR ONE (FX5, Jerus
 *      2026-10-20: the highway's and the railway's "to the world" read
 *      "tetohe wodld"): two parts three pixels apart on one bearing are drawn
 *      by worldLabelAt() alone with their boxes overlapping (the fixture
 *      causes it); LandMap.worldLabels() keeps the first's place exactly and
 *      slides the second back along its own part until the boxes are clear
 *      by WORLD_LABEL_PAD; where the parts are too short for that, the second
 *      has none and the first names both (WORLD_BOTH_WORDS), inside the view;
 *      two lines on opposite sides of the view keep worldLabelAt()'s places
 *      to the pixel, none shared; a part of no length, or none at all, has no
 *      label and does not disturb the others.
 *
 * WHY THIS EXISTS: batch FR0 finds the land's mirror of SeaRoutes' offing,
 * so the highway and railway know which way the world is. A fixture CAUSES
 * each condition rather than waiting for a map to. Batch FX2 sections 16 to
 * 19: Jerus's Halifax had the line to the world start on the harbour's open
 * water and every train run out over it, its label ran off the view, and the
 * first Elevated Highway stood alone in a block.
 */
public class GatewayCheck {

    static int fails = 0;

    /** The coast fixture's railway: each track type's count times this, 10 - enough track that the railway's gate arm reaches the coast and waits at the sea (section 16). */
    static final int COAST_TRACK_TIMES = 10;

    /** Jerus's founding site, Halifax (section 17), in degrees. */
    static final double HALIFAX_LAT = 44.656780322078724, HALIFAX_LON = -63.613890005401984;

    /** The most Elevated Highways section 19 tries when it looks for the count that reaches a street. */
    static final int MOST_HIGHWAYS = 20;

    /** The tolerance of a position in plots or pixels, well below anything either measures. */
    static final double EPS = 1e-9;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-78s %s%n", label, ok ? "ok" : "FAIL");
    }

    /** A square of owned dry ground the runs may lay on, -1 outside: a harness fixture for the spine. */
    static class BoxGround implements CityRuns.Ground {
        final long x0, y0, x1, y1;

        BoxGround(long x0, long y0, long x1, long y1) {
            this.x0 = x0; this.y0 = y0; this.x1 = x1; this.y1 = y1;
        }

        @Override public int at(long x, long y, int dir) {
            return x >= x0 && x <= x1 && y >= y0 && y <= y1 ? World.GRASS : -1;
        }

        @Override public long version() {
            return 1;
        }

        @Override public long[] box() {
            return new long[] { x0, y0, x1, y1 };
        }
    }

    /** The game's building templates, as the map draws them. */
    static BuildingVisual.Type[] gatewayTypes() {
        return BuildingVisual.table(new BuildingCatalog().load());
    }

    /** The highway type's id. */
    static int highwayType(BuildingVisual.Type[] types) {
        for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].road() == BuildingVisual.HIGHWAY) return t;
        return -1;
    }

    /** The first railway line type's id. */
    static int trackType(BuildingVisual.Type[] types) {
        for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].track()) return t;
        return -1;
    }

    /** A square city on the default world's dry place with `highway` highways and `track` railway lines, its gateway found. */
    static CityMap gatewayCity(BuildingVisual.Type[] types, long highway, long track) {
        World world = World.of(Founding.DEFAULT_WORLD_SEED);
        long[] place = MapCheck.dryPlace(world);
        long[] counts = new long[types.length];
        int h = highwayType(types), t = trackType(types);
        if (h >= 0) counts[h] = highway;
        if (t >= 0) counts[t] = track;
        return CityMap.square(Founding.DEFAULT_WORLD_SEED, place[0], place[1], types, 2, CityMap.DISTRICT * CityMap.DISTRICT,
                counts);
    }

    /** The longest run of land cells out from a cell, over every bearing - the harness's own look at the same ground. */
    static int bestRun(Gateways.Ground ground, long ci, long cj) {
        int best = 0;
        for (int deg = 0; deg < 360; deg += Gateways.BEARING_STEP) {
            double r = Math.toRadians(deg), c = Math.cos(r), s = Math.sin(r);
            int run = 0;
            for (int k = 1; k <= Gateways.RAY_CELLS; k++) {
                long i = Math.round(ci + c * k), j = Math.round(cj + s * k);
                if (!ground.land(i, j)) break;
                run = k;
            }
            if (run > best) best = run;
        }
        return best;
    }

    /** Lays a fresh spine toward `gateway` on a square box and proves the arm order: the toward arm first, the opposite arm only when it waits, and the opposite arm taken back first. */
    static void spineCase(int gateway, String toward, String opposite, boolean vertical, long siteX, long siteY, long cell) {
        long boxX0 = siteX - 20 * cell, boxX1 = siteX + 20 * cell;
        long boxY0 = siteY - 20 * cell, boxY1 = siteY + 20 * cell;
        CityRuns runs = new CityRuns();
        runs.setGateway(gateway);
        BoxGround ground = new BoxGround(boxX0, boxY0, boxX1, boxY1);
        List<long[]> noMines = new ArrayList<>();
        runs.layTo(1, 0, 0, 0, 0, ground, siteX, siteY, siteX, siteY, noMines);
        CityRuns.Net hw = runs.highways;
        long[] hub = hw.hub();
        long edge = vertical ? boxY1 - hub[1] : boxX1 - hub[0];
        runs.layTo(8, 0, 0, 0, 0, ground, siteX, siteY, siteX, siteY, noMines);
        boolean firstOk = true;
        int covered = 0;
        for (long y = boxY0; y <= boxY1; y++) {
            for (long x = boxX0; x <= boxX1; x++) {
                if (!CityRuns.covered(hw, x, y)) continue;
                covered++;
                if (vertical ? (x != hub[0] || y < hub[1]) : (y != hub[1] || x < hub[0])) firstOk = false;
            }
        }
        check("with gateway " + gateway + " the first plots all lie on the hub's " + (vertical ? "column" : "row") + " " + toward + " of it", firstOk && covered > 0);
        check("...and only the " + toward + " arm has grown",
                hw.arms.get(0).steps > 0 && hw.arms.get(1).steps == 0 && hw.arms.get(2).steps == 0 && hw.arms.get(3).steps == 0);
        runs.layTo(edge + 20, 0, 0, 0, 0, ground, siteX, siteY, siteX, siteY, noMines);
        int towardArm = hw.arms.get(0).steps;
        int oppositeArm = hw.arms.get(1).steps;
        boolean colOk = true;
        int towardSteps = 0, oppositeSteps = 0;
        for (long y = boxY0; y <= boxY1; y++) {
            for (long x = boxX0; x <= boxX1; x++) {
                if (!CityRuns.covered(hw, x, y)) continue;
                if (vertical ? x != hub[0] : y != hub[1]) colOk = false;
                else if (vertical ? y > hub[1] : x > hub[0]) towardSteps++;
                else if (vertical ? y < hub[1] : x < hub[0]) oppositeSteps++;
            }
        }
        check("...laid past the edge, the " + toward + " arm stops at the edge and the rest go to the opposite arm", colOk && towardSteps == towardArm && oppositeSteps == oppositeArm && towardArm > 0 && oppositeArm > 0);
        boolean outside = CityRuns.covered(hw, vertical ? hub[0] : boxX1 + 1, vertical ? boxY1 + 1 : hub[1])
                || CityRuns.covered(hw, vertical ? hub[0] : boxX0 - 1, vertical ? boxY0 - 1 : hub[1]);
        check("...and no highway plot lies outside the box", !outside);
        runs.layTo(1 + towardArm, 0, 0, 0, 0, ground, siteX, siteY, siteX, siteY, noMines);
        boolean trimOk = true;
        int keptToward = 0;
        for (long y = boxY0; y <= boxY1; y++) {
            for (long x = boxX0; x <= boxX1; x++) {
                if (!CityRuns.covered(hw, x, y)) continue;
                if (vertical ? x != hub[0] : y != hub[1]) trimOk = false;
                else if (vertical ? y < hub[1] : x < hub[0]) trimOk = false;
                else if (vertical ? y > hub[1] : x > hub[0]) keptToward++;
            }
        }
        check("...and taking back fewer plots takes from the " + opposite + " arm first, leaving the " + toward + " arm untouched", trimOk && keptToward == towardArm && hw.arms.get(1).steps == 0);
    }

    public static void main(String[] args) {
        int cell = SeaRoutes.CELL;
        long sc = 100;                                  // the site's cell, a synthetic world's middle
        long siteX = sc * cell + cell / 2, siteY = sc * cell + cell / 2;
        double radius = 2 * cell;                       // the city's edge, two cells out
        int reach = Gateways.reachCells(radius);

        /* ============ 1. a strip of land east ============ */
        System.out.println("--- a strip of land east ---");
        long strip = reach + 3L;
        Gateways.Ground east = (ci, cj) -> cj == sc && ci >= sc && ci <= sc + strip;
        Gateways.Gateway g = Gateways.of(east, siteX, siteY, radius);
        check("a strip east gives a gateway", g != null);
        check("...whose bearing is east (0)", g != null && g.bearing() == 0);
        check("...and whose bearing is a multiple of the step",
                g != null && g.bearing() % Gateways.BEARING_STEP == 0);
        double r = Math.toRadians(g == null ? 0 : g.bearing());
        check("...and whose point lies on the city's edge on that bearing",
                g != null && g.x() == Math.round(siteX + Math.cos(r) * radius)
                        && g.y() == Math.round(siteY + Math.sin(r) * radius));
        check("...and is the city's radius out",
                g != null && Math.abs(Math.hypot(g.x() - siteX, g.y() - siteY) - radius) <= 1.0);

        /* ============ 2. an island founding ============ */
        System.out.println("\n--- an island founding ---");
        long islandCells = Math.max(1, reach / 4);
        Gateways.Ground island = (ci, cj) -> {
            long di = ci - sc, dj = cj - sc;
            return di * di + dj * dj <= islandCells * islandCells;
        };
        check("land only within a small disc round the site has no gateway",
                Gateways.of(island, siteX, siteY, radius) == null);

        /* ============ 3. a tie ============ */
        System.out.println("\n--- a tie ---");
        long arm = reach + 2;
        Gateways.Ground tie = (ci, cj) ->
                (cj == sc && ci >= sc && ci <= sc + arm)        // east
                || (ci == sc && cj >= sc && cj <= sc + arm);    // south
        Gateways.Gateway tied = Gateways.of(tie, siteX, siteY, radius);
        check("equal arms east and south tie, and the gateway is one of the two lone tied bearings",
                tied != null && (tied.bearing() == 0 || tied.bearing() == 90));
        check("...and the tied bearing is a multiple of the step",
                tied != null && tied.bearing() % Gateways.BEARING_STEP == 0);
        double tr = Math.toRadians(tied == null ? 0 : tied.bearing());
        check("...and its point lies on the city's edge on that bearing",
                tied != null && tied.x() == Math.round(siteX + Math.cos(tr) * radius)
                        && tied.y() == Math.round(siteY + Math.sin(tr) * radius));
        int eastSite = -1, southSite = -1;
        for (int off = 0; off < cell; off++) {
            Gateways.Gateway pick = Gateways.of(tie, sc * cell + off, siteY, radius);
            if (pick == null) continue;
            if (pick.bearing() == 0 && eastSite < 0) eastSite = off;
            if (pick.bearing() == 90 && southSite < 0) southSite = off;
        }
        if (eastSite >= 0 && southSite >= 0) {
            check("two founding sites in the same cell pick different tied bearings (the hash, not the smaller bearing)", true);
            check("...one picks east", Gateways.of(tie, sc * cell + eastSite, siteY, radius).bearing() == 0);
            check("...and the other picks south", Gateways.of(tie, sc * cell + southSite, siteY, radius).bearing() == 90);
        } else {
            check("no two offsets in the cell pick differently; the site's own pick is one of the two", tied != null && (tied.bearing() == 0 || tied.bearing() == 90));
        }

        /* ============ 4. a half-plane of land west ============ */
        System.out.println("\n--- a half-plane of land west ---");
        Gateways.Ground west = (ci, cj) -> ci <= sc;
        Gateways.Gateway w = Gateways.of(west, siteX, siteY, radius);
        check("every westward bearing runs to the cap, and the gateway is the middle of that arc (180)",
                w != null && w.bearing() == 180);

        /* ============ 5. the threshold ============ */
        System.out.println("\n--- the threshold ---");
        long shortStrip = Math.max(0, reach - 1);
        Gateways.Ground shortOf = (ci, cj) -> cj == sc && ci >= sc && ci <= sc + shortStrip;
        check("a strip one cell short of the reach has no gateway",
                Gateways.of(shortOf, siteX, siteY, radius) == null);
        Gateways.Ground justReaches = (ci, cj) -> cj == sc && ci >= sc && ci <= sc + reach;
        Gateways.Gateway exact = Gateways.of(justReaches, siteX, siteY, radius);
        check("a strip that just reaches has one", exact != null);
        check("...on the east bearing", exact != null && exact.bearing() == 0);

        /* ============ 6. the default world ============ */
        System.out.println("\n--- the default world ---");
        World def = World.of(Founding.DEFAULT_WORLD_SEED);
        long fx = def.foundingX(), fy = def.foundingY();
        Gateways.Gateway da = Gateways.of(def, fx, fy, radius);
        Gateways.Gateway db = Gateways.of(def, fx, fy, radius);
        check("the default world's gateway is deterministic", java.util.Objects.equals(da, db));
        if (da != null) {
            double dr = Math.toRadians(da.bearing());
            check("...its point lies on the city's edge on that bearing",
                    da.x() == Math.round(fx + Math.cos(dr) * radius)
                            && da.y() == Math.round(fy + Math.sin(dr) * radius));
            Gateways.Ground ground = Gateways.ground(def);
            check("...and the cell at its point is land",
                    ground.land(Math.floorDiv(da.x(), cell), Math.floorDiv(da.y(), cell)));
            System.out.printf("  the default world has a gateway: bearing %d, run %d%n",
                    da.bearing(), da.run());
        } else {
            long sci = Math.floorDiv(fx, cell), scj = Math.floorDiv(fy, cell);
            Gateways.Ground ground = Gateways.ground(def);
            int bestDefault = bestRun(ground, sci, scj);
            check("...with none, its best land run is short of the reach", bestDefault < reach);
            System.out.printf("  the default world has no gateway: best land run %d, reach %d%n",
                    bestDefault, reach);
        }

        /* ============ 7. twelve real generated worlds ============ */
        System.out.println("\n--- twelve real generated worlds ---");
        long[] seeds = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 77 };
        int found = 0;
        for (long seed : seeds) {
            World world = World.of(seed);
            if (Gateways.of(world, world.foundingX(), world.foundingY(), radius) != null) found++;
        }
        System.out.printf("  gateway found on %d of %d worlds%n", found, seeds.length);
        check("at least one of the twelve worlds has a gateway", found > 0);

        /* ============ 8. a full tie is not due east for everyone ============ */
        System.out.println("\n--- a full tie is not due east for everyone ---");
        Gateways.Ground allLand = (ci, cj) -> true;
        Set<Integer> bearings = new TreeSet<>();
        int fullFound = 0;
        for (int k = 0; k < 24; k++) {
            long x = (sc + k) * cell + cell / 2, y = (sc - k) * cell + cell / 2;
            Gateways.Gateway full = Gateways.of(allLand, x, y, radius);
            if (full == null) continue;
            fullFound++;
            bearings.add(full.bearing());
        }
        check("an all-land ground finds a gateway at each of 24 founding sites", fullFound == 24);
        check("...and every full tie's bearing is a multiple of the step", bearings.stream().allMatch(b -> b % Gateways.BEARING_STEP == 0));
        check("...with at least 6 distinct bearings among them", bearings.size() >= 6);
        System.out.printf("  %d distinct bearings over 24 all-land sites%n", bearings.size());

        /* ============ 9. the Earth's rule ============ */
        System.out.println("\n--- the Earth's rule ---");
        List<long[]> oneNorth = List.of(new long[] { siteX, siteY - 5L * cell });
        Gateways.Gateway north = Gateways.of(allLand, siteX, siteY, radius, oneNorth);
        check("one larger place due north wins the north bearing (270)", north != null && north.bearing() == 270);
        Gateways.Ground seaNorth = (ci, cj) -> !(cj == sc - 2 && ci == sc);
        List<long[]> nearNorthThenEast = List.of(
                new long[] { siteX, siteY - 3L * cell },
                new long[] { siteX + 6L * cell, siteY });
        Gateways.Gateway passed = Gateways.of(seaNorth, siteX, siteY, radius, nearNorthThenEast);
        check("a nearer place whose way is sea is passed over for the farther clear one (east)",
                passed != null && passed.bearing() == 0);
        Gateways.Gateway landRule = Gateways.of(allLand, siteX, siteY, radius);
        Gateways.Gateway empty = Gateways.of(allLand, siteX, siteY, radius, List.of());
        check("an empty Earth list falls back to the land rule's own answer", empty != null && empty.equals(landRule));
        Gateways.Gateway nullList = Gateways.of(allLand, siteX, siteY, radius, null);
        check("...and a null list does too", nullList != null && nullList.equals(landRule));

        /* ============ 10. the spine is laid toward the gateway ============ */
        System.out.println("\n--- the spine is laid toward the gateway ---");
        spineCase(90, "south", "north", true, siteX, siteY, cell);
        spineCase(0, "east", "west", false, siteX, siteY, cell);

        /* ============ 11. no gateway lays as before ============ */
        System.out.println("\n--- no gateway lays as before ---");
        CityRuns old = new CityRuns();
        old.setGateway(CityRuns.NO_GATEWAY);
        long boxX0 = siteX - 20L * cell, boxX1 = siteX + 20L * cell;
        long boxY0 = siteY - 20L * cell, boxY1 = siteY + 20L * cell;
        BoxGround oldGround = new BoxGround(boxX0, boxY0, boxX1, boxY1);
        old.layTo(1, 0, 0, 0, 0, oldGround, siteX, siteY, siteX, siteY, new ArrayList<>());
        old.layTo(6, 0, 0, 0, 0, oldGround, siteX, siteY, siteX, siteY, new ArrayList<>());
        CityRuns.Net oldHw = old.highways;
        int eastSteps = 0, westSteps = 0;
        for (CityRuns.Arm a : oldHw.arms) {
            if (a.home == 0) eastSteps += a.steps;
            if (a.home == 4) westSteps += a.steps;
        }
        check("with no gateway the row's two arms both grow first", eastSteps > 0 && westSteps > 0);
        check("...and no column arm has grown yet", oldHw.arms.get(2).steps == 0 && oldHw.arms.get(3).steps == 0);

        /* ============ 12. the sidecar keeps it ============ */
        System.out.println("\n--- the sidecar keeps it ---");
        BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        World world = World.of(Founding.DEFAULT_WORLD_SEED);
        long[] place = MapCheck.dryPlace(world);
        long sx = place[0], sy = place[1];
        long[] counts = new long[types.length];
        int highway = -1;
        for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].road() == BuildingVisual.HIGHWAY) highway = t;
        check("the catalog has a highway type", highway >= 0);
        if (highway >= 0) counts[highway] = 100;
        CityMap m = CityMap.square(Founding.DEFAULT_WORLD_SEED, sx, sy, types, 2, CityMap.DISTRICT * CityMap.DISTRICT, counts);
        System.out.printf("  the square city's gateway: %d%n", m.gateway());
        check("a square city's runs have found a gateway (or found none)", m.gateway() != CityRuns.GATEWAY_UNKNOWN);
        byte[] eight = m.writeSidecar(1);
        CityMap read8 = CityMap.readSidecar(eight, m.land(), res -> m.land().amountsInOrder(res), m.types(), 1, CityMap.stampIn(eight));
        check("FORMAT 8 writes the gateway and reads it back the same", read8 != null && read8.gateway() == m.gateway());
        byte[] seven = m.writeSidecar(1, 7, true);
        CityMap read7 = CityMap.readSidecar(seven, m.land(), res -> m.land().amountsInOrder(res), m.types(), 1, CityMap.stampIn(seven));
        check("a FORMAT 7 sidecar reads it back unknown", read7 != null && read7.gateway() == CityRuns.GATEWAY_UNKNOWN);
        if (read7 != null) read7.layRuns(counts);
        check("...and the next layRuns finds the same gateway again", read7 != null && read7.gateway() == m.gateway());

        /* ============ 13. what finding one costs ============ */
        System.out.println("\n--- what finding one costs ---");
        World timedWorld = World.of(0);
        long t0 = System.nanoTime();
        Gateways.Gateway timed = Gateways.of(timedWorld, timedWorld.foundingX(), timedWorld.foundingY(), radius);
        double ms = (System.nanoTime() - t0) / 1e6;
        System.out.printf("  one real generated world's gateway found in %.3f ms (bearing %s)%n",
                ms, timed == null ? "none" : timed.bearing());

        /* ============ 14. the world line ============ */
        System.out.println("\n--- the world line ---");
        BuildingVisual.Type[] wtypes = gatewayTypes();
        int wh = highwayType(wtypes), wt = trackType(wtypes);
        check("the catalog has a highway and a track type", wh >= 0 && wt >= 0);
        CityMap wm = gatewayCity(wtypes, 100, 10);
        System.out.printf("  the world-line city's gateway: %d%n", wm.gateway());
        check("the fixture has a gateway", wm.gateway() >= 0);
        List<CityMap.WorldLine> lines = wm.worldLines();
        CityRuns.Net whw = wm.runs().highways(), wrail = wm.runs().rail();
        int hwArm = CityRuns.gateArm(whw, wm.gateway()), railArm = CityRuns.gateArm(wrail, wm.gateway());
        int hwLines = 0, railLines = 0;
        for (CityMap.WorldLine line : lines) {
            if (line.net() == CityRuns.HIGHWAYS) hwLines++;
            else if (line.net() == CityRuns.RAILWAY) railLines++;
            check("a world line's net is a highway or the railway",
                    line.net() == CityRuns.HIGHWAYS || line.net() == CityRuns.RAILWAY);
            check("...its bearing is the gateway", line.bearing() == wm.gateway());
            long px = (long) Math.floor(line.x()), py = (long) Math.floor(line.y());
            check("...its point is ground the city does not own", wm.runGround().at(px, py, -1) == -1);
            CityRuns.Net net = line.net() == CityRuns.HIGHWAYS ? whw : wrail;
            int a = CityRuns.gateArm(net, wm.gateway());
            CityRuns.Arm gateArm = net.arms.get(a);
            int h = gateArm.steps == 0 ? gateArm.home : gateArm.head;
            check("...and the plot one step back along the gate arm's heading is owned",
                    wm.runGround().at(px - CityRuns.DX[h], py - CityRuns.DY[h], -1) != -1);
        }
        check("worldLines() has one line for the highway, whose gate arm is laid", (hwArm >= 0) == (hwLines == 1));
        check("...and one for the railway, whose gate arm is laid", (railArm >= 0) == (railLines == 1));
        CityMap none = gatewayCity(wtypes, 100, 0);
        none.runs().setGateway(CityRuns.NO_GATEWAY);
        check("with NO_GATEWAY set there are no world lines", none.worldLines().isEmpty());

        /* ============ 15. the cache ============ */
        System.out.println("\n--- the cache ---");
        BuildingVisual.Type[] ctypes = gatewayTypes();
        int ch = highwayType(ctypes), ct = trackType(ctypes);
        CityMap small = gatewayCity(ctypes, 1, 1);
        List<CityMap.WorldLine> first = small.worldLines();
        List<CityMap.WorldLine> second = small.worldLines();
        check("two calls with nothing changed return equal lists", first.equals(second));
        long changesBefore = small.changes();
        long[] grown = small.totals().clone();
        grown[ch] = 100;
        grown[ct] = 10;
        boolean moved = small.layRuns(grown);
        check("a month's runs move changes()", moved && small.changes() > changesBefore);
        CityMap fresh = gatewayCity(ctypes, 100, 10);
        check("after the runs move, the world lines are recomputed equal to a fresh map's",
                small.worldLines().equals(fresh.worldLines()));

        coastSection();
        coastMonths();
        halifaxSection();
        labelSection();
        undrawnSection();
        togetherSection();

        System.out.println();
        System.out.println(fails == 0 ? "The gateways are found." : fails + " FAILED");
        System.exit(fails);
    }

    /* ============ FX2: the line to the world on land, the label in view, the undrawn highway ============ */

    /** An arm as one line of text: its index, heading in degrees and end, or "none". */
    static String armText(CityRuns.Net n, int a) {
        if (a < 0 || a >= n.arms.size()) return "none";
        CityRuns.Arm arm = n.arms.get(a);
        return String.format("%d (heading %d, end (%d, %d), %d steps)", a, arm.head * 45, arm.x, arm.y, arm.steps);
    }

    /**
     * FR0's walk, written out: from net n's arm a's end (the hub while the arm has no steps) along its heading, up to
     * CityMap.DISTRICT plots, to the first plot the city does not own. Returns {the owned plots that are not dry ground
     * it crossed, 1 when it found that plot and 0 when it did not}.
     */
    static int[] fr0Walk(CityMap m, CityRuns.Net n, int a) {
        CityRuns.Arm arm = n.arms.get(a);
        long x = arm.steps == 0 ? n.hubX : arm.x, y = arm.steps == 0 ? n.hubY : arm.y;
        int h = arm.steps == 0 ? arm.home : arm.head;
        int water = 0;
        for (int s = 1; s <= CityMap.DISTRICT; s++) {
            int g = m.runGround().at(x + (long) CityRuns.DX[h] * s, y + (long) CityRuns.DY[h] * s, -1);
            if (g == -1) return new int[] { water, 1 };
            if (!World.dry(g)) water++;
        }
        return new int[] { water, 0 };
    }

    /** The least distance in plots from (x, y) to the route's polyline (FX5: a rounded route passes by its turns' points). */
    static double nearestOnRoute(TrainSchedule.Route r, double x, double y) {
        double best = Double.MAX_VALUE;
        double[] xs = r.xs(), ys = r.ys();
        for (int i = 0; i + 1 < xs.length; i++) {
            double dx = xs[i + 1] - xs[i], dy = ys[i + 1] - ys[i], l2 = dx * dx + dy * dy;
            double t = l2 > 0 ? Math.max(0, Math.min(1, ((x - xs[i]) * dx + (y - ys[i]) * dy) / l2)) : 0;
            best = Math.min(best, Math.hypot(xs[i] + t * dx - x, ys[i] + t * dy - y));
        }
        return best;
    }

    /** How many of a route's points, read every WORLD_STEP along each leg, lie on neither dry ground nor a plot the railway carries (track, track over a highway, a yard); the points read are in samples[0]. */
    static int offTrack(World w, CityRuns runs, TrainSchedule.Route r, int[] samples) {
        int bad = 0;
        for (int i = 1; i < r.xs().length; i++) {
            double x0 = r.xs()[i - 1], y0 = r.ys()[i - 1], dx = r.xs()[i] - x0, dy = r.ys()[i] - y0, len = Math.hypot(dx, dy);
            int n = (int) Math.ceil(len / CityMap.WORLD_STEP);
            for (int k = 0; k <= n; k++) {
                double t = len == 0 ? 0 : Math.min(len, k * CityMap.WORLD_STEP) / len;
                long px = (long) Math.floor(x0 + dx * t), py = (long) Math.floor(y0 + dy * t);
                samples[0]++;
                byte at = runs.at(px, py);
                boolean carried = at == CityRuns.F_RAIL || at == CityRuns.F_RAIL_OVER || at == CityRuns.F_YARD;
                if (!World.dry(w.terrainAt(px, py)) && !carried) bad++;
            }
        }
        return bad;
    }

    /**
     * The checks every FX2 fixture runs: each of the map's world lines starts on a dry plot the city does not own, at the
     * end of an arm's track within WORLD_GAP plots of it over the city's own dry ground, and runs out over dry ground
     * for TrainSchedule.WORLD_PLOTS to CityMap.WORLD_REACH plots and no farther than the land runs; the railway's main
     * arm is its line's arm; and each train route's first point is WORLD_PLOTS out along the line's way, its second the
     * line's point, its third the main arm's end plot's middle, with no point of any leg on water off the track.
     */
    static void worldLinesOnLand(String what, CityMap m) {
        World w = World.of(m.worldKey());
        CityRuns runs = m.runs();
        CityRuns.Ground ground = m.runGround();
        List<CityMap.WorldLine> lines = m.worldLines();
        int gateway = m.gateway();
        int railGate = CityRuns.gateArm(runs.rail(), gateway), main = runs.mainArm();
        List<TrainSchedule.Route> routes = m.trainRoutes();
        System.out.printf("  %s: gateway %d; rail gate arm %s; rail main arm %s; %d line(s); %d route(s)%n",
                what, gateway, armText(runs.rail(), railGate), armText(runs.rail(), main), lines.size(), routes.size());
        for (CityMap.WorldLine l : lines) {
            System.out.printf("      line: net %s, way %d, reach %.2f plots, from arm %d at plot (%.1f, %.1f)%n",
                    l.net() == CityRuns.HIGHWAYS ? "highways" : "railway", l.way(), l.reach(), l.arm(), l.x(), l.y());
        }
        check(what + ": fixture: the city has world lines", !lines.isEmpty());
        boolean oneEach = true, unowned = true, dryPoint = true, armOk = true, gapOk = true, between = true,
                reachOk = true, onLand = true, tight = true;
        int[] perNet = new int[2];
        for (CityMap.WorldLine l : lines) {
            perNet[l.net() == CityRuns.HIGHWAYS ? 0 : 1]++;
            long px = (long) Math.floor(l.x()), py = (long) Math.floor(l.y());
            unowned &= ground.at(px, py, -1) == -1;
            dryPoint &= World.dry(w.terrainAt(px, py));
            CityRuns.Net net = l.net() == CityRuns.HIGHWAYS ? runs.highways() : runs.rail();
            boolean haveArm = l.arm() >= 0 && l.arm() < net.arms.size() && net.arms.get(l.arm()).steps > 0;
            armOk &= haveArm;
            if (haveArm) {
                CityRuns.Arm a = net.arms.get(l.arm());
                int gap = -1;
                for (int s = 1; s <= CityMap.WORLD_GAP && gap < 0; s++) {
                    if (a.x + (long) CityRuns.DX[a.head] * s == px && a.y + (long) CityRuns.DY[a.head] * s == py) gap = s;
                }
                gapOk &= gap >= 1;
                for (int s = 1; s < gap; s++) {
                    int g = ground.at(a.x + (long) CityRuns.DX[a.head] * s, a.y + (long) CityRuns.DY[a.head] * s, -1);
                    between &= g != -1 && World.dry(g);
                }
            }
            reachOk &= l.reach() >= TrainSchedule.WORLD_PLOTS && l.reach() <= CityMap.WORLD_REACH;
            double c = Math.cos(Math.toRadians(l.way())), s = Math.sin(Math.toRadians(l.way()));
            // The points read are those before the first that is not dry ground (dryRun's): the last, at WORLD_REACH, is read too.
            boolean full = l.reach() >= CityMap.WORLD_REACH;
            for (int i = 0; (full ? i * CityMap.WORLD_STEP <= l.reach() : i * CityMap.WORLD_STEP < l.reach()); i++) {
                double t = i * CityMap.WORLD_STEP;
                onLand &= World.dry(w.terrainAt((long) Math.floor(l.x() + c * t), (long) Math.floor(l.y() + s * t)));
            }
            if (!full) {
                double t = l.reach();
                tight &= !World.dry(w.terrainAt((long) Math.floor(l.x() + c * t), (long) Math.floor(l.y() + s * t)));
            }
            if (l.net() == CityRuns.RAILWAY) check(what + ": the railway's main arm is its line's arm", main == l.arm());
        }
        oneEach = perNet[0] <= 1 && perNet[1] <= 1;
        check(what + ": at most one world line a net", oneEach);
        check(what + ": every line's point lies on a plot the city does not own", unowned);
        check("...and on dry ground", dryPoint);
        check("...at the end of a laid arm's track", armOk);
        check("...within WORLD_GAP plots of it along its heading", gapOk);
        check("...every plot between the arm's end and the point owned and dry", between);
        check("...its reach from TrainSchedule.WORLD_PLOTS to CityMap.WORLD_REACH", reachOk);
        check("...every point along its way, every WORLD_STEP, on dry ground", onLand);
        check("...and it runs as far as the land does: the next point is not dry, or the reach is the most", tight);

        CityMap.WorldLine rail = null;
        for (CityMap.WorldLine l : lines) if (l.net() == CityRuns.RAILWAY) rail = l;
        if (rail == null) {
            check(what + ": with no railway line there are no trains' routes", routes.isEmpty());
            return;
        }
        check(what + ": the railway has trains' routes", !routes.isEmpty());
        CityRuns.Arm mainArm = runs.rail().arms.get(rail.arm());
        double fx = rail.x() + Math.cos(Math.toRadians(rail.way())) * TrainSchedule.WORLD_PLOTS;
        double fy = rail.y() + Math.sin(Math.toRadians(rail.way())) * TrainSchedule.WORLD_PLOTS;
        boolean first = true, second = true, third = true;
        int bad = 0;
        int[] samples = new int[1];
        for (TrainSchedule.Route r : routes) {
            if (r.xs().length < 3) { first = false; continue; }
            first &= Math.abs(r.xs()[0] - fx) < EPS && Math.abs(r.ys()[0] - fy) < EPS;
            // FX5: the routes are rounded at their turns (TrackCurves.RADIUS), the line's join and the arm's end among them, so the
            // route passes by those points within the radius rather than through them as points of its own.
            second &= nearestOnRoute(r, rail.x(), rail.y()) <= TrackCurves.RADIUS;
            third &= nearestOnRoute(r, mainArm.x + 0.5, mainArm.y + 0.5) <= TrackCurves.RADIUS;
            bad += offTrack(w, runs, r, samples);
        }
        System.out.printf("      %d route(s), %,d points read, %d on water off the track%n", routes.size(), samples[0], bad);
        check("...every route's first point is WORLD_PLOTS out along the line's way", first);
        check("...it passes the line's point, within the curve's radius (FX5: its turns are rounded)", second);
        check("...and the main arm's end plot's middle, within the radius: the train meets the line where the track ends", third);
        check("...no point of any leg on water off the track", bad == 0 && samples[0] > 0);
    }

    /** The coast fixture's counts: the shore's town with every track type's count times COAST_TRACK_TIMES and YARDS_ADDED more of each Rail Terminal. */
    static long[] coastCounts(BuildingVisual.Type[] types) {
        long[] c = MapCheck.shoreCounts(types);
        for (BuildingVisual.Type t : types) {
            if (t == null) continue;
            if (t.track()) c[t.id()] *= COAST_TRACK_TIMES;
            if (t.terminal()) c[t.id()] += MapCheck.YARDS_ADDED;
        }
        return c;
    }

    /** Section 16: the default world's founding site one district north, three districts a side, drawn canonically on its coast. */
    static void coastSection() {
        System.out.println("\n--- the line to the world leaves on land, on a generated coast ---");
        BuildingVisual.Type[] types = gatewayTypes();
        World w = World.of(Founding.DEFAULT_WORLD_SEED);
        long[] counts = coastCounts(types);
        CityMap m = MapCheck.coastal(Founding.DEFAULT_WORLD_SEED, w.foundingX(), w.foundingY() - CityMap.DISTRICT, types, MapCheck.SHORE_SIDE, counts);
        CityRuns runs = m.runs();
        int gate = CityRuns.gateArm(runs.rail(), m.gateway());
        check("fixture: the coast city has a gateway and a railway whose gate arm is laid",
                m.gateway() >= 0 && gate >= 0 && runs.rail().arms.get(gate).steps > 0);
        if (gate >= 0) {
            int[] walk = fr0Walk(m, runs.rail(), gate);
            System.out.printf("  FR0's walk from the gate arm's end: %d plot(s) of the city's own water crossed, %s an unowned plot%n",
                    walk[0], walk[1] == 1 ? "then" : "no");
            check("fixture: the railway's gate arm waits at the sea, FR0's walk crossing the city's own water", walk[0] >= 1);
        }
        worldLinesOnLand("the coast", m);
        CityMap.WorldLine rail = null;
        for (CityMap.WorldLine l : m.worldLines()) if (l.net() == CityRuns.RAILWAY) rail = l;
        check("the coast: the railway has a line, so the checks above are not vacuous", rail != null);
        check("...and its main arm is not the gate arm", rail != null && runs.mainArm() != gate);
    }

    /**
     * Section 16's coast over its months (FX2's "over a real run's months": the
     * played city of MapCheck 1 lays no highway and no track in its 120): the
     * same city from nothing, its counts rising evenly to coastCounts() over
     * MapCheck.MONTHS months and reconciled each month as the game's map is,
     * every month checked by MapCheck.worldWaysOk().
     */
    static void coastMonths() {
        System.out.println("\n--- the coast's months: the line on land as the city grows ---");
        BuildingVisual.Type[] types = gatewayTypes();
        World w = World.of(Founding.DEFAULT_WORLD_SEED);
        long[] last = coastCounts(types), now = new long[last.length];
        CityMap m = MapCheck.coastal(Founding.DEFAULT_WORLD_SEED, w.foundingX(), w.foundingY() - CityMap.DISTRICT, types, MapCheck.SHORE_SIDE, now);
        int bad = 0, lineMonths = 0, routeMonths = 0, offGate = 0, firstLine = -1, firstRoutes = -1;
        for (int month = 1; month <= MapCheck.MONTHS; month++) {
            for (int t = 0; t < last.length; t++) now[t] = last[t] * month / MapCheck.MONTHS;
            m.reconcile(now);
            int[] seen = new int[2];
            if (!MapCheck.worldWaysOk(m, seen)) bad++;
            lineMonths += seen[0];
            routeMonths += seen[1];
            if (seen[0] == 1 && firstLine < 0) firstLine = month;
            if (seen[1] == 1 && firstRoutes < 0) firstRoutes = month;
            if (seen[1] == 1 && m.runs().mainArm() != CityRuns.gateArm(m.runs().rail(), m.gateway())) offGate++;
        }
        System.out.printf("  over %d months: a world line in %d (from month %d), trains' routes in %d (from month %d), %d of them from an arm"
                + " not the gate arm; %d month(s) with a line or a train on water%n", MapCheck.MONTHS, lineMonths, firstLine, routeMonths,
                firstRoutes, offGate, bad);
        check("the coast's months: some months have a world line and trains' routes, so the run exercised the rule", lineMonths > 0 && routeMonths > 0);
        check("...and no month has a line's point or a train position on water off the track", bad == 0);
    }

    /** Section 17: a square city at Jerus's founding site on the Earth, when earth-2.bin is named. */
    static void halifaxSection() {
        System.out.println("\n--- Halifax on the Earth, when earth-2.bin is named ---");
        Path file = EarthData.locate();
        String named = System.getProperty(EarthData.PROPERTY);
        if (named != null && !named.isBlank() && !Files.isRegularFile(file)) {
            check("the file -D" + EarthData.PROPERTY + " names is there: " + named, false);
            return;
        }
        if (file == null) {
            System.out.println("  earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": this section did not run");
            return;
        }
        try (EarthData data = EarthData.open(file)) {
            BuildingVisual.Type[] types = gatewayTypes();
            World w = World.of(World.Key.earth(EarthTerrainCheck.SEED, HALIFAX_LAT, HALIFAX_LON, EarthTerrain.use(data)));
            // Jerus's city x 1 (MapCheck.Squares' first copy: JERUS_COUNTS times TIMES[0]) with no iron mines, and the yards and freight line his city has.
            long[] counts = new long[types.length];
            for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < counts.length; t++) counts[t] = Math.round(MapCheck.JERUS_COUNTS[t] * MapCheck.TIMES[0]);
            counts[ham.citybuildersim.sectors.Mining.IRON_MINE] = 0;
            for (BuildingVisual.Type t : types) {
                if (t == null) continue;
                if (t.terminal()) counts[t.id()] += MapCheck.YARDS_ADDED;
                else if (t.track() && t.plots() > 100) counts[t.id()] += MapCheck.FREIGHT_ADDED;
            }
            CityMap m = CityMap.square(w.key(), w.foundingX(), w.foundingY(), types, MapCheck.x1Side(types),
                    (int) Math.round(MapCheck.JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT), counts);
            CityRuns runs = m.runs();
            int gate = CityRuns.gateArm(runs.rail(), m.gateway());
            check("fixture: Halifax has a gateway and a railway whose gate arm is laid",
                    m.gateway() >= 0 && gate >= 0 && runs.rail().arms.get(gate).steps > 0);
            if (gate >= 0) {
                int[] walk = fr0Walk(m, runs.rail(), gate);
                System.out.printf("  FR0's walk from the gate arm's end: %d plot(s) of the city's own water crossed, %s an unowned plot%n",
                        walk[0], walk[1] == 1 ? "then" : "no");
                check("fixture (ET6): Halifax's railway gate arm no longer waits at the harbour - it bridges the shallow water to the city's edge, so FR0's walk crosses none of the city's own water (section 16's coast keeps the arm that waits)", walk[0] == 0 && walk[1] == 1);
            }
            worldLinesOnLand("Halifax", m);
            CityMap.WorldLine rail = null;
            for (CityMap.WorldLine l : m.worldLines()) if (l.net() == CityRuns.RAILWAY) rail = l;
            check("Halifax: the railway has a line, so the checks above are not vacuous", rail != null);
        } catch (IOException e) {
            check("the Earth's file opens: " + e, false);
        }
    }

    /* ----- the label (section 18) ----- */

    /** The view and the label it is tried with: 1,000 x 600 pixels, a label 110 wide and 13 high above its baseline. */
    static final double VIEW_W = 1000, VIEW_H = 600, LABEL_W = 110, LABEL_H = 13;

    /** Whether a label whose baseline starts at (x, y) lies inside a w x h view by LandMap.WORLD_LABEL_PAD on every side - the harness's own reading of the rule. */
    static boolean inView(double x, double y, double w, double h) {
        double pad = LandMap.WORLD_LABEL_PAD;
        return x >= pad - EPS && x + LABEL_W <= w - pad + EPS && y - LABEL_H >= pad - EPS && y <= h - pad + EPS;
    }

    /** How far (x, y) lies from the segment (x0, y0) to (x1, y1), in pixels. */
    static double offPart(double x, double y, double x0, double y0, double x1, double y1) {
        double dx = x1 - x0, dy = y1 - y0, len2 = dx * dx + dy * dy;
        double t = len2 == 0 ? 0 : Math.max(0, Math.min(1, ((x - x0) * dx + (y - y0) * dy) / len2));
        return Math.hypot(x - (x0 + dx * t), y - (y0 + dy * t));
    }

    /** One line leaving the view: FR0's spot clips, and worldLabelAt() puts the label whole inside the view, its baseline's left end on the part, at the first place along it, from FR0's spot back, that fits. */
    static void labelLeaving(String what, double x0, double y0, double x1, double y1) {
        double len = Math.hypot(x1 - x0, y1 - y0), ux = (x1 - x0) / len, uy = (y1 - y0) / len;
        double frx = x1 - ux * LandMap.WORLD_LABEL_BACK, fry = y1 - uy * LandMap.WORLD_LABEL_BACK;
        check(what + ": fixture: FR0's spot (" + Math.round(frx) + ", " + Math.round(fry) + ") runs the label past the view", !inView(frx, fry, VIEW_W, VIEW_H));
        double[] at = LandMap.worldLabelAt(x0, y0, x1, y1, LABEL_W, LABEL_H, VIEW_W, VIEW_H);
        check("...the label is placed", at != null);
        if (at == null) return;
        System.out.printf("      the label's baseline starts at (%.1f, %.1f)%n", at[0], at[1]);
        check("...its box lies inside the view by WORLD_LABEL_PAD on every side", inView(at[0], at[1], VIEW_W, VIEW_H));
        check("...its baseline's left end on the line's part", offPart(at[0], at[1], x0, y0, x1, y1) < 1e-6);
        double back = Math.hypot(x1 - at[0], y1 - at[1]);
        double nearer = back - 1;
        check("...at the first place from FR0's spot that fits: a pixel nearer the far end does not",
                nearer < LandMap.WORLD_LABEL_BACK - EPS || !inView(x1 - ux * nearer, y1 - uy * nearer, VIEW_W, VIEW_H));
    }

    /** Section 18: LandMap.worldLabelAt() on pure fixtures. */
    static void labelSection() {
        System.out.println("\n--- the label stays in view ---");
        labelLeaving("a line leaving the right edge", 200, 400, VIEW_W, 300);
        labelLeaving("a line leaving the top edge", 100, 60, 700, 0);
        labelLeaving("a line leaving the bottom edge", 100, 570, 700, VIEW_H);
        labelLeaving("a line leaving the top right corner", 400, VIEW_H, VIEW_W, 0);

        double[] fits = LandMap.worldLabelAt(0, 300, 600, 300, LABEL_W, LABEL_H, VIEW_W, VIEW_H);
        check("a line ending well inside the view keeps FR0's spot, WORLD_LABEL_BACK from its far end",
                fits != null && Math.abs(fits[0] - (600 - LandMap.WORLD_LABEL_BACK)) < EPS && Math.abs(fits[1] - 300) < EPS);

        // A part too short to hold the label anywhere on it: read every quarter pixel, none fits.
        double[][] shorts = { { 996, 300, VIEW_W, 300 }, { 500, 3, 500, 0 } };
        for (double[] p : shorts) {
            double len = Math.hypot(p[2] - p[0], p[3] - p[1]);
            boolean anywhere = false;
            for (double t = 0; t <= len + EPS; t += 0.25) anywhere |= inView(p[0] + (p[2] - p[0]) * t / len, p[1] + (p[3] - p[1]) * t / len, VIEW_W, VIEW_H);
            check("a part of " + Math.round(len) + " px at (" + Math.round(p[2]) + ", " + Math.round(p[3]) + "): fixture: the label fits nowhere on it", !anywhere);
            double[] at = LandMap.worldLabelAt(p[0], p[1], p[2], p[3], LABEL_W, LABEL_H, VIEW_W, VIEW_H);
            check("...the label still lies inside the view", at != null && inView(at[0], at[1], VIEW_W, VIEW_H));
        }
        check("a part of no length has no label", LandMap.worldLabelAt(500, 300, 500, 300, LABEL_W, LABEL_H, VIEW_W, VIEW_H) == null);
        check("a view smaller than the label and its pads has none either",
                LandMap.worldLabelAt(0, 50, 90, 50, LABEL_W, LABEL_H, LABEL_W, VIEW_H) == null);
    }

    /* ----- two lines leaving together (section 20) ----- */

    /** The shared label's width in the harness: WORLD_BOTH_WORDS in the font that makes WORLD_WORDS LABEL_W wide (monospaced: by the characters). */
    static final double LABEL_BOTH_W = LABEL_W * LandMap.WORLD_BOTH_WORDS.length() / (double) LandMap.WORLD_WORDS.length();

    /** Whether two label boxes (baselines' left ends a and b, widths wa and wb, LABEL_H high above their baselines) are clear of each other by `gap` pixels, across or down - the harness's own reading of the rule. */
    static boolean clearOf(double[] a, double wa, double[] b, double wb, double gap) {
        return a[0] + wa + gap <= b[0] + EPS || b[0] + wb + gap <= a[0] + EPS
                || a[1] + gap <= b[1] - LABEL_H + EPS || b[1] + gap <= a[1] - LABEL_H + EPS;
    }

    /** Section 20: two world lines leaving together. */
    static void togetherSection() {
        System.out.println("\n--- two lines leaving together get labels apart, or one ---");
        // His city's: the highway's and the railway's lines leave on one bearing, a few pixels apart, both through the right edge.
        double[] hw = { 200, 400, VIEW_W, 300 }, rl = { 200, 403, VIEW_W, 303 };
        double[] a = LandMap.worldLabelAt(hw[0], hw[1], hw[2], hw[3], LABEL_W, LABEL_H, VIEW_W, VIEW_H);
        double[] b = LandMap.worldLabelAt(rl[0], rl[1], rl[2], rl[3], LABEL_W, LABEL_H, VIEW_W, VIEW_H);
        check("fixture: two parts three pixels apart on one bearing are both placed by worldLabelAt() alone", a != null && b != null);
        check("...and, drawn so (as MapView did), their boxes overlap: the words print over each other", a != null && b != null && !clearOf(a, LABEL_W, b, LABEL_W, 0));

        // Parts too short for either label to slide clear: the fixture's old placement still overlaps.
        double[] sh = { 900, 300, VIEW_W, 300 }, sr = { 900, 303, VIEW_W, 303 };
        double[] sa = LandMap.worldLabelAt(sh[0], sh[1], sh[2], sh[3], LABEL_W, LABEL_H, VIEW_W, VIEW_H);
        double[] sb = LandMap.worldLabelAt(sr[0], sr[1], sr[2], sr[3], LABEL_W, LABEL_H, VIEW_W, VIEW_H);
        check("fixture: two parts of 100 px, three apart, fit no label anywhere on them, and worldLabelAt() puts both on one spot",
                sa != null && sb != null && !clearOf(sa, LABEL_W, sb, LABEL_W, 0));

        // The fix: the second slides back along its own part until the boxes are clear.
        LandMap.WorldLabel[] two = LandMap.worldLabels(new double[][] { hw, rl }, LABEL_W, LABEL_BOTH_W, LABEL_H, VIEW_W, VIEW_H);
        check("worldLabels() gives each of two parts leaving together a label", two.length == 2 && two[0] != null && two[1] != null);
        if (two[0] != null && two[1] != null) {
            double[] p0 = { two[0].x(), two[0].y() }, p1 = { two[1].x(), two[1].y() };
            System.out.printf("      the labels' baselines start at (%.1f, %.1f) and (%.1f, %.1f)%n", p0[0], p0[1], p1[0], p1[1]);
            check("...the first keeps worldLabelAt()'s place to the pixel", Math.abs(p0[0] - a[0]) < EPS && Math.abs(p0[1] - a[1]) < EPS);
            check("...the boxes are clear of each other by WORLD_LABEL_PAD", clearOf(p0, LABEL_W, p1, LABEL_W, LandMap.WORLD_LABEL_PAD));
            check("...each lies inside the view by WORLD_LABEL_PAD, on its own part", inView(p0[0], p0[1], VIEW_W, VIEW_H) && inView(p1[0], p1[1], VIEW_W, VIEW_H)
                    && offPart(p0[0], p0[1], hw[0], hw[1], hw[2], hw[3]) < 1e-6 && offPart(p1[0], p1[1], rl[0], rl[1], rl[2], rl[3]) < 1e-6);
            check("...and each names its own line: neither is shared", !two[0].both() && !two[1].both()
                    && LandMap.WORLD_WORDS.equals(two[0].words()) && LandMap.WORLD_WORDS.equals(two[1].words()));
        }
        LandMap.WorldLabel[] five = LandMap.worldLabels(new double[][] { hw, rl }, LABEL_W, LABEL_H, VIEW_W, VIEW_H);
        check("...the five-argument form reads the shared words' width off the characters and places the same",
                five[0] != null && five[1] != null && two[0] != null && two[1] != null && five[1].x() == two[1].x() && five[1].y() == two[1].y());

        // Parts with no room to slide: one label names both.
        LandMap.WorldLabel[] cramped = LandMap.worldLabels(new double[][] { sh, sr }, LABEL_W, LABEL_BOTH_W, LABEL_H, VIEW_W, VIEW_H);
        check("two parts with no clear place anywhere have one label between them: the first names both, the second has none",
                cramped[0] != null && cramped[0].both() && cramped[1] == null && LandMap.WORLD_BOTH_WORDS.equals(cramped[0].words()));
        if (cramped[0] != null) {
            double[] at = { cramped[0].x(), cramped[0].y() };
            boolean inside = at[0] >= LandMap.WORLD_LABEL_PAD - EPS && at[0] + LABEL_BOTH_W <= VIEW_W - LandMap.WORLD_LABEL_PAD + EPS
                    && at[1] - LABEL_H >= LandMap.WORLD_LABEL_PAD - EPS && at[1] <= VIEW_H - LandMap.WORLD_LABEL_PAD + EPS;
            check("...and, the shared words being wider, it lies whole inside the view", inside);
        }

        // Two lines on opposite sides of the view: untouched.
        double[] east = { 200, 300, VIEW_W, 250 }, west = { 800, 350, 0, 400 };
        LandMap.WorldLabel[] apart = LandMap.worldLabels(new double[][] { east, west }, LABEL_W, LABEL_BOTH_W, LABEL_H, VIEW_W, VIEW_H);
        double[] ea = LandMap.worldLabelAt(east[0], east[1], east[2], east[3], LABEL_W, LABEL_H, VIEW_W, VIEW_H);
        double[] wa = LandMap.worldLabelAt(west[0], west[1], west[2], west[3], LABEL_W, LABEL_H, VIEW_W, VIEW_H);
        check("lines on opposite sides of the view keep worldLabelAt()'s places to the pixel",
                apart[0] != null && apart[1] != null && ea != null && wa != null
                        && apart[0].x() == ea[0] && apart[0].y() == ea[1] && apart[1].x() == wa[0] && apart[1].y() == wa[1]);
        check("...and neither is shared", apart[0] != null && apart[1] != null && !apart[0].both() && !apart[1].both());

        // A part of no length, or no part: no label, and the others undisturbed.
        double[] none = { 500, 300, 500, 300 };
        LandMap.WorldLabel[] mixed = LandMap.worldLabels(new double[][] { none, null, east }, LABEL_W, LABEL_BOTH_W, LABEL_H, VIEW_W, VIEW_H);
        check("a part of no length has no label, nor has a line with no part", mixed[0] == null && mixed[1] == null);
        check("...and the line after them keeps worldLabelAt()'s place", mixed[2] != null && ea != null && mixed[2].x() == ea[0] && mixed[2].y() == ea[1] && !mixed[2].both());
        check("a call with no parts returns no labels", LandMap.worldLabels(new double[0][], LABEL_W, LABEL_BOTH_W, LABEL_H, VIEW_W, VIEW_H).length == 0);
    }

    /* ----- the highway that reaches no street (section 19) ----- */

    /** A whole-box fill of a map's runs: the plots from (x0, y0), w x h, their codes and marks. */
    static final class BoxFill {
        final long x0, y0;
        final int w, h;
        final byte[] fixed, marks;

        BoxFill(CityMap m) {
            long[] b = m.runGround().box();
            x0 = b[0];
            y0 = b[1];
            w = (int) (b[2] - b[0] + 1);
            h = (int) (b[3] - b[1] + 1);
            fixed = new byte[w * h];
            marks = new byte[w * h];
            m.runs().fill(x0, y0, w, h, fixed, marks, false);
        }

        /** Whether plot index g carries a highway (a railway over one included). */
        boolean hw(int g) {
            return fixed[g] == CityRuns.F_HIGHWAY || fixed[g] == CityRuns.F_RAIL_OVER;
        }

        /** Highway plots drawn. */
        long drawn() {
            long n = 0;
            for (int g = 0; g < w * h; g++) if (hw(g)) n++;
            return n;
        }

        /** Whether plot g shows a ramp on a cell line: M_RAMP, and its coordinate along the highway (x on a row, y on a column) a multiple of CityRuns.CELL. */
        boolean rampOnCellLine(int g) {
            if ((marks[g] & CityRuns.M_RAMP) == 0) return false;
            int x = g % w, y = g / w;
            boolean row = (x > 0 && hw(g - 1)) || (x < w - 1 && hw(g + 1));
            boolean col = (y > 0 && hw(g - w)) || (y < h - 1 && hw(g + w));
            return (row && Math.floorMod(x0 + x, (long) CityRuns.CELL) == 0) || (col && Math.floorMod(y0 + y, (long) CityRuns.CELL) == 0);
        }

        /** {pieces of highway (8 neighbours), highway plots in a piece with no ramp on a cell line}. */
        long[] pieces() {
            boolean[] seen = new boolean[w * h];
            int[] queue = new int[w * h];
            long pieces = 0, orphaned = 0;
            for (int s = 0; s < w * h; s++) {
                if (!hw(s) || seen[s]) continue;
                pieces++;
                int head = 0, tail = 0;
                queue[tail++] = s;
                seen[s] = true;
                boolean ramp = false;
                while (head < tail) {
                    int g = queue[head++], x = g % w, y = g / w;
                    ramp |= rampOnCellLine(g);
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            int nx = x + dx, ny = y + dy;
                            if ((dx == 0 && dy == 0) || nx < 0 || ny < 0 || nx >= w || ny >= h) continue;
                            int k = ny * w + nx;
                            if (hw(k) && !seen[k]) { seen[k] = true; queue[tail++] = k; }
                        }
                    }
                }
                if (!ramp) orphaned += tail;
            }
            return new long[] { pieces, orphaned };
        }
    }

    /** The highway plots the model has (its Elevated Highways' plots), as MapCheck counts them. */
    static long modelHighwayPlots(CityMap m) {
        return MapCheck.roadPlotsOf(m.totals(), m.types(), BuildingVisual.HIGHWAY);
    }

    /** Whether a map has a world line for the highways. */
    static boolean hasHighwayLine(CityMap m) {
        for (CityMap.WorldLine l : m.worldLines()) if (l.net() == CityRuns.HIGHWAYS) return true;
        return false;
    }

    /** A reaching city's highways as the map draws them: every plot in a piece with a ramp on a cell line, and the drawn plots and the undrawn ones the model's. */
    static void drawnWhole(String what, CityMap m) {
        CityRuns runs = m.runs();
        check(what + ": fixture: the highways reach a street", runs.highwaysReach());
        BoxFill f = new BoxFill(m);
        long drawn = f.drawn();
        long[] pieces = f.pieces();
        System.out.printf("      %s: %,d highway plots drawn in %d piece(s) of the model's %,d; %,d undrawn%n",
                what, drawn, pieces[0], modelHighwayPlots(m), runs.highwayUndrawn());
        check("...the highways are drawn: there is a piece", drawn > 0 && pieces[0] >= 1);
        check("...every highway plot drawn lies in a piece holding a ramp on a cell line", pieces[1] == 0);
        check("...the drawn plots and highwayUndrawn() are the model's highway plots", drawn + runs.highwayUndrawn() == modelHighwayPlots(m));
        check("...none is undrawn but what the runs could not lay", runs.highwayUndrawn() == runs.highwayShort());
    }

    /** Section 19: gatewayCity() with highways and no track. */
    static void undrawnSection() {
        System.out.println("\n--- a highway that reaches no street is not drawn ---");
        BuildingVisual.Type[] types = gatewayTypes();
        CityMap one = gatewayCity(types, 1, 0);
        CityRuns runs = one.runs();
        check("fixture: with one Elevated Highway the runs laid it", runs.highways().plots() > 0);
        check("...it reaches no arterial's ramp: highwaysReach() is false", !runs.highwaysReach());
        BoxFill f = new BoxFill(one);
        check("...a fill over the city's whole box holds no highway plot", f.drawn() == 0);
        check("...highwayUndrawn() is the model's highway plots", runs.highwayUndrawn() == modelHighwayPlots(one) && modelHighwayPlots(one) > 0);
        check("...highwayShort() stays what the runs could not lay: none here", runs.highwayShort() == 0);
        check("...fixture: the city has a gateway, so the line's absence is the rule's", one.gateway() >= 0);
        check("...and the world lines hold no highway line", !hasHighwayLine(one));

        int n = 1;
        CityMap at = one;
        while (!at.runs().highwaysReach() && n < MOST_HIGHWAYS) at = gatewayCity(types, ++n, 0);
        check("some count of at most " + MOST_HIGHWAYS + " Elevated Highways reaches a street", at.runs().highwaysReach());
        CityMap before = gatewayCity(types, n - 1, 0);
        System.out.printf("  the least count that reaches an arterial's ramp: %d Elevated Highways, %,d plots laid (%,d plots at %d)%n",
                n, at.runs().highways().plots(), before.runs().highways().plots(), n - 1);
        check("...one fewer does not (" + (n - 1) + ")", n >= 2 && !before.runs().highwaysReach());
        check("...and has no highway plot in a whole-box fill", new BoxFill(before).drawn() == 0 && !hasHighwayLine(before));
        drawnWhole("at " + n, at);
        CityMap big = gatewayCity(types, 100, 10);
        drawnWhole("at 100 Elevated Highways and 10 Freight Lines", big);
    }
}
