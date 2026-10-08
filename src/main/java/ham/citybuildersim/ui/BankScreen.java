package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import ham.citybuildersim.ui.Pieces.Rung;
import ham.citybuildersim.ui.Pieces.Segment;
import ham.citybuildersim.ui.Pieces.Slice;
import ham.citybuildersim.ui.Pieces.Step;
import ham.citybuildersim.ui.Pieces.Tick;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * The bank tab: whether the city's bank is healthy and what it charges, on
 * an Overview - its state in a sentence, its capital in its band, eight
 * figures and the ladder of its rates drawn in their parts - with its profit,
 * its balance sheet, its lending, its funding, its capital and owners, and
 * its history behind it, a picture a page.
 *
 * WHY THIS SHAPE (0.7.9). Jerus: "a redesign of the bank UI info, cause when
 * you click on bank you dont even see all the relevant stuff, lets make
 * banks realistic." The tab it replaced was split out of UserInterface on
 * 2026-09-18 and still opened on the strain premium's questions - a gauge,
 * the two limits, another branch - after 0.7.7 took the premium away. A
 * player could not find the bank's rates side by side, its return on its
 * capital, its capital against a target, its losses as a rate, what it did
 * with its profit, its account at the central bank, or its owners in one
 * place; and a dozen of the figures it did print were worked out on the
 * screen, several of them wrong (the project's the-bank-tab.md has the
 * list). Every figure is a model getter now - Bank's WHAT THE BANK TAB
 * READS has the ones this tab asked for.
 *
 * ...AND WHY IT LOOKS LIKE THIS (0.7.33). It was still a 560 px statement
 * column in a 1,270 px centre: a landing of a sentence, two rows of four
 * figures and the ladder as bars with a paragraph under every rung, then six
 * pages of statement lines and some fifty notes. Jerus, on the screens not
 * yet redone: "the others are still full of text and the design could be
 * more intuitive and fun". Redrawn in Build's style (the project's
 * spec-bank-0733.md): the Overview leads with the capital gauge beside the
 * scorecard and the ladder in its parts - prime as its four, each borrower as
 * prime and its own risk, record and concentration (BusinessDebtManager
 * .quoteParts()) - and every page with the one picture that answers its
 * question: a waterfall from interest to what it kept, the balance sheet as
 * two bars on one scale, the book by borrower and a card a borrower, the
 * deposits and what funds the book, both capital ratios on their bands, and
 * the rates over the city's life with the decisions as flags. Every
 * paragraph is behind an (i); every statement and table the pages printed is
 * behind "details", verbatim. One verdict each: the stance (Bank
 * .payoutStance()) in the banner, the strip and the capital gauge; losses
 * past Bank.LOSS_WATCH; HOW FULL as NEEDS YOU's THE BANK row; defaults,
 * shut-outs and the window when it is used; the residuals. The colours of the
 * series are the areas' (businesses violet, families teal, landlords pink,
 * the bank's own blue), never a verdict's.
 *
 * ONE WAY TO WRITE EACH KIND OF NUMBER: a rate as "x.xx% a year"
 * (Money.ratePerYear(), three places under a tenth of a per cent), a spread
 * between two rates in points to two decimals (Money.points(), "pts" on a
 * chip), a share or a ratio as "x.x%" (Money.share1()), money through Money
 * - a flow says "this month", a stock does not - and a true minus wherever
 * the tab writes a figure itself. The statements under "details" and
 * History's money charts (City History's formatter) still write a negative
 * as Money.tightMoney() does, with a hyphen (the spec's B16, left for a
 * batch of its own).
 *
 * The shell reads which page is open (bankArea, bankPage) for the rail and
 * the scroll memory; the panel is rebuilt on the clock, so the page, the
 * scroll position (UserInterface.scrolled()) and what the player has opened -
 * a borrower's line, a fold, the waterfall's year (openLines, profitYear) -
 * all survive a redraw.
 */
final class BankScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    BankScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       THE BANK (0.7.33): THE FRAME

       The head ("Bank", or "Bank › Profit" with "Bank" a way back to the
       Overview) and its two doors - the policy rate every rate here is built
       on, and the treasury's setting for when the bank fails; the pages as
       chips with icons, Overview first; on a page, the bank's state and four
       figures, so a page never loses the state of the whole bank. Only the
       page scrolls under them (GovernmentScreen's frame, 0.7.31).

       THE ACTION STAYS AS IT WAS. Jerus: rescue on failure only. Putting
       capital in whenever you like is a lever the city does not have, and
       adding one is a game change rather than a screen change. Since 0.7.14
       the rescue is a resolution for the bank's shares (bankRescue()), and
       a standing bank under its minimum asks the city to buy preferred
       (preferredOffer()) - both at the top of every page while they wait,
       as cards, with the other states a player must act on or know of first
       (no bank, under the minimum, money from abroad on Funding, a sheet
       that does not foot).
       ===================================================================== */

    String bankArea = null;                  // null is the Overview; BANK_PAGES, the pages behind it
    static final String BANK_PAGES = "The bank's pages";
    /** The page behind the Overview that is lit until the player picks another; the rail's bank icon resets to it. */
    static final String BANK_HOME  = "Profit";
    String bankPage = BANK_HOME;

    /** The six pages behind the Overview, in the chip strip's order: the balance sheet beside the income statement since 0.7.13. */
    static final String[] BANK_PAGE_NAMES =
            {"Profit", "Balance sheet", "Lending", "Funding", "Capital & owners", "History"};

    /** The strip (0.7.33): the Overview first - it was the landing, with a button back to it at the foot of every page - then the six. */
    static final String OVERVIEW = "Overview";
    /** ...the strip's seven chips: the Overview, then BANK_PAGE_NAMES in their order. */
    static final String[] CHIPS =
            {OVERVIEW, "Profit", "Balance sheet", "Lending", "Funding", "Capital & owners", "History"};
    /** ...and each chip's icon. */
    static final String[] CHIP_ICONS =
            {Icons.OVERVIEW, Icons.COIN, Icons.FINANCES, Icons.SECTOR, Icons.BANK, Icons.STAFF, Icons.REPORTS};

    /** What the player has opened - a statement's line, a borrower's sectors, a "details" fold - by key, so a redraw on the clock leaves it open (Statement.opens()). */
    final Set<String> openLines = new HashSet<>();

    /** The Profit page's waterfall: the last twelve months rather than this month (0.7.33), kept while the tab is. */
    boolean profitYear = false;

    /** Where the page is to be scrolled to once it is drawn - a card's key - or null for its top. */
    private String scrollTarget;

    /** The nodes a door on this tab can scroll to, by key, as the page draws them. */
    private final java.util.Map<String, Node> targets = new java.util.HashMap<>();

    /** The page's scroller. */
    private javafx.scene.control.ScrollPane body;

    /** History's window, shared by its charts and kept across the month's redraw, for a chart dragged back to stay where it was put - though no chart here takes a drag (a small TimeChart does not pan; 0.7.38 took "Drag to look back" out of ITS RATES' (i)), so it stays on its range: the last ten years, or the whole history while it is younger. */
    private final ChartModel historyWindow = new ChartModel();

    /** The month the tab last drew: a new month landing is when its figures pop and its gauge grows. */
    private int drawnMonth = Integer.MIN_VALUE;

    /** How much of the stage the fixed frame takes above the page's scroller - the head, the chips and, on a page, the status strip - until the frame is laid out and its own height is read (0.7.33). */
    static final double FRAME_CHROME = 190;

    /** What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - GovernmentScreen's, for the same frame. */
    static final double STAGE_REST = 36;

    /** The scroll targets a door can land on: Lending's cards and its pricing, Funding's carry card, the details folds. */
    static final String SET_ASIDE = "#setAside", PRICING = "#pricing", LANDLORDS = "#landlords",
            FAMILIES = "#families", CARRY = "#carry", CAN_CARRY = "#canCarry", DETAILS = "#details",
            RESCUES = "#rescues";

    /* ------------------------ one way to write each number ------------------------ */

    /** A default rate, which runs from the curve's far tail to all of it: "under 0.1%" rather than a "0.0%" that reads as none, "x.x%" to 99.9%, then "all". */
    static String defaultShare(double pd) {
        if (!(pd > 0)) return "none";
        if (pd < .001) return "under 0.1%";
        if (pd > .999) return "all";
        return share1(pd);
    }

    /** "the last 12 months", or as many as the bank has lived. */
    static String yearWords(Bank bank) {
        int n = bank.monthsInYear();
        return n <= 1 ? "this month" : "the last " + n + " months";
    }

    /** A spread for a chip: "+2.06 pts", "−0.22 pts" - Money.points() shortened. */
    static String pts(double spread) { return points(spread).replace(" points", " pts"); }

    /** Money signed for a movement, with a true minus: "+$851,963", "−$81,238", "$0" (Money.signed(), never negated). */
    static String mv(double thousands) { return signed(thousands, false); }

    /** Money with a true minus when below nothing, abbreviated: "−$2.1M". */
    static String m(double thousands) {
        String shown = money(Math.abs(thousands));
        return (thousands < 0 && !"$0".equals(shown) ? "−" : "") + shown;
    }

    /** ...with every digit. */
    static String mFull(double thousands) {
        String shown = moneyFull(Math.abs(thousands));
        return (thousands < 0 && !"$0".equals(shown) ? "−" : "") + shown;
    }

    /* ------------------------------ the frame ------------------------------ */

    /**
     * The tab's entry point: the Overview, or the page behind it the player
     * was on. Named for the shell, which calls it from the rail, the inbox
     * and the summary panel.
     */
    void showBankMenu() {
        ui.clearMenu("showBankMenu", () -> showBankMenu());

        boolean known = false;
        for (String p : BANK_PAGE_NAMES) known |= p.equals(bankPage);
        if (!known) bankPage = BANK_HOME;
        targets.clear();

        boolean fresh = ui.game.getMonth() != drawnMonth;
        drawnMonth = ui.game.getMonth();

        VBox page = widePage();
        page.getChildren().addAll(actionCards());
        if (bankArea == null) {
            overviewPage(page, fresh);
        } else {
            boolean formal = ui.sectorScreen.statementView;
            switch (bankPage) {
                case "Balance sheet"    -> { if (formal) sheetStatementView(page); else sheetPage(page); }
                case "Lending"          -> lendingPage(page);
                case "Funding"          -> fundingPage(page);
                case "Capital & owners" -> capitalPage(page);
                case "History"          -> historyPage(page);
                default                 -> { if (formal) profitStatementView(page); else profitPage(page); }
            }
        }

        // The pages, and on Profit and Balance sheet the Summary | Statement switch the sector pages share (0.7.74, D9).
        boolean switched = bankArea != null && ("Profit".equals(bankPage) || "Balance sheet".equals(bankPage));
        VBox frame = new VBox(Palette.GAP, head(), StatementView.stripWithSwitch(
                chipStrip(CHIPS, CHIP_ICONS, bankArea == null ? OVERVIEW : bankPage, Palette.SIZE_LABEL, this::pick),
                switched ? ui.sectorScreen.statementView : null, this::pickView));
        if (bankArea != null) frame.getChildren().add(statusStrip(fresh));
        frameOver(frame, page);

        if (scrollTarget != null) {
            String target = scrollTarget;
            scrollTarget = null;
            javafx.application.Platform.runLater(() -> {
                Node n = targets.get(target);
                if (n == null || body == null) return;
                body.applyCss();
                body.layout();
                scrollTo(n);
            });
        }
    }

    /** The switch picked (0.7.74): this page in the other view, at its top - one choice with the sector pages' (D1). */
    void pickView(boolean statement) {
        ui.sectorScreen.statementView = statement;
        ui.innerScrollAt.remove("showBankMenu:body");
        showBankMenu();
    }

    /* ---------------------- its statements, formal (0.7.74) ----------------------

       The Statement view of Profit and Balance sheet (the project's
       spec-sector-statements.md, 4.5, D9): the bank's income statement in a
       bank's order - interest income by who paid it, less interest expense,
       NET INTEREST INCOME; fees, the desk and its gains, TOTAL OPERATING
       INCOME; provisions; staff and branches; PROFIT BEFORE TAX; the tax a
       month in arrears; PROFIT; what its owners took; KEPT IN THE BANK - and
       its sheet most liquid first, its loans gross less the allowance, its
       equity in its parts and the deposits it holds as a memorandum.
       SectorStatements builds both from the bank's own lines, and
       SectorStatementCheck holds every total to them. */

    /** The bank's income statement, formal, its ratios beside it. */
    void profitStatementView(VBox page) {
        Bank bank = ui.game.getBank();
        ham.citybuildersim.SectorStatements.Table t = ham.citybuildersim.SectorStatements.bankIncome(bank);
        SectorScreen sectors = ui.sectorScreen;
        boolean millions = ham.citybuildersim.SectorStatements.inMillions(t);
        StatementView.Columns c = sectors.columns("of income");
        VBox table = StatementView.table(t, "bank:income", "this month", "last month", c,
                bank.thisMonth(Bank.Line.REVENUE), bank.lastMonth(Bank.Line.REVENUE), openLines, null,
                id -> ham.citybuildersim.SectorStatements.B_PROVISIONS.equals(id) ? PROVISIONS_INFO
                        : ham.citybuildersim.SectorStatements.B_TAX.equals(id) ? String.format("Tax is paid a month in "
                        + "arrears, on last month's %s of profit: the city's take is struck before the bank knows what "
                        + "it made.", m(bank.getProfitLastMonth())) : null, ui::redraw);
        VBox card = StatementView.card(StatementView.titleBlock("The bank", t.title(), "For the month of "
                        + CityCalendar.format(ui.game.getMonth()), millions),
                StatementView.toolbar(c, () -> { sectors.showChange = !sectors.showChange; ui.redraw(); },
                        () -> { sectors.showShare = !sectors.showShare; ui.redraw(); }, null, null), table);
        page.getChildren().add(StatementView.beside(card, bankRatios(bank, false)));
    }

    /** The bank's balance sheet, formal, against a year ago, its ratios beside it. */
    void sheetStatementView(VBox page) {
        Bank bank = ui.game.getBank();
        ham.citybuildersim.SectorStatements.Table t = ham.citybuildersim.SectorStatements.bankSheet(bank);
        SectorScreen sectors = ui.sectorScreen;
        boolean millions = ham.citybuildersim.SectorStatements.inMillions(t);
        StatementView.Columns c = sectors.columns("of assets");
        VBox table = StatementView.table(t, "bank:sheet", "this month", "a year ago", c,
                bank.sheet(Bank.Sheet.ASSETS), bank.yearAgo(Bank.Sheet.ASSETS), openLines, null,
                id -> id.startsWith("bs.") && id.length() > 3 && sheetLineOf(id) != null ? sheetSaid(bank, sheetLineOf(id))
                        : null, ui::redraw);
        VBox card = StatementView.card(StatementView.titleBlock("The bank", t.title(), "As "
                        + CityCalendar.format(ui.game.getMonth()) + " closed", millions),
                StatementView.toolbar(c, () -> { sectors.showChange = !sectors.showChange; ui.redraw(); },
                        () -> { sectors.showShare = !sectors.showShare; ui.redraw(); }, null, null), table);
        page.getChildren().add(StatementView.beside(card, bankRatios(bank, true)));
        if (!bank.knowsYearAgo()) {
            page.getChildren().add(SectorScreen.caption("A year ago is kept from the first month played in this build, so it reads — "
                    + "until the city has lived a year of them.", Palette.TEXT_MUTED));
        }
    }

    /** The sheet line a statement row is, by its id ("bs.RESERVES"), or null for a subtotal. */
    static Bank.Sheet sheetLineOf(String id) {
        for (Bank.Sheet s : Bank.Sheet.values()) if (id.equals(ham.citybuildersim.SectorStatements.sheetId(s))) return s;
        return null;
    }

    /**
     * Its ratios (spec 4.5), on the income statement: net interest margin,
     * cost to income, the fees' share, provisions over what it has lent,
     * return on equity and its payout; on the sheet: its capital over its
     * weighted book, equity over assets, loans over the deposits it holds,
     * the liquid share, the allowance over its loans and the window's share.
     */
    VBox bankRatios(Bank bank, boolean sheet) {
        double income = bank.thisMonth(Bank.Line.REVENUE);
        double loans = ham.citybuildersim.SectorStatements.bankGrossLoans(bank);
        double assets = bank.sheet(Bank.Sheet.ASSETS);
        List<Node> rows = new ArrayList<>();
        if (!sheet) {
            double nim = bank.netInterestMargin();
            rows.add(SectorScreen.ratioRow("Net interest margin", ratePerYear(nim), nim < 0 ? Palette.BAD : Palette.TEXT_LABEL,
                    "What it charges less what it pays for its money, on everything it has lent, a year at this month's."));
            double costs = income > 0 ? bank.thisMonth(Bank.Line.COSTS) / income : Double.NaN;
            rows.add(SectorScreen.ratioRow("Cost to income", Double.isFinite(costs) ? share1(costs) : "—",
                    !Double.isFinite(costs) || costs > 1 ? Palette.BAD : Palette.TEXT_LABEL,
                    "Its staff and branches over its total operating income."));
            double fees = income > 0 ? bank.thisMonth(Bank.Line.FEES) / income : Double.NaN;
            rows.add(SectorScreen.ratioRow("Fees, of its income", Double.isFinite(fees) ? share1(fees) : "—", Palette.TEXT_LABEL, null));
            rows.add(SectorScreen.ratioRow("Provisions, of its loans", loans > 0
                    ? ratePerYear(bank.thisMonth(Bank.Line.PROVISIONS) * 12 / loans) : "—", Palette.TEXT_LABEL,
                    PROVISIONS_INFO + " A year at this month's, over its loans and advances."));
            double roe = bank.returnOnEquity();
            rows.add(SectorScreen.ratioRow("Return on equity", ratePerYear(roe),
                    roe < 0 ? Palette.BAD : roe < Bank.requiredReturn() ? Palette.WARN : Palette.GOOD,
                    "This month's profit over the equity it opened with, a year; its owners want "
                    + ratePerYear(Bank.requiredReturn()) + "."));
            double net = bank.thisMonth(Bank.Line.NET);
            rows.add(SectorScreen.ratioRow("Payout", net > 0 ? share1((bank.thisMonth(Bank.Line.DIVIDENDS)
                    + bank.thisMonth(Bank.Line.BUYBACKS)) / net) : "—", Palette.TEXT_LABEL,
                    "What its owners took this month - paid on last month's profit - over this month's."));
        } else {
            double capital = bank.capitalRatio();
            rows.add(SectorScreen.ratioRow("Capital, of its weighted book", capital >= Double.MAX_VALUE ? "nothing lent"
                    : share1(capital), capital < bank.capitalTarget() ? Palette.WARN : Palette.GOOD,
                    String.format("Its equity over its risk-weighted book; it aims for %s.", share1(bank.capitalTarget()))));
            rows.add(SectorScreen.ratioRow("Equity, of its assets", assets > 0 ? share1(bank.sheet(Bank.Sheet.EQUITY) / assets) : "—",
                    Palette.TEXT_LABEL, null));
            double deposits = bank.sheet(Bank.Sheet.HOUSEHOLD_DEPOSITS) + bank.sheet(Bank.Sheet.SECTOR_DEPOSITS);
            rows.add(SectorScreen.ratioRow("Loans, of the deposits it holds", deposits > 0 ? share1(loans / deposits) : "—",
                    Palette.TEXT_LABEL, "Its loans and advances over the families' and the businesses' deposits."));
            rows.add(SectorScreen.ratioRow("Liquid, of its assets", assets > 0 ? share1((bank.sheet(Bank.Sheet.RESERVES)
                    + bank.sheet(Bank.Sheet.CITY_PAPER)) / assets) : "—", Palette.TEXT_LABEL,
                    "Its reserves at the central bank and the city's paper, over everything it owns."));
            rows.add(SectorScreen.ratioRow("Allowance, of its loans", loans > 0 ? share1(-bank.sheet(Bank.Sheet.ALLOWANCE) / loans) : "—",
                    Palette.TEXT_LABEL, null));
            double liabilities = bank.sheet(Bank.Sheet.LIABILITIES);
            rows.add(SectorScreen.ratioRow("Overnight, of what it owes", liabilities > 0 ? share1(bank.sheet(Bank.Sheet.WINDOW) / liabilities) : "—",
                    Palette.TEXT_LABEL, "What it borrowed at the central bank's window over its liabilities."));
        }
        return SectorScreen.ratioColumn("READ AS RATIOS", rows.toArray(new Node[0]));
    }

    /** A chip picked: the Overview, or a page at its top. */
    void pick(String name) {
        if (OVERVIEW.equals(name)) openOverview();
        else openPage(name);
    }

    /** Opens one page behind the Overview, at its top - the Overview's cards, and the inbox's "defaults" notice (0.7.8). */
    void openPage(String page) {
        open(page, null);
    }

    /** ...scrolled to a card on it (0.7.33: the doors land where the line is decided). */
    void open(String page, String target) {
        bankArea = BANK_PAGES;
        bankPage = page;
        scrollTarget = target;
        ui.innerScrollAt.remove("showBankMenu:body");
        showBankMenu();
    }

    /** The Overview, at its top. */
    void openOverview() {
        bankArea = null;
        ui.innerScrollAt.remove("showBankMenu:body");
        showBankMenu();
    }

    /** The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (FinancesScreen's, 0.7.32). */
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

    /** The tab's (i): the Overview's lead (the spec's L2), with what the Overview shows. */
    static final String LEAD_INFO = "Every loan in the city is its money, priced from what it costs the bank to make. "
            + "The Overview says whether it is healthy - its capital against what it must hold and what it aims for, "
            + "and eight figures a banker reads first - and what it charges: the ladder of its rates, from the price "
            + "of money to what each borrower pays, every rung in the parts it is built from. The six pages behind it "
            + "take each figure apart.";

    /** Each page's (i), in BANK_PAGE_NAMES' order: what it holds (the landing's row blurbs until 0.7.33). */
    static final String[] PAGE_INFO = {
        "Its income statement, and what it did with the profit: a walk from the interest it earned to what it kept, "
                + "this month or over the last twelve. The statement itself is under \"details\".",
        "What it owns, what it owes and what is its owners', against a year ago: two bars on one scale, every line "
                + "of the sheet in the key under them. The statement itself is under \"details\".",
        "Who owes it, how sound each borrower is, what it has set aside, and how the next loan is priced. The old "
                + "tables are under \"details\".",
        "What the city has banked with it and what funds its book, what it pays savers and the central bank, what "
                + "it can carry, and its branches.",
        "Its capital against what it must hold and what it aims for, on both measures; what it does with its profit; "
                + "how its equity moved; who owns it; its rescues and the city's preferred.",
        "Its rates, its capital, its returns, its losses, how full it is and its fees, over the city's life.",
    };

    /** A page's (i) by its name. */
    static String pageInfo(String page) {
        for (int i = 0; i < BANK_PAGE_NAMES.length; i++) if (BANK_PAGE_NAMES[i].equals(page)) return PAGE_INFO[i];
        return LEAD_INFO;
    }

    /**
     * The head: "Bank" with the money blue's swatch, or the breadcrumb "Bank ›
     * Profit", the first word a way back to the Overview; at its right the two
     * doors - the policy rate (Policy › Money › The policy rate) and the
     * treasury's setting for a failed bank (Finances, WHEN THE BANK FAILS).
     */
    HBox head() {
        Label policy = door("The policy rate", Palette.ACCENT, () -> openPolicy("Money", "The policy rate"));
        Label fails = door("When it fails", Palette.ACCENT,
                () -> ui.financesScreen.open(null, null, FinancesScreen.RESCUE));
        if (bankArea == null) return pageHead("Bank", Palette.MONEY, null, null, LEAD_INFO, policy, fails);
        return pageHead("Bank", Palette.MONEY, this::openOverview, bankPage, pageInfo(bankPage), policy, fails);
    }

    /** Policy, on one of its areas and pages, at its top (FinancesScreen.openPolicy()'s). */
    void openPolicy(String area, String page) {
        ui.policyScreen.policyArea = area;
        ui.policyScreen.policyPage = page;
        ui.policyScreen.dropProposal();
        ui.innerScrollAt.remove("showPolicyMenu:body");
        ui.policyScreen.showPolicyMenu();
    }

    /** One sector's books, on one of its pages (Sectors › it › Cash & debt). */
    void openSector(String key, String page) {
        Sector s = ui.game.getSectors().byKey(key);
        if (s != null) ui.sectorScreen.openSectorBooks(s, page);
    }

    /* ------------------------------ the stance ------------------------------ */

    /** Good at or over its target, a warning while it rebuilds, bad under the minimum or failed. */
    static String stanceTone(Bank bank) {
        return switch (bank.payoutStance()) {
            case PAYING, RETURNING -> Palette.GOOD;
            case REBUILDING        -> Palette.WARN;
            case UNDER_MINIMUM, FAILED -> Palette.BAD;
            case NO_BANK           -> Palette.TEXT_MUTED;
        };
    }

    /** The stance in a word or three, on its chip (the spec's D18). */
    static String stanceWord(Bank bank) {
        return switch (bank.payoutStance()) {
            case PAYING, RETURNING -> "HEALTHY";
            case REBUILDING        -> "REBUILDING";
            case UNDER_MINIMUM     -> "UNDER ITS MINIMUM";
            case FAILED            -> "FAILED";
            case NO_BANK           -> "NO BANK";
        };
    }

    /**
     * THE STATUS STRIP, on every page behind the Overview: the stance as a
     * chip and the four figures across the top of every page until 0.7.33 -
     * profit, the binding capital ratio, prime with its move on last month,
     * and what is lent out - each a door to where it is taken apart.
     */
    HBox statusStrip(boolean fresh) {
        Bank bank = ui.game.getBank();
        List<Cell> four = stripCells();
        Runnable[] go = {
                () -> openPage("Profit"),
                () -> openPage("Capital & owners"),
                () -> open("Lending", PRICING),
                () -> openPage("Lending")};
        VBox[] cells = new VBox[four.size()];
        for (int i = 0; i < cells.length; i++) {
            Cell c = four.get(i);
            cells[i] = limitCell(c.label(), c.value(), c.note(), c.tone(), c.where(), go[i]);
        }
        String moved = primeChange();
        if (moved != null) {
            Label l = new Label(moved);
            l.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            cells[2].getChildren().add(l);
        }
        if (fresh && cells[0].getChildren().size() > 1 && cells[0].getChildren().get(1) instanceof Region r) {
            UserInterface.popPip(r);
        }
        Label stance = chip(stanceWord(bank), stanceTone(bank));
        HBox strip = new HBox(Palette.GAP_LOOSE, stance, vitalsBar(cells));
        strip.setAlignment(Pos.CENTER_LEFT);
        return strip;
    }

    /** One figure of a strip or the scorecard, worked out without drawing it: its label, its figure, its note, its colour, and where its click goes. */
    record Cell(String label, String value, String note, String tone, String where) { }

    /** The status strip's four (pure: the probe reads them as the strip shows them). */
    List<Cell> stripCells() {
        Bank bank = ui.game.getBank();
        double dial = ui.game.getDebtManager().getPolicyRate();
        boolean onLeverage = bank.leverageBinds();
        return List.of(
                new Cell("PROFIT", m(bank.getNetIncome()), "this month",
                        bank.getNetIncome() < 0 ? Palette.BAD : Palette.GOOD,
                        "Profit: from what it earned to what it kept"),
                new Cell(onLeverage ? "LEVERAGE RATIO" : "CAPITAL RATIO", capitalFigure(bank),
                        "its target " + share1(bank.bindingTarget()), stanceTone(bank),
                        "Capital & owners: both ratios on their bands"),
                new Cell("PRIME", ratePct(bank.prime(dial)), "what a sound business pays a year", Palette.TEXT_HEAD,
                        "Lending: how the next loan is priced"),
                new Cell("LENT OUT", m(bank.getBook()), "loans at face, bonds at cost", Palette.TEXT_HEAD,
                        "Lending: who owes it"));
    }

    /** The binding ratio as the gauge and the strip write it: "failed", "—" with nothing lent, else "14.1%". */
    static String capitalFigure(Bank bank) {
        if (bank.isInsolvent()) return "failed";
        boolean lent = bank.leverageBinds() || bank.getWeightedBook() > 0;
        return lent ? share1(bank.bindingRatio()) : "—";
    }

    /** PRIME's move on last month, from History's last two points: "▲ 0.25 pts on last month"; null with fewer than two or under half a hundredth of a point (the spec's section 5). */
    String primeChange() {
        double[] s = ui.game.getHistorySave().aligned("bankPrime");
        double last = Double.NaN, before = Double.NaN;
        for (int i = s.length - 1; i >= 0; i--) {
            if (!Double.isFinite(s[i])) continue;
            if (Double.isNaN(last)) last = s[i];
            else { before = s[i]; break; }
        }
        if (!Double.isFinite(last) || !Double.isFinite(before)) return null;
        double points = (last - before) * 100;
        if (Math.abs(points) < .005) return null;
        return (points > 0 ? "▲ " : "▼ ") + String.format("%.2f pts on last month", Math.abs(points));
    }

    /* ------------------------------ the action cards ------------------------------ */

    /**
     * What the player must act on or know of first, at the top of every page:
     * no bank; a failed bank and its rescue; a bank under its minimum; the
     * bank asking the city for preferred; money from abroad (on Funding); a
     * sheet that does not foot. Each a card on the raised ground with a 3 px
     * edge at its top in its verdict's colour (the spec's section 3).
     */
    List<Node> actionCards() {
        Bank bank = ui.game.getBank();
        List<Node> out = new ArrayList<>();
        if (bank.getBranches() <= 0) {
            HBox build = doorPill("Build a Commercial Bank", Icons.BUILD, Palette.BUILDING,
                    () -> ui.buildScreen.openCategory(BuildAdvice.SHOPS));
            out.add(actionCard(Icons.BANK, Palette.BAD, "There is no bank in this city",
                    "Every borrower is lent money from the central bank, priced as a bank would price it.",
                    "Every borrower is lent money from the central bank, priced as a bank would price it. Build a "
                            + "Commercial Bank: one branch is its owners' capital as well as a building, and its savers' "
                            + "deposits start funding the city's loans.", build));
        }
        if (bank.isInsolvent()) out.add(bankRescue());
        if (bank.payoutStance() == Bank.Payout.UNDER_MINIMUM) {
            out.add(actionCard(Icons.ALERT, Palette.BAD, "It is under the minimum",
                    String.format("%s would take it back to its own target of %s.", m(bank.recapitalisationNeeded()),
                            share1(bank.bindingTarget())),
                    String.format("Under the %s the city requires, it lends only what keeps its borrowers going until "
                            + "it is back over it - by earning it or by being given it. %s would take it back to its own "
                            + "target of %s.", share1(bank.bindingMinimum())
                            + (bank.leverageBinds() ? " of everything it has lent" : ""),
                            m(bank.recapitalisationNeeded()), share1(bank.bindingTarget())), null));
        }
        if (ui.game.isPreferredOfferPending()) out.add(preferredOffer());
        if (bankArea != null && "Funding".equals(bankPage) && bank.getForeignDeposits() > 0) {
            double hot = bank.hotFundingShare();
            String title = hot > .25 ? "A quarter of its funding can leave tomorrow" : "Some of its funding is foreign";
            out.add(actionCard(Icons.TRADE, Palette.WARN, title,
                    String.format("%s of what it lends against is money from abroad - %.0f%% of it.",
                            m(bank.getForeignDeposits()), hot * 100),
                    String.format("%s of what it lends against is money from abroad - %.0f%% of it - here because the "
                            + "rate is good. It leaves on no notice, and the bank must find the cash when it does. The "
                            + "Trade tab is where that is priced.", m(bank.getForeignDeposits()), hot * 100), null));
        }
        if (bank.knowsEquitySplit() && Math.abs(bank.equitySplitResidual()) > .005) {
            out.add(actionCard(Icons.ALERT, Palette.BAD, "Its two parts do not add up to its equity",
                    String.format("%s of its equity is neither paid in nor retained.", m(Math.abs(bank.equitySplitResidual()))),
                    String.format("%s of its equity is neither paid in nor retained. That should be impossible - worth "
                            + "reporting.", m(Math.abs(bank.equitySplitResidual()))), null));
        }
        double off = bank.sheetResidual(), offThen = bank.yearAgoResidual();
        if (Math.abs(off) > .005 || (bank.knowsYearAgo() && Math.abs(offThen) > .005)) {
            out.add(actionCard(Icons.ALERT, Palette.BAD, "Its lines do not add up",
                    String.format("Not accounted for: %s now, %s a year ago.", mFull(off), mFull(offThen)),
                    "Something on its books is not on this page, or is on it twice. That should be impossible - worth "
                            + "reporting.", null));
        }
        return out;
    }

    /** One action card: its icon square, its title, one line with the whole behind an (i), and what to press (null: nothing), on the raised ground with a 3 px top edge in `tone`. */
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
        c.setStyle(actionStyle(tone));
        return c;
    }

    /** An action card's ground: the raised ground, its top edge 3 px in the verdict's colour. */
    static String actionStyle(String tone) {
        return "-fx-padding: 12 14 12 14; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + tone + " "
                + Palette.EDGE + " " + Palette.EDGE + " " + Palette.EDGE + "; -fx-border-width: 3 1 1 1;";
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

    /** A card's head: its icon in a square tinted in its colour, its title in capitals, an (i) when `info` is not null, and at its right `right` (null: nothing) - LandScreen.worthCard()'s head, the spec's info card. */
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

    /** A plain line of words in a card, wrapping. */
    static Label line(String text) { return words(text, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL); }

    /** ...muted. */
    static Label muted(String text) { return words(text, Palette.SIZE_LABEL, Palette.TEXT_MUTED); }

    /** A heading's quiet words at its right, never cut (Pieces.hint() at its own width). */
    static Label quiet(String text) {
        Label l = hint(text);
        l.setMinWidth(Region.USE_PREF_SIZE);
        return l;
    }

    /** A big figure on a card, in a colour. */
    static Label big(String text, String tone) { return figure(text, 22, tone); }

    /** A statement column, for a fold: the old rows at their old width (the spec's D15). */
    static VBox column(Node... rows) {
        VBox c = new VBox(0);
        for (Node n : rows) if (n != null) c.getChildren().add(n);
        c.setMaxWidth(STATEMENT);
        return c;
    }

    /** A fold kept on this screen: "details ▸ caption". */
    VBox fold(String key, String caption, java.util.function.Supplier<Node> inside) {
        return details(key, caption, openLines, this::showBankMenu, inside);
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

    /** A key's entry: a swatch and a name, the swatch outlined when `ghost`. */
    static HBox swatch(String colour, String name, boolean ghost) {
        HBox s = keySwatch(colour, name);
        if (ghost && !s.getChildren().isEmpty() && s.getChildren().get(0) instanceof Region r) {
            r.setStyle("-fx-background-color: " + colour + "33; -fx-border-color: " + colour + "; -fx-border-width: 1;");
        }
        return s;
    }

    /** A row of a key: entries with a gap, wrapping. */
    static javafx.scene.layout.FlowPane keyRow(Node... entries) {
        javafx.scene.layout.FlowPane key = new javafx.scene.layout.FlowPane(14, 4);
        for (Node n : entries) if (n != null) key.getChildren().add(n);
        return key;
    }

    /** A stretch of a segment bar with its tooltip. */
    static Segment seg(double amount, String colour, String tip) {
        return new Segment(amount, colour, false, null, null, tip, null);
    }

    /** ...with a label in it, drawn where it fits. */
    static Segment seg(double amount, String colour, String label, String tip) {
        return new Segment(amount, colour, false, null, label, tip, null);
    }

    /** ...hollow: what a rate is built on, or a quote nobody pays. */
    static Segment ghost(double amount, String colour, String tip) {
        return new Segment(amount, colour, true, null, null, tip, null);
    }

    /** A named mark across a bar. */
    static Tick tick(double at, String colour, String name, String tip) {
        return new Tick(at, colour, 2, name, tip);
    }

    /** A chip with a tooltip. */
    static Label chipTip(String text, String colour, String tip) {
        Label c = chip(text, colour);
        if (tip != null) {
            Tooltip t = new Tooltip(tip);
            t.setShowDelay(Duration.millis(250));
            t.setWrapText(true);
            t.setMaxWidth(POPOVER_WIDTH);
            Tooltip.install(c, t);
        }
        return c;
    }

    /** The last `n` months of a History series, for a sparkline. */
    Region spark(String series, int n, String colour, double width, double height) {
        HistorySave h = ui.game.getHistorySave();
        double[] all = h.aligned(series);
        List<Integer> months = h.getMonth();
        int from = Math.max(0, all.length - n);
        double[] part = java.util.Arrays.copyOfRange(all, from, all.length);
        List<Integer> when = months.size() == all.length ? months.subList(from, all.length) : null;
        return sparkline(part, when, colour, width, height);
    }

    /** A segment bar's tooltip line: "name  $X  (12.3%)". */
    static String partTip(String name, double amount, double whole) {
        return name + "\n" + mFull(amount) + (whole > 0 ? "  (" + share1(amount / whole) + ")" : "");
    }

    /* =====================================================================
       THE OVERVIEW (0.7.33; the landing, "THE BANK AT A GLANCE", until then)

       Is my bank healthy, and what does it charge? One screen that answers
       without a click: the bank's state in the model's own sentence, beside
       its word; THE CAPITAL GAUGE - the ratio that binds against the minimum,
       its target and the top of its band - beside the scorecard of the eight
       figures a banker reads first (HOW FULL among them since 0.7.33, NEEDS
       YOU's THE BANK row as a figure); THE LADDER OF ITS RATES, every rung
       drawn in the parts it is built from, from the price of money to what
       each borrower pays; and the six pages behind it as cards, each with
       its headline and a year of its line.
       ===================================================================== */

    void overviewPage(VBox page, boolean fresh) {
        Bank bank = ui.game.getBank();
        List<CityNeeds.Need> all = CityNeeds.measure(ui.game, SummaryScreen.WORDS);
        page.getChildren().add(statusBanner(stanceWord(bank), stanceTone(bank), bank.status(), Icons.BANK));
        VBox capital = capitalCard(bank, fresh);
        capital.setMinWidth(380);
        capital.setPrefWidth(452);
        capital.setMaxWidth(452);
        VBox score = scorecard(all, fresh);
        HBox.setHgrow(score, Priority.ALWAYS);
        HBox hero = new HBox(TILE_GAP, capital, score);
        hero.setAlignment(Pos.TOP_LEFT);
        page.getChildren().add(hero);
        page.getChildren().add(ladderCard());
        page.getChildren().add(pagesBehind());
    }

    /* ------------------------------ the capital gauge ------------------------------ */

    /** The capital gauge's tooltip: both measures, the one that binds named (the old capital band's, since 0.7.11 round 2). */
    static String capitalTip(Bank bank) {
        boolean onLeverage = bank.leverageBinds();
        boolean lent = onLeverage || bank.getWeightedBook() > 0;
        double ratio = bank.isInsolvent() ? 0 : lent ? bank.bindingRatio() : 0;
        double scale = bank.bindingTop() * BAND_SCALE;
        return String.format("Capital: %s of %s%s\nthe minimum the city requires: %s\n"
                        + "its own target: %s\nthe top of its band: %s\n"
                        + "(risk-based %s against %s; leverage %s against %s - the larger requirement binds)",
                bank.isInsolvent() ? "none - it has failed" : lent ? share1(ratio) : "nothing lent",
                onLeverage ? "everything it has lent" : "its risk-weighted book",
                lent && ratio > scale ? " (past the end of this scale)" : "",
                share1(bank.bindingMinimum()), share1(bank.bindingTarget()), share1(bank.bindingTop()),
                bank.getWeightedBook() > 0 ? share1(bank.capitalRatio()) : "nothing weighed",
                share1(Bank.CAPITAL_RATIO),
                bank.exposure() > 0 ? share1(bank.leverageRatio()) : "nothing lent",
                share1(Bank.LEVERAGE_RATIO_MIN));
    }

    /** The gauge's words, worked out without drawing it (pure: the probe reads them). */
    record Gauge(String caption, String figure, String tone, String line, String key, String other,
                 double value, double min, double target, double top) { }

    /** The Overview's gauge: the measure that binds, as status() reads it (the spec's D4). */
    Gauge gauge() {
        Bank bank = ui.game.getBank();
        boolean onLeverage = bank.leverageBinds();
        boolean lent = onLeverage || bank.getWeightedBook() > 0;
        String caption = bank.getBranches() <= 0 || !lent ? "CAPITAL"
                : onLeverage ? "CAPITAL · the leverage ratio binds" : "CAPITAL · risk-weighted";
        String line = (onLeverage ? "of everything it has lent" : "of its risk-weighted book")
                + " · its target " + share1(bank.bindingTarget());
        String key = "minimum " + share1(bank.bindingMinimum()) + " · its target " + share1(bank.bindingTarget())
                + " · top of its band " + share1(bank.bindingTop());
        String other = onLeverage
                ? (bank.getWeightedBook() > 0
                        ? "the other measure: risk-weighted " + share1(bank.capitalRatio()) + ", its target "
                          + share1(bank.capitalTarget())
                        : "the other measure: nothing weighed for risk")
                : (bank.exposure() > 0
                        ? "the other measure: leverage " + share1(bank.leverageRatio()) + ", its target "
                          + share1(bank.leverageTarget())
                        : "the other measure: nothing lent");
        double value = bank.isInsolvent() || !lent ? Double.NaN : bank.bindingRatio();
        return new Gauge(caption, capitalFigure(bank), stanceTone(bank), line, key, other, value,
                bank.bindingMinimum(), bank.bindingTarget(), bank.bindingTop());
    }

    /**
     * THE CAPITAL CARD: the ratio that binds at 28 px in its stance's colour,
     * what it is of and its target, the bar in its band with the minimum,
     * the target and the top marked (Pieces.bandBar()), the other measure in
     * one muted line. On a new month the fill grows from last month's ratio
     * while the risk-weighted ratio is the one that binds - History keeps
     * that one only (the spec's D11). A click opens Capital & owners.
     */
    VBox capitalCard(Bank bank, boolean fresh) {
        Gauge g = gauge();
        Label cap = new Label(g.caption());
        cap.setWrapText(true);
        cap.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        Label fig = figure(g.figure(), 28, g.tone());
        SegmentBar bar = bandBar(g.value(), g.min(), g.target(), g.top(), g.tone(), 0, 14, true, capitalTip(bank));
        if (fresh && !bank.leverageBinds() && Double.isFinite(g.value())) {
            double[] s = ui.game.getHistorySave().aligned("bankCapitalRatio");
            double before = Double.NaN;
            int seen = 0;
            for (int i = s.length - 1; i >= 0 && seen < 2; i--) {
                if (!Double.isFinite(s[i])) continue;
                if (++seen == 2) before = s[i];
            }
            if (Double.isFinite(before)) bar.animateFirst(Math.min(before, g.top() * BAND_SCALE), 600);
        }
        VBox c = card(cap, fig, line(g.line()), bar, muted(g.key()), muted(g.other()));
        Tooltip t = new Tooltip("Capital & owners: both ratios on their bands");
        t.setShowDelay(Duration.millis(300));
        Tooltip.install(c, t);
        return doorCard(c, () -> openPage("Capital & owners"));
    }

    /* ------------------------------ the scorecard ------------------------------ */

    /**
     * The eight figures (pure: the probe reads them as the scorecard shows
     * them): what it earned, what that returns its owners, its losses and
     * HOW FULL it is (0.7.33: Bank.strain(), worded and coloured as NEEDS
     * YOU's THE BANK row - the spec's D5); its margin, its costs, and the two
     * sides of its book a player knows by name. The capital ratio that was
     * the fourth is the gauge beside them.
     */
    List<Cell> scorecardCells(List<CityNeeds.Need> all) {
        Bank bank = ui.game.getBank();
        double roe = bank.returnOnEquityOverYear();
        double lost = bank.provisionRateOverYear();
        double nim = bank.netInterestMarginOverYear();
        double costs = bank.costShareOverYear();
        int need = -1;
        for (CityNeeds.Need n : all) if (n.kind() == CityNeeds.Kind.BANK) need = n.level();
        String full = bank.isInsolvent() ? "failed" : bank.getBranches() <= 0 ? "none"
                : String.format("%.0f%% lent", bank.strain() * 100);
        return List.of(
                new Cell("PROFIT", m(bank.getNetIncome()),
                        m(bank.overYear(Bank.Line.NET)) + " over " + yearWords(bank),
                        bank.getNetIncome() < 0 ? Palette.BAD : Palette.GOOD,
                        "Profit: from what it earned to what it kept"),
                new Cell("RETURN ON EQUITY", ratePct(roe),
                        String.format("over %s; its owners want %s", yearWords(bank), ratePct(Bank.requiredReturn())),
                        roe < 0 ? Palette.BAD : roe < Bank.requiredReturn() ? Palette.WARN : Palette.GOOD,
                        "Profit: its return, against what its owners want"),
                new Cell("CREDIT LOSSES", ratePct(lost), "set aside a year, of its loans, over " + yearWords(bank),
                        lost > Bank.LOSS_WATCH * Bank.BASE_LOSS_RATE ? Palette.WARN : Palette.TEXT_HEAD,
                        "Lending: what it has set aside"),
                new Cell("HOW FULL", full, "of what its capital and its deposits carry",
                        need >= 2 ? Palette.BAD : need == 1 ? Palette.WARN : Palette.TEXT_HEAD,
                        "Funding: what it can carry"),
                new Cell("INTEREST MARGIN", ratePct(nim), "net, on what it lent, " + yearWords(bank),
                        nim < 0 ? Palette.BAD : Palette.TEXT_HEAD, "Profit: the margin and the year"),
                new Cell("COSTS", Double.isNaN(costs) ? "—" : share1(costs),
                        Double.isNaN(costs) ? "it earned nothing to set them against"
                                : "of what it earns, " + yearWords(bank),
                        Double.isNaN(costs) || costs > 1 ? Palette.BAD : Palette.TEXT_HEAD,
                        "Profit: its staff and branches"),
                new Cell("LENT OUT", m(bank.getBook()), "loans at face, bonds at cost", Palette.TEXT_HEAD,
                        "Lending: who owes it"),
                new Cell("DEPOSITS", m(bank.getDeposits()), "banked with it", Palette.TEXT_HEAD,
                        "Funding: what the city has banked"));
    }

    /** The scorecard: the eight as two rows of four, each a door. PROFIT pops on a new month. */
    VBox scorecard(List<CityNeeds.Need> all, boolean fresh) {
        List<Cell> eight = scorecardCells(all);
        Runnable[] go = {
                () -> openPage("Profit"), () -> openPage("Profit"),
                () -> open("Lending", SET_ASIDE), () -> open("Funding", CAN_CARRY),
                () -> openPage("Profit"), () -> openPage("Profit"),
                () -> openPage("Lending"), () -> openPage("Funding")};
        VBox[] cells = new VBox[8];
        for (int i = 0; i < 8; i++) {
            Cell c = eight.get(i);
            cells[i] = limitCell(c.label(), c.value(), c.note(), c.tone(), c.where(), go[i]);
        }
        if (fresh && cells[0].getChildren().get(1) instanceof Region r) UserInterface.popPip(r);
        HBox top = vitalsBar(cells[0], cells[1], cells[2], cells[3]);
        HBox bottom = vitalsBar(cells[4], cells[5], cells[6], cells[7]);
        VBox c = new VBox(12, top, bottom);
        c.setStyle(cardStyle(Palette.EDGE));
        c.setMaxWidth(Double.MAX_VALUE);
        return c;
    }

    /* ------------------------------ the rate ladder ------------------------------ */

    /** THE LADDER's (i): the landing's note (the spec's L10), with what the parts are. */
    static final String LADDER_INFO = "From the price of money to what each borrower pays. Every step up is a cost "
            + "the bank carries; the step down is its margin on a deposit. Each bar is drawn in the parts its rate is "
            + "built from: prime as what its money costs, running the bank, the loans expected to go bad and the "
            + "capital a loan ties up; a business as prime and its own risk, its record and what the bank's "
            + "concentration on it costs. An outlined part is what a rate is built on, or a quote nobody pays. Hover a "
            + "part for its figure; the (i) at the end of a row says why.";

    /** The ladder's card: its head, its key, the rungs. */
    VBox ladderCard() {
        List<Rung> rungs = rungs();
        double top = 0;
        for (Rung r : rungs) {
            if (r.caption()) continue;
            if (Double.isFinite(r.rate())) top = Math.max(top, r.rate());
            double drawn = 0;
            for (Segment s : r.parts()) drawn += Math.max(0, s.amount());
            top = Math.max(top, drawn);
            for (Tick t : r.ticks()) top = Math.max(top, t.at());
        }
        javafx.scene.layout.FlowPane key = keyRow(
                swatch(Palette.LADDER[1], "the bank's own", false),
                swatch(Palette.BUSINESS, "businesses", false),
                swatch(Palette.PEOPLE, "families", false),
                swatch(Palette.BUILDING, "landlords", false),
                swatch(Palette.RAMP_REST, "a quote, or not the bank's price", true));
        HBox head = cardHead(Icons.BANK, Palette.MONEY, "THE LADDER OF ITS RATES", LADDER_INFO,
                quiet("from the price of money to what each borrower pays"));
        return card(head, key, rateLadder(rungs, top * 1.02, 230, 110, 170));
    }

    /** Why savers get what they get: the share its funding asks for, and whether its margin held them under it. */
    static String saversWhy(Bank.Ladder l) {
        String why = l.fundingPosition() <= 0 ? "it holds reserves to spare"
                : l.fundingPosition() >= 1 ? "it borrows from the central bank"
                : String.format("it has lent %.0f%% of what its branches gathered", l.fundingPosition() * 100);
        String said = String.format("It aims to pass on %.0f%% of it, because %s, and moves a sixth "
                + "of the way there a month.", l.saversShare() * 100, why);
        if (l.saversHeld()) {
            said += String.format(" Its margin could not pay the %s it chose, so savers got less.",
                    ratePct(l.saversChose()));
        }
        return said;
    }

    /** The sectors that owe the bank or are shut out of it, in the registry's order. */
    List<String> owing() {
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        List<String> out = new ArrayList<>();
        for (String name : Sectors.KEYS) {
            if (credit.getPrincipal(name) > 0 || credit.isBorrowingBlocked(name)) out.add(name);
        }
        return out;
    }

    /** A sector's icon, by its key (Icons.ofSector()). */
    String sectorIcon(String key) { return Icons.ofSector(ui.game.getSectors().byKey(key)); }

    /**
     * THE LADDER'S RUNGS (pure: the probe reads them), each drawn in its
     * parts - the spec's D2 and D3. The bank's own in its blues: the policy
     * rate; what savers get, its margin outlined over it; what a loan's
     * money costs it, on the policy rate outlined; prime as its four parts
     * (Bank.ladder(), which BankCheck sums). Then what each borrower pays:
     * a business as prime outlined and its own risk, its record and the
     * book's concentration over it (BusinessDebtManager.quoteParts(), the
     * parts its rate was struck from - a negative part shortens the outline
     * and a tick marks prime); its bonds under it, the yield the bank would
     * hold one at over its loan's rate, a tick at what they pay; the
     * families in teal (their line's start, outlined, when nobody owes); the
     * carry trade; an insured mortgage in the landlords' pink on its money;
     * and the city's paper outlined - the market's price, not the bank's.
     */
    List<Rung> rungs() {
        Bank bank = ui.game.getBank();
        double dial = ui.game.getDebtManager().getPolicyRate();
        Bank.Ladder l = bank.ladder(dial);
        List<Rung> out = new ArrayList<>();

        out.add(Rung.of(Icons.POLICY, Palette.LADDER[1], "The policy rate",
                        List.of(seg(l.policy(), Palette.LADDER[0], "the policy rate\n" + ratePerYear(l.policy()))),
                        l.policy(), "set by the central bank")
                .info("Set by the central bank - and what the bank's reserves earn there. Every rate on this ladder "
                        + "is built on it. A click goes to it.")
                .go(() -> openPolicy("Money", "The policy rate")));

        List<Segment> savers = new ArrayList<>();
        savers.add(seg(Math.max(0, l.savers()), Palette.PEOPLE, "what savers get\n" + ratePerYear(l.savers())));
        if (-l.saversOverPolicy() > 0) {
            savers.add(ghost(-l.saversOverPolicy(), Palette.PEOPLE, "its margin\n" + points(-l.saversOverPolicy())
                    + " under the policy rate"));
        }
        out.add(Rung.of(Icons.POPULATION, Palette.PEOPLE, "What savers get", savers, l.savers(),
                        pts(l.saversOverPolicy()) + " on policy")
                .info(points(l.saversOverPolicy()) + " on the policy rate: its margin on a deposit. " + saversWhy(l))
                .go(() -> openPage("Funding")));

        String window = bank.windowShare() > 0
                ? String.format(", and the central bank's %s penalty on the %.0f%% it borrows there",
                        points(CentralBank.WINDOW_PENALTY), bank.windowShare() * 100) : "";
        out.add(Rung.of(Icons.BANK, Palette.LADDER[1], "What a loan's money costs it",
                        List.of(ghost(Math.max(0, l.policy()), Palette.LADDER[1], "the policy rate\n" + ratePerYear(l.policy())),
                                seg(Math.max(0, l.transferOverPolicy()), Palette.LADDER[1],
                                        "the term premium" + (bank.windowShare() > 0 ? " and the window's penalty" : "")
                                        + "\n" + points(l.transferOverPolicy()))),
                        l.transfer(), pts(l.transferOverPolicy()) + " on policy")
                .info(points(l.transferOverPolicy()) + " on the policy rate: the term premium on a "
                        + Bank.PRIME_TERM_MONTHS + "-month loan" + window + ". The funds-transfer price: what a dollar "
                        + "lent for a business loan's term costs the bank - not what its deposits cost it, which is the "
                        + "deposit side's margin.")
                .go(() -> open("Lending", PRICING)));

        out.add(Rung.of(Icons.BANK, Palette.LADDER[2], "Prime",
                        List.of(seg(Math.max(0, l.transfer()), Palette.REVENUE_RAMP[1], "money",
                                        "what its money costs\n" + ratePerYear(l.transfer())),
                                seg(Math.max(0, l.running()), Palette.REVENUE_RAMP[2], "running",
                                        "running the bank\n" + points(l.running())),
                                seg(Math.max(0, l.loss()), Palette.REVENUE_RAMP[3], "loss",
                                        "the loans expected to go bad\n" + points(l.loss())),
                                seg(Math.max(0, l.capital()), Palette.REVENUE_RAMP[4], "capital",
                                        "the capital a loan ties up\n" + points(l.capital()))),
                        l.prime(), pts(l.primeOverTransfer()) + " on its money")
                .tag(primeChange(), Palette.TEXT_MUTED)
                .info(String.format("%s on that: running the bank %s, loans expected to go bad %s, the capital a loan "
                                + "ties up %s. What a sound business pays for a new loan. Every other borrower pays its "
                                + "own risk over it.", points(l.primeOverTransfer()), points(l.running()),
                        points(l.loss()), points(l.capital())))
                .go(() -> open("Lending", PRICING)));

        out.add(Rung.caption("WHAT EACH BORROWER PAYS TO BORROW NOW"));
        out.addAll(borrowerRungs(l, true));
        return out;
    }

    /**
     * What each borrower pays (pure): the businesses that owe it or are shut
     * out, each with its bonds under it when `all`; the families; the carry
     * trade; an insured mortgage; and, when `all`, the city's paper. Lending's
     * HOW THE NEXT LOAN IS PRICED draws them without the bonds and the
     * paper - the rates the bank itself prices a borrower at.
     */
    List<Rung> borrowerRungs(Bank.Ladder l, boolean all) {
        Bank bank = ui.game.getBank();
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        HouseholdBalance homes = ui.game.getHouseholdBalance();
        double families = homes == null ? 0 : homes.averageRate();
        double city = ui.game.getInterestRate();
        BondMarket market = ui.game.getBondMarket();
        List<Rung> out = new ArrayList<>();
        Tick prime = tick(l.prime(), Palette.TEXT_LABEL, null, "prime " + ratePerYear(l.prime()));
        List<String> owing = owing();
        if (owing.isEmpty()) {
            out.add(Rung.caption("No business owes it anything; each would borrow at prime and its own risk over it."));
        }
        for (String name : Sectors.KEYS) {
            boolean owes = owing.contains(name);
            if (owes) out.add(sectorRung(name, l, credit));
            if (all && market.principal(name) > 0) {
                double y = market.bankYield(name, CorporateBond.TERM_MONTHS, 0, 0, 0);
                if (Double.isFinite(y)) {
                    Rung bond = bondRung(name, y, bank, credit, market);
                    out.add(owes ? bond.under() : bond);
                }
            }
        }

        boolean anyFamily = families > 0;
        out.add(Rung.of(Icons.HOMES, Palette.PEOPLE, "The families",
                        List.of(anyFamily
                                ? seg(families, Palette.PEOPLE, "what the families pay, on average\n" + ratePerYear(families))
                                : ghost(Math.max(0, l.household()), Palette.PEOPLE,
                                        "where a family's line starts\n" + ratePerYear(l.household())
                                        + "\nNo family owes it anything.")),
                        anyFamily ? families : l.household(),
                        anyFamily ? pts(l.overPrime(families)) + " on prime" : "nobody owes")
                .ticks(List.of(prime))
                .info(anyFamily
                        ? String.format("%s on prime, on average: their line starts at %s and adds %.2f points for "
                                + "every month of income a family owes.", points(l.overPrime(families)),
                                ratePerYear(l.household()), HouseholdBalance.RISK_SLOPE * 100)
                        : "What a family's credit line starts from - no family owes it anything, so the bar is "
                          + "outlined: a quote nobody pays.")
                .go(() -> open("Lending", FAMILIES)));

        out.add(Rung.of(Icons.TRADE, Palette.RAMP_REST, "The carry trade",
                        List.of(seg(Math.max(0, l.carry()), Palette.RAMP_REST, "the carry trade\n" + ratePerYear(l.carry()))),
                        l.carry(), pts(l.overPrime(l.carry())) + " on prime")
                .ticks(List.of(prime))
                .info(points(l.overPrime(l.carry())) + " on prime: lent short and to borrowers who never default "
                        + "here, so no term premium and no expected loss. Foreigners borrowing here to hold the money "
                        + "abroad, while the bank lends cheaper than the world pays.")
                .go(() -> open("Lending", CARRY)));

        out.add(Rung.of(Icons.BUILD, Palette.BUILDING, "An insured mortgage (10 years)",
                        List.of(ghost(Math.max(0, l.mortgageTransfer()), Palette.BUILDING,
                                        "its money, for ten years\n" + ratePerYear(l.mortgageTransfer())),
                                seg(Math.max(0, l.mortgageRunning()), Palette.BUILDING_LIGHT,
                                        "running the bank\n" + points(l.mortgageRunning())),
                                seg(Math.max(0, l.mortgageCapital()), Palette.BUILDING,
                                        "the capital its leverage minimum ties up\n" + points(l.mortgageCapital()))),
                        l.mortgage(), pts(l.overPrime(l.mortgage())) + " on prime")
                .info(String.format("%s on the policy rate - the %d-year term premium %s%s - running the bank %s and "
                                + "the capital the %s leverage minimum ties up %s; no expected loss, the city insures it "
                                + "(%s on prime). What the landlords' new buildings are financed at, fixed for the term "
                                + "and renewed at the day's rate when it ends.",
                        points(l.mortgage() - l.policy()), Mortgage.MORTGAGE_TERM_MONTHS / 12,
                        points(DebtManager.termPremium(Mortgage.MORTGAGE_TERM_MONTHS)),
                        bank.windowShare() > 0 ? String.format(", the window's penalty on its share %s",
                                points(CentralBank.WINDOW_PENALTY * bank.windowShare())) : "",
                        points(l.mortgageRunning()), share1(Bank.LEVERAGE_RATIO_MIN), points(l.mortgageCapital()),
                        points(l.overPrime(l.mortgage()))))
                .go(() -> open("Lending", LANDLORDS)));

        if (!all) return out;
        out.add(Rung.of(Icons.GOVERNMENT, Palette.MONEY, "The city's own paper",
                        List.of(ghost(Math.max(0, city), Palette.MONEY_LIGHT,
                                "the city's rate\n" + ratePerYear(city) + "\nthe market's price, not the bank's")),
                        city, pts(l.overPrime(city)) + " on prime")
                .ticks(List.of(prime))
                .info(points(l.overPrime(city)) + " on prime: the city's rate, which the market sets on its credit - "
                        + "the bank does not price it, so the bar is outlined.")
                .go(() -> ui.financesScreen.open("Borrow", "At home", null)));
        return out;
    }

    /** A sector's rung: prime outlined, then its own risk, its record and the book's concentration (the spec's D2; B4 named the concentration). */
    Rung sectorRung(String name, Bank.Ladder l, BusinessDebtManager credit) {
        boolean shut = credit.isBorrowingBlocked(name);
        double rate = credit.getRate(name);
        BusinessDebtManager.QuoteParts q = credit.quoteParts(name);
        List<Segment> parts = new ArrayList<>();
        List<Tick> ticks = new ArrayList<>();
        String sentence;
        String step;
        if (q != null) {
            double under = Math.min(0, q.risk()) + Math.min(0, q.record()) + Math.min(0, q.concentration());
            parts.add(ghost(Math.max(0, q.prime() + under), Palette.BUSINESS, "prime\n" + ratePerYear(q.prime())
                    + (under < 0 ? "\nless " + points(-under).replace("+", "") + " the book's concentration takes off" : "")));
            if (q.risk() > 0) parts.add(seg(q.risk(), Palette.BUSINESS, "its own risk\n" + points(q.risk())));
            if (q.record() > 0) parts.add(seg(q.record(), Palette.BUSINESS_DARK, "its record\n" + points(q.record())));
            if (q.concentration() > 0) parts.add(seg(q.concentration(), Palette.BUSINESS_LIGHT,
                    "the book's concentration\n" + points(q.concentration())));
            if (under < 0) ticks.add(tick(q.prime(), Palette.TEXT_LABEL, null, "prime " + ratePerYear(q.prime())));
            sentence = String.format("%s on prime: its own expected loss %s - %s of its firms default a year at %.2fx "
                            + "its assets over its last quarter%s, and %s for the book's concentration on it",
                    points(q.spread()), points(q.risk()), defaultShare(credit.getQuarterDefaultRate(name)),
                    credit.getQuarterLeverage(name),
                    q.record() > 0 ? ", its record " + points(q.record()) : "",
                    points(q.concentration()));
            step = pts(q.spread()) + " on prime";
        } else {
            parts.add(seg(Math.max(0, rate), Palette.BUSINESS, name + "\n" + ratePerYear(rate)));
            sentence = "Not priced yet: its rate is struck at the top of the month.";
            step = null;
        }
        Rung r = Rung.of(sectorIcon(name), Palette.BUSINESS, name, parts, rate, step)
                .ticks(ticks)
                .info(sentence + (shut ? String.format(" - shut out for %d more months", credit.getBlockedMonths(name)) : "")
                        + ". A click opens its Cash & debt page.")
                .go(() -> openSector(name, "Cash & debt"));
        if (shut) r = r.tone(Palette.BAD).tag("shut " + credit.getBlockedMonths(name) + " mo", Palette.BAD);
        else if (credit.defaultsAreNews(name)) r = r.tag("defaulting", Palette.BAD);
        return r;
    }

    /** A sector's bond: what the bank would hold one at, over its loan's rate, a tick at what its bonds pay (BondMarket.bankYield(), 0.7.12). */
    Rung bondRung(String name, double y, Bank bank, BusinessDebtManager credit, BondMarket market) {
        double loan = credit.getRate(name);
        double coupon = market.averageCoupon(name);
        boolean holds = coupon >= y;
        List<Segment> parts = new ArrayList<>();
        parts.add(ghost(Math.max(0, Math.min(y, loan)), Palette.BUSINESS_LIGHT, "its loan's rate\n" + ratePerYear(loan)));
        if (y > loan) parts.add(seg(y - loan, Palette.BUSINESS_LIGHT, "a bond's ten years and its loss over the loan\n"
                + points(y - loan)));
        return Rung.of(Icons.FINANCES, Palette.BUSINESS, "A bond of " + name, parts, y,
                        holds ? "holds them" : "leaves them to others")
                .ticks(List.of(tick(coupon, Palette.TEXT_HEAD, null, "its bonds pay " + ratePerYear(coupon) + " on average")))
                .info(String.format("%s over its loan's rate: a %d-year loan's money, its concentration charge %s, and the "
                                + "%.0f%% a bondholder loses on what defaults, where a loan loses %.0f%%; its bonds pay %s "
                                + "on average, so the bank %s. The yield at which a bond of this business earns the bank "
                                + "what an equal loan would.",
                        points(y - loan), CorporateBond.TERM_MONTHS / 12,
                        points(bank.concentrationCharge(ui.game.getDebtManager().getPolicyRate(),
                                CorporateBond.TERM_MONTHS, name)),
                        BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT * 100,
                        BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT * 100, ratePct(coupon),
                        holds ? "would hold them" : "leaves them to others"))
                .go(() -> { openLines.add("lending:bonds"); open("Lending", DETAILS); });
    }

    /* ------------------------------ the pages behind it ------------------------------ */

    /** One page as a card on the Overview, worked out without drawing it (pure: the probe reads them). */
    record PageCard(String name, String icon, String blurb, String figure, String sub, String tone, String series) { }

    /** The six (the landing's rows until 0.7.33, the spec's L16): what each holds and its headline. */
    List<PageCard> pageCards() {
        Bank bank = ui.game.getBank();
        HistorySave h = ui.game.getHistorySave();
        int troubled = bank.getBooksWatched();
        return List.of(
                new PageCard("Profit", Icons.COIN, "its income statement, and what it did with the profit",
                        m(bank.getNetIncome()) + " this month",
                        m(bank.overYear(Bank.Line.NET)) + " over " + yearWords(bank),
                        bank.getNetIncome() < 0 ? Palette.BAD : Palette.GOOD, "bankProfit"),
                new PageCard("Balance sheet", Icons.FINANCES, "what it owns, what it owes and its equity, against a year ago",
                        m(bank.totalAssets()) + " of assets",
                        bank.knowsYearAgo() ? m(bank.yearAgo(Bank.Sheet.ASSETS)) + " a year ago" : "a year ago not on file yet",
                        Palette.TEXT_HEAD, "bankLent"),
                new PageCard("Lending", Icons.SECTOR, "who owes it, what it has set aside, how the next loan is priced",
                        m(bank.getBook()) + " lent",
                        troubled > 0 ? troubled + (troubled == 1 ? " borrower" : " borrowers") + " in trouble"
                                : "every borrower sound",
                        troubled > 0 ? Palette.WARN : Palette.TEXT_HEAD, "bankWriteOffs"),
                new PageCard("Funding", Icons.BANK, "deposits, the central bank, and its branches",
                        m(bank.getDeposits()) + " deposited",
                        bank.wholesaleFunding() > 0 ? m(bank.wholesaleFunding()) + " borrowed from the central bank"
                                : "nothing borrowed from the central bank",
                        bank.wholesaleFunding() > 0 ? Palette.WARN : Palette.TEXT_HEAD, "bankDeposits"),
                new PageCard("Capital & owners", Icons.STAFF, "its capital against its target, its payout, its shares",
                        bank.isInsolvent() ? "failed"
                                : bank.leverageBinds() ? share1(bank.leverageRatio()) + " leverage"
                                : bank.getWeightedBook() > 0 ? share1(bank.capitalRatio()) + " capital" : "nothing lent",
                        bank.payoutDecision(), stanceTone(bank), "bankEquity"),
                new PageCard("History", Icons.REPORTS, "its rates, capital, returns and losses over time",
                        formatter.format(h.months()) + (h.months() == 1 ? " month" : " months"),
                        h.months() < 2 ? "a line needs two points" : "recorded", Palette.TEXT_HEAD, "bankPrime"));
    }

    /** THE PAGES BEHIND IT: the six as cards, three to a row, each with a year of its line. */
    VBox pagesBehind() {
        GridPane grid = equalColumns(3, TILE_GAP);
        List<PageCard> six = pageCards();
        for (int i = 0; i < six.size(); i++) {
            PageCard p = six.get(i);
            Label name = new Label(p.name());
            name.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
            Label blurb = muted(p.blurb());
            Label fig = figure(p.figure(), Palette.SIZE_LEAD, p.tone());
            Label sub = words(p.sub(), Palette.SIZE_LABEL, Palette.TEXT_LABEL);
            VBox text = new VBox(2, name, blurb, fig, sub);
            text.setMinWidth(0);
            HBox.setHgrow(text, Priority.ALWAYS);
            HBox top = new HBox(Palette.GAP_LOOSE, iconSquare(p.icon(), Palette.MONEY, 28, 15), text,
                    spark(p.series(), 12, Palette.MONEY, 120, 28));
            top.setAlignment(Pos.TOP_LEFT);
            String page = p.name();
            VBox c = doorCard(card(top), () -> openPage(page));
            GridPane.setFillHeight(c, true);
            c.setMaxHeight(Double.MAX_VALUE);
            grid.add(c, i % 3, i / 3);
        }
        return new VBox(Palette.GAP, sectionHead("THE PAGES BEHIND IT", null), grid);
    }

    /* =====================================================================
       PROFIT

       Where does its profit come from, and where does it go? Since 0.7.33 a
       WATERFALL leads, FROM WHAT IT EARNED TO WHAT IT KEPT: the interest by
       who paid it, what savers and the window took, fees by kind, the
       provisions, the desk, the gains, staff and branches, tax, and what it
       did with the rest - each total the model's own figure, this month or
       over the last twelve. Under it the three ratios a bank is read by and
       the year, as cards; the trading desk as a card when it holds or trades;
       and the income statement itself behind "details", as it was - every
       line opening into what it is made of, the interest by who paid it since
       0.7.9 (the old page said it "cannot be split further", and it could).
       ===================================================================== */

    void profitPage(VBox page) {
        Bank bank = ui.game.getBank();
        page.getChildren().add(waterfallCard(bank));
        page.getChildren().add(row(marginCard(bank), costsCard(bank), roeCard(bank), yearCard(bank)));
        if (deskShows(bank)) page.getChildren().add(deskCard(bank));
        VBox statement = fold("profit:statement",
                "the income statement, this month and last, every line opening; the last twelve months; the ratios",
                () -> profitStatement(bank));
        targets.put(DETAILS, statement);
        page.getChildren().add(statement);
    }

    /* ------------------------------ the waterfall ------------------------------ */

    /** The waterfall worked out without drawing it (pure: the probe reads it): its steps, and the lines at nothing named in a line under it. */
    record Walk(List<Step> steps, List<String> nothing) { }

    /** A step under half a dollar is nothing: it is left off the walk and named under it. */
    static final double NOTHING = .0005;

    /**
     * FROM WHAT IT EARNED TO WHAT IT KEPT (pure): the income statement's
     * lines as steps, this month or over the year (Bank.thisMonth(),
     * overYear()); the four totals are the model's own (NET_INTEREST,
     * PRE_TAX, NET, RETAINED), drawn from zero, never re-summed here. A step
     * up is the money blue, a step down its dark step; a total is neutral,
     * red under nothing; the interest is stacked by who paid it and the fees
     * by kind, in the areas' colours.
     */
    Walk walk(boolean year) {
        Bank bank = ui.game.getBank();
        java.util.function.ToDoubleFunction<Bank.Line> v = line -> year ? bank.overYear(line) : bank.thisMonth(line);
        List<Step> steps = new ArrayList<>();
        List<String> nothing = new ArrayList<>();

        List<Slice> payers = new ArrayList<>();
        Object[][] who = {
                {"From the businesses", Bank.Line.FROM_BUSINESSES, Palette.BUSINESS},
                {"On the businesses' bonds it holds", Bank.Line.FROM_BONDS, Palette.BUSINESS_LIGHT},
                {"From the families", Bank.Line.FROM_HOUSEHOLDS, Palette.PEOPLE},
                {"From the city, on its paper", Bank.Line.FROM_CITY, Palette.MONEY},
                {"The discount on the city's paper, as it is earned", Bank.Line.DISCOUNT, Palette.MONEY_DARK},
                {"From the carry trade", Bank.Line.FROM_CARRY, Palette.RAMP_REST},
                {"On its reserves at the central bank", Bank.Line.FROM_RESERVES, Palette.MONEY_LIGHT}};
        for (Object[] w : who) {
            double a = v.applyAsDouble((Bank.Line) w[1]);
            if (Math.abs(a) >= NOTHING) payers.add(new Slice((String) w[0], a, (String) w[2]));
        }
        step(steps, nothing, "Interest earned", v.applyAsDouble(Bank.Line.INTEREST), Icons.COIN, payers,
                "The interest it earned, by who paid it.");
        step(steps, nothing, "Paid to savers", -v.applyAsDouble(Bank.Line.SAVERS), Icons.POPULATION, null, null);
        step(steps, nothing, "The window", -v.applyAsDouble(Bank.Line.WINDOW), Icons.BANK, null,
                "Paid for borrowing overnight from the central bank.");
        total(steps, "Net interest", v.applyAsDouble(Bank.Line.NET_INTEREST));

        List<Slice> kinds = new ArrayList<>();
        Object[][] fees = {
                {"On the families' accounts", Bank.Line.ACCOUNT_FEES, Palette.PEOPLE},
                {"On the businesses' new loans", Bank.Line.LOAN_FEES_PAID, Palette.BUSINESS},
                {"On the families' new borrowing, added to what they owe", Bank.Line.LOAN_FEES_OWED, Palette.PEOPLE_LIGHT},
                {"Underwriting the businesses' bonds", Bank.Line.UNDERWRITING, Palette.BUSINESS_LIGHT}};
        for (Object[] f : fees) {
            double a = v.applyAsDouble((Bank.Line) f[1]);
            if (Math.abs(a) >= NOTHING) kinds.add(new Slice((String) f[0], a, (String) f[2]));
        }
        step(steps, nothing, "Fees", v.applyAsDouble(Bank.Line.FEES), Icons.COIN, kinds, "Its fees, by kind.");
        step(steps, nothing, "Provisions", -v.applyAsDouble(Bank.Line.PROVISIONS), Icons.ALERT, null,
                PROVISIONS_INFO);
        step(steps, nothing, "The trading desk", v.applyAsDouble(Bank.Line.TRADING), Icons.REPORTS, null,
                "What its trading desk made, the re-mark of what it holds included.");
        step(steps, nothing, "Gains on the city's paper", v.applyAsDouble(Bank.Line.PAPER_GAINS), Icons.GOVERNMENT,
                null, "Gains on the city's paper that changed hands.");
        step(steps, nothing, "Gains on bonds", v.applyAsDouble(Bank.Line.BOND_GAINS), Icons.FINANCES, null,
                "Gains on the businesses' bonds it sold or was repaid.");
        List<Slice> running = new ArrayList<>();
        double payroll = v.applyAsDouble(Bank.Line.PAYROLL), upkeep = v.applyAsDouble(Bank.Line.UPKEEP);
        if (Math.abs(payroll) >= NOTHING) running.add(new Slice("Its staff", -payroll, Palette.MONEY_DARK));
        if (Math.abs(upkeep) >= NOTHING) running.add(new Slice("Its branches' upkeep", -upkeep, Palette.LADDER[2]));
        step(steps, nothing, "Staff and branches", -v.applyAsDouble(Bank.Line.COSTS), Icons.STAFF, running, null);
        total(steps, "Profit before tax", v.applyAsDouble(Bank.Line.PRE_TAX));
        step(steps, nothing, "Tax", -v.applyAsDouble(Bank.Line.TAX), Icons.GOVERNMENT, null,
                String.format("Tax is paid a month in arrears, on last month's %s of profit: the city's take is struck "
                        + "before the bank knows what it made.", m(bank.getProfitLastMonth())));
        total(steps, "What it kept", v.applyAsDouble(Bank.Line.NET));
        step(steps, nothing, "Dividends", -v.applyAsDouble(Bank.Line.DIVIDENDS), Icons.STAFF, null,
                "Its owners are paid on last month's profit, by its capital - " + bank.payoutDecision()
                        + ". A click opens Capital & owners, which has the rule.", () -> openPage("Capital & owners"));
        step(steps, nothing, "Buybacks", -v.applyAsDouble(Bank.Line.BUYBACKS), Icons.STAFF, null,
                "Its own shares, bought back with what it holds over its target. A click opens Capital & owners.",
                () -> openPage("Capital & owners"));
        total(steps, "Kept in the bank", v.applyAsDouble(Bank.Line.RETAINED));
        return new Walk(steps, nothing);
    }

    /** A plain step of the walk: up in the money blue, down in its dark step; at nothing, named instead. */
    void step(List<Step> steps, List<String> nothing, String name, double amount, String icon, List<Slice> parts,
              String tip) {
        step(steps, nothing, name, amount, icon, parts, tip, null);
    }

    /** ...with a door of its own (null: the statement under "details"). */
    void step(List<Step> steps, List<String> nothing, String name, double amount, String icon, List<Slice> parts,
              String tip, Runnable go) {
        if (!(Math.abs(amount) >= NOTHING)) {
            nothing.add(name);
            return;
        }
        Step s = Step.of(name, amount, amount < 0 ? Palette.MONEY_DARK : Palette.MONEY).icon(icon);
        if (parts != null && !parts.isEmpty()) s = s.parts(parts);
        if (tip != null) s = s.tip(tip);
        steps.add(s.go(n -> { if (go != null) go.run(); else openStatement(); }));
    }

    /** A total of the walk, the model's own figure, drawn from zero: neutral, red under nothing. */
    void total(List<Step> steps, String name, double amount) {
        steps.add(Step.total(name, amount, amount < 0 ? Palette.BAD : Palette.TEXT_SPENT).go(n -> openStatement()));
    }

    /** The statement under "details", opened and scrolled to. */
    void openStatement() {
        openLines.add("profit:statement");
        scrollTarget = DETAILS;
        showBankMenu();
    }

    /** The provisions' note (the spec's T, Profit 790), behind the step's tooltip and the statement's line. */
    static final String PROVISIONS_INFO = "A provision is money set aside for loans expected to go bad: a year's "
            + "expected loss on a sound borrower, what a default would cost on one in trouble.";

    /** The waterfall's card: its head with the toggle, the walk, and what was nothing. */
    VBox waterfallCard(Bank bank) {
        javafx.scene.layout.FlowPane toggle = chipStrip(new String[] {"this month", "last 12 months"},
                profitYear ? "last 12 months" : "this month", Palette.SIZE_LABEL, pick -> {
                    profitYear = "last 12 months".equals(pick);
                    showBankMenu();
                });
        toggle.setAlignment(Pos.CENTER_RIGHT);
        HBox head = cardHead(Icons.COIN, Palette.MONEY, "FROM WHAT IT EARNED TO WHAT IT KEPT",
                "Each column a line of its income statement, "
                        + (profitYear ? "added up over " + yearWords(bank) : "this month, " + CityCalendar.format(ui.game.getMonth()))
                        + ": a step up is money in, a step down money out, and a column from the axis a total - the "
                        + "model's own figure. Hover a column for what it is made of; a click opens the statement under "
                        + "\"details\" at the foot of the page - Dividends and Buybacks open Capital & owners. "
                        + PROVISIONS_INFO,
                toggle);
        VBox c = card(head);
        if (!bank.isMonthKnown() && !profitYear) {
            c.getChildren().add(line("Recorded from the next month played: there is no month yet to walk."));
            return c;
        }
        Walk w = walk(profitYear);
        Pieces.Waterfall fall = waterfall(w.steps(), v -> (v < 0 ? "−" : "") + money(Math.abs(v)), 0, 250);
        c.getChildren().add(fall);
        if (!w.nothing().isEmpty()) {
            c.getChildren().add(muted((profitYear ? "nothing over " + yearWords(bank) + ": " : "nothing this month: ")
                    + String.join(" · ", w.nothing())));
        }
        return c;
    }

    /* ------------------------------ the four cards ------------------------------ */

    /** The ratios' note (Profit 874), behind MARGIN's and COSTS' (i). */
    static final String RATIOS_INFO = "The margin is what it charges less what it pays for its money, on everything lent. "
            + "Its costs are read against what it earns before them - about half to three-fifths at a real bank.";

    VBox marginCard(Bank bank) {
        double nim = bank.netInterestMarginOverYear();
        return card(cardHead(null, null, "INTEREST MARGIN", RATIOS_INFO, null),
                big(ratePct(nim), nim < 0 ? Palette.BAD : Palette.TEXT_HEAD),
                line("net, on what it lent, over " + yearWords(bank)),
                muted("this month alone " + ratePct(bank.netInterestMargin())));
    }

    VBox costsCard(Bank bank) {
        double costs = bank.costShareOverYear();
        boolean none = Double.isNaN(costs);
        Region ring = ring(none ? 0 : Math.min(1, costs), none || costs > 1 ? Palette.BAD : Palette.MONEY, 64, 8,
                none ? "—" : share1(costs), 12);
        VBox words = new VBox(2, line(none ? "it earned nothing to set them against" : "of what it earns"),
                muted("its staff and branches, over " + yearWords(bank)));
        words.setMinWidth(0);
        HBox body = new HBox(Palette.GAP_LOOSE, ring, words);
        body.setAlignment(Pos.CENTER_LEFT);
        return card(cardHead(null, null, "COSTS", RATIOS_INFO, null), body);
    }

    VBox roeCard(Bank bank) {
        double roe = bank.returnOnEquityOverYear();
        double want = Bank.requiredReturn();
        String tone = roe < 0 ? Palette.BAD : roe < want ? Palette.WARN : Palette.GOOD;
        double scale = Math.max(Math.max(roe, want), 1e-9) * 1.25;
        SegmentBar bar = segmentBar(List.of(seg(Math.max(0, roe), tone, "its return\n" + ratePerYear(roe))), scale,
                List.of(tick(want, Palette.TEXT_HEAD, "its owners want " + ratePct(want), null)), 0, 10);
        return card(cardHead(null, null, "RETURN ON EQUITY",
                        "What it earned over the year, at a yearly rate, on the equity it held on average - steadier "
                        + "than a month's. Its owners want " + ratePct(want) + " over time.", null),
                big(ratePct(roe), tone), bar,
                muted("over " + yearWords(bank) + " · this month alone " + ratePct(bank.returnOnEquity())));
    }

    VBox yearCard(Bank bank) {
        HistorySave h = ui.game.getHistorySave();
        int losing = h.monthsUnder("bankProfit", 0.0);
        double net = bank.overYear(Bank.Line.NET);
        return card(cardHead(null, null, "THE YEAR", null, null),
                big(m(net), net < 0 ? Palette.BAD : Palette.TEXT_HEAD),
                line("kept over " + yearWords(bank)),
                spark("bankProfit", 12, Palette.MONEY, 220, 32),
                muted("lost money in " + losing + " of " + h.monthsRecorded("bankProfit") + " months on record"));
    }

    /* ------------------------------ the trading desk ------------------------------ */

    /** Whether the desk card shows: it holds something, or traded or was paid this month. */
    boolean deskShows(Bank bank) {
        Exchange exchange = ui.game.getExchange();
        return Math.abs(bank.getSecurities()) > NOTHING || Math.abs(bank.getTradingIncome()) > NOTHING
                || exchange.deskSoldToHouseholds() > NOTHING || exchange.deskSoldAbroad() > NOTHING
                || exchange.deskBoughtFromHouseholds() > NOTHING || exchange.deskBoughtFromAbroad() > NOTHING;
    }

    /**
     * THE TRADING DESK as a card (0.7.33): what it sold and bought and from
     * whom, what it was paid, its re-mark and what it holds; its book and its
     * largest holding against their caps (Exchange.BOOK_LIMIT and
     * POSITION_LIMIT as ticks); and what it holds, a chip a company. The
     * orders and the re-mark's sentence are in its (i); the old block whole
     * is the statement's, under "details".
     */
    VBox deskCard(Bank bank) {
        Exchange exchange = ui.game.getExchange();
        Equity register = ui.game.getEquity();
        double reMark = bank.getMarkChange();
        VBox lines = new VBox(3,
                cardLine("Sold to the households", mFull(exchange.deskSoldToHouseholds()), null),
                cardLine("Sold abroad", mFull(exchange.deskSoldAbroad()), null),
                cardLine("Bought from the households", mv(-exchange.deskBoughtFromHouseholds()), null),
                cardLine("Bought from abroad", mv(-exchange.deskBoughtFromAbroad()), null),
                cardLine("Dividends on what it holds", mFull(register.getDividendDeskThisMonth()), null),
                cardLine("Tendered into buybacks", mFull(exchange.getBuybackToDesk()), null),
                cardLine("Re-marked what it holds", mv(reMark), reMark < 0 ? Palette.WARN : null),
                cardLine("What it holds, at the mark", mFull(bank.getSecurities()), null));
        if (exchange.getEmigrantsPaid() > 0) {
            lines.getChildren().add(4, cardLine("...of which from families leaving the city",
                    mFull(exchange.getEmigrantsPaid()), null));
        }
        lines.setMinWidth(0);
        HBox.setHgrow(lines, Priority.ALWAYS);

        double largest = 0;
        String largestName = null;
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            if (c == Equity.BANK) continue;
            double s = exchange.deskPositionShare(register, c);
            if (s > largest) { largest = s; largestName = Equity.COMPANIES[c]; }
        }
        double bookShare = exchange.deskBookShare(register);
        double bookScale = Math.max(bookShare, Exchange.BOOK_LIMIT) * 1.25;
        double posScale = Math.max(largest, Exchange.POSITION_LIMIT) * 1.25;
        VBox caps = new VBox(6,
                line("its book, of the bank's equity: " + String.format("%.0f%% of a %.0f%% cap", bookShare * 100,
                        Exchange.BOOK_LIMIT * 100)),
                segmentBar(List.of(seg(bookShare, bookShare > Exchange.BOOK_LIMIT ? Palette.WARN : Palette.MONEY,
                                "its book at fair value\n" + share1(bookShare) + " of the bank's equity")), bookScale,
                        List.of(tick(Exchange.BOOK_LIMIT, Palette.TEXT_HEAD, "cap", null)), 0, 10),
                line(largestName == null ? "no holding in any company"
                        : "its largest, " + largestName + ": " + String.format("%.0f%% of a %.0f%% cap", largest * 100,
                        Exchange.POSITION_LIMIT * 100)),
                segmentBar(List.of(seg(largest, largest > Exchange.POSITION_LIMIT ? Palette.WARN : Palette.MONEY,
                                "its largest holding\n" + share1(largest) + " of the bank's equity")), posScale,
                        List.of(tick(Exchange.POSITION_LIMIT, Palette.TEXT_HEAD, "cap", null)), 0, 10));
        caps.setMinWidth(300);
        caps.setPrefWidth(360);

        javafx.scene.layout.FlowPane held = new javafx.scene.layout.FlowPane(6, 6);
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            double share = register.deskShare(c);
            if (share <= 0) continue;
            held.getChildren().add(chip(String.format("%s %.1f%% at %s", Equity.COMPANIES[c], share * 100,
                    tightMoney(toDollars(exchange.mark(c)), false).replace('-', '−')), Palette.MONEY));
        }
        if (held.getChildren().isEmpty()) held.getChildren().add(muted("It holds nothing."));
        held.setPrefWrapLength(320);
        VBox right = new VBox(6, line("what it holds"), held);
        right.setMinWidth(260);
        right.setPrefWidth(340);

        HBox body = new HBox(24, lines, caps, right);
        body.setAlignment(Pos.TOP_LEFT);
        return card(cardHead(Icons.REPORTS, Palette.MONEY, "THE TRADING DESK", deskInfo(bank), null), body);
    }

    /** The desk card's (i): its orders on the book and, when it lost on the re-mark, why (Profit 968-997). */
    String deskInfo(Bank bank) {
        Exchange exchange = ui.game.getExchange();
        StringBuilder orders = new StringBuilder();
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            double bid = exchange.deskResting(c, OrderBook.Side.BUY), ask = exchange.deskResting(c, OrderBook.Side.SELL);
            if (!(bid > 0) && !(ask > 0)) continue;
            if (orders.length() > 0) orders.append("; ");
            orders.append(Equity.COMPANIES[c]).append(':');
            if (bid > 0) orders.append(String.format(" bids for %,.0f at %s", bid,
                    tightMoney(toDollars(exchange.bestBidOf(c, Exchange.DESK)), false)));
            if (ask > 0) orders.append(String.format("%s asks %,.0f at %s", bid > 0 ? "," : "", ask,
                    tightMoney(toDollars(exchange.bestAskOf(c, Exchange.DESK)), false)));
        }
        String said = "It bids for every company a little under what the register says a share is worth, for what "
                + "its capital carries, and asks a little over it for what it holds - carried at the last trade or the "
                + "register's value, whichever is lower. "
                + (orders.length() == 0
                        ? (exchange.isOpen() ? "Nothing of its own rests on the book." : "It posts nothing: it has no capital to trade with.")
                        : "Its orders resting on the book: " + orders + ".");
        double reMark = bank.getMarkChange();
        if (bank.getTradingIncome() < 0 && reMark <= bank.getTradingIncome() / 2) {
            said += String.format(" %s of the %s lost is the re-mark, not the trading: shares bought above the "
                    + "register's value are marked down the day they are bought, and a company whose value falls marks "
                    + "down everything the desk holds in it.", mFull(-reMark), mFull(-bank.getTradingIncome()));
        }
        return said;
    }

    /* ------------------------------ the statement, under details ------------------------------ */

    /**
     * The income statement as the page printed it until 0.7.33, verbatim
     * (the spec's R1-R12, D15): this month against last, every line opening
     * into what it is made of, then what it did with it, the last twelve
     * months, and the ratios. Its colours are the B2 fix's: no green on the
     * interest, no amber on a provision or the desk.
     */
    VBox profitStatement(Bank bank) {
        VBox column = column();
        boolean known = bank.knowsLastMonth();

        column.getChildren().add(statementHead("Its income statement"));
        column.getChildren().add(bookHead(CityCalendar.format(ui.game.getMonth())));

        VBox byWho = new VBox(0);
        byWho.getChildren().addAll(
                line(bank, "From the businesses", Bank.Line.FROM_BUSINESSES, known, false),
                line(bank, "From the families", Bank.Line.FROM_HOUSEHOLDS, known, false),
                line(bank, "From the city, on its paper", Bank.Line.FROM_CITY, known, false),
                line(bank, "The discount on the city's paper, as it is earned", Bank.Line.DISCOUNT, known, false),
                line(bank, "From the carry trade", Bank.Line.FROM_CARRY, known, false),
                line(bank, "On its reserves at the central bank", Bank.Line.FROM_RESERVES, known, false),
                line(bank, "On the businesses' bonds it holds", Bank.Line.FROM_BONDS, known, false));
        column.getChildren().add(bookLine("Interest earned",
                bank.thisMonth(Bank.Line.INTEREST), bank.lastMonth(Bank.Line.INTEREST), known,
                null, byWho, "from whom", openLines));
        column.getChildren().add(line(bank, "Paid to savers", Bank.Line.SAVERS, known, true));
        column.getChildren().add(line(bank, "Paid for borrowing overnight from the central bank",
                Bank.Line.WINDOW, known, true));
        column.getChildren().add(total(bank, "Net interest income", Bank.Line.NET_INTEREST, known));

        VBox fees = new VBox(0);
        fees.getChildren().addAll(
                line(bank, "On the families' accounts", Bank.Line.ACCOUNT_FEES, known, false),
                line(bank, "On the businesses' new loans", Bank.Line.LOAN_FEES_PAID, known, false),
                line(bank, "On the families' new borrowing, added to what they owe",
                        Bank.Line.LOAN_FEES_OWED, known, false),
                line(bank, "Underwriting the businesses' bonds", Bank.Line.UNDERWRITING, known, false));
        column.getChildren().add(bookLine("Fees",
                bank.thisMonth(Bank.Line.FEES), bank.lastMonth(Bank.Line.FEES), known,
                null, fees, "on what", openLines));

        /*
         * PROVISIONS, "money set aside for loans expected to go bad" (0.7.8):
         * the allowance's move and whatever the month wrote off that it had
         * not set aside. Opened into the three things it is - this month's.
         */
        VBox provided = new VBox(0);
        double charged = bank.getProvisionCharge();
        provided.getChildren().add(statementLine(charged >= 0 ? "Set aside against the loans, this month"
                        : "Released, the borrowers having recovered",
                signed(charged, false), Palette.TEXT_MUTED));
        provided.getChildren().add(statementLine("Written off against what was set aside",
                moneyFull(bank.getAllowanceUsed()), Palette.TEXT_MUTED));
        provided.getChildren().add(statementLine("Written off beyond it",
                moneyFull(bank.getWriteOffsBeyondAllowance()), Palette.TEXT_MUTED));
        provided.getChildren().add(statementNote(String.format(
                "%s It holds %s now.", PROVISIONS_INFO, moneyFull(bank.getAllowance()))));
        column.getChildren().add(bookLine("Provisions for loans expected to go bad",
                -bank.thisMonth(Bank.Line.PROVISIONS), -bank.lastMonth(Bank.Line.PROVISIONS), known,
                null, provided, "what", openLines));

        column.getChildren().add(bookLine("The trading desk",
                bank.thisMonth(Bank.Line.TRADING), bank.lastMonth(Bank.Line.TRADING), known,
                null, deskDetail(bank), "what it did", openLines));
        if (Math.abs(bank.thisMonth(Bank.Line.PAPER_GAINS)) > 1e-9
                || Math.abs(bank.lastMonth(Bank.Line.PAPER_GAINS)) > 1e-9) {
            column.getChildren().add(line(bank, "Gains on the city's paper that changed hands",
                    Bank.Line.PAPER_GAINS, known, false));
        }
        if (Math.abs(bank.thisMonth(Bank.Line.BOND_GAINS)) > 1e-9
                || Math.abs(bank.lastMonth(Bank.Line.BOND_GAINS)) > 1e-9) {
            column.getChildren().add(line(bank, "Gains on the businesses' bonds it sold or was repaid",
                    Bank.Line.BOND_GAINS, known, false));
        }

        VBox running = new VBox(0);
        running.getChildren().addAll(
                line(bank, "Its staff", Bank.Line.PAYROLL, known, true),
                line(bank, "Its branches' upkeep", Bank.Line.UPKEEP, known, true));
        column.getChildren().add(bookLine("Staff and branches",
                -bank.thisMonth(Bank.Line.COSTS), -bank.lastMonth(Bank.Line.COSTS), known,
                null, running, "what", openLines));
        column.getChildren().add(total(bank, "Profit before tax", Bank.Line.PRE_TAX, known));
        column.getChildren().add(line(bank, "Tax", Bank.Line.TAX, known, true));
        column.getChildren().add(total(bank, "What it kept", Bank.Line.NET, known));
        column.getChildren().add(statementNote(String.format(
                "Tax is paid a month in arrears, on last month's %s of profit: the city's take is "
                + "struck before the bank knows what it made.", money(bank.getProfitLastMonth()))));

        /* ---------------------------- and what it did with it ---------------------------- */
        column.getChildren().add(subHead("And what it did with it"));
        column.getChildren().add(line(bank, "Paid to its shareholders", Bank.Line.DIVIDENDS, known, true));
        column.getChildren().add(line(bank, "Its own shares, bought back", Bank.Line.BUYBACKS, known, true));
        column.getChildren().add(total(bank, "Kept in the bank", Bank.Line.RETAINED, known));
        column.getChildren().add(statementNote(
                "Its owners are paid on last month's profit, by its capital - " + bank.payoutDecision() + "."));

        /* ---------------------------- the last twelve months ---------------------------- */
        int n = bank.monthsInYear();
        column.getChildren().add(statementHead(n >= Bank.YEAR_MONTHS ? "The last twelve months"
                : n <= 1 ? "Its first month on record" : "The " + n + " months on record"));
        year(column, bank, "Net interest income", Bank.Line.NET_INTEREST, false);
        year(column, bank, "Fees", Bank.Line.FEES, false);
        year(column, bank, "Provisions", Bank.Line.PROVISIONS, true);
        year(column, bank, "The trading desk", Bank.Line.TRADING, false);
        year(column, bank, "Gains on the city's paper", Bank.Line.PAPER_GAINS, false);
        year(column, bank, "Gains on the businesses' bonds", Bank.Line.BOND_GAINS, false);
        year(column, bank, "Staff and branches", Bank.Line.COSTS, true);
        column.getChildren().add(statementTotal("Profit before tax",
                moneyFull(bank.overYear(Bank.Line.PRE_TAX)), bank.overYear(Bank.Line.PRE_TAX) < 0 ? Palette.BAD : null));
        year(column, bank, "Tax", Bank.Line.TAX, true);
        column.getChildren().add(statementTotal("What it kept",
                moneyFull(bank.overYear(Bank.Line.NET)), bank.overYear(Bank.Line.NET) < 0 ? Palette.BAD : null));
        year(column, bank, "Paid to its shareholders", Bank.Line.DIVIDENDS, true);
        year(column, bank, "Its own shares, bought back", Bank.Line.BUYBACKS, true);
        column.getChildren().add(statementTotal("Kept in the bank",
                moneyFull(bank.overYear(Bank.Line.RETAINED)), null));

        /* ------------------------------- as ratios ------------------------------- */
        column.getChildren().add(subHead("Read as ratios, over " + yearWords(bank)));
        double nim = bank.netInterestMarginOverYear();
        column.getChildren().add(statementLine("Net interest margin, on what it lent",
                ratePerYear(nim), nim < 0 ? Palette.BAD : null));
        column.getChildren().add(statementLine("...this month alone", ratePerYear(bank.netInterestMargin()),
                Palette.TEXT_MUTED));
        double costs = bank.costShareOverYear();
        column.getChildren().add(statementLine("Staff and branches, of what it earns",
                Double.isNaN(costs) ? "—" : share1(costs),
                Double.isNaN(costs) || costs > 1 ? Palette.BAD : null));
        double roe = bank.returnOnEquityOverYear();
        column.getChildren().add(statementLine("Return on its equity", ratePerYear(roe),
                roe < 0 ? Palette.BAD : roe < Bank.requiredReturn() ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("...this month alone", ratePerYear(bank.returnOnEquity()),
                Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("...and what its owners want", ratePerYear(Bank.requiredReturn()),
                Palette.TEXT_MUTED));
        column.getChildren().add(statementNote(RATIOS_INFO));
        return column;
    }

    /** One statement line, this month and last - negated for money going out. */
    HBox line(Bank bank, String label, Bank.Line which, boolean known, boolean out) {
        double now = bank.thisMonth(which), then = bank.lastMonth(which);
        return bookLine(label, out ? -now : now, out ? -then : then, known, null);
    }

    /** One total, this month and last. */
    VBox total(Bank bank, String label, Bank.Line which, boolean known) {
        double now = bank.thisMonth(which);
        return bookTotal(label, now, bank.lastMonth(which), known, now < 0 ? Palette.BAD : null);
    }

    /** One line of the year, negated for money going out. */
    void year(VBox column, Bank bank, String label, Bank.Line which, boolean out) {
        double v = bank.overYear(which);
        column.getChildren().add(statementLine(label, moneyFull(out ? -v : v)));
    }

    /**
     * THE TRADING DESK, opened (2026-09-18, and so it foots). Jerus: "the
     * bank, just explain to me the trading desk, cause a bunch of times it's
     * losing billions of dollars due to the trading desk." The lines sum to
     * the figure above them - the re-mark of what the desk holds among them
     * (BankCheck asserts it) - and the bank's own shares are not among them,
     * which are capital since 0.7.8. Under "details" since 0.7.33, verbatim;
     * the desk's card draws the same figures.
     */
    VBox deskDetail(Bank bank) {
        VBox desk = new VBox(0);
        Exchange exchange = ui.game.getExchange();
        Equity register = ui.game.getEquity();
        double reMark = bank.getMarkChange();
        desk.getChildren().add(statementLine("Sold to the households",
                moneyFull(exchange.deskSoldToHouseholds()), Palette.TEXT_MUTED));
        desk.getChildren().add(statementLine("Sold abroad",
                moneyFull(exchange.deskSoldAbroad()), Palette.TEXT_MUTED));
        desk.getChildren().add(statementLine("Bought from the households",
                signed(exchange.deskBoughtFromHouseholds(), true), Palette.TEXT_MUTED));
        desk.getChildren().add(statementLine("Bought from abroad",
                signed(exchange.deskBoughtFromAbroad(), true), Palette.TEXT_MUTED));
        if (exchange.getEmigrantsPaid() > 0) {
            desk.getChildren().add(statementLine("...of which from families leaving the city",
                    moneyFull(exchange.getEmigrantsPaid()), Palette.TEXT_MUTED));
        }
        desk.getChildren().add(statementLine("Dividends on what it holds",
                moneyFull(register.getDividendDeskThisMonth()), Palette.TEXT_MUTED));
        desk.getChildren().add(statementLine("Tendered into buybacks",
                moneyFull(exchange.getBuybackToDesk()), Palette.TEXT_MUTED));
        desk.getChildren().add(statementLine("Re-marked what it holds",
                signed(reMark, false), reMark < 0 ? Palette.WARN : Palette.TEXT_MUTED));
        desk.getChildren().add(statementLine("What it holds, at the mark",
                moneyFull(bank.getSecurities()), Palette.TEXT_MUTED));
        /*
         * ...AGAINST ITS CAPS (0.7.12 round 3): its book and its largest
         * holding at fair value over the bank's equity, beside the limits its
         * bid stops at. Past a cap when what it already held has come to
         * outweigh it - the bank's equity fallen, or the holding's fair value
         * risen; it buys nothing more there. Since round 4 what is over them
         * is on offer at fair value (Exchange.deskExcess()), and each line
         * says how much of it still rests on the book.
         */
        double largest = 0;
        String largestName = null;
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            if (c == Equity.BANK) continue;
            double s = exchange.deskPositionShare(register, c);
            if (s > largest) { largest = s; largestName = Equity.COMPANIES[c]; }
        }
        double bookShare = exchange.deskBookShare(register);
        double onOffer = exchange.deskExcessOnOffer();
        desk.getChildren().add(statementLine("...at fair value, of the bank's equity",
                String.format("%.0f%% of a %.0f%% cap%s", bookShare * 100, Exchange.BOOK_LIMIT * 100,
                        onOffer > 0 ? ", " + tightMoney(toDollars(onOffer), false) + " over it on offer at fair value" : ""),
                bookShare > Exchange.BOOK_LIMIT ? Palette.WARN : Palette.TEXT_MUTED));
        if (largestName != null) {
            double itsOffer = exchange.deskExcessOnOffer(Equity.indexOf(largestName));
            desk.getChildren().add(statementLine("...its largest holding, " + largestName,
                    String.format("%.0f%% of a %.0f%% cap%s", largest * 100, Exchange.POSITION_LIMIT * 100,
                            itsOffer > 0 ? ", " + tightMoney(toDollars(itsOffer), false) + " of it on offer at fair value" : ""),
                    largest > Exchange.POSITION_LIMIT ? Palette.WARN : Palette.TEXT_MUTED));
        }
        StringBuilder positions = new StringBuilder();
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            double held = register.deskShare(c);
            if (held <= 0) continue;
            if (positions.length() > 0) positions.append(", ");
            positions.append(String.format("%s %.1f%% of the company at %s",
                    Equity.COMPANIES[c], held * 100, tightMoney(toDollars(exchange.mark(c)), false)));
        }
        desk.getChildren().add(statementNote(positions.length() == 0
                ? "The desk holds nothing. It bids for every company a little under what the register says"
                  + " a share is worth, for what its capital carries, and asks a little over it for what it holds."
                : "On the desk: " + positions + ". Carried at the last trade or the register's value, whichever"
                  + " is lower - the desk does not mark its own book up on a price nobody has paid yet."));
        /*
         * ITS ORDERS, on the book since 0.7.12 round 2: one participant among
         * the others, bidding what its capital carries and asking what it
         * holds - nobody obliged to meet it, and it obliged to meet nobody.
         */
        StringBuilder orders = new StringBuilder();
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            double bid = exchange.deskResting(c, OrderBook.Side.BUY), ask = exchange.deskResting(c, OrderBook.Side.SELL);
            if (!(bid > 0) && !(ask > 0)) continue;
            if (orders.length() > 0) orders.append("; ");
            orders.append(Equity.COMPANIES[c]).append(':');
            if (bid > 0) orders.append(String.format(" bids for %,.0f at %s", bid,
                    tightMoney(toDollars(exchange.bestBidOf(c, Exchange.DESK)), false)));
            if (ask > 0) orders.append(String.format("%s asks %,.0f at %s", bid > 0 ? "," : "", ask,
                    tightMoney(toDollars(exchange.bestAskOf(c, Exchange.DESK)), false)));
        }
        desk.getChildren().add(statementNote(orders.length() == 0
                ? (exchange.isOpen() ? "Nothing of its own rests on the book." : "It posts nothing: it has no capital to trade with.")
                : "Its orders resting on the book: " + orders + "."));
        if (bank.getTradingIncome() < 0 && reMark <= bank.getTradingIncome() / 2) {
            desk.getChildren().add(statementNote(String.format(
                    "%s of the %s lost is the re-mark, not the trading: shares bought above the register's"
                    + " value are marked down the day they are bought, and a company whose value falls"
                    + " marks down everything the desk holds in it.",
                    moneyFull(-reMark), moneyFull(-bank.getTradingIncome()))));
        }
        return desk;
    }

    /* =====================================================================
       BALANCE SHEET (0.7.13)

       Jerus: "in the bank section, i should be able to see a proper balance
       sheet, detailed" - "New page, vs a year ago". What the bank's own
       totalAssets() and totalLiabilities() sum, line by line (Bank.Sheet),
       this month and twelve months back (the year of sheets it files at the
       top of every month), each line opening into what it is made of; its
       equity as the model books it, the residual, shown since round 2 as
       paid-in capital and retained earnings - and the city's preferred since
       0.7.14 - ("—" for both on a save from before the bank kept them); and,
       beside the sheet, the city's own deposits, the memorandum the model
       keeps them as (Bank, THE THREE STATEMENTS). It foots in both columns,
       and "Not accounted for" is a red card at the top of every page when it
       does not.

       DRAWN SINCE 0.7.33 (the spec's D6): what it owns and what it owes and
       its owners' as TWO BARS ON ONE SCALE, the allowance a tick at the net
       total rather than a segment, a year ago a tick on each, a failed
       bank's hole a red outline; every line in the key under them with its
       move on a year ago, its sentence behind an (i), the loans and the
       interim financing opening by sector in place. The memorandum deposits
       are NOT on that scale - 73 times the sheet in the 2,400-month city -
       and have a card of their own. The statement itself is under
       "details", verbatim.
       ===================================================================== */

    void sheetPage(VBox page) {
        Bank bank = ui.game.getBank();
        page.getChildren().add(sheetHero(bank));
        page.getChildren().add(row(besideTheSheet(bank), equityParts(bank)));
        VBox statement = fold("sheet:statement", "the balance sheet, line by line, this month and a year ago",
                () -> sheetStatement(bank));
        targets.put(DETAILS, statement);
        page.getChildren().add(statement);
    }

    /** One line of the sheet as the bars and the key draw it: the line, its name, its colour, whether it is outlined. */
    record SheetPart(Bank.Sheet line, String name, String colour, boolean ghost) { }

    /** What it owns, in the bars' order (the spec's section 3): the businesses' loans first, its reserves last; the allowance is the net tick. */
    static final List<SheetPart> OWNS = List.of(
            new SheetPart(Bank.Sheet.BUSINESS_LOANS, "Loans to the businesses", Palette.BUSINESS, false),
            new SheetPart(Bank.Sheet.INTERIM, "Interim financing", Palette.BUSINESS_DARK, false),
            new SheetPart(Bank.Sheet.MORTGAGES, "The landlords' insured mortgages", Palette.BUILDING, false),
            new SheetPart(Bank.Sheet.FAMILIES, "The families' credit lines", Palette.PEOPLE, false),
            new SheetPart(Bank.Sheet.CARRY, "Lent to the carry trade", Palette.RAMP_REST, false),
            new SheetPart(Bank.Sheet.CITY_PAPER, "The city's paper", Palette.MONEY, false),
            new SheetPart(Bank.Sheet.BONDS, "The businesses' bonds it holds", Palette.BUSINESS_LIGHT, false),
            new SheetPart(Bank.Sheet.DESK, "The trading desk's shares, at their mark", Palette.CATEGORIES[4], false),
            new SheetPart(Bank.Sheet.RESERVES, "Reserves at the central bank", Palette.MONEY_LIGHT, false));

    /** ...and what it owes, then its owners' (equity last). */
    static final List<SheetPart> OWES = List.of(
            new SheetPart(Bank.Sheet.DEPOSIT_FUNDING, "Lent past its own cash, on its deposits", Palette.LADDER[1], false),
            new SheetPart(Bank.Sheet.WINDOW, "Borrowed at the central bank's window", Palette.MONEY_DARK, false),
            new SheetPart(Bank.Sheet.FOREIGN_DEPOSITS, "Deposits from abroad", Palette.RAMP_REST, false),
            new SheetPart(Bank.Sheet.DESK_SHORT, "Shares the desk has sold short", Palette.RAMP_REST, true),
            new SheetPart(Bank.Sheet.UNEARNED_DISCOUNT, "The discount on the city's paper, not yet earned",
                    Palette.LADDER[0], false));

    /** The two bars worked out without drawing them (pure: the probe reads them): their segments and ticks, their words, and the one scale. */
    record SheetBars(List<Segment> owns, List<Tick> ownsTicks, List<Segment> owes, List<Tick> owesTicks,
                     double scale, String ownsWords, String owesWords) { }

    /** WHAT IT OWNS · WHAT IT OWES, AND WHAT IS ITS OWNERS' (pure; the spec's D6). */
    SheetBars sheetSegments() {
        Bank bank = ui.game.getBank();
        boolean known = bank.knowsYearAgo();
        double assets = bank.sheet(Bank.Sheet.ASSETS);
        double liabilities = bank.sheet(Bank.Sheet.LIABILITIES);
        double equity = bank.sheet(Bank.Sheet.EQUITY);
        double allowance = bank.sheet(Bank.Sheet.ALLOWANCE);

        List<Segment> owns = new ArrayList<>();
        double gross = 0;
        for (SheetPart p : OWNS) {
            double a = bank.sheet(p.line());
            if (!(a > 0)) continue;
            gross += a;
            owns.add(seg(a, p.colour(), partTip(p.name(), a, assets)));
        }
        if (equity < 0) {
            owns.add(new Segment(-equity, Palette.BAD, true, null, "the hole", "the hole: what it owes past what it owns\n"
                    + mFull(-equity), null));
        }
        List<Tick> ownsTicks = new ArrayList<>();
        if (allowance < 0) {
            ownsTicks.add(tick(assets, Palette.TEXT_HEAD, "less set aside " + m(allowance),
                    "What it owns, net of what it has set aside for loans expected to go bad\n" + mFull(assets)));
        }
        if (known) {
            ownsTicks.add(tick(bank.yearAgo(Bank.Sheet.ASSETS), Palette.TEXT_MUTED, null,
                    "a year ago\n" + mFull(bank.yearAgo(Bank.Sheet.ASSETS))));
        }

        List<Segment> owes = new ArrayList<>();
        double owed = 0;
        for (SheetPart p : OWES) {
            double a = bank.sheet(p.line());
            if (!(a > 0)) continue;
            owed += a;
            owes.add(new Segment(a, p.colour(), p.ghost(), null, null, partTip(p.name(), a, liabilities), null));
        }
        if (equity > 0) {
            String parts = bank.knowsEquitySplit()
                    ? String.format("\nthe city's preferred %s · paid in %s · retained %s",
                            mFull(bank.sheet(Bank.Sheet.PREFERRED)), mFull(bank.sheet(Bank.Sheet.PAID_IN)),
                            mFull(bank.sheet(Bank.Sheet.RETAINED)))
                    : "";
            owes.add(seg(equity, Palette.MONEY, "its owners'", "Its equity: its owners'\n" + mFull(equity) + parts));
            owed += equity;
        }
        List<Tick> owesTicks = new ArrayList<>();
        if (known) {
            owesTicks.add(tick(bank.yearAgoLiabilitiesAndEquity(), Palette.TEXT_MUTED, null,
                    "a year ago\n" + mFull(bank.yearAgoLiabilitiesAndEquity())));
        }
        double scale = Math.max(gross + Math.max(0, -equity), owed);
        if (known) scale = Math.max(scale, Math.max(bank.yearAgo(Bank.Sheet.ASSETS), bank.yearAgoLiabilitiesAndEquity()));
        String ownsWords = "Owns " + m(assets) + (known ? " · a year ago " + m(bank.yearAgo(Bank.Sheet.ASSETS)) : "");
        String owesWords = "Owes " + m(liabilities) + " · " + (equity < 0 ? "its hole " + m(-equity)
                : "its owners' " + m(equity))
                + (known ? " · a year ago " + m(bank.yearAgoLiabilitiesAndEquity()) : "");
        return new SheetBars(owns, ownsTicks, owes, owesTicks, scale * 1.01, ownsWords, owesWords);
    }

    /** The hero's (i): what the two bars are, and the year ago's note when it is not on file (the spec's B7). */
    String sheetInfo(Bank bank) {
        return "What it owns, over what it owes and what is left for its owners, on one scale - the same money "
                + "both ways. The loans are drawn whole and what it has set aside against them is the tick at the net "
                + "total; a year ago is a grey tick on each bar. A bank that has lost more than it owns has a hole, "
                + "outlined red. The city's own deposits are beside the sheet, not on it: the card below says why."
                + (bank.knowsYearAgo() ? "" : " A year ago is kept from the first month played in this build, so it "
                + "is not drawn until the city has lived a year of them.");
    }

    /** The hero card: the two bars and, under them, every line in two columns. */
    VBox sheetHero(Bank bank) {
        SheetBars b = sheetSegments();
        VBox bars = balanceBars(b.ownsWords(), b.owns(), b.ownsTicks(), b.owesWords(), b.owes(), b.owesTicks(),
                b.scale(), 220, 22);
        boolean known = bank.knowsYearAgo();
        VBox owns = new VBox(2, muted("WHAT IT OWNS"));
        for (SheetPart p : OWNS) owns.getChildren().add(legendRow(bank, p, known));
        owns.getChildren().add(legendRow(bank,
                new SheetPart(Bank.Sheet.ALLOWANCE, "Less what it has set aside for loans expected to go bad",
                        Palette.TEXT_MUTED, true), known));
        owns.getChildren().add(legendTotal("Total assets", bank.sheet(Bank.Sheet.ASSETS), bank.yearAgo(Bank.Sheet.ASSETS), known));
        VBox owes = new VBox(2, muted("WHAT IT OWES, AND WHAT IS ITS OWNERS'"));
        for (SheetPart p : OWES) owes.getChildren().add(legendRow(bank, p, known));
        owes.getChildren().add(legendTotal("Total liabilities", bank.sheet(Bank.Sheet.LIABILITIES),
                bank.yearAgo(Bank.Sheet.LIABILITIES), known));
        owes.getChildren().add(legendRow(bank, new SheetPart(Bank.Sheet.EQUITY, "Its equity", Palette.MONEY, false), known));
        owes.getChildren().add(legendTotal("Liabilities and equity", bank.liabilitiesAndEquity(),
                bank.yearAgoLiabilitiesAndEquity(), known));
        GridPane key = equalColumns(2, 24);
        key.add(owns, 0, 0);
        key.add(owes, 1, 0);
        return card(cardHead(Icons.FINANCES, Palette.MONEY, "WHAT IT OWNS · WHAT IT OWES, AND WHAT IS ITS OWNERS'",
                        sheetInfo(bank), quiet("as at " + CityCalendar.format(ui.game.getMonth()))),
                bars, key);
    }

    /** A line's move on a year ago: "▲ $1.2M", "▼ $3.4M", nothing when none is on file or it did not move. */
    static String moved(double now, double then, boolean known) {
        if (!known) return "";
        double d = now - then;
        if (Math.abs(d) < .5) return "";
        return (d > 0 ? "▲ " : "▼ ") + money(Math.abs(d));
    }

    /** One line of the key: its swatch, its name with its sentence behind an (i), its figure now and its move on a year ago; the loans and the interim financing open by sector in place. */
    Node legendRow(Bank bank, SheetPart p, boolean known) {
        double now = bank.sheet(p.line()), then = bank.yearAgo(p.line());
        Region sw = new Region();
        sw.setMinSize(10, 10);
        sw.setPrefSize(10, 10);
        sw.setMaxSize(10, 10);
        sw.setStyle(p.ghost() ? "-fx-background-color: " + p.colour() + "33; -fx-border-color: " + p.colour() + "; -fx-border-width: 1;"
                : "-fx-background-color: " + p.colour() + "; -fx-background-radius: 2;");
        VBox detail = bySector(bank, p.line(), known);
        String key = "sheet:" + p.line().name();
        boolean opens = detail != null;
        boolean open = opens && openLines.contains(key);
        Label name = words((opens ? (open ? "▾ " : "▸ ") : "") + p.name(), Palette.SIZE_LABEL + 1,
                opens ? Palette.ACCENT : Palette.TEXT_LABEL);
        if (opens) {
            name.setStyle(name.getStyle() + " -fx-cursor: hand;");
            name.setOnMouseClicked(e -> {
                if (!openLines.remove(key)) openLines.add(key);
                showBankMenu();
            });
        }
        HBox.setHgrow(name, Priority.ALWAYS);
        name.setMaxWidth(Double.MAX_VALUE);
        HBox row = new HBox(Palette.GAP, sw, name);
        String said = sheetSaid(bank, p.line());
        if (said != null) row.getChildren().add(infoButton(said, true));
        Label mv = figure(moved(now, then, known), Palette.SIZE_LABEL, Palette.TEXT_MUTED);
        mv.setMinWidth(70);
        mv.setAlignment(Pos.CENTER_RIGHT);
        row.getChildren().addAll(figure(mFull(now), Palette.SIZE_LABEL + 1,
                p.line() == Bank.Sheet.EQUITY && now < 0 ? Palette.BAD : Palette.TEXT_HEAD), mv);
        row.setAlignment(Pos.CENTER_LEFT);
        if (!open) return row;
        VBox both = new VBox(2, row, detail);
        VBox.setMargin(detail, new javafx.geometry.Insets(0, 0, 4, 18));
        return both;
    }

    /** A total under a column of the key. */
    Node legendTotal(String label, double now, double then, boolean known) {
        Label name = words(label, Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD);
        HBox.setHgrow(name, Priority.ALWAYS);
        name.setMaxWidth(Double.MAX_VALUE);
        Label mv = figure(moved(now, then, known), Palette.SIZE_LABEL, Palette.TEXT_MUTED);
        mv.setMinWidth(70);
        mv.setAlignment(Pos.CENTER_RIGHT);
        HBox row = new HBox(Palette.GAP, name, figure(mFull(now), Palette.SIZE_LABEL + 1,
                now < 0 ? Palette.BAD : Palette.TEXT_HEAD), mv);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 1 0 0 0; -fx-padding: 3 0 4 18;");
        return row;
    }

    /** The loans or the interim financing by sector, this month and a year ago - null for a line that does not open. */
    VBox bySector(Bank bank, Bank.Sheet line, boolean known) {
        if (line != Bank.Sheet.BUSINESS_LOANS && line != Bank.Sheet.INTERIM) return null;
        VBox out = new VBox(0);
        for (int s = 0; s < Sectors.KEYS.length; s++) {
            double now = line == Bank.Sheet.INTERIM ? bank.getInterimToSector(s) : bank.getLoansToSector(s);
            double then = line == Bank.Sheet.INTERIM ? bank.yearAgoInterimToSector(s) : bank.yearAgoLoansToSector(s);
            if (Math.abs(now) > 1e-9 || (known && Math.abs(then) > 1e-9)) {
                out.getChildren().add(bookLine(Sectors.KEYS[s], now, then, known, null));
            }
        }
        if (out.getChildren().isEmpty()) {
            out.getChildren().add(statementNote(line == Bank.Sheet.INTERIM
                    ? "Lent to a sector the month it defaulted, for the bills its write-down left unpaid, ranked ahead "
                      + "of the rest of its debt. None is outstanding."
                    : "No business owes it a loan."));
        }
        return out;
    }

    /** Each line's sentence (the statement's said()s, Balance sheet 1029-1130): the key's (i)s and the statement's openings. */
    String sheetSaid(Bank bank, Bank.Sheet line) {
        Equity register = ui.game.getEquity();
        return switch (line) {
            case RESERVES -> String.format("The cash it is not lending, held at the central bank and paid the policy "
                    + "rate - %s this month. Its placements abroad came home to the central bank in 0.7.0; it holds "
                    + "none.", moneyFull(bank.getPlacementIncome()));
            case BUSINESS_LOANS -> "What the businesses owe it outside the landlords' insured mortgages and the interim "
                    + "financing. It opens by sector; Lending has each borrower.";
            case INTERIM -> "Lent to a sector the month it defaulted, for the bills its write-down left unpaid, ranked "
                    + "ahead of the rest of its debt.";
            case MORTGAGES -> "Lent to the landlords for new housing, and insured by the city: what a default would cost "
                    + "the bank on them, the insurance pays.";
            case FAMILIES -> "What the households owe on their credit lines.";
            case CARRY -> "Lent to foreigners who took it abroad for the spread; they owe it back.";
            case CITY_PAPER -> "Its share of the city's own paper, at face - the households and the central bank hold the "
                    + "rest. The discount it paid under face and has not yet earned is owed below.";
            case BONDS -> String.format("At what they cost it; their face is %s.", moneyFull(bank.getBondFace()));
            case ALLOWANCE -> "The allowance: a year's expected loss on a sound borrower, what a default would cost on one "
                    + "in trouble.";
            case DESK -> "Other companies' shares the desk holds, at the last trade. Its own shares are not among them: "
                    + "bought back, they are cancelled.";
            case DEPOSIT_FUNDING -> "What it has lent past its own cash, as far as the deposits its branches gather cover "
                    + "it: the city's savings, lent back out - the cheap tranche of its funding.";
            case WINDOW -> "The rest of what it has lent past its own cash, borrowed overnight at the policy rate plus the "
                    + "window's penalty.";
            case FOREIGN_DEPOSITS -> "Hot money: the world's deposits, which can leave on no notice. The city's own are "
                    + "beside the sheet, below.";
            case DESK_SHORT -> "Shares the desk owes, at their mark.";
            case UNEARNED_DISCOUNT -> "What it paid under face for the city's paper, which it earns over the paper's life "
                    + "rather than the day it settles.";
            case EQUITY -> "What is left for its owners: what it owns less what it owes - the city's preferred, the "
                    + "capital paid in and the earnings it kept, in ITS EQUITY IN PARTS, below.";
            case PREFERRED -> String.format("The senior preferred the city bought when the bank was under its minimum, "
                    + "at par: %s in %d block(s), ranking ahead of the common. It counts in the bank's capital. Its "
                    + "cumulative dividend owed and unpaid is %s, which is not on the sheet until it is paid.",
                    moneyFull(bank.preferredOutstanding()), bank.getPreferred().size(),
                    moneyFull(bank.getPreferredArrears()));
            case PAID_IN -> String.format("What its owners and the city have put in: the capital its shareholders paid "
                    + "at its offerings - %s at home and %s abroad over its life, a new branch's capital among it - and "
                    + "the new shares it issued, less what it paid for its own shares bought back, and %s the city paid "
                    + "in resolutions, for every share. A resolution writes the old owners' paid-in capital off against "
                    + "the losses.",
                    moneyFull(register.getLifetimeRaisedHome(Equity.BANK)),
                    moneyFull(register.getLifetimeRaisedAbroad(Equity.BANK)),
                    moneyFull(bank.getBailoutsLifetime()));
            case RETAINED -> String.format("Everything else: the profit it has kept, less its dividends - the city's "
                    + "preferred's among them - and its losses; the old owners' paid-in capital and the city's preferred, "
                    + "written off against the hole when a resolution wiped them out; the %s its creditors absorbed in a "
                    + "failure before 0.7.14, while its owners kept their shares; the gains and losses on the city's "
                    + "paper the treasury bought back from it; and the settlement that opened its first branch on its "
                    + "owners' capital. The Capital & owners page has this month's movement, cause by cause.",
                    moneyFull(bank.getResolutionLoss()));
            default -> null;
        };
    }

    /* ------------------------------ beside the sheet ------------------------------ */

    /** BESIDE THE SHEET's (i) (Balance sheet 1161). */
    static final String BESIDE_INFO = "Counted for what it can lend and not held on its sheet in full: the families' "
            + "savings and the businesses' tills are their own money in the city's accounts, and holding them here as "
            + "well would count every dollar twice. What it has lent on them is the sheet's first line owed; the world's "
            + "deposits are on it whole, because that money was not in the city until it came.";

    /** BESIDE THE SHEET: the city's own deposits as one bar, what it has lent on them, and how big they are against the sheet. */
    VBox besideTheSheet(Bank bank) {
        double families = bank.sheet(Bank.Sheet.HOUSEHOLD_DEPOSITS), businesses = bank.sheet(Bank.Sheet.SECTOR_DEPOSITS);
        double local = bank.localDeposits();
        Bank.Ladder l = bank.ladder(ui.game.getDebtManager().getPolicyRate());
        boolean known = bank.knowsYearAgo();
        VBox c = card(cardHead(Icons.POPULATION, Palette.PEOPLE, "BESIDE THE SHEET", BESIDE_INFO, null),
                big(m(local), Palette.TEXT_HEAD),
                line("the city's own deposits with it"),
                segmentBar(List.of(seg(Math.max(0, families), Palette.PEOPLE, partTip("the families' savings", families, local)),
                        seg(Math.max(0, businesses), Palette.BUSINESS, partTip("the businesses' cash in credit", businesses, local))),
                        0, List.of(), 0, 14),
                keyRow(swatch(Palette.PEOPLE, "families " + m(families), false),
                        swatch(Palette.BUSINESS, "businesses " + m(businesses), false)));
        if (bank.depositFunding() > 0) {
            c.getChildren().add(line(l.fundingPosition() >= 1
                    ? "lent " + m(bank.depositFunding()) + " past its own cash on them: all it gathered"
                    : String.format("lent %s past its own cash on them (%.0f%% of what it gathered)",
                            m(bank.depositFunding()), l.fundingPosition() * 100)));
        } else {
            c.getChildren().add(line("lent none of them: it lends from its own cash"));
        }
        if (bank.totalAssets() > 0) {
            c.getChildren().add(muted(String.format("%.1f× the sheet", local / bank.totalAssets())
                    + (known ? " · a year ago " + m(bank.yearAgo(Bank.Sheet.HOUSEHOLD_DEPOSITS)
                            + bank.yearAgo(Bank.Sheet.SECTOR_DEPOSITS)) : "")));
        }
        return c;
    }

    /* ------------------------------ its equity in parts ------------------------------ */

    /** ITS EQUITY IN PARTS: the city's preferred, paid in and retained, now and a year ago, on one scale; a part below nothing drawn leftward in the spent grey. */
    VBox equityParts(Bank bank) {
        boolean known = bank.knowsYearAgo();
        VBox c = card(cardHead(Icons.STAFF, Palette.MONEY, "ITS EQUITY IN PARTS",
                "Its equity in two parts since 0.7.13's round 2 - what was paid in and what it kept - and since 0.7.14 a "
                + "third, the city's preferred, ahead of the common. " + sheetSaid(bank, Bank.Sheet.PAID_IN) + " "
                + sheetSaid(bank, Bank.Sheet.RETAINED), null),
                big(m(bank.sheet(Bank.Sheet.EQUITY)), bank.sheet(Bank.Sheet.EQUITY) < 0 ? Palette.BAD : Palette.TEXT_HEAD));
        if (!bank.knowsEquitySplit()) {
            c.getChildren().add(line("This city was saved before the bank kept its equity in two parts, and they cannot be "
                    + "rebuilt: what its buybacks paid was never kept. Its equity is shown whole, what it owns less what "
                    + "it owes."));
            return c;
        }
        Bank.Sheet[] parts = {Bank.Sheet.PREFERRED, Bank.Sheet.PAID_IN, Bank.Sheet.RETAINED};
        String[] names = {"the city's preferred", "paid in", "retained"};
        double pos = 0, neg = 0;
        for (Bank.Sheet p : parts) {
            for (double v : new double[] {bank.sheet(p), known ? bank.yearAgo(p) : 0}) {
                pos = Math.max(pos, v);
                neg = Math.max(neg, -v);
            }
        }
        GridPane g = new GridPane();
        g.setHgap(Palette.GAP);
        g.setVgap(2);
        javafx.scene.layout.ColumnConstraints a = new javafx.scene.layout.ColumnConstraints(110);
        javafx.scene.layout.ColumnConstraints b = new javafx.scene.layout.ColumnConstraints();
        b.setHgrow(Priority.ALWAYS);
        javafx.scene.layout.ColumnConstraints f = new javafx.scene.layout.ColumnConstraints();
        f.setMinWidth(Region.USE_PREF_SIZE);
        g.getColumnConstraints().addAll(a, b, f);
        int r = 0;
        for (int i = 0; i < parts.length; i++) {
            double now = bank.sheet(parts[i]);
            HBox name = new HBox(4, words(names[i], Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
            String said = sheetSaid(bank, parts[i]);
            if (said != null) name.getChildren().add(infoButton(said, true));
            name.setAlignment(Pos.CENTER_LEFT);
            g.add(name, 0, r);
            g.add(new Pieces.RankBar(now, pos, neg, Palette.MONEY), 1, r);
            g.add(figure(mFull(now), Palette.SIZE_LABEL + 1, now < 0 ? Palette.TEXT_SPENT : Palette.TEXT_HEAD), 2, r);
            r++;
            if (known) {
                double then = bank.yearAgo(parts[i]);
                g.add(muted("a year ago"), 0, r);
                g.add(new Pieces.RankBar(then, pos, neg, Palette.MONEY_LIGHT), 1, r);
                g.add(figure(mFull(then), Palette.SIZE_LABEL, Palette.TEXT_MUTED), 2, r);
                r++;
            }
        }
        c.getChildren().add(g);
        return c;
    }

    /* ------------------------------ the statement, under details ------------------------------ */

    /** The balance sheet as the page printed it until 0.7.33, verbatim (the spec's B1-B7, D15). */
    VBox sheetStatement(Bank bank) {
        VBox column = column();
        boolean known = bank.knowsYearAgo();

        column.getChildren().add(statementHead("Its balance sheet"));
        column.getChildren().add(bookHead("as at " + CityCalendar.format(ui.game.getMonth()),
                "this month", "a year ago"));

        /* ------------------------------ what it owns ------------------------------ */
        column.getChildren().add(subHead("What it owns"));
        column.getChildren().add(sheetLine(bank, "Reserves at the central bank", Bank.Sheet.RESERVES, known,
                said(sheetSaid(bank, Bank.Sheet.RESERVES))));
        VBox bySector = bySector(bank, Bank.Sheet.BUSINESS_LOANS, known);
        column.getChildren().add(sheetLine(bank, "Loans to the businesses", Bank.Sheet.BUSINESS_LOANS, known,
                bySector, "by sector"));
        column.getChildren().add(sheetLine(bank, "Interim financing", Bank.Sheet.INTERIM, known,
                bySector(bank, Bank.Sheet.INTERIM, known), "by sector"));
        column.getChildren().add(sheetLine(bank, "The landlords' insured mortgages", Bank.Sheet.MORTGAGES, known,
                said(sheetSaid(bank, Bank.Sheet.MORTGAGES))));
        column.getChildren().add(sheetLine(bank, "The families' credit lines", Bank.Sheet.FAMILIES, known,
                said(sheetSaid(bank, Bank.Sheet.FAMILIES))));
        column.getChildren().add(sheetLine(bank, "Lent to the carry trade", Bank.Sheet.CARRY, known,
                said(sheetSaid(bank, Bank.Sheet.CARRY))));
        column.getChildren().add(sheetLine(bank, "The city's paper", Bank.Sheet.CITY_PAPER, known,
                said(sheetSaid(bank, Bank.Sheet.CITY_PAPER))));
        column.getChildren().add(sheetLine(bank, "The businesses' bonds it holds", Bank.Sheet.BONDS, known,
                said(sheetSaid(bank, Bank.Sheet.BONDS))));
        column.getChildren().add(sheetLine(bank, "Less what it has set aside for loans expected to go bad",
                Bank.Sheet.ALLOWANCE, known, said(sheetSaid(bank, Bank.Sheet.ALLOWANCE))));
        column.getChildren().add(sheetLine(bank, "The trading desk's shares, at their mark", Bank.Sheet.DESK, known,
                said(sheetSaid(bank, Bank.Sheet.DESK))));
        column.getChildren().add(bookTotal("Total assets", bank.sheet(Bank.Sheet.ASSETS),
                bank.yearAgo(Bank.Sheet.ASSETS), known, null));

        /* ------------------------------ what it owes ------------------------------ */
        column.getChildren().add(subHead("What it owes"));
        column.getChildren().add(sheetLine(bank, "Lent past its own cash, on its deposits",
                Bank.Sheet.DEPOSIT_FUNDING, known, said(sheetSaid(bank, Bank.Sheet.DEPOSIT_FUNDING))));
        column.getChildren().add(sheetLine(bank, "Borrowed at the central bank's window", Bank.Sheet.WINDOW, known,
                said(sheetSaid(bank, Bank.Sheet.WINDOW))));
        column.getChildren().add(sheetLine(bank, "Deposits from abroad", Bank.Sheet.FOREIGN_DEPOSITS, known,
                said(sheetSaid(bank, Bank.Sheet.FOREIGN_DEPOSITS))));
        column.getChildren().add(sheetLine(bank, "Shares the desk has sold short", Bank.Sheet.DESK_SHORT, known,
                said(sheetSaid(bank, Bank.Sheet.DESK_SHORT))));
        column.getChildren().add(sheetLine(bank, "The discount on the city's paper, not yet earned",
                Bank.Sheet.UNEARNED_DISCOUNT, known, said(sheetSaid(bank, Bank.Sheet.UNEARNED_DISCOUNT))));
        column.getChildren().add(bookTotal("Total liabilities", bank.sheet(Bank.Sheet.LIABILITIES),
                bank.yearAgo(Bank.Sheet.LIABILITIES), known, null));

        /* ------------------------------ what is left ------------------------------ */
        // Its equity in two parts since 0.7.13's round 2 (Bank, ITS EQUITY, IN
        // TWO PARTS); a save from before it kept neither, and reads "—" for both.
        column.getChildren().add(subHead("What is left for its owners"));
        boolean split = bank.knowsEquitySplit();
        // The city's preferred, ahead of the common (0.7.14): a line of its own, in the equity.
        column.getChildren().add(bookLine("Preferred shares (the city)",
                bank.sheet(Bank.Sheet.PREFERRED), bank.yearAgo(Bank.Sheet.PREFERRED), known, null,
                said(sheetSaid(bank, Bank.Sheet.PREFERRED)), "what", openLines));
        column.getChildren().add(bookLine("Paid-in capital",
                split ? bank.sheet(Bank.Sheet.PAID_IN) : Double.NaN,
                split ? bank.yearAgo(Bank.Sheet.PAID_IN) : Double.NaN, known, null,
                said(sheetSaid(bank, Bank.Sheet.PAID_IN)), "what", openLines));
        column.getChildren().add(bookLine("Retained earnings",
                split ? bank.sheet(Bank.Sheet.RETAINED) : Double.NaN,
                split ? bank.yearAgo(Bank.Sheet.RETAINED) : Double.NaN, known, null,
                said(sheetSaid(bank, Bank.Sheet.RETAINED)), "what", openLines));
        column.getChildren().add(bookTotal("Its equity", bank.sheet(Bank.Sheet.EQUITY),
                bank.yearAgo(Bank.Sheet.EQUITY), known, bank.isInsolvent() ? Palette.BAD : null));
        if (!split) {
            column.getChildren().add(statementNote("This city was saved before the bank kept its equity in two "
                    + "parts, and they cannot be rebuilt: what its buybacks paid was never kept. Its equity is "
                    + "shown whole, what it owns less what it owes."));
        }
        double off = bank.sheetResidual(), offThen = bank.yearAgoResidual();
        boolean unexplained = Math.abs(off) > .005 || (known && Math.abs(offThen) > .005);
        if (unexplained) {
            column.getChildren().add(bookLine("Not accounted for", off, offThen, known, Palette.BAD));
        }
        column.getChildren().add(bookTotal("Liabilities and equity", bank.liabilitiesAndEquity(),
                bank.yearAgoLiabilitiesAndEquity(), known, bank.isInsolvent() ? Palette.BAD : null));

        /* ------------------------------ beside the sheet ------------------------------ */
        column.getChildren().add(subHead("Beside the sheet"));
        column.getChildren().add(bookLine("Deposits the families hold with it",
                bank.sheet(Bank.Sheet.HOUSEHOLD_DEPOSITS), bank.yearAgo(Bank.Sheet.HOUSEHOLD_DEPOSITS), known, null));
        column.getChildren().add(bookLine("...and the businesses",
                bank.sheet(Bank.Sheet.SECTOR_DEPOSITS), bank.yearAgo(Bank.Sheet.SECTOR_DEPOSITS), known, null));
        column.getChildren().add(statementNote("Counted for what it can lend and not held on its sheet in "
                + "full: the families' savings and the businesses' tills are their own money in the city's "
                + "accounts, and holding them here as well would count every dollar twice. What it has lent "
                + "on them is the line above; the world's deposits are on it whole, because that money was "
                + "not in the city until it came."));
        if (!known) {
            column.getChildren().add(statementNote("A year ago is kept from the first month played in this "
                    + "build, so it reads — until the city has lived a year of them."));
        }
        return column;
    }

    /** One line of the balance sheet, this month and a year ago, opening into its detail. */
    VBox sheetLine(Bank bank, String label, Bank.Sheet line, boolean known, VBox detail) {
        return sheetLine(bank, label, line, known, detail, "what");
    }

    VBox sheetLine(Bank bank, String label, Bank.Sheet line, boolean known, VBox detail, String word) {
        return bookLine(label, bank.sheet(line), bank.yearAgo(line), known, null, detail, word, openLines);
    }

    /** A line's detail that is a sentence. */
    static VBox said(String text) {
        return new VBox(0, statementNote(text));
    }

    /* =====================================================================
       LENDING

       Who owes it, how sound is each borrower, and how is the next loan
       priced? Since 0.7.33 THE BOOK leads as one bar by borrower - the
       businesses' bonds it holds among them, so the bar foots to getBook()
       (the spec's B3: the old lines missed them) - then a card a borrower:
       a business with what it owes THE BANK and what of its bonds the bank
       holds (B9: the old table's "owed" was its loans and its bonds held by
       anybody), its leverage on a scale from nothing past the default point,
       its default rate, its stage and what is set aside and written off; the
       landlords, the families and the carry trade. Then what it has set aside
       and written off, and HOW THE NEXT LOAN IS PRICED - prime in its parts
       and a row a borrower, the concentration named (B4). The old tables are
       under "details", verbatim; the landlords' mortgages among the
       businesses since 0.7.11.
       ===================================================================== */

    void lendingPage(VBox page) {
        Bank bank = ui.game.getBank();
        page.getChildren().add(stanceRow(bank));
        page.getChildren().add(bookCard(bank));
        page.getChildren().add(borrowersSection(bank));
        VBox setAside = setAsideCard(bank);
        targets.put(SET_ASIDE, setAside);
        page.getChildren().add(row(setAside, writtenOffCard(bank)));
        VBox pricing = pricingCard(bank);
        targets.put(PRICING, pricing);
        page.getChildren().add(pricing);
        VBox tables = new VBox(4,
                fold("lending:tables", "the businesses one by one, who has stopped paying, what it lent this month, "
                        + "what a dollar of the book weighs, and what its concentration costs - the old tables",
                        () -> lendingTables(bank)),
                fold("lending:bonds", "the businesses' bonds it holds", () -> bondsHeld(bank)));
        targets.put(DETAILS, tables);
        page.getChildren().add(tables);
    }

    /** The capital-lets-it-lend note (Lending 1449), behind the stance chip's (i). */
    String stanceInfo(Bank bank) {
        return String.format("At or over its own target (%s) it lends freely. Between the %s minimum and the target a "
                + "borrower's new lending - a building, a new mortgage - may grow its debt a little each month, more the "
                + "nearer the target; under the minimum, none. Whatever its capital, while it stands it honours a "
                + "business's working-capital line: the cover of a short month, up to %.0f%% of what the business owns.",
                share1(bank.capitalTarget()), share1(Bank.CAPITAL_RATIO), BusinessDebtManager.MAX_LOAN_TO_ASSETS * 100);
    }

    /** What its capital lets it lend: Bank.lendingStance() as a chip and its line, and the growth it allows when it is finite. */
    HBox stanceRow(Bank bank) {
        String stance = bank.lendingStance();
        int cut = stance.indexOf(" - ");
        String word = (cut > 0 ? stance.substring(0, cut) : stance).toUpperCase();
        String rest = cut > 0 ? stance.substring(cut + 3) : "";
        double limit = bank.lendingLimit();
        if (!Double.isInfinite(limit) && limit > 0) rest += (rest.isEmpty() ? "" : " · ") + "≈" + m(limit) + " of growth across its book";
        HBox row = new HBox(Palette.GAP, chip(word, stanceTone(bank)), words(rest, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL),
                infoButton(stanceInfo(bank), true));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /* ------------------------------ the book ------------------------------ */

    /** One borrower's part of the book (pure). */
    record BookPart(String name, double amount, String colour) { }

    /** THE BOOK by borrower (pure): the sheet's loan lines, the bonds among them, so they foot to Bank.getBook() (B3). */
    List<BookPart> bookParts() {
        Bank bank = ui.game.getBank();
        List<BookPart> out = new ArrayList<>();
        Object[][] lines = {
                {"The businesses' loans", Bank.Sheet.BUSINESS_LOANS, Palette.BUSINESS},
                {"Interim financing", Bank.Sheet.INTERIM, Palette.BUSINESS_DARK},
                {"The businesses' bonds", Bank.Sheet.BONDS, Palette.BUSINESS_LIGHT},
                {"The landlords' insured mortgages", Bank.Sheet.MORTGAGES, Palette.BUILDING},
                {"The families", Bank.Sheet.FAMILIES, Palette.PEOPLE},
                {"The carry trade", Bank.Sheet.CARRY, Palette.RAMP_REST},
                {"The city's own paper", Bank.Sheet.CITY_PAPER, Palette.MONEY}};
        for (Object[] l : lines) {
            double a = bank.sheet((Bank.Sheet) l[1]);
            if (a > 0) out.add(new BookPart((String) l[0], a, (String) l[2]));
        }
        return out;
    }

    /** Who-owes-it note (Lending 1225), behind THE BOOK's (i). */
    static final String BOOK_INFO = "Everything it has lent, by who owes it: the businesses' loans and the bonds of theirs "
            + "it holds, at what they cost it; the interim financing lent after a default, ranked ahead of their other "
            + "debt; the landlords' insured mortgages; the families' credit lines; the carry trade; and the city's paper, "
            + "its bonds, which the bank buys. The carry trade is foreigners borrowing here to hold the money abroad while "
            + "the bank lends cheaper than the world pays.";

    VBox bookCard(Bank bank) {
        double book = bank.getBook();
        HBox head = cardHead(Icons.SECTOR, Palette.MONEY, "THE BOOK", BOOK_INFO,
                figure(m(book) + " on the book", Palette.SIZE_HEADING, Palette.TEXT_HEAD));
        if (book <= 0) return card(head, line("Nothing is lent out."));
        List<Segment> parts = new ArrayList<>();
        List<Node> key = new ArrayList<>();
        for (BookPart p : bookParts()) {
            parts.add(seg(p.amount(), p.colour(), partTip(p.name(), p.amount(), book)));
            key.add(swatch(p.colour(), p.name() + "  " + m(p.amount()) + " · " + share1(p.amount() / book), false));
        }
        return card(head, segmentBar(parts, book, List.of(), 0, 18), keyRow(key.toArray(new Node[0])));
    }

    /* ------------------------------ the borrowers ------------------------------ */

    /** One business as its card shows it (pure: the probe reads them). */
    record Borrower(String key, boolean shut, int blockedMonths, double rate, double spread, double owesBank,
                    double bonds, double bankHolds, double leverage, double defaultRate, int stage, double stageTwo,
                    double setAside, double writtenOff, boolean news, int wentUnder, double concentration) { }

    /** The businesses the old table listed (Lending 1243): owing, shut out, set aside against, or written off this month. */
    List<Borrower> borrowers() {
        Bank bank = ui.game.getBank();
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        BondMarket market = ui.game.getBondMarket();
        double exposure = bank.getConcentrationExposure();
        List<Borrower> out = new ArrayList<>();
        for (String name : Sectors.KEYS) {
            double owed = credit.getPrincipal(name);
            boolean shut = credit.isBorrowingBlocked(name);
            double lost = bank.getWrittenOff(name);
            if (owed <= 0 && !shut && bank.getSectorAllowance(name) <= 0 && lost <= 0) continue;
            Bank.Exposure x = bank.getExposure(name);
            out.add(new Borrower(name, shut, credit.getBlockedMonths(name), credit.getRate(name), credit.getSpread(name),
                    credit.getLoanPrincipal(name), credit.getBondPrincipal(name), market.bankCost(name),
                    credit.getLeverage(name), credit.getDefaultRate(name), bank.getStage(name),
                    bank.getStageTwoShare(name), bank.getSectorAllowance(name), lost, credit.defaultsAreNews(name),
                    credit.getRestructureCount(name),
                    exposure > 0 && x != null && x.amount() > 0 ? x.amount() / exposure : 0));
        }
        return out;
    }

    /** The businesses' note (Lending 1280): its short line on the section's head, and the whole behind its (i). */
    static String borrowersInfo() {
        double watch = Bank.SECTOR_WATCH_LEVERAGE, point = BusinessDebtManager.INSOLVENCY_TRIGGER;
        return String.format(
                "\"Pays\" is what its next loan would cost it. Leverage is what a business owes over what "
                + "it owns, and \"a year\" is the share of its firms that default within a year there - "
                + "%s at %.2f, %s at %.2f, half at %.2f, where a firm owes more than it could ever repay. "
                + "Each month the bank writes off %.0f%% of what that month's defaulters owed it, and their "
                + "bondholders %.0f%% of their bonds; they keep "
                + "their plant, so the business owes less and fewer default the next month. Past %.2f the "
                + "bank will not lend it more to cover a loss; \"stage 2\" is the share of its firms past "
                + "that line, on which the bank sets aside a loan's whole term of defaults - half of them "
                + "at the line itself. Only a business with nothing left at all is written down whole "
                + "and shut out for a while. The allowance, its stage and the price of the next loan read "
                + "the business as a lender reads its statements, over its last %d month-ends, starting "
                + "again from the month it is written down whole; the defaults read the month.",
                defaultShare(BusinessDebtManager.defaultProbability(watch * 2 / 3)), watch * 2 / 3,
                defaultShare(BusinessDebtManager.defaultProbability(watch)), watch, point,
                BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT * 100, BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT * 100,
                watch, BusinessDebtManager.STATEMENT_MONTHS);
    }

    /** THE BORROWERS: a card a business, then the landlords, the families and the carry trade, three to a row. */
    VBox borrowersSection(Bank bank) {
        List<VBox> cards = new ArrayList<>();
        for (Borrower b : borrowers()) cards.add(borrowerCard(b));
        VBox landlords = landlordsCard(bank);
        targets.put(LANDLORDS, landlords);
        cards.add(landlords);
        if (bank.getHouseholdBook() > 0 || bank.getHouseholdAllowance() > 0) {
            VBox fam = familiesCard(bank);
            targets.put(FAMILIES, fam);
            cards.add(fam);
        }
        if (bank.getCarryBook() > 0 || bank.getCarryLent() > 0) {
            VBox carry = carryCard(bank);
            targets.put(CARRY, carry);
            cards.add(carry);
        }
        GridPane grid = equalColumns(3, TILE_GAP);
        for (int i = 0; i < cards.size(); i++) {
            VBox c = cards.get(i);
            GridPane.setFillHeight(c, true);
            c.setMaxHeight(Double.MAX_VALUE);
            grid.add(c, i % 3, i / 3);
        }
        HBox head = sectionHead("WHO OWES IT, ONE BY ONE", borrowersInfo(),
                quiet("Pays: its next loan's rate. Leverage: what it owes over what it owns."));
        VBox box = new VBox(Palette.GAP, head);
        if (borrowers().isEmpty()) box.getChildren().add(muted("No business owes it anything."));
        box.getChildren().add(grid);
        return box;
    }

    /** One business as a card: its rate over prime, what it owes the bank and of its bonds, its leverage on its scale, its default rate, its chips; a click opens its Cash & debt page. */
    VBox borrowerCard(Borrower b) {
        Label name = new Label(b.key());
        name.setWrapText(true);
        name.setMinWidth(0);
        name.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, b.shut() ? Palette.BAD : Palette.TEXT_HEAD));
        VBox rate = new VBox(0, figure(ratePct(b.rate()), Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD),
                muted(pts(b.spread()) + " on prime"));
        rate.setAlignment(Pos.CENTER_RIGHT);
        rate.setMinWidth(Region.USE_PREF_SIZE);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox head = new HBox(Palette.GAP, iconSquare(sectorIcon(b.key()), Palette.BUSINESS, 28, 15), name, gap, rate);
        head.setAlignment(Pos.CENTER_LEFT);

        double point = BusinessDebtManager.INSOLVENCY_TRIGGER, watch = Bank.SECTOR_WATCH_LEVERAGE;
        double scale = 1.25 * point;
        String levTone = b.leverage() >= point ? Palette.BAD : b.leverage() > watch ? Palette.WARN : Palette.TEXT_HEAD;
        SegmentBar lev = segmentBar(List.of(), scale, List.of(
                        tick(watch, Palette.TEXT_MUTED, String.format("%.2f×", watch),
                                "past this the bank lends it nothing more to cover a loss"),
                        tick(point, Palette.TEXT_MUTED, String.format("%.2f×", point),
                                "the default point: half its firms default a year here"),
                        new Tick(Math.min(b.leverage(), scale), levTone, 3, null,
                                String.format("its leverage %.2f×", b.leverage()))),
                0, 10);

        VBox c = card(head,
                line("owes the bank " + m(b.owesBank())),
                b.bonds() > 0 ? muted("its bonds " + m(b.bonds()) + " · the bank holds " + m(b.bankHolds())) : null,
                lev,
                line(String.format("leverage %.2f× · %s of its firms default a year", b.leverage(),
                        b.owesBank() + b.bonds() > 0 ? defaultShare(b.defaultRate()) : "none")));
        javafx.scene.layout.FlowPane chips = new javafx.scene.layout.FlowPane(6, 6);
        if (b.stageTwo() > 0) {
            chips.getChildren().add(chip((b.stage() == 2 ? "STAGE 2" : "stage 1") + String.format(" · %.0f%% of its firms",
                    b.stageTwo() * 100), b.stage() == 2 ? Palette.WARN : Palette.TEXT_MUTED));
        }
        if (b.wentUnder() > 0) chips.getChildren().add(chip("went under " + b.wentUnder() + "×", Palette.TEXT_MUTED));
        if (b.shut()) chips.getChildren().add(chip("shut " + b.blockedMonths() + " mo", Palette.BAD));
        else if (b.news()) chips.getChildren().add(chip("its firms are defaulting", Palette.BAD));
        if (b.concentration() > 0) {
            chips.getChildren().add(chipTip(share1(b.concentration()) + " of what it stands to lose", Palette.TEXT_MUTED,
                    "Its share of the bank's exposure, as the concentration reads it: what the bank stands to lose on "
                    + "it, loans and bonds."));
        }
        if (!chips.getChildren().isEmpty()) c.getChildren().add(chips);
        HBox lost = new HBox(4, muted("set aside " + mFull(b.setAside()) + " · written off "),
                words(b.writtenOff() > 0 ? mFull(b.writtenOff()) + " this month" : "nothing this month",
                        Palette.SIZE_LABEL, b.news() ? Palette.BAD : Palette.TEXT_MUTED));
        lost.setAlignment(Pos.CENTER_LEFT);
        c.getChildren().add(lost);
        return doorCard(c, () -> openSector(b.key(), "Cash & debt"));
    }

    /** THE LANDLORDS: their insured mortgages (Lending 1309-1344), part of what the businesses owe. */
    VBox landlordsCard(Bank bank) {
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        NationalAccounts na = ui.game.getEconomyManager().getNationalAccounts();
        int mortgages = credit.getMortgages().size();
        String info = String.format(
                "A landlord's new building is bought with at least %.0f%% of its own money and an insured "
                + "mortgage for the rest - CMHC's terms for rental housing: at most %.0f%% of the cost, paid "
                + "down over %d years at a rate fixed for %d, renewed at the day's rate, and the building's "
                + "net rent must cover the payment %.2f times. The city insures it, for a premium of %.2f%% "
                + "of the loan, so when a landlord's debt is written down the city pays the bank what came "
                + "off its mortgages: the bank weighs them at nothing and sets nothing aside against them.",
                (1 - Mortgage.MORTGAGE_MAX_LOAN_TO_COST) * 100, Mortgage.MORTGAGE_MAX_LOAN_TO_COST * 100,
                Mortgage.MORTGAGE_AMORTIZATION_MONTHS / 12, Mortgage.MORTGAGE_TERM_MONTHS / 12,
                Mortgage.MORTGAGE_DEBT_COVERAGE, Mortgage.premiumRate() * 100);
        VBox c = card(cardHead(Icons.BUILD, Palette.BUILDING, "THE LANDLORDS", info, null));
        if (mortgages == 0) {
            c.getChildren().add(line(String.format("No landlord owes one. A new one would be written at %s, fixed for %d years.",
                    ratePerYear(credit.getInsuredMortgageRate()), Mortgage.MORTGAGE_TERM_MONTHS / 12)));
        } else {
            int renewing = credit.getMortgagesRenewingWithin(12);
            c.getChildren().addAll(
                    big(m(credit.getMortgagePrincipal()), Palette.TEXT_HEAD),
                    line(String.format("%,d insured mortgage%s at %s on average", mortgages, mortgages == 1 ? "" : "s",
                            ratePct(credit.getMortgageRate()))),
                    cardLine("Their payments, a month", mFull(credit.getMortgagePayment()), null),
                    cardLine("Principal repaid this month", mFull(credit.getMortgageRepaidThisMonth()), null),
                    cardLine("Renewing within a year", renewing == 0 ? "none" : String.format("%,d", renewing), null),
                    cardLine("Insured by the city", credit.allMortgagesInsured() ? "all of them" : "not all",
                            credit.allMortgagesInsured() ? null : Palette.BAD));
        }
        c.getChildren().addAll(
                cardLine("Claims the city paid it this month", mFull(na.getMortgageClaims()), null),
                cardLine("...and over the city's life", mFull(credit.getInsuredWrittenOffTotal()), Palette.TEXT_MUTED));
        return c;
    }

    /** THE FAMILIES: their credit lines (Lending 1347-1369), shown while they owe or something is set aside. */
    VBox familiesCard(Bank bank) {
        HouseholdBalance homes = ui.game.getHouseholdBalance();
        VBox c = card(cardHead(Icons.HOMES, Palette.PEOPLE, "THE FAMILIES",
                        "A family's credit line is revolving: it draws on it when it cannot meet its bills, and one that "
                        + "never catches up is discharged - the bank loses all of it.", null),
                big(m(bank.getHouseholdBook()), Palette.TEXT_HEAD),
                line("owed on their credit lines"),
                cardLine(String.format("...by families owing more than %.0f months of their income", Bank.HOUSEHOLD_WATCH_MONTHS),
                        mFull(bank.getHouseholdWatchedDebt()), bank.getHouseholdWatchedDebt() > 0 ? Palette.WARN : null),
                cardLine("Stage", bank.getHouseholdStage() == 2 ? "2 - some are in trouble" : "1 - every family sound",
                        bank.getHouseholdStage() == 2 ? Palette.WARN : null),
                cardLine("Set aside against it", mFull(bank.getHouseholdAllowance()), null),
                cardLine("Drawn this month", mFull(bank.getLentToHouseholds()), null),
                cardLine("Repaid this month", mFull(bank.getRepaidByHouseholds()), null));
        if (homes != null && homes.getWrittenOff() > 0) {
            c.getChildren().add(cardLine("Discharged in bankruptcy this month", mFull(homes.getWrittenOff()), Palette.BAD));
        }
        return c;
    }

    /** THE CARRY TRADE: what foreigners have borrowed here to hold abroad (the Overview's rung, Balance sheet's line). */
    VBox carryCard(Bank bank) {
        double dial = ui.game.getDebtManager().getPolicyRate();
        return card(cardHead(Icons.TRADE, Palette.RAMP_REST, "THE CARRY TRADE",
                        "Foreigners borrowing here to hold the money abroad, while the bank lends cheaper than the world "
                        + "pays: lent short and to borrowers who never default here, so no term premium and no expected "
                        + "loss. Lent only the room under what it counts itself full at, and no more than keeps it at its "
                        + "own capital target.", null),
                big(m(bank.getCarryBook()), Palette.TEXT_HEAD),
                line("lent at " + ratePerYear(bank.carryRate(dial))),
                cardLine("Lent this month", mFull(bank.getCarryLent()), null),
                cardLine("Its interest this month", mFull(bank.getCarryInterest()), null));
    }

    /* ------------------------------ set aside, written off ------------------------------ */

    VBox setAsideCard(Bank bank) {
        double sectors = bank.getSectorAllowance(), families = bank.getHouseholdAllowance(), all = bank.getAllowance();
        return card(cardHead(Icons.ALERT, Palette.MONEY, "SET ASIDE", String.format(
                        "The allowance is money set aside for loans expected to go bad - a year's expected defaults on a "
                        + "sound borrower, never under %.1f%%, and a loan's whole term of them on one in trouble. A "
                        + "write-off is drawn from it first, so a loss reaches the profit as the borrower weakens, not "
                        + "the month it is written off.", Bank.BASE_LOSS_RATE * 100), null),
                big(m(all), Palette.TEXT_HEAD),
                line("the allowance, against the loans expected to go bad"),
                segmentBar(List.of(seg(Math.max(0, sectors), Palette.BUSINESS, partTip("against the businesses", sectors, all)),
                        seg(Math.max(0, families), Palette.PEOPLE, partTip("against the families", families, all))),
                        0, List.of(), 0, 12),
                keyRow(swatch(Palette.BUSINESS, "the businesses " + m(sectors), false),
                        swatch(Palette.PEOPLE, "the families " + m(families), false)),
                cardLine("This month's provision", mv(bank.provisions()), null));
    }

    VBox writtenOffCard(Bank bank) {
        HistorySave h = ui.game.getHistorySave();
        int recorded = h.monthsRecorded("bankWriteOffs");
        return card(cardHead(Icons.ALERT, Palette.MONEY, "WRITTEN OFF",
                        "What the bank lost as borrowers' firms defaulted, a few at a time, and the families it "
                        + "discharged - drawn from what it had set aside first.", null),
                big(m(bank.getWriteOffs()), bank.getWriteOffs() > 0 ? Palette.BAD : Palette.TEXT_HEAD),
                line("this month"),
                spark("bankWriteOffs", 12, Palette.MONEY, 220, 32),
                muted(String.format("%s over the %d months on record", m(h.total("bankWriteOffs")), recorded)));
    }

    /* ------------------------------ how the next loan is priced ------------------------------ */

    /** Prime's note and the quotes' note (Lending 1470, 1515), behind HOW THE NEXT LOAN IS PRICED's (i). */
    String pricingInfo(Bank bank) {
        return String.format("What a sound business pays for a %d-month loan. The capital part is the equity a loan "
                + "ties up at its %s target, at the %.1f%% its owners want, over what the same money would cost as "
                + "debt. ", Bank.PRIME_TERM_MONTHS, share1(bank.capitalTarget()), Bank.requiredReturn() * 100)
                + String.format("A business's own risk is its expected loss over the book's: the share of its firms that "
                + "default a year at its leverage, times the %.0f%% the bank loses on them, less the %.1f%% prime already "
                + "carries - nothing for a sound one, %s at %.2fx its assets, %s at %.2fx. Its record adds %s a "
                + "write-down, for up to %d, and the book's concentration on it its share of the capital that adds - "
                + "less than nothing for a business that spreads the book. This is the rate at its leverage over its "
                + "last quarter, as the bank reads its statements; a loan is written at the leverage it leaves it at, "
                + "its building counted, and a business decides whether to build at that rate. The funds-transfer "
                + "price: the policy rate, the central bank's penalty on the share it borrows there, and the term "
                + "premium for %d months.",
                BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT * 100, Bank.BASE_LOSS_RATE * 100,
                points(BusinessDebtManager.expectedLossSpread(Bank.SECTOR_WATCH_LEVERAGE)), Bank.SECTOR_WATCH_LEVERAGE,
                points(BusinessDebtManager.expectedLossSpread(BusinessDebtManager.INSOLVENCY_TRIGGER)),
                BusinessDebtManager.INSOLVENCY_TRIGGER,
                points(BusinessDebtManager.DEFAULT_SURCHARGE), BusinessDebtManager.DEFAULT_SURCHARGE_MAX_COUNT,
                Bank.PRIME_TERM_MONTHS);
    }

    /** HOW THE NEXT LOAN IS PRICED: prime as its four parts, each named, then a row a borrower. */
    VBox pricingCard(Bank bank) {
        Bank.Ladder l = bank.ladder(ui.game.getDebtManager().getPolicyRate());
        SegmentBar primeBar = segmentBar(List.of(
                        seg(Math.max(0, l.transfer()), Palette.REVENUE_RAMP[1], "its money " + ratePct(l.transfer()),
                                "what its money costs\n" + ratePerYear(l.transfer())),
                        seg(Math.max(0, l.running()), Palette.REVENUE_RAMP[2], "running " + pts(l.running()),
                                "running the bank\n" + points(l.running())),
                        seg(Math.max(0, l.loss()), Palette.REVENUE_RAMP[3], "loss " + pts(l.loss()),
                                "the loans expected to go bad\n" + points(l.loss())),
                        seg(Math.max(0, l.capital()), Palette.REVENUE_RAMP[4], "capital " + pts(l.capital()),
                                "the capital a loan ties up\n" + points(l.capital()))),
                0, List.of(), 0, 18);
        javafx.scene.layout.FlowPane key = keyRow(
                swatch(Palette.REVENUE_RAMP[1], "what its money costs " + ratePct(l.transfer()), false),
                swatch(Palette.REVENUE_RAMP[2], "running the bank " + pts(l.running()), false),
                swatch(Palette.REVENUE_RAMP[3], "the loans expected to go bad " + pts(l.loss()), false),
                swatch(Palette.REVENUE_RAMP[4], "the capital a loan ties up " + pts(l.capital()), false));
        List<Rung> rows = borrowerRungs(l, false);
        double top = l.prime();
        for (Rung r : rows) if (Double.isFinite(r.rate())) top = Math.max(top, r.rate());
        HBox head = cardHead(Icons.BANK, Palette.MONEY, "HOW THE NEXT LOAN IS PRICED", pricingInfo(bank),
                figure("prime " + ratePerYear(l.prime()), Palette.SIZE_HEADING, Palette.TEXT_HEAD));
        return card(head, primeBar, key, muted("...and what each borrower pays over it"),
                rateLadder(rows, top * 1.02, 230, 110, 170));
    }

    /* ------------------------------ the tables, under details ------------------------------ */

    /**
     * The old page's tables and lines, verbatim (the spec's N2, N6, N7, N9,
     * N11; D15), with B9's fix - the businesses' first column is what each
     * owes THE BANK - and B2's: no green or amber on what is not a verdict.
     */
    VBox lendingTables(Bank bank) {
        VBox column = column();
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();

        /* ---------------------------- the businesses ---------------------------- */
        column.getChildren().add(statementHead("The businesses, one by one"));
        /*
         * A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8): beside its leverage,
         * the share of its firms that default a year there (PD, the curve the
         * slice and the allowance both read), and beside what the bank set
         * aside, what this month wrote off - the allowance's figure for the
         * sector, saved with it (Bank.getWrittenOff()). Whether it is shut
         * out is in its name's colour here and in the table under.
         */
        GridPane t = grid(new double[] {104, 66, 84, 50, 50, 36, 64, 64}, rightAfterFirst(8));
        gridHead(t, "", "owes it", "pays", "leverage", "a year", "stage 2", "set aside", "this month");
        int row = 1;
        for (String name : Sectors.KEYS) {
            double owed = credit.getPrincipal(name);
            boolean shut = credit.isBorrowingBlocked(name);
            double lost = bank.getWrittenOff(name);
            if (owed <= 0 && !shut && bank.getSectorAllowance(name) <= 0 && lost <= 0) continue;
            double lev = credit.getLeverage(name);
            double pd = credit.getDefaultRate(name);
            int stage = bank.getStage(name);
            double inStage2 = bank.getStageTwoShare(name);
            String levTone = lev >= BusinessDebtManager.INSOLVENCY_TRIGGER ? Palette.BAD
                    : lev > Bank.SECTOR_WATCH_LEVERAGE ? Palette.WARN : Palette.TEXT_MUTED;
            t.add(gridCell(name, shut ? Palette.BAD : Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, row);
            t.add(gridCell(money(credit.getLoanPrincipal(name)), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, row);
            t.add(gridCell(ratePerYear(credit.getRate(name)),
                    Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, row);
            t.add(gridCell(String.format("%.2fx", lev), levTone, Palette.SIZE_CAPTION, true), 3, row);
            t.add(gridCell(owed > 0 ? defaultShare(pd) : "—", owed > 0 ? levTone : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 4, row);
            // The share of its book in stage 2, firm by firm (0.7.8): amber
            // once most of its firms are past the watch line.
            t.add(gridCell(owed > 0 ? String.format("%.0f%%", inStage2 * 100) : "—",
                    stage == 2 ? Palette.WARN : Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 5, row);
            t.add(gridCell(money(bank.getSectorAllowance(name)), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 6, row);
            t.add(gridCell(lost > 0 ? money(lost) : "—",
                    credit.defaultsAreNews(name) ? Palette.BAD : lost > 0 ? Palette.TEXT_MUTED : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 7, row);
            row++;
        }
        if (row == 1) {
            column.getChildren().add(sentence("No business owes it anything.", Palette.TEXT_MUTED));
        } else {
            column.getChildren().add(t);
        }
        // In two layers since 0.7.22 (the text cut): the line, and the whole note behind its (i).
        column.getChildren().add(statementNote(
                "Owes it: its loans from the bank. Pays: its next loan's rate. Leverage: what it owes over what it owns.",
                borrowersInfo()));

        /* -------------------------- who has stopped paying -------------------------- */
        /*
         * Since 0.7.8 most of what is written off is the slices - a business's
         * firms defaulting a few at a time - and "went under" counts only the
         * whole-sector backstop, the only default on a borrower's record. A
         * total under a dollar is left off: a slice that small is the curve's
         * far tail, not a borrower that stopped paying.
         */
        GridPane trouble = grid(new double[] {150, 100, 100, 96, 90}, rightAfterFirst(5));
        gridHead(trouble, "", "this month", "in total", "went under", "status");
        int line = 1;
        for (String name : Sectors.KEYS) {
            double month = bank.getWrittenOff(name);
            // What the bank lost: the insured part of a landlord's write-downs
            // was the city's to pay (0.7.11), not the bank's to lose.
            double ever = credit.getWrittenOffTotal(name) - credit.getInsuredWrittenOffTotal(name);
            boolean shut = credit.isBorrowingBlocked(name);
            int whole = credit.getRestructureCount(name);
            if (toDollars(ever) < 1 && whole == 0 && !shut) continue;
            trouble.add(gridCell(name, shut ? Palette.BAD : Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            trouble.add(gridCell(month > 0 ? money(month) : "—",
                    credit.defaultsAreNews(name) ? Palette.BAD : month > 0 ? Palette.TEXT_MUTED : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 1, line);
            trouble.add(gridCell(ever > 0 ? money(ever) : "—", Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 2, line);
            trouble.add(gridCell(whole > 0 ? whole + (whole == 1 ? " time" : " times") : "never",
                    whole > 0 ? Palette.TEXT_MUTED : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 3, line);
            trouble.add(gridCell(shut ? credit.getBlockedMonths(name) + "mo shut" : "borrowing",
                    shut ? Palette.BAD : Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        column.getChildren().add(statementHead("Who has stopped paying"));
        if (line == 1) {
            column.getChildren().add(sentence("No business's firms have defaulted, and none is shut out.",
                    Palette.TEXT_MUTED));
        } else {
            column.getChildren().add(trouble);
            column.getChildren().add(statementNote(
                    "What the bank lost as a business's firms defaulted, a few at a time. A business that "
                    + "went under had nothing left at all: its loans were written off whole, and it is shut "
                    + "out of new borrowing for a while after - the bank refusing, not the business declining."));
        }

        /* -------------------------- what it lent this month -------------------------- */
        column.getChildren().add(statementHead("What it lent this month"));
        column.getChildren().add(statementLine("To the businesses", moneyFull(credit.getLentThisMonth())));
        column.getChildren().add(statementLine("Drawn by the families", moneyFull(bank.getLentToHouseholds())));
        column.getChildren().add(statementLine("To the carry trade", moneyFull(bank.getCarryLent())));
        String stance = bank.lendingStance();
        column.getChildren().add(subHead("What its capital lets it lend"));
        column.getChildren().add(sentence(stance.substring(0, 1).toUpperCase() + stance.substring(1) + ".",
                stanceTone(bank)));
        double limit = bank.lendingLimit();
        if (!Double.isInfinite(limit) && limit > 0) {
            column.getChildren().add(statementLine("...about this much growth across its book",
                    moneyFull(limit), Palette.WARN));
        }
        column.getChildren().add(statementNote(stanceInfo(bank)));

        /* ------------------------ how the next loan is priced ------------------------ */
        double dial = ui.game.getDebtManager().getPolicyRate();
        Bank.Ladder l = bank.ladder(dial);
        HouseholdBalance homes = ui.game.getHouseholdBalance();
        column.getChildren().add(statementHead("How the next loan's rate is built"));
        column.getChildren().add(statementLine("What the money costs it", ratePerYear(l.transfer())));
        column.getChildren().add(statementLine("...running the bank", points(l.running())));
        column.getChildren().add(statementLine("...the loans expected to go bad", points(l.loss())));
        column.getChildren().add(statementLine("...the capital a loan ties up", points(l.capital())));
        column.getChildren().add(statementTotal("Prime", ratePerYear(l.prime()), Palette.TEXT_HEAD));
        /*
         * EACH SECTOR'S OWN EXPECTED LOSS OVER THE BOOK'S (0.7.8), its record
         * apart and since 0.7.33 the book's concentration on it - the parts
         * its rate was struck from (BusinessDebtManager.quoteParts()), so the
         * columns add up to what it pays. The leverage column is the
         * quarter's (getQuarterLeverage()), the reading the risk is struck on.
         */
        GridPane quotes = grid(new double[] {150, 60, 84, 70, 84, 90}, rightAfterFirst(6));
        gridHead(quotes, "", "leverage", "its own risk", "its record", "concentration", "it pays");
        int q = 1;
        for (String name : Sectors.KEYS) {
            if (credit.getPrincipal(name) <= 0) continue;
            BusinessDebtManager.QuoteParts parts = credit.quoteParts(name);
            quotes.add(gridCell(name, Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, q);
            quotes.add(gridCell(String.format("%.2fx", credit.getQuarterLeverage(name)), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 1, q);
            quotes.add(gridCell(parts == null ? "—" : points(parts.risk()), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, q);
            quotes.add(gridCell(parts != null && parts.record() > 0 ? points(parts.record()) : "—",
                    Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 3, q);
            quotes.add(gridCell(parts == null ? "—" : points(parts.concentration()), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 4, q);
            quotes.add(gridCell(ratePerYear(credit.getRate(name)), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 5, q);
            q++;
        }
        double families = homes == null ? 0 : homes.averageRate();
        quotes.add(gridCell("The families, on average", Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, q);
        quotes.add(gridCell(families > 0 ? points(l.overPrime(families)) : "—", Palette.TEXT_MUTED,
                Palette.SIZE_CAPTION, true), 2, q);
        quotes.add(gridCell(ratePerYear(families > 0 ? families : l.household()), Palette.TEXT_HEAD,
                Palette.SIZE_CAPTION, true), 5, q++);
        quotes.add(gridCell("The carry trade", Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, q);
        quotes.add(gridCell(points(l.overPrime(l.carry())), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, q);
        quotes.add(gridCell(ratePerYear(l.carry()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 5, q++);
        // ...and a landlord's insured mortgage (0.7.11): under prime, by the
        // loss it does not carry and most of the capital (only the leverage
        // minimum's, since round 2), over it by a longer term.
        quotes.add(gridCell("An insured mortgage (10 years)", Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, q);
        quotes.add(gridCell(points(l.overPrime(l.mortgage())), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, q);
        quotes.add(gridCell(ratePerYear(l.mortgage()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 5, q);
        column.getChildren().add(subHead("...and what each borrower pays over it"));
        column.getChildren().add(quotes);
        column.getChildren().add(statementNote(pricingInfo(bank)));

        /* ---------------------------- what the book weighs ---------------------------- */
        column.getChildren().add(statementHead("What a dollar of the book weighs"));
        GridPane w = grid(new double[] {150, 100, 60, 80, 110}, rightAfterFirst(5));
        gridHead(w, "", "at face", "term", "risk weight", "weighs");
        int r = 1;
        for (Bank.WeightRow one : bank.weightTable()) {
            w.add(gridCell(bookName(one.book()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, r);
            w.add(gridCell(money(one.face()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, r);
            w.add(gridCell(String.format("%.2f", one.term()), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, r);
            w.add(gridCell(String.format("%.0f%%", one.risk() * 100), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 3, r);
            w.add(gridCell(money(one.weighted()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 4, r);
            r++;
        }
        column.getChildren().add(w);
        column.getChildren().add(statementTotal("Weighed for risk and term",
                moneyFull(bank.getWeightedBook()), Palette.TEXT_HEAD));
        column.getChildren().add(statementNote(String.format(
                "Risk-weighted: each dollar counted by how likely it is to be lost and how long it runs. A "
                + "city bond weighs %.0f%% of a business loan, an insured mortgage %.0f%% - the city "
                + "guarantees it - and the desk's shares %.0f%%; \"term\" is the "
                + "share of its face a loan's remaining months count for, as little as %.0f%% for one repaying "
                + "soon. The bank's capital is measured against this - and against the face as well, "
                + "at the leverage minimum, whichever asks more.",
                Bank.RISK_CITY * 100, Bank.RISK_INSURED_MORTGAGE * 100, Bank.RISK_EQUITY * 100,
                Bank.SHORTEST_WEIGHT * 100)));
        column.getChildren().add(statementNote(
                "The businesses' bonds it holds weigh as loans to their issuers, at what they cost it. Its "
                + "concentration is the capital the book's reliance on a few industries adds - the next "
                + "section - carried as the weight that capital would need at the minimum."));

        /* ------------------------ what concentration costs (0.7.12) ------------------------ */
        column.getChildren().add(statementHead("What its concentration costs"));
        column.getChildren().add(statementLine("How concentrated its book is (Herfindahl index)",
                String.format("%.2f", bank.getConcentrationHerfindahl())));
        column.getChildren().add(statementLine("The capital it adds, at the minimum",
                moneyFull(bank.getConcentrationAddOn())));
        double exposure = bank.getConcentrationExposure();
        if (exposure > 0) {
            GridPane c = grid(new double[] {150, 90, 110, 110}, rightAfterFirst(4));
            gridHead(c, "", "of the book", "a dollar more adds", "on its loans");
            int k = 1;
            for (String name : Sectors.KEYS) {
                Bank.Exposure x = bank.getExposure(name);
                if (x == null || !(x.amount() > 0)) continue;
                double charge = bank.concentrationCharge(dial, Bank.PRIME_TERM_MONTHS, name);
                c.add(gridCell(name, Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, k);
                c.add(gridCell(share1(x.amount() / exposure), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 1, k);
                // Signed, with a true minus and no "-0.00" (B1's case at Lending 1608).
                c.add(gridCell(String.format("%+.2f%%", unsigned0(bank.concentrationPerDollar(name) * 100, 2))
                        .replace('-', '\u2212'), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, k);
                c.add(gridCell(points(charge), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 3, k);
                k++;
            }
            column.getChildren().add(c);
        }
        column.getChildren().add(statementNote(String.format(
                "No limit on one industry: a price. Basel's capital formula for a loan book assumes it is spread "
                + "across every industry; this one's firms move together more the fewer industries it lends to, "
                + "their correlation rising from Basel's to %.2f times it in a book of one industry. What that "
                + "adds is shared out by each industry's part in it - a dollar more to an industry already large "
                + "in the book adds more than its average, one to a small industry less than nothing - and "
                + "carried into each industry's rate through the capital charge, and into what the bank must "
                + "hold.", Bank.SECTOR_CORRELATION_MULTIPLIER)));
        return column;
    }

    /** What the weight table calls each book. */
    static String bookName(Bank.Book book) {
        return switch (book) {
            case BUSINESSES -> "The businesses";
            case CITY       -> "The city's own paper";
            case FAMILIES   -> "The families";
            case CARRY      -> "The carry trade";
            case DESK       -> "The desk's shares";
            case MORTGAGES  -> "Insured mortgages";
            case BONDS      -> "The businesses' bonds";
            case CONCENTRATION -> "Its concentration";
        };
    }

    /** THE BUSINESSES' BONDS IT HOLDS (0.7.12), verbatim, under their own fold - the Overview's bond rungs open it. */
    VBox bondsHeld(Bank bank) {
        VBox column = column();
        BondMarket market = ui.game.getBondMarket();
        column.getChildren().add(statementHead("The businesses' bonds it holds"));
        if (bank.getBondBook() <= 0) {
            column.getChildren().add(sentence(market.getBonds().isEmpty()
                    ? "None: no business has sold a bond."
                    : "None. It buys a business's bond only at the yield an equal loan to that business "
                            + "would earn it, and only while it is over its capital target.", Palette.TEXT_MUTED));
        } else {
            column.getChildren().add(statementLine("At what they cost it", moneyFull(bank.getBondBook())));
            column.getChildren().add(statementLine("...their face", moneyFull(bank.getBondFace())));
            column.getChildren().add(statementLine("Their coupons this month", moneyFull(bank.getInterestFromBonds())));
            if (Math.abs(bank.getBondGains()) > 1e-9) {
                column.getChildren().add(statementLine("Gained on those it sold or was repaid",
                        signed(bank.getBondGains(), false)));
            }
            for (String name : Sectors.KEYS) {
                if (!(market.bankCost(name) > 0)) continue;
                column.getChildren().add(statementLine("   " + name + ", of its " + money(market.principal(name)),
                        moneyFull(market.bankCost(name)), Palette.TEXT_MUTED));
            }
        }
        if (bank.getUnderwritingFees() > 0) {
            column.getChildren().add(statementLine("Paid for underwriting this month's issues",
                    moneyFull(bank.getUnderwritingFees())));
        }
        column.getChildren().add(statementNote(
                "It is every issue's underwriter, paid the issue's costs. It bids for a bond at the yield that "
                + "earns it what an equal loan to its issuer would - the loan's parts for the bond's ten years, "
                + "the concentration charge, and the loss a bondholder takes, larger than a lender's "
                + "- on the capital it holds over its target. Under its target it offers its bonds for sale at "
                + "what they are worth, for as many as take it back there."));
        return column;
    }

    /* =====================================================================
       FUNDING

       Where does its money come from, and what does it cost? What the city
       has banked with it and what of it the bank can lend against - all of
       it since 0.7.19; until then, how much of it its branches reached, in
       today's money (the founding constants, until 0.7.9 - a hundred times
       out after a currency reform); what it pays savers and why; its account
       at the central bank; how its lending is funded; what it can carry; and
       its branches, with the investors' own verdict on another one - since
       0.7.33 the planner's (BusinessInvestment.planBank(), the spec's B6:
       the page said "Yes" when the bank wanted one and the city could not
       staff it) - and, since round 2 of 0.7.11, on whether the ones standing
       still pay. Drawn since 0.7.33: the deposits and what funds the book as
       two bars, then the savers, the central bank and what it can carry as
       cards, and the branches; the old lines under "details".
       ===================================================================== */

    void fundingPage(VBox page) {
        Bank bank = ui.game.getBank();
        Bank.Ladder l = bank.ladder(ui.game.getDebtManager().getPolicyRate());
        page.getChildren().add(fundingHero(bank, l));
        VBox carry = canCarryCard(bank);
        targets.put(CAN_CARRY, carry);
        page.getChildren().add(row(saversCard(bank, l), centralBankCard(bank, l), carry));
        page.getChildren().add(branchesCard(bank));
        VBox lines = fold("funding:lines", "what is banked, what it can lend against, savers, the central bank, how "
                + "its lending is funded, what it can carry, and its branches, line by line", () -> fundingLines(bank, l));
        targets.put(DETAILS, lines);
        page.getChildren().add(lines);
    }

    /** What it can lend against (Funding 1681), behind the banked bar's (i). */
    static final String LEND_AGAINST_INFO = "Savings reach the bank wherever its branches are - online - so it lends "
            + "against everything the city has banked with it, not what its counters reach. Money wired from abroad can "
            + "leave as fast as it came.";

    /** The two bars worked out without drawing them (pure: the probe reads them). */
    record FundingBars(List<Segment> banked, List<Tick> bankedTicks, List<Segment> funds, String bankedWords,
                       String fundsWords) { }

    /** WHAT THE CITY HAS BANKED and WHAT FUNDS ITS BOOK (pure), each on its own scale. */
    FundingBars fundingBars() {
        Bank bank = ui.game.getBank();
        Bank.Ladder l = bank.ladder(ui.game.getDebtManager().getPolicyRate());
        double deposits = bank.getDeposits();
        List<Segment> banked = new ArrayList<>();
        if (bank.getHouseholdDeposits() > 0) banked.add(seg(bank.getHouseholdDeposits(), Palette.PEOPLE,
                partTip("the families' savings", bank.getHouseholdDeposits(), deposits)));
        if (bank.getSectorDeposits() > 0) banked.add(seg(bank.getSectorDeposits(), Palette.BUSINESS,
                partTip("the businesses' cash in credit", bank.getSectorDeposits(), deposits)));
        if (bank.getForeignDeposits() > 0) banked.add(seg(bank.getForeignDeposits(), Palette.RAMP_REST,
                partTip("money from abroad", bank.getForeignDeposits(), deposits)));
        List<Tick> ticks = new ArrayList<>();
        if (bank.depositFunding() > 0) {
            ticks.add(tick(bank.depositFunding(), Palette.TEXT_HEAD,
                    l.fundingPosition() >= 1 ? "lent past its own cash, all of it"
                            : String.format("lent past its own cash, %.0f%%", l.fundingPosition() * 100),
                    "what it has lent past its own cash on them\n" + mFull(bank.depositFunding())));
        }
        double equity = bank.equity(), foreign = bank.getForeignDeposits();
        double funded = Math.max(0, equity) + bank.depositFunding() + foreign + bank.wholesaleFunding();
        List<Segment> funds = new ArrayList<>();
        if (equity > 0) funds.add(seg(equity, Palette.MONEY, "its capital", partTip("its own capital", equity, funded)));
        if (bank.depositFunding() > 0) funds.add(seg(bank.depositFunding(), Palette.LADDER[1], "savers",
                partTip("savers' deposits it has lent out", bank.depositFunding(), funded)));
        if (foreign > 0) funds.add(seg(foreign, Palette.RAMP_REST, partTip("money from abroad", foreign, funded)));
        if (bank.wholesaleFunding() > 0) funds.add(seg(bank.wholesaleFunding(), Palette.MONEY_DARK, "the window",
                partTip("the central bank, overnight", bank.wholesaleFunding(), funded)));
        return new FundingBars(banked, ticks, funds,
                "The city has banked " + m(deposits) + " with it",
                "What funds its book: " + m(equity) + " of its own"
                        + (bank.depositFunding() > 0 ? ", " + m(bank.depositFunding()) + " of savers'" : "")
                        + (bank.wholesaleFunding() > 0 ? ", " + m(bank.wholesaleFunding()) + " from the window" : ""));
    }

    /** The hero: the two bars, each with its words at the left and its key under it. */
    VBox fundingHero(Bank bank, Bank.Ladder l) {
        FundingBars f = fundingBars();
        VBox c = card(cardHead(Icons.BANK, Palette.MONEY, "WHAT THE CITY HAS BANKED · WHAT FUNDS ITS BOOK",
                LEND_AGAINST_INFO + " Each bar is on its own scale: the deposits are the city's money, most of it not "
                        + "lent; the book is what the bank has lent, and the second bar is where that money came from.",
                null));
        if (bank.getDeposits() <= 0) c.getChildren().add(line("Nobody has banked anything yet."));
        else c.getChildren().add(fundingRow(f.bankedWords(), segmentBar(f.banked(), 0, f.bankedTicks(), 0, 20)));
        c.getChildren().add(keyRow(
                swatch(Palette.PEOPLE, "the families " + m(bank.getHouseholdDeposits()), false),
                swatch(Palette.BUSINESS, "the businesses " + m(bank.getSectorDeposits()), false),
                bank.getForeignDeposits() > 0 ? swatch(Palette.RAMP_REST, "from abroad " + m(bank.getForeignDeposits()), false) : null));
        if (!f.funds().isEmpty()) c.getChildren().add(fundingRow(f.fundsWords(), segmentBar(f.funds(), 0, List.of(), 0, 20)));
        c.getChildren().add(keyRow(
                swatch(Palette.MONEY, "its own capital", false),
                swatch(Palette.LADDER[1], "savers' deposits", false),
                bank.getForeignDeposits() > 0 ? swatch(Palette.RAMP_REST, "money from abroad", false) : null,
                bank.wholesaleFunding() > 0 ? swatch(Palette.MONEY_DARK, "the central bank, overnight", false) : null));
        return c;
    }

    /** One of the hero's rows: its words in a column at the left, the bar taking the rest. */
    static HBox fundingRow(String words, Node bar) {
        Label l = words(words, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL);
        l.setMinWidth(220);
        l.setPrefWidth(220);
        l.setMaxWidth(220);
        HBox.setHgrow(bar, Priority.ALWAYS);
        HBox row = new HBox(Palette.GAP_LOOSE, l, bar);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /* ------------------------------ the three cards ------------------------------ */

    /** What savers are paid's note (Funding 1712). */
    static String saversShareInfo() {
        return String.format("A bank flush with reserves passes on about %.0f%% of the policy rate - its next deposit "
                + "only earns the policy rate at the central bank. One borrowing there passes on %.0f%%, because every "
                + "deposit it finds saves it the central bank's rate. It never pays savers more than its margin, after "
                + "its staff and branches, can pay.", Bank.DEPOSIT_SHARE_FLUSH * 100, Bank.DEPOSIT_SHARE_AT_WINDOW * 100);
    }

    /** SAVERS: the policy rate, the share its funding asks it to pass on, the rate it chose and what it paid - a ladder of four - and what that paid this month. */
    VBox saversCard(Bank bank, Bank.Ladder l) {
        HBox steps = new HBox(6,
                saversStep("policy", ratePct(l.policy()), null),
                muted("›"),
                saversStep("its funding asks", String.format("%.0f%%", l.saversShare() * 100), "of it"),
                muted("›"),
                saversStep("it chose", ratePct(l.saversChose()), null),
                muted("›"),
                saversStep("paid", ratePct(l.savers()), null));
        steps.setAlignment(Pos.CENTER_LEFT);
        VBox c = card(cardHead(Icons.POPULATION, Palette.PEOPLE, "SAVERS", saversWhy(l) + " " + saversShareInfo(),
                l.saversHeld() ? chipTip("held", Palette.WARN, "Its margin could not pay the rate it chose, so savers got less.") : null),
                steps,
                cardLine("Paid to the families this month", mFull(bank.getDepositInterestToHouseholds()), null),
                cardLine("...to the businesses", mFull(bank.getDepositInterestToSectors()), null));
        if (bank.getDepositInterestToForeign() > 0) {
            c.getChildren().add(cardLine("...and abroad", mFull(bank.getDepositInterestToForeign()), null));
        }
        return c;
    }

    /** One step of the savers' ladder: its words over its figure. */
    static VBox saversStep(String what, String figure, String under) {
        VBox s = new VBox(0, muted(what), figure(figure, Palette.SIZE_BODY + 2, Palette.TEXT_HEAD));
        if (under != null) s.getChildren().add(muted(under));
        return s;
    }

    /** THE CENTRAL BANK: its reserves there and what they earned, and what it borrowed overnight and what that cost - red while it borrows. */
    VBox centralBankCard(Bank bank, Bank.Ladder l) {
        return card(cardHead(Icons.BANK, Palette.MONEY, "THE CENTRAL BANK", String.format(
                        "\"The window\" is borrowing overnight from the central bank. It lends a bank whatever it "
                        + "needs, at the policy rate plus a %s penalty - so what stops a bank lending more is what that "
                        + "money costs, which is in every loan's rate, and its capital, not the money.",
                        points(CentralBank.WINDOW_PENALTY)), null),
                cardLine("Reserves held there", mFull(bank.cashReserves()), null),
                cardLine("...earning the policy rate", ratePerYear(l.policy()), null),
                cardLine("...which paid it this month", mFull(bank.getPlacementIncome()), null),
                cardLine("Borrowed overnight", mFull(bank.wholesaleFunding()),
                        bank.wholesaleFunding() > 0 ? Palette.BAD : null),
                cardLine("...at the window's rate", ratePerYear(l.window()), null),
                cardLine("...which cost it this month", mFull(bank.getFundingCost()), null));
    }

    /** WHAT IT CAN CARRY: what its capital carries and what its deposits carry on one scale, the tighter marked BINDS, its weighed book against them, and how full that is. */
    VBox canCarryCard(Bank bank) {
        double cap = bank.capitalLimit(), dep = bank.fundingLimit(), book = bank.getWeightedBook();
        boolean capitalBinds = bank.capitalBound();
        double scale = Math.max(Math.max(cap, dep), book) * 1.02;
        List<Tick> weighs = book > 0 ? List.of(tick(book, Palette.TEXT_HEAD, null, "its book weighs\n" + mFull(book))) : List.of();
        VBox c = card(cardHead(Icons.SECTOR, Palette.MONEY, "WHAT IT CAN CARRY", String.format("What its capital carries "
                        + "is its equity at the minimum (%s of what it weighs, or %s of everything it has lent when the "
                        + "leverage ratio binds); what its deposits carry is what it gathered, lent %.0f times over. The "
                        + "tighter of the two is what it can carry, and its book - weighed for risk and term - is set "
                        + "against it.", share1(Bank.CAPITAL_RATIO), share1(Bank.LEVERAGE_RATIO_MIN), Bank.LEVERAGE), null),
                carryLine("its capital carries", cap, scale, capitalBinds, weighs),
                carryLine("its deposits carry", dep, scale, !capitalBinds, weighs));
        if (bank.isInsolvent()) {
            c.getChildren().add(line("It can carry nothing - it has failed."));
        } else if (bank.capacity() > 0) {
            int need = -1;
            for (CityNeeds.Need n : CityNeeds.measure(ui.game, SummaryScreen.WORDS)) if (n.kind() == CityNeeds.Kind.BANK) need = n.level();
            HBox full = new HBox(6, line("its book weighs " + m(book) + " ·"),
                    words(String.format("%.0f%% full", bank.strain() * 100), Palette.SIZE_LABEL + 1,
                            need >= 2 ? Palette.BAD : need == 1 ? Palette.WARN : Palette.TEXT_HEAD));
            full.setAlignment(Pos.CENTER_LEFT);
            c.getChildren().add(full);
        }
        c.getChildren().add(muted(bank.isInsolvent()
                ? "A failed bank may lend nothing new, so it can carry nothing until it has capital again."
                : capitalBinds
                ? "Its capital is the limit: it lends more as it earns, or as its owners or the city put more in."
                : "Its deposits are the limit: the city has not banked enough with it to lend more."));
        return c;
    }

    /** One of what it can carry's two bars: its words, the bar on the card's scale with the book's weight ticked, and BINDS on the tighter. */
    static VBox carryLine(String words, double amount, double scale, boolean binds, List<Tick> ticks) {
        HBox head = new HBox(6, line(words + " " + m(amount)));
        if (binds) head.getChildren().add(chip("BINDS", Palette.TEXT_HEAD));
        head.setAlignment(Pos.CENTER_LEFT);
        return new VBox(3, head, segmentBar(List.of(seg(Math.max(0, amount), binds ? Palette.MONEY : Palette.MONEY_LIGHT,
                words + "\n" + mFull(amount))), scale, ticks, 0, 10));
    }

    /* ------------------------------ the branches ------------------------------ */

    /** The branch verdict worked out without drawing it (pure: the probe reads it, BankCheck asserts the planner it reads). */
    record Verdict(String words, String tone, boolean build) { }

    /**
     * WOULD ANOTHER ONE PAY? The investors' own answer since 0.7.33 - the
     * planner the month runs (BusinessInvestment.planBank(), which asks the
     * bank's rule and then whether the city could staff the branch) - so the
     * page says "Yes" only when they would build one, and otherwise what holds
     * them (the spec's B6 and D9).
     */
    Verdict branchVerdict() {
        Bank bank = ui.game.getBank();
        BusinessInvestment.Decision d = ui.game.getBusinessInvestment().planBank();
        if (d.build) return new Verdict("Yes: " + d.reason + ". The city's own investors build one the month it holds.",
                Palette.GOOD, true);
        boolean capital = bank.isInsolvent() || bank.lendsOnlyToKeepBorrowersGoing();
        return new Verdict("Not yet: " + d.reason + ".", capital ? Palette.BAD : Palette.TEXT_MUTED, false);
    }

    /** The branches' notes (Funding 1801, 1848): a branch's cost, and the test run the other way. */
    String branchesInfo() {
        return "A branch's cost is its staff, its repairs and its running costs - rent, systems and supplies. Every "
                + "branch after the first has to be paid for by its customers' account fees; the first is the city's "
                + "charter, stays open however few it serves, and pays its staff and repairs but no running costs. "
                + "The city's own investors open another when there are customers for it, its share of the fees would "
                + "cover it and the city could staff it - the verdict here is theirs. The same test runs the other way: "
                + "when a month's fees do not cover what its branches cost at last month's cost, the branches past what "
                + "they cover close, never the first. It decides on last month's cost, so a month whose wages rise can "
                + "run a branch short once; the next month's test closes it. The building is sold as any retired "
                + "building is - the plot back to the city, the material to the builders - and what the bank was "
                + "founded with stays in it."
                + (ui.game.getBranchesClosed() > 0
                        ? String.format(" It has closed %d this session.", ui.game.getBranchesClosed()) : "");
    }

    /**
     * ITS BRANCHES, BY THEIR CUSTOMERS (0.7.19) as a card: the customers a
     * branch against what one serves as a ring; a branch's fees against what
     * one past the first costs; the two questions, the investors' verdict,
     * whether the ones standing still pay; what another costs and what its
     * owners put in; and Build.
     */
    VBox branchesCard(Bank bank) {
        BuildingsTemplate branch = ui.game.getBuildingManager().getTemplateByName("Commercial Bank");
        int standing = ui.game.getBuildingManager().countByName("Commercial Bank");
        double per = bank.customersPerBranch();
        double share = per / Bank.CUSTOMERS_PER_BRANCH;
        Region ring = ring(Math.min(1, share), Palette.MONEY, 72, 8,
                share > 1 ? String.format("%.1f×", share) : String.format("%.0f%%", share * 100), 12);
        VBox ringSide = new VBox(4, ring, muted(String.format("%s customers a branch, of the %s one serves",
                formatter.format(Math.round(per)), formatter.format(Math.round(Bank.CUSTOMERS_PER_BRANCH)))));
        ringSide.setAlignment(Pos.TOP_CENTER);
        ringSide.setMinWidth(170);
        ringSide.setPrefWidth(190);
        ringSide.setMaxWidth(190);

        double fees = bank.feesPerBranch(), cost = bank.laterBranchCost();
        double scale = Math.max(Math.max(fees, cost), 1e-9) * 1.05;
        VBox money = new VBox(6,
                line("a branch's fees this month " + mFull(fees)),
                segmentBar(List.of(seg(Math.max(0, fees), Palette.MONEY, "a branch's fees\n" + mFull(fees))), scale,
                        List.of(), 0, 10),
                line("what a branch past the first cost last month " + mFull(cost)),
                segmentBar(List.of(seg(Math.max(0, cost), Palette.MONEY_DARK, "a branch's cost\n" + mFull(cost))), scale,
                        List.of(), 0, 10),
                muted(formatter.format(Math.round(bank.getCustomers())) + " customers pay its fee, at "
                        + standing + (standing == 1 ? " branch" : " branches")));
        money.setMinWidth(260);
        HBox.setHgrow(money, Priority.ALWAYS);

        Verdict v = branchVerdict();
        boolean customers = bank.customersForAnother(standing), cover = bank.feesWouldCoverAnother(standing);
        VBox answer = new VBox(4,
                question("Are there customers for another?", customers),
                question("Would its share of the fees cover it? " + m(bank.feesPerBranchWithAnother(standing)) + " a branch",
                        cover),
                words(v.words(), Palette.SIZE_LABEL + 1, v.tone()),
                question(bank.getUncoveredMonths() == 0 ? "Have its fees covered its branches? Yes, last month"
                        : String.format("Have its fees covered its branches? Not for %d month%s", bank.getUncoveredMonths(),
                        bank.getUncoveredMonths() == 1 ? "" : "s"), bank.getUncoveredMonths() == 0),
                muted((branch != null ? "another costs " + m(ui.game.quoteBuild(branch, 1).total) + " · " : "")
                        + "its owners put in " + m(bank.getPaidInPerBranch())));
        HBox build = doorPill("Build a Commercial Bank", Icons.BUILD, Palette.BUILDING,
                () -> ui.buildScreen.openCategory(BuildAdvice.SHOPS));
        answer.getChildren().add(build);
        answer.setMinWidth(300);
        answer.setPrefWidth(420);

        HBox body = new HBox(24, ringSide, money, answer);
        body.setAlignment(Pos.TOP_LEFT);
        javafx.scene.layout.FlowPane events = new javafx.scene.layout.FlowPane(6, 6);
        double[] b = ui.game.getHistorySave().aligned("bankBranches");
        if (b.length >= 2 && Double.isFinite(b[b.length - 1]) && Double.isFinite(b[b.length - 2])) {
            long moved = Math.round(b[b.length - 1] - b[b.length - 2]);
            if (moved > 0) events.getChildren().add(chip("+" + moved + (moved == 1 ? " branch" : " branches") + " last month", Palette.MONEY));
            if (moved < 0) events.getChildren().add(chip("−" + (-moved) + (moved == -1 ? " branch" : " branches") + " last month", Palette.TEXT_MUTED));
        }
        if (ui.game.getBranchesClosed() > 0) {
            events.getChildren().add(chip("closed " + ui.game.getBranchesClosed() + " this session", Palette.TEXT_MUTED));
        }
        return card(cardHead(Icons.CRANE, Palette.MONEY, "ITS BRANCHES · " + standing + " standing", branchesInfo(),
                events.getChildren().isEmpty() ? null : events), body);
    }

    /** One of the branch questions: a tick or a cross, and the words. */
    static HBox question(String words, boolean yes) {
        Label mark = figure(yes ? "✓" : "✗", Palette.SIZE_BODY, yes ? Palette.GOOD : Palette.TEXT_MUTED);
        Label w = words(words, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL);
        HBox.setHgrow(w, Priority.ALWAYS);
        HBox row = new HBox(6, mark, w);
        row.setAlignment(Pos.TOP_LEFT);
        return row;
    }

    /* ------------------------------ the lines, under details ------------------------------ */

    /** The Funding page's lines as it printed them until 0.7.33, verbatim (the spec's F1-F7), with B2's colours and B6's verdict. */
    VBox fundingLines(Bank bank, Bank.Ladder l) {
        VBox column = column();

        /* ---------------------------- what is banked ---------------------------- */
        column.getChildren().add(statementHead("What the city has banked with it"));
        column.getChildren().add(statementLine("The families' savings", moneyFull(bank.getHouseholdDeposits())));
        column.getChildren().add(statementLine("The businesses' cash in credit", moneyFull(bank.getSectorDeposits())));
        column.getChildren().add(statementLine("Money from abroad", moneyFull(bank.getForeignDeposits())));
        column.getChildren().add(statementTotal("Deposits", moneyFull(bank.getDeposits()), Palette.TEXT_HEAD));

        /* ---------------------------- what it can lend against (0.7.19) ---------------------------- */
        column.getChildren().add(statementHead("What it can lend against"));
        column.getChildren().add(statementLine("The city's own savings with it", moneyFull(bank.localDeposits())));
        column.getChildren().add(statementLine("...and money from abroad", moneyFull(bank.getForeignDeposits())));
        column.getChildren().add(statementTotal("Deposits it can lend against", moneyFull(bank.depositsGathered()),
                Palette.TEXT_HEAD));
        column.getChildren().add(statementNote(LEND_AGAINST_INFO));

        /* ---------------------------- what it pays savers ---------------------------- */
        column.getChildren().add(statementHead("What it pays savers"));
        column.getChildren().add(statementLine("The policy rate", ratePerYear(l.policy())));
        column.getChildren().add(statementLine("The share of it its funding asks it to pass on",
                share1(l.saversShare())));
        column.getChildren().add(statementLine("The rate it chose, a sixth of the way there a month",
                ratePerYear(l.saversChose())));
        column.getChildren().add(statementLine("What savers were paid", ratePerYear(l.savers()),
                l.saversHeld() ? Palette.WARN : null));
        column.getChildren().add(statementNote(saversWhy(l)));
        column.getChildren().add(statementLine("Paid to the families this month",
                moneyFull(bank.getDepositInterestToHouseholds())));
        column.getChildren().add(statementLine("...to the businesses", moneyFull(bank.getDepositInterestToSectors())));
        if (bank.getDepositInterestToForeign() > 0) {
            column.getChildren().add(statementLine("...and abroad", moneyFull(bank.getDepositInterestToForeign())));
        }
        column.getChildren().add(statementNote(saversShareInfo()));

        /* ------------------------ its account at the central bank ------------------------ */
        column.getChildren().add(statementHead("Its account at the central bank"));
        column.getChildren().add(statementLine("Reserves held there", moneyFull(bank.cashReserves())));
        column.getChildren().add(statementLine("...earning the policy rate", ratePerYear(l.policy())));
        column.getChildren().add(statementLine("...which paid it this month", moneyFull(bank.getPlacementIncome())));
        column.getChildren().add(statementLine("Borrowed overnight from the central bank",
                moneyFull(bank.wholesaleFunding()), bank.wholesaleFunding() > 0 ? Palette.BAD : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("...at the window's rate", ratePerYear(l.window())));
        column.getChildren().add(statementLine("...which cost it this month", moneyFull(bank.getFundingCost())));
        column.getChildren().add(statementNote(String.format(
                "\"The window\" is borrowing overnight from the central bank. It lends a bank whatever it "
                + "needs, at the policy rate plus a %.2f-point penalty - so what stops a bank lending more "
                + "is what that money costs, which is in every loan's rate, and its capital, not the money.",
                CentralBank.WINDOW_PENALTY * 100)));

        /* ---------------------------- how it is funded ---------------------------- */
        column.getChildren().add(statementHead("How its lending is funded"));
        column.getChildren().add(statementLine("Its own capital", moneyFull(bank.equity()),
                bank.equity() < 0 ? Palette.BAD : null));
        column.getChildren().add(statementLine("Savers' deposits it has lent out", moneyFull(bank.depositFunding())));
        column.getChildren().add(statementLine("Money from abroad", moneyFull(bank.getForeignDeposits())));
        column.getChildren().add(statementLine("Borrowed overnight from the central bank",
                moneyFull(bank.wholesaleFunding()), bank.wholesaleFunding() > 0 ? Palette.BAD : null));

        /* ---------------------------- what it can carry ---------------------------- */
        column.getChildren().add(statementHead("What it can carry"));
        column.getChildren().add(statementLine(bank.leverageBinds()
                ? String.format("What its capital carries, weighed, at the %s leverage minimum", share1(Bank.LEVERAGE_RATIO_MIN))
                : String.format("What its capital carries, at the %s minimum", share1(Bank.CAPITAL_RATIO)),
                moneyFull(bank.capitalLimit())));
        column.getChildren().add(statementLine(String.format("What its deposits carry, lent %.0f times over",
                Bank.LEVERAGE), moneyFull(bank.fundingLimit())));
        column.getChildren().add(statementTotal("The tighter of the two",
                bank.isInsolvent() ? "nothing - it has failed" : moneyFull(bank.capacity()),
                bank.isInsolvent() ? Palette.BAD : Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("Its book weighs", moneyFull(bank.getWeightedBook())));
        if (bank.capacity() > 0) {
            column.getChildren().add(statementLine("...which is", share1(bank.strain()) + " of that",
                    bank.strain() > 1 ? Palette.BAD : bank.strain() > Bank.EASY_STRAIN ? Palette.WARN : null));
        }
        column.getChildren().add(statementNote(bank.isInsolvent()
                ? "A failed bank may lend nothing new, so it can carry nothing until it has capital again."
                : bank.capitalBound()
                ? "Its capital is the limit: it lends more as it earns, or as its owners or the city put more in."
                : "Its deposits are the limit: the city has not banked enough with it to lend more."));

        /* ---------------------------- its branches ---------------------------- */
        BuildingsTemplate branch = ui.game.getBuildingManager().getTemplateByName("Commercial Bank");
        int standing = ui.game.getBuildingManager().countByName("Commercial Bank");
        column.getChildren().add(statementHead("Its branches"));
        column.getChildren().add(statementLine("Standing", formatter.format(standing)));
        column.getChildren().add(statementLine("Its customers, the households paying its fee",
                formatter.format(Math.round(bank.getCustomers()))));
        column.getChildren().add(statementLine("...a branch", formatter.format(Math.round(bank.customersPerBranch()))
                + ", against the " + formatter.format(Math.round(Bank.CUSTOMERS_PER_BRANCH)) + " one serves"));
        column.getChildren().add(statementLine("A branch's fees this month", moneyFull(bank.feesPerBranch())));
        column.getChildren().add(statementLine("...against what a branch past the first cost to run last month",
                moneyFull(bank.laterBranchCost())));
        if (branch != null) {
            column.getChildren().add(statementLine("Another costs to build", moneyFull(ui.game.quoteBuild(branch, 1).total)));
        }
        column.getChildren().add(statementLine("...and its owners put in", moneyFull(bank.getPaidInPerBranch())));
        column.getChildren().add(subHead("Would another one pay?"));
        column.getChildren().add(statementLine("Are there customers for it?",
                bank.customersForAnother(standing) ? "yes" : "no",
                bank.customersForAnother(standing) ? Palette.GOOD : Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("Would its share of the fees cover it?",
                bank.feesWouldCoverAnother(standing)
                        ? "yes - " + money(bank.feesPerBranchWithAnother(standing)) + " a branch"
                        : "no - " + money(bank.feesPerBranchWithAnother(standing)) + " a branch",
                bank.feesWouldCoverAnother(standing) ? Palette.GOOD : Palette.TEXT_MUTED));
        Verdict v = branchVerdict();
        column.getChildren().add(sentence(v.words(), v.tone()));
        column.getChildren().add(subHead("Do its branches still pay?"));
        column.getChildren().add(statementLine("Have its fees covered its branches?",
                bank.getUncoveredMonths() == 0 ? "yes, last month"
                        : String.format("not for %d month%s", bank.getUncoveredMonths(),
                                bank.getUncoveredMonths() == 1 ? "" : "s"),
                bank.getUncoveredMonths() == 0 ? Palette.GOOD : Palette.WARN));
        column.getChildren().add(statementNote(branchesInfo()));
        return column;
    }

    /* =====================================================================
       CAPITAL & OWNERS

       Is it strong enough, what does it pay out, and who owns it? Since
       0.7.33 BOTH RATIOS ON THEIR BANDS lead, the one that binds marked (the
       spec's D4): the risk-weighted ratio against the minimum, its target -
       and why the target is where it is - and the top of its band; and since
       round 2 of 0.7.11 the same against everything it has lent (the leverage
       ratio). Then what its rule does with the month's profit; how its equity
       moved, every cause named and a residual that must be nothing
       (Bank.equityMovement(), since 0.7.9 - it left out the founding
       settlement when the screen worked it out); its owners, the Sectors
       screen's card (SectorScreen.ownersCard(), 0.7.30); its rescues and the
       city's preferred. The old lines are under "details".
       ===================================================================== */

    void capitalPage(VBox page) {
        Bank bank = ui.game.getBank();
        page.getChildren().add(row(gaugeCard(bank, false), gaugeCard(bank, true)));
        page.getChildren().add(row(payoutCard(bank), equityMovedCard(bank)));
        VBox owners = ui.sectorScreen.ownersCard(Equity.BANK, bank.equity(), bank.getNetIncome(), true);
        HBox stake = new HBox(Palette.GAP, chip("the city's stake " + share1(ui.game.cityStakeInBank()), Palette.MONEY),
                muted("its fund's shares, in the rescue book"), infoButton(STAKE_INFO, true));
        stake.setAlignment(Pos.CENTER_LEFT);
        if (owners != null) {
            owners.setMaxWidth(Double.MAX_VALUE);
            owners.setPrefWidth(Region.USE_COMPUTED_SIZE);
            owners.getChildren().add(stake);
            page.getChildren().add(owners);
        } else {
            page.getChildren().add(card(cardHead(Icons.STAFF, Palette.MONEY, "ITS OWNERS", null, null),
                    line("No shares in issue yet."), stake));
        }
        VBox rescues = rescuesCard(bank);
        targets.put(RESCUES, rescues);
        page.getChildren().add(preferredHasHistory(bank) ? row(rescues, preferredCard(bank)) : rescues);
        VBox lines = fold("capital:lines", "its capital, against everything it has lent, what it does with its profit, "
                + "how its equity moved, its rescues and the city's preferred, line by line", () -> capitalLines(bank));
        targets.put(DETAILS, lines);
        page.getChildren().add(lines);
    }

    /** The city's stake's note (Capital 2002). */
    static final String STAKE_INFO = "A resolution makes every share the city's, in its fund's rescue book, which its rule "
            + "never sells. The bank's new shares go to others: those it sells to rebuild under its target, and those it "
            + "sells to repay the city's preferred, so the stake falls with both. Warrants the bank has not bought back "
            + "add to it at their expiry, if they are worth anything.";

    /** The band's note (Capital 1896). */
    static String bandInfo() {
        return String.format("Past the top it returns the excess to its owners. The band is %.1f points wide - about what "
                + "Canada's big banks hold over what their regulator expects of them.", Bank.MANAGEMENT_CUSHION * 100);
    }

    /** The leverage measure's note (Capital 1910). */
    static String leverageInfo(Bank bank) {
        return String.format("The backstop to the risk weights: equity of at least %s of everything the bank has lent, "
                + "whatever it weighs - an insured mortgage, which weighs nothing, included. Its own target and band on it "
                + "keep the proportion it chose on the risk side. %s", share1(Bank.LEVERAGE_RATIO_MIN),
                bank.leverageBinds()
                        ? "It is the larger requirement now: what the bank lends is what its capital is short against."
                        : "The risk-weighted requirement is the larger now.");
    }

    /** One of the two gauges, worked out without drawing it (pure: the probe reads them): the risk-weighted ratio, or the leverage ratio when `leverage`. */
    Gauge measureGauge(boolean leverage) {
        Bank bank = ui.game.getBank();
        boolean binds = bank.leverageBinds() == leverage;
        if (leverage) {
            boolean lent = bank.exposure() > 0;
            return new Gauge("LEVERAGE", bank.isInsolvent() ? "failed" : lent ? share1(bank.leverageRatio()) : "nothing lent",
                    binds ? stanceTone(bank) : Palette.TEXT_HEAD,
                    "equity " + m(bank.equity()) + " of " + m(bank.exposure()) + " lent, whatever it weighs",
                    "minimum " + share1(Bank.LEVERAGE_RATIO_MIN) + " · its target " + share1(bank.leverageTarget())
                            + " · top of its band " + share1(bank.leverageTop()),
                    null, bank.isInsolvent() || !lent ? Double.NaN : bank.leverageRatio(),
                    Bank.LEVERAGE_RATIO_MIN, bank.leverageTarget(), bank.leverageTop());
        }
        boolean lent = bank.getWeightedBook() > 0;
        String reason = bank.targetReason();
        int cut = reason.indexOf(" - ");
        return new Gauge("RISK-WEIGHTED", bank.isInsolvent() ? "failed" : lent ? share1(bank.capitalRatio()) : "nothing lent",
                binds ? stanceTone(bank) : Palette.TEXT_HEAD,
                "equity " + m(bank.equity()) + " of a weighted book of " + m(bank.getWeightedBook()),
                "minimum " + share1(Bank.CAPITAL_RATIO) + " · its target " + share1(bank.capitalTarget())
                        + " · top of its band " + share1(bank.capitalTop()),
                "It chose " + (cut > 0 ? reason.substring(0, cut) : reason) + ".",
                bank.isInsolvent() || !lent ? Double.NaN : bank.capitalRatio(),
                Bank.CAPITAL_RATIO, bank.capitalTarget(), bank.capitalTop());
    }

    /** A gauge's card: its caption with BINDS on the larger requirement, the ratio, the bar in its band, the key, what it is of, and why the target is where it is. */
    VBox gaugeCard(Bank bank, boolean leverage) {
        Gauge g = measureGauge(leverage);
        boolean binds = bank.leverageBinds() == leverage;
        String info = leverage ? leverageInfo(bank)
                : "It chose " + bank.targetReason() + ". " + bandInfo();
        HBox head = cardHead(leverage ? Icons.SECTOR : Icons.STAFF, Palette.MONEY, g.caption(), info,
                binds ? chip("BINDS", Palette.TEXT_HEAD) : null);
        VBox c = card(head, figure(g.figure(), 28, g.tone()),
                bandBar(g.value(), g.min(), g.target(), g.top(), g.tone(), 0, 14, true, capitalTip(bank)),
                muted(g.key()), line(g.line()));
        if (g.other() != null) c.getChildren().add(muted(g.other()));
        return c;
    }

    /* ------------------------------ payout, and how its equity moved ------------------------------ */

    /** The payout rule (Capital 1948). */
    static String payoutInfo() {
        return String.format("Under its target it keeps everything. Inside its band it pays out %.0f%% of its profit after "
                + "tax, never so much that it would fall under the target; over the top it also returns a twelfth of the "
                + "excess a month. Its desk buys its own shares back only with what it holds over its target, never taking "
                + "it under, and issues new ones only while it is under it; it buys other companies' shares only while its "
                + "capital carries them at the desk's weight.", Bank.PAYOUT_IN_BAND * 100);
    }

    /** PAYOUT: its decision; last month's profit after tax with what it paid and bought back on it; what it held over its target and the top; the year's. */
    VBox payoutCard(Bank bank) {
        String decision = bank.payoutDecision();
        double profit = bank.getPayoutProfit(), paid = bank.getDividendsPaid(), bought = bank.getSharesBoughtBack();
        double scale = Math.max(Math.max(profit, paid + bought), 1e-9) * 1.05;
        VBox c = card(cardHead(Icons.COIN, Palette.MONEY, "PAYOUT", payoutInfo(), null),
                words(decision.substring(0, 1).toUpperCase() + decision.substring(1) + ".", Palette.SIZE_LABEL + 1,
                        stanceTone(bank)),
                segmentBar(List.of(seg(Math.max(0, paid), Palette.MONEY, "paid to its shareholders\n" + mFull(paid)),
                                seg(Math.max(0, bought), Palette.MONEY_LIGHT, "its own shares bought back\n" + mFull(bought))),
                        scale, profit > 0 ? List.of(tick(profit, Palette.TEXT_HEAD, "last month's profit " + m(profit),
                                "Last month's profit after tax, which it pays on\n" + mFull(profit))) : List.of(), 0, 12),
                keyRow(swatch(Palette.MONEY, paid > 0 ? "paid its owners " + m(paid) : "paid its owners nothing", false),
                        swatch(Palette.MONEY_LIGHT, "bought back " + m(bought), false)),
                line("over its target " + m(bank.getPayoutOverTarget()) + " · past the top " + m(bank.getPayoutExcess())),
                muted("over the last twelve months: dividends " + m(bank.dividendsOverYear()) + " · bought back "
                        + m(bank.buybacksOverYear()) + " · issued " + m(bank.overYear(Bank.Line.ISSUED))));
        return c;
    }

    /** The equity's note (Capital 1990): Jerus's rule on a plug. */
    static final String MOVED_INFO = "Equity moves by what it earned, what it was given and what it paid out, and by "
            + "nothing else. The unaccounted line is printed even at zero, because a plug nobody checks is a lie.";

    /** HOW ITS EQUITY MOVED, worked out without drawing it (pure): each cause as a step, at nothing left off, "Not accounted for" always. */
    List<Step> equitySteps() {
        Bank bank = ui.game.getBank();
        Bank.EquityMovement mv = bank.equityMovement();
        List<Step> steps = new ArrayList<>();
        Object[][] causes = {
                {"What it kept", mv.kept()},
                {"Capital from its shareholders", mv.fromShareholders()},
                {"Capital from the city, resolving it", mv.fromCity()},
                {"The city's preferred, bought", mv.preferredIn()},
                {"The city's preferred, redeemed", -mv.preferredOut()},
                {"Dividends on the city's preferred", -mv.preferredDividends()},
                {"The city's warrants, bought back", -mv.warrantsBoughtBack()},
                {"The founding settlement", mv.founding()},
                {"Paid to its shareholders", -mv.dividends()},
                {"Its own shares bought back", -mv.boughtBack()},
                {"New shares it issued", mv.issued()},
                {"Absorbed when it failed", mv.absorbed()},
                {"Gain on paper the treasury bought back", mv.treasuryBuyback()},
                {"Set aside when this older save was opened", -mv.allowanceOpened()}};
        for (Object[] c : causes) {
            double a = (Double) c[1];
            if (Math.abs(a) < .0005) continue;
            steps.add(Step.of((String) c[0], a, a < 0 ? Palette.MONEY_DARK : Palette.MONEY));
        }
        double residual = mv.residual();
        steps.add(Step.of("Not accounted for", residual, Math.abs(residual) > .005 ? Palette.BAD : Palette.TEXT_SPENT)
                .tip("Not accounted for\n" + mv(residual) + "\n\n" + MOVED_INFO));
        return steps;
    }

    VBox equityMovedCard(Bank bank) {
        VBox c = card(cardHead(Icons.FINANCES, Palette.MONEY, "HOW ITS EQUITY MOVED", MOVED_INFO, null));
        if (!bank.isMonthKnown()) {
            c.getChildren().add(line("Recorded from the next month played: there is no month yet to read it from."));
            return c;
        }
        Bank.EquityMovement mv = bank.equityMovement();
        c.getChildren().add(line("opened the month at " + m(mv.opening()) + " · closed it at " + m(mv.closing())));
        c.getChildren().add(waterfall(equitySteps(), v -> (v < 0 ? "−" : "") + money(Math.abs(v)), 0, 170));
        if (Math.abs(mv.residual()) > .005) {
            c.getChildren().add(noteLine("Something moved its equity without telling it",
                    String.format("%s of the month's change in its equity is none of the causes above. That should be "
                            + "impossible - worth reporting.", m(Math.abs(mv.residual()))), 560));
        }
        return c;
    }

    /* ------------------------------ rescues, and the city's preferred ------------------------------ */

    /** The rescues' note (Capital 2027). */
    static final String RESCUES_INFO = "When a bank loses more than it owns, the city resolves it: the old owners lose "
            + "everything, the city's preferred and warrants go with the hole, and the city pays the hole and the capital "
            + "to reopen - from the treasury's cash, and what that lacks the central bank advances. Nobody outside the "
            + "city pays. It reopens the same month.";

    VBox rescuesCard(Bank bank) {
        java.util.List<TreasuryFund.Resolution> all = ui.game.getFund().getResolutions();
        VBox c = card(cardHead(Icons.ALERT, Palette.MONEY, "RESCUES", RESCUES_INFO, bankDecisionChip()),
                cardLine("Times it has failed", String.valueOf(bank.getFailures()), bank.getFailures() > 0 ? Palette.BAD : null),
                cardLine("What the city has paid to rescue it", mFull(bank.getBailoutsLifetime()), null));
        if (bank.getResolutionLoss() > 0) {
            c.getChildren().add(cardLine("What its creditors absorbed, before 0.7.14", mFull(bank.getResolutionLoss()),
                    Palette.BAD));
        }
        for (int i = all.size() - 1; i >= 0 && i >= all.size() - 5; i--) {
            TreasuryFund.Resolution r = all.get(i);
            c.getChildren().add(cardLine(CityCalendar.format(r.month()),
                    String.format("%s paid (%s from cash, %s advanced) · owners lost %s",
                            m(r.paid()), m(r.fromCash()), m(r.advanced()), m(r.ownersLost())), Palette.TEXT_MUTED));
        }
        if (all.size() > 5) c.getChildren().add(muted((all.size() - 5) + " earlier resolution(s) are in the treasury journal."));
        return c;
    }

    /** The bank's first decision this month, in the order they were made - a rescue, the preferred offer - as a chip (the spec's section 5), or null. */
    Label bankDecisionChip() {
        for (DecisionLog.Entry e : ui.game.getDecisions().inMonth(ui.game.getMonth())) {
            if (DecisionLog.BANK.equals(e.kind())) return chipTip("this month: " + e.label(), Palette.MONEY, e.label());
        }
        return null;
    }

    /** Whether the city's preferred has any history to show. */
    static boolean preferredHasHistory(Bank bank) {
        return bank.preferredOutstanding() > 0 || bank.getPreferredDividendsLifetime() > 0
                || bank.getPreferredRedeemedLifetime() > 0 || bank.getWarrantsBoughtBackLifetime() > 0
                || bank.warrantSharesOut() > 0 || bank.getRepaymentRaisedLifetime() > 0;
    }

    /** The preferred's note (Capital 2046). */
    static String preferredInfo() {
        return String.format("Each block is repaid whole at its %d-month anniversary, at par with its unpaid dividends: from "
                + "what the bank holds over its target first, and the rest by selling new shares to the public - the "
                + "households, then the world - which dilutes the city's own. Once none is left it buys the city's warrants "
                + "back at their value, the same way. No common dividend is paid while any preferred dividend is owed.",
                Bank.PREFERRED_REDEEM_MONTHS);
    }

    VBox preferredCard(Bank bank) {
        return card(cardHead(Icons.GOVERNMENT, Palette.MONEY, "THE CITY'S PREFERRED", preferredInfo(), null),
                cardLine("Outstanding, at par", mFull(bank.preferredOutstanding()), null),
                cardLine("...its dividends owed and unpaid", mFull(bank.getPreferredArrears()),
                        bank.getPreferredArrears() > 0 ? Palette.WARN : null),
                cardLine("Dividends paid on it, over its life", mFull(bank.getPreferredDividendsLifetime()), null),
                cardLine("Redeemed, over its life", mFull(bank.getPreferredRedeemedLifetime()), null),
                cardLine("Warrants bought back, over its life", mFull(bank.getWarrantsBoughtBackLifetime()), null),
                cardLine("New shares it sold to repay the city, over its life", mFull(bank.getRepaymentRaisedLifetime()), null),
                cardLine("Warrants still out, on this many shares", String.format("%,.3f", bank.warrantSharesOut()), null),
                cardLine("The consent binds - no buyback, no higher dividend a share", bank.inConsentPeriod() ? "yes" : "no", null));
    }

    /* ------------------------------ the lines, under details ------------------------------ */

    /** The Capital & owners page's lines as it printed them until 0.7.33, verbatim (the spec's C1-C8), with B2's colours. */
    VBox capitalLines(Bank bank) {
        VBox column = column();
        boolean lent = bank.getWeightedBook() > 0;

        /* ------------------------------ its capital ------------------------------ */
        column.getChildren().add(statementHead("Its capital"));
        column.getChildren().add(statementLine("Equity", moneyFull(bank.equity()),
                bank.equity() < 0 ? Palette.BAD : null));
        column.getChildren().add(statementLine("Its risk-weighted book", moneyFull(bank.getWeightedBook())));
        column.getChildren().add(statementTotal("Capital ratio",
                bank.isInsolvent() ? "failed" : lent ? share1(bank.capitalRatio()) : "nothing lent",
                stanceTone(bank)));
        column.getChildren().add(statementLine("The minimum the city requires", share1(Bank.CAPITAL_RATIO)));
        column.getChildren().add(statementLine("Its own target", share1(bank.capitalTarget()), Palette.TEXT_HEAD));
        column.getChildren().add(statementNote("It chose " + bank.targetReason() + "."));
        column.getChildren().add(statementLine("The top of its band", share1(bank.capitalTop())));
        column.getChildren().add(statementNote(bandInfo()));

        /* ---------------- ...and against everything it has lent (0.7.11, round 2) ---------------- */
        column.getChildren().add(subHead("...and against everything it has lent"));
        column.getChildren().add(statementLine("Everything on its books, whatever it weighs", moneyFull(bank.exposure())));
        column.getChildren().add(statementTotal("Leverage ratio",
                bank.isInsolvent() ? "failed" : bank.exposure() > 0 ? share1(bank.leverageRatio()) : "nothing lent",
                bank.leverageBinds() ? stanceTone(bank) : Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("The minimum, Basel III's", share1(Bank.LEVERAGE_RATIO_MIN)));
        column.getChildren().add(statementLine("Its own target on it", share1(bank.leverageTarget()), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("The top of its band on it", share1(bank.leverageTop())));
        column.getChildren().add(statementNote(leverageInfo(bank)));

        /* ------------------------ what it does with its profit ------------------------ */
        column.getChildren().add(statementHead("What it does with its profit"));
        String decision = bank.payoutDecision();
        column.getChildren().add(sentence(decision.substring(0, 1).toUpperCase() + decision.substring(1) + ".",
                stanceTone(bank)));
        column.getChildren().add(statementLine("Last month's profit after tax, which it pays on",
                moneyFull(bank.getPayoutProfit())));
        column.getChildren().add(statementLine("What it held over its target", moneyFull(bank.getPayoutOverTarget())));
        column.getChildren().add(statementLine("...and past the top of its band", moneyFull(bank.getPayoutExcess())));
        column.getChildren().add(statementLine("Paid to its shareholders this month", moneyFull(bank.getDividendsPaid())));
        column.getChildren().add(statementLine("...over the last twelve months", moneyFull(bank.dividendsOverYear()),
                Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("Its own shares bought back this month",
                moneyFull(bank.getSharesBoughtBack())));
        column.getChildren().add(statementLine("...over the last twelve months", moneyFull(bank.buybacksOverYear()),
                Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("New shares issued this month", moneyFull(bank.getSharesIssued())));
        column.getChildren().add(statementLine("...over " + yearWords(bank), moneyFull(bank.overYear(Bank.Line.ISSUED)),
                Palette.TEXT_MUTED));
        column.getChildren().add(statementNote(payoutInfo()));

        /* ------------------------------ how its equity moved ------------------------------ */
        column.getChildren().add(statementHead("How its equity moved this month"));
        if (!bank.isMonthKnown()) {
            column.getChildren().add(sentence("Recorded from the next month played: there is no month yet "
                    + "to read it from.", Palette.TEXT_MUTED));
        } else {
            Bank.EquityMovement m = bank.equityMovement();
            column.getChildren().add(statementLine("At the start of the month", moneyFull(m.opening())));
            column.getChildren().add(statementLine("What it kept", signed(m.kept(), false),
                    m.kept() < 0 ? Palette.BAD : null));
            moved(column, "Capital from its shareholders", m.fromShareholders());
            moved(column, "Capital from the city, resolving it", m.fromCity());
            moved(column, "The city's preferred, bought", m.preferredIn());
            moved(column, "The city's preferred, redeemed", -m.preferredOut());
            moved(column, "Dividends on the city's preferred", -m.preferredDividends());
            moved(column, "The city's warrants, bought back", -m.warrantsBoughtBack());
            moved(column, "The founding settlement, taking over the city's loans", m.founding());
            moved(column, "Paid to its shareholders", -m.dividends());
            moved(column, "Its own shares bought back", -m.boughtBack());
            moved(column, "New shares it issued", m.issued());
            moved(column, "Absorbed when it failed", m.absorbed());
            moved(column, "Gain on paper the treasury bought back", m.treasuryBuyback());
            moved(column, "Set aside when this older save was opened", -m.allowanceOpened());
            double residual = m.residual();
            boolean off = Math.abs(residual) > .005;
            column.getChildren().add(statementLine("Not accounted for", signed(residual, false),
                    off ? Palette.BAD : Palette.TEXT_SPENT));
            column.getChildren().add(statementTotal("At the end of it", moneyFull(m.closing()),
                    bank.isInsolvent() ? Palette.BAD : Palette.TEXT_HEAD));
            if (off) {
                column.getChildren().add(alert("Something moved its equity without telling it",
                        String.format("%s of the month's change in its equity is none of the causes above. "
                        + "That should be impossible - worth reporting.", money(Math.abs(residual)))));
            } else {
                column.getChildren().add(statementNote(MOVED_INFO));
            }
        }

        /* -------------------------------- its owners -------------------------------- */
        column.getChildren().add(statementHead("Its owners"));
        column.getChildren().add(statementLine("The city's stake, its fund's shares", share1(ui.game.cityStakeInBank()),
                Palette.TEXT_HEAD));
        column.getChildren().add(statementNote(STAKE_INFO));

        /* -------------------------------- its rescues -------------------------------- */
        column.getChildren().add(statementHead("Its rescues"));
        column.getChildren().add(statementLine("Times it has failed", String.valueOf(bank.getFailures()),
                bank.getFailures() > 0 ? Palette.BAD : null));
        column.getChildren().add(statementLine("What the city has paid to rescue it", moneyFull(bank.getBailoutsLifetime())));
        if (bank.getResolutionLoss() > 0) {
            column.getChildren().add(statementLine("What its creditors absorbed, before 0.7.14",
                    moneyFull(bank.getResolutionLoss()), Palette.BAD));
        }
        java.util.List<TreasuryFund.Resolution> all = ui.game.getFund().getResolutions();
        for (int i = all.size() - 1; i >= 0 && i >= all.size() - 5; i--) {
            TreasuryFund.Resolution r = all.get(i);
            column.getChildren().add(statementLine("   " + CityCalendar.format(r.month()),
                    String.format("%s paid (%s from cash, %s advanced)  ·  owners lost %s",
                            money(r.paid()), money(r.fromCash()), money(r.advanced()), money(r.ownersLost()))));
        }
        if (all.size() > 5) {
            column.getChildren().add(statementNote((all.size() - 5) + " earlier resolution(s) are in the treasury journal."));
        }
        column.getChildren().add(statementNote(RESCUES_INFO));

        /* -------------------------------- the city's preferred -------------------------------- */
        column.getChildren().add(statementHead("The city's preferred"));
        column.getChildren().add(statementLine("Outstanding, at par", moneyFull(bank.preferredOutstanding())));
        column.getChildren().add(statementLine("...its dividends owed and unpaid", moneyFull(bank.getPreferredArrears()),
                bank.getPreferredArrears() > 0 ? Palette.WARN : null));
        column.getChildren().add(statementLine("Dividends paid on it, over its life", moneyFull(bank.getPreferredDividendsLifetime())));
        column.getChildren().add(statementLine("Redeemed, over its life", moneyFull(bank.getPreferredRedeemedLifetime())));
        column.getChildren().add(statementLine("Warrants bought back, over its life", moneyFull(bank.getWarrantsBoughtBackLifetime())));
        column.getChildren().add(statementLine("New shares it sold to repay the city, over its life",
                moneyFull(bank.getRepaymentRaisedLifetime())));
        column.getChildren().add(statementLine("Warrants still out, on this many shares", String.format("%,.3f", bank.warrantSharesOut())));
        column.getChildren().add(statementLine("The consent binds - no buyback, no higher dividend a share",
                bank.inConsentPeriod() ? "yes" : "no"));
        column.getChildren().add(statementNote(preferredInfo()));
        return column;
    }

    /** One cause of the equity's movement, printed only when it moved it - plain since 0.7.33 (B2: it was green in and amber out). */
    void moved(VBox column, String label, double amount) {
        if (Math.abs(amount) < .0005) return;
        column.getChildren().add(statementLine(label, signed(amount, false), null));
    }

    /* =====================================================================
       THE RESCUE, WHEREVER THE PLAYER IS LOOKING.

       Jerus: "when the bank has an issue, and you click go to bank, the
       recapitalise the bank button is quite hidden, make it so that in the
       bank section its on the top, not all the way hidden in the balance
       sheet."

       ONE CARD, at the top of every page of the tab since 0.7.33 (the
       landing and the Capital page until then), where a failed bank is the
       only thing worth reading. Since 0.7.14 the button is the whole rescue,
       nothing to choose: the resolution (Game.resolveBank()) the treasury's
       automatic setting runs the month the bank fails. Shown only while the
       bank waits frozen (Game.canResolveBank()) - its button is; the card
       stands while the bank is failed. It needs no cash in hand: what the
       treasury lacks, the central bank advances.
       ===================================================================== */

    /** The rescue's note (Rescue 2119). */
    static final String RESCUE_INFO = "Its owners lose everything: the households' shares and the world's pass to the city "
            + "for nothing, and the city's own preferred and warrants go with the hole. The city pays from the treasury's "
            + "cash first, and what that lacks the central bank advances at the next settle, past its ceiling if it must. "
            + "The bank reopens this month, and every share is the city's fund's rescue book. With the treasury's setting "
            + "on automatic (Finances, When the bank fails) it happens the month the bank fails.";

    VBox bankRescue() {
        Bank bank = ui.game.getBank();
        double needed = ui.game.bankRecapitalisationNeeded();
        boolean can = ui.game.canResolveBank();
        GridPane four = equalColumns(4, TILE_GAP);
        four.add(rescueFigure("THE HOLE AND THE CAPITAL TO REOPEN", mFull(needed), Palette.BAD), 0, 0);
        four.add(rescueFigure("OF IT, THE CAPITAL TO REOPEN", mFull(bank.resolutionExitEquity()), Palette.TEXT_HEAD), 1, 0);
        four.add(rescueFigure("THE TREASURY HOLDS", mFull(ui.game.getCash()), Palette.TEXT_HEAD), 2, 0);
        four.add(rescueFigure("THE CENTRAL BANK WOULD ADVANCE", mFull(ui.game.bankResolutionAdvance()),
                ui.game.bankResolutionAdvance() > 0 ? Palette.WARN : Palette.TEXT_HEAD), 3, 0);
        Button rescue = new Button("Resolve the bank for its shares – " + money(needed));
        rescue.setDisable(!can);
        if (can) {
            rescue.setStyle("-fx-background-color: " + Palette.CONFIRM + "; -fx-text-fill: white;"
                    + " -fx-padding: 8 18 8 18;");
        }
        rescue.setOnAction(e -> {
            if (!ui.game.canResolveBank()) return;
            ui.game.resolveBank();
            ui.innerScrollAt.remove("showBankMenu:body");
            showBankMenu();
        });
        VBox c = actionCard(Icons.ALERT, Palette.BAD, "The bank has failed",
                "It lost more than it owned, and lends nothing new until the city resolves it.",
                "It lost more than it owned. Frozen, it lends nothing new and carries its hole until the city resolves "
                        + "it - here, or the month it fails when the treasury's setting is automatic - or until it earns "
                        + "its way back out, slowly, on the book it already has.", null);
        c.getChildren().addAll(four, rescue, noteLine("Its owners lose everything; the city pays, and every share is its fund's.",
                RESCUE_INFO, 1100));
        return c;
    }

    /** One of the rescue's four figures: its caption over it. */
    static VBox rescueFigure(String caption, String value, String tone) {
        return new VBox(1, muted(caption), figure(value, Palette.SIZE_BODY + 3, tone));
    }

    /* =====================================================================
       THE BANK ASKS FOR PREFERRED (0.7.14)

       Jerus: "its a popup message saying bank wants to issue you shares or
       something". The game asks its questions through the inbox: the
       notice's button opens the Bank tab, and this card is the answer - at
       the top of every page while the offer waits. Accept buys it through
       the treasury (Game.acceptPreferredOffer()); a treasury short of it is
       offered two loans first, as the land office's cards side by side
       (Pieces.offerCard(), since 0.7.33; Build's statement page's until
       then), then buys. Decline, and the bank asks again in
       a quarter while it is still under its minimum.
       ===================================================================== */

    VBox preferredOffer() {
        Game game = ui.game;
        Bank bank = game.getBank();
        String here = game.getCurrency().qualifiedSymbol();
        double size = game.preferredOfferSize();
        double shortBy = game.preferredOfferShortBy();
        java.util.function.DoubleFunction<String> written = v -> marked(here, money(v));

        String terms = String.format("%s of preferred shares: %.0f%% a year for five years, then %.0f%%, repayable at par "
                        + "after three years; with warrants on %s of its shares at %s.", marked(here, money(size)),
                Bank.PREFERRED_RATE * 100, Bank.PREFERRED_STEP_RATE * 100,
                marked(here, money(game.preferredOfferWarrantValue())), marked(here, unitPrice(game.preferredOfferStrike())));
        VBox c = actionCard(Icons.BANK, Palette.WARN, "The bank asks the city for capital",
                "It is under its minimum capital and asks the city to buy " + terms,
                "The bank is under its minimum capital and asks the city to buy " + terms + " The terms: its dividend is "
                        + "cumulative - what the bank cannot pay accrues - and no common dividend is paid while any is owed. "
                        + "For three years it buys back none of its shares and raises no dividend a share. It counts in the "
                        + "bank's capital, ahead of its common. At its third anniversary the bank repays it at par with any "
                        + "dividends still owed - from its capital over its target, and the rest by selling new shares to the "
                        + "public. Once no preferred is left it buys the warrants back at their value, or they are taken as "
                        + "shares at ten years if they are worth anything. In a failure the preferred and its warrants go "
                        + "with the hole.", null);
        if (bank.preferredOfferCapped()) {
            c.getChildren().add(noteLine(String.format("That is %.0f%% of its risk-weighted book, the most it may ask.",
                            Bank.PREFERRED_MAX_SHARE * 100),
                    String.format("That is %.0f%% of its risk-weighted book, the most it may ask; the %s it is still short of "
                            + "its target is left to its own share issues.", Bank.PREFERRED_MAX_SHARE * 100,
                            marked(here, money(bank.preferredOfferShortOfTarget()))), 1100));
        }
        c.getChildren().add(cardLine("The treasury holds", marked(here, money(game.getCash())),
                shortBy > 0 ? Palette.BAD : null));
        if (shortBy > 0) {
            c.getChildren().add(cardLine("Funding required", marked(here, money(shortBy)), Palette.BAD));
            DebtQuote bond = game.quoteLongBondForCash(shortBy, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
            DebtQuote note = game.quoteTBill(shortBy, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
            DebtManager market = game.getDebtManager();
            VBox bondCard = offerCard(Game.BUILD_BOND_YEARS + "-year bond", null, bond,
                    pct2(bond.marketRate()) + " yield  ·  " + pct2(bond.couponRate()) + " coupon",
                    rateColour(bond, market),
                    String.format("Paid over %d years: the coupon every month, then the whole %s at the end.",
                            bond.duration(), written.apply(bond.faceValue())),
                    "Issue the " + Game.BUILD_BOND_YEARS + "-year bond and accept",
                    () -> {
                        game.handleLongBondForCash(game.preferredOfferShortBy(), Game.BUILD_BOND_YEARS,
                                Game.BUILD_BOND_GRANULE);
                        game.acceptPreferredOffer();
                        showBankMenu();
                    }, written);
            VBox noteCard = offerCard(Game.BUILD_NOTE_MONTHS + "-month note", null, note,
                    pct2(note.marketRate()) + " a year, taken as a discount", rateColour(note, market),
                    String.format(game.getRollover().getMode() == Rollover.Mode.MANUAL
                                    ? "Falls due in %d months: the whole %s at once, out of the treasury."
                                    : "Falls due in %d months: the whole %s at once, refinanced then by the treasury's rollover.",
                            note.duration(), written.apply(note.faceValue())),
                    "Issue the " + Game.BUILD_NOTE_MONTHS + "-month note and accept",
                    () -> {
                        game.handleTBillLogic(game.preferredOfferShortBy(), Game.BUILD_NOTE_MONTHS,
                                Game.BUILD_NOTE_GRANULE);
                        game.acceptPreferredOffer();
                        showBankMenu();
                    }, written);
            GridPane two = row(bondCard, noteCard);
            two.setMaxWidth(820);
            c.getChildren().add(two);
            c.getChildren().add(noteLine("Borrowing for it has the bank capitalise itself.",
                    "The city borrows FROM this bank. Paper issued to buy its preferred is paper the bank buys, so the "
                            + "cash comes back to it as capital and its balance sheet has grown on both sides without "
                            + "anybody putting anything in.", 1100));
        }
        Button accept = new Button("Accept – buy " + marked(here, money(size)));
        accept.setDisable(shortBy > 0);
        if (!(shortBy > 0)) {
            accept.setStyle("-fx-background-color: " + Palette.CONFIRM + "; -fx-text-fill: white;"
                    + " -fx-padding: 8 18 8 18;");
        }
        accept.setOnAction(e -> { game.acceptPreferredOffer(); showBankMenu(); });
        Button decline = new Button("Decline");
        decline.setOnAction(e -> { game.declinePreferredOffer(); showBankMenu(); });
        HBox act = new HBox(8, accept, decline);
        act.setAlignment(Pos.CENTER_LEFT);
        c.getChildren().add(act);
        c.getChildren().add(noteLine(String.format("Declined, it asks again in %d months.", TreasuryFund.OFFER_AGAIN_MONTHS),
                String.format("Declined, it asks again in %d months if it is still under its minimum. Left unanswered it "
                        + "waits, and a time skip passes without the city's capital.", TreasuryFund.OFFER_AGAIN_MONTHS), 1100));
        return c;
    }

    /* =====================================================================
       HISTORY

       How has it done over the city's life? Its rates as City History's
       chart (TimeChart, 0.7.33; the statement's trend chart until then):
       the policy rate, what savers got and prime, with the central bank's
       and the bank's own decisions as flags - the dial moving under them, a
       rescue, the preferred - and the recessions as bands. Under it six small
       charts on its window, each on one scale in one unit: its capital
       against its target, its return on equity against what its owners want,
       its provisions against its write-offs, how full it is (Bank.strain(),
       with its 100% line - the spec's D12: the old chart set the face of the
       book against what its capital carries weighed, which reads 94 times
       over in the 2,400-month city while it is 38% full), what it lent and
       what was deposited, and its fees. The statistics that mean something
       since 0.7.8 are chips under them - months under its target and under
       the minimum, months it lost money, its worst year of provisions -
       asked of the record (HistorySave).

       THE CAPITAL CHART IS THE RISK-WEIGHTED RATIO, and says so (the spec's
       B7, D11): HistorySave keeps that one only, clamped at ten, so while
       the leverage ratio binds the chart says which measure decides it now.
       Keeping the leverage ratio and its target as series is a later model
       batch (a saved series, not this screen's).

       DRAWN HERE, NOT ONLY LINKED TO CITY HISTORY ("Over the years ›" opens
       it on prime and the policy rate), on purpose: the bank's lines share a
       scale on each chart, which the picker chart does not.
       ===================================================================== */

    void historyPage(VBox page) {
        Bank bank = ui.game.getBank();
        HistorySave h = ui.game.getHistorySave();
        if (h.months() < 2) {
            page.getChildren().add(card(cardHead(Icons.REPORTS, Palette.MONEY, "NOTHING TO DRAW YET", null, null),
                    line(String.format("The city has lived %d month%s and a line needs two points. Come back in a year.",
                            h.months(), h.months() == 1 ? "" : "s"))));
            return;
        }
        List<YearBook.Band> bands = ChartModel.bands(h);
        TimeChart rates = chart(h, List.of(
                        percentLine(h, "policyRate", "The policy rate", Palette.TEXT_MUTED),
                        percentLine(h, "bankDepositRate", "What savers got", Palette.PEOPLE),
                        percentLine(h, "bankPrime", "Prime", Palette.MONEY)),
                "percent", bands, ChartModel.onAxis(ChartModel.flagsOf(ui.game.getDecisions(), DecisionLog.CENTRAL_BANK,
                        DecisionLog.BANK), h.getMonth().get(0)),
                null, 1180, 240);
        page.getChildren().add(card(cardHead(Icons.REPORTS, Palette.MONEY, "ITS RATES",
                        "Prime sits over the policy rate by the bank's costs; savers under it by its margin on a deposit. "
                        + "A flag is a decision under them - the central bank's dial, a rescue, the preferred offer; a "
                        + "band is a recession. Hover either for what it was. The charts under it show the same years, "
                        + "the last ten; Over the years opens City History, which goes back to the founding.",
                        door("Over the years", Palette.ACCENT, () -> ui.historyScreen.openOn("bankPrime", "policyRate"))),
                rates,
                keyRow(swatch(Palette.TEXT_MUTED, "the policy rate", false), swatch(Palette.PEOPLE, "what savers got", false),
                        swatch(Palette.MONEY, "prime", false))));

        // While the leverage ratio binds, the card draws that measure (0.7.46, A7), once History has two months of it.
        boolean leverageDrawn = bank.leverageBinds() && h.monthsRecorded("bankLeverageRatio") >= 2;
        String ratioKey = leverageDrawn ? "bankLeverageRatio" : "bankCapitalRatio";
        String targetKey = leverageDrawn ? "bankLeverageTarget" : "bankCapitalTarget";
        int capitalMonths = h.monthsRecorded(ratioKey);
        int underTarget = h.monthsUnder(ratioKey, targetKey);
        int underMinimum = h.monthsUnder(ratioKey, leverageDrawn ? Bank.LEVERAGE_RATIO_MIN : Bank.CAPITAL_RATIO);
        List<Node> capitalChips = new ArrayList<>(List.of(
                chip("under its target " + underTarget + " of " + capitalMonths + " months",
                        underTarget > 0 ? Palette.WARN : Palette.TEXT_MUTED),
                chip("under the minimum " + underMinimum + " of " + capitalMonths + " months",
                        underMinimum > 0 ? Palette.BAD : Palette.TEXT_MUTED)));
        if (bank.leverageBinds()) {
            capitalChips.add(0, chip(String.format("the leverage ratio binds now: %s of %s",
                    share1(bank.leverageRatio()), share1(bank.leverageTarget())), Palette.TEXT_HEAD));
        }
        VBox capital = leverageDrawn
                ? smallCard("THE LEVERAGE RATIO", "Its capital against everything on its sheet, and the target it holds "
                        + "on that measure - the one that binds now, so the months under its target and under the "
                        + "minimum count it. Recorded to a ceiling of 1,000%, from 0.7.46 on.",
                "percent", rates, h, bands, List.of(
                        percentLine(h, ratioKey, "Leverage ratio", Palette.MONEY),
                        percentLine(h, targetKey, "Its target", Palette.TEXT_MUTED)), capitalChips)
                : smallCard("RISK-WEIGHTED CAPITAL", "Its capital against its risk-weighted book, and the target it "
                        + "chose. Recorded to a ceiling of 1,000%: a young bank's capital against a tiny book is enormous, "
                        + "and nothing lent at all is drawn at the ceiling. The months under its target and under the "
                        + "minimum count this measure; while the leverage ratio binds, the other measure decides.",
                "percent", rates, h, bands, List.of(
                        percentLine(h, ratioKey, "Capital ratio", Palette.MONEY),
                        percentLine(h, targetKey, "Its target", Palette.TEXT_MUTED)), capitalChips);

        int losing = h.monthsUnder("bankProfit", 0.0);
        VBox roe = smallCard("RETURN ON EQUITY", String.format("Each month's profit at a yearly rate, on the equity it opened "
                        + "with - so it swings; its owners want %s over time, the grey line. Recorded between −1,000%% and "
                        + "1,000%%.", ratePct(Bank.requiredReturn())),
                "percent", rates, h, bands, List.of(
                        percentLine(h, "bankReturnOnEquity", "Return on equity", Palette.MONEY),
                        flatLine(h, "required", "What its owners want", Bank.requiredReturn(), Palette.TEXT_MUTED, "percent")),
                List.of(chip("lost money in " + losing + " of " + h.monthsRecorded("bankProfit") + " months",
                        losing > 0 ? Palette.WARN : Palette.TEXT_MUTED)));

        VBox losses = smallCard("SET ASIDE, AND WRITTEN OFF", "It sets money aside as a borrower weakens, so a loss shows "
                        + "here before it is written off. A whole business sector is one borrower in this city, so losses "
                        + "come in lumps.",
                "money", rates, h, bands, List.of(
                        moneyLine(h, "bankProvisions", "Provisions", Palette.MONEY),
                        moneyLine(h, "bankWriteOffs", "Written off", Palette.MONEY_DARK)),
                List.of(chip("its worst year of provisions " + m(h.worstYear("bankProvisions")), Palette.TEXT_MUTED),
                        chip("as its capital target reads it " + share1(bank.getWorstLossRate()) + " of its risk-weighted book",
                                Palette.TEXT_MUTED)));

        VBox full = smallCard("HOW FULL", "What it has lent, weighed for risk and term, over what its capital and its "
                        + "deposits carry - Bank.strain(), the Overview's HOW FULL and NEEDS YOU's THE BANK row. Past the "
                        + "100% line it lends past what it counts as full. Recorded to a ceiling of 1,000%.",
                "percent", rates, h, bands, List.of(
                        percentLine(h, "bankStrain", "How full", Palette.MONEY),
                        flatLine(h, "full", "Full", 1, Palette.TEXT_MUTED, "percent")),
                List.of(chip(String.format("now %.0f%%", bank.strain() * 100), Palette.TEXT_MUTED)));

        VBox lent = smallCard("LENT, AND DEPOSITED", "What it has lent, loans at face and bonds at cost, and what the city "
                        + "has banked with it - money against money. How full it is, which sets the book weighed against "
                        + "what it can carry, is the HOW FULL chart.",
                "money", rates, h, bands, List.of(
                        moneyLine(h, "bankLent", "Lent out", Palette.MONEY),
                        moneyLine(h, "bankDeposits", "Deposits", Palette.PEOPLE)), List.of());

        VBox fees = smallCard("ITS FEES", "Its fees a month: the families' accounts, the businesses' new loans, the families' "
                        + "new borrowing, and underwriting the businesses' bonds.",
                "money", rates, h, bands, List.of(moneyLine(h, "bankFees", "Fees a month", Palette.MONEY)), List.of());

        GridPane six = equalColumns(2, TILE_GAP);
        VBox[] cards = {capital, roe, losses, full, lent, fees};
        for (int i = 0; i < cards.length; i++) {
            GridPane.setFillHeight(cards[i], true);
            cards[i].setMaxHeight(Double.MAX_VALUE);
            six.add(cards[i], i % 2, i / 2);
        }
        page.getChildren().add(six);
    }

    /** A line of a History series in per cent, read out as a rate. */
    TimeChart.Line percentLine(HistorySave h, String key, String name, String colour) {
        return new TimeChart.Line(key, name, colour, 0, h.aligned(key),
                v -> HistoryScreen.plotScale("percent", v), v -> ratePct(v), "");
    }

    /** ...in money. */
    TimeChart.Line moneyLine(HistorySave h, String key, String name, String colour) {
        return new TimeChart.Line(key, name, colour, 0, h.aligned(key),
                v -> HistoryScreen.plotScale("money", v), v -> ui.historyScreen.fmtUnit("money", v), "");
    }

    /** A reference line at one level for every month: what its owners want, the 100% line. */
    static TimeChart.Line flatLine(HistorySave h, String key, String name, double level, String colour, String unit) {
        double[] values = new double[h.getMonth().size()];
        java.util.Arrays.fill(values, level);
        return new TimeChart.Line(key, name, colour, 0, values,
                v -> HistoryScreen.plotScale(unit, v), v -> ratePct(v), "");
    }

    /** One of the charts, on the tab's window (it follows `leader` when there is one), without controls. */
    TimeChart chart(HistorySave h, List<TimeChart.Line> lines, String unit, List<YearBook.Band> bands,
                    List<ChartModel.Flag> flags, TimeChart leader, double width, double height) {
        TimeChart chart = new TimeChart(historyWindow, java.util.Set.of(), false);
        if (leader != null) chart.follow(leader);
        chart.setData(h.getMonth(), lines, HistoryScreen.axisFor(unit), null, false, false, null,
                bands, List.of(), flags, "no months recorded yet");
        chart.setSize(width, height);
        return chart;
    }

    /** A small chart's card: its title and (i), the chart following the rates' window, its key, and its statistics as chips. */
    VBox smallCard(String title, String info, String unit, TimeChart leader, HistorySave h, List<YearBook.Band> bands,
                   List<TimeChart.Line> lines, List<Node> chips) {
        TimeChart c = chart(h, lines, unit, bands, List.of(), leader, 575, 150);
        javafx.scene.layout.FlowPane key = keyRow();
        for (TimeChart.Line l : lines) key.getChildren().add(swatch(l.colour(), l.label(), false));
        VBox card = card(cardHead(null, null, title, info, null), c, key);
        if (!chips.isEmpty()) {
            javafx.scene.layout.FlowPane row = new javafx.scene.layout.FlowPane(6, 6);
            row.getChildren().addAll(chips);
            card.getChildren().add(row);
        }
        return card;
    }

}
