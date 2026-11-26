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
import static ham.citybuildersim.ui.BankScreen.cardHead;
import static ham.citybuildersim.ui.BankScreen.cardLine;
import static ham.citybuildersim.ui.BankScreen.doorCard;
import static ham.citybuildersim.ui.BankScreen.m;
import static ham.citybuildersim.ui.BankScreen.mv;
import static ham.citybuildersim.ui.BankScreen.pts;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Pieces.*;

/**
 * The central bank's pages in the Bank tab: its frame - the head "Bank › Central bank", the strip of nine chips - and the
 * pages behind them, the Overview, its income statement, its balance sheet and the money it made and destroyed in this
 * slice, the other five to follow. BankScreen owns it: it is drawn inside BankScreen's frame and kept pool, reached through
 * the tab's choice of the two banks (BankScreen.choicePage()) or the rail's bank icon.
 *
 * <p>WHY. Jerus, 2026-11-26: "when you click bank you have two options, the commercial bank, and the central bank ... give
 * both the banks proper statements, aka ability to not only view summary but also the statements same way the other
 * sectors, but make sure to really include alot of stuff into the central bank area". The central bank was a card on
 * Finances > Money and another on Government, each a figure or two read once; it had no page of its own, no last month, no
 * year ago and no statement that added up. The statements here are CentralBankStatements' tables drawn through
 * StatementView as the sectors' and the commercial bank's are (BankScreen.profitStatementView()), beside ratios whose parts
 * are the bank's own getters; the screen sums and works out nothing a statement already holds.
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
        "The city's paper and its other assets, by how long each has left, at face and at market.",
        "Prices against the target the city holds inflation to, and how far the central bank has leaned against it.",
        "The money the city holds: M0, what the public holds of it, and how it grows.",
        "Its figures over the city's life.",
        "The policy rate it sets and the rules it follows.",
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
            case HOLDINGS, INFLATION, MONEY, HISTORY, POLICY -> laterPage(p);
            default                 -> overviewPage(p);
        }
    }

    /** A page the next slice draws: a muted line, in a scope of its own. */
    private void laterPage(VBox p) {
        p.getChildren().add(Kept.scope(page, () -> SectorScreen.caption("This page is drawn in the next slice.", Palette.TEXT_MUTED)));
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
}
