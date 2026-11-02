package ham.citybuildersim.ui;

import ham.citybuildersim.ChartModel;
import ham.citybuildersim.ForeignAccounts;
import ham.citybuildersim.Game;
import ham.citybuildersim.HistorySave;
import ham.citybuildersim.VaultView;
import ham.citybuildersim.WorldMoneys;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Money.ratePct;
import static ham.citybuildersim.ui.Money.usd;
import static ham.citybuildersim.ui.Pieces.gridCell;
import static ham.citybuildersim.ui.Pieces.gridHead;
import static ham.citybuildersim.ui.Pieces.rightAfterFirst;
import static ham.citybuildersim.ui.TradeScreen.card;
import static ham.citybuildersim.ui.TradeScreen.cardHead;
import static ham.citybuildersim.ui.TradeScreen.line;
import static ham.citybuildersim.ui.TradeScreen.muted;
import static ham.citybuildersim.ui.TradeScreen.quiet;

/**
 * The vault in eight moneys (WN6; docs/studies/spec-nations.md 8, board 3): the first card of Trade &gt; The reserves where the vault holds, or can hold,
 * more than the dollar - a table of the eight (what it holds of each in the money's own mark, what that is worth in US$, its share, how it moved against
 * the US$ this month, its central bank's policy rate and, where anything is owed in it, what the city owes), the total, the month's revaluation across the
 * moneys in words, and under a fold the seven against the US$ and the eight's policy rates over the years. Every figure is VaultView's.
 * <p>
 * WHY. The reserves page said the sum, the sum's revaluation by the rate and the biggest cross mover, and a line of what was held; the vault keeps a
 * balance in each of the eight (ForeignAccounts, THE VAULT IN EIGHT) and the page showed none of them in a place a player could read across. The card's
 * charts are History's series (the rates are HistorySave's fx&lt;code&gt;, the policy rates policy&lt;code&gt;), drawn in TimeChart as the currency page's are.
 */
final class VaultTable {

    /** The plot's height in the two charts, pixels. */
    static final double CHART_HEIGHT = 200;

    private final UserInterface ui;
    private final TradeScreen trade;

    /** The two charts' windows and hidden lines, kept across the month's redraw. */
    private final ChartModel rateWindow = new ChartModel(), policyWindow = new ChartModel();
    private final Set<String> rateHidden = new HashSet<>(), policyHidden = new HashSet<>();

    VaultTable(UserInterface ui, TradeScreen trade) {
        this.ui = ui;
        this.trade = trade;
    }

    private Game game() { return ui.game; }

    static final String EIGHT_INFO = "The vault keeps a balance in each of the eight moneys central banks keep: the US dollar, the euro, the yen, "
            + "the pound, the yuan, the Canadian and Australian dollars and the Swiss franc. Each is held in its own money and worth what the "
            + "day's rate says, so the worth moves when the moneys move against each other, and nobody is paid for it. Each balance earns its "
            + "central bank's policy rate. Where the city owes in one of them, it is in the last column.";

    static final String YEARS_INFO = "The seven against the US dollar since the city was founded, as how many US dollars one of each buys, "
            + "100 at the start: a rise is a stronger money. The second chart is the eight central banks' policy rates.";

    /** Whether the vault holds, or can hold, more than the dollar: a rate for another money, or a balance in one. */
    boolean applies() {
        ForeignAccounts fx = game().getForeignAccounts();
        boolean others = false;
        for (int k = 1; k < ForeignAccounts.MONEYS; k++) others |= fx.hasRate(k) || fx.getHolding(k) > 0;
        return others;
    }

    /** IN THE VAULT, IN EIGHT MONEYS; null where only the dollar can be held. */
    VBox eightCard() {
        if (!applies()) return null;
        VaultView v = VaultView.of(game());
        VBox c = card(cardHead(Icons.SAFE, Palette.MONEY, "IN THE VAULT, IN EIGHT MONEYS", EIGHT_INFO,
                quiet("worth " + usd(v.worthUsd()) + " · " + trade.d(v.worthD()))));
        boolean owed = false;
        for (VaultView.Row r : v.rows()) owed |= r.owedOwn() > 0;
        c.getChildren().add(table(v, owed));
        String moved = revaluation(v);
        if (moved != null) c.getChildren().add(line(moved, null));
        c.getChildren().add(trade.fold("reserves:eight", "the eight over the years", this::years));
        return c;
    }

    /** The table: a row a money and a total row. */
    private GridPane table(VaultView v, boolean owed) {
        double[] widths = owed ? new double[] {170, 100, 190, 70, 90, 90, 170} : new double[] {170, 100, 190, 70, 90, 90};
        GridPane t = Pieces.grid(widths, rightAfterFirst(widths.length));
        t.setHgap(12);
        t.setVgap(5);
        List<String> head = new ArrayList<>(List.of("money", "held", "worth, US$", "share", "this month", "policy rate"));
        if (owed) head.add("the city owes in it");
        gridHead(t, head.toArray(new String[0]));
        int at = 1;
        for (VaultView.Row r : v.rows()) {
            boolean held = r.held() > 0;
            String tone = held ? Palette.TEXT_HEAD : Palette.TEXT_MUTED;
            int col = 0;
            t.add(gridCell(capital(r.name()) + (r.rated() ? "" : " (no rate yet)"), r.rated() || held ? Palette.TEXT_BODY : Palette.TEXT_MUTED,
                    Palette.SIZE_LABEL + 1, false), col++, at);
            t.add(gridCell(held ? TradeScreen.ownMoney(r.index(), r.held()) : "–", tone, Palette.SIZE_LABEL + 1, true), col++, at);
            t.add(gridCell(held ? usd(r.worthUsd()) : "–", tone, Palette.SIZE_LABEL + 1, true), col++, at);
            t.add(gridCell(held ? Money.share1(r.share()) : "–", tone, Palette.SIZE_LABEL + 1, true), col++, at);
            t.add(gridCell(r.index() == 0 || !Double.isFinite(r.move()) ? "–" : signedPct(r.move()), Palette.TEXT_LABEL, Palette.SIZE_LABEL + 1, true), col++, at);
            t.add(gridCell(Double.isFinite(r.policyRate()) ? ratePct(r.policyRate()) : "·", Palette.TEXT_LABEL, Palette.SIZE_LABEL + 1, true), col++, at);
            if (owed) {
                t.add(gridCell(r.owedOwn() > 0 ? TradeScreen.ownMoney(r.index(), r.owedOwn()) + " · " + usd(r.owedUsd()) : "–",
                        r.owedOwn() > 0 ? Palette.TEXT_HEAD : Palette.TEXT_MUTED, Palette.SIZE_LABEL + 1, true), col++, at);
            }
            at++;
        }
        // the total: worth in US$ and in D$, the whole share, and the month's move across the moneys
        int col = 0;
        t.add(gridCell("All eight", Palette.TEXT_HEAD, Palette.SIZE_LABEL + 1, false), col++, at);
        t.add(gridCell("", Palette.TEXT_HEAD, Palette.SIZE_LABEL + 1, true), col++, at);
        t.add(gridCell(usd(v.worthUsd()) + " · " + trade.d(v.worthD()), Palette.TEXT_HEAD, Palette.SIZE_LABEL + 1, true), col++, at);
        t.add(gridCell(v.worthUsd() > 0 ? "100%" : "–", Palette.TEXT_HEAD, Palette.SIZE_LABEL + 1, true), col++, at);
        t.add(gridCell(Math.abs(v.crossUsd()) > .005 ? (v.crossUsd() < 0 ? "−" : "+") + usd(Math.abs(v.crossUsd())) : "–", Palette.TEXT_LABEL,
                Palette.SIZE_LABEL + 1, true), col++, at);
        return t;
    }

    private static String capital(String name) {
        String bare = name.startsWith("the ") ? name.substring(4) : name;
        return bare.isEmpty() ? bare : Character.toUpperCase(bare.charAt(0)) + bare.substring(1);
    }

    /** A move against the US$ as a signed per cent to a place, with a true minus: "−0.9%", "+0.4%", "0.0%". */
    static String signedPct(double move) {
        double shown = Money.unsigned0(move * 100, 1);
        return (shown > 0 ? "+" : shown < 0 ? "−" : "") + String.format("%.1f%%", Math.abs(shown));
    }

    /**
     * The month's cross revaluation under the table, as TradeScreen.moved() words it: what the vault gained or lost because the eight moved against
     * each other, and the money that moved it most; null in a month nothing moved between them.
     */
    private String revaluation(VaultView v) {
        if (!(Math.abs(v.crossUsd()) > .005)) return null;
        ForeignAccounts.CrossDriver driver = v.crossDriver();
        String who = driver == null ? "" : String.format(": %s %s %.1f%% against the dollar", ForeignAccounts.MONEY_NAME[driver.index()],
                driver.move() < 0 ? "fell" : "rose", Math.abs(driver.move()) * 100);
        return "This month the vault's worth moved " + (v.crossUsd() < 0 ? "−" : "+") + usd(Math.abs(v.crossUsd())) + who
                + ". Nothing was spent or earned: it is a revaluation.";
    }

    /** Under the fold: the seven against the US$, and the eight's policy rates. */
    private Node years() {
        HistorySave h = game().getHistorySave();
        VBox box = new VBox(Palette.GAP_LOOSE);
        if (h.months() < 2) {
            box.getChildren().add(muted("Two months have to be recorded before there is a line to draw."));
            return box;
        }
        String[] colours = Palette.lineColours(Collections.nCopies(ForeignAccounts.MONEYS, null));
        List<TimeChart.Line> rates = new ArrayList<>(), policies = new ArrayList<>();
        List<TimeChart> charts = new ArrayList<>();
        for (int k = 0; k < ForeignAccounts.MONEYS; k++) {
            String code = WorldMoneys.EIGHT.get(k);
            String name = capital(ForeignAccounts.MONEY_NAME[k]);
            double[] policy = h.aligned(HistorySave.policyRateKey(code));
            if (anyNumber(policy)) {
                policies.add(new TimeChart.Line(HistorySave.policyRateKey(code), name, colours[k], 0, policy, v -> v * 100, v -> ratePct(v), ""));
            }
            if (k == 0) continue;
            double[] rate = h.aligned(HistorySave.worldRateKey(code));
            double base = firstPositive(rate);
            if (!(base > 0)) continue;
            rates.add(new TimeChart.Line(HistorySave.worldRateKey(code), name, colours[k], 0, rate, v -> v > 0 ? 100 * base / v : Double.NaN,
                    v -> String.format("%s %.4f a US$", code, v), ""));
        }
        if (!rates.isEmpty()) {
            charts.add(chart("THE SEVEN AGAINST THE US$", "US$ one buys, 100 at the start", rates, rateWindow, rateHidden,
                    new TimeChart.Axis((v, step) -> String.format("%.0f", v), false)));
        }
        if (!policies.isEmpty()) {
            charts.add(chart("THE EIGHT'S POLICY RATES", "a year", policies, policyWindow, policyHidden, HistoryScreen.axisFor("percent")));
        }
        if (rates.isEmpty() && policies.isEmpty()) box.getChildren().add(muted("This city's history holds none of the moneys' series."));
        // the charts are drawn as wide as the card they sit in
        box.getChildren().addAll(charts);
        box.widthProperty().addListener((o, was, now) -> {
            if (now.doubleValue() > 0) for (TimeChart chart : charts) chart.setSize(now.doubleValue() - 16, CHART_HEIGHT);
        });
        return box;
    }

    private TimeChart chart(String title, String unit, List<TimeChart.Line> lines, ChartModel window, Set<String> hidden, TimeChart.Axis axis) {
        HistorySave h = game().getHistorySave();
        TimeChart chart = new TimeChart(window, hidden, true);
        chart.setData(h.getMonth(), lines, axis, null, false, false, null, List.of(), List.of(), List.of(), "no months recorded yet");
        chart.setSize(794, CHART_HEIGHT);
        chart.withoutFullScreen();
        Label head = new Label(title);
        head.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        head.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        chart.lead().getChildren().setAll(head, quiet(unit));
        return chart;
    }

    private static boolean anyNumber(double[] series) {
        for (double v : series) if (Double.isFinite(v)) return true;
        return false;
    }

    private static double firstPositive(double[] series) {
        for (double v : series) if (Double.isFinite(v) && v > 0) return v;
        return Double.NaN;
    }
}
