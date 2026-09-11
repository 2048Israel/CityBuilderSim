package ham.citybuildersim;

/**
 * One fill: somebody sold somebody some units of a good at a price.
 *
 * The month's trades are the ledger everything downstream reads. A sector's
 * revenue is the trades it sold, its input cost the trades it bought; the
 * sales tax is struck from who sold to whom (a local sale at the seller's
 * rate, an export zero-rated, an import charged to the buyer); the balance
 * of payments and the national accounts read the trades with the world.
 * Nothing is computed twice, so nothing can disagree about a month - which
 * is the rule this codebase kept being caught breaking while every sector
 * booked its own version of the same sale.
 *
 * SELLER AND BUYER ARE KEYS, not objects: a sector's saved name, or one of
 * the three parties that are not sectors - WORLD, HOUSEHOLDS, CITY. Money is
 * in thousands, as everywhere.
 *
 * @param units    what changed hands, in the good's own unit
 * @param price    per unit, in the city's money, as traded
 */
public record Trade(Good good, String seller, String buyer, double units, double price) {

    /** The other side of every export and every import. */
    public static final String WORLD = "world";

    /** The households, as a buyer of the basket. */
    public static final String HOUSEHOLDS = "households";

    /** The treasury, as a buyer of its own buildings' materials and repairs. */
    public static final String CITY = "city";

    public double value() { return units * price; }

    public boolean isExport() { return WORLD.equals(buyer); }
    public boolean isImport() { return WORLD.equals(seller); }

    /** Sold to a local buyer - a sector, the households or the city. */
    public boolean isLocalSale() { return !isExport() && !isImport(); }
}
