package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Policy › Zoning's figures (0.7.109, batch Z2): ZoningView, and the words
 * zoning puts on the screens it does not own - NEEDS YOU's GROUND row, the
 * build card's land gate, an order's quote - held to the model's own reads.
 *
 * WHY. The screens Z2 draws (ui/ZoningScreen) paint ZoningView's figures
 * and nothing else, so a figure the view worked out wrong is a figure the
 * player decides by. A bar whose parts did not add up to the dry ground, a
 * held ground that was not what the zone keeps from everyone else, a staged
 * zone whose "after" was not what the city reads once it is applied, or a
 * build card that refused a zoned sector's building the ground its own zone
 * holds would each be a screen that says one thing while the model does
 * another. Nothing in the default playtest sets a zone, so this is the
 * only place the screens' figures are played.
 *
 * What this has to prove:
 *   1. NOTHING SET: the four figures read nothing zoned, nothing held,
 *      nothing given up and the forest kept "none"; the bar is the built
 *      ground and the bare ground and open forest free for anyone, adding
 *      up to the dry ground; no zone has a row.
 *   2. THE HUB'S FIGURES ARE THE MODEL'S: zoned is the zones' sizes,
 *      held is what they hold, the price given up and the relief are
 *      PolicyPreview's, the forest kept is the protected and working forest.
 *   3. THE GROUND BAR adds up to the dry ground: each zone's used and held
 *      ground its own, the bare ground and open forest free for anyone
 *      LandManager.freeForAnyone(), the built ground the allocated less the
 *      zones' used, the working and protected forest the law's.
 *   4. A ZONE'S ROW is its zone: size, share, used, held, its price a
 *      square foot, its three rates and offsets (one dial, two doors: a
 *      move through the zone's door reads on its row and on Policy › Taxes),
 *      what each tax raised last month; a zone bigger than the free ground
 *      reads short.
 *   5. A SECTOR'S PAGE, STAGED, MOVES NOTHING, and applied reads as staged:
 *      what the zone holds, the ground free for anyone, what it carries
 *      and a plot at its price - for a new zone, a re-sized one and one
 *      taken off.
 *   6. THE FOREST LAW'S PAGE, the same: the three kinds, the wood kept
 *      standing for ever and the wood that may be cut.
 *   7. ZONING HAS A DECISION KIND OF ITS OWN: a zone set, re-priced or taken
 *      off and the law each record one ZONING line, nothing when nothing
 *      moves; a zone's tax row records TAX, being the sector's offset.
 *   8. THE WORDS ELSEWHERE: listed, NEEDS YOU's GROUND row names what the
 *      zones hold (not the homes' own zone); the build card's land gate and
 *      an order's quote count a zoned sector's zone, and an unzoned
 *      building's gate says what is zoned for others.
 *
 * Every fixture causes its condition.
 */
public class ZoningViewCheck {

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

    /** ...and two ground figures the same to a part in a billion of the larger, or of the dry ground they are cut from: one rounding, not a square foot. */
    static boolean same(double a, double b) { return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b))); }

    static boolean same(double a, double b, double whole) { return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.abs(whole)); }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** ZoningCheck's orders: a small city with homes, shops, diners and works. */
    static final String[][] ORDERS = {
            { "House", "580" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Construction Depot", "6" },
            { "Coal Power Plant", "2" }, { "Water Treatment Plant", "2" }, { "Industrial Bakery", "3" },
            { "Commercial Bank", "1" }, { "Elementary School", "3" }, { "Walk-in Clinic", "3" }, { "Paved Road", "20" } };

    /** The ground the city is drawn to round the default world's founding site: ZoningCheck's land office's 80 km², forest and bare ground both. */
    static final double CITY_KM2 = 80;

    /** A played city, ZoningCheck's: the orders put up finished out of a Wealthy treasury, its ground drawn to CITY_KM2, then `months` played. */
    static Game city(Path root, String name, int months) {
        Game g = new Game(new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy")));
        quietly(() -> {
            g.run();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getGovernmentInvestor().spend(-2_000_000);
            g.getLandManager().setOwnedSqFt(LandManager.sqFt(CITY_KM2));
            g.getBusinessInvestment().holdSector(Sectors.AGRICULTURE);
            for (String[] w : ORDERS) {
                BuildingsTemplate t = LongPlaytest.template(g, w[0]);
                if (t != null) g.buildStack(t, Integer.parseInt(w[1]), true);
            }
            g.simulateMonths(months);
        });
        return g;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("zoningviewcheck");

        Game g = city(root, "city", 12);
        LandManager land = g.getLandManager();
        out.printf("%n      the city: month %d, %,d people; %s dry, %s of it forest, %s built on%n",
                g.getMonth(), g.getPopulationManager().getPopulation(), LandManager.areaWords(land.getOwnedSqFt()),
                LandManager.areaWords(land.getForestSqFt()), LandManager.areaWords(land.getAllocatedSqFt()));
        nothingSet(g);
        zoned(g);
        theHubsFigures(g);
        theGroundBar(g);
        theRows(g);
        aSectorsPage(g);
        theForestLaw(g);
        theDecisionKind(g);
        theWordsElsewhere(g);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. NOTHING SET ============================ */

    static void nothingSet(Game g) {
        out.println("\n--- 1. nothing set: nothing zoned, held or given up, the forest kept \"none\", the bar the built and the free ---");
        LandManager land = g.getLandManager();
        assertTrue("fixture: a played city with no zone and the law at 0 and 0, built on, with forest",
                g.getZoning().isEmpty() && land.getAllocatedSqFt() > 0 && land.getForestSqFt() > 0);
        ZoningView.Figures f = ZoningView.figures(g);
        assertTrue("the four: nothing zoned in no zone, nothing held, no price given up, no relief, no law",
                f.zonedSqFt() == 0 && f.zones() == 0 && f.heldSqFt() == 0 && f.priceGivenUp() == 0 && f.taxRelief() == 0
                && !f.lawSet() && f.keptSqFt() == 0 && f.drySqFt() == land.getOwnedSqFt());
        List<ZoningView.Part> bar = ZoningView.ground(g);
        assertTrue("the bar: the built ground is the allocated; the bare ground and open forest are free for anyone",
                ZoningView.total(bar, ZoningView.Kind.BUILT) == land.getAllocatedSqFt()
                && same(ZoningView.total(bar, ZoningView.Kind.BARE) + ZoningView.total(bar, ZoningView.Kind.OPEN),
                        land.freeForAnyone(), land.getOwnedSqFt())
                && ZoningView.total(bar, ZoningView.Kind.WORKING) == 0 && ZoningView.total(bar, ZoningView.Kind.PROTECTED) == 0);
        assertTrue("...and it adds up to the dry ground the city owns", same(ZoningView.total(bar), land.getOwnedSqFt()));
        assertTrue("no zone has a row, and the closer bar is the bare ground alone",
                ZoningView.zones(g).isEmpty() && ZoningView.closer(g).size() == 1
                && ZoningView.closer(g).get(0).kind() == ZoningView.Kind.BARE);
    }

    /** Two zones - Mining's a share at 40% of the going price, Restaurants' in km² for nothing, a Diner built in it - and a law. */
    static void zoned(Game g) {
        quietly(() -> {
            g.setZone(Sectors.MINING, Zoning.Size.SHARE, .02, .4);
            g.setZone(Sectors.RESTAURANTS, Zoning.Size.KM2, .5, 0);
            g.setForestLaw(.1, .25);
            g.getSectors().byKey(Sectors.RESTAURANTS).addCash(1_000_000);
            g.buildFor(g.getSectorInvestor(Sectors.RESTAURANTS), LongPlaytest.template(g, "Diner"), 1);
            g.toggleNextMonth();
        });
        out.printf("%n      zoned: Mining 2%% of the dry ground at 40%%, Restaurants 0.5 km² free, a Diner in it; "
                + "the law 10%% protected, 25%% kept%n");
    }

    /* ============================ 2. THE HUB'S FIGURES ============================ */

    static void theHubsFigures(Game g) {
        out.println("\n--- 2. the hub's four figures are the model's ---");
        LandManager land = g.getLandManager();
        Zoning z = g.getZoning();
        assertTrue("fixture: two zones, one with ground used in it and both holding ground, and a law",
                z.zones().size() == 2 && land.usedBy(Sectors.RESTAURANTS) > 0 && land.heldBy(Sectors.MINING) > 0
                && land.heldBy(Sectors.RESTAURANTS) > 0 && z.keptShare() == .25);
        ZoningView.Figures f = ZoningView.figures(g);
        double zoned = land.zoneSizeOf(Sectors.MINING) + land.zoneSizeOf(Sectors.RESTAURANTS);
        assertTrue("ZONED: the zones' sizes together, two of them, and their share of the dry ground",
                same(f.zonedSqFt(), zoned) && f.zones() == 2 && same(f.zonedShare(), zoned / land.getOwnedSqFt()));
        assertTrue("HELD FOR THEM: what the zones hold, LandManager.heldByZones()", f.heldSqFt() == land.heldByZones());
        PolicyPreview.ZoneAt mining = PolicyPreview.zoneAt(g, z, Sectors.MINING);
        PolicyPreview.ZoneAt diners = PolicyPreview.zoneAt(g, z, Sectors.RESTAURANTS);
        assertTrue("PRICE GIVEN UP: PolicyPreview's price given up and tax relief, summed over the zones",
                near(f.priceGivenUp(), mining.priceGivenUp() + diners.priceGivenUp())
                && near(f.taxRelief(), mining.taxRelief() + diners.taxRelief()) && f.priceGivenUp() > 0);
        assertTrue("FOREST KEPT: the protected and the working forest, the law's two shares",
                same(f.keptSqFt(), land.getProtectedForestSqFt() + land.getWorkingForestSqFt())
                && f.protectedSqFt() == land.getProtectedForestSqFt() && f.lawSet()
                && same(f.keptSqFt(), .25 * land.getForestSqFt()));
        out.printf("      zoned %s in %d zones, held %s, given up $%,.1fk, relief $%,.3fk a month, kept %s%n",
                LandManager.areaWords(f.zonedSqFt()), f.zones(), LandManager.areaWords(f.heldSqFt()), f.priceGivenUp(),
                f.taxRelief(), LandManager.areaWords(f.keptSqFt()));
    }

    /* ============================ 3. THE GROUND BAR ============================ */

    static void theGroundBar(Game g) {
        out.println("\n--- 3. the ground bar adds up to the dry ground, each part the model's ---");
        LandManager land = g.getLandManager();
        List<ZoningView.Part> bar = ZoningView.ground(g);
        double dry = land.getOwnedSqFt();
        assertTrue("its parts add up to the dry ground the city owns", same(ZoningView.total(bar), dry));
        boolean zones = true;
        for (ZoningView.Part p : bar) {
            if (p.kind() == ZoningView.Kind.USED) zones &= p.sqFt() == land.usedBy(p.sector());
            if (p.kind() == ZoningView.Kind.HELD) zones &= p.sqFt() == land.heldBy(p.sector());
        }
        assertTrue("each zone's used and held ground is its own, used before held, in the zones' order",
                zones && bar.get(1).kind() == ZoningView.Kind.USED && Sectors.MINING.equals(bar.get(1).sector())
                && bar.get(4).kind() == ZoningView.Kind.HELD && Sectors.RESTAURANTS.equals(bar.get(4).sector()));
        assertTrue("the built ground and the zones' used ground are the allocated",
                same(ZoningView.total(bar, ZoningView.Kind.BUILT) + ZoningView.total(bar, ZoningView.Kind.USED),
                        land.getAllocatedSqFt(), dry));
        assertTrue("the bare ground and the open forest free for anyone are freeForAnyone()",
                same(ZoningView.total(bar, ZoningView.Kind.BARE) + ZoningView.total(bar, ZoningView.Kind.OPEN),
                        land.freeForAnyone(), dry));
        assertTrue("...and the zones' held ground HELD FOR THEM", same(ZoningView.total(bar, ZoningView.Kind.HELD), land.heldByZones(), dry));
        assertTrue("the working and protected forest are the law's",
                ZoningView.total(bar, ZoningView.Kind.WORKING) == land.getWorkingForestSqFt()
                && ZoningView.total(bar, ZoningView.Kind.PROTECTED) == land.getProtectedForestSqFt());
        List<ZoningView.Part> closer = ZoningView.closer(g);
        assertTrue("the closer bar: the zones' used and held ground and the bare ground free for anyone, no more",
                closer.size() == 5 && same(ZoningView.total(closer), ZoningView.total(bar, ZoningView.Kind.USED)
                        + ZoningView.total(bar, ZoningView.Kind.HELD) + ZoningView.total(bar, ZoningView.Kind.BARE), dry));
        for (ZoningView.Part p : bar) {
            out.printf("      %-10s %-12s %s%n", p.kind(), p.sector() == null ? "" : p.sector(), LandManager.areaWords(p.sqFt()));
        }
    }

    /* ============================ 4. THE ROWS ============================ */

    static void theRows(Game g) {
        out.println("\n--- 4. a zone's row is its zone, its taxes the sector's own ---");
        LandManager land = g.getLandManager();
        EconomyManager em = g.getEconomyManager();
        List<ZoningView.Row> rows = ZoningView.zones(g);
        assertTrue("a row a zone, in the zones' order", rows.size() == 2 && Sectors.MINING.equals(rows.get(0).sector())
                && Sectors.RESTAURANTS.equals(rows.get(1).sector()));
        ZoningView.Row mining = rows.get(0), diners = rows.get(1);
        assertTrue("its size as given and as ground, its share of the dry ground",
                mining.kind() == Zoning.Size.SHARE && mining.size() == .02 && same(mining.share(), .02)
                && diners.kind() == Zoning.Size.KM2 && same(diners.sizeSqFt(), LandManager.sqFt(.5))
                && same(diners.share(), LandManager.sqFt(.5) / land.getOwnedSqFt()));
        assertTrue("its used and held ground, and its price a square foot (groundPriceFor())",
                diners.usedSqFt() == land.usedBy(Sectors.RESTAURANTS) && diners.heldSqFt() == land.heldBy(Sectors.RESTAURANTS)
                && mining.pricePerSqFt() == land.groundPriceFor(Sectors.MINING) && diners.pricePerSqFt() == 0
                && same(mining.pricePerSqFt(), .4 * land.getPricePerSqFt()));
        boolean rates = true;
        for (ZoningView.Row r : rows) {
            for (Zoning.Tax t : Zoning.Tax.values()) rates &= r.rate()[t.ordinal()] == g.zoneTaxRate(r.sector(), t);
        }
        assertTrue("its three rates are the sector's (Game.zoneTaxRate())", rates);
        assertTrue("what each raised last month: the books' profit tax, the ledger's net sales tax, the property charged",
                diners.paid()[0] == g.getSectorBooks().get(Sectors.RESTAURANTS).tax()
                && diners.paid()[1] == em.getSalesTaxLedger().getNet(Sectors.RESTAURANTS)
                && diners.paid()[2] == em.getPropertyTaxCharged(Sectors.RESTAURANTS));
        quietly(() -> g.setZoneTax(Sectors.RESTAURANTS, Zoning.Tax.SALES, -.02));
        ZoningView.Row moved = ZoningView.row(g, Sectors.RESTAURANTS);
        TaxPolicy p = em.getTaxPolicy();
        assertTrue("one dial, two doors: a move through the zone's door reads on its row and is Policy › Taxes' offset",
                moved.offset()[1] == -.02 && p.getSalesOffset(Sectors.RESTAURANTS) == -.02
                && moved.rate()[1] == p.effectiveSalesRate(Sectors.RESTAURANTS));
        quietly(() -> g.setZoneTax(Sectors.RESTAURANTS, Zoning.Tax.SALES, 0));
        assertTrue("neither zone is short while the free ground covers both", !mining.isShort() && !diners.isShort());
        Zoning big = ZoningView.with(g.getZoning(), Sectors.MANUFACTURING, Zoning.Size.SHARE, 1, 1);
        double held = land.heldUnder(big, Sectors.MANUFACTURING);
        assertTrue("fixture: a zone of all the dry ground holds less than it wants", held < land.getOwnedSqFt());
        quietly(() -> g.setZone(Sectors.MANUFACTURING, Zoning.Size.SHARE, 1, 1));
        ZoningView.Row whole = ZoningView.row(g, Sectors.MANUFACTURING);
        assertTrue("...and its row reads short, holding what is free (Z10)", whole.isShort() && whole.heldSqFt() == land.heldBy(Sectors.MANUFACTURING));
        quietly(() -> g.removeZone(Sectors.MANUFACTURING));
        assertTrue("a sector with no zone has no row", ZoningView.row(g, Sectors.MANUFACTURING) == null);
    }

    /* ============================ 5. A SECTOR'S PAGE ============================ */

    static boolean samePage(ZoningView.Page a, ZoningView.Page b) {
        return same(a.zone().heldSqFt(), b.zone().heldSqFt()) && same(a.freeForAnyone(), b.freeForAnyone())
                && same(a.zone().sizeSqFt(), b.zone().sizeSqFt()) && a.carries() == b.carries()
                && near(a.plotAtZone(), b.plotAtZone()) && near(a.zone().priceGivenUp(), b.zone().priceGivenUp())
                && near(a.zone().heldAtGoingPrice(), b.zone().heldAtGoingPrice());
    }

    static void aSectorsPage(Game g) {
        out.println("\n--- 5. a sector's page: staged it moves nothing, applied it reads as staged ---");
        LandManager land = g.getLandManager();
        double anyone = land.freeForAnyone(), mining = land.heldBy(Sectors.MINING);
        int decisions = g.getDecisions().size();

        // A new zone: Retail, 1 km² at half the going price.
        Zoning staged = ZoningView.with(g.getZoning(), Sectors.RETAIL, Zoning.Size.KM2, 1, .5);
        ZoningView.Page before = ZoningView.page(g, g.getZoning(), Sectors.RETAIL);
        ZoningView.Page after = ZoningView.page(g, staged, Sectors.RETAIL);
        assertTrue("staged, the city is untouched: no Retail zone, the ground free for anyone and Mining's held as they were, no decision",
                g.getZoning().zone(Sectors.RETAIL) == null && land.freeForAnyone() == anyone
                && land.heldBy(Sectors.MINING) == mining && g.getDecisions().size() == decisions);
        BuildingsTemplate plot = ZoningView.plotOf(g, Sectors.RETAIL);
        assertTrue("before, no zone: it holds nothing and carries nothing, and a plot pays the going price",
                before.zone().heldSqFt() == 0 && before.carries() == 0 && near(before.plotAtZone(), before.plotAtGoing()));
        assertTrue("after: it holds the km² (the free ground covers it), the ground free for anyone falls by as much",
                same(after.zone().heldSqFt(), LandManager.sqFt(1)) && same(after.freeForAnyone(), anyone - LandManager.sqFt(1), anyone));
        assertTrue("...it carries as many of the plot the sector builds most of as fit, and a plot pays half",
                plot != null && "Convenience Store".equals(plot.getName())
                && after.carries() == (long) Math.floor(LandManager.sqFt(1) / plot.getLandSqFt() + 1e-9)
                && near(after.plotAtZone(), after.plotAtGoing() / 2));
        quietly(() -> g.setZone(Sectors.RETAIL, Zoning.Size.KM2, 1, .5));
        assertTrue("applied, the city reads what was staged", samePage(after, ZoningView.page(g, g.getZoning(), Sectors.RETAIL)));

        // Re-sized and re-priced: Mining to 3% at nothing.
        Zoning resized = ZoningView.with(g.getZoning(), Sectors.MINING, Zoning.Size.SHARE, .03, 0);
        ZoningView.Page staged2 = ZoningView.page(g, resized, Sectors.MINING);
        assertTrue("a zone re-sized and re-priced, staged, keeps what it has used",
                staged2.zone().usedSqFt() == land.usedBy(Sectors.MINING) && staged2.zone().priceShare() == 0
                && staged2.plotAtZone() == 0);
        quietly(() -> g.setZone(Sectors.MINING, Zoning.Size.SHARE, .03, 0));
        assertTrue("...and applied reads as staged", samePage(staged2, ZoningView.page(g, g.getZoning(), Sectors.MINING)));

        // Taken off.
        Zoning off = ZoningView.without(g.getZoning(), Sectors.RETAIL);
        ZoningView.Page gone = ZoningView.page(g, off, Sectors.RETAIL);
        assertTrue("taken off, staged: it holds nothing, and its ground is free for anyone again",
                gone.zone().heldSqFt() == 0 && same(gone.freeForAnyone(), land.freeForAnyone() + land.heldBy(Sectors.RETAIL), anyone));
        quietly(() -> g.removeZone(Sectors.RETAIL));
        assertTrue("...and applied reads as staged", samePage(gone, ZoningView.page(g, g.getZoning(), Sectors.RETAIL)));
        quietly(() -> g.setZone(Sectors.MINING, Zoning.Size.SHARE, .02, .4));
    }

    /* ============================ 6. THE FOREST LAW ============================ */

    static void theForestLaw(Game g) {
        out.println("\n--- 6. the forest law's page: staged it moves nothing, applied it reads as staged ---");
        LandManager land = g.getLandManager();
        double cuttable = land.cuttableForest(), protectedWood = land.getProtectedWood();
        ZoningView.Forest now = ZoningView.forest(g, g.getZoning());
        assertTrue("at the city's own law the page is the model's: the three kinds, the wood kept and the wood that may be cut",
                now.at().protectedSqFt() == land.getProtectedForestSqFt() && now.at().workingSqFt() == land.getWorkingForestSqFt()
                && now.at().openSqFt() == land.getOpenForestSqFt() && same(now.at().cuttableWood(), cuttable)
                && same(now.at().protectedWood(), protectedWood) && now.standingM3() == land.getRemaining(Resource.FOREST));
        // Kept under protected: the model's clamp lifts it.
        Zoning staged = ZoningView.withLaw(g.getZoning(), .4, .3);
        ZoningView.Forest after = ZoningView.forest(g, staged);
        assertTrue("staged 40% protected and 30% kept, the kept is the protected (the model's clamp), and nothing in the city moves",
                staged.keptShare() == .4 && g.getZoning().protectedShare() == .1 && land.cuttableForest() == cuttable
                && same(after.at().protectedSqFt(), .4 * land.getForestSqFt()) && after.at().workingSqFt() == 0);
        assertTrue("...more wood kept standing for ever, less that may be cut, less forest a building may take",
                after.at().protectedWood() > now.at().protectedWood() && after.at().cuttableWood() < now.at().cuttableWood()
                && after.at().openSqFt() < now.at().openSqFt());
        quietly(() -> g.setForestLaw(.4, .3));
        ZoningView.Forest applied = ZoningView.forest(g, g.getZoning());
        assertTrue("applied, the city reads what was staged",
                same(applied.at().protectedSqFt(), after.at().protectedSqFt()) && same(applied.at().openSqFt(), after.at().openSqFt())
                && same(applied.at().cuttableWood(), after.at().cuttableWood()) && same(applied.at().freeForAnyone(), after.at().freeForAnyone())
                && same(land.cuttableForest(), after.at().cuttableWood()));
        out.printf("      at 40%%: %s protected, %s open, %,.0f m³ kept standing, %,.0f m³ may be cut%n",
                LandManager.areaWords(applied.at().protectedSqFt()), LandManager.areaWords(applied.at().openSqFt()),
                applied.at().protectedWood(), applied.at().cuttableWood());
        quietly(() -> g.setForestLaw(.1, .25));
    }

    /* ============================ 7. THE DECISION KIND ============================ */

    /** The hand, then exactly one new entry of this kind. */
    static boolean records(Game g, String kind, Runnable hand) {
        DecisionLog log = g.getDecisions();
        int before = log.size();
        quietly(hand);
        return log.size() == before + 1 && kind.equals(log.last().kind());
    }

    static void theDecisionKind(Game g) {
        out.println("\n--- 7. zoning has a decision kind of its own ---");
        assertTrue("a zone set: one ZONING line", records(g, DecisionLog.ZONING,
                () -> g.setZone(Sectors.RETAIL, Zoning.Size.SHARE, .01, 1)));
        assertTrue("...re-priced: one ZONING line", records(g, DecisionLog.ZONING,
                () -> g.setZone(Sectors.RETAIL, Zoning.Size.SHARE, .01, .5)));
        DecisionLog log = g.getDecisions();
        int before = log.size();
        quietly(() -> g.setZone(Sectors.RETAIL, Zoning.Size.SHARE, .01, .5));
        assertTrue("...set to what it already is: nothing", log.size() == before);
        assertTrue("...taken off: one ZONING line", records(g, DecisionLog.ZONING, () -> g.removeZone(Sectors.RETAIL)));
        assertTrue("the forest law: one ZONING line", records(g, DecisionLog.ZONING, () -> g.setForestLaw(.2, .25)));
        assertTrue("a zone's tax row: one TAX line - the sector's own offset, as Policy › Taxes logs it",
                records(g, DecisionLog.TAX, () -> g.setZoneTax(Sectors.MINING, Zoning.Tax.PROFIT, .01)));
        quietly(() -> { g.setZoneTax(Sectors.MINING, Zoning.Tax.PROFIT, 0); g.setForestLaw(.1, .25); });
    }

    /* ============================ 8. THE WORDS ELSEWHERE ============================ */

    static void theWordsElsewhere(Game g) {
        out.println("\n--- 8. the words elsewhere: NEEDS YOU's GROUND row, the build card's land gate, an order's quote ---");
        LandManager land = g.getLandManager();
        double allocated = land.getAllocatedSqFt();
        CityNeeds.Need roomy = CityNeeds.ground(g, CityNeeds.PLAIN);
        assertTrue("with room to build the GROUND row is not listed and names no zone",
                roomy.level() == 0 && !roomy.reading().contains("zone"));
        // Ground set by hand: a block and a half free, short of what the two zones want.
        double free = 1.5 * LandManager.BLOCK_SQ_FT;
        land.setAllocatedSqFt(land.getOwnedSqFt() - .25 * land.getForestSqFt() - free);
        try {
            assertTrue("fixture: the zones hold all the free ground, so none is free for anyone",
                    same(land.heldByZones(), free) && land.freeForAnyone() < 1e-6 * free);
            CityNeeds.Need row = CityNeeds.ground(g, CityNeeds.PLAIN);
            assertTrue("listed, the GROUND row names what the zones hold: \"" + row.reading() + "\"",
                    row.level() == 2 && row.reading().endsWith("2 zones hold " + LandManager.areaWords(land.heldByZones())));
            BuildingsTemplate diner = LongPlaytest.template(g, "Diner"), store = LongPlaytest.template(g, "Convenience Store");
            BuildCard.Appraisal zoned = BuildCard.appraise(g, diner);
            assertTrue("fixture: a Diner's plot fits in what Restaurants' zone holds, and is more than is free for anyone",
                    diner.getLandSqFt() <= land.heldBy(Sectors.RESTAURANTS) && diner.getLandSqFt() > land.freeForAnyone());
            assertTrue("the build card's land gate counts the zone: a Diner passes it", zoned.at(BuildCard.GateKind.LAND) == BuildCard.Mark.PASS);
            assertTrue("...an order's quote reads the ground its sector may build on, freeFor()",
                    g.quoteBuild(diner, 1).landFree == land.freeFor(Sectors.RESTAURANTS)
                    && BuildCard.of(g, diner, null).landFree() == land.freeFor(Sectors.RESTAURANTS));
            BuildCard.Appraisal shop = BuildCard.appraise(g, store);
            BuildCard.Gate gate = null;
            for (BuildCard.GateMark m : shop.gates()) if (m.kind() == BuildCard.GateKind.LAND) gate = m.failure();
            assertTrue("an unzoned shop fails it, and its gate says what is zoned for others: \"" + (gate == null ? "" : gate.why()) + "\"",
                    gate != null && BusinessInvestment.zonedForOthersWords(land.heldByZones()).equals(gate.why()));
            quietly(() -> {
                g.removeZone(Sectors.MINING);
                g.removeZone(Sectors.RESTAURANTS);
                g.setZone(Sectors.REAL_ESTATE, Zoning.Size.KM2, 1, 1);
            });
            CityNeeds.Need homes = CityNeeds.ground(g, CityNeeds.PLAIN);
            assertTrue("the homes' own zone is not named: the GROUND row says only what is free",
                    homes.level() == 2 && !homes.reading().contains("zone") && land.heldBy(Sectors.REAL_ESTATE) > 0);
            BuildCard.Appraisal alone = BuildCard.appraise(g, store);
            BuildCard.Gate own = null;
            for (BuildCard.GateMark m : alone.gates()) if (m.kind() == BuildCard.GateKind.LAND) own = m.failure();
            assertTrue("...though to a shop it is ground zoned for others", own != null && own.why() != null);
        } finally {
            land.setAllocatedSqFt(allocated);
            quietly(() -> g.removeZone(Sectors.REAL_ESTATE));
        }
        BuildCard.Appraisal none = BuildCard.appraise(g, LongPlaytest.template(g, "Convenience Store"));
        BuildCard.Gate unzoned = null;
        for (BuildCard.GateMark m : none.gates()) if (m.kind() == BuildCard.GateKind.LAND) unzoned = m.failure();
        assertTrue("with no zone and room, the shop passes the land gate as it always did",
                none.at(BuildCard.GateKind.LAND) == BuildCard.Mark.PASS && unzoned == null);
    }
}
