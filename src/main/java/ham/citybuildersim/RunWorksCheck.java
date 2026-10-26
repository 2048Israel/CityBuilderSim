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
 * Highways and rail on the slopes (ET4, slices A and B; docs/studies/spec-earth2-terrain.md 4.3, decision D10): a
 * highway's or a railway line's ground comes from every class of ground in the city's proportions, not from the
 * buildings' pool alone, so a mountain city's highways use its steep ground as real ones do; and an order for one pays
 * for the cuts, the viaducts and the tunnels that ground needs, at the city's own mix of it.
 *
 * WHAT THIS HAS TO PROVE, every fixture causing the condition it tests and every figure the model's own getters
 * (sections 1 to 6 are slice A's, the land; 7 to 12 slice B's, the price of the works; 13 and 14 slice C's, the map):
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
 *   7. THE RATES. RunWorks' dollars a m2 on each class against their constants: none on the flat, a highway's foothill
 *      a pad's, foothill under steep under very steep, a railway's a highway's times RAIL_FACTOR and above it on every
 *      class above flat; the rate at a mix is the weighted sum written from the rates, and the points are its arithmetic.
 *   8. A RUN OVER EACH CLASS. A city of one class of ground, an Elevated Highway and a Freight Line quoted: the works
 *      are the land at the class's rate in points at the template's own founding price, their money the builders'
 *      price of those points, and the quote exceeds the same city's flat quote by exactly that money, taxed with the work.
 *   9. THE MIX. A city of all four classes: the works at the rate of its mix of dry ground, the mix the quote reports,
 *      a railway's a m2 above a highway's by RAIL_FACTOR; and what is no run carries no works at all.
 *  10. BUILDERS' WORK IN THE BOOKS. The city's highways and the Freight sector's Freight Line through the real paths:
 *      the works owed on the site before the first building finishes, billed to Construction and earned as the crews
 *      deliver them, the money audit and the GDP identity closing month by month; the investor's price includes them.
 *  11. A FLAT CITY TO THE BIT. On all-flat ground a run's quote, its order, the books and the investor's price are what
 *      they were before ET4: no works, no line, the counters at nothing.
 *  12. THE SCREEN'S WORDS. BuildScreen.worksWords() reads the quote's fields, the mix among them; nothing on flat ground.
 *  13. ON THE MAP. A tile with an east-west highway and a north-south track each crossing flat, steep, very steep and flat
 *      ground: Painted.bridge is true on exactly the runs' steep plots and Painted.tunnel on exactly their very steep plots,
 *      neither on the flat; at 8 px a plot (and 4) a flat plot of the highway has its surface in the middle, a viaduct's
 *      plot is cased in the deck's colour and the track's has its deck beside the bed, a tunnel's plot is its ground with
 *      the run's dashed line, and TUNNEL_PORTAL pixels lie at each tunnel's two ends and nowhere inside it; a plot of the
 *      same relief with no run is drawn as it is without the runs; in the blocks' views a tunnel is its ground and the
 *      run's colour on every other plot; the hover says "a viaduct" and "a tunnel".
 *  14. THE STREETS' RULE DID NOT MOVE. A street plot on fresh water is still a bridge and no tunnel, a street plot on steep
 *      or very steep ground neither (the plans never lay one there), a run plot on foothill neither, and a run over fresh
 *      water a bridge and never a tunnel, whatever relief the plot carries.
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
        theRates();
        overEachClass();
        theMix();
        inTheBooks();
        aFlatCityPaysNoWorks();
        theScreensWords();
        onTheMap();
        theStreetsRuleDidNotMove();

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
            // ...a works is a template with a site, and since HY1 the Hydro Station, which has no site but stands on any ground, a dam's wings on the valley's sides.
            LandManager.Use want = Game.siteOf(t) != null || t.isHydro() ? LandManager.Use.WORKS : run ? LandManager.Use.RUN : LandManager.Use.BUILDING;
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

    /* =====================================================================
       slice B: the price of the cuts, the viaducts and the tunnels
       ===================================================================== */

    /** The cash a business is given to pay a run's order from by fiat, thousands: the Freight Line's price and more, in a figure small enough for the sectors' statements to be exact to the cent. */
    static final double BUSINESS_CASH = 4e6;

    /** The city's ground made all flat, as SlopeCheck 4 does it: the relief is read live, so the same order can be asked again as a flat city's. */
    static void flatten(Game g) {
        double dry = g.getCityLand().totalKm2(CityLand.DRY);
        g.getCityLand().setCentreRelief(new double[] { dry, 0, 0, 0, 0 });
    }

    /** The city's own dry ground by relief (CityLand.setCentreRelief()'s five figures, km2), to put back after a fixture has been flattened. */
    static double[] reliefOf(Game g) {
        double[] relief = new double[CityLand.RELIEF_AREAS];
        for (int i = 0; i < relief.length; i++) relief[i] = g.getCityLand().centreReliefKm2(i);
        return relief;
    }

    /** The founding cash a point of a template's own work, thousands: what RunWorks.points() is struck at. */
    static double perPoint(BuildingsTemplate t) {
        return t.getFoundingCashCost() / (double) t.getConstructionPoints();
    }

    /** The dollars a m2 of the run's land that a quote's works come to: its points at the founding price a point, over the land it priced. */
    static double usdPerM2Of(Game.BuildQuote q, BuildingsTemplate t) {
        return q.worksPoints * perPoint(t) * 1000 / (q.worksSqFt * LandManager.SQ_M_PER_SQ_FT);
    }

    /* =====================================================================
       7. THE RATES
       ===================================================================== */

    static void theRates() {
        out.println("\n--- 7. the works a m2 of a run's land needs on each class: RunWorks' rates against their constants ---");
        for (int net = 0; net < 2; net++) {
            boolean rail = net == 1;
            String who = rail ? "a railway's" : "a highway's";
            check(who + " flat m2 needs no works", RunWorks.usdPerM2(World.FLAT, rail) == 0);
            double foothill = RunWorks.usdPerM2(World.FOOTHILL, rail), steep = RunWorks.usdPerM2(World.STEEP, rail),
                    very = RunWorks.usdPerM2(World.VERY_STEEP, rail);
            report("...and the works rise with the class: foothill under steep under very steep, each above nothing",
                    foothill > 0 && foothill < steep && steep < very,
                    String.format("%s: $%,.1f, $%,.1f, $%,.1f a m2", who, foothill, steep, very));
        }
        report("a highway's foothill m2 is a foothill pad's, SitePreparation's dollars a m2",
                same(RunWorks.usdPerM2(World.FOOTHILL, false), SitePreparation.USD_PER_M2) && RunWorks.FOOTHILL_USD_PER_M2 == SitePreparation.USD_PER_M2,
                String.format("$%,.4f against $%,.4f", RunWorks.usdPerM2(World.FOOTHILL, false), SitePreparation.USD_PER_M2));
        check("...its steep m2 is a viaduct's deck over the run's width, its very steep m2 the bores of a tunnel over the same",
                same(RunWorks.usdPerM2(World.STEEP, false), RunWorks.DECK_USD_PER_M2 * RunWorks.DECK_WIDTH_M / RunWorks.RUN_WIDTH_M)
                        && same(RunWorks.usdPerM2(World.VERY_STEEP, false),
                                RunWorks.TUNNEL_BORES * RunWorks.TUNNEL_BORE_USD_PER_KM / (1000 * RunWorks.RUN_WIDTH_M))
                        && RunWorks.RUN_WIDTH_M == SitePreparation.PAD_WIDTH_M);
        check("a railway's rate is a highway's times RAIL_FACTOR on every class above flat, and above it; RAIL_FACTOR is the grades' ratio",
                RunWorks.RAIL_FACTOR == RunWorks.HIGHWAY_GRADE / RunWorks.RAIL_GRADE && RunWorks.RAIL_FACTOR > 1
                        && same(RunWorks.usdPerM2(World.FOOTHILL, true), RunWorks.usdPerM2(World.FOOTHILL, false) * RunWorks.RAIL_FACTOR)
                        && same(RunWorks.usdPerM2(World.STEEP, true), RunWorks.usdPerM2(World.STEEP, false) * RunWorks.RAIL_FACTOR)
                        && same(RunWorks.usdPerM2(World.VERY_STEEP, true), RunWorks.usdPerM2(World.VERY_STEEP, false) * RunWorks.RAIL_FACTOR)
                        && RunWorks.usdPerM2(World.FOOTHILL, true) > RunWorks.usdPerM2(World.FOOTHILL, false)
                        && RunWorks.usdPerM2(World.STEEP, true) > RunWorks.usdPerM2(World.STEEP, false)
                        && RunWorks.usdPerM2(World.VERY_STEEP, true) > RunWorks.usdPerM2(World.VERY_STEEP, false));

        // The rate at a mix is each class's share of the four times that class's rate, in any unit of area.
        double[][] mixes = { { 40, 30, 20, 10 }, { 4, 3, 2, 1 }, { 0.5, 0.25, 0.125, 0.125 }, { 0, 1, 0, 0 }, { 0, 0, 3, 0 }, { 0, 0, 0, 7 }, { 9, 0, 1, 0 } };
        boolean weighted = true;
        String wrong = "";
        for (double[] m : mixes) {
            double total = m[0] + m[1] + m[2] + m[3];
            for (int net = 0; net < 2; net++) {
                boolean rail = net == 1;
                double want = (m[1] * RunWorks.usdPerM2(World.FOOTHILL, rail) + m[2] * RunWorks.usdPerM2(World.STEEP, rail)
                        + m[3] * RunWorks.usdPerM2(World.VERY_STEEP, rail)) / total;
                double got = RunWorks.usdPerM2OfMix(m[0], m[1], m[2], m[3], rail);
                if (!near(got, want)) { weighted = false; wrong += String.format(" %s%s: %.6f against %.6f;", java.util.Arrays.toString(m), rail ? " rail" : "", got, want); }
            }
        }
        report("the rate at a mix of ground is the weighted sum of the classes' rates, in any unit of area, for a highway and a railway",
                weighted, weighted ? mixes.length + " mixes, both nets" : wrong);
        check("...none where the mix holds no ground, none above flat, or only flat; a negative area counts for nothing",
                RunWorks.usdPerM2OfMix(0, 0, 0, 0, false) == 0 && RunWorks.usdPerM2OfMix(5, 0, 0, 0, true) == 0
                        && near(RunWorks.usdPerM2OfMix(-5, 2, 0, 0, false), RunWorks.usdPerM2(World.FOOTHILL, false))
                        && RunWorks.usdPerM2OfMix(-5, -1, 0, 0, true) == 0);
        check("...a mix of one class above flat is that class's own rate; flat ground in the mix dilutes it, steeper ground raises it",
                near(RunWorks.usdPerM2OfMix(0, 0, 8, 0, true), RunWorks.usdPerM2(World.STEEP, true))
                        && near(RunWorks.usdPerM2OfMix(1, 1, 0, 0, false), RunWorks.usdPerM2(World.FOOTHILL, false) / 2)
                        && RunWorks.usdPerM2OfMix(1, 1, 0, 0, false) < RunWorks.usdPerM2OfMix(0, 1, 0, 0, false)
                        && RunWorks.usdPerM2OfMix(0, 1, 1, 0, false) > RunWorks.usdPerM2OfMix(0, 1, 0, 0, false));

        // The points are the dollars in thousands over the template's own founding price a point.
        double land = 63_000, price = 0.96;
        double usd = RunWorks.usdPerM2(World.STEEP, false);
        close("the points of a run's works are its square metres at the rate, in thousands, over the founding cash a point",
                RunWorks.points(land, usd, price), land * LandManager.SQ_M_PER_SQ_FT * usd / 1000 / price, TOL);
        check("...twice the land, twice the points; a dearer point, fewer",
                near(RunWorks.points(2 * land, usd, price), 2 * RunWorks.points(land, usd, price))
                        && RunWorks.points(land, usd, 2 * price) < RunWorks.points(land, usd, price));
        check("...none for no land, no works, or a price a point that is not a positive figure",
                RunWorks.points(0, usd, price) == 0 && RunWorks.points(land, 0, price) == 0 && RunWorks.points(land, usd, 0) == 0
                        && RunWorks.points(land, usd, -1) == 0 && RunWorks.points(land, usd, Double.NaN) == 0
                        && RunWorks.points(land, usd, Double.POSITIVE_INFINITY) == 0 && RunWorks.points(-land, usd, price) == 0);
    }

    /* =====================================================================
       8. A RUN OVER EACH CLASS OF GROUND
       ===================================================================== */

    static void overEachClass() {
        out.println("\n--- 8. a run over each class of ground: the works at the class's rate, in points, owed on the site and in the sticker ---");
        int n = 2;
        double[][] points = new double[2][World.RELIEFS], dollars = new double[2][World.RELIEFS];
        for (int relief = World.FLAT; relief < World.RELIEFS; relief++) {
            double[] share = new double[World.RELIEFS];
            share[relief] = 1;
            Game g = SlopeCheck.city("runworks-8-" + relief, share[0], share[1], share[2], share[3]);
            EconomyManager em = g.getEconomyManager();
            String ground = SlopeCheck.GROUND[relief];
            double[] hilly = reliefOf(g);
            for (int net = 0; net < 2; net++) {
                boolean rail = net == 1;
                BuildingsTemplate t = template(g, rail ? LINE : HIGHWAY);
                String run = "an order of " + n + " x " + t.getName() + " on all-" + ground + " ground";
                double nm = g.getBuildingManager().nonMaterialCost(t), land = t.getLandSqFt() * n;
                Game.BuildQuote q = g.quoteBuild(t, n);
                if (relief == World.FLAT) {
                    report(run + ": no works at all - no land priced, no points, no money, no mix - and the sticker is the builders' work alone",
                            q.worksSqFt == 0 && q.worksPoints == 0 && q.worksCost == 0 && java.util.Arrays.equals(q.worksMix, new double[4])
                                    && q.prepPoints == 0 && same(q.sticker, nm * n) && Game.isRun(t),
                            String.format("sticker %,.3f of %,.3f", q.sticker, nm * n));
                    continue;
                }
                double want = RunWorks.points(land, RunWorks.usdPerM2(relief, rail), perPoint(t));
                close(run + ": the works are its land at the class's rate, in points at the template's own founding price a point",
                        q.worksPoints, want, TOL);
                boolean wholeClass = near(q.worksMix[relief], 1);
                for (int i = 0; i < World.RELIEFS; i++) if (i != relief && q.worksMix[i] != 0) wholeClass = false;
                check("...on the run's whole land, at a mix that is this class whole, and the order prepares no pad of its own",
                        want > 0 && near(q.worksSqFt, land) && wholeClass && q.prepSqFt == 0 && q.prepPoints == 0);
                close("...their money is those points at the builders' price a point of the run's own work today (non-material cost over points)",
                        q.worksCost, q.worksPoints * nm / t.getConstructionPoints(), TOL);
                check("...in the sticker, summed as the quote sums it: the builders' work, then the works' money", same(q.sticker, nm * n + q.worksCost));
                points[net][relief] = q.worksPoints;
                dollars[net][relief] = usdPerM2Of(q, t);

                // The same city, flat: the relief is read live, so the order is quoted again as a flat city's.
                flatten(g);
                Game.BuildQuote flat = g.quoteBuild(t, n);
                check("...the same order quoted on flat has none, and is the builders' work alone",
                        flat.worksPoints == 0 && flat.worksCost == 0 && same(flat.sticker, nm * n));
                close("...so the total exceeds the flat one by the works' money grossed up by the builders' sales tax, as all the work is",
                        q.total - flat.total, em.withBuildersTax(q.worksCost), TOL);
                check("...and the wait is longer: the works are owed on the site before the first building finishes (quote months)",
                        q.months > flat.months);
                g.getCityLand().setCentreRelief(hilly);
            }
        }
        check("the works rise with the class of ground, for a highway and for a railway: foothill under steep under very steep",
                points[0][1] > 0 && points[0][1] < points[0][2] && points[0][2] < points[0][3]
                        && points[1][1] > 0 && points[1][1] < points[1][2] && points[1][2] < points[1][3]);
        boolean rails = true;
        String detail = "";
        for (int relief = World.FOOTHILL; relief < World.RELIEFS; relief++) {
            rails &= dollars[1][relief] > dollars[0][relief] && near(dollars[1][relief] / dollars[0][relief], RunWorks.RAIL_FACTOR)
                    && near(dollars[0][relief], RunWorks.usdPerM2(relief, false)) && near(dollars[1][relief], RunWorks.usdPerM2(relief, true));
            detail += String.format(" %s $%,.0f and $%,.0f;", SlopeCheck.GROUND[relief], dollars[0][relief], dollars[1][relief]);
        }
        report("a railway's works a m2 of its land are a highway's times RAIL_FACTOR on every class above flat, the quotes' own dollars a m2", rails, "a m2:" + detail);
    }

    /* =====================================================================
       9. THE CITY'S MIX OF GROUND
       ===================================================================== */

    static void theMix() {
        out.println("\n--- 9. the city's mix of ground: the works at the mix of its dry ground; a railway's above a highway's; what is no run pays none ---");
        Game g = hills("runworks-9", OWNED, 0.3, 0.4, 0.25, 0.05);
        LandManager lm = g.getLandManager();
        BuildingManager bm = g.getBuildingManager();
        double[] area = new double[World.RELIEFS];
        double total = 0;
        for (int i = 0; i < World.RELIEFS; i++) { area[i] = SlopeCheck.ofClass(g, i); total += area[i]; }
        report("fixture: 30% flat, 40% foothill, 25% steep and 5% very steep owned, the city hilly",
                Math.abs(area[0] / total - 0.3) <= 1e-6 && Math.abs(area[1] / total - 0.4) <= 1e-6 && Math.abs(area[2] / total - 0.25) <= 1e-6
                        && Math.abs(area[3] / total - 0.05) <= 1e-6 && !lm.isFlatOnly(),
                String.format("flat %,.0f, foothill %,.0f, steep %,.0f, very steep %,.0f sq ft", area[0], area[1], area[2], area[3]));

        double[] usd = new double[2];
        for (int net = 0; net < 2; net++) {
            boolean rail = net == 1;
            for (String name : rail ? new String[] { SPUR, LINE } : new String[] { HIGHWAY }) {
                BuildingsTemplate t = template(g, name);
                int n = rail ? 1 : 3;
                double nm = bm.nonMaterialCost(t);
                Game.BuildQuote q = g.quoteBuild(t, n);
                usd[net] = RunWorks.usdPerM2OfMix(area[0], area[1], area[2], area[3], rail);
                double want = RunWorks.points(t.getLandSqFt() * n, usd[net], perPoint(t));
                String run = "an order of " + n + " x " + name + " on the mixed city";
                close(run + ": the works are its land at the rate of the city's mix of ground, in points", q.worksPoints, want, TOL);
                boolean shares = q.worksMix.length == 4;
                double sum = 0;
                for (int i = 0; shares && i < 4; i++) { shares &= near(q.worksMix[i], area[i] / total); sum += q.worksMix[i]; }
                check("...at the mix the quote reports, the shares of the city's dry ground by class, adding up to one",
                        want > 0 && shares && near(sum, 1) && near(q.worksSqFt, t.getLandSqFt() * n));
                close("...which is RunWorks' rate at the mix, dollars a m2 of the run's land", usdPerM2Of(q, t), usd[net], TOL);
                check("...its money the builders' price of the points, in the sticker, summed as the quote sums it",
                        near(q.worksCost, q.worksPoints * nm / t.getConstructionPoints()) && same(q.sticker, nm * n + q.worksCost));
            }
        }
        check("a railway's works a m2 of land are a highway's times RAIL_FACTOR at the city's mix, and more than a highway's",
                usd[1] > usd[0] && near(usd[1], usd[0] * RunWorks.RAIL_FACTOR));
        BuildingsTemplate highway = template(g, HIGHWAY);
        close("an order of 3 highways owes three times one's works: the land scales it",
                g.quoteBuild(highway, 3).worksPoints, 3 * g.quoteBuild(highway, 1).worksPoints, TOL);
        // The works are in every price the player is shown or charged: the card's verdict, the invoice of a run of orders, the cost the treasury checks.
        Game.BuildQuote three = g.quoteBuild(highway, 3);
        Map<BuildingsTemplate, Integer> invoiced = new LinkedHashMap<>();
        invoiced.put(highway, 3);
        report("...and they are in every price: the build card's verdict, the invoice of a run of orders and the treasury's cost are the quote's total",
                three.worksCost > 0 && same(BuildCard.verdict(g, highway, 3).quote().total, three.total)
                        && same(g.buildRunInvoice(invoiced), three.total) && same(g.calculateTotalCost(highway, 3), three.total),
                String.format("quote %,.3fk, of it the works %,.3fk before the tax", three.total, three.worksCost));

        boolean none = true;
        String paid = "";
        for (String name : new String[] { HOUSE, "Paved Road", "Gravel Road", "Light Rail Line", "Metro Line", "Rail Terminal", "Rail Depot", "Diner" }) {
            BuildingsTemplate t = template(g, name);
            Game.BuildQuote q = g.quoteBuild(t, 4);
            double sticker = bm.nonMaterialCost(t) * 4;
            if (q.prepCost > 0) sticker += q.prepCost;
            if (Game.isRun(t) || q.worksSqFt != 0 || q.worksPoints != 0 || q.worksCost != 0 || !java.util.Arrays.equals(q.worksMix, new double[4])
                    || !same(q.sticker, sticker)) {
                none = false;
                paid += " " + name;
            }
        }
        BuildingsTemplate mine = bm.getTemplate(ham.citybuildersim.sectors.Mining.IRON_MINE);
        Game.BuildQuote m = g.quoteBuild(mine, 1);
        none &= m.worksPoints == 0 && m.worksCost == 0 && !Game.isRun(mine);
        report("what is no run - the Houses and streets, the transit lines and the yards, a works - pays no works on the same city, its sticker the old one",
                none, none ? "9 templates" : "paid:" + paid);
    }

    /* =====================================================================
       10. BUILDERS' WORK IN THE BOOKS
       ===================================================================== */

    /** The depots a fixture's builders are given for 10: enough that a few highways (a Freight Line) take them some years, not centuries. */
    static final int HIGHWAY_DEPOTS = 20, FREIGHT_DEPOTS = 100;

    static void inTheBooks() {
        out.println("\n--- 10. builders' work in the books: the city's highways and the Freight sector's Freight Line, the works owed on the site, billed to Construction, audited month by month ---");
        for (int who = 0; who < 2; who++) {
            SectorStatementCheck.bondsSoonBefore.clear();
            SectorStatementCheck.tallies.clear();
            boolean business = who == 1;
            String by = business ? "the Freight sector's Freight Line (buildFor())" : "the city's Elevated Highways (buildStack())";
            Game g = hills("runworks-10-" + who, OWNED, 0.4, 0.3, 0.2, 0.1);
            g.setCashForTest(SlopeCheck.CASH);
            if (business) g.getEconomyManager().setSectorCash(Sectors.FREIGHT, BUSINESS_CASH);
            EconomyManager em = g.getEconomyManager();
            BuildingManager bm = g.getBuildingManager();
            ham.citybuildersim.sectors.Construction construction = g.getSectors().construction();
            BuildingsTemplate t = template(g, business ? LINE : HIGHWAY);
            // The founding's builders do 156 points a month: a run's order would take them centuries. Depots are put up by fiat (the
            // instant build), so the order is built in a few years - and its first month is all the depots could do at every post.
            int depots = business ? FREIGHT_DEPOTS : HIGHWAY_DEPOTS;
            quietly(() -> g.buildStack(template(g, "Construction Depot"), depots, true));
            double crews = g.getBuildingOutputAtEveryPost();
            Game.BuildQuote one = g.quoteBuild(t, 1);
            // ...and the order is the fewest runs whose works are more than that first month: they cannot be paid in the month they are ordered.
            int n = business ? 1 : (int) Math.max(1, Math.min(20, Math.floor(crews / one.worksPoints) + 1));
            Game.BuildQuote q = g.quoteBuild(t, n);
            double own = n * (double) t.getConstructionPoints(), works = q.worksPoints;
            report("fixture (" + by + "): the works alone are more than a month of the crews' work, so they cannot be paid in the month they are ordered",
                    works > crews && works > 0 && !g.getLandManager().isFlatOnly() && bm.countByName("Construction Depot") >= depots,
                    String.format("%d ordered; the works %.1f points, the crews' first month %,d with %d depots, the order's own %.1f", n, works, (int) crews, depots, own));

            // What the investor sees: the same price on flat ground less the works, grossed up by the builders' tax.
            BusinessInvestment bi = g.getBusinessInvestment();
            double withWorks = bi.getCostOf(t, n);
            double[] relief = reliefOf(g);
            flatten(g);
            double withoutWorks = bi.getCostOf(t, n);
            g.getCityLand().setCentreRelief(relief);
            report("the investor's price (BusinessInvestment.getCostOf()) includes the works, taxed with the builders' work: the flat price and the works' money grossed up",
                    q.worksCost > 0 && withWorks > withoutWorks && Math.abs((withWorks - withoutWorks) - em.withBuildersTax(q.worksCost)) <= TOL * withWorks
                            && same(g.runWorksCost(t, n), q.worksCost),
                    String.format("%,.3fk on hills, %,.3fk on flat, the works %,.3fk before the tax", withWorks, withoutWorks, q.worksCost));

            double backlog0 = construction.getBacklogPoints(), unearned0 = construction.getUnearnedRevenue(), billed0 = construction.getBilledPoints();
            boolean[] ordered = new boolean[1];
            quietly(() -> ordered[0] = business ? g.buildFor(g.getSectorInvestor(Sectors.FREIGHT), t, n)
                    : g.buildStack(t, n, false) == Game.BuildResult.SUCCESS);
            BuildingsStacks stack = bm.getStack(t);
            report(by + ": Construction's backlog rises by the buildings' points and the works', its unearned revenue by the bill",
                    ordered[0] && Math.abs(construction.getBacklogPoints() - backlog0 - (own + works)) <= TOL * (own + works)
                            && Math.abs(construction.getUnearnedRevenue() - unearned0 - q.total) <= TOL * q.total
                            && Math.abs(construction.getBilledPoints() - billed0 - (own + works)) <= TOL * (own + works),
                    String.format("backlog +%.3f of %.3f + %.3f, unearned +%,.3fk of the quote's %,.3fk", construction.getBacklogPoints() - backlog0,
                            own, works, construction.getUnearnedRevenue() - unearned0, q.total));
            check("...and the site owes the works: its progress is below nothing by their points, none of the buildings finished",
                    stack != null && Math.abs(stack.getConstructionProgress() + works) <= TOL * works
                            && stack.getQuantity() == 0 && stack.getUnderConstruction() == n);
            check("...the counters read it: one order, its points and its money", g.runWorksOrders() == 1
                    && same(g.runWorksPointsBilled(), q.worksPoints) && same(g.runWorksMoneyBilled(), q.worksCost) && g.preparationOrders() == 0);

            SlopeCheck.Months m = new SlopeCheck.Months();
            while (stack.getUnderConstruction() > 0 && m.played < SlopeCheck.PATIENCE) SlopeCheck.month(g, stack, m);
            report(by + ": the first building finishes only once the works are paid - the crews' points up to then are at least the works and a building",
                    m.firstFinished > 1 && m.appliedAtFirst >= works + t.getConstructionPoints() - 1e-6,
                    String.format("month %d, %.3f points applied against works %.3f + building %d", m.firstFinished, m.appliedAtFirst, works,
                            t.getConstructionPoints()));
            report("...and every building is built, the points applied the order's whole - the buildings' and the works'",
                    stack.getUnderConstruction() == 0 && stack.getQuantity() == n && Math.abs(m.applied - (own + works)) <= 1e-6 * (own + works),
                    String.format("%d months, %.3f applied of %.3f", m.played, m.applied, own + works));
            report("...all that was billed is earned: the order book is back to what it was, in points and in money",
                    Math.abs(construction.getBacklogPoints() - backlog0) <= 1e-6 * (own + works)
                            && Math.abs(construction.getUnearnedRevenue() - unearned0) <= 1e-6 * q.total,
                    String.format("backlog %+.6f, unearned %+.6fk", construction.getBacklogPoints() - backlog0,
                            construction.getUnearnedRevenue() - unearned0));
            double recognised = 0;
            for (double r : m.recognised) recognised += r;
            report("...and Construction's earnings are the order's money: the work it recognised over the months is the bill, off by no more than the material the price allowed for (BuildQuote.allowance), which is trued up as the crews draw it",
                    Math.abs(recognised - q.total) <= q.allowance && q.allowance < q.total,
                    String.format("%,.3fk recognised of the quote's %,.3fk, the material allowed for %,.3fk", recognised, q.total, q.allowance));
            double firstWork = m.recognised.get(0), counted = m.counted.size() > 1 ? m.counted.get(1) : Double.NaN;
            report("...GDP counted the works as construction: the first month's work, when no building had finished, is what GDP counts the month after",
                    firstWork > 0 && m.firstFinished > 1 && Math.abs(counted - firstWork) <= 1e-9 * firstWork,
                    String.format("%,.3fk recognised in month 1, %,.3fk counted in month 2", firstWork, counted));
            int[] statements = SlopeCheck.statements();
            report("over those months the money audit closed, the GDP identity held and GDP's construction was the builders' recognised work, post-audit drift"
                            + " was nil, and every sector's statements held",
                    m.played > 0 && m.moneyOff == 0 && m.gdpOff == 0 && SlopeCheck.gdpOffTheBooks(m) == 0 && m.driftOff == 0
                            && statements[1] == 0 && statements[0] > 0,
                    String.format("%d months; the audit off %d, GDP off %d + %d, drift off %d; %,d statement identities checked, %d missed (%s); worst residual %.2e",
                            m.played, m.moneyOff, m.gdpOff, SlopeCheck.gdpOffTheBooks(m), m.driftOff, statements[0], statements[1],
                            SlopeCheck.statementsMissed(), m.worstResidual));
        }
    }

    /* =====================================================================
       11. A FLAT CITY TO THE BIT
       ===================================================================== */

    static void aFlatCityPaysNoWorks() {
        out.println("\n--- 11. a flat city to the bit: a run's quote, order, books and investor's price are what they were before ET4 ---");
        Game g = SlopeCheck.city("runworks-11", 1, 0, 0, 0);
        g.getEconomyManager().setSectorCash(Sectors.FREIGHT, BUSINESS_CASH);
        BuildingManager bm = g.getBuildingManager();
        ham.citybuildersim.sectors.Construction construction = g.getSectors().construction();
        BuildingsTemplate highway = template(g, HIGHWAY), line = template(g, LINE);
        g.getLandManager().restoreWorks(0);
        g.getLandManager().restoreRuns(0);
        int highways = 3;
        double nmHighway = bm.nonMaterialCost(highway), nmLine = bm.nonMaterialCost(line);
        Game.BuildQuote hq = g.quoteBuild(highway, highways), lq = g.quoteBuild(line, 1);
        report("fixture: a city of neither foothill nor steep ground, no works counted yet",
                g.getLandManager().isFlatOnly() && g.runWorksOrders() == 0 && Game.isRun(highway) && Game.isRun(line),
                "flat only: " + g.getLandManager().isFlatOnly());
        boolean quotes = true;
        for (Game.BuildQuote q : new Game.BuildQuote[] { hq, lq }) {
            quotes &= q.worksSqFt == 0 && q.worksPoints == 0 && q.worksCost == 0 && java.util.Arrays.equals(q.worksMix, new double[4]);
        }
        check("the quotes carry no works: no land priced, no points, no money, no mix - for a highway and a Freight Line",
                quotes && g.runWorksCost(highway, highways) == 0 && g.runWorksCost(line, 1) == 0);
        check("...the sticker is the builders' non-material cost times the order, to the bit, and the wait asks the crews for the buildings' points alone",
                same(hq.sticker, nmHighway * highways) && same(lq.sticker, nmLine) && same(hq.months, g.quoteCityMonths(highway, highways))
                        && same(lq.months, g.quoteCityMonths(line, 1)));

        // The investor's price: the same double with the works' supplier cleared, which is the arithmetic before ET4.
        BusinessInvestment bi = g.getBusinessInvestment();
        double priced = bi.getCostOf(line, 1), pricedHighways = bi.getCostOf(highway, highways);
        bi.setRunWorksCost(null);
        double before = bi.getCostOf(line, 1), beforeHighways = bi.getCostOf(highway, highways);
        bi.setRunWorksCost(g::runWorksCost);
        check("the investor's price of a Freight Line and of highways is the old price, to the bit (BusinessInvestment.getCostOf())",
                same(priced, before) && same(pricedHighways, beforeHighways) && priced > 0);

        double backlog0 = construction.getBacklogPoints(), unearned0 = construction.getUnearnedRevenue(), billed0 = construction.getBilledPoints();
        Game.BuildResult hw = order(g, highway, highways);
        // ...the Freight Line quoted after the highways' order, which leaves the yard what it leaves.
        lq = g.quoteBuild(line, 1);
        boolean lined = businessOrder(g, Sectors.FREIGHT, line, 1);
        double own = highways * (double) highway.getConstructionPoints() + line.getConstructionPoints();
        report("the city's highways and the Freight Line go through; Construction's backlog and billed points rise by the buildings' points alone, its unearned revenue by the bills",
                hw == Game.BuildResult.SUCCESS && lined && Math.abs(construction.getBacklogPoints() - backlog0 - own) <= TOL * own
                        && Math.abs(construction.getBilledPoints() - billed0 - own) <= TOL * own
                        && Math.abs(construction.getUnearnedRevenue() - unearned0 - hq.total - lq.total) <= TOL * (hq.total + lq.total),
                String.format("%s, %b; backlog +%.3f of %.3f", hw, lined, construction.getBacklogPoints() - backlog0, own));
        BuildingsStacks hStack = bm.getStack(highway), lStack = bm.getStack(line);
        check("...no site owes anything before its first building: the progress is nothing, not below it",
                hStack != null && lStack != null && hStack.getConstructionProgress() == 0 && lStack.getConstructionProgress() == 0
                        && hStack.getUnderConstruction() == highways && lStack.getUnderConstruction() == 1);
        check("...and the counters of works and of preparation stand at nothing",
                g.runWorksOrders() == 0 && g.runWorksPointsBilled() == 0 && g.runWorksMoneyBilled() == 0
                        && g.preparationOrders() == 0 && g.preparationPointsBilled() == 0 && g.preparationMoneyBilled() == 0);
    }

    /* =====================================================================
       12. THE SCREEN'S WORDS
       ===================================================================== */

    /** BuildScreen.worksWords(), a package-private static of the build screen, read through reflection: it must not need a toolkit. */
    static String worksWords(Game.BuildQuote q) {
        try {
            Class<?> screen = Class.forName("ham.citybuildersim.ui.BuildScreen");
            java.lang.reflect.Method words = screen.getDeclaredMethod("worksWords", Game.BuildQuote.class);
            words.setAccessible(true);
            return (String) words.invoke(null, q);
        } catch (ReflectiveOperationException | LinkageError e) {
            return "(BuildScreen.worksWords() could not be called: " + e + ")";
        }
    }

    static void theScreensWords() {
        out.println("\n--- 12. the screen's words: BuildScreen.worksWords() reads the quote's fields; nothing on flat ground, for a building, or for no quote ---");
        Game g = hills("runworks-12", OWNED, 0.3, 0.4, 0.25, 0.05);
        BuildingsTemplate highway = template(g, HIGHWAY), house = template(g, HOUSE);
        Game.BuildQuote q = g.quoteBuild(highway, 5);
        String words = worksWords(q);
        String land = LandManager.areaWords(q.worksSqFt), points = String.format("%,.1f", q.worksPoints), money = ham.citybuildersim.ui.Money.money(q.worksCost);
        report("on the mixed city a highway order is worded from the quote: the run's land, the points and the money, as the screen writes them",
                words != null && q.worksPoints > 0 && words.startsWith("Cuts, viaducts and tunnels for its " + land + " over the city's ground (")
                        && words.contains(points + " points of builders' work, " + money),
                String.valueOf(words));
        String foothill = "foothill " + String.format("%.0f%%", q.worksMix[1] * 100), steep = "steep " + String.format("%.0f%%", q.worksMix[2] * 100),
                very = "very steep " + String.format("%.0f%%", q.worksMix[3] * 100);
        check("...the city's ground in its shares by class above flat, each from the quote's mix - foothill 40%, steep 25%, very steep 5% - and not the flat",
                words != null && words.contains("(" + foothill + ", " + steep + ", " + very + ")") && foothill.equals("foothill 40%")
                        && steep.equals("steep 25%") && very.equals("very steep 5%") && !words.contains("flat"));
        check("a building's quote on the same city, a quote with no works and no quote at all have no line, so a flat city's card reads as it did",
                worksWords(g.quoteBuild(house, 5)) == null && worksWords(null) == null);

        // The same highway on a flat city.
        Game flat = SlopeCheck.city("runworks-12-flat", 1, 0, 0, 0);
        check("the same order on a flat city has no line", worksWords(flat.quoteBuild(template(flat, HIGHWAY), 5)) == null
                && worksWords(flat.quoteBuild(template(flat, LINE), 1)) == null);

        // A mix with a sliver of very steep ground reads under 1%, not 0%.
        Game sliver = hills("runworks-12-sliver", OWNED, 0.6, 0.3959, 0.004, 0.0001);
        String thin = worksWords(sliver.quoteBuild(template(sliver, LINE), 1));
        check("a class that is under a half per cent of the ground reads \"under 1%\", never \"0%\"",
                thin != null && thin.contains("(foothill 40%, steep under 1%, very steep under 1%)") && !thin.contains(" 0%"));

        // ...and it is drawn where the preparation's words are, under the quote on the build card.
        java.nio.file.Path screen = java.nio.file.Path.of("src/main/java/ham/citybuildersim/ui/BuildScreen.java");
        if (Files.exists(screen)) {
            try {
                String source = Files.readString(screen);
                check("the build card draws it with the preparation's line: worksWords(v.quote()) beside prepWords(v.quote())",
                        source.contains("prepWords(v.quote())") && source.contains("worksWords(v.quote())"));
            } catch (java.io.IOException e) {
                check("could not read Build's source: " + e, false);
            }
        } else {
            out.println("      (Build's source is not under the working folder: the line is not looked for in it)");
        }
    }

    /* =====================================================================
       13. ON THE MAP, AND 14. THE STREETS' RULE (slice C; spec-earth2-terrain 4.3: "a run on very steep ground is drawn as a
       tunnel (portals, a dashed line), on steep ground as a viaduct")
       ===================================================================== */

    static final int T = TilePainter.TILE;

    /** The fixture's rows and columns: the highway along row HY from column HX0, the track down column TX, and row BARE with no run. */
    static final int HY = 6, HX0 = 8, TX = 4, BARE = 20;

    /** A fixture's relief at k along an axis: flat to 9, steep 10 to 15, very steep 16 to 23, flat from 24. */
    static byte band(int k) {
        return k < 10 ? World.FLAT : k < 16 ? World.STEEP : k < 24 ? World.VERY_STEEP : World.FLAT;
    }

    /** One owned tile of dry grass: relief in bands along x (and, down the track's column, along y), an east-west highway on row HY and a north-south track on column TX. */
    public static TilePainter.Input mapTile() {
        TilePainter.Input in = new TilePainter.Input();
        java.util.Arrays.fill(in.owned, true);
        for (int y = 0; y < T; y++) for (int x = 0; x < T; x++) in.relief[y * T + x] = x == TX ? band(y) : band(x);
        for (int x = HX0; x < T; x++) in.fixed[HY * T + x] = (byte) BuildingVisual.HIGHWAY;
        for (int y = 0; y < T; y++) in.fixed[y * T + TX] = TilePainter.RAIL;
        return in;
    }

    static TilePainter.Painted paintTile(TilePainter.Input in) {
        TilePainter.Painted p = new TilePainter.Painted();
        TilePainter.paint(in, p);
        return p;
    }

    static int[] rasterAt(TilePainter.Input in, TilePainter.Painted p, int px) {
        int[] img = new int[T * px * T * px];
        TileRaster.raster(in, p, px, img);
        return img;
    }

    /** One pixel of plot (plotX, plotY), local (dx, dy), at px pixels a plot. */
    static int pixel(int[] img, int px, int plotX, int plotY, int dx, int dy) {
        return img[(plotY * px + dy) * (T * px) + plotX * px + dx];
    }

    /** How many pixels of plot (plotX, plotY) are TUNNEL_PORTAL. */
    static int portals(int[] img, int px, int plotX, int plotY) {
        int n = 0;
        for (int dy = 0; dy < px; dy++) for (int dx = 0; dx < px; dx++) if (pixel(img, px, plotX, plotY, dx, dy) == TileRaster.TUNNEL_PORTAL) n++;
        return n;
    }

    /** How many pixels of the whole image are TUNNEL_PORTAL. */
    static int portals(int[] img) {
        int n = 0;
        for (int c : img) if (c == TileRaster.TUNNEL_PORTAL) n++;
        return n;
    }

    static void onTheMap() {
        out.println("\n--- 13. on the map: a run across steep ground is a viaduct, across very steep ground a tunnel with a dashed line and a portal at each end ---");
        TilePainter.Input in = mapTile();
        TilePainter.Painted p = paintTile(in);

        // The painter's flags.
        int wantBridges = 0, wantTunnels = 0, bridges = 0, tunnels = 0;
        boolean exact = true, flatClear = true, both = false;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            boolean run = in.fixed[i] != 0;
            boolean steep = run && in.relief[i] == World.STEEP, very = run && in.relief[i] == World.VERY_STEEP;
            if (steep) wantBridges++;
            if (very) wantTunnels++;
            if (p.bridge[i]) bridges++;
            if (p.tunnel[i]) tunnels++;
            exact &= p.bridge[i] == steep && p.tunnel[i] == very;
            if (!steep && !very) flatClear &= !p.bridge[i] && !p.tunnel[i];
            both |= p.bridge[i] && p.tunnel[i];
        }
        check("fixture: both runs cross steep and very steep ground (more than one plot each) as well as flat", wantBridges > 2 && wantTunnels > 2
                && in.relief[HY * T + 9] == World.FLAT && in.relief[HY * T + 27] == World.FLAT && in.relief[4 * T + TX] == World.FLAT);
        report("Painted.bridge is true on exactly the runs' steep plots and Painted.tunnel on exactly their very steep plots",
                exact && bridges == wantBridges && tunnels == wantTunnels, bridges + " bridges, " + tunnels + " tunnels, from the fixture " + wantBridges + " and " + wantTunnels);
        check("...neither on a flat plot of a run nor on any plot that holds no run, and no plot is both", flatClear && !both);

        // At 8 px a plot.
        int px = 8, roadC = TileRaster.ROAD[BuildingVisual.HIGHWAY];
        int[] cs = TileRaster.crossSection(BuildingVisual.HIGHWAY, px);
        int row = (px - cs[0]) / 2 + cs[1];
        int mid = px / 2, grey = TileRaster.colour(World.GRASS, World.VERY_STEEP), greySteep = TileRaster.colour(World.GRASS, World.STEEP);
        int[] img = rasterAt(in, p, px);
        check("at 8 px a flat plot of the highway has the highway's surface colour in the middle of its band",
                pixel(img, px, 27, HY, 3, row) == roadC && pixel(img, px, 9, HY, 3, row) == roadC);
        check("...a viaduct's plot has it too, its casing in the deck's colour where a flat plot's is the highway's own",
                pixel(img, px, 12, HY, 3, row) == roadC && pixel(img, px, 12, HY, 3, 0) == TileRaster.DECK && pixel(img, px, 27, HY, 3, 0) == TileRaster.HIGHWAY_CASE);
        check("...a tunnel's plot has not: where the surface lies on a flat plot it is the ground's grey, with the highway's colour only in the dashed line along the middle",
                pixel(img, px, 19, HY, 3, row) == grey && pixel(img, px, 19, HY, 3, row) != roadC && pixel(img, px, 19, HY, 0, mid) == roadC && pixel(img, px, 19, HY, px - 1, mid) == grey);
        int bedCol = (px - Math.min(px, Math.max(1, (int) Math.round(TileRaster.BED * px)))) / 2;
        check("the track's flat plot has its ballast and no deck, its steep plot the deck beside the bed, its very steep plot its grey with the steel in the dashed line",
                pixel(img, px, TX, 27, bedCol, 1) == TileRaster.BALLAST && pixel(img, px, TX, 27, 0, 3) == TileRaster.GROUND[World.GRASS]
                        && pixel(img, px, TX, 12, bedCol, 1) == TileRaster.BALLAST && pixel(img, px, TX, 12, 0, 3) == TileRaster.DECK
                        && pixel(img, px, TX, 19, bedCol, 1) == grey && pixel(img, px, TX, 19, mid, 0) == TileRaster.STEEL && pixel(img, px, TX, 19, mid, px - 1) == grey);

        // The portals.
        int depth = Math.max(1, (int) Math.round(TileRaster.TUNNEL_PORTAL_DEPTH * px));
        int hAcross = Math.min(px, cs[0] + 2), tAcross = Math.min(px, TileRaster.crossSection(BuildingVisual.PAVED, px)[0] + 2);
        boolean highwayEnds = portals(img, px, 16, HY) == hAcross * depth && portals(img, px, 23, HY) == hAcross * depth
                && pixel(img, px, 16, HY, 0, mid) == TileRaster.TUNNEL_PORTAL && pixel(img, px, 16, HY, depth, mid) != TileRaster.TUNNEL_PORTAL
                && pixel(img, px, 23, HY, px - 1, mid) == TileRaster.TUNNEL_PORTAL && pixel(img, px, 23, HY, px - depth - 1, mid) != TileRaster.TUNNEL_PORTAL;
        boolean inside = true;
        for (int x = 17; x <= 22; x++) inside &= portals(img, px, x, HY) == 0;
        check("the highway's tunnel has a portal band across its way at the west edge of its first plot and the east edge of its last, TUNNEL_PORTAL, a little wider than the run, the dial's depth", highwayEnds);
        check("...and none inside it, none on the viaduct or the flat plots beside it", inside && portals(img, px, 15, HY) == 0 && portals(img, px, 24, HY) == 0 && portals(img, px, 9, HY) == 0);
        boolean trackEnds = portals(img, px, TX, 16) == tAcross * depth && portals(img, px, TX, 23) == tAcross * depth
                && pixel(img, px, TX, 16, mid, 0) == TileRaster.TUNNEL_PORTAL && pixel(img, px, TX, 23, mid, px - 1) == TileRaster.TUNNEL_PORTAL;
        boolean trackInside = true;
        for (int y = 17; y <= 22; y++) trackInside &= portals(img, px, TX, y) == 0;
        check("the track's tunnel has its portals at the north edge of its first plot and the south edge of its last, and none inside", trackEnds && trackInside);
        check("...and the tile holds no other portal pixel: the two tunnels' four ends alone, from the dials", portals(img) == 2 * hAcross * depth + 2 * tAcross * depth);

        // A plot with no run is drawn as it is without the runs.
        TilePainter.Input bareIn = mapTile();
        java.util.Arrays.fill(bareIn.fixed, (byte) 0);
        TilePainter.Painted bareP = paintTile(bareIn);
        boolean sameGround = true;
        int[] bare8 = rasterAt(bareIn, bareP, 8), bare4 = rasterAt(bareIn, bareP, 4), img4 = rasterAt(in, p, 4);
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (in.fixed[i] != 0) continue;
            int x = i % T, y = i / T;
            for (int dy = 0; dy < 8; dy++) for (int dx = 0; dx < 8; dx++) sameGround &= pixel(img, 8, x, y, dx, dy) == pixel(bare8, 8, x, y, dx, dy);
            for (int dy = 0; dy < 4; dy++) for (int dx = 0; dx < 4; dx++) sameGround &= pixel(img4, 4, x, y, dx, dy) == pixel(bare4, 4, x, y, dx, dy);
        }
        check("a plot with no run is drawn as it is without the runs, at 8 px and at 4: every pixel of every such plot is the same", sameGround);
        check("...a plot of steep ground with none is its steep grey, of very steep its darker grey, of flat the grass",
                pixel(img, px, 12, BARE, 3, 3) == greySteep && pixel(img, px, 20, BARE, 3, 3) == grey && pixel(img, px, 2, BARE, 3, 3) == TileRaster.GROUND[World.GRASS] && greySteep != grey);

        // At 4 px a plot: a highway fills its plot's width, so the viaduct's casing is one pixel of the deck's colour.
        int p4 = 4, roadF = TileRaster.ROAD[BuildingVisual.HIGHWAY];
        int d4 = Math.max(1, (int) Math.round(TileRaster.TUNNEL_PORTAL_DEPTH * p4));
        int h4 = Math.min(p4, TileRaster.crossSection(BuildingVisual.HIGHWAY, p4)[0] + 2), t4 = Math.min(p4, TileRaster.crossSection(BuildingVisual.PAVED, p4)[0] + 2);
        check("at 4 px a flat plot of the highway is its surface across, a viaduct's plot has the deck's colour on the pixel either side of it",
                pixel(img4, p4, 27, HY, 1, 1) == roadF && pixel(img4, p4, 27, HY, 1, 0) == roadF
                        && pixel(img4, p4, 12, HY, 1, 1) == roadF && pixel(img4, p4, 12, HY, 1, 0) == TileRaster.DECK && pixel(img4, p4, 12, HY, 1, p4 - 1) == TileRaster.DECK);
        check("...a tunnel's plot is its ground with the dashed line (the highway's colour on the first pixels along the middle row), and the portals are four ends of the dials' depth",
                pixel(img4, p4, 19, HY, 1, p4 - 1) == grey && pixel(img4, p4, 19, HY, 0, p4 / 2) == roadF && portals(img4) == 2 * h4 * d4 + 2 * t4 * d4);
        check("...the track's tunnel plot is ground and steel there too, its steep plot has the deck beside the bed",
                pixel(img4, p4, TX, 19, 0, 3) == grey && pixel(img4, p4, TX, 19, p4 / 2, 0) == TileRaster.STEEL && pixel(img4, p4, TX, 12, 0, 1) == TileRaster.DECK);

        // The blocks' views: a tunnel is its ground and, every other plot along the run, the run's colour.
        int[] blocks = new int[T * 2 * T * 2];
        TileRaster.blocks(in, p, 2, TileRaster.BLOCK_MIDDLE, false, blocks);
        boolean dashed = true;
        for (int x = 16; x <= 23; x++) {
            boolean dash = TileRaster.tunnelDash(HY * T + x);
            dashed &= pixel(blocks, 2, x, HY, 0, 0) == (dash ? roadC : grey);
        }
        check("in the blocks' views (2 px a plot) a tunnel of the highway is its bare ground but for every other plot, the highway's colour, and a viaduct is a highway like any",
                dashed && pixel(blocks, 2, 12, HY, 0, 0) == roadC && pixel(blocks, 2, 27, HY, 0, 0) == roadC
                        && TileRaster.tunnelDash(HY * T + 18) != TileRaster.tunnelDash(HY * T + 19));
        int[] far = new int[T * T];
        TileRaster.blocks(in, p, 1, TileRaster.BLOCK_FAR, false, far);
        boolean farDashed = true;
        for (int x = 16; x <= 23; x++) farDashed &= far[HY * T + x] == (TileRaster.tunnelDash(HY * T + x) ? roadC : grey);
        check("...and in the far view (1 px a plot) likewise", farDashed);

        // The hover says what carries the run.
        java.util.function.IntFunction<String> noName = id -> null;
        check("the hover card says \"a viaduct\" on a steep plot of a run and \"a tunnel\" on a very steep one, highway and railway alike, and nothing on the flat",
                "Highway, a viaduct".equals(LandMap.plotWords(in, p, HY * T + 12, noName)) && "Highway, a tunnel".equals(LandMap.plotWords(in, p, HY * T + 19, noName))
                        && "Highway".equals(LandMap.plotWords(in, p, HY * T + 27, noName))
                        && "Railway, a viaduct".equals(LandMap.plotWords(in, p, 12 * T + TX, noName)) && "Railway, a tunnel".equals(LandMap.plotWords(in, p, 19 * T + TX, noName))
                        && "Railway".equals(LandMap.plotWords(in, p, 27 * T + TX, noName)));
    }

    static void theStreetsRuleDidNotMove() {
        out.println("\n--- 14. the streets' rule did not move: a street on fresh water is a bridge, a street or a run on foothill is none, a run over water a bridge and no tunnel ---");
        TilePainter.Input in = new TilePainter.Input();
        java.util.Arrays.fill(in.owned, true);
        int sx = 3, code = DistrictPlan.PAVED | (1 << TilePainter.S_WIDTH_SHIFT);
        for (int y = 0; y < T; y++) in.street[y * T + sx] = (byte) code;
        for (int y = 5; y <= 9; y++) in.terrain[y * T + sx] = World.FRESH;
        for (int y = 12; y <= 14; y++) in.relief[y * T + sx] = World.STEEP;
        for (int y = 16; y <= 18; y++) in.relief[y * T + sx] = World.VERY_STEEP;
        for (int y = 22; y <= 24; y++) in.relief[y * T + sx] = World.FOOTHILL;
        // A highway along row 28 from column 8: foothill to column 15, then over fresh water on plots a hypothetical very steep relief marks (the water wins).
        for (int x = 8; x < T; x++) in.fixed[28 * T + x] = (byte) BuildingVisual.HIGHWAY;
        for (int x = 8; x <= 15; x++) in.relief[28 * T + x] = World.FOOTHILL;
        for (int x = 16; x <= 19; x++) { in.terrain[28 * T + x] = World.FRESH; in.relief[28 * T + x] = World.VERY_STEEP; }
        TilePainter.Painted p = paintTile(in);
        boolean water = true, steep = true, very = true, foot = true, wetRun = true, footRun = true;
        for (int y = 5; y <= 9; y++) water &= p.use[y * T + sx] == TilePainter.ROAD && p.bridge[y * T + sx] && !p.tunnel[y * T + sx];
        for (int y = 12; y <= 14; y++) steep &= p.use[y * T + sx] == TilePainter.ROAD && !p.bridge[y * T + sx] && !p.tunnel[y * T + sx];
        for (int y = 16; y <= 18; y++) very &= p.use[y * T + sx] == TilePainter.ROAD && !p.bridge[y * T + sx] && !p.tunnel[y * T + sx];
        for (int y = 22; y <= 24; y++) foot &= p.use[y * T + sx] == TilePainter.ROAD && !p.bridge[y * T + sx] && !p.tunnel[y * T + sx];
        for (int x = 8; x <= 15; x++) footRun &= p.use[28 * T + x] == TilePainter.ROAD && !p.bridge[28 * T + x] && !p.tunnel[28 * T + x];
        for (int x = 16; x <= 19; x++) wetRun &= p.bridge[28 * T + x] && !p.tunnel[28 * T + x];
        check("a street plot on fresh water is a bridge, as it was, and no tunnel", water);
        check("...a street plot on steep ground, or on very steep, is neither (the plans never lay one there; its relief does not make it a bridge)", steep && very);
        check("...a street plot on foothill is neither", foot);
        check("a run plot on foothill is neither a bridge nor a tunnel", footRun);
        check("a run plot over fresh water is a bridge and never a tunnel, whatever relief the plot carries", wetRun);
        int viaducts = 0, tunnels = 0;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (p.bridge[i] && in.terrain[i] != World.FRESH) viaducts++;
            if (p.tunnel[i]) tunnels++;
        }
        check("...and this tile has no run on steep ground, so it has no viaduct and no tunnel: the bridges are over water alone", viaducts == 0 && tunnels == 0);
    }
}
