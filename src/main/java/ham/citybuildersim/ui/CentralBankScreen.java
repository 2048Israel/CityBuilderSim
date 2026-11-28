package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.BankScreen.card;
import static ham.citybuildersim.ui.BankScreen.cardHead;
import static ham.citybuildersim.ui.BankScreen.cardLine;
import static ham.citybuildersim.ui.BankScreen.doorCard;
import static ham.citybuildersim.ui.BankScreen.figure;
import static ham.citybuildersim.ui.BankScreen.line;
import static ham.citybuildersim.ui.BankScreen.m;
import static ham.citybuildersim.ui.BankScreen.muted;
import static ham.citybuildersim.ui.BankScreen.mv;
import static ham.citybuildersim.ui.BankScreen.pts;
import static ham.citybuildersim.ui.BankScreen.words;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Pieces.*;

/**
 * The central bank's pages in the Bank tab: its frame - the head "Bank › Central bank", the strip of nine chips - and the
 * nine pages behind them: the Overview; its income statement, its balance sheet and the money it made and destroyed; its
 * Holdings (the city's paper it holds by when each piece matures, at face and at market, what it bought and sold, and what
 * the holdings take off the curve); Inflation &amp; credibility (prices against the target, how far people trust it, the real
 * rate and what the rule would set); Money (M0 in its parts against M2, the vault, the gold, what it has made since
 * founding); its History (the charts of the series City History keeps for it); and its Policy (the dials that apply at once,
 * Policy's own cards drawn here, beside the policy rate read and a door to where it is set). BankScreen owns it: it is drawn
 * inside BankScreen's frame and kept pool, reached through the tab's choice of the two banks (BankScreen.choicePage()) or the
 * rail's bank icon.
 *
 * <p>WHY. Jerus, 2026-11-26: "when you click bank you have two options, the commercial bank, and the central bank ... give
 * both the banks proper statements, aka ability to not only view summary but also the statements same way the other
 * sectors, but make sure to really include alot of stuff into the central bank area". The central bank was a card on
 * Finances > Money and another on Government, each a figure or two read once; it had no page of its own, no last month, no
 * year ago and no statement that added up. The statements here are CentralBankStatements' tables drawn through
 * StatementView as the sectors' and the commercial bank's are (BankScreen.profitStatementView()), beside ratios whose parts
 * are the bank's own getters; the screen sums and works out nothing a statement already holds. The five pages after them
 * read the same getters as cards (the holdings' ladder is CentralBankStatements.ladder()), and work out no more than a
 * share of one getter over another.
 */
final class CentralBankScreen {

    /** The window this screen draws into: its game, its redraw. */
    private final UserInterface ui;

    /** The Bank tab this screen is a side of: the way back to the choice, the frame, and the formatters. */
    private final BankScreen bank;

    CentralBankScreen(UserInterface ui, BankScreen bank) {
        this.ui = ui;
        this.bank = bank;
    }

    /* =====================================================================
       THE FRAME (CBK1, 2026-11-26)

       The head ("Bank › Central bank", the first word a way back to the
       choice, the second a way back to its Overview from a page) with the
       door to the policy rate it sets; the pages as chips, Overview first.
       ===================================================================== */

    /** The Overview, and the eight pages behind it. */
    static final String OVERVIEW = "Overview", INCOME = "Income statement", SHEET = "Balance sheet",
            MADE_AND_DESTROYED = "Money made and destroyed", HOLDINGS = "Holdings",
            INFLATION = "Inflation & credibility", MONEY = "Money", HISTORY = "History", POLICY = "Policy";

    /** The nine chips, in the strip's order: the Overview first. */
    static final String[] PAGES = {OVERVIEW, INCOME, SHEET, MADE_AND_DESTROYED, HOLDINGS, INFLATION, MONEY, HISTORY, POLICY};

    /** ...and each chip's icon. */
    static final String[] ICONS = {Icons.OVERVIEW, Icons.COIN, Icons.FINANCES, Icons.BANKNOTE, Icons.SAFE, Icons.FLAME,
            Icons.EXCHANGE, Icons.REPORTS, Icons.POLICY};

    /** Each page's (i), in PAGES' order. */
    static final String[] PAGE_INFO = {
        "The central bank at a glance: the money it has made, what it holds, its month and the rate it sets. The eight "
                + "pages behind it take each figure apart.",
        "Its profit or loss for the month, line by line - the interest and gains it earned, the interest it paid on "
                + "reserves, and what the month's strike did with the profit - this month against last.",
        "What it owns, what it owes - M0, every dollar it has made and not taken back - and its equity, as the month "
                + "closed against a year ago.",
        "Every dollar it made and every dollar it took back in the month, from M0 as the month opened to M0 as it closed.",
        "The city's paper it holds, by when each piece matures, at face and at market; what it bought and sold, and what its "
                + "holdings take off the long rates.",
        "Prices against the target the city holds inflation to, how far people trust the central bank, the real rate, and what "
                + "the rule would set the policy rate to.",
        "M0 in its parts against M2 - what the public holds - the vault and the gold when it has any, and the money it has "
                + "made and destroyed since founding.",
        "Its money, its reserves, what it advanced the treasury and remitted to it, the policy rate, what people expected and "
                + "how far they trusted it, over the city's life.",
        "The dials it sets at once - the inflation target and how strictly it holds it, the share of the term paper it holds, "
                + "the ceiling on what it advances the treasury - and the policy rate, read, with the door to where it is set.",
    };

    /** Which page is open; the rail's bank icon resets it to the Overview. */
    String page = OVERVIEW;

    /** What the player has opened on a statement - a line's note - by key, so a redraw on the clock leaves it open (BankScreen.openLines'). */
    final Set<String> openLines = new HashSet<>();

    /** A page's (i) by its name. */
    static String pageInfo(String page) {
        for (int i = 0; i < PAGES.length; i++) if (PAGES[i].equals(page)) return PAGE_INFO[i];
        return PAGE_INFO[0];
    }

    /** The head: "Bank › Central bank", at its right the door to the policy rate. */
    HBox head() {
        Label policy = door("The policy rate", Palette.ACCENT, () -> bank.openPolicy("Money", "The policy rate"));
        return pageHeadTo("Bank", Palette.MONEY, bank::openChoice, BankScreen.CENTRAL,
                OVERVIEW.equals(page) ? null : () -> bank.openCentral(OVERVIEW), pageInfo(page), policy);
    }

    /** The nine chips, the page open lit; the strip has no Summary | Statement switch, since each page is what it is. */
    HBox strip() {
        return StatementView.stripWithSwitch(chipStrip(PAGES, ICONS, page, Palette.SIZE_LABEL, bank::openCentral), null, null);
    }

    /** The page open, drawn into the scroller's page. */
    void page(VBox p) {
        switch (page) {
            case INCOME             -> incomePage(p);
            case SHEET              -> sheetPage(p);
            case MADE_AND_DESTROYED -> madePage(p);
            case HOLDINGS           -> holdingsPage(p);
            case INFLATION          -> inflationPage(p);
            case MONEY              -> moneyPage(p);
            case HISTORY            -> historyPage(p);
            case POLICY             -> policyPage(p);
            default                 -> overviewPage(p);
        }
    }

    /* =====================================================================
       THE OVERVIEW (CBK1, 2026-11-26)

       What has it made, what does it hold, how was its month, and what does
       it set? A sentence in the model's own figures, four cards of them, and
       a door to each of the other eight pages.
       ===================================================================== */

    /** The Overview: its month in a sentence, four cards of figures, the doors. */
    private void overviewPage(VBox p) {
        CentralBank cb = ui.game.getCentralBank();
        DebtManager ledger = ui.game.getDebtManager();
        PriceIndex prices = ui.game.getPriceIndex();
        double profit = cb.getStruckProfit();
        // Kept: each section is a scope of its own, so what one makes shifts none of the next one's nodes.
        p.getChildren().add(Kept.scope("banner", () -> statusBanner(
                profit < 0 ? "A LOSS" : profit > 0 ? "A PROFIT" : "NO PROFIT", profit < 0 ? Palette.BAD : Palette.MONEY,
                lead(cb), Icons.BANKNOTE)));
        VBox[] cards = {
            Kept.scope("made", () -> madeCard(cb)),
            Kept.scope("holds", () -> holdsCard(cb)),
            Kept.scope("month", () -> monthCard(cb)),
            Kept.scope("rate", () -> rateCard(cb, ledger, prices))};
        GridPane grid = equalColumns(2, TILE_GAP);
        for (int i = 0; i < cards.length; i++) {
            GridPane.setFillHeight(cards[i], true);
            cards[i].setMaxHeight(Double.MAX_VALUE);
            grid.add(cards[i], i % 2, i / 2);
        }
        p.getChildren().add(grid);
        p.getChildren().add(Kept.scope("pages", this::pagesBehind));
    }

    /** The lead: what it has made and holds, what it earned or lost this month and what it owes the treasury. */
    private static String lead(CentralBank cb) {
        double profit = cb.getStruckProfit();
        return "It has made " + m(cb.m0()) + " and not taken it back, and holds " + m(cb.totalAssets()) + ". This month it "
                + (profit < 0 ? "lost " + m(-profit) : profit > 0 ? "earned " + m(profit) : "earned nothing")
                + "; it owes the treasury " + m(cb.getRemittanceDue()) + ".";
    }

    /** THE MONEY IT HAS MADE: M0 and its two parts; a click opens the money made and destroyed. */
    private VBox madeCard(CentralBank cb) {
        return doorCard(() -> bank.openCentral(MADE_AND_DESTROYED),
                cardHead(Icons.BANKNOTE, Palette.MONEY, "THE MONEY IT HAS MADE",
                        "M0 is every dollar the central bank has made and not taken back: the reserves the commercial bank "
                                + "keeps with it, and the currency in circulation.", null),
                cardLine("M0", m(cb.m0()), null),
                cardLine("Reserves: the commercial bank's account here", m(cb.getReserves()), null),
                cardLine("Currency in circulation", m(cb.getCurrency()), null));
    }

    /** WHAT IT HOLDS: its advances against the treasury's ceiling, its paper and its vault; a click opens the balance sheet. */
    private VBox holdsCard(CentralBank cb) {
        return doorCard(() -> bank.openCentral(SHEET),
                cardHead(Icons.SAFE, Palette.MONEY, "WHAT IT HOLDS",
                        "What it has lent the commercial bank at the window and the treasury past its own means, the city's "
                                + "paper it has bought, and the foreign reserves in its vault.", null),
                cardLine("Advances to the commercial bank", m(cb.getAdvancesToBank()), null),
                cardLine("Advances to the treasury, against its ceiling", m(cb.getAdvancesToTreasury()) + " of " + m(cb.ceiling()), null),
                cardLine("The city's paper it holds", m(cb.getPaperHeld()), null),
                cardLine("The vault: the foreign reserves", m(cb.getVault()), null));
    }

    /** ITS MONTH: its profit and what the strike did with it, the loss it carries and what M0 moved by; a click opens the income statement. */
    private VBox monthCard(CentralBank cb) {
        return doorCard(() -> bank.openCentral(INCOME),
                cardHead(Icons.COIN, Palette.MONEY, "ITS MONTH",
                        "What the month's strike made of its profit or loss: a profit goes to the treasury once a loss "
                                + "carried is made good; a loss is carried to be made good from profit.", null),
                cardLine("Its profit this month", m(cb.getStruckProfit()), cb.getStruckProfit() < 0 ? Palette.BAD : null),
                cardLine("...owed to the treasury at the next press", m(cb.getToRemit()), null),
                cardLine("...or a loss added to carry", m(cb.getLossAdded()), cb.getLossAdded() > 0 ? Palette.WARN : null),
                cardLine("A loss still carried", m(cb.getLossCarried()), cb.getLossCarried() > 0 ? Palette.WARN : null),
                cardLine("What it owes the treasury now", m(cb.getRemittanceDue()), null),
                cardLine("M0 moved by, this month", mv(cb.getM0Moved()), null));
    }

    /** THE RATE AND PRICES: the policy rate, inflation against its target, its share of the city's term paper; a click opens Inflation & credibility. */
    private VBox rateCard(CentralBank cb, DebtManager ledger, PriceIndex prices) {
        boolean known = prices.hasRate();
        double ceiling = CentralBankStatements.ceilingUsed(cb);
        return doorCard(() -> bank.openCentral(INFLATION),
                cardHead(Icons.FLAME, Palette.MONEY, "THE RATE AND PRICES",
                        "The rate it sets, and what it leans against: inflation over the last twelve months against the "
                                + "target the city holds it to. Prices need a year of them before inflation reads.", null),
                cardLine("The policy rate", ratePerYear(ledger.getPolicyRate()), null),
                cardLine("Inflation, over the last twelve months", known ? ratePct(prices.inflation()) : "—", null),
                cardLine("The target it aims for", ratePct(ledger.getInflationTarget()), null),
                cardLine("Over the target, in points", known ? pts(CentralBankStatements.inflationGap(prices.inflation(), ledger)) : "—", null),
                cardLine("Its share of the city's term paper", share1(ledger.centralBankShareOfTerm()), null),
                cardLine("Its advances to the treasury, of the ceiling", Double.isNaN(ceiling) ? "—" : share1(ceiling), null));
    }

    /** THE PAGES BEHIND IT: a door pill to each of the other eight. */
    private VBox pagesBehind() {
        FlowPane doors = Kept.flow(Palette.GAP, Palette.GAP);
        for (int i = 1; i < PAGES.length; i++) {
            final String name = PAGES[i];
            final String icon = ICONS[i];
            // Kept: a pill is a scope keyed by the page it opens.
            doors.getChildren().add(Kept.scope(name, () -> doorPill(name, icon, Palette.MONEY, () -> bank.openCentral(name))));
        }
        return Kept.column(Palette.GAP, sectionHead("THE PAGES BEHIND IT", null), doors);
    }

    /* =====================================================================
       ITS STATEMENTS (CBK1, 2026-11-26)

       The income statement, the balance sheet and the money made and
       destroyed, each a CentralBankStatements table drawn as the commercial
       bank's are (BankScreen.profitStatementView()): the card of the
       statement with its toolbar, the ratios beside it, and a caption where
       a column is not yet known. Each bottom line is the bank's own figure;
       CentralBankStatementCheck holds the sums.
       ===================================================================== */

    /** The income statement, formal, this month against last, its ratios beside it. */
    private void incomePage(VBox p) {
        CentralBank cb = ui.game.getCentralBank();
        SectorStatements.Table t = CentralBankStatements.income(cb, ui.game.getForeignAccounts().getVaultInterest());
        p.getChildren().add(statement(t, "cb:income", "this month", "last month", "of income",
                t.now(CentralBankStatements.I_INCOME), t.row(CentralBankStatements.I_INCOME).then(),
                "For the month of " + CityCalendar.format(ui.game.getMonth()), CentralBankScreen::incomeSaid,
                incomeRatios(cb, t)));
        if (!cb.knowsLastMonth()) {
            p.getChildren().add(Kept.scope("last month caption", () -> SectorScreen.caption("Last month is kept from the "
                    + "second month the central bank has filed, so it reads — until then.", Palette.TEXT_MUTED)));
        }
    }

    /** The balance sheet, formal, this month against a year ago, its ratios beside it. */
    private void sheetPage(VBox p) {
        CentralBank cb = ui.game.getCentralBank();
        DebtManager ledger = ui.game.getDebtManager();
        SectorStatements.Table t = CentralBankStatements.sheet(cb, ledger.centralBankMarketValue(), ledger.centralBankMarkGain());
        p.getChildren().add(statement(t, "cb:sheet", "this month", "a year ago", "of assets",
                t.now(CentralBankStatements.S_ASSETS), t.row(CentralBankStatements.S_ASSETS).then(),
                "As " + CityCalendar.format(ui.game.getMonth()) + " closed", CentralBankScreen::sheetSaid,
                sheetRatios(cb, ledger)));
        if (!cb.knowsYearAgo()) {
            p.getChildren().add(Kept.scope("year caption", () -> SectorScreen.caption("A year ago is kept from the first month "
                    + "played in this build, so it reads — until the city has lived a year of them.", Palette.TEXT_MUTED)));
        }
    }

    /** The money made and destroyed, this month against last, with what M0 did under it. */
    private void madePage(VBox p) {
        CentralBank cb = ui.game.getCentralBank();
        SectorStatements.Table t = CentralBankStatements.money(cb);
        p.getChildren().add(statement(t, "cb:money", "this month", "last month", "of money made",
                t.now(CentralBankStatements.M_MADE), t.row(CentralBankStatements.M_MADE).then(),
                "For the month of " + CityCalendar.format(ui.game.getMonth()), CentralBankScreen::moneySaid,
                moneyRatios(cb)));
        p.getChildren().add(Kept.scope("opened caption", () -> SectorScreen.caption("M0 opened the month at "
                + m(cb.getM0AtStart()) + " and closed at " + m(cb.m0()) + ": what was made less what was destroyed.",
                Palette.TEXT_MUTED)));
        if (!cb.knowsLastMonth()) {
            p.getChildren().add(Kept.scope("last month caption", () -> SectorScreen.caption("Last month is kept from the "
                    + "second month the central bank has filed, so it reads — until then.", Palette.TEXT_MUTED)));
        }
    }

    /** A statement as BankScreen draws its own: the title block, the toolbar and the table in a card, the ratios beside it. */
    private Node statement(SectorStatements.Table t, String key, String nowWord, String thenWord, String shareWord,
                           double base, double baseThen, String period, java.util.function.Function<String, String> info,
                           Node ratios) {
        SectorScreen sectors = ui.sectorScreen;
        boolean millions = SectorStatements.inMillions(t);
        StatementView.Columns c = sectors.columns(shareWord);
        VBox table = StatementView.table(t, key, nowWord, thenWord, c, base, baseThen, openLines, null, info, ui::redraw);
        VBox card = StatementView.card(StatementView.titleBlock("The central bank", t.title(), period, millions),
                StatementView.toolbar(c, () -> { sectors.showChange = !sectors.showChange; ui.redraw(); },
                        () -> { sectors.showShare = !sectors.showShare; ui.redraw(); }, null, null), table);
        return StatementView.beside(card, ratios);
    }

    /** A part over its whole as a share, "—" with no whole. */
    private static String of(double part, double whole) {
        return whole > 0 ? share1(part / whole) : "—";
    }

    /**
     * The income statement's ratios: its return on what it holds, each source's share of what it earned, what its
     * reserves cost, how much of the profit goes to the treasury, and the loss it still carries.
     */
    private VBox incomeRatios(CentralBank cb, SectorStatements.Table t) {
        double earned = t.now(CentralBankStatements.I_INCOME);
        double assets = cb.totalAssets();
        double profit = cb.getStruckProfit();
        List<Node> rows = new ArrayList<>();
        rows.add(SectorScreen.ratioRow("Return on its assets", assets > 0 ? ratePerYear(profit * 12 / assets) : "—",
                profit < 0 ? Palette.BAD : Palette.TEXT_LABEL,
                "This month's profit over everything it holds, a year at this month's."));
        rows.add(SectorScreen.ratioRow("The window, of what it earned", of(cb.thisMonth(CentralBank.Line.WINDOW_INTEREST), earned),
                Palette.TEXT_LABEL, "The interest the commercial bank paid on what it borrowed here, over what it earned."));
        rows.add(SectorScreen.ratioRow("Its advances, of what it earned", of(cb.thisMonth(CentralBank.Line.ADVANCES_INTEREST), earned),
                Palette.TEXT_LABEL, "The interest the treasury paid on what the central bank advanced it, over what it earned."));
        rows.add(SectorScreen.ratioRow("Coupons, of what it earned", of(cb.thisMonth(CentralBank.Line.PAPER_COUPONS), earned),
                Palette.TEXT_LABEL, "The coupons on the city's paper it holds, over what it earned."));
        rows.add(SectorScreen.ratioRow("Gains on its paper, of what it earned", of(cb.thisMonth(CentralBank.Line.PAPER_GAINS), earned),
                Palette.TEXT_LABEL, "A gain or loss on the paper against its face, over what it earned."));
        rows.add(SectorScreen.ratioRow("Reserves' interest, of what it earned", of(cb.thisMonth(CentralBank.Line.RESERVES_INTEREST), earned),
                Palette.TEXT_LABEL, "What it paid the commercial bank on the reserves it keeps here, over what it earned."));
        rows.add(SectorScreen.ratioRow("Owed the treasury, of its profit", profit > 0 ? share1(cb.getToRemit() / profit) : "—",
                Palette.TEXT_LABEL, "What the strike left to remit, over the month's profit: the rest made good a loss carried."));
        rows.add(SectorScreen.ratioRow("A loss still carried", m(cb.getLossCarried()),
                cb.getLossCarried() > 0 ? Palette.WARN : Palette.TEXT_LABEL,
                "Made good from future profit before anything more is remitted."));
        return SectorScreen.ratioColumn("READ AS RATIOS", rows.toArray(new Node[0]));
    }

    /** The balance sheet's ratios: its equity and its paper over its assets, its draw on the treasury's ceiling, the parts of M0, its vault, and the market's mark on its paper. */
    private VBox sheetRatios(CentralBank cb, DebtManager ledger) {
        double assets = cb.totalAssets();
        double ceiling = CentralBankStatements.ceilingUsed(cb);
        double mark = ledger.centralBankMarkGain();
        List<Node> rows = new ArrayList<>();
        rows.add(SectorScreen.ratioRow("Equity, of its assets", of(cb.equity(), assets), cb.equity() < 0 ? Palette.BAD : Palette.TEXT_LABEL,
                "What it owns past what it owes, over everything it owns."));
        rows.add(SectorScreen.ratioRow("Its paper, of its assets", of(cb.getPaperHeld(), assets), Palette.TEXT_LABEL,
                "The city's paper it holds, at face, over everything it owns."));
        rows.add(SectorScreen.ratioRow("The vault, of its assets", of(cb.getVault(), assets), Palette.TEXT_LABEL, null));
        rows.add(SectorScreen.ratioRow("Its advances, of the ceiling", Double.isNaN(ceiling) ? "—" : share1(ceiling), Palette.TEXT_LABEL,
                "What it has advanced the treasury over the most the treasury may owe it."));
        rows.add(SectorScreen.ratioRow("M0, of its assets", of(cb.m0(), assets), Palette.TEXT_LABEL, null));
        rows.add(SectorScreen.ratioRow("Reserves, of M0", of(cb.getReserves(), cb.m0()), Palette.TEXT_LABEL,
                "The commercial bank's account here, over every dollar made and not taken back."));
        rows.add(SectorScreen.ratioRow("Currency, of M0", of(cb.getCurrency(), cb.m0()), Palette.TEXT_LABEL, null));
        rows.add(SectorScreen.ratioRow("Its paper, at market against face", mv(mark), mark < 0 ? Palette.WARN : Palette.TEXT_LABEL,
                "What the market would pay for the paper it holds, less its face: unbooked, so it is not in the equity."));
        return SectorScreen.ratioColumn("READ AS RATIOS", rows.toArray(new Node[0]));
    }

    /** The money statement's ratios: what was made and what was destroyed against the M0 the month opened with, and what M0 moved by. */
    private VBox moneyRatios(CentralBank cb) {
        List<Node> rows = new ArrayList<>();
        rows.add(SectorScreen.ratioRow("Made, of the M0 it opened with", of(cb.getIssued(), cb.getM0AtStart()), Palette.TEXT_LABEL,
                "Every flow that made money this month, over M0 as the month opened."));
        rows.add(SectorScreen.ratioRow("Destroyed, of the M0 it opened with", of(cb.getRetired(), cb.getM0AtStart()), Palette.TEXT_LABEL,
                "Every flow that took money back this month, over M0 as the month opened."));
        rows.add(SectorScreen.ratioRow("M0 moved by", mv(cb.getM0Moved()), Palette.TEXT_LABEL,
                "What was made less what was destroyed."));
        return SectorScreen.ratioColumn("READ AS RATIOS", rows.toArray(new Node[0]));
    }

    /** An (i) for a row of the income statement, by its id; null for a row that needs none. */
    private static String incomeSaid(String id) {
        return switch (id) {
            case CentralBankStatements.I_WINDOW -> "What the commercial bank paid on what it borrowed here overnight. "
                    + "Money paid to the central bank is money taken out of M0.";
            case CentralBankStatements.I_ADVANCES -> "What the treasury paid on what the central bank advanced it.";
            case CentralBankStatements.I_COUPONS -> "The coupons on the city's paper it holds.";
            case CentralBankStatements.I_GAINS -> "A gain or loss on the paper it holds against its face, as the month "
                    + "struck it; a loss is below nothing.";
            case CentralBankStatements.I_RESERVES -> "What it pays the commercial bank on the reserves it keeps here. "
                    + "It pays in new money, so it adds to M0.";
            case CentralBankStatements.I_TO_REMIT -> "A central bank keeps no profit: what is left once a loss carried is "
                    + "made good is owed to the treasury and paid at the next press.";
            case CentralBankStatements.I_KEPT -> "Nothing in every month: the profit goes to the treasury or makes good "
                    + "a loss, and a loss is carried, never kept.";
            case CentralBankStatements.I_VAULT_INTEREST -> "Interest on the foreign reserves in the vault stays in the "
                    + "vault; it is not in the profit that is remitted.";
            default -> null;
        };
    }

    /** An (i) for a row of the balance sheet, by its id; null for a row that needs none. */
    private static String sheetSaid(String id) {
        return switch (id) {
            case CentralBankStatements.S_TREASURY -> "What it has printed for the treasury and not been repaid.";
            case CentralBankStatements.S_PAPER -> "The city's paper it holds, at face.";
            case CentralBankStatements.S_VAULT -> "The foreign reserves it holds, at the month's rate.";
            case CentralBankStatements.S_RESERVES -> "The commercial bank's account here: money it owes the commercial bank.";
            case CentralBankStatements.S_CURRENCY -> "The currency in circulation: money it owes whoever holds it.";
            case CentralBankStatements.S_LIABILITIES -> "M0: every dollar it has made and not taken back.";
            case CentralBankStatements.S_AT_MARKET -> "What the market would pay today for the paper it holds.";
            default -> null;
        };
    }

    /** An (i) for a row of the money statement, by its id; null for a row that needs none. */
    private static String moneySaid(String id) {
        return switch (id) {
            case CentralBankStatements.M_MOVED -> "What was made less what was destroyed.";
            case CentralBankStatements.M_REST -> "Only a reform in the month - a new unit of money - leaves anything "
                    + "between M0 as it opened and as it closed.";
            default -> null;
        };
    }

    /* =====================================================================
       THE FIVE PAGES' PIECES (CBK1, 2026-11-26)

       Each of the pages below opens on a lead sentence with its (i) and is a
       few cards of the central bank's own getters, two across, each card (and
       each card that comes and goes) a Kept.scope of its own so a month's
       redraw hands every node back.
       ===================================================================== */

    /** How wide a page's lead sentence runs before it wraps: Finances' Money page's. */
    private static final double LEAD_WIDE = 900;

    /** A page's lead: a sentence in the model's figures, and behind its (i) what the page holds. */
    private static HBox leadLine(String sentence, String info) {
        return infoLine(sentence, info, true, Palette.SIZE_BODY + 1, Palette.TEXT_LABEL, LEAD_WIDE);
    }

    /** Cards two across, each as tall as its row, as the Overview's four are. */
    private static GridPane twoAcross(List<VBox> cards) {
        GridPane grid = equalColumns(2, TILE_GAP);
        for (int i = 0; i < cards.size(); i++) {
            VBox card = cards.get(i);
            GridPane.setFillHeight(card, true);
            card.setMaxHeight(Double.MAX_VALUE);
            grid.add(card, i % 2, i / 2);
        }
        return grid;
    }

    /** Months left in words: "7.5 months", "4.2 years"; "—" with none. */
    private static String monthsLeft(double months) {
        if (!(months > 0)) return "—";
        return months >= 24 ? String.format("%.1f years", months / 12) : String.format("%.1f months", months);
    }

    /* =====================================================================
       HOLDINGS (CBK1, 2026-11-26)

       What paper does the central bank hold, when does each piece fall due,
       and what would the market pay for it today? A ladder of the paper by
       the months it has left, at face and at market, with the gain or loss
       between; its book at market and the months left on average; what it
       bought and sold this month, and since founding; and what its holdings
       take off the long rates. Every figure a getter on CentralBank or
       DebtManager, the ladder CentralBankStatements.ladder().
       ===================================================================== */

    /** The Holdings page's (i). */
    private static final String HOLDINGS_INFO = "The city's own paper the central bank holds - bought with money it made, which is "
            + "how it bends the long rates down - by how long each piece has left to run: at face, which is what the treasury "
            + "owes it, and at market, which is what the market would pay for it today. It carries the paper at face, and the "
            + "mark is not booked. Under the ladder: its book, what it bought and sold this month and since founding, and what "
            + "its holdings take off the curve.";

    /** The terms whose premium and compression the Holdings page lists, in months: a year, five, ten and thirty. */
    private static final int[] CURVE_TERMS = {12, 60, 120, 360};

    /** The Holdings page: its lead, the ladder, then four cards. */
    private void holdingsPage(VBox p) {
        CentralBank cb = ui.game.getCentralBank();
        DebtManager ledger = ui.game.getDebtManager();
        p.getChildren().add(Kept.scope("lead", () -> leadLine(holdingsLead(cb, ledger), HOLDINGS_INFO)));
        p.getChildren().add(Kept.scope("ladder", () -> ladderCard(ledger)));
        List<VBox> cards = List.of(
                Kept.scope("book", () -> bookCard(cb, ledger)),
                Kept.scope("month", () -> boughtThisMonthCard(cb)),
                Kept.scope("founding", () -> boughtSinceFoundingCard(cb)),
                Kept.scope("curve", () -> curveCard(ledger)));
        p.getChildren().add(Kept.scope("cards", () -> twoAcross(cards)));
    }

    /** The lead: what it holds at face, its share of the term paper and of all the paper, and what its dial aims at. */
    private static String holdingsLead(CentralBank cb, DebtManager ledger) {
        String aims = "its dial aims at " + share1(cb.getTargetShare()) + " of the term paper";
        if (nothing(cb.getPaperHeld())) return "It holds none of the city's paper; " + aims + ".";
        return "It holds " + m(cb.getPaperHeld()) + " of the city's paper at face: " + share1(ledger.centralBankShareOfTerm())
                + " of the term paper and " + share1(ledger.centralBankShareOfPaper()) + " of all of it; " + aims + ".";
    }

    /** ITS PAPER, BY WHEN IT MATURES: the ladder as a table, or a line saying it holds nothing. */
    private VBox ladderCard(DebtManager ledger) {
        List<CentralBankStatements.Rung> rungs = CentralBankStatements.ladder(ledger);
        boolean holds = something(rungs.get(rungs.size() - 1).face());
        Node body = holds ? Kept.scope("table", () -> ladderTable(rungs))
                : Kept.scope("nothing", () -> muted("It holds none of the city's paper, so there is nothing to put on a ladder."));
        return card(cardHead(Icons.SAFE, Palette.MONEY, "ITS PAPER, BY WHEN IT MATURES",
                "Every piece of the city's paper it holds, grouped by the months it has left. At face is what the treasury owes it; "
                        + "at market is what the market would pay for its share today, on the curve as it stands, so a piece "
                        + "that pays less than today's rates is worth less than face. The gain or loss is the one less the "
                        + "other, below nothing in red. Nothing here is booked: the central bank carries its paper at face.",
                null), body);
    }

    /** The ladder: a row to a rung, the last "Every piece", in the table pieces the commercial bank's lending tables use. */
    private static GridPane ladderTable(List<CentralBankStatements.Rung> rungs) {
        GridPane t = grid(new double[] {150, 100, 100, 110}, rightAfterFirst(4));
        gridHead(t, "it matures in", "at face", "at market", "gain or loss");
        int row = 1;
        for (CentralBankStatements.Rung r : rungs) {
            String tone = row == rungs.size() ? Palette.TEXT_HEAD : Palette.TEXT_BODY;
            t.add(gridCell(r.label(), tone, Palette.SIZE_CAPTION, false), 0, row);
            t.add(gridCell(something(r.face()) ? m(r.face()) : "—", something(r.face()) ? tone : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 1, row);
            t.add(gridCell(something(r.market()) ? m(r.market()) : "—", something(r.market()) ? tone : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 2, row);
            t.add(gridCell(something(r.gain()) ? mv(r.gain()) : "—",
                    !something(r.gain()) ? Palette.TEXT_SPENT : r.gain() < 0 ? Palette.BAD : tone, Palette.SIZE_CAPTION, true), 3, row);
            row++;
        }
        return t;
    }

    /** ITS BOOK AT MARKET: its paper at face and at market, the mark between, the months left on average. */
    private VBox bookCard(CentralBank cb, DebtManager ledger) {
        double mark = ledger.centralBankMarkGain();
        return card(cardHead(Icons.COIN, Palette.MONEY, "ITS BOOK AT MARKET",
                        "What the market would pay today for every piece it holds, against what the treasury owes it. The months "
                                + "left are the average of its pieces', weighted by their face.", null),
                cardLine("At face, as it carries it", m(cb.getPaperHeld()), null),
                cardLine("At market, what the market would pay", m(ledger.centralBankMarketValue()), null),
                cardLine("The mark: market less face", mv(mark), something(mark) && mark < 0 ? Palette.BAD : null),
                cardLine("Months left, on average", monthsLeft(ledger.centralBankMonthsLeft()), null),
                muted("It carries the paper at face. The mark is not booked, so it is not in the equity on the balance sheet."));
    }

    /** THIS MONTH: what it paid for paper and from whom, what it sold, what the paper paid it, and the gains. */
    private VBox boughtThisMonthCard(CentralBank cb) {
        return card(cardHead(Icons.BANKNOTE, Palette.MONEY, "BOUGHT AND SOLD THIS MONTH",
                        "What it paid, in money it made, for the city's paper this month, and whom it paid: the commercial bank, "
                                + "the families, the world, and the treasury at issue, when it rolls its own maturing paper at par. "
                                + "What it sold, destroying the money it was paid; the coupons and the principal its paper paid it; "
                                + "and a gain or loss against face.", null),
                cardLine("Bought: what it paid", m(cb.getBoughtPaper()), null),
                cardLine("...the commercial bank", m(cb.getBoughtFromBank()), Palette.TEXT_MUTED),
                cardLine("...the families", m(cb.getBoughtFromHouseholds()), Palette.TEXT_MUTED),
                cardLine("...abroad", m(cb.getBoughtFromWorld()), Palette.TEXT_MUTED),
                cardLine("...the treasury, at issue", m(cb.getBoughtAtIssue()), Palette.TEXT_MUTED),
                cardLine("...for a par of", m(cb.getParAtIssue()), Palette.TEXT_MUTED),
                cardLine("Sold: what it was paid", m(cb.getSoldPaper()), null),
                cardLine("Coupons paid it", m(cb.getPaperCoupons()), null),
                cardLine("Principal repaid", m(cb.getPaperRedeemed()), null),
                cardLine("Gains on the paper", mv(cb.getPaperGains()), cb.getPaperGains() < 0 && something(cb.getPaperGains()) ? Palette.BAD : null));
    }

    /** SINCE FOUNDING: what it paid for paper and was paid for it, and of the buying from the families and the treasury at issue. */
    private VBox boughtSinceFoundingCard(CentralBank cb) {
        return card(cardHead(Icons.REPORTS, Palette.MONEY, "BOUGHT AND SOLD SINCE FOUNDING",
                        "The same lines as this month's, added up since the central bank was founded.", null),
                cardLine("Bought: what it paid", m(cb.getBoughtPaperLifetime()), null),
                cardLine("...the families", m(cb.getBoughtFromHouseholdsLifetime()), Palette.TEXT_MUTED),
                cardLine("...the treasury, at issue", m(cb.getBoughtAtIssueLifetime()), Palette.TEXT_MUTED),
                cardLine("...for a par of", m(cb.getParAtIssueLifetime()), Palette.TEXT_MUTED),
                cardLine("Sold: what it was paid", m(cb.getSoldPaperLifetime()), null),
                cardLine("It holds now, at face", m(cb.getPaperHeld()), null));
    }

    /** WHAT IT TAKES OFF THE CURVE: for a year, five, ten and thirty, the term premium and what its holdings take off it. */
    private VBox curveCard(DebtManager ledger) {
        GridPane t = grid(new double[] {80, 110, 150}, rightAfterFirst(3));
        gridHead(t, "a loan of", "the premium", "its holdings take off");
        int row = 1;
        for (int months : CURVE_TERMS) {
            t.add(gridCell(months / 12 + (months == 12 ? " year" : " years"), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, row);
            t.add(gridCell(points(DebtManager.termPremium(months)), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 1, row);
            t.add(gridCell(points(-ledger.compression(months)), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 2, row);
            row++;
        }
        return card(cardHead(Icons.FLAME, Palette.MONEY, "WHAT IT TAKES OFF THE CURVE",
                String.format("The premium a lender adds for tying money up that long, and what the central bank's holdings take "
                                + "off it: the premium, times its share of the city's term paper (%s) over the %.0f%% that takes "
                                + "all of it off. A year or less has no premium to take off.",
                        share1(ledger.centralBankShareOfTerm()), CentralBank.FULL_COMPRESSION_SHARE * 100), null),
                t);
    }

    /* =====================================================================
       INFLATION & CREDIBILITY (CBK1, 2026-11-26)

       Is the central bank holding prices to its target, and does the city
       believe it will? Inflation against the target and the inflation the
       policy rule read; how far people trust it and what they expect; the
       real rate; and what the rule would set the policy rate to against the
       dial. PolicyScreen's own words where they fit (anchorWords(), the
       rule's reason, the (i)s), so Policy and this page say one thing.
       ===================================================================== */

    /** The Inflation & credibility page's (i). */
    private static final String INFLATION_INFO = "Prices against the target the city holds inflation to: this year's inflation, the "
            + "one the policy rule read at the top of the month, and what people expect. How far people trust the central bank to "
            + "hold the target. The real rate - the dial less the inflation people expect - and what the rule would set the "
            + "dial to against the dial itself. Prices need a year of readings before inflation reads.";

    /** The Inflation & credibility page: its lead, then four cards. */
    private void inflationPage(VBox p) {
        DebtManager ledger = ui.game.getDebtManager();
        PriceIndex px = ui.game.getPriceIndex();
        p.getChildren().add(Kept.scope("lead", () -> leadLine(inflationLead(ledger, px), INFLATION_INFO)));
        List<VBox> cards = List.of(
                Kept.scope("inflation", () -> inflationCard(ledger, px)),
                Kept.scope("credibility", () -> credibilityCard()),
                Kept.scope("real", () -> realRateCard(ledger)),
                Kept.scope("rule", () -> ruleSaysCard(ledger, px)));
        p.getChildren().add(Kept.scope("cards", () -> twoAcross(cards)));
    }

    /** The lead: inflation against its target ("—" before a year of prices), and how far people trust it. */
    private String inflationLead(DebtManager ledger, PriceIndex px) {
        String target = DebtManager.targetWords(ledger.getInflationTarget());
        return "Inflation is " + (px.hasRate() ? ratePct(px.inflation()) : "—") + " a year against its " + target + " target; people trust the central bank "
                + trust(ui.game.getCredibility()) + " and expect " + ratePct(ui.game.getExpectedInflation()) + ".";
    }

    /** INFLATION: this year's, the one the rule read, the target, the gap in points, and what people expect. */
    private VBox inflationCard(DebtManager ledger, PriceIndex px) {
        boolean known = px.hasRate();
        double struck = ledger.getStruckInflation();
        return card(cardHead(Icons.FLAME, Palette.MONEY, "INFLATION",
                        "Inflation over the last twelve months, which needs a year of prices before it reads, against the target "
                                + "the central bank holds it to. The policy rule reads the inflation struck at the top of the month and "
                                + "holds it for the month. What people expect is a blend of the target, weighted by how far they trust "
                                + "the bank, and inflation lately.", null),
                cardLine("This year's, over the last twelve months", known ? ratePct(px.inflation()) : "—", null),
                cardLine("Struck for the policy rule, at the month's top", Double.isNaN(struck) ? "—" : ratePct(struck), null),
                cardLine("The target it aims for", DebtManager.targetWords(ledger.getInflationTarget()), null),
                cardLine("Over the target, in points", known ? points(CentralBankStatements.inflationGap(px.inflation(), ledger)) : "—", null),
                cardLine("What people expect", ratePct(ui.game.getExpectedInflation()), null));
    }

    /** CREDIBILITY: how far people trust it, a gauge between the least and the most it can be, and PolicyScreen's words for its month. */
    private VBox credibilityCard() {
        PolicyScreen.AnchorWords w = ui.policyScreen.anchorWords();
        List<Tick> ticks = List.of(
                new Tick(Expectations.KMIN, Palette.TEXT_MUTED, 2, trust(Expectations.KMIN) + " the least", null),
                new Tick(Expectations.KMAX, Palette.TEXT_MUTED, 2, trust(Expectations.KMAX) + " the most", null));
        Node gauge = segmentBar(List.of(new Segment(w.trust(), Palette.MONEY, false, null, null,
                "How far people trust the bank: " + trust(w.trust()), null)), 1, ticks, 0, 10);
        return card(cardHead(Icons.POLICY, Palette.MONEY, "CREDIBILITY", PolicyScreen.ANCHOR_INFO, null),
                cardLine("How far people trust it", trust(ui.game.getCredibility()), null),
                gauge,
                cardLine("What people expect, a year", ratePct(ui.game.getExpectedInflation()), null),
                cardLine("Inflation smoothed over a year", ratePct(ui.game.getExpectations().getSmoothedInflation()), null),
                words(FinancesScreen.upFirst(w.move()) + ".", Palette.SIZE_LABEL + 1, w.moveTone()),
                line(FinancesScreen.upFirst(w.lean()) + "."),
                muted(w.judged()));
    }

    /** THE REAL RATE: the dial less what people expect, and the real rate against the world's. */
    private VBox realRateCard(DebtManager ledger) {
        return card(cardHead(Icons.EXCHANGE, Palette.MONEY, "THE REAL RATE", PolicyScreen.REAL_DIAL_INFO, null),
                cardLine("The policy rate, the dial", ratePerYear(ledger.getPolicyRate()), null),
                cardLine("...less what people expect", ratePct(ui.game.getExpectedInflation()), Palette.TEXT_MUTED),
                cardLine("The real rate", points(ui.game.realPolicyRate()), null),
                cardLine("The real rate against the world's", points(ui.game.realRateDifferential()), null, PolicyScreen.REAL_INFO));
    }

    /** THE RULE: what it would set the dial to, why, and the dial today; with no year of prices, that it has nothing to say. */
    private VBox ruleSaysCard(DebtManager ledger, PriceIndex px) {
        boolean known = px.hasRate();
        Label policy = door("The dials", Palette.ACCENT, () -> bank.openCentral(POLICY));
        return card(cardHead(Icons.POLICY, Palette.MONEY, "THE RULE",
                        "The rule the central bank follows sets the policy rate from inflation against the target: above it a higher "
                                + "rate, below it a lower one, as strictly as its dial says. It advises; the dial moves only when "
                                + "the rule holds it. The target and the strictness are on its Policy page.", policy),
                cardLine("What the rule would set", known ? ratePerYear(ledger.advisedPolicyRate(px.inflation())) : "—", null),
                cardLine("The dial today", ratePerYear(ledger.getPolicyRate()), null),
                cardLine("Who holds the dial", ledger.isAutopilot() ? "the rule" : "you", null),
                line(known ? ledger.adviceReason(px.inflation())
                        : "There is not yet a year of prices to measure inflation against. The rule has nothing to say until there is."));
    }

    /* =====================================================================
       MONEY (CBK1, 2026-11-26)

       How much money has the central bank made, in what parts, and how much
       of it does the public hold? M0 as reserves and currency against M2 and
       its multiple; the vault and the gold, each only once it has something;
       and what it has made, destroyed, printed for the treasury and remitted
       since founding. Every figure a getter; the multiple is
       CentralBankStatements.m2OverM0().
       ===================================================================== */

    /** The Money page's (i). */
    private static final String MONEY_INFO = "M0, every dollar the central bank has made and not taken back, in its two parts - the "
            + "reserves the commercial bank keeps with it and the currency in circulation - against M2, what the public holds. The "
            + "vault and the gold, when it has any, and the money it has made and destroyed since founding.";

    /** The Money page: its lead, then the cards, the vault's and the gold's only once they have something. */
    private void moneyPage(VBox p) {
        CentralBank cb = ui.game.getCentralBank();
        DebtManager ledger = ui.game.getDebtManager();
        p.getChildren().add(Kept.scope("lead", () -> leadLine(moneyLead(cb), MONEY_INFO)));
        List<VBox> cards = new ArrayList<>();
        cards.add(Kept.scope("parts", () -> partsCard(cb)));
        cards.add(Kept.scope("m2", () -> againstM2Card(cb)));
        if (something(cb.getVault()) || something(cb.getDollarsBoughtLifetime()) || something(cb.vaultSpent())) {
            cards.add(Kept.parkedScope("vault", () -> vaultCard(cb, ledger)));
        }
        if (something(cb.getGoldMintedLifetime()) || something(cb.getGoldRedeemedLifetime()) || something(cb.getGoldRedeemDue())) {
            cards.add(Kept.parkedScope("gold", () -> goldCard(cb)));
        }
        cards.add(Kept.scope("founding", () -> madeSinceFoundingCard(cb)));
        p.getChildren().add(Kept.scope("cards", () -> twoAcross(cards)));
    }

    /** The lead: M0 against M2, and the multiple. */
    private String moneyLead(CentralBank cb) {
        double times = CentralBankStatements.m2OverM0(ui.game.getM2(), cb);
        return "The central bank has made " + m(cb.m0()) + " and not taken it back; the public holds " + m(ui.game.getM2())
                + (Double.isNaN(times) ? "." : ", " + timesWords(times) + " as much.");
    }

    /** M2 over M0 as "3.4 times", to two places under one ("0.02 times"); "—" with no M0. */
    private static String timesWords(double times) {
        return Double.isNaN(times) ? "—" : String.format(times < 1 ? "%.2f times" : "%.1f times", times);
    }

    /** M0 IN ITS PARTS: reserves and currency as a bar and as lines, each of M0. */
    private VBox partsCard(CentralBank cb) {
        Node bar = segmentBar(List.of(
                FinancesScreen.seg(cb.getReserves(), Palette.MONEY_DARK, "reserves " + m(cb.getReserves())),
                FinancesScreen.seg(cb.getCurrency(), Palette.MONEY, "currency " + m(cb.getCurrency()))), 0, null, 0, 14);
        return card(cardHead(Icons.BANKNOTE, Palette.MONEY, "M0 IN ITS PARTS",
                        "M0 is every dollar the central bank has made and not taken back. Nobody here holds cash outside the bank, "
                                + "so it is the reserves the commercial bank keeps at the central bank, and the currency in circulation.",
                        null),
                cardLine("M0", m(cb.m0()), null),
                bar,
                FinancesScreen.keyRow(keySwatch(Palette.MONEY_DARK, "reserves"), keySwatch(Palette.MONEY, "currency")),
                cardLine("Reserves: the commercial bank's account here", m(cb.getReserves()), null),
                cardLine("...of M0", of(cb.getReserves(), cb.m0()), Palette.TEXT_MUTED),
                cardLine("Currency in circulation", m(cb.getCurrency()), null),
                cardLine("...of M0", of(cb.getCurrency(), cb.m0()), Palette.TEXT_MUTED));
    }

    /** M2 AGAINST M0: what the public holds, what the central bank made, and how many times as much. */
    private VBox againstM2Card(CentralBank cb) {
        double times = CentralBankStatements.m2OverM0(ui.game.getM2(), cb);
        return card(cardHead(Icons.FINANCES, Palette.MONEY, "M2 AGAINST M0", FinancesScreen.M2_INFO, null),
                cardLine("M2: what the public holds", m(ui.game.getM2()), null),
                cardLine("M0: what the central bank has made", m(cb.m0()), null),
                cardLine("M2 for each dollar of M0", timesWords(times), null),
                cardLine("M0 moved by, this month", mv(cb.getM0Moved()), null));
    }

    /** THE VAULT: the foreign reserves at today's rate, how much of it is money this bank made, and what defending the currency has spent. */
    private VBox vaultCard(CentralBank cb, DebtManager ledger) {
        return card(cardHead(Icons.SAFE, Palette.MONEY, "THE VAULT",
                        "The foreign reserves the central bank holds, valued at the month's exchange rate. Part of it was bought with "
                                + "money the bank made; selling it to defend the currency spends capital and takes equity down with "
                                + "the vault, and leaves M0 where it was.", null),
                cardLine("The vault, at today's rate", m(cb.getVault()), null),
                cardLine("...of it in money this bank made", m(cb.vaultInNewMoney()), Palette.TEXT_MUTED),
                cardLine("The exchange rate", ui.financesScreen.perUsd(ledger.getExchangeRate()), null),
                cardLine("Spent defending the currency, this month", m(cb.getDefendedThisMonth()), null),
                cardLine("...since founding", m(cb.vaultSpent()), Palette.TEXT_MUTED,
                        "The local price of every dollar the defence has sold: the reason equity is lower than the assets it was "
                                + "bought with, since the vault is read at today's rate and what was sold has left it."),
                cardLine("Dollars bought, this month: what it paid", m(cb.getDollarsBought()), null),
                cardLine("...since founding", m(cb.getDollarsBoughtLifetime()), Palette.TEXT_MUTED));
    }

    /** GOLD: what the mint paid out, what the gold it paid retired, and what is still to retire. */
    private VBox goldCard(CentralBank cb) {
        return card(cardHead(Icons.COIN, Palette.MONEY, "GOLD",
                        "Under gold the mint pays a domestic miner in money the central bank makes: that is minted. When the currency "
                                + "is defended the gold it paid out retires money again, M0 down by it: that is redeemed. What the last "
                                + "reprice's gold fetched is still to retire, at the top of the next month.", null),
                cardLine("Minted, this month", m(cb.getGoldMinted()), null),
                cardLine("...since founding", m(cb.getGoldMintedLifetime()), Palette.TEXT_MUTED),
                cardLine("Redeemed, this month", m(cb.getGoldRedeemed()), null),
                cardLine("...since founding", m(cb.getGoldRedeemedLifetime()), Palette.TEXT_MUTED),
                cardLine("Still to be redeemed", m(cb.getGoldRedeemDue()), null));
    }

    /** SINCE FOUNDING: the money it made and destroyed, what it printed for the treasury, what it remitted, and M0 now. */
    private VBox madeSinceFoundingCard(CentralBank cb) {
        return card(cardHead(Icons.REPORTS, Palette.MONEY, "SINCE FOUNDING",
                        "Every dollar the central bank has made and every dollar it has taken back since it was founded, the money it "
                                + "printed for the treasury, and the profit it remitted to it.", null),
                cardLine("Made", m(cb.getIssuedLifetime()), null),
                cardLine("Destroyed", m(cb.getRetiredLifetime()), null),
                cardLine("Printed for the treasury", m(cb.getPrintedLifetime()), null),
                cardLine("Remitted to the treasury", m(cb.getRemittedLifetime()), null),
                cardLine("M0 now", m(cb.m0()), null));
    }

    /* =====================================================================
       HISTORY (CBK1, 2026-11-26)

       How have its figures moved over the city's life? The series City
       History already keeps for the central bank - M2 and M0, the reserves,
       what it advanced the treasury and remitted to it, the policy rate,
       what people expected and how far they trusted it - as small charts,
       two across, drawn by FinancesScreen.smallChart() as its Money page's
       are. No series is new.
       ===================================================================== */

    /** The History page's (i). */
    private static final String HISTORY_INFO = "Its figures as City History keeps them, a point a month: M2 and M0, the reserves, what "
            + "it advanced the treasury, the remittance, the policy rate, the inflation people expected and how far they trusted it. "
            + "The last ten years, or the whole history while it is younger.";

    /** The History page: its lead, then the charts - or a line saying the history is too young. */
    private void historyPage(VBox p) {
        HistorySave h = ui.game.getHistorySave();
        FinancesScreen fin = ui.financesScreen;
        p.getChildren().add(Kept.scope("lead", () -> leadLine("Its money and its rates over the city's life: " + h.months()
                + (h.months() == 1 ? " month" : " months") + " kept so far.", HISTORY_INFO)));
        if (h.months() < 2) {
            p.getChildren().add(Kept.scope("young", () -> muted("A chart needs two months of history; these draw from the second.")));
            return;
        }
        List<VBox> charts = List.of(
                Kept.scope("m2", () -> fin.smallChart("M2 AND M0", h, new String[] {"m2", "m0"}, new String[] {"M2", "M0"},
                        new String[] {Palette.ACCENT, Palette.PEOPLE})),
                Kept.scope("reserves", () -> fin.smallChart("RESERVES", h, new String[] {"reserves"},
                        new String[] {"Reserves at the central bank"}, new String[] {Palette.MONEY_DARK})),
                Kept.scope("advanced", () -> fin.smallChart("ADVANCED TO THE TREASURY", h, new String[] {"advancesToTreasury"},
                        new String[] {"Advanced to the treasury"}, new String[] {Palette.MONEY})),
                Kept.scope("remitted", () -> fin.smallChart("THE REMITTANCE", h, new String[] {"remittance"},
                        new String[] {"Remitted to the treasury"}, new String[] {Palette.PEOPLE})),
                Kept.scope("rate", () -> fin.smallChart("THE POLICY RATE", h, new String[] {"policyRate"},
                        new String[] {"Policy rate"}, new String[] {Palette.ACCENT}, "percent")),
                Kept.scope("expected", () -> fin.smallChart("EXPECTED INFLATION", h, new String[] {"expectedInflation"},
                        new String[] {"Expected inflation"}, new String[] {Palette.WARN}, "percent")),
                Kept.scope("trust", () -> fin.smallChart("CREDIBILITY", h, new String[] {"credibility"},
                        new String[] {"Trust in the bank"}, new String[] {Palette.MONEY}, "percent")));
        p.getChildren().add(Kept.scope("charts", () -> twoAcross(charts)));
    }

    /* =====================================================================
       POLICY (CBK1, 2026-11-26)

       What does it set, and how do I change it? Its dials that apply at once -
       the inflation target and how strictly it holds it, the share of the term
       paper it aims to hold, and how far it will advance the treasury - are
       PolicyScreen's own cards (ruleCard(), holdingsCard(), ceilingCard(), each
       with an `again` so a dial set here redraws this page and not Policy); the
       policy rate stages a proposal on Policy, so it is read here beside a door
       to where it is set. Each of the three cards sets at once and stages
       nothing, which is why they can be drawn away from Policy's Apply bar.
       ===================================================================== */

    /** The Policy page's (i). */
    private static final String POLICY_INFO = "The central bank's own dials, the same ones Policy sets: the inflation target and how "
            + "strictly it holds it, the share of the city's term paper it aims to hold, and how far it will advance the treasury. "
            + "It also sets the commercial bank's window, capital, reserve and credit rules. They apply at once. "
            + "The policy rate is read here and set on Policy, where a proposal is staged and applied.";

    /** The Policy page: its lead, the policy rate read beside the rule, and the holdings and the ceiling. */
    private void policyPage(VBox p) {
        CentralBank cb = ui.game.getCentralBank();
        DebtManager market = ui.game.getDebtManager();
        PriceIndex px = ui.game.getPriceIndex();
        PolicyScreen policy = ui.policyScreen;
        // A dial set here redraws the Bank tab's page, not Policy's.
        Runnable again = bank::showBankMenu;
        p.getChildren().add(Kept.scope("lead", () -> leadLine("It sets the policy rate at " + ratePerYear(market.getPolicyRate())
                + ", aims at inflation of " + DebtManager.targetWords(market.getInflationTarget()) + ", aims to hold "
                + share1(cb.getTargetShare()) + " of the term paper, and will advance the treasury up to "
                + PolicyScreen.monthsWords(cb.getAdvancesCeilingMonths()) + " of its revenue.", POLICY_INFO)));
        p.getChildren().add(Kept.scope("dials", () -> {
            VBox left = Kept.column(TILE_GAP,
                    Kept.scope("rate", () -> policyRateCard(market)),
                    Kept.scope("rule", () -> policy.ruleCard(market, px, again)));
            VBox right = Kept.column(TILE_GAP,
                    Kept.scope("holdings", () -> policy.holdingsCard(market, again)),
                    Kept.scope("ceiling", () -> policy.ceilingCard(again)));
            GridPane two = equalColumns(2, TILE_GAP);
            GridPane.setFillHeight(left, true);
            GridPane.setFillHeight(right, true);
            two.add(left, 0, 0);
            two.add(right, 1, 0);
            return two;
        }));
        // CBK2: its rules for the holdings, after the holdings' card: how fast it moves toward the share it aims at,
        // how it shrinks, its auction share and the law on direct purchases.
        p.getChildren().add(Kept.scope("holdings rules", () -> HoldingsRulesPane.build(ui.game, again)));
        p.getChildren().add(Kept.scope("bank rules", () -> BankRulesPane.build(ui.game, again)));
    }

    /** THE POLICY RATE, read: the dial today and its real rate, with the door to Policy where it is set. */
    private VBox policyRateCard(DebtManager market) {
        return card(cardHead(Icons.POLICY, Palette.MONEY, "THE POLICY RATE",
                        "The rate the central bank sets: the base for window borrowing before the chosen spread, the rate paid on "
                                + "reserves, and what the city's paper is priced up from. Changing it stages a proposal on Policy's "
                                + "Money page, which is applied there.", null),
                figure(ratePerYear(market.getPolicyRate()), Palette.SIZE_LEAD, Palette.TEXT_HEAD),
                muted("the dial today · read here, set on Policy"),
                cardLine("Its real rate", points(ui.game.realPolicyRate()), null, PolicyScreen.REAL_DIAL_INFO),
                cardLine("Who holds the dial", market.isAutopilot() ? "the rule" : "you", null),
                Kept.scope("door", () -> doorPill("Set it on Policy › Money", Icons.POLICY, Palette.MONEY,
                        () -> bank.openPolicy("Money", "The policy rate"))));
    }
}
