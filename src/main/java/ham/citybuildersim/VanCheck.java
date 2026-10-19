package ham.citybuildersim;

import ham.citybuildersim.sectors.HeavyIndustry;

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
 *   2. THE FLEET IS SIZED BY WHAT THE PLANT MOVES - the GOODS it makes and the
 *      GOODS it buys, by weight, at NAMEPLATE rather than at this month's
 *      rate, which would be a circle. Since FR2 (2026-10-18) the vans carry the
 *      goods stream only: a bulk works keeps no vans, its tonnes go by
 *      Freight's trucks (sectors.Haulage), and the fifth throttle is the lesser
 *      of the vans' ratio and Haulage's (Sector.getFreightRatio()).
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
            // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
            // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
            // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
            game.setCashForTest(Founding.WEALTHY_CASH);
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
             * AND SOMETHING HEAVY, to be the one that keeps no vans (FR2,
             * 2026-10-18). Until FR2 a foundry was where the lorries went: it
             * buys scrap by the thousand tonnes and ships steel by the thousand,
             * and a van was kept for every 120 t of it. Steel is BULK now, and
             * its tonnes go by Freight's trucks, so the furnaces stand here to
             * show a works with fifteen thousand tonnes a month and no fleet.
             */
            game.buildStack(b.getTemplate(HeavyIndustry.ARC_FURNACE), 6, true);
            /*
             * ...AND WHAT DOES NEED VANS, because a fleet is about GOODS
             * tonnage and a town of shops barely has any: twenty convenience
             * stores move a few hundred tonnes a month between them and
             * want two or three vehicles. A department store's luxuries are
             * goods and nothing else (no bulk leg, so Haulage's half of the
             * fifth throttle is a literal one and the vans' is the whole of
             * it): the bench for the fleet's arithmetic. A meat works makes
             * goods and buys goods AND a little grain, which goes by truck:
             * the sector with both sides of the door.
             */
            game.buildStack(b.getTemplateByName("Department Store"), 6, true);
            game.buildStack(b.getTemplateByName("Meat Works"), 6, true);
            /*
             * ...AND THE MILLS ARE HELD (0.7.120, batch I3): at steel's world
             * price a furnace on imported scrap shipping its steel loses money
             * (the study's case A, -0.5% of its cost a month), and Iron &
             * Steel's planner sheds it inside two years - the furnaces stood
             * here for their tonnage would be gone before they are read. An
             * assertion whose subject is decided by a planner is an assertion
             * about the planner.
             */
            game.getBusinessInvestment().holdSector(Sectors.HEAVY_INDUSTRY);
            // ...and the two stood up for their goods are held for the same reason: their tonnage is the subject.
            game.getBusinessInvestment().holdSector(Sectors.LUXURY_RETAIL);
            game.getBusinessInvestment().holdSector(Sectors.FOOD_PROCESSING);
            for (int i = 0; i < 24; i++) game.toggleNextMonth();
        });

        /* ================================================================
           1. WHAT A FLEET IS FOR
           ================================================================ */

        System.out.println("--- a sector's fleet is sized by what its plant moves ---");

        Sector shops = game.getSectors().retail();
        Sector services = game.getSectors().businessServices();
        Sector luxury = game.getSectors().luxuryRetail();
        Sector works = game.getSectors().foodProcessing();
        Sector mills = game.getSectors().heavyIndustry();

        report("a sector that moves nothing needs no vehicles, and its ratio is one exactly",
                same(services.tonnesMoved(), 0) && same(services.vansNeeded(), 0)
                        && same(services.getVanRatio(), 1),
                String.format("%.0f tonnes, %.0f vans, ratio %s",
                        services.tonnesMoved(), services.vansNeeded(), services.getVanRatio()));

        report("...and one that moves goods needs vehicles in proportion",
                shops.tonnesMoved() > 0
                        && same(shops.vansNeeded(), shops.tonnesMoved() / Sector.TONNES_PER_VAN),
                String.format("%,.0f tonnes a month, %,.1f vehicles",
                        shops.tonnesMoved(), shops.vansNeeded()));

        /*
         * "...AND A SECTOR WITH VEHICLES ENOUGH MULTIPLIES ITS RATE BY ONE, to
         * the bit" is asked of a sector whose whole freight is goods. A shop
         * also buys grain (BULK), and since FR2 the fifth factor is the lesser
         * of the vans' ratio and Haulage's, so a shop's rate carries Haulage's
         * half whatever its vans are doing - this city has no truck depot, and
         * a shop runs a share below one. A department store buys luxuries and
         * nothing else, so it has no bulk leg and Haulage's half is a literal
         * one; the fleet is set to exactly its need, the first fleet that is
         * enough, where have/need is one to within an ulp and not one.
         */
        double luxuryNeed = luxury.vansNeeded();
        double luxuryHad = luxury.vanFleet();
        luxury.setPantry(Good.VANS, luxuryNeed);
        report("...and a sector with vehicles enough multiplies its rate by one, to the bit",
                luxuryNeed > 0 && !luxury.hasBulkLegs() && same(luxury.getHaulageRatio(), 1)
                        && same(luxury.getVanRatio(), 1) && same(luxury.getFreightRatio(), 1)
                        && same(luxury.getOperatingRate(),
                                luxury.getAverageFill() * luxury.getEnergyRatio() * luxury.getWaterRatio()
                                        * luxury.getRoadRatio() * luxury.getHealthRatio()),
                String.format("fleet %,.2f against %,.2f needed", luxury.vanFleet(), luxury.vansNeeded()));
        luxury.setPantry(Good.VANS, luxuryHad);

        /*
         * THE TONNAGE IS BOTH SIDES OF THE DOOR, but only the goods side. A
         * meat works that buys nine tonnes of meat and vegetables and ships
         * twelve and a half of ready meals and processed meat runs twenty-one
         * and a half tonnes of van movements, not twelve and a half - and a
         * fixture that only counted output would have sized every fleet short. The grain it also buys is BULK and goes by
         * Freight's trucks: counted in the works' bulk, not in its vans.
         */
        double out = 0, in = 0, bulk = 0;
        for (Good g : works.goodsMade()) {
            double t = works.getCapacity(g) * g.tonnesPerUnit();
            if (g.traffic() == Traffic.GOODS) out += t; else bulk += t;
        }
        for (Good g : works.goodsUsed()) {
            if (g == Good.VANS || g == Good.TRUCKS) continue;
            double t = works.getInputAtCapacity(g) * g.tonnesPerUnit();
            if (g.traffic() == Traffic.GOODS) in += t; else bulk += t;
        }
        report("what goes in is a van movement as much as what comes out, and its bulk is neither",
                out > 0 && in > 0 && bulk > 0
                        && Math.abs(works.tonnesMoved() - (out + in)) <= 1e-9 * (out + in),
                String.format("%,.1f out + %,.1f in, and %,.1f t of bulk it leaves to the trucks", out, in, bulk));

        /*
         * AND THE WORKS THAT HAD THE VANS BEFORE FR2 HAS NONE. Six arc
         * furnaces stand in this city, fifteen thousand tonnes a month of scrap
         * and steel, and every tonne of it is BULK: it keeps no vans, because
         * its tonnes go by Freight's trucks. The premise is caused (the bulk
         * is counted from the works' capacities) and not assumed.
         */
        double bulkTonnes = 0;
        for (Good g : mills.goodsMade()) if (g.traffic() == Traffic.BULK) bulkTonnes += mills.getCapacity(g) * g.tonnesPerUnit();
        for (Good g : mills.goodsUsed()) {
            if (g == Good.VANS || g == Good.TRUCKS) continue;
            if (g.traffic() == Traffic.BULK) bulkTonnes += mills.getInputAtCapacity(g) * g.tonnesPerUnit();
        }
        report("a bulk works keeps no vans: its tonnes go by Freight's trucks",
                bulkTonnes > 0 && mills.hasBulkLegs()
                        && same(mills.tonnesMoved(), 0) && same(mills.vansNeeded(), 0)
                        && same(mills.getVanRatio(), 1),
                String.format("%,.0f t of bulk a month, %.0f counted for vans, %.0f vans", bulkTonnes,
                        mills.tonnesMoved(), mills.vansNeeded()));

        /* ================================================================
           2. THE FLOOR AND THE CEILING OF THE RATIO
           ================================================================ */

        System.out.println("\n--- and what being short of them does ---");

        // The bench for the fleet's arithmetic: goods and nothing else, so the vans are the whole of its freight.
        Sector bench = luxury;
        double need = bench.vansNeeded();
        double had = bench.vanFleet();

        if (need > 0) {
            bench.setPantry(Good.VANS, 0);
            report("a sector with no lorries at all is slowed to the floor, not stopped",
                    same(bench.getVanRatio(), Sector.MIN_VAN_RATE),
                    String.format("%.0f%% of nameplate", bench.getVanRatio() * 100));

            report("...and it is the vans that bind, so the sector's rate is cut by the floor and no more",
                    !bench.hasBulkLegs() && same(bench.getFreightRatio(), Sector.MIN_VAN_RATE)
                            && same(bench.getOperatingRate(),
                                    bench.getAverageFill() * bench.getEnergyRatio() * bench.getWaterRatio()
                                            * bench.getRoadRatio() * bench.getHealthRatio() * Sector.MIN_VAN_RATE),
                    String.format("rate %.1f%%", bench.getOperatingRate() * 100));

            bench.setPantry(Good.VANS, need / 2);
            report("...and half a fleet is half way up from the floor",
                    Math.abs(bench.getVanRatio()
                            - (Sector.MIN_VAN_RATE + (1 - Sector.MIN_VAN_RATE) * .5)) < 1e-9,
                    String.format("%.1f%%", bench.getVanRatio() * 100));

            bench.setPantry(Good.VANS, need * 2);
            report("...and a sector with more than it needs gets no bonus for them",
                    same(bench.getVanRatio(), 1), String.valueOf(bench.getVanRatio()));
        } else {
            report("fixture: the department stores move goods, or this proves nothing", false, "no tonnage");
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

        /*
         * TEN TIMES WHAT THE BENCH NEEDS, so the tail the wear leaves is a
         * fixed share of the need whatever the bench is: a fleet of twelve
         * hundred would outlast a department store's few vans for ever and
         * prove nothing about the floor.
         */
        double stock = need * 10;
        bench.setPantry(Good.VANS, stock);
        double before = bench.vanFleet();
        bench.runFleet();
        report("a fleet wears out at one part in VAN_LIFE_MONTHS a month",
                stock > 0 && Math.abs((before - bench.vanFleet()) - stock / Sector.VAN_LIFE_MONTHS) < 1e-9,
                String.format("%.3f of %,.2f scrapped", before - bench.vanFleet(), stock));

        /*
         * AND A SECTOR THAT CANNOT REPLACE THEM LOSES THEM. Ten years of wear
         * with nothing bought, and what is left is a business hiring haulage.
         */
        for (int i = 0; i < 600; i++) bench.runFleet();
        report("...and ten years of it with nothing bought is a fleet gone",
                bench.vanFleet() < stock * .01,
                String.format("%,.3f left of %,.2f", bench.vanFleet(), stock));
        /*
         * NEAR the floor and not ON it: wear is exponential, so six hundred
         * months leaves a tail of two thirds of one percent of the stock, which
         * is a fifteenth of the need, and the ratio sits a couple of points
         * above MIN_VAN_RATE. A harness that demanded the last lorry would be
         * asserting on the exponential rather than on the rule.
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
         * The one that matters most. A city saved yesterday has stores and no
         * lorries - not because it scrapped them but because the game had none
         * - and a ratio of zero would stop every business in it dead on the
         * first tick after a load. It WAS moving goods, so it HAD lorries.
         */
        SectorState old = bench.toState();
        old.vansKnown = false;
        old.pantry.remove(Good.VANS.name());
        Game reloaded = new Game(files);
        quietly(() -> {
            reloaded.run();
            // The stores the save carries, standing again: a sector with nothing to move would run at one either way.
            reloaded.setCashForTest(Founding.WEALTHY_CASH);
            reloaded.getLandManager().setOwnedSqFt(400_000_000L);
            reloaded.buildStack(reloaded.getBuildingManager().getTemplateByName("Department Store"), 6, true);
        });
        Sector fresh = reloaded.getSectors().luxuryRetail();
        fresh.restore(old);
        report("a save from before vans reads as a sector that has never been asked",
                !fresh.isFleetKnown() && same(fresh.vanFleet(), 0), "");
        report("...and is not stopped by it, because an unasked sector runs at one",
                fresh.vansNeeded() > 0 && same(fresh.getVanRatio(), 1),
                String.format("%s against a need of %,.2f", fresh.getVanRatio(), fresh.vansNeeded()));

        SectorState carried = bench.toState();
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

        /* ================================================================
           7. AND THE VANS BURN DIESEL (0.7.83, batch O6; spec-oil 2.5)
           ================================================================ */

        System.out.println("\n--- and every van-month burns diesel, drawn at wholesale ---");

        report("a van-month is TONNES_PER_VAN's loads at a van's weight, KM_A_LOAD a load at DIESEL_LITRES_PER_100_KM: 240 L",
                same(Sector.LOADS_A_VAN_MONTH, Sector.TONNES_PER_VAN / Good.VANS.tonnesPerUnit())
                        && same(Sector.DIESEL_LITRES_A_VAN_MONTH,
                                Sector.LOADS_A_VAN_MONTH * Sector.KM_A_LOAD * Sector.DIESEL_LITRES_PER_100_KM / 100)
                        && same(Sector.DIESEL_LITRES_A_VAN_MONTH, 240),
                String.format("%.0f loads x %.0f km x %.0f L/100 km", Sector.LOADS_A_VAN_MONTH, Sector.KM_A_LOAD,
                        Sector.DIESEL_LITRES_PER_100_KM));

        // The department stores' month of driving, drawn by hand on the running city: no refinery, so every litre is the world's.
        Sector.Split dieselBefore = new Sector.Split();
        Sector.Split was = bench.pending().boughtOf(Good.DIESEL);
        dieselBefore.atHome = was.atHome;
        dieselBefore.abroad = was.abroad;
        GoodsMarket diesel = game.getMarkets().get(Good.DIESEL);
        bench.runFleet(game.getSectors());
        double litres = bench.vansNeeded() * bench.getOperatingRate() * Sector.DIESEL_LITRES_A_VAN_MONTH;
        report("a sector's month draws its van-months at the operating rate, DIESEL_LITRES_A_VAN_MONTH each, to the bit",
                litres > 0 && same(bench.getFleetDieselLitres(), litres),
                String.format("%,.0f L for %,.1f vans at %.1f%%", bench.getFleetDieselLitres(), bench.vansNeeded(),
                        bench.getOperatingRate() * 100));
        Sector.Split now = bench.pending().boughtOf(Good.DIESEL);
        report("...bought on its own books, from the world with no refinery in the city, at the import price",
                game.getSectors().refining().buildingsStanding() == 0
                        && Math.abs(now.abroad - dieselBefore.abroad - litres * diesel.importPrice()) <= 1e-9 * litres * diesel.importPrice()
                        && same(now.atHome, dieselBefore.atHome)
                        && same(bench.getFleetDieselImported(), bench.getFleetDieselCost()),
                String.format("$%,.2fk at %.6f a litre", now.abroad - dieselBefore.abroad, diesel.importPrice()));
        services.runFleet(game.getSectors());
        report("...and a sector with nothing to move burns none",
                same(services.getFleetDieselLitres(), 0) && same(services.getFleetDieselCost(), 0), "");
        double kept = bench.vanFleet();
        bench.runFleet();
        report("...nor does a bare month of wear, with no market to draw on",
                same(bench.getFleetDieselLitres(), 0) && bench.vanFleet() < kept, "");
        bench.setPantry(Good.VANS, had);

        double[] burned = new double[2];
        quietly(() -> {
            game.toggleNextMonth();
            for (Sector s : game.getSectors().all()) {
                burned[0] += s.getFleetDieselLitres();
                burned[1] += s.statement().bought.getOrDefault(Good.DIESEL, new Sector.Split()).total();
            }
        });
        report("in a month that runs, the fleets burn diesel and the businesses pay for it",
                burned[0] > 0 && burned[1] > 0,
                String.format("%,.0f L, $%,.1fk on the statements", burned[0], burned[1]));
        report("...and the money audit closes with it", game.getLastMoneyAudit().relative() < 1e-10,
                String.format("%.2e", game.getLastMoneyAudit().relative()));

        // ...and a van moved with the steel in it (0.7.120, batch I3; Jerus's C): CarCheck's THE VEHICLES MOVED WITH THE STEEL IN THEM.
        System.out.println("\n--- and a van moved with the steel in it, its plant's margin a van to the cent (0.7.120) ---");
        report("a van is the world's at 68.135: 72 less 5 t of fabricated steel at .665 and 0.6 t of machinery at .9",
                Good.VANS.worldImportPrice() == 68.135 && !Good.VANS.exportable(), String.format("%.4f", Good.VANS.worldImportPrice()));
        BuildingManager catalogue = new BuildingManager();
        catalogue.initializeTemplates();
        java.util.List<String> vanLines = new java.util.ArrayList<>();
        report("...so the van plant makes of a van over the parts in it what it did, to the cent",
                CarCheck.marginsHeld(catalogue, Good.VANS, CarCheck.VANS_WAS_IN, Double.NaN, vanLines), String.join("; ", vanLines));

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
