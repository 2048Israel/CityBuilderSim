package ham.citybuildersim;

import ham.citybuildersim.sectors.CopperMining;
import ham.citybuildersim.sectors.CopperRefining;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 * The Sulphide Leach & SX-EW Plant, checked (R6b, FE2: sulphide heap leaching, Johnson Camp, Arizona, Nov 2025): the near-future row that lets a sulphide
 * field - four copper fields in five - yield cathode at the mine, with no concentrator and no smelter, in a city that knows it.
 *
 * WHY. Copper Mining's machinery has kept a kind of dig by (type, dig) since 0.7.133 (ExtractionSector.digs()), so a plant of the oxide plant's figures
 * on a sulphide site should run on it with no new code - but a city founded today's way must never know it (R6b's call 1) and a city founded the 1900
 * way must know it from FE2's month and not before, and a building the sector cannot see (digs() reads the candidates, which are the known ones) would
 * make nothing at all. So this harness proves the row, the rule that knows it, what it does standing and played, and what the planner makes of it.
 *
 * What this has to prove:
 *   1. THE ROW. Template 153 stands on one sulphide site at SULPHIDE_LEACH_DIG (better than the oxide plant's LEACH_DIG: the builder's 85% against 72%),
 *      makes cathode and no concentrate, takes acid and costs, staffs, powers and takes land as the oxide plant does, is dated "FE2" (near-future), and its
 *      water is 71 L a kg of its cathode in the game's unit of 10,000 US gallons.
 *   2. KNOWN BY THE RESEARCH. A city founded the 1900 way does not have it among Copper Mining's candidates the month before FE2's and does at it; a city
 *      founded today's way never does, and refuses an order for it, past FE2's month.
 *   3. STANDING AND PLAYED. Built on a sulphide field of a 1900 city at FE2's month it makes its nameplate of cathode at the sector's rate and bids for its
 *      acid; at the month's end the sulphide ground gives up SULPHIDE_LEACH_DIG a tonne of it and the oxide ground nothing, W exact; a played month makes
 *      cathode, buys acid abroad (no smelter stands), makes no concentrate, and the audit closes; the sector's page says each kind of plant's cathode
 *      under its own ground (CopperMining.ownLines()).
 *   4. THE PLAN. In a town founded on a sulphide field (no oxide ground, so the oxide plant is not in the race) the planner's earnings on the whole cost,
 *      153's and the Mine & Mill's, are printed, and it orders whichever its own rule ranks first - at the sector's own money gate and with the gate set
 *      aside; 153's estimate is the maker's less its consumables and freight, to the bit.
 *   5. A PLAYED COUNT. That town played two years from FE2's month with the planner free, its money gate its own and then set aside (RefineryCheck's lever):
 *      the 153s ordered and built are counted and printed, none asserted; a city founded today's way, played the same months, orders none; the audit
 *      closes and W holds by type in each.
 *
 * Every fixture causes its condition: a town founded on the copper field nearest the default site of the type the section wants (CopperMiningCheck.
 * nearest(): oxide, with its sulphide ground by fiat, for 2 and 3, which then show the oxide ground untouched; sulphide for 4 and 5), the 1900 town's
 * clock set at FE2's month by ResearchCheck.atMonth() and the buildings placed by hand.
 */
public class SulphideLeachCheck {

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
        theRow();
        knownByTheResearch();
        standingAndPlayed();
        thePlan();
        aPlayedCount();
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static final Resource CU = Resource.COPPER;
    static final Deposit.FieldType SULPHIDE = Deposit.FieldType.SULPHIDE, OXIDE = Deposit.FieldType.OXIDE;

    /** The builder's water, litres a kilogram of cathode (Johnson Camp: "71 litres per kilogram copper", the source of the row's water). */
    static final double LITRES_A_KG = 71;
    /** Kilograms in a tonne, the unit of the row's nameplate. */
    static final double KG_A_TONNE = 1000;
    /** The game's unit of water, litres: 10,000 US gallons (UtilitiesHandler's units; WaterCheck's 37.854 m3) - the model keeps no constant for it. */
    static final double LITRES_A_WATER_UNIT = 37_854;
    /** The months section 5 plays from FE2's: two years. */
    static final int PLAYED_MONTHS = 24;

    /** FE2's month in the game's count, from the catalogue. */
    static int fe2() { return ResearchCatalog.get().entry("FE2").worldMonth(); }

    /** The clock set at a month (ResearchCheck.atMonth()), its checked exception the harness's own. */
    static void at(Game g, int month) {
        try { ResearchCheck.atMonth(g, month); } catch (Exception e) { throw new IllegalStateException(e); }
    }

    /**
     * CopperMiningCheck.copperTown() for a city founded the 1900 way (Founding.withUnfitted(true)) with its clock at FE2's month before its works are
     * built, so that the whole catalogue is known: founded on the copper field of a type nearest the default site (CopperMiningCheck.nearest(), so it holds
     * that field whole and none of the other type), `sulphideSites` sulphide sites more by fiat.
     */
    static Game town1900(String label, Deposit.FieldType founded, int sulphideSites) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> {
            g.newGame(Founding.defaults().withUnfitted(true));
            at(g, fe2());
            World w = World.of(g.getCityLand().seed());
            Deposit field = CopperMiningCheck.nearest(w, founded);
            LandManager land = g.getLandManager();
            land.install(CityLand.found(w, field.x(), field.y(), LandManager.km2(land.getOwnedSqFt())), new double[CityLand.KINDS],
                    w.totals(), w.seaTheta());
            land.restoreOwnedSqFt(land.getLandDrySqFt());
            g.setCashForTest(Founding.WEALTHY_CASH);
            land.setOwnedSqFt(land.getOwnedSqFt() + 400_000_000L);
            int held = land.getSites(CU);
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

    /** The standing and the ordered of a template: what is built plus what is under construction. */
    static long held(BuildingManager b, BuildingsTemplate t) {
        BuildingsStacks s = b.getStack(t);
        return s == null ? 0 : s.getQuantity() + s.getUnderConstruction();
    }

    /* ============================ 1. the row ============================ */

    static void theRow() {
        out.println("--- 1. the row: one sulphide site at SULPHIDE_LEACH_DIG, cathode and no concentrate, the oxide plant's acid, dated FE2, 71 L a kg ---");
        BuildingManager b = CopperMiningCheck.copperTown("sulphideleachcheck-row", 0).getBuildingManager();
        BuildingsTemplate t = b.getTemplate(CopperMining.SULPHIDE_LEACH), oxide = b.getTemplate(CopperMining.LEACH_PLANT);
        assertTrue("fixture: Sulphide Leach & SX-EW Plant (153) and the Leach & SX-EW Plant (123) are in the catalogue, in Copper Mining",
                t != null && oxide != null && t.getSector().equals(Sectors.COPPER_MINING) && oxide.getSector().equals(Sectors.COPPER_MINING)
                        && t.getName().equals("Sulphide Leach & SX-EW Plant"));
        if (t == null || oxide == null) return;
        BuildingsTemplate.Site site = t.site(), oxideSite = oxide.site();
        report("it stands on one sulphide site of copper at SULPHIDE_LEACH_DIG, the oxide plant's on one oxide site at LEACH_DIG",
                site != null && site.resource() == CU && site.type() == SULPHIDE && site.sites() == 1 && site.dig() == CopperMining.SULPHIDE_LEACH_DIG
                        && oxideSite != null && oxideSite.type() == OXIDE && oxideSite.sites() == 1 && oxideSite.dig() == CopperMining.LEACH_DIG,
                site == null ? "no site" : String.format("%s %d site(s) at %.4f t a tonne (oxide's %.4f)", site.type(), site.sites(), site.dig(), CopperMining.LEACH_DIG));
        assertTrue("...a dig of 1 / SULPHIDE_LEACH_RECOVERY, below the oxide plant's: the builder's ceiling recovers more than the oxide plant's",
                CopperMining.SULPHIDE_LEACH_DIG == 1 / CopperMining.SULPHIDE_LEACH_RECOVERY && CopperMining.SULPHIDE_LEACH_DIG < CopperMining.LEACH_DIG
                        && CopperMining.SULPHIDE_LEACH_RECOVERY > 1 / CopperMining.LEACH_DIG);
        assertTrue("it makes cathode at the oxide plant's nameplate and no concentrate; it takes acid as the oxide plant does and nothing else",
                t.makes(Good.COPPER) > 0 && t.makes(Good.COPPER) == oxide.makes(Good.COPPER) && t.makes(Good.COPPER_CONC) == 0
                        && t.uses(Good.ACID) > 0 && t.uses(Good.ACID) == oxide.uses(Good.ACID) && t.goodsMade().size() == 1);
        assertTrue("...and costs, builds, powers, stands on land, loads the road, stocks and employs as the oxide plant does",
                t.getCashCost() == oxide.getCashCost() && t.getConstructionPoints() == oxide.getConstructionPoints()
                        && t.getConstructionMaterials() == oxide.getConstructionMaterials() && t.getElectricityConsumption() == oxide.getElectricityConsumption()
                        && t.getLandSqFt() == oxide.getLandSqFt() && t.getRoadLoad() == oxide.getRoadLoad() && t.getStock() == oxide.getStock()
                        && t.getConsumables() == oxide.getConsumables() && t.getTotalJobs() == oxide.getTotalJobs());
        assertTrue("it is dated by FE2, a near-future row (a city founded today's way never knows it); the oxide plant by neither",
                t.getSinceText().equals("FE2") && ResearchCatalog.nearFuture(t.getSinceText()) && !ResearchCatalog.nearFuture(oxide.getSinceText()));
        double litres = LITRES_A_KG * t.makes(Good.COPPER) * KG_A_TONNE, units = litres / LITRES_A_WATER_UNIT;
        report("its water is 71 L a kg of its cathode in units of 10,000 US gallons: the nameplate's litres over the unit, to within half a unit",
                Math.abs(t.getWaterConsumption() - units) <= .5,
                String.format("%,.0f units for %,.0f t (%,.1f exactly); the oxide plant's %,.0f", t.getWaterConsumption(), t.makes(Good.COPPER), units,
                        oxide.getWaterConsumption()));
    }

    /* ============================ 2. known by the research ============================ */

    static void knownByTheResearch() {
        out.println("\n--- 2. known by the research: a 1900 city from FE2's month and not before; a city founded today's way never ---");
        int fe2 = fe2();
        Game old = town1900("sulphideleachcheck-1900", OXIDE, 3);
        BuildingManager ob = old.getBuildingManager();
        BuildingsTemplate t = ob.getTemplate(CopperMining.SULPHIDE_LEACH);
        assertTrue("fixture: a city founded the 1900 way, a sulphide field in its ground, FE2 dated " + CityCalendar.format(fe2),
                old.foundedUnfitted() && old.getLandManager().getSites(CU, SULPHIDE) == 3 && fe2 == ResearchCatalog.get().entry("FE2").worldMonth());
        at(old, fe2 - 1);
        boolean knowsBefore = old.knows(t), candidateBefore = ob.getTemplatesBySector(Sectors.COPPER_MINING).contains(t);
        Game.BuildResult refused = old.buildStack(t, 1, false);
        at(old, fe2);
        boolean knowsAt = old.knows(t), candidateAt = ob.getTemplatesBySector(Sectors.COPPER_MINING).contains(t);
        assertTrue("the 1900 city does not know it, nor list it among Copper Mining's candidates, the month before FE2's (and refuses an order for it)",
                !knowsBefore && !candidateBefore && refused == Game.BuildResult.NOT_KNOWN && ob.getStack(t) == null);
        assertTrue("...and knows it and lists it at FE2's month, the oxide plant among them throughout",
                knowsAt && candidateAt && ob.getTemplatesBySector(Sectors.COPPER_MINING).contains(ob.getTemplate(CopperMining.LEACH_PLANT)));

        Game modern = CopperMiningCheck.copperTown("sulphideleachcheck-today", 3);
        BuildingManager mb = modern.getBuildingManager();
        BuildingsTemplate tm = mb.getTemplate(CopperMining.SULPHIDE_LEACH);
        at(modern, fe2 + 12);
        Game.BuildResult order = modern.buildStack(tm, 1, false);
        report("a city founded today's way, its clock a year past FE2's month, does not know it, lists it for no sector and refuses the order",
                !modern.foundedUnfitted() && !mb.isTemplateKnown(tm) && !mb.getTemplatesBySector(Sectors.COPPER_MINING).contains(tm)
                        && order == Game.BuildResult.NOT_KNOWN && mb.getStack(tm) == null,
                "month " + CityCalendar.format(modern.getMonth()) + ", order " + order);
        assertTrue("...while the Copper Mine & Mill and the oxide plant are its candidates there",
                mb.getTemplatesBySector(Sectors.COPPER_MINING).contains(mb.getTemplate(CopperMining.MINE_AND_MILL))
                        && mb.getTemplatesBySector(Sectors.COPPER_MINING).contains(mb.getTemplate(CopperMining.LEACH_PLANT)));
    }

    /* ============================ 3. built on a sulphide field ============================ */

    static void standingAndPlayed() {
        out.println("\n--- 3. built on a sulphide field: cathode, its acid, the sulphide ground by SULPHIDE_LEACH_DIG, W exact; a played month ---");
        Game g = town1900("sulphideleachcheck-standing", OXIDE, 3);
        CopperMining cm = g.getSectors().copperMining();
        BuildingManager b = g.getBuildingManager();
        LandManager land = g.getLandManager();
        BuildingsTemplate t = b.getTemplate(CopperMining.SULPHIDE_LEACH);
        assertTrue("fixture: it fits a sulphide site, three of them the three and not a fourth; a smelter nowhere in the city",
                cm.fits(t) && g.hasDepositFor(t, 3) && !g.hasDepositFor(t, 4) && b.getQuantity(CopperRefining.SMELTER) == 0);
        quietly(() -> b.addStack(t, 1, true));
        double rate = cm.getOperatingRate(), lift = cm.lift();
        report("the month's cathode is its nameplate at the sector's rate, and it makes no concentrate: the sector's capacities say so",
                rate > 0 && lift == t.makes(Good.COPPER) * rate && cm.getCapacity(Good.COPPER) == t.makes(Good.COPPER) && cm.getCapacity(Good.COPPER_CONC) == 0,
                String.format("rate %.4f: %,.1f t", rate, lift));
        assertTrue("...what the market is priced on and the month makes (Sector.madeAtRate()): never past the lift",
                cm.getPlannedOutput(Good.COPPER) + cm.getExportBoundOutput(Good.COPPER) <= lift + 1e-9);
        assertTrue("...and it bids for its acid, as the oxide plant does: its input at the rate", Math.abs(cm.bid(Good.ACID) - t.uses(Good.ACID) * rate) < 1e-9);
        String heapLine = pageValue(cm, g, "Cathode heap-leached, on sulphide fields");
        assertTrue("the sector's page, the sulphide plant alone: its cathode on a sulphide line and no line says oxide, which would be untrue",
                heapLine != null && heapLine.equals(Formats.INSTANCE.count(t.makes(Good.COPPER)) + " t")
                        && pageValue(cm, g, "Cathode leached, on oxide fields") == null);
        double[] before = land.getExtractedByType(CU);
        quietly(() -> cm.endOfMonth(g));
        double[] after = land.getExtractedByType(CU);
        int s = CU.typesOf().indexOf(SULPHIDE), x = CU.typesOf().indexOf(OXIDE);
        report("at the month's end the sulphide ground gives up SULPHIDE_LEACH_DIG a tonne of the cathode made, the oxide nothing; W exact",
                Math.abs(after[s] - before[s] - lift * CopperMining.SULPHIDE_LEACH_DIG) < 1e-6 && after[x] == before[x] && LandCheck.wByType(land),
                String.format("%,.1f t of the field's copper for %,.1f t", after[s] - before[s], lift));
        double left = 700;
        land.extract(CU, SULPHIDE, land.getRemaining(CU, SULPHIDE) - left);
        report("with 700 t of the sulphide's copper left the plant leaches what it holds, 700 / SULPHIDE_LEACH_DIG t",
                Math.abs(cm.lift() - left / CopperMining.SULPHIDE_LEACH_DIG) < 1e-6, String.format("%,.1f t", cm.lift()));
        land.extract(CU, SULPHIDE, land.getRemaining(CU, SULPHIDE));
        double[] spare = cm.retirementDemandAndCapacity(g);
        assertTrue("...and once the sulphide is out it leaches nothing and is spare, while the oxide still holds copper",
                cm.lift() == 0 && land.getRemaining(CU, OXIDE) > 0 && spare != null && spare[1] == t.makes(Good.COPPER) && LandCheck.wByType(land));

        // A played month, on a fresh town: staffed, two months to settle, one measured.
        Game p = town1900("sulphideleachcheck-month", OXIDE, 3);
        CopperMining pm = p.getSectors().copperMining();
        BuildingManager pb = p.getBuildingManager();
        LandManager pl = p.getLandManager();
        BuildingsTemplate pt = pb.getTemplate(CopperMining.SULPHIDE_LEACH);
        quietly(() -> pb.addStack(pt, 1, true));
        MaterialsCheck.handWorkers(p);
        quietly(() -> p.simulateMonths(2));
        double[] pBefore = pl.getExtractedByType(CU);
        quietly(() -> p.simulateMonths(1));
        double[] pAfter = pl.getExtractedByType(CU);
        Sector.Output cathode = pm.outputRow(Good.COPPER), conc = pm.outputRow(Good.COPPER_CONC);
        double made = cathode == null ? 0 : cathode.planned + cathode.exportBound + cathode.idled;
        report("a played month, the plant standing: cathode made, none shipped as concentrate, and no smelter in the city",
                made > 0 && (conc == null || conc.produced == 0) && pb.getQuantity(CopperRefining.SMELTER) == 0 && pb.getQuantity(pt.getId()) == 1,
                String.format("%,.1f t of cathode; rate %.3f (vans %.3f)", made, pm.getOperatingRate(), pm.getVanRatio()));
        report("...the ground gave up the month's copper by type: the sulphide SULPHIDE_LEACH_DIG a tonne of cathode, the oxide nothing; W exact",
                Math.abs(pAfter[s] - pBefore[s] - made * CopperMining.SULPHIDE_LEACH_DIG) < 1e-6 && pAfter[x] == pBefore[x] && LandCheck.wByType(pl),
                String.format("sulphide %,.1f t, oxide %,.1f t", pAfter[s] - pBefore[s], pAfter[x] - pBefore[x]));
        Sector.Split acid = pm.statement().bought.get(Good.ACID);
        report("...the plant bought its acid - from the world, no smelter standing", acid != null && acid.abroad > 0 && acid.atHome == 0,
                acid == null ? "none" : String.format("D$%,.1fk abroad", acid.abroad));
        MoneyAudit.Result audit = p.getLastMoneyAudit();
        report("the month's money audit closes", audit != null && audit.relative() < 1e-4,
                audit == null ? "none" : String.format("residual %.3g of what moved", audit.relative()));

        // The sector's page: each kind of plant's cathode under its own ground, the oxide line as it always read when no sulphide plant stands.
        Game q = town1900("sulphideleachcheck-page", OXIDE, 3);
        CopperMining qm = q.getSectors().copperMining();
        BuildingManager qb = q.getBuildingManager();
        Formats f = Formats.INSTANCE;
        quietly(() -> qb.addStack(qb.getTemplate(CopperMining.LEACH_PLANT), 1, true));
        String oxideOnly = pageValue(qm, q, "Cathode leached, on oxide fields");
        assertTrue("the sector's page, an oxide plant standing: its cathode on the oxide line, no sulphide line",
                oxideOnly != null && oxideOnly.equals(f.count(qb.getTemplate(CopperMining.LEACH_PLANT).makes(Good.COPPER)) + " t")
                        && pageValue(qm, q, "Cathode heap-leached, on sulphide fields") == null);
        quietly(() -> qb.addStack(qb.getTemplate(CopperMining.SULPHIDE_LEACH), 2, true));
        double heaps = 2 * qb.getTemplate(CopperMining.SULPHIDE_LEACH).makes(Good.COPPER);
        assertTrue("...with two sulphide plants beside it, each kind on its own line: the oxide plant's cathode and the two sulphide plants'",
                oxideOnly.equals(pageValue(qm, q, "Cathode leached, on oxide fields"))
                        && (f.count(heaps) + " t").equals(pageValue(qm, q, "Cathode heap-leached, on sulphide fields")));
    }


    /** The value of the line of a sector's page with the label, or null when the page has none. */
    static String pageValue(CopperMining cm, Game g, String label) {
        for (Sector.Line l : cm.ownLines(g)) if (label.equals(l.label())) return l.value();
        return null;
    }
    /* ============================ 4. the plan ============================ */

    /** What the planner's own rule ranks first among Copper Mining's candidates that fit and pass: the best earnings on the whole cost, and each one's. */
    record Ranking(BuildingsTemplate best, double leach, double mill, String each) { }

    /** CopperMiningCheck.plan()'s loop, over the candidates that fit and are staffed and, for `best`, pass the sector's money gate. */
    static Ranking rank(Game g) {
        CopperMining cm = g.getSectors().copperMining();
        BuildingManager b = g.getBuildingManager();
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate leach = b.getTemplate(CopperMining.SULPHIDE_LEACH), mill = b.getTemplate(CopperMining.MINE_AND_MILL), best = null;
        double top = 0, leachScore = Double.NaN, millScore = Double.NaN;
        StringBuilder each = new StringBuilder();
        for (BuildingsTemplate t : b.getTemplatesBySector(Sectors.COPPER_MINING)) {
            if (!cm.fits(t) || !cm.staffing(t).passes()) continue;
            double e = cm.estimatedMonthlyProfit(t, plans), cost = plans.getCostOf(t, 1) + cm.fleetCapital(t);
            each.append(String.format("%s %.3f%%; ", t.getName(), 100 * e / cost));
            if (t == leach) leachScore = e / cost;
            if (t == mill) millScore = e / cost;
            if (!cm.planner().passesMoney(cm, plans, g, e, cost)) continue;
            if (e / cost > top) { top = e / cost; best = t; }
        }
        return new Ranking(best, leachScore, millScore, each.toString());
    }

    /** The dearest of Copper Mining's candidates' whole cost: what the sector's till is topped to when its money gate is set aside. */
    static double dearest(Game g) {
        CopperMining cm = g.getSectors().copperMining();
        BusinessInvestment plans = g.getBusinessInvestment();
        double most = 0;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(Sectors.COPPER_MINING)) most = Math.max(most, plans.getCostOf(t, 1) + cm.fleetCapital(t));
        return most;
    }

    /**
     * The sector's money gate set aside, as RefineryCheck sets its planner's: ON_ITS_BORROWING, with the dearest unit's cost in the sector's till, so
     * the order's path is what is tested and not whether a mine's earnings clear 1.25 times the interest on its whole cost at 2025's rates. Put back
     * with planner().setMoneyGate(null).
     */
    static void freeTheGate(Game g) {
        CopperMining cm = g.getSectors().copperMining();
        cm.planner().setMoneyGate(SpreadPlanner.ON_ITS_BORROWING);
        cm.setCash(Math.max(cm.getCash(), dearest(g)));
    }

    static void thePlan() {
        out.println("\n--- 4. the plan: on sulphide ground, 153's earnings on its whole cost against the Mine & Mill's, the best of those that pass ordered ---");
        Game g = town1900("sulphideleachcheck-plan", SULPHIDE, 0);
        CopperMining cm = g.getSectors().copperMining();
        BuildingManager b = g.getBuildingManager();
        MaterialsCheck.handWorkers(g);
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate leach = b.getTemplate(CopperMining.SULPHIDE_LEACH);
        assertTrue("fixture: a town founded on a sulphide field, no oxide ground in it; the sulphide plant a candidate of the 1900 city at FE2's month, fitting and staffed",
                g.getLandManager().getSites(CU, SULPHIDE) > 0 && g.getLandManager().getSites(CU, OXIDE) == 0
                        && b.getTemplatesBySector(Sectors.COPPER_MINING).contains(leach) && cm.fits(leach) && cm.staffing(leach).passes());
        Ranking asPlayed = rank(g);
        BusinessInvestment.Decision d = cm.plan(plans, g);
        out.printf("    earnings on the whole cost, a month: Sulphide Leach & SX-EW Plant %.3f%%, Copper Mine & Mill %.3f%%%n", 100 * asPlayed.leach, 100 * asPlayed.mill);
        report("at the sector's own money gate the plan is the best earnings on the whole cost of those that fit and pass, or none",
                asPlayed.best == null ? !d.build : d.build && d.template == asPlayed.best, d.reason + " - " + asPlayed.each);
        freeTheGate(g);
        Ranking freed = rank(g);
        BusinessInvestment.Decision order = cm.plan(plans, g);
        cm.planner().setMoneyGate(null);
        report("with the gate set aside something passes and the plan orders whichever its own rule ranks first (the best earnings on the whole cost)",
                freed.best != null && order.build && order.template == freed.best,
                freed.best == null ? "none passes" : "orders " + freed.best.getName() + " - " + order.reason);
        double rate = BusinessInvestment.operatingRateOf(cm.getOperatingRate());
        double expect = plans.estimatedMakerProfit(cm, leach)
                - SpreadPlanner.ownCosts(leach, rate, cm.exchangeFactor(), g.getEconomyManager().getPricePerWatt()) - cm.freightRunning(leach, rate);
        assertTrue("...153's estimate the maker's, less its consumables and its freight (ExtractionSector), to the bit",
                cm.estimatedMonthlyProfit(leach, plans) == expect);
    }

    /* ============================ 5. a played count ============================ */

    /** The plants held (built and ordered) after the played months, in a city: the sulphide leach plant, the mine and mill, the oxide leach plant. */
    static long[] played(Game g, boolean freed) {
        BuildingManager b = g.getBuildingManager();
        CopperMining cm = g.getSectors().copperMining();
        double till = freed ? dearest(g) : 0;
        if (freed) cm.planner().setMoneyGate(SpreadPlanner.ON_ITS_BORROWING);
        for (int m = 0; m < PLAYED_MONTHS; m++) {
            // ...the staff handed each month, as 4's fixture hands it: the plan under test is the planner's own, not a labour market's.
            MaterialsCheck.handWorkers(g);
            if (freed) cm.setCash(Math.max(cm.getCash(), till));
            quietly(() -> g.simulateMonths(1));
        }
        if (freed) cm.planner().setMoneyGate(null);
        return new long[] { held(b, b.getTemplate(CopperMining.SULPHIDE_LEACH)), held(b, b.getTemplate(CopperMining.MINE_AND_MILL)),
                held(b, b.getTemplate(CopperMining.LEACH_PLANT)) };
    }

    static void aPlayedCount() {
        out.println("\n--- 5. a played count: two years from FE2's month with the planner free - the 153s ordered, and none in a city founded today's way ---");
        for (boolean freed : new boolean[] { false, true }) {
            Game g = town1900("sulphideleachcheck-played-" + (freed ? "freed" : "gated"), SULPHIDE, 0);
            long[] n = played(g, freed);
            MoneyAudit.Result audit = g.getLastMoneyAudit();
            out.printf("    the 1900 city, %s to %s, the money gate %s: Sulphide Leach & SX-EW Plants %d ordered or built, Copper Mines & Mills %d, Leach & SX-EW Plants %d%n",
                    CityCalendar.format(fe2()), CityCalendar.format(g.getMonth()), freed ? "set aside" : "the sector's own", n[0], n[1], n[2]);
            report("the 1900 city played " + PLAYED_MONTHS + " months (gate " + (freed ? "set aside" : "its own") + "): the clock moved on by them, the audit closes and W holds by type",
                    g.getMonth() == fe2() + PLAYED_MONTHS && audit != null && audit.relative() < 1e-4 && LandCheck.wByType(g.getLandManager()),
                    audit == null ? "no audit" : String.format("residual %.3g of what moved", audit.relative()));
        }
        Game today = CopperMiningCheck.copperTown("sulphideleachcheck-today-played", 3);
        at(today, fe2());
        long[] n = played(today, true);
        MoneyAudit.Result audit = today.getLastMoneyAudit();
        report("a city founded today's way, played the same months from FE2's with the gate set aside, orders and builds no 153, and its audit closes",
                n[0] == 0 && audit != null && audit.relative() < 1e-4 && LandCheck.wByType(today.getLandManager()),
                String.format("%d Copper Mines & Mills, %d Leach & SX-EW Plants", n[1], n[2]));
    }
}
