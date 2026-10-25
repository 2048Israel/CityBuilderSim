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
import java.util.List;
import java.util.Map;

/**
 * Quarries - Quarrying (0.7.131, batch I7; docs/studies/spec-materials.md
 * 2.1, 2.4, 2.10 and 5's I7 row; the study's 6): the third materials chain's
 * digging industry, on Sectors.KEYS after Coal Mining - its end when it came
 * (Jerus's Q21, 2026-10-08: iron, coal, stone, copper, wood).
 *
 * WHY. Since the integrated route (0.7.123, batch I4) the blast furnaces
 * have taken LIMESTONE as flux and the oxygen shop LIME, and nobody in the
 * city made either, so every tonne was imported. Stone lies in fields of two
 * types (the study's 1.4): limestone (70% of fields), which kilns and blast
 * furnaces must have, and hard rock - granite and the like - which is
 * crushed for aggregate or cut into blocks. Real crushed stone is 70%
 * limestone [S1], and what the kilns leave is sold as aggregate at the
 * aggregate price. So this sector's one rule of its own is THE CARRY.
 *
 * THE THREE BUILDINGS (the study's 6.4; ids 115 to 117):
 *   - the QUARRY, 40,000 t a month on one stone site of either type;
 *   - the LARGE QUARRY, 250,000 t on three sites of one field;
 *   - the DIMENSION STONE QUARRY, 1,000 t of blocks a month on one HARD ROCK
 *     site, three tonnes of rock a tonne of blocks.
 * The Quarry and the Large Quarry stand on ANY stone field, and their rows
 * say they make AGGREGATE, stone's pricing good (Resource.STONE).
 *
 * WHICH FIELD A QUARRY IS ON (spec 2.4: "fits() packs ANY quarries onto
 * limestone first"). No quarry is recorded on a field; LandManager.packed()
 * places the quarries standing - then those on site - as fits() would, a
 * works on a type first, ANY onto the first type, limestone, first. A
 * quarry's sites on limestone make LIMESTONE, those on hard rock AGGREGATE
 * (nameplate()): what getCapacity() and getPipeline() read, so the
 * Quarrying's limestone is what the market sees. A quarry the ground has no
 * room for (a fixture placed by hand, or an older save) reads as on
 * limestone, the first type.
 *
 * THE CARRY (spec 2.10; the study's 6.1; Jerus's Q17, "limestone sells as
 * aggregate, one way"). LIMESTONE clears before AGGREGATE - Good's order -
 * so when limestone's market has taken what the kilns and furnaces want,
 * shipUnsoldFlow(LIMESTONE) books nothing: it carries the tonnes left into
 * the month's AGGREGATE output, which sells them at home or ships them at
 * aggregate's price. The ground they came out of stays limestone (the dig
 * is by type), so W holds; the limestone's row keeps what sold as
 * limestone, and carried() what went on as aggregate - which the works that
 * burn limestone weigh it at, its export price (ProcessingSector.values()).
 * Never the other way: hard rock is not limestone.
 *
 * THE GROUND. All three goods are flow goods, so groundLimit() digs them:
 * limestone from limestone ground, aggregate and blocks from hard rock, each
 * at its quarries' dig (one tonne of field a tonne; three for blocks), with
 * LandManager.extract(r, type, t), so E and W hold by type.
 *
 * THE PLAN is ExtractionSector's: the best earnings on the whole cost among
 * the quarries that fit, their freight in it - its estimate the quarry's
 * increment to the packing (madeBy()): one more on ANY makes limestone while
 * a limestone field has room, and its limestone past what the city buys of
 * it is valued as aggregate. Who buys: Iron & Steel's blast furnaces and
 * Cement & Concrete's kilns and cement plants the limestone; nobody here the
 * aggregate or the blocks until the construction upgrade (Q18), so they ship
 * - a quarry pays where it replaces imported limestone (the study's 6.5).
 */
public final class Quarrying extends ExtractionSector {

    /** The Quarry's permanent id in buildings.json (0.7.131): 40,000 t a month on one stone site of either type. */
    public static final int QUARRY = 115;

    /** The Large Quarry's (0.7.131): 250,000 t a month on three sites of one stone field. */
    public static final int LARGE_QUARRY = 116;

    /** The Dimension Stone Quarry's (0.7.131): 1,000 t of blocks a month on one hard-rock site. */
    public static final int DIMENSION_STONE_QUARRY = 117;

    /** Limestone carried into aggregate at the last clearing (THE CARRY), tonnes; nothing until limestone first clears. */
    private double carried;

    public Quarrying() {
        super(Sectors.QUARRYING, "Quarrying", BuildingType.MINING, Resource.STONE);
        // Aggregate first: what its planning and its retirement measure in tonnes (unitsOf()).
        makes(Good.AGGREGATE);
        makes(Good.LIMESTONE);
        makes(Good.DIMENSION_STONE);
        blurb("Quarries the stone under the ground the city owns: limestone, which the kilns, "
                + "cement plants and blast furnaces buy, and aggregate - crushed hard rock, and "
                + "the limestone they leave - and blocks of dimension stone, which ship.");
    }

    /* ----- which field a quarry is on ----- */

    /** Whether a building of its stands on a stone site of either type: the Quarry and the Large Quarry. */
    static boolean onAny(BuildingsTemplate t) {
        return t != null && t.site() != null && t.site().resource() == Resource.STONE && t.site().any();
    }

    /** What a quarry digs a month at nameplate, whatever field it stands on: what its row makes of aggregate and blocks. */
    static double tonnesOf(BuildingsTemplate t) {
        return t == null ? 0 : t.makes(Good.AGGREGATE) + t.makes(Good.LIMESTONE) + t.makes(Good.DIMENSION_STONE);
    }

    /**
     * Its quarries' nameplate a month by good, placed as the ground packs
     * them (pure but for the city's ground): {LIMESTONE, AGGREGATE,
     * DIMENSION_STONE} of the quarries standing, with `onSite` those on site
     * too, and with `plus` one more of a building of its (null for none) - the
     * quarries on ANY placed by LandManager.packed() in that order, their
     * limestone sites making limestone and their hard-rock sites aggregate;
     * a quarry on a type makes its type's good (Resource.goodOf()); the
     * blocks as the row says.
     */
    double[] nameplate(boolean onSite, BuildingsTemplate plus) {
        double[] out = new double[3];
        if (buildings == null) return out;
        int[] building = onSite ? buildings.getUnderConstructionById() : new int[0];
        List<BuildingsTemplate> who = new ArrayList<>();
        List<LandManager.Claim> claims = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        for (int pass = 0; pass < 3; pass++) {
            if (pass == 1 && !onSite) continue;
            if (pass == 2 && plus == null) continue;
            for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
                int n = pass == 0 ? buildings.getQuantity(t.getId())
                        : pass == 1 ? (t.getId() < building.length ? building[t.getId()] : 0)
                        : t == plus ? 1 : 0;
                if (n <= 0 || !(tonnesOf(t) > 0)) continue;
                BuildingsTemplate.Site site = t.site();
                who.add(t);
                counts.add(n);
                claims.add(site == null || site.resource() != Resource.STONE ? null
                        : new LandManager.Claim(site.type(), site.sites(), n));
            }
        }
        LandManager land = game == null ? null : game.getLandManager();
        int[][] packed = land == null ? null : land.packed(Resource.STONE, claims);
        int hard = Resource.STONE.typesOf().indexOf(Deposit.FieldType.HARD_ROCK);
        double anyLime = 0, anyHard = 0;
        for (int i = 0; i < who.size(); i++) {
            BuildingsTemplate t = who.get(i);
            int n = counts.get(i);
            out[2] += t.makes(Good.DIMENSION_STONE) * n;
            double stone = (t.makes(Good.AGGREGATE) + t.makes(Good.LIMESTONE)) * n;
            if (!(stone > 0)) continue;
            BuildingsTemplate.Site site = t.site();
            if (site == null || site.resource() != Resource.STONE || !site.any()) {
                // ...on a type, or on no stone site at all (a row without one): its type's good, limestone by default.
                boolean limestone = site == null || site.resource() != Resource.STONE
                        || Resource.STONE.goodOf(site.type()) == Good.LIMESTONE;
                out[limestone ? 0 : 1] += stone;
                continue;
            }
            double all = (double) site.sites() * n;
            double onHard = packed == null || !(all > 0) ? 0 : packed[i][hard];
            // ...the sites placed on hard rock make aggregate; on limestone, or placed nowhere, limestone.
            double hardShare = all > 0 ? Math.min(1, onHard / all) : 0;
            anyHard += stone * hardShare;
            anyLime += stone * (1 - hardShare);
        }
        /*
         * ...AND A QUARRY ON ANY DIGS THE TYPE THAT IS LEFT (★): the packing
         * counts sites, not tonnes, so once a type is worked out its quarries
         * on ANY dig the other type's next tonne, as a dig on ANY takes the
         * walk's next unworked tonne whatever its type (LandManager, THE
         * GROUND BY TYPE); with both out, the ground gives nothing either way.
         */
        if (land != null && anyLime > 0 && !(land.getRemaining(Resource.STONE, Deposit.FieldType.LIMESTONE) > 0)
                && land.getRemaining(Resource.STONE, Deposit.FieldType.HARD_ROCK) > 0) {
            anyHard += anyLime;
            anyLime = 0;
        } else if (land != null && anyHard > 0 && !(land.getRemaining(Resource.STONE, Deposit.FieldType.HARD_ROCK) > 0)
                && land.getRemaining(Resource.STONE, Deposit.FieldType.LIMESTONE) > 0) {
            anyLime += anyHard;
            anyHard = 0;
        }
        out[0] += anyLime;
        out[1] += anyHard;
        return out;
    }

    /** The index of a good in nameplate()'s array, or -1 for one it does not make. */
    private static int slot(Good g) {
        return g == Good.LIMESTONE ? 0 : g == Good.AGGREGATE ? 1 : g == Good.DIMENSION_STONE ? 2 : -1;
    }

    /** Nameplate of each of its goods a month: the quarries standing as the ground packs them - those on limestone make limestone, on hard rock aggregate. */
    @Override
    public double getCapacity(Good g) {
        int s = slot(g);
        return s < 0 ? super.getCapacity(g) : nameplate(false, null)[s];
    }

    /** ...and what the quarries on site will add, which counts as supply for the planner: the packing with them less the packing without, never below nothing (one on site can push no standing quarry off its field, but a fixture's could). */
    @Override
    public double getPipeline(Good g) {
        int s = slot(g);
        if (s < 0) return super.getPipeline(g);
        return Math.max(0, nameplate(true, null)[s] - nameplate(false, null)[s]);
    }

    /**
     * What one more of `t` adds to its nameplate, on top of what stands and
     * is on site: {good: tonnes} - one more on ANY makes limestone while a
     * limestone field has room for it, aggregate on hard rock past that; the
     * Dimension Stone Quarry its blocks.
     */
    public Map<Good, Double> madeBy(BuildingsTemplate t) {
        Map<Good, Double> out = new EnumMap<>(Good.class);
        if (t == null) return out;
        double[] before = nameplate(true, null), after = nameplate(true, t);
        Good[] goods = { Good.LIMESTONE, Good.AGGREGATE, Good.DIMENSION_STONE };
        for (int i = 0; i < 3; i++) {
            double d = after[i] - before[i];
            if (d != 0) out.put(goods[i], d);
        }
        return out;
    }

    /* ----- the ground ----- */

    /**
     * The ground decides what comes up, by type: the month's ask of a good
     * - limestone out of limestone ground, aggregate and blocks out of hard
     * rock - at its quarries' dig (one tonne of field a tonne of stone,
     * three a tonne of blocks), each type's E rising by what it gave
     * (LandManager.extract(r, type, t)), so W holds by type. Aggregate is dug
     * before the blocks (makes()' order), so a hard-rock field running short
     * holds the blocks first.
     */
    @Override
    protected double groundLimit(Good g, double asked) {
        int s = slot(g);
        if (s < 0 || game == null) return asked;
        LandManager land = game.getLandManager();
        if (land == null || !(asked > 0)) return asked;
        Deposit.FieldType type = g == Good.LIMESTONE ? Deposit.FieldType.LIMESTONE : Deposit.FieldType.HARD_ROCK;
        double dig = digOf(g);
        if (!(dig > 0)) return asked;
        return land.extract(Resource.STONE, type, asked * dig) / dig;
    }

    /** The tonnes of field a tonne of a good takes: its quarries' dig, weighted by their nameplate of it (a tonne a tonne for limestone and aggregate, three for blocks); 1 with none standing. */
    double digOf(Good g) {
        if (buildings == null) return 1;
        double units = 0, field = 0;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
            int n = buildings.getQuantity(t.getId());
            if (n <= 0) continue;
            double made = g == Good.DIMENSION_STONE ? t.makes(Good.DIMENSION_STONE) : t.makes(Good.AGGREGATE) + t.makes(Good.LIMESTONE);
            if (!(made > 0)) continue;
            BuildingsTemplate.Site site = t.site();
            double dig = site != null && site.resource() == Resource.STONE ? site.dig() : 1;
            units += made * n;
            field += made * n * dig;
        }
        return units > 0 ? field / units : 1;
    }

    /* ----- the carry ----- */

    /** The month's limestone is made: and the last month's carry is spent, so carried() is this month's from here (THE CARRY). */
    @Override
    public void produceFlow(Good g) {
        if (g == Good.LIMESTONE) carried = 0;
        super.produceFlow(g);
    }

    /**
     * A flow good's unsold units, once its market has taken what it wants:
     * LIMESTONE's carried into the month's AGGREGATE output, tonne for tonne,
     * booking nothing (THE CARRY) - aggregate clears after it and sells them
     * at home or ships them at its price - and the limestone row left with
     * what sold as limestone; every other good shipped as any maker's is.
     */
    @Override
    public void shipUnsoldFlow(Good g, GoodsMarket market) {
        if (g != Good.LIMESTONE) {
            super.shipUnsoldFlow(g, market);
            return;
        }
        Output o = output(Good.LIMESTONE);
        double unsold = Math.max(0, o.produced - o.soldLocal);
        if (!(unsold > 0)) return;
        o.produced -= unsold;
        output(Good.AGGREGATE).produced += unsold;
        carried += unsold;
    }

    /** Limestone carried into aggregate at the last clearing, tonnes (THE CARRY): what the works that burn limestone weigh it at its export price for (ProcessingSector.values()). Saved since FW6 (2026-10-25), as a market's month of trade is: a city just loaded reads the carry of the month it was saved in. */
    public double carried() {
        return carried;
    }

    /** The carry crosses a save (FW6, 2026-10-25): the first investment pass after a load weighs limestone by it. */
    @Override
    protected void saveExtras(Map<String, Double> extras) {
        extras.put("carried", carried);
    }

    /** A save without it reads none. */
    @Override
    protected void restoreExtras(Map<String, Double> extras) {
        carried = extras.getOrDefault("carried", 0.0);
    }

    /* ----- the estimate ----- */

    /**
     * What one more of its quarries would clear a month: ExtractionSector's
     * estimate on what it adds to the packing (madeBy()), not its row's
     * makes - limestone the city would buy at the makers' rule, and the rest
     * of it as aggregate (THE CARRY) - less its consumables and its freight.
     */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        if (t == null || plans == null || markets == null || !key().equals(t.getSector())) return super.estimatedMonthlyProfit(t, plans);
        Map<Good, Double> made = madeBy(t);
        double limestone = made.getOrDefault(Good.LIMESTONE, 0.0);
        if (limestone > 0) {
            GoodsMarket m = markets.get(Good.LIMESTONE);
            double room = m == null ? 0
                    : Math.max(0, plans.forecast(this, m) - getCapacity(Good.LIMESTONE) - getPipeline(Good.LIMESTONE));
            double home = Math.min(limestone, room);
            made.put(Good.LIMESTONE, home);
            if (limestone > home) made.merge(Good.AGGREGATE, limestone - home, Double::sum);
        }
        double rate = BusinessInvestment.operatingRateOf(getOperatingRate());
        return plans.estimatedMakerProfit(this, t, made) - SpreadPlanner.ownCosts(t, rate, exchangeFactor(), pricePerWatt)
                - freightRunning(t, rate);
    }

    /* ----- the plan and the retirement ----- */

    /** A quarry of its digs stone: aggregate or limestone, or the Dimension Stone Quarry's blocks. */
    @Override
    protected boolean lifts(BuildingsTemplate t) {
        return tonnesOf(t) > 0;
    }

    /** What one of its quarries contributes to the measures it is retired on: the tonnes it digs a month at nameplate. */
    @Override
    public double unitsOf(BuildingsTemplate t) {
        return tonnesOf(t);
    }

    /**
     * Stone worked out leaves its quarries spare, measured in the tonnes
     * they dig: all of them once the city's stone is out; while some is left,
     * those over a type worked out (ExtractionSector's workedOut(): the
     * Dimension Stone Quarry once the hard rock is out - a quarry on ANY digs
     * whichever type is left, nameplate()).
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        if (game == null || buildings == null) return null;
        double capacity = buildings.totalBySector(key(), Quarrying::tonnesOf);
        if (!(capacity > 0)) return null;
        LandManager land = game.getLandManager();
        if (!(land.getRemaining(Resource.STONE) > 0)) return new double[] { 0, capacity };
        double spare = workedOutCapacity(land);
        return spare > 0 ? new double[] { 0, spare } : null;
    }

    /** Its nameplate standing over a type worked out, in the tonnes each digs (unitsOf()): the blocks' once the hard rock is out. */
    @Override
    double workedOutCapacity(LandManager land) {
        double spare = 0;
        if (buildings == null || land == null) return 0;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
            int held = buildings.getQuantity(t.getId());
            if (held > 0 && workedOut(land, t)) spare += held * tonnesOf(t);
        }
        return spare;
    }

    /* ----- the page ----- */

    @Override
    public List<Line> ownLines(Game game) {
        List<Line> lines = super.ownLines(game);
        if (game == null) return lines;
        Formats f = Formats.INSTANCE;
        double[] now = nameplate(false, null);
        // ...and where the stone goes (0.7.142, batch I13), standing or not: the kilns and furnaces the limestone, the world the rest.
        if (!(now[0] > 0 || now[1] > 0 || now[2] > 0)) {
            lines.addAll(ham.citybuildersim.BuildCard.chainLines(game, this));
            return lines;
        }
        lines.add(Line.head("The stone, a month at nameplate"));
        if (now[0] > 0) lines.add(Line.of("Limestone, on limestone fields", f.count(now[0]) + " t"));
        if (now[1] > 0) lines.add(Line.of("Aggregate, on hard rock", f.count(now[1]) + " t"));
        if (now[2] > 0) lines.add(Line.of("Dimension stone blocks", f.count(now[2]) + " t"));
        if (carried > 0) {
            lines.add(Line.of("Limestone sold as aggregate", f.count(carried) + " t", Line.Tone.MUTED));
            lines.add(Line.note("Limestone no kiln or furnace here buys sells as aggregate, tonne for tonne."));
        }
        lines.addAll(ham.citybuildersim.BuildCard.chainLines(game, this));
        return lines;
    }
}
