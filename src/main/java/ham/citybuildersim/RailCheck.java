package ham.citybuildersim;

import ham.citybuildersim.sectors.Rail;

/**
 * The railway: what it charges, who pays it, and what it does to the band.
 *
 * WHY THIS HARNESS EXISTS. TradeCostCheck proved the decomposition - that every
 * delivered price is still the literal it was, and that three quarters of the
 * wedge is freight. This one is about the business that now charges for that
 * freight, and it has four claims to hold that the earlier one cannot:
 *
 *   1. A CITY WITH NO RAILWAY IS THE CITY IT WAS, to the bit, on all four
 *      prices - the band and the net pair both. Everything below only matters
 *      if this holds, because it is the promise that a mechanic this large did
 *      not quietly move every existing save.
 *
 *   2. THE TWO HALVES ADD UP. What leaves the band and what the railway bills
 *      come to exactly the blended rate the shipper should pay, for any share
 *      and any quote. This is the one piece of arithmetic in the whole design
 *      that has to be right, and it is two lines of algebra that are easy to
 *      get backwards - the first draft of this model had the exporter paying
 *      the freight twice.
 *
 *   3. THE MONEY IS CONSERVED. Every dollar of haulage on the railway's
 *      revenue is a dollar on some shipper's input line, in the same month,
 *      because the invoice and the charge are one call.
 *
 *   4. THE PRICE IS A PRICE. An over-built network quotes near its floor and a
 *      starved one quotes near the lorries, and in between it covers what it
 *      costs to run plus a return on the track. A haulage rate that did not
 *      respond to either would make this a subsidy rather than a sector.
 *
 * See claude/transport-and-the-freight-band.md and sectors.Rail.
 */
public class RailCheck {

    static int fails = 0;

    static void quietly(Runnable r) {
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
            @Override public void write(int b) { }
        }));
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

    /** Bitwise, not near. See TradeCostCheck for why this harness family compares this way. */
    static boolean same(double a, double b) {
        return Double.compare(a, b) == 0 || (Double.isNaN(a) && Double.isNaN(b));
    }

    static boolean near(double a, double b, double tol) {
        return Math.abs(a - b) <= tol * Math.max(1, Math.abs(b));
    }

    public static void main(String[] args) {

        /* ================================================================
           1. A CITY WITH NO RAILWAY
           ================================================================ */

        System.out.println("--- a city with no railway quotes what it always quoted ---");

        Markets rest = new Markets();
        rest.setExchangeRate(1.0);
        int checked = 0;
        for (Good g : Good.values()) {
            GoodsMarket m = rest.get(g);
            boolean ok = same(m.importPrice(), g.importable() ? g.worldImportPrice() : Double.NaN)
                    && same(m.exportPrice(), g.exportable() ? g.worldExportPrice() : Double.NaN)
                    && same(m.netImportPrice(), m.importPrice())
                    && same(m.netExportPrice(), m.exportPrice());
            if (!ok) report(g.name() + ": four prices, untouched", false,
                    String.format("band %s-%s net %s-%s", m.exportPrice(), m.importPrice(),
                            m.netExportPrice(), m.netImportPrice()));
            checked++;
        }
        report("every good's band and net price is the literal, to the bit", fails == 0,
                checked + " goods");

        /*
         * AND THE SAME AT A RATE THAT IS NOT ONE, because the delta is added
         * BEFORE the multiply and a zero added before a multiply is only exact
         * if it is exactly zero.
         */
        Markets rested = new Markets();
        rested.setExchangeRate(0.8137);
        boolean scaled = true;
        for (Good g : Good.values()) {
            GoodsMarket m = rested.get(g);
            if (!g.importable()) continue;
            scaled &= same(m.importPrice(), g.worldImportPrice() * 0.8137);
            scaled &= same(m.netImportPrice(), m.importPrice());
        }
        assertTrue("...and at an exchange rate that is not one, still to the bit", scaled);

        /* ================================================================
           2. THE TWO HALVES ADD UP
           ================================================================ */

        System.out.println("\n--- what leaves the band plus what the railway bills ---");

        /*
         * THE WHOLE DESIGN IN ONE IDENTITY. The band keeps the lorries' share
         * (1 - s) and the railway bills s x q, so the shipper's total freight
         * is base x (1 - s(1 - q)) - which is the blended rate of a fleet that
         * is s rail at q and (1 - s) lorry at 1. Checked over the corners and
         * the middle of both dials, on a good with real freight in it.
         */
        Good steel = Good.STEEL;
        double base = steel.baseFreight();
        double fx = 0.9137;
        boolean blends = true;
        StringBuilder worst = new StringBuilder();
        for (double s : new double[] { 0, .17, .5, .83, 1 }) {
            for (double q : new double[] { Rail.RAIL_FLOOR, .45, .6, .88, 1 }) {
                Markets mk = new Markets();
                mk.setExchangeRate(fx);
                GoodsMarket m = mk.get(steel);
                m.setFreightFactor(1 - s);
                m.setRailCharge(s * q);

                // What the exporter is left with, and what it should be.
                double paid = (steel.worldSellPrice() * fx) - m.netExportPrice();
                double want = base * (1 - s * (1 - q)) * fx;
                if (!near(paid, want, 1e-12)) {
                    blends = false;
                    worst.setLength(0);
                    worst.append(String.format("s=%.2f q=%.2f paid %.8f want %.8f", s, q, paid, want));
                }
                // ...and the importer, from the other end.
                double landed = m.netImportPrice() - (steel.worldBuyPrice() * fx);
                if (!near(landed, want, 1e-12)) {
                    blends = false;
                    worst.setLength(0);
                    worst.append(String.format("IMPORT s=%.2f q=%.2f paid %.8f want %.8f",
                            s, q, landed, want));
                }
            }
        }
        report("the shipper pays the blended rate, both directions, 25 pairs",
                blends, blends ? "exact" : worst.toString());

        Markets full = new Markets();
        full.setExchangeRate(1);
        GoodsMarket fm = full.get(steel);
        fm.setFreightFactor(0);
        fm.setRailCharge(1.0);
        report("a railway carrying everything at the lorry rate changes nothing",
                near(fm.netExportPrice(), steel.worldExportPrice(), 1e-12)
                        && near(fm.netImportPrice(), steel.worldImportPrice(), 1e-12),
                String.format("%.6f-%.6f against %.6f-%.6f", fm.netExportPrice(),
                        fm.netImportPrice(), steel.worldExportPrice(), steel.worldImportPrice()));

        fm.setRailCharge(Rail.RAIL_FLOOR);
        double wedgeNow = fm.netImportPrice() - fm.netExportPrice();
        double wedgeWas = steel.worldImportPrice() - steel.worldExportPrice();
        report("...and at its floor the wedge is the world's margin plus a real freight",
                wedgeNow < wedgeWas * .55 && wedgeNow > wedgeWas * .25,
                String.format("%.1f%% of the old wedge, %.1f%% of the import price",
                        wedgeNow / wedgeWas * 100, wedgeNow / steel.worldImportPrice() * 100));

        /* ================================================================
           3. THE LAND, THE ROAD AND THE CATALOGUE
           ================================================================ */

        System.out.println("\n--- a terminal is trucks, not an office ---");

        GameFiles files = GameFiles.scratch("railcheck");
        Game game = new Game(files);
        BuildingManager bm = game.getBuildingManager();
        if (bm.getTemplates().isEmpty()) bm.initializeTemplates();

        int rails = 0;
        boolean sums = true, bulky = true;
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (t.getRailCapacity() <= 0) continue;
            rails++;
            double sum = t.loadOf(Traffic.COMMUTERS) + t.loadOf(Traffic.GOODS)
                       + t.loadOf(Traffic.BULK);
            sums &= same(sum, t.getRoadLoad());
            bulky &= t.loadOf(Traffic.BULK) > t.getRoadLoad() * .6;
        }
        report("the catalogue has rail in it", rails == 3, rails + " templates");
        assertTrue("...whose three loads still add to the one, to the bit", sums);
        report("...and whose road load is mostly the drayage, not the staff", bulky,
                String.format("%.0f%% bulk on a spur",
                        bm.getTemplateByName("Rail Spur").loadOf(Traffic.BULK)
                                / bm.getTemplateByName("Rail Spur").getRoadLoad() * 100));

        /*
         * THE LADDER. Bigger is cheaper per tonne and always has to be, or the
         * top rung is a trap the player pays more for.
         */
        double perTonneSpur = bm.getTemplateByName("Rail Spur").getCashCost()
                / bm.getTemplateByName("Rail Spur").getRailCapacity();
        double perTonneTerm = bm.getTemplateByName("Rail Terminal").getCashCost()
                / bm.getTemplateByName("Rail Terminal").getRailCapacity();
        report("the ladder is cheaper per tonne the further up it you go",
                perTonneTerm < perTonneSpur,
                String.format("$%.0f a tonne on a spur, $%.0f on a terminal",
                        perTonneSpur * 1000, perTonneTerm * 1000));

        System.out.println("\n--- and the road it takes the ore off ---");

        InfrastructureManager roads = new InfrastructureManager();
        roads.setBuiltCapacity(10000);
        roads.setLoad(12000);
        roads.setBreakdown(new double[] { 8600, 400, 3000 });
        roads.setModes(0, 0);

        double bulkBefore = roads.roadCostOf(Traffic.BULK);
        double loadBefore = roads.getEffectiveLoad();
        roads.setRailShare(new double[] { 0, 1, 1 });
        report("a fully railed city still carries its freight's last mile",
                roads.roadCostOf(Traffic.BULK) > .2 && roads.roadCostOf(Traffic.BULK) < .3,
                String.format("%.0f%% of the road it used to cost, from %.0f%%",
                        roads.roadCostOf(Traffic.BULK) * 100, bulkBefore * 100));
        report("...and its commuters are not on the train at all",
                same(roads.roadCostOf(Traffic.COMMUTERS), 1),
                String.format("%.4f", roads.roadCostOf(Traffic.COMMUTERS)));
        report("...so the network is relieved, and nowhere near halved",
                roads.getEffectiveLoad() < loadBefore
                        && roads.getEffectiveLoad() > loadBefore * .7,
                String.format("%.0f of %.0f", roads.getEffectiveLoad(), loadBefore));

        roads.setRailShare(new double[] { 0, 0, 0 });
        report("...and a city with no railway is back where it started, to the bit",
                same(roads.getEffectiveLoad(), loadBefore)
                        && same(roads.roadCostOf(Traffic.BULK), bulkBefore),
                String.format("%s vs %s", roads.getEffectiveLoad(), loadBefore));

        /* ================================================================
           4. THE SECTOR, IN A CITY
           ================================================================ */

        System.out.println("\n--- and the business, in a city that trades ---");

        quietly(() -> {
            game.newGame();
            /*
             * THE RAILWAY IS HELD, so what stands is what this harness laid and
             * nothing else. The first version let the investor build too, and
             * the comparison below - a sized network against an over-built one -
             * silently became "two spurs against three" the day a thirteenth
             * sector shifted the fixture city. An assertion whose subject is
             * decided by a planner is an assertion about the planner.
             */
            game.getBusinessInvestment().holdSector(Sectors.RAIL);
            game.getLandManager().setOwnedSqFt(400_000_000L);
            BuildingManager b = game.getBuildingManager();
            game.buildStack(b.getTemplateByName("House"), 900, true);
            game.buildStack(b.getTemplateByName("Convenience Store"), 20, true);
            game.buildStack(b.getTemplateByName("Small Grocery Store"), 6, true);
            game.buildStack(b.getTemplateByName("Paved Road"), 60, true);
            game.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            game.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            game.buildStack(b.getTemplateByName("Construction Depot"), 4, true);
            game.buildStack(b.getTemplateByName("Steel Foundry"), 30, true);
            for (int i = 0; i < 36; i++) game.toggleNextMonth();
        });

        Rail rail = game.getSectors().rail();
        BuildingManager live = game.getBuildingManager();

        report("a city that trades steel has freight to move",
                rail.getTradeTonnes() > 10_000,
                String.format("%,.0f tonnes a month, worth %,.0fk by lorry",
                        rail.getTradeTonnes(), rail.getTruckBill()));
        report("...and the band agrees with what the railway says it is carrying",
                same(game.getMarkets().get(Good.STEEL).getFreightFactor(),
                     1 - rail.getCarried()[Traffic.BULK.ordinal()]),
                String.format("%,.0f tonnes of track on %d line(s)", rail.getCapacityTonnes(),
                        live.countByName("Rail Spur") + live.countByName("Freight Line")
                                + live.countByName("Rail Terminal")));

        /*
         * THE INVOICE AND THE CHARGE ARE ONE CALL, so they cannot disagree.
         * Track is handed to the sector directly rather than waited for, so
         * what is being measured is the billing and not the planner.
         */
        /*
         * LONG ENOUGH FOR THE FLEET TO BE BOUGHT AND THE BOOKS TO SETTLE. A
         * railway pays for its locomotives in the month it takes delivery, so a
         * short window measures a capital purchase rather than a business - the
         * first draft ran ten months and read the spur's whole fleet as a loss.
         */
        /*
         * TRACK WITH NO LOCOMOTIVES CARRIES NOTHING, which is the whole of step
         * 5b in one assertion. The month a spur opens the railway owns no
         * rolling stock, so the network is laid and idle; it takes delivery at
         * the end of that month and runs from the next one.
         */
        quietly(() -> {
            lay(game, "Rail Spur", 2);
            game.toggleNextMonth();
        });
        report("track laid and no trains bought yet carries nothing",
                rail.trackTonnes() > 0 && rail.getCapacityTonnes() == 0,
                String.format("%,.0f tonnes of track, %,.1f wagon sets, %,.0f tonnes carried",
                        rail.trackTonnes(), rail.fleet(), rail.getCapacityTonnes()));
        quietly(game::toggleNextMonth);
        report("...and the month after it has the trains, it carries",
                rail.getCapacityTonnes() > 0,
                String.format("%,.1f wagon sets now carry %,.0f tonnes",
                        rail.fleet(), rail.getCapacityTonnes()));

        quietly(() -> { for (int i = 0; i < 28; i++) game.toggleNextMonth(); });

        double billed = rail.statement().otherRevenue;
        double paidOut = 0;
        for (Sector s : game.getSectors().all()) {
            if (s == rail) continue;
            paidOut += s.otherInputParts().getOrDefault("Haulage", 0.0);
        }
        report("every dollar the railway billed is on a shipper's cost line",
                billed > 0 && near(billed, paidOut, 1e-9),
                String.format("billed %,.2fk, charged %,.2fk", billed, paidOut));

        report("...and the shippers' opened cost line still adds to the closed one",
                allInputsAddUp(game),
                "every sector");

        /*
         * TWO THINGS IT BUYS FROM ABROAD, and between them they are the whole
         * of its import line. The fuel is a running cost; the locomotives are
         * capital, and a city with no Locomotive Works buys them from the world
         * like anything else. Both are on the trade balance, which is the point
         * of putting them through the front door rather than calling them
         * notional costs.
         */
        double fuelBill = rail.otherInputParts().getOrDefault("Fuel", 0.0);
        double trainBill = rail.statement().bought
                .getOrDefault(Good.ROLLING_STOCK, new Sector.Split()).abroad;
        report("the railway's fuel and its locomotives are both imports",
                fuelBill > 0 && trainBill > 0
                        && near(rail.statement().imports, fuelBill + trainBill, 1e-9),
                String.format("fuel %,.0fk + trains %,.0fk = %,.0fk on the trade balance",
                        fuelBill, trainBill, rail.statement().imports));

        report("...and it bought the fleet its track needs",
                rail.fleet() > 0 && near(rail.fleet(), rail.setsNeeded(), .02),
                String.format("%,.1f wagon sets against the %,.1f %,.0f tonnes of track wants",
                        rail.fleet(), rail.setsNeeded(), rail.trackTonnes()));

        report("it is carrying what it built to carry",
                rail.getHauledTonnes() > 0
                        && rail.getHauledTonnes() <= rail.getTradeTonnes() + 1,
                String.format("%,.0f of %,.0f tonnes, at %.0f%% of the lorry rate",
                        rail.getHauledTonnes(), rail.getTradeTonnes(), rail.getQuote() * 100));

        report("...and the band moved for it",
                game.getMarkets().get(Good.STEEL).getFreightFactor() < 1,
                String.format("%.0f%% of the lorry freight still in the band",
                        game.getMarkets().get(Good.STEEL).getFreightFactor() * 100));

        /*
         * THE QUOTE IS A PRICE, AND IT PRICES THE TRACK.
         *
         * A railway sized to its city charges about what the rule says it is
         * allowed: what the month cost to run, plus a return on the sunk track.
         * One with four times the track it needs is allowed twice as much and
         * CANNOT CHARGE IT - nobody pays more than a lorry - so it bills what
         * it can, pins at the cap, and earns less on more capital.
         *
         * THIS IS THE MECHANIC AND NOT AN EDGE CASE. Over-building a railway
         * does not make freight cheaper; it makes a railway that cannot pay for
         * itself. See Rail.TARGET_RETURN for the four-thousand-month run that
         * chose this rule over a shop's cost-plus.
         *
         * A NOTE ON THE LEVELS HERE. This fixture is a steel town, and a tonne
         * of steel is the cheapest thing in the catalogue to move - so its
         * lorry rate is half a played city's and the quote sits high. The
         * played city, whose exports are fabricated steel at three times the
         * freight, settles at 38-43%. Both are the same rule; nothing below
         * asserts a level, only what the rule says about itself.
         */
        System.out.println("\n--- and the quote prices the track ---");

        double sizedQuote = rail.getQuote();
        double sizedBilled = rail.getHaulageBilled();
        double sizedAllowed = rail.getAllowedRevenue();
        double sizedNet = rail.statement().netIncome;
        double sizedCapital = rail.getBuildingsValue() + rail.getLandValue();

        report("a railway sized to its city bills about what the rule allows it",
                sizedAllowed > 0 && near(sizedBilled, sizedAllowed, .25),
                String.format("billed %,.0fk against %,.0fk allowed, at %.0f%% of the lorry rate",
                        sizedBilled, sizedAllowed, sizedQuote * 100));
        report("...and is inside its own bounds while it does",
                sizedQuote > Rail.RAIL_FLOOR && sizedQuote <= 1 + 1e-12,
                String.format("%.1f%%, floor %.0f%%", sizedQuote * 100, Rail.RAIL_FLOOR * 100));

        quietly(() -> {
            lay(game, "Rail Spur", 6);
            for (int i = 0; i < 40; i++) game.toggleNextMonth();
        });

        report("four times the track it needs, and it cannot charge for it",
                rail.getHaulageBilled() < rail.getAllowedRevenue() * .75,
                String.format("billed %,.0fk against %,.0fk allowed, on %,.0f tonnes of track"
                        + " for %,.0f tonnes of freight",
                        rail.getHaulageBilled(), rail.getAllowedRevenue(),
                        rail.getCapacityTonnes(), rail.getTradeTonnes()));
        report("...so it pins at the lorries' price, which is its ceiling",
                rail.getQuote() > sizedQuote && rail.getQuote() <= 1 + 1e-12,
                String.format("%.1f%% against %.1f%%", rail.getQuote() * 100, sizedQuote * 100));
        report("...and earns LESS on four times the capital",
                rail.statement().netIncome < sizedNet,
                String.format("%,.0fk on %,.0fk of track against %,.0fk on %,.0fk",
                        rail.statement().netIncome,
                        rail.getBuildingsValue() + rail.getLandValue(), sizedNet, sizedCapital));
        report("...but never quotes below its floor, because freight needs profit",
                rail.getQuote() >= Rail.RAIL_FLOOR - 1e-12,
                String.format("%.1f%% against a floor of %.0f%%",
                        rail.getQuote() * 100, Rail.RAIL_FLOOR * 100));

        /* ================================================================
           5. AND IT SURVIVES A RELOAD
           ================================================================ */

        System.out.println("\n--- and what it was charging survives a reload ---");

        double quoteWas = rail.getQuote();
        double[] carriedWas = rail.getCarried();
        quietly(() -> game.saveGame(10));
        Game reloaded = new Game(files);
        quietly(() -> { reloaded.run(); reloaded.loadGame(10); });
        Rail back = reloaded.getSectors().rail();
        report("the quote came back",
                same(back.getQuote(), quoteWas),
                String.format("%.6f against %.6f", back.getQuote(), quoteWas));
        boolean shares = true;
        for (Traffic t : Traffic.values()) {
            shares &= same(back.getCarried()[t.ordinal()], carriedWas[t.ordinal()]);
        }
        assertTrue("...and so did what it was carrying, stream by stream", shares);
        report("...and its locomotives came back with it",
                same(back.fleet(), rail.fleet()) && back.fleet() > 0,
                String.format("%,.4f wagon sets against %,.4f", back.fleet(), rail.fleet()));
        report("...and the band the reloaded city quotes is the one it was saved with",
                same(reloaded.getMarkets().get(Good.STEEL).getFreightFactor(),
                     game.getMarkets().get(Good.STEEL).getFreightFactor()),
                String.format("%.6f", reloaded.getMarkets().get(Good.STEEL).getFreightFactor()));

        if (fails > 0) {
            System.out.printf("%n%d check(s) failed%n", fails);
            System.exit(1);
        }
        System.out.println("\nAll checks passed.");
    }

    /**
     * Track, handed to the city rather than waited for.
     *
     * A Rail Terminal is $1.24bn and buildStack() charges the TREASURY for it,
     * so a fixture that just asks for four of them is refused for funding and
     * the harness then measures a city with no track while reporting a
     * different one. The first draft did exactly that and said "four times the
     * track it needs" over fifty thousand tonnes of spur.
     */
    static void lay(Game game, String name, int count) {
        BuildingsTemplate t = game.getBuildingManager().getTemplateByName(name);
        game.getBuildingManager().addStack(t, count, true);
    }

    /**
     * The goods bought, plus the services named, come to the input line - for
     * every sector, every month. The rule the opened cost line lives or dies
     * by; see Sector.Ledger.otherInputs.
     */
    static boolean allInputsAddUp(Game game) {
        boolean ok = true;
        for (Sector s : game.getSectors().all()) {
            Sector.Statement st = s.statement();
            double goods = 0;
            for (Sector.Split x : st.bought.values()) goods += x.total();
            double named = 0;
            for (double v : st.otherInputs.values()) named += v;
            if (Math.abs(goods + named - st.inputs) > 1e-6 * Math.max(1, st.inputs)) {
                System.out.printf("   %s: goods %,.2f + named %,.2f != inputs %,.2f%n",
                        s.key(), goods, named, st.inputs);
                ok = false;
            }
        }
        return ok;
    }
}
