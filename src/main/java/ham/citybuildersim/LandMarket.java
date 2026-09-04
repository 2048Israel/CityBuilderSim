package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The land office's window: ten plots on offer, and what the next one costs.
 *
 * WHY A LISTING RATHER THAN A PRICE
 *
 * Buying land used to be a button with a number on it. There was no decision in
 * it - the price only went up, so the answer was always "buy now or buy later",
 * and the only thing the player could get wrong was timing.
 *
 * Ten plots at once is a decision. They are different sizes at different prices,
 * and roughly one in five has iron under it, which is worth far more than the
 * ground but costs more up front. Buying the cheap one, buying the big one and
 * buying the one with the ore are three different plays.
 *
 * TWO PRICES, MOVED BY DIFFERENT THINGS
 *
 * What the CITY pays for a new parcel is a function of the city's size - a
 * bigger city annexes further out and is negotiating from a weaker position, so
 * every block owned and every thousand residents makes the next offer slightly
 * dearer. It does not care whether the city is full.
 *
 * What BUSINESSES pay the city per square foot is the opposite: pure supply
 * against demand inside the city limits. A city with empty blocks sells cheap; a
 * city with nothing spare sells dear. The player no longer sets this by hand -
 * it is a market now, and the way to make land cheap is to go and buy some.
 *
 * WHAT IS LISTED STAYS LISTED
 *
 * A parcel's price is fixed the moment it appears and never moves, so a player
 * can save up for the expensive one without it drifting away. Each parcel is
 * generated from its own id, so reloading a save cannot reroll the offers into
 * something better either.
 */
public class LandMarket {

    /** Plots on offer at any one time. */
    public static final int LISTING_SIZE = 10;

    /* ------------------------- what the city pays ------------------------- */

    /**
     * Ground price per square foot before any premium, in thousands.
     *
     * $0.70/sq ft, which is exactly what a block cost before parcels existed
     * ($70,000 for 100,000 sq ft). The opening of the game should feel the same.
     */
    private static final double BASE_PRICE_PER_SQ_FT = .0007;

    /**
     * Each block already owned makes the next offer this much dearer.
     *
     * Lower than the 2% the old per-block button charged, and it has to be: that
     * 2% was per PURCHASE, and a purchase was always exactly one block. A parcel
     * averages about two and a half, so charging the old rate per block owned
     * escalates two and a half times faster than the game was balanced for - a
     * city 500 blocks in was being quoted eleven times the going rate.
     */
    private static final double PREMIUM_PER_BLOCK_OWNED = .008;

    /** ...and so does each thousand residents. Deliberately small. */
    private static final double PREMIUM_PER_1000_PEOPLE = .05;

    /**
     * What the seller charges for the ore, per tonne in the ground, in thousands.
     *
     * $0.40 a tonne against an export price of $320 a tonne, so the ground is
     * changing hands at about a thousandth of what is under it. That sounds
     * generous until you price it against the mine: a three-million-tonne
     * deposit costs $1.2M, and a mine feeding local mills clears about $62k a
     * month, so the ground is roughly a year and a half of the mine's profit.
     * Expensive enough to be a decision, cheap enough to be a good one.
     */
    private static final double IRON_PRICE_PER_TONNE = .0004;

    /* ------------------------- what businesses pay ------------------------- */

    /** The city's margin on land nobody is competing for. */
    private static final double BASE_MARKUP = .15;

    /** How much more a full city can charge on top of that. */
    private static final double SCARCITY_MARKUP = .90;

    /* ---------------------------- the parcels ---------------------------- */

    /**
     * Fixed seed. Every parcel is generated from SEED and its own id, so the
     * same id always produces the same plot - which is what lets the listing be
     * restored from a save without storing every field, and stops a reload from
     * being a reroll.
     */
    private static final long SEED = 705_398_211_733L;

    private final List<LandParcel> listing = new ArrayList<>();
    private int nextId = 1;

    /** Ground price per square foot right now, before any parcel's ore premium. */
    private double marketPricePerSqFt = BASE_PRICE_PER_SQ_FT;

    /** What businesses are charged. Derived, not set. */
    private double salePricePerSqFt = BASE_PRICE_PER_SQ_FT * (1 + BASE_MARKUP);

    /* ===================================================================
       PRICING
       =================================================================== */

    /**
     * Re-prices the market and tops the listing back up to ten.
     *
     * Called once a month and after every purchase. Existing parcels are never
     * touched: a new market price only affects plots listed from now on.
     *
     * @param ownedSqFt     everything the city has annexed
     * @param allocatedSqFt what is standing on or being built on
     * @param population    residents - the other half of "how big is this city"
     */
    public void update(double ownedSqFt, double allocatedSqFt, int population) {

        double blocksOwned = Math.max(0,
                (ownedSqFt - LandManager.STARTING_SQ_FT) / LandManager.BLOCK_SQ_FT);

        marketPricePerSqFt = BASE_PRICE_PER_SQ_FT
                * (1 + PREMIUM_PER_BLOCK_OWNED * blocksOwned)
                * (1 + PREMIUM_PER_1000_PEOPLE * population / 1000.0);

        /*
         * Supply against demand, inside the city.
         *
         * Utilisation is the honest measure of both at once: it is what share of
         * the city's land is already spoken for, so a city with room is a city
         * whose land nobody is fighting over. Priced off the market rate rather
         * than a constant so the sale price rises with acquisition costs on its
         * own - otherwise a mature city would eventually be selling at a loss.
         */
        double utilisation = (ownedSqFt > 0)
                ? Math.min(Math.max(allocatedSqFt / ownedSqFt, 0), 1)
                : 1;

        salePricePerSqFt = marketPricePerSqFt
                * (1 + BASE_MARKUP + SCARCITY_MARKUP * utilisation);

        while (listing.size() < LISTING_SIZE) {
            listing.add(generate(nextId++));
        }
    }

    /** Ground price per square foot the city would pay today, in thousands. */
    public double getMarketPricePerSqFt() { return marketPricePerSqFt; }

    /** What a business pays the city per square foot, in thousands. */
    public double getSalePricePerSqFt()   { return salePricePerSqFt; }

    /* ===================================================================
       GENERATING A PARCEL

       Deterministic in the id, so parcel 47 is the same plot in every session
       and in every reload of the same session. The market price is NOT part of
       that determinism - it is applied at listing time and then frozen into the
       parcel, which is why what is listed stays listed.
       =================================================================== */

    private LandParcel generate(int id) {

        Random random = new Random(scramble(SEED + id));

        double sizeSqFt = rollSize(random);
        double ironTonnes = rollIron(random, sizeSqFt);

        double price = sizeSqFt * marketPricePerSqFt
                + ironTonnes * IRON_PRICE_PER_TONNE;

        // Round to something a player can read. Nobody wants to compare
        // $103,847 against $98,211.
        price = Math.round(price / 5) * 5.0;

        return new LandParcel(id, sizeSqFt, price, ironTonnes);
    }

    /**
     * Plot sizes, weighted towards the small and awkward.
     *
     * Mostly small because most of what a city buys is infill, and the occasional
     * enormous tract is what makes the listing worth reading - a 15-block parcel
     * is the only way to site a coal plant and a water plant without buying six
     * separate plots.
     */
    private double rollSize(Random random) {
        int roll = random.nextInt(100);
        if (roll < 40) return round(30_000 + random.nextDouble() * 50_000);    // infill
        if (roll < 75) return round(100_000 + random.nextDouble() * 150_000);  // a few blocks
        if (roll < 93) return round(300_000 + random.nextDouble() * 400_000);  // room to work
        return round(900_000 + random.nextDouble() * 900_000);                 // a tract
    }

    /**
     * Iron under the ground, in tonnes.
     *
     * Big plots are likelier to hold ore - the deposits are out in open country,
     * not under the infill - which also means the parcels that cost the most to
     * buy are the ones worth the most to own.
     *
     * Deposits are sized in millions of tonnes against a mine that lifts about
     * 16,000 tonnes a year, so a single deposit is a century or two of one mine
     * and rather less of four. Finite, but not something the player has to plan
     * around in their first hundred years.
     */
    private double rollIron(Random random, double sizeSqFt) {

        double chance = .06 + .00000025 * sizeSqFt;   // 6% for infill, ~50% for a tract
        if (random.nextDouble() > Math.min(chance, .55)) {
            return 0;
        }

        double tonnes = 1_500_000 + random.nextDouble() * 4_500_000;
        return Math.round(tonnes / 50_000) * 50_000.0;
    }

    private double round(double sqFt) {
        return Math.round(sqFt / 1000) * 1000.0;
    }

    /**
     * Spreads consecutive ids into unrelated seeds.
     *
     * java.util.Random is a linear congruential generator: seed it with 1001,
     * 1002, 1003 and the first few values it hands back are strongly related.
     * Parcel ids ARE consecutive, so seeding it with them directly produced ten
     * plots in a row of almost the same size and never once any iron - the
     * listing looked broken because it was.
     *
     * This is the SplitMix64 finaliser, which is three multiplies and three
     * shifts and exists precisely to turn a counter into something that looks
     * random. Determinism is preserved, which is the whole reason for seeding
     * by id in the first place.
     */
    private static long scramble(long value) {
        long z = value + 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    /* ===================================================================
       THE LISTING
       =================================================================== */

    /** The plots on offer, in the order they were listed. */
    public List<LandParcel> getListing() {
        return new ArrayList<>(listing);
    }

    public LandParcel find(int id) {
        for (LandParcel parcel : listing) {
            if (parcel.getId() == id) return parcel;
        }
        return null;
    }

    /** The cheapest thing on offer, for a caller that just wants some land. */
    public LandParcel cheapest() {
        LandParcel best = null;
        for (LandParcel parcel : listing) {
            if (best == null || parcel.getPrice() < best.getPrice()) {
                best = parcel;
            }
        }
        return best;
    }

    /** The best value per square foot that carries no ore premium. */
    public LandParcel bestValue() {
        LandParcel best = null;
        for (LandParcel parcel : listing) {
            if (parcel.hasIron()) continue;
            if (best == null || parcel.getPricePerSqFt() < best.getPricePerSqFt()) {
                best = parcel;
            }
        }
        return (best != null) ? best : cheapest();
    }

    /** The listed deposit with the most ore, or null if none is on offer. */
    public LandParcel richestDeposit() {
        LandParcel best = null;
        for (LandParcel parcel : listing) {
            if (!parcel.hasIron()) continue;
            if (best == null || parcel.getIronTonnes() > best.getIronTonnes()) {
                best = parcel;
            }
        }
        return best;
    }

    /**
     * Removes a parcel from the window. The caller has bought it.
     *
     * Does NOT refill - update() does that, so the replacement is priced against
     * the city as it stands after the purchase rather than before it.
     */
    public LandParcel take(int id) {
        LandParcel parcel = find(id);
        if (parcel != null) {
            listing.remove(parcel);
        }
        return parcel;
    }

    /* ===================================================================
       SAVE AND RESTORE

       The listing is written out in full rather than regenerated from the id
       counter. Regenerating would be smaller, but it would tie every existing
       save to the exact contents of rollSize() and rollIron() forever - change
       a weighting and every player's window silently reshuffles, including the
       parcel they were saving up for.
       =================================================================== */

    public double[] getListingState() {
        double[] state = new double[1 + listing.size() * 4];
        state[0] = nextId;
        int i = 1;
        for (LandParcel parcel : listing) {
            state[i++] = parcel.getId();
            state[i++] = parcel.getSizeSqFt();
            state[i++] = parcel.getPrice();
            state[i++] = parcel.getIronTonnes();
        }
        return state;
    }

    /** @return false if the array is not this build's shape; nothing is changed */
    public boolean restoreListingState(double[] state) {

        if (state == null || state.length < 1 || (state.length - 1) % 4 != 0) {
            return false;
        }

        listing.clear();
        nextId = (int) state[0];

        for (int i = 1; i + 3 < state.length; i += 4) {
            listing.add(new LandParcel(
                    (int) state[i], state[i + 1], state[i + 2], state[i + 3]));
        }
        return true;
    }

    public void reset() {
        listing.clear();
        nextId = 1;
        marketPricePerSqFt = BASE_PRICE_PER_SQ_FT;
        salePricePerSqFt = BASE_PRICE_PER_SQ_FT * (1 + BASE_MARKUP);
    }
}
