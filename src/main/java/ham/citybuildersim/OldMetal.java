package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The city's old steel: what it throws out each month that a Scrap Yard can
 * buy and sort into scrap - its scrapped cars, its demolitions, its
 * fabricators' offcuts and its railway's worn-out wagon sets (0.7.123, batch
 * I4; runs/spec-materials.md 2.9, materials-chains-research.md 2.7).
 *
 * WHY. Until 0.7.123 the scrap the arc furnaces melt (Good.SCRAP, since
 * 0.7.120) had no source in the city: every tonne was imported, because
 * nothing here made any. A real city is one of the world's scrap mines - the
 * cars its households scrap, the buildings it pulls down, the offcuts of its
 * fabrication shops - and a mill on the city's own scrap is the one an arc
 * furnace pays on (the study's 2.5: the Electric Arc Furnace 0.7% of its cost
 * a month replacing imported steel on the city's scrap, 0.2% on imported).
 * Until then the cars a household scrapped were dropped
 * (HouseholdBalance.wearOutCars()'s return, which Motoring threw away), and a
 * demolished building's steel went with its debris.
 *
 * NOT A GOOD (the study's 2.3): old steel has no market of its own and no
 * world price. It is a pool that arises each month, at RECOVERY of the steel
 * in what the city throws out, kept by who threw it out (the seller):
 *   - a car scrapped, STEEL_A_CAR, the households' (Motoring);
 *   - a building demolished, STEEL_A_MATERIALS_UNIT a unit of the material
 *     it was built with, its owner's - a sector's own when it retires its
 *     plant (Game.retire()), and the builders' when the city pulls one of
 *     its own down (★ Game.closeDemolished(): the demolition is the
 *     builders' contract and what they recover from it is theirs, as the
 *     material's salvage share is - ConstructionControl.SALVAGE_SHARE);
 *   - a tonne of fabricated steel made, OFFCUTS_A_TONNE_FABRICATED, its
 *     maker's (Manufacturing);
 *   - a wagon set worn out, STEEL_A_SET, the railway's (sectors.Rail).
 *
 * THE YARDS BUY IT (settle()), once a month, when Iron & Steel makes its
 * scrap (sectors.HeavyIndustry, THE OLD STEEL): as much as its Scrap Yards
 * take at the rate they run, from every seller pro rata to what each threw
 * out, at PAY_SHARE of scrap's export price (Jerus's Q8, 2026-10-08: "pay
 * sellers 70%"). What they do not take goes to landfill, and the pool starts
 * again: old steel is not kept. With no yard standing all of it goes to
 * landfill, as it always did.
 *
 * WHO IS PAID, AND WHEN (★, Iron & Steel's books): the yards' purchase is
 * on their input line, named LINE, from each seller (Sector.billForService(),
 * the households by Trade.HOUSEHOLDS, which no sales tax credits), and each
 * selling sector's sale is its other revenue, named SOLD
 * (Sector.sellOldMetal()) - struck and banked at the next strike, the buyer's
 * and the seller's cash in the same month's window. The households are paid
 * as the yards buy, into their savings pro rata to the cars each scrapped
 * (HouseholdBalance.creditScrappedCars()); the yards' cash falls at their
 * strike, and the money audit's "- sectors OldMetalToHouseholds" reads it off
 * their statements there.
 *
 * Saved whole (DataSave.oldMetal, state() and restore()): the steel arisen
 * since the yards last bought, by seller, and the last month's record. A
 * save from before 0.7.123 carries none, which is right: that city had
 * thrown nothing out that a yard could have bought.
 */
public final class OldMetal {

    /** The share of the steel in what the city throws out that comes back as old steel: 85% for construction and cars (the study's 2.7, [I52]). */
    public static final double RECOVERY = .85;

    /** Tonnes of steel in a car scrapped (the study's 2.7, [I63]). */
    public static final double STEEL_A_CAR = .9;

    /** Tonnes of steel a unit of a demolished building's construction material holds: ★est., the study's 9.3 - about 3 t in a House's ten units. */
    public static final double STEEL_A_MATERIALS_UNIT = .3;

    /** Tonnes of offcuts a tonne of fabricated steel made leaves: the Fabrication Shop's 1,260 t of steel into 1,200 (the study's 2.7). */
    public static final double OFFCUTS_A_TONNE_FABRICATED = .05;

    /** Tonnes of steel in a wagon set worn out: a set's weight, a locomotive and what it pulls (Good.ROLLING_STOCK's 300 t). */
    public static final double STEEL_A_SET = 300;

    /** What a yard pays the sellers for a tonne of old steel, a share of scrap's export price: 70% (Jerus's Q8, 2026-10-08). */
    public static final double PAY_SHARE = .70;

    /** The yards' purchase, named on their input line. */
    public static final String LINE = "Old metal";

    /** ...and the sellers' sale, named in their other revenue. */
    public static final String SOLD = "Old metal sold";

    /** Where old steel comes from. */
    public enum Source {
        /** Cars the households scrapped. */
        CARS,
        /** Buildings pulled down. */
        DEMOLITIONS,
        /** The fabricators' offcuts. */
        OFFCUTS,
        /** The railway's wagon sets worn out. */
        ROLLING_STOCK
    }

    /** Steel arisen since the yards last bought, tonnes recovered, by seller (a sector's key, or Trade.HOUSEHOLDS), in the order each first arose. */
    private final Map<String, Double> pending = new LinkedHashMap<>();

    /** ...and by source, in Source's order. */
    private final double[] pendingBySource = new double[Source.values().length];

    /* ----- the last month the yards bought (settle()): the record, for the planner and the screens ----- */

    private final double[] lastBySource = new double[Source.values().length];
    private double lastArisen, lastBought, lastLandfill, lastPrice;
    private final Map<String, Double> lastPaid = new LinkedHashMap<>();

    /* ============================ the steel arises ============================ */

    /** `tonnes` of steel thrown out by `seller` from `source`, of which RECOVERY comes back as old steel; nothing for none, or for nobody. */
    public void arise(Source source, String seller, double tonnes) {
        if (source == null || seller == null || !(tonnes > 0) || !Double.isFinite(tonnes)) return;
        double steel = tonnes * RECOVERY;
        pending.merge(seller, steel, Double::sum);
        pendingBySource[source.ordinal()] += steel;
    }

    /** Cars the households scrapped (Motoring, at the top of the month). */
    public void carsScrapped(double cars) {
        if (cars > 0) arise(Source.CARS, Trade.HOUSEHOLDS, cars * STEEL_A_CAR);
    }

    /** `n` of a building pulled down, its steel its owner's (`owner`, a sector's key). */
    public void demolished(BuildingsTemplate t, int n, String owner) {
        if (t == null || n <= 0) return;
        arise(Source.DEMOLITIONS, owner, t.getConstructionMaterials() * (double) n * STEEL_A_MATERIALS_UNIT);
    }

    /** `tonnes` of fabricated steel made by `maker` this month: its offcuts. */
    public void fabricated(String maker, double tonnes) {
        if (tonnes > 0) arise(Source.OFFCUTS, maker, tonnes * OFFCUTS_A_TONNE_FABRICATED);
    }

    /** `sets` of rolling stock worn out by `railway` this month. */
    public void setsRetired(String railway, double sets) {
        if (sets > 0) arise(Source.ROLLING_STOCK, railway, sets * STEEL_A_SET);
    }

    /** Tonnes of old steel standing to be bought now: arisen since the yards last bought. */
    public double available() {
        double t = 0;
        for (double v : pending.values()) t += v;
        return t;
    }

    /** ...and from one source. */
    public double available(Source s) { return s == null ? 0 : pendingBySource[s.ordinal()]; }

    /** ...and one seller's. */
    public double availableFrom(String seller) { return pending.getOrDefault(seller, 0.0); }

    /* ============================ the yards buy ============================ */

    /** One seller's part of a month's purchase: who, the tonnes the yards took of theirs, and what they were paid. */
    public record Sale(String seller, double tonnes, double paid) { }

    /**
     * The month's purchase: the yards take `tonnes` of old steel (no more than
     * stands), from every seller pro rata to what each threw out, at
     * `pricePerTonne`; the rest goes to landfill, and the pool starts again.
     * Books no money - the buyer does (sectors.HeavyIndustry) - and returns
     * each seller's part, in the order each first arose. Recorded as the last
     * month (lastArisen() and the rest).
     */
    public List<Sale> settle(double tonnes, double pricePerTonne) {
        double stood = available();
        double take = stood > 0 && tonnes > 0 && Double.isFinite(tonnes) ? Math.min(tonnes, stood) : 0;
        double price = Double.isFinite(pricePerTonne) && pricePerTonne > 0 ? pricePerTonne : 0;
        List<Sale> sales = new ArrayList<>();
        lastPaid.clear();
        if (take > 0) {
            for (Map.Entry<String, Double> e : pending.entrySet()) {
                double t = take == stood ? e.getValue() : e.getValue() * (take / stood);
                if (!(t > 0)) continue;
                double paid = t * price;
                sales.add(new Sale(e.getKey(), t, paid));
                if (paid > 0) lastPaid.merge(e.getKey(), paid, Double::sum);
            }
        }
        System.arraycopy(pendingBySource, 0, lastBySource, 0, lastBySource.length);
        lastArisen = stood;
        lastBought = take;
        lastLandfill = Math.max(0, stood - take);
        lastPrice = price;
        pending.clear();
        java.util.Arrays.fill(pendingBySource, 0);
        return Collections.unmodifiableList(sales);
    }

    /** Old steel that arose in the last month the yards bought (or would have), tonnes recovered: what the planner weighs a yard against. */
    public double lastArisen() { return lastArisen; }

    /** ...from one source. */
    public double lastArisen(Source s) { return s == null ? 0 : lastBySource[s.ordinal()]; }

    /** ...of it the yards bought. */
    public double lastBought() { return lastBought; }

    /** ...and sent to landfill. */
    public double lastLandfill() { return lastLandfill; }

    /** What the yards paid a tonne that month: PAY_SHARE of scrap's export price; 0 before any month. */
    public double lastPrice() { return lastPrice; }

    /** What each seller was paid that month, by seller. */
    public Map<String, Double> lastPaid() { return Collections.unmodifiableMap(lastPaid); }

    /** ...and in all. */
    public double lastPaidTotal() {
        double t = 0;
        for (double v : lastPaid.values()) t += v;
        return t;
    }

    /* ============================ across a save ============================ */

    /**
     * Everything it holds, as DataSave.oldMetal keeps it: the steel arisen
     * since the yards last bought by seller and by source, and the last
     * month's record. Plain fields, so Gson writes it as it is.
     */
    public static final class State {
        public Map<String, Double> pending = new LinkedHashMap<>();
        public double[] pendingBySource;
        public double[] lastBySource;
        public double lastArisen, lastBought, lastLandfill, lastPrice;
        public Map<String, Double> lastPaid = new LinkedHashMap<>();
    }

    /** Its state, for the save. */
    public State state() {
        State s = new State();
        s.pending = new LinkedHashMap<>(pending);
        s.pendingBySource = pendingBySource.clone();
        s.lastBySource = lastBySource.clone();
        s.lastArisen = lastArisen;
        s.lastBought = lastBought;
        s.lastLandfill = lastLandfill;
        s.lastPrice = lastPrice;
        s.lastPaid = new LinkedHashMap<>(lastPaid);
        return s;
    }

    /** Puts a saved state back; null (a save from before 0.7.123) is a city that has thrown nothing out yet. */
    public void restore(State s) {
        reset();
        if (s == null) return;
        if (s.pending != null) for (Map.Entry<String, Double> e : s.pending.entrySet()) {
            if (e.getKey() != null && e.getValue() != null && Double.isFinite(e.getValue())) pending.put(e.getKey(), e.getValue());
        }
        copyInto(s.pendingBySource, pendingBySource);
        copyInto(s.lastBySource, lastBySource);
        lastArisen = finite(s.lastArisen);
        lastBought = finite(s.lastBought);
        lastLandfill = finite(s.lastLandfill);
        lastPrice = finite(s.lastPrice);
        if (s.lastPaid != null) for (Map.Entry<String, Double> e : s.lastPaid.entrySet()) {
            if (e.getKey() != null && e.getValue() != null && Double.isFinite(e.getValue())) lastPaid.put(e.getKey(), e.getValue());
        }
    }

    /** Nothing arisen and no month recorded: a new city. */
    public void reset() {
        pending.clear();
        java.util.Arrays.fill(pendingBySource, 0);
        java.util.Arrays.fill(lastBySource, 0);
        lastArisen = lastBought = lastLandfill = lastPrice = 0;
        lastPaid.clear();
    }

    /**
     * A currency reform (Game's redenomination): the money in it scaled - the
     * price paid a tonne and what each seller was paid - and the tonnes not,
     * a tonne being a tonne through a reform.
     */
    public void scale(double s) {
        lastPrice *= s;
        lastPaid.replaceAll((k, v) -> v * s);
    }

    private static void copyInto(double[] from, double[] to) {
        if (from == null) return;
        for (int i = 0; i < Math.min(from.length, to.length); i++) to[i] = finite(from[i]);
    }

    private static double finite(double v) { return Double.isFinite(v) ? v : 0; }
}
