package ham.citybuildersim;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The quay crane (HB2, 2026-11-09; QuayCrane, BoatSchedule.atQuay()): a boat at its terminal's quay is worked by a light line from over
 * the ship to the apron and a box that slides along it, as Jerus picked look 3a from the renders. Pure geometry, so every fixture is a
 * work, a boat's point and a share.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE SHARE: BoatSchedule.atQuay() is 0 or more exactly when position() has the boat docked, over a month's calls and times about
 *      each call's arrival and departure; it is 0 at the arrival, 1 at the end of BERTH_SECONDS, -1 before and after.
 *
 *   2. WHERE THE LINE LIES, on each of the four sides: one crane at a small berth; its apron end is inside the box, in its sea-most row;
 *      its ship end is on the water beyond the wall, past the boat; the line crosses the wall square to the shore; the hook is on it.
 *
 *   3. THE MOVES: over a boat's time at the quay the first crane's hook leaves the ship end carrying and puts the load down at the apron
 *      end MOVES times, and comes back bare each time; a berth of TWO_CRANES_FROM plots has a second crane that carries from the apron
 *      end to the ship end; one plot shorter has one.
 *
 *   4. THE CARGO: dry bulk is a grab of ore, containers and general cargo a box, a tanker two loading arms that do not move; a pier of
 *      its own (a quay that is not HARBOUR_QUAY_PLOTS), a share outside 0..1 and a null cargo give no crane.
 *
 *   5. THE TERMINAL: worksByBerth() finds each berthed work by its berth's point, and no point that is not one.
 *
 *   6. THE COST: a frame's boats drawn() and the cranes of every docked one over a bay of 64 berths, within a frame's boat budget.
 *
 * Timing harness (Timing.HARNESSES): section 6 judges a frame's cranes through Timing.
 *
 * Every fixture CAUSES its condition: the works are laid on a shore, the routes and the month's calls come from the trade at sea.
 */
public class QuayCraneCheck {

    static int fails = 0;
    static int checks = 0;
    static PrintStream out;

    /** How many shares a boat's time at the quay is sampled at in the sections that follow the hook: the hook moves 0.001 of a move a step. */
    static final int STEPS = 2000;

    static void check(String label, boolean ok) {
        checks++;
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** A timing assertion's line, its verdict Timing's: soft under -Dcbs.timing=soft, hard at the gate. */
    static void timed(String label, boolean ok, String detail) {
        checks++;
        if (!ok) fails++;
        out.printf("%-100s %s  %s%n", label, Timing.verdict(ok, label), detail);
    }

    static void section(String what) {
        out.println();
        out.println("--- " + what + " ---");
    }

    /* ----- the fixtures ----- */

    /** A work whose sea is on `side`, `along` plots long and the Bulk Berth's PortCheck.BULK_DEEP deep, with the harbour's quay, at (3000, 3000). */
    static CityShore.Work box(int side, int along) {
        boolean ns = side % 2 == 0;
        return new CityShore.Work(3000, 3000, ns ? along : PortCheck.BULK_DEEP, ns ? PortCheck.BULK_DEEP : along, 0, side, CityShore.HARBOUR_QUAY_PLOTS);
    }

    /** How far a world point is from the work's land edge, out toward its sea, in plots: worked out here side by side, not as the model does. */
    static double seaward(CityShore.Work w, double x, double y) {
        switch (w.side()) {
            case 0:  return w.y0() + w.h() - y;
            case 1:  return x - w.x0();
            case 2:  return y - w.y0();
            default: return w.x0() + w.w() - x;
        }
    }

    /** A world point's place along the shore: its x for a sea on the north or south, its y for one on the east or west. */
    static double alongShore(CityShore.Work w, double x, double y) {
        return w.side() % 2 == 0 ? x : y;
    }

    /** How deep the work's box is, land edge to sea edge, in plots. */
    static double depth(CityShore.Work w) {
        return w.side() % 2 == 0 ? w.h() : w.w();
    }

    /** Whether a world point lies in the plot row of the box nearest the sea. */
    static boolean inSeaRow(CityShore.Work w, double x, double y) {
        long fx = (long) Math.floor(x), fy = (long) Math.floor(y);
        if (!w.holds(fx, fy)) return false;
        switch (w.side()) {
            case 0:  return fy == w.y0();
            case 1:  return fx == w.x0() + w.w() - 1;
            case 2:  return fy == w.y0() + w.h() - 1;
            default: return fx == w.x0();
        }
    }

    /** The shares 0, 1/steps ... (steps-1)/steps. */
    static double share(int k, int steps) {
        return (double) k / steps;
    }

    /* ----- 1 ----- */

    /** Section 1: atQuay() is position()'s docked(), as a share. */
    static void theShare() {
        section("1. THE SHARE: atQuay() is a boat's share of its time at the quay, and -1 when it is not at it");
        int pitch = PortCheck.BULK_ALONG + CityShore.CLEAR, berths = 8;
        CityShore bay = PortCheck.berthRow(berths, 0, pitch);
        List<BoatSchedule.Route> routes = PortCheck.routesOf(bay, Ports.Cargo.LIQUID, Ports.Cargo.DRY_BULK);
        BoatSchedule month = BoatSchedule.of(4000, MapCheck.tradeAtSea(berths * 125_000.0, berths * 375_000.0, 0, 0), routes, Ports.BULK_TERMINAL_SHIPS);
        List<BoatSchedule.Call> calls = month.calls();
        double berth = BoatSchedule.BERTH_SECONDS / BoatSchedule.MONTH_SECONDS, leg = BoatSchedule.LEG_SECONDS / BoatSchedule.MONTH_SECONDS;
        double[] offsets = { -2 * leg, -leg, -leg / 2, -1e-9, 0, berth * 0.1, berth * 0.25, berth * 0.5, berth * 0.75, berth * 0.9, berth, berth + 1e-9,
                berth + leg / 2, berth + leg, berth + 2 * leg };
        boolean agree = true, valued = true, start = true, end = true, before = true, after = true;
        int docked = 0, afloat = 0;
        for (BoatSchedule.Call c : calls) {
            double a = c.arrives();
            for (double off : offsets) {
                double t = a + off, at = BoatSchedule.atQuay(c, t);
                BoatSchedule.Boat b = BoatSchedule.position(c, routes.get(c.route()), t);
                boolean isDocked = b != null && b.docked();
                agree &= (at >= 0) == isDocked;
                if (isDocked) {
                    docked++;
                    valued &= Math.abs(at - (t - a) / berth) < 1e-9 && at <= 1;
                } else {
                    afloat++;
                    valued &= at == -1;
                }
            }
            start &= BoatSchedule.atQuay(c, a) == 0;
            end &= Math.abs(BoatSchedule.atQuay(c, a + berth) - 1) < 1e-9;
            before &= BoatSchedule.atQuay(c, a - 1e-9) == -1;
            after &= BoatSchedule.atQuay(c, a + berth + 1e-9) == -1;
        }
        check("atQuay() >= 0 exactly when position() has the boat docked: " + calls.size() + " calls at " + offsets.length + " times each, " + docked
                + " docked and " + afloat + " not", agree && docked > 0 && afloat > 0);
        check("...and it is (t - arrives) over BERTH_SECONDS / MONTH_SECONDS at the quay, -1 away from it", valued);
        check("...0 at arrives(), 1 at arrives() + BERTH_SECONDS / MONTH_SECONDS, -1 just before and just after", start && end && before && after);
        check("...-1 for no call", BoatSchedule.atQuay(null, 0.5) == -1);
    }

    /* ----- 2 ----- */

    /** Section 2: the line, for a small berth on each side. */
    static void whereTheLineLies() {
        section("2. WHERE THE LINE LIES: from over the ship across the quay's wall to the apron, square to the shore, on all four sides");
        String[] names = { "north", "east", "south", "west" };
        for (int side = 0; side < 4; side++) {
            CityShore.Work w = PortCheck.bulkWork(3000, 3000, side);
            double[] boat = w.berth();
            double shipAt = seaward(w, boat[0], boat[1]);
            boolean one = true, apron = true, ship = true, square = true, onIt = true;
            int hooks = 0;
            for (int k = 0; k <= 20; k++) {
                List<QuayCrane.Reach> rs = QuayCrane.reaches(w, Ports.Cargo.GENERAL, boat[0], boat[1], k / 20.0);
                one &= rs.size() == 1;
                if (rs.isEmpty()) continue;
                QuayCrane.Reach r = rs.get(0);
                apron &= inSeaRow(w, r.x1(), r.y1()) && Math.abs(seaward(w, r.x1(), r.y1()) - (depth(w) - QuayCrane.APRON_IN - QuayCrane.LINE_LANDWARD)) < 1e-9;
                long sx = (long) Math.floor(r.x0()), sy = (long) Math.floor(r.y0());
                ship &= !w.holds(sx, sy) && !w.quayAt(sx, sy) && seaward(w, r.x0(), r.y0()) > depth(w) + w.quay()
                        && Math.abs(seaward(w, r.x0(), r.y0()) - shipAt - QuayCrane.LINE_SEAWARD) < 1e-9;
                square &= Math.abs(alongShore(w, r.x0(), r.y0()) - alongShore(w, r.x1(), r.y1())) < 1e-9
                        && seaward(w, r.x0(), r.y0()) > depth(w) + w.quay() && seaward(w, r.x1(), r.y1()) < depth(w);      // it runs from beyond the wall to inside the box
                double lo = Math.min(seaward(w, r.x0(), r.y0()), seaward(w, r.x1(), r.y1())), hi = Math.max(seaward(w, r.x0(), r.y0()), seaward(w, r.x1(), r.y1())),
                        h = seaward(w, r.hx(), r.hy());
                onIt &= Math.abs(alongShore(w, r.hx(), r.hy()) - alongShore(w, r.x0(), r.y0())) < 1e-9 && h >= lo - 1e-9 && h <= hi + 1e-9;
                hooks++;
            }
            check(names[side] + ": a small berth (" + PortCheck.BULK_ALONG + " along, " + PortCheck.BULK_DEEP + " deep) has one crane at every one of 21 shares", one && hooks == 21);
            check(names[side] + ": its apron end is inside the box, in the plot row nearest the sea", apron);
            check(names[side] + ": its ship end is on the water, outside the box and the quay's strip, past the boat's distance from the land", ship);
            check(names[side] + ": the line crosses the wall square to the shore (the same place along it at both ends), and the hook is on it between its ends", square && onIt);
            // A boat moored stern-to: its middle lies well past the crane's reach, at the same place along the shore.
            double far = depth(w) + w.quay() + QuayCrane.OUTREACH + 3, off = far - shipAt;
            double fx = boat[0] + CityShore.DX[side] * off, fy = boat[1] + CityShore.DY[side] * off;
            boolean reach = Math.abs(seaward(w, fx, fy) - far) < 1e-9 && shipAt <= depth(w) + w.quay() + QuayCrane.OUTREACH, sameApron = true;
            for (int k = 0; k <= 20; k++) {
                List<QuayCrane.Reach> near = QuayCrane.reaches(w, Ports.Cargo.GENERAL, boat[0], boat[1], k / 20.0), rs = QuayCrane.reaches(w, Ports.Cargo.GENERAL, fx, fy, k / 20.0);
                if (rs.size() != 1 || near.size() != 1) {
                    reach = false;
                    continue;
                }
                QuayCrane.Reach r = rs.get(0);
                reach &= Math.abs(seaward(w, r.x0(), r.y0()) - (depth(w) + w.quay() + QuayCrane.OUTREACH + QuayCrane.LINE_SEAWARD)) < 1e-9
                        && seaward(w, r.hx(), r.hy()) <= depth(w) + w.quay() + QuayCrane.OUTREACH + 1e-9;
                sameApron &= r.x1() == near.get(0).x1() && r.y1() == near.get(0).y1();
            }
            check(names[side] + ": a boat whose middle lies " + (QuayCrane.OUTREACH + 3) + " plots off the wall (moored stern-to) is worked at OUTREACH (" + QuayCrane.OUTREACH
                    + " plots) from the wall's outer edge and not at its middle, the hook never past it, the apron end where it was", reach && sameApron);
        }
    }

    /* ----- 3 ----- */

    /** What one crane does through a boat's time at the quay, sampled STEPS times. */
    record Moves(int pickups, int drops, boolean atEnds, boolean steady) { }

    /** The loads a crane picks up and puts down in a time at the quay, and whether it does so at the ends `from` and `to` (plots from the land) and goes between them steadily. */
    static Moves moves(CityShore.Work w, Ports.Cargo cargo, int crane, double from, double to) {
        double[] boat = w.berth();
        double[] v = new double[STEPS];
        int[] load = new int[STEPS];
        for (int k = 0; k < STEPS; k++) {
            QuayCrane.Reach r = QuayCrane.reaches(w, cargo, boat[0], boat[1], share(k, STEPS)).get(crane);
            v[k] = seaward(w, r.hx(), r.hy());
            load[k] = r.load();
        }
        int pick = 0, drop = 0;
        boolean ends = true, steady = true;
        double dir = Math.signum(to - from);
        for (int k = 0; k < STEPS; k++) {
            int j = (k + STEPS - 1) % STEPS;      // the time at the quay comes round: its end is its start
            boolean now = load[k] != QuayCrane.BARE, was = load[j] != QuayCrane.BARE;
            if (!was && now) {
                pick++;
                ends &= Math.abs(v[j] - from) < 1e-9 && Math.abs(v[k] - from) < 1e-3;
            }
            if (was && !now) {
                drop++;
                ends &= Math.abs(v[j] - to) < 1e-9;
            }
            if (was && now) steady &= (v[k] - v[j]) * dir >= -1e-9;           // carrying: toward `to`
            if (!was && !now) steady &= (v[k] - v[j]) * dir <= 1e-9;          // bare: back toward `from`
        }
        return new Moves(pick, drop, ends, steady);
    }

    /** Section 3: the moves of one and of two cranes. */
    static void theMoves() {
        section("3. THE MOVES: carried out to the apron MOVES times and back bare; a second crane loads where the first unloads");
        String[] names = { "north", "east", "south", "west" };
        for (int side = 0; side < 4; side++) {
            CityShore.Work big = box(side, QuayCrane.TWO_CRANES_FROM), small = box(side, QuayCrane.TWO_CRANES_FROM - 1);
            double[] bb = big.berth(), sb = small.berth();
            check(names[side] + ": a berth of TWO_CRANES_FROM (" + QuayCrane.TWO_CRANES_FROM + ") plots has two cranes, one plot shorter has one",
                    QuayCrane.reaches(big, Ports.Cargo.GENERAL, bb[0], bb[1], 0.3).size() == 2 && QuayCrane.reaches(small, Ports.Cargo.GENERAL, sb[0], sb[1], 0.3).size() == 1);
            double shipV = seaward(small, sb[0], sb[1]), apronV = depth(small) - QuayCrane.APRON_IN;
            Moves m = moves(small, Ports.Cargo.GENERAL, 0, shipV, apronV);
            check(names[side] + ": the only crane of a small berth picks the box up at the ship and puts it down at the apron " + QuayCrane.MOVES + " times (" + m.pickups() + " and "
                    + m.drops() + "), and comes back bare each time, in between at the ends and steadily", m.pickups() == QuayCrane.MOVES && m.drops() == QuayCrane.MOVES && m.atEnds() && m.steady());
            shipV = seaward(big, bb[0], bb[1]);
            apronV = depth(big) - QuayCrane.APRON_IN;
            Moves first = moves(big, Ports.Cargo.GENERAL, 0, shipV, apronV), second = moves(big, Ports.Cargo.GENERAL, 1, apronV, shipV);
            check(names[side] + ": at a large berth the first crane unloads (ship to apron) " + first.pickups() + " times and the second loads (apron to ship) " + second.pickups() + ", each " + QuayCrane.MOVES,
                    first.pickups() == QuayCrane.MOVES && first.drops() == QuayCrane.MOVES && first.atEnds() && first.steady()
                    && second.pickups() == QuayCrane.MOVES && second.drops() == QuayCrane.MOVES && second.atEnds() && second.steady());
            boolean apart = true;
            for (int k = 0; k < 20; k++) {
                List<QuayCrane.Reach> rs = QuayCrane.reaches(big, Ports.Cargo.GENERAL, bb[0], bb[1], share(k, 20));
                double a0 = alongShore(big, rs.get(0).hx(), rs.get(0).hy()), a1 = alongShore(big, rs.get(1).hx(), rs.get(1).hy()), mid = alongShore(big, bb[0], bb[1]);
                apart &= Math.abs(Math.abs(a0 - a1) - 2 * QuayCrane.TWO_APART) < 1e-9 && Math.abs((a0 + a1) / 2 - mid) < 1e-9;
            }
            check(names[side] + ": the two cranes stand 2 x TWO_APART plots apart, either side of the boat's place along the shore", apart);
        }
    }

    /* ----- 4 ----- */

    /** Section 4: what hangs on the hook, and the works that have no crane. */
    static void theCargo() {
        section("4. THE CARGO: a grab of ore, a box, a tanker's two arms; no crane for a pier of its own, a share outside 0..1 or no cargo");
        CityShore.Work w = box(0, QuayCrane.TWO_CRANES_FROM);
        double[] boat = w.berth();
        for (Ports.Cargo cargo : new Ports.Cargo[] { Ports.Cargo.DRY_BULK, Ports.Cargo.CONTAINER, Ports.Cargo.GENERAL }) {
            Set<Integer> seen = new HashSet<>();
            for (int k = 0; k < STEPS; k++) for (QuayCrane.Reach r : QuayCrane.reaches(w, cargo, boat[0], boat[1], share(k, STEPS))) seen.add(r.load());
            int carried = cargo == Ports.Cargo.DRY_BULK ? QuayCrane.GRAB : QuayCrane.BOX;
            check(cargo + " hangs " + (carried == QuayCrane.GRAB ? "a grab of ore" : "a box") + " on the hook, and the bare hook between: " + seen, seen.equals(Set.of(QuayCrane.BARE, carried)));
        }
        boolean arms = true, still = true, apart = true, hooked = true, reach = true;
        List<QuayCrane.Reach> at0 = QuayCrane.reaches(w, Ports.Cargo.LIQUID, boat[0], boat[1], 0);
        for (int k = 0; k < 50; k++) {
            List<QuayCrane.Reach> rs = QuayCrane.reaches(w, Ports.Cargo.LIQUID, boat[0], boat[1], share(k, 50));
            arms &= rs.size() == 2 && rs.get(0).load() == QuayCrane.ARM && rs.get(1).load() == QuayCrane.ARM;
            still &= rs.equals(at0);
            if (rs.size() != 2) continue;
            apart &= Math.abs(Math.abs(alongShore(w, rs.get(0).x0(), rs.get(0).y0()) - alongShore(w, rs.get(1).x0(), rs.get(1).y0())) - 2 * QuayCrane.ARM_APART) < 1e-9;
            for (QuayCrane.Reach r : rs) {
                hooked &= r.hx() == r.x0() && r.hy() == r.y0();
                reach &= Math.abs(seaward(w, r.x0(), r.y0()) - seaward(w, boat[0], boat[1])) < 1e-9
                        && Math.abs(seaward(w, r.x1(), r.y1()) - (depth(w) + w.quay() - QuayCrane.APRON_IN)) < 1e-9;
            }
        }
        check("LIQUID is two loading arms (ARM), at every one of 50 shares", arms);
        check("...they do not move: the same at every share", still);
        check("...they stand 2 x ARM_APART plots apart, with no hook but the ship end", apart && hooked);
        check("...and run from the wall's outer edge less half a plot out to the boat's distance", reach);

        boolean none = true;
        int tried = 0;
        for (int quay : new int[] { 0, CityShore.HARBOUR_QUAY_PLOTS - 1, CityShore.HARBOUR_QUAY_PLOTS + 1 }) {
            CityShore.Work pier = new CityShore.Work(3000, 3000, 5, PortCheck.BULK_DEEP, 0, 0, quay);
            double[] pb = pier.berth();
            for (Ports.Cargo cargo : Ports.Cargo.values()) {
                none &= QuayCrane.reaches(pier, cargo, pb[0], pb[1], 0.3).isEmpty();
                tried++;
            }
        }
        check("a work with a pier of its own (a quay that is not HARBOUR_QUAY_PLOTS: 0, one under, one over) has no crane, " + tried + " tried, whatever the cargo", none);
        boolean outside = true;
        for (double bad : new double[] { -1, -1e-9, 1 + 1e-9, 2, Double.NaN, Double.POSITIVE_INFINITY }) {
            for (Ports.Cargo cargo : Ports.Cargo.values()) outside &= QuayCrane.reaches(w, cargo, boat[0], boat[1], bad).isEmpty();
        }
        check("a share of -1 (the boat is not at the quay), or any outside 0..1, gives none, whatever the cargo", outside);
        check("...and so does a null cargo or a null work",
                QuayCrane.reaches(w, null, boat[0], boat[1], 0.3).isEmpty() && QuayCrane.reaches(null, Ports.Cargo.GENERAL, boat[0], boat[1], 0.3).isEmpty());
        check("...but the ends of 0..1 give the cranes",
                QuayCrane.reaches(w, Ports.Cargo.GENERAL, boat[0], boat[1], 0).size() == 2 && QuayCrane.reaches(w, Ports.Cargo.GENERAL, boat[0], boat[1], 1).size() == 2);
    }

    /* ----- 5 ----- */

    /** Section 5: a berth's point finds its work. */
    static void theTerminal() {
        section("5. THE TERMINAL: worksByBerth() finds a berthed work by its berth's point");
        for (int side = 0; side < 4; side++) {
            CityShore bay = PortCheck.berthRow(8, side, PortCheck.BULK_ALONG + CityShore.CLEAR);
            bay.add(new CityShore.Work(3100, 3100, 6, 6, 0, side, 0));       // a tank farm has no quay: no berth
            Map<Long, CityShore.Work> by = QuayCrane.worksByBerth(bay);
            boolean found = true;
            int berthed = 0;
            for (CityShore.Work w : bay.works()) {
                if (!w.berthed()) continue;
                berthed++;
                double[] b = w.berth();
                found &= by.get(QuayCrane.berthKey((long) Math.floor(b[0]), (long) Math.floor(b[1]))) == w;
            }
            CityShore.Work first = bay.works().get(0);
            check("side " + side + ": each of the bay's " + berthed + " berthed works is found by its berth's point (floor of Work.berth()), and the map holds no more",
                    found && berthed == 8 && by.size() == berthed);
            check("side " + side + ": the box's own corner, the plot before the berth and the tank farm's plot are no berth",
                    by.get(QuayCrane.berthKey(first.x0(), first.y0())) == null && by.get(QuayCrane.berthKey((long) Math.floor(first.berth()[0]) - 1, (long) Math.floor(first.berth()[1]))) == null
                    && by.get(QuayCrane.berthKey(3100, 3100)) == null);
        }
        Set<Long> keys = new HashSet<>();
        for (long x = -10; x <= 10; x++) for (long y = -10; y <= 10; y++) keys.add(QuayCrane.berthKey(x, y));
        check("berthKey() is one key for a point: 21 x 21 points about the origin (negative coordinates too) give " + keys.size() + " keys, and (x, y) is not (y, x)",
                keys.size() == 21 * 21 && QuayCrane.berthKey(3, 5) != QuayCrane.berthKey(5, 3));
    }

    /* ----- 6 ----- */

    /** Section 6: a frame's boats and cranes over a bay of 64 berths, within a frame's boat budget. */
    static void theCost() {
        section("6. THE COST: a frame's boats drawn() and the cranes of every docked one, over a bay of 64 berths");
        int berths = 64, pitch = PortCheck.BULK_ALONG + CityShore.CLEAR;
        double scale = PortCheck.BERTH_SCALES[1];
        CityShore bay = PortCheck.berthRow(berths, 0, pitch);
        List<BoatSchedule.Route> routes = PortCheck.routesOf(bay, Ports.Cargo.LIQUID, Ports.Cargo.DRY_BULK);
        BoatSchedule month = BoatSchedule.of(4000, MapCheck.tradeAtSea(berths * 125_000.0, berths * 375_000.0, 0, 0), routes, Ports.BULK_TERMINAL_SHIPS);
        long x0 = 3000 - 10, x1 = 3000 + (long) berths * pitch + 10, y0 = 2900, y1 = 3010;
        List<BoatSchedule.Call> calls = month.calls();
        Map<Long, CityShore.Work> byBerth = QuayCrane.worksByBerth(bay);
        int frames = Math.min(calls.size(), 400);
        double best = Double.MAX_VALUE;
        long shown = 0, cranes = 0;
        for (int round = 0; round < MapCheck.WARM_ROUNDS + MapCheck.TIMED_ROUNDS; round++) {
            long t0 = System.nanoTime();
            shown = 0;
            cranes = 0;
            for (int i = 0; i < frames; i++) {
                double t = calls.get(i * calls.size() / frames).arrives();
                List<BoatSchedule.Boat> drawn = month.drawn(month.frame(x0, y0, x1, y1, t), t, scale, bay);
                shown += drawn.size();
                for (BoatSchedule.Boat b : drawn) {
                    if (!b.docked()) continue;
                    BoatSchedule.Berth berth = routes.get(b.call().route()).berth();
                    CityShore.Work w = byBerth.get(QuayCrane.berthKey(berth.x(), berth.y()));
                    if (w != null) cranes += QuayCrane.reaches(w, b.call().cargo(), b.x(), b.y(), BoatSchedule.atQuay(b.call(), t)).size();
                }
            }
            double ms = (System.nanoTime() - t0) / 1e6 / frames;
            if (round >= MapCheck.WARM_ROUNDS) best = Math.min(best, ms);
        }
        check("the frames gave cranes to work: " + cranes + " reaches over " + frames + " frames of the bay of " + berths + " berths", cranes > 0);
        timed("a frame's boats drawn() and the reaches() of every docked one take no more than MapCheck.BOAT_FRAME_MS (" + MapCheck.BOAT_FRAME_MS + " ms)",
                Timing.within(best, MapCheck.BOAT_FRAME_MS) && shown > 0 && cranes > 0,
                String.format("%.3f ms a frame, %,d boats and %,d reaches over %d frames", best, shown, cranes, frames));
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        theShare();
        whereTheLineLies();
        theMoves();
        theCargo();
        theTerminal();
        theCost();
        out.println();
        out.println(checks + " checks.");
        if (fails == 0) {
            out.println("All checks passed.");
        } else {
            out.println(fails + " FAILED");
            System.exit(fails);
        }
    }
}
