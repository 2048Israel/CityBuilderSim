package ham.citybuildersim;

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
 * WHY THIS EXISTS: batch FR0 finds the land's mirror of SeaRoutes' offing,
 * so the highway and railway know which way the world is. A fixture CAUSES
 * each condition rather than waiting for a map to.
 */
public class GatewayCheck {

    static int fails = 0;

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

        System.out.println();
        System.out.println(fails == 0 ? "The gateways are found." : fails + " FAILED");
        System.exit(fails);
    }
}
