package ham.citybuildersim;

import ham.citybuildersim.sectors.Oil;
import ham.citybuildersim.sectors.Rail;
import ham.citybuildersim.sectors.Refining;

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
 *      end of the registry.
 *
 *   2. A WELL LIFTS ONLY OWNED OIL. No oil site, no well - the order, the
 *      card, the investors all refuse it; an offer with oil bought is one
 *      well a site; and what the wells lift is exactly what leaves the
 *      ground, the world's oil conserved to the tonne.
 *
 *   3. THE OIL RUNS OUT, AND THE WELLS RETIRE, as the mines do when the ore
 *      does.
 *
 *   4. THE REFINERY TAKES THE WELLS' CRUDE FIRST and imports the rest.
 *
 *   5. THE DRIVERS AND THE RAILWAY TAKE THE REFINERS' FUEL FIRST, and import
 *      only what the tanks do not hold.
 *
 *   6. FUEL'S MONEY AUDIT CLOSES in every month of a city with wells and a
 *      refinery: the imported part is the households' FuelImports, the
 *      domestic part is on Refining's statement, and the goods foot to the
 *      balance of payments.
 *
 *   7. WITH NO REFINERY, THE HOUSEHOLDS PAY TODAY'S BILL AT THE WORLD'S PRICE
 *      LEVEL, as every good is priced - the railway's fuel always was.
 *
 *   8. A REFINERY IS BUILT FOR THE CITY'S OWN FUEL OR ITS OWN CRUDE, a whole
 *      plant's worth (Refining.plan(), the star the playtest measured: 120
 *      export refineries without it), and its estimate pays for the crude it
 *      would have to import at the import price.
 *
 *   9. THE MONTH'S FUEL CROSSES A SAVE: the bill, the imports and the litres
 *      as 6d drew them, and a save from before them derives what that month
 *      struck - every litre imported.
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
     * centre holds none of the oil field the default world puts 1.08 km from
     * its site.
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

    /** The offer standing nearest the oil field nearest the city's site that it does not own (MiningCheck.towardIron(), on oil). */
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
                    if (land.ownsPlot(d.x(), d.y()) || r >= best) continue;
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

        out.println();
        if (fails == 0) {
            out.println("All checks passed.");
        } else {
            out.println(fails + " FAILED");
            System.exit(fails);
        }
    }

    /* ============================ 1. THE GOODS AND THE BUILDINGS ============================ */

    static void goodsAndBuildings() {
        out.println("--- 1. the goods, the buildings and the sectors are the spec's ---");
        Good crude = Good.CRUDE, fuel = Good.FUEL;
        assertTrue("crude: US$600 a tonne in, US$500 out - about US$75 a barrel at 7.33 barrels a tonne",
                crude.worldImportPrice() == .60 && crude.worldExportPrice() == .50 && crude.unit().equals("tonne"));
        assertTrue("fuel: a journey's fuel over a journey's litres in, to the bit; .0007 a litre out",
                fuel.worldImportPrice() == Motoring.CAR_FUEL_PER_JOURNEY / Motoring.LITRES_PER_JOURNEY
                        && fuel.worldExportPrice() == .0007 && fuel.unit().equals("litre"));
        for (Good g : new Good[] { crude, fuel }) {
            double half = (g.worldImportPrice() - g.worldExportPrice()) / 2;
            close(g.name() + ": freight is three quarters of the half-wedge, Good's rule", g.baseFreight(), .75 * half, 1e-12);
            assertTrue("...so the world still buys below what it sells for", g.worldBuyPrice() > g.worldSellPrice());
        }
        assertTrue("crude ships what is lifted (a flow good, star); fuel is held in the refiners' tanks",
                !crude.stockable() && fuel.stockable() && crude.traded() && fuel.traded());
        assertTrue("crude goes by the bulk stream, fuel with the goods", crude.traffic() == Traffic.BULK
                && fuel.traffic() == Traffic.GOODS);
        assertTrue("a litre is a kilogram: the refinery's thousand litres a tonne, a tonne in and a tonne out",
                fuel.tonnesPerUnit() * Refining.LITRES_PER_TONNE == crude.tonnesPerUnit());
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
        assertTrue("...each found by its saved name, the wells making crude, the refinery crude into fuel",
                s.byKey(Sectors.OIL) == s.oil() && s.byKey(Sectors.REFINING) == s.refining()
                        && s.oil().isMaker(crude) && s.refining().isUser(crude) && s.refining().isMaker(fuel));

        BuildingManager bm = new BuildingManager();
        bm.initializeTemplates();
        BuildingsTemplate well = bm.getTemplateByName("Oil Well"), refinery = bm.getTemplateByName("Oil Refinery");
        assertTrue("the Oil Well: id 74, MINING, the Oil sector's, 415 t of crude a month (a hundred barrels a day)",
                well != null && well.getId() == 74 && well.getCategory() == BuildingType.MINING
                        && Sectors.OIL.equals(well.getSector()) && well.makes(crude) == 415);
        assertTrue("...and stands on an oil site: Game.siteOf() is oil, an Iron Mine's iron",
                Game.siteOf(well) == Resource.OIL && Game.siteOf(bm.getTemplateByName("Iron Mine")) == Resource.IRON
                        && Game.siteOf(refinery) == null);
        assertTrue("the Oil Refinery: id 75, HEAVY_INDUSTRY, Refining's, 8,300 t of crude into 8,300,000 L",
                refinery != null && refinery.getId() == 75 && refinery.getCategory() == BuildingType.HEAVY_INDUSTRY
                        && Sectors.REFINING.equals(refinery.getSector()) && refinery.uses(crude) == 8300
                        && refinery.makes(fuel) == 8300 * Refining.LITRES_PER_TONNE);
        assertTrue("...with three months of it in its tanks", refinery != null && refinery.getStock() >= 3 * refinery.makes(fuel));
        close("the railway burns a tonne's world fuel in litres: WORLD_FUEL_PER_TONNE over a litre's import price, eighteen",
                Rail.FUEL_LITRES_PER_TONNE, 18, 1e-12);
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

        // Bought: toward the nearest oil field, the offer nearest it each time, until an offer holds a site.
        int pushes = 0;
        double paid = 0;
        while (land.getOilSites() == 0 && pushes < 200) {
            LandParcel p = land.getMarket().richest(Resource.OIL);
            if (p == null) p = towardOil(g);
            if (p == null) break;
            final LandParcel buy = p;
            g.setCashForTest(g.getCash() + buy.localPrice(g.getForeignAccounts().getRate()));
            if (!quietlyGet(() -> g.buyLandParcel(buy.getId()))) break;
            paid += buy.getPriceUsd();
            pushes++;
        }
        int sites = land.getOilSites();
        double owned = land.getOwnedAmount(Resource.OIL);
        int bought = 0;
        for (CityLand.Purchase p : g.getCityLand().purchases()) bought += p.offer().getSites(Resource.OIL);
        report("fixture: an offer with oil bought (toward the nearest field)", sites > 0,
                String.format("%d purchase(s), US$%,.0fk, %d site(s), %,.0f t", pushes, paid, sites, owned));
        assertTrue("...the city's oil sites are the sites its purchases listed", sites == bought);
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
        return g;
    }

    /* ============================ 5. THE DRIVERS AND THE RAILWAY ============================ */

    static void driversAndRailwayTakeLocalFuel(Game g) {
        out.println("\n--- 5. the drivers and the railway take the refiners' fuel first ---");
        Motoring mo = g.getMotoring();
        Refining refiners = g.getSectors().refining();
        GoodsMarket fuel = g.getMarkets().get(Good.FUEL);
        // A month, the litre's price at the drivers' draw (6d: the last clearing's) kept.
        double priced = fuel.getLocalPrice();
        quietly(() -> g.simulateMonths(1));
        report("fixture: the refinery's tanks hold fuel and the town drives", refiners.getStock(Good.FUEL) > 0 && mo.getFuelLitres() > 0,
                String.format("%,.0f L in the tanks, %,.0f L burned", refiners.getStock(Good.FUEL), mo.getFuelLitres()));
        close("the month's drivers bought every litre at home: none imported", mo.getFuelImports(), 0, 1e-12);
        close("...and paid the refiners' price for it", mo.getFuelBill(), mo.getFuelLitres() * priced, 1e-9);

        // A draw past the tanks, on a copy of what the refinery holds: the shelf first, the world for the rest.
        double held = refiners.getStock(Good.FUEL);
        double salesBefore = refiners.pending().salesToHouseholds;
        double journeys = (held * 1.5) / Motoring.LITRES_PER_JOURNEY;
        double local = fuel.getLocalPrice(), imported = fuel.importPrice();
        quietly(() -> mo.drawFuel(g, journeys));
        close("a draw past the tanks takes all they hold", refiners.getStock(Good.FUEL), 0, 1e-9);
        close("...imports the rest at the import price", mo.getFuelImports(), (held * 1.5 - held) * imported, 1e-9);
        close("...and the bill is the two together", mo.getFuelBill(), held * local + (held * .5) * imported, 1e-9);
        close("...the shelf's part a sale to the households on Refining's books", refiners.pending().salesToHouseholds - salesBefore,
                held * local, 1e-9);

        // The railway: track in the oil town, carrying its crude and its fuel across the edge.
        quietly(() -> {
            g.getBusinessInvestment().holdSector(Sectors.RAIL);
            g.buildStack(template(g, "Rail Spur"), 2, true);
            g.simulateMonths(4);
        });
        // ...and one month more, the litre's price at its draw (the top of the month, the last clearing's) kept.
        double atDraw = fuel.getLocalPrice();
        quietly(() -> g.simulateMonths(1));
        Rail rail = g.getSectors().rail();
        report("fixture: the railway hauls and burns fuel, with fuel in the refiners' tanks",
                rail.getHauledTonnes() > 0 && rail.getFuelBill() > 0 && refiners.getStock(Good.FUEL) > 0,
                String.format("%,.0f t hauled, fuel %,.2fk, %,.0f L in the tanks", rail.getHauledTonnes(), rail.getFuelBill(),
                        refiners.getStock(Good.FUEL)));
        close("the railway imported none of its fuel", rail.getFuelImported(), 0, 1e-12);
        Sector.Split bought = rail.statement().bought.getOrDefault(Good.FUEL, new Sector.Split());
        close("...it bought it at home, on its books", bought.atHome, rail.getFuelBill(), 1e-9);
        close("...eighteen litres a tonne hauled, at the litre's price when it drew them", rail.getFuelBill(),
                rail.getHauledTonnes() * Rail.FUEL_LITRES_PER_TONNE * atDraw, 1e-9);
    }

    /* ============================ 6. THE AUDIT ============================ */

    static void theAudit(Game g) {
        out.println("\n--- 6. fuel's money audit closes, every month of a city with wells and a refinery ---");
        Refining refiners = g.getSectors().refining();
        double worstAudit = 0, worstImports = 0, worstDomestic = 0, worstGoods = 0;
        int months = 24, withImports = 0, withDomestic = 0;
        for (int m = 0; m < months; m++) {
            // Every few months the tanks are emptied, so some months the drivers import and some they do not.
            if (m % 4 == 1) refiners.setStock(Good.FUEL, 0);
            quietly(() -> g.simulateMonths(1));
            MoneyAudit.Result r = g.getLastMoneyAudit();
            worstAudit = Math.max(worstAudit, r.relative());
            double imports = g.getHouseholdFuelImports();
            if (imports > 0) withImports++;
            worstImports = Math.max(worstImports, Math.abs(auditLine(r, "+ households FuelFunded") - imports)
                    + Math.abs(auditLine(r, "- households FuelImports") - imports));
            double domestic = g.getHouseholdFuel() - imports;
            if (domestic > 0) withDomestic++;
            worstDomestic = Math.max(worstDomestic, Math.abs(refiners.statement().salesToHouseholds - domestic)
                    / Math.max(1, g.getHouseholdFuel()));
            Sectors.TradeByGood goods = g.getTradeByGood();
            double tradeImports = g.getForeignAccounts().tradeImports();
            worstGoods = Math.max(worstGoods, Math.abs(goods.bought() - tradeImports) / Math.max(1, tradeImports));
        }
        report("the audit closes every month (relative to what moved)", worstAudit < 1e-10,
                String.format("worst %.2e over %d months", worstAudit, months));
        report("fixture: months the drivers imported and months they bought at home", withImports > 0 && withDomestic > 0,
                withImports + " and " + withDomestic + " of " + months);
        report("the imported part is the audit's FuelFunded and FuelImports, to the cent", worstImports <= .011,
                String.format("worst %.4f", worstImports));
        report("...and the domestic part is on Refining's statement, its sales to the households", worstDomestic < 1e-9,
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
        GoodsMarket fuel = g.getMarkets().get(Good.FUEL);
        double drivers = g.getInfrastructureManager().getDrivers();
        report("fixture: the town drives, and has no refinery", drivers > 0 && g.getSectors().refining().buildingsStanding() == 0,
                String.format("%,.0f drivers", drivers));
        close("its drivers burned a month of journeys at 1.2 litres", mo.getFuelLitres(),
                drivers * TaxPolicy.JOURNEYS_A_MONTH * Motoring.LITRES_PER_JOURNEY, 1e-12);
        close("...every litre imported", mo.getFuelImports(), mo.getFuelBill(), 1e-12);
        double oldBill = drivers * TaxPolicy.JOURNEYS_A_MONTH * Motoring.CAR_FUEL_PER_JOURNEY * g.getForeignAccounts().getRate();
        close("...at the import price: today's bill (0.7.49's, a journey at the rate) times the world's price level",
                mo.getFuelBill(), oldBill * level, 1e-12);
        close("...the level the markets were told", fuel.getExchangeRate() / g.getForeignAccounts().getRate(), level, 1e-12);
        close("a journey's fuel, which the owners weigh a ride against, is a journey's litres at the import price",
                g.getInfrastructureManager().getFuelPerJourney(), Motoring.LITRES_PER_JOURNEY * fuel.importPrice(), 1e-12);
    }

    /* ============================ 8. WHEN A REFINERY IS BUILT ============================ */

    static void whenARefineryIsBuilt() {
        out.println("\n--- 8. a refinery is built for the city's own fuel or its own crude, a whole plant's worth ---");
        Game g = town("oilcheck-plan", 600, 0);
        // Two years, so the town burns fuel: some, not a refinery's worth.
        quietly(() -> g.simulateMonths(24));
        Refining refiners = g.getSectors().refining();
        BuildingsTemplate refinery = template(g, "Oil Refinery");
        GoodsMarket fuel = g.getMarkets().get(Good.FUEL);
        double room = g.getBusinessInvestment().forecast(refiners, fuel);
        BusinessInvestment.Decision d = refiners.plan(g.getBusinessInvestment(), g);
        report("a town burning less than a refinery makes, with no wells, is not given one", !d.build && room > 0
                        && room < refinery.makes(Good.FUEL) && refiners.spareCrude(refinery) == 0,
                String.format("%,.0f L a month: \"%s\"", room, d.reason));
        double estimate = refiners.estimatedMonthlyProfit(refinery, g.getBusinessInvestment());
        GoodsMarket crude = g.getMarkets().get(Good.CRUDE);
        double template = g.getBusinessInvestment().estimatedMakerProfit(refiners, refinery);
        close("...and its estimate pays for every tonne of crude at the import price, not the band's middle",
                template - estimate, refinery.uses(Good.CRUDE) * (crude.netImportPrice() - crude.getLocalPrice())
                        * BusinessInvestment.operatingRateOf(refiners.getOperatingRate()), 1e-9);

        BuildingsTemplate well = template(g, "Oil Well");
        // Wells, ten at a time, until they lift a refinery's worth at the rate the town runs them.
        int[] sites = { 0 };
        while (refiners.spareCrude(refinery) < refinery.uses(Good.CRUDE) && sites[0] < 300) {
            quietly(() -> {
                sites[0] += 10;
                g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 10 * well.getLandSqFt());
                g.getLandManager().restoreSites(Resource.OIL, sites[0], 50_000_000);
                g.buildStack(well, 10, true);
                if (sites[0] % 40 == 0) g.buildStack(template(g, "Coal Power Plant"), 1, true);
                g.simulateMonths(1);
            });
        }
        double spare = refiners.spareCrude(refinery);
        BusinessInvestment.Decision with = refiners.plan(g.getBusinessInvestment(), g);
        report("with a refinery's worth of crude the wells lift and nobody takes, the gate opens", spare >= refinery.uses(Good.CRUDE)
                        && (with.build || !with.reason.contains("none for another")),
                String.format("%d wells, %,.0f t spare: \"%s\"", sites[0], spare, with.reason));
        double withSpare = refiners.estimatedMonthlyProfit(refinery, g.getBusinessInvestment());
        close("...and its estimate is the template's: no crude to import", withSpare,
                g.getBusinessInvestment().estimatedMakerProfit(refiners, refinery), 1e-12);
    }

    /* ============================ 9. ACROSS A SAVE ============================ */

    static void acrossASave(Game g) throws Exception {
        out.println("\n--- 9. the month's fuel crosses a save ---");
        // A month with both parts: the tanks left holding the railway's litres and half the drivers'.
        Motoring mo = g.getMotoring();
        Refining refiners = g.getSectors().refining();
        refiners.setStock(Good.FUEL, g.getSectors().rail().getHauledTonnes() * Rail.FUEL_LITRES_PER_TONNE
                + .5 * mo.getFuelLitres());
        quietly(() -> g.simulateMonths(1));
        report("fixture: a month with fuel from both the tanks and the world",
                mo.getFuelImports() > 0 && mo.getFuelImports() < mo.getFuelBill(),
                String.format("bill %,.2fk, %,.2fk of it imported", mo.getFuelBill(), mo.getFuelImports()));
        GameFiles files = FILES.get(g);
        quietly(() -> g.saveGame(10, "oil"));
        Path file = files.saveFile(10);
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        Motoring back = twin.getMotoring();
        assertTrue("the bill, its imported part and the litres load as 6d drew them",
                back.getFuelBill() == mo.getFuelBill() && back.getFuelImports() == mo.getFuelImports()
                        && back.getFuelLitres() == mo.getFuelLitres());
        quietly(() -> { g.simulateMonths(1); twin.simulateMonths(1); });
        report("...and a month on, both cities' households pay the same fuel, as much of it abroad",
                twin.getHouseholdFuel() == g.getHouseholdFuel() && twin.getHouseholdFuelImports() == g.getHouseholdFuelImports(),
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

    static <T> T quietlyGet(java.util.function.Supplier<T> s) {
        Object[] r = new Object[1];
        quietly(() -> r[0] = s.get());
        @SuppressWarnings("unchecked") T t = (T) r[0];
        return t;
    }
}
