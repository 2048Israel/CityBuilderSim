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
 * The 0.7.70 playtest never paved (a new road was cheaper there at every
 * look); the 0.7.99 one paved 87 gravel roads in 14 orders over its four
 * thousand months, and the 0.7.106 one, its player ordering roads by need
 * since 0.7.103, 10 in 2 (its report counts them). This is where the paving is
 * played to order, each fixture causing it.
 *
 * What this has to prove:
 *   1. A ROAD'S LIFE, BY THE MODEL'S OWN RULES: every road and line weighed
 *      for the road is its quote, its ground at landValue(), and running()
 *      for LIFE_MONTHS at the real rate - each part recomputed here, to the
 *      bit; the repairs in running() are what the month's maintenance bill
 *      charges the city for one more such road - since 0.7.103 (batch P4,
 *      Jerus's A11) each road kind at its own share a year of its price
 *      (BuildingsTemplate.repairsPerYear(): gravel 3%, paved 2%, the
 *      highway 1.25%; a line the house's 1%), so gravel's share of its
 *      price is the dearest and the highway's the least, and gravel's lead
 *      a trip it carries over paved shrinks from nearly half to a seventh.
 *  1b. (FR7 2.2, Jerus's star 5, 2026-10-18) A GRAVEL ROAD'S REPAIRS RISE WITH
 *      THE TRAFFIC ON ITS STREET: its flat share up to 200 vehicles a day,
 *      then that share times (vehicles / 200) squared - struck from the dials,
 *      the paved road's and the highway's flat, the month's bill the same
 *      on a quiet street and up by the wear on a busy one.
 *   2. WITHIN THE STREETS, THE KIND FOLLOWS THE LIFE COST AND THE LOAD (FR7,
 *      Jerus's star 4; this section's one-pool ladder - gravel, then paved,
 *      then a highway by land price - is retired by his decision): on a
 *      quiet street the gravel road is the least over its life where ground
 *      is cheap and the paved road where it is dear; on a busy one the paved
 *      road is the least on the same ground and is the advice's card, every
 *      candidate's figure recomputed (0.7.101, Jerus's A4 and A19).
 *   3. THE ROAD CARDS SAY THE SAME: bar 1 is the road's life a trip off the
 *      road and bar 2 its ground a trip, to the bit, and the card tagged
 *      cheapest is the street the advice ranks first (the highway has a
 *      group of its own since FR7).
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
 *   8. THE ADVICE OFFERS THE PAVING WHERE THE STREETS ARE BUSY, and not where
 *      they are quiet (FR7 2.2; it was where it beat a new Paved Road past a
 *      crossover of the land price): on a busy street the suggestion is the
 *      paving - its quote, its gravel roads gone, the ground it frees, its
 *      figure the order's over its life (0.7.101) - and the Gravel Road
 *      card's paving says so, a trip under a new Paved Road's; "Build all
 *      three" leaves it out; on a quiet one nothing is offered.
 *   9. THE TEST PLAYER ASKS THE ADVICE: its first road move is the road the
 *      advice ranks first, and the paving where it beats a new Paved Road
 *      and the roads - a move that paves; and since 0.7.103 how many: the
 *      move paves the advice's card's count, a paved town with its ground
 *      free orders the advice's road and count, and a town whose roads are
 *      well ahead makes no road move.
 *  10. (FR7 2.1) A CITY WITH NO HIGHWAY CARRIES ITS THROUGH TRIPS ON THE
 *      STREETS, AT A STRAIN: a fixture whose through trips alone put the
 *      streets past their free-flow line, and every figure the old single
 *      pool's to the bit with no highway standing; the gates' load goes
 *      through whole; with a highway its capacity at free flow carries the
 *      through trips and the streets the rest, at their own curve.
 *  11. A LARGE CITY'S NEEDS YOU LISTS BOTH THE STREETS AND THE HIGHWAYS: past
 *      the through-need threshold (CityNeeds.HIGHWAY_MIN_FILL of a highway)
 *      both rows are listed, the advice suggests a street road and an
 *      Elevated Highway, and the test player orders both; below it, only the
 *      streets are listed.
 *  12. A HIGHWAY GIVES NO ACCESS: adding one leaves getStreetCapacity() as it
 *      was, in the advice's what-if and in a played city, and the streets'
 *      ranking never names it.
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

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-96s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
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
        Path root = TempDirs.create("roadcheck");

        life(root);
        gravelWear(root);
        advice(root);
        cards(root);
        pavePrice(root);
        played(root);
        refused(root);
        saveAndLoad(root);
        offered(root);
        player(root);
        throughStrain();
        bothNeeds(root);
        noAccess(root);

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
            // ...the roads and the lines: not ET6's long bridges, which are Infrastructure's too but carry no trips (their own repairs are asserted below).
            if (t.getCategory() != BuildingType.INFRASTRUCTURE || t.isBridge()) continue;
            double materials = Math.max(0, bm.getConstructionMaterialPrice());
            // FR7 2.2: a Gravel Road is repaired at the traffic on the streets the city would have with it (BuildingsTemplate
            // .gravelRepairsAt()); every other road and line at its flat share.
            double share = ConstructionControl.paves(t)
                    ? t.gravelRepairsAt(BuildAdvice.roads(g, BuildAdvice.plus(site, t, 1)).getUtilisation())
                    : t.repairsPerYear();
            double repairs = econ.withBuildersTax(bm.nonMaterialCost(t) + t.getConstructionMaterials() * materials)
                    * share / 12;
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
        assertTrue("running() is the repairs (its own share a year of its price since 0.7.103, tax in), power and water at"
                + " the utility's prices, its posts and upkeep, less its riders' fares, to the bit (" + weighed
                + " roads and lines)", run && weighed == 6);
        // EACH ROAD KIND ITS OWN REPAIRS (0.7.103, batch P4; Jerus's A11), buildings.json's "repairs".
        BuildingsTemplate gravelT = template(g, GRAVEL), pavedT = template(g, "Paved Road"),
                highwayT = template(g, "Elevated Highway");
        boolean lines = true;
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (t.getCategory() == BuildingType.INFRASTRUCTURE && t != gravelT && t != pavedT && t != highwayT && !t.isBridge()) {
                lines &= !t.hasOwnRepairs() && t.repairsPerYear() == ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR;
            }
        }
        // ...and ET6's three long bridges have the Elevated Highway's rate, on their two landings.
        boolean bridgeRepairs = true;
        int bridgeCount = 0;
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (!t.isBridge()) continue;
            bridgeCount++;
            bridgeRepairs &= t.hasOwnRepairs() && t.repairsPerYear() == highwayT.repairsPerYear();
        }
        assertTrue("the three long bridges (ET6) carry the Elevated Highway's own repairs a year, 1.25%, on their two landings", bridgeCount == 3 && bridgeRepairs);
        assertTrue("each road kind's repairs are its own share a year - gravel 3%, paved 2%, the highway 1.25% - and every"
                + " line's the house's 1% (0.7.103)", gravelT.repairsPerYear() == .03 && pavedT.repairsPerYear() == .02
                && highwayT.repairsPerYear() == .0125 && lines);
        double[] perTrip = new double[3];
        BuildingsTemplate[] three = { gravelT, pavedT, highwayT };
        for (int k = 0; k < 3; k++) {
            double materials = Math.max(0, bm.getConstructionMaterialPrice());
            perTrip[k] = econ.withBuildersTax(bm.nonMaterialCost(three[k]) + three[k].getConstructionMaterials() * materials)
                    * three[k].repairsPerYear() / 12 / three[k].getCapacity();
        }
        // ...at the house's 1% a gravel road's repairs a trip were its price a trip's share of a paved road's; now its
        // share's ratio over that: gravel's lead a trip narrows from nearly a half to a seventh, the highway under paved.
        double atOnePercent = (perTrip[0] / gravelT.repairsPerYear()) / (perTrip[1] / pavedT.repairsPerYear());
        report("...gravel's share of its price the dearest and the highway's the least; a trip it carries, gravel's repairs"
                        + " come to its price's ratio times the shares' (3 to 2) of a paved road's, and the highway's under paved's",
                gravelT.repairsPerYear() > pavedT.repairsPerYear() && pavedT.repairsPerYear() > highwayT.repairsPerYear()
                        && Math.abs(perTrip[0] / perTrip[1] - atOnePercent * 1.5) <= 1e-12 && perTrip[2] < perTrip[1],
                String.format("repairs a month a trip: gravel %.5f, paved %.5f, highway %.5f; gravel %.0f%% of paved's, %.0f%%"
                        + " at the house's 1%%", perTrip[0], perTrip[1], perTrip[2], 100 * perTrip[0] / perTrip[1], 100 * atOnePercent));
        assertTrue("lifetime() is its quote, its ground at landValue() and running() x the life factor, to the bit", whole);
        // The repairs are the month's own bill: one more gravel road standing adds exactly them.
        BuildingsTemplate gravel = template(g, GRAVEL);
        double price = Math.max(0, bm.getConstructionMaterialPrice());
        // ...at the street utilisation the month is charged at (FR7 2.2): the one running() reads for the road added.
        double withRoad = BuildAdvice.roads(g, BuildAdvice.plus(site, gravel, 1)).getUtilisation();
        econ.setStreetUtilisation(() -> withRoad);
        double before = econ.maintenanceBillFor(BuildingType.INFRASTRUCTURE, price);
        double repairs = BuildAdvice.running(g, gravel, site) - gravel.getElectricityConsumption() * econ.getPricePerWatt()
                - gravel.getWaterConsumption() * econ.getPricePerWaterUnit() - BuildCard.runningCost(g, gravel);
        quietly(() -> g.buildStack(gravel, 1, true));
        double after = econ.maintenanceBillFor(BuildingType.INFRASTRUCTURE, price);
        close("...and the repairs are what the month's maintenance bill charges the city for one more gravel road, at its"
                + " own share", after - before, repairs, 1e-9);
    }

    /* ======================= 1b. GRAVEL'S REPAIRS RISE WITH ITS TRAFFIC ======================= */

    /**
     * FR7 2.2 (Jerus's star 5, 2026-10-18): a Gravel Road's repairs rise with
     * the load on its street. Every figure here is struck from the dials
     * (InfrastructureManager.STREET_VEHICLES_A_DAY_AT_CAPACITY and
     * GRAVEL_QUIET_VEHICLES_A_DAY), and the bill is the month's own.
     */
    static void gravelWear(Path root) {
        out.println("\n--- 1b. a gravel road's repairs rise with the traffic on its street (200 vehicles a day, Minnesota) ---");
        Game g = town(root, "wear");
        BuildingManager bm = g.getBuildingManager();
        EconomyManager econ = g.getEconomyManager();
        BuildingsTemplate gravelT = template(g, GRAVEL), pavedT = template(g, PAVED), highwayT = template(g, HIGHWAY);
        double quiet = InfrastructureManager.GRAVEL_QUIET_UTILISATION;
        bits("the quiet line is the quiet load over a street's whole capacity: 200 / 450 vehicles a day",
                quiet, InfrastructureManager.GRAVEL_QUIET_VEHICLES_A_DAY / InfrastructureManager.STREET_VEHICLES_A_DAY_AT_CAPACITY);
        bits("...a street at its whole capacity carries the dial's vehicles a day",
                InfrastructureManager.vehiclesADay(1), InfrastructureManager.STREET_VEHICLES_A_DAY_AT_CAPACITY);
        bits("at the quiet line and under it a gravel road is repaired at its flat share (repairsPerYear()), exactly",
                gravelT.gravelRepairsAt(quiet), gravelT.repairsPerYear());
        bits("...and on an empty street too", gravelT.gravelRepairsAt(0), gravelT.repairsPerYear());
        double busy = 3 * quiet;
        close("past it the share is the flat share times (vehicles a day / 200) squared: three times the load, nine times the repairs",
                gravelT.gravelRepairsAt(busy), gravelT.repairsPerYear() * 9, 1e-12);
        assertTrue("...rising with the load, and only the Gravel Road's: the paved road's and the highway's stay flat",
                gravelT.gravelRepairsAt(busy) > gravelT.gravelRepairsAt(2 * quiet) && gravelT.gravelRepairsAt(2 * quiet) > gravelT.repairsPerYear()
                        && pavedT.repairsPerYearAt(busy) == pavedT.repairsPerYear()
                        && highwayT.repairsPerYearAt(busy) == highwayT.repairsPerYear()
                        && gravelT.repairsPerYearAt(busy) == gravelT.gravelRepairsAt(busy));
        // The month's bill: quiet streets charge what they always did, busy ones the gravel roads' wear on top.
        double price = Math.max(0, bm.getConstructionMaterialPrice());
        econ.setStreetUtilisation(() -> 0);
        double flat = econ.maintenanceBillFor(BuildingType.INFRASTRUCTURE, price);
        econ.setStreetUtilisation(() -> quiet);
        bits("the bill on a quiet street is the bill with no utilisation told: to the bit, as it was",
                econ.maintenanceBillFor(BuildingType.INFRASTRUCTURE, price), flat);
        econ.setStreetUtilisation(() -> busy);
        double worn = econ.maintenanceBillFor(BuildingType.INFRASTRUCTURE, price);
        int roads = bm.getQuantity(gravelT.getId());
        double perRoad = econ.withBuildersTax(bm.nonMaterialCost(gravelT) + gravelT.getConstructionMaterials() * price)
                * gravelT.repairsPerYear() / 12;
        close("...and at three times the quiet load each of the " + roads + " gravel roads costs nine times its flat repairs: the bill"
                + " is up by eight of them, no other building's moved", worn - flat, roads * perRoad * 8, 1e-9);
        assertTrue("fixture: the town has gravel roads standing to wear", roads == 4);
    }

    /* ============================ 2. THE ADVICE ============================ */

    /**
     * Whether the advice's road card is the least of its own candidates
     * (0.7.101): the ranking walked as automatic building walks it - each
     * card in turn left out (BuildAdvice.suggestFor()'s skip) - every card's
     * figure its order over its life for the trips it will take off
     * (BuildAdviceCheck.lifeFigure(), recomputed), and none of the same rank -
     * keeping the road ahead, fitting the land left - under the first.
     */
    static boolean leastOrder(Game g, CityNeeds.Need need, BuildAdvice.Suggestion first) {
        double free = g.getLandManager().getAvailableSqFt();
        java.util.Set<BuildingsTemplate> skip = new java.util.HashSet<>();
        boolean ok = Double.doubleToLongBits(BuildAdviceCheck.lifeFigure(g, first, free)) == Double.doubleToLongBits(first.pricePerUnit());
        skip.add(first.template());
        for (int guard = 0; guard < 8; guard++) {
            BuildAdvice.Suggestion c = BuildAdvice.suggestFor(g, need, ROADS, g.getCash(), free, BuildAdvice.SLACK, skip, true);
            if (c == null) break;
            ok &= Double.doubleToLongBits(BuildAdviceCheck.lifeFigure(g, c, free)) == Double.doubleToLongBits(c.pricePerUnit());
            boolean sameRank = c.closes() == first.closes() && (c.landShort() > 0) == (first.landShort() > 0);
            if (sameRank && c.pricePerUnit() < first.pricePerUnit()) ok = false;
            skip.add(c.template());
        }
        return ok;
    }

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

    /**
     * FR7 (Jerus's stars 4 and 5, 2026-10-18) RETIRED THIS SECTION'S LADDER, by his decision and not to let a change through:
     * the road was one pool with one winner, and the advice named gravel below the gravel-to-paved crossover of the land price,
     * paved between, and a highway only past the paved-to-highway one. A highway is not a street now (it gives no access: its
     * need is the through trips', section 11), and gravel's repairs rise with the traffic on its street (1b), so the streets'
     * kind follows the life cost AND the load: gravel on a quiet street where ground is cheap, paved on a busy one on the same
     * ground. The fixture causes each side of the 200-vehicle line from the dials: ten Paved Roads carry the town's trips at
     * 154 vehicles a day (under GRAVEL_QUIET_VEHICLES_A_DAY), three at 460 (over it).
     */
    static void advice(Path root) {
        out.println("\n--- 2. within the streets, the kind follows the life cost and the load: gravel where quiet, paved where busy ---");
        Game quiet = town(root, "advice-quiet", PAVED, 10);
        Game busy = town(root, "advice-busy", PAVED, 3);
        CityNeeds.Need need = roadNeed(busy);
        assertTrue("fixture: the busy town's streets are past NEEDS YOU's line, and no gravel road to pave", need != null
                && need.level() > 0 && busy.paveable() == 0 && roadNeed(quiet).level() == 0);
        double[][] quietBase = noGroundFree(quiet), busyBase = noGroundFree(busy);
        assertTrue("fixture: no ground free, so every road's ground is bought at the land office's price",
                quiet.getLandManager().getAvailableSqFt() == 0 && busy.getLandManager().getAvailableSqFt() == 0);
        // The load each town's streets would carry with one more gravel road on them, from the dials.
        BuildingsTemplate gravelQ = template(quiet, GRAVEL), gravelB = template(busy, GRAVEL);
        double uQuiet = BuildAdvice.roads(quiet, BuildAdvice.plus(BuildAdvice.onSite(quiet, ROADS), gravelQ, 1)).getUtilisation();
        double uBusy = BuildAdvice.roads(busy, BuildAdvice.plus(BuildAdvice.onSite(busy, ROADS), gravelB, 1)).getUtilisation();
        out.printf("      the quiet town's street: %.0f vehicles a day with a gravel road more; the busy town's: %.0f; the line: %.0f%n",
                InfrastructureManager.vehiclesADay(uQuiet), InfrastructureManager.vehiclesADay(uBusy),
                InfrastructureManager.GRAVEL_QUIET_VEHICLES_A_DAY);
        assertTrue("fixture: the two towns' streets fall either side of the quiet line, GRAVEL_QUIET_VEHICLES_A_DAY of"
                + " STREET_VEHICLES_A_DAY_AT_CAPACITY", uQuiet < InfrastructureManager.GRAVEL_QUIET_UTILISATION
                && uBusy > InfrastructureManager.GRAVEL_QUIET_UTILISATION);

        // 1. QUIET: ground at nothing, the street carries under the line, the gravel road is the least over its life.
        scale(quiet, quietBase, 1);
        double office = quiet.getLandManager().getOfficePricePerSqFt();
        scale(quiet, quietBase, 0);
        String[] atNothing = least(quiet, true);
        // ...and past the gravel-to-paved crossover of the land price the paved road still wins on a quiet street: ground counts.
        double gp = crossover(quiet, gravelQ, template(quiet, PAVED));
        scale(quiet, quietBase, 1.1 * gp / office);
        String[] dear = least(quiet, true);
        scale(quiet, quietBase, 1);
        out.printf("      quiet street, ground at nothing: the least over its life is the %s; with ground dear (past the %.6g a sq ft"
                + " crossover): the %s%n", atNothing[0], gp, dear[0]);
        assertTrue("on a quiet street (under 200 vehicles a day) the gravel road is the least of the streets' roads over its life"
                + " where ground is cheap - and the paved road past the crossover of the land price", GRAVEL.equals(atNothing[0])
                && PAVED.equals(dear[0]) && gp > 0);

        // 2. BUSY: the same ground at nothing, the street carries over the line, the paved road is the least, and the advice's card.
        scale(busy, busyBase, 0);
        String[] busyLeast = least(busy, true);
        BuildAdvice.Suggestion s = BuildAdvice.suggestFor(busy, need, ROADS, busy.getCash(), busy.getLandManager().getAvailableSqFt());
        out.printf("      busy street, the same ground: the least over its life is the %s; the advice: %s%n", busyLeast[0],
                s == null ? "nothing" : s.count() + " x " + s.template().getName() + " (" + String.format("%,.4f", s.pricePerUnit())
                        + " a trip over its life)");
        // The advice's figure is the order's over its life for the trips it will take off, each candidate recomputed.
        boolean agrees = s != null && !s.paving() && leastOrder(busy, need, s);
        scale(busy, busyBase, 1);
        assertTrue("on a busy street (over 200 vehicles a day) the paved road is the least over its life on the same ground, and"
                + " it is the advice's card, every candidate's figure recomputed here to the bit", PAVED.equals(busyLeast[0])
                && agrees && s.template() == template(busy, PAVED));
    }

    /* ============================ 3. THE CARDS ============================ */

    static void cards(Path root) {
        out.println("\n--- 3. the road cards say the same: their life and their ground a trip off the road ---");
        Game g = town(root, "cards", PAVED, 3);
        double[][] base = noGroundFree(g);
        scale(g, base, 1);
        boolean bars = true, tagged = true;
        int seen = 0;
        // FR7: the streets' cards are the gravel and the paved road (the highway is the through need's, its own group:
        // section 11), so four land prices - nothing, today's, ten and a hundred times it - make eight cards, not twelve.
        // The prices were the crossovers of the one-pool ladder this section's second fixture retired.
        for (double f : new double[] { 0, 1, 10, 100 }) {
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
                + " to the bit (" + seen + " cards)", bars && seen == 8);
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

    /** Every road and line in the Infrastructure category (Roads & transit until FR4): what the advice's paving-only ranking leaves out, as automatic building walks past roads the ground cannot hold. */
    static java.util.Set<BuildingsTemplate> everyRoadAndLine(Game g) {
        java.util.Set<BuildingsTemplate> all = new java.util.HashSet<>();
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) if (t.getCategory() == BuildingType.INFRASTRUCTURE) all.add(t);
        return all;
    }

    /**
     * FR7 2.2 (Jerus's star 5, 2026-10-18) retired the premise of the old section 8: that paving beats a new Paved Road only
     * past a CROSSOVER OF THE LAND PRICE above today's. Gravel's repairs rise with the traffic on its street now, so the
     * paving is offered by the load, at the same ground: a busy gravel network is paved and a quiet one stays gravel. The
     * fixtures are the same town on four gravel roads (460 vehicles a day on them) and on fourteen (147), each at the land
     * office's price; the suggestion is read with the new roads left out of the ranking, as automatic building walks past the
     * roads the ground cannot hold, so the paving's own card is what is asserted (BuildAdvice.weighPaving()'s).
     */
    static void offered(Path root) {
        out.println("\n--- 8. the advice offers the paving where the streets are busy, and not where they are quiet ---");
        Game g = town(root, "offered");
        Game calm = town(root, "offered-quiet", GRAVEL, 14);
        CityNeeds.Need need = roadNeed(g);
        double[][] base = noGroundFree(g), calmBase = noGroundFree(calm);
        BuildingsTemplate gravel = template(g, GRAVEL), paved = template(g, PAVED);
        scale(g, base, 1);
        scale(calm, calmBase, 1);
        Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, ROADS), calmSite = BuildAdvice.onSite(calm, ROADS);
        double line = InfrastructureManager.GRAVEL_QUIET_UTILISATION;
        out.printf("      the busy town: %.0f vehicles a day on %d gravel roads to pave; the quiet one: %.0f on %d; the line: %.0f%n",
                InfrastructureManager.vehiclesADay(g.getInfrastructureManager().getUtilisation()), g.paveable(),
                InfrastructureManager.vehiclesADay(calm.getInfrastructureManager().getUtilisation()), calm.paveable(),
                InfrastructureManager.GRAVEL_QUIET_VEHICLES_A_DAY);
        assertTrue("fixture: the busy town's streets are past NEEDS YOU's line and over the quiet line with four gravel roads to"
                + " pave; the quiet town's under it, with fourteen", need != null && need.level() > 0 && g.paveable() == 4
                && g.getInfrastructureManager().getUtilisation() > line
                && calm.paveable() == 14 && calm.getInfrastructureManager().getUtilisation() < line);
        java.util.Set<BuildingsTemplate> noNew = everyRoadAndLine(g);
        BuildAdvice.Suggestion offer = BuildAdvice.suggestFor(g, need, ROADS, g.getCash(), 0, BuildAdvice.SLACK, noNew, false);
        BuildAdvice.Suggestion calmOffer = BuildAdvice.suggestFor(calm, roadNeed(calm), ROADS, calm.getCash(), 0, BuildAdvice.SLACK,
                everyRoadAndLine(calm), false);
        BuildCard.Paving calmCard = BuildCard.paving(calm, template(calm, GRAVEL));
        assertTrue("on a quiet street: no paving beats a new Paved Road, the Gravel Road card does not recommend it, and nothing"
                + " is offered", !BuildAdvice.pavingBeatsPaved(calm, 0, calmSite) && !calmCard.beatsPaved() && calmOffer == null);
        BuildCard.Paving card = BuildCard.paving(g, gravel);
        assertTrue("on a busy street, at the same ground: the paving beats a new Paved Road over its life a trip, the suggestion"
                + " is the paving, its gravel roads to Paved Roads, no more than there are to pave",
                BuildAdvice.pavingBeatsPaved(g, 0, site) && offer != null && offer.paving() && offer.template() == paved
                        && offer.count() >= 1 && offer.count() <= g.paveable());
        if (offer == null || !offer.paving()) return;
        out.printf("      busy: pave %d, %,.1f all in, %,.4f a trip over its life against a new Paved Road's %,.4f%n",
                offer.count(), offer.price(), offer.pricePerUnit(), card.pavedPerTrip());
        bits("...its price the paving's quote for its count", offer.price(), g.quotePave(offer.count()).total);
        bits("...the ground it frees, taken off the land the cards after it see", offer.landSqFt(),
                -BuildAdvice.pavingFrees(g) * offer.count());
        Map<BuildingsTemplate, Integer> done = BuildAdvice.plus(site, BuildAdvice.added(g, offer));
        bits("...and the road it leaves, with its gravel roads gone", offer.after(), BuildAdvice.figure(g, ROADS, done));
        assertTrue("...which takes load off the streets: the figure it leaves is under the figure before", offer.after() < offer.before());
        // The card's figure is a paving's life a trip (pavingLifetime() over pavingUnit()); since 0.7.101 the suggestion's is
        // the pavings' order over its life for the trips it will take off (BuildAdvice, A BUILDING OVER ITS LIFE).
        assertTrue("the Gravel Road card recommends it too: a paving's life a trip under a new Paved Road's (until 0.7.101 the"
                + " suggestion's own figure; since then the suggestion's is the order's for the trips it will take off, recomputed"
                + " here to the bit)",
                card.beatsPaved() && card.perTrip() < card.pavedPerTrip()
                        && Double.doubleToLongBits(BuildAdviceCheck.lifeFigure(g, offer, 0)) == Double.doubleToLongBits(offer.pricePerUnit()));
        List<BuildAdvice.Suggestion> one = new ArrayList<>(List.of(offer));
        assertTrue("\"Build all three\" leaves the paving out: it is not a build order",
                BuildAdvice.run(one).isEmpty() && BuildAdvice.builds(one).isEmpty());
        scale(g, base, 1);
    }

    /* ============================ 9. THE TEST PLAYER ============================ */

    static void player(Path root) {
        out.println("\n--- 9. the test player asks the advice which road and how many, and paves where it beats a new Paved Road ---");
        // FR7 (Jerus's stars 4 and 5, 2026-10-18) retired two premises of this section's first fixtures, which he decided and
        // which were not moved to let a change through. A busy street's gravel is repaired at its traffic, so on the paved
        // town (460 vehicles a day on it) the advice's road is the paved road even with the ground at nothing - it was the
        // gravel road there - and past the gravel-to-paved crossover of the land price, as before. And the paving is offered
        // by the load and not by a land price above today's, so the gravel town is one the paving can close: five gravel
        // roads (four left the paving short of the projection, and the player rightly orders the new paved road then).
        Game a = town(root, "player-new", PAVED, 3);
        double[][] baseA = noGroundFree(a);
        scale(a, baseA, 1);
        boolean first = true;
        first &= firstRoad(a, baseA, 0, "roads");
        first &= firstRoad(a, baseA, 10, "roads");
        // Paving, on a gravel town whose busy street it closes.
        Game g = town(root, "player-pave", GRAVEL, 5);
        double[][] base = noGroundFree(g);
        scale(g, base, 1);
        out.printf("      the gravel town: %.0f vehicles a day on %d gravel roads%n",
                InfrastructureManager.vehiclesADay(g.getInfrastructureManager().getUtilisation()), g.paveable());
        first &= firstRoad(g, base, 1, "paved gravel");
        assertTrue("its first road move is the advice's: the paved road on a busy street at nothing and when ground is dear,"
                + " paving where it beats a new Paved Road and closes the road", first);
        // ...and the move paves as many as the advice's card says (0.7.103; Jerus, 2026-10-10: "Fix the test player too"):
        // its count, the least that keeps the road ahead at its projection - until then as many as a road order at its gap,
        // ceil(25 x the share of output the road costs), and no more than there are.
        scale(g, base, 1);
        BuildAdvice.Suggestion paveCard = roadCard(g);
        List<LongPlaytest.Move> moves = new ArrayList<>();
        LongPlaytest.addRoadThrottle(moves, g, Math.max(1, g.getEconomyManager().getMonthGdp()) * .1);
        int before = LongPlaytest.roadsPaved, standing = g.paveable();
        final boolean[] did = { false };
        quietly(() -> did[0] = moves.get(0).act().getAsBoolean());
        int count = LongPlaytest.roadsPaved - before;
        out.printf("      the move paved %d of the %d gravel roads%n", count, standing);
        assertTrue("...and its move paves them: on the Paved Road site, out of the cash, counted in the report",
                did[0] && count >= 1 && count <= standing && g.pavingNow() == count);
        assertTrue("...as many as the advice's card: its count, the least that keeps the road ahead at its projection (0.7.103)",
                paveCard != null && paveCard.paving() && count == paveCard.count());
        scale(g, base, 1);

        /*
         * ROADS BY NEED (0.7.103, batch P4). The player ordered ceil(25 x the share of output the road costs) of the
         * road it chose, whatever the trips - ten Gravel Roads for 294 people at the playtest's month 32 - and once each
         * road kind carried its own repairs (Jerus's A11) their bill passed that town's tax. It orders the advice's card
         * now: the advice's road and the advice's count; and none when what is on site keeps the road ahead. Asked of
         * the paved town with its ground free (gravel the least) and of a town whose roads are well ahead, each with the
         * road's cost at a tenth of its output - three roads on the old rule's scale.
         */
        scale(a, baseA, 0);
        BuildAdvice.Suggestion newCard = roadCard(a);
        // ...the advice's road is the paved road on this busy street (FR7), where the gravel road was the least on it before.
        BuildingsTemplate cardRoad = newCard == null ? template(a, PAVED) : newCard.template();
        List<LongPlaytest.Move> roadMoves = new ArrayList<>();
        LongPlaytest.addRoadThrottle(roadMoves, a, Math.max(1, a.getEconomyManager().getMonthGdp()) * .1);
        int beforeA = standingAndOnSite(a, cardRoad);
        final boolean[] built = { false };
        if (!roadMoves.isEmpty()) quietly(() -> built[0] = roadMoves.get(0).act().getAsBoolean());
        int ordered = standingAndOnSite(a, cardRoad) - beforeA;
        out.printf("      the paved town, its ground free: the move ordered %d %s(s), the advice's card %d; the old"
                + " rule's %d%n", ordered, cardRoad.getName(), newCard == null ? 0 : newCard.count(), (int) Math.ceil(.1 * 25));
        assertTrue("the test player's road move orders the advice's road and the advice's count, by need (0.7.103)",
                newCard != null && !newCard.paving() && newCard.template() == template(a, PAVED) && built[0]
                        && ordered == newCard.count());
        scale(a, baseA, 1);
        Game ahead = town(root, "player-ahead", PAVED, 60);
        List<LongPlaytest.Move> aheadMoves = new ArrayList<>();
        LongPlaytest.addRoadThrottle(aheadMoves, ahead, Math.max(1, ahead.getEconomyManager().getMonthGdp()) * .1);
        int roadMovesAhead = 0;
        for (LongPlaytest.Move m : aheadMoves) {
            if (List.of("gravel road", "roads", "highway", "paved gravel").contains(m.label())) roadMovesAhead++;
        }
        out.printf("      sixty Paved Roads: roads at %.1f%% of capacity, %d road move(s)%n",
                ahead.getInfrastructureManager().getUtilisation() * 100, roadMovesAhead);
        assertTrue("...and none when what is on site keeps the road ahead at its projection (the old rule ordered three)",
                roadCard(ahead) == null && roadMovesAhead == 0);
    }

    /** The advice's card for the road, the transit lines left out of its ranking as the player leaves them: BuildAdvice.suggestFor() at its own slack, the cash and the free ground as they stand. */
    static BuildAdvice.Suggestion roadCard(Game g) {
        java.util.Set<BuildingsTemplate> lines = new java.util.HashSet<>();
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) if (t.getTransitCapacity() > 0) lines.add(t);
        return BuildAdvice.suggestFor(g, null, ROADS, g.getCash(), g.getLandManager().getAvailableSqFt(), BuildAdvice.SLACK,
                lines, false);
    }

    /** A building's count standing and on site. */
    static int standingAndOnSite(Game g, BuildingsTemplate t) {
        BuildingsStacks s = g.getBuildingManager().getStack(t);
        return g.getBuildingManager().getQuantity(t.getId()) + (s == null ? 0 : s.getUnderConstruction());
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

    /* ======================= 10. THE THROUGH TRIPS, WITH NO HIGHWAY ======================= */

    /** A built-up area, in square feet, whose through share is the dials' halfway: THROUGH_HALF_AREA_KM2 times `halves`. */
    static double areaOf(double halves) { return halves * InfrastructureManager.THROUGH_HALF_AREA_KM2 * LandManager.SQ_FT_PER_KM2; }

    /**
     * FR7 2.1 (Jerus's star 4, 2026-10-18): a city with no highway carries its
     * through trips on the streets, and strains them. The fixture is a bare
     * network struck from the dials: built-up ground at the through share's
     * halfway (so the share is THROUGH_MAX_SHARE / 2), and a load that leaves
     * the streets clear without the through trips and over their free-flow
     * line with them. With no highway the figures are the old single pool's
     * to the bit - the same inputs without the split's told, and the curve
     * itself.
     */
    static void throughStrain() {
        out.println("\n--- 10. a city with no highway carries its through trips on the streets, at a strain ---");
        double built = 6000, area = areaOf(1);
        double share = InfrastructureManager.THROUGH_MAX_SHARE / 2;
        double capacity = InfrastructureManager.BASE_CAPACITY + built;
        double load = 1.05 * capacity;
        InfrastructureManager split = new InfrastructureManager(), pool = new InfrastructureManager();
        for (InfrastructureManager r : new InfrastructureManager[] { split, pool }) {
            r.setBuiltCapacity(built);
            r.setLoad(load);
        }
        split.setThrough(0, area, 0);           // the split's inputs told; the pool is the single pool of before
        close("the through share is THROUGH_MAX_SHARE x A^2 / (A^2 + THROUGH_HALF_AREA_KM2^2): half the maximum at the halfway area",
                split.getThroughShare(), share, 1e-12);
        InfrastructureManager small = new InfrastructureManager(), large = new InfrastructureManager();
        small.setThrough(0, areaOf(.25), 0);
        large.setThrough(0, areaOf(4), 0);
        assertTrue("...flat in a small town and steep through the knee (the review's square): a quarter of the halfway area"
                        + " has a seventeenth of the maximum, four times it sixteen seventeenths",
                Math.abs(small.getThroughShare() - InfrastructureManager.THROUGH_MAX_SHARE / 17) < 1e-12
                        && Math.abs(large.getThroughShare() - InfrastructureManager.THROUGH_MAX_SHARE * 16 / 17) < 1e-12);
        close("...so the through trips are that share of the load (no gates)", split.getThroughLoad(), share * load, 1e-12);
        assertTrue("with no highway standing none of them is on one: the whole of the through load overflows onto the streets,"
                + " and none is served", split.getThroughOnHighways() == 0 && split.getThroughOverflow() == split.getThroughLoad()
                && split.getThroughServed() == 0 && split.getHighwayCapacity() == 0);
        assertTrue("fixture: without its through trips the streets would be clear (under STRAINED), and with them they are past"
                + " the free-flow line",
                (load - split.getThroughLoad()) / capacity < InfrastructureManager.STRAINED
                        && split.getUtilisation() > InfrastructureManager.FREE_FLOW);
        assertTrue("the through trips strain the streets: the network is congested, and its flow is under 1",
                split.isCongested() && split.getThroughputRatio() < 1 && split.getStreetLoad() == split.getEffectiveLoad());
        bits("with no highway getThroughputRatio() is the old single pool's, to the bit", split.getThroughputRatio(), pool.getThroughputRatio());
        bits("...and the curve at the road's own use", split.getThroughputRatio(),
                InfrastructureManager.throughputAt(load / capacity));
        assertTrue("...as are its utilisation, served, spare room, headroom, free-flow line and the street capacity under the"
                + " transit - each to the bit",
                bitsEqual(split.getUtilisation(), pool.getUtilisation()) && bitsEqual(split.getServed(), pool.getServed())
                        && bitsEqual(split.getSpareCapacity(), pool.getSpareCapacity())
                        && bitsEqual(split.getHeadroom(), pool.getHeadroom())
                        && bitsEqual(split.getFreeFlowLoad(), pool.getFreeFlowLoad())
                        && bitsEqual(split.getStreetCapacity(), pool.getCapacity())
                        && bitsEqual(split.getTransitRoadCeiling(), pool.getTransitRoadCeiling())
                        && bitsEqual(split.getStreetLoad(), pool.getEffectiveLoad()));

        // The gates' load goes through whole: it is not a share of anything.
        InfrastructureManager gated = new InfrastructureManager();
        gated.setBuiltCapacity(built);
        gated.setLoad(load);
        double gates = 900;
        gated.setThrough(0, area, gates);
        close("the freight gates' road load goes through whole: gates plus the share of what is past them",
                gated.getThroughLoad(), gates + share * (load - gates), 1e-12);
        InfrastructureManager lonely = new InfrastructureManager();
        lonely.setBuiltCapacity(built);
        lonely.setLoad(300);
        lonely.setThrough(0, areaOf(1), 1000);
        assertTrue("...and never more than the load itself", lonely.getThroughLoad() == 300 && lonely.getThroughLoad() <= lonely.getEffectiveLoad());

        // WITH A HIGHWAY standing: its capacity at free flow carries the through trips, the streets the rest.
        // ...at a heavier load, so the streets are past their free-flow line even with the highway, and the curve has work to do.
        double highway = 1500, heavy = 1.3 * capacity;
        InfrastructureManager without = new InfrastructureManager(), with = new InfrastructureManager();
        without.setBuiltCapacity(built);
        without.setLoad(heavy);
        without.setThrough(0, area, 0);
        with.setBuiltCapacity(built + highway);
        with.setLoad(heavy);
        with.setThrough(highway, area, 0);
        double carried = Math.min(with.getThroughLoad(), highway * InfrastructureManager.FREE_FLOW);
        close("with a highway the through trips it carries are its capacity at free flow, or the through load if that is less",
                with.getThroughOnHighways(), carried, 1e-12);
        close("...the streets carry the rest of the load, with the through trips past the highway on them", with.getStreetLoad(),
                heavy - carried, 1e-12);
        double streets = with.getStreetLoad() / capacity;
        close("...at the streets' utilisation: their load over their capacity, the highway's not counted", with.getUtilisation(),
                streets, 1e-12);
        double ratio = (carried + with.getStreetLoad() * InfrastructureManager.throughputAt(streets)) / heavy;
        close("...and the city's flow the trips' average: the highway at free flow, the streets at their own curve",
                with.getThroughputRatio(), ratio, 1e-12);
        assertTrue("fixture: the streets are past their free-flow line with the highway standing, so the flow is under 1",
                streets > InfrastructureManager.FREE_FLOW && with.getThroughputRatio() < 1);
        assertTrue("a highway relieves the streets that had its through trips: the flow is better than with none, the streets less used",
                with.getThroughputRatio() > without.getThroughputRatio() && with.getUtilisation() < without.getUtilisation());
    }

    static boolean bitsEqual(double a, double b) { return Double.doubleToLongBits(a) == Double.doubleToLongBits(b); }

    /* ======================= 11. A LARGE CITY'S NEEDS YOU LISTS BOTH ======================= */

    /**
     * FR7 2.1: the streets and the highways are two needs, each its own
     * measure and line on NEEDS YOU, and in a large city both come up. The
     * town is the busy paved one with the through share of a metro told to its
     * network (the same seam ServicesManager.updateInfrastructure() tells it
     * at: the built-up ground), four times the halfway area; small ground
     * leaves the through need below its line, HIGHWAY_MIN_FILL of a highway.
     */
    static void bothNeeds(Path root) {
        out.println("\n--- 11. a large city's NEEDS YOU lists both the streets and the highways; below the line, only the streets ---");
        Game g = town(root, "both", PAVED, 3);
        InfrastructureManager r = g.getInfrastructureManager();
        double townArea = r.getBuiltUpKm2() * LandManager.SQ_FT_PER_KM2;
        double one = BuildAdvice.highwayFlow(g);
        // BELOW the line: the town's own small ground.
        double smallFill = r.getThroughOverflow() / one;
        List<CityNeeds.Need> small = CityNeeds.measure(g, CityNeeds.PLAIN);
        boolean smallLists = false;
        for (CityNeeds.Need n : CityNeeds.biting(small)) smallLists |= n.kind() == CityNeeds.Kind.HIGHWAYS;
        boolean streetsListed = false;
        for (CityNeeds.Need n : CityNeeds.biting(small)) streetsListed |= n.kind() == CityNeeds.Kind.ROADS;
        out.printf("      the town: through overflow %.0f trips = %.2f of a highway (the line: %.2f); its streets %.0f%% used%n",
                r.getThroughOverflow(), smallFill, CityNeeds.HIGHWAY_MIN_FILL, r.getUtilisation() * 100);
        assertTrue("below the through-need threshold the streets are listed and the highways are not",
                smallFill < CityNeeds.HIGHWAY_MIN_FILL && streetsListed && !smallLists);
        // PAST it: the metro's ground, the town's own trips.
        r.setThrough(0, 4 * areaOf(1), 0);
        double fill = r.getThroughOverflow() / one;
        List<CityNeeds.Need> all = CityNeeds.measure(g, CityNeeds.PLAIN);
        CityNeeds.Need streetNeed = null, highwayNeed = null;
        for (CityNeeds.Need n : CityNeeds.biting(all)) {
            if (n.kind() == CityNeeds.Kind.ROADS) streetNeed = n;
            if (n.kind() == CityNeeds.Kind.HIGHWAYS) highwayNeed = n;
        }
        out.printf("      a metro's ground: through share %.0f%%, overflow %.0f trips = %.2f of a highway, %s of the through trips on highways%n",
                r.getThroughShare() * 100, r.getThroughOverflow(), fill,
                highwayNeed == null ? "-" : CityNeeds.servedPct(Math.min(1, r.getThroughServed())));
        assertTrue("past the threshold NEEDS YOU lists both: the STREETS row and the HIGHWAYS row, each its own measure and door",
                fill >= CityNeeds.HIGHWAY_MIN_FILL && streetNeed != null && highwayNeed != null
                        && BuildAdvice.measureOf(streetNeed).kind() == BuildAdvice.Kind.ROADS
                        && BuildAdvice.measureOf(highwayNeed).kind() == BuildAdvice.Kind.HIGHWAYS
                        && streetNeed.go() == CityNeeds.Go.ROADS && highwayNeed.go() == CityNeeds.Go.ROADS);
        bits("the HIGHWAYS row's figure is the through overflow over what one highway carries at free flow, to the bit",
                highwayNeed.value(), r.getThroughOverflow() / (g.getBuildingManager().getTemplateByName(HIGHWAY).getCapacity()
                        * InfrastructureManager.FREE_FLOW));
        List<BuildAdvice.Suggestion> advice = BuildAdvice.suggest(g, all);
        BuildAdvice.Suggestion streetCard = null, highwayCard = null;
        for (BuildAdvice.Suggestion s : advice) {
            if (s.measure().kind() == BuildAdvice.Kind.ROADS) streetCard = s;
            if (s.measure().kind() == BuildAdvice.Kind.HIGHWAYS) highwayCard = s;
        }
        out.printf("      the advice: %s; %s%n",
                streetCard == null ? "no street card" : streetCard.count() + " x " + streetCard.template().getName(),
                highwayCard == null ? "no highway card" : highwayCard.count() + " x " + highwayCard.template().getName());
        assertTrue("...and the advice suggests a street road for the one and an Elevated Highway for the other",
                streetCard != null && !streetCard.template().isHighway() && streetCard.template().getCapacity() > 0
                        && highwayCard != null && highwayCard.template() == template(g, HIGHWAY) && highwayCard.count() >= 1);
        // The highway card takes the through overflow off the streets: what one more highway takes off is its unit.
        double unit = BuildAdvice.unit(g, BuildAdvice.Measure.of(BuildAdvice.Kind.HIGHWAYS), template(g, HIGHWAY));
        bits("one more highway takes off the through trips that overflow, up to its capacity at free flow (its unit)", unit,
                Math.min(one, r.getThroughOverflow()));
        // ...and its order, sized to the line: the fill the highways leave is under HIGHWAY_MIN_FILL at the demand it opens to.
        Map<BuildingsTemplate, Integer> done = BuildAdvice.plus(BuildAdvice.onSite(g, BuildAdvice.Measure.of(BuildAdvice.Kind.HIGHWAYS)),
                BuildAdvice.added(g, highwayCard));
        assertTrue("...the order keeps the through need ahead: off NEEDS YOU's list at the demand it is sized to",
                BuildAdvice.ahead(g, BuildAdvice.Measure.of(BuildAdvice.Kind.HIGHWAYS), done, highwayCard.ahead()));
        // The test player orders both, each by its own measure.
        List<LongPlaytest.Move> moves = new ArrayList<>();
        LongPlaytest.addRoadThrottle(moves, g, Math.max(1, g.getEconomyManager().getMonthGdp()) * .1);
        boolean highwayMove = false, streetMove = false;
        for (LongPlaytest.Move m : moves) {
            highwayMove |= m.label().equals("highway");
            streetMove |= m.label().equals("roads") || m.label().equals("gravel road");
        }
        assertTrue("the test player's road moves hold both: the highways' card and the streets'", highwayMove && streetMove);
    }

    /* ======================= 12. A HIGHWAY GIVES NO ACCESS ======================= */

    /**
     * FR7 2.1: a highway is a carriageway nothing fronts, so its capacity is
     * not the streets'. Adding one leaves the street capacity as it was, in
     * the advice's what-if and in a played city's network.
     */
    static void noAccess(Path root) {
        out.println("\n--- 12. a highway gives no access: its capacity is not the streets' ---");
        Game g = town(root, "access", PAVED, 3);
        BuildingsTemplate highway = template(g, HIGHWAY);
        InfrastructureManager now = g.getInfrastructureManager();
        double streets = now.getStreetCapacity(), all = now.getCapacity();
        Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(g, ROADS);
        InfrastructureManager what = BuildAdvice.roads(g, BuildAdvice.plus(site, highway, 2));
        assertTrue("in the advice's what-if, two highways added leave getStreetCapacity() as it was and raise getCapacity()",
                bitsEqual(what.getStreetCapacity(), streets) && what.getCapacity() == all + 2 * highway.getCapacity()
                        && what.getHighwayCapacity() == 2 * highway.getCapacity());
        assertTrue("the streets' measure does not serve a highway, and the highways' serves only one",
                !ROADS.serves(highway) && BuildAdvice.Measure.of(BuildAdvice.Kind.HIGHWAYS).serves(highway)
                        && !BuildAdvice.Measure.of(BuildAdvice.Kind.HIGHWAYS).serves(template(g, PAVED))
                        && !BuildAdvice.Measure.of(BuildAdvice.Kind.HIGHWAYS).serves(template(g, GRAVEL))
                        && BuildAdvice.unit(g, ROADS, highway) == 0);
        // ...and in the played city's own network once one stands.
        quietly(() -> g.getBuildingManager().addStack(highway, 1, true));
        g.getServicesManager().updateInfrastructure();
        assertTrue("in the network once one stands: the street capacity is what it was, the pool is up by the highway, and the"
                + " highway capacity is its own", bitsEqual(now.getStreetCapacity(), streets) && now.getCapacity() == all + highway.getCapacity()
                && now.getHighwayCapacity() == highway.getCapacity());
        // The streets' candidates never name a highway, however far down the ranking is walked.
        java.util.Set<BuildingsTemplate> skip = new java.util.HashSet<>();
        boolean named = false;
        for (int guard = 0; guard < 8; guard++) {
            BuildAdvice.Suggestion c = BuildAdvice.suggestFor(g, null, ROADS, g.getCash(), g.getLandManager().getAvailableSqFt(),
                    BuildAdvice.SLACK, skip, true);
            if (c == null) break;
            named |= c.template().isHighway();
            skip.add(c.template());
        }
        assertTrue("the streets' ranking never names the Elevated Highway", !named);
    }
}
