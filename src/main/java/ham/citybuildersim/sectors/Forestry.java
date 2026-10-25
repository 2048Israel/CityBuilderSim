package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.Investor;
import ham.citybuildersim.LandManager;
import ham.citybuildersim.Resource;
import ham.citybuildersim.Sectors;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The crews that cut the city's forest, and the plantations they plant -
 * Forestry (0.7.138, batch I9; docs/studies/spec-materials.md 2.1, 2.11 and
 * 5's I9 row; the study's - materials-chains-research.md - 1.3, 5 and 10's
 * Q13 to Q15; the zoning study, spec-zoning.md 2 and 3): the fifth materials
 * chain's cutting industry, on the end of Sectors.KEYS after Copper Refining
 * (Jerus's Q21, 2026-10-08: iron, coal, stone, copper, wood).
 *
 * WHY. Forest has been a third of the land since 0.7.56 - 13,700 m3 of
 * standing timber a square kilometre, Canada's 137 m3 a hectare [W1][W2] -
 * and since 0.7.107 (batch Z1) the forest law has said which of it may be
 * cut and a building on open forest has cleared it, its wood counted as
 * taken out and lost. Nothing cut it, so the city's forest was worth nothing
 * and its wood went into the ground under the houses. Forestry cuts it into
 * the two goods a harvest really makes, about half sawlogs and half pulpwood
 * [W14][W15], sold to the world until Wood Products (batch I10) buys them.
 *
 * NO SITES, AND NO CUTTING LIMIT (Jerus's Q14: "the map will include
 * forest"). A crew stands on its own small landing and cuts the CUTTABLE
 * FOREST (LandManager.cuttableForest(): what stands less the wood the law
 * protects - the working and the open forest's, Z8), as much as its
 * nameplate at the sector's rate asks, as far as the forest goes. Its four
 * buildings (the study's 5.4; ids 129 to 132):
 *   - the LOGGING CAMP, 1,000 m3 of sawlogs and 1,000 of pulpwood a month, 11
 *     posts, D$3M, running D$61k;
 *   - the HARVESTING OPERATION, 6,250 and 6,250, a harvester and forwarder,
 *     18 posts, D$15M, D$217k - 1973 (research-design.md E14);
 *   - the TREE PLANTATION, 1,000 ha growing 20 m3 a hectare a year [W4], 5
 *     posts, D$2M, D$24k;
 *   - the WOODLOT, 100 ha the same (Jerus's A12, 2026-10-10: "Yes" to "a
 *     100-ha woodlot (167 m3 a month) so a small city zones forestry in
 *     steps"), a tenth of a plantation and one post.
 * At parity (the study's 5.5) a camp earns 1.3% of its cost a month
 * exporting and 4.6% replacing imports, a harvesting operation 2.7% and
 * 6.9%; a plantation loses 1.2%, nothing to sell for 25 years, which is why
 * it is never an investment but a cost of cutting.
 *
 * E, THE CREWS' RULE (Jerus's E and Z13, 2026-10-08: "yes 120"): a crew is
 * a candidate only while the cuttable forest lasts FOREST_CREW_MONTHS - 120
 * months, a crew's working life rather than its payback - of the cut of
 * every crew standing, on site and the one ordered (roomFor()). A Logging
 * Camp then needs 17.5 km2 of cuttable forest and a Harvesting Operation 109
 * km2; a 10 km2 forest gets none. Past it the plan is ExtractionSector's:
 * the best earnings on the whole cost, its freight in both, through the
 * money gate, one crew in flight.
 *
 * REGROWTH (Jerus's Q13: keep the rule, and show where it settles): what was
 * cut grows back by LandManager.FOREST_REGROWTH, 1/240, a month - so a
 * forest cut by H a month settles with 240 H cut, and the regrowth pays most
 * at none standing (spec-zoning 3.3): only the law's protected share keeps
 * forest standing. The planted stands add PLANTED_M3_A_HA_A_YEAR on their
 * hectares a month to it (plantedGrowth(), handed to the land at the month's
 * end, LandManager, THE PLANTED STANDS), never past the forest's full stock.
 * The page shows where the forest settles (settlesAt()).
 *
 * PLANTING (Jerus's Q15 and B, 2026-10-08: "No Forestry zone, no
 * plantation"): at the month's end (endOfMonth()), one stand a month while
 * the month's cut - the crews', the salvage not counted, as spec-zoning 2.2
 * recommends - exceeds the regrowth the month's end will give, the stands
 * on site counted as grown; ONLY on ground Forestry's zone holds free, at
 * the zone's price, paid from Forestry's cash outside the interest test
 * (Game.buildFor(), which asks none). A Tree Plantation where the zone holds
 * its 1,000 ha, a Woodlot where it holds 100 ha but not 1,000 (★, A12: a
 * small zone fills in steps, a big one in plantations). Its ground is the
 * zone's used ground from then on - nothing else builds there.
 *
 * THE SALVAGE CUT (Jerus's Z7: building on open forest clears it, "Forestry
 * sells it"): the standing wood a building clears (LandManager
 * .takeSalvage()) is added to the month's sawlogs and pulpwood, half each,
 * and sold as any cut (salvage()); the builders who cleared it bill Forestry
 * for it (Sector.billForService(), sectors.Construction.earnSalvageCut(), ★)
 * at its crews' running cost a cubic metre at today's wages - with no crew
 * standing, a Harvesting Operation's. It needs no crew and is outside E's
 * test, which is about the forest that stays. Until 0.7.138 that wood was
 * lost.
 */
public final class Forestry extends ExtractionSector {

    /** The Logging Camp's permanent id in buildings.json (0.7.138): 1,000 m3 of sawlogs and 1,000 of pulpwood a month. */
    public static final int LOGGING_CAMP = 129;

    /** The Harvesting Operation's (0.7.138): 6,250 and 6,250, a harvester and forwarder crew. */
    public static final int HARVESTING_OPERATION = 130;

    /** The Tree Plantation's (0.7.138): 1,000 ha planted on Forestry's zone. */
    public static final int TREE_PLANTATION = 131;

    /** The Woodlot's (0.7.138; Jerus's A12): 100 ha, a tenth of a plantation. */
    public static final int WOODLOT = 132;

    /** E (Jerus, 2026-10-08, Z13: "yes 120"): a crew only while the cuttable forest lasts this many months of every crew's cut with it - a crew's working life, not its payback (spec-zoning 3.4). */
    public static final int FOREST_CREW_MONTHS = 120;

    /** What a planted stand grows, cubic metres a hectare a year (the study's 5.1 and 5.7 [W4]: plantations 15-40, the managed Nordic 4-6): a plantation's 1,000 ha 1,667 m3 a month, a woodlot's 100 ha 167. */
    public static final double PLANTED_M3_A_HA_A_YEAR = 20;

    /** The share of a cubic metre cut that is a sawlog, the rest pulpwood: the harvest about 50:50 sawlog to pulpwood (the study's 5.1 [W14][W15]) - a crew's rows and the salvage cut alike. */
    public static final double SAWLOG_SHARE = .5;

    /** The line the builders' bill for a salvage cut is booked under on Forestry's inputs (Sector.billForService()). */
    public static final String SALVAGE_LINE = "Salvage cut";

    /** Square feet in a hectare: a hundredth of a square kilometre. */
    static final double SQ_FT_PER_HA = LandManager.SQ_FT_PER_KM2 / 100;

    public Forestry() {
        super(Sectors.FORESTRY, "Forestry", BuildingType.MINING, Resource.FOREST);
        makes(Good.SAWLOGS);
        makes(Good.PULPWOOD);
        blurb("Cuts the forest the city owns and the law lets it cut into sawlogs and pulpwood, and sells "
                + "the wood a new building clears. Adds a crew only while that forest would last ten years of "
                + "the crews' cut, and plants on ground the city zones for it while its cut outgrows the regrowth.");
    }

    /* ----- what each building is ----- */

    /** Cubic metres of forest a building of its cuts a month at nameplate - sawlogs and pulpwood - nothing for a planted stand. */
    public static double cutOf(BuildingsTemplate t) {
        return t == null ? 0 : t.makes(Good.SAWLOGS) + t.makes(Good.PULPWOOD);
    }

    /** Whether a building is one of its crews: it cuts the forest. */
    public static boolean isCrew(BuildingsTemplate t) {
        return cutOf(t) > 0;
    }

    /** Whether a building is a planted stand: Forestry's plantation or woodlot. */
    public static boolean isStand(BuildingsTemplate t) {
        return t != null && (t.getId() == TREE_PLANTATION || t.getId() == WOODLOT) && Sectors.FORESTRY.equals(t.getSector());
    }

    /** What a planted stand grows a month, cubic metres: its hectares - its ground - at PLANTED_M3_A_HA_A_YEAR, a twelfth; nothing for anything else. */
    public static double growthOf(BuildingsTemplate t) {
        return isStand(t) ? t.getLandSqFt() / SQ_FT_PER_HA * PLANTED_M3_A_HA_A_YEAR / 12 : 0;
    }

    /** A crew's lifts its ground (ExtractionSector's plan and retirement test): any building that cuts. */
    @Override
    protected boolean lifts(BuildingsTemplate t) {
        return isCrew(t);
    }

    /** The crews' cut a month at nameplate, cubic metres: those standing, and with `coming` those on site too - E's "every crew". */
    public double crewsCut(boolean coming) {
        if (buildings == null) return 0;
        double cut = buildings.totalBySector(key(), Forestry::cutOf);
        return coming ? cut + buildings.underConstructionBySector(key(), Forestry::cutOf) : cut;
    }

    /** The planted stands' growth a month, cubic metres: those standing, and with `coming` those being planted too. */
    public double plantedGrowth(boolean coming) {
        if (buildings == null) return 0;
        double grows = buildings.totalBySector(key(), Forestry::growthOf);
        return coming ? grows + buildings.underConstructionBySector(key(), Forestry::growthOf) : grows;
    }

    /* ----- the month's cut ----- */

    /** The month's wood by good, struck once at the first of its two goods' produceFlow() and handed out a good at a time (cutTheMonth()). */
    private final Map<Good, Double> due = new EnumMap<>(Good.class);

    /** ...and what each good's crews asked for, for its Output row. */
    private final Map<Good, Double> asked = new EnumMap<>(Good.class);

    /** The crews' cut this month, cubic metres: what the forest gave their ask (the salvage not in it) - what planting weighs. Saved in the extras for the page and the retirement. */
    private double cutThisMonth;

    /** The salvage cut sold this month, cubic metres. */
    private double salvageThisMonth;

    /** ...and what the builders billed for it. */
    private double salvageBilledThisMonth;

    /** The stand planted this month, or null. */
    private BuildingsTemplate plantedThisMonth;

    /** Stands planted since the city was founded, for the page. */
    private int standsPlanted;

    /**
     * A flow good this month - sawlogs and pulpwood from one cut: the crews'
     * ask of both at the rate taken out of the cuttable forest at once
     * (LandManager.extract(FOREST, m3): never the protected wood), each good
     * its share of what came; and the salvage cut added, half each (cutTheMonth()).
     */
    @Override
    public void produceFlow(Good g) {
        if (g != Good.SAWLOGS && g != Good.PULPWOOD) {
            super.produceFlow(g);
            return;
        }
        if (!due.containsKey(g)) cutTheMonth();
        Output o = output(g);
        o.capacity = getCapacity(g);
        double share = g == Good.SAWLOGS ? SAWLOG_SHARE : 1 - SAWLOG_SHARE;
        o.planned = asked.getOrDefault(g, 0.0) + salvageThisMonth * share;
        Double made = due.remove(g);
        o.produced = made == null ? 0 : made;
        o.idled = 0;
        o.exportBound = 0;
    }

    /** The month's cut, once: the crews' ask of both goods out of the cuttable forest together, and the salvage. */
    private void cutTheMonth() {
        double rate = getOperatingRate();
        double saw = getCapacity(Good.SAWLOGS) * rate, pulp = getCapacity(Good.PULPWOOD) * rate;
        double ask = saw + pulp;
        LandManager land = game == null ? null : game.getLandManager();
        double cut = ask > 0 && land != null ? land.extract(Resource.FOREST, ask) : ask;
        double share = ask > 0 ? cut / ask : 0;
        cutThisMonth = cut;
        salvageThisMonth = 0;
        salvageBilledThisMonth = 0;
        if (land != null) salvage(land.takeSalvage());
        asked.put(Good.SAWLOGS, saw);
        asked.put(Good.PULPWOOD, pulp);
        due.put(Good.SAWLOGS, saw * share + salvageThisMonth * SAWLOG_SHARE);
        due.put(Good.PULPWOOD, pulp * share + salvageThisMonth * (1 - SAWLOG_SHARE));
    }

    /**
     * THE SALVAGE CUT (Z7): `m3` of standing wood a building cleared, added to
     * the month's cut - half sawlogs, half pulpwood (SAWLOG_SHARE) - and billed
     * to Forestry by the builders who cleared it at its crews' running cost a
     * cubic metre (salvageCostPerM3()), their revenue in the same strike. No
     * crew, and outside E. What the builders billed.
     */
    public double salvage(double m3) {
        if (!(m3 > 0) || !Double.isFinite(m3)) return 0;
        double bill = m3 * salvageCostPerM3();
        salvageThisMonth += m3;
        if (bill > 0 && Double.isFinite(bill)) {
            billForService(Sectors.CONSTRUCTION, SALVAGE_LINE, bill);
            if (game != null) game.getSectors().construction().earnSalvageCut(bill);
            salvageBilledThisMonth += bill;
        }
        return bill;
    }

    /**
     * What felling and hauling a cubic metre costs (★, spec 2.11): its crews'
     * running cost - wages at today's rates, power and water
     * (BusinessInvestment.runningCostOf()) and their consumables at the
     * exchange factor - over the cubic metres they cut, at nameplate; with no
     * crew standing, a Harvesting Operation's. Nothing with no city.
     */
    public double salvageCostPerM3() {
        if (game == null || buildings == null) return 0;
        BusinessInvestment plans = game.getBusinessInvestment();
        if (plans == null) return 0;
        double cost = 0, m3 = 0;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
            int n = buildings.getQuantity(t.getId());
            if (n <= 0 || !isCrew(t)) continue;
            cost += n * crewRunning(t, plans);
            m3 += n * cutOf(t);
        }
        if (!(m3 > 0)) {
            BuildingsTemplate h = buildings.getTemplate(HARVESTING_OPERATION);
            if (h == null) return 0;
            cost = crewRunning(h, plans);
            m3 = cutOf(h);
        }
        return m3 > 0 ? cost / m3 : 0;
    }

    /** One crew's running cost a month at today's wages: its posts, power and water, and its consumables at the exchange factor. */
    double crewRunning(BuildingsTemplate t, BusinessInvestment plans) {
        return plans.runningCostOf(t) + Math.max(0, t.getConsumables()) * exchangeFactor();
    }

    /** The crews' cut this month, cubic metres, the salvage not in it. */
    public double getCutThisMonth()       { return cutThisMonth; }

    /** The salvage cut sold this month, cubic metres. */
    public double getSalvageThisMonth()   { return salvageThisMonth; }

    /** ...and what the builders billed Forestry for it. */
    public double getSalvageBilled()      { return salvageBilledThisMonth; }

    /** The stand planted this month, or null. */
    public BuildingsTemplate getPlantedThisMonth() { return plantedThisMonth; }

    /* ----- the month's end: the planted stands, and planting ----- */

    /**
     * Once every good is made: the planted stands' growth handed to the land
     * for the month's end (LandManager.setPlantedGrowth()), then planting
     * (plant()).
     */
    @Override
    public void endOfMonth(Game game) {
        super.endOfMonth(game);
        plantedThisMonth = null;
        if (game == null || buildings == null) return;
        LandManager land = game.getLandManager();
        if (land == null) return;
        land.setPlantedGrowth(plantedGrowth(false));
        plantedThisMonth = plant(game, land);
        if (plantedThisMonth != null) standsPlanted++;
    }

    /** Why the month planted nothing, for the page: set by plant(). */
    private String plantingWord = "";

    /** ...read. */
    public String getPlantingWord() { return plantingWord; }

    /**
     * PLANTING (Q15, B; spec 2.11): one stand a month while the month's cut
     * exceeds the regrowth the month's end will give - the stands on site
     * counted as grown, so a stand is not planted twice - on ground
     * Forestry's zone holds free: a Tree Plantation where it holds 1,000 ha, a
     * Woodlot where it holds 100 but not 1,000 (★). At the zone's price, from
     * Forestry's cash - outside the interest test, and never borrowed. The
     * stand planted, or null.
     */
    BuildingsTemplate plant(Game game, LandManager land) {
        double regrowth = land.regrowthDue(plantedGrowth(true));
        if (!(cutThisMonth > regrowth)) {
            plantingWord = "its cut is within the regrowth";
            return null;
        }
        if (land.getZoning().zone(key()) == null) {
            plantingWord = "no Forestry zone, no plantation";
            return null;
        }
        BuildingsTemplate t = standFor(land.heldBy(key()));
        if (t == null) {
            plantingWord = "its zone holds no 100 ha free";
            return null;
        }
        double cost = game.quoteBuild(t, 1).total + land.priceFor(t.getLandSqFt(), key(), Game.groundUse(t));
        Investor payer = game.getSectorInvestor(key());
        if (!(payer.getCash() >= cost)) {
            plantingWord = String.format("its cash will not pay for %s", ProcessingSector.a(t.getName()));
            return null;
        }
        if (!game.buildFor(payer, t, 1)) {
            plantingWord = String.format("%s could not be planted", ProcessingSector.a(t.getName()));
            return null;
        }
        plantingWord = String.format("planted %s", ProcessingSector.a(t.getName()));
        return t;
    }

    /** The stand `held` square feet of Forestry's zone holds room for (★, A12): a Tree Plantation where it holds its 1,000 ha, a Woodlot where it holds 100 ha but not 1,000; null where it holds neither. */
    public BuildingsTemplate standFor(double held) {
        if (buildings == null) return null;
        BuildingsTemplate plantation = buildings.getTemplate(TREE_PLANTATION), woodlot = buildings.getTemplate(WOODLOT);
        if (plantation != null && held >= plantation.getLandSqFt()) return plantation;
        if (woodlot != null && held >= woodlot.getLandSqFt()) return woodlot;
        return null;
    }

    /* ----- E, and the plan ----- */

    /**
     * E (Z13): whether the cuttable forest lasts FOREST_CREW_MONTHS of the cut
     * of every crew - `committed` m3 a month standing and on site - and one more
     * `t` (pure).
     */
    public static boolean roomFor(double cuttable, double committed, BuildingsTemplate t) {
        return cuttable >= FOREST_CREW_MONTHS * (committed + cutOf(t));
    }

    /**
     * Whether to put another crew on the forest (E; spec 2.11): one crew in
     * flight (a stand being planted holds none back); the cuttable forest
     * lasting FOREST_CREW_MONTHS of every crew's cut with it (roomFor()); of
     * the crews that do and the city could staff, the best earnings on the
     * whole cost - its freight in both - through the money gate
     * (ExtractionSector's, decision A); then ground for it.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {
        String sector = key();
        double coming = buildings.underConstructionBySector(sector, t -> isCrew(t) ? 1 : 0);
        if (coming >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }
        LandManager land = game.getLandManager();
        double cuttable = land.cuttableForest();
        if (!(cuttable > 0)) return BusinessInvestment.Decision.no(sector, "no forest it may cut");
        double committed = crewsCut(true);

        BuildingsTemplate best = null, forestHold = null, moneyHold = null;
        double bestScore = 0, moneyEarns = 0, moneyCost = 0;
        Staffing staffingHold = null;
        String staffingHoldName = null;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (!isCrew(t)) continue;
            // ...E: the forest it may cut lasts 120 months of every crew's cut with it.
            if (!roomFor(cuttable, committed, t)) {
                if (forestHold == null || cutOf(t) < cutOf(forestHold)) forestHold = t;
                continue;
            }
            Staffing staffing = staffing(t);
            if (!staffing.passes()) {
                if (staffingHold == null || staffing.share > staffingHold.share) {
                    staffingHold = staffing;
                    staffingHoldName = t.getName();
                }
                continue;
            }
            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            cost += fleetCapital(t);
            double earns = estimatedMonthlyProfit(t, plans);
            if (!planner().passesMoney(this, plans, game, earns, cost)) {
                if (moneyHold == null || earns / cost > moneyEarns / moneyCost) {
                    moneyHold = t;
                    moneyEarns = earns;
                    moneyCost = cost;
                }
                continue;
            }
            double score = earns / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }
        if (best == null) {
            if (moneyHold != null) {
                return BusinessInvestment.Decision.no(sector, ProcessingSector.a(moneyHold.getName()) + " "
                        + planner().city(this, plans, game).money(moneyEarns, moneyCost));
            }
            if (staffingHold != null) return BusinessInvestment.Decision.no(sector, staffingHold.why(staffingHoldName));
            if (forestHold != null) return BusinessInvestment.Decision.no(sector, eWords(cuttable, committed, forestHold));
            return BusinessInvestment.Decision.no(sector, "nothing worth building");
        }
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }
        return new BusinessInvestment.Decision(sector, best, 1,
                String.format("%,.0fk m³ of forest it may cut, %.0f months of the crews' cut with %s",
                        cuttable / 1000, cuttable / (committed + cutOf(best)), ProcessingSector.a(best.getName())), true);
    }

    /** E's refusal in words: the forest it may cut, and the months it would last with the smallest crew. */
    static String eWords(double cuttable, double committed, BuildingsTemplate t) {
        double with = committed + cutOf(t);
        return String.format("the forest it may cut, %,.0fk m³, would last %.0f months of the crews' cut with %s; it asks %d",
                cuttable / 1000, with > 0 ? cuttable / with : 0, ProcessingSector.a(t.getName()), FOREST_CREW_MONTHS);
    }

    /* ----- the retirement ----- */

    /** What one of its buildings contributes to the measures it is retired on: a crew's cut a month, a stand's growth. */
    @Override
    public double unitsOf(BuildingsTemplate t) {
        return isCrew(t) ? cutOf(t) : growthOf(t);
    }

    /**
     * Its crews against the forest they may cut: spare past what stands to
     * cut - all of them with none - and the distress rule only while the
     * forest holds a month of their cut.
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        if (game == null || buildings == null) return null;
        double capacity = crewsCut(false);
        if (!(capacity > 0)) return null;
        double cuttable = game.getLandManager().cuttableForest();
        if (!(cuttable > 0)) return new double[] { 0, capacity };
        return cuttable < capacity ? new double[] { cuttable, capacity } : null;
    }

    /** A crew may be sold; a planted stand only once no crew stands or is coming - it is the cut's cost while there is a cut. */
    @Override
    public boolean mayRetire(BuildingsTemplate t) {
        if (!isStand(t)) return true;
        return !(crewsCut(true) > 0);
    }

    /* ----- where the forest settles (Q13) ----- */

    /**
     * WHERE THE FOREST SETTLES (Jerus's Q13: keep the regrowth rule, and show
     * it; spec 2.11, spec-zoning 3.3), pure: the share of the forest's full
     * stock `full` standing in the long run at a crews' cut of `cut` m3 a
     * month with `planted` m3 a month of planted growth, `cleared` cleared for
     * building and the law protecting `protectedShare`. Without a law, full
     * less cleared less the depletion the regrowth rule settles at, 240 times
     * the cut past the planted growth; with one, never under its protected
     * share - the crews cut everything else to it (the study's 5.2: mockup
     * 4's 25.4M m3 at 75,000 a month settle at 29.1% standing; 40% with 40%
     * protected).
     */
    public static double settlesAt(double full, double cleared, double cut, double planted, double protectedShare) {
        if (!(full > 0)) return 0;
        double standing = Math.max(0, full - cleared);
        double depletion = Math.max(0, cut - planted) / LandManager.FOREST_REGROWTH;
        double settled = Math.max(0, standing - depletion);
        double kept = Math.min(Math.max(0, protectedShare) * full, standing);
        return Math.max(settled, kept) / full;
    }

    /** ...this city's, at the month's cut: its forest, cleared ground, planted growth and law. */
    public double settlesAt() {
        if (game == null) return 0;
        LandManager land = game.getLandManager();
        return settlesAt(land.getOwnedAmount(Resource.FOREST), land.getForestClearedM3(), cutThisMonth,
                plantedGrowth(false), land.getZoning().protectedShare());
    }

    /* ----- the page ----- */

    @Override
    public List<Line> ownLines(Game game) {
        List<Line> lines = new ArrayList<>();
        if (game == null || buildings == null) return lines;
        Formats f = Formats.INSTANCE;
        LandManager land = game.getLandManager();
        double full = land.getOwnedAmount(Resource.FOREST), standing = land.getRemaining(Resource.FOREST);
        double cuttable = land.cuttableForest(), crews = crewsCut(false);
        lines.add(Line.head("The forest"));
        if (!(full > 0)) {
            lines.add(Line.of("Owned", "none", Line.Tone.MUTED));
            if (salvageThisMonth > 0) lines.add(Line.of("Salvage cut, from ground built on", f.count(salvageThisMonth) + " m³"));
            lines.add(Line.note("The city's ground holds no forest: buy ground with forest on it for a crew to cut."));
            return lines;
        }
        lines.add(Line.of("Owned", f.count(full) + " m³ on " + LandManager.areaWords(land.getForestSqFt())));
        lines.add(Line.of("Standing", f.count(standing) + " m³" + (full > 0 ? ", " + f.pct(standing / full) : "")));
        if (land.getProtectedWood() > 0) lines.add(Line.of("Kept by the forest law", f.count(land.getProtectedWood()) + " m³"));
        lines.add(Line.of("It may cut", f.count(cuttable) + " m³"));
        lines.add(Line.head("The cut, a month"));
        lines.add(Line.of("The crews'", f.count(cutThisMonth) + " m³ of " + f.count(crews) + " at nameplate"));
        if (salvageThisMonth > 0) lines.add(Line.of("Salvage cut, from ground built on", f.count(salvageThisMonth) + " m³"));
        lines.add(Line.of("Regrowth", f.count(land.regrowthDue(plantedGrowth(false))) + " m³"));
        double grows = plantedGrowth(false);
        if (grows > 0) lines.add(Line.of("...of it the planted stands'", f.count(grows) + " m³"));
        lines.add(Line.of("Where the forest settles", f.pct(settlesAt()) + " standing",
                settlesAt() < .25 ? Line.Tone.WARN : Line.Tone.NONE));
        double committed = crewsCut(true);
        lines.add(Line.of("It lasts the crews", committed > 0 ? String.format("%.0f months", cuttable / committed) : "no crew",
                committed > 0 && cuttable < FOREST_CREW_MONTHS * committed ? Line.Tone.WARN : Line.Tone.NONE));
        // ...and E as the forest law's page says it (0.7.142, batch I13): the crews of the smallest kind it has room for.
        ham.citybuildersim.BuildCard.CrewRule rule = ham.citybuildersim.BuildCard.crewRule(game);
        if (rule != null) {
            lines.add(Line.of("Room under E for more " + ham.citybuildersim.BuildCard.smallestCrew(game).getName() + "s",
                    f.count(rule.more()) + " more; it carries " + f.count(rule.carries()),
                    rule.more() > 0 ? Line.Tone.NONE : Line.Tone.WARN));
        }
        lines.add(Line.note("A crew is added only while the forest it may cut lasts " + FOREST_CREW_MONTHS
                + " months of every crew's cut. What is cut grows back a 240th a month, so the forest settles with 240 months"
                + " of the cut gone; the law's protected share is what always stands."));
        lines.add(Line.head("Planting"));
        lines.add(Line.of("Stands", f.count(buildings.getQuantity(TREE_PLANTATION)) + " plantations, "
                + f.count(buildings.getQuantity(WOODLOT)) + " woodlots"));
        if (!plantingWord.isEmpty()) lines.add(Line.of("This month", plantingWord));
        if (land.getZoning().zone(key()) == null) {
            lines.add(Line.note("No Forestry zone, no plantation: zone ground for Forestry in Policy › Zoning."));
        }
        lines.addAll(ham.citybuildersim.BuildCard.chainLines(game, this));
        return lines;
    }

    /* ----- the save ----- */

    @Override
    protected void saveExtras(Map<String, Double> extras) {
        super.saveExtras(extras);
        if (cutThisMonth != 0) extras.put("cutThisMonth", cutThisMonth);
        if (salvageThisMonth != 0) extras.put("salvageThisMonth", salvageThisMonth);
        if (salvageBilledThisMonth != 0) extras.put("salvageBilledThisMonth", salvageBilledThisMonth);
        if (standsPlanted != 0) extras.put("standsPlanted", (double) standsPlanted);
    }

    @Override
    protected void restoreExtras(Map<String, Double> extras) {
        super.restoreExtras(extras);
        cutThisMonth = extras.getOrDefault("cutThisMonth", 0.0);
        salvageThisMonth = extras.getOrDefault("salvageThisMonth", 0.0);
        salvageBilledThisMonth = extras.getOrDefault("salvageBilledThisMonth", 0.0);
        standsPlanted = (int) Math.round(extras.getOrDefault("standsPlanted", 0.0));
        due.clear();
        asked.clear();
    }

    /** Stands planted since the city was founded. */
    public int getStandsPlanted() { return standsPlanted; }

    /** Everything back to a founding sector: no cut, no salvage, nothing planted. */
    @Override
    public void reset() {
        super.reset();
        cutThisMonth = 0;
        salvageThisMonth = 0;
        salvageBilledThisMonth = 0;
        standsPlanted = 0;
        plantedThisMonth = null;
        plantingWord = "";
        due.clear();
        asked.clear();
    }

    /* =====================================================================
       THE PICTOGRAMS' READS (0.7.141, batch I11; ExtractionSector's, and
       ham.citybuildersim.IndustryView)
       ===================================================================== */

    /** A crew's ground is the forest it may cut, in plan()'s words: none at all, or E - room for 120 months of every crew's cut with one more (roomFor(), eWords()). */
    @Override
    protected String groundRefusal(BuildingsTemplate t) {
        double cuttable = game.getLandManager().cuttableForest();
        if (!(cuttable > 0)) return "no forest it may cut";
        double committed = crewsCut(true);
        return roomFor(cuttable, committed, t) ? null : eWords(cuttable, committed, t);
    }
}
