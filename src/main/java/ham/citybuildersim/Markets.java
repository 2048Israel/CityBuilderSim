package ham.citybuildersim;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Every goods market in the city, and the month they clear in.
 *
 * ONE OBJECT PER GOOD, and one pass a month over all of them. The pass is
 * the whole of what used to be EconomyManager.procedureUpdate(),
 * priceFoodMarket(), priceIronMarket(), mineIron(), produceFood(),
 * updateFinalIndustrialHandler() and CommercialHandler.buyInventory(),
 * written once for any good:
 *
 *   1. the flow goods are made - a mine lifts what the ground allows
 *   2. the seller-priced goods are sold - the shops to the households, the
 *      landlords' doors - so a shelf that emptied is restocked below
 *   2b. each buyer is told what it can pay for (0.7.12 round 6; Sector, BUY
 *      ONLY WHAT IT CAN PAY FOR), so an order for stock it cannot pay for
 *      is not placed; a maker's inputs are bought whole
 *   3. each traded good, in turn: priced off what the makers will bring and
 *      what the buyers intend; offered by the makers at that price; bid for
 *      by the users; allocated pro rata both ways; the buyers' shortfall
 *      imported if the world sells it; the makers' unsold flow exported if
 *      the world buys it
 *   4. the stockable goods are made into the warehouses, for next month
 *   5. each sector finishes its month
 *
 * THE CITY TRADES IN ONE MARKET (0.7.85, batch O8; runs/spec-oil.md 2.8):
 * its strategic reserve (StrategicReserve, a CityTrader) bids for crude to
 * fill it and offers crude to release it, in crude's clearing beside the
 * sectors - its fill pro rata with theirs from the wells and the world for
 * the rest, its release pro rata with the wells' offers and shipped, as
 * their unsold crude is, past what the buyers take. A city with no order
 * standing trades nothing, and every figure of the clearing is what it was.
 *
 * PRO RATA BOTH WAYS. With one mill and ten shops, or three mines and one
 * mill, somebody has to decide who gets what. Every buyer gets the same
 * share of its bid and every seller sells the same share of its offer, and
 * each pair is recorded as its own trade so the input-tax credit can be
 * struck at the supplier's rate - see SalesTaxLedger.
 *
 * DRAWS. Building material is not bid for monthly; an order takes it the
 * moment it is placed, out of the city's yard first and then from whoever
 * makes it, and imports the rest. draw() is that, against the same market
 * at the same price, and the units drawn count as demand for next month's
 * strike so a city that builds hard makes materials dear.
 *
 * NOTHING HERE MOVES MONEY. Trades land in the sectors' ledgers and are
 * banked at the strike; see Sector.
 */
public final class Markets {

    private final Map<Good, GoodsMarket> markets = new EnumMap<>(Good.class);

    /**
     * The city as a trader in a market (0.7.85): what it bids for and offers
     * in the month's clearing of a good, and what it bought and sold there -
     * its strategic reserve's crude (StrategicReserve). Read once a clearing;
     * the trades it makes are booked to it as they are recorded, and its
     * money moves at the next strike (Game.settleReserve()), beside the
     * sectors it traded with.
     */
    public interface CityTrader {
        /** Units of a good the city bids for in this month's clearing. */
        double cityBid(Good g);
        /** ...and offers in it. A city never bids and offers the same good in one month. */
        double cityOffer(Good g);
        /** A fill it bought: from a seller here, or the world's. */
        void cityBought(Trade t);
        /** A sale it made: to a buyer here, or shipped abroad. */
        void citySold(Trade t);
        /** The good's clearing is done: whatever of its bid the month did not fill lapses. */
        void cityCleared(Good g);
    }

    /** The city's trader, or null - a harness's bare market (0.7.85). */
    private CityTrader city;

    /** The city's trader (Game.buildWorld()): its strategic reserve. */
    public void setCity(CityTrader trader) { this.city = trader; }

    public Markets() {
        for (Good g : Good.values()) markets.put(g, new GoodsMarket(g));
    }

    public GoodsMarket get(Good g) { return markets.get(g); }

    public Iterable<GoodsMarket> all() { return markets.values(); }

    /** City money per dollar, times the world's price level. Before anything is priced. */
    public void setExchangeRate(double rate) {
        for (GoodsMarket m : markets.values()) m.setExchangeRate(rate);
    }

    /**
     * The one rate every market here was told, read off one of them.
     *
     * READ AND NOT KEPT, deliberately: a second copy of a number is a number
     * that can disagree with the first, and this codebase has been bitten four
     * times by a duplicated calculation. The caller that wants it has no good
     * in mind - the railway pricing a tonne of anything - so it should not have
     * to pick one arbitrarily to ask.
     */
    public double getExchangeRate() {
        for (GoodsMarket m : markets.values()) return m.getExchangeRate();
        return 1;
    }

    /* ===================================================================
       THE MONTH
       =================================================================== */

    public void clearMonth(Sectors sectors, Game game) {

        /*
         * 0. THE FLEETS, before anything is made with them.
         *
         * A month of wear on every sector's lorries, and - once, on the first
         * pass a save from before vans ever runs - the fleet its standing plant
         * implies. First, because the ratio it sets is a multiplier on
         * everything the four steps below do, and because a sector that is
         * handed its opening fleet here is running at its own rate in the same
         * month rather than at zero for one of them. See Sector.runFleet().
         * ...and the month's diesel for them (0.7.83, batch O6), drawn off the
         * refiners' tanks and the world as the railway draws its own.
         */
        for (Sector s : sectors.all()) s.runFleet(sectors);

        // 1. the flow goods are made
        for (Sector s : sectors.all()) {
            for (Good g : s.goodsMade()) {
                if (!g.stockable() && g.traded()) s.produceFlow(g);
            }
        }

        // 2. the seller-priced goods
        for (Sector s : sectors.all()) s.sellOwnPriced(this, game);

        /*
         * 2b. WHAT EACH BUYER CAN PAY FOR (0.7.12 round 6), after the shops
         * have sold, so the month's takings are in it, and before anybody
         * orders. See Sector, BUY ONLY WHAT IT CAN PAY FOR. With no city to
         * ask - a harness's bare market - nobody is limited.
         */
        for (Sector s : sectors.all()) {
            double budget = game == null ? Double.POSITIVE_INFINITY : game.purchaseBudget(s);
            // A maker's inputs come out of it first: they are bought whole. And
            // the orders its suppliers' credit covers apart (0.7.44; SupplierCredit).
            s.openPurchases(budget - orderValue(s, false), orderValue(s, true), coveredOrderValue(s));
        }

        // 3. the traded goods, in turn
        for (Good g : Good.values()) {
            if (!g.traded()) continue;
            clear(g, sectors);
        }
        for (Sector s : sectors.all()) s.closePurchases();

        // 4. into the warehouses, for next month
        for (Sector s : sectors.all()) {
            for (Good g : s.goodsMade()) {
                if (g.stockable() && g.traded()) s.produceStock(g, markets.get(g));
            }
        }

        // 5. the month's loose ends
        for (Sector s : sectors.all()) s.endOfMonth(game);
    }

    /** What a sector's orders this month would come to, each at what a unit costs to bring in (GoodsMarket.landedPrice()): its orders for stock, or its inputs. */
    private double orderValue(Sector s, boolean stock) {
        double total = 0;
        for (Good g : Good.values()) {
            if (!g.traded() || !s.isUser(g) || s.buysAhead(g) != stock) continue;
            GoodsMarket m = markets.get(g);
            if (m == null) continue;
            double units = Math.max(0, s.bid(g));
            double unit = m.landedPrice();
            if (units > 0 && unit > 0 && Double.isFinite(unit)) total += units * unit;
        }
        return total;
    }

    /** ...and what its orders for the stock its suppliers' credit covers would come to (0.7.44; SupplierCredit): nothing without one. */
    private double coveredOrderValue(Sector s) {
        SupplierCredit credit = s.supplierCredit();
        if (credit == null) return 0;
        double total = 0;
        for (Good g : Good.values()) {
            if (!g.traded() || !s.isUser(g) || !s.hasPantry(g) || !credit.covers(g)) continue;
            GoodsMarket m = markets.get(g);
            if (m == null) continue;
            double units = Math.max(0, s.bid(g));
            double unit = m.landedPrice();
            if (units > 0 && unit > 0 && Double.isFinite(unit)) total += units * unit;
        }
        return total;
    }

    /** Prices one good, then everybody trades it. */
    private void clear(Good g, Sectors sectors) {

        GoodsMarket m = markets.get(g);
        m.startMonth();

        List<Sector> makers = new ArrayList<>();
        List<Sector> users = new ArrayList<>();
        for (Sector s : sectors.all()) {
            if (s.isMaker(g)) makers.add(s);
            if (s.isUser(g))  users.add(s);
        }

        // what will come, what is held, what is wanted
        double flow = 0, held = 0, wanted = 0;
        for (Sector s : makers) {
            flow += g.stockable() ? s.getPlannedOutput(g) : s.output(g).produced;
            held += s.getStock(g);
        }
        // ...and the city's, when it trades this good this month (0.7.85): its
        // release comes to market as a flow does, and its fill is wanted. A
        // city that bids offers nothing (CityTrader).
        double cityBid = city == null ? 0 : Math.max(0, city.cityBid(g));
        double cityOffer = city == null || cityBid > 0 ? 0 : Math.max(0, city.cityOffer(g));
        if (cityOffer > 0) flow += cityOffer;
        double[] bids = new double[users.size()];
        double[] asked = new double[users.size()];
        for (int j = 0; j < users.size(); j++) {
            asked[j] = Math.max(0, users.get(j).bid(g));
            // ...the share of an order for stock the buyer can pay for (0.7.12
            // round 6): an order it cannot pay for is not placed, so it is not
            // demand. A maker's input is bought whole - see Sector.
            bids[j] = users.get(j).buysAhead(g) ? asked[j] * users.get(j).purchaseShare(g) : asked[j];
            wanted += bids[j];
        }
        if (cityBid > 0) wanted += cityBid;
        // ...and what was drawn on demand since the last strike counts as wanted too
        double drawn = m.takeDrawn();
        wanted += drawn;
        m.noteBid(drawn);

        m.strike(flow, held, wanted);
        double price = m.getLocalPrice();

        double[] offers = new double[makers.size()];
        double offered = 0;
        for (int i = 0; i < makers.size(); i++) {
            offers[i] = Math.max(0, makers.get(i).offer(g, price));
            offered += offers[i];
            m.noteOffered(offers[i]);
        }
        if (cityOffer > 0) {
            offered += cityOffer;
            m.noteOffered(cityOffer);
        }
        /*
         * ...AND NEVER PAST WHAT IS LEFT OF IT, at what a unit will cost this
         * buyer now the price is struck: the local price on the share the
         * makers can fill, the import price on the rest. Cutting an order only
         * raises the share filled at home, never above the import price, so
         * what it is charged cannot pass what it had left. A good the world
         * does not sell is only ever filled at home, at the price.
         */
        double asking = 0;
        for (double b : bids) asking += b;
        if (cityBid > 0) asking += cityBid;
        // ...and with every order cut to nothing, the share the makers could
        // have filled: all of it at home if anybody here offers, none if not.
        double filled = asking > 0 ? Math.min(1, offered / asking) : offered > 0 ? 1 : 0;
        double unit = g.importable() ? filled * price + (1 - filled) * m.importPrice() : price;
        double unitValue = g.importable() ? unit : filled * price;
        for (int j = 0; j < users.size(); j++) {
            Sector u = users.get(j);
            if (u.buysAhead(g) && unit > 0 && bids[j] * unit > u.purchasesLeft()) bids[j] = u.purchasesLeft() / unit;
            u.noteForgone(g, asked[j] - bids[j], (asked[j] - bids[j]) * unitValue);
        }

        double bid = 0;
        for (int j = 0; j < users.size(); j++) {
            users.get(j).input(g).bid = bids[j];
            users.get(j).input(g).needed = users.get(j).getInputAtCapacity(g) * users.get(j).getOperatingRate();
            m.noteBid(bids[j]);
            bid += bids[j];
        }
        if (cityBid > 0) {
            m.noteBid(cityBid);
            bid += cityBid;
        }

        double fill = Math.min(offered, bid);
        double citySoldHere = 0;

        if (fill > 0) {
            for (int j = 0; j < users.size(); j++) {
                if (bids[j] <= 0) continue;
                double take = bids[j] * fill / bid;
                for (int i = 0; i < makers.size(); i++) {
                    if (offers[i] <= 0) continue;
                    double units = take * offers[i] / offered;
                    if (units <= 0) continue;
                    trade(m, makers.get(i), users.get(j), units, price);
                }
                // ...and from the city's release, its share of the offers (0.7.85).
                if (cityOffer > 0) {
                    double units = take * cityOffer / offered;
                    if (units > 0) {
                        Trade t = m.record(Trade.CITY, users.get(j).key(), units, price);
                        users.get(j).bookPurchase(t);
                        users.get(j).receiveInput(g, units);
                        city.citySold(t);
                        citySoldHere += units;
                    }
                }
            }
            // ...and the city's fill, from the makers here (0.7.85).
            if (cityBid > 0) {
                double take = cityBid * fill / bid;
                for (int i = 0; i < makers.size(); i++) {
                    if (offers[i] <= 0) continue;
                    double units = take * offers[i] / offered;
                    if (units <= 0) continue;
                    Trade t = m.record(makers.get(i).key(), Trade.CITY, units, price);
                    makers.get(i).bookSale(t);
                    makers.get(i).takeFromStock(g, units);
                    city.cityBought(t);
                }
            }
        }

        // the shortfall, from the world
        if (g.importable()) {
            double importPrice = m.importPrice();
            for (int j = 0; j < users.size(); j++) {
                double got = bids[j] <= 0 || bid <= 0 ? 0 : bids[j] * fill / bid;
                double shortfall = Math.max(0, bids[j] - got);
                if (shortfall <= 0) continue;
                Trade t = m.record(Trade.WORLD, users.get(j).key(), shortfall, importPrice);
                users.get(j).bookPurchase(t);
                users.get(j).receiveInput(g, shortfall);
            }
            // ...and the city's (0.7.85).
            if (cityBid > 0) {
                double got = bid <= 0 ? 0 : cityBid * fill / bid;
                double shortfall = Math.max(0, cityBid - got);
                if (shortfall > 0) city.cityBought(m.record(Trade.WORLD, Trade.CITY, shortfall, importPrice));
            }
        }

        // the makers' unsold flow, to the world or to nobody
        for (Sector s : makers) s.shipUnsoldFlow(g, m);
        // ...and the city's release past what the buyers took, shipped as theirs is (0.7.85).
        if (cityOffer > 0 && g.exportable()) {
            double unsold = cityOffer - citySoldHere;
            if (unsold > 0) city.citySold(m.record(Trade.CITY, Trade.WORLD, unsold, m.exportPrice()));
        }
        if (city != null && (cityBid > 0 || cityOffer > 0)) city.cityCleared(g);
        // ...and each buyer told its good has cleared (0.7.85; Sector.afterClearing()).
        for (Sector u : users) u.afterClearing(g);

        m.closeMonth();
    }

    /** One local fill between two sectors, booked both sides. */
    private void trade(GoodsMarket m, Sector seller, Sector buyer, double units, double price) {
        Trade t = m.record(seller.key(), buyer.key(), units, price);
        seller.bookSale(t);
        seller.takeFromStock(m.good(), units);
        buyer.bookPurchase(t);
        buyer.receiveInput(m.good(), units);
    }

    /* ===================================================================
       DRAWS - taken on demand, at the price of the month
       =================================================================== */

    /** What a draw came to. */
    public record Draw(double units, double local, double imported, double localCost, double importCost) {
        public double cost() { return localCost + importCost; }
    }

    /**
     * What a draw WOULD come to, at today's prices, without taking anything -
     * the quote the build screen shows and the affordability check uses.
     * The same split draw() makes, so a quote is what is charged.
     */
    public Draw quote(Good g, double units, Sectors sectors) {
        return quote(g, units, sectors, 0);
    }

    /**
     * ...with `takenAhead` units of the makers' stock drawn first by orders
     * placed before this one (0.7.83: the refiners' bitumen a run's earlier
     * roads take as they are placed - Game.buildRunInvoice()). Nothing ahead
     * is the quote above, to the bit.
     */
    public Draw quote(Good g, double units, Sectors sectors, double takenAhead) {
        if (!(units > 0)) return new Draw(0, 0, 0, 0, 0);
        GoodsMarket m = markets.get(g);
        double held = 0;
        for (Sector s : sectors.all()) if (s.isMaker(g)) held += s.getStock(g);
        if (takenAhead > 0) held = Math.max(0, held - takenAhead);
        double local = Math.min(units, held);
        double shortfall = units - local;
        double imported = g.importable() ? shortfall : 0;
        return new Draw(units, local, imported, local * m.getLocalPrice(),
                imported * (g.importable() ? m.importPrice() : 0));
    }

    /**
     * Takes units of a good now, from whoever makes and holds it, and
     * imports the rest.
     *
     * @param buyer   the sector taking it, or null for the city
     * @param buyerKey what to book the trade against when the buyer is not a sector
     */
    public Draw draw(Good g, Sector buyer, String buyerKey, double units, Sectors sectors) {

        if (!(units > 0)) return new Draw(0, 0, 0, 0, 0);
        GoodsMarket m = markets.get(g);
        double price = m.getLocalPrice();
        String who = buyer != null ? buyer.key() : buyerKey;

        double held = 0;
        List<Sector> makers = new ArrayList<>();
        for (Sector s : sectors.all()) {
            if (!s.isMaker(g) || s.getStock(g) <= 0) continue;
            makers.add(s);
            held += s.getStock(g);
        }

        double local = Math.min(units, held);
        double localCost = 0;
        if (local > 0) {
            for (Sector s : makers) {
                double share = local * s.getStock(g) / held;
                if (share <= 0) continue;
                Trade t = m.record(s.key(), who, share, price);
                s.bookSale(t);
                s.takeFromStock(g, share);
                if (buyer != null) buyer.bookPurchase(t);
                localCost += t.value();
            }
        }

        double imported = 0, importCost = 0;
        double shortfall = units - local;
        if (shortfall > 0 && g.importable()) {
            Trade t = m.record(Trade.WORLD, who, shortfall, m.importPrice());
            if (buyer != null) buyer.bookPurchase(t);
            imported = shortfall;
            importCost = t.value();
        }

        m.noteDrawn(units);
        return new Draw(units, local, imported, localCost, importCost);
    }

    /* ===================================================================
       READERS
       =================================================================== */

    /** Units the world sold the city this month, of one good, from the markets' own record. */
    public double importedUnits(Good g) { return markets.get(g).getImported(); }
    public double exportedUnits(Good g) { return markets.get(g).getExported(); }

    /* ===================================================================
       SAVE, RESET, THE REFORM
       =================================================================== */

    /** One market as a save carries it: the price it traded at and the strike behind it. */
    public static final class State {
        public String good;
        public double price, flow, stock, demand;
        public double[] taken;
    }

    public List<State> toState() {
        List<State> out = new ArrayList<>();
        for (GoodsMarket m : markets.values()) {
            State s = new State();
            s.good = m.good().name();
            s.price = m.getLocalPrice();
            s.flow = m.getSupplyFlow();
            s.stock = m.getSupplyStock();
            s.demand = m.getDemand();
            s.taken = m.getTakenHistory();
            out.add(s);
        }
        return out;
    }

    public void restore(List<State> saved) {
        if (saved == null) return;
        for (State s : saved) {
            if (s == null) continue;
            Good g = Good.byName(s.good);
            if (g == null) continue;
            GoodsMarket m = markets.get(g);
            m.setLocalPrice(s.price);
            m.restoreStrike(s.flow, s.stock, s.demand);
            m.restoreTakenHistory(s.taken);
        }
    }

    public void reset() {
        for (GoodsMarket m : markets.values()) m.reset();
    }

    public void redenominate(double scale) {
        for (GoodsMarket m : markets.values()) m.redenominate(scale);
    }
}
