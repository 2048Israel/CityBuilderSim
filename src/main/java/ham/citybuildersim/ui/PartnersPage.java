package ham.citybuildersim.ui;

import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.Nations;
import ham.citybuildersim.NationsMap;
import ham.citybuildersim.PartnerTrade;
import ham.citybuildersim.PartnersView;
import ham.citybuildersim.WorldData;
import ham.citybuildersim.WorldMarket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleFunction;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import static ham.citybuildersim.ui.Money.fxRate;
import static ham.citybuildersim.ui.Money.ratePct;
import static ham.citybuildersim.ui.Money.shortNumber;
import static ham.citybuildersim.ui.Money.usd;
import static ham.citybuildersim.ui.Pieces.chip;
import static ham.citybuildersim.ui.Pieces.chipStrip;
import static ham.citybuildersim.ui.Pieces.divergingBars;
import static ham.citybuildersim.ui.Pieces.door;

import static ham.citybuildersim.ui.Pieces.gridCell;
import static ham.citybuildersim.ui.Pieces.gridHead;
import static ham.citybuildersim.ui.Pieces.mirrorRows;
import static ham.citybuildersim.ui.Pieces.rightAfterFirst;
import static ham.citybuildersim.ui.Pieces.sectionHead;
import static ham.citybuildersim.ui.TradeScreen.card;
import static ham.citybuildersim.ui.TradeScreen.cardHead;
import static ham.citybuildersim.ui.TradeScreen.cardLine;


import static ham.citybuildersim.ui.TradeScreen.doorCard;

import static ham.citybuildersim.ui.TradeScreen.line;
import static ham.citybuildersim.ui.TradeScreen.muted;
import static ham.citybuildersim.ui.TradeScreen.quiet;
import static ham.citybuildersim.ui.TradeScreen.row;
import static ham.citybuildersim.ui.TradeScreen.words;

/**
 * Trade &gt; Partners (WN6; docs/studies/spec-nations.md 8, board 2) and a nation's page behind it: who the city trades with, nation by nation; for a
 * chosen good, where the next unit goes and what it nets there; the good's band this month and what moves it; and, for one nation, its figures, our trade
 * with it, its money, its land and the city beside it. Every figure is PartnersView's (the snapshot, the curve and the page are immutable and safe from the
 * screen's thread); this only draws them, in TradeScreen's cards.
 * <p>
 * WHY. Jerus: "if canda only needs 10 tons of wood, more wood would have to be sold esle where say portugal, thus transportation costs are higher". The
 * model answered that in WN3 (a curve a good); nothing showed it. The page answers it for each good the city trades: the line of what the next unit nets as
 * the city sells more, the places on it where a farther buyer joins, and the buyers the city sells to now, all netting the same.
 * <p>
 * WHAT SURVIVES THE REDRAW. TradeScreen redraws every month; the nation shown, the span, the good and the goods' fold are fields here (and the folds
 * TradeScreen's openLines), so a month landing leaves the page where the player left it.
 */
final class PartnersPage {

    private final UserInterface ui;
    private final TradeScreen trade;

    /** The nation shown: its number from 1, or 0 for the table. */
    int nation = 0;

    /** The span the table is of, and the good the board is of (null: the largest sold). */
    private PartnersView.Span span = PartnersView.Span.MONTH;
    private Good good;

    /** How many rows the table shows before "+ N more nations". */
    static final int FIRST_ROWS = 12;

    /** How many goods get a chip of their own before "every good", and how many buyers or sellers a list names. */
    static final int GOOD_CHIPS = 5, LIST_ROWS = 6, GOODS_SHOWN = 10;

    PartnersPage(UserInterface ui, TradeScreen trade) {
        this.ui = ui;
        this.trade = trade;
    }

    private Game game() { return ui.game; }

    /* =====================================================================
       THE PAGE
       ===================================================================== */

    /** The page: a nation's, or the table with the good's board under it. */
    void page(VBox page) {
        // the trade first: getNations() opens the world file whenever it holds no nations, and this page is drawn on the clock
        if (game().getPartnerTrade() == null || game().getWorldMarket() == null || game().getNations() == null) {
            page.getChildren().add(card(cardHead(Icons.FLAG, Palette.BUSINESS, "WHO WE TRADE WITH", NONE_INFO, null),
                    line("There are no nations in this city.", NONE_INFO)));
            return;
        }
        if (nation > 0) {
            PartnersView.Page p = PartnersView.nation(game(), nation);
            if (p != null) {
                nationPage(page, p);
                return;
            }
            nation = 0;
        }
        tablePage(page);
    }

    static final String NONE_INFO = "This city's world has no nations in it: it was founded without the world file, so it trades on the "
            + "goods' own world prices and with \"the world\" rather than with anybody.";

    /** A door to a nation's page from anywhere on the tab. */
    void open(int nationNumber) {
        trade.openNation(nationNumber);
    }

    /* =====================================================================
       WHO WE TRADE WITH
       ===================================================================== */

    private void tablePage(VBox page) {
        PartnersView.Table table = PartnersView.table(game(), span);
        page.getChildren().add(whoCard(table));
        nextSection(page);
    }

    static final String WHO_INFO = "Every nation the city sold to or bought from, ranked by the two together. Money is the city's own, "
            + "D$; the nation's money and what one of it is worth in D$ are the rates the currency page uses. The haul is the tonnes that "
            + "crossed, out and in, and the distance runs from the city's site to the nation's trading point. Tap a nation for its page.";

    /** WHO WE TRADE WITH: the table, the month or the year so far, twelve rows and then the rest. */
    private VBox whoCard(PartnersView.Table table) {
        boolean month = table.span() == PartnersView.Span.MONTH;
        FlowPane pick = chipStrip(new String[] {"Month", "This year"}, month ? "Month" : "This year", Palette.SIZE_LABEL, name -> {
            span = "Month".equals(name) ? PartnersView.Span.MONTH : PartnersView.Span.YEAR;
            trade.showForeignMenu();
        });
        pick.setAlignment(Pos.CENTER_RIGHT);
        pick.setPrefWrapLength(180);
        HBox right = new HBox(Palette.GAP, quiet(month ? "the month last counted" : "the year so far, " + table.year()), pick);
        right.setAlignment(Pos.CENTER_RIGHT);
        VBox c = card(cardHead(Icons.FLAG, Palette.BUSINESS, "WHO WE TRADE WITH", WHO_INFO, right));
        List<PartnersView.Row> rows = table.rows();
        if (rows.isEmpty()) {
            c.getChildren().add(line(month ? "Nothing crossed the city's edge to a nation in the month last counted."
                    : "Nothing has crossed the city's edge to a nation yet this year.", null));
            return c;
        }
        boolean all = trade.openLines.contains("partners:all");
        List<PartnersView.Row> shown = all || rows.size() <= FIRST_ROWS + 1 ? rows : rows.subList(0, FIRST_ROWS);
        boolean land = false;
        for (PartnersView.Row r : rows) land |= Double.isFinite(r.landFactor());
        c.getChildren().add(table(shown, land));
        if (shown.size() < rows.size()) {
            c.getChildren().add(door("+ " + (rows.size() - shown.size()) + " more nations", Palette.ACCENT, () -> {
                trade.openLines.add("partners:all");
                trade.showForeignMenu();
            }));
        } else if (all && rows.size() > FIRST_ROWS + 1) {
            c.getChildren().add(door("the first " + FIRST_ROWS + " only", Palette.ACCENT, () -> {
                trade.openLines.remove("partners:all");
                trade.showForeignMenu();
            }));
        }
        c.getChildren().add(muted(totals(table, month)));
        return c;
    }

    /** The table: a row a nation, a column each for what the rows carry. */
    private GridPane table(List<PartnersView.Row> rows, boolean land) {
        double[] widths = land ? new double[] {26, 170, 84, 84, 58, 78, 62, 150, 84, 72, 130}
                : new double[] {26, 170, 84, 84, 58, 78, 62, 150, 84};
        javafx.geometry.HPos[] align = rightAfterFirst(widths.length);
        align[1] = javafx.geometry.HPos.LEFT;
        GridPane table = Pieces.grid(widths, align);
        table.setHgap(10);
        table.setVgap(5);
        List<String> head = new ArrayList<>(List.of("", "nation", "we sold it", "we bought", "its money", "D$ a unit", "policy rate", "the haul, out · in",
                "distance"));
        if (land) {
            head.add("land factor");
            head.add("land bought");
        }
        gridHead(table, head.toArray(new String[0]));
        int at = 1;
        for (PartnersView.Row r : rows) {
            int col = 0;
            table.add(gridCell(String.valueOf(r.rank()), Palette.TEXT_MUTED, Palette.SIZE_LABEL, true), col++, at);
            table.add(nationLink(r.name(), r.nation()), col++, at);
            table.add(gridCell(r.sold() > 0 ? trade.d(r.sold()) : "–", r.sold() > 0 ? Palette.TEXT_HEAD : Palette.TEXT_MUTED, Palette.SIZE_LABEL, true), col++, at);
            table.add(gridCell(r.bought() > 0 ? trade.d(r.bought()) : "–", r.bought() > 0 ? Palette.TEXT_HEAD : Palette.TEXT_MUTED, Palette.SIZE_LABEL, true), col++, at);
            table.add(gridCell(r.mark().isEmpty() ? "–" : r.mark(), Palette.TEXT_LABEL, Palette.SIZE_LABEL, true), col++, at);
            table.add(gridCell(Double.isFinite(r.dollarsPerUnit()) ? trade.sym() + fxRate(r.dollarsPerUnit()) : "–", Palette.TEXT_LABEL, Palette.SIZE_LABEL, true), col++, at);
            table.add(gridCell(Double.isFinite(r.policyRate()) ? ratePct(r.policyRate()) : "·", Palette.TEXT_LABEL, Palette.SIZE_LABEL, true), col++, at);
            table.add(gridCell(tonnes(r.tonnesOut()) + " · " + tonnes(r.tonnesIn()), Palette.TEXT_LABEL, Palette.SIZE_LABEL, true), col++, at);
            table.add(gridCell(Double.isFinite(r.distanceKm()) ? String.format("%,.0f km", r.distanceKm()) : "–", Palette.TEXT_LABEL, Palette.SIZE_LABEL, true), col++, at);
            if (land) {
                table.add(gridCell(Double.isFinite(r.landFactor()) ? String.format("%.2f×", r.landFactor()) + markWords(r.landMark()) : "–",
                        Palette.TEXT_LABEL, Palette.SIZE_LABEL, true), col++, at);
                table.add(gridCell(r.landKm2() > 0 ? String.format("%.2f km² · ", r.landKm2()) + NationsMap.usd(r.landUsd()) : "–",
                        r.landKm2() > 0 ? Palette.TEXT_HEAD : Palette.TEXT_MUTED, Palette.SIZE_LABEL, true), col++, at);
            }
            at++;
        }
        return table;
    }

    /** The line under the table: how many nations, the sums, and the trade that no partner took. */
    private String totals(PartnersView.Table table, boolean month) {
        PartnersView.Totals t = table.totals();
        StringBuilder s = new StringBuilder("Sold to ").append(t.nationsSoldTo()).append(t.nationsSoldTo() == 1 ? " nation" : " nations")
                .append(" (").append(trade.d(t.sold())).append("), bought from ").append(t.nationsBoughtFrom())
                .append(t.nationsBoughtFrom() == 1 ? " nation" : " nations").append(" (").append(trade.d(t.bought())).append(")");
        if (month && (t.noPartnerSold() > 0 || t.noPartnerBought() > 0)) {
            s.append(". With no partner: ");
            if (t.noPartnerSold() > 0) s.append(trade.d(t.noPartnerSold())).append(" sold");
            if (t.noPartnerSold() > 0 && t.noPartnerBought() > 0) s.append(", ");
            if (t.noPartnerBought() > 0) s.append(trade.d(t.noPartnerBought())).append(" bought");
        }
        return s.append('.').toString();
    }

    /** A nation's name as a door to its page. */
    private Label nationLink(String name, int number) {
        Label l = new Label(name);
        String rest = Palette.words(Palette.SIZE_LABEL + 1, Palette.ACCENT) + " -fx-cursor: hand;";
        l.setStyle(rest);
        l.setMinWidth(0);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setOnMouseEntered(e -> l.setStyle(rest + " -fx-underline: true;"));
        l.setOnMouseExited(e -> l.setStyle(rest));
        l.setOnMouseClicked(e -> {
            e.consume();
            open(number);
        });
        Tooltip.install(l, new Tooltip(name));
        return l;
    }

    /** Tonnes as the haul column writes them: "840 t", "12.4 kt", "3.20 Mt". */
    static String tonnes(double t) {
        if (!(t > 0)) return "0 t";
        if (t >= 1e6) return String.format("%.2f Mt", t / 1e6);
        if (t >= 1e4) return String.format("%.1f kt", t / 1e3);
        return String.format("%,.0f t", t);
    }

    /** The mark a figure carries: a published figure none, a projection a half-moon, the game's estimate a ring (NationsMap's). */
    static String markWords(int mark) {
        return mark == WorldData.MARK_PROJECTED ? " ◐" : mark == WorldData.MARK_ESTIMATE ? " ○" : "";
    }

    /** A power of ten of the share of the world's use, as the chart's axis writes it: "10%", "1%", "0.1%", "0.001%". */
    static String tickWords(double share) {
        double pct = share * 100;
        return (pct >= 1 ? String.format("%.0f", pct) : new java.text.DecimalFormat("0.######").format(pct)) + "%";
    }

    /** A share of the world's use, to the places it needs: "12%", "2.1%", "0.04%", "0.0013%". */
    static String shareWords(double share) {
        if (!Double.isFinite(share)) return "–";
        double pct = share * 100;
        if (pct >= 10) return String.format("%.0f%%", pct);
        if (pct >= 1) return String.format("%.1f%%", pct);
        if (pct >= .01) return String.format("%.2f%%", pct);
        return String.format("%.4f%%", pct);
    }

    /* =====================================================================
       WHERE THE NEXT UNIT GOES
       ===================================================================== */

    static final String NEXT_INFO = "Sell more of a good and the best buyers abroad each net a little less for the next one; at some volume a "
            + "farther buyer is worth the freight and joins in. The violet line is what the next unit nets here at each volume, the share of the "
            + "world's use a year across (a log scale: each tick is ten times the one before); the dashed one is the same once the world's own "
            + "prices have answered to that much more selling. The dot is where the city sold last month, and the ticks are where the next buyers join.";

    static final String SELL_INFO = "The buyers that took what the city sold last month. The city sells where the next unit nets most, so each "
            + "buyer takes until the next unit would net more somewhere else, and every buyer taking nets the same. A farther buyer joins when "
            + "the city sells enough to bring that level down to what it would net there.";

    static final String BUY_INFO = "The nations that sold what the city bought last month, cheapest landed cost first: the price at the nation's "
            + "mill, the freight to the city, and the same level for every seller taking. A dearer seller joins when the city buys enough to need it.";

    static final String BAND_INFO = "The floor is what the world would net the city for the next unit at the volume it sold last month: the best "
            + "buyer's price less the haul, and less what the world's own prices have answered to the selling. The ceiling is what the world would "
            + "charge to bring one in at the volume it bought. The city's market sits between them, with the railway's freight moving both ends.";

    static final String MOVES_INFO = "Three things take from what the next unit nets. The city's own selling pushes the nearest buyers' prices "
            + "down as they take more; the world's answer is how far its prices have fallen (or risen) to the city selling more than it buys; "
            + "and the freight is the haul to the first buyer, which every unit pays.";

    /** The second card and the three under it, for the good chosen. */
    private void nextSection(VBox page) {
        PartnersView.Goods goods = PartnersView.goods(game());
        if (goods.sold().isEmpty() && goods.others().isEmpty()) return;
        List<Good> all = new ArrayList<>(goods.sold());
        all.addAll(goods.others());
        if (good == null || !all.contains(good)) good = goods.sold().isEmpty() ? goods.others().get(0) : goods.sold().get(0);
        PartnersView.GoodBoard board = PartnersView.good(game(), good);
        if (board == null) return;
        page.getChildren().add(nextCard(goods, board));
        page.getChildren().add(row(sellNowCard(board), bandCard(board), movesCard(board)));
    }

    /** WHERE THE NEXT UNIT GOES: the good's chips, the chart, its key. */
    private VBox nextCard(PartnersView.Goods goods, PartnersView.GoodBoard board) {
        Good g = board.good();
        VBox c = card(cardHead(Icons.ofGood(g), Palette.BUSINESS, "WHERE THE NEXT " + g.unit().toUpperCase() + " GOES", NEXT_INFO,
                quiet(g.label() + " · what the next one nets here, by how much we sell")));
        c.getChildren().add(goodChips(goods, g));
        WorldMarket.Curve curve = board.curve();
        List<WorldMarket.Curve.Point> pts = curve.points();
        if (pts.size() < 2) {
            c.getChildren().add(muted(curve.sinks().isEmpty() || !g.exportable()
                    ? "Nobody abroad buys " + g.label().toLowerCase() + " from this city, so there is no line of what the next one nets. Who sells it to us is below."
                    : "The buyers are too few to draw a line."));
            return c;
        }
        double[] xs = new double[pts.size()], net = new double[pts.size()], world = new double[pts.size()];
        for (int i = 0; i < xs.length; i++) {
            xs[i] = pts.get(i).share();
            net[i] = board.inCityMoney(pts.get(i).net());
            world[i] = board.inCityMoney(pts.get(i).world());
        }
        CurveChart.Dot city = null;
        double sold = board.exportUnits(), level = curve.netAt(sold);
        if (sold > 0 && Double.isFinite(level)) {
            city = new CurveChart.Dot(curve.shareOfWorldUse(sold), board.inCityMoney(level),
                    "we sold " + volume(sold, g) + " a month · nets " + trade.unitD(board.inCityMoney(level)));
        }
        List<CurveChart.Join> joins = new ArrayList<>();
        Nations nations = game().getNations();
        for (WorldMarket.Curve.Sink s : curve.nextBuyers(sold, 12)) joins.add(new CurveChart.Join(s.joinsAtShare(), nations.name(s.buyer())));
        CurveChart chart = new CurveChart(xs, net, world, city, joins, trade::unitD, PartnersPage::tickWords);
        c.getChildren().add(chart);
        c.getChildren().add(chartKey());
        return c;
    }

    /** The chips: the goods the city sold abroad (the first few), the good chosen, and "every good" to open the rest. */
    private Node goodChips(PartnersView.Goods goods, Good chosen) {
        List<Good> first = new ArrayList<>(goods.sold().subList(0, Math.min(GOOD_CHIPS, goods.sold().size())));
        if (!first.contains(chosen)) first.add(chosen);
        VBox box = new VBox(6);
        box.getChildren().add(goodStrip(first, chosen, true));
        boolean open = trade.openLines.contains("partners:goods");
        List<Good> rest = new ArrayList<>();
        for (Good g : goods.sold()) if (!first.contains(g)) rest.add(g);
        for (Good g : goods.others()) if (!first.contains(g)) rest.add(g);
        if (!rest.isEmpty()) {
            if (open) box.getChildren().add(goodStrip(rest, chosen, false));
            box.getChildren().add(door(open ? "fewer goods" : "every good (" + rest.size() + " more)", Palette.ACCENT, () -> {
                if (!trade.openLines.remove("partners:goods")) trade.openLines.add("partners:goods");
                trade.showForeignMenu();
            }));
        }
        return box;
    }

    private FlowPane goodStrip(List<Good> list, Good chosen, boolean leftAligned) {
        String[] names = new String[list.size()];
        for (int i = 0; i < names.length; i++) names[i] = list.get(i).label();
        FlowPane strip = chipStrip(names, chosen.label(), Palette.SIZE_LABEL, name -> {
            for (Good g : list) if (g.label().equals(name)) good = g;
            trade.showForeignMenu();
        });
        strip.setAlignment(Pos.CENTER_LEFT);
        return strip;
    }

    /** The chart's key: the month's line, the world's, the dot and the ticks. */
    private Node chartKey() {
        return TradeScreen.keyRow(lineKey(Palette.BUSINESS, false, "what the next one nets, as we sell more"),
                lineKey(Palette.BUSINESS_LIGHT, true, "the same once the world's prices have answered"),
                TradeScreen.swatch(Palette.TEXT_HEAD, "where we sold last month"),
                TradeScreen.swatch(Palette.TEXT_MUTED, "where the next buyers join"));
    }

    private static HBox lineKey(String colour, boolean dashed, String name) {
        Line l = new Line(0, 6, 22, 6);
        l.setStroke(Color.web(colour));
        l.setStrokeWidth(2.5);
        if (dashed) l.getStrokeDashArray().setAll(5.0, 3.0);
        Label n = new Label(name);
        n.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        Pane sample = new Pane(l);
        sample.setMinSize(22, 12);
        sample.setPrefSize(22, 12);
        sample.setMaxSize(22, 12);
        HBox item = new HBox(6, sample, n);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    /* ----- AT WHAT WE SELL NOW ----- */

    /** AT WHAT WE SELL NOW (or, for a good the city only buys, WHO SELLS IT TO US). */
    private VBox sellNowCard(PartnersView.GoodBoard b) {
        WorldMarket.Curve curve = b.curve();
        Nations nations = game().getNations();
        Good g = b.good();
        boolean sells = g.exportable() && !curve.sinks().isEmpty();
        if (!sells) return sellersCard(b);
        VBox c = card(cardHead(Icons.ofGood(g), Palette.BUSINESS, "AT WHAT WE SELL NOW", SELL_INFO,
                quiet(b.exportUnits() > 0 ? volume(b.exportUnits(), g) + " a month" : "nothing sold last month")));
        double x = b.exportUnits();
        if (!(x > 0)) {
            WorldMarket.Curve.Sink first = curve.sinks().get(0);
            c.getChildren().add(line("The city sold none of it last month.", null));
            c.getChildren().add(line("The first buyer would be " + nations.name(first.buyer()) + ", netting " + trade.unitD(b.inCityMoney(first.netback()))
                    + " a " + g.unit() + ", then " + joinedWords(curve, 0, 3) + ".", null));
            addBought(c, b);
            return c;
        }
        WorldMarket.Curve.Level level = curve.levelAt(x);
        List<PartnersView.Part> taking = PartnersView.buyers(curve, x);
        String nets = trade.unitD(b.inCityMoney(level.level()));
        int shown = Math.min(LIST_ROWS, taking.size());
        for (int i = 0; i < shown; i++) {
            PartnersView.Part p = taking.get(i);
            c.getChildren().add(cardLine(nations.name(p.nation()) + " · " + shareOfSale(p.share()), "nets " + nets, null));
        }
        if (taking.size() > shown) {
            double rest = 0;
            for (PartnersView.Part p : taking.subList(shown, taking.size())) rest += p.share();
            c.getChildren().add(muted("+ " + (taking.size() - shown) + " more buyers, " + shareOfSale(rest) + " between them, netting the same."));
        }
        c.getChildren().add(line("Every buyer nets the same: we sell to each until the next unit would net more somewhere else.", null));
        List<WorldMarket.Curve.Sink> next = curve.nextBuyers(x, 3);
        if (!next.isEmpty()) {
            StringBuilder s = new StringBuilder("Next to buy: ");
            for (int i = 0; i < next.size(); i++) {
                if (i > 0) s.append(", ");
                s.append(nations.name(next.get(i).buyer())).append(" at ").append(shareWords(next.get(i).joinsAtShare())).append(" of the world's use");
            }
            c.getChildren().add(muted(s.append('.').toString()));
        }
        addBought(c, b);
        return c;
    }

    /** "the first three sinks after k": "Cuba at 2.1% of the world's use, ..." for the buyers that would take it first. */
    private String joinedWords(WorldMarket.Curve curve, int from, int count) {
        Nations nations = game().getNations();
        StringBuilder s = new StringBuilder();
        List<WorldMarket.Curve.Sink> sinks = curve.sinks();
        for (int i = from + 1; i < Math.min(sinks.size(), from + 1 + count - 1); i++) {
            if (s.length() > 0) s.append(", ");
            s.append(nations.name(sinks.get(i).buyer())).append(" at ").append(shareWords(sinks.get(i).joinsAtShare()));
        }
        return s.length() == 0 ? "no one else" : s + " of the world's use";
    }

    /** A volume with its unit: "46 cubic metres", "1.5 tonnes", "0.40 vans". */
    static String volume(double units, Good g) {
        String n = units >= 10 ? shortNumber(units) : units >= 1 ? String.format("%.1f", units) : String.format("%.2f", units);
        return n + " " + g.unit() + (Math.abs(units - 1) < 1e-9 ? "" : "s");
    }

    /** A share of what the city sold: "89%", "7%", "0.4%". */
    static String shareOfSale(double share) {
        double pct = share * 100;
        if (pct >= 1) return String.format("%.0f%%", pct);
        return String.format("%.1f%%", pct);
    }

    /** Under the buyers, what the city bought of the same good, when it did: who sold it and what it landed at. */
    private void addBought(VBox c, PartnersView.GoodBoard b) {
        WorldMarket.Curve curve = b.curve();
        if (!(b.importUnits() > 0) || curve.sellers().isEmpty()) return;
        Nations nations = game().getNations();
        List<PartnersView.Part> from = PartnersView.sellers(curve, b.importUnits());
        if (from.isEmpty()) return;
        StringBuilder s = new StringBuilder("It also bought ").append(volume(b.importUnits(), b.good())).append(" a month, from ");
        for (int i = 0; i < Math.min(3, from.size()); i++) {
            if (i > 0) s.append(", ");
            s.append(nations.name(from.get(i).nation())).append(' ').append(shareOfSale(from.get(i).share()));
        }
        if (from.size() > 3) s.append(" and ").append(from.size() - 3).append(" more");
        s.append(", landed at ").append(trade.unitD(b.inCityMoney(curve.costLevelAt(b.importUnits()).level()))).append('.');
        c.getChildren().add(muted(s.toString()));
    }

    /** WHO SELLS IT TO US: for a good the city only buys, the sellers at what it buys now, and the next to sell. */
    private VBox sellersCard(PartnersView.GoodBoard b) {
        WorldMarket.Curve curve = b.curve();
        Nations nations = game().getNations();
        Good g = b.good();
        VBox c = card(cardHead(Icons.ofGood(g), Palette.BUSINESS_DARK, "WHO SELLS IT TO US", BUY_INFO,
                quiet(b.importUnits() > 0 ? volume(b.importUnits(), g) + " a month" : "nothing bought last month")));
        if (curve.sellers().isEmpty()) {
            c.getChildren().add(line("Nobody abroad sells it to this city, and the city sells none abroad.", null));
            return c;
        }
        double m = b.importUnits();
        if (!(m > 0)) {
            WorldMarket.Curve.Seller first = curve.sellers().get(0);
            c.getChildren().add(line("The city bought none of it last month.", null));
            c.getChildren().add(line("The cheapest seller is " + nations.name(first.nation()) + ", landed here at "
                    + trade.unitD(b.inCityMoney(first.landed())) + " a " + g.unit() + ".", null));
            return c;
        }
        WorldMarket.Curve.Level level = curve.costLevelAt(m);
        List<PartnersView.Part> from = PartnersView.sellers(curve, m);
        String cost = trade.unitD(b.inCityMoney(level.level()));
        int shown = Math.min(LIST_ROWS, from.size());
        for (int i = 0; i < shown; i++) {
            PartnersView.Part p = from.get(i);
            c.getChildren().add(cardLine(nations.name(p.nation()) + " · " + shareOfSale(p.share()), "lands at " + cost, null));
        }
        if (from.size() > shown) c.getChildren().add(muted("+ " + (from.size() - shown) + " more sellers at the same landed cost."));
        c.getChildren().add(line("Every seller asks the same landed: we buy from each until the next unit would cost less somewhere else.", null));
        int taking = level.taking();
        if (taking < curve.sellers().size()) {
            StringBuilder s = new StringBuilder("Next to sell: ");
            List<WorldMarket.Curve.Seller> next = curve.sellers().subList(taking, Math.min(curve.sellers().size(), taking + 3));
            for (int i = 0; i < next.size(); i++) {
                if (i > 0) s.append(", ");
                s.append(nations.name(next.get(i).nation())).append(" at ").append(shareWords(next.get(i).joinsAtShare())).append(" of the world's use");
            }
            c.getChildren().add(muted(s.append('.').toString()));
        }
        return c;
    }

    /* ----- THE BAND THIS MONTH ----- */

    /** THE BAND THIS MONTH: the floor, the price here, the ceiling, each with where it comes from. */
    private VBox bandCard(PartnersView.GoodBoard b) {
        Nations nations = game().getNations();
        Good g = b.good();
        VBox c = card(cardHead(Icons.ofGood(g), Palette.ORE, "THE BAND THIS MONTH", BAND_INFO, quiet("D$ a " + g.unit())));
        boolean any = false;
        if (Double.isFinite(b.floor())) {
            any = true;
            c.getChildren().add(bandRow("The floor", trade.unitD(b.inCityMoney(b.floor())),
                    "set by " + nations.name(b.floorNation()) + ": what the buyers abroad net us for the next one, at what we sell now."));
        }
        if (Double.isFinite(b.sellsAt())) {
            any = true;
            c.getChildren().add(bandRow("We sell it at", trade.unitD(b.sellsAt()),
                    "what the market pays for one this month: the floor, in " + trade.sym() + ", with the railway's freight on it."));
        }
        if (Double.isFinite(b.buysAt())) {
            any = true;
            c.getChildren().add(bandRow("We buy it at", trade.unitD(b.buysAt()),
                    "what the market charges to bring one in this month: the ceiling, in " + trade.sym() + ", with the freight on it."));
        }
        if (Double.isFinite(b.ceiling())) {
            any = true;
            c.getChildren().add(bandRow("The ceiling", trade.unitD(b.inCityMoney(b.ceiling())),
                    "set by " + nations.name(b.ceilingNation()) + ": what the cheapest seller abroad would ask to land one here."));
        }
        if (!any) c.getChildren().add(muted("The world neither buys nor sells this from the city: there is no band."));
        return c;
    }

    private static VBox bandRow(String name, String figure, String reason) {
        HBox top = cardLine(name, figure, null);
        return new VBox(1, top, muted(reason));
    }

    /* ----- WHAT MOVES IT ----- */

    /** WHAT MOVES IT: our own selling, the world's answer, the freight to the first buyer, each a small diverging line in D$ a unit. */
    private VBox movesCard(PartnersView.GoodBoard b) {
        Good g = b.good();
        VBox c = card(cardHead(Icons.ofGood(g), Palette.BUSINESS, "WHAT MOVES IT", MOVES_INFO, quiet("D$ a " + g.unit() + " off what the next one nets")));
        PartnersView.Moves m = b.moves();
        List<Pieces.Force> rows = new ArrayList<>();
        double scale = 0;
        scale = add(rows, scale, "Our own selling", b.inCityMoney(m.own()), "the month's line at what we sell, against at none");
        scale = add(rows, scale, "The world's answer", b.inCityMoney(m.world()), "how far its prices answer by January to what we sell, less what we buy");
        scale = add(rows, scale, "Freight to the first buyer", b.inCityMoney(m.freight()), "the haul to " + firstBuyer(b) + ", paid on every unit");
        if (rows.isEmpty()) {
            c.getChildren().add(muted("Nothing moves it: the world has no buyer for this from the city."));
            return c;
        }
        c.getChildren().add(divergingBars(rows, scale * 1.05, 150, 82, Palette.BUSINESS_LIGHT, Palette.BUSINESS, "‹ takes off", "adds ›",
                this::signedUnit));
        return c;
    }

    private String firstBuyer(PartnersView.GoodBoard b) {
        return b.curve().sinks().isEmpty() ? "the first buyer" : game().getNations().name(b.curve().sinks().get(0).buyer());
    }

    private static double add(List<Pieces.Force> rows, double scale, String name, double value, String sentence) {
        if (!Double.isFinite(value)) return scale;
        rows.add(new Pieces.Force(name, value, false, false, null, sentence));
        return Math.max(scale, Math.abs(value));
    }

    /** A difference in D$ a unit, signed, with a true minus. */
    private String signedUnit(double thousands) {
        String shown = trade.unitD(Math.abs(thousands));
        if (Math.abs(thousands) < 1e-9) return shown;
        return (thousands < 0 ? "−" : "+") + shown;
    }

    /* =====================================================================
       THE OVERVIEW'S STRIP
       ===================================================================== */

    static final String STRIP_INFO = "The five nations the city traded most with in the month last counted, sold and bought together. Tap one "
            + "for its page, or the door for all of them.";

    /** OUR PARTNERS THIS MONTH: the top five of the month's table as small door cards, and a door to the page; null where there is no table or no partner. */
    Node strip() {
        PartnersView.Table table = PartnersView.table(game(), PartnersView.Span.MONTH);
        if (table == null || table.rows().isEmpty()) return null;
        List<Node> cards = new ArrayList<>();
        for (PartnersView.Row r : table.rows().subList(0, Math.min(5, table.rows().size()))) {
            Label name = new Label(r.name());
            name.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
            name.setMinWidth(0);
            VBox c = card(name,
                    muted("sold " + (r.sold() > 0 ? trade.d(r.sold()) : "–") + " · bought " + (r.bought() > 0 ? trade.d(r.bought()) : "–")),
                    muted("its money " + (r.mark().isEmpty() ? "–" : r.mark())
                            + (Double.isFinite(r.dollarsPerUnit()) ? " · 1 = " + trade.sym() + fxRate(r.dollarsPerUnit()) : "")));
            cards.add(doorCard(c, () -> open(r.nation())));
        }
        VBox box = new VBox(Palette.GAP, sectionHead("OUR PARTNERS THIS MONTH", STRIP_INFO,
                door("All the partners", Palette.ACCENT, () -> trade.open(TradeScreen.PARTNERS))), row(cards.toArray(new Node[0])));
        return box;
    }

    /** "to 38 nations" / "from 24 nations" for the five figures' notes, or null where the month's table has none. */
    String reachWords(boolean sold) {
        PartnersView.Table table = PartnersView.table(game(), PartnersView.Span.MONTH);
        if (table == null) return null;
        int n = sold ? table.totals().nationsSoldTo() : table.totals().nationsBoughtFrom();
        return n > 0 ? (sold ? "to " : "from ") + n + (n == 1 ? " nation" : " nations") : null;
    }

    /* =====================================================================
       A NATION'S PAGE
       ===================================================================== */

    static final String FIGURES_INFO = "The nation's own measures for this year, from the world file the game was built on: its output, its people "
            + "and the output a head, each with its rank among all of them, its growth and prices this year, and its money. A ring or a half-moon "
            + "beside a figure means the game estimated or projected it; none means a published figure. They are the very rows the map card shows.";

    static final String OURS_INFO = "What the city sold this nation and bought from it, in D$ and in tonnes, this month and so far this year, "
            + "and by good. Sold goes to the right of the bars and bought to the left, as on the Overview. The finished years are the history's.";

    static final String MONEY_INFO = "What this nation's money is worth against the US$ and in D$, its central bank's policy rate where it is one of "
            + "the eight the vault can hold, and what the vault holds of it and the city owes in it.";

    static final String LAND_INFO = "Land is bought from the nation it lies in, at that nation's land price, in its money. The factor is its price "
            + "against Canada's, the reference; the published price is the nation's own a hectare. The mark says how sure the table is of it.";

    static final String BESIDE_INFO = "For comparison only: the city's GDP converted to real 2015 US$, the unit the nations' output is counted in, "
            + "set beside the nation's. Nothing in the city's economy reads it.";

    private void nationPage(VBox page, PartnersView.Page p) {
        page.getChildren().add(nationHead(p));
        // the nation's own figures, money, land and the city beside it down the left; our trade with it, which is long, on the right
        VBox left = new VBox(Palette.GAP_LOOSE, figuresCard(p), moneyCard(p));
        if (Double.isFinite(p.landFactor())) left.getChildren().add(landCard(p));
        left.getChildren().add(besideCard(p));
        page.getChildren().add(row(left, oursCard(p)));
        HBox doors = new HBox(Palette.GAP_LOOSE, door("All its measures", Palette.MONEY, () -> ui.rankingsScreen.openOn(p.iso3())),
                door("All partners", Palette.ACCENT, () -> {
                    nation = 0;
                    ui.innerScrollAt.remove("showForeignMenu:body");
                    trade.showForeignMenu();
                }));
        doors.setAlignment(Pos.CENTER_LEFT);
        page.getChildren().add(doors);
    }

    /** The head: a way back, the nation's name, and its money. */
    private Node nationHead(PartnersView.Page p) {
        Label back = new Label("‹ All partners");
        String rest = Palette.words(Palette.SIZE_LABEL + 1, Palette.ACCENT) + " -fx-cursor: hand;";
        back.setStyle(rest);
        back.setMinWidth(Region.USE_PREF_SIZE);
        back.setOnMouseEntered(e -> back.setStyle(rest + " -fx-underline: true;"));
        back.setOnMouseExited(e -> back.setStyle(rest));
        back.setOnMouseClicked(e -> {
            nation = 0;
            ui.innerScrollAt.remove("showForeignMenu:body");
            trade.showForeignMenu();
        });
        Label name = new Label(p.name());
        name.setStyle(Palette.strong(Palette.SIZE_TITLE, Palette.TEXT_HEAD));
        name.setMinWidth(Region.USE_PREF_SIZE);
        HBox head = new HBox(Palette.GAP_LOOSE, back, name);
        head.setAlignment(Pos.BASELINE_LEFT);
        if (p.money() != null) head.getChildren().add(chip(p.money() + (p.mark().equals(p.money()) ? "" : " · " + p.mark().trim()), Palette.BUSINESS));
        return head;
    }

    /** ITS FIGURES: label · value · rank, the map card's rows. */
    private VBox figuresCard(PartnersView.Page p) {
        VBox c = card(cardHead(Icons.FLAG, Palette.BUSINESS, "ITS FIGURES", FIGURES_INFO, quiet(String.valueOf(p.year()))));
        GridPane g = new GridPane();
        g.setHgap(Palette.GAP + 6);
        g.setVgap(5);
        javafx.scene.layout.ColumnConstraints labels = new javafx.scene.layout.ColumnConstraints(),
                values = new javafx.scene.layout.ColumnConstraints(), ranks = new javafx.scene.layout.ColumnConstraints();
        values.setHgrow(Priority.ALWAYS);
        values.setMinWidth(0);
        ranks.setHalignment(javafx.geometry.HPos.RIGHT);
        g.getColumnConstraints().addAll(labels, values, ranks);
        int at = 0;
        for (NationsMap.Row r : p.figureRows()) {
            Label k = new Label(r.label());
            k.setStyle(Palette.words(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
            k.setMinWidth(Region.USE_PREF_SIZE);
            Label v = new Label(r.value());
            v.setStyle(Palette.figureRegular(Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD));
            v.setWrapText(true);
            v.setMinWidth(0);
            Label rank = new Label(r.rank());
            rank.setStyle(Palette.figureRegular(Palette.SIZE_LABEL + 1, Palette.TEXT_MUTED));
            rank.setMinWidth(Region.USE_PREF_SIZE);
            g.add(k, 0, at);
            g.add(v, 1, at);
            g.add(rank, 2, at);
            at++;
        }
        c.getChildren().add(g);
        if (Double.isFinite(p.distanceKm())) c.getChildren().add(muted(String.format("%,.0f km from the city.", p.distanceKm())));
        return c;
    }

    /** OUR TRADE WITH IT: the month, the year so far, by good, and the finished years. */
    private VBox oursCard(PartnersView.Page p) {
        VBox c = card(cardHead(Icons.LORRY, Palette.BUSINESS, "OUR TRADE WITH IT", OURS_INFO, quiet("bought left, sold right")));
        c.getChildren().add(cardLine("this month", "sold " + trade.d(p.monthSold()) + " · bought " + trade.d(p.monthBought()), null));
        c.getChildren().add(cardLine("this year so far", "sold " + trade.d(p.yearSold()) + " · bought " + trade.d(p.yearBought()), null));
        c.getChildren().add(cardLine("the haul this year", tonnes(p.yearTonnesOut()) + " out · " + tonnes(p.yearTonnesIn()) + " in", null));
        if (!p.goods().isEmpty()) {
            c.getChildren().add(words("THIS YEAR, BY GOOD", Palette.SIZE_LABEL, Palette.TEXT_LABEL));
            List<PartnerTrade.Cell> top = p.goods().subList(0, Math.min(GOODS_SHOWN, p.goods().size()));
            double scale = 0;
            for (PartnerTrade.Cell cell : p.goods()) scale = Math.max(scale, Math.max(cell.valueIn(), cell.valueOut()));
            final double shared = scale;
            c.getChildren().add(goodBars(top, shared, true));
            if (p.goods().size() > top.size()) {
                List<PartnerTrade.Cell> rest = p.goods().subList(top.size(), p.goods().size());
                c.getChildren().add(trade.fold("partners:goods:" + p.nation(), rest.size() + " more goods", () -> goodBars(rest, shared, false)));
            }
        }
        if (p.years().stream().anyMatch(y -> y.valueOut() > 0 || y.valueIn() > 0)) {
            c.getChildren().add(words("ITS FINISHED YEARS", Palette.SIZE_LABEL, Palette.TEXT_LABEL));
            c.getChildren().add(yearBars(p.years()));
        }
        return c;
    }

    private Node goodBars(List<PartnerTrade.Cell> cells, double scale, boolean heads) {
        List<Pieces.Mirror> rows = new ArrayList<>();
        for (PartnerTrade.Cell cell : cells) {
            rows.add(new Pieces.Mirror(Icons.ofGood(cell.good()), cell.good().label(), cell.valueIn(), cell.valueOut(),
                    cell.valueIn() > 0 ? trade.d(cell.valueIn()) : null, cell.valueOut() > 0 ? trade.d(cell.valueOut()) : null, null,
                    cell.good().label() + "\nsold " + trade.dFull(cell.valueOut()) + " · bought " + trade.dFull(cell.valueIn()), null));
        }
        return mirrorRows(rows, scale, Palette.BUSINESS_DARK, Palette.BUSINESS, heads ? "BOUGHT" : null, heads ? "SOLD" : null, 150, 0);
    }

    private Node yearBars(List<PartnersView.YearRow> years) {
        List<Pieces.Mirror> rows = new ArrayList<>();
        for (int i = years.size() - 1; i >= 0 && rows.size() < 10; i--) {
            PartnersView.YearRow y = years.get(i);
            if (!(y.valueOut() > 0) && !(y.valueIn() > 0)) continue;
            rows.add(new Pieces.Mirror(null, String.valueOf(y.year()), y.valueIn(), y.valueOut(), y.valueIn() > 0 ? trade.d(y.valueIn()) : null,
                    y.valueOut() > 0 ? trade.d(y.valueOut()) : null, null,
                    y.year() + "\nsold " + trade.dFull(y.valueOut()) + " · bought " + trade.dFull(y.valueIn()), null));
        }
        return mirrorRows(rows, 0, Palette.BUSINESS_DARK, Palette.BUSINESS, "BOUGHT", "SOLD", 60, 0);
    }

    /** ITS MONEY: the rate, what one is worth in D$, the policy rate, the vault's holding and what the city owes in it. */
    private VBox moneyCard(PartnersView.Page p) {
        VBox c = card(cardHead(Icons.EXCHANGE, Palette.MONEY, "ITS MONEY", MONEY_INFO, quiet(p.money() == null ? "" : p.money())));
        if (p.money() == null) {
            c.getChildren().add(line("The file names no money for this nation this year.", null));
            return c;
        }
        if (Nations.DOLLAR.equals(p.money())) {
            c.getChildren().add(cardLine("the money", "the US dollar", null));
        } else if (Double.isFinite(p.unitsPerUsd())) {
            c.getChildren().add(cardLine("the rate", fxRate(p.unitsPerUsd()) + " " + p.money() + " a US$", null));
        }
        if (Double.isFinite(p.dollarsPerUnit())) {
            c.getChildren().add(cardLine("one of it is worth", trade.sym() + fxRate(p.dollarsPerUnit()), null));
        }
        if (Double.isFinite(p.policyRate())) {
            c.getChildren().add(cardLine("its policy rate", ratePct(p.policyRate()) + " a year", null));
        }
        if (p.vaultIndex() >= 0) {
            c.getChildren().add(cardLine("the vault holds", p.vaultHeld() > 0 ? (p.vaultIndex() == 0 ? "" : TradeScreen.ownMoney(p.vaultIndex(), p.vaultHeld()) + " · ") + usd(p.vaultHeldUsd()) : "none", null));
            c.getChildren().add(cardLine("the city owes in it", p.owedOwn() > 0 ? (p.vaultIndex() == 0 ? "" : TradeScreen.ownMoney(p.vaultIndex(), p.owedOwn()) + " · ") + usd(p.owedUsd()) : "nothing", null));
        } else {
            c.getChildren().add(muted("Not one of the eight moneys the vault can hold."));
        }
        return c;
    }

    /** ITS LAND: its factor and mark, the published price a hectare, and what the city bought from it. */
    private VBox landCard(PartnersView.Page p) {
        VBox c = card(cardHead(Icons.MAP, Palette.BUILDING, "ITS LAND", LAND_INFO, null));
        c.getChildren().add(cardLine("its land factor", String.format("%.2f×", p.landFactor()) + markWords(p.landMark()), null));
        if (Double.isFinite(p.landUsdPerHectare())) {
            c.getChildren().add(cardLine("the published price a hectare", NationsMap.usd(p.landUsdPerHectare()), null));
        }
        c.getChildren().add(cardLine("the city bought from it", p.landKm2() > 0 ? String.format("%.2f km² · ", p.landKm2()) + NationsMap.usd(p.landUsd()) : "none yet", null));
        return c;
    }

    /** THE CITY BESIDE IT: the city's GDP in real 2015 US$ a year beside its output, and the share. */
    private VBox besideCard(PartnersView.Page p) {
        VBox c = card(cardHead(Icons.POPULATION, Palette.PEOPLE, "THE CITY BESIDE IT", BESIDE_INFO, quiet("real 2015 US$, a year")));
        c.getChildren().add(cardLine("the city's GDP", NationsMap.usd(p.cityGdpUsd()), null));
        c.getChildren().add(cardLine("its output", NationsMap.usd(p.output()) + markWords(p.outputMark()), null));
        if (Double.isFinite(p.gdpShare())) {
            c.getChildren().add(line("For comparison: the city's output is " + sharePercent(p.gdpShare()) + " of " + p.name() + (p.name().endsWith("s") ? "'" : "'s") + ".", null));
        }
        return c;
    }

    /** A small share of an output: "0.04%", "12%", "<0.001%". */
    static String sharePercent(double share) {
        double pct = share * 100;
        if (pct >= 10) return String.format("%.0f%%", pct);
        if (pct >= 1) return String.format("%.1f%%", pct);
        if (pct >= .01) return String.format("%.2f%%", pct);
        if (pct >= .001) return String.format("%.3f%%", pct);
        return "less than 0.001%";
    }

    /* =====================================================================
       THE CHART - drawn as shapes in a Pane
       ===================================================================== */

    /**
     * What the next unit nets (up, D$ a unit) against how much the city sells (across, the share of the world's use a year, on a log scale): the
     * month's line solid in the export violet, the world's dashed, a dot where the city sold last month, and ticks where the next buyers join, the first
     * three named. It is handed arrays and laid out at whatever width it is given; the shapes are made once and only moved.
     */
    static final class CurveChart extends Pane {
        /** The dot: where the city sold, its share of the world's use, what the next one nets there, its words. */
        record Dot(double share, double value, String words) { }

        /** A tick: where a buyer joins (a share of the world's use) and its name. */
        record Join(double share, String name) { }

        static final double HEIGHT = 250, LEFT = 74, RIGHT = 18, TOP = 22, BOTTOM = 44;

        private final double xLo, xHi, yHi;
        private final double[] xs, net, world;
        private final Dot dot;
        private final List<Join> joins;
        private final List<Line> yGrid = new ArrayList<>(), xGrid = new ArrayList<>();
        private final List<Label> yLabels = new ArrayList<>(), xLabels = new ArrayList<>(), joinLabels = new ArrayList<>();
        private final List<Line> joinLines = new ArrayList<>();
        private final List<Polygon> joinMarks = new ArrayList<>();
        private final List<Join> drawnJoins = new ArrayList<>();
        private final List<Double> yTicks = new ArrayList<>(), xTicks = new ArrayList<>();
        private final Polyline netLine = new Polyline(), worldLine = new Polyline();
        private final Line axis = new Line();
        private final Circle mark = new Circle(5.5);
        private final Line drop = new Line();
        private final Label dotLabel = new Label(), xCaption = new Label("share of the world's use a year · a log scale"), yCaption = new Label();

        CurveChart(double[] xs, double[] net, double[] world, Dot dot, List<Join> joins, DoubleFunction<String> money, DoubleFunction<String> share) {
            this.xs = xs;
            this.net = net;
            this.world = world;
            this.dot = dot;
            setMinHeight(HEIGHT);
            setPrefHeight(HEIGHT);
            setMaxHeight(HEIGHT);
            setMinWidth(0);
            setMaxWidth(Double.MAX_VALUE);
            xLo = xs[0];
            xHi = xs[xs.length - 1];
            double top = 0;
            for (int i = 0; i < xs.length; i++) top = Math.max(top, Math.max(net[i], world[i]));
            if (dot != null) top = Math.max(top, dot.value());
            // y: from nothing to a nice figure over the top
            double step = niceStep(top / 4);
            double hi = Math.ceil(top / step) * step;
            yHi = hi > 0 ? hi : 1;
            for (double v = 0; v <= yHi + step * .01; v += step) yTicks.add(v);
            // x: a tick at each power of ten inside the range
            for (double t = Math.pow(10, Math.ceil(Math.log10(xLo) - 1e-9)); t <= xHi * (1 + 1e-9); t *= 10) xTicks.add(t);
            Color faint = Color.web(Palette.EDGE), text = Color.web(Palette.TEXT_MUTED);
            for (double v : yTicks) {
                Line l = new Line();
                l.setStroke(faint);
                yGrid.add(l);
                yLabels.add(small(money.apply(v), Palette.TEXT_MUTED));
            }
            for (double t : xTicks) {
                Line l = new Line();
                l.setStroke(faint);
                xGrid.add(l);
                xLabels.add(small(share.apply(t), Palette.TEXT_MUTED));
            }
            axis.setStroke(Color.web(Palette.CONTROL_EDGE));
            netLine.setStroke(Color.web(Palette.BUSINESS));
            netLine.setStrokeWidth(2.5);
            netLine.setFill(null);
            worldLine.setStroke(Color.web(Palette.BUSINESS_LIGHT));
            worldLine.setStrokeWidth(2);
            worldLine.setFill(null);
            worldLine.getStrokeDashArray().setAll(6.0, 4.0);
            mark.setFill(Color.web(Palette.TEXT_HEAD));
            mark.setStroke(Color.web(Palette.BUSINESS));
            mark.setStrokeWidth(2.5);
            drop.setStroke(Color.web(Palette.TEXT_MUTED));
            drop.getStrokeDashArray().setAll(2.0, 3.0);
            dotLabel.setText(dot == null ? "" : dot.words());
            dotLabel.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_HEAD) + " -fx-background-color: " + Pieces.tint(Palette.RAISED, .92)
                    + "; -fx-background-radius: 3; -fx-padding: 1 5 1 5;");
            xCaption.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            yCaption.setText("what the next one nets, D$ a unit");
            yCaption.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            this.joins = new ArrayList<>();
            for (Join j : joins) {
                if (j.share() <= xLo || j.share() > xHi) continue;
                this.joins.add(j);
                Line l = new Line();
                l.setStroke(text);
                l.getStrokeDashArray().setAll(2.0, 3.0);
                joinLines.add(l);
                Polygon tri = new Polygon(-4.0, 0.0, 4.0, 0.0, 0.0, -6.0);
                tri.setFill(text);
                joinMarks.add(tri);
                if (joinLabels.size() < 3) joinLabels.add(small(j.name(), Palette.TEXT_LABEL));
            }
            getChildren().addAll(yGrid);
            getChildren().addAll(xGrid);
            getChildren().addAll(axis, worldLine, netLine);
            getChildren().addAll(joinLines);
            getChildren().addAll(joinMarks);
            getChildren().addAll(yLabels);
            getChildren().addAll(xLabels);
            getChildren().addAll(joinLabels);
            getChildren().addAll(xCaption, yCaption);
            if (dot != null) getChildren().addAll(drop, mark, dotLabel);
        }

        private static Label small(String text, String tone) {
            Label l = new Label(text);
            l.setStyle(Palette.words(Palette.SIZE_CAPTION, tone));
            return l;
        }

        /** A step of 1, 2, 2.5 or 5 times a power of ten that is at least v (nothing: 1). */
        static double niceStep(double v) {
            if (!(v > 0)) return 1;
            double p = Math.pow(10, Math.floor(Math.log10(v))), f = v / p;
            return (f <= 1 ? 1 : f <= 2 ? 2 : f <= 2.5 ? 2.5 : f <= 5 ? 5 : 10) * p;
        }

        private double px(double share, double plotL, double plotW) {
            double t = (Math.log(Math.max(xLo, Math.min(xHi, share))) - Math.log(xLo)) / (Math.log(xHi) - Math.log(xLo));
            return plotL + plotW * t;
        }

        private double py(double v, double plotT, double plotH) {
            return plotT + plotH * (1 - Math.max(0, Math.min(yHi, v)) / yHi);
        }

        private static void place(Label l, double x, double y) {
            double w = l.prefWidth(-1), h = l.prefHeight(-1);
            l.resizeRelocate(x, y, w, h);
        }

        @Override protected void layoutChildren() {
            double w = getWidth(), h = getHeight();
            double plotL = LEFT, plotR = Math.max(plotL + 40, w - RIGHT), plotT = TOP, plotB = h - BOTTOM, plotW = plotR - plotL, plotH = plotB - plotT;
            for (int i = 0; i < yGrid.size(); i++) {
                double y = py(yTicks.get(i), plotT, plotH);
                Line l = yGrid.get(i);
                l.setStartX(plotL);
                l.setEndX(plotR);
                l.setStartY(y);
                l.setEndY(y);
                Label t = yLabels.get(i);
                double lw = t.prefWidth(-1), lh = t.prefHeight(-1);
                t.resizeRelocate(plotL - 6 - lw, y - lh / 2, lw, lh);
            }
            for (int i = 0; i < xGrid.size(); i++) {
                double x = px(xTicks.get(i), plotL, plotW);
                Line l = xGrid.get(i);
                l.setStartX(x);
                l.setEndX(x);
                l.setStartY(plotT);
                l.setEndY(plotB);
                Label t = xLabels.get(i);
                double lw = t.prefWidth(-1), lh = t.prefHeight(-1);
                t.resizeRelocate(x - lw / 2, plotB + 5, lw, lh);
            }
            axis.setStartX(plotL);
            axis.setEndX(plotR);
            axis.setStartY(plotB);
            axis.setEndY(plotB);
            netLine.getPoints().clear();
            worldLine.getPoints().clear();
            for (int i = 0; i < xs.length; i++) {
                double x = px(xs[i], plotL, plotW);
                netLine.getPoints().addAll(x, py(net[i], plotT, plotH));
                worldLine.getPoints().addAll(x, py(world[i], plotT, plotH));
            }
            for (int i = 0; i < joins.size(); i++) {
                double x = px(joins.get(i).share(), plotL, plotW);
                Line l = joinLines.get(i);
                l.setStartX(x);
                l.setEndX(x);
                l.setStartY(plotT);
                l.setEndY(plotB);
                Polygon tri = joinMarks.get(i);
                tri.setLayoutX(x);
                tri.setLayoutY(plotB);
            }
            for (int i = 0; i < joinLabels.size(); i++) {
                Label t = joinLabels.get(i);
                double x = px(joins.get(i).share(), plotL, plotW), lw = t.prefWidth(-1), lh = t.prefHeight(-1);
                double at = x + 4 + lw > plotR ? x - 4 - lw : x + 4;
                t.resizeRelocate(at, plotT + 2 + (i % 3) * (lh + 1), lw, lh);
            }
            place(xCaption, plotL + (plotW - xCaption.prefWidth(-1)) / 2, h - xCaption.prefHeight(-1) - 2);
            place(yCaption, 4, 2);
            if (dot != null) {
                double x = px(dot.share(), plotL, plotW), y = py(dot.value(), plotT, plotH);
                mark.setCenterX(x);
                mark.setCenterY(y);
                drop.setStartX(x);
                drop.setEndX(x);
                drop.setStartY(y);
                drop.setEndY(plotB);
                double lw = dotLabel.prefWidth(-1), lh = dotLabel.prefHeight(-1);
                double at = x - 10 - lw >= plotL ? x - 10 - lw : x + 10;
                dotLabel.resizeRelocate(at, Math.min(plotB - lh - 4, y + 8), lw, lh);
            }
        }
    }
}
