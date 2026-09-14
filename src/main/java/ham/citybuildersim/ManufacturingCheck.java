package ham.citybuildersim;

import ham.citybuildersim.sectors.Manufacturing;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The ninth sector: what the city makes out of its own steel, and ships.
 *
 * WHAT THIS HAS TO PROVE, and why none of it is "the city got bigger". The
 * crime batch established that the long-run population of a city moves ten
 * percent on perturbations of one part in a million, so no ensemble this
 * harness could afford is evidence about a sector. Sixteen seeds of 333 years
 * say the effect is real and enormous - 6/16 cities past forty thousand
 * becoming 16/16, median 16,834 becoming 89,700 - and that measurement lives
 * in claude/manufacturing.md where a number that will drift belongs. What is
 * CHECKABLE is whether the thing works:
 *
 *   1. the two goods are export-only, flow not stock, and clear at the floor
 *   2. STEEL has a ceiling now, and it binds only when somebody here bids
 *   3. the templates are the arithmetic the design claims, at every steel price
 *   4. the traps: no gated post without a licence, and the staffing floor bites
 *   5. the two brakes are different - fabrication dies on the steel price,
 *      the machine works on the wage bill, and each is CAUSED here
 *   6. it books like everybody else, the audit holds, and it reloads
 *
 * Every fixture causes its condition. The steel test moves the steel price
 * rather than waiting for a mill; the wage test moves the floor rather than
 * hoping a run drifts.
 */
public class ManufacturingCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-64s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void check(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-64s FAIL  %.6f != %.6f%n", label, actual, expected);
        } else {
            out.printf("%-64s OK%n", label);
        }
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static final String SHOP  = "Fabrication Shop";
    static final String WORKS = "Fabrication Works";
    static final String MACH  = "Machine Works";

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ============ 1. two goods the city makes and cannot buy ============ */
        out.println("--- two goods that leave ---");

        Good[] kinds = { Good.FABRICATED_STEEL, Good.MACHINERY };
        for (Good g : kinds) {
            assertTrue(g.label() + " is not importable - nobody here buys one", !g.importable());
            assertTrue(g.label() + " is exportable", g.exportable());
            assertTrue(g.label() + " is cut to order, not warehoused", !g.stockable());
            assertTrue(g.label() + " clears on the band", g.traded());
            assertTrue(g.label() + " is not tax exempt - zero-rated, like steel", !g.taxExempt());
            assertTrue(g.label() + " is sold by the tonne", "tonne".equals(g.unit()));
        }
        assertTrue("shaped steel is worth more than the steel in it",
                Good.FABRICATED_STEEL.worldExportPrice() > Good.STEEL.worldImportPrice());
        assertTrue("...and a machine is worth more than the beam",
                Good.MACHINERY.worldExportPrice() > Good.FABRICATED_STEEL.worldExportPrice());

        GoodsMarket fab = new GoodsMarket(Good.FABRICATED_STEEL);
        fab.strike(1200, 0, 0);
        check("nobody here bidding - a tonne fetches the export floor",
                fab.getLocalPrice(), fab.floor(), 1e-12);
        check("...which at founding is the world's own price",
                fab.floor(), Good.FABRICATED_STEEL.worldExportPrice(), 1e-12);
        fab.setExchangeRate(2);
        check("a weak currency lifts what the work fetches",
                fab.floor(), Good.FABRICATED_STEEL.worldExportPrice() * 2, 1e-12);

        /* ============ 2. and steel has a ceiling now ============ */
        out.println("\n--- steel finally has somebody to sell to ---");

        assertTrue("steel is importable - something here buys it", Good.STEEL.importable());
        check("...at the US hot-rolled band", Good.STEEL.worldImportPrice(), 1.284, 1e-12);
        assertTrue("...which is above what a mill gets shipping it out",
                Good.STEEL.worldImportPrice() > Good.STEEL.worldExportPrice());

        GoodsMarket st = new GoodsMarket(Good.STEEL);
        st.strike(1200, 0, 0);
        check("a city with mills and no fabricator: steel sits on the floor, as it always did",
                st.getLocalPrice(), st.floor(), 1e-12);
        st.strike(0, 0, 1200);
        check("a city with a fabricator and no mill pays the world's price",
                st.getLocalPrice(), st.ceiling(), 1e-12);
        st.strike(1200, 0, 1200);
        check("...and a city with both splits the difference",
                st.getLocalPrice(), (st.floor() + st.ceiling()) / 2, 1e-9);
        out.printf("   floor $%.0f, ceiling $%.0f, and a matched pair clears at $%.0f%n",
                st.floor() * 1000, st.ceiling() * 1000, st.getLocalPrice() * 1000);

        /* ============ 3. the templates are the design's arithmetic ============ */
        out.println("\n--- the three plants ---");

        Path root = Files.createTempDirectory("mfgcheck");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game plain = new Game(files);
        quietly(plain::run);
        BuildingManager bm = plain.getBuildingManager();

        String[] names = { SHOP, MACH, WORKS };
        Good[] makes = { Good.FABRICATED_STEEL, Good.MACHINERY, Good.FABRICATED_STEEL };
        // output a month, steel in, jobs, all-in build cost in thousands
        double[][] want = {
            { 1200, 1260, 135, 31800,  480000 },
            {  180,  261, 173, 19440,  150000 },
            { 3000, 3150, 276, 79558, 1150000 },
        };

        for (int i = 0; i < names.length; i++) {
            BuildingsTemplate t = bm.getTemplateByName(names[i]);
            assertTrue(names[i] + " exists", t != null);
            if (t == null) continue;
            check(names[i] + ": what it makes a month", t.makes(makes[i]), want[i][0], 0);
            check(names[i] + ": steel it eats a month", t.uses(Good.STEEL), want[i][1], 0);
            check(names[i] + ": posts", jobsOf(t), want[i][2], 0);
            check(names[i] + ": all-in build cost",
                    t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials(),
                    want[i][3], 12);
            check(names[i] + ": the ground it stands on", t.getLandSqFt(), want[i][4], 1);
            // The build cost is ONE YEAR of what the plant bills. That is the
            // strong end of the game's own convention (the Steel Foundry is
            // 2.5 years, the Contact Centre 1.4) and it is deliberate: the
            // brake on this sector is the land, not the price of the shed.
            check(names[i] + ": a year of its own billings, which is the whole shed",
                    want[i][3] / (t.makes(makes[i]) * makes[i].worldExportPrice() * 12), 1.0, .01);
            assertTrue(names[i] + " is owned by the sector, not the city",
                    Sectors.MANUFACTURING.equals(t.getSector()));
            assertTrue(names[i] + " pays no upkeep - it pays maintenance like everyone",
                    t.getUpkeep() == 0);
            assertTrue(names[i] + " is filed with the mills it buys from",
                    t.getCategory() == BuildingType.HEAVY_INDUSTRY);
        }

        check("fabrication loses five percent of every tonne it cuts",
                bm.getTemplateByName(SHOP).uses(Good.STEEL)
                        / bm.getTemplateByName(SHOP).makes(Good.FABRICATED_STEEL), 1.05, 1e-9);
        check("...and the works loses the same, being the same trade",
                bm.getTemplateByName(WORKS).uses(Good.STEEL)
                        / bm.getTemplateByName(WORKS).makes(Good.FABRICATED_STEEL), 1.05, 1e-9);
        check("machining takes a tonne and a bit under a half",
                bm.getTemplateByName(MACH).uses(Good.STEEL)
                        / bm.getTemplateByName(MACH).makes(Good.MACHINERY), 1.45, 1e-9);

        /* ---- the table the design note claims, struck from the templates ---- */
        out.println("\n   what a plant clears, per month, as a share of its build cost:");
        out.println("                        revenue   steel   wages | on imported   mid band   at the floor");
        double[] steelAt = { Good.STEEL.worldImportPrice(),
                (Good.STEEL.worldImportPrice() + Good.STEEL.worldExportPrice()) / 2,
                Good.STEEL.worldExportPrice() };
        for (int i = 0; i < names.length; i++) {
            BuildingsTemplate t = bm.getTemplateByName(names[i]);
            if (t == null) continue;
            double revenue = t.makes(makes[i]) * makes[i].worldExportPrice();
            double payroll = payrollOf(t);
            double utilities = t.getElectricityConsumption() * plain.getEconomyManager().getPricePerWatt()
                    + t.getWaterConsumption() * plain.getEconomyManager().getPricePerWaterUnit();
            double cost = t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials();
            double[] ret = new double[3];
            for (int k = 0; k < 3; k++) {
                ret[k] = (revenue - t.uses(Good.STEEL) * steelAt[k] - payroll - utilities) / cost;
            }
            out.printf("   %-20s $%,8.0fk  %5.1f%%  %5.1f%% |    %.2f%%/mo   %.2f%%/mo      %.2f%%/mo%n",
                    names[i], revenue,
                    t.uses(Good.STEEL) * steelAt[0] / revenue * 100, payroll / revenue * 100,
                    ret[0] * 100, ret[1] * 100, ret[2] * 100);

            assertTrue(names[i] + ": it pays even when the steel is the world's",
                    ret[0] > 0);
            assertTrue(names[i] + ": ...and pays better with a mill in the city",
                    ret[2] > ret[0]);
            /*
             * A GOOD BUSINESS AT EVERY STEEL PRICE, ON PURPOSE. Four
             * calibrations were measured over sixteen seeds and the price of
             * the shed turned out to decide only how big the city gets, never
             * whether it makes it - at three times this cost every one of
             * sixteen cities still took off, inside a 23,000-person band. So
             * the shed is priced where the price index and the currency read
             * best, and the GROUND is the brake. See the note in
             * BuildingManager and claude/manufacturing.md.
             */
            assertTrue(names[i] + ": ...and is a good business either way, which is the design",
                    ret[0] > .01 && ret[2] < .04);
        }

        /*
         * THE TWO ARE BOUNDED BY DIFFERENT THINGS, which is the entire design
         * and the one assertion here that would fail if somebody "tidied" the
         * numbers into a single shape. Fabrication is steel and a fifth wages;
         * the machine works is a fifth steel and half wages.
         */
        BuildingsTemplate shop = bm.getTemplateByName(SHOP), mach = bm.getTemplateByName(MACH);
        double shopRev = shop.makes(Good.FABRICATED_STEEL) * Good.FABRICATED_STEEL.worldExportPrice();
        double machRev = mach.makes(Good.MACHINERY) * Good.MACHINERY.worldExportPrice();
        double shopSteel = shop.uses(Good.STEEL) * Good.STEEL.worldImportPrice() / shopRev;
        double machSteel = mach.uses(Good.STEEL) * Good.STEEL.worldImportPrice() / machRev;
        double shopPay = payrollOf(shop) / shopRev, machPay = payrollOf(mach) / machRev;
        assertTrue("fabrication lives and dies on the steel price", shopSteel > 2 * shopPay);
        assertTrue("...and the machine works on the wage bill", machPay > 2 * machSteel);
        assertTrue("neither is a business at all if both bite",
                shopSteel + shopPay < 1 && machSteel + machPay < 1);

        /* ---- and the ground is the brake, which is the other half of it ---- */
        BuildingsTemplate works = bm.getTemplateByName(WORKS);
        BuildingsTemplate foundry = bm.getTemplateByName("Steel Foundry");
        double perJob = foundry.getLandSqFt() / jobsOf(foundry);
        out.printf("   ground a post: shop %,.0f, works %,.0f, machine works %,.0f, steel foundry %,.0f sq ft%n",
                shop.getLandSqFt() / jobsOf(shop), works.getLandSqFt() / jobsOf(works),
                mach.getLandSqFt() / jobsOf(mach), perJob);
        assertTrue("a fabrication yard takes more ground a post than a steel mill",
                shop.getLandSqFt() / jobsOf(shop) > perJob
                        && works.getLandSqFt() / jobsOf(works) > perJob);
        assertTrue("...and more than anything else an investor builds",
                shop.getLandSqFt() / jobsOf(shop)
                        > bm.getTemplateByName("Contact Centre").getLandSqFt() / 300.0);
        assertTrue("the machine works does not - machining happens indoors",
                mach.getLandSqFt() / jobsOf(mach) < perJob);
        assertTrue("...and pays for its density in power instead",
                mach.getElectricityConsumption() / jobsOf(mach)
                        > 3 * shop.getElectricityConsumption() / jobsOf(shop));

        /* ============ 4. the traps ============ */
        out.println("\n--- the traps the eighth sector paid for ---");

        for (String n : names) {
            BuildingsTemplate t = bm.getTemplateByName(n);
            if (t == null) continue;
            for (JobType job : JobType.values()) {
                if (t.getJobs(job) <= 0) continue;
                if (!PopulationManager.isGated(job)) continue;
                assertTrue(n + " only has gated posts it declares a licence for",
                        job == t.getRequiresLicence());
            }
        }
        assertTrue("no plant here needs a licence at all - none of them practise anything",
                bm.getTemplateByName(SHOP).getRequiresLicence() == null
                        && bm.getTemplateByName(MACH).getRequiresLicence() == null
                        && bm.getTemplateByName(WORKS).getRequiresLicence() == null);
        out.println("   (thirty gated finance posts in an ungated building put the eighth");
        out.println("    sector's payroll at 95% of revenue and sent it bust nineteen times.)");

        check("the staffing floor is eighty percent", Sector.MIN_STAFFABLE_TO_ORDER, .80, 0);

        Game village = new Game(files);
        quietly(() -> {
            village.run();
            village.getLandManager().setOwnedSqFt(village.getLandManager().getOwnedSqFt() + 200_000_000L);
        });
        Manufacturing small = village.getSectors().manufacturing();
        double staffable = small.staffableShare(bm.getTemplateByName(WORKS));
        out.printf("   a village could staff %.0f%% of a two-hundred-and-seventy-six-post works%n",
                staffable * 100);
        assertTrue("a village cannot staff the biggest plant in the game",
                staffable < Sector.MIN_STAFFABLE_TO_ORDER);
        BusinessInvestment.Decision no = small.plan(village.getBusinessInvestment(), village);
        assertTrue("...so the sector does not ask for one", !no.build);
        assertTrue("...and says why, in people rather than money",
                no.reason != null && no.reason.contains("staff"));
        out.println("   \"" + no.reason + "\"");

        /* ============ 5. the two brakes, each caused ============ */
        out.println("\n--- what closes it, and it is not the same thing twice ---");

        Game city = new Game(files);
        quietly(() -> {
            city.run();
            BuildingManager b = city.getBuildingManager();
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 400_000_000L);
            b.addStack(b.getTemplateByName("House"), 2000, true);
            b.addStack(b.getTemplateByName("Convenience Store"), 40, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 2, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 2, true);
            b.addStack(b.getTemplateByName("Paved Road"), 40, true);
            b.addStack(b.getTemplateByName(SHOP), 2, true);
            b.addStack(b.getTemplateByName(MACH), 1, true);
            for (int i = 0; i < 24; i++) city.simulateMonths(1);
        });

        Manufacturing man = city.getSectors().manufacturing();
        check("two shops is 2,400 tonnes of fabricating capacity",
                man.getCapacity(Good.FABRICATED_STEEL), 2400, 0);
        check("...and one machine works is 180 of machinery",
                man.getCapacity(Good.MACHINERY), 180, 0);
        assertTrue("it sold something", man.statement().revenue > 0);
        assertTrue("...and every tonne of it left the city", man.statement().exports > 0);
        check("everything it sold, it exported",
                man.statement().exports, man.statement().revenue, 1e-6);
        assertTrue("it bought steel to do it", man.statement().inputs > 0);
        out.printf("   steel is %.0f%% of what it billed and wages %.0f%%%n",
                man.steelShare() * 100, man.payrollShare() * 100);
        assertTrue("the two together are under one, or it would be shedding",
                man.costShare() > 0 && man.costShare() < 1);

        BusinessInvestment plans = city.getBusinessInvestment();

        // THE STEEL PRICE. Move the world's steel, not the city: nothing about
        // the plants, the staff or the clients changes.
        double baseShop = man.estimatedMonthlyProfit(shop, plans);
        double baseMach = man.estimatedMonthlyProfit(mach, plans);
        GoodsMarket steel = city.getMarkets().get(Good.STEEL);
        double wasSteel = steel.getLocalPrice();
        // Half again, which is roughly what losing the city's mills does to a
        // fabricator - not a contrived multiple.
        steel.setLocalPrice(wasSteel * 1.5);
        double dearShop = man.estimatedMonthlyProfit(shop, plans);
        double dearMach = man.estimatedMonthlyProfit(mach, plans);
        out.printf("   steel at $%.0f: a shop clears $%,.0fk and a machine works $%,.0fk%n",
                wasSteel * 1000, baseShop, baseMach);
        out.printf("   steel at $%.0f: a shop clears $%,.0fk and a machine works $%,.0fk%n",
                wasSteel * 1500, dearShop, dearMach);
        assertTrue("dear steel makes another shop worth less", dearShop < baseShop);
        assertTrue("...far less: it is most of what a shop spends", dearShop < 0);
        assertTrue("...while the machine works is still worth building", dearMach > 0);
        assertTrue("...which is the whole point of having both",
                (baseShop - dearShop) > 3 * (baseMach - dearMach));
        steel.setLocalPrice(wasSteel);

        // THE WAGE BILL, from the other side. Move the floor every wage is
        // built on; the machine works is the one that closes.
        double cheapMach = man.estimatedMonthlyProfit(mach, plans);
        double cheapShop = man.estimatedMonthlyProfit(shop, plans);
        double floor = city.getLabourMarket().getMinimumWage();
        quietly(() -> {
            city.getLabourMarket().setMinimumWage(floor * 4);
            for (int i = 0; i < 36; i++) city.simulateMonths(1);
        });
        double dearWageMach = man.estimatedMonthlyProfit(mach, plans);
        double dearWageShop = man.estimatedMonthlyProfit(shop, plans);
        out.printf("   wages x4: a machine works goes $%,.0fk -> $%,.0fk, a shop $%,.0fk -> $%,.0fk%n",
                cheapMach, dearWageMach, cheapShop, dearWageShop);
        assertTrue("quadrupling the wage floor makes another machine works worth less",
                dearWageMach < cheapMach);
        assertTrue("...and it is the machine works that gives way first",
                (cheapMach - dearWageMach) / Math.max(1, Math.abs(cheapMach))
                        > (cheapShop - dearWageShop) / Math.max(1, Math.abs(cheapShop)));

        /* ============ 6. the books, the audit, and a reload ============ */
        out.println("\n--- it books like everybody else ---");

        Game booked = new Game(files);
        quietly(() -> {
            booked.run();
            BuildingManager b = booked.getBuildingManager();
            booked.getLandManager().setOwnedSqFt(booked.getLandManager().getOwnedSqFt() + 400_000_000L);
            b.addStack(b.getTemplateByName("House"), 2000, true);
            b.addStack(b.getTemplateByName("Convenience Store"), 40, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 2, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 2, true);
            b.addStack(b.getTemplateByName("Paved Road"), 40, true);
            b.addStack(b.getTemplateByName("Steel Foundry"), 2, true);
            b.addStack(b.getTemplateByName(SHOP), 2, true);
            b.addStack(b.getTemplateByName(MACH), 1, true);
            for (int i = 0; i < 24; i++) booked.simulateMonths(1);
        });

        double worstBooks = 0;
        for (SectorBooks.SectorMonth s : booked.getSectorBooks().thisMonth()) {
            worstBooks = Math.max(worstBooks, Math.abs(s.unexplained()));
        }
        assertTrue("every sector's statement still foots", worstBooks < 1e-6);
        assertTrue("and the money identity holds with a ninth sector in it",
                booked.getLastMoneyAudit().relative() < 1e-9);

        Manufacturing withMill = booked.getSectors().manufacturing();
        Sector mills = booked.getSectors().heavyIndustry();
        double steelPrice = booked.getMarkets().get(Good.STEEL).getLocalPrice();
        out.printf("   with two foundries next door, steel clears at $%.0f - floor $%.0f, ceiling $%.0f%n",
                steelPrice * 1000,
                booked.getMarkets().get(Good.STEEL).floor() * 1000,
                booked.getMarkets().get(Good.STEEL).ceiling() * 1000);
        assertTrue("the mills sold steel at home for the first time in this game's history",
                mills.statement().localSales > 0);
        assertTrue("...and got more than the ship would have paid",
                steelPrice > booked.getMarkets().get(Good.STEEL).floor());
        assertTrue("...and less than the fabricator would have paid the world",
                steelPrice <= booked.getMarkets().get(Good.STEEL).ceiling() + 1e-9);
        assertTrue("the fabricators bought from them", withMill.input(Good.STEEL).boughtLocal > 0);

        assertTrue("Manufacturing is the ninth sector",
                Sectors.KEYS.length > 8 && Sectors.KEYS[8].equals(Sectors.MANUFACTURING));
        assertTrue("...and the share register is the sectors and the bank",
                Equity.COMPANIES.length == Sectors.KEYS.length + 1);
        // No assertion on GameVersion.SAVE_FORMAT here. There was one, pinned
        // at 23, and it went red the morning the TENTH sector bumped it to 24 -
        // a harness about fabricated steel failing over a number it does not
        // own. What this harness cares about is that a city with plants in it
        // saves and comes back, which is the next four lines.

        quietly(() -> booked.saveGame(10));
        Game back = new Game(files);
        quietly(() -> { back.run(); back.loadGame(10); });
        Manufacturing there = back.getSectors().manufacturing();
        check("a reloaded city has the same fabricating capacity",
                there.getCapacity(Good.FABRICATED_STEEL),
                withMill.getCapacity(Good.FABRICATED_STEEL), 0);
        check("...the same machinery capacity",
                there.getCapacity(Good.MACHINERY), withMill.getCapacity(Good.MACHINERY), 0);
        check("...the same cash",
                back.getEconomyManager().getSectorCash(Sectors.MANUFACTURING),
                booked.getEconomyManager().getSectorCash(Sectors.MANUFACTURING), 1e-9);
        check("...and the same steel price, restored rather than recomputed",
                back.getMarkets().get(Good.STEEL).getLocalPrice(), steelPrice, 1e-9);

        out.println();
        if (fails == 0) {
            out.println("Manufacturing works: the world pays by the tonne, the steel price decides"
                    + " the shops and the wage bill decides the machine works.");
        } else {
            out.println(fails + " check(s) failed.");
        }
        System.exit(fails);
    }

    static double jobsOf(BuildingsTemplate t) {
        double n = 0;
        for (JobType job : JobType.values()) n += t.getJobs(job);
        return n;
    }

    static double payrollOf(BuildingsTemplate t) {
        double n = 0;
        for (JobType job : JobType.values()) n += t.getJobs(job) * PayTier.of(job).getMonthlyWage();
        return n;
    }
}
