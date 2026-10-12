package ham.citybuildersim;

import ham.citybuildersim.sectors.ExtractionSector;
import ham.citybuildersim.sectors.Forestry;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 * Forestry, checked (0.7.138, batch I9; docs/studies/spec-materials.md 2.1,
 * 2.3, 2.4, 2.11, 4 and 5's I9 row; the study's - materials-chains-research.md
 * - 1.3, 5 and 10's Q13 to Q15; the zoning study's - spec-zoning.md - 2, 3 and
 * 8.4, and Jerus's A12): the fifth materials chain's cutting industry, on the
 * end of Sectors.KEYS after Copper Refining.
 *
 * WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md; I6's
 * CoalMiningCheck to I8's CopperMiningCheck), and forestry is the chain that
 * is not a mine: no sites and no cutting limit (Q14), a crew's room set by
 * how long the forest it may cut would last (E), a forest that grows back
 * (Q13) and is planted on the ground the city zones for it (Q15, B), and a
 * building's clearing sold as a salvage cut (Z7). ZoningCheck keeps the law
 * and the clearing; this keeps what Forestry makes of them.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE SECTOR ON THE REGISTRY: Forestry the twenty-third key, after
 *      Copper Refining (Wood Products after it since 0.7.140); an ExtractionSector on the forest making sawlogs
 *      and pulpwood; what is as long as the keys grew with them; the two
 *      goods at the study's 5.3 prices, freight three eighths of each wedge,
 *      by the cubic metre at 0.9 t, flow goods; Resource.FOREST priced as
 *      pulpwood at 2% in the ground, so an offer's forest is in its price,
 *      and bare ground still means no deposit (LandMarket.bareGround()).
 *
 *   2. THE FOUR ROWS (ids 129 to 132, at parity): the study's 5.4 - cost,
 *      posts, what each cuts, its running cost - and its 5.5 table's four
 *      cases to its rounding; the Woodlot a tenth of the Tree Plantation in
 *      ground and growth (A12: 167 m3 a month), and losing in every case.
 *
 *   3. E, THE CREWS' RULE: a crew only while the cuttable forest lasts
 *      FOREST_CREW_MONTHS of every crew's cut with it - a 10 km2 forest gets
 *      none, a Logging Camp needs 17.5 km2, a Harvesting Operation 109 km2 -
 *      and in a town the plan holds with E's words under it and picks among
 *      the crews E allows past it.
 *
 *   4. THE CUT: a crew's month takes its two goods out of the cuttable
 *      forest together, E rising by it and W exact; the forest law's
 *      protected wood never cut, a forest running short holding both goods
 *      alike; spare once it is gone.
 *
 *   5. REGROWTH: E falls by FOREST_REGROWTH and the planted stands' growth -
 *      a plantation's 1,000 ha at PLANTED_M3_A_HA_A_YEAR, a woodlot's 100 -
 *      never below nothing, so a stand never grows timber past the forest's
 *      full stock, and W stays exact; with no stand the month is what it was.
 *
 *   6. PLANTING: only on Forestry's zone - no zone, no plantation - a Tree
 *      Plantation where it holds 1,000 ha, a Woodlot where it holds 100 but
 *      not 1,000, nothing under 100; only while the cut exceeds the
 *      regrowth; at the zone's price, from Forestry's cash, never borrowed.
 *
 *   7. THE SALVAGE CUT: a building on open forest clears its standing wood,
 *      which Forestry sells that month, half sawlogs and half pulpwood,
 *      billed by the builders at its crews' running cost a cubic metre (a
 *      Harvesting Operation's with none standing); none lost - a clearing
 *      after the month's market and across a save is sold the month after;
 *      W exact.
 *
 *   8. WHERE THE FOREST SETTLES (Q13): the study's mockup 4 - 25.4M m3 cut by
 *      75,000 a month settle at 29.1% standing, and at 40% with 40%
 *      protected - by the regrowth rule's own constant.
 *
 *   9. A PLAYED CITY: a crew and a clearing a month on - wood shipped, the
 *      salvage on Forestry's inputs and the builders' revenue in the same
 *      strike, the audit closed - and saved and reloaded the same.
 *
 * Every fixture causes its condition: a town on the default world, its
 * ground restated round the founding site to hold forest, the law's
 * protected share set to leave exactly the cuttable forest a section needs.
 */
public class ForestryCheck {

    static int fails = 0;
    /** Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. */
    static final PrintStream out = System.out;
    /** A stream that drops what is written to it. */
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

    /** Two wood figures the same to a part in a billion of the larger: one rounding, not a cubic metre. */
    static boolean same(double a, double b) { return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b))); }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        sector();
        rows();
        crewsRule();
        theCut();
        regrowth();
        planting();
        salvage();
        settles();
        played();
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static final Resource F = Resource.FOREST;

    /** Timber a square kilometre of forest holds: the world's (World.FOREST_M3_PER_KM2, Canada's 137 m3 a hectare). */
    static final double M3_A_KM2 = World.FOREST_M3_PER_KM2;

    /** W for the forest: the world's total equals what no city owns, what remains and what was taken out, to a rounding. */
    static boolean w(LandManager land) {
        return same(land.getWorldTotal(F), land.getUnowned(F) + land.getRemaining(F) + land.getExtracted(F));
    }

    /* ============================ 1. the sector on the registry ============================ */

    /** Wood's two goods, the study's 5.3: import and export price, D$ thousands a cubic metre. */
    static final Good[] GOODS = { Good.SAWLOGS, Good.PULPWOOD };
    /** ...each one's import price (pulpwood's widened from 70, the study's 1.2)... */
    static final double[] STUDY_IMPORT = { .110, .090 };
    /** ...and its export price. */
    static final double[] STUDY_EXPORT = { .065, .035 };
    /** Good.java's rule: freight three eighths of the wedge. */
    static final double FREIGHT_SHARE = .375;
    /** Green roundwood, tonnes a cubic metre (spec 2.3, est.). */
    static final double GREEN_WOOD = .9;
    /** The share of forest's pulpwood price its standing timber sells for in the ground (the study's 8). */
    static final double IN_GROUND = .02;

    static void sector() {
        out.println("--- 1. the sector on the registry: Forestry after copper's two, a cutter of the forest, its goods priced ---");
        String[] keys = Sectors.KEYS;
        int at = java.util.Arrays.asList(keys).indexOf(Sectors.FORESTRY);
        assertTrue("Sectors.KEYS has Forestry after Copper Refining, the twenty-third (Jerus's Q21: iron, coal, stone, copper, wood) -"
                + " Wood Products after it since 0.7.140", at == 22 && keys[at - 1].equals(Sectors.COPPER_REFINING)
                && keys[at + 1].equals(Sectors.WOOD_PRODUCTS));
        Sectors s = new Sectors(new BuildingManager(), new Markets());
        Forestry fo = s.forestry();
        assertTrue("...an extraction sector on the forest, found by its saved name, making sawlogs and pulpwood (and buying its vans, as"
                        + " every sector does); Mining's group; pulpwood its ground's good",
                s.byKey(Sectors.FORESTRY) == fo && fo instanceof ExtractionSector && fo.resource() == F
                        && java.util.Set.copyOf(fo.goodsMade()).equals(java.util.Set.of(Good.SAWLOGS, Good.PULPWOOD))
                        && java.util.Set.copyOf(fo.goodsUsed()).equals(java.util.Set.of(Good.VANS)) && fo.group() == BuildingType.MINING
                        && fo.groundGood() == Good.PULPWOOD);
        assertTrue("Equity's companies are the keys and then the bank; the households' share block a slot a company; the audit's pools a"
                        + " pool a sector in the keys' order; the bank's year of sheets a column a sector",
                Equity.COMPANIES.length == keys.length + 1 && Equity.COMPANIES[at].equals(Sectors.FORESTRY)
                        && HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD - HouseholdBalance.CELL_SLOTS_BEFORE_SHARES == Equity.COMPANIES.length
                        && MoneyAudit.POOL_NAMES.length == keys.length + 4 && MoneyAudit.POOL_NAMES[at + 1].equals("forestry")
                        && (int) new Bank().sheetYearToSave()[2] == keys.length);
        boolean prices = true;
        StringBuilder got = new StringBuilder();
        for (int i = 0; i < GOODS.length; i++) {
            Good g = GOODS[i];
            prices &= g.worldImportPrice() == STUDY_IMPORT[i] && g.worldExportPrice() == STUDY_EXPORT[i]
                    && Math.abs(g.baseFreight() - FREIGHT_SHARE * (STUDY_IMPORT[i] - STUDY_EXPORT[i])) < 1e-12
                    && "cubic metre".equals(g.unit()) && g.traded() && !g.stockable();
            got.append(String.format("%s %.3f/%.3f; ", g.name(), g.worldImportPrice(), g.worldExportPrice()));
        }
        report("wood's two goods at the study's 5.3 prices - pulpwood's import end widened to 90 (its 1.2) - freight three eighths of"
                + " each wedge, by the cubic metre, flow goods", prices, got.toString());
        assertTrue("...a cubic metre of either 0.9 t on the road (green roundwood, spec 2.3), by the BULK stream, and by sea general cargo",
                Good.SAWLOGS.tonnesPerUnit() == GREEN_WOOD && Good.PULPWOOD.tonnesPerUnit() == GREEN_WOOD
                        && Double.isNaN(Good.IRON.tonnesPerCubicMetre())
                        && Good.SAWLOGS.traffic() == Traffic.BULK && Good.PULPWOOD.traffic() == Traffic.BULK
                        && Good.SAWLOGS.cargo() == Ports.Cargo.GENERAL && Good.PULPWOOD.cargo() == Ports.Cargo.GENERAL);
        assertTrue("Resource.FOREST is priced as pulpwood, its one pricing good (spec 2.4), at 2% of its export price in the ground"
                        + " (the study's 8: \"its standing timber at the pulpwood price\")",
                F.good() == Good.PULPWOOD && F.goodOf(Deposit.FieldType.NONE) == Good.PULPWOOD && F.goodOf(null) == Good.PULPWOOD
                        && F.inGroundShare() == IN_GROUND);
        double[] km2 = new double[CityLand.AREAS];
        km2[CityLand.TOTAL] = 1;
        km2[CityLand.DRY] = 1;
        double[] none = new double[Resource.values().length], wood = none.clone();
        wood[F.ordinal()] = M3_A_KM2;
        double ground = .0007, level = 1;
        double with = LandMarket.price(km2, wood, ground, level), without = LandMarket.price(km2, none, ground, level);
        report("...so an offer's forest is in its price: a square kilometre's 13,700 m3 at US$0.70, about US$9,590 (to the US$5k"
                        + " rounding)",
                Math.abs((with - without) - M3_A_KM2 * Good.PULPWOOD.worldExportPrice() * IN_GROUND) <= 5,
                String.format("US$%,.0fk", with - without));
        LandParcel forested = new LandParcel(1, 0, 0, 0, 0, 0, 10, 10, km2, null, wood, 0, 0);
        double[] ore = none.clone();
        ore[Resource.IRON.ordinal()] = 1_000;
        LandParcel ironed = new LandParcel(2, 0, 0, 0, 0, 0, 10, 10, km2, null, ore, 0, 0);
        assertTrue("...and an offer holding forest and nothing else is still bare ground - forest is the terrain, not a deposit -"
                        + " while one holding ore is not (spec 2.4)",
                LandMarket.bareGround(forested) && !LandMarket.bareGround(ironed));
    }

    /* ============================ 2. the four rows against the study ============================ */

    /** Forestry's rows, by their permanent ids, in the study's 5.4 order and the woodlot after. */
    static final int[] ROWS = { Forestry.LOGGING_CAMP, Forestry.HARVESTING_OPERATION, Forestry.TREE_PLANTATION, Forestry.WOODLOT };

    /** The study's 5.4 for the first three: cost, D$ thousands all in. */
    static final double[] STUDY_COSTS = { 3_000, 15_000, 2_000 };
    /** ...posts. */
    static final int[] STUDY_POSTS = { 11, 18, 5 };
    /** ...sawlogs a month, m3 (and as much pulpwood). */
    static final double[] STUDY_SAWLOGS = { 1_000, 6_250, 0 };
    /** ...its running cost a month, D$ thousands ("Runs"). */
    static final double[] STUDY_RUNS = { 61, 217, 24 };
    /** The study's 5.5 table, % of the cost a month, cases A to D. */
    static final double[][] STUDY_TABLE = { { 1.3, 1.3, 4.6, 4.6 }, { 2.7, 2.7, 6.9, 6.9 }, { -1.2, -1.2, -1.2, -1.2 } };
    /** The study's rounding: its returns to a tenth of a point. */
    static final double STUDY_ROUNDING = .05;
    /** A plantation's hectares (the study's 5.4) and a woodlot's (A12). */
    static final double PLANTATION_HA = 1_000, WOODLOT_HA = 100;
    /** The year each first existed: 1900 - standing before the start - but the Harvesting Operation, 1973 (research-design.md E14, PIKA 75). */
    static final int[] SINCE = { 1900, 1973, 1900, 1900 };

    static void rows() {
        out.println("\n--- 2. the four rows at parity: the study's 5.4, and its 5.5 table's four cases to its rounding ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        UtilitiesHandler founding = new UtilitiesHandler();
        double tariff = founding.getPricerPerWatt(), water = founding.getPricePerWaterUnit();
        java.util.function.ToDoubleFunction<Good> imp = Good::worldImportPrice, exp = Good::worldExportPrice;
        BuildingsTemplate mine = b.getTemplate(ham.citybuildersim.sectors.Mining.IRON_MINE);
        double perTonne = mine.getRoadLoad() / mine.makes(Good.IRON);
        String[] cases = { "A", "B", "C", "D" };
        double[] pctOf = new double[ROWS.length];
        for (int r = 0; r < 3; r++) {
            BuildingsTemplate t = b.getTemplate(ROWS[r]);
            boolean crew = r < 2;
            double cost = t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials();
            report("the " + t.getName() + " (" + t.getId() + ") is the study's: D$" + String.format("%,.0f", STUDY_COSTS[r]) + "k all in, "
                            + STUDY_POSTS[r] + " posts, " + (crew ? String.format("%,.0f m3 of sawlogs and as much pulpwood a month", STUDY_SAWLOGS[r])
                            : "nothing to sell") + ", no site; Forestry's, MINING, dated " + SINCE[r],
                    cost == STUDY_COSTS[r] && t.getTotalJobs() == STUDY_POSTS[r] && t.site() == null
                            && t.makes(Good.SAWLOGS) == STUDY_SAWLOGS[r] && t.makes(Good.PULPWOOD) == STUDY_SAWLOGS[r]
                            && t.goodsMade().size() == (crew ? 2 : 0) && t.goodsUsed().isEmpty()
                            && Sectors.FORESTRY.equals(t.getSector()) && t.getCategory() == BuildingType.MINING
                            && t.getSince() == SINCE[r] && t.getGen() == 0 && t.getElectricityConsumption() == 0 && t.getWaterConsumption() == 0,
                    String.format("cash %,.0f + 18 x %,d material", t.getCashCost(), t.getConstructionMaterials()));
            double wages = 0;
            for (JobType j : JobType.values()) wages += t.getJobs(j) * PayTier.wageOf(j);
            double running = wages + t.getElectricityConsumption() * tariff + t.getWaterConsumption() * water;
            report("...its consumables are the study's running cost less its wages - no power or water, the study giving none",
                    Math.abs(t.getConsumables() - (STUDY_RUNS[r] - running)) < 1e-9 && t.uses(Good.DIESEL) == 0,
                    String.format("%,.2f = %,.0f - %,.2f", t.getConsumables(), STUDY_RUNS[r], running));
            if (crew) {
                report("...its road load its logs out at 0.9 t a cubic metre, at the Iron Mine's 420 for 2,500 t; no yard",
                        t.getRoadLoad() == Math.round(2 * STUDY_SAWLOGS[r] * GREEN_WOOD * perTonne) && t.getStock() == 0,
                        String.format("road %,.0f", t.getRoadLoad()));
            } else {
                report("...its ground its 1,000 ha (10 km2), a cubic metre of growth on it a month at nameplate PLANTED x ha / 12",
                        Math.abs(t.getLandSqFt() - PLANTATION_HA / 100 * LandManager.SQ_FT_PER_KM2) <= .5
                                && Math.abs(Forestry.growthOf(t) - PLANTATION_HA * Forestry.PLANTED_M3_A_HA_A_YEAR / 12) < 1e-3,
                        String.format("%,.0f sq ft, %,.2f m3 a month", t.getLandSqFt(), Forestry.growthOf(t)));
            }
            boolean all = true;
            StringBuilder got = new StringBuilder();
            for (SpreadPlanner.Case c : SpreadPlanner.Case.values()) {
                double pct = 100 * SpreadPlanner.caseEarns(t, c, imp, exp, running, 0, 1, tariff) / cost;
                all &= Math.abs(pct - STUDY_TABLE[r][c.ordinal()]) <= STUDY_ROUNDING + 1e-9;
                got.append(String.format("%s %.3f%%  ", cases[c.ordinal()], pct));
                if (c == SpreadPlanner.Case.A) pctOf[r] = pct;
            }
            report("...its four cases are the study's 5.5 to its rounding: " + java.util.Arrays.toString(STUDY_TABLE[r]) + "%", all,
                    got.toString().trim());
        }
        boolean drawn = true;
        for (int id : ROWS) drawn &= BuildingVisual.of(b.getTemplate(id)).site() == null;
        assertTrue("...and the map draws each as a building on its own ground, on no resource's sites - a crew cuts terrain, so the"
                + " map's districts sum to Forestry's buildings (BuildingVisual)", drawn);
        BuildingsTemplate plantation = b.getTemplate(Forestry.TREE_PLANTATION), woodlot = b.getTemplate(Forestry.WOODLOT);
        double cost = woodlot.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * woodlot.getConstructionMaterials();
        double plantationCost = plantation.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * plantation.getConstructionMaterials();
        report("the Woodlot (132) is a tenth of the plantation (Jerus's A12): 100 ha, its cost a tenth, 167 m3 a month once grown, its"
                        + " consumables a tenth, one post, nothing to sell, Forestry's, dated 1900",
                Math.abs(woodlot.getLandSqFt() - WOODLOT_HA / 100 * LandManager.SQ_FT_PER_KM2) <= .5 && cost == plantationCost / 10
                        && Math.round(Forestry.growthOf(woodlot)) == 167
                        && Math.abs(Forestry.growthOf(woodlot) * 10 - Forestry.growthOf(plantation)) < 1e-3
                        && Math.abs(woodlot.getConsumables() * 10 - plantation.getConsumables()) < 1e-9
                        && woodlot.getTotalJobs() == 1 && woodlot.goodsMade().isEmpty() && Sectors.FORESTRY.equals(woodlot.getSector())
                        && woodlot.getSince() == 1900,
                String.format("D$%,.0fk, %,.0f sq ft, %,.2f m3 a month", cost, woodlot.getLandSqFt(), Forestry.growthOf(woodlot)));
        double wages = PayTier.wageOf(JobType.NO_DIPLOMA);
        double woodlotPct = 100 * SpreadPlanner.caseEarns(woodlot, SpreadPlanner.Case.A, imp, exp, wages, 0, 1, tariff) / cost;
        report("...and like the plantation it never pays - nothing to sell for 25 years: every case below nothing, a post's wage and its"
                        + " seedlings on its cost (the study's 5.5: the plantation -1.2%)",
                woodlotPct < 0 && pctOf[2] < 0, String.format("%.3f%% a month", woodlotPct));
    }

    /* ============================ 3. E, the crews' rule ============================ */

    static void crewsRule() {
        out.println("\n--- 3. E: a crew only while the forest it may cut lasts 120 months of every crew's cut with it ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        BuildingsTemplate camp = b.getTemplate(Forestry.LOGGING_CAMP), ops = b.getTemplate(Forestry.HARVESTING_OPERATION);
        double campNeeds = Forestry.FOREST_CREW_MONTHS * Forestry.cutOf(camp) / M3_A_KM2;
        double opsNeeds = Forestry.FOREST_CREW_MONTHS * Forestry.cutOf(ops) / M3_A_KM2;
        report("a 10 km2 forest gets no crew: its 137,000 m3 is under a Logging Camp's 120 months of 2,000 m3 (spec-zoning 3.2)",
                !Forestry.roomFor(10 * M3_A_KM2, 0, camp) && !Forestry.roomFor(10 * M3_A_KM2, 0, ops),
                String.format("%,.0f m3 against %,.0f", 10 * M3_A_KM2, Forestry.FOREST_CREW_MONTHS * Forestry.cutOf(camp)));
        report("...a Logging Camp needs 17.5 km2 of cuttable forest and a Harvesting Operation 109 km2 (spec 2.11), at the world's"
                        + " 13,700 m3 a km2",
                Math.abs(campNeeds - 17.5) < .05 && Math.abs(opsNeeds - 109.5) < .05
                        && Forestry.roomFor(campNeeds * M3_A_KM2, 0, camp) && !Forestry.roomFor(campNeeds * M3_A_KM2 - 1, 0, camp)
                        && Forestry.roomFor(opsNeeds * M3_A_KM2, 0, ops) && !Forestry.roomFor(opsNeeds * M3_A_KM2 - 1, 0, ops),
                String.format("%.2f and %.2f km2", campNeeds, opsNeeds));
        assertTrue("...and every crew's cut counts: with a Harvesting Operation on the forest a camp needs 120 months of both",
                !Forestry.roomFor(opsNeeds * M3_A_KM2, Forestry.cutOf(ops), camp)
                        && Forestry.roomFor((opsNeeds + campNeeds) * M3_A_KM2, Forestry.cutOf(ops), camp));
        Game g = forestTown("forestrycheck-e");
        Forestry fo = g.getSectors().forestry();
        LandManager land = g.getLandManager();
        MaterialsCheck.handWorkers(g);
        double full = land.getRemaining(F);
        report("fixture: a town whose ground round the founding site holds more forest than a Harvesting Operation's room",
                full > opsNeeds * M3_A_KM2 * 1.1, String.format("%,.0f m3 on %s", full, LandManager.areaWords(land.getForestSqFt())));
        BusinessInvestment plans = g.getBusinessInvestment();
        // The law's protected share leaves exactly the cuttable forest each reading needs.
        protect(g, 10 * M3_A_KM2);
        BusinessInvestment.Decision ten = fo.plan(plans, g);
        report("with 10 km2 of it cuttable the plan holds, in E's words", !ten.build && ten.reason.contains("would last")
                && same(land.cuttableForest(), 10 * M3_A_KM2), ten.reason);
        protect(g, campNeeds * M3_A_KM2 + 1);
        BusinessInvestment.Decision campOnly = fo.plan(plans, g);
        report("...with a Logging Camp's 17.5 km2 the plan is a camp or, refused by the money gate or the staff, says why - never a"
                        + " Harvesting Operation", campOnly.build ? campOnly.template.getId() == Forestry.LOGGING_CAMP
                        : !campOnly.reason.contains("would last"),
                campOnly.reason);
        protect(g, full);
        BusinessInvestment.Decision any = fo.plan(plans, g);
        BuildingsTemplate expected = null;
        double best = 0;
        StringBuilder each = new StringBuilder();
        for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(Sectors.FORESTRY)) {
            if (!Forestry.isCrew(t) || !Forestry.roomFor(land.cuttableForest(), fo.crewsCut(true), t) || !fo.staffing(t).passes()) continue;
            double e = fo.estimatedMonthlyProfit(t, plans), cost = plans.getCostOf(t, 1) + fo.fleetCapital(t);
            each.append(String.format("%s %.3f%%; ", t.getName(), 100 * e / cost));
            if (!fo.planner().passesMoney(fo, plans, g, e, cost)) continue;
            if (e / cost > best) { best = e / cost; expected = t; }
        }
        report("...with the whole forest cuttable the plan is the best earnings on the whole cost of the crews E allows, or none",
                expected == null ? !any.build : any.build && any.template == expected, any.reason + " - " + each);
        assertTrue("...a planted stand is never a candidate (it is planted, not ordered), and nothing is planned on no forest",
                !any.build || Forestry.isCrew(any.template));
    }

    /**
     * Sets the forest law's protected share - kept as large as the protected
     * share, so the law keeps no working forest past it - to leave `cuttable`
     * cubic metres that may be cut, or the whole forest.
     */
    static void protect(Game g, double cuttable) {
        LandManager land = g.getLandManager();
        double owned = land.getOwnedAmount(F), standing = land.getRemaining(F);
        double p = Math.max(0, Math.min(1, (standing - cuttable) / owned));
        quietly(() -> g.setForestLaw(p, p));
    }

    /**
     * A town to cut forest in: the Wealthy preset's cash and the founding
     * default's works, its ground restated round the default world's founding
     * site to 4,000 km2 more (LandManager.setOwnedSqFt()), forest and bare
     * ground both.
     */
    static Game forestTown(String label) {
        return forestTown(GameFiles.scratch(label));
    }

    /** ...in these files, so a harness can load what it saves. */
    static Game forestTown(GameFiles files) {
        Game g = new Game(files);
        quietly(() -> {
            g.newGame();
            g.setCashForTest(Founding.WEALTHY_CASH);
            LandManager land = g.getLandManager();
            land.setOwnedSqFt(land.getOwnedSqFt() + LandManager.sqFt(TOWN_KM2));
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("House"), 400, true);
            g.buildStack(b.getTemplateByName("Small Grocery Store"), 4, true);
            g.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Paved Road"), 10, true);
        });
        return g;
    }

    /** The ground a forest town is restated to hold past its founding, km2: enough for a Harvesting Operation's 109 km2 of forest at the default world's share. */
    static final double TOWN_KM2 = 1_000;

    /* ============================ 4. the cut ============================ */

    static void theCut() {
        out.println("\n--- 4. the cut: a crew's two goods out of the cuttable forest together, the protected wood never, W exact ---");
        Game g = forestTown("forestrycheck-cut");
        Forestry fo = g.getSectors().forestry();
        BuildingManager b = g.getBuildingManager();
        LandManager land = g.getLandManager();
        quietly(() -> b.addStack(b.getTemplate(Forestry.LOGGING_CAMP), 1, true));
        double rate = fo.getOperatingRate();
        double e0 = land.getDepletionState()[F.ordinal()], left0 = land.getRemaining(F);
        fo.produceFlow(Good.SAWLOGS);
        fo.produceFlow(Good.PULPWOOD);
        Sector.Output saw = fo.outputRow(Good.SAWLOGS), pulp = fo.outputRow(Good.PULPWOOD);
        double e1 = land.getDepletionState()[F.ordinal()];
        report("fixture: a Logging Camp standing: the month's sawlogs and pulpwood its 1,000 m3 each at the sector's rate",
                rate > 0 && saw != null && pulp != null && same(saw.produced, 1_000 * rate) && same(pulp.produced, 1_000 * rate),
                String.format("rate %.4f: %,.1f + %,.1f m3", rate, saw == null ? 0 : saw.produced, pulp == null ? 0 : pulp.produced));
        report("...taken out of the forest together, once: E rises by the 2,000 m3 at the rate, what stands falls by it, W exact",
                same(e1 - e0, 2_000 * rate) && same(left0 - land.getRemaining(F), 2_000 * rate) && same(fo.getCutThisMonth(), 2_000 * rate)
                        && w(land),
                String.format("E +%,.1f m3", e1 - e0));
        // The law keeps all but 500 m3 standing: the cut stops at the protected wood, both goods alike.
        protect(g, 500);
        double protectedWood = land.getProtectedWood();
        fo.produceFlow(Good.PULPWOOD);
        fo.produceFlow(Good.SAWLOGS);
        report("with the law leaving 500 m3 to cut the crew takes it, 250 m3 of each - in either good's order - and the protected wood"
                        + " still stands",
                same(fo.outputRow(Good.SAWLOGS).produced, 250) && same(fo.outputRow(Good.PULPWOOD).produced, 250)
                        && same(land.getRemaining(F), protectedWood) && land.cuttableForest() <= 1e-6 && w(land),
                String.format("%,.1f + %,.1f m3; %,.0f m3 kept", fo.outputRow(Good.SAWLOGS).produced, fo.outputRow(Good.PULPWOOD).produced,
                        protectedWood));
        fo.produceFlow(Good.SAWLOGS);
        fo.produceFlow(Good.PULPWOOD);
        double[] spare = fo.retirementDemandAndCapacity(g);
        assertTrue("...a month later it cuts nothing, and with nothing it may cut its crew is spare, measured in its cut, and may be sold",
                fo.outputRow(Good.SAWLOGS).produced == 0 && fo.outputRow(Good.PULPWOOD).produced == 0 && spare != null && spare[0] == 0
                        && spare[1] == 2_000 && fo.mayRetire(b.getTemplate(Forestry.LOGGING_CAMP))
                        && fo.unitsOf(b.getTemplate(Forestry.LOGGING_CAMP)) == 2_000);
    }

    /* ============================ 5. regrowth ============================ */

    static void regrowth() {
        out.println("\n--- 5. regrowth: a 240th of what was cut and the planted stands' growth a month, never past the full stock ---");
        LandManager l = office(400);
        double full = l.getRemaining(F);
        double cut = l.extract(F, 1_000_000);
        double e = l.getDepletionState()[F.ordinal()];
        l.endMonth();
        report("fixture: a million m3 cut; a month with no stand grows back FOREST_REGROWTH of it, to the bit, W exact",
                cut == 1_000_000 && l.getDepletionState()[F.ordinal()] == e * (1 - LandManager.FOREST_REGROWTH) && w(l),
                String.format("E %,.1f m3", l.getDepletionState()[F.ordinal()]));
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        double plantation = Forestry.growthOf(b.getTemplate(Forestry.TREE_PLANTATION)), woodlot = Forestry.growthOf(b.getTemplate(Forestry.WOODLOT));
        double e1 = l.getDepletionState()[F.ordinal()];
        l.setPlantedGrowth(3 * plantation + 2 * woodlot);
        assertTrue("...the regrowth the month's end will give is the rule's and the stands', and Forestry's planting reads it",
                same(l.regrowthDue(), e1 * LandManager.FOREST_REGROWTH + 3 * plantation + 2 * woodlot));
        l.endMonth();
        report("three plantations and two woodlots take 20 m3 a ha a year off E with it: 3 x 1,666.7 + 2 x 166.7 m3 more grows back",
                same(l.getDepletionState()[F.ordinal()], e1 * (1 - LandManager.FOREST_REGROWTH) - 3 * plantation - 2 * woodlot) && w(l)
                        && l.getPlantedGrowth() == 0,
                String.format("E %,.1f m3", l.getDepletionState()[F.ordinal()]));
        l.setPlantedGrowth(10 * e1);
        l.endMonth();
        assertTrue("...and never past the forest's full stock: stands growing more than was cut leave E at nothing, the forest full, W exact",
                l.getDepletionState()[F.ordinal()] == 0 && same(l.getRemaining(F), full) && w(l));
    }

    /** A land office on its own whose ground is drawn round the default world's founding site to this many square kilometres. */
    static LandManager office(double km2) {
        LandManager l = new LandManager();
        quietly(() -> l.setOwnedSqFt(LandManager.sqFt(km2)));
        return l;
    }

    /* ============================ 6. planting ============================ */

    static void planting() {
        out.println("\n--- 6. planting: only on Forestry's zone, a plantation or a woodlot by its room, only while the cut outgrows the regrowth ---");
        Game g = forestTown("forestrycheck-plant");
        Forestry fo = g.getSectors().forestry();
        BuildingManager b = g.getBuildingManager();
        LandManager land = g.getLandManager();
        BuildingsTemplate plantation = b.getTemplate(Forestry.TREE_PLANTATION), woodlot = b.getTemplate(Forestry.WOODLOT);
        quietly(() -> b.addStack(b.getTemplate(Forestry.HARVESTING_OPERATION), 1, true));
        fo.addCash(1_000_000);
        cutAMonth(g);
        int stands0 = b.getUnderConstructionById()[Forestry.TREE_PLANTATION];
        report("fixture: a Harvesting Operation's month cut, more than the regrowth; no zone", fo.getCutThisMonth() > land.regrowthDue()
                && g.getZoning().zone(Sectors.FORESTRY) == null, String.format("%,.0f m3 cut", fo.getCutThisMonth()));
        quietly(() -> fo.endOfMonth(g));
        report("with no Forestry zone nothing is planted (B: \"No Forestry zone, no plantation\")", fo.getPlantedThisMonth() == null
                && onOrder(b) == 0, fo.getPlantingWord());
        quietly(() -> g.setZone(Sectors.FORESTRY, Zoning.Size.KM2, .5, 0));
        cutAMonth(g);
        quietly(() -> fo.endOfMonth(g));
        report("...with a zone of 50 ha nothing either: it holds no woodlot's 100 ha", fo.getPlantedThisMonth() == null && onOrder(b) == 0,
                fo.getPlantingWord());
        quietly(() -> g.setZone(Sectors.FORESTRY, Zoning.Size.KM2, 5, 0));
        double cash = fo.getCash(), city = g.getCash();
        double quote = g.quoteBuild(woodlot, 1).total;
        cutAMonth(g);
        quietly(() -> fo.endOfMonth(g));
        report("a zone of 500 ha, free, plants a Woodlot (A12: a plantation's 1,000 ha will not fit), its ground the zone's used ground",
                fo.getPlantedThisMonth() == woodlot && b.getUnderConstructionById()[Forestry.WOODLOT] == 1
                        && same(land.usedBy(Sectors.FORESTRY), woodlot.getLandSqFt()),
                fo.getPlantingWord());
        report("...paid from Forestry's cash, the zone's price nothing, outside the interest test and never borrowed", same(cash - fo.getCash(), quote)
                        && g.getCash() == city,
                String.format("D$%,.1fk", cash - fo.getCash()));
        quietly(() -> g.setZone(Sectors.FORESTRY, Zoning.Size.KM2, 20, .5));
        double cash2 = fo.getCash(), city2 = g.getCash();
        double ground = land.priceFor(plantation.getLandSqFt(), Sectors.FORESTRY);
        double quote2 = g.quoteBuild(plantation, 1).total;
        cutAMonth(g);
        quietly(() -> fo.endOfMonth(g));
        report("a zone of 20 km2 at half the going price plants a Tree Plantation, the city paid the zone's price for its ground",
                fo.getPlantedThisMonth() == plantation && b.getUnderConstructionById()[Forestry.TREE_PLANTATION] == stands0 + 1
                        && same(cash2 - fo.getCash(), quote2 + ground) && same(g.getCash() - city2, ground)
                        && same(ground, .5 * plantation.getLandSqFt() * land.getPricePerSqFt()),
                String.format("ground D$%,.1fk", ground));
        // The stands on site counted as grown: a cut within them plants nothing more.
        quietly(() -> b.addStack(plantation, 10, true));
        cutAMonth(g);
        quietly(() -> fo.endOfMonth(g));
        report("...and with ten plantations growing more than the cut nothing more is planted", fo.getPlantedThisMonth() == null
                && fo.getCutThisMonth() <= land.regrowthDue(fo.plantedGrowth(true)), fo.getPlantingWord());
        assertTrue("...a planted stand is the cut's cost while there is a cut: never sold while a crew stands",
                !fo.mayRetire(plantation) && !fo.mayRetire(woodlot) && fo.mayRetire(b.getTemplate(Forestry.HARVESTING_OPERATION)));
        // Short of cash: nothing, and nothing borrowed.
        Game poor = forestTown("forestrycheck-poor");
        Forestry pf = poor.getSectors().forestry();
        quietly(() -> {
            poor.getBuildingManager().addStack(poor.getBuildingManager().getTemplate(Forestry.HARVESTING_OPERATION), 1, true);
            poor.setZone(Sectors.FORESTRY, Zoning.Size.KM2, 20, 0);
        });
        pf.addCash(-pf.getCash());
        cutAMonth(poor);
        double debt = poor.getEconomyManager().getBusinessDebtManager().getPrincipal(Sectors.FORESTRY);
        quietly(() -> pf.endOfMonth(poor));
        report("...and a Forestry with no cash plants nothing and borrows nothing", pf.getPlantedThisMonth() == null
                && poor.getEconomyManager().getBusinessDebtManager().getPrincipal(Sectors.FORESTRY) == debt, pf.getPlantingWord());
    }

    /** The month's cut, as the market's first step makes it: both goods' produceFlow(). */
    static void cutAMonth(Game g) {
        Forestry fo = g.getSectors().forestry();
        fo.produceFlow(Good.SAWLOGS);
        fo.produceFlow(Good.PULPWOOD);
        g.getLandManager().setPlantedGrowth(fo.plantedGrowth(false));
    }

    /** Planted stands on site. */
    static int onOrder(BuildingManager b) {
        int[] site = b.getUnderConstructionById();
        return site[Forestry.TREE_PLANTATION] + site[Forestry.WOODLOT];
    }

    /* ============================ 7. the salvage cut ============================ */

    static void salvage() {
        out.println("\n--- 7. the salvage cut: a clearing's wood sold half and half, billed by the builders, none lost, W exact ---");
        GameFiles files = GameFiles.scratch("forestrycheck-salvage");
        Game g = forestTown(files);
        Forestry fo = g.getSectors().forestry();
        LandManager land = g.getLandManager();
        // Build past the bare ground: a square kilometre of standing forest cleared.
        double bare = Math.max(0, land.getBareSqFt() - land.getAllocatedSqFt());
        quietly(() -> land.allocate(bare + LandManager.sqFt(1)));
        double cleared = land.getSalvagePending();
        report("fixture: a square kilometre built past the bare ground clears its standing wood, waiting to be sold",
                same(cleared, M3_A_KM2) && same(land.getForestClearedThisMonth(), M3_A_KM2) && w(land),
                String.format("%,.0f m3", cleared));
        double perM3 = fo.salvageCostPerM3();
        BuildingsTemplate ops = g.getBuildingManager().getTemplate(Forestry.HARVESTING_OPERATION);
        double opsRun = g.getBusinessInvestment().runningCostOf(ops) + ops.getConsumables() * fo.exchangeFactor();
        double builders = g.getSectors().construction().getSalvageCutThisMonth();
        fo.produceFlow(Good.SAWLOGS);
        fo.produceFlow(Good.PULPWOOD);
        report("Forestry sells it that month, with no crew standing: half sawlogs and half pulpwood, nothing left waiting",
                same(fo.outputRow(Good.SAWLOGS).produced, cleared / 2) && same(fo.outputRow(Good.PULPWOOD).produced, cleared / 2)
                        && fo.getCutThisMonth() == 0 && same(fo.getSalvageThisMonth(), cleared) && land.getSalvagePending() == 0,
                String.format("%,.0f + %,.0f m3", fo.outputRow(Good.SAWLOGS).produced, fo.outputRow(Good.PULPWOOD).produced));
        report("...billed by the builders at a Harvesting Operation's running cost a cubic metre (none standing), at today's wages: the"
                        + " builders' revenue the same", same(perM3, opsRun / Forestry.cutOf(ops))
                        && same(fo.getSalvageBilled(), cleared * perM3)
                        && same(g.getSectors().construction().getSalvageCutThisMonth() - builders, fo.getSalvageBilled()),
                String.format("D$%.4fk a m3, D$%,.1fk", perM3, fo.getSalvageBilled()));
        // With crews standing, the crews' own cost a cubic metre.
        quietly(() -> g.getBuildingManager().addStack(g.getBuildingManager().getTemplate(Forestry.LOGGING_CAMP), 2, true));
        BuildingsTemplate camp = g.getBuildingManager().getTemplate(Forestry.LOGGING_CAMP);
        double campRun = g.getBusinessInvestment().runningCostOf(camp) + camp.getConsumables() * fo.exchangeFactor();
        assertTrue("...with crews standing, the crews' own running cost a cubic metre", same(fo.salvageCostPerM3(), campRun / Forestry.cutOf(camp)));
        // None lost: a clearing after the market waits for the next month, and across a save.
        quietly(() -> land.allocate(LandManager.sqFt(.5)));
        double late = land.getSalvagePending();
        land.clearMonth();
        assertTrue("a clearing after the month's market waits past the month's end - none lost", same(late, M3_A_KM2 / 2)
                && land.getSalvagePending() == late && land.zoningState() != null && land.zoningState().salvageM3 == late);
        quietly(() -> g.saveGame(10, "forestrycheck"));
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        report("...and across a save: the loaded city's salvage waiting is the saved one's, and its next month sells it",
                twin.getLandManager().getSalvagePending() == late, String.format("%,.0f m3", twin.getLandManager().getSalvagePending()));
        Forestry tf = twin.getSectors().forestry();
        tf.produceFlow(Good.SAWLOGS);
        tf.produceFlow(Good.PULPWOOD);
        assertTrue("...sold the next month, half and half, after the crews' cut", same(tf.getSalvageThisMonth(), late)
                && same(tf.outputRow(Good.SAWLOGS).produced, tf.getCutThisMonth() / 2 + late / 2) && w(twin.getLandManager()));
    }

    /* ============================ 8. where the forest settles ============================ */

    /** The study's mockup 4 (its 5.2): the natural woods' full stock, m3, and the month's cut. */
    static final double MOCKUP_FULL = 25_400_000, MOCKUP_CUT = 75_000;
    /** ...where they settle: 29.1% standing, and at 40% with 40% protected (spec-zoning 3.3; spec 2.11). */
    static final double MOCKUP_SETTLES = .291, MOCKUP_PROTECTED = .40;

    static void settles() {
        out.println("\n--- 8. where the forest settles (Q13): 240 months of the cut gone, or the law's protected share ---");
        double bare = Forestry.settlesAt(MOCKUP_FULL, 0, MOCKUP_CUT, 0, 0);
        report("mockup 4's 25.4M m3 cut by 75,000 a month settle with 240 x the cut gone - 18.0M - at 29.1% standing",
                Math.abs(bare - MOCKUP_SETTLES) < .0005 && same(MOCKUP_CUT / LandManager.FOREST_REGROWTH, 18_000_000),
                String.format("%.2f%%", 100 * bare));
        double law = Forestry.settlesAt(MOCKUP_FULL, 0, MOCKUP_CUT, 0, MOCKUP_PROTECTED);
        report("...and at 40% with 40% protected: the crews cut the rest to the bone", same(law, MOCKUP_PROTECTED),
                String.format("%.2f%%", 100 * law));
        double planted = Forestry.settlesAt(MOCKUP_FULL, 0, MOCKUP_CUT, 32 * 1_000 * Forestry.PLANTED_M3_A_HA_A_YEAR / 12, 0);
        assertTrue("...the planted stands' growth off the cut (32 plantations, the study's 5.7, close the gap) and ground cleared for"
                        + " building out of it",
                planted > bare && Forestry.settlesAt(MOCKUP_FULL, 1_000_000, MOCKUP_CUT, 0, 0) < bare
                        && Forestry.settlesAt(MOCKUP_FULL, 0, 1e9, 0, 0) == 0);
    }

    /* ============================ 9. a played city ============================ */

    static void played() {
        out.println("\n--- 9. a played city: a crew and a clearing a month on, the books and the audit, saved and reloaded the same ---");
        GameFiles files = GameFiles.scratch("forestrycheck-played");
        Game g = forestTown(files);
        Forestry fo = g.getSectors().forestry();
        LandManager land = g.getLandManager();
        BuildingManager b = g.getBuildingManager();
        // Built ground lies on bare ground first, less the bare ground zones hold for others (Z7): a zone for the fields holding
        // every free bare square foot, a Logging Camp and a Woodlot put up on what is left - open forest - clear it.
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(2));
        double bareFree = Math.max(0, land.getBareSqFt() + land.getForestClearedSqFt() - land.getAllocatedSqFt());
        quietly(() -> {
            g.setZone(Sectors.AGRICULTURE, Zoning.Size.KM2, LandManager.km2(bareFree), 1);
            g.buildStack(b.getTemplate(Forestry.LOGGING_CAMP), 1, true);
            g.buildStack(b.getTemplate(Forestry.WOODLOT), 1, true);
        });
        double waiting = land.getSalvagePending();
        double clearedKm2 = LandManager.km2(b.getTemplate(Forestry.LOGGING_CAMP).getLandSqFt() + b.getTemplate(Forestry.WOODLOT).getLandSqFt());
        report("fixture: the fields' zone holding the free bare ground, a Logging Camp and a Woodlot put up clear the forest under"
                        + " them, its standing wood waiting to be sold",
                b.getQuantity(Forestry.LOGGING_CAMP) == 1 && b.getQuantity(Forestry.WOODLOT) == 1 && same(waiting, clearedKm2 * M3_A_KM2),
                String.format("%,.0f m3 on %.4f km2", waiting, clearedKm2));
        quietly(() -> g.simulateMonths(1));
        Sector.Output saw = fo.outputRow(Good.SAWLOGS);
        double billed = fo.getSalvageBilled(), sold = fo.getSalvageThisMonth();
        report("a month on: that month's sawlogs its crew's and half the salvage, nothing left waiting", saw != null && same(sold, waiting)
                        && same(saw.produced, fo.getCutThisMonth() / 2 + waiting / 2) && fo.getCutThisMonth() > 0
                        && land.getSalvagePending() == 0,
                String.format("%,.0f m3 cut, %,.0f salvaged", fo.getCutThisMonth(), sold));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("the month's money audit closes", audit != null && audit.relative() < 1e-4,
                audit == null ? "none" : String.format("residual %.3g of what moved", audit.relative()));
        quietly(() -> g.simulateMonths(1));
        Double input = fo.statement().otherInputs.get(Forestry.SALVAGE_LINE);
        Double revenue = g.getSectors().construction().statement().otherParts.get("Salvage cut cleared");
        report("...struck the next month: the salvage on Forestry's inputs, the builders' revenue named, the same figure",
                input != null && revenue != null && same(input, billed) && same(revenue, billed),
                String.format("D$%,.2fk / %s", input == null ? 0 : input, revenue == null ? "none" : String.format("D$%,.2fk", revenue)));
        Sector.Split exported = fo.statement().sold.get(Good.SAWLOGS);
        assertTrue("...and the wood sold - abroad, nobody here buying logs without a Wood Products mill (0.7.140)", exported != null && exported.abroad > 0);
        assertTrue("W exact after the months played", w(land));
        double cutBefore = fo.getCutThisMonth();
        quietly(() -> g.saveGame(10, "forestrycheck-played"));
        Game twin = new Game(files);
        quietly(() -> twin.loadGameSave(10));
        Forestry tf = twin.getSectors().forestry();
        assertTrue("saved and reloaded: the crews' cut and the forest the same", tf.getCutThisMonth() == cutBefore
                && twin.getLandManager().getRemaining(F) == land.getRemaining(F)
                && twin.getLandManager().getDepletionState()[F.ordinal()] == land.getDepletionState()[F.ordinal()]);
        quietly(() -> { g.toggleNextMonth(); twin.toggleNextMonth(); });
        report("...a month on, the two the same month: Forestry's cash and cut, the forest, the city's cash",
                Math.abs(fo.getCash() - tf.getCash()) < 1e-9 && fo.getCutThisMonth() == tf.getCutThisMonth()
                        && twin.getLandManager().getRemaining(F) == land.getRemaining(F) && Math.abs(g.getCash() - twin.getCash()) < 1e-9,
                String.format("cash %.6f/%.6f cut %.6f/%.6f forest %.3f/%.3f city %.6f/%.6f sectors %.6f/%.6f", fo.getCash(), tf.getCash(),
                        fo.getCutThisMonth(), tf.getCutThisMonth(), land.getRemaining(F), twin.getLandManager().getRemaining(F),
                        g.getCash(), twin.getCash(), g.getSectors().totalCash(), twin.getSectors().totalCash()));
    }
}
