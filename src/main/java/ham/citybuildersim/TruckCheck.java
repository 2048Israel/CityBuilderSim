package ham.citybuildersim;

import ham.citybuildersim.TruckSchedule.Call;
import ham.citybuildersim.TruckSchedule.Load;
import ham.citybuildersim.TruckSchedule.Route;
import ham.citybuildersim.TruckSchedule.Truck;
import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Haulage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Proves that Haulage's loads are drawn as trucks on the map's roads, as a
 * pure function of time, and that drawing them changes nothing in the game
 * (0.8.8, batch FR5, slice A; docs/studies/spec-freight-and-infrastructure.md
 * 4.7, D21). Not part of the game.
 *
 * WHY THIS EXISTS. TruckSchedule counts a month's trucks from three tallies
 * Freight keeps beside the railway's (truckedIn(), truckedOut(), truckedHome()),
 * read by nothing in the model and saved with it. A tally that did not come
 * to Haulage's tonnes, a count that dropped a load, an N that read as no round
 * number, a truck that appeared at the month's turn or a setting that was
 * off for an old settings.json would each draw a map that lies. Every number
 * asserted against is the classes' own constant or the model's own getter.
 *
 * What it has to prove, a section each:
 *
 *   1. the tally: in a played city, truckedIn() + truckedOut() + truckedHome()
 *      come to Haulage.getTonnes() every month, each is the sum of the
 *      model's own getters at the railway's share in force, and a city with
 *      no bulk has all three 0;
 *   2. the counts: the trucks drawn, times what each stands for, come to the
 *      month's loads within one truck a load group; a world route takes no
 *      home load; stands() is 1 up to MOST_A_MONTH loads and then the
 *      smallest 1-2-5 number that brings the trucks to MOST_A_MONTH or fewer,
 *      the city's N, which a district's part (a share under 1) keeps;
 *   3. a pure function: the same month, freight and routes give the same
 *      calls and the same frames, a salt gives others, no visit crosses the
 *      month's turn, frame() is exactly the trucks position() says are in
 *      the box, and a truck runs its route and back, loaded as its load is;
 *   4. the near-zoom gate: trucks are drawn at MapFrame.L0 with the setting
 *      on, at no other level and never with it off;
 *   5. the setting: GamePrefs.isTrucksOnMap() is on by default, an old
 *      settings.json reads on, and off and on both survive a save and load;
 *   6. never saved: the tallies ride in Freight's save and come back, and no
 *      save class mentions a TruckSchedule.
 */
public class TruckCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-90s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void check(String label, boolean ok, String detail) {
        if (!ok) fails++;
        System.out.printf("%-90s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static boolean near(double a, double b, double rel) {
        return Math.abs(a - b) <= rel * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    /** The routes of the counts' sections: one world route, two access roads. */
    static List<Route> routes() {
        List<Route> r = new ArrayList<>();
        r.add(Route.through(true, new double[] { -TruckSchedule.WORLD_PLOTS, 0, 100 }, new double[] { 0, 0, 0 }, TruckSchedule.WORLD_PLOTS));
        r.add(Route.through(false, new double[] { 0, 60, 60 }, new double[] { 0, 0, 40 }, 0));
        r.add(Route.through(false, new double[] { 200, 200 }, new double[] { 200, 260 }, 0));
        return r;
    }

    static Set<Integer> routesOf(TruckSchedule s, Load l) {
        Set<Integer> out = new HashSet<>();
        for (Call c : s.calls()) if (c.load() == l) out.add(c.route());
        return out;
    }

    /** The trucks position() puts in the box at t, found the long way: every call, in arrival order. */
    static List<Truck> brute(TruckSchedule s, long x0, long y0, long x1, long y1, double t) {
        List<Truck> out = new ArrayList<>();
        double w = TruckSchedule.TRUCK_PLOTS;
        for (Call c : s.calls()) {
            if (c.route() < 0) continue;
            Truck tr = TruckSchedule.position(c, s.routes().get(c.route()), t);
            if (tr != null && tr.x() + w >= x0 && tr.x() - w < x1 && tr.y() + w >= y0 && tr.y() - w < y1) out.add(tr);
        }
        return out;
    }

    /** The N the rule gives for a month of `loads`, by a plain walk of the sequence 1, 2, 5, 10, 20, 50...: the first that brings loads / N to MOST_A_MONTH or under. */
    static long walkedStands(double loads) {
        long decade = 1;
        while (true) {
            for (int m : new int[] { 1, 2, 5 }) if (loads / (decade * m) <= TruckSchedule.MOST_A_MONTH) return decade * m;
            decade *= 10;
        }
    }

    /** The step of 1, 2, 5, 10, 20, 50... below n (0 for 1). */
    static long stepBelow(long n) {
        long before = 0;
        for (long decade = 1; decade <= n; decade *= 10) for (int m : new int[] { 1, 2, 5 }) {
            if (decade * m == n) return before;
            before = decade * m;
        }
        return before;
    }

    static void quietly(Runnable r) {
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { r.run(); } finally { System.setOut(real); }
    }

    public static void main(String[] args) {
        Game city = HaulageCheck.base(GameFiles.scratch("truckcheck-a"));
        Freight fr = city.getSectors().freight();
        Haulage h = fr.haulage();

        /* ================================================================
           1. THE TALLY
           ================================================================ */

        System.out.println("--- 1. the tally: the three come to Haulage's tonnes, each from the model's own getters ---");

        int guard = 0;
        // The ramp first, as HaulageCheck waits: until the trucks are seeded and bought there is nothing carried.
        while (!(h.getBill() > 0) && guard++ < 16) quietly(city::toggleNextMonth);
        check("fixture: the city has played until Haulage has a fleet and a bill, so it carries tonnes", h.getBill() > 0 && h.getTonnes() > 0 && h.getCarried() > 0);
        boolean sums = true, nonNegative = true, split = true, sumsPlayed = true;
        double seenHome = 0, seenIn = 0, seenOut = 0;
        String detail = "";
        for (int m = 0; m < 6; m++) {
            // What the haul will tally, worked out from the rows the month just run left and the railway's share in force, in the brief's own rule.
            double in = 0, out = 0, home = 0;
            for (Sector s : city.getSectors().all()) {
                if (s == fr) continue;
                for (Good g : Good.values()) {
                    if (g.traffic() != Traffic.BULK) continue;
                    double rail = Math.max(0, Math.min(1, fr.railShareInForce(g)));
                    double relief = 1 - InfrastructureManager.RAIL_ROAD_RELIEF * rail;
                    in += g.tonnesPerUnit() * s.unitsImported(g) * relief;
                    out += g.tonnesPerUnit() * s.unitsExported(g) * relief;
                    home += g.tonnesPerUnit() * s.unitsBoughtLocal(g);
                }
            }
            // The haul run by hand, as HaulageCheck does: the builders' repair imports at the top of a real month are not among the rows the prediction sees.
            quietly(() -> HaulageCheck.haul(city));
            double sum = fr.truckedIn() + fr.truckedOut() + fr.truckedHome();
            sums &= near(sum, h.getTonnes(), 1e-9);
            nonNegative &= fr.truckedIn() >= 0 && fr.truckedOut() >= 0 && fr.truckedHome() >= 0;
            boolean here = near(fr.truckedIn(), in, 1e-9) && near(fr.truckedOut(), out, 1e-9) && near(fr.truckedHome(), home, 1e-9);
            split &= here;
            if (!here || !near(sum, h.getTonnes(), 1e-9)) detail = "month " + m + ": " + fr.truckedIn() + " / " + fr.truckedOut() + " / " + fr.truckedHome() + " against " + in + " / " + out + " / " + home + " and tonnes " + h.getTonnes();
            seenIn += fr.truckedIn(); seenOut += fr.truckedOut(); seenHome += fr.truckedHome();
            // ...and the month played through, whose haul is the game's own.
            quietly(city::toggleNextMonth);
            sumsPlayed &= near(fr.truckedIn() + fr.truckedOut() + fr.truckedHome(), h.getTonnes(), 1e-9);
            nonNegative &= fr.truckedIn() >= 0 && fr.truckedOut() >= 0 && fr.truckedHome() >= 0;
        }
        check("the three tallies sum to Haulage.getTonnes() to a billionth, six hauls running", sums, detail);
        check("...and so they do in the six months the game played through itself", sumsPlayed);
        check("each tally is zero or more, every month", nonNegative);
        check("each is the sum of the sectors' own getters at the railway's share in force", split, detail);
        check("fixture: the city landed, shipped and bought at home, so no split is vacuous", seenIn > 0 && seenOut > 0 && seenHome > 0,
                seenIn + " / " + seenOut + " / " + seenHome);

        Game bare = new Game(GameFiles.scratch("truckcheck-bare"));
        quietly(() -> { bare.newGame(); for (int i = 0; i < 3; i++) bare.toggleNextMonth(); });
        double bulk = 0;
        for (Sector s : bare.getSectors().all()) for (Good g : Good.values()) bulk += Haulage.legTonnes(s, g, 0);
        Freight bf = bare.getSectors().freight();
        check("fixture: a city of houses has no bulk anywhere to haul", bulk == 0, "bulk tonnes " + bulk);
        check("a city with no bulk has all three tallies 0", bf.truckedIn() == 0 && bf.truckedOut() == 0 && bf.truckedHome() == 0);

        /* ================================================================
           2. THE COUNTS
           ================================================================ */

        System.out.println("\n--- 2. the counts: a truck for a load, N a round number past MOST_A_MONTH ---");

        List<Route> routes = routes();
        List<Route> worldOnly = List.of(routes.get(0));
        List<Route> roadsOnly = List.of(routes.get(1), routes.get(2));
        TruckSchedule played = TruckSchedule.of(7, fr, routes, 1, 0);
        double carried = h.getTonnes() > 0 ? h.getCarried() / h.getTonnes() : 0;
        double loadsOfCity = h.getCarried() / Haulage.TONNES_A_LOAD;
        double sumLoads = played.loads(Load.IMPORT) + played.loads(Load.EXPORT) + played.loads(Load.HOME);
        check("the played city's loads are Haulage's carried tonnes over TONNES_A_LOAD", near(sumLoads, loadsOfCity, 1e-9),
                sumLoads + " against " + loadsOfCity + " (carried share " + carried + ")");
        check("the trucks, each standing for stands() loads, are within one truck a load group of the loads",
                Math.abs((double) played.callCount() * played.stands() - sumLoads) <= 3.0 * played.stands(),
                played.callCount() + " x " + played.stands() + " against " + sumLoads);
        TruckSchedule playedWorld = TruckSchedule.of(7, fr, worldOnly, 1, 0);
        check("fixture: the played city bought tonnes at home, so the next line is not vacuous", played.loads(Load.HOME) > 0);
        check("on a world route alone no home load is drawn, and imports and exports are",
                playedWorld.callsOf(Load.HOME) == 0 && playedWorld.callsOf(Load.IMPORT) + playedWorld.callsOf(Load.EXPORT) > 0);
        check("with no freight, or no routes, a schedule has no calls",
                TruckSchedule.of(7, (Freight) null, routes, 1, 0).callCount() == 0 && TruckSchedule.of(7, fr, List.of(), 1, 0).callCount() == 0
                        && TruckSchedule.of(7, fr, null, 1, 0).callCount() == 0);

        double[] quiet = new double[Load.values().length];
        quiet[Load.IMPORT.ordinal()] = 300; quiet[Load.EXPORT.ordinal()] = 200; quiet[Load.HOME.ordinal()] = 400;
        TruckSchedule q = TruckSchedule.of(3, quiet, routes, 1, 0);
        check("900 loads, at most MOST_A_MONTH: stands() is 1 and each load group is within one truck of its loads",
                q.stands() == 1 && q.callCount() <= 900 + 3
                        && Math.abs(q.callsOf(Load.IMPORT) - 300) <= 1 && Math.abs(q.callsOf(Load.EXPORT) - 200) <= 1 && Math.abs(q.callsOf(Load.HOME) - 400) <= 1,
                q.callsOf(Load.IMPORT) + " / " + q.callsOf(Load.EXPORT) + " / " + q.callsOf(Load.HOME) + " x " + q.stands());
        check("a load no route takes has no truck: home loads on a world route alone are 0, the rest drawn",
                TruckSchedule.of(3, quiet, worldOnly, 1, 0).callsOf(Load.HOME) == 0 && TruckSchedule.of(3, quiet, worldOnly, 1, 0).callsOf(Load.IMPORT) > 0);
        check("...and on access roads alone every load is drawn",
                TruckSchedule.of(3, quiet, roadsOnly, 1, 0).callsOf(Load.HOME) > 0 && TruckSchedule.of(3, quiet, roadsOnly, 1, 0).callsOf(Load.EXPORT) > 0);
        boolean homeOnRoads = true, validRoutes = true;
        for (Call c : q.calls()) {
            validRoutes &= c.route() >= 0 && c.route() < routes.size();
            if (c.load() == Load.HOME) homeOnRoads &= !routes.get(c.route()).world();
        }
        check("every call has a route among the schedule's, and every home load is on an access road", validRoutes && homeOnRoads);
        check("imports spread over every route that takes them (the world route and both roads)", routesOf(q, Load.IMPORT).size() == 3,
                routesOf(q, Load.IMPORT).toString());
        check("home loads spread over both access roads and no more", routesOf(q, Load.HOME).equals(Set.of(1, 2)), routesOf(q, Load.HOME).toString());

        // The boundary of the rule, by the city's loads (N is the city's, from every load before `share`: the review).
        int most = TruckSchedule.MOST_A_MONTH;
        double[] edge = new double[Load.values().length];
        edge[Load.IMPORT.ordinal()] = most / 2.0; edge[Load.EXPORT.ordinal()] = most / 2.0;
        check("exactly MOST_A_MONTH loads: stands() is 1", TruckSchedule.of(3, edge, routes, 1, 0).stands() == 1);
        edge[Load.EXPORT.ordinal()] = most / 2.0 + 1;
        check("one load over MOST_A_MONTH: stands() is 2, the smallest step that brings the trucks under", TruckSchedule.of(3, edge, routes, 1, 0).stands() == 2);
        boolean rule = true, round = true, few = true, minimal = true, accounted = true;
        String bad = "";
        for (double share : new double[] { 1.1, 2, 3, 5, 7.5, 11, 25, 50, 123, 400, 1000, 2500, 33_333 }) {
            double[] scaled = new double[quiet.length];
            for (int k = 0; k < quiet.length; k++) scaled[k] = quiet[k] * share;
            TruckSchedule big = TruckSchedule.of(3, scaled, routes, 1, 0);
            double loads = 900 * share;
            long n = big.stands();
            long expected = walkedStands(loads);
            rule &= n == expected;
            round &= String.valueOf(n).matches("[125]0*");
            few &= big.callCount() <= most + 3;
            // The step below would not do: the N is the smallest, not just one that fits.
            minimal &= n == 1 || loads / stepBelow(n) > most;
            accounted &= Math.abs((double) big.callCount() * n - loads) <= 3.0 * n;
            if (n != expected || big.callCount() > most + 3) bad += " x" + share + ": N " + n + " expected " + expected + " trucks " + big.callCount();
        }
        check("past MOST_A_MONTH loads, stands() is the smallest 1-2-5 step that fits (13 cities from 1.1 to 33,333 times 900 loads)", rule, bad);
        check("...it reads as a round N: 1, 2 or 5 and noughts", round);
        check("...the trucks drawn are MOST_A_MONTH or fewer, a truck a load group over", few, bad);
        check("...the step below would have drawn more than MOST_A_MONTH", minimal);
        check("...and the trucks, each for N loads, still come to the loads within one a group", accounted);
        double[] fiftyLoads = new double[quiet.length];
        for (int k = 0; k < quiet.length; k++) fiftyLoads[k] = quiet[k] * 50;
        TruckSchedule fifty = TruckSchedule.of(3, fiftyLoads, routes, 1, 0);
        check("45,000 loads: stands() is the 1-2-5 rule's, " + walkedStands(900 * 50.0), fifty.stands() == walkedStands(900 * 50.0) && fifty.stands() > 1,
                "stands " + fifty.stands() + ", " + fifty.callCount() + " trucks");
        // A district's part of the city (share under 1) draws its trucks at the city's N, the legend's, and fewer of them.
        TruckSchedule part = TruckSchedule.of(3, fiftyLoads, roadsOnly, 0.05, 17);
        check("a district's part (share .05) keeps the city's N, and draws about its share of the trucks",
                part.stands() == fifty.stands() && Math.abs(part.callCount() * (double) part.stands() - 900 * 50.0 * 0.05) <= 3.0 * part.stands(),
                "N " + part.stands() + " against " + fifty.stands() + ", " + part.callCount() + " trucks");

        /* ================================================================
           3. A PURE FUNCTION
           ================================================================ */

        System.out.println("\n--- 3. a pure function of the month, the freight, the routes and the salt ---");

        TruckSchedule a = TruckSchedule.of(4, quiet, routes, 1, 11), b = TruckSchedule.of(4, quiet, routes, 1, 11);
        check("two schedules of one month, loads, routes and salt have equal calls", a.calls().equals(b.calls()) && a.callCount() == b.callCount() && a.stands() == b.stands());
        TruckSchedule played2 = TruckSchedule.of(7, fr, routes, 1, 0);
        check("...and so do two of the played city's freight", played.calls().equals(played2.calls()));
        boolean framesEqual = true, framesNonEmpty = false, again = true;
        for (double t : new double[] { 0.03, 0.2, 0.41, 0.5, 0.77, 0.97 }) {
            List<Truck> fa = a.frame(-200, -200, 400, 400, t), fb = b.frame(-200, -200, 400, 400, t);
            framesEqual &= fa.equals(fb);
            framesNonEmpty |= !fa.isEmpty();
            again &= fa.equals(a.frame(-200, -200, 400, 400, t));
        }
        check("fixture: some frame has trucks on the road", framesNonEmpty);
        check("two schedules give the same frame at each of six moments", framesEqual);
        check("frame() at t and at t again gives the same trucks", again);
        TruckSchedule salted = TruckSchedule.of(4, quiet, routes, 1, 12);
        check("a different salt gives a different set of arrivals, the same number of loads",
                !a.calls().equals(salted.calls()) && Math.abs(a.callCount() - salted.callCount()) <= 3);
        check("a different month gives a different set of arrivals", !a.calls().equals(TruckSchedule.of(5, quiet, routes, 1, 11).calls()));

        double leg = TruckSchedule.LEG_SECONDS / TruckSchedule.MONTH_SECONDS, dwell = TruckSchedule.DWELL_SECONDS / TruckSchedule.MONTH_SECONDS;
        boolean inside = true, sorted = true;
        double last = -1;
        for (Call c : a.calls()) {
            inside &= c.arrives() - leg >= 0 && c.arrives() + dwell + leg <= 1;
            sorted &= c.arrives() >= last;
            last = c.arrives();
        }
        check("no truck's whole visit crosses the month's turn (it arrives a leg after the start and leaves a stand and a leg before the end)", inside);
        check("calls() comes in the order of arrival", sorted);

        boolean frameIsBrute = true, boxed = true;
        int inBox = 0;
        long[][] boxes = { { -200, -200, 400, 400 }, { 150, 150, 300, 300 }, { 30, -10, 70, 20 }, { 1000, 1000, 2000, 2000 } };
        for (double t : new double[] { 0.01, 0.12, 0.33, 0.5, 0.64, 0.88, 0.99 }) {
            for (long[] bx : boxes) {
                List<Truck> f = a.frame(bx[0], bx[1], bx[2], bx[3], t);
                frameIsBrute &= f.equals(brute(a, bx[0], bx[1], bx[2], bx[3], t));
                inBox += f.size();
            }
        }
        check("frame() is exactly the trucks position() puts in the box, in four boxes at seven moments", frameIsBrute);
        check("fixture: the boxes held trucks, and one box held none", inBox > 0 && a.frame(1000, 1000, 2000, 2000, 0.5).isEmpty());
        check("crosses() says yes over the routes and no over empty ground",
                a.crosses(-200, -200, 400, 400) && a.crosses(190, 190, 210, 210) && !a.crosses(1000, 1000, 2000, 2000)
                        && !TruckSchedule.of(4, quiet, List.of(), 1, 0).crosses(-200, -200, 400, 400));

        // One truck, by hand: the second access road, a straight run south 60 plots, and the bent one.
        double[] one = new double[Load.values().length];
        one[Load.IMPORT.ordinal()] = 1;
        Route bent = routes.get(1);
        Call imp = new Call(Load.IMPORT, 0, 0.5), exp = new Call(Load.EXPORT, 0, 0.5), home = new Call(Load.HOME, 0, 0.5);
        double at = 0.5;
        Truck t0 = TruckSchedule.position(imp, bent, at - leg), tHalf = TruckSchedule.position(imp, bent, at - leg / 2);
        Truck tBend = TruckSchedule.position(imp, bent, at - 0.2 * leg), tGate = TruckSchedule.position(imp, bent, at + dwell / 4);
        Truck tOutBend = TruckSchedule.position(imp, bent, at + dwell + 0.2 * leg), tOutHalf = TruckSchedule.position(imp, bent, at + dwell + leg / 2);
        double len = bent.length();
        check("a truck is not on the road before its leg, nor after its stand and leg",
                TruckSchedule.position(imp, bent, at - leg - 1e-6) == null && TruckSchedule.position(imp, bent, at + dwell + leg + 1e-6) == null);
        check("it comes in at the route's start, half its length along half a leg on",
                t0 != null && near(t0.x(), 0, 1e-9) && near(t0.y(), 0, 1e-9) && tHalf != null && near(Math.hypot(tHalf.x(), tHalf.y()), len / 2, 1e-9)
                        && near(tHalf.x(), 50, 1e-9) && near(tHalf.y(), 0, 1e-9));
        check("it turns the corner: a fifth of a leg before the gate it is 80 plots along, on the second stretch (60, 20), heading south",
                tBend != null && near(tBend.x(), 60, 1e-9) && near(tBend.y(), 20, 1e-9) && near(Math.cos(tBend.heading()), 0, 1e-9) && near(Math.sin(tBend.heading()), 1, 1e-9));
        check("it heads east on the first stretch, and stands at the gate, the route's end",
                near(Math.cos(tHalf.heading()), 1, 1e-9) && near(Math.sin(tHalf.heading()), 0, 1e-9) && tGate != null && near(tGate.x(), 60, 1e-9) && near(tGate.y(), 40, 1e-9));
        check("running out it heads the way it came: west on the first stretch, north on the second",
                near(Math.cos(tOutHalf.heading()), -1, 1e-9) && near(Math.sin(tOutHalf.heading()), 0, 1e-9)
                        && near(Math.cos(tOutBend.heading()), 0, 1e-9) && near(Math.sin(tOutBend.heading()), -1, 1e-9) && near(tOutBend.y(), 20, 1e-9));
        check("an import comes loaded and goes empty; an export comes empty and goes loaded; a home load comes loaded and goes empty",
                tHalf.loaded() && !tOutHalf.loaded()
                        && !TruckSchedule.position(exp, bent, at - leg / 2).loaded() && TruckSchedule.position(exp, bent, at + dwell + leg / 2).loaded()
                        && TruckSchedule.position(home, bent, at - leg / 2).loaded() && !TruckSchedule.position(home, bent, at + dwell + leg / 2).loaded());
        Route world = routes.get(0);
        Truck w0 = TruckSchedule.position(imp, world, at - leg), wHalfFade = TruckSchedule.position(imp, world, at - leg * (1 - world.fadeTo() / 2 / world.length()));
        Truck wFull = TruckSchedule.position(imp, world, at - leg * (1 - (world.fadeTo() + 1) / world.length())), wOut = TruckSchedule.position(imp, world, at + dwell + leg * 0.5);
        check("a world route fades in: alpha 0 at its far end, half at half the fade, 1 past it, and fades out the same way",
                w0 != null && w0.alpha() < 1e-9 && near(wHalfFade.alpha(), 0.5, 1e-9) && wFull.alpha() == 1 && wOut.alpha() == 1
                        && TruckSchedule.position(imp, world, at + dwell + leg * (1 - 1e-9)).alpha() < 1e-6);
        check("a road with no fade (fadeTo 0) shows whole from its first plot", t0 != null && t0.alpha() == 1 && tOutHalf.alpha() == 1);

        /* ================================================================
           4. THE NEAR-ZOOM GATE
           ================================================================ */

        System.out.println("\n--- 4. the near-zoom gate ---");

        check("trucks are drawn at MapFrame.L0 with the setting on", TruckSchedule.drawnAt(MapFrame.L0, true));
        check("...not at L0 with it off", !TruckSchedule.drawnAt(MapFrame.L0, false));
        boolean others = true;
        for (int level : new int[] { MapFrame.L1, MapFrame.L2, MapFrame.FAR }) others &= !TruckSchedule.drawnAt(level, true) && !TruckSchedule.drawnAt(level, false);
        check("...and at no other level, on or off (L1, L2, FAR)", others);

        /* ================================================================
           5. THE SETTING
           ================================================================ */

        System.out.println("\n--- 5. the setting: on by default, an old settings.json reads on, both survive a save ---");

        com.google.gson.Gson gson = new com.google.gson.Gson();
        check("a new GamePrefs has trucks on the map", new GamePrefs().isTrucksOnMap());
        check("a settings.json of \"{}\" reads on", gson.fromJson("{}", GamePrefs.class).isTrucksOnMap());
        check("an older settings.json with other keys and none for the trucks reads on", gson.fromJson("{\"fullScreen\":false}", GamePrefs.class).isTrucksOnMap());
        check("a settings.json that says off reads off", !gson.fromJson("{\"trucksOnMap\":false}", GamePrefs.class).isTrucksOnMap());
        GameFiles prefsFiles = GameFiles.scratch("truckcheck-prefs");
        GamePrefs off = new GamePrefs();
        off.setTrucksOnMap(false);
        boolean saved = off.save(prefsFiles);
        check("off, saved and loaded, reads off", saved && !GamePrefs.load(prefsFiles).isTrucksOnMap());
        GamePrefs on = new GamePrefs();
        on.setTrucksOnMap(true);
        boolean savedOn = on.save(prefsFiles);
        check("on, saved over it and loaded, reads on", savedOn && GamePrefs.load(prefsFiles).isTrucksOnMap());
        check("no settings.json at all reads on", GamePrefs.load(GameFiles.scratch("truckcheck-prefs-none")).isTrucksOnMap());

        /* ================================================================
           6. NEVER SAVED
           ================================================================ */

        System.out.println("\n--- 6. never saved: the tallies ride in Freight's save, the schedule in none ---");

        SectorState state = fr.toState();
        check("Freight's save carries the three tallies", state.extras.containsKey("truckIn") && state.extras.containsKey("truckOut") && state.extras.containsKey("truckHome"));
        check("...at the tallies' values", state.extras.get("truckIn") == fr.truckedIn() && state.extras.get("truckOut") == fr.truckedOut() && state.extras.get("truckHome") == fr.truckedHome());
        Game other = new Game(GameFiles.scratch("truckcheck-other"));
        quietly(other::newGame);
        Freight twin = other.getSectors().freight();
        check("fixture: a fresh city's Freight has none", twin.truckedIn() == 0 && twin.truckedOut() == 0 && twin.truckedHome() == 0);
        twin.restore(state);
        check("restored into a fresh Freight, the three come back equal",
                twin.truckedIn() == fr.truckedIn() && twin.truckedOut() == fr.truckedOut() && twin.truckedHome() == fr.truckedHome()
                        && twin.truckedIn() + twin.truckedOut() + twin.truckedHome() > 0);
        TruckSchedule fromTwin = TruckSchedule.of(7, twin, routes, 1, 0);
        check("...and the reload's trucks are the month's: the same loads", fromTwin.loads(Load.IMPORT) == played.loads(Load.IMPORT) || twin.haulage().getTonnes() == 0,
                "loads " + fromTwin.loads(Load.IMPORT) + " against " + played.loads(Load.IMPORT));
        SectorState empty = new SectorState();
        empty.extras = new java.util.LinkedHashMap<>();
        twin.restore(empty);
        check("a save from before them restores with none", twin.truckedIn() == 0 && twin.truckedOut() == 0 && twin.truckedHome() == 0);

        java.nio.file.Path src = java.nio.file.Paths.get("src", "main", "java", "ham", "citybuildersim");
        if (java.nio.file.Files.isDirectory(src)) {
            List<String> naming = new ArrayList<>();
            try (java.util.stream.Stream<java.nio.file.Path> files = java.nio.file.Files.list(src)) {
                for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) files::iterator) {
                    String n = p.getFileName().toString();
                    if (!n.endsWith(".java") || !(n.contains("Save") && !n.endsWith("Check.java") || n.equals("SectorState.java") || n.equals("GameState.java"))) continue;
                    if (java.nio.file.Files.readString(p).contains("TruckSchedule")) naming.add(n);
                }
            } catch (java.io.IOException e) {
                naming.add("unreadable: " + e);
            }
            check("no save class (the *Save files, SectorState) mentions a TruckSchedule", naming.isEmpty(), naming.toString());
        } else {
            System.out.println("(source tree not under the working directory: the save classes' text is not scanned)");
        }

        System.out.println();
        if (fails == 0) {
            System.out.println("Trucks work: the tallies come to Haulage's tonnes, a truck is a load (or N of them, a round N), the schedule is a pure"
                    + " function of the month, drawn at the near view with a setting that is on by default, and nothing of it is saved.");
        } else {
            System.out.println(fails + " FAILED");
        }
        System.exit(fails);
    }
}
