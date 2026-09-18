package ham.citybuildersim;

/**
 * The vans: what a sector needs, what it costs it, and what happens while it
 * waits for them.
 *
 * THE FIFTH THROTTLE, and the first one a business buys. Energy, water, road
 * and health arrive from outside - the city builds the plants, lays the streets
 * and staffs the clinics - and a sector takes whatever it is given. A lorry is
 * the sector's own capital: it decides how many it needs, it pays for them, it
 * replaces them when they wear out, and if it cannot get them its output falls.
 *
 * Jerus asked for exactly that: "a constraint - a sector with too few vans can't
 * move what it makes; its operating rate falls."
 *
 * FIVE CLAIMS.
 *
 *   1. A SECTOR WITH VEHICLES ENOUGH IS THE SECTOR IT WAS, to the bit, and so
 *      is one with nothing to move. Both are early returns rather than
 *      arithmetic, because have/need is 1 to within an ulp and not 1, and an
 *      ulp in an operating rate is a different city.
 *
 *   2. THE FLEET IS SIZED BY WHAT THE PLANT MOVES - everything it makes and
 *      everything it buys, by weight, at NAMEPLATE rather than at this month's
 *      rate, which would be a circle.
 *
 *   3. IT IS A STOCK. It wears out at VAN_LIFE_MONTHS whether or not anything
 *      can replace it, so a Commercial Vehicle Plant has a customer next year
 *      as well as this one - and a sector that can no longer buy loses its
 *      fleet over the life of a lorry rather than overnight.
 *
 *   4. YOU CANNOT MOBILISE INSTANTLY, which is the whole of what makes this a
 *      constraint rather than a bill. Built without the delivery cap it was
 *      measured and every ratio in the city read 100.0%: vans are importable,
 *      and a sector asking for a whole fleet got a whole fleet in one month,
 *      every time.
 *
 *   5. AND IT NEVER STOPS A BUSINESS DEAD. A firm short of its own lorries
 *      hires haulage and sends fuller loads. The same shape as the road's own
 *      floor, and for the same reason.
 *
 * See claude/the-sixth-link.md and Sector's THE FLEET.
 */
public class VanCheck {

    static int fails = 0;

    static void quietly(Runnable r) {
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { r.run(); } finally { System.setOut(real); }
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-68s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        System.out.printf("%-68s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    /** Bitwise. Same family as CarCheck and RailCheck, same reason. */
    static boolean same(double a, double b) {
        return Double.compare(a, b) == 0 || (Double.isNaN(a) && Double.isNaN(b));
    }

    public static void main(String[] args) {

        GameFiles files = GameFiles.scratch("vancheck");
        Game game = new Game(files);

        quietly(() -> {
            game.newGame();
            game.getForeignAccounts().pinRate(1.0);
            game.getWorldEconomy().pin();
            game.getLandManager().setOwnedSqFt(400_000_000L);
            BuildingManager b = game.getBuildingManager();
            game.buildStack(b.getTemplateByName("House"), 900, true);
            game.buildStack(b.getTemplateByName("Convenience Store"), 20, true);
            game.buildStack(b.getTemplateByName("Small Grocery Store"), 6, true);
            game.buildStack(b.getTemplateByName("Paved Road"), 60, true);
            game.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            game.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            game.buildStack(b.getTemplateByName("Construction Depot"), 4, true);
            /*
             * AND SOMETHING HEAVY, because a fleet is about tonnage and a town
             * of shops barely has any: twenty convenience stores move two
             * hundred and eighty-eight tonnes a month between them and want
             * two and a half vehicles. A foundry buys scrap by the thousand
             * tonnes and ships steel by the thousand, which is where lorries
             * actually go.
             */
            game.buildStack(b.getTemplateByName("Steel Foundry"), 6, true);
            for (int i = 0; i < 24; i++) game.toggleNextMonth();
        });

        /* ================================================================
           1. WHAT A FLEET IS FOR
           ================================================================ */

        System.out.println("--- a sector's fleet is sized by what its plant moves ---");

        Sector shops = game.getSectors().retail();
        Sector services = game.getSectors().businessServices();

        report("a sector that moves nothing needs no vehicles, and its ratio is one exactly",
                same(services.tonnesMoved(), 0) && same(services.vansNeeded(), 0)
                        && same(services.getVanRatio(), 1),
                String.format("%.0f tonnes, %.0f vans, ratio %s",
                        services.tonnesMoved(), services.vansNeeded(), services.getVanRatio()));

        report("...and one that moves something needs vehicles in proportion",
                shops.tonnesMoved() > 0
                        && same(shops.vansNeeded(), shops.tonnesMoved() / Sector.TONNES_PER_VAN),
                String.format("%,.0f tonnes a month, %,.1f vehicles",
                        shops.tonnesMoved(), shops.vansNeeded()));

        report("...and a sector with vehicles enough multiplies its rate by one, to the bit",
                same(shops.getVanRatio(), 1)
                        && same(shops.getOperatingRate(),
                                shops.getAverageFill() * shops.getEnergyRatio() * shops.getWaterRatio()
                                        * shops.getRoadRatio() * shops.getHealthRatio()),
                String.format("fleet %,.1f against %,.1f needed", shops.vanFleet(), shops.vansNeeded()));

        /*
         * THE TONNAGE IS BOTH SIDES OF THE DOOR. A mill that buys a hundred
         * tonnes of ore and ships eighty of steel runs a hundred and eighty
         * tonnes of lorry movements, not eighty - and a fixture that only
         * counted output would have sized every fleet at half.
         */
        Sector mills = game.getSectors().heavyIndustry();
        double out = 0, in = 0;
        for (Good g : mills.goodsMade()) out += mills.getCapacity(g) * g.tonnesPerUnit();
        for (Good g : mills.goodsUsed()) if (g != Good.VANS) in += mills.getInputAtCapacity(g) * g.tonnesPerUnit();
        report("what goes in is a lorry movement as much as what comes out",
                same(mills.tonnesMoved(), out + in),
                String.format("%,.0f out + %,.0f in", out, in));

        /* ================================================================
           2. THE FLOOR AND THE CEILING OF THE RATIO
           ================================================================ */

        System.out.println("\n--- and what being short of them does ---");

        Sector bench = game.getSectors().heavyIndustry();
        double need = bench.vansNeeded();
        double had = bench.vanFleet();

        if (need > 0) {
            bench.setPantry(Good.VANS, 0);
            report("a sector with no lorries at all is slowed to the floor, not stopped",
                    same(bench.getVanRatio(), Sector.MIN_VAN_RATE),
                    String.format("%.0f%% of nameplate", bench.getVanRatio() * 100));

            bench.setPantry(Good.VANS, need / 2);
            report("...and half a fleet is half way up from the floor",
                    Math.abs(bench.getVanRatio()
                            - (Sector.MIN_VAN_RATE + (1 - Sector.MIN_VAN_RATE) * .5)) < 1e-9,
                    String.format("%.1f%%", bench.getVanRatio() * 100));

            bench.setPantry(Good.VANS, need * 2);
            report("...and a sector with more than it needs gets no bonus for them",
                    same(bench.getVanRatio(), 1), String.valueOf(bench.getVanRatio()));
        } else {
            report("fixture: the mills move something, or this proves nothing", false, "no tonnage");
        }
        bench.setPantry(Good.VANS, had);

        /* ================================================================
           3. YOU CANNOT PUT A FLEET ON THE ROAD IN A MONTH
           ================================================================ */

        System.out.println("\n--- a fleet is built up, not bought ---");

        bench.setPantry(Good.VANS, 0);
        report("a sector with nothing asks for what it can take delivery of, not for everything",
                Math.abs(bench.bid(Good.VANS) - need / Sector.FLEET_DELIVERY_MONTHS) < 1e-9,
                String.format("%,.1f of the %,.1f it needs", bench.bid(Good.VANS), need));

        bench.setPantry(Good.VANS, need * .99);
        report("...and one that is nearly there asks only for the gap",
                Math.abs(bench.bid(Good.VANS) - need * .01) < 1e-6,
                String.format("%,.2f", bench.bid(Good.VANS)));

        bench.setPantry(Good.VANS, need);
        report("...and one with enough asks for nothing",
                same(bench.bid(Good.VANS), 0), "");
        bench.setPantry(Good.VANS, had);

        /* ================================================================
           4. IT WEARS OUT
           ================================================================ */

        System.out.println("\n--- and a fleet is a stock, not a flow ---");

        bench.setPantry(Good.VANS, 1200);
        double before = bench.vanFleet();
        bench.runFleet();
        report("a fleet wears out at one part in VAN_LIFE_MONTHS a month",
                Math.abs((before - bench.vanFleet()) - 1200 / Sector.VAN_LIFE_MONTHS) < 1e-9,
                String.format("%.2f of 1,200 scrapped", before - bench.vanFleet()));

        /*
         * AND A SECTOR THAT CANNOT REPLACE THEM LOSES THEM. Ten years of wear
         * with nothing bought, and what is left is a business hiring haulage.
         */
        for (int i = 0; i < 600; i++) bench.runFleet();
        report("...and ten years of it with nothing bought is a fleet gone",
                bench.vanFleet() < 1200 * .01,
                String.format("%,.2f left of 1,200", bench.vanFleet()));
        /*
         * NEAR the floor and not ON it: wear is exponential, so six hundred
         * months leaves a tail of about eight vehicles out of twelve hundred
         * and the ratio sits a couple of points above MIN_VAN_RATE. A harness
         * that demanded the last lorry would be asserting on the exponential
         * rather than on the rule.
         */
        report("...which leaves the sector down at its floor",
                bench.getVanRatio() < Sector.MIN_VAN_RATE + .05
                        && bench.getVanRatio() >= Sector.MIN_VAN_RATE,
                String.format("%.1f%% against a floor of %.0f%%",
                        bench.getVanRatio() * 100, Sector.MIN_VAN_RATE * 100));
        bench.setPantry(Good.VANS, had);

        /* ================================================================
           5. A SAVE FROM BEFORE VANS HAD VANS
           ================================================================ */

        System.out.println("\n--- and every city that exists already owns a fleet ---");

        /*
         * The one that matters most. A city saved yesterday has mills and no
         * lorries - not because it scrapped them but because the game had none
         * - and a ratio of zero would stop every factory in it dead on the
         * first tick after a load. It WAS moving steel, so it HAD lorries.
         */
        SectorState old = mills.toState();
        old.vansKnown = false;
        old.pantry.remove(Good.VANS.name());
        Game reloaded = new Game(files);
        quietly(reloaded::run);
        Sector fresh = reloaded.getSectors().heavyIndustry();
        fresh.restore(old);
        report("a save from before vans reads as a sector that has never been asked",
                !fresh.isFleetKnown() && same(fresh.vanFleet(), 0), "");
        report("...and is not stopped by it, because an unasked sector runs at one",
                same(fresh.getVanRatio(), 1), String.valueOf(fresh.getVanRatio()));

        SectorState carried = mills.toState();
        report("...while a save from this build carries the fleet it had",
                carried.vansKnown
                        && Math.abs(carried.pantry.getOrDefault(Good.VANS.name(), 0.0) - had) < 1e-9,
                String.format("%,.1f vehicles", carried.pantry.getOrDefault(Good.VANS.name(), 0.0)));

        /* ================================================================
           6. IN A CITY THAT RUNS
           ================================================================ */

        System.out.println("\n--- and the city really buys them ---");

        double[] spent = new double[1];
        double[] fleet = new double[1];
        quietly(() -> {
            for (int i = 0; i < 12; i++) game.toggleNextMonth();
            for (Sector s : game.getSectors().all()) {
                spent[0] += s.statement().bought
                        .getOrDefault(Good.VANS, new Sector.Split()).total();
                fleet[0] += s.vanFleet();
            }
        });
        report("a city with industry in it runs a fleet",
                fleet[0] > 0, String.format("%,.0f vehicles across every sector", fleet[0]));
        report("...and pays for it, every month, on somebody's cost line",
                spent[0] > 0, String.format("$%,.0fk this month", spent[0]));

        GoodsMarket van = game.getMarkets().get(Good.VANS);
        report("...at a price the market struck, inside its band",
                van.getLocalPrice() > 0 && van.getLocalPrice() <= van.ceiling() + 1e-9,
                String.format("$%,.1fk a vehicle, band $%,.1fk-$%,.1fk",
                        van.getLocalPrice(), van.floor(), van.ceiling()));

        /*
         * AND THE PLANT HAS A CUSTOMER, which is the other half of why this
         * exists. Vans are deliberately not exportable - an unbounded export
         * market at a fixed floor built two hundred and forty Commercial
         * Vehicle Plants and shipped thirty-six thousand vans a month to
         * nobody - so until today a Commercial Vehicle Plant had no market at
         * all. The city's own industry is the market.
         */
        report("the city's own industry is the market for a van plant",
                van.getBid() > 0,
                String.format("%,.0f vehicles wanted this month", van.getBid()));

        System.out.println();
        if (fails == 0) {
            System.out.println("The vans work: a fleet is sized by the tonnage, built up rather than"
                    + " bought, worn out, and a sector short of one is slowed and not stopped.");
        } else {
            System.out.println(fails + " FAILED");
        }
        System.exit(fails);
    }
}
