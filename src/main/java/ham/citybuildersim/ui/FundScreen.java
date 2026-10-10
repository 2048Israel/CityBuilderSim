package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import ham.citybuildersim.ui.Pieces.Column;
import ham.citybuildersim.ui.Pieces.Look;
import ham.citybuildersim.ui.Pieces.Pnl;
import ham.citybuildersim.ui.Pieces.Press;
import ham.citybuildersim.ui.Pieces.Segment;
import ham.citybuildersim.ui.Pieces.Tick;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Statement.statementLine;
import static ham.citybuildersim.ui.FinancesScreen.caption;
import static ham.citybuildersim.ui.FinancesScreen.card;
import static ham.citybuildersim.ui.FinancesScreen.cardLine;
import static ham.citybuildersim.ui.FinancesScreen.figure;
import static ham.citybuildersim.ui.FinancesScreen.keyRow;
import static ham.citybuildersim.ui.FinancesScreen.line;
import static ham.citybuildersim.ui.FinancesScreen.muted;
import static ham.citybuildersim.ui.FinancesScreen.pair;
import static ham.citybuildersim.ui.FinancesScreen.per100;
import static ham.citybuildersim.ui.FinancesScreen.seg;
import static ham.citybuildersim.ui.FinancesScreen.words;

/**
 * The city's fund on the Finances tab, as a brokerage (0.7.39): its Portfolio - what it is worth, what it has made and on what, every holding with its average cost and P&L - a Search of the shares and bonds it can buy, a page a security with the order ticket beside the fund's position in it, its Activity, and its Rules & cash.
 *
 * WHY. Jerus, the morning after 0.7.38: "when you click buy manually i want
 * it to be like wealthsimple trade type kinda like a brokerage, where you can
 * search the shares and bonds and see and all, and also the city fund should
 * show pnl and acb and all that". The fund was two pages in FinancesScreen:
 * Holdings, which said what it held at its marks and nothing of what it had
 * cost, and By hand, a company chip, a bond chip, an amount and a Buy at fair
 * value - which, in both of the research cities, filled nothing, because
 * nobody was asking at or under fair (the project's spec-fund-0739.md, 1.4).
 *
 * WHAT IT IS. Four pages under Finances' frame - PAGES - and a page a
 * security, reached from any row ("Finances › The city's fund › Automotive"),
 * with a way back to the page it was opened from ("‹ Portfolio"). Every
 * figure is FundView's, the model's pure door
 * to the fund (and so FundLedger's cost basis): nothing here adds a column
 * up. The words each page composes are worked out without a node, by the
 * static methods under WORDS, so a probe reads every one of them on a played
 * city. The cards that were the Holdings page - the dial, the transfer to
 * the treasury, the rescue book, WHAT IT HOLDS and the rule's aim - moved
 * here whole (Rules & cash, Portfolio; the withdrawal's dial joined Rules &
 * cash in 0.7.48); By hand's amount, chips and buttons are the
 * ticket, Search and a security's page (the spec's 4.8, nothing lost).
 *
 * COLOURS. P&L is a verdict - did this purchase make or lose the city money -
 * so it is green and red (Pieces.pnl(), the spec's D7), always with a sign
 * and an arrow; nothing else here is: a price, its move, a yield and every
 * chart line are in ink or their area's colour.
 */
final class FundScreen {

    /** The window this screen draws into, and the tab it is drawn on. */
    private final UserInterface ui;
    private final FinancesScreen fin;

    FundScreen(UserInterface ui, FinancesScreen fin) {
        this.ui = ui;
        this.fin = fin;
    }

    /* =====================================================================
       THE PAGES
       ===================================================================== */

    /** The area's name on Finances. */
    static final String AREA = "The city's fund";

    /** The fund's four pages (0.7.39; "Holdings" and "By hand" until then). */
    static final String[] PAGES = {"Portfolio", "Search", "Activity", "Rules & cash"};
    /** ...and their icons on the chips. */
    static final String[] ICONS = {Icons.SAFE, Icons.EXCHANGE, Icons.REPORTS, Icons.SETTINGS};

    /** An older door's page by its new name: Holdings is Portfolio, By hand is Search (Government's doors still say Holdings). */
    static String pageFor(String page) {
        if ("Holdings".equals(page)) return "Portfolio";
        if ("By hand".equals(page)) return "Search";
        return page;
    }

    /** The months of the record Activity shows before "show older", and how many more each press adds. */
    static final int ACTIVITY_MONTHS = 60;

    /** The rows of a security's own record under its book. */
    static final int SECURITY_ROWS = 12;

    /** The closed lots the Portfolio's CLOSED card lists before its fold. */
    static final int CLOSED_ROWS = 8;

    /** The money steps: the ticket's on a buy by amount and on a bond's face, and PAY IN, DRAW OUT's; a quantity of shares steps by 1, 10, 100 and 1,000 instead. */
    static final double[] MONEY_STEPS = {1_000, 10_000, 100_000, 1_000_000};

    /** The security page's two columns at the 1,389 window: the picture and its facts, then the position and the ticket. */
    static final double LEFT = 812, RIGHT = 412;

    /* ----- what is kept across the clock's redraws: the page, the box, the ticket ----- */

    /** The security open, by its lot key ("S:Automotive", "B:142"), or null; and the page it was opened from. */
    String security;
    String cameFrom = "Portfolio";

    /** The search box's words and its chip; Activity's chip and how many months it shows. */
    String query = "";
    String filter = "All";
    String group = "All";
    int activityMonths = ACTIVITY_MONTHS;

    /** Folds and opened rows, by key. */
    final Set<String> open = new HashSet<>();

    /** The charts' windows and the lines their legends switched off. */
    final ChartModel worthWindow = new ChartModel(), priceWindow = new ChartModel();
    final Set<String> worthHidden = new HashSet<>(), priceHidden = new HashSet<>();

    /** The search box, and whether the player was typing in it when the month redrew the page. */
    private TextField box;

    /**
     * THE TICKET, kept on the screen: which security it is for, buy or sell,
     * by amount or by quantity, the figure, the price (FAIR, BID, ASK, LAST,
     * or OWN - fair value moved by `steps` per cent), and whether it is at
     * its review. A new security starts it afresh. The price is kept as its
     * kind, not its number, so a month re-strikes it with the book.
     */
    String ticketKey;
    boolean buy = true;
    boolean byAmount = true;
    double amount = 0;
    String priceKind = FAIR;
    int steps = 0;
    boolean review = false;

    /** The ticket's prices. */
    static final String FAIR = "fair", BID = "bid", ASK = "ask", LAST = "last", OWN = "own";

    /**
     * A MONTH LANDING (the spec's 5): whether the tab was showing before this
     * draw (FinancesScreen sets it), the page and the month last drawn, and
     * the worth the hero said then. Only when the same page is redrawn where
     * it stands in a later month does the worth count on from what it said
     * (SectorScreen.countUp(), City History's since 0.7.37) and a holding's
     * payment pop.
     */
    boolean here;
    private String drawnPage;
    private int drawnMonth = -1;
    private double shownWorth = Double.NaN;
    private boolean landed;

    /** Draws one of the fund's pages - or the security open - into `page`. */
    void draw(VBox page, String name) {
        String drawing = security != null ? "security:" + security : name == null ? "" : name;
        int month = ui.game.getMonth();
        landed = here && drawing.equals(drawnPage) && drawnMonth >= 0 && month != drawnMonth;
        drawnPage = drawing;
        drawnMonth = month;
        if (security != null) { securityPage(page); return; }
        switch (name == null ? "" : name) {
            case "Search"       -> searchPage(page);
            case "Activity"     -> activityPage(page);
            case "Rules & cash" -> rulesPage(page);
            default             -> portfolioPage(page);
        }
    }

    /** Opens a security's page from the page drawn now. */
    void openSecurity(String key) {
        if (key == null) return;
        if (security == null) cameFrom = fin.financePage == null ? "Portfolio" : fin.financePage;
        security = key;
        if (!key.equals(ticketKey)) resetTicket(key);
        fin.financeArea = AREA;
        ui.innerScrollAt.remove("showFinanceMenu:body");
        fin.showFinanceMenu();
    }

    /** Back to the page the security was opened from. */
    void back() {
        String to = cameFrom;
        fin.open(AREA, to, null);
    }

    /** A page of the fund, from a door. */
    void go(String page) { fin.open(AREA, page, null); }

    private void redraw() { fin.showFinanceMenu(); }

    /**
     * The head on a security's page: "Finances › The city's fund ›
     * Automotive", each but the last a way back; at its right the debt's
     * history door and a way back to the page it was opened from
     * ("‹ Portfolio", "‹ Search").
     */
    HBox head(Node history) {
        Game g = ui.game;
        HBox head = pageHead("Finances", Palette.MONEY, () -> fin.open(null, null, null), AREA, null,
                history, FinancesScreen.backDoor(cameFrom, this::back));
        if (!head.getChildren().isEmpty() && head.getChildren().get(0) instanceof HBox titled) {
            for (Node n : titled.getChildren()) {
                if (n instanceof Label l && AREA.equals(l.getText())) {
                    l.setStyle(l.getStyle() + " -fx-cursor: hand;");
                    l.setOnMouseClicked(e -> back());
                }
            }
            Label sep = new Label("›");
            sep.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_MUTED) + " -fx-padding: 8 0 2 0;");
            Label here = new Label(securityName(g, security));
            here.setStyle(Palette.strong(Palette.SIZE_TITLE, Palette.TEXT_HEAD) + " -fx-padding: 8 0 2 0;");
            here.setWrapText(true);
            here.setMinWidth(0);
            titled.getChildren().addAll(sep, here);
        }
        return head;
    }

    /* =====================================================================
       WORDS - every figure and sentence the pages compose, worked out
       without a node (pure: the probe reads them)
       ===================================================================== */

    /** The city's money's mark: "D$". */
    static String sym(Game g) { return g.getCurrency().qualifiedSymbol(); }

    /** Local money with its mark and a true minus: "D$2.4M", "−D$725k". */
    static String d(Game g, double thousands) {
        if (!Double.isFinite(thousands)) return "—";
        String shown = tidyMoney(money(Math.abs(thousands)));
        return (thousands < 0 && !"$0".equals(shown) ? "−" : "") + marked(sym(g), shown);
    }

    /** ...signed, as a movement: "+D$1.2M", "−D$3.5M", "D$0". */
    static String signed(Game g, double thousands) {
        if (!Double.isFinite(thousands)) return "—";
        String shown = tidyMoney(money(Math.abs(thousands)));
        if ("$0".equals(shown)) return marked(sym(g), shown);
        return (thousands < 0 ? "−" : "+") + marked(sym(g), shown);
    }

    /** A price a unit: a share's money ("D$101,355", "D$0.9415"), or a bond's per 100 ("102.80 per 100"). */
    static String price(Game g, boolean bond, double p) {
        if (!Double.isFinite(p) || !(p > 0)) return "—";
        return bond ? per100(p) + " per 100" : marked(sym(g), unitPrice(p));
    }

    /** Shares: two places from one ("553.73"), four under it ("0.0076"), and none at all as "0". */
    static String shares(double n) {
        if (!Double.isFinite(n)) return "—";
        double a = Math.abs(n);
        if (a == 0) return "0";
        if (a >= 1) return String.format("%,.2f", n);
        if (a < 0.00005) return "under 0.0001";
        return String.format("%.4f", n);
    }

    /** Units of a holding: shares, or face in money. */
    static String units(Game g, boolean bond, double n) {
        return bond ? d(g, n) + " of face" : shares(n) + (Math.abs(n - 1) < 1e-9 ? " share" : " shares");
    }

    /** A share as a percentage, to one place, with a true minus. */
    static String pct1(double share) {
        return Double.isFinite(share) ? String.format("%.1f%%", share * 100).replace('-', '−') : "—";
    }

    /** ...signed: "+14.4%". */
    static String signedPct(double share) {
        if (!Double.isFinite(share)) return "—";
        return (share < 0 ? "−" : "+") + String.format("%,.1f%%", Math.abs(share) * 100);
    }

    /** A move in ink with its arrow: "▲ +4.3%", or "unchanged" when it rounds to none - never a verdict's colour. */
    static String move(double share) {
        if (!Double.isFinite(share)) return null;
        if (Math.abs(share) < 0.0005) return "unchanged";
        String arrow = share > 0 ? "▲ " : share < 0 ? "▼ " : "";
        return arrow + signedPct(share);
    }

    /** The time a bond has left: "3y 5m left", "5 months left", "a month left", "falls due this month". */
    static String left(int now, int maturity) {
        int r = maturity - now;
        if (r <= 0) return "falls due this month";
        if (r == 1) return "a month left";
        return CityCalendar.until(now, maturity).replaceFirst("^in ", "") + " left";
    }

    /** A month as the pages date things: "Apr 2197". */
    static String month(int m) { return CityCalendar.formatShort(m); }

    /** A security's name by its key: the company's, "the rescue book", or the bond's. */
    static String securityName(Game g, String key) {
        if (key == null) return "";
        if (key.equals(FundLedger.PREFERRED_KEY)) return "The bank's preferred";
        if (key.equals(FundLedger.WARRANTS_KEY)) return "Warrants on the bank";
        int c = FundView.companyOf(key);
        if (c >= 0) return Equity.COMPANIES[c];
        int id = FundView.bondIdOf(key);
        CorporateBond b = g.getBondMarket().bond(id);
        if (b != null) return FundView.bondName(b);
        FundLedger.Lot lot = g.getFund().getLedger().lot(key);
        return lot != null ? lot.name() : "Bond #" + id;
    }

    /* ------------------------------ the hero ------------------------------ */

    /**
     * The Portfolio's hero, in words: the fund's worth; its return over the
     * chart's window ("▲ +D$X (+Y%) over 10Y", or since when it was
     * recorded) in P&L's colour; since it began, exactly; and its three
     * small figures - cash, last month's income, the transfer.
     */
    record Hero(String value, String ret, String retTone, String since, String sinceInfo,
                String cash, String cashNote, String income, String incomeNote, String transfer, String transferNote,
                String transferTone, String recorded) { }

    static Hero hero(Game g, ChartModel window) {
        FundView.Portfolio p = FundView.portfolio(g);
        HistorySave h = g.getHistorySave();
        String ret, tone = Palette.TEXT_MUTED, recorded = null;
        if (h.months() > 0 && window != null && window.span() > 0) {
            int lo = window.firstMonthShown(), hi = window.lastMonthShown();
            FundView.RangeReturn r = FundView.rangeReturn(h, lo, hi);
            String over = wholeRange(window) && r.recordedFrom() <= lo ? "over " + rangeName(window.range())
                    : "from " + month(r.fromMonth()) + " to " + month(r.toMonth());
            if (r.recorded()) {
                Pnl pnl = pnl(sym(g), r.gain(), r.pct());
                ret = pnl.text() + " " + over;
                tone = pnl.tone();
                if (r.recordedFrom() > lo) recorded = "recorded from " + month(r.recordedFrom()) + " on";
            } else {
                ret = r.recordedFrom() >= 0 ? "recorded from " + month(r.recordedFrom()) + " on: a month more to compare"
                        : "not recorded yet: its worth is kept from this build on, a month at a time";
            }
        } else {
            ret = "not recorded yet: its worth is kept from this build on, a month at a time";
        }
        String since;
        if (p.putIn() > 0) {
            since = "Since it began: " + pnl(sym(g), p.gain(), Double.NaN).text().replaceFirst("^[▲▼] ", "")
                    + " on " + d(g, p.putIn()) + " put in" + (Double.isFinite(p.multiple())
                    ? " · ×" + String.format("%.1f", p.multiple()) : "");
        } else {
            since = "Since it began: nothing put in yet";
        }
        TreasuryFund f = g.getFund();
        String sinceInfo = String.format("What the city has put in: %s of pay-ins (the dial %s, by hand %s), %s for the "
                        + "bank's rescues and %s of the bank's preferred - %s in all. What it has taken out: %s of the "
                        + "monthly transfer and %s drawn out by hand - %s. It is worth %s now, so it has made %s: worth plus "
                        + "taken out less put in. Exact on any save, from the fund's own record of every flow in and out.",
                d(g, f.getPaidInFromSurplus() + f.getPaidInFromCash() + f.getHandPaidIn()),
                d(g, f.getPaidInFromSurplus() + f.getPaidInFromCash()), d(g, f.getHandPaidIn()), d(g, f.getRescuesPaid()),
                d(g, f.getPreferredBought()), d(g, p.putIn()), d(g, f.getTransfersPaid()), d(g, f.getHandDrawnOut()),
                d(g, p.takenOut()), d(g, p.value()), signed(g, p.gain()));
        String cashNote = p.reserved() > 0 ? d(g, p.reserved()) + " held for your orders; it earns nothing"
                : "waits for the book; it earns nothing";
        String incomeNote = p.transferLastMonth() > 0 && p.incomeLastMonth() > 0
                ? "covered " + pct1(p.incomeLastMonth() / p.transferLastMonth()).replace(".0%", "%") + " of the transfer"
                : "dividends and coupons";
        String transferNote = p.transferShort() > 0 ? d(g, p.transferShort()) + (g.getFund().getToRaise() > 0
                        ? " of it sold for, to pay next month" : " of it not paid, for want of cash")
                : withdrawalShare(g.getFund().getWithdrawal());
        return new Hero(d(g, p.value()), ret, tone, since, sinceInfo, d(g, p.cash()), cashNote, d(g, p.incomeLastMonth()),
                incomeNote, d(g, p.transferLastMonth()), transferNote,
                // Amber only for what is not paid (0.7.102, Jerus's B): over the default the short is what the step
                // sells for and the next month pays - a policy, not a warning - as Finances' fund summary has it.
                p.transferShort() > 0 && !(g.getFund().getToRaise() > 0) ? Palette.WARN : null, recorded);
    }

    /** Whether a window shows the whole of a named range ("over 10Y"): picked by its chip, not moved since, not "All", and the history at least that long - else the window says its months. */
    static boolean wholeRange(ChartModel window) {
        return window.onRange() && window.range() != ChartModel.ALL && window.monthsShown() >= window.range();
    }

    /** A range's name: "1Y", "10Y", "All". */
    static String rangeName(int range) {
        for (int i = 0; i < ChartModel.RANGES.length; i++) if (ChartModel.RANGES[i] == range) return ChartModel.RANGE_NAMES[i];
        return ChartModel.RANGE_NAMES[ChartModel.RANGE_NAMES.length - 1];
    }

    /* ------------------------------ the holdings ------------------------------ */

    /**
     * One row of HOLDINGS: its group, the lot's key, its name and the line
     * under it, the units, the price and its staleness, the worth, the
     * average cost and whether it was counted at market value, the
     * unrealized P&L, and the holding's share of the fund; and what it paid
     * in the month just landed, when it paid anything (the spec's 5).
     */
    record Holding(String group, String key, String name, String sub, int company, int bondId, String issuer,
                   String units, String price, String priceNote, String value, String average, String averageTag,
                   Pnl unrealized, double share, double worth, String paidNow) { }

    /** The holdings' groups, in the page's order. */
    static final String SHARES = "COMPANY SHARES", RESCUE = "THE RESCUE BOOK", BONDS = "BONDS";

    static List<Holding> holdings(Game g) {
        List<Holding> out = new ArrayList<>();
        double total = Math.max(0, g.fundValue());
        int now = g.getMonth();
        for (FundView.Position p : FundView.positions(g)) {
            boolean bond = p.isBond();
            String group = switch (p.kind()) {
                case FundView.SHARE -> SHARES;
                case FundView.BOND -> BONDS;
                default -> RESCUE;
            };
            FundLedger.Lot lot = g.getFund().getLedger().lot(p.key());
            String sub = switch (p.kind()) {
                case FundView.RESCUE -> "the rescue book · " + pct1(p.stake()) + " of the bank · "
                        + d(g, p.income() + p.incomeBefore()) + " of dividends on " + d(g, p.acb()) + " of cost";
                case FundView.SHARE -> pct1(p.stake()) + " of it";
                case FundView.BOND -> p.maturity() - now <= 12
                        ? String.format("%.2f%% · repays %s %s (%s)", p.coupon() * 100, d(g, p.units()),
                                p.maturity() - now <= 0 ? "this month" : CityCalendar.until(now, p.maturity()), month(p.maturity()))
                        : String.format("%.2f%% · %s · %s", p.coupon() * 100, month(p.maturity()),
                                CityCalendar.until(now, p.maturity()));
                case FundView.PREFERRED -> String.format("%.0f%% a year, at par", Bank.PREFERRED_RATE * 100);
                default -> "bought back or exercised; they cost nothing";
            };
            String priceNote = p.stale(now) ? (p.company() >= 0 && g.getExchange().markedAtFair(p.company())
                    ? "marked at fair value: last traded " : "last traded ") + month(p.priceMonth())
                    : p.isShare() && p.priceMonth() < 0 ? "fair value: never traded" : null;
            String avg = FundView.WARRANTS.equals(p.kind()) ? "nothing"
                    : FundView.PREFERRED.equals(p.kind()) ? "at par" : price(g, bond, p.average());
            String tag = p.seeded() ? "cost from " + month(p.since()) : null;
            String bargain = bargain(lot);
            if (bargain != null) tag = tag == null ? bargain : tag + " · " + bargain;
            String paidNow = lot != null && lot.lastIncomeMonth() == now && !"$0".equals(tidyMoney(money(lot.lastIncome())))
                    ? "paid +" + d(g, lot.lastIncome()) + " this month" : null;
            String unitsWords = FundView.PREFERRED.equals(p.kind()) ? d(g, p.units()) + " at par"
                    : FundView.WARRANTS.equals(p.kind()) ? shares(p.units()) + " shares' worth" : units(g, bond, p.units());
            String priceWords = FundView.PREFERRED.equals(p.kind()) ? "par" : price(g, bond, p.price());
            out.add(new Holding(group, p.key(), p.name(), sub, p.company(), p.bondId(), p.issuer(), unitsWords, priceWords,
                    priceNote, d(g, p.value()), avg, tag, pnl(sym(g), p.unrealized(), p.unrealizedPct()),
                    total > 0 ? p.value() / total : 0, p.value(), paidNow));
        }
        return out;
    }

    /** "bought 26% below value": a lot whose buys paid FundLedger.BARGAIN or more under what they bought was worth at the step's value (the spec's 5); null otherwise. */
    static String bargain(FundLedger.Lot lot) {
        if (lot == null || !(lot.underValue() >= FundLedger.BARGAIN)) return null;
        return "bought " + Math.round(lot.underValue() * 100) + "% below value";
    }

    /** The lines of SINCE IT BEGAN, BY KIND, and its total. */
    record KindLine(String name, String putIn, String gotBack, String worth, String income, Pnl gain, String note) { }

    static List<KindLine> kinds(Game g) {
        List<KindLine> out = new ArrayList<>();
        FundView.Portfolio p = FundView.portfolio(g);
        TreasuryFund f = g.getFund();
        for (FundView.Kind k : p.kinds()) {
            String note = null;
            if ("Company bonds".equals(k.name()) && f.getBondFaceLost() > 0) note = d(g, f.getBondFaceLost()) + " of face lost to defaults";
            if ("Preferred & warrants".equals(k.name()) && k.note() != null) note = "some cancelled by a rescue: the hole took it";
            out.add(new KindLine(k.name(), d(g, k.putIn()), d(g, k.gotBack()), d(g, k.worth()), d(g, k.income()),
                    pnl(sym(g), k.gain(), k.putIn() > 0 ? k.gain() / k.putIn() : Double.NaN), note));
        }
        out.add(new KindLine("The fund", d(g, p.putIn()), d(g, p.takenOut()), d(g, p.value()), "",
                pnl(sym(g), p.gain(), p.putIn() > 0 ? p.gain() / p.putIn() : Double.NaN),
                "put in: pay-ins, rescues, preferred; got back: the transfers and draw-outs; worth: with its cash"));
        return out;
    }

    /** One closed lot: its name, the month it closed and why it closed, what it realized, what it paid. */
    record Closed(String key, String name, String when, Pnl realized, String income) { }

    static List<Closed> closed(Game g) {
        List<Closed> out = new ArrayList<>();
        for (FundLedger.Lot l : FundView.closedLots(g)) {
            String how = l.isBond() ? (l.proceeds() > 0 && l.maturity() >= 0 && l.closedMonth() >= l.maturity() ? "repaid "
                    : l.proceeds() > 0 ? "sold " : "written off ") : l.isRescue() ? "sold " : l.proceeds() > 0 ? "sold " : "taken in a rescue ";
            out.add(new Closed(l.key(), l.isRescue() ? l.name() + ", the rescue book" : l.name(), how + month(l.closedMonth()),
                    pnl(sym(g), l.realized(), l.acbOut() > 0 ? l.realized() / l.acbOut() : Double.NaN), d(g, l.income())));
        }
        return out;
    }

    /* ------------------------------ search ------------------------------ */

    /** One row of Search: its section, key, name and the line under it, its kind tag, price, move or yield, what the fund holds of it, and its sparkline. */
    record Found(String section, String key, String name, String sub, String tag, String price, String move,
                 String held, double[] spark, int company, String issuer) { }

    static List<Found> found(Game g, String query, String filter) {
        List<Found> out = new ArrayList<>();
        int now = g.getMonth();
        for (FundView.Hit h : FundView.search(g, query, filter)) {
            boolean bond = FundView.BOND.equals(h.kind());
            String sub, tag, mv;
            switch (h.kind()) {
                case FundView.BOND -> {
                    sub = h.issuer() + " · " + String.format("%.2f%%", h.coupon() * 100) + " · falls due "
                            + month(h.maturity());
                    tag = "BOND";
                    mv = "yields " + pct2(h.yield()) + " · " + left(now, h.maturity());
                }
                case FundView.CLOSED -> {
                    FundLedger.Lot l = g.getFund().getLedger().lot(h.key());
                    sub = "closed " + month(h.priceMonth()) + (l == null ? "" : ": realized "
                            + pnl(sym(g), l.realized(), Double.NaN).text() + ", income " + d(g, l.income()));
                    tag = "CLOSED";
                    mv = "no longer on any book";
                }
                case FundView.PREFERRED -> { sub = "held, not traded"; tag = "PREFERRED"; mv = pct2(h.yield()) + " a year"; }
                case FundView.WARRANTS -> { sub = "held, not traded"; tag = "WARRANTS"; mv = ""; }
                default -> {
                    sub = h.priceMonth() >= 0 ? "last traded " + month(h.priceMonth()) : "fair value: never traded";
                    tag = h.company() == Equity.BANK ? "BANK" : "SHARES";
                    String m = move(h.move());
                    mv = m != null ? m + " over 1Y"
                            : FundView.recordShort(g.getHistorySave(), h.name(), FundView.MOVE_MONTHS)
                            ? "1Y: its record is shorter than a year" : "1Y: not recorded precisely enough";
                }
            }
            String priceWords = FundView.PREFERRED.equals(h.kind()) ? "par" : FundView.CLOSED.equals(h.kind()) ? ""
                    : price(g, bond, h.price());
            out.add(new Found(h.section(), h.key(), h.name(), sub, tag, priceWords, mv,
                    h.held() > 0 ? "held " + d(g, h.held()) : "", h.spark(), h.company(), h.issuer()));
        }
        return out;
    }

    /* ------------------------------ a security ------------------------------ */

    /** A security's head and its eight facts, in words. */
    record Security(String name, String tag, String price, String priceNote, List<String[]> facts) { }

    static Security security(Game g, String key) {
        int now = g.getMonth();
        int c = FundView.companyOf(key);
        List<String[]> facts = new ArrayList<>();
        if (c >= 0) {
            Exchange ex = g.getExchange();
            Equity reg = g.getEquity();
            OrderBook book = ex.bookOf(c);
            double p = ex.price(c);
            String note = ex.hasTraded(c) ? "last trade, " + month(book.lastTradeMonth())
                    + (ex.markedAtFair(c) ? " - the city's holding is marked at fair value, a year on; the others' at it"
                        : now - book.lastTradeMonth() > FundView.STALE_MONTHS ? " - every holding is marked at it" : "")
                    : "fair value: it has never traded";
            double div = reg.dividendPerShareAnnual(c);
            facts.add(new String[] {"PRICE", price(g, false, p), ex.hasTraded(c) ? "the last trade" : "fair value"});
            facts.add(new String[] {"FAIR VALUE", price(g, false, ex.fair(c)), "the register's reckoning: book, or its dividend capitalised"});
            facts.add(new String[] {"MARKET VALUE", d(g, ex.marketCap(reg, c)), "every share in owners' hands, at the price"});
            facts.add(new String[] {"DIVIDEND", div > 0 ? price(g, false, div) + " a share a year" : "none this year",
                    div > 0 ? pct2(ex.yieldAt(reg, c, p)) + " at the price" : "it paid nothing over the last twelve months"});
            facts.add(new String[] {"THE FUND'S SHARE", pct1(reg.cityShare(c)), reg.getCityRescueShares(c) > 0
                    ? pct1(reg.getShares(c) > 0 ? reg.getCityRescueShares(c) / reg.getShares(c) : 0) + " of it the rescue book"
                    : "both books"});
            double mkt = reg.getShares(c) > 0 ? reg.getCityMarketShares(c) / reg.getShares(c) : 0;
            facts.add(new String[] {"AGAINST THE CAP", pct1(mkt) + " of " + pct1(TreasuryFund.OWNERSHIP_LIMIT).replace(".0", ""),
                    "the market book; past the cap the rule asks the excess at fair value"});
            facts.add(new String[] {"IN ISSUE", shares(reg.getShares(c)), "shares"});
            double bid = ex.bestBid(c), ask = ex.bestAsk(c);
            facts.add(new String[] {"BEST BID / ASK", (Double.isNaN(bid) ? "none" : price(g, false, bid)) + " / "
                    + (Double.isNaN(ask) ? "none" : price(g, false, ask)), c == Equity.BANK
                    ? "the desk's asks are new shares; the fund never buys them" : "on today's book; the step posts it afresh"});
            String tag = c == Equity.BANK ? "BANK" : "SHARES";
            return new Security(Equity.COMPANIES[c], tag, price(g, false, p), note, facts);
        }
        int id = FundView.bondIdOf(key);
        CorporateBond b = g.getBondMarket().bond(id);
        if (b != null) {
            BondMarket bm = g.getBondMarket();
            double v = bm.modelPrice(b, now), last = bm.lastPrice(b);
            String note = Double.isNaN(last) ? "its value: it has never traded" : "its value; last traded at " + per100(last)
                    + " in " + month(bm.bookOf(b).lastTradeMonth());
            facts.add(new String[] {"PRICE", per100(v) + " per 100", "the curve and its issuer's risk"});
            facts.add(new String[] {"YIELD", pct2(bm.modelYield(b, now)), "at its value"});
            facts.add(new String[] {"COUPON", pct2(b.coupon()), "a year, paid monthly"});
            facts.add(new String[] {"FALLS DUE", month(b.maturityMonth()), CityCalendar.until(now, b.maturityMonth())});
            facts.add(new String[] {"ISSUER", b.issuer(), "issued " + month(b.issueMonth())});
            facts.add(new String[] {"EXPECTED LOSS", pct2(bm.expectedLoss(b.issuer())) + " a year",
                    "its issuer's default rate times what a bond loses"});
            facts.add(new String[] {"THE FUND HOLDS", d(g, b.city()), pct1(b.face() > 0 ? b.city() / b.face() : 0)
                    + " of it; the bar, everyone who holds it"});
            facts.add(new String[] {"OUTSTANDING", d(g, b.face()), b.writtenOff() > 0 ? d(g, b.writtenOff()) + " written off"
                    : "none written off"});
            return new Security(FundView.bondName(b), "BOND", per100(v) + " per 100", note, facts);
        }
        if (key.equals(FundLedger.PREFERRED_KEY) || key.equals(FundLedger.WARRANTS_KEY)) {
            FundView.Position p = FundView.position(g, key);
            String v = p == null ? d(g, 0) : d(g, p.value());
            boolean pref = key.equals(FundLedger.PREFERRED_KEY);
            return new Security(securityName(g, key), pref ? "PREFERRED" : "WARRANTS", v, pref
                    ? "held, not traded: the bank redeems it at par" : "held, not traded: the bank buys them back, or they are exercised", facts);
        }
        return new Security(securityName(g, key), "GONE", "—", "no longer on any book", facts);
    }

    /** The lines of YOUR POSITION: a label, a figure, its colour (null: the head's ink), and a muted line under it (null: none). */
    record PositionLine(String label, String value, String tone, String note) { }

    static List<PositionLine> position(Game g, String key) {
        List<PositionLine> out = new ArrayList<>();
        int c = FundView.companyOf(key);
        List<FundView.Position> lots = new ArrayList<>();
        if (c >= 0) {
            FundView.Position m = FundView.sharePosition(g, c, false), r = FundView.sharePosition(g, c, true);
            if (m != null) lots.add(m);
            if (r != null) lots.add(r);
        } else {
            FundView.Position p = FundView.position(g, key);
            if (p != null) lots.add(p);
        }
        if (lots.isEmpty()) {
            FundLedger.Lot l = g.getFund().getLedger().lot(key);
            out.add(new PositionLine("The fund holds", "none", Palette.TEXT_MUTED, null));
            if (l != null && (l.realized() != 0 || l.income() > 0)) {
                Pnl r = pnl(sym(g), l.realized(), l.acbOut() > 0 ? l.realized() / l.acbOut() : Double.NaN);
                out.add(new PositionLine("Realized, while it held it", r.text(), r.tone(), null));
                out.add(new PositionLine("...and its income", d(g, l.income()), null, null));
            }
            return out;
        }
        boolean two = lots.size() > 1;
        double sumValue = 0, sumAcb = 0, sumReal = 0, sumIncome = 0;
        for (FundView.Position p : lots) {
            boolean bond = p.isBond();
            String head = two ? (FundView.RESCUE.equals(p.kind()) ? "THE RESCUE BOOK" : "THE MARKET BOOK") : null;
            if (head != null) out.add(new PositionLine(head, "", null, null));
            out.add(new PositionLine(bond ? "Face" : FundView.PREFERRED.equals(p.kind()) ? "At par" : "Shares",
                    bond ? d(g, p.units()) : FundView.PREFERRED.equals(p.kind()) ? d(g, p.units()) : shares(p.units()), null, null));
            if (!FundView.WARRANTS.equals(p.kind()) && !FundView.PREFERRED.equals(p.kind())) {
                out.add(new PositionLine("Average cost", price(g, bond, p.average()), null,
                        p.seeded() ? "counted at its market value in " + month(p.since()) + ", when cost tracking began" : null));
                FundLedger.Lot lot = g.getFund().getLedger().lot(p.key());
                if (bargain(lot) != null) {
                    out.add(new PositionLine("Bought below value", pct1(lot.underValue()) + " under", null,
                            "what its buys paid against what they bought was worth at the step's "
                                    + (bond ? "value" : "fair value") + ", " + d(g, lot.boughtValue())));
                }
            }
            out.add(new PositionLine("Adjusted cost base", d(g, p.acb()), null, FundView.WARRANTS.equals(p.kind())
                    ? "they came with the preferred, for nothing" : null));
            out.add(new PositionLine("Market value", d(g, p.value()), null, !p.stale(g.getMonth()) ? null
                    : p.company() >= 0 && g.getExchange().markedAtFair(p.company())
                    ? "at fair value: last traded " + month(p.priceMonth()) : "at its last trade, " + month(p.priceMonth())));
            Pnl un = pnl(sym(g), p.unrealized(), p.unrealizedPct());
            out.add(new PositionLine("Unrealized", un.text(), un.tone(), null));
            Pnl re = pnl(sym(g), p.realized(), Double.NaN);
            out.add(new PositionLine("Realized", re.text(), re.tone(), null));
            String since = p.since() >= 0 ? "since " + month(p.since()) : null;
            out.add(new PositionLine(bond ? "Coupons" : "Dividends", d(g, p.income()), null,
                    p.incomeBefore() > 0 ? "before " + month(p.since()) + ": " + d(g, p.incomeBefore()) : since));
            if (FundView.RESCUE.equals(p.kind())) {
                double yoc = FundView.yieldOnCost(g, p);
                if (Double.isFinite(yoc)) out.add(new PositionLine("Yield on what it cost", pct1(yoc) + " a year", null,
                        "the dividend a share over the average cost a share"));
            }
            Pnl tot = pnl(sym(g), p.totalReturn(), p.acb() > 0 ? p.totalReturn() / p.acb() : Double.NaN);
            out.add(new PositionLine("Total return", tot.text(), tot.tone(), "unrealized, realized and income together"));
            sumValue += p.value();
            sumAcb += Double.isNaN(p.acb()) ? 0 : p.acb();
            sumReal += p.realized();
            sumIncome += p.income();
        }
        if (two) {
            out.add(new PositionLine("BOTH BOOKS", "", null, null));
            out.add(new PositionLine("Market value", d(g, sumValue), null, null));
            out.add(new PositionLine("Adjusted cost base", d(g, sumAcb), null, null));
            Pnl un = pnl(sym(g), sumValue - sumAcb, sumAcb > 0 ? (sumValue - sumAcb) / sumAcb : Double.NaN);
            out.add(new PositionLine("Unrealized", un.text(), un.tone(), null));
            Pnl all = pnl(sym(g), sumValue - sumAcb + sumReal + sumIncome, Double.NaN);
            out.add(new PositionLine("Total return", all.text(), all.tone(), null));
        }
        return out;
    }

    /* ------------------------------ the ticket ------------------------------ */

    /** What the ticket says: its quote's lines, the press, and the sentence under the button (null: none). */
    record Ticket(FundView.Quote quote, List<PositionLine> lines, Press press, String held, double limit) { }

    /** The price a unit the ticket would post at for its kind, and 0 for fair value (what the rule asks at; a bond's value is its bid too). */
    static double limitFor(Game g, String key, boolean buy, String kind, int steps) {
        int c = FundView.companyOf(key);
        CorporateBond b = c < 0 ? g.getBondMarket().bond(FundView.bondIdOf(key)) : null;
        double fair = c >= 0 ? g.getExchange().fair(c) : b != null ? g.getBondMarket().modelPrice(b, g.getMonth()) : Double.NaN;
        OrderBook book = c >= 0 ? g.getExchange().bookOf(c) : b != null ? g.getBondMarket().bookOf(b) : null;
        if (book == null) return 0;
        return switch (kind) {
            case BID -> book.bestBid() > 0 ? book.bestBid() : 0;
            case ASK -> book.bestAsk() > 0 ? book.bestAsk() : 0;
            case LAST -> book.lastPrice() > 0 ? book.lastPrice() : 0;
            case OWN -> fair > 0 ? fair * (1 + steps / 100.0) : 0;
            default -> 0;
        };
    }

    /**
     * THE TICKET'S WORDS (the spec's 4.4): FundView.quote() on today's book,
     * said line by line - what is offered at the price and what would rest,
     * what stands ahead of it, the price over fair value, no fee, the free
     * cash after, the position after, a sale's cost out and what it realizes,
     * the mark it would leave, and the cap - and the action button's press:
     * CHOOSE with nothing chosen, GO to review and GO to place, HELD with no
     * cash to buy, nothing to sell, or no room under the cap for a buy.
     */
    static Ticket ticket(Game g, String key, boolean buy, boolean byAmount, double figure, String kind, int steps, boolean review) {
        boolean bond = key != null && key.startsWith("B:");
        String name = securityName(g, key);
        double limit = limitFor(g, key, buy, kind, steps);
        FundView.Quote q = FundView.quote(g, new FundView.Order(key, buy, byAmount, Math.max(0, figure), limit));
        List<PositionLine> lines = new ArrayList<>();
        if (q == null) return new Ticket(null, lines, new Press(Look.HELD, "Not on any book", "it cannot be traded"), null, 0);
        String u = bond ? "of face" : "shares";
        String unitsWord = bond ? d(g, q.units()) + " of face" : shares(q.units()) + " shares";
        lines.add(new PositionLine("At", price(g, bond, q.price()), null, limit > 0 ? null : bond
                ? "its value: the price the rule posts at" : "fair value: what the rule asks; it bids "
                        + pct1(TreasuryFund.RULE_PREMIUM).replace(".0", "") + " over, at the desk's ask"));
        if (q.units() > 0) {
            lines.add(new PositionLine(buy ? "You would buy" : "You would sell", unitsWord + " · " + d(g, q.money()), null,
                    q.capped() ? "capped at the room under the " + pct1(TreasuryFund.OWNERSHIP_LIMIT).replace(".0", "")
                            + " cap: past it the rule asks the excess back at fair value from the next step" : null));
            lines.add(new PositionLine(buy ? "Offered at or under it today" : "Bid at or over it today",
                    q.offeredUnits() > FundLedger.DUST ? (bond ? d(g, q.offeredUnits()) + " of face" : shares(q.offeredUnits()))
                            + " for " + d(g, q.offeredMoney()) + ", " + price(g, bond, q.offeredAverage()) + " on average" : "nothing",
                    null, "on today's book: the step withdraws every order and posts again before yours"));
            if (q.rests() > FundLedger.DUST) {
                String ahead = q.aheadUnits() > FundLedger.DUST ? "behind " + (bond ? d(g, q.aheadUnits()) + " of face" : shares(q.aheadUnits()) + " shares")
                        + " at or " + (buy ? "over" : "under") + " its price"
                        + (q.aheadRule() > FundLedger.DUST ? ", " + (bond ? d(g, q.aheadRule()) : shares(q.aheadRule())) + " of them the rule's own" : "")
                        + (Double.isFinite(q.aheadBuyback()) ? ", the company's own buyback at " + price(g, false, q.aheadBuyback()) + " first" : "")
                        : "nobody ahead of it at that price";
                lines.add(new PositionLine("Would wait for the month", (bond ? d(g, q.rests()) + " of face" : shares(q.rests()) + " shares"), null, ahead));
            }
        }
        if (Double.isFinite(q.overFair()) && Math.abs(q.overFair()) > 1e-9) {
            lines.add(new PositionLine(q.overFair() > 0 ? "Over fair value" : "Under fair value", signedPct(q.overFair()), null,
                    buy ? (q.overFair() > 0 ? "the seller is paid more than the register reckons; the fund carries it at the last trade"
                            : "a seller has to come down to it")
                            : (q.overFair() > 0 ? "a buyer has to come up to it" : "the buyer pays less than the register reckons")));
        }
        lines.add(new PositionLine("Fee", "none", null, "the books charge nobody to trade"));
        double free = g.fundCashFree();
        boolean short_ = buy && q.money() > free + 1e-9;
        if (buy) {
            lines.add(new PositionLine("The fund's free cash", d(g, q.cash()) + " → " + d(g, q.cashAfter()),
                    q.cashAfter() < -1e-9 ? Palette.WARN : null, "held for this order from now until the step after next; "
                    + "the rule bids with the rest"));
            if (short_ && free > 0) {
                lines.add(new PositionLine("More than its free cash", "only " + d(g, free) + " of it is placed", Palette.WARN,
                        "an order holds its money from the moment it is placed; pay in for the rest"));
            }
        }
        if (q.units() > 0) {
            lines.add(new PositionLine("The position after", (bond ? d(g, q.unitsAfter()) + " " + u : shares(q.unitsAfter()) + " " + u),
                    null, "if all of it fills: cost " + d(g, q.acbAfter()) + ", " + price(g, bond, q.averageAfter()) + " on average"));
            if (!buy) {
                Pnl r = pnl(sym(g), q.realized(), q.acbOut() > 0 && q.offeredUnits() > 0 && q.units() > 0
                        ? q.realized() / (q.acbOut() * q.offeredUnits() / q.units()) : Double.NaN);
                lines.add(new PositionLine("Cost out", d(g, q.acbOut()), null, "its share of the adjusted cost base"));
                lines.add(new PositionLine("Realized on what is bid today", r.text(), r.tone(), null));
            }
            if (Double.isFinite(q.markAfter())) {
                lines.add(new PositionLine("The last trade would be", price(g, bond, q.markAfter()),
                        null, bond ? "the bond's last trade; the fund marks its bonds at their value"
                        : "the price every holding is marked at" + (!Double.isFinite(q.markMove()) ? ""
                        : Math.abs(q.markMove()) < 0.0005 ? ": what the fund keeps would be marked where it is now"
                        : ": what the fund keeps would be marked " + signedPct(q.markMove()))));
            }
            if (!bond) {
                lines.add(new PositionLine("The market book against the cap", pct1(q.capBefore()) + " → " + pct1(q.capAfter()), null,
                        (q.capOrders() >= 0.0005 ? (buy ? "counting the " + pct1(q.capOrders()) + " the fund's other buys on it could add"
                                : "the fund's buys on it could add " + pct1(q.capOrders())) + "; " : "")
                                + (buy && q.ruleGivesWay() > FundLedger.DUST ? "the rule's bid makes way for "
                                + shares(q.ruleGivesWay()) + " shares of yours; " : "")
                                + "room for " + shares(q.room()) + " shares, " + d(g, q.cappedAt()) + " at this price"));
            }
        }
        // ...and a buy the cap leaves no room for: the step would post none of it (Exchange.fundRoom()).
        boolean noRoom = buy && !bond && q.capped() && !(q.units() > FundLedger.DUST);
        String cap = pct1(TreasuryFund.OWNERSHIP_LIMIT).replace(".0", "");
        if (noRoom) {
            lines.add(new PositionLine("The market book against the cap", pct1(q.capBefore()) + " held"
                    + (q.capOrders() >= 0.0005 ? ", " + pct1(q.capOrders()) + " more in your orders" : ""), null,
                    "no room for this order under the " + cap + " cap: the step would post none of it"));
        }
        Press press;
        String held = null;
        double holding = q.unitsHeld();
        String money = d(g, short_ ? free : q.money());
        if (buy && !(free > 0)) {
            press = new Press(Look.HELD, "The fund's free cash is " + d(g, 0), "pay in from the treasury first");
            held = "pay in";
        } else if (!buy && !(holding > FundLedger.DUST)) {
            press = new Press(Look.HELD, "The fund holds none to sell", null);
        } else if (noRoom) {
            press = new Press(Look.HELD, "No room under the " + cap + " cap", q.capOrders() >= 0.0005
                    ? "what the fund holds of it and your orders on it come to it" : "the fund holds that much of it already");
        } else if (!(figure > 0) || !(q.units() > FundLedger.DUST)) {
            press = new Press(Look.CHOOSE, buy ? "Choose an amount" : "Choose how many", null);
        } else if (!review) {
            press = new Press(Look.GO, "Review: " + (buy ? "buy " : "sell ") + (buy && (byAmount || short_) ? money + " of "
                    : unitsWord + " of ") + name, null);
        } else {
            press = new Press(Look.GO, "Place it: " + (buy ? "buy " : "sell ") + (buy && (byAmount || short_) ? money
                    : unitsWord), "on the book at the next step, good for a month");
        }
        return new Ticket(q, lines, press, held, limit);
    }

    /* ------------------------------ the record ------------------------------ */

    /** One line of Activity: its month, what happened, who, the units, the money, what it realized, the lot to open, and lines it holds (a month's rule trades together). */
    record Act(int month, String what, String by, String units, String money, Pnl realized, String key, boolean fromLog,
               List<Act> inside) { }

    /** The record's lines, newest first, grouped: a month's rule trades on one side of one market are one line with the lots inside. */
    static List<Act> acts(Game g, String group, String key, int months) {
        List<Act> out = new ArrayList<>();
        int now = g.getMonth();
        List<FundView.Line> lines = FundView.activity(g, group, key);
        int i = 0;
        while (i < lines.size()) {
            FundView.Line l = lines.get(i);
            if (months > 0 && now - l.month() >= months) break;
            FundLedger.Activity a = l.row();
            if (a != null && !a.hand() && (FundLedger.BUY.equals(a.kind()) || FundLedger.SELL.equals(a.kind())) && key == null) {
                // The rule's same-month, same-side trades in one market: one line.
                boolean bondRow = a.key() != null && a.key().startsWith("B:");
                List<Act> inside = new ArrayList<>();
                double money = 0, realized = 0;
                int j = i;
                while (j < lines.size()) {
                    FundView.Line m = lines.get(j);
                    FundLedger.Activity b = m.row();
                    if (b == null || b.hand() || b.month() != a.month() || !b.kind().equals(a.kind())
                            || (b.key() != null && b.key().startsWith("B:")) != bondRow) break;
                    inside.add(act(g, m));
                    money += b.money();
                    realized += b.realized();
                    j++;
                }
                if (inside.size() == 1) out.add(inside.get(0));
                else {
                    String verb = FundLedger.BUY.equals(a.kind()) ? "bought" : "sold";
                    out.add(new Act(a.month(), "The rule " + verb + " " + inside.size() + (bondRow ? " bonds" : " companies' shares"),
                            "the rule", "", d(g, money), FundLedger.SELL.equals(a.kind()) ? pnl(sym(g), realized, Double.NaN) : null,
                            null, false, inside));
                }
                i = j;
                continue;
            }
            out.add(act(g, l));
            i++;
        }
        return out;
    }

    /** One line, in words. */
    static Act act(Game g, FundView.Line l) {
        if (l.row() == null) {
            if (l.resolution() != null) {
                return new Act(l.month(), "Rescued the bank for its shares: " + d(g, l.resolution().paid())
                        + " (from the record of rescues)", "the model", "", d(g, l.resolution().paid()), null,
                        FundLedger.rescueKey(Equity.COMPANIES[Equity.BANK]), true, List.of());
            }
            return new Act(l.month(), l.entry().label() + " (from the decision log)", "you", "", "", null, null, true, List.of());
        }
        FundLedger.Activity a = l.row();
        boolean bond = a.key() != null && a.key().startsWith("B:");
        String name = a.key() == null ? "" : securityName(g, a.key());
        String units = a.units() > 0 && (FundLedger.BUY.equals(a.kind()) || FundLedger.SELL.equals(a.kind())
                || FundLedger.MATURED.equals(a.kind()))
                ? (bond ? d(g, a.units()) + " face" : shares(a.units())) : "";
        Pnl real = a.realized() != 0 ? pnl(sym(g), a.realized(), Double.NaN) : null;
        String what, by = a.hand() ? "your order" : "the model";
        switch (a.kind()) {
            case FundLedger.BUY, FundLedger.SELL -> {
                boolean b = FundLedger.BUY.equals(a.kind());
                if (a.hand() && a.asked() > 0) {
                    String done = (bond ? d(g, a.units()) : shares(a.units())) + " of " + (bond ? d(g, a.asked()) + " face" : shares(a.asked()) + " shares");
                    String at = a.units() > FundLedger.DUST ? " at " + price(g, bond, a.average()) : "";
                    what = "Your order: " + (b ? "bought " : "sold ") + done + " of " + name + at
                            + (a.open() ? " so far, on the book until the next step"
                            : a.lapsed() > FundLedger.DUST ? " - " + (bond ? d(g, a.lapsed()) + " face" : shares(a.lapsed()))
                            + " lapsed" + (b ? ", " + d(g, a.lapsed() * a.limit()) + " back to the fund" : "") : "");
                } else {
                    what = (a.hand() ? "Your order " : "The rule ") + (b ? "bought " : "sold ") + name
                            + (a.units() > FundLedger.DUST ? " at " + price(g, bond, a.average()) : "");
                    by = a.hand() ? "your order" : "the rule";
                }
                if (!a.hand()) by = "the rule";
                if (b && a.underValue() >= FundLedger.BARGAIN) what += ", " + Math.round(a.underValue() * 100) + "% under its value";
                if (a.hand() && !bond && a.units() > FundLedger.DUST) what += markAfter(g, a);
            }
            case FundLedger.LAPSED -> what = "Your order to " + (a.buy() ? "buy " : "sell ") + name + " was not posted: "
                    + (a.note() == null ? "nothing to post" : a.note());
            case FundLedger.DIVIDEND -> what = "Dividends from " + (int) Math.round(a.units()) + (a.units() == 1 ? " company" : " companies");
            case FundLedger.COUPON -> what = "Coupons on " + (int) Math.round(a.units()) + (a.units() == 1 ? " bond" : " bonds");
            case FundLedger.MATURED -> what = name + " repaid at par";
            case FundLedger.WRITTEN_DOWN -> what = "gone".equals(a.note()) ? "Written off: " + name + ", its face gone"
                    : "Written down: " + name + ", " + d(g, a.units()) + " of its face";
            case FundLedger.RESCUE -> what = "Rescued the bank for its shares: " + d(g, a.money())
                    + (a.realized() < 0 ? "; its market-book shares went with the rest, for nothing" : "");
            case FundLedger.PREFERRED -> what = switch (a.note() == null ? "" : a.note()) {
                case "bought" -> "Bought the bank's preferred";
                case "redeemed" -> "The bank redeemed its preferred at par";
                case "cancelled" -> "The bank's preferred cancelled: the hole took it";
                default -> "Dividends on the bank's preferred";
            };
            case FundLedger.WARRANTS -> what = switch (a.note() == null ? "" : a.note()) {
                case "bought back" -> "The bank bought its warrants back";
                case "exercised" -> "The warrants exercised: " + shares(a.units()) + " new shares into the rescue book";
                default -> "The warrants cancelled with the preferred";
            };
            case FundLedger.PAY_IN -> { what = a.hand() ? "Paid in from the treasury" : "Paid in by the dial, at the year end"; by = a.hand() ? "you" : "the dial"; }
            case FundLedger.DRAW_OUT -> { what = "Drawn out to the treasury"; by = "you"; }
            case FundLedger.TRANSFER -> what = "The withdrawal to the treasury";
            case FundLedger.SPLIT -> what = name + " split " + (a.note() == null ? "" : a.note());
            case FundLedger.TRACKING -> what = a.units() > 0 ? "Cost tracking began with " + (int) Math.round(a.units())
                    + (Math.round(a.units()) == 1 ? " holding" : " holdings")
                    + ": those bought on the market counted at their market value this month" : "Cost tracking began";
            default -> what = a.kind();
        }
        String money = a.money() > 0 ? d(g, a.money()) : "";
        return new Act(a.month(), what, by, units, money, real, a.key(), false, List.of());
    }

    /** A hand trade in shares, after it: the month's last trade is every holding's mark - "your own trade moves the mark" (the spec's 5) - while that month's is still the last. */
    static String markAfter(Game g, FundLedger.Activity a) {
        int c = FundView.companyOf(a.key());
        if (c < 0) return "";
        OrderBook book = g.getExchange().bookOf(c);
        if (book == null || book.lastTradeMonth() != a.month() || !(book.lastPrice() > 0)) return "";
        return " - the month's last trade, " + price(g, false, book.lastPrice()) + ", is its mark";
    }

    /* =====================================================================
       PORTFOLIO - what is it worth, has it made money, and on what?
       ===================================================================== */

    /** WHAT IT HOLDS' (i). */
    static String holdsInfo() {
        return String.format("Shares at the exchange's price, bonds at the market's valuation, the preferred at par and "
                        + "its warrants at their Black-Scholes value. The rule keeps %s of its market book and cash in "
                        + "shares and the rest in bonds, and trades back when shares pass %s or fall %.0f points under "
                        + "the aim. It buys only on the market - never a new issue, never the city's own paper - and what "
                        + "does not fit waits as cash, which earns nothing, as the treasury's own does not.",
                pct1(TreasuryFund.EQUITY_WEIGHT), pct1(TreasuryFund.REBALANCE_OVER), TreasuryFund.REBALANCE_UNDER * 100);
    }

    /** THE CITY'S FUND's (i). */
    static final String HERO_INFO = "Everything the fund holds at the marks every other holder uses, and its cash. The "
            + "return beside it is over the chart's window: what it gained less what was paid in and taken out in that "
            + "time, against what it was worth at the start (Modified Dietz, each flow weighted by the time it was in). "
            + "Green is a gain and red a loss; the prices themselves never are.";

    void portfolioPage(VBox page) {
        Game g = ui.game;
        HistorySave h = g.getHistorySave();
        // The window as the chart will place it, so the hero's return reads this month's window and not last month's.
        List<Integer> axis = h.getMonth();
        if (axis.size() >= 2) worthWindow.setData(axis.get(0), axis.get(axis.size() - 1));
        Hero w = hero(g, h.months() >= 2 ? worthWindow : null);

        /* ----- the hero: its worth, its return, since it began; the chart ----- */
        Label value = figure(w.value(), 28, Palette.TEXT_HEAD);
        if (landed && Double.isFinite(shownWorth)) SectorScreen.countUp(value, shownWorth, g.fundValue(), v -> d(g, v));
        shownWorth = g.fundValue();
        Label ret = words(w.ret(), Palette.SIZE_BODY + 1, w.retTone());
        VBox left = new VBox(6, caption("THE CITY'S FUND", HERO_INFO), value, ret);
        if (w.recorded() != null) left.getChildren().add(muted(w.recorded()));
        left.getChildren().add(infoLine(w.since(), w.sinceInfo(), true, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL, 340));
        left.getChildren().add(mini("CASH", w.cash(), w.cashNote(), null));
        left.getChildren().add(mini("INCOME LAST MONTH", w.income(), w.incomeNote(), null));
        left.getChildren().add(mini("TO THE TREASURY LAST MONTH", w.transfer(), w.transferNote(), w.transferTone()));
        Node right;
        if (h.months() >= 2) {
            TimeChart chart = worthChart(g);
            Label title = new Label("WHAT IT IS WORTH");
            title.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
            title.setMinWidth(Region.USE_PREF_SIZE);
            chart.lead().getChildren().setAll(title);
            chart.onSettled(this::redraw);
            right = chart;
        } else {
            right = card(caption("WHAT IT IS WORTH", null), line("The fund's worth is recorded a month at a time from this "
                    + "build on; a month on, the chart starts."));
        }
        page.getChildren().add(heroCard(left, 360, right));

        /* ----- what it holds, and the rule's aim (the old Holdings page's, whole) ----- */
        page.getChildren().add(holdsCard(g));

        /* ----- every holding ----- */
        page.getChildren().add(holdingsCard(g));

        /* ----- since it began, by kind; closed ----- */
        page.getChildren().add(pair(kindsCard(g), closedCard(g)));

        /* ----- the doors ----- */
        HBox doors = new HBox(10, doorPill("Search shares and bonds", Icons.EXCHANGE, Palette.MONEY, () -> go("Search")),
                doorPill("Activity", Icons.REPORTS, Palette.MONEY, () -> go("Activity")),
                doorPill("Rules & cash", Icons.SETTINGS, Palette.MONEY, () -> go("Rules & cash")));
        doors.setAlignment(Pos.CENTER_LEFT);
        page.getChildren().add(doors);
    }

    /** A small figure under the hero's: its caption, the figure, and a line under it. */
    static VBox mini(String cap, String value, String note, String tone) {
        Label c = new Label(cap);
        c.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        c.setMinWidth(Region.USE_PREF_SIZE);
        VBox box = new VBox(1, c, figure(value, Palette.SIZE_LEAD, tone == null ? Palette.TEXT_HEAD : tone));
        if (note != null) box.getChildren().add(words(note, Palette.SIZE_CAPTION, tone == null ? Palette.TEXT_MUTED : tone));
        box.setStyle("-fx-padding: 4 0 0 0;");
        return box;
    }

    /** The fund's worth and the net put in on City History's big chart, the FUND and BANK decisions as flags. */
    TimeChart worthChart(Game g) {
        HistorySave h = g.getHistorySave();
        List<TimeChart.Line> lines = List.of(
                new TimeChart.Line("fundValue", "what it is worth", Palette.MONEY, 0, h.aligned("fundValue"),
                        v -> HistoryScreen.plotScale("money", v), v -> d(g, v), ""),
                new TimeChart.Line("fundNet", "put in, less taken out", Palette.RAMP_REST, 0, FundView.netPutIn(h),
                        v -> HistoryScreen.plotScale("money", v), v -> d(g, v), ""));
        TimeChart chart = new TimeChart(worthWindow, worthHidden, true);
        chart.setData(h.getMonth(), lines, HistoryScreen.axisFor("money"), null, false, false, null, List.of(), List.of(),
                h.getMonth().isEmpty() ? List.of()
                        : ChartModel.onAxis(ChartModel.flagsOf(g.getDecisions(), DecisionLog.FUND, DecisionLog.BANK), h.getMonth().get(0)),
                "no months recorded yet");
        // The hero's right at the 1,389 window: the page's 1,234 less the card's padding, the left's 360 and the gap.
        chart.setSize(800, 230);
        chart.withoutFullScreen();
        return chart;
    }

    /** WHAT IT HOLDS: the four parts as one bar, and the shares against the rule's aim (the Holdings page's until 0.7.39, moved whole). */
    VBox holdsCard(Game g) {
        TreasuryFund fund = g.getFund();
        VBox holds = card(caption("WHAT IT HOLDS", holdsInfo(), hint(d(g, g.fundValue()) + " altogether")));
        holds.getChildren().add(segmentBar(List.of(
                seg(fund.getCash(), Palette.MONEY_LIGHT, "its cash " + d(g, fund.getCash())),
                seg(g.fundMarketSharesValue(), Palette.BUSINESS, "company shares, its market book " + d(g, g.fundMarketSharesValue())),
                seg(g.fundBondsValue(), Palette.MONEY, "company bonds " + d(g, g.fundBondsValue())),
                seg(g.fundRescueValue(), Palette.MONEY_DARK, "the rescue book " + d(g, g.fundRescueValue()))), 0, null, 0, 16));
        holds.getChildren().add(keyRow(keySwatch(Palette.MONEY_LIGHT, "cash " + d(g, fund.getCash())),
                keySwatch(Palette.BUSINESS, "shares " + d(g, g.fundMarketSharesValue())),
                keySwatch(Palette.MONEY, "bonds " + d(g, g.fundBondsValue())),
                keySwatch(Palette.MONEY_DARK, "the rescue book " + d(g, g.fundRescueValue()))));
        holds.getChildren().add(cardLine("Shares, of its market book and cash", pct1(g.fundEquityShare())
                + " · the rule's aim " + pct1(TreasuryFund.EQUITY_WEIGHT), null));
        holds.getChildren().add(segmentBar(List.of(seg(g.fundEquityShare(), Palette.BUSINESS, "shares "
                        + pct1(g.fundEquityShare()))), 1,
                List.of(new Tick(TreasuryFund.EQUITY_WEIGHT, Palette.TEXT_HEAD, 3, "aim " + pct1(TreasuryFund.EQUITY_WEIGHT), null),
                        new Tick(TreasuryFund.REBALANCE_OVER, Palette.TEXT_MUTED, 2, null, "it sells shares past "
                                + pct1(TreasuryFund.REBALANCE_OVER)),
                        new Tick(TreasuryFund.EQUITY_WEIGHT - TreasuryFund.REBALANCE_UNDER, Palette.TEXT_MUTED, 2, null,
                                "it buys shares under " + pct1(TreasuryFund.EQUITY_WEIGHT - TreasuryFund.REBALANCE_UNDER))),
                0, 10));
        return holds;
    }

    /** HOLDINGS' columns: the name, units, price, worth, average cost, unrealized, and the share of the fund's bar. */
    static final double COL_NAME = 250, COL_UNITS = 120, COL_PRICE = 110, COL_VALUE = 110, COL_AVG = 110, COL_PNL = 160;

    /** HOLDINGS' (i). */
    static final String HOLDINGS_INFO = "Every lot the fund holds, at its mark, with what it cost by the average-cost "
            + "method: a buy adds what it paid to the lot's adjusted cost base, a sale takes out the cost of what it "
            + "sold in proportion, and the average is the cost base over the units - so a split moves the average and "
            + "not the cost. The rule's buys and yours are one lot a company; the bank's rescue book is its own. "
            + "Dividends and coupons are income, not cost. A holding bought before this build tracked costs is counted "
            + "at its market value in the month it began, and says so.";

    VBox holdingsCard(Game g) {
        List<Holding> all = holdings(g);
        VBox card = card(caption("HOLDINGS", HOLDINGS_INFO, hint(all.isEmpty() ? "none" : all.size() + (all.size() == 1 ? " lot" : " lots"))));
        if (all.isEmpty()) {
            card.getChildren().add(line("It holds nothing yet: what is paid in waits as cash until the book offers something."));
            return card;
        }
        card.getChildren().add(headRow());
        for (String grp : new String[] {SHARES, RESCUE, BONDS}) {
            List<Holding> rows = new ArrayList<>();
            for (Holding hd : all) if (hd.group().equals(grp)) rows.add(hd);
            if (rows.isEmpty()) continue;
            rows.sort((a, b) -> Double.compare(b.worth(), a.worth()));
            card.getChildren().add(groupHead(grp));
            if (BONDS.equals(grp)) {
                // Folded by issuer: a row an issuer, its bonds inside it.
                Map<String, List<Holding>> byIssuer = new LinkedHashMap<>();
                for (Holding hd : rows) byIssuer.computeIfAbsent(hd.issuer(), k -> new ArrayList<>()).add(hd);
                for (Map.Entry<String, List<Holding>> e : byIssuer.entrySet()) {
                    String k = "fund:issuer:" + e.getKey();
                    boolean shown = open.contains(k);
                    double worth = 0;
                    for (Holding hd : e.getValue()) worth += hd.worth();
                    card.getChildren().add(issuerRow(g, e.getKey(), e.getValue().size(), worth, shown, () -> {
                        if (!open.remove(k)) open.add(k);
                        redraw();
                    }));
                    if (shown) for (Holding hd : e.getValue()) card.getChildren().add(holdingRow(g, hd, true));
                }
                continue;
            }
            int small = 0;
            double smallWorth = 0;
            for (Holding hd : rows) {
                if (hd.worth() < FundView.FOLD_UNDER && !open.contains("fund:small:" + grp)) { small++; smallWorth += hd.worth(); continue; }
                card.getChildren().add(holdingRow(g, hd, false));
            }
            if (small > 0) {
                String k = "fund:small:" + grp;
                Label more = new Label("and " + small + " more under " + d(g, FundView.FOLD_UNDER) + " ▸");
                more.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.ACCENT) + " -fx-cursor: hand; -fx-padding: 4 0 4 6;");
                more.setOnMouseClicked(e -> { open.add(k); redraw(); });
                card.getChildren().add(more);
            }
        }
        return card;
    }

    /** The column heads over the holdings. */
    static HBox headRow() {
        HBox row = new HBox(8, cell("", COL_NAME, false), cell("UNITS", COL_UNITS, true), cell("PRICE", COL_PRICE, true),
                cell("WORTH", COL_VALUE, true), cell("AVERAGE COST", COL_AVG, true), cell("UNREALIZED", COL_PNL, true));
        Label share = new Label("OF THE FUND");
        share.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        share.setMinWidth(0);
        HBox.setHgrow(share, Priority.ALWAYS);
        share.setMaxWidth(Double.MAX_VALUE);
        row.getChildren().add(share);
        row.setStyle("-fx-padding: 0 26 2 6;");
        return row;
    }

    /** A column head. */
    static Label cell(String text, double width, boolean right) {
        Label l = new Label(text);
        l.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        l.setWrapText(true);
        l.setMinWidth(width);
        l.setPrefWidth(width);
        l.setMaxWidth(width);
        l.setAlignment(right ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return l;
    }

    static Label groupHead(String text) {
        Label l = new Label(text);
        l.setStyle(Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_LABEL) + " -fx-padding: 8 0 2 6;");
        return l;
    }

    /** A figure in a column, right-aligned, wrapping rather than cut, with a muted line under it (null: none). */
    static VBox figureCell(String value, String tone, String note, double width) {
        Label v = new Label(value);
        v.setStyle(Palette.figure(Palette.SIZE_BODY, tone == null ? Palette.TEXT_HEAD : tone));
        v.setWrapText(true);
        v.setMinWidth(0);
        v.setMaxWidth(width);
        v.setAlignment(Pos.CENTER_RIGHT);
        v.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);
        VBox box = new VBox(1, v);
        if (note != null) {
            Label n = new Label(note);
            n.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            n.setWrapText(true);
            n.setMinWidth(0);
            n.setMaxWidth(width);
            n.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);
            box.getChildren().add(n);
        }
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setMinWidth(width);
        box.setPrefWidth(width);
        box.setMaxWidth(width);
        return box;
    }

    /** The icon a holding is drawn with: its sector's, the bank's, a bond's paper, the safe for the preferred. */
    String iconOf(String key) {
        int c = FundView.companyOf(key);
        if (c == Equity.BANK) return Icons.BANK;
        if (c >= 0) return Icons.ofSector(ui.game.getSectors().byKey(Equity.COMPANIES[c]));
        if (key != null && key.startsWith("B:")) return Icons.PAPER;
        return Icons.SAFE;
    }

    /** ...and its colour. */
    static String colourOf(String key) {
        int c = FundView.companyOf(key);
        if (c == Equity.BANK) return Palette.MONEY_DARK;
        if (c >= 0) return Palette.BUSINESS;
        return Palette.MONEY;
    }

    /** One holding as a row: a click opens its security. */
    HBox holdingRow(Game g, Holding hd, boolean indented) {
        Label name = new Label(hd.name());
        name.setWrapText(true);
        name.setMinWidth(0);
        name.setStyle(Palette.words(Palette.SIZE_BODY + 1, Palette.TEXT_BODY));
        Label sub = new Label(hd.sub());
        sub.setWrapText(true);
        sub.setMinWidth(0);
        sub.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        VBox named = new VBox(0, name, sub);
        if (hd.paidNow() != null) {
            // What it paid in the month just landed, in the money's blue - income, not a verdict - popping as the month lands.
            Label paid = new Label(hd.paidNow());
            paid.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.MONEY));
            paid.setWrapText(true);
            paid.setMinWidth(0);
            named.getChildren().add(paid);
            if (landed) pop(paid);
        }
        HBox.setHgrow(named, Priority.ALWAYS);
        HBox lead = new HBox(8, iconSquare(iconOf(hd.key()), colourOf(hd.key()), 24, 13), named);
        lead.setAlignment(Pos.CENTER_LEFT);
        double nameWidth = indented ? COL_NAME - 18 : COL_NAME;
        lead.setMinWidth(nameWidth);
        lead.setPrefWidth(nameWidth);
        lead.setMaxWidth(nameWidth);
        Region bar = Pieces.bar(hd.share(), colourOf(hd.key()), 120, 8);
        HBox barBox = new HBox(6, bar, figure(pct1(hd.share()), Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        barBox.setAlignment(Pos.CENTER_LEFT);
        barBox.setMinWidth(0);
        HBox.setHgrow(barBox, Priority.ALWAYS);
        Label arrow = new Label("›");
        arrow.setStyle(Palette.strong(Palette.SIZE_HEADING + 2, Palette.ACCENT));
        arrow.setMinWidth(16);
        HBox row = new HBox(8, lead,
                figureCell(hd.units(), null, null, COL_UNITS),
                figureCell(hd.price(), null, hd.priceNote(), COL_PRICE),
                figureCell(hd.value(), null, null, COL_VALUE),
                figureCell(hd.average(), null, hd.averageTag(), COL_AVG),
                figureCell(hd.unrealized().text(), hd.unrealized().tone(), null, COL_PNL),
                barBox, arrow);
        row.setAlignment(Pos.CENTER_LEFT);
        String rest = "-fx-padding: 5 6 5 " + (indented ? 24 : 6) + "; -fx-background-radius: 6; -fx-cursor: hand;"
                + " -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 0 1 0;";
        row.setStyle(rest);
        row.setOnMouseEntered(e -> row.setStyle(rest + " -fx-background-color: " + Palette.CONTROL + ";"));
        row.setOnMouseExited(e -> row.setStyle(rest));
        row.setOnMouseClicked(e -> openSecurity(hd.key()));
        return row;
    }

    /** A label grown a little and back, once, as a month lands on it. */
    static void pop(Label l) {
        javafx.animation.ScaleTransition up = new javafx.animation.ScaleTransition(Duration.millis(POP_MILLIS), l);
        up.setFromX(1); up.setFromY(1);
        up.setToX(1.12); up.setToY(1.12);
        up.setAutoReverse(true);
        up.setCycleCount(2);
        up.play();
    }

    /** How long a payment's pop takes each way. */
    static final double POP_MILLIS = 220;

    /** An issuer's bonds folded into one row: "▸ Automotive · 8 bonds", its worth. */
    HBox issuerRow(Game g, String issuer, int bonds, double worth, boolean shown, Runnable toggle) {
        Label name = new Label((shown ? "▾ " : "▸ ") + issuer + " · " + bonds + (bonds == 1 ? " bond" : " bonds"));
        name.setStyle(Palette.words(Palette.SIZE_BODY + 1, Palette.TEXT_BODY));
        name.setWrapText(true);
        name.setMinWidth(0);
        HBox lead = new HBox(8, iconSquare(Icons.PAPER, Palette.MONEY, 24, 13), name);
        lead.setAlignment(Pos.CENTER_LEFT);
        lead.setMinWidth(COL_NAME);
        lead.setPrefWidth(COL_NAME);
        lead.setMaxWidth(COL_NAME);
        Region gap = new Region();
        gap.setMinWidth(COL_UNITS + COL_PRICE + 16);
        HBox row = new HBox(8, lead, gap, figureCell(d(g, worth), null, null, COL_VALUE));
        row.setAlignment(Pos.CENTER_LEFT);
        String rest = "-fx-padding: 5 6 5 6; -fx-background-radius: 6; -fx-cursor: hand;"
                + " -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 0 1 0;";
        row.setStyle(rest);
        row.setOnMouseEntered(e -> row.setStyle(rest + " -fx-background-color: " + Palette.CONTROL + ";"));
        row.setOnMouseExited(e -> row.setStyle(rest));
        row.setOnMouseClicked(e -> toggle.run());
        return row;
    }

    /** SINCE IT BEGAN, BY KIND's (i). */
    static final String KINDS_INFO = "Each kind of holding since the fund began: what the city put into it (bought on "
            + "the book, paid in a rescue, the preferred), what came back out of it (sold, repaid, redeemed), what it is "
            + "worth now and the income it paid. Its gain is worth plus got back plus income less put in, and the four add "
            + "up to the fund's own gain. Exact from the fund's record of every flow, on any save, whatever each lot's cost "
            + "was before cost tracking began.";

    VBox kindsCard(Game g) {
        VBox card = card(caption("SINCE IT BEGAN, BY KIND", KINDS_INFO));
        GridPane t = new GridPane();
        t.setHgap(8);
        t.setVgap(3);
        String[] heads = {"", "PUT IN", "GOT BACK", "WORTH", "INCOME", "GAIN"};
        for (int i = 0; i < heads.length; i++) {
            Label l = new Label(heads[i]);
            l.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            l.setWrapText(true);
            t.add(l, i, 0);
            if (i > 0) GridPane.setHalignment(l, javafx.geometry.HPos.RIGHT);
        }
        int r = 1;
        for (KindLine k : kinds(g)) {
            boolean total = "The fund".equals(k.name());
            Label name = words(k.name(), Palette.SIZE_LABEL + 1, total ? Palette.TEXT_HEAD : Palette.TEXT_LABEL);
            name.setMinWidth(90);
            t.add(name, 0, r);
            String[] vals = {k.putIn(), k.gotBack(), k.worth(), k.income()};
            for (int i = 0; i < vals.length; i++) {
                Label v = figure(vals[i], Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD);
                t.add(v, i + 1, r);
                GridPane.setHalignment(v, javafx.geometry.HPos.RIGHT);
            }
            // The gain wraps its percentage under it rather than push the card past its 612.
            Label gain = figure(k.gain().text(), Palette.SIZE_LABEL + 1, k.gain().tone());
            gain.setWrapText(true);
            gain.setMinWidth(0);
            gain.setMaxWidth(150);
            gain.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);
            t.add(gain, 5, r);
            GridPane.setHalignment(gain, javafx.geometry.HPos.RIGHT);
            r++;
            if (k.note() != null) {
                Label n = words(k.note(), Palette.SIZE_CAPTION, Palette.TEXT_MUTED);
                t.add(n, 0, r, 6, 1);
                r++;
            }
        }
        card.getChildren().add(t);
        return card;
    }

    VBox closedCard(Game g) {
        List<Closed> all = closed(g);
        VBox card = card(caption("CLOSED", "Lots the fund no longer holds - sold out, repaid at maturity, written off "
                + "in a default, or passed to the rescue book in a rescue - with what they realized against what they "
                + "cost, and the income they paid while it held them. Newest first."));
        if (all.isEmpty()) {
            card.getChildren().add(line("None yet."));
            return card;
        }
        int n = 0;
        for (Closed c : all) {
            if (n++ >= CLOSED_ROWS) break;
            card.getChildren().add(closedRow(c));
        }
        if (all.size() > CLOSED_ROWS) {
            card.getChildren().add(details("fund:closed", "every closed lot (" + all.size() + ")", open, this::redraw, () -> {
                VBox more = new VBox(2);
                for (int i = CLOSED_ROWS; i < all.size(); i++) more.getChildren().add(closedRow(all.get(i)));
                return more;
            }));
        }
        return card;
    }

    HBox closedRow(Closed c) {
        Label name = words(c.name(), Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL);
        Label when = words(c.when(), Palette.SIZE_CAPTION, Palette.TEXT_MUTED);
        VBox named = new VBox(0, name, when);
        HBox.setHgrow(named, Priority.ALWAYS);
        named.setMinWidth(0);
        VBox fig = new VBox(0, figure(c.realized().text(), Palette.SIZE_LABEL + 1, c.realized().tone()),
                figure("income " + c.income(), Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        fig.setAlignment(Pos.CENTER_RIGHT);
        HBox row = new HBox(8, named, fig);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-cursor: hand; -fx-padding: 2 0 2 0;");
        row.setOnMouseClicked(e -> openSecurity(c.key()));
        return row;
    }

    /* =====================================================================
       SEARCH - what can the fund buy, and what is it doing?
       ===================================================================== */

    /** Search's chips. */
    static final String[] FILTERS = {"All", "Shares", "Bonds", "Held"};

    /** Search's (i): the spec's D1. */
    static final String SEARCH_INFO = "Every company listed on the exchange, every business's bond still outstanding, and "
            + "the bank's preferred and its warrants while the fund holds them. Type a company's or an issuer's name, "
            + "a bond's number (\"#142\"), a coupon (\"2.8\"), the year it falls due, or a kind - bond, share, bank, "
            + "preferred. Names that start with what you typed come first, then what the fund holds, then the biggest. "
            + "Empty, it is the market: your holdings, the shares by their value, the bonds by their face.";

    void searchPage(VBox page) {
        Game g = ui.game;
        VBox results = new VBox(2);
        results.setMaxWidth(Double.MAX_VALUE);
        boolean wasTyping = box != null && box.isFocused();
        box = searchBox(query, "Search shares and bonds", 600, typed -> {
            query = typed;
            fillResults(g, results);
        }, () -> { if (ui.rootMenu != null) ui.rootMenu.requestFocus(); });
        FlowPane chips = chipStrip(FILTERS, filter, Palette.SIZE_LABEL, pick -> {
            filter = pick;
            redraw();
        });
        HBox top = new HBox(14, box, chips);
        top.setAlignment(Pos.CENTER_LEFT);
        page.getChildren().add(sectionHead("SEARCH SHARES AND BONDS", SEARCH_INFO, null));
        page.getChildren().add(top);
        fillResults(g, results);
        page.getChildren().add(results);
        if (wasTyping) {
            TextField again = box;
            javafx.application.Platform.runLater(() -> {
                again.requestFocus();
                again.positionCaret(again.getText().length());
            });
        }
    }

    /** The results, refilled in place as the box is typed in (a redraw would lose the next key). */
    void fillResults(Game g, VBox results) {
        results.getChildren().clear();
        List<Found> all = found(g, query, filter);
        if (all.isEmpty()) {
            results.getChildren().add(line(query.isBlank() ? "Nothing on the market."
                    : "Nothing matches \"" + query.trim() + "\"."));
            return;
        }
        String section = null;
        boolean anyBond = false;
        for (Found f : all) {
            if (f.section() != null && !f.section().equals(section)) {
                section = f.section();
                results.getChildren().add(groupHead(section));
            }
            anyBond |= "BOND".equals(f.tag());
            results.getChildren().add(foundRow(g, f));
        }
        if (query.isBlank() && !anyBond && ("All".equals(filter) || "Bonds".equals(filter))) {
            results.getChildren().add(groupHead(FundView.BONDS_SECTION));
            results.getChildren().add(line("No company has a bond outstanding."));
        }
    }

    HBox foundRow(Game g, Found f) {
        Label name = new Label(f.name());
        name.setWrapText(true);
        name.setMinWidth(0);
        name.setStyle(Palette.words(Palette.SIZE_BODY + 1, Palette.TEXT_BODY));
        Label sub = new Label(f.sub());
        sub.setWrapText(true);
        sub.setMinWidth(0);
        sub.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        VBox named = new VBox(0, name, sub);
        HBox lead = new HBox(8, iconSquare(iconOf(f.key()), colourOf(f.key()), 26, 14), named);
        lead.setAlignment(Pos.CENTER_LEFT);
        lead.setMinWidth(300);
        lead.setPrefWidth(330);
        lead.setMaxWidth(330);
        HBox tagBox = new HBox(tag(f.tag(), colourOf(f.key())));
        tagBox.setMinWidth(86);
        tagBox.setAlignment(Pos.CENTER_LEFT);
        VBox priceBox = figureCell(f.price(), null, null, 150);
        Label mv = words(f.move(), Palette.SIZE_LABEL, Palette.TEXT_BODY);
        mv.setMinWidth(0);
        mv.setPrefWidth(220);
        mv.setMaxWidth(220);
        Node spark = f.spark() != null && f.spark().length >= 2
                ? sparkline(f.spark(), months(g, f.spark().length), Palette.BUSINESS, 96, 22) : spacer(96);
        Label held = words(f.held(), Palette.SIZE_LABEL, Palette.TEXT_LABEL);
        held.setMinWidth(0);
        HBox.setHgrow(held, Priority.ALWAYS);
        held.setMaxWidth(Double.MAX_VALUE);
        held.setAlignment(Pos.CENTER_RIGHT);
        Label arrow = new Label("›");
        arrow.setStyle(Palette.strong(Palette.SIZE_HEADING + 2, Palette.ACCENT));
        arrow.setMinWidth(16);
        HBox row = new HBox(10, lead, tagBox, priceBox, mv, spark, held, arrow);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(44);
        String rest = "-fx-padding: 4 6 4 6; -fx-background-radius: 6; -fx-cursor: hand;"
                + " -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 0 1 0;";
        row.setStyle(rest);
        row.setOnMouseEntered(e -> row.setStyle(rest + " -fx-background-color: " + Palette.CONTROL + ";"));
        row.setOnMouseExited(e -> row.setStyle(rest));
        row.setOnMouseClicked(e -> openSecurity(f.key()));
        return row;
    }

    static Region spacer(double w) {
        Region r = new Region();
        r.setMinWidth(w);
        r.setPrefWidth(w);
        return r;
    }

    /** The last `n` months of the history's axis, for a sparkline of n points. */
    static List<Integer> months(Game g, int n) {
        List<Integer> axis = g.getHistorySave().getMonth();
        return axis.subList(Math.max(0, axis.size() - n), axis.size());
    }

    /* =====================================================================
       A SECURITY - what is it, what has it done, and what do we have in it?
       ===================================================================== */

    void securityPage(VBox page) {
        Game g = ui.game;
        String key = security;
        Security s = security(g, key);

        /* ----- the left: its head, its chart, its facts, its book, its record ----- */
        VBox left = new VBox(Palette.GAP_LOOSE);
        Label nm = new Label(s.name());
        nm.setWrapText(true);
        nm.setMinWidth(0);
        nm.setStyle(Palette.strong(Palette.SIZE_SECTION + 2, Palette.TEXT_HEAD));
        HBox titleRow = new HBox(10, iconSquare(iconOf(key), colourOf(key), 34, 18), nm, tag(s.tag(), colourOf(key)));
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label px = figure(s.price(), 28, Palette.TEXT_HEAD);
        Label note = words(s.priceNote(), Palette.SIZE_LABEL + 1, Palette.TEXT_MUTED);
        VBox headBox = new VBox(4, titleRow, px, note);
        int c = FundView.companyOf(key);
        HistorySave h = g.getHistorySave();
        if (c >= 0 && h.months() >= 2) {
            TimeChart chart = priceChart(g, c);
            double[][] series = FundView.sharePrices(g, c);
            String mv = windowMove(series[0], h.getMonth(), priceWindow);
            if (mv != null) headBox.getChildren().add(words(mv, Palette.SIZE_LABEL + 1, Palette.TEXT_BODY));
            Label title = new Label("ITS PRICE, A SHARE TODAY");
            title.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
            title.setMinWidth(Region.USE_PREF_SIZE);
            chart.lead().getChildren().setAll(title);
            chart.onSettled(this::redraw);
            left.getChildren().addAll(headBox, chart);
        } else if (key.startsWith("B:") && g.getBondMarket().bond(FundView.bondIdOf(key)) != null) {
            left.getChildren().addAll(headBox, cashFlowCard(g, g.getBondMarket().bond(FundView.bondIdOf(key))));
        } else {
            left.getChildren().add(headBox);
        }
        if (!s.facts().isEmpty()) left.getChildren().add(factsGrid(g, key, s.facts()));
        Node book = bookCard(g, key);
        if (book != null) left.getChildren().add(book);
        left.getChildren().add(recordCard(g, key));

        /* ----- the right: your position, and the ticket ----- */
        VBox right = new VBox(Palette.GAP_LOOSE, positionCard(g, key));
        if (FundView.companyOf(key) >= 0 || (key.startsWith("B:") && g.getBondMarket().bond(FundView.bondIdOf(key)) != null)) {
            right.getChildren().add(ticketCard(g, key));
        }
        if (c == Equity.BANK) right.getChildren().add(stakeCard(g));
        right.setMinWidth(320);
        right.setPrefWidth(RIGHT);
        right.setMaxWidth(RIGHT);
        HBox.setHgrow(left, Priority.ALWAYS);
        left.setMinWidth(0);
        HBox both = new HBox(TILE_GAP, left, right);
        both.setAlignment(Pos.TOP_LEFT);
        page.getChildren().add(both);
    }

    /** The move over a chart's window, in ink: "▲ 4.3% over 10Y". */
    static String windowMove(double[] series, List<Integer> axis, ChartModel window) {
        if (series == null || axis.isEmpty() || window.span() <= 0) return null;
        int i0 = ChartModel.nearest(axis, window.firstMonthShown()), i1 = ChartModel.nearest(axis, window.lastMonthShown());
        if (i0 < 0 || i1 < 0) return null;
        while (i0 < i1 && !(series[i0] > 0)) i0++;
        double a = series[i0], b = series[i1];
        if (!(a > 0) || !(b > 0) || i1 <= i0) return null;
        String m = move(b / a - 1);
        return m == null ? null : m + (wholeRange(window) ? " over " + rangeName(window.range())
                : " from " + month(axis.get(i0)) + " to " + month(axis.get(i1)));
    }

    /** A company's price a share today, its fair value, and the fund's average cost, on City History's chart. */
    TimeChart priceChart(Game g, int c) {
        HistorySave h = g.getHistorySave();
        double[][] s = FundView.sharePrices(g, c);
        List<TimeChart.Line> lines = new ArrayList<>(List.of(
                new TimeChart.Line("last", "the last trade", Palette.BUSINESS, 0, s[0],
                        v -> HistoryScreen.plotScale("share", v), v -> price(g, false, v), ""),
                new TimeChart.Line("fair", "fair value", Palette.RAMP_REST, 0, s[1],
                        v -> HistoryScreen.plotScale("share", v), v -> price(g, false, v), "")));
        boolean cost = false;
        for (double v : s[2]) if (v > 0) { cost = true; break; }
        if (cost) lines.add(new TimeChart.Line("cost", "your average cost", Palette.MONEY_LIGHT, 0, s[2],
                v -> HistoryScreen.plotScale("share", v), v -> price(g, false, v), ""));
        TimeChart chart = new TimeChart(priceWindow, priceHidden, true);
        chart.setData(h.getMonth(), lines, HistoryScreen.axisFor("share"), null, false, false, null, List.of(), List.of(),
                FundView.flagsFor(g, Equity.COMPANIES[c]), "no months recorded yet");
        chart.setSize(LEFT - 30, 220);
        chart.withoutFullScreen();
        return chart;
    }

    /** A bond's chart is what it still pays (the spec's D9): a column a calendar year, its coupons and its face, the fund's part solid. */
    VBox cashFlowCard(Game g, CorporateBond b) {
        List<FundView.YearFlow> flows = FundView.bondCashFlows(g, b);
        List<Column> cols = new ArrayList<>();
        for (FundView.YearFlow y : flows) {
            List<Segment> parts = new ArrayList<>();
            parts.add(seg(y.fundCoupons(), Palette.MONEY_LIGHT, "the fund's coupons " + d(g, y.fundCoupons())));
            if (y.fundFace() > 0) parts.add(seg(y.fundFace(), Palette.MONEY, "the fund's face " + d(g, y.fundFace())));
            double rest = (y.coupons() - y.fundCoupons()) + (y.face() - y.fundFace());
            if (rest > 0) parts.add(FinancesScreen.ghost(rest, Palette.MONEY_LIGHT, null, "the rest of the bond's " + d(g, rest)));
            cols.add(new Column(String.valueOf(y.year()), null, y.face() > 0 ? d(g, y.fundFace() + y.fundCoupons()) : null,
                    parts, false, y.face() > 0 ? "repaid" : null, Palette.MONEY,
                    y.year() + ": " + d(g, y.coupons() + y.face()) + " in all, " + d(g, y.fundCoupons() + y.fundFace())
                            + " of it the fund's", null));
        }
        VBox card = card(caption("WHAT IT STILL PAYS", "A bond's price is not kept month by month, so its chart is its "
                + "cash: a twelfth of its coupon on its face every month it has left, and the face in its last, a column "
                + "a calendar year. Solid is the fund's part; outlined, the rest of the bond's."));
        if (cols.isEmpty()) card.getChildren().add(line("Nothing: it falls due this month."));
        else card.getChildren().add(columns(cols, 0, 0, 180));
        card.getChildren().add(keyRow(keySwatch(Palette.MONEY_LIGHT, "coupons"), keySwatch(Palette.MONEY, "face"),
                FinancesScreen.swatch(Palette.MONEY_LIGHT, "the rest of the bond", true)));
        return card;
    }

    /** KEY STATS: eight tiles, four across; the market book against the cap as a bullet bar. */
    GridPane factsGrid(Game g, String key, List<String[]> facts) {
        GridPane grid = equalColumns(4, TILE_GAP);
        int c = FundView.companyOf(key);
        for (int i = 0; i < facts.size(); i++) {
            String[] f = facts.get(i);
            Label cap = new Label(f[0]);
            cap.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            cap.setWrapText(true);
            VBox tile = new VBox(3, cap, words(f[1], Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
            if ("AGAINST THE CAP".equals(f[0]) && c >= 0) {
                Equity reg = g.getEquity();
                double mkt = reg.getShares(c) > 0 ? reg.getCityMarketShares(c) / reg.getShares(c) : 0;
                tile.getChildren().add(bulletBar(mkt, TreasuryFund.OWNERSHIP_LIMIT, Palette.BUSINESS, 0, null));
            }
            CorporateBond bond = c < 0 ? g.getBondMarket().bond(FundView.bondIdOf(key)) : null;
            if ("THE FUND HOLDS".equals(f[0]) && bond != null) {
                // Its holders as the bond market's one bar: households, the bank, companies, the world, the fund.
                tile.getChildren().add(fin.holdersBar(new double[] {bond.households(), bond.bank(), bond.companiesTotal(),
                        bond.world(), bond.city()}, 6, false));
            }
            tile.getChildren().add(words(f[2], Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            tile.setStyle(FinancesScreen.cardStyle(Palette.EDGE));
            tile.setMaxWidth(Double.MAX_VALUE);
            GridPane.setFillHeight(tile, true);
            grid.add(tile, i % 4, i / 4);
        }
        return grid;
    }

    /** THE BOOK: today's depth, asks over bids, a row a price level, each with what rests there. */
    Node bookCard(Game g, String key) {
        int c = FundView.companyOf(key);
        CorporateBond b = c < 0 ? g.getBondMarket().bond(FundView.bondIdOf(key)) : null;
        OrderBook book = c >= 0 ? g.getExchange().bookOf(c) : b != null ? g.getBondMarket().bookOf(b) : null;
        if (book == null) return null;
        boolean bond = b != null;
        VBox card = card(caption("THE BOOK", "What rests on its book now, a row a price: what is asked over what is bid, "
                + "the best nearest the middle. The step withdraws every order and every participant posts again; "
                + "between steps a household short of money sells into the bids." + (c == Equity.BANK
                ? " The desk's asks in the bank's own shares are new shares, which the fund never buys." : "")));
        List<OrderBook.Level> bids = book.levels(OrderBook.Side.BUY), asks = book.levels(OrderBook.Side.SELL);
        if (bids.isEmpty() && asks.isEmpty()) {
            card.getChildren().add(line("Nobody is bidding and nobody is selling."));
            return card;
        }
        int shown = 8;
        double most = 0;
        for (int i = 0; i < Math.min(shown, bids.size()); i++) most = Math.max(most, bids.get(i).quantity());
        for (int i = 0; i < Math.min(shown, asks.size()); i++) most = Math.max(most, asks.get(i).quantity());
        List<OrderBook.Level> askRows = new ArrayList<>(asks.subList(0, Math.min(shown, asks.size())));
        java.util.Collections.reverse(askRows);
        for (OrderBook.Level l : askRows) card.getChildren().add(depthRow(g, l, false, most, bond));
        if (asks.isEmpty()) card.getChildren().add(muted("nobody is asking"));
        if (bids.isEmpty()) card.getChildren().add(muted("nobody is bidding"));
        for (int i = 0; i < Math.min(shown, bids.size()); i++) card.getChildren().add(depthRow(g, bids.get(i), true, most, bond));
        card.getChildren().add(keyRow(keySwatch(Palette.MONEY, "bids"), keySwatch(Palette.BUSINESS_LIGHT, "asks")));
        return card;
    }

    Node depthRow(Game g, OrderBook.Level l, boolean bid, double most, boolean bond) {
        double side = 220;
        double w = most > 0 ? Math.max(2, l.quantity() / most * side) : 2;
        Region bar = new Region();
        bar.setMinSize(w, 12);
        bar.setPrefSize(w, 12);
        bar.setMaxSize(w, 12);
        bar.setStyle("-fx-background-color: " + (bid ? Palette.MONEY : Palette.BUSINESS_LIGHT) + "; -fx-background-radius: 2;");
        HBox left = new HBox(), right = new HBox();
        left.setMinWidth(side);
        left.setPrefWidth(side);
        right.setMinWidth(side);
        right.setPrefWidth(side);
        left.setAlignment(Pos.CENTER_RIGHT);
        right.setAlignment(Pos.CENTER_LEFT);
        (bid ? left : right).getChildren().add(bar);
        Label p = figure(price(g, bond, l.price()), Palette.SIZE_LABEL, Palette.TEXT_HEAD);
        p.setMinWidth(120);
        p.setAlignment(Pos.CENTER);
        Label q = figure(bond ? d(g, l.quantity()) : shares(l.quantity()), Palette.SIZE_CAPTION, Palette.TEXT_MUTED);
        HBox row = new HBox(6, left, p, right, q);
        row.setAlignment(Pos.CENTER_LEFT);
        Tooltip tip = new Tooltip((bid ? "bid" : "asked") + " at " + price(g, bond, l.price()) + " · "
                + (bond ? d(g, l.quantity()) + " of face" : shares(l.quantity()) + " shares") + " · " + l.orders()
                + (l.orders() == 1 ? " order" : " orders"));
        tip.setShowDelay(Duration.millis(150));
        Tooltip.install(row, tip);
        return row;
    }

    /** This security's own record: its newest SECURITY_ROWS lines. */
    VBox recordCard(Game g, String key) {
        VBox card = card(caption("ITS RECORD", "Every trade in it, by the rule and by your orders, and what happened to it - a "
                + "split, a maturity, a write-down - newest first."));
        List<Act> rows = acts(g, "All", key, 0);
        if (rows.isEmpty()) card.getChildren().add(line("Nothing yet."));
        int n = 0;
        for (Act a : rows) {
            if (n++ >= SECURITY_ROWS) break;
            card.getChildren().add(actRow(g, a, false));
        }
        if (rows.size() > SECURITY_ROWS) card.getChildren().add(doorPill("All of it on Activity", Icons.REPORTS, Palette.MONEY, () -> go("Activity")));
        return card;
    }

    /** YOUR POSITION. */
    VBox positionCard(Game g, String key) {
        VBox card = card(caption("YOUR POSITION", "What the fund holds of it, what that cost by the average-cost method, what "
                + "it is worth at the mark, and what it has made: unrealized on what it still holds, realized on what it "
                + "sold, and the income it was paid. The bank's shares can be in two books: the market book, which the rule "
                + "trades, and the rescue book, which the city took in a rescue and the rule never sells."));
        for (PositionLine l : position(g, key)) {
            if (l.value().isEmpty()) {
                Label head = new Label(l.label());
                head.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.TEXT_MUTED) + " -fx-padding: 4 0 0 0;");
                card.getChildren().add(head);
                continue;
            }
            card.getChildren().add(narrowLine(l.label(), l.value(), l.tone()));
            if (l.note() != null) card.getChildren().add(muted(l.note()));
        }
        return card;
    }

    /**
     * A line in the security page's right column, 412 at the 1,389 window:
     * its words at the left and its figure at the right, both wrapping rather
     * than cut - FinancesScreen.cardLine()'s figure is never cut, and a long
     * quote ("193.70 for D$19.7M, D$101,771 on average") would push past the
     * card.
     */
    static HBox narrowLine(String label, String value, String tone) {
        Label what = words(label, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL);
        what.setMinWidth(96);
        what.setPrefWidth(150);
        Label v = new Label(value);
        v.setStyle(BuildScreen.figureAt(Palette.SIZE_LABEL + 1, tone == null ? Palette.TEXT_HEAD : tone));
        v.setWrapText(true);
        v.setMinWidth(0);
        v.setMaxWidth(Double.MAX_VALUE);
        v.setAlignment(Pos.TOP_RIGHT);
        v.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);
        HBox.setHgrow(v, Priority.ALWAYS);
        HBox row = new HBox(Palette.GAP, what, v);
        row.setAlignment(Pos.TOP_LEFT);
        return row;
    }

    /** The city's stake in its bank as a ring, with a door to the Bank tab's owners (the rescue book's card's, here too). */
    VBox stakeCard(Game g) {
        double stake = g.cityStakeInBank();
        HBox ring = new HBox(10, ring(stake, Palette.MONEY, 56, 6, FinancesScreen.pc(stake), 11),
                words("the city's stake in its bank, both books", Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        ring.setAlignment(Pos.CENTER_LEFT);
        ring.setStyle("-fx-cursor: hand;");
        ring.setOnMouseClicked(e -> ui.bankScreen.openPage("Capital & owners"));
        return card(caption("THE CITY'S STAKE", null), ring, doorPill("The bank's owners", Icons.BANK, Palette.MONEY, () -> ui.bankScreen.openPage("Capital & owners")));
    }

    /* ------------------------------ the ticket ------------------------------ */

    /** A new security's ticket: buy, by amount, nothing chosen, at fair value. */
    void resetTicket(String key) {
        ticketKey = key;
        buy = true;
        byAmount = true;
        amount = 0;
        priceKind = FAIR;
        steps = 0;
        review = false;
    }

    /** THE ORDER TICKET's (i). */
    static final String TICKET_INFO = "An order goes on the book at the next month's step, after every other participant "
            + "has posted - the rule first - and is good for that month: it takes what is offered at or better than its "
            + "price, at the offers' own prices, and what is left waits at its price until the step after withdraws it. "
            + "Fair value is what the rule asks at, and a bond's value what it bids; a share it bids at the desk's ask, "
            + pct1(TreasuryFund.RULE_PREMIUM).replace(".0", "") + " over fair. You may name another. A buy's money is held for it from the moment you "
            + "place it until it fills or lapses, and the rule bids with the rest. A buy goes no further than the room "
            + "under the cap the rule keeps of a company, counting what the fund holds of it and your other buys on it, on "
            + "the book or waiting, as if all of them filled; the rule's own bid makes way for yours, from the moment you "
            + "place it. Past the cap the rule would sell the excess back at fair value. "
            + "The quote reads today's book, which the step withdraws and posts afresh, so it says what stands now, not "
            + "what will fill.";

    VBox ticketCard(Game g, String key) {
        if (!key.equals(ticketKey)) resetTicket(key);
        boolean bond = key.startsWith("B:");
        Ticket t = ticket(g, key, buy, byAmount, amount, priceKind, steps, review);
        VBox card = card(caption("THE ORDER TICKET", TICKET_INFO));

        /* ----- buy or sell; by amount or by quantity ----- */
        FlowPane side = chipStrip(new String[] {"Buy", "Sell"}, buy ? "Buy" : "Sell", Palette.SIZE_LABEL + 1, pick -> {
            boolean b = "Buy".equals(pick);
            if (b != buy) { buy = b; byAmount = b; amount = 0; review = false; }
            redraw();
        });
        FlowPane mode = chipStrip(new String[] {"by amount", "by quantity"}, byAmount ? "by amount" : "by quantity",
                Palette.SIZE_LABEL, pick -> {
                    boolean a = "by amount".equals(pick);
                    if (a != byAmount && t.quote() != null) {
                        // The toggle converts at the order's price.
                        amount = a ? t.quote().units() * t.quote().price() : t.quote().units();
                    }
                    byAmount = a;
                    review = false;
                    redraw();
                });
        card.getChildren().addAll(side, mode);

        /* ----- the figure and its steps ----- */
        String[] figs = ticketFigure(g, t, bond, byAmount, amount);
        Label figLabel = figure(figs[0], Palette.SIZE_TITLE, amount > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT);
        String other = figs[1];
        card.getChildren().add(figLabel);
        if (other != null) card.getChildren().add(muted(other));
        FlowPane up = new FlowPane(4, 4), down = new FlowPane(4, 4);
        double held = t.quote() == null ? 0 : t.quote().unitsHeld();
        if (byAmount || bond) {
            for (double s : MONEY_STEPS) {
                up.getChildren().add(stepChip("+" + d(g, s), () -> { amount += s; review = false; redraw(); }, false));
                down.getChildren().add(stepChip("−" + d(g, s), () -> { amount = Math.max(0, amount - s); review = false; redraw(); }, true));
            }
        } else {
            for (double s : new double[] {1, 10, 100, 1_000}) {
                up.getChildren().add(stepChip("+" + shares(s).replace(".00", ""), () -> { amount += s; review = false; redraw(); }, false));
                down.getChildren().add(stepChip("−" + shares(s).replace(".00", ""), () -> { amount = Math.max(0, amount - s); review = false; redraw(); }, true));
            }
        }
        if (!buy && held > 0) {
            for (double part : new double[] {.25, .5, 1}) {
                String w = part == 1 ? "all" : part == .5 ? "½" : "¼";
                up.getChildren().add(stepChip(w, () -> {
                    amount = byAmount ? held * part * (t.quote() == null ? 0 : t.quote().price()) : held * part;
                    review = false;
                    redraw();
                }, false));
            }
        }
        if (buy && g.fundCashFree() > 0) {
            up.getChildren().add(stepChip("all its free cash", () -> {
                byAmount = true;
                amount = g.fundCashFree();
                review = false;
                redraw();
            }, false));
        }
        down.getChildren().add(stepChip("clear", () -> { amount = 0; review = false; redraw(); }, true));
        card.getChildren().addAll(up, down);

        /* ----- the price ----- */
        card.getChildren().add(priceChips(g, key, bond));

        /* ----- the quote ----- */
        for (PositionLine l : t.lines()) {
            card.getChildren().add(narrowLine(l.label(), l.value(), l.tone()));
            if (l.note() != null) card.getChildren().add(muted(l.note()));
        }

        /* ----- the action ----- */
        ActionButton go = actionButton(Icons.EXCHANGE, Palette.MONEY, ACTION_TALL, t.press(), () -> press(g, key, t));
        go.setMaxWidth(Double.MAX_VALUE);
        card.getChildren().add(go);
        if (review) card.getChildren().add(door("Change it", Palette.ACCENT, () -> { review = false; redraw(); }));
        if (t.held() != null) {
            double needs = t.quote() == null ? 0 : Math.max(0, t.quote().money() - g.fundCashFree());
            card.getChildren().add(doorPill("Pay in", Icons.COIN, Palette.MONEY, () -> {
                fin.fundAsk = needs > 0 ? needs : fin.fundAsk;
                go("Rules & cash");
            }));
        }

        /* ----- this security's orders: waiting, and on the book ----- */
        VBox waiting = waitingFor(g, key);
        if (waiting != null) card.getChildren().add(waiting);
        return card;
    }

    /** The price chips: fair value (the rule's ask; a bond's value, the rule's), the best bid, the best ask, the last trade, and a step of 1% of fair either way. */
    Node priceChips(Game g, String key, boolean bond) {
        Map<String, String> kinds = priceChoices(g, key);
        List<String> names = new ArrayList<>(kinds.keySet());
        String current = names.get(0);
        for (Map.Entry<String, String> e : kinds.entrySet()) if (e.getValue().equals(priceKind)) current = e.getKey();
        FlowPane chips = chipStrip(names.toArray(new String[0]), OWN.equals(priceKind) ? null : current, Palette.SIZE_CAPTION, pick -> {
            priceKind = kinds.getOrDefault(pick, FAIR);
            steps = 0;
            review = false;
            redraw();
        });
        HBox own = new HBox(6, stepper("−1%", 40, () -> { priceKind = OWN; steps--; review = false; redraw(); }),
                figure(OWN.equals(priceKind) ? signedPct(steps / 100.0) + " on fair" : "your own price",
                        Palette.SIZE_LABEL, OWN.equals(priceKind) ? Palette.TEXT_HEAD : Palette.TEXT_MUTED),
                stepper("+1%", 40, () -> { priceKind = OWN; steps++; review = false; redraw(); }));
        own.setAlignment(Pos.CENTER_LEFT);
        Label cap = new Label("PRICE");
        cap.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        return new VBox(4, cap, chips, own);
    }

    /** The price chips' words and the kind each picks: fair value (the rule's ask; a bond's value, the rule's price both ways) first, then the best bid, the best ask and the last trade where the book has them. */
    static Map<String, String> priceChoices(Game g, String key) {
        int c = FundView.companyOf(key);
        boolean bond = c < 0;
        CorporateBond b = bond ? g.getBondMarket().bond(FundView.bondIdOf(key)) : null;
        double fair = c >= 0 ? g.getExchange().fair(c) : b != null ? g.getBondMarket().modelPrice(b, g.getMonth()) : Double.NaN;
        OrderBook book = c >= 0 ? g.getExchange().bookOf(c) : b != null ? g.getBondMarket().bookOf(b) : null;
        Map<String, String> kinds = new LinkedHashMap<>();
        kinds.put((bond ? "Value " : "Fair value ") + price(g, bond, fair) + (bond ? " (the rule's)" : " (the rule's ask)"), FAIR);
        if (book != null && book.bestBid() > 0) kinds.put("Best bid " + price(g, bond, book.bestBid()), BID);
        if (book != null && book.bestAsk() > 0) kinds.put("Best ask " + price(g, bond, book.bestAsk()), ASK);
        if (book != null && book.lastPrice() > 0) kinds.put("Last " + price(g, bond, book.lastPrice()), LAST);
        return kinds;
    }

    /** The ticket's figure in words, and the other unit it comes to: "D$1.0M" and "≈ 9.87 shares". */
    static String[] ticketFigure(Game g, Ticket t, boolean bond, boolean byAmount, double amount) {
        String fig = byAmount ? d(g, amount) : bond ? d(g, amount) + " of face" : shares(amount) + " shares";
        String other = t.quote() == null || !(amount > 0) ? null
                : byAmount ? "≈ " + (bond ? d(g, t.quote().units()) + " of face" : shares(t.quote().units()) + " shares")
                : "≈ " + d(g, t.quote().money());
        return new String[] {fig, other};
    }

    /** The action button's press: review, then place. */
    void press(Game g, String key, Ticket t) {
        Press p = t.press();
        if (p.look() == Look.CHOOSE) {
            // Nothing chosen: the first step of the amount.
            amount = byAmount ? MONEY_STEPS[0] : 1;
            redraw();
            return;
        }
        if (p.look() != Look.GO || t.quote() == null) return;
        if (!review) { review = true; redraw(); return; }
        place(g, key, buy, t);
        amount = 0;
        review = false;
        redraw();
    }

    /** Places the ticket's order through Game's own calls - what the reviewed press does (the probe's path too): a buy of the quote's money, a sale of its units, at the ticket's price (0 is fair value). */
    static void place(Game g, String key, boolean buy, Ticket t) {
        if (t.quote() == null) return;
        double limit = t.limit();
        int c = FundView.companyOf(key);
        if (c >= 0) {
            if (buy) g.fundBuyShares(c, t.quote().money(), limit);
            else g.fundSellShares(c, t.quote().units(), limit);
        } else {
            int id = FundView.bondIdOf(key);
            if (buy) g.fundBuyBond(id, t.quote().money(), limit);
            else g.fundSellBond(id, t.quote().units(), limit);
        }
    }

    /** The player's orders for one security - or all, with `key` null: waiting for the step, each with Cancel, and on the book, filled so far. */
    VBox waitingFor(Game g, String key) {
        TreasuryFund f = g.getFund();
        VBox box = new VBox(4);
        List<TreasuryFund.HandOrder> waiting = f.getHandOrders();
        boolean any = false;
        for (int i = 0; i < waiting.size(); i++) {
            TreasuryFund.HandOrder o = waiting.get(i);
            String k = o.bond() ? FundLedger.bondKey(o.bondId()) : FundLedger.shareKey(Equity.COMPANIES[o.company()]);
            if (key != null && !FundView.sameLot(key, k)) continue;
            if (!any) { box.getChildren().add(captionLine("WAITING FOR THE NEXT STEP")); any = true; }
            int at = i;
            box.getChildren().add(orderRow(orderWords(g, o, false), "Cancel", () -> {
                g.fundCancelOrder(at);
                redraw();
            }));
        }
        boolean posted = false;
        for (TreasuryFund.HandOrder o : f.getPosted()) {
            String k = o.bond() ? FundLedger.bondKey(o.bondId()) : FundLedger.shareKey(Equity.COMPANIES[o.company()]);
            if (key != null && !FundView.sameLot(key, k)) continue;
            if (!posted) { box.getChildren().add(captionLine("ON THE BOOK UNTIL THE NEXT STEP")); posted = true; }
            box.getChildren().add(orderRow(orderWords(g, o, true), null, null));
        }
        return any || posted ? box : null;
    }

    static Label captionLine(String text) {
        Label l = new Label(text);
        l.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.TEXT_MUTED) + " -fx-padding: 6 0 0 0;");
        return l;
    }

    HBox orderRow(String words, String act, Runnable go) {
        Label w = words(words, Palette.SIZE_LABEL + 1, Palette.TEXT_BODY);
        HBox.setHgrow(w, Priority.ALWAYS);
        w.setMaxWidth(Double.MAX_VALUE);
        HBox row = new HBox(8, w);
        if (act != null) row.getChildren().add(door(act, Palette.ACCENT, go));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** An order of the player's, in words: "Buy D$4.0M of Construction at fair value", or posted, "...: 0 of 39.47 filled". */
    static String orderWords(Game g, TreasuryFund.HandOrder o, boolean posted) {
        String name = o.bond() ? securityName(g, FundLedger.bondKey(o.bondId())) : Equity.COMPANIES[o.company()];
        String size = o.buy() ? d(g, o.amount()) + " of " : o.bond() ? d(g, o.amount()) + " face of " : shares(o.amount()) + " shares of ";
        String at = o.limit() > 0 ? " at " + price(g, o.bond(), o.limit()) : o.bond() ? " at its value" : " at fair value";
        String words = (o.buy() ? "Buy " : "Sell ") + size + name + (posted ? " at " + price(g, o.bond(), o.price()) : at);
        if (posted) {
            words += ": " + (o.bond() ? d(g, o.filled()) : shares(o.filled())) + " of " + (o.bond() ? d(g, o.units()) : shares(o.units()))
                    + " filled" + (o.filled() > 0 ? " for " + d(g, o.spent()) : "");
        } else {
            words += " · placed " + month(o.month());
        }
        return words;
    }

    /* =====================================================================
       ACTIVITY - what has the fund done, and what was done to it?
       ===================================================================== */

    /** Activity's chips: every row, or one group of them (FundView.groupOf()). */
    static final String[] GROUPS = {"All", FundView.TRADES, FundView.INCOME, FundView.MONEY, FundView.EVENTS};

    void activityPage(VBox page) {
        Game g = ui.game;
        VBox waiting = waitingFor(g, null);
        page.getChildren().add(card(caption("YOUR ORDERS", "Waiting for the next step - each can be cancelled until then - "
                        + "and on the book from the last step until the next, with what has filled so far."),
                waiting != null ? waiting : line("None waiting and none on the book.")));
        FlowPane chips = chipStrip(GROUPS, group, Palette.SIZE_LABEL, pick -> { group = pick; activityMonths = ACTIVITY_MONTHS; redraw(); });
        page.getChildren().add(sectionHead("WHAT IT DID", "Every trade the rule and your orders made, what the fund was paid, "
                + "what went in and out of it, and what happened to it, newest first. A month's trades by the rule in "
                + "one market are one line: open it for the lots. Before this build kept a record, the rescues and the "
                + "fund's and the bank's decisions fill in, from the decision log.", chips));
        List<Act> rows = acts(g, group, null, activityMonths);
        VBox list = new VBox(0);
        int lastMonth = Integer.MIN_VALUE;
        for (Act a : rows) {
            if (a.month() != lastMonth) {
                lastMonth = a.month();
                Label m = new Label(CityCalendar.format(a.month()));
                m.setStyle(Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_LABEL) + " -fx-padding: 8 0 2 6;");
                list.getChildren().add(m);
            }
            list.getChildren().add(actRow(g, a, true));
            String k = "fund:act:" + a.month() + ":" + a.what();
            if (!a.inside().isEmpty() && open.contains(k)) {
                for (Act inner : a.inside()) {
                    HBox r = actRow(g, inner, false);
                    r.setStyle(r.getStyle() + " -fx-padding: 2 6 2 40;");
                    list.getChildren().add(r);
                }
            }
        }
        if (rows.isEmpty()) list.getChildren().add(line("Nothing in the last " + activityMonths + " months."));
        page.getChildren().add(card(list));
        List<Act> older = acts(g, group, null, 0);
        if (older.size() > rows.size()) {
            page.getChildren().add(door("Show older", Palette.ACCENT, () -> { activityMonths += ACTIVITY_MONTHS; redraw(); }));
        }
        FundLedger ledger = g.getFund().getLedger();
        if (ledger.getDropped() > 0) {
            page.getChildren().add(muted("The record keeps its newest " + FundLedger.ACTIVITY_ROWS + " lines; "
                    + ledger.getDropped() + " older ones have gone."));
        }
    }

    /** One line of the record: what, by whom, units, money, realized, and "›" to its security; a grouped line opens. */
    HBox actRow(Game g, Act a, boolean dated) {
        Label what = words(a.what(), Palette.SIZE_LABEL + 1, Palette.TEXT_BODY);
        HBox.setHgrow(what, Priority.ALWAYS);
        what.setMaxWidth(Double.MAX_VALUE);
        if (!a.inside().isEmpty()) {
            String k = "fund:act:" + a.month() + ":" + a.what();
            what.setText((open.contains(k) ? "▾ " : "▸ ") + a.what());
            what.setStyle(what.getStyle() + " -fx-cursor: hand;");
            what.setOnMouseClicked(e -> { if (!open.remove(k)) open.add(k); redraw(); });
        }
        Label by = words(a.by(), Palette.SIZE_CAPTION, Palette.TEXT_MUTED);
        by.setMinWidth(80);
        by.setPrefWidth(80);
        HBox row = new HBox(8, what, by, figureCell(a.units(), Palette.TEXT_LABEL, null, 110),
                figureCell(a.money(), null, null, 110),
                figureCell(a.realized() == null ? "" : a.realized().text(), a.realized() == null ? null : a.realized().tone(), null, 150));
        if (a.key() != null) {
            Label arrow = new Label("›");
            arrow.setStyle(Palette.strong(Palette.SIZE_HEADING + 2, Palette.ACCENT) + " -fx-cursor: hand;");
            arrow.setMinWidth(16);
            String key = a.key();
            arrow.setOnMouseClicked(e -> openSecurity(key));
            row.getChildren().add(arrow);
        } else {
            row.getChildren().add(spacer(16));
        }
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 2 6 2 6; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 0 1 0;");
        return row;
    }

    /* =====================================================================
       RULES & CASH - how does it run, and how do I move money?
       (THE DIAL, TO THE TREASURY and THE RESCUE BOOK are FinancesScreen's
       cards of 0.7.32, moved here whole; THE WITHDRAWAL, first and full
       width, is 0.7.48's dial - Jerus: "the city fund, you should be able to
       click how much to withdraw automatically, even 0 or 10% a month".)
       ===================================================================== */

    void rulesPage(VBox page) {
        Game g = ui.game;
        page.getChildren().add(withdrawalCard(g));
        GridPane three = equalColumns(3, TILE_GAP);
        VBox dial = dialCard(g), transfer = transferCard(g), rescue = rescueBookCard(g);
        for (VBox c : List.of(dial, transfer, rescue)) GridPane.setFillHeight(c, true);
        three.add(dial, 0, 0);
        three.add(transfer, 1, 0);
        three.add(rescue, 2, 0);
        page.getChildren().add(three);
        page.getChildren().add(pair(moveCard(g), ruleCard(g)));
        VBox waiting = waitingFor(g, null);
        page.getChildren().add(card(caption("YOUR ORDERS", null), waiting != null ? waiting : line("None waiting and none on the book.")));
    }

    /** The dial's (i). */
    static String dialInfo() {
        return String.format("Once a year, when December closes, the fund takes the dial's share of the year's surplus - "
                + "before the rollover nets it, and the surplus is used once between them. A year in deficit saves "
                + "nothing. Past 100%% the rest comes from the treasury's cash, never taking it under a month of its "
                + "spending. Up to %.0f%%.", TreasuryFund.MAX_DIAL * 100);
    }

    /** THE DIAL: its share of the year's surplus, the steps, and what it reads against. */
    VBox dialCard(Game g) {
        TreasuryFund fund = g.getFund();
        VBox card = card(caption("THE DIAL", dialInfo()),
                figure(String.format("%.0f%% of the year's surplus", fund.getDial() * 100), Palette.SIZE_LEAD,
                        fund.getDial() > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT));
        FlowPane steps = new FlowPane(6, 6);
        for (double step : new double[] {-.5, -.1, .1, .5}) {
            steps.getChildren().add(stepChip(String.format("%+.0f points", step * 100), () -> {
                g.setFundDial(Math.max(0, Math.min(TreasuryFund.MAX_DIAL, fund.getDial() + step)));
                redraw();
            }, step < 0));
        }
        card.getChildren().add(steps);
        card.getChildren().add(line("Once a year, in December, the fund takes this share of the year's surplus."));
        card.getChildren().add(cardLine("This year's surplus so far", d(g, g.surplusThisYearSoFar()),
                g.surplusThisYearSoFar() < 0 ? Palette.WARN : null));
        card.getChildren().add(cardLine("...kept for the fund from the rollover", d(g, g.fundReservation()), null));
        card.getChildren().add(cardLine("A month of the treasury's spending, the floor", d(g, g.monthOfSpending()), null));
        card.getChildren().add(cardLine("The last pay-in", fund.getLastPayInMonth() >= 0
                ? CityCalendar.formatShort(fund.getLastPayInMonth()) : "never", null));
        card.getChildren().add(details("fund:life", "what it has been paid", open, this::redraw, () -> FinancesScreen.column(
                statementLine("The last pay-in", fund.getLastPayInMonth() >= 0
                        ? String.format("%s: %s from the surplus, %s from cash", CityCalendar.format(fund.getLastPayInMonth()),
                                d(g, fund.getLastPayInFromSurplus()), d(g, fund.getLastPayInFromCash())) : "never"),
                statementLine("Paid in by the dial, over its life", d(g, fund.getPaidInFromSurplus()) + " from the surplus · "
                        + d(g, fund.getPaidInFromCash()) + " from cash"),
                statementLine("...and by hand, in and out", d(g, fund.getHandPaidIn()) + " in  ·  "
                        + d(g, fund.getHandDrawnOut()) + " out"))));
        return card;
    }

    /* ----- THE WITHDRAWAL (0.7.48, C2) ----- */

    /** The withdrawal's ladder: the card runs the page's width, the dial at the left and what it would do beside it. */
    static final double WITHDRAWAL_LADDER = 520;

    /** The withdrawal's (i). */
    static String withdrawalInfo() {
        return String.format("What the fund pays the budget every month, as a share of everything it holds - its cash and "
                + "both books at their marks - as the revenue line \"Transfer from the fund\". A step is %s a month, a "
                + "twelfth of the %.0f%% a year Norway's fiscal rule takes, which is the default: about what the fund is "
                + "expected to earn, so it keeps its worth. At nothing it pays nothing and keeps all it earns. At or under the "
                + "default it pays from its cash only, and what the cash cannot cover is not paid. Over it the dial spends the "
                + "fund: what its cash cannot cover is sold from its market book at the step, shares and bonds pro rata - "
                + "the shares at the desk's bid, the bonds at their value; never the rescue book - and paid at the next "
                + "month's top, and the rule buys nothing "
                + "meanwhile; what the sale does not raise is not paid. Up to %s a month.",
                pct2(TreasuryFund.WITHDRAWAL_STEP), TreasuryFund.TRANSFER_RATE * 100,
                pct2(TreasuryFund.MAX_WITHDRAWAL_STEPS * TreasuryFund.WITHDRAWAL_STEP));
    }

    /** The withdrawal's caveat, under what it would do. */
    static final String WITHDRAWAL_CAVEAT = "next month's on the fund as it stands - its prices and what the book takes will move it";
    /** ...and the sentence behind it, its (i). */
    static final String WITHDRAWAL_CAVEAT_INFO = "Next month's withdrawal is struck on what the fund is worth at the top of "
            + "next month; this reads it on the fund as it stands. Its prices move by then, its dividends and coupons come "
            + "in, and a sale fills only as far as somebody bids for it - what it cannot sell is not paid.";

    /**
     * The withdrawal's status line (pure: the probe reads it): nothing at 0,
     * Norway's rule at the default, and otherwise the year it makes - twelve
     * times the rate, the rule's own arithmetic - and how soon, earning
     * nothing, it would halve: ln 0.5 / ln(1 - the rate) months.
     */
    static String withdrawalWords(double rate) {
        int steps = TreasuryFund.stepsFor(rate);
        if (steps <= 0) return "nothing to the treasury: it keeps all it earns";
        double share = steps * TreasuryFund.WITHDRAWAL_STEP;
        String year = String.format("%.0f%% a year", share * TreasuryFund.YEAR_MONTHS * 100);
        if (steps == TreasuryFund.DEFAULT_WITHDRAWAL_STEPS) return year + " - Norway's rule: what it is expected to earn";
        double months = Math.log(.5) / Math.log(1 - share);
        String halve = months < 2 * TreasuryFund.YEAR_MONTHS ? String.format("%.1f months", months)
                : String.format("%.1f years", months / TreasuryFund.YEAR_MONTHS);
        return year + " (12 × the rate) · earning nothing it would halve in " + halve
                + (steps > TreasuryFund.DEFAULT_WITHDRAWAL_STEPS ? " · past its cash it sells to pay" : "");
    }

    /** The withdrawal in a few words, as the page's head reads it: "0.25% of its worth a month". */
    static String withdrawalShare(double rate) {
        return rate > 0 ? pct2(rate) + " of its worth a month" : "the withdrawal at nothing";
    }

    /**
     * What the withdrawal would do at any value of its thumb (pure: the
     * probe reads them), each PolicyPreview.fundWithdrawalAt() against the
     * dial in force: next month's to the treasury, what its cash pays of it,
     * the rest - sold from its market book over the default, not paid at or
     * under it - and what it would be worth in a year earning nothing. Money
     * colours only: a fund spent and a fund kept are both a policy.
     */
    static java.util.function.DoubleFunction<List<Pieces.Effect>> withdrawalEffects(Game g) {
        PolicyPreview.Withdrawal now = PolicyPreview.fundWithdrawalAt(g, g.getFundWithdrawal());
        java.util.function.DoubleFunction<String> money = v -> d(g, v), move = v -> signed(g, v);
        return v -> {
            PolicyPreview.Withdrawal then = PolicyPreview.fundWithdrawalAt(g, v);
            boolean sells = TreasuryFund.stepsFor(v) > TreasuryFund.DEFAULT_WITHDRAWAL_STEPS;
            return List.of(
                    Pieces.Effect.of("To the treasury next month", g.fundTransferDue(), then.due(), money).delta(move),
                    Pieces.Effect.of("...from its cash", now.fromCash(), then.fromCash(), money).delta(move),
                    sells ? Pieces.Effect.of("...sold from its market book", now.toSell(), then.toSell(), money).delta(move)
                            : Pieces.Effect.of("...not paid, for want of cash", now.unpaid(), then.unpaid(), money).delta(move),
                    Pieces.Effect.of("Worth in a year, earning nothing", now.yearOn(), then.yearOn(), money).delta(move));
        };
    }

    /** THE WITHDRAWAL (0.7.48, C2): the dial on a Levers card, as the fare's on Infrastructure - its reading, its status, the ladder, what it would do, and its Apply once staged. */
    VBox withdrawalCard(Game g) {
        double rate = g.getFundWithdrawal();
        double want = ui.policyScreen.staged("fundWithdrawal", rate);
        Ladder ladder = ui.policyScreen.ownLadder("fundWithdrawal", rate, 0,
                TreasuryFund.MAX_WITHDRAWAL_STEPS * TreasuryFund.WITHDRAWAL_STEP, TreasuryFund.WITHDRAWAL_STEP,
                v -> pct2(v) + " a month");
        return Levers.dialCard(new Levers.DialCard(Icons.SAFE, Palette.MONEY, "THE WITHDRAWAL", withdrawalInfo(),
                        pct2(rate) + " a month", withdrawalWords(rate), null, ladder, want, withdrawalEffects(g),
                        WITHDRAWAL_CAVEAT, WITHDRAWAL_CAVEAT_INFO,
                        ui.policyScreen.applyFoot("fundWithdrawal", want <= 0 ? "Withdraw nothing"
                                : "Withdraw " + pct2(want) + " a month", () -> g.setFundWithdrawal(want))),
                WITHDRAWAL_LADDER, false);
    }

    /** The transfer's (i): the withdrawal dial in force, read. */
    static String transferInfo(TreasuryFund fund) {
        return String.format("The withdrawal's share of everything the fund holds, every month - %s a month now, %.0f%% a "
                + "year - as the budget's revenue line \"Transfer from the fund\". At or under Norway's rule, %.0f%% a year "
                + "and the default, it is paid from the fund's cash only, and what the cash cannot cover is not paid; over "
                + "it, what the cash cannot cover is sold from its market book and paid the month after. THE WITHDRAWAL, "
                + "above, sets it.", pct2(fund.getWithdrawal()), fund.getWithdrawal() * TreasuryFund.YEAR_MONTHS * 100,
                TreasuryFund.TRANSFER_RATE * 100);
    }

    /** TO THE TREASURY: last month's, paid and not, this year's, next month's, and what came in. */
    VBox transferCard(Game g) {
        TreasuryFund fund = g.getFund();
        VBox card = card(caption("TO THE TREASURY", transferInfo(fund)));
        card.getChildren().add(cardLine("Last month's, on what it was worth", d(g, fund.getTransferDue()), null));
        card.getChildren().add(cardLine("...paid from its cash", d(g, fund.getTransferPaid()), null));
        // Over the default the short is what the step sells for, paid at the next top (0.7.48): its own words, no warning.
        if (fund.getToRaise() > 0) {
            card.getChildren().add(cardLine("...sold for, to pay next month", d(g, fund.getToRaise()), null));
        } else {
            card.getChildren().add(cardLine("...not paid, for want of cash", d(g, fund.getTransferShort()),
                    fund.getTransferShort() > 0 ? Palette.WARN : Palette.TEXT_SPENT));
        }
        if (fund.getTransferPaidLate() > 0) {
            card.getChildren().add(cardLine("...and the month before's, paid from what it sold", d(g, fund.getTransferPaidLate()), null));
        }
        // ...and the year's short without what the step is selling for, the line above's, which the next month
        // pays: "not paid" was counting it (0.7.102, Jerus's B - the fund summary neutral while the dial sells).
        double notPaidThisYear = Math.max(0, fund.getTransferShortThisYear() - Math.max(0, fund.getToRaise()));
        card.getChildren().add(cardLine("This year so far", d(g, fund.getTransfersThisYear())
                + (notPaidThisYear > 0 ? " · " + d(g, notPaidThisYear) + " not paid" : ""), null));
        card.getChildren().add(cardLine("Next month's, on what it is worth now", d(g, g.fundTransferDue()), null));
        card.getChildren().add(caption("WHAT CAME IN LAST MONTH", null));
        card.getChildren().add(cardLine("Dividends", d(g, fund.getMonthDividends()), null));
        card.getChildren().add(cardLine("Coupons", d(g, fund.getMonthCoupons()), null));
        card.getChildren().add(cardLine("Bonds repaid", d(g, fund.getMonthPrincipal()), null));
        card.getChildren().add(cardLine("Bought and sold", d(g, fund.getMonthBought()) + " bought · "
                + d(g, fund.getMonthSold()) + " sold", null));
        return card;
    }

    /** The rescue book's (i). */
    static final String RESCUE_BOOK_INFO = "What the city holds from rescuing its bank: the shares it took when it "
            + "resolved it, which the rule never sells, and the preferred and warrants it bought when the bank was "
            + "under its minimum. By hand, any of it the exchange trades may be sold.";

    /** THE RESCUE BOOK: the city's stake in its bank as a ring, the book's lines, and the preferred's terms (0.7.39). */
    VBox rescueBookCard(Game g) {
        Bank bank = g.getBank();
        double stake = g.cityStakeInBank();
        HBox ring = new HBox(10, ring(stake, Palette.MONEY, 56, 6, FinancesScreen.pc(stake), 11),
                words("the city's stake in its bank", Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        ring.setAlignment(Pos.CENTER_LEFT);
        VBox card = card(caption("THE RESCUE BOOK", RESCUE_BOOK_INFO), ring,
                cardLine("Shares it took in a resolution, or from its warrants", d(g, g.fundRescueSharesValue()), null),
                cardLine("The bank's preferred, at par", d(g, g.fundPreferredValue()), null),
                cardLine("...its dividends owed and unpaid", d(g, bank.getPreferredArrears()),
                        bank.getPreferredArrears() > 0 ? Palette.WARN : null),
                cardLine("The warrants on its shares", d(g, g.fundWarrantsValue()), null));
        for (String t : rescueTerms(g)) card.getChildren().add(muted(t));
        return card;
    }

    /** The preferred's terms and its warrants', a line each (0.7.39). */
    static List<String> rescueTerms(Game g) {
        List<String> out = new ArrayList<>();
        int now = g.getMonth();
        for (Bank.Preferred p : g.getBank().getPreferred()) {
            if (p.par() > 0) {
                out.add(String.format("%s at par, bought %s: %.0f%% a year now, redeemed at its third anniversary, %s",
                        d(g, p.par()), month(p.issued()), p.rate(now) * 100, month(p.issued() + Bank.PREFERRED_REDEEM_MONTHS)));
            }
            if (p.warrantsOut()) {
                out.add(String.format("Warrants on %s shares at %s, expiring %s", shares(p.warrantShares()),
                        price(g, false, p.strike()), month(p.warrantsExpire())));
            }
        }
        return out;
    }

    /** PAY IN, DRAW OUT: the amount and its steps (FinancesScreen.fundAsk), and the two moves. */
    VBox moveCard(Game g) {
        TreasuryFund fund = g.getFund();
        VBox card = card(caption("PAY IN, DRAW OUT", "A transfer between the treasury and its own fund: not revenue and "
                        + "not spending, so the budget and its surplus do not see it. The treasury journal carries it, as "
                        + "\"Paid into the fund\" or \"Drawn from the fund\". Only the fund's free cash can be drawn - "
                        + "not what your orders hold - and only the treasury's paid in; sell first to draw more."),
                cardLine("The treasury holds", d(g, g.getCash()), null),
                cardLine("The fund's cash", d(g, fund.getCash()), null),
                cardLine("...of it free, not held for your orders", d(g, g.fundCashFree()), null));
        card.getChildren().add(figure(fin.fundAsk <= 0 ? "nothing asked for yet" : d(g, fin.fundAsk),
                Palette.SIZE_TITLE, fin.fundAsk > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT));
        FlowPane up = new FlowPane(6, 6), down = new FlowPane(6, 6);
        for (double step : MONEY_STEPS) {
            up.getChildren().add(stepChip("+" + d(g, step), () -> { fin.fundAsk += step; redraw(); }, false));
            down.getChildren().add(stepChip("−" + d(g, step), () -> { fin.fundAsk = Math.max(0, fin.fundAsk - step); redraw(); }, false));
        }
        down.getChildren().add(stepChip("clear", () -> { fin.fundAsk = 0; redraw(); }, true));
        card.getChildren().addAll(up, down);
        Press in = payInPress(g, fin.fundAsk), out = drawOutPress(g, fin.fundAsk);
        ActionButton payIn = actionButton(Icons.COIN, Palette.MONEY, ACTION_TALL, in, () -> {
            if (in.look() == Look.GO) { g.fundPayIn(fin.fundAsk); redraw(); }
        });
        ActionButton drawOut = actionButton(Icons.COIN, Palette.MONEY, ACTION_TALL, out, () -> {
            if (out.look() == Look.GO) { g.fundDrawOut(Math.min(fin.fundAsk, g.fundCashFree())); redraw(); }
        });
        payIn.setMaxWidth(Double.MAX_VALUE);
        drawOut.setMaxWidth(Double.MAX_VALUE);
        card.getChildren().addAll(payIn, drawOut);
        return card;
    }

    /** PAY IN's press: choose, nothing in the treasury, or what it would move. */
    static Press payInPress(Game g, double ask) {
        return !(ask > 0) ? new Press(Look.CHOOSE, "Pay in · choose how much", null)
                : !(g.getCash() > 0) ? new Press(Look.HELD, "The treasury holds nothing to pay in", null)
                : new Press(Look.GO, "Pay in " + d(g, Math.min(ask, g.getCash())), "from the treasury's cash");
    }

    /** DRAW OUT's press: only the fund's free cash - not what the player's buys hold - can be drawn. */
    static Press drawOutPress(Game g, double ask) {
        return !(ask > 0) ? new Press(Look.CHOOSE, "Draw out · choose how much", null)
                : !(g.fundCashFree() > 0) ? new Press(Look.HELD, "No free cash to draw", "sell first, or cancel an order")
                : new Press(Look.GO, "Draw out " + d(g, Math.min(ask, g.fundCashFree())), "into the treasury");
    }

    /** THE RULE's sentence on the cap. */
    static String capRule() {
        return String.format("It holds no more than %s of any company on its market book, counting its bid and your "
                        + "orders as if they filled, its own bid making way for yours; past it - a company's buyback can "
                        + "lift it there - it asks the excess back at fair value from the next "
                        + "step. The rescue book is outside the cap. Each month it bids for its aim - a share at the desk's ask, "
                        + "fair value plus %s, a bond at its value - and takes only what is asked at or under it; what does "
                        + "not fit waits as cash.",
                pct1(TreasuryFund.OWNERSHIP_LIMIT).replace(".0", ""), pct1(TreasuryFund.RULE_PREMIUM).replace(".0", ""));
    }

    /** THE RULE: the shares against the aim, the cap in words, and the treasury's rescue setting. */
    VBox ruleCard(Game g) {
        VBox card = card(caption("THE RULE", holdsInfo()));
        card.getChildren().add(cardLine("Shares, of its market book and cash", pct1(g.fundEquityShare())
                + " · the aim " + pct1(TreasuryFund.EQUITY_WEIGHT), null));
        card.getChildren().add(segmentBar(List.of(seg(g.fundEquityShare(), Palette.BUSINESS, "shares "
                        + pct1(g.fundEquityShare()))), 1,
                List.of(new Tick(TreasuryFund.EQUITY_WEIGHT, Palette.TEXT_HEAD, 3, "aim " + pct1(TreasuryFund.EQUITY_WEIGHT), null),
                        new Tick(TreasuryFund.REBALANCE_OVER, Palette.TEXT_MUTED, 2, null, null),
                        new Tick(TreasuryFund.EQUITY_WEIGHT - TreasuryFund.REBALANCE_UNDER, Palette.TEXT_MUTED, 2, null, null)),
                0, 10));
        card.getChildren().add(line(capRule()));
        card.getChildren().add(fin.settingsPointer());
        return card;
    }
}
