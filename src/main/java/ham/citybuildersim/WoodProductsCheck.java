package ham.citybuildersim;

import ham.citybuildersim.sectors.Forestry;
import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.ProcessingSector;
import ham.citybuildersim.sectors.SpreadPlanner;
import ham.citybuildersim.sectors.WoodProducts;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 * Wood Products, checked (0.7.140, batch I10; docs/studies/spec-materials.md
 * 2.1, 2.2, 2.3, 2.7, 2.8, 2.12, 4 and 5's I10 row; the study's -
 * materials-chains-research.md - 1.2, 1.6 and 5.3 to 5.6): wood's processing
 * industry, the twenty-fourth key, on Forestry's logs.
 *
 * WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md; I6's
 * CoalMiningCheck to I9's ForestryCheck), and wood's mills add what the
 * other chains' works do not all have together: a by-product with two
 * buyers inside the sector (the sawmills' residues, the pulp mill's and the
 * pellet mill's), a second captive good one step on (the pulp mill's pulp,
 * the paper mill's), a works that makes more power than it draws and sells
 * it (the pulp mill's liquor), and a feed the city cuts itself (Forestry's
 * logs) bought before the world's.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE SECTOR ON THE REGISTRY: Wood Products the twenty-fourth and last
 *      key, after Forestry; a ProcessingSector making lumber, residues,
 *      panels, pulp, paper and pellets of sawlogs, pulpwood, residues, pulp
 *      and fuel oil, Iron & Steel's group; residues and pulp captive, lumber
 *      its planning good; what is as long as the keys grew with them; the six
 *      goods at the study's 5.3 prices, freight three eighths of each wedge,
 *      lumber, panels and residues by the cubic metre at their tonnes, the
 *      five stockable on HELD's end; the buyers (spec 2.12) - the logs, the
 *      residues and the pulp its own, the fuel oil the paper mill's, the rest
 *      nobody's here, shipped.
 *
 *   2. THE SIX ROWS (ids 133 to 138, at parity): the study's 5.4 and 5.6 -
 *      cost, posts, what each makes and uses, its running cost, the pulp
 *      mill's 13,500 kW and the paper mill's 4,900 kW, 1,450 water units and
 *      19 ha - the spec's 2.2 for the rest (power by the research's kWh a
 *      unit, water, ground, road load, stock), and their four cases, the
 *      study's 5.5 table to its rounding; then what the study says of them -
 *      a cubic metre of residues worth more to a pulp mill than to a pellet
 *      mill, the paper mill paying only where it replaces imports.
 *
 *   3. THE CHAIN, CAPTIVE: a sawmill, a pulp mill and a paper mill standing -
 *      the sawmill's residues to the pulp mill first, at the rate (min of
 *      what is made and what is needed), the pulp mill's pulp to the paper
 *      mill the same, nothing booked for either; the rest of the residues and
 *      the fuel oil bought, the lumber, the rest of the pulp and the paper
 *      shipped; the audit closes.
 *
 *   4. THE PULP MILL'S POWER SOLD (spec 2.8): it buys none from the grid and
 *      sells its 13,500 kW at the tariff, in its revenue; the city's utility
 *      pays it, and the grid counts it as generation.
 *
 *   5. FEED FROM FORESTRY BEFORE IMPORTS: a sawmill beside a Logging Camp
 *      buys the camp's sawlogs at home and imports only what the camp does
 *      not cut; beside a Harvesting Operation it imports none, and the rest
 *      of the logs ship.
 *
 *   6. THE PLAN AT FOUNDING: on imported feed for export - every mill's case
 *      A, at the founding's prices, its lorries and repairs with it - no mill
 *      clears the whole-cost hurdle (the study's 1.2), so the plan orders none
 *      and says so.
 *
 *   7. A PLAYED CITY, saved and reloaded the same: the mills' yards on HELD,
 *      the sector's cash and stock, a month on.
 *
 * Every fixture causes its condition: MaterialsCheck's town with the mills
 * put up in it, and ForestryCheck's forest town for the logs.
 */
public class WoodProductsCheck {

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

    static boolean same(double a, double b) { return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b))); }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        sector();
        rows();
        chain();
        power();
        feed();
        planAtFounding();
        played();
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. the sector ============================ */

    /** Wood's six goods, in Good's order (the spec's 2.3). */
    static final Good[] GOODS = { Good.LUMBER, Good.PANELS, Good.RESIDUES, Good.PULP, Good.PAPER, Good.WOOD_PELLETS };
    /** The study's 5.3: each one's import price, D$ thousands a cubic metre or a tonne. */
    static final double[] STUDY_IMPORT = { .340, .420, .045, .760, 1.050, .220 };
    /** ...and its export price. */
    static final double[] STUDY_EXPORT = { .250, .260, .015, .600, .880, .150 };
    /** ...its unit. */
    static final String[] STUDY_UNIT = { "cubic metre", "cubic metre", "cubic metre", "tonne", "tonne", "tonne" };
    /** ...the tonnes a cubic metre of it weighs (spec 2.3, est.: sawn lumber 0.5, OSB 0.62 [W35], chips green as the logs, 0.9); NaN for one by the tonne. */
    static final double[] TONNES_A_M3 = { .5, .62, .9, Double.NaN, Double.NaN, Double.NaN };
    /** ...and whether it keeps in a yard (spec 2.3: all but the residues). */
    static final boolean[] STOCKABLE = { true, true, false, true, true, true };
    /** Good.java's rule: freight three eighths of the wedge. */
    static final double FREIGHT_SHARE = .375;

    static void sector() {
        out.println("--- 1. the sector on the registry: Wood Products after Forestry, a works on the logs, its goods priced ---");
        String[] keys = Sectors.KEYS;
        int at = java.util.Arrays.asList(keys).indexOf(Sectors.WOOD_PRODUCTS);
        assertTrue("Sectors.KEYS has Wood Products after Forestry, the twenty-fourth and last (Jerus's Q21: iron, coal, stone, copper, wood)",
                at == 23 && keys[at - 1].equals(Sectors.FORESTRY) && keys.length == at + 1);
        Sectors s = new Sectors(new BuildingManager(), new Markets());
        WoodProducts wp = s.woodProducts();
        assertTrue("...a processing sector found by its saved name, Iron & Steel's group, making lumber, residues, panels, pulp, paper and"
                        + " pellets of sawlogs, pulpwood, residues, pulp and fuel oil (and its vans, as every sector); lumber its planning good",
                s.byKey(Sectors.WOOD_PRODUCTS) == wp && wp instanceof ProcessingSector && wp.group() == BuildingType.HEAVY_INDUSTRY
                        && new java.util.ArrayList<>(wp.goodsMade()).equals(java.util.List.of(Good.LUMBER, Good.RESIDUES, Good.PANELS,
                        Good.PULP, Good.PAPER, Good.WOOD_PELLETS))
                        && java.util.Set.copyOf(wp.goodsUsed()).equals(java.util.Set.of(Good.VANS, Good.SAWLOGS, Good.PULPWOOD, Good.RESIDUES,
                        Good.PULP, Good.FUEL_OIL))
                        && wp.planningGood() == Good.LUMBER);
        assertTrue("...residues and pulp captive - made and used inside it - and nothing else",
                wp.captive(Good.RESIDUES) && wp.captive(Good.PULP) && !wp.captive(Good.LUMBER) && !wp.captive(Good.PAPER)
                        && !wp.captive(Good.SAWLOGS) && !wp.captive(Good.PULPWOOD) && !wp.captive(Good.FUEL_OIL));
        assertTrue("Equity's companies are the keys and then the bank; the households' share block a slot a company; the audit's pools a"
                        + " pool a sector in the keys' order; the bank's year of sheets a column a sector",
                Equity.COMPANIES.length == keys.length + 1 && Equity.COMPANIES[at].equals(Sectors.WOOD_PRODUCTS)
                        && HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD - HouseholdBalance.CELL_SLOTS_BEFORE_SHARES == Equity.COMPANIES.length
                        && MoneyAudit.POOL_NAMES.length == keys.length + 4
                        && (int) new Bank().sheetYearToSave()[2] == keys.length);
        boolean prices = true;
        StringBuilder got = new StringBuilder();
        for (int i = 0; i < GOODS.length; i++) {
            Good g = GOODS[i];
            boolean m3 = !Double.isNaN(TONNES_A_M3[i]);
            prices &= g.worldImportPrice() == STUDY_IMPORT[i] && g.worldExportPrice() == STUDY_EXPORT[i]
                    && Math.abs(g.baseFreight() - FREIGHT_SHARE * (STUDY_IMPORT[i] - STUDY_EXPORT[i])) < 1e-12
                    && STUDY_UNIT[i].equals(g.unit()) && g.traded() && g.stockable() == STOCKABLE[i]
                    && (m3 ? g.tonnesPerUnit() == TONNES_A_M3[i] : g.tonnesPerUnit() == 1);
            got.append(String.format("%s %.3f/%.3f; ", g.name(), g.worldImportPrice(), g.worldExportPrice()));
        }
        report("the six goods at the study's 5.3 prices, freight three eighths of each wedge; lumber, panels and residues by the cubic"
                        + " metre at 0.5, 0.62 and 0.9 t, the rest by the tonne; all but the residues stockable",
                prices, got.toString().trim());
        int n = NationalAccounts.HELD.length;
        // ...and since FR2 (2026-10-18) the trucks after them, HELD's last: the five keep their places, one from the end.
        assertTrue("...the five stockable on HELD's end in Good's order, after copper's cathode and before FR2's trucks, so a save's"
                        + " held slots keep their places",
                n >= 7 && NationalAccounts.HELD[n - 7] == Good.COPPER && NationalAccounts.HELD[n - 6] == Good.LUMBER
                        && NationalAccounts.HELD[n - 5] == Good.PANELS && NationalAccounts.HELD[n - 4] == Good.PULP
                        && NationalAccounts.HELD[n - 3] == Good.PAPER && NationalAccounts.HELD[n - 2] == Good.WOOD_PELLETS
                        && NationalAccounts.HELD[n - 1] == Good.TRUCKS);
        assertTrue("...lumber, panels and paper by the goods stream and, by sea, in containers; residues, pulp and pellets by the bulk"
                        + " stream - chips and pellets dry bulk, pulp's bales general cargo",
                Good.LUMBER.traffic() == Traffic.GOODS && Good.PANELS.traffic() == Traffic.GOODS && Good.PAPER.traffic() == Traffic.GOODS
                        && Good.LUMBER.cargo() == Ports.Cargo.CONTAINER && Good.PAPER.cargo() == Ports.Cargo.CONTAINER
                        && Good.RESIDUES.traffic() == Traffic.BULK && Good.PULP.traffic() == Traffic.BULK && Good.WOOD_PELLETS.traffic() == Traffic.BULK
                        && Good.RESIDUES.cargo() == Ports.Cargo.DRY_BULK && Good.WOOD_PELLETS.cargo() == Ports.Cargo.DRY_BULK
                        && Good.PULP.cargo() == Ports.Cargo.GENERAL);
        java.util.Map<Good, java.util.List<String>> users = new java.util.EnumMap<>(Good.class), makers = new java.util.EnumMap<>(Good.class);
        Good[] watched = { Good.SAWLOGS, Good.PULPWOOD, Good.LUMBER, Good.PANELS, Good.RESIDUES, Good.PULP, Good.PAPER, Good.WOOD_PELLETS,
                Good.FUEL_OIL };
        for (Good g : watched) {
            java.util.List<String> u = new java.util.ArrayList<>(), m = new java.util.ArrayList<>();
            for (Sector sec : s.all()) {
                if (sec.isUser(g)) u.add(sec.key());
                if (sec.isMaker(g)) m.add(sec.key());
            }
            users.put(g, u);
            makers.put(g, m);
        }
        java.util.List<String> wood = java.util.List.of(Sectors.WOOD_PRODUCTS);
        report("the buyers (spec 2.12): Forestry's logs Wood Products'; residues and pulp its own; fuel oil the paper mill's, its first"
                        + " buyer here; lumber, panels, paper and pellets nobody's here - they ship until the construction upgrade, a"
                        + " basket and the electricity run",
                makers.get(Good.SAWLOGS).equals(java.util.List.of(Sectors.FORESTRY)) && users.get(Good.SAWLOGS).equals(wood)
                        && makers.get(Good.PULPWOOD).equals(java.util.List.of(Sectors.FORESTRY)) && users.get(Good.PULPWOOD).equals(wood)
                        && makers.get(Good.RESIDUES).equals(wood) && users.get(Good.RESIDUES).equals(wood)
                        && makers.get(Good.PULP).equals(wood) && users.get(Good.PULP).equals(wood)
                        && users.get(Good.FUEL_OIL).equals(wood)
                        && users.get(Good.LUMBER).isEmpty() && users.get(Good.PANELS).isEmpty() && users.get(Good.PAPER).isEmpty()
                        && users.get(Good.WOOD_PELLETS).isEmpty()
                        && Good.LUMBER.exportable() && Good.PANELS.exportable() && Good.PAPER.exportable() && Good.WOOD_PELLETS.exportable(),
                "users " + users);
    }

    /* ============================ 2. the six rows against the study ============================ */

    /** The rows, by their permanent ids, in the study's 5.4 order. */
    static final int[] ROWS = { WoodProducts.SAWMILL, WoodProducts.LARGE_SAWMILL, WoodProducts.PANEL_MILL, WoodProducts.PULP_MILL,
            WoodProducts.PAPER_MILL, WoodProducts.PELLET_MILL };

    /** The study's 5.4: each one's cost, D$ thousands all in. */
    static final double[] STUDY_COSTS = { 15_000, 150_000, 140_000, 260_000, 150_000, 30_000 };

    /** ...its posts. */
    static final int[] STUDY_POSTS = { 35, 130, 100, 200, 120, 30 };

    /** ...what it makes a month. */
    static final Good[][] STUDY_MADE = { { Good.LUMBER, Good.RESIDUES }, { Good.LUMBER, Good.RESIDUES }, { Good.PANELS }, { Good.PULP },
            { Good.PAPER }, { Good.WOOD_PELLETS } };
    /** ...and how much of each, a month (cubic metres or tonnes). */
    static final double[][] STUDY_MAKES = { { 2_080, 1_640 }, { 20_020, 15_785 }, { 15_000 }, { 11_000 }, { 5_500 }, { 6_000 } };

    /** ...what it takes a month. */
    static final Good[][] STUDY_FEED = { { Good.SAWLOGS }, { Good.SAWLOGS }, { Good.PULPWOOD }, { Good.PULPWOOD, Good.RESIDUES },
            { Good.PULP, Good.FUEL_OIL }, { Good.RESIDUES } };
    /** ...and how much of each (the paper mill's fuel in litres, the study's 5.6). */
    static final double[][] STUDY_USES = { { 4_000 }, { 38_500 }, { 27_750 }, { 28_000, 21_500 }, { 5_775, 850_000 }, { 13_200 } };

    /** ...its running cost a month, D$ thousands, the study's 5.4 "Runs" - power bought from the grid only (its 2.4), the paper mill's fuel in it. */
    static final double[] STUDY_RUNS = { 177, 844, 1_360, 2_170, 1_380, 183 };

    /** The study's 5.5 table, % of the cost a month, cases A to D. */
    static final double[][] STUDY_TABLE = { { -0.5, 0.7, 2.3, 1.1 }, { 0.1, 1.3, 2.8, 1.6 }, { 0.0, 1.1, 2.8, 1.7 }, { 0.4, 1.3, 1.9, 1.1 },
            { -0.6, 0.0, 0.6, 0.0 }, { 0.4, 1.7, 3.1, 1.8 } };

    /** The study's rounding: its returns to a tenth of a point. */
    static final double STUDY_ROUNDING = .05;

    /** The pulp mill's power past its own draw, kW (the study's 1.6: 11,000 t at 0.9 MWh a tonne [W42]). */
    static final double PULP_GEN = 13_500;

    /** The paper mill's draw, kW, its water units and its ground, ha (the study's 5.6). */
    static final double PAPER_KW = 4_900, PAPER_WATER = 1_450, PAPER_HA = 19;

    /** The pulp mill's water units (the study's 5.4). */
    static final double PULP_WATER = 11_600;

    /** The paper mill's posts by tier (the study's 5.4: 50/45/20/5). */
    static final int[] PAPER_POSTS = { 50, 45, 20, 5 };

    /** The sawmills' power, kWh a cubic metre of lumber: the middle of the research's 99-109 [W27]. */
    static final double SAWMILL_KWH = 104;
    /** ...the panel mill's, a cubic metre of OSB [W34]. */
    static final double OSB_KWH = 207;
    /** The study's month in hours (its 2.2: the Steel Mini-Mill's 3,700 kW for 6,000 t is 450 kWh a tonne). */
    static final double HOURS = 730;

    /** The posts' families (spec 2.2), no diploma / diploma / college / university, %: the heavy plants' and pulp and paper's. */
    static final double[] HEAVY = { 54, 35, 9, 2 }, PULP_AND_PAPER = { 42, 37, 17, 4 };

    /** Square feet in a hectare. */
    static final double SQ_FT_A_HA = 107_639.104;

    static void rows() {
        out.println("\n--- 2. the six rows at parity: the study's 5.4 and 5.6, and its 5.5 table's four cases to its rounding ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        UtilitiesHandler founding = new UtilitiesHandler();
        double tariff = founding.getPricerPerWatt(), water = founding.getPricePerWaterUnit();
        java.util.function.ToDoubleFunction<Good> imp = Good::worldImportPrice, exp = Good::worldExportPrice;
        BuildingsTemplate foundry = b.getTemplate(HeavyIndustry.ARC_FURNACE), miniMill = b.getTemplateByName("Steel Mini-Mill"),
                pelletising = b.getTemplate(HeavyIndustry.PELLETISING_PLANT);
        double foundryCost = foundry.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * foundry.getConstructionMaterials();
        // The research's power for each (spec 2.2): the sawmills' and the panel mill's kWh a unit, the pellet mill the Pelletising
        // Plant's per tonne, the paper mill the study's own, the pulp mill none bought.
        double[] kw = { Math.round(STUDY_MAKES[0][0] * SAWMILL_KWH / HOURS), Math.round(STUDY_MAKES[1][0] * SAWMILL_KWH / HOURS),
                Math.round(STUDY_MAKES[2][0] * OSB_KWH / HOURS), 0, PAPER_KW,
                Math.round(STUDY_MAKES[5][0] * pelletising.getElectricityConsumption() / pelletising.makes(Good.PELLETS)) };
        String[] cases = { "A", "B", "C", "D" };
        double[][] pct = new double[ROWS.length][4];
        for (int r = 0; r < ROWS.length; r++) {
            BuildingsTemplate t = b.getTemplate(ROWS[r]);
            double cost = t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials();
            boolean makes = t.goodsMade().size() == STUDY_MADE[r].length, uses = t.goodsUsed().size() == STUDY_FEED[r].length;
            for (int i = 0; i < STUDY_MADE[r].length; i++) makes &= t.makes(STUDY_MADE[r][i]) == STUDY_MAKES[r][i];
            for (int i = 0; i < STUDY_FEED[r].length; i++) uses &= t.uses(STUDY_FEED[r][i]) == STUDY_USES[r][i];
            boolean pulpMill = ROWS[r] == WoodProducts.PULP_MILL, paperMill = ROWS[r] == WoodProducts.PAPER_MILL;
            report("the " + t.getName() + " (" + t.getId() + ") is the study's: D$" + String.format("%,.0f", STUDY_COSTS[r]) + "k all in, "
                            + STUDY_POSTS[r] + " posts, what it makes and uses; Wood Products', HEAVY_INDUSTRY, on no site",
                    cost == STUDY_COSTS[r] && t.getTotalJobs() == STUDY_POSTS[r] && makes && uses
                            && Sectors.WOOD_PRODUCTS.equals(t.getSector()) && t.getCategory() == BuildingType.HEAVY_INDUSTRY && t.site() == null
                            && t.oldMetalTaken().isEmpty(),
                    String.format("cash %,.0f + 18 x %,d material", t.getCashCost(), t.getConstructionMaterials()));
            double[] fam = pulpMill ? PULP_AND_PAPER : HEAVY;
            int[] posts = paperMill ? PAPER_POSTS : split(STUDY_POSTS[r], fam);
            boolean tiers = t.getJobs(JobType.NO_DIPLOMA) == posts[0] && t.getJobs(JobType.DIPLOMA) == posts[1]
                    && t.getJobs(JobType.COLLEGE_ENGINEERING) == posts[2] && t.getJobs(JobType.UNIV_SCIENCE) == posts[3];
            double waterUnits = pulpMill ? PULP_WATER : paperMill ? PAPER_WATER
                    : Math.round(kw[r] * miniMill.getWaterConsumption() / miniMill.getElectricityConsumption());
            double sqFt = paperMill ? PAPER_HA * SQ_FT_A_HA : STUDY_COSTS[r] * foundry.getLandSqFt() / foundryCost;
            double tonnes = 0, stock = 0;
            for (int i = 0; i < STUDY_MADE[r].length; i++) {
                tonnes += STUDY_MAKES[r][i] * STUDY_MADE[r][i].tonnesPerUnit();
                if (STUDY_MADE[r][i].stockable()) stock += 3 * STUDY_MAKES[r][i];
            }
            for (int i = 0; i < STUDY_FEED[r].length; i++) tonnes += STUDY_USES[r][i] * STUDY_FEED[r][i].tonnesPerUnit();
            report("...its posts " + (paperMill ? "the study's 50/45/20/5" : pulpMill ? "pulp and paper's 42/37/17/4" : "the heavy plants' 54/35/9/2")
                            + "; its draw " + (pulpMill ? "none bought - its boiler meets it - and 13,500 kW made past it (the study's 1.6)"
                            : paperMill ? "the study's 4,900 kW" : "the research's kWh a unit over a 730-hour month")
                            + "; water " + (pulpMill || paperMill ? "the study's" : "the Steel Mini-Mill's a kW") + "; ground "
                            + (paperMill ? "its 19 ha" : "the Steel Foundry's per D$") + "; road load its tonnes in and out at the Foundry's 110"
                            + " for 2,520 t; three months of its stockable output in its yard",
                    tiers && t.getElectricityConsumption() == kw[r] && t.getGen() == (pulpMill ? PULP_GEN : 0)
                            && t.getWaterConsumption() == waterUnits && Math.abs(t.getLandSqFt() - sqFt) <= .5
                            && t.getRoadLoad() == Math.round(tonnes * foundry.getRoadLoad() / 2_520) && t.getStock() == stock,
                    String.format("%d/%d/%d/%d posts, %,.0f kW, gen %,.0f, water %,.0f, %,.0f sq ft, road %,.0f, stock %,.0f",
                            t.getJobs(JobType.NO_DIPLOMA), t.getJobs(JobType.DIPLOMA), t.getJobs(JobType.COLLEGE_ENGINEERING),
                            t.getJobs(JobType.UNIV_SCIENCE), (double) t.getElectricityConsumption(), t.getGen(), t.getWaterConsumption(),
                            t.getLandSqFt(), t.getRoadLoad(), t.getStock()));
            double wages = 0;
            for (JobType j : JobType.values()) wages += t.getJobs(j) * PayTier.wageOf(j);
            // Its running cost at founding, as BusinessInvestment.runningCostOf() strikes it: its posts, its draw and its water.
            double running = wages + t.getElectricityConsumption() * tariff + t.getWaterConsumption() * water;
            // The paper mill's fuel: the study names no price, so its running cost carries it at the middle of fuel oil's band.
            double fuel = t.uses(Good.FUEL_OIL) * (Good.FUEL_OIL.worldImportPrice() + Good.FUEL_OIL.worldExportPrice()) / 2;
            report("its consumables are the study's running cost less its wages, the power it buys and water" + (fuel > 0
                            ? ", and its fuel oil at the middle of the band" : ""),
                    Math.abs(t.getConsumables() - (STUDY_RUNS[r] - running - fuel)) < 1e-9 && t.getConsumables() > 0,
                    String.format("%,.4f = %,.0f - %,.2f%s", t.getConsumables(), STUDY_RUNS[r], running,
                            fuel > 0 ? String.format(" - %,.4f", fuel) : ""));
            boolean all = true;
            StringBuilder got = new StringBuilder();
            for (SpreadPlanner.Case c : SpreadPlanner.Case.values()) {
                pct[r][c.ordinal()] = 100 * SpreadPlanner.caseEarns(t, c, imp, exp, running, 0, 1, tariff) / cost;
                all &= Math.abs(pct[r][c.ordinal()] - STUDY_TABLE[r][c.ordinal()]) <= STUDY_ROUNDING + 1e-9;
                got.append(String.format("%s %.3f%%  ", cases[c.ordinal()], pct[r][c.ordinal()]));
            }
            report("...its four cases are the study's 5.5 to its rounding" + (pulpMill ? ", its power sales in them" : "") + ": "
                    + java.util.Arrays.toString(STUDY_TABLE[r]) + "%", all, got.toString().trim());
        }
        /*
         * WHERE caseEarns() READS THE ROWS OTHERWISE THAN THE STUDY DID: the
         * paper mill's fuel oil is a feed, priced as the case prices feed -
         * landed in A and D, at its export price in B and C - where the study
         * carried it in its running cost at one price it does not name. The
         * middle of the band, the row's, is within D$31.5k a month of either.
         */
        BuildingsTemplate paper = b.getTemplate(WoodProducts.PAPER_MILL);
        double litres = paper.uses(Good.FUEL_OIL), mid = (Good.FUEL_OIL.worldImportPrice() + Good.FUEL_OIL.worldExportPrice()) / 2;
        report("where caseEarns() and the study differ: the paper mill's fuel is a feed at the case's price, not one price in its running"
                        + " cost - within half the band either way, and no case past its rounding",
                Math.abs(litres * (Good.FUEL_OIL.worldImportPrice() - mid)) < 32 && Math.abs(litres * (mid - Good.FUEL_OIL.worldExportPrice())) < 32,
                String.format("D$%,.1fk landed, D$%,.1fk at export, the row's D$%,.1fk", litres * Good.FUEL_OIL.worldImportPrice(),
                        litres * Good.FUEL_OIL.worldExportPrice(), litres * mid));
        int a = SpreadPlanner.Case.A.ordinal(), bb = SpreadPlanner.Case.B.ordinal(), cc = SpreadPlanner.Case.C.ordinal();
        // The study's 5.5: "a cubic metre of residues is worth D$92 to a pulp mill (power sales included) and D$54 to a pellet mill".
        BuildingsTemplate pulp = b.getTemplate(WoodProducts.PULP_MILL), pellets = b.getTemplate(WoodProducts.PELLET_MILL);
        double pulpWorth = residueWorth(pulp, tariff, water), pelletWorth = residueWorth(pellets, tariff, water);
        report("residues have two buyers, and the pulp mill pays more for a cubic metre of them than the pellet mill (the study's 5.5): its"
                        + " earnings in case B, its residues at nothing, over the residues it takes",
                pulpWorth > pelletWorth && pelletWorth > 0, String.format("pulp mill D$%,.1f, pellet mill D$%,.1f a cubic metre", 1000 * pulpWorth,
                        1000 * pelletWorth));
        report("...the paper mill pays only where it replaces imported paper (the study's 5.6): nothing on the city's pulp exporting (B), its"
                        + " case C past it, and a loss on imported pulp (A)",
                Math.abs(pct[4][bb]) <= STUDY_ROUNDING && pct[4][cc] > 10 * Math.abs(pct[4][bb]) && pct[4][a] < 0,
                String.format("A %.3f%%, B %.3f%%, C %.3f%%", pct[4][a], pct[4][bb], pct[4][cc]));
        report("...and on imported feed for export (A) no mill earns more than 0.4% of its cost a month, under the hurdle (the study's 1.2)",
                maxOf(pct, a) <= .4 + STUDY_ROUNDING, String.format("at most %.3f%%", maxOf(pct, a)));
    }

    /** Posts split by a family's shares by largest remainder, ties to the less skilled (spec 2.2, O4-4). */
    static int[] split(int n, double[] fam) {
        double[] raw = new double[4];
        int[] fl = new int[4];
        int left = n;
        for (int i = 0; i < 4; i++) { raw[i] = n * fam[i] / 100; fl[i] = (int) Math.floor(raw[i] + 1e-9); left -= fl[i]; }
        Integer[] order = { 0, 1, 2, 3 };
        java.util.Arrays.sort(order, (x, y) -> {
            double rx = raw[x] - fl[x], ry = raw[y] - fl[y];
            return Math.abs(rx - ry) > 1e-9 ? Double.compare(ry, rx) : Integer.compare(x, y);
        });
        for (int i = 0; i < left; i++) fl[order[i]]++;
        return fl;
    }

    /** What a works earns at parity in case B with its residues at nothing, a cubic metre of the residues it takes, D$ thousands. */
    static double residueWorth(BuildingsTemplate t, double tariff, double water) {
        double wages = 0;
        for (JobType j : JobType.values()) wages += t.getJobs(j) * PayTier.wageOf(j);
        double running = wages + t.getElectricityConsumption() * tariff + t.getWaterConsumption() * water;
        java.util.function.ToDoubleFunction<Good> exp = g -> g == Good.RESIDUES ? 0 : g.worldExportPrice();
        return SpreadPlanner.caseEarns(t, SpreadPlanner.Case.B, Good::worldImportPrice, exp, running, 0, 1, tariff) / t.uses(Good.RESIDUES);
    }

    static double maxOf(double[][] pct, int c) {
        double m = Double.NEGATIVE_INFINITY;
        for (double[] row : pct) m = Math.max(m, row[c]);
        return m;
    }

    /* ============================ 3. the chain, captive ============================ */

    static void chain() {
        out.println("\n--- 3. the chain: the sawmill's residues to the pulp mill, its pulp to the paper mill, captive; the rest bought or shipped ---");
        Game g = MaterialsCheck.town("woodproductscheck-chain");
        BuildingManager b = g.getBuildingManager();
        WoodProducts wp = g.getSectors().woodProducts();
        quietly(() -> {
            b.addStack(b.getTemplate(WoodProducts.SAWMILL), 1, true);
            b.addStack(b.getTemplate(WoodProducts.PULP_MILL), 1, true);
            b.addStack(b.getTemplate(WoodProducts.PAPER_MILL), 1, true);
        });
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(2));
        Sector.Output residues = wp.outputRow(Good.RESIDUES), pulp = wp.outputRow(Good.PULP), lumber = wp.outputRow(Good.LUMBER),
                paper = wp.outputRow(Good.PAPER);
        Sector.Input resIn = wp.inputRow(Good.RESIDUES), pulpIn = wp.inputRow(Good.PULP), fuel = wp.inputRow(Good.FUEL_OIL);
        // The month's rate is the sector's, one for all its works, but the month reads it at three steps - the flow goods made, the
        // bids, the stockable goods made after the clearing (Markets' step 4) - so each row is read by its own ratio at its own step.
        double made = residues == null ? 0 : residues.captive / 1_640, bidAt = resIn == null ? 0 : resIn.needed / 21_500;
        report("fixture: a sawmill, a pulp mill and a paper mill standing, two months on: the sawmill's 1,640 m3 of residues at the rate all to"
                        + " the pulp mill, captive - under its 21,500 - none to the market; the pulp mill bids only for the rest of its need",
                made > 0 && bidAt > 0 && residues.produced < 1e-6 && Math.abs(resIn.bid - (resIn.needed - residues.captive)) < 1e-6,
                residues == null || resIn == null ? "none" : String.format("rate %.4f: %,.1f m3 captive, %,.1f to market; at the bid's rate %.4f,"
                        + " %,.1f needed and %,.1f bid", made, residues.captive, residues.produced, bidAt, resIn.needed, resIn.bid));
        // ...pulp is stockable: the paper mill's 5,775 t first, the rest to the yard or abroad.
        double stockAt = pulp == null ? 0 : pulp.captive / 5_775;
        report("...the pulp mill's pulp to the paper mill first, captive: min of its 11,000 t and the paper mill's 5,775 at the rate - the"
                        + " paper mill bids for none; the rest to its yard or abroad",
                stockAt > 0 && pulpIn != null && pulpIn.bid < 1e-6
                        && Math.abs(pulp.planned + pulp.exportBound + pulp.idled - (11_000 - 5_775) * stockAt) < 1e-6,
                pulp == null ? "none" : String.format("rate %.4f: %,.1f t captive, %,.1f made for the market; the paper mill bid %,.1f", stockAt,
                        pulp.captive, pulp.planned + pulp.exportBound + pulp.idled, pulpIn == null ? 0 : pulpIn.bid));
        report("...the paper mill's 850,000 litres of fuel oil needed at the rate it needs its 5,775 t of pulp - no refinery here, so all"
                        + " imported",
                fuel != null && pulpIn != null && fuel.needed > 0 && Math.abs(fuel.needed / 850_000 - pulpIn.needed / 5_775) < 1e-12
                        && fuel.bid > 0 && Math.abs(fuel.imported - fuel.bid) < 1e-6 && fuel.boughtLocal == 0,
                fuel == null ? "none" : String.format("%,.0f L needed, %,.0f imported", fuel.needed, fuel.imported));
        report("...its lumber and paper made for the market, nobody here buying them: none sold at home",
                lumber != null && paper != null && lumber.planned + lumber.exportBound + lumber.idled > 0
                        && paper.planned + paper.exportBound + paper.idled > 0 && lumber.soldLocal == 0 && paper.soldLocal == 0,
                lumber == null || paper == null ? "none" : String.format("lumber %,.1f m3, paper %,.1f t", lumber.planned + lumber.exportBound,
                        paper.planned + paper.exportBound));
        quietly(() -> g.simulateMonths(1));
        Sector.Split soldRes = wp.statement().sold.get(Good.RESIDUES), boughtRes = wp.statement().bought.get(Good.RESIDUES),
                boughtPulp = wp.statement().bought.get(Good.PULP);
        report("...on the books (struck at the next month's top): no sale of residues or pulp booked to itself - the residues bought only"
                        + " abroad, the pulp not bought at all",
                (soldRes == null || soldRes.atHome == 0) && boughtRes != null && boughtRes.atHome == 0 && boughtRes.abroad > 0
                        && (boughtPulp == null || boughtPulp.atHome + boughtPulp.abroad == 0),
                boughtRes == null ? "none" : String.format("residues bought D$%,.1fk abroad", boughtRes.abroad));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("the month's money audit closes", audit != null && audit.relative() < 1e-4,
                audit == null ? "none" : String.format("residual %.3g of what moved", audit.relative()));
    }

    /* ============================ 4. the pulp mill's power sold ============================ */

    static void power() {
        out.println("\n--- 4. the pulp mill's own power: none bought, its 13,500 kW sold to the grid at the tariff, the city's utility paying ---");
        Game g = MaterialsCheck.town("woodproductscheck-power");
        BuildingManager b = g.getBuildingManager();
        WoodProducts wp = g.getSectors().woodProducts();
        quietly(() -> {
            b.addStack(b.getTemplate(WoodProducts.PULP_MILL), 1, true);
            g.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            g.simulateMonths(2);
        });
        BuildingsTemplate mill = b.getTemplate(WoodProducts.PULP_MILL);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double price = g.getEconomyManager().getPricePerWatt();
        report("fixture: a town with a pulp mill standing, two months on: it draws nothing from the grid and sells 13,500 kW",
                b.getQuantity(WoodProducts.PULP_MILL) == 1 && mill.powerBought() == 0 && mill.powerSold() == PULP_GEN
                        && wp.getPowerSurplus() == PULP_GEN,
                String.format("Wood Products sells %,.0f kW, buys %,.0f", wp.getPowerSurplus(), mill.powerBought()));
        Sector.Statement st = wp.statement();
        assertTrue("its statement's powerSold is the 13,500 kW at the tariff, in its revenue past its sales; its electricity bill nothing",
                st.powerSold == PULP_GEN * price && st.powerSold > 0 && st.revenue == st.localSales + st.exports + st.otherRevenue + st.powerSold
                        && wp.getElectricityCost() == 0);
        assertTrue("the utility pays the works what their statement booked, and the grid counts their surplus as generation",
                u.getPowerBought() == g.getEconomyManager().getSectorPowerSold() && u.getWorksPower() >= PULP_GEN);
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("the month's money audit closes", audit.relative() < 1e-4, String.format("residual %.3g of what moved", audit.relative()));
    }

    /* ============================ 5. feed from Forestry before imports ============================ */

    static void feed() {
        out.println("\n--- 5. the logs: a sawmill buys Forestry's sawlogs at home first and imports only the rest; past its need they ship ---");
        Game g = ForestryCheck.forestTown("woodproductscheck-feed");
        BuildingManager b = g.getBuildingManager();
        WoodProducts wp = g.getSectors().woodProducts();
        Forestry fo = g.getSectors().forestry();
        quietly(() -> {
            b.addStack(b.getTemplate(Forestry.LOGGING_CAMP), 1, true);
            b.addStack(b.getTemplate(WoodProducts.SAWMILL), 1, true);
        });
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(2));
        Sector.Output saw = fo.outputRow(Good.SAWLOGS), pulpwood = fo.outputRow(Good.PULPWOOD);
        Sector.Input logs = wp.inputRow(Good.SAWLOGS);
        report("fixture: a Logging Camp and a sawmill on a forest town, two months on: the camp's sawlogs all sold at home to the sawmill,"
                        + " which imports only the rest of its need",
                saw != null && logs != null && saw.produced > 0 && logs.bid > saw.produced
                        && same(saw.soldLocal, saw.produced) && saw.exported == 0 && same(logs.boughtLocal, saw.produced)
                        && same(logs.imported, logs.bid - logs.boughtLocal) && logs.imported > 0,
                saw == null || logs == null ? "none" : String.format("cut %,.4f m3, sold at home %,.1f; the mill bid %,.1f, bought %,.1f at home"
                        + " and %,.1f abroad", saw.produced, saw.soldLocal, logs.bid, logs.boughtLocal, logs.imported));
        report("...and the camp's pulpwood, nobody here buying it without a panel or pulp mill, all shipped",
                pulpwood != null && pulpwood.produced > 0 && pulpwood.soldLocal == 0 && same(pulpwood.exported, pulpwood.produced),
                pulpwood == null ? "none" : String.format("%,.1f m3 cut, %,.1f shipped", pulpwood.produced, pulpwood.exported));
        quietly(() -> b.addStack(b.getTemplate(Forestry.HARVESTING_OPERATION), 1, true));
        quietly(() -> g.simulateMonths(1));
        saw = fo.outputRow(Good.SAWLOGS);
        logs = wp.inputRow(Good.SAWLOGS);
        // "None" to the cent, not to the bit (WN4, 2026-10-31): with the world's prices on the US's path the mill's bid less what it bought at home
        // left 2.3e-13 m3 to import, D$1e-14k abroad - dust of a subtraction, judged by same() as every other figure here.
        report("with a Harvesting Operation beside the camp, cutting more sawlogs than the mill takes: the mill imports none, its whole bid"
                        + " bought at home, and the rest of the logs ship",
                saw != null && logs != null && saw.produced > logs.bid && logs.bid > 0 && same(logs.bid - logs.imported, logs.bid) && same(logs.boughtLocal, logs.bid)
                        && same(saw.soldLocal, logs.bid) && same(saw.exported, saw.produced - saw.soldLocal),
                saw == null || logs == null ? "none" : String.format("cut %,.4f m3, sold at home %,.4f; the mill bid %,.4f, bought %,.4f at home, %.3e abroad;"
                        + " %,.4f shipped", saw.produced, saw.soldLocal, logs.bid, logs.boughtLocal, logs.imported, saw.exported));
        quietly(() -> g.simulateMonths(1));
        Sector.Split bought = wp.statement().bought.get(Good.SAWLOGS), sold = fo.statement().sold.get(Good.SAWLOGS);
        report("...on the books (struck at the next month's top): Wood Products bought its sawlogs at home and none abroad, Forestry sold at"
                        + " home and abroad",
                bought != null && bought.atHome > 0 && same(bought.atHome + bought.abroad, bought.atHome) && sold != null && sold.atHome > 0 && sold.abroad > 0,
                bought == null || sold == null ? "none" : String.format("bought D$%,.1fk at home, D$%.3ek abroad; sold D$%,.1fk at home, D$%,.1fk abroad",
                        bought.atHome, bought.abroad, sold.atHome, sold.abroad));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("the month's money audit closes", audit != null && audit.relative() < 1e-4,
                audit == null ? "none" : String.format("residual %.3g of what moved", audit.relative()));
    }

    /* ============================ 6. the plan at founding ============================ */

    static void planAtFounding() {
        out.println("\n--- 6. the plan at founding: on imported feed for export no mill clears the whole-cost hurdle, so none is ordered ---");
        Game g = MaterialsCheck.town("woodproductscheck-plan");
        WoodProducts wp = g.getSectors().woodProducts();
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(2));
        BusinessInvestment plans = g.getBusinessInvestment();
        boolean noTrade = true;
        for (Good gd : new Good[] { Good.SAWLOGS, Good.PULPWOOD, Good.RESIDUES, Good.PULP, Good.LUMBER, Good.PANELS, Good.PAPER, Good.WOOD_PELLETS }) {
            GoodsMarket m = g.getMarkets().get(gd);
            noTrade &= m.getImported() == 0 && m.getExported() == 0;
        }
        java.util.List<SpreadPlanner.Candidate> all = wp.appraise(plans, g);
        StringBuilder each = new StringBuilder();
        boolean anyPasses = false;
        for (SpreadPlanner.Candidate c : all) {
            each.append(String.format("%s %.3f%%; ", c.template().getName(), 100 * c.score()));
            anyPasses |= c.passes();
        }
        report("fixture: a founded town, two months on, trading no wood: each of the six weighed at the city's values - its feed landed, its"
                        + " output at the export price - with its lorries and its repairs",
                noTrade && all.size() == ROWS.length, each.toString().trim());
        BusinessInvestment.Decision d = wp.plan(plans, g);
        report("...none clears the money gate, so the plan orders none and says why", !anyPasses && !d.build && d.reason != null
                && !d.reason.isEmpty(), d.reason);
    }

    /* ============================ 7. a played city, saved and reloaded ============================ */

    static void played() {
        out.println("\n--- 7. a played city: the mills' yards on HELD, saved and reloaded the same ---");
        GameFiles files = GameFiles.scratch("woodproductscheck-played");
        Game g = MaterialsCheck.town(files);
        BuildingManager b = g.getBuildingManager();
        WoodProducts wp = g.getSectors().woodProducts();
        quietly(() -> {
            b.addStack(b.getTemplate(WoodProducts.LARGE_SAWMILL), 1, true);
            b.addStack(b.getTemplate(WoodProducts.PANEL_MILL), 1, true);
            b.addStack(b.getTemplate(WoodProducts.PULP_MILL), 1, true);
            b.addStack(b.getTemplate(WoodProducts.PELLET_MILL), 1, true);
        });
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(3));
        // The accounts measure what is held at the month's top, before anything is made (EconomyManager.updateNationalAccounts()):
        // so the yards at one month's end are the units the next month's accounts hold.
        Good[] five = { Good.LUMBER, Good.PANELS, Good.PULP, Good.PAPER, Good.WOOD_PELLETS };
        double[] yards = new double[five.length];
        for (int i = 0; i < five.length; i++) yards[i] = wp.getStock(five[i]);
        quietly(() -> g.simulateMonths(1));
        double[] state = g.getEconomyManager().getNationalAccountsState();
        // The five's slots where HELD keeps them (FR2's trucks come after).
        int n = NationalAccounts.HELD.length, at = 15, first = java.util.Arrays.asList(NationalAccounts.HELD).indexOf(Good.LUMBER);
        boolean held = state.length >= at + n && first >= 0;
        StringBuilder got = new StringBuilder();
        for (int i = 0; i < five.length && held; i++) {
            held &= state[at + first + i] == yards[i];
            got.append(String.format("%s %,.1f/%,.1f; ", five[i].name(), state[at + first + i], yards[i]));
        }
        report("fixture: a large sawmill, a panel mill, a pulp mill and a pellet mill, three months on: the next month's accounts hold"
                        + " wood's five as the sector's yards stood - lumber, panels, pulp and pellets in them, no paper",
                held && yards[0] > 0 && yards[1] > 0 && yards[2] > 0 && yards[3] == 0 && yards[4] > 0, got.toString().trim());
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("the month's money audit closes", audit != null && audit.relative() < 1e-4,
                audit == null ? "none" : String.format("residual %.3g of what moved", audit.relative()));
        quietly(() -> g.saveGame(10, "woodproductscheck-played"));
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        WoodProducts tw = twin.getSectors().woodProducts();
        boolean yardsSame = true;
        for (Good gd : five) yardsSame &= tw.getStock(gd) == wp.getStock(gd);
        assertTrue("saved and reloaded: the sector's cash and its five yards the same, and the accounts' held units",
                tw.getCash() == wp.getCash() && yardsSame
                        && java.util.Arrays.equals(twin.getEconomyManager().getNationalAccountsState(), g.getEconomyManager().getNationalAccountsState()));
        quietly(() -> { g.toggleNextMonth(); twin.toggleNextMonth(); });
        // A month on, a reloaded city with any works of the materials chains standing is not exact (QUEUE.md's FW6): in this same
        // town a Lime Kiln, an Electrolytic Refinery, a Cement Plant or the Coke Ovens leave the city's cash 0.6 to 2.8 thousandths
        // of D$1k apart, and its sectors' at most 1.9 thousandths, as these mills do (measured at 0.7.140) - so money is held the
        // same to RELOAD_RESIDUAL_K and the yards to a millionth of what they hold.
        report("...a month on, the two the same month to the reload's known residual: Wood Products' cash and yards, the city's cash",
                Math.abs(wp.getCash() - tw.getCash()) <= RELOAD_RESIDUAL_K && close(wp.getStock(Good.LUMBER), tw.getStock(Good.LUMBER))
                        && close(wp.getStock(Good.PULP), tw.getStock(Good.PULP)) && Math.abs(g.getCash() - twin.getCash()) <= RELOAD_RESIDUAL_K,
                String.format("cash %.6f/%.6f lumber %.3f/%.3f pulp %.3f/%.3f city %.6f/%.6f", wp.getCash(), tw.getCash(),
                        wp.getStock(Good.LUMBER), tw.getStock(Good.LUMBER), wp.getStock(Good.PULP), tw.getStock(Good.PULP),
                        g.getCash(), twin.getCash()));
    }

    /** A reloaded city's known residual a month on in money, D$ thousands (QUEUE.md's FW6): ten dollars, past the 2.8 measured. */
    static final double RELOAD_RESIDUAL_K = .01;

    /** ...and in what a yard holds, a share of it: a millionth. */
    static final double RELOAD_RESIDUAL = 1e-6;

    static boolean close(double a, double b) {
        return Math.abs(a - b) <= RELOAD_RESIDUAL * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }
}
