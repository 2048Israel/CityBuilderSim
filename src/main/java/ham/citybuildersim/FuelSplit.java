package ham.citybuildersim;

import ham.citybuildersim.sectors.Rail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FUEL, split into the PETROL and DIESEL it became: a save written before
 * 0.7.76 converted once, on its load, before anything is restored (batch O1;
 * runs/spec-oil.md 3).
 *
 * WHY. Until 0.7.76 a refinery made FUEL, a litre of whatever a car or a
 * locomotive burned (Good's note where FUEL was). Now it makes petrol for the
 * drivers, diesel for the railway and the rest of the barrel, and a good is
 * saved by its name - so a save from before carries "FUEL" in its markets,
 * in its sectors' stock and books, and in its national accounts' goods held,
 * and this build knows no such good: Sector.restore() would drop it, and the
 * refiners' tanks, the month's sales and the market's demand would go with
 * it.
 *
 * THE SPLIT. p is the share of the month's litres the drivers burned: their
 * litres (DataSave.householdFuel's third figure) over theirs and the
 * railway's (Rail's `hauled` tonnes at Rail.FUEL_LITRES_PER_TONNE); with
 * neither, p is 1. Every FUEL figure x becomes PETROL x p and DIESEL x less
 * that (split()), so each pair sums to x to the bit - the petrol an ulp off
 * x p where no diesel could close the sum (measured: m4000's tanks, whose
 * 6,581,562.75 L lies where every PETROL + DIESEL rounds to a neighbour).
 * Two places are not split
 * because what they were is known: the railway's own purchases are all
 * DIESEL - it burns nothing else - and the market's price is kept for both.
 *
 * NO MONEY MOVES. At 0.7.76 PETROL and DIESEL carry FUEL's band, so a FUEL
 * unit is worth what either is: the stock's value, the month's books and the
 * market's strike read the same after the load as before it. Nothing here
 * touches a cash figure. Since 0.7.78 (batch O2) the two are on the
 * wholesale ladder, under FUEL's band: the price is still kept for both, so
 * the load still moves nothing, and the month's clearing strikes each in its
 * own band.
 *
 * WHAT IS CONVERTED, ON NAMES (spec-oil 3):
 *   - each market (Markets.State): FUEL's becomes PETROL's (its price; its
 *     flow, stock, demand and every month of `taken` x p) and DIESEL's (the
 *     price; the rest of each);
 *   - each sector's maps keyed "FUEL": stock, pantry, pantryUsed, the
 *     ledger's and the struck statement's units and money (unitsSold,
 *     unitsBought, sold, bought, each side of a split apart), and the month's
 *     carried exported and imported;
 *   - the national accounts' goods held (EconomyManager's slots 15 on): a
 *     file with FUEL's slot (NationalAccounts.HELD_WITH_FUEL goods) gets
 *     NationalAccounts.HELD's, FUEL's units split and the other seven
 *     products at a known zero.
 *
 * A save of format 34 or later is left alone, and so is anything already in
 * the new shape - a map with no FUEL key, a national accounts array of any
 * other length - so a later save handed an older format number by a harness
 * (SectorStatementCheck 6b) loads as it was written.
 */
public final class FuelSplit {

    /** The last save format that can carry FUEL: 0.7.75's. A save of this format or older is converted. */
    public static final int LAST_FUEL_FORMAT = 33;

    /** FUEL's saved name, which no Good carries any more. */
    public static final String FUEL = "FUEL";

    /** Where the national accounts' goods held begin in the saved array (EconomyManager.getNationalAccountsState()). */
    static final int HELD_AT = 15;

    private FuelSplit() { }

    /** What a conversion did: whether it ran, the share that became petrol, and how many FUEL figures it split. */
    public record Result(boolean ran, double petrolShare, double driversLitres, double railwayLitres, int figures) { }

    /**
     * The share of a save's FUEL that becomes PETROL: the drivers' litres over
     * the drivers' and the railway's that month, 1 with neither.
     */
    public static double petrolShare(DataSave s) {
        double drivers = driversLitres(s), railway = railwayLitres(s);
        return drivers + railway > 0 ? drivers / (drivers + railway) : 1;
    }

    /** The drivers' litres the month the save was taken: DataSave.householdFuel's third figure, 0 without it. */
    static double driversLitres(DataSave s) {
        double[] hf = s == null ? null : s.getHouseholdFuel();
        return hf != null && hf.length >= 3 && Double.isFinite(hf[2]) ? Math.max(0, hf[2]) : 0;
    }

    /** ...and the railway's: the tonnes it hauled at FUEL_LITRES_PER_TONNE, 0 without a railway. */
    static double railwayLitres(DataSave s) {
        if (s == null || s.getSectors() == null) return 0;
        for (SectorState st : s.getSectors()) {
            if (st == null || !Sectors.RAIL.equals(st.key) || st.extras == null) continue;
            Double hauled = st.extras.get("hauled");
            return hauled != null && Double.isFinite(hauled) ? Math.max(0, hauled) * Rail.FUEL_LITRES_PER_TONNE : 0;
        }
        return 0;
    }

    /**
     * x split at a share: {petrol, diesel}, the two summing to x to the bit.
     * The petrol is x share and the diesel x less it, or the double next to
     * either where the subtraction rounded away from a sum that closes: two
     * doubles a binade apart can sum only to every other double of x's
     * binade, so for some x no diesel closes x share's sum and the petrol
     * moves by its ulp. A share of 0 is all diesel, 1 all petrol.
     */
    public static double[] split(double x, double share) {
        if (!(share > 0)) return new double[] { 0, x };
        double first = x * share;
        if (!Double.isFinite(x)) return new double[] { first, x - first };
        for (double a : new double[] { first, Math.nextDown(first), Math.nextUp(first) }) {
            double d = x - a;
            for (double b : new double[] { d, Math.nextUp(d), Math.nextDown(d) }) {
                if (a + b == x) return new double[] { a, b };
            }
        }
        return new double[] { first, x - first };
    }

    /** Converts the save in place, once, if its format can carry FUEL. Called by Game's load before anything is restored. */
    public static Result convert(DataSave s) {
        if (s == null || s.getSaveFormat() > LAST_FUEL_FORMAT) return new Result(false, Double.NaN, 0, 0, 0);
        double drivers = driversLitres(s), railway = railwayLitres(s);
        double p = drivers + railway > 0 ? drivers / (drivers + railway) : 1;
        int[] n = { 0 };

        // The markets: FUEL's state becomes PETROL's and DIESEL's.
        List<Markets.State> markets = s.getMarkets();
        if (markets != null) {
            List<Markets.State> out = new ArrayList<>();
            for (Markets.State m : markets) {
                if (m == null || !FUEL.equals(m.good)) { out.add(m); continue; }
                Markets.State petrol = new Markets.State(), diesel = new Markets.State();
                petrol.good = Good.PETROL.name();
                diesel.good = Good.DIESEL.name();
                petrol.price = diesel.price = m.price;
                double[] f = split(m.flow, p), k = split(m.stock, p), w = split(m.demand, p);
                petrol.flow = f[0];   diesel.flow = f[1];
                petrol.stock = k[0];  diesel.stock = k[1];
                petrol.demand = w[0]; diesel.demand = w[1];
                if (m.taken != null) {
                    petrol.taken = new double[m.taken.length];
                    diesel.taken = new double[m.taken.length];
                    for (int i = 0; i < m.taken.length; i++) {
                        double[] t = split(m.taken[i], p);
                        petrol.taken[i] = t[0];
                        diesel.taken[i] = t[1];
                    }
                }
                out.add(petrol);
                out.add(diesel);
                n[0]++;
            }
            s.setMarkets(out);
        }

        // Every sector's maps keyed by FUEL; the railway's own purchases all diesel.
        if (s.getSectors() != null) {
            for (SectorState st : s.getSectors()) {
                if (st == null) continue;
                double share = Sectors.RAIL.equals(st.key) ? 0 : p;
                n[0] += units(st.stock, share) + units(st.pantry, share) + units(st.pantryUsed, share)
                        + units(st.exported, share) + units(st.imported, share);
                if (st.ledger != null) {
                    n[0] += units(st.ledger.unitsSold, share) + units(st.ledger.unitsBought, share)
                            + money(st.ledger.sold, share) + money(st.ledger.bought, share);
                }
                if (st.statement != null) {
                    n[0] += money(st.statement.sold, share) + money(st.statement.bought, share);
                }
            }
        }

        // The national accounts' goods held: FUEL's slot into PETROL's and DIESEL's, the rest at a known zero.
        double[] na = s.getNationalAccounts();
        if (na != null && na.length == HELD_AT + NationalAccounts.HELD_WITH_FUEL) {
            double[] wider = new double[HELD_AT + NationalAccounts.HELD.length];
            int fuelAt = HELD_AT + NationalAccounts.HELD_WITH_FUEL - 1;
            System.arraycopy(na, 0, wider, 0, fuelAt);
            double[] h = split(na[fuelAt], p);
            wider[fuelAt] = h[0];
            wider[fuelAt + 1] = h[1];
            s.setNationalAccounts(wider);
            n[0]++;
        }

        Result r = new Result(true, p, drivers, railway, n[0]);
        // ...said when it split anything: a save from before FUEL (0.7.62) carries none.
        if (n[0] > 0) System.out.printf("FUEL split into PETROL and DIESEL (save format %d, %s): petrol %.4f of it - the drivers'"
                        + " %,.0f L of the month's %,.0f with the railway's - %d figure(s) split, no money moved.%n",
                s.getSaveFormat(), s.getGameVersion(), p, drivers, drivers + railway, n[0]);
        return r;
    }

    /**
     * A map of units by good name: its FUEL into PETROL (x share) and DIESEL
     * (the rest), or all into DIESEL at a share of 0 (the railway's); 1 if it
     * held FUEL. A save that can carry FUEL holds neither product, so nothing
     * is added to.
     */
    private static int units(Map<String, Double> m, double share) {
        if (m == null || !m.containsKey(FUEL)) return 0;
        Double x = m.remove(FUEL);
        double[] v = split(x == null ? 0 : x, share);
        if (share > 0) m.put(Good.PETROL.name(), v[0]);
        m.put(Good.DIESEL.name(), v[1]);
        return 1;
    }

    /** ...and a map of money by good name, each side of the split apart. */
    private static int money(Map<String, SectorState.SplitState> m, double share) {
        if (m == null || !m.containsKey(FUEL)) return 0;
        SectorState.SplitState x = m.remove(FUEL);
        SectorState.SplitState petrol = new SectorState.SplitState(), diesel = new SectorState.SplitState();
        if (x != null) {
            double[] home = split(x.atHome, share), abroad = split(x.abroad, share);
            petrol.atHome = home[0];
            diesel.atHome = home[1];
            petrol.abroad = abroad[0];
            diesel.abroad = abroad[1];
        }
        if (share > 0) m.put(Good.PETROL.name(), petrol);
        m.put(Good.DIESEL.name(), diesel);
        return 1;
    }
}
