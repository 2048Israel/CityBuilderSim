package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import ham.citybuildersim.ui.Pieces.Slice;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * The bank tab: whether the city's bank is healthy and why, on one landing -
 * a sentence, a scorecard and the ladder of its rates - with its profit, its
 * lending, its funding, its capital and owners, and its history behind it.
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
 * screen, several of them wrong (the project's design note for 0.7.9 has
 * the list). Every figure is a model getter now - Bank's WHAT THE BANK TAB
 * READS has the ones this tab asked for.
 *
 * ONE WAY TO WRITE EACH KIND OF NUMBER: a rate as "x.xx% a year" (rate()),
 * a spread between two rates in points to two decimals, as the rates are
 * (points()), a share or a ratio as "x.x%" (share()), and money through
 * Money - a flow says "this month", a stock does not.
 *
 * The shell reads which page is open (bankArea, bankPage) for the rail and
 * the scroll memory; the panel is rebuilt on the clock, so the page, the
 * scroll position (UserInterface.scrolled()) and the lines the player has
 * opened (openLines) all survive a redraw.
 */
final class BankScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    BankScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       THE BANK AT A GLANCE

       One screen that answers "is my bank healthy, and why" without a click:
       the bank's state in a sentence with the figure that decides it; a
       scorecard of the eight figures a banker would read first; and the
       ladder of its rates, from the price of money to what each borrower
       pays, every step labelled with what it is for. Then the five pages
       behind it, each with its headline figure.

       THE ACTION STAYS AS IT WAS. Jerus: rescue on failure only. Putting
       capital in whenever you like is a lever the city does not have, and
       adding one is a game change rather than a screen change.
       ===================================================================== */

    String bankArea = null;                  // null is the landing; BANK_PAGES, the pages behind it
    static final String BANK_PAGES = "The bank's pages";
    static final String BANK_HOME  = "Profit";
    String bankPage = BANK_HOME;

    static final String[] BANK_PAGE_NAMES =
            {"Profit", "Lending", "Funding", "Capital & owners", "History"};

    /** The lines the player has opened, by label, so a redraw on the clock leaves them open (Statement.opens()). */
    final Set<String> openLines = new HashSet<>();

    /* ------------------------ one way to write each number ------------------------ */

    /** A rate: "x.xx% a year". */
    static String rate(double r) { return String.format("%.2f%% a year", r * 100); }

    /** A spread between two rates, signed, to the rates' own two decimals - a quarter point must not print as 0.3. */
    static String points(double p) {
        return String.format("%s%.2f points", p < -5e-7 ? "−" : "+", Math.abs(p * 100));
    }

    /** A share or a ratio that is not a yearly rate: "x.x%". */
    static String share(double s) { return String.format("%.1f%%", s * 100); }

    /** "the last 12 months", or as many as the bank has lived. */
    static String yearWords(Bank bank) {
        int n = bank.monthsInYear();
        return n <= 1 ? "this month" : "the last " + n + " months";
    }

    /**
     * The tab's entry point: the landing, or the page behind it the player
     * was on. Named for the shell, which calls it from the rail, the inbox
     * and the summary panel.
     */
    void showBankMenu() {
        ui.clearMenu("showBankMenu", () -> showBankMenu());

        if (bankArea != null) {
            drawBankScreen();
            return;
        }

        Bank bank = ui.game.getBank();

        Label title = new Label("THE COMMERCIAL BANK");
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        Label lead = new Label("Every loan in the city is its money, priced from what it "
                + "costs the bank to make.");
        lead.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED)
                + " -fx-padding: 0 0 6 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        /* ------------------------- there is no bank ------------------------- */
        if (bank.getBranches() <= 0) {
            column.getChildren().add(alert("There is no bank in this city",
                    "Every borrower is lent money from the central bank, priced as a bank "
                    + "would price it. Build a Commercial Bank: one branch is its owners' "
                    + "capital as well as a building, and its savers' deposits start "
                    + "funding the city's loans."));
        }

        if (bank.isInsolvent()) {
            column.getChildren().add(alert("The bank has failed",
                    "It lost more than it owned, so it may lend nothing new. It can be "
                    + "recapitalised here, or earn its way back out, slowly, on the book it "
                    + "already has."));
            column.getChildren().add(bankRescue());
        }

        ladder(column, bank);

        /* ------------------------------ behind it ------------------------------ */
        column.getChildren().add(statementHead("Behind it"));
        HistorySave h = ui.game.getHistorySave();
        column.getChildren().add(bankRow("Profit",
                "its income statement, and what it did with the profit",
                money(bank.getNetIncome()) + " this month",
                money(bank.overYear(Bank.Line.NET)) + " over " + yearWords(bank),
                bank.getNetIncome() < 0 ? Palette.BAD : Palette.GOOD, "Profit"));
        int troubled = bank.getBooksWatched();
        column.getChildren().add(bankRow("Lending",
                "who owes it, what it has set aside, how the next loan is priced",
                money(bank.getBook()) + " lent",
                troubled > 0 ? troubled + (troubled == 1 ? " borrower" : " borrowers") + " in trouble"
                        : "every borrower sound",
                troubled > 0 ? Palette.WARN : Palette.TEXT_HEAD, "Lending"));
        column.getChildren().add(bankRow("Funding",
                "deposits, the central bank, and its branches",
                money(bank.getDeposits()) + " deposited",
                bank.wholesaleFunding() > 0
                        ? money(bank.wholesaleFunding()) + " borrowed from the central bank"
                        : "nothing borrowed from the central bank",
                bank.wholesaleFunding() > 0 ? Palette.WARN : Palette.TEXT_HEAD, "Funding"));
        column.getChildren().add(bankRow("Capital & owners",
                "its capital against its target, its payout, its shares",
                bank.isInsolvent() ? "failed"
                        : bank.getWeightedBook() > 0 ? share(bank.capitalRatio()) + " capital" : "nothing lent",
                bank.payoutDecision(), stanceTone(bank), "Capital & owners"));
        column.getChildren().add(bankRow("History",
                "its rates, capital, returns and losses over time",
                h.months() + (h.months() == 1 ? " month" : " months"),
                h.months() < 2 ? "a line needs two points" : "recorded",
                Palette.TEXT_HEAD, "History"));

        ui.rootMenu.getChildren().addAll(title, lead, statusLine(bank),
                scorecardTop(bank), scorecardBottom(bank), ui.scrolled(column, 330));
    }

    /** The bank's state in one sentence, in the colour of the news. */
    Label statusLine(Bank bank) {
        Label says = new Label(bank.status());
        says.setWrapText(true);
        says.setMaxWidth(STATEMENT + 180);
        says.setStyle(Palette.words(Palette.SIZE_BODY, stanceTone(bank))
                + " -fx-font-weight: bold; -fx-padding: 0 0 8 0;");
        return says;
    }

    /** Good at or over its target, a warning while it rebuilds, bad under the minimum or failed. */
    static String stanceTone(Bank bank) {
        return switch (bank.payoutStance()) {
            case PAYING, RETURNING -> Palette.GOOD;
            case REBUILDING        -> Palette.WARN;
            case UNDER_MINIMUM, FAILED -> Palette.BAD;
            case NO_BANK           -> Palette.TEXT_MUTED;
        };
    }

    /* ------------------------------ the scorecard ------------------------------ */

    /** What it earned, what that returns its owners, its capital, and its losses. */
    HBox scorecardTop(Bank bank) {
        double roe = bank.returnOnEquityOverYear();
        double lost = bank.provisionRateOverYear();
        return vitalsBar(
                limitCell("PROFIT", money(bank.getNetIncome()),
                        money(bank.overYear(Bank.Line.NET)) + " over " + yearWords(bank),
                        bank.getNetIncome() < 0 ? Palette.BAD : Palette.GOOD),
                limitCell("RETURN ON EQUITY", rate(roe),
                        String.format("over %s; its owners want %.1f%%", yearWords(bank),
                                Bank.requiredReturn() * 100),
                        roe < 0 ? Palette.BAD : roe < Bank.requiredReturn() ? Palette.WARN : Palette.GOOD),
                capitalCell(bank),
                limitCell("CREDIT LOSSES", rate(lost),
                        "set aside, of its loans, " + yearWords(bank),
                        lost > 2 * Bank.BASE_LOSS_RATE ? Palette.WARN : Palette.TEXT_HEAD));
    }

    /** Its margin, its costs, and the two sides of its balance sheet a player knows by name. */
    HBox scorecardBottom(Bank bank) {
        double nim = bank.netInterestMarginOverYear();
        double costs = bank.costShareOverYear();
        return vitalsBar(
                limitCell("INTEREST MARGIN", rate(nim),
                        "net, on what it lent, " + yearWords(bank),
                        nim < 0 ? Palette.BAD : Palette.TEXT_HEAD),
                limitCell("COSTS", Double.isNaN(costs) ? "—" : share(costs),
                        Double.isNaN(costs) ? "it earned nothing to set them against"
                                : "of what it earns, " + yearWords(bank),
                        Double.isNaN(costs) || costs > 1 ? Palette.BAD : Palette.TEXT_HEAD),
                limitCell("LENT OUT", money(bank.getBook()), "at face value", Palette.TEXT_HEAD),
                limitCell("DEPOSITS", money(bank.getDeposits()), "banked with it", Palette.TEXT_HEAD));
    }

    /**
     * The capital ratio with its band drawn under it: the minimum, the bank's
     * own target and the top of its band, and where it stands against them.
     * limitCell()'s shape, with the bar where its note would start.
     */
    VBox capitalCell(Bank bank) {
        boolean lent = bank.getWeightedBook() > 0;

        Label what = new Label("CAPITAL RATIO");
        what.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        Label figure = new Label(bank.isInsolvent() ? "failed" : lent ? share(bank.capitalRatio()) : "—");
        figure.setStyle(Palette.figure(Palette.SIZE_SECTION, stanceTone(bank)));

        Label says = new Label(String.format("target %s, minimum %s",
                share(bank.capitalTarget()), share(Bank.CAPITAL_RATIO)));
        says.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));

        VBox cell = new VBox(1, what, figure, capitalBand(bank, 150, false), says);
        cell.setAlignment(Pos.CENTER_LEFT);
        cell.setPrefWidth(190);
        cell.setStyle("-fx-padding: 0 14 0 14;"
                + " -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 1 0 0;");
        return cell;
    }

    /**
     * The capital ratio on a bar, with the minimum, the target and the top
     * of the band marked on it. The scale runs to 1.6 times the top, so the
     * band sits in the left of it and a well-capitalised bank reads as full;
     * a ratio past the scale pins at the end and the tooltip says so.
     */
    Pane capitalBand(Bank bank, double width, boolean labelled) {

        double min = Bank.CAPITAL_RATIO, target = bank.capitalTarget(), top = bank.capitalTop();
        double scale = top * 1.6;
        boolean lent = bank.getWeightedBook() > 0;
        double ratio = bank.isInsolvent() ? 0 : lent ? bank.capitalRatio() : 0;
        final double BAR = labelled ? 14 : 7;
        double tall = labelled ? BAR + 18 : BAR + 4;

        Pane band = new Pane();
        band.setPrefSize(width, tall);
        band.setMinSize(width, tall);
        band.setMaxSize(width, tall);

        Region track = new Region();
        track.setPrefSize(width, BAR);
        track.setMinSize(width, BAR);
        track.setMaxSize(width, BAR);
        track.setLayoutY(2);
        track.setStyle("-fx-background-color: " + Palette.CONTROL + "; -fx-background-radius: 2;");
        band.getChildren().add(track);

        double wide = Math.max(lent ? 2 : 0, Math.min(1, ratio / scale) * width);
        Region fill = new Region();
        fill.setPrefSize(wide, BAR);
        fill.setMinSize(wide, BAR);
        fill.setMaxSize(wide, BAR);
        fill.setLayoutY(2);
        fill.setStyle("-fx-background-color: "
                + (ratio < min ? Palette.BAD : ratio < target ? Palette.WARN : Palette.GOOD)
                + "; -fx-background-radius: 2;");
        band.getChildren().add(fill);

        double[] marks = {min, target, top};
        String[] names = {"minimum", "its target", "top of its band"};
        for (int i = 0; i < marks.length; i++) {
            double x = Math.min(width - 2, marks[i] / scale * width);
            Region tick = new Region();
            tick.setPrefSize(2, BAR + 4);
            tick.setMinSize(2, BAR + 4);
            tick.setMaxSize(2, BAR + 4);
            tick.setLayoutX(x);
            tick.setLayoutY(0);
            tick.setStyle("-fx-background-color: " + (i == 1 ? Palette.TEXT_HEAD : Palette.TEXT_MUTED) + ";");
            band.getChildren().add(tick);
            if (labelled) {
                Label at = new Label(share(marks[i]));
                at.setStyle(Palette.figure(Palette.SIZE_CAPTION, i == 1 ? Palette.TEXT_BODY : Palette.TEXT_LABEL)
                        + " -fx-font-weight: normal;");
                at.setLayoutX(Math.max(0, Math.min(width - 36, x - 14)));
                at.setLayoutY(BAR + 5);
                band.getChildren().add(at);
            }
        }

        Tooltip tip = new Tooltip(String.format(
                "Capital: %s of its risk-weighted book%s\nthe minimum the city requires: %s\n"
                + "its own target: %s\nthe top of its band: %s",
                bank.isInsolvent() ? "none - it has failed" : lent ? share(ratio) : "nothing lent",
                lent && ratio > scale ? " (past the end of this scale)" : "",
                share(min), share(target), share(top)));
        tip.setShowDelay(Duration.millis(250));
        Tooltip.install(band, tip);
        return band;
    }

    /* ------------------------------ the rate ladder ------------------------------ */

    /** How wide the ladder's bars run at the highest rate on it. */
    static final double LADDER_BAR = 190;

    /**
     * THE LADDER OF ITS RATES, the landing's centrepiece: the policy rate;
     * what savers get, a share of it; what a prime loan's money costs the
     * bank; prime, with its four parts; and what each borrower pays - every
     * rung with its step from the one it is built on, in points. One read of
     * the model (Bank.ladder()), so every rung is the same moment's.
     */
    void ladder(VBox column, Bank bank) {

        double dial = ui.game.getDebtManager().getPolicyRate();
        Bank.Ladder l = bank.ladder(dial);
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        HouseholdBalance homes = ui.game.getHouseholdBalance();
        double families = homes == null ? 0 : homes.averageRate();
        double city = ui.game.getInterestRate();

        List<String> owing = new ArrayList<>();
        for (String name : Sectors.KEYS) {
            if (credit.getPrincipal(name) > 0 || credit.isBorrowingBlocked(name)) owing.add(name);
        }

        // One scale for every bar: the dearest rate on the ladder.
        double top = Math.max(Math.max(l.prime(), l.carry()), Math.max(families, city));
        top = Math.max(top, Math.max(l.policy(), l.household()));
        for (String name : owing) top = Math.max(top, credit.getRate(name));

        column.getChildren().add(statementHead("The ladder of its rates"));
        column.getChildren().add(statementNote(
                "From the price of money to what each borrower pays. Every step up is a cost "
                + "the bank carries; the step down is its margin on a deposit."));

        column.getChildren().add(rung("The policy rate", l.policy(), top, Palette.ACCENT,
                "set by the central bank - and what the bank's reserves earn there",
                "Go to the policy rate, which every rate here is built on",
                () -> {
                    ui.policyScreen.policyArea = "Money";
                    ui.policyScreen.policyPage = "The policy rate";
                    ui.policyScreen.dropProposal();
                    ui.innerScrollAt.remove("showPolicyMenu:body");
                    ui.policyScreen.showPolicyMenu();
                }));

        column.getChildren().add(rung("What savers get", l.savers(), top, Palette.LADDER[0],
                points(l.saversOverPolicy()) + " on the policy rate. " + saversWhy(l),
                null, null));

        column.getChildren().add(rung("What a loan's money costs it", l.transfer(), top, Palette.LADDER[1],
                points(l.transferOverPolicy()) + " on the policy rate: the term premium on a "
                        + Bank.PRIME_TERM_MONTHS + "-month loan"
                        + (bank.windowShare() > 0
                                ? String.format(", and the central bank's %.2f-point penalty on the %.0f%% it borrows there",
                                        CentralBank.WINDOW_PENALTY * 100, bank.windowShare() * 100)
                                : ""),
                "The funds-transfer price: what a dollar lent for a business loan's term costs the "
                        + "bank - not what its deposits cost it, which is the deposit side's margin.",
                null));

        column.getChildren().add(rung("Prime", l.prime(), top, Palette.LADDER[2],
                String.format("%s on that: running the bank %s, loans expected to go bad %s, "
                        + "the capital a loan ties up %s", points(l.primeOverTransfer()),
                        points(l.running()), points(l.loss()), points(l.capital())),
                "What a sound business pays for a new loan. Every other borrower pays its own risk over it.",
                null));

        /* ------------------------------ the borrowers ------------------------------ */
        column.getChildren().add(subHead("What each borrower pays to borrow now"));
        if (owing.isEmpty()) {
            column.getChildren().add(statementNote(
                    "No business owes it anything. Each would borrow at prime and its own risk over it."));
        }
        for (String name : owing) {
            boolean shut = credit.isBorrowingBlocked(name);
            column.getChildren().add(rung(name, credit.getRate(name), top, Palette.LADDER[2],
                    points(credit.getSpread(name)) + " on prime: its own debts and record"
                            + (shut ? String.format(" - shut out for %d more months", credit.getBlockedMonths(name)) : ""),
                    null, null));
        }
        column.getChildren().add(rung("The families", families > 0 ? families : l.household(), top,
                Palette.SPENDING_RAMP[2],
                families > 0
                        ? String.format("%s on prime, on average: their line starts at %s and adds %.2f points "
                                + "for every month of income a family owes", points(l.overPrime(families)),
                                rate(l.household()), HouseholdBalance.RISK_SLOPE * 100)
                        : "what a family's credit line starts from - no family owes it anything",
                null, null));
        column.getChildren().add(rung("The carry trade", l.carry(), top, Palette.RAMP_REST,
                points(l.overPrime(l.carry())) + " on prime: lent short and to borrowers who never "
                        + "default here, so no term premium and no expected loss",
                "Foreigners borrowing here to hold the money abroad, while the bank lends cheaper "
                        + "than the world pays.",
                null));
        column.getChildren().add(rung("The city's own paper", city, top, Palette.RAMP_REST,
                points(l.overPrime(city)) + " on prime: the city's rate, which the market sets on its "
                        + "credit - the bank does not price it",
                null, null));
    }

    /** Why savers get what they get: the share its funding asks for, and whether its margin held them under it. */
    static String saversWhy(Bank.Ladder l) {
        String why = l.fundingPosition() <= 0 ? "it holds reserves to spare"
                : l.fundingPosition() >= 1 ? "it borrows from the central bank"
                : String.format("it has lent %.0f%% of what its branches gathered", l.fundingPosition() * 100);
        String said = String.format("It aims to pass on %.0f%% of it, because %s, and moves a sixth "
                + "of the way there a month.", l.saversShare() * 100, why);
        if (l.saversHeld()) {
            said += String.format(" Its margin could not pay the %.2f%% it chose, so savers got less.",
                    l.saversChose() * 100);
        }
        return said;
    }

    /**
     * One rung: its name (a link where the rate is set somewhere else), a bar
     * on the ladder's one scale, the rate, and under them its step from the
     * rung it is built on and what the step is for.
     */
    VBox rung(String name, double value, double top, String colour, String step,
              String explain, Runnable go) {

        Label who = new Label(go == null ? name : name + "  ›");
        who.setStyle(Palette.words(Palette.SIZE_BODY, go == null ? Palette.TEXT_BODY : Palette.ACCENT));
        who.setPrefWidth(190);
        who.setMinWidth(190);
        if (explain != null) {
            Tooltip tip = new Tooltip(explain);
            tip.setShowDelay(Duration.millis(250));
            tip.setWrapText(true);
            tip.setMaxWidth(360);
            Tooltip.install(who, tip);
        }

        double wide = top > 0 ? Math.max(2, Math.min(1, Math.max(0, value) / top) * LADDER_BAR) : 2;
        Region bar = new Region();
        bar.setPrefSize(wide, 12);
        bar.setMinSize(wide, 12);
        bar.setMaxSize(wide, 12);
        bar.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 2;");
        HBox lane = new HBox(bar);
        lane.setAlignment(Pos.CENTER_LEFT);
        lane.setPrefWidth(LADDER_BAR);
        lane.setMinWidth(LADDER_BAR);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label figure = new Label(rate(value));
        figure.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.TEXT_HEAD));

        HBox row = new HBox(Palette.GAP, who, lane, gap, figure);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);
        row.setPrefWidth(STATEMENT);

        Label under = new Label(step);
        under.setWrapText(true);
        under.setMaxWidth(STATEMENT - 16);
        under.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED) + " -fx-padding: 0 0 0 14;");

        VBox box = new VBox(1, row, under);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 4 0 4 0;");

        if (go != null) {
            String rest = box.getStyle();
            box.setStyle(rest + " -fx-cursor: hand;");
            box.setOnMouseEntered(e -> box.setStyle(rest + " -fx-cursor: hand;"
                    + " -fx-background-color: " + Palette.CONTROL + ";"));
            box.setOnMouseExited(e -> box.setStyle(rest + " -fx-cursor: hand;"));
            box.setOnMouseClicked(e -> go.run());
        }
        return box;
    }

    /** One page on the landing: what it holds, and its headline. */
    HBox bankRow(String name, String blurb, String figure, String sub, String tone, String page) {

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
            bankArea = BANK_PAGES;
            bankPage = page;
            ui.innerScrollAt.remove("showBankMenu:body");
            showBankMenu();
        });
        row.setOnMouseEntered(e -> row.setStyle(rest
                + Palette.block(Palette.RAISED, Palette.ACCENT)));
        row.setOnMouseExited(e -> row.setStyle(rest + Palette.block(Palette.CONTROL)));
        VBox.setMargin(row, new javafx.geometry.Insets(0, 0, 6, 0));
        return row;
    }

    /* =====================================================================
       THE PAGES BEHIND IT

       Five, on one chip strip: its profit; its lending; its funding; its
       capital and its owners; and its history. Four figures across the top
       of every one, so a page never loses the state of the whole bank.
       ===================================================================== */

    void drawBankScreen() {

        Label title = new Label("THE BANK — " + bankPage.toUpperCase());
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        javafx.scene.layout.FlowPane strip =
                chipStrip(BANK_PAGE_NAMES, bankPage, Palette.SIZE_LABEL, name -> {
                    bankPage = name;
                    ui.innerScrollAt.remove("showBankMenu:body");
                    showBankMenu();
                });
        strip.setStyle("-fx-padding: 8 0 10 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        switch (bankPage) {
            case "Lending"          -> lendingPage(column);
            case "Funding"          -> fundingPage(column);
            case "Capital & owners" -> capitalPage(column);
            case "History"          -> historyPage(column);
            default                 -> profitPage(column);
        }

        Button back = new Button("The bank at a glance");
        back.setOnAction(e -> {
            bankArea = null;
            ui.innerScrollAt.remove("showBankMenu:body");
            showBankMenu();
        });

        ui.rootMenu.getChildren().addAll(title, pageVitals(), strip,
                ui.scrolled(column, 250), back);
    }

    /** The four figures across the top of every page. */
    HBox pageVitals() {
        Bank bank = ui.game.getBank();
        double dial = ui.game.getDebtManager().getPolicyRate();
        return vitalsBar(
                limitCell("PROFIT", money(bank.getNetIncome()), "this month",
                        bank.getNetIncome() < 0 ? Palette.BAD : Palette.GOOD),
                limitCell("CAPITAL RATIO",
                        bank.isInsolvent() ? "failed" : bank.getWeightedBook() > 0 ? share(bank.capitalRatio()) : "—",
                        "its target " + share(bank.capitalTarget()), stanceTone(bank)),
                limitCell("PRIME", rate(bank.prime(dial)), "what a sound business pays", Palette.TEXT_HEAD),
                limitCell("LENT OUT", money(bank.getBook()), "at face value", Palette.TEXT_HEAD));
    }

    /* =====================================================================
       PROFIT

       Its income statement this month against last, every line opening
       into what it is made of - the interest by who paid it, since 0.7.9
       (the old page said it "cannot be split further", and it could) -
       then what it did with the profit, the last twelve months, and the
       three ratios a bank is read by.
       ===================================================================== */

    void profitPage(VBox column) {

        Bank bank = ui.game.getBank();
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
                line(bank, "On its reserves at the central bank", Bank.Line.FROM_RESERVES, known, false));
        column.getChildren().add(bookLine("Interest earned",
                bank.thisMonth(Bank.Line.INTEREST), bank.lastMonth(Bank.Line.INTEREST), known,
                Palette.GOOD_MONEY, byWho, "from whom", openLines));
        column.getChildren().add(line(bank, "Paid to savers", Bank.Line.SAVERS, known, true));
        column.getChildren().add(line(bank, "Paid for borrowing overnight from the central bank",
                Bank.Line.WINDOW, known, true));
        column.getChildren().add(total(bank, "Net interest income", Bank.Line.NET_INTEREST, known));

        VBox fees = new VBox(0);
        fees.getChildren().addAll(
                line(bank, "On the families' accounts", Bank.Line.ACCOUNT_FEES, known, false),
                line(bank, "On the businesses' new loans", Bank.Line.LOAN_FEES_PAID, known, false),
                line(bank, "On the families' new borrowing, added to what they owe",
                        Bank.Line.LOAN_FEES_OWED, known, false));
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
                "A provision is money set aside for loans expected to go bad: a year's expected loss "
                + "on a sound borrower, what a default would cost on one in trouble. It holds %s now.",
                moneyFull(bank.getAllowance()))));
        column.getChildren().add(bookLine("Provisions for loans expected to go bad",
                -bank.thisMonth(Bank.Line.PROVISIONS), -bank.lastMonth(Bank.Line.PROVISIONS), known,
                bank.provisions() > 0 ? Palette.WARN : null, provided, "what", openLines));

        column.getChildren().add(bookLine("The trading desk",
                bank.thisMonth(Bank.Line.TRADING), bank.lastMonth(Bank.Line.TRADING), known,
                bank.getTradingIncome() < 0 ? Palette.WARN : null, deskDetail(bank), "what it did", openLines));
        if (Math.abs(bank.thisMonth(Bank.Line.PAPER_GAINS)) > 1e-9
                || Math.abs(bank.lastMonth(Bank.Line.PAPER_GAINS)) > 1e-9) {
            column.getChildren().add(line(bank, "Gains on the city's paper that changed hands",
                    Bank.Line.PAPER_GAINS, known, false));
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
                "Its owners are paid on last month's profit, by its capital - "
                + bank.payoutDecision() + ". The Capital & owners page has the rule."));

        /* ---------------------------- the last twelve months ---------------------------- */
        int n = bank.monthsInYear();
        column.getChildren().add(statementHead(n >= Bank.YEAR_MONTHS ? "The last twelve months"
                : n <= 1 ? "Its first month on record" : "The " + n + " months on record"));
        year(column, bank, "Net interest income", Bank.Line.NET_INTEREST, false);
        year(column, bank, "Fees", Bank.Line.FEES, false);
        year(column, bank, "Provisions", Bank.Line.PROVISIONS, true);
        year(column, bank, "The trading desk", Bank.Line.TRADING, false);
        year(column, bank, "Gains on the city's paper", Bank.Line.PAPER_GAINS, false);
        year(column, bank, "Staff and branches", Bank.Line.COSTS, true);
        column.getChildren().add(statementTotal("Profit before tax",
                moneyFull(bank.overYear(Bank.Line.PRE_TAX)), bank.overYear(Bank.Line.PRE_TAX) < 0 ? Palette.BAD : null));
        year(column, bank, "Tax", Bank.Line.TAX, true);
        column.getChildren().add(statementTotal("What it kept",
                moneyFull(bank.overYear(Bank.Line.NET)), bank.overYear(Bank.Line.NET) < 0 ? Palette.BAD : Palette.GOOD));
        year(column, bank, "Paid to its shareholders", Bank.Line.DIVIDENDS, true);
        year(column, bank, "Its own shares, bought back", Bank.Line.BUYBACKS, true);
        column.getChildren().add(statementTotal("Kept in the bank",
                moneyFull(bank.overYear(Bank.Line.RETAINED)), null));

        /* ------------------------------- as ratios ------------------------------- */
        column.getChildren().add(subHead("Read as ratios, over " + yearWords(bank)));
        double nim = bank.netInterestMarginOverYear();
        column.getChildren().add(statementLine("Net interest margin, on what it lent",
                rate(nim), nim < 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(statementLine("...this month alone", rate(bank.netInterestMargin()),
                Palette.TEXT_MUTED));
        double costs = bank.costShareOverYear();
        column.getChildren().add(statementLine("Staff and branches, of what it earns",
                Double.isNaN(costs) ? "—" : share(costs),
                Double.isNaN(costs) || costs > 1 ? Palette.BAD : null));
        double roe = bank.returnOnEquityOverYear();
        column.getChildren().add(statementLine("Return on its equity", rate(roe),
                roe < 0 ? Palette.BAD : roe < Bank.requiredReturn() ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("...this month alone", rate(bank.returnOnEquity()),
                Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("...and what its owners want", rate(Bank.requiredReturn()),
                Palette.TEXT_MUTED));
        column.getChildren().add(statementNote(
                "The margin is what it charges less what it pays for its money, on everything lent. "
                + "Its costs are read against what it earns before them - about half to three-fifths "
                + "at a real bank."));
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
     * which are capital since 0.7.8.
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
        StringBuilder positions = new StringBuilder();
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            double held = register.deskShare(c);
            if (held <= 0) continue;
            if (positions.length() > 0) positions.append(", ");
            positions.append(String.format("%s %.1f%% of the company at %s",
                    Equity.COMPANIES[c], held * 100, tightMoney(toDollars(exchange.mark(c)), false)));
        }
        desk.getChildren().add(statementNote(positions.length() == 0
                ? "The desk holds nothing. It quotes every company round what the register says a share is"
                  + " worth, buys what comes and sells what it has."
                : "On the desk: " + positions + ". Carried at the quote or the register's value, whichever"
                  + " is lower - the desk does not mark its own book up on a quote nobody has paid yet."));
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
       LENDING

       Who owes it and how sound each borrower is; what it has set aside
       against them and who has stopped paying; what it lent this month
       against what its capital allows; how the next loan's rate is built;
       and what the book weighs against its capital.
       ===================================================================== */

    void lendingPage(VBox column) {

        Bank bank = ui.game.getBank();
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        HouseholdBalance homes = ui.game.getHouseholdBalance();

        /* ------------------------------ who owes it ------------------------------ */
        column.getChildren().add(statementHead("Who owes it"));
        if (bank.getBook() <= 0) {
            column.getChildren().add(sentence("Nothing is lent out.", Palette.TEXT_MUTED));
        } else {
            List<Slice> split = new ArrayList<>();
            if (bank.getSectorBook() > 0)    split.add(new Slice("The businesses", bank.getSectorBook(), Palette.LADDER[1]));
            if (bank.getCityBook() > 0)      split.add(new Slice("The city's own paper", bank.getCityBook(), Palette.LADDER[0]));
            if (bank.getHouseholdBook() > 0) split.add(new Slice("The families", bank.getHouseholdBook(), Palette.SPENDING_RAMP[2]));
            if (bank.getCarryBook() > 0)     split.add(new Slice("The carry trade", bank.getCarryBook(), Palette.RAMP_REST));
            column.getChildren().add(stackedBar(split, STATEMENT - 40));
        }
        column.getChildren().add(statementLine("The businesses", moneyFull(bank.getSectorBook())));
        column.getChildren().add(statementLine("The city's own paper", moneyFull(bank.getCityBook())));
        column.getChildren().add(statementLine("The families", moneyFull(bank.getHouseholdBook())));
        column.getChildren().add(statementLine("The carry trade", moneyFull(bank.getCarryBook())));
        column.getChildren().add(statementTotal("On the book", moneyFull(bank.getBook()), Palette.TEXT_HEAD));
        column.getChildren().add(statementNote(
                "The city's paper is its bonds, which the bank buys. The carry trade is foreigners "
                + "borrowing here to hold the money abroad while the bank lends cheaper than the world pays."));

        /* ---------------------------- the businesses ---------------------------- */
        column.getChildren().add(statementHead("The businesses, one by one"));
        javafx.scene.layout.GridPane t = grid(
                new double[] {112, 72, 92, 56, 36, 72, 70}, rightAfterFirst(7));
        gridHead(t, "", "owed", "pays", "leverage", "stage", "set aside", "");
        int row = 1;
        for (String name : Sectors.KEYS) {
            double owed = credit.getPrincipal(name);
            boolean shut = credit.isBorrowingBlocked(name);
            if (owed <= 0 && !shut && bank.getSectorAllowance(name) <= 0) continue;
            double lev = credit.getLeverage(name);
            int stage = bank.getStage(name);
            t.add(gridCell(name, shut ? Palette.BAD : Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, row);
            t.add(gridCell(money(owed), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, row);
            t.add(gridCell(rate(credit.getRate(name)),
                    Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, row);
            t.add(gridCell(String.format("%.2fx", lev),
                    lev >= BusinessDebtManager.INSOLVENCY_TRIGGER ? Palette.BAD
                            : lev > Bank.SECTOR_WATCH_LEVERAGE ? Palette.WARN : Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 3, row);
            t.add(gridCell(String.valueOf(stage), stage == 2 ? Palette.WARN : Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 4, row);
            t.add(gridCell(money(bank.getSectorAllowance(name)), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 5, row);
            t.add(gridCell(shut ? credit.getBlockedMonths(name) + "mo shut" : "borrowing",
                    shut ? Palette.BAD : Palette.GOOD, Palette.SIZE_CAPTION, true), 6, row);
            row++;
        }
        if (row == 1) {
            column.getChildren().add(sentence("No business owes it anything.", Palette.TEXT_MUTED));
        } else {
            column.getChildren().add(t);
        }
        column.getChildren().add(statementNote(String.format(
                "\"Pays\" is what its next loan would cost it. Leverage is what a business owes over what "
                + "it owns: past %.2f the bank will not lend it more to cover a loss and sets aside what "
                + "its default would cost (stage 2); at %.2f it is restructured, written down to %.0f%% of "
                + "its assets and shut out for a while.", Bank.SECTOR_WATCH_LEVERAGE,
                BusinessDebtManager.INSOLVENCY_TRIGGER, BusinessDebtManager.RESTRUCTURE_TARGET * 100)));

        /* ------------------------------ the families ------------------------------ */
        column.getChildren().add(statementHead("The families"));
        if (bank.getHouseholdBook() <= 0 && bank.getHouseholdAllowance() <= 0) {
            column.getChildren().add(sentence("No family owes it anything.", Palette.TEXT_MUTED));
        } else {
            column.getChildren().add(statementLine("Owed altogether", moneyFull(bank.getHouseholdBook())));
            column.getChildren().add(statementLine(String.format(
                    "...by families owing more than %.0f months of their income", Bank.HOUSEHOLD_WATCH_MONTHS),
                    moneyFull(bank.getHouseholdWatchedDebt()),
                    bank.getHouseholdWatchedDebt() > 0 ? Palette.WARN : Palette.TEXT_SPENT));
            column.getChildren().add(statementLine("Stage", bank.getHouseholdStage() == 2
                    ? "2 - some are in trouble" : "1 - every family sound",
                    bank.getHouseholdStage() == 2 ? Palette.WARN : null));
            column.getChildren().add(statementLine("Set aside against it", moneyFull(bank.getHouseholdAllowance())));
            column.getChildren().add(statementLine("Drawn this month", moneyFull(bank.getLentToHouseholds())));
            column.getChildren().add(statementLine("Repaid this month", moneyFull(bank.getRepaidByHouseholds())));
            if (homes != null && homes.getWrittenOff() > 0) {
                column.getChildren().add(statementLine("Discharged in bankruptcy this month",
                        moneyFull(homes.getWrittenOff()), Palette.BAD));
            }
            column.getChildren().add(statementNote(
                    "A family's credit line is revolving: it draws on it when it cannot meet its bills, "
                    + "and one that never catches up is discharged - the bank loses all of it."));
        }

        /* ---------------------------- what it set aside ---------------------------- */
        column.getChildren().add(statementHead("What it has set aside"));
        column.getChildren().add(statementLine("Against the businesses", moneyFull(bank.getSectorAllowance())));
        column.getChildren().add(statementLine("Against the families", moneyFull(bank.getHouseholdAllowance())));
        column.getChildren().add(statementTotal("The allowance", moneyFull(bank.getAllowance()), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("This month's provision", signed(bank.provisions(), false),
                bank.provisions() > 0 ? Palette.WARN : null));
        column.getChildren().add(statementLine("Written off this month", moneyFull(bank.getWriteOffs()),
                bank.getWriteOffs() > 0 ? Palette.BAD : Palette.TEXT_SPENT));
        HistorySave h = ui.game.getHistorySave();
        int recorded = h.monthsRecorded("bankWriteOffs");
        column.getChildren().add(statementLine(String.format("...and over the %d months on record", recorded),
                moneyFull(h.total("bankWriteOffs")), Palette.TEXT_MUTED));
        column.getChildren().add(statementNote(String.format(
                "The allowance is money set aside for loans expected to go bad - a year's expected loss, "
                + "%.1f%%, on a sound borrower, and what its default would cost on one in trouble. A "
                + "write-off is drawn from it first, so a loss reaches the profit as the borrower weakens, "
                + "not the month it is written off.", Bank.BASE_LOSS_RATE * 100)));

        /* -------------------------- who has stopped paying -------------------------- */
        javafx.scene.layout.GridPane trouble = grid(
                new double[] {150, 100, 100, 96, 90}, rightAfterFirst(5));
        gridHead(trouble, "", "this month", "in total", "restructured", "status");
        int line = 1;
        for (String name : Sectors.KEYS) {
            double month = credit.getWrittenOffThisMonth(name);
            double ever = credit.getWrittenOffTotal(name);
            boolean shut = credit.isBorrowingBlocked(name);
            if (month <= 0 && ever <= 0 && !shut) continue;
            trouble.add(gridCell(name, shut ? Palette.BAD : Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            trouble.add(gridCell(month > 0 ? money(month) : "—", month > 0 ? Palette.BAD : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 1, line);
            trouble.add(gridCell(ever > 0 ? money(ever) : "—", Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 2, line);
            trouble.add(gridCell(String.valueOf(credit.getRestructureCount(name)),
                    credit.getRestructureCount(name) > 0 ? Palette.WARN : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 3, line);
            trouble.add(gridCell(shut ? credit.getBlockedMonths(name) + "mo shut" : "borrowing",
                    shut ? Palette.BAD : Palette.GOOD, Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        column.getChildren().add(statementHead("Who has stopped paying"));
        if (line == 1) {
            column.getChildren().add(sentence("No business has been written down, and none is shut out.",
                    Palette.GOOD));
        } else {
            column.getChildren().add(trouble);
            column.getChildren().add(statementNote(
                    "A restructured business had its loans written down rather than repaid, and is shut "
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
        column.getChildren().add(statementNote(String.format(
                "At or over its own target (%s) it lends freely. Between the %s minimum and the target a "
                + "borrower's debt may grow a little each month - more the nearer the target. Under the "
                + "minimum it lends only the interest that keeps its borrowers going.",
                share(bank.capitalTarget()), share(Bank.CAPITAL_RATIO))));

        /* ------------------------ how the next loan is priced ------------------------ */
        double dial = ui.game.getDebtManager().getPolicyRate();
        Bank.Ladder l = bank.ladder(dial);
        column.getChildren().add(statementHead("How the next loan's rate is built"));
        HBox cost = statementLine("What the money costs it", rate(l.transfer()));
        Tooltip.install(cost, new Tooltip("The funds-transfer price: the policy rate, the central bank's "
                + "penalty on the share it borrows there, and the term premium for "
                + Bank.PRIME_TERM_MONTHS + " months."));
        column.getChildren().add(cost);
        column.getChildren().add(statementLine("...running the bank", points(l.running())));
        column.getChildren().add(statementLine("...the loans expected to go bad", points(l.loss())));
        column.getChildren().add(statementLine("...the capital a loan ties up", points(l.capital())));
        column.getChildren().add(statementTotal("Prime", rate(l.prime()), Palette.TEXT_HEAD));
        column.getChildren().add(statementNote(String.format(
                "What a sound business pays for a %d-month loan. The capital part is the equity a loan "
                + "ties up at its %s target, at the %.1f%% its owners want, over what the same money "
                + "would cost as debt.", Bank.PRIME_TERM_MONTHS, share(bank.capitalTarget()),
                Bank.requiredReturn() * 100)));

        javafx.scene.layout.GridPane quotes = grid(new double[] {190, 120, 120}, rightAfterFirst(3));
        gridHead(quotes, "", "its own risk", "it pays");
        int q = 1;
        for (String name : Sectors.KEYS) {
            if (credit.getPrincipal(name) <= 0) continue;
            quotes.add(gridCell(name, Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, q);
            quotes.add(gridCell(points(credit.getSpread(name)), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 1, q);
            quotes.add(gridCell(rate(credit.getRate(name)), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, q);
            q++;
        }
        double families = homes == null ? 0 : homes.averageRate();
        quotes.add(gridCell("The families, on average", Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, q);
        quotes.add(gridCell(families > 0 ? points(l.overPrime(families)) : "—", Palette.TEXT_MUTED,
                Palette.SIZE_CAPTION, true), 1, q);
        quotes.add(gridCell(rate(families > 0 ? families : l.household()), Palette.TEXT_HEAD,
                Palette.SIZE_CAPTION, true), 2, q++);
        quotes.add(gridCell("The carry trade", Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, q);
        quotes.add(gridCell(points(l.overPrime(l.carry())), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 1, q);
        quotes.add(gridCell(rate(l.carry()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, q);
        column.getChildren().add(subHead("...and what each borrower pays over it"));
        column.getChildren().add(quotes);

        /* ---------------------------- what the book weighs ---------------------------- */
        column.getChildren().add(statementHead("What a dollar of the book weighs"));
        javafx.scene.layout.GridPane w = grid(new double[] {150, 100, 60, 80, 110}, rightAfterFirst(5));
        gridHead(w, "", "at face", "term", "risk weight", "weighs");
        int r = 1;
        for (Bank.WeightRow one : bank.weightTable()) {
            w.add(gridCell(bookName(one.book()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, r);
            w.add(gridCell(money(one.face()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, r);
            w.add(gridCell(String.format("%.2f", one.term()), one.term() < 1 ? Palette.GOOD : Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 2, r);
            w.add(gridCell(String.format("%.0f%%", one.risk() * 100),
                    one.risk() < 1 ? Palette.GOOD : one.risk() > 1 ? Palette.WARN : Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 3, r);
            w.add(gridCell(money(one.weighted()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 4, r);
            r++;
        }
        column.getChildren().add(w);
        column.getChildren().add(statementTotal("Weighed for risk and term",
                moneyFull(bank.getWeightedBook()), Palette.TEXT_HEAD));
        column.getChildren().add(statementNote(String.format(
                "Risk-weighted: each dollar counted by how likely it is to be lost and how long it runs. A "
                + "city bond weighs %.0f%% of a business loan, and the desk's shares %.0f%%; \"term\" is the "
                + "share of its face a loan's remaining months count for, as little as %.0f%% for one repaying "
                + "soon. The bank's capital is measured against this, not the face.",
                Bank.RISK_CITY * 100, Bank.RISK_EQUITY * 100, Bank.SHORTEST_WEIGHT * 100)));
    }

    /** What the weight table calls each book. */
    static String bookName(Bank.Book book) {
        return switch (book) {
            case BUSINESSES -> "The businesses";
            case CITY       -> "The city's own paper";
            case FAMILIES   -> "The families";
            case CARRY      -> "The carry trade";
            case DESK       -> "The desk's shares";
        };
    }

    /* =====================================================================
       FUNDING

       What the city has banked with it and how much of that its branches
       reach, in today's money (the founding constants, until 0.7.9 - a
       hundred times out after a currency reform); what it pays savers and
       why; its account at the central bank; how its lending is funded; what
       it can carry; and its branches, with the model's own verdict on
       another one.
       ===================================================================== */

    void fundingPage(VBox column) {

        Bank bank = ui.game.getBank();
        double dial = ui.game.getDebtManager().getPolicyRate();
        Bank.Ladder l = bank.ladder(dial);

        /* ---------------------------- what is banked ---------------------------- */
        column.getChildren().add(statementHead("What the city has banked with it"));
        if (bank.getDeposits() <= 0) {
            column.getChildren().add(sentence("Nobody has banked anything yet.", Palette.TEXT_MUTED));
        } else {
            List<Slice> whose = new ArrayList<>();
            if (bank.getHouseholdDeposits() > 0) whose.add(new Slice("The families", bank.getHouseholdDeposits(), Palette.LADDER[0]));
            if (bank.getSectorDeposits() > 0)    whose.add(new Slice("The businesses", bank.getSectorDeposits(), Palette.LADDER[1]));
            if (bank.getForeignDeposits() > 0)   whose.add(new Slice("From abroad", bank.getForeignDeposits(), Palette.SPENDING_RAMP[3]));
            column.getChildren().add(stackedBar(whose, STATEMENT - 40));
        }
        column.getChildren().add(statementLine("The families' savings", moneyFull(bank.getHouseholdDeposits())));
        column.getChildren().add(statementLine("The businesses' cash in credit", moneyFull(bank.getSectorDeposits())));
        column.getChildren().add(statementLine("Money from abroad", moneyFull(bank.getForeignDeposits()),
                bank.getForeignDeposits() > 0 ? Palette.WARN : null));
        column.getChildren().add(statementTotal("Deposits", moneyFull(bank.getDeposits()), Palette.TEXT_HEAD));

        /* ---------------------------- what it can reach ---------------------------- */
        column.getChildren().add(statementHead("What its branches can reach"));
        column.getChildren().add(statementLine("One branch reaches", moneyFull(bank.getDepositsPerBranch())));
        column.getChildren().add(statementLine(String.format("...so its %s reach",
                bank.getBranches() == 1 ? "one branch" : formatter.format(Math.round(bank.getBranches())) + " branches"),
                moneyFull(bank.branchReach())));
        column.getChildren().add(statementLine("The city's own savings with it", moneyFull(bank.localDeposits())));
        column.getChildren().add(statementLine("...within its branches' reach", moneyFull(bank.localDepositsReached()),
                Palette.GOOD));
        column.getChildren().add(statementLine("...beyond it", moneyFull(bank.localDepositsBeyondReach()),
                bank.localDepositsBeyondReach() > 0 ? Palette.WARN : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("...and money from abroad, which needs no branch",
                moneyFull(bank.getForeignDeposits())));
        column.getChildren().add(statementTotal("Deposits it can lend against", moneyFull(bank.depositsGathered()),
                Palette.TEXT_HEAD));
        column.getChildren().add(statementNote(
                "A bank cannot lend savings its counters cannot reach. Money wired from abroad needs no "
                + "counter - and can leave as fast as it came."));

        if (bank.getForeignDeposits() > 0) {
            double hot = bank.hotFundingShare();
            column.getChildren().add(alert(
                    hot > .25 ? "A quarter of its funding can leave tomorrow" : "Some of its funding is foreign",
                    String.format("%s of what it lends against is money from abroad - %.0f%% of it - here "
                    + "because the rate is good. It leaves on no notice, and the bank must find the cash "
                    + "when it does. The Trade tab is where that is priced.",
                    money(bank.getForeignDeposits()), hot * 100)));
        }

        /* ---------------------------- what it pays savers ---------------------------- */
        column.getChildren().add(statementHead("What it pays savers"));
        column.getChildren().add(statementLine("The policy rate", rate(l.policy())));
        column.getChildren().add(statementLine("The share of it its funding asks it to pass on",
                share(l.saversShare())));
        column.getChildren().add(statementLine("The rate it chose, a sixth of the way there a month",
                rate(l.saversChose())));
        column.getChildren().add(statementLine("What savers were paid", rate(l.savers()),
                l.saversHeld() ? Palette.WARN : Palette.ACCENT));
        column.getChildren().add(statementNote(saversWhy(l)));
        column.getChildren().add(statementLine("Paid to the families this month",
                moneyFull(bank.getDepositInterestToHouseholds())));
        column.getChildren().add(statementLine("...to the businesses", moneyFull(bank.getDepositInterestToSectors())));
        if (bank.getDepositInterestToForeign() > 0) {
            column.getChildren().add(statementLine("...and abroad", moneyFull(bank.getDepositInterestToForeign())));
        }
        column.getChildren().add(statementNote(String.format(
                "A bank flush with reserves passes on about %.0f%% of the policy rate - its next deposit "
                + "only earns the policy rate at the central bank. One borrowing there passes on %.0f%%, "
                + "because every deposit it finds saves it the central bank's rate. It never pays savers "
                + "more than its margin, after its staff and branches, can pay.",
                Bank.DEPOSIT_SHARE_FLUSH * 100, Bank.DEPOSIT_SHARE_AT_WINDOW * 100)));

        /* ------------------------ its account at the central bank ------------------------ */
        column.getChildren().add(statementHead("Its account at the central bank"));
        column.getChildren().add(statementLine("Reserves held there", moneyFull(bank.cashReserves()),
                bank.cashReserves() > 0 ? Palette.GOOD : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("...earning the policy rate", rate(l.policy())));
        column.getChildren().add(statementLine("...which paid it this month", moneyFull(bank.getPlacementIncome())));
        column.getChildren().add(statementLine("Borrowed overnight from the central bank",
                moneyFull(bank.wholesaleFunding()), bank.wholesaleFunding() > 0 ? Palette.BAD : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("...at the window's rate", rate(l.window())));
        column.getChildren().add(statementLine("...which cost it this month", moneyFull(bank.getFundingCost()),
                bank.getFundingCost() > 0 ? Palette.WARN : null));
        column.getChildren().add(statementNote(String.format(
                "\"The window\" is borrowing overnight from the central bank. It lends a bank whatever it "
                + "needs, at the policy rate plus a %.2f-point penalty - so what stops a bank lending more "
                + "is what that money costs, which is in every loan's rate, and its capital, not the money.",
                CentralBank.WINDOW_PENALTY * 100)));

        /* ---------------------------- how it is funded ---------------------------- */
        column.getChildren().add(statementHead("How its lending is funded"));
        List<Slice> mix = new ArrayList<>();
        if (bank.equity() > 0)            mix.add(new Slice("Its own capital", bank.equity(), Palette.GOOD_MONEY));
        if (bank.depositFunding() > 0)    mix.add(new Slice("Savers' deposits", bank.depositFunding(), Palette.LADDER[1]));
        if (bank.getForeignDeposits() > 0) mix.add(new Slice("Money from abroad", bank.getForeignDeposits(), Palette.SPENDING_RAMP[3]));
        if (bank.wholesaleFunding() > 0)  mix.add(new Slice("The central bank, overnight", bank.wholesaleFunding(), Palette.BAD));
        if (!mix.isEmpty()) column.getChildren().add(keyedBar(mix, STATEMENT - 40));
        column.getChildren().add(statementLine("Its own capital", moneyFull(bank.equity()),
                bank.equity() < 0 ? Palette.BAD : null));
        column.getChildren().add(statementLine("Savers' deposits it has lent out", moneyFull(bank.depositFunding())));
        column.getChildren().add(statementLine("Money from abroad", moneyFull(bank.getForeignDeposits())));
        column.getChildren().add(statementLine("Borrowed overnight from the central bank",
                moneyFull(bank.wholesaleFunding()), bank.wholesaleFunding() > 0 ? Palette.BAD : null));

        /* ---------------------------- what it can carry ---------------------------- */
        column.getChildren().add(statementHead("What it can carry"));
        column.getChildren().add(statementLine(String.format("What its capital carries, at the %s minimum",
                share(Bank.CAPITAL_RATIO)), moneyFull(bank.capitalLimit())));
        column.getChildren().add(statementLine(String.format("What its deposits carry, lent %.0f times over",
                Bank.LEVERAGE), moneyFull(bank.fundingLimit())));
        column.getChildren().add(statementTotal("The tighter of the two",
                bank.isInsolvent() ? "nothing - it has failed" : moneyFull(bank.capacity()),
                bank.isInsolvent() ? Palette.BAD : Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("Its book weighs", moneyFull(bank.getWeightedBook())));
        if (bank.capacity() > 0) {
            column.getChildren().add(statementLine("...which is", share(bank.strain()) + " of that",
                    bank.strain() > 1 ? Palette.BAD : bank.strain() > Bank.EASY_STRAIN ? Palette.WARN : Palette.GOOD));
        }
        column.getChildren().add(statementNote(bank.isInsolvent()
                ? "A failed bank may lend nothing new, so it can carry nothing until it has capital again."
                : bank.capitalBound()
                ? "Its capital is the limit: another branch helps only by the capital its owners open it with."
                : "Its deposits are the limit: another branch reaches more of the city's savings."));

        branches(column, bank);
    }

    /**
     * ITS BRANCHES, and whether another would pay - the model's own verdict,
     * both halves of Bank.wantsBranch(): does it relieve anything (the book
     * spilling past what the bank comfortably carries), and would it earn its
     * keep (last month's book per branch at its kept margin, against what a
     * branch costs to run). Worked out on the screen until 0.7.9, and not the
     * same way the model did.
     */
    void branches(VBox column, Bank bank) {

        BuildingsTemplate branch = ui.game.getBuildingManager().getTemplateByName("Commercial Bank");

        column.getChildren().add(statementHead("Its branches"));
        column.getChildren().add(statementLine("Standing", formatter.format(Math.round(bank.getBranches()))));
        column.getChildren().add(statementLine("One more would add", moneyFull(bank.capacityAnotherBranchWouldAdd())
                + " of what it can carry", bank.capacityAnotherBranchWouldAdd() > 0 ? Palette.GOOD : Palette.TEXT_SPENT));
        if (branch != null) {
            column.getChildren().add(statementLine("...costs to build", moneyFull(branch.getCashCost())));
        }
        column.getChildren().add(statementLine("...and its owners put in", moneyFull(bank.getPaidInPerBranch()),
                Palette.GOOD));
        column.getChildren().add(statementLine("A branch cost to run last month",
                moneyFull(bank.runningCostPerBranch())));
        column.getChildren().add(statementLine("...and its share of what the book kept",
                moneyFull(bank.keptPerBranch()),
                bank.branchWouldPayForItself() ? Palette.GOOD : Palette.WARN));

        column.getChildren().add(subHead("Would another one pay?"));
        boolean relieves = bank.bookAnotherBranchWouldCarry() > 0;
        column.getChildren().add(statementLine("Is anything spilling over?",
                bank.overflowPastComfortable() > 0
                        ? "yes - " + money(bank.overflowPastComfortable()) + " of the book"
                        : "no", relieves ? Palette.GOOD : Palette.TEXT_MUTED));
        column.getChildren().add(statementNote(String.format(
                "The weighed book past %.0f%% of what it can carry; one more branch would take %s of it "
                + "onto the bank's own account.", Bank.EASY_STRAIN * 100, money(bank.bookAnotherBranchWouldCarry()))));
        column.getChildren().add(statementLine("Would it earn its keep?",
                bank.branchWouldPayForItself() ? "yes" : "no",
                bank.branchWouldPayForItself() ? Palette.GOOD : Palette.WARN));
        column.getChildren().add(statementLine(String.format("Is the bank past %.0f%% of what it can carry?",
                Bank.BUILD_AT_STRAIN * 100), bank.strain() > Bank.BUILD_AT_STRAIN ? "yes" : "no",
                bank.strain() > Bank.BUILD_AT_STRAIN ? Palette.GOOD : Palette.TEXT_MUTED));

        String verdict;
        String tone;
        if (bank.wantsBranch()) {
            verdict = "Yes. The city's own investors run the same test, and build one when it passes.";
            tone = Palette.GOOD;
        } else if (bank.isInsolvent()) {
            verdict = "No: a failed bank may not lend, so another counter changes nothing. It needs capital.";
            tone = Palette.BAD;
        } else if (bank.capacityAnotherBranchWouldAdd() <= 0) {
            verdict = "No: its branches already reach every dollar the city has banked. A counter cannot "
                    + "fix a shortage of savings.";
            tone = Palette.TEXT_MUTED;
        } else if (!relieves) {
            verdict = "Not yet: nothing is spilling over, so nothing is waiting for the room.";
            tone = Palette.TEXT_MUTED;
        } else if (!bank.branchWouldPayForItself()) {
            verdict = "No: a branch would cost more to run than its share of the book keeps.";
            tone = Palette.WARN;
        } else {
            verdict = String.format("Not yet: it builds ahead of being full, from %.0f%% of what it can carry.",
                    Bank.BUILD_AT_STRAIN * 100);
            tone = Palette.TEXT_MUTED;
        }
        column.getChildren().add(sentence(verdict, tone));

        Button build = new Button("Build a Commercial Bank  ›");
        build.setOnAction(e -> ui.buildScreen.handleAllBuildingMenus("Commercial",
                EnumSet.of(BuildingType.COMMERCIAL)));
        HBox act = new HBox(build);
        act.setAlignment(Pos.CENTER_LEFT);
        act.setStyle("-fx-padding: 6 0 4 0;");
        column.getChildren().add(act);
    }

    /* =====================================================================
       CAPITAL & OWNERS

       Its capital in its band and why the target is where it is; what its
       rule does with the month's profit; how its equity moved, every cause
       named and a residual that must be nothing (Bank.equityMovement(),
       since 0.7.9 - it left out the founding settlement when the screen
       worked it out); its share price and who owns it; its rescues.
       ===================================================================== */

    void capitalPage(VBox column) {

        Bank bank = ui.game.getBank();
        boolean lent = bank.getWeightedBook() > 0;

        /* ------------------------------ its capital ------------------------------ */
        column.getChildren().add(statementHead("Its capital"));
        column.getChildren().add(capitalBand(bank, STATEMENT - 40, true));
        column.getChildren().add(statementLine("Equity", moneyFull(bank.equity()),
                bank.equity() < 0 ? Palette.BAD : null));
        column.getChildren().add(statementLine("Its risk-weighted book", moneyFull(bank.getWeightedBook())));
        column.getChildren().add(statementTotal("Capital ratio",
                bank.isInsolvent() ? "failed" : lent ? share(bank.capitalRatio()) : "nothing lent",
                stanceTone(bank)));
        column.getChildren().add(statementLine("The minimum the city requires", share(Bank.CAPITAL_RATIO)));
        column.getChildren().add(statementLine("Its own target", share(bank.capitalTarget()), Palette.TEXT_HEAD));
        column.getChildren().add(statementNote("It chose " + bank.targetReason() + "."));
        column.getChildren().add(statementLine("The top of its band", share(bank.capitalTop())));
        column.getChildren().add(statementNote(String.format(
                "Past the top it returns the excess to its owners. The band is %.1f points wide - about what "
                + "Canada's big banks hold over what their regulator expects of them.",
                Bank.MANAGEMENT_CUSHION * 100)));

        if (bank.payoutStance() == Bank.Payout.UNDER_MINIMUM) {
            column.getChildren().add(alert("It is under the minimum",
                    String.format("Under the %s the city requires, it lends only what keeps its borrowers "
                    + "going until it is back over it - by earning it or by being given it. %s would take "
                    + "it back to its own target of %s.", share(Bank.CAPITAL_RATIO),
                    money(bank.recapitalisationNeeded()), share(bank.capitalTarget()))));
        }

        /* ------------------------ what it does with its profit ------------------------ */
        column.getChildren().add(statementHead("What it does with its profit"));
        String decision = bank.payoutDecision();
        column.getChildren().add(sentence(decision.substring(0, 1).toUpperCase() + decision.substring(1) + ".",
                stanceTone(bank)));
        column.getChildren().add(statementLine("Last month's profit after tax, which it pays on",
                moneyFull(bank.getPayoutProfit())));
        column.getChildren().add(statementLine("What it held over its target", moneyFull(bank.getPayoutOverTarget())));
        column.getChildren().add(statementLine("...and past the top of its band", moneyFull(bank.getPayoutExcess())));
        column.getChildren().add(statementLine("Paid to its shareholders this month", moneyFull(bank.getDividendsPaid()),
                bank.getDividendsPaid() > 0 ? Palette.GOOD : null));
        column.getChildren().add(statementLine("...over the last twelve months", moneyFull(bank.dividendsOverYear()),
                Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("Its own shares bought back this month",
                moneyFull(bank.getSharesBoughtBack())));
        column.getChildren().add(statementLine("...over the last twelve months", moneyFull(bank.buybacksOverYear()),
                Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("New shares issued this month", moneyFull(bank.getSharesIssued())));
        column.getChildren().add(statementLine("...over " + yearWords(bank), moneyFull(bank.overYear(Bank.Line.ISSUED)),
                Palette.TEXT_MUTED));
        column.getChildren().add(statementNote(String.format(
                "Under its target it keeps everything. Inside its band it pays out %.0f%% of its profit after "
                + "tax, never so much that it would fall under the target; over the top it also returns a "
                + "twelfth of the excess a month. Its desk buys its own shares back while it stands at or over "
                + "its target, and issues new ones only while it is under it.", Bank.PAYOUT_IN_BAND * 100)));

        /* ------------------------------ how its equity moved ------------------------------ */
        column.getChildren().add(statementHead("How its equity moved this month"));
        if (!bank.isMonthKnown()) {
            column.getChildren().add(sentence("Recorded from the next month played: there is no month yet "
                    + "to read it from.", Palette.TEXT_MUTED));
        } else {
            Bank.EquityMovement m = bank.equityMovement();
            column.getChildren().add(statementLine("At the start of the month", moneyFull(m.opening())));
            column.getChildren().add(statementLine("What it kept", signed(m.kept(), false),
                    m.kept() < 0 ? Palette.BAD : Palette.GOOD));
            moved(column, "Capital from its shareholders", m.fromShareholders(), Palette.GOOD);
            moved(column, "Capital from the city, in a rescue", m.fromCity(), Palette.GOOD);
            moved(column, "The founding settlement, taking over the city's loans", m.founding(), null);
            moved(column, "Paid to its shareholders", -m.dividends(), Palette.WARN);
            moved(column, "Its own shares bought back", -m.boughtBack(), Palette.WARN);
            moved(column, "New shares it issued", m.issued(), Palette.GOOD);
            moved(column, "Absorbed when it failed", m.absorbed(), Palette.BAD);
            moved(column, "Gain on paper the treasury bought back", m.treasuryBuyback(), null);
            moved(column, "Set aside when this older save was opened", -m.allowanceOpened(), Palette.WARN);
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
                column.getChildren().add(statementNote(
                        "Equity moves by what it earned, what it was given and what it paid out, and by "
                        + "nothing else. The unaccounted line is printed even at zero, because a plug "
                        + "nobody checks is a lie."));
            }
        }

        /* -------------------------------- its owners -------------------------------- */
        // the sector screen's block, borrowed: it reads the game and is not a pure piece
        ui.sectorScreen.ownersBlock(column, Equity.BANK, bank.equity(), bank.getNetIncome());
        column.getChildren().add(statementNote(
                "The treasury holds none of its shares: capital the city puts in during a rescue is given, "
                + "not bought."));

        /* -------------------------------- its rescues -------------------------------- */
        column.getChildren().add(statementHead("Its rescues"));
        column.getChildren().add(statementLine("Times it has failed", String.valueOf(bank.getFailures()),
                bank.getFailures() > 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(statementLine("What its creditors absorbed", moneyFull(bank.getResolutionLoss()),
                bank.getResolutionLoss() > 0 ? Palette.BAD : null));
        column.getChildren().add(statementLine("What the city has put in", moneyFull(bank.getBailoutsLifetime())));
        // TODO(docs): since 0.7.0 the bank's wholesale lender is the central
        // bank's window, not creditors outside the city; the loss is still
        // booked as crossing the edge (MoneyAudit's "+ bank ResolutionLoss").
        // Who absorbs a failed bank now is Jerus's open question, and this
        // sentence follows his answer.
        column.getChildren().add(statementNote(
                "When a bank loses more than it owns, somebody eats the hole: here it is booked to its "
                + "creditors outside the city. It comes out owning nothing, and may not lend until it has "
                + "capital again - from a rescue, or from its own profit."));

        if (bank.isInsolvent()) {
            column.getChildren().add(statementHead("The bank has failed"));
            column.getChildren().add(bankRescue());
        }
    }

    /** One cause of the equity's movement, printed only when it moved it. */
    void moved(VBox column, String label, double amount, String tone) {
        if (Math.abs(amount) < .0005) return;
        column.getChildren().add(statementLine(label, signed(amount, false), tone));
    }

    /* =====================================================================
       THE RESCUE, WHEREVER THE PLAYER IS LOOKING.

       Jerus: "when the bank has an issue, and you click go to bank, the
       recapitalise the bank button is quite hidden, make it so that in the
       bank section its on the top, not all the way hidden in the balance
       sheet."

       ONE block used in TWO places - the top of the landing, where a failed
       bank is the only thing worth reading, and the Capital page. The guard
       on the treasury's cash is the model's (Game.canRecapitaliseBank(),
       since 0.7.9), so the two cannot drift apart and nothing else can offer
       the rescue on a different rule.
       ===================================================================== */
    VBox bankRescue() {

        double needed = ui.game.bankRecapitalisationNeeded();
        double cash = ui.game.getCash();
        boolean can = ui.game.canRecapitaliseBank();

        VBox block = new VBox(0);
        block.setMaxWidth(Region.USE_PREF_SIZE);

        block.getChildren().add(statementLine("To put it back on its feet", moneyFull(needed), Palette.BAD));
        block.getChildren().add(statementLine("The treasury holds", moneyFull(cash),
                can ? Palette.GOOD : Palette.BAD));

        Button rescue = new Button("Recapitalise the bank — " + money(needed));
        rescue.setDisable(!can);
        if (can) {
            rescue.setStyle("-fx-background-color: " + Palette.CONFIRM + "; -fx-text-fill: white;"
                    + " -fx-padding: 8 18 8 18;");
        }
        rescue.setOnAction(e -> {
            if (!ui.game.canRecapitaliseBank()) return;
            ui.game.recapitaliseBank(ui.game.bankRecapitalisationNeeded());
            ui.innerScrollAt.remove("showBankMenu:body");
            showBankMenu();
        });

        HBox act = new HBox(rescue);
        act.setAlignment(Pos.CENTER_LEFT);
        act.setStyle("-fx-padding: 10 0 4 0;");
        block.getChildren().add(act);

        if (!can) {
            block.getChildren().add(statementNote(
                    "The treasury cannot cover it today, so the button is dead until it can. The bank can "
                    + "still earn its way back out on the book it already has - slower, and it costs the "
                    + "city nothing."));
        }

        /*
         * SAID OUT LOUD, because a player who borrows to do this is doing
         * something that looks free and is not. See Bank.receiveBailout().
         */
        block.getChildren().add(alert("Borrowing to do it has the bank capitalise itself",
                "The city borrows FROM this bank. Issuing paper to raise the rescue money means the "
                + "bank buys the bond, the cash comes back to it as capital, and its balance sheet has "
                + "grown on both sides without anybody putting anything in. It works on the screen and "
                + "it is not a rescue."));
        return block;
    }

    /* =====================================================================
       HISTORY

       Six charts, each on one scale in one unit: its rates; its capital
       against its target; its return on equity; its provisions against its
       write-offs; its lending against its deposits and what it could carry;
       and its fees. Under them the statistics that mean something since
       0.7.8 - months under its target and under the minimum, its worst
       year of provisions - asked of the record (HistorySave), where the
       premium-era ones compared the face book with capacity.

       DRAWN HERE RATHER THAN LINKED TO THE REPORTS TAB on purpose: the big
       picker chart normalises every line to its own range, and the point of
       these is that their lines share one.
       ===================================================================== */

    void historyPage(VBox column) {

        Bank bank = ui.game.getBank();
        HistorySave h = ui.game.getHistorySave();

        if (h.months() < 2) {
            column.getChildren().add(statementHead("Nothing to draw yet"));
            column.getChildren().add(sentence(String.format(
                    "The city has lived %d month%s and a line needs two points. Come back in a year.",
                    h.months(), h.months() == 1 ? "" : "s"), Palette.TEXT_MUTED));
            return;
        }

        column.getChildren().add(statementHead("Its rates"));
        column.getChildren().add(trendChart(
                new String[] {"The policy rate", "What savers got", "Prime"},
                new double[][] {h.aligned("policyRate"), h.aligned("bankDepositRate"), h.aligned("bankPrime")},
                new String[] {Palette.TEXT_MUTED, Palette.SERIES[4], Palette.ACCENT},
                BankScreen::rate));
        column.getChildren().add(statementNote(
                "Prime sits over the policy rate by the bank's costs; savers under it by its margin on a deposit."));

        column.getChildren().add(statementHead("Its capital against its target"));
        column.getChildren().add(trendChart(
                new String[] {"Capital ratio", "Its target"},
                new double[][] {h.aligned("bankCapitalRatio"), h.aligned("bankCapitalTarget")},
                new String[] {Palette.ACCENT, Palette.TEXT_MUTED},
                BankScreen::share));
        column.getChildren().add(statementNote(
                "Recorded to a ceiling of 1,000%: a young bank's capital against a tiny book is enormous, "
                + "and nothing lent at all is drawn at the ceiling."));
        int recorded = h.monthsRecorded("bankCapitalRatio");
        int underTarget = h.monthsUnder("bankCapitalRatio", "bankCapitalTarget");
        int underMinimum = h.monthsUnder("bankCapitalRatio", Bank.CAPITAL_RATIO);
        column.getChildren().add(statementLine("Months under its target", underTarget + " of " + recorded,
                underTarget > 0 ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("Months under the minimum", underMinimum + " of " + recorded,
                underMinimum > 0 ? Palette.BAD : Palette.GOOD));

        column.getChildren().add(statementHead("Its return on equity"));
        column.getChildren().add(trendChart(
                new String[] {"Return on equity"},
                new double[][] {h.aligned("bankReturnOnEquity")},
                new String[] {Palette.ACCENT},
                BankScreen::rate));
        column.getChildren().add(statementNote(String.format(
                "Each month's profit at a yearly rate, on the equity it opened with - so it swings; its "
                + "owners want %.1f%% over time. Recorded between -1,000%% and 1,000%%.",
                Bank.requiredReturn() * 100)));
        int losing = h.monthsUnder("bankProfit", 0.0);
        column.getChildren().add(statementLine("Months it lost money",
                losing + " of " + h.monthsRecorded("bankProfit"), losing > 0 ? Palette.WARN : Palette.GOOD));

        column.getChildren().add(statementHead("What it set aside, and what it wrote off"));
        column.getChildren().add(trendChart(
                new String[] {"Provisions", "Written off"},
                new double[][] {h.aligned("bankProvisions"), h.aligned("bankWriteOffs")},
                new String[] {Palette.WARN, Palette.BAD}));
        column.getChildren().add(statementLine("Its worst year of provisions", moneyFull(h.worstYear("bankProvisions")),
                Palette.WARN));
        column.getChildren().add(statementLine("...as its capital target reads it",
                share(bank.getWorstLossRate()) + " of its risk-weighted book"));
        column.getChildren().add(statementNote(
                "It sets money aside as a borrower weakens, so a loss shows here before it is written off. "
                + "A whole business sector is one borrower in this city, so losses come in lumps."));

        column.getChildren().add(statementHead("What it lent, against what it could"));
        column.getChildren().add(trendChart(
                new String[] {"Lent out", "What it could carry", "Deposits"},
                new double[][] {h.aligned("bankLent"), h.aligned("bankCapacity"), h.aligned("bankDeposits")},
                new String[] {Palette.LADDER[2], Palette.GOOD, Palette.RAMP_REST}));
        column.getChildren().add(statementNote(
                "Lent out is at face; what it could carry is measured against the risk-weighted book, "
                + "so a book of city bonds can sit above the green line and still leave room."));

        column.getChildren().add(statementHead("Its fees"));
        column.getChildren().add(trendChart(
                new String[] {"Fees a month"},
                new double[][] {h.aligned("bankFees")},
                new String[] {Palette.ACCENT}));
    }
}
