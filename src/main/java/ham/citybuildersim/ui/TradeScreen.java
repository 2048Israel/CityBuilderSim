package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import ham.citybuildersim.ui.Pieces.Force;
import ham.citybuildersim.ui.Pieces.Mirror;
import ham.citybuildersim.ui.Pieces.Segment;
import ham.citybuildersim.ui.Pieces.Step;
import ham.citybuildersim.ui.Pieces.Tick;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;

/**
 * The Trade & the world tab: how the city stands against the world this
 * month - what it sells and buys abroad, the month's balance of payments,
 * what a US dollar costs and which way it is going, and the vault - on an
 * Overview and four pages behind it, a picture a page.
 *
 * WHY THIS SHAPE (0.7.35). It was a 560 px statement column in a 1,270 px
 * centre: a landing of four rows and three quiet gauges, then nine pages
 * under four areas, each a ledger with a paragraph under every line (the
 * river of the month the one picture). Jerus, on the screens not yet redone:
 * "the others are still full of text and the design could be more intuitive
 * and fun". Redrawn in Build's style (the project's spec-trade-0734.md): one
 * strip of five pages - Overview, The month, What we trade, The currency,
 * The reserves - the five figures across the top of every one, each a door;
 * the Overview leads with what the city trades as mirrored bars a good,
 * bought to the left and sold to the right, beside the rate on its own
 * chart, and the three gauges that decide whether a shock is survivable;
 * The month a waterfall from exports to the month's balance with the river
 * one toggle away; What we trade every good, or every business, with the
 * ten years and the record since founding; The currency the rate, its
 * chart and the forces on it as diverging bars; The reserves whose the
 * vault is, how long it would last, and the exchange. Every paragraph is
 * behind an (i); every statement the pages printed is behind "details".
 *
 * THE GOODS ARE THE BUSINESSES' OWN BOOKS (Game.getTradeByGood()): each
 * sector's statement split by good, home and abroad, which foots to the
 * balance of payments to the bit (ForeignCheck), the railway's fuel and the households' named;
 * not the markets' tally, which does not (the spec's B13). Colours say the
 * kind - exports the business violet, imports its darker step, income and
 * capital the money blues - and the verdict colours stay on the gauges,
 * THE CURRENCY and IN THE VAULT, the alerts and what was walked away from
 * (the spec's D7); one parity rule (ForeignAccounts.PARITY_WATCH and
 * PARITY_FAR) for this tab, the drawer's THE CURRENCY row and the
 * header's rate line (D8).
 *
 * BEFORE A MONTH IS COUNTED (ForeignAccounts.isMonthCounted()) - a city
 * just founded, or loaded from a save before 0.7.46 - the tab says "not
 * counted yet" until a month turns, rather than printing the flows' zeros
 * as the month's figures. Since 0.7.46 the save carries the month's flows
 * (the spec's D4, A2 in the model fixes), so a newer save loads counted.
 *
 * The shell reads which page is open (tradePage) for the rail and the
 * scroll memory, and FinancesScreen's doors set tradeArea, which names a
 * page; the panel is rebuilt on the clock, so the page, the scroll position,
 * the folds, the river's toggle, the goods' toggle and the exchange's ask
 * all survive a redraw.
 */
final class TradeScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    TradeScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       TRADE & THE WORLD (0.7.35): THE FRAME

       The head ("Trade & the world", or "Trade › The currency" with "Trade"
       a way back to the Overview) and its door to City History; the five
       pages as chips with icons; the five figures across the top of every
       page - SOLD ABROAD, BOUGHT ABROAD, THE MONTH, THE CURRENCY, IN THE
       VAULT - each a door to the page that takes it apart. Only the page
       scrolls under them (BankScreen's frame, 0.7.33). What the player must
       act on or know of first - the money leaving, thin cover, hot money
       barely backed, more owed abroad than held - is a card at the top of
       every page (Jerus: "both - quiet gauges plus loud blocks").
       ===================================================================== */

    /** The five pages, in the strip's order. */
    static final String OVERVIEW = "Overview", MONTH = "The month", GOODS = "What we trade",
            CURRENCY = "The currency", RESERVES = "The reserves";

    /** ...as the strip lists them. */
    static final String[] PAGES = {OVERVIEW, MONTH, GOODS, CURRENCY, RESERVES};

    /** ...and each one's icon: the tiles, the coin, the lorry, the two arrows, the safe. */
    static final String[] PAGE_ICONS = {Icons.OVERVIEW, Icons.COIN, Icons.LORRY, Icons.EXCHANGE, Icons.SAFE};

    /** The page the tab opens on, and the one the rail's trade icon resets to. */
    static final String TRADE_HOME = OVERVIEW;

    /** The page open: one of PAGES. */
    String tradePage = TRADE_HOME;

    /**
     * A page asked for by name from outside, read once and then null: the
     * nine pages lived under four areas until 0.7.35, and FinancesScreen's
     * doors still set the area ("The currency", "The reserves") with its
     * first page (TRADE_MONEY_PAGES, TRADE_VAULT_PAGES) - the area is the
     * page now.
     */
    String tradeArea = null;

    /** The currency's two pages until 0.7.35: a door that names the first lands on The currency. */
    static final String[] TRADE_MONEY_PAGES = {"The rate", "What is moving it"};

    /** The reserves' three pages until 0.7.35: a door that names the first lands on The reserves. */
    static final String[] TRADE_VAULT_PAGES = {"What is yours", "Cover", "Exchange"};

    /** What the player has opened - a fold, the goods past the first twelve - by key, so a redraw on the clock leaves it open. */
    final Set<String> openLines = new HashSet<>();

    /** The month page draws the river rather than the steps (the spec's D2: the steps lead, the river one toggle away), kept while the game runs. */
    boolean showRiver = false;

    /** What we trade draws a row a business rather than a row a good (D19), kept while the game runs. */
    boolean byBusiness = false;

    /** The exchange: buying foreign money rather than selling it, and how much is asked, in local money. */
    boolean tradeBuying = true;
    double tradeExchange = 0;

    /** Where the page is to be scrolled to once it is drawn - a card's key - or null for its top. */
    private String scrollTarget;

    /** A good whose popover opens once What we trade is drawn - an Overview row's click - or null. */
    private Good popGood;

    /** The nodes a door on this tab can scroll to, by key, as the page draws them. */
    private final Map<String, Node> targets = new HashMap<>();

    /** The page's scroller. */
    private javafx.scene.control.ScrollPane body;

    /** The currency chart's window and the lines its legend has hidden, kept across the month's redraw so a chart dragged back stays where it was put. */
    private final ChartModel rateWindow = new ChartModel();
    private final Set<String> rateHidden = new HashSet<>();

    /** The month the tab last drew: a new month landing is when its figures count up and its bars grow. */
    private int drawnMonth = Integer.MIN_VALUE;

    /** How much of the stage the fixed frame takes above the page's scroller - the head, the chips and the five figures - until the frame is laid out and its own height is read. */
    static final double FRAME_CHROME = 200;

    /** What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - BankScreen's, for the same frame. */
    static final double STAGE_REST = 36;

    /** How long the bars take to grow out of their axis when a month lands. */
    static final double GROW_MILLIS = 500;

    /** The Overview shows this many goods; What we trade this many before "+N more" (the spec's D6). */
    static final int OVERVIEW_GOODS = 8, PAGE_GOODS = 12;

    /** The scroll targets a door can land on. */
    static final String EXCHANGE_CARD = "#exchange", HOLDINGS = "#holdings", FORCES = "#forces", DEBT = "#debt",
            LEAVE = "#leave", COVER = "#cover", GOODS_CARD = "#goods";

    /* ------------------------ one way to write each number ------------------------ */

    /** The city's money's mark: "D$". */
    String sym() { return ui.game.getCurrency().qualifiedSymbol(); }

    /** Local money with its mark and a true minus: "D$2.4M", "−D$725k" (FinancesScreen's, the spec's D9). */
    String d(double thousands) {
        String shown = money(Math.abs(thousands));
        return (thousands < 0 && !"$0".equals(shown) ? "−" : "") + marked(sym(), shown);
    }

    /** ...with every digit. */
    String dFull(double thousands) {
        String shown = moneyFull(Math.abs(thousands));
        return (thousands < 0 && !"$0".equals(shown) ? "−" : "") + marked(sym(), shown);
    }

    /** ...signed, as a movement: "+D$1.2M", "−D$3.5M", "D$0". */
    String signedD(double thousands) {
        String shown = money(Math.abs(thousands));
        if ("$0".equals(shown)) return marked(sym(), shown);
        return (thousands < 0 ? "−" : "+") + marked(sym(), shown);
    }

    /** A price a unit, with its mark: "D$29,559", "D$1.31". */
    String unitD(double thousands) { return marked(sym(), unitPrice(thousands)); }

    /** An exchange rate with its unit, the header's way: "D$0.7072 per US$" (the spec's B4). */
    String perUsd(double rate) { return sym() + fxRate(rate) + " per " + Currency.FOREIGN_SYMBOL; }

    /** One of ours in theirs: "D$1 = US$1.41", "D$1 = US¢0.27" for a currency worth under a cent of theirs. */
    String oneOfOurs() {
        double usd = ui.game.getForeignAccounts().toUsd(1);
        if (usd >= .01) return sym() + "1 = " + Currency.FOREIGN_SYMBOL + String.format("%,.2f", usd);
        return sym() + "1 = " + Currency.FOREIGN_CENT_SYMBOL + String.format("%.2f", usd * 100);
    }

    /** How far from parity, in words: "26.2% stronger than parity", "at parity", "held fixed". */
    static String parityWords(ForeignAccounts fx) {
        if (fx.isPinned()) return "held fixed";
        double dev = fx.deviationFromParity();
        if (Math.abs(dev) < .0005) return "at parity";
        return share1(Math.abs(dev)) + (dev > 0 ? " weaker" : " stronger") + " than parity";
    }

    /** A parity level's colour: plain near, the watch amber, far red (ForeignAccounts.parityLevel()). */
    static String parityTone(int level) {
        return level >= 2 ? Palette.BAD : level == 1 ? Palette.WARN : Palette.TEXT_HEAD;
    }

    /** A cover level's colour: red thin, amber under comfortable, plain over it (ForeignAccounts.coverLevel()). */
    static String coverTone(int level) {
        return level >= 2 ? Palette.BAD : level == 1 ? Palette.WARN : Palette.TEXT_HEAD;
    }

    /** A move as a fraction, in words with its direction - a rate up is a weaker currency: "0.229% weaker", "0.142% stronger", "no move". */
    static String moveWords(double move) {
        double shown = Math.abs(move) * 100;
        if (shown < .0005) return "no move";
        return String.format("%.3f%%", shown) + (move > 0 ? " weaker" : " stronger");
    }

    /** A pressure as a share of the currency, with its direction: "15.5% weaker", "none". */
    static String pushWords(double pressure) {
        if (Math.abs(pressure) < 1e-9) return "none";
        return share1(Math.abs(pressure)) + (pressure > 0 ? " weaker" : " stronger");
    }

    /* ------------------------------ the frame ------------------------------ */

    /**
     * The tab's entry point: the Overview, or the page the player was on.
     * Named for the shell, which calls it from the rail and redraws it on
     * the clock; Finances' and Government's doors call it too (the drawer's
     * THE CURRENCY row calls open(), since 0.7.35).
     */
    void showForeignMenu() {
        ui.clearMenu("showForeignMenu", () -> showForeignMenu());

        if (tradeArea != null) {
            tradePage = tradeArea;
            tradeArea = null;
        }
        boolean known = false;
        for (String p : PAGES) known |= p.equals(tradePage);
        if (!known) tradePage = TRADE_HOME;
        targets.clear();

        boolean fresh = ui.game.getMonth() != drawnMonth;
        drawnMonth = ui.game.getMonth();

        VBox page = widePage();
        page.getChildren().addAll(actionCards());
        switch (tradePage) {
            case MONTH    -> monthPage(page);
            case GOODS    -> goodsPage(page, fresh);
            case CURRENCY -> currencyPage(page);
            case RESERVES -> reservesPage(page);
            default       -> overviewPage(page, fresh);
        }

        VBox frame = new VBox(Palette.GAP, head(),
                chipStrip(PAGES, PAGE_ICONS, tradePage, Palette.SIZE_LABEL, this::open), kpiStrip(fresh));
        frameOver(frame, page);

        if (scrollTarget != null || popGood != null) {
            String target = scrollTarget;
            Good good = popGood;
            scrollTarget = null;
            popGood = null;
            javafx.application.Platform.runLater(() -> {
                if (body == null) return;
                body.applyCss();
                body.layout();
                if (target != null) scrollTo(targets.get(target));
                Node row = good == null ? null : targets.get("#good:" + good.name());
                if (row != null) {
                    scrollTo(targets.get(GOODS_CARD));
                    openPopover(row, goodPopover(good));
                }
            });
        }
    }

    /** One of the five pages, at its top: a chip picked, or a door. */
    void open(String page) {
        open(page, null);
    }

    /** ...scrolled to a card on it (the doors land where the figure is taken apart). */
    void open(String page, String target) {
        tradeArea = null;
        tradePage = page;
        scrollTarget = target;
        ui.innerScrollAt.remove("showForeignMenu:body");
        showForeignMenu();
    }

    /** The Overview, at its top. */
    void openOverview() { open(OVERVIEW); }

    /** The exchange card, buying or selling - an ask the other way cleared: the cover alert's "Buy reserves". */
    void openExchange(boolean buying) {
        if (buying != tradeBuying) tradeExchange = 0;
        tradeBuying = buying;
        open(RESERVES, EXCHANGE_CARD);
    }

    /** The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (BankScreen's). */
    private void frameOver(VBox frame, VBox page) {
        frame.setMaxWidth(PAGE_WIDE);
        frame.setFillWidth(true);
        frame.setPadding(new javafx.geometry.Insets(0, 18, 4, 18));
        body = ui.scrolled(page, FRAME_CHROME);
        body.prefHeightProperty().unbind();
        body.prefHeightProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                () -> Math.max(260, ui.menuScroller.getHeight()
                        - (frame.getHeight() > 0 ? frame.getHeight() + STAGE_REST : FRAME_CHROME)),
                ui.menuScroller.heightProperty(), frame.heightProperty()));
        final javafx.scene.control.ScrollPane scroller = body;
        page.prefWidthProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                () -> Math.min(PAGE_WIDE, Math.max(320, scroller.getViewportBounds().getWidth())),
                scroller.viewportBoundsProperty()));
        ui.rootMenu.getChildren().addAll(frame, body);
    }

    /** Scroll the page to a node on it (InfrastructureScreen's). */
    void scrollTo(Node target) {
        if (body == null || target == null || body.getContent() == null) return;
        double contentH = body.getContent().getBoundsInLocal().getHeight();
        double viewH = body.getViewportBounds().getHeight();
        if (contentH <= viewH) return;
        javafx.geometry.Bounds at = target.localToScene(target.getBoundsInLocal());
        javafx.geometry.Bounds top = body.getContent().localToScene(body.getContent().getBoundsInLocal());
        if (at == null || top == null) return;
        double y = at.getMinY() - top.getMinY() - 12;
        body.setVvalue(Math.max(0, Math.min(1, y / (contentH - viewH))));
    }

    /** The tab's (i): the landing's lead (the spec's L2), and what the Overview shows. */
    static final String LEAD_INFO = "What the city sells, what it buys, and what it owes in somebody else's money. "
            + "The Overview shows what it traded this month, good by good, beside what a US dollar costs, and the three "
            + "readings that decide whether a shock is survivable. The four pages behind it take each one apart.";

    /** Each page's (i), in PAGES' order after the Overview: what it holds (the landing's row blurbs until 0.7.35). */
    static final String[] PAGE_INFO = {
        "Every dollar that crossed the city's edge this month, and which way: a walk from what it sold abroad to the "
                + "month's balance, through the two accounts - or the same month as a river. Under it, what the city "
                + "holds abroad and what the world holds here. The statement is under \"details\".",
        "What the city sells abroad and what it buys, good by good or business by business, from the businesses' own "
                + "books; the last ten years, the record since founding, and what the world pays and charges.",
        "What a US dollar costs, and which way it is going: the rate against parity, its history with the city's "
                + "decisions on it, and the forces on it next month - the trade term, the real rate, the vault's "
                + "defence, the anchored drift and the pull back to parity. Then what the rate is doing to the dollar "
                + "debt and the vault.",
        "What the vault holds, whose it is and how long it would last; the money that can leave on no notice; what "
                + "else moved the vault; and the exchange - cash into the vault, or the vault into cash.",
    };

    /** A page's (i) by its name. */
    static String pageInfo(String page) {
        for (int i = 1; i < PAGES.length; i++) if (PAGES[i].equals(page)) return PAGE_INFO[i - 1];
        return LEAD_INFO;
    }

    /**
     * The head: "Trade & the world" with the business violet's swatch, or the
     * breadcrumb "Trade › The currency", "Trade" a way back to the Overview;
     * at its right a door to City History on the page's own lines.
     */
    HBox head() {
        HBox years = doorPill("Over the years", Icons.REPORTS, Palette.ACCENT, () -> {
            switch (tradePage) {
                case CURRENCY -> ui.historyScreen.openOn("fxRate", "fxParity");
                case RESERVES -> ui.historyScreen.openOn("reservesUsd", "foreignDebtUsd");
                case MONTH    -> ui.historyScreen.openOn("currentAccount", "tradeBalance");
                default       -> ui.historyScreen.openOn("exportsAbroad", "importsAbroad");
            }
        });
        if (OVERVIEW.equals(tradePage)) return pageHead("Trade & the world", Palette.BUSINESS, null, null, LEAD_INFO, years);
        return pageHead("Trade", Palette.BUSINESS, this::openOverview, tradePage, pageInfo(tradePage), years);
    }

    /* ------------------------------ the five figures ------------------------------ */

    /** One figure of the strip, worked out without drawing it: its label, its figure, its note, its colour, and where its click goes. */
    record Cell(String label, String value, String note, String tone, String where) { }

    /** What a figure of the month says until a month has turned since the city was loaded or founded (ForeignAccounts.isMonthCounted()). */
    static final String NOT_COUNTED = "not counted yet";

    /** ...and its note. */
    static final String NOT_COUNTED_NOTE = "since the city was founded, or loaded from a save before 0.7.46: a month on, it is";

    /**
     * THE FIVE (pure: the probe reads them as the strip shows them). SOLD
     * and BOUGHT and THE MONTH are plain - "surplus" and "deficit" in
     * words, never a verdict's colour (the spec's D10, B7); THE CURRENCY
     * takes the parity level (D8); IN THE VAULT the cover's (red under
     * THIN_COVER, amber under COMFORTABLE_COVER: B10's green over a red
     * alert gone).
     */
    List<Cell> kpiCells() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        boolean counted = fx.isMonthCounted();
        List<Cell> out = new ArrayList<>();
        out.add(counted
                ? new Cell("SOLD ABROAD", d(fx.getExports()), fx.monthlyExports() > 0
                        ? d(fx.monthlyExports()) + " a month on average" : "nothing yet",
                        Palette.TEXT_HEAD, "What we trade: every good, sold and bought")
                : new Cell("SOLD ABROAD", NOT_COUNTED, NOT_COUNTED_NOTE, Palette.TEXT_SPENT,
                        "What we trade: every good, sold and bought"));
        out.add(counted
                ? new Cell("BOUGHT ABROAD", d(fx.tradeImports()), fx.monthlyImports() > 0
                        ? d(fx.monthlyImports()) + " a month on average" : "nothing yet",
                        Palette.TEXT_HEAD, "What we trade: every good, sold and bought")
                : new Cell("BOUGHT ABROAD", NOT_COUNTED, NOT_COUNTED_NOTE, Palette.TEXT_SPENT,
                        "What we trade: every good, sold and bought"));
        out.add(counted
                ? new Cell("THE MONTH", signedD(fx.balance()), (fx.balance() >= 0 ? "surplus" : "deficit")
                        + " · current " + signedD(fx.currentAccount()) + ", financial " + signedD(fx.financialAccount()),
                        Palette.TEXT_HEAD, "The month: every dollar that crossed the edge")
                : new Cell("THE MONTH", NOT_COUNTED, NOT_COUNTED_NOTE, Palette.TEXT_SPENT,
                        "The month: every dollar that crossed the edge"));
        out.add(new Cell("THE CURRENCY", perUsd(fx.getRate()), parityWords(fx),
                fx.isPinned() ? Palette.TEXT_HEAD : parityTone(ForeignAccounts.parityLevel(fx.deviationFromParity())),
                "The currency: the rate, its chart and the forces on it"));
        boolean buying = fx.monthlyImports() > 0;
        out.add(new Cell("IN THE VAULT", usd(fx.getReservesUsd()), d(fx.getReserves()) + " · "
                + (buying ? coverMonths(fx.importCover()) + " of imports" : "nothing bought abroad yet"),
                buying ? coverTone(ForeignAccounts.coverLevel(fx.importCover())) : Palette.TEXT_HEAD,
                "The reserves: whose it is and how long it would last"));
        return out;
    }

    /** The strip: the five as limit cells, each a door; SOLD and BOUGHT count up from last month's when a month lands (the spec's section 5). */
    HBox kpiStrip(boolean fresh) {
        List<Cell> five = kpiCells();
        Runnable[] go = {() -> open(GOODS), () -> open(GOODS), () -> open(MONTH),
                () -> open(CURRENCY), () -> open(RESERVES)};
        VBox[] cells = new VBox[five.size()];
        for (int i = 0; i < cells.length; i++) {
            Cell c = five.get(i);
            cells[i] = limitCell(c.label(), c.value(), c.note(), c.tone(), c.where(), go[i]);
        }
        ForeignAccounts fx = ui.game.getForeignAccounts();
        String moved = rateMove();
        if (moved != null) {
            Label l = new Label(moved);
            l.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            cells[3].getChildren().add(l);
        }
        if (fresh && fx.isMonthCounted()) {
            HistorySave h = ui.game.getHistorySave();
            countFrom(cells[0], h, "exportsAbroad", fx.getExports());
            countFrom(cells[1], h, "importsAbroad", fx.tradeImports());
        }
        return vitalsBar(cells);
    }

    /** A cell's figure counted up from the month before's, History's second-last point (SectorScreen.countUp()). */
    private void countFrom(VBox cell, HistorySave h, String series, double now) {
        double[] s = h.aligned(series);
        if (s.length < 2 || !(cell.getChildren().size() > 1) || !(cell.getChildren().get(1) instanceof Label figure)) return;
        double before = s[s.length - 2];
        if (Double.isFinite(before)) SectorScreen.countUp(figure, before, now, this::d);
    }

    /** The rate's move on last month, from History's last two points: "▲ 0.100% weaker this month"; null with fewer than two or no move to three places (the spec's section 5). */
    String rateMove() {
        double move = ui.game.getHistorySave().changeOver("fxRate", 1);
        if (!Double.isFinite(move) || Math.abs(move) * 100 < .0005) return null;
        return (move > 0 ? "▲ " : "▼ ") + moveWords(move) + " this month";
    }

    /* ------------------------------ the action cards ------------------------------ */

    /**
     * What the player must act on or know of first, at the top of every page
     * (the landing's four loud blocks, the spec's D18): the money leaving,
     * under three months of cover, the hot money barely backed - red - and
     * more owed abroad than held - amber. Each a card with one line of
     * figures, the old paragraph whole behind its (i), and a door.
     */
    List<Node> actionCards() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        CapitalFlows hot = ui.game.getCapitalFlows();
        List<Node> out = new ArrayList<>();
        if (hot.isStopped()) {
            int left = hot.stopMonthsLeft();
            String months = left + " month" + (left == 1 ? "" : "s");
            out.add(actionCard(Icons.ALERT, Palette.BAD, "The money is leaving",
                    String.format("%s · %s before anybody abroad looks at this city again.", hot.getStopReason(), months),
                    String.format("%s. %s before anybody abroad looks at this city again. Nothing new will be lent here "
                            + "and what is here is going home.", hot.getStopReason(), months),
                    doorPill("The money that can leave", Icons.SAFE, Palette.MONEY, () -> open(RESERVES, LEAVE))));
        }
        if (fx.monthlyImports() > 0 && fx.importCover() < ForeignAccounts.THIN_COVER) {
            out.add(actionCard(Icons.ALERT, Palette.BAD,
                    String.format("Under %.0f months of import cover", ForeignAccounts.THIN_COVER),
                    String.format("The vault holds %s against %s a month of imports.",
                            d(fx.getReserves()), d(fx.monthlyImports())),
                    String.format("The vault holds %s against %s a month of buying abroad. %.0f months is the line below "
                            + "which the world stops pricing a currency on its trade balance and starts pricing it for a "
                            + "crisis.", d(fx.getReserves()), d(fx.monthlyImports()), ForeignAccounts.THIN_COVER),
                    doorPill("Buy reserves", Icons.EXCHANGE, Palette.MONEY, () -> openExchange(true))));
        }
        if (hot.getStock() > 0 && fx.getReserves() / hot.getStock() < CapitalFlows.PANIC_BACKING) {
            double backing = fx.getReserves() / hot.getStock();
            out.add(actionCard(Icons.ALERT, Palette.BAD, "The hot money is barely backed",
                    String.format("%s parked here against %s in the vault: %.0f%% cover.",
                            d(hot.getStock()), d(fx.getReserves()), backing * 100),
                    String.format("%s of foreign money is parked in this city's bank and the vault holds %s against it — "
                            + "%.0f%% cover. Under %.0f%% the next shock stops being a wobble and becomes a run.",
                            d(hot.getStock()), d(fx.getReserves()), backing * 100, CapitalFlows.PANIC_BACKING * 100),
                    doorPill("The money that can leave", Icons.SAFE, Palette.MONEY, () -> open(RESERVES, LEAVE))));
        }
        if (fx.netForeignPosition() < 0 && fx.getForeignDebt() > 0) {
            out.add(actionCard(Icons.ALERT, Palette.WARN, "The city owes the world more than it holds",
                    String.format("%s of dollar paper against %s of reserves.", d(fx.getForeignDebt()), d(fx.getReserves())),
                    String.format("%s of dollar paper against %s of reserves. A city with deep reserves and deeper dollar "
                            + "debt is not rich, and the reserve line on its own says it is.",
                            d(fx.getForeignDebt()), d(fx.getReserves())),
                    doorPill("The dollar debt", Icons.EXCHANGE, Palette.MONEY, () -> open(CURRENCY, DEBT))));
        }
        return out;
    }

    /** One action card: its icon square, its title, one line with the whole behind an (i), and its door (null: none), on the raised ground with a 3 px top edge in `tone` (BankScreen's). */
    static VBox actionCard(String svg, String tone, String title, String line, String whole, Node act) {
        Label t = new Label(title);
        t.setWrapText(true);
        t.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        HBox head = new HBox(Palette.GAP, iconSquare(svg, tone, 28, 15), t);
        head.setAlignment(Pos.CENTER_LEFT);
        VBox c = new VBox(8, head);
        if (line != null) c.getChildren().add(noteLine(line, whole, 1100));
        if (act != null) c.getChildren().add(act);
        c.setMaxWidth(Double.MAX_VALUE);
        c.setStyle("-fx-padding: 12 14 12 14; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + tone + " "
                + Palette.EDGE + " " + Palette.EDGE + " " + Palette.EDGE + "; -fx-border-width: 3 1 1 1;");
        return c;
    }

    /* ----------------------------- the screen's own pieces ----------------------------- */

    /** Words that wrap, at a size, in a colour. */
    static Label words(String text, double size, String tone) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setMinWidth(0);
        l.setStyle(BuildScreen.wordsAt(size, tone));
        return l;
    }

    /** A figure that is never cut, at a size, in a colour. */
    static Label figure(String text, double size, String tone) {
        Label l = new Label(text);
        l.setMinWidth(Region.USE_PREF_SIZE);
        l.setStyle(BuildScreen.figureAt(size, tone));
        return l;
    }

    /** A card's head: its icon in a square tinted in its colour, its title in capitals, an (i) when `info` is not null, and at its right `right` (null: nothing) - BankScreen's. */
    static HBox cardHead(String svg, String colour, String title, String info, Node right) {
        Label t = new Label(title);
        t.setWrapText(true);
        t.setMinWidth(0);
        t.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        HBox row = new HBox(Palette.GAP);
        if (svg != null) row.getChildren().add(iconSquare(svg, colour, 28, 15));
        row.getChildren().add(t);
        if (info != null) row.getChildren().add(infoButton(info, true));
        if (right != null) {
            Region gap = new Region();
            HBox.setHgrow(gap, Priority.ALWAYS);
            row.getChildren().addAll(gap, right);
        }
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 12. */
    static VBox card(Node... rows) {
        VBox c = new VBox(8);
        for (Node n : rows) if (n != null) c.getChildren().add(n);
        c.setMaxWidth(Double.MAX_VALUE);
        c.setStyle(cardStyle(Palette.EDGE));
        return c;
    }

    /** The card's ground, with its edge in a colour. */
    static String cardStyle(String edge) {
        return "-fx-padding: 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + edge + ";";
    }

    /** A card that opens somewhere: the accent edge under the pointer, and a click goes. A door inside it opens itself. */
    static VBox doorCard(VBox c, Runnable go) {
        if (go == null) return c;
        String rest = c.getStyle() + " -fx-cursor: hand;";
        c.setStyle(rest);
        c.setOnMouseEntered(e -> c.setStyle(rest.replace(Palette.EDGE + ";", Palette.ACCENT + ";")));
        c.setOnMouseExited(e -> c.setStyle(rest));
        c.setOnMouseClicked(e -> go.run());
        return c;
    }

    /** A line of a card: words at the left, wrapping, and a figure at the right. */
    static HBox cardLine(String label, String value, String tone) {
        Label what = words(label, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL);
        HBox.setHgrow(what, Priority.ALWAYS);
        what.setMaxWidth(Double.MAX_VALUE);
        Label v = figure(value, Palette.SIZE_LABEL + 1, tone == null ? Palette.TEXT_HEAD : tone);
        HBox row = new HBox(Palette.GAP, what, v);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** ...with an (i) after its words. */
    static HBox cardLine(String label, String value, String tone, String info) {
        HBox row = cardLine(label, value, tone);
        if (info != null) row.getChildren().add(1, infoButton(info, true));
        return row;
    }

    /** A muted line with its whole behind an (i), wrapping at `wide`. */
    static HBox noteLine(String shown, String whole, double wide) {
        return infoLine(shown, whole, true, Palette.SIZE_LABEL, Palette.TEXT_MUTED, wide);
    }

    /** A plain line of words in a card, wrapping, with its whole behind an (i) when `whole` is not null. */
    static Node line(String text, String whole) {
        if (whole == null) return words(text, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL);
        return infoLine(text, whole, true, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL, 1100);
    }

    /** ...muted. */
    static Label muted(String text) { return words(text, Palette.SIZE_LABEL, Palette.TEXT_MUTED); }

    /** A heading's quiet words at its right, never cut (Pieces.hint() at its own width). */
    static Label quiet(String text) {
        Label l = hint(text);
        l.setMinWidth(Region.USE_PREF_SIZE);
        return l;
    }

    /** A chip, and after it an (i) holding `whole` when that is not null. */
    static HBox chipInfo(String text, String colour, String whole) {
        HBox row = new HBox(Palette.GAP_TIGHT, chip(text, colour));
        if (whole != null) row.getChildren().add(infoButton(whole, true));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** A row of chips, wrapping. */
    static FlowPane chips(Node... cs) {
        FlowPane row = new FlowPane(8, 6);
        for (Node n : cs) if (n != null) row.getChildren().add(n);
        return row;
    }

    /** A statement column, for a fold: the old rows at their old width. */
    static VBox column(Node... rows) {
        VBox c = new VBox(0);
        for (Node n : rows) if (n != null) c.getChildren().add(n);
        c.setMaxWidth(STATEMENT);
        return c;
    }

    /** A fold kept on this screen: "details ▸ caption". */
    VBox fold(String key, String caption, java.util.function.Supplier<Node> inside) {
        return details(key, caption, openLines, this::showForeignMenu, inside);
    }

    /** Cards side by side in equal columns, each as tall as the tallest. */
    static GridPane row(Node... cards) {
        GridPane g = equalColumns(cards.length, TILE_GAP);
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] == null) continue;
            if (cards[i] instanceof Region r) {
                GridPane.setFillHeight(r, true);
                r.setMaxHeight(Double.MAX_VALUE);
            }
            g.add(cards[i], i, 0);
        }
        return g;
    }

    /** A key's entry: a swatch and a name. */
    static HBox swatch(String colour, String name) { return keySwatch(colour, name); }

    /** A row of a key: entries with a gap, wrapping. */
    static FlowPane keyRow(Node... entries) {
        FlowPane key = new FlowPane(14, 4);
        for (Node n : entries) if (n != null) key.getChildren().add(n);
        return key;
    }

    /** A stretch of a segment bar with its tooltip. */
    static Segment seg(double amount, String colour, String tip) {
        return new Segment(amount, colour, false, null, null, tip, null);
    }

    /** A sector by its key, or null. */
    Sector sector(String key) { return ui.game.getSectors().byKey(key); }

    /** One business's books, on its Operations page (Sectors › it). */
    void openSector(String key) {
        Sector s = sector(key);
        if (s != null) ui.sectorScreen.openSectorBooks(s, SectorScreen.SECTOR_PAGES[0]);
    }

    /** A door to one business: a pill in the business violet with its icon. */
    HBox sectorDoor(String words, String key) {
        return doorPill(words, Icons.ofSector(sector(key)), Palette.BUSINESS, () -> openSector(key));
    }

    /* =====================================================================
       THE OVERVIEW (0.7.35; the landing's four rows until then)

       How does the city stand against the world this month? WHAT WE TRADE
       as mirrored bars, a row a good - what it bought to the left of the
       axis and what it sold to the right, on one scale, the top eight -
       beside THE CURRENCY: what a US dollar costs, against parity, on a
       chart of its last ten years. Under them the three readings that decide
       whether a shock is survivable, as gauge cards: import cover, what
       backs the money that can leave, how far the rate has run from parity.
       ===================================================================== */

    void overviewPage(VBox page, boolean fresh) {
        VBox trade = tradeCard(OVERVIEW_GOODS, fresh, false);
        VBox money = currencyCard();
        money.setMinWidth(400);
        money.setPrefWidth(458);
        money.setMaxWidth(458);
        HBox.setHgrow(trade, Priority.ALWAYS);
        HBox hero = new HBox(TILE_GAP, trade, money);
        hero.setAlignment(Pos.TOP_LEFT);
        page.getChildren().add(hero);
        page.getChildren().add(sectionHead("WHAT DECIDES WHETHER A SHOCK IS SURVIVABLE", GAUGES_INFO,
                quiet("the same three every month")));
        page.getChildren().add(row(coverGauge(), backingGauge(), parityGauge()));
    }

    /** The section's (i): the banner's reason (the spec's L8). */
    static final String GAUGES_INFO = "Always here, deliberately understated, and the same three every month - so a "
            + "player learns what to watch before the shock rather than reading about it afterwards in a red box. Each is "
            + "a band rather than a number, because \"4.2 months of cover\" means nothing on its own and \"just inside the "
            + "line\" means something to anybody.";

    /* ------------------------------ what we trade, as bars ------------------------------ */

    /**
     * One row of the goods or the businesses, worked out without drawing it
     * (pure: the probe reads them): its icon, its name, what it bought and
     * sold abroad, its net, its tooltip, and the good or the sector key it
     * opens (one is null; both null for the households' row by business).
     */
    record GoodRow(String icon, String name, double bought, double sold, double net, String tip,
                   Good good, String sector) { }

    /**
     * Every good that crossed the edge this month, largest side first (the
     * spec's D6: a good with nothing either way is not a row), then what was
     * bought abroad with no good behind it, by the sector that bought it
     * (pure). The railway's fuel and the households' were such rows until
     * fuel was a good (0.7.62); they are FUEL's buyers now.
     */
    List<GoodRow> goodRows(Sectors.TradeByGood t) {
        List<Sectors.GoodTrade> goods = new ArrayList<>(t.goods().values());
        goods.removeIf(g -> !(g.sold() > 0) && !(g.bought() > 0));
        goods.sort((a, b) -> Double.compare(Math.max(b.sold(), b.bought()), Math.max(a.sold(), a.bought())));
        List<GoodRow> out = new ArrayList<>();
        for (Sectors.GoodTrade g : goods) {
            out.add(new GoodRow(Icons.ofGood(g.good()), g.good().label(), g.bought(), g.sold(), g.net(),
                    goodTip(g), g.good(), null));
        }
        for (Map.Entry<String, Double> e : t.services().entrySet()) {
            if (!(e.getValue() > 0)) continue;
            out.add(new GoodRow(Icons.ofSector(sector(e.getKey())), "Bought abroad by " + e.getKey(),
                    e.getValue(), 0, -e.getValue(),
                    "Bought abroad by " + e.getKey() + "\n" + dFull(e.getValue())
                            + "\nAn import with no good behind it.", null, e.getKey()));
        }
        return out;
    }

    /** A good's tooltip: what crossed each way, and who. */
    String goodTip(Sectors.GoodTrade g) {
        StringBuilder s = new StringBuilder(g.good().label());
        if (g.sold() > 0) s.append("\nsold abroad ").append(dFull(g.sold())).append(" - ").append(whoWords(g.sellers()));
        if (g.bought() > 0) s.append("\nbought abroad ").append(dFull(g.bought())).append(" - ").append(whoWords(g.buyers()));
        s.append("\nClick: who, and what the world pays and charges.");
        return s.toString();
    }

    /** "Automotive D$288.4M, Manufacturing D$37.0M" - by sector, largest first. */
    String whoWords(Map<String, Double> who) {
        List<Map.Entry<String, Double>> rows = new ArrayList<>(who.entrySet());
        rows.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        StringBuilder s = new StringBuilder();
        for (Map.Entry<String, Double> e : rows) {
            if (s.length() > 0) s.append(", ");
            s.append(e.getKey().equals(Sectors.HOUSEHOLDS) ? "the households" : e.getKey()).append(' ').append(d(e.getValue()));
        }
        return s.toString();
    }

    /**
     * A row a business (pure, the spec's D19): every sector that sold or
     * bought abroad this month, off its statement's exports and imports, and
     * the households' cars when they bought any - and their fuel (0.7.49), on
     * the same row, so the rows add up to what the city bought.
     */
    List<GoodRow> businessRows(Sectors.TradeByGood t) {
        List<GoodRow> out = new ArrayList<>();
        for (Sector s : ui.game.getSectors().all()) {
            Sector.Statement st = s.statement();
            if (!(st.exports > 0) && !(st.imports > 0)) continue;
            out.add(new GoodRow(Icons.ofSector(s), s.key(), st.imports, st.exports, Double.NaN,
                    s.key() + "\nsold abroad " + dFull(st.exports) + "\nbought abroad " + dFull(st.imports)
                            + "\nClick: its books.", null, s.key()));
        }
        double householdFuel = t.householdFuel();
        if (t.householdCars() > 0 || householdFuel > 0) {
            out.add(new GoodRow(Icons.POPULATION, "The households", t.householdCars() + householdFuel, 0, Double.NaN,
                    "The households\nbought abroad " + (t.householdCars() > 0 ? dFull(t.householdCars()) + " of cars" : "")
                            + (t.householdCars() > 0 && householdFuel > 0 ? " and " : "")
                            + (householdFuel > 0 ? dFull(householdFuel) + " of fuel" : ""), null, null));
        }
        out.sort((a, b) -> Double.compare(Math.max(b.sold(), b.bought()), Math.max(a.sold(), a.bought())));
        return out;
    }

    /** The rows as mirrored bars, each a click: a good's popover, a business's books. */
    MirrorRows mirror(List<GoodRow> rows, String leftHead, String rightHead, boolean nets, boolean toPage) {
        List<Mirror> m = new ArrayList<>();
        for (GoodRow r : rows) {
            java.util.function.Consumer<Node> go;
            if (r.good() != null) {
                Good g = r.good();
                go = toPage ? anchor -> { popGood = g; byBusiness = false; open(GOODS); }
                        : anchor -> openPopover(anchor, goodPopover(g));
            } else if (r.sector() != null) {
                String key = r.sector();
                go = anchor -> openSector(key);
            } else {
                go = null;
            }
            m.add(new Mirror(r.icon(), r.name(), r.bought(), r.sold(),
                    r.bought() > 0 ? d(r.bought()) : null, r.sold() > 0 ? d(r.sold()) : null,
                    nets && Double.isFinite(r.net()) ? signedD(r.net()) : null, r.tip(), go));
        }
        return mirrorRows(m, 0, Palette.BUSINESS_DARK, Palette.BUSINESS, leftHead, rightHead,
                toPage ? 168 : 220, nets ? 110 : 0);
    }

    /**
     * WHAT WE TRADE, as a card: the goods as mirrored bars, the first `shown`
     * of them, the rest folded into one line; on the Overview a door to What
     * we trade (a row there opens What we trade with its popover).
     */
    VBox tradeCard(int shown, boolean fresh, boolean page) {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        Sectors.TradeByGood t = ui.game.getTradeByGood();
        List<GoodRow> all = goodRows(t);
        String hint = fx.isMonthCounted() ? "this month · bought left, sold right"
                : "the month the city was saved in · bought left, sold right";
        VBox c = card(cardHead(Icons.LORRY, Palette.BUSINESS, "WHAT WE TRADE", TRADE_INFO, quiet(hint)));
        if (all.isEmpty()) {
            c.getChildren().add(line("Nothing crossed the city's edge this month.", NOTHING_CROSSED));
            return page ? c : doorCard(c, () -> open(GOODS));
        }
        List<GoodRow> top = all.subList(0, Math.min(shown, all.size()));
        MirrorRows bars = mirror(top, "BOUGHT ABROAD " + d(t.bought()), "SOLD ABROAD " + d(t.sold()), false, true);
        if (fresh && fx.isMonthCounted()) bars.grow(GROW_MILLIS);
        c.getChildren().add(bars);
        if (all.size() > shown) {
            double sold = 0, bought = 0;
            for (GoodRow r : all.subList(shown, all.size())) {
                sold += r.sold();
                bought += r.bought();
            }
            int goods = all.size() - shown;
            String more = "+" + goods + " more good" + (goods == 1 ? "" : "s")
                    + " · " + d(sold) + " sold · " + d(bought) + " bought";
            c.getChildren().add(door(more, Palette.ACCENT, () -> open(GOODS)));
        }
        return doorCard(c, () -> open(GOODS));
    }

    /** WHAT WE TRADE's (i). */
    static final String TRADE_INFO = "Every good the city's businesses sold abroad and bought abroad this month, from "
            + "their own books - each line of revenue and of cost split home and abroad as it was traded - so it adds to "
            + "SOLD ABROAD and BOUGHT ABROAD to the dollar. Fuel is a good: the railway and the households' drivers buy it "
            + "off the city's refineries first and from the world for the rest. Every world price is quoted in the world's money and converted at the rate, so a weaker "
            + "currency raises what imports cost at home and what exports earn at home, both at once.";

    /** The empty month's whole (the spec's M2). */
    static final String NOTHING_CROSSED = "Nothing crossed the city's edge this month. No exports, no imports, nothing "
            + "borrowed and nothing repaid — which is a city that is not trading rather than a screen with nothing to say.";

    /**
     * A good's popover (the spec's section 3): who sold it and who bought it,
     * by sector; what the world pays for one and charges to bring one in,
     * the market's own prices (GoodsMarket.exportPrice(), importPrice() -
     * the railway's freight in them, B5); the same in US$ at today's rate;
     * and a door to the business that sold most of it (or bought most).
     */
    Node goodPopover(Good good) {
        Sectors.GoodTrade g = ui.game.getTradeByGood().goods().get(good);
        ForeignAccounts fx = ui.game.getForeignAccounts();
        GoodsMarket market = ui.game.getMarkets().get(good);
        VBox box = new VBox(6);
        Label title = new Label(good.label());
        title.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        box.getChildren().add(title);
        String main = null;
        if (g != null && g.sold() > 0) {
            box.getChildren().add(cardLine("sold abroad", d(g.sold()), null));
            for (Map.Entry<String, Double> e : sorted(g.sellers())) box.getChildren().add(cardLine("   by " + e.getKey(), d(e.getValue()), Palette.TEXT_LABEL));
            main = sorted(g.sellers()).get(0).getKey();
        }
        if (g != null && g.bought() > 0) {
            box.getChildren().add(cardLine("bought abroad", d(g.bought()), null));
            for (Map.Entry<String, Double> e : sorted(g.buyers())) {
                box.getChildren().add(cardLine("   by " + (Sectors.HOUSEHOLDS.equals(e.getKey()) ? "the households" : e.getKey()),
                        d(e.getValue()), Palette.TEXT_LABEL));
            }
            if (main == null) {
                String first = sorted(g.buyers()).get(0).getKey();
                if (!Sectors.HOUSEHOLDS.equals(first)) main = first;
            }
        }
        if (market != null) {
            String a = " a" + (good.unit().matches("^[aeiou].*") ? "n " : " ") + good.unit();
            double pays = market.exportPrice(), charges = market.importPrice();
            if (Double.isFinite(pays)) {
                box.getChildren().add(cardLine("the world pays", unitD(pays) + a, null));
                box.getChildren().add(cardLine("   at today's rate", marked(Currency.FOREIGN_SYMBOL, unitPrice(fx.toUsd(pays))), Palette.TEXT_LABEL));
            }
            if (Double.isFinite(charges)) {
                box.getChildren().add(cardLine("the world charges to bring one in", unitD(charges) + a, null));
                box.getChildren().add(cardLine("   at today's rate", marked(Currency.FOREIGN_SYMBOL, unitPrice(fx.toUsd(charges))), Palette.TEXT_LABEL));
            }
            if (!fx.isMonthCounted()) box.getChildren().add(muted(FREIGHT_AFTER_LOAD));
        }
        if (main != null) box.getChildren().add(sectorDoor(main, main));
        box.setPrefWidth(POPOVER_WIDTH);
        return box;
    }

    /** Who, largest first. */
    static List<Map.Entry<String, Double>> sorted(Map<String, Double> who) {
        List<Map.Entry<String, Double>> rows = new ArrayList<>(who.entrySet());
        rows.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return rows;
    }

    /** The prices before a month is counted - a city just founded, or loaded from a save before 0.7.46 (the spec's B14; a newer save carries the month's trade the freight is struck on, A1). */
    static final String FREIGHT_AFTER_LOAD = "Not counted yet: the railway's freight on each good is struck when the month "
            + "turns, so these prices may step a month on.";

    /* ------------------------------ the currency, as a card ------------------------------ */

    /**
     * THE CURRENCY, as a card: what a US dollar costs at 28 px with its unit,
     * one of ours in theirs and parity, the distance from parity as a chip
     * in its level, the rate's last ten years on a small chart with parity
     * beside it, and its move over the year in words - no verdict colour. A
     * door to The currency.
     */
    VBox currencyCard() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        HistorySave h = ui.game.getHistorySave();
        Label rate = figure(sym() + fxRate(fx.getRate()), 28, Palette.TEXT_HEAD);
        Label per = words("per " + Currency.FOREIGN_SYMBOL, 13, Palette.TEXT_LABEL);
        HBox big = new HBox(6, rate, per);
        big.setAlignment(Pos.BASELINE_LEFT);
        VBox c = card(cardHead(Icons.EXCHANGE, Palette.MONEY, "THE CURRENCY", RATE_INFO, null), big,
                muted(oneOfOurs() + " · parity " + sym() + fxRate(fx.getParity())));
        int level = fx.isPinned() ? 0 : ForeignAccounts.parityLevel(fx.deviationFromParity());
        c.getChildren().add(chips(chip(parityWords(fx), level == 0 ? Palette.TEXT_MUTED : parityTone(level))));
        if (h.months() >= 2) {
            c.getChildren().add(rateChart(h, new ChartModel(), false, 426, 140));
        }
        double year = h.changeOver("fxRate", 12);
        c.getChildren().add(muted(Double.isFinite(year)
                ? (Math.abs(year) < .0005 ? "no move in a year" : share1(Math.abs(year)) + (year > 0 ? " weaker" : " stronger") + " than a year ago")
                : "not a year recorded yet"));
        return doorCard(c, () -> open(CURRENCY));
    }

    /** THE CURRENCY's (i): the unit, and parity (the spec's Q1, Q3). */
    static final String RATE_INFO = "How many of the city's dollars one US dollar costs. Higher is a weaker currency: "
            + "imports cost more at home and what the city sells abroad earns more. Parity is where a basket of goods "
            + "costs the same here and abroad - the city's own price index against the world's - and a currency that has "
            + "wandered a long way from it gets pulled back, slowly, whatever else is happening to it.";

    /**
     * The rate and parity on a chart: History's fxRate in the money blue and
     * fxParity (recorded since 0.7.35) in its light step. The big one -
     * with its legend, its ranges, the currency's crises on the episode lane
     * and the CURRENCY decisions as flags, the founding month's on its
     * first month (ChartModel.onAxis(), 0.7.38) - on The currency; a small
     * one, without them (handed neither: a small TimeChart draws no
     * episodes, and flags only when handed them), on the Overview.
     */
    TimeChart rateChart(HistorySave h, ChartModel window, boolean main, double width, double height) {
        List<TimeChart.Line> lines = List.of(
                new TimeChart.Line("fxRate", "the rate", Palette.MONEY, 0, h.aligned("fxRate"),
                        v -> HistoryScreen.plotScale("rate", v), v -> perUsd(v), ""),
                new TimeChart.Line("fxParity", "parity", Palette.MONEY_LIGHT, 0, h.aligned("fxParity"),
                        v -> HistoryScreen.plotScale("rate", v), v -> perUsd(v), ""));
        TimeChart chart = new TimeChart(window, main ? rateHidden : Set.of(), main);
        List<YearBook.Episode> crises = new ArrayList<>();
        if (main) for (YearBook.Episode e : ChartModel.episodes(h)) if ("currency".equals(e.kind())) crises.add(e);
        chart.setData(h.getMonth(), lines, HistoryScreen.axisFor("rate"), null, false, false, null, List.of(),
                crises, main && !h.getMonth().isEmpty()
                        ? ChartModel.onAxis(ChartModel.flags(ui.game.getDecisions(), DecisionLog.CURRENCY), h.getMonth().get(0))
                        : List.of(),
                "no months recorded yet");
        chart.setSize(width, height);
        if (main) chart.withoutFullScreen();
        return chart;
    }

    /* ------------------------------ the three gauges ------------------------------ */

    /**
     * One gauge's words, worked out without drawing it (pure: the probe
     * reads them): its reading, its chip and the chip's colour (null: none),
     * where its mark sits (0 to 1), and whether the reading does not apply.
     */
    record Gauge(String reading, String chip, String chipTone, double at, boolean muted) { }

    /** Import cover on 0 to 12 months: red under THIN_COVER "crisis pricing", amber under COMFORTABLE_COVER "thin", green "comfortable" (pure). */
    Gauge cover() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        if (!(fx.monthlyImports() > 0)) return new Gauge("nothing bought yet", null, null, 0, true);
        double months = fx.importCover();
        int level = ForeignAccounts.coverLevel(months);
        return new Gauge(coverMonths(months), level == 2 ? "crisis pricing" : level == 1 ? "thin" : "comfortable",
                level == 2 ? Palette.BAD : level == 1 ? Palette.WARN : Palette.GOOD,
                Math.min(1, months / COVER_SCALE), false);
    }

    /** The cover gauge's scale: twice the comfortable line, a year of imports. */
    static final double COVER_SCALE = ForeignAccounts.COMFORTABLE_COVER * 2;

    /** What backs the money that can leave, on 0 to 200%: red under PANIC_BACKING "a run likely", amber under 100% "thin", green "covered" (pure). */
    Gauge backing() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        CapitalFlows hot = ui.game.getCapitalFlows();
        if (!(hot.getStock() > 0)) return new Gauge("nothing parked here", null, null, 0, true);
        double backing = fx.getReserves() / hot.getStock();
        return new Gauge(backing >= 5 ? "over 500%" : String.format("%.0f%%", backing * 100),
                backing < CapitalFlows.PANIC_BACKING ? "a run likely" : backing < 1 ? "thin" : "covered",
                backing < CapitalFlows.PANIC_BACKING ? Palette.BAD : backing < 1 ? Palette.WARN : Palette.GOOD,
                Math.min(1, backing / 2), false);
    }

    /** How far the rate has run from parity, either side, on 0 to 1.5 times PARITY_FAR: "near" plain, "far" amber past PARITY_WATCH, "very far" red past PARITY_FAR (D8; pure). */
    Gauge parity() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        if (fx.isPinned()) return new Gauge("held fixed", "pinned", Palette.TEXT_MUTED, 0, true);
        double dev = fx.deviationFromParity();
        int level = ForeignAccounts.parityLevel(dev);
        return new Gauge(share1(Math.abs(dev)) + (dev > 0 ? " weaker" : dev < 0 ? " stronger" : ""),
                level == 2 ? "very far" : level == 1 ? "far" : "near",
                level == 2 ? Palette.BAD : level == 1 ? Palette.WARN : Palette.TEXT_MUTED,
                Math.min(1, Math.abs(dev) / PARITY_SCALE), false);
    }

    /** The parity gauge's scale either side: half as far again as PARITY_FAR, so the red band shows. */
    static final double PARITY_SCALE = ForeignAccounts.PARITY_FAR * 1.5;

    VBox coverGauge() {
        Gauge g = cover();
        return gaugeCard(Icons.SAFE, Palette.MONEY, "IMPORT COVER", COVER_INFO, g.reading(), g.muted() ? Palette.TEXT_SPENT : null,
                g.chip(), g.chipTone(), g.at(),
                new double[] {ForeignAccounts.THIN_COVER / COVER_SCALE, ForeignAccounts.COMFORTABLE_COVER / COVER_SCALE, 1},
                new String[] {Palette.BAD, Palette.WARN, Palette.GOOD}, null,
                String.format("crisis pricing under %.0f · comfortable over %.0f months",
                        ForeignAccounts.THIN_COVER, ForeignAccounts.COMFORTABLE_COVER),
                g.muted(), () -> open(RESERVES, COVER));
    }

    VBox backingGauge() {
        Gauge g = backing();
        return gaugeCard(Icons.BANKNOTE, Palette.MONEY, "BACKING FOR THE MONEY THAT CAN LEAVE", BACKING_INFO, g.reading(),
                g.muted() ? Palette.TEXT_SPENT : null, g.chip(), g.chipTone(), g.at(),
                new double[] {CapitalFlows.PANIC_BACKING / 2, .5, 1},
                new String[] {Palette.BAD, Palette.WARN, Palette.GOOD}, null,
                String.format("a run becomes likely under %.0f%%", CapitalFlows.PANIC_BACKING * 100),
                g.muted(), () -> open(RESERVES, LEAVE));
    }

    VBox parityGauge() {
        Gauge g = parity();
        return gaugeCard(Icons.EXCHANGE, Palette.MONEY, "HOW FAR THE RATE HAS RUN FROM PARITY", PARITY_INFO, g.reading(),
                g.muted() ? Palette.TEXT_SPENT : null, g.chip(), g.chipTone(), g.at(),
                new double[] {ForeignAccounts.PARITY_WATCH / PARITY_SCALE, ForeignAccounts.PARITY_FAR / PARITY_SCALE, 1},
                new String[] {Palette.TEXT_SPENT, Palette.WARN, Palette.BAD}, null,
                "the basket pulls it back, slowly, however far it goes",
                g.muted(), () -> open(CURRENCY));
    }

    /** The gauges' (i)s: what each one measures (the cover sentence, the spec's C1; the others the landing's notes). */
    static final String COVER_INFO = String.format("Import cover is the oldest test there is: if every dollar of "
            + "earnings stopped tomorrow, how long could the city go on buying? %.0f months is the comfortable line in "
            + "this model - from there the vault damps a push on the currency as much as it ever can - and %.0f is "
            + "where the world stops pricing a currency on its trade balance and starts pricing it for a crisis.",
            ForeignAccounts.COMFORTABLE_COVER, ForeignAccounts.THIN_COVER);
    /** ...the backing gauge's: the vault against the money that can leave, and where a run becomes likely. */
    static final String BACKING_INFO = String.format("The vault against the foreign money parked in the city's bank, "
            + "which can leave on no notice. Under %.0f%% the next shock stops being a wobble and becomes a run.",
            CapitalFlows.PANIC_BACKING * 100);
    /** ...and the parity gauge's: the one parity rule's two lines, and where else they are read. */
    static final String PARITY_INFO = String.format("How far the rate sits from parity - where a basket costs the same "
            + "here and abroad - either side. Past %.0f%% it reads amber, past %.0f%% red, here, in the drawer's THE "
            + "CURRENCY row and on the header's rate line alike. The basket pulls it back, slowly, however far it goes.",
            ForeignAccounts.PARITY_WATCH * 100, ForeignAccounts.PARITY_FAR * 100);

    /* =====================================================================
       THE MONTH (0.7.35; "The picture" and "The two accounts" until then)

       Did more come in than went out, and through which account? A
       waterfall from what the city sold abroad to the month's balance:
       sold, bought, the trade balance, the income from abroad, the current
       account, capital in and out, the month - the two accounts as the
       totals between them, which a river cannot show. The river is one
       toggle away (the spec's D2), because it is the one picture of
       conservation: every dollar that came in went somewhere. Under it,
       what the city holds abroad and what the world holds here - the
       stocks the flows add up to. The statement is under "details".
       ===================================================================== */

    void monthPage(VBox page) {
        page.getChildren().add(monthHero());
        VBox holdings = holdingsCard();
        targets.put(HOLDINGS, holdings);
        page.getChildren().add(holdings);
        page.getChildren().add(fold("month:ledger", "the balance of payments, as the statement it was",
                () -> ledger()));
    }

    /** The walk worked out without drawing it (pure: the probe reads it): its steps, and the steps at nothing named in a line. */
    record Walk(List<Step> steps, List<String> nothing) { }

    /** Under half a dollar is nothing: a step that small is named, not drawn. */
    static final double NOTHING = .0005;

    /**
     * THE MONTH'S STEPS (pure): sold abroad up, bought abroad down, the
     * trade balance; the income from abroad, net - up in the money blue when
     * the city received more than it paid, down in its darker step when it
     * paid more (the spec's B2 and B3: the river dropped it, the ledger
     * called it paid whichever way it went); the current account; capital
     * in and out in the light step; and the month. Totals are the model's
     * own figures drawn from zero, in the spent grey.
     */
    Walk monthWalk() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        Sectors.TradeByGood t = ui.game.getTradeByGood();
        BondMarket market = ui.game.getBondMarket();
        List<Step> steps = new ArrayList<>();
        List<String> nothing = new ArrayList<>();
        step(steps, nothing, "sold abroad", fx.getExports(), Palette.BUSINESS,
                "Sold abroad\n" + dFull(fx.getExports()) + topGoods(t, true));
        step(steps, nothing, "bought abroad", -fx.tradeImports(), Palette.BUSINESS_DARK,
                "Bought abroad\n" + dFull(fx.tradeImports()) + topGoods(t, false));
        steps.add(Step.total("trade balance", fx.tradeBalance(), Palette.TEXT_SPENT)
                .tip("The trade balance\n" + signedFull(fx.tradeBalance()) + "\nwhat it sold abroad less what it bought"));
        double income = -fx.getForeignInterest();
        step(steps, nothing, incomeName(income), income, income >= 0 ? Palette.MONEY : Palette.MONEY_DARK,
                incomeName(income) + "\n" + signedFull(income) + "\n" + INCOME_INFO);
        steps.add(Step.total("current account", fx.currentAccount(), Palette.TEXT_SPENT)
                .tip("The current account\n" + signedFull(fx.currentAccount()) + "\n" + CURRENT_INFO));
        step(steps, nothing, "capital in", fx.getFinancialIn(), Palette.MONEY_LIGHT,
                "Capital in\n" + dFull(fx.getFinancialIn()) + "\nborrowed abroad, money parked here, the world buying "
                        + "the businesses' bonds (" + dFull(market.getWorldPurchases()) + " this month)");
        step(steps, nothing, "capital out", -fx.getFinancialOut(), Palette.MONEY_LIGHT,
                "Capital out\n" + dFull(fx.getFinancialOut()) + "\nrepaid abroad, money going home, savings invested "
                        + "abroad, the world selling the businesses' bonds (" + dFull(market.getWorldSales()) + " this month)");
        steps.add(Step.total("the month", fx.balance(), Palette.TEXT_SPENT)
                .tip("The month's balance\n" + signedFull(fx.balance()) + "\n" + (fx.balance() >= 0 ? SURPLUS_INFO : DEFICIT_INFO)));
        return new Walk(steps, nothing);
    }

    /** A plain step, or its name in the line of nothing when it is under NOTHING. */
    static void step(List<Step> steps, List<String> nothing, String name, double amount, String colour, String tip) {
        if (!(Math.abs(amount) >= NOTHING)) { nothing.add(name); return; }
        steps.add(Step.of(name, amount, colour).tip(tip));
    }

    /** The income line's name follows its sign (B3): received, or paid. */
    static String incomeName(double received) {
        return received >= 0 ? "income from abroad, net" : "interest and dividends abroad";
    }

    /** "+D$1,234,567" or "−D$…", every digit. */
    String signedFull(double thousands) {
        String shown = moneyFull(Math.abs(thousands));
        if ("$0".equals(shown)) return marked(sym(), shown);
        return (thousands < 0 ? "−" : "+") + marked(sym(), shown);
    }

    /** The two goods that moved most one way, for a tooltip and the river's note (the spec's B8: the words were static and untrue). */
    String topGoods(Sectors.TradeByGood t, boolean sold) {
        List<Sectors.GoodTrade> goods = new ArrayList<>(t.goods().values());
        goods.sort((a, b) -> Double.compare(sold ? b.sold() : b.bought(), sold ? a.sold() : a.bought()));
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < goods.size() && i < 2; i++) {
            double v = sold ? goods.get(i).sold() : goods.get(i).bought();
            if (!(v > 0)) break;
            s.append(s.length() == 0 ? "\nmostly " : ", ").append(goods.get(i).good().label().toLowerCase())
                    .append(' ').append(d(v));
        }
        return s.toString();
    }

    /** The current account's note (the spec's A1). */
    static final String CURRENT_INFO = "What the city earned from the world by selling it things, less what it spent buying "
            + "things, and what its paper abroad earned or cost it. This is the line a country is judged on: it can be "
            + "negative for years and it cannot be negative for ever.";

    /** The financial account's note (A2). */
    static final String FINANCIAL_INFO = "Borrowing abroad and foreign money parking here are both inflows, and neither is "
            + "income. A country running a current-account deficit funded by capital inflows is borrowing to buy — which "
            + "works until the money decides to go home.";

    /** The income line's note (B3). */
    static final String INCOME_INFO = "Interest and dividends: what the businesses' and the households' paper abroad paid "
            + "them, less what the city paid on its dollar paper, the world's deposits and the shares and bonds the world "
            + "holds here. Net, as the accounts strike it.";

    /** The month's closing words (the spec's D17). */
    static final String SURPLUS_INFO = "A surplus month: the world owes the city a little more than it did, and that is what "
            + "makes the next dollar of borrowing cheaper. It lands with the firms and households that earned it; the vault "
            + "moves only when the treasury buys or sells.";
    /** ...and a deficit month's. */
    static final String DEFICIT_INFO = "A deficit month has to be settled in somebody else's money: out of the vault, or by "
            + "borrowing abroad, or by the currency falling until the trade balance closes it.";

    /**
     * THE MONTH ACROSS THE EDGE: the two accounts as chips at the right, the
     * toggle "steps · river", the picture, the month in one sentence, and the
     * treasury's hand and the claims written off as chips when there were
     * any. Before a month is counted (a founding, or an older save's load), one line says so.
     */
    VBox monthHero() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        FlowPane toggle = chipStrip(new String[] {"steps", "river"}, showRiver ? "river" : "steps", Palette.SIZE_LABEL,
                pick -> {
                    showRiver = "river".equals(pick);
                    showForeignMenu();
                });
        toggle.setAlignment(Pos.CENTER_RIGHT);
        HBox right = new HBox(Palette.GAP);
        right.setAlignment(Pos.CENTER_RIGHT);
        if (fx.isMonthCounted()) {
            right.getChildren().addAll(
                    chipInfo("current account " + signedD(fx.currentAccount()), Palette.TEXT_LABEL, CURRENT_INFO),
                    chipInfo("financial account " + signedD(fx.financialAccount()), Palette.TEXT_LABEL, FINANCIAL_INFO));
        }
        right.getChildren().add(toggle);
        VBox c = card(cardHead(Icons.COIN, Palette.MONEY, "THE MONTH ACROSS THE EDGE", MONTH_INFO, right));
        if (!fx.isMonthCounted()) {
            c.getChildren().add(line("Not counted yet: the balance of payments is struck when a month ends, and none "
                    + "has ended since the city was founded or loaded from a save before 0.7.46.", NOT_SAVED_INFO));
            return c;
        }
        if (showRiver) {
            River r = river();
            if (r.in().isEmpty() && r.out().isEmpty()) {
                c.getChildren().add(line("Nothing crossed the city's edge this month.", NOTHING_CROSSED));
                return c;
            }
            c.getChildren().add(bopRiver(r.in(), r.out(), 1180, 260, 160));
        } else {
            Walk w = monthWalk();
            c.getChildren().add(waterfall(w.steps(), this::signedD, 0, 250));
            if (!w.nothing().isEmpty()) c.getChildren().add(muted("nothing this month: " + String.join(" · ", w.nothing())));
        }
        double balance = fx.balance();
        c.getChildren().add(line(balance >= 0
                        ? String.format("The city took in %s more than it paid out.", d(balance))
                        : String.format("The city paid out %s more than it took in.", d(-balance)),
                balance >= 0 ? SURPLUS_INFO : DEFICIT_INFO));
        List<Node> hand = new ArrayList<>();
        if (fx.getBoughtThisMonth() > 0) {
            hand.add(chipInfo("the treasury bought " + d(fx.getBoughtThisMonth()) + " of foreign money", Palette.MONEY, HAND_INFO));
        }
        if (fx.getSoldThisMonth() > 0) {
            hand.add(chipInfo("the treasury sold " + d(fx.getSoldThisMonth()) + " of foreign money", Palette.MONEY, HAND_INFO));
        }
        if (Math.abs(fx.valuationChange()) > .005) {
            hand.add(chipInfo("claims written off " + d(Math.abs(fx.valuationChange())), Palette.TEXT_LABEL,
                    String.format(VALUATION_INFO, d(Math.abs(fx.valuationChange())))));
        }
        if (!hand.isEmpty()) c.getChildren().add(chips(hand.toArray(new Node[0])));
        return c;
    }

    /** The hero's (i). */
    static final String MONTH_INFO = "Every dollar that crossed the city's edge this month, and which way. Steps: each "
            + "column a line of the balance of payments - a step up is money in, a step down money out, and a column from "
            + "the axis a total, the model's own figure: the trade balance, the current account and the month. River: "
            + "the same month as one conserved flow, sources at the left and uses at the right, a band's width its money.";

    /** Why a city reads nothing yet (the spec's B1): founded, or loaded from a save before 0.7.46, which did not carry the month's flows (D4, built as A2). */
    static final String NOT_SAVED_INFO = "The month's flows across the edge - what was sold and bought abroad, the income, "
            + "the capital, the treasury's purchases - are struck at the end of each month. A save keeps them since 0.7.46; "
            + "a city just founded, or loaded from an older save, has none until its first month ends. The businesses' own "
            + "books are saved: What we trade shows the month the city was saved in, good by good.";

    /** The treasury's hand (the spec's A6). */
    static final String HAND_INFO = "Below the line, and deliberately: an intervention does not earn or spend anything "
            + "abroad, it swaps one currency for another. It is the financing item, which is what makes it the last resort "
            + "rather than the first - so it is not on the steps or the river.";

    /** The valuation change (M5, A4; D15: a chip, not an alert). */
    static final String VALUATION_INFO = "%s of foreign claims were written off this month. It improves what the city owes "
            + "the world and it is NOT a dollar earned — nobody paid it, so it never crossed the edge and it is not drawn "
            + "above. It changes the position, which is what a valuation change is.";

    /* ------------------------------ the river ------------------------------ */

    /** One band of the river: its name, its note, its money and its colour. */
    record Flow(String name, String note, double amount, String colour) { }

    /** The river's two sides, worked out without drawing it (pure: the probe reads them). */
    record River(List<Flow> in, List<Flow> out) { }

    /**
     * THE RIVER'S BANDS (pure). In: sold abroad, the income from abroad when
     * the city received more than it paid (B2: it was dropped, and a
     * +D$183.7M month drew a red D$120.5M deficit), capital in. Out: bought
     * abroad, the income paid when it paid more, capital out. Then the band
     * that balances them, which is the month's surplus or deficit - THE
     * MONTH, to the dollar. The treasury's purchases and sales are the
     * financing item below the line, so they are chips under the picture,
     * not bands (the spec's D17). Colours by kind, never a verdict (B7);
     * the goods named live from the month's books (B8).
     */
    River river() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        Sectors.TradeByGood t = ui.game.getTradeByGood();
        List<Flow> in = new ArrayList<>(), out = new ArrayList<>();
        String soldNote = topGoods(t, true).replace("\nmostly ", ""), boughtNote = topGoods(t, false).replace("\nmostly ", "");
        if (fx.getExports() > 0) in.add(new Flow("Sold abroad", soldNote, fx.getExports(), Palette.BUSINESS));
        double income = -fx.getForeignInterest();
        if (income > 0) in.add(new Flow("Income from abroad, net", "interest and dividends", income, Palette.MONEY));
        if (fx.getFinancialIn() > 0) in.add(new Flow("Capital in", "borrowed abroad, and money parked here",
                fx.getFinancialIn(), Palette.MONEY_LIGHT));
        if (fx.tradeImports() > 0) out.add(new Flow("Bought abroad", boughtNote, fx.tradeImports(), Palette.BUSINESS_DARK));
        if (income < 0) out.add(new Flow("Paid abroad, net", "interest and dividends", -income, Palette.MONEY_DARK));
        if (fx.getFinancialOut() > 0) out.add(new Flow("Capital out", "repaid abroad, and money going home",
                fx.getFinancialOut(), Palette.MONEY_LIGHT));
        double balance = fx.balance();
        if (balance < -.005) in.add(new Flow("The month's deficit", "settled abroad", -balance, Palette.TEXT_SPENT));
        else if (balance > .005) out.add(new Flow("The month's surplus", "held by those who earned it", balance, Palette.TEXT_SPENT));
        return new River(in, out);
    }

    /**
     * The river itself (the Trade tab's since 0.6.x, at the page's width
     * since 0.7.35).
     *
     * THREE STAGES, not two. A two-column Sankey needs a mapping from each
     * source to each use, and there is none here - exports do not
     * specifically pay for imports, they pay for whatever the city bought.
     * So every source merges into one trunk and the trunk splits into every
     * use, which is both the honest structure and the one that draws "the
     * month" as a single quantity. The ribbons are cubic paths with equal
     * heights at both ends, because the amount does not change on the way
     * across; the closing band is outlined, the difference rather than a
     * flow of its own kind.
     */
    VBox bopRiver(List<Flow> in, List<Flow> out, double W, double H, double LABEL) {
        final double BAR = 11;            // the source and use bars
        final double TRUNK = 52;          // the city in the middle
        final double GAP = 3;             // between stacked bands

        double sum = 0;
        for (Flow f : in) sum += f.amount();
        if (sum <= 0) return new VBox();

        int bands = Math.max(in.size(), out.size());
        double usable = H - GAP * Math.max(0, bands - 1);
        double scale = usable / sum;

        double leftBarX  = LABEL;
        double trunkX0   = W / 2 - TRUNK / 2;
        double trunkX1   = W / 2 + TRUNK / 2;
        double rightBarX = W - LABEL - BAR;

        javafx.scene.layout.Pane face = new javafx.scene.layout.Pane();
        face.setPrefSize(W, H);
        face.setMinSize(W, H);
        face.setMaxSize(W, H);

        Region trunk = new Region();
        trunk.setPrefSize(TRUNK, H);
        trunk.setMinSize(TRUNK, H);
        trunk.setStyle("-fx-background-color: " + Palette.PINNED + "; -fx-background-radius: 3;");
        trunk.setLayoutX(trunkX0);
        trunk.setLayoutY(0);
        face.getChildren().add(trunk);

        // A LABEL PER BAND, PUSHED APART: a band worth $347k beside one worth
        // $46M is two pixels tall, and its name would land on its neighbour's.
        double sourceY = 0, trunkInY = 0, labelFloor = 0;
        for (Flow f : in) {
            double h = Math.max(2, f.amount() * scale);
            face.getChildren().add(ribbon(leftBarX + BAR, sourceY, trunkX0, trunkInY, h, f.colour()));
            face.getChildren().add(riverBar(f, leftBarX, sourceY, BAR, h));
            double at = Math.max(labelFloor, sourceY + h / 2 - 14);
            face.getChildren().add(bandLabel(f, 0, at, LABEL - 8, true));
            labelFloor = at + 28;
            sourceY += h + GAP;
            trunkInY += h + GAP;
        }
        double useY = 0, trunkOutY = 0;
        labelFloor = 0;
        for (Flow f : out) {
            double h = Math.max(2, f.amount() * scale);
            face.getChildren().add(ribbon(trunkX1, trunkOutY, rightBarX, useY, h, f.colour()));
            face.getChildren().add(riverBar(f, rightBarX, useY, BAR, h));
            double at = Math.max(labelFloor, useY + h / 2 - 14);
            face.getChildren().add(bandLabel(f, rightBarX + BAR + 8, at, LABEL - 8, false));
            labelFloor = at + 28;
            useY += h + GAP;
            trunkOutY += h + GAP;
        }

        Label here = new Label("THE\nCITY");
        here.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED) + " -fx-text-alignment: center;");
        here.setLayoutX(trunkX0 + 6);
        here.setLayoutY(H / 2 - 14);
        face.getChildren().add(here);

        Label inHead = new Label("CAME IN   " + d(sum));
        inHead.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Region spread = new Region();
        HBox.setHgrow(spread, Priority.ALWAYS);
        Label outHead = new Label(d(sum) + "   WENT OUT");
        outHead.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        HBox heads = new HBox(inHead, spread, outHead);
        heads.setMaxWidth(W);
        heads.setPrefWidth(W);

        VBox box = new VBox(4, heads, face, noteLine("Band width is money; the two sides balance.", RIVER_INFO, W));
        box.setMaxWidth(W);
        return box;
    }

    /** The river's foot (the spec's section 4: one line, the rest in the (i), no colour named). */
    static final String RIVER_INFO = "Band width is money. The two sides balance because they must: what came in and what "
            + "went out are the same money seen twice. The outlined band is the difference - the month's surplus on the "
            + "right, where it went, or its deficit on the left, where it came from - and it is THE MONTH to the dollar.";

    /** One source or use bar, with its tooltip; the closing band outlined. */
    Region riverBar(Flow f, double x, double y, double w, double h) {
        Region bar = new Region();
        bar.setPrefSize(w, h);
        bar.setMinSize(w, h);
        boolean closing = Palette.TEXT_SPENT.equals(f.colour());
        bar.setStyle(closing
                ? "-fx-background-color: transparent; -fx-border-color: " + Palette.TEXT_LABEL + "; -fx-border-width: 1;"
                : "-fx-background-color: " + f.colour() + ";");
        bar.setLayoutX(x);
        bar.setLayoutY(y);
        Tooltip.install(bar, new Tooltip(f.name() + "\n" + dFull(f.amount()) + "\n" + f.note()));
        return bar;
    }

    /**
     * One ribbon: a filled cubic band of constant height between two columns.
     * Both ends are the same height because the amount does not change on the
     * way across - the whole claim a Sankey makes.
     */
    javafx.scene.shape.Path ribbon(double x0, double y0, double x1, double y1, double h, String colour) {
        double mid = (x0 + x1) / 2;
        javafx.scene.shape.Path band = new javafx.scene.shape.Path();
        band.getElements().addAll(
                new javafx.scene.shape.MoveTo(x0, y0),
                new javafx.scene.shape.CubicCurveTo(mid, y0, mid, y1, x1, y1),
                new javafx.scene.shape.LineTo(x1, y1 + h),
                new javafx.scene.shape.CubicCurveTo(mid, y1 + h, mid, y0 + h, x0, y0 + h),
                new javafx.scene.shape.ClosePath());
        band.setFill(javafx.scene.paint.Color.web(colour));
        band.setStroke(null);
        // Translucent, so a ribbon crossing another still reads as two ribbons.
        band.setOpacity(.55);
        return band;
    }

    /** A band's name and figure, beside its bar; the name wraps rather than end in "…". */
    VBox bandLabel(Flow f, double x, double y, double width, boolean rightAlign) {
        Label name = new Label(f.name());
        name.setWrapText(true);
        name.setMaxWidth(width);
        name.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_BODY)
                + (rightAlign ? " -fx-text-alignment: right;" : ""));
        Label figure = new Label(d(f.amount()));
        figure.setStyle(Palette.figure(Palette.SIZE_CAPTION,
                Palette.TEXT_SPENT.equals(f.colour()) ? Palette.TEXT_LABEL : f.colour()));
        VBox box = new VBox(-2, name, figure);
        box.setPrefWidth(width);
        box.setMinWidth(width);
        box.setMaxWidth(width);
        box.setAlignment(rightAlign ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        box.setLayoutX(x);
        box.setLayoutY(Math.max(0, y));
        return box;
    }

    /* ------------------------------ what is held where ------------------------------ */

    /**
     * WHAT THE CITY HOLDS ABROAD · WHAT THE WORLD HOLDS HERE: two bars on one
     * scale - abroad, the businesses' paper, the households' and the vault;
     * here, the city's shares in the world's hands, the businesses' bonds it
     * holds, the money parked in the bank and the city's dollar paper - each
     * part with its local and its dollar figure in the key under them.
     * Stocks, saved, so they read the same just after a load.
     */
    VBox holdingsCard() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        OutwardInvestment firms = ui.game.getOutwardInvestment();
        HouseholdBalance savers = ui.game.getHouseholdBalance();
        double shares = ui.game.getSharesHeldAbroad();
        double bonds = ui.game.getBondMarket().faceHeldByWorld();
        double parked = ui.game.getCapitalFlows().getStock();
        double owed = fx.getForeignDebt();
        double abroad = ui.game.getHeldAbroad(), here = ui.game.getHeldHereByTheWorld();
        String sand = Palette.CATEGORIES[4];
        List<Segment> top = List.of(
                seg(firms.totalLocalValue(), Palette.BUSINESS, "the businesses' paper abroad\n" + dFull(firms.totalLocalValue())),
                seg(savers.totalAbroadValue(), Palette.PEOPLE, "the households' paper abroad\n" + dFull(savers.totalAbroadValue())),
                seg(fx.getReserves(), Palette.MONEY, "the vault\n" + dFull(fx.getReserves())));
        List<Segment> under = List.of(
                seg(shares, Palette.BUSINESS_LIGHT, "the city's shares in the world's hands\n" + dFull(shares)),
                seg(bonds, Palette.BUSINESS_DARK, "the businesses' bonds the world holds, at face\n" + dFull(bonds)),
                seg(parked, sand, "foreign money parked in the bank\n" + dFull(parked)),
                seg(owed, Palette.MONEY_DARK, "the city's dollar paper\n" + dFull(owed)));
        double scale = Math.max(abroad, here);
        VBox c = card(cardHead(Icons.TRADE, Palette.MONEY, "WHAT THE CITY HOLDS ABROAD · WHAT THE WORLD HOLDS HERE",
                HOLDINGS_INFO, null));
        if (!(scale > 0)) {
            c.getChildren().add(line("Nothing is held abroad and the world holds nothing here.", null));
            return c;
        }
        c.getChildren().add(balanceBars("Held abroad " + d(abroad), top, null, "Held here by the world " + d(here), under,
                null, scale, 200, 18));
        GridPane key = equalColumns(2, TILE_GAP);
        VBox left = new VBox(4,
                keyLine(Palette.BUSINESS, "the businesses' paper", d(firms.totalLocalValue()) + " · " + usd(firms.totalUsd())),
                keyLine(Palette.PEOPLE, "the households' paper", d(savers.totalAbroadValue()) + " · " + usd(savers.totalAbroadUsd())),
                keyLine(Palette.MONEY, "the vault", d(fx.getReserves()) + " · " + usd(fx.getReservesUsd())));
        VBox right = new VBox(4,
                keyLine(Palette.BUSINESS_LIGHT, "the city's shares", d(shares)),
                keyLine(Palette.BUSINESS_DARK, "the businesses' bonds", d(bonds)),
                keyLine(sand, "money parked in the bank", d(parked)),
                keyLine(Palette.MONEY_DARK, "the city's dollar paper", d(owed) + " · " + usd(fx.getForeignDebtUsd())));
        key.add(left, 0, 0);
        key.add(right, 1, 0);
        c.getChildren().add(key);
        return c;
    }

    /** A key's row: its swatch, its name, and its figures at the right. */
    static HBox keyLine(String colour, String name, String figures) {
        HBox sw = swatch(colour, name);
        HBox.setHgrow(sw, Priority.ALWAYS);
        sw.setMaxWidth(Double.MAX_VALUE);
        HBox row = new HBox(Palette.GAP, sw, figure(figures, Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** The holdings' (i) (the spec's A5). */
    static final String HOLDINGS_INFO = "The stocks the flows add up to - the rough shape of an international investment "
            + "position. Idle money buys the world's paper when the world pays more than the bank, and comes home when the "
            + "bank pays more or its owner needs it - the businesses' tills and the households' savings by the same rule. "
            + "The coupon is rolled where it is earned; it reaches the currency only when the money comes home. The city's "
            + "shares in foreign hands are at their price, the last trade; the businesses' bonds at face.";

    /* ------------------------------ the statement, under details ------------------------------ */

    /**
     * THE BALANCE OF PAYMENTS, as the statement it was (the old "The two
     * accounts" page, verbatim but for these: the income line's label
     * follows its sign, and the current account's note counts that line
     * either way (B3); every line is plain - sold, bought, capital out
     * and what is held abroad were green and amber whatever they said (B7);
     * a negative is written with a true minus; and before a month is
     * counted, a line says so).
     */
    VBox ledger() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        VBox column = column();
        if (!fx.isMonthCounted()) column.getChildren().add(statementNote("Not counted yet: the figures "
                + "below are the month's flows, which a month on will read again."));
        column.getChildren().add(statementHead("The current account"));
        column.getChildren().add(statementLine("Sold abroad", signedTight(fx.getExports(), false), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("Bought abroad", signedTight(fx.tradeImports(), true), Palette.TEXT_HEAD));
        column.getChildren().add(statementTotal("TRADE BALANCE", signedTight(fx.tradeBalance(), false), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine(fx.getForeignInterest() > 0
                        ? "Paid abroad in interest and dividends, net" : "Received from abroad in interest and dividends, net",
                signedTight(fx.getForeignInterest(), true), Palette.TEXT_HEAD));
        column.getChildren().add(statementTotal("CURRENT ACCOUNT", signedTight(fx.currentAccount(), false), Palette.TEXT_HEAD));
        column.getChildren().add(statementNote(CURRENT_INFO));

        column.getChildren().add(statementHead("The financial account"));
        column.getChildren().add(statementLine("Capital in", signedTight(fx.getFinancialIn(), false), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("Capital out", signedTight(fx.getFinancialOut(), true), Palette.TEXT_HEAD));
        column.getChildren().add(statementTotal("FINANCIAL ACCOUNT", signedTight(fx.financialAccount(), false), Palette.TEXT_HEAD));
        column.getChildren().add(statementNote(FINANCIAL_INFO));

        // The businesses' bonds and the world (0.7.12): what it paid for them
        // arrives as capital above, what they pay it leaves as income, and what
        // a default took off them is below the line with the claims written off.
        BondMarket market = ui.game.getBondMarket();
        if (market.faceHeldByWorld() > 0 || market.getWorldPurchases() > 0 || market.getWorldSales() > 0
                || market.getCouponsAbroad() > 0) {
            column.getChildren().add(statementHead("The world and the businesses' bonds"));
            column.getChildren().add(statementLine("It bought them, this month",
                    signedTight(market.getWorldPurchases(), false), Palette.TEXT_HEAD));
            column.getChildren().add(statementLine("...and sold them", signedTight(market.getWorldSales(), true), Palette.TEXT_HEAD));
            column.getChildren().add(statementLine("Their coupons, paid abroad",
                    signedTight(market.getCouponsAbroad(), true), Palette.TEXT_HEAD));
            if (market.getPrincipalAbroad() > 0) {
                column.getChildren().add(statementLine("...and their face, repaid abroad",
                        signedTight(market.getPrincipalAbroad(), true), Palette.TEXT_HEAD));
            }
            if (market.getWorldWrittenOff() > 0) {
                column.getChildren().add(statementLine("...and what defaults took off them, which it lost",
                        money(market.getWorldWrittenOff()), Palette.TEXT_MUTED));
            }
            column.getChildren().add(statementLine("What it holds of them",
                    money(market.faceHeldByWorld()) + " of face", Palette.TEXT_HEAD));
            column.getChildren().add(statementNote(
                    "The world buys a business's bonds when what they pay, less what it expects to lose on "
                    + "them, beats the world's rate and what it charges the city for its risk - the rule hot "
                    + "money follows - and sells them when that goes, all at once when the money runs. What it "
                    + "pays arrives as capital, which the currency sees; the coupons leave as income."));
        }

        column.getChildren().add(statementHead("The two together"));
        column.getChildren().add(statementTotal("THE MONTH'S BALANCE", signedTight(fx.balance(), false), Palette.TEXT_HEAD));
        if (Math.abs(fx.valuationChange()) > .005) {
            column.getChildren().add(statementLine("Claims written off", signedTight(fx.valuationChange(), false),
                    Palette.TEXT_MUTED));
            column.getChildren().add(statementNote(
                    "What foreign creditors gave up on. It improves the position and it is "
                    + "not a dollar earned, so it is below the line and out of the river."));
        }

        OutwardInvestment sectorsAbroad = ui.game.getOutwardInvestment();
        HouseholdBalance savers = ui.game.getHouseholdBalance();
        double sharesAbroad = ui.game.getSharesHeldAbroad();
        if (sectorsAbroad.totalUsd() > 0 || savers.totalAbroadUsd() > 0 || sharesAbroad > 0) {
            column.getChildren().add(statementHead("What the city holds abroad, and what the world holds here"));
            column.getChildren().add(statementLine("The businesses' paper abroad",
                    money(sectorsAbroad.totalLocalValue()) + " (US" + money(sectorsAbroad.totalUsd()) + ")", Palette.TEXT_HEAD));
            column.getChildren().add(statementLine("The households' paper abroad",
                    money(savers.totalAbroadValue()) + " (US" + money(savers.totalAbroadUsd()) + ")", Palette.TEXT_HEAD));
            column.getChildren().add(statementLine("The city's shares in foreign hands",
                    "−" + money(sharesAbroad), Palette.TEXT_HEAD));
            column.getChildren().add(statementNote(
                    "Idle money buys the world's paper when the world pays more than the bank, and "
                    + "comes home when the bank pays more or its owner needs it - the businesses' "
                    + "tills and the households' savings by the same rule. The coupon is rolled where "
                    + "it is earned; it reaches the currency only when the money comes home."));
        }

        if (fx.getBoughtThisMonth() > 0 || fx.getSoldThisMonth() > 0) {
            column.getChildren().add(statementHead("...and what the treasury did about it"));
            if (fx.getBoughtThisMonth() > 0) {
                column.getChildren().add(statementLine("Bought foreign money", money(fx.getBoughtThisMonth()), Palette.TEXT_HEAD));
            }
            if (fx.getSoldThisMonth() > 0) {
                column.getChildren().add(statementLine("Sold foreign money", money(fx.getSoldThisMonth()), Palette.TEXT_HEAD));
            }
            column.getChildren().add(statementNote(
                    "Below the line, and deliberately: an intervention does not earn or "
                    + "spend anything abroad, it swaps one currency for another. It is the "
                    + "financing item, which is what makes it the last resort rather than "
                    + "the first."));
        }
        return column;
    }

    /* =====================================================================
       WHAT WE TRADE (0.7.35; "In and out" and "Since founding" until then)

       What does the city sell abroad, and what does it buy? Every good
       that crossed the edge as mirrored bars - bought to the left, sold to
       the right, the net at the end - or every business (the spec's D19),
       off the businesses' own books, which are saved: a city just loaded
       shows the month it was saved in. A good's click opens who sold and
       who bought it, and what the world pays and charges for one. Under
       them, the last ten years and the record since founding; then the
       world's prices in one line, and the market's price of every good
       under "details" - read off the markets, the railway's freight in them
       (B5: the old table worked them out and missed it).
       ===================================================================== */

    void goodsPage(VBox page, boolean fresh) {
        VBox hero = goodsHero(fresh);
        targets.put(GOODS_CARD, hero);
        page.getChildren().add(hero);
        page.getChildren().add(row(tenYearsCard(), sinceFoundingCard()));
        page.getChildren().add(worldPrices());
    }

    /** The hero: the head with the toggle and its three figures, the bars, and the rest folded. */
    VBox goodsHero(boolean fresh) {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        Sectors.TradeByGood t = ui.game.getTradeByGood();
        FlowPane toggle = chipStrip(new String[] {"by good", "by business"}, byBusiness ? "by business" : "by good",
                Palette.SIZE_LABEL, pick -> {
                    byBusiness = "by business".equals(pick);
                    showForeignMenu();
                });
        toggle.setAlignment(Pos.CENTER_RIGHT);
        VBox c = card(cardHead(Icons.LORRY, Palette.BUSINESS, fx.isMonthCounted() ? "WHAT WE TRADE THIS MONTH"
                : "WHAT WE TRADE, THE MONTH THE CITY WAS SAVED IN", TRADE_INFO, toggle));
        c.getChildren().add(figure("sold " + d(t.sold()) + " · bought " + d(t.bought()) + " · balance " + signedD(t.balance()),
                Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        if (!fx.isMonthCounted()) c.getChildren().add(noteLine("From the businesses' saved books: the balance of payments "
                + "counts again a month on.", NOT_SAVED_INFO + " The households' own imports - their cars, and since 0.7.49 "
                + "their fuel - are counted from the next month.", 1100));
        List<GoodRow> rows = byBusiness ? businessRows(t) : goodRows(t);
        if (rows.isEmpty()) {
            c.getChildren().add(line("Nothing crossed the city's edge this month.", NOTHING_CROSSED));
            return c;
        }
        boolean all = openLines.contains("goods:all");
        List<GoodRow> shown = all || rows.size() <= PAGE_GOODS + 1 ? rows : rows.subList(0, PAGE_GOODS);
        MirrorRows bars = mirror(shown, "BOUGHT ABROAD", "SOLD ABROAD", !byBusiness, false);
        if (fresh && fx.isMonthCounted()) bars.grow(GROW_MILLIS);
        for (int i = 0; i < shown.size(); i++) {
            if (shown.get(i).good() != null) targets.put("#good:" + shown.get(i).good().name(), bars.row(i));
        }
        c.getChildren().add(bars);
        if (shown.size() < rows.size()) {
            int more = rows.size() - shown.size();
            c.getChildren().add(door("+" + more + " more", Palette.ACCENT, () -> {
                openLines.add("goods:all");
                showForeignMenu();
            }));
        } else if (all && rows.size() > PAGE_GOODS + 1) {
            c.getChildren().add(door("the first " + PAGE_GOODS + " only", Palette.ACCENT, () -> {
                openLines.remove("goods:all");
                showForeignMenu();
            }));
        }
        c.getChildren().add(muted(byBusiness ? "a row a business: tap one for its books"
                : "a row a good, largest side first: tap one for who sold it, who bought it and what the world pays"));
        return c;
    }

    /** THE LAST TEN YEARS: History's exports and imports abroad on one small chart, and the last twelve months in figures. */
    VBox tenYearsCard() {
        HistorySave h = ui.game.getHistorySave();
        VBox c = card(cardHead(Icons.REPORTS, Palette.BUSINESS, "THE LAST TEN YEARS", "What the city sold abroad and "
                + "bought abroad each month, the last ten years. One month says whether a mill was staffed; ten years say "
                + "whether the city earns its living.", null));
        if (h.months() < 2) {
            c.getChildren().add(line("A line needs two months; come back next month.", null));
            return c;
        }
        TimeChart chart = new TimeChart(new ChartModel(), Set.of(), false);
        chart.setData(h.getMonth(), List.of(
                        new TimeChart.Line("exportsAbroad", "sold abroad", Palette.BUSINESS, 0, h.aligned("exportsAbroad"),
                                v -> HistoryScreen.plotScale("money", v), this::d, ""),
                        new TimeChart.Line("importsAbroad", "bought abroad", Palette.BUSINESS_DARK, 0, h.aligned("importsAbroad"),
                                v -> HistoryScreen.plotScale("money", v), this::d, "")),
                HistoryScreen.axisFor("money"), null, false, false, null, List.of(), List.of(), List.of(),
                "no months recorded yet");
        chart.setSize(560, 150);
        int n = Math.min(12, h.months());
        c.getChildren().addAll(chart, keyRow(swatch(Palette.BUSINESS, "sold abroad"), swatch(Palette.BUSINESS_DARK, "bought abroad")),
                line((n == 12 ? "the last 12 months: " : "the last " + n + " months: ")
                        + d(h.recentTotal("exportsAbroad", 12)) + " sold · " + d(h.recentTotal("importsAbroad", 12)) + " bought", null));
        return c;
    }

    /**
     * SINCE FOUNDING (pure): sold, bought, the income from abroad - its name
     * following its sign (B3: it read "paid abroad" for D$159.4B received) -
     * capital, and the whole record, the model's balanceFromFlows().
     */
    Walk recordWalk() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        List<Step> steps = new ArrayList<>();
        List<String> nothing = new ArrayList<>();
        step(steps, nothing, "sold abroad", fx.getLifetimeExports(), Palette.BUSINESS,
                "Sold abroad since founding\n" + dFull(fx.getLifetimeExports()));
        step(steps, nothing, "bought abroad", -fx.getLifetimeImports(), Palette.BUSINESS_DARK,
                "Bought abroad since founding\n" + dFull(fx.getLifetimeImports()));
        double income = -fx.getLifetimeInterest();
        step(steps, nothing, incomeName(income), income, income >= 0 ? Palette.MONEY : Palette.MONEY_DARK,
                incomeName(income) + " since founding\n" + signedFull(income));
        step(steps, nothing, "capital", fx.getLifetimeFinancial(), Palette.MONEY_LIGHT,
                "Capital, in less out, since founding\n" + signedFull(fx.getLifetimeFinancial()));
        steps.add(Step.total("the whole record", fx.balanceFromFlows(), Palette.TEXT_SPENT)
                .tip("The whole record\n" + signedFull(fx.balanceFromFlows()) + "\n" + RECORD_INFO));
        return new Walk(steps, nothing);
    }

    /** The record's sentence (the spec's H1). */
    static final String RECORD_INFO = "One month says whether a mill was staffed. The run says whether the city earns its "
            + "living. Nobody can spend this figure — it is a record, not a stock.";

    VBox sinceFoundingCard() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        VBox c = card(cardHead(Icons.COIN, Palette.BUSINESS, "SINCE FOUNDING", RECORD_INFO, null));
        Walk w = recordWalk();
        c.getChildren().add(waterfall(w.steps(), this::signedD, 0, 170));
        double trade = fx.getLifetimeTradeBalance();
        c.getChildren().add(chips(chip("on trade alone " + signedD(trade), Palette.TEXT_LABEL)));
        c.getChildren().add(line(trade >= 0
                        ? "Over its whole life this city has sold the world more than it has bought from it."
                        : "Over its whole life this city has bought more from the world than it has sold to it.",
                trade >= 0
                        ? "Over its whole life this city has sold the world more than it has bought from it. That is a city "
                          + "that pays its own way, and it is the position from which borrowing abroad is a choice rather "
                          + "than a necessity."
                        : "Over its whole life this city has bought more from the world than it has sold to it. Every dollar "
                          + "of that gap was financed — borrowed, or paid out of the vault, or taken out of the currency. It "
                          + "is not a debt in itself, but it is what a debt is made of."));
        List<Node> also = new ArrayList<>();
        if (fx.getLifetimeIntervention() != 0) {
            also.add(chipInfo("net bought into the vault " + signedD(fx.getLifetimeIntervention()), Palette.TEXT_LABEL,
                    "What the treasury has bought into the vault less what it, the central bank's defence and the land "
                            + "office have taken out, in the local money each cost or fetched."));
        }
        if (fx.getForgiven() != 0) {
            also.add(chipInfo("forgiven or settled " + d(fx.getForgiven()), Palette.TEXT_LABEL,
                    "Claims the world has written off since founding - a failed bank's foreign creditors, a resolution, "
                            + "a default. It improved the position without a dollar being earned."));
        }
        if (fx.getRepudiated() > 0) {
            also.add(chipInfo("walked away from " + d(fx.getRepudiated()), Palette.BAD,
                    "What the city refused to pay abroad, since founding. It flatters every ratio for a year and costs "
                            + "the city the window abroad for five."));
        }
        if (!also.isEmpty()) c.getChildren().add(chips(also.toArray(new Node[0])));
        return c;
    }

    /**
     * THE WORLD'S PRICES in one line (the spec's G3) and, under "details",
     * every good the world trades with the city: what it pays for one and
     * what it charges to bring one in - the market's own prices, freight in
     * them (B5) - each in D$ and in US$ at today's rate (D16), and which way
     * it crossed this month. One row a good (B6: it was 45 rows of one
     * direction each).
     */
    VBox worldPrices() {
        WorldEconomy world = ui.game.getWorldEconomy();
        VBox c = card();
        if (world != null) {
            c.getChildren().add(line(String.format("The world's prices are %.3f× their founding level, %s %.2f%% a year.",
                    world.getPriceLevel(), world.getInflation() >= 0 ? "rising" : "falling",
                    Math.abs(world.getInflation()) * 100), WORLD_INFO));
        }
        c.getChildren().add(fold("goods:prices", "what the world pays and charges, good by good", this::priceGrid));
        return c;
    }

    /** The world prices' note (the spec's G3). */
    static final String WORLD_INFO = "Every world price is quoted in the world's money and converted at the rate. So a "
            + "weaker currency raises what imports cost at home and what exports earn at home, both at once — which is the "
            + "whole mechanism by which a devaluation closes a trade deficit, and the whole reason it hurts while it does.";

    /** The grid under "details". */
    Node priceGrid() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        Sectors.TradeByGood t = ui.game.getTradeByGood();
        GridPane grid = grid(new double[] {150, 92, 74, 92, 74, 78}, rightAfterFirst(6));
        gridHead(grid, "", "it pays", "in US$", "it charges", "in US$", "this month");
        int line = 1;
        for (GoodsMarket m : ui.game.getMarkets().all()) {
            Good g = m.good();
            if (!g.exportable() && !g.importable()) continue;
            double pays = m.exportPrice(), charges = m.importPrice();
            Sectors.GoodTrade crossed = t.goods().get(g);
            boolean sold = crossed != null && crossed.sold() > 0, bought = crossed != null && crossed.bought() > 0;
            grid.add(gridCell(g.label() + (g == Good.IRON ? " (scrap in)" : "") + " · a " + g.unit(),
                    Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            grid.add(gridCell(Double.isFinite(pays) ? unitD(pays) : "—", Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, line);
            grid.add(gridCell(Double.isFinite(pays) ? marked(Currency.FOREIGN_SYMBOL, unitPrice(fx.toUsd(pays))) : "",
                    Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
            grid.add(gridCell(Double.isFinite(charges) ? unitD(charges) : "—", Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 3, line);
            grid.add(gridCell(Double.isFinite(charges) ? marked(Currency.FOREIGN_SYMBOL, unitPrice(fx.toUsd(charges))) : "",
                    Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 4, line);
            grid.add(gridCell(sold && bought ? "both ways" : sold ? "sold to it" : bought ? "bought from it" : "·",
                    Palette.TEXT_LABEL, Palette.SIZE_CAPTION, true), 5, line);
            line++;
        }
        VBox box = new VBox(6, grid);
        box.getChildren().add(statementNote("\"It pays\" is what the world pays the city for one, delivered there; \"it "
                + "charges\" what it charges to bring one in. Both are the markets' own prices, at today's rate and with "
                + "the railway's freight in them, so they are what a trade this month clears at; the US$ is the same price "
                + "in the world's money."));
        if (!fx.isMonthCounted()) box.getChildren().add(statementNote(FREIGHT_AFTER_LOAD));
        return box;
    }

    /* =====================================================================
       THE CURRENCY (0.7.35; "The rate" and "What is moving it" until then)

       What does a US dollar cost, and which way is it going? The rate at
       28 px with its unit (B4: it had none), against parity on a gauge
       either side of it in the one parity rule's bands (D8), beside the
       rate's history on City History's own chart - parity beside it since
       0.7.35, the currency's crises on the episode lane, the city's
       decisions on the currency as flags. Then what is moving it next
       month, as diverging bars - stronger to the left, weaker to the right,
       in the money blues and never a verdict's colours (B19: a currency 26%
       above parity and still rising is not good news by colour): the
       pressure in its two terms, and what the month's push and the
       basket's pull come to. Then what the rate is doing to the dollar debt
       and to the vault.
       ===================================================================== */

    void currencyPage(VBox page) {
        HistorySave h = ui.game.getHistorySave();
        VBox rate = rateCard();
        rate.setMinWidth(360);
        rate.setPrefWidth(400);
        rate.setMaxWidth(400);
        HBox hero = new HBox(TILE_GAP, rate);
        hero.setAlignment(Pos.TOP_LEFT);
        if (h.months() >= 2) {
            TimeChart chart = rateChart(h, rateWindow, true, 794, 230);
            Label title = new Label("THE RATE OVER TIME");
            title.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
            title.setMinWidth(Region.USE_PREF_SIZE);
            chart.lead().getChildren().setAll(title);
            VBox right = new VBox(6, chart);
            int parity = h.monthsRecorded("fxParity");
            if (parity < h.months()) {
                right.getChildren().add(noteLine(parity == 0
                                ? "Parity is recorded from 0.7.35 on: none yet in this city's history - today's is "
                                  + sym() + fxRate(ui.game.getForeignAccounts().getParity()) + "."
                                : "Parity is recorded from 0.7.35 on: " + parity + " month" + (parity == 1 ? "" : "s") + " so far.",
                        "Parity is where a basket costs the same here and abroad. History kept the rate from the "
                                + "founding and parity only since 0.7.35, so an older city's parity line starts the "
                                + "month it was first played in this build.", 790));
            }
            hero.getChildren().add(right);
        }
        page.getChildren().add(hero);
        VBox forces = forcesCard();
        targets.put(FORCES, forces);
        page.getChildren().add(forces);
        page.getChildren().add(sectionHead("WHAT THE RATE IS DOING TO THE CITY", null, null));
        VBox debt = dollarDebtCard();
        targets.put(DEBT, debt);
        page.getChildren().add(row(debt, vaultCard()));
    }

    /**
     * THE RATE: what a US dollar costs at 28 px with its unit; one of ours in
     * theirs; this month's move; parity with its line; the parity gauge
     * either side of parity - its bands the one rule's (amber past
     * PARITY_WATCH, red past PARITY_FAR), its mark where the rate is - or a
     * "held fixed" chip when the rate is pinned.
     */
    VBox rateCard() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        Label rate = figure(sym() + fxRate(fx.getRate()), 28, Palette.TEXT_HEAD);
        Label per = words("per " + Currency.FOREIGN_SYMBOL, 13, Palette.TEXT_LABEL);
        HBox big = new HBox(6, rate, per);
        big.setAlignment(Pos.BASELINE_LEFT);
        VBox c = card(cardHead(Icons.EXCHANGE, Palette.MONEY, "THE RATE", RATE_INFO, null), big, muted(oneOfOurs()));
        String moved = rateMove();
        if (moved != null) c.getChildren().add(muted(moved));
        c.getChildren().add(cardLine("parity " + sym() + fxRate(fx.getParity()) + " · where a basket costs the same", "",
                null, parityLineInfo(fx)));
        // ...where the real rate alone would hold it, and the drift the anchor hands it (0.7.45; the UI spec's D18).
        if (!fx.isPinned()) {
            PolicyScreen.DriftWords drift = ui.policyScreen.driftWords();
            c.getChildren().add(muted("the real rate alone would hold it " + PolicyScreen.parityWords(fx.uipLevel() - 1)));
            c.getChildren().add(muted(("no drift".equals(drift.figure()) ? "no drift" : "drifts " + drift.figure()) + ": " + drift.line()));
        }
        if (fx.isPinned()) {
            c.getChildren().add(chips(chip("held fixed", Palette.TEXT_LABEL)));
        } else {
            double dev = fx.deviationFromParity();
            int level = ForeignAccounts.parityLevel(dev);
            double s = PARITY_SCALE, w = ForeignAccounts.PARITY_WATCH, f = ForeignAccounts.PARITY_FAR;
            double[] edges = {(s - f) / (2 * s), (s - w) / (2 * s), (s + w) / (2 * s), (s + f) / (2 * s), 1};
            String[] tones = {Palette.BAD, Palette.WARN, Palette.TEXT_SPENT, Palette.WARN, Palette.BAD};
            double at = .5 + Math.max(-s, Math.min(s, dev)) / (2 * s);
            c.getChildren().add(bandTrack(at, edges, tones,
                    List.of(new Tick(.5, Palette.TEXT_LABEL, 1, "parity", "parity " + perUsd(fx.getParity()))), 0, 12, false));
            Label stronger = muted("‹ stronger"), weaker = muted("weaker ›");
            Region gap = new Region();
            HBox.setHgrow(gap, Priority.ALWAYS);
            c.getChildren().add(new HBox(stronger, gap, weaker));
            c.getChildren().add(chips(chip(parityWords(fx), level == 0 ? Palette.TEXT_MUTED : parityTone(level))));
        }
        return c;
    }

    /** Where the rate sits against parity, and what that does (the spec's Q3 and the parity note). */
    static String parityLineInfo(ForeignAccounts fx) {
        double drift = fx.deviationFromParity();
        String where = fx.isPinned()
                ? "The rate is pinned. A fixed rate is a promise the reserves have to keep, and the market will find out "
                  + "whether they can."
                : drift > .005
                ? "A rate ABOVE parity means it takes more of ours to buy one of theirs — the currency is weaker than the "
                  + "same basket of goods says it should be. Imports cost more; what the city sells abroad earns more at home."
                : drift < -.005
                ? "A rate BELOW parity means the currency is stronger than the basket says. Imports are cheap and the "
                  + "exporters are being squeezed."
                : "The rate is where the price of the same basket at home and abroad says it should be. That is not luck — "
                  + "it is what the pull back to parity does over time.";
        return where + " Parity here is relative purchasing power: the city's own price index against the world's. A "
                + "currency that has wandered a long way from what the same basket costs abroad gets pulled back, slowly, "
                + "whatever else is happening to it.";
    }

    /* ------------------------------ what is moving it ------------------------------ */

    /**
     * THE PRESSURE (pure): the trade term - both accounts' trailing
     * imbalance over everything that crossed (ForeignAccounts.pressure()) -
     * and the real rate's term (ratePressure()), and the two together, the
     * model's previewRawPressure(). Positive is weaker, drawn right. Each
     * row's (i) its old sentence whole.
     */
    List<Force> pressureForces() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        double account = fx.monthlyCurrentAccount(), capital = fx.monthlyFinancialAccount();
        double real = fx.getRealRateDifferential();
        return List.of(
                new Force("the trade term", fx.pressure(), false, false, tradeTermInfo(fx),
                        "current account " + d(account) + " a month · financial " + d(capital) + ", a 12-month average"),
                new Force("the real rate", fx.ratePressure(), false, false, realRateInfo(fx),
                        points(real).replace(" points", " pts") + " over the world's, worth " + pushWords(fx.ratePressure())),
                new Force("pressure", fx.previewRawPressure(), true, false,
                        "The two together: what the month would push the currency by before the vault and the openness "
                                + "have had their say.", null));
    }

    /**
     * WHICH COMES TO (pure): the month's push - the pressure less what the
     * vault meets, times the openness, at DRIFT_SPEED - the anchored drift
     * (0.7.45; the UI spec's B6, D18: the month applies it, and the preview
     * left it out), and the basket's pull back to parity, and the next
     * month's move, all the model's previews (ForeignAccounts.previewPush(),
     * previewDrift(), previewPull(), previewMove()). A pinned rate moves by
     * none of it.
     */
    List<Force> comesToForces() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        double move = fx.previewMove();
        return List.of(
                new Force("the month's push", fx.previewPush(), false, fx.isPinned(),
                        "What the month is doing to the currency: the pressure, less what the vault's sale meets of it, "
                                + "times how open the city is, at the rate a month's pressure moves the currency.", null),
                new Force("the anchored drift", fx.previewDrift(), false, fx.isPinned(),
                        "The credible part of the inflation people expect, against the world's: trust in the bank times "
                                + "the target, against the world's own inflation - a twelfth of the year's slide a month. "
                                + "Policy › Money's THE CURRENCY'S DRIFT.", null),
                new Force("the basket's pull", fx.previewPull(), false, fx.isPinned(),
                        "The basket dragging it back to what the same goods cost abroad - the force that never lets go.",
                        null),
                new Force("next month", move, true, fx.isPinned(), null,
                        fx.isPinned() ? "held fixed: it does not move" : "about " + moveWords(move)));
    }

    /** The trade term's sentences (the spec's F2: the current account's, the financial account's and the trade term's, verbatim). */
    String tradeTermInfo(ForeignAccounts fx) {
        double account = fx.monthlyCurrentAccount(), capital = fx.monthlyFinancialAccount();
        String a = Math.abs(account) < .5
                ? "Nothing has crossed the city's edge in either direction, so there is no imbalance for the rate to answer "
                  + "and the basket is the only thing left holding it where it is."
                : account > 0
                ? "A surplus is the world handing the city more of its money than the city hands back. That has to be held "
                  + "somewhere, and the somewhere pushes the rate stronger."
                : "A deficit has to be paid for in somebody else's money. The city either earns it, borrows it, or spends "
                  + "the vault - and whichever it is, it pushes the rate weaker.";
        String b = Math.abs(capital) < .5
                ? "No money is crossing the edge except for goods, so the trade balance above is the whole of the imbalance."
                : capital < 0
                ? "Money leaving to be invested or lent abroad - the families' savings going out for the world's rate, and "
                  + "foreigners borrowing here to hold dollars. Every dollar of it is local currency sold, which offsets a "
                  + "surplus one for one. A city can run a permanent surplus and a flat rate at the same time, and this is how."
                : "Money coming in to be lent or invested here. It has to be bought with somebody's dollars first, so it "
                  + "pushes the same way a surplus does.";
        return "The current account, 12 months: " + a + "\n\nThe financial account, 12 months: " + b
                + "\n\nBoth imbalances together, as a share of everything that crosses in either direction. A big number on a "
                + "small trade is a small push - and a surplus financed by money going back out is no push at all.";
    }

    /**
     * The real rate's sentence (F2), with the dial, the inflation people
     * expect and the world's rate - ex ante since 0.7.42, as the model strikes
     * it (Game.realRateDifferential()); it printed the year's inflation as the
     * subtrahend until 0.7.45 (the UI spec's B9).
     */
    String realRateInfo(ForeignAccounts fx) {
        return String.format("The dial less the inflation people expect (%s less %s), against the world's rate less the "
                + "world's inflation (%s less %s). Money goes where it is paid better in real terms: a dial under what "
                + "people expect is a rate the world is paid to leave, and the currency falls on the outflow; a dial over "
                + "it holds the currency however fast prices are rising.",
                ratePct(ui.game.getDebtManager().getPolicyRate()), ratePct(ui.game.getExpectedInflation()),
                ratePct(DebtManager.WORLD_BASE_RATE), ratePct(fx.getWorldInflation()));
    }

    /**
     * WHAT IS MOVING IT, NEXT MONTH: a banner when the rate is pinned; two
     * halves - THE PRESSURE on its own scale, the vault's defence and the
     * openness under it; WHICH COMES TO on its own - and the old page's
     * readings under "details".
     */
    VBox forcesCard() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        VBox c = card(cardHead(Icons.EXCHANGE, Palette.MONEY, "WHAT IS MOVING IT, NEXT MONTH", FORCES_INFO, null));
        if (fx.isPinned()) {
            c.getChildren().add(line("The rate is pinned: none of this is moving it.", "None of this is moving it, because "
                    + "the treasury has promised it will not move. What the forces below would have done is what the "
                    + "reserves are now absorbing instead."));
        }
        List<Force> pressure = pressureForces();
        double ps = .20;
        for (Force f : pressure) ps = Math.max(ps, Math.abs(f.value()));
        VBox left = new VBox(8, words("THE PRESSURE", Palette.SIZE_LABEL, Palette.TEXT_LABEL),
                divergingBars(pressure, ps, 190, 110, Palette.MONEY_LIGHT, Palette.MONEY, "‹ stronger", "weaker ›",
                        TradeScreen::pushWords));
        left.getChildren().add(noteLine(vaultWords(fx), vaultInfo(fx), 560));
        left.getChildren().add(chips(chipInfo(opennessWords(fx), Palette.TEXT_LABEL, "Trade measured against output. A "
                + "city that barely trades barely moves its own rate, whatever the pressure reads. The model holds it at "
                + "100% at most: past that, trade is at least as big as output and the whole push reaches the rate.")));
        List<Force> comes = comesToForces();
        double cs = .003;
        for (Force f : comes) cs = Math.max(cs, Math.abs(f.value()));
        VBox right = new VBox(8, words("WHICH COMES TO", Palette.SIZE_LABEL, Palette.TEXT_LABEL),
                divergingBars(comes, cs, 170, 110, Palette.MONEY_LIGHT, Palette.MONEY, "‹ stronger", "weaker ›",
                        TradeScreen::moveWords));
        right.getChildren().add(noteLine("The push, the drift and the pull add to the move.", COMES_TO_INFO, 560));
        GridPane halves = equalColumns(2, 24);
        halves.add(left, 0, 0);
        halves.add(right, 1, 0);
        c.getChildren().add(halves);
        c.getChildren().add(fold("currency:forces", "the forces, as the readings they were", this::forceReadings));
        return c;
    }

    /** The forces card's (i) (the spec's F1). */
    static final String FORCES_INFO = "One reading: what the next month does to the rate, on the accounts as they stand "
            + "today. Five forces: the trade balance, the real rate, the vault's defence against a fall, the anchored "
            + "drift - your target, weighted by how far people trust the bank, against the world's inflation - and the "
            + "pull back to parity. The year's inflation is not one of them - it reaches the rate through the parity it "
            + "raises, the trade a dear currency loses and the inflation people come to expect. Stronger is drawn to the "
            + "left, weaker to the right.";

    /** WHICH COMES TO's (i) (F3's two notes). */
    static final String COMES_TO_INFO = "The push is what the month is doing to the currency; the drift is the slide "
            + "the inflation people expect hands it, against the world's; the pull is the basket "
            + "dragging it back to what the same goods cost abroad. They pull against each other when the signs differ, "
            + "and the rate moves by whatever is left over. A city whose central bank sells dollars into a fall feels less "
            + "of it - while the vault lasts - and none of a push to rise is ever met; the pull is the force that never lets go.";

    /** The vault's defence in words: what it would sell, or nothing - or not counted yet (a city just founded, or loaded from a save before 0.7.46), when the month's deficit decides it (B1). */
    String vaultWords(ForeignAccounts fx) {
        if (!fx.isMonthCounted() && fx.previewRawPressure() > 0 && fx.absorption() > 0) {
            return "the vault's defence: not counted yet";
        }
        double absorbed = fx.previewAbsorption();
        return absorbed > 0
                ? String.format("the vault sells %s, meeting %.0f%% of the month's deficit", usd(fx.previewDefenceUsd()), absorbed * 100)
                : "the vault sells nothing";
    }

    /** ...and its sentence (F2). */
    String vaultInfo(ForeignAccounts fx) {
        return String.format("Against a push to fall, the central bank sells the vault's dollars: up to %.0f%% of what the "
                + "month's own accounts are short of (the vault could do %.0f%% at %s of cover), and what the dollars meet is "
                + "taken off the push, at the cost of the central bank's equity. A push to rise is never met, and a month "
                + "that is not short of dollars sells none. At the last reprice it sold %s and met %.0f%% of that month's "
                + "deficit.%s", ForeignAccounts.MAX_ABSORPTION * 100, fx.absorption() * 100,
                fx.monthlyImports() > 0 ? coverMonths(fx.importCover()) : "endless", usdFull(fx.getDefenceUsd()),
                fx.getLastAbsorption() * 100,
                fx.isMonthCounted() ? "" : " The month's deficit is not kept in the save, so until a month turns the "
                        + "vault's share of the push is not counted, and the push above is drawn without it.");
    }

    /** Openness, clamped at 100% by the model (B17, D20): "trade ≥ output (100%)" when it is. */
    static String opennessWords(ForeignAccounts fx) {
        double open = fx.getOpenness();
        return open >= 1 ? "openness: trade ≥ output (100%)" : String.format("openness %.0f%%", open * 100);
    }

    /** The old page's readings, under "details": each reading and its sentence (the statement it was, B19's colours plain). */
    Node forceReadings() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        VBox column = column();
        double account = fx.monthlyCurrentAccount(), capital = fx.monthlyFinancialAccount();
        column.getChildren().add(statementHead("The forces on the rate"));
        forceLine(column, "The current account, 12 months", Math.abs(account) < .5 ? "nothing crossed" : signed(account, false));
        forceLine(column, "The financial account, 12 months", Math.abs(capital) < .5 ? "nothing moved" : signed(capital, false));
        forceLine(column, "The trade term", pushWords(fx.pressure()));
        forceLine(column, "The real rate, over the world's", points(fx.getRealRateDifferential()).replace(" points", " pts")
                + ", worth " + pushWords(fx.ratePressure()));
        forceLine(column, "Pressure on the rate", pushWords(fx.previewRawPressure()));
        forceLine(column, "The vault's defence", fx.previewAbsorption() > 0
                ? String.format("sells %s, %.0f%%", usdFull(fx.previewDefenceUsd()), fx.previewAbsorption() * 100) : "sells nothing");
        forceLine(column, "Openness of the economy", String.format("%.0f%%", fx.getOpenness() * 100));
        column.getChildren().add(statementHead("Which comes to"));
        column.getChildren().add(statementLine("This month's push", signedPct3(fx.previewPush()), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("The anchored drift", signedPct3(fx.previewDrift()), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("Pull back to parity", signedPct3(fx.previewPull()), Palette.TEXT_HEAD));
        column.getChildren().add(statementTotal("Net movement", signedPct3(fx.previewMove()), Palette.TEXT_HEAD));
        column.getChildren().add(statementNote("Up is weaker: a rate that rises is more of ours for one of theirs."));
        return column;
    }

    /** A reading on its statement line. */
    static void forceLine(VBox column, String label, String value) {
        column.getChildren().add(statementLine(label, value, Palette.TEXT_HEAD));
    }

    /** A fraction as a signed per cent to three places, with a true minus: "−0.229%". */
    static String signedPct3(double v) {
        double shown = unsigned0(v * 100, 3);
        return (shown > 0 ? "+" : shown < 0 ? "−" : "") + String.format("%.3f%%", Math.abs(shown));
    }

    /* ------------------------------ what the rate is doing ------------------------------ */

    /** THE DOLLAR DEBT (the spec's Q4): owed abroad in US$, what that is at this rate, and what the currency did to it - or one line when nothing is owed. */
    VBox dollarDebtCard() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        VBox c = card(cardHead(Icons.PAPER, Palette.MONEY, "THE DOLLAR DEBT", null, null));
        if (!(fx.getForeignDebt() > 0)) {
            c.getChildren().add(line("Nothing is owed abroad, so the rate cannot make the city's debt bigger.",
                    "Nothing is owed abroad, so the rate cannot make the city's debt bigger. It still decides what imports "
                            + "cost and what exports earn."));
            return c;
        }
        c.getChildren().add(cardLine("owed abroad", usdFull(fx.getForeignDebtUsd()), null));
        c.getChildren().add(cardLine("which at this rate is", dFull(fx.getForeignDebt()), null));
        if (Math.abs(fx.getLastRevaluation()) > .005) {
            c.getChildren().add(cardLine("the currency moved it this month by", signedFull(fx.getLastRevaluation()), null,
                    fx.getLastRevaluation() > 0
                            ? "Dearer this month, and nobody was paid a cent for it. That is what a devaluation does to a "
                              + "country that borrowed in somebody else's money."
                            : "Cheaper this month — the same paper, a stronger currency. It works both ways and it is not "
                              + "income either way."));
        }
        if (Math.abs(fx.getLifetimeRevaluation()) > .005) {
            c.getChildren().add(cardLine("since founding, the currency has " + (fx.getLifetimeRevaluation() > 0 ? "added" : "taken off"),
                    dFull(Math.abs(fx.getLifetimeRevaluation())), null));
        }
        c.getChildren().add(doorPill("Finances · home & abroad", Icons.FINANCES, Palette.MONEY,
                () -> ui.financesScreen.open("The position", "Home & abroad", null)));
        return c;
    }

    /** THE VAULT (E2): its dollars, what they are worth at this rate, and what the currency did to them - a door to The reserves. */
    VBox vaultCard() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        VBox c = card(cardHead(Icons.SAFE, Palette.MONEY, "THE VAULT", VAULT_MOVED_INFO, null),
                cardLine("in the vault", usdFull(fx.getReservesUsd()), null),
                cardLine("worth at home, at this rate", dFull(fx.getReserves()), null));
        if (Math.abs(fx.getLastVaultRevaluation()) > .005) {
            c.getChildren().add(cardLine("the currency moved it this month by", signedFull(fx.getLastVaultRevaluation()), null));
        }
        c.getChildren().add(doorPill("The reserves", Icons.SAFE, Palette.MONEY, () -> open(RESERVES)));
        return c;
    }

    /** What the currency does to the vault (the spec's E2 note, both ways). */
    static final String VAULT_MOVED_INFO = "The vault is kept in dollars, so its dollar figure stays put and its local figure "
            + "moves with the rate - booked as a line of its own. The currency falling makes the same dollars worth more at "
            + "home: nobody was paid anything, it is not cash until they are sold, and a sale is at that day's rate. It is "
            + "what a reserve is for. The currency rising makes them fetch less; nothing was spent and nothing left the vault.";

    /* =====================================================================
       THE RESERVES (0.7.35; "What is yours", "Cover" and "Exchange" until then)

       What does the treasury hold, whose is it, and how long would it last?
       WHOSE IT IS: the vault as one bar with the claims against it - the
       dollar paper, the money parked in the bank - eaten out of it, and what
       is the city's own; beside it HOW LONG IT WOULD LAST: months of
       imports on a gauge, and what three months would need. Then the money
       that can leave on no notice, and what else moved the vault this month
       - the currency, the defence, the land office. Then the exchange: cash
       into the vault, or the vault into cash, with what it would do to the
       cover before the button is pressed.
       ===================================================================== */

    void reservesPage(VBox page) {
        VBox cover = coverCard();
        targets.put(COVER, cover);
        page.getChildren().add(row(whoseCard(), cover));
        VBox leave = leaveCard();
        targets.put(LEAVE, leave);
        page.getChildren().add(row(leave, movedCard()));
        VBox exchange = exchangeCard();
        targets.put(EXCHANGE_CARD, exchange);
        page.getChildren().add(exchange);
        page.getChildren().add(fold("reserves:claims", "the two claims on the vault, side by side", this::claimsGrid));
    }

    /**
     * WHOSE IT IS: the claim bar - the dollar paper in the money blue's dark
     * step, the parked money sand, the city's own in the money blue (B7:
     * they were amber, red and green, verdicts on parts of one pot) - and a
     * line a part in D$ and US$; the treasury's position (B12: "Net
     * position" spoke of the vault and left out the D$182.9B the firms and
     * households hold abroad, which is a door now).
     */
    VBox whoseCard() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        CapitalFlows hot = ui.game.getCapitalFlows();
        double gross = fx.getReserves(), debt = fx.getForeignDebt(), parked = hot.getStock();
        double own = ui.game.getOwnReserves();
        VBox c = card(cardHead(Icons.SAFE, Palette.MONEY, "WHOSE IT IS", ONE_POT_INFO, null));
        if (gross <= 0 && debt <= 0 && parked <= 0) {
            c.getChildren().add(line("The vault is empty and nothing is owed abroad.",
                    "The vault is empty and nothing is owed abroad. Export earnings go to the firms that earned them — a "
                            + "treasury holds foreign money only if it has bought some, or borrowed it abroad."));
            return c;
        }
        c.getChildren().add(claimBar(gross, debt, parked, own));
        c.getChildren().add(cardLine("in the vault", usd(fx.getReservesUsd()) + " · " + d(gross), null));
        c.getChildren().add(cardLine("owed abroad on paper", d(debt) + (debt > 0 ? " · " + usd(fx.getForeignDebtUsd()) : ""), null));
        c.getChildren().add(cardLine("parked in the bank, free to leave", d(parked), null));
        c.getChildren().add(cardLine("the city's own", d(own) + (own > 0 ? " · " + usd(fx.toUsd(own)) : ""),
                own < 0 ? Palette.BAD : null));
        c.getChildren().add(cardLine("the treasury's position", signedD(fx.netForeignPosition()), null,
                "The vault less the dollar paper - the figure a country is judged on. A city with "
                        + "deep reserves and deeper dollar debt is not rich, and the reserve line on its own says it is. "
                        + "The parked money is not in it; the city's own, above, takes it off too."));
        if (fx.getRepudiated() > 0) {
            c.getChildren().add(cardLine("walked away from, since founding", d(fx.getRepudiated()), Palette.BAD));
        }
        double elsewhere = ui.game.getHeldAbroadPrivately();
        if (elsewhere > 0) {
            c.getChildren().add(doorPill(d(elsewhere) + " more is held abroad by the firms and households", Icons.TRADE,
                    Palette.MONEY, () -> open(MONTH, HOLDINGS)));
        }
        return c;
    }

    /** WHOSE IT IS's (i) (the spec's R4, and B11: the method's name is out of it). */
    static final String ONE_POT_INFO = "The vault is one pot — the game does not tag a dollar as borrowed or earned, and it "
            + "does not need to, because money is fungible. Hold a hundred, owe eighty, and twenty is yours whichever dollar "
            + "came from where. Both sides of that subtraction are known exactly: the paper is re-read from the bonds "
            + "themselves each month, and the parked money is tracked to the cent. The parked money is counted as a claim "
            + "on purpose: a claim that can be exercised at no notice is a heavier claim than a bond with a date on it, "
            + "not a lighter one.";

    /**
     * One bar of the vault with the claims against it eaten out of the left
     * (the Trade tab's since 0.7.0), at any width: drawn as one stock with
     * bites taken out, because the question is what is left; when the claims
     * run past the vault the bar is all claims and a mark says where the
     * vault ends.
     */
    VBox claimBar(double gross, double debt, double parked, double own) {
        double scale = Math.max(gross, debt + parked);
        if (!(scale > 0)) return new VBox();
        String sand = Palette.CATEGORIES[4];
        List<Segment> parts = new ArrayList<>();
        if (debt > 0) parts.add(seg(debt, Palette.MONEY_DARK, "owed abroad on paper\n" + dFull(debt)));
        if (parked > 0) parts.add(seg(parked, sand, "parked here and free to leave\n" + dFull(parked)));
        if (own > 0) parts.add(seg(own, Palette.MONEY, "the city's own\n" + dFull(own)));
        List<Tick> ticks = List.of(new Tick(gross, Palette.TEXT_MAX, 2, "the vault ends here", "the vault ends here: " + dFull(gross)));
        VBox box = new VBox(4, segmentBar(parts, scale, ticks, 0, 22),
                keyRow(debt > 0 ? swatch(Palette.MONEY_DARK, "dollar paper") : null,
                        parked > 0 ? swatch(sand, "parked money") : null,
                        own > 0 ? swatch(Palette.MONEY, "the city's own") : null));
        return box;
    }

    /**
     * HOW LONG IT WOULD LAST: the cover gauge on 0 to 12 months with ticks at
     * THIN_COVER and COMFORTABLE_COVER, its reading at 28 px, the vault
     * against a month of imports, and what THIN_COVER months would need
     * (D12) when it is short.
     */
    VBox coverCard() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        VBox c = card(cardHead(Icons.SAFE, Palette.MONEY, "HOW LONG IT WOULD LAST", COVER_INFO, null));
        if (!(fx.monthlyImports() > 0)) {
            c.getChildren().add(line("Nothing is bought abroad yet, so there is nothing to cover.", null));
            return c;
        }
        double months = fx.importCover();
        int level = ForeignAccounts.coverLevel(months);
        c.getChildren().add(figure(coverMonths(months), 28, coverTone(level)));
        c.getChildren().add(bandTrack(Math.min(1, months / COVER_SCALE),
                new double[] {ForeignAccounts.THIN_COVER / COVER_SCALE, ForeignAccounts.COMFORTABLE_COVER / COVER_SCALE, 1},
                new String[] {Palette.BAD, Palette.WARN, Palette.GOOD},
                List.of(new Tick(ForeignAccounts.THIN_COVER / COVER_SCALE, Palette.TEXT_LABEL, 1,
                                String.format("%.0f", ForeignAccounts.THIN_COVER), null),
                        new Tick(ForeignAccounts.COMFORTABLE_COVER / COVER_SCALE, Palette.TEXT_LABEL, 1,
                                String.format("%.0f months", ForeignAccounts.COMFORTABLE_COVER), null)),
                0, 12, false));
        c.getChildren().add(line(usd(fx.getReservesUsd()) + " against " + d(fx.monthlyImports()) + " a month of imports", null));
        double toThin = fx.toCover(ForeignAccounts.THIN_COVER);
        if (toThin > 0) {
            c.getChildren().add(line(String.format("%.0f months would need %s more", ForeignAccounts.THIN_COVER, d(toThin))
                    + " · " + String.format("%.0f months %s", ForeignAccounts.COMFORTABLE_COVER,
                    d(fx.toCover(ForeignAccounts.COMFORTABLE_COVER))), null));
            c.getChildren().add(doorPill("Buy reserves", Icons.EXCHANGE, Palette.MONEY, () -> openExchange(true)));
        }
        return c;
    }

    /** THE MONEY THAT CAN LEAVE (the spec's C2): what is parked here, what pulls it, and what it has done before. */
    VBox leaveCard() {
        CapitalFlows hot = ui.game.getCapitalFlows();
        Bank bank = ui.game.getBank();
        Gauge g = backing();
        VBox c = card(cardHead(Icons.BANKNOTE, Palette.MONEY, "THE MONEY THAT CAN LEAVE", BACKING_INFO, null));
        if (!(hot.getStock() > 0)) {
            c.getChildren().add(line("Nothing is parked here: nothing can leave on no notice.",
                    "No foreign money is parked in this city's bank. Nothing can leave on no notice, which is a real form "
                            + "of safety and one that a high policy rate gives away."));
        } else {
            HBox reading = new HBox(Palette.GAP, figure(g.reading(), 22, Palette.TEXT_HEAD), chip(g.chip(), g.chipTone()),
                    muted("backed by the vault"));
            reading.setAlignment(Pos.CENTER_LEFT);
            c.getChildren().add(reading);
            c.getChildren().add(cardLine("parked in the bank", dFull(hot.getStock()), null));
            c.getChildren().add(cardLine("as a share of the bank's funding", share1(bank.hotFundingShare()), null));
            c.getChildren().add(cardLine("the excess return pulling it", points(hot.getSpread()), null,
                    "What the city pays over the world rate, less what the world charges it for its own risk. Only the rest "
                            + "is a reason to come — and the same money leaves the moment it stops being one."));
            c.getChildren().add(cardLine("what would come at that rate", dFull(hot.getTarget()), null));
        }
        if (hot.getStopsSuffered() > 0) {
            c.getChildren().add(cardLine("sudden stops since founding", String.valueOf(hot.getStopsSuffered()), null));
        }
        if (hot.getPeakStock() > 0) c.getChildren().add(cardLine("the most that was ever here", dFull(hot.getPeakStock()), null));
        return c;
    }

    /** One of WHAT ELSE MOVED THE VAULT's chips, worked out without drawing it (pure): its words, its colour, its (i). */
    record Moved(String words, String tone, String info) { }

    /**
     * WHAT ELSE MOVED THE VAULT (pure; the spec's E2-E5): the currency's move
     * on its dollars, the central bank's defence - amber the month it sold,
     * an event (section 5) - the land office's dollars out of it, and on a
     * young city the founders' dollars.
     */
    List<Moved> moved() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        List<Moved> out = new ArrayList<>();
        double revalued = fx.getLastVaultRevaluation();
        if (Math.abs(revalued) > .005) {
            out.add(new Moved("the currency moved it " + signedD(revalued) + " this month", Palette.TEXT_LABEL, revalued > 0
                    ? "The currency fell and the same dollars are worth more at home. Nobody was paid anything: it is not "
                      + "cash until they are sold, and a sale is at that day's rate. It is what a reserve is for."
                    : "The currency rose and the same dollars fetch less at home. Nothing was spent and nothing left the "
                      + "vault; sold today, this is what they would raise."));
        }
        if (fx.getDefenceUsdLifetime() > 0) {
            out.add(new Moved(fx.getDefenceUsd() > 0
                    ? "the central bank sold " + usd(fx.getDefenceUsd()) + " defending the currency"
                    : "the defence has sold " + usd(fx.getDefenceUsdLifetime()) + " since founding",
                    fx.getDefenceUsd() > 0 ? Palette.WARN : Palette.TEXT_LABEL,
                    String.format("Sold %s this month defending the currency; %s since founding. The central bank sells "
                            + "when the currency is pushed down on a month the city is short of dollars - up to %.0f%% of "
                            + "what the month wanted, the share falling with the cover - and what they fetch is the central "
                            + "bank's capital spent, not paid to the treasury. That is what the founders' dollars were for; "
                            + "they are not bought back.", usdFull(fx.getDefenceUsd()), usdFull(fx.getDefenceUsdLifetime()),
                            ForeignAccounts.MAX_ABSORPTION * 100)));
        }
        if (fx.getLandUsdLifetime() > 0) {
            out.add(new Moved(fx.getLandUsdFromVaultThisMonth() > 0
                    ? "land took " + usd(fx.getLandUsdFromVaultThisMonth()) + " out of it this month"
                    : fx.getLandUsdFromVaultLifetime() > 0
                    ? "land has taken " + usd(fx.getLandUsdFromVaultLifetime()) + " out of it since founding"
                    : "land cost " + usd(fx.getLandUsdLifetime()) + " since founding, none of it from the vault",
                    Palette.TEXT_LABEL,
                    String.format("Land is priced in US dollars. The city paid %s for it this month and %s since founding; "
                            + "%s of that came out of this vault (%s this month), and the rest was cash converted at the "
                            + "day's rate, which buys exactly the dollars and pays them straight over. The land office's "
                            + "toggle chooses which.", usdFull(fx.getLandUsdThisMonth()), usdFull(fx.getLandUsdLifetime()),
                            usdFull(fx.getLandUsdFromVaultLifetime()), usdFull(fx.getLandUsdFromVaultThisMonth()))));
        }
        if (ui.game.getMonth() <= Game.FOUNDERS_NOTE_MONTHS && fx.getReservesUsd() > 0 && ui.game.getFoundingReserveUsd() > 0) {
            out.add(new Moved("the founders left " + usd(ui.game.getFoundingReserveUsd()) + " in it", Palette.TEXT_LABEL,
                    String.format("The founders left %s in this vault on the first day, bought at %s%.2f to the dollar out "
                            + "of the city's endowment. %s", usdFull(ui.game.getFoundingReserveUsd()), sym(),
                            ForeignAccounts.OPENING_RATE, fx.monthlyImports() > 0
                                    ? "What the vault holds now would pay for " + coverMonths(fx.importCover())
                                      + " of what the city buys abroad."
                                    : "The city is not buying anything abroad yet.")));
        }
        return out;
    }

    VBox movedCard() {
        VBox c = card(cardHead(Icons.SAFE, Palette.MONEY, "WHAT ELSE MOVED THE VAULT", "The treasury's purchases and "
                + "sales are the exchange, below. Besides them three things move the vault: the currency, which revalues "
                + "its dollars; the central bank, which sells them defending a currency pushed down; and the land office, "
                + "when land is paid for out of it.", null));
        List<Moved> all = moved();
        if (all.isEmpty()) {
            c.getChildren().add(line("Nothing else moved it this month.", null));
            return c;
        }
        VBox list = new VBox(6);
        for (Moved m : all) list.getChildren().add(chipInfo(m.words(), m.tone(), m.info()));
        c.getChildren().add(list);
        return c;
    }

    /* ------------------------------ the exchange ------------------------------ */

    /** What a press on the exchange's button says (pure: the probe reads it): GO with the amount; CHOOSE with nothing asked yet - a press asks the smallest step and prices it, and never trades. */
    Press exchangePress() {
        if (tradeExchange <= 0) {
            return new Press(Look.CHOOSE, tradeBuying ? "Buy foreign money" : "Sell foreign money", "choose how much above");
        }
        return new Press(Look.GO, tradeBuying ? "Buy " + d(tradeExchange) + " of foreign money"
                : "Sell " + d(tradeExchange) + " of the vault", tradeBuying
                ? "cash out of the treasury, " + usd(ui.game.getForeignAccounts().toUsd(tradeExchange)) + " into the vault"
                : usd(ui.game.getForeignAccounts().toUsd(tradeExchange)) + " out of the vault, cash into the treasury");
    }

    /** The exchange's "to N months" steps (pure, D12): buying and short, each cover line the cash can reach, as {months, local money}; the shares, all of it and clear are the card's own. */
    List<double[]> exchangeSteps(double ceiling) {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        List<double[]> out = new ArrayList<>();
        if (tradeBuying && fx.monthlyImports() > 0) {
            double thin = fx.toCover(ForeignAccounts.THIN_COVER), comfortable = fx.toCover(ForeignAccounts.COMFORTABLE_COVER);
            if (thin > 0 && thin <= ceiling) out.add(new double[] {ForeignAccounts.THIN_COVER, thin});
            if (comfortable > 0 && comfortable <= ceiling) out.add(new double[] {ForeignAccounts.COMFORTABLE_COVER, comfortable});
        }
        return out;
    }

    /**
     * BUY OR SELL FOREIGN MONEY (the Exchange page until 0.7.35): the toggle,
     * the first sentence with the rest behind the (i), what can be spent or
     * sold, the ask and its steps, what it would do - before and after, the
     * cover on a bullet bar against THIN_COVER - and the button, 0.7.34's
     * action button in the money blue. Selling under THIN_COVER months keeps
     * its alert: it is the moment of choice.
     */
    VBox exchangeCard() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        FlowPane toggle = chipStrip(new String[] {"Buy — cash into the vault", "Sell — the vault into cash"},
                tradeBuying ? "Buy — cash into the vault" : "Sell — the vault into cash", Palette.SIZE_LABEL, pick -> {
                    boolean nowBuying = pick.startsWith("Buy");
                    if (nowBuying != tradeBuying) tradeExchange = 0;
                    tradeBuying = nowBuying;
                    showForeignMenu();
                });
        toggle.setAlignment(Pos.CENTER_RIGHT);
        VBox c = card(cardHead(Icons.EXCHANGE, Palette.MONEY, "BUY OR SELL FOREIGN MONEY", null, toggle));
        c.getChildren().add(line(tradeBuying
                        ? "Local money out of the treasury, foreign money into the vault."
                        : "Foreign money out of the vault, local money into the treasury.",
                tradeBuying
                        ? "Local money out of the treasury, foreign money into the vault. It costs the city cash it could "
                          + "have spent, and it buys cover — months of imports the vault could pay for, which is what the "
                          + "central bank sells to meet a push against the currency."
                        : "Foreign money out of the vault, local money into the treasury. This is how a treasury gets at "
                          + "money it holds abroad, AND it is everything a central bank has ever been able to do about an "
                          + "exchange rate. Both are true at once."));
        if (fx.getBoughtThisMonth() > 0 || fx.getSoldThisMonth() > 0) {
            c.getChildren().add(chips(
                    fx.getBoughtThisMonth() > 0 ? chip("bought " + d(fx.getBoughtThisMonth()) + " this month", Palette.MONEY) : null,
                    fx.getSoldThisMonth() > 0 ? chip("sold " + d(fx.getSoldThisMonth()) + " this month", Palette.MONEY) : null));
        }
        double ceiling = tradeBuying ? Math.max(0, ui.game.getCash()) : fx.sellableReserves();
        tradeExchange = Math.max(0, Math.min(tradeExchange, ceiling));
        c.getChildren().add(cardLine(tradeBuying ? "the treasury holds" : "the vault holds", dFull(ceiling), null));
        if (ceiling <= 0) {
            c.getChildren().add(line(tradeBuying ? "There is no cash to buy with." : "There is nothing in the vault to sell.",
                    tradeBuying
                            ? "The treasury is empty. Buying reserves is spending money the city does not have."
                            : "You cannot sell what you do not hold. A city that needs foreign money and has none has to "
                              + "earn it, borrow it abroad, or let the currency do the work."));
            return c;
        }

        Label asking = words(tradeBuying ? "HOW MUCH TO BUY" : "HOW MUCH TO SELL", Palette.SIZE_LABEL, Palette.TEXT_LABEL);
        Label ask = figure(tradeExchange <= 0 ? "nothing yet" : dFull(tradeExchange), 20,
                tradeExchange > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT);
        FlowPane pick = new FlowPane(6, 6);
        for (double share : new double[] {.05, .25, .5}) {
            double step = ceiling * share;
            if (step <= 0) continue;
            pick.getChildren().add(stepChip("+" + d(step), () -> {
                tradeExchange = Math.min(ceiling, tradeExchange + step);
                showForeignMenu();
            }, false));
        }
        for (double[] to : exchangeSteps(ceiling)) {
            pick.getChildren().add(stepChip(String.format("to %.0f months (%s)", to[0], d(to[1])), () -> {
                tradeExchange = Math.min(ceiling, to[1]);
                showForeignMenu();
            }, false));
        }
        pick.getChildren().add(stepChip("all of it", () -> {
            tradeExchange = ceiling;
            showForeignMenu();
        }, false));
        pick.getChildren().add(stepChip("clear", () -> {
            tradeExchange = 0;
            showForeignMenu();
        }, true));
        VBox askBox = new VBox(4, asking, ask, pick);

        ActionButton button = actionButton(Icons.EXCHANGE, Palette.MONEY, ACTION_TALL, exchangePress(), null);
        button.onPress(() -> {
            if (tradeExchange <= 0) {
                List<double[]> to = exchangeSteps(ceiling);
                tradeExchange = Math.min(ceiling, to.isEmpty() ? ceiling * .05 : Math.min(ceiling * .05, to.get(0)[1]));
                showForeignMenu();
                return;
            }
            if (tradeBuying) ui.game.buyForeignCurrency(tradeExchange);
            else             ui.game.sellForeignCurrency(tradeExchange);
            tradeExchange = 0;
            showForeignMenu();
        });

        if (tradeExchange <= 0) {
            c.getChildren().addAll(askBox, button);
            return c;
        }

        double sign = tradeBuying ? 1 : -1;
        double coverNow = fx.importCover(), coverAfter = fx.coverWith(sign * tradeExchange);
        boolean imports = fx.monthlyImports() > 0;
        VBox after = new VBox(6, words("WHAT IT WOULD DO", Palette.SIZE_LABEL, Palette.TEXT_LABEL),
                cardLine("in the vault", d(fx.getReserves()) + "  →  " + d(fx.getReserves() + sign * tradeExchange), null),
                cardLine("in the treasury", d(ui.game.getCash()) + "  →  " + d(ui.game.getCash() - sign * tradeExchange), null),
                cardLine("import cover", imports ? coverMonths(coverNow) + "  →  " + coverMonths(coverAfter) : "nothing to cover",
                        imports ? coverTone(ForeignAccounts.coverLevel(coverAfter)) : Palette.TEXT_SPENT),
                cardLine("in " + Currency.FOREIGN_CODE, usdFull(fx.toUsd(tradeExchange)) + " at " + perUsd(fx.getRate()), null));
        if (imports) {
            after.getChildren().add(bulletBar(Math.min(COVER_SCALE, coverAfter), ForeignAccounts.THIN_COVER, Palette.MONEY, 0,
                    String.format("the cover after it, against the %.0f-month line", ForeignAccounts.THIN_COVER)));
        }
        after.getChildren().add(noteLine("The cost of a defence is not the cash.", "The cost of a defence is not the cash. "
                + "It is the cover you no longer have — which is why the two figures are shown before and after.", 560));
        GridPane two = equalColumns(2, 24);
        two.add(askBox, 0, 0);
        two.add(after, 1, 0);
        c.getChildren().add(two);
        if (!tradeBuying && imports && coverAfter < ForeignAccounts.THIN_COVER && coverNow >= ForeignAccounts.THIN_COVER) {
            c.getChildren().add(actionCard(Icons.ALERT, Palette.BAD,
                    String.format("This would take the city under %.0f months of cover", ForeignAccounts.THIN_COVER),
                    "Below the line the world prices the currency for a crisis.",
                    String.format("Below %.0f months the world stops pricing the currency on the trade balance and starts "
                            + "pricing it for a crisis. Selling reserves to defend a rate, and ending up under the line doing "
                            + "it, is the sequence every currency crisis in the literature has in common.",
                            ForeignAccounts.THIN_COVER), null));
        }
        c.getChildren().add(button);
        return c;
    }

    /** The two claims, side by side (the spec's R5, under "details"), and what was walked away from (R6). */
    Node claimsGrid() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        double debt = fx.getForeignDebt(), parked = ui.game.getCapitalFlows().getStock();
        VBox column = column();
        column.getChildren().add(statementHead("The two claims are not alike"));
        GridPane t = grid(new double[] {160, 126, 130, 130}, rightAfterFirst(4));
        gridHead(t, "", "amount", "when it is due", "whose claim");
        t.add(gridCell("Dollar paper", Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, 1);
        t.add(gridCell(money(debt), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, 1);
        t.add(gridCell("on a maturity date", Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, 1);
        t.add(gridCell("the treasury's", Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 3, 1);
        t.add(gridCell("Parked money", Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, 2);
        t.add(gridCell(money(parked), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, 2);
        t.add(gridCell("whenever it likes", Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, 2);
        t.add(gridCell("the bank's", Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 3, 2);
        column.getChildren().add(t);
        column.getChildren().add(statementNote("The city's own figure for what it is worth abroad takes off the paper and "
                + "nothing else. This tab takes off the parked money too, and that is the right addition: a claim that can "
                + "be exercised at no notice is a heavier claim than a bond with a date on it, not a lighter one."));
        if (fx.getRepudiated() > 0) {
            column.getChildren().add(statementHead("...and what was walked away from"));
            column.getChildren().add(statementLine("Repudiated", moneyFull(fx.getRepudiated()), Palette.BAD));
        }
        return column;
    }
}
