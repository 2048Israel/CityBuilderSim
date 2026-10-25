package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Highways and rail on the slopes (ET4, slice A; docs/studies/spec-earth2-terrain.md 4.3, decision D10): a highway's
 * or a railway line's ground comes from every class of ground in the city's proportions, not from the buildings' pool
 * alone, so a mountain city's highways use its steep ground as real ones do.
 *
 * WHAT THIS HAS TO PROVE, every fixture causing the condition it tests and every figure the model's own getters
 * (slice B adds the price of the cuts, the viaducts and the tunnels, and its sections after these):
 *
 *   1. WHICH TEMPLATES ARE RUNS. Game.isRun() over the whole catalogue agrees with what the map draws as a highway or
 *      as track (BuildingVisual.Type's road() and track()); the Elevated Highway, the Rail Spur and the Freight Line are
 *      the runs, and the Rail Terminal and Depot (yards), the roads and the transit lines are buildings; no template
 *      with a site is a run.
 *   2. A RUN'S GROUND OVER THE CLASSES IN THE CITY'S PROPORTIONS. Mixed ground, 40% flat, 30% foothill, 20% steep, 10%
 *      very steep: an Elevated Highway, a Rail Spur and a Freight Line are ordered, and the runs' ground on each class
 *      is their land times the class's share of the owned ground; buildings still stand on the flat first after them,
 *      works still on the steep first of what the runs leave, and the idle steep ground is down by the runs' steep share.
 *   3. THE ROOM FOR A RUN. A city whose flat and foothill are full of buildings and whose steep ground is idle: the
 *      ground free for a run is the arithmetic of the two constraints from the getters, an order a highway over it is
 *      refused (NO_LAND) and one within it taken, and the buildings still fit after; a run whose share of the flat
 *      pushes buildings onto the foothill raises the foothill prepared by exactly the rise in what stands on it, and
 *      the next House is quoted its own pad only; a run in a run of orders is invoiced as it is charged.
 *   4. THE PRICE AND THE BOOKS. A business's run plot is priced class by class at the city's proportions and given back
 *      at the same; the books put a sector's track, and the city's highways, at the runs' mix, and summed over the
 *      sectors, the categories and the ground no one holds each class is the split's own.
 *   5. A FLAT CITY TO THE BIT. A city with neither foothill nor steep ground reads a run as a building: the ground free,
 *      the price, the ground in use, the utilisation and the preparation are the same doubles as the buildings' path.
 *   6. SAVED. landRunsSqFt goes through a save and a load to the bit; a save without it reads the standing runs' ground.
 *
 * WHY. ET2 made the land a pool of three classes with buildings on the flat and foothill only and works on any, and a
 * highway, a building of the pool, could therefore never stand on a steep slope; the spec says it does (4.3). Each
 * section works its figure out from the getters, never from a number copied from a run.
 */
public class RunWorksCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-118s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-118s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-118s %s  %,.6f against %,.6f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** The two figures agree to a rounding of the split's arithmetic, relative to the larger. */
    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static boolean near(double[] a, double[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) if (!near(a[i], b[i])) return false;
        return true;
    }

    /** The two figures are the same double, to the bit. */
    static boolean same(double a, double b) {
        return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
    }

    /** A fixture city's treasury: more than any order here costs, so a refusal is never for cash. */
    static final double CASH = 1e10;

    /** What a fixture of figures near one another can be told apart by: a rounding, not ground. */
    static final double TOL = 1e-9;

    /** The ground a fixture city owns, square feet: a Freight Line (2.4 million) and the buildings beside it fit in it. */
    static final double OWNED = 24_000_000;

    static final String HOUSE = "House", HIGHWAY = "Elevated Highway", SPUR = "Rail Spur", LINE = "Freight Line";

    /** The save folder of the fixture last founded (hills()), which section 6 saves to and loads from. */
    static GameFiles files;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        // SlopeCheck's flat fixture (section 5) prints through its own streams, set here as its main sets them.
        SlopeCheck.out = out;
        SlopeCheck.quiet = quiet;

        whichAreRuns();
        spreadOverTheClasses();
        theRoomForARun();
        thePriceAndTheBooks();
        aFlatCityToTheBit();
        saved();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       fixtures
       ===================================================================== */

    static BuildingsTemplate template(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        if (t == null) throw new IllegalStateException("no template named " + name);
        return t;
    }

    /**
     * A city founded as a harness founds one, OWNED square feet of dry ground (its founded size where `owned` is 0, which
     * is what a save keeps the ground by relief of: the relief must add up to the centre's own) of the shares given by
     * relief (flat, foothill, steep, very steep, adding up to 1; CityLand.setCentreRelief()), every sector held so no
     * business builds anything here but by a fixture's hand, the treasury CASH, the buildings of its founding put back
     * as ground in use (as the load path reads them) and no works and no runs among them.
     */
    static Game hills(String label, double owned, double flat, double foothill, double steep, double veryStep) {
        files = GameFiles.scratch(label);
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            g.setCashForTest(CASH);
            for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
        });
        LandManager lm = g.getLandManager();
        if (owned > 0) lm.restoreOwnedSqFt(owned);
        double dry = LandManager.km2(lm.getOwnedSqFt());
        g.getCityLand().setCentreRelief(new double[] { flat * dry, foothill * dry, steep * dry, veryStep * dry, 0 });
        BuildingManager bm = g.getBuildingManager();
        lm.setAllocatedSqFt(bm.getTotalLandFootprint());
        lm.restoreWorks(bm.getWorksLandFootprint(Game::standsAnywhere));
        lm.restoreRuns(bm.getWorksLandFootprint(Game::isRun));
        return g;
    }

    /** The city's own order, as the build screen places it. */
    static Game.BuildResult order(Game g, BuildingsTemplate t, int n) {
        Game.BuildResult[] r = new Game.BuildResult[1];
        quietly(() -> r[0] = g.buildStack(t, n, false));
        return r[0];
    }

    /** A business's order, built by hand from cash it is given. */
    static boolean businessOrder(Game g, String sector, BuildingsTemplate t, int n) {
        g.getEconomyManager().setSectorCash(sector, 1e9);
        boolean[] ok = new boolean[1];
        quietly(() -> ok[0] = g.buildFor(g.getSectorInvestor(sector), t, n));
        return ok[0];
    }

    /** The books as a month's close leaves them (Game.recordMonth(): the land's price and its classes pushed, the sheets pushed, the books taken), without playing the month. */
    static void closeBooks(Game g) {
        EconomyManager em = g.getEconomyManager();
        em.setLandPricePerSqFt(g.getLandManager().getPricePerSqFt());
        g.pushLandByRelief();
        em.pushBalanceSheetInputs();
        quietly(() -> g.getSectorBooks().takeMonth(g));
    }

    /** The share of the city's owned dry ground that is flat, foothill and steep (steep and very steep together): what a run's ground is spread by. */
    static double[] shares(LandManager lm) {
        double flat = lm.getFlatSqFt(), foothill = lm.getFoothillSqFt(), slope = lm.getSlopeSqFt(), total = flat + foothill + slope;
        return new double[] { flat / total, foothill / total, slope / total };
    }

    /* =====================================================================
       1. WHICH TEMPLATES ARE RUNS
       ===================================================================== */

    static void whichAreRuns() {
        out.println("--- 1. which templates are runs: the map's highway and track, never a site, never a yard ---");
        Game g = hills("runworks-1", OWNED, 0.4, 0.3, 0.2, 0.1);
        BuildingManager bm = g.getBuildingManager();
        List<String> disagree = new ArrayList<>(), runs = new ArrayList<>(), withSite = new ArrayList<>(), badUse = new ArrayList<>();
        int all = 0;
        for (BuildingsTemplate t : bm.getTemplates()) {
            all++;
            BuildingVisual.Type v = BuildingVisual.of(t);
            boolean onTheMap = v.road() == BuildingVisual.HIGHWAY || v.track();
            boolean run = Game.isRun(t);
            if (run != onTheMap) disagree.add(t.getName());
            if (run) runs.add(t.getName());
            if (run && Game.siteOf(t) != null) withSite.add(t.getName());
            LandManager.Use want = Game.siteOf(t) != null ? LandManager.Use.WORKS : run ? LandManager.Use.RUN : LandManager.Use.BUILDING;
            if (Game.groundUse(t) != want) badUse.add(t.getName());
        }
        report("Game.isRun() agrees with the map's highway and track over all " + all + " templates", disagree.isEmpty() && all > 100,
                "disagreeing: " + disagree);
        report("...the runs of the catalogue are the Elevated Highway, the Rail Spur and the Freight Line, and no other",
                runs.size() == 3 && runs.contains(HIGHWAY) && runs.contains(SPUR) && runs.contains(LINE), "runs: " + runs);
        check("no template with a site is a run, and groundUse() is WORKS for a site, RUN for a run, BUILDING for the rest",
                withSite.isEmpty() && badUse.isEmpty());
        BuildingsTemplate terminal = template(g, "Rail Terminal"), depot = template(g, "Rail Depot");
        check("the Rail Terminal and the Rail Depot are yards that sort tonnes (isStation()), not runs: buildings",
                terminal.getCategory() == BuildingType.RAIL && depot.getCategory() == BuildingType.RAIL && terminal.isStation() && depot.isStation()
                        && !Game.isRun(terminal) && !Game.isRun(depot)
                        && Game.groundUse(terminal) == LandManager.Use.BUILDING && Game.groundUse(depot) == LandManager.Use.BUILDING);
        boolean streets = true;
        for (String name : new String[] { "Gravel Road", "Paved Road", "Light Rail Line", "Metro Line" }) {
            streets &= !Game.isRun(template(g, name)) && Game.groundUse(template(g, name)) == LandManager.Use.BUILDING;
        }
        check("the Gravel and Paved Roads and the Light Rail and Metro lines are not runs", streets);
        check("a House is a building and an Iron Mine a works (the other two uses)",
                Game.groundUse(template(g, HOUSE)) == LandManager.Use.BUILDING
                        && Game.groundUse(g.getBuildingManager().getTemplate(ham.citybuildersim.sectors.Mining.IRON_MINE)) == LandManager.Use.WORKS);
    }

    /* =====================================================================
       2. A RUN'S GROUND OVER THE CLASSES IN THE CITY'S PROPORTIONS
       ===================================================================== */

    static void spreadOverTheClasses() {
        out.println("\n--- 2. a run's ground over flat, foothill and steep in the city's proportions; buildings and works on what is left ---");
        Game g = hills("runworks-2", OWNED, 0.4, 0.3, 0.2, 0.1);
        LandManager lm = g.getLandManager();
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate highway = template(g, HIGHWAY), spur = template(g, SPUR), line = template(g, LINE), house = template(g, HOUSE);
        double flat = lm.getFlatSqFt(), foothill = lm.getFoothillSqFt(), slope = lm.getSlopeSqFt();
        double[] share = shares(lm);
        double base = lm.getAllocatedSqFt();
        report("fixture: 40% flat, 30% foothill, 30% steep and very steep owned, the city hilly, no run standing yet",
                Math.abs(share[0] - 0.4) <= 1e-6 && Math.abs(share[1] - 0.3) <= 1e-6 && Math.abs(share[2] - 0.3) <= 1e-6
                        && !lm.isFlatOnly() && lm.getRunsSqFt() == 0 && lm.getWorksSqFt() == 0,
                String.format("flat %,.0f, foothill %,.0f, steep %,.0f of %,.0f sq ft owned", flat, foothill, slope, lm.getOwnedSqFt()));

        int highways = 20;
        Game.BuildResult city = order(g, highway, highways);
        boolean spurred = businessOrder(g, Sectors.FREIGHT, spur, 1);
        boolean lined = businessOrder(g, Sectors.FREIGHT, line, 1);
        double land = highways * highway.getLandSqFt() + spur.getLandSqFt() + line.getLandSqFt();
        report("the city's " + highways + " Elevated Highways and a business's Rail Spur and Freight Line go through; the pool and the runs' part of it grow by their land",
                city == Game.BuildResult.SUCCESS && spurred && lined && near(lm.getAllocatedSqFt() - base, land) && near(lm.getRunsSqFt(), land)
                        && near(lm.getRunsSqFt(), bm.getWorksLandFootprint(Game::isRun)) && lm.getWorksSqFt() == 0,
                String.format("%s, %b, %b; runs %,.0f of %,.0f sq ft", city, spurred, lined, lm.getRunsSqFt(), land));

        LandManager.ReliefUse use = lm.reliefUse();
        report("the runs' ground on each class is their land times the class's share of the owned ground: " + "flat 40%, foothill 30%, steep 30%",
                near(use.runsFlat(), land * share[0]) && near(use.runsFoothill(), land * share[1]) && near(use.runsSlope(), land * share[2])
                        && near(use.runs(), land) && near(lm.getRunsOnFlatSqFt(), use.runsFlat()) && near(lm.getRunsOnFoothillSqFt(), use.runsFoothill())
                        && near(lm.getRunsOnSlopeSqFt(), use.runsSlope()),
                String.format("flat %,.0f, foothill %,.0f, steep %,.0f of %,.0f", use.runsFlat(), use.runsFoothill(), use.runsSlope(), land));
        check("...the steep ground a run stands on is not idle: the idle steep is the steep less the runs' share, and the runs take nothing from the buildings' pool",
                near(lm.idleSlopeSqFt(), slope - use.runsSlope()) && near(use.buildings(), base));

        // Buildings: flat first, after the runs.
        double flatLeft = flat - use.runsFlat(), slopeLeft = slope - use.runsSlope();
        double b1 = 0.5 * flatLeft, b2 = 0.6 * flatLeft;
        boolean first = lm.allocate(b1, house.getSector(), LandManager.Use.BUILDING);
        LandManager.ReliefUse afterB1 = lm.reliefUse();
        report("buildings stand on the flat the runs leave, first: half of it taken, none on foothill",
                first && near(afterB1.buildingsFlat() - use.buildingsFlat(), b1) && afterB1.buildingsFoothill() == 0 && lm.getBuildingsOnFoothillSqFt() == 0,
                String.format("flat %,.0f of %,.0f left, foothill %,.0f", afterB1.buildingsFlat() - use.buildingsFlat(), flatLeft, afterB1.buildingsFoothill()));
        boolean second = lm.allocate(b2, house.getSector(), LandManager.Use.BUILDING);
        double excess = base + b1 + b2 - flatLeft;
        LandManager.ReliefUse afterB2 = lm.reliefUse();
        report("...and past it onto the foothill by exactly the excess over the flat the runs left",
                second && excess > 0 && near(lm.getBuildingsOnFoothillSqFt(), excess) && near(afterB2.buildingsFlat(), flatLeft)
                        && near(afterB2.runsFlat(), use.runsFlat()),
                String.format("excess %,.0f, on foothill %,.0f sq ft", excess, lm.getBuildingsOnFoothillSqFt()));

        // Works: steep first, on what the runs leave.
        double w1 = 0.5 * slopeLeft, w2 = 0.75 * slopeLeft;
        boolean worked = lm.allocate(w1, Sectors.MINING, LandManager.Use.WORKS);
        report("works stand on the steep the runs leave, first: half of it taken, none on foothill, the idle steep down by the runs' share and the works'",
                worked && near(lm.getWorksOnSlopeSqFt(), w1) && lm.getWorksOnFoothillSqFt() == 0 && near(lm.getWorksSqFt(), w1)
                        && near(lm.idleSlopeSqFt(), slope - use.runsSlope() - w1),
                String.format("on steep %,.0f, idle %,.0f of %,.0f steep, the runs' %,.0f", lm.getWorksOnSlopeSqFt(), lm.idleSlopeSqFt(), slope, use.runsSlope()));
        boolean past = lm.allocate(w2, Sectors.MINING, LandManager.Use.WORKS);
        check("...and past the steep the runs leave onto the foothill they leave, by exactly the excess; the runs' ground does not move",
                past && near(lm.getWorksOnSlopeSqFt(), slopeLeft) && near(lm.getWorksOnFoothillSqFt(), w1 + w2 - slopeLeft) && lm.idleSlopeSqFt() <= TOL * slope
                        && near(lm.getRunsOnSlopeSqFt(), use.runsSlope()) && near(lm.getRunsOnFlatSqFt(), use.runsFlat())
                        && near(lm.getRunsOnFoothillSqFt(), use.runsFoothill()));
        check("the split's sums hold: runs, works and buildings add up to the pool, on every class the ground in use is within the ground owned",
                near(lm.getRunsSqFt() + lm.getWorksSqFt() + lm.reliefUse().buildings(), lm.getAllocatedSqFt())
                        && lm.reliefUse().runsFlat() + lm.reliefUse().buildingsFlat() + lm.reliefUse().worksFlat() <= flat * (1 + TOL)
                        && lm.reliefUse().runsFoothill() + lm.reliefUse().buildingsFoothill() + lm.reliefUse().worksFoothill() <= foothill * (1 + TOL)
                        && lm.reliefUse().runsSlope() + lm.reliefUse().worksSlope() <= slope * (1 + TOL));
    }

    /* =====================================================================
       3. THE ROOM FOR A RUN
       ===================================================================== */

    static void theRoomForARun() {
        out.println("\n--- 3. the room for a run: the buildings and the works still fit beside it; the pads it displaces; the invoice ---");
        Game g = hills("runworks-3", OWNED, 0.4, 0.3, 0.3, 0);
        LandManager lm = g.getLandManager();
        BuildingsTemplate highway = template(g, HIGHWAY), house = template(g, HOUSE);
        double flat = lm.getFlatSqFt(), foothill = lm.getFoothillSqFt(), slope = lm.getSlopeSqFt(), owned = lm.getOwnedSqFt();
        double fillTo = 0.95 * (flat + foothill) - lm.getAllocatedSqFt();
        boolean filled = lm.allocate(fillTo, house.getSector(), LandManager.Use.BUILDING);
        report("fixture: the flat and foothill 95% full of buildings, the steep ground idle - the buildings' own room is nearly gone, the run's is bounded by it",
                filled && lm.getBuildingsOnFoothillSqFt() > 0 && near(lm.idleSlopeSqFt(), slope) && lm.getRunsSqFt() == 0
                        && lm.freeFor(house.getSector(), LandManager.Use.BUILDING) < 0.1 * (flat + foothill),
                String.format("buildings %,.0f of %,.0f flat+foothill, idle steep %,.0f, free for a building %,.0f sq ft", lm.getAllocatedSqFt(), flat + foothill,
                        lm.idleSlopeSqFt(), lm.freeFor(house.getSector(), LandManager.Use.BUILDING)));

        // The two constraints, from the getters.
        double flatFoothill = flat + foothill, sFF = flatFoothill / (flatFoothill + slope);
        double buildings = lm.getAllocatedSqFt() - lm.getWorksSqFt() - lm.getRunsSqFt();
        double room = Math.min(owned - lm.getAllocatedSqFt(), (flatFoothill - buildings) / sFF - lm.getRunsSqFt());
        String sector = highway.getSector();
        report("the ground free for a run is the smaller of the ground unbuilt and (flat and foothill less the buildings) over their share, less the runs",
                room > 0 && room < owned - lm.getAllocatedSqFt() && near(lm.freeFor(sector, LandManager.Use.RUN), room)
                        && near(lm.freeForAnyone(LandManager.Use.RUN), room),
                String.format("%,.0f sq ft, the unbuilt ground %,.0f", lm.freeFor(sector, LandManager.Use.RUN), owned - lm.getAllocatedSqFt()));
        check("...more than a building's room (a run puts a part of itself on the steep) and less than a works's, which any dry ground takes",
                lm.freeFor(sector, LandManager.Use.RUN) > lm.freeFor(sector, LandManager.Use.BUILDING)
                        && lm.freeFor(sector, LandManager.Use.RUN) < lm.freeFor(sector, LandManager.Use.WORKS));

        double each = highway.getLandSqFt();
        int fit = (int) Math.floor(room / each);
        double allocated0 = lm.getAllocatedSqFt();
        boolean can = lm.canAllocate(fit * each, sector, LandManager.Use.RUN), cannot = lm.canAllocate((fit + 1) * each, sector, LandManager.Use.RUN);
        Game.BuildResult refused = order(g, highway, fit + 1);
        report("an order of Elevated Highways one over the room is refused NO_LAND, nothing taken; canAllocate() says the same",
                fit >= 1 && can && !cannot && refused == Game.BuildResult.NO_LAND && lm.getAllocatedSqFt() == allocated0 && lm.getRunsSqFt() == 0,
                String.format("%d fit; %s", fit, refused));
        Game.BuildResult taken = order(g, highway, fit);
        double ffLeft = flatFoothill - lm.getRunsOnFlatSqFt() - lm.getRunsOnFoothillSqFt();
        double buildingsAfter = lm.getAllocatedSqFt() - lm.getWorksSqFt() - lm.getRunsSqFt();
        report("...and one at it is taken; the buildings still fit on the flat and foothill the run leaves, to within less than one more highway",
                taken == Game.BuildResult.SUCCESS && near(lm.getRunsSqFt(), fit * each) && buildingsAfter <= ffLeft * (1 + TOL)
                        && ffLeft - buildingsAfter < each * sFF + TOL * ffLeft && lm.freeFor(sector, LandManager.Use.RUN) < each,
                String.format("%s; buildings %,.0f of %,.0f left, room for a run %,.0f sq ft", taken, buildingsAfter, ffLeft, lm.freeFor(sector, LandManager.Use.RUN)));

        // The pads a run displaces.
        Game d = hills("runworks-3b", OWNED, 0.5, 0.3, 0.2, 0);
        LandManager dl = d.getLandManager();
        BuildingsTemplate dHighway = template(d, HIGHWAY), dHouse = template(d, HOUSE);
        double dFlat = dl.getFlatSqFt();
        boolean dFilled = dl.allocate(0.99 * dFlat - dl.getAllocatedSqFt(), dHouse.getSector(), LandManager.Use.BUILDING);
        double bOF0 = dl.getBuildingsOnFoothillSqFt(), prepared0 = dl.getPreparedSqFt();
        int n = 40;
        Game.BuildQuote runQuote = d.quoteBuild(dHighway, n);
        report("fixture: the buildings fill 99% of the flat, none on the foothill, no pad cut; a run quoted prepares nothing itself",
                dFilled && bOF0 == 0 && prepared0 == 0 && runQuote.prepSqFt == 0 && runQuote.prepPoints == 0 && runQuote.prepCost == 0,
                String.format("buildings on foothill %,.0f, prepared %,.0f, the run's pad %,.0f sq ft", bOF0, prepared0, runQuote.prepSqFt));
        Game.BuildResult placed = order(d, dHighway, n);
        double bOF1 = dl.getBuildingsOnFoothillSqFt(), prepared1 = dl.getPreparedSqFt();
        double displaced = (dl.getAllocatedSqFt() - dl.getWorksSqFt() - dl.getRunsSqFt()) - (dFlat - dl.getRunsOnFlatSqFt());
        report(n + " highways take their share of the flat and push buildings onto the foothill, and the foothill prepared rises by exactly that rise",
                placed == Game.BuildResult.SUCCESS && bOF1 > bOF0 && near(prepared1 - prepared0, bOF1 - bOF0) && near(bOF1, displaced),
                String.format("on foothill %,.0f -> %,.0f, prepared %,.0f -> %,.0f sq ft", bOF0, bOF1, prepared0, prepared1));
        double each2 = dHouse.getLandSqFt();
        Game.BuildQuote next = d.quoteBuild(dHouse, 1);
        report("...so the next House is quoted its own pad only, not the pads the run displaced",
                near(next.prepSqFt, each2) && next.prepSqFt < each2 + 0.5 * (bOF1 - bOF0),
                String.format("quoted %,.0f sq ft for a %,.0f sq ft House, %,.0f displaced", next.prepSqFt, each2, bOF1 - bOF0));

        // A run of orders: the invoice is what placing them charges.
        Game e = hills("runworks-3c", OWNED, 0.5, 0.3, 0.2, 0);
        LandManager el = e.getLandManager();
        BuildingsTemplate eHighway = template(e, HIGHWAY), eHouse = template(e, HOUSE);
        el.allocate(0.99 * el.getFlatSqFt() - el.getAllocatedSqFt(), eHouse.getSector(), LandManager.Use.BUILDING);
        Map<BuildingsTemplate, Integer> run = new LinkedHashMap<>();
        run.put(eHighway, n);
        run.put(eHouse, 10);
        double invoice = e.buildRunInvoice(run), alone = e.quoteBuild(eHighway, n).total + e.quoteBuild(eHouse, 10).total;
        int ahead = e.buildRunAhead(run);
        double cash0 = e.getCash();
        Game.BuildResult r1 = order(e, eHighway, n), r2 = order(e, eHouse, 10);
        double charged = cash0 - e.getCash();
        report("a run of orders - " + n + " highways, then 10 Houses - is invoiced as it is charged, to a rounding, the Houses' pad the highways displaced included",
                r1 == Game.BuildResult.SUCCESS && r2 == Game.BuildResult.SUCCESS && ahead == 2 && near(invoice, charged) && invoice > alone,
                String.format("invoice %,.6f, charged %,.6f, the two quoted alone %,.6f; %d of the run ahead", invoice, charged, alone, ahead));
    }

    /* =====================================================================
       4. THE PRICE AND THE BOOKS
       ===================================================================== */

    static void thePriceAndTheBooks() {
        out.println("\n--- 4. the price of a run's plot class by class, given back at the same; the books at the runs' mix ---");
        Game g = hills("runworks-4", OWNED, 0.4, 0.3, 0.2, 0.1);
        LandManager lm = g.getLandManager();
        EconomyManager em = g.getEconomyManager();
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate highway = template(g, HIGHWAY), spur = template(g, SPUR), house = template(g, HOUSE), diner = template(g, "Diner");
        double base = lm.getMarket().getBasePricePerSqFt(), prep = lm.getPreparationPerSqFt();
        LandClassCheck.setGoing(lm, base + 4 * prep);
        double going = lm.getPricePerSqFt(), foothillPrice = lm.getFoothillPricePerSqFt(), steepPrice = lm.getSteepPricePerSqFt();
        report("fixture: the going price four preparations over the base, so flat is dearer than foothill and foothill than steep; 40/30/30 owned",
                going > foothillPrice && foothillPrice > steepPrice && Math.abs(shares(lm)[2] - 0.3) <= 1e-6,
                String.format("flat %.6f, foothill %.6f, steep %.6f a sq ft", going, foothillPrice, steepPrice));

        double[] share = shares(lm);
        double plot = spur.getLandSqFt(), perSqFt = share[0] * going + share[1] * foothillPrice + share[2] * steepPrice;
        String freight = spur.getSector();
        close("a run's plot is priced class by class at the city's proportions: its land times the shares of each class's price",
                lm.priceFor(plot, freight, LandManager.Use.RUN), plot * perSqFt, TOL);
        close("...and given back at the same shares (buybackPriceFor())", lm.buybackPriceFor(plot, freight, LandManager.Use.RUN), plot * perSqFt, TOL);
        double[] parts = lm.plotByRelief(plot, LandManager.Use.RUN);
        check("...its parts are its own, summing to the plot - flat, foothill and steep in the city's proportions, and not what the buildings it displaces would be",
                near(parts[0] + parts[1] + parts[2], plot) && near(parts[0], plot * share[0]) && near(parts[1], plot * share[1]) && near(parts[2], plot * share[2])
                        && near(lm.releasedByRelief(plot, LandManager.Use.RUN), parts));
        check("...cheaper than a building's plot of the same size while the flat is free (a third of it is steep, at the base price)",
                lm.priceFor(plot, freight, LandManager.Use.RUN) < lm.priceFor(plot, freight, LandManager.Use.BUILDING)
                        && same(lm.priceFor(plot, freight, LandManager.Use.BUILDING), plot * going));

        double sold0 = lm.getLandSalesThisMonth(), expected = lm.priceFor(plot, freight, LandManager.Use.RUN);
        boolean spurred = businessOrder(g, Sectors.FREIGHT, spur, 1);
        close("a business's Rail Spur order (buildFor()) pays the city exactly that for its plot", lm.getLandSalesThisMonth() - sold0, expected, TOL);

        // A city with the lot: highways, houses, diners and the spur, the books closed.
        int highways = 10, houses = 200, diners = 30;
        Game.BuildResult hw = order(g, highway, highways);
        boolean built = spurred && hw == Game.BuildResult.SUCCESS && businessOrder(g, Sectors.REAL_ESTATE, house, houses)
                && businessOrder(g, Sectors.RESTAURANTS, diner, diners);
        closeBooks(g);
        LandManager.ReliefUse use = lm.reliefUse();
        report("fixture: a city of " + highways + " highways, a Rail Spur, " + houses + " houses and " + diners + " diners on mixed ground, the books closed and reading it as hilly",
                built && em.valuesByRelief() && !lm.isFlatOnly() && near(lm.getAllocatedSqFt(), bm.getTotalLandFootprint())
                        && near(use.runs(), bm.getWorksLandFootprint(Game::isRun)) && use.runsSlope() > 0,
                String.format("runs %,.0f (flat %,.0f, foothill %,.0f, steep %,.0f), buildings %,.0f sq ft", use.runs(), use.runsFlat(), use.runsFoothill(),
                        use.runsSlope(), use.buildings()));

        // The Freight sector holds the spur and nothing else: its land is its track at the runs' mix.
        double spurLand = bm.getLandSqFtBySector(Sectors.FREIGHT);
        double[] freightAreas = em.landByReliefOf(g.getSectors().byKey(Sectors.FREIGHT));
        double[] atRunsMix = { spurLand * use.runsFlat() / use.runs(), spurLand * use.runsFoothill() / use.runs(), spurLand * use.runsSlope() / use.runs() };
        report("the books put the Freight sector's track at the city's runs' mix - flat, foothill and steep - and not at the buildings'",
                near(spurLand, spur.getLandSqFt()) && near(freightAreas, atRunsMix) && freightAreas[2] > 0,
                String.format("flat %,.0f, foothill %,.0f, steep %,.0f of %,.0f sq ft", freightAreas[0], freightAreas[1], freightAreas[2], spurLand));

        // Summed over the sectors, the ground no one holds and the categories, each class is the split's own.
        BuildingType infra = BuildingType.INFRASTRUCTURE;
        double highwayLand = bm.getWorksLandSqFtByCategory(infra, Game::isRun);
        double[] categoryAreas = em.landByReliefOf(infra);
        double others = bm.getLandSqFtByCategory(infra) - highwayLand;
        double buildingsAll = use.buildings();
        double[] categoryWant = { highwayLand * use.runsFlat() / use.runs() + others * use.buildingsFlat() / buildingsAll,
                highwayLand * use.runsFoothill() / use.runs() + others * use.buildingsFoothill() / buildingsAll,
                highwayLand * use.runsSlope() / use.runs() };
        report("the city's highways, in the Infrastructure category, are put at the runs' mix too, the rest of the category at the buildings'",
                near(highwayLand, highways * highway.getLandSqFt()) && near(categoryAreas, categoryWant),
                String.format("flat %,.0f, foothill %,.0f, steep %,.0f of %,.0f sq ft", categoryAreas[0], categoryAreas[1], categoryAreas[2], bm.getLandSqFtByCategory(infra)));
        double[] sectorsSum = new double[3];
        double sectorLand = 0, sectorRuns = 0;
        for (Sector s : g.getSectors().all()) {
            double[] a = em.landByReliefOf(s);
            for (int c = 0; c < 3; c++) sectorsSum[c] += a[c];
            sectorLand += bm.getLandSqFtBySector(s.key());
            sectorRuns += bm.getWorksLandSqFtBySector(s.key(), Game::isRun);
        }
        // ...and the ground no business holds: the highways at the runs' mix, the bank's branches and the rest at the buildings'.
        double unheldRuns = bm.getWorksLandFootprint(Game::isRun) - sectorRuns, unheld = bm.getTotalLandFootprint() - sectorLand - unheldRuns;
        double[] rest = { unheldRuns * use.runsFlat() / use.runs() + unheld * use.buildingsFlat() / buildingsAll,
                unheldRuns * use.runsFoothill() / use.runs() + unheld * use.buildingsFoothill() / buildingsAll,
                unheldRuns * use.runsSlope() / use.runs() };
        double[] all3 = { sectorsSum[0] + rest[0], sectorsSum[1] + rest[1], sectorsSum[2] + rest[2] };
        double[] split = { use.buildingsFlat() + use.worksFlat() + use.runsFlat(), use.buildingsFoothill() + use.worksFoothill() + use.runsFoothill(),
                use.worksSlope() + use.runsSlope() };
        report("summed over every sector and the ground no business holds, the areas by class are the split's own (LandManager.reliefUse())",
                unheld >= 0 && unheldRuns >= 0 && near(all3, split),
                String.format("flat %,.0f, foothill %,.0f, steep %,.0f sq ft; the split's %,.0f, %,.0f, %,.0f; no business's %,.0f of runs, %,.0f of buildings",
                        all3[0], all3[1], all3[2], split[0], split[1], split[2], unheldRuns, unheld));
        double[] categoriesSum = new double[3];
        for (BuildingType c : BuildingType.values()) {
            double[] a = em.landByReliefOf(c);
            for (int k = 0; k < 3; k++) categoriesSum[k] += a[k];
        }
        check("...and summed over the city's categories likewise", near(categoriesSum, split));
        double[] prices = { em.priceOfClass(0), em.priceOfClass(1), em.priceOfClass(2) };
        close("a category's land is valued at its areas times the class prices: the highways' at the runs' mix", em.landValueOf(infra),
                categoryAreas[0] * prices[0] + categoryAreas[1] * prices[1] + categoryAreas[2] * prices[2], TOL);
    }

    /* =====================================================================
       5. A FLAT CITY TO THE BIT
       ===================================================================== */

    static void aFlatCityToTheBit() {
        out.println("\n--- 5. a city with neither foothill nor steep ground reads a run as a building, to the bit ---");
        Game g = SlopeCheck.city("runworks-5a", 1, 0, 0, 0);
        g.setCashForTest(CASH);
        LandManager lm = g.getLandManager();
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate highway = template(g, HIGHWAY), line = template(g, LINE), house = template(g, HOUSE);
        lm.restoreWorks(0);
        lm.restoreRuns(0);
        int highways = 3;
        Game.BuildResult hw = order(g, highway, highways);
        boolean lined = businessOrder(g, Sectors.FREIGHT, line, 1);
        boolean housed = businessOrder(g, Sectors.REAL_ESTATE, house, 5);
        String sector = line.getSector(), hSector = highway.getSector();
        double runs = highways * highway.getLandSqFt() + line.getLandSqFt();
        report("fixture: an all-flat city (neither foothill nor steep ground) with " + highways + " highways, a Freight Line and 5 Houses standing",
                lm.isFlatOnly() && hw == Game.BuildResult.SUCCESS && lined && housed && near(lm.getRunsSqFt(), runs)
                        && near(lm.getRunsSqFt(), bm.getWorksLandFootprint(Game::isRun)),
                String.format("%s, %b, %b; the runs hold %,.0f sq ft of %,.0f", hw, lined, housed, lm.getRunsSqFt(), lm.getAllocatedSqFt()));

        double x = line.getLandSqFt() / 3;
        check("the ground free for a run is the ground free for a building, to the bit (freeFor(), freeForAnyone())",
                same(lm.freeFor(sector, LandManager.Use.RUN), lm.freeFor(sector, LandManager.Use.BUILDING))
                        && same(lm.freeFor(hSector, LandManager.Use.RUN), lm.freeFor(hSector, LandManager.Use.BUILDING))
                        && same(lm.freeForAnyone(LandManager.Use.RUN), lm.freeForAnyone(LandManager.Use.BUILDING))
                        && same(lm.freeForAnyone(LandManager.Use.RUN), lm.freeForAnyone(false)));
        check("...the price of a run's plot and what is given back for it are a building's, to the bit",
                same(lm.priceFor(x, sector, LandManager.Use.RUN), lm.priceFor(x, sector, LandManager.Use.BUILDING))
                        && same(lm.priceFor(x, sector, LandManager.Use.RUN), x * lm.getPricePerSqFt())
                        && same(lm.buybackPriceFor(x, sector, LandManager.Use.RUN), lm.buybackPriceFor(x, sector, LandManager.Use.BUILDING)));
        check("...its plot on the classes is all flat and a run prepares nothing: the same as a building's",
                java.util.Arrays.equals(lm.plotByRelief(x, LandManager.Use.RUN), lm.plotByRelief(x, LandManager.Use.BUILDING))
                        && java.util.Arrays.equals(lm.releasedByRelief(x, LandManager.Use.RUN), lm.releasedByRelief(x, LandManager.Use.BUILDING))
                        && lm.foothillToPrepareSqFt(x, sector, LandManager.Use.RUN) == 0 && lm.foothillToPrepareSqFt(x, sector, LandManager.Use.BUILDING) == 0);

        // The runs' ground makes no difference to any figure: taken away, every reading is the same one.
        LandManager.ReliefUse use = lm.reliefUse();
        double utilisation = lm.getUtilisation(), share = lm.getUsedShare(), free = lm.freeFor(sector, LandManager.Use.BUILDING);
        double prepared = lm.getPreparedSqFt(), bOF = lm.getBuildingsOnFoothillSqFt(), idle = lm.idleSlopeSqFt();
        double keep = lm.getRunsSqFt();
        lm.restoreRuns(0);
        check("the ground in use, the utilisation, the used share, the ground free and the preparation are the same doubles whether the runs are known or not",
                use.equals(lm.reliefUse()) && use.runs() == 0 && same(utilisation, lm.getUtilisation()) && same(share, lm.getUsedShare())
                        && same(free, lm.freeFor(sector, LandManager.Use.BUILDING)) && same(prepared, lm.getPreparedSqFt())
                        && same(bOF, lm.getBuildingsOnFoothillSqFt()) && same(idle, lm.idleSlopeSqFt()) && lm.getRunsOnFlatSqFt() == 0
                        && lm.getRunsOnFoothillSqFt() == 0 && lm.getRunsOnSlopeSqFt() == 0);
        lm.restoreRuns(keep);

        // ...and the same ground taken as buildings, on a twin city, reads the same.
        Game twin = SlopeCheck.city("runworks-5b", 1, 0, 0, 0);
        LandManager tm = twin.getLandManager();
        tm.restoreWorks(0);
        tm.restoreRuns(0);
        boolean took = tm.allocate(lm.getAllocatedSqFt(), sector, LandManager.Use.BUILDING);
        check("a twin city that took the same ground as buildings reads the same, to the bit: the use, the utilisation and the ground free",
                took && lm.reliefUse().equals(tm.reliefUse()) && same(lm.getUtilisation(), tm.getUtilisation()) && same(lm.getUsedShare(), tm.getUsedShare())
                        && same(lm.freeFor(sector, LandManager.Use.RUN), tm.freeFor(sector, LandManager.Use.RUN))
                        && same(lm.freeFor(sector, LandManager.Use.BUILDING), tm.freeFor(sector, LandManager.Use.BUILDING))
                        && same(lm.getAvailableSqFt(), tm.getAvailableSqFt()));
        // A run taken in a flat city is a run in the books' count of ground too: the books put it where the buildings are, all on flat.
        closeBooks(g);
        double[] areas = g.getEconomyManager().landByReliefOf(g.getSectors().byKey(Sectors.FREIGHT));
        check("the books of a flat city value every square foot at the one price: the Freight sector's land is all flat, as ever",
                !g.getEconomyManager().valuesByRelief() && areas[0] == bm.getLandSqFtBySector(Sectors.FREIGHT) && areas[1] == 0 && areas[2] == 0);
    }

    /* =====================================================================
       6. SAVED
       ===================================================================== */

    static void saved() throws Exception {
        out.println("\n--- 6. the runs' ground is saved: through a save and a load to the bit; a save without it reads the standing runs ---");
        Game g = hills("runworks-6", 0, 0.4, 0.3, 0.2, 0.1);
        LandManager lm = g.getLandManager();
        BuildingManager bm = g.getBuildingManager();
        boolean built = order(g, template(g, HIGHWAY), 10) == Game.BuildResult.SUCCESS && businessOrder(g, Sectors.FREIGHT, template(g, SPUR), 1);
        // The load allocates the city's footprint (Game's load path), so the city is given it first, and the runs a figure that the
        // buildings' footprint is not, so that what the load brings back is the saved key and nothing it could have worked out.
        lm.setAllocatedSqFt(bm.getTotalLandFootprint());
        double footprint = bm.getWorksLandFootprint(Game::isRun), fiat = 0.75 * footprint;
        lm.restoreRuns(fiat);
        GameFiles saved = files;
        quietly(() -> g.saveGame(10, "runworks"));
        Game loaded = new Game(saved);
        quietly(() -> loaded.loadGameSave(10));
        LandManager ll = loaded.getLandManager();
        report("fixture: highways and a Rail Spur standing, the runs' ground a figure the footprint is not",
                built && fiat > 0 && !same(fiat, footprint) && same(lm.getRunsSqFt(), fiat) && !lm.isFlatOnly(),
                String.format("footprint %,.0f, saved as %,.0f sq ft", footprint, fiat));
        report("through a save and a load the runs' ground is back, to the bit",
                same(ll.getRunsSqFt(), fiat) && same(ll.getRunsSqFt(), lm.getRunsSqFt()),
                String.format("%,.6f saved, %,.6f loaded", fiat, ll.getRunsSqFt()));
        report("...and so is everything read off it: the ground in use by use and class, the runs on each class, the idle steep",
                lm.reliefUse().equals(ll.reliefUse()) && same(lm.getRunsOnSlopeSqFt(), ll.getRunsOnSlopeSqFt())
                        && same(lm.getRunsOnFlatSqFt(), ll.getRunsOnFlatSqFt()) && same(lm.idleSlopeSqFt(), ll.idleSlopeSqFt())
                        && same(lm.getPreparedSqFt(), ll.getPreparedSqFt()),
                String.format("allocated %,.3f and %,.3f, works %,.3f and %,.3f, prepared %,.3f and %,.3f; %s and %s", lm.getAllocatedSqFt(), ll.getAllocatedSqFt(),
                        lm.getWorksSqFt(), ll.getWorksSqFt(), lm.getPreparedSqFt(), ll.getPreparedSqFt(), lm.reliefUse(), ll.reliefUse()));

        // The same save with the key taken out, as every save before ET4 is.
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(Files.readString(saved.saveFile(10))).getAsJsonObject();
        boolean had = json.has("landRunsSqFt");
        json.remove("landRunsSqFt");
        Files.writeString(saved.saveFile(10), new com.google.gson.Gson().toJson(json));
        Game without = new Game(saved);
        quietly(() -> without.loadGameSave(10));
        double standing = without.getBuildingManager().getWorksLandFootprint(Game::isRun);
        report("a save without the key reads the standing highways' and railway lines' ground, as the allocation is read from the buildings",
                had && standing > 0 && same(without.getLandManager().getRunsSqFt(), standing) && !same(without.getLandManager().getRunsSqFt(), fiat),
                String.format("the key was there: %b; read back %,.0f sq ft of %,.0f standing", had, without.getLandManager().getRunsSqFt(), standing));
    }
}
