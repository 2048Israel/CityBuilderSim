package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import ham.citybuildersim.ui.Pieces.Slice;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
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
 * The government tab: the budget as two rings and a balance, what the
 * treasury actually did against the size of the economy, the two lists - who
 * pays what and what it spends, every revenue line opening into who paid it -
 * what the debt costs by the paper it is owed on, and the output.
 *
 * Split out of UserInterface on 2026-09-18: the four banners from THE
 * GOVERNMENT to WHAT THE DEBT COSTS exactly as they were, the shell's members
 * reached through ui. The rail keeps no place inside this tab; the one thing
 * the shell touches is govPage, which the income dome sets to Overview before
 * it opens the tab.
 */
final class GovernmentScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    GovernmentScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       THE GOVERNMENT.

       WHAT IT WAS: eleven revenue lines and six expenditure lines as padded
       Courier, a GDP block, and a growth block - all correct, all impossible to
       weigh. "Business Tax: $2,431" tells a player nothing without the two
       things it never said: what share of the budget that is, and which of the
       six sectors it came from.

       SO THE THREE QUESTIONS THIS TAB NOW ANSWERS:

         1. Is the city in surplus, and by how much - as money, as a share of
            revenue, and as a share of the economy.
         2. Where does the money come from and where does it go - as a shape you
            can see before you have read a single figure.
         3. WHO PAYS IT. Every revenue line opens into the sectors, tiers or
            categories that produced it, at that payer's own rate.

       EVERYTHING IS ALSO A SHARE OF ANNUAL GDP, because that is the only unit
       in which a budget can be compared with anything - another city, another
       year, or a rule of thumb.
       ===================================================================== */

    String govPage = "Overview";

    static final String[] GOV_PAGES =
            {"Overview", "Revenue", "Spending", "Output"};

    /* ---------------------------------------------------------------------
       ONE SLICE OF A DONUT, AND THE DONUT.
       --------------------------------------------------------------------- */

    /**
     * Slices, biggest first, with everything past the fifth folded into one.
     *
     * FIVE AND A REST, not nine. A ring with nine segments is a ring nobody
     * reads - the eye can hold about five areas at a glance, and past that the
     * small wedges are indistinguishable from each other and from the gaps.
     * The tail is not lost: it is one grey segment here and a full list on the
     * page behind this one.
     */
    java.util.List<Slice> topSlices(java.util.List<String> names,
                                            java.util.List<Double> amounts,
                                            String[] ramp) {

        java.util.List<Integer> order = new java.util.ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            if (amounts.get(i) > 0) order.add(i);
        }
        order.sort((a, b) -> Double.compare(amounts.get(b), amounts.get(a)));

        java.util.List<Slice> out = new java.util.ArrayList<>();
        double rest = 0;
        for (int rank = 0; rank < order.size(); rank++) {
            int i = order.get(rank);
            if (rank < ramp.length) {
                out.add(new Slice(names.get(i), amounts.get(i), ramp[rank]));
            } else {
                rest += amounts.get(i);
            }
        }
        if (rest > 0) out.add(new Slice("Everything else", rest, Palette.RAMP_REST));
        return out;
    }

    /**
     * A ring, with the total in the hole.
     *
     * AN ARC PER SLICE, stroked rather than filled, because a stroked open arc
     * IS a ring segment - no path arithmetic, no hole to cut out, and the
     * thickness is one property. Each segment is shortened by a degree and a
     * half so the ring reads as separate pieces rather than one banded circle;
     * that gap is the surface showing through, which is what keeps two adjacent
     * steps of the same hue from merging.
     */
    StackPane donut(java.util.List<Slice> slices, String centreTop,
                            String centreFigure, String centreTone, double size) {

        javafx.scene.layout.Pane ring = new javafx.scene.layout.Pane();
        ring.setPrefSize(size, size);
        ring.setMinSize(size, size);
        ring.setMaxSize(size, size);

        double total = 0;
        for (Slice s : slices) total += s.amount();

        double radius = (size - Palette.RING) / 2;
        double at = 90;                       // twelve o'clock, going clockwise
        double gap = slices.size() > 1 ? 1.5 : 0;

        for (Slice s : slices) {
            double span = total > 0 ? s.amount() / total * 360 : 0;
            javafx.scene.shape.Arc arc = new javafx.scene.shape.Arc(
                    size / 2, size / 2, radius, radius,
                    at - span + gap / 2, Math.max(0, span - gap));
            arc.setType(javafx.scene.shape.ArcType.OPEN);
            arc.setFill(null);
            arc.setStroke(javafx.scene.paint.Color.web(s.colour()));
            arc.setStrokeWidth(Palette.RING);
            arc.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.BUTT);

            Tooltip tip = new Tooltip(String.format("%s%n%s   %.1f%% of the total",
                    s.name(), tightMoney(toDollars(s.amount())),
                    total > 0 ? s.amount() / total * 100 : 0));
            tip.setShowDelay(Duration.millis(200));
            Tooltip.install(arc, tip);

            ring.getChildren().add(arc);
            at -= span;
        }

        Label top = new Label(centreTop);
        top.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        Label figure = new Label(centreFigure);
        figure.setStyle(Palette.figure(Palette.SIZE_LEAD, centreTone));

        VBox middle = new VBox(0, top, figure);
        middle.setAlignment(Pos.CENTER);

        StackPane box = new StackPane(ring, middle);
        box.setPrefSize(size, size);
        box.setMinSize(size, size);
        box.setMaxSize(size, size);
        return box;
    }

    /**
     * The key beside a ring.
     *
     * ALWAYS PRESENT, and it carries the figures. A ring can say "this one is
     * about a third"; only the list can say which one and how much, and a
     * player deciding a tax rate needs the second. The swatch carries the
     * identity and the text stays in the ordinary text colours - a legend
     * written in its own series colours is a legend that shouts.
     */
    VBox donutKey(java.util.List<Slice> slices, double total, double annualGdp) {

        VBox key = new VBox(2);
        for (Slice s : slices) {

            Region swatch = new Region();
            swatch.setMinSize(10, 10);
            swatch.setPrefSize(10, 10);
            swatch.setMaxSize(10, 10);
            swatch.setStyle("-fx-background-color: " + s.colour() + ";"
                    + " -fx-background-radius: 2;");

            Label name = new Label(s.name());
            name.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_BODY));

            Region gap = new Region();
            HBox.setHgrow(gap, Priority.ALWAYS);

            Label share = new Label(String.format("%.1f%%",
                    total > 0 ? s.amount() / total * 100 : 0));
            share.setPrefWidth(46);
            share.setMinWidth(46);
            share.setAlignment(Pos.CENTER_RIGHT);
            share.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_HEAD));

            Label money = new Label(tightMoney(toDollars(s.amount())));
            money.setPrefWidth(72);
            money.setMinWidth(72);
            money.setAlignment(Pos.CENTER_RIGHT);
            money.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));

            HBox row = new HBox(Palette.GAP_TIGHT, swatch, name, gap, share, money);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPrefWidth(300);
            row.setMaxWidth(300);
            key.getChildren().add(row);
        }
        return key;
    }

    /**
     * Revenue against spending, to one scale, with the difference shown.
     *
     * TWO BARS ON ONE SCALE is the whole point - a surplus is not a number, it
     * is one bar being longer than the other, and no pair of figures says that
     * as fast. The shorter bar carries the gap in its own colour, so the
     * deficit is a length rather than a subtraction the player has to do.
     */
    VBox balanceBars(double revenue, double spending) {

        double scale = Math.max(revenue, spending);
        double width = STATEMENT - 130;

        VBox box = new VBox(6);
        box.setMaxWidth(STATEMENT);

        box.getChildren().add(balanceBar("Taken in", revenue, scale, width,
                Palette.REVENUE_RAMP[4], 0));
        box.getChildren().add(balanceBar("Paid out", spending, scale, width,
                Palette.SPENDING_RAMP[4], 0));

        double gap = revenue - spending;
        box.getChildren().add(balanceBar(gap >= 0 ? "Surplus" : "Deficit",
                Math.abs(gap), scale, width,
                gap >= 0 ? Palette.GOOD : Palette.BAD,
                gap >= 0 ? spending : revenue));
        return box;
    }

    /** One bar: a name, a length, and the figure on the end of it. */
    HBox balanceBar(String label, double value, double scale, double width,
                            String colour, double offset) {

        Label name = new Label(label);
        name.setPrefWidth(64);
        name.setMinWidth(64);
        name.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        Region lead = new Region();
        double leadWide = scale > 0 ? offset / scale * width : 0;
        lead.setMinWidth(leadWide);
        lead.setPrefWidth(leadWide);

        Region bar = new Region();
        double wide = scale > 0 ? Math.max(2, value / scale * width) : 2;
        bar.setMinSize(wide, 14);
        bar.setPrefSize(wide, 14);
        bar.setMaxSize(wide, 14);
        bar.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 3;");

        Label figure = new Label(tightMoney(toDollars(value)));
        figure.setStyle(Palette.figure(Palette.SIZE_CAPTION,
                colour.equals(Palette.BAD) ? Palette.BAD : Palette.TEXT_BODY)
                + " -fx-padding: 0 0 0 6;");

        HBox row = new HBox(0, name, lead, bar, figure);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);
        return row;
    }

    /* =====================================================================
       THE SCREEN
       ===================================================================== */

    void showGovernmentMenu() {
        ui.clearMenu("showGovernmentMenu", () -> showGovernmentMenu());

        EconomyManager em = ui.game.getEconomyManager();
        NationalAccounts na = em.getNationalAccounts();

        Label title = new Label("GOVERNMENT ECONOMY");
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        HBox vitals = governmentVitals(em, na);

        javafx.scene.layout.FlowPane strip =
                chipStrip(GOV_PAGES, govPage, Palette.SIZE_LABEL, name -> {
                    govPage = name;
                    ui.innerScrollAt.remove("showGovernmentMenu:body");
                    showGovernmentMenu();
                });
        strip.setStyle("-fx-padding: 8 0 8 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        switch (govPage) {
            case "Revenue"  -> revenuePage(column, em, na);
            case "Spending" -> spendingPage(column, em, na);
            case "Output"   -> outputPage(column, em, na);
            default         -> budgetOverview(column, em, na);
        }

        ui.rootMenu.getChildren().addAll(title, vitals, strip, ui.scrolled(column, 250));
    }

    /** What a share of the year's output comes to, in words. */
    String ofGdp(double monthly, double annualGdp) {
        if (annualGdp <= 0) return "no output to compare";
        return String.format("%.1f%% of annual GDP", monthly * 12 / annualGdp * 100);
    }

    HBox governmentVitals(EconomyManager em, NationalAccounts na) {

        double revenue = na.getTotalRevenue();
        double spending = na.getTotalExpenses();
        double balance = na.getBalance();
        double annual = annualGdp(na);
        double debt = ui.game.getDebtManager().getAllPrincipal();

        return vitalsBar(
                limitCell("TAKEN IN", tightMoney(toDollars(revenue)),
                        ofGdp(revenue, annual), Palette.TEXT_HEAD),
                limitCell("PAID OUT", tightMoney(toDollars(spending)),
                        ofGdp(spending, annual), Palette.TEXT_HEAD),
                limitCell(balance >= 0 ? "SURPLUS" : "DEFICIT",
                        tightMoney(toDollars(Math.abs(balance))),
                        revenue > 0
                                ? String.format("%.1f%% of what it took in",
                                        Math.abs(balance) / revenue * 100)
                                : "nothing came in",
                        balance >= 0 ? Palette.GOOD : Palette.BAD),
                limitCell("OWED", tightMoney(toDollars(debt)),
                        annual > 0
                                ? String.format("%.0f%% of annual GDP", debt / annual * 100)
                                : "no output to compare",
                        annual > 0 && debt / annual > 1.2 ? Palette.BAD
                                : annual > 0 && debt / annual > .6 ? Palette.WARN
                                : Palette.GOOD));
    }

    /* ------------------------------ THE OVERVIEW ------------------------------ */

    void budgetOverview(VBox column, EconomyManager em, NationalAccounts na) {

        double revenue = na.getTotalRevenue();
        double spending = na.getTotalExpenses();
        double balance = na.getBalance();
        double annual = annualGdp(na);

        java.util.List<Slice> inSlices = topSlices(
                revenueNames(), revenueAmounts(em, na), Palette.REVENUE_RAMP);
        java.util.List<Slice> outSlices = topSlices(
                spendingNames(), spendingAmounts(em, na), Palette.SPENDING_RAMP);

        /* ------------------------------ the two rings ------------------------------ */
        VBox inBox = new VBox(Palette.GAP,
                donut(inSlices, "taken in", tightMoney(toDollars(revenue)),
                        Palette.TEXT_HEAD, 170),
                donutKey(inSlices, revenue, annual));
        inBox.setAlignment(Pos.CENTER);

        VBox outBox = new VBox(Palette.GAP,
                donut(outSlices, "paid out", tightMoney(toDollars(spending)),
                        Palette.TEXT_HEAD, 170),
                donutKey(outSlices, spending, annual));
        outBox.setAlignment(Pos.CENTER);

        Label inTitle = new Label("WHERE THE MONEY COMES FROM");
        inTitle.setStyle(Palette.words(Palette.SIZE_HEADING, Palette.ACCENT)
                + " -fx-font-weight: bold;");
        Label outTitle = new Label("WHERE IT GOES");
        outTitle.setStyle(Palette.words(Palette.SIZE_HEADING, Palette.ACCENT)
                + " -fx-font-weight: bold;");

        VBox left = new VBox(Palette.GAP, inTitle, inBox);
        VBox right = new VBox(Palette.GAP, outTitle, outBox);
        left.setAlignment(Pos.TOP_CENTER);
        right.setAlignment(Pos.TOP_CENTER);

        HBox rings = new HBox(Palette.GAP_SECTION, left, right);
        rings.setAlignment(Pos.TOP_CENTER);
        rings.setStyle("-fx-padding: 4 0 12 0;");
        column.getChildren().add(rings);

        /* ------------------------------ the balance ------------------------------ */
        column.getChildren().add(statementHead(
                balance >= 0 ? "The city is running a surplus"
                             : "The city is running a deficit"));
        column.getChildren().add(balanceBars(revenue, spending));

        column.getChildren().add(sentence(balance >= 0
                ? String.format("It took in %s more than it spent — %.1f%% of revenue, and "
                        + "%s. A surplus pays down debt or buys the next thing without "
                        + "borrowing for it.",
                        tightMoney(toDollars(balance)),
                        revenue > 0 ? balance / revenue * 100 : 0,
                        ofGdp(balance, annual))
                : String.format("It spent %s more than it took in — %.1f%% of revenue, and "
                        + "%s. That gap is borrowed, and next month's interest is charged "
                        + "on it.",
                        tightMoney(toDollars(-balance)),
                        revenue > 0 ? -balance / revenue * 100 : 0,
                        ofGdp(-balance, annual)),
                balance >= 0 ? Palette.GOOD : Palette.BAD_TEXT));

        /* ===================================================================
           ...AND WHAT THE TREASURY ACTUALLY DID

           Jerus: "show how much was the actual month change, like in the next
           month button it shows 3k but sometimes cause of land buybacks or
           sales it was actually more or less."

           The budget above is an INCOME measure. The treasury balance is a CASH
           measure, and four ordinary things move one without moving the other:

             - issuing paper raises cash and is not revenue
             - repaying principal spends cash and is not an expense
             - land the city buys or sells, buildings it pays for, reserves,
               capital put into the bank, bonds bought back - the player's own
               decisions between one month and the next
             - and the books dating a movement to a different month from the
               money (see the note under the table)

           So this is written as a COMPARISON, not as an identity that foots.
           The change is measured - opening balance to closing balance, press to
           press - and the named flows are subtracted from it. What is left gets
           its own row with its own name. A total that quietly absorbs its own
           gap is worse than no total, which is the same rule the sector cash
           flow and the sick rate follow.
           =================================================================== */
        if (ui.game.hasTreasuryMonth()) {

            double opening = ui.game.getTreasuryOpening();
            double closing = ui.game.getTreasuryClosing();
            double change  = ui.game.getTreasuryChange();
            double raised  = ui.game.getTreasuryRaised();
            double repaidP = ui.game.getTreasuryRepaid();
            double booked  = ui.game.getTreasurySurplus();
            double rest    = ui.game.getTreasuryUnexplained();

            column.getChildren().add(statementHead("What the treasury actually did"));

            column.getChildren().add(statementLine("Opened the month with",
                    tightMoney(toDollars(opening), false)));
            column.getChildren().add(statementLine("Closed the month with",
                    tightMoney(toDollars(closing), false)));
            column.getChildren().add(statementTotal(
                    change >= 0 ? "The balance GREW by" : "The balance FELL by",
                    tightMoney(toDollars(Math.abs(change)), false),
                    change >= 0 ? Palette.GOOD : Palette.BAD));

            column.getChildren().add(sentence(
                    "That is the figure on the button, and it is not the surplus above. "
                    + "Here is the difference between them, line by line.",
                    Palette.TEXT_MUTED));

            javafx.scene.layout.GridPane bridge = grid(
                    new double[] {286, 130, 118}, rightAfterFirst(3));
            gridHead(bridge, "", "moved the balance", "of the change");

            int row = 1;
            row = bridgeRow(bridge, row,
                    booked >= 0 ? "Surplus on the budget" : "Deficit on the budget",
                    booked, change, booked >= 0 ? Palette.GOOD : Palette.BAD);
            row = bridgeRow(bridge, row, "Raised by issuing paper",
                    raised, change, raised > 0 ? Palette.ACCENT : null);
            row = bridgeRow(bridge, row, "Principal repaid to lenders",
                    -repaidP, change, repaidP > 0 ? Palette.WARN : null);
            row = bridgeRow(bridge, row, "Everything else the treasury did",
                    rest, change, Math.abs(rest) > .5 ? Palette.TEXT_HEAD : null);
            column.getChildren().add(bridge);

            column.getChildren().add(statementTotal("Which is the change",
                    (change >= 0 ? "+" : "\u2212")
                            + tightMoney(toDollars(Math.abs(change)), false),
                    change >= 0 ? Palette.GOOD : Palette.BAD));

            column.getChildren().add(statementNote(
                    "The last row is everything that moves cash without being a budget "
                    + "line: land the city bought or sold, buildings it paid for, reserves, "
                    + "capital put into the bank, bonds bought back \u2014 and any movement "
                    + "the books date to a different month from the money. It is printed "
                    + "rather than folded into a total, because a bridge that hides its own "
                    + "gap is not a bridge."));

            if (Math.abs(rest) > Math.abs(booked) && Math.abs(rest) > .5) {
                column.getChildren().add(alert("Most of the month was not the budget",
                        String.format("%s of the %s the balance moved was something other "
                        + "than revenue and spending. On a month where the city bought or "
                        + "sold land, or paid for a building, that is exactly what it "
                        + "should say \u2014 those are real money and they are not a budget.",
                        tightMoney(toDollars(Math.abs(rest))),
                        tightMoney(toDollars(Math.abs(change))))));
            }
        }

        /* --------------------- against the size of the economy --------------------- */
        column.getChildren().add(statementHead("Against the size of the economy"));

        if (annual <= 0) {
            column.getChildren().add(sentence(
                    "There is not a year of output recorded yet, so nothing here can be "
                    + "put in proportion. Twelve months and this section fills in.",
                    Palette.TEXT_MUTED));
        } else {
            javafx.scene.layout.GridPane share = grid(
                    new double[] {186, 104, 92, 104}, rightAfterFirst(4));
            gridHead(share, "", "a month", "a year", "of GDP");

            int line = 1;
            line = shareRow(share, line, "Revenue", revenue, annual, null);
            line = shareRow(share, line, "Spending", spending, annual, null);
            line = shareRow(share, line, balance >= 0 ? "Surplus" : "Deficit",
                    Math.abs(balance), annual,
                    balance >= 0 ? Palette.GOOD : Palette.BAD);
            line = shareRow(share, line, "Wage bill of the city's own staff",
                    ui.game.getHealthcare().getPayroll() + ui.game.getEducation().getPayroll(),
                    annual, null);
            column.getChildren().add(share);

            column.getChildren().add(statementLine("Annual GDP",
                    tightMoney(toDollars(annual), false)));
            int heads = ui.game.getPopulationManager().getPopulation();
            column.getChildren().add(statementLine("Per head, a year",
                    tightMoney(toDollars(heads > 0 ? annual / heads : 0), false)));
            double debt = ui.game.getDebtManager().getAllPrincipal();
            column.getChildren().add(statementLine("Total debt",
                    String.format("%s   ·   %.0f%% of GDP",
                            tightMoney(toDollars(debt)), debt / annual * 100),
                    debt / annual > 1.2 ? Palette.BAD : null));
            column.getChildren().add(statementNote(
                    "A month of revenue against a year of output is the only honest way to "
                    + "compare a budget with anything — another city, another year, or a "
                    + "rule of thumb. The monthly figures are annualised to get there."));
            if (gdpEstimated(na)) {
                column.getChildren().add(statementNote(String.format(
                        "This city has only %d months of output recorded, so the year is "
                        + "scaled up from those rather than measured. It settles as the "
                        + "twelfth month goes by.", na.getMonthsRecorded())));
            }
        }

        column.getChildren().add(statementNote(
                "Every line on both rings opens up. The Revenue and Spending pages have "
                + "the full lists, and each one says which sector, which pay tier or which "
                + "kind of building it came from."));
    }

    /** One row of the treasury bridge: a signed movement and its share of the change. */
    int bridgeRow(javafx.scene.layout.GridPane table, int line,
                          String label, double amount, double change, String tone) {
        table.add(gridCell(label, Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
        // A zero movement is written as a zero, not as "+$0" and "-0%" - a sign
        // on nothing reads as a direction, and there was not one.
        boolean nothing = Math.abs(amount) < .5;
        table.add(gridCell(nothing ? "$0"
                        : (amount > 0 ? "+" : "\u2212")
                          + tightMoney(toDollars(Math.abs(amount))),
                nothing ? Palette.TEXT_SPENT : tone == null ? Palette.TEXT_BODY : tone,
                Palette.SIZE_CAPTION, true), 1, line);
        table.add(gridCell(nothing || Math.abs(change) <= 1e-9 ? "\u2014"
                        : String.format("%.0f%%", amount / change * 100),
                Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
        return line + 1;
    }

    /** One row of the share table: a month, a year, and a percentage of GDP. */
    int shareRow(javafx.scene.layout.GridPane table, int line,
                         String label, double monthly, double annual, String tone) {
        table.add(gridCell(label, Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
        table.add(gridCell(tightMoney(toDollars(monthly)),
                tone == null ? Palette.TEXT_BODY : tone, Palette.SIZE_CAPTION, true), 1, line);
        table.add(gridCell(tightMoney(toDollars(monthly * 12)),
                Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
        table.add(gridCell(String.format("%.2f%%", monthly * 12 / annual * 100),
                tone == null ? Palette.TEXT_HEAD : tone, Palette.SIZE_CAPTION, true), 3, line);
        return line + 1;
    }

    /* =====================================================================
       THE TWO LISTS.

       Every line opens. Jerus: "you can for example click business tax and it
       expands to show all the info, same with sales, wages, property etc" —
       "by all the info i mean which sector is giving how much".

       That is the question the old screen could not answer at all. Business tax
       was one figure; it is six businesses paying six different rates, and
       which of them is carrying the city is a thing a player would change a
       policy over.
       ===================================================================== */

    static java.util.List<String> revenueNames() {
        return java.util.List.of("Business tax", "Sales tax", "Wage tax",
                "Property tax", "Pension contributions", "EI premiums", "Utility income",
                "Healthcare fees", "School fees", "Land sold");
    }

    java.util.List<Double> revenueAmounts(EconomyManager em, NationalAccounts na) {
        return java.util.List.of(
                na.getTaxBusiness() + na.getTaxIndustrial(),
                na.getTaxSales(),
                na.getTaxWage(),
                na.getPropertyTax(),
                na.getContributions(),
                na.getEiPremiums(),
                na.getUtilityIncome(),
                na.getHealthFees(),
                na.getEducationFees(),
                na.getLandSales());
    }

    static java.util.List<String> spendingNames() {
        return java.util.List.of("Pensions", "EI", "Student grants", "Healthcare", "Education",
                "Police and prisons", "Buildings", "Repairs", "Land bought", "Debt interest");
    }

    /*
     * REPAIRS JOINED THIS LIST ON 2026-09-09, and it is a real line rather than
     * a nicety. Jerus: "all buildings need maintenance, and make sure they get
     * billed." The city owns the roads, the schools, the hospitals and both
     * utility plants, so the treasury pays their repair bill - and on the first
     * measurement that was two thirds of the whole city's maintenance and about
     * $2M a month. A cost that size belongs on the budget the player reads,
     * not only in the cash balance. Leaving it off is the same failure as the
     * bank's profit tax and the auto-subsidy, both of which were on no budget
     * at all until somebody went looking.
     */
    java.util.List<Double> spendingAmounts(EconomyManager em, NationalAccounts na) {
        return java.util.List.of(
                na.getPensions(),
                na.getEiBenefits(),
                na.getStudentGrants(),
                na.getHealthSpending(),
                na.getEducationSpending(),
                na.getSafetySpending(),
                na.getCapitalSpending(),
                ui.game.getCityMaintenancePaid(),
                na.getLandPurchases(),
                na.getInterestExpense());
    }

    /**
     * A line of the budget that opens into whoever paid it.
     *
     * The share of the budget and the share of GDP are on the closed row,
     * because those are the two figures that let a player rank the line against
     * everything else without opening anything.
     */
    VBox budgetLine(String label, double amount, double total, double annual,
                            String colour, VBox detail) {
        return budgetLine(label, amount, total, annual, colour, detail, "who");
    }

    /**
     * @param word what the disclosure offers - "who" pays a tax, "to whom" the
     *             city pays interest. One word, because the mark is four
     *             characters wide and a sentence there reads as a label.
     */
    VBox budgetLine(String label, double amount, double total, double annual,
                            String colour, VBox detail, String word) {

        Region swatch = new Region();
        swatch.setMinSize(10, 10);
        swatch.setPrefSize(10, 10);
        swatch.setMaxSize(10, 10);
        swatch.setStyle("-fx-background-color: " + (colour == null ? "transparent" : colour)
                + "; -fx-background-radius: 2;");

        Label name = new Label(label);
        name.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));

        Label mark = new Label(detail == null ? "" : CLOSED + " " + word);
        mark.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.ACCENT));

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label money = new Label(tightMoney(toDollars(amount)));
        money.setPrefWidth(100);
        money.setMinWidth(100);
        money.setAlignment(Pos.CENTER_RIGHT);
        money.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.TEXT_HEAD));

        Label share = new Label(String.format("%.1f%%", total > 0 ? amount / total * 100 : 0));
        share.setPrefWidth(58);
        share.setMinWidth(58);
        share.setAlignment(Pos.CENTER_RIGHT);
        share.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));

        Label gdp = new Label(annual > 0
                ? String.format("%.2f%%", amount * 12 / annual * 100) : "—");
        gdp.setPrefWidth(60);
        gdp.setMinWidth(60);
        gdp.setAlignment(Pos.CENTER_RIGHT);
        gdp.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_SPENT));

        HBox row = new HBox(Palette.GAP_TIGHT, swatch, name, mark, gap, money, share, gdp);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefWidth(STATEMENT);
        row.setMaxWidth(STATEMENT);
        row.setStyle("-fx-padding: 5 0 5 0;"
                + (detail == null ? "" : " -fx-cursor: hand;"));

        VBox box = new VBox(0, row);
        if (detail != null) {
            detail.setVisible(false);
            detail.setManaged(false);
            detail.setStyle("-fx-padding: 2 0 8 22;");
            row.setOnMouseClicked(e -> {
                boolean open = !detail.isVisible();
                detail.setVisible(open);
                detail.setManaged(open);
                mark.setText((open ? OPENED : CLOSED) + " " + word);
            });
            box.getChildren().add(detail);
        }
        box.setMaxWidth(STATEMENT);
        return box;
    }

    /** The heading over a budget list: what the three right-hand columns are. */
    HBox budgetHead(String left) {
        Label what = new Label(left);
        what.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label a = head("a month", 100), b = head("of the budget", 58), c = head("of GDP", 60);
        HBox row = new HBox(Palette.GAP_TIGHT, what, gap, a, b, c);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefWidth(STATEMENT);
        row.setMaxWidth(STATEMENT);
        row.setStyle("-fx-padding: 4 0 2 0;");
        return row;
    }

    Label head(String text, double width) {
        Label l = new Label(text);
        l.setPrefWidth(width);
        l.setMinWidth(width);
        l.setAlignment(Pos.CENTER_RIGHT);
        l.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        return l;
    }

    /* --------------------------- WHO PAYS WHAT --------------------------- */

    /** Business tax, by the companies that pay it - every sector, and the bank. */
    VBox businessTaxDetail(double total) {
        SectorBooks books = ui.game.getSectorBooks();
        TaxPolicy policy = ui.game.getEconomyManager().getTaxPolicy();
        VBox box = new VBox(0);
        for (Sector sector : ui.game.getSectors().all()) {
            box.getChildren().add(payerRow(sector.label(),
                    books.get(sector).tax(), total,
                    String.format("%.1f%%", policy.effectiveProfitRate(sector) * 100)));
        }
        box.getChildren().add(payerRow("Bank", ui.game.getEconomyManager().getBankTax(), total,
                String.format("%.1f%%", policy.getIncomeTaxRate() * 100)));
        box.getChildren().add(statementNote(
                "Charged on each company's own profit and never refunded on a loss, so a "
                + "sector that lost money this month simply paid nothing."));
        return box;
    }

    /** Sales tax, by the sector that remitted it. */
    VBox salesTaxDetail(double total) {
        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        VBox box = new VBox(0);
        for (Sector sector : ui.game.getSectors().all()) {
            box.getChildren().add(payerRow(sector.label(),
                    em.getSectorSalesTax(sector), total,
                    String.format("%.1f%%", policy.effectiveSalesRate(sector) * 100)));
        }
        box.getChildren().add(statementNote(
                "The rate follows the producer, so the producer remits — what it owes on "
                + "what it sold, less the credit on what it bought. A sector in a refund "
                + "position shows as a negative."));
        return box;
    }

    /** Wage tax, by the pay tier that earned the wages. */
    VBox wageTaxDetail(double total) {
        HouseholdAccounts hh = ui.game.getHouseholds();
        VBox box = new VBox(0);
        for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
            box.getChildren().add(payerRow(hh.getRowLabel(t), hh.getRowTax(t), total,
                    hh.getRowWages(t) > 0
                            ? String.format("%.1f%%", hh.getRowTax(t) / hh.getRowWages(t) * 100)
                            : "—"));
        }
        box.getChildren().add(statementNote(String.format(
                "Banded, so the rate on the right is what each tier actually paid rather "
                + "than the headline %.0f%%. The bands are set per skill level on the tax "
                + "policy screen.",
                ui.game.getEconomyManager().getTaxPolicy().getIncomeTaxRate() * 100)));
        return box;
    }

    /** Pension contributions, by the tier that paid them. */
    VBox contributionsDetail(double total) {
        HouseholdAccounts hh = ui.game.getHouseholds();
        VBox box = new VBox(0);
        for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
            box.getChildren().add(payerRow(hh.getRowLabel(t),
                    hh.getRowContributions(t), total, null));
        }
        box.getChildren().add(statementNote(String.format(
                "%.2f%% of every wage in the city, at one rate for everybody. It is not a "
                + "fund — this month's contributions pay this month's pensions.",
                ui.game.getEconomyManager().getTaxPolicy().getContributionRate() * 100)));
        return box;
    }

    /** Property tax, by the sector it is assessed on. */
    VBox propertyTaxDetail(double total) {
        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        VBox box = new VBox(0);
        for (Sector sector : ui.game.getSectors().all()) {
            box.getChildren().add(payerRow(sector.label(),
                    em.getPropertyTaxFor(sector), total,
                    String.format("%.2f%%", policy.effectivePropertyRate(sector) * 100)));
        }
        box.getChildren().add(statementNote(
                "Assessed on land plus buildings at what that sector's own balance sheet "
                + "claims they are worth — a business is taxed on the value it books. The "
                + "city's own buildings are exempt: taxing them would move money from one "
                + "pocket to the other and make the utilities look worse for nothing."));
        return box;
    }

    /** Healthcare fees, by the kind of care that charged them. */
    VBox healthFeeDetail(double total) {
        Healthcare service = ui.game.getHealthcare();
        VBox box = new VBox(0);
        for (CareType care : new CareType[]{CareType.GENERAL, CareType.CHILDCARE,
                                            CareType.SENIOR}) {
            box.getChildren().add(payerRow(care.getLabel(), service.feesFrom(care), total,
                    cash(service.feeNow(care))));
        }
        box.getChildren().add(payerRow("Burials",
                service.getBurials() * service.feeNow(CareType.BURIAL), total,
                cash(service.feeNow(CareType.BURIAL))));
        box.getChildren().add(payerRow("Cremations",
                service.getCremations() * service.feeNow(CareType.CREMATION), total,
                cash(service.feeNow(CareType.CREMATION))));
        box.getChildren().add(statementNote(
                "The rate column is the fee each one charges. Care runs at a deficit by "
                + "design — the Services tab has what the rest of it buys."));
        return box;
    }

    /** School fees, by the course. */
    VBox schoolFeeDetail(double total) {
        Education schools = ui.game.getEducation();
        double subsidy = schools.getTuitionSubsidy();
        VBox box = new VBox(0);
        for (EducationType course : EducationType.values()) {
            if (course == EducationType.NONE) continue;
            double students = schools.getEnrolled(course);
            if (students < .5) continue;
            box.getChildren().add(payerRow(course.getLabel(),
                    schools.feeFor(course) * students * (1 - subsidy), total,
                    cash(schools.outOfPocket(course))));
        }
        box.getChildren().add(statementNote(String.format(
                "What households paid after the city's %.0f%% subsidy. The rate column is "
                + "what one seat costs a family a month.", subsidy * 100)));
        return box;
    }

    void revenuePage(VBox column, EconomyManager em, NationalAccounts na) {

        double total = na.getTotalRevenue();
        double annual = annualGdp(na);
        java.util.List<String> names = revenueNames();
        java.util.List<Double> amounts = revenueAmounts(em, na);

        column.getChildren().add(statementHead("Everything the city took in"));
        column.getChildren().add(budgetHead(CityCalendar.format(ui.game.getMonth())));

        /* Coloured to match the ring on the overview, so a line a player picked
         * out of the picture is the same colour when they come here for the
         * detail. Anything outside the top five has no colour rather than a
         * wrong one. */
        java.util.Map<String, String> colours = new java.util.HashMap<>();
        for (Slice s : topSlices(names, amounts, Palette.REVENUE_RAMP)) {
            colours.put(s.name(), s.colour());
        }

        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            double amount = amounts.get(i);
            column.getChildren().add(budgetLine(name, amount, total, annual,
                    colours.get(name), revenueDetail(name, amount)));
        }

        column.getChildren().add(statementTotal("Taken in altogether",
                tightMoney(toDollars(total), false), Palette.GOOD));
        column.getChildren().add(statementNote(annual > 0
                ? String.format("Which is %s. The rightmost column on every line above is "
                        + "that line's own share of the year's output.",
                        ofGdp(total, annual))
                : "There is not a year of output recorded yet, so the GDP column is empty."));
    }

    VBox revenueDetail(String name, double amount) {
        return switch (name) {
            case "Business tax"         -> businessTaxDetail(amount);
            case "Sales tax"            -> salesTaxDetail(amount);
            case "Wage tax"             -> wageTaxDetail(amount);
            case "Property tax"         -> propertyTaxDetail(amount);
            case "Pension contributions"-> contributionsDetail(amount);
            case "Healthcare fees"      -> healthFeeDetail(amount);
            case "School fees"          -> schoolFeeDetail(amount);
            default                     -> null;
        };
    }

    /* ------------------------------ WHAT IT SPENDS ------------------------------ */

    /** Healthcare spending, by the kind of care it is spent on. */
    VBox healthSpendDetail(double total) {
        BuildingManager bm = ui.game.getBuildingManager();
        double[] wages = ui.game.getPopulationManager().getWagesPerType();
        double[] fill = ui.game.getPopulationManager().getJobFillRate();
        VBox box = new VBox(0);
        for (CareType care : new CareType[]{CareType.GENERAL, CareType.CHILDCARE,
                CareType.SENIOR, CareType.BURIAL, CareType.CREMATION}) {
            double cost = bm.getCarePayroll(care, wages, fill) + bm.getCareUpkeep(care);
            if (cost <= 0) continue;
            box.getChildren().add(payerRow(care.getLabel(), cost, total, null));
        }
        box.getChildren().add(statementNote(String.format(
                "%s of it is wages and %s is keeping the buildings standing. Wages are "
                + "discounted by how many of the posts are actually filled, which is why "
                + "an understaffed ward is cheap and useless at once.",
                tightMoney(toDollars(ui.game.getHealthcare().getPayroll())),
                tightMoney(toDollars(ui.game.getHealthcare().getUpkeep())))));
        return box;
    }

    /** Education spending, by the school it is spent on. */
    VBox educationSpendDetail(double total) {
        BuildingManager bm = ui.game.getBuildingManager();
        double[] wages = ui.game.getPopulationManager().getWagesPerType();
        double[] fill = ui.game.getPopulationManager().getJobFillRate();
        VBox box = new VBox(0);
        for (EducationType course : EducationType.values()) {
            if (course == EducationType.NONE) continue;
            double cost = bm.getSchoolPayroll(course, wages, fill)
                        + bm.getSchoolUpkeep(course);
            if (cost <= 0) continue;
            box.getChildren().add(payerRow(course.getLabel(), cost, total, null));
        }
        box.getChildren().add(payerRow("Tuition the city covers",
                ui.game.getEducation().getSubsidy(), total, null));
        box.getChildren().add(statementNote(
                "The last line is the share of tuition the subsidy dial forgives. Note "
                + "that the city is already paying the teachers above it — see the note on "
                + "the education books about that being charged twice."));
        return box;
    }

    /* ===================================================================
       WHAT THE DEBT COSTS, BY THE PAPER IT IS OWED ON

       Jerus: "remember interest also goes in the expense", and then "if its
       term loans, then in spending and a note or something indicating how much
       is going towards the term loans."

       Both point at the same hole. The city borrows on three instruments and
       they behave completely differently in a month:

         NOTE    a discount bill. The lender pays less than the face and
                 collects the face at maturity, so there is NO coupon. It shows
                 nothing on the spending list and then repays a lump.
         SERIAL  a coupon every month on the declining balance, and a slice of
                 principal every anniversary. It amortises.
         TERM    a coupon every month on the whole face, and the entire face at
                 the end. The low monthly payment with the cliff behind it.

       So "Debt interest: $0" can be true while the city is paying millions,
       and "Debt interest: $40k" can be true while a $2M slice falls due next
       month. Neither is visible from one line, and the fix is not a different
       number - it is showing the paper.

       PRINCIPAL IS NOT AN EXPENSE and is not added into this line. It is a
       balance-sheet movement: the money leaves and the debt shrinks by the
       same amount, so nothing was consumed. It gets its own row underneath,
       clearly marked, because a player watching the treasury does not care
       which of the two took their money.
       =================================================================== */

    /** One kind of paper, totalled. */
    record PaperKind(String name, int count, double principal,
                             double coupon, String note) { }

    java.util.List<PaperKind> paperKinds() {

        java.util.Map<String, double[]> tally = new java.util.LinkedHashMap<>();
        for (String t : new String[] {"NOTE", "SERIAL", "TERM"}) {
            tally.put(t, new double[3]);
        }

        for (Debt d : ui.game.getDebtManager().getDebt()) {
            double[] row = tally.get(d.getType());
            if (row == null) continue;
            row[0]++;
            row[1] += d.getOustandingPrincipal();
            row[2] += d.getMonthlyInterestExpense();
        }

        java.util.List<PaperKind> kinds = new java.util.ArrayList<>();
        kinds.add(new PaperKind("Notes", (int) tally.get("NOTE")[0],
                tally.get("NOTE")[1], tally.get("NOTE")[2],
                "discounted at issue \u2014 no coupon"));
        kinds.add(new PaperKind("Serial bonds", (int) tally.get("SERIAL")[0],
                tally.get("SERIAL")[1], tally.get("SERIAL")[2],
                "coupon monthly, principal yearly"));
        kinds.add(new PaperKind("Term loans", (int) tally.get("TERM")[0],
                tally.get("TERM")[1], tally.get("TERM")[2],
                "coupon monthly, face at the end"));
        return kinds;
    }

    /** One kind's row, or an empty one. Never null, so callers can just read it. */
    PaperKind paperKind(String name) {
        for (PaperKind k : paperKinds()) {
            if (k.name().equals(name)) return k;
        }
        return new PaperKind(name, 0, 0, 0, "");
    }

    /**
     * The interest line, opened into the paper it is charged on.
     *
     * @param total this month's whole interest bill, so each row can carry its
     *              share of it
     */
    VBox debtServiceDetail(double total) {

        VBox box = new VBox(0);
        boolean any = false;

        for (PaperKind k : paperKinds()) {
            if (k.count() == 0) continue;
            any = true;
            box.getChildren().add(payerRow(
                    k.name() + "  \u00d7" + k.count(),
                    k.coupon(), total,
                    tightMoney(toDollars(k.principal())) + " owed"));
            box.getChildren().add(bookNote("        " + k.note()));
        }

        if (!any) {
            box.getChildren().add(statementNote("The city owes nothing."));
            return box;
        }

        /* --------------- and the money that is not on this statement --------------- */
        double repaid = ui.game.getCityPrincipalRepaidThisMonth()
                + ui.game.getForeignPrincipalRepaidThisMonth();
        if (repaid > 0) {
            box.getChildren().add(payerRow("Principal repaid \u2014 not an expense",
                    repaid, 0, "left the treasury"));
        }

        double foreignInterest = ui.game.getForeignInterestPaidThisMonth();
        if (foreignInterest > 0) {
            box.getChildren().add(payerRow("...of which paid abroad",
                    foreignInterest, total, "in dollars"));
        }

        box.getChildren().add(statementNote(repaid > 0
                ? String.format("Interest is the only part of debt service that is an "
                        + "expense, so it is the only part on the list above. %s of "
                        + "principal also left the treasury this month, and that is not "
                        + "spending \u2014 the money went out and the debt went down by the "
                        + "same amount. The treasury block on the Overview page is where "
                        + "the two meet.", tightMoney(toDollars(repaid)))
                : "Interest is the only part of debt service that is an expense, so it is "
                        + "the only part on the list above. No principal fell due this "
                        + "month; when it does, it leaves the treasury without appearing "
                        + "here, and the treasury block on the Overview page is where the "
                        + "two meet."));
        return box;
    }

    /** Pensions, and who they go to. */
    VBox pensionDetail(double total) {
        EconomyManager em = ui.game.getEconomyManager();
        HouseholdAccounts hh = ui.game.getHouseholds();
        VBox box = new VBox(0);
        box.getChildren().add(payerRow("Paid to seniors",
                hh.getRowPensions(HouseholdAccounts.RETIRED), total,
                cash(em.getTaxPolicy().pensionPerSenior())));
        box.getChildren().add(statementNote(String.format(
                "%s seniors at %s each. Contributions brought in %s, so the scheme covers "
                + "%.0f%% of itself and the rest comes out of general revenue.",
                people(em.getSeniors()),
                cash(em.getTaxPolicy().pensionPerSenior()),
                tightMoney(toDollars(em.getContributions())),
                em.getPensionCoverage() * 100)));
        return box;
    }

    void spendingPage(VBox column, EconomyManager em, NationalAccounts na) {

        double total = na.getTotalExpenses();
        double annual = annualGdp(na);
        java.util.List<String> names = spendingNames();
        java.util.List<Double> amounts = spendingAmounts(em, na);

        column.getChildren().add(statementHead("Everything the city paid out"));
        column.getChildren().add(budgetHead(CityCalendar.format(ui.game.getMonth())));

        java.util.Map<String, String> colours = new java.util.HashMap<>();
        for (Slice s : topSlices(names, amounts, Palette.SPENDING_RAMP)) {
            colours.put(s.name(), s.colour());
        }

        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            double amount = amounts.get(i);
            VBox detail = switch (name) {
                case "Healthcare"    -> healthSpendDetail(amount);
                case "Education"     -> educationSpendDetail(amount);
                case "Pensions"      -> pensionDetail(amount);
                case "Debt interest" -> debtServiceDetail(amount);
                default              -> null;
            };
            column.getChildren().add(budgetLine(name, amount, total, annual,
                    colours.get(name), detail,
                    "Debt interest".equals(name) ? "on what" : "who"));
        }

        column.getChildren().add(statementTotal("Paid out altogether",
                tightMoney(toDollars(total), false), Palette.WARN));

        /* ========================= WHAT THE DEBT IS COSTING =========================
         *
         * The interest line above is one number for three instruments that do
         * completely different things to a month, so it gets a paragraph rather
         * than being left to speak for itself. See debtServiceDetail().
         */
        PaperKind notes  = paperKind("Notes");
        PaperKind serial = paperKind("Serial bonds");
        PaperKind term   = paperKind("Term loans");
        double repaid = ui.game.getCityPrincipalRepaidThisMonth()
                + ui.game.getForeignPrincipalRepaidThisMonth();

        if (notes.count() + serial.count() + term.count() > 0) {

            column.getChildren().add(statementHead("What the debt is costing"));

            javafx.scene.layout.GridPane debtTable = grid(
                    new double[] {158, 62, 116, 104, 104}, rightAfterFirst(5));
            gridHead(debtTable, "", "pieces", "owed", "interest /mo", "of spending");

            int line = 1;
            for (PaperKind k : java.util.List.of(notes, serial, term)) {
                if (k.count() == 0) continue;
                debtTable.add(gridCell(k.name(), Palette.TEXT_BODY,
                        Palette.SIZE_CAPTION, false), 0, line);
                debtTable.add(gridCell(String.valueOf(k.count()), Palette.TEXT_MUTED,
                        Palette.SIZE_CAPTION, true), 1, line);
                debtTable.add(gridCell(tightMoney(toDollars(k.principal())),
                        Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, line);
                debtTable.add(gridCell(tightMoney(toDollars(k.coupon())),
                        k.coupon() > 0 ? Palette.WARN : Palette.TEXT_SPENT,
                        Palette.SIZE_CAPTION, true), 3, line);
                debtTable.add(gridCell(total > 0
                        ? String.format("%.2f%%", k.coupon() / total * 100) : "\u2014",
                        Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 4, line);
                line++;
            }
            column.getChildren().add(debtTable);

            if (repaid > 0) {
                column.getChildren().add(statementLine("Principal repaid this month",
                        tightMoney(toDollars(repaid), false), Palette.TEXT_HEAD));
                column.getChildren().add(statementNote(
                        "Not on the list above, and deliberately: repaying principal is not "
                        + "spending. The money leaves and the debt falls by the same amount, "
                        + "so nothing was consumed \u2014 but the treasury is lighter by it "
                        + "either way. The Overview page reconciles the two."));
            }

            /* ------------------------- the term loans ------------------------- */
            if (term.count() > 0) {
                column.getChildren().add(sentence(String.format(
                        "%s of the debt is on term loans, costing %s a month \u2014 %s of "
                        + "everything the city spends. A term loan pays its coupon every "
                        + "month and the whole face at the end, so the monthly cost is "
                        + "small, does not fall, and takes nothing off the principal. The "
                        + "Finances tab has the maturity strip that says when the face is "
                        + "due.",
                        tightMoney(toDollars(term.principal())),
                        tightMoney(toDollars(term.coupon())),
                        total > 0 ? String.format("%.2f%%", term.coupon() / total * 100)
                                  : "an unmeasurable share"),
                        Palette.TEXT_BODY));
            }

            if (serial.count() > 0) {
                column.getChildren().add(sentence(String.format(
                        "%s is on serial bonds, costing %s a month. Those amortise \u2014 a "
                        + "slice of principal falls due every anniversary, so both the debt "
                        + "and this interest line shrink on their own as long as nothing new "
                        + "is issued.",
                        tightMoney(toDollars(serial.principal())),
                        tightMoney(toDollars(serial.coupon()))),
                        Palette.TEXT_BODY));
            }

            /* ---------- when the budget line disagrees with the paper ---------- */
            /*
             * A FRESHLY LOADED CITY HAS NOT CHARGED A COUPON YET.
             *
             * The interest on the budget is struck from what the city actually
             * paid, and a save does not carry a part-finished month - so on the
             * first month back the line reads zero while the bonds plainly owe
             * a coupon. The two figures next to each other look like a broken
             * screen, and the honest answer is to say which is which rather
             * than to quietly print the larger one.
             */
            double coupons = notes.coupon() + serial.coupon() + term.coupon();
            double booked = na.getInterestExpense();
            if (coupons > 0 && Math.abs(coupons - booked) > coupons * .02) {
                column.getChildren().add(alert("The budget's interest line does not match the paper",
                        String.format("The bonds above owe %s a month in coupons, and the "
                        + "budget recorded %s. The budget is struck from what the city "
                        + "actually paid over a completed month, so the two part company "
                        + "on the first month after loading a save, and again in any month "
                        + "a bond was issued or retired part-way through. Advance a month "
                        + "and they meet.",
                        tightMoney(toDollars(coupons)),
                        tightMoney(toDollars(booked)))));
            }

            /*
             * WHY THE INTEREST LINE CAN READ ZERO WITH DEBT ON THE BOOKS.
             *
             * A note is a discount instrument: the lender hands over less than
             * the face and collects the face at maturity, so there is no
             * monthly coupon to expense. A city financed entirely on notes
             * shows nothing here while plainly owing millions, which reads as a
             * broken screen rather than as an instrument.
             */
            if (notes.principal() > 0) {
                column.getChildren().add(alert("Notes cost nothing a month and that is not free",
                        String.format("%s of the city\u2019s debt is notes, which carry no "
                        + "interest at all \u2014 the lender\u2019s return was the discount, "
                        + "taken out of the proceeds when the note was issued. So the "
                        + "interest line above understates what the borrowing costs, and "
                        + "the whole %s falls due as a lump rather than in slices.",
                        tightMoney(toDollars(notes.principal())),
                        tightMoney(toDollars(notes.principal())))));
            }
        }

        /* -------------------- the two services, as businesses -------------------- */
        column.getChildren().add(statementHead("The two services that charge for themselves"));

        column.getChildren().add(statementLine("Healthcare costs",
                tightMoney(toDollars(em.getHealthcareBill()), false)));
        column.getChildren().add(statementLine("...and charges back",
                tightMoney(toDollars(em.getHealthcareFees()), false), Palette.GOOD));
        column.getChildren().add(statementTotal("Net cost of care",
                tightMoney(toDollars(-em.getHealthcareNet()), false),
                em.getHealthcareNet() > 0 ? Palette.WARN : Palette.GOOD));

        column.getChildren().add(statementLine("Education costs",
                tightMoney(toDollars(em.getEducationBill()), false)));
        column.getChildren().add(statementLine("...and charges back",
                tightMoney(toDollars(em.getEducationFees()), false), Palette.GOOD));
        column.getChildren().add(statementTotal("Net cost of schooling",
                tightMoney(toDollars(-em.getEducationNet()), false),
                em.getEducationNet() > 0 ? Palette.WARN : Palette.GOOD));

        column.getChildren().add(statementNote(
                "Both run at a deficit on purpose. What the deficit buys is on the Services "
                + "tab, in deaths avoided and diplomas issued rather than in this column."));

        /* ------------------------------ the pension gap ------------------------------ */
        if (em.getPensionShortfall() > 0) {
            column.getChildren().add(alert("The pension scheme does not pay for itself",
                    String.format("Contributions brought in %s and pensions cost %s, so %s "
                    + "came out of general revenue. It is pay-as-you-go: this month's "
                    + "workers pay this month's pensioners, so the gap widens with the "
                    + "dependency ratio rather than with anything you set.",
                    tightMoney(toDollars(em.getContributions())),
                    tightMoney(toDollars(em.getPensionsPaid())),
                    tightMoney(toDollars(em.getPensionShortfall())))));
        }
    }

    /* -------------------------------- THE OUTPUT -------------------------------- */

    void outputPage(VBox column, EconomyManager em, NationalAccounts na) {

        int population = ui.game.getPopulationManager().getPopulation();

        column.getChildren().add(statementHead("What the city produced"));

        /*
         * A STACKED BAR, NOT A RING, and the reason is arithmetic rather than
         * taste: net exports can be negative, and a ring cannot draw a wedge
         * that subtracts. A bar can, and the parts still read as a whole.
         */
        java.util.List<Slice> parts = java.util.List.of(
                new Slice("Consumption", na.getConsumption(), Palette.REVENUE_RAMP[0]),
                new Slice("Investment", na.getInvestment(), Palette.REVENUE_RAMP[2]),
                new Slice("Government", na.getGovernment(), Palette.REVENUE_RAMP[4]),
                new Slice("Net exports", na.getNetExports(),
                        na.getNetExports() < 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(stackedBar(parts, STATEMENT));
        column.getChildren().add(donutKey(parts, Math.abs(na.getConsumption())
                + Math.abs(na.getInvestment()) + Math.abs(na.getGovernment())
                + Math.abs(na.getNetExports()), annualGdp(na)));

        column.getChildren().add(statementTotal("GDP this month",
                tightMoney(toDollars(na.getGdp()), false), Palette.ACCENT));

        column.getChildren().add(subHead("Consumption"));
        column.getChildren().add(statementLine("Goods bought in shops",
                tightMoney(toDollars(na.getConsumptionGoods()), false)));
        column.getChildren().add(statementLine("Rent paid",
                tightMoney(toDollars(na.getConsumptionHousing()), false)));

        column.getChildren().add(subHead("Investment"));
        column.getChildren().add(statementLine("Construction put up",
                tightMoney(toDollars(na.getInvestmentConstruction()), false)));
        column.getChildren().add(statementLine("Change in stock",
                tightMoney(toDollars(na.getInvestmentInventories()), false)));
        column.getChildren().add(statementNote(
                "Stock built up is output that has been made and not yet sold, so it counts "
                + "the month it was produced rather than the month it clears."));

        column.getChildren().add(subHead("Government"));
        column.getChildren().add(statementLine("Services provided",
                tightMoney(toDollars(na.getGovernment()), false)));
        column.getChildren().add(statementNote(
                "The cost of running the city's own utilities. Land trading is not output "
                + "and is not counted here."));

        column.getChildren().add(subHead("Trade"));
        column.getChildren().add(statementLine("Steel exported",
                tightMoney(toDollars(na.getExports()), false), Palette.GOOD));
        column.getChildren().add(statementLine("Food imported",
                tightMoney(toDollars(-na.getImportsFood()), false)));
        column.getChildren().add(statementLine("Materials imported",
                tightMoney(toDollars(-na.getImportsMaterials()), false)));
        column.getChildren().add(statementLine("Scrap imported",
                tightMoney(toDollars(-na.getImportsRawMaterial()), false)));
        column.getChildren().add(statementTotal("Net exports",
                tightMoney(toDollars(na.getNetExports()), false),
                na.getNetExports() < 0 ? Palette.BAD : Palette.GOOD));

        /* ------------------------------- and growth ------------------------------- */
        column.getChildren().add(statementHead("Growth"));
        column.getChildren().add(statementLine("Annual GDP",
                tightMoney(toDollars(annualGdp(na)), false)));
        column.getChildren().add(statementLine("Trend, twelve-month average",
                tightMoney(toDollars(na.getTrendGdp()), false)));
        column.getChildren().add(statementLine("Per head, a year",
                tightMoney(toDollars(population > 0
                        ? annualGdp(na) / population : 0), false)));

        double mom = na.getMonthlyGrowthAnnualised();
        column.getChildren().add(statementLine("Month on month, annualised",
                String.format("%+.1f%%", mom * 100),
                mom < 0 ? Palette.BAD : mom > .02 ? Palette.GOOD : Palette.WARN));

        if (na.getMonthsRecorded() >= 13) {
            double yoy = na.getYearOnYearGrowth();
            column.getChildren().add(statementLine("Year on year",
                    String.format("%+.1f%%", yoy * 100),
                    yoy < 0 ? Palette.BAD : yoy > .02 ? Palette.GOOD : Palette.WARN));
        } else {
            column.getChildren().add(statementLine("Year on year",
                    "needs " + (13 - na.getMonthsRecorded()) + " more months",
                    Palette.TEXT_SPENT));
        }
        column.getChildren().add(statementLine("Months recorded",
                people(na.getMonthsRecorded()),
                na.getMonthsRecorded() < 12 ? Palette.WARN : null));
        if (gdpEstimated(na)) {
            column.getChildren().add(statementNote(String.format(
                    "Fewer than twelve, so the annual figure above is %d months scaled up "
                    + "rather than a year measured.", na.getMonthsRecorded())));
        }
        column.getChildren().add(statementNote(
                "Month-on-month annualised is one month multiplied up, so it swings hard on "
                + "a city this size. The year-on-year figure is the one to steer by."));

        Button graphs = new Button("See it over time  →");
        graphs.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                + " -fx-background-color: " + Palette.ACCENT_FILL + ";");
        graphs.setOnAction(e -> ui.historyScreen.showHistoryMenu());
        VBox.setMargin(graphs, new javafx.geometry.Insets(8, 0, 4, 0));
        column.getChildren().add(graphs);
    }
}
