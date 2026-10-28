package ham.citybuildersim;

import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Haulage;
import ham.citybuildersim.sectors.Mining;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Proves the trucks' routes (0.8.8, batch FR5, slice B;
 * docs/studies/spec-freight-and-infrastructure.md 4.7, D21): the ways the
 * map lets a truck drive, CityMap.truckRoutes() along the highways from their
 * line to the world and CityMap.accessRoutesIn() along each drawn district's
 * access roads - and, since FR9, the TRIPS they are joined into
 * (CityMap.tripsDrawn()): a truck's whole way from the world line to a
 * works' gate, and from one works' gate to another's. Not part of the game.
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
 *      TruckSchedule.WORLD_PLOTS out along the line's way, passes the line's point,
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
 *   7. a trip is whole (FR9), on the access city and on a trips city built to
 *      cause a road with a street leg as well as roads that end at a ramp:
 *      the city has trips, world and home, and a road of each kind; every
 *      world trip starts WORLD_PLOTS out along the highways' line, passes
 *      the line's point, and from the arm's end on every quarter plot lies on
 *      a highway plot or a street the plans draw, the highway part ends at a
 *      ramp, the rest keeps to streets, and its last point is a gate with its
 *      last leg on the access road; every home trip runs gate to gate on
 *      streets alone, never on a highway plot that carries none, never back
 *      on itself, from the gate of the nearest other road that comes to the
 *      same ramp; no sample of any trip lies where no road is drawn;
 *   8. only what is drawn: asking for trips plans nothing (the plans kept,
 *      the links and changes() as they were); before a district is drawn it
 *      has no trips and after it is drawn it has; a city whose highways have
 *      no world line has no trip; the list is kept while nothing changes (the
 *      same object) and made again once a plan or the runs change;
 *   9. a district's trips in its tonnes' share: perBuilding() and sharesOf()
 *      on numbers by hand (two districts holding a sector's buildings 3 : 1
 *      take 3 : 1 of its tonnes; a district of no works takes 0; the shares
 *      together are the share of the city's tonnes whose sectors stand on the
 *      map, 1 when all do); on a played city truckedBy() over every sector
 *      comes to Freight.truckedIn(), truckedOut() and truckedHome() to a
 *      billionth; a district's schedule draws its shares' loads and keeps the
 *      city's N; a home trip takes home loads alone; and the CATCHMENT
 *      (TruckSchedule.sharesByTrips()): a district's trips carry the tonnes
 *      of the districts about it too - the nearest district with trips is
 *      ridden within 2 districts, by hand a district with trips and no works
 *      takes its roadless neighbour's, one 3 districts from every district
 *      with trips gives nothing, a district between two goes to the nearer
 *      (then the smaller Euclid, then the smaller key), the shares of the
 *      districts with trips are those of the districts within reach, and on
 *      the trips city's layout a district whose trips' works are Truck Depots
 *      alone draws a share;
 *  10. the pace: Route.legSeconds() is the trains' rule at three lengths, any
 *      truck's whole visit is inside the month on a route of 10 plots and on
 *      one of 5,000, a truck is at its gate at its arrival and at its route's
 *      start a leg before, and frame() finds exactly the trucks position()
 *      puts in the box.
 *
 * They run in the order 1, 2, 5, 3, 4, 6, 8, 7, 9, 10: section 5, like 1 and
 * 2, plays on GatewayCheck's cities, 3, 4 and 6 share one access city built
 * after them, and 8 and 7 share a trips city built after that: 8 draws it a
 * district at a time and then 7 reads it whole (and the access city 4
 * drew), so 8's last part, which changes the city, comes after 7.
 */
public class TruckRouteCheck {

    static int fails = 0;

    /** How finely a leg is read, in plots: a quarter, as the world lines are (CityMap.WORLD_STEP). */
    static final double STEP = CityMap.WORLD_STEP;

    /** The tolerance of a position in plots, well below anything the map measures. */
    static final double EPS = 1e-9;

    /**
     * Section 7's trips city: MapCheck's access city with 12 Truck Depots more and 60 Elevated Highways in place of its 200. FR9 found the
     * access city itself (200 highways) lays every road to a ramp in its own frame, so no road has a street leg; with 60 the highways
     * leave some frames no ramp, a road ends on an arterial and its trucks drive a street leg to a ramp across the district's box.
     */
    static final int TRIPS_MORE_DEPOTS = 12, TRIPS_HIGHWAYS = 60;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-78s %s%n", label, ok ? "ok" : "FAIL");
    }

    /** Whether two routes are the same, point for point (a record holds arrays by reference, so equals() would not say). */
    static boolean same(TruckSchedule.Route a, TruckSchedule.Route b) {
        return a.world() == b.world() && a.home() == b.home() && Arrays.equals(a.xs(), b.xs()) && Arrays.equals(a.ys(), b.ys()) && Arrays.equals(a.along(), b.along())
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
        return accessCity(types, 0);
    }

    /** ...with `moreDepots` Truck Depots besides MapCheck's (section 7's trips city: a depot in a frame with no ramp ends its road on an arterial, and its trucks drive a street leg to a ramp). */
    static CityMap accessCity(BuildingVisual.Type[] types, int moreDepots) {
        return accessCity(types, moreDepots, MapCheck.ACCESS_HIGHWAYS_ADDED);
    }

    /** ...and `highways` Elevated Highways in place of MapCheck's. */
    static CityMap accessCity(BuildingVisual.Type[] types, int moreDepots, int highways) {
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
        c[hw] += highways;
        c[MapCheck.depotType(types)] += MapCheck.ACCESS_DEPOTS_ADDED + moreDepots;
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
        double fx = line.x() + Math.cos(r) * TruckSchedule.WORLD_PLOTS, fy = line.y() + Math.sin(r) * TruckSchedule.WORLD_PLOTS;
        boolean world = true, starts = true, passes = true, faded = true, onHighways = true, endsAtRamp = true, least = true, grows = true;
        java.util.Set<String> ends = new java.util.HashSet<>();
        boolean distinct = true;
        for (TruckSchedule.Route rt : routes) {
            int n = rt.xs().length;
            world &= rt.world();
            starts &= n >= 3 && Math.abs(rt.xs()[0] - fx) < EPS && Math.abs(rt.ys()[0] - fy) < EPS;
            passes &= n >= 3 && Math.abs(rt.xs()[1] - line.x()) < EPS && Math.abs(rt.ys()[1] - line.y()) < EPS;
            faded &= Math.abs(rt.fadeTo() - TruckSchedule.WORLD_PLOTS) < EPS && Math.abs(rt.along()[1] - rt.fadeTo()) < EPS;
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
        check("...starts TruckSchedule.WORLD_PLOTS out along the line's way from the line's point", starts);
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

    // ------------------------------------------------------------------ 7 to 10 (batch FR9)

    /** A test of one plot. */
    interface PlotTest { boolean at(long px, long py); }

    /** How many plots, reading every STEP along each leg of r from the leg that starts at point `from`, the test says yes to. */
    static int samplesWhere(TruckSchedule.Route r, int from, PlotTest test) {
        int n = 0;
        for (int i = from; i + 1 < r.xs().length; i++) {
            double len = Math.hypot(r.xs()[i + 1] - r.xs()[i], r.ys()[i + 1] - r.ys()[i]);
            int steps = (int) Math.ceil(len / STEP);
            for (int k = 0; k <= steps; k++) {
                double t = steps == 0 ? 0 : (double) k / steps;
                long px = (long) Math.floor(r.xs()[i] + t * (r.xs()[i + 1] - r.xs()[i])), py = (long) Math.floor(r.ys()[i] + t * (r.ys()[i + 1] - r.ys()[i]));
                if (test.at(px, py)) n++;
            }
        }
        return n;
    }

    /** Whether the plans draw a street (a code of any kind but none) on world plot (px, py): the truth the painter reads. */
    static boolean isStreet(CityMap m, long px, long py) {
        return (m.streetAt(px, py) & TilePainter.S_KIND) != DistrictPlan.NONE;
    }

    /** Whether the runs mark a ramp on the plot under (x, y). */
    static boolean isRamp(CityMap m, double x, double y) {
        byte[] fixed = new byte[1], marks = new byte[1];
        m.runs().fill((long) Math.floor(x), (long) Math.floor(y), 1, 1, fixed, marks, false);
        return (marks[0] & CityRuns.M_RAMP) != 0;
    }

    /** A plot's middle packed with its plot, for a map key. */
    static long pk(double x, double y) { return ((long) Math.floor(x) << 32) ^ ((long) Math.floor(y) & 0xffffffffL); }

    /** Every work of every district of the map, each {x, y, w, h} in world plots. */
    static List<int[]> worldWorks(CityMap m) {
        List<int[]> out = new ArrayList<>();
        for (CityMap.District d : m.districts()) {
            long[] o = origin(m, d);
            for (int[] w : worksOf(m, d, m.drawn(d))) out.add(new int[] { (int) (o[0] + w[0]), (int) (o[1] + w[1]), w[2], w[3] });
        }
        return out;
    }

    /** Whether (x, y) is a plot's middle beside the box of one of the works. */
    static boolean atGate(List<int[]> works, double x, double y) {
        if (Math.abs(x - Math.floor(x) - 0.5) > EPS || Math.abs(y - Math.floor(y) - 0.5) > EPS) return false;
        for (int[] w : works) if (JunctionCheck.beside(w, (int) Math.floor(x), (int) Math.floor(y))) return true;
        return false;
    }

    /** The index of the point a world trip leaves the highways at: the last ramp among the points the trip keeps on highway plots from the arm's end (point 2) on; -1 for none. */
    static int rampIndex(CityMap m, TruckSchedule.Route r) {
        CityRuns runs = m.runs();
        int n = r.xs().length, last = 2;
        TruckSchedule.Route leg;
        while (last + 1 < n) {
            leg = TruckSchedule.Route.through(true, new double[] { r.xs()[last], r.xs()[last + 1] }, new double[] { r.ys()[last], r.ys()[last + 1] }, 0);
            if (samplesWhere(leg, 0, (px, py) -> !CityRuns.covered(runs.highways(), px, py)) > 0) break;
            last++;
        }
        int ramp = -1;
        for (int i = 2; i <= last && i < n; i++) if (isRamp(m, r.xs()[i], r.ys()[i])) ramp = i;
        return ramp;
    }

    /** Whether a trip turns back on itself at a point: the leg out of it runs the way the leg into it came from. */
    static boolean turnsBack(TruckSchedule.Route r) {
        for (int i = 1; i + 1 < r.xs().length; i++) {
            double ax = r.xs()[i] - r.xs()[i - 1], ay = r.ys()[i] - r.ys()[i - 1], bx = r.xs()[i + 1] - r.xs()[i], by = r.ys()[i + 1] - r.ys()[i];
            if (Math.abs(ax * by - ay * bx) < EPS && ax * bx + ay * by < 0) return true;
        }
        return false;
    }

    /** Whether every trip of two lists is the same, district by district and point by point. */
    static boolean sameTrips(List<CityMap.DistrictTrips> a, List<CityMap.DistrictTrips> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) if (a.get(i).key() != b.get(i).key() || !same(a.get(i).trips(), b.get(i).trips())) return false;
        return true;
    }

    /** One road as the trips are checked against it: where it ends and the plot of the ramp its trip left the highways at. */
    record RoadAt(long endX, long endY, long ramp) { }

    /** What it costs a truck to enter world plot (px, py): 1 on an arterial, boulevard or access street, TRIP_STREET_COST on any other street, -1 where no street is drawn. */
    static int plotCost(CityMap m, long px, long py) {
        int code = m.streetAt(px, py) & 0xff, role = (code >> TilePainter.S_ROLE_SHIFT) & 3;
        if ((code & TilePainter.S_KIND) == DistrictPlan.NONE) return -1;
        return role == TilePainter.S_ARTERIAL || role == TilePainter.S_BOULEVARD || role == TilePainter.S_ACCESS ? 1 : CityMap.TRIP_STREET_COST;
    }

    /**
     * The cheapest way to world plot (ex, ey) over the streets of the box about a district (its frame, with origin o, grown by
     * CityMap.TRIP_REACH), from any ramp in the box that carries a street and has a way along the highways: a plain Dijkstra with a
     * priority queue, nothing of CityMap's search but the costs. -1 for none.
     */
    static int cheapestLeg(CityMap m, CityMap.WorldLine line, long[] o, long ex, long ey) {
        long bx0 = o[0] - CityMap.TRIP_REACH, by0 = o[1] - CityMap.TRIP_REACH;
        int bw = CityMap.TRIP_BOX;
        byte[] fixed = new byte[bw * bw], marks = new byte[bw * bw];
        m.runs().fill(bx0, by0, bw, bw, fixed, marks, false);
        Map<Long, Integer> dist = new HashMap<>();
        java.util.PriorityQueue<long[]> queue = new java.util.PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        for (int i = 0; i < bw * bw; i++) {
            if ((marks[i] & CityRuns.M_RAMP) == 0) continue;
            long x = bx0 + i % bw, y = by0 + i / bw;
            if (plotCost(m, x, y) < 0 || m.runs().highwayWayTo(line.arm(), x, y) == null) continue;
            dist.put(pk(x + 0.5, y + 0.5), 0);
            queue.add(new long[] { 0, x, y });
        }
        int[][] around = { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };
        while (!queue.isEmpty()) {
            long[] top = queue.poll();
            long key = pk(top[1] + 0.5, top[2] + 0.5);
            if (dist.getOrDefault(key, Integer.MAX_VALUE) < top[0]) continue;
            if (top[1] == ex && top[2] == ey) return (int) top[0];
            for (int[] a : around) {
                long x = top[1] + a[0], y = top[2] + a[1];
                if (x < bx0 || y < by0 || x >= bx0 + bw || y >= by0 + bw) continue;
                int c = plotCost(m, x, y);
                if (c < 0) continue;
                long next = top[0] + c;
                long nk = pk(x + 0.5, y + 0.5);
                if (next < dist.getOrDefault(nk, Integer.MAX_VALUE)) {
                    dist.put(nk, (int) next);
                    queue.add(new long[] { next, x, y });
                }
            }
        }
        return -1;
    }

    /**
     * Section 7 on one drawn city: every trip is whole. A world trip runs from the world line along the highways to a ramp, along the
     * streets and down its access road to a gate; a home trip from one gate to another by the streets. `mixed` is the city built to
     * cause a road with a street leg and a road that ends at a ramp both.
     */
    static void tripsWhole(String name, CityMap m, boolean mixed, String coldNote) {
        for (CityMap.District d : m.districts()) m.drawn(d);
        boolean current = m.districts().size() <= CityMap.PLANS_KEPT;
        for (CityMap.District d : m.districts()) current &= m.planned(d);
        check(name + ": fixture: every district's plan is drawn and current, none let go of", current);
        CityMap.WorldLine line = highwayLine(m);
        check("...and the highways have a world line", line != null);
        if (line == null) return;
        int searched = m.tripSearches();
        long t0 = System.nanoTime();
        List<CityMap.DistrictTrips> all = m.tripsDrawn();
        double ms = (System.nanoTime() - t0) / 1e6;
        String cold = coldNote != null ? coldNote : String.format("cold: made in %.0f ms with %d street searches", ms, m.tripSearches() - searched);
        int[] rc = m.tripRoadCounts();
        int world = 0, home = 0;
        for (CityMap.DistrictTrips dt : all) for (TruckSchedule.Route rt : dt.trips()) { if (rt.home()) home++; else world++; }
        System.out.printf("      %s: %d roads (%d end at a ramp, %d have a street leg, %d no ramp reached), %d world trips and %d home trips in %d districts;"
                + " %s, the search's scratch %,d bytes%n", name, rc[0], rc[1], rc[2], rc[3], world, home, all.size(), cold, m.tripScratchBytes());
        check("fixture: the city has trips, world ones and home ones", world > 0 && home > 0);
        check("...a road whose end is a ramp (its street leg empty)" + (mixed ? " and a road with a street leg" : ""), rc[1] > 0 && (!mixed || rc[2] > 0));
        check("...one world trip a road a ramp reached, no more home trips than such roads", world == rc[1] + rc[2] && home <= world);

        double rad = Math.toRadians(line.way());
        double fx = line.x() + Math.cos(rad) * TruckSchedule.WORLD_PLOTS, fy = line.y() + Math.sin(rad) * TruckSchedule.WORLD_PLOTS;
        CityRuns runs = m.runs();
        List<int[]> works = worldWorks(m);
        Map<Long, RoadAt> roads = new HashMap<>();
        boolean worldKind = true, starts = true, passes = true, faded = true, ramps = true, leaves = true, gateEnds = true, accessLast = true, endsKept = true;
        boolean fourWay = true, cheapest = true;
        int wholeSamples = 0, nowhere = 0, legsChecked = 0;
        for (CityMap.DistrictTrips dt : all) {
            CityMap.District d = dt.district();
            CityMap.Drawn dr = m.drawn(d);
            long[] o = origin(m, d);
            for (TruckSchedule.Route rt : dt.trips()) {
                if (rt.home()) continue;
                int n = rt.xs().length;
                worldKind &= rt.world() && !rt.home();
                starts &= n >= 4 && Math.abs(rt.xs()[0] - fx) < EPS && Math.abs(rt.ys()[0] - fy) < EPS;
                passes &= n >= 4 && Math.abs(rt.xs()[1] - line.x()) < EPS && Math.abs(rt.ys()[1] - line.y()) < EPS;
                faded &= Math.abs(rt.fadeTo() - TruckSchedule.WORLD_PLOTS) < EPS && n >= 2 && Math.abs(rt.along()[1] - rt.fadeTo()) < EPS;
                // From the arm's end on: every sample on a highway plot or a street the plans draw.
                int off = samplesWhere(rt, 2, (px, py) -> !CityRuns.covered(runs.highways(), px, py) && !isStreet(m, px, py));
                nowhere += off;
                wholeSamples += samplesWhere(rt, 2, (px, py) -> true);
                int ramp = rampIndex(m, rt);
                ramps &= ramp >= 2;
                // ...and from the ramp on, on streets alone: a highway plot carrying none is no road for a truck there.
                if (ramp >= 2) {
                    TruckSchedule.Route after = TruckSchedule.Route.through(true, java.util.Arrays.copyOfRange(rt.xs(), ramp, n), java.util.Arrays.copyOfRange(rt.ys(), ramp, n), 0);
                    leaves &= samplesWhere(after, 0, (px, py) -> !isStreet(m, px, py)) == 0;
                }
                double gx = rt.xs()[n - 1], gy = rt.ys()[n - 1];
                gateEnds &= atGate(works, gx, gy);
                // The last leg runs down the access road: access plots of the district's own plan.
                accessLast &= n >= 2 && samplesWhere(rt, n - 2, (px, py) -> notAccess(dr, px - o[0], py - o[1])) == 0;
                // The road: the way (Drawn.access) whose gate is the trip's last point, its end a point of the trip.
                RoadAt road = null;
                if (dr.access != null) {
                    for (int[] way : dr.access) {
                        int f = DistrictPlan.FRAME, g = way[way.length - 1], e = way[0];
                        if (way.length < 2 || Math.abs(o[0] + g % f + 0.5 - gx) > EPS || Math.abs(o[1] + g / f + 0.5 - gy) > EPS) continue;
                        double ex = o[0] + e % f + 0.5, ey = o[1] + e / f + 0.5;
                        int at = -1;
                        for (int i = ramp < 0 ? 0 : ramp; i < n && at < 0; i++) if (Math.abs(rt.xs()[i] - ex) < EPS && Math.abs(rt.ys()[i] - ey) < EPS) at = i;
                        endsKept &= at >= 0;
                        if (ramp >= 2) road = new RoadAt((long) Math.floor(ex), (long) Math.floor(ey), pk(rt.xs()[ramp], rt.ys()[ramp]));
                        if (ramp >= 2 && at >= ramp) {
                            // The street leg, from the ramp to the road's end: no dearer than a plain search of the same streets finds.
                            int cost = 0;
                            for (int i = ramp; i < at; i++) {
                                long px = (long) Math.floor(rt.xs()[i]), py = (long) Math.floor(rt.ys()[i]);
                                long qx = (long) Math.floor(rt.xs()[i + 1]), qy = (long) Math.floor(rt.ys()[i + 1]);
                                for (int k = 1; k <= Math.max(Math.abs(qx - px), Math.abs(qy - py)); k++) {
                                    cost += plotCost(m, px + Long.signum(qx - px) * k, py + Long.signum(qy - py) * k);
                                }
                            }
                            if (at > ramp) {
                                legsChecked++;
                                cheapest &= cost == cheapestLeg(m, line, o, (long) Math.floor(ex), (long) Math.floor(ey));
                            }
                        }
                        // From the ramp on, along rows and columns: a street leg and an access road are plots side by side.
                        if (ramp >= 2) for (int i = ramp; i + 1 < n; i++) fourWay &= rt.xs()[i] == rt.xs()[i + 1] || rt.ys()[i] == rt.ys()[i + 1];
                    }
                }
                endsKept &= road != null;
                if (road != null) roads.put(pk(gx, gy), road);
            }
        }
        System.out.printf("      the world trips read %,d samples from the arm's end on, %d of them where no road is drawn%n", wholeSamples, nowhere);
        check("every world trip is a world trip (imports and exports), not a home trip", worldKind && world > 0);
        check("...starts TruckSchedule.WORLD_PLOTS out along the line's way and passes the line's point, faded in over that world part", starts && passes && faded);
        check("...from the arm's end on every quarter plot lies on a highway plot or a street the plans draw: none where no road is drawn", nowhere == 0 && wholeSamples > 0);
        check("...the highway part ends at a ramp the runs mark, and from there on it keeps to streets the plans draw", ramps && leaves);
        check("...its last point is a gate, beside the box of a work, a plot's middle", gateEnds);
        check("...its last leg runs down the access road: every quarter plot an access street of the district's plan", accessLast);
        check("...it passes the end of the access road whose gate it reaches, a point of the trip", endsKept && roads.size() == world);
        check("...from the ramp on it runs along rows and columns: a street leg is plots side by side", fourWay);
        check("...a street leg costs what a plain search of the same streets finds cheapest, from any ramp in the district's box (" + legsChecked
                + " legs; 1 an arterial, boulevard or access plot, TRIP_STREET_COST any other)", cheapest && (!mixed || legsChecked > 0));

        // The home trips: from a partner's gate by the streets to this road's gate.
        boolean homeKind = true, fade = true, gates = true, differ = true, streets = true, noHighwayOnly = true, noTurnBack = true, partnered = true;
        int homeSamples = 0;
        for (CityMap.DistrictTrips dt : all) {
            for (TruckSchedule.Route rt : dt.trips()) {
                if (!rt.home()) continue;
                int n = rt.xs().length;
                homeKind &= !rt.world() && rt.home();
                fade &= Math.abs(rt.fadeTo() - CityMap.TRUCK_ACCESS_FADE) < EPS;
                gates &= atGate(works, rt.xs()[0], rt.ys()[0]) && atGate(works, rt.xs()[n - 1], rt.ys()[n - 1]);
                differ &= pk(rt.xs()[0], rt.ys()[0]) != pk(rt.xs()[n - 1], rt.ys()[n - 1]);
                streets &= samplesWhere(rt, 0, (px, py) -> !isStreet(m, px, py)) == 0;
                noHighwayOnly &= samplesWhere(rt, 0, (px, py) -> CityRuns.covered(runs.highways(), px, py) && !isStreet(m, px, py)) == 0;
                homeSamples += samplesWhere(rt, 0, (px, py) -> true);
                noTurnBack &= !turnsBack(rt);
                // The partner: the road whose gate it leaves from comes to the same ramp as this road, and is the nearest such road, end to end.
                RoadAt a = roads.get(pk(rt.xs()[n - 1], rt.ys()[n - 1])), b = roads.get(pk(rt.xs()[0], rt.ys()[0]));
                boolean ok = a != null && b != null && a != b && a.ramp() == b.ramp();
                if (ok) {
                    double nearest = Math.hypot(a.endX() - b.endX(), a.endY() - b.endY());
                    for (RoadAt c : roads.values()) {
                        if (c == a || c.ramp() != a.ramp()) continue;
                        ok &= nearest <= Math.hypot(a.endX() - c.endX(), a.endY() - c.endY()) + EPS;
                    }
                }
                partnered &= ok;
            }
        }
        System.out.printf("      the home trips read %,d samples%n", homeSamples);
        check("every home trip is a home trip: it takes the tonnes bought at home alone, and is faded in over TRUCK_ACCESS_FADE", homeKind && fade && home > 0);
        check("...runs from one gate to another's, beside the boxes of two works", gates && differ);
        check("...every quarter plot of it is a street the plans draw, none a highway plot that carries none", streets && noHighwayOnly && homeSamples > 0);
        check("...the part the two roads' legs share is cut off: it never turns back on itself", noTurnBack);
        check("...leaves the gate of the nearest other road that comes to the same ramp", partnered);
        warmCost(name, m);
    }

    /** Prints what making a city's trips again costs, warm: five remakes of everything (the legs and the list forgotten), the best and the median of a whole tripsDrawn() and of one district's search. No assertion. */
    static void warmCost(String name, CityMap m) {
        int runs = 5, searches = 0;
        double[] whole = new double[runs], each = new double[runs];
        for (int r = 0; r < runs; r++) {
            m.forgetTrips();
            int before = m.tripSearches();
            long t0 = System.nanoTime();
            m.tripsDrawn();
            whole[r] = (System.nanoTime() - t0) / 1e6;
            searches = m.tripSearches() - before;
            each[r] = searches > 0 ? whole[r] / searches : 0;
        }
        Arrays.sort(whole);
        Arrays.sort(each);
        System.out.printf("      %s, warm: %d remakes of %d searches each: a whole tripsDrawn() best %.1f ms, median %.1f ms; one district's search best %.1f ms, median %.1f ms%n",
                name, runs, searches, whole[0], whole[runs / 2], each[0], each[runs / 2]);
    }

    // ------------------------------------------------------------------ 8

    /** Whether district d has trips in the list. */
    static boolean hasTrips(List<CityMap.DistrictTrips> trips, CityMap.District d) {
        for (CityMap.DistrictTrips dt : trips) if (dt.district() == d) return true;
        return false;
    }

    /** Section 8's first half, on a city nothing of which is drawn: trips plan nothing, and a district drawn later gets its trips then. Returns what making them cost as the districts were drawn. */
    static String tripsOnlyDrawn(CityMap m) {
        System.out.println("\n--- 8. only what is drawn: the trips plan nothing, and a district drawn later has its trips then ---");
        boolean unplanned = true;
        for (CityMap.District d : m.districts()) unplanned &= !m.planned(d);
        long bytes = m.plansBytes(), changes = m.changes();
        int links = m.linksKept();
        List<CityMap.DistrictTrips> none = m.tripsDrawn();
        boolean still = true;
        for (CityMap.District d : m.districts()) still &= !m.planned(d);
        check("fixture: no plan is drawn yet, and asking for the trips gives none and draws none (the plans kept, the links and changes() as they were)",
                unplanned && none.isEmpty() && still && m.plansBytes() == bytes && m.linksKept() == links && m.changes() == changes);
        boolean subset = true, plansNothing = true, notBefore = true;
        int became = 0, steps = 0, searches0 = m.tripSearches();
        double askedMs = 0;
        for (CityMap.District d : m.districts()) {
            boolean was = hasTrips(m.tripsDrawn(), d), drawnBefore = m.planned(d);
            m.drawn(d);
            long bytesNow = m.plansBytes();
            int linksNow = m.linksKept();
            long q0 = System.nanoTime();
            List<CityMap.DistrictTrips> got = m.tripsDrawn();
            askedMs += (System.nanoTime() - q0) / 1e6;
            plansNothing &= m.plansBytes() == bytesNow && m.linksKept() == linksNow;
            // A district's trips come from a plan that is drawn: every district with trips has its plan drawn and current.
            for (CityMap.DistrictTrips dt : got) subset &= m.planned(dt.district());
            if (!drawnBefore) notBefore &= !was;
            if (!was && hasTrips(got, d)) became++;
            steps++;
        }
        check("asking after each of " + steps + " districts is drawn plans nothing: the plans kept and the links are as they were", plansNothing);
        check("...every district with trips has its plan drawn and current", subset);
        check("...a district not drawn has no trips before it is, and one that had none has them once it is (" + became + " did)", notBefore && became > 0);
        return String.format("cold: %d asks, one as each district was drawn, took %.0f ms in all and made %d street searches", steps, askedMs, m.tripSearches() - searches0);
    }

    /** Section 8's second half, on a city drawn whole: the list is kept while nothing changes and made again once a plan or the runs change; no world line, no world trip. */
    static void tripsKept(CityMap m, BuildingVisual.Type[] types) {
        System.out.println("\n--- 8 (the rest). the trips are kept against the changes and the plans kept; no world line, no trip ---");
        List<CityMap.DistrictTrips> first = m.tripsDrawn();
        check("asked again with nothing changed: the same list", !first.isEmpty() && m.tripsDrawn() == first);
        check("...and each district's trips are the same list under the same key", m.tripsDrawn().get(0).trips() == first.get(0).trips() && m.tripsDrawn().get(0).key() == first.get(0).key());
        // The plans let go and drawn again before the trips are asked: the same plans, so the street legs are kept, not searched again.
        int searches = m.tripSearches();
        m.forgetPainted();
        for (CityMap.District d : m.districts()) m.drawn(d);
        List<CityMap.DistrictTrips> kept = m.tripsDrawn();
        check("the plans let go and drawn again, then asked: a new list, equal to the first, and no street search made again (the legs kept against the plans' stamps)",
                kept != first && sameTrips(first, kept) && m.tripSearches() == searches);
        m.forgetPainted();
        List<CityMap.DistrictTrips> gone = m.tripsDrawn();
        check("the plans forgotten: the trips are made again, a new list, and a district whose plan is not drawn takes none", gone != first && gone.isEmpty());
        for (CityMap.District d : m.districts()) m.drawn(d);
        List<CityMap.DistrictTrips> back = m.tripsDrawn();
        check("...drawn again they are made again once more, a new list, equal point for point to the first, the street legs searched again (let go with their plans)",
                back != gone && sameTrips(first, back) && m.tripSearches() > searches);
        int hw = GatewayCheck.highwayType(types);
        long before = m.changes();
        long[] grown = m.totals().clone();
        grown[hw] += 100;
        boolean moved = m.layRuns(grown);
        List<CityMap.DistrictTrips> afterRuns = m.tripsDrawn();
        check("a month's runs move changes() and the trips are made again: a new list", moved && m.changes() > before && afterRuns != back);
        for (CityMap.District d : m.districts()) m.drawn(d);
        List<CityMap.DistrictTrips> redrawn = m.tripsDrawn();
        check("...the plans drawn again, the trips are made again and the city has them", redrawn != afterRuns && !redrawn.isEmpty() && m.tripsDrawn() == redrawn);

        // No world line, no world trip: highways that reach a street and find no way out, and works with roads beside them.
        int depot = MapCheck.depotType(types);
        CityMap five = GatewayCheck.gatewayCity(types, 5, 0);
        long[] c = five.totals().clone();
        c[depot] += 6;
        five.reconcile(c);
        int roads = 0;
        for (CityMap.District d : five.districts()) { CityMap.Drawn dr = five.drawn(d); if (dr.access != null) roads += dr.access.length; }
        check("fixture: five highways reach a street and find no way out (no world line), and six Truck Depots have " + roads + " access roads drawn",
                five.runs().highwaysReach() && !hasHighwayLine(five) && roads > 0);
        check("...so there is no world trip, and no trip at all: a truck comes in from the world line or not at all", five.tripsDrawn().isEmpty() && five.truckRoutes().isEmpty());
    }

    // ------------------------------------------------------------------ 9

    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static void shares(BuildingVisual.Type[] types, CityMap trips) {
        System.out.println("\n--- 9. a district's trips in its tonnes' share ---");
        // By hand: two sectors with buildings, a type of no sector.
        int a = -1, b = -1, none = -1;
        String sa = null, sb = null;
        for (int t = 0; t < types.length; t++) {
            if (types[t] == null) continue;
            String s = types[t].sector();
            if (s == null || s.isEmpty()) { if (none < 0) none = t; }
            else if (sa == null) { sa = s; a = t; }
            else if (sb == null && !s.equals(sa)) { sb = s; b = t; }
        }
        check("fixture: two sectors with a building type each, and a type of no sector", a >= 0 && b >= 0 && none >= 0 && !sa.equals(sb));
        final String keyA = sa, keyB = sb;
        java.util.function.Function<String, double[]> tonnes = s -> s.equals(keyA) ? new double[] { 100, 40, 8 } : s.equals(keyB) ? new double[] { 50, 10, 6 } : new double[3];
        long[] totals = new long[types.length];
        totals[a] = 4;
        totals[b] = 2;
        totals[none] = 7;
        double[][] per = TruckSchedule.perBuilding(tonnes, types, totals);
        double[] city = { 150, 50, 14 };
        check("one standing building of a sector's type carries the sector's truck tonnes over its buildings standing: 100, 40 and 8 over 4",
                near(per[a][0], 25) && near(per[a][1], 10) && near(per[a][2], 2));
        check("...a type of no sector carries nothing", per[none][0] == 0 && per[none][1] == 0 && per[none][2] == 0);
        CityMap.District d1 = new CityMap.District(0, 0, types.length, 0), d2 = new CityMap.District(1, 0, types.length, 1);
        CityMap.District d3 = new CityMap.District(2, 0, types.length, 2), d4 = new CityMap.District(3, 0, types.length, 3);
        d1.counts[a] = 3;
        d2.counts[a] = 1;
        d3.counts[none] = 7;
        d4.counts[b] = 2;
        double[] s1 = TruckSchedule.sharesOf(per, d1, city), s2 = TruckSchedule.sharesOf(per, d2, city), s3 = TruckSchedule.sharesOf(per, d3, city), s4 = TruckSchedule.sharesOf(per, d4, city);
        check("two districts holding one sector's buildings 3 : 1 take its tonnes 3 : 1, a share of the city's by load",
                near(s1[0], 0.5) && near(s1[1], 0.6) && near(s1[2], 6.0 / 14) && near(s1[0], 3 * s2[0]) && near(s1[1], 3 * s2[1]) && near(s1[2], 3 * s2[2]));
        check("...a district of no works takes nothing", s3[0] == 0 && s3[1] == 0 && s3[2] == 0);
        double[] total = new double[3];
        for (double[] s : new double[][] { s1, s2, s3, s4 }) for (int l = 0; l < 3; l++) total[l] += s[l];
        check("...and every district's share together is 1 of each load, where every sector's buildings stand", near(total[0], 1) && near(total[1], 1) && near(total[2], 1));
        totals[b] = 0;
        d4.counts[b] = 0;
        double[][] perMissing = TruckSchedule.perBuilding(tonnes, types, totals);
        double[] m1 = TruckSchedule.sharesOf(perMissing, d1, city), m2 = TruckSchedule.sharesOf(perMissing, d2, city);
        check("a sector with no building standing carries nothing, and the rest come to the share of the city's tonnes whose sectors stand",
                perMissing[b][0] == 0 && near(m1[0] + m2[0], 100.0 / 150) && near(m1[1] + m2[1], 40.0 / 50) && near(m1[2] + m2[2], 8.0 / 14));
        double[] small = TruckSchedule.sharesOf(per, d1, new double[] { 1, 1, 1 }), zero = TruckSchedule.sharesOf(per, d1, new double[3]);
        check("a share is at most 1 where the city's tonnes are fewer, and 0 where they are 0", small[0] == 1 && small[1] == 1 && small[2] == 1 && zero[0] == 0 && zero[1] == 0 && zero[2] == 0);

        // On a played city, the model's own figure.
        Game city2 = HaulageCheck.base(GameFiles.scratch("truckroutecheck-shares"));
        Freight fr = city2.getSectors().freight();
        Haulage h = fr.haulage();
        int guard = 0;
        while (!(h.getBill() > 0) && guard++ < 16) TruckCheck.quietly(city2::toggleNextMonth);
        check("fixture: the city has played until Haulage has a fleet and a bill, so it carries tonnes", h.getBill() > 0 && h.getTonnes() > 0 && h.getCarried() > 0);
        boolean sums = true, played = true;
        double seen = 0;
        for (int month = 0; month < 3; month++) {
            TruckCheck.quietly(() -> HaulageCheck.haul(city2));
            double[] by = new double[3];
            for (Sector s : city2.getSectors().all()) {
                double[] one = TruckSchedule.truckedBy(fr, s.key());
                for (int l = 0; l < 3; l++) by[l] += one[l];
            }
            sums &= near(by[0], fr.truckedIn()) && near(by[1], fr.truckedOut()) && near(by[2], fr.truckedHome());
            seen += by[0] + by[1] + by[2];
            TruckCheck.quietly(city2::toggleNextMonth);
            by = new double[3];
            for (Sector s : city2.getSectors().all()) {
                double[] one = TruckSchedule.truckedBy(fr, s.key());
                for (int l = 0; l < 3; l++) by[l] += one[l];
            }
            played &= near(by[0], fr.truckedIn()) && near(by[1], fr.truckedOut()) && near(by[2], fr.truckedHome());
        }
        check("truckedBy() over every sector comes to Freight.truckedIn(), truckedOut() and truckedHome() to a billionth, six hauls running", sums && seen > 0);
        check("...and so it does in the months the game played through itself", played);
        check("a sector that moved nothing, no freight and no sector carry nothing",
                TruckSchedule.truckedBy(fr, "nothing here")[0] == 0 && TruckSchedule.truckedBy(null, keyA)[1] == 0 && TruckSchedule.truckedBy(fr, null)[2] == 0);

        // The trips city's districts over the played city's tonnes: the shares come to what its sectors carry.
        double[] cityTonnes = { fr.truckedIn(), fr.truckedOut(), fr.truckedHome() };
        long[] mapTotals = trips.totals();
        double[][] perMap = TruckSchedule.perBuilding(s -> TruckSchedule.truckedBy(fr, s), trips.types(), mapTotals);
        double[] sum = new double[3], expect = new double[3];
        boolean unit = true;
        for (CityMap.District d : trips.districts()) {
            double[] sh = TruckSchedule.sharesOf(perMap, d, cityTonnes);
            for (int l = 0; l < 3; l++) { sum[l] += sh[l]; unit &= sh[l] >= 0 && sh[l] <= 1; }
        }
        java.util.Set<String> standing = new java.util.HashSet<>();
        for (int t = 0; t < trips.types().length; t++) if (mapTotals[t] > 0 && trips.types()[t] != null && !trips.types()[t].sector().isEmpty()) standing.add(trips.types()[t].sector());
        for (String s : standing) { double[] one = TruckSchedule.truckedBy(fr, s); for (int l = 0; l < 3; l++) expect[l] += one[l] / Math.max(1e-300, cityTonnes[l]); }
        check("the districts of a map, over a played city's tonnes, each take a share in [0, 1] and together the share of the city's tonnes whose sectors stand on the map",
                unit && near(sum[0], expect[0]) && near(sum[1], expect[1]) && near(sum[2], expect[2]) && sum[0] + sum[1] + sum[2] > 0);

        // A district's schedule draws its shares' loads and keeps the city's N.
        TruckSchedule.Route world = TruckSchedule.Route.through(true, new double[] { -64, 0, 100 }, new double[] { 0, 0, 0 }, 64);
        TruckSchedule.Route homeTrip = TruckSchedule.Route.home(new double[] { 10, 60, 60 }, new double[] { 10, 10, 40 }, CityMap.TRUCK_ACCESS_FADE);
        List<TruckSchedule.Route> routes = List.of(world, homeTrip);
        double[] share = { 0.5, 0.25, 0.125 };
        TruckSchedule whole = TruckSchedule.of(7, fr, routes, 1.0, 5), part = TruckSchedule.of(7, fr, routes, share, 5);
        check("fixture: the played city imported, exported and bought at home, so no share is vacuous",
                whole.loads(TruckSchedule.Load.IMPORT) > 0 && whole.loads(TruckSchedule.Load.EXPORT) > 0 && whole.loads(TruckSchedule.Load.HOME) > 0);
        boolean loadsOk = true;
        for (TruckSchedule.Load l : TruckSchedule.Load.values()) loadsOk &= near(part.loads(l), whole.loads(l) * share[l.ordinal()]);
        check("a district's schedule draws its shares' loads: the city's of each load times its share", loadsOk);
        check("...and keeps the city's N, the legend's: stands() is what the whole city's is", part.stands() == whole.stands());
        double sumLoads = part.loads(TruckSchedule.Load.IMPORT) + part.loads(TruckSchedule.Load.EXPORT) + part.loads(TruckSchedule.Load.HOME);
        check("...its trucks, each for N loads, come to its loads within one a load group", Math.abs((double) part.callCount() * part.stands() - sumLoads) <= 3.0 * part.stands());
        TruckSchedule nothing = TruckSchedule.of(7, fr, routes, new double[3], 5);
        check("a district of no works (every share 0) draws no truck and keeps the city's N", nothing.callCount() == 0 && nothing.stands() == whole.stands());
        TruckSchedule noHome = TruckSchedule.of(7, fr, routes, new double[] { 1, 1, 0 }, 5);
        check("a share of 0 for the tonnes bought at home draws none of those, the imports and exports as they were",
                noHome.callsOf(TruckSchedule.Load.HOME) == 0 && noHome.callsOf(TruckSchedule.Load.IMPORT) == whole.callsOf(TruckSchedule.Load.IMPORT));
        check("a scalar share is the same share for every load", TruckSchedule.of(7, fr, routes, 0.5, 5).calls().equals(TruckSchedule.of(7, fr, routes, new double[] { 0.5, 0.5, 0.5 }, 5).calls()));

        // A home trip takes home loads alone, and they go to no other route.
        double[] quiet = new double[3];
        quiet[0] = 300; quiet[1] = 200; quiet[2] = 400;
        TruckSchedule both = TruckSchedule.of(3, quiet, routes, 1.0, 0), homeOnly = TruckSchedule.of(3, quiet, List.of(homeTrip), 1.0, 0), worldOnly = TruckSchedule.of(3, quiet, List.of(world), 1.0, 0);
        boolean homeOnHome = true;
        for (TruckSchedule.Call c : both.calls()) if (c.load() == TruckSchedule.Load.HOME) homeOnHome &= routes.get(c.route()).home();
        check("a home trip is a home trip and a world trip is not: Route.home() and Route.through(true, ...)", homeTrip.home() && !homeTrip.world() && world.world() && !world.home());
        check("a home trip takes the tonnes bought at home alone: a schedule of one draws no import and no export", homeOnly.callsOf(TruckSchedule.Load.HOME) > 0
                && homeOnly.callsOf(TruckSchedule.Load.IMPORT) == 0 && homeOnly.callsOf(TruckSchedule.Load.EXPORT) == 0);
        check("...and a world trip takes no home load, so every home load of a mix is on the home trip", worldOnly.callsOf(TruckSchedule.Load.HOME) == 0 && homeOnHome && both.callsOf(TruckSchedule.Load.HOME) > 0);

        // The catchment: whose tonnes a district's trips carry.
        catchment(types, trips, per, city, a, b);
    }

    // ------------------------------------------------------------------ 9, the catchment (FR9 slice 2)

    /** A district by hand at (dx, dy) holding `count` buildings of each type given as type, count pairs. */
    static CityMap.District hand(BuildingVisual.Type[] types, int dx, int dy, int... typeAndCount) {
        CityMap.District d = new CityMap.District(dx, dy, types.length, 0);
        for (int i = 0; i + 1 < typeAndCount.length; i += 2) d.counts[typeAndCount[i]] = typeAndCount[i + 1];
        return d;
    }

    /** A district's trips, by hand: the trips themselves are nothing to the catchment. */
    static CityMap.DistrictTrips tripsOf(CityMap.District d) {
        return new CityMap.DistrictTrips(d.key, d, List.of());
    }

    /** A lookup of districts by (dx, dy), as CityMap.district() gives. */
    static java.util.function.BiFunction<Integer, Integer, CityMap.District> gridOf(java.util.Collection<CityMap.District> ds) {
        Map<Long, CityMap.District> by = new HashMap<>();
        for (CityMap.District d : ds) by.put(d.key, d);
        return (x, y) -> by.get(CityMap.key(x, y));
    }

    static boolean same3(double[] x, double[] y) {
        return near(x[0], y[0]) && near(x[1], y[1]) && near(x[2], y[2]);
    }

    /** The district with trips that district t rides, by the rule written out again: the nearest within CATCHMENT_DISTRICTS, Chebyshev, then squared Euclidean, then key; null for none. */
    static CityMap.DistrictTrips winnerOf(List<CityMap.DistrictTrips> trips, CityMap.District t) {
        CityMap.DistrictTrips best = null;
        long[] bestAt = null;
        for (CityMap.DistrictTrips e : trips) {
            long dx = e.district().dx - t.dx, dy = e.district().dy - t.dy, cheb = Math.max(Math.abs(dx), Math.abs(dy));
            if (cheb > TruckSchedule.CATCHMENT_DISTRICTS) continue;
            long[] at = { cheb, dx * dx + dy * dy, e.key() };
            boolean nearer = best == null;
            for (int k = 0; !nearer && k < 3; k++) if (at[k] != bestAt[k]) { nearer = at[k] < bestAt[k]; break; }
            if (nearer) { best = e; bestAt = at; }
        }
        return best;
    }

    /** The shares of the districts that ride `e`'s trips, summed. */
    static double[] ridersOf(List<CityMap.DistrictTrips> trips, java.util.Collection<CityMap.District> all, CityMap.DistrictTrips e, double[][] per, double[] city) {
        double[] sum = new double[3];
        for (CityMap.District t : all) {
            CityMap.DistrictTrips w = winnerOf(trips, t);
            if (w == null || w.key() != e.key()) continue;
            double[] s = TruckSchedule.sharesOf(per, t, city);
            for (int l = 0; l < 3; l++) sum[l] += s[l];
        }
        return sum;
    }

    /** Section 9's catchment: a district's trips carry the tonnes of the works about it, not its own alone. */
    static void catchment(BuildingVisual.Type[] types, CityMap trips, double[][] per, double[] city, int a, int b) {
        System.out.println("      the catchment (FR9, slice 2): a works district rides the trips of the nearest district that has any");
        check("fixture: the catchment is how far a work looks for a ramp, 2 districts", TruckSchedule.CATCHMENT_DISTRICTS == DistrictPlan.ACCESS_RAMP_DISTRICTS && TruckSchedule.CATCHMENT_DISTRICTS == 2);
        // (a) A district with trips and no works beside a works district with no trips.
        CityMap.District e = hand(types, 0, 0), w = hand(types, 1, 0, a, 3);
        Map<Long, double[]> got = TruckSchedule.sharesByTrips(List.of(tripsOf(e)), gridOf(List.of(e, w)), per, city);
        double[] wShare = TruckSchedule.sharesOf(per, w, city);
        check("a district with trips and no works takes the share of the works district beside it that has no trips",
                got.size() == 1 && same3(got.get(e.key), wShare) && wShare[0] > 0 && wShare[1] > 0 && wShare[2] > 0 && TruckSchedule.sharesOf(per, e, city)[0] == 0);
        // (b) The reach: 2 districts gives, 3 does not.
        CityMap.District own = hand(types, 0, 0, b, 2), far = hand(types, 6, 0), two = hand(types, 2, 0, a, 1), three = hand(types, 3, 0, a, 4);
        got = TruckSchedule.sharesByTrips(List.of(tripsOf(own), tripsOf(far)), gridOf(List.of(own, far, two, three)), per, city);
        double[] ownPlusTwo = new double[3], ownS = TruckSchedule.sharesOf(per, own, city), twoS = TruckSchedule.sharesOf(per, two, city);
        for (int l = 0; l < 3; l++) ownPlusTwo[l] = ownS[l] + twoS[l];
        double[] threeS = TruckSchedule.sharesOf(per, three, city);
        check("a works district 2 districts from a district with trips gives it its share, and one 3 districts from every district with trips gives nothing to any",
                same3(got.get(own.key), ownPlusTwo) && got.get(far.key)[0] == 0 && got.get(far.key)[1] == 0 && got.get(far.key)[2] == 0 && threeS[0] > 0 && twoS[0] > 0);
        // (c) Between two districts with trips: the nearer; equally near by Chebyshev, the smaller Euclidean; then the smaller key.
        CityMap.District e1 = hand(types, 0, 0), e2 = hand(types, 3, 0), near1 = hand(types, 1, 0, a, 3), near2 = hand(types, 2, 0, b, 2);
        got = TruckSchedule.sharesByTrips(List.of(tripsOf(e1), tripsOf(e2)), gridOf(List.of(e1, e2, near1, near2)), per, city);
        check("a district between two districts with trips goes to the nearer: 1 district from one and 2 from the other, and the other way about",
                same3(got.get(e1.key), TruckSchedule.sharesOf(per, near1, city)) && same3(got.get(e2.key), TruckSchedule.sharesOf(per, near2, city)));
        CityMap.District between = hand(types, 2, -1, a, 2), pa = hand(types, 0, 0), pb = hand(types, 2, 1);
        got = TruckSchedule.sharesByTrips(List.of(tripsOf(pa), tripsOf(pb)), gridOf(List.of(pa, pb, between)), per, city);
        check("...2 districts from both (Chebyshev), it goes to the nearer by Euclid, though that district's key is the larger",
                pb.key > pa.key && same3(got.get(pb.key), TruckSchedule.sharesOf(per, between, city)) && got.get(pa.key)[0] == 0);
        CityMap.District mid = hand(types, 0, 0, a, 2), ka = hand(types, 1, 0), kb = hand(types, -1, 0);
        got = TruckSchedule.sharesByTrips(List.of(tripsOf(ka), tripsOf(kb)), gridOf(List.of(ka, kb, mid)), per, city);
        CityMap.District smaller = ka.key < kb.key ? ka : kb, larger = smaller == ka ? kb : ka;
        check("...and equally near by both, to the smaller key",
                ka.key != kb.key && same3(got.get(smaller.key), TruckSchedule.sharesOf(per, mid, city)) && got.get(larger.key)[0] == 0);
        // (d) A map of 7 x 7 districts, three of them with trips: the shares of the districts with trips are the districts within reach's.
        List<CityMap.District> all = new ArrayList<>();
        long[] totals = new long[types.length];
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                int na = Math.floorMod(dx * 7 + dy, 5) + 1, nb = Math.floorMod(dx + 2 * dy, 3);
                all.add(hand(types, dx, dy, a, na, b, nb));
                totals[a] += na;
                totals[b] += nb;
            }
        }
        final String keyA = types[a].sector(), keyB = types[b].sector();
        java.util.function.Function<String, double[]> tonnes = s -> s.equals(keyA) ? new double[] { 100, 40, 8 } : s.equals(keyB) ? new double[] { 50, 10, 6 } : new double[3];
        double[][] perAll = TruckSchedule.perBuilding(tonnes, types, totals);
        double[] cityAll = { 150, 50, 14 };
        List<CityMap.DistrictTrips> withTrips = new ArrayList<>();
        for (CityMap.District d : all) if (d.dx == -2 && d.dy == -2 || d.dx == 1 && d.dy == 0 || d.dx == 3 && d.dy == 3) withTrips.add(tripsOf(d));
        got = TruckSchedule.sharesByTrips(withTrips, gridOf(all), perAll, cityAll);
        double[] sum = new double[3], within = new double[3], every = new double[3];
        int inReach = 0;
        for (CityMap.DistrictTrips t : withTrips) for (int l = 0; l < 3; l++) sum[l] += got.get(t.key())[l];
        for (CityMap.District d : all) {
            double[] s = TruckSchedule.sharesOf(perAll, d, cityAll);
            boolean reaches = winnerOf(withTrips, d) != null;
            if (reaches) inReach++;
            for (int l = 0; l < 3; l++) { every[l] += s[l]; if (reaches) within[l] += s[l]; }
        }
        System.out.printf("      %d of the 49 districts are within reach of the 3 with trips, and carry %.3f of the imports%n", inReach, within[0]);
        check("the shares of the districts with trips come to the shares of the districts within reach of one, at most 1 of a load",
                same3(sum, within) && sum[0] <= 1 + 1e-9 && sum[1] <= 1 + 1e-9 && sum[2] <= 1 + 1e-9 && same3(every, new double[] { 1, 1, 1 }) && inReach < 49 && sum[0] < 1 - 1e-6);

        // The trips city's own layout and trips, with tonnes by hand: every sector but Freight (the depots', yards' and terminals') 100 t a load.
        java.util.function.Function<String, double[]> by100 = s -> s.equals("Freight") ? new double[3] : new double[] { 100, 100, 100 };
        long[] mapTotals = trips.totals();
        java.util.Set<String> standing = new java.util.HashSet<>();
        for (int t = 0; t < trips.types().length; t++) {
            if (mapTotals[t] > 0 && trips.types()[t] != null && !trips.types()[t].sector().isEmpty() && !trips.types()[t].sector().equals("Freight")) standing.add(trips.types()[t].sector());
        }
        double[] c100 = { 100.0 * standing.size(), 100.0 * standing.size(), 100.0 * standing.size() };
        double[][] per100 = TruckSchedule.perBuilding(by100, trips.types(), mapTotals);
        List<CityMap.DistrictTrips> real = trips.tripsDrawn();
        got = TruckSchedule.sharesByTrips(real, trips::district, per100, c100);
        boolean positive = true, ridersAre = true;
        double[] realSum = new double[3];
        int ownNothing = 0;
        for (CityMap.DistrictTrips t : real) {
            double[] mine = got.get(t.key()), own100 = TruckSchedule.sharesOf(per100, t.district(), c100);
            positive &= mine[0] > 0 && mine[1] > 0 && mine[2] > 0;
            ridersAre &= same3(mine, ridersOf(real, trips.districts(), t, per100, c100));
            if (own100[0] == 0 && own100[1] == 0 && own100[2] == 0) ownNothing++;
            for (int l = 0; l < 3; l++) realSum[l] += mine[l];
        }
        System.out.printf("      the trips city: %d districts with trips, %d of them whose own works carry nothing; the shares come to %.3f / %.3f / %.3f%n",
                real.size(), ownNothing, realSum[0], realSum[1], realSum[2]);
        check("the trips city, tonnes by hand (100 t a load for each of " + standing.size() + " sectors but Freight): each district with trips draws a share, "
                + "and it is the shares of the districts that ride its trips", positive && ridersAre && real.size() >= 2);
        check("...the whole city is within reach of a district with trips, so together they take 1 of each load", near(realSum[0], 1) && near(realSum[1], 1) && near(realSum[2], 1));

        // A district whose trips' works are Truck Depots alone: the trips city has none (each holds a mix of everything), so by hand one is made on its
        // layout - the district with trips that a roadless district rides keeps its depots and gives the rest of its works to that roadless district.
        Map<Long, CityMap.District> fab = new HashMap<>();
        CityMap.District roadless = null;
        for (CityMap.District d : trips.districts()) {
            CityMap.District c = new CityMap.District(d.dx, d.dy, d.counts.length, d.order);
            System.arraycopy(d.counts, 0, c.counts, 0, d.counts.length);
            fab.put(d.key, c);
        }
        int most = -1;
        for (CityMap.District d : trips.districts()) {
            int n = 0;
            for (int t = 0; t < d.counts.length; t++) n += d.counts[t];
            if (winnerOf(real, d) != null && winnerOf(real, d).key() != d.key && n > most) { most = n; roadless = d; }
        }
        CityMap.DistrictTrips rider = roadless == null ? null : winnerOf(real, roadless);
        check("fixture: a district with works and no trips rides a district with trips (the catchment does something here)", roadless != null && most > 0 && rider != null);
        if (rider == null) return;
        CityMap.District depots = fab.get(rider.key()), landing = fab.get(roadless.key);
        for (int t = 0; t < depots.counts.length; t++) {
            if (trips.types()[t] != null && trips.types()[t].category() == BuildingType.HAULAGE) continue;
            landing.counts[t] += depots.counts[t];
            depots.counts[t] = 0;
        }
        List<CityMap.DistrictTrips> made = new ArrayList<>();
        for (CityMap.DistrictTrips t : real) made.add(new CityMap.DistrictTrips(t.key(), fab.get(t.key()), t.trips()));
        got = TruckSchedule.sharesByTrips(made, gridOf(fab.values()), per100, c100);
        int drawing = 0;
        boolean ownZero = true, riders = true;
        for (CityMap.DistrictTrips t : made) {
            double[] own100 = TruckSchedule.sharesOf(per100, t.district(), c100), mine = got.get(t.key());
            boolean alone = own100[0] == 0 && own100[1] == 0 && own100[2] == 0;
            if (alone && mine[0] > 0 && mine[1] > 0 && mine[2] > 0) drawing++;
            if (t.key() == rider.key()) {
                ownZero &= alone;
                riders &= same3(mine, ridersOf(made, fab.values(), t, per100, c100));
            }
        }
        double[] drawn = got.get(rider.key());
        System.out.printf("      by hand, the district (%d,%d) keeps its %d depots and gives its other works to (%d,%d): %d district with trips whose works are depots alone draws a share"
                + " (%.3f of the imports)%n", rider.district().dx, rider.district().dy, depots.count(MapCheck.depotType(trips.types())), roadless.dx, roadless.dy, drawing, drawn[0]);
        check("a district whose trips' works are Truck Depots alone (its own works carry nothing) draws a share from the works about it: 1 such district, its share > 0 of each load",
                ownZero && drawing == 1 && riders && drawn[0] > 0 && drawn[1] > 0 && drawn[2] > 0);
        double[] total = new double[3];
        for (CityMap.DistrictTrips t : made) for (int l = 0; l < 3; l++) total[l] += got.get(t.key())[l];
        check("...and the tonnes moved by hand are drawn once: together the districts with trips still take 1 of each load", near(total[0], 1) && near(total[1], 1) && near(total[2], 1));
    }

    // ------------------------------------------------------------------ 10

    static void pace() {
        System.out.println("\n--- 10. the pace: the trains' legs, and any visit inside the month ---");
        double[] lengths = { 100, 400, 5000 };
        boolean trains = true, clamped = true;
        double[] legs = new double[lengths.length];
        for (int i = 0; i < lengths.length; i++) {
            TruckSchedule.Route r = TruckSchedule.Route.through(true, new double[] { 0, lengths[i] }, new double[] { 0, 0 }, 0);
            TrainSchedule.Route tr = TrainSchedule.Route.through(0, false, 0, new double[] { 0, lengths[i] }, new double[] { 0, 0 }, 0);
            legs[i] = r.legSeconds();
            trains &= Math.abs(r.legSeconds() - tr.legSeconds()) < EPS;
            clamped &= Math.abs(r.legSeconds() - Math.max(TruckSchedule.LEG_SECONDS, Math.min(TruckSchedule.LEG_MOST, lengths[i] / TrainSchedule.TRAIN_PLOTS_A_SECOND))) < EPS;
        }
        check("a route of 100 plots takes LEG_SECONDS, one of 400 its length over TRAIN_PLOTS_A_SECOND, one of 5,000 LEG_MOST",
                legs[0] == TruckSchedule.LEG_SECONDS && Math.abs(legs[1] - 400 / TrainSchedule.TRAIN_PLOTS_A_SECOND) < EPS && legs[1] > TruckSchedule.LEG_SECONDS
                        && legs[1] < TruckSchedule.LEG_MOST && legs[2] == TruckSchedule.LEG_MOST);
        check("...which is the trains' rule at each length: TrainSchedule.Route.legSeconds()", trains && clamped);
        check("LEG_MOST is the trains'", TruckSchedule.LEG_MOST == TrainSchedule.LEG_MOST && TruckSchedule.LEG_MOST > TruckSchedule.LEG_SECONDS);

        TruckSchedule.Route shortR = TruckSchedule.Route.through(true, new double[] { -10, 0 }, new double[] { 0, 0 }, 0);
        TruckSchedule.Route longR = TruckSchedule.Route.through(true, new double[] { -64, 5000 }, new double[] { 0, 0 }, 64);
        List<TruckSchedule.Route> routes = List.of(shortR, longR);
        double[] loads = { 900, 700, 1200 };
        TruckSchedule s = TruckSchedule.of(4, loads, routes, 1.0, 3);
        double dwell = TruckSchedule.DWELL_SECONDS / TruckSchedule.MONTH_SECONDS;
        boolean inside = true, gate = true, start = true, gone = true;
        boolean onShort = false, onLong = false;
        for (TruckSchedule.Call c : s.calls()) {
            TruckSchedule.Route r = routes.get(c.route());
            double leg = r.legSeconds() / TruckSchedule.MONTH_SECONDS;
            inside &= c.arrives() - leg >= 0 && c.arrives() + dwell + leg <= 1;
            onShort |= c.route() == 0;
            onLong |= c.route() == 1;
            TruckSchedule.Truck at = TruckSchedule.position(c, r, c.arrives()), from = TruckSchedule.position(c, r, c.arrives() - leg);
            gate &= at != null && Math.abs(at.x() - r.xs()[r.xs().length - 1]) < 1e-6 && Math.abs(at.y() - r.ys()[r.ys().length - 1]) < 1e-6;
            start &= from != null && Math.abs(from.x() - r.xs()[0]) < 1e-6 && Math.abs(from.y() - r.ys()[0]) < 1e-6;
            gone &= TruckSchedule.position(c, r, c.arrives() - leg - 1e-6) == null && TruckSchedule.position(c, r, c.arrives() + dwell + leg + 1e-6) == null;
        }
        check("fixture: trucks run both a route of 10 plots and one of 5,000", onShort && onLong && s.callCount() > 0);
        check("every truck's whole visit lies inside the month, on a route of 10 plots and on one of 5,000", inside);
        check("...a truck is at its gate at its arrival and at its route's start a leg (its route's own) before", gate && start);
        check("...and is on the road at no moment outside its visit, a leg before its arrival to a stand and a leg after", gone);

        // frame() finds every truck position() puts in the box, whatever the legs, near the month's edges too.
        boolean frames = true;
        int found = 0;
        long[][] boxes = { { -100, -20, 300, 20 }, { 2000, -20, 2400, 20 }, { 4900, -20, 5100, 20 }, { -2000, 500, -1000, 900 } };
        for (double t : new double[] { 0.001, 0.05, 0.12, 0.4, 0.5, 0.77, 0.9, 0.95, 0.999 }) {
            for (long[] bx : boxes) {
                List<TruckSchedule.Truck> got = s.frame(bx[0], bx[1], bx[2], bx[3], t);
                List<TruckSchedule.Truck> want = new ArrayList<>();
                double w = TruckSchedule.TRUCK_PLOTS;
                for (TruckSchedule.Call c : s.calls()) {
                    TruckSchedule.Truck tr = TruckSchedule.position(c, routes.get(c.route()), t);
                    if (tr != null && tr.x() + w >= bx[0] && tr.x() - w < bx[2] && tr.y() + w >= bx[1] && tr.y() - w < bx[3]) want.add(tr);
                }
                frames &= got.equals(want);
                found += got.size();
            }
        }
        check("frame() is exactly the trucks position() puts in the box, in four boxes at nine moments, a 5,000-plot route among the routes", frames && found > 0);
    }

    public static void main(String[] args) {
        noLine();
        highwayRoutes();
        kept();
        CityMap access = accessCity(types());
        railwayUnchanged(access);
        accessRoutes(access);
        count(access);
        // FR9: the trips city is built to cause a road with a street leg as well as roads that end at a ramp; 8 draws it a district at a
        // time, so it runs before 7, which reads it whole (and the access city 4 drew), and 8's second half changes it last.
        CityMap trips = accessCity(types(), TRIPS_MORE_DEPOTS, TRIPS_HIGHWAYS);
        String tripsCold = tripsOnlyDrawn(trips);
        System.out.println("\n--- 7. a trip is whole: from the world to a gate, on a road all the way ---");
        tripsWhole("the access city", access, false, null);
        tripsWhole("the trips city", trips, true, tripsCold);
        tripsKept(trips, types());
        shares(types(), trips);
        pace();
        System.out.println();
        System.out.println(fails == 0 ? "The trucks' routes and trips are found." : fails + " FAILED");
        System.exit(fails);
    }
}
