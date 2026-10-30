package ham.citybuildersim;

import ham.citybuildersim.sectors.CopperMining;
import ham.citybuildersim.sectors.CopperRefining;
import ham.citybuildersim.sectors.Manufacturing;
import ham.citybuildersim.sectors.ProcessingSector;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 * Copper Refining, checked (0.7.133, batch I8; docs/studies/spec-materials.md
 * 2.1, 2.7, 2.9, 2.12, 4 and 5's I8 row; the study's -
 * materials-chains-research.md - 1.2, 4, 7 and 10's Q11 and Q12): copper's
 * processing industry, the twenty-second key, and the Wire & Cable Works it feeds in
 * Manufacturing.
 *
 * WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md; I7's
 * CementConcreteCheck), and copper's works add three things the other
 * chains do not: a co-product another industry buys (the smelter's acid, the
 * leach plants'), a chain four works long inside one sector with two
 * captive goods, and the city's old copper bought before the market's scrap.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE SECTOR ON THE REGISTRY: a ProcessingSector, the twenty-second key,
 *      after Copper Mining (Forestry after it since 0.7.138); making cathode, anode, acid and rod of concentrate,
 *      anode, cathode and scrap, Iron & Steel's group; anode and cathode
 *      captive; cathode its planning good. Manufacturing makes wire and cable
 *      of rod, its planning good still fabricated steel.
 *
 *   2. THE FIVE ROWS ARE THE STUDY'S (ids 124 to 128, at parity): cost,
 *      posts, draw, makes and uses, ground, road load, stock, consumables,
 *      the scrap refinery's old copper; and their four cases, the study's 4.4
 *      table to its rounding, each row's running cost backed out of its own
 *      table (the scrap refinery's from its stated -0.02% in case A); then
 *      what the study says of them - the smelter loses on imported
 *      concentrate, imported scrap just breaks even, the rod mill's 11% a thin
 *      premium on a big tonnage, the wire works built to replace imports and
 *      not to export.
 *
 *   3. THE CHAIN BY ITS RATIOS: concentrate to anode and acid (0.9 t of acid
 *      a tonne of concentrate [K27]), anode to cathode, cathode to rod, rod to
 *      wire and cable; and a played chain - a smelter, a refinery and a rod
 *      mill standing beside a wire works - its anode and cathode captive at
 *      the rate (min of what is made and what is needed), its rod sold at
 *      home to the wire works, the rest of the rod and the wire shipped.
 *
 *   4. THE SMELTER'S ACID SOLD, THE LEACH PLANT'S BOUGHT: a smelter beside a
 *      leach plant makes 29,700 t of acid at the rate, the leach plant buys
 *      its 3,500 t of it at home, and the rest ships.
 *
 *   5. OLD COPPER: a car scrapped leaves COPPER_A_CAR of copper, a demolished
 *      building COPPER_A_MATERIALS_UNIT a unit of its material, each at
 *      RECOVERY, in a pool of its own beside the steel's; a Scrap Copper
 *      Refinery buys it at PAY_SHARE of COPPER_SCRAP's export price, as much as
 *      its intake at the rate and no more than arose, pro rata, the rest to
 *      landfill; its bid for scrap the need past it; the households paid into
 *      their savings and a selling sector by its other revenue, on the
 *      refinery's input line; the audit closes.
 *
 *   6. THE PLANNER WEIGHS THE OLD COPPER: one more scrap refinery's scrap is
 *      the old copper past what those standing take, at what it pays, and the
 *      rest at the market's value; with none arisen, the market's alone, to
 *      the bit.
 *
 *   7. THE WIRE & CABLE WORKS' ESTIMATE (Manufacturing): the maker's less its
 *      insulation at the exchange factor and its freight; the sector's older
 *      rows the maker's less their city legs (since FR2, as every maker's), to
 *      the bit.
 *
 *   8. A WORKS BUYS INPUTS ONLY FOR WHAT IT MAKES (WN3): an Electrolytic
 *      Refinery whose shed stands at the dump line, its cathode under what a
 *      unit costs to make and to ship, orders no anode and makes no cathode,
 *      and every unit of input it did buy is made, held or written off, never
 *      lost; beside a smelter that makes less anode than the refineries want,
 *      the refineries that make nothing do not swallow the smelter's anode as
 *      captive, it is sold or shipped; a works whose month makes its whole
 *      rate orders what it always did; and the month's books lose nothing
 *      (the works' imports are matched by output made), so its GDP is not
 *      below what C + G alone would make it.
 */
public class CopperRefiningCheck {

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
        chain();
        acid();
        oldCopper();
        plannerWeighsOldCopper();
        wireWorksEstimate();
        idleWorks();
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. the sector ============================ */

    static void sector() {
        out.println("--- 1. the sector on the registry: a works on concentrate, anode, cathode and scrap; the wire works in Manufacturing ---");
        String[] keys = Sectors.KEYS;
        int at = java.util.Arrays.asList(keys).indexOf(Sectors.COPPER_REFINING);
        assertTrue("Sectors.KEYS has Copper Refining after Copper Mining, the twenty-second - wood's Forestry after it since 0.7.138",
                at == 21 && keys[at - 1].equals(Sectors.COPPER_MINING) && keys[at + 1].equals(Sectors.FORESTRY));
        Sectors s = new Sectors(new BuildingManager(), new Markets());
        CopperRefining cr = s.copperRefining();
        assertTrue("...a processing sector found by its saved name, Iron & Steel's group, making cathode, anode, acid and rod of"
                        + " concentrate, anode, cathode and scrap (and its vans, as every sector); cathode its planning good",
                s.byKey(Sectors.COPPER_REFINING) == cr && cr instanceof ProcessingSector && cr.group() == BuildingType.HEAVY_INDUSTRY
                        && java.util.Set.copyOf(cr.goodsMade()).equals(java.util.Set.of(Good.COPPER, Good.ANODE, Good.ACID, Good.COPPER_ROD))
                        && java.util.Set.copyOf(cr.goodsUsed()).equals(java.util.Set.of(Good.VANS, Good.COPPER_CONC, Good.ANODE, Good.COPPER,
                        Good.COPPER_SCRAP))
                        && cr.planningGood() == Good.COPPER);
        assertTrue("...anode and cathode captive - made and used inside it - and nothing else",
                cr.captive(Good.ANODE) && cr.captive(Good.COPPER) && !cr.captive(Good.ACID) && !cr.captive(Good.COPPER_ROD)
                        && !cr.captive(Good.COPPER_CONC) && !cr.captive(Good.COPPER_SCRAP));
        Manufacturing man = s.manufacturing();
        assertTrue("Manufacturing makes wire and cable of rod, appended after its own two goods and two inputs; its planning good still"
                        + " fabricated steel",
                man.isMaker(Good.WIRE_CABLE) && man.isUser(Good.COPPER_ROD)
                        && new java.util.ArrayList<>(man.goodsMade()).equals(java.util.List.of(Good.FABRICATED_STEEL, Good.MACHINERY, Good.WIRE_CABLE))
                        && new java.util.ArrayList<>(man.goodsUsed()).equals(java.util.List.of(Good.VANS, Good.STEEL, Good.LUBRICANTS, Good.COPPER_ROD))
                        && man.planningGood() == Good.FABRICATED_STEEL);
        java.util.Map<Good, java.util.List<String>> users = new java.util.EnumMap<>(Good.class), makers = new java.util.EnumMap<>(Good.class);
        for (Good g : CopperMiningCheck.GOODS) {
            java.util.List<String> u = new java.util.ArrayList<>(), m = new java.util.ArrayList<>();
            for (Sector sec : s.all()) {
                if (sec.isUser(g)) u.add(sec.key());
                if (sec.isMaker(g)) m.add(sec.key());
            }
            users.put(g, u);
            makers.put(g, m);
        }
        report("the buyers (spec 2.12): concentrate the smelters', cathode the mines' leach plants' and the refineries', the rod mills'"
                        + " own; acid the smelters' to the leach plants; scrap the scrap refineries'; rod to the wire works; wire and cable to"
                        + " nobody here - it ships until the construction upgrade",
                makers.get(Good.COPPER_CONC).equals(java.util.List.of(Sectors.COPPER_MINING))
                        && users.get(Good.COPPER_CONC).equals(java.util.List.of(Sectors.COPPER_REFINING))
                        && makers.get(Good.COPPER).equals(java.util.List.of(Sectors.COPPER_MINING, Sectors.COPPER_REFINING))
                        && users.get(Good.COPPER).equals(java.util.List.of(Sectors.COPPER_REFINING))
                        && makers.get(Good.ACID).equals(java.util.List.of(Sectors.COPPER_REFINING))
                        && users.get(Good.ACID).equals(java.util.List.of(Sectors.COPPER_MINING))
                        && makers.get(Good.COPPER_SCRAP).isEmpty() && users.get(Good.COPPER_SCRAP).equals(java.util.List.of(Sectors.COPPER_REFINING))
                        && users.get(Good.COPPER_ROD).equals(java.util.List.of(Sectors.MANUFACTURING))
                        && makers.get(Good.WIRE_CABLE).equals(java.util.List.of(Sectors.MANUFACTURING)) && users.get(Good.WIRE_CABLE).isEmpty()
                        && Good.WIRE_CABLE.exportable(),
                "users " + users);
    }

    /* ============================ 2. the five rows against the study ============================ */

    /** The rows, by their permanent ids, in the study's 4.3 order: Copper Refining's four and Manufacturing's wire works. */
    static final int[] ROWS = { CopperRefining.SMELTER, CopperRefining.ELECTROLYTIC_REFINERY, CopperRefining.SCRAP_REFINERY,
            CopperRefining.ROD_MILL, Manufacturing.WIRE_CABLE_WORKS };

    /** The study's 4.3: each one's cost, D$ thousands all in. */
    static final double[] STUDY_COSTS = { 500_000, 180_000, 280_000, 45_000, 110_000 };

    /** ...its posts. */
    static final int[] STUDY_POSTS = { 700, 200, 200, 80, 400 };

    /** ...its draw, kW. */
    static final int[] STUDY_KW = { 13_400, 6_000, 5_500, 1_100, 2_500 };

    /** ...what it makes a month, t. */
    static final Good[][] STUDY_MADE = { { Good.ANODE, Good.ACID }, { Good.COPPER }, { Good.COPPER }, { Good.COPPER_ROD }, { Good.WIRE_CABLE } };
    /** ...and how many tonnes of each, a month. */
    static final double[][] STUDY_MAKES = { { 9_750, 29_700 }, { 9_700 }, { 4_000 }, { 8_000 }, { 2_000 } };

    /** ...and what it takes, t a month. */
    static final Good[] STUDY_FEED = { Good.COPPER_CONC, Good.ANODE, Good.COPPER_SCRAP, Good.COPPER, Good.COPPER_ROD };
    /** ...and how many tonnes of it, a month. */
    static final double[] STUDY_USES = { 33_000, 9_750, 4_400, 8_016, 2_000 };

    /** The study's 4.4 table, % of the cost a month, cases A to D. */
    static final double[][] STUDY_TABLE = { { -0.3, 1.2, 2.7, 1.2 }, { -1.9, 0.8, 3.5, 0.8 }, { 0.0, 2.2, 2.9, 0.7 },
            { -6.7, 2.2, 11.1, 2.2 }, { -0.3, 0.6, 7.9, 7.0 } };

    /** Each row's running cost a month, D$ thousands, backed out of its 4.4 table (its 4.3 gives none): the middle of the range on which all four of its returns round to the table - but the scrap refinery's, from the -0.02% the study states for its case A. */
    static final double[] STUDY_RUNS = { 5_253, 1_460, 1_776, 846, 4_820 };

    /** The scrap refinery's case A as the study states it (its 4.4: "imported scrap just breaks even (case A, -0.02%)"), and that figure's rounding. */
    static final double STUDY_SCRAP_A = -0.02, STUDY_SCRAP_A_ROUNDING = .005;

    /** The study's rounding: its returns to a tenth of a point. */
    static final double STUDY_ROUNDING = .05;

    static void rows() {
        out.println("\n--- 2. the five rows at parity: the study's 4.3, and its 4.4 table's four cases to its rounding ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        UtilitiesHandler founding = new UtilitiesHandler();
        double tariff = founding.getPricerPerWatt(), water = founding.getPricePerWaterUnit();
        java.util.function.ToDoubleFunction<Good> imp = Good::worldImportPrice, exp = Good::worldExportPrice;
        BuildingsTemplate foundry = b.getTemplate(ham.citybuildersim.sectors.HeavyIndustry.ARC_FURNACE),
                shop = b.getTemplateByName("Fabrication Shop");
        double foundryCost = foundry.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * foundry.getConstructionMaterials();
        double shopCost = shop.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * shop.getConstructionMaterials();
        String[] cases = { "A", "B", "C", "D" };
        double[][] pct = new double[ROWS.length][4];
        for (int r = 0; r < ROWS.length; r++) {
            BuildingsTemplate t = b.getTemplate(ROWS[r]);
            boolean wire = r == 4;
            double cost = t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials();
            boolean makes = t.goodsMade().size() == STUDY_MADE[r].length;
            for (int i = 0; i < STUDY_MADE[r].length; i++) makes &= t.makes(STUDY_MADE[r][i]) == STUDY_MAKES[r][i];
            String sector = wire ? Sectors.MANUFACTURING : Sectors.COPPER_REFINING;
            report("the " + t.getName() + " (" + t.getId() + ") is the study's: D$" + String.format("%,.0f", STUDY_COSTS[r]) + "k all in, "
                            + STUDY_POSTS[r] + " posts, " + String.format("%,d", STUDY_KW[r]) + " kW, its 4.3 makes of "
                            + String.format("%,.0f", STUDY_USES[r]) + " t of " + STUDY_FEED[r].label().toLowerCase() + "; " + sector
                            + "'s, HEAVY_INDUSTRY, dated 1900, on no site",
                    cost == STUDY_COSTS[r] && t.getTotalJobs() == STUDY_POSTS[r] && t.getElectricityConsumption() == STUDY_KW[r] && makes
                            && t.uses(STUDY_FEED[r]) == STUDY_USES[r] && t.goodsUsed().size() == 1 && sector.equals(t.getSector())
                            && t.getCategory() == BuildingType.HEAVY_INDUSTRY && t.getSince() == 1900 && t.site() == null && t.getGen() == 0,
                    String.format("cash %,.0f + 18 x %,d material", t.getCashCost(), t.getConstructionMaterials()));
            double tonnes = STUDY_USES[r];
            for (double m : STUDY_MAKES[r]) tonnes += m;
            boolean cathode = t.makes(Good.COPPER) > 0;
            double sqFt = wire ? STUDY_COSTS[r] * shop.getLandSqFt() / shopCost : STUDY_COSTS[r] * foundry.getLandSqFt() / foundryCost;
            report("...its ground " + (wire ? "the Fabrication Shop's" : "the Steel Foundry's") + " per D$ of cost, its road load its tonnes in and"
                            + " out at the Foundry's 110 for 2,520 t, and " + (cathode ? "three months of cathode in its yard" : "no yard"),
                    Math.abs(t.getLandSqFt() - sqFt) <= .5 && t.getRoadLoad() == Math.round(tonnes * foundry.getRoadLoad() / 2_520)
                            && t.getStock() == (cathode ? 3 * t.makes(Good.COPPER) : 0),
                    String.format("%,.0f sq ft, road %,.0f, stock %,.0f", t.getLandSqFt(), t.getRoadLoad(), t.getStock()));
            double wages = 0;
            for (JobType j : JobType.values()) wages += t.getJobs(j) * PayTier.wageOf(j);
            double running = wages + t.getElectricityConsumption() * tariff + t.getWaterConsumption() * water;
            report("its consumables are its running cost less its wages, power and water" + (wire ? " - the cable's insulation" : ""),
                    Math.abs(t.getConsumables() - (STUDY_RUNS[r] - running)) < 1e-9,
                    String.format("%,.2f = %,.0f - %,.2f", t.getConsumables(), STUDY_RUNS[r], running));
            boolean all = true;
            StringBuilder got = new StringBuilder();
            for (SpreadPlanner.Case c : SpreadPlanner.Case.values()) {
                pct[r][c.ordinal()] = 100 * SpreadPlanner.caseEarns(t, c, imp, exp, running, 0, 1, tariff) / cost;
                all &= Math.abs(pct[r][c.ordinal()] - STUDY_TABLE[r][c.ordinal()]) <= STUDY_ROUNDING + 1e-9;
                got.append(String.format("%s %.3f%%  ", cases[c.ordinal()], pct[r][c.ordinal()]));
            }
            report("...its four cases are the study's 4.4 to its rounding: " + java.util.Arrays.toString(STUDY_TABLE[r]) + "%", all,
                    got.toString().trim());
        }
        int a = SpreadPlanner.Case.A.ordinal(), bb = SpreadPlanner.Case.B.ordinal(), cc = SpreadPlanner.Case.C.ordinal();
        report("the study's 4.4: the smelter loses on imported concentrate (case A), so the rule never builds one without a mine - on the"
                        + " city's own concentrate it earns (B)", pct[0][a] < 0 && pct[0][bb] > 0,
                String.format("A %.3f%%, B %.3f%%", pct[0][a], pct[0][bb]));
        report("...imported scrap just breaks even: the scrap refinery's case A the study's -0.02%", Math.abs(pct[2][a] - STUDY_SCRAP_A)
                <= STUDY_SCRAP_A_ROUNDING, String.format("%.4f%%", pct[2][a]));
        report("...the rod mill's 11% in case C, a thin premium on a big tonnage; the wire works built to replace imports (C) far past"
                        + " exporting (B)",
                Math.abs(pct[3][cc] - 11.1) <= STUDY_ROUNDING && pct[4][cc] > 10 * pct[4][bb] && pct[4][bb] > 0,
                String.format("rod C %.3f%%; wire C %.3f%%, B %.3f%%", pct[3][cc], pct[4][cc], pct[4][bb]));
        BuildingsTemplate scrap = b.getTemplate(CopperRefining.SCRAP_REFINERY);
        java.util.Map<Integer, java.util.Map<Good, Double>> takers = new java.util.TreeMap<>();
        for (BuildingsTemplate t : b.getTemplates()) if (!t.oldMetalTaken().isEmpty()) takers.put(t.getId(), t.oldMetalTaken());
        report("the scrap refinery takes the city's old copper, its whole feed - oldMetal COPPER 4,400 t a month, its scrap's tonnes; it and"
                        + " the Scrap Yard the only takers of old metal",
                scrap.oldMetal(Good.COPPER) == scrap.uses(Good.COPPER_SCRAP) && scrap.oldMetal(Good.STEEL) == 0
                        && takers.keySet().equals(java.util.Set.of(ham.citybuildersim.sectors.HeavyIndustry.SCRAP_YARD, CopperRefining.SCRAP_REFINERY)),
                takers.toString());
        BuildingsTemplate smelter = b.getTemplate(CopperRefining.SMELTER);
        assertTrue("the heavy plants' posts 54/35/9/2 by largest remainder: the smelter's 700 are 378/245/63/14",
                smelter.getJobs(JobType.NO_DIPLOMA) == 378 && smelter.getJobs(JobType.DIPLOMA) == 245
                        && smelter.getJobs(JobType.COLLEGE_ENGINEERING) == 63 && smelter.getJobs(JobType.UNIV_SCIENCE) == 14);
    }

    /* ============================ 3. the chain by its ratios ============================ */

    /** The acid a smelter makes a tonne of concentrate: 0.9 t (the study's 4.1, [K27]). */
    static final double ACID_A_TONNE = .9;

    static void chain() {
        out.println("\n--- 3. the chain by its ratios: concentrate to anode to cathode to rod to wire, anode and cathode captive ---");
        BuildingManager tb = new BuildingManager();
        tb.initializeTemplates();
        BuildingsTemplate smelter = tb.getTemplate(CopperRefining.SMELTER), refinery = tb.getTemplate(CopperRefining.ELECTROLYTIC_REFINERY),
                rod = tb.getTemplate(CopperRefining.ROD_MILL), wire = tb.getTemplate(Manufacturing.WIRE_CABLE_WORKS);
        assertTrue("the rows' ratios: 33,000 t of concentrate into 9,750 t of anode and 0.9 t of acid a tonne; the anode into 9,700 t of"
                        + " cathode; 8,016 t of cathode into 8,000 t of rod; 2,000 t of rod into 2,000 t of wire and cable",
                smelter.makes(Good.ACID) == ACID_A_TONNE * smelter.uses(Good.COPPER_CONC) && refinery.uses(Good.ANODE) == smelter.makes(Good.ANODE)
                        && refinery.makes(Good.COPPER) == 9_700 && rod.uses(Good.COPPER) == 8_016 && rod.makes(Good.COPPER_ROD) == 8_000
                        && wire.uses(Good.COPPER_ROD) == wire.makes(Good.WIRE_CABLE));
        Game g = CopperMiningCheck.copperTown("copperrefiningcheck-chain", 0);
        BuildingManager b = g.getBuildingManager();
        CopperRefining cr = g.getSectors().copperRefining();
        Manufacturing man = g.getSectors().manufacturing();
        quietly(() -> {
            b.addStack(b.getTemplate(CopperRefining.SMELTER), 1, true);
            b.addStack(b.getTemplate(CopperRefining.ELECTROLYTIC_REFINERY), 1, true);
            b.addStack(b.getTemplate(CopperRefining.ROD_MILL), 1, true);
            b.addStack(b.getTemplate(Manufacturing.WIRE_CABLE_WORKS), 1, true);
        });
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(2));
        Sector.Output anode = cr.outputRow(Good.ANODE), cathode = cr.outputRow(Good.COPPER), rods = cr.outputRow(Good.COPPER_ROD);
        // The month's rate is the sector's, one for all its works: so the chain is read by its ratios, each made at that one rate.
        double r = anode == null ? 0 : anode.captive / 9_750;
        report("fixture: a smelter, a refinery and a rod mill standing beside a wire works, two months on: the smelter's anode all to the"
                        + " refinery, captive - the smelter's 9,750 t and the refinery's 9,750 at the sector's rate - none to the market",
                r > 0 && anode.produced < 1e-6 && cr.inputRow(Good.ANODE) != null
                        && cr.inputRow(Good.ANODE).bid <= .05 * cr.inputRow(Good.ANODE).needed,
                anode == null ? "none" : String.format("rate %.4f: %,.1f t captive, %,.1f t to market", r, anode.captive, anode.produced));
        // ...cathode is stockable, made into the yard after the clearing (Markets' step 4), at the rate the sector runs at by then.
        double r4 = cathode == null ? 0 : cathode.captive / 8_016;
        report("...the refinery's cathode to the rod mill first, captive: min of its 9,700 t and the mill's 8,016 at the rate; the rest to"
                        + " its yard or abroad",
                cathode != null && r4 > 0 && Math.abs(cathode.planned + cathode.exportBound + cathode.idled - (9_700 - 8_016) * r4) < 1e-6,
                cathode == null ? "none" : String.format("%,.1f t captive, %,.1f made for the market", cathode.captive,
                        cathode.planned + cathode.exportBound + cathode.idled));
        Sector.Input rodIn = man.inputRow(Good.COPPER_ROD);
        report("...the rod mill's 8,000 t of rod at the rate to the market: what the wire works bid sold at home, the rest shipped",
                rods != null && rodIn != null && Math.abs(rods.produced - 8_000 * r) < 1e-6 && rodIn.bid > 0
                        && Math.abs(rods.soldLocal - Math.min(rods.produced, rodIn.bid)) < 1e-6 && Math.abs(rods.exported - (rods.produced - rods.soldLocal)) < 1e-6,
                rods == null ? "none" : String.format("%,.1f t made, %,.1f sold at home, %,.1f shipped", rods.produced, rods.soldLocal, rods.exported));
        Sector.Output wires = man.outputRow(Good.WIRE_CABLE);
        report("...and the wire works' wire and cable, a tonne a tonne of rod, nobody here buying it, all shipped",
                wires != null && wires.produced > 0 && wires.soldLocal == 0 && Math.abs(wires.exported - wires.produced) < 1e-6,
                wires == null ? "none" : String.format("%,.1f t made, %,.1f shipped", wires.produced, wires.exported));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("the month's money audit closes", audit != null && audit.relative() < 1e-4,
                audit == null ? "none" : String.format("residual %.3g of what moved", audit.relative()));
    }

    /* ============================ 4. the smelter's acid sold, the leach plant's bought ============================ */

    static void acid() {
        out.println("\n--- 4. the smelter's acid sold, the leach plant's bought at home, the rest shipped ---");
        Game g = CopperMiningCheck.copperTown("copperrefiningcheck-acid", 0);
        BuildingManager b = g.getBuildingManager();
        CopperRefining cr = g.getSectors().copperRefining();
        CopperMining cm = g.getSectors().copperMining();
        quietly(() -> {
            b.addStack(b.getTemplate(CopperRefining.SMELTER), 1, true);
            b.addStack(b.getTemplate(CopperMining.LEACH_PLANT), 1, true);
        });
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(2));
        Sector.Output acid = cr.outputRow(Good.ACID), anode = cr.outputRow(Good.ANODE);
        Sector.Input wants = cm.inputRow(Good.ACID);
        double rate = anode == null ? 0 : anode.produced / 9_750;
        report("fixture: a smelter beside a leach plant, two months on: the smelter made 29,700 t of acid to its 9,750 of anode, the"
                        + " leach plant's bid sold to it at home, the rest shipped",
                rate > 0 && acid != null && wants != null && wants.bid > 0 && Math.abs(acid.produced - 29_700 * rate) < 1e-6
                        && Math.abs(acid.soldLocal - Math.min(acid.produced, wants.bid)) < 1e-6
                        && Math.abs(acid.exported - (acid.produced - acid.soldLocal)) < 1e-6,
                acid == null ? "none" : String.format("rate %.4f: %,.1f t made, %,.1f sold at home, %,.1f shipped", rate, acid.produced,
                        acid.soldLocal, acid.exported));
        quietly(() -> g.simulateMonths(1));
        Sector.Split bought = cm.statement().bought.get(Good.ACID), sold = cr.statement().sold.get(Good.ACID);
        report("...on the books (struck at the next month's top): Copper Mining bought acid at home, Copper Refining sold it at home and"
                        + " abroad",
                bought != null && bought.atHome > 0 && bought.abroad == 0 && sold != null && sold.atHome > 0 && sold.abroad > 0,
                bought == null || sold == null ? "none" : String.format("bought D$%,.1fk; sold D$%,.1fk at home, D$%,.1fk abroad",
                        bought.atHome, sold.atHome, sold.abroad));
    }

    /* ============================ 5. old copper ============================ */

    static void oldCopper() {
        out.println("\n--- 5. the city's old copper: from cars and demolitions, bought by a scrap refinery at 70% of scrap's export, the rest landfill ---");
        OldMetal pool = new OldMetal();
        BuildingManager tb = new BuildingManager();
        tb.initializeTemplates();
        BuildingsTemplate house = tb.getTemplateByName("House");
        pool.carsScrapped(1_000);
        double cars = pool.copper().available();
        pool.demolished(house, 2, Sectors.REAL_ESTATE);
        double fromHouses = pool.copper().available() - cars;
        report("a thousand cars scrapped leave 23 kg of copper each at 85%, and two Houses pulled down 0.1 t a unit of their material at"
                        + " 85%, the owner's to sell, in the copper's pool",
                Math.abs(cars - 1_000 * OldMetal.COPPER_A_CAR * OldMetal.RECOVERY) < 1e-9
                        && Math.abs(fromHouses - 2 * house.getConstructionMaterials() * OldMetal.COPPER_A_MATERIALS_UNIT * OldMetal.RECOVERY) < 1e-9
                        && pool.copper().availableFrom(Trade.HOUSEHOLDS) == cars
                        && Math.abs(pool.copper().availableFrom(Sectors.REAL_ESTATE) - fromHouses) < 1e-9,
                String.format("%,.2f t from cars, %,.2f t from the houses", cars, fromHouses));
        assertTrue("...beside the steel's pool, which is as it was: 0.9 t a car and 0.3 t a unit, at 85%",
                Math.abs(pool.available() - (1_000 * OldMetal.STEEL_A_CAR + 2 * house.getConstructionMaterials() * OldMetal.STEEL_A_MATERIALS_UNIT)
                        * OldMetal.RECOVERY) < 1e-9 && pool.steel().available() == pool.available());
        OldMetal back = new OldMetal();
        back.restore(pool.state());
        OldMetal.State older = pool.state();
        older.copper = null;
        OldMetal before = new OldMetal();
        before.restore(older);
        assertTrue("its state carries the copper beside the steel, and reads back the same; a state with none (a save from before 0.7.133)"
                        + " reads none",
                back.copper().available() == cars + fromHouses && back.available() == pool.available()
                        && before.copper().available() == 0 && before.available() == pool.available());

        // A town with a Scrap Copper Refinery, handed old copper from the households and a sector.
        Game g = CopperMiningCheck.copperTown("copperrefiningcheck-oldcopper", 0);
        BuildingManager b = g.getBuildingManager();
        CopperRefining cr = g.getSectors().copperRefining();
        quietly(() -> b.addStack(b.getTemplate(CopperRefining.SCRAP_REFINERY), 1, true));
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(1));
        double handed = 6_000;
        g.getOldMetal().carsScrapped(handed / 2 / OldMetal.COPPER_A_CAR / OldMetal.RECOVERY);
        g.getOldMetal().copper().arise(OldMetal.Source.DEMOLITIONS, Sectors.MANUFACTURING, handed / 2 / OldMetal.RECOVERY);
        quietly(() -> g.simulateMonths(1));
        OldMetal.Pool cu = g.getOldMetal().copper();
        Sector.Input scrap = cr.input(Good.COPPER_SCRAP);
        double price = OldMetal.PAY_SHARE * g.getMarkets().get(Good.COPPER_SCRAP).exportPrice();
        report("fixture: a Scrap Copper Refinery standing, 6,000 t of old copper handed it - half the households' cars', half a sector's:"
                        + " it bought its intake at the rate the month's flow goods were made at, the rest to landfill",
                cu.lastArisen() >= handed - 1e-6 && cu.lastBought() > 0 && cu.lastBought() <= scrap.needed + 1e-6
                        && cu.lastBought() >= .95 * scrap.needed && Math.abs(cu.lastLandfill() - (cu.lastArisen() - cu.lastBought())) < 1e-6,
                String.format("needed %,.1f t: %,.1f t arose, %,.1f bought, %,.1f to landfill", scrap.needed, cu.lastArisen(), cu.lastBought(),
                        cu.lastLandfill()));
        report("...at PAY_SHARE of COPPER_SCRAP's export price, from each seller pro rata",
                Math.abs(cu.lastPrice() - price) < 1e-9 && Math.abs(cu.lastPaidTotal() - cu.lastBought() * price) < 1e-6
                        && cu.lastPaid().containsKey(Trade.HOUSEHOLDS) && cu.lastPaid().containsKey(Sectors.MANUFACTURING),
                String.format("D$%.4fk a tonne, D$%,.1fk paid: %s", cu.lastPrice(), cu.lastPaidTotal(), cu.lastPaid()));
        report("...and its bid for scrap its need at the clearing past the old copper it took - next to none, the old copper covering"
                        + " it all but what its vans' arrival added to its rate since",
                Math.abs(scrap.bid - Math.max(0, scrap.needed - cu.lastBought())) < 1e-6 && scrap.bid < .05 * scrap.needed,
                String.format("bid %,.1f t of %,.1f needed", scrap.bid, scrap.needed));
        quietly(() -> g.simulateMonths(1));
        Sector.Statement st = cr.statement();
        double line = st.otherInputs.getOrDefault(OldMetal.LINE, 0.0), households = st.purchasesBySupplier.getOrDefault(Trade.HOUSEHOLDS, 0.0);
        double sold = g.getSectors().manufacturing().statement().otherParts.getOrDefault(OldMetal.SOLD, 0.0);
        report("...on the books at the next strike: its input line \"Old metal\" what it paid, the households' part billed by them, the"
                        + " sector's sale its other revenue",
                line > 0 && households > 0 && sold > 0 && Math.abs(line - households - sold) < .01,
                String.format("D$%,.1fk: households D$%,.1fk, Manufacturing D$%,.1fk", line, households, sold));
        // ...and a month with less old copper than it takes: all of it bought, the rest of its need bid on the market.
        g.getOldMetal().carsScrapped(500 / OldMetal.COPPER_A_CAR / OldMetal.RECOVERY);
        quietly(() -> g.simulateMonths(1));
        Sector.Input scrap2 = cr.input(Good.COPPER_SCRAP);
        report("with less old copper than its intake it buys all of it, nothing to landfill, and bids for the rest of its scrap",
                cu.lastLandfill() == 0 && cu.lastBought() == cu.lastArisen() && cu.lastArisen() < scrap2.needed
                        && Math.abs(scrap2.bid - (scrap2.needed - cu.lastBought())) < 1e-6,
                String.format("%,.1f t bought, %,.1f t bid of %,.1f needed", cu.lastBought(), scrap2.bid, scrap2.needed));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("the month's money audit closes (MoneyCheck holds its old-metal lines over two years)", audit != null && audit.relative() < 1e-4,
                audit == null ? "none" : String.format("residual %.3g of what moved", audit.relative()));
        // With no refinery standing it all goes to landfill.
        Game none = CopperMiningCheck.copperTown("copperrefiningcheck-landfill", 0);
        none.getOldMetal().carsScrapped(1_000 / OldMetal.COPPER_A_CAR / OldMetal.RECOVERY);
        quietly(() -> none.simulateMonths(1));
        OldMetal.Pool nc = none.getOldMetal().copper();
        assertTrue("...and with no scrap refinery standing the month's old copper all goes to landfill, nobody paid",
                nc.lastArisen() >= 1_000 - 1e-6 && nc.lastBought() == 0 && nc.lastLandfill() == nc.lastArisen() && nc.lastPaidTotal() == 0);
    }

    /* ============================ 6. the planner weighs the old copper ============================ */

    static void plannerWeighsOldCopper() {
        out.println("\n--- 6. the planner weighs one more scrap refinery on the old copper past what those standing take ---");
        Game g = CopperMiningCheck.copperTown("copperrefiningcheck-weigh", 0);
        CopperRefining cr = g.getSectors().copperRefining();
        BuildingManager b = g.getBuildingManager();
        MaterialsCheck.handWorkers(g);
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate t = b.getTemplate(CopperRefining.SCRAP_REFINERY);
        quietly(() -> g.simulateMonths(1));
        SpreadPlanner.Values plain = SpreadPlanner.values(g.getMarkets());
        double none = cr.appraise(t, plans).earns();
        report("fixture: a town, its own few cars' old copper: one more refinery weighed on it and the market's scrap",
                cr.spareOldCopper() == g.getOldMetal().copper().lastArisen() && none == weigh(cr, t, oldValues(plain, cr.spareOldCopper(),
                        cr.oldCopperPrice()), plans, g).earns(),
                String.format("%,.2f t arose: D$%,.1fk a month", g.getOldMetal().copper().lastArisen(), none));
        g.getOldMetal().carsScrapped(3_000 / OldMetal.COPPER_A_CAR / OldMetal.RECOVERY);
        quietly(() -> g.simulateMonths(1));
        double spare = cr.spareOldCopper(), price = cr.oldCopperPrice();
        plain = SpreadPlanner.values(g.getMarkets());
        double earns = cr.appraise(t, plans).earns();
        double byHand = weigh(cr, t, oldValues(plain, spare, price), plans, g).earns(), market = weigh(cr, t, plain, plans, g).earns();
        report("3,000 t of old copper arisen, no refinery standing: one more weighs it at PAY_SHARE of scrap's export price and the rest"
                        + " of its scrap at the market's, to the bit - more than on the market's scrap alone",
                spare >= 3_000 - 1e-6 && earns == byHand && earns > market,
                String.format("%,.1f t spare at D$%.4fk: D$%,.1fk a month, D$%,.1fk on the market's", spare, price, earns, market));
        quietly(() -> b.addStack(t, 1, true));
        assertTrue("...with one standing it takes its 4,400 t first: one more is weighed on what is left past them, the last on what"
                        + " the others leave it",
                cr.spareOldCopper() == Math.max(0, g.getOldMetal().copper().lastArisen() - 4_400)
                        && cr.leftForTheLast() == Math.min(4_400, g.getOldMetal().copper().lastArisen()));
    }

    /** The planner's values with `spare` t of old copper at `price` in front of the market's scrap (CopperRefining, THE OLD COPPER), built by hand. */
    static SpreadPlanner.Values oldValues(SpreadPlanner.Values plain, double spare, double price) {
        return new SpreadPlanner.Values() {
            @Override public double out(Good gd, double u) { return plain.out(gd, u); }
            @Override public double in(Good gd, double u) {
                if (gd != Good.COPPER_SCRAP || !(spare > 0) || !(price > 0)) return plain.in(gd, u);
                double old = Math.min(u, spare);
                return old * price + plain.in(gd, u - old);
            }
        };
    }

    /** One works of `cr` weighed at `v`, as ProcessingSector weighs it: its freight in its earnings and its vans in its cost. */
    static SpreadPlanner.Candidate weigh(CopperRefining cr, BuildingsTemplate t, SpreadPlanner.Values v, BusinessInvestment plans, Game g) {
        SpreadPlanner.City city = cr.planner().city(cr, plans, g);
        return ProcessingSector.estimate(t, v, city, cr.exchangeFactor(), cr.tariff(), null, 1,
                ProcessingSector.freightRunning(t, v, city.rate(), cr.legPrice()), ProcessingSector.fleetCapital(t, v));
    }

    /* ============================ 7. the wire works' estimate ============================ */

    static void wireWorksEstimate() {
        out.println("\n--- 7. the Wire & Cable Works weighed with its insulation and its freight; Manufacturing's older rows with their legs, as every maker ---");
        Game g = CopperMiningCheck.copperTown("copperrefiningcheck-wire", 0);
        Manufacturing man = g.getSectors().manufacturing();
        BuildingManager b = g.getBuildingManager();
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(1));
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate wire = b.getTemplate(Manufacturing.WIRE_CABLE_WORKS), shop = b.getTemplateByName("Fabrication Shop");
        double rate = BusinessInvestment.operatingRateOf(man.getOperatingRate());
        double factor = g.getMarkets().getExchangeRate(), tariff = g.getEconomyManager().getPricePerWatt();
        double expect = plans.estimatedMakerProfit(man, wire) - SpreadPlanner.ownCosts(wire, rate, factor, tariff)
                - ProcessingSector.freightRunning(wire, SpreadPlanner.values(g.getMarkets()), rate, man.legPrice());
        double got = man.estimatedMonthlyProfit(wire, plans);
        report("the wire works' estimate: the maker's, less its insulation (its consumables) at the exchange factor and its freight, to the"
                        + " bit - what the plan and Game.consider() read",
                got == expect && SpreadPlanner.ownCosts(wire, rate, factor, tariff) > 0,
                String.format("D$%,.1fk a month; the maker's alone D$%,.1fk", got, plans.estimatedMakerProfit(man, wire)));
        // ...weighed with its city legs as every maker is since FR2's gate round (Sector.estimatedMonthlyProfit()).
        double legs = ProcessingSector.cityLegs(shop, rate, man.legPrice());
        report("...and a Fabrication Shop's, which buys no consumables, the maker's estimate less its city legs, to the bit",
                shop.getConsumables() == 0 && legs > 0
                        && man.estimatedMonthlyProfit(shop, plans) == plans.estimatedMakerProfit(man, shop) - legs,
                String.format("D$%,.1fk a month; its legs D$%,.1fk", man.estimatedMonthlyProfit(shop, plans), legs));
    }

    /* ============================ 8. a works buys inputs only for what it makes ============================ */

    /**
     * A copper town on the goods' literals (Game.playOnLiterals(): the premise is a fixed world price) with the works standing and, where the
     * cathode's export price is given, that price set on its market (nothing resets it on a city that plays on the literals), two months settled.
     */
    static Game copperWorks(String label, int smelters, int refineries, int rodMills, double cathodeExport) {
        Game g = CopperMiningCheck.copperTown(label, 0);
        g.playOnLiterals();
        if (Double.isFinite(cathodeExport)) g.getMarkets().get(Good.COPPER).setWorldPrices(Good.COPPER.worldImportPrice(), cathodeExport);
        BuildingManager b = g.getBuildingManager();
        quietly(() -> {
            if (smelters > 0) b.addStack(b.getTemplate(CopperRefining.SMELTER), smelters, true);
            if (refineries > 0) b.addStack(b.getTemplate(CopperRefining.ELECTROLYTIC_REFINERY), refineries, true);
            if (rodMills > 0) b.addStack(b.getTemplate(CopperRefining.ROD_MILL), rodMills, true);
        });
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(2));
        return g;
    }

    /** One month with the cathode shed put back at the dump line first: the condition caused again each month, not waited for. */
    static void holdAndRun(Game g, CopperRefining cr) {
        cr.setStock(Good.COPPER, cr.getStockCapacity(Good.COPPER) * Sector.DUMP_THRESHOLD);
        quietly(() -> g.simulateMonths(1));
    }

    /** Whether a number is within a relative hair of another (a month's figures are sums of products). */
    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static void idleWorks() {
        out.println("\n--- 8. a works buys inputs only for what it makes: the idle refinery orders no anode, and does not swallow its smelter's ---");
        BuildingManager tb = new BuildingManager();
        tb.initializeTemplates();
        BuildingsTemplate refinery = tb.getTemplate(CopperRefining.ELECTROLYTIC_REFINERY);
        double anodePerCathode = refinery.uses(Good.ANODE) / refinery.makes(Good.COPPER);

        // (a) the found case: a refinery on imported anode, its shed full to the dump line, its cathode under what a unit costs to make and to ship.
        Game g = copperWorks("copperrefiningcheck-idle", 0, 1, 0, Double.NaN);
        CopperRefining cr = g.getSectors().copperRefining();
        double pantryBefore = cr.getPantry(Good.ANODE);
        holdAndRun(g, cr);
        Sector.Output cu = cr.outputRow(Good.COPPER);
        Sector.Input anodeIn = cr.inputRow(Good.ANODE);
        GoodsMarket cathode = g.getMarkets().get(Good.COPPER);
        double marginal = cr.getMarginalCostPerUnit(Good.COPPER);
        double dumpLine = cr.getStockCapacity(Good.COPPER) * Sector.DUMP_THRESHOLD;
        report("fixture (a): an Electrolytic Refinery on imported anode, the shed held at the dump line: the whole shed withheld, none offered,"
                        + " the cathode's price and its net export price both under what a unit costs to make",
                cr.getOperatingRate() > 0 && cu != null && anodeIn != null && near(cu.withheld, dumpLine) && cu.offered == 0
                        && cathode.getLocalPrice() < marginal && cathode.netExportPrice() < marginal,
                cu == null ? "none" : String.format("rate %.4f; withheld %,.0f of %,.0f t; price %.4f, net export %.4f, marginal cost %.4f",
                        cr.getOperatingRate(), cu.withheld, dumpLine, cathode.getLocalPrice(), cathode.netExportPrice(), marginal));
        double received = anodeIn == null ? 0 : anodeIn.boughtLocal + anodeIn.imported;
        double made = cu == null ? 0 : cu.produced + cu.exportBound + cu.captive;
        double used = made * anodePerCathode;
        double pantry = cr.getPantry(Good.ANODE) - pantryBefore;
        report("(a) every unit of anode it bought this month was made into cathode (which the yard then holds or writes off) or went to a pantry,"
                        + " none was lost",
                near(received, used + pantry),
                String.format("bought %,.1f t; made %,.1f t of cathode (%,.1f t of anode); pantry %+,.1f t", received, made, used, pantry));
        report("...and in this case it orders no anode and makes no cathode",
                anodeIn != null && cu != null && anodeIn.bid == 0 && received == 0 && cu.produced == 0 && cu.exportBound == 0,
                anodeIn == null || cu == null ? "none" : String.format("ordered %,.1f t of anode of its %,.1f; made %,.1f t of cathode, idled %,.1f",
                        anodeIn.bid, anodeIn.needed, cu.produced + cu.exportBound, cu.idled));

        // (d) the books, struck a month on (a statement describes the month before): the same shed held once more, then the accounts read.
        double madeThen = made, marginalThen = marginal;
        holdAndRun(g, cr);
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        double gdp = g.getEconomyManager().getMonthGdp();
        double consumables = cr.statement().otherInputs.getOrDefault(Sector.CONSUMABLES, 0.0);
        double inputImports = cr.statement().imports - consumables;
        double rest = na.getTotalImports() - inputImports;
        report("(d) the books lose nothing: what the works' input imports came to in the accounts is matched by the cathode it made, at no more"
                        + " than a unit costs to make",
                inputImports <= madeThen * marginalThen + 1e-6,
                String.format("input imports D$%,.1f; made %,.1f t at D$%.4f; consumables D$%,.1f", inputImports, madeThen, marginalThen, consumables));
        report("...so the month's GDP (C + Iconstr + Istock + G + NX) is not below what C + G alone would make it, less the rest of the imports line",
                gdp + rest >= na.getConsumption() + na.getGovernment() - 1e-6,
                String.format("GDP D$%,.1f; C D$%,.1f + G D$%,.1f; Istock D$%,.1f; the rest of the imports D$%,.1f", gdp, na.getConsumption(),
                        na.getGovernment(), na.getInvestmentInventories(), rest));

        // (b) the shared pool: one smelter, two refineries (the smelter's anode covers half their need), the cathode's export price low enough
        // that the refineries' marginal cost, the anode they must buy included, is over it.
        Game pool = copperWorks("copperrefiningcheck-pool", 1, 2, 0, 3.0);
        CopperRefining pr = pool.getSectors().copperRefining();
        holdAndRun(pool, pr);
        Sector.Output pcu = pr.outputRow(Good.COPPER), panode = pr.outputRow(Good.ANODE);
        GoodsMarket pcathode = pool.getMarkets().get(Good.COPPER);
        double pmarginal = pr.getMarginalCostPerUnit(Good.COPPER);
        report("fixture (b): a smelter and two refineries, the shed held at the dump line, the cathode's price and net export price under"
                        + " their marginal cost: the refineries make nothing",
                pr.getOperatingRate() > 0 && pcu != null && panode != null && pcu.offered == 0 && pcu.produced == 0 && pcu.exportBound == 0
                        && pcathode.getLocalPrice() < pmarginal && pcathode.netExportPrice() < pmarginal,
                pcu == null ? "none" : String.format("rate %.4f; made %,.1f t; price %.4f, net export %.4f, marginal cost %.4f",
                        pr.getOperatingRate(), pcu.produced + pcu.exportBound, pcathode.getLocalPrice(), pcathode.netExportPrice(), pmarginal));
        double smelted = panode == null ? 0 : panode.captive + panode.produced;
        double pused = pcu == null ? 0 : (pcu.produced + pcu.exportBound + pcu.captive) * anodePerCathode;
        double disposed = panode == null ? 0 : panode.soldLocal + panode.exported;
        report("(b) the smelter's anode is not swallowed as captive by refineries that make nothing: what it smelted was used by the cathode"
                        + " made, sold here or shipped",
                panode != null && near(smelted, pused + disposed) && panode.captive <= pused,
                String.format("smelted %,.1f t (%,.1f kept, %,.1f to market); used %,.1f t; sold %,.1f t, shipped %,.1f t", smelted,
                        panode == null ? 0 : panode.captive, panode == null ? 0 : panode.produced, pused,
                        panode == null ? 0 : panode.soldLocal, panode == null ? 0 : panode.exported));
        Sector.Input pin = pr.inputRow(Good.ANODE);
        double pbought = pin == null ? 0 : pin.boughtLocal + pin.imported;
        report("...and the anode it bought from the world is likewise used, none of it lost",
                pin != null && near(pbought, pused),
                String.format("bought %,.1f t; used %,.1f t", pbought, pused));

        // (c) a month that makes its whole rate is unchanged: the order is nameplate at the rate, less what is captive.
        Game full = copperWorks("copperrefiningcheck-whole", 0, 1, 0, Double.NaN);
        CopperRefining fr = full.getSectors().copperRefining();
        quietly(() -> full.simulateMonths(1));
        Sector.Output fcu = fr.outputRow(Good.COPPER);
        Sector.Input fin = fr.inputRow(Good.ANODE);
        double nameplate = fr.getCapacity(Good.COPPER) * fr.getOperatingRate();
        report("(c) a refinery that makes its whole rate (a yard with room) orders exactly its need at the rate: getInputAtCapacity() times"
                        + " the operating rate, to the bit",
                fcu != null && fin != null && fr.getOperatingRate() > 0 && fcu.produced == nameplate
                        && fin.bid == fr.getInputAtCapacity(Good.ANODE) * fr.getOperatingRate(),
                fin == null || fcu == null ? "none" : String.format("made %,.1f of %,.1f t; ordered %,.1f t of anode", fcu.produced, nameplate, fin.bid));
        Game chain = copperWorks("copperrefiningcheck-wholechain", 1, 1, 1, Double.NaN);
        CopperRefining kr = chain.getSectors().copperRefining();
        quietly(() -> chain.simulateMonths(1));
        boolean same = true;
        StringBuilder seen = new StringBuilder();
        for (Good in : new Good[] { Good.COPPER_CONC, Good.ANODE, Good.COPPER, Good.COPPER_SCRAP }) {
            Sector.Input row = kr.inputRow(in);
            if (row == null) continue;
            double expect = Math.max(0, kr.getInputAtCapacity(in) * kr.getOperatingRate() - (kr.captive(in) ? kr.captiveUnits(in) : 0));
            same &= row.bid == expect;
            seen.append(String.format("%s %,.1f of %,.1f t; ", in, row.bid, expect));
        }
        report("...and so does a chain whose month makes its whole rate (a smelter, a refinery and a rod mill): every order is the need at the"
                        + " rate less what is captive, to the bit",
                same && seen.length() > 0 && kr.getOperatingRate() > 0, seen.toString());
    }
}
