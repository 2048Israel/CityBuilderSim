package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.Deposit;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.LandManager;
import ham.citybuildersim.Resource;
import ham.citybuildersim.Sectors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copper mines and the leach plants on oxide fields - Copper Mining (0.7.133,
 * batch I8; docs/studies/spec-materials.md 2.1, 2.4 and 5's I8 row; the
 * study's - materials-chains-research.md - 4): the fourth materials chain's
 * digging industry, on the end of Sectors.KEYS with Copper Refining, after
 * Cement & Concrete (Jerus's Q21, 2026-10-08: iron, coal, stone, copper,
 * wood).
 *
 * WHY. Copper lies in the world's ground since 0.7.56 and its fields have had
 * two types since 0.7.110 - sulphide (80% of fields) and oxide (20%: SX-EW
 * was 21% of mine output in 2024 [K2]) - but nothing dug it, so its ground
 * was free. Ore at 0.5% copper has no market [K12], so a sulphide mine is a
 * mine AND MILL: it floats its ore into a concentrate of 30% copper, which is
 * what it sells, and a mine is worth building before anything downstream
 * exists (the study's 1.1). An oxide field is leached with acid instead and
 * makes finished copper - cathode - at the mine, buying the acid the
 * smelters make (Jerus's Q12: acid is traded).
 *
 * THE THREE BUILDINGS (the study's 4.3; ids 121 to 123):
 *   - the COPPER MINE & MILL, 2,200 t of concentrate a month (660 t of
 *     copper) from 150,000 t of ore at 0.5%, on one sulphide site;
 *   - the LARGE COPPER MINE, 26,400 t from 1.8 Mt, on eight sites of one
 *     sulphide field - an open-pit porphyry mine, 1906 (Bingham Canyon);
 *   - the LEACH & SX-EW PLANT, 1,000 t of cathode from 280,000 t of ore and
 *     3,500 t of acid, on one oxide site - 1968 (Bluebird, Arizona).
 *
 * THE GROUND (spec 2.4). A field holds tonnes of copper metal, and a
 * building's site says how much of it a unit made takes: CONC_DIG, .341 t a
 * tonne of concentrate (the mill's 88% recovery of 0.5% ore into 30%), and
 * LEACH_DIG, 1.4 t a tonne of cathode (72% recovered). Concentrate is a flow
 * good, so groundLimit() digs it out of the sulphide ground as the month
 * makes it, as Iron Mining's ore is dug; cathode is stockable, so - as Coal
 * Mining's coals are (batch I6) - a month's cathode is what the leach
 * plants can make at the sector's rate held to what the oxide ground still
 * holds (lift(), through Sector.madeAtRate()), and the same dig comes out of
 * the ground at the month's end (endOfMonth()). Either way
 * LandManager.extract(r, type, t), so E and W hold by type.
 *
 * THE PLAN is ExtractionSector's: the best earnings on the whole cost among
 * the mines that fit a field of their type, their lorries in it. What it pays
 * (the study's 4.4, at parity): the Copper Mine & Mill 2.6% of its cost a
 * month exporting its concentrate and 2.9% replacing imported concentrate,
 * the Large Copper Mine 2.8% and 3.1%, the leach plant 3.1-3.4% - copper is a
 * mine worth building, its field's price the gate (Resource.COPPER: a tonne
 * of metal in the ground at 1/100 of cathode's export price).
 *
 * Who buys: Copper Refining's smelters the concentrate, its rod mills the
 * cathode; the world the rest. The leach plants buy Copper Refining's acid
 * first, the world's past it.
 */
public final class CopperMining extends ExtractionSector {

    /** The Copper Mine & Mill's permanent id in buildings.json (0.7.133): 2,200 t of concentrate a month on one sulphide site. */
    public static final int MINE_AND_MILL = 121;

    /** The Large Copper Mine's (0.7.133): 26,400 t of concentrate a month on eight sites of one sulphide field. */
    public static final int LARGE_MINE = 122;

    /** The Leach & SX-EW Plant's (0.7.133): 1,000 t of cathode a month on one oxide site, of 3,500 t of acid. */
    public static final int LEACH_PLANT = 123;

    /** Tonnes of a sulphide field's copper a tonne of concentrate takes: .341 (spec 2.4; the study's 4.3 - 150,000 t of ore at 0.5% into 2,200 t of concentrate). */
    public static final double CONC_DIG = .341;

    /** Tonnes of an oxide field's copper a tonne of leached cathode takes: 1.4 (spec 2.4; the study's 4.3 - 280,000 t of ore at 0.5% into 1,000 t). */
    public static final double LEACH_DIG = 1.4;

    public CopperMining() {
        super(Sectors.COPPER_MINING, "Copper Mining", BuildingType.MINING, Resource.COPPER);
        makes(Good.COPPER_CONC);
        makes(Good.COPPER);
        // ...the leach plants' acid, 3.5 t a tonne of copper [K27] (the study's 7).
        uses(Good.ACID);
        blurb("Digs the copper under the ground the city owns: a sulphide field's ore floated into "
                + "concentrate at the mine, which the smelters buy, and an oxide field's leached with "
                + "acid into finished copper cathode. The world takes what the city's works do not.");
    }

    /* ----- the ground ----- */

    /**
     * The ground decides what comes up of concentrate, as ExtractionSector's
     * groundLimit() decides its resource's good: the month's ask split by each
     * kind of dig's share of the nameplate (digs()), each part's field tonnes
     * - the ask times its dig, CONC_DIG a tonne on sulphide - taken out of its
     * own type's ground, and what came up the field tonnes over the dig.
     * Cathode is stockable and never reaches here (madeAtRate()).
     */
    @Override
    protected double groundLimit(Good g, double asked) {
        if (g != Good.COPPER_CONC) return super.groundLimit(g, asked);
        if (game == null) return asked;
        LandManager land = game.getLandManager();
        if (land == null) return asked;
        List<Dig> digs = digs(g);
        if (digs.isEmpty()) return dig(land, asked);
        double nameplate = 0;
        for (Dig d : digs) nameplate += d.capacity();
        double came = 0;
        for (int i = 0; i < digs.size(); i++) {
            Dig d = digs.get(i);
            double part = digs.size() == 1 ? asked : asked * (d.capacity() / nameplate);
            if (!(d.dig() > 0)) {
                came += part;
                continue;
            }
            came += dig(land, d.type(), part * d.dig(), i > 0) / d.dig();
        }
        return came;
    }

    /**
     * What each kind of its leach plants makes of cathode this month (pure):
     * its nameplate at the sector's rate, held to what its type still has in
     * the ground - kinds on one type sharing it in proportion to the copper
     * they ask for - in tonnes of cathode, in digs()'s order. Coal Mining's
     * dug(), for one good.
     */
    double[] leached(List<Dig> kinds) {
        double[] out = new double[kinds.size()];
        LandManager land = game == null ? null : game.getLandManager();
        double rate = getOperatingRate();
        Map<Deposit.FieldType, Double> asked = new HashMap<>();
        for (Dig k : kinds) asked.merge(k.type(), k.capacity() * rate * k.dig(), Double::sum);
        for (int i = 0; i < out.length; i++) {
            Dig k = kinds.get(i);
            double ask = k.capacity() * rate;
            if (land == null || !(ask > 0)) {
                out[i] = Math.max(0, ask);
                continue;
            }
            double ore = asked.get(k.type()), left = land.getRemaining(Resource.COPPER, k.type());
            out[i] = ore <= left ? ask : ask * (Math.max(0, left) / ore);
        }
        return out;
    }

    /** The month's cathode at the sector's rate, as far as the oxide ground holds it (pure): what the market is priced on and the month makes. */
    public double lift() {
        double total = 0;
        for (double t : leached(digs(Good.COPPER))) total += t;
        return total;
    }

    /** Cathode this month: the leach plants' lift, the ground's limit in it (Sector.madeAtRate()); every other good as Sector's. */
    @Override
    protected double madeAtRate(Good g) {
        if (g != Good.COPPER) return super.madeAtRate(g);
        return lift();
    }

    /**
     * The month's cathode out of the ground, once every good is made: each
     * kind's lift (leached(), the figures the month's cathode was made of)
     * taken out of its type's ground at its dig, so E and W hold by type to
     * the tonne.
     */
    @Override
    public void endOfMonth(Game game) {
        super.endOfMonth(game);
        if (game == null) return;
        LandManager land = game.getLandManager();
        if (land == null) return;
        List<Dig> kinds = digs(Good.COPPER);
        double[] made = leached(kinds);
        for (int i = 0; i < made.length; i++) {
            if (made[i] > 0 && kinds.get(i).dig() > 0) land.extract(Resource.COPPER, kinds.get(i).type(), made[i] * kinds.get(i).dig());
        }
    }

    /* ----- the plan and the retirement ----- */

    /** What a building of its makes a month at nameplate, concentrate or cathode, in tonnes - what the plan weighs and the retirement measures. */
    static double tonnesOf(BuildingsTemplate t) {
        return t == null ? 0 : t.makes(Good.COPPER_CONC) + t.makes(Good.COPPER);
    }

    /** A building of its digs copper: the mines' concentrate or the leach plant's cathode (copper's pricing good is cathode, which the mines do not make). */
    @Override
    protected boolean lifts(BuildingsTemplate t) {
        return tonnesOf(t) > 0;
    }

    /** What one of its buildings contributes to the measures it is retired on: the tonnes it makes a month at nameplate. */
    @Override
    public double unitsOf(BuildingsTemplate t) {
        return tonnesOf(t);
    }

    /**
     * Copper worked out leaves its mines spare, measured in the tonnes they
     * make: all of them once the city's copper is out; while some is left,
     * those over a type worked out (ExtractionSector's workedOut()).
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        if (game == null || buildings == null) return null;
        double capacity = buildings.totalBySector(key(), CopperMining::tonnesOf);
        if (!(capacity > 0)) return null;
        LandManager land = game.getLandManager();
        if (!(land.getRemaining(Resource.COPPER) > 0)) return new double[] { 0, capacity };
        double spare = workedOutCapacity(land);
        return spare > 0 ? new double[] { 0, spare } : null;
    }

    /** Its nameplate standing over a type worked out, in the tonnes each makes (unitsOf()). */
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
        if (game == null || buildings == null) return lines;
        Formats f = Formats.INSTANCE;
        double conc = getCapacity(Good.COPPER_CONC), cathode = getCapacity(Good.COPPER);
        // ...and where the copper goes (0.7.142, batch I13), standing or not: the smelters the concentrate, the rod mills the cathode.
        if (!(conc > 0 || cathode > 0)) {
            lines.addAll(ham.citybuildersim.BuildCard.chainLines(game, this));
            return lines;
        }
        lines.add(Line.head("The copper, a month at nameplate"));
        if (conc > 0) lines.add(Line.of("Concentrate, on sulphide fields", f.count(conc) + " t"));
        if (cathode > 0) {
            lines.add(Line.of("Cathode leached, on oxide fields", f.count(cathode) + " t"));
            lines.add(Line.of("Acid it takes", f.count(getInputAtCapacity(Good.ACID)) + " t"));
        }
        lines.addAll(ham.citybuildersim.BuildCard.chainLines(game, this));
        return lines;
    }
}
