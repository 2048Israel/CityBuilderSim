package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The roads over their lives, and a gravel road paved (0.7.70, batch N1):
 * BuildAdvice's A ROAD OVER ITS LIFE and ConstructionControl's F, held to
 * their own arithmetic in a played city.
 *
 * WHY. Jerus: "the game still recommends gravel roads, even when i think
 * paved roads are better, also ... make it an option to upgrade from gravel
 * to paved, but not from paved to highway, and that the build menu allows
 * and recommends this if better, total cost is higher than just building
 * paved." The advice priced a road's ground from 0.7.51, but Build's road
 * cards priced a trip without it and the test player ranked the roads by
 * their founding cash cost - gravel first everywhere (runs/fixN1-notes.md).
 * Now one figure, a road's cost over its life a trip it takes off the road,
 * ranks the advice, draws the cards' first bar and orders the test player's
 * roads; and a gravel road can be paved, at a price above a Paved Road's
 * less the gravel's own, on its own ground, open while the works go on.
 * Paving never fires in the default playtest (the playtest's report counts
 * it: a new road is cheaper there at every look), so this is where it is
 * played.
 *
 * What this has to prove:
 *   1. A ROAD'S LIFE, BY THE MODEL'S OWN RULES: every road and line weighed
 *      for the road is its quote, its ground at landValue(), and running()
 *      for LIFE_MONTHS at the real rate - each part recomputed here, to the
 *      bit; the repairs in running() are what the month's maintenance bill
 *      charges the city for one more such road.
 *   2. THE ADVICE TAKES THE LEAST OVER ITS LIFE, AND GRAVEL CAN STILL WIN:
 *      with no ground free, the road the advice suggests is the candidate
 *      least over its life a trip; with the land office's prices at nothing
 *      it is the gravel road, past the crossover the model's figures give
 *      the paved road, and past the next the highway.
 *   3. THE ROAD CARDS SAY THE SAME: bar 1 is the road's life a trip off the
 *      road and bar 2 its ground a trip, to the bit, and the card tagged
 *      cheapest is the road the advice ranks first among the three.
 *   4. THE PAVING'S PRICE: a Paved Road's work and the take-up at
 *      DEMOLITION_SHARE of the gravel road's, the material of a Paved Road
 *      less a gravel road's, no ground, a Paved Road's wait; a gravel road
 *      and its paving cost more than a Paved Road built outright. Since
 *      0.7.83 (batch O6) plus its surface's bitumen, a Paved Road's 64.25 t,
 *      in the quote - as a new Paved Road's and an Elevated Highway's are.
 *   5. PAVING, ONE ROAD AND MANY, PLAYED: a paving of one and a paving of
 *      three behind a new Paved Road; the treasury pays the quote; each
 *      gravel road carries its traffic until its own Paved Road opens, the
 *      new one first; the network's capacity is the roads standing every
 *      month; the land ledger is the footprint every month and frees the
 *      ground between the two roads as each opens; the money audit closes.
 *   6. WHAT IT REFUSES: more than there are to pave; a Paved Road site set
 *      to stop; short of the cash, with nothing moved; and while it paves
 *      the Paved Road site is not stopped and a gravel road being paved is
 *      not demolished.
 *   7. A SAVE AND A LOAD, TO THE CENT: a paving in progress reads back, its
 *      ground and the ledger to the bit, and the two cities play its
 *      months alike; an older save, with no pavings, loads with none.
 *   8. THE ADVICE OFFERS THE PAVING WHEN IT BEATS A NEW PAVED ROAD, and not
 *      before: at the crossover the model's figures give, the suggestion is
 *      the paving - its quote, its gravel roads gone, the ground it frees -
 *      and the Gravel Road card's paving says so; "Build all three" leaves
 *      it out.
 *   9. THE TEST PLAYER ASKS THE ADVICE: its first road move is the road the
 *      advice ranks first, and the paving where it beats a new Paved Road
 *      and the roads - a move that paves.
 *
 * Every fixture causes its condition.
 */
public class RoadCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void bits(String label, double actual, double expected) {
        boolean ok = Double.doubleToLongBits(actual) == Double.doubleToLongBits(expected);
        if (!ok) fails++;
        out.printf("%-96s %s  %s against %s%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-96s %s  %,.9f against %,.9f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static BuildingsTemplate template(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        if (t == null) throw new IllegalStateException("no template named " + name);
        return t;
    }

    /** The three roads by name: the paving's from and to, and the highway. */
    static final String GRAVEL = ConstructionControl.PAVE_FROM, PAVED = ConstructionControl.PAVE_TO,
            HIGHWAY = "Elevated Highway";

    /** The advice's measure for roads, which the sections here read the site, the units and the cards through. */
    static final BuildAdvice.Measure ROADS = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);

    /** The months audited, and the worst: a month passes the playtest's audit within a cent or 1e-7 of what moved. */
    static int monthsAudited = 0, monthsOff = 0;

    static void month(Game g) {
        quietly(() -> g.simulateMonths(1));
        MoneyAudit.Result r = g.getLastMoneyAudit();
        monthsAudited++;
        if (r != null && Math.abs(r.residual) > MoneyAudit.CENT && r.relative() > 1e-7) monthsOff++;
    }

    /**
     * The fixture: a town of 1,200 houses on four gravel roads, every sector
     * held, played three years - the road past NEEDS YOU's line, and four
     * gravel roads to pave (calibrated: scratch-n1/fix-1200.txt).
     */
    static Game town(Path root, String name) {
        return town(root, name, GRAVEL, 4);
    }

    /** ...or on `n` roads of another kind: three Paved Roads carry what the four gravel roads do, and leave nothing to pave. */
    static Game town(Path root, String name, String road, int n) {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 80_000_000L);
            for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
            for (String[] w : new String[][] {
                    { "Industrial Bakery", "4" }, { "Coal Power Plant", "1" }, { "Water Treatment Plant", "1" },
                    { "Commercial Bank", "1" }, { "Convenience Store", "10" }, { "House", "1200" },
                    { "Construction Depot", "4" }, { road, String.valueOf(n) } }) {
                g.buildStack(template(g, w[0]), Integer.parseInt(w[1]), true);
            }
            g.simulateMonths(36);
            g.setCashForTest(Founding.WEALTHY_CASH);
        });
        return g;
    }

    /** The same town with no ground free: every road's ground bought at the land office's price. Returns the listing as it stood, for scaling. */
    static double[][] noGroundFree(Game g) {
        LandManager land = g.getLandManager();
        LandMarket market = land.getMarket();
        double[][] base = market.getOffersState();
        int nextId = market.getNextOfferId();
        land.setOwnedSqFt(land.getAllocatedSqFt());
        market.restoreOffers(base, nextId);
        return base;
    }

    /** The land office's listing at f times its prices. */
    static void scale(Game g, double[][] base, double f) {
        LandMarket market = g.getLandManager().getMarket();
        market.restoreOffers(BuildAdviceCheck.scaledOffers(base, f), market.getNextOfferId());
    }

    static CityNeeds.Need roadNeed(Game g) {
        for (CityNeeds.Need n : CityNeeds.measure(g, CityNeeds.PLAIN)) if (n.kind() == CityNeeds.Kind.ROADS) return n;
        return null;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("roadcheck");

        life(root);
        advice(root);
        cards(root);
        pavePrice(root);
        played(root);
        refused(root);
        saveAndLoad(root);
        offered(root);
        player(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. A ROAD'S LIFE ============================ */

    static void life(Path root) {
        out.println("--- 1. a road's life, by the model's own rules: its quote, its ground, and its running for its life ---");
        Game g = town(root, "life");
        BuildingManager bm = g.getBuildingManager();
        EconomyManager econ = g.getEconomyManager();
        Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, ROADS);
        double free = g.getLandManager().getAvailableSqFt();
        bits("its life is the funding page's bond term: BUILD_BOND_YEARS x 12 months", BuildAdvice.LIFE_MONTHS,
                Game.BUILD_BOND_YEARS * 12);
        double real = g.getBusinessInvestment().realTestRate(g.getDebtManager().quoteRate(0, BuildAdvice.LIFE_MONTHS));
        bits("the rate is the debt market's for that term less the inflation expected (realTestRate())",
                BuildAdvice.lifeRate(g), real);
        double i = real / 12;
        double factor = i > 0 ? (1 - Math.pow(1 + i, -BuildAdvice.LIFE_MONTHS)) / i : BuildAdvice.LIFE_MONTHS;
        bits("...and the life factor the level annuity's over those months", BuildAdvice.lifeFactor(g), factor);
        assertTrue("fixture: a positive real rate, so the factor is under the months (" + String.format("%.2f", factor) + ")",
                real > 0 && factor < BuildAdvice.LIFE_MONTHS);
        boolean run = true, whole = true;
        int weighed = 0;
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (t.getCategory() != BuildingType.INFRASTRUCTURE) continue;
            double materials = Math.max(0, bm.getConstructionMaterialPrice());
            double repairs = econ.withBuildersTax(bm.nonMaterialCost(t) + t.getConstructionMaterials() * materials)
                    * ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12;
            double utilities = t.getElectricityConsumption() * econ.getPricePerWatt()
                    + t.getWaterConsumption() * econ.getPricePerWaterUnit();
            double riders = BuildAdvice.roads(g, BuildAdvice.plus(site, t, 1)).getTransitRiders()
                    - BuildAdvice.roads(g, site).getTransitRiders();
            double expect = repairs + utilities + BuildCard.runningCost(g, t) - riders * econ.getTaxPolicy().monthlyFare();
            run &= Double.doubleToLongBits(BuildAdvice.running(g, t, site)) == Double.doubleToLongBits(expect);
            double life = g.quoteBuild(t, 1).total + BuildAdvice.landValue(g, t.getLandSqFt(), free)
                    + BuildAdvice.running(g, t, site) * BuildAdvice.lifeFactor(g);
            whole &= Double.doubleToLongBits(BuildAdvice.lifetime(g, t, free, site)) == Double.doubleToLongBits(life);
            weighed++;
            out.printf("      %-17s quote %,.1f, ground %,.1f, running %,.3f a month: %,.1f over its life%n", t.getName(),
                    g.quoteBuild(t, 1).total, BuildAdvice.landValue(g, t.getLandSqFt(), free), BuildAdvice.running(g, t, site),
                    BuildAdvice.lifetime(g, t, free, site));
        }
        assertTrue("running() is the repairs (1% a year of its price, tax in), power and water at the utility's prices,"
                + " its posts and upkeep, less its riders' fares, to the bit (" + weighed + " roads and lines)", run && weighed == 6);
        assertTrue("lifetime() is its quote, its ground at landValue() and running() x the life factor, to the bit", whole);
        // The repairs are the month's own bill: one more gravel road standing adds exactly them.
        BuildingsTemplate gravel = template(g, GRAVEL);
        double price = Math.max(0, bm.getConstructionMaterialPrice());
        double before = econ.maintenanceBillFor(BuildingType.INFRASTRUCTURE, price);
        double repairs = BuildAdvice.running(g, gravel, site) - gravel.getElectricityConsumption() * econ.getPricePerWatt()
                - gravel.getWaterConsumption() * econ.getPricePerWaterUnit() - BuildCard.runningCost(g, gravel);
        quietly(() -> g.buildStack(gravel, 1, true));
        double after = econ.maintenanceBillFor(BuildingType.INFRASTRUCTURE, price);
        close("...and the repairs are what the month's maintenance bill charges the city for one more gravel road",
                after - before, repairs, 1e-9);
    }

    /* ============================ 2. THE ADVICE ============================ */

    /** The least over its life a trip of every road and line the advice weighs, recomputed: {its name, its figure}. */
    static String[] least(Game g, boolean roadsOnly) {
        Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, ROADS);
        double free = g.getLandManager().getAvailableSqFt();
        String best = null;
        double per = Double.POSITIVE_INFINITY;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getCategory() != BuildingType.INFRASTRUCTURE) continue;
            if (roadsOnly && t.getCapacity() <= 0) continue;
            double u = BuildAdvice.unit(g, ROADS, t);
            if (!(u > 0)) continue;
            double p = BuildAdvice.lifetime(g, t, free, site) / u;
            if (p < per) { per = p; best = t.getName(); }
        }
        return new String[] { best, String.valueOf(per) };
    }

    /**
     * The land price a square foot, all of a road's ground at it, at which two
     * roads cost the same a trip over their lives: (u_a k_b - u_b k_a) /
     * (u_b A_a - u_a A_b), k a road's life but its ground, A its ground.
     */
    static double crossover(Game g, BuildingsTemplate a, BuildingsTemplate b) {
        Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, ROADS);
        double f = BuildAdvice.lifeFactor(g);
        double ka = g.quoteBuild(a, 1).total + BuildAdvice.running(g, a, site) * f;
        double kb = g.quoteBuild(b, 1).total + BuildAdvice.running(g, b, site) * f;
        double ua = BuildAdvice.unit(g, ROADS, a), ub = BuildAdvice.unit(g, ROADS, b);
        return (ua * kb - ub * ka) / (ub * a.getLandSqFt() - ua * b.getLandSqFt());
    }

    static void advice(Path root) {
        out.println("\n--- 2. the advice takes the least over its life; gravel wins where ground is cheap ---");
        // On paved roads: no gravel road to pave, so the new roads alone are weighed (8 weighs the paving).
        Game g = town(root, "advice", PAVED, 3);
        CityNeeds.Need need = roadNeed(g);
        assertTrue("fixture: the road is past NEEDS YOU's line, and no gravel road to pave", need != null
                && need.level() > 0 && g.paveable() == 0);
        double[][] base = noGroundFree(g);
        assertTrue("fixture: no ground free, so every road's ground is bought at the land office's price",
                g.getLandManager().getAvailableSqFt() == 0);
        BuildingsTemplate gravel = template(g, GRAVEL), paved = template(g, PAVED), highway = template(g, HIGHWAY);
        scale(g, base, 1);
        double office = g.getLandManager().getOfficePricePerSqFt();
        double gp = crossover(g, gravel, paved), ph = crossover(g, paved, highway);
        out.printf("      the land office at %.6g a sq ft; a paved road beats a gravel road over its life past %.6g (x%.1f),"
                + " a highway a paved road past %.6g (x%.1f)%n", office, gp, gp / office, ph, ph / office);
        assertTrue("fixture: the crossovers are past today's price and the paved road's comes first", gp > office && ph > gp);
        double[] at = { 0, .9 * gp / office, 1.1 * gp / office, 1.1 * ph / office };
        String[] want = { GRAVEL, GRAVEL, PAVED, HIGHWAY };
        boolean agrees = true, roads = true;
        for (int k = 0; k < at.length; k++) {
            scale(g, base, at[k]);
            BuildAdvice.Suggestion s = BuildAdvice.suggestFor(g, need, ROADS, g.getCash(), g.getLandManager().getAvailableSqFt());
            String[] all = least(g, false), three = least(g, true);
            out.printf("      x%-7.2f the advice: %s %s (%,.4f a trip over its life); the least of the six %s, of the roads %s%n",
                    at[k], s == null ? "-" : s.count() + " x", s == null ? "nothing" : s.template().getName(),
                    s == null ? Double.NaN : s.pricePerUnit(), all[0], three[0]);
            agrees &= s != null && !s.paving() && s.template().getName().equals(all[0])
                    && Double.doubleToLongBits(s.pricePerUnit()) == Double.doubleToLongBits(Double.parseDouble(all[1]));
            roads &= want[k].equals(three[0]);
        }
        scale(g, base, 1);
        assertTrue("the advice's road is the candidate least over its life a trip, its figure to the bit, at every price", agrees);
        assertTrue("...the gravel road at nothing and under the crossover, the paved road past it, the highway past the next",
                roads);
    }

    /* ============================ 3. THE CARDS ============================ */

    static void cards(Path root) {
        out.println("\n--- 3. the road cards say the same: their life and their ground a trip off the road ---");
        Game g = town(root, "cards", PAVED, 3);
        double[][] base = noGroundFree(g);
        BuildingsTemplate gravel = template(g, GRAVEL), paved = template(g, PAVED), highway = template(g, HIGHWAY);
        scale(g, base, 1);
        double gp = crossover(g, gravel, paved), ph = crossover(g, paved, highway);
        double office = g.getLandManager().getOfficePricePerSqFt();
        boolean bars = true, tagged = true;
        int seen = 0;
        for (double f : new double[] { 0, .9 * gp / office, 1.1 * gp / office, 1.1 * ph / office }) {
            scale(g, base, f);
            Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, ROADS);
            double free = g.getLandManager().getAvailableSqFt();
            BuildCard.Group group = BuildCard.groups(g, BuildAdvice.ROADS, ROADS).get(0);
            String best = null;
            for (BuildCard.Figures c : group.cards()) {
                BuildingsTemplate t = c.template();
                double u = BuildAdvice.unit(g, ROADS, t);
                bars &= Double.doubleToLongBits(c.bar1()) == Double.doubleToLongBits(BuildAdvice.lifetime(g, t, free, site) / u)
                        && Double.doubleToLongBits(c.bar2()) == Double.doubleToLongBits(t.getLandSqFt() / u)
                        && c.bar2Kind() == BuildCard.Bar2.LAND && c.figure() == t.getCapacity();
                if (group.best1(c)) best = t.getName();
                seen++;
            }
            out.printf("      x%-7.2f the card tagged cheapest over its life: %s; the advice's first road: %s%n", f, best,
                    least(g, true)[0]);
            tagged &= best != null && best.equals(least(g, true)[0]);
        }
        scale(g, base, 1);
        assertTrue("bar 1 is the road's life over the trips it takes off, bar 2 its ground over them, its hero its capacity,"
                + " to the bit (" + seen + " cards)", bars && seen == 12);
        assertTrue("...and the card tagged cheapest is the advice's first road, at every price", tagged);
    }

    /* ============================ 4. THE PAVING'S PRICE ============================ */

    static void pavePrice(Path root) {
        out.println("\n--- 4. the paving's price: a paved road's, less the gravel's material, plus the take-up ---");
        Game g = town(root, "price");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate gravel = template(g, GRAVEL), paved = template(g, PAVED);
        boolean parts = true;
        for (int n : new int[] { 1, 3, 4 }) {
            Game.BuildQuote q = g.quotePave(n);
            double sticker = (bm.nonMaterialCost(paved) + ConstructionControl.DEMOLITION_SHARE * bm.nonMaterialCost(gravel)) * n;
            parts &= Double.doubleToLongBits(q.sticker) == Double.doubleToLongBits(sticker)
                    && q.materialsNeeded == (paved.getConstructionMaterials() - gravel.getConstructionMaterials()) * (double) n
                    && q.landNeeded == 0 && Double.doubleToLongBits(q.months) == Double.doubleToLongBits(g.quoteBuild(paved, n).months);
            out.printf("      %d: %,.1f all in - sticker %,.1f, %,.0f units of material, %.1f months%n", n, q.total, q.sticker,
                    q.materialsNeeded, q.months);
        }
        assertTrue("its work is a Paved Road's and DEMOLITION_SHARE of a gravel road's, its material a Paved Road's less a"
                + " gravel road's, no ground, a Paved Road's wait - to the bit, one road and many", parts);
        /*
         * ...PLUS ITS SURFACE'S BITUMEN (0.7.83, batch O6; spec-oil 2.5): the
         * material past a gravel road's, at five tonnes a unit, five per cent
         * binder [R18] - 64.25 t a road, a Paved Road's own, the bed having
         * none; an Elevated Highway 151.75 t. The builders buy it as the
         * order is placed, the refiners' tanks first and the world for the
         * rest, and bill it in the quote: the refiners' net of the credit on
         * it at their rate, the world's at its landed cost, their own tax
         * passed on. This town has no refinery, so it is all the world's.
         */
        BuildingsTemplate highway = template(g, HIGHWAY);
        bits("a Paved Road takes 64.25 t of bitumen: (450 - 193) units at five tonnes, BITUMEN_BINDER_SHARE of it",
                g.bitumenFor(paved), (paved.getConstructionMaterials() - gravel.getConstructionMaterials())
                        * Good.MATERIALS.tonnesPerUnit() * Game.BITUMEN_BINDER_SHARE);
        assertTrue("...64.25 t; an Elevated Highway 151.75 t; a Gravel Road, the bed, none; and a paving a Paved Road's",
                g.bitumenFor(paved) == 64.25 && g.bitumenFor(highway) == 151.75 && g.bitumenFor(gravel) == 0
                        && g.bitumenForPaving() == g.bitumenFor(paved) && g.bitumenFor(template(g, "House")) == 0);
        GoodsMarket bitumen = g.getMarkets().get(Good.BITUMEN);
        double gross = 1 / (1 - g.getEconomyManager().buildersSalesRate());
        double rM = g.getEconomyManager().getTaxPolicy().effectiveSalesRate(g.getSectors().materials());
        boolean billed = true;
        for (int n : new int[] { 1, 3, 4 }) {
            Game.BuildQuote q = g.quotePave(n);
            billed &= q.bitumenNeeded == g.bitumenForPaving() * n && q.bitumenFromRefiners == 0
                    && Double.doubleToLongBits(q.bitumenImportCost) == Double.doubleToLongBits(q.bitumenNeeded * bitumen.importPrice())
                    && Double.doubleToLongBits(q.bitumen) == Double.doubleToLongBits(q.bitumenImportCost * gross)
                    && Double.doubleToLongBits(q.total) == Double.doubleToLongBits(
                            q.sticker * gross + q.plantCost * (1 - rM) * gross + q.importCost * gross + q.bitumen);
        }
        assertTrue("the paving's quote carries its bitumen, the world's at the import price with the builders' tax passed on,"
                + " in its total - to the bit, one road and many", billed);
        Game.BuildQuote one = g.quotePave(1), road = g.quoteBuild(paved, 1), raised = g.quoteBuild(highway, 1);
        assertTrue("...and a new Paved Road's and Elevated Highway's quotes carry theirs, a Gravel Road's none",
                road.bitumenNeeded == 64.25 && raised.bitumenNeeded == 151.75 && g.quoteBuild(gravel, 1).bitumenNeeded == 0
                        && Double.doubleToLongBits(one.bitumen) == Double.doubleToLongBits(road.bitumen));
        // Jerus: "total cost is higher than just building paved". At one price for the material the work
        // decides it: a gravel road's and its paving's are a Paved Road's and more, by the gravel road's own
        // work and its take-up, and their material is a Paved Road's. (Each quote takes the yard's material
        // free, as every order's does, so on a day the yard holds a gravel road's and a paving's but not a
        // Paved Road's, the two quotes can add to less: below.)
        Game.BuildQuote pv = g.quotePave(1), gq = g.quoteBuild(gravel, 1), pq = g.quoteBuild(paved, 1);
        close("a gravel road's work and its paving's are a Paved Road's, and the gravel road's own and its take-up more",
                gq.sticker + pv.sticker - pq.sticker, (1 + ConstructionControl.DEMOLITION_SHARE) * bm.nonMaterialCost(gravel), 1e-12);
        bits("...and their material is a Paved Road's", gq.materialsNeeded + pv.materialsNeeded, pq.materialsNeeded);
        assertTrue("so at one price for the material, a gravel road and its paving cost more than a Paved Road built"
                + " outright (Jerus)", bm.nonMaterialCost(gravel) > 0);
        out.printf("      today, the yard holding %,d units: a gravel road %,.1f and its paving %,.1f, %,.1f, against a paved"
                + " road %,.1f (%+.1f%%)%n", bm.getConstructionMaterials(), gq.total, pv.total, gq.total + pv.total, pq.total,
                100 * (gq.total + pv.total - pq.total) / pq.total);
        assertTrue("...and paving one costs less than a new Paved Road: its bed is in the ground already", pv.total < pq.total);
        assertTrue("only a gravel road is paved, to a Paved Road; a Paved Road is not raised to a highway",
                ConstructionControl.paves(gravel) && !ConstructionControl.paves(paved)
                        && !ConstructionControl.paves(template(g, HIGHWAY)) && PAVED.equals(ConstructionControl.PAVE_TO));
    }

    /* ============================ 5. PLAYED ============================ */

    static void played(Path root) {
        out.println("\n--- 5. paving, one road and many, played: open while paved, the new road first, the ground freed ---");
        Game g = town(root, "played");
        BuildingManager bm = g.getBuildingManager();
        LandManager land = g.getLandManager();
        BuildingsTemplate gravel = template(g, GRAVEL), paved = template(g, PAVED);
        bits("fixture: the land ledger is the buildings' footprint", land.getAllocatedSqFt(), bm.getTotalLandFootprint());
        int g0 = bm.getQuantity(gravel.getId()), p0 = bm.getQuantity(paved.getId());
        // A new Paved Road first, then a paving of one and a paving of three behind it.
        quietly(() -> g.buildStack(paved, 1, false));
        double cash0 = g.getCash();
        Game.BuildQuote q1 = g.quotePave(1);
        double contract0 = bm.getStack(paved).getContractValue();
        final boolean[] ok = new boolean[2];
        quietly(() -> ok[0] = g.paveRoads(1));
        close("paving one: the treasury pays its quote", cash0 - g.getCash(), q1.total, 1e-12);
        close("...booked as the builders' contract on the Paved Road site", bm.getStack(paved).getContractValue() - contract0,
                q1.total, 1e-12);
        Game.BuildQuote q3 = g.quotePave(3);
        double cash1 = g.getCash();
        quietly(() -> ok[1] = g.paveRoads(3));
        close("paving three: the treasury pays its quote", cash1 - g.getCash(), q3.total, 1e-12);
        List<ConstructionControl.Paving> pv = bm.getControl().pavings();
        assertTrue("both placed: five Paved Roads on site, the first new, the pavings of one and three behind it",
                ok[0] && ok[1] && bm.getStack(paved).getUnderConstruction() == 5 && pv.size() == 2
                        && pv.get(0).roads == 1 && pv.get(0).ahead == 1 && pv.get(1).roads == 3 && pv.get(1).ahead == 2);
        assertTrue("...four gravel roads being paved, none left to pave, and the gravel roads all standing",
                g.pavingNow() == 4 && g.paveable() == 0 && bm.getQuantity(gravel.getId()) == g0);
        bits("...and the land ledger is still the footprint: the pavings took no ground", land.getAllocatedSqFt(),
                bm.getTotalLandFootprint());
        boolean capacity = true, ledger = true, order = true, freed = true;
        int months = 0, opened = 0;
        double alloc = land.getAllocatedSqFt();
        while (bm.getStack(paved).getUnderConstruction() > 0 && months < 600) {
            month(g);
            months++;
            int pNow = bm.getQuantity(paved.getId()) - p0, gNow = bm.getQuantity(gravel.getId());
            int retired = g0 - gNow;
            // The new road opens first; every one after it is a paving, and retires its gravel road as it opens.
            order &= retired == Math.max(0, pNow - 1) && g.pavingNow() == 4 - retired;
            capacity &= g.getInfrastructureManager().getBuiltCapacity()
                    == gravel.getCapacity() * (double) gNow + paved.getCapacity() * (double) bm.getQuantity(paved.getId());
            ledger &= Double.doubleToLongBits(land.getAllocatedSqFt()) == Double.doubleToLongBits(bm.getTotalLandFootprint());
            int newly = retired - opened;
            freed &= Math.abs((alloc - land.getAllocatedSqFt())
                    - (gravel.getLandSqFt() - paved.getLandSqFt()) * newly) <= 1e-6;
            alloc = land.getAllocatedSqFt();
            opened = retired;
        }
        out.printf("      %d months: %d gravel roads paved, %d paved roads standing, %d gravel%n", months, opened,
                bm.getQuantity(paved.getId()), bm.getQuantity(gravel.getId()));
        assertTrue("fixture: every Paved Road opened", bm.getStack(paved).getUnderConstruction() == 0 && opened == 4);
        assertTrue("the new Paved Road opened first; each after it retired its gravel road as it opened, and the"
                + " pavings are done", order && g.pavingNow() == 0 && bm.getControl().pavings().isEmpty());
        assertTrue("the network carried the roads standing every month: a gravel road its 900 trips until its paving opened",
                capacity);
        assertTrue("the land ledger was the footprint every month, to the bit", ledger);
        assertTrue("...and each paving freed the ground between the two roads as it opened (200,000 sq ft a road)", freed);
        bits("a month of the audit off by more than a cent and 1e-7 of what moved: none", monthsOff, 0);
    }

    /* ============================ 6. REFUSED ============================ */

    static void refused(Path root) {
        out.println("\n--- 6. what it refuses, and what it holds while it paves ---");
        Game g = town(root, "refused");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate gravel = template(g, GRAVEL), paved = template(g, PAVED);
        String key = ConstructionControl.keyOf(paved);
        double cash = g.getCash();
        final boolean[] r = new boolean[6];
        quietly(() -> r[0] = g.paveRoads(g.paveable() + 1));
        assertTrue("more gravel roads than there are to pave: refused, nothing paid, nothing on site",
                !r[0] && g.getCash() == cash && bm.getStack(paved) == null && g.pavingNow() == 0);
        quietly(() -> {
            g.buildStack(paved, 1, false);
            g.cancelSite(key, true);
        });
        double cash1 = g.getCash();
        quietly(() -> r[1] = g.paveRoads(1));
        assertTrue("the Paved Road site set to stop at the month's end: refused, nothing paid",
                !r[1] && g.getCash() == cash1 && g.pavingNow() == 0);
        quietly(() -> g.cancelSite(key, false));
        g.setCashForTest(g.quotePave(2).total / 2);
        double cash2 = g.getCash();
        int site2 = bm.getStack(paved).getUnderConstruction();
        quietly(() -> r[2] = g.paveRoads(2));
        assertTrue("short of the cash: refused, and nothing moved", !r[2] && g.getCash() == cash2
                && bm.getStack(paved).getUnderConstruction() == site2 && g.pavingNow() == 0);
        g.setCashForTest(Founding.WEALTHY_CASH);
        quietly(() -> r[3] = g.paveRoads(2));
        quietly(() -> r[4] = g.cancelSite(key, true));
        assertTrue("paving: the Paved Road site is not stopped while it paves", r[3] && !r[4]
                && !bm.getControl().isCancelling(key) && g.isPavingSite(key));
        int standing = bm.getQuantity(gravel.getId());
        assertTrue("...and a gravel road being paved is not demolished: demolishable is those standing less those paving",
                g.demolishable(gravel) == standing - 2);
        quietly(() -> r[5] = g.demolish(gravel, standing - 1));
        assertTrue("...so demolishing more than that is refused", !r[5]);
    }

    /* ============================ 7. SAVE AND LOAD ============================ */

    static void saveAndLoad(Path root) throws Exception {
        out.println("\n--- 7. a paving in progress saved and loaded, to the cent; an older save has none ---");
        Game g = town(root, "saved");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate gravel = template(g, GRAVEL), paved = template(g, PAVED);
        quietly(() -> {
            g.buildStack(paved, 1, false);
            g.paveRoads(3);
        });
        month(g);
        month(g);
        assertTrue("fixture: a paving part done, on site, its gravel roads standing",
                g.pavingNow() > 0 && bm.getStack(paved).getUnderConstruction() > 0);
        final boolean[] saved = { false };
        quietly(() -> saved[0] = g.saveGame(1, "paving").ok);
        assertTrue("saved", saved[0]);
        GameFiles files = new GameFiles(root.resolve("saved"), root.resolve("saved-no-legacy"));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(1));
        for (Sector s : back.getSectors().all()) back.getBusinessInvestment().holdSector(s.key());
        BuildingManager bb = back.getBuildingManager();
        Gson gson = new GsonBuilder().create();
        assertTrue("the pavings read back as they were written",
                gson.toJson(bb.getControl().toState()).equals(gson.toJson(bm.getControl().toState())));
        bits("the ground the buildings hold", bb.getTotalLandFootprint(), bm.getTotalLandFootprint());
        bits("...as the land office allocates it", back.getLandManager().getAllocatedSqFt(), g.getLandManager().getAllocatedSqFt());
        bits("the treasury", back.getCash(), g.getCash());
        BuildingsStacks s0 = bm.getStack(paved), s1 = bb.getStack(template(back, PAVED));
        assertTrue("the Paved Road site: its count, work, material owed and contract, to the bit",
                s1 != null && s1.getUnderConstruction() == s0.getUnderConstruction()
                        && Double.doubleToLongBits(s1.getConstructionProgress()) == Double.doubleToLongBits(s0.getConstructionProgress())
                        && Double.doubleToLongBits(s1.getMaterialsOwed()) == Double.doubleToLongBits(s0.getMaterialsOwed())
                        && Double.doubleToLongBits(s1.getContractValue()) == Double.doubleToLongBits(s0.getContractValue()));
        boolean alike = true;
        int months = 0;
        while ((g.pavingNow() > 0 || back.pavingNow() > 0) && months < 600) {
            month(g);
            month(back);
            months++;
            alike &= Double.doubleToLongBits(back.getCash()) == Double.doubleToLongBits(g.getCash())
                    && bb.getQuantity(gravel.getId()) == bm.getQuantity(gravel.getId())
                    && bb.getQuantity(paved.getId()) == bm.getQuantity(paved.getId())
                    && back.pavingNow() == g.pavingNow()
                    && Double.doubleToLongBits(back.getLandManager().getAllocatedSqFt())
                            == Double.doubleToLongBits(g.getLandManager().getAllocatedSqFt());
        }
        assertTrue("the two cities played the paving's " + months + " months alike: the treasury, the roads and the ledger to"
                + " the bit, every month", alike && months > 0 && g.pavingNow() == 0);

        Path file = files.saveFile(1);
        JsonObject json = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
        assertTrue("fixture: the save carries the pavings under the hand's key",
                json.getAsJsonObject("constructionControl").has("paving"));
        json.getAsJsonObject("constructionControl").remove("paving");
        Files.writeString(files.saveFile(2), new GsonBuilder().setPrettyPrinting().create().toJson(json), StandardCharsets.UTF_8);
        Game old = new Game(files);
        quietly(() -> old.loadGameSave(2));
        assertTrue("a save with no pavings (before 0.7.70) loads, with none", (old.getLoadFailure() == null
                || old.getLoadFailure().isEmpty()) && old.pavingNow() == 0 && old.getBuildingManager().getControl().pavings().isEmpty());
    }

    /* ============================ 8. OFFERED ============================ */

    static void offered(Path root) {
        out.println("\n--- 8. the advice offers the paving when it beats a new Paved Road, and not before ---");
        Game g = town(root, "offered");
        CityNeeds.Need need = roadNeed(g);
        double[][] base = noGroundFree(g);
        BuildingsTemplate gravel = template(g, GRAVEL), paved = template(g, PAVED);
        scale(g, base, 1);
        Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, ROADS);
        // The crossover the model's figures give, all ground at the office's price: the paving's life a trip,
        // its freed ground taken off, against a new Paved Road's.
        double f = BuildAdvice.lifeFactor(g);
        double ku = g.quotePave(1).total + (BuildAdvice.running(g, paved, site) - BuildAdvice.running(g, gravel, site)) * f;
        double kp = g.quoteBuild(paved, 1).total + BuildAdvice.running(g, paved, site) * f;
        double uu = BuildAdvice.pavingUnit(g, site), up = BuildAdvice.unit(g, ROADS, paved);
        double x = (ku * up - kp * uu) / (BuildAdvice.pavingFrees(g) * up + paved.getLandSqFt() * uu);
        double office = g.getLandManager().getOfficePricePerSqFt();
        out.printf("      paving beats a new Paved Road past %.6g a sq ft, x%.1f the land office's %.6g%n", x, x / office, office);
        assertTrue("fixture: the road past its line, four gravel roads to pave, and the crossover past today's price",
                need != null && need.level() > 0 && g.paveable() == 4 && x > office);
        scale(g, base, .9 * x / office);
        BuildAdvice.Suggestion under = BuildAdvice.suggestFor(g, need, ROADS, g.getCash(), 0);
        BuildCard.Paving cardUnder = BuildCard.paving(g, gravel);
        assertTrue("under the crossover: no paving suggested, and the Gravel Road card does not recommend it",
                under != null && !under.paving() && !BuildAdvice.pavingBeatsPaved(g, 0, site) && !cardUnder.beatsPaved());
        scale(g, base, 1.1 * x / office);
        BuildAdvice.Suggestion s = BuildAdvice.suggestFor(g, need, ROADS, g.getCash(), 0);
        BuildCard.Paving card = BuildCard.paving(g, gravel);
        assertTrue("past it: the suggestion is the paving, its gravel roads to Paved Roads, no more than there are to pave",
                s != null && s.paving() && s.template() == paved && s.count() >= 1 && s.count() <= g.paveable());
        if (s == null || !s.paving()) return;
        out.printf("      past it: pave %d, %,.1f all in, %,.4f a trip over its life against a new Paved Road's %,.4f%n",
                s.count(), s.price(), s.pricePerUnit(), card.pavedPerTrip());
        bits("...its price the paving's quote for its count", s.price(), g.quotePave(s.count()).total);
        bits("...the ground it frees, taken off the land the cards after it see", s.landSqFt(),
                -BuildAdvice.pavingFrees(g) * s.count());
        Map<BuildingsTemplate, Integer> done = BuildAdvice.plus(site, BuildAdvice.added(g, s));
        bits("...and the road it leaves, with its gravel roads gone", s.after(), BuildAdvice.figure(g, ROADS, done));
        assertTrue("...which keeps the road ahead", s.closes() && BuildAdvice.ahead(g, ROADS, done, s.ahead()));
        assertTrue("the Gravel Road card recommends it too: its figure the suggestion's, under a new Paved Road's",
                card.beatsPaved() && Double.doubleToLongBits(card.perTrip()) == Double.doubleToLongBits(s.pricePerUnit())
                        && card.perTrip() < card.pavedPerTrip());
        List<BuildAdvice.Suggestion> one = new ArrayList<>(List.of(s));
        assertTrue("\"Build all three\" leaves the paving out: it is not a build order",
                BuildAdvice.run(one).isEmpty() && BuildAdvice.builds(one).isEmpty());
        scale(g, base, 1);
    }

    /* ============================ 9. THE TEST PLAYER ============================ */

    static void player(Path root) {
        out.println("\n--- 9. the test player asks the advice which road, and paves where it beats a new Paved Road ---");
        // New roads, on the paved town: gravel at nothing, the paved road past the crossover.
        Game a = town(root, "player-new", PAVED, 3);
        double[][] baseA = noGroundFree(a);
        scale(a, baseA, 1);
        double officeA = a.getLandManager().getOfficePricePerSqFt();
        double gp = crossover(a, template(a, GRAVEL), template(a, PAVED));
        boolean first = true;
        first &= firstRoad(a, baseA, 0, "gravel road");
        first &= firstRoad(a, baseA, 1.1 * gp / officeA, "roads");
        // Paving, on the gravel town: past where it beats a new Paved Road and a new Gravel Road.
        Game g = town(root, "player-pave");
        double[][] base = noGroundFree(g);
        BuildingsTemplate gravel = template(g, GRAVEL), paved = template(g, PAVED);
        scale(g, base, 1);
        double office = g.getLandManager().getOfficePricePerSqFt();
        Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, ROADS);
        double f = BuildAdvice.lifeFactor(g);
        double ku = g.quotePave(1).total + (BuildAdvice.running(g, paved, site) - BuildAdvice.running(g, gravel, site)) * f;
        double kp = g.quoteBuild(paved, 1).total + BuildAdvice.running(g, paved, site) * f;
        double kg = g.quoteBuild(gravel, 1).total + BuildAdvice.running(g, gravel, site) * f;
        double uu = BuildAdvice.pavingUnit(g, site), up = BuildAdvice.unit(g, ROADS, paved), ug = BuildAdvice.unit(g, ROADS, gravel);
        double frees = BuildAdvice.pavingFrees(g);
        double overPaved = (ku * up - kp * uu) / (frees * up + paved.getLandSqFt() * uu);
        double overGravel = (ku * ug - kg * uu) / (frees * ug + gravel.getLandSqFt() * uu);
        out.printf("      paving beats a new paved road past %.6g a sq ft and a new gravel road past %.6g (today %.6g)%n",
                overPaved, overGravel, office);
        first &= firstRoad(g, base, 1.5 * Math.max(overPaved, overGravel) / office, "paved gravel");
        assertTrue("its first road move is the advice's: the gravel road at nothing, the paved road past the crossover,"
                + " paving past paving's", first);
        // ...and the move paves: as many as a road order at its gap, no more than there are.
        scale(g, base, 1.5 * Math.max(overPaved, overGravel) / office);
        List<LongPlaytest.Move> moves = new ArrayList<>();
        LongPlaytest.addRoadThrottle(moves, g, Math.max(1, g.getEconomyManager().getMonthGdp()) * .1);
        int before = LongPlaytest.roadsPaved, standing = g.paveable();
        final boolean[] did = { false };
        quietly(() -> did[0] = moves.get(0).act().getAsBoolean());
        int count = LongPlaytest.roadsPaved - before;
        out.printf("      the move paved %d of the %d gravel roads%n", count, standing);
        assertTrue("...and its move paves them: on the Paved Road site, out of the cash, counted in the report",
                did[0] && count >= 1 && count <= standing && g.pavingNow() == count);
        scale(g, base, 1);
    }

    /**
     * The test player's first road move at the land office's prices x f, against the advice's ranking recomputed:
     * the least of the three roads over its life a trip, and the paving where it beats a new Paved Road and is less.
     */
    static boolean firstRoad(Game g, double[][] base, double f, String want) {
        scale(g, base, f);
        Map<BuildingsTemplate, Integer> now = BuildAdvice.onSite(g, ROADS);
        String[] three = least(g, true);
        String advised = three[0].equals(GRAVEL) ? "gravel road" : three[0].equals(PAVED) ? "roads" : "highway";
        if (BuildAdvice.pavingBeatsPaved(g, 0, now)
                && BuildAdvice.pavingLifetime(g, 0, now) / BuildAdvice.pavingUnit(g, now) < Double.parseDouble(three[1])) {
            advised = "paved gravel";
        }
        List<LongPlaytest.Move> moves = new ArrayList<>();
        LongPlaytest.addRoadThrottle(moves, g, Math.max(1, g.getEconomyManager().getMonthGdp()) * .1);
        String label = moves.isEmpty() ? "-" : moves.get(0).label();
        out.printf("      x%-8.2f the player's first road move: %s; the advice's: %s%n", f, label, advised);
        scale(g, base, 1);
        return advised.equals(label) && want.equals(label);
    }
}
