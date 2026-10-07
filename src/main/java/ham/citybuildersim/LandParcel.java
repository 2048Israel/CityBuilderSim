package ham.citybuildersim;

/**
 * One offer on the market, as the land office lists it: a rectangle of whole blocks against one side of the city, in one of that side's six places, with what its ground holds - its area dry, fresh, sea and forest, and the sites and amounts of the seven resources - and its price.
 *
 * WHAT IT IS FOR
 *
 * Land used to be one fungible number bought a block at a time at a price that
 * rose 2% per block. That is a slider, not a market: there was never a decision
 * to make beyond "yes" or "later".
 *
 * A listing is a decision. Twenty-four offers stand at once, six on each side
 * of the city, different sizes and differently priced, some wet, some with ore
 * under them. Buying the cheapest ground and buying the one with the ore are
 * different moves, and the player has to weigh them against a treasury.
 *
 * A PLACE SINCE 0.7.57 (batch J1b; the project's spec-land.md 2.2). Until then
 * an offer was a size, a price and some iron drawn by a generator seeded with
 * its id, with no coordinates. From 0.7.57 to 0.7.66 it was the band of one
 * of forty lanes of wedges from r1 to r2 plots out, its ground sampled.
 *
 * WHOLE BLOCKS SINCE 0.7.67 (batch M3; the project's spec-grid.md 2.2). It is
 * a rectangle of the block grid (LandGrid, GridOffers): its side, its place on
 * that side (0 to 5, left to right facing out), the level of its blocks and
 * its plots [x0, x1) x [y0, y1). Its ground is the rectangle's plots the city
 * does not own yet, counted plot by plot when it is listed (CityLand.groundOf():
 * the books follow the map, exact at every size), and it holds every field
 * whose centre plot lies on that ground, whole (spec-land star 12), and the
 * sites there of a field the city holds only part of. What the city's square
 * feet grow by is its DRY ground (getSizeSqFt()); its water is owned too and
 * priced lower.
 *
 * An offer is IMMUTABLE once listed. What is listed stays listed at the price
 * it was listed at: the city can save up for the big one without it drifting
 * out of reach, and a reload cannot reroll it. Its ground cannot change while
 * it stands either: standing offers never meet, and a purchase claims only its
 * own rectangle's plots.
 *
 * PRICED IN US DOLLARS (0.7.6). Jerus: "when you buy land, make it so that it
 * costs USD not domestic currency". The seller is the world, as it always
 * implicitly was, and the world is paid in its own money: the dollar price is
 * what is fixed at listing, and what the treasury pays in local money is
 * that price times the rate on the day it buys (localPrice()) - so a weak
 * currency makes the same ground dear and a strong one cheap.
 */
public final class LandParcel {

    private final int id;
    private final int side, place, level;
    private final long x0, y0, x1, y1;
    private final double[] km2;
    private final int[] sites;
    private final double[] amounts;

    /**
     * What the city pays, in thousands of US DOLLARS since 0.7.6. Fixed the
     * moment it is listed; a currency reform cannot reach it.
     */
    private final double priceUsd;

    /** The month it was listed in. */
    private final int listedMonth;

    /** The share of an offer's area that, when sea, tags it "mostly sea": 70% - listed, because sea is cheap, but never the best value (spec-land 2.2). */
    public static final double MOSTLY_SEA = 0.70;

    /**
     * An offer: its id, its side (0 north, 1 east, 2 south, 3 west), its
     * place on that side (0 to 5, left to right facing out), the level of its
     * blocks and its rectangle of plots [x0, x1) x [y0, y1), its five areas in
     * square kilometres (CityLand.TOTAL, DRY, FRESH, SEA, FOREST), each
     * resource's sites and amount in Resource's order, its dollar price and
     * the month it was listed.
     */
    public LandParcel(int id, int side, int place, int level, long x0, long y0, long x1, long y1, double[] km2,
                      int[] sites, double[] amounts, double priceUsd, int listedMonth) {
        this.id = id;
        this.side = side;
        this.place = place;
        this.level = level;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.km2 = new double[CityLand.AREAS];
        this.sites = new int[CityLand.KINDS];
        this.amounts = new double[CityLand.KINDS];
        for (int a = 0; a < CityLand.AREAS && km2 != null && a < km2.length; a++) this.km2[a] = Math.max(0, km2[a]);
        for (int k = 0; k < CityLand.KINDS && sites != null && k < sites.length; k++) this.sites[k] = Math.max(0, sites[k]);
        for (int k = 0; k < CityLand.KINDS && amounts != null && k < amounts.length; k++) this.amounts[k] = Math.max(0, amounts[k]);
        this.priceUsd = Math.max(0, priceUsd);
        this.listedMonth = listedMonth;
    }

    /**
     * Dry ground with no place - what a harness hands the office to price by
     * hand: `sizeSqFt` of it, its iron as given, in North's first place on no
     * ground of the grid (an empty rectangle).
     */
    public LandParcel(int id, double sizeSqFt, double priceUsd, double ironTonnes, int deposits) {
        this(id, 0, 0, 0, 0, 0, 0, 0, dryOnly(sizeSqFt), ironOnly(deposits), ironAmount(ironTonnes), priceUsd, 0);
    }

    private static double[] dryOnly(double sqFt) {
        double k = Math.max(0, sqFt) / LandManager.SQ_FT_PER_KM2;
        double[] a = new double[CityLand.AREAS];
        a[CityLand.TOTAL] = k;
        a[CityLand.DRY] = k;
        return a;
    }

    private static int[] ironOnly(int deposits) {
        int[] s = new int[CityLand.KINDS];
        s[Resource.IRON.ordinal()] = deposits;
        return s;
    }

    private static double[] ironAmount(double tonnes) {
        double[] a = new double[CityLand.KINDS];
        a[Resource.IRON.ordinal()] = tonnes;
        return a;
    }

    public int getId()             { return id; }

    /** Its side: 0 north, 1 east, 2 south, 3 west. */
    public int getSide()           { return side; }

    /** Its place on that side, 0 to 5, left to right facing out (GridOffers.PLACES). */
    public int getPlace()          { return place; }

    /** The level of its blocks: 2^level plots a side (LandGrid). */
    public int getLevel()          { return level; }

    /** Its rectangle of plots, half-open: its west edge... */
    public long getX0()            { return x0; }

    /** ...its north edge... */
    public long getY0()            { return y0; }

    /** ...one past its east edge... */
    public long getX1()            { return x1; }

    /** ...and one past its south edge. */
    public long getY1()            { return y1; }

    /** Whether its rectangle holds plot (x, y). */
    public boolean contains(long x, long y) { return x >= x0 && x < x1 && y >= y0 && y < y1; }

    /** Its blocks across its side. */
    public long blocksAcross()     { return ((side == 0 || side == 2) ? x1 - x0 : y1 - y0) >> level; }

    /** ...and deep, outward. */
    public long blocksDeep()       { return ((side == 0 || side == 2) ? y1 - y0 : x1 - x0) >> level; }

    /** Its rectangle as GridOffers stands it. */
    GridOffers.Rect rect()         { return new GridOffers.Rect(side, place, level, x0, y0, x1, y1); }

    /** The month it was listed in. */
    public int getListedMonth()    { return listedMonth; }

    /** One of its areas, in square kilometres: CityLand.TOTAL, DRY, FRESH, SEA or FOREST. */
    public double getKm2(int area) { return km2[area]; }

    /** Its whole area, in square kilometres. */
    public double getKm2()         { return km2[CityLand.TOTAL]; }

    /** Its dry ground, in square kilometres. */
    public double getDryKm2()      { return km2[CityLand.DRY]; }

    /** Its sites of a resource. */
    public int getSites(Resource r)      { return sites[r.ordinal()]; }

    /** Its amount of a resource, in the resource's unit. */
    public double getAmount(Resource r)  { return amounts[r.ordinal()]; }

    /** What the city's ground grows by when it is bought: its dry ground, in square feet. */
    public double getSizeSqFt()    { return km2[CityLand.DRY] * LandManager.SQ_FT_PER_KM2; }

    /** The listed price, in thousands of US dollars. */
    public double getPriceUsd()    { return priceUsd; }

    /** Its iron ore, in tonnes: the fields of iron whose centres lie on its ground. */
    public double getIronTonnes()  { return amounts[Resource.IRON.ordinal()]; }

    /** Its iron sites: how many more mines it lets the city stand. */
    public int getDeposits()       { return sites[Resource.IRON.ordinal()]; }

    public boolean hasIron()       { return getIronTonnes() > 0 && getDeposits() > 0; }

    /** Whether it is mostly sea: more than MOSTLY_SEA of its area. Listed, never the best value. */
    public boolean isMostlySea()   { return km2[CityLand.SEA] > MOSTLY_SEA * km2[CityLand.TOTAL]; }

    /** Its side and place as the office names them: "North 3", the place counted 1 to 6. */
    public String where()          { return CityLand.sideName(side) + " " + (place + 1); }

    /**
     * What the treasury pays for it in local money at this rate - local money
     * per US dollar, ForeignAccounts.getRate() on the day it buys.
     */
    public double localPrice(double rate) { return priceUsd * rate; }

    /**
     * Its dollars a square foot of DRY ground (0.7.57): what the office
     * ranks offers by. An offer with no dry ground is infinitely dear by it.
     * In US dollars, like the price; every offer on the shelf is converted
     * at the same rate, so the ranking is the same in either money.
     */
    public double getUsdPerSqFt() {
        double sqFt = getSizeSqFt();
        return sqFt > 0 ? priceUsd / sqFt : Double.POSITIVE_INFINITY;
    }

    /** Its dry square kilometres a thousand US dollars: what the office's best value is the most of. */
    public double getDryKm2PerUsd() {
        return priceUsd > 0 ? km2[CityLand.DRY] / priceUsd : (km2[CityLand.DRY] > 0 ? Double.POSITIVE_INFINITY : 0);
    }

    /** How many city blocks' worth of dry ground, for a player who thinks in blocks. */
    public double getBlocks() {
        return getSizeSqFt() / LandManager.BLOCK_SQ_FT;
    }

    /**
     * A one-line label for the listing.
     *
     * Ore is quoted in thousands of tonnes because the deposits are large and
     * "1,400,000 t" is a number nobody reads carefully.
     */
    public String describe() {
        String base = String.format("%s: %s, %s dry - US$%,.0f", where(),
                LandManager.areaWords(getKm2() * LandManager.SQ_FT_PER_KM2), LandManager.areaWords(getSizeSqFt()), priceUsd);
        if (!hasIron()) return base;

        // The site count leads, because it is the number that decides how many
        // mines this ground is worth. The tonnage is centuries deep either way.
        return base + (getDeposits() > 1
                ? String.format("  [%d iron sites: %,.0fk tonnes]", getDeposits(), getIronTonnes() / 1000)
                : String.format("  [iron: %,.0fk tonnes]", getIronTonnes() / 1000));
    }

    /* =====================================================================
       ITS RECORDS (DataSave's landOffers and landHoldings, SAVE_FORMAT 32)
       ===================================================================== */

    /** Where a record keeps the rectangle: side, place, level, x0, y0, x1, y1 - seven. */
    static final int RECT_FIELDS = 7;

    /** How wide an offer's record is: its id, its rectangle (side, place, level, x0, y0, x1, y1), its five areas, its sites, its amounts, its price and the month it was listed - 29. */
    public static final int OFFER_FIELDS = 1 + RECT_FIELDS + CityLand.AREAS + 2 * CityLand.KINDS + 2;

    /** How wide a holding's record is: its rectangle, the month bought, the price, what was paid here, the five areas, the sites, the amounts, and the offer's id and month listed - 31 (spec-grid 3, M3). */
    public static final int PURCHASE_FIELDS = RECT_FIELDS + 3 + CityLand.AREAS + 2 * CityLand.KINDS + 2;

    /** Its record as an offer standing. */
    public double[] offerRow() {
        double[] row = new double[OFFER_FIELDS];
        int i = 0;
        row[i++] = id;
        i = rectInto(row, i);
        i = contents(row, i);
        row[i++] = priceUsd;
        row[i] = listedMonth;
        return row;
    }

    /** An offer from its record; null when the record is the wrong width. */
    public static LandParcel fromOfferRow(double[] row) {
        if (row == null || row.length != OFFER_FIELDS) return null;
        double[] km2 = new double[CityLand.AREAS];
        int[] sites = new int[CityLand.KINDS];
        double[] amounts = new double[CityLand.KINDS];
        int i = read(row, 1 + RECT_FIELDS, km2, sites, amounts);
        return new LandParcel((int) row[0], (int) row[1], (int) row[2], (int) row[3], (long) row[4], (long) row[5],
                (long) row[6], (long) row[7], km2, sites, amounts, row[i], (int) row[i + 1]);
    }

    /** Its record as a holding: the month it was bought in and what the treasury paid for it in local money. */
    double[] purchaseRow(int month, double paidLocal) {
        double[] row = new double[PURCHASE_FIELDS];
        int i = rectInto(row, 0);
        row[i++] = month;
        row[i++] = priceUsd;
        row[i++] = paidLocal;
        i = contents(row, i);
        row[i++] = id;
        row[i] = listedMonth;
        return row;
    }

    /** The offer a holding's record bought; null when the record is the wrong width. */
    static LandParcel fromPurchaseRow(double[] row) {
        if (row == null || row.length != PURCHASE_FIELDS) return null;
        double[] km2 = new double[CityLand.AREAS];
        int[] sites = new int[CityLand.KINDS];
        double[] amounts = new double[CityLand.KINDS];
        int i = read(row, RECT_FIELDS + 3, km2, sites, amounts);
        return new LandParcel((int) row[i], (int) row[0], (int) row[1], (int) row[2], (long) row[3], (long) row[4],
                (long) row[5], (long) row[6], km2, sites, amounts, row[RECT_FIELDS + 1], (int) row[i + 1]);
    }

    /** The month a holding's record was bought in. */
    static int purchaseMonth(double[] row)   { return (int) row[RECT_FIELDS]; }

    /** What the treasury paid for it, in local money. */
    static double purchasePaid(double[] row) { return row[RECT_FIELDS + 2]; }

    private int rectInto(double[] row, int i) {
        row[i++] = side;
        row[i++] = place;
        row[i++] = level;
        row[i++] = x0;
        row[i++] = y0;
        row[i++] = x1;
        row[i++] = y1;
        return i;
    }

    private int contents(double[] row, int i) {
        for (int a = 0; a < CityLand.AREAS; a++) row[i++] = km2[a];
        for (int k = 0; k < CityLand.KINDS; k++) row[i++] = sites[k];
        for (int k = 0; k < CityLand.KINDS; k++) row[i++] = amounts[k];
        return i;
    }

    private static int read(double[] row, int i, double[] km2, int[] sites, double[] amounts) {
        for (int a = 0; a < CityLand.AREAS; a++) km2[a] = row[i++];
        for (int k = 0; k < CityLand.KINDS; k++) sites[k] = (int) row[i++];
        for (int k = 0; k < CityLand.KINDS; k++) amounts[k] = row[i++];
        return i;
    }

    /** Whether two offers are the same, field for field. */
    public boolean same(LandParcel o) {
        return o != null && java.util.Arrays.equals(offerRow(), o.offerRow());
    }
}
