package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import ham.citybuildersim.ui.Pieces.Slice;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * The Finances tab: the position, the ladder of what the city owes, debt
 * service, home and abroad, your rate taken apart, the book, buying back,
 * and borrowing - at home or in somebody else's money - and, since 0.7.0,
 * the money itself: the central bank's books, M0 and M2; since 0.7.12
 * the businesses' bond market beside the city's own; and since 0.7.13, at
 * the top of both borrow pages, the treasury's rollover of what falls due.
 *
 * Split out of UserInterface on 2026-09-18: the eleven banners from FINANCES
 * to BORROW exactly as they were, the shell's members reached through ui. The
 * shell still reads which page and area are open (financePage, financeArea)
 * for the rail and the scroll memory. THE DEBT RESULT - what a debt decision
 * did, shown after it - had sat at the tail of the build cards and came here
 * the same day, because it is this tab's.
 */
final class FinancesScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    FinancesScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       FINANCES

       WHAT IT WAS: one long page of buttons, plus three separate screens
       reached from the bottom of it - a debt portfolio in raw Courier, a
       buy-back table, and the bank. The borrowing flow was three screens deep
       and the quote only appeared on the third, so a player picked an
       instrument and a duration blind and found out the price afterwards.

       WHAT IT IS: a landing page of rows, each opening into its own strip -
       the same shape as the Sector economy, and for the same reason. Finance
       is not one screen, it is five subjects that share a vocabulary:

         THE POSITION   where the city stands, and why its money costs what it
                        costs. Five pages, because "what do I owe" and "when is
                        it due" and "can I afford it" are three questions with
                        three different answers.
         THE BOOK       every piece of paper, and what each would cost to
                        retire. Buy-back lives on the bond it is about.
         BORROW         one page. Instrument, duration and amount are chips and
                        steppers on the same screen as the quote, and the quote
                        re-strikes on every click - because the rate is a
                        function of the size of the ask, which was true and
                        invisible.
         MONEY          the central bank's books and the money supply, M0
                        and M2 with a year of each (0.7.0).
         THE BOND       the businesses' bonds, not the city's (0.7.12): every
         MARKET         issue, who holds it, and one bond's order book.

       THE BANK IS NOT HERE ANY MORE. Jerus: "i think we are going to have 11
       rails, bank is its own thing." It is a rail tab now.
       ===================================================================== */

    /** Which subject is open. Null is the landing page. */
    String financeArea = null;
    static final String FINANCE_HOME  = "Overview";
    String financePage = FINANCE_HOME;

    static final String[] POSITION_PAGES =
            {"Overview", "The ladder", "Debt service", "Home & abroad", "Your rate"};
    static final String[] BOOK_PAGES   = {"Every piece", "Buy back"};
    static final String[] BORROW_PAGES = {"At home", "Abroad"};
    /** The central bank's books and the money supply, on one page (0.7.0). */
    static final String[] MONEY_PAGES  = {"Money"};
    /** The businesses' bonds: every issue, and one bond's order book (0.7.12). */
    static final String[] BOND_PAGES   = {"Every issue", "A bond's book"};

    /**
     * One kind of paper the city can sell.
     *
     * The four numbers were scattered across six button handlers - three
     * domestic and three foreign, each passing the same min, max and rounding
     * as literals. They are one table now, so a change to what a term loan is
     * happens once.
     *
     * AND THE STEP BETWEEN TERMS (0.7.1): Jerus, "i think we should only be
     * able to issue 10y 20y 30y 40y and 50y ... serial and tbills are fine
     * tho." A term loan's chips step by ten (LongTermBond.MATURITIES); the
     * note's and the serial's by one, as they always did.
     */
    record Instrument(String key, String name, String short_,
                              int min, int max, int step, double rounding,
                              String unit, String blurb, String colour) { }

    static final Instrument[] INSTRUMENTS = {
        new Instrument("Note", "Notes", "NOTE", 3, 12, 1, 1000, "months",
                "No coupon at all. The lender pays less than the face and collects the "
                + "whole face at the end, so it costs nothing monthly and everything at "
                + "once.", Palette.LADDER[0]),
        new Instrument("Serial", "Serial bonds", "SERIAL", 1, 10, 1, 10000, "years",
                "A coupon every month on what is still out, and a slice of principal "
                + "every year. It pays itself off: both the debt and the coupon shrink "
                + "without you doing anything.", Palette.LADDER[1]),
        new Instrument("Term", "Term loans", "TERM", 10, 50, 10, 100000, "years",
                "A coupon every month on the whole face, and the entire face at the end. "
                + "The smallest monthly payment there is, and the only one with a cliff "
                + "behind it. Issued at 10, 20, 30, 40 or 50 years.", Palette.LADDER[2]),
    };

    static Instrument instrument(String key) {
        for (Instrument i : INSTRUMENTS) if (i.key().equals(key)) return i;
        return INSTRUMENTS[2];
    }

    static String instrumentColour(String type) {
        return switch (type) {
            case "NOTE"   -> Palette.LADDER[0];
            case "SERIAL" -> Palette.LADDER[1];
            default       -> Palette.LADDER[2];
        };
    }

    /* what the borrow page is currently asking for */
    String borrowType = "Term";
    int borrowTerm = 10;
    double borrowAsk = 0;
    boolean borrowHold = false;

    /* =====================================================================
       THE LANDING
       ===================================================================== */

    void showFinanceMenu() {
        ui.clearMenu("showFinanceMenu", () -> showFinanceMenu());

        if (financeArea != null) {
            drawFinanceScreen();
            return;
        }

        DebtManager ledger = ui.game.getDebtManager();

        Label title = new Label("FINANCES");
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        Label lead = new Label("What the city holds, what it owes, and what money costs it.");
        lead.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED)
                + " -fx-padding: 0 0 10 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        double principal = ledger.getAllPrincipal();
        double annual = annualGdp(ui.game.getEconomyManager().getNationalAccounts());
        double coupon = couponNow(ledger);
        int pieces = ledger.getDebt().size();

        column.getChildren().add(financeRow("The position",
                "what the city holds, what it owes, and why the rate is the rate",
                money(principal) + " owed",
                annual > 0 ? String.format("%.0f%% of a year of output", principal / annual * 100)
                           : "no output to compare",
                principal > 0 && annual > 0 && principal / annual > 1.2 ? Palette.BAD
                        : Palette.TEXT_HEAD,
                "The position", "Overview"));

        column.getChildren().add(financeRow("The book",
                pieces == 0 ? "nothing outstanding"
                        : "every piece of paper, and what each would cost to retire",
                pieces + (pieces == 1 ? " piece" : " pieces"),
                coupon > 0 ? money(coupon) + " of coupon a month" : "no coupon falls due",
                Palette.TEXT_HEAD,
                "The book", "Every piece"));

        column.getChildren().add(financeRow("Borrow",
                "notes, serial bonds and term loans — here and abroad",
                String.format("%.2f%%", ui.game.getInterestRate() * 100),
                ledger.foreignWindowOpen()
                        ? String.format("%.2f%% abroad, and the window is open",
                                ledger.foreignRate() * 100)
                        : "the window abroad is shut",
                ledger.atCeiling() ? Palette.BAD : Palette.ACCENT,
                "Borrow", "At home"));

        CentralBank central = ui.game.getCentralBank();
        column.getChildren().add(financeRow("Money",
                "the central bank's books, and how much money the city has",
                money(central.m0()) + " made",
                money(ui.game.getM2()) + " held by the public",
                central.getAdvancesToTreasury() > 0 ? Palette.WARN : Palette.TEXT_HEAD,
                "Money", "Money"));

        BondMarket market = ui.game.getBondMarket();
        double bondFace = market.totalFace(), businessDebt = businessDebt();
        column.getChildren().add(financeRow("The bond market",
                "the businesses' bonds: every issue, who holds it, and its order book",
                bondFace > 0 ? money(bondFace) + " owed" : "no bonds",
                businessDebt > 0 ? String.format("%.0f%% of what the businesses owe", bondFace / businessDebt * 100)
                        : "the businesses owe nothing",
                Palette.TEXT_HEAD, "The bond market", "Every issue"));

        /* ------------------------- the standing warnings ------------------------- */
        if (ledger.getOverdraft() > 0) {
            column.getChildren().add(alert("The city is overdrawn",
                    String.format("%s of it. At the top of next month the central bank "
                    + "advances it, at the policy rate, in money it makes. Past %.0f months "
                    + "of revenue owed, only the city's promises are still paid that way; the "
                    + "rest waits for cash, and what waits is owed as arrears. The market "
                    + "prices it as principal meanwhile - it is already in the rate above.",
                    money(ledger.getOverdraft()), ui.game.getCentralBank().getAdvancesCeilingMonths())));
        }
        if (ledger.atCeiling()) {
            column.getChildren().add(alert("The rate is pinned at the top of the curve",
                    "Both measures the market prices on — debt against output and debt "
                    + "against what the city can collect — have said everything they "
                    + "have to say. Borrowing more will not make it dearer, because it "
                    + "cannot get dearer. That is not good news."));
        }
        if (ledger.getDefaultScar() > 0) {
            column.getChildren().add(alert("A default abroad is still being charged for",
                    String.format("%.1f points of the city's rate, fading over about five "
                    + "years from when it happened.", ledger.getDefaultScar() * 100)));
        }

        ui.rootMenu.getChildren().addAll(title, lead, financeVitals(), ui.scrolled(column, 210));
    }

    /**
     * One subject on the landing page.
     *
     * The figure is the one number that says whether the row is worth opening -
     * the same test the left panel's sections use, and the reason the landing
     * page is readable at a glance instead of being a menu.
     */
    HBox financeRow(String name, String blurb, String figure, String sub,
                            String tone, String area, String page) {

        Label heading = new Label(name);
        heading.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));

        Label what = new Label(blurb);
        what.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        VBox left = new VBox(0, heading, what);
        left.setAlignment(Pos.CENTER_LEFT);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label big = new Label(figure);
        big.setStyle(Palette.figure(Palette.SIZE_LEAD, tone));

        Label small = new Label(sub);
        small.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        VBox right = new VBox(0, big, small);
        right.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(Palette.GAP, left, gap, right);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefWidth(STATEMENT);
        row.setMaxWidth(STATEMENT);
        String rest = "-fx-padding: 10 12 10 12; -fx-cursor: hand;";
        row.setStyle(rest + Palette.block(Palette.CONTROL));
        row.setOnMouseClicked(e -> {
            financeArea = area;
            financePage = page;
            ui.innerScrollAt.remove("showFinanceMenu:body");
            showFinanceMenu();
        });
        row.setOnMouseEntered(e -> row.setStyle(rest
                + Palette.block(Palette.RAISED, Palette.ACCENT)));
        row.setOnMouseExited(e -> row.setStyle(rest + Palette.block(Palette.CONTROL)));
        VBox.setMargin(row, new javafx.geometry.Insets(0, 0, 6, 0));
        return row;
    }

    /** The four figures that are true of the whole tab. */
    HBox financeVitals() {

        DebtManager ledger = ui.game.getDebtManager();
        NationalAccounts na = ui.game.getEconomyManager().getNationalAccounts();

        double cash = ui.game.getCash();
        double principal = ledger.getAllPrincipal();
        double annual = annualGdp(na);
        double revenue = na.getTotalRevenue();
        double service = couponNow(ledger);

        String moved = ui.game.hasTreasuryMonth()
                ? (ui.game.getTreasuryChange() >= 0 ? "+" : "−")
                        + money(Math.abs(ui.game.getTreasuryChange())) + " last month"
                : "no month closed yet";

        return vitalsBar(
                limitCell("TREASURY", money(cash), moved,
                        cash < 0 ? Palette.BAD : Palette.GOOD),
                limitCell("OWED", money(principal),
                        annual > 0 ? String.format("%.0f%% of annual GDP", principal / annual * 100)
                                   : "no output to compare",
                        annual > 0 && principal / annual > 1.2 ? Palette.BAD
                                : annual > 0 && principal / annual > .6 ? Palette.WARN
                                : Palette.TEXT_HEAD),
                limitCell("THE RATE", String.format("%.2f%%", ui.game.getInterestRate() * 100),
                        "rated " + ui.game.getCreditRating(),
                        ledger.atCeiling() ? Palette.BAD : Palette.ACCENT,
                        "Go to the policy rate, which this one is built on",
                        () -> {
                            ui.policyScreen.policyArea = "Money";
                            ui.policyScreen.policyPage = "The policy rate";
                            ui.policyScreen.dropProposal();
                            ui.innerScrollAt.remove("showPolicyMenu:body");
                            ui.policyScreen.showPolicyMenu();
                        }),
                limitCell("SERVICE", money(service) + "/mo",
                        revenue > 0 ? String.format("%.1f%% of what it takes in",
                                service / revenue * 100)
                                : "nothing came in",
                        revenue > 0 && service / revenue > .25 ? Palette.BAD
                                : revenue > 0 && service / revenue > .12 ? Palette.WARN
                                : Palette.GOOD));
    }

    /** What the city is charged in coupon each month, across every instrument. */
    double couponNow(DebtManager owed) {
        double coupon = 0;
        for (Debt debt : owed.getDebt()) coupon += debt.getMonthlyInterestExpense();
        return coupon;
    }

    /* =====================================================================
       ONE SUBJECT, ITS OWN STRIP
       ===================================================================== */

    void drawFinanceScreen() {

        String[] pages = switch (financeArea) {
            case "The book" -> BOOK_PAGES;
            case "Borrow"   -> BORROW_PAGES;
            case "Money"    -> MONEY_PAGES;
            case "The bond market" -> BOND_PAGES;
            default         -> POSITION_PAGES;
        };

        Label title = new Label(financeArea.toUpperCase());
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        javafx.scene.layout.FlowPane strip =
                chipStrip(pages, financePage, Palette.SIZE_LABEL, name -> {
                    financePage = name;
                    ui.innerScrollAt.remove("showFinanceMenu:body");
                    showFinanceMenu();
                });
        strip.setStyle("-fx-padding: 8 0 10 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        switch (financeArea) {
            case "The book" -> {
                if ("Buy back".equals(financePage)) buyBackPage(column);
                else                                theBookPage(column);
            }
            case "Borrow" -> borrowPage(column, "Abroad".equals(financePage));
            case "Money"  -> moneyPage(column);
            case "The bond market" -> {
                if ("A bond's book".equals(financePage)) bondBookPage(column);
                else                                     bondMarketPage(column);
            }
            default -> {
                switch (financePage) {
                    case "The ladder"   -> ladderPage(column);
                    case "Debt service" -> debtServicePage(column);
                    case "Home & abroad"-> homeAndAbroadPage(column);
                    case "Your rate"    -> yourRatePage(column);
                    default             -> positionPage(column);
                }
            }
        }

        Button back = new Button("All of finance");
        back.setOnAction(e -> {
            financeArea = null;
            ui.innerScrollAt.remove("showFinanceMenu:body");
            showFinanceMenu();
        });

        ui.rootMenu.getChildren().addAll(title, financeVitals(), strip,
                ui.scrolled(column, 250), back);
    }

    /* =====================================================================
       THE POSITION
       ===================================================================== */

    void positionPage(VBox column) {

        DebtManager ledger = ui.game.getDebtManager();
        NationalAccounts na = ui.game.getEconomyManager().getNationalAccounts();

        double cash = ui.game.getCash();
        double principal = ledger.getAllPrincipal();
        double overdraft = ledger.getOverdraft();
        double annual = annualGdp(na);

        column.getChildren().add(statementHead("What the city stands on"));

        column.getChildren().add(statementLine("In the treasury",
                moneyFull(cash), cash < 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(statementLine("Owed on paper",
                moneyFull(principal)));
        if (overdraft > 0) {
            column.getChildren().add(statementLine("...and overdrawn by",
                    moneyFull(overdraft), Palette.BAD));
        }
        column.getChildren().add(statementTotal("Net position",
                moneyFull(cash - principal - overdraft),
                cash - principal - overdraft >= 0 ? Palette.GOOD : Palette.BAD));

        column.getChildren().add(statementNote(
                "A negative net position is not by itself a problem — a city that borrows "
                + "to build owns the building. What decides whether the debt is carryable "
                + "is on the next two pages: when it falls due, and whether the month's "
                + "revenue covers what the month's paper costs."));

        /* --------------------------- the credit --------------------------- */
        column.getChildren().add(statementHead("What the market thinks"));

        column.getChildren().add(statementLine("Credit rating", ui.game.getCreditRating()));
        column.getChildren().add(statementLine("It will lend at",
                String.format("%.2f%%", ui.game.getInterestRate() * 100),
                ledger.atCeiling() ? Palette.BAD : Palette.ACCENT));
        column.getChildren().add(statementLine("A spotless city would pay",
                String.format("%.2f%%", ledger.floorRate() * 100), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("A hopeless one would pay",
                String.format("%.2f%%", ledger.ceilingRate() * 100),
                Palette.TEXT_MUTED));

        column.getChildren().add(sentence(
                "The gap between those two is the whole of the credit judgement, and the "
                + "Your rate page takes it apart into the four things that decide where "
                + "in it this city sits.", Palette.TEXT_BODY));

        /* ------------------------ against the economy ------------------------ */
        if (annual > 0) {
            column.getChildren().add(statementHead("Against the size of the economy"));

            javafx.scene.layout.GridPane t = grid(
                    new double[] {224, 128, 116}, rightAfterFirst(3));
            gridHead(t, "", "amount", "of GDP");

            int line = 1;
            line = ratioRow(t, line, "Debt outstanding", principal, annual, null);
            line = ratioRow(t, line, "A year of debt service", couponNow(ledger) * 12, annual,
                    null);
            line = ratioRow(t, line, "A year of revenue", na.getTotalRevenue() * 12, annual, null);
            column.getChildren().add(t);

            double ratio = principal / annual;
            column.getChildren().add(sentence(String.format(
                    "The city owes %.0f%% of a year of what it produces. %s",
                    ratio * 100,
                    ratio > 1.2
                        ? "That is more than a year of output, and the market is pricing it."
                        : ratio > .6
                        ? "That is carryable and the market is charging for it."
                        : "That is comfortable by any standard a real city is held to."),
                    ratio > 1.2 ? Palette.BAD_TEXT : ratio > .6 ? Palette.TEXT_BODY : Palette.GOOD));

            if (gdpEstimated(na)) {
                column.getChildren().add(statementNote(String.format(
                        "This city has only %d months of output recorded, so the year is "
                        + "scaled up from those rather than measured.", na.getMonthsRecorded())));
            }
        }
    }

    int ratioRow(javafx.scene.layout.GridPane table, int line,
                         String label, double amount, double annual, String tone) {
        table.add(gridCell(label, Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
        table.add(gridCell(money(amount), tone == null ? Palette.TEXT_HEAD : tone,
                Palette.SIZE_CAPTION, true), 1, line);
        table.add(gridCell(annual > 0 ? String.format("%.1f%%", amount / annual * 100) : "—",
                Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
        return line + 1;
    }

    /* =====================================================================
       THE LADDER

       The one thing the game could not show, and the one a term loan makes
       essential: WHEN the money is due. A city can carry any amount of debt
       whose payments are spread out and be destroyed by a smaller amount that
       all falls in one year - and until this existed the only evidence was five
       chips on the bottom strip and a date on each.

       EVERYTHING OWED, not just principal. Debt.remainingCashFlows() is the
       exact month-by-month schedule each instrument declares - coupons and
       principal together - because what a treasury has to find in a given year
       is the sum of both, and separating them would draw a ladder nobody has to
       climb.
       ===================================================================== */

    /** How many years out the ladder is drawn before it gives up and totals. */
    static final int LADDER_YEARS = 12;

    void ladderPage(VBox column) {

        java.util.List<Debt> paper = ui.game.getDebtManager().getDebt();

        column.getChildren().add(statementHead("When the money is due"));

        if (paper.isEmpty()) {
            column.getChildren().add(sentence(
                    "The city owes nothing, so nothing falls due. The Borrow page is where "
                    + "that changes.", Palette.TEXT_MUTED));
            return;
        }

        column.getChildren().add(ladderChart(paper, null, null));

        column.getChildren().add(statementNote(
                "Coupons and principal together, because what a treasury has to find in a "
                + "given year is the sum of both. A year with a tall bar is a year the "
                + "city has to have the money, or refinance before it arrives."));

        /* ----------------------- and what it adds up to ----------------------- */
        double[][] years = ladderYears(paper, null);
        double worst = 0;
        int worstYear = 0;
        double total = 0;
        for (int y = 0; y < years.length; y++) {
            double sum = years[y][0] + years[y][1] + years[y][2];
            total += sum;
            if (sum > worst) { worst = sum; worstYear = y; }
        }

        double revenue = ui.game.getEconomyManager().getNationalAccounts().getTotalRevenue() * 12;

        column.getChildren().add(statementHead("The year that matters"));
        column.getChildren().add(statementLine("Heaviest year",
                worstYear == 0 ? "the next twelve months"
                        : String.format("year %d from now", worstYear + 1)));
        column.getChildren().add(statementLine("What falls due in it",
                moneyFull(worst), Palette.WARN));
        if (revenue > 0) {
            column.getChildren().add(statementLine("Against a year of revenue",
                    String.format("%.0f%%", worst / revenue * 100),
                    worst / revenue > .5 ? Palette.BAD
                            : worst / revenue > .25 ? Palette.WARN : Palette.GOOD));
        }
        column.getChildren().add(statementTotal("Everything still to pay",
                moneyFull(total), Palette.TEXT_HEAD));

        if (revenue > 0 && worst / revenue > .5) {
            column.getChildren().add(alert("One year carries more than half a year of revenue",
                    String.format("%s falls due in %s, against %s of revenue a year. A city "
                    + "meets a wall like that by refinancing it before it arrives, not on "
                    + "the morning — and refinancing is dearest exactly when everybody can "
                    + "see the wall.", moneyFull(worst),
                    worstYear == 0 ? "the next twelve months"
                            : "year " + (worstYear + 1), money(revenue))));
        }
    }

    /**
     * What falls due in each of the next LADDER_YEARS years, split by instrument.
     *
     * @param extra an additional schedule to draw on top, or null
     * @return [year][note, serial, term] plus one more row for the extra
     */
    double[][] ladderYears(java.util.List<Debt> paper, double[] extra) {

        double[][] years = new double[LADDER_YEARS + 1][4];

        for (Debt debt : paper) {
            int slot = switch (debt.getType()) {
                case "NOTE"   -> 0;
                case "SERIAL" -> 1;
                default       -> 2;
            };
            double[] flows = debt.remainingCashFlows();
            for (int m = 0; m < flows.length; m++) {
                years[Math.min(LADDER_YEARS, m / 12)][slot] += flows[m];
            }
        }

        if (extra != null) {
            for (int m = 0; m < extra.length; m++) {
                years[Math.min(LADDER_YEARS, m / 12)][3] += extra[m];
            }
        }
        return years;
    }

    /**
     * The ladder itself: a bar per year, stacked by instrument.
     *
     * @param extra      a proposed issue, drawn in a fourth colour on top
     * @param extraLabel what to call it in the key
     */
    VBox ladderChart(java.util.List<Debt> paper, double[] extra, String extraLabel) {

        double[][] years = ladderYears(paper, extra);

        double peak = 0;
        for (double[] year : years) {
            peak = Math.max(peak, year[0] + year[1] + year[2] + year[3]);
        }
        if (peak <= 0) peak = 1;

        final double TALL = 132;
        final double WIDE = 40;

        HBox bars = new HBox(6);
        bars.setAlignment(Pos.BOTTOM_LEFT);

        int thisMonth = ui.game.getMonth();

        for (int y = 0; y < years.length; y++) {

            double sum = years[y][0] + years[y][1] + years[y][2] + years[y][3];

            VBox stack = new VBox(0);
            stack.setAlignment(Pos.BOTTOM_CENTER);
            stack.setPrefWidth(WIDE);
            stack.setMinWidth(WIDE);

            /*
             * THE FIGURE ONLY WHERE IT SAYS SOMETHING.
             *
             * A number over every bar is the classic chart mistake: thirteen
             * labels is thirteen things to read, and the whole point of a
             * ladder is that the SHAPE answers the question. So: the year in
             * hand, the peak, and anything within reach of the peak. The rest
             * are on hover.
             */
            boolean worthSaying = sum > 0
                    && (y == 0 || sum >= peak * .999 || sum >= peak * .25);
            Label amount = new Label(worthSaying ? money(sum) : "");
            amount.setStyle(Palette.figure(Palette.SIZE_CAPTION,
                    sum >= peak * .999 ? Palette.WARN : Palette.TEXT_MUTED));
            amount.setRotate(-90);
            javafx.scene.Group turned = new javafx.scene.Group(amount);

            VBox column = new VBox(0);
            column.setAlignment(Pos.BOTTOM_CENTER);
            column.setPrefHeight(TALL);
            column.setMinHeight(TALL);

            String[] colours = {Palette.LADDER[0], Palette.LADDER[1], Palette.LADDER[2],
                                Palette.GOOD};
            String[] names = {"Notes", "Serial bonds", "Term loans",
                              extraLabel == null ? "Proposed" : extraLabel};

            // Tallest slice at the bottom of the stack is wrong for a ladder -
            // the instruments are ordered short to long and the colour ramp says
            // so, so the drawing order has to match the key.
            for (int s = 3; s >= 0; s--) {
                double part = years[y][s];
                if (part <= 0) continue;
                double h = Math.max(2, part / peak * TALL);
                Region seg = new Region();
                seg.setPrefSize(WIDE - 6, h);
                seg.setMinSize(WIDE - 6, h);
                seg.setMaxSize(WIDE - 6, h);
                seg.setStyle("-fx-background-color: " + colours[s] + ";");
                Tooltip.install(seg, new Tooltip(names[s] + ": " + moneyFull(part)));
                column.getChildren().add(seg);
                // A 2px gap of the ground between fills, so two segments of one
                // ramp do not read as one taller segment.
                Region gap = new Region();
                gap.setPrefHeight(2);
                gap.setMinHeight(2);
                column.getChildren().add(gap);
            }

            // The YEAR the bar covers, not a month inside it. Twelve month
            // names down the axis is twelve things to read; twelve years is a
            // scale.
            Label when = new Label(y >= LADDER_YEARS ? "later"
                    : String.valueOf(CityCalendar.yearOf(thisMonth + y * 12)));
            when.setStyle(Palette.words(Palette.SIZE_CAPTION,
                    y == 0 ? Palette.TEXT_HEAD : Palette.TEXT_LABEL));

            stack.getChildren().addAll(turned, column, when);
            bars.getChildren().add(stack);
        }

        /* --------------------------- the key --------------------------- */
        // ONLY WHAT IS ACTUALLY DRAWN. A key listing three instruments in
        // front of a chart made entirely of one is a key that has to be read
        // to be dismissed.
        javafx.scene.layout.FlowPane key = new javafx.scene.layout.FlowPane(14, 4);
        key.setStyle("-fx-padding: 10 0 0 0;");
        String[] kNames = {"Notes", "Serial bonds", "Term loans"};
        for (int i = 0; i < 3; i++) {
            double any = 0;
            for (double[] year : years) any += year[i];
            if (any <= 0) continue;
            key.getChildren().add(keySwatch(Palette.LADDER[i], kNames[i]));
        }
        if (extra != null) {
            key.getChildren().add(keySwatch(Palette.GOOD,
                    extraLabel == null ? "What you are about to issue" : extraLabel));
        }

        VBox box = new VBox(0, bars, key);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 22 0 4 0;");
        return box;
    }

    /* =====================================================================
       DEBT SERVICE

       The ratio the market actually rates a city on, and the one a player can
       do something about. Debt against GDP is what everybody quotes; debt
       service against REVENUE is what decides whether next month's coupon gets
       paid, because a lender is repaid out of what the city collects and not
       out of what the economy produces.
       ===================================================================== */

    void debtServicePage(VBox column) {

        DebtManager ledger = ui.game.getDebtManager();
        NationalAccounts na = ui.game.getEconomyManager().getNationalAccounts();

        double revenue = na.getTotalRevenue();
        double coupon = couponNow(ledger);
        double repaid = ui.game.hasTreasuryMonth() ? ui.game.getTreasuryRepaid() : 0;
        double service = coupon + repaid;

        column.getChildren().add(statementHead("What the paper costs a month"));

        column.getChildren().add(statementLine("Coupon on everything outstanding",
                moneyFull(coupon), coupon > 0 ? Palette.WARN : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("Principal that fell due last month",
                moneyFull(repaid), repaid > 0 ? Palette.WARN : Palette.TEXT_SPENT));
        column.getChildren().add(statementTotal("Debt service",
                moneyFull(service), service > 0 ? Palette.WARN : Palette.TEXT_HEAD));

        column.getChildren().add(statementNote(
                "Only the coupon is an expense; the principal is a balance-sheet movement. "
                + "Both leave the treasury, which is why they are added here and separated "
                + "on the Government spending page."));

        /* ------------------------- against the take ------------------------- */
        column.getChildren().add(statementHead("Against what the city collects"));

        double share = revenue > 0 ? service / revenue : 0;
        column.getChildren().add(serviceBands(share));

        column.getChildren().add(statementLine("A month of revenue", moneyFull(revenue)));
        column.getChildren().add(statementLine("...of which goes to lenders",
                revenue > 0 ? String.format("%.1f%%", share * 100) : "—",
                share > .25 ? Palette.BAD : share > .12 ? Palette.WARN : Palette.GOOD));

        column.getChildren().add(sentence(
                share > .25
                    ? "A quarter of the take is going to lenders. That is the level at "
                    + "which a real city stops being able to choose what it spends on, "
                    + "because the choice has already been made."
                    : share > .12
                    ? "Comfortable, but it is being felt. Every point of rate from here "
                    + "comes out of something the city was going to do."
                    : "The city is barely feeling its debt. That is the moment borrowing "
                    + "is cheapest and the moment nobody thinks about it.",
                share > .25 ? Palette.BAD_TEXT : share > .12 ? Palette.TEXT_BODY : Palette.GOOD));

        /* ------------------------ what the world sees ------------------------ */
        if (ledger.hasForeignDebt()) {
            column.getChildren().add(statementHead("...and what the world sees"));
            double exports = ledger.getMonthlyExports() * 12;
            double next = ledger.nextYearService();
            column.getChildren().add(statementLine("A year of exports", moneyFull(exports)));
            column.getChildren().add(statementLine("A year of foreign debt service",
                    moneyFull(next), Palette.WARN));
            column.getChildren().add(statementLine("Which is",
                    exports > 0 ? String.format("%.0f%% of exports", next / exports * 100) : "—",
                    exports > 0 && next / exports > .25 ? Palette.BAD : Palette.TEXT_HEAD));
            column.getChildren().add(statementNote(
                    "Foreign paper is repaid in somebody else's money, and the only way "
                    + "the city earns that is by selling abroad. Exports are the "
                    + "denominator the world actually uses."));
        }
    }

    /**
     * The three bands a debt-service ratio falls in, with the city's own mark.
     *
     * BANDS RATHER THAN A NUMBER, because 14% means nothing on its own and
     * "comfortable, and about to stop being" means something to anybody.
     */
    VBox serviceBands(double share) {

        final double WIDE = STATEMENT - 40;

        HBox track = new HBox(2);
        track.setAlignment(Pos.CENTER_LEFT);

        double[] widths = {.12, .13, .25};             // 0-12, 12-25, 25-50
        String[] tones = {Palette.GOOD, Palette.WARN, Palette.BAD};
        String[] names = {"comfortable", "felt", "constrained"};
        for (int i = 0; i < 3; i++) {
            Region band = new Region();
            double w = WIDE * widths[i] / .5;
            band.setPrefSize(w, 12);
            band.setMinSize(w, 12);
            band.setStyle("-fx-background-color: " + tones[i] + "; -fx-opacity: .35;");
            Tooltip.install(band, new Tooltip(names[i]));
            track.getChildren().add(band);
        }

        // The mark, positioned by the ratio. Clamped, because a city paying
        // 300% of its revenue to lenders should show as pinned at the end
        // rather than as a marker somewhere off the screen.
        double at = Math.max(0, Math.min(1, share / .5)) * WIDE;
        Region mark = new Region();
        mark.setPrefSize(3, 20);
        mark.setMinSize(3, 20);
        mark.setStyle("-fx-background-color: " + Palette.TEXT_HEAD + ";");

        javafx.scene.layout.Pane over = new javafx.scene.layout.Pane(mark);
        over.setPrefSize(WIDE, 20);
        over.setMinSize(WIDE, 20);
        over.setMaxSize(WIDE, 20);
        mark.setLayoutX(Math.min(WIDE - 3, at));

        Label caption = new Label(String.format(
                "%.1f%% of revenue goes to lenders   ·   comfortable to 12%%, "
                + "constrained past 25%%", share * 100));
        caption.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));

        VBox box = new VBox(2, track, over, caption);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 6 0 10 0;");
        return box;
    }

    /* =====================================================================
       HOME AND ABROAD
       ===================================================================== */

    void homeAndAbroadPage(VBox column) {

        DebtManager ledger = ui.game.getDebtManager();
        ForeignAccounts fx = ui.game.getForeignAccounts();

        double home = ledger.getDomesticPrincipal();
        double away = ledger.getForeignPrincipal();
        double all = home + away;

        column.getChildren().add(statementHead("Who the city owes"));

        if (all <= 0) {
            column.getChildren().add(sentence("The city owes nothing, at home or abroad.",
                    Palette.TEXT_MUTED));
            return;
        }

        java.util.List<Slice> split = new java.util.ArrayList<>();
        if (home > 0) split.add(new Slice("At home", home, Palette.LADDER[1]));
        if (away > 0) split.add(new Slice("Abroad", away, Palette.SPENDING_RAMP[3]));
        column.getChildren().add(stackedBar(split, STATEMENT - 40));

        column.getChildren().add(statementLine("Owed at home", moneyFull(home)));
        column.getChildren().add(statementLine("Owed abroad", moneyFull(away),
                away > 0 ? Palette.WARN : Palette.TEXT_SPENT));
        column.getChildren().add(statementTotal("Altogether", moneyFull(all),
                Palette.TEXT_HEAD));

        column.getChildren().add(sentence(
                "Domestic paper is bought by the city's own bank, so the coupon is the "
                + "bank's income and none of it crosses the city's edge. Foreign paper is "
                + "bought by somebody else, is repaid in their money, and every payment "
                + "genuinely leaves. That is the whole difference, and it is why the two "
                + "are never added together on a rating.", Palette.TEXT_BODY));

        /* ------------------------- the foreign half ------------------------- */
        if (away > 0) {
            column.getChildren().add(statementHead("The dollar debt"));

            column.getChildren().add(statementLine("Face, in " + Currency.FOREIGN_CODE,
                    usdFull(ledger.getForeignPrincipalUsd())));
            column.getChildren().add(statementLine("At an exchange rate of",
                    fxRate(ledger.getExchangeRate())));
            column.getChildren().add(statementLine("Which comes to", moneyFull(away)));

            if (Math.abs(fx.getLastRevaluation()) > .005) {
                column.getChildren().add(statementLine("The currency moved it by",
                        (fx.getLastRevaluation() > 0 ? "+" : "−")
                                + moneyFull(Math.abs(fx.getLastRevaluation())),
                        fx.getLastRevaluation() > 0 ? Palette.BAD : Palette.GOOD));
                column.getChildren().add(statementNote(fx.getLastRevaluation() > 0
                        ? "Nobody was paid a cent for that. The same paper, a weaker "
                        + "currency, and a bigger debt — which is what a devaluation does "
                        + "to a country that borrowed in someone else's money."
                        : "The same paper, a stronger currency, and a smaller debt. It "
                        + "works both ways and it is not income either way."));
            }

            column.getChildren().add(statementHead("What is behind it"));
            column.getChildren().add(statementLine("Reserves held",
                    moneyFull(fx.getReserves()),
                    fx.getReserves() > 0 ? Palette.GOOD : Palette.TEXT_SPENT));
            column.getChildren().add(statementLine("Import cover",
                    String.format("%.1f months", ledger.getImportCover()),
                    ledger.getImportCover() < 3 ? Palette.BAD
                            : ledger.getImportCover() < 6 ? Palette.WARN : Palette.GOOD));
            column.getChildren().add(statementNote(
                    "Three months of cover is the line below which the world starts to "
                    + "price a currency for a crisis rather than for a trade balance. The "
                    + "Trade tab is where reserves are bought and sold."));
        } else {
            column.getChildren().add(statementNote(
                    "Nothing is owed abroad. Every piece of the city's paper is held by "
                    + "its own bank, so its debt cannot be made bigger by the exchange "
                    + "rate — which is a real form of safety and one that is easy to give "
                    + "up for a lower headline coupon."));
        }
    }

    /* =====================================================================
       YOUR RATE, TAKEN APART
       ===================================================================== */

    void yourRatePage(VBox column) {

        DebtManager ledger = ui.game.getDebtManager();

        double floor = ledger.baseComponent();
        double gdpPart = ledger.gdpSpread();
        double revPart = ledger.revenueSpread();
        double total = ui.game.getInterestRate();

        column.getChildren().add(statementHead("Why money costs this city what it does"));

        java.util.List<Slice> parts = new java.util.ArrayList<>();
        if (floor > 0)   parts.add(new Slice("The floor", floor, Palette.REVENUE_RAMP[1]));
        if (gdpPart > 0) parts.add(new Slice("Debt vs output", gdpPart, Palette.SPENDING_RAMP[1]));
        if (revPart > 0) parts.add(new Slice("Debt vs revenue", revPart, Palette.SPENDING_RAMP[3]));
        if (!parts.isEmpty()) {
            column.getChildren().add(stackedBar(parts, STATEMENT - 40));
        }

        javafx.scene.layout.GridPane t = grid(
                new double[] {214, 72, 182},
                new javafx.geometry.HPos[] {javafx.geometry.HPos.LEFT,
                                            javafx.geometry.HPos.RIGHT,
                                            javafx.geometry.HPos.LEFT});
        gridHead(t, "", "points", "   what moves it");

        int line = 1;
        line = rateRow(t, line, "The floor everybody pays", floor,
                floor > ledger.getPolicyRate() + 1e-9
                        ? "what the bank's money costs it"
                        : "the " + pct2(ledger.getPolicyRate()) + " dial");
        line = rateRow(t, line, "Debt against a year of output", gdpPart,
                gdpPart <= 0 ? "nothing owed" : "grow, or owe less");
        line = rateRow(t, line, "Debt against a year of tax", revPart,
                revPart <= 0 ? "nothing owed" : "tax more, or owe less");
        // "What the bank adds" was the strain premium's row; 0.7.7 removed it.
        column.getChildren().add(t);

        column.getChildren().add(statementTotal("What the market quotes",
                String.format("%.2f%%", total * 100),
                ledger.atCeiling() ? Palette.BAD : Palette.ACCENT));

        /*
         * ...AND AT THE LONG END (0.7.1). The figure above is the short end -
         * the note, the T-bill rate. Thirty-year money pays the term premium
         * on top of it, less what the central bank's holdings compress of it.
         */
        double premium30 = DebtManager.termPremium(360);
        double comp30 = ledger.compression(360);
        javafx.scene.layout.GridPane longEnd = grid(
                new double[] {214, 72, 182},
                new javafx.geometry.HPos[] {javafx.geometry.HPos.LEFT,
                                            javafx.geometry.HPos.RIGHT,
                                            javafx.geometry.HPos.LEFT});
        gridHead(longEnd, "", "points", "   what moves it");
        int row = 1;
        row = rateRow(longEnd, row, "At thirty years, the term premium", premium30,
                "the price of time, the same for everyone");
        longEnd.add(gridCell("...less what the central bank holds", Palette.TEXT_BODY,
                Palette.SIZE_CAPTION, false), 0, row);
        longEnd.add(gridCell(comp30 > 0 ? String.format("−%.2f", comp30 * 100) : "—",
                comp30 > 0 ? Palette.GOOD : Palette.TEXT_SPENT, Palette.SIZE_CAPTION, true), 1, row);
        longEnd.add(gridCell("   the holdings dial, on the Policy tab", Palette.TEXT_MUTED,
                Palette.SIZE_CAPTION, false), 2, row);
        column.getChildren().add(longEnd);
        column.getChildren().add(statementTotal("What thirty-year money costs",
                pct2(ledger.curveRate(360)), Palette.ACCENT));
        /*
         * WHY THE POLICY SCREEN SAYS SOMETHING ELSE (2026-09-12). Jerus, seeing
         * 3% on one screen and 1% on the other: one was the dial and the other
         * what the market quoted THIS city, with the floor two points UNDER the
         * dial. Since 0.7.0 the floor is the dial itself - the bank's reserves
         * earn it at the central bank, so nobody lends the city less - and the
         * two screens agree on a city that owes nothing.
         */
        column.getChildren().add(statementNote(String.format(
                "The Policy tab's %s is the rate the city's central bank pays on the "
                + "bank's reserves, and nobody lends for less than money earns sitting "
                + "still - so it is the floor here too. Everything below it is the city's "
                + "own record.",
                pct2(ledger.getPolicyRate()))));

        /* --------------------- how far each measure has run --------------------- */
        column.getChildren().add(statementHead("How much each measure has left to say"));

        column.getChildren().add(stressRow("Debt against output", ledger.gdpStress()));
        column.getChildren().add(stressRow("Debt against revenue", ledger.revenueStress()));

        column.getChildren().add(statementNote(String.format(
                "Each measure adds up to %.0f points and then stops, so being sound on one "
                + "and hopeless on the other is priced as exactly half the worst case. Both "
                + "run out at %.0f years of whatever they measure against.",
                DebtManager.maxSpreadPerMeasure() * 100,
                DebtManager.fullStressMultiple())));

        // "The bank is adding to every rate in the city" stood here until 0.7.7,
        // when the strain premium it reported went (Bank, WHAT A LOAN COSTS).

        if (ledger.getDefaultScar() > 0) {
            column.getChildren().add(alert("And a default is still being charged for",
                    String.format("%.1f points on top of everything above, %d months after "
                    + "it happened. It fades over about five years.",
                    ledger.getDefaultScar() * 100,
                    Math.max(0, ledger.getMonthsSinceForeignDefault()))));
        }
    }

    int rateRow(javafx.scene.layout.GridPane table, int line,
                        String label, double rate, String lever) {
        table.add(gridCell(label, Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
        table.add(gridCell(rate > 0 ? String.format("%.2f", rate * 100) : "—",
                rate > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT,
                Palette.SIZE_CAPTION, true), 1, line);
        table.add(gridCell("   " + lever, Palette.TEXT_MUTED,
                Palette.SIZE_CAPTION, false), 2, line);
        return line + 1;
    }

    /** How much of one measure's worst case the city has used up. */
    VBox stressRow(String label, double stress) {

        final double WIDE = STATEMENT - 200;
        double used = Math.max(0, Math.min(1, stress));

        Region track = new Region();
        track.setPrefSize(WIDE, 8);
        track.setMinSize(WIDE, 8);
        track.setStyle("-fx-background-color: " + Palette.CONTROL
                + "; -fx-background-radius: 4;");

        Region fill = new Region();
        fill.setPrefSize(Math.max(2, WIDE * used), 8);
        fill.setMinSize(Math.max(2, WIDE * used), 8);
        fill.setStyle("-fx-background-color: "
                + (used > .8 ? Palette.BAD : used > .4 ? Palette.WARN : Palette.GOOD)
                + "; -fx-background-radius: 4;");

        StackPane bar = new StackPane(track, fill);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);

        Label name = new Label(label);
        name.setPrefWidth(170);
        name.setMinWidth(170);
        name.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_BODY));

        Label pct = new Label(String.format("%.0f%%", used * 100));
        pct.setPrefWidth(50);
        pct.setMinWidth(50);
        pct.setAlignment(Pos.CENTER_RIGHT);
        pct.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));

        HBox row = new HBox(Palette.GAP_TIGHT, name, bar, pct);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);
        row.setStyle("-fx-padding: 3 0 3 0;");

        VBox box = new VBox(row);
        box.setMaxWidth(STATEMENT);
        return box;
    }

    /* =====================================================================
       THE BOOK
       ===================================================================== */

    void theBookPage(VBox column) {

        DebtManager ledger = ui.game.getDebtManager();
        java.util.List<Debt> paper = new java.util.ArrayList<>(ledger.getDebt());
        paper.sort(java.util.Comparator.comparingInt(Debt::getMaturityMonth));

        column.getChildren().add(statementHead("Every piece of paper the city has sold"));

        if (paper.isEmpty()) {
            column.getChildren().add(sentence("Nothing outstanding.", Palette.TEXT_MUTED));
            return;
        }

        javafx.scene.layout.GridPane t = grid(
                new double[] {104, 116, 110, 100, 122}, rightAfterFirst(5));
        gridHead(t, "", "owed", "coupon /mo", "matures", "how far off");

        int month = ui.game.getMonth();
        int line = 1;
        double principal = 0, coupon = 0;

        for (Debt debt : paper) {

            principal += debt.getOustandingPrincipal();
            coupon += debt.getMonthlyInterestExpense();

            int gap = debt.getMaturityMonth() - month;
            String tone = gap <= 3 ? Palette.BAD : gap <= 12 ? Palette.WARN : Palette.TEXT_HEAD;

            Label kind = new Label((debt.isForeign() ? Currency.FOREIGN_CODE + " " : "")
                    + debt.getType());
            kind.setStyle(Palette.words(Palette.SIZE_CAPTION,
                    instrumentColour(debt.getType())));
            t.add(kind, 0, line);

            t.add(gridCell(money(debt.getOustandingPrincipal()),
                    Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, line);
            t.add(gridCell(debt.getMonthlyInterestExpense() > 0
                            ? money(debt.getMonthlyInterestExpense()) : "none",
                    debt.getMonthlyInterestExpense() > 0
                            ? Palette.TEXT_BODY : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 2, line);
            t.add(gridCell(CityCalendar.formatShort(debt.getMaturityMonth()),
                    Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 3, line);
            t.add(gridCell(CityCalendar.until(month, debt.getMaturityMonth()),
                    tone, Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        column.getChildren().add(t);

        column.getChildren().add(statementTotal("On the book altogether",
                moneyFull(principal), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("Coupon a month",
                moneyFull(coupon), coupon > 0 ? Palette.WARN : Palette.TEXT_SPENT));

        double notes = ledger.getNotePrincipal();
        if (notes > 0) {
            column.getChildren().add(statementNote(String.format(
                    "%s of that pays no coupon at all — the lender's return was the "
                    + "discount, taken out of the proceeds at issue. It costs nothing "
                    + "monthly and the whole face falls due at once.", money(notes))));
        }

        /* ------------------------ who holds it (0.7.1) ------------------------
         * Jerus: "yes households should be able to hold." The city's paper has
         * four holders now - the households, the bank, the central bank, and
         * the world for the dollar bonds - and this is the split, at face.
         */
        double atHome = ledger.getDomesticPrincipal();
        double abroad = ledger.getForeignPrincipal();
        double owed = atHome + abroad;
        if (owed > 0) {
            column.getChildren().add(statementHead("Who holds it"));
            double[] held = {ledger.householdPrincipal(), ledger.bankPrincipal(),
                             ledger.centralBankPrincipal(), abroad};
            String[] who = {"The households", "The bank", "The central bank", "Abroad"};
            for (int i = 0; i < who.length; i++) {
                column.getChildren().add(statementLine(who[i], money(held[i])
                        + String.format("   %.0f%%", held[i] / owed * 100),
                        held[i] > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT));
            }
            column.getChildren().add(statementNote(
                    "The households buy their share when an issue settles and it pays them "
                    + "better than a deposit, and sell to the bank's desk when they are short. "
                    + "The central bank buys and sells the bank's term paper with money it "
                    + "makes - the holdings dial on the Policy tab. The bank holds the rest. "
                    + "Dollar paper is held abroad."));
        }

        column.getChildren().add(statementNote(
                "The Buy back page prices every one of these at what it is worth today, "
                + "which is not its face value."));
    }

    /* =====================================================================
       BUY BACK

       On the bond it is about. It was a separate screen with its own header
       row and its own copy of the table, so a player comparing what they owe
       against what it would cost to clear had the two facts on two screens.
       ===================================================================== */

    /**
     * Buying the city's own debt back, one bond at a time.
     *
     * The mirror of the finance menu: that one turns future payments into cash
     * now, this one turns cash now into no future payments.
     *
     * WHY EVERY ROW SHOWS THE DISCOUNT AND NOT JUST THE PRICE. The interesting
     * thing here is never the price on its own, it is the gap between the price
     * and the face - which is a statement about the city's credit. Paper issued
     * when the city was sound and held while its rate climbed is CHEAP to
     * retire: $500,000 of face for $302,646, a real $197,354 gain. Paper issued
     * dear and held while the city improved costs a premium to escape. Showing
     * only "Buy back: $302,646" would hide the entire mechanic behind a number
     * that looks like a bill.
     */
    void buyBackPage(VBox column) {

        DebtManager ledger = ui.game.getDebtManager();
        java.util.List<Debt> paper = new java.util.ArrayList<>(ledger.getDebt());
        paper.sort(java.util.Comparator.comparingInt(Debt::getMaturityMonth));

        column.getChildren().add(statementHead("What it would cost to make it go away"));

        if (paper.isEmpty()) {
            column.getChildren().add(sentence("The city owes nothing. Nothing to buy back.",
                    Palette.TEXT_MUTED));
            return;
        }

        double rate = ledger.getRate();

        column.getChildren().add(sentence(String.format(
                "Every bond is priced at what it is worth today: the present value of "
                + "everything it still owes, discounted at the curve's rate for the years it "
                + "has left - %.2f%% for a note, %.2f%% for thirty-year money. Above par means "
                + "getting out costs a premium. Below par means your own paper has become cheap "
                + "to retire — which happens when your credit has got worse, so it is not the "
                + "bargain it looks like.",
                rate * 100, ledger.curveRate(360) * 100), Palette.TEXT_BODY));

        column.getChildren().add(statementLine("Cash available",
                moneyFull(ui.game.getCash()),
                ui.game.getCash() > 0 ? Palette.GOOD : Palette.BAD));

        int month = ui.game.getMonth();
        for (Debt debt : paper) {
            // Each at its own point on the curve (0.7.1); a dollar bond at the
            // standing rate, as it always was.
            double at = debt.isForeign() ? rate : ledger.curveRate(debt.getRemainingMonths());
            column.getChildren().add(buyBackRow(debt, at, month));
        }

        column.getChildren().add(statementTotal("All of it, cleared today",
                moneyFull(ledger.getTotalMarketValue()), Palette.TEXT_HEAD));
        column.getChildren().add(statementNote(String.format(
                "%s of face for %s of cash. Retiring paper lowers the city's rate, which "
                + "raises what the rest of it is worth — so these prices are quoted off the "
                + "market as it stands, not as it will stand after the first trade.",
                money(ledger.getAllPrincipal()), money(ledger.getTotalMarketValue()))));
    }

    /** One bond, with what it would cost to clear and what that saves. */
    VBox buyBackRow(Debt debt, double rate, int month) {

        double face = debt.getOustandingPrincipal();
        double price = debt.getMarketValue(rate);
        double gain = face - price;
        boolean affordable = price > 0 && price <= ui.game.getCash();

        Label kind = new Label((debt.isForeign() ? Currency.FOREIGN_CODE + " " : "")
                + debt.getType() + "  ·  " + CityCalendar.until(month, debt.getMaturityMonth()));
        kind.setStyle(Palette.words(Palette.SIZE_BODY, instrumentColour(debt.getType())));

        Label terms = new Label(String.format(
                "%s of par   ·   yield %.2f%%   ·   coupon %s/mo%s",
                String.format("%.1f", debt.getPriceAsPercentOfPar(rate)),
                debt.getCurrentYield(rate) * 100,
                money(debt.getMonthlyInterestExpense()),
                debt.isForeign()
                        ? String.format("   ·   %s at %.4f",
                                usd(debt.principalInCurrency()),
                                debt.getExchangeRate())
                        : ""));
        terms.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        VBox left = new VBox(0, kind, terms);
        left.setAlignment(Pos.CENTER_LEFT);
        // PINNED, because an HBox squeezes whichever child will let it and the
        // terms line was losing its last two facts to an ellipsis.
        left.setPrefWidth(330);
        left.setMinWidth(330);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label owed = new Label(money(face));
        owed.setPrefWidth(104);
        owed.setMinWidth(104);
        owed.setAlignment(Pos.CENTER_RIGHT);
        owed.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.TEXT_HEAD));

        Label cost = new Label(money(price));
        cost.setPrefWidth(104);
        cost.setMinWidth(104);
        cost.setAlignment(Pos.CENTER_RIGHT);
        cost.setStyle(Palette.figure(Palette.SIZE_BODY,
                gain >= 0 ? Palette.GOOD : Palette.BAD));

        Button buy = new Button(affordable ? "Buy it back" : "Too dear");
        buy.setMinWidth(92);
        buy.setDisable(!affordable);
        if (affordable) {
            buy.setStyle("-fx-background-color: " + "#2f7d52"
                    + "; -fx-text-fill: white;");
        }
        buy.setOnAction(e -> {
            ui.game.repurchaseDebt(debt);
            // Straight back to a freshly built page: retiring paper moves the
            // rate, so every other row's price is now stale.
            ui.innerScrollAt.remove("showFinanceMenu:body");
            showFinanceMenu();
        });

        HBox row = new HBox(Palette.GAP, left, gap, owed, cost, buy);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);
        row.setPrefWidth(STATEMENT);
        row.setStyle("-fx-padding: 7 10 7 10;" + Palette.block(Palette.CONTROL));

        Label verdict = new Label(gain >= 0
                ? String.format("Clears %s of debt for %s — %s less than the face.",
                        money(face), money(price), money(gain))
                : String.format("Clears %s of debt for %s — a premium of %s, because this "
                        + "bond pays more than the city could borrow at today.",
                        money(face), money(price), money(-gain)));
        verdict.setStyle(Palette.words(Palette.SIZE_CAPTION,
                gain >= 0 ? Palette.TEXT_MUTED : Palette.WARN));
        verdict.setWrapText(true);
        verdict.setMaxWidth(STATEMENT - 20);

        VBox box = new VBox(2, row, verdict);
        box.setMaxWidth(STATEMENT);
        VBox.setMargin(box, new javafx.geometry.Insets(0, 0, 8, 0));
        return box;
    }

    /* ---------------------- ROLLING WHAT FALLS DUE (0.7.13) ----------------------
     *
     * Jerus: "you should have an option, where it defualt toggles on, so
     * basically the game checks whats going to mature next month, and issues
     * what the treasury is lacking ... either manual, aka you do it yourself,
     * or that it defualts to same structure, or that it defualts to 12 month
     * tbill". At the top of both borrow pages, since it is the treasury's own
     * borrowing, at home and abroad: the three chips, a sentence on what the
     * setting does, and next month's maturities with what the press will
     * issue for them - Game.rolloverPlan(), the function the press books, so
     * the sentence here is the issue there.
     */
    static final String[] ROLLOVER_CHIPS = { "By hand", "Same structure", "12-month notes" };

    void rolloverBlock(VBox column) {
        Game game = ui.game;
        Rollover.Mode mode = game.getRolloverMode();
        String here = game.getCurrency().qualifiedSymbol();

        column.getChildren().add(statementHead("Rolling what falls due"));
        column.getChildren().add(chipStrip(ROLLOVER_CHIPS, ROLLOVER_CHIPS[mode.ordinal()], Palette.SIZE_BODY,
                picked -> {
                    for (int i = 0; i < ROLLOVER_CHIPS.length; i++) {
                        if (ROLLOVER_CHIPS[i].equals(picked)) game.setRolloverMode(Rollover.Mode.values()[i]);
                    }
                    showFinanceMenu();
                }));
        column.getChildren().add(sentence(switch (mode) {
            case MANUAL -> "Nothing automatic: what falls due is paid out of the treasury's cash, and "
                    + "what the cash cannot cover the central bank advances.";
            case SAME_STRUCTURE -> "Each month the treasury looks at what falls due the next, nets last "
                    + "year's surplus from it, and issues the rest a month ahead as the same paper - the "
                    + "same instrument, term and currency, dollars abroad in dollars - at home instead while the window "
                    + "abroad is shut.";
            case TWELVE_MONTH_BILL -> "Each month the treasury looks at what falls due the next, nets last "
                    + "year's surplus from it, and issues the rest a month ahead as "
                    + Rollover.BILL_MONTHS + "-month notes at home.";
        }, Palette.TEXT_BODY));

        Rollover.Plan plan = game.rolloverPlan();
        if (!(plan.due() > 0)) {
            column.getChildren().add(statementLine("Next month", "nothing falls due", Palette.TEXT_MUTED));
        } else {
            column.getChildren().add(statementLine("Falls due next month", marked(here, money(plan.due()))
                    + (plan.dueAbroad() > 0
                            ? "  \u00b7  " + marked(here, money(plan.dueAbroad())) + " of it abroad" : "")));
            column.getChildren().add(statementLine("Last year's surplus", marked(here, money(plan.surplus())),
                    plan.surplus() < 0 ? Palette.WARN : null));
            if (plan.used() > 0) {
                column.getChildren().add(statementLine("...of it netted already this year",
                        marked(here, money(plan.used())), Palette.TEXT_MUTED));
            }
            if (mode != Rollover.Mode.MANUAL) {
                column.getChildren().add(statementLine("Netted from it", marked(here, money(plan.netted()))));
                StringBuilder as = new StringBuilder();
                for (int i = 0; i < plan.issues().size(); i++) {
                    Rollover.Issue issue = plan.issues().get(i);
                    column.getChildren().add(statementLine("Raised as " + issue.paper(),
                            marked(here, money(issue.cash())), Palette.GOOD));
                    column.getChildren().add(statementLine("...its face, as quoted today",
                            marked(here, money(issue.face())), issue.face() > issue.cash() ? Palette.WARN : null));
                    as.append(i == 0 ? "" : i == plan.issues().size() - 1 ? " and " : ", ")
                            .append(issue.paper()).append(" (").append(marked(here, money(issue.cash()))).append(")");
                }
                column.getChildren().add(sentence(plan.issues().isEmpty()
                        ? String.format("%s falls due, last year's surplus nets %s, and %s.",
                                marked(here, money(plan.due())), marked(here, money(plan.netted())),
                                plan.toRoll() > 0 ? marked(here, money(plan.toRoll()))
                                        + " is under the smallest deal worth arranging, so the cash pays it"
                                        : "nothing is issued")
                        : String.format("%s falls due, last year's surplus nets %s, %s will be raised as %s: "
                                        + "about %s of new face.",
                                marked(here, money(plan.due())), marked(here, money(plan.netted())),
                                marked(here, money(plan.toRaise())), as, marked(here, money(plan.issued()))),
                        Palette.TEXT_BODY));
            }
        }
        Rollover rolled = game.getRollover();
        if (rolled.getLastMonth() >= 0) {
            column.getChildren().add(statementNote(String.format("The last, in %s: %s fell due, %s was netted "
                    + "from the year's surplus, and %s of new paper raised %s.",
                    CityCalendar.format(rolled.getLastMonth()),
                    marked(here, money(rolled.getLastDue())), marked(here, money(rolled.getLastNetted())),
                    marked(here, money(rolled.getLastIssued())), marked(here, money(rolled.getLastRaised())))));
        }
        column.getChildren().add(statementNote("The new paper is sized so its cash covers what falls due. It sells "
                + "under its face - by its discount, a term loan's redemption premium, its costs - so its face is "
                + "more than the part of the maturity it refinances: rolling for cash borrows the old paper's interest into the new "
                + "principal, at every roll. The proceeds wait a month for the maturity - what pre-funding a "
                + "redemption costs. A treasury short of cash for its spending is still the central bank's to "
                + "advance."));
    }

    /* =====================================================================
       BORROW

       ONE PAGE. It was three - instrument, then duration, then amount - and the
       quote only existed on the third, so the two decisions that move the price
       were made blind and the price appeared after both were locked in.

       Every control re-strikes the quote, because the rate is a FUNCTION of the
       size of the ask: ask for twice as much and you are charged more than
       twice as much. That was true and invisible.

       AND THE LADDER IS DRAWN WITH THE NEW BOND IN IT, in green, on top of what
       the city already owes. A term loan's whole appeal is the small monthly
       payment; its whole danger is the year it all comes due, and that year is
       twenty or thirty bars to the right of the button you are about to press.
       ===================================================================== */

    void borrowPage(VBox column, boolean foreign) {

        DebtManager ledger = ui.game.getDebtManager();

        rolloverBlock(column);

        if (foreign && !ledger.foreignWindowOpen()) {
            column.getChildren().add(statementHead("The window abroad is shut"));
            column.getChildren().add(alert("Nobody will take this city's paper",
                    ledger.foreignWindowReason() + ". Everything already borrowed still "
                    + "falls due on schedule — a shut window stops new lending, not old "
                    + "obligations."));
            if (ledger.hasForeignDebt()) {
                column.getChildren().add(foreignDoor());
            }
            return;
        }

        Instrument kit = instrument(borrowType);
        borrowTerm = Math.max(kit.min(), Math.min(kit.max(), borrowTerm));
        // ...and onto the instrument's step: a term loan at 10, 20, 30, 40 or 50.
        borrowTerm = kit.min() + Math.round((borrowTerm - kit.min()) / (float) kit.step()) * kit.step();

        /* --------------------------- what to sell --------------------------- */
        column.getChildren().add(statementHead(foreign
                ? "Sell paper abroad, in " + Currency.FOREIGN_CODE
                : "Sell paper at home"));

        String[] names = new String[INSTRUMENTS.length];
        for (int i = 0; i < INSTRUMENTS.length; i++) names[i] = INSTRUMENTS[i].name();

        column.getChildren().add(chipStrip(names, kit.name(), Palette.SIZE_BODY, picked -> {
            for (Instrument i : INSTRUMENTS) {
                if (i.name().equals(picked)) {
                    borrowType = i.key();
                    borrowTerm = i.min();
                    borrowAsk = 0;
                }
            }
            showFinanceMenu();
        }));

        column.getChildren().add(sentence(kit.blurb(), Palette.TEXT_BODY));

        /* --------------------------- the curve (0.7.1) ---------------------------
         * One line per maturity this instrument offers, at the rate the city
         * would pay today - for the amount typed, once there is one - so the
         * player sees the long end sit above the note, and bend down when the
         * central bank holds term paper. At home only: the world lends at one
         * rate whatever the term.
         */
        if (!foreign) {
            column.getChildren().add(subHead(borrowAsk > 0
                    ? "The curve, for " + money(borrowAsk)
                    : "The curve today"));
            for (int d = kit.min(); d <= kit.max(); d += kit.step()) {
                int months = kit.key().equals("Note") ? d : d * 12;
                double rate = borrowAsk > 0
                        ? ui.game.quoteDebt(kit.key(), borrowAsk, d, kit.rounding()).marketRate()
                        : ledger.curveRate(months);
                double comp = ledger.compression(months);
                column.getChildren().add(statementLine(d + " " + kit.unit(),
                        pct2(rate) + (comp > 1e-6
                                ? String.format("   (%.2f points off for the central bank)", comp * 100)
                                : ""),
                        d == borrowTerm ? Palette.ACCENT : Palette.TEXT_BODY));
            }
            column.getChildren().add(statementNote(
                    "The note is the policy rate plus what the city's own debt adds. Longer money "
                    + "carries a term premium on top - half a point at ten years, a point and a "
                    + "half at fifty - less whatever the central bank's holdings of term paper "
                    + "take off it."));
        }

        /* --------------------------- for how long --------------------------- */
        column.getChildren().add(subHead("For how long, in " + kit.unit()));

        javafx.scene.layout.FlowPane terms = new javafx.scene.layout.FlowPane(6, 6);
        terms.setMaxWidth(STATEMENT);
        for (int d = kit.min(); d <= kit.max(); d += kit.step()) {
            final int chosen = d;
            Label chip = new Label(String.valueOf(d));
            boolean on = d == borrowTerm;
            chip.setStyle("-fx-padding: 3 9 3 9; -fx-cursor: hand;"
                    + " -fx-background-color: " + (on ? Palette.RAISED : Palette.CONTROL) + ";"
                    + " -fx-background-radius: " + Palette.RADIUS_TIGHT + ";"
                    + " -fx-border-color: " + (on ? Palette.ACCENT : "transparent") + ";"
                    + " -fx-border-width: 0 0 2 0;"
                    + Palette.words(Palette.SIZE_CAPTION,
                            on ? Palette.TEXT_HEAD : Palette.TEXT_MUTED));
            chip.setOnMouseClicked(e -> {
                borrowTerm = chosen;
                showFinanceMenu();
            });
            terms.getChildren().add(chip);
        }
        column.getChildren().add(terms);

        /* ---------------------------- how much ---------------------------- */
        double minimum = ui.game.minimumIssueSize();
        column.getChildren().add(subHead(foreign
                ? "How many " + Currency.FOREIGN_CODE + " to raise"
                : "How much to raise"));

        Label ask = new Label(borrowAsk <= 0 ? "nothing asked for yet"
                : (foreign ? usdFull(borrowAsk) : moneyFull(borrowAsk)));
        ask.setStyle(Palette.figure(Palette.SIZE_TITLE,
                borrowAsk > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT));
        column.getChildren().add(ask);

        // ONE, TWO, FIVE, TEN of the rounding unit. It was 1/5/25/100, which on a
        // term loan put a ten-billion-dollar button in front of a city holding
        // half a billion - a step nobody will ever press, taking the place of
        // one they would.
        double[] steps = {kit.rounding(), kit.rounding() * 2, kit.rounding() * 5,
                          kit.rounding() * 10};

        javafx.scene.layout.FlowPane up = new javafx.scene.layout.FlowPane(6, 6);
        up.setMaxWidth(STATEMENT);
        for (double step : steps) {
            up.getChildren().add(stepChip("+" + money(step), () -> {
                borrowAsk += step;
                showFinanceMenu();
            }, false));
        }
        up.getChildren().add(stepChip("the minimum", () -> {
            borrowAsk = Math.max(minimum, kit.rounding());
            showFinanceMenu();
        }, false));

        javafx.scene.layout.FlowPane down = new javafx.scene.layout.FlowPane(6, 6);
        down.setMaxWidth(STATEMENT);
        for (double step : steps) {
            down.getChildren().add(stepChip("−" + money(step), () -> {
                borrowAsk = Math.max(0, borrowAsk - step);
                showFinanceMenu();
            }, false));
        }
        down.getChildren().add(stepChip("clear", () -> {
            borrowAsk = 0;
            showFinanceMenu();
        }, true));

        column.getChildren().addAll(up, down);

        column.getChildren().add(statementNote(String.format(
                "Issues round to %s, and the market will not arrange anything under %s for "
                + "a city this size — the legal and rating work costs the same however "
                + "little you raise.", money(kit.rounding()), money(minimum))));

        /* ----------------------------- the quote ----------------------------- */
        if (borrowAsk <= 0) {
            column.getChildren().add(sentence(
                    "Ask for something and the quote appears here, with the ladder it "
                    + "would build. Nothing is committed until you press the button at "
                    + "the bottom.", Palette.TEXT_MUTED));
            return;
        }

        DebtQuote quote = foreign
                ? ui.game.quoteForeign(kit.key(), borrowAsk, borrowTerm, kit.rounding())
                : ui.game.quoteDebt(kit.key(), borrowAsk, borrowTerm, kit.rounding());

        column.getChildren().add(statementHead("What that would cost"));

        column.getChildren().add(statementLine("You asked for",
                moneyFull(borrowAsk), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("You would receive",
                moneyFull(quote.cashReceived()), Palette.GOOD));
        column.getChildren().add(statementLine("You would owe",
                moneyFull(quote.faceValue()), Palette.WARN));
        column.getChildren().add(statementLine("Issued at",
                String.format("%.2f of par", quote.pricePerPar())));
        column.getChildren().add(statementLine("Coupon",
                quote.monthlyInterest() > 0
                        ? moneyFull(quote.monthlyInterest()) + " a month"
                        : "none — the discount is the lender's return"));
        column.getChildren().add(statementLine("Rate",
                String.format("%.2f%%", quote.marketRate() * 100), Palette.ACCENT));
        column.getChildren().add(statementTotal("Cost of the credit",
                moneyFull(quote.totalCost()), Palette.WARN));

        /*
         * WHY THE PROCEEDS ARE NOT THE ASK.
         *
         * Paper is issued in round lots and the face is grossed up for the
         * discount, so the cash that actually arrives lands on whichever side
         * of the request the rounding puts it. Left unsaid it reads as an
         * arithmetic error on the game's part.
         */
        if (Math.abs(quote.cashReceived() - borrowAsk) > borrowAsk * .005) {
            column.getChildren().add(statementNote(String.format(
                    "That is %s %s than you asked for. Paper is sold in lots of %s and the "
                    + "face is grossed up for the discount, so the proceeds land on "
                    + "whichever side of the request the rounding puts them.",
                    money(Math.abs(quote.cashReceived() - borrowAsk)),
                    quote.cashReceived() > borrowAsk ? "more" : "less",
                    money(instrument(borrowType).rounding()))));
        }

        double moved = (quote.marketRate() - quote.rateBefore()) * 100;
        if (moved >= .005) {
            column.getChildren().add(sentence(String.format(
                    "Asking for this much moves the city's own rate from %.2f%% to %.2f%% "
                    + "— %.2f points, charged on this bond and on the next one. The market "
                    + "prices a loan with the loan already on the books.",
                    quote.rateBefore() * 100, quote.marketRate() * 100, moved),
                    Palette.WARN));
        }

        /* ------------------- the ladder, with this bond in it ------------------- */
        column.getChildren().add(statementHead("The ladder this would build"));
        column.getChildren().add(ladderChart(ledger.getDebt(),
                proposedSchedule(kit.key(), quote, borrowTerm, foreign),
                "This issue"));
        column.getChildren().add(statementNote(
                "Green is what you are about to add. Everything else is already owed."));

        /* --------------------------- who is buying it --------------------------- */
        column.getChildren().add(bankAppetite());

        /* ----------------------- and where the dollars go ----------------------- */
        if (foreign) {
            column.getChildren().add(statementHead("And what to do with the dollars"));
            column.getChildren().add(chipStrip(
                    new String[] {"Convert and spend it", "Hold it as reserves"},
                    borrowHold ? "Hold it as reserves" : "Convert and spend it",
                    Palette.SIZE_CAPTION, pick -> {
                        borrowHold = pick.startsWith("Hold");
                        showFinanceMenu();
                    }));
            column.getChildren().add(statementNote(
                    "Spending it leaves a dollar debt with nothing behind it, and the next "
                    + "devaluation makes that debt bigger for free. Holding it leaves the "
                    + "net position unchanged and the next bond cheaper — and buys nothing "
                    + "today. Every finance ministry in the literature has made this trade."));
        }

        /* ------------------------------ the button ------------------------------ */
        Button confirm = new Button(foreign
                ? "Issue " + Currency.FOREIGN_CODE + " " + kit.name().toLowerCase()
                        + " for " + money(quote.cashReceived())
                : "Issue for " + money(quote.cashReceived()));
        confirm.setStyle("-fx-background-color: #2f7d52; -fx-text-fill: white;"
                + " -fx-padding: 8 18 8 18;");
        confirm.setOnAction(e -> {
            String summary = foreign
                    ? ui.game.handleForeignLogic(kit.key(), borrowAsk, borrowTerm,
                            kit.rounding(), borrowHold)
                    : executeDebtLogic(kit.key(), borrowAsk, borrowTerm, kit.rounding());
            borrowAsk = 0;
            showDebtResultMenu(summary);
        });

        HBox act = new HBox(confirm);
        act.setAlignment(Pos.CENTER_LEFT);
        act.setStyle("-fx-padding: 12 0 4 0;");
        column.getChildren().add(act);

        if (foreign && ledger.hasForeignDebt()) {
            column.getChildren().add(foreignDoor());
        }
    }

    /**
     * The payment schedule the proposed bond would add, month by month.
     *
     * Built from the quote rather than from a real instrument, because the
     * instrument does not exist yet and constructing one to draw a picture is
     * how a reporting layer ends up changing the thing it reports.
     */
    double[] proposedSchedule(String type, DebtQuote quote,
                                      int term, boolean foreign) {

        int months = "Note".equals(type) ? term : term * 12;
        if (months <= 0) return new double[0];

        double[] flows = new double[months];
        double face = quote.faceValue();
        double coupon = quote.monthlyInterest();

        switch (type) {
            case "Note" -> flows[months - 1] = face;

            case "Serial" -> {
                // A slice of principal on each anniversary, and a coupon on
                // the declining balance - which is why the coupon here falls
                // rather than sitting flat.
                int slices = Math.max(1, term);
                double perSlice = face / slices;
                double outstanding = face;
                double monthlyRate = quote.marketRate() / 12;
                for (int m = 0; m < months; m++) {
                    flows[m] = outstanding * monthlyRate;
                    if ((m + 1) % 12 == 0) {
                        double due = Math.min(perSlice, outstanding);
                        flows[m] += due;
                        outstanding -= due;
                    }
                }
            }

            default -> {
                java.util.Arrays.fill(flows, coupon);
                flows[months - 1] += face;
            }
        }

        if (foreign) {
            double rate = ui.game.getDebtManager().getExchangeRate();
            for (int m = 0; m < months; m++) flows[m] *= rate;
        }
        return flows;
    }

    /**
     * WHO IS BUYING THIS, AND WHAT IT DOES TO EVERYONE ELSE.
     *
     * The bank buys the city's paper, so a bond programme is not only a cost to
     * the treasury - it takes room on the bank's book that the city's own
     * businesses were going to borrow. Until 0.7.7, past the point where the
     * bank was comfortable, every borrower in the city paid a premium the
     * treasury caused; since then a loan is priced by what it costs the bank
     * (Bank, WHAT A LOAN COSTS), and a full book shows as strain on the Bank
     * tab and a branch wanted, not as a dearer rate.
     */
    VBox bankAppetite() {

        Bank bank = ui.game.getBank();
        double room = bank.capacity();

        if (bank.isInsolvent()) {
            return alert("The bank has failed and cannot buy this",
                    "Anything issued now is funded at the central bank's window. "
                    + "Recapitalise it first — the Bank tab has the figure.");
        }
        if (room <= 0) {
            return alert("There is no bank to buy this",
                    "The households take what share of it pays them better than a deposit "
                    + "would, and the rest is funded at the central bank's window, the "
                    + "money every borrower in a city without a branch is already lent. "
                    + "One branch changes that.");
        }
        if (bank.strain() > Bank.EASY_STRAIN) {
            return alert("The bank is past comfortable already",
                    String.format("It is %.0f%% lent out. Treasury paper is risk-weighted at "
                    + "%.0f%%, so it ties up less room than a business loan of the same size "
                    + "— but it is still room the shops and the mills were going to use.",
                    bank.getWeightedBook() / room * 100, Bank.RISK_CITY * 100));
        }

        VBox box = new VBox(0);
        box.getChildren().add(statementHead("Who buys this"));
        box.getChildren().add(sentence(String.format(
                "The households first, when it pays them more than the bank does: up to "
                + "%.0f%% of an issue, out of what they have saved past a cushion, at the "
                + "settle next month. Then your own bank, for the rest. It is %.0f%% lent "
                + "out, and treasury paper is risk-weighted at %.0f%%, so it ties up far less "
                + "of the bank's room than a business loan of the same size. Borrow enough "
                + "and that changes — for every borrower in the city, not only for the "
                + "treasury.",
                HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE * 100,
                bank.getWeightedBook() / room * 100, Bank.RISK_CITY * 100),
                Palette.TEXT_BODY));
        box.setMaxWidth(STATEMENT);
        return box;
    }

    /** The door marked do not open. Only where there is something to walk away from. */
    VBox foreignDoor() {
        Button repudiate = new Button("Default on the foreign debt");
        repudiate.setStyle("-fx-background-color: #7d2f2f; -fx-text-fill: white;");
        repudiate.setOnAction(e -> showForeignDefaultMenu());
        HBox row = new HBox(repudiate);
        row.setAlignment(Pos.CENTER_LEFT);
        VBox box = new VBox(row);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 18 0 4 0;");
        return box;
    }

    /**
     * Asking twice, with the bill written out.
     *
     * The gain is immediate and enormous and the cost is five years away, which
     * is precisely the shape of decision a confirmation screen exists for. Both
     * halves are on it in the same size type.
     */
    void showForeignDefaultMenu() {
        ui.clearMenu("showForeignDefaultMenu", () -> showForeignDefaultMenu());

        DebtManager market = ui.game.getDebtManager();

        Label title = new Label("DEFAULT ON THE FOREIGN DEBT");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;"
                + " -fx-text-fill: #ff8a7a;");

        VBox column = new VBox(2);
        column.getChildren().addAll(
                bookLine("  written off", toDollars(market.getForeignPrincipal()), true, "#9be89b"),
                bookNote(String.format("%s of paper, at %.4f",
                        usd(market.getForeignPrincipalUsd()),
                        market.getExchangeRate())),
                bookLine("  a year of payments, gone",
                        toDollars(market.nextYearService()), false, "#9be89b"),
                bookRule());

        Label cost = monoLabel("  AND WHAT IT COSTS");
        cost.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;"
                + " -fx-font-weight: bold; -fx-text-fill: #ff8a7a;");
        column.getChildren().add(cost);
        column.getChildren().addAll(
                bookNote("no lender abroad will take this city's paper for five years"),
                bookNote(String.format("and %.0f points on its rate when they will again,",
                        DebtManager.DEFAULT_SCAR * 100)),
                bookNote("fading over about five years after that"),
                bookNote(""),
                bookNote("every ratio on the trade screen will look better tomorrow."),
                bookNote("None of them is what stops the next bond being sold."));

        Button confirm = new Button("Default. I understand nobody will lend abroad for five years.");
        confirm.setStyle("-fx-background-color: #7d2f2f; -fx-text-fill: white;");
        confirm.setOnAction(e -> {
            ui.game.defaultOnForeignDebt("the city chose to");
            showFinanceMenu();
        });

        Button back = new Button("Keep paying");
        back.setOnAction(e -> showFinanceMenu());

        ui.rootMenu.getChildren().addAll(title, ui.scrolled(column), back, confirm);
    }

    /* =====================================================================
       MONEY (0.7.0)

       Jerus: "the feds sheet would show how much debt it holds, like debt to
       itself aka money printing ... and that is the M2 supply or what do you
       think?" This is that sheet, in the player's words: what the central
       bank holds and owes, what it made and destroyed this month, what it
       has printed for the treasury and what its profit paid back, and the two
       money figures with a year of each behind them. Every figure is a
       getter on CentralBank, Game or Bank; nothing is worked out here.
       ===================================================================== */

    void moneyPage(VBox column) {
        CentralBank cb = ui.game.getCentralBank();
        Bank bank = ui.game.getBank();
        double policy = ui.game.getDebtManager().getPolicyRate();

        /* ---------------------------- in two sentences ---------------------------- */
        column.getChildren().add(statementHead("In two sentences"));
        column.getChildren().add(sentence(String.format(
                "The central bank has made %s and not taken it back: %s lent to the bank at "
                + "its window, %s advanced to the treasury, and %s paid out on reserves beyond "
                + "what it has earned%s.",
                moneyFull(cb.m0()), moneyFull(cb.getAdvancesToBank()),
                moneyFull(cb.getAdvancesToTreasury()), moneyFull(cb.getLossCarried()),
                cb.getRemittanceDue() > 0
                        ? String.format(", less the %s it owes the treasury at the next press",
                                moneyFull(cb.getRemittanceDue()))
                        : ""),
                Palette.TEXT_BODY));
        /*
         * ...AND THE CITY'S PAPER IT HOLDS (0.7.1), in the design's own words:
         * the holdings dial on the Policy tab, and what it bends.
         */
        if (cb.getPaperHeld() > 0 || cb.getTargetShare() > 0) {
            DebtManager ledger = ui.game.getDebtManager();
            column.getChildren().add(sentence(String.format(
                    "It holds %s of the city's own paper, bought with money it made; the "
                    + "30-year rate is %.2f points lower for it. Its dial aims at %.0f%% of the "
                    + "city's term paper and it holds %.0f%%.",
                    moneyFull(cb.getPaperHeld()), ledger.compression(360) * 100,
                    cb.getTargetShare() * 100, ledger.centralBankShareOfTerm() * 100),
                    Palette.TEXT_BODY));
        }
        column.getChildren().add(sentence(String.format(
                "%s is chasing goods - everything the public holds at the bank - against a "
                + "month's output of %s.",
                moneyFull(ui.game.getM2()),
                moneyFull(ui.game.getEconomyManager().getMonthGdp())),
                Palette.TEXT_BODY));

        /* ---------------------------- the balance sheet ---------------------------- */
        column.getChildren().add(statementHead("What the central bank holds"));
        column.getChildren().add(statementLine("Lent to the bank at the window",
                moneyFull(cb.getAdvancesToBank())));
        column.getChildren().add(statementLine("Advanced to the treasury - printed",
                moneyFull(cb.getAdvancesToTreasury()),
                cb.getAdvancesToTreasury() > 0 ? Palette.WARN : Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("The city's paper it holds",
                moneyFull(cb.getPaperHeld())));
        column.getChildren().add(statementLine("The vault, at today's rate",
                moneyFull(cb.getVault())));
        column.getChildren().add(statementTotal("TOTAL ASSETS",
                moneyFull(cb.totalAssets()), Palette.TEXT_HEAD));

        column.getChildren().add(statementLine("Reserves - the money it has made",
                "−" + moneyFull(cb.getReserves()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("Currency in circulation",
                "−" + moneyFull(cb.getCurrency()), Palette.TEXT_MUTED));
        column.getChildren().add(statementTotal("TOTAL LIABILITIES - M0",
                moneyFull(cb.m0()), Palette.TEXT_HEAD));
        column.getChildren().add(statementTotal("EQUITY", moneyFull(cb.equity()),
                cb.equity() >= 0 ? Palette.GOOD : Palette.BAD));
        /*
         * ...AND WHAT THE DEFENCE SPENT OF IT (0.7.2). Selling the vault's
         * dollars to hold the currency up is capital spent against the world:
         * the vault above is smaller by them and equity with it, and M0 does
         * not move (CentralBank, THE DEFENCE). This line says why equity is
         * lower; it is not a second deduction.
         */
        column.getChildren().add(statementLine("...spent defending the currency since founding",
                "−" + moneyFull(cb.vaultSpent()), Palette.TEXT_MUTED));
        column.getChildren().add(statementNote(String.format(
                "Its liabilities are every dollar it has made and not taken back, which is "
                + "what M0 is. Nobody here holds cash outside the bank, so all of it is "
                + "reserves. The commercial bank's own spare cash - %s - is what it is paid "
                + "the policy rate on, %s a year. The vault is the city's dollars, which a "
                + "central bank holds; the treasury still buys and sells them, and since "
                + "0.7.2 the central bank sells them when the currency is pushed down - "
                + "capital spent, which takes equity down with the vault and leaves M0 "
                + "where it was.",
                moneyFull(bank.cashReserves()), pct2(policy))));
        if (cb.getLossCarried() > 0) {
            column.getChildren().add(statementNote(String.format(
                    "It is carrying a loss of %s: what it has paid on reserves beyond what its "
                    + "loans have earned. Nothing is remitted to the treasury until that is "
                    + "made good out of profit.", moneyFull(cb.getLossCarried()))));
        }

        /* ---------------------------- this month ---------------------------- */
        column.getChildren().add(statementHead("What it did this month"));
        column.getChildren().add(statementLine("Money made",
                moneyFull(cb.getIssued()), Palette.ACCENT));
        column.getChildren().add(statementLine("...of which interest on reserves",
                moneyFull(cb.getInterestOnReserves()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("...lent at the window",
                moneyFull(cb.getAdvancedToBank()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("...printed for the treasury",
                moneyFull(cb.getAdvancedToTreasury()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("...remitted to the treasury",
                moneyFull(cb.getRemitted()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("...paid for the city's paper it bought",
                moneyFull(cb.getBoughtPaper()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("Money destroyed",
                "−" + moneyFull(cb.getRetired()), Palette.WARN));
        column.getChildren().add(statementLine("...of which paid it for paper it sold",
                "−" + moneyFull(cb.getSoldPaper()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("...and the coupons and principal on its paper",
                "−" + moneyFull(cb.getPaperCoupons() + cb.getPaperRedeemed() + cb.getBoughtBack()),
                Palette.TEXT_MUTED));
        column.getChildren().add(statementTotal("M0 moved by",
                (cb.getIssued() >= cb.getRetired() ? "+" : "−")
                        + moneyFull(Math.abs(cb.getIssued() - cb.getRetired())),
                Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("Printed for the treasury since founding",
                moneyFull(cb.getPrintedLifetime()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("Remitted to the treasury since founding",
                moneyFull(cb.getRemittedLifetime()), Palette.TEXT_MUTED));

        /* ---------------------------- M0 and M2 ---------------------------- */
        column.getChildren().add(statementHead("M0 and M2"));
        column.getChildren().add(statementLine("M0 - what the central bank has made",
                moneyFull(cb.m0()), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("M2 - what the public holds",
                moneyFull(ui.game.getM2()), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("...the households' deposits",
                moneyFull(ui.game.getHouseholdDeposits()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("...the businesses' deposits",
                moneyFull(ui.game.getSectorDeposits()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("...money from abroad on deposit",
                moneyFull(bank.getForeignDeposits()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("...currency",
                moneyFull(cb.getCurrency()), Palette.TEXT_MUTED));
        column.getChildren().add(statementNote(
                "M2 is the bank's deposits - the households', the businesses' and the "
                + "world's - plus currency, which nobody here holds. It grows when the bank "
                + "lends on into somebody's account or the treasury spends money that was "
                + "printed for it; it does not grow when the central bank lends to the bank."));

        HistorySave h = ui.game.getHistorySave();
        if (h.months() >= 2) {
            column.getChildren().add(statementHead("The last year"));
            column.getChildren().add(trendChart(
                    new String[] {"M2", "M0"},
                    new double[][] {lastYear(h.aligned("m2")), lastYear(h.aligned("m0"))},
                    new String[] {Palette.ACCENT, Palette.WARN}));
            column.getChildren().add(trendChart(
                    new String[] {"Advanced to the treasury"},
                    new double[][] {lastYear(h.aligned("advancesToTreasury"))},
                    new String[] {Palette.BAD}));
        }
    }

    /** The last twelve months of a series, or all of it if the city is younger. */
    static double[] lastYear(double[] series) {
        int from = Math.max(0, series.length - 12);
        return java.util.Arrays.copyOfRange(series, from, series.length);
    }

    /* =====================================================================
       THE BOND MARKET (0.7.12)

       The businesses' bonds, not the city's: every issue the sectors have
       sold, what each pays and is worth, who holds it, and the month on
       the order books. On the Finances tab because it is a market beside
       the city's own paper, and the book of that paper already lives here;
       a sector's own bonds are on its Cash & debt page, the bank's on its
       Lending page, the world's on the Trade tab. Every figure is the
       market's own getter (BondMarket).
       ===================================================================== */

    /** What every business owes, bank loans and bonds together. */
    double businessDebt() {
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        double owed = 0;
        for (String k : credit.sectors()) owed += credit.getPrincipal(k);
        return owed;
    }

    /** A bond's price, per 100 of face. */
    static String per100(double price) {
        return Double.isFinite(price) && price > 0 ? String.format("%.2f", price * 100) : "—";
    }

    void bondMarketPage(VBox column) {

        BondMarket market = ui.game.getBondMarket();
        int month = ui.game.getMonth();
        List<CorporateBond> bonds = new ArrayList<>(market.getBonds());
        bonds.sort(java.util.Comparator.comparingInt(CorporateBond::maturityMonth));

        column.getChildren().add(statementHead("Every bond the businesses have sold"));
        if (bonds.isEmpty()) {
            column.getChildren().add(sentence("None outstanding. A business sells a bond when the book would "
                    + "take it for no more than the bank's loan costs, its issuing costs spread over its ten "
                    + "years; a small amount goes to the bank, because the fixed part of those costs makes "
                    + "a small bond dear.", Palette.TEXT_MUTED));
        } else {
            javafx.scene.layout.GridPane t = grid(new double[] {130, 96, 64, 84, 70, 70}, rightAfterFirst(6));
            gridHead(t, "", "owed", "coupon", "matures", "price", "yield");
            int line = 1;
            for (CorporateBond b : bonds) {
                double last = market.lastPrice(b);
                t.add(gridCell(b.issuer(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
                t.add(gridCell(money(b.face()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, line);
                t.add(gridCell(pct2(b.coupon()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 2, line);
                t.add(gridCell(CityCalendar.formatShort(b.maturityMonth()), Palette.TEXT_MUTED,
                        Palette.SIZE_CAPTION, true), 3, line);
                t.add(gridCell(Double.isNaN(last) ? per100(market.modelPrice(b, month)) + "*" : per100(last),
                        Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 4, line);
                double y = market.lastYield(b, month);
                t.add(gridCell(pct2(y), y > b.coupon() + .005 ? Palette.WARN : Palette.TEXT_BODY,
                        Palette.SIZE_CAPTION, true), 5, line);
                line++;
            }
            column.getChildren().add(t);
            column.getChildren().add(statementNote("The price is per 100 of face, at the last trade on its book; "
                    + "* where it has not traded yet, what it is worth at the city's curve for the months it "
                    + "has left and what a holder expects to lose on its issuer a year. The yield is the one "
                    + "that price gives."));

            javafx.scene.layout.GridPane h = grid(new double[] {130, 100, 90, 100, 100}, rightAfterFirst(5));
            gridHead(h, "held by", "households", "the bank", "companies", "the world");
            line = 1;
            for (CorporateBond b : bonds) {
                h.add(gridCell(b.issuer(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
                h.add(gridCell(money(b.households()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 1, line);
                h.add(gridCell(money(b.bank()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 2, line);
                h.add(gridCell(money(b.companiesTotal()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 3, line);
                h.add(gridCell(money(b.world()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 4, line);
                line++;
            }
            column.getChildren().add(subHead("...and who holds each"));
            column.getChildren().add(h);
        }

        double face = market.totalFace();
        column.getChildren().add(statementTotal("Outstanding altogether", moneyFull(face), Palette.TEXT_HEAD));
        double business = businessDebt();
        if (business > 0) {
            column.getChildren().add(statementLine("...of everything the businesses owe",
                    String.format("%.1f%%", face / business * 100)));
        }
        column.getChildren().add(statementLine("Their coupons, weighted by face", pct2(market.averageCoupon())));
        if (face > 0) {
            double[] held = { market.faceHeldByHouseholds(), market.faceHeldByBank(),
                              market.faceHeldByCompanies(), market.faceHeldByWorld() };
            String[] who = { "The households", "The bank", "The companies with cash to spare", "Abroad" };
            column.getChildren().add(statementHead("Who holds them"));
            for (int i = 0; i < who.length; i++) {
                column.getChildren().add(statementLine(who[i], money(held[i])
                        + String.format("   %.0f%%", held[i] / face * 100),
                        held[i] > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT));
            }
        }

        /* ------------------------------ the month ------------------------------ */
        column.getChildren().add(statementHead("This month"));
        column.getChildren().add(statementLine(String.format("Sold, %d issue%s", market.getIssues(),
                market.getIssues() == 1 ? "" : "s"), moneyFull(market.getIssuedFace())));
        if (market.getIssuedCosts() > 0) {
            column.getChildren().add(statementLine("...its costs, paid to the bank as underwriter",
                    moneyFull(market.getIssuedCosts()), Palette.TEXT_MUTED));
        }
        double coupons = market.getCouponsToHouseholds() + market.getCouponsToBank()
                + market.getCouponsToCompanies() + market.getCouponsAbroad();
        column.getChildren().add(statementLine("Coupons paid", moneyFull(coupons)));
        double principal = market.getPrincipalToHouseholds() + market.getPrincipalToBank()
                + market.getPrincipalToCompanies() + market.getPrincipalAbroad();
        if (principal > 0) column.getChildren().add(statementLine("Repaid at maturity", moneyFull(principal)));
        double lost = market.getLossHouseholds() + market.getLossBank() + market.getLossCompanies()
                + market.getWorldWrittenOff();
        if (lost > 0) {
            column.getChildren().add(statementLine("Written off in defaults", moneyFull(lost), Palette.BAD));
            column.getChildren().add(statementNote(String.format(
                    "The households lost %s of it, the bank %s, the companies %s and the world %s. A defaulted "
                    + "bank loan gets back more of what it is owed than a defaulted bond does.",
                    money(market.getLossHouseholds()), money(market.getLossBank()),
                    money(market.getLossCompanies()), money(market.getWorldWrittenOff()))));
        }
        if (market.getLastIssuer() != null) {
            column.getChildren().add(statementNote(String.format(
                    "The last issue: %s sold %s of %d-year bonds at %.2f%%, against the bank's %.2f%%, in %s.",
                    market.getLastIssuer(), money(market.getLastIssueFace()), CorporateBond.TERM_MONTHS / 12,
                    market.getLastIssueCoupon() * 100, market.getLastIssueLoanRate() * 100,
                    CityCalendar.format(market.getLastIssueMonth()))));
        }

        /* ---------------------------- the order books ---------------------------- */
        column.getChildren().add(statementHead("On the order books, last month"));
        column.getChildren().add(statementLine("Offered for sale", moneyFull(market.getLastPostedSell())));
        column.getChildren().add(statementLine("...of it sold", market.getLastPostedSell() > 0
                ? String.format("%s   %.0f%%", money(market.getLastFilled()),
                        market.getLastFilled() / market.getLastPostedSell() * 100) : "—"));
        column.getChildren().add(statementLine("Sellers who waited", market.getLastSellsPosted() > 0
                ? String.format("%d of %d", market.getLastSellsWaited(), market.getLastSellsPosted()) : "none"));
        column.getChildren().add(statementNote(
                "Everybody posts buy and sell orders at prices, and an order fills only when it meets one on "
                + "the other side, at the price of the one that was there first; the rest wait, and nobody "
                + "has to trade. The households buy by the rule they buy the city's paper by, the companies "
                + "with idle cash and the world by the rules that send money where the return is, and the "
                + "bank only at the yield an equal loan would earn it. Orders are good for a month: each is "
                + "posted again from that month's rates. A household short of money sells into what rests "
                + "there when that is cheaper than borrowing, and waits when it is not."));
    }

    /** The bond whose book is open, by its number; the largest one when it has gone. */
    int bookBondId = -1;

    void bondBookPage(VBox column) {

        BondMarket market = ui.game.getBondMarket();
        int month = ui.game.getMonth();
        List<CorporateBond> bonds = new ArrayList<>(market.getBonds());
        if (bonds.isEmpty()) {
            column.getChildren().add(sentence("No bond outstanding, so no book.", Palette.TEXT_MUTED));
            return;
        }
        bonds.sort((a, b) -> Double.compare(b.face(), a.face()));
        CorporateBond open = market.bond(bookBondId);
        if (open == null) open = bonds.get(0);

        // The twelve largest as chips, and the open one among them.
        java.util.Map<String, Integer> byName = new java.util.LinkedHashMap<>();
        for (CorporateBond b : bonds) {
            if (byName.size() >= 12 && b != open) continue;
            byName.put(b.issuer() + " " + b.id(), b.id());
        }
        String current = open.issuer() + " " + open.id();
        javafx.scene.layout.FlowPane chips = chipStrip(byName.keySet().toArray(new String[0]), current,
                Palette.SIZE_CAPTION, name -> {
                    bookBondId = byName.get(name);
                    showFinanceMenu();
                });
        chips.setStyle("-fx-padding: 0 0 8 0;");
        column.getChildren().add(chips);

        CorporateBond b = open;
        OrderBook book = market.bookOf(b);
        column.getChildren().add(statementHead(b.issuer() + "'s bond " + b.id()));
        column.getChildren().add(statementLine("Owed", moneyFull(b.face())));
        column.getChildren().add(statementLine("Coupon", pct2(b.coupon()) + " a year, paid monthly"));
        column.getChildren().add(statementLine("Sold", CityCalendar.format(b.issueMonth())));
        column.getChildren().add(statementLine("Matures", CityCalendar.format(b.maturityMonth())
                + " (" + CityCalendar.until(month, b.maturityMonth()) + ")"));
        column.getChildren().add(statementLine("Worth, at the curve and its issuer's risk",
                per100(market.modelPrice(b, month)) + " per 100, " + pct2(market.modelYield(b, month))));
        double last = market.lastPrice(b);
        column.getChildren().add(statementLine("Last traded", Double.isNaN(last) ? "not yet"
                : per100(last) + " per 100, in " + CityCalendar.format(book.lastTradeMonth())));
        column.getChildren().add(statementNote(String.format(
                "Its issuer's firms default at %s a year at its leverage, and a holder of its bonds loses "
                + "%.0f%% of what defaults, where the bank's loans lose %.0f%% - what bonds and loans have "
                + "given back on average, 1987-2024.",
                BankScreen.defaultShare(market.defaultRate(b.issuer(), 0, 0)),
                BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT * 100, BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT * 100)));

        for (OrderBook.Side side : OrderBook.Side.values()) {
            boolean bids = side == OrderBook.Side.BUY;
            List<OrderBook.Level> levels = book.levels(side);
            column.getChildren().add(subHead(bids ? "Bids, best first" : "Asks, best first"));
            if (levels.isEmpty()) {
                column.getChildren().add(sentence(bids ? "Nobody is bidding." : "Nobody is selling.",
                        Palette.TEXT_MUTED));
                continue;
            }
            javafx.scene.layout.GridPane t = grid(new double[] {110, 90, 120, 80}, rightAfterFirst(4));
            gridHead(t, "price per 100", "its yield", "face", "orders");
            int line = 1;
            for (OrderBook.Level level : levels) {
                if (line > 12) break;
                t.add(gridCell(per100(level.price()), bids ? Palette.GOOD : Palette.WARN, Palette.SIZE_CAPTION, false), 0, line);
                t.add(gridCell(pct2(CorporateBond.yieldAtPrice(b.coupon(), b.remainingMonths(month), level.price())),
                        Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 1, line);
                t.add(gridCell(money(level.quantity()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, line);
                t.add(gridCell(String.valueOf(level.orders()), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 3, line);
                line++;
            }
            column.getChildren().add(t);
        }
        column.getChildren().add(statementNote(
                "What rests on the book after the month's step: a bid under every ask, since whatever could "
                + "meet has already traded. The orders are withdrawn at next month's step and posted again."));
    }

    /* =====================================================================
       THE DEBT RESULT

       Issuing the paper a screen asked for, and the page that says what was
       raised. The borrow page comes through here; the two offers the build
       screen makes when the treasury is short do not - each books its own
       quote on Game and places the order again (BuildScreen's
       buildOnTheLoan()). Its own section since 2026-09-18, on the way out of
       the stat card and into the finances screen.
       ===================================================================== */

    String executeDebtLogic(String type, double amount, int duration, double rounding) {
        return switch (type) {
            case "Note" -> ui.game.handleTBillLogic(amount, duration, rounding);
            case "Serial" -> ui.game.handleMediumBondLogic(amount, duration, rounding);
            case "Term" -> ui.game.handleLongBondLogic(amount, duration, rounding);
            default -> "Unknown instrument.";
        };
    }

    /** Shows the terms the player just agreed to. */
    void showDebtResultMenu(String summary) {
        ui.clearMenu("showDebtResultMenu", () -> showDebtResultMenu(summary));

        Label title = new Label("ISSUANCE CONFIRMED");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10;");

        Label terms = new Label(summary);
        terms.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        Label rate = new Label(String.format("New market rate: %.2f%%",
                ui.game.getInterestRate() * 100));
        rate.setStyle("-fx-text-fill: #8fa3b0; -fx-padding: 10 0 0 0;");

        Button back = new Button("Back to Finance");
        back.setOnAction(e -> showFinanceMenu());

        ui.rootMenu.getChildren().addAll(title, terms, rate, back);
    }
}
