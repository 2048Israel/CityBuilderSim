package ham.citybuildersim;

/**
 * The city's strategic reserve of crude oil: what it holds, what it paid for
 * it, and what the player has told it to fill and release (0.7.85, batch O8;
 * runs/spec-oil.md 2.8, the research's 4.5 and Q14).
 *
 * WHY. Crude was a flow until the oil run: the wells shipped what they
 * lifted and the refiners ran what they bought, the month it landed. The
 * research's Tank Farm makes it storable, and "a city-owned one is a
 * strategic reserve: it buys when oil is cheap and releases in a price
 * shock, a lever in the spirit of the game". The refiners' farm is theirs
 * (sectors.Refining, THE TANK FARM); this is the city's.
 *
 * THE ROOM is the Strategic Reserves standing (buildings.json: the Tank
 * Farm's 500,000 m3 of tanks, owned by the city, no sector) - their `stock`
 * litres at Refining.CRUDE_LITRES_PER_TONNE to the tonne, 429,185 t each.
 *
 * FILL (Game.fillReserve(t)): an order for t tonnes, bought in the month's
 * crude market as a buyer (Trade.CITY; Markets.CityTrader) - the wells'
 * crude pro rata with the refiners, the world's for the rest, as a refiner
 * buys - cut to the room; what the room could not take lapses with the
 * month. RELEASE (Game.releaseReserve(t)): t a month offered into the same
 * market as a seller, pro rata with the wells' offers, as long as there is
 * crude to offer; what the buyers here do not take ships at the export
 * price, as the wells' unsold crude does. A fill order stops the release,
 * and a release cancels a fill, so the city never trades with itself.
 *
 * ITS MONEY MOVES AT THE NEXT STRIKE, beside the businesses it traded with
 * (Game.settleReserve()): what it bought from the wells and sold to the
 * refiners is a pool paying a pool in the same window, which the money
 * audit never lists; what it bought from the world and shipped to it
 * crosses the edge, as "- city ReserveFill" and "+ city ReserveSales"
 * (TRADE). The month's trades are carried to the strike in the save.
 *
 * ITS BOOK is at average cost: a fill adds what was paid, a sale takes the
 * share of the book the tonnes sold were. Pure bookkeeping; nothing here
 * moves money - Game pays and journals it.
 */
public final class StrategicReserve implements Markets.CityTrader {

    /** Whether a building is a Strategic Reserve (0.7.85): the city's (no sector), with tanks - crude oil's room. */
    public static boolean isReserve(BuildingsTemplate t) {
        return t != null && !t.isOwnedBySector() && t.getCategory() == BuildingType.HEAVY_INDUSTRY
                && t.stocks(Good.CRUDE) > 0;
    }

    /** Tonnes of crude the city's Strategic Reserves standing hold: their tanks' litres at a tonne of crude's (sectors.Refining.CRUDE_LITRES_PER_TONNE). */
    public static double room(BuildingManager buildings) {
        if (buildings == null) return 0;
        double litres = 0;
        for (BuildingsTemplate t : buildings.getTemplates()) {
            if (isReserve(t)) litres += buildings.getQuantity(t.getId()) * t.stocks(Good.CRUDE);
        }
        return litres / ham.citybuildersim.sectors.Refining.CRUDE_LITRES_PER_TONNE;
    }

    /* ----- the state, saved (DataSave.reserve) ----- */

    /** Tonnes of crude held, and what they cost the city (its book, at average cost). */
    private double tonnes, cost;

    /** Tonnes a month to release, standing until changed; and tonnes ordered to fill, for the next clearing. */
    private double release, fill;

    /** The month's trades, carried to the next strike: money and tonnes bought at home and abroad, sold at home and shipped. */
    private double boughtHome, boughtHomeTonnes, boughtAbroad, boughtAbroadTonnes;
    private double soldHome, soldHomeTonnes, soldAbroad, soldAbroadTonnes;

    /** What the last strike settled: paid for crude bought abroad, and taken in for crude shipped - the audit's two lines - and the whole of each side. */
    private double settledImports, settledExports, settledBought, settledSold;

    /** The room the clearing may fill to, told by the city before the month's markets (Game.finalUpdateEconomy()). */
    private double roomNow;

    public double getTonnes()  { return tonnes; }
    public double getCost()    { return cost; }
    public double getRelease() { return release; }
    public double getFill()    { return fill; }

    /** The book a tonne: what the crude held cost, over the tonnes; 0 when empty. */
    public double costPerTonne() { return tonnes > 0 ? cost / tonnes : 0; }

    /** The month's trades not yet settled: bought at home, bought abroad, sold at home, shipped - money. */
    public double getBoughtHome()   { return boughtHome; }
    public double getBoughtAbroad() { return boughtAbroad; }
    public double getSoldHome()     { return soldHome; }
    public double getSoldAbroad()   { return soldAbroad; }
    /** ...and in tonnes. */
    public double getBoughtHomeTonnes()   { return boughtHomeTonnes; }
    public double getBoughtAbroadTonnes() { return boughtAbroadTonnes; }
    public double getSoldHomeTonnes()     { return soldHomeTonnes; }
    public double getSoldAbroadTonnes()   { return soldAbroadTonnes; }

    /** What the last strike settled abroad: paid for the crude bought from the world (the audit's "- city ReserveFill"), taken in for what shipped ("+ city ReserveSales"). */
    public double getSettledImports() { return settledImports; }
    public double getSettledExports() { return settledExports; }
    /** ...and each side whole, home and abroad: what the treasury paid, and took in. */
    public double getSettledBought()  { return settledBought; }
    public double getSettledSold()    { return settledSold; }

    /* ----- the player's two levers, applied by Game ----- */

    /** An order for `tonnes`, for the next clearing; a release standing is stopped. */
    void orderFill(double tonnes) {
        fill = Math.max(0, tonnes);
        if (fill > 0) release = 0;
    }

    /** `tonnesAMonth` released from the next clearing on; a fill not yet bought is cancelled. */
    void setRelease(double tonnesAMonth) {
        release = Math.max(0, tonnesAMonth);
        if (release > 0) fill = 0;
    }

    /** The room the next clearing may fill to (Game, before the markets clear). */
    void setRoom(double room) { roomNow = Math.max(0, room); }

    /* ----- the city in crude's clearing (Markets.CityTrader) ----- */

    /** Its fill, as far as the room left holds; nothing for any other good. */
    @Override
    public double cityBid(Good g) {
        if (g != Good.CRUDE || !(fill > 0)) return 0;
        return Math.min(fill, Math.max(0, roomNow - tonnes));
    }

    /** Its release, as far as it holds crude; nothing while a fill stands, nothing for any other good. */
    @Override
    public double cityOffer(Good g) {
        if (g != Good.CRUDE || fill > 0 || !(release > 0)) return 0;
        return Math.min(release, tonnes);
    }

    @Override
    public void cityBought(Trade t) {
        if (t == null) return;
        tonnes += t.units();
        cost += t.value();
        fill = Math.max(0, fill - t.units());
        if (t.isImport()) {
            boughtAbroad += t.value();
            boughtAbroadTonnes += t.units();
        } else {
            boughtHome += t.value();
            boughtHomeTonnes += t.units();
        }
    }

    @Override
    public void citySold(Trade t) {
        if (t == null || !(tonnes > 0)) return;
        double units = Math.min(t.units(), tonnes);
        cost -= cost * (units / tonnes);
        tonnes -= units;
        if (!(tonnes > 0)) {
            tonnes = 0;
            cost = 0;
        }
        if (t.isExport()) {
            soldAbroad += t.value();
            soldAbroadTonnes += t.units();
        } else {
            soldHome += t.value();
            soldHomeTonnes += t.units();
        }
    }

    /** What the month's clearing did not fill of the order lapses: the room held no more. */
    @Override
    public void cityCleared(Good g) {
        if (g == Good.CRUDE) fill = 0;
    }

    /**
     * The strike settles the month's trades (Game.settleReserve()): what was
     * bought, and what was sold, each whole and its part abroad, struck for
     * the audit and the treasury, and the month cleared. Returns {bought,
     * sold}: what the treasury pays, and takes in.
     */
    double[] settle() {
        settledBought = boughtHome + boughtAbroad;
        settledSold = soldHome + soldAbroad;
        settledImports = boughtAbroad;
        settledExports = soldAbroad;
        boughtHome = boughtHomeTonnes = boughtAbroad = boughtAbroadTonnes = 0;
        soldHome = soldHomeTonnes = soldAbroad = soldAbroadTonnes = 0;
        return new double[] { settledBought, settledSold };
    }

    /** A currency reform: the book and the month's money, scaled; the tonnes are tonnes. */
    void redenominate(double scale) {
        cost *= scale;
        boughtHome *= scale; boughtAbroad *= scale; soldHome *= scale; soldAbroad *= scale;
        settledImports *= scale; settledExports *= scale; settledBought *= scale; settledSold *= scale;
    }

    /* ----- the save ----- */

    /** The reserve as a save carries it (DataSave.reserve): spec-oil 2.8's {tonnes, cost, release}, the fill ordered, and the month's trades and what the last strike settled. Public fields, for Gson. */
    public static final class State {
        public double tonnes, cost, release, fill;
        public double boughtHome, boughtHomeTonnes, boughtAbroad, boughtAbroadTonnes;
        public double soldHome, soldHomeTonnes, soldAbroad, soldAbroadTonnes;
        public double settledImports, settledExports, settledBought, settledSold;
    }

    public State toState() {
        State s = new State();
        s.tonnes = tonnes; s.cost = cost; s.release = release; s.fill = fill;
        s.boughtHome = boughtHome; s.boughtHomeTonnes = boughtHomeTonnes;
        s.boughtAbroad = boughtAbroad; s.boughtAbroadTonnes = boughtAbroadTonnes;
        s.soldHome = soldHome; s.soldHomeTonnes = soldHomeTonnes;
        s.soldAbroad = soldAbroad; s.soldAbroadTonnes = soldAbroadTonnes;
        s.settledImports = settledImports; s.settledExports = settledExports;
        s.settledBought = settledBought; s.settledSold = settledSold;
        return s;
    }

    /** A save's reserve; null - a save from before 0.7.85 - an empty one, which is what that city had. */
    public void restore(State s) {
        if (s == null) s = new State();
        tonnes = finite(s.tonnes); cost = finite(s.cost); release = finite(s.release); fill = finite(s.fill);
        boughtHome = finite(s.boughtHome); boughtHomeTonnes = finite(s.boughtHomeTonnes);
        boughtAbroad = finite(s.boughtAbroad); boughtAbroadTonnes = finite(s.boughtAbroadTonnes);
        soldHome = finite(s.soldHome); soldHomeTonnes = finite(s.soldHomeTonnes);
        soldAbroad = finite(s.soldAbroad); soldAbroadTonnes = finite(s.soldAbroadTonnes);
        settledImports = finite(s.settledImports); settledExports = finite(s.settledExports);
        settledBought = finite(s.settledBought); settledSold = finite(s.settledSold);
    }

    private static double finite(double v) { return Double.isFinite(v) ? v : 0; }
}
