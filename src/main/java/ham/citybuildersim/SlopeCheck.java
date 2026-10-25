package ham.citybuildersim;

import ham.citybuildersim.sectors.Construction;
import ham.citybuildersim.sectors.Mining;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Buildable by slope, checked (ET2, slice D; docs/studies/spec-earth2-terrain.md 3.2, 3.4, 4.1, 4.2, 4.4): a building
 * stands on flat and foothill ground and on no steeper, a mine on any, a foothill pad is prepared and paid for as
 * builders' work, and the map and the founding keep to the same ground.
 *
 * WHAT THIS HAS TO PROVE, every fixture causing the condition it tests and every figure the model's own constant:
 *
 *   1. A BUILDING ON EACH CLASS OF GROUND. Four cities, their dry ground all flat, all foothill, all steep, all
 *      very steep: a House's canAllocate(), the city's order (Game.buildStack()) and a business's (buildFor()) go
 *      through on flat and foothill and are refused on steep and very steep - NO_LAND, nothing allocated, no money
 *      taken.
 *   2. A MINE ON EACH CLASS OF GROUND. The same four: a site-bound works (Game.standsAnywhere()) is allowed on all,
 *      its ground comes from the class it stands on, and it is charged no preparation, foothill included.
 *   3. THE ORDER OF USE. Mixed ground, 40% flat, 30% foothill, 30% steep: buildings take flat before foothill
 *      (buildingsOnFoothill stays 0 until the flat is used, then is the excess, to the square foot); works take steep,
 *      then foothill, then flat; idle steep ground is not free for a building and is free for a works.
 *   4. THE PREPARATION'S PRICE, against the constants: a House onto foothill carries its land as prepSqFt, the
 *      points SitePreparation's dollars come to at the House's founding cash a point, the money at the House's
 *      non-material cost a point, and the quote's total exceeds the same order on flat by that money grossed up by
 *      the builders' sales tax; on flat all three are 0.
 *   5. PREPARED ONCE. Built on foothill, demolished, built again on the same ground: no second preparation, and the
 *      foothill prepared never falls.
 *   6. BUILDERS' WORK IN THE BOOKS. The city's order and a business's onto foothill: Construction's backlog rises by
 *      the buildings' points and the pad's, its unearned revenue by the bill; the pad is paid before the first building
 *      finishes; over the months the money audit, the GDP identity (the pad's work counted as construction) and the
 *      sectors' statements hold, and what was billed is all earned by the time it is built.
 *   7. A STREET, A BUILDING AND A MINE ON THE MAP BY RELIEF. On the plan's own predicate and on a district whose
 *      ground has bands of steep, very steep and ice: flat and foothill take streets and buildings, the bands take
 *      neither - and the same district without the bands would have used them; a mine's site stands on dry ground
 *      whatever the slope.
 *   8. FOUNDING ON BUILDABLE GROUND, where earth-2.bin is named: Medellin's snapped site and its eight test points
 *      are buildable, a spot among Everest's steep slopes is refused in the new words naming the nearest buildable
 *      ground (its summit, ice, in the ice's), and a plan over Medellin's real ground puts no street or building on
 *      a steep plot of it.
 *   9. HOW OFTEN IT FIRES IN A REAL RUN, where earth-2.bin is named: a Medellin city played on the real Earth, the
 *      orders charged a preparation, the foothill prepared and its points as a share of every point billed counted;
 *      it fires, and no building stands on steep ground.
 *
 * WHY. ET1 gave every plot a relief and drew it; ET2's slices A to C made the land model, the price, the map and the
 * founding read it. Each has its own arithmetic and none of it is the arithmetic of another, so a figure here is
 * worked out from the constants the model states (SitePreparation, World, LandManager) and never typed. The
 * sections that need the real Earth say so in one line and skip where the file is not named, as EarthFoundingCheck
 * does (-Dcbs.earth=<path>, or data/earth-2.bin).
 */
public class SlopeCheck {

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

    /** A fixture city's treasury: more than any order here costs, so a refusal is never for cash. */
    static final double CASH = 1e8;

    /** ...and the cash the Real Estate sector is given to pay a business's order from by fiat: an order's worth and more, in a figure small enough for the sectors' statements to be exact to the cent (a fixture rich beyond it loses cents in the doubles). */
    static final double BUSINESS_CASH = 1e6;

    /** What a fixture of figures near one another can be told apart by: a rounding, not ground. */
    static final double TOL = 1e-9;

    /** The relief classes' names, in World's order (FLAT to VERY_STEEP). */
    static final String[] GROUND = { "flat", "foothill", "steep", "very steep" };

    static final String HOUSE = "House";

    /** A building of the city's own, which the city may demolish (Game.demolish(): a House is a landlord's) and the builders take a few months to put up. */
    static final String HOME_CARE = "Home Care Service";

    static BuildingsTemplate template(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        if (t == null) throw new IllegalStateException("no template named " + name);
        return t;
    }

    /**
     * A city founded as a harness founds one, its dry ground of the shares given by relief (flat, foothill, steep,
     * very steep, adding up to 1; CityLand.setCentreRelief()), every sector held so no business builds or scrapps
     * anything here but by a fixture's hand, and the treasury CASH.
     */
    static Game city(String label, double flat, double foothill, double steep, double veryStep) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> {
            g.run();
            g.setCashForTest(CASH);
            for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
        });
        double dry = g.getCityLand().totalKm2(CityLand.DRY);
        g.getCityLand().setCentreRelief(new double[] { flat * dry, foothill * dry, steep * dry, veryStep * dry, 0 });
        // A harness's fiat buildings take no ground (ReadPathCheck.afterALoad()), and the founding's own - the yard's and the
        // bank's, allocated on the founding's flat before the hills were given - are such: the fixture starts with all of its ground free.
        g.getLandManager().setAllocatedSqFt(0);
        return g;
    }

    /** The city's dry ground of one relief class, square feet (CityLand's, which the land model reads live). */
    static double ofClass(Game g, int relief) {
        return LandManager.sqFt(g.getCityLand().totalReliefKm2(relief));
    }

    static Game.BuildResult order(Game g, BuildingsTemplate t, int n) {
        Game.BuildResult[] r = new Game.BuildResult[1];
        quietly(() -> r[0] = g.buildStack(t, n, false));
        return r[0];
    }

    /** Gives the city iron sites by fiat, for the Iron Mine (BuildCardCheck's way): hematite, the first type. */
    static void ironSites(Game g, int sites) {
        g.getLandManager().restoreSites(Resource.IRON, sites, sites * Resource.IRON.amountPerSite());
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        buildingOnEachGround();
        mineOnEachGround();
        orderOfUse();
        preparationPrice();
        preparedOnce();
        inTheBooks();
        onTheMap();
        EarthData file = theEarth();
        foundingOnBuildableGround(file);
        inARealRun(file);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       1. A BUILDING ON EACH CLASS OF GROUND
       ===================================================================== */

    /** How many Houses the fixtures of 1 order at once. */
    static final int ORDER = 5;

    static void buildingOnEachGround() {
        out.println("--- 1. a building on each class of ground: flat and foothill take it, steep and very steep refuse it ---");
        for (int c = World.FLAT; c <= World.VERY_STEEP; c++) {
            double[] share = new double[4];
            share[c] = 1;
            Game g = city("slopecheck-1-" + c, share[0], share[1], share[2], share[3]);
            LandManager lm = g.getLandManager();
            BuildingsTemplate house = template(g, HOUSE);
            double each = house.getLandSqFt(), ground = ofClass(g, c);
            boolean may = World.buildable(World.GRASS, c);
            String name = GROUND[c];
            boolean fixture = Math.abs(ground - lm.getOwnedSqFt()) <= TOL * ground && ground > ORDER * each;
            for (int other = World.FLAT; other <= World.VERY_STEEP; other++) {
                if (other != c) fixture &= ofClass(g, other) == 0;
            }
            report("fixture: the city's whole dry ground is " + name + ", more than " + ORDER + " Houses want", fixture,
                    String.format("%,.0f sq ft of %,.0f owned", ground, lm.getOwnedSqFt()));

            check(name + ": a House's canAllocate() is " + may + ", and the ground free for it is " + (may ? "all of it" : "none"),
                    lm.canAllocate(each, house.getSector()) == may
                            && Math.abs(lm.freeFor(house.getSector(), false) - (may ? ground : 0)) <= TOL * ground);

            double cash0 = g.getCash(), allocated0 = lm.getAllocatedSqFt();
            Game.BuildQuote q = g.quoteBuild(house, ORDER);
            Game.BuildResult r = order(g, house, ORDER);
            BuildingsStacks stack = g.getBuildingManager().getStack(house);
            int onSite = stack == null ? 0 : stack.getUnderConstruction();
            if (may) {
                report(name + ": the city's order of " + ORDER + " goes through - ground taken, the quote's money paid, the Houses on site",
                        r == Game.BuildResult.SUCCESS && onSite == ORDER
                                && Math.abs(lm.getAllocatedSqFt() - allocated0 - ORDER * each) <= TOL * ground
                                && Math.abs(cash0 - g.getCash() - q.total) <= TOL * q.total,
                        r + ", $" + String.format("%,.2fk", cash0 - g.getCash()));
            } else {
                report(name + ": the city's order is refused NO_LAND - nothing allocated, no money taken, no House on site",
                        r == Game.BuildResult.NO_LAND && onSite == 0 && lm.getAllocatedSqFt() == allocated0 && g.getCash() == cash0,
                        String.valueOf(r));
            }

            double allocated1 = lm.getAllocatedSqFt();
            g.getEconomyManager().setSectorCash(Sectors.REAL_ESTATE, BUSINESS_CASH);
            boolean[] taken = new boolean[1];
            quietly(() -> taken[0] = g.buildFor(g.getSectorInvestor(Sectors.REAL_ESTATE), house, 1));
            check(name + ": a business's order (buildFor()) is " + (may ? "taken" : "refused") + ", its ground "
                            + (may ? "allocated" : "none of it allocated"),
                    taken[0] == may && Math.abs(lm.getAllocatedSqFt() - allocated1 - (may ? each : 0)) <= TOL * ground);
        }
    }

    /* =====================================================================
       2. A MINE ON EACH CLASS OF GROUND
       ===================================================================== */

    static void mineOnEachGround() {
        out.println("\n--- 2. a mine on each class of ground: a works stands anywhere, takes its ground from the class it stands on, and is not prepared ---");
        for (int c = World.FLAT; c <= World.VERY_STEEP; c++) {
            double[] share = new double[4];
            share[c] = 1;
            Game g = city("slopecheck-2-" + c, share[0], share[1], share[2], share[3]);
            LandManager lm = g.getLandManager();
            ironSites(g, 3);
            BuildingsTemplate mine = g.getBuildingManager().getTemplate(Mining.IRON_MINE);
            BuildingsTemplate house = template(g, HOUSE);
            double each = mine.getLandSqFt(), ground = ofClass(g, c);
            String name = GROUND[c];
            report("fixture: the city's whole dry ground is " + name + ", three iron sites by fiat, and room for the mine",
                    Math.abs(ground - lm.getOwnedSqFt()) <= TOL * ground && ground > each && g.hasDepositFor(mine, 1),
                    String.format("%,.0f sq ft, a mine's %,.0f", ground, each));
            check(name + ": an Iron Mine is a works that stands anywhere (Game.standsAnywhere()), and its ground is free for it as one",
                    Game.standsAnywhere(mine) && lm.canAllocate(each, mine.getSector(), true)
                            && lm.canAllocate(each, mine.getSector(), false) == World.buildable(World.GRASS, c));

            Game.BuildQuote q = g.quoteBuild(mine, 1);
            report(name + ": its quote carries no preparation - no foothill to prepare, no points, no money",
                    q.prepSqFt == 0 && q.prepPoints == 0 && q.prepCost == 0, String.format("%,.0f sq ft", q.prepSqFt));
            if (c == World.FOOTHILL) {
                // ...which is not for want of foothill: a House on the same ground is charged one.
                check("foothill: ...while a House on the same ground is charged one (the nothing above is the works', not the ground's)",
                        g.quoteBuild(house, 1).prepSqFt > 0);
            }

            Game.BuildResult r = order(g, mine, 1);
            double onSlope = lm.getWorksOnSlopeSqFt(), onFoothill = lm.getWorksOnFoothillSqFt();
            boolean slope = c == World.STEEP || c == World.VERY_STEEP;
            report(name + ": the order goes through, its ground the works' and from the " + name + " ground, none of it prepared",
                    r == Game.BuildResult.SUCCESS && Math.abs(lm.getWorksSqFt() - each) <= TOL * each
                            && Math.abs(onSlope - (slope ? each : 0)) <= TOL * each
                            && Math.abs(onFoothill - (c == World.FOOTHILL ? each : 0)) <= TOL * each
                            && lm.getPreparedSqFt() == 0 && lm.getBuildingsOnFoothillSqFt() == 0,
                    String.format("%s; works %,.0f, on the slope %,.0f, on foothill %,.0f, prepared %,.0f", r, lm.getWorksSqFt(), onSlope,
                            onFoothill, lm.getPreparedSqFt()));
        }
    }

    /* =====================================================================
       3. THE ORDER OF USE
       ===================================================================== */

    /** The mixed ground's shares: 40% flat, 30% foothill, 30% steep. */
    static final double FLAT_SHARE = 0.4, FOOTHILL_SHARE = 0.3, STEEP_SHARE = 0.3;

    static void orderOfUse() {
        out.println("\n--- 3. the order of use: buildings take flat before foothill, works steep, then foothill, then flat; idle steep is a works' ---");
        buildingsFirst();
        worksFirst();
    }

    static void buildingsFirst() {
        Game g = city("slopecheck-3a", FLAT_SHARE, FOOTHILL_SHARE, STEEP_SHARE, 0);
        LandManager lm = g.getLandManager();
        ironSites(g, 3);
        BuildingsTemplate house = template(g, HOUSE), mine = g.getBuildingManager().getTemplate(Mining.IRON_MINE);
        double each = house.getLandSqFt(), flat = lm.getFlatSqFt(), foothill = lm.getFoothillSqFt(), slope = lm.getSlopeSqFt();
        report("fixture: " + (int) (100 * FLAT_SHARE) + "% flat, " + (int) (100 * FOOTHILL_SHARE) + "% foothill, " + (int) (100 * STEEP_SHARE)
                        + "% steep of the dry ground, which is all the city owns",
                Math.abs(flat / lm.getOwnedSqFt() - FLAT_SHARE) <= 1e-6 && Math.abs(foothill / lm.getOwnedSqFt() - FOOTHILL_SHARE) <= 1e-6
                        && Math.abs(slope / lm.getOwnedSqFt() - STEEP_SHARE) <= 1e-6 && Math.abs(flat + foothill + slope - lm.getOwnedSqFt()) <= TOL * lm.getOwnedSqFt(),
                String.format("flat %,.0f, foothill %,.0f, steep %,.0f sq ft", flat, foothill, slope));

        int onFlat = (int) Math.floor(flat / each);
        Game.BuildResult first = order(g, house, onFlat);
        report("buildings, flat first: the Houses the flat holds go up and none stands on foothill yet, nothing prepared",
                first == Game.BuildResult.SUCCESS && lm.getAllocatedSqFt() <= flat && lm.getBuildingsOnFoothillSqFt() == 0 && lm.getPreparedSqFt() == 0,
                String.format("%d Houses, %,.0f of %,.0f sq ft of flat", onFlat, lm.getAllocatedSqFt(), flat));
        int more = 7;
        Game.BuildQuote q = g.quoteBuild(house, more);
        Game.BuildResult second = order(g, house, more);
        double excess = lm.getAllocatedSqFt() - flat;
        report("...then " + more + " more: what stands on foothill grows by exactly the excess over the flat, and that much is prepared (the quote's)",
                second == Game.BuildResult.SUCCESS && excess > 0 && Math.abs(lm.getBuildingsOnFoothillSqFt() - excess) <= TOL * flat
                        && Math.abs(lm.getPreparedSqFt() - excess) <= TOL * flat && Math.abs(q.prepSqFt - excess) <= TOL * flat,
                String.format("excess %,.0f, on foothill %,.0f, prepared %,.0f, quoted %,.0f sq ft", excess, lm.getBuildingsOnFoothillSqFt(),
                        lm.getPreparedSqFt(), q.prepSqFt));

        double room = lm.freeFor(house.getSector(), false);
        close("the ground free for a building is the flat and foothill left, to the foot - the steep not in it", room,
                flat + foothill - lm.getAllocatedSqFt(), TOL);
        int fit = (int) Math.floor(room / each);
        Game.BuildResult fill = order(g, house, fit);
        Game.BuildResult over = order(g, house, 1);
        double raw = lm.getOwnedSqFt() - lm.getAllocatedSqFt();
        report("...filled to the last House the flat and foothill hold, the next is refused NO_LAND though raw ground is left - the steep, only a works'",
                fill == Game.BuildResult.SUCCESS && over == Game.BuildResult.NO_LAND && raw >= each && raw >= slope
                        && lm.getBuildingsOnFoothillSqFt() <= foothill + TOL * flat && lm.getAllocatedSqFt() <= flat + foothill,
                String.format("%s, then %s; %,.0f sq ft owned and not built on, steep %,.0f", fill, over, raw, slope));

        // Idle steep ground is not free for a building and is free for a works, to the square foot.
        double idle = lm.idleSlopeSqFt(), forWorks = lm.freeFor(mine.getSector(), true), forBuilding = lm.freeFor(mine.getSector(), false);
        report("idle steep ground is not free for a building and is free for a works: the two differ by exactly the idle steep, all of it",
                Math.abs(idle - slope) <= TOL * slope && Math.abs((forWorks - forBuilding) - idle) <= TOL * slope,
                String.format("idle %,.0f, free for a works %,.0f, for a building %,.0f", idle, forWorks, forBuilding));
        Game.BuildResult mineOrder = order(g, mine, 1);
        check("...and a mine goes up on it, the steep taken, where a House was refused", mineOrder == Game.BuildResult.SUCCESS
                && Math.abs(lm.getWorksOnSlopeSqFt() - mine.getLandSqFt()) <= TOL * slope);
    }

    static void worksFirst() {
        Game g = city("slopecheck-3b", FLAT_SHARE, FOOTHILL_SHARE, STEEP_SHARE, 0);
        LandManager lm = g.getLandManager();
        ironSites(g, 50);
        BuildingsTemplate mine = g.getBuildingManager().getTemplate(Mining.IRON_MINE);
        double each = mine.getLandSqFt(), slope = lm.getSlopeSqFt(), foothill = lm.getFoothillSqFt(), owned = lm.getOwnedSqFt();
        double tol = TOL * owned;
        check("fixture: the mine is smaller than the steep ground, and the steep and foothill together smaller than the city",
                each < slope && slope + foothill < owned);

        int toFillSlope = (int) Math.floor(slope / each);
        boolean ok = true;
        for (int i = 0; i < toFillSlope; i++) ok &= order(g, mine, 1) == Game.BuildResult.SUCCESS;
        report("works, steep first: the mines the steep ground holds go up on it - none on foothill or flat, no building displaced",
                ok && Math.abs(lm.getWorksOnSlopeSqFt() - toFillSlope * each) <= tol && lm.getWorksOnFoothillSqFt() == 0
                        && Math.abs(lm.getWorksSqFt() - lm.getWorksOnSlopeSqFt()) <= tol && lm.getBuildingsOnFoothillSqFt() == 0,
                String.format("%d mines, %,.0f of %,.0f sq ft of steep, idle %,.0f", toFillSlope, lm.getWorksOnSlopeSqFt(), slope, lm.idleSlopeSqFt()));

        ok = order(g, mine, 1) == Game.BuildResult.SUCCESS;
        double past = lm.getWorksSqFt() - slope;
        report("...the next, past the steep: the steep all taken, the rest on foothill",
                ok && past > 0 && Math.abs(lm.getWorksOnSlopeSqFt() - slope) <= tol && Math.abs(lm.getWorksOnFoothillSqFt() - past) <= tol
                        && Math.abs(lm.idleSlopeSqFt()) <= tol,
                String.format("on the steep %,.0f, on foothill %,.0f, idle steep %,.0f", lm.getWorksOnSlopeSqFt(), lm.getWorksOnFoothillSqFt(), lm.idleSlopeSqFt()));

        int toPassFoothill = (int) Math.ceil((slope + foothill) / each) + 1;
        while (lm.getWorksSqFt() <= slope + foothill && ok) ok &= order(g, mine, 1) == Game.BuildResult.SUCCESS;
        double onFlat = lm.getWorksSqFt() - lm.getWorksOnSlopeSqFt() - lm.getWorksOnFoothillSqFt();
        report("...and past the foothill too: steep and foothill all taken, then the flat",
                ok && lm.getWorksSqFt() <= toPassFoothill * each + tol && onFlat > 0 && Math.abs(lm.getWorksOnSlopeSqFt() - slope) <= tol
                        && Math.abs(lm.getWorksOnFoothillSqFt() - foothill) <= tol
                        && Math.abs(onFlat - (lm.getWorksSqFt() - slope - foothill)) <= tol,
                String.format("works %,.0f: steep %,.0f, foothill %,.0f, flat %,.0f sq ft", lm.getWorksSqFt(), lm.getWorksOnSlopeSqFt(),
                        lm.getWorksOnFoothillSqFt(), onFlat));
    }

    /* =====================================================================
       4. THE PREPARATION'S PRICE, AGAINST THE CONSTANTS
       ===================================================================== */

    static void preparationPrice() {
        out.println("\n--- 4. the preparation's price: a House on foothill, against SitePreparation's constants and the House's own price ---");
        Game g = city("slopecheck-4", 0, 1, 0, 0);
        LandManager lm = g.getLandManager();
        BuildingsTemplate house = template(g, HOUSE);
        double each = house.getLandSqFt();
        int n = 10;

        check("the rate is its two terms: the earth a m2 of pad moves at its price, and the wall's face at its",
                SitePreparation.USD_PER_M2 == SitePreparation.EARTH_M3_PER_M2 * SitePreparation.EARTH_USD_PER_M3
                        + SitePreparation.WALL_M2_PER_M2 * SitePreparation.WALL_USD_PER_M2
                        && SitePreparation.EARTH_M3_PER_M2 == SitePreparation.MEAN_FOOTHILL_GRADE * SitePreparation.PAD_WIDTH_M / 4
                        && SitePreparation.WALL_M2_PER_M2 == SitePreparation.MEAN_FOOTHILL_GRADE);

        Game.BuildQuote q = g.quoteBuild(house, n);
        double perPoint = house.getFoundingCashCost() / house.getConstructionPoints();
        close("a House onto foothill prepares its land: prepSqFt is the House's square feet, the order's", q.prepSqFt, n * each, TOL);
        close("...its points are the square metres at SitePreparation's dollars a m2, in thousands, over the House's founding cash a point",
                q.prepPoints, q.prepSqFt * LandManager.SQ_M_PER_SQ_FT * SitePreparation.USD_PER_M2 / 1000 / perPoint, TOL);
        close("...and are the model's own (Game.preparationPoints(), SitePreparation.points())", q.prepPoints, g.preparationPoints(q.prepSqFt), TOL);
        close("...its money is those points at the House's non-material cost a point (what the builders charge for house work today)",
                q.prepCost, q.prepPoints * g.getBuildingManager().nonMaterialCost(house) / house.getConstructionPoints(), TOL);
        check("...all three more than nothing, and the order's points scale with its ground: " + n + " Houses' pad is " + n + " times one's",
                q.prepSqFt > 0 && q.prepPoints > 0 && q.prepCost > 0
                        && Math.abs(q.prepPoints - n * g.quoteBuild(house, 1).prepPoints) <= TOL * q.prepPoints);
        check("...and the measure LandManager gives is the quote's, and a works is measured none on the same ground",
                lm.foothillToPrepareSqFt(n * each, house.getSector(), false) == q.prepSqFt
                        && lm.foothillToPrepareSqFt(n * each, house.getSector(), true) == 0);

        // The same order on flat: the relief is read live, so the city's ground is made flat and asked again.
        double dry = g.getCityLand().totalKm2(CityLand.DRY);
        g.getCityLand().setCentreRelief(new double[] { dry, 0, 0, 0, 0 });
        Game.BuildQuote flat = g.quoteBuild(house, n);
        report("the same order on flat: prepSqFt, prepPoints and prepCost are all 0",
                flat.prepSqFt == 0 && flat.prepPoints == 0 && flat.prepCost == 0,
                String.format("%,.0f sq ft, %.4f points, $%.4fk", flat.prepSqFt, flat.prepPoints, flat.prepCost));
        double gross = g.getEconomyManager().withBuildersTax(q.prepCost);
        close("...so the foothill sticker is the flat's and the preparation's money", q.sticker - flat.sticker, q.prepCost, TOL);
        close("...and the foothill total exceeds the flat's by that money grossed up by the builders' sales tax (BuildQuote's own gross-up)",
                q.total - flat.total, gross, TOL);
        check("...and it is a longer wait: the pad's points are owed on the site before the first House finishes (quote months)",
                q.months > flat.months);
    }

    /* =====================================================================
       5. PREPARED ONCE
       ===================================================================== */

    /** The most months a fixture waits for the builders before it says they did not come. */
    static final int PATIENCE = 120;

    static void preparedOnce() {
        out.println("\n--- 5. prepared once: built on foothill, demolished, built again - no second preparation, the foothill prepared never falls ---");
        Game g = city("slopecheck-5", 0, 1, 0, 0);
        LandManager lm = g.getLandManager();
        Construction construction = g.getSectors().construction();
        BuildingsTemplate house = template(g, HOME_CARE);
        double each = house.getLandSqFt();
        int n = 2;
        double[] lowest = { Double.MAX_VALUE }, prepared = { 0 };
        boolean[] fell = { false };
        Runnable watch = () -> {
            double p = lm.getPreparedSqFt();
            if (p < prepared[0] - TOL) fell[0] = true;
            prepared[0] = Math.max(prepared[0], p);
            lowest[0] = Math.min(lowest[0], p);
        };

        Game.BuildQuote q = g.quoteBuild(house, n);
        Game.BuildResult r = order(g, house, n);
        watch.run();
        report("the first order of " + n + " Home Care Services onto foothill is charged its preparation, and the land counts the pad prepared",
                r == Game.BuildResult.SUCCESS && q.prepSqFt == n * each && Math.abs(lm.getPreparedSqFt() - n * each) <= TOL * n * each,
                String.format("%,.0f sq ft quoted, %,.0f prepared", q.prepSqFt, lm.getPreparedSqFt()));
        BuildingsStacks stack = g.getBuildingManager().getStack(house);
        int months = 0;
        while (stack.getUnderConstruction() > 0 && months++ < PATIENCE) {
            quietly(() -> g.simulateMonths(1));
            watch.run();
        }
        check("fixture: the Home Care Services are built (" + months + " months)", stack.getUnderConstruction() == 0 && stack.getQuantity() == n);

        boolean[] ordered = { false };
        quietly(() -> ordered[0] = g.demolish(house, n));
        months = 0;
        while (lm.getAllocatedSqFt() > TOL && months++ < PATIENCE) {
            quietly(() -> g.simulateMonths(1));
            watch.run();
        }
        report("they are demolished and their ground is released: nothing stands on foothill, and the pad is still prepared",
                ordered[0] && lm.getAllocatedSqFt() <= TOL && lm.getBuildingsOnFoothillSqFt() == 0
                        && Math.abs(lm.getPreparedSqFt() - n * each) <= TOL * n * each,
                String.format("allocated %,.0f, on foothill %,.0f, prepared %,.0f sq ft after %d months", lm.getAllocatedSqFt(),
                        lm.getBuildingsOnFoothillSqFt(), lm.getPreparedSqFt(), months));

        double backlog0 = construction.getBacklogPoints();
        Game.BuildQuote again = g.quoteBuild(house, n);
        Game.BuildResult second = order(g, house, n);
        watch.run();
        report("built again on the same ground: no second preparation - prepSqFt, prepPoints and prepCost 0, only the Home Care Services' own points billed",
                second == Game.BuildResult.SUCCESS && again.prepSqFt == 0 && again.prepPoints == 0 && again.prepCost == 0
                        && Math.abs(construction.getBacklogPoints() - backlog0 - n * (double) house.getConstructionPoints()) <= TOL * n * house.getConstructionPoints()
                        && Math.abs(lm.getPreparedSqFt() - n * each) <= TOL * n * each,
                String.format("%,.0f sq ft to prepare, prepared %,.0f", again.prepSqFt, lm.getPreparedSqFt()));

        int extra = 1;
        Game.BuildQuote beyond = g.quoteBuild(house, extra);
        order(g, house, extra);
        watch.run();
        report("...and " + extra + " Home Care Services past the pad prepare exactly their own ground, the pad's not again",
                Math.abs(beyond.prepSqFt - extra * each) <= TOL * extra * each && Math.abs(lm.getPreparedSqFt() - (n + extra) * each) <= TOL * (n + extra) * each,
                String.format("%,.0f sq ft quoted, prepared %,.0f", beyond.prepSqFt, lm.getPreparedSqFt()));
        check("the foothill prepared never fell, month by month, through the build, the demolition and the rebuild", !fell[0] && lowest[0] >= 0);
    }

    /* =====================================================================
       6. BUILDERS' WORK IN THE BOOKS
       ===================================================================== */

    /** What the months of an order audited: those that did not close or hold, and how many were. */
    static final class Months {
        int played, moneyOff, gdpOff, gdpBooksOff, driftOff, firstFinished = -1;
        double applied, appliedAtFirst, worstResidual;
        /** Each month's recognised work (the builders') and the construction GDP counted that month, in order. */
        final List<Double> recognised = new ArrayList<>(), counted = new ArrayList<>();
    }

    /** The playtest's own test of a month's money audit (LongPlaytest.audit()): no residual over a cent, or none over a ten-millionth of the money moved. */
    static boolean closes(MoneyAudit.Result r) {
        return Double.isFinite(r.residual) && (Math.abs(r.residual) <= .01 || r.relative() <= 1e-7);
    }

    /** One month, played and audited as the playtest audits one: the money, the GDP identity, GDP's construction against the builders' own books, and every sector's statements (SectorStatementCheck.audit()). */
    static void month(Game g, BuildingsStacks stack, Months m) {
        Construction construction = g.getSectors().construction();
        int before = stack.getQuantity();
        quietly(() -> g.simulateMonths(1));
        m.played++;
        m.applied += stack.getLastApplied();
        if (m.firstFinished < 0 && stack.getQuantity() > before) {
            m.firstFinished = m.played;
            m.appliedAtFirst = m.applied;
        }
        MoneyAudit.Result r = g.getLastMoneyAudit();
        if (!closes(r)) m.moneyOff++;
        m.worstResidual = Math.max(m.worstResidual, r.relative());
        if (Math.abs(g.getPostAuditDrift()) > 1e-6) m.driftOff++;
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        double terms = Math.abs(na.getConsumption()) + Math.abs(na.getInvestment()) + Math.abs(na.getGovernment()) + Math.abs(na.getNetExports());
        double gap = Math.abs(na.getGdp() - (na.getConsumption() + na.getInvestment() + na.getGovernment() + na.getNetExports()));
        if (gap > 1e-9 * Math.max(1, terms) && !(Math.abs(na.getGdp()) <= terms * 1e-12)) m.gdpOff++;
        // GDP's construction is what the builders recognised, a tick behind (Game.nextMonth(): constructionWorkDone is last month's figure,
        // booked in last month's ledger) - the pad's work among it. Each month's is compared with the one before's after the loop.
        m.recognised.add(construction.getRecognisedThisMonth());
        m.counted.add(na.getInvestmentConstruction());
        SectorStatementCheck.audit(g);
    }

    /** Counts the months in which what GDP counted as construction was not the work the builders recognised the month before. */
    static int gdpOffTheBooks(Months m) {
        int off = 0;
        for (int i = 1; i < m.counted.size(); i++) {
            if (Math.abs(m.counted.get(i) - m.recognised.get(i - 1)) > 1e-9 * Math.max(1, m.recognised.get(i - 1))) off++;
        }
        return off;
    }

    /** What SectorStatementCheck's tallies hold of the identities it checks: how many it checked, how many missed by more than a cent. */
    static int[] statements() {
        int checked = 0, missed = 0;
        for (SectorStatementCheck.Tally t : SectorStatementCheck.tallies.values()) {
            checked += t.checked;
            missed += t.missed;
        }
        return new int[] { checked, missed };
    }

    /** The first identity SectorStatementCheck's tallies missed, in its words: "none" when none. */
    static String statementsMissed() {
        for (java.util.Map.Entry<String, SectorStatementCheck.Tally> e : SectorStatementCheck.tallies.entrySet()) {
            if (e.getValue().missed > 0) return e.getKey() + ": " + e.getValue().words();
        }
        return "none";
    }

    /** Houses ordered in 6, enough that the pad alone (their land's points) is more than a month of the builders' work. */
    static final int HOUSES = 100;

    static void inTheBooks() {
        out.println("\n--- 6. builders' work in the books: the city's order and a business's, owed on the site, billed to Construction, audited month by month ---");
        for (int who = 0; who < 2; who++) {
            SectorStatementCheck.bondsSoonBefore.clear();
            SectorStatementCheck.tallies.clear();
            boolean business = who == 1;
            String by = business ? "a business's order (buildFor())" : "the city's order (buildStack())";
            Game g = city("slopecheck-6-" + who, 0, 1, 0, 0);
            if (business) g.getEconomyManager().setSectorCash(Sectors.REAL_ESTATE, BUSINESS_CASH);
            LandManager lm = g.getLandManager();
            Construction construction = g.getSectors().construction();
            BuildingsTemplate house = template(g, HOUSE);
            double backlog0 = construction.getBacklogPoints(), unearned0 = construction.getUnearnedRevenue(), billed0 = construction.getBilledPoints();
            Game.BuildQuote q = g.quoteBuild(house, HOUSES);
            double own = HOUSES * (double) house.getConstructionPoints();
            report("fixture (" + by + "): the pad alone is more than a month of the crews' work, so it cannot be paid in the month it is ordered",
                    q.prepPoints > g.getBuildingOutputAtEveryPost() && q.prepPoints > 0,
                    String.format("pad %.1f points, the crews' month %,d", q.prepPoints, g.getBuildingOutputAtEveryPost()));

            boolean[] ordered = new boolean[1];
            quietly(() -> ordered[0] = business ? g.buildFor(g.getSectorInvestor(Sectors.REAL_ESTATE), house, HOUSES)
                    : g.buildStack(house, HOUSES, false) == Game.BuildResult.SUCCESS);
            BuildingsStacks stack = g.getBuildingManager().getStack(house);
            report(by + ": Construction's backlog rises by the Houses' points and the pad's, its unearned revenue by the bill",
                    ordered[0] && Math.abs(construction.getBacklogPoints() - backlog0 - (own + q.prepPoints)) <= TOL * (own + q.prepPoints)
                            && Math.abs(construction.getUnearnedRevenue() - unearned0 - q.total) <= TOL * q.total
                            && Math.abs(construction.getBilledPoints() - billed0 - (own + q.prepPoints)) <= TOL * (own + q.prepPoints),
                    String.format("backlog +%.3f of %.3f + %.3f, unearned +%,.3fk of the quote's %,.3fk", construction.getBacklogPoints() - backlog0,
                            own, q.prepPoints, construction.getUnearnedRevenue() - unearned0, q.total));
            check("...and the site owes the pad: its progress is below nothing by the pad's points, no House finished",
                    stack != null && Math.abs(stack.getConstructionProgress() + q.prepPoints) <= TOL * q.prepPoints
                            && stack.getQuantity() == 0 && stack.getUnderConstruction() == HOUSES);
            check("...the foothill the land counts prepared is the order's ground, once", Math.abs(lm.getPreparedSqFt() - q.prepSqFt) <= TOL * q.prepSqFt
                    && Math.abs(q.prepSqFt - HOUSES * house.getLandSqFt()) <= TOL * q.prepSqFt);

            Months m = new Months();
            while (stack.getUnderConstruction() > 0 && m.played < PATIENCE) month(g, stack, m);
            report(by + ": the first House finishes only once the pad is paid - the crews' points up to then are at least the pad and a House",
                    m.firstFinished > 1 && m.appliedAtFirst >= q.prepPoints + house.getConstructionPoints() - 1e-6,
                    String.format("month %d, %.3f points applied against pad %.3f + House %d", m.firstFinished, m.appliedAtFirst, q.prepPoints,
                            house.getConstructionPoints()));
            report("...and every House is built, the points applied the order's whole - the Houses' and the pad's",
                    stack.getUnderConstruction() == 0 && stack.getQuantity() == HOUSES
                            && Math.abs(m.applied - (own + q.prepPoints)) <= 1e-6 * (own + q.prepPoints),
                    String.format("%d months, %.3f applied of %.3f", m.played, m.applied, own + q.prepPoints));
            report("...all that was billed is earned: the order book is back to what it was, in points and in money",
                    Math.abs(construction.getBacklogPoints() - backlog0) <= 1e-6 * (own + q.prepPoints)
                            && Math.abs(construction.getUnearnedRevenue() - unearned0) <= 1e-6 * q.total,
                    String.format("backlog %+.6f, unearned %+.6fk", construction.getBacklogPoints() - backlog0,
                            construction.getUnearnedRevenue() - unearned0));
            double firstWork = m.recognised.get(0), counted = m.counted.size() > 1 ? m.counted.get(1) : Double.NaN;
            report("...GDP counted the pad's work as construction: the first month's work, when no House had finished, is what GDP counts the month after",
                    firstWork > 0 && m.firstFinished > 1 && Math.abs(counted - firstWork) <= 1e-9 * firstWork,
                    String.format("%,.3fk recognised in month 1, %,.3fk counted in month 2", firstWork, counted));
            int[] statements = statements();
            report("over those months the money audit closed, the GDP identity held and GDP's construction was the builders' recognised work, post-audit drift"
                            + " was nil, and every sector's statements held",
                    m.played > 0 && m.moneyOff == 0 && m.gdpOff == 0 && gdpOffTheBooks(m) == 0 && m.driftOff == 0 && statements[1] == 0 && statements[0] > 0,
                    String.format("%d months; the audit off %d, GDP off %d + %d, drift off %d; %,d statement identities checked, %d missed (%s); worst residual %.2e",
                            m.played, m.moneyOff, m.gdpOff, gdpOffTheBooks(m), m.driftOff, statements[0], statements[1], statementsMissed(), m.worstResidual));
        }
    }

    /* =====================================================================
       7. A STREET, A BUILDING AND A MINE ON THE MAP, BY RELIEF
       ===================================================================== */

    /** Where the bands of 7's district lie, in plots: foothill the first cell's columns; steep, then very steep, then ice rows below. */
    static final int FOOT_COLUMNS = DistrictPlan.CELL,
            STEEP_FROM = 3 * DistrictPlan.CELL + DistrictPlan.CELL / 8, STEEP_ROWS = DistrictPlan.CELL / 4,
            VERY_ROWS = DistrictPlan.CELL / 4, ICE_ROWS = DistrictPlan.CELL / 8;

    /** The bands' rows: steep [STEEP_FROM, VERY_FROM), very steep [VERY_FROM, ICE_FROM), ice [ICE_FROM, ICE_TO). */
    static final int VERY_FROM = STEEP_FROM + STEEP_ROWS, ICE_FROM = VERY_FROM + VERY_ROWS, ICE_TO = ICE_FROM + ICE_ROWS;

    /** Jerus's mix on a district of flat ground, fully owned, at the fullest fill the plans are checked at; banded, the relief and ice above laid over it. */
    static DistrictPlan.Input district(BuildingVisual.Type[] types, boolean banded) {
        DistrictPlan.Input in = PlanCheck.flatMix(types, PlanCheck.FULL_FILL, true, false);
        if (!banded) return in;
        int f = DistrictPlan.FRAME;
        for (int y = 0; y < f; y++) {
            for (int x = 0; x < f; x++) {
                int q = y * f + x;
                if (y >= ICE_FROM && y < ICE_TO) in.terrain[q] = World.ICE;
                else if (y >= VERY_FROM && y < ICE_FROM) in.relief[q] = World.VERY_STEEP;
                else if (y >= STEEP_FROM && y < VERY_FROM) in.relief[q] = World.STEEP;
                else if (x < FOOT_COLUMNS) in.relief[q] = World.FOOTHILL;
            }
        }
        return in;
    }

    /** What a plan laid on each kind of ground of its input: street plots, and building plots, on buildable flat, on foothill, and on ground no street or building may take. */
    record Laid(long streetFlat, long streetFoot, long streetUnfit, long buildFlat, long buildFoot, long buildUnfit) { }

    /** Counts what a plan laid by the ground the input has under each plot (World.buildable() of its cover and relief). */
    static Laid laid(DistrictPlan p, DistrictPlan.Input in) {
        int f = DistrictPlan.FRAME;
        boolean[] built = new boolean[DistrictPlan.AREA];
        for (int i = 0; i < p.buildings; i++) {
            for (int y = p.by[i]; y < p.by[i] + p.bh[i]; y++) {
                for (int x = p.bx[i]; x < p.bx[i] + p.bw[i]; x++) built[y * f + x] = true;
            }
        }
        long[] n = new long[6];
        for (int q = 0; q < DistrictPlan.AREA; q++) {
            int kind = !World.buildable(in.terrain[q], in.relief[q]) ? 2 : in.relief[q] == World.FOOTHILL ? 1 : 0;
            if (p.street[q] != 0) n[kind]++;
            if (built[q]) n[3 + kind]++;
        }
        return new Laid(n[0], n[1], n[2], n[3], n[4], n[5]);
    }

    /** The building types the plans are drawn with (BuildingVisual.table() of the catalogue, which says as it loads). */
    static BuildingVisual.Type[] planTypes() {
        BuildingVisual.Type[][] types = new BuildingVisual.Type[1][];
        quietly(() -> types[0] = BuildingVisual.table(new BuildingCatalog().load()));
        return types[0];
    }

    /** Whether a plan's building stands on plot q of its frame. */
    static boolean plotBuilt(DistrictPlan p, int q) {
        int f = DistrictPlan.FRAME, x = q % f, y = q / f;
        for (int i = 0; i < p.buildings; i++) {
            if (x >= p.bx[i] && x < p.bx[i] + p.bw[i] && y >= p.by[i] && y < p.by[i] + p.bh[i]) return true;
        }
        return false;
    }

    static void onTheMap() {
        out.println("\n--- 7. a street, a building and a mine on the map by relief: the plan's ground, buildable only where flat or foothill ---");
        boolean rule = true;
        for (int relief = World.FLAT; relief <= World.VERY_STEEP; relief++) {
            rule &= World.buildable(World.GRASS, relief) == (relief == World.FLAT || relief == World.FOOTHILL)
                    && World.buildable(World.FOREST, relief) == World.buildable(World.GRASS, relief) && !World.buildable(World.ICE, relief);
        }
        check("the predicate the plan and the map's room are counted on: dry ground is buildable flat and foothill, not steep, very steep, and ice never", rule);
        check("...and a mine's site, counted on World.dry() whatever the slope (CityMap.measure()), may stand on steep ground a building may not:"
                + " dry and workable, not buildable - on steep and on very steep ground alike", World.dry(World.GRASS) && World.workable(World.GRASS)
                && World.workable(World.ICE) && !World.workable(World.SALT) && !World.buildable(World.GRASS, World.STEEP)
                && !World.buildable(World.GRASS, World.VERY_STEEP));

        BuildingVisual.Type[] types = planTypes();
        DistrictPlan.Input in = district(types, true), control = district(types, false);
        // A mine's site on the steep band, and one on the flat: sites are ground only a mine takes first.
        int f = DistrictPlan.FRAME, steepSite = (STEEP_FROM + 2) * f + DistrictPlan.CELL * 3 / 2, flatSite = (DistrictPlan.CELL / 2) * f + DistrictPlan.CELL * 3 / 2;
        in.site[steepSite] = DistrictPlan.SITE_FIELD;
        in.site[flatSite] = DistrictPlan.SITE_FIELD;
        DistrictPlan banded = DistrictPlan.make(in), open = DistrictPlan.make(control);
        Laid b = laid(banded, in);
        report("a district with a foothill column of cells, bands of steep, very steep and ice: streets and buildings on its flat and its foothill",
                b.streetFlat() > 0 && b.streetFoot() > 0 && b.buildFlat() > 0 && b.buildFoot() > 0,
                String.format("streets %,d flat + %,d foothill, buildings %,d + %,d plots", b.streetFlat(), b.streetFoot(), b.buildFlat(), b.buildFoot()));
        report("...and on no steep, very steep or ice plot: no street, no building",
                b.streetUnfit() == 0 && b.buildUnfit() == 0, String.format("%d street plots, %d building plots on them", b.streetUnfit(), b.buildUnfit()));
        // The bands are what keeps them off: the same plots, with nothing the matter with them, are laid on.
        long wanted = 0, wantedStreets = 0;
        boolean[] built = new boolean[DistrictPlan.AREA];
        for (int i = 0; i < open.buildings; i++) {
            for (int y = open.by[i]; y < open.by[i] + open.bh[i]; y++) {
                for (int x = open.bx[i]; x < open.bx[i] + open.bw[i]; x++) built[y * f + x] = true;
            }
        }
        for (int q = 0; q < DistrictPlan.AREA; q++) {
            if (World.buildable(in.terrain[q], in.relief[q])) continue;
            if (open.street[q] != 0) wantedStreets++;
            if (built[q]) wanted++;
        }
        report("fixture: the same district with its ground flat lays streets and buildings on those very plots - it is the slope that keeps them off",
                wantedStreets > 0 && wanted > 0, String.format("%,d street plots and %,d building plots over the bands' plots", wantedStreets, wanted));
        check("a mine's site on the steep band - dry ground, not buildable - is taken by no building the plan lays, nor by a street (a mine's own plots are the map's, "
                        + "counted on World.dry())",
                in.site[steepSite] == DistrictPlan.SITE_FIELD && World.dry(in.terrain[steepSite]) && !World.buildable(in.terrain[steepSite], in.relief[steepSite])
                        && !plotBuilt(banded, steepSite) && banded.street[steepSite] == 0);
    }

    /* =====================================================================
       8. FOUNDING ON BUILDABLE GROUND (the real Earth)
       ===================================================================== */

    /** The Earth's data where it is named or lies at its place, or null - and the one line saying so. */
    static EarthData theEarth() {
        Path p = EarthData.locate();
        String named = System.getProperty(EarthData.PROPERTY);
        if (named != null && !named.isBlank() && (p == null || !Files.isRegularFile(p))) {
            report("the file -D" + EarthData.PROPERTY + " names is there", false, named);
            return null;
        }
        if (p == null) {
            out.println("\nearth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE
                    + ": sections 8 and 9, which found cities on the real Earth, were not run");
            return null;
        }
        EarthSite.Data game = EarthSite.gameData();
        if (!game.here() || !game.reader().identity().equals(EarthData.EARTH_2)) {
            out.println("\nthe game's Earth is not earth-2 (" + game.words() + "): sections 8 and 9 were not run");
            return null;
        }
        return game.reader();
    }

    static final double[] MEDELLIN = { 6.25, -75.57 };

    /** Everest's summit: ice (EarthTerrainCheck's SUMMIT) - and a spot on its steep southern slopes, no ice about it, no buildable ground within 5 km. */
    static final double[] SUMMIT = EarthTerrainCheck.SUMMIT, SLOPES = { 27.60, 86.70 };

    static World.Key key(double[] at) {
        return World.Key.earth(Founding.DEFAULT_WORLD_SEED, at[0], at[1], EarthData.EARTH_2);
    }

    static void foundingOnBuildableGround(EarthData file) {
        out.println("\n--- 8. founding on buildable ground: Medellin's site and its test points, Everest's slopes refused in the new words ---");
        if (file == null) {
            out.println("earth-2.bin not named: Medellin's founding site, Everest's slopes and the plan over Medellin's ground were not read");
            return;
        }
        World.Key medellin = key(MEDELLIN);
        EarthSite.Card card = EarthSite.measure(medellin);
        World w = World.of(medellin);
        EarthTerrain e = w.earthTerrain();
        long sx = card.siteX(), sy = card.siteY();
        double cx = sx + 0.5, cy = sy + 0.5;
        int buildable = World.buildable(w.terrainAt(sx, sy), w.reliefAt(sx, sy)) ? 1 : 0;
        for (int q = 0; q < 8; q++) {
            double a = q * Math.PI / 4;
            long px = (long) Math.floor(cx + World.SITE_DRY_PLOTS * StrictMath.cos(a)), py = (long) Math.floor(cy + World.SITE_DRY_PLOTS * StrictMath.sin(a));
            if (World.buildable(w.terrainAt(px, py), w.reliefAt(px, py))) buildable++;
        }
        report("Medellin: a site, the world's founding site, and it and its eight test points (World.SITE_DRY_PLOTS round it) are all buildable ground",
                card.hasSite() && card.foundable() && sx == w.foundingX() && sy == w.foundingY() && buildable == 9 && e.siteBuildable(sx, sy),
                String.format("%d of 9 buildable; the site %.0f m from the spot, ground %s", buildable, card.snappedM(), EarthSite.reliefWords(card)));

        // Everest: the summit is ice, and ice keeps its own words (asked first); the steep flank has no buildable ground within the snap.
        World.Key summit = key(SUMMIT), slopes = key(SLOPES);
        check("Everest's summit is refused as ice, in the ice's own words (snow and ice win over slope): " + EarthSite.foundingProblem(summit),
                EarthSite.ICE_WORDS.equals(EarthSite.foundingProblem(summit)));
        EarthTerrain top = World.of(slopes).earthTerrain();
        long[] near = top.siteNear(), ground = top.snapGround(), beyond = top.siteBeyond();
        String words = EarthSite.foundingProblem(slopes);
        report("a spot among Everest's steep southern slopes: the snap finds no ground a building may stand on within 5 km, and half the dry ground there"
                        + " or more is steep or very steep",
                near[0] < 0 && ground[0] > 0 && 2 * ground[1] >= ground[0], String.format("%,d of %,d dry plots steep within the snap", ground[1], ground[0]));
        double east = beyond[0] - EarthTerrain.spotPlot(), south = beyond[1] - EarthTerrain.spotPlot(), km = Math.hypot(east, south) * World.PLOT_M / 1000;
        String way = LandMap.COMPASS[(int) Math.floorMod(Math.round(StrictMath.toDegrees(StrictMath.atan2(east, -south)) / 45), 8L)];
        String wants = EarthSite.SLOPE_WORDS + " The nearest buildable ground is "
                + (km < 10 ? String.format(Locale.ROOT, "%.1f km", km) : String.format(Locale.ROOT, "%,.0f km", km)) + " " + way + ".";
        report("...it is refused in the slope words, naming the nearest buildable ground and its way (EarthTerrain.siteBeyond()), which is buildable",
                beyond[0] >= 0 && top.siteBuildable(beyond[0], beyond[1]) && words != null && words.equals(wants)
                        && wants.equals(Founding.defaults().withWorldKey(slopes).problem()), words);
        EarthSite.Card refused = EarthSite.measure(slopes);
        check("...and the card says so: no site, not foundable, the same words", !refused.hasSite() && !refused.foundable() && wants.equals(refused.refusal()));

        // A plan over Medellin's real ground: the district about the site, its ground read as the map reads it (CityMap.readGround()).
        BuildingVisual.Type[] types = planTypes();
        DistrictPlan.Input in = PlanCheck.flatMix(types, PlanCheck.FILLS[0], true, false);
        in.x0 = Math.floorDiv(sx, (long) World.DISTRICT) * World.DISTRICT;
        in.y0 = Math.floorDiv(sy, (long) World.DISTRICT) * World.DISTRICT;
        in.hubX = sx - in.x0;
        in.hubY = sy - in.y0;
        CityMap.readGround(in, medellin);
        long steep = 0, buildablePlots = 0;
        for (int q = 0; q < DistrictPlan.AREA; q++) {
            if (World.dry(in.terrain[q]) && in.relief[q] >= World.STEEP) steep++;
            if (World.buildable(in.terrain[q], in.relief[q])) buildablePlots++;
        }
        DistrictPlan p = DistrictPlan.make(in);
        Laid l = laid(p, in);
        report("a plan over Medellin's real ground, read as the map reads it: the district has steep ground, and streets and buildings stand on its buildable plots and on no steep one",
                steep > 0 && l.streetFlat() + l.streetFoot() > 0 && l.buildFlat() + l.buildFoot() > 0 && l.streetUnfit() == 0 && l.buildUnfit() == 0,
                String.format("%,d steep plots of %,d dry; streets %,d, buildings %,d plots on buildable ground, %d + %d on none", steep,
                        steep + buildablePlots, l.streetFlat() + l.streetFoot(), l.buildFlat() + l.buildFoot(), l.streetUnfit(), l.buildUnfit()));
    }

    /* =====================================================================
       9. HOW OFTEN IT FIRES IN A REAL RUN (Medellin, the real Earth)
       ===================================================================== */

    /** The months the Medellin run plays: enough for the land office's offers to have brought the city steep ground (at 1,000 it owns none; at 2,000, about a tenth of it) - the section about twenty seconds. */
    static final int RUN_MONTHS = 2000;

    static void inARealRun(EarthData file) {
        out.println("\n--- 9. how often it fires in a real run: a Medellin city on the real Earth, " + RUN_MONTHS + " months, played as the playtest plays ---");
        if (file == null) {
            out.println("earth-2.bin not named: the Medellin run was not played");
            return;
        }
        long started = System.currentTimeMillis();
        LongPlaytest.findings.clear();
        PrintStream was = LongPlaytest.out;
        LongPlaytest.out = quiet;
        Game g = new Game(GameFiles.scratch("slopecheck-medellin"), LongPlaytest.earthFounding("medellin"));
        try {
            quietly(() -> {
                g.run();
                g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
                g.setRolloverMode(LongPlaytest.ROLLOVER);
                g.setRescueMode(TreasuryFund.RescueMode.AUTOMATIC);
                g.getCityMap();
                LongPlaytest.layoutOn = true;
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
                int stop = 0;
                while (g.getMonth() < RUN_MONTHS) {
                    stop++;
                    int skip = switch (stop % 6) { case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120; };
                    LongPlaytest.run(g, Math.min(skip, RUN_MONTHS - g.getMonth()));
                    if (g.getMonth() >= RUN_MONTHS) break;
                    for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                        if (LongPlaytest.advise(g) == null) break;
                        LongPlaytest.run(g, 1);
                    }
                    LongPlaytest.run(g, 2);
                }
            });
        } finally {
            LongPlaytest.out = was;
        }
        LandManager lm = g.getLandManager();
        Construction construction = g.getSectors().construction();
        double prepared = lm.getPreparedSqFt(), billed = g.preparationPointsBilled(), all = construction.getBilledPoints();
        double km2 = LandManager.km2(prepared);
        out.printf("      %d months in %d s: %,d orders charged a preparation, %.4f km2 of foothill prepared, %,.1f points of %,.1f billed (%.2f%%), $%,.1fk before the builders' tax%n",
                g.getMonth(), (System.currentTimeMillis() - started) / 1000, g.preparationOrders(), km2, billed, all, all > 0 ? 100 * billed / all : 0,
                g.preparationMoneyBilled());
        out.printf("      the city's ground: flat %.3f km2, foothill %.3f, steep %.3f (very steep %.3f); %.3f km2 built on, %.3f of it by works, %.3f of that on steep ground%n",
                LandManager.km2(lm.getFlatSqFt()), LandManager.km2(lm.getFoothillSqFt()), LandManager.km2(lm.getSlopeSqFt()),
                g.getCityLand().totalReliefKm2(CityLand.RELIEF_VERY_STEEP), LandManager.km2(lm.getAllocatedSqFt()), LandManager.km2(lm.getWorksSqFt()),
                LandManager.km2(lm.getWorksOnSlopeSqFt()));
        double buildings = lm.getAllocatedSqFt() - lm.getWorksSqFt(), eps = TOL * Math.max(1, lm.getOwnedSqFt());
        check("the run played its months and found nothing: every month passed the playtest's audit and the layout watcher, "
                        + LongPlaytest.findings.keySet(), g.getMonth() >= RUN_MONTHS && LongPlaytest.findings.isEmpty());
        check("the preparation fires: orders charged one, foothill prepared, its points billed", g.preparationOrders() > 0 && prepared > 0 && billed > 0);
        check("...its points are a share of the points billed, more than nothing and less than the whole", all > billed && billed > 0);
        check("...the foothill buildings stand on never exceeds the foothill, and it is all prepared",
                lm.getBuildingsOnFoothillSqFt() <= lm.getFoothillSqFt() + eps && lm.getPreparedSqFt() >= lm.getBuildingsOnFoothillSqFt() - eps);
        report("fixture: the city owns steep ground by the end, which the land office's offers carry with the rest - more than the flat and foothill it can build on",
                lm.getSlopeSqFt() > 0 && lm.getOwnedSqFt() > lm.getFlatSqFt() + lm.getFoothillSqFt(),
                String.format("%.3f km2 steep of %.3f km2 owned", LandManager.km2(lm.getSlopeSqFt()), LandManager.km2(lm.getOwnedSqFt())));
        check("...and no building stands on it: buildings never exceed the flat and foothill, the works' part on the steep alone, and the steep not used is idle",
                buildings <= lm.getFlatSqFt() + lm.getFoothillSqFt() + eps && lm.getWorksOnSlopeSqFt() <= lm.getSlopeSqFt() + eps
                        && Math.abs(lm.getWorksOnSlopeSqFt() + lm.idleSlopeSqFt() - lm.getSlopeSqFt()) <= eps);
        check("...the points billed for preparation are what the foothill prepared comes to (an instant build prepares without charge, so never more)",
                billed <= g.preparationPoints(prepared) * (1 + 1e-9) + 1e-9);
    }
}
