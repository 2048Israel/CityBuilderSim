package ham.citybuildersim;

import ham.citybuildersim.sectors.CopperMining;
import ham.citybuildersim.sectors.ExtractionSector;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 * Copper Mining, checked (0.7.133, batch I8; runs/spec-materials.md 2.1, 2.3,
 * 2.4, 4 and 5's I8 row; the study's - materials-chains-research.md - 1.3,
 * 1.4, 4 and 8): the fourth materials chain's digging industry, on the end
 * of Sectors.KEYS after Cement & Concrete.
 *
 * WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md; I6's
 * CoalMiningCheck, I7's QuarryingCheck), and copper digs its two types into
 * two different goods: a sulphide field's ore floated into concentrate, a
 * flow good dug as the month makes it, and an oxide field's leached into
 * cathode, a stockable good dug at the month's end. CopperRefiningCheck
 * keeps what the processing side adds.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE SECTOR ON THE REGISTRY: copper's two keys on the end of
 *      Sectors.KEYS after Cement & Concrete (Jerus's Q21: iron, coal, stone,
 *      copper, wood), Copper Mining first; an ExtractionSector on copper
 *      making concentrate and cathode and using acid; and what is as long as
 *      the keys grew with them. Resource.COPPER priced as cathode, a sulphide
 *      field's good concentrate and an oxide field's cathode, 1/100 of
 *      cathode's export price in the ground; copper's seven goods at the
 *      study's 4.2 prices, freight three eighths of each wedge, cathode the
 *      one stockable good, on NationalAccounts.HELD's end.
 *
 *   2. THE THREE ROWS ARE THE STUDY'S (ids 121 to 123, at parity): cost,
 *      posts, draw, what each makes, its site (type, sites and dig), ground,
 *      road load, stock and consumables; and their four cases, A to D, the
 *      study's 4.4 table to its rounding. The study's 4.3 gives no running
 *      cost, so each row's is backed out of its own table - the middle of the
 *      range on which all four returns round to it (STUDY_RUNS) - and the
 *      consumables are that less wages, power and water.
 *
 *   3. THE TYPES AND THE FIT: copper's fields are sulphide 80% and oxide 20%
 *      (I1's draw, within two points on the default world); a Copper Mine &
 *      Mill stands on one sulphide site, the Large Copper Mine on eight of
 *      one sulphide field, the leach plant only on oxide (fits()).
 *
 *   4. A SULPHIDE MINE MAKES CONCENTRATE, DIGGING ITS OWN TYPE: the month's
 *      concentrate its nameplate at the rate, the sulphide ground giving up
 *      CONC_DIG a tonne as it is made, the oxide untouched, W exact by type.
 *
 *   5. AN OXIDE FIELD'S LEACH PLANT MAKES CATHODE: the month's cathode is the
 *      lift - its nameplate at the rate, held to the oxide ground - what the
 *      market is priced on and the month makes; at the month's end the oxide
 *      gives up LEACH_DIG a tonne, W exact; it bids for its acid; a type
 *      running short holds it to what is left, and worked out it is spare.
 *
 *   6. THE PLAN: ExtractionSector's - the best earnings on the whole cost of
 *      the buildings that fit, their lorries in it.
 *
 *   7. A PLAYED MONTH: a mine and mill and a leach plant standing, a month
 *      on - concentrate shipped, cathode made, acid bought, the ground given
 *      up by type - and the audit closes.
 *
 * Every fixture causes its condition: a town founded on the oxide field
 * nearest the default site (oxide the rarer type), its sulphide ground by
 * fiat (the first type), the buildings placed by hand.
 */
public class CopperMiningCheck {

    static int fails = 0;
    /** Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. */
    static final PrintStream out = System.out;
    /** A stream that drops what is written to it: the game's prose while a fixture town is founded and played. */
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
        sector();
        rows();
        typesAndFit();
        sulphide();
        oxide();
        plan();
        playedMonth();
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** Copper, the resource its mines and leach plants dig. */
    static final Resource CU = Resource.COPPER;
    /** Copper's two types of field (Resource.typesOf()): sulphide, floated at the mine, and oxide, leached. */
    static final Deposit.FieldType SULPHIDE = Deposit.FieldType.SULPHIDE, OXIDE = Deposit.FieldType.OXIDE;

    /* ============================ 1. the sector on the registry ============================ */

    /** Copper's goods, the study's 4.2: import and export price, D$ thousands a tonne. */
    static final Good[] GOODS = { Good.COPPER_CONC, Good.ANODE, Good.COPPER, Good.COPPER_SCRAP, Good.COPPER_ROD, Good.ACID, Good.WIRE_CABLE };
    /** ...each one's import price, D$ thousands a tonne (the study's 4.2)... */
    static final double[] STUDY_IMPORT = { 2.800, 10.150, 10.500, 8.700, 10.750, .160, 17.000 };
    /** ...and its export price. */
    static final double[] STUDY_EXPORT = { 2.575, 9.650, 10.000, 7.300, 10.250, .070, 13.000 };

    /** Good.java's rule: freight three eighths of the wedge. */
    static final double FREIGHT_SHARE = .375;

    static void sector() {
        out.println("--- 1. the sector on the registry: copper's two keys after stone's, a mine on copper, its goods priced ---");
        String[] keys = Sectors.KEYS;
        int at = java.util.Arrays.asList(keys).indexOf(Sectors.COPPER_MINING);
        assertTrue("Sectors.KEYS has Copper Mining after Cement & Concrete, the twenty-first, and Copper Refining after it, the last"
                        + " (Jerus's Q21: iron, coal, stone, copper, wood)",
                at == 20 && keys[at - 1].equals(Sectors.CEMENT_CONCRETE) && keys[at + 1].equals(Sectors.COPPER_REFINING)
                        && keys.length == at + 2);
        Sectors s = new Sectors(new BuildingManager(), new Markets());
        CopperMining cm = s.copperMining();
        assertTrue("...an extraction sector on copper, found by its saved name, making concentrate and cathode and using acid (and its"
                        + " vans, as every sector does); Mining's group; cathode its planning good",
                s.byKey(Sectors.COPPER_MINING) == cm && cm instanceof ExtractionSector && cm.resource() == CU
                        && java.util.Set.copyOf(cm.goodsMade()).equals(java.util.Set.of(Good.COPPER_CONC, Good.COPPER))
                        && java.util.Set.copyOf(cm.goodsUsed()).equals(java.util.Set.of(Good.VANS, Good.ACID)) && cm.group() == BuildingType.MINING
                        && cm.planningGood() == Good.COPPER);
        assertTrue("Equity's companies are the keys and then the bank; the households' share block a slot a company; the audit's pools a"
                        + " pool a sector in the keys' order; the bank's year of sheets a column a sector",
                Equity.COMPANIES.length == keys.length + 1 && Equity.COMPANIES[at].equals(Sectors.COPPER_MINING)
                        && Equity.COMPANIES[Equity.BANK].equals("Bank")
                        && HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD - HouseholdBalance.CELL_SLOTS_BEFORE_SHARES == Equity.COMPANIES.length
                        && MoneyAudit.POOL_NAMES.length == keys.length + 4 && MoneyAudit.POOL_NAMES[at + 1].equals("copper mining")
                        && MoneyAudit.POOL_NAMES[at + 2].equals("copper refining")
                        && (int) new Bank().sheetYearToSave()[2] == keys.length);
        assertTrue("Resource.COPPER is priced as cathode: a sulphide field's good concentrate, an oxide field's cathode, ANY's cathode;"
                        + " 1/100 of cathode's export price in the ground (the study's 8)",
                CU.good() == Good.COPPER && CU.goodOf(SULPHIDE) == Good.COPPER_CONC && CU.goodOf(OXIDE) == Good.COPPER
                        && CU.goodOf(null) == Good.COPPER && CU.goodOf(Deposit.FieldType.HEMATITE) == null && CU.inGroundShare() == 1.0 / 100);
        double[] km2 = new double[CityLand.AREAS];
        km2[CityLand.TOTAL] = .1;
        km2[CityLand.DRY] = .1;
        double[] none = new double[Resource.values().length], cuIn = none.clone();
        cuIn[CU.ordinal()] = CU.amountPerSite();
        double ground = .0007, level = 1;
        double with = LandMarket.price(km2, cuIn, ground, level), without = LandMarket.price(km2, none, ground, level);
        report("...so an offer's copper is in its price: a site's 300,000 t of metal at US$100 a tonne, US$30M (to the US$5k rounding)",
                Math.abs((with - without) - CU.amountPerSite() * Good.COPPER.worldExportPrice() * CU.inGroundShare()) <= 5,
                String.format("US$%,.0fk", with - without));
        boolean prices = true;
        StringBuilder got = new StringBuilder();
        for (int i = 0; i < GOODS.length; i++) {
            Good g = GOODS[i];
            prices &= g.worldImportPrice() == STUDY_IMPORT[i] && g.worldExportPrice() == STUDY_EXPORT[i]
                    && Math.abs(g.baseFreight() - FREIGHT_SHARE * (STUDY_IMPORT[i] - STUDY_EXPORT[i])) < 1e-12 && "tonne".equals(g.unit())
                    && g.traded() && g.stockable() == (g == Good.COPPER);
            got.append(String.format("%s %.3f/%.3f; ", g.name(), g.worldImportPrice(), g.worldExportPrice()));
        }
        report("copper's seven goods at the study's 4.2 prices - scrap's import end and wire's export end widened (its 1.2) - freight"
                + " three eighths of each wedge, by the tonne; cathode the one stockable good", prices, got.toString());
        assertTrue("...wire and cable and rod by the GOODS stream, the other five BULK (spec 2.3); by sea acid a tanker's, concentrate and"
                        + " scrap dry bulk, anode and cathode general cargo, rod and wire in containers",
                Good.WIRE_CABLE.traffic() == Traffic.GOODS && Good.COPPER_ROD.traffic() == Traffic.GOODS
                        && Good.COPPER_CONC.traffic() == Traffic.BULK && Good.ANODE.traffic() == Traffic.BULK
                        && Good.COPPER.traffic() == Traffic.BULK && Good.COPPER_SCRAP.traffic() == Traffic.BULK
                        && Good.ACID.traffic() == Traffic.BULK && Good.ACID.cargo() == Ports.Cargo.LIQUID
                        && Good.COPPER_CONC.cargo() == Ports.Cargo.DRY_BULK && Good.COPPER_SCRAP.cargo() == Ports.Cargo.DRY_BULK
                        && Good.ANODE.cargo() == Ports.Cargo.GENERAL && Good.COPPER.cargo() == Ports.Cargo.GENERAL
                        && Good.COPPER_ROD.cargo() == Ports.Cargo.CONTAINER && Good.WIRE_CABLE.cargo() == Ports.Cargo.CONTAINER);
        Good[] held = NationalAccounts.HELD;
        assertTrue("...and cathode joins NationalAccounts.HELD on its end, after cement", held[held.length - 1] == Good.COPPER
                && held[held.length - 2] == Good.CEMENT);
    }

    /* ============================ 2. the three rows against the study ============================ */

    /** Copper Mining's rows, by their permanent ids, in the study's 4.3 order. */
    static final int[] ROWS = { CopperMining.MINE_AND_MILL, CopperMining.LARGE_MINE, CopperMining.LEACH_PLANT };

    /** The study's 4.3: each one's cost, D$ thousands all in. */
    static final double[] STUDY_COSTS = { 160_000, 2_000_000, 250_000 };

    /** ...its posts. */
    static final int[] STUDY_POSTS = { 120, 450, 150 };

    /** ...its draw, kW. */
    static final int[] STUDY_KW = { 5_500, 60_000, 6_000 };

    /** ...what it makes a month, t: concentrate, concentrate, cathode. */
    static final double[] STUDY_MAKES = { 2_200, 26_400, 1_000 };

    /** ...the acid the leach plant takes, t a month. */
    static final double STUDY_ACID = 3_500;

    /** ...the sites each stands on, and of which type (its 4.3 and 1.3: "an oxide copper field takes a leach plant"). */
    static final int[] STUDY_SITES = { 1, 8, 1 };
    /** ...and the type of field each stands on. */
    static final Deposit.FieldType[] STUDY_TYPES = { SULPHIDE, SULPHIDE, OXIDE };

    /** The study's 4.4 table, % of the cost a month, cases A to D. */
    static final double[][] STUDY_TABLE = { { 2.6, 2.6, 2.9, 2.9 }, { 2.8, 2.8, 3.1, 3.1 }, { 3.1, 3.2, 3.4, 3.3 } };

    /** Each row's running cost a month, D$ thousands, backed out of its 4.4 table (its 4.3 gives none): the middle of the range on which all four of its returns round to the table. */
    static final double[] STUDY_RUNS = { 1_512.5, 11_950, 1_722.5 };

    /** The year each first existed: 1900 - standing before the start - but the Large Copper Mine, the open-pit porphyry mine at Bingham Canyon in 1906 (research-1900.md 1.2, PE8), and the leach plant, SX-EW at Bluebird in 1968 (E12). */
    static final int[] SINCE = { 1900, 1906, 1968 };

    /** The study's rounding: its returns to a tenth of a point. */
    static final double STUDY_ROUNDING = .05;

    static void rows() {
        out.println("\n--- 2. the three rows at parity: the study's 4.3, and its 4.4 table's four cases to its rounding ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        UtilitiesHandler founding = new UtilitiesHandler();
        double tariff = founding.getPricerPerWatt(), water = founding.getPricePerWaterUnit();
        java.util.function.ToDoubleFunction<Good> imp = Good::worldImportPrice, exp = Good::worldExportPrice;
        BuildingsTemplate mill = b.getTemplate(ham.citybuildersim.sectors.Mining.IRON_MINE);
        double perTonne = mill.getRoadLoad() / mill.makes(Good.IRON);
        String[] cases = { "A", "B", "C", "D" };
        for (int r = 0; r < ROWS.length; r++) {
            BuildingsTemplate t = b.getTemplate(ROWS[r]);
            boolean leach = r == 2;
            Good made = leach ? Good.COPPER : Good.COPPER_CONC;
            double cost = t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials();
            BuildingsTemplate.Site site = t.site();
            boolean siteOk = site != null && site.resource() == CU && site.type() == STUDY_TYPES[r] && site.sites() == STUDY_SITES[r]
                    && site.dig() == (leach ? CopperMining.LEACH_DIG : CopperMining.CONC_DIG);
            report("the " + t.getName() + " (" + t.getId() + ") is the study's: D$" + String.format("%,.0f", STUDY_COSTS[r]) + "k all in, "
                            + STUDY_POSTS[r] + " posts, " + String.format("%,d", STUDY_KW[r]) + " kW, " + String.format("%,.0f", STUDY_MAKES[r])
                            + " t of " + made.label().toLowerCase() + " a month on " + STUDY_SITES[r] + " " + STUDY_TYPES[r] + " site(s), "
                            + (leach ? "of 3,500 t of acid; " : "") + "Copper Mining's, MINING, dated " + SINCE[r],
                    cost == STUDY_COSTS[r] && t.getTotalJobs() == STUDY_POSTS[r] && t.getElectricityConsumption() == STUDY_KW[r] && siteOk
                            && t.makes(made) == STUDY_MAKES[r] && t.goodsMade().size() == 1
                            && (leach ? t.uses(Good.ACID) == STUDY_ACID && t.goodsUsed().size() == 1 : t.goodsUsed().isEmpty())
                            && Sectors.COPPER_MINING.equals(t.getSector()) && t.getCategory() == BuildingType.MINING
                            && t.getSince() == SINCE[r] && t.getGen() == 0 && t.oldMetalTaken().isEmpty(),
                    String.format("cash %,.0f + 18 x %,d material", t.getCashCost(), t.getConstructionMaterials()));
            double sqFt = STUDY_SITES[r] * CU.siteKm2() * LandManager.SQ_FT_PER_KM2;
            double tonnes = STUDY_MAKES[r] + (leach ? STUDY_ACID : 0);
            report("...its ground the copper sites it takes, 0.5 km2 each, its road load its tonnes out" + (leach ? " and its acid in" : "")
                            + " at the Iron Mine's 420 for 2,500 t, and " + (leach ? "three months of cathode in its yard" : "no yard: concentrate is a flow good"),
                    Math.abs(t.getLandSqFt() - sqFt) <= .5 && t.getRoadLoad() == Math.round(tonnes * perTonne)
                            && t.getStock() == (leach ? 3 * STUDY_MAKES[r] : 0),
                    String.format("%,.0f sq ft, road %,.0f, stock %,.0f", t.getLandSqFt(), t.getRoadLoad(), t.getStock()));
            double wages = 0;
            for (JobType j : JobType.values()) wages += t.getJobs(j) * PayTier.wageOf(j);
            double running = wages + t.getElectricityConsumption() * tariff + t.getWaterConsumption() * water;
            report("its consumables are its running cost less its wages, power and water - no diesel, the study naming none",
                    Math.abs(t.getConsumables() - (STUDY_RUNS[r] - running)) < 1e-9 && t.uses(Good.DIESEL) == 0,
                    String.format("%,.2f = %,.1f - %,.2f", t.getConsumables(), STUDY_RUNS[r], running));
            boolean all = true;
            StringBuilder got = new StringBuilder();
            for (SpreadPlanner.Case c : SpreadPlanner.Case.values()) {
                double pct = 100 * SpreadPlanner.caseEarns(t, c, imp, exp, running, 0, 1, tariff) / cost;
                all &= Math.abs(pct - STUDY_TABLE[r][c.ordinal()]) <= STUDY_ROUNDING + 1e-9;
                got.append(String.format("%s %.3f%%  ", cases[c.ordinal()], pct));
            }
            report("...its four cases are the study's 4.4 to its rounding: " + java.util.Arrays.toString(STUDY_TABLE[r]) + "%", all,
                    got.toString().trim());
        }
        assertTrue("the study's 4.4: a mine is always worth building - every case of all three over 2.5% a month, the leach plant's"
                + " best replacing imported cathode on the city's acid (C 3.4%)", STUDY_TABLE[2][2] == 3.4);
        assertTrue("...and the dig the spec's 2.4: .341 t of a sulphide field's copper a tonne of concentrate (2,200 t of 30% from"
                        + " 150,000 t of 0.5% ore, 88% recovered), 1.4 t of an oxide field's a tonne of cathode (72%)",
                CopperMining.CONC_DIG == .341 && CopperMining.LEACH_DIG == 1.4
                        && Math.abs(STUDY_MAKES[0] * CopperMining.CONC_DIG - 150_000 * .005) < 1);
    }

    /* ============================ 3. the types and the fit ============================ */

    /** How far copper's sulphide share of the fields may sit from Deposit.TYPE_SHARES (spec-materials 4: two points, WorldCheck 8's). */
    static final double TYPE_WITHIN = .02;

    static void typesAndFit() {
        out.println("\n--- 3. copper's types at I1's shares, and each building on the field that fits it ---");
        World w = World.of(Founding.DEFAULT_WORLD_SEED);
        long[] n = new long[2];
        for (int py = 0; py < World.poolsOnSide(CU); py++) {
            for (int px = 0; px < World.poolsOnSide(CU); px++) {
                for (Deposit d : w.poolFields(CU, px, py)) n[CU.typesOf().indexOf(d.type())]++;
            }
        }
        double sulphide = (double) n[0] / (n[0] + n[1]);
        report("copper's fields on the default world are sulphide at TYPE_SHARES' 80% and oxide 20%, within two points (the study's 1.4)",
                Deposit.TYPE_SHARES[SULPHIDE.ordinal()] == .80 && Deposit.TYPE_SHARES[OXIDE.ordinal()] == .20
                        && Math.abs(sulphide - .80) <= TYPE_WITHIN,
                String.format("%,d fields, %.2f%% sulphide", n[0] + n[1], 100 * sulphide));
        Game g = copperTown("copperminingcheck-fit", 3);
        CopperMining cm = g.getSectors().copperMining();
        BuildingManager b = g.getBuildingManager();
        LandManager land = g.getLandManager();
        BuildingsTemplate mill = b.getTemplate(CopperMining.MINE_AND_MILL), large = b.getTemplate(CopperMining.LARGE_MINE),
                leach = b.getTemplate(CopperMining.LEACH_PLANT);
        int oxide = land.getSites(CU, OXIDE), sul = land.getSites(CU, SULPHIDE);
        report("fixture: a town founded on the oxide field nearest the default site, three sulphide sites by fiat (the first type)",
                oxide > 0 && sul == 3, String.format("%d oxide site(s), %,.0f kt of copper; %d sulphide (%s)", oxide,
                        land.getRemaining(CU, OXIDE) / 1e3, sul, lastSearch));
        assertTrue("a Copper Mine & Mill fits a sulphide site, three of them the three; the Large Copper Mine, eight sites of one"
                        + " field, does not",
                cm.fits(mill) && g.hasDepositFor(mill, 3) && !g.hasDepositFor(mill, 4) && !cm.fits(large) && !g.hasDepositFor(large, 1));
        assertTrue("...a leach plant fits only the oxide sites, as many as there are", cm.fits(leach) && g.hasDepositFor(leach, oxide)
                && !g.hasDepositFor(leach, oxide + 1));
        quietly(() -> land.restoreSites(CU, oxide + 8, land.getOwnedAmount(CU) + 5 * CU.amountPerSite()));
        assertTrue("on eight sulphide sites of one piece the Large Copper Mine fits, one and not two",
                cm.fits(large) && g.hasDepositFor(large, 1) && !g.hasDepositFor(large, 2) && land.getSites(CU, SULPHIDE) == 8);
    }

    /**
     * A town to dig copper in: the Wealthy preset's cash and the founding
     * default's works, founded on the oxide field nearest the default site
     * whose centre plot is dry ground - so it holds an oxide field whole
     * (CityLand.found(): the field its centre is on goes with that ground) -
     * and `sulphideSites` sulphide sites by fiat, ground listed by fiat being
     * the first type.
     */
    static Game copperTown(String label, int sulphideSites) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> {
            g.newGame();
            World w = World.of(g.getCityLand().seed());
            Deposit field = nearest(w, OXIDE);
            LandManager land = g.getLandManager();
            land.install(CityLand.found(w, field.x(), field.y(), LandManager.km2(land.getOwnedSqFt())), new double[CityLand.KINDS],
                    w.totals(), w.seaTheta());
            land.restoreOwnedSqFt(land.getLandDrySqFt());
            g.setCashForTest(Founding.WEALTHY_CASH);
            // ...the ground restated to hold the town (LandConversion.restate()), then the sulphide by fiat on it.
            land.setOwnedSqFt(land.getOwnedSqFt() + 400_000_000L);
            int held = land.getSites(CU);
            lastSearch = String.format("field of %d site(s) at %,d m", field.sites(),
                    Math.round(Math.hypot(field.x() - w.foundingX(), field.y() - w.foundingY())));
            land.restoreSites(CU, held + sulphideSites, land.getOwnedAmount(CU) + sulphideSites * CU.amountPerSite());
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("House"), 400, true);
            g.buildStack(b.getTemplateByName("Small Grocery Store"), 4, true);
            g.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Paved Road"), 10, true);
        });
        return g;
    }

    /** The last town's field, for its report. */
    static String lastSearch = "";

    /** The copper field of a type nearest a world's founding site whose centre plot is dry ground, cell ring by cell ring. */
    static Deposit nearest(World w, Deposit.FieldType type) {
        long fx = w.foundingX(), fy = w.foundingY();
        int cx = (int) (fx / World.CELL), cy = (int) (fy / World.CELL);
        for (int ring = 0; ring < World.CELLS; ring++) {
            Deposit best = null;
            double far = Double.MAX_VALUE;
            for (int y = cy - ring; y <= cy + ring; y++) {
                for (int x = cx - ring; x <= cx + ring; x++) {
                    if (Math.max(Math.abs(x - cx), Math.abs(y - cy)) != ring) continue;
                    if (x < 0 || y < 0 || x >= World.CELLS || y >= World.CELLS) continue;
                    for (Deposit d : w.fieldsInCell(y * World.CELLS + x, CU)) {
                        if (d.type() != type || !World.dry(w.terrainAt(d.x(), d.y()))) continue;
                        double r = Math.hypot(d.x() - fx, d.y() - fy);
                        if (r < far) { far = r; best = d; }
                    }
                }
            }
            if (best != null) return best;
        }
        throw new IllegalStateException("no " + type + " copper field on dry ground on world " + w);
    }

    /* ============================ 4. a sulphide mine makes concentrate ============================ */

    static void sulphide() {
        out.println("\n--- 4. a sulphide mine makes concentrate, digging its own type as it is made, W exact ---");
        Game g = copperTown("copperminingcheck-sulphide", 3);
        CopperMining cm = g.getSectors().copperMining();
        BuildingManager b = g.getBuildingManager();
        LandManager land = g.getLandManager();
        quietly(() -> b.addStack(b.getTemplate(CopperMining.MINE_AND_MILL), 1, true));
        double rate = cm.getOperatingRate();
        double[] before = land.getExtractedByType(CU);
        double eBefore = land.getExtracted(CU);
        cm.produceFlow(Good.COPPER_CONC);
        Sector.Output o = cm.outputRow(Good.COPPER_CONC);
        double[] after = land.getExtractedByType(CU);
        int s = CU.typesOf().indexOf(SULPHIDE), x = CU.typesOf().indexOf(OXIDE);
        report("fixture: a Copper Mine & Mill standing: the month's concentrate its 2,200 t at the sector's rate",
                rate > 0 && o != null && Math.abs(o.produced - 2_200 * rate) < 1e-9,
                String.format("rate %.4f: %,.1f t made", rate, o == null ? 0 : o.produced));
        report("...the sulphide ground gives up CONC_DIG a tonne as it is made, the oxide nothing; E by it and W by type exact",
                Math.abs(after[s] - before[s] - o.produced * CopperMining.CONC_DIG) < 1e-6 && after[x] == before[x]
                        && Math.abs(land.getExtracted(CU) - eBefore - o.produced * CopperMining.CONC_DIG) < 1e-6 && LandCheck.wByType(land),
                String.format("%,.1f t of the field's copper", after[s] - before[s]));
        assertTrue("...and it makes no cathode: the lift's cathode, and the sector's, nothing", cm.lift() == 0
                && cm.getCapacity(Good.COPPER) == 0);
        // A type running short holds the mine to what is left.
        double left = 300;
        land.extract(CU, SULPHIDE, land.getRemaining(CU, SULPHIDE) - left);
        cm.produceFlow(Good.COPPER_CONC);
        report("with 300 t of the sulphide's copper left the mine makes what it holds, 300 / .341 t of concentrate, and W holds",
                Math.abs(cm.outputRow(Good.COPPER_CONC).produced - left / CopperMining.CONC_DIG) < 1e-6 && LandCheck.wByType(land),
                String.format("%,.1f t", cm.outputRow(Good.COPPER_CONC).produced));
        double[] spare = cm.retirementDemandAndCapacity(g);
        assertTrue("...and once the sulphide is out the mine is spare, measured in the tonnes it makes, and may be sold",
                land.getRemaining(CU, SULPHIDE) <= 1e-6 && spare != null && spare[0] == 0 && spare[1] == 2_200
                        && cm.mayRetire(b.getTemplate(CopperMining.MINE_AND_MILL)) && cm.unitsOf(b.getTemplate(CopperMining.MINE_AND_MILL)) == 2_200);
    }

    /* ============================ 5. an oxide field's leach plant makes cathode ============================ */

    static void oxide() {
        out.println("\n--- 5. an oxide field's leach plant makes cathode: the lift, the month's end dig, its acid ---");
        Game g = copperTown("copperminingcheck-oxide", 3);
        CopperMining cm = g.getSectors().copperMining();
        BuildingManager b = g.getBuildingManager();
        LandManager land = g.getLandManager();
        quietly(() -> b.addStack(b.getTemplate(CopperMining.LEACH_PLANT), 1, true));
        double rate = cm.getOperatingRate();
        double lift = cm.lift();
        report("fixture: a Leach & SX-EW Plant standing on the oxide field: the month's cathode its 1,000 t at the sector's rate",
                rate > 0 && lift == 1_000 * rate && cm.getCapacity(Good.COPPER) == 1_000,
                String.format("rate %.4f: %,.1f t", rate, lift));
        assertTrue("...what the market is priced on and the month makes (Sector.madeAtRate()): never past the lift",
                cm.getPlannedOutput(Good.COPPER) + cm.getExportBoundOutput(Good.COPPER) <= lift + 1e-9);
        assertTrue("...and it bids for its acid, 3,500 t a month at the rate", Math.abs(cm.bid(Good.ACID) - 3_500 * rate) < 1e-9);
        double[] before = land.getExtractedByType(CU);
        quietly(() -> cm.endOfMonth(g));
        double[] after = land.getExtractedByType(CU);
        int s = CU.typesOf().indexOf(SULPHIDE), x = CU.typesOf().indexOf(OXIDE);
        report("at the month's end the oxide ground gives up LEACH_DIG a tonne of the cathode made, the sulphide nothing; W exact",
                Math.abs(after[x] - before[x] - lift * CopperMining.LEACH_DIG) < 1e-6 && after[s] == before[s] && LandCheck.wByType(land),
                String.format("%,.1f t of the field's copper", after[x] - before[x]));
        double left = 700;
        land.extract(CU, OXIDE, land.getRemaining(CU, OXIDE) - left);
        report("with 700 t of the oxide's copper left the plant leaches what it holds, 700 / 1.4 t", Math.abs(cm.lift() - left / CopperMining.LEACH_DIG) < 1e-6,
                String.format("%,.1f t", cm.lift()));
        land.extract(CU, OXIDE, land.getRemaining(CU, OXIDE));
        double[] spare = cm.retirementDemandAndCapacity(g);
        assertTrue("...and once the oxide is out it leaches nothing and is spare, while the sulphide still holds copper",
                cm.lift() == 0 && land.getRemaining(CU, SULPHIDE) > 0 && spare != null && spare[1] == 1_000 && LandCheck.wByType(land));
    }

    /* ============================ 6. the plan ============================ */

    static void plan() {
        out.println("\n--- 6. the plan: the best earnings on the whole cost of the buildings that fit, their lorries in it ---");
        Game g = copperTown("copperminingcheck-plan", 3);
        CopperMining cm = g.getSectors().copperMining();
        BuildingManager b = g.getBuildingManager();
        MaterialsCheck.handWorkers(g);
        BusinessInvestment plans = g.getBusinessInvestment();
        BusinessInvestment.Decision d = cm.plan(plans, g);
        BuildingsTemplate expected = null;
        double best = 0;
        StringBuilder each = new StringBuilder();
        for (BuildingsTemplate t : b.getTemplatesBySector(Sectors.COPPER_MINING)) {
            if (!cm.fits(t) || !cm.staffing(t).passes()) continue;
            double e = cm.estimatedMonthlyProfit(t, plans), cost = plans.getCostOf(t, 1) + cm.fleetCapital(t);
            each.append(String.format("%s %.3f%%; ", t.getName(), 100 * e / cost));
            if (!cm.planner().passesMoney(cm, plans, g, e, cost)) continue;
            if (e / cost > best) { best = e / cost; expected = t; }
        }
        report("with oxide and sulphide sites free, the plan is the best earnings on the whole cost of those that fit and pass, or none",
                expected == null ? !d.build : d.build && d.template == expected, d.reason + " - " + each);
        BuildingsTemplate leach = b.getTemplate(CopperMining.LEACH_PLANT);
        double rate = BusinessInvestment.operatingRateOf(cm.getOperatingRate());
        double expect = plans.estimatedMakerProfit(cm, leach)
                - SpreadPlanner.ownCosts(leach, rate, cm.exchangeFactor(), g.getEconomyManager().getPricePerWatt()) - cm.fleetRunning(leach, rate);
        assertTrue("...each one's estimate the maker's, less its consumables and its lorries (ExtractionSector), to the bit",
                cm.estimatedMonthlyProfit(leach, plans) == expect);
    }

    /* ============================ 7. a played month ============================ */

    static void playedMonth() {
        out.println("\n--- 7. a played month: concentrate shipped, cathode made, acid bought, the ground given up by type, the audit closed ---");
        Game g = copperTown("copperminingcheck-month", 3);
        CopperMining cm = g.getSectors().copperMining();
        BuildingManager b = g.getBuildingManager();
        LandManager land = g.getLandManager();
        quietly(() -> {
            b.addStack(b.getTemplate(CopperMining.MINE_AND_MILL), 1, true);
            b.addStack(b.getTemplate(CopperMining.LEACH_PLANT), 1, true);
        });
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(2));
        double[] before = land.getExtractedByType(CU);
        quietly(() -> g.simulateMonths(1));
        double[] after = land.getExtractedByType(CU);
        Sector.Output conc = cm.outputRow(Good.COPPER_CONC), cathode = cm.outputRow(Good.COPPER);
        // ...the month's cathode as it was made: what came of it - for home, from the line abroad, and idled - is the lift (madeAtRate()).
        double lift = cathode == null ? 0 : cathode.planned + cathode.exportBound + cathode.idled;
        int s = CU.typesOf().indexOf(SULPHIDE), x = CU.typesOf().indexOf(OXIDE);
        report("fixture: a mine and mill and a leach plant standing, three months on: concentrate made and shipped, cathode made",
                conc != null && conc.produced > 0 && cathode != null && cathode.planned + cathode.exportBound > 0,
                conc == null || cathode == null ? "no rows" : String.format("%,.1f t of concentrate, %,.1f t of cathode; rate %.3f (vans %.3f)",
                        conc.produced, lift, cm.getOperatingRate(), cm.getVanRatio()));
        report("...the ground gave up the month's copper by type: the sulphide CONC_DIG a tonne of concentrate, the oxide LEACH_DIG a"
                        + " tonne of cathode; W exact",
                Math.abs(after[s] - before[s] - conc.produced * CopperMining.CONC_DIG) < 1e-6
                        && Math.abs(after[x] - before[x] - lift * CopperMining.LEACH_DIG) < 1e-6 && LandCheck.wByType(land),
                String.format("sulphide %,.1f t, oxide %,.1f t", after[s] - before[s], after[x] - before[x]));
        Sector.Split acid = cm.statement().bought.get(Good.ACID);
        report("...the leach plant bought its acid - from the world, no smelter standing", acid != null && acid.abroad > 0 && acid.atHome == 0,
                acid == null ? "none" : String.format("D$%,.1fk abroad", acid.abroad));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("the month's money audit closes", audit != null && audit.relative() < 1e-4,
                audit == null ? "none" : String.format("residual %.3g of what moved", audit.relative()));
    }
}
