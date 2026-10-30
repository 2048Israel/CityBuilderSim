package ham.citybuildersim;

import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Haulage;
import ham.citybuildersim.sectors.PortDivision;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The freight band of what the city trades, split into its legs, as the Trade
 * tab draws it (batch FR4; docs/studies/spec-freight-and-infrastructure.md 6,
 * last bullet): the gap between what the world pays for a good and what it
 * charges to bring one in, as the world's own margin, the long haul by mode
 * (the world's lorries, the city's railway, the ships and the quay that works
 * them) and the city leg from the gateway to the door.
 *
 * WHY. Until FR2 the band was two figures and a bar on Infrastructure's Freight
 * page - the world's margin, the freight still paid abroad, the railway's
 * charge - and the Trade tab, where the city meets the world, named the prices
 * (the markets' own, "the railway's freight in them") and nothing of what is in
 * them. Since FR2 and FR3 a tonne that crosses the boundary moves in legs, each
 * paid once and to somebody (spec 2): the long haul by lorry or ship abroad, by
 * rail at home; at the quay, handling at home; and the city leg at home, to
 * Freight's trucks or its siding. The study asks the band to say so on Trade.
 * These are those figures, held by a harness (TradeBandCheck) to the getters
 * that keep them rather than worked out in the screen, which only paints them.
 *
 * TWO READINGS OF ONE BAND. A row is a good's gap A UNIT, in the city's money,
 * at the prices and shares in force now: every piece the model's own getter
 * (GoodsMarket.worldMargin(), freightInBand(), railInWedge(), the quote the
 * quay and Haulage.legPrice() state), the first three of them summing to the
 * band the price grid shows (importPrice() less exportPrice()), and with the
 * railway's, the delivered gap (netImportPrice() less netExportPrice()). The
 * whole is the same pieces over the month's trade: each good's crossings - what
 * the books say it sold and bought abroad, divided by the price it cleared at
 * - times half its two-way piece, a crossing paying one way. It is money at
 * today's prices on last month's trade, because the model keeps the month's
 * trade in money by good and not in units (the units are cleared at the
 * strike), and says so; the exact bills of the month are Freight's own
 * (getHaulageBilled(), getPaidAbroad(), Haulage's bill), which a screen on the
 * Freight page prints.
 *
 * WHICH GOODS. The ones the city can both buy and sell abroad with a price to
 * measure against - Infrastructure's bars' set - so the band exists. Money that
 * crossed in a good with no band (a service, a good only one way) is not in the
 * whole, and its row is not here.
 *
 * Every figure is finite whatever the city: a getter's NaN or infinity reads 0,
 * a ratio with nothing under it is 0, and a city that has never traded has rows
 * of the prices and a whole of zeros.
 *
 * Pure: it reads the city and changes nothing.
 */
public final class TradeBand {

    private TradeBand() { }

    /* =====================================================================
       THE VOCABULARY
       ===================================================================== */

    /**
     * The pieces of the band, in the order the bar lays them: the world's own
     * margin, the long haul by mode, and the city leg. Whether each is paid
     * abroad is the Freight page's hatching: the world's lorries and ships and
     * the world's own margin leave with the cargo; the railway, the quay's
     * handling and the trucks are paid at home.
     */
    public enum Part {
        MARGIN("The world's margin", "the world's margin", true, "kept by the world"),
        WORLD_LORRY("World lorry", "the long haul", true, "the world's lorries, paid abroad"),
        RAIL("Rail", "the long haul", false, "the city's railway, billed at home"),
        SEA("Sea", "the long haul", true, "the world's ships, paid abroad"),
        PORT("Port handling", "the long haul", false, "the city's quay, billed at home (its crews and the dues)"),
        CITY_LEG("City leg", "the city leg", false, "Freight's trucks and sidings, billed at home");

        private final String label, leg, paidTo;
        private final boolean abroad;

        Part(String label, String leg, boolean abroad, String paidTo) {
            this.label = label;
            this.leg = leg;
            this.abroad = abroad;
            this.paidTo = paidTo;
        }

        public String label() { return label; }

        /** The leg it belongs to, in words: "the world's margin", "the long haul" or "the city leg". */
        public String leg() { return leg; }

        /** Whether it is paid abroad (the Freight page's hatching): the world's margin, its lorries and its ships. */
        public boolean abroad() { return abroad; }

        /** Who is paid, in words. */
        public String paidTo() { return paidTo; }
    }

    /* =====================================================================
       THE RECORDS
       ===================================================================== */

    /** One piece of the band: which, and how much, in the city's money. */
    public record Piece(Part part, double amount) { }

    /**
     * One good's gap a unit at the prices in force: what the world pays and
     * charges, the money that crossed in the good in the month the books last
     * struck, and the pieces, one for every Part in Part order.
     */
    public record Row(Good good, double importPrice, double exportPrice, double crossed, List<Piece> pieces) {
        /** The piece of one part. */
        public double amount(Part part) { return pieces.get(part.ordinal()).amount(); }

        /** The band the price grid shows: the world's margin, the lorries' freight and the ships' - importPrice() less exportPrice(). */
        public double band() { return amount(Part.MARGIN) + amount(Part.WORLD_LORRY) + amount(Part.SEA); }

        /** ...and the railway's charge on it: the delivered gap, netImportPrice() less netExportPrice(). */
        public double delivered() { return band() + amount(Part.RAIL); }

        /** ...and the quay's handling and the city leg: the gap to the door. */
        public double toTheDoor() { return delivered() + amount(Part.PORT) + amount(Part.CITY_LEG); }

        /** The part of the money paid abroad, in the city's money. */
        public double abroad() {
            double sum = 0;
            for (Piece p : pieces) if (p.part().abroad()) sum += p.amount();
            return sum;
        }

        /** A part as a share of the good's import price (the bars' scale); 0 where the good has no price. */
        public double share(Part part) { return importPrice > 0 ? amount(part) / importPrice : 0; }

        /** The whole gap to the door as a share of the import price. */
        public double shareToTheDoor() { return importPrice > 0 ? toTheDoor() / importPrice : 0; }
    }

    /**
     * The month's trade in the same pieces: the money that crossed in the
     * goods the band measures (what they sold abroad and bought abroad), and
     * each part's money on it at today's prices, one piece for every Part in
     * Part order.
     */
    public record Whole(double crossed, List<Piece> pieces) {
        /** The piece of one part. */
        public double amount(Part part) { return pieces.get(part.ordinal()).amount(); }

        /** All the pieces together: what the trade's gap to the door came to. */
        public double total() {
            double sum = 0;
            for (Piece p : pieces) sum += p.amount();
            return sum;
        }

        /** The part paid abroad, in money. */
        public double abroad() {
            double sum = 0;
            for (Piece p : pieces) if (p.part().abroad()) sum += p.amount();
            return sum;
        }

        /** A part's share of the gap (0 with no gap). */
        public double shareOfGap(Part part) {
            double t = total();
            return t > 0 ? amount(part) / t : 0;
        }

        /** The gap to the door as a share of the money that crossed (0 when nothing did). */
        public double shareOfTrade() { return crossed > 0 ? total() / crossed : 0; }
    }

    /** The whole, and the goods' rows: those that crossed first (largest first), then the rest by the widest gap. */
    public record Band(Whole whole, List<Row> rows) { }

    /* =====================================================================
       THE BAND
       ===================================================================== */

    /** The band of the city's trade, from its markets, its Freight sector and the books of the month last struck. */
    public static Band of(Game game) {
        Markets markets = game.getMarkets();
        Freight freight = game.getSectors().freight();
        Sectors.TradeByGood trade = game.getTradeByGood();
        Haulage.LegPrice leg = freight.haulage().legPrice();
        PortDivision quay = freight.portDivision();
        List<Row> rows = new ArrayList<>();
        double[] month = new double[Part.values().length];
        double crossedAll = 0;
        for (Good g : Good.values()) {
            if (!g.traded() || !g.exportable() || !g.importable()) continue;
            GoodsMarket m = markets == null ? null : markets.get(g);
            if (m == null) continue;
            double imp = fin(m.importPrice()), exp = fin(m.exportPrice());
            if (!(imp > 0)) continue;

            // The pieces a unit, both ways: each the getter's.
            double margin = Math.max(0, fin(m.worldMargin()));
            double inBand = fin(m.freightInBand());
            double seaShare = unit(freight.seaShareInForce(g));
            // ...the ships' piece of the band's own freight (WN3: bandFreight(), the lorries' freight shrunk to a world-struck band, not Good.baseFreight()).
            double seaMost = 2 * fin(m.bandFreight()) * fin(m.getExchangeRate()) * Ports.seaFactor(seaShare, Ports.seaFreightShareOf(g));
            double sea = Math.max(0, Math.min(inBand, fin(seaMost)));
            double lorry = inBand - sea;
            double rail = fin(m.railInWedge());
            double tonnes = fin(g.tonnesPerUnit());
            Ports.Cargo kind = g.cargo();
            double handling = kind == null ? 0 : fin(quay.getQuote(kind)) * tonnes * seaShare;
            double legIn = fin(leg.in(g)) * tonnes, legOut = fin(leg.out(g)) * tonnes;
            double[] unit = new double[Part.values().length];
            unit[Part.MARGIN.ordinal()] = margin;
            unit[Part.WORLD_LORRY.ordinal()] = lorry;
            unit[Part.RAIL.ordinal()] = rail;
            unit[Part.SEA.ordinal()] = sea;
            unit[Part.PORT.ordinal()] = 2 * handling;
            unit[Part.CITY_LEG.ordinal()] = legIn + legOut;

            // The month's crossings of it, in units at today's prices.
            Sectors.GoodTrade crossed = trade == null ? null : trade.goods().get(g);
            double bought = crossed == null ? 0 : Math.max(0, fin(crossed.bought()));
            double sold = crossed == null ? 0 : Math.max(0, fin(crossed.sold()));
            double boughtUnits = bought / imp, soldUnits = exp > 0 ? sold / exp : 0;
            double crossings = boughtUnits + soldUnits;
            for (Part p : Part.values()) {
                int i = p.ordinal();
                if (p == Part.CITY_LEG) month[i] += boughtUnits * legIn + soldUnits * legOut;
                else month[i] += crossings * unit[i] / 2;
            }
            crossedAll += bought + sold;

            List<Piece> pieces = new ArrayList<>();
            for (Part p : Part.values()) pieces.add(new Piece(p, unit[p.ordinal()]));
            rows.add(new Row(g, imp, exp, bought + sold, List.copyOf(pieces)));
        }
        rows.sort(Comparator.comparingDouble(Row::crossed).reversed()
                .thenComparing(Comparator.comparingDouble(Row::shareToTheDoor).reversed())
                .thenComparingInt(r -> r.good().ordinal()));
        List<Piece> whole = new ArrayList<>();
        for (Part p : Part.values()) whole.add(new Piece(p, fin(month[p.ordinal()])));
        return new Band(new Whole(fin(crossedAll), List.copyOf(whole)), List.copyOf(rows));
    }

    /** A finite figure: NaN and infinity read 0, so no screen ever draws or divides by one. */
    private static double fin(double v) {
        return Double.isFinite(v) ? v : 0;
    }

    /** A share clamped to 0..1, and 0 for a number that is not one. */
    private static double unit(double x) {
        return Double.isFinite(x) ? Math.max(0, Math.min(1, x)) : 0;
    }
}
