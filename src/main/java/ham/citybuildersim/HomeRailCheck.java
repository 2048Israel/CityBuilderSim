package ham.citybuildersim;

import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Haulage;
import ham.citybuildersim.sectors.Mining;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * The railway's carriage of the city's own bulk, and the port yard, held to FR6's
 * brief: a bulk tonne bought at home rides a siding at the city leg's rate on
 * the room the boundary's tonnes leave, a tonne that crossed a berth rides the
 * port yard's rails to the same rule, and every leg is billed once.
 *
 * WHY THIS HARNESS EXISTS. Until FR6 the railway was offered only what crossed
 * the boundary: the ore from a city's mine to its own mill was a whole truck leg
 * at Haulage's quote though FR7 lays the mine a siding, and a tonne landed at a
 * berth was a whole truck leg from the quay to the door. FR6 (docs/queue/FR6.md)
 * deals those tonnes to the railway last, after the boundary's, bills them at the
 * railway's quote on Haulage's, and takes their railed part off the trucks' leg.
 * It was written by one agent from the brief and is held here by another, from
 * the brief's rules and the model's own getters and constants: no figure below
 * is typed in that a getter or a constant could give. Nine claims, numbered as
 * FR6.md section 6 numbers them:
 *
 *   1. NO RAILWAY, NO CHANGE. With no station and no spur nothing of the city's
 *      own bulk is dealt, and the trucks' legs with zero shares are the old
 *      legs to the bit.
 *   2. DEALT AFTER THE BOUNDARY. Never more than the offer, than what the track
 *      or the sets leave once the boundary's bulk and goods are dealt, or than
 *      what the stations and the sidings leave of the same; nothing when either
 *      is used up; never below zero.
 *   3. EACH LEG ONCE. A home tonne's truck leg is (1 - RAIL_ROAD_RELIEF x h) of
 *      it and the railway's tonnes are RAIL_ROAD_RELIEF x h: together the tonne.
 *   4. THE PORT YARD. A station and a sea terminal both standing, and then a
 *      tonne that crossed a berth has (1 - PORT_YARD_SHARE x the quay share) of
 *      its truck leg; with either missing, no yard and no quay share.
 *   5. THE BILL. The shippers' "Rail freight" lines are Freight's rail revenue
 *      to the cent; the city part is the railed city tonnes x the railway's
 *      quote x Haulage's, both read before the haul; a mine that sold at home
 *      pays no city bill for it.
 *   6. THE DIESEL. The city tonnes burn CITY_LITRES_A_TONNE each.
 *   7. THE WEIGHING. A planner weighs a home-bought tonne at the steady quote
 *      x (its truck weight + the railway's quote x its rail weight): below the
 *      all-truck leg while the railway's quote is below 1.
 *   8. THE SAVE. The shares in force cross a save, and the reload bills the
 *      next month to the bit.
 *   9. A REAL RUN. LongPlaytest's city, played with its advisor: the months the
 *      home share is above zero and the months a port yard stands, counted.
 *
 * It tests the model through its public getters and constants and changes
 * nothing in it; where the code and the brief disagree it says FAIL and the
 * line says where. The fixtures are HaulageCheck's played city (three iron
 * mines selling ore at home to a pelletising plant that lands its coal and
 * ships its pellets) with a railway handed to it, because the tonnes a
 * mechanic deals are the tonnes a city makes: each fixture says what it lays
 * and the section asserts the condition it needed actually came about.
 */
public class HomeRailCheck {

    static int fails = 0;

    static void quietly(Runnable r) { HaulageCheck.quietly(r); }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        System.out.printf("%-96s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    /** Bitwise, not near (TradeCostCheck explains why this family compares so). */
    static boolean same(double a, double b) { return HaulageCheck.same(a, b); }

    static boolean near(double a, double b, double tol) { return HaulageCheck.near(a, b, tol); }

    /** A positive zero, bit for bit: what "exactly nothing" means for a deal. */
    static boolean isZero(double x) { return Double.doubleToRawLongBits(x) == 0L; }

    /** The siding's part of a home tonne, and of a railed boundary tonne (InfrastructureManager). */
    static final double RELIEF = InfrastructureManager.RAIL_ROAD_RELIEF;

    /** The most of the tonnes that crossed a berth the port yard takes to rail (FR6, D7). */
    static final double YARD = Freight.PORT_YARD_SHARE;

    /** The diesel a railed city tonne burns, in litres. */
    static final double CITY_LITRES = Freight.CITY_LITRES_A_TONNE;

    /** Months a fixture city is played before a railway is handed to it: HaulageCheck's, long enough for the trucks to be seeded and the books settled. */
    static final int BASE_MONTHS = 14;

    /** Months of a real run section 9 plays: the advisor lays its first station at about month 990 in this city (the run's own count prints it) and the run takes about half a minute. */
    static final int REAL_RUN_MONTHS = 1250;

    /* ================================================================
       THE FIXTURES
       ================================================================ */

    /**
     * HaulageCheck's played city with a railway handed to it and played on: `depots` Rail Depots, `spurs` Rail Spurs, `lines` Freight Lines
     * and one building from the ports (`berth`, or none) laid on their ground (HaulageCheck.lay), then `months` months.
     */
    static Game railed(GameFiles files, int depots, int spurs, int lines, String berth, int months) {
        Game g = HaulageCheck.base(files, true);
        // ...on the literals, pellets' world prices doubled (FL1): the Pelletising Plant's pellets net under its ore and coal abroad, and THE ORDER
        // no longer buys ore for an export at a loss, so the mines would ship what the plant left; the premise is mines that sell their ore at home.
        g.playOnLiterals();
        g.getMarkets().get(Good.PELLETS).setWorldPrices(Good.PELLETS.worldImportPrice() * 2, Good.PELLETS.worldExportPrice() * 2);
        quietly(() -> { for (int i = 0; i < BASE_MONTHS; i++) g.toggleNextMonth(); });
        quietly(() -> {
            if (depots > 0) HaulageCheck.lay(g, "Rail Depot", depots);
            if (spurs > 0) HaulageCheck.lay(g, "Rail Spur", spurs);
            if (lines > 0) HaulageCheck.lay(g, "Freight Line", lines);
            if (berth != null) HaulageCheck.lay(g, berth, 1);
            for (int i = 0; i < months; i++) g.toggleNextMonth();
        });
        return g;
    }

    /** ...played on, up to a year, until the railway's room after the boundary's tonnes is short of the city's: the share in force strictly between nothing and all. */
    static void untilFractional(Game g) {
        Freight fr = g.getSectors().freight();
        int guard = 0;
        while (!(fr.getHomeShare() > 0 && fr.getHomeShare() < 1) && guard++ < 12) quietly(g::toggleNextMonth);
    }

    /** The home and quay shares put where a fixture wants them, through the sector's own save and restore (the shares are struck at a haul, so a fixture cannot reach them by moving the room). */
    static void setShares(Freight fr, double home, double quay) {
        SectorState st = fr.toState();
        st.extras.put("homeShare", home);
        st.extras.put("quayShare", quay);
        fr.restore(st);
    }

    /** What a sector pays the railway, in the statement's input line (Freight bills it "Rail freight"). */
    static double railLine(Sector s) { return s.otherInputParts().getOrDefault("Rail freight", 0.0); }

    /** The pre-FR6 city leg of a good: the old three-argument form, written out from FR2's brief. */
    static double beforeFr6(Sector s, Good g, double railShare) {
        if (s == null || g == null || g.traffic() != Traffic.BULK) return 0;
        double rail = Double.isFinite(railShare) ? Math.max(0, Math.min(1, railShare)) : 0;
        return g.tonnesPerUnit() * (s.unitsBoughtLocal(g) + (s.unitsImported(g) + s.unitsExported(g)) * (1 - RELIEF * rail));
    }

    /** A bare Freight on a catalogue with these buildings standing (RailDepotCheck's fixture): no city, so only the buildings decide a port yard. */
    static final class Bare {
        final BuildingManager bm = new BuildingManager();
        final Freight fr = new Freight();

        Bare(String... standing) {
            quietly(bm::initializeTemplates);
            ((Sector) fr).attach(bm, new Markets());
            for (String name : standing) bm.addStack(bm.getTemplateByName(name), 1, true);
        }

        /** The tonnes a month of berth standing, over every kind of cargo. */
        double berthTonnes() {
            double t = 0;
            for (double b : Ports.berths(bm)) t += b;
            return t;
        }
    }

    /* ----- the next haul's bill, worked out before it runs ----- */

    /** What the next haul will bill each sector, from the rows the last month left and the state in force: the brief's rule, not the code's loop. */
    static final class Plan {
        double h, q, rq, hq;
        final Map<Sector, Double> boundary = new LinkedHashMap<>(), home = new LinkedHashMap<>(), quay = new LinkedHashMap<>();
        double homeTonnes, quayTonnes;

        /** The city part of a sector's line: its railed city tonnes x the railway's quote x Haulage's. */
        double city(Sector s) { return (home.getOrDefault(s, 0.0) + quay.getOrDefault(s, 0.0)) * rq * hq; }

        double expected(Sector s) { return boundary.getOrDefault(s, 0.0) + city(s); }
    }

    static Plan plan(Game game) {
        Freight fr = game.getSectors().freight();
        Plan p = new Plan();
        p.h = fr.getHomeShare();
        p.q = fr.getQuayShare();
        p.rq = fr.getQuote();
        p.hq = fr.haulage().getQuote();
        double fx = game.getMarkets().getExchangeRate();
        for (Sector s : game.getSectors().all()) {
            if (s == fr) continue;
            double boundary = 0, home = 0, quay = 0;
            for (Good g : Good.values()) {
                Traffic stream = g.traffic();
                if (stream == null || !stream.isFreight()) continue;
                double units = s.unitsExported(g) + s.unitsImported(g);
                if (units > 0) boundary += units * g.baseFreight() * fx * fr.railShareInForce(g) * p.rq;
                if (stream != Traffic.BULK) continue;
                double bought = s.unitsBoughtLocal(g);
                if (bought > 0) home += bought * g.tonnesPerUnit() * RELIEF * p.h;
                if (units > 0) quay += units * g.tonnesPerUnit() * fr.seaShareInForce(g) * YARD * p.q;
            }
            p.boundary.put(s, boundary);
            p.home.put(s, home);
            p.quay.put(s, quay);
            p.homeTonnes += home;
            p.quayTonnes += quay;
        }
        return p;
    }

    /** One month run with its bill worked out first: the plan, then what the haul in the month billed and burned. */
    static final class Month {
        Plan plan;
        double lines, haulageBilled, railRevenue, homeRailed, quayRailed, homeBill, cityLitres, fuelLitres, hauled;
        double worstMiss;
        String worstWhere = "";
        double constructionExcess;
    }

    /** NaN leaves the shares as they stand. */
    static Month runMonth(Game city, double setHome, double setQuay) {
        Freight fr = city.getSectors().freight();
        if (!Double.isNaN(setHome)) setShares(fr, setHome, setQuay);
        Month m = new Month();
        m.plan = plan(city);
        quietly(city::toggleNextMonth);
        for (Sector s : city.getSectors().all()) {
            if (s == fr) continue;
            double got = railLine(s), want = m.plan.expected(s);
            m.lines += got;
            // THE BUILDERS BUY THEIR REPAIR MATERIALS AT THE TOP OF THE MONTH (HaulageCheck.compare): those purchases are in no row between
            // two months, so Construction's line is held to be no less than the rows say, and its excess is counted.
            if (Sectors.CONSTRUCTION.equals(s.key())) {
                m.constructionExcess += got - want;
                if (got < want * (1 - 1e-9) - 1e-12 && Math.abs(got - want) > m.worstMiss) {
                    m.worstMiss = Math.abs(got - want);
                    m.worstWhere = String.format("%s billed %.9f against at least %.9f", s.key(), got, want);
                }
                continue;
            }
            double miss = Math.abs(got - want) / Math.max(1, Math.abs(want));
            if (miss > m.worstMiss) {
                m.worstMiss = miss;
                m.worstWhere = String.format("%s billed %.9f against %.9f", s.key(), got, want);
            }
        }
        m.haulageBilled = fr.getHaulageBilled();
        m.railRevenue = fr.otherRevenueParts().getOrDefault("Rail freight billed", 0.0);
        m.homeRailed = fr.getHomeRailed();
        m.quayRailed = fr.getQuayRailed();
        m.homeBill = fr.getHomeBill();
        m.cityLitres = fr.getCityLitres();
        m.fuelLitres = fr.getFuelLitres();
        m.hauled = fr.getHauledTonnes();
        return m;
    }

    /* ================================================================
       THE RUN
       ================================================================ */

    public static void main(String[] args) {

        GameFiles yardFiles = GameFiles.scratch("homerailcheck-yard");

        System.out.println("fixtures: the cities are built first, and each section says what it needs of them");
        Game plain = HaulageCheck.played(GameFiles.scratch("homerailcheck-plain"), BASE_MONTHS);
        Game trackOnly = railed(GameFiles.scratch("homerailcheck-track"), 0, 0, 4, null, 3);
        Game tight = railed(GameFiles.scratch("homerailcheck-tight"), 1, 3, 0, null, 8);
        Game rail = railed(GameFiles.scratch("homerailcheck-rail"), 1, 4, 0, null, 3);
        untilFractional(rail);
        Game yard = railed(yardFiles, 1, 0, 0, "Bulk Berth", 4);
        Game spurBerth = railed(GameFiles.scratch("homerailcheck-spurberth"), 0, 3, 4, "Bulk Berth", 4);

        noRailway(plain, trackOnly);
        dealt(tight, rail);
        eachLegOnce(rail, yard);
        portYard(rail, yard, spurBerth);
        Month[] months = bill(rail, yard);
        diesel(plain, months);
        weighing(rail, yard);
        save(yard, yardFiles);
        realRun(rail);

        System.out.println();
        if (fails == 0) {
            System.out.println("The railway carries the city's own bulk: dealt after the boundary's tonnes, billed once at the city leg's rate,"
                    + " burning a third of a truck's diesel, with the port yard on the quay and the shares in the save.");
        } else {
            System.out.println(fails + " FAILED");
        }
        System.exit(fails);
    }

    /* ================================================================
       1. NO RAILWAY, NO CHANGE
       ================================================================ */

    static void noRailway(Game plain, Game trackOnly) {
        System.out.println("\n--- 1. no railway, no change: with no station and no spur the city's own bulk rides the trucks, to the bit ---");

        int cases = 0;
        boolean allZero = true;
        double[] offers = { 1, 750, 40_000, 5e6, 1e12 };
        double[] capacities = { 0, 1e5, 1e9, Double.POSITIVE_INFINITY };
        double[] dealtBefore = { 0, 1e4, 1e8 };
        for (double offer : offers) {
            for (double capacity : capacities) {
                for (double bulk : dealtBefore) {
                    for (double goods : dealtBefore) {
                        cases++;
                        allZero &= isZero(Freight.dealHome(capacity, 0, 0, bulk, goods, offer));
                    }
                }
            }
        }
        report("dealHome() with no station and no siding takes exactly nothing, whatever is offered or left of the track",
                allZero, cases + " offers against every capacity and boundary deal");

        // The legs: a fixture city's every sector and good, at a range of railed shares (and some that are not shares at all).
        double[] railShares = { -1, 0, .1, .25, .5, .9, 1, 2, Double.NaN };
        int rows = 0, withLegs = 0;
        boolean zeroForm = true, oldForm = true, seaWithNoYard = true;
        for (Sector s : plain.getSectors().all()) {
            for (Good g : Good.values()) {
                for (double r : railShares) {
                    double three = Haulage.legTonnes(s, g, r);
                    zeroForm &= same(Haulage.legTonnes(s, g, r, 0, 0, 0), three);
                    seaWithNoYard &= same(Haulage.legTonnes(s, g, r, .7, 0, 0), three);
                    oldForm &= same(three, beforeFr6(s, g, r));
                    rows++;
                    if (three > 0) withLegs++;
                }
            }
        }
        report("legTonnes() with zero shares is the three-argument form for every sector, good and railed share, bit for bit",
                withLegs > 0 && zeroForm && seaWithNoYard, String.format("%,d rows, %,d of them with legs", rows, withLegs));
        report("...and that form is the pre-FR6 leg: the tonnes bought at home in full and the crossing tonnes less the siding's railed part",
                oldForm, "written out from FR2's brief, compared bitwise");

        Freight fr = plain.getSectors().freight();
        report("a played city with no station and no spur: the railway is offered the home tonnes and takes none, bills none",
                fr.stationTonnes() == 0 && fr.sidingTonnes() == 0 && fr.getHomeOffered() > 0
                        && same(fr.getHomeShare(), 0) && same(fr.getQuayShare(), 0) && same(fr.getHomeBill(), 0)
                        && same(fr.getHomeRailed(), 0) && same(fr.getQuayRailed(), 0) && !fr.hasPortYard(),
                String.format("%,.0f t offered, home share %s, quay share %s, bill %s", fr.getHomeOffered(), fr.getHomeShare(), fr.getQuayShare(), fr.getHomeBill()));

        Freight ft = trackOnly.getSectors().freight();
        report("...and so is a city with four freight lines laid and no station or spur: track alone rails nothing of it",
                ft.trackTonnes() > 0 && ft.stationTonnes() == 0 && ft.sidingTonnes() == 0 && ft.getHomeOffered() > 0
                        && same(ft.getCapacityTonnes(), 0) && same(ft.getHomeShare(), 0) && same(ft.getQuayShare(), 0)
                        && same(ft.getHomeBill(), 0) && same(ft.getHomeRailed(), 0),
                String.format("%,.0f t of track, %,.0f t offered, capacity %s, home share %s", ft.trackTonnes(), ft.getHomeOffered(), ft.getCapacityTonnes(), ft.getHomeShare()));
    }

    /* ================================================================
       2. DEALT AFTER THE BOUNDARY
       ================================================================ */

    static void dealt(Game tight, Game rail) {
        System.out.println("\n--- 2. dealt after the boundary: what the track and the stations leave of the boundary's tonnes, never more ---");

        // Hand-made cases, each causing one cap to bind. {capacity, stations, sidings, bulk dealt, goods dealt, offer, tonnes taken}.
        double[][] hand = {
            { 1000, 600, 400, 100, 50, 30, 30 },     // the offer binds
            { 200, 600, 400, 100, 50, 1000, 50 },    // the track or the sets bind: 200 less the boundary's 150
            { 5000, 600, 400, 100, 50, 1000, 850 },  // the stations and the sidings bind: 1,000 less the boundary's 150
            { 5000, 600, 0, 100, 50, 1000, 450 },    // stations alone
            { 5000, 0, 400, 100, 50, 1000, 250 },    // sidings alone
            { 150, 600, 400, 100, 50, 10, 0 },       // the track is used up, exactly
            { 100, 600, 400, 100, 50, 10, 0 },       // ...and overdealt: nothing, not a negative
            { 5000, 100, 50, 100, 50, 10, 0 },       // the stations and sidings are used up, exactly
            { 5000, 100, 50, 120, 50, 10, 0 },       // ...and overdealt
            { 5000, 600, 400, 100, 50, 0, 0 },       // nothing offered
        };
        String[] says = { "the offer", "the track", "the stations and sidings", "stations alone", "sidings alone", "the track used up",
                "the track overdealt", "the rooms used up", "the rooms overdealt", "no offer" };
        boolean right = true;
        StringBuilder wrong = new StringBuilder();
        for (int i = 0; i < hand.length; i++) {
            double[] c = hand[i];
            double got = Freight.dealHome(c[0], c[1], c[2], c[3], c[4], c[5]);
            if (!same(got, c[6])) {
                right = false;
                wrong.append(says[i]).append(": ").append(got).append(" against ").append(c[6]).append("; ");
            }
        }
        report("each cap binds in turn: the offer, the track or sets, the stations and sidings, each alone, and each used up",
                right, hand.length + " hand-made cases" + (wrong.length() > 0 ? " " + wrong : ""));

        // The boundary's own deal, as the haul strikes it (Freight.deal), and the city's tonnes on what it leaves.
        double[] fillsIt = Freight.deal(1000, 600, 400, 5000, 5000);
        report("the boundary's bulk and goods take the room first: a boundary that fills the railway leaves the city's tonnes nothing",
                fillsIt[0] + fillsIt[1] == 1000 && isZero(Freight.dealHome(1000, 600, 400, fillsIt[0], fillsIt[1], 300)),
                String.format("the boundary dealt %,.0f t of 1,000, the city took %s", fillsIt[0] + fillsIt[1], Freight.dealHome(1000, 600, 400, fillsIt[0], fillsIt[1], 300)));
        double[] byStations = Freight.deal(1000, 400, 300, 200, 100);
        double[] byTrack = Freight.deal(600, 400, 300, 100, 50);
        double stationsLeft = 400 + 300 - (byStations[0] + byStations[1]), trackLeft = 1000 - (byStations[0] + byStations[1]);
        double trackLeft2 = 600 - (byTrack[0] + byTrack[1]), stationsLeft2 = 400 + 300 - (byTrack[0] + byTrack[1]);
        report("...a lighter one leaves what the stations and sidings have left when that is the lesser (track 1,000, rooms 700 less the boundary's 300)",
                stationsLeft < trackLeft && same(Freight.dealHome(1000, 400, 300, byStations[0], byStations[1], 5000), stationsLeft) && stationsLeft == 400,
                String.format("%,.0f t left of the stations and sidings against %,.0f t of track", stationsLeft, trackLeft));
        report("...and what the track has left when that is the lesser (track 600, rooms 700 less the boundary's 150)",
                trackLeft2 < stationsLeft2 && same(Freight.dealHome(600, 400, 300, byTrack[0], byTrack[1], 5000), trackLeft2) && trackLeft2 == 450,
                String.format("%,.0f t left of the track against %,.0f t of the stations and sidings", trackLeft2, stationsLeft2));

        // A sweep: whatever the numbers, the four bounds hold and one of them is tight when the offer is not met.
        Random random = new Random(2026_10_19L);
        int sweep = 6000, tight4 = 0;
        boolean bounded = true, binds = true;
        for (int i = 0; i < sweep; i++) {
            double cap = random.nextInt(4) == 0 ? 0 : random.nextInt(1_000_000);
            double st = random.nextInt(4) == 0 ? 0 : random.nextInt(1_000_000);
            double sid = random.nextInt(4) == 0 ? 0 : random.nextInt(1_000_000);
            double bulk = random.nextInt(1_200_000), goods = random.nextInt(300_000);
            double offer = random.nextInt(2_000_000);
            double got = Freight.dealHome(cap, st, sid, bulk, goods, offer);
            double dealt = bulk + goods;
            double trackRoom = Math.max(0, cap - dealt), stationRoom = Math.max(0, st + sid - dealt);
            bounded &= got >= 0 && got <= offer && got <= trackRoom && got <= stationRoom;
            boolean tightBound = got == offer || got == trackRoom || got == stationRoom;
            binds &= tightBound;
            if (got > 0 && got < offer) tight4++;
        }
        report("at random: never negative, never more than the offer, the track left or the stations and sidings left, and one of them is the answer",
                bounded && binds && tight4 > 0, sweep + " cases, " + tight4 + " of them partly dealt");

        // Never a share of nothing: a non-offer is no tonnes.
        report("an offer that is not a positive number is nothing, with room to spare",
                isZero(Freight.dealHome(1e9, 1e9, 1e9, 0, 0, 0)) && isZero(Freight.dealHome(1e9, 1e9, 1e9, 0, 0, -5))
                        && isZero(Freight.dealHome(1e9, 1e9, 1e9, 0, 0, Double.NaN)) && isZero(Freight.dealHome(1e9, 1e9, 1e9, 0, 0, Double.POSITIVE_INFINITY)),
                "0, -5, NaN and infinity offered");

        // In the cities themselves.
        Freight ft = tight.getSectors().freight();
        double boundaryDealt = RailDepotCheck.dealtNow(ft);
        report("a played city whose boundary fills the railway (a depot and three spurs): the city's own bulk is offered and none is dealt",
                ft.getHomeOffered() > 0 && near(boundaryDealt, ft.getCapacityTonnes(), 1e-9) && same(ft.getHomeShare(), 0) && same(ft.getHomeBill(), 0),
                String.format("%,.0f t offered, the boundary dealt %,.0f of %,.0f t, home share %s", ft.getHomeOffered(), boundaryDealt, ft.getCapacityTonnes(), ft.getHomeShare()));

        Freight fr = rail.getSectors().freight();
        double taken = fr.getHomeShare() * fr.getHomeOffered();
        double left = fr.getCapacityTonnes() - RailDepotCheck.dealtNow(fr);
        report("...and one the boundary leaves room in (a depot and four spurs): the city's tonnes are exactly the room left, the share in force strictly between nothing and all",
                fr.getHomeShare() > 0 && fr.getHomeShare() < 1 && fr.getHomeOffered() > taken && near(taken, left, 1e-9),
                String.format("share %.4f of %,.0f t offered = %,.0f t; the boundary dealt %,.0f of %,.0f t, %,.0f t left", fr.getHomeShare(), fr.getHomeOffered(),
                        taken, RailDepotCheck.dealtNow(fr), fr.getCapacityTonnes(), left));
    }

    /* ================================================================
       3. EACH LEG ONCE
       ================================================================ */

    static void eachLegOnce(Game rail, Game yard) {
        System.out.println("\n--- 3. each leg once: a home tonne's truck leg and the railway's tonnes are the tonne, billed and relieved together ---");

        // The formula, on the plant's ore at the share the city really has and at hand-set ones.
        Freight fr = rail.getSectors().freight();
        Sector plant = rail.getSectors().heavyIndustry();
        double h = fr.getHomeShare(), r = fr.railShareInForce(Good.IRON);
        assertTrue("fixture: the railway carries a share of the city's own bulk, strictly between nothing and all, and of the landed ore",
                h > 0 && h < 1 && fr.getHomeRailed() > 0 && r > 0);
        double tpu = Good.IRON.tonnesPerUnit(), bought = plant.unitsBoughtLocal(Good.IRON), crossing = plant.unitsImported(Good.IRON) + plant.unitsExported(Good.IRON);
        report("the plant's ore leg at the home share h is its home tonnes x (1 - RAIL_ROAD_RELIEF x h) plus its crossing tonnes at the boundary's rule",
                bought > 0 && crossing > 0 && near(Haulage.legTonnes(plant, Good.IRON, r, 0, h, 0),
                        tpu * bought * (1 - RELIEF * h) + tpu * crossing * (1 - RELIEF * r), 1e-12),
                String.format("h=%.4f: %,.0f t at home + %,.0f t crossing = %,.0f t of legs", h, tpu * bought, tpu * crossing, Haulage.legTonnes(plant, Good.IRON, r, 0, h, 0)));
        boolean linear = true;
        for (double share : new double[] { 0, .1, .4, .75, 1 }) {
            double truck = tpu * bought * (1 - RELIEF * share), railed = tpu * bought * RELIEF * share;
            linear &= near(Haulage.legTonnes(plant, Good.IRON, r, 0, share, 0), truck + tpu * crossing * (1 - RELIEF * r), 1e-12)
                    && near(truck + railed, tpu * bought, 1e-12)
                    && near(Haulage.legTonnes(plant, Good.IRON, r, 0, 0, 0) - Haulage.legTonnes(plant, Good.IRON, r, 0, share, 0), railed, 1e-9);
        }
        report("...and at any share the truck tonnes and the railed tonnes (RAIL_ROAD_RELIEF x h of it) are the home tonne, once",
                linear, "h = 0, .1, .4, .75, 1");
        report("a home share cannot make a leg dearer or the legs negative: the share is clamped to a share",
                Haulage.legTonnes(plant, Good.IRON, r, 0, 5, 0) <= Haulage.legTonnes(plant, Good.IRON, r, 0, 1, 0) * (1 + 1e-12)
                        && same(Haulage.legTonnes(plant, Good.IRON, r, 0, 5, 0), Haulage.legTonnes(plant, Good.IRON, r, 0, 1, 0))
                        && same(Haulage.legTonnes(plant, Good.IRON, r, 0, -3, 0), Haulage.legTonnes(plant, Good.IRON, r, 0, 0, 0))
                        && same(Haulage.legTonnes(plant, Good.IRON, r, 0, Double.NaN, 0), Haulage.legTonnes(plant, Good.IRON, r, 0, 0, 0)),
                "h = 5 as 1, -3 and NaN as 0");

        // The invariant, in both cities, in the haul itself: the tonnes the trucks are relieved of are the tonnes the railway carries and bills.
        for (Game city : new Game[] { rail, yard }) {
            boolean withYard = city == yard;
            Freight f = city.getSectors().freight();
            double h0 = f.getHomeShare(), q0 = f.getQuayShare();
            double relieved = 0, home = 0, trucksHome = 0;
            for (Sector s : city.getSectors().all()) {
                if (s == f) continue;
                for (Good g : Good.values()) {
                    if (g.traffic() != Traffic.BULK) continue;
                    double rail0 = f.railShareInForce(g), sea = f.seaShareInForce(g);
                    relieved += Haulage.legTonnes(s, g, rail0, sea, 0, 0) - Haulage.legTonnes(s, g, rail0, sea, h0, q0);
                    double bu = s.unitsBoughtLocal(g) * g.tonnesPerUnit();
                    home += bu;
                    trucksHome += bu * (1 - RELIEF * h0);
                }
            }
            HaulageCheck.haul(city);
            double railed = f.getHomeRailed() + f.getQuayRailed();
            report((withYard ? "with a port yard" : "with sidings and a depot") + ", the tonnes taken off the trucks' legs are the tonnes the railway carries",
                    relieved > 0 && h0 > 0 && (!withYard || q0 > 0) && near(relieved, railed, 1e-9),
                    String.format("%,.0f t relieved against %,.0f t railed (home %,.0f, quay %,.0f); home share %.4f, quay share %.4f",
                            relieved, railed, f.getHomeRailed(), f.getQuayRailed(), h0, q0));
            report("...and the home tonnes are the railway's and the trucks' together, none paid twice and none left out",
                    home > 0 && near(f.getHomeRailed() + trucksHome, home, 1e-9) && f.getHomeRailed() <= f.getHomeOffered() * (1 + 1e-12),
                    String.format("%,.0f t railed + %,.0f t by truck = %,.0f t bought at home", f.getHomeRailed(), trucksHome, home));
        }
    }

    /* ================================================================
       4. THE PORT YARD
       ================================================================ */

    static void portYard(Game rail, Game yard, Game spurBerth) {
        System.out.println("\n--- 4. the port yard: a station and a sea terminal both standing, and then the quay's tonnes ride rails too ---");

        // The condition, from the buildings alone.
        Object[][] cases = {
            { new String[0], false, "no building" },
            { new String[] { "Rail Depot" }, false, "a Rail Depot and no sea terminal" },
            { new String[] { "Rail Terminal" }, false, "a Rail Terminal and no sea terminal" },
            { new String[] { "Bulk Berth" }, false, "a Bulk Berth and no station" },
            { new String[] { "General Cargo Terminal" }, false, "a General Cargo Terminal and no station" },
            { new String[] { "Rail Spur", "Bulk Berth" }, false, "a siding and a berth (a spur is no station)" },
            { new String[] { "Freight Line", "Bulk Berth" }, false, "track and a berth (track is no station)" },
            { new String[] { "Rail Depot", "Bulk Berth" }, true, "a Rail Depot and a Bulk Berth" },
            { new String[] { "Rail Depot", "General Cargo Terminal" }, true, "a Rail Depot and a General Cargo Terminal" },
            { new String[] { "Rail Terminal", "Bulk Terminal" }, true, "a Rail Terminal and a Bulk Terminal" },
            { new String[] { "Rail Depot", "Rail Spur", "Bulk Berth" }, true, "a depot, a spur and a berth" },
        };
        boolean right = true;
        StringBuilder wrong = new StringBuilder();
        for (Object[] c : cases) {
            boolean got = new Bare((String[]) c[0]).fr.hasPortYard();
            if (got != (Boolean) c[1]) {
                right = false;
                wrong.append(c[2]).append(" says ").append(got).append("; ");
            }
        }
        report("hasPortYard() is true with a station and a sea terminal standing, and false with either missing, a spur or track alone not being a station",
                right, cases.length + " combinations" + (wrong.length() > 0 ? ": " + wrong : ""));
        Bare depotOnly = new Bare("Rail Depot"), berthOnly = new Bare("Bulk Berth"), spurOnly = new Bare("Rail Spur");
        assertTrue("fixture: the depot is a station and the berth a sea terminal, and the spur is neither, so the combinations above are what they say",
                depotOnly.fr.stationTonnes() > 0 && depotOnly.berthTonnes() == 0 && berthOnly.fr.stationTonnes() == 0 && berthOnly.berthTonnes() > 0
                        && spurOnly.fr.stationTonnes() == 0 && spurOnly.fr.sidingTonnes() > 0 && spurOnly.berthTonnes() == 0);

        // In the cities.
        Freight fy = yard.getSectors().freight();
        double sea = fy.seaShareInForce(Good.IRON), q = fy.getQuayShare(), h = fy.getHomeShare();
        report("a played city with a Rail Depot and a Bulk Berth: the yard stands, the ore comes by sea, and the quay share is above zero",
                fy.hasPortYard() && sea > 0 && q > 0 && h > 0,
                String.format("sea share %.3f of the ore, quay share %.4f, home share %.4f", sea, q, h));
        report("...the quay and the home tonnes are dealt together, one share for both, pro rata",
                same(fy.getHomeShare(), fy.getQuayShare()) && fy.getQuayShare() < 1,
                String.format("home %.9f, quay %.9f", fy.getHomeShare(), fy.getQuayShare()));
        Freight fr = rail.getSectors().freight();
        report("a city with a depot and spurs and no sea terminal has no yard and no quay share, while its home share is above zero",
                !fr.hasPortYard() && same(fr.getQuayShare(), 0) && same(fr.getQuayRailed(), 0) && fr.getHomeShare() > 0 && fr.seaShareInForce(Good.IRON) == 0,
                String.format("quay share %s, home share %.4f", fr.getQuayShare(), fr.getHomeShare()));
        Freight fb = spurBerth.getSectors().freight();
        report("...and a city with spurs, track and a berth but no station has no yard either, the ore going by sea all the same",
                fb.sidingTonnes() > 0 && fb.stationTonnes() == 0 && fb.seaShareInForce(Good.IRON) > 0 && !fb.hasPortYard()
                        && same(fb.getQuayShare(), 0) && same(fb.getQuayRailed(), 0) && fb.getHomeShare() > 0,
                String.format("%,.0f t of sidings, %,.0f t of stations, sea share %.3f, quay share %s", fb.sidingTonnes(), fb.stationTonnes(), fb.seaShareInForce(Good.IRON), fb.getQuayShare()));

        // The legs of a tonne that crossed a berth.
        Sector plant = yard.getSectors().heavyIndustry();
        double r = fy.railShareInForce(Good.IRON);
        double tpu = Good.IRON.tonnesPerUnit(), bought = plant.unitsBoughtLocal(Good.IRON), crossing = plant.unitsImported(Good.IRON) + plant.unitsExported(Good.IRON);
        double got = Haulage.legTonnes(plant, Good.IRON, r, sea, h, q);
        report("with the yard and a quay share q, a sea tonne's truck leg is 1 - RAIL_ROAD_RELIEF x rail - PORT_YARD_SHARE x q x sea of it",
                crossing > 0 && near(got, tpu * (bought * (1 - RELIEF * h) + crossing * (1 - RELIEF * r - YARD * q * sea)), 1e-12),
                String.format("sea %.3f, q %.4f: %,.0f t crossing, %,.0f t of legs", sea, q, tpu * crossing, got));
        report("...so the yard takes exactly PORT_YARD_SHARE x q x sea of the crossing tonnes off the legs, and nothing off the home tonnes",
                near(Haulage.legTonnes(plant, Good.IRON, r, sea, h, 0) - got, tpu * crossing * YARD * q * sea, 1e-9)
                        && same(Haulage.legTonnes(plant, Good.IRON, r, 0, h, q), Haulage.legTonnes(plant, Good.IRON, r, 0, h, 0)),
                String.format("%,.0f t off the legs; with no sea tonnes a quay share changes nothing", Haulage.legTonnes(plant, Good.IRON, r, sea, h, 0) - got));
        boolean handSet = near(Haulage.legTonnes(plant, Good.IRON, 0, 1, 0, 1), tpu * bought + tpu * crossing * (1 - YARD), 1e-12)
                && near(Haulage.legTonnes(plant, Good.IRON, 0, 1, 0, .5), tpu * bought + tpu * crossing * (1 - YARD * .5), 1e-12)
                && near(Haulage.legTonnes(plant, Good.IRON, 0, 1, 0, 0), tpu * bought + tpu * crossing, 1e-12);
        report("by hand: a tonne wholly by sea has PORT_YARD_SHARE of its leg railed at a quay share of 1, half of that at a half, none at none",
                handSet, "sea 1, rail 0");
        report("...and a leg is never below zero, whatever shares are asked of it",
                Haulage.legTonnes(plant, Good.IRON, 1, 1, 0, 1) >= tpu * bought * (1 - 1e-12)
                        && Haulage.legTonnes(plant, Good.IRON, 1, 1, 0, 1) <= tpu * bought * (1 + 1e-12),
                "rail 1 and sea 1 leave the crossing leg nought, not negative");
        // After a haul run by hand, the no-yard city's quay share is nought (the model's own shares, not the fixture's).
        HaulageCheck.haul(rail);
        HaulageCheck.haul(spurBerth);
        report("...and a haul run by hand in the cities with no yard leaves the quay share at nought",
                same(rail.getSectors().freight().getQuayShare(), 0) && same(spurBerth.getSectors().freight().getQuayShare(), 0),
                String.format("%s and %s", rail.getSectors().freight().getQuayShare(), spurBerth.getSectors().freight().getQuayShare()));
    }

    /* ================================================================
       5. THE BILL
       ================================================================ */

    static Month[] bill(Game rail, Game yard) {
        System.out.println("\n--- 5. the bill: the city's tonnes pay the railway its quote on Haulage's, in the shippers' own line, once ---");

        // A haul run by hand adds its bills to the month's statement (Sector.billForService), so the hauls of the sections before this one are
        // let go by a month played through, and the months below are one haul each.
        quietly(() -> { rail.toggleNextMonth(); yard.toggleNextMonth(); });
        Month[] months = new Month[4];
        String[] names = { "the depot-and-spurs city", "...with the home share put at .4", "the port-yard city", "...with the home share put at .4 and the quay share at .6" };
        months[0] = runMonth(rail, Double.NaN, 0);
        months[1] = runMonth(rail, .4, 0);
        months[2] = runMonth(yard, Double.NaN, 0);
        months[3] = runMonth(yard, .4, .6);
        Game[] cities = { rail, rail, yard, yard };

        for (int i = 0; i < months.length; i++) {
            Month m = months[i];
            Freight fr = cities[i].getSectors().freight();
            Plan p = m.plan;
            boolean yardCity = cities[i] == yard;
            report(names[i] + ": every shipper's \"Rail freight\" line is its boundary tonnes plus its city tonnes, to the cent",
                    p.h > 0 && p.homeTonnes > 0 && (!yardCity || (p.q > 0 && p.quayTonnes > 0)) && m.worstMiss <= 1e-9,
                    String.format("home share %.4f, quay share %.4f: %,.0f t railed at home, %,.0f t on the quay; worst miss %.2e %s",
                            p.h, p.q, p.homeTonnes, p.quayTonnes, m.worstMiss, m.worstWhere));
            report("...the lines come to Freight's rail revenue (boundary and city), to the cent",
                    m.lines > 0 && near(m.lines, m.haulageBilled, 1e-9) && near(m.lines, m.railRevenue, 1e-9),
                    String.format("lines %,.6fk, rail freight billed %,.6fk, other revenue part %,.6fk", m.lines, m.haulageBilled, m.railRevenue));
            double tonnes = m.homeRailed + m.quayRailed;
            report("...the city part is the railed city tonnes x the railway's quote x Haulage's, both read before the haul",
                    tonnes > 0 && near(m.homeBill, tonnes * p.rq * p.hq, 1e-9) && m.homeBill <= m.haulageBilled * (1 + 1e-12)
                            && m.homeRailed >= p.homeTonnes * (1 - 1e-9) && m.quayRailed >= p.quayTonnes * (1 - 1e-9),
                    String.format("%,.0f t x %.5f x D$%.6fk = %,.6fk against %,.6fk billed (of %,.6fk)", tonnes, p.rq, p.hq, tonnes * p.rq * p.hq, m.homeBill, m.haulageBilled));
        }

        // The mines sold their ore at home and pay no city bill for it.
        Sector mine = rail.getSectors().mining(), plant = rail.getSectors().heavyIndustry();
        Mining mining = rail.getSectors().mining();
        Sector.Output ore = mining.outputRow(Good.IRON);
        Plan p = months[1].plan;
        report("the mines that sold their ore at home pay the railway no city bill for it: their line is their boundary tonnes alone",
                ore != null && ore.soldLocal > 0 && p.h > 0 && same(mining.unitsExported(Good.IRON), 0) && same(p.city(mine), 0)
                        && near(railLine(mine), p.boundary.getOrDefault(mine, 0.0), 1e-9),
                String.format("the mine sold %,.0f t of ore at home; its line %,.6fk is its boundary part %,.6fk and no city part",
                        Good.IRON.tonnesPerUnit() * (ore == null ? 0 : ore.soldLocal), railLine(mine), p.boundary.getOrDefault(mine, 0.0)));
        report("...while the plant that bought the ore at home pays its home tonnes' city leg, on top of its boundary tonnes",
                p.home.getOrDefault(plant, 0.0) > 0 && p.city(plant) > 0 && near(railLine(plant), p.expected(plant), 1e-9),
                String.format("the plant's line %,.6fk = boundary %,.6fk + city %,.6fk (%,.0f t railed at home)", railLine(plant),
                        p.boundary.getOrDefault(plant, 0.0), p.city(plant), p.home.getOrDefault(plant, 0.0)));

        // The excess of Construction's top-of-month purchases is stated, not hidden.
        System.out.printf("%-96s %s  %s%n", "(Construction's repair imports billed above the rows, in the four months)", "NOTE",
                String.format("%,.6fk, %,.6fk, %,.6fk, %,.6fk", months[0].constructionExcess, months[1].constructionExcess, months[2].constructionExcess, months[3].constructionExcess));
        return months;
    }

    /* ================================================================
       6. THE DIESEL
       ================================================================ */

    static void diesel(Game plain, Month[] months) {
        System.out.println("\n--- 6. the diesel: a city tonne burns CITY_LITRES_A_TONNE, a third of a truck's leg, and the boundary's tonnes their own ---");

        report("a city tonne burns less than a truck's whole leg and far less than a boundary haul's tonne: CITY_LITRES_A_TONNE is a third of Haulage's",
                CITY_LITRES > 0 && CITY_LITRES < Haulage.DIESEL_LITRES_A_TONNE && CITY_LITRES < Freight.FUEL_LITRES_PER_TONNE
                        && near(CITY_LITRES * 3, Haulage.DIESEL_LITRES_A_TONNE, 1e-12),
                String.format("%.4f L against a truck's %.4f L and a boundary haul's %.1f L", CITY_LITRES, Haulage.DIESEL_LITRES_A_TONNE, Freight.FUEL_LITRES_PER_TONNE));
        String[] names = { "the depot-and-spurs city", "...with the home share put at .4", "the port-yard city", "...with the shares put at .4 and .6" };
        for (int i = 0; i < months.length; i++) {
            Month m = months[i];
            double tonnes = m.homeRailed + m.quayRailed;
            report(names[i] + ": the litres the city tonnes burned are their tonnes x CITY_LITRES_A_TONNE",
                    tonnes > 0 && near(m.cityLitres, tonnes * CITY_LITRES, 1e-12),
                    String.format("%,.0f t x %.4f = %,.1f L (got %,.1f)", tonnes, CITY_LITRES, tonnes * CITY_LITRES, m.cityLitres));
            report("...and the haul's diesel is the boundary tonnes' at FUEL_LITRES_PER_TONNE and the city's on top, the boundary's count untouched by them",
                    near(m.fuelLitres, m.hauled * Freight.FUEL_LITRES_PER_TONNE + m.cityLitres, 1e-9) && m.fuelLitres > m.cityLitres,
                    String.format("%,.0f t hauled x %.0f L + %,.1f L = %,.1f L (drew %,.1f)", m.hauled, Freight.FUEL_LITRES_PER_TONNE, m.cityLitres,
                            m.hauled * Freight.FUEL_LITRES_PER_TONNE + m.cityLitres, m.fuelLitres));
        }
        Freight fp = plain.getSectors().freight();
        report("a city with no railway burns nothing on city tonnes",
                same(fp.getCityLitres(), 0) && same(fp.getHomeRailed(), 0) && same(fp.getQuayRailed(), 0),
                "no city litres");
    }

    /* ================================================================
       7. THE WEIGHING
       ================================================================ */

    static void weighing(Game rail, Game yard) {
        System.out.println("\n--- 7. the weighing: a planner weighs a home-bought tonne at the steady quote x (its truck weight + the railway's quote x its rail weight) ---");

        for (Game city : new Game[] { rail, yard }) {
            boolean withYard = city == yard;
            Freight fr = city.getSectors().freight();
            Haulage haul = fr.haulage();
            // Strike the shares from the rows the last month left (HaulageCheck.weighing).
            HaulageCheck.haul(city);
            double h = fr.getHomeShare(), qs = fr.getQuayShare(), rq = fr.getQuote();
            double q = haul.getSteadyQuote();
            Haulage.LegPrice price = haul.legPrice();
            boolean right = true, belowAllTruck = true;
            StringBuilder shown = new StringBuilder();
            for (Good g : new Good[] { Good.IRON, Good.THERMAL_COAL, Good.PELLETS }) {
                double r = fr.railShareInForce(g), sea = fr.seaShareInForce(g);
                double imp = HaulageCheck.tradeShares(city, g)[0], exp = HaulageCheck.tradeShares(city, g)[1];
                double truckIn = 1 - RELIEF * r * imp - YARD * qs * sea * imp - RELIEF * h * (1 - imp);
                double railIn = RELIEF * h * (1 - imp) + YARD * qs * sea * imp;
                double truckOut = exp * Math.max(0, 1 - RELIEF * r - YARD * qs * sea);
                double railOut = exp * YARD * qs * sea;
                right &= near(price.in(g), q * (truckIn + rq * railIn), 1e-12) && near(price.out(g), q * (truckOut + rq * railOut), 1e-12);
                double allTruckIn = q * (1 - RELIEF * r * imp), allTruckOut = q * exp * (1 - RELIEF * r);
                belowAllTruck &= price.in(g) <= allTruckIn * (1 + 1e-12) && price.out(g) <= allTruckOut * (1 + 1e-12);
                shown.append(String.format("%s in %.6f out %.6f (r %.3f, sea %.3f, imp %.3f); ", g.name(), price.in(g), price.out(g), r, sea, imp));
            }
            report((withYard ? "the port-yard city" : "the depot-and-spurs city") + ": legPrice() is the steady quote x (truck weight + the railway's quote x rail weight), in and out",
                    q > 0 && h > 0 && (!withYard || qs > 0) && right && belowAllTruck,
                    String.format("steady quote %.6f, railway's %.4f, home %.4f, quay %.4f: %s", q, rq, h, qs, shown));

            // With the shares at nought and then put back: the price with the railway on the city's tonnes is lower while the railway's quote is below one.
            double impIron = HaulageCheck.tradeShares(city, Good.IRON)[0];
            double withRail = price.in(Good.IRON), withRailOut = price.out(Good.PELLETS);
            setShares(fr, 0, 0);
            double noRail = price.in(Good.IRON), noRailOut = price.out(Good.PELLETS);
            setShares(fr, h, qs);
            double back = price.in(Good.IRON);
            report("...a received tonne of the ore is cheaper to plan for with the railway on the city's tonnes than with the trucks alone, the railway's quote being below 1",
                    rq < 1 && impIron < 1 && withRail < noRail && same(back, withRail) && withRailOut <= noRailOut * (1 + 1e-12) && (!withYard || withRailOut < noRailOut),
                    String.format("%.6f against %.6f a tonne (railway's quote %.4f, %.1f%% of the ore landed); a made tonne of pellets %.6f against %.6f",
                            withRail, noRail, rq, impIron * 100, withRailOut, noRailOut));
            // The saving is the railed weight's difference at the quote: q x weight x (1 - rq).
            double h2 = fr.getHomeShare(), q2 = fr.getQuayShare();
            double sea = fr.seaShareInForce(Good.IRON);
            double railWeight = RELIEF * h2 * (1 - impIron) + YARD * q2 * sea * impIron;
            report("...by exactly the steady quote x the rail weight x (1 - the railway's quote)",
                    railWeight > 0 && near(noRail - withRail, q * railWeight * (1 - rq), 1e-9),
                    String.format("%.6f saved = %.6f x %.5f x (1 - %.4f)", noRail - withRail, q, railWeight, rq));
        }
    }

    /* ================================================================
       8. THE SAVE
       ================================================================ */

    static void save(Game yard, GameFiles files) {
        System.out.println("\n--- 8. the save: the shares in force cross a save, and the reload bills the next month as the live city does ---");

        Freight fr = yard.getSectors().freight();
        quietly(yard::toggleNextMonth);   // a month played through, to let go the bills of the hauls run by hand above
        // The shares as the city has them, and then put where a bit-for-bit copy matters: neither a 0 nor a 1.
        setShares(fr, .37 + Math.PI / 100, .61 - Math.E / 100);
        quietly(() -> yard.saveGame(10));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(10));
        back.getForeignAccounts().pinRate(1.0);
        back.getWorldEconomy().pin();
        Freight fb = back.getSectors().freight();
        report("the home and quay shares come back from the file to the bit, and the month's tonnes, bill and offer with them",
                fr.getHomeShare() > 0 && fr.getQuayShare() > 0 && same(fr.getHomeShare(), fb.getHomeShare()) && same(fr.getQuayShare(), fb.getQuayShare())
                        && same(fr.getHomeOffered(), fb.getHomeOffered()) && same(fr.getHomeRailed(), fb.getHomeRailed())
                        && same(fr.getQuayRailed(), fb.getQuayRailed()) && same(fr.getHomeBill(), fb.getHomeBill()),
                String.format("home %.17g, quay %.17g", fb.getHomeShare(), fb.getQuayShare()));
        report("...the yard stands in the loaded city as it does in the live one, and the litres, which are not saved, are none until the next haul",
                fb.hasPortYard() == fr.hasPortYard() && fb.hasPortYard() && same(fb.getCityLitres(), 0) && fr.getCityLitres() > 0,
                String.format("%,.1f L live, %,.1f L loaded", fr.getCityLitres(), fb.getCityLitres()));
        quietly(() -> { yard.toggleNextMonth(); back.toggleNextMonth(); });
        boolean linesSame = true;
        StringBuilder diff = new StringBuilder();
        for (Sector s : yard.getSectors().all()) {
            double live = railLine(s), loaded = railLine(back.getSectors().byKey(s.key()));
            if (!same(live, loaded)) {
                linesSame = false;
                diff.append(String.format("%s %s v %s; ", s.key(), live, loaded));
            }
        }
        report("the next haul bills the city's tonnes the same in the loaded city as in the live one, to the bit: every shipper's line",
                linesSame && fr.getHomeBill() > 0 && same(fr.getHomeBill(), fb.getHomeBill()) && same(fr.getHomeRailed(), fb.getHomeRailed())
                        && same(fr.getQuayRailed(), fb.getQuayRailed()) && same(fr.getHaulageBilled(), fb.getHaulageBilled()),
                diff.length() > 0 ? diff.toString() : String.format("%,.6fk billed by both (home %,.0f t, quay %,.0f t)", fr.getHomeBill(), fr.getHomeRailed(), fr.getQuayRailed()));
        report("...and the shares they strike for the month after are the same too, as are the litres burned",
                same(fr.getHomeShare(), fb.getHomeShare()) && same(fr.getQuayShare(), fb.getQuayShare()) && same(fr.getCityLitres(), fb.getCityLitres())
                        && same(fr.getQuote(), fb.getQuote()),
                String.format("home %.9f v %.9f, quay %.9f v %.9f", fr.getHomeShare(), fb.getHomeShare(), fr.getQuayShare(), fb.getQuayShare()));
    }

    /* ================================================================
       9. A REAL RUN
       ================================================================ */

    static void realRun(Game rail) {
        System.out.println("\n--- 9. a real run: LongPlaytest's city, played with its advisor, counted month by month ---");

        PrintStream was = LongPlaytest.out, real = System.out;
        PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());
        long started = System.currentTimeMillis();
        int roomMonths = 0, stationMonths = 0, homeMonths = 0, yardMonths = 0, quayMonths = 0, homeWithoutRoom = 0, quayWithoutYard = 0;
        int firstStation = 0, firstHome = 0, firstYard = 0, lastHomeMonth = 0;
        double homeTonnes = 0, quayTonnes = 0, billed = 0, litres = 0, lastShare = 0;
        Game g = new Game(GameFiles.scratch("homerailcheck-run"), LongPlaytest.founding());
        LongPlaytest.out = quiet;
        System.setOut(quiet);
        try {
            g.run();
            g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
            g.setRolloverMode(LongPlaytest.ROLLOVER);
            g.getCityMap();
            g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
            g.setFundDial(LongPlaytest.FUND_DIAL);
            Freight fr = g.getSectors().freight();
            // The founding village by hand, then the rhythm of LongPlaytest.main(): a stretch of months, the advisor's moves, two more.
            LongPlaytest.villageBuild(g, "House", 40);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            LongPlaytest.run(g, 3);
            LongPlaytest.villageBuild(g, "House", 20);
            LongPlaytest.run(g, 4);
            LongPlaytest.villageBuild(g, "Convenience Store", 2);
            LongPlaytest.villageBuild(g, "Construction Depot", 1);
            LongPlaytest.run(g, 5);
            LongPlaytest.advise(g);
            LongPlaytest.run(g, 6);
            LongPlaytest.ensureSchools(g);
            LongPlaytest.childcareWhenNeeded(g);
            int[] skips = { 100, 12, 24, 60, 6, 120 };
            int stop = 0;
            while (g.getMonth() < REAL_RUN_MONTHS) {
                stop++;
                int skip = Math.min(skips[stop % 6], REAL_RUN_MONTHS - g.getMonth());
                for (int i = 0; i < skip; i++) {
                    /*
                     * THE ROOM THE HAUL SAW (FR8, 2026-10-28). The shares are struck at the haul, early in the month, over the
                     * stations and spurs standing then (the last month's end), and are in force for the month; the distress rule
                     * (Game.runRetirement()) runs later in it. On FR8's path Freight's distress scrapped its one Rail Depot at m1160,
                     * after the haul, and the month ended with the share struck over a depot that had gone. A share with no room is
                     * one with none at the haul that struck it and none at the month's end.
                     */
                    boolean roomBefore = fr.stationTonnes() > 0 || fr.sidingTonnes() > 0, yardBefore = fr.hasPortYard();
                    LongPlaytest.run(g, 1);
                    boolean station = fr.stationTonnes() > 0, room = station || fr.sidingTonnes() > 0;
                    if (station) { stationMonths++; if (firstStation == 0) firstStation = g.getMonth(); }
                    if (room) roomMonths++;
                    if (fr.hasPortYard()) { yardMonths++; if (firstYard == 0) firstYard = g.getMonth(); }
                    if (fr.getHomeShare() > 0) {
                        homeMonths++;
                        if (firstHome == 0) firstHome = g.getMonth();
                        lastHomeMonth = g.getMonth();
                        lastShare = fr.getHomeShare();
                        if (!room && !roomBefore) homeWithoutRoom++;
                    }
                    if (fr.getQuayShare() > 0) {
                        quayMonths++;
                        if (!fr.hasPortYard() && !yardBefore) quayWithoutYard++;
                    }
                    homeTonnes += fr.getHomeRailed();
                    quayTonnes += fr.getQuayRailed();
                    billed += fr.getHomeBill();
                    litres += fr.getCityLitres();
                }
                LongPlaytest.ensureSchools(g);
                LongPlaytest.childcareWhenNeeded(g);
                for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                    if (LongPlaytest.advise(g) == null) break;
                    LongPlaytest.run(g, 1);
                }
                LongPlaytest.run(g, 2);
                // The policy dials LongPlaytest.main() turns on a rhythm of its own.
                TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
                if (stop % 11 == 0) tax.setIncomeTaxRate(tax.getIncomeTaxRate() > .25 ? .12 : tax.getIncomeTaxRate() + .06);
                if (stop % 17 == 0) tax.setPropertyTaxRate(tax.getPropertyTaxRate() > .03 ? .01 : tax.getPropertyTaxRate() + .01);
            }
        } finally {
            System.setOut(real);
            LongPlaytest.out = was;
        }
        long seconds = (System.currentTimeMillis() - started) / 1000;
        System.out.printf("  %d months in %d s: a station stood %d months (from m%d), some room %d, the port yard %d (from m%d); the home share was above zero %d months"
                        + " (from m%d, last m%d at %.1f%%) and the quay share %d; %,.0f t railed at home and %,.0f t on the quay, billed %,.0fk, %,.0f L%n",
                g.getMonth(), seconds, stationMonths, firstStation, roomMonths, yardMonths, firstYard, homeMonths, firstHome, lastHomeMonth, lastShare * 100,
                quayMonths, homeTonnes, quayTonnes, billed, litres);
        if (roomMonths > 0) {
            report("the railway took some of the city's own bulk in at least one month of the run",
                    homeMonths > 0 && homeTonnes > 0 && billed > 0 && litres > 0,
                    homeMonths + " month(s) with a home share, " + roomMonths + " with room");
            report("...and never without a station or a spur to take it, and the quay share was never above zero without a yard (at the haul that struck it or at the month's end)",
                    homeWithoutRoom == 0 && quayWithoutYard == 0, homeWithoutRoom + " month(s) of a home share with no room, " + quayWithoutYard + " of a quay share with no yard");
        } else {
            // The run is too short for the advisor to have laid any railway: say why, and hold the mechanic to the dealt case in a played fixture instead.
            System.out.printf("  the advisor laid no station and no spur in %d months (the 4,000-month trace lays its first Rail Depot at about month 860):"
                    + " the mechanic is held to the played fixture instead%n", g.getMonth());
            Freight fr = rail.getSectors().freight();
            report("the home share was above zero in the played fixture the run was too short to reach",
                    fr.getHomeShare() > 0 && fr.getHomeRailed() > 0 && fr.getHomeBill() > 0,
                    String.format("home share %.4f, %,.0f t railed", fr.getHomeShare(), fr.getHomeRailed()));
        }
    }
}
