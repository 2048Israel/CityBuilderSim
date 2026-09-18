package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * Where one good clears between whoever makes it and whoever wants it.
 *
 * THE GENERALISATION OF FoodMarket AND IronMarket, which it replaces. Both of
 * those priced a month off scarcity inside a band the world set; they
 * differed in the shape of the band and the formula in it, and the ore
 * market's version won on both counts:
 *
 *     floor    = the export price   (what a seller gets shipping it out)
 *     ceiling  = the import price   (what a buyer pays bringing one in)
 *     price    = floor + (ceiling - floor) x demand / (demand + supply)
 *
 * The formula is symmetric and needs no clamping: a matched pair splits the
 * difference and both make money, which is the property the ore band was
 * designed around and the food market never had - it priced a balanced
 * market at the buyer's worst price, and floored itself at a quarter of the
 * ceiling while the mills were already exporting at six tenths of it. One
 * rule for every good now; if the eight seeds say food needs its old floor
 * back it becomes a per-good setting, not a second market (Jerus: "onto the
 * band, measure").
 *
 * SUPPLY IS FLOW PLUS A SLICE OF THE STOCK, not the raw stockpile. Pricing
 * off inventory alone is circular - a low price makes a maker withhold,
 * withholding grows the stock, the bigger stock reads as more supply and
 * pushes the price down again - and measured that way the food price sat on
 * its floor in every scenario including an actual shortage. A warehouse can
 * be drawn down, but not all at once: a sixth of it a month counts.
 *
 * A GOOD WITH NO CEILING - one the world will not sell the city - has none
 * from this class either; it is priced between the floor and twice the
 * floor, which is a guess that matters for no good in the game today (steel
 * is the only one, and nothing in the city buys steel). A good with no floor
 * either is a seller-priced good and never comes here; see Good.Pricing.
 *
 * THE ALLOCATION IS IN Markets, which can see every seller's offer and every
 * buyer's bid at once. This class is one price and one month's tally.
 *
 * NOTHING HERE MOVES MONEY. It strikes a price, records what was filled, and
 * the sectors' own ledgers carry the trades to their statements.
 */
public final class GoodsMarket {

    /**
     * How many months it would take to release the whole stockpile into the
     * market. FoodMarket's figure, kept.
     */
    public static final double STOCK_RELEASE_MONTHS = 6;

    /** Where the price sits in a band with no ceiling: up to this multiple of the floor. */
    private static final double NO_CEILING_MULTIPLE = 2;

    private final Good good;

    /** City money per world unit - the exchange rate times the world's price level. */
    private double exchangeRate = 1;

    /** The price the month trades at, in the city's money. */
    private double localPrice;

    /* --------------------------- the last strike --------------------------- */
    private double rSupplyFlow;
    private double rSupplyStock;
    private double rDemand;

    /**
     * What the city has been taking of this good, month by month for the
     * last year - the figure a maker PLANS against is the average. A
     * month's demand is lumpy for anything drawn on order rather than bid
     * for monthly: the founding city's two hundred houses drew three
     * thousand units of building material in one month and nothing the
     * next, and a plant sized to that month is a plant that idles for
     * years. Each month is the larger of what was wanted and what was
     * actually taken, local or imported. A plain window rather than a
     * decaying average so a one-month burst is gone a year later rather
     * than lingering; the months before the city existed count as
     * nothing, which is what they were. Saved.
     */
    private final double[] taken = new double[TREND_MONTHS];
    private int takenAt;

    /**
     * The longest window any good plans over - see Good.planningMonths() for
     * how many of these months a good actually reads.
     */
    public static final int TREND_MONTHS = 36;

    /* ----------------------------- the month ----------------------------- */
    private double offered;
    private double bid;
    private double localFilled;
    private double imported;
    private double exported;
    private final List<Trade> trades = new ArrayList<>();

    /**
     * Units taken on demand since the last clearing - the builders' orders,
     * the repair bill - which count as demand when the month is next priced.
     * Kept apart from the month's tally because a draw happens at any time
     * and the tally is wiped when the clearing starts.
     */
    private double drawn;

    public GoodsMarket(Good good) {
        this.good = good;
        this.localPrice = openingPrice();
    }

    public Good good() { return good; }

    /** City money per dollar. Pushed in each month by EconomyManager, before anything is priced. */
    public void setExchangeRate(double rate) {
        this.exchangeRate = rate > 0 ? rate : 1;
    }

    public double getExchangeRate() { return exchangeRate; }

    /* =======================================================================
       WHAT IT COSTS TO MOVE ONE, THIS MONTH (2026-09-16)

       Good.baseFreight() is what a lorry charges, and it is the freight in
       every price this game has ever quoted. This is what the city is ACTUALLY
       paying now - a rail network undercutting the lorries pulls it down, a
       rail network that is full lets it back up - and the band moves with it.

       ON THE MARKET AND NOT ON THE GOOD, which is not a style preference. Good
       is an enum, so anything mutable on it is shared by every Game in the
       process, and half the harnesses in this suite run two cities side by
       side on purpose - InfrastructureCheck's withRoads and without,
       DenominationCheck's reformed twin, every ForeignCheck parity pair. A
       freight quote living on the enum would have leaked between them.

       THE BAND MOVES BY THE CHANGE, NOT BY RECOMPUTING THE ENDS, and that is
       what makes a city with no rail bit-identical to one from before any of
       this existed. worldBuyPrice() + freight does NOT reliably come back to
       the stored literal - iron comes back 0.4099999999999999 and snacks
       0.009999999999999998, two of the seven goods tested - but adding a delta
       of exactly zero is exact for every finite number there is. So a city
       whose freight has not moved is quoting the literal it always quoted, by
       construction rather than by luck.
       ======================================================================= */

    /** What a unit's freight costs today, as a share of what a lorry charges. */
    private double freightFactor = 1;

    /**
     * @param factor the share of the city's freight on this good the LORRIES
     *               still carry, and therefore the share of baseFreight() left
     *               in the world-facing price. One when there is no railway.
     *
     * ZERO IS LEGAL AND IT IS NOT "FREE FREIGHT". A railway carrying all of a
     * good bills the shipper itself, at home, at its own quote - see
     * sectors.Rail.haul(). What leaves this band is the money that used to go
     * abroad with the cargo; what replaces it is a domestic invoice. Jerus:
     * "the freight cant reach zero cause freight needs profit" - and it does
     * not: Rail.RAIL_FLOOR is what stops the CHARGE falling, and it lives on
     * the railway's price rather than on this band, which is only about who
     * is owed the money.
     */
    public void setFreightFactor(double factor) {
        this.freightFactor = Double.isFinite(factor) && factor >= 0 ? Math.min(1, factor) : 1;
    }

    public double getFreightFactor() { return freightFactor; }

    /**
     * WHAT THE SHIPPER STILL PAYS AT HOME, per unit, as a share of the lorry
     * rate - the railway's own invoice. Zero in a city with no railway.
     *
     * THE TWO NUMBERS TOGETHER ARE THE WHOLE FREIGHT BILL and neither is it on
     * its own, which is the thing to understand about this pair. The band above
     * is what crosses the boundary and the world is paid for; this is what the
     * city's own railway charges to get the cargo to the boundary. A tonne of
     * steel with half the network's freight on rail at a 60% quote reads
     *
     *      freightFactor .50   still in the band, paid abroad
     *      railCharge    .30   billed at home, by sectors.Rail
     *      -----------------
     *      .80 of what a lorry alone would have cost
     *
     * and a screen or a decision that reads only the first of the two thinks
     * the city's freight got twice as cheap as it did. See netExportPrice().
     */
    private double railCharge;

    public void setRailCharge(double shareOfLorryRate) {
        this.railCharge = Double.isFinite(shareOfLorryRate) && shareOfLorryRate > 0
                ? Math.min(1, shareOfLorryRate) : 0;
    }

    public double getRailCharge() { return railCharge; }

    /** The railway's charge on one unit, in city money. Exactly zero with no railway. */
    public double domesticFreight() { return good.baseFreight() * railCharge * exchangeRate; }

    /** What the freight on one unit has moved by, in the world's money. Exactly zero at rest. */
    public double freightChange() {
        return good.baseFreight() * (freightFactor - 1);
    }

    /** What an import costs the city, in the city's money. NaN when the good cannot be imported. */
    public double importPrice() {
        return good.importable()
                ? (good.worldImportPrice() + freightChange()) * exchangeRate : Double.NaN;
    }

    /** What the world pays the city for one, in the city's money. NaN when it will not buy. */
    public double exportPrice() {
        return good.exportable()
                ? (good.worldExportPrice() - freightChange()) * exchangeRate : Double.NaN;
    }

    /* =======================================================================
       ...AND WHAT THE SHIPPER IS ACTUALLY LEFT WITH

       The two above are the prices AT THE BOUNDARY - free on board, in the
       trade statistician's language - and they are the right figures for the
       trade itself, for the balance of payments and for the money audit,
       because they are what actually crosses the edge. They are the WRONG
       figures for a decision, because the shipper has a second invoice coming
       from its own city's railway.

       So every place that asks "is this worth exporting" or "what will this
       import cost me" reads the pair below, and every place that settles a
       trade reads the pair above. Getting that backwards is not a rounding
       error: measured at a 92% quote on 92% of the traffic, the band alone
       said steel's wedge had fallen from 52% to 11% when what the shipper
       actually saved was eight per cent.

       EXACTLY THE GROSS PRICE WHEN THERE IS NO RAILWAY, by the same
       construction as everything else here: railCharge is zero, so
       domesticFreight() is a product with a zero in it, and subtracting zero
       is exact for every finite number there is.
       ======================================================================= */

    /** What an exporter nets on one, after the haulage it will be billed for. */
    public double netExportPrice() { return exportPrice() - domesticFreight(); }

    /** ...and what an importer pays for one, landed AND hauled. */
    public double netImportPrice() { return importPrice() + domesticFreight(); }

    /** The floor: the export price, or nothing. */
    public double floor() {
        return good.exportable() ? exportPrice() : 0;
    }

    /** The ceiling: the import price, or twice the floor. */
    public double ceiling() {
        if (good.importable()) return importPrice();
        return floor() * NO_CEILING_MULTIPLE;
    }

    /** The middle of the band: where a market nobody has told about anything opens. */
    private double openingPrice() {
        return (floor() + ceiling()) / 2;
    }

    /**
     * Prices the month.
     *
     * @param productionFlow what the makers will bring this month
     * @param stock          what they already hold
     * @param demand         what the buyers intend to take
     */
    public void strike(double productionFlow, double stock, double demand) {

        double supply = Math.max(0, productionFlow) + Math.max(0, stock) / STOCK_RELEASE_MONTHS;
        rSupplyFlow = Math.max(0, productionFlow);
        rSupplyStock = Math.max(0, stock);
        rDemand = Math.max(0, demand);

        double position;
        if (supply <= 0 && rDemand <= 0) {
            position = .5;                      // nothing either side - the middle
        } else if (supply <= 0) {
            position = 1;                       // no makers - buyers pay the ceiling
        } else if (rDemand <= 0) {
            position = 0;                       // no takers - makers get the floor
        } else {
            position = rDemand / (rDemand + supply);
        }

        double lo = floor(), hi = ceiling();
        localPrice = lo + (hi - lo) * position;
    }

    /** Wipes the month's tally, not the draws. Called by Markets as this good's clearing starts. */
    public void startMonth() {
        offered = 0;
        bid = 0;
        localFilled = 0;
        imported = 0;
        exported = 0;
        trades.clear();
    }

    /** A draw since the last clearing, counted toward the next strike's demand. */
    void noteDrawn(double units) { drawn += Math.max(0, units); }

    /**
     * Closes the month's clearing: folds what the city took - the larger of
     * what it asked for and what it got, at home or from the world - into
     * the trend. Called by Markets once every fill is in.
     */
    void closeMonth() {
        taken[takenAt] = Math.max(rDemand, localFilled + imported);
        takenAt = (takenAt + 1) % TREND_MONTHS;
    }

    /** The average take of this good a month, over the months the good plans on. */
    public double getDemandTrend() {
        int months = Math.min(TREND_MONTHS, Math.max(1, good.planningMonths()));
        double sum = 0;
        for (int i = 1; i <= months; i++) {
            sum += taken[(takenAt - i + TREND_MONTHS) % TREND_MONTHS];
        }
        return sum / months;
    }

    /** The year, as the save carries it: oldest first. */
    public double[] getTakenHistory() {
        double[] out = new double[TREND_MONTHS];
        for (int i = 0; i < TREND_MONTHS; i++) out[i] = taken[(takenAt + i) % TREND_MONTHS];
        return out;
    }

    /** The year, put back on load. A shorter or missing record restores what it holds. */
    public void restoreTakenHistory(double[] saved) {
        java.util.Arrays.fill(taken, 0);
        takenAt = 0;
        if (saved == null) return;
        int n = Math.min(saved.length, TREND_MONTHS);
        for (int i = 0; i < n; i++) taken[i] = Math.max(0, saved[saved.length - n + i]);
        takenAt = n % TREND_MONTHS;
    }

    /** ...and the strike takes them. */
    double takeDrawn() {
        double d = drawn;
        drawn = 0;
        return d;
    }

    public double getDrawn() { return drawn; }

    /** A fill, recorded. The sectors' own ledgers carry it from here. */
    public Trade record(String seller, String buyer, double units, double price) {
        if (!(units > 0)) return null;
        Trade t = new Trade(good, seller, buyer, units, price);
        trades.add(t);
        if (t.isImport())         imported += units;
        else if (t.isExport())    exported += units;
        else                      localFilled += units;
        return t;
    }

    void noteOffered(double units) { offered += Math.max(0, units); }
    void noteBid(double units)     { bid += Math.max(0, units); }

    /* ------------------------------ readers ------------------------------ */

    public double getLocalPrice()   { return localPrice; }
    public double getSupplyFlow()   { return rSupplyFlow; }
    public double getSupplyStock()  { return rSupplyStock; }
    /** Flow plus the slice of stock the month was priced on. */
    public double getSupply()       { return rSupplyFlow + rSupplyStock / STOCK_RELEASE_MONTHS; }
    public double getDemand()       { return rDemand; }

    public double getOffered()      { return offered; }
    public double getBid()          { return bid; }
    public double getLocalFilled()  { return localFilled; }
    public double getImported()     { return imported; }
    public double getExported()     { return exported; }
    public List<Trade> getTrades()  { return trades; }

    /** Where in the band the price sits: 0 the floor, 1 the ceiling. */
    public double getPriceIndex() {
        double band = ceiling() - floor();
        return band > 0 ? (localPrice - floor()) / band : 0;
    }

    /** True when the buyers wanted more than the makers brought. */
    public boolean isShortage() {
        return rDemand > getSupply();
    }

    /**
     * The price a month traded at, put back on load - restored, never
     * recomputed.
     *
     * ZERO IS A PRICE (2026-09-17). This refused anything that was not
     * strictly positive, which was defensiveness against a missing figure and
     * was silently wrong for the goods this game gained on 2026-09-16: VANS
     * and ROLLING_STOCK are the first two the world will NOT buy, so their
     * floor is zero, and strike() sets a good with makers and no takers to
     * exactly its floor. A city that saved a rolling-stock price of $0 - which
     * is every city with a locomotive works and no railway wanting trains -
     * reloaded with the guard dropping it and the OPENING price, mid-band,
     * left in its place.
     *
     * Measured: seed 7 of the van ensemble, month 2484, one month, $1,200.00
     * against $0.00 - the mid-point of a $0-$2,400 band against its floor. It
     * did not cascade, because the next strike re-priced both cities the same
     * way, and it is exactly the kind of one-month difference that is a
     * coincidence away from being a different city.
     *
     * A market absent from the save is not restored at all (Markets.restore
     * iterates what the save carries), so an absent figure and a saved zero
     * were never the same thing and did not need one guard between them. What
     * is refused now is what should always have been refused: a negative price
     * and a NaN.
     */
    public void setLocalPrice(double price) {
        if (price >= 0 && Double.isFinite(price)) localPrice = price;
    }

    /** The last strike's inputs, put back so the screens read the saved month. */
    public void restoreStrike(double flow, double stock, double demand) {
        rSupplyFlow = Math.max(0, flow);
        rSupplyStock = Math.max(0, stock);
        rDemand = Math.max(0, demand);
    }

    public void reset() {
        localPrice = openingPrice();
        // A city with no railway quotes the literal it always quoted. See above.
        freightFactor = 1;
        rSupplyFlow = rSupplyStock = rDemand = 0;
        java.util.Arrays.fill(taken, 0);
        takenAt = 0;
        drawn = 0;
        startMonth();
    }

    /**
     * The price in the new unit. The world's prices are the world's and do
     * not move; the rate they reach the city at has already been divided
     * by ForeignAccounts, and is divided here too so importPrice() agrees.
     */
    public void redenominate(double scale) {
        localPrice *= scale;
        exchangeRate *= scale;
    }
}
