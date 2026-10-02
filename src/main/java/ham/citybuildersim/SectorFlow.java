package ham.citybuildersim;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * One business's month as a flow (0.7.30): what went in, what its plant made
 * of it and what held the plant back, and what came out - each good's units
 * off the month's production rows, its money off the statement, the plant's
 * six throttles and the rate they multiply to.
 *
 * WHY. The Sectors screen's Operations page was the factory's block of
 * label-and-figure lines (Sector.plantLines()) - "Could make", "Made",
 * "Idled", "Sold at home", "Exported", "Wanted", "From the city", "Imported
 * instead" a good at a time - and a note that said output was cut by the
 * thinnest of five ratios when it is the product of six. Jerus: "the others
 * are still full of text and the design could be more intuitive and fun".
 * Redrawn as inputs → the plant → outputs (the project's
 * spec-sectors-0730.md, D1), and this is that picture's figures, held by a
 * harness (SectorFlowCheck) rather than worked out in the screen: the money
 * a row carries adds up to the income statement's lines to the cent, and the
 * cascade's last step is getOperatingRate() itself.
 *
 * TWO MONTHS, AND WHICH IS WHICH. The units are the production rows
 * (Sector.Output, Sector.Input): the month the plants just ran, the figures
 * the operations page has always shown. The money is the statement the books
 * closed on (Sector.statement(), SectorBooks), the Income page's. In a city
 * that is not changing fast the two agree; a row's money is not its units
 * times a price. The rows are not saved, so after a load the units are NOT
 * COUNTED (NaN) until a month runs - the rule BuildCard.counted() uses for
 * the same flows - and the money, which is saved, is shown.
 *
 * Pure: it reads the sector and the city and changes nothing; the
 * production rows are read without creating any (Sector.outputRow()).
 */
public final class SectorFlow {

    /**
     * One good or service that went in.
     *
     * @param good        the good, or null for a service (haulage, fuel, stock paid for earlier - Sector.otherInputParts())
     * @param name        what it is called
     * @param wanted      what it asked the market for this month (Input.bid); NaN not counted, or for a service
     * @param fromCity    ...filled at home (Input.boughtLocal)
     * @param imported    ...filled from the world (Input.imported)
     * @param onHand      what is in its pantry, for a good it keeps one of; NaN otherwise
     * @param moneyHome   the statement's money for it bought in the city; a service's whole bill
     * @param moneyAbroad ...and landed
     * @param priceHere   the city's price of a unit today; NaN for a service or a good with no market price
     * @param priceLanded ...and what a landed one costs, freight in (GoodsMarket.netImportPrice()); NaN if it cannot be imported
     */
    public record In(Good good, String name, double wanted, double fromCity, double imported, double onHand,
                     double moneyHome, double moneyAbroad, double priceHere, double priceLanded) {
        public boolean service() { return good == null; }
        /** Units bought, home and abroad. */
        public double units() { return fromCity + imported; }
        public double money() { return moneyHome + moneyAbroad; }
        /** The city's share of the units bought; NaN with none bought or not counted. */
        public double cityShare() { return units() > 0 ? fromCity / units() : Double.NaN; }
    }

    /**
     * One good that came out, or a part of its revenue that is not a good.
     *
     * @param good        the good, or null for work billed (Sector.otherRevenueParts())
     * @param name        what it is called
     * @param capacity    nameplate a month (getCapacity()); for a good the seller prices, what the Build group's
     *                    note says it can serve (capacityNote()); NaN where neither says
     * @param made        made this month, for home and for the ship (Output.produced + exportBound); NaN not counted
     * @param idled       nameplate neither the city nor the world would take (Output.idled)
     * @param soldHome    units taken by buyers in the city (Output.soldLocal)
     * @param exported    units shipped (Output.exported)
     * @param withheld    kept back below cost (Output.withheld)
     * @param spoiled     stock lost to a demolished warehouse (Output.writtenOff)
     * @param stock       in the warehouse now, for a good it can stock; NaN otherwise
     * @param moneyHome   the statement's money for it sold in the city; work billed's whole amount
     * @param moneyAbroad ...and shipped
     * @param price       the city's price of a unit today; NaN for a good the seller prices
     * @param cost        what one costs to make (Output.costPerUnit); NaN with no line to cost
     */
    public record Out(Good good, String name, double capacity, double made, double idled, double soldHome,
                      double exported, double withheld, double spoiled, double stock,
                      double moneyHome, double moneyAbroad, double price, double cost) {
        public boolean work() { return good == null; }
        public double money() { return moneyHome + moneyAbroad; }
        /** Made and not sold or shipped this month: into the warehouse (0 when the month sold more than it made). */
        public double intoStock() { return Double.isFinite(made) ? Math.max(0, made - soldHome - exported) : Double.NaN; }
        /** Sold or shipped beyond what was made: out of the warehouse (0 when it made more than it sold). */
        public double fromStock() { return Double.isFinite(made) ? Math.max(0, soldHome + exported - made) : Double.NaN; }
        /** Whether a unit costs more to make than the city pays for it. */
        public boolean underwater() { return Double.isFinite(cost) && Double.isFinite(price) && cost > price; }
    }

    /** The six throttles in getOperatingRate()'s order, as the screen names them. */
    public static final String[] THROTTLES = { "staffed", "power", "water", "roads", "well", "vans" };

    /**
     * The plant.
     *
     * @param standing  its buildings, finished (Sector.buildingsStanding())
     * @param onSite    on site, for anyone's order (Sector.buildingsOnSite())
     * @param posts     the posts it offers (getPostsOffered())
     * @param workers   the posts filled (getWorkers())
     * @param ratios    the six throttles, THROTTLES' order: the fill, power, water, the road, health, its vans
     * @param cascade   the running product after each - the last is the rate, to the bit
     * @param rate      getOperatingRate(); NaN with nothing standing (no plant has a rate)
     * @param let       the landlords' homes let over the homes they own; NaN for everyone else
     */
    public record Plant(int standing, int onSite, int posts, double workers, double[] ratios, double[] cascade,
                        double rate, double let) {
        public boolean none() { return standing <= 0; }
        /** The throttle that cuts most: the lowest ratio, the first of equals; -1 with nothing standing. */
        public int lowest() {
            if (none()) return -1;
            int at = 0;
            for (int i = 1; i < ratios.length; i++) if (ratios[i] < ratios[at]) at = i;
            return at;
        }
    }

    /**
     * The flow.
     *
     * @param counted   whether the units are this month's: false after a load until a month runs (the
     *                  sector has no word for the month - BuildCard.counted()'s rule)
     * @param note      the Build group's note for its own buildings (BuildCard.noteOf())
     * @param points    the builders' output a month, building points (Game.getConstructionOutput()); NaN for others
     * @param busy      ...and how busy they were (Construction.getUtilisation()); NaN for others
     */
    public record Flow(Sector sector, List<In> inputs, List<Out> outputs, Plant plant, boolean counted,
                       BuildCard.Note note, double points, double busy) {
        /** The money in, goods and services: the statement's inputs line. */
        public double inputsMoney() {
            double t = 0;
            for (In i : inputs) t += i.money();
            return t;
        }
        /** The money out, goods and work: the statement's revenue line. */
        public double revenueMoney() {
            double t = 0;
            for (Out o : outputs) t += o.money();
            return t;
        }
    }

    private SectorFlow() { }

    public static Flow of(Game game, Sector sector) {
        Sector.Statement st = sector.statement();
        Markets markets = game.getMarkets();
        boolean counted = !game.getLastInvestment(sector.key()).isEmpty();
        double unknown = Double.NaN;

        /* ------------------------------- what went in ------------------------------- */
        List<In> ins = new ArrayList<>();
        Set<Good> used = new LinkedHashSet<>(sector.goodsUsed());
        used.addAll(st.bought.keySet());
        for (Good g : used) {
            Sector.Input row = sector.inputRow(g);
            Sector.Split money = st.bought.get(g);
            double home = money == null ? 0 : money.atHome, abroad = money == null ? 0 : money.abroad;
            double wanted = row == null ? 0 : row.bid, local = row == null ? 0 : row.boughtLocal;
            double landed = row == null ? 0 : row.imported;
            double onHand = sector.hasPantry(g) ? sector.getPantry(g) : Double.NaN;
            // Nothing wanted, bought, held or paid for: no row (the vans' block on every page, B5).
            if (wanted <= 0 && local <= 0 && landed <= 0 && home == 0 && abroad == 0 && !(onHand > 0)) continue;
            GoodsMarket m = g.traded() && markets != null ? markets.get(g) : null;
            ins.add(new In(g, g.label(), counted ? wanted : unknown, counted ? local : unknown,
                    counted ? landed : unknown, onHand, home, abroad,
                    m == null ? Double.NaN : m.getLocalPrice(),
                    m == null || !g.importable() ? Double.NaN : m.netImportPrice()));
        }
        for (Map.Entry<String, Double> e : sector.otherInputParts().entrySet()) {
            if (e.getValue() == null || e.getValue() == 0) continue;
            ins.add(new In(null, e.getKey(), Double.NaN, Double.NaN, Double.NaN, Double.NaN,
                    e.getValue(), 0, Double.NaN, Double.NaN));
        }

        Plant plant = plant(sector);

        /* ------------------------------ what came out ------------------------------- */
        BuildCard.Note note = BuildCard.noteOf(game, sector);
        List<Out> outs = new ArrayList<>();
        Set<Good> made = new LinkedHashSet<>(sector.goodsMade());
        made.addAll(st.sold.keySet());
        for (Good g : made) {
            Sector.Output row = sector.outputRow(g);
            Sector.Split money = st.sold.get(g);
            double home = money == null ? 0 : money.atHome, abroad = money == null ? 0 : money.abroad;
            double capacity = g.traded() ? sector.getCapacity(g) : capacityNote(note, g);
            double make = row == null ? 0 : row.produced + row.exportBound;
            double sold = row == null ? 0 : row.soldLocal, shipped = row == null ? 0 : row.exported;
            double stock = g.stockable() ? sector.getStock(g) : Double.NaN;
            // A good this sector has no plant for, sells none of and holds none of is no row (plantLines()'s rule).
            if (!(capacity > 0) && make <= 0 && sold <= 0 && shipped <= 0 && !(stock > 0) && home == 0 && abroad == 0) continue;
            GoodsMarket m = g.traded() && markets != null ? markets.get(g) : null;
            boolean costed = row != null && Double.isFinite(row.costPerUnit) && row.costPerUnit < Double.MAX_VALUE && row.costPerUnit > 0;
            outs.add(new Out(g, g.label(), capacity,
                    counted ? make : unknown, counted && row != null ? row.idled : counted ? 0 : unknown,
                    counted ? sold : unknown, counted ? shipped : unknown,
                    counted && row != null ? row.withheld : counted ? 0 : unknown,
                    counted && row != null ? row.writtenOff : counted ? 0 : unknown,
                    stock, home, abroad, m == null ? Double.NaN : m.getLocalPrice(),
                    costed && counted ? row.costPerUnit : Double.NaN));
        }
        for (Map.Entry<String, Double> e : sector.otherRevenueParts().entrySet()) {
            if (e.getValue() == null || e.getValue() == 0) continue;
            outs.add(new Out(null, e.getKey(), Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
                    Double.NaN, Double.NaN, Double.NaN, e.getValue(), 0, Double.NaN, Double.NaN));
        }

        double points = Double.NaN, busy = Double.NaN;
        if (sector instanceof ham.citybuildersim.sectors.Construction builders) {
            points = game.getConstructionOutput();
            busy = builders.getUtilisation();
        }
        return new Flow(sector, ins, outs, plant, counted, note, points, busy);
    }

    /** The plant alone (the Sectors list's cards and every page's RUNNING AT). */
    public static Plant plant(Sector sector) {
        double[] ratios = { sector.getAverageFill(), sector.getEnergyRatio(), sector.getWaterRatio(),
                sector.getRoadRatio(), sector.getHealthRatio(), sector.getVanRatio() };
        // The running product in getOperatingRate()'s own order, so its last
        // step is the rate to the bit.
        double[] cascade = new double[ratios.length];
        double run = ratios[0];
        cascade[0] = run;
        for (int i = 1; i < ratios.length; i++) {
            run = run * ratios[i];
            cascade[i] = run;
        }
        int standing = sector.buildingsStanding();
        double let = Double.NaN;
        if (sector instanceof ham.citybuildersim.sectors.RealEstate homes && homes.getHomes() > 0) {
            let = homes.getOccupiedHomes() / homes.getHomes();
        }
        return new Plant(standing, sector.buildingsOnSite(), sector.getPostsOffered(), sector.getWorkers(),
                ratios, cascade, standing > 0 ? sector.getOperatingRate() : Double.NaN, let);
    }

    /**
     * What a good the seller prices can serve, off the Build group's note:
     * the shops' coverage for groceries, the counters' for a counter's trade,
     * the kitchens' seats for meals; NaN for any other (homes are counted in
     * doors, the plant's "let", and the railway's tonnes are its own page's).
     */
    static double capacityNote(BuildCard.Note note, Good g) {
        if (note == null) return Double.NaN;
        switch (note.kind()) {
            case SHOPS:  return g == Good.GROCERIES ? note.a() : Double.NaN;
            case LUXURY: return g == Good.LUXURY_TRADE ? note.b() : Double.NaN;
            case MEALS:  return g == Good.MEALS ? note.b() : Double.NaN;
            default:     return Double.NaN;
        }
    }
}
