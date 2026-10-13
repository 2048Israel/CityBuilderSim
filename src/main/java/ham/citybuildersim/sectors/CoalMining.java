package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Deposit;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.LandManager;
import ham.citybuildersim.Resource;
import ham.citybuildersim.Sectors;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Coal mines and the plants that wash their coal - Coal Mining (0.7.126,
 * batch I6; docs/studies/spec-materials.md 2.1, 2.10 and 5's I6 row; the
 * study's 3): the second chain's digging industry, the first sector added on
 * the end of Sectors.KEYS since the refinery (Jerus's Q21, 2026-10-08: iron,
 * coal, stone, copper, wood).
 *
 * WHY. The integrated route (0.7.123, batch I4) brought the coals as goods -
 * THERMAL_COAL for the pellet plants and the blast furnaces, COKING_COAL for
 * the coke ovens - and nobody in the city made either, so every tonne was
 * imported. The world's coal lies in fields of two types (the study's 1.4):
 * thermal, sub-bituminous, sold as dug (85% of fields), and coking,
 * bituminous, which must be washed to sell as coking coal (15%). So a mine
 * stands on its type (ExtractionSector, BY TYPE AND SIZE), and a coking
 * mine's coal is RAW until a Coal Preparation Plant washes it - a separate
 * building, Jerus's Q9 - which is this class's one rule of its own.
 *
 * THE FOUR BUILDINGS (the study's 3.3; ids 111 to 114):
 *   - the SURFACE COAL MINE, 100,000 t of thermal coal a month on one thermal
 *     site, on diesel;
 *   - the LARGE SURFACE MINE, a dragline pit, 800,000 t on four sites of one
 *     thermal field;
 *   - the UNDERGROUND COAL MINE, a longwall, 250,000 t of RAW coal on one
 *     coking site;
 *   - the COAL PREPARATION PLANT, which washes 500,000 t of raw coal a month
 *     (its template's `wash`) and buys and makes no good of its own.
 *
 * THE FLOW (flow(), pure; the spec's 2.10). Raw coal is not a good - it is a
 * stream inside the sector, as the refinery's cuts are (RefineryFlow), so
 * there is nothing to price it at and nobody else may buy it:
 *   raw     = what the mines on COKING sites make;
 *   washed  = min(raw, the plants' wash);
 *   COKING  = COKING_YIELD x washed (.55);
 *   THERMAL = what the other mines make + THERMAL_YIELD x washed (.20, the
 *             middlings) + (raw - washed), sold as thermal tonne for tonne;
 *   spoil   = the rest of what was washed (.25).
 * The study's figures: 500,000 t raw through one plant is 275,000 t of coking
 * coal, 100,000 of thermal and 125,000 of spoil. getCapacity() reads the flow
 * at nameplate, so an Underground Coal Mine with no plant beside it sells its
 * raw coal as thermal, tonne for tonne (0.8% a month on its cost, the study's
 * 3.4), and with one the pair earns 2.4%.
 *
 * THE GROUND. Both coals are stockable, so the flow goods' groundLimit()
 * never sees them: a month's coal is what the mines can dig at the sector's
 * rate (lift(): each type's ask, times its dig - 1/.95 tonnes of seam a
 * tonne at the surface, 1/.80 underground - held to what its type still has
 * in the ground), the flow of THAT the figure the market is priced on and
 * the plant makes (Sector.madeAtRate()), and the same dig taken out of the
 * ground at the end of the month (endOfMonth(): LandManager.extract(r, type,
 * t), so W holds by type).
 *
 * THE PLAN. A mine is ExtractionSector's: the best earnings on the whole cost
 * among the mines that fit a field of their type, their lorries in it. And
 * the sector builds a preparation plant beside its coking mines (the study's
 * 3.4) - first, whenever raw coal standing or on site goes unwashed and the
 * plant would staff and pay (planWash()). What one more of any of the four
 * would earn is its flow's increment (madeBy()): the thermal a plant stops
 * selling raw is a loss to it, the coking coal it makes its gain.
 *
 * Who buys: Iron & Steel's coke ovens the coking coal, its pellet plants and
 * blast furnaces the thermal (I4); Cement & Concrete's kilns and cement
 * plants since 0.7.131 (I7); the world the rest. Coal power plants wait for the electricity run (Jerus's
 * Q10: a coal plant at today's tariff would lose money on its fuel alone).
 */
public final class CoalMining extends ExtractionSector {

    /** The Surface Coal Mine's permanent id in buildings.json (0.7.126): 100,000 t of thermal coal a month on one thermal site. */
    public static final int SURFACE_MINE = 111;

    /** The Large Surface Mine's (0.7.126): a dragline pit, 800,000 t of thermal coal a month on four sites of one thermal field. */
    public static final int LARGE_SURFACE_MINE = 112;

    /** The Underground Coal Mine's (0.7.126): a longwall, 250,000 t of raw coal a month on one coking site. */
    public static final int UNDERGROUND_MINE = 113;

    /** The Coal Preparation Plant's (0.7.126): washes 500,000 t of raw coal a month. */
    public static final int PREPARATION_PLANT = 114;

    /** The coking coal a tonne of raw coal washes into: .55, the study's 3.3 (275,000 t of 500,000). */
    public static final double COKING_YIELD = .55;

    /** ...the thermal coal it washes into, the middlings: .20, the study's 3.3 (100,000 t of 500,000). */
    public static final double THERMAL_YIELD = .20;

    /** ...and the spoil, the rest, to the tip: .25, the study's 3.3 (125,000 t of 500,000). */
    public static final double SPOIL_YIELD = .25;

    public CoalMining() {
        super(Sectors.COAL_MINING, "Coal Mining", BuildingType.MINING, Resource.COAL);
        makes(Good.THERMAL_COAL);
        makes(Good.COKING_COAL);
        // ...on diesel, to dig and haul at the surface: 2.3 litres a tonne (the study's 3.3).
        uses(Good.DIESEL);
        blurb("Digs coal out of the ground the city owns: thermal coal from the surface mines, "
                + "and raw coal from the underground mines on coking fields, which a preparation "
                + "plant washes into coking coal. The steelworks buy it, and the world the rest.");
    }

    /* ----- the flow ----- */

    /**
     * A month of the sector's coal (pure): the surface mines' thermal, the
     * coking mines' raw, the plants' wash, what of the raw was washed, and
     * the two coals and the spoil that came of it, in tonnes.
     */
    public record Flow(double surface, double raw, double wash, double washed, double coking, double thermal, double spoil) {
        /** The tonnes of a good it makes: COKING_COAL or THERMAL_COAL; nothing of any other. */
        public double of(Good g) {
            return g == Good.COKING_COAL ? coking : g == Good.THERMAL_COAL ? thermal : 0;
        }
    }

    /**
     * The flow (pure; the spec's 2.10): `surface` t of thermal coal as dug,
     * `raw` t of a coking field's raw coal, through plants that wash `wash`
     * t: washed = min(raw, wash); COKING_YIELD of it coking, THERMAL_YIELD
     * thermal, SPOIL_YIELD spoil; the raw not washed sold as thermal, tonne
     * for tonne.
     */
    public static Flow flow(double surface, double raw, double wash) {
        double s = Math.max(0, surface), r = Math.max(0, raw), w = Math.max(0, wash);
        double washed = Math.min(r, w);
        return new Flow(s, r, w, washed, COKING_YIELD * washed, s + THERMAL_YIELD * washed + (r - washed),
                SPOIL_YIELD * washed);
    }

    /** Whether a building digs a coking field's raw coal: it stands on a COKING site. */
    public static boolean digsRaw(BuildingsTemplate t) {
        return t != null && t.site() != null && t.site().resource() == Resource.COAL
                && t.site().type() == Deposit.FieldType.COKING;
    }

    /** A building's part of the flow's streams at nameplate: {surface, raw, wash}. */
    static double[] streamsOf(BuildingsTemplate t) {
        double coal = t.makes(Good.THERMAL_COAL);
        return digsRaw(t) ? new double[] { 0, coal, t.getWash() } : new double[] { coal, 0, t.getWash() };
    }

    /** The streams of the plant standing, and with `onSite` what is on site too: {surface, raw, wash} at nameplate. */
    double[] streams(boolean onSite) {
        double[] s = new double[3];
        if (buildings == null) return s;
        for (int i = 0; i < 3; i++) {
            final int k = i;
            s[k] = buildings.totalBySector(key(), t -> streamsOf(t)[k]);
            if (onSite) s[k] += buildings.underConstructionBySector(key(), t -> streamsOf(t)[k]);
        }
        return s;
    }

    /** The month's flow at nameplate, of the plant standing: what getCapacity() reads. */
    public Flow flow() {
        double[] s = streams(false);
        return flow(s[0], s[1], s[2]);
    }

    /** Nameplate of either coal a month: the flow's (0.7.126) - with no plant standing a coking mine's raw coal is thermal, tonne for tonne. */
    @Override
    public double getCapacity(Good g) {
        if (g != Good.THERMAL_COAL && g != Good.COKING_COAL) return super.getCapacity(g);
        return flow().of(g);
    }

    /** ...and what the buildings on site will add to it, which counts as supply for the planner: the flow with them less the flow without, never below nothing (a plant on site makes coking coal of what was thermal). */
    @Override
    public double getPipeline(Good g) {
        if (g != Good.THERMAL_COAL && g != Good.COKING_COAL) return super.getPipeline(g);
        double[] all = streams(true);
        return Math.max(0, flow(all[0], all[1], all[2]).of(g) - flow().of(g));
    }

    /* ----- the ground ----- */

    /** One kind of dig its mines make: the type of field they stand on (null for ANY), the tonnes of field a tonne takes, whether its coal is raw, and their nameplate. */
    record Kind(Deposit.FieldType type, double dig, boolean raw, double capacity) { }

    /** Its mines standing, by the type of field they stand on and their dig, each kind's nameplate, in the catalogue's order. */
    List<Kind> kinds() {
        Map<String, Kind> kinds = new LinkedHashMap<>();
        if (buildings == null) return new ArrayList<>();
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
            double coal = t.makes(Good.THERMAL_COAL) * buildings.getQuantity(t.getId());
            if (!(coal > 0)) continue;
            BuildingsTemplate.Site site = t.site();
            boolean on = site != null && site.resource() == Resource.COAL;
            Deposit.FieldType type = on ? site.type() : null;
            double dig = on ? site.dig() : 1;
            boolean raw = digsRaw(t);
            kinds.merge(type + "/" + dig + "/" + raw, new Kind(type, dig, raw, coal),
                    (a, b) -> new Kind(a.type, a.dig, a.raw, a.capacity + b.capacity));
        }
        return new ArrayList<>(kinds.values());
    }

    /**
     * What each kind of its mines digs this month (pure): its nameplate at the
     * sector's rate, held to what its type still has in the ground - the
     * kinds on one type sharing it in proportion to the seam they ask for -
     * in tonnes of coal (not of seam), in kinds()'s order.
     */
    double[] dug(List<Kind> kinds) {
        double[] out = new double[kinds.size()];
        LandManager land = game == null ? null : game.getLandManager();
        double rate = getOperatingRate();
        Map<Deposit.FieldType, Double> asked = new java.util.HashMap<>();
        for (Kind k : kinds) asked.merge(k.type, k.capacity * rate * k.dig, Double::sum);
        for (int i = 0; i < out.length; i++) {
            Kind k = kinds.get(i);
            double ask = k.capacity * rate;
            if (land == null || !(ask > 0)) {
                out[i] = Math.max(0, ask);
                continue;
            }
            double seam = asked.get(k.type), left = land.getRemaining(Resource.COAL, k.type);
            out[i] = seam <= left ? ask : ask * (left / seam);
        }
        return out;
    }

    /**
     * The month's coal at the sector's rate, as far as the ground holds it
     * (pure): each kind's dig (dug()) through the flow, the plants washing
     * their wash at the same rate. What the market is priced on and the
     * month makes (madeAtRate()), and what endOfMonth() takes out of the
     * ground.
     */
    public Flow lift() {
        List<Kind> kinds = kinds();
        double[] dug = dug(kinds);
        double surface = 0, raw = 0;
        for (int i = 0; i < dug.length; i++) {
            if (kinds.get(i).raw) raw += dug[i];
            else surface += dug[i];
        }
        double wash = buildings == null ? 0 : buildings.totalBySector(key(), BuildingsTemplate::getWash) * getOperatingRate();
        return flow(surface, raw, wash);
    }

    /** Either coal this month: the lift's, the ground's limit in it (Sector.madeAtRate()). */
    @Override
    protected double madeAtRate(Good g) {
        if (g != Good.THERMAL_COAL && g != Good.COKING_COAL) return super.madeAtRate(g);
        return lift().of(g);
    }

    /**
     * The month's coal out of the ground, once every good is made: each kind's
     * dig (dug(), the figures the month's coal was made of) taken out of its
     * type's ground at its dig, so E and W hold by type to the tonne.
     */
    @Override
    public void endOfMonth(Game game) {
        super.endOfMonth(game);
        if (game == null) return;
        LandManager land = game.getLandManager();
        if (land == null) return;
        List<Kind> kinds = kinds();
        double[] dug = dug(kinds);
        for (int i = 0; i < dug.length; i++) {
            if (dug[i] > 0) land.extract(Resource.COAL, kinds.get(i).type, dug[i] * kinds.get(i).dig);
        }
    }

    /* ----- the estimate ----- */

    /**
     * What one more of `t` adds to the flow at nameplate, on top of what
     * stands and is on site: {good: tonnes}, a tonne a plant stops selling
     * raw as thermal a negative tonne of thermal. A surface mine's own makes;
     * an underground mine's raw coal as thermal, or washed as far as the
     * plants have room; a plant's coking coal, less the thermal the raw coal
     * it washes no longer makes.
     */
    public Map<Good, Double> madeBy(BuildingsTemplate t) {
        Map<Good, Double> out = new EnumMap<>(Good.class);
        if (t == null) return out;
        double[] base = streams(true), one = streamsOf(t);
        Flow before = flow(base[0], base[1], base[2]), after = flow(base[0] + one[0], base[1] + one[1], base[2] + one[2]);
        for (Good g : new Good[] { Good.THERMAL_COAL, Good.COKING_COAL }) {
            double d = after.of(g) - before.of(g);
            if (d != 0) out.put(g, d);
        }
        return out;
    }

    /**
     * What one more of its buildings would clear a month: ExtractionSector's
     * estimate on what it adds to the flow (madeBy()), not its template's
     * makes - its gains through BusinessInvestment.estimatedMakerProfit(),
     * the thermal coal a plant stops selling raw valued as it would have
     * sold (lostValue()) - less its consumables and its lorries. A surface
     * mine adds its own makes, so its figure is the maker's on them.
     */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        if (t == null || plans == null || !key().equals(t.getSector())) return super.estimatedMonthlyProfit(t, plans);
        Map<Good, Double> made = madeBy(t), gains = new EnumMap<>(Good.class);
        double rate = BusinessInvestment.operatingRateOf(getOperatingRate());
        double lost = 0;
        for (Map.Entry<Good, Double> e : made.entrySet()) {
            if (e.getValue() > 0) gains.put(e.getKey(), e.getValue());
            else lost += lostValue(e.getKey(), -e.getValue(), plans);
        }
        double maker = plans.estimatedMakerProfit(this, t, gains) - lost * rate;
        return maker - SpreadPlanner.ownCosts(t, rate, exchangeFactor(), pricePerWatt) - fleetRunning(t, rate);
    }

    /**
     * What `units` of a good the sector would no longer make were worth to
     * it: the mirror of estimatedMakerProfit()'s split - off what it sends
     * abroad first, at the net export price (its make past the planners'
     * forecast of the city's demand), and off its sales at home past that,
     * at the local price.
     */
    public double lostValue(Good g, double units, BusinessInvestment plans) {
        if (!(units > 0) || markets == null) return 0;
        GoodsMarket m = markets.get(g);
        if (m == null) return 0;
        double surplus = Math.max(0, getCapacity(g) + getPipeline(g) - plans.forecast(this, m));
        double abroad = g.exportable() ? Math.min(units, surplus) : 0;
        return abroad * Math.max(0, m.netExportPrice()) + (units - abroad) * m.getLocalPrice();
    }

    /* ----- the plan ----- */

    /** The raw coal its coking mines standing and on site make past what its plants standing and on site wash, a month at nameplate. */
    public double unwashed() {
        double[] s = streams(true);
        return Math.max(0, s[1] - s[2]);
    }

    /**
     * Whether to open another mine, or a plant to wash the raw coal: the
     * plant first (planWash()), whenever raw coal goes unwashed and one would
     * staff and pay; otherwise ExtractionSector's rule for a mine.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {
        BusinessInvestment.Decision wash = planWash(plans, game);
        return wash != null ? wash : super.plan(plans, game);
    }

    /**
     * A preparation plant beside the coking mines (the study's 3.4: "the
     * sector builds one beside every coking mine"): with no order in flight
     * and raw coal standing or on site that no plant washes, the plant that
     * earns most on its whole cost of those it could staff and that would
     * carry the interest on it (SpreadPlanner's MONEY gate, decision A) -
     * ordered, or refused for want of ground; null when no plant is wanted
     * or none passes, and the mines' rule decides.
     */
    BusinessInvestment.Decision planWash(BusinessInvestment plans, Game game) {
        String sector = key();
        if (buildings == null || game == null) return null;
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) return null;
        double unwashed = unwashed();
        if (!(unwashed > 0)) return null;
        BuildingsTemplate best = null;
        double bestScore = 0;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (!(t.getWash() > 0) || !staffing(t).passes()) continue;
            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            cost += fleetCapital(t);
            double earns = estimatedMonthlyProfit(t, plans);
            if (!planner().passesMoney(this, plans, game, earns, cost)) continue;
            if (earns / cost > bestScore) {
                bestScore = earns / cost;
                best = t;
            }
        }
        if (best == null) return null;
        if (plans.plotsAvailableFor(best) < 1) return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        return new BusinessInvestment.Decision(sector, best, 1,
                String.format("%,.0fk t of raw coal a month unwashed", unwashed / 1000), true);
    }

    /* ----- the page ----- */

    @Override
    public List<Line> ownLines(Game game) {
        List<Line> lines = super.ownLines(game);
        if (game == null) return lines;
        Formats f = Formats.INSTANCE;
        Flow now = flow();
        // ...and where the coal goes (0.7.142, batch I13), standing or not: Iron & Steel's ovens and furnaces, or the world.
        if (!(now.surface() > 0 || now.raw() > 0 || now.wash() > 0)) {
            lines.addAll(ham.citybuildersim.BuildCard.chainLines(game, this));
            return lines;
        }
        lines.add(Line.head("The coal, a month at nameplate"));
        if (now.surface() > 0) lines.add(Line.of("Thermal coal dug", f.count(now.surface()) + " t"));
        if (now.raw() > 0) {
            lines.add(Line.of("Raw coal from coking fields", f.count(now.raw()) + " t"));
            lines.add(Line.of("Washed", f.count(now.washed()) + " of " + f.count(now.raw()) + " t",
                    now.washed() < now.raw() ? Line.Tone.WARN : Line.Tone.GOOD));
        }
        lines.add(Line.of("Coking coal made", f.count(now.coking()) + " t"));
        lines.add(Line.of("Thermal coal made", f.count(now.thermal()) + " t"));
        if (now.spoil() > 0) lines.add(Line.of("Spoil to the tip", f.count(now.spoil()) + " t", Line.Tone.MUTED));
        if (now.washed() < now.raw()) {
            lines.add(Line.note("Raw coal no plant washes sells as thermal coal, tonne for tonne."));
        }
        lines.addAll(ham.citybuildersim.BuildCard.chainLines(game, this));
        return lines;
    }

    /* =====================================================================
       THE PICTOGRAMS' READS (0.7.141, batch I11; ExtractionSector's, and
       ham.citybuildersim.IndustryView)
       ===================================================================== */

    /** Whether the plan weighs a building of its: a mine that lifts its coal, and since its plants are planned too (planWash()), a plant that washes it. */
    @Override
    public boolean isWeighed(BuildingsTemplate t) {
        return super.isWeighed(t) || (t != null && t.getWash() > 0);
    }

    /** A wash plant's ground is the raw coal: planWash() weighs one only while raw coal standing or on site goes unwashed (unwashed()); a mine's is ExtractionSector's. */
    @Override
    protected String groundRefusal(BuildingsTemplate t) {
        if (t == null || lifts(t) || !(t.getWash() > 0)) return super.groundRefusal(t);
        return unwashed() > 0 ? null : "no raw coal goes unwashed";
    }
}
