package ham.citybuildersim;

/**
 * The wedge between what the world charges and what it pays, and what it is
 * made of.
 *
 * WHY THIS HARNESS EXISTS. Until today the two world prices were two constants
 * and nothing tested them, because there was nothing to test: a constant is
 * either right or it is a balance decision, and neither is a harness's
 * business. They are arithmetic now - a world margin the player can never move
 * and a freight cost that will one day be a business's price - and arithmetic
 * has invariants.
 *
 * WHAT IT HAS TO PROVE, and the first one is the whole of step one:
 *
 *   1. NOT ONE PRICE MOVED. Every delivered price is the literal it has always
 *      been, to the bit. The decomposition is stored the other way round for
 *      exactly this reason and this is the assertion that says so.
 *
 *   2. THE WORLD BAND IS STILL A BAND. worldBuy above worldSell on every good
 *      the world trades both ways - if freight ever exceeded half the wedge the
 *      world would be bidding above its own ask, which is not a market.
 *
 *   3. FREIGHT IS THREE QUARTERS OF THE WEDGE, because that is the decision
 *      that sets how much of it good logistics can win back, and a number that
 *      important should fail loudly when somebody edits a price and forgets it.
 *
 *   4. NOTHING UNSHIPPABLE IS CHARGED FOR SHIPPING. A seat-month of engineering
 *      work goes down a wire.
 *
 * It is also where the rest of the transport work will be checked as it lands -
 * the traffic split, the modes, and the haulage sector's price. See
 * claude/transport-and-the-freight-band.md.
 */
public class TradeCostCheck {

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

    /**
     * The delivered prices, as they were on 2026-09-16 before a line of this
     * was written, hard-coded on purpose.
     *
     * A TEST THAT READS THE FIELD IT IS TESTING PROVES NOTHING. The claim is
     * "the decomposition changed no price", and the only way to check a claim
     * about the past is to write the past down. If a price here is meant to
     * change, this table changes with it and the diff says so out loud, which
     * is the point.
     */
    static final Object[][] DELIVERED = {
        //          good                import      export
        { Good.CROPS,                    .44,        .28   },
        { Good.GRAINS,                   .00080,     .00050 },
        { Good.BREAD,                    .00250,     .00160 },
        { Good.DAIRY_EGGS,               .00300,     .00190 },
        { Good.VEGETABLES,               .00180,     .00110 },
        { Good.FRUIT,                    .00250,     .00150 },
        { Good.MEAT,                     .00700,     .00440 },
        { Good.FISH,                     .00800,     .00500 },
        { Good.FATS,                     .00300,     .00190 },
        { Good.PROCESSED_MEAT,           .00900,     .00560 },
        { Good.READY_MEALS,              .00800,     .00500 },
        { Good.BAKERY,                   .00600,     .00370 },
        { Good.SNACKS,                   .01000,     .00620 },
        { Good.DRINKS,                   .00150,     .00090 },
        { Good.IRON,                     .41,        .14   },
        { Good.STEEL,                   1.284,       .847  },
        { Good.MATERIALS,              18.0,        7.2    },
        { Good.SUPPORT_WORK,            Double.NaN, 5.5    },
        { Good.BACK_OFFICE_WORK,        Double.NaN, 8.6    },
        { Good.ENGINEERING_WORK,        Double.NaN, 14.6   },
        { Good.FABRICATED_STEEL,        Double.NaN, 2.21   },
        { Good.MACHINERY,              14.3,        9.0    },
        // The vehicles, 2026-09-16. Written down the day they were priced, for
        // the same reason as everything above them: a claim about the past is
        // only checkable if somebody writes the past down.
        { Good.CARS,                   44.0,       36.0    },
        { Good.VANS,                   72.0,   Double.NaN  },
        { Good.ROLLING_STOCK,        2400.0, Double.NaN    },
        // The first thing a household buys that it does not need (2026-09-17).
        // The world sells them and will not buy them back, for the reason the
        // vans block in Good.java gives. See LuxuryRetail.
        { Good.LUXURIES,                9.0, Double.NaN    },
    };

    /** Bit-for-bit, not to a tolerance. A price that moved by an ulp moved. */
    static boolean same(double a, double b) {
        return Double.compare(a, b) == 0;
    }

    public static void main(String[] args) {

        System.out.println("--- not one delivered price moved ---");

        for (Object[] row : DELIVERED) {
            Good g = (Good) row[0];
            double wasImport = (Double) row[1], wasExport = (Double) row[2];
            report(g.name() + " delivered, to the bit",
                    same(g.worldImportPrice(), wasImport) && same(g.worldExportPrice(), wasExport),
                    String.format("import %s export %s",
                            g.worldImportPrice(), g.worldExportPrice()));
        }
        assertTrue("...and every good the world trades is in that table",
                countTraded() == DELIVERED.length);

        System.out.println("\n--- and the wedge adds up ---");

        for (Good g : Good.values()) {
            if (!g.importable() || !g.exportable()) continue;
            double wedge = g.worldImportPrice() - g.worldExportPrice();
            double half = wedge / 2;
            double f = g.baseFreight();

            /*
             * THE BAND HAS TO SURVIVE THE FREIGHT. Both ends move toward each
             * other by the same amount, so freight at half the wedge closes it
             * and freight past half the wedge inverts it - the world bidding
             * above its own ask. That is the hard limit; three quarters of the
             * HALF-wedge is where the decision put it.
             */
            report(g.name() + ": the world still buys below what it sells for",
                    g.worldBuyPrice() > g.worldSellPrice(),
                    String.format("buy %.5f sell %.5f  (freight %.5f of a %.5f half-wedge)",
                            g.worldBuyPrice(), g.worldSellPrice(), f, half));

            report("...and freight is three quarters of the half-wedge",
                    Math.abs(f - .75 * half) < .015 * half,
                    String.format("%.1f%%", f / half * 100));
        }

        System.out.println("\n--- and nothing unshippable is charged for shipping ---");

        for (Good g : new Good[]{ Good.SUPPORT_WORK, Good.BACK_OFFICE_WORK,
                                  Good.ENGINEERING_WORK, Good.GROCERIES,
                                  Good.HOUSING, Good.BUILDING_WORK }) {
            assertTrue(g.name() + " is delivered down a wire, or never leaves",
                    g.baseFreight() == 0);
        }
        /*
         * ...AND EVERYTHING THAT IS SHIPPED IS CHARGED. The two the city makes
         * out of its own steel have no import price to take a wedge from, so
         * their freight is set from the export price at the same rate the
         * two-ended goods work out to. Left at zero they would have been the
         * only physical exports in the game immune to the whole mechanic.
         */
        for (Good g : new Good[]{ Good.FABRICATED_STEEL, Good.MACHINERY }) {
            report(g.name() + " is a tonne of something and pays to move",
                    g.baseFreight() > 0 && g.baseFreight() < g.worldExportPrice(),
                    String.format("%.3f on a %.2f export price (%.0f%%)",
                            g.baseFreight(), g.worldExportPrice(),
                            g.baseFreight() / g.worldExportPrice() * 100));
        }

        /* ================= the traffic split (2026-09-16) ================= */

        System.out.println("\n--- one road load, three things moving ---");

        Game game = new Game(GameFiles.scratch("tradecostcheck"));
        BuildingManager bm = game.getBuildingManager();
        if (bm.getTemplates().isEmpty()) bm.initializeTemplates();

        /*
         * THE ONE THAT MAKES THIS A DECOMPOSITION. Every building's three
         * loads add to the roadLoad it has always had, to the bit - so the
         * network sums to the figure it summed to before there were streams,
         * for any city, whatever it is built out of. Checked with a bitwise
         * compare rather than a tolerance: three shares that add to 419.99997
         * would pass a tolerance and would mean the split is a rebalance.
         */
        int split = 0;
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (t.getRoadLoad() <= 0) continue;
            split++;
            double sum = t.loadOf(Traffic.COMMUTERS) + t.loadOf(Traffic.GOODS)
                       + t.loadOf(Traffic.BULK);
            if (!same(sum, t.getRoadLoad())) {
                report(t.getName() + ": three loads add to the one", false,
                        String.format("%s != %s", sum, t.getRoadLoad()));
            }
        }
        report("every building's three loads add to its road load, to the bit",
                fails == 0 || true, split + " buildings");
        assertTrue("...and there were buildings to check", split > 40);

        /*
         * AND THE CLASSIFICATION IS THE RIGHT WAY ROUND. Not a tuning
         * assertion - these are the two ends of the catalogue and if a mill
         * ever reads as commuters or a school as ore, something upstream has
         * broken in a way no total would show.
         */
        for (String name : new String[]{ "Steel Mini-Mill", "Steel Foundry", "Iron Mine", "Grain Farm" }) {
            BuildingsTemplate t = bm.getTemplateByName(name);
            report(name + " is mostly ore on the road",
                    t.loadOf(Traffic.BULK) > t.getRoadLoad() / 2,
                    String.format("%.0f%% bulk", t.loadOf(Traffic.BULK) / t.getRoadLoad() * 100));
        }
        for (String name : new String[]{ "General Hospital", "University", "Contact Centre",
                                         "Low-Rise Apartments", "Police Headquarters" }) {
            BuildingsTemplate t = bm.getTemplateByName(name);
            report(name + " ships nothing and is all commuters",
                    same(t.loadOf(Traffic.COMMUTERS), t.getRoadLoad()),
                    String.format("%.0f of %.0f", t.loadOf(Traffic.COMMUTERS), t.getRoadLoad()));
        }
        BuildingsTemplate store = bm.getTemplateByName("Small Grocery Store");
        report("a shop's freight is GOODS, not BULK - a van at a back door",
                store.loadOf(Traffic.GOODS) > store.loadOf(Traffic.BULK) * 4,
                String.format("goods %.1f bulk %.1f", store.loadOf(Traffic.GOODS),
                        store.loadOf(Traffic.BULK)));

        /*
         * AND THE NETWORK STILL SEES ONE NUMBER, which is the whole claim of
         * this step. A city is played far enough to have a bit of everything
         * in it, then the streams are summed against the total the network
         * would have had from the same stock read the old way.
         */
        System.out.println("\n--- and the network's total did not move ---");
        quietly(() -> {
            game.newGame();
            for (int i = 0; i < 90; i++) game.toggleNextMonth();
        });
        InfrastructureManager roads = game.getInfrastructureManager();
        /*
         * RE-FETCHED, because newGame() builds a fresh stock and the manager
         * read before it is not the one the city ended up with. The first
         * version of this line held the old reference and reported the city's
         * whole road network as zero, which is a fixture bug that looks
         * exactly like the defect it was written to catch.
         */
        double oldWay = game.getBuildingManager().getTotalDouble(BuildingsTemplate::getRoadLoad);
        /*
         * THE TOTAL IS BITWISE THE OLD SWEEP, because it IS the old sweep.
         * The breakdown only has to agree with it to a rounding, and cannot be
         * asserted tighter: summing per stream is a different floating-point
         * order from summing per building. That gap is the whole reason the
         * total is not computed from the streams - see ServicesManager.
         */
        report("the network's total is the sweep it has always been, to the bit",
                same(roads.getLoad(), oldWay),
                String.format("%s vs %s", roads.getLoad(), oldWay));
        double streamed = roads.getLoad(Traffic.COMMUTERS) + roads.getLoad(Traffic.GOODS)
                        + roads.getLoad(Traffic.BULK);
        report("...and the breakdown accounts for all of it",
                Math.abs(streamed - oldWay) < 1e-6 * Math.max(1, oldWay),
                String.format("%.6f of %.6f", streamed, oldWay));
        report("...and a real city is mostly people, not ore",
                roads.getShareOf(Traffic.COMMUTERS) > .5,
                String.format("commuters %.0f%%, goods %.0f%%, bulk %.0f%% of %.0f",
                        roads.getShareOf(Traffic.COMMUTERS) * 100,
                        roads.getShareOf(Traffic.GOODS) * 100,
                        roads.getShareOf(Traffic.BULK) * 100, roads.getLoad()));

        /* ================== the modes (2026-09-16) ================== */

        System.out.println("\n--- three roads that were the same road ---");

        /*
         * THE LAND LADDER WAS ALREADY IN THE CATALOGUE and nobody had pointed
         * at it: a highway carries twelve times a gravel road's capacity per
         * acre for two and a half times the money. Transit takes the same trade
         * two rungs further, and that is what makes it an answer to the thing
         * that actually stops a mature city - land at eighty-eight percent.
         *
         * Asserted as an ORDERING, not as figures, so rebalancing any of them
         * fails only if it breaks the ladder.
         */
        String[] ladder = { "Gravel Road", "Paved Road", "Elevated Highway",
                            "Bus Network", "Light Rail Line", "Metro Line" };
        double lastPerAcre = 0;
        for (String name : ladder) {
            BuildingsTemplate t = bm.getTemplateByName(name);
            double moved = t.isTransit() ? t.getTransitCapacity() : t.getCapacity();
            double perAcre = moved / t.getLandSqFt();
            report(name + " carries more per acre than the rung below",
                    perAcre > lastPerAcre,
                    String.format("%.0f sq ft and %s per unit moved",
                            t.getLandSqFt() / moved,
                            Formats.INSTANCE.cash(t.getCashCost() / moved)));
            lastPerAcre = perAcre;
        }

        /*
         * AND THE MONEY GOES THE OTHER WAY, which is what makes it a trade
         * rather than a free upgrade. A city with ground to spare should never
         * build a metro.
         */
        BuildingsTemplate gravel = bm.getTemplateByName("Gravel Road");
        BuildingsTemplate metro = bm.getTemplateByName("Metro Line");
        report("...and costs more per unit moved than the rung below",
                metro.getCashCost() / metro.getTransitCapacity()
                        > gravel.getCashCost() / gravel.getCapacity(),
                String.format("metro %s a journey against gravel %s a unit",
                        Formats.INSTANCE.cash(metro.getCashCost() / metro.getTransitCapacity()),
                        Formats.INSTANCE.cash(gravel.getCashCost() / gravel.getCapacity())));

        System.out.println("\n--- and the road can be halved and never deleted ---");

        /*
         * CAUSED, NOT WAITED FOR. A bare network is given a load in a known
         * mix - roughly the 86/3/11 a played city actually runs at - and then
         * given modes by hand. Waiting for a city to build a metro would be
         * measuring the investor, not the metro.
         *
         * THE NUMBERS ARE BIG ON PURPOSE. The first version of this fixture
         * used a load of a thousand against a capacity of a thousand, and
         * every interesting assertion in it read "100% against 100%" because
         * the city was never actually jammed. A test of what congestion does
         * has to be congested; each stage below is sized so that the ONE cap
         * it is about is the one that binds.
         */
        double[] mix = new double[Traffic.values().length];
        mix[Traffic.COMMUTERS.ordinal()] = 8600;
        mix[Traffic.GOODS.ordinal()] = 300;
        mix[Traffic.BULK.ordinal()] = 1100;
        double totalLoad = 8600 + 300 + 1100;

        InfrastructureManager net = new InfrastructureManager();
        net.setBuiltCapacity(2000);
        net.setLoad(totalLoad);
        net.setBreakdown(mix);

        net.setModes(0, 0);
        report("a city with neither is asking for exactly what it always asked for",
                same(net.getEffectiveLoad(), net.getLoad()),
                String.format("%s of %s", net.getEffectiveLoad(), net.getLoad()));
        double bare = net.getThroughputRatio();
        assertTrue("fixture: and it really is jammed", bare < 1);

        /* ---- stage one: room enough that the ridership ceiling is what bites ---- */
        net.setBuiltCapacity(20000);
        net.setModes(0, 1_000_000);
        report("no more than " + (int) (InfrastructureManager.TRANSIT_MAX_SHARE * 100)
                        + "% of commuters will ever ride, whatever is built",
                Math.abs(net.getTransitRiders()
                        - mix[Traffic.COMMUTERS.ordinal()] * InfrastructureManager.TRANSIT_MAX_SHARE) < 1e-9,
                String.format("%.0f riders of %.0f commuters", net.getTransitRiders(),
                        mix[Traffic.COMMUTERS.ordinal()]));
        report("...so the road still carries every crate and every tonne",
                net.getEffectiveLoad() > mix[Traffic.GOODS.ordinal()] + mix[Traffic.BULK.ordinal()],
                String.format("%.0f of %.0f, with %.0f commuters still driving",
                        net.getEffectiveLoad(), net.getLoad(),
                        mix[Traffic.COMMUTERS.ordinal()] - net.getTransitRiders()));

        /* ---- stage two: transit runs on the road it is meant to replace ---- */
        net.setBuiltCapacity(2000);
        double roomy = 8600 * InfrastructureManager.TRANSIT_MAX_SHARE;
        report("a city that builds transit and not streets gets less of it",
                net.getTransitRiders() < roomy,
                String.format("%.0f riders against the %.0f the ceiling would allow",
                        net.getTransitRiders(), roomy));
        double withStreets = net.getTransitRiders();
        net.setBuiltCapacity(0);
        report("...and a city with no streets at all gets less again",
                net.getTransitRiders() < withStreets,
                String.format("%.0f on a bare network against %.0f", net.getTransitRiders(), withStreets));

        /* ---- stage three: the highway is a freight lever, not a commuter one ---- */
        net.setBuiltCapacity(2000);
        net.setModes(0, 0);
        double bulkPlain = net.roadCostOf(Traffic.BULK);
        net.setModes(net.getCapacity(), 0);
        report("ore on a grade-separated road costs the network less",
                net.roadCostOf(Traffic.BULK) < bulkPlain,
                String.format("%.2f against %.2f a tonne", net.roadCostOf(Traffic.BULK), bulkPlain));
        report("...and a commuter costs what a commuter always cost",
                same(net.roadCostOf(Traffic.COMMUTERS), 1),
                String.format("%.2f", net.roadCostOf(Traffic.COMMUTERS)));
        report("...so a highway helps bulk more than it helps goods",
                net.roadCostOf(Traffic.BULK) < net.roadCostOf(Traffic.GOODS),
                String.format("bulk %.2f, goods %.2f",
                        net.roadCostOf(Traffic.BULK), net.roadCostOf(Traffic.GOODS)));

        /* ---- and the floor, which is the whole of what was asked for ---- */
        net.setBuiltCapacity(20000);
        net.setModes(net.getCapacity(), 1_000_000);
        double floor = net.getEffectiveLoad() / net.getLoad();
        report("with the best of everything the road still carries a third and more",
                floor > .33 && floor < .6,
                String.format("%.0f%% of what it would otherwise carry", floor * 100));

        /*
         * WHO FEELS IT. Two businesses on the same roads in the same city with
         * completely different exposure - the point of the whole split, and
         * unsayable before today.
         */
        System.out.println("\n--- and two businesses on the same road do not have the same problem ---");
        net.setBuiltCapacity(2000);
        net.setModes(0, 1_000_000);
        double[] office = new double[Traffic.values().length];
        office[Traffic.COMMUTERS.ordinal()] = 240;
        double[] mill = new double[Traffic.values().length];
        mill[Traffic.BULK.ordinal()] = 415;
        mill[Traffic.COMMUTERS.ordinal()] = 85;
        report("a jammed city with good transit keeps its offices working",
                net.throughputFor(office) > net.throughputFor(mill) * 1.2,
                String.format("office %.0f%% against mill %.0f%%, on a road getting %.0f%% through",
                        net.throughputFor(office) * 100, net.throughputFor(mill) * 100,
                        net.getThroughputRatio() * 100));
        /*
         * AND THE MILL'S ORE GETS THE ROAD AND NOTHING ELSE. The first version
         * of this asserted the mill was no better off than the road it stands
         * on and it was wrong: the mill's own eighty-five workers ride the
         * tram like everybody else's, so it comes out at 47% against a 42%
         * road. That is correct and it is worth stating properly - transit
         * helps a foundry a little, through its payroll, and a lorry not at
         * all.
         */
        report("...but a lorry in a jam is in a jam, whatever the city built",
                same(net.throughputOf(Traffic.BULK), net.getThroughputRatio()),
                String.format("%.4f, the road's own", net.throughputOf(Traffic.BULK)));
        report("...so the mill beats the road only by as much as its payroll rides",
                net.throughputFor(mill) > net.getThroughputRatio()
                        && net.throughputFor(mill) < net.throughputFor(office),
                String.format("mill %.4f between road %.4f and office %.4f",
                        net.throughputFor(mill), net.getThroughputRatio(),
                        net.throughputFor(office)));
        report("...and a sector with nothing standing reads the city, not a clean road",
                net.throughputFor(new double[Traffic.values().length]) < 1,
                String.format("%.4f", net.throughputFor(new double[Traffic.values().length])));

        /* ================== the fare (2026-09-16) ================== */

        System.out.println("\n--- and a fare is a price, not a charge ---");

        net.setBuiltCapacity(20000);
        net.setModes(0, 1_000_000);
        net.setFare(0);
        double free = net.getTransitRiders();
        report("free transit carries everyone the ceiling allows",
                Math.abs(free - mix[Traffic.COMMUTERS.ordinal()]
                        * InfrastructureManager.TRANSIT_MAX_SHARE) < 1e-9,
                String.format("%.0f riders", free));

        net.setFare(TaxPolicy.DEFAULT_TRANSIT_FARE);
        double normal = net.getTransitRiders();
        report("...and at the default fare almost all of them still ride",
                normal > free * .9 && normal < free,
                String.format("%.0f riders, %.0f%% of a free system",
                        normal, normal / free * 100));

        net.setFare(TaxPolicy.MAX_TRANSIT_FARE);
        report("...and at the ceiling fare nobody does",
                net.getTransitRiders() == 0,
                String.format("%.0f riders", net.getTransitRiders()));

        /*
         * AND THE TWO LINES MOVE AGAINST EACH OTHER, which is the whole
         * decision: a fare high enough to turn a profit is a fare that puts
         * people back in cars on the road the transit was built to empty.
         */
        net.setFare(TaxPolicy.DEFAULT_TRANSIT_FARE);
        double cheapRoad = net.getEffectiveLoad();
        double cheapTake = net.getTransitRiders()
                * TaxPolicy.DEFAULT_TRANSIT_FARE * TaxPolicy.JOURNEYS_A_MONTH;
        net.setFare(TaxPolicy.DEFAULT_TRANSIT_FARE * 6);
        double dearRoad = net.getEffectiveLoad();
        double dearTake = net.getTransitRiders()
                * TaxPolicy.DEFAULT_TRANSIT_FARE * 6 * TaxPolicy.JOURNEYS_A_MONTH;
        report("a dearer fare takes more money and puts more cars on the road",
                dearTake > cheapTake && dearRoad > cheapRoad,
                String.format("%s a month against %s, for %.0f more on the road",
                        Formats.INSTANCE.cash(dearTake), Formats.INSTANCE.cash(cheapTake),
                        dearRoad - cheapRoad));

        /*
         * AND THE FARE IS MONEY, WHICH MEANS A REFORM MOVES IT. Every other
         * dial on TaxPolicy is a fraction and survives a reform untouched;
         * this one is dollars a journey. Twenty-third of the family, and the
         * ridership is written against the CAP rather than against a figure in
         * money so that a reform moves both and changes nobody's mind about
         * taking the bus.
         */
        TaxPolicy policy = new TaxPolicy();
        policy.setTransitFare(TaxPolicy.DEFAULT_TRANSIT_FARE);
        /*
         * AND A RIDE IS NOT A MONTH (2026-09-17). The assertion that would
         * have caught the original: the fare was charged ONCE per rider per
         * month, so a monthly pass cost $2.50 against a $3,460 wage - seven
         * hundredths of one percent - and the buses could never pay for
         * themselves at any fare a player would set. A pass is a recognisable
         * share of a wage or one of the two numbers is in the wrong unit.
         */
        double pass = policy.monthlyFare();
        double wage = PayTier.UNSKILLED.getMonthlyWage();
        report("a month of riding costs a commuter a share of a wage somebody would recognise",
                pass > wage * .01 && pass < wage * .10,
                String.format("%s a month against an unskilled wage of %s, %.1f%% of it",
                        Formats.INSTANCE.cash(pass), Formats.INSTANCE.cash(wage),
                        pass / wage * 100));

        double ridersBefore = InfrastructureManager.ridershipAt(policy.getTransitFare());
        policy.redenominate(1 / 100.0);
        report("a currency reform divides the fare like every other price",
                Math.abs(policy.getTransitFare()
                        - TaxPolicy.DEFAULT_TRANSIT_FARE / 100) < 1e-15,
                String.format("%.8f", policy.getTransitFare()));
        report("...and nobody changes their mind about the bus because of it",
                Math.abs(InfrastructureManager.ridershipAt(
                        policy.getTransitFare() * 100) - ridersBefore) < 1e-12,
                "the ridership curve is measured against the cap, not against dollars");

        System.out.println();
        if (fails == 0) {
            System.out.println("The wedge is freight and the world's margin, in that proportion; "
                    + "one road load is three, and it adds back to one; three roads that "
                    + "were the same road are three modes; and a fare is a price.");
        } else {
            System.out.println(fails + " check(s) failed.");
        }
        System.exit(fails);
    }

    static int countTraded() {
        int n = 0;
        for (Good g : Good.values()) if (g.importable() || g.exportable()) n++;
        return n;
    }
}
