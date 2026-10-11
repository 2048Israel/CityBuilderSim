package ham.citybuildersim;

import ham.citybuildersim.sectors.CementConcrete;
import ham.citybuildersim.sectors.ExtractionSector;
import ham.citybuildersim.sectors.Quarrying;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Quarrying, checked (0.7.131, batch I7; runs/spec-materials.md 2.1, 2.3,
 * 2.4, 2.10, 4 and 5's I7 row; the study's - materials-chains-research.md -
 * 1.4, 6 and 8): the third materials chain's digging industry, on the end of
 * Sectors.KEYS after Coal Mining.
 *
 * WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md, "adding
 * things"; I6's CoalMiningCheck the same), and stone brings the one market
 * rule new to the game (the study's 6.1, Jerus's Q17): a limestone quarry's
 * limestone no buyer here takes sells as aggregate, one way - and with it
 * the packing that says which quarry is on which field.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE CARRY: a limestone quarry with no buyer sells its tonnes as
 *      aggregate, tonne for tonne - nothing booked on limestone, the tonnes
 *      shipped at aggregate's export price - the ground it drew still
 *      limestone's, E rising on limestone alone and W exact; with a buyer
 *      (a Lime Kiln) limestone clears first, the kiln takes its limestone
 *      at home and only the rest is carried.
 *
 *   2. THE SECTOR ON THE END OF THE REGISTRY: Sectors.KEYS ends with
 *      Quarrying and Cement & Concrete after Coal Mining (Q21's order); an
 *      ExtractionSector on stone making limestone, aggregate and blocks;
 *      what grew with the keys - Equity's companies, the households' share
 *      block, the audit's pools, the bank's year of sheets. Resource.STONE
 *      is priced: AGGREGATE its good, LIMESTONE or AGGREGATE by type, 1/400
 *      of aggregate's export price in the ground - in an offer's price, and
 *      no longer bare ground. The three goods are the study's 6.3.
 *
 *   3. THE THREE ROWS ARE THE STUDY'S (ids 115 to 117, at parity): cost,
 *      posts, draw, stone, sites, type, dig, ground, consumables, and their
 *      four cases. The study gives stone's returns in words (its 6.5), so
 *      the comparison is worked from its own figures - each case's stone at
 *      the 6.3 prices, less the 6.4 running cost, over the 6.4 cost - and the
 *      model's cases (SpreadPlanner.caseEarns()) are those to its rounding;
 *      then the returns it states: 1.6-1.7% exporting at D$10, about 10%
 *      replacing imports at D$40.
 *
 *   4. A HARD-ROCK QUARRY MAKES AGGREGATE ONLY, and the Dimension Stone
 *      Quarry stands only on hard rock.
 *
 *   5. THE PACKING: quarries on ANY go onto limestone first (fits(),
 *      LandManager.packed()) - a works on a type before them, the larger
 *      first - and a quarry's sites on limestone make limestone, on hard rock
 *      aggregate (getCapacity(), getPipeline(), madeBy()); the order gate
 *      (Game.hasDepositFor()) agrees.
 *
 *   6. THE DIG BY TYPE: each good comes out of its type's ground at its dig
 *      (blocks three tonnes of rock a tonne), W exact; with limestone worked
 *      out a quarry on ANY digs hard rock; the blocks are spare once hard
 *      rock is.
 *
 *   7. THE PLAN: one more quarry's estimate is its increment to the packing,
 *      its limestone past the city's room for it valued as aggregate (the
 *      carry); the plan orders the best on the whole cost of those that
 *      fit, staff and pay - or says why none.
 *
 *   8. A PLAYED MONTH closes the money audit.
 *
 * Every fixture causes its condition: a town founded on the stone field of
 * the type the section needs nearest the default founding site, limestone by
 * fiat where it needs both (ground listed by fiat is the first type).
 */
public class QuarryingCheck {

    static int fails = 0;
    static final PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-100s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        carry();
        sector();
        rows();
        hardRock();
        packing();
        dig();
        plan();
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static final Resource STONE = Resource.STONE;
    static final Deposit.FieldType LIME = Deposit.FieldType.LIMESTONE, HARD = Deposit.FieldType.HARD_ROCK;

    /* ============================ 1. the carry ============================ */

    static void carry() {
        out.println("--- 1. the carry: limestone no buyer takes sells as aggregate, tonne for tonne; the ground stays limestone's ---");
        // ...first by hand, on a bare registry: the month's flow made, nothing sold here, the unsold shipped.
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        Markets markets = new Markets();
        Sectors s = new Sectors(b, markets);
        Quarrying q = s.quarrying();
        b.addStack(b.getTemplate(Quarrying.QUARRY), 1, true);
        report("fixture: a Quarry standing on no ground the city owns reads as on limestone, the first type: 40,000 t of limestone",
                q.getCapacity(Good.LIMESTONE) == 40_000 && q.getCapacity(Good.AGGREGATE) == 0,
                String.format("%,.0f t limestone, %,.0f t aggregate", q.getCapacity(Good.LIMESTONE), q.getCapacity(Good.AGGREGATE)));
        q.produceFlow(Good.AGGREGATE);
        q.produceFlow(Good.LIMESTONE);
        double made = q.outputRow(Good.LIMESTONE).produced;
        GoodsMarket lime = markets.get(Good.LIMESTONE), agg = markets.get(Good.AGGREGATE);
        lime.startMonth();
        agg.startMonth();
        double exportsBefore = q.pending().exports;
        q.shipUnsoldFlow(Good.LIMESTONE, lime);
        report("limestone's unsold tonnes book nothing on limestone and are carried into the month's aggregate, tonne for tonne",
                made > 0 && q.carried() == made && q.outputRow(Good.LIMESTONE).produced == 0
                        && q.outputRow(Good.AGGREGATE).produced == made && lime.getExported() == 0
                        && q.pending().exports == exportsBefore && q.outputRow(Good.LIMESTONE).exported == 0,
                String.format("%,.0f t made, %,.0f carried", made, q.carried()));
        q.shipUnsoldFlow(Good.AGGREGATE, agg);
        report("...and aggregate, clearing after it (Good's order), ships them at aggregate's export price",
                Good.LIMESTONE.ordinal() < Good.AGGREGATE.ordinal() && q.outputRow(Good.AGGREGATE).exported == made
                        && agg.getExported() == made
                        && Math.abs(q.pending().exports - exportsBefore - made * agg.exportPrice()) < 1e-6,
                String.format("%,.0f t at D$%.4fk", q.outputRow(Good.AGGREGATE).exported, agg.exportPrice()));
        assertTrue("...one way only: a quarry's aggregate is never carried into limestone",
                q.outputRow(Good.LIMESTONE).produced == 0 && q.outputRow(Good.LIMESTONE).soldLocal == 0);

        // ...then a played month, in a town on a limestone field.
        Game g = stoneTown("quarryingcheck-carry", LIME, 0);
        Quarrying quarry = g.getSectors().quarrying();
        BuildingManager bm = g.getBuildingManager();
        LandManager land = g.getLandManager();
        quietly(() -> bm.addStack(bm.getTemplate(Quarrying.QUARRY), 1, true));
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(1));
        double[] before = land.getExtractedByType(STONE);
        quietly(() -> g.simulateMonths(1));
        double[] after = land.getExtractedByType(STONE);
        int li = STONE.typesOf().indexOf(LIME), hi = STONE.typesOf().indexOf(HARD);
        Sector.Output lo = quarry.outputRow(Good.LIMESTONE), ao = quarry.outputRow(Good.AGGREGATE);
        double dug = lo.produced + quarry.carried();
        GoodsMarket aggM = g.getMarkets().get(Good.AGGREGATE), limeM = g.getMarkets().get(Good.LIMESTONE);
        double shipped = aggM.getExported(), price = aggM.exportPrice();
        report("fixture: a town on a limestone field, a Quarry standing and no kiln or furnace: a month on, every tonne it dug carried",
                land.getSites(STONE, LIME) > 0 && land.getSites(STONE, HARD) == 0 && dug > 0 && lo.produced == 0
                        && quarry.carried() == dug && ao.produced == dug,
                String.format("%,.0f t dug at a rate of %.4f, %,.0f carried", dug, quarry.getOperatingRate(), quarry.carried()));
        report("...shipped as aggregate - the market's month - and none as limestone",
                shipped == dug && limeM.getExported() == 0, String.format("%,.0f t at D$%.4fk", shipped, price));
        report("...the ground it drew limestone's: E by type rising on limestone by the tonnes dug, hard rock not at all; W exact",
                Math.abs(after[li] - before[li] - dug) < 1e-6 && after[hi] == before[hi] && LandCheck.wByType(land),
                String.format("%,.0f t of limestone ground", after[li] - before[li]));
        // ...and its statement, struck at the top of the next month (Game.startOfMonthUpdate()): aggregate abroad, no limestone.
        quietly(() -> g.simulateMonths(1));
        Sector.Split sold = quarry.statement().sold.get(Good.AGGREGATE), soldLime = quarry.statement().sold.get(Good.LIMESTONE);
        report("...its statement, struck at the next month's top: the tonnes sold abroad as aggregate at its export price, nothing as"
                        + " limestone",
                sold != null && Math.abs(sold.abroad - shipped * price) <= 1e-9 * sold.abroad && sold.atHome == 0
                        && (soldLime == null || soldLime.total() == 0),
                String.format("D$%,.1fk abroad", sold == null ? 0 : sold.abroad));

        // ...and with a buyer: a Lime Kiln standing beside it takes its limestone at home first.
        Game k = stoneTown("quarryingcheck-kiln", LIME, 0);
        Quarrying kq = k.getSectors().quarrying();
        CementConcrete kilns = k.getSectors().cementConcrete();
        BuildingManager kb = k.getBuildingManager();
        quietly(() -> {
            kb.addStack(kb.getTemplate(Quarrying.QUARRY), 1, true);
            kb.addStack(kb.getTemplate(CementConcrete.LIME_KILN), 1, true);
        });
        MaterialsCheck.handWorkers(k);
        quietly(() -> k.simulateMonths(2));
        Sector.Output klo = kq.outputRow(Good.LIMESTONE);
        double kDug = klo.produced + kq.carried();
        double bought = k.getMarkets().get(Good.LIMESTONE).getLocalFilled();
        double home = klo.produced, limePrice = k.getMarkets().get(Good.LIMESTONE).getLocalPrice();
        quietly(() -> k.simulateMonths(1));
        Sector.Split kSold = kq.statement().sold.get(Good.LIMESTONE);
        report("with a Lime Kiln standing, limestone clears first: the kiln's limestone sold at home, only the rest carried",
                kDug > 0 && home > 0 && kDug - home >= 0 && Math.abs(bought - home) < 1e-6 && kSold != null
                        && Math.abs(kSold.atHome - home * limePrice) <= 1e-9 * kSold.atHome && kSold.abroad == 0
                        && kilns.getCapacity(Good.LIME) == 18_000,
                String.format("%,.0f t dug: %,.0f sold to the kiln, %,.0f carried; its statement D$%,.1fk at home", kDug,
                        home, kDug - home, kSold == null ? 0 : kSold.atHome));
        MoneyAudit.Result audit = g.getLastMoneyAudit(), kAudit = k.getLastMoneyAudit();
        report("the two towns' months close the money audit (8)", audit != null && audit.relative() < 1e-4 && kAudit != null
                        && kAudit.relative() < 1e-4,
                audit == null || kAudit == null ? "none" : String.format("residuals %.3g and %.3g of what moved", audit.relative(),
                        kAudit.relative()));
    }

    /* ============================ 2. the sector on the end of the registry ============================ */

    static void sector() {
        out.println("\n--- 2. the sector on the end of the registry, stone priced, and stone's goods ---");
        String[] keys = Sectors.KEYS;
        int at = java.util.Arrays.asList(keys).indexOf(Sectors.QUARRYING);
        assertTrue("Sectors.KEYS ends with Quarrying and Cement & Concrete, after Coal Mining (Jerus's Q21: iron, coal, stone)",
                keys.length == 20 && at == 18 && keys[17].equals(Sectors.COAL_MINING) && keys[19].equals(Sectors.CEMENT_CONCRETE));
        Sectors s = new Sectors(new BuildingManager(), new Markets());
        Quarrying q = s.quarrying();
        assertTrue("...an extraction sector on stone, found by its saved name, making limestone, aggregate and blocks - aggregate its planning"
                        + " good; Mining's group",
                s.byKey(Sectors.QUARRYING) == q && q instanceof ExtractionSector && q.resource() == STONE
                        && q.isMaker(Good.LIMESTONE) && q.isMaker(Good.AGGREGATE) && q.isMaker(Good.DIMENSION_STONE)
                        && q.goodsMade().size() == 3 && q.planningGood() == Good.AGGREGATE && !q.isUser(Good.LIMESTONE)
                        && q.group() == BuildingType.MINING);
        assertTrue("Equity's companies are the keys and then the bank, the bank's index the keys' count; the stone pair before the bank",
                Equity.COMPANIES.length == keys.length + 1 && Equity.COMPANIES[at].equals(Sectors.QUARRYING)
                        && Equity.COMPANIES[at + 1].equals(Sectors.CEMENT_CONCRETE) && Equity.BANK == keys.length
                        && Equity.COMPANIES[Equity.BANK].equals("Bank"));
        assertTrue("...the households' share block a slot a company",
                HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD - HouseholdBalance.CELL_SLOTS_BEFORE_SHARES == Equity.COMPANIES.length);
        assertTrue("...the audit's pools the city, a pool a sector in the keys' order - quarrying's and cement & concrete's last - and the"
                        + " three after",
                MoneyAudit.POOL_NAMES.length == keys.length + 4 && MoneyAudit.POOL_NAMES[at + 1].equals("quarrying")
                        && MoneyAudit.POOL_NAMES[at + 2].equals("cement & concrete") && MoneyAudit.POOL_NAMES[keys.length + 1].equals("order book"));
        double[] sheets = new Bank().sheetYearToSave();
        assertTrue("...and the bank's year of sheets files a column of loans and of interim financing a sector", (int) sheets[2] == keys.length);
        assertTrue("Resource.STONE is priced as aggregate, its pricing good; a limestone field's stone is LIMESTONE and hard rock's"
                        + " AGGREGATE (goodOf()); 1/400 of aggregate's export price in the ground (the study's 8)",
                STONE.good() == Good.AGGREGATE && STONE.goodOf(LIME) == Good.LIMESTONE && STONE.goodOf(HARD) == Good.AGGREGATE
                        && STONE.goodOf(null) == Good.AGGREGATE && STONE.goodOf(Deposit.FieldType.THERMAL) == null
                        && STONE.inGroundShare() == 1.0 / 400);
        double[] km2 = new double[CityLand.AREAS];
        km2[CityLand.TOTAL] = .1;
        km2[CityLand.DRY] = .1;
        double[] none = new double[Resource.values().length], stoneIn = none.clone();
        stoneIn[STONE.ordinal()] = STONE.amountPerSite();
        double ground = .0007, level = 1;
        double with = LandMarket.price(km2, stoneIn, ground, level), without = LandMarket.price(km2, none, ground, level);
        report("...so an offer's stone is in its price: a site's 13.5 Mt at US$0.025 a tonne, about US$340k (to the US$5k rounding)",
                Math.abs((with - without) - STONE.amountPerSite() * Good.AGGREGATE.worldExportPrice() / 400) <= 5,
                String.format("US$%,.0fk", with - without));
        assertTrue("stone's goods are the study's 6.3: aggregate 40/10 (limestone's band), dimension stone 260/170, cement 110/45;"
                        + " freight three eighths of each wedge",
                Good.AGGREGATE.worldImportPrice() == .040 && Good.AGGREGATE.worldExportPrice() == .010
                        && Good.AGGREGATE.worldImportPrice() == Good.LIMESTONE.worldImportPrice()
                        && Good.AGGREGATE.worldExportPrice() == Good.LIMESTONE.worldExportPrice()
                        && Good.DIMENSION_STONE.worldImportPrice() == .260 && Good.DIMENSION_STONE.worldExportPrice() == .170
                        && Good.CEMENT.worldImportPrice() == .110 && Good.CEMENT.worldExportPrice() == .045
                        && Math.abs(Good.AGGREGATE.baseFreight() - .375 * .030) < 1e-12
                        && Math.abs(Good.DIMENSION_STONE.baseFreight() - .375 * .090) < 1e-12
                        && Math.abs(Good.CEMENT.baseFreight() - .375 * .065) < 1e-12);
        assertTrue("...cement stockable, on HELD's end; aggregate and blocks flow goods; aggregate and cement BULK and dry bulk by sea,"
                        + " the blocks GOODS by road and general cargo by sea",
                Good.CEMENT.stockable() && !Good.AGGREGATE.stockable() && !Good.DIMENSION_STONE.stockable()
                        && NationalAccounts.HELD[NationalAccounts.HELD.length - 1] == Good.CEMENT
                        && Good.AGGREGATE.traffic() == Traffic.BULK && Good.CEMENT.traffic() == Traffic.BULK
                        && Good.DIMENSION_STONE.traffic() == Traffic.GOODS && Good.AGGREGATE.cargo() == Ports.Cargo.DRY_BULK
                        && Good.CEMENT.cargo() == Ports.Cargo.DRY_BULK && Good.DIMENSION_STONE.cargo() == Ports.Cargo.GENERAL);
        assertTrue("LIMESTONE's makers are the quarries and its buyers the blast furnaces and the kilns and cement plants",
                s.heavyIndustry().isUser(Good.LIMESTONE) && s.cementConcrete().isUser(Good.LIMESTONE) && q.isMaker(Good.LIMESTONE)
                        && !s.coalMining().isMaker(Good.LIMESTONE));
    }

    /* ============================ 3. the three rows against the study ============================ */

    /** Quarrying's rows, by their permanent ids, in the study's 6.4 order. */
    static final int[] ROWS = { Quarrying.QUARRY, Quarrying.LARGE_QUARRY, Quarrying.DIMENSION_STONE_QUARRY };

    /** The study's 6.4: each one's cost, D$ thousands all in. */
    static final double[] STUDY_COSTS = { 15_000, 90_000, 6_000 };

    /** ...its posts. */
    static final int[] STUDY_POSTS = { 22, 125, 17 };

    /** ...its running cost a month, D$ thousands. */
    static final double[] STUDY_RUNS = { 163, 981, 104 };

    /** ...what it makes a month, t: stone, stone, blocks. */
    static final double[] STUDY_MAKES = { 40_000, 250_000, 1_000 };

    /** ...the sites it takes, and of which type (null for either; the study's 6.4). */
    static final int[] STUDY_SITES = { 1, 3, 1 };
    static final Deposit.FieldType[] STUDY_TYPES = { null, null, HARD };

    /** ...and the tonnes of rock a tonne of blocks takes (spec 2.4: "dimension stone 3"); stone is a tonne a tonne. */
    static final double BLOCKS_DIG = 3;

    /** The research's crushing, kWh a tonne of stone dug: 1.7, the middle of the study's 6.2 "1.6-1.8" [S18]. */
    static final double CRUSH_KWH = 1.7;

    /** The hours of a month the study reads its kW at: its 2.2, "3,700 kW for 6,000 t a month is 450 kWh a tonne" - 450 x 6,000 / 3,700, 730 to the hour. */
    static final double HOURS = 730;

    /** The study's rounding: its returns to a tenth of a point, "about 10%" to the point. */
    static final double STUDY_ROUNDING = .05, STUDY_ROUNDING_POINT = .5;

    /** The returns the study's 6.5 states, % of the cost a month: the Quarry's and the Large Quarry's exporting at D$10, and replacing imports at D$40. */
    static final double STUDY_EXPORTING_LOW = 1.6, STUDY_EXPORTING_HIGH = 1.7, STUDY_REPLACING = 10;

    static void rows() {
        out.println("\n--- 3. the three rows at parity: the study's 6.4, and its four cases worked from its own figures, to its rounding ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        UtilitiesHandler founding = new UtilitiesHandler();
        double tariff = founding.getPricerPerWatt(), water = founding.getPricePerWaterUnit();
        java.util.function.ToDoubleFunction<Good> imp = Good::worldImportPrice, exp = Good::worldExportPrice;
        String[] cases = { "A", "B", "C", "D" };
        assertTrue("the study's month is 730 hours: its 3,700 kW for 6,000 t is 450 kWh a tonne (its 2.2)",
                Math.round(450 * 6_000 / 3_700.0) == HOURS);
        for (int r = 0; r < ROWS.length; r++) {
            BuildingsTemplate t = b.getTemplate(ROWS[r]);
            boolean blocks = r == 2;
            double cost = t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials();
            BuildingsTemplate.Site site = t.site();
            double rock = blocks ? STUDY_MAKES[r] * BLOCKS_DIG : STUDY_MAKES[r];
            long kw = Math.round(CRUSH_KWH * rock / HOURS);
            boolean siteOk = site != null && site.resource() == STONE && site.type() == STUDY_TYPES[r] && site.sites() == STUDY_SITES[r]
                    && site.dig() == (blocks ? BLOCKS_DIG : 1)
                    && (blocks ? t.makes(Good.DIMENSION_STONE) == STUDY_MAKES[r] : t.makes(Good.AGGREGATE) == STUDY_MAKES[r])
                    && t.goodsMade().size() == 1 && t.goodsUsed().isEmpty();
            report("the " + t.getName() + " (" + t.getId() + ") is the study's: D$" + String.format("%,.0f", STUDY_COSTS[r]) + "k all in, "
                            + STUDY_POSTS[r] + " posts, " + kw + " kW (" + CRUSH_KWH + " kWh a tonne of rock dug), "
                            + String.format("%,.0f", STUDY_MAKES[r]) + (blocks ? " t of blocks on one hard-rock site" : " t of stone on "
                            + STUDY_SITES[r] + " site(s) of either type") + "; Quarrying's, MINING, dated 1900",
                    cost == STUDY_COSTS[r] && t.getTotalJobs() == STUDY_POSTS[r] && t.getElectricityConsumption() == kw && siteOk
                            && Sectors.QUARRYING.equals(t.getSector()) && t.getCategory() == BuildingType.MINING && t.getSince() == 1900
                            && t.getStock() == 0,
                    String.format("cash %,.0f + 18 x %,d material", t.getCashCost(), t.getConstructionMaterials()));
            double sqFt = STUDY_SITES[r] * STONE.siteKm2() * LandManager.SQ_FT_PER_KM2;
            report("...its ground the stone sites it takes, 0.1 km2 each, to the square foot", Math.abs(t.getLandSqFt() - sqFt) <= .5,
                    String.format("%,.0f sq ft", t.getLandSqFt()));
            double wages = 0;
            for (JobType j : JobType.values()) wages += t.getJobs(j) * PayTier.wageOf(j);
            double running = wages + t.getElectricityConsumption() * tariff + t.getWaterConsumption() * water;
            report("its consumables are the study's running cost less its wages, power and water (no diesel: the study names none)",
                    Math.abs(t.getConsumables() - (STUDY_RUNS[r] - running)) < 1e-9 && t.uses(Good.DIESEL) == 0,
                    String.format("%,.2f = %,.0f - %,.2f", t.getConsumables(), STUDY_RUNS[r], running));
            boolean all = true;
            StringBuilder got = new StringBuilder(), study = new StringBuilder();
            Good good = blocks ? Good.DIMENSION_STONE : Good.AGGREGATE;
            for (SpreadPlanner.Case c : SpreadPlanner.Case.values()) {
                java.util.function.ToDoubleFunction<Good> price = c.outputAtImport() ? imp : exp;
                double model = SpreadPlanner.caseEarns(t, c, imp, exp, running, 0, 1, tariff);
                double theirs = STUDY_MAKES[r] * price.applyAsDouble(good) - STUDY_RUNS[r];
                double pct = 100 * model / cost, want = 100 * theirs / STUDY_COSTS[r];
                all &= Math.abs(pct - want) <= STUDY_ROUNDING + 1e-9;
                got.append(String.format("%s %.3f%%  ", cases[c.ordinal()], pct));
                study.append(c.ordinal() == 0 ? "" : "/").append(String.format("%.3f", want));
            }
            report("...its four cases are the study's arithmetic to its rounding: " + study + "%", all, got.toString().trim());
        }
        // The returns the study states (its 6.5), each from its own figures.
        double small = 100 * (STUDY_MAKES[0] * Good.AGGREGATE.worldExportPrice() - STUDY_RUNS[0]) / STUDY_COSTS[0];
        double large = 100 * (STUDY_MAKES[1] * Good.AGGREGATE.worldExportPrice() - STUDY_RUNS[1]) / STUDY_COSTS[1];
        report("the study's 6.5: quarries return 1.6-1.7% a month exporting at D$10 - the Quarry 1.6, the Large Quarry 1.7",
                Math.abs(small - STUDY_EXPORTING_LOW) <= STUDY_ROUNDING && Math.abs(large - STUDY_EXPORTING_HIGH) <= STUDY_ROUNDING,
                String.format("%.3f%%, %.3f%%", small, large));
        double smallIn = 100 * (STUDY_MAKES[0] * Good.AGGREGATE.worldImportPrice() - STUDY_RUNS[0]) / STUDY_COSTS[0];
        double largeIn = 100 * (STUDY_MAKES[1] * Good.AGGREGATE.worldImportPrice() - STUDY_RUNS[1]) / STUDY_COSTS[1];
        report("...and about 10% replacing imports at D$40 (\"D$4 a tonne to run\": 163k for 40,000 t)",
                Math.abs(smallIn - STUDY_REPLACING) <= STUDY_ROUNDING_POINT && Math.abs(largeIn - STUDY_REPLACING) <= STUDY_ROUNDING_POINT
                        && Math.round(STUDY_RUNS[0] / STUDY_MAKES[0] * 1000) == 4,
                String.format("%.2f%%, %.2f%%", smallIn, largeIn));
    }

    /* ============================ 4. hard rock makes aggregate only ============================ */

    static void hardRock() {
        out.println("\n--- 4. a hard-rock quarry makes aggregate only; the Dimension Stone Quarry only on hard rock ---");
        Game g = stoneTown("quarryingcheck-hard", HARD, 0);
        Quarrying q = g.getSectors().quarrying();
        BuildingManager b = g.getBuildingManager();
        LandManager land = g.getLandManager();
        BuildingsTemplate quarry = b.getTemplate(Quarrying.QUARRY), blocks = b.getTemplate(Quarrying.DIMENSION_STONE_QUARRY);
        int hardSites = land.getSites(STONE, HARD);
        report("fixture: a town founded on a hard-rock field, no limestone", hardSites > 0 && land.getSites(STONE, LIME) == 0,
                String.format("%d hard-rock site(s), %,.0f Mt; tried%s", hardSites, land.getRemaining(STONE, HARD) / 1e6,
                        lastSearch.length() > 200 ? lastSearch.substring(0, 200) : lastSearch));
        assertTrue("the Dimension Stone Quarry fits a hard-rock site, as many as there are; the Quarry fits too",
                q.fits(blocks) && g.hasDepositFor(blocks, hardSites) && !g.hasDepositFor(blocks, hardSites + 1) && q.fits(quarry));
        quietly(() -> b.addStack(quarry, 1, true));
        report("a Quarry standing on hard rock makes aggregate only: 40,000 t, no limestone",
                q.getCapacity(Good.AGGREGATE) == 40_000 && q.getCapacity(Good.LIMESTONE) == 0
                        && q.madeBy(quarry).equals(hardSites > 1 ? Map.of(Good.AGGREGATE, 40_000.0) : Map.of()),
                String.format("%,.0f t aggregate, %,.0f t limestone", q.getCapacity(Good.AGGREGATE), q.getCapacity(Good.LIMESTONE)));
        Game l = stoneTown("quarryingcheck-lime", LIME, 0);
        Quarrying lq = l.getSectors().quarrying();
        BuildingsTemplate lBlocks = l.getBuildingManager().getTemplate(Quarrying.DIMENSION_STONE_QUARRY);
        assertTrue("...and on a town of limestone alone the Dimension Stone Quarry does not fit, whatever its free sites",
                l.getLandManager().getSites(STONE, LIME) > 0 && !lq.fits(lBlocks) && !l.hasDepositFor(lBlocks, 1)
                        && lq.fits(l.getBuildingManager().getTemplate(Quarrying.QUARRY)));
    }

    /* ============================ 5. the packing ============================ */

    static void packing() {
        out.println("\n--- 5. the packing: quarries on ANY onto limestone first, a typed works before them, the larger first ---");
        Game g = stoneTown("quarryingcheck-pack", HARD, 3);
        Quarrying q = g.getSectors().quarrying();
        BuildingManager b = g.getBuildingManager();
        LandManager land = g.getLandManager();
        int hardSites = land.getSites(STONE, HARD);
        report("fixture: a town on a hard-rock field, three limestone sites by fiat (the first type)",
                land.getSites(STONE, LIME) == 3 && hardSites >= 4,
                String.format("%d limestone, %d hard rock", land.getSites(STONE, LIME), hardSites));
        List<LandManager.Claim> claims = List.of(new LandManager.Claim(null, 1, 2), new LandManager.Claim(null, 3, 1),
                new LandManager.Claim(HARD, 1, 1));
        int[][] packed = land.packed(STONE, claims);
        int li = STONE.typesOf().indexOf(LIME), hi = STONE.typesOf().indexOf(HARD);
        report("LandManager.packed(): two Quarries, a Large Quarry and a Dimension Stone Quarry - the blocks on hard rock first, the"
                        + " Large Quarry's three on limestone, the Quarries past it on hard rock",
                packed[2][hi] == 1 && packed[2][li] == 0 && packed[1][li] == 3 && packed[1][hi] == 0 && packed[0][li] == 0 && packed[0][hi] == 2,
                java.util.Arrays.deepToString(packed));
        quietly(() -> {
            b.addStack(b.getTemplate(Quarrying.QUARRY), 2, true);
            b.addStack(b.getTemplate(Quarrying.DIMENSION_STONE_QUARRY), 1, true);
        });
        report("two Quarries standing: both on limestone - 80,000 t of limestone, the blocks 1,000 t, no aggregate",
                q.getCapacity(Good.LIMESTONE) == 80_000 && q.getCapacity(Good.AGGREGATE) == 0 && q.getCapacity(Good.DIMENSION_STONE) == 1_000,
                String.format("%,.0f / %,.0f / %,.0f", q.getCapacity(Good.LIMESTONE), q.getCapacity(Good.AGGREGATE), q.getCapacity(Good.DIMENSION_STONE)));
        Map<Good, Double> third = q.madeBy(b.getTemplate(Quarrying.QUARRY));
        Map<Good, Double> large = q.madeBy(b.getTemplate(Quarrying.LARGE_QUARRY));
        report("...a third would take the last limestone site; a Large Quarry would take the limestone's three, the two standing"
                        + " repacked onto hard rock (the packing is pure, nothing recorded on a field): its increment 250,000 t all the same",
                third.equals(Map.of(Good.LIMESTONE, 40_000.0)) && large.get(Good.LIMESTONE) == 170_000 && large.get(Good.AGGREGATE) == 80_000,
                "a third " + third + "; a large " + large);
        quietly(() -> b.addStack(b.getTemplate(Quarrying.QUARRY), 1, false));
        report("...a third on site is the pipeline: 40,000 t of limestone coming, nothing standing moved",
                q.getPipeline(Good.LIMESTONE) == 40_000 && q.getPipeline(Good.AGGREGATE) == 0 && q.getCapacity(Good.LIMESTONE) == 80_000,
                String.format("%,.0f t coming", q.getPipeline(Good.LIMESTONE)));
        Map<Good, Double> fourth = q.madeBy(b.getTemplate(Quarrying.QUARRY));
        assertTrue("...and a fourth, past the limestone, goes onto hard rock: aggregate", fourth.equals(Map.of(Good.AGGREGATE, 40_000.0)));
        BuildingsTemplate largeT = b.getTemplate(Quarrying.LARGE_QUARRY);
        assertTrue("the order gate agrees with the packing: room for a Large Quarry on the hard rock beside the standing (fits(), Game"
                        + ".hasDepositFor()), and not for more than the free sites hold",
                q.fits(largeT) == land.fits(STONE, null, g.claimsOn(STONE), 3) && g.hasDepositFor(b.getTemplate(Quarrying.QUARRY), 1)
                        && !g.hasDepositFor(b.getTemplate(Quarrying.QUARRY), land.getSites(STONE) - g.committedOn(STONE) + 1));
    }

    /* ============================ 6. the dig by type ============================ */

    static void dig() {
        out.println("\n--- 6. the dig by type: each good out of its type's ground at its dig, W exact; a type worked out ---");
        Game g = stoneTown("quarryingcheck-dig", HARD, 3);
        Quarrying q = g.getSectors().quarrying();
        BuildingManager b = g.getBuildingManager();
        LandManager land = g.getLandManager();
        quietly(() -> {
            b.addStack(b.getTemplate(Quarrying.QUARRY), 1, true);
            b.addStack(b.getTemplate(Quarrying.LARGE_QUARRY), 1, true);
            b.addStack(b.getTemplate(Quarrying.DIMENSION_STONE_QUARRY), 1, true);
        });
        double rate = q.getOperatingRate();
        double[] before = land.getExtractedByType(STONE);
        double eBefore = land.getExtracted(STONE);
        for (Good gd : new Good[] { Good.AGGREGATE, Good.LIMESTONE, Good.DIMENSION_STONE }) q.produceFlow(gd);
        double[] after = land.getExtractedByType(STONE);
        int li = STONE.typesOf().indexOf(LIME), hi = STONE.typesOf().indexOf(HARD);
        double lime = q.outputRow(Good.LIMESTONE).produced, agg = q.outputRow(Good.AGGREGATE).produced,
                blocks = q.outputRow(Good.DIMENSION_STONE).produced;
        report("fixture: a Quarry, a Large Quarry and a Dimension Stone Quarry on three limestone sites and hard rock: the Large"
                        + " Quarry's limestone, the Quarry's aggregate and the blocks, each its nameplate at the sector's rate",
                rate > 0 && lime == 250_000 * rate && agg == 40_000 * rate && blocks == 1_000 * rate,
                String.format("rate %.4f: %,.0f limestone, %,.0f aggregate, %,.0f blocks", rate, lime, agg, blocks));
        report("...out of their types' ground: limestone the limestone, aggregate and three tonnes of rock a tonne of blocks the hard"
                        + " rock; E by them, W by type exact",
                Math.abs(after[li] - before[li] - lime) < 1e-6 && Math.abs(after[hi] - before[hi] - agg - BLOCKS_DIG * blocks) < 1e-6
                        && Math.abs(land.getExtracted(STONE) - eBefore - lime - agg - BLOCKS_DIG * blocks) < 1e-6 && LandCheck.wByType(land),
                String.format("limestone %,.0f t, hard rock %,.0f t of ground", after[li] - before[li], after[hi] - before[hi]));
        // The limestone worked out: the quarries on ANY dig the hard rock that is left.
        land.extract(STONE, LIME, land.getRemaining(STONE, LIME));
        report("with the limestone worked out, the quarries on ANY dig hard rock: 290,000 t of aggregate, no limestone",
                q.getCapacity(Good.LIMESTONE) == 0 && q.getCapacity(Good.AGGREGATE) == 290_000 && q.retirementDemandAndCapacity(g) == null
                        && LandCheck.wByType(land),
                String.format("%,.0f t aggregate", q.getCapacity(Good.AGGREGATE)));
        // ...and the hard rock nearly out: aggregate first (its makes' order), the blocks held to what is left.
        double left = 30_000;
        land.extract(STONE, HARD, land.getRemaining(STONE, HARD) - left);
        for (Good gd : new Good[] { Good.AGGREGATE, Good.LIMESTONE, Good.DIMENSION_STONE }) q.produceFlow(gd);
        report("with 30,000 t of hard rock left, the quarries take it first and the blocks get what they leave; W exact",
                Math.abs(q.outputRow(Good.AGGREGATE).produced - left) < 1e-6 && q.outputRow(Good.DIMENSION_STONE).produced == 0
                        && q.outputRow(Good.LIMESTONE).produced == 0 && !(land.getRemaining(STONE) > 0) && LandCheck.wByType(land),
                String.format("%,.0f t aggregate", q.outputRow(Good.AGGREGATE).produced));
        double[] spare = q.retirementDemandAndCapacity(g);
        assertTrue("...and with stone worked out every quarry is spare, measured in the tonnes it digs (unitsOf())",
                spare != null && spare[0] == 0 && spare[1] == 291_000 && q.unitsOf(b.getTemplate(Quarrying.DIMENSION_STONE_QUARRY)) == 1_000);
    }

    /* ============================ 7. the plan ============================ */

    static void plan() {
        out.println("\n--- 7. the plan: the increment to the packing, limestone past the city's room valued as aggregate ---");
        Game g = stoneTown("quarryingcheck-plan", LIME, 0);
        Quarrying q = g.getSectors().quarrying();
        BuildingManager b = g.getBuildingManager();
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(1));
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate quarry = b.getTemplate(Quarrying.QUARRY);
        Map<Good, Double> adds = q.madeBy(quarry);
        GoodsMarket lime = g.getMarkets().get(Good.LIMESTONE);
        double room = Math.max(0, plans.forecast(q, lime) - q.getCapacity(Good.LIMESTONE) - q.getPipeline(Good.LIMESTONE));
        double home = Math.min(40_000, room);
        java.util.Map<Good, Double> valued = new java.util.EnumMap<>(Good.class);
        valued.put(Good.LIMESTONE, home);
        if (40_000 > home) valued.put(Good.AGGREGATE, 40_000 - home);
        double rate = BusinessInvestment.operatingRateOf(q.getOperatingRate());
        double expect = plans.estimatedMakerProfit(q, quarry, valued)
                - SpreadPlanner.ownCosts(quarry, rate, q.exchangeFactor(), g.getEconomyManager().getPricePerWatt()) - q.fleetRunning(quarry, rate);
        double earns = q.estimatedMonthlyProfit(quarry, plans);
        report("one more Quarry on a limestone town adds 40,000 t of limestone; its estimate the city's room for limestone at the"
                        + " makers' rule, the rest as aggregate, less its consumables and its lorries, to the bit",
                adds.equals(Map.of(Good.LIMESTONE, 40_000.0)) && earns == expect,
                String.format("room %,.0f t; D$%,.1fk a month on D$%,.0fk", room, earns, plans.getCostOf(quarry, 1) + q.fleetCapital(quarry)));
        BusinessInvestment.Decision d = q.plan(plans, g);
        BuildingsTemplate expected = null;
        double best = 0;
        for (BuildingsTemplate t : b.getTemplatesBySector(Sectors.QUARRYING)) {
            if (!q.fits(t) || !q.staffing(t).passes()) continue;
            double e = q.estimatedMonthlyProfit(t, plans), cost = plans.getCostOf(t, 1) + q.fleetCapital(t);
            if (!q.planner().passesMoney(q, plans, g, e, cost)) continue;
            if (e / cost > best) { best = e / cost; expected = t; }
        }
        report("the plan: the best earnings on the whole cost of the quarries that fit, staff and pay - or none, and why",
                expected == null ? !d.build && d.reason != null && !d.reason.isEmpty() : d.build && d.template == expected,
                d.reason + (expected == null ? "" : String.format("; %s at %.3f%% a month", expected.getName(), 100 * best)));
        BuildingsTemplate blocks = b.getTemplate(Quarrying.DIMENSION_STONE_QUARRY);
        assertTrue("...the Dimension Stone Quarry weighed only where it fits: never on a limestone town", !q.fits(blocks));
    }

    /* ============================ the fixture ============================ */

    /**
     * A town to quarry in: MaterialsCheck's and CoalMiningCheck's (the
     * Wealthy preset's cash and ground, homes, shops, power and water), its
     * land founded instead on the stone field of `type` nearest the default
     * founding site on dry ground - so it holds that field whole - and
     * `limestoneSites` sites by fiat, ground listed by fiat being the first
     * type, limestone.
     */
    static Game stoneTown(String label, Deposit.FieldType type, int limestoneSites) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> {
            g.newGame();
            World w = World.of(g.getCityLand().seed());
            LandManager land = g.getLandManager();
            // ...on the founding default's rings, small enough to hold the field it is centred on and little else (CityLand.found()).
            double km2 = FOUND_KM2;
            // ...the nearest field of the type whose founded ground holds none of the other type (stone's fields cluster).
            Deposit.FieldType other = type == LIME ? HARD : LIME;
            StringBuilder tried = new StringBuilder();
            for (Deposit field : nearest(w, type)) {
                land.install(CityLand.found(w, field.x(), field.y(), km2), new double[CityLand.KINDS], w.totals(), w.seaTheta());
                tried.append(String.format(" %d/%d/%d", field.sites(), land.getSites(STONE, type), land.getSites(STONE, other)));
                if (land.getSites(STONE, type) > 0 && land.getSites(STONE, other) == 0) break;
            }
            lastSearch = tried.toString();
            // ...its ground figure room for the town, the land as it stands (not restated: a restatement founds it again,
            // bigger, and stone's fields cluster), and `limestoneSites` by fiat on the field.
            land.restoreOwnedSqFt(land.getLandDrySqFt() + 400_000_000L);
            g.setCashForTest(Founding.WEALTHY_CASH);
            if (limestoneSites > 0) {
                land.restoreSites(STONE, land.getSites(STONE) + limestoneSites,
                        land.getOwnedAmount(STONE) + limestoneSites * STONE.amountPerSite());
            }
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("House"), 400, true);
            g.buildStack(b.getTemplateByName("Small Grocery Store"), 4, true);
            g.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Paved Road"), 10, true);
        });
        return g;
    }

    /** The stone fields of a type whose centre plot is dry ground, nearest a world's founding site first, cell ring by cell ring to MAX_RING; hard rock's of at least four sites, so the packing has room. */
    static List<Deposit> nearest(World w, Deposit.FieldType type) {
        long fx = w.foundingX(), fy = w.foundingY();
        int cx = (int) (fx / World.CELL), cy = (int) (fy / World.CELL);
        List<Deposit> out = new java.util.ArrayList<>();
        for (int ring = 0; ring <= MAX_RING; ring++) {
            List<Deposit> found = new java.util.ArrayList<>();
            for (int y = cy - ring; y <= cy + ring; y++) {
                for (int x = cx - ring; x <= cx + ring; x++) {
                    if (Math.max(Math.abs(x - cx), Math.abs(y - cy)) != ring) continue;
                    if (x < 0 || y < 0 || x >= World.CELLS || y >= World.CELLS) continue;
                    for (Deposit d : w.fieldsInCell(y * World.CELLS + x, STONE)) {
                        if (d.type() != type || !World.dry(w.terrainAt(d.x(), d.y()))) continue;
                        if (type == HARD && d.sites() < 4) continue;
                        found.add(d);
                    }
                }
            }
            found.sort(java.util.Comparator.comparingDouble(d -> Math.hypot(d.x() - fx, d.y() - fy)));
            out.addAll(found);
        }
        if (out.isEmpty()) throw new IllegalStateException("no " + type + " field on dry ground on world " + w);
        return out;
    }

    /** The last fixture's search, for its report: each field tried, its sites, the town's of the type and of the other. */
    static String lastSearch = "";

    /** The dry ground a fixture town is founded on round its field, km2: the default city's rings (CityLand.found()). */
    static final double FOUND_KM2 = .3;

    /** How many cells round the founding site's the fixture looks for its field in. */
    static final int MAX_RING = 6;
}
