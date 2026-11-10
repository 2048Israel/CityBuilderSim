package ham.citybuildersim.ui;

import ham.citybuildersim.*;
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

    /** The Partners page and the nation's behind it (WN6): its choices - the nation, the span, the good - survive the redraw in its fields. */
    final PartnersPage partners;

    /** The reserves in eight (WN6): the vault's table of the eight moneys and the years behind it. */
    private final VaultTable vault;

    TradeScreen(UserInterface ui) {
        this.ui = ui;
        this.partners = new PartnersPage(ui, this);
        this.vault = new VaultTable(ui, this);
    }

    /** The nodes this screen made at its last draw, handed back to it at the next (KP6; study UP4, docs/studies/spec-screens-in-place.md). */
    final Kept.Pool pool = new Kept.Pool();

    /** Every node is made new at the next draw: another city opened (UserInterface.anotherCity()). */
    void forget() {
        pool.forget();
    }

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

    /** The six pages, in the strip's order (Partners since WN6). */
    static final String OVERVIEW = "Overview", MONTH = "The month", GOODS = "What we trade", PARTNERS = "Partners",
            CURRENCY = "The currency", RESERVES = "The reserves";

    /** ...as the strip lists them. */
    static final String[] PAGES = {OVERVIEW, MONTH, GOODS, PARTNERS, CURRENCY, RESERVES};

    /** ...and each one's icon: the tiles, the coin, the lorry, the flag, the two arrows, the safe. */
    static final String[] PAGE_ICONS = {Icons.OVERVIEW, Icons.COIN, Icons.LORRY, Icons.FLAG, Icons.EXCHANGE, Icons.SAFE};

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

    /** ...and in which money (WN4): a code of the nine (the eight and "XAU"), or null for the reserve mix when buying and the largest holding when selling. Kept as tradeExchange is, so a redraw on the clock leaves it. */
    String tradeMoney = null;

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
            LEAVE = "#leave", COVER = "#cover", GOODS_CARD = "#goods", EIGHT_CARD = "#eight";

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

    /** How far from parity, in words: "26.2% stronger than parity", "at parity", "held fixed" - and "1.2M times parity" for a rate at its guard (farFrom()). */
    static String parityWords(ForeignAccounts fx) {
        if (fx.isPinned()) return "held fixed";
        return farFrom(fx.deviationFromParity(), "parity");
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

    /* ------------------------------ the frame ------------------------------ */

    /**
     * The tab's entry point: the Overview, or the page the player was on.
     * Named for the shell, which calls it from the rail and redraws it on
     * the clock; Finances' and Government's doors call it too (the drawer's
     * THE CURRENCY row calls open(), since 0.7.35).
     */
    void showForeignMenu() {
        // Kept (KP6): the page is drawn over last time's nodes when the same screen is redrawn; the rail's own tab arrives afresh.
        ui.keepMenu("showForeignMenu", () -> showForeignMenu(), pool, this::drawPage);

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

    /**
     * The page itself, put in the window through setPage(): what keepMenu runs as a kept draw from the pool, and what
     * KeptBench runs kept and built new for KeptCheck - the page `tradePage` names (a nation's, on Partners, when
     * `partners.nation` names one), in the view the river's and the goods' toggles name. What the show method did
     * before the draw (a door's page taken, the page known, the door targets cleared, the month's first draw told) is
     * done here, in the draw, so the bench's draw has it too.
     */
    void drawPage() {
        if (tradeArea != null) {
            tradePage = tradeArea;
            tradeArea = null;
        }
        boolean known = false;
        for (String p : PAGES) known |= p.equals(tradePage);
        if (!known) tradePage = TRADE_HOME;
        targets.clear();

        // The figures count up and the bars grow on the first draw of a month and no other, and only at a clock speed
        // a figure can follow (UserInterface.countsUp(), KP1's rule): at 50x a month lands every 100 ms.
        boolean fresh = ui.game.getMonth() != drawnMonth && ui.countsUp();
        drawnMonth = ui.game.getMonth();

        // Kept (KP6): the whole page - its frame, its scroller and its page - is a scope keyed by the page (and on Partners the
        // nation, 0 for its table), so another page or another nation shares no node with the last and opens at its top; the
        // river's and the goods' toggles are scopes of their own on their pages, so a toggle keeps the scroll. A month's
        // redraw of the same page keeps every node and the scroll.
        Kept.scope(tradePage + (PARTNERS.equals(tradePage) ? "/" + partners.nation : ""), () -> {
            // ...the page itself in a scope of its own, so what its sections make shifts none of the frame's nodes.
            VBox page = Kept.scope("page", () -> {
                VBox p = widePage();
                p.getChildren().addAll(actionCards());
                switch (tradePage) {
                    case MONTH    -> monthPage(p);
                    case GOODS    -> goodsPage(p, fresh);
                    case PARTNERS -> partners.page(p);
                    case CURRENCY -> currencyPage(p);
                    case RESERVES -> reservesPage(p);
                    default       -> overviewPage(p, fresh);
                }
                return p;
            });
            FlowPane strip = Kept.scope("strip",
                    () -> chipStrip(PAGES, PAGE_ICONS, tradePage, Palette.SIZE_LABEL, this::open));
            VBox frame = Kept.column(Palette.GAP, Kept.scope("head", this::head), strip,
                    Kept.scope("five", () -> kpiStrip(fresh)));
            frameOver(frame, page);
        });
    }

    /** One of the six pages, at its top: a chip picked, or a door. Partners opens on its table, not on the nation last looked at. */
    void open(String page) {
        if (PARTNERS.equals(page)) partners.nation = 0;
        open(page, null);
    }

    /** A nation's page on Partners (the map card's "Its trade with us", the Overview's partners strip, a row of the table), at its top. */
    void openNation(int nation) {
        partners.nation = nation;
        open(PARTNERS, null);
    }

    /** ...scrolled to a card on it (the doors land where the figure is taken apart). */
    void open(String page, String target) {
        tradeArea = null;
        tradePage = page;
        scrollTarget = target;
        ui.innerScrollAt.remove("showForeignMenu:body");
        if (target == null) toTop();
        showForeignMenu();
    }

    /**
     * The page on show scrolled to its top (KP6): a chip or a door picked draws its page from the top, and another page is a
     * scope of its own with a scroller of its own, which opens there; but the page the pick names may be the one already on
     * show, whose kept scroller stays where the player left it unless it is put back.
     */
    private void toTop() {
        if (body != null && body.getVvalue() != 0) body.setVvalue(0);
    }

    /** The Overview, at its top. */
    void openOverview() { open(OVERVIEW); }

    /** The exchange card, buying or selling - an ask the other way cleared: the cover alert's "Buy reserves". */
    void openExchange(boolean buying) {
        if (buying != tradeBuying) { tradeExchange = 0; tradeMoney = null; }
        tradeBuying = buying;
        open(RESERVES, EXCHANGE_CARD);
    }

    /** The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (BankScreen's). */
    private void frameOver(VBox frame, VBox page) {
        frame.setMaxWidth(PAGE_WIDE);
        frame.setFillWidth(true);
        frame.setPadding(Pieces.FRAME_ROOM);
        body = ui.scrolled(page, FRAME_CHROME);
        final javafx.scene.control.ScrollPane scroller = body;
        // Kept (KP6): both bindings are made once per scroller, since a kept page is handed back each draw with the
        // scroller and frame it is already bound to; a draw never unbinds and binds again.
        if (scroller.getProperties().get(BOUND_TO_FRAME) != frame) {
            scroller.prefHeightProperty().unbind();
            scroller.prefHeightProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                    () -> Math.max(260, ui.menuScroller.getHeight()
                            - (frame.getHeight() > 0 ? frame.getHeight() + STAGE_REST : FRAME_CHROME)),
                    ui.menuScroller.heightProperty(), frame.heightProperty()));
            scroller.getProperties().put(BOUND_TO_FRAME, frame);
        }
        if (page.getProperties().get(BOUND_TO_SCROLLER) != scroller) {
            page.prefWidthProperty().unbind();
            page.prefWidthProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                    () -> Math.min(PAGE_WIDE, Math.max(320, scroller.getViewportBounds().getWidth())),
                    scroller.viewportBoundsProperty()));
            page.getProperties().put(BOUND_TO_SCROLLER, scroller);
        }
        ui.setPage(frame, scroller);
    }

    /** The key in a kept scroller's properties for the frame its height is bound to, and in a kept page's for the scroller its width is bound to (frameOver()). */
    private static final String BOUND_TO_FRAME = "trade.frame", BOUND_TO_SCROLLER = "trade.scroller";

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
            + "readings that decide whether a shock is survivable. The five pages behind it take each one apart.";

    /** Each page's (i), in PAGES' order after the Overview: what it holds (the landing's row blurbs until 0.7.35). */
    static final String[] PAGE_INFO = {
        "Every dollar that crossed the city's edge this month, and which way: a walk from what it sold abroad to the "
                + "month's balance, through the two accounts - or the same month as a river. Under it, what the city "
                + "holds abroad and what the world holds here. The statement is under \"details\".",
        "What the city sells abroad and what it buys, good by good or business by business, from the businesses' own "
                + "books; the last ten years, the record since founding, and what the world pays and charges.",
        "Which nations the city trades with, the month or the year so far, each with its money and its haul; and, for a "
                + "good, where the next unit goes and what it nets there, the good's band this month and what moves it. A "
                + "nation opens on a page of its own.",
        "What a US dollar costs, and what put it there: the rate against parity and against the fair level, its "
                + "history with the city's decisions on it, what moved it this month force by force - parity's trend, the "
                + "real rate, the balance, the vault's cover, interventions and the market - and the steps from parity to "
                + "the rate. Then what the rate is doing to the dollar debt and the vault.",
        "What the vault holds in each of the eight moneys and in gold, whose it is and how long it would last; the money that "
                + "can leave on no notice; what else moved the vault; and the exchange - cash into the vault, or the "
                + "vault into cash.",
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
        // ...and to how many nations, where the month's table of partners has them (WN6).
        String to = partners.reachWords(true), from = partners.reachWords(false);
        out.add(counted
                ? new Cell("SOLD ABROAD", d(fx.getExports()), fx.monthlyExports() > 0
                        ? d(fx.monthlyExports()) + " a month on average" + (to == null ? "" : ", " + to) : "nothing yet",
                        Palette.TEXT_HEAD, "What we trade: every good, sold and bought")
                : new Cell("SOLD ABROAD", NOT_COUNTED, NOT_COUNTED_NOTE, Palette.TEXT_SPENT,
                        "What we trade: every good, sold and bought"));
        out.add(counted
                ? new Cell("BOUGHT ABROAD", d(fx.tradeImports()), fx.monthlyImports() > 0
                        ? d(fx.monthlyImports()) + " a month on average" + (from == null ? "" : ", " + from) : "nothing yet",
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
            // Kept (KP6): drawn only sometimes, so a scope of its own: it shifts no label after it.
            cells[3].getChildren().add(Kept.scope("moved", () -> {
                Label l = Kept.label(moved);
                l.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
                return l;
            }));
        }
        HistorySave h = ui.game.getHistorySave();
        boolean counts = fresh && fx.isMonthCounted();
        countFrom(cells[0], h, "exportsAbroad", fx.getExports(), counts);
        countFrom(cells[1], h, "importsAbroad", fx.tradeImports(), counts);
        return vitalsBar(cells);
    }

    /**
     * A cell's figure counted up from the month before's, History's second-last point (SectorScreen.countUp()) when `counts`;
     * else (KP6) the count the last draw gave the figure stopped, for a draw that does not count - a clock too fast to follow,
     * a redraw in the same month, a month not counted - or it would write that month's figure over this one's.
     */
    private void countFrom(VBox cell, HistorySave h, String series, double now, boolean counts) {
        if (!(cell.getChildren().size() > 1) || !(cell.getChildren().get(1) instanceof Label figure)) return;
        double[] s = counts ? h.aligned(series) : new double[0];
        if (s.length < 2) {
            SectorScreen.stopCounting(figure);
            return;
        }
        double before = s[s.length - 2];
        if (Double.isFinite(before)) SectorScreen.countUp(figure, before, now, this::d);
        else SectorScreen.stopCounting(figure);
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
        // Kept (KP6): each card is a scope of its own, since they come and go with the city's state.
        if (hot.isStopped()) {
            int left = hot.stopMonthsLeft();
            String months = left + " month" + (left == 1 ? "" : "s");
            out.add(Kept.scope("leaving", () -> actionCard(Icons.ALERT, Palette.BAD, "The money is leaving",
                    String.format("%s · %s before anybody abroad looks at this city again.", hot.getStopReason(), months),
                    String.format("%s. %s before anybody abroad looks at this city again. Nothing new will be lent here "
                            + "and what is here is going home.", hot.getStopReason(), months),
                    doorPill("The money that can leave", Icons.SAFE, Palette.MONEY, () -> open(RESERVES, LEAVE)))));
        }
        if (fx.monthlyImports() > 0 && fx.importCover() < ForeignAccounts.THIN_COVER) {
            out.add(Kept.scope("thin cover", () -> actionCard(Icons.ALERT, Palette.BAD,
                    String.format("Under %.0f months of import cover", ForeignAccounts.THIN_COVER),
                    String.format("The vault holds %s against %s a month of imports.",
                            d(fx.getReserves()), d(fx.monthlyImports())),
                    String.format("The vault holds %s against %s a month of buying abroad. %.0f months is the line below "
                            + "which the world stops pricing a currency on its trade balance and starts pricing it for a "
                            + "crisis.", d(fx.getReserves()), d(fx.monthlyImports()), ForeignAccounts.THIN_COVER),
                    doorPill("Buy reserves", Icons.EXCHANGE, Palette.MONEY, () -> openExchange(true)))));
        }
        if (hot.getStock() > 0 && fx.getReserves() / hot.getStock() < CapitalFlows.PANIC_BACKING) {
            double backing = fx.getReserves() / hot.getStock();
            out.add(Kept.scope("barely backed", () -> actionCard(Icons.ALERT, Palette.BAD, "The hot money is barely backed",
                    String.format("%s parked here against %s in the vault: %.0f%% cover.",
                            d(hot.getStock()), d(fx.getReserves()), backing * 100),
                    String.format("%s of foreign money is parked in this city's bank and the vault holds %s against it — "
                            + "%.0f%% cover. Under %.0f%% the next shock stops being a wobble and becomes a run.",
                            d(hot.getStock()), d(fx.getReserves()), backing * 100, CapitalFlows.PANIC_BACKING * 100),
                    doorPill("The money that can leave", Icons.SAFE, Palette.MONEY, () -> open(RESERVES, LEAVE)))));
        }
        if (fx.netForeignPosition() < 0 && fx.getForeignDebt() > 0) {
            out.add(Kept.scope("owes more", () -> actionCard(Icons.ALERT, Palette.WARN, "The city owes the world more than it holds",
                    String.format("%s of dollar paper against %s of reserves.", d(fx.getForeignDebt()), d(fx.getReserves())),
                    String.format("%s of dollar paper against %s of reserves. A city with deep reserves and deeper dollar "
                            + "debt is not rich, and the reserve line on its own says it is.",
                            d(fx.getForeignDebt()), d(fx.getReserves())),
                    doorPill("The dollar debt", Icons.EXCHANGE, Palette.MONEY, () -> open(CURRENCY, DEBT)))));
        }
        return out;
    }

    /** One action card: its icon square, its title, one line with the whole behind an (i), and its door (null: none), on the raised ground with a 3 px top edge in `tone` (BankScreen's). */
    static VBox actionCard(String svg, String tone, String title, String line, String whole, Node act) {
        Label t = Kept.label(title);
        t.setWrapText(true);
        t.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        HBox head = Kept.row(Palette.GAP, iconSquare(svg, tone, 28, 15), t);
        head.setAlignment(Pos.CENTER_LEFT);
        VBox c = Kept.column(8, head);
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
        return wordsIn(text, BuildScreen.wordsAt(size, tone));
    }

    /** Words that wrap, in a style of their own (KP2b: a total's, strong): made so, not made in the plain style and set again. */
    static Label wordsIn(String text, String style) {
        Label l = Kept.label(text);
        l.setWrapText(true);
        l.setMinWidth(0);
        l.setStyle(style);
        return l;
    }

    /** A figure that is never cut, at a size, in a colour. */
    static Label figure(String text, double size, String tone) {
        Label l = Kept.label(text);
        l.setWrapText(false);
        l.setMinWidth(Region.USE_PREF_SIZE);
        l.setStyle(BuildScreen.figureAt(size, tone));
        return l;
    }

    /** A card's head: its icon in a square tinted in its colour, its title in capitals, an (i) when `info` is not null, and at its right `right` (null: nothing) - BankScreen's. */
    static HBox cardHead(String svg, String colour, String title, String info, Node right) {
        Label t = Kept.label(title);
        t.setWrapText(true);
        t.setMinWidth(0);
        t.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        HBox row = Kept.row(Palette.GAP);
        if (svg != null) row.getChildren().add(iconSquare(svg, colour, 28, 15));
        row.getChildren().add(t);
        if (info != null) row.getChildren().add(infoButton(info, true));
        if (right != null) {
            Region gap = Kept.region();
            HBox.setHgrow(gap, Priority.ALWAYS);
            row.getChildren().addAll(gap, right);
        }
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 12. */
    static VBox card(Node... rows) {
        VBox c = Kept.column(8);
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

    /**
     * A card that opens somewhere: the accent edge under the pointer, and a click goes. A door inside it opens itself. Made as a door, in its
     * look (KP2b): it was made by card() in the plain look and then set again, two writes to the style of a node a kept page hands back, at
     * each draw. The look the pointer calls for is chosen from isHover() where it is set (KP6), and the handlers are set, not added.
     */
    static VBox doorCard(Runnable go, Node... rows) {
        VBox c = Kept.column(8);
        for (Node n : rows) if (n != null) c.getChildren().add(n);
        c.setMaxWidth(Double.MAX_VALUE);
        String rest = cardStyle(Palette.EDGE) + " -fx-cursor: hand;";
        String over = rest.replace(Palette.EDGE + ";", Palette.ACCENT + ";");
        c.setStyle(c.isHover() ? over : rest);
        c.setOnMouseEntered(e -> c.setStyle(over));
        c.setOnMouseExited(e -> c.setStyle(rest));
        c.setOnMouseClicked(e -> go.run());
        return c;
    }

    /** A line of a card: words at the left, wrapping, and a figure at the right. */
    static HBox cardLine(String label, String value, String tone) {
        return cardLine(label, value, tone, null);
    }

    /** ...with an (i) after its words. */
    static HBox cardLine(String label, String value, String tone, String info) {
        // Kept (KP6): a scope of its own, so the words that take the row's room (and the (i) some have) are always a card line's; a line
        // whose figure says nothing in a scope of its own again, since a label that had words and has none keeps the height of them.
        return Kept.scope(value.isEmpty() ? "cardLine/-" : "cardLine", () -> {
            Label what = words(label, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL);
            HBox.setHgrow(what, Priority.ALWAYS);
            what.setMaxWidth(Double.MAX_VALUE);
            Label v = figure(value, Palette.SIZE_LABEL + 1, tone == null ? Palette.TEXT_HEAD : tone);
            HBox row = Kept.row(Palette.GAP, what, v);
            if (info != null) row.getChildren().add(1, infoButton(info, true));
            row.setAlignment(Pos.CENTER_LEFT);
            return row;
        });
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
        // Kept (KP6): a scope of its own, so the (i) some have shifts no node of a chip after it.
        return Kept.scope("chipInfo", () -> {
            HBox row = Kept.row(Palette.GAP_TIGHT, chip(text, colour));
            if (whole != null) row.getChildren().add(infoButton(whole, true));
            row.setAlignment(Pos.CENTER_LEFT);
            return row;
        });
    }

    /** A row of chips, wrapping. */
    static FlowPane chips(Node... cs) {
        FlowPane row = Kept.flow(8, 6);
        for (Node n : cs) if (n != null) row.getChildren().add(n);
        return row;
    }

    /** A statement column, for a fold: the old rows at their old width. */
    static VBox column(Node... rows) {
        VBox c = Kept.column(0);
        for (Node n : rows) if (n != null) c.getChildren().add(n);
        c.setMaxWidth(STATEMENT);
        return c;
    }

    /** A statement's note in a scope of its own (KP6): a note drawn only sometimes, or a table's rows in a number that changes, shifts no label after it. */
    static Label statementNote(String text) {
        return Kept.scope("note", () -> Statement.statementNote(text));
    }

    /** A fold kept on this screen: "details ▸ caption". */
    VBox fold(String key, String caption, java.util.function.Supplier<Node> inside) {
        // Kept (KP6): a fold is a scope keyed by its own key, its insides built only while it is open - they shift no node after it.
        return Kept.scope(key, () -> details(key, caption, openLines, this::showForeignMenu, inside));
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
        FlowPane key = Kept.flow(14, 4);
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
        // Kept (KP6): each section a scope of its own, so what one draws only sometimes shifts no node of another.
        VBox trade = Kept.scope("trade", () -> tradeCard(OVERVIEW_GOODS, fresh, false));
        VBox money = Kept.scope("currency", this::currencyCard);
        money.setMinWidth(400);
        money.setPrefWidth(458);
        money.setMaxWidth(458);
        HBox.setHgrow(trade, Priority.ALWAYS);
        HBox hero = Kept.row(TILE_GAP, trade, money);
        hero.setAlignment(Pos.TOP_LEFT);
        page.getChildren().add(hero);
        // ...and under it who the month's trade was with, the top five nations as doors to their pages (WN6); nothing where there is no partner.
        Node strip = Kept.scope("partners", partners::strip);
        if (strip != null) page.getChildren().add(strip);
        page.getChildren().add(Kept.scope("gauges head", () -> sectionHead("WHAT DECIDES WHETHER A SHOCK IS SURVIVABLE",
                GAUGES_INFO, quiet("the same three every month"))));
        page.getChildren().add(row(Kept.scope("cover", this::coverGauge), Kept.scope("backing", this::backingGauge),
                Kept.scope("parity", this::parityGauge)));
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
     * fuel was a good (0.7.62); they are FUEL's buyers now - the households
     * PETROL's and the railway DIESEL's since 0.7.76, and since 0.7.83 the
     * grocers PETROL's for the drivers and the vans DIESEL's (a save from
     * before keeps the households' month it struck).
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
            s.append(who(e.getKey())).append(' ').append(d(e.getValue()));
        }
        return s.toString();
    }

    /** A buyer's or seller's name in words: a sector's key, "the households", and since 0.7.85 "the city's reserve" (Sectors.CITY: its crude). */
    static String who(String key) {
        return Sectors.HOUSEHOLDS.equals(key) ? "the households" : Sectors.CITY.equals(key) ? "the city's reserve" : key;
    }

    /** ...and whether it has a sector's page to open: not the households, not the city (0.7.85). */
    static boolean hasPage(String key) {
        return !Sectors.HOUSEHOLDS.equals(key) && !Sectors.CITY.equals(key);
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
                            + (householdFuel > 0 ? dFull(householdFuel) + " of petrol" : ""), null, null));
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
        // Made as a door (KP2b) unless it is the page's own and has nothing to show.
        Node head = cardHead(Icons.LORRY, Palette.BUSINESS, "WHAT WE TRADE", TRADE_INFO, quiet(hint));
        VBox c = page && all.isEmpty() ? card(head) : doorCard(() -> open(GOODS), head);
        if (all.isEmpty()) {
            c.getChildren().add(Kept.scope("nothing", () -> line("Nothing crossed the city's edge this month.", NOTHING_CROSSED)));
            return c;
        }
        List<GoodRow> top = all.subList(0, Math.min(shown, all.size()));
        MirrorRows bars = Kept.scope("bars", () -> mirror(top, "BOUGHT ABROAD " + d(t.bought()), "SOLD ABROAD " + d(t.sold()), false, true));
        // The bars grow out of their axis on the month's first draw (fresh: UserInterface.countsUp() held); any other draw stands them at rest.
        if (fresh && fx.isMonthCounted()) bars.grow(GROW_MILLIS);
        else bars.stand();
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
            c.getChildren().add(Kept.scope("more", () -> door(more, Palette.ACCENT, () -> open(GOODS))));
        }
        return c;
    }

    /** WHAT WE TRADE's (i). */
    static final String TRADE_INFO = "Every good the city's businesses sold abroad and bought abroad this month, from "
            + "their own books - each line of revenue and of cost split home and abroad as it was traded - so it adds to "
            + "SOLD ABROAD and BOUGHT ABROAD to the dollar. The grocers' filling stations buy the drivers' petrol, and the "
            + "railway and the businesses' vans their diesel, off the city's refineries first and from the world for the rest. "
            + "Every world price is quoted in the world's money and converted at the rate, so a weaker "
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
        VBox box = Kept.column(6);
        Label title = Kept.label(good.label());
        title.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        box.getChildren().add(title);
        String main = null;
        if (g != null && g.sold() > 0) {
            box.getChildren().add(cardLine("sold abroad", d(g.sold()), null));
            for (Map.Entry<String, Double> e : sorted(g.sellers())) box.getChildren().add(cardLine("   by " + who(e.getKey()), d(e.getValue()), Palette.TEXT_LABEL));
            String firstSeller = sorted(g.sellers()).get(0).getKey();
            if (hasPage(firstSeller)) main = firstSeller;
        }
        if (g != null && g.bought() > 0) {
            box.getChildren().add(cardLine("bought abroad", d(g.bought()), null));
            for (Map.Entry<String, Double> e : sorted(g.buyers())) {
                box.getChildren().add(cardLine("   by " + who(e.getKey()), d(e.getValue()), Palette.TEXT_LABEL));
            }
            if (main == null) {
                String first = sorted(g.buyers()).get(0).getKey();
                if (hasPage(first)) main = first;
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
        // ...and petrol at the pump (0.7.83, batch O6): what the grocers' forecourts charge the drivers on it.
        if (good == Good.PETROL) {
            ham.citybuildersim.sectors.Retail grocers = ui.game.getSectors().retail();
            double pump = grocers.pumpPriceToday(ui.game);
            if (pump > 0) {
                box.getChildren().add(cardLine("at the pump today", unitD(pump) + " a litre", null));
                box.getChildren().add(muted(PUMP_WORDS));
            }
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

    /** Petrol's popover, under the pump price (0.7.83): where it comes from - the fuel duty on the sign since 0.7.103. */
    static final String PUMP_WORDS = String.format("The grocers' filling stations buy the drivers' petrol at wholesale - the"
            + " city's refineries first, the world for the rest - and sell it %.0f%% dearer with the sales tax and the city's"
            + " fuel duty on top.",
            ham.citybuildersim.sectors.Retail.PUMP_MARGIN * 100);

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
        HBox big = Kept.row(6, rate, per);
        big.setAlignment(Pos.BASELINE_LEFT);
        VBox c = doorCard(() -> open(CURRENCY), cardHead(Icons.EXCHANGE, Palette.MONEY, "THE CURRENCY", RATE_INFO, null), big,
                muted(oneOfOurs() + " · parity " + sym() + fxRate(fx.getParity())));
        addRegimeLine(c);
        int level = fx.isPinned() ? 0 : ForeignAccounts.parityLevel(fx.deviationFromParity());
        c.getChildren().add(chips(chip(parityWords(fx), level == 0 ? Palette.TEXT_MUTED : parityTone(level))));
        if (h.months() >= 2) {
            c.getChildren().add(Kept.scope("chart", () -> rateChart(h, null, false, 426, 140)));
        }
        double year = h.changeOver("fxRate", 12);
        c.getChildren().add(Kept.scope("year", () -> muted(Double.isFinite(year)
                ? (Math.abs(year) < .0005 ? "no move in a year" : share1(Math.abs(year)) + (year > 0 ? " weaker" : " stronger") + " than a year ago")
                : "not a year recorded yet")));
        return c;
    }

    /**
     * How the D$ is held, in one muted line, when it is not Lean (XR2; pure):
     * "Held at a peg of D$1.300", "In a band of D$1.261-D$1.339", "Floating
     * freely". Null under Lean, so the card is exactly as it was.
     */
    String regimeLine() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        return switch (fx.getRegime()) {
            case LEAN -> null;
            case FLOAT -> "Floating freely";
            case BAND -> "In a band of " + sym() + fxRate(fx.bandStrongEdge()) + "-" + sym() + fxRate(fx.bandWeakEdge());
            // The peg kept in another money than the US$ (WN4b): the D$ per US$ it holds this month, and the peg itself in its own money.
            case PEG -> "Held at a peg of " + sym() + fxRate(fx.getPegRate())
                    + (fx.getPegAnchor() == 0 ? "" : " per " + Currency.FOREIGN_SYMBOL + " this month, " + sym() + fxRate(fx.getPegPerAnchor())
                            + " per " + PolicyScreen.anchorMark(fx.getPegAnchor()));
        };
    }

    /** The regime's line and its door to Policy › Money › The currency, on a card of the rate; nothing under Lean. */
    void addRegimeLine(VBox card) {
        String regime = regimeLine();
        if (regime == null) return;
        // Kept (KP6): drawn only sometimes, so a scope of its own each: they shift no node after them.
        card.getChildren().add(Kept.scope("regime", () -> muted(regime)));
        card.getChildren().add(Kept.scope("regime door", () -> doorPill("Policy › The currency", Icons.POLICY, Palette.MONEY,
                () -> ui.policyScreen.open(PolicyScreen.MONEY, PolicyScreen.CURRENCY_PAGE))));
    }

    /** THE CURRENCY's (i): the unit, and parity (the spec's Q1, Q3). */
    static final String RATE_INFO = "How many of the city's dollars one US dollar costs. Higher is a weaker currency: "
            + "imports cost more at home and what the city sells abroad earns more. Parity is where a basket of goods "
            + "costs the same here and abroad - the city's own basket, with rent held to a third of it, against the "
            + "world's - and the currency follows its trend one for one. The real rate, the balance, the vault's cover and "
            + "the market's own swing put it above or below.";

    /** The real 2015 US$'s (i) (D29): the game has two US dollars, and this is the one the nations' output is counted in. */
    static final String REAL_USD_INFO = "The game has two US dollars. The one above is the in-game one, whose prices move with the world's. "
            + "This is the real 2015 US$, the unit the nations' output is counted in: what one costs in the city's dollars, the rate "
            + "times the US dollar's prices since the city was founded. It is shown to compare the city with the nations, and for "
            + "nothing else: nothing in the city's economy reads it.";

    /** THE RATE's line for the D$ the goods are struck at (AN2): what the traded goods are priced at, and how it follows the rate (AN1's PASS_THROUGH). */
    static final String STRUCK_INFO = String.format("What the city's traded goods are priced at: the dock's imports and exports, the band "
            + "the city's own goods trade in and the materials in a building's cost are each the world's price, in its own money, times "
            + "this rate - not the rate of the month. Each month it closes 1/%.0f of its gap to the rate, so a step in the rate reaches "
            + "the goods in full, half of it in about %.0f months. The rate itself still converts the vault, the debts and the land office.",
            1 / EconomyManager.PASS_THROUGH, Math.log(.5) / Math.log(1 - EconomyManager.PASS_THROUGH));

    /** The real effective rate's (i) (D29). */
    static final String REAL_EFFECTIVE_INFO = "The city's dollar against the moneys of the nations it trades with, each weighted by how much the "
            + "city traded with it over the last finished year, with the prices taken out of both ends. It is 100 at founding. Above 100 "
            + "the city's dollar is dearer in real terms than it was and its goods cost the partners more; below 100, cheaper. It is "
            + "shown to compare, and for nothing else: nothing in the city's economy reads it. The line under it names the partners it weighs.";

    /** How many partners the line under the real effective rate names before it says how many more there are (WN5b). */
    static final int WEIGHS_NAMED = 4;

    /**
     * The line under the real effective rate (WN5b): "Weighs Germany 50% · Japan 25% · United States 25%", the WEIGHS_NAMED largest shares in whole
     * percents, most weight first and a tie by nation number, then " · 12 more" for the partners past them; where the rate weighs no partner, "Weighs
     * the US$ alone: no nation's trade to weigh yet", or "...: this world has no nations' moneys" where it has none (no world file). The figures are
     * the getter's (Game.realEffectivePartners()); this only orders and words them.
     */
    static String weighsWords(List<RealDollar.Weighed> weighed, Nations nations, boolean moneys) {
        if (weighed.isEmpty()) return "Weighs the " + Currency.FOREIGN_SYMBOL + " alone: " + (moneys ? "no nation's trade to weigh yet" : "this world has no nations' moneys");
        List<RealDollar.Weighed> order = new ArrayList<>(weighed);
        order.sort((a, b) -> a.share() != b.share() ? Double.compare(b.share(), a.share()) : Integer.compare(a.nation(), b.nation()));
        StringBuilder words = new StringBuilder("Weighs ");
        for (int i = 0; i < Math.min(WEIGHS_NAMED, order.size()); i++) {
            long percent = Math.round(100 * order.get(i).share());
            words.append(i > 0 ? " · " : "").append(nations.name(order.get(i).nation())).append(' ').append(percent < 1 ? "<1" : String.valueOf(percent)).append('%');
        }
        if (order.size() > WEIGHS_NAMED) words.append(" · ").append(order.size() - WEIGHS_NAMED).append(" more");
        return words.toString();
    }

    /**
     * The rate and parity on a chart: History's fxRate in the money blue and
     * fxParity (recorded since 0.7.35; since XR1 the basket with rent held to a
     * third) in its light step, and on the big one fxFair (recorded since XR1),
     * the fair level, dashed in the same light step. The big one -
     * with its legend, its ranges, the currency's crises on the episode lane
     * and the CURRENCY decisions as flags, the founding month's on its
     * first month (ChartModel.onAxis(), 0.7.38) - on The currency; a small
     * one, without them (handed neither: a small TimeChart draws no
     * episodes, and flags only when handed them), on the Overview.
     */
    TimeChart rateChart(HistorySave h, ChartModel window, boolean main, double width, double height) {
        List<TimeChart.Line> lines = new ArrayList<>(List.of(
                new TimeChart.Line("fxRate", "the rate", Palette.MONEY, 0, h.aligned("fxRate"),
                        v -> HistoryScreen.plotScale("rate", v), v -> perUsd(v), ""),
                new TimeChart.Line("fxParity", "parity (rent held to a third)", Palette.MONEY_LIGHT, 0, h.aligned("fxParity"),
                        v -> HistoryScreen.plotScale("rate", v), v -> perUsd(v), "")));
        // The fair level, dashed, on the big chart only: the Overview's small one keeps the rate and parity (XR1).
        if (main) {
            lines.add(new TimeChart.Line("fxFair", "the fair level", Palette.MONEY_LIGHT, 0, h.aligned("fxFair"),
                    v -> HistoryScreen.plotScale("rate", v), v -> perUsd(v), "").asDashed());
        }
        // Kept (KP6), as City History's and the Bank's are: made once in its scope, told its data and its size at every draw. The big
        // chart is on the tab's own window and legend (rateWindow, rateHidden); the small one on a window of its own, made with it
        // (`window` null), since a small TimeChart does not pan.
        TimeChart chart = Kept.of(TimeChart.class, () -> new TimeChart(window == null ? new ChartModel() : window,
                main ? rateHidden : Set.of(), main));
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
                "the rate follows parity's trend; the rest is the fundamentals and a swing",
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
            + "CURRENCY row and on the header's rate line alike. The rate follows parity's trend one for one, so a gap is the "
            + "fundamentals and the market's swing, not a pull still to come.",
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
        // Kept (KP6): each section a scope of its own.
        page.getChildren().add(Kept.scope("hero", this::monthHero));
        VBox holdings = Kept.scope("holdings", this::holdingsCard);
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
        FlowPane toggle = Kept.scope("toggle", () -> chipStrip(new String[] {"steps", "river"}, showRiver ? "river" : "steps",
                Palette.SIZE_LABEL, pick -> {
                    showRiver = "river".equals(pick);
                    showForeignMenu();
                }));
        toggle.setAlignment(Pos.CENTER_RIGHT);
        HBox right = Kept.row(Palette.GAP);
        right.setAlignment(Pos.CENTER_RIGHT);
        if (fx.isMonthCounted()) {
            right.getChildren().addAll(
                    chipInfo("current account " + signedD(fx.currentAccount()), Palette.TEXT_LABEL, CURRENT_INFO),
                    chipInfo("financial account " + signedD(fx.financialAccount()), Palette.TEXT_LABEL, FINANCIAL_INFO));
        }
        right.getChildren().add(toggle);
        VBox c = card(cardHead(Icons.COIN, Palette.MONEY, "THE MONTH ACROSS THE EDGE", MONTH_INFO, right));
        if (!fx.isMonthCounted()) {
            c.getChildren().add(Kept.scope("not counted", () -> line("Not counted yet: the balance of payments is struck when a month ends, and none "
                    + "has ended since the city was founded or loaded from a save before 0.7.46.", NOT_SAVED_INFO)));
            return c;
        }
        // Kept (KP6): the steps and the river are scopes of their own, so the toggle shifts no node of the page.
        if (showRiver) {
            River r = river();
            if (r.in().isEmpty() && r.out().isEmpty()) {
                c.getChildren().add(Kept.scope("nothing crossed", () -> line("Nothing crossed the city's edge this month.", NOTHING_CROSSED)));
                return c;
            }
            c.getChildren().add(Kept.scope("river", () -> bopRiver(r.in(), r.out(), 1180, 260, 160)));
        } else {
            Walk w = monthWalk();
            c.getChildren().add(Kept.scope("steps", () -> waterfall(w.steps(), this::signedD, 0, 250)));
            if (!w.nothing().isEmpty()) {
                c.getChildren().add(Kept.scope("nothing", () -> muted("nothing this month: " + String.join(" · ", w.nothing()))));
            }
        }
        double balance = fx.balance();
        c.getChildren().add(Kept.scope("balance", () -> line(balance >= 0
                        ? String.format("The city took in %s more than it paid out.", d(balance))
                        : String.format("The city paid out %s more than it took in.", d(-balance)),
                balance >= 0 ? SURPLUS_INFO : DEFICIT_INFO)));
        List<Node> hand = new ArrayList<>();
        if (fx.getBoughtThisMonth() > 0) {
            hand.add(Kept.scope("bought", () -> chipInfo("the treasury bought " + d(fx.getBoughtThisMonth()) + " of foreign money", Palette.MONEY, HAND_INFO)));
        }
        if (fx.getSoldThisMonth() > 0) {
            hand.add(Kept.scope("sold", () -> chipInfo("the treasury sold " + d(fx.getSoldThisMonth()) + " of foreign money", Palette.MONEY, HAND_INFO)));
        }
        if (Math.abs(fx.valuationChange()) > .005) {
            hand.add(Kept.scope("written off", () -> chipInfo("claims written off " + d(Math.abs(fx.valuationChange())), Palette.TEXT_LABEL,
                    String.format(VALUATION_INFO, d(Math.abs(fx.valuationChange()))))));
        }
        if (!hand.isEmpty()) c.getChildren().add(Kept.scope("hand", () -> chips(hand.toArray(new Node[0]))));
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
        if (sum <= 0) return Kept.column(0);

        int bands = Math.max(in.size(), out.size());
        double usable = H - GAP * Math.max(0, bands - 1);
        double scale = usable / sum;

        double leftBarX  = LABEL;
        double trunkX0   = W / 2 - TRUNK / 2;
        double trunkX1   = W / 2 + TRUNK / 2;
        double rightBarX = W - LABEL - BAR;

        javafx.scene.layout.Pane face = Kept.plain();
        face.setPrefSize(W, H);
        face.setMinSize(W, H);
        face.setMaxSize(W, H);

        Region trunk = Kept.region();
        trunk.setPrefSize(TRUNK, H);
        trunk.setMinSize(TRUNK, H);
        trunk.setStyle("-fx-background-color: " + Palette.PINNED + "; -fx-background-radius: 3;");
        trunk.setLayoutX(trunkX0);
        trunk.setLayoutY(0);
        face.getChildren().add(trunk);

        // A LABEL PER BAND, PUSHED APART: a band worth $347k beside one worth
        // $46M is two pixels tall, and its name would land on its neighbour's.
        // Kept (KP6): each band is a scope keyed by its side and its name, which the bands that come and go leave alone.
        double sourceY = 0, trunkInY = 0, labelFloor = 0;
        for (Flow f : in) {
            double h = Math.max(2, f.amount() * scale);
            double at = Math.max(labelFloor, sourceY + h / 2 - 14);
            double y0 = sourceY, y1 = trunkInY;
            Kept.scope("in:" + f.name(), () -> {
                face.getChildren().add(ribbon(leftBarX + BAR, y0, trunkX0, y1, h, f.colour()));
                face.getChildren().add(riverBar(f, leftBarX, y0, BAR, h));
                face.getChildren().add(bandLabel(f, 0, at, LABEL - 8, true));
            });
            labelFloor = at + 28;
            sourceY += h + GAP;
            trunkInY += h + GAP;
        }
        double useY = 0, trunkOutY = 0;
        labelFloor = 0;
        for (Flow f : out) {
            double h = Math.max(2, f.amount() * scale);
            double at = Math.max(labelFloor, useY + h / 2 - 14);
            double y0 = trunkOutY, y1 = useY;
            Kept.scope("out:" + f.name(), () -> {
                face.getChildren().add(ribbon(trunkX1, y0, rightBarX, y1, h, f.colour()));
                face.getChildren().add(riverBar(f, rightBarX, y1, BAR, h));
                face.getChildren().add(bandLabel(f, rightBarX + BAR + 8, at, LABEL - 8, false));
            });
            labelFloor = at + 28;
            useY += h + GAP;
            trunkOutY += h + GAP;
        }

        Label here = Kept.label("THE\nCITY");
        here.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED) + " -fx-text-alignment: center;");
        here.setLayoutX(trunkX0 + 6);
        here.setLayoutY(H / 2 - 14);
        face.getChildren().add(here);

        Label inHead = Kept.label("CAME IN   " + d(sum));
        inHead.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Region spread = Kept.region();
        HBox.setHgrow(spread, Priority.ALWAYS);
        Label outHead = Kept.label(d(sum) + "   WENT OUT");
        outHead.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        HBox heads = Kept.row(0, inHead, spread, outHead);
        heads.setMaxWidth(W);
        heads.setPrefWidth(W);

        VBox box = Kept.column(4, heads, face, noteLine("Band width is money; the two sides balance.", RIVER_INFO, W));
        box.setMaxWidth(W);
        return box;
    }

    /** The river's foot (the spec's section 4: one line, the rest in the (i), no colour named). */
    static final String RIVER_INFO = "Band width is money. The two sides balance because they must: what came in and what "
            + "went out are the same money seen twice. The outlined band is the difference - the month's surplus on the "
            + "right, where it went, or its deficit on the left, where it came from - and it is THE MONTH to the dollar.";

    /** One source or use bar, with its tooltip; the closing band outlined. */
    Region riverBar(Flow f, double x, double y, double w, double h) {
        Region bar = Kept.region();
        bar.setPrefSize(w, h);
        bar.setMinSize(w, h);
        boolean closing = Palette.TEXT_SPENT.equals(f.colour());
        bar.setStyle(closing
                ? "-fx-background-color: transparent; -fx-border-color: " + Palette.TEXT_LABEL + "; -fx-border-width: 1;"
                : "-fx-background-color: " + f.colour() + ";");
        bar.setLayoutX(x);
        bar.setLayoutY(y);
        // Kept (KP6): the tooltip the bar has is told this month's words (the default delay, as Tooltip.install gave it).
        Kept.tip(bar, f.name() + "\n" + dFull(f.amount()) + "\n" + f.note(), DEFAULT_TIP_MILLIS);
        return bar;
    }

    /** How long a tooltip waits before it shows when the code names no delay: JavaFX's own default, a second (what Tooltip.install gave the river's bars). */
    static final double DEFAULT_TIP_MILLIS = 1000;

    /**
     * One ribbon: a filled cubic band of constant height between two columns.
     * Both ends are the same height because the amount does not change on the
     * way across - the whole claim a Sankey makes.
     */
    javafx.scene.shape.Path ribbon(double x0, double y0, double x1, double y1, double h, String colour) {
        double mid = (x0 + x1) / 2;
        javafx.scene.shape.Path band = Kept.of(javafx.scene.shape.Path.class, javafx.scene.shape.Path::new);
        // Set, not added to (KP6): a kept ribbon is handed back with last month's path.
        band.getElements().setAll(
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
        Label name = Kept.label(f.name());
        name.setWrapText(true);
        name.setMaxWidth(width);
        name.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_BODY)
                + (rightAlign ? " -fx-text-alignment: right;" : ""));
        Label figure = Kept.label(d(f.amount()));
        figure.setStyle(Palette.figure(Palette.SIZE_CAPTION,
                Palette.TEXT_SPENT.equals(f.colour()) ? Palette.TEXT_LABEL : f.colour()));
        VBox box = Kept.column(-2, name, figure);
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
            c.getChildren().add(Kept.scope("nothing held", () -> line("Nothing is held abroad and the world holds nothing here.", null)));
            return c;
        }
        c.getChildren().add(Kept.scope("bars", () -> balanceBars("Held abroad " + d(abroad), top, null,
                "Held here by the world " + d(here), under, null, scale, 200, 18)));
        GridPane key = equalColumns(2, TILE_GAP);
        VBox left = Kept.column(4,
                keyLine(Palette.BUSINESS, "the businesses' paper", d(firms.totalLocalValue()) + " · " + usd(firms.totalUsd())),
                keyLine(Palette.PEOPLE, "the households' paper", d(savers.totalAbroadValue()) + " · " + usd(savers.totalAbroadUsd())),
                keyLine(Palette.MONEY, "the vault", d(fx.getReserves()) + " · " + usd(fx.getReservesUsd())));
        VBox right = Kept.column(4,
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
        // Kept (KP6): a scope of its own, so the swatch that takes the row's room is always a key line's.
        return Kept.scope("keyLine", () -> {
            HBox sw = swatch(colour, name);
            HBox.setHgrow(sw, Priority.ALWAYS);
            sw.setMaxWidth(Double.MAX_VALUE);
            HBox row = Kept.row(Palette.GAP, sw, figure(figures, Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD));
            row.setAlignment(Pos.CENTER_LEFT);
            return row;
        });
    }

    /** The holdings' (i) (the spec's A5). */
    static final String HOLDINGS_INFO = "The stocks the flows add up to - the rough shape of an international investment "
            + "position. Idle money buys the world's paper when the world pays more than the bank, and comes home when the "
            + "bank pays more or its owner needs it - the businesses' tills and the households' savings by the same rule. "
            + "The coupon is a line of the businesses' profit, taxed and paid out on, but kept where it is earned; it reaches the currency only when the money comes home. The city's "
            + "shares in foreign hands are at their price, the quote; the businesses' bonds at face.";

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
       band in legs (FR4: the gap between what the world pays and charges,
       split into the world's margin, the long haul and the city leg); then the
       world's prices in one line, and the market's price of every good
       under "details" - read off the markets, the railway's freight in them
       (B5: the old table worked them out and missed it).
       ===================================================================== */

    void goodsPage(VBox page, boolean fresh) {
        // Kept (KP6): each section a scope of its own.
        VBox hero = Kept.scope("hero", () -> goodsHero(fresh));
        targets.put(GOODS_CARD, hero);
        page.getChildren().add(hero);
        page.getChildren().add(row(Kept.scope("ten years", this::tenYearsCard), Kept.scope("since founding", this::sinceFoundingCard)));
        page.getChildren().add(Kept.scope("band", this::bandInLegsCard));
        page.getChildren().add(Kept.scope("prices", this::worldPrices));
    }

    /** The hero: the head with the toggle and its three figures, the bars, and the rest folded. */
    VBox goodsHero(boolean fresh) {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        Sectors.TradeByGood t = ui.game.getTradeByGood();
        FlowPane toggle = Kept.scope("toggle", () -> chipStrip(new String[] {"by good", "by business"},
                byBusiness ? "by business" : "by good", Palette.SIZE_LABEL, pick -> {
                    byBusiness = "by business".equals(pick);
                    showForeignMenu();
                }));
        toggle.setAlignment(Pos.CENTER_RIGHT);
        VBox c = card(cardHead(Icons.LORRY, Palette.BUSINESS, fx.isMonthCounted() ? "WHAT WE TRADE THIS MONTH"
                : "WHAT WE TRADE, THE MONTH THE CITY WAS SAVED IN", TRADE_INFO, toggle));
        c.getChildren().add(Kept.scope("sold and bought", () -> figure("sold " + d(t.sold()) + " · bought " + d(t.bought())
                + " · balance " + signedD(t.balance()), Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD)));
        if (!fx.isMonthCounted()) {
            c.getChildren().add(Kept.scope("saved books", () -> noteLine("From the businesses' saved books: the balance of payments "
                    + "counts again a month on.", NOT_SAVED_INFO + " The households' own imports - their cars, and from 0.7.49 to "
                    + "0.7.82 their petrol - are counted from the next month.", 1100)));
        }
        List<GoodRow> rows = byBusiness ? businessRows(t) : goodRows(t);
        if (rows.isEmpty()) {
            c.getChildren().add(Kept.scope("nothing crossed", () -> line("Nothing crossed the city's edge this month.", NOTHING_CROSSED)));
            return c;
        }
        boolean all = openLines.contains("goods:all");
        List<GoodRow> shown = all || rows.size() <= PAGE_GOODS + 1 ? rows : rows.subList(0, PAGE_GOODS);
        // Kept (KP6): the bars of a good and of a business are scopes of their own, so the toggle shifts no node of the page.
        MirrorRows bars = Kept.scope(byBusiness ? "businesses" : "goods", () -> mirror(shown, "BOUGHT ABROAD", "SOLD ABROAD", !byBusiness, false));
        // The bars grow on the month's first draw (fresh: UserInterface.countsUp() held); any other draw stands them at rest.
        if (fresh && fx.isMonthCounted()) bars.grow(GROW_MILLIS);
        else bars.stand();
        for (int i = 0; i < shown.size(); i++) {
            if (shown.get(i).good() != null) targets.put("#good:" + shown.get(i).good().name(), bars.row(i));
        }
        c.getChildren().add(bars);
        if (shown.size() < rows.size()) {
            int more = rows.size() - shown.size();
            c.getChildren().add(Kept.scope("more", () -> door("+" + more + " more", Palette.ACCENT, () -> {
                openLines.add("goods:all");
                showForeignMenu();
            })));
        } else if (all && rows.size() > PAGE_GOODS + 1) {
            c.getChildren().add(Kept.scope("fewer", () -> door("the first " + PAGE_GOODS + " only", Palette.ACCENT, () -> {
                openLines.remove("goods:all");
                showForeignMenu();
            })));
        }
        c.getChildren().add(Kept.scope("hint", () -> muted(byBusiness ? "a row a business: tap one for its books"
                : "a row a good, largest side first: tap one for who sold it, who bought it and what the world pays")));
        return c;
    }

    /** THE LAST TEN YEARS: History's exports and imports abroad on one small chart, and the last twelve months in figures. */
    VBox tenYearsCard() {
        HistorySave h = ui.game.getHistorySave();
        VBox c = card(cardHead(Icons.REPORTS, Palette.BUSINESS, "THE LAST TEN YEARS", "What the city sold abroad and "
                + "bought abroad each month, the last ten years. One month says whether a mill was staffed; ten years say "
                + "whether the city earns its living.", null));
        if (h.months() < 2) {
            c.getChildren().add(Kept.scope("short", () -> line("A line needs two months; come back next month.", null)));
            return c;
        }
        // Kept (KP6): made once in its scope (a small chart does not pan, so its window is its own), told its data and size at every draw.
        TimeChart chart = Kept.of(TimeChart.class, () -> new TimeChart(new ChartModel(), Set.of(), false));
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
        c.getChildren().add(Kept.scope("steps", () -> waterfall(w.steps(), this::signedD, 0, 170)));
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
                    "What the treasury and the central bank have bought into the vault less what the treasury, the central "
                            + "bank's defence and the land office have taken out, in the local money each cost or fetched."));
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
        if (!also.isEmpty()) c.getChildren().add(Kept.scope("also", () -> chips(also.toArray(new Node[0]))));
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
            // Where the city has the nations' moneys the dollar's prices are the US's own, on its path (WN4, D29).
            c.getChildren().add(Kept.scope("world", () -> line(String.format("%s %.3f× their founding level, %s %.2f%% a year.",
                    world.isOnUsPath() ? "US prices are" : "The world's prices are",
                    world.getPriceLevel(), world.getInflation() >= 0 ? "rising" : "falling",
                    Math.abs(world.getInflation()) * 100), WORLD_INFO)));
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
            // Kept (KP6): a row a good, its cells in a scope keyed by the good and by which of its two US$ cells say anything - a cell
            // that had words and has none keeps the Text node's bounds of the words it had, so it is made afresh.
            String usdPays = Double.isFinite(pays) ? marked(Currency.FOREIGN_SYMBOL, unitPrice(fx.toUsd(pays))) : "";
            String usdCharges = Double.isFinite(charges) ? marked(Currency.FOREIGN_SYMBOL, unitPrice(fx.toUsd(charges))) : "";
            int row = line;
            Kept.scope("price:" + g.name() + (usdPays.isEmpty() ? "/-" : "/p") + (usdCharges.isEmpty() ? "/-" : "/c"), () -> {
                grid.add(gridCell(g.label() + " · a " + g.unit(),
                        Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, row);
                grid.add(gridCell(Double.isFinite(pays) ? unitD(pays) : "—", Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, row);
                grid.add(gridCell(usdPays, Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, row);
                grid.add(gridCell(Double.isFinite(charges) ? unitD(charges) : "—", Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 3, row);
                grid.add(gridCell(usdCharges, Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 4, row);
                grid.add(gridCell(sold && bought ? "both ways" : sold ? "sold to it" : bought ? "bought from it" : "·",
                        Palette.TEXT_LABEL, Palette.SIZE_CAPTION, true), 5, row);
            });
            line++;
        }
        VBox box = Kept.column(6, grid);
        box.getChildren().add(statementNote("\"It pays\" is what the world pays the city for one, delivered there; \"it "
                + "charges\" what it charges to bring one in. Both are the markets' own prices, at today's rate and with "
                + "the railway's freight in them, so they are what a trade this month clears at; the US$ is the same price "
                + "in the world's money."));
        if (!fx.isMonthCounted()) box.getChildren().add(statementNote(FREIGHT_AFTER_LOAD));
        return box;
    }

    /* ----- THE BAND IN LEGS (FR4, 2026-10-20) -----

       The world's prices above carry a band: what the world pays for a good
       and what it charges to bring one in. Since FR2 and FR3 a tonne that
       crosses the edge moves in legs, each paid once and to somebody (the
       freight study's section 2), and the study asks Trade to say so (section
       6, last bullet). So this card splits the band into the world's margin,
       the long haul by mode - the world's lorries and ships, paid abroad, the
       city's railway and the quay's handling, paid at home - and the city
       leg from the gateway to the door, billed at home by Freight's trucks
       and sidings. The first bar is the month's trade in those pieces; under
       "details" each good's own gap a unit, on one scale, the share of what
       the world charges to bring one in. The legs paid abroad are hatched,
       as the Freight page hatches the world's lorries and ships. Every figure
       is TradeBand's (pure, held to the model's getters by TradeBandCheck);
       this only paints them. */

    /** The card's (i): the legs, who each is paid to, and the two readings. */
    static final String BAND_LEGS_INFO = "Between what the world pays for a good and what it charges to bring one in "
            + "there is a band, and the band is made of legs, each paid once. The world's own margin is the world's, and "
            + "nothing the city builds touches it. The long haul brings a tonne to the city's gate: by the world's lorries "
            + "or the world's ships, paid abroad, or by the city's railway, billed at home; and where it crosses a berth "
            + "the quay's handling is billed at home too. The city leg takes it from the gate to the door, billed at "
            + "home to Freight's trucks and sidings, and the buyer pays the leg of what it receives, the seller the leg "
            + "of what it ships.\n\nHatched is what leaves with the cargo: the world's margin, its lorries and its "
            + "ships. The bar is the month's trade in these pieces at today's prices; under details, each good's own gap a "
            + "unit as a share of what the world charges to bring one in.";

    /** The card's line while no good has a band: the world will not both buy and sell anything the city makes. */
    static final String NO_BAND = "No good the city trades has both a world buyer and a world seller yet, so there is no "
            + "band to show.";

    /** ...and while the books have no trade to weigh it by (a city just founded, or a month with nothing across the edge). */
    static final String NO_BAND_TRADE = "Nothing has crossed the edge in the month the books last struck, so there is no "
            + "trade to weigh the band by; the goods' own gaps are under details.";

    /** The colour of a leg's bar: the Freight page's mode colours where it has one, and a grey of its own for the world's margin. */
    static String legColour(TradeBand.Part part) {
        return switch (part) {
            case MARGIN -> Palette.TEXT_3;
            case WORLD_LORRY -> SectorScreen.modeColour(FreightView.Mode.WORLD_LORRY);
            case RAIL -> SectorScreen.modeColour(FreightView.Mode.RAIL);
            case SEA, PORT -> SectorScreen.modeColour(FreightView.Mode.SEA);
            case CITY_LEG -> SectorScreen.modeColour(FreightView.Mode.TRUCK);
        };
    }

    /** THE BAND IN LEGS: the month's trade in the legs as one bar with its key, and under details each good's own gap. */
    VBox bandInLegsCard() {
        TradeBand.Band band = TradeBand.of(ui.game);
        TradeBand.Whole whole = band.whole();
        VBox c = card(cardHead(Icons.LORRY, Palette.ORE, "THE BAND IN LEGS", BAND_LEGS_INFO, null));
        if (band.rows().isEmpty()) {
            c.getChildren().add(Kept.scope("no band", () -> muted(NO_BAND)));
            return c;
        }
        if (whole.crossed() > 0 && whole.total() > 0) {
            // Kept (KP6): the whole's picture, its key and its words are scopes of their own, since the band has none of them in a month with no trade.
            c.getChildren().add(Kept.scope("total", () -> figure(d(whole.total()) + " · " + BuildScreen.pct1(whole.shareOfTrade()) + " of the "
                    + d(whole.crossed()) + " that crossed", Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD)));
            List<String> tips = new ArrayList<>();
            for (TradeBand.Piece p : whole.pieces()) tips.add(wholeTip(whole, p));
            c.getChildren().add(Kept.scope("bar", () -> bandBar(whole.pieces(), tips, 0, true)));
            c.getChildren().add(Kept.scope("key", () -> bandKey(whole)));
            c.getChildren().add(Kept.scope("what", () -> muted("What the world's margin, the long haul and the city leg came to on the month's trade, at "
                    + "today's prices. Hatched: paid abroad, with the cargo.")));
        } else {
            c.getChildren().add(Kept.scope("no trade", () -> muted(NO_BAND_TRADE)));
        }
        if (!ui.game.getForeignAccounts().isMonthCounted()) c.getChildren().add(Kept.scope("after load", () -> muted(FREIGHT_AFTER_LOAD)));
        c.getChildren().add(fold("goods:band", "each good's gap in legs", () -> bandGoods(band)));
        return c;
    }

    /** A band's bar (KP6): a picture on a kept page is told its pieces by set(); anywhere else this is a new bar, as it was. */
    static BandBar bandBar(List<TradeBand.Piece> pieces, List<String> tips, double scale, boolean named) {
        BandBar bar = Kept.of(BandBar.class, BandBar::new);
        bar.set(pieces, tips, scale, named);
        return bar;
    }

    /** One leg's tooltip on the whole's bar: its name, its money, its share of the gap, and who is paid. */
    String wholeTip(TradeBand.Whole whole, TradeBand.Piece p) {
        return p.part().label() + " · " + d(p.amount()) + " · " + BuildScreen.pct1(whole.shareOfGap(p.part())) + " of the band"
                + "\n" + p.part().leg() + ": " + p.part().paidTo() + (p.part().abroad() ? " · paid abroad" : "");
    }

    /** The bar's key: a swatch a leg - hatched where it is paid abroad - with its name, its money and its share of the band. */
    FlowPane bandKey(TradeBand.Whole whole) {
        FlowPane key = Kept.flow(16, 6);
        for (TradeBand.Piece p : whole.pieces()) {
            // Kept (KP6): an item a scope keyed by its leg, which the legs that come and go leave alone.
            key.getChildren().add(Kept.scope("leg:" + p.part(), () -> {
                Label name = Kept.label(p.part().label());
                name.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
                HBox item = Kept.row(6, legSwatch(p.part()), name, figure(d(p.amount()), Palette.SIZE_LABEL, Palette.TEXT_HEAD),
                        muted(BuildScreen.pct1(whole.shareOfGap(p.part()))));
                item.setAlignment(Pos.CENTER_LEFT);
                Kept.tip(item, p.part().leg() + ": " + p.part().paidTo(), 200);
                return item;
            }));
        }
        return key;
    }

    /** A leg's key swatch: its colour, hatched over where the money is paid abroad (the Freight page's key). */
    static javafx.scene.layout.StackPane legSwatch(TradeBand.Part part) {
        javafx.scene.layout.StackPane swatch = Kept.stack();
        javafx.scene.shape.Rectangle fill = Kept.of(javafx.scene.shape.Rectangle.class, javafx.scene.shape.Rectangle::new);
        fill.setWidth(14);
        fill.setHeight(10);
        fill.setFill(javafx.scene.paint.Color.web(legColour(part)));
        swatch.getChildren().add(fill);
        if (part.abroad()) {
            javafx.scene.shape.Rectangle hatch = Kept.of(javafx.scene.shape.Rectangle.class, javafx.scene.shape.Rectangle::new);
            hatch.setWidth(14);
            hatch.setHeight(10);
            hatch.setFill(SectorScreen.hatch(Palette.STAGE, .6));
            swatch.getChildren().add(hatch);
        }
        return swatch;
    }

    /** Under details: a row a good - those that crossed first - its gap in legs as a share of what the world charges to bring one in, all on one scale. */
    Node bandGoods(TradeBand.Band band) {
        VBox box = Kept.column(6);
        boolean all = openLines.contains("band:all");
        List<TradeBand.Row> rows = band.rows();
        List<TradeBand.Row> shown = all || rows.size() <= PAGE_GOODS + 1 ? rows : rows.subList(0, PAGE_GOODS);
        double scale = 0;
        for (TradeBand.Row r : shown) scale = Math.max(scale, r.shareToTheDoor());
        final double barScale = scale;
        for (TradeBand.Row r : shown) {
            Good g = r.good();
            // Kept (KP6): a row a good, a scope keyed by the good, which the goods that come and go (the first twelve, or all) leave alone.
            box.getChildren().add(Kept.scope("good:" + g.name(), () -> {
                Label name = words(g.label() + " · a " + g.unit(), Palette.SIZE_CAPTION, Palette.TEXT_BODY);
                name.setMinWidth(170);
                name.setPrefWidth(170);
                name.setMaxWidth(170);
                List<TradeBand.Piece> shares = new ArrayList<>();
                List<String> tips = new ArrayList<>();
                for (TradeBand.Piece p : r.pieces()) {
                    shares.add(new TradeBand.Piece(p.part(), r.share(p.part())));
                    tips.add(g.label() + " · " + p.part().label() + " " + unitD(p.amount()) + " a " + g.unit() + " · "
                            + BuildScreen.pct1(r.share(p.part())) + " of the " + unitD(r.importPrice()) + " it costs to bring one in"
                            + "\n" + p.part().leg() + ": " + p.part().paidTo() + (p.part().abroad() ? " · paid abroad" : ""));
                }
                BandBar bar = bandBar(shares, tips, barScale, false);
                HBox.setHgrow(bar, Priority.ALWAYS);
                Label at = figure(BuildScreen.pct1(r.shareToTheDoor()) + " · " + unitD(r.toTheDoor()), Palette.SIZE_CAPTION,
                        r.crossed() > 0 ? Palette.TEXT_HEAD : Palette.TEXT_MUTED);
                at.setMinWidth(110);
                at.setAlignment(Pos.CENTER_RIGHT);
                HBox line = Kept.row(Palette.GAP, name, bar, at);
                line.setAlignment(Pos.CENTER_LEFT);
                return line;
            }));
        }
        if (shown.size() < rows.size()) {
            box.getChildren().add(Kept.scope("more", () -> door("+" + (rows.size() - shown.size()) + " more", Palette.ACCENT, () -> {
                openLines.add("band:all");
                showForeignMenu();
            })));
        } else if (all && rows.size() > PAGE_GOODS + 1) {
            box.getChildren().add(Kept.scope("fewer", () -> door("the first " + PAGE_GOODS + " only", Palette.ACCENT, () -> {
                openLines.remove("band:all");
                showForeignMenu();
            })));
        }
        box.getChildren().add(statementNote("A row is one good's gap to the door a unit, as a share of what the world charges to "
                + "bring one in and in the city's money: the goods that crossed in the month first, then the widest. It is "
                + "the prices and shares in force, not the month's bill."));
        return box;
    }

    /**
     * A band's bar: its pieces laid end to end to a scale (0 or less: their
     * sum), each in its leg's colour and hatched where the money is paid
     * abroad, each with a tooltip and, when asked, its name inside where it
     * fits - the Freight page's LegBar, for money.
     *
     * <p>A picture (KP6): made through Kept (bandBar()), told its pieces by set(). Its parts are its own, made with new (a
     * picture owns its children); set() keeps them while the bar has the pieces' legs it had and makes them afresh when
     * they change, so the common month - the figures moved, the legs the same - makes no node, and asks for a layout only
     * when a figure the layout reads moved.
     */
    static final class BandBar extends javafx.scene.layout.Pane {
        /** The bar's height, in pixels: 26, the Freight page's LegBar's. */
        static final double BAND = 26;
        private List<TradeBand.Piece> pieces = List.of();
        private double scale;
        private boolean named;
        /** What the parts were made for at the last set() (see shapeOf()), and what the layout read (see layoutKey()): null before the first. */
        private String shape, laidOut;
        private final Region track = new Region();
        private final List<javafx.scene.shape.Rectangle> blocks = new ArrayList<>(), stripes = new ArrayList<>();
        private final List<Label> labels = new ArrayList<>();

        /** The track and the bar's own size, made once in its life; the pieces come with set(). */
        BandBar() {
            track.setStyle("-fx-background-color: " + Palette.CONTROL + ";");
            getChildren().add(track);
            setMinHeight(BAND);
            setPrefHeight(BAND);
            setMaxHeight(BAND);
            setMinWidth(0);
            setPrefWidth(200);
            setMaxWidth(Double.MAX_VALUE);
        }

        /** What the bar draws now: the pieces and each one's tooltip, the scale (0 or less: their sum), and whether the legs are named inside. */
        void set(List<TradeBand.Piece> pieces, List<String> tips, double scale, boolean named) {
            this.pieces = pieces;
            this.named = named;
            double sum = 0;
            for (TradeBand.Piece p : pieces) sum += Math.max(0, p.amount());
            this.scale = scale > 0 ? scale : sum;
            String now = shapeOf();
            boolean built = !now.equals(shape);
            if (built) {
                shape = now;
                build();
            }
            for (int i = 0; i < pieces.size(); i++) {
                javafx.scene.shape.Rectangle block = blocks.get(i);
                block.setFill(javafx.scene.paint.Color.web(legColour(pieces.get(i).part())));
                tipFor(block, tips.get(i), 200);
            }
            String key = layoutKey();
            if (built || !key.equals(laidOut)) {
                laidOut = key;
                requestLayout();
            }
        }

        /** What the parts depend on: whether the legs are named inside, and which leg each piece is. */
        private String shapeOf() {
            StringBuilder s = new StringBuilder(named ? "named" : "plain");
            for (TradeBand.Piece p : pieces) s.append('|').append(p.part());
            return s.toString();
        }

        /** Every figure the layout reads, in one string: laid out again only when it differs. */
        private String layoutKey() {
            StringBuilder s = new StringBuilder().append(scale);
            for (TradeBand.Piece p : pieces) s.append('|').append(p.amount());
            return s.toString();
        }

        /** The parts made afresh, a block, a hatched stripe and (when named) a label a piece. */
        private void build() {
            blocks.clear();
            stripes.clear();
            labels.clear();
            List<javafx.scene.Node> all = new ArrayList<>();
            all.add(track);
            for (TradeBand.Piece p : pieces) {
                javafx.scene.shape.Rectangle block = new javafx.scene.shape.Rectangle();
                blocks.add(block);
                all.add(block);
                javafx.scene.shape.Rectangle stripe = new javafx.scene.shape.Rectangle();
                stripe.setMouseTransparent(true);
                stripe.setVisible(p.part().abroad());
                stripe.setFill(SectorScreen.hatch(Palette.STAGE, .6));
                stripes.add(stripe);
                all.add(stripe);
                Label label = new Label(p.part().label());
                label.setMouseTransparent(true);
                label.setVisible(false);
                label.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_HEAD) + " -fx-background-color: "
                        + tint(Palette.STAGE, .55) + "; -fx-background-radius: 2; -fx-padding: 1 4 1 4;");
                if (named) {
                    labels.add(label);
                    all.add(label);
                } else {
                    labels.add(null);
                }
            }
            getChildren().setAll(all);
        }

        @Override protected void layoutChildren() {
            double w = getWidth(), x = 0;
            track.resizeRelocate(0, 0, w, BAND);
            int n = pieces.size();
            for (int i = 0; i < n; i++) {
                double wide = scale > 0 ? w * Math.max(0, pieces.get(i).amount()) / scale : 0;
                double shown = wide > 0 ? Math.max(1, Math.min(w - x, wide - (i < n - 1 ? 1 : 0))) : 0;
                javafx.scene.shape.Rectangle block = blocks.get(i), stripe = stripes.get(i);
                block.setVisible(shown > 0);
                stripe.setVisible(shown > 0 && pieces.get(i).part().abroad());
                for (javafx.scene.shape.Rectangle s : List.of(block, stripe)) {
                    s.setX(x);
                    s.setY(0);
                    s.setWidth(shown);
                    s.setHeight(BAND);
                }
                Label label = labels.get(i);
                if (label != null) {
                    label.applyCss();
                    double lw = label.prefWidth(-1), lh = label.prefHeight(-1);
                    label.setVisible(shown > lw + 8);
                    label.resizeRelocate(x + 4, Math.max(0, (BAND - lh) / 2), lw, lh);
                }
                x += wide;
            }
        }
    }

    /* =====================================================================
       THE CURRENCY (0.7.35; "The rate" and "What is moving it, next month"
       until then; redrawn for the float, XR1, 2026-10-21; board 1 of the
       exchange-rate study)

       What does a US dollar cost, and what put it there this month? The
       rate at 28 px with its unit (B4: it had none), against parity - the
       city's basket with rent held to a third - on a gauge either side of it
       in the one parity rule's bands (D8), and against the fair level, where
       the fundamentals alone put it, on a gauge of the same scale with no
       bands at all (B19: the market's swing is not a verdict). Beside it the
       rate's history on City History's own chart - parity and the fair level
       beside it, the currency's crises on the episode lane, the city's
       decisions on the currency as flags. Then WHAT MOVED IT THIS MONTH: the
       month's move by force as diverging bars - the D$ stronger to the
       left, weaker to the right, in the money blues and never a verdict's
       colours - parity's trend, the real rate, the balance, the vault's
       cover, interventions and the market, which add to the month's move
       (ForeignAccounts.getMoveTrend() and the rest, struck by the model; the
       screen computes none of them). Then WHERE THE FUNDAMENTALS HOLD IT,
       the rate built up step by step from parity to the fair level and from
       the fair level to the rate; what the rate is doing to the dollar debt
       and to the vault; and THE RULE, folded at the page's foot.

       WHAT IT REPLACED. "What is moving it, next month" forecast next
       month's push and the basket's pull back to parity, and the float has
       neither: it follows parity's trend one for one, with no lag, so what
       the screen can truthfully say is what moved the rate, force by force.
       Every sentence on the bars and in the steps is a pure String method of
       this class (trendSentence() and the rest, fairSteps()) built from the
       model's getters, so a probe can read them. Two figures of board 1 are
       not here: D$ per 2015 US$ and the real effective rate wait for WN6,
       and today's rule's path on the chart went with the rule.
       ===================================================================== */

    void currencyPage(VBox page) {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        HistorySave h = ui.game.getHistorySave();
        // Kept (KP6): each section a scope of its own.
        VBox rate = Kept.scope("rate", this::rateCard);
        rate.setMinWidth(360);
        rate.setPrefWidth(400);
        rate.setMaxWidth(400);
        HBox hero = Kept.row(TILE_GAP, rate);
        hero.setAlignment(Pos.TOP_LEFT);
        if (h.months() >= 2) {
            hero.getChildren().add(Kept.scope("over time", () -> overTime(h, fx)));
        }
        page.getChildren().add(hero);
        VBox forces = Kept.scope("forces", this::forcesCard);
        targets.put(FORCES, forces);
        page.getChildren().add(forces);
        page.getChildren().add(Kept.scope("fundamentals", this::fundamentalsCard));
        page.getChildren().add(Kept.scope("doing head", () -> sectionHead("WHAT THE RATE IS DOING TO THE CITY", null, null)));
        VBox debt = Kept.scope("debt", this::dollarDebtCard);
        targets.put(DEBT, debt);
        page.getChildren().add(row(debt, Kept.scope("vault", this::vaultCard)));
        page.getChildren().add(fold("currency:rule", "the rule the rate follows",
                () -> words(RULE, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL)));
    }

    /** The rate over time, beside THE RATE: the big chart on the tab's own window, with the notes on what History recorded since which build. */
    private VBox overTime(HistorySave h, ForeignAccounts fx) {
        TimeChart chart = rateChart(h, rateWindow, true, 794, 230);
        Label title = Kept.label("THE RATE OVER TIME");
        title.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        title.setMinWidth(Region.USE_PREF_SIZE);
        // Set where it differs: the chart is kept, and its lead already holds this month's title.
        Kept.set(chart.lead().getChildren(), List.of(title));
        VBox right = Kept.column(6, chart);
        int parity = h.monthsRecorded("fxParity");
        if (parity < h.months()) {
            right.getChildren().add(Kept.scope("parity note", () -> noteLine(parity == 0
                            ? "Parity is recorded from 0.7.35 on: none yet in this city's history - today's is "
                              + sym() + fxRate(ui.game.getForeignAccounts().getParity()) + "."
                            : "Parity is recorded from 0.7.35 on: " + parity + " month" + (parity == 1 ? "" : "s") + " so far.",
                    "Parity is where a basket costs the same here and abroad. History kept the rate from the "
                            + "founding and parity only since 0.7.35, so an older city's parity line starts the "
                            + "month it was first played in this build.", 790)));
        }
        int fair = h.monthsRecorded("fxFair");
        if (fair < h.months()) {
            right.getChildren().add(Kept.scope("fair note", () -> noteLine(fair == 0
                            ? "The fair level is recorded from 0.8.19 on: none yet in this city's history - today's is "
                              + sym() + fxRate(fx.getFairLevel()) + "."
                            : "The fair level is recorded from 0.8.19 on: " + fair + " month" + (fair == 1 ? "" : "s") + " so far.",
                    "The fair level is where the fundamentals alone put the currency: parity's trend, the real rate, the "
                            + "vault's cover and the balance, with nothing for the market's swing or any dealing. History "
                            + "kept the rate from the founding and the fair level only from the float on, so an older city's "
                            + "fair line starts the month it was first played in this build.", 790)));
        }
        return right;
    }

    /**
     * THE RATE: what a US dollar costs at 28 px with its unit; one of ours in
     * theirs; this month's move; the key to reading it (a rising figure is a
     * cheaper currency); then two gauges of one scale either side of their
     * centre - AGAINST PARITY (rent held to a third), in the one rule's bands
     * (amber past PARITY_WATCH, red past PARITY_FAR), and AGAINST THE FAIR
     * LEVEL, with no bands and one tone: the market's swing is not a verdict
     * (B19) - or a "held fixed" chip when the rate is pinned.
     */
    VBox rateCard() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        Label rate = figure(sym() + fxRate(fx.getRate()), 28, Palette.TEXT_HEAD);
        Label per = words("per " + Currency.FOREIGN_SYMBOL, 13, Palette.TEXT_LABEL);
        HBox big = Kept.row(6, rate, per);
        big.setAlignment(Pos.BASELINE_LEFT);
        VBox c = card(cardHead(Icons.EXCHANGE, Palette.MONEY, "THE RATE", RATE_INFO, null), big, muted(oneOfOurs()));
        // Kept (KP6): what is drawn only sometimes is a scope of its own, so it shifts no node after it.
        String moved = rateMove();
        if (moved != null) c.getChildren().add(Kept.scope("moved", () -> muted(moved)));
        // AN2: the D$ the traded goods are struck at (AN1), and how far it trails the rate - a scope of its own, drawn once something is struck.
        double struck = ui.game.getEconomyManager().getStruckRate(), gap = ui.game.struckFromRate();
        if (struck > 0) {
            Kept.scope("struck", () -> {
                c.getChildren().add(cardLine("The goods are struck at", sym() + fxRate(struck), null, STRUCK_INFO));
                c.getChildren().add(muted(Math.abs(gap) < .0005 ? "Level with the rate: the goods have caught up."
                        : farFrom(gap, "the rate") + ", closing " + String.format("1/%.0f", 1 / EconomyManager.PASS_THROUGH) + " of the gap a month"));
            });
        }
        addRegimeLine(c);
        c.getChildren().add(Kept.scope("rising", () -> muted("A rising figure is a cheaper " + sym() + ": " + Currency.FOREIGN_SYMBOL + "1 costs more of it.")));
        // D29 (WN6): the other US dollar, the real 2015 one the nations' output is counted in, and the D$'s real effective rate against the partners'.
        double real = ui.game.dollarsPer2015Usd(), effective = ui.game.realEffectiveRate();
        if (Double.isFinite(real)) {
            c.getChildren().add(Kept.scope("real", () -> cardLine("A real 2015 " + Currency.FOREIGN_SYMBOL + " costs", sym() + fxRate(real), null, REAL_USD_INFO)));
        }
        if (Double.isFinite(effective)) {
            c.getChildren().add(Kept.scope("effective", () -> cardLine("The real effective rate", String.format("%.1f", effective) + " (100 at founding)", null, REAL_EFFECTIVE_INFO)));
            // ...and (WN5b) the partners it weighs, from the getter the rate is made from.
            c.getChildren().add(Kept.scope("weighs", () -> muted(weighsWords(ui.game.realEffectivePartners(), ui.game.getNations(), ui.game.getWorldMoneys() != null))));
        }
        if (fx.isPinned()) {
            c.getChildren().add(Kept.scope("held fixed", () -> chips(chip("held fixed", Palette.TEXT_LABEL))));
            return c;
        }
        double dev = fx.deviationFromParity();
        int level = ForeignAccounts.parityLevel(dev);
        double s = PARITY_SCALE, w = ForeignAccounts.PARITY_WATCH, f = ForeignAccounts.PARITY_FAR;
        double[] edges = {(s - f) / (2 * s), (s - w) / (2 * s), (s + w) / (2 * s), (s + f) / (2 * s), 1};
        String[] tones = {Palette.BAD, Palette.WARN, Palette.TEXT_SPENT, Palette.WARN, Palette.BAD};
        // ...the two gauges, each a scope of its own: their lines say nothing ("") where the card line elsewhere says a figure.
        Kept.scope("against parity", () -> {
            c.getChildren().add(cardLine("Against parity (rent held to a third)", "", null, parityLineInfo(fx)));
            c.getChildren().add(bandTrack(gaugeAt(dev), edges, tones,
                    List.of(new Tick(.5, Palette.TEXT_LABEL, 1, "parity", "parity " + perUsd(fx.getParity()))), 0, 12, false));
            c.getChildren().add(gaugeEnds());
            c.getChildren().add(chips(chip(parityWords(fx), level == 0 ? Palette.TEXT_MUTED : parityTone(level))));
        });
        double off = fx.deviationFromFair();
        Kept.scope("against fair", () -> {
            c.getChildren().add(cardLine("Against the fair level", "", null, FAIR_INFO));
            c.getChildren().add(bandTrack(gaugeAt(off), new double[] {1}, new String[] {Palette.TEXT_SPENT},
                    List.of(new Tick(.5, Palette.TEXT_LABEL, 1, "fair level", "the fair level " + perUsd(fx.getFairLevel()))),
                    0, 12, false));
            c.getChildren().add(gaugeEnds());
            c.getChildren().add(chips(chip(farFrom(off, "the fair level"), Palette.TEXT_MUTED)));
        });
        return c;
    }

    /** Where a deviation sits on the two gauges, 0 to 1 across: the centre at nothing, PARITY_SCALE either side at the ends. */
    static double gaugeAt(double deviation) {
        double dev = Double.isFinite(deviation) ? deviation : 0;
        return .5 + Math.max(-PARITY_SCALE, Math.min(PARITY_SCALE, dev)) / (2 * PARITY_SCALE);
    }

    /** The two gauges' ends, named in the scale they share: "‹ 75% stronger" and "75% weaker ›". */
    static HBox gaugeEnds() {
        String scale = String.format("%.0f%%", PARITY_SCALE * 100);
        return Kept.scope("gaugeEnds", () -> {
            Label stronger = muted("‹ " + scale + " stronger"), weaker = muted(scale + " weaker ›");
            Region gap = Kept.region();
            HBox.setHgrow(gap, Priority.ALWAYS);
            return Kept.row(0, stronger, gap, weaker);
        });
    }

    /** A number past a thousand written short, so a currency at its guard fits a card: "1,234.5", "12,346", "1.2M", "1.0B". */
    static String compact(double v) {
        if (!Double.isFinite(v)) return "—";
        if (v >= 1e9) return String.format("%.1fB", v / 1e9);
        if (v >= 1e6) return String.format("%.1fM", v / 1e6);
        if (v >= 1e4) return String.format("%,.0f", v);
        return String.format("%,.1f", v);
    }

    /** How far a rate sits from a level, in words: "8.3% weaker than the fair level", "at parity", and past a hundredfold "1.2M times parity" - the rate at its guard fits the card. */
    static String farFrom(double deviation, String anchor) {
        if (!Double.isFinite(deviation) || Math.abs(deviation) < .0005) return "at " + anchor;
        if (deviation >= 100) return compact(1 + deviation) + " times " + anchor;
        return share1(Math.abs(deviation)) + (deviation > 0 ? " weaker" : " stronger") + " than " + anchor;
    }

    /** The parity gauge's (i): which way the rate sits and what that does, then what parity is and what follows it (the spec's Q3). */
    static String parityLineInfo(ForeignAccounts fx) {
        double drift = fx.deviationFromParity();
        String where = fx.isPinned()
                ? "The rate is pinned. A fixed rate is a promise the reserves have to keep, and the market will find out "
                  + "whether they can."
                : drift > .005
                ? "A rate ABOVE parity means it takes more of ours to buy one of theirs - the currency is weaker than the "
                  + "same basket of goods says it should be. Imports cost more; what the city sells abroad earns more at home."
                : drift < -.005
                ? "A rate BELOW parity means the currency is stronger than the basket says. Imports are cheap and the "
                  + "exporters are being squeezed."
                : "The rate is within half a per cent of what the same basket at home and abroad says it should be: the real "
                  + "rate, the balance, the vault and the market's swing net out to nothing just now.";
        return where + " Parity here is relative purchasing power: the city's own basket - with rent held to a third of it, "
                + "because rent is what no border arbitrages - priced against the world's. The currency follows parity's "
                + "trend one for one, with no lag; the real rate, the balance, the vault's cover and the market's own swing "
                + "put it above or below. That distance is the fundamentals and a swing, not a pull still to come.";
    }

    /** The fair-level gauge's (i): what the level is and why its gauge has no verdict. */
    static final String FAIR_INFO = String.format("The fair level is where the fundamentals alone put the currency: parity's "
            + "trend, the real rate gap, the vault's cover and the balance, with nothing for the market's own swing or any "
            + "dealing in dollars. The rate swings around it, and a swing fades by half in about %.1f years. The gauge has "
            + "no bands and one tone because a swing is not a verdict: a currency well past its fair level is not good or "
            + "bad news by colour.", ForeignAccounts.MARKET_HALF_LIFE / 12);

    /* ------------------------------ what moved it ------------------------------ */

    /**
     * WHAT MOVED IT THIS MONTH (XR1; board 1): the month's move by force as the
     * model struck it - parity's trend, the real rate, the balance, the vault's
     * cover, interventions and the market (ForeignAccounts.getMoveTrend() and
     * the rest, log points, which add) - as diverging bars with their
     * sentences, the total under them, and a chip saying how much of the
     * market and the fundamentals the city's trade lets through. One line,
     * and no bars, when the rate is pinned. Since XR2 a seventh, THE HOLD,
     * joins them under a band or a peg (moveForces()).
     */
    VBox forcesCard() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        VBox c = card(cardHead(Icons.EXCHANGE, Palette.MONEY, "WHAT MOVED IT THIS MONTH", FORCES_INFO,
                quiet("percentage points of the rate")));
        if (fx.isPinned()) {
            c.getChildren().add(Kept.scope("pinned", () -> line("The rate is pinned: nothing moved it.", "Nothing moved it, because the treasury has "
                    + "promised it will not move. While the pin holds there is no market, no dealing and no trend to feel.")));
            return c;
        }
        List<Force> rows = moveForces();
        double scale = .005;
        for (Force f : rows) if (Double.isFinite(f.value())) scale = Math.max(scale, Math.abs(f.value()));
        final double barScale = scale;
        c.getChildren().add(Kept.scope("bars", () -> divergingBars(rows, barScale * 1.05, 420, 96, Palette.MONEY_LIGHT, Palette.MONEY,
                "‹ the " + sym() + " stronger", "the " + sym() + " weaker ›", TradeScreen::pointsOfRate)));
        c.getChildren().add(Kept.scope("gate", () -> chips(chipInfo(gateWords(fx), Palette.TEXT_LABEL, GATE_INFO))));
        return c;
    }

    /**
     * The seven forces and their sum as the bars' rows (pure): each its sentence
     * under its name, the (i) behind what the force is. The seventh, THE HOLD
     * (XR2), is the vault holding a peg or a band's edge, and is a row only
     * under a band or a peg or in a month it moved the rate (showsHold()): under
     * Lean and Float the card is the six and the sum, exactly as it was.
     */
    List<Force> moveForces() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        List<Force> rows = new ArrayList<>(List.of(
                new Force("Parity's trend", fx.getMoveTrend(), false, false, TREND_INFO, trendSentence()),
                new Force("The real rate", fx.getMoveGap(), false, false, REAL_RATE_INFO, realRateSentence()),
                new Force("The balance", fx.getMoveBalance(), false, false, BALANCE_INFO, balanceSentence()),
                new Force("The vault's cover", fx.getMoveCover(), false, false, COVER_FORCE_INFO, coverSentence()),
                new Force("Interventions", fx.getMoveIntervention(), false, false, INTERVENTION_INFO, interventionSentence())));
        // The world (WN4): the dollar's cycle, money running to safety and the prices of what the city sells abroad, a row in a city that has the world's moneys.
        if (showsWorld(fx)) rows.add(new Force("The world", fx.getMoveWorld(), false, false, WORLD_FORCE_INFO, worldSentence()));
        rows.add(new Force("The market", fx.getMoveMarket(), false, false, MARKET_INFO, marketSentence()));
        if (showsHold(fx)) rows.add(new Force("The hold", fx.getMoveHeld(), false, false, HOLD_INFO, holdSentence()));
        rows.add(new Force("The month's move", fx.getLastMove(), true, false, null, totalSentence()));
        return rows;
    }

    /** Whether THE WORLD is a row: the city has the world's moneys, or the world moved the rate last month (a pure read of the foreign accounts and the game). */
    boolean showsWorld(ForeignAccounts fx) {
        return ui.game.getWorldMoneys() != null || fx.getMoveWorld() != 0 || fx.getWorldLevel() != 0;
    }

    /** Whether THE HOLD is a row: the regime is a band or a peg, or the hold moved the rate last month (a peg that broke, a regime since left). */
    static boolean showsHold(ForeignAccounts fx) {
        return fx.getRegime() == ForeignAccounts.Regime.BAND || fx.getRegime() == ForeignAccounts.Regime.PEG
                || fx.getMoveHeld() != 0;
    }

    /** A log move as percentage points of the rate, signed with a true minus: "+0.440%", "−0.120%", and whole past a hundred points so a currency at its guard fits the figure column. */
    static String pointsOfRate(double move) {
        if (!Double.isFinite(move)) return "—";
        double shown = move * 100;
        if (Math.abs(shown) >= 100) return (shown > 0 ? "+" : "−") + String.format("%,.0f%%", Math.abs(shown));
        return signedPct3(move);
    }

    /** How much of the market and the fundamentals the city's trade lets reach the rate, in words (pure). */
    static String gateWords(ForeignAccounts fx) {
        double g = fx.gate();
        if (!(g > 0)) return "the city trades next to nothing: the market and the fundamentals do not reach the rate";
        String trades = String.format("the city trades %.0f%% of its output", fx.getOpenness() * 100);
        return g >= .9995 ? trades + ": the market and the fundamentals reach the rate in full"
                : trades + ": the market and the fundamentals reach the rate in part, " + String.format("%.0f%%", g * 100);
    }

    /** What a log shift does to the rate, as a sentence's verb: "holds it about 2.4% dearer", "holds the D$ about 10.5% cheaper", "leaves it where it stands". Pure. */
    static String heldWords(double log, String subject) {
        double change = Math.expm1(log);
        if (!(Math.abs(change) >= .0005)) return "leaves " + subject + " where it stands";
        return "holds " + subject + " about " + share1(Math.abs(change)) + (change > 0 ? " cheaper" : " dearer");
    }

    /** A real rate gap in the sentence's words: "1.0 point over the world's real rate", "level with the world's real rate". Pure. */
    static String overUnder(double gap) {
        double points = Math.abs(gap) * 100;
        if (!(points >= .05)) return "level with the world's real rate";
        String shown = String.format("%.1f", points);
        return shown + ("1.0".equals(shown) ? " point " : " points ") + (gap > 0 ? "over" : "under") + " the world's real rate";
    }

    /** The overall balance in words: "a deficit of 0.2% of GDP", "a surplus of 1.4% of GDP", "a balance within 0.05% of GDP", or that no year of output is known yet. Pure. */
    static String balancePhrase(ForeignAccounts fx) {
        double gdp = fx.getGdpTrailing();
        if (!(gdp > 0)) return "no year of output yet to weigh it against";
        double share = (fx.monthlyCurrentAccount() + fx.monthlyFinancialAccount()) / gdp;
        if (!(Math.abs(share) >= .0005)) return "a balance within 0.05% of GDP";
        return (share < 0 ? "a deficit" : "a surplus") + " of " + share1(Math.abs(share)) + " of GDP";
    }

    /** The vault in the sentence's words: "an empty vault", "a vault holding 6.2 months of imports", "a vault with no import bill to cover". Pure. */
    static String coverPhrase(ForeignAccounts fx) {
        if (!(fx.getReservesUsd() > 0)) return "an empty vault";
        if (!(fx.monthlyImports() > 0)) return "a vault with no import bill to cover";
        return "a vault holding " + coverMonths(fx.importCover()) + " of imports";
    }

    /** PARITY'S TREND (pure): which way the city's prices ran against the world's this month, and that the rate follows them one for one. */
    String trendSentence() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        String d = sym();
        double move = fx.getMoveTrend();
        String ran = Math.abs(move) < 5e-6 ? "kept pace with the world's, so the " + d + " buys the same"
                : move > 0 ? "rose faster than the world's, so the " + d + " buys less"
                : "rose slower than the world's, so the " + d + " buys more";
        return "Prices here, with rent held to a third of the basket, " + ran + ". Followed one for one, with no lag: parity "
                + "stands at " + d + fxRate(fx.getParity()) + ", its trend at " + d + fxRate(fx.getParityTrend()) + ".";
    }

    /** THE REAL RATE (pure): the dial, the inflation people expect, the gap to the world's real rate, where that holds the currency, and how fast. */
    String realRateSentence() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        double held = fx.gate() * fx.getGapReached();
        return "The dial, " + ratePct(ui.game.getDebtManager().getPolicyRate()) + ", against expected inflation of "
                + ratePct(ui.game.getExpectedInflation()) + ": " + overUnder(fx.getRealRateDifferential()) + ", a level that "
                + heldWords(held, "the " + sym()) + ". " + String.format("%.0f%%", ForeignAccounts.GAP_IMPACT * 100)
                + " of a change shows at once, the rest within a year or two.";
    }

    /** THE BALANCE (pure): trade and capital over twelve months against a year of output, and where that holds the currency. */
    String balanceSentence() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        if (!(fx.getGdpTrailing() > 0)) return "Trade and capital over twelve months: " + balancePhrase(fx) + ".";
        return "Trade and capital over twelve months: " + balancePhrase(fx) + ", which "
                + heldWords(fx.gate() * fx.getBalanceReached(), "the " + sym()) + ".";
    }

    /** THE VAULT'S COVER (pure): how many months of imports it holds, the market's discount for the shortfall from THIN_COVER, and whether it moved anything this month. */
    String coverSentence() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        String idle = Math.abs(fx.getMoveCover()) < 5e-6 ? " It moved nothing this month." : "";
        String full = String.format("%.0f%%", ForeignAccounts.RISK_PREMIUM * 100);
        if (!(fx.getReservesUsd() > 0)) return "The vault is empty: the market's discount holds at its full " + full + "." + idle;
        if (!(fx.monthlyImports() > 0)) {
            return "The vault holds " + usd(fx.getReservesUsd()) + " and the city buys nothing abroad to cover: the market "
                    + "asks no discount." + idle;
        }
        String months = coverMonths(fx.importCover());
        String thin = String.format("%.0f months", ForeignAccounts.THIN_COVER);
        double discount = fx.coverTarget();
        return discount > 0
                ? "The vault holds " + months + " of imports, under the " + thin + " the market looks for: a discount of "
                  + String.format("%.1f%%", discount * 100) + ", the full " + full + " only when it is empty." + idle
                : "The vault holds " + months + " of imports, at least the " + thin + " the market looks for: no discount." + idle;
    }

    /** INTERVENTIONS (pure): what the treasury bought or sold this month, what the central bank sold defending the currency, and what is still felt - or that nothing was dealt. */
    String interventionSentence() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        List<String> did = new ArrayList<>();
        if (fx.getBoughtThisMonth() >= Money.NOTHING) did.add("the treasury bought dollars with " + d(fx.getBoughtThisMonth()));
        if (fx.getSoldThisMonth() >= Money.NOTHING) did.add("the treasury sold dollars for " + d(fx.getSoldThisMonth()));
        if (fx.getDefenceUsd() >= .005) did.add("the central bank sold " + usd(fx.getDefenceUsd()) + " defending the " + sym());
        double felt = Math.expm1(fx.getInterventionFelt());
        boolean still = Math.abs(felt) >= .0005;
        String fading = still ? "Still felt: the " + sym() + " " + share1(Math.abs(felt)) + (felt > 0 ? " cheaper" : " dearer")
                + ", fading by half in " + String.format("%.0f", ForeignAccounts.INTERVENTION_HALF_LIFE) + " months." : "";
        if (did.isEmpty()) return "Nothing dealt this month. " + (still ? fading : "Nothing earlier is still felt.");
        String said = String.join(", and ", did);
        return Character.toUpperCase(said.charAt(0)) + said.substring(1) + "." + (still ? " " + fading : "");
    }

    /**
     * THE WORLD (WN4; pure): the dollar's own swing against every money, money running to safety, and the prices of what the city sells abroad, each
     * as what it has the currency standing at now (gated by the city's trade, as the market is); in a panic, what the city's traits make of it.
     */
    String worldSentence() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        String d = sym();
        double g = fx.gate(), loading = fx.getWorldLoading(), risk = fx.getWorldRiskOff();
        String text = "The dollar's cycle " + heldWords(g * fx.getWorldCycle(), "the " + d)
                + ", money running to safety " + heldWords(g * loading * risk, "the " + d)
                + ", and the prices of what the city sells abroad " + heldWords(g * fx.getTermsOfTradeReached(), "the " + d) + ".";
        if (risk > .005 && loading < 0) text += " The city is a haven: a deep vault and a surplus make the " + d + " rise when money runs to safety.";
        else if (risk > .005) text += " Money is running to safety, and by the city's traits the " + d + " loads " + String.format("%.0f%%", loading * 100) + " weaker on a full one.";
        if (g < .9995) text += " They reach the rate in part, " + String.format("%.0f%%", g * 100) + ", as the market does.";
        return text;
    }

    /** THE MARKET (pure): the currency's own swing, what is left of the month's move, where it stands and how fast it fades. */
    String marketSentence() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        double swing = Math.expm1(fx.gate() * fx.getMarket());
        String stands = Math.abs(swing) < .0005 ? "stands at nothing now"
                : "has the " + sym() + " " + share1(Math.abs(swing)) + (swing > 0 ? " cheaper" : " dearer")
                  + " than the fundamentals put it";
        return "The " + sym() + "'s own swing: what is left of the month's move once the others are taken. It " + stands
                + ", and fades by half in about " + String.format("%.1f", ForeignAccounts.MARKET_HALF_LIFE / 12) + " years.";
    }

    /**
     * THE HOLD (XR2; pure): what the vault did to the rate beside where the
     * float's rule put it - a peg held, a band's edge held, or the peg letting
     * go - in the month's own figures; or that nothing was held.
     */
    String holdSentence() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        String d = sym();
        if (fx.brokeThisMonth()) {
            double jump = Math.expm1(fx.getLastBreakJump());
            return "The peg gave way, because " + fx.getLastBreakCause().words + ": the " + d + " landed at " + d
                    + fxRate(fx.getRate()) + ", " + share1(Math.abs(jump)) + (jump >= 0 ? " weaker" : " stronger")
                    + " in the month, where the float's rule had it at " + d + fxRate(fx.getLastBreakShadow())
                    + ". This is the peg letting go of the rate it had held.";
        }
        ForeignAccounts.Regime regime = fx.getRegime();
        if (regime == ForeignAccounts.Regime.PEG) {
            // Kept in another money than the US$ (WN4b), the rate is said to be that money's peg; for the US$ the sentences are what they were.
            String to = fx.getPegAnchor() == 0 ? "" : " (the peg is to " + ForeignAccounts.MONEY_NAME[fx.getPegAnchor()] + ")";
            return fx.isHeld()
                    ? "The vault held the " + d + " at the peg, " + d + fxRate(fx.getPegRate()) + to + ", away from where the "
                      + "float's rule put it, " + d + fxRate(fx.getShadowRate()) + ": the part of the month's move the peg "
                      + "kept off the rate."
                    : "The float's rule and the peg were in step, " + d + fxRate(fx.getPegRate()) + to + ": nothing to hold "
                      + "this month.";
        }
        if (regime == ForeignAccounts.Regime.BAND) {
            // Inside the edges and still held (XR5): the rate is not at an edge but leans on what the vault dealt at one, which fades.
            boolean inside = fx.getRate() < fx.bandWeakEdge() * (1 - 1e-9) && fx.getRate() > fx.bandStrongEdge() * (1 + 1e-9);
            if (fx.isHeld() && inside) {
                return "What the vault dealt at the band's edge still leans on the rate, " + d + fxRate(fx.getRate()) + " against "
                        + d + fxRate(fx.getShadowRate()) + " where the float's rule put it, fading by half in about "
                        + String.format("%.0f", ForeignAccounts.INTERVENTION_HALF_LIFE) + " months: the part of the month's "
                        + "move that dealing kept off the rate.";
            }
            return fx.isHeld()
                    ? "The rate reached the band's edge and the vault held it there, " + d + fxRate(fx.getRate()) + ", away "
                      + "from where the float's rule put it, " + d + fxRate(fx.getShadowRate()) + ": the part of the month's "
                      + "move the band kept off the rate."
                    : "The rate stayed inside the band, " + d + fxRate(fx.bandStrongEdge()) + " to " + d
                      + fxRate(fx.bandWeakEdge()) + ": nothing to hold this month.";
        }
        return "The hold moved the rate last month, under a regime the city has since left; nothing is held now.";
    }

    /** THE HOLD's (i): what it is, why the six do not stand on their own under it, and what it is outside a band and a peg. */
    static final String HOLD_INFO = "The part of the month's move the vault's hold made: under a peg the rate stays at the "
            + "peg, and under a band it stops at the edge, away from where the float's rule - the other forces - would "
            + "have put it. So this line undoes what they added, and all of them still add to the month's move. In a month a "
            + "peg gave way it is the peg letting go of the rate it had held. Nothing under Lean and Float, and "
            + "inside a band once what the vault dealt at its edge has faded. Policy › Money › The currency is where it is set.";

    /** THE MONTH'S MOVE (pure): where US$1 stood and where it stands, which the forces (and the hold, when it is a row) add up to. */
    String totalSentence() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        double now = fx.getRate(), move = fx.getLastMove(), before = now / Math.exp(move);
        if (!Double.isFinite(before) || Math.abs(move) < 5e-6) {
            return Currency.FOREIGN_SYMBOL + "1 stayed at " + sym() + fxRate(now) + ": nothing to add up.";
        }
        return Currency.FOREIGN_SYMBOL + "1 went from " + sym() + fxRate(before) + " to " + sym() + fxRate(now)
                + (showsHold(fx) ? ". The forces and the hold add up to it." : ". The forces add up to it.");
    }

    /** The forces card's (i): the parts and what each is, the world's among them, and under a band or a peg the hold. */
    static final String FORCES_INFO = "These parts add to the month's move, in log points of the rate. Parity's trend: the "
            + "city's prices against the world's, followed one for one. The real rate: a real rate over the world's holds the "
            + "currency dearer, building over a year or two. The balance: a deficit year after year cheapens it. The "
            + "vault's cover: an empty vault costs it a discount. Interventions: dollars the treasury or the central bank "
            + "buys or sells. The world: the dollar's own swing against every money, money running to safety and the "
            + "prices of what the city sells abroad. The market: the currency's own swing, which most of a real float's "
            + "month is. Under a band or a peg the hold joins them: the vault keeping the rate where it is set, away from "
            + "where the others would take it. Weaker is to the right.";

    /** ...and each force's own (i): what it is, and the dials behind it. */
    static final String TREND_INFO = "The city's price level against the world's, on the basket with rent held to a third "
            + "(rent is what no border arbitrages), smoothed into a level and a slope and followed one for one: with no lag, "
            + "a steady inflation gap passes into the rate in full.";
    static final String REAL_RATE_INFO = String.format("A real rate over the world's brings money here and holds the "
            + "currency dearer, as far as the market believes the gap will last (%.1f years, and never past %.0f%% either "
            + "way). %.0f%% of a change in the level shows in its month and the rest builds over a year or two. Policy › "
            + "Money's THE CURRENCY'S LEVEL shows it beside the dial.", ForeignAccounts.PERSISTENCE,
            ForeignAccounts.GAP_CAP * 100, ForeignAccounts.GAP_IMPACT * 100);
    static final String BALANCE_INFO = String.format("A year of the current and the financial account together, against a "
            + "year of the city's output: a deficit cheapens the currency by %.0f%% of its share of output, a surplus makes it "
            + "dearer, at most %.0f%% either way. Half of a change in it shows in a month. The vault's own dealing is not in "
            + "it.", ForeignAccounts.BALANCE_WEIGHT * 100, ForeignAccounts.BALANCE_CAP * 100);
    static final String COVER_FORCE_INFO = String.format("An empty vault costs the currency a %.0f%% discount, falling to "
            + "nothing at %.0f months of imports: the oldest rule of thumb for a safe reserve.",
            ForeignAccounts.RISK_PREMIUM * 100, ForeignAccounts.THIN_COVER);
    static final String INTERVENTION_INFO = String.format("Dollars the treasury buys cheapen the currency and dollars it "
            + "sells make it dearer, at the real size: %.0f%% for each 1%% of a year's output up to %.0f%% of it, and "
            + "less for each dollar past that, counted over everything dealt since the month turned, fading by half in %.0f "
            + "months. The central bank sells dollars into a fall, and that lifts the currency the same way. Under a peg or "
            + "a band the rate is held, and the hold (its own row) takes this off it again.",
            ForeignAccounts.INTERVENTION_IMPACT, ForeignAccounts.INTERVENTION_LINEAR * 100,
            ForeignAccounts.INTERVENTION_HALF_LIFE);
    static final String WORLD_FORCE_INFO = String.format("The dollar's own swing against every money, money running to safety, and the "
            + "prices of what the city sells abroad. The dollar's cycle moves the currency one for one: when the dollar rises "
            + "against everything it rises against this one. About once in eight years money runs to safety for a few months; "
            + "the currency's move in one is its traits': %.0f%% weaker for a small open money, less with a deep vault "
            + "(up to %.0f points at %.0f months of imports) and a surplus (%.0f points), more with hot money and debt abroad "
            + "(%.0f points for a fifth of a year's output). A city with six months of cover and a surplus is a haven and "
            + "rises, as the Swiss franc does. What the city sells abroad matters by %.0f%% of the change in its price "
            + "over the year, a third in each of the year's first %d months: a city that sells lumber cheapens when lumber "
            + "does. Gated by the city's trade, as the market is.",
            ForeignAccounts.RISK_OFF_BASE * 100, ForeignAccounts.HAVEN_PULL * 100, ForeignAccounts.HAVEN_COVER,
            ForeignAccounts.SURPLUS_PULL * 100, ForeignAccounts.EXPOSURE_WEIGHT * 20, WorldMoneys.TOT_PASS * 100, WorldMoneys.TOT_MONTHS);
    static final String MARKET_INFO = String.format("The currency's own swing, drawn from the city's seed so the same seed "
            + "gives the same path: about %.1f%% of fresh news a month, fading by half in %.1f years.",
            ForeignAccounts.MARKET_SIGMA * 100, ForeignAccounts.MARKET_HALF_LIFE / 12);

    /** The gate's (i): what it lets through, and what it does not gate. */
    static final String GATE_INFO = String.format("How much of the market's swing and of the fundamentals - the real rate, "
            + "the balance and the vault's cover - reaches the rate. A city that hardly trades has no market price to move: "
            + "they reach it in full once trade is %.0f%% of its output, in proportion below that, and not at all while it "
            + "trades next to nothing. Parity's trend and any dealing in dollars are not gated: the prices are the city's "
            + "own and a purchase is the treasury's.", ForeignAccounts.OPEN_ENOUGH * 100);

    /* ------------------------------ where the fundamentals hold it ------------------------------ */

    /** One step of the build-up: its name, what it does in words (null: the name spans), the rate after it, and whether it is a result (the fair level, the rate). */
    record FairStep(String name, String what, double after, boolean total) { }

    /**
     * WHERE THE FUNDAMENTALS HOLD IT (XR1; board 1): US$1 in the city's money
     * built up step by step - parity, the real rate, the vault's cover, the
     * balance, parity's trend ahead of (or behind) the month's parity, which
     * makes the fair level; then the market's swing, and any dealing still
     * felt, which make the rate - each with what it does in words, a thin bar
     * on a log scale, and the rate after it; and how fast a swing fades.
     */
    VBox fundamentalsCard() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        VBox c = card(cardHead(Icons.EXCHANGE, Palette.MONEY, "WHERE THE FUNDAMENTALS HOLD IT", FUNDAMENTALS_INFO,
                quiet(Currency.FOREIGN_SYMBOL + "1 in " + sym() + ", built up step by step")));
        if (fx.isPinned()) {
            c.getChildren().add(Kept.scope("pinned", () -> line("The rate is pinned: these steps are not driving it. Left to the fundamentals the "
                    + sym() + " would stand at " + sym() + fxRate(fx.getFairLevel()) + " per " + Currency.FOREIGN_SYMBOL + ".",
                    null)));
            return c;
        }
        List<FairStep> steps = fairSteps();
        double base = fairBase(steps);
        c.getChildren().add(Kept.scope("table", () -> fairTable(steps, base)));
        c.getChildren().add(Kept.scope("bars start", () -> muted("Bars start at " + sym() + fxRate(base) + ", on a log scale: a cheaper " + sym()
                + " is a longer bar.")));
        c.getChildren().add(Kept.scope("fade", () -> line(fadeSentence(), null)));
        return c;
    }

    /**
     * The steps from parity to the rate (pure): each computed from the model's
     * getters, the fair level reached by them and set to getFairLevel() itself
     * (they are equal but for rounding); the market's step is what the rate
     * has over the fair level once any dealing still felt is taken, so the
     * table closes on getRate() whatever the guards did. The market and the
     * dealing rows have no "what" - their name says it.
     */
    List<FairStep> fairSteps() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        double g = fx.gate();
        double trend = fx.getParityTrend();
        double parity = fx.getParity() > 0 ? fx.getParity() : trend;
        double gap = g * fx.getGapReached(), cover = g * fx.getCoverReached(), balance = g * fx.getBalanceReached();
        double afterGap = parity * Math.exp(gap);
        double afterCover = afterGap * Math.exp(cover);
        double afterBalance = afterCover * Math.exp(balance);
        double ahead = Math.log(trend / parity);
        double fair = fx.getFairLevel(), rate = fx.getRate();
        List<FairStep> out = new ArrayList<>();
        out.add(new FairStep("Parity (rent held to a third)", "where the same basket costs the same here and abroad, this month",
                parity, false));
        out.add(new FairStep("+ the real rate", overUnder(fx.getRealRateDifferential()) + " " + heldWords(gap, "it"), afterGap, false));
        out.add(new FairStep("+ the vault's cover", coverPhrase(fx) + " " + heldWords(cover, "it"), afterCover, false));
        out.add(new FairStep("+ the balance", balancePhrase(fx) + " " + heldWords(balance, "it"), afterBalance, false));
        double move = Math.expm1(ahead);
        if (!(Math.abs(move) >= .0005)) {
            out.add(new FairStep("+ parity's trend", "level with the month's parity: leaves it where it stands", fair, false));
        } else {
            out.add(new FairStep(ahead > 0 ? "+ parity's trend ahead" : "+ parity's trend behind",
                    (ahead > 0 ? "ahead of" : "behind") + " the month's parity: " + share1(Math.abs(move))
                            + (ahead > 0 ? " cheaper" : " dearer"), fair, false));
        }
        out.add(new FairStep("= the fair level", "where the fundamentals alone put it", fair, true));
        double felt = fx.getInterventionFelt();
        boolean dealt = Math.abs(Math.expm1(felt)) >= .0005;
        double market = fair > 0 ? rate / (fair * (dealt ? Math.exp(felt) : 1)) : 1;
        double past = market - 1;
        String name = !(Math.abs(past) >= .0005) ? "The market has it at the fair level"
                : "The market has it at " + share1(Math.abs(past)) + (past > 0 ? " past" : " short of") + " the fair level";
        out.add(new FairStep(name, null, dealt ? fair * market : rate, !dealt));
        if (dealt) {
            double change = Math.expm1(felt);
            out.add(new FairStep("Interventions still felt: " + share1(Math.abs(change)) + (change > 0 ? " cheaper" : " dearer"),
                    null, rate, true));
        }
        return out;
    }

    /** Where the table's bars start: a rate of 1, or the lowest step when a step runs under it. Pure. */
    static double fairBase(List<FairStep> steps) {
        double lo = 1;
        for (FairStep r : steps) if (r.after() > 0) lo = Math.min(lo, r.after());
        return lo;
    }

    /** The steps as a table: step, what it does, a thin bar from `base` on a log scale, and the rate after it. */
    Node fairTable(List<FairStep> steps, double base) {
        GridPane g = Kept.grid();
        g.setHgap(14);
        g.setVgap(8);
        javafx.scene.layout.ColumnConstraints stepCol = new javafx.scene.layout.ColumnConstraints(190, 250, 320);
        javafx.scene.layout.ColumnConstraints whatCol = new javafx.scene.layout.ColumnConstraints(120, 400, Double.MAX_VALUE);
        whatCol.setHgrow(Priority.ALWAYS);
        javafx.scene.layout.ColumnConstraints barCol = new javafx.scene.layout.ColumnConstraints(100, 220, 260);
        javafx.scene.layout.ColumnConstraints rateCol = new javafx.scene.layout.ColumnConstraints(90, 110, 140);
        g.getColumnConstraints().addAll(stepCol, whatCol, barCol, rateCol);
        g.add(muted("step"), 0, 0);
        g.add(muted("what it does"), 1, 0);
        Label heading = muted(Currency.FOREIGN_SYMBOL + "1 is");
        GridPane.setHalignment(heading, javafx.geometry.HPos.RIGHT);
        g.add(heading, 3, 0);
        double hi = base;
        for (FairStep r : steps) if (r.after() > 0) hi = Math.max(hi, r.after());
        double span = Math.log(hi / base);
        int row = 1;
        for (FairStep r : steps) {
            int at = row;
            final double barSpan = span;
            // Kept (KP6): a row a step, a scope keyed by its place and whether its name spans the "what" column (a spanning label keeps its
            // span: GridPane.add() does not set it back), so a step that gains or loses its words is made afresh.
            Kept.scope("step:" + (row - 1) + (r.what() == null ? "/span" : ""), () -> {
                String tone = r.total() ? Palette.TEXT_HEAD : Palette.TEXT_LABEL;
                Label step = r.total() ? wordsIn(r.name(), Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD))
                        : words(r.name(), Palette.SIZE_LABEL + 1, tone);
                if (r.what() == null) {
                    g.add(step, 0, at, 2, 1);
                } else {
                    g.add(step, 0, at);
                    g.add(words(r.what(), Palette.SIZE_LABEL, Palette.TEXT_MUTED), 1, at);
                }
                double len = r.after() > 0 && barSpan > 0 ? Math.max(0, Math.log(r.after() / base)) : 0;
                Node bar = segmentBar(List.of(Segment.of(len, r.total() ? Palette.MONEY : Palette.MONEY_LIGHT)),
                        barSpan > 0 ? barSpan : 1, null, 0, 8);
                GridPane.setValignment(bar, javafx.geometry.VPos.CENTER);
                g.add(bar, 2, at);
                Label after = figure(sym() + fxRate(r.after()), Palette.SIZE_LABEL + 1, r.total() ? Palette.TEXT_HEAD : Palette.TEXT_LABEL);
                GridPane.setHalignment(after, javafx.geometry.HPos.RIGHT);
                g.add(after, 3, at);
            });
            row++;
        }
        return g;
    }

    /** How fast a market swing fades, and where it would be halfway back (pure): the rate halfway to the fair level in logs, by the half-life's month. */
    String fadeSentence() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        double fair = fx.getFairLevel(), rate = fx.getRate();
        String years = String.format("%.1f", ForeignAccounts.MARKET_HALF_LIFE / 12);
        if (!(fair > 0) || !(rate > 0) || Math.abs(rate / fair - 1) < .0005) {
            return "A market swing fades by half in about " + years + " years. The rate stands at the fair level: no swing "
                    + "to fade just now.";
        }
        double half = fair * Math.exp(Math.log(rate / fair) / 2);
        int by = ui.game.getMonth() + (int) ForeignAccounts.MARKET_HALF_LIFE;
        return "A market swing fades by half in about " + years + " years. From here it is about halfway back (" + sym()
                + fxRate(half) + ", if the fair level stood still) by month " + String.format("%,d", by) + ".";
    }

    /** The steps card's (i): how to read the table. */
    static final String FUNDAMENTALS_INFO = "The rate built up from parity, one step at a time, each row the rate after "
            + "its step. Parity is the city's basket with rent held to a third, against the world's. The real rate, the "
            + "vault's cover and the balance each move it by the level the market has priced so far, as much of it as the "
            + "city's trade lets through. Parity's trend - which the rate follows one for one - takes it from the month's "
            + "parity to where its smoothed level and slope stand. That is the fair level. The market's swing, and any "
            + "dealing still felt, put the rate where it is.";

    /** THE RULE, folded at the page's foot: the board's paragraph in the model's words and dials. */
    static final String RULE = String.format("The rate is parity's trend, times where the fundamentals put the currency, "
            + "times the market's swing, times what dealing in dollars still adds. Parity's trend, on the basket with rent "
            + "held to a third (rent is the part no border arbitrages), is followed one for one, with no lag: a level that "
            + "takes %.0f%% of a month's jump in parity at once and a slope that learns over about %.0f months. The "
            + "fundamentals are the real rate gap times how long the market expects it (%.1f years, capped at %.0f%%; %.0f%% "
            + "of a change shows at once and the rest within a year or two), the balance (%.0f%% of a year's overall balance "
            + "against a year's output, at most %.0f%% either way) and the vault's cover (a %.0f%% discount on an empty "
            + "vault, falling to nothing at %.0f months of imports). The market is its own swing: seeded, fading by half in "
            + "%.1f years, about %.1f%% of fresh news a month. Dealing in dollars moves the rate at the real size, %.0f%% for "
            + "each 1%% of a year's output, up to %.0f%% of it dealt between two month ends and by less for each dollar past "
            + "that, fading by half in %.0f months. Under a peg or a band the rate is held, so dealing moves the rate "
            + "behind the hold, and the vault deals to meet it. The market and the fundamentals reach the rate in "
            + "full once the city trades %.0f%% of its output, and in proportion below that.",
            ForeignAccounts.TREND_LEVEL * 100, 1 / ForeignAccounts.TREND_SLOPE, ForeignAccounts.PERSISTENCE,
            ForeignAccounts.GAP_CAP * 100, ForeignAccounts.GAP_IMPACT * 100, ForeignAccounts.BALANCE_WEIGHT * 100,
            ForeignAccounts.BALANCE_CAP * 100, ForeignAccounts.RISK_PREMIUM * 100, ForeignAccounts.THIN_COVER,
            ForeignAccounts.MARKET_HALF_LIFE / 12, ForeignAccounts.MARKET_SIGMA * 100, ForeignAccounts.INTERVENTION_IMPACT,
            ForeignAccounts.INTERVENTION_LINEAR * 100, ForeignAccounts.INTERVENTION_HALF_LIFE, ForeignAccounts.OPEN_ENOUGH * 100);

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
            c.getChildren().add(Kept.scope("nothing owed", () -> line("Nothing is owed abroad, so the rate cannot make the city's debt bigger.",
                    "Nothing is owed abroad, so the rate cannot make the city's debt bigger. It still decides what imports "
                            + "cost and what exports earn.")));
            return c;
        }
        c.getChildren().add(cardLine("owed abroad", usdFull(fx.getForeignDebtUsd()), null));
        c.getChildren().add(cardLine("which at this rate is", dFull(fx.getForeignDebt()), null));
        // CD9 (CD8-R, 2026-11-08): the booked line (a gain positive), not ForeignAccounts' second figure; shown dearer-positive as before.
        double moved = -ui.game.getEconomyManager().getNationalAccounts().getDebtExchangeGain();
        double sinceFounding = -ui.game.debtExchangeSinceFounding();
        if (Math.abs(moved) > .005) {
            c.getChildren().add(cardLine("the currency moved it this month by", signedFull(moved), null,
                    moved > 0
                            ? "Dearer this month, and nobody was paid a cent for it. That is what a devaluation does to a "
                              + "country that borrowed in somebody else's money."
                            : "Cheaper this month — the same paper, a stronger currency. It works both ways and it is not "
                              + "income either way."));
        }
        if (Math.abs(sinceFounding) > .005) {
            c.getChildren().add(cardLine("since founding, the currency has " + (sinceFounding > 0 ? "added" : "taken off"),
                    dFull(Math.abs(sinceFounding)), null));
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
        // XR4: the balances earn their central banks' rates; the yield, struck on the dollars the earning balances were worth.
        if (Math.abs(fx.getVaultInterestUsd()) > .005) {
            c.getChildren().add(cardLine("earning, a year on average", ratePct(fx.getVaultYield()), null));
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
        // Kept (KP6): each card a scope of its own.
        // The vault in eight (WN6, board 3): first, where it holds or can hold more than the dollar.
        VBox eight = Kept.scope("eight", vault::eightCard);
        if (eight != null) {
            targets.put(EIGHT_CARD, eight);
            page.getChildren().add(eight);
        }
        VBox cover = Kept.scope("cover", this::coverCard);
        targets.put(COVER, cover);
        page.getChildren().add(row(Kept.scope("whose", this::whoseCard), cover));
        VBox leave = Kept.scope("leave", this::leaveCard);
        targets.put(LEAVE, leave);
        page.getChildren().add(row(leave, Kept.scope("moved", this::movedCard)));
        VBox exchange = Kept.scope("exchange", this::exchangeCard);
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
            c.getChildren().add(Kept.scope("empty", () -> line("The vault is empty and nothing is owed abroad.",
                    "The vault is empty and nothing is owed abroad. Export earnings go to the firms that earned them — a "
                            + "treasury holds foreign money only if it has bought some, or borrowed it abroad.")));
            return c;
        }
        c.getChildren().add(Kept.scope("claims", () -> claimBar(gross, debt, parked, own)));
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
            c.getChildren().add(Kept.scope("elsewhere", () -> doorPill(d(elsewhere) + " more is held abroad by the firms and households",
                    Icons.TRADE, Palette.MONEY, () -> open(MONTH, HOLDINGS))));
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
        if (!(scale > 0)) return Kept.column(0);
        String sand = Palette.CATEGORIES[4];
        List<Segment> parts = new ArrayList<>();
        if (debt > 0) parts.add(seg(debt, Palette.MONEY_DARK, "owed abroad on paper\n" + dFull(debt)));
        if (parked > 0) parts.add(seg(parked, sand, "parked here and free to leave\n" + dFull(parked)));
        if (own > 0) parts.add(seg(own, Palette.MONEY, "the city's own\n" + dFull(own)));
        List<Tick> ticks = List.of(new Tick(gross, Palette.TEXT_MAX, 2, "the vault ends here", "the vault ends here: " + dFull(gross)));
        VBox box = Kept.column(4, segmentBar(parts, scale, ticks, 0, 22),
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
            c.getChildren().add(Kept.scope("nothing bought", () -> line("Nothing is bought abroad yet, so there is nothing to cover.", null)));
            return c;
        }
        double months = fx.importCover();
        int level = ForeignAccounts.coverLevel(months);
        c.getChildren().add(Kept.scope("reading", () -> figure(coverMonths(months), 28, coverTone(level))));
        c.getChildren().add(Kept.scope("track", () -> bandTrack(Math.min(1, months / COVER_SCALE),
                new double[] {ForeignAccounts.THIN_COVER / COVER_SCALE, ForeignAccounts.COMFORTABLE_COVER / COVER_SCALE, 1},
                new String[] {Palette.BAD, Palette.WARN, Palette.GOOD},
                List.of(new Tick(ForeignAccounts.THIN_COVER / COVER_SCALE, Palette.TEXT_LABEL, 1,
                                String.format("%.0f", ForeignAccounts.THIN_COVER), null),
                        new Tick(ForeignAccounts.COMFORTABLE_COVER / COVER_SCALE, Palette.TEXT_LABEL, 1,
                                String.format("%.0f months", ForeignAccounts.COMFORTABLE_COVER), null)),
                0, 12, false)));
        c.getChildren().add(Kept.scope("against", () -> line(usd(fx.getReservesUsd()) + " against " + d(fx.monthlyImports())
                + " a month of imports", null)));
        double toThin = fx.toCover(ForeignAccounts.THIN_COVER);
        if (toThin > 0) {
            c.getChildren().add(Kept.scope("would need", () -> line(String.format("%.0f months would need %s more", ForeignAccounts.THIN_COVER, d(toThin))
                    + " · " + String.format("%.0f months %s", ForeignAccounts.COMFORTABLE_COVER,
                    d(fx.toCover(ForeignAccounts.COMFORTABLE_COVER))), null)));
            c.getChildren().add(Kept.scope("buy", () -> doorPill("Buy reserves", Icons.EXCHANGE, Palette.MONEY, () -> openExchange(true))));
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
            c.getChildren().add(Kept.scope("nothing parked", () -> line("Nothing is parked here: nothing can leave on no notice.",
                    "No foreign money is parked in this city's bank. Nothing can leave on no notice, which is a real form "
                            + "of safety and one that a high policy rate gives away.")));
        } else {
            // Kept (KP6): the parked money's lines are a scope of their own, which a city with none parked has not.
            Kept.scope("parked", () -> {
                HBox reading = Kept.row(Palette.GAP, figure(g.reading(), 22, Palette.TEXT_HEAD), chip(g.chip(), g.chipTone()),
                        muted("backed by the vault"));
                reading.setAlignment(Pos.CENTER_LEFT);
                c.getChildren().add(reading);
                c.getChildren().add(cardLine("parked in the bank", dFull(hot.getStock()), null));
                c.getChildren().add(cardLine("as a share of the bank's funding", share1(bank.hotFundingShare()), null));
                c.getChildren().add(cardLine("the excess return pulling it", points(hot.getSpread()), null,
                        "What the city pays over the world rate, less what the world charges it for its own risk. Only the rest "
                                + "is a reason to come — and the same money leaves the moment it stops being one."));
                c.getChildren().add(cardLine("what would come at that rate", dFull(hot.getTarget()), null));
            });
        }
        Kept.scope("history", () -> {
            if (hot.getStopsSuffered() > 0) {
                c.getChildren().add(cardLine("sudden stops since founding", String.valueOf(hot.getStopsSuffered()), null));
            }
            if (hot.getPeakStock() > 0) c.getChildren().add(cardLine("the most that was ever here", dFull(hot.getPeakStock()), null));
            c.getChildren().addAll(riskOffLines(hot));
        });
        return c;
    }

    /**
     * ...and what the risk-offs did to it (XR4; pure): when one has ever sent the hot money or the carry home, a line of the local money it took, the
     * months counted, and this month's share of it where this month was one. None for a city no risk-off has touched.
     */
    List<Node> riskOffLines(CapitalFlows hot) {
        List<Node> out = new ArrayList<>();
        double lifetime = hot.getRiskOffDepartedLifetime() + hot.getCarryRiskRepaidLifetime();
        if (!(lifetime > 0)) return out;
        double month = hot.getRiskOffDeparted() + hot.getCarryRiskRepaid();
        int months = hot.getRiskOffMonths();
        out.add(cardLine("risk-offs sent " + dFull(lifetime) + " home", months + (months == 1 ? " month" : " months")
                + (month > 0 ? " · " + dFull(month) + " this month" : ""), month > 0 ? Palette.WARN : null,
                String.format("When the world runs for the exit the money that came for the rate leaves with it: %.0f%% of the "
                        + "parked money a month at a risk-off of size one, and %.0f%% of the carry trade's borrowing repaid, scaled "
                        + "by how deep the risk-off is as it fades. These are the sums, in local money, since founding, over the "
                        + "%d %s a risk-off was felt.", CapitalFlows.RISK_OFF_HOT_UNWIND * 100, CapitalFlows.RISK_OFF_CARRY_UNWIND * 100,
                        months, months == 1 ? "month" : "months")));
        return out;
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
        // ...AND THE MONEYS MOVING AGAINST EACH OTHER (WN4, THE VAULT IN EIGHT): the US$ the vault gained or lost because the eight moved, and the
        // holding that moved it most. None on a vault that is all US$.
        double cross = fx.getLastVaultCrossUsd();
        if (Math.abs(cross) > .005) {
            ForeignAccounts.CrossDriver driver = fx.getLastVaultCrossDriver();
            String who = driver == null ? "" : String.format(": %s %s %.1f%% against the dollar", ForeignAccounts.MONEY_NAME[driver.index()],
                    driver.move() < 0 ? "fell" : "rose", Math.abs(driver.move()) * 100);
            out.add(new Moved("the moneys moved it by " + (cross < 0 ? "−" : "+") + usd(Math.abs(cross)) + who, Palette.TEXT_LABEL,
                    "The vault keeps a balance in each of the eight moneys, and the dollar value of each moves when it moves against the "
                            + "dollar (gold, if the vault holds any, is worth the dollars an ounce fetches, and moves the same way). Nobody was paid anything and nothing left the vault: it is not cash until a balance is sold, "
                            + "and a sale is at that day's rate. A vault that is all dollars has none of this."));
        }
        // ...AND WHAT THE BALANCES EARNED (XR4, THE VAULT EARNS ITS MONEYS' RATES): each at its central bank's policy rate, kept in the vault in that
        // money, and the money that earned (or lost) most. None in a month nothing was earned: a vault that is empty, or a city with no world file.
        double interest = fx.getVaultInterestUsd();
        if (Math.abs(interest) > .005) {
            int driver = fx.getVaultInterestDriver();
            PolicyRates policy = ui.game.getPolicyRates();
            String who = driver < 0 ? "" : policy == null || !Double.isFinite(policy.rate(driver))
                    ? ": " + ForeignAccounts.MONEY_NAME[driver] + (fx.getVaultInterestDriverUsd() < 0 ? " cost it most" : " earned most")
                    : String.format(": %s %s most, at %s", ForeignAccounts.MONEY_NAME[driver],
                            fx.getVaultInterestDriverUsd() < 0 ? "cost it" : "earned", ratePct(policy.rate(driver)));
            out.add(new Moved("the vault " + (interest < 0 ? "paid " : "earned ") + usd(Math.abs(interest)) + " in interest this month" + who,
                    Palette.TEXT_LABEL,
                    String.format("The vault keeps a balance in each of the eight moneys, and each balance earns its central bank's "
                            + "policy rate - the Fed's on the dollars, the ECB's on the euros, and so on - a twelfth of it a month (gold earns none). The "
                            + "interest stays in the vault, in that money: no %s is made for it, and it reaches the city only when the "
                            + "vault sells. The vault's yield this month is %s a year, the interest over what the earning balances were "
                            + "worth; %s has been earned since founding. A bank whose rate is below nothing takes from its balance "
                            + "instead.", sym(), ratePct(fx.getVaultYield()), usdFull(fx.getVaultInterestUsdLifetime()))));
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
                + "sales are the exchange, below. Besides them four things move the vault: the currency, which revalues "
                + "its dollars; the interest its balances earn; the central bank, which sells them defending a currency pushed down; "
                + "and the land office, when land is paid for out of it.", null));
        List<Moved> all = moved();
        if (all.isEmpty()) {
            c.getChildren().add(Kept.scope("nothing else", () -> line("Nothing else moved it this month.", null)));
            return c;
        }
        VBox list = Kept.column(6);
        for (Moved m : all) list.getChildren().add(chipInfo(m.words(), m.tone(), m.info()));
        c.getChildren().add(list);
        return c;
    }

    /* ------------------------------ the exchange ------------------------------ */

    /** The warning past the bound (XI2): its title, its line and what sits behind its (i). */
    record PastBound(String title, String line, String whole) { }

    /**
     * The warning for an ask that takes the month's dealing past INTERVENTION_LINEAR of a year's output, or null (pure: the model's figures only). XI2: the
     * bound is on what the treasury has dealt since the last reprice with this piece on top (ForeignAccounts.yearsOfOutputWith(), the sum step 5 reads), not on
     * the piece alone - several pieces each under it cross it together - and the piece's move is what it adds to what is dealt already
     * (dealEffectOnTop()). Under a peg the rate stays at the peg and the deal moves the float's own rate behind it, which the vault then deals against (XR5);
     * under a band the rate stops at its edge once the move would carry it there.
     */
    PastBound pastBound() {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        if (!(tradeExchange > 0)) return null;
        double piece = tradeBuying ? tradeExchange : -tradeExchange;
        double total = Math.abs(fx.yearsOfOutputWith(piece));
        if (total <= ForeignAccounts.INTERVENTION_LINEAR) return null;
        String kind = tradeBuying ? "purchase" : "sale";
        boolean before = Money.something(fx.getDealtSinceReprice());
        String size = total >= 1 ? String.format("%.1f years", total) : String.format("%.0f%% of a year", total * 100);
        String title = before ? String.format("This %s takes the month's dealing to %s of the city's output", kind, size)
                : String.format("This %s is %s of the city's output", kind, size);
        String line = String.format(before ? "The month's dealing counts together: past %.0f%% of a year's output each further dollar moves the %s less."
                        : "Past %.0f%% of a year's output each further dollar moves the %s less.",
                ForeignAccounts.INTERVENTION_LINEAR * 100, sym());
        StringBuilder whole = new StringBuilder(String.format("Up to %.0f%% of a year's output dealt between two month ends moves the rate %.0f%% for "
                        + "each 1%% of it, fading by half in %.0f months; past that each further dollar moves it less, so a deal many times "
                        + "the size moves it only a few times as much. ",
                ForeignAccounts.INTERVENTION_LINEAR * 100, ForeignAccounts.INTERVENTION_IMPACT, ForeignAccounts.INTERVENTION_HALF_LIFE));
        if (before) {
            double dealt = fx.getDealtSinceReprice();
            whole.append(String.format("The treasury has already %s %s since the month turned, %.1f%% of a year's output, so this piece lands on top of it. ",
                    dealt > 0 ? "bought" : "sold", d(Math.abs(dealt)), Math.abs(fx.dealtYears()) * 100));
        }
        String own = before ? "On top of that this piece alone" : "This one alone";
        String mult = (fx.dealEffectOnTop(piece) >= 0 ? "×" : "÷") + String.format("%.1f", fx.dealFactor(piece));
        String fades = String.format("half of that gone in %.0f months", ForeignAccounts.INTERVENTION_HALF_LIFE);
        ForeignAccounts.Regime regime = fx.getRegime();
        if (regime == ForeignAccounts.Regime.PEG) {
            whole.append(String.format("Under the peg the rate stays at %s%s, held. %s would move the float's own rate behind it about %s, and the "
                            + "vault deals to hold the peg against that (a peg gives way if holding it would take the vault under its floor).",
                    sym(), fxRate(fx.rateAfterDeal(piece)), own, mult));
        } else if (regime == ForeignAccounts.Regime.BAND && fx.regimeHoldsDeal(piece)) {
            whole.append(String.format("Under the band the rate stops at its edge, %s%s. %s would move the float's own rate about %s, and the vault "
                            + "deals to hold the rest.", sym(), fxRate(fx.rateAfterDeal(piece)), own, mult));
        } else if (regime == ForeignAccounts.Regime.BAND) {
            whole.append(String.format("Under the band the rate stays inside it. %s would move it about %s (%s per %s) in its month, to %s%s, %s.",
                    own, mult, sym(), Currency.FOREIGN_SYMBOL, sym(), fxRate(fx.rateAfterDeal(piece)), fades));
        } else {
            whole.append(String.format("%s would move the rate about %s (%s per %s) in its month, %s.", own, mult, sym(), Currency.FOREIGN_SYMBOL, fades));
        }
        return new PastBound(title, line, whole.toString());
    }

    /** What a press on the exchange's button says (pure: the probe reads it): GO with the amount; CHOOSE with nothing asked yet - a press asks the smallest step and prices it, and never trades. */
    Press exchangePress() {
        int money = ForeignAccounts.moneyIndex(tradeMoney);
        String plural = money < 0 ? null : ForeignAccounts.MONEY_PLURAL[money];
        if (tradeExchange <= 0) {
            return new Press(Look.CHOOSE, plural != null ? (tradeBuying ? "Buy " : "Sell ") + plural
                    : tradeBuying ? "Buy foreign money" : "Sell foreign money", "choose how much above");
        }
        return new Press(Look.GO, tradeBuying ? "Buy " + d(tradeExchange) + " of " + (plural != null ? plural : "foreign money")
                : "Sell " + d(tradeExchange) + " of " + (plural != null ? plural : "the vault"), tradeBuying
                ? "cash out of the treasury, " + usd(ui.game.getForeignAccounts().toUsd(tradeExchange)) + " into the vault"
                        + (plural != null ? ", as " + plural : "")
                : usd(ui.game.getForeignAccounts().toUsd(tradeExchange)) + " out of the vault, cash into the treasury");
    }

    /**
     * The names on the exchange's chips, and the mark each money's amounts carry (WN4), in ForeignAccounts' order: WorldMoneys.EIGHT's, then
     * gold's (GD2, the ninth: its mark is ounces, which ownMoney writes after the figure).
     */
    static final String[] MONEY_CHIP = {"US$", "Euro", "Yen", "Pound", "Yuan", "Canadian $", "Australian $", "Franc", "Gold"};
    static final String[] MONEY_MARK = {"US$", "€", "¥", "£", "CN¥", "C$", "A$", "CHF ", "XAU "};

    /**
     * A balance of one of the nine in its own money, abbreviated and marked: "€3.4M". Gold's (GD2) is thousands of troy ounces, written as the
     * others' figures are, with " oz" after and no sign: "12.3k oz", "1.2M oz".
     */
    static String ownMoney(int k, double thousands) {
        return k == ForeignAccounts.GOLD ? marked("", money(thousands)) + " oz" : marked(MONEY_MARK[k], money(thousands));
    }

    /** Gold's price this month, US$ an ounce (ForeignAccounts.getMoneyUsd(GOLD)), as the screens say it: "US$20.67 an ounce", or with the unit "oz", "US$20.67 an oz". */
    static String goldPrice(double usdPerOunce, String unit) {
        return Currency.FOREIGN_SYMBOL + String.format("%,.2f an %s", usdPerOunce, unit);
    }

    /**
     * THE MONEY PICKER (WN4, under the toggle): "By the mix" when buying and "The largest" when selling, then the eight that have a rate
     * this month (just the US$ with no world file), and Gold last when it has a price (GD2: gold is bought by name, as the dollars are).
     * The chosen one is what a press deals in (tradeMoney, null for the first chip).
     */
    FlowPane moneyPicker(ForeignAccounts fx) {
        String first = tradeBuying ? "By the mix" : "The largest";
        List<String> names = new ArrayList<>();
        names.add(first);
        for (int k = 0; k < ForeignAccounts.MONEYS; k++) if (fx.hasRate(k)) names.add(MONEY_CHIP[k]);
        int now = ForeignAccounts.moneyIndex(tradeMoney);
        FlowPane strip = Kept.scope("money picker", () -> chipStrip(names.toArray(new String[0]), now < 0 ? first : MONEY_CHIP[now],
                Palette.SIZE_LABEL, pick -> {
                    tradeMoney = null;
                    for (int k = 0; k < ForeignAccounts.MONEYS; k++) if (MONEY_CHIP[k].equals(pick)) tradeMoney = ForeignAccounts.codeOf(k);
                    showForeignMenu();
                }));
        strip.setAlignment(Pos.CENTER_LEFT);
        return strip;
    }

    /**
     * THE RESERVE MIX (WN4, D20): where a purchase of the treasury's, the strong edge's or a peg's goes - All US$ (the default), Match our
     * trade, The world's mix, or Choose - read from and written to ForeignAccounts; under Choose nine rows of the money (the eight, then gold: no interest, GD2), its share and
     * two small buttons, the model keeping the shares at 100%; (what the vault holds in each money is VaultTable's card, since WN6). Empty where
     * no money but the dollar has a rate and the vault holds nothing else: there is nothing to mix.
     */
    VBox reserveMix(ForeignAccounts fx) {
        boolean others = false;
        for (int k = 1; k < ForeignAccounts.MONEYS; k++) others |= fx.hasRate(k) || fx.getHolding(k) > 0;
        VBox box = Kept.column(6);
        if (!others) return box;
        box.getChildren().add(words("WHERE A PURCHASE GOES", Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        ForeignAccounts.Mix[] mixes = ForeignAccounts.Mix.values();
        String[] names = new String[mixes.length];
        for (int i = 0; i < names.length; i++) names[i] = mixes[i].words;
        FlowPane strip = Kept.scope("mixes", () -> chipStrip(names, fx.getReserveMix().words, Palette.SIZE_LABEL, pick -> {
            for (ForeignAccounts.Mix m : mixes) if (m.words.equals(pick)) fx.setReserveMix(m);
            showForeignMenu();
        }));
        strip.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().add(strip);
        if (fx.getReserveMix() == ForeignAccounts.Mix.CHOSEN) {
            double[] shares = fx.getChosenShares();
            for (int k = 0; k < ForeignAccounts.MONEYS; k++) {
                final int money = k;
                // Kept (KP6): a row a money, a scope keyed by it.
                box.getChildren().add(Kept.scope("mix:" + k, () -> {
                    Label name = words(ForeignAccounts.MONEY_PLURAL[money].substring(0, 1).toUpperCase() + ForeignAccounts.MONEY_PLURAL[money].substring(1)
                            + (!fx.hasRate(money) ? " (no rate yet)" : money == ForeignAccounts.GOLD ? " (no interest)" : ""), Palette.SIZE_LABEL, fx.hasRate(money) ? Palette.TEXT_LABEL : Palette.TEXT_MUTED);
                    HBox.setHgrow(name, Priority.ALWAYS);
                    name.setMaxWidth(Double.MAX_VALUE);
                    HBox row = Kept.row(Palette.GAP, name, figure(String.format("%.0f%%", shares[money] * 100), Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD),
                            stepChip("−5", () -> {
                                fx.shiftChosen(money, -.05);
                                showForeignMenu();
                            }, true),
                            stepChip("+5", () -> {
                                fx.shiftChosen(money, .05);
                                showForeignMenu();
                            }, false));
                    row.setAlignment(Pos.CENTER_LEFT);
                    return row;
                }));
            }
        }
        // The "held: ..." line this ended with is IN THE VAULT, IN EIGHT MONEYS' table now (WN6, VaultTable).
        return box;
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
        FlowPane toggle = Kept.scope("toggle", () -> chipStrip(new String[] {"Buy — cash into the vault", "Sell — the vault into cash"},
                tradeBuying ? "Buy — cash into the vault" : "Sell — the vault into cash", Palette.SIZE_LABEL, pick -> {
                    boolean nowBuying = pick.startsWith("Buy");
                    if (nowBuying != tradeBuying) { tradeExchange = 0; tradeMoney = null; }
                    tradeBuying = nowBuying;
                    showForeignMenu();
                }));
        toggle.setAlignment(Pos.CENTER_RIGHT);
        VBox c = card(cardHead(Icons.EXCHANGE, Palette.MONEY, "BUY OR SELL FOREIGN MONEY", null, toggle));
        c.getChildren().add(Kept.scope("lead", () -> line(tradeBuying
                        ? "Local money out of the treasury, foreign money into the vault."
                        : "Foreign money out of the vault, local money into the treasury.",
                tradeBuying
                        ? "Local money out of the treasury, foreign money into the vault. It costs the city cash it could "
                          + "have spent, and it buys cover — months of imports the vault could pay for, which is what the "
                          + "central bank sells to meet a push against the currency."
                        : "Foreign money out of the vault, local money into the treasury. This is how a treasury gets at "
                          + "money it holds abroad, AND it is everything a central bank has ever been able to do about an "
                          + "exchange rate. Both are true at once.")));
        if (fx.getBoughtThisMonth() > 0 || fx.getSoldThisMonth() > 0) {
            c.getChildren().add(Kept.scope("this month", () -> chips(
                    fx.getBoughtThisMonth() > 0 ? Kept.scope("bought", () -> chip("bought " + d(fx.getBoughtThisMonth()) + " this month", Palette.MONEY)) : null,
                    fx.getSoldThisMonth() > 0 ? Kept.scope("sold", () -> chip("sold " + d(fx.getSoldThisMonth()) + " this month", Palette.MONEY)) : null)));
        }
        // THE MONEY AND THE MIX (WN4): a money that has lost its rate is no longer the one dealt in.
        int money = ForeignAccounts.moneyIndex(tradeMoney);
        if (money >= 0 && !fx.hasRate(money)) {
            tradeMoney = null;
            money = -1;
        }
        c.getChildren().add(moneyPicker(fx));
        VBox mixBox = Kept.scope("mix", () -> reserveMix(fx));
        if (!mixBox.getChildren().isEmpty()) c.getChildren().add(mixBox);
        double ceiling = tradeBuying ? Math.max(0, ui.game.getCash())
                : money < 0 ? fx.sellableReserves() : fx.sellableReserves(tradeMoney);
        tradeExchange = Math.max(0, Math.min(tradeExchange, ceiling));
        final int moneyAt = money;
        c.getChildren().add(cardLine(tradeBuying ? "the treasury holds"
                : money < 0 ? "the vault holds" : "the vault holds in " + ForeignAccounts.MONEY_PLURAL[money], dFull(ceiling), null));
        if (ceiling <= 0) {
            c.getChildren().add(Kept.scope("nothing to deal", () -> line(tradeBuying ? "There is no cash to buy with." : moneyAt < 0 ? "There is nothing in the vault to sell."
                            : "The vault holds no " + ForeignAccounts.MONEY_PLURAL[moneyAt] + " to sell.",
                    tradeBuying
                            ? "The treasury is empty. Buying reserves is spending money the city does not have."
                            : "You cannot sell what you do not hold. A city that needs foreign money and has none has to "
                              + "earn it, borrow it abroad, or let the currency do the work.")));
            return c;
        }

        // Kept (KP6): the ask is a scope of its own, since the card has it (and the steps that follow it) only while there is something to deal.
        final double reach = ceiling;
        VBox askBox = Kept.scope("ask", () -> {
            Label asking = words(tradeBuying ? "HOW MUCH TO BUY" : "HOW MUCH TO SELL", Palette.SIZE_LABEL, Palette.TEXT_LABEL);
            Label ask = figure(tradeExchange <= 0 ? "nothing yet" : dFull(tradeExchange), 20,
                    tradeExchange > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT);
            FlowPane pick = Kept.flow(6, 6);
            for (double share : new double[] {.05, .25, .5}) {
                double step = reach * share;
                if (step <= 0) continue;
                pick.getChildren().add(stepChip("+" + d(step), () -> {
                    tradeExchange = Math.min(reach, tradeExchange + step);
                    showForeignMenu();
                }, false));
            }
            for (double[] to : exchangeSteps(reach)) {
                pick.getChildren().add(stepChip(String.format("to %.0f months (%s)", to[0], d(to[1])), () -> {
                    tradeExchange = Math.min(reach, to[1]);
                    showForeignMenu();
                }, false));
            }
            pick.getChildren().add(stepChip("all of it", () -> {
                tradeExchange = reach;
                showForeignMenu();
            }, false));
            pick.getChildren().add(stepChip("clear", () -> {
                tradeExchange = 0;
                showForeignMenu();
            }, true));
            return Kept.column(4, asking, ask, pick);
        });

        ActionButton button = actionButton(Icons.EXCHANGE, Palette.MONEY, ACTION_TALL, exchangePress(), null);
        button.onPress(() -> {
            if (tradeExchange <= 0) {
                List<double[]> to = exchangeSteps(ceiling);
                tradeExchange = Math.min(ceiling, to.isEmpty() ? ceiling * .05 : Math.min(ceiling * .05, to.get(0)[1]));
                showForeignMenu();
                return;
            }
            if (tradeBuying) ui.game.buyForeignCurrency(tradeExchange, tradeMoney);
            else             ui.game.sellForeignCurrency(tradeExchange, tradeMoney);
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
        // Kept (KP6): what it would do is a scope of its own, since the card has it only once something is asked.
        VBox after = Kept.scope("would do", () -> {
            VBox box = Kept.column(6, words("WHAT IT WOULD DO", Palette.SIZE_LABEL, Palette.TEXT_LABEL),
                    cardLine("in the vault", d(fx.getReserves()) + "  →  " + d(fx.getReserves() + sign * tradeExchange), null),
                    cardLine("in the treasury", d(ui.game.getCash()) + "  →  " + d(ui.game.getCash() - sign * tradeExchange), null),
                    cardLine("import cover", imports ? coverMonths(coverNow) + "  →  " + coverMonths(coverAfter) : "nothing to cover",
                            imports ? coverTone(ForeignAccounts.coverLevel(coverAfter)) : Palette.TEXT_SPENT),
                    cardLine("in " + Currency.FOREIGN_CODE, usdFull(fx.toUsd(tradeExchange)) + " at " + perUsd(fx.getRate()), null));
            if (moneyAt >= 0 && fx.getMoneyUsd(moneyAt) > 0) {
                // A named money: its own balance, in its own units, before and after.
                double ownChange = fx.toUsd(tradeExchange) / fx.getMoneyUsd(moneyAt);
                box.getChildren().add(2, cardLine("of which " + ForeignAccounts.MONEY_PLURAL[moneyAt], ownMoney(moneyAt, fx.getHolding(moneyAt)) + "  →  "
                        + ownMoney(moneyAt, Math.max(0, fx.getHolding(moneyAt) + sign * ownChange)), null));
                // Gold (GD2): the ounces above are the deal at the month's price, which is the line under them.
                if (moneyAt == ForeignAccounts.GOLD) {
                    box.getChildren().add(3, cardLine("gold's price this month", goldPrice(fx.getMoneyUsd(ForeignAccounts.GOLD), "ounce"), null));
                }
            }
            if (imports) {
                box.getChildren().add(Kept.scope("cover bar", () -> bulletBar(Math.min(COVER_SCALE, coverAfter), ForeignAccounts.THIN_COVER, Palette.MONEY, 0,
                        String.format("the cover after it, against the %.0f-month line", ForeignAccounts.THIN_COVER))));
            }
            box.getChildren().add(noteLine("The cost of a defence is not the cash.", "The cost of a defence is not the cash. "
                    + "It is the cover you no longer have — which is why the two figures are shown before and after.", 560));
            return box;
        });
        GridPane two = equalColumns(2, 24);
        two.add(askBox, 0, 0);
        two.add(after, 1, 0);
        c.getChildren().add(two);
        if (!tradeBuying && imports && coverAfter < ForeignAccounts.THIN_COVER && coverNow >= ForeignAccounts.THIN_COVER) {
            c.getChildren().add(Kept.scope("under the line", () -> actionCard(Icons.ALERT, Palette.BAD,
                    String.format("This would take the city under %.0f months of cover", ForeignAccounts.THIN_COVER),
                    "Below the line the world prices the currency for a crisis.",
                    String.format("Below %.0f months the world stops pricing the currency on the trade balance and starts "
                            + "pricing it for a crisis. Selling reserves to defend a rate, and ending up under the line doing "
                            + "it, is the sequence every currency crisis in the literature has in common.",
                            ForeignAccounts.THIN_COVER), null)));
        }
        // XI1/XI2: the month's dealing past INTERVENTION_LINEAR of a year's output moves the rate by less for each dollar (pastBound() has the words).
        PastBound past = pastBound();
        if (past != null) {
            c.getChildren().add(Kept.scope("past the bound", () -> actionCard(Icons.ALERT, Palette.WARN, past.title(), past.line(), past.whole(), null)));
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
            Kept.scope("walked away", () -> {
                column.getChildren().add(statementHead("...and what was walked away from"));
                column.getChildren().add(statementLine("Repudiated", moneyFull(fx.getRepudiated()), Palette.BAD));
            });
        }
        return column;
    }
}
