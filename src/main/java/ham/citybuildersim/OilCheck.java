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
 *      ground, the world's oil conserved to the tonne.
 *
 *   3. THE OIL RUNS OUT, AND THE WELLS RETIRE, as the mines do when the ore
 *      does.
 *
 *   4. THE REFINERY TAKES THE WELLS' CRUDE FIRST and imports the rest.
 *
 *   5. THE DRIVERS AND THE RAILWAY TAKE THE REFINERS' FUEL FIRST, and import
 *      only what the tanks do not hold - the drivers' petrol and the
 *      railway's diesel since 0.7.76.
 *
 *   6. FUEL'S MONEY AUDIT CLOSES in every month of a city with wells and a
 *      refinery: the imported part is the households' PetrolImports (their
 *      FuelImports until 0.7.76), the domestic part is on Refining's
 *      statement, and the goods foot to the balance of payments - and the
 *      tanks, shared among the products, write none of them off: what nobody
 *      here buys fills its share to the dump line, then ships or (on the
 *      wholesale ladder, 0.7.78, a medium crude's slate) idles.
 *
 *   7. WITH NO REFINERY, THE HOUSEHOLDS PAY TODAY'S BILL AT THE WORLD'S PRICE
 *      LEVEL, as every good is priced - the railway's fuel always was. Since
 *      0.7.78 (batch O2) the bill is a journey's litres at petrol's place on
 *      the wholesale ladder, 63% under 0.7.49's journey at the pump price.
 *
 *   8. A REFINERY IS BUILT FOR THE CITY'S OWN FUEL OR ITS OWN CRUDE, a whole
 *      plant's worth (Refining.plan(), the star the playtest measured: 120
 *      export refineries without it), and its estimate pays for the crude it
 *      would have to import at the import price. The plant's worth is its
 *      petrol and diesel since 0.7.76, the estimate struck over its slate.
 *
 *   9. THE MONTH'S FUEL CROSSES A SAVE: the bill, the imports and the litres
 *      as 6d drew them, and a save from before them derives what that month
 *      struck - every litre imported.
 *
 *  10. A SAVE FROM BEFORE 0.7.76 HAS ITS FUEL SPLIT (FuelSplit, spec-oil 3):
 *      every FUEL figure into PETROL and DIESEL by the drivers' share of the
 *      month's litres, each pair summing to the figure to the bit, the
 *      railway's own all diesel, no money moving; the audit closes for a
 *      year after; and saved as format 34 it loads back to the cent.
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
 *      graded by where E stands; a field's sites are the sea's when its
 *      centre is sea, the rest dry. The refinery's month's mix is its local
 *      crude at the lift's grades and its imports at medium, next month's
 *      slate is struck on it, and it crosses a save (a save without it
 *      reads medium).
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
        Game back = theSplit(oilTown);
        aRefineryCloses(back);
        crudeByGrade();

        out.println();
        if (fails == 0) {
            out.println("All checks passed.");
        } else {
            out.println(fails + " FAILED");
            System.exit(fails);
        }
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
            /*
             * THE REFINERY THE FIXTURE STANDS IS HELD (0.7.76): on imported
             * crude its slate is worth less abroad than the crude costs, so it
             * idles once its tanks are full and the distress rule sheds it in
             * its third year - this section and the next five are about where
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
        close("the month's drivers bought every litre at home: none imported", mo.getFuelImports(), 0, 1e-12);
        close("...and paid the refiners' price for it", mo.getFuelBill(), mo.getFuelLitres() * priced, 1e-9);

        // A draw past the tanks, on a copy of what the refinery holds: the shelf first, the world for the rest.
        double held = refiners.getStock(Good.PETROL);
        double salesBefore = refiners.pending().salesToHouseholds;
        double journeys = (held * 1.5) / Motoring.LITRES_PER_JOURNEY;
        double local = fuel.getLocalPrice(), imported = fuel.importPrice();
        quietly(() -> mo.drawFuel(g, journeys));
        close("a draw past the tanks takes all the petrol they hold", refiners.getStock(Good.PETROL), 0, 1e-9);
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
        int months = 24, withImports = 0, withDomestic = 0;
        boolean toTheLine = true;
        for (int m = 0; m < months; m++) {
            // Every few months the petrol is emptied, so some months the drivers import and some they do not.
            if (m % 4 == 1) refiners.setStock(Good.PETROL, 0);
            quietly(() -> g.simulateMonths(1));
            MoneyAudit.Result r = g.getLastMoneyAudit();
            worstAudit = Math.max(worstAudit, r.relative());
            double imports = g.getHouseholdFuelImports();
            if (imports > 0) withImports++;
            worstImports = Math.max(worstImports, Math.abs(auditLine(r, "+ households PetrolFunded") - imports)
                    + Math.abs(auditLine(r, "- households PetrolImports") - imports));
            for (Good p : refiners.goodsMade()) {
                Sector.Output o = refiners.outputRow(p);
                if (o == null) continue;
                writtenOff += o.writtenOff;
                if (p == Good.PETROL || p == Good.DIESEL) continue;
                shipped += o.exportBound + o.exported;
                idled += o.idled;
                toTheLine &= refiners.getStock(p) <= refiners.getStockCapacity(p) * Sector.DUMP_THRESHOLD * (1 + 1e-12);
            }
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
        report("the imported part is the audit's PetrolFunded and PetrolImports, to the cent", worstImports <= .011,
                String.format("worst %.4f", worstImports));
        /*
         * SHIPPED OR IDLED (0.7.78, batch O2): the products nobody here buys
         * fill their share of the tanks to the dump line and no further; past
         * it each line ships what it makes when the export price clears its
         * share of the line's cost (Sector.getExportBoundOutput()) and idles
         * when it does not. At FUEL's band (0.7.76) this town's lines shipped
         * 282.8M L in the 24 months; on the wholesale ladder a medium crude's
         * slate is worth less than the crude (spec-oil 6: an Oil Refinery on
         * medium crude fails its gate by 22%), so they idle.
         */
        report("the products nobody here buys fill their tank share to the dump line and no further, shipped past it or"
                        + " idled, and the tanks write none of any product off (spec-oil 6)",
                toTheLine && shipped + idled > 0 && writtenOff == 0,
                String.format("%,.0f L shipped, %,.0f L of nameplate idled, %,.0f written off", shipped, idled, writtenOff));
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
        GoodsMarket fuel = g.getMarkets().get(Good.PETROL);
        double drivers = g.getInfrastructureManager().getDrivers();
        report("fixture: the town drives, and has no refinery", drivers > 0 && g.getSectors().refining().buildingsStanding() == 0,
                String.format("%,.0f drivers", drivers));
        close("its drivers burned a month of journeys at 1.2 litres", mo.getFuelLitres(),
                drivers * TaxPolicy.JOURNEYS_A_MONTH * Motoring.LITRES_PER_JOURNEY, 1e-12);
        close("...every litre imported", mo.getFuelImports(), mo.getFuelBill(), 1e-12);
        // Since 0.7.78 (O2) a journey's litres at petrol's place on the wholesale ladder; 0.7.49's journey at the pump price before.
        double ladderBill = drivers * TaxPolicy.JOURNEYS_A_MONTH * Motoring.LITRES_PER_JOURNEY * Good.PETROL.worldImportPrice()
                * g.getForeignAccounts().getRate();
        close("...at the import price: a journey's litres at petrol's ladder price, at the rate, times the world's price level",
                mo.getFuelBill(), ladderBill * level, 1e-12);
        double oldBill = drivers * TaxPolicy.JOURNEYS_A_MONTH * Motoring.CAR_FUEL_PER_JOURNEY * g.getForeignAccounts().getRate();
        double cut = 1 - mo.getFuelBill() / (oldBill * level);
        report("...63% under the bill the pump price struck (0.7.49's journey at the rate and the level)",
                Math.round(100 * cut) == PETROL_CUT_PCT,
                String.format("%,.2fk against %,.2fk, %.2f%% less", mo.getFuelBill(), oldBill * level, 100 * cut));
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
        // ...its petrol and diesel since 0.7.76 (O1): the forecast of each, against what the plant makes of the two.
        double room = 0;
        for (Good p : Refining.BOUGHT_HERE) room += g.getBusinessInvestment().forecast(refiners, g.getMarkets().get(p));
        double plantsWorth = Refining.boughtHere(Refining.slate(refinery.uses(Good.CRUDE)));
        BusinessInvestment.Decision d = refiners.plan(g.getBusinessInvestment(), g);
        report("a town burning less petrol and diesel than a refinery makes, with no wells, is not given one", !d.build && room > 0
                        && room < plantsWorth && refiners.spareCrude() == 0,
                String.format("%,.0f L a month against %,.0f: \"%s\"", room, plantsWorth, d.reason));
        double estimate = refiners.estimatedMonthlyProfit(refinery, g.getBusinessInvestment());
        GoodsMarket crude = g.getMarkets().get(Good.CRUDE);
        // The investors' estimate over what it makes, its slate (0.7.76): its template makes nothing.
        double template = g.getBusinessInvestment().estimatedMakerProfit(refiners, refinery, Refining.madeBy(refinery));
        close("...and its estimate pays for every tonne of crude at the import price, not the band's middle",
                template - estimate, refinery.uses(Good.CRUDE) * (crude.netImportPrice() - crude.getLocalPrice())
                        * BusinessInvestment.operatingRateOf(refiners.getOperatingRate()), 1e-9);

        BuildingsTemplate well = template(g, "Oil Well");
        // Wells, ten at a time, until they lift a refinery's worth at the rate the town runs them.
        int[] sites = { 0 };
        while (refiners.spareCrude() < refinery.uses(Good.CRUDE) && sites[0] < 300) {
            quietly(() -> {
                sites[0] += 10;
                g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 10 * well.getLandSqFt());
                g.getLandManager().restoreSites(Resource.OIL, sites[0], 50_000_000);
                g.buildStack(well, 10, true);
                if (sites[0] % 40 == 0) g.buildStack(template(g, "Coal Power Plant"), 1, true);
                g.simulateMonths(1);
            });
        }
        double spare = refiners.spareCrude();
        BusinessInvestment.Decision with = refiners.plan(g.getBusinessInvestment(), g);
        report("with a refinery's worth of crude the wells lift and nobody takes, the gate opens", spare >= refinery.uses(Good.CRUDE)
                        && (with.build || !with.reason.contains("none for another")),
                String.format("%d wells, %,.0f t spare: \"%s\"", sites[0], spare, with.reason));
        double withSpare = refiners.estimatedMonthlyProfit(refinery, g.getBusinessInvestment());
        close("...and its estimate is the investors' over its slate: no crude to import", withSpare,
                g.getBusinessInvestment().estimatedMakerProfit(refiners, refinery, Refining.madeBy(refinery)), 1e-12);
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
     * for a year; saved as 34, it loads back to the cent.
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

        // Saved as 34, and loaded back.
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

    /** The world whose founding site has an oil field in the sea 2.6 km out - heavy, 8 sites, 91 m deep (spec-oil 2.7's third seed): what section 12's fixtures buy toward. */
    static final long SEA_OIL_SEED = 709_115_276L;

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

        // Its oil as it is worked out: the centre's by fiat first, then the field.
        double bought = lm.getOwnedAmount(Resource.OIL);
        lm.restoreSites(Resource.OIL, (int) all, bought + FIAT_TONNES);
        double[][] runs = lm.oilRuns();
        report("the oil is laid out as it is worked out: the centre's 1,000 t by fiat first, medium as the world's crude, then the"
                        + " field's, heavy",
                runs.length >= 2 && runs[0][0] == FIAT_TONNES && runs[0][1] == medium && runs[1][1] == heavy
                        && runs[1][0] == FIAT_TONNES + sea.amount() && runs[runs.length - 1][0] == lm.getOwnedAmount(Resource.OIL),
                runs.length + " run(s), ending at " + String.format("%,.0f t", runs.length == 0 ? 0 : runs[runs.length - 1][0]));
        double world = lm.getWorldTotal(Resource.OIL);
        double got = lm.extractOil(1_500);
        double[] graded = lm.getOilLiftedByGrade();
        report("a lift across the two is graded by where E stood: 1,000 t medium, then 500 t heavy, to the tonne",
                got == 1_500 && graded[medium] == FIAT_TONNES && graded[heavy] == 500 && graded[light] == 0,
                String.format("%.1f / %.1f / %.1f t", graded[light], graded[medium], graded[heavy]));
        lm.extractOil(415);
        graded = lm.getOilLiftedByGrade();
        assertTrue("...a month's lifts add up, all of the field's grade past the fiat", graded[heavy] == 915 && graded[medium] == FIAT_TONNES);
        assertTrue("...nothing moves but E: unowned + remaining + extracted is the world's, to the tonne",
                Math.abs(lm.getUnowned(Resource.OIL) + lm.getRemaining(Resource.OIL) + lm.getExtracted(Resource.OIL) - world) <= 1
                        && lm.getExtracted(Resource.OIL) == 1_915);
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
        int wellsOn = glm.getOilSites();
        Game.BuildResult wellsBuilt = quietlyGet(() -> g.buildStack(t[0], wellsOn, true));
        Game.BuildResult refineryBuilt = quietlyGet(() -> g.buildStack(t[1], 1, true));
        double[][] gruns = glm.oilRuns();
        report("fixture: a town on that world owns the field, its wells on every site and a refinery standing, all its oil heavy",
                wellsBuilt == Game.BuildResult.SUCCESS && refineryBuilt == Game.BuildResult.SUCCESS && gruns.length == 1 && gruns[0][1] == heavy,
                String.format("%d purchase(s), %d well(s), %s / %s", pushes, wellsOn, wellsBuilt, refineryBuilt));
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
