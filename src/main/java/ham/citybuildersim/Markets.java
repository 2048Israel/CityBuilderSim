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
 *   3. each traded good, in turn: priced off what the makers will bring and
 *      what the buyers intend; offered by the makers at that price; bid for
 *      by the users; allocated pro rata both ways; the buyers' shortfall
 *      imported if the world sells it; the makers' unsold flow exported if
 *      the world buys it
 *   4. the stockable goods are made into the warehouses, for next month
 *   5. each sector finishes its month
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

    public Markets() {
        for (Good g : Good.values()) markets.put(g, new GoodsMarket(g));
    }

    public GoodsMarket get(Good g) { return markets.get(g); }

    public Iterable<GoodsMarket> all() { return markets.values(); }

    /** City money per dollar, times the world's price level. Before anything is priced. */
    public void setExchangeRate(double rate) {
        for (GoodsMarket m : markets.values()) m.setExchangeRate(rate);
    }

    /* ===================================================================
       THE MONTH
       =================================================================== */

    public void clearMonth(Sectors sectors, Game game) {

        // 1. the flow goods are made
        for (Sector s : sectors.all()) {
            for (Good g : s.goodsMade()) {
                if (!g.stockable() && g.traded()) s.produceFlow(g);
            }
        }

        // 2. the seller-priced goods
        for (Sector s : sectors.all()) s.sellOwnPriced(this, game);

        // 3. the traded goods, in turn
        for (Good g : Good.values()) {
            if (!g.traded()) continue;
            clear(g, sectors);
        }

        // 4. into the warehouses, for next month
        for (Sector s : sectors.all()) {
            for (Good g : s.goodsMade()) {
                if (g.stockable() && g.traded()) s.produceStock(g, markets.get(g));
            }
        }

        // 5. the month's loose ends
        for (Sector s : sectors.all()) s.endOfMonth(game);
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
        double[] bids = new double[users.size()];
        for (int j = 0; j < users.size(); j++) {
            bids[j] = Math.max(0, users.get(j).bid(g));
            wanted += bids[j];
        }
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
        double bid = 0;
        for (int j = 0; j < users.size(); j++) {
            users.get(j).input(g).bid = bids[j];
            users.get(j).input(g).needed = users.get(j).getInputAtCapacity(g) * users.get(j).getOperatingRate();
            m.noteBid(bids[j]);
            bid += bids[j];
        }

        double fill = Math.min(offered, bid);

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
        }

        // the makers' unsold flow, to the world or to nobody
        for (Sector s : makers) s.shipUnsoldFlow(g, m);

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
        if (!(units > 0)) return new Draw(0, 0, 0, 0, 0);
        GoodsMarket m = markets.get(g);
        double held = 0;
        for (Sector s : sectors.all()) if (s.isMaker(g)) held += s.getStock(g);
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
