package ham.citybuildersim;

import ham.citybuildersim.TradeBand.Band;
import ham.citybuildersim.TradeBand.Part;
import ham.citybuildersim.TradeBand.Piece;
import ham.citybuildersim.TradeBand.Row;
import ham.citybuildersim.TradeBand.Whole;
import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Haulage;

import java.lang.reflect.RecordComponent;
import java.util.List;

/**
 * TradeBand, the model behind the Trade tab's band in legs, held to the
 * study's last bullet of section 6 (docs/studies/spec-freight-and-infrastructure.md):
 * the freight band split into the world's margin, the long haul by mode (the
 * world's lorries, the railway, the ships) and the city leg.
 *
 * WHY THIS HARNESS EXISTS. The screen only paints TradeBand's figures, so the
 * figures are the finding: a leg the model did not keep, a share worked out in
 * the screen or a division by a city's zero would show on a player's page and
 * in no other check. It is written from the model's getters, and where the band
 * says a figure it must equal the getter that keeps it. Six claims:
 *
 *   1. NO TRADE, NO NUMBER. A city that has never traded reads rows of its
 *      prices and a whole of zeros; with no railway and no berth the lorries
 *      carry the whole freight in the band, and no figure is NaN or infinite.
 *   2. THE LEGS SUM TO THE BAND. In a played city the world's margin, the
 *      lorries' freight and the ships' sum to the band the price grid shows
 *      (importPrice() less exportPrice()); with the railway's charge, the
 *      delivered gap (netImportPrice() less netExportPrice()); and each piece is
 *      its getter: worldMargin(), railInWedge(), the quay's handling quote and
 *      Haulage's leg price.
 *   3. THE LORRIES AND THE SHIPS SHARE ONE FIGURE. The freight still paid
 *      abroad (freightInBand()) is the lorries' part and the ships' part,
 *      the ships' at their kind's sea freight on the berths' share in force
 *      (Ports.seaFreightShareOf(), Freight.seaShareInForce()), and the order and
 *      the hatching are the Freight page's.
 *   4. THE WHOLE IS THE MONTH'S TRADE IN THE SAME PIECES. Each part's money is
 *      the goods' crossings at their prices times half the two-way piece (the
 *      city leg's the way it was received or made), the money that crossed is
 *      the rows' and no more than the books' balance of trade, and the shares
 *      of the gap sum to one.
 *   5. THE ORDER. Goods that crossed come first, largest first; asking twice
 *      gives the same band.
 *   6. A RELOAD reads the same band: the shares in force, the quay's quote and
 *      the leg prices are saved, so a loaded city's pieces are the live city's,
 *      and a month on, with the books struck again, so is the whole.
 *
 * It tests the model through its public getters and constants and changes
 * nothing in it; where the code and the brief disagree it says FAIL and the
 * line says where. Every fixture is a city of its own in GameFiles.scratch().
 */
public class TradeBandCheck {

    static int fails = 0;

    static void quietly(Runnable r) {
        HaulageCheck.quietly(r);
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        System.out.printf("%-100s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    /** Bitwise, not near: the band hands a getter's figure on unchanged. */
    static boolean same(double a, double b) {
        return Double.compare(a, b) == 0;
    }

    /** Within a rounding: sums the model makes in another order. */
    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    /** Every double in a record (lists included) is finite; the first that is not, named. */
    static String nonFinite(Object o, String path) {
        if (o == null) return null;
        if (o instanceof Double d) return Double.isFinite(d) ? null : path + " = " + d;
        if (o instanceof List<?> list) {
            for (int i = 0; i < list.size(); i++) {
                String bad = nonFinite(list.get(i), path + "[" + i + "]");
                if (bad != null) return bad;
            }
            return null;
        }
        if (o.getClass().isRecord()) {
            try {
                for (RecordComponent c : o.getClass().getRecordComponents()) {
                    String bad = nonFinite(c.getAccessor().invoke(o), path + "." + c.getName());
                    if (bad != null) return bad;
                }
            } catch (ReflectiveOperationException e) {
                return path + " unreadable: " + e;
            }
        }
        return null;
    }

    /* ================================================================
       THE FIXTURES
       ================================================================ */

    /** A founded city that has never been played: no haul has run, nothing has crossed. */
    static Game unplayed(String label) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> {
            g.newGame();
            g.setCashForTest(Founding.WEALTHY_CASH);
        });
        return g;
    }

    /**
     * FreightViewCheck's full city, built here from HomeRailCheck's railed one
     * (HaulageCheck's three iron mines and a pelletising plant, played
     * fourteen months) with two Rail Depots, two Rail Spurs and a Bulk Terminal
     * laid, then a Truck Depot, a General Cargo Terminal, two Tanker Terminals
     * and a second Bulk Terminal besides, played on: trucks, a railway with
     * sidings, a port yard, berths of three kinds and dues all at once.
     */
    static Game full(GameFiles files) {
        Game g = HomeRailCheck.railed(files, 2, 2, 0, "Bulk Terminal", 4);
        quietly(() -> {
            HaulageCheck.lay(g, "Truck Depot", 1);
            HaulageCheck.lay(g, "General Cargo Terminal", 1);
            HaulageCheck.lay(g, "Tanker Terminal", 2);
            HaulageCheck.lay(g, "Bulk Terminal", 1);
            for (int i = 0; i < 3; i++) g.toggleNextMonth();
        });
        return g;
    }

    /** A row by good. */
    static Row row(Band b, Good g) {
        for (Row r : b.rows()) if (r.good() == g) return r;
        return null;
    }

    /** The goods the band measures: the ones the city can both buy and sell abroad, at a price. */
    static boolean measured(Game g, Good good) {
        GoodsMarket m = g.getMarkets().get(good);
        return good.traded() && good.exportable() && good.importable() && m != null && m.importPrice() > 0
                && Double.isFinite(m.exportPrice());
    }

    /* ================================================================
       1. NO TRADE
       ================================================================ */

    static void noTrade() {
        System.out.println("\n--- 1. no trade, no number: a city that never traded reads zeros, and no NaN ---");
        Game g = unplayed("tradeband-none");
        Band b = TradeBand.of(g);
        assertTrue("the parts are six, in the bar's order: margin, world lorry, rail, sea, port handling, city leg",
                java.util.Arrays.equals(Part.values(), new Part[] { Part.MARGIN, Part.WORLD_LORRY, Part.RAIL, Part.SEA,
                        Part.PORT, Part.CITY_LEG }));
        report("fixture: the city has goods the world will buy and sell, at a price", !b.rows().isEmpty(),
                b.rows().size() + " goods");
        assertTrue("the whole has a piece for every part, and nothing crossed", b.whole().pieces().size() == Part.values().length
                && b.whole().crossed() == 0);
        double sum = 0;
        for (Piece p : b.whole().pieces()) sum += p.amount();
        report("every piece of the whole is zero, and so are its total and its shares",
                sum == 0 && b.whole().total() == 0 && b.whole().abroad() == 0 && b.whole().shareOfTrade() == 0
                        && b.whole().shareOfGap(Part.MARGIN) == 0, "pieces " + sum);
        String bad = nonFinite(b, "band");
        report("no figure of any record is NaN or infinite", bad == null, bad == null ? "" : bad);
        boolean noRail = true, noSea = true, lorries = true, band = true, noMoney = true;
        for (Row r : b.rows()) {
            GoodsMarket m = g.getMarkets().get(r.good());
            noRail &= r.amount(Part.RAIL) == 0 && r.amount(Part.PORT) == 0;
            noSea &= r.amount(Part.SEA) == 0;
            lorries &= same(r.amount(Part.WORLD_LORRY), m.freightInBand());
            band &= near(r.band(), m.bandByLorry()) && near(r.band(), m.importPrice() - m.exportPrice());
            noMoney &= r.crossed() == 0;
        }
        assertTrue("with no railway and no berth the rows have no rail, no sea and no handling", noRail && noSea);
        assertTrue("...the lorries carry all the freight in the band (the band with every tonne on a lorry)", lorries && band);
        assertTrue("...and no good crossed", noMoney);
        assertTrue("the band is pure: asked twice it is the same", b.equals(TradeBand.of(g)));
    }

    /* ================================================================
       2. THE LEGS SUM TO THE BAND
       ================================================================ */

    static void legs(Game g, Band b) {
        System.out.println("\n--- 2. the legs sum to the band: each piece is its getter, and they add up to the price grid's gap ---");
        Freight fr = g.getSectors().freight();
        Haulage.LegPrice leg = fr.haulage().legPrice();
        int rows = 0, rail = 0, sea = 0, port = 0, city = 0;
        boolean margin = true, rails = true, band = true, delivered = true, door = true, handling = true, city2 = true;
        boolean pure = true;
        StringBuilder wrong = new StringBuilder();
        for (Row r : b.rows()) {
            Good good = r.good();
            GoodsMarket m = g.getMarkets().get(good);
            rows++;
            margin &= same(r.amount(Part.MARGIN), Math.max(0, m.worldMargin()));
            rails &= same(r.amount(Part.RAIL), m.railInWedge());
            boolean thisBand = near(r.band(), m.importPrice() - m.exportPrice())
                    && near(r.amount(Part.MARGIN) + r.amount(Part.WORLD_LORRY) + r.amount(Part.SEA), m.worldMargin() + m.freightInBand());
            band &= thisBand;
            delivered &= near(r.delivered(), m.netImportPrice() - m.netExportPrice());
            double tonnes = good.tonnesPerUnit();
            double seaShare = fr.seaShareInForce(good);
            double wantHandling = good.cargo() == null ? 0 : 2 * fr.portDivision().getQuote(good.cargo()) * tonnes * seaShare;
            handling &= same(r.amount(Part.PORT), wantHandling);
            city2 &= same(r.amount(Part.CITY_LEG), (leg.in(good) + leg.out(good)) * tonnes);
            door &= near(r.toTheDoor(), r.delivered() + r.amount(Part.PORT) + r.amount(Part.CITY_LEG));
            if (!thisBand) wrong.append(good.name()).append(' ');
            if (r.amount(Part.RAIL) > 0) rail++;
            if (r.amount(Part.SEA) > 0) sea++;
            if (r.amount(Part.PORT) > 0) port++;
            if (r.amount(Part.CITY_LEG) > 0) city++;
            pure &= r.pieces().size() == Part.values().length;
        }
        report("fixture: a played city with a railway, ships, a quay and a siding - goods on rail, at sea, handled and hauled",
                rows > 0 && rail > 0 && sea > 0 && port > 0 && city > 0,
                String.format("%d goods: %d on rail, %d at sea, %d handled, %d with a city leg", rows, rail, sea, port, city));
        report("every good's margin is the market's worldMargin() (never below nothing)", margin, "");
        report("...its rail piece is the railway's charge both ways, railInWedge()", rails, "");
        report("...the margin, the lorries' freight and the ships' sum to importPrice() less exportPrice()", band,
                wrong.length() == 0 ? "" : wrong.toString());
        report("...with the railway's charge, to netImportPrice() less netExportPrice(), the delivered gap", delivered, "");
        report("...its port handling is the quay's handling quote on its tonnes, both ways, at the berths' share in force", handling, "");
        report("...its city leg is Haulage's leg price received and made, a tonne's, on its weight", city2, "");
        report("...and the gap to the door is the delivered gap, the handling and the city leg", door && pure, "");
        String bad = nonFinite(b, "band");
        report("no figure of any record is NaN or infinite in the played city", bad == null, bad == null ? "" : bad);
    }

    /* ================================================================
       3. THE LORRIES AND THE SHIPS
       ================================================================ */

    static void lorriesAndShips(Game g, Band b) {
        System.out.println("\n--- 3. the lorries and the ships share one figure, and the hatching is the Freight page's ---");
        Freight fr = g.getSectors().freight();
        boolean sums = true, ships = true, lorries = true, noBerth = true;
        int shipped = 0, landed = 0;
        for (Row r : b.rows()) {
            Good good = r.good();
            GoodsMarket m = g.getMarkets().get(good);
            double rate = m.getExchangeRate();
            double seaShare = fr.seaShareInForce(good), railShare = fr.railShareInForce(good);
            double x = Ports.seaFreightShareOf(good);
            sums &= near(r.amount(Part.WORLD_LORRY) + r.amount(Part.SEA), m.freightInBand());
            if (seaShare > 0 && x > 0) {
                shipped++;
                // ...the good's freight in the band (WN3: the lorries' freight shrunk to a world-struck band, Good.baseFreight() on the literals').
                ships &= near(r.amount(Part.SEA), 2 * m.bandFreight() * rate * seaShare * x);
                lorries &= near(r.amount(Part.WORLD_LORRY), 2 * m.bandFreight() * rate * (1 - railShare - seaShare));
            } else {
                landed++;
                noBerth &= r.amount(Part.SEA) == 0 && same(r.amount(Part.WORLD_LORRY), m.freightInBand());
            }
        }
        report("fixture: some goods go by ship and some do not", shipped > 0 && landed > 0, shipped + " by ship, " + landed + " not");
        assertTrue("the lorries' piece and the ships' sum to the freight still in the band, freightInBand()", sums);
        assertTrue("...the ships' piece is the good's two-way freight at its kind's sea freight on the berths' share in force", ships);
        assertTrue("...the lorries' is the freight the railway and the ships did not take (1 less their shares)", lorries);
        assertTrue("...a good no ship carries has no sea piece, and the lorries carry the whole freight in the band", noBerth);
        assertTrue("hatched, as the Freight page marks the modes: the world's margin, its lorries and its ships paid abroad; the rest at home",
                Part.MARGIN.abroad() && Part.WORLD_LORRY.abroad() == FreightView.Mode.WORLD_LORRY.abroad()
                        && Part.SEA.abroad() == FreightView.Mode.SEA.abroad() && Part.RAIL.abroad() == FreightView.Mode.RAIL.abroad()
                        && Part.CITY_LEG.abroad() == FreightView.Mode.TRUCK.abroad() && !Part.PORT.abroad());
        Row any = b.rows().get(0);
        double abroad = 0;
        for (Piece p : any.pieces()) if (p.part().abroad()) abroad += p.amount();
        assertTrue("a row's money paid abroad is the margin, the lorries' and the ships'", near(any.abroad(), abroad)
                && near(any.abroad(), any.band()));
    }

    /* ================================================================
       4. THE WHOLE
       ================================================================ */

    static void whole(Game g, Band b) {
        System.out.println("\n--- 4. the whole is the month's trade in the same pieces, at today's prices ---");
        Sectors.TradeByGood trade = g.getTradeByGood();
        Freight fr = g.getSectors().freight();
        Haulage.LegPrice leg = fr.haulage().legPrice();
        Whole w = b.whole();
        // Worked out again from the books and the getters, in another order: each good's crossings, then the parts.
        double[] want = new double[Part.values().length];
        double crossed = 0;
        int goods = 0;
        for (Good good : Good.values()) {
            if (!measured(g, good)) continue;
            GoodsMarket m = g.getMarkets().get(good);
            Sectors.GoodTrade t = trade.goods().get(good);
            double bought = t == null ? 0 : t.bought(), sold = t == null ? 0 : t.sold();
            if (bought + sold > 0) goods++;
            crossed += bought + sold;
            double boughtUnits = bought / m.importPrice();
            double soldUnits = m.exportPrice() > 0 ? sold / m.exportPrice() : 0;
            double half = (boughtUnits + soldUnits) / 2;
            double rate = m.getExchangeRate();
            double seaShare = fr.seaShareInForce(good);
            double seaMost = 2 * good.baseFreight() * rate * seaShare * Ports.seaFreightShareOf(good);
            double sea = Math.min(m.freightInBand(), seaMost);
            double tonnes = good.tonnesPerUnit();
            want[Part.MARGIN.ordinal()] += half * Math.max(0, m.worldMargin());
            want[Part.SEA.ordinal()] += half * sea;
            want[Part.WORLD_LORRY.ordinal()] += half * (m.freightInBand() - sea);
            want[Part.RAIL.ordinal()] += half * m.railInWedge();
            want[Part.PORT.ordinal()] += good.cargo() == null ? 0
                    : half * 2 * fr.portDivision().getQuote(good.cargo()) * tonnes * seaShare;
            want[Part.CITY_LEG.ordinal()] += (boughtUnits * leg.in(good) + soldUnits * leg.out(good)) * tonnes;
        }
        report("fixture: the city sold and bought goods abroad in the month the books last struck", goods > 0 && crossed > 0,
                String.format("%d goods crossed, %s of trade", goods, ham.citybuildersim.ui.Money.money(crossed)));
        report("the money that crossed is the books' by good, in the goods the band measures", near(w.crossed(), crossed),
                String.format("%,.6f against %,.6f", w.crossed(), crossed));
        double rowsCrossed = 0;
        for (Row r : b.rows()) rowsCrossed += r.crossed();
        assertTrue("...the rows' crossings sum to it, and it is no more than the whole trade by good (the books' sold and bought)",
                near(rowsCrossed, w.crossed()) && w.crossed() <= trade.sold() + trade.bought() + 1e-9);
        boolean parts = true;
        StringBuilder diff = new StringBuilder();
        for (Part p : Part.values()) {
            boolean ok = near(w.amount(p), want[p.ordinal()]);
            parts &= ok;
            if (!ok) diff.append(String.format("%s %s v %s; ", p, w.amount(p), want[p.ordinal()]));
        }
        report("each part's money is the crossings times half the two-way piece, the city leg the way it was received or made",
                parts && w.total() > 0, diff.length() > 0 ? diff.toString() : String.format("the gap to the door %s of %s",
                        ham.citybuildersim.ui.Money.money(w.total()), ham.citybuildersim.ui.Money.money(w.crossed())));
        double shares = 0;
        for (Part p : Part.values()) shares += w.shareOfGap(p);
        assertTrue("the parts' shares of the gap sum to one", near(shares, 1));
        assertTrue("...its share of the trade is the gap over what crossed, and the part paid abroad is the margin, the lorries and the ships",
                near(w.shareOfTrade(), w.total() / w.crossed())
                        && near(w.abroad(), w.amount(Part.MARGIN) + w.amount(Part.WORLD_LORRY) + w.amount(Part.SEA)));
        assertTrue("the railway, the ships, the quay and the trucks all appear in the month's gap",
                w.amount(Part.RAIL) > 0 && w.amount(Part.SEA) > 0 && w.amount(Part.PORT) > 0 && w.amount(Part.CITY_LEG) > 0
                        && w.amount(Part.WORLD_LORRY) >= 0 && w.amount(Part.MARGIN) > 0);
    }

    /* ================================================================
       5. THE ORDER
       ================================================================ */

    static void order(Game g, Band b) {
        System.out.println("\n--- 5. the order: the goods that crossed first, the largest first - and the same band when asked twice ---");
        boolean sorted = true, crossedFirst = true;
        boolean seenQuiet = false;
        for (int i = 0; i < b.rows().size(); i++) {
            Row r = b.rows().get(i);
            if (i > 0) sorted &= r.crossed() <= b.rows().get(i - 1).crossed();
            if (r.crossed() == 0) seenQuiet = true;
            else crossedFirst &= !seenQuiet;
        }
        assertTrue("the rows are largest crossing first, and a good that crossed is never behind one that did not", sorted && crossedFirst);
        int measured = 0;
        for (Good good : Good.values()) if (measured(g, good)) measured++;
        assertTrue("a row for every good the city can buy and sell abroad, at a price - no more, no fewer", b.rows().size() == measured);
        assertTrue("the band is pure: asked twice it is the same", b.equals(TradeBand.of(g)));
    }

    /* ================================================================
       6. A RELOAD
       ================================================================ */

    static void reload(Game g, GameFiles files) {
        System.out.println("\n--- 6. a reload reads the same band: the shares in force, the quay's quote and the leg prices are saved ---");
        quietly(() -> g.saveGame(10));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(10));
        // The live city's exchange rate and the world's price level are pinned by the fixture, and a pin is the harness's, not the save's.
        back.getForeignAccounts().pinRate(1.0);
        back.getWorldEconomy().pin();
        Band live = TradeBand.of(g), loaded = TradeBand.of(back);
        report("the loaded city measures the same goods", loaded.rows().size() == live.rows().size(), loaded.rows().size() + " goods");
        // A unit's pieces need no month of trade: they are the prices, the shares in force, the quay's quote and Haulage's leg price,
        // and every one of them is saved with the city.
        boolean pieces = true;
        StringBuilder unlike = new StringBuilder();
        for (Row r : live.rows()) {
            Row l = row(loaded, r.good());
            boolean ok = l != null;
            for (Part p : Part.values()) ok = ok && l != null && near(l.amount(p), r.amount(p));
            pieces &= ok;
            if (!ok) unlike.append(r.good().name()).append(' ');
        }
        report("...and every good's pieces a unit are the live city's: the margin, the lorries, the railway, the ships, the quay, the trucks",
                pieces, unlike.toString());
        // The month's trade is the books of the month last struck; the households' cars and fuel are counted again from the next
        // month after a load (the Trade tab says so), so the whole is compared a month on, when both cities have struck one.
        quietly(() -> { g.toggleNextMonth(); back.toggleNextMonth(); });
        Band a = TradeBand.of(g), b = TradeBand.of(back);
        boolean order = a.rows().size() == b.rows().size() && sameGoods(a, b);
        report("a month on, the two cities measure the same goods in the same order", order, a.rows().size() + " goods");
        report("...with the same money crossed", near(a.whole().crossed(), b.whole().crossed()) && a.whole().crossed() > 0,
                String.format("%,.6f v %,.6f", a.whole().crossed(), b.whole().crossed()));
        boolean parts = true;
        StringBuilder diff = new StringBuilder();
        for (Part p : Part.values()) {
            boolean ok = near(a.whole().amount(p), b.whole().amount(p));
            parts &= ok;
            if (!ok) diff.append(String.format("%s %s v %s; ", p, a.whole().amount(p), b.whole().amount(p)));
        }
        report("...and every part of the whole the live city's", parts, diff.toString());
        String bad = nonFinite(b, "band");
        report("no figure of the loaded band is NaN or infinite", bad == null, bad == null ? "" : bad);
    }

    static boolean sameGoods(Band a, Band b) {
        for (int i = 0; i < a.rows().size(); i++) if (a.rows().get(i).good() != b.rows().get(i).good()) return false;
        return true;
    }

    public static void main(String[] args) throws Exception {
        noTrade();
        GameFiles files = GameFiles.scratch("tradeband-full");
        Game g = full(files);
        Band b = TradeBand.of(g);
        legs(g, b);
        lorriesAndShips(g, b);
        whole(g, b);
        order(g, b);
        reload(g, files);
        System.out.println();
        if (fails == 0) {
            System.out.println("TradeBandCheck: the Trade tab's band in legs - the world's margin, the lorries, the railway, the ships, the quay and the city leg are the model's own, sum to the band the prices show, and a city that never traded reads zeros: all passed");
        } else {
            System.out.println(fails + " FAILED");
        }
        System.exit(fails);
    }
}
