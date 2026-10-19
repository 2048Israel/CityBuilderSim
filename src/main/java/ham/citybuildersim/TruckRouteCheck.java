package ham.citybuildersim;

import ham.citybuildersim.sectors.Mining;
import java.util.Arrays;
import java.util.List;

/**
 * Proves the trucks' routes (0.8.8, batch FR5, slice B;
 * docs/studies/spec-freight-and-infrastructure.md 4.7, D21): the ways the
 * map lets a truck drive, CityMap.truckRoutes() along the highways from their
 * line to the world and CityMap.accessRoutesIn() along each drawn district's
 * access roads. Not part of the game.
 *
 * WHY THIS EXISTS. Haulage's trucks (FR2) carry every bulk tonne's city leg,
 * and the highways and access roads (FR7) are drawn, but nothing said where a
 * truck's mark runs. The routes are the map's alone - nothing in the model
 * reads them - and are found as the trains' are (FR1): the highways' by the
 * same walk CityRuns.wayToPlot() makes on the railway, which was factored into
 * CityRuns.wayOn() for it and must give the railway what it gave; the access
 * roads' when a plan is packed (CityMap.Drawn.access), and never by drawing a
 * plan.
 *
 * What it has to prove, a section each, every one a fixture that causes the
 * condition (GatewayCheck's and MapCheck's cities; never new Game()):
 *
 *   1. with no highway world line there is no world route: highways that
 *      reach no street (not drawn), highways that reach one but find no way
 *      out, and a railway alone;
 *   2. in a city with a highway world line each world route starts
 *      TRUCK_WORLD_PLOTS out along the line's way, passes the line's point,
 *      runs on highway plots only (the points and the middles of every leg
 *      from the arm's end), ends at a ramp, and is faded in over its world
 *      part; no two end at one ramp, none is shorter than TRUCK_ROUTE_LEAST
 *      along the highways, the number is within TRUCK_ROUTES_MOST;
 *   3. the railway's way is unchanged: wayToPlot() is wayOn() of the rail net
 *      from the main arm for every yard's anchor and every siding's end, and
 *      wayTo() of a yard is wayToPlot() of its anchor; asking for trucks
 *      leaves the trains' routes as they were;
 *   4. the access roads: no plan is drawn by asking; once a district's plan
 *      is drawn each road the plan laid has one non-world route from the
 *      road's end to its gate, every plot along it an access plot, a straight
 *      run kept to its two ends, faded in over TRUCK_ACCESS_FADE; a box
 *      meets only the districts whose frame it meets, under their own keys;
 *   5. the routes are kept against the map's changes (the same list asked
 *      again) and made again once the runs move them;
 *   6. accessRoadCount() is the works that need an access road: the yards
 *      the runs draw, the shore's sea terminals, the Truck Depots - at
 *      least 1.
 */
public class TruckRouteCheck {

    static int fails = 0;

    /** How finely a leg is read, in plots: a quarter, as the world lines are (CityMap.WORLD_STEP). */
    static final double STEP = CityMap.WORLD_STEP;

    /** The tolerance of a position in plots, well below anything the map measures. */
    static final double EPS = 1e-9;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-78s %s%n", label, ok ? "ok" : "FAIL");
    }

    /** Whether two routes are the same, point for point (a record holds arrays by reference, so equals() would not say). */
    static boolean same(TruckSchedule.Route a, TruckSchedule.Route b) {
        return a.world() == b.world() && Arrays.equals(a.xs(), b.xs()) && Arrays.equals(a.ys(), b.ys()) && Arrays.equals(a.along(), b.along())
                && a.fadeTo() == b.fadeTo();
    }

    static boolean same(List<TruckSchedule.Route> a, List<TruckSchedule.Route> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) if (!same(a.get(i), b.get(i))) return false;
        return true;
    }

    /** Whether a map has a world line for the highways. */
    static boolean hasHighwayLine(CityMap m) {
        for (CityMap.WorldLine l : m.worldLines()) if (l.net() == CityRuns.HIGHWAYS) return true;
        return false;
    }

    static CityMap.WorldLine highwayLine(CityMap m) {
        for (CityMap.WorldLine l : m.worldLines()) if (l.net() == CityRuns.HIGHWAYS) return l;
        return null;
    }

    /** The fewest plots a world route's points are read at, every STEP along each leg from `from` (the leg that starts at that point index): how many of them lie on no plot of the highways. */
    static int offHighway(CityRuns runs, TruckSchedule.Route r, int from) {
        int off = 0;
        for (int i = from; i + 1 < r.xs().length; i++) {
            double len = Math.hypot(r.xs()[i + 1] - r.xs()[i], r.ys()[i + 1] - r.ys()[i]);
            int n = (int) Math.ceil(len / STEP);
            for (int k = 0; k <= n; k++) {
                double t = n == 0 ? 0 : (double) k / n;
                long px = (long) Math.floor(r.xs()[i] + t * (r.xs()[i + 1] - r.xs()[i])), py = (long) Math.floor(r.ys()[i] + t * (r.ys()[i + 1] - r.ys()[i]));
                if (!CityRuns.covered(runs.highways(), px, py)) off++;
            }
        }
        return off;
    }

    // ------------------------------------------------------------------ the fixtures

    /** The game's building templates, as the map draws them. */
    static BuildingVisual.Type[] types() {
        return GatewayCheck.gatewayTypes();
    }

    /**
     * MapCheck's access city on its own (his city x 1 where its square holds iron, with Rail Terminals, a Freight Line,
     * 200 more highways and 3 Truck Depots added: MapCheck.accessCity() without the copies it takes a Squares for).
     */
    static CityMap accessCity(BuildingVisual.Type[] types) {
        long seed = Founding.DEFAULT_WORLD_SEED;
        long[] iron = MapCheck.dryPlace(World.of(seed), MapCheck.x1Side(types));
        int capacity = (int) Math.round(MapCheck.JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        int mine = -1, yard = -1, freight = -1, hw = -1;
        for (BuildingVisual.Type t : types) {
            if (t == null) continue;
            if (t.id() == Mining.IRON_MINE) mine = t.id();
            if (t.terminal()) yard = t.id();
            else if (t.track() && t.plots() > 100) freight = t.id();
            if (t.road() == BuildingVisual.HIGHWAY) hw = t.id();
        }
        // His mines, no more than the x 1 square's sites (as MapCheck.Squares has them).
        long[] c = new long[types.length];
        for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < c.length; t++) c[t] = MapCheck.JERUS_COUNTS[t];
        long wantMines = c[mine];
        c[mine] = 0;
        CityMap x1 = CityMap.square(seed, iron[0], iron[1], types, MapCheck.x1Side(types), capacity, c);
        c[mine] = Math.min(wantMines, MapCheck.mineSites(x1, mine));
        // The access city, as MapCheck.accessCity() lays it.
        long[] rail = new long[c.length];
        for (int t = 0; t < c.length; t++) if (types[t] != null && (types[t].track() || types[t].terminal())) { rail[t] = c[t]; c[t] = 0; }
        rail[yard] += MapCheck.YARDS_ADDED;
        rail[freight] += MapCheck.FREIGHT_ADDED;
        c[hw] += MapCheck.ACCESS_HIGHWAYS_ADDED;
        c[MapCheck.depotType(types)] += MapCheck.ACCESS_DEPOTS_ADDED;
        wantMines = c[mine];
        c[mine] = 0;
        int side = MapCheck.x1Side(types);
        CityMap m = CityMap.square(seed, iron[0], iron[1], types, side, capacity, c);
        c[mine] = Math.min(wantMines, MapCheck.mineSites(m, mine));
        m.reconcile(c);
        for (int t = 0; t < c.length; t++) c[t] += rail[t];
        m.reconcile(c);
        return m;
    }

    /** The frame origin of district d, in world plots. */
    static long[] origin(CityMap m, CityMap.District d) {
        return new long[] { (m.baseDX() + d.dx) * CityMap.DISTRICT, (m.baseDY() + d.dy) * CityMap.DISTRICT };
    }

    // ------------------------------------------------------------------ 1

    static void noLine() {
        System.out.println("\n--- no highway world line, no world route ---");
        BuildingVisual.Type[] types = types();
        CityMap one = GatewayCheck.gatewayCity(types, 1, 0);
        check("fixture: one Elevated Highway is laid and reaches no street", one.runs().highways().plots() > 0 && !one.runs().highwaysReach());
        check("...it has no highway world line", !hasHighwayLine(one));
        check("...so no ramps to run to, no highway way, no truck route", one.runs().highwayRamps().isEmpty()
                && one.runs().highwayWayTo(0, one.runs().highways().hub()[0], one.runs().highways().hub()[1]) == null && one.truckRoutes().isEmpty());

        CityMap five = GatewayCheck.gatewayCity(types, 5, 0);
        check("fixture: five Elevated Highways reach a street (they are drawn) and their arms find no way out to the world",
                five.runs().highwaysReach() && five.gateway() >= 0 && !hasHighwayLine(five));
        check("...they have ramps, and still no truck route: a truck comes in from the world line or not at all",
                !five.runs().highwayRamps().isEmpty() && five.truckRoutes().isEmpty());

        CityMap railOnly = GatewayCheck.gatewayCity(types, 0, 10);
        boolean railLine = false;
        for (CityMap.WorldLine l : railOnly.worldLines()) railLine |= l.net() == CityRuns.RAILWAY;
        check("fixture: a railway alone has its world line", railLine && !hasHighwayLine(railOnly));
        check("...and no truck route: the railway is not a road", railOnly.truckRoutes().isEmpty());
    }

    // ------------------------------------------------------------------ 2

    static void highwayRoutes() {
        System.out.println("\n--- the highways' routes: from the world line, along the highways, to each ramp ---");
        BuildingVisual.Type[] types = types();
        CityMap m = GatewayCheck.gatewayCity(types, 300, 10);
        CityRuns runs = m.runs();
        CityMap.WorldLine line = highwayLine(m);
        check("fixture: the highways reach a street and have a world line", runs.highwaysReach() && line != null);
        if (line == null) return;
        List<TruckSchedule.Route> routes = m.truckRoutes();
        List<long[]> ramps = runs.highwayRamps();
        System.out.printf("      %d ramps, %d truck routes; the line leaves arm %d at (%.1f, %.1f) along %d degrees%n", ramps.size(), routes.size(),
                line.arm(), line.x(), line.y(), line.way());
        check("...a ramp at each arm's end: " + runs.highways().arms() + " arms", ramps.size() == runs.highways().arms());
        check("one route a ramp but the arm the line leaves from (the trucks have no road to drive there)",
                routes.size() == ramps.size() - 1 && routes.size() > 1);
        check("...no more than TRUCK_ROUTES_MOST", routes.size() <= CityMap.TRUCK_ROUTES_MOST);

        double r = Math.toRadians(line.way());
        double fx = line.x() + Math.cos(r) * 64, fy = line.y() + Math.sin(r) * 64;
        boolean world = true, starts = true, passes = true, faded = true, onHighways = true, endsAtRamp = true, least = true, grows = true;
        java.util.Set<String> ends = new java.util.HashSet<>();
        boolean distinct = true;
        for (TruckSchedule.Route rt : routes) {
            int n = rt.xs().length;
            world &= rt.world();
            starts &= n >= 3 && Math.abs(rt.xs()[0] - fx) < EPS && Math.abs(rt.ys()[0] - fy) < EPS;
            passes &= n >= 3 && Math.abs(rt.xs()[1] - line.x()) < EPS && Math.abs(rt.ys()[1] - line.y()) < EPS;
            faded &= Math.abs(rt.fadeTo() - 64) < EPS && Math.abs(rt.along()[1] - rt.fadeTo()) < EPS;
            // From the arm's end (the third point) on: every point, and every quarter plot of every leg, on a highway plot.
            onHighways &= offHighway(runs, rt, 2) == 0;
            for (int i = 2; i < n; i++) onHighways &= CityRuns.covered(runs.highways(), (long) Math.floor(rt.xs()[i]), (long) Math.floor(rt.ys()[i]));
            boolean ramp = false;
            double lx = rt.xs()[n - 1], ly = rt.ys()[n - 1];
            for (long[] p : ramps) ramp |= Math.abs(lx - (p[0] + 0.5)) < EPS && Math.abs(ly - (p[1] + 0.5)) < EPS;
            endsAtRamp &= ramp;
            distinct &= ends.add(lx + "," + ly);
            least &= rt.length() - rt.along()[2] >= CityMap.TRUCK_ROUTE_LEAST;
            for (int i = 1; i < n; i++) grows &= rt.along()[i] > rt.along()[i - 1];
        }
        check("every world route is a world route", world && !routes.isEmpty());
        check("...starts TRUCK_WORLD_PLOTS (64) out along the line's way from the line's point", starts);
        check("...passes the line's point, its world part the first leg", passes);
        check("...is faded in over the world part: fadeTo is its length", faded);
        check("...from the arm's end runs on highway plots only: its points and every quarter plot of its legs", onHighways);
        check("...ends at a ramp, at a plot's middle", endsAtRamp);
        check("...no two end at one ramp", distinct);
        check("...each runs at least TRUCK_ROUTE_LEAST along the highways", least);
        check("...and has no leg of no length", grows);

        // Routes of a smaller net: the least that has a line.
        CityMap small = GatewayCheck.gatewayCity(types, 100, 10);
        check("a smaller city's routes (100 highways) are routes on its highways too",
                highwayLine(small) != null && !small.truckRoutes().isEmpty() && small.truckRoutes().size() == small.runs().highwayRamps().size() - 1);
        boolean ok = true;
        for (TruckSchedule.Route rt : small.truckRoutes()) ok &= offHighway(small.runs(), rt, 2) == 0 && rt.world();
        check("...every quarter plot on a highway plot", ok);
    }

    // ------------------------------------------------------------------ 3

    /** The rail plot in a yard's box nearest its middle: CityRuns.anchorInYard(), as the harness reads it. */
    static long[] anchorOf(CityRuns runs, long[] y) {
        double mx = y[0] + y[2] / 2.0, my = y[1] + y[3] / 2.0, bestD = Double.MAX_VALUE;
        long[] best = null;
        for (long py = y[1]; py < y[1] + y[3]; py++) {
            for (long px = y[0]; px < y[0] + y[2]; px++) {
                if (!CityRuns.covered(runs.rail(), px, py)) continue;
                double d = (px - mx) * (px - mx) + (py - my) * (py - my);
                if (d < bestD) { bestD = d; best = new long[] { px, py }; }
            }
        }
        return best;
    }

    static boolean sameWay(double[][] a, double[][] b) {
        return a == null ? b == null : b != null && Arrays.equals(a[0], b[0]) && Arrays.equals(a[1], b[1]);
    }

    static void railwayUnchanged(CityMap m) {
        System.out.println("\n--- the railway's way is wayOn() of the rail net from its main arm ---");
        CityRuns runs = m.runs();
        int main = runs.mainArm();
        List<TrainSchedule.Route> trains = m.trainRoutes();
        check("fixture: the railway has a main arm, yards and trains' routes", main >= 0 && !runs.yards().isEmpty() && !trains.isEmpty());
        boolean yardsSame = true, anchored = true, reached = false;
        double[][] firstWay = null;
        for (long[] y : runs.yards()) {
            long[] a = anchorOf(runs, y);
            anchored &= a != null;
            if (a == null) continue;
            double[][] via = runs.wayToPlot(a[0], a[1]);
            reached |= via != null;
            if (firstWay == null) firstWay = via;
            yardsSame &= sameWay(via, runs.wayOn(runs.rail(), main, a[0], a[1])) && sameWay(via, runs.wayTo(y));
        }
        check("every yard's anchor: wayToPlot(), wayTo() and wayOn(rail, mainArm()) give the one way", anchored && reached && yardsSame);
        List<long[]> sidings = runs.sidings();
        boolean sidingsSame = true;
        for (long[] s : sidings) sidingsSame &= sameWay(runs.wayToPlot(s[3], s[4]), runs.wayOn(runs.rail(), main, s[3], s[4]));
        System.out.printf("      %d yards, %d sidings%n", runs.yards().size(), sidings.size());
        check("...and every siding's end", sidingsSame);
        check("...and a way starts at the main arm's end, a plot's middle", firstWay != null
                && Math.abs(firstWay[0][0] - (runs.rail().arms.get(main).x + 0.5)) < EPS && Math.abs(firstWay[1][0] - (runs.rail().arms.get(main).y + 0.5)) < EPS);
        check("wayOn() of no arm, or of an arm the net has not, is no way", runs.wayOn(runs.rail(), -1, 0, 0) == null && runs.wayOn(runs.rail(), runs.rail().arms() + 3, 0, 0) == null);
        List<TrainSchedule.Route> before = m.trainRoutes();
        m.truckRoutes();
        m.accessRoutesIn(0, 0, World.SIDE, World.SIDE);
        check("asking for the trucks' routes leaves the trains' as they were: the same list", m.trainRoutes() == before);
    }

    // ------------------------------------------------------------------ 4

    /**
     * How many plots, reading every STEP along each leg of route r, are not a street the plan draws as an access road (or a boulevard's, which
     * keeps its role) in the Drawn dr whose frame starts at (ox, oy): the truth the painter reads, not the plan the route was traced from.
     */
    static int offAccess(TruckSchedule.Route r, CityMap.Drawn dr, long ox, long oy) {
        int off = 0;
        for (int i = 0; i + 1 < r.xs().length; i++) {
            double len = Math.hypot(r.xs()[i + 1] - r.xs()[i], r.ys()[i + 1] - r.ys()[i]);
            int n = (int) Math.ceil(len / STEP);
            for (int k = 0; k <= n; k++) {
                double t = n == 0 ? 0 : (double) k / n;
                long px = (long) Math.floor(r.xs()[i] + t * (r.xs()[i + 1] - r.xs()[i])) - ox, py = (long) Math.floor(r.ys()[i] + t * (r.ys()[i + 1] - r.ys()[i])) - oy;
                if (notAccess(dr, px, py)) off++;
            }
        }
        return off;
    }

    /** Whether frame plot (px, py) of the Drawn is not a street drawn as an access road's (or a boulevard's, which keeps its role). */
    static boolean notAccess(CityMap.Drawn dr, long px, long py) {
        if (px < 0 || py < 0 || px >= DistrictPlan.FRAME || py >= DistrictPlan.FRAME) return true;
        int code = dr.codeAt((int) px, (int) py) & 0xff, role = (code >> TilePainter.S_ROLE_SHIFT) & 3;
        return (code & TilePainter.S_KIND) == DistrictPlan.NONE || role != TilePainter.S_ACCESS && role != TilePainter.S_BOULEVARD;
    }

    /** The works of district d that stand where a road comes to: the yards and sea terminals the map gave its plan, and the Truck Depots among its buildings as the Drawn packs them, each {x, y, w, h} in the frame's plots. */
    static List<int[]> worksOf(CityMap m, CityMap.District d, CityMap.Drawn dr) {
        List<int[]> out = new java.util.ArrayList<>(m.planInputHere(d).works);
        for (int k = 0; k < CityMap.TILES; k++) {
            if (dr.boxes[k] == null) continue;
            for (int b : dr.boxes[k]) {
                int type = b >>> CityMap.BOX_TYPE_SHIFT;
                if (m.types()[type] == null || m.types()[type].category() != BuildingType.HAULAGE) continue;
                out.add(new int[] { (k % CityMap.TILES_A_SIDE) * World.TILE + (b & 31), (k / CityMap.TILES_A_SIDE) * World.TILE + ((b >> 5) & 31),
                        ((b >> 10) & 31) + 1, ((b >> 15) & 31) + 1 });
            }
        }
        return out;
    }

    static void accessRoutes(CityMap m) {
        System.out.println("\n--- the access roads: a route a road, from its end to its gate ---");
        long[] lo = { Long.MAX_VALUE, Long.MAX_VALUE }, hi = { Long.MIN_VALUE, Long.MIN_VALUE };
        for (CityMap.District d : m.districts()) {
            long[] o = origin(m, d);
            lo[0] = Math.min(lo[0], o[0]); lo[1] = Math.min(lo[1], o[1]);
            hi[0] = Math.max(hi[0], o[0] + CityMap.DISTRICT); hi[1] = Math.max(hi[1], o[1] + CityMap.DISTRICT);
        }
        List<CityMap.DistrictRoutes> none = m.accessRoutesIn(lo[0] - 10, lo[1] - 10, hi[0] + 10, hi[1] + 10);
        boolean drawnNone = true;
        for (CityMap.District d : m.districts()) drawnNone &= !m.planned(d);
        check("fixture: no plan is drawn yet, and asking over the whole city gives no route and draws none", none.isEmpty() && drawnNone);
        m.truckRoutes();
        drawnNone = true;
        for (CityMap.District d : m.districts()) drawnNone &= !m.planned(d);
        check("...truckRoutes() draws no plan either", drawnNone);

        int routes = 0, works = 0, served = 0, districtsWith = 0;
        boolean routesAre = true, endsOnStreet = true, endsAtGate = true, onRoad = true, corners = true, faded = true, notWorld = true, keyed = true, fourWay = true;
        for (CityMap.District d : m.districts()) m.drawn(d);
        List<CityMap.DistrictRoutes> all = m.accessRoutesIn(lo[0] - 10, lo[1] - 10, hi[0] + 10, hi[1] + 10);
        for (CityMap.District d : m.districts()) {
            CityMap.Drawn dr = m.drawn(d);
            long[] o = origin(m, d);
            CityMap.DistrictRoutes got = null;
            for (CityMap.DistrictRoutes dro : all) if (dro.key() == d.key) got = dro;
            int ways = dr.access == null ? 0 : dr.access.length;
            routesAre &= ways == (got == null ? 0 : got.routes().size());
            List<int[]> ws = worksOf(m, d, dr);
            works += ws.size();
            if (got == null) continue;
            districtsWith++;
            int[] gatesAt = new int[ws.size()];
            for (TruckSchedule.Route rt : got.routes()) {
                int n = rt.xs().length;
                routes++;
                notWorld &= !rt.world();
                faded &= Math.abs(rt.fadeTo() - CityMap.TRUCK_ACCESS_FADE) < EPS;
                // The first point is the road's end: a street the plan draws as an access road, a plot's middle; the last is its gate, beside a work's box.
                long ex = (long) Math.floor(rt.xs()[0]) - o[0], ey = (long) Math.floor(rt.ys()[0]) - o[1];
                endsOnStreet &= Math.abs(rt.xs()[0] - Math.floor(rt.xs()[0]) - 0.5) < EPS && Math.abs(rt.ys()[0] - Math.floor(rt.ys()[0]) - 0.5) < EPS
                        && ex >= 0 && ey >= 0 && ex < DistrictPlan.FRAME && ey < DistrictPlan.FRAME && !notAccess(dr, ex, ey);
                long gx = (long) Math.floor(rt.xs()[n - 1]) - o[0], gy = (long) Math.floor(rt.ys()[n - 1]) - o[1];
                boolean beside = false;
                for (int w = 0; w < ws.size(); w++) {
                    if (JunctionCheck.beside(ws.get(w), (int) gx, (int) gy)) { beside = true; gatesAt[w]++; }
                }
                endsAtGate &= beside && Math.abs(rt.xs()[n - 1] - Math.floor(rt.xs()[n - 1]) - 0.5) < EPS;
                onRoad &= offAccess(rt, dr, o[0], o[1]) == 0;
                // Only corners are kept: no point lies on the straight line between its neighbours; every leg runs along a row or a column.
                for (int k = 0; k + 1 < n; k++) {
                    fourWay &= rt.xs()[k] == rt.xs()[k + 1] || rt.ys()[k] == rt.ys()[k + 1];
                    if (k == 0) continue;
                    double ax = rt.xs()[k] - rt.xs()[k - 1], ay = rt.ys()[k] - rt.ys()[k - 1], bx = rt.xs()[k + 1] - rt.xs()[k], by = rt.ys()[k + 1] - rt.ys()[k];
                    corners &= ax * by - ay * bx != 0 || ax * bx + ay * by < 0;
                }
            }
            for (int w = 0; w < ws.size(); w++) {
                if (gatesAt[w] > 0) served++;
            }
            keyed &= got.key() == d.key;
        }
        System.out.printf("      %d routes in %d districts, for %d works the plans were given (%d with a gate)%n", routes, districtsWith, works, served);
        check("fixture: the plans laid access roads, in more than one district's frame", routes >= 2 && districtsWith >= 1);
        check("a drawn district's routes are as many as the roads its Drawn traced (none without)", routesAre && all.size() == districtsWith);
        check("...each a route of the street grid, not the world's", notWorld);
        check("...its first point is the road's end: an access street the plan draws, a plot's middle", endsOnStreet);
        check("...its last point is the road's gate, beside a work's box, a plot's middle", endsAtGate);
        check("...every quarter plot along it is a street the plan draws as an access road (or a boulevard's)", onRoad);
        check("...along rows and columns, a straight run kept to its two ends", fourWay && corners);
        check("...faded in over TRUCK_ACCESS_FADE", faded);
        check("...every work given to the plans has a road to its gate, every road a work", served == works && works > 0 && routes <= works);
        check("...under the district's own key", keyed);

        // The box.
        CityMap.DistrictRoutes first = all.get(0);
        TruckSchedule.Route rt0 = first.routes().get(0);
        long px = (long) Math.floor(rt0.xs()[0]), py = (long) Math.floor(rt0.ys()[0]);
        List<CityMap.DistrictRoutes> at = m.accessRoutesIn(px, py, px, py);
        check("a box of the one plot a road ends on meets that district's frame alone: its routes, under its key",
                at.size() == 1 && at.get(0).key() == first.key() && at.get(0).routes() == first.routes());
        check("a box off the city's ground meets none", m.accessRoutesIn(lo[0] - 5000, lo[1] - 5000, lo[0] - 4000, lo[1] - 4000).isEmpty()
                && m.accessRoutesIn(hi[0] + 1000, hi[1] + 1000, hi[0] + 2000, hi[1] + 2000).isEmpty());
        check("...and a box turned about (its far corner before its near) meets none", m.accessRoutesIn(px, py, px - 1, py - 1).isEmpty());
        List<CityMap.DistrictRoutes> again = m.accessRoutesIn(lo[0] - 10, lo[1] - 10, hi[0] + 10, hi[1] + 10);
        boolean sameLists = again.size() == all.size();
        for (int i = 0; sameLists && i < all.size(); i++) sameLists &= again.get(i).routes() == all.get(i).routes() && again.get(i).key() == all.get(i).key();
        check("asked again, with nothing changed: the same lists under the same keys", sameLists);
    }

    // ------------------------------------------------------------------ 5

    static void kept() {
        System.out.println("\n--- the routes are kept against the map's changes ---");
        BuildingVisual.Type[] types = types();
        int ch = GatewayCheck.highwayType(types), ct = GatewayCheck.trackType(types);
        CityMap m = GatewayCheck.gatewayCity(types, 1, 1);
        List<TruckSchedule.Route> first = m.truckRoutes();
        check("fixture: one highway reaches no street: no routes", first.isEmpty());
        check("asked again with nothing changed: the same list", m.truckRoutes() == first);
        long before = m.changes();
        long[] grown = m.totals().clone();
        grown[ch] = 300;
        grown[ct] = 10;
        boolean moved = m.layRuns(grown);
        check("a month's runs move changes()", moved && m.changes() > before);
        List<TruckSchedule.Route> second = m.truckRoutes();
        check("...and the routes are made again: a new list, now with routes", second != first && !second.isEmpty());
        check("asked again: the same list", m.truckRoutes() == second);
        CityMap fresh = GatewayCheck.gatewayCity(types, 300, 10);
        check("...equal, point for point, to a fresh map's of the same counts", same(second, fresh.truckRoutes()));
    }

    // ------------------------------------------------------------------ 6

    static void count(CityMap access) {
        System.out.println("\n--- the works that need an access road ---");
        CityRuns runs = access.runs();
        int depots = 0;
        long[] totals = access.totals();
        for (int t = 0; t < access.types().length; t++) {
            if (access.types()[t] != null && access.types()[t].category() == BuildingType.HAULAGE) depots += (int) totals[t];
        }
        int berthed = 0;
        for (CityShore.Work w : access.shore().works()) if (w.berthed()) berthed++;
        System.out.printf("      %d yards, %d sea terminals, %d Truck Depots; accessRoadCount() %d%n", runs.yards().size(), berthed, depots, access.accessRoadCount());
        check("fixture: the city has yards and Truck Depots", !runs.yards().isEmpty() && depots > 0);
        check("accessRoadCount() is the yards the runs draw, the shore's sea terminals and the Truck Depots",
                access.accessRoadCount() == runs.yards().size() + berthed + depots);
        int laid = 0;
        for (CityMap.District d : access.districts()) {
            CityMap.Drawn dr = access.drawn(d);
            if (dr != null && dr.access != null) laid += dr.access.length;
        }
        check("...of which the plans laid a road each: the roads drawn are the count", laid == access.accessRoadCount());
        CityMap bare = GatewayCheck.gatewayCity(types(), 1, 0);
        check("a city with none of them still counts 1: a share is never over zero", bare.accessRoadCount() == 1);
    }

    public static void main(String[] args) {
        noLine();
        highwayRoutes();
        kept();
        CityMap access = accessCity(types());
        railwayUnchanged(access);
        accessRoutes(access);
        count(access);
        System.out.println();
        System.out.println(fails == 0 ? "The trucks' routes are found." : fails + " FAILED");
        System.exit(fails);
    }
}
