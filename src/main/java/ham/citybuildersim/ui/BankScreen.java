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
 * The bank tab: the gauge, the two limits, another branch, who owes it, where
 * the money comes from, the books, the rescue, and its history.
 *
 * Split out of UserInterface on 2026-09-18: the ten banners from THE BANK to
 * ITS HISTORY exactly as they were, the shell's members reached through ui. The
 * shell still reads which page and area are open (bankPage, bankArea) for the
 * rail and the scroll memory.
 */
final class BankScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    BankScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       THE BANK

       ITS OWN RAIL TAB NOW, and the redo follows: it was one screen of Courier
       at the foot of Finances, and it is the counterparty to every loan in the
       city. What made that indefensible is a single number - ratePremium() -
       which is added to the rate of every mill, shop, household and bond in the
       city, and which lived four lines down a monospaced wall with no
       explanation of what moves it.

       FIVE SUBJECTS, each a row on the landing and its own strip inside:

         WHAT IT CAN LEND   the gauge, the two limits, and what a branch buys.
                            This is the tab's reason to exist.
         WHO OWES IT        by borrower and by sector, and who has stopped
                            paying.
         WHERE THE MONEY    what the city has banked, what the branches can
         COMES FROM         actually reach, and what it borrows at the central
                            bank's window (abroad, until 0.7.0).
         THE BOOKS          the income statement and the balance sheet, in the
                            language the sector books use.
         ITS HISTORY        the four series HistorySave now keeps: the book
                            against capacity, the strain, the equity and the
                            profit.

       THE ACTION STAYS AS IT WAS. Jerus: rescue on failure only. Putting
       capital in whenever you like is a lever the city does not currently have
       and adding one is a game change rather than a screen change.
       ===================================================================== */

    String bankArea = null;                  // null is the landing
    static final String BANK_HOME     = "The gauge";
    String bankPage = BANK_HOME;

    static final String[] BANK_LEND_PAGES =
            {"The gauge", "The two limits", "Another branch"};
    static final String[] BANK_OWED_PAGES  = {"By borrower", "In trouble"};
    static final String[] BANK_MONEY_PAGES = {"Deposits", "Funding"};
    static final String[] BANK_BOOKS_PAGES = {"Income", "Balance sheet"};
    static final String[] BANK_PAST_PAGES  = {"Lending", "Strain", "Capital"};

    /**
     * THE BANK.
     *
     * One screen for the one question the player actually has to answer about
     * it: is the city's credit dear, and if so, which of the two limits is
     * making it dear. The answer is a different building in each case - another
     * branch when the counters are full, and nothing at all when the savings
     * are short, because a counter cannot fix a shortage of savings.
     *
     * Written as a statement rather than as a dashboard for the same reason the
     * household screen was: five boxes of numbers is a screen you read once, and
     * a set of books is a screen you come back to.
     *
     * (The tab's first header, from before the redo the banner above describes;
     * the one question is still the gauge's. It had sat in the shell with no
     * method under it, and came to the tab's entry point on 2026-09-18.)
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

        Label lead = new Label("Every loan in the city is its money, and its strain is in "
                + "every rate.");
        lead.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED)
                + " -fx-padding: 0 0 10 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        /* ------------------------- there is no bank ------------------------- */
        if (bank.getBranches() <= 0) {
            column.getChildren().add(alert("There is no bank in this city",
                    String.format("Every borrower in it is paying %.0f points over the odds, "
                    + "because the lending is coming from strangers who have never heard of "
                    + "the place. Build a Commercial Bank — one branch is an equity "
                    + "injection as well as a building, which is what incorporating a bank "
                    + "actually is.", Bank.MAX_STRAIN_PREMIUM * 100)));
        }

        if (bank.isInsolvent()) {
            column.getChildren().add(alert("The bank has failed",
                    "It has lost more than it owns, so it may lend nothing and every "
                    + "borrower in the city is paying the full premium. It can be "
                    + "recapitalised here — or it can earn its way back out, slowly, on "
                    + "the book it already has."));
            column.getChildren().add(bankRescue());
        }

        double book = bank.getBook();
        double capacity = bank.capacity();
        double strain = capacity > 0 ? book / capacity : (book > 0 ? Double.NaN : 0);
        double premium = bank.ratePremium();
        double written = bank.getWriteOffs();

        column.getChildren().add(bankRow("What it can lend",
                "the two limits, the strain, and what one more branch would buy",
                capacity > 0 ? String.format("%.0f%% used", bank.strain() * 100) : "no capacity",
                premium > 0
                        ? String.format("%+.1f points on every rate in the city", premium * 100)
                        : "adding nothing to anybody's rate",
                premium > 0 ? Palette.BAD : Palette.GOOD,
                "What it can lend", "The gauge"));

        column.getChildren().add(bankRow("Who owes it",
                "the businesses, the treasury and the families — and who has stopped paying",
                money(book),
                written > 0 ? money(written) + " written off this month"
                            : "nothing written off this month",
                written > 0 ? Palette.WARN : Palette.TEXT_HEAD,
                "Who owes it", "By borrower"));

        column.getChildren().add(bankRow("Where the money comes from",
                "savings it can reach, and the central bank's window for the rest",
                money(bank.depositsGathered()),
                bank.wholesaleFunding() > 0
                        ? money(bank.wholesaleFunding()) + " borrowed at the window"
                        : "funded entirely by deposits",
                bank.hotFundingShare() > .25 ? Palette.WARN : Palette.TEXT_HEAD,
                "Where the money comes from", "Deposits"));

        column.getChildren().add(bankRow("The books",
                "what it made this month, and what it is standing on",
                money(bank.getNetIncome()) + "/mo",
                String.format("%.1f%% capital against a required %.0f%%",
                        Math.min(999, bank.capitalRatio()) * 100, Bank.CAPITAL_RATIO * 100),
                bank.getNetIncome() < 0 ? Palette.BAD : Palette.GOOD,
                "The books", "Income"));

        int months = ui.game.getHistorySave().months();
        column.getChildren().add(bankRow("Its history",
                "how the book, the strain and the capital have moved",
                months + (months == 1 ? " month" : " months"),
                months < 2 ? "a line needs two points" : "recorded",
                Palette.TEXT_HEAD,
                "Its history", "Lending"));

        ui.rootMenu.getChildren().addAll(title, lead, bankVitals(), ui.scrolled(column, 210));
    }

    /** One subject on the bank's landing page. */
    HBox bankRow(String name, String blurb, String figure, String sub,
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
            bankArea = area;
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

    /** The four figures that are true of the whole tab. */
    HBox bankVitals() {

        Bank bank = ui.game.getBank();
        double capacity = bank.capacity();
        double premium = bank.ratePremium();

        return vitalsBar(
                // WEIGHED, and the caption has to say so: the face is $7.2B
                // against a capacity of $4.3B, which is 167% - and the strain
                // is 76%, because capacity is measured on the risk-weighted
                // book. Two true numbers that look like a contradiction.
                limitCell("LENT OUT", money(bank.getBook()),
                        capacity > 0
                                ? String.format("weighed, %.0f%% of capacity", bank.strain() * 100)
                                : "against no capacity at all",
                        bank.strain() > Bank.EASY_STRAIN ? Palette.BAD : Palette.TEXT_HEAD),
                limitCell("IT CAN CARRY", money(capacity),
                        bank.capacityWith(bank.getBranches()) <= 0 ? "nothing"
                                : bank.capitalBound() ? "capital is the limit"
                                : "deposits are the limit",
                        capacity > 0 ? Palette.TEXT_HEAD : Palette.BAD),
                limitCell("IT ADDS", String.format("%.1f pts", premium * 100),
                        "to every rate in the city",
                        premium > 0 ? Palette.BAD : Palette.GOOD),
                limitCell("EQUITY", money(bank.equity()),
                        bank.getWeightedBook() > 0
                                ? String.format("%.1f%% of its risk-weighted book",
                                        Math.min(999, bank.capitalRatio()) * 100)
                                : "nothing lent to weigh it against",
                        bank.isInsolvent() ? Palette.BAD
                                : bank.capitalRatio() < Bank.CAPITAL_RATIO * 1.5 ? Palette.WARN
                                : Palette.GOOD));
    }

    /* =====================================================================
       ONE SUBJECT, ITS OWN STRIP
       ===================================================================== */

    void drawBankScreen() {

        String[] pages = switch (bankArea) {
            case "Who owes it"                -> BANK_OWED_PAGES;
            case "Where the money comes from" -> BANK_MONEY_PAGES;
            case "The books"                  -> BANK_BOOKS_PAGES;
            case "Its history"                -> BANK_PAST_PAGES;
            default                           -> BANK_LEND_PAGES;
        };

        Label title = new Label(bankArea.toUpperCase());
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        javafx.scene.layout.FlowPane strip =
                chipStrip(pages, bankPage, Palette.SIZE_LABEL, name -> {
                    bankPage = name;
                    ui.innerScrollAt.remove("showBankMenu:body");
                    showBankMenu();
                });
        strip.setStyle("-fx-padding: 8 0 10 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        switch (bankArea) {
            case "Who owes it" -> {
                if ("In trouble".equals(bankPage)) bankTroublePage(column);
                else                               bankBorrowerPage(column);
            }
            case "Where the money comes from" -> {
                if ("Funding".equals(bankPage)) bankFundingPage(column);
                else                            bankDepositPage(column);
            }
            case "The books" -> {
                if ("Balance sheet".equals(bankPage)) bankBalancePage(column);
                else                                  bankIncomePage(column);
            }
            case "Its history" -> bankHistoryPage(column, bankPage);
            default -> {
                switch (bankPage) {
                    case "The two limits"  -> bankLimitsPage(column);
                    case "Another branch"  -> bankBranchPage(column);
                    default                -> bankGaugePage(column);
                }
            }
        }

        Button back = new Button("All of the bank");
        back.setOnAction(e -> {
            bankArea = null;
            ui.innerScrollAt.remove("showBankMenu:body");
            showBankMenu();
        });

        ui.rootMenu.getChildren().addAll(title, bankVitals(), strip,
                ui.scrolled(column, 250), back);
    }

    /* =====================================================================
       THE GAUGE

       ONE PICTURE FOR THE WHOLE TAB. The bank's strain is the number that
       decides what every borrower in the city pays, and it was four lines of
       monospace saying "Lent out, as a share  167%". A percentage with no
       scale behind it cannot say whether 167% is fine or fatal - and the
       answer is that 80% is where it starts to hurt and 150% is where it has
       hurt as much as it can.

       So: an arc with the bands drawn on it, the needle where the bank is, and
       the premium in the middle. The bands are the whole point; the needle
       alone would be the same number in a rounder shape.
       ===================================================================== */

    /** The top of the gauge's scale, as a multiple of capacity. */
    static final double GAUGE_MAX = 2.0;

    void bankGaugePage(VBox column) {

        Bank bank = ui.game.getBank();
        double capacity = bank.capacity();
        double weighted = bank.getWeightedBook();
        double strain = bank.strain();
        double premium = bank.ratePremium();

        column.getChildren().add(statementHead("How hard the bank is working"));

        if (capacity <= 0 && weighted <= 0) {
            column.getChildren().add(sentence(
                    "Nothing lent and nothing to lend. Build a branch and this fills in.",
                    Palette.TEXT_MUTED));
            return;
        }

        HBox middle = new HBox(Palette.GAP_SECTION,
                strainGauge(strain, premium, 260), gaugeKey(bank));
        middle.setAlignment(Pos.CENTER_LEFT);
        middle.setMaxWidth(STATEMENT);
        column.getChildren().add(middle);

        /* --------------------------- and in words --------------------------- */
        column.getChildren().add(sentence(
                premium <= 0
                    ? String.format("The bank is inside itself. It has %s of room before "
                        + "the premium starts, and while that lasts nobody in the city is "
                        + "paying anything for the state of its bank.",
                        money(bank.headroom()))
                    : strain >= Bank.HARD_STRAIN
                    ? String.format("The bank is lending half again what it can carry. The "
                        + "premium is at its maximum of %.0f points and cannot get worse — "
                        + "which is not good news, it means the price has stopped "
                        + "responding to the problem.", Bank.MAX_STRAIN_PREMIUM * 100)
                    : String.format("Past %.0f%% the strain is priced, and it is being "
                        + "priced: %.1f points on every loan in the city. It reaches its "
                        + "maximum of %.0f points at %.0f%%.",
                        Bank.EASY_STRAIN * 100, premium * 100,
                        Bank.MAX_STRAIN_PREMIUM * 100, Bank.HARD_STRAIN * 100),
                premium <= 0 ? Palette.GOOD : Palette.BAD_TEXT));

        /* ------------------------ what it is measured on ------------------------ */
        column.getChildren().add(statementHead("The figures behind it"));

        column.getChildren().add(statementLine("Lent out, at face",
                moneyFull(bank.getBook())));
        column.getChildren().add(statementLine("...weighed for risk and term",
                moneyFull(weighted), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("What it can carry",
                moneyFull(capacity), Palette.TEXT_HEAD));
        column.getChildren().add(statementTotal("Which is",
                capacity > 0 ? String.format("%.0f%% of capacity", strain * 100)
                             : "past any capacity at all",
                premium > 0 ? Palette.BAD : Palette.GOOD));

        column.getChildren().add(statementNote(String.format(
                "The strain is measured on the WEIGHTED book, not the face. A treasury "
                + "bill is sovereign and short, so it weighs %.0f%% of what a business "
                + "loan does — the bank is holding it instead of cash rather than lending "
                + "against it. The relief is worth %s today.",
                Bank.RISK_CITY * 100,
                money(bank.getBook() - weighted))));

        /* --------------------------- who pays for it --------------------------- */
        if (premium > 0) {
            column.getChildren().add(statementHead("What that costs the city"));

            DebtManager market = ui.game.getDebtManager();
            column.getChildren().add(statementLine("The city's own paper",
                    String.format("%.2f%%   ·   %.2f%% of it is the bank",
                            market.getRate() * 100, premium * 100), Palette.BAD));
            column.getChildren().add(statementNote(
                    "And the same points are on every business loan and every household "
                    + "overdraft in the city. This is the one figure on any screen in the "
                    + "game that moves everybody's costs at once."));
        }
    }

    /**
     * An arc with the bands on it, the needle where the bank is, and the
     * premium in the hole.
     *
     * A HALF CIRCLE rather than a full one, because a gauge that can read past
     * its own end has to look like it does: the scale stops at twice capacity
     * and the needle pins there, which is honest about a city with no bank
     * (strain is infinite) rather than drawing it as a full ring that happens
     * to have gone round.
     */
    StackPane strainGauge(double strain, double premium, double size) {

        javafx.scene.layout.Pane face = new javafx.scene.layout.Pane();
        face.setPrefSize(size, size * .62);
        face.setMinSize(size, size * .62);
        face.setMaxSize(size, size * .62);

        double cx = size / 2;
        double cy = size * .56;
        double radius = (size - Palette.RING) / 2;

        /* ------------------------------ the bands ------------------------------ */
        double[] edges = {0, Bank.EASY_STRAIN, Bank.HARD_STRAIN, GAUGE_MAX};
        String[] tones = {Palette.GOOD, Palette.WARN, Palette.BAD};
        String[] names = {"comfortable", "the premium is rising", "as bad as it gets"};

        for (int i = 0; i < 3; i++) {
            double from = 180 - edges[i] / GAUGE_MAX * 180;
            double to   = 180 - edges[i + 1] / GAUGE_MAX * 180;
            javafx.scene.shape.Arc band = new javafx.scene.shape.Arc(
                    cx, cy, radius, radius, to, from - to);
            band.setType(javafx.scene.shape.ArcType.OPEN);
            band.setFill(null);
            band.setStroke(javafx.scene.paint.Color.web(tones[i]));
            band.setStrokeWidth(Palette.RING);
            band.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.BUTT);
            band.setOpacity(.28);
            Tooltip.install(band, new Tooltip(String.format("%.0f%%–%.0f%% of capacity: %s",
                    edges[i] * 100, edges[i + 1] * 100, names[i])));
            face.getChildren().add(band);
        }

        /* ---------------------------- and the reading ---------------------------- */
        double shown = Math.max(0, Math.min(GAUGE_MAX,
                Double.isFinite(strain) ? strain : GAUGE_MAX));
        double span = shown / GAUGE_MAX * 180;

        javafx.scene.shape.Arc value = new javafx.scene.shape.Arc(
                cx, cy, radius, radius, 180 - span, span);
        value.setType(javafx.scene.shape.ArcType.OPEN);
        value.setFill(null);
        value.setStroke(javafx.scene.paint.Color.web(
                premium <= 0 ? Palette.GOOD
                        : shown >= Bank.HARD_STRAIN ? Palette.BAD : Palette.WARN));
        value.setStrokeWidth(Palette.RING - 10);
        value.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.BUTT);
        face.getChildren().add(value);

        /* -------------------------------- the mark -------------------------------- */
        double angle = Math.toRadians(180 - span);
        javafx.scene.shape.Line needle = new javafx.scene.shape.Line(
                cx + Math.cos(angle) * (radius - Palette.RING / 2 - 4),
                cy - Math.sin(angle) * (radius - Palette.RING / 2 - 4),
                cx + Math.cos(angle) * (radius + Palette.RING / 2 + 4),
                cy - Math.sin(angle) * (radius + Palette.RING / 2 + 4));
        needle.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_MAX));
        needle.setStrokeWidth(2.5);
        face.getChildren().add(needle);

        /* ------------------------------- the ticks ------------------------------- */
        double[] ticks = {0, Bank.EASY_STRAIN, Bank.HARD_STRAIN, GAUGE_MAX};
        for (double tick : ticks) {
            double a = Math.toRadians(180 - tick / GAUGE_MAX * 180);
            Label mark = new Label(tick >= GAUGE_MAX
                    ? String.format("%.0f%%+", tick * 100)
                    : String.format("%.0f%%", tick * 100));
            mark.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
            // CLAMPED INSIDE THE FACE. The ticks at the two ends sit on the
            // horizontal, so their labels want to be a centimetre outside the
            // pane - and a Pane does not grow for a child that is off its own
            // left edge, it just clips it. The 0% mark was drawn as "%".
            mark.setLayoutX(Math.max(0, Math.min(size - 34,
                    cx + Math.cos(a) * (radius + Palette.RING / 2 + 14) - 14)));
            mark.setLayoutY(cy - Math.sin(a) * (radius + Palette.RING / 2 + 14) - 6);
            face.getChildren().add(mark);
        }

        /* ------------------------------- the middle ------------------------------- */
        Label top = new Label(premium > 0 ? "IT IS ADDING" : "IT IS ADDING");
        top.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        Label figure = new Label(String.format("%.1f pts", premium * 100));
        figure.setStyle(Palette.figure(Palette.SIZE_TITLE,
                premium > 0 ? Palette.BAD : Palette.GOOD));

        Label under = new Label("to every rate");
        under.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        VBox hole = new VBox(-2, top, figure, under);
        hole.setAlignment(Pos.CENTER);
        hole.setTranslateY(size * .10);

        StackPane box = new StackPane(face, hole);
        StackPane.setAlignment(hole, Pos.CENTER);
        box.setPrefSize(size, size * .62);
        box.setMinSize(size, size * .62);
        box.setMaxSize(size, size * .62);
        return box;
    }

    /** The three bands, said in words beside the gauge. */
    VBox gaugeKey(Bank bank) {

        VBox key = new VBox(6);
        key.setAlignment(Pos.CENTER_LEFT);

        double strain = bank.strain();
        double[] edges = {0, Bank.EASY_STRAIN, Bank.HARD_STRAIN, GAUGE_MAX};
        String[] tones = {Palette.GOOD, Palette.WARN, Palette.BAD};
        String[] names = {"Comfortable", "Paying for it", "As bad as it gets"};
        String[] blurbs = {
            "nothing added to anybody's rate",
            "the premium rises with every dollar",
            "the price has stopped responding"
        };

        for (int i = 0; i < 3; i++) {
            boolean here = strain >= edges[i]
                    && (i == 2 || strain < edges[i + 1]);

            Region dot = new Region();
            dot.setMinSize(10, 10);
            dot.setPrefSize(10, 10);
            dot.setMaxSize(10, 10);
            dot.setStyle("-fx-background-color: " + tones[i] + "; -fx-background-radius: 2;"
                    + (here ? "" : " -fx-opacity: .35;"));

            Label name = new Label(i == 2
                    ? String.format("%s  %.0f%% and up", names[i], edges[i] * 100)
                    : String.format("%s  %.0f–%.0f%%",
                            names[i], edges[i] * 100, edges[i + 1] * 100));
            name.setStyle(Palette.words(Palette.SIZE_CAPTION,
                    here ? Palette.TEXT_HEAD : Palette.TEXT_LABEL)
                    + (here ? " -fx-font-weight: bold;" : ""));

            Label what = new Label("     " + blurbs[i]);
            what.setStyle(Palette.words(Palette.SIZE_CAPTION,
                    here ? Palette.TEXT_MUTED : Palette.TEXT_SPENT));

            HBox head = new HBox(6, dot, name);
            head.setAlignment(Pos.CENTER_LEFT);
            key.getChildren().addAll(head, what);
        }

        key.getChildren().add(bookNote(""));
        key.getChildren().add(bookNote("the needle is this bank, today"));
        return key;
    }

    /* =====================================================================
       THE TWO LIMITS

       Jerus, when this model was written: "i just want it to be what banks
       do." What banks do is be held back by exactly two things, and which of
       the two binds decides what the city should do about it - so the screen
       has to say which, rather than printing both and the smaller one.
       ===================================================================== */

    void bankLimitsPage(VBox column) {

        Bank bank = ui.game.getBank();

        double byCapital = bank.capitalLimit();
        double byFunding = bank.depositsGathered() * Bank.LEVERAGE;
        double capacity = bank.capacity();
        double weighted = bank.getWeightedBook();
        boolean capitalBinds = bank.capitalBound();

        column.getChildren().add(statementHead("What holds the bank back"));

        column.getChildren().add(limitBar("Its capital",
                String.format("equity of %s at the required %.0f%%",
                        money(bank.equity()), Bank.CAPITAL_RATIO * 100),
                byCapital, weighted, capitalBinds));

        column.getChildren().add(limitBar("The deposits it can reach",
                String.format("%s gathered, lent %.0f times over",
                        money(bank.depositsGathered()), Bank.LEVERAGE),
                byFunding, weighted, !capitalBinds));

        column.getChildren().add(statementTotal("The tighter of the two",
                moneyFull(capacity), Palette.TEXT_HEAD));

        column.getChildren().add(sentence(capitalBinds
                ? "CAPITAL is the limit. Another branch would not help much — what this "
                + "bank needs is to earn, or to be given some. A bank that loses money "
                + "must lend less, which is what turns a run of write-offs into a credit "
                + "crunch rather than a bad month."
                : "DEPOSITS are the limit. The city has savings this bank cannot reach, and "
                + "reaching them is what branches are actually for. Another counter buys "
                + "real capacity here.",
                Palette.TEXT_BODY));

        /* ------------------------- what a dollar weighs ------------------------- */
        column.getChildren().add(statementHead("What a dollar of the book weighs"));

        javafx.scene.layout.GridPane w = grid(
                new double[] {206, 110, 120, 130}, rightAfterFirst(4));
        gridHead(w, "", "at face", "risk weight", "against capacity");

        int line = 1;
        line = weightRow(w, line, "The treasury", bank.getCityBook(), Bank.RISK_CITY);
        line = weightRow(w, line, "The businesses", bank.getSectorBook(), Bank.RISK_BUSINESS);
        line = weightRow(w, line, "The families", bank.getHouseholdBook(), Bank.RISK_HOUSEHOLD);
        line = weightRow(w, line, "The carry trade", bank.getCarryBook(), Bank.RISK_CARRY);
        column.getChildren().add(w);

        column.getChildren().add(statementLine("Face value of the book",
                moneyFull(bank.getBook())));
        column.getChildren().add(statementTotal("Weighed for risk and term",
                moneyFull(weighted), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("Which is a relief of",
                String.format("%.0f%%", bank.weightingRelief() * 100), Palette.GOOD));

        column.getChildren().add(statementNote(String.format(
                "Sovereign paper does not default and short paper repays itself before you "
                + "have finished worrying about it, so a treasury bill ties up %.0f%% of "
                + "what a business loan of the same size does — and a loan with under %.0f "
                + "months to run weighs as little as %.0f%% of its face. A bank holds bills "
                + "INSTEAD of cash, which is the opposite of treating them as risk.",
                Bank.RISK_CITY * 100, Bank.LONG_TERM_MONTHS, Bank.SHORTEST_WEIGHT * 100)));

        /* ------------------------------- the ratio ------------------------------- */
        column.getChildren().add(statementHead("The ratio a regulator reads"));
        column.getChildren().add(statementLine("Equity", moneyFull(bank.equity()),
                bank.equity() < 0 ? Palette.BAD : null));
        column.getChildren().add(statementLine("Against the weighted book",
                weighted > 0 ? String.format("%.1f%%", Math.min(999, bank.capitalRatio()) * 100)
                             : "nothing lent",
                bank.capitalRatio() < Bank.CAPITAL_RATIO ? Palette.BAD
                        : bank.capitalRatio() < Bank.CAPITAL_RATIO * 1.5 ? Palette.WARN
                        : Palette.GOOD));
        column.getChildren().add(statementLine("Required",
                String.format("%.0f%%", Bank.CAPITAL_RATIO * 100), Palette.TEXT_MUTED));

        if (bank.recapitalisationNeeded() > 0) {
            column.getChildren().add(alert("It is under its required ratio",
                    String.format("%s of capital short. Until that is made good — by "
                    + "earning it or by being given it — the bank cannot lend against the "
                    + "book it already has, let alone a new one.",
                    money(bank.recapitalisationNeeded()))));
        }
    }

    int weightRow(javafx.scene.layout.GridPane table, int line,
                          String label, double face, double weight) {
        table.add(gridCell(label, Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
        table.add(gridCell(money(face), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, line);
        table.add(gridCell(String.format("%.0f%%", weight * 100),
                weight < 1 ? Palette.GOOD : Palette.TEXT_MUTED,
                Palette.SIZE_CAPTION, true), 2, line);
        table.add(gridCell("up to " + money(face * weight), Palette.TEXT_MUTED,
                Palette.SIZE_CAPTION, true), 3, line);
        return line + 1;
    }

    /**
     * One of the two limits, with the book drawn into it.
     *
     * EACH BAR IS ITS OWN LIMIT rather than both being drawn on a shared
     * scale, and that was the second attempt. A shared scale is the honest
     * picture of which limit is bigger - and on a real city it is a full-width
     * grey bar for the capital next to a two-pixel sliver for the deposits,
     * which answers a question nobody asked. The question is "has the book run
     * into this one", so the bar is the limit and the fill is the book.
     *
     * The fill runs past the end when it has, because a limit that has been
     * exceeded is exactly the state worth drawing.
     */
    VBox limitBar(String name, String how, double limit,
                          double book, boolean binds) {

        final double WIDE = STATEMENT - 60;
        double used = limit > 0 ? book / limit : (book > 0 ? 2 : 0);

        Label label = new Label(name + (binds ? "   \u2190 the binding one" : ""));
        label.setStyle(Palette.words(Palette.SIZE_BODY,
                binds ? Palette.WARN : Palette.TEXT_BODY)
                + (binds ? " -fx-font-weight: bold;" : ""));

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label figure = new Label(money(limit));
        figure.setStyle(Palette.figure(Palette.SIZE_BODY,
                binds ? Palette.WARN : Palette.TEXT_MUTED));

        HBox head = new HBox(Palette.GAP_TIGHT, label, gap, figure);
        head.setAlignment(Pos.CENTER_LEFT);
        head.setMaxWidth(STATEMENT);

        Region track = new Region();
        track.setPrefSize(WIDE, 14);
        track.setMinSize(WIDE, 14);
        track.setMaxSize(WIDE, 14);
        track.setStyle("-fx-background-color: " + Palette.CONTROL
                + "; -fx-background-radius: 3;");

        // MAX SIZE AS WELL AS PREF. A StackPane stretches its children to its
        // own width unless they refuse, and a Region that only sets prefWidth
        // does not refuse - which drew every bar full-width whatever it held.
        double w = Math.max(2, Math.min(1, used) * WIDE);
        Region fill = new Region();
        fill.setPrefSize(w, 14);
        fill.setMinSize(w, 14);
        fill.setMaxSize(w, 14);
        fill.setStyle("-fx-background-color: "
                + (used > 1 ? Palette.BAD : used > Bank.EASY_STRAIN ? Palette.WARN : Palette.GOOD)
                + "; -fx-background-radius: 3;");
        Tooltip.install(fill, new Tooltip(String.format(
                "the weighted book, %s, is %.0f%% of this limit", moneyFull(book), used * 100)));

        StackPane bar = new StackPane(track, fill);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        bar.setMaxWidth(WIDE);
        bar.setAlignment(Pos.CENTER_LEFT);

        Label caption = new Label(String.format("%s   \u00b7   the book uses %.0f%% of it%s",
                how, used * 100, used > 1 ? " \u2014 it is past this one" : ""));
        caption.setStyle(Palette.words(Palette.SIZE_CAPTION,
                used > 1 ? Palette.BAD : Palette.TEXT_LABEL));

        VBox box = new VBox(2, head, bar, caption);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 6 0 12 0;");
        return box;
    }

    /* =====================================================================
       ANOTHER BRANCH

       The one decision this screen exists to inform, and the one the old
       screen could not: a branch is worth building when it buys capacity, and
       it buys nothing at all when capital is what binds. The model already
       knows the difference - capacityWith() and bookAnotherBranchWouldCarry()
       are what the advisor uses - and the player could not see either.
       ===================================================================== */

    void bankBranchPage(VBox column) {

        Bank bank = ui.game.getBank();
        BuildingsTemplate branch = ui.game.getBuildingManager()
                .getTemplateByName("Commercial Bank");

        double now = bank.capacity();
        double after = bank.capacityWith(bank.getBranches() + 1);
        double buys = Math.max(0, after - now);
        double carries = bank.bookAnotherBranchWouldCarry();

        column.getChildren().add(statementHead("What one more counter would buy"));

        column.getChildren().add(statementLine("Branches standing",
                formatter.format(Math.round(bank.getBranches()))));
        column.getChildren().add(statementLine("It can carry now", moneyFull(now)));
        column.getChildren().add(statementLine("...and with one more",
                moneyFull(after), buys > 0 ? Palette.GOOD : Palette.TEXT_SPENT));
        column.getChildren().add(statementTotal("Which buys",
                buys > 0 ? moneyFull(buys) + " of capacity" : "nothing at all",
                buys > 0 ? Palette.GOOD : Palette.BAD));

        if (branch != null) {
            column.getChildren().add(statementLine("What it costs to build",
                    moneyFull(branch.getCashCost())));
            column.getChildren().add(statementLine("...and to run",
                    moneyFull(branch.getUpkeep()) + " a month", Palette.TEXT_MUTED));
            column.getChildren().add(statementLine("Shareholders also put in",
                    moneyFull(Bank.PAID_IN_PER_BRANCH), Palette.GOOD));
            column.getChildren().add(statementNote(
                    "Opening a branch is an equity injection as well as a building, which "
                    + "is what incorporating a bank actually is — and it is why the first "
                    + "branch in a city is worth building at all. A bank with no capital "
                    + "has no capacity, and with no capacity it can never earn any."));
        }

        /* ------------------------------ the verdict ------------------------------ */
        column.getChildren().add(statementHead("Is it worth it"));

        if (bank.isInsolvent()) {
            column.getChildren().add(alert("Not while the bank is frozen",
                    "A failed bank may not lend, so its capacity is nothing and another "
                    + "counter changes nothing. What it needs is capital. Left to itself an "
                    + "advisor once built two thousand branches trying to fix this."));
        } else if (buys <= 0) {
            column.getChildren().add(alert("A counter cannot fix a shortage of savings",
                    "The branches already standing can reach every dollar the city has "
                    + "banked, so another one gathers nothing and adds no capacity. What "
                    + "raises the ceiling from here is the city saving more, or the bank "
                    + "earning more capital."));
        } else if (carries <= 0) {
            column.getChildren().add(sentence(String.format(
                    "It would add %s of capacity, and the bank is not short of any. Nothing "
                    + "is spilling over, so nothing is waiting for the room — build one "
                    + "when the strain is near %.0f%%, not before.",
                    money(buys), Bank.BUILD_AT_STRAIN * 100), Palette.TEXT_MUTED));
        } else {
            column.getChildren().add(sentence(String.format(
                    "Yes. %s of the book is spilling past what this bank can comfortably "
                    + "carry, and a new counter would take %s of it onto the bank's own "
                    + "account instead of the market's. The private sector starts building "
                    + "at %.0f%% strain on purpose — a branch takes months to put up, and "
                    + "waiting for the premium means paying it for the whole of the lead "
                    + "time.",
                    money(Math.max(0, bank.getWeightedBook() - now * Bank.EASY_STRAIN)),
                    money(carries), Bank.BUILD_AT_STRAIN * 100),
                    Palette.GOOD));
        }

        if (bank.wantsBranch()) {
            column.getChildren().add(statementNote(
                    "The city's own investors think so too — this is the test they use, and "
                    + "it is currently passing. A Commercial Bank is in the Build tab under "
                    + "Commercial."));
        }
    }

    /* =====================================================================
       WHO OWES IT
       ===================================================================== */

    void bankBorrowerPage(VBox column) {

        Bank bank = ui.game.getBank();
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();

        double sector = bank.getSectorBook();
        double city = bank.getCityBook();
        double families = bank.getHouseholdBook();
        double carry = bank.getCarryBook();
        double all = sector + city + families + carry;

        column.getChildren().add(statementHead("Who owes the bank money"));

        if (all <= 0) {
            column.getChildren().add(sentence("Nothing is lent out.", Palette.TEXT_MUTED));
            return;
        }

        java.util.List<Slice> split = new java.util.ArrayList<>();
        if (sector > 0)   split.add(new Slice("The businesses", sector, Palette.LADDER[1]));
        if (city > 0)     split.add(new Slice("The treasury", city, Palette.LADDER[0]));
        if (families > 0) split.add(new Slice("The families", families, Palette.SPENDING_RAMP[2]));
        if (carry > 0)    split.add(new Slice("The carry trade", carry, Palette.LADDER[3]));
        column.getChildren().add(stackedBar(split, STATEMENT - 40));

        column.getChildren().add(statementLine("The businesses", moneyFull(sector)));
        column.getChildren().add(statementLine("The treasury", moneyFull(city)));
        column.getChildren().add(statementLine("The families", moneyFull(families)));
        column.getChildren().add(statementLine("The carry trade", moneyFull(carry)));
        column.getChildren().add(statementTotal("On the book", moneyFull(all),
                Palette.TEXT_HEAD));

        column.getChildren().add(statementNote(
                "The treasury's share is the city's own bonds — the bank is what buys them, "
                + "which is why a bond programme takes room the shops and mills were going "
                + "to borrow. It is also the cheapest thing on this list to hold.\n\n"
                + "The carry trade is foreigners. When the bank lends cheaper than the "
                + "world pays, it is worth borrowing here and holding the money abroad, "
                + "and they do. They take what is left after everyone who lives here has "
                + "borrowed, they pay the same rate a business does, and the currency they "
                + "sell on the way out is what keeps a trade surplus from lifting the rate "
                + "forever. They also leave when the gap closes."));

        /* -------------------------- the businesses, split -------------------------- */
        column.getChildren().add(statementHead("The businesses, one by one"));

        javafx.scene.layout.GridPane t = grid(
                new double[] {150, 112, 86, 86, 100}, rightAfterFirst(5));
        gridHead(t, "", "owed", "rate", "leverage", "loans");

        int line = 1;
        for (String name : Sectors.KEYS) {
            double owed = credit.getPrincipal(name);
            if (owed <= 0 && !credit.isBorrowingBlocked(name)) continue;

            boolean blocked = credit.isBorrowingBlocked(name);
            t.add(gridCell(name, blocked ? Palette.BAD : Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            t.add(gridCell(money(owed), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, line);
            t.add(gridCell(String.format("%.2f%%", credit.getRate(name) * 100),
                    Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
            t.add(gridCell(String.format("%.2fx", credit.getLeverage(name)),
                    credit.getLeverage(name) > 2 ? Palette.WARN : Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 3, line);
            t.add(gridCell(blocked ? "BLOCKED" : String.valueOf(credit.getLoanCount(name)),
                    blocked ? Palette.BAD : Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        column.getChildren().add(t);

        column.getChildren().add(statementNote(String.format(
                "Each sector is priced on its own leverage, over a risk-free rate of "
                + "%.2f%%. A business that has borrowed against everything it owns pays "
                + "for it, and one that has stopped paying is refused — the In trouble "
                + "page is where that shows.", credit.getRiskFreeRate() * 100)));

        /* --------------------------- and the families --------------------------- */
        if (families > 0) {
            HouseholdBalance homes = ui.game.getHouseholdBalance();
            column.getChildren().add(statementHead("The families"));
            column.getChildren().add(statementLine("Owed altogether", moneyFull(families)));
            column.getChildren().add(statementLine("Borrowed this month",
                    moneyFull(bank.getLentToHouseholds()), Palette.WARN));
            column.getChildren().add(statementLine("Repaid this month",
                    moneyFull(bank.getRepaidByHouseholds()), Palette.GOOD));
            if (homes != null && homes.getWrittenOff() > 0) {
                column.getChildren().add(statementLine("Discharged in bankruptcy",
                        moneyFull(homes.getWrittenOff()), Palette.BAD));
            }
            column.getChildren().add(statementNote(
                    "Household credit is revolving — a family that cannot meet its fixed "
                    + "costs draws on it, and one that never catches up is discharged. It "
                    + "weighs its full face against the bank's capital, because a family "
                    + "can be discharged and the money does not come back."));
        }
    }

    /* --------------------------------------------------------------------- */

    void bankTroublePage(VBox column) {

        Bank bank = ui.game.getBank();
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();

        column.getChildren().add(statementHead("What the bank is losing"));

        column.getChildren().add(statementLine("Written off this month",
                moneyFull(bank.getWriteOffs()),
                bank.getWriteOffs() > 0 ? Palette.BAD : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("Written off since the city began",
                moneyFull(credit.getTotalWrittenOff()), Palette.TEXT_MUTED));

        column.getChildren().add(statementNote(
                "A write-off costs the book, not the cash — the money left months ago when "
                + "the loan was made. What it takes is equity, and equity is what decides "
                + "how much the bank may lend. That is how a run of bad loans becomes a "
                + "credit crunch rather than a bad month."));

        /* ------------------------------ by borrower ------------------------------ */
        boolean any = false;
        javafx.scene.layout.GridPane t = grid(
                new double[] {150, 106, 106, 100, 92}, rightAfterFirst(5));
        gridHead(t, "", "this month", "in total", "restructured", "status");

        int line = 1;
        for (String name : Sectors.KEYS) {
            double month = credit.getWrittenOffThisMonth(name);
            double total = credit.getWrittenOffTotal(name);
            boolean blocked = credit.isBorrowingBlocked(name);
            if (month <= 0 && total <= 0 && !blocked) continue;
            any = true;

            t.add(gridCell(name, blocked ? Palette.BAD : Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            t.add(gridCell(month > 0 ? money(month) : "—",
                    month > 0 ? Palette.BAD : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 1, line);
            t.add(gridCell(total > 0 ? money(total) : "—", Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 2, line);
            t.add(gridCell(String.valueOf(credit.getRestructureCount(name)),
                    credit.getRestructureCount(name) > 0 ? Palette.WARN : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 3, line);
            t.add(gridCell(blocked
                            ? credit.getBlockedMonths(name) + "mo shut"
                            : "borrowing",
                    blocked ? Palette.BAD : Palette.GOOD,
                    Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }

        if (any) {
            column.getChildren().add(statementHead("Who has stopped paying"));
            column.getChildren().add(t);
            column.getChildren().add(statementNote(
                    "A sector that has been restructured is one whose loans were written "
                    + "down rather than repaid. It is shut out of new borrowing for a "
                    + "while afterwards, which is the bank refusing rather than the "
                    + "business declining."));
        } else {
            column.getChildren().add(sentence(
                    "Nobody is in arrears and nobody is shut out. Every sector that wants "
                    + "to borrow can.", Palette.GOOD));
        }

        /* ---------------------------- and if it failed ---------------------------- */
        if (bank.getFailures() > 0) {
            column.getChildren().add(statementHead("When the bank itself failed"));
            column.getChildren().add(statementLine("Times it has failed",
                    String.valueOf(bank.getFailures()), Palette.BAD));
            column.getChildren().add(statementLine("Its creditors absorbed",
                    moneyFull(bank.getResolutionLoss()), Palette.BAD));
            // TODO(docs): since 0.7.0 the bank's wholesale lender is the central
            // bank's window, not creditors outside the city; the loss is still
            // booked as crossing the edge (MoneyAudit's "+ bank ResolutionLoss").
            // Who absorbs a failed bank now is Jerus's open question, and this
            // sentence follows his answer.
            column.getChildren().add(statementNote(
                    "When a bank loses more than it owns, somebody eats the hole. Here it "
                    + "is the wholesale creditors — who are outside the city, so the loss "
                    + "genuinely crosses its edge. The bank comes out owning nothing, owing "
                    + "nothing, and unable to lend until it has capital again."));
        }
    }

    /* =====================================================================
       WHERE THE MONEY COMES FROM

       The half of a bank nobody thinks about until it is gone. It lends the
       city's own savings, levered - and it can only lend savings it can
       REACH, which is what branches are for and what the old screen said in
       one grey memo line at the bottom of the balance sheet.
       ===================================================================== */

    void bankDepositPage(VBox column) {

        Bank bank = ui.game.getBank();

        double held = bank.getDeposits();
        double gathered = bank.depositsGathered();
        double foreign = bank.getForeignDeposits();
        double reach = bank.getBranches() * Bank.DEPOSITS_PER_BRANCH;

        column.getChildren().add(statementHead("What the city has banked"));

        if (held <= 0) {
            column.getChildren().add(sentence(
                    "Nobody has banked anything yet.", Palette.TEXT_MUTED));
            return;
        }

        /* ----------------------- reached, and out of reach ----------------------- */
        double local = Math.max(0, held - foreign);
        double reached = Math.min(local, reach);
        double beyond = Math.max(0, local - reached);

        java.util.List<Slice> split = new java.util.ArrayList<>();
        if (reached > 0) split.add(new Slice("Reached by the branches", reached,
                Palette.LADDER[1]));
        if (foreign > 0) split.add(new Slice("Foreign money", foreign,
                Palette.SPENDING_RAMP[3]));
        if (beyond > 0)  split.add(new Slice("Beyond their reach", beyond,
                Palette.RAMP_REST));
        column.getChildren().add(stackedBar(split, STATEMENT - 40));

        column.getChildren().add(statementLine("Banked in the city", moneyFull(local)));
        column.getChildren().add(statementLine("...which the branches can reach",
                moneyFull(reached), Palette.GOOD));
        if (beyond > 0) {
            column.getChildren().add(statementLine("...and which they cannot",
                    moneyFull(beyond), Palette.WARN));
        }
        if (foreign > 0) {
            column.getChildren().add(statementLine("Foreign money parked here",
                    moneyFull(foreign), Palette.WARN));
        }
        column.getChildren().add(statementTotal("It can fund itself with",
                moneyFull(gathered), Palette.TEXT_HEAD));

        column.getChildren().add(statementNote(String.format(
                "One branch reaches %s of a city's savings. A bank cannot fund itself with "
                + "money kept in somebody's mattress, and %s of branches is what stands "
                + "between this bank and the rest of it.",
                money(Bank.DEPOSITS_PER_BRANCH),
                formatter.format(Math.round(bank.getBranches())))));

        /* -------------------------------- whose -------------------------------- */
        column.getChildren().add(statementHead("Whose money it is"));
        column.getChildren().add(statementLine("Paid to savers this month",
                moneyFull(bank.depositInterest()), Palette.WARN));
        column.getChildren().add(statementLine("...to the families",
                moneyFull(bank.getDepositInterestToHouseholds()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("...to the businesses",
                moneyFull(bank.getDepositInterestToSectors()), Palette.TEXT_MUTED));
        if (foreign > 0) {
            column.getChildren().add(statementLine("...and abroad",
                    moneyFull(bank.getDepositInterestToForeign()), Palette.TEXT_MUTED));
        }
        column.getChildren().add(statementLine("At a deposit rate of",
                String.format("%.2f%%", bank.depositRate() * 100), Palette.ACCENT));
        column.getChildren().add(statementNote(String.format(
                "Savers get %.0f%% of what the bank can earn on the money. Paying for "
                + "deposits is what turns the gap between the two rates into a margin — "
                + "without it the bank is a shoebox with a licence, and household savings "
                + "never compound.", Bank.DEPOSIT_PASS_THROUGH * 100)));

        /* ------------------------------- hot money ------------------------------- */
        if (foreign > 0) {
            double share = bank.hotFundingShare();
            column.getChildren().add(alert(
                    share > .25 ? "A quarter of this bank's funding can leave tomorrow"
                                : "Some of this funding is foreign",
                    String.format("%s of what the bank funds itself with is money from "
                    + "abroad — %.0f%% of the total — and it is here because the rate is "
                    + "good, not because anybody banks here. It leaves on no notice, and "
                    + "when it does the bank must find the cash whether it has it or not. "
                    + "That is what a sudden stop is, and the Trade tab is where it is "
                    + "priced.", money(foreign), share * 100)));
        }
    }

    /* --------------------------------------------------------------------- */

    void bankFundingPage(VBox column) {

        Bank bank = ui.game.getBank();

        double borrowed = bank.borrowings();
        double cheap = bank.depositFunding();
        double dear = bank.wholesaleFunding();

        column.getChildren().add(statementHead("What it had to go and borrow"));

        double policy = ui.game.getDebtManager().getPolicyRate();

        if (borrowed <= 0) {
            column.getChildren().add(sentence(
                    "The bank owes nothing. What it has not lent is its reserves at the "
                    + "central bank, earning the policy rate — which is the least it will "
                    + "lend at, because it can earn that by doing nothing.", Palette.GOOD));
            column.getChildren().add(statementLine("Reserves at the central bank",
                    moneyFull(bank.cashReserves()), Palette.GOOD));
            column.getChildren().add(statementLine("Earning",
                    String.format("%.2f%% a year, the policy rate", policy * 100),
                    Palette.TEXT_HEAD));
            column.getChildren().add(statementLine("Which paid it this month",
                    moneyFull(bank.getPlacementIncome()), Palette.GOOD));
            return;
        }

        java.util.List<Slice> split = new java.util.ArrayList<>();
        if (cheap > 0) split.add(new Slice("Deposits", cheap, Palette.LADDER[1]));
        if (dear > 0)  split.add(new Slice("The window", dear, Palette.BAD));
        column.getChildren().add(stackedBar(split, STATEMENT - 40));

        column.getChildren().add(statementLine("Funded by deposits", moneyFull(cheap),
                Palette.GOOD));
        column.getChildren().add(statementLine("Borrowed at the central bank's window",
                moneyFull(dear), dear > 0 ? Palette.BAD : Palette.TEXT_SPENT));
        column.getChildren().add(statementTotal("Everything it owes",
                moneyFull(borrowed), Palette.TEXT_HEAD));

        column.getChildren().add(statementLine("The window charges",
                String.format("%.2f%% a year", bank.fundingRate() * 100), Palette.WARN));
        column.getChildren().add(statementLine("Which cost it this month",
                moneyFull(bank.getFundingCost()), Palette.WARN));

        column.getChildren().add(sentence(String.format(
                "Past what its branches can gather, the bank borrows from the central "
                + "bank at the policy rate plus a %.0f-point penalty, so a dollar of reserves "
                + "always costs more to borrow than it earns to hold. The window is not "
                + "rationed: what stops the bank lending more is the strain premium it has "
                + "to charge and the capital it has to hold, not the money.",
                CentralBank.WINDOW_PENALTY * 100),
                Palette.TEXT_BODY));
    }

    /* =====================================================================
       THE BOOKS
       ===================================================================== */

    void bankIncomePage(VBox column) {

        Bank bank = ui.game.getBank();
        DebtManager market = ui.game.getDebtManager();

        column.getChildren().add(statementHead("What it made this month"));

        /* --------------------- interest, opened by borrower --------------------- */
        VBox who = new VBox(0);
        who.getChildren().addAll(
                payerRow("The businesses", bank.getSectorBook(), bank.getBook(), "on the book"),
                payerRow("The treasury", bank.getCityBook(), bank.getBook(), "on the book"),
                payerRow("The families", bank.getHouseholdBook(), bank.getBook(), "on the book"),
                payerRow("The carry trade", bank.getCarryBook(), bank.getBook(), "on the book"));
        who.getChildren().add(statementNote(
                "The books those rates are charged on. Interest is collected on all four "
                + "at once, so it cannot be split further than this without inventing a "
                + "figure the model does not keep."));

        column.getChildren().add(statementDisclosure("Interest earned",
                moneyFull(bank.interestIncome()), who, "on what"));

        column.getChildren().add(statementLine("Paid to savers",
                "−" + moneyFull(bank.depositInterest()), Palette.WARN));
        column.getChildren().add(statementLine("Paid at the central bank's window",
                "−" + moneyFull(bank.getFundingCost()), Palette.WARN));
        column.getChildren().add(statementTotal("NET INTEREST INCOME",
                moneyFull(bank.netInterestIncome()),
                bank.netInterestIncome() < 0 ? Palette.BAD : Palette.TEXT_HEAD));

        column.getChildren().add(statementLine("Loans that died",
                "−" + moneyFull(bank.getWriteOffs()),
                bank.getWriteOffs() > 0 ? Palette.BAD : Palette.TEXT_SPENT));
        /*
         * THE TRADING DESK, since the exchange (2026-09-11). The bank makes
         * the market in the city's shares; what that made or lost this month
         * - the spread it earned, the dividends on what it holds, and the
         * change in what its inventory is marked at - is income beside the
         * interest. Opened to show the desk's own book. See Exchange.
         *
         * AND IT FOOTS (2026-09-18). Jerus: "the bank, just explain to me the
         * trading desk, cause a bunch of times it's losing billions of dollars
         * due to the trading desk." The opened lines did not add up to the
         * figure above them, because the biggest term - the re-mark of what
         * the desk holds, which Bank.markSecurities() adds to the trading
         * result - was not among them. It is now, from Bank.getMarkChange(),
         * so the lines sum to the total exactly (BankCheck asserts it), and
         * when the re-mark is the bulk of a loss the note says why.
         */
        VBox desk = new VBox(0);
        Exchange exchange = ui.game.getExchange();
        Equity register = ui.game.getEquity();
        double reMark = bank.getMarkChange();
        desk.getChildren().add(statementLine("Sold to the households",
                moneyFull(exchange.getSoldToHouseholds()), Palette.TEXT_MUTED));
        desk.getChildren().add(statementLine("Sold abroad",
                moneyFull(exchange.getSoldAbroad()), Palette.TEXT_MUTED));
        desk.getChildren().add(statementLine("Bought from the households",
                "−" + moneyFull(exchange.getBoughtFromHouseholds()), Palette.TEXT_MUTED));
        desk.getChildren().add(statementLine("Bought from abroad",
                "−" + moneyFull(exchange.getBoughtFromAbroad()), Palette.TEXT_MUTED));
        if (exchange.getEmigrantsPaid() > 0) {
            desk.getChildren().add(statementLine("...of which from families leaving the city",
                    moneyFull(exchange.getEmigrantsPaid()), Palette.TEXT_MUTED));
        }
        desk.getChildren().add(statementLine("Dividends on what it holds",
                moneyFull(register.getDividendDeskThisMonth()), Palette.TEXT_MUTED));
        desk.getChildren().add(statementLine("Tendered into buybacks",
                moneyFull(exchange.getBuybackToDesk()), Palette.TEXT_MUTED));
        // The line that makes the rest add up: what re-marking the inventory
        // did to the result. Signed, because it is the one line here that
        // can go either way.
        desk.getChildren().add(statementLine("Re-marked what it holds",
                (reMark < 0 ? "−" : "") + moneyFull(Math.abs(reMark)),
                reMark < 0 ? Palette.WARN : Palette.TEXT_MUTED));
        desk.getChildren().add(statementLine("What it holds, at the mark",
                moneyFull(bank.getSecurities()), Palette.TEXT_MUTED));
        StringBuilder positions = new StringBuilder();
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            double held = register.getDealerShares(c);
            if (held <= 0 || register.getShares(c) <= 0) continue;
            if (positions.length() > 0) positions.append(", ");
            positions.append(String.format("%s %.1f%% of the company at %s",
                    Equity.COMPANIES[c], held / register.getShares(c) * 100,
                    tightMoney(toDollars(exchange.mark(c)), false)));
        }
        desk.getChildren().add(statementNote(positions.length() == 0
                ? "The desk holds nothing. It quotes every company round what the register says a share is"
                  + " worth, buys what comes and sells what it has - never what it does not."
                : "On the desk: " + positions + ". Carried at the quote or the register's value, whichever"
                  + " is lower - the desk does not mark its own book up on a quote nobody has paid yet."));
        /*
         * WHEN THE RE-MARK IS THE LOSS, say why in one sentence. "Bulk" is
         * half or more of a losing month; below that the loss is the
         * trading, and the lines above already say so.
         */
        if (bank.getTradingIncome() < 0 && reMark <= bank.getTradingIncome() / 2) {
            desk.getChildren().add(statementNote(String.format(
                    "%s of the %s lost is the re-mark, not the trading. The desk carries what it holds"
                    + " at the quote or the register's value, whichever is lower, so shares bought at a"
                    + " quote above that value are marked down the day they are bought, and a company"
                    + " whose value falls marks down everything the desk holds in it.",
                    moneyFull(-reMark), moneyFull(-bank.getTradingIncome()))));
        }
        column.getChildren().add(statementDisclosure("The trading desk",
                (bank.getTradingIncome() >= 0 ? "" : "−") + moneyFull(Math.abs(bank.getTradingIncome())),
                desk, "what it did"));
        // ...and the city's paper that changed hands (0.7.1): bought from the
        // households at the desk, sold to or bought from the central bank. The
        // gain or loss against what the book carried the paper at.
        if (Math.abs(bank.getPaperGains()) > 1e-9) {
            column.getChildren().add(statementLine("Gains on the city's paper that changed hands",
                    (bank.getPaperGains() >= 0 ? "" : "−") + moneyFull(Math.abs(bank.getPaperGains())),
                    bank.getPaperGains() >= 0 ? Palette.GOOD : Palette.WARN));
        }
        column.getChildren().add(statementLine("Staff and premises",
                "−" + moneyFull(bank.operatingExpenses()), Palette.WARN));
        column.getChildren().add(statementTotal("PROFIT BEFORE TAX",
                moneyFull(bank.profitBeforeTax()),
                bank.profitBeforeTax() < 0 ? Palette.BAD : Palette.TEXT_HEAD));

        column.getChildren().add(statementLine("Tax",
                "−" + moneyFull(bank.getTaxPaid()), Palette.WARN));
        column.getChildren().add(statementTotal("WHAT IT KEPT",
                moneyFull(bank.getNetIncome()),
                bank.getNetIncome() < 0 ? Palette.BAD : Palette.GOOD));

        /* ------------------------------ the margin ------------------------------ */
        column.getChildren().add(statementHead("The business, in one line"));
        column.getChildren().add(statementLine("It charges",
                String.format("%.2f%%", market.getRate() * 100), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("It pays savers",
                String.format("%.2f%%", bank.depositRate() * 100), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("Net interest margin",
                String.format("%.2f%% a year on the book", bank.netInterestMargin() * 100),
                bank.netInterestMargin() < 0 ? Palette.BAD : Palette.GOOD));

        column.getChildren().add(statementNote(String.format(
                "Tax is charged in arrears, on last month's %s of profit — the city's take "
                + "is struck before the bank knows what it made, and paying it a month "
                + "late is the only ordering that keeps the money in one channel.",
                money(bank.getProfitLastMonth()))));

        column.getChildren().add(statementNote(
                "A write-off costs the book, not the cash. The money left months ago when "
                + "the loan was made; what a default takes today is equity."));
    }

    /* --------------------------------------------------------------------- */

    void bankBalancePage(VBox column) {

        Bank bank = ui.game.getBank();

        column.getChildren().add(statementHead("What it is standing on"));

        column.getChildren().add(statementLine("Reserves at the central bank",
                moneyFull(bank.cashReserves())));
        column.getChildren().add(statementLine("Loans",
                moneyFull(bank.getBook())));
        column.getChildren().add(statementLine("Shares on the trading desk, at the mark",
                moneyFull(Math.max(0, bank.getSecurities()))));
        column.getChildren().add(statementTotal("TOTAL ASSETS",
                moneyFull(bank.totalAssets()), Palette.TEXT_HEAD));

        column.getChildren().add(statementLine("Deposits",
                "−" + moneyFull(bank.depositFunding()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("Owed at the central bank's window",
                "−" + moneyFull(bank.wholesaleFunding()), Palette.TEXT_MUTED));
        if (bank.getForeignDeposits() > 0) {
            column.getChildren().add(statementLine("Foreign deposits",
                    "−" + moneyFull(bank.getForeignDeposits()), Palette.WARN));
        }
        // The discount on the city's paper it has not yet earned (0.7.1): it
        // bought under face, and earns the difference a month at a time.
        if (bank.getUnearnedDiscount() > 0) {
            column.getChildren().add(statementLine("Discount on the city's paper, not yet earned",
                    "−" + moneyFull(bank.getUnearnedDiscount()), Palette.TEXT_MUTED));
        }
        column.getChildren().add(statementTotal("TOTAL LIABILITIES",
                moneyFull(bank.totalLiabilities()), Palette.TEXT_HEAD));

        column.getChildren().add(statementTotal("EQUITY", moneyFull(bank.equity()),
                bank.isInsolvent() ? Palette.BAD : Palette.GOOD));

        if (bank.getWeightedBook() > 0) {
            column.getChildren().add(statementLine("Capital ratio",
                    String.format("%.1f%% against a required %.0f%%",
                            Math.min(999, bank.capitalRatio()) * 100,
                            Bank.CAPITAL_RATIO * 100),
                    bank.capitalRatio() < Bank.CAPITAL_RATIO ? Palette.BAD : Palette.GOOD));
        }

        /* ------------------------- and how equity moved ------------------------- */
        column.getChildren().add(statementHead("How the equity moved"));

        double putIn = bank.getCapitalInjected() + bank.getCapitalFromHome()
                + bank.getBailoutReceived();

        column.getChildren().add(statementLine("At the start of the month",
                moneyFull(bank.getOpeningEquity())));
        column.getChildren().add(statementLine("What it kept",
                (bank.getNetIncome() >= 0 ? "+" : "−")
                        + moneyFull(Math.abs(bank.getNetIncome())),
                bank.getNetIncome() < 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(statementLine("Capital put in",
                putIn > 0 ? "+" + moneyFull(putIn) : "$0",
                putIn > 0 ? Palette.GOOD : Palette.TEXT_SPENT));
        if (bank.getDividendsPaid() > 0) {
            column.getChildren().add(statementLine("Paid to its shareholders",
                    "−" + moneyFull(bank.getDividendsPaid()), Palette.WARN));
        }
        /*
         * A FAILED BANK'S MONTH: its creditors absorb the shortfall, which
         * lifts the equity back to nothing - see Bank.resolveIfFailed(). Not
         * income and not capital, so it has its own line; without one, every
         * month in resolution printed the whole loss as "Not accounted for"
         * and raised the alarm below it. Seen on the PC, 2026-09-11.
         */
        double absorbed = bank.getResolutionLossThisMonth();
        if (absorbed > 0) {
            column.getChildren().add(statementLine("Absorbed by its creditors",
                    "+" + moneyFull(absorbed), Palette.BAD));
        }

        double moved = bank.equity() - bank.getOpeningEquity();
        double gap = moved - bank.getNetIncome() - putIn + bank.getDividendsPaid() - absorbed;
        if (Math.abs(gap) > .005) {
            column.getChildren().add(statementLine("Not accounted for",
                    (gap >= 0 ? "+" : "−") + moneyFull(Math.abs(gap)), Palette.BAD));
        }
        column.getChildren().add(statementTotal("At the end of it",
                moneyFull(bank.equity()),
                bank.isInsolvent() ? Palette.BAD : Palette.TEXT_HEAD));

        // the sector screen's block, borrowed on purpose: it reads the game and is
        // not a pure piece, so it stays the sector screen's until a third screen wants it
        ui.sectorScreen.ownersBlock(column, Equity.BANK, bank.equity(), bank.getNetIncome());

        if (Math.abs(gap) > .005) {
            column.getChildren().add(alert("Something moved the book without telling the bank",
                    String.format("%s of the month's change in equity is not net income and "
                    + "is not capital. That should be impossible — equity moves by what the "
                    + "bank earned and what it was given, and by nothing else. Worth "
                    + "reporting.", money(Math.abs(gap)))));
        } else {
            column.getChildren().add(statementNote(
                    "Equity moves by what the bank earned and what it was given, and by "
                    + "nothing else. The line above is printed even when it is zero, "
                    + "because a plug nobody checks is a lie."));
        }

        /* ============================ AND IF IT FAILED ============================ */
        if (bank.isInsolvent()) {

            column.getChildren().add(statementHead("The bank has failed"));
            column.getChildren().add(sentence(
                    "It has lost more than it owns, so it may lend nothing and every "
                    + "borrower in the city is paying the full premium. It can earn its way "
                    + "back out on the book it already has — retained profit is capital "
                    + "like any other — or the city can put capital in and lift the freeze "
                    + "today.", Palette.BAD_TEXT));

            column.getChildren().add(bankRescue());
        }
    }

    /* =====================================================================
       THE RESCUE, WHEREVER THE PLAYER IS LOOKING.

       Jerus: "when the bank has an issue, and you click go to bank, the
       recapitalise the bank button is quite hidden, make it so that in the bank
       section its on the top, not all the way hidden in the balance sheet."

       He is right, and the inbox made it worse rather than better: the notice
       says "Go to the bank", its button lands on the bank's landing page, and
       the landing page then said the button was on the balance sheet. Three
       screens to press one button, and the last hop was a sentence rather than
       a link.

       So this is ONE block used in TWO places - the top of the landing, where a
       failed bank is the only thing on that screen worth reading, and the
       balance sheet, where it is the end of the argument the statement has just
       made. Two copies of a button that moves money is two places for the guard
       on the treasury's cash to drift apart, and that guard is the whole safety
       of it.
       ===================================================================== */
    VBox bankRescue() {

        Bank bank = ui.game.getBank();
        double needed = bank.recapitalisationNeeded();
        double cash = ui.game.getCash();

        VBox block = new VBox(0);
        block.setMaxWidth(Region.USE_PREF_SIZE);

        block.getChildren().add(statementLine("To put it back at its ratio",
                moneyFull(needed), Palette.BAD));
        block.getChildren().add(statementLine("The treasury holds",
                moneyFull(cash), cash < needed ? Palette.BAD : Palette.GOOD));

        Button rescue = new Button("Recapitalise the bank — " + money(needed));
        rescue.setDisable(cash < needed);
        if (cash >= needed) {
            rescue.setStyle("-fx-background-color: #2f7d52; -fx-text-fill: white;"
                    + " -fx-padding: 8 18 8 18;");
        }
        rescue.setOnAction(e -> {
            ui.game.recapitaliseBank(needed);
            ui.innerScrollAt.remove("showBankMenu:body");
            showBankMenu();
        });

        HBox act = new HBox(rescue);
        act.setAlignment(Pos.CENTER_LEFT);
        act.setStyle("-fx-padding: 10 0 4 0;");
        block.getChildren().add(act);

        if (cash < needed) {
            block.getChildren().add(statementNote(
                    "The treasury cannot cover it today, so the button is dead until it "
                    + "can. The bank can still earn its way back out on the book it "
                    + "already has — retained profit is capital like any other — which is "
                    + "slower and costs the city nothing."));
        }

        /*
         * SAID OUT LOUD, because a player who borrows to do this is doing
         * something that looks free and is not. See Bank.receiveBailout().
         */
        block.getChildren().add(alert("Borrowing to do it has the bank capitalise itself",
                "The city borrows FROM this bank. Issuing paper to raise the rescue "
                + "money means the bank buys the bond, the cash comes back to it as "
                + "capital, and its balance sheet has grown on both sides without "
                + "anybody putting anything in. It works on the screen and it is not a "
                + "rescue."));
        return block;
    }

    /* =====================================================================
       ITS HISTORY

       HistorySave already kept the bank's premium, deposits, book, equity and
       write-offs; it now also keeps its capacity, its strain, its profit and
       its branch count, because those four are what make the first five mean
       anything. A book of $7.2B is a fact; a book of $7.2B against a capacity
       of $4.3B is a story.

       DRAWN HERE RATHER THAN LINKED TO THE REPORTS TAB on purpose. The big
       picker chart normalises every line to its own low-to-high so that
       unrelated series can share an axis - which is right there and wrong
       here, because the whole point of putting the book next to the capacity
       is that they are the same units and one is above the other.
       ===================================================================== */

    void bankHistoryPage(VBox column, String page) {

        HistorySave h = ui.game.getHistorySave();

        if (h.months() < 2) {
            column.getChildren().add(statementHead("Nothing to draw yet"));
            column.getChildren().add(sentence(String.format(
                    "The city has lived %d month%s and a line needs two points. Come back "
                    + "in a year.", h.months(), h.months() == 1 ? "" : "s"),
                    Palette.TEXT_MUTED));
            return;
        }

        switch (page) {
            case "Strain"  -> bankStrainHistory(column, h);
            case "Capital" -> bankCapitalHistory(column, h);
            default        -> bankLendingHistory(column, h);
        }
    }

    void bankLendingHistory(VBox column, HistorySave h) {

        double[] book = h.aligned("bankLent");
        double[] room = h.aligned("bankCapacity");
        double[] deposits = h.aligned("bankDeposits");

        column.getChildren().add(statementHead("What it has lent, against what it could"));
        column.getChildren().add(trendChart(
                new String[] {"Lent out", "What it could carry", "Deposits gathered"},
                new double[][] {book, room, deposits},
                new String[] {Palette.LADDER[2], Palette.GOOD, Palette.RAMP_REST}));
        column.getChildren().add(statementNote(
                "One scale, because all three are money. The months where the blue is "
                + "above the green are the months the city paid a premium on every loan "
                + "in it."));

        double[] branches = h.aligned("bankBranches");
        column.getChildren().add(statementHead("And how many counters it had"));
        column.getChildren().add(trendChart(
                new String[] {"Branches"},
                new double[][] {branches},
                new String[] {Palette.ACCENT}));
    }

    void bankStrainHistory(VBox column, HistorySave h) {

        double[] strain = h.aligned("bankStrain");
        double[] premium = h.aligned("bankPremium");

        column.getChildren().add(statementHead("How hard it has been working"));
        column.getChildren().add(trendChart(
                new String[] {"Book against capacity"},
                new double[][] {strain},
                new String[] {Palette.WARN}));
        column.getChildren().add(statementNote(String.format(
                "A ratio, not money: 1.00 is lending exactly what it can carry. The "
                + "premium starts at %.2f and is fully bitten at %.2f. Recorded to a "
                + "ceiling of 10, because a city with no bank has infinite strain and an "
                + "infinity flattens every other point into the axis.",
                Bank.EASY_STRAIN, Bank.HARD_STRAIN)));

        column.getChildren().add(statementHead("...and what that cost everybody"));
        column.getChildren().add(trendChart(
                new String[] {"Points on every rate in the city"},
                new double[][] {premium},
                new String[] {Palette.BAD}));

        /* ------------------------ how long it has hurt ------------------------ */
        int bitten = 0;
        double worst = 0;
        for (double p : premium) {
            if (Double.isNaN(p)) continue;
            if (p > 0) bitten++;
            worst = Math.max(worst, p);
        }
        column.getChildren().add(statementLine("Months with a premium",
                bitten + " of " + h.months(), bitten > 0 ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("The worst it got",
                String.format("%.1f points", worst * 100),
                worst > 0 ? Palette.BAD : Palette.GOOD));
    }

    void bankCapitalHistory(VBox column, HistorySave h) {

        double[] equity = h.aligned("bankEquity");
        double[] profit = h.aligned("bankProfit");
        double[] losses = h.aligned("bankWriteOffs");

        column.getChildren().add(statementHead("What it owns"));
        column.getChildren().add(trendChart(
                new String[] {"Equity"},
                new double[][] {equity},
                new String[] {Palette.GOOD}));
        column.getChildren().add(statementNote(
                "Equity is capacity: at the required ratio every dollar of it lets the "
                + "bank carry about twelve of risk-weighted book. A month where this line "
                + "falls is a month the city's whole credit ceiling came down with it."));

        column.getChildren().add(statementHead("What it earned, and what it lost"));
        column.getChildren().add(trendChart(
                new String[] {"Profit a month", "Written off"},
                new double[][] {profit, losses},
                new String[] {Palette.ACCENT, Palette.BAD}));

        int lossMonths = 0;
        for (double p : profit) {
            if (!Double.isNaN(p) && p < 0) lossMonths++;
        }
        column.getChildren().add(statementLine("Months it lost money",
                lossMonths + " of " + h.months(),
                lossMonths > 0 ? Palette.WARN : Palette.GOOD));
    }
}
