package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Zoning and the forest law, the model (0.7.107, batch Z1): Zoning, and
 * LandManager's ZONES AND THE FOREST LAW, held to the ledger, the treasury,
 * the roll and the world's totals.
 *
 * WHY. Jerus, 2026-10-08 (the project's spec-zoning.md): "the default is
 * free, anything everywhere, but with zoning, you can allocate a % of total
 * land or a specific km² for a specific industry, and even provide the
 * land at a lower rate, or free" - and a forest law "of not building on the
 * forest, or always keep a certain % of forest available". A zone that leaked
 * its ground to another sector, a price the city charged and did not book, a
 * roll that read the going price for zoned ground, or a cut that reached the
 * protected wood would each be a lever that says one thing and does another.
 * Nothing in the default playtest sets a zone (it stays byte-identical), so
 * this is where zoning is played (and, since 0.7.109, ZoningViewCheck holds
 * the screens' figures to it).
 *
 * What this has to prove (spec-materials 4, ZoningCheck):
 *   1. NOTHING SET, NOTHING MOVES: with no zones and the law at 0 and 0 the
 *      free ground is owned less allocated, a sector's free ground and price
 *      the going ones, the roll the books' - and a city whose zone was set
 *      and taken off before it played plays the month the untouched one does.
 *   2. A ZONE'S GROUND IS ITS SECTOR'S: what a zone holds leaves the others'
 *      free ground, only its sector builds on it, it builds there first, and
 *      giving the ground back hands it to the zone again.
 *   3. THE CITY IS PAID THE ZONE'S PRICE AND ASSESSES AT IT: a building in a
 *      zone pays the city its share of the going price for its plot, booked
 *      in the month's land sales; the sector's roll reads its zoned ground at
 *      that price, the planner's standing cost too; un-zoned, the going price.
 *   4. ONE DIAL, TWO DOORS: a zone's tax row moves the sector's own offset
 *      exactly as Policy > Taxes does, and reads the sector's rate.
 *   5. A ZONE BIGGER THAN THE FREE GROUND holds what is free, two such zones
 *      each the same share of their wants, and they fill as land is bought.
 *   6. THE FOREST LAW: protected forest is never cut or built on, working
 *      forest never built on; the cut stops at the protected wood.
 *   7. BUILDING ON OPEN FOREST CLEARS IT, cut-over ground first, then
 *      standing forest counted as taken out; cleared ground never regrows,
 *      a demolition leaves it cleared, and the world's totals hold.
 *   8. ZONED FIELDS DO NOT YIELD TO HOMES: while somebody sleeps outside the
 *      fields break no ground but in their zone.
 *   9. SAVED AND RELOADED, THE SAME CITY: zones, used ground, the law and the
 *      forest cleared, and the month after.
 *
 * Every fixture causes its condition.
 */
public class ZoningCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-104s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Money is in thousands, so this is a cent. */
    static final double CENT = 1e-5;

    static boolean near(double a, double b) { return Math.abs(a - b) <= CENT; }

    /** ...and two ground or wood figures the same to a part in a billion of the larger: one rounding, not a square foot. */
    static boolean same(double a, double b) { return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b))); }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** Spare unskilled hands section 8 hands the city past what its posts are short of, so the fields' planner gets past the staff to the zone (0.7.113): more than any farm's posts. */
    static final int FARM_HANDS = 100;

    /** The fixture's orders: homes, shops, a diner, builders, a bakery buying crops, power, water and roads. */
    static final String[][] ORDERS = {
            { "House", "580" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Construction Depot", "6" },
            { "Coal Power Plant", "2" }, { "Water Treatment Plant", "2" }, { "Industrial Bakery", "3" },
            { "Commercial Bank", "1" }, { "Elementary School", "3" }, { "Walk-in Clinic", "3" }, { "Paved Road", "20" } };

    /**
     * A played city: the orders put up finished out of a Wealthy treasury on
     * 200 million square feet more ground, the fields held out of the
     * investment engine (section 8 asks them itself), then `months` played -
     * `before` run on it first, before anything is built.
     */
    static Game city(Path root, String name, int months, java.util.function.Consumer<Game> before) {
        Game g = new Game(new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy")));
        quietly(() -> {
            g.run();
            if (before != null) before.accept(g);
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getGovernmentInvestor().spend(-2_000_000);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 200_000_000L);
            g.getBusinessInvestment().holdSector(Sectors.AGRICULTURE);
            for (String[] w : ORDERS) {
                BuildingsTemplate t = LongPlaytest.template(g, w[0]);
                if (t != null) g.buildStack(t, Integer.parseInt(w[1]), true);
            }
            g.simulateMonths(months);
        });
        return g;
    }

    /** A land office on its own whose ground is drawn round the default world's founding site to this many square kilometres: forest and bare ground both. */
    static LandManager office(double km2) {
        LandManager l = new LandManager();
        quietly(() -> l.setOwnedSqFt(LandManager.sqFt(km2)));
        return l;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("zoningcheck");

        nothingSet(root);
        Game g = city(root, "city", 24, null);
        out.printf("%n      the city: month %d, %,d people; %s dry, %s of it forest, %s built on, the going price %s/m²%n",
                g.getMonth(), g.getPopulationManager().getPopulation(),
                LandManager.areaWords(g.getLandManager().getOwnedSqFt()), LandManager.areaWords(g.getLandManager().getForestSqFt()),
                LandManager.areaWords(g.getLandManager().getAllocatedSqFt()),
                Formats.INSTANCE.amount(LandManager.perM2(g.getLandManager().getPricePerSqFt())));
        theZonesGround(g);
        thePriceAndTheRoll(g);
        oneDialTwoDoors(root);
        biggerThanTheFreeGround();
        theForestLaw();
        clearing();
        theFields(g);
        savedAndReloaded(root, g);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. NOTHING SET, NOTHING MOVES ============================ */

    static void nothingSet(Path root) {
        out.println("\n--- 1. nothing set, nothing moves: no zones and the law at 0 and 0 give the ground as it was ---");
        LandManager l = office(60);
        quietly(() -> l.allocate(LandManager.sqFt(7)));
        assertTrue("fixture: the office's ground holds forest and bare ground, some of it built on",
                l.getForestSqFt() > 0 && l.getBareSqFt() > 0 && l.getAllocatedSqFt() > 0);
        assertTrue("with nothing set the ground free for anyone is owned less allocated, to the bit",
                l.getAvailableSqFt() == Math.max(l.getOwnedSqFt() - l.getAllocatedSqFt(), 0)
                && l.getFreeSqFt() == l.getAvailableSqFt() && l.freeForAnyone() == l.getAvailableSqFt());
        boolean bySector = true;
        for (String key : Sectors.KEYS) {
            bySector &= l.freeFor(key) == l.getAvailableSqFt() && l.heldBy(key) == 0 && l.usedBy(key) == 0
                    && l.priceFor(123_456, key) == l.priceFor(123_456) && l.groundPriceFor(key) == l.getPricePerSqFt()
                    && l.buybackPriceFor(123_456, key) == l.priceFor(123_456);
        }
        assertTrue("...every sector's free ground, held ground and price the same as anyone's", bySector);
        assertTrue("...the law keeps nothing: all the forest is open and the whole of it may be cut",
                l.getProtectedForestSqFt() == 0 && l.getWorkingForestSqFt() == 0 && l.getOpenForestSqFt() == l.getForestSqFt()
                && l.cuttableForest() == l.getRemaining(Resource.FOREST) && l.getProtectedWood() == 0);
        assertTrue("...and nothing to save: the zoning key is absent, as an older save's", l.zoningState() == null);

        // The twin: a city whose zone and law were set and taken off before it played.
        Game plain = city(root, "plain", 12, null);
        Game undone = city(root, "undone", 12, g -> {
            g.setZone(Sectors.RETAIL, Zoning.Size.SHARE, .05, .5);
            g.setForestLaw(.3, .5);
            g.removeZone(Sectors.RETAIL);
            g.setForestLaw(0, 0);
        });
        assertTrue("fixture: the undone city's zoning was set and is empty again, its decisions logged",
                undone.getZoning().isEmpty() && undone.getDecisions().entries().size() >= 4);
        assertTrue("a year on, the two cities are the same month: cash, people, ground, its price, the businesses' cash",
                plain.getCash() == undone.getCash()
                && plain.getPopulationManager().getPopulation() == undone.getPopulationManager().getPopulation()
                && plain.getLandManager().getAllocatedSqFt() == undone.getLandManager().getAllocatedSqFt()
                && plain.getLandManager().getOwnedSqFt() == undone.getLandManager().getOwnedSqFt()
                && plain.getLandManager().getPricePerSqFt() == undone.getLandManager().getPricePerSqFt()
                && plain.getSectors().totalCash() == undone.getSectors().totalCash()
                && plain.getLandManager().getForestClearedSqFt() == undone.getLandManager().getForestClearedSqFt());
        EconomyManager em = plain.getEconomyManager();
        boolean roll = true;
        for (Sector s : plain.getSectors().all()) {
            roll &= em.assessedLandValueOf(s) == em.landValueOf(s);
        }
        BuildingsTemplate diner = LongPlaytest.template(plain, "Diner");
        assertTrue("...and with no zones the roll reads every sector's ground at the going price, a new plot too",
                roll && em.groundPriceFor(diner) == Math.max(0, em.getLandPricePerSqFt()));
    }

    /* ============================ 2. A ZONE'S GROUND IS ITS SECTOR'S ============================ */

    static void theZonesGround(Game g) {
        out.println("\n--- 2. a zone's ground is its sector's: held from the others, built on by it alone, first ---");
        LandManager land = g.getLandManager();
        double freeBefore = land.getAvailableSqFt();
        quietly(() -> g.setZone(Sectors.RESTAURANTS, Zoning.Size.KM2, 1, .25));
        double held = land.heldBy(Sectors.RESTAURANTS);
        assertTrue("fixture: Restaurants' zone of 1 km² holds all of it, the free ground having room",
                held == LandManager.sqFt(1) && freeBefore > LandManager.sqFt(2));
        assertTrue("what it holds leaves the ground free for anyone: free less held",
                same(land.getAvailableSqFt(), freeBefore - held) && same(land.getFreeSqFt(), freeBefore));
        assertTrue("...Restaurants see both, every other sector the ground free for anyone",
                same(land.freeFor(Sectors.RESTAURANTS), freeBefore) && land.freeFor(Sectors.RETAIL) == land.getAvailableSqFt());

        // Only its sector builds there: the zone takes every square foot free.
        quietly(() -> g.setZone(Sectors.RESTAURANTS, Zoning.Size.SHARE, 1, .25));
        assertTrue("fixture: re-sized to all the dry ground, the zone holds every square foot free",
                land.getAvailableSqFt() == 0 && same(land.heldBy(Sectors.RESTAURANTS), land.getFreeSqFt()));
        BuildingsTemplate store = LongPlaytest.template(g, "Convenience Store"), diner = LongPlaytest.template(g, "Diner");
        g.getSectors().retail().addCash(1_000_000);
        g.getSectors().byKey(Sectors.RESTAURANTS).addCash(1_000_000);
        double allocated = land.getAllocatedSqFt();
        boolean shop = g.buildFor(g.getSectorInvestor(Sectors.RETAIL), store, 1);
        assertTrue("a shop is refused: Retail has no ground the zone does not hold", !shop && land.getAllocatedSqFt() == allocated);
        assertTrue("...the city's own works too (Build's refusal: no ground for anyone)",
                g.buildStack(LongPlaytest.template(g, "Walk-in Clinic"), 1, true) == Game.BuildResult.NO_LAND);
        int stores = g.getBuildingManager().countByName("Convenience Store");
        boolean dinerBuilt = g.buildFor(g.getSectorInvestor(Sectors.RESTAURANTS), diner, 2);
        assertTrue("two diners stand on the zone's ground, and it records them as used",
                dinerBuilt && stores == g.getBuildingManager().countByName("Convenience Store")
                && same(land.usedBy(Sectors.RESTAURANTS), 2 * diner.getLandSqFt())
                && same(land.getAllocatedSqFt(), allocated + 2 * diner.getLandSqFt()));
        BusinessInvestment plans = g.getBusinessInvestment();
        quietly(g::toggleNextMonth);
        assertTrue("a month on, the investors saw it as the land office does: Restaurants' plots on the zone, none for a shop",
                plans.plotsAvailableFor(store) == 0 && plans.zoneHeldFor(Sectors.RESTAURANTS) > 0
                && plans.plotsAvailableFor(diner) >= 1 && land.getAvailableSqFt() == 0);
        assertTrue("...and a shop's refusal says why: \"... is zoned for others\"",
                plans.landReason(store).contains("is zoned for others"));

        // Giving ground back hands it to the zone again.
        double used = land.usedBy(Sectors.RESTAURANTS), heldNow = land.heldBy(Sectors.RESTAURANTS);
        land.release(diner.getLandSqFt(), Sectors.RESTAURANTS);
        assertTrue("a diner's plot given back leaves the zone's used ground, and the zone holds it again",
                same(land.usedBy(Sectors.RESTAURANTS), used - diner.getLandSqFt())
                && same(land.heldBy(Sectors.RESTAURANTS), heldNow + diner.getLandSqFt()));
        land.allocate(diner.getLandSqFt(), Sectors.RESTAURANTS);
        quietly(() -> g.setZone(Sectors.RESTAURANTS, Zoning.Size.KM2, 1, .25));
    }

    /* ============================ 3. THE PRICE AND THE ROLL ============================ */

    static void thePriceAndTheRoll(Game g) {
        out.println("\n--- 3. the city is paid the zone's price for its plot, and the roll assesses zoned ground at it ---");
        LandManager land = g.getLandManager();
        EconomyManager em = g.getEconomyManager();
        BuildingsTemplate diner = LongPlaytest.template(g, "Diner");
        Sector restaurants = g.getSectors().byKey(Sectors.RESTAURANTS);
        restaurants.addCash(1_000_000);
        double going = land.getPricePerSqFt(), share = g.getZoning().priceShare(Sectors.RESTAURANTS);
        assertTrue("fixture: Restaurants' zone sells at a quarter of the going price and holds a diner's plot",
                share == .25 && land.heldBy(Sectors.RESTAURANTS) >= diner.getLandSqFt() && going > 0);
        double cash = g.getCash(), sales = land.getLandSalesThisMonth(), sold = land.getSqFtSoldThisMonth();
        double quoted = land.priceFor(diner.getLandSqFt(), Sectors.RESTAURANTS);
        boolean built = g.buildFor(g.getSectorInvestor(Sectors.RESTAURANTS), diner, 1);
        assertTrue("its plot's price is the zone's: a quarter of the going price",
                quoted == diner.getLandSqFt() * (share * going) && land.groundPriceFor(Sectors.RESTAURANTS) == share * going);
        assertTrue("built, the treasury is paid exactly that, and the month's land sales book it",
                built && near(g.getCash() - cash, quoted) && near(land.getLandSalesThisMonth() - sales, quoted)
                && same(land.getSqFtSoldThisMonth() - sold, diner.getLandSqFt()));

        // The roll: the zone's used ground at the zone's price, the rest at the going price.
        double all = g.getBuildingManager().getLandSqFtBySector(Sectors.RESTAURANTS);
        double used = land.usedBy(Sectors.RESTAURANTS);
        double price = em.getLandPricePerSqFt();
        TaxPolicy p = em.getTaxPolicy();
        assertTrue("fixture: Restaurants stand on ground outside the zone and in it", used > 0 && all > used);
        assertTrue("the roll reads its zoned ground at the zone's price, the rest at the going price",
                near(em.assessedLandValueOf(restaurants), (all - used) * price + used * share * price)
                && near(em.getAssessedValue(restaurants), em.assessedLandValueOf(restaurants) * p.assessedLandShare(Sectors.RESTAURANTS)
                        + g.getBuildingManager().getBookValueBySector(Sectors.RESTAURANTS)));
        assertTrue("...the books keep the going price, as the fields' do under the relief",
                near(em.landValueOf(restaurants), all * price));
        double standing = g.getBusinessInvestment().standingCostOf(restaurants, diner);
        assertTrue("a new diner's standing cost reads its plot at the zone's price while the zone holds it",
                em.groundPriceFor(diner) == price * share
                && land.heldBy(Sectors.RESTAURANTS) >= diner.getLandSqFt());
        double tax = em.getPropertyTaxFor(restaurants);
        quietly(() -> g.removeZone(Sectors.RESTAURANTS));
        double rate = p.effectiveMonthlyPropertyRate(Sectors.RESTAURANTS);
        assertTrue("un-zoned, the roll reads the going price again: the tax rises by the zoned ground's relief",
                near(em.getPropertyTaxFor(restaurants) - tax,
                        (em.landValueOf(restaurants) - ((all - used) * price + used * share * price))
                        * p.assessedLandShare(Sectors.RESTAURANTS) * rate));
        assertTrue("...and the standing cost by its plot's",
                Math.abs(g.getBusinessInvestment().standingCostOf(restaurants, diner) - standing
                        - diner.getLandSqFt() * price * (1 - share) * p.assessedLandShare(Sectors.RESTAURANTS) * rate) <= 1e-9);
        assertTrue("...and what the zone held is free for anyone", land.heldByZones() == 0 && land.freeFor(Sectors.RETAIL) == land.getFreeSqFt());
        quietly(() -> g.setZone(Sectors.RESTAURANTS, Zoning.Size.KM2, 1, .25));
        assertTrue("re-zoned, its used ground starts again from none: what was sold stays with its owner at what it paid",
                land.usedBy(Sectors.RESTAURANTS) == 0);
    }

    /* ============================ 4. ONE DIAL, TWO DOORS ============================ */

    static void oneDialTwoDoors(Path root) {
        out.println("\n--- 4. one dial, two doors: a zone's tax row is the sector's own offset ---");
        Game a = new Game(GameFiles.scratch("zoningcheck-a")), b = new Game(GameFiles.scratch("zoningcheck-b"));
        quietly(() -> { a.run(); b.run(); });
        TaxPolicy pa = a.getEconomyManager().getTaxPolicy(), pb = b.getEconomyManager().getTaxPolicy();
        quietly(() -> {
            a.setZoneTax(Sectors.MINING, Zoning.Tax.PROFIT, -.04);
            a.setZoneTax(Sectors.MINING, Zoning.Tax.SALES, .02);
            a.setZoneTax(Sectors.MINING, Zoning.Tax.PROPERTY, -.5);
            pb.setProfitOffset(Sectors.MINING, -.04);
            pb.setSalesOffset(Sectors.MINING, .02);
            pb.setPropertyOffset(Sectors.MINING, -.5);
        });
        assertTrue("the zone's door and Policy > Taxes set the same offsets, clamped alike (property held at MAX_PROPERTY_TAX)",
                pa.getProfitOffset(Sectors.MINING) == pb.getProfitOffset(Sectors.MINING)
                && pa.getSalesOffset(Sectors.MINING) == pb.getSalesOffset(Sectors.MINING)
                && pa.getPropertyOffset(Sectors.MINING) == pb.getPropertyOffset(Sectors.MINING)
                && pa.getProfitOffset(Sectors.MINING) == -.04);
        assertTrue("...so every building of the sector moves with the zone: the effective rates the same",
                pa.effectiveProfitRate(Sectors.MINING) == pb.effectiveProfitRate(Sectors.MINING)
                && pa.effectiveSalesRate(Sectors.MINING) == pb.effectiveSalesRate(Sectors.MINING)
                && pa.effectivePropertyRate(Sectors.MINING) == pb.effectivePropertyRate(Sectors.MINING));
        assertTrue("...and the zone's rows read them",
                a.zoneTaxRate(Sectors.MINING, Zoning.Tax.PROFIT) == pa.effectiveProfitRate(Sectors.MINING)
                && a.zoneTaxRate(Sectors.MINING, Zoning.Tax.SALES) == pa.effectiveSalesRate(Sectors.MINING)
                && a.zoneTaxRate(Sectors.MINING, Zoning.Tax.PROPERTY) == pa.effectivePropertyRate(Sectors.MINING));
    }

    /* ============================ 5. BIGGER THAN THE FREE GROUND ============================ */

    static void biggerThanTheFreeGround() {
        out.println("\n--- 5. a zone bigger than the free ground holds what is free, and fills as land is bought ---");
        LandManager l = office(20);
        l.allocate(LandManager.sqFt(5));
        l.getZoning().set(Sectors.MINING, Zoning.Size.SHARE, 1, .5);
        double free = l.getFreeSqFt();
        assertTrue("fixture: a zone of all the dry ground wants more than is free", l.zoneSizeOf(Sectors.MINING) > free && free > 0);
        assertTrue("it holds what is free, and nobody else has any", l.heldBy(Sectors.MINING) == free && l.getAvailableSqFt() == 0);
        l.getZoning().set(Sectors.RETAIL, Zoning.Size.KM2, 30, 1);
        double wantM = l.zoneSizeOf(Sectors.MINING), wantR = LandManager.sqFt(30);
        assertTrue("two zones short of ground each hold the same share of what they want, together all of it",
                same(l.heldBy(Sectors.MINING) / wantM, l.heldBy(Sectors.RETAIL) / wantR)
                && same(l.heldBy(Sectors.MINING) + l.heldBy(Sectors.RETAIL), free));
        double heldBefore = l.heldBy(Sectors.MINING);
        quietly(() -> l.setOwnedSqFt(LandManager.sqFt(40)));
        assertTrue("land bought, they fill: the share zone re-read on the bigger ground holds more",
                l.heldBy(Sectors.MINING) > heldBefore && same(l.heldByZones(), l.getFreeSqFt()));
        l.getZoning().set(Sectors.MINING, Zoning.Size.SHARE, .1, .5);
        l.getZoning().remove(Sectors.RETAIL);
        assertTrue("...and a zone smaller than the free ground holds its size, its share of the dry ground now",
                same(l.heldBy(Sectors.MINING), .1 * l.getOwnedSqFt()));
    }

    /* ============================ 6. THE FOREST LAW ============================ */

    static void theForestLaw() {
        out.println("\n--- 6. the forest law: protected forest is never cut or built on, working forest never built on ---");
        LandManager l = office(80);
        double forest = l.getForestSqFt(), wood = l.getOwnedAmount(Resource.FOREST);
        assertTrue("fixture: the ground holds forest, and its wood", forest > LandManager.sqFt(5) && wood > 0);
        l.getZoning().setForestLaw(.4, .6);
        assertTrue("40% protected, 20% working, 40% open, of the forest ground",
                same(l.getProtectedForestSqFt(), .4 * forest) && same(l.getWorkingForestSqFt(), .2 * forest)
                && same(l.getOpenForestSqFt(), .4 * forest));
        assertTrue("the ground that may be built on is the dry ground less what the law keeps",
                same(l.getBuildableSqFt(), l.getOwnedSqFt() - .6 * forest)
                && same(l.getAvailableSqFt(), l.getOwnedSqFt() - .6 * forest - l.getAllocatedSqFt()));
        double free = l.getAvailableSqFt();
        assertTrue("so a plot past it is refused, and every square foot of it can be built", !l.allocate(free + 1) && l.allocate(free));
        assertTrue("built to the last foot, the forest cleared is within the open forest, the kept forest standing",
                l.getForestClearedSqFt() <= l.getOpenForestSqFt() * (1 + 1e-12)
                && same(l.getForestClearedSqFt(), l.getAllocatedSqFt() - l.getBareSqFt()));
        double protectedWood = l.getProtectedWood();
        assertTrue("the protected wood is the protected share of the forest's full stock", same(protectedWood, .4 * wood));
        double cut = l.extract(Resource.FOREST, 10 * wood);
        assertTrue("a cut of ten forests' worth takes only what may be cut: the protected wood still stands",
                cut > 0 && same(l.getRemaining(Resource.FOREST), protectedWood) && l.cuttableForest() <= 1e-9 * wood
                && l.extract(Resource.FOREST, wood) <= 1e-9 * wood);
        l.endMonth();
        assertTrue("a month's regrowth on the cut ground may be cut again; the protected wood stays the protected share",
                l.cuttableForest() > 0 && same(l.getProtectedWood(), .4 * wood));
    }

    /* ============================ 7. CLEARING ============================ */

    static void clearing() {
        out.println("\n--- 7. building on open forest clears it: cut-over ground first, then standing forest; W holds ---");
        LandManager l = office(80);
        Resource f = Resource.FOREST;
        double bare = l.getBareSqFt();
        l.allocate(bare);
        assertTrue("fixture: the bare ground built on to the foot, no forest cleared", l.getForestClearedSqFt() == 0
                && l.getAllocatedSqFt() == bare);
        double km2Wood = World.FOREST_M3_PER_KM2;
        l.extract(f, km2Wood);
        double w = l.getWorldTotal(f);
        double remaining = l.getRemaining(f);
        assertTrue("fixture: a square kilometre's wood cut (E), and W = unowned + remaining + taken out",
                l.getDepletionState()[f.ordinal()] == km2Wood && same(w, l.getUnowned(f) + remaining + l.getExtracted(f)));
        l.allocate(LandManager.sqFt(.5));
        assertTrue("half a square kilometre built past the bare ground clears it, on cut-over ground: its wood moves from E to C",
                same(l.getForestClearedSqFt(), LandManager.sqFt(.5)) && same(l.getDepletionState()[f.ordinal()], km2Wood / 2)
                && same(l.getForestClearedM3(), km2Wood / 2) && same(l.getRemaining(f), remaining)
                && l.getForestClearedThisMonth() == 0);
        l.allocate(LandManager.sqFt(2));
        assertTrue("two more: the last cut-over half first, then a square kilometre and a half of standing forest",
                same(l.getForestClearedSqFt(), LandManager.sqFt(2.5)) && l.getDepletionState()[f.ordinal()] == 0
                && same(l.getForestClearedM3(), 2.5 * km2Wood) && same(l.getRemaining(f), remaining - 1.5 * km2Wood)
                && same(l.getForestClearedThisMonth(), 1.5 * km2Wood));
        assertTrue("...its standing wood counted as taken out: the world's totals hold",
                same(w, l.getUnowned(f) + l.getRemaining(f) + l.getExtracted(f)) && same(l.getExtracted(f), 2.5 * km2Wood));
        l.release(LandManager.sqFt(1));
        double cleared = l.getForestClearedSqFt();
        l.endMonth();
        assertTrue("a demolition leaves it cleared, and the month's regrowth grows none of it back",
                l.getForestClearedSqFt() == cleared && same(l.getRemaining(f), remaining - 1.5 * km2Wood)
                && l.getForestClearedThisMonth() == 0);
        // Ground set by hand past the bare ground (a fixture, an older save): the month's end clears what it needs.
        LandManager hand = office(80);
        hand.setAllocatedSqFt(hand.getBareSqFt() + LandManager.sqFt(1));
        hand.endMonth();
        assertTrue("ground set by hand past the bare ground is cleared at the month's end, its wood taken out",
                same(hand.getForestClearedSqFt(), LandManager.sqFt(1)) && same(hand.getForestClearedM3(), km2Wood)
                && same(hand.getWorldTotal(f), hand.getUnowned(f) + hand.getRemaining(f) + hand.getExtracted(f)));
        // The zones hold bare ground first: others build on the forest sooner.
        LandManager z = office(80);
        z.allocate(z.getBareSqFt() - LandManager.sqFt(1));
        z.getZoning().set(Sectors.AGRICULTURE, Zoning.Size.KM2, 1, 0);
        z.allocate(LandManager.sqFt(.5));
        assertTrue("with a zone holding the last bare square kilometre, a shop's plot clears forest, the zone's bare ground kept",
                same(z.getForestClearedSqFt(), LandManager.sqFt(.5)));
        z.allocate(LandManager.sqFt(1), Sectors.AGRICULTURE);
        assertTrue("...and the fields build on the zone's bare ground without clearing any",
                same(z.getForestClearedSqFt(), LandManager.sqFt(.5)) && same(z.usedBy(Sectors.AGRICULTURE), LandManager.sqFt(1)));
    }

    /* ============================ 8. ZONED FIELDS DO NOT YIELD ============================ */

    static void theFields(Game g) {
        out.println("\n--- 8. zoned fields do not yield to homes: short of homes, the fields build in their zone alone ---");
        // Somebody sleeping outside: most of the houses bought out and demolished.
        BuildingsTemplate house = LongPlaytest.template(g, "House");
        int houses = g.getBuildingManager().countByName("House");
        g.setCashForTest(g.getCash() + 10_000_000);
        quietly(() -> {
            g.buyOutAndDemolish(house, houses * 9 / 10);
            g.toggleNextMonth();
        });
        double outside = g.getFamilies().getStillUnplaced();
        assertTrue("fixture: nine houses in ten demolished, households sleep outside", outside > 0);
        ham.citybuildersim.sectors.Agriculture fields = (ham.citybuildersim.sectors.Agriculture)
                g.getSectors().byKey(Sectors.AGRICULTURE);
        BusinessInvestment plans = g.getBusinessInvestment();
        BusinessInvestment.Decision none = fields.plan(plans, g);
        assertTrue("with no zone the fields yield, as they always have", !none.build && none.reason.contains("short of homes"));
        /*
         * ...AND HANDS FOR A FARM (0.7.113, re-caused). The fields' planner
         * asks the city for a farm's workers before it reads the zone
         * (Sector.staffing(), in planMaker()). This city's two years left it
         * spare hands until the investors were asked the interest on an
         * order's whole cost (Game, ON ITS WHOLE COST, 0.7.113): it builds
         * otherwise since, and its unskilled posts outnumber its unskilled
         * hands - "the city could staff 33% of a Mixed Farm". The question
         * here is the zone, so the city is handed the hands its unskilled
         * posts are short of and FARM_HANDS more for the asks, its workforce
         * put back after.
         */
        PopulationManager people = g.getPopulationManager();
        long workforce = people.getWorkforceForSave();
        double[] posts = people.staffablePostsByBand(), supply = people.supplyByBand(posts);
        int unskilled = WageBand.NONE.ordinal();
        long hands = (long) Math.ceil(Math.max(0, posts[unskilled] - supply[unskilled])) + FARM_HANDS;
        people.restoreWorkforce(workforce + hands);
        quietly(() -> g.setZone(Sectors.AGRICULTURE, Zoning.Size.KM2, 2, 0));
        BusinessInvestment.Decision zoned = fields.plan(plans, g);
        assertTrue("fixture: handed the unskilled hands its posts are short of and FARM_HANDS more, the fields order a farm"
                + " in their zone", zoned.build);
        out.printf("      %,d hands handed%n", hands);
        double held = g.getLandManager().heldBy(Sectors.AGRICULTURE);
        assertTrue("fixture: the fields' zone holds 2 km² at no price", same(held, LandManager.sqFt(2)));
        assertTrue("zoned, they do not: the homes rule stops nothing, and any order fits in the zone",
                !zoned.reason.contains("the city is short of homes - ground")
                && (!zoned.build || zoned.quantity * zoned.template.getLandSqFt() <= held));
        out.printf("      the fields, zoned while %.0f households sleep outside: %s%n", outside,
                zoned.build ? zoned.quantity + " " + zoned.template.getName() : zoned.reason);
        quietly(() -> g.setZone(Sectors.AGRICULTURE, Zoning.Size.KM2, 1e-4, 0));
        BusinessInvestment.Decision tiny = fields.plan(plans, g);
        assertTrue("a zone too small for any farm's plot refuses it, homes still short",
                !tiny.build && tiny.reason.contains("zone holds too little"));
        people.restoreWorkforce(workforce);
        quietly(() -> g.removeZone(Sectors.AGRICULTURE));
    }

    /* ============================ 9. SAVED AND RELOADED ============================ */

    static void savedAndReloaded(Path root, Game g) {
        out.println("\n--- 9. saved and reloaded, the same city: zones, used ground, the law and the forest cleared ---");
        LandManager land = g.getLandManager();
        quietly(() -> {
            g.setZone(Sectors.MINING, Zoning.Size.SHARE, .02, .4);
            g.setForestLaw(.1, .25);
        });
        g.getSectors().byKey(Sectors.RESTAURANTS).addCash(1_000_000);
        g.buildFor(g.getSectorInvestor(Sectors.RESTAURANTS), LongPlaytest.template(g, "Diner"), 1);
        assertTrue("fixture: two zones - one with ground used in it - and a law, set", g.getZoning().zones().size() == 2
                && land.usedBy(Sectors.RESTAURANTS) > 0 && g.getZoning().keptShare() == .25);
        Zoning.State was = land.zoningState();
        quietly(() -> g.saveGame(10, "zoningcheck"));
        Game twin = new Game(new GameFiles(root.resolve("city"), root.resolve("city-no-legacy")));
        quietly(() -> twin.loadGameSave(10));
        twin.getBusinessInvestment().holdSector(Sectors.AGRICULTURE);   // the harness's hold, not the save's
        LandManager back = twin.getLandManager();
        boolean zones = twin.getZoning().zones().size() == g.getZoning().zones().size();
        for (Zoning.Zone z : g.getZoning().zones()) {
            Zoning.Zone b = twin.getZoning().zone(z.sector());
            zones &= b != null && b.kind() == z.kind() && b.size() == z.size() && b.priceShare() == z.priceShare()
                    && b.usedSqFt() == z.usedSqFt();
        }
        assertTrue("every zone back: its sector, size and kind, price share and the ground used in it", zones);
        assertTrue("...the law's two shares, and the forest cleared, its ground and wood",
                twin.getZoning().protectedShare() == g.getZoning().protectedShare()
                && twin.getZoning().keptShare() == g.getZoning().keptShare()
                && back.getForestClearedSqFt() == was.clearedSqFt && back.getForestClearedM3() == was.clearedM3);
        assertTrue("...and the ground as the live city reads it: free for anyone, each zone's held",
                back.getAvailableSqFt() == land.getAvailableSqFt() && back.heldBy(Sectors.MINING) == land.heldBy(Sectors.MINING)
                && back.heldBy(Sectors.RESTAURANTS) == land.heldBy(Sectors.RESTAURANTS));
        quietly(() -> { g.toggleNextMonth(); twin.toggleNextMonth(); });
        assertTrue("a month on, the two are the same month: cash, ground, the zones' held ground, the businesses' cash",
                Math.abs(g.getCash() - twin.getCash()) < 1e-9 && land.getAllocatedSqFt() == back.getAllocatedSqFt()
                && land.heldBy(Sectors.MINING) == back.heldBy(Sectors.MINING)
                && land.usedBy(Sectors.RESTAURANTS) == back.usedBy(Sectors.RESTAURANTS)
                && Math.abs(g.getSectors().totalCash() - twin.getSectors().totalCash()) < 1e-9
                && land.getForestClearedSqFt() == back.getForestClearedSqFt());
    }
}
