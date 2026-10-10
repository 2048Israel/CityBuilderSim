package ham.citybuildersim;

import ham.citybuildersim.sectors.Oil;
import ham.citybuildersim.sectors.Rail;
import ham.citybuildersim.sectors.RefineryFlow;
import ham.citybuildersim.sectors.Refining;
import ham.citybuildersim.sectors.Retail;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Fuel, from the oil in the ground to the drivers' tanks (0.7.62, batch K;
 * the project's spec-land.md 2.7 and section 3's K entry).
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE GOODS AND THE BUILDINGS ARE THE SPEC'S. Crude at US$550 a tonne,
 *      fuel at a journey's $2.00 over its 1.2 litres - so a city with no
 *      refinery pays what it always paid a journey - freight by Good's rule,
 *      oil in the ground at 5% of the crude's price, and two sectors on the
 *      end of the registry. SINCE 0.7.76 (batch O1; runs/spec-oil.md 2.1,
 *      2.3): FUEL is nine products - all at the research's wholesale ladder
 *      (petrol and diesel since 0.7.78, batch O2; at FUEL's band before),
 *      each litre weighing its good's tonne
 *      over its litres - and the refinery a crude unit whose products are
 *      THE SLATE of its crude: a tonne of medium crude into 70 L of petrol
 *      and 169 L of diesel, not a thousand litres of fuel, the litres
 *      balancing to the litre.
 *
 *   2. A WELL LIFTS ONLY OWNED OIL. No oil site, no well - the order, the
 *      card, the investors all refuse it; an offer with oil bought is one
 *      well a site; and what the wells lift is exactly what leaves the
 *      ground, the world's oil conserved to the tonne. A well has one post,
 *      a diploma's (0.7.84, batch O7; spec-oil 2.6, the research's Q13 -
 *      three until then); its decline, its life and its dry sites are
 *      WellCheck's.
 *
 *   3. THE OIL RUNS OUT, AND THE WELLS RETIRE, as the mines do when the ore
 *      does.
 *
 *   4. THE REFINERY TAKES THE WELLS' CRUDE FIRST and imports the rest.
 *
 *   5. THE DRIVERS AND THE RAILWAY TAKE THE REFINERS' FUEL FIRST, and import
 *      only what the tanks do not hold - the drivers' petrol and the
 *      railway's diesel since 0.7.76. The drivers' since 0.7.83 (batch O6)
 *      through the grocers' forecourts, which draw it at the refiners'
 *      price and sell it on at the pump's (sectors.Retail, THE FORECOURTS).
 *
 *   6. FUEL'S MONEY AUDIT CLOSES in every month of a city with wells and a
 *      refinery: the imported part is the households' PetrolImports (their
 *      FuelImports until 0.7.76) - since 0.7.83 the forecourts' import, on
 *      Retail's books, and the households' own nothing - the domestic part
 *      is on Refining's statement (a sale to the forecourts since 0.7.83,
 *      the drivers' bill Retail's sale to them), and the goods foot to the
 *      balance of payments - and the
 *      tanks, shared among the products, write none of them off: what nobody
 *      here buys fills its share to the dump line, then ships - since 0.7.98
 *      (batch O14) whatever its share of the crude: the run's every product
 *      leaves, nothing of it idled, in months when its share of the line's
 *      whole bill, the crude among it, would not have shipped it (until
 *      then it idled: on the wholesale ladder, 0.7.78, a medium crude's
 *      slate).
 *
 *   7. WITH NO REFINERY, THE HOUSEHOLDS PAY TODAY'S BILL AT THE WORLD'S PRICE
 *      LEVEL, as every good is priced - the railway's fuel always was. Since
 *      0.7.78 (batch O2) the bill is a journey's litres at petrol's place on
 *      the wholesale ladder, 63% under 0.7.49's journey at the pump price -
 *      the forecourts' wholesale bill since 0.7.83, the drivers paying the
 *      pump's price on it.
 *
 *   8. A REFINERY IS BUILT FOR THE CITY'S OWN FUEL OR ITS OWN CRUDE
 *      (Refining.plan(), the star the playtest measured: 120 export
 *      refineries without it), and pays for the crude it would have to
 *      import at the import price. Since 0.7.82 (batch O5, the spread
 *      planner; spec-oil 2.4's rule 6) a crude unit is weighed once the
 *      city's petrol and diesel short come to FEED_GATE (60%) of what it
 *      would make of them, or the wells' spare to FEED_GATE of its crude -
 *      a whole plant's worth until then - and as a package with the units
 *      its cuts would feed: its crude at the net import price, the wells'
 *      spare at the local price, and its estimate its share of the
 *      package's by cost.
 *
 *   9. THE MONTH'S FUEL CROSSES A SAVE: the bill, the imports and the litres
 *      as 6d drew them - and since 0.7.83 the forecourts' month - and a save
 *      from before them derives what that month struck - every litre
 *      imported.
 *
 *  10. A SAVE FROM BEFORE 0.7.76 HAS ITS FUEL SPLIT (FuelSplit, spec-oil 3):
 *      every FUEL figure into PETROL and DIESEL by the drivers' share of the
 *      month's litres, each pair summing to the figure to the bit, the
 *      railway's own all diesel, no money moving; the audit closes for a
 *      year after; and saved again, in this build's format, it loads back to
 *      the cent.
 *
 *  11. A REFINERY THAT CLOSES SHIPS WHAT ITS TANKS HELD (Refining.
 *      produceStock(), star O1): nothing written off.
 *
 *  12. CRUDE BY GRADE (0.7.79, batch O3; spec-oil 2.2): each grade's cuts
 *      are a barrel - light Brent's [R1], heavy Maya's [R2], medium O1's
 *      blend - and a slate on medium alone is O1's to the bit; light makes
 *      more petrol and diesel, heavy burns the residue its diesel cannot
 *      cut. The city's oil is worked out field by field in acquisition
 *      order, a fixture's oil by fiat as medium, so a month's lift is
 *      graded by where E stands - since 0.7.93 in two pools, the ground's
 *      (its dry fields and the oil by fiat) and the sea's, each graded by
 *      where its own E stands; a field's sites are the sea's when its
 *      centre is sea, the rest dry. The refinery's month's mix is its local
 *      crude at the lift's grades and its imports at medium, next month's
 *      slate is struck on it, and it crosses a save (a save without it
 *      reads medium).
 *
 *  13. THE PHASE-1 BUYERS (0.7.83, batch O6; spec-oil 2.5): the vans burn
 *      diesel and the factories lubricants, the paved roads take bitumen,
 *      the drivers' petrol comes from the forecourts - each drawn or bought
 *      off the refiners' tanks first and the world for the rest - and the
 *      goods, the four among them, foot to the balance of payments' imports
 *      every month, the audit closing.
 *
 *  14. THE FORECOURTS (0.7.83; runs/research-pump.md 7): the grocers draw
 *      the drivers' litres at wholesale and sell them at the pump price -
 *      the wholesale a litre times 1 + PUMP_MARGIN, the sales tax passed on
 *      - as far as their stations can sell, and at the queue's price
 *      (QUEUE_MARGIN) past that; the drivers' bill is Retail's sale to the
 *      households to the bit; a journey's fuel is the pump's; the grocers
 *      order a Filling Station when the drivers' litres outrun what their
 *      stations can sell, earning on the litres it would sell, and the shops'
 *      rules never sell one; the month crosses a save, and a save from
 *      before reads none sold.
 *
 *  15. THE TANK FARM (0.7.85, batch O8; spec-oil 2.8): with none standing
 *      the refiners keep no crude and every figure is what it was; with one
 *      they keep CRUDE_COVER_MONTHS of their crude units' run as far as its
 *      room holds, their products share the rest of the room, their order
 *      is the run and what fills the store, bought as stock - and a refinery
 *      short of cash runs down its tanks and then runs no more than its
 *      crude on hand and the month's fill: the run held to the crude. The
 *      store crosses a save.
 *
 *  16. THE STRATEGIC RESERVE (0.7.85; spec-oil 2.8): no room, no fill; a
 *      fill, cut to the room and the treasury's means, bought in the month's
 *      crude market from the wells first and the world for the rest, its
 *      book what it paid; settled at the next strike - the part bought
 *      abroad the audit's "- city ReserveFill", the part from the wells
 *      Oil's sale banked at the same strike, the whole journalled, the
 *      reserve's cash exact; a release sold to the refiners pro rata with
 *      the wells and shipped past what they take ("+ city ReserveSales"),
 *      its book falling at average cost; each lever the player's decision,
 *      a release cancelling a fill; the crude among the goods held, the
 *      goods footing to the balance of payments and the audit closing
 *      every month; and the reserve crossing a save to settle the same.
 *
 * Every fixture CAUSES its condition: the city is handed its oil the way
 * MiningCheck hands a city its ore (LandManager.restoreSites()), except in
 * section 2, which buys it.
 */
public class OilCheck {

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

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-96s %s  %,.6f against %,.6f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    /** Bitwise, NaN equal to NaN: a figure that crossed a save (0.7.83). */
    static boolean same(double a, double b) {
        return Double.compare(a, b) == 0;
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

    /** Each fixture town's save folder, for the reloads (section 9). */
    static final java.util.Map<Game, GameFiles> FILES = new java.util.IdentityHashMap<>();

    /**
     * A town that pays its people and drives: houses, shops, bakeries to work
     * in, power, water, roads and builders, standing, on ground a quarter more
     * than they take (and `spare` more square feet) - small enough that its
     * centre holds none of the oil field the default world put 1.08 km from
     * its site until 0.7.98 (since 0.7.99, batch W1, the nearest lies 14 km
     * out).
     */
    static Game town(String label, int houses, double spare) {
        GameFiles files = GameFiles.scratch(label);
        Game g = new Game(files);
        FILES.put(g, files);
        quietly(() -> {
            g.newGame();
            g.setCashForTest(Founding.WEALTHY_CASH);
            BuildingManager b = g.getBuildingManager();
            String[] names = { "House", "Convenience Store", "Small Grocery Store", "Industrial Bakery", "Paved Road",
                    "Coal Power Plant", "Water Treatment Plant", "Construction Depot" };
            int[] counts = { houses, Math.max(1, houses / 45), Math.max(1, houses / 150), Math.max(1, houses / 150),
                    Math.max(4, houses / 15), 2, 1, 4 };
            double ground = 0;
            for (int i = 0; i < names.length; i++) ground += b.getTemplateByName(names[i]).getLandSqFt() * counts[i];
            g.getLandManager().setOwnedSqFt(LandManager.STARTING_SQ_FT + ground * 1.25 + spare);
            for (int i = 0; i < names.length; i++) g.buildStack(b.getTemplateByName(names[i]), counts[i], true);
        });
        return g;
    }

    /** The offer standing nearest the oil field on dry ground nearest the city's site that it does not own (MiningCheck.towardIron(), on oil): what a land well stands on (since 0.7.84 a dry site; the nearest field may lie in the sea since 0.7.99). */
    static LandParcel towardOil(Game game) {
        CityLand land = game.getCityLand();
        World world = game.getWorld();
        long cx = land.siteX() / World.CELL, cy = land.siteY() / World.CELL;
        Deposit nearest = null;
        double best = Double.MAX_VALUE;
        for (long y = cy - 1; y <= cy + 1; y++) {
            for (long x = cx - 1; x <= cx + 1; x++) {
                if (x < 0 || y < 0 || x >= World.CELLS || y >= World.CELLS) continue;
                for (Deposit d : world.fieldsInCell((int) (y * World.CELLS + x), Resource.OIL)) {
                    double r = LegacyLand.radius(d.x() - land.siteX(), d.y() - land.siteY());
                    if (land.ownsPlot(d.x(), d.y()) || r >= best || world.depthAt(d.x(), d.y()) > 0) continue;
                    best = r;
                    nearest = d;
                }
            }
        }
        if (nearest == null) return null;
        return MiningCheck.nearestOffer(game.getLandManager().getMarket(), nearest.x(), nearest.y());
    }

    /** One audit line's figure, read off the month's detail (to the cent, as the detail writes it); 0 when the line is absent. */
    static double auditLine(MoneyAudit.Result r, String label) {
        for (String line : r.detail.split("\n")) {
            String t = line.trim();
            if (!t.startsWith(label)) continue;
            String rest = t.substring(label.length()).trim().replace(",", "");
            try { return Double.parseDouble(rest); } catch (NumberFormatException e) { return Double.NaN; }
        }
        return 0;
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        goodsAndBuildings();
        onlyOwnedOil();
        theOilRunsOut();
        Game oilTown = refineryTakesLocalCrude();
        driversAndRailwayTakeLocalFuel(oilTown);
        theAudit(oilTown);
        noRefinery();
        whenARefineryIsBuilt();
        acrossASave(oilTown);
        Game back = theSplit(oilTown);
        aRefineryCloses(back);
        crudeByGrade();
        phaseOneBuyers();
        theForecourts();
        theTankFarm();
        theStrategicReserve();

        out.println();
        if (fails == 0) {
            out.println("All checks passed.");
        } else {
            out.println(fails + " FAILED");
            System.exit(fails);
        }
    }

    /* ============================ 15. THE TANK FARM (0.7.85) ============================ */

    /** The litres a tonne of medium crude's slate makes, every product: what the tanks are shared by with no unit standing (Refining.getStockCapacity()). */
    static double slateLitres(Refining.Slate one) {
        double all = 0;
        for (double v : one.products().values()) all += v;
        return all;
    }

    /**
     * A 600-house town with an Oil Refinery (held, as section 4's is) on
     * imported crude, then a Tank Farm beside it: the store, the room, the
     * order; two months with the refiners' till emptied, so their order for
     * crude - stock now - is not placed, and they run down the store and
     * then run on what is left of it; and the store across a save.
     */
    static void theTankFarm() throws Exception {
        out.println("\n--- 15. the tank farm: a month's crude kept, the products the rest of the room, the run held to the"
                + " crude (0.7.85) ---");
        Game g = town("oilcheck-tanks", 600, 870_000 + 1_076_391 + 700_000);
        BuildingsTemplate refinery = template(g, "Oil Refinery"), farm = template(g, "Tank Farm");
        Refining refiners = g.getSectors().refining();
        quietly(() -> {
            g.getBusinessInvestment().holdSector(Sectors.REFINING);
            g.buildStack(refinery, 1, true);
            g.simulateMonths(3);
        });
        double run = refiners.getInputAtCapacity(Good.CRUDE), tanks = refinery.getStock();
        Refining.Slate one = Refining.slate(1);
        double all = slateLitres(one);
        report("with no Tank Farm the refiners keep no crude: bought whole at the rate, the tanks the products' as they"
                        + " were, to the bit",
                !refiners.keepsCrude() && refiners.crudeKept() == 0 && !refiners.buysAhead(Good.CRUDE)
                        && refiners.getPantry(Good.CRUDE) == 0 && refiners.tankFarmRoom() == 0
                        && refiners.getStockCapacity(Good.PETROL) == tanks * one.of(Good.PETROL) / all
                        && refiners.bid(Good.CRUDE) == run * refiners.getOperatingRate(),
                String.format("%,.0f t a month, %,.0f L of tanks", run, tanks));

        quietly(() -> g.buildStack(farm, 1, true));
        double kept = refiners.crudeKept();
        double rest = tanks + farm.getStock() - kept * Refining.CRUDE_LITRES_PER_TONNE;
        report("a Tank Farm standing, they keep CRUDE_COVER_MONTHS of their crude units' run at nameplate, as far as its"
                        + " room holds: a month, 8,300 t - crude's own room",
                refiners.keepsCrude() && refiners.tankFarmRoom() == farm.getStock() && kept == 8300
                        && kept == Math.min(Refining.CRUDE_COVER_MONTHS * run, farm.getStock() / Refining.CRUDE_LITRES_PER_TONNE)
                        && refiners.getStockCapacity(Good.CRUDE) == kept,
                String.format("%,.0f t of %,.0f L", kept, farm.getStock()));
        boolean shared = true;
        for (Good p : refiners.goodsMade()) shared &= refiners.getStockCapacity(p) == rest * one.of(p) / all;
        report("...and the products share the rest of the room, the farm's and the refinery's tanks less the crude's"
                + " litres, as they shared the tanks before, to the bit", shared,
                String.format("%,.0f L of %,.0f", rest, tanks + farm.getStock()));
        double held0 = refiners.getPantry(Good.CRUDE), rate0 = refiners.getOperatingRate();
        report("...their order the month's run at the rate and what fills the store to it, and their crude is stock:"
                        + " bought as far as they can pay (Sector.buysAhead())",
                refiners.buysAhead(Good.CRUDE) && refiners.bid(Good.CRUDE) == Math.max(0, run * rate0 + kept - held0),
                String.format("%,.0f t", refiners.bid(Good.CRUDE)));

        // A month: the store fills.
        quietly(() -> g.simulateMonths(1));
        Sector.Input in = refiners.inputRow(Good.CRUDE);
        double fill1 = in.boughtLocal + in.imported, rate1 = refiners.getOperatingRate();
        double held1 = refiners.getPantry(Good.CRUDE);
        report("a month on, what it had and the fill less the run at the rate: the store a month's run",
                held1 == Refining.crudeAfterRun(held0, fill1, run * rate1) && Math.abs(held1 - kept) <= 1e-9 * kept,
                String.format("%,.4f t on hand, %,.4f t bought", held1, fill1));
        report("...counted among the goods the national accounts hold (NationalAccounts.HELD)",
                java.util.Arrays.asList(NationalAccounts.HELD).contains(Good.CRUDE)
                        && g.getEconomyManager().getNationalAccounts().getLastHeldUnits()[NationalAccounts.HELD.length - 1] >= 0,
                "");

        // ...across a save, before the till is emptied.
        GameFiles files = FILES.get(g);
        quietly(() -> g.saveGame(10, "tanks"));
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        Refining twinRefiners = twin.getSectors().refining();
        report("the store crosses a save: the crude on hand, to the bit, and kept as the live city keeps it",
                same(twinRefiners.getPantry(Good.CRUDE), held1) && twinRefiners.keepsCrude() && twinRefiners.crudeKept() == kept,
                String.format("%,.4f t", twinRefiners.getPantry(Good.CRUDE)));
        quietly(() -> { g.simulateMonths(1); twin.simulateMonths(1); });
        report("...and a month on the reloaded city's store is the live one's, to the bit",
                same(twinRefiners.getPantry(Good.CRUDE), refiners.getPantry(Good.CRUDE)),
                String.format("%,.4f t", refiners.getPantry(Good.CRUDE)));

        // The till emptied two months running: the order for stock is not placed; the store runs down, then binds.
        double[] h = new double[3], fills = new double[2], made = new double[2], cap = new double[2], ratesAfter = new double[2];
        h[0] = refiners.getPantry(Good.CRUDE);
        for (int m = 0; m < 2; m++) {
            final int k = m;
            quietly(() -> {
                refiners.setCash(-1e12);
                g.simulateMonths(1);
            });
            Sector.Input row = refiners.inputRow(Good.CRUDE);
            fills[k] = row.boughtLocal + row.imported;
            h[k + 1] = refiners.getPantry(Good.CRUDE);
            ratesAfter[k] = refiners.getOperatingRate();
            cap[k] = Refining.crudeRunCap(h[k], fills[k], run);
            Sector.Output petrol = refiners.output(Good.PETROL);
            made[k] = petrol.planned + petrol.exportBound + petrol.idled;
        }
        report("fixture: with the refiners' till emptied their order for crude is not placed, and the store pays",
                fills[0] == 0 && fills[1] == 0 && h[1] < h[0],
                String.format("bought %,.0f and %,.0f t; on hand %,.0f, %,.0f, %,.0f t", fills[0], fills[1], h[0], h[1], h[2]));
        report("...the first month runs on the store, at the rate, the crude on hand covering it",
                cap[0] >= ratesAfter[0] && h[1] == Refining.crudeAfterRun(h[0], fills[0], run * ratesAfter[0])
                        && Math.abs(made[0] - refiners.getCapacity(Good.PETROL) * ratesAfter[0]) <= 1e-9 * made[0],
                String.format("cap %.4f, rate %.4f", cap[0], ratesAfter[0]));
        report("...and the second runs no more than what was left: the rate held to the crude on hand and the month's fill"
                        + " (Refining.crudeRunCap()), the store empty",
                cap[1] < ratesAfter[1] && h[2] <= 1e-9 * h[1]
                        && Math.abs(made[1] - refiners.getCapacity(Good.PETROL) * cap[1]) <= 1e-9 * Math.max(1, made[1]),
                String.format("cap %.4f under the rate %.4f; %,.0f L of petrol's nameplate run, %,.0f at the rate",
                        cap[1], ratesAfter[1], made[1], refiners.getCapacity(Good.PETROL) * ratesAfter[1]));
        assertTrue("...the run's cap is the crude on hand and the fill over the nameplate run, none with no crude unit (pure)",
                Refining.crudeRunCap(100, 200, 400) == .75 && Refining.crudeRunCap(-5, 300, 600) == .5
                        && Refining.crudeRunCap(1, 1, 0) == Double.POSITIVE_INFINITY
                        && Refining.crudeAfterRun(100, 200, 250) == 50 && Refining.crudeAfterRun(100, 0, 250) == 0);
    }

    /* ============================ 16. THE STRATEGIC RESERVE (0.7.85) ============================ */

    /** The amount of a journal line last month (Game.getTreasuryJournal()), summed; NaN when absent. */
    static double journalled(Game g, String label) {
        double sum = Double.NaN;
        for (TreasuryJournal.Entry e : g.getTreasuryJournal()) {
            if (!e.label().equals(label)) continue;
            sum = Double.isNaN(sum) ? e.amount() : sum + e.amount();
        }
        return sum;
    }

    /**
     * A 600-house town with four wells and room for a Strategic Reserve: no
     * room, no fill; a fill more than the wells lift, with no refinery to
     * share their crude; the strike after; a refinery (held) and a release it
     * takes all of, then one past what it takes; and the reserve across a
     * save. Every month the audit closes and the goods foot to the balance of
     * payments.
     */
    static void theStrategicReserve() throws Exception {
        out.println("\n--- 16. the strategic reserve: the city's crude, filled from the wells and the world, released to"
                + " the refiners and the world, its money at the strike (0.7.85) ---");
        int wellsN = 4;
        Game g = town("oilcheck-reserve", 600, wellsN * 87_120 + 870_000 + 1_076_391 + 700_000);
        BuildingsTemplate well = template(g, "Oil Well"), refinery = template(g, "Oil Refinery");
        BuildingsTemplate reserveT = template(g, "Strategic Reserve");
        StrategicReserve reserve = g.getReserve();
        Oil oil = g.getSectors().oil();
        Refining refiners = g.getSectors().refining();
        double[] noRoom = new double[1];
        int[] logged = new int[1];
        quietly(() -> {
            g.getBusinessInvestment().holdSector(Sectors.REFINING);
            g.getLandManager().restoreSites(Resource.OIL, wellsN, 5_000_000);
            g.buildStack(well, wellsN, true);
            g.simulateMonths(3);
            logged[0] = g.getDecisions().size();
            noRoom[0] = g.fillReserve(1_000);
        });
        report("with no Strategic Reserve standing there is no room: a fill orders nothing, and is no decision",
                noRoom[0] == 0 && reserve.getFill() == 0 && g.getDecisions().size() == logged[0]
                        && StrategicReserve.room(g.getBuildingManager()) == 0, "");
        quietly(() -> g.buildStack(reserveT, 1, true));
        double room = StrategicReserve.room(g.getBuildingManager());
        report("a Strategic Reserve's room is its tanks' litres at a tonne of crude's: 429,185 t",
                room == reserveT.getStock() / Refining.CRUDE_LITRES_PER_TONNE && Math.round(room) == 429_185,
                String.format("%,.2f t", room));

        // The fill: more than the wells lift, with no refinery to share their crude.
        double order = 50_000;
        double ordered = g.fillReserve(order);
        DecisionLog.Entry said = g.getDecisions().last();
        report("a fill is ordered for the next clearing and recorded as the player's decision",
                ordered == order && reserve.getFill() == order && g.getDecisions().size() == logged[0] + 1
                        && DecisionLog.RESERVE.equals(said.kind()),
                said.label());
        double worstAudit = 0, worstGoods = 0;
        quietly(() -> g.simulateMonths(1));
        worstAudit = Math.max(worstAudit, g.getLastMoneyAudit().relative());
        Sector.Output lifted = oil.output(Good.CRUDE);
        GoodsMarket crude = g.getMarkets().get(Good.CRUDE);
        report("fixture: four wells lift less than the order, and no refinery buys their crude",
                lifted.produced > 0 && lifted.produced < order && refiners.buildingsStanding() == 0,
                String.format("%,.0f t lifted", lifted.produced));
        close("the reserve holds the order, bought in the month's crude market", reserve.getTonnes(), order, 1e-12);
        close("...every tonne the wells lifted first", reserve.getBoughtHomeTonnes(), lifted.produced, 1e-12);
        close("...and the world's for the rest", reserve.getBoughtAbroadTonnes(), order - lifted.produced, 1e-12);
        report("...at the local price and the import price, its book what it paid, to the bit",
                same(reserve.getCost(), reserve.getBoughtHome() + reserve.getBoughtAbroad())
                        && same(reserve.getBoughtAbroad(), reserve.getBoughtAbroadTonnes() * crude.importPrice())
                        && reserve.getFill() == 0,
                String.format("$%,.2fk, $%,.2fk of it abroad", reserve.getCost(), reserve.getBoughtAbroad()));
        report("...the wells' crude Oil's sale to the city, on its books, none shipped",
                same(oil.pending().soldOf(Good.CRUDE).atHome, reserve.getBoughtHome()) && oil.unitsExported(Good.CRUDE) == 0,
                String.format("$%,.2fk", oil.pending().soldOf(Good.CRUDE).atHome));

        // The strike settles it.
        double boughtHome = reserve.getBoughtHome(), boughtAbroad = reserve.getBoughtAbroad(), tonnes1 = reserve.getTonnes();
        quietly(() -> g.simulateMonths(1));
        MoneyAudit.Result a = g.getLastMoneyAudit();
        worstAudit = Math.max(worstAudit, a.relative());
        report("the next strike settles it: what it bought abroad is the audit's - city ReserveFill, TRADE",
                reserve.getSettledImports() == boughtAbroad && Math.abs(auditLine(a, "- city ReserveFill") - boughtAbroad) <= .005
                        && auditLine(a, "+ city ReserveSales") == 0,
                String.format("$%,.2fk", auditLine(a, "- city ReserveFill")));
        report("...the treasury paid the whole, at home and abroad, and the bridge names it: the reserve's cash, exact",
                same(reserve.getSettledBought(), boughtHome + boughtAbroad)
                        && same(journalled(g, "Bought crude for the strategic reserve"), -(boughtHome + boughtAbroad)),
                String.format("$%,.2fk", -journalled(g, "Bought crude for the strategic reserve")));
        report("...and what it paid the wells is Oil's sale, banked at the same strike: a pool paying a pool, not listed",
                same(oil.statement().sold.get(Good.CRUDE).atHome, boughtHome),
                String.format("$%,.2fk", oil.statement().sold.get(Good.CRUDE).atHome));
        Sectors.TradeByGood goods = g.getTradeByGood();
        Sectors.GoodTrade crudeTrade = goods.goods().get(Good.CRUDE);
        report("...the crude it bought abroad among crude's, the city among its buyers, footing to the balance of payments",
                crudeTrade != null && same(crudeTrade.buyers().getOrDefault(Sectors.CITY, 0.0), boughtAbroad)
                        && Math.abs(goods.bought() - g.getForeignAccounts().tradeImports())
                        <= 1e-9 * Math.max(1, g.getForeignAccounts().tradeImports()),
                String.format("$%,.2fk", crudeTrade == null ? 0 : crudeTrade.bought()));
        report("...and its crude among the goods held, the national accounts' fifth term",
                g.getEconomyManager().getNationalAccounts().getLastHeldUnits()[NationalAccounts.HELD.length - 1] == tonnes1
                        && NationalAccounts.HELD[NationalAccounts.HELD.length - 1] == Good.CRUDE,
                String.format("%,.0f t", tonnes1));

        // A fill past the room is cut to it, or to what the treasury could pay; a release cancels it.
        double cut = Math.min(room - reserve.getTonnes(), g.discretionaryRoom() / crude.importPrice());
        double asked = g.fillReserve(1e12);
        report("a fill past the room is cut to the room left, or to what the treasury could pay at the import price",
                asked == cut && reserve.getFill() == cut, String.format("%,.0f t", asked));
        double release = 2_000;
        g.releaseReserve(release);
        report("...and a release cancels it: the city never trades with itself",
                reserve.getFill() == 0 && reserve.getRelease() == release
                        && g.getDecisions().last().label().contains("the fill cancelled"),
                g.getDecisions().last().label());

        // The refinery: a release it takes all of, pro rata with the wells, wanting more.
        quietly(() -> {
            g.releaseReserve(0);
            g.buildStack(refinery, 1, true);
        });
        quietly(() -> g.simulateMonths(1));
        worstAudit = Math.max(worstAudit, g.getLastMoneyAudit().relative());
        g.releaseReserve(release);
        double tonnes2 = reserve.getTonnes(), cost2 = reserve.getCost();
        quietly(() -> g.simulateMonths(1));
        worstAudit = Math.max(worstAudit, g.getLastMoneyAudit().relative());
        Sector.Input in = refiners.inputRow(Good.CRUDE);
        report("fixture: the refinery wants more than the wells and the release offer it",
                in.bid > release + lifted.produced, String.format("%,.0f t wanted", in.bid));
        close("the refiners took the release whole, at the local price, pro rata with the wells", reserve.getSoldHomeTonnes(),
                release, 1e-12);
        report("...nothing of it shipped, and their books the purchase from the city at the local price",
                reserve.getSoldAbroadTonnes() == 0 && same(refiners.pending().purchasesBySupplier.getOrDefault(Trade.CITY, 0.0),
                        reserve.getSoldHome())
                        && Math.abs(reserve.getSoldHome() - release * crude.getLocalPrice()) <= 1e-9 * reserve.getSoldHome(),
                String.format("$%,.2fk", reserve.getSoldHome()));
        close("...the reserve's tonnes fall by the release", reserve.getTonnes(), tonnes2 - release, 1e-12);
        close("...and its book by the share of it the tonnes were, at average cost", reserve.getCost(),
                cost2 - cost2 * (release / tonnes2), 1e-12);

        // ...a release past what the refiners take: the rest ships at the export price.
        double big = 30_000, tonnes3 = reserve.getTonnes();
        g.releaseReserve(big);
        quietly(() -> g.simulateMonths(1));
        worstAudit = Math.max(worstAudit, g.getLastMoneyAudit().relative());
        double home = reserve.getSoldHomeTonnes(), abroad = reserve.getSoldAbroadTonnes();
        report("a release past what the refiners want ships the rest at the export price, as the wells' unsold crude does",
                home > 0 && abroad > 0 && Math.abs(home + abroad - big) <= 1e-9 * big
                        && same(reserve.getSoldAbroad(), abroad * crude.exportPrice()),
                String.format("%,.0f t to the refiners, %,.0f t shipped", home, abroad));
        double shipped = reserve.getSoldAbroad(), soldHome = reserve.getSoldHome();

        // ...across a save, its month unsettled.
        GameFiles files = FILES.get(g);
        quietly(() -> g.saveGame(10, "reserve"));
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        StrategicReserve back = twin.getReserve();
        report("the reserve crosses a save: its crude, its book, its release and the month the strike will settle, to the bit",
                same(back.getTonnes(), reserve.getTonnes()) && same(back.getCost(), reserve.getCost())
                        && back.getRelease() == big && same(back.getSoldAbroad(), shipped) && same(back.getSoldHome(), soldHome),
                String.format("%,.2f t, $%,.2fk", back.getTonnes(), back.getCost()));
        quietly(() -> { g.simulateMonths(1); twin.simulateMonths(1); });
        worstAudit = Math.max(worstAudit, g.getLastMoneyAudit().relative());
        MoneyAudit.Result b = g.getLastMoneyAudit();
        report("...the strike after settles what shipped as the audit's + city ReserveSales, TRADE, journalled",
                reserve.getSettledExports() == shipped && Math.abs(auditLine(b, "+ city ReserveSales") - shipped) <= .005
                        && same(journalled(g, "Sold crude from the strategic reserve"), soldHome + shipped),
                String.format("$%,.2fk", auditLine(b, "+ city ReserveSales")));
        report("...and the reloaded city settles the same, and holds the same, to the bit",
                same(twin.getReserve().getSettledSold(), reserve.getSettledSold())
                        && same(twin.getReserve().getSettledExports(), reserve.getSettledExports())
                        && same(twin.getReserve().getTonnes(), reserve.getTonnes())
                        && same(twin.getReserve().getCost(), reserve.getCost()),
                String.format("$%,.2fk", twin.getReserve().getSettledSold()));
        report("...the release a standing order, the tonnes falling by it", reserve.getTonnes() < tonnes3 - big,
                String.format("%,.0f t left", reserve.getTonnes()));

        // A year more: the release runs the reserve down and stops with it, the audit closing and the goods footing.
        for (int m = 0; m < 12; m++) {
            quietly(() -> g.simulateMonths(1));
            worstAudit = Math.max(worstAudit, g.getLastMoneyAudit().relative());
            Sectors.TradeByGood gd = g.getTradeByGood();
            double imports = g.getForeignAccounts().tradeImports(), exports = g.getForeignAccounts().getExports();
            worstGoods = Math.max(worstGoods, Math.max(Math.abs(gd.bought() - imports) / Math.max(1, imports),
                    Math.abs(gd.sold() - exports) / Math.max(1, exports)));
        }
        report("a release offers no more than the reserve holds: it empties, and its book with it",
                reserve.getTonnes() == 0 && reserve.getCost() == 0, String.format("%,.2f t", reserve.getTonnes()));
        report("the goods, the city's crude among them, foot to the balance of payments both ways every month",
                worstGoods < 1e-9, String.format("worst %.2e", worstGoods));
        report("...and the audit closes every month", worstAudit < 1e-10, String.format("worst %.2e", worstAudit));
    }

    /* ============================ 13. THE PHASE-1 BUYERS (0.7.83) ============================ */

    /**
     * A town that drives, with two filling stations, two fabrication shops, a
     * machine works and a vehicle works, and a Paved Road ordered: its vans
     * burn diesel, its factories lubricants, the road bitumen and its drivers
     * petrol bought at the pump - each from the world, the town having no
     * refinery - and for a year the goods, the four among them, foot to the
     * balance of payments' imports and the audit closes.
     */
    static void phaseOneBuyers() {
        out.println("\n--- 13. the phase-1 buyers: vans' diesel, lubricants, bitumen and the pump, footing to the balance of"
                + " payments (0.7.83) ---");
        Game g = town("oilcheck-buyers", 600, 4_000_000);
        BuildingsTemplate paved = template(g, "Paved Road");
        double[] bitumen = new double[2];
        quietly(() -> {
            BuildingManager b = g.getBuildingManager();
            for (String[] w : new String[][] { { "Filling Station", "2" }, { "Fabrication Shop", "2" }, { "Machine Works", "1" },
                    { "Vehicle Works", "1" } }) {
                g.buildStack(b.getTemplateByName(w[0]), Integer.parseInt(w[1]), true);
            }
            g.simulateMonths(2);
            bitumen[0] = g.getBitumenTonnes();
            g.buildStack(paved, 1, false);
            bitumen[1] = g.getBitumenTonnes();
        });
        report("a Paved Road ordered draws its bitumen as it is placed: 64.25 t, bought by the builders",
                bitumen[1] - bitumen[0] == g.bitumenFor(paved) && g.bitumenFor(paved) == 64.25
                        && g.getSectors().construction().pending().boughtOf(Good.BITUMEN).abroad > 0,
                String.format("%,.2f t, $%,.2fk", bitumen[1] - bitumen[0], g.getBitumenCost()));
        double worstAudit = 0, worstGoods = 0, diesel = 0, lubricants = 0, petrol = 0, bitumenBought = 0;
        java.util.Set<String> dieselBuyers = new java.util.TreeSet<>(), lubricantBuyers = new java.util.TreeSet<>();
        int months = 12;
        for (int m = 0; m < months; m++) {
            quietly(() -> g.simulateMonths(1));
            worstAudit = Math.max(worstAudit, g.getLastMoneyAudit().relative());
            Sectors.TradeByGood goods = g.getTradeByGood();
            double tradeImports = g.getForeignAccounts().tradeImports();
            worstGoods = Math.max(worstGoods, Math.abs(goods.bought() - tradeImports) / Math.max(1, tradeImports));
            Sectors.GoodTrade d = goods.goods().get(Good.DIESEL), l = goods.goods().get(Good.LUBRICANTS),
                    p = goods.goods().get(Good.PETROL), t = goods.goods().get(Good.BITUMEN);
            if (d != null) { diesel += d.bought(); dieselBuyers.addAll(d.buyers().keySet()); }
            if (l != null) { lubricants += l.bought(); lubricantBuyers.addAll(l.buyers().keySet()); }
            if (p != null) petrol += p.buyers().getOrDefault(Sectors.RETAIL, 0.0);
            if (t != null) bitumenBought += t.buyers().getOrDefault(Sectors.CONSTRUCTION, 0.0);
        }
        report("fixture: the town has no refinery, so its four fuels and oils come from the world",
                g.getSectors().refining().buildingsStanding() == 0, "");
        report("the vans' diesel is DIESEL bought abroad, by the sectors that run fleets", diesel > 0
                        && dieselBuyers.contains(Sectors.MANUFACTURING) && dieselBuyers.contains(Sectors.RETAIL),
                String.format("$%,.1fk over %d months, by %s", diesel, months, dieselBuyers));
        report("...the factories' lubricants LUBRICANTS', by Manufacturing and Automotive and nobody else", lubricants > 0
                        && lubricantBuyers.equals(new java.util.TreeSet<>(java.util.List.of(Sectors.MANUFACTURING, Sectors.AUTOMOTIVE))),
                String.format("$%,.1fk, by %s", lubricants, lubricantBuyers));
        report("...the drivers' petrol PETROL's, by the forecourts", petrol > 0, String.format("$%,.1fk", petrol));
        report("...and the road's bitumen BITUMEN's, by the builders, the month after it was drawn",
                bitumenBought > 0, String.format("$%,.2fk", bitumenBought));
        report("the goods, the four among them, foot to the balance of payments' imports every month", worstGoods < 1e-9,
                String.format("worst %.2e", worstGoods));
        report("...and the audit closes every month", worstAudit < 1e-10, String.format("worst %.2e over %d months", worstAudit, months));
    }

    /* ============================ 14. THE FORECOURTS (0.7.83) ============================ */

    static void theForecourts() throws Exception {
        out.println("\n--- 14. the forecourts: the pump price, the queue past the stations, the grocers' books and their stations (0.7.83) ---");
        double w = .00071, r = .15;
        assertTrue("the pump price is the wholesale times 1 + PUMP_MARGIN, over 1 less the sales tax, to the bit; the queue's"
                        + " QUEUE_MARGIN's; no tax, no gross-up",
                Retail.pumpPrice(w, r) == w * (1 + Retail.PUMP_MARGIN) / (1 - r)
                        && Retail.queuePrice(w, r) == w * (1 + Retail.QUEUE_MARGIN) / (1 - r)
                        && Retail.pumpPrice(w, 0) == w * (1 + Retail.PUMP_MARGIN)
                        && Retail.PUMP_MARGIN == .12 && Retail.QUEUE_MARGIN == .18);
        close("...so on a litre the grocers keep PUMP_MARGIN of its wholesale once they have remitted their rate on the price",
                Retail.pumpPrice(w, r) * (1 - r) - w, w * Retail.PUMP_MARGIN, 1e-12);

        // A town that drives, the grocers' own orders held, with three filling stations, two years on.
        Game g = town("oilcheck-pump", 600, 2_000_000);
        Retail pump = g.getSectors().retail();
        BuildingsTemplate station = template(g, "Filling Station");
        quietly(() -> {
            g.getBusinessInvestment().holdSector(Sectors.RETAIL);
            g.buildStack(station, 3, true);
            g.simulateMonths(24);
        });
        report("fixture: three filling stations stand, and the town drives", pump.stationsStanding() == 3
                        && pump.stationLitres() == 3 * 350_000 && pump.getPumpLitres() > 0,
                String.format("%,.0f L a month at nameplate, %,.0f L sold", pump.stationLitres(), pump.getPumpLitres()));
        close("what they can sell is their litres at the grocers' operating rate", pump.stationCapacity(),
                pump.stationLitres() * pump.getOperatingRate(), 1e-12);
        // The grocers' word on a station: these can sell what the town burns.
        BusinessInvestment plans = g.getBusinessInvestment();
        BusinessInvestment.Decision covered = pump.planStations(plans, g);
        report("with stations that can sell what the drivers burn, the grocers order none", !covered.build
                        && pump.litresForecast(station, plans) <= pump.stationLitres() * pump.getOperatingRate()
                        && covered.reason.startsWith("the stations can sell"),
                String.format("%,.0f L forecast: \"%s\"", pump.litresForecast(station, plans), covered.reason));

        // A month's sale past what they can sell, drawn by hand: the pump's price on their litres, the queue's on the rest.
        double cap = pump.stationCapacity(), litres = cap * 1.5;
        double rate = g.getEconomyManager().getTaxPolicy().effectiveSalesRate(pump);
        Sector.Ledger before = pump.pending();
        double soldBefore = before.soldOf(Good.PETROL).atHome, homesBefore = before.salesToHouseholds,
                boughtBefore = before.boughtOf(Good.PETROL).total();
        Retail.FuelSale sale = pump.sellFuel(litres, g);
        double wl = pump.getWholesaleLitre();
        report("a month past their capacity: what they can sell at the pump, the rest past the stations",
                pump.getPumpLitres() == cap && pump.getQueueLitres() == litres - cap && sale.queued() == litres - cap,
                String.format("%,.0f L at the pump, %,.0f L past them", pump.getPumpLitres(), pump.getQueueLitres()));
        report("...at the pump price and the queue's on the wholesale they paid a litre, the bill the two sales to the bit",
                pump.getPumpPrice() == Retail.pumpPrice(wl, rate) && pump.getQueuePrice() == Retail.queuePrice(wl, rate)
                        && sale.bill() == cap * pump.getPumpPrice() + (litres - cap) * pump.getQueuePrice(),
                String.format("%.6f and %.6f on %.6f", pump.getPumpPrice(), pump.getQueuePrice(), wl));
        close("...the bill a sale to the households on Retail's books", pump.pending().salesToHouseholds - homesBefore, sale.bill(), 1e-12);
        close("...and its petrol line's", pump.pending().soldOf(Good.PETROL).atHome - soldBefore, sale.bill(), 1e-12);
        close("...the wholesale its purchase, bought from the world with no refinery here", pump.pending().boughtOf(Good.PETROL).total()
                - boughtBefore, sale.wholesale(), 1e-12);
        assertTrue("...and none of it the households' own import", sale.imported() == sale.wholesale() && pump.getFuelImported() == sale.imported());

        // ...and a town with no station, two years on, its drivers' litres all past them - with three hundred houses
        // more than its jobs, so it has the hands to staff one.
        Game bare = town("oilcheck-nopump", 600, 8_000_000);
        Retail none = bare.getSectors().retail();
        quietly(() -> {
            bare.getBusinessInvestment().holdSector(Sectors.RETAIL);
            bare.buildStack(template(bare, "House"), 300, true);
            bare.simulateMonths(24);
        });
        BusinessInvestment barePlans = bare.getBusinessInvestment();
        BusinessInvestment.Decision wanted = none.planStations(barePlans, bare);
        report("a town with no station, its drivers' petrol all past the stations at the queue's price, is given one",
                none.stationsStanding() == 0 && none.getQueueLitres() > 0 && none.getPumpLitres() == 0 && wanted.build
                        && wanted.template == template(bare, "Filling Station") && wanted.quantity >= 1,
                String.format("%,.0f L past them: \"%s\"", none.getQueueLitres(), wanted.reason));
        BuildingsTemplate one = template(bare, "Filling Station");
        double landed = bare.getMarkets().get(Good.PETROL).landedPrice();
        double expected = Math.min(one.pumpLitres(), none.litresForecast(one, barePlans)) * landed * Retail.PUMP_MARGIN;
        close("...earning, for the interest test, its margin on the litres it would sell: the forecast's, under its 350,000",
                none.estimatedMonthlyProfit(one, barePlans), expected, 1e-12);
        assertTrue("...and the shops' rules never sell a station: it serves the drivers, not the baskets they count",
                !none.mayRetire(one) && none.mayRetire(template(bare, "Convenience Store")));

        // The month crosses a save (section 9); a save from before 0.7.83 sold no petrol here.
        GameFiles files = FILES.get(g);
        quietly(() -> g.saveGame(10, "pump"));
        Path file = files.saveFile(10);
        com.google.gson.JsonObject o = com.google.gson.JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        com.google.gson.JsonObject shops = sectorOf(o, Sectors.RETAIL);
        com.google.gson.JsonObject extras = shops.getAsJsonObject("extras");
        java.util.List<String> keys = new java.util.ArrayList<>();
        for (String k : extras.keySet()) if (k.startsWith("pump.")) keys.add(k);
        for (String k : keys) extras.remove(k);
        Files.writeString(file, new com.google.gson.GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(o));
        Game old = new Game(files);
        quietly(() -> old.loadGameSave(10));
        Retail oldPump = old.getSectors().retail();
        report("a save from before 0.7.83 loads with no petrol sold at the pump and no pump price",
                keys.size() >= 5 && oldPump.getPumpLitres() == 0 && oldPump.getQueueLitres() == 0
                        && Double.isNaN(oldPump.getPumpPrice()) && oldPump.getFuelBill() == 0,
                keys.size() + " keys taken out");
        quietly(() -> old.simulateMonths(1));
        report("...and its first month sells the drivers' petrol at the pump, the audit closing",
                oldPump.getPumpLitres() > 0 && old.getLastMoneyAudit().relative() < 1e-10,
                String.format("%,.0f L, %.2e", oldPump.getPumpLitres(), old.getLastMoneyAudit().relative()));
    }

    /* ============================ 1. THE GOODS AND THE BUILDINGS ============================ */

    /** The nine products of 0.7.76 (spec-oil 2.1), in the spec's table's order. */
    static final Good[] PRODUCTS = { Good.LPG, Good.NAPHTHA, Good.PETROL, Good.JET, Good.DIESEL, Good.LUBRICANTS,
            Good.FUEL_OIL, Good.BITUMEN, Good.COKE };

    /** The spec's table (2.1): litres a tonne for the litre goods ([P35]; naphtha and lubricants est., JODI), NaN for the two in tonnes. */
    static final double[] LITRES_A_TONNE = { 1850, 1351, 1320, 1260, 1180, 1127, 1010, Double.NaN, Double.NaN };

    /** ...and each one's ratio to crude's world middle (the research's ladder, the prototype's RATIO): a litre's, or a tonne's for bitumen and coke. Petrol and diesel on it since 0.7.78 (batch O2). */
    static final double[] LADDER = { .46, 1.0, 1.20, 1.28, 1.35, 1.89, .98, 1.08, .155 };

    /** FUEL's import price, 0.7.62 to 0.7.75 (petrol's and diesel's at 0.7.76 and 0.7.77): a journey's pump price over its litres. */
    static final double PUMP_LITRE = Motoring.CAR_FUEL_PER_JOURNEY / Motoring.LITRES_PER_JOURNEY;

    /** What the ladder takes off the pump price, as the spec gives it (spec-oil 1, item 4): petrol 63%, diesel 59%, to the percent. */
    static final int PETROL_CUT_PCT = 63, DIESEL_CUT_PCT = 59;

    /** The spec's four figures: a price within half a unit in its fourth significant figure. */
    static final double FOUR_FIGURES = 5e-4;

    static void goodsAndBuildings() {
        out.println("--- 1. the goods, the buildings and the sectors are the spec's ---");
        Good crude = Good.CRUDE;
        assertTrue("crude: US$600 a tonne in, US$500 out - about US$75 a barrel at 7.33 barrels a tonne",
                crude.worldImportPrice() == .60 && crude.worldExportPrice() == .50 && crude.unit().equals("tonne"));
        double half = (crude.worldImportPrice() - crude.worldExportPrice()) / 2;
        close("CRUDE: freight is three quarters of the half-wedge, Good's rule", crude.baseFreight(), .75 * half, 1e-12);
        assertTrue("...so the world still buys below what it sells for", crude.worldBuyPrice() > crude.worldSellPrice());
        assertTrue("crude ships what is lifted (a flow good, star)", !crude.stockable() && crude.traded());
        assertTrue("crude goes by the bulk stream", crude.traffic() == Traffic.BULK);
        assertTrue("FUEL is retired (0.7.76): no good is saved by its name", Good.byName(FuelSplit.FUEL) == null);

        // The nine (0.7.76, spec-oil 2.1); petrol and diesel on the ladder since 0.7.78 (O2, Jerus: "yes wholesale").
        double petrolCut = 1 - Good.PETROL.worldImportPrice() / PUMP_LITRE, dieselCut = 1 - Good.DIESEL.worldImportPrice() / PUMP_LITRE;
        report("petrol and diesel at wholesale (0.7.78): the ladder takes 63% off the pump price a litre the drivers paid, and 59%"
                        + " off the railway's (spec-oil 1, item 4)",
                Math.round(100 * petrolCut) == PETROL_CUT_PCT && Math.round(100 * dieselCut) == DIESEL_CUT_PCT,
                String.format("petrol %.4f%% off, diesel %.4f%%", 100 * petrolCut, 100 * dieselCut));
        double crudeLitre = (crude.worldImportPrice() + crude.worldExportPrice()) / 2 / Refining.CRUDE_LITRES_PER_TONNE;
        boolean flags = true, weights = true, ladder = true, freight = true, streams = true;
        for (int i = 0; i < PRODUCTS.length; i++) {
            Good g = PRODUCTS[i];
            flags &= g.stockable() && g.traded() && !g.taxExempt() && g.importable() && g.exportable();
            boolean litres = !Double.isNaN(LITRES_A_TONNE[i]);
            weights &= litres ? g.unit().equals("litre") && g.litresPerTonne() == LITRES_A_TONNE[i]
                    && g.tonnesPerUnit() == 1 / LITRES_A_TONNE[i]
                    : g.unit().equals("tonne") && g.tonnesPerUnit() == 1 && Double.isNaN(g.litresPerTonne());
            double mid = (g.worldImportPrice() + g.worldExportPrice()) / 2;
            if (!Double.isNaN(LADDER[i])) {
                freight &= Math.abs(g.baseFreight() - .06 * mid) <= FOUR_FIGURES * g.baseFreight();
                double world = LADDER[i] * (litres ? crudeLitre : (crude.worldImportPrice() + crude.worldExportPrice()) / 2);
                ladder &= Math.abs(g.worldImportPrice() - world * 1.08) <= FOUR_FIGURES * g.worldImportPrice()
                        && Math.abs(g.worldExportPrice() - world * .92) <= FOUR_FIGURES * g.worldExportPrice();
                streams &= g.traffic() == (g == Good.PETROL || g == Good.DIESEL ? Traffic.GOODS : Traffic.BULK);
            }
            half = (g.worldImportPrice() - g.worldExportPrice()) / 2;
            freight &= g.worldBuyPrice() > g.worldSellPrice() && Math.abs(g.baseFreight() - .75 * half) < .015 * half;
        }
        assertTrue("the nine are stockable, priced in the band, taxed, and the world sells and buys each", flags);
        assertTrue("...a litre weighs its good's tonne over its litres (1,850 for gas to 1,010 for fuel oil), bitumen and coke"
                + " a tonne", weights);
        assertTrue("...all nine on the ladder: crude's world middle a litre (a tonne) x the research's ratio, x1.08 in and x0.92"
                + " out, to the spec's four figures (petrol and diesel since 0.7.78)", ladder);
        assertTrue("...freight three quarters of the half-wedge (Good's rule; on the ladder .06 of the world's price), so the"
                + " world still buys below what it sells for", freight);
        assertTrue("...petrol and diesel with the goods, the other seven by the bulk stream, as crude", streams);

        // THE SLATE (spec-oil 2.3): a tonne of medium crude, cut by the leftovers and the residue's cut.
        Refining.Slate tonne = Refining.slate(1);
        double[] c = Refining.MEDIUM_CUTS;
        double litres = Refining.CRUDE_LITRES_PER_TONNE;
        double residue = litres * c[Refining.CUT_RESIDUE], straightDiesel = litres * c[Refining.CUT_DIESEL];
        double sum = 0;
        for (double v : c) sum += v;
        close("the medium cuts are a barrel: they sum to one", sum, 1, 1e-15);
        close("a tonne of medium crude makes 70 L of petrol (its light naphtha)", tonne.of(Good.PETROL), 69.9, 1e-12);
        close("...169 L of diesel: its 256 less the third of the residue's litres it cuts",
                tonne.of(Good.DIESEL), straightDiesel - residue / Refining.RESIDUE_PER_DIESEL, 1e-12);
        close("...629 L of fuel oil: its gas oil and the residue, cut three to one with the diesel",
                tonne.of(Good.FUEL_OIL), litres * c[Refining.CUT_GAS_OIL] + residue * 4 / 3, 1e-12);
        close("...and its gas, heavy naphtha and kerosene as petroleum gas, naphtha and jet fuel",
                tonne.of(Good.LPG) + tonne.of(Good.NAPHTHA) + tonne.of(Good.JET),
                litres * (c[Refining.CUT_GAS] + c[Refining.CUT_HEAVY_NAPHTHA] + c[Refining.CUT_KEROSENE]), 1e-12);
        double made = 0;
        for (double v : tonne.products().values()) made += v;
        close("the litres balance: the products and the residue burned are the tonne's 1,165 L", made + tonne.burned(),
                litres, 1e-12);
        assertTrue("...a medium crude's diesel cuts all its residue: none burned, and no lubricants, bitumen or coke without"
                        + " a conversion unit",
                tonne.burned() == 0 && tonne.of(Good.LUBRICANTS) == 0 && tonne.of(Good.BITUMEN) == 0 && tonne.of(Good.COKE) == 0);
        Refining.Slate plant = Refining.slate(8300);
        close("an Oil Refinery's 8,300 t make 1.98M L of petrol and diesel (where they made 8.3M L of FUEL)",
                Refining.boughtHere(plant), 8300 * (tonne.of(Good.PETROL) + tonne.of(Good.DIESEL)), 1e-12);
        assertTrue("oil in the ground is crude, sold at 5% of its world price",
                Resource.OIL.good() == crude && Resource.OIL.inGroundShare() == .05);
        double[] km2 = new double[CityLand.AREAS];
        km2[CityLand.TOTAL] = .1;
        km2[CityLand.DRY] = .1;
        double[] none = new double[Resource.values().length], oil = none.clone();
        oil[Resource.OIL.ordinal()] = 150_000;
        double ground = .0007, level = 1.137;
        double withOil = LandMarket.price(km2, oil, ground, level), without = LandMarket.price(km2, none, ground, level);
        close("an offer's oil adds its tonnes at 5% of .50 at the world's price level (to the US$5k rounding)",
                withOil - without, 150_000 * .50 * .05 * level, 5);

        String[] keys = Sectors.KEYS;
        assertTrue("the registry ends with the wells and the refinery: " + keys[keys.length - 2] + ", " + keys[keys.length - 1],
                keys[keys.length - 2].equals(Sectors.OIL) && keys[keys.length - 1].equals(Sectors.REFINING));
        Sectors s = new Sectors(new BuildingManager(), new Markets());
        boolean makesAll = true;
        for (Good g : PRODUCTS) makesAll &= s.refining().isMaker(g);
        assertTrue("...each found by its saved name, the wells making crude, the refinery crude into the nine",
                s.byKey(Sectors.OIL) == s.oil() && s.byKey(Sectors.REFINING) == s.refining()
                        && s.oil().isMaker(crude) && s.refining().isUser(crude) && makesAll
                        && s.refining().goodsMade().size() == PRODUCTS.length);

        BuildingManager bm = new BuildingManager();
        bm.initializeTemplates();
        BuildingsTemplate well = bm.getTemplateByName("Oil Well"), refinery = bm.getTemplateByName("Oil Refinery");
        assertTrue("the Oil Well: id 74, MINING, the Oil sector's, 415 t of crude a month (a hundred barrels a day)",
                well != null && well.getId() == 74 && well.getCategory() == BuildingType.MINING
                        && Sectors.OIL.equals(well.getSector()) && well.makes(crude) == 415);
        assertTrue("...and stands on an oil site: Game.siteOf() is oil, an Iron Mine's iron",
                Game.siteOf(well) == Resource.OIL && Game.siteOf(bm.getTemplateByName("Iron Mine")) == Resource.IRON
                        && Game.siteOf(refinery) == null);
        assertTrue("the Oil Refinery: id 75, HEAVY_INDUSTRY, Refining's, a crude unit of 8,300 t, its products the slate of"
                        + " them (its template makes nothing)",
                refinery != null && refinery.getId() == 75 && refinery.getCategory() == BuildingType.HEAVY_INDUSTRY
                        && Sectors.REFINING.equals(refinery.getSector()) && refinery.uses(crude) == 8300
                        && refinery.goodsMade().isEmpty() && Refining.isCrudeUnit(refinery)
                        && Refining.madeBy(refinery).equals(plant.products()));
        assertTrue("...with more than the months of its run a maker keeps in its tanks (STOCK_MONTHS)",
                refinery != null && refinery.getStock() >= Sector.STOCK_MONTHS * made * 8300);
        // The litres a tonne were WORLD_FUEL_PER_TONNE over FUEL's import price; at the ladder's price the quotient is 43.6.
        report("the railway burns eighteen litres of diesel a tonne hauled, pinned (0.7.78) where WORLD_FUEL_PER_TONNE over the"
                        + " pump price put them, not moved by the ladder's price",
                Rail.FUEL_LITRES_PER_TONNE == 18 && Math.abs(Rail.WORLD_FUEL_PER_TONNE / PUMP_LITRE - 18) < 1e-12
                        && Rail.WORLD_FUEL_PER_TONNE / Good.DIESEL.worldImportPrice() > 40,
                String.format("%.15f L; .03 over the ladder's import price would be %.1f", Rail.FUEL_LITRES_PER_TONNE,
                        Rail.WORLD_FUEL_PER_TONNE / Good.DIESEL.worldImportPrice()));
    }

    /* ============================ 2. A WELL LIFTS ONLY OWNED OIL ============================ */

    static void onlyOwnedOil() {
        out.println("\n--- 2. a well lifts only the oil the city owns ---");
        Game g = town("oilcheck-owned", 300, 0);
        BuildingsTemplate well = template(g, "Oil Well"), mine = template(g, "Iron Mine");
        LandManager land = g.getLandManager();
        assertTrue("fixture: a new city owns no oil", land.getOilSites() == 0);
        Game.BuildResult refused = quietlyGet(() -> g.buildStack(well, 1, false));
        BuildCard.Verdict v = BuildCard.verdict(g, well, 1);
        report("with no oil an Oil Well is refused for its deposit, on the card as at the order",
                refused == Game.BuildResult.NO_DEPOSIT && v.kind() == BuildCard.VerdictKind.NO_DEPOSIT && v.site() == Resource.OIL,
                refused + ", " + v.kind() + " on " + v.site());
        BusinessInvestment.Decision d = g.getSectors().oil().plan(g.getBusinessInvestment(), g);
        report("...and the investors drill nothing: the wells' word names the deposit", !d.build && d.reason.contains("deposit"),
                "\"" + d.reason + "\"");
        assertTrue("an Oil Well has one post, a diploma's (0.7.84; three until then: two without a diploma and one with)",
                well.getTotalJobs() == 1 && well.getJobs(JobType.DIPLOMA) == 1);

        // Bought: toward the nearest oil field on dry ground, the offer nearest it each time, until the city holds a dry site
        // (a land well's; since 0.7.99 the nearest field may lie in the sea, fourteen kilometres out on the default world).
        int pushes = 0;
        double paid = 0;
        while (land.getSites(Resource.OIL, true) == 0 && pushes < 200) {
            LandParcel p = towardOil(g);
            if (p == null) break;
            final LandParcel buy = p;
            g.setCashForTest(g.getCash() + buy.localPrice(g.getForeignAccounts().getRate()));
            if (!quietlyGet(() -> g.buyLandParcel(buy.getId()))) break;
            paid += buy.getPriceUsd();
            pushes++;
        }
        int all = land.getOilSites(), sites = land.getSites(Resource.OIL, true);
        double owned = land.getOwnedAmount(Resource.OIL);
        int bought = 0;
        for (CityLand.Purchase p : g.getCityLand().purchases()) bought += p.offer().getSites(Resource.OIL);
        report("fixture: an offer with oil on dry ground bought (toward the nearest such field)", sites > 0,
                String.format("%d purchase(s), US$%,.0fk, %d site(s), %d dry, %,.0f t", pushes, paid, all, sites, owned));
        assertTrue("...the city's oil sites are the sites its purchases listed", all == bought);
        report("an Iron Mine is not let through by oil", g.hasDepositFor(mine, 1) == (land.getIronDeposits() > 0),
                land.getIronDeposits() + " iron site(s)");

        Game.BuildResult ok = quietlyGet(() -> g.buildStack(well, sites, true));
        Game.BuildResult past = quietlyGet(() -> g.buildStack(well, 1, false));
        report("a well a site: as many as the city owns stand, and one more is refused",
                ok == Game.BuildResult.SUCCESS && past == Game.BuildResult.NO_DEPOSIT && g.wellsCommitted() == sites
                        && g.minesCommitted() == 0,
                String.format("%d well(s) on %d site(s), %d mine(s) counted", g.wellsCommitted(), sites, g.minesCommitted()));

        Oil wells = g.getSectors().oil();
        double world = land.getWorldTotal(Resource.OIL);
        boolean lifts = true, conserved = true;
        double liftedRun = 0;
        for (int m = 0; m < 4; m++) {
            double before = land.getExtracted(Resource.OIL);
            quietly(() -> g.simulateMonths(1));
            double lifted = land.getExtracted(Resource.OIL) - before;
            double produced = wells.output(Good.CRUDE).produced;
            liftedRun += lifted;
            lifts &= Math.abs(lifted - produced) <= 1e-9 * Math.max(1, produced)
                    && produced <= well.makes(Good.CRUDE) * sites + 1e-9;
            conserved &= Math.abs(land.getUnowned(Resource.OIL) + land.getRemaining(Resource.OIL)
                    + land.getExtracted(Resource.OIL) - world) <= 1;
        }
        report("what the wells lift is what leaves the ground, never past their nameplate", lifts && liftedRun > 0,
                String.format("%,.0f t over four months", liftedRun));
        assertTrue("...and the world's oil is unowned + the city's remaining + its extracted, to the tonne", conserved);
    }

    /* ============================ 3. THE OIL RUNS OUT ============================ */

    static void theOilRunsOut() {
        out.println("\n--- 3. the oil runs out, and the wells retire ---");
        Game g = town("oilcheck-dry", 300, 4 * 87_120);
        BuildingsTemplate well = template(g, "Oil Well");
        LandManager land = g.getLandManager();
        int sites = 4;
        double tonnes = 2 * sites * well.makes(Good.CRUDE);
        quietly(() -> {
            land.restoreSites(Resource.OIL, sites, tonnes);
            g.buildStack(well, sites, true);
        });
        Oil wells = g.getSectors().oil();
        int month = 0;
        while (land.getOilReserveTonnes() > 0 && month < 12) {
            quietly(() -> g.simulateMonths(1));
            month++;
        }
        report("fixture: two months of four wells' nameplate in the ground, and it is gone", land.getOilReserveTonnes() == 0,
                month + " month(s)");
        quietly(() -> g.simulateMonths(1));
        report("a well over a worked-out field lifts nothing", wells.output(Good.CRUDE).produced == 0
                && land.getExtracted(Resource.OIL) == tonnes, String.format("%,.0f t taken of %,.0f", land.getExtracted(Resource.OIL), tonnes));
        double[] measure = wells.retirementDemandAndCapacity(g);
        assertTrue("...so the wells' plant is spare by any measure: no demand against their capacity",
                measure != null && measure[0] == 0 && measure[1] == wells.getCapacity(Good.CRUDE));
        BusinessInvestment.Decision d = wells.plan(g.getBusinessInvestment(), g);
        report("...and nobody drills more: the word names the deposit", !d.build && d.reason.contains("deposit"), "\"" + d.reason + "\"");
        int standing = wells.buildingsStanding(), waited = 0;
        while (wells.buildingsStanding() == standing && waited < 36) {
            quietly(() -> g.simulateMonths(1));
            waited++;
        }
        report("the wells retire, as the mines do when the ore runs out", wells.buildingsStanding() < standing,
                String.format("%d of %d standing after %d month(s)", wells.buildingsStanding(), standing, waited));
    }

    /* ============================ 4. THE REFINERY TAKES THE WELLS' CRUDE FIRST ============================ */

    static Game refineryTakesLocalCrude() {
        out.println("\n--- 4. the refinery takes the wells' crude first, and imports the rest ---");
        Game g = town("oilcheck-refinery", 600, 30 * 87_120 + 870_000 + 2 * 700_000);
        BuildingsTemplate well = template(g, "Oil Well"), refinery = template(g, "Oil Refinery");
        int few = 4;
        quietly(() -> {
            /*
             * THE REFINERY THE FIXTURE STANDS IS HELD (0.7.76): on imported
             * crude its slate is worth less abroad than the crude costs - at
             * 0.7.76 it idled once its tanks were full and the distress rule
             * shed it in its third year; since 0.7.98 it ships what it makes,
             * at a loss - and this section and the next five are about where
             * its crude comes from and where its products go, not whether a
             * refinery pays in a town of 600 houses (that is section 8's).
             */
            g.getBusinessInvestment().holdSector(Sectors.REFINING);
            g.getLandManager().restoreSites(Resource.OIL, few, 5_000_000);
            g.buildStack(well, few, true);
            g.buildStack(refinery, 1, true);
            // Two years: the town's households buy their cars, and drive (section 5).
            g.simulateMonths(24);
        });
        Oil wells = g.getSectors().oil();
        Refining refiners = g.getSectors().refining();
        Sector.Input in = refiners.input(Good.CRUDE);
        Sector.Output lifted = wells.output(Good.CRUDE);
        report("fixture: four wells lift less than the refinery runs on", lifted.produced > 0 && lifted.produced < in.bid,
                String.format("%,.0f t lifted, %,.0f t wanted", lifted.produced, in.bid));
        close("every tonne the wells lifted went to the refinery", in.boughtLocal, lifted.produced, 1e-9);
        close("...none of it shipped", wells.unitsExported(Good.CRUDE), 0, 1e-9);
        close("...and the refinery imported the rest of what it wanted", in.imported, in.bid - in.boughtLocal, 1e-9);

        // ...and wells past the refinery's run: it takes what it wants at home, and the wells ship the rest.
        BuildingsTemplate w = well;
        quietly(() -> {
            // Ground for them: the town's own has gone to what its investors built in two years.
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + (30 - few) * w.getLandSqFt());
            g.getLandManager().restoreSites(Resource.OIL, 30, 50_000_000);
            g.buildStack(w, 30 - few, true);
            g.simulateMonths(2);
        });
        report("fixture: thirty wells lift more than the refinery runs on", lifted.produced > in.bid,
                String.format("%,.0f t lifted, %,.0f t wanted", lifted.produced, in.bid));
        close("the refinery imports none of it", in.imported, 0, 1e-9);
        close("...takes all it wanted from the wells", in.boughtLocal, in.bid, 1e-9);
        close("...and the wells ship the rest abroad", wells.unitsExported(Good.CRUDE), lifted.produced - in.boughtLocal, 1e-9);

        // ...and what it makes is the slate of the crude it runs (0.7.76), at its month's crude mix since 0.7.79 (section 12).
        double run = refiners.getInputAtCapacity(Good.CRUDE);
        Refining.Slate slate = Refining.slate(run, refiners.getCrudeMix());
        boolean capacity = true;
        double room = 0, tanks = refinery.getStock() * refiners.buildingsStanding();
        for (Good p : refiners.goodsMade()) {
            capacity &= refiners.getCapacity(p) == slate.of(p) && refiners.output(p).capacity == slate.of(p);
            room += refiners.getStockCapacity(p);
        }
        report("its nameplate, good by good, is the slate of its crude, to the bit", capacity && run == 8300,
                String.format("%,.0f t: %,.0f L of petrol, %,.0f of diesel", run, refiners.getCapacity(Good.PETROL),
                        refiners.getCapacity(Good.DIESEL)));
        close("...and its tanks are shared among the products as its run is, the shares its whole tankage", room, tanks, 1e-12);
        close("...the petrol's room its share of the run", refiners.getStockCapacity(Good.PETROL),
                tanks * slate.of(Good.PETROL) / (run * Refining.CRUDE_LITRES_PER_TONNE), 1e-12);
        return g;
    }

    /* ============================ 5. THE DRIVERS AND THE RAILWAY ============================ */

    static void driversAndRailwayTakeLocalFuel(Game g) {
        out.println("\n--- 5. the drivers and the railway take the refiners' fuel first ---");
        Motoring mo = g.getMotoring();
        Refining refiners = g.getSectors().refining();
        // The drivers' fuel is petrol since 0.7.76 (batch O1), the railway's diesel.
        GoodsMarket fuel = g.getMarkets().get(Good.PETROL);
        // A month, the litre's price at the drivers' draw (6d: the last clearing's) kept.
        double priced = fuel.getLocalPrice();
        quietly(() -> g.simulateMonths(1));
        report("fixture: the refinery's tanks hold petrol and the town drives", refiners.getStock(Good.PETROL) > 0 && mo.getFuelLitres() > 0,
                String.format("%,.0f L in the tanks, %,.0f L burned", refiners.getStock(Good.PETROL), mo.getFuelLitres()));
        /*
         * THROUGH THE FORECOURTS SINCE 0.7.83 (batch O6): the grocers draw the
         * drivers' litres at the refiners' price and sell them at the pump's.
         * Until then: "the month's drivers bought every litre at home: none
         * imported" (the households' imports nothing), "...and paid the
         * refiners' price for it" (their bill the litres at it). The litres
         * its filling stations can sell (the grocers order them as the town
         * drives) go at the pump price, the rest at the queue's (QUEUE_MARGIN).
         */
        ham.citybuildersim.sectors.Retail pump = g.getSectors().retail();
        double rate = g.getEconomyManager().getTaxPolicy().effectiveSalesRate(pump);
        close("the month's drivers' petrol, all of it, the forecourts bought at home: none imported, by them or by the"
                + " households", pump.getFuelImported() + mo.getFuelImports(), 0, 1e-12);
        close("...at the refiners' price", pump.getWholesaleLitre(), priced, 1e-12);
        report("fixture: every litre the forecourts sold, at the pump or past the stations",
                pump.getPumpLitres() + pump.getQueueLitres() == mo.getFuelLitres(),
                String.format("%,.0f L at the pump, %,.0f past %d station(s)", pump.getPumpLitres(), pump.getQueueLitres(),
                        pump.stationsStanding()));
        close("...and the drivers paid the pump's price on it: the refiners' price times 1 + PUMP_MARGIN, over 1 less the"
                        + " grocers' sales tax, on what the stations can sell, and 1 + QUEUE_MARGIN on the rest", mo.getFuelBill(),
                pump.getPumpLitres() * ham.citybuildersim.sectors.Retail.pumpPrice(priced, rate)
                        + pump.getQueueLitres() * ham.citybuildersim.sectors.Retail.queuePrice(priced, rate), 1e-9);

        // A draw past the tanks, on a copy of what the refinery holds: the shelf first, the world for the rest.
        double held = refiners.getStock(Good.PETROL);
        double salesBefore = refiners.pending().localSales, toHouseholds = refiners.pending().salesToHouseholds;
        double journeys = (held * 1.5) / Motoring.LITRES_PER_JOURNEY;
        double local = fuel.getLocalPrice(), imported = fuel.importPrice();
        quietly(() -> mo.drawFuel(g, journeys));
        close("a draw past the tanks takes all the petrol they hold", refiners.getStock(Good.PETROL), 0, 1e-9);
        close("...the forecourts import the rest at the import price", pump.getFuelImported(), (held * 1.5 - held) * imported, 1e-9);
        close("...and their wholesale bill is the two together", pump.getWholesaleLitre() * mo.getFuelLitres(),
                held * local + (held * .5) * imported, 1e-9);
        close("...the shelf's part a sale to the forecourts on Refining's books, none to the households",
                refiners.pending().localSales - salesBefore + Math.abs(refiners.pending().salesToHouseholds - toHouseholds),
                held * local, 1e-9);

        // The railway: track in the oil town, carrying its crude and its fuel across the edge.
        quietly(() -> {
            g.getBusinessInvestment().holdSector(Sectors.RAIL);
            g.buildStack(template(g, "Rail Spur"), 2, true);
            g.simulateMonths(4);
        });
        // ...and one month more, the litre's price at its draw (the top of the month, the last clearing's) kept.
        double atDraw = g.getMarkets().get(Good.DIESEL).getLocalPrice();
        quietly(() -> g.simulateMonths(1));
        Rail rail = g.getSectors().rail();
        report("fixture: the railway hauls and burns diesel, with diesel in the refiners' tanks",
                rail.getHauledTonnes() > 0 && rail.getFuelBill() > 0 && refiners.getStock(Good.DIESEL) > 0,
                String.format("%,.0f t hauled, diesel %,.2fk, %,.0f L in the tanks", rail.getHauledTonnes(), rail.getFuelBill(),
                        refiners.getStock(Good.DIESEL)));
        close("the railway imported none of its diesel", rail.getFuelImported(), 0, 1e-12);
        Sector.Split bought = rail.statement().bought.getOrDefault(Good.DIESEL, new Sector.Split());
        assertTrue("...and bought no petrol", !rail.statement().bought.containsKey(Good.PETROL));
        close("...it bought it at home, on its books", bought.atHome, rail.getFuelBill(), 1e-9);
        close("...eighteen litres a tonne hauled, at the litre's price when it drew them", rail.getFuelBill(),
                rail.getHauledTonnes() * Rail.FUEL_LITRES_PER_TONNE * atDraw, 1e-9);
    }

    /* ============================ 6. THE AUDIT ============================ */

    static void theAudit(Game g) {
        out.println("\n--- 6. fuel's money audit closes, every month of a city with wells and a refinery ---");
        Refining refiners = g.getSectors().refining();
        double worstAudit = 0, worstImports = 0, worstDomestic = 0, worstGoods = 0, writtenOff = 0, shipped = 0, idled = 0;
        double ran = 0, made = 0, idledAll = 0;
        int months = 24, withImports = 0, withDomestic = 0, refusedByWhole = 0;
        boolean toTheLine = true;
        for (int m = 0; m < months; m++) {
            // Every few months the petrol is emptied, so some months the drivers import and some they do not.
            if (m % 4 == 1) refiners.setStock(Good.PETROL, 0);
            quietly(() -> g.simulateMonths(1));
            MoneyAudit.Result r = g.getLastMoneyAudit();
            worstAudit = Math.max(worstAudit, r.relative());
            // The forecourts' month the strike just banked (0.7.83): their petrol, its imported part, the drivers' bill.
            ham.citybuildersim.sectors.Retail pump = g.getSectors().retail();
            Sector.Split petrol = pump.statement().bought.getOrDefault(Good.PETROL, new Sector.Split());
            double imports = petrol.abroad;
            if (imports > 0) withImports++;
            worstImports = Math.max(worstImports, Math.abs(auditLine(r, "+ households PetrolFunded"))
                    + Math.abs(auditLine(r, "- households PetrolImports"))
                    + Math.abs(g.getHouseholdFuelImports()));
            for (Good p : refiners.goodsMade()) {
                Sector.Output o = refiners.outputRow(p);
                if (o == null) continue;
                writtenOff += o.writtenOff;
                // The run's products (0.7.98): made for home or shipped, and whether the line's whole bill, the crude among it,
                // shared by value (Sector's marginal cost, the refiners' until then) would have refused what the city did not take.
                double run = refiners.getCapacity(p) * refiners.getOperatingRate();
                if (run > 0) {
                    ran += run;
                    made += o.planned + o.exportBound;
                    idledAll += o.idled;
                    double whole = (refiners.getElectricityCost() + refiners.getWaterCost() + refiners.inputCostAtRate())
                            * refiners.costShareOf(p) / run;
                    if (run - o.planned > 0 && g.getMarkets().get(p).netExportPrice() < whole) refusedByWhole++;
                }
                if (p == Good.PETROL || p == Good.DIESEL) continue;
                shipped += o.exportBound + o.exported;
                idled += o.idled;
                toTheLine &= refiners.getStock(p) <= refiners.getStockCapacity(p) * Sector.DUMP_THRESHOLD * (1 + 1e-12);
            }
            double domestic = petrol.atHome;
            if (domestic > 0) withDomestic++;
            Sector.Split sold = pump.statement().sold.getOrDefault(Good.PETROL, new Sector.Split());
            Sector.Split refined = refiners.statement().sold.getOrDefault(Good.PETROL, new Sector.Split());
            worstDomestic = Math.max(worstDomestic, (Math.abs(refined.atHome - domestic)
                    + Math.abs(refiners.statement().salesToHouseholds) + Math.abs(sold.atHome - g.getHouseholdFuel()))
                    / Math.max(1, g.getHouseholdFuel()));
            Sectors.TradeByGood goods = g.getTradeByGood();
            double tradeImports = g.getForeignAccounts().tradeImports();
            worstGoods = Math.max(worstGoods, Math.abs(goods.bought() - tradeImports) / Math.max(1, tradeImports));
        }
        report("the audit closes every month (relative to what moved)", worstAudit < 1e-10,
                String.format("worst %.2e over %d months", worstAudit, months));
        /*
         * THE FORECOURTS' SINCE 0.7.83 (batch O6): the drivers' petrol is bought
         * by the grocers and sold to the drivers, so its imported part is
         * Retail's import, and the households' own - the audit's PetrolFunded
         * and PetrolImports - nothing. Until then: "fixture: months the drivers
         * imported and months they bought at home", "the imported part is the
         * audit's PetrolFunded and PetrolImports, to the cent".
         */
        report("fixture: months the forecourts imported the drivers' petrol and months they bought it at home",
                withImports > 0 && withDomestic > 0, withImports + " and " + withDomestic + " of " + months);
        report("the households import none of it: the audit's PetrolFunded and PetrolImports are nothing, to the cent",
                worstImports <= .011, String.format("worst %.4f", worstImports));
        /*
         * SHIPPED OR IDLED (0.7.78, batch O2): the products nobody here buys
         * fill their share of the tanks to the dump line and no further; past
         * it each line ships what it makes when the export price clears its
         * share of the line's cost (Sector.getExportBoundOutput()) and idles
         * when it does not. At FUEL's band (0.7.76) this town's lines shipped
         * 282.8M L in the 24 months; on the wholesale ladder a medium crude's
         * slate is worth less than the crude (spec-oil 6: an Oil Refinery on
         * medium crude fails its gate by 22%), so they idled - 155.2M L of
         * nameplate in the 24 months, none shipped, the crude for it bought.
         *
         * THE RUN'S PRODUCTS ALL LEAVE since 0.7.98 (batch O14; Refining,
         * THE RUN'S PRODUCTS ALL LEAVE): the crude is the run's, bought at
         * nameplate x the rate whatever a line plans (spec-oil 2.3), so a
         * refined product's marginal cost is its share of the power and water
         * alone, and what the city does not take ships (spec-oil 2.5). The
         * fixture is the condition: months in which a product's share of the
         * line's whole bill, the crude among it, was over its net export
         * price - the rule that idled it.
         */
        report("the products nobody here buys fill their tank share to the dump line and no further, shipped past it or"
                        + " idled, and the tanks write none of any product off (spec-oil 6)",
                toTheLine && shipped + idled > 0 && writtenOff == 0,
                String.format("%,.0f L shipped, %,.0f L of nameplate idled, %,.0f written off", shipped, idled, writtenOff));
        report("fixture: a product the city does not take all of, whose share of the line's whole bill - the crude among it, by"
                        + " value (Sector's marginal cost) - is over its net export price", refusedByWhole > 0,
                refusedByWhole + " product-months");
        report("...yet the run's every product leaves: made for home or shipped, none of it idled (Refining."
                        + "getMarginalCostPerUnit(): its share of the power and water alone; 0.7.98)",
                idledAll == 0 && ran > 0 && Math.abs(made - ran) <= 1e-12 * ran,
                String.format("%,.0f L of the run's %,.0f made or shipped, %,.0f idled", made, ran, idledAll));
        report("...and the domestic part is on Refining's statement, its sales to the forecourts (to the households until"
                        + " 0.7.83), and the drivers' bill Retail's sale of petrol to them", worstDomestic < 1e-9,
                String.format("worst %.2e of the bill", worstDomestic));
        report("...and the goods, fuel among them, foot to the balance of payments' imports", worstGoods < 1e-9,
                String.format("worst %.2e", worstGoods));
    }

    /* ============================ 7. NO REFINERY ============================ */

    static void noRefinery() {
        out.println("\n--- 7. with no refinery, the households pay today's bill at the world's price level ---");
        Game g = town("oilcheck-norefinery", 600, 0);
        double level = 1.137;
        quietly(() -> {
            // Jerus's level, held, and the currency held at parity with the dollar, so the level is all that moves the price.
            g.getWorldEconomy().restore(new double[] { level, 0 });
            g.getWorldEconomy().pin();
            g.getForeignAccounts().pinRate(1.0);
            g.getBusinessInvestment().holdSector(Sectors.REFINING);
            g.simulateMonths(24);
        });
        Motoring mo = g.getMotoring();
        GoodsMarket fuel = g.getMarkets().get(Good.PETROL);
        double drivers = g.getInfrastructureManager().getDrivers();
        report("fixture: the town drives, and has no refinery", drivers > 0 && g.getSectors().refining().buildingsStanding() == 0,
                String.format("%,.0f drivers", drivers));
        close("its drivers burned a month of journeys at 1.2 litres", mo.getFuelLitres(),
                drivers * TaxPolicy.JOURNEYS_A_MONTH * Motoring.LITRES_PER_JOURNEY, 1e-12);
        /*
         * THE FORECOURTS BUY IT SINCE 0.7.83 (batch O6), so the wholesale bill
         * is theirs and the drivers pay the pump's price on it. Until then
         * "...every litre imported" and the ladder premises below read the
         * drivers' bill, which was the wholesale bill; they read the
         * forecourts' now, and the drivers' is the pump's on it.
         */
        ham.citybuildersim.sectors.Retail pump = g.getSectors().retail();
        double wholesale = pump.getWholesaleLitre() * mo.getFuelLitres();
        close("...every litre imported, by the forecourts", pump.getFuelImported(), wholesale, 1e-12);
        // Since 0.7.78 (O2) a journey's litres at petrol's place on the wholesale ladder; 0.7.49's journey at the pump price before.
        double ladderBill = drivers * TaxPolicy.JOURNEYS_A_MONTH * Motoring.LITRES_PER_JOURNEY * Good.PETROL.worldImportPrice()
                * g.getForeignAccounts().getRate();
        close("...at the import price: a journey's litres at petrol's ladder price, at the rate, times the world's price level",
                wholesale, ladderBill * level, 1e-12);
        double oldBill = drivers * TaxPolicy.JOURNEYS_A_MONTH * Motoring.CAR_FUEL_PER_JOURNEY * g.getForeignAccounts().getRate();
        double cut = 1 - wholesale / (oldBill * level);
        report("...63% under the bill the pump price struck (0.7.49's journey at the rate and the level)",
                Math.round(100 * cut) == PETROL_CUT_PCT,
                String.format("%,.2fk against %,.2fk, %.2f%% less", wholesale, oldBill * level, 100 * cut));
        double rate = g.getEconomyManager().getTaxPolicy().effectiveSalesRate(pump);
        double litre = pump.getWholesaleLitre();
        close("...and the drivers pay the forecourts' price on it: the pump's on what their stations can sell, the queue's on"
                        + " the rest", mo.getFuelBill(), pump.getPumpLitres() * ham.citybuildersim.sectors.Retail.pumpPrice(litre, rate)
                        + pump.getQueueLitres() * ham.citybuildersim.sectors.Retail.queuePrice(litre, rate), 1e-12);
        close("...the level the markets were told", fuel.getExchangeRate() / g.getForeignAccounts().getRate(), level, 1e-12);
        // ...a journey's litres at the pump price on the import price since 0.7.83: the price on the sign (star O6).
        close("a journey's fuel, which the owners weigh a ride against, is a journey's litres at the pump price on the import"
                        + " price (0.7.83; at the import price until then)",
                g.getInfrastructureManager().getFuelPerJourney(), Motoring.LITRES_PER_JOURNEY
                        * ham.citybuildersim.sectors.Retail.pumpPrice(fuel.importPrice(), rate), 1e-12);
    }

    /* ============================ 8. WHEN A REFINERY IS BUILT ============================ */

    static void whenARefineryIsBuilt() {
        out.println("\n--- 8. a refinery is built for the city's own fuel or its own crude: rule 6 and the package (0.7.82) ---");
        Game g = town("oilcheck-plan", 600, 0);
        // Two years, so the town burns fuel: some, not FEED_GATE of a refinery's worth.
        quietly(() -> g.simulateMonths(24));
        Refining refiners = g.getSectors().refining();
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate refinery = template(g, "Oil Refinery");
        double gate = SpreadPlanner.FEED_GATE;
        // ...its petrol and diesel since 0.7.76 (O1): the forecast of each, against what the plant makes of the two.
        double room = 0;
        for (Good p : Refining.BOUGHT_HERE) {
            room += plans.forecast(refiners, g.getMarkets().get(p)) - refiners.getCapacity(p) - refiners.getPipeline(p);
        }
        double plantsWorth = Refining.boughtHere(Refining.slate(refinery.uses(Good.CRUDE)));
        BusinessInvestment.Decision d = refiners.plan(plans, g);
        SpreadPlanner.Candidate c = refiners.appraise(refinery, plans);
        report("a town short of less petrol and diesel than FEED_GATE of what a refinery makes of them, with no wells, is not given"
                        + " one: refused at its feed", !d.build && room > 0 && room < gate * plantsWorth && refiners.spareCrude() == 0
                        && c.failed() == SpreadPlanner.Gate.FEED,
                String.format("%,.0f L a month against %,.0f: \"%s\"", room, gate * plantsWorth, d.reason));
        // Its crude, imported: a dearer import costs the package every tonne of it, at the rate (spec-oil 2.4, K's prices).
        GoodsMarket crude = g.getMarkets().get(Good.CRUDE);
        Refining.Outlook o = refiners.outlook(plans);
        double rate = BusinessInvestment.operatingRateOf(refiners.getOperatingRate()), dear = .01, tonnes = refinery.uses(Good.CRUDE);
        report("...its package charges every tonne of its crude at the net import price, not the band's middle: a dearer import"
                        + " costs it the tonnes at the rate", o.crudeImport() == crude.netImportPrice()
                        && crude.netImportPrice() > crude.getLocalPrice()
                        && Math.abs(packageEarns(refiners, plans, g, o, 0, dear) - packageEarns(refiners, plans, g, o, 0, 0)
                        + tonnes * dear * rate) <= 1e-9 * tonnes * dear,
                String.format("%,.0f t at %.4f landed and hauled, the local price %.4f", tonnes, crude.netImportPrice(),
                        crude.getLocalPrice()));

        BuildingsTemplate well = template(g, "Oil Well");
        // Wells, ten at a time, until they lift FEED_GATE of a refinery's crude that nobody takes, then a whole refinery's.
        int[] sites = { 0 };
        boolean opened = false;
        String atGate = "";
        while (refiners.spareCrude() < tonnes && sites[0] < 300) {
            quietly(() -> {
                sites[0] += 10;
                g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 10 * well.getLandSqFt());
                g.getLandManager().restoreSites(Resource.OIL, sites[0], 50_000_000);
                g.buildStack(well, 10, true);
                if (sites[0] % 40 == 0) g.buildStack(template(g, "Coal Power Plant"), 1, true);
                g.simulateMonths(1);
            });
            if (!opened && refiners.spareCrude() >= gate * tonnes) {
                opened = refiners.appraise(refinery, plans).refusal(SpreadPlanner.Gate.FEED) == null;
                atGate = String.format("%d wells, %,.0f t spare: \"%s\"", sites[0], refiners.spareCrude(),
                        refiners.plan(plans, g).reason);
            }
        }
        report("with FEED_GATE of a refinery's crude lifted that nobody takes, the gate opens: it passes its feed", opened, atGate);
        double spare = refiners.spareCrude(), local = .01;
        Refining.Outlook own = refiners.outlook(plans);
        rate = BusinessInvestment.operatingRateOf(refiners.getOperatingRate());
        report("...and with a whole refinery's worth spare its crude is the wells', at the local price: the import price moves its"
                        + " package nothing, the local price every tonne at the rate", spare >= tonnes
                        && packageEarns(refiners, plans, g, own, 0, dear) == packageEarns(refiners, plans, g, own, 0, 0)
                        && Math.abs(packageEarns(refiners, plans, g, own, local, 0) - packageEarns(refiners, plans, g, own, 0, 0)
                        + tonnes * local * rate) <= 1e-9 * tonnes * local,
                String.format("%d wells, %,.0f t spare", sites[0], spare));
        SpreadPlanner.Candidate pkg = refiners.appraise(refinery, plans);
        report("...its estimate (what Game.consider() tests) is its share of its package's earnings, by cost", pkg.cost() > 0
                        && refiners.estimatedMonthlyProfit(refinery, plans) == pkg.earns() * plans.getCostOf(refinery, 1) / pkg.cost(),
                String.format("%,.1fk a month on %,.0fk with %d unit(s)", pkg.earns(), pkg.cost(), pkg.with().size()));
    }

    /** The Oil Refinery's package earnings at an outlook with its crude's two prices raised by `local` and `imported` (Refining.packageEstimate()). */
    static double packageEarns(Refining refiners, BusinessInvestment plans, Game g, Refining.Outlook o, double local, double imported) {
        Refining.Outlook moved = new Refining.Outlook(o.feed(), o.crude(), o.mix(), o.values(), o.room(), o.spareCrude(), o.spareMix(),
                o.crudeLocal() + local, o.crudeImport() + imported);
        RefineryFlow.Flow before = RefineryFlow.solve(moved.feed(), moved.crude(), moved.mix(), moved.values());
        return Refining.packageEstimate(moved, before, template(g, "Oil Refinery"),
                g.getBuildingManager().getTemplatesBySector(refiners.key()), refiners.planner().city(refiners, plans, g)).earns();
    }

    /* ============================ 9. ACROSS A SAVE ============================ */

    static void acrossASave(Game g) throws Exception {
        out.println("\n--- 9. the month's fuel crosses a save ---");
        // A month with both parts: the tanks left holding half the drivers' litres of petrol (0.7.76; and the railway's diesel).
        Motoring mo = g.getMotoring();
        Refining refiners = g.getSectors().refining();
        refiners.setStock(Good.PETROL, .5 * mo.getFuelLitres());
        refiners.setStock(Good.DIESEL, g.getSectors().rail().getHauledTonnes() * Rail.FUEL_LITRES_PER_TONNE);
        quietly(() -> g.simulateMonths(1));
        // ...the forecourts' since 0.7.83: their wholesale bill, from the tanks and the world (the drivers' own imports nothing).
        ham.citybuildersim.sectors.Retail pump = g.getSectors().retail();
        double wholesale = pump.getWholesaleLitre() * mo.getFuelLitres();
        report("fixture: a month with fuel from both the tanks and the world",
                pump.getFuelImported() > 0 && pump.getFuelImported() < wholesale,
                String.format("wholesale %,.2fk, %,.2fk of it imported; the drivers' bill %,.2fk", wholesale,
                        pump.getFuelImported(), mo.getFuelBill()));
        GameFiles files = FILES.get(g);
        quietly(() -> g.saveGame(10, "oil"));
        Path file = files.saveFile(10);
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        Motoring back = twin.getMotoring();
        assertTrue("the bill, its imported part and the litres load as 6d drew them",
                back.getFuelBill() == mo.getFuelBill() && back.getFuelImports() == mo.getFuelImports()
                        && back.getFuelLitres() == mo.getFuelLitres());
        ham.citybuildersim.sectors.Retail pumpBack = twin.getSectors().retail();
        assertTrue("...and the forecourts' month: the litres at the pump and past it, what the stations could sell, the two"
                        + " prices, the wholesale a litre, the bill and its imported part (0.7.83)",
                pumpBack.getPumpLitres() == pump.getPumpLitres() && pumpBack.getQueueLitres() == pump.getQueueLitres()
                        && pumpBack.getPumpCapacity() == pump.getPumpCapacity()
                        && same(pumpBack.getPumpPrice(), pump.getPumpPrice()) && same(pumpBack.getQueuePrice(), pump.getQueuePrice())
                        && same(pumpBack.getWholesaleLitre(), pump.getWholesaleLitre())
                        && pumpBack.getFuelBill() == pump.getFuelBill() && pumpBack.getFuelImported() == pump.getFuelImported());
        quietly(() -> { g.simulateMonths(1); twin.simulateMonths(1); });
        report("...and a month on, both cities' households pay the same fuel, as much of it abroad by the forecourts",
                twin.getHouseholdFuel() == g.getHouseholdFuel() && twin.getHouseholdFuelImports() == g.getHouseholdFuelImports()
                        && twin.getSectors().retail().getFuelImported() == g.getSectors().retail().getFuelImported(),
                String.format("%,.4fk / %,.4fk", twin.getHouseholdFuel(), g.getHouseholdFuel()));

        // A save from before the fuel was drawn: no householdFuel key.
        String json = Files.readString(file);
        com.google.gson.JsonObject o = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
        o.remove("householdFuel");
        Files.writeString(file, new com.google.gson.GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(o));
        Game old = new Game(files);
        quietly(() -> old.loadGameSave(10));
        Motoring derived = old.getMotoring();
        double struck = old.getInfrastructureManager().getDrivers() * TaxPolicy.JOURNEYS_A_MONTH
                * old.getInfrastructureManager().getFuelPerJourney();
        assertTrue("a save from before 0.7.62 derives the month as it struck it: drivers x journeys x a journey's fuel",
                derived.getFuelBill() == struck && struck > 0);
        assertTrue("...every litre imported", derived.getFuelImports() == derived.getFuelBill());
    }

    /* ============================ 10. A SAVE FROM BEFORE 0.7.76 ============================ */

    /** A JSON map of numbers by good name: the nine folded into one FUEL figure, as a 0.7.75 refinery made only FUEL. */
    static void foldUnits(com.google.gson.JsonObject parent, String key) {
        if (parent == null || !parent.has(key) || !parent.get(key).isJsonObject()) return;
        com.google.gson.JsonObject m = parent.getAsJsonObject(key);
        double fuel = 0;
        boolean any = false;
        for (Good p : PRODUCTS) {
            if (!m.has(p.name())) continue;
            fuel += m.get(p.name()).getAsDouble();
            m.remove(p.name());
            any = true;
        }
        if (any) m.addProperty(FuelSplit.FUEL, fuel);
    }

    /** ...and a map of money by good name, each side apart. */
    static void foldMoney(com.google.gson.JsonObject parent, String key) {
        if (parent == null || !parent.has(key) || !parent.get(key).isJsonObject()) return;
        com.google.gson.JsonObject m = parent.getAsJsonObject(key);
        double home = 0, abroad = 0;
        boolean any = false;
        for (Good p : PRODUCTS) {
            if (!m.has(p.name())) continue;
            com.google.gson.JsonObject s = m.getAsJsonObject(p.name());
            home += s.get("atHome").getAsDouble();
            abroad += s.get("abroad").getAsDouble();
            m.remove(p.name());
            any = true;
        }
        if (!any) return;
        com.google.gson.JsonObject f = new com.google.gson.JsonObject();
        f.addProperty("atHome", home);
        f.addProperty("abroad", abroad);
        m.add(FuelSplit.FUEL, f);
    }

    static com.google.gson.JsonObject sectorOf(com.google.gson.JsonObject save, String key) {
        for (com.google.gson.JsonElement e : save.getAsJsonArray("sectors")) {
            if (key.equals(e.getAsJsonObject().get("key").getAsString())) return e.getAsJsonObject();
        }
        return null;
    }

    static com.google.gson.JsonObject marketOf(com.google.gson.JsonObject save, String good) {
        for (com.google.gson.JsonElement e : save.getAsJsonArray("markets")) {
            if (good.equals(e.getAsJsonObject().get("good").getAsString())) return e.getAsJsonObject();
        }
        return null;
    }

    static double money(com.google.gson.JsonObject sector, String part, String map, String side) {
        com.google.gson.JsonObject m = sector.getAsJsonObject(part).getAsJsonObject(map);
        return m.has(FuelSplit.FUEL) ? m.getAsJsonObject(FuelSplit.FUEL).get(side).getAsDouble() : 0;
    }

    /**
     * A FORMAT-33 SAVE (0.7.75's shape), made from the oil town by folding the
     * nine products back into FUEL - its market (PETROL's price, the nine's
     * strike and history summed), every sector's maps, and the goods held
     * four wide - loads with each FUEL figure split into petrol and diesel
     * by the drivers' share of the month's litres, the pairs summing to the
     * bit, the railway's all diesel; nothing a money figure; the audit closes
     * for a year; saved again, in this build's format, it loads back to the
     * cent.
     */
    static Game theSplit(Game g) throws Exception {
        out.println("\n--- 10. a save from before 0.7.76 has its FUEL split into petrol and diesel, no money moving ---");
        quietly(() -> g.simulateMonths(1));
        GameFiles files = FILES.get(g);
        quietly(() -> g.saveGame(10, "oil"));
        Path file = files.saveFile(10);
        com.google.gson.JsonObject o = com.google.gson.JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        o.addProperty("saveFormat", FuelSplit.LAST_FUEL_FORMAT);
        // The market: one FUEL, at petrol's price, the nine's strike summed.
        com.google.gson.JsonArray markets = new com.google.gson.JsonArray();
        com.google.gson.JsonObject fuel = new com.google.gson.JsonObject();
        double flow = 0, stock = 0, demand = 0;
        double[] taken = null;
        for (com.google.gson.JsonElement e : o.getAsJsonArray("markets")) {
            com.google.gson.JsonObject m = e.getAsJsonObject();
            Good gd = Good.byName(m.get("good").getAsString());
            if (gd == null || !java.util.Arrays.asList(PRODUCTS).contains(gd)) { markets.add(m); continue; }
            if (gd == Good.PETROL) fuel.addProperty("price", m.get("price").getAsDouble());
            flow += m.get("flow").getAsDouble();
            stock += m.get("stock").getAsDouble();
            demand += m.get("demand").getAsDouble();
            com.google.gson.JsonArray t = m.getAsJsonArray("taken");
            if (taken == null) taken = new double[t.size()];
            for (int i = 0; i < t.size(); i++) taken[i] += t.get(i).getAsDouble();
        }
        fuel.addProperty("good", FuelSplit.FUEL);
        fuel.addProperty("flow", flow);
        fuel.addProperty("stock", stock);
        fuel.addProperty("demand", demand);
        com.google.gson.JsonArray t = new com.google.gson.JsonArray();
        for (double v : taken) t.add(v);
        fuel.add("taken", t);
        markets.add(fuel);
        o.add("markets", markets);
        // Every sector's maps.
        for (com.google.gson.JsonElement e : o.getAsJsonArray("sectors")) {
            com.google.gson.JsonObject s = e.getAsJsonObject();
            for (String k : new String[] { "stock", "pantry", "pantryUsed", "exported", "imported" }) foldUnits(s, k);
            com.google.gson.JsonObject ledger = s.has("ledger") ? s.getAsJsonObject("ledger") : null;
            foldUnits(ledger, "unitsSold");
            foldUnits(ledger, "unitsBought");
            foldMoney(ledger, "sold");
            foldMoney(ledger, "bought");
            com.google.gson.JsonObject statement = s.has("statement") ? s.getAsJsonObject("statement") : null;
            foldMoney(statement, "sold");
            foldMoney(statement, "bought");
        }
        // The goods held, four wide: the nine's units in FUEL's slot.
        com.google.gson.JsonArray na = o.getAsJsonArray("nationalAccounts"), four = new com.google.gson.JsonArray();
        int fuelAt = FuelSplit.HELD_AT + NationalAccounts.HELD_WITH_FUEL - 1;
        double held = 0;
        for (int i = 0; i < na.size(); i++) {
            if (i < fuelAt) four.add(na.get(i)); else held += na.get(i).getAsDouble();
        }
        four.add(held);
        o.add("nationalAccounts", four);
        Files.writeString(file, new com.google.gson.GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(o));

        com.google.gson.JsonObject refJson = sectorOf(o, Sectors.REFINING), railJson = sectorOf(o, Sectors.RAIL);
        double fuelStock = refJson.getAsJsonObject("stock").has(FuelSplit.FUEL)
                ? refJson.getAsJsonObject("stock").get(FuelSplit.FUEL).getAsDouble() : 0;
        double drivers = o.getAsJsonArray("householdFuel").get(2).getAsDouble();
        double railway = railJson.getAsJsonObject("extras").get("hauled").getAsDouble() * Rail.FUEL_LITRES_PER_TONNE;
        double p = drivers / (drivers + railway);
        report("fixture: a format-33 save, FUEL in the refiners' tanks, its market and both sides' books, burned by the"
                        + " drivers and the railway",
                fuelStock > 0 && drivers > 0 && railway > 0 && money(railJson, "statement", "bought", "atHome") > 0
                        && money(refJson, "statement", "sold", "atHome") > 0 && held > 0,
                String.format("%,.0f L in the tanks; the drivers %,.0f L, the railway %,.0f: petrol %.4f", fuelStock, drivers,
                        railway, p));

        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        // ...its refinery held, as the town's was (section 4): a hold is the fixture's, not the save's.
        twin.getBusinessInvestment().holdSector(Sectors.REFINING);
        Refining refiners = twin.getSectors().refining();
        double petrolStock = refiners.getStock(Good.PETROL), dieselStock = refiners.getStock(Good.DIESEL);
        assertTrue("the refiners' FUEL is petrol at the drivers' share of the month's litres (to its ulp) and diesel the rest:"
                        + " the two sum to it to the bit",
                Math.abs(petrolStock - fuelStock * p) <= Math.ulp(fuelStock * p) && petrolStock + dieselStock == fuelStock);
        Sector.Split sold = refiners.statement().sold.get(Good.PETROL), soldD = refiners.statement().sold.get(Good.DIESEL);
        Sector.Split bought = twin.getSectors().rail().statement().bought.get(Good.DIESEL);
        assertTrue("...the month's sales, home and abroad, split the same way and summing to the bit",
                sold != null && soldD != null
                        && sold.atHome + soldD.atHome == money(refJson, "statement", "sold", "atHome")
                        && sold.abroad + soldD.abroad == money(refJson, "statement", "sold", "abroad")
                        && Math.abs(sold.atHome - money(refJson, "statement", "sold", "atHome") * p) <= Math.ulp(sold.atHome));
        assertTrue("...and the railway's purchases all diesel: it burns nothing else",
                bought != null && bought.atHome == money(railJson, "statement", "bought", "atHome")
                        && bought.abroad == money(railJson, "statement", "bought", "abroad")
                        && !twin.getSectors().rail().statement().bought.containsKey(Good.PETROL));
        GoodsMarket mp = twin.getMarkets().get(Good.PETROL), md = twin.getMarkets().get(Good.DIESEL);
        double[] tp = mp.getTakenHistory(), td = md.getTakenHistory();
        boolean history = tp.length == taken.length && td.length == taken.length;
        for (int i = 0; history && i < taken.length; i++) {
            history = tp[i] + td[i] == taken[i] && Math.abs(tp[i] - taken[i] * p) <= Math.ulp(taken[i] * p);
        }
        boolean prices = mp.getLocalPrice() == fuel.get("price").getAsDouble() && md.getLocalPrice() == mp.getLocalPrice();
        boolean strike = mp.getDemand() + md.getDemand() == demand && mp.getSupplyStock() + md.getSupplyStock() == stock
                && mp.getSupplyFlow() + md.getSupplyFlow() == flow;
        report("...the market's price kept for both, its strike and every month of its history split, summing to the bit",
                prices && strike && history, String.format("price %s, strike %s, history %s (%d months)", prices, strike, history,
                        taken.length));
        double[] last = twin.getEconomyManager().getNationalAccounts().getLastHeldUnits();
        boolean zeros = true;
        for (int i = 5; i < last.length; i++) zeros &= last[i] == 0;
        assertTrue("...and the goods held twelve wide: FUEL's slot petrol's and diesel's, summing to the bit, the other seven"
                        + " at a known zero",
                last.length == NationalAccounts.HELD.length && last[3] + last[4] == held
                        && Math.abs(last[3] - held * p) <= Math.ulp(held * p) && zeros);
        double price = fuel.get("price").getAsDouble();
        close("no money moved: the twin's cash is the save's, to the cent", twin.getCash(), o.get("cash").getAsDouble(), .01);
        close("...and its refiners' petrol and diesel are worth what their FUEL was, at its price, to the cent",
                (petrolStock + dieselStock) * price, fuelStock * price, .01);

        // A year on: the audit closes every month.
        double past = 0;
        for (Good p2 : Refining.BOUGHT_HERE) past += Math.max(0, refiners.getStock(p2) - refiners.getStockCapacity(p2));
        double worst = 0, shipped = 0, wrote = 0;
        boolean fits = true;
        for (int m = 0; m < 12; m++) {
            quietly(() -> twin.simulateMonths(1));
            worst = Math.max(worst, twin.getLastMoneyAudit().relative());
            for (Good p2 : refiners.goodsMade()) {
                Sector.Output row = refiners.outputRow(p2);
                if (row == null) continue;
                wrote += row.writtenOff;
                if (m == 0 && (p2 == Good.PETROL || p2 == Good.DIESEL)) shipped += row.exported;
                fits &= refiners.getStock(p2) <= refiners.getStockCapacity(p2);
            }
        }
        report("the converted city's audit closes for a year (relative to what moved)", worst < 1e-10,
                String.format("worst %.2e", worst));
        report("...its tanks shared as its run is (2.3): what the split put past petrol's and diesel's shares leaves the"
                        + " first month - sold at home, the rest shipped - and nothing is written off",
                past > 0 && fits && wrote == 0,
                String.format("%,.0f L past the shares, %,.0f shipped, %,.0f written off", past, shipped, wrote));

        // Saved again, in this build's format (34 at 0.7.76), and loaded back.
        quietly(() -> twin.saveGame(10, "oil"));
        String json = Files.readString(file);
        com.google.gson.JsonObject again = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(10));
        back.getBusinessInvestment().holdSector(Sectors.REFINING);
        boolean same = back.getCash() == twin.getCash()
                && back.getSectors().refining().getStock(Good.PETROL) == refiners.getStock(Good.PETROL)
                && back.getSectors().refining().getStock(Good.DIESEL) == refiners.getStock(Good.DIESEL)
                && back.getMarkets().get(Good.DIESEL).getDemand() == twin.getMarkets().get(Good.DIESEL).getDemand();
        report("saved as format " + GameVersion.SAVE_FORMAT + " (no FUEL in it), it loads back the same, cash and tanks to the bit",
                again.get("saveFormat").getAsInt() == GameVersion.SAVE_FORMAT && !json.contains("\"" + FuelSplit.FUEL + "\"") && same,
                String.format("cash %,.2f / %,.2f", back.getCash(), twin.getCash()));
        quietly(() -> { twin.simulateMonths(1); back.simulateMonths(1); });
        close("...and a month on, the same to the cent", back.getCash(), twin.getCash(), .01);
        return back;
    }

    /* ============================ 11. A REFINERY THAT CLOSES ============================ */

    /**
     * Its refinery bought out and pulled down (the player's hand, so no
     * planner decides it): the next month its tanks are gone, and what they
     * held - the dead stock of the products nobody here buys among it - is
     * shipped at the export price, not written off.
     */
    static void aRefineryCloses(Game g) {
        out.println("\n--- 11. a refinery that closes ships what its tanks held ---");
        Refining refiners = g.getSectors().refining();
        double held = 0;
        for (Good p : refiners.goodsMade()) held += refiners.getStock(p);
        BuildingsTemplate refinery = template(g, "Oil Refinery");
        g.setCashForTest(g.getCash() + 1e9);
        boolean bought = quietlyGet(() -> g.buyOutAndDemolish(refinery, 1));
        double[] shipped = { 0 }, wrote = { 0 };
        for (int m = 0; m < 2; m++) {
            quietly(() -> g.simulateMonths(1));
            for (Good p : refiners.goodsMade()) {
                Sector.Output row = refiners.outputRow(p);
                if (row == null) continue;
                shipped[0] += row.exported;
                wrote[0] += row.writtenOff;
            }
        }
        double left = 0;
        for (Good p : refiners.goodsMade()) left += refiners.getStock(p);
        report("fixture: the town's one refinery, its tanks holding its products, bought out and gone", bought && held > 0
                        && refiners.buildingsStanding() == 0,
                String.format("%,.0f L held, %d standing", held, refiners.buildingsStanding()));
        report("...its tanks are empty, what they held sold at home or shipped, and nothing written off", left == 0
                        && shipped[0] > 0 && wrote[0] == 0,
                String.format("%,.0f L shipped, %,.0f left, %,.0f written off", shipped[0], left, wrote[0]));
    }

    /* ============================ 12. CRUDE BY GRADE (0.7.79) ============================ */

    /**
     * The world whose founding site's nearest oil field in the sea is heavy and shallow enough for a jacket: 518, its field 3.5 km
     * out - heavy, 12 sites (a jacket's slots), 58 m deep (fixW1-notes.md, the seed scan) - what section 12's fixtures buy toward.
     * To 0.7.98 709,115,276 (spec-oil 2.7's third seed: 2.6 km out, 8 sites, 91 m deep); since 0.7.99 (batch W1) the deposits are
     * fewer and bigger, at least World.FIELD_SCALE sites a field, and that world's nearest lies 103 km out, 332 m deep.
     */
    static final long SEA_OIL_SEED = 518;

    /** Tonnes of oil a fixture's centre is handed by fiat ahead of the field, so a lift crosses from it into the field: 1,000. */
    static final double FIAT_TONNES = 1_000;

    /** The oil field in the sea nearest a world's founding site, among the nine cells round it; null when there is none. */
    static Deposit nearestSeaOil(World w) {
        long sx = w.foundingX(), sy = w.foundingY(), cx = sx / World.CELL, cy = sy / World.CELL;
        Deposit best = null;
        double far = Double.MAX_VALUE;
        for (long y = cy - 1; y <= cy + 1; y++) {
            for (long x = cx - 1; x <= cx + 1; x++) {
                if (x < 0 || y < 0 || x >= World.CELLS || y >= World.CELLS) continue;
                for (Deposit d : w.fieldsInCell((int) (y * World.CELLS + x), Resource.OIL)) {
                    double r = Math.hypot(d.x() - sx, d.y() - sy);
                    if (w.depthAt(d.x(), d.y()) > 0 && r < far) {
                        far = r;
                        best = d;
                    }
                }
            }
        }
        return best;
    }

    /** The grades of the oil from `from` to `to` tonnes into the city's, by its runs (LandManager.oilRuns()), in Deposit.Grade's order. */
    static double[] gradesBetween(double[][] runs, double from, double to) {
        double[] out = new double[Deposit.Grade.values().length];
        double start = 0;
        for (double[] r : runs) {
            double part = Math.min(r[0], to) - Math.max(start, from);
            if (part > 0) out[(int) r[1]] += part;
            start = r[0];
        }
        return out;
    }

    static void crudeByGrade() throws Exception {
        out.println("\n--- 12. a field's crude is light, medium or heavy, and the refinery runs its month's mix (0.7.79) ---");
        int light = Deposit.Grade.LIGHT.ordinal(), medium = Deposit.Grade.MEDIUM.ordinal(), heavy = Deposit.Grade.HEAVY.ordinal();
        double litres = Refining.CRUDE_LITRES_PER_TONNE;

        // The columns: the research's 1.1, each normalised.
        double[] brent = { 4.1, 8.4, 15.9, 13.9, 25.6, 21.3, 11.4 }, maya = { 0.3, 15.3 / 3, 15.3 * 2 / 3, 13.8, 9.4, 24.3, 36.9 };
        boolean barrels = true, columns = true;
        for (double[] c : Refining.CUTS) {
            double s = 0;
            for (double v : c) s += v;
            barrels &= Math.abs(s - 1) <= 1e-15;
        }
        double sb = 0, sm = 0;
        for (int i = 0; i < brent.length; i++) { sb += brent[i]; sm += maya[i]; }
        for (int i = 0; i < brent.length; i++) {
            columns &= Math.abs(Refining.CUTS[light][i] - brent[i] / sb) <= 1e-15 && Math.abs(Refining.CUTS[heavy][i] - maya[i] / sm) <= 1e-15;
        }
        assertTrue("each grade's cuts are a barrel, and medium's are O1's MEDIUM_CUTS", barrels && Refining.CUTS[medium] == Refining.MEDIUM_CUTS);
        assertTrue("...light's Brent's column [R1] and heavy's Maya's [R2], its naphtha split one to two as the blend's", columns);

        // The slates.
        Refining.Slate m = Refining.slate(1, Refining.MEDIUM_MIX), l = Refining.slate(1, Refining.mixOf(Deposit.Grade.LIGHT)),
                h = Refining.slate(1, Refining.mixOf(Deposit.Grade.HEAVY));
        double[] cm = Refining.MEDIUM_CUTS, cl = Refining.CUTS[light], ch = Refining.CUTS[heavy];
        assertTrue("on medium crude alone a slate is O1's to the bit: a tonne's petrol its run x 1 x the light naphtha's cut",
                m.of(Good.PETROL) == litres * 1.0 * cm[Refining.CUT_LIGHT_NAPHTHA] && m.burned() == 0
                        && Refining.slate(8300, Refining.MEDIUM_MIX).products().equals(Refining.slate(8300).products()));
        close("a tonne of light crude makes 97 L of petrol...", l.of(Good.PETROL), litres * cl[Refining.CUT_LIGHT_NAPHTHA], 1e-12);
        close("...and 252 L of diesel, its 296 less the third of its residue it cuts, none burned",
                l.of(Good.DIESEL) + l.burned(), litres * (cl[Refining.CUT_DIESEL] - cl[Refining.CUT_RESIDUE] / Refining.RESIDUE_PER_DIESEL), 1e-12);
        close("a tonne of heavy crude burns 101 L of residue its diesel cannot cut, three to one", h.burned(),
                litres * (ch[Refining.CUT_RESIDUE] - Refining.RESIDUE_PER_DIESEL * ch[Refining.CUT_DIESEL]), 1e-12);
        report("...its diesel all spent cutting, held at nothing", h.of(Good.DIESEL) >= 0 && h.of(Good.DIESEL) <= 1e-12,
                String.format("%.3e L", h.of(Good.DIESEL)));
        double third = 1.0 / 3;
        Refining.Slate mixed = Refining.slate(1, new double[] { third, third, third });
        boolean balance = true;
        for (Refining.Slate s : new Refining.Slate[] { l, m, h, mixed }) {
            double made = s.burned();
            for (double v : s.products().values()) made += v;
            balance &= Math.abs(made - litres) <= 1e-12 * litres;
        }
        assertTrue("the litres balance on every grade and on a third of each", balance);
        report("light crude makes more of what the city buys than medium, and heavy less",
                Refining.boughtHere(l) > Refining.boughtHere(m) && Refining.boughtHere(m) > Refining.boughtHere(h),
                String.format("%.1f, %.1f and %.1f L of petrol and diesel a tonne", Refining.boughtHere(l), Refining.boughtHere(m), Refining.boughtHere(h)));

        // The month's mix, pure.
        double[] lift = { 100, 0, 200 };
        double[] mix = Refining.monthsMix(300, lift, 600);
        assertTrue("a month's mix: nothing bought, or only imports, is medium to the bit",
                java.util.Arrays.equals(Refining.monthsMix(0, lift, 0), Refining.MEDIUM_MIX)
                        && java.util.Arrays.equals(Refining.monthsMix(0, lift, 900), Refining.MEDIUM_MIX));
        assertTrue("...local crude at the lift's grades and imports at medium, each a share of what was bought",
                Math.abs(mix[light] - 100.0 / 900) <= 1e-15 && Math.abs(mix[medium] - 600.0 / 900) <= 1e-15
                        && Math.abs(mix[heavy] - 200.0 / 900) <= 1e-15
                        && java.util.Arrays.equals(Refining.monthsMix(300, new double[3], 600), Refining.MEDIUM_MIX));

        // A land office on the sea-oil world, buying toward its field in the sea.
        World w = World.of(SEA_OIL_SEED);
        Deposit sea = nearestSeaOil(w);
        report("fixture: the world's nearest oil field to its site is in the sea, heavy", sea != null && sea.grade() == Deposit.Grade.HEAVY,
                sea == null ? "none" : String.format("%d sites, %,.0f t, %.0f m deep, %.2f km out", sea.sites(), sea.amount(),
                        w.depthAt(sea.x(), sea.y()), Math.hypot(sea.x() - w.foundingX(), sea.y() - w.foundingY()) * World.PLOT_M / 1000));
        if (sea == null) return;
        LandManager lm = new LandManager(() -> ForeignAccounts.OPENING_RATE, () -> 1.0, () -> SEA_OIL_SEED, () -> 0);
        lm.updateMarket(0);
        CityLand land = lm.getCityLand();
        int purchases = 0;
        while (!land.ownsPlot(sea.x(), sea.y()) && purchases < 200) {
            LandParcel p = MiningCheck.nearestOffer(lm.getMarket(), sea.x(), sea.y());
            if (p == null || !(lm.buyParcel(p.getId(), Double.MAX_VALUE, 0) > 0)) break;
            purchases++;
        }
        java.util.List<java.util.List<CityLand.Held>> held = land.heldFields(Resource.OIL);
        report("fixture: the office buys the ground under it, and the centre holds no oil field",
                land.ownsPlot(sea.x(), sea.y()) && held.get(0).isEmpty(), purchases + " purchase(s)");
        long seaSites = 0, all = lm.getSites(Resource.OIL);
        for (java.util.List<CityLand.Held> hs : held) for (CityLand.Held f : hs) if (w.depthAt(f.field().x(), f.field().y()) > 0) seaSites += f.sites();
        report("a field's sites are sea sites when its centre plot is sea, the rest dry, and the two are the city's sites",
                lm.getSites(Resource.OIL, false) == seaSites && seaSites >= sea.sites()
                        && lm.getSites(Resource.OIL, true) + lm.getSites(Resource.OIL, false) == all,
                String.format("%d dry, %d sea of %d", lm.getSites(Resource.OIL, true), lm.getSites(Resource.OIL, false), all));

        // Its oil as it is worked out, a pool apart (0.7.93): the centre's by fiat the ground pool's, the field the sea's.
        double bought = lm.getOwnedAmount(Resource.OIL);
        lm.restoreSites(Resource.OIL, (int) all, bought + FIAT_TONNES);
        double[][] runs = lm.oilRuns(), seaRuns = lm.oilRunsAtSea();
        report("the oil is laid out as it is worked out, each pool on its own fields (0.7.93): the ground's the centre's 1,000 t by"
                        + " fiat, medium as the world's crude; the sea's the field's, heavy (until then one pool, the fiat first)",
                runs.length >= 1 && runs[0][0] == FIAT_TONNES && runs[0][1] == medium && runs[runs.length - 1][0] == lm.getOilOwnedOnGround()
                        && seaRuns.length >= 1 && seaRuns[0][1] == heavy && seaRuns[0][0] == sea.amount()
                        && seaRuns[seaRuns.length - 1][0] == lm.getOilOwnedAtSea()
                        && lm.getOilOwnedOnGround() + lm.getOilOwnedAtSea() == lm.getOwnedAmount(Resource.OIL),
                runs.length + " run(s) on the ground ending at " + String.format("%,.0f t", runs.length == 0 ? 0 : runs[runs.length - 1][0])
                        + ", " + seaRuns.length + " at sea ending at " + String.format("%,.0f t", seaRuns.length == 0 ? 0 : seaRuns[seaRuns.length - 1][0]));
        double world = lm.getWorldTotal(Resource.OIL);
        double got = lm.extractOil(1_500);
        double[] graded = lm.getOilLiftedByGrade();
        report("a land well's lift takes the ground pool's alone: 1,500 t asked, its 1,000 t lifted, medium (until 0.7.93 the one"
                        + " pool's 1,000 t medium and 500 t heavy)",
                got == FIAT_TONNES && graded[medium] == FIAT_TONNES && graded[heavy] == 0 && graded[light] == 0 && lm.getOilLeftOnGround() == 0,
                String.format("%.1f t: %.1f / %.1f / %.1f t", got, graded[light], graded[medium], graded[heavy]));
        double atSea = lm.extractOilAtSea(500);
        graded = lm.getOilLiftedByGrade();
        report("...and a platform's the offshore pool's: 500 t of the field's heavy, graded by where the sea's E stood, to the tonne",
                atSea == 500 && graded[heavy] == 500 && graded[medium] == FIAT_TONNES && graded[light] == 0,
                String.format("%.1f / %.1f / %.1f t", graded[light], graded[medium], graded[heavy]));
        lm.extractOilAtSea(415);
        graded = lm.getOilLiftedByGrade();
        assertTrue("...a month's lifts add up, all of the sea's the field's grade", graded[heavy] == 915 && graded[medium] == FIAT_TONNES);
        assertTrue("...nothing moves but the two pools' E: unowned + remaining + extracted is the world's, to the tonne",
                Math.abs(lm.getUnowned(Resource.OIL) + lm.getRemaining(Resource.OIL) + lm.getExtracted(Resource.OIL) - world) <= 1
                        && lm.getExtracted(Resource.OIL) == 1_915 && lm.getOilExtractedOnGround() == FIAT_TONNES
                        && lm.getOilExtractedAtSea() == 915);
        lm.clearMonth();
        assertTrue("...and the month's grades clear with its flows", java.util.Arrays.equals(lm.getOilLiftedByGrade(), new double[3]));

        // A refinery on the same world: its wells on the heavy field, imports for the rest.
        GameFiles files = GameFiles.scratch("oilcheck-grades");
        Game g = new Game(files);
        BuildingsTemplate[] t = new BuildingsTemplate[2];
        quietly(() -> {
            g.newGame(Founding.defaults().withWorldSeed(SEA_OIL_SEED));
            g.setCashForTest(Founding.WEALTHY_CASH);
            BuildingManager b = g.getBuildingManager();
            t[0] = b.getTemplateByName("Oil Well");
            t[1] = b.getTemplateByName("Oil Refinery");
            String[] names = { "House", "Convenience Store", "Paved Road", "Coal Power Plant", "Water Treatment Plant", "Construction Depot" };
            int[] counts = { 300, 6, 20, 2, 1, 4 };
            double ground = t[0].getLandSqFt() * sea.sites() + t[1].getLandSqFt();
            for (int i = 0; i < names.length; i++) ground += b.getTemplateByName(names[i]).getLandSqFt() * counts[i];
            g.getLandManager().setOwnedSqFt(LandManager.STARTING_SQ_FT + ground * 1.25);
            for (int i = 0; i < names.length; i++) g.buildStack(b.getTemplateByName(names[i]), counts[i], true);
            g.getBusinessInvestment().holdSector(Sectors.REFINING);
            g.getBusinessInvestment().holdSector(Sectors.OIL);
        });
        LandManager glm = g.getLandManager();
        int pushes = 0;
        while (!g.getCityLand().ownsPlot(sea.x(), sea.y()) && pushes < 200) {
            LandParcel p = MiningCheck.nearestOffer(glm.getMarket(), sea.x(), sea.y());
            if (p == null) break;
            g.setCashForTest(g.getCash() + p.localPrice(g.getForeignAccounts().getRate()));
            if (!quietlyGet(() -> g.buyLandParcel(p.getId()))) break;
            pushes++;
        }
        /*
         * ITS WELLS ON THE FIELD'S PLATFORM (0.7.93): the field's crude is the
         * offshore pool's since the city's oil is two pools (LandManager's THE
         * TWO OIL POOLS), and only a platform's wells lift it - so an Offshore
         * Platform stands on the field (58 m deep, WellCheck 8) and a Platform
         * Well in each of its slots, one a site. From 0.7.84 the wells were
         * land wells on dry sites handed to the centre with none of the oil,
         * lifting the one pool, the field's; until then on its own sites.
         */
        int wellsOn = glm.getOilSites();
        double fieldTonnes = glm.getOwnedAmount(Resource.OIL);
        BuildingsTemplate jacket = g.getBuildingManager().getTemplateByName("Offshore Platform"),
                platformWell = g.getBuildingManager().getTemplateByName("Platform Well");
        g.setCashForTest(Founding.WEALTHY_CASH);
        Game.BuildResult jacketBuilt = quietlyGet(() -> g.buildStack(jacket, 1, true));
        Game.BuildResult wellsBuilt = quietlyGet(() -> g.buildStack(platformWell, wellsOn, true));
        Game.BuildResult refineryBuilt = quietlyGet(() -> g.buildStack(t[1], 1, true));
        double[][] gruns = glm.oilRunsAtSea();
        report("fixture: a town on that world owns the field, a platform on it with a well in each of its slots, and a refinery"
                        + " standing, all its oil heavy - the offshore pool's",
                jacketBuilt == Game.BuildResult.SUCCESS && wellsBuilt == Game.BuildResult.SUCCESS && refineryBuilt == Game.BuildResult.SUCCESS
                        && gruns.length == 1 && gruns[0][1] == heavy && glm.getSites(Resource.OIL, false) == wellsOn
                        && glm.getOilOwnedAtSea() == fieldTonnes && g.getSectors().oil().platformWellsInSlots() == wellsOn,
                String.format("%d purchase(s), %d well(s), %s / %s / %s", pushes, wellsOn, jacketBuilt, wellsBuilt, refineryBuilt));
        Refining refiners = g.getSectors().refining();
        // A month for the wells and the refinery to start, then the month measured.
        quietly(() -> g.simulateMonths(2));
        Sector.Input in = refiners.input(Good.CRUDE);
        double lifted = g.getSectors().oil().output(Good.CRUDE).produced;
        double[] struck = refiners.getCrudeMix();
        report("fixture: the refinery took the wells' heavy crude and imported the rest",
                in.boughtLocal > 0 && in.imported > 0 && lifted > 0,
                String.format("%,.0f t local of %,.0f lifted, %,.0f t imported", in.boughtLocal, lifted, in.imported));
        assertTrue("its month's mix is its local crude at the lift's grade and its imports at medium, to the bit",
                java.util.Arrays.equals(struck, Refining.monthsMix(in.boughtLocal, new double[] { 0, 0, lifted }, in.imported)));
        quietly(() -> g.simulateMonths(1));
        double run = refiners.getInputAtCapacity(Good.CRUDE);
        boolean nextMonth = true;
        for (Good p : refiners.goodsMade()) nextMonth &= refiners.output(p).capacity == Refining.slate(run, struck).of(p);
        report("...and next month's nameplate is the slate of its run at that mix, good by good, to the bit - less petrol than"
                        + " medium crude's",
                nextMonth && refiners.output(Good.PETROL).capacity < Refining.slate(run).of(Good.PETROL),
                String.format("%.3f light, %.3f medium, %.3f heavy: %,.0f L of petrol (medium %,.0f)", struck[light], struck[medium], struck[heavy],
                        refiners.output(Good.PETROL).capacity, Refining.slate(run).of(Good.PETROL)));
        double[] before = refiners.getCrudeMix();
        quietly(() -> g.saveGame(10, "grades"));
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        assertTrue("the mix crosses a save, to the bit", twin.getLoadFailure() == null
                && java.util.Arrays.equals(twin.getSectors().refining().getCrudeMix(), before) && before[heavy] > 0);
        Path file = files.saveFile(10);
        com.google.gson.JsonObject o = com.google.gson.JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        com.google.gson.JsonObject extras = sectorOf(o, Sectors.REFINING).getAsJsonObject("extras");
        boolean named = extras != null && extras.has("crudeMix.LIGHT") && extras.has("crudeMix.MEDIUM") && extras.has("crudeMix.HEAVY");
        if (extras != null) for (Deposit.Grade gr : Deposit.Grade.values()) extras.remove("crudeMix." + gr.name());
        Files.writeString(file, new com.google.gson.GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(o));
        Game old = new Game(files);
        quietly(() -> old.loadGameSave(10));
        assertTrue("...saved as Refining's crudeMix.LIGHT, .MEDIUM and .HEAVY; a save without them reads medium",
                named && old.getLoadFailure() == null && java.util.Arrays.equals(old.getSectors().refining().getCrudeMix(), Refining.MEDIUM_MIX));
        LongPlaytest.cleanUp(files.getDirectory().getParent());
    }

    static <T> T quietlyGet(java.util.function.Supplier<T> s) {
        Object[] r = new Object[1];
        quietly(() -> r[0] = s.get());
        @SuppressWarnings("unchecked") T t = (T) r[0];
        return t;
    }
}
