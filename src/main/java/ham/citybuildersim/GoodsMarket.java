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

    /** What an import costs the city, in the city's money. NaN when the good cannot be imported. */
    public double importPrice() {
        return good.importable() ? good.worldImportPrice() * exchangeRate : Double.NaN;
    }

    /** What the world pays the city for one, in the city's money. NaN when it will not buy. */
    public double exportPrice() {
        return good.exportable() ? good.worldExportPrice() * exchangeRate : Double.NaN;
    }

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

    /** The price a month traded at, put back on load - restored, never recomputed. */
    public void setLocalPrice(double price) {
        if (price > 0 && Double.isFinite(price)) localPrice = price;
    }

    /** The last strike's inputs, put back so the screens read the saved month. */
    public void restoreStrike(double flow, double stock, double demand) {
        rSupplyFlow = Math.max(0, flow);
        rSupplyStock = Math.max(0, stock);
        rDemand = Math.max(0, demand);
    }

    public void reset() {
        localPrice = openingPrice();
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
