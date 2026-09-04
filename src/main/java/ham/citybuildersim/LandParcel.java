package ham.citybuildersim;

/**
 * One plot on the market, as the land office lists it.
 *
 * WHAT IT IS FOR
 *
 * Land used to be one fungible number bought a block at a time at a price that
 * rose 2% per block. That is a slider, not a market: there was never a decision
 * to make beyond "yes" or "later".
 *
 * A listing is a decision. Ten plots are on offer at once, all different sizes,
 * all differently priced, and some of them have iron under them. Buying the
 * cheap one and buying the one with the ore are different moves, and the player
 * has to weigh them against a treasury.
 *
 * WHAT IT IS NOT
 *
 * Not a location. Nothing here has coordinates and no building sits on a
 * specific parcel - the square footage joins the city's pool on purchase,
 * exactly as an annexed block used to. This is a market, not a map.
 *
 * A parcel is IMMUTABLE once listed. What is listed stays listed at the price
 * it was listed at: the city can save up for the big one without it drifting
 * out of reach, and a reload cannot reroll the offers into something better.
 */
public final class LandParcel {

    private final int id;
    private final double sizeSqFt;

    /** What the city pays, in thousands. Fixed the moment it is listed. */
    private final double price;

    /**
     * Iron ore in the ground, in tonnes, across all of this parcel's deposits.
     *
     * The ore is not extracted by owning it - it needs an Iron Mine standing on
     * a deposit, and the deposit COUNT is what caps how many mines the city can
     * run. The tonnage is a single pool they all draw from.
     */
    private final double ironTonnes;

    /**
     * How many separate deposit sites are on this plot. Usually one, sometimes
     * more on a large parcel, zero on most.
     *
     * A parcel used to be worth exactly one mine or none, because LandManager
     * did `ironDeposits++` on purchase. That made every ore parcel identical in
     * the way that actually mattered - the tonnage varied, but the tonnage is
     * centuries deep either way, so what a deposit is really worth is the mine
     * it lets you stand up. Now a big tract can be worth several, which is the
     * difference between "some ore" and "a mining district".
     */
    private final int deposits;

    public LandParcel(int id, double sizeSqFt, double price,
                      double ironTonnes, int deposits) {
        this.id = id;
        this.sizeSqFt = Math.max(0, sizeSqFt);
        this.price = Math.max(0, price);
        this.ironTonnes = Math.max(0, ironTonnes);
        this.deposits = Math.max(0, deposits);
    }

    /**
     * Pre-multi-deposit parcels: any ore at all meant exactly one site.
     *
     * Kept so a save written before deposits were counted restores as the plot
     * the player was actually looking at, rather than being discarded.
     */
    public LandParcel(int id, double sizeSqFt, double price, double ironTonnes) {
        this(id, sizeSqFt, price, ironTonnes, ironTonnes > 0 ? 1 : 0);
    }

    public int getId()             { return id; }
    public double getSizeSqFt()    { return sizeSqFt; }
    public double getPrice()       { return price; }
    public double getIronTonnes()  { return ironTonnes; }
    public int getDeposits()       { return deposits; }

    public boolean hasIron()       { return ironTonnes > 0 && deposits > 0; }

    /** For comparing offers, which is the whole point of listing ten at once. */
    public double getPricePerSqFt() {
        return (sizeSqFt > 0) ? price / sizeSqFt : 0;
    }

    /** How many city blocks' worth, for a player who thinks in blocks. */
    public double getBlocks() {
        return sizeSqFt / LandManager.BLOCK_SQ_FT;
    }

    /**
     * A one-line label for the listing.
     *
     * Ore is quoted in thousands of tonnes because the deposits are large and
     * "1,400,000 t" is a number nobody reads carefully.
     */
    public String describe() {
        String base = String.format("%,.0f sq ft (%.1f blocks) - $%,.0f",
                sizeSqFt, getBlocks(), price);
        if (!hasIron()) return base;

        // The site count leads, because it is the number that decides how many
        // mines this plot is worth. The tonnage is centuries deep either way.
        return base + (deposits > 1
                ? String.format("  [%d iron deposits: %,.0fk tonnes]", deposits, ironTonnes / 1000)
                : String.format("  [iron: %,.0fk tonnes]", ironTonnes / 1000));
    }
}
