package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * The policy tab: the four rows of levers - taxes, wages, money, promises -
 * the staged set every dial writes into, the ladder and the batch preview that
 * show what a proposal would cost before it is applied, and the pages each
 * lever opens.
 *
 * Split out of UserInterface on 2026-09-18: the nineteen banners from THE
 * POLICY TAB to PROMISES - the standing subsidies, the shell's members reached
 * through ui. Eighteen of them moved exactly as they were; the nineteenth, the
 * clinic's price and a premium, was added on 2026-09-19. The shell still reads
 * which area and page are open (policyArea, policyPage) for the rail and the
 * scroll memory, and the staged set (staged, isStaged, taxRaised, applyBar,
 * stageSlider, dropProposal, pinnedBands) because other screens offer the same
 * levers.
 */
final class PolicyScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    PolicyScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       THE POLICY TAB

       IT WAS A COLUMN OF NINE BUTTONS over two lines of monospace, and behind
       each one a screen that set a number and said almost nothing about what
       the number was doing. A city could be running a 15% income tax and a
       1.5% property tax and the only way to find out what either was worth was
       to leave this tab and go and read the Government one.

       FOUR ROWS, BY WHAT KIND OF LEVER IT IS - Jerus picked the grouping:

         TAXES     the two city rates, then the wage bands and the sectors that
                   move off them
         WAGES     the minimum wage, which is the floor under every other wage
                   in the city and therefore not a tax screen
         MONEY     the policy rate, and the currency reform
         PROMISES  pensions, the schools, and what the city will keep alive

       AND EVERY DIAL IS A PROPOSAL UNTIL IT IS APPLIED. "A before/after
       preview once you've moved the dial" - his call, and the right one. A tax
       rate is not a slider to scrub, it is a decision, and the screen now
       shows what the decision would cost before it costs anything. Moving a
       dial stages a change, the page draws what it would do against last
       month's real base, and nothing reaches the model until Apply.

       THE PREVIEWS ARE EXACT WHERE THE MODEL LETS THEM BE. The wage tax is
       recomputed job type by job type off the actual payroll; the property tax
       off the actual assessed values; each sector's profit tax off what it
       actually paid. Where a figure has to be scaled instead - the sales tax,
       whose input credits move with the rate - the note says so. And none of
       them know that a higher rate makes next month's base smaller, so the
       note says that too: a preview that pretends otherwise is worse than no
       preview at all.
       ===================================================================== */

    String policyArea = null;                  // null is the landing
    static final String POLICY_HOME   = "Everything";
    String policyPage = POLICY_HOME;

    /*
     * BY WHICH TAX IT IS, not by where the modifier lives.
     *
     * "The two rates / By wage band / By sector" was a fact about the code:
     * it filed a sector's profit, sales and property moves together because
     * they were all offsets on a Sector, and it called profit, sales and wage
     * "income tax" because they share a dial. A player asking what the city
     * charges on a payroll had to read two of the three pages and ignore most
     * of both.
     *
     * Everything reads and the four act. Jerus: "perhaps even controls to
     * filter out what type of taxes, so perhaps putting property, and it only
     * shows the controls to change property tax and so on."
     */
    static final String[] POLICY_TAX_PAGES =
            {"Everything", "Profit", "Sales", "Wage", "Property"};
    static final String[] POLICY_WAGE_PAGES    = {"The floor"};
    static final String[] POLICY_MONEY_PAGES   = {"The policy rate", "Currency reform"};
    static final String[] POLICY_PROMISE_PAGES = {"Pensions", "Out of work", "Health", "Schools", "Subsidies"};

    /* =====================================================================
       THE STAGED SET.

       It was ONE proposal - a key and a value - and moving a second dial threw
       the first away. That was right while a tax page was one lever at a time;
       it is wrong now that a page is a whole tax, because a budget is not one
       rate moved in isolation. Jerus: "stage several and apply together".

       A LinkedHashMap, so the foot bar lists them in the order they were
       moved. Cleared on every page and area change for the same reason it
       always was: a proposal is about the page you are looking at, and one
       carried to another page is one nobody can see.
       ===================================================================== */
    final java.util.LinkedHashMap<String, Double> policyStaged =
            new java.util.LinkedHashMap<>();

    /**
     * Every dial DRAWN this pass, by key.
     *
     * The foot bar is drawn last, so by the time it needs to name a staged
     * change and know how to apply it, the lever that owns that key has
     * already registered itself. That is what lets the bar apply a mixed batch
     * without a switch on the key's prefix - the dial carries its own setter,
     * so there is exactly one place that knows how to set each rate.
     */
    final java.util.LinkedHashMap<String, Lever> policyLevers =
            new java.util.LinkedHashMap<>();

    /** One dial: what it is, where it can go, how to read it, how to set it. */
    record Lever(String key, String name, double current,
                         double min, double max, double step,
                         java.util.function.DoubleFunction<String> read,
                         java.util.function.DoubleConsumer apply) { }

    boolean isStaged(String key) { return policyStaged.containsKey(key); }

    double staged(String key, double current) {
        Double value = policyStaged.get(key);
        return value == null ? current : value;
    }

    void stage(String key, double value) { policyStaged.put(key, value); }

    void unstage(String key) { policyStaged.remove(key); }

    void dropProposal() { policyStaged.clear(); }

    /** TaxPolicy.clamp(), which is private there and needed here to preview it. */
    static double clampRate(double value, double max) {
        return Math.max(0, Math.min(max, value));
    }

    /* =====================================================================
       THE LADDER

       Jerus: "i need a step ladder with 0.50% or 0.25% snapping."

       A drag alone cannot land on a quarter point reliably and a pair of
       buttons alone cannot cross sixty of them, so it is both: the bar gets
       you across the range and snaps hard when you let go, and the two buttons
       either side are worth exactly one step each. The reading is the truth
       about where it is, in accent when it has moved off what the city is
       actually charging.

       THE STEP IS A QUARTER POINT, except on property, which gets a twentieth.
       Same step, very different bite: 20.00% to 20.25% of income is a one-in-
       eighty change in what income tax raises, while 1.00% to 1.25% of
       assessed value is one in four. The screens say which step they are on.
       ===================================================================== */

    /** A quarter of a point - every rate that moves off the income tax. */
    static final double STEP_INCOME = .0025;

    /** A twentieth of a point - property, where a quarter is a quarter of the tax. */
    static final double STEP_PROPERTY = .0005;

    static final double LADDER_READ = 118;

    VBox taxLadder(Lever lever) {

        policyLevers.put(lever.key(), lever);

        double at = bounded(staged(lever.key(), lever.current()), lever);
        boolean off = moved(at, lever.current(), lever.step());

        javafx.scene.control.Slider bar =
                new javafx.scene.control.Slider(lever.min(), lever.max(), at);
        bar.setBlockIncrement(lever.step());
        bar.setMajorTickUnit(Math.max(lever.step(), 1e-9));
        bar.setMinorTickCount(0);
        bar.setSnapToTicks(true);
        double wide = STATEMENT - LADDER_READ - 2 * 28 - 30;
        bar.setPrefWidth(wide);
        bar.setMaxWidth(wide);

        Label reading = new Label(lever.read().apply(at));
        reading.setPrefWidth(LADDER_READ);
        reading.setMinWidth(LADDER_READ);
        reading.setAlignment(Pos.CENTER_RIGHT);
        reading.setStyle(Palette.figure(Palette.SIZE_BODY,
                off ? Palette.ACCENT : Palette.TEXT_HEAD));

        /*
         * The reading follows the drag and the COMMIT waits for the release.
         * Restaging on every pixel would redraw the whole screen under the
         * pointer, which is both slow and how you lose the thing you were
         * dragging.
         */
        bar.valueProperty().addListener((o, was, now) -> {
            double v = snapped(now.doubleValue(), lever.min(), lever.step());
            reading.setText(lever.read().apply(v));
            reading.setStyle(Palette.figure(Palette.SIZE_BODY,
                    moved(v, lever.current(), lever.step())
                            ? Palette.ACCENT : Palette.TEXT_HEAD));
        });
        Runnable commit = () -> propose(lever, bar.getValue());
        bar.setOnMouseReleased(e -> commit.run());
        bar.setOnKeyReleased(e -> commit.run());

        HBox row = new HBox(10,
                stepButton("−", at - lever.step() >= lever.min() - 1e-12,
                        () -> propose(lever, at - lever.step())),
                bar,
                stepButton("+", at + lever.step() <= lever.max() + 1e-12,
                        () -> propose(lever, at + lever.step())),
                reading);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);

        Label ends = new Label(lever.read().apply(lever.min())
                + "   to   " + lever.read().apply(lever.max())
                + "        one step is " + stepWord(lever.step())
                + (off ? "        it is " + lever.read().apply(lever.current()) : ""));
        ends.setStyle(Palette.words(Palette.SIZE_CAPTION,
                off ? Palette.ACCENT : Palette.TEXT_SPENT));

        VBox box = new VBox(1, row, ends);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 6 0 10 0;");
        return box;
    }

    /**
     * One notch, in the units the dial is read in.
     *
     * PADDING ZERO INLINE. The theme's .button rule carries 6 14 6 14, and on a
     * button pinned to 28px that leaves the glyph a content box narrower than
     * nothing - JavaFX draws an ellipsis. setPadding(EMPTY) does not fix it;
     * only an inline style beats a stylesheet. Third time in the interface,
     * after the build card's buttons and the shell's round ones.
     */
    Button stepButton(String glyph, boolean live, Runnable go) {
        Button button = new Button(glyph);
        button.setMinSize(28, 28);
        button.setPrefSize(28, 28);
        button.setMaxSize(28, 28);
        button.setDisable(!live);
        button.setStyle("-fx-padding: 0; -fx-font-size: 14px; -fx-font-weight: bold;"
                + " -fx-background-radius: 4; -fx-background-insets: 0;"
                + " -fx-background-color: " + (live ? Palette.CONTROL : Palette.PANEL) + ";"
                + " -fx-text-fill: " + (live ? Palette.TEXT_HEAD : Palette.TEXT_SPENT) + ";"
                + (live ? " -fx-cursor: hand;" : ""));
        button.setOnAction(e -> go.run());
        return button;
    }

    static String stepWord(double step) {
        return step >= .01 ? String.format("%.0f points", step * 100)
                           : String.format("%.2f points", step * 100);
    }

    static double bounded(double value, Lever lever) {
        return Math.max(lever.min(), Math.min(lever.max(), value));
    }

    /**
     * A dial has been moved to a value.
     *
     * Back at what the city is actually charging, it UNSTAGES rather than
     * staging a no-op - otherwise the foot bar would list "20.00% to 20.00%"
     * as a pending change and the Apply button would offer to do nothing.
     */
    void propose(Lever lever, double value) {
        double v = bounded(snapped(value, lever.min(), lever.step()), lever);
        if (moved(v, lever.current(), lever.step())) stage(lever.key(), v);
        else unstage(lever.key());
        showPolicyMenu();
    }


    /* =====================================================================
       WHAT THE WHOLE BATCH WOULD DO

       One preview for every dial on the page at once, struck the same way
       twice - once with what the city charges and once with what has been
       staged - so the DIFFERENCE is the answer even though neither figure is
       last month's actual revenue. Comparing a model against an actual would
       have made a batch that changed nothing read as a swing.
       ===================================================================== */
    double taxTotalUnder(boolean proposed) {

        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy p = em.getTaxPolicy();
        SalesTaxLedger vat = em.getSalesTaxLedger();
        SectorBooks books = ui.game.getSectorBooks();

        double base = proposed ? staged("income", p.getIncomeTaxRate()) : p.getIncomeTaxRate();
        double prop = proposed ? staged("property", p.getPropertyTaxRate())
                               : p.getPropertyTaxRate();
        double total = 0;

        for (Sector s : ui.game.getSectors().all()) {

            SectorBooks.SectorMonth m = books.get(s);
            double bearing = m == null ? 0 : Math.max(0, m.preTaxIncome());
            double pOff = proposed ? staged("profit:" + s.key(), p.getProfitOffset(s))
                                   : p.getProfitOffset(s);
            total += bearing * clampRate(base + pOff, TaxPolicy.MAX_INCOME_TAX);

            // Sales moves by the RATIO, not recomputed: this sector's input
            // credits are somebody else's rate and those have not moved.
            double net = vat.getNet(s.key());
            double nowRate = p.effectiveSalesRate(s);
            double sOff = proposed ? staged("sales:" + s.key(), p.getSalesOffset(s))
                                   : p.getSalesOffset(s);
            double atRate = clampRate(base + sOff, TaxPolicy.MAX_INCOME_TAX);
            total += nowRate > 1e-9 ? net * atRate / nowRate
                                    : vat.getTaxableSales(s.key()) * atRate;

            double rOff = proposed ? staged("prop:" + s.key(), p.getPropertyOffset(s))
                                   : p.getPropertyOffset(s);
            total += assessedUnder(em, p, s, proposed)
                    * clampRate(prop + rOff, TaxPolicy.MAX_PROPERTY_TAX) / 12;
        }

        double[] wages = em.getStaffedWagePerType();
        for (int i = 0; i < wages.length && i < JobType.values().length; i++) {
            if (wages[i] <= 0) continue;
            WageBand in = WageBand.of(JobType.values()[i]);
            double off = proposed ? staged("wage:" + in.name(), p.getWageOffset(in))
                                  : p.getWageOffset(in);
            total += wages[i] * clampRate(base + off, TaxPolicy.MAX_INCOME_TAX);
        }
        return total;
    }

    /**
     * What a sector is on the roll for, with a staged farmland relief applied.
     *
     * getAssessedValue() already nets the relief off the fields, so previewing
     * a MOVE in it means taking the old share back out and the new one in:
     * roll = structure + ground x (1 - relief), and ground is known.
     */
    double assessedUnder(EconomyManager em, TaxPolicy p, Sector s, boolean proposed) {
        double roll = em.getAssessedValue(s);
        if (!proposed || !Sectors.AGRICULTURE.equals(s.key()) || !isStaged("farmland")) {
            return roll;
        }
        double ground = em.landValueOf(s);
        double now = p.getFarmlandRelief();
        return Math.max(0, roll - ground * (1 - now) + ground * (1 - staged("farmland", now)));
    }

    /** The foot bar: everything pending, what it adds up to, and one button. */
    VBox stagedBar() { return stagedBar(true); }

    /**
     * The same bar with or without the tax total: the Schools page's dials
     * (2026-09-21) are not taxes, and a bar that footed them to "Tax a month:
     * no difference" would be answering a question nobody asked. The list
     * and the one button are what every page shares.
     */
    VBox stagedBar(boolean taxes) {

        VBox box = new VBox(0);
        box.setMaxWidth(STATEMENT);
        if (policyStaged.isEmpty()) return box;

        box.getChildren().add(statementHead("What you have staged"));

        int listed = 0;
        for (java.util.Map.Entry<String, Double> entry : policyStaged.entrySet()) {
            Lever lever = policyLevers.get(entry.getKey());
            if (lever == null) continue;   // not on this page; cannot be described
            box.getChildren().add(wouldBe(lever.name(),
                    lever.read().apply(lever.current()),
                    lever.read().apply(entry.getValue()), Palette.ACCENT));
            listed++;
        }
        if (listed == 0) return new VBox();

        if (taxes) {
            double now = taxTotalUnder(false);
            double then = taxTotalUnder(true);
            box.getChildren().add(wouldTotal("Tax a month",
                    money(now), money(then), then >= now ? Palette.GOOD : Palette.WARN));
            box.getChildren().add(statementLine("A difference of",
                    signedTight(then - now, false),
                    then >= now ? Palette.GOOD : Palette.BAD));
            box.getChildren().add(previewCaveat(
                    "Both figures are struck the same way against this month's books - the "
                    + "profit each sector made, the value each added, the payroll each paid "
                    + "and what each is assessed at - so the DIFFERENCE is the answer even "
                    + "where neither matches last month's actual revenue to the dollar."));
        }

        final int count = listed;
        Button go = new Button(count == 1 ? "Apply it" : "Apply all " + count);
        go.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                + " -fx-background-color: " + Palette.ACCENT_FILL + "; -fx-cursor: hand;");
        go.setOnAction(e -> { applyStaged(); showPolicyMenu(); });

        Button no = new Button("Discard");
        no.setOnAction(e -> { dropProposal(); showPolicyMenu(); });

        HBox bar = new HBox(8, go, no);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setMaxWidth(STATEMENT);
        bar.setStyle("-fx-padding: 10 0 4 0;");
        box.getChildren().add(bar);
        return box;
    }

    /** Each staged value through its own dial's setter, then the set is empty. */
    void applyStaged() {
        for (java.util.Map.Entry<String, Double> entry
                : new java.util.ArrayList<>(policyStaged.entrySet())) {
            Lever lever = policyLevers.get(entry.getKey());
            if (lever != null) lever.apply().accept(entry.getValue());
        }
        dropProposal();
    }

    /* =====================================================================
       THE FOUR TAXES

       The area used to be split by where a modifier LIVED - "the two rates",
       "by wage band", "by sector" - and that is a fact about the code rather
       than about the money. It also hid the thing a player most needs to know:
       "income tax" is not one tax, it is THREE, charged on three different
       things, and a sector's three offsets were filed together on one row
       whichever of them you had come to look at.

       It is split by which tax it is now. Everything reads; the four act.
       ===================================================================== */

    /** Profit, sales and wage all move off the income rate, so it is on all three. */
    void cityRateLever(VBox column, TaxPolicy policy, String which) {

        double base = policy.getIncomeTaxRate();
        column.getChildren().add(statementHead("The city rate"));
        column.getChildren().add(leverHead(pct2(base),
                "One rate on what every business earns, on the value they add, and on "
                + "every wage paid. " + which + " moves off this number rather than "
                + "replacing it - so this dial moves the other two taxes with it."));
        column.getChildren().add(taxLadder(new Lever("income", "The city rate", base,
                0, TaxPolicy.MAX_INCOME_TAX, STEP_INCOME,
                Money::pct2, policy::setIncomeTaxRate)));
    }

    /* ------------------------------ EVERYTHING ------------------------------ */

    void taxOverviewPage(VBox column) {

        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        NationalAccounts na = em.getNationalAccounts();

        double profit = na.getTaxBusiness() + na.getTaxIndustrial();
        double sales  = na.getTaxSales();
        double wage   = na.getTaxWage();
        double prop   = na.getPropertyTax();
        double all    = profit + sales + wage + prop;

        column.getChildren().add(statementHead("Where the money comes from"));
        column.getChildren().add(statementNote(
                "Last month, by tax. Three of the four are the city rate with a move off "
                + "it; the fourth is charged on what things are worth rather than on "
                + "anything anybody earned."));

        column.getChildren().add(taxSourceRow("Profit", profit, all,
                pct2(policy.getIncomeTaxRate()), "on what the sectors earned", "Profit"));
        column.getChildren().add(taxSourceRow("Sales", sales, all,
                pct2(policy.getIncomeTaxRate()), "on the value they add", "Sales"));
        column.getChildren().add(taxSourceRow("Wage", wage, all,
                pct2(policy.getIncomeTaxRate()), "off every payroll in the city", "Wage"));
        column.getChildren().add(taxSourceRow("Property", prop, all,
                String.format("%.2f%% a year", policy.getPropertyTaxRate() * 100),
                "on land and buildings, earning or not", "Property"));

        column.getChildren().add(statementTotal("Raised last month", money(all),
                all > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT));

        double annual = annualGdp(na);
        column.getChildren().add(statementLine("Which is, of everything the city made",
                annual > 0 ? String.format("%.1f%% of GDP", all * 12 / annual * 100)
                           : "no output to compare",
                Palette.TEXT_MUTED));
        column.getChildren().add(statementNote(
                "A sector that lost money paid no profit tax at all, which is why the "
                + "first line falls in a bad month with nobody having touched a rate."));

        cityRateLever(column, policy, "Profit, sales and wage tax each");

        if (isStaged("income")) {
            double want = staged("income", policy.getIncomeTaxRate());
            double base = policy.getIncomeTaxRate();
            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("Profit tax", money(profit),
                    money(scaled(profit, profitFactor(policy, base, want))), null));
            column.getChildren().add(wouldBe("Sales tax", money(sales),
                    money(scaled(sales, salesFactor(policy, base, want))), null));
            column.getChildren().add(wouldBe("Wage tax", money(wage),
                    money(wageTaxAtBase(policy, want)), null));
            column.getChildren().add(statementNote(
                    "Property is not on this list: it is charged on value rather than on "
                    + "income, and the city rate does not reach it."));
        }

        java.util.List<String[]> flags = policyFlags();
        if (!flags.isEmpty()) {
            column.getChildren().add(statementHead("What is currently biting"));
            for (String[] flag : flags) {
                column.getChildren().add(alert(flag[1], flag[2]));
            }
        }
    }

    /** One tax on the overview: what it raised, its share, its rate, and a door. */
    HBox taxSourceRow(String name, double raised, double all,
                              String rate, String what, String page) {

        Label heading = new Label(name);
        heading.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));

        Label says = new Label(what + "  ·  " + rate);
        says.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        VBox left = new VBox(0, heading, says);
        left.setAlignment(Pos.CENTER_LEFT);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label big = new Label(money(raised));
        big.setStyle(Palette.figure(Palette.SIZE_LEAD,
                raised > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT));

        Label share = new Label(all > 0 ? String.format("%.0f%% of the tax take", raised / all * 100)
                                        : "nothing raised");
        share.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        VBox right = new VBox(0, big, share);
        right.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(Palette.GAP, left, gap, right);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefWidth(STATEMENT);
        row.setMaxWidth(STATEMENT);
        String rest = "-fx-padding: 10 12 10 12; -fx-cursor: hand;";
        row.setStyle(rest + Palette.block(Palette.CONTROL));
        row.setOnMouseClicked(e -> {
            policyPage = page;
            dropProposal();
            ui.innerScrollAt.remove("showPolicyMenu:body");
            showPolicyMenu();
        });
        row.setOnMouseEntered(e -> row.setStyle(rest
                + Palette.block(Palette.RAISED, Palette.ACCENT)));
        row.setOnMouseExited(e -> row.setStyle(rest + Palette.block(Palette.CONTROL)));
        VBox.setMargin(row, new javafx.geometry.Insets(0, 0, 6, 0));
        return row;
    }

    /* -------------------------------- PROFIT -------------------------------- */

    void profitTaxPage(VBox column) {

        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        NationalAccounts na = em.getNationalAccounts();
        SectorBooks books = ui.game.getSectorBooks();
        double base = policy.getIncomeTaxRate();

        column.getChildren().add(statementHead("Profit tax"));
        column.getChildren().add(leverHead(money(na.getTaxBusiness() + na.getTaxIndustrial()),
                "Charged on what each sector earned before tax. A sector that lost money "
                + "pays nothing, so this is the half of the city's revenue that falls "
                + "exactly when the city needs it most."));

        cityRateLever(column, policy, "Every sector's profit rate");

        column.getChildren().add(statementHead("Every sector, and its move off that"));

        javafx.scene.layout.GridPane table = grid(
                new double[] {170, 130, 120, 130}, rightAfterFirst(4));
        gridHead(table, "", "pre-tax income", "taxed at", "paid");
        int line = 1;
        for (Sector sector : ui.game.getSectors().all()) {
            SectorBooks.SectorMonth month = books.get(sector);
            double bearing = month == null ? 0 : Math.max(0, month.preTaxIncome());
            table.add(gridCell(sector.label(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            table.add(gridCell(bearing > 0 ? money(bearing) : "—",
                    bearing > 0 ? Palette.TEXT_MUTED : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 1, line);
            table.add(gridCell(pct2(policy.effectiveProfitRate(sector)),
                    Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, line);
            table.add(gridCell(month == null ? "—" : money(month.tax()),
                    Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 3, line);
            line++;
        }
        column.getChildren().add(table);
        column.getChildren().add(statementNote(
                "A move is in POINTS off the city rate, so a sector left at zero is taxed "
                + "at exactly " + pct2(base) + " and follows the city rate wherever it "
                + "goes. The offsets are capped at "
                + String.format("%.0f", TaxPolicy.MAX_OFFSET * 100) + " points either way."));

        for (Sector sector : ui.game.getSectors().all()) {
            SectorBooks.SectorMonth month = books.get(sector);
            double bearing = month == null ? 0 : Math.max(0, month.preTaxIncome());
            double offset = policy.getProfitOffset(sector);
            final Sector s = sector;
            String key = "profit:" + sector.key();
            boolean touched = isStaged("income") || isStaged(key);
            double wantBase = staged("income", base);
            column.getChildren().add(bandLever(sector.label(), pts(offset),
                    arrow(touched, pct2(policy.effectiveProfitRate(sector)),
                            pct2(clampRate(wantBase + staged(key, offset),
                                    TaxPolicy.MAX_INCOME_TAX))),
                    bearing > 0 ? money(month.tax()) + " paid, on " + money(bearing)
                                + " of pre-tax income"
                                : "it made nothing to be taxed on last month",
                    offset));
            column.getChildren().add(taxLadder(new Lever(key,
                    sector.label() + ", profit", offset,
                    -TaxPolicy.MAX_OFFSET, TaxPolicy.MAX_OFFSET, STEP_INCOME,
                    Money::pts, v -> policy.setProfitOffset(s, v))));
        }
    }

    /* --------------------------------- SALES --------------------------------- */

    void salesTaxPage(VBox column) {

        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        NationalAccounts na = em.getNationalAccounts();
        SalesTaxLedger vat = em.getSalesTaxLedger();

        column.getChildren().add(statementHead("Sales tax"));
        column.getChildren().add(leverHead(money(na.getTaxSales()),
                "Charged on VALUE ADDED - what a sector sells, less the tax it already "
                + "paid on what it bought. A sector that only assembles somebody else's "
                + "parts pays on the assembly, which is why this raises money from a "
                + "chain without charging the same dollar twice."));

        cityRateLever(column, policy, "Every sector's sales rate");

        column.getChildren().add(statementHead("Every sector, and its move off that"));

        javafx.scene.layout.GridPane table = grid(
                new double[] {170, 130, 120, 130}, rightAfterFirst(4));
        gridHead(table, "", "taxable sales", "charged at", "net");
        int line = 1;
        for (Sector sector : ui.game.getSectors().all()) {
            double sold = vat.getTaxableSales(sector.key());
            table.add(gridCell(sector.label(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            table.add(gridCell(sold > 0 ? money(sold) : "—",
                    sold > 0 ? Palette.TEXT_MUTED : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 1, line);
            table.add(gridCell(pct2(policy.effectiveSalesRate(sector)),
                    Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, line);
            table.add(gridCell(vat.isInRefund(sector.key()) ? "in refund"
                            : money(vat.getNet(sector.key())),
                    vat.isInRefund(sector.key()) ? Palette.WARN : Palette.TEXT_HEAD,
                    Palette.SIZE_CAPTION, true), 3, line);
            line++;
        }
        column.getChildren().add(table);
        column.getChildren().add(statementNote(
                "\"In refund\" means a sector's credits on what it bought exceed the tax "
                + "on what it sold, so the city owes it rather than the other way round. "
                + "That is the mechanism working, not a fault - it happens to anybody "
                + "building stock or plant faster than they are selling."));

        for (Sector sector : ui.game.getSectors().all()) {
            double sold = vat.getTaxableSales(sector.key());
            double net = vat.getNet(sector.key());
            double offset = policy.getSalesOffset(sector);
            final Sector s = sector;
            String key = "sales:" + sector.key();
            boolean touched = isStaged("income") || isStaged(key);
            double wantBase = staged("income", policy.getIncomeTaxRate());
            column.getChildren().add(bandLever(sector.label(), pts(offset),
                    arrow(touched, pct2(policy.effectiveSalesRate(sector)),
                            pct2(clampRate(wantBase + staged(key, offset),
                                    TaxPolicy.MAX_INCOME_TAX))),
                    sold > 0 ? money(net) + " net, on " + money(sold) + " of taxable sales"
                             : vat.isInRefund(sector.key())
                                     ? "in refund - its credits exceed what it owes"
                                     : "it sold nothing taxable last month",
                    offset));
            column.getChildren().add(taxLadder(new Lever(key,
                    sector.label() + ", sales", offset,
                    -TaxPolicy.MAX_OFFSET, TaxPolicy.MAX_OFFSET, STEP_INCOME,
                    Money::pts, v -> policy.setSalesOffset(s, v))));
        }
    }

    /* ---------------------------------- WAGE ---------------------------------- */

    void wageTaxPage(VBox column) {

        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        NationalAccounts na = em.getNationalAccounts();
        double base = policy.getIncomeTaxRate();

        column.getChildren().add(statementHead("Wage tax"));
        column.getChildren().add(leverHead(money(na.getTaxWage()),
                "Taken off every payroll in the city before the household sees it. The "
                + "jobs are grouped by the education they need, which is the only "
                + "grouping the labour market itself uses."));

        cityRateLever(column, policy, "Every band's rate");

        column.getChildren().add(statementHead("The eleven jobs, in four bands"));

        javafx.scene.layout.GridPane table = grid(
                new double[] {170, 130, 120, 130}, rightAfterFirst(4));
        gridHead(table, "", "payroll", "taxed at", "raised");
        int line = 1;
        for (WageBand band : WageBand.values()) {
            double payroll = payrollIn(band);
            table.add(gridCell(band.label(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            table.add(gridCell(payroll > 0 ? money(payroll) : "—",
                    payroll > 0 ? Palette.TEXT_MUTED : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 1, line);
            table.add(gridCell(pct2(policy.effectiveWageRate(band)),
                    Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, line);
            table.add(gridCell(payroll > 0
                            ? money(wageTaxWith(policy, null, base, 0, band)) : "—",
                    Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 3, line);
            line++;
        }
        column.getChildren().add(table);

        /* ------------------- what a payslip actually loses ------------------- */
        payrollBurden(column, policy, base);

        for (WageBand band : WageBand.values()) {
            double offset = policy.getWageOffset(band);
            double payroll = payrollIn(band);
            final WageBand b = band;
            String key = "wage:" + band.name();
            boolean touched = isStaged("income") || isStaged(key);
            double wantBase = staged("income", base);
            column.getChildren().add(bandLever(band.label(), pts(offset),
                    arrow(touched, pct2(policy.effectiveWageRate(band)),
                            pct2(clampRate(wantBase + staged(key, offset),
                                    TaxPolicy.MAX_INCOME_TAX))),
                    payroll > 0 ? money(wageTaxWith(policy, null, base, 0, band))
                                + " a month, off " + money(payroll) + " of payroll"
                                : "nobody in the city holds one of these jobs",
                    offset));
            column.getChildren().add(taxLadder(new Lever(key,
                    band.label() + ", wage", offset,
                    -TaxPolicy.MAX_OFFSET, TaxPolicy.MAX_OFFSET, STEP_INCOME,
                    Money::pts, v -> policy.setWageOffset(b, v))));
        }
    }

    /**
     * The whole charge on a payslip, which no screen has ever totalled.
     *
     * Wage tax is not the only thing taken off a wage - the pension
     * contribution and the EI premium come off the same payroll, and both are
     * set two pages away under Promises. A player tuning the wage rate was
     * looking at one of three numbers and had no way to see the other two, let
     * alone what they came to together.
     *
     * Read-only here ON PURPOSE. Promises owns those two dials; a second copy
     * of a lever is how two screens start disagreeing about what the city
     * charges. The door goes to the page that does own them.
     */
    void payrollBurden(VBox column, TaxPolicy policy, double base) {

        double pension = policy.getContributionRate();
        double ei = policy.getEiPremiumRate();
        double health = policy.getHealthPremiumRate();

        column.getChildren().add(statementHead("What a payslip actually loses"));
        column.getChildren().add(statementLine("Wage tax, at the city rate",
                pct2(base), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("Pension contribution",
                pct2(pension), Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("EI premium",
                pct2(ei), Palette.TEXT_SPENT));
        // ...and the health premium, since the clinic had a price (2026-09-19).
        column.getChildren().add(statementLine("Health premium",
                pct2(health), Palette.TEXT_SPENT));
        column.getChildren().add(statementTotal("Off a wage in the middle band",
                pct2(base + pension + ei + health),
                base + pension + ei + health > .45 ? Palette.WARN : Palette.TEXT_HEAD));
        column.getChildren().add(statementNote(
                "Only the first is a tax and only the first is set here - the other three "
                + "are promises the city has made and they are charged on the same "
                + "payroll. A band with a move off the city rate pays that instead of "
                + "the first line; the other three are flat across every band."));

        Label door = new Label("Set the pension contribution and the premiums  ›");
        door.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.ACCENT)
                + " -fx-cursor: hand; -fx-padding: 2 0 8 0;");
        door.setOnMouseClicked(e -> {
            policyArea = "Promises";
            policyPage = "Pensions";
            dropProposal();
            ui.innerScrollAt.remove("showPolicyMenu:body");
            showPolicyMenu();
        });
        column.getChildren().add(door);
    }

    /* -------------------------------- PROPERTY -------------------------------- */

    void propertyTaxPage(VBox column) {

        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        NationalAccounts na = em.getNationalAccounts();
        double prop = policy.getPropertyTaxRate();

        column.getChildren().add(statementHead("Property tax"));
        column.getChildren().add(leverHead(String.format("%.2f%% a year", prop * 100),
                "Charged on what land and buildings are assessed at, whether or not the "
                + "owner earned anything. That is what makes it the steady half of the "
                + "city's revenue and the unpopular half."));

        column.getChildren().add(statementLine("Billed monthly at",
                String.format("%.4f%% of assessed value",
                        policy.getMonthlyPropertyTaxRate() * 100), Palette.TEXT_MUTED));
        column.getChildren().add(statementTotal("Raised last month", money(na.getPropertyTax()),
                na.getPropertyTax() > 0 ? Palette.TEXT_HEAD : Palette.TEXT_SPENT));

        column.getChildren().add(taxLadder(new Lever("property", "The property rate", prop,
                0, TaxPolicy.MAX_PROPERTY_TAX, STEP_PROPERTY,
                r -> String.format("%.2f%%", r * 100), policy::setPropertyTaxRate)));
        column.getChildren().add(statementNote(
                "A twentieth of a point a step rather than the quarter the other three "
                + "get, because the same step is a very different change: a quarter point "
                + "on a rate of one is a quarter of the tax."));

        column.getChildren().add(statementHead("Every sector, and what it is on the roll for"));

        javafx.scene.layout.GridPane assess = grid(
                new double[] {170, 130, 120, 130}, rightAfterFirst(4));
        gridHead(assess, "", "assessed", "a year", "billed");
        int line = 1;
        double billing = 0;
        for (Sector sector : ui.game.getSectors().all()) {
            double value = em.getAssessedValue(sector);
            if (value <= 0) continue;
            double bill = value * policy.effectiveMonthlyPropertyRate(sector);
            billing += bill;
            assess.add(gridCell(sector.label(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            assess.add(gridCell(money(value), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 1, line);
            assess.add(gridCell(String.format("%.2f%%",
                            policy.effectivePropertyRate(sector) * 100),
                    Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, line);
            assess.add(gridCell(money(bill), Palette.TEXT_HEAD,
                    Palette.SIZE_CAPTION, true), 3, line);
            line++;
        }
        column.getChildren().add(assess);
        // The column bills at TODAY'S rate and the line above it is history, so
        // the two differ the moment the rate is changed. Footing the column
        // makes that a comparison rather than a contradiction.
        column.getChildren().add(statementTotal("Which comes to at today's rate",
                money(billing), Palette.TEXT_HEAD));
        column.getChildren().add(statementNote(
                "The power and water plants are the city's own and exempt. Raising what "
                + "land sells for raises what every existing owner is assessed at, so the "
                + "Land Office moves this line without anybody touching this rate."));

        for (Sector sector : ui.game.getSectors().all()) {
            double assessed = em.getAssessedValue(sector);
            double offset = policy.getPropertyOffset(sector);
            final Sector s = sector;
            String key = "prop:" + sector.key();
            boolean touched = isStaged("property") || isStaged(key);
            double wantProp = staged("property", prop);
            column.getChildren().add(bandLever(sector.label(),
                    String.format("%+.2f pts", offset * 100),
                    arrow(touched,
                            String.format("%.2f%%",
                                    policy.effectivePropertyRate(sector) * 100),
                            String.format("%.2f%%",
                                    clampRate(wantProp + staged(key, offset),
                                            TaxPolicy.MAX_PROPERTY_TAX) * 100)),
                    assessed > 0
                            ? money(assessed * policy.effectiveMonthlyPropertyRate(sector))
                              + " a month, on " + money(assessed) + " assessed"
                            : "it owns nothing the city can assess",
                    offset));
            column.getChildren().add(taxLadder(new Lever(key,
                    sector.label() + ", property", offset,
                    -TaxPolicy.MAX_PROPERTY_TAX, TaxPolicy.MAX_PROPERTY_TAX, STEP_PROPERTY,
                    o -> String.format("%+.2f pts", o * 100),
                    v -> policy.setPropertyOffset(s, v))));
        }

        farmlandRelief(column, ui.game, policy, em);
    }

    /* =====================================================================
       THE LANDING
       ===================================================================== */

    void showPolicyMenu() {
        ui.clearMenu("showPolicyMenu", () -> showPolicyMenu());

        if (policyArea != null) {
            drawPolicyScreen();
            return;
        }

        TaxPolicy policy = ui.game.getEconomyManager().getTaxPolicy();
        DebtManager market = ui.game.getDebtManager();
        LabourMarket labour = ui.game.getLabourMarket();

        Label title = new Label("POLICY");
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        Label lead = new Label("Every number the city sets for itself, and what each "
                + "one is doing this month.");
        lead.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED)
                + " -fx-padding: 0 0 10 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        column.getChildren().add(policyRow("Taxes",
                "the two city rates, and the moves off them by band and by sector",
                money(taxRaised()),
                String.format("%.1f%% on income · %.2f%% a year on property",
                        policy.getIncomeTaxRate() * 100, policy.getPropertyTaxRate() * 100),
                Palette.GOOD, "Taxes", "Everything"));

        column.getChildren().add(policyRow("Wages",
                "the floor under every wage in the city, including the city's own",
                unitPrice(labour.getMinimumWage()),
                pinnedBands() > 0
                        ? pinnedBands() + " skill level"
                          + (pinnedBands() == 1 ? " is" : "s are") + " pinned against it"
                        : "no skill level is pinned against it",
                pinnedBands() > 0 ? Palette.WARN : Palette.ACCENT, "Wages", "The floor"));

        column.getChildren().add(policyRow("Money",
                "the price of money, and the units it is counted in",
                String.format("%.2f%%", market.getPolicyRate() * 100),
                String.format("the city itself borrows at %.2f%%", market.getRate() * 100),
                Palette.ACCENT, "Money", "The policy rate"));

        column.getChildren().add(policyRow("Promises",
                "pensions, EI, the clinic's price, school fees, and the sectors the city will not let fail",
                money(promisesCost()),
                "a month, out of the treasury",
                promisesCost() > 0 ? Palette.WARN : Palette.TEXT_SPENT,
                "Promises", "Pensions"));

        /* ======================== WHAT IS ACTUALLY BITING ========================
         *
         * Jerus asked for flags on the landing rather than a second summary:
         * a lever that is merely set is not news, and a lever that is forcing
         * somebody's hand is. Every one of these is a consequence the player
         * can undo from this tab.
         */
        column.getChildren().add(statementHead("What is biting"));

        java.util.List<String[]> flags = policyFlags();
        if (flags.isEmpty()) {
            column.getChildren().add(sentence(
                    "Nothing is binding. Every lever has room to move, no sector is shut "
                    + "out of credit, and no wage has run out of room to fall.",
                    Palette.GOOD));
        } else {
            for (String[] flag : flags) {
                column.getChildren().add(flagLine(flag[0], flag[1], flag[2]));
            }
        }

        ui.rootMenu.getChildren().addAll(title, lead, policyVitals(), ui.scrolled(column, 210));
    }

    /** Every tax line the city collected last month. */
    double taxRaised() {
        NationalAccounts na = ui.game.getEconomyManager().getNationalAccounts();
        return na.getTaxBusiness() + na.getTaxIndustrial() + na.getTaxSales()
                + na.getTaxWage() + na.getPropertyTax();
    }

    /** What the three promises cost the treasury in a month, net of what they collect. */
    double promisesCost() {
        EconomyManager em = ui.game.getEconomyManager();
        return em.getPensionShortfall() + ui.game.getEducation().getSubsidy()
                + ui.game.getTotalSubsidyPaid()
                // EI past its premiums, and the students' grants (2026-09-11)
                + Math.max(0, em.getEiBenefits() - em.getEiPremiums()) + em.getStudentGrants();
    }

    int pinnedBands() {
        LabourMarket labour = ui.game.getLabourMarket();
        PopulationManager people = ui.game.getPopulationManager();
        int pinned = 0;
        for (WageBand band : WageBand.values()) {
            if (labour.isPinned(band) && people.surplusInBand(band) > 0) pinned++;
        }
        return pinned;
    }

    HBox policyVitals() {

        TaxPolicy policy = ui.game.getEconomyManager().getTaxPolicy();
        DebtManager market = ui.game.getDebtManager();
        LabourMarket labour = ui.game.getLabourMarket();
        EconomyManager em = ui.game.getEconomyManager();

        return vitalsBar(
                limitCell("TAX A MONTH", money(taxRaised()),
                        "profit, sales, wages and property",
                        taxRaised() > 0 ? Palette.GOOD : Palette.TEXT_SPENT),
                limitCell("THE FLOOR", unitPrice(labour.getMinimumWage()),
                        "every wage is a multiple of it", Palette.ACCENT),
                limitCell("THE POLICY RATE",
                        String.format("%.2f%%", market.getPolicyRate() * 100),
                        String.format("savers are paid %.2f%%",
                                ui.game.getBank().depositRate() * 100),
                        Palette.ACCENT),
                limitCell("PROMISES", money(promisesCost()),
                        String.format("%s of it is the pension gap",
                                money(em.getPensionShortfall())),
                        promisesCost() > 0 ? Palette.WARN : Palette.TEXT_SPENT));
    }

    /**
     * The levers that are currently forcing something.
     *
     * @return {tone, heading, what it means} - never a lever that is merely
     *         set to something, only one whose setting is having an effect
     *         somebody would want to know about.
     */
    java.util.List<String[]> policyFlags() {

        java.util.List<String[]> out = new java.util.ArrayList<>();

        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        LabourMarket labour = ui.game.getLabourMarket();
        PopulationManager people = ui.game.getPopulationManager();
        Education schools = ui.game.getEducation();
        DebtManager market = ui.game.getDebtManager();
        PriceIndex px = ui.game.getPriceIndex();

        /* --------------------------- the wage floor --------------------------- */
        for (WageBand band : WageBand.values()) {
            double surplus = people.surplusInBand(band);
            if (labour.isPinned(band) && surplus > 0) {
                out.add(new String[] {Palette.WARN, band.label() + " cannot get any cheaper",
                        String.format("%,.0f more of them than there is work, and their wage "
                        + "is already at the floor. They leave the city instead of taking "
                        + "less.", surplus)});
            }
        }

        /* ---------------------------- who is shut out ---------------------------- */
        BusinessDebtManager credit = em.getBusinessDebtManager();
        for (String sector : Sectors.KEYS) {
            if (credit.isBorrowingBlocked(sector)) {
                out.add(new String[] {Palette.BAD, sector + " cannot borrow",
                        credit.getBlockedMonths(sector) + " more months of it. A sector in "
                        + "default cannot build, and a subsidy is the only thing on this "
                        + "tab that reaches it."});
            }
        }

        /* ------------------------------- tuition ------------------------------- */
        int shut = 0;
        String worst = null;
        for (EducationType type : EducationType.values()) {
            if (type == EducationType.NONE) continue;
            double pocket = schools.feeFor(type) * (1 - schools.getTuitionSubsidy());
            WageBand from = type.requires();
            double wage = from == null ? labour.getWage(JobType.NO_DIPLOMA)
                                       : bestWageIn(labour, from);
            if (wage > 0 && pocket / wage >= Education.MAX_BURDEN) {
                shut++;
                if (worst == null) worst = type.getLabel();
            }
        }
        if (shut > 0) {
            out.add(new String[] {Palette.BAD,
                    shut == 1 ? "Nobody can afford " + worst
                              : shut + " courses cost more than anybody can carry",
                    String.format("At %.0f%% of a month's wage nobody enrols at all, and the "
                    + "city's share of tuition is %.0f%%. The buildings can be there and "
                    + "produce nobody.",
                    Education.MAX_BURDEN * 100, schools.getTuitionSubsidy() * 100)});
        }

        /* ------------------------------- pensions ------------------------------- */
        double coverage = em.getPensionCoverage();
        if (coverage < .5 && em.getPensionsPaid() > 0) {
            out.add(new String[] {Palette.WARN, "The pension is mostly general revenue",
                    String.format("Workers' contributions cover %.0f%% of what seniors are "
                    + "paid. The other %s a month comes out of the same pot as everything "
                    + "else.", coverage * 100, money(em.getPensionShortfall()))});
        }

        /* ------------------------------ subsidies ------------------------------ */
        int protectedCount = 0;
        for (Sector sector : ui.game.getSectors().all()) {
            if (ui.game.isAutoSubsidised(sector)) protectedCount++;
        }
        if (protectedCount > 0 && ui.game.getTotalSubsidyPaid() > 0) {
            out.add(new String[] {Palette.WARN,
                    protectedCount + " sector" + (protectedCount == 1 ? " is" : "s are")
                            + " being kept alive",
                    money(ui.game.getTotalSubsidyPaid()) + " last month. A protected sector "
                    + "never sells its capacity, which is the point, and it never has to "
                    + "fix itself either."});
        }

        /* --------------------------- the price of money --------------------------- */
        if (px.hasRate()) {
            double advised = market.advisedPolicyRate(px.inflation());
            if (Math.abs(advised - market.getPolicyRate()) >= .01) {
                out.add(new String[] {Palette.ACCENT, "The rule disagrees with the dial",
                        String.format("The policy rate is %.2f%% and the rule would set it "
                        + "at %.2f%%. Inflation is running at %+.1f%% against a %.0f%% "
                        + "target.", market.getPolicyRate() * 100, advised * 100,
                        px.inflation() * 100, DebtManager.INFLATION_TARGET * 100)});
            }
        }

        /* ---------------------------- the money itself ---------------------------- */
        if (ui.game.canReformCurrency()) {
            out.add(new String[] {Palette.ACCENT, "The money could be reformed",
                    String.format("Prices are %.1f times what they were at founding. A "
                    + "reform restates every number in the city at once and changes "
                    + "nothing else.", ui.game.getPriceIndex().getIndex())});
        }

        /* ------------------------------ at the stops ------------------------------ */
        if (policy.getIncomeTaxRate() >= TaxPolicy.MAX_INCOME_TAX - 1e-9) {
            out.add(new String[] {Palette.WARN, "Income tax is at its legal maximum",
                    String.format("%.0f%%. There is no more revenue to be had from this "
                    + "lever, whatever the budget says.", TaxPolicy.MAX_INCOME_TAX * 100)});
        }

        return out;
    }

    VBox flagLine(String tone, String heading, String body) {

        Label head = new Label(heading);
        head.setStyle(Palette.words(Palette.SIZE_BODY, tone) + " -fx-font-weight: bold;");

        Label says = new Label(body);
        says.setWrapText(true);
        says.setMaxWidth(STATEMENT - 26);
        says.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        VBox box = new VBox(1, head, says);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 7 12 8 12;" + Palette.block(Palette.CONTROL)
                + " -fx-border-color: " + tone + "; -fx-border-width: 0 0 0 2;");
        VBox.setMargin(box, new javafx.geometry.Insets(0, 0, 5, 0));
        return box;
    }

    HBox policyRow(String name, String blurb, String figure, String sub,
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
            policyArea = area;
            policyPage = page;
            dropProposal();
            ui.innerScrollAt.remove("showPolicyMenu:body");
            showPolicyMenu();
        });
        row.setOnMouseEntered(e -> row.setStyle(rest
                + Palette.block(Palette.RAISED, Palette.ACCENT)));
        row.setOnMouseExited(e -> row.setStyle(rest + Palette.block(Palette.CONTROL)));
        VBox.setMargin(row, new javafx.geometry.Insets(0, 0, 6, 0));
        return row;
    }

    /* =====================================================================
       ONE SUBJECT, ITS OWN STRIP
       ===================================================================== */

    void drawPolicyScreen() {

        // Redrawn from scratch every pass, so the register of what is on screen
        // is too - a lever from the page before is not a lever you can apply.
        policyLevers.clear();

        String[] pages = switch (policyArea) {
            case "Wages"    -> POLICY_WAGE_PAGES;
            case "Money"    -> POLICY_MONEY_PAGES;
            case "Promises" -> POLICY_PROMISE_PAGES;
            default         -> POLICY_TAX_PAGES;
        };

        Label title = new Label(policyArea.toUpperCase());
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        switch (policyArea) {
            case "Wages" -> minimumWagePage(column);
            case "Money" -> {
                if ("Currency reform".equals(policyPage)) currencyReformPage(column);
                else                                      policyRatePage(column);
            }
            case "Promises" -> {
                switch (policyPage) {
                    case "Schools"     -> schoolsPage(column);
                    case "Subsidies"   -> subsidyPage(column);
                    case "Out of work" -> outOfWorkPage(column);
                    case "Health"      -> healthPage(column);
                    default            -> pensionPage(column);
                }
                // The Schools page registers its five dials so one bar
                // applies them (2026-09-21); a page that registers nothing
                // draws nothing here, and keeps its own apply bars.
                column.getChildren().add(stagedBar(false));
            }
            default -> {
                switch (policyPage) {
                    case "Profit"   -> profitTaxPage(column);
                    case "Sales"    -> salesTaxPage(column);
                    case "Wage"     -> wageTaxPage(column);
                    case "Property" -> propertyTaxPage(column);
                    default         -> taxOverviewPage(column);
                }
                // Every dial on the page has registered itself by now, which
                // is what lets one bar describe and apply a mixed batch.
                column.getChildren().add(stagedBar());
            }
        }

        Button back = new Button("All of policy");
        back.setOnAction(e -> {
            policyArea = null;
            dropProposal();
            ui.innerScrollAt.remove("showPolicyMenu:body");
            showPolicyMenu();
        });

        // A strip of one chip is a chip that does nothing, so Wages has none.
        if (pages.length > 1) {
            javafx.scene.layout.FlowPane strip =
                    chipStrip(pages, policyPage, Palette.SIZE_LABEL, name -> {
                        policyPage = name;
                        dropProposal();
                        ui.innerScrollAt.remove("showPolicyMenu:body");
                        showPolicyMenu();
                    });
            strip.setStyle("-fx-padding: 8 0 10 0;");
            ui.rootMenu.getChildren().addAll(title, policyVitals(), strip,
                    ui.scrolled(column, 250), back);
        } else {
            ui.rootMenu.getChildren().addAll(title, policyVitals(),
                    ui.scrolled(column, 210), back);
        }
    }

    /* =====================================================================
       THE PIECES A LEVER IS MADE OF

       Every page below is the same four things in the same order: what it
       reads now, what that is doing, the dial, and - only once the dial has
       been moved - what it would do instead. Doing it in one order on every
       page is most of what makes nine levers feel like one screen.
       ===================================================================== */

    /**
     * The lever itself: a slider that STAGES a change rather than making one.
     *
     * Jerus: "i think the tax should a slider, as well as the other stuff in
     * policy" - and he is right. A rate is a continuous quantity, and a row of
     * step buttons makes the player do arithmetic to get anywhere they can
     * already see on a scale.
     *
     * THE READING TRACKS THE THUMB, THE PREVIEW WAITS FOR THE RELEASE. Rebuilding
     * the screen on every pixel of a drag would make the drag unusable, so the
     * figure beside the slider updates live off the value property and only
     * letting go stages the proposal and redraws the before/after under it.
     *
     * Dropping the thumb back where it started clears the proposal rather than
     * staging a change of nothing, which is what makes the slider its own
     * cancel button - ITS OWN, since 2026-09-21: it unstages its key, as
     * propose() does for a ladder, where it used to drop the whole set. On a
     * page of one dial that is the same thing; on the Schools page's five it
     * was one thumb put back throwing four other decisions away.
     *
     * THE REDRAW IS THE CALLER'S SCREEN, not this one. The transit fare is a
     * lever on the Services tab drawn with this same slider, and until
     * 2026-09-18 letting go of it redrew the policy screen - the player was
     * carried to a tab the dial is not on. ui.redraw() draws whatever screen
     * registered itself last, which on this tab is showPolicyMenu().
     */
    VBox stageSlider(String key, double current, double min, double max,
                             double step, java.util.function.DoubleFunction<String> label) {

        double at = Math.max(min, Math.min(max, staged(key, current)));

        javafx.scene.control.Slider bar = new javafx.scene.control.Slider(min, max, at);
        bar.setBlockIncrement(step);
        bar.setMajorTickUnit(Math.max(step, 1e-9));
        bar.setMinorTickCount(0);
        bar.setSnapToTicks(true);
        bar.setPrefWidth(STATEMENT - 170);
        bar.setMaxWidth(STATEMENT - 170);

        Label reading = new Label(label.apply(at));
        reading.setPrefWidth(130);
        reading.setMinWidth(130);
        reading.setAlignment(Pos.CENTER_RIGHT);
        reading.setStyle(Palette.figure(Palette.SIZE_BODY, moved(at, current, step)
                ? Palette.ACCENT : Palette.TEXT_HEAD));

        bar.valueProperty().addListener((o, was, now) -> {
            double v = snapped(now.doubleValue(), min, step);
            reading.setText(label.apply(v));
            reading.setStyle(Palette.figure(Palette.SIZE_BODY, moved(v, current, step)
                    ? Palette.ACCENT : Palette.TEXT_HEAD));
        });

        Runnable commit = () -> {
            double v = Math.max(min, Math.min(max, snapped(bar.getValue(), min, step)));
            if (moved(v, current, step)) stage(key, v); else unstage(key);
            ui.redraw();   // whichever screen the dial is on - the fare dial lives on Services
        };
        bar.setOnMouseReleased(e -> commit.run());
        bar.setOnKeyReleased(e -> commit.run());

        HBox row = new HBox(Palette.GAP, bar, reading);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);

        Label ends = new Label(label.apply(min) + "   to   " + label.apply(max)
                + (moved(at, current, step) ? "        it is " + label.apply(current) : ""));
        ends.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_SPENT));

        VBox box = new VBox(0, row, ends);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 6 0 8 0;");
        return box;
    }

    /** Apply, or put it back. Nothing else on this bar. */
    HBox applyBar(String label, Runnable apply) {

        Button go = new Button(label);
        go.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                + " -fx-background-color: " + Palette.ACCENT_FILL + "; -fx-cursor: hand;");
        go.setOnAction(e -> {
            apply.run();
            dropProposal();
            ui.redraw();
        });

        Button no = new Button("Leave it as it is");
        no.setOnAction(e -> { dropProposal(); ui.redraw(); });

        HBox bar = new HBox(8, go, no);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setMaxWidth(STATEMENT);
        bar.setStyle("-fx-padding: 10 0 4 0;");
        return bar;
    }

    /* =====================================================================
       TAXES - the two rates
       ===================================================================== */

    /**
     * The one dial that decides whether the city keeps its fields.
     *
     * A farm is the only building in the game whose cost is the GROUND rather
     * than the structure, and the property tax is struck on what that ground
     * would FETCH. In a young city that is nothing; in a grown one it is more
     * than the field can possibly grow. Assessing farmland at USE value instead
     * is what Ontario's Farm Property Class, Nova Scotia's resource rate and
     * California's Williamson Act all do, for exactly this reason, and this is
     * the same lever with the same cost stated in the same place.
     *
     * The cost is shown as money rather than as a principle, because that is
     * the decision: this many dollars a month of tax the city is choosing not
     * to collect, against the fields it would otherwise lose.
     */
    void farmlandRelief(javafx.scene.layout.VBox column, Game game,
                                TaxPolicy policy, EconomyManager em) {

        Sector fields = game.getSectors().byKey(Sectors.AGRICULTURE);
        if (fields == null) return;

        double relief = policy.getFarmlandRelief();
        double want = staged("farmland", relief);
        double ground = em.landValueOf(fields);
        double monthly = policy.effectiveMonthlyPropertyRate(Sectors.AGRICULTURE);

        column.getChildren().add(statementHead("Farmland, and what it is assessed at"));
        column.getChildren().add(leverHead(String.format("%.0f%% relieved", relief * 100),
                "A field is worth what a developer would pay for it and grows what a "
                + "farmer can grow on it. Tax the first and you lose the second, which is "
                + "what happened to every market garden that was ever within a cart ride "
                + "of a growing town. Relieving the land half is what real jurisdictions "
                + "do; the cost is the tax you do not collect."));

        column.getChildren().add(statementLine("The ground under the fields", money(ground),
                ground > 0 ? Palette.TEXT_BODY : Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("On the roll at",
                money(ground * (1 - relief)), Palette.TEXT_MUTED));
        column.getChildren().add(statementTotal("Tax forgone, a month",
                money(ground * relief * monthly),
                relief > 0 && ground > 0 ? Palette.WARN : Palette.TEXT_MUTED));

        if (ground <= 0) {
            column.getChildren().add(statementNote(
                    "Nothing under cultivation, so the dial costs nothing today. It decides "
                    + "whether a field sunk now survives the city reaching it."));
        }

        column.getChildren().add(taxLadder(new Lever("farmland", "Farmland relieved",
                relief, 0, 1, .05, r -> String.format("%.0f%%", r * 100),
                policy::setFarmlandRelief)));

        if (isStaged("farmland")) {
            double now = ground * relief * monthly;
            double then = ground * want * monthly;
            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("Relieved",
                    String.format("%.0f%%", relief * 100),
                    String.format("%.0f%%", want * 100), Palette.ACCENT));
            column.getChildren().add(wouldBe("Farmland on the roll",
                    money(ground * (1 - relief)), money(ground * (1 - want)),
                    Palette.TEXT_MUTED));
            column.getChildren().add(wouldTotal("Raised from it a month",
                    money(ground * (1 - relief) * monthly),
                    money(ground * (1 - want) * monthly),
                    then <= now ? Palette.WARN : Palette.GOOD));
            column.getChildren().add(previewCaveat(
                    "Exact against today's land price. What it actually decides is whether "
                    + "the next field is worth sinking, which shows up years later."));
        }
    }

    /* ------------------------- the arithmetic behind it ------------------------- */

    static double scaled(double amount, double factor) {
        return Double.isFinite(factor) ? amount * factor : 0;
    }

    /** How the profit tax moves with the base rate, weighted by what each sector paid. */
    double profitFactor(TaxPolicy policy, double from, double to) {
        SectorBooks books = ui.game.getSectorBooks();
        double before = 0, after = 0;
        for (Sector s : ui.game.getSectors().all()) {
            SectorBooks.SectorMonth m = books.get(s);
            if (m == null) continue;
            double bearing = Math.max(0, m.preTaxIncome());
            if (bearing <= 0) continue;
            before += bearing * clampRate(from + policy.getProfitOffset(s),
                    TaxPolicy.MAX_INCOME_TAX);
            after  += bearing * clampRate(to + policy.getProfitOffset(s),
                    TaxPolicy.MAX_INCOME_TAX);
        }
        return before > 0 ? after / before : 0;
    }

    /** ...and the sales tax, weighted by what each sector actually sold. */
    double salesFactor(TaxPolicy policy, double from, double to) {
        SalesTaxLedger vat = ui.game.getEconomyManager().getSalesTaxLedger();
        double before = 0, after = 0;
        for (Sector s : ui.game.getSectors().all()) {
            double sold = vat.getTaxableSales(s.key());
            if (sold <= 0) continue;
            before += sold * clampRate(from + policy.getSalesOffset(s),
                    TaxPolicy.MAX_INCOME_TAX);
            after  += sold * clampRate(to + policy.getSalesOffset(s),
                    TaxPolicy.MAX_INCOME_TAX);
        }
        return before > 0 ? after / before : 0;
    }

    /**
     * The wage tax, recomputed rather than scaled.
     *
     * The one line on the page that can be exact, and it is exact for the same
     * reason TaxPolicy refuses to average: the tax is summed job type by job
     * type off the staffed payroll, so a change to the base rate can be run
     * through the same loop the city bills with.
     */
    double wageTaxAtBase(TaxPolicy policy, double base) {
        return wageTaxWith(policy, null, base, 0);
    }

    /**
     * @param band     a band whose offset is being previewed, or null for none
     * @param base     the base rate to price at
     * @param offset   that band's proposed offset
     * @param onlyBand when true, only that band's tax is returned
     */
    double wageTaxWith(TaxPolicy policy, WageBand band, double base, double offset) {
        return wageTaxWith(policy, band, base, offset, null);
    }

    double wageTaxWith(TaxPolicy policy, WageBand band, double base,
                               double offset, WageBand onlyBand) {
        double[] wages = ui.game.getEconomyManager().getStaffedWagePerType();
        double tax = 0;
        for (int i = 0; i < wages.length && i < JobType.values().length; i++) {
            if (wages[i] <= 0) continue;
            JobType type = JobType.values()[i];
            WageBand in = WageBand.of(type);
            if (onlyBand != null && in != onlyBand) continue;
            double rate = in == band
                    ? clampRate(base + offset, TaxPolicy.MAX_INCOME_TAX)
                    : clampRate(base + policy.getWageOffset(in), TaxPolicy.MAX_INCOME_TAX);
            tax += wages[i] * rate;
        }
        return Math.max(0, tax);
    }

    /** The payroll a band carries, which is what its rate is charged on. */
    double payrollIn(WageBand band) {
        double[] wages = ui.game.getEconomyManager().getStaffedWagePerType();
        double total = 0;
        for (int i = 0; i < wages.length && i < JobType.values().length; i++) {
            if (wages[i] > 0 && WageBand.of(JobType.values()[i]) == band) total += wages[i];
        }
        return total;
    }

    /* =====================================================================
       TAXES - by wage band
       ===================================================================== */

    /**
     * The heading row of one band or one sector's line: what it is, and where it sits.
     *
     * @param effective what this row is charged at - and, when something on the
     *        page is staged that reaches it, what it WOULD be charged at,
     *        written "20.00%  \u2192  20.25%".
     *
     *        The per-lever preview blocks are gone: with ten sectors on a page
     *        and a batch staged across several of them, ten "What it would do"
     *        sections is a page nobody can read. The move belongs on the row it
     *        happened to, and the totals belong once, at the foot.
     *
     *        It also answers a question the old screens could not: stage the
     *        CITY rate alone and every row on the page shows what that does to
     *        it, sector by sector, without touching anything else.
     */
    VBox bandLever(String name, String offset, String effective,
                           String note, double signedOffset) {

        Label what = new Label(name);
        what.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label move = new Label(offset);
        move.setStyle(Palette.figure(Palette.SIZE_CAPTION,
                signedOffset < 0 ? Palette.GOOD
                        : signedOffset > 0 ? Palette.WARN : Palette.TEXT_SPENT));

        Label at = new Label(effective);
        at.setStyle(Palette.figure(Palette.SIZE_BODY,
                effective.contains("\u2192") ? Palette.ACCENT : Palette.TEXT_HEAD));

        HBox top = new HBox(Palette.GAP_LOOSE, what, gap, move, at);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setMaxWidth(STATEMENT);
        top.setPrefWidth(STATEMENT);

        Label says = new Label(note);
        says.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        VBox box = new VBox(0, top, says);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 12 0 2 0;");
        return box;
    }

    /* =====================================================================
       TAXES - by sector
       ===================================================================== */

    /* =====================================================================
       WAGES - the floor

       EVERY OTHER WAGE IS A MULTIPLE OF THIS ONE, which is what makes it a
       decision rather than a dial: raising it does not merely protect
       labourers, it lifts the doctor's pay in the same proportion and the
       city's own hospital payroll with it.

       And it is the reason unemployment exists in this game at all. The
       unskilled band's base IS the minimum wage, so an oversupply of labourers
       cannot be priced away - the wage has nowhere to fall. The adjustment has
       to happen in people instead, and Migration turns that surplus into
       departures. Lower the floor and the market clears by price; raise it and
       it clears by emigration. That trade is the screen.
       ===================================================================== */

    void minimumWagePage(VBox column) {

        LabourMarket market = ui.game.getLabourMarket();
        PopulationManager people = ui.game.getPopulationManager();
        Education schools = ui.game.getEducation();
        Healthcare hospitals = ui.game.getHealthcare();

        double floor = market.getMinimumWage();
        double want = staged("floor", floor);

        column.getChildren().add(statementHead("The floor under every wage"));
        column.getChildren().add(leverHead(unitPrice(floor) + " a month",
                "Every wage in the city is a multiple of this number, so moving it moves "
                + "all of them - including the doctors and nurses the city pays out of its "
                + "own treasury. Wages walk to their new level over about a year rather "
                + "than jumping, so a change made now is a bill met slowly."));

        /* -------------------------------- the ladder -------------------------------- */
        javafx.scene.layout.GridPane ladder = grid(
                new double[] {220, 120, 110, 110},
                new javafx.geometry.HPos[] {javafx.geometry.HPos.LEFT,
                                            javafx.geometry.HPos.RIGHT,
                                            javafx.geometry.HPos.RIGHT,
                                            javafx.geometry.HPos.RIGHT});
        gridHead(ladder, "", "multiple", "now", isStaged("floor") ? "after" : "");

        int line = 1;
        for (PayTier tier : PayTier.values()) {
            double ratio = tier.getMonthlyWage() / PayTier.UNSKILLED.getMonthlyWage();
            ladder.add(gridCell(tier.getLabel(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            ladder.add(gridCell(String.format("%.2fx", ratio), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 1, line);
            ladder.add(gridCell(unitPrice(floor * ratio), Palette.TEXT_HEAD,
                    Palette.SIZE_CAPTION, true), 2, line);
            if (isStaged("floor")) {
                ladder.add(gridCell(unitPrice(want * ratio),
                        want >= floor ? Palette.GOOD : Palette.WARN,
                        Palette.SIZE_CAPTION, true), 3, line);
            }
            line++;
        }
        column.getChildren().add(ladder);

        /* ------------------------------ who is pinned ------------------------------ */
        column.getChildren().add(statementHead("What it is doing now"));

        boolean anyPinned = false;
        for (WageBand band : WageBand.values()) {
            double surplus = people.surplusInBand(band);
            boolean pinned = market.isPinned(band);
            if (pinned && surplus > 0) anyPinned = true;
            column.getChildren().add(statementLine(band.label(),
                    (pinned ? "wage cannot fall further" : "wage still has room")
                            + String.format("   ·   %,.0f spare", surplus),
                    pinned && surplus > 0 ? Palette.WARN
                            : pinned ? Palette.TEXT_MUTED : Palette.TEXT_BODY));
        }

        column.getChildren().add(sentence(anyPinned
                ? "At least one skill level is oversupplied AND cannot get any cheaper, so "
                + "those workers are leaving the city rather than taking a pay cut. That is "
                + "what a binding minimum wage does: the market clears in people instead of "
                + "in price. Lowering the floor would keep them, and pay them less."
                : "No skill level is pinned against the floor, so the labour market is "
                + "clearing on price alone and the minimum wage is not currently binding "
                + "on anybody.",
                anyPinned ? Palette.WARN : Palette.GOOD));

        /* --------------------------- and the city's own bill --------------------------- */
        double cityPayroll = schools.getPayroll() + hospitals.getPayroll();
        column.getChildren().add(statementHead("What the city itself pays"));
        column.getChildren().add(statementLine("Teachers", money(schools.getPayroll())));
        column.getChildren().add(statementLine("Doctors and nurses",
                money(hospitals.getPayroll())));
        column.getChildren().add(statementTotal("Its own payroll", money(cityPayroll),
                Palette.WARN));

        column.getChildren().add(stageSlider("floor", floor,
                market.getMinSettable(), market.getMaxSettable(),
                (market.getMaxSettable() - market.getMinSettable()) / 200,
                Money::unitPrice));

        if (isStaged("floor")) {
            double move = floor > 0 ? want / floor : 1;
            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("The floor",
                    unitPrice(floor), unitPrice(want),
                    want >= floor ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(wouldBe("Every other wage",
                    "as it is", String.format("%+.0f%%", (move - 1) * 100),
                    Palette.TEXT_MUTED));
            column.getChildren().add(wouldTotal("The city's own payroll",
                    money(cityPayroll), money(cityPayroll * move),
                    move >= 1 ? Palette.BAD : Palette.GOOD));
            column.getChildren().add(statementNote(
                    "Once wages have walked there, which takes about a year. Nothing about "
                    + "this preview knows who leaves or who arrives because of it - a "
                    + "higher floor is a bill AND a reason for skilled people to come."));
            column.getChildren().add(applyBar("Set the floor to " + unitPrice(want),
                    () -> market.setMinimumWage(want)));
        }
    }

    /** The best-paid job somebody in this band can hold. */
    double bestWageIn(LabourMarket market, WageBand band) {
        double best = 0;
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) == band) best = Math.max(best, market.getWage(job));
        }
        return best;
    }

    /* =====================================================================
       MONEY - the policy rate

       THE DIAL IS THE PLAYER'S. The rule sits beside it saying what it would
       do and why, in a sentence, because a central bank is the most
       jargon-dense thing in this game and a number that moves without a reason
       on the screen is a number that punishes the player for not having read a
       textbook.

       Jerus picked this over an independent bank with a mandate: "the dial is
       yours, but the screen shows what a Taylor rule would do".
       ===================================================================== */

    void policyRatePage(VBox column) {

        DebtManager market = ui.game.getDebtManager();
        PriceIndex px = ui.game.getPriceIndex();
        Bank bank = ui.game.getBank();
        WorldEconomy world = ui.game.getWorldEconomy();

        double rate = market.getPolicyRate();
        double cityRate = market.getRate();
        /*
         * AGAINST WHAT THE CITY ACTUALLY PAYS, not against the dial (2026-09-12).
         *
         * This read `rate - WORLD_BASE_RATE` and sat two lines under "The city
         * itself borrows at 1.00%", so the page said the city paid a point over
         * the world's 2% while its own paper was quoted a point under it. The
         * carry trade reads the city's rate and the bank's deposit rate, not the
         * dial (see Game.nextMonth and CapitalFlows), so this is the difference
         * money actually follows.
         *
         * Worked out here rather than read off ForeignAccounts because that
         * field reads zero on a freshly loaded city until a month has ticked.
         */
        double over = cityRate - DebtManager.WORLD_BASE_RATE;
        double want = staged("policy", rate);
        double inflation = px.inflation();
        double advised = market.advisedPolicyRate(inflation);

        column.getChildren().add(statementHead("The price of money"));
        column.getChildren().add(leverHead(pct2(rate),
                "The one number under every other rate in the city. Raising it supports "
                + "the currency and makes every borrower pay more - that is not a side "
                + "effect, it is the same act."));
        column.getChildren().add(statementNote(String.format(
                "This is the DIAL, not what anybody is charged. The city's own paper is "
                + "priced from it: %.0f points under when the city owes nothing, and up to "
                + "%.0f points over when it owes too much against its output and its taxes. "
                + "The Finances tab takes that apart.",
                DebtManager.CITY_DISCOUNT * 100,
                (2 * DebtManager.maxSpreadPerMeasure() - DebtManager.CITY_DISCOUNT) * 100)));

        column.getChildren().add(statementLine("What the rule would set",
                pct2(advised), Math.abs(advised - rate) >= .01
                        ? Palette.WARN : Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("Inflation, year on year",
                px.hasRate() ? String.format("%+.1f%%", inflation * 100) : "not yet",
                px.hasRate() && Math.abs(inflation - DebtManager.INFLATION_TARGET) > .01
                        ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("...against a target of",
                String.format("%.0f%%", DebtManager.INFLATION_TARGET * 100),
                Palette.TEXT_MUTED));
        column.getChildren().add(statementNote(px.hasRate()
                ? market.adviceReason(inflation)
                : "There is not yet a year of prices to measure inflation against. The "
                + "rule has nothing to say until there is."));

        column.getChildren().add(statementHead("What it is costing"));
        column.getChildren().add(statementLine("The city itself borrows at",
                pct2(cityRate), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine(String.format(
                "...the dial less %.0f points, plus what it owes", DebtManager.CITY_DISCOUNT * 100),
                pct2(market.floorRate()) + " floor", Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("Savers are paid",
                pct2(bank.depositRate()), Palette.GOOD));
        column.getChildren().add(statementLine("The world's own rate",
                pct2(DebtManager.WORLD_BASE_RATE), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("...so the city pays over it",
                String.format("%+.2f pts", over * 100),
                over > 0 ? Palette.ACCENT : Palette.TEXT_MUTED));
        column.getChildren().add(statementNote(
                "Money follows that difference in, and leaves the day it closes. It is "
                + "also what the bank's premium is added on top of, so a city with a "
                + "strained bank pays this rate twice over."));

        /* ----------------------------- prices, in words ----------------------------- */
        Denomination unit = ui.game.getDenomination();
        column.getChildren().add(statementHead("Prices"));
        column.getChildren().add(statementLine("Since founding",
                String.format("%.3f", px.getIndex()), Palette.TEXT_HEAD));

        /* ------------------------- where it has been -------------------------
         *
         * Jerus, 2026-09-14: "something as well that stores the highest price
         * index and lowest".
         *
         * TODAY'S LEVEL IS ONE SAMPLE OF A PATH THAT MOVES. Measured across
         * sixteen four-thousand-month runs, cities that FINISH between 0.84 and
         * 1.26 times founding prices peak at a median of 1.62 and as high as
         * 3.78 getting there, and one swung +298% inside a decade - with
         * nothing on any screen, in any log, saying so. A player reading 1.02
         * has no way to tell a city that has never left 0.95-1.10 from one that
         * went to 3.78 and came back, and those are not the same place to live.
         *
         * Shown only once the two marks have parted: on a young city they are
         * both today's number and three identical lines say less than one.
         */
        if (px.swing() > 1.005) {
            column.getChildren().add(statementLine("Dearest it has been",
                    String.format("%.3f  (month %,d)", px.getPeak(), px.getPeakMonth()),
                    px.getPeak() >= 2 ? Palette.WARN : Palette.TEXT_MUTED));
            column.getChildren().add(statementLine("...and cheapest",
                    String.format("%.3f  (month %,d)", px.getTrough(), px.getTroughMonth()),
                    Palette.TEXT_MUTED));
            column.getChildren().add(statementLine("...so the level has swung",
                    String.format("%.2fx", px.swing()),
                    px.swing() >= 2 ? Palette.WARN : Palette.TEXT_MUTED));
            if (px.swing() >= 2) {
                column.getChildren().add(statementNote(
                        "Prices here have more than doubled and come back at some point. "
                        + "Wages, rents and every debt in the city were struck against "
                        + "those levels as they passed, and the people who lived through "
                        + "it did not get that back. Today's number does not show it."));
            }
        }

        if (world != null) {
            column.getChildren().add(statementLine("The world's, likewise",
                    String.format("%.3f", world.getPriceLevel()), Palette.TEXT_MUTED));
        }
        if (unit.getReforms() > 0) {
            column.getChildren().add(statementLine("The money is the", unit.name(),
                    Palette.ACCENT));
            column.getChildren().add(statementLine("...one of which is",
                    String.format("%,.0f founding", unit.getUnit()), Palette.TEXT_MUTED));
            column.getChildren().add(statementNote(
                    "The index is measured against the FOUNDING basket in founding money, "
                    + "and stays comparable across reforms precisely because a reform "
                    + "divides its base too."));
        }

        column.getChildren().add(stageSlider("policy", rate,
                DebtManager.MIN_POLICY_RATE, DebtManager.MAX_POLICY_RATE, .0005,
                Money::pct2));

        if (px.hasRate() && Math.abs(advised - rate) > 1e-9) {
            javafx.scene.layout.FlowPane rule = new javafx.scene.layout.FlowPane(6, 6);
            rule.setMaxWidth(STATEMENT);
            rule.getChildren().add(stepChip("do what the rule says",
                    () -> { stage("policy", advised); showPolicyMenu(); }, true));
            column.getChildren().add(rule);
        }

        if (isStaged("policy")) {
            double cityNow  = market.getRate();
            double cityThen = market.rateAtPolicy(want);
            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("The policy rate", pct2(rate), pct2(want),
                    Palette.ACCENT));
            column.getChildren().add(wouldBe("The city borrows at",
                    pct2(cityNow), pct2(cityThen),
                    cityThen >= cityNow ? Palette.BAD : Palette.GOOD));
            column.getChildren().add(wouldBe("Savers are paid",
                    pct2(bank.depositRate()),
                    pct2(Math.max(0, cityThen) * Bank.DEPOSIT_PASS_THROUGH),
                    cityThen >= cityNow ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(wouldTotal("Over the world's rate",
                    String.format("%+.2f pts", over * 100),
                    String.format("%+.2f pts",
                            (cityThen - DebtManager.WORLD_BASE_RATE) * 100),
                    want >= rate ? Palette.ACCENT : Palette.WARN));
            column.getChildren().add(statementNote(
                    "This does not reprice a single bond the city has already sold - every "
                    + "coupon on the book was struck on the day it was issued. It changes "
                    + "what the NEXT one costs, what every business and household is "
                    + "charged, and what savers are paid, all from next month."));
            column.getChildren().add(applyBar("Set the policy rate to " + pct2(want),
                    () -> market.setPolicyRate(want)));
        }
    }

    /* =====================================================================
       MONEY - the currency reform

       THE BUTTON EXISTS BEFORE IT WORKS, and that is deliberate. Jerus asked
       for it to "unlock past a threshold" and be the player's decision, which
       is also how it works in life: a currency reform is an act with a date on
       it, not something that happens to you. Below the threshold the page is
       shown with the reason on it rather than hidden - a control that appears
       from nowhere after two hundred years is a control nobody finds.
       ===================================================================== */

    /**
     * THE CURRENCY REFORM, which is a change of units and says so.
     *
     * Jerus: "perhaps even so often the player presses a button and everything
     * gets divided by 10, or 100, or whatever, so that bread doesnt show as
     * 300M and we start getting binary rounding issues all over the place."
     *
     * The screen's whole job is to make clear that nothing is being taken away.
     * A player who has just watched their treasury go from $84 billion to $840
     * million needs to see, in the same instant, that the loaf went from $3.00
     * to $0.03 and that they can still buy exactly as many loaves. So the
     * preview shows a before-and-after of four things they know the price of,
     * and the wording is about ZEROS rather than about value.
     */
    void currencyReformPage(VBox column) {

        Denomination unit = ui.game.getDenomination();
        ham.citybuildersim.sectors.Retail shops = ui.game.getSectors().retail();
        ham.citybuildersim.sectors.RealEstate landlords = ui.game.getSectors().realEstate();
        LabourMarket labour = ui.game.getLabourMarket();
        ForeignAccounts fx = ui.game.getForeignAccounts();

        column.getChildren().add(statementHead("Lopping the zeros off"));
        column.getChildren().add(sentence(
                "A reform issues a new " + Currency.NAME + " worth a round number of old "
                + "ones and restates every price, wage, balance and debt in the city at the "
                + "same moment. Nobody gains and nobody loses: the same wage buys the same "
                + "bread. It is what France did in 1960 and Turkey in 2005, and it is the "
                + "only thing in this game that is purely a change of units.",
                Palette.TEXT_BODY));

        column.getChildren().add(statementLine("Prices since founding",
                String.format("%.2fx", ui.game.getPriceIndex().getIndex()),
                Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("...and it unlocks at",
                String.format("%.0fx", Denomination.UNLOCK_AT), Palette.TEXT_MUTED));

        if (!ui.game.canReformCurrency()) {
            column.getChildren().add(alert("Not yet",
                    String.format("The numbers are not hard to read yet. At %.0f times "
                    + "founding prices this becomes available, and until then a reform "
                    + "would be a change of units nobody asked for.",
                    Denomination.UNLOCK_AT)));
            return;
        }

        double factor = staged("reform", 0);

        javafx.scene.layout.FlowPane pick = new javafx.scene.layout.FlowPane(6, 6);
        pick.setMaxWidth(STATEMENT);
        pick.setStyle("-fx-padding: 8 0 6 0;");
        for (double f : Denomination.FACTORS) {
            boolean can = unit.canLop(f);
            final double chosen = f;
            pick.getChildren().add(stepChip(String.format("%,.0f to 1", f),
                    () -> { if (can) { stage("reform", chosen); showPolicyMenu(); } },
                    !can || factor != f));
        }
        column.getChildren().add(pick);

        if (factor <= 0) {
            column.getChildren().add(statementNote(
                    "Pick how many of today's " + Currency.PLURAL + " one new one should "
                    + "be worth. Nothing happens until you do."));
            return;
        }

        column.getChildren().add(wouldHead());
        column.getChildren().add(wouldBe("A unit on the shelf",
                unitPrice(shops.getStoreSellPrice()),
                unitPrice(shops.getStoreSellPrice() / factor), Palette.TEXT_HEAD));
        column.getChildren().add(wouldBe("Rent, per person housed",
                unitPrice(landlords.getRentPrice()),
                unitPrice(landlords.getRentPrice() / factor), Palette.TEXT_HEAD));
        column.getChildren().add(wouldBe("The minimum wage",
                unitPrice(labour.cashMinimumWage()),
                unitPrice(labour.cashMinimumWage() / factor), Palette.TEXT_HEAD));
        column.getChildren().add(wouldBe("The city's cash",
                money(ui.game.getCash()), money(ui.game.getCash() / factor), Palette.TEXT_HEAD));
        // A RATE, NOT A SUM. It is local money per US dollar - a ratio between
        // two units, not an amount of either - so it must not be multiplied
        // into dollars like everything above it.
        column.getChildren().add(wouldBe("US$1 costs",
                String.format("%,.4f", fx.getRate()),
                String.format("%,.4f", fx.getRate() / factor), Palette.ACCENT));
        column.getChildren().add(wouldBe("One new one would be worth",
                "1 of today's", String.format("%,.0f of today's", factor),
                Palette.TEXT_HEAD));
        column.getChildren().add(wouldTotal("...and be called",
                unit.name(), afterName(unit, factor), Palette.ACCENT));

        column.getChildren().add(statementNote(
                "Everything foreign stays where it is. A debt owed in US dollars is still "
                + "owed in US dollars, and food bought abroad still costs abroad what it "
                + "always did - what changes is the number of " + Currency.PLURAL
                + " it takes to buy one."));

        column.getChildren().add(applyBar(
                "Issue the new " + Currency.NAME,
                () -> {
                    if (ui.game.reformCurrency(factor)) {
                        GameLog.note(String.format(
                                "Currency reform: one new %s for %,.0f old ones.",
                                Currency.NAME, factor));
                    }
                }));
    }

    /** What the money would be called after lopping by this factor. */
    static String afterName(Denomination unit, double factor) {
        Denomination next = new Denomination();
        next.restore(unit.toSaveArray());
        next.lop(factor);
        return next.name();
    }

    /* =====================================================================
       PROMISES - the pension

       THE TWO HALVES DELIBERATELY DO NOT BALANCE, and that is the screen.
       Contributions are a slice off every wage; the pension is a flat amount
       paid to every senior. The first does not cover the second and never did
       - the city carries the difference out of general revenue, which is how
       the real thing works.

       A city whose pyramid is greying has exactly two levers - charge the
       workers more, or pay the pensioners less - and every other consequence
       here falls out of those two: the coverage gap, a worker's take-home, and
       whether a pensioner living alone can afford to eat.
       ===================================================================== */

    void pensionPage(VBox column) {

        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        HouseholdAccounts hh = ui.game.getHouseholds();
        HouseholdBalance bal = ui.game.getHouseholdBalance();

        double contribution = policy.getContributionRate();
        double replacement  = policy.getPensionReplacement();
        double collected = em.getContributions();
        double paid = em.getPensionsPaid();
        double coverage = em.getPensionCoverage();

        column.getChildren().add(statementHead("The two dials"));
        column.getChildren().add(statementLine("Workers contribute",
                String.format("%.2f%% of every wage", contribution * 100), Palette.WARN));
        column.getChildren().add(statementLine("Seniors receive",
                moneyFull(policy.pensionPerSenior()) + " a month each", Palette.GOOD));
        column.getChildren().add(statementLine("...which replaces",
                String.format("%.0f%% of an unskilled wage", replacement * 100),
                Palette.TEXT_MUTED));

        column.getChildren().add(statementHead("What it costs the city"));
        column.getChildren().add(statementLine("Collected from workers",
                money(collected), Palette.GOOD));
        column.getChildren().add(statementLine("Paid to pensioners",
                signedTight(paid, true), Palette.WARN));
        column.getChildren().add(statementTotal(
                String.format("Covered, %.0f%%", coverage * 100),
                signedTight(em.getPensionShortfall(), true),
                coverage >= .9 ? Palette.GOOD : coverage >= .5 ? Palette.WARN : Palette.BAD));
        column.getChildren().add(statementNote(
                "The rest is general revenue - the same pot the schools and the hospitals "
                + "come out of, which is what makes an ageing city a budget problem before "
                + "it is anything else."));

        /* --------------------------- and what it does to people --------------------------- */
        int retired = HouseholdAccounts.RETIRED;
        if (hh.getRowHouseholds(retired) >= .5) {
            double homes = hh.getRowHouseholds(retired);
            column.getChildren().add(statementHead("And what it does to people"));
            column.getChildren().add(statementLine("A pensioner household gets",
                    moneyFull(hh.getRowPensions(retired) / homes), Palette.GOOD));
            /*
             * ONE HOUSEHOLD, NOT ONE CITY, so signed() must not be used here.
             *
             * All three signed helpers print "$0" below half a thousand, which
             * is right for a city ledger and wrong for a pension: this line read
             * "$0" against a household getting $883 and keeping $657, which is
             * a breakdown that does not foot in front of the player.
             */
            double fixed = (hh.getRowRent(retired) + hh.getRowHealthcare(retired)
                    + hh.getRowTuition(retired) + hh.getRowFares(retired)
                    + hh.getRowInterest(retired)) / homes;
            column.getChildren().add(statementLine("...its rent and bills come to",
                    fixed > 0 ? "\u2212" + moneyFull(fixed) : "nothing",
                    fixed > 0 ? Palette.WARN : Palette.TEXT_SPENT));
            column.getChildren().add(statementTotal("...leaving for the shop",
                    moneyFull(bal.getAfterFixed(retired)),
                    bal.getAfterFixed(retired) < bal.getSubsistence(retired)
                            ? Palette.BAD : Palette.GOOD));
            column.getChildren().add(statementLine("...against a basket costing",
                    moneyFull(bal.getSubsistence(retired)), Palette.TEXT_MUTED));
            column.getChildren().add(sentence(bal.isGoingShort(retired)
                    ? "They are eating less than they need, and that is in the sick rate."
                    : "They can afford to eat.",
                    bal.isGoingShort(retired) ? Palette.BAD_TEXT : Palette.GOOD));
        }

        /* ------------------------------ what workers pay ------------------------------ */
        column.getChildren().add(statementHead("What workers pay in"));
        column.getChildren().add(leverHead(String.format("%.2f%%", contribution * 100),
                "Off every wage in the city, before the worker sees it."));
        column.getChildren().add(stageSlider("contrib", contribution,
                0, TaxPolicy.MAX_CONTRIBUTION, .0025, Money::pct2));

        if (isStaged("contrib")) {
            double want = staged("contrib", contribution);
            double then = contribution > 0 ? collected * want / contribution
                                           : 0;
            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("Workers contribute",
                    String.format("%.2f%%", contribution * 100),
                    String.format("%.2f%%", want * 100),
                    want >= contribution ? Palette.WARN : Palette.GOOD));
            column.getChildren().add(wouldBe("Collected a month",
                    money(collected), money(then), Palette.GOOD));
            column.getChildren().add(wouldBe("Covered",
                    String.format("%.0f%%", coverage * 100),
                    String.format("%.0f%%", paid > 0 ? then / paid * 100 : 100),
                    Palette.TEXT_HEAD));
            column.getChildren().add(wouldTotal("Out of the treasury",
                    signedTight(paid - collected, true), signedTight(paid - then, true),
                    then >= collected ? Palette.GOOD : Palette.BAD));
            column.getChildren().add(previewCaveat(
                    "Against last month's payroll, which is the base it is charged on."));
            column.getChildren().add(applyBar(
                    String.format("Set contributions to %.2f%%", want * 100),
                    () -> policy.setContributionRate(want)));
        }

        /* ---------------------------- what seniors receive ---------------------------- */
        column.getChildren().add(statementHead("What seniors receive"));
        column.getChildren().add(leverHead(String.format("%.0f%%", replacement * 100),
                "Of an unskilled wage, paid flat to every senior in the city."));
        column.getChildren().add(stageSlider("pension", replacement,
                0, TaxPolicy.MAX_REPLACEMENT, .01,
                r -> String.format("%.0f%%", r * 100)));

        if (isStaged("pension")) {
            double want = staged("pension", replacement);
            double then = replacement > 0 ? paid * want / replacement : 0;
            double perSenior = replacement > 0
                    ? policy.pensionPerSenior() * want / replacement : 0;
            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("...which replaces",
                    String.format("%.0f%%", replacement * 100),
                    String.format("%.0f%%", want * 100),
                    want >= replacement ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(wouldBe("Each senior gets",
                    moneyFull(policy.pensionPerSenior()), moneyFull(perSenior),
                    want >= replacement ? Palette.GOOD : Palette.BAD));
            column.getChildren().add(wouldBe("Paid altogether",
                    money(paid), money(then), Palette.WARN));
            column.getChildren().add(wouldTotal("Out of the treasury",
                    signedTight(paid - collected, true), signedTight(then - collected, true),
                    then <= paid ? Palette.GOOD : Palette.BAD));
            column.getChildren().add(previewCaveat(
                    "Against today's number of seniors."));
            column.getChildren().add(applyBar(
                    String.format("Set the pension to %.0f%%", want * 100),
                    () -> policy.setPensionReplacement(want)));
        }

        column.getChildren().add(sentence(
                "Raising the contribution closes the gap out of workers' pay, and a worker "
                + "short of money stops at the shop rather than at the landlord - so it "
                + "arrives as hunger somewhere else. Cutting the pension closes it out of "
                + "the seniors, who have no other income at all. There is no setting where "
                + "nobody pays; the screen is which of them does.", Palette.TEXT_MUTED));
    }

    /* =====================================================================
       PROMISES - the schools: the price of a place, who pays it, and the
       loan that covers the rest (2026-09-21; the tuition page until then)

       THE SUBSIDY DOES TWO OPPOSITE THINGS AND THAT IS THE DECISION. Every
       point of subsidy is money out of the treasury and a few more people
       who can afford to enrol - and the second effect is far larger than the
       first, because tuition is small against what a school costs to run. So
       this is not "how much do we spend on education", it is "who is allowed
       to go", and the budget line is the smaller half of the answer.

       AND SINCE 2026-09-21 THE PRICE IS A DIAL TOO, WITH THE GRANT AND THE
       LOAN BESIDE IT. Jerus: "grants its just a menu where you can choose
       between a fixed amount, or a percentage of last month's surplus, or a
       % as it is now of living costs, or a % of tuition. and then another
       slider which is the interest rate for the student loans ... and also
       make it so that you can tweak the price of tuition as well." Five
       dials on one page, because they are one decision: what a seat costs,
       what share of that the city forgives, what a student is handed to
       live on, and what the loan for the rest costs them afterwards. All
       five stage into the foot bar and one button applies them; every
       figure here is a public getter, and the previews are the model's own
       rule (Game.studentGrantBillUnder, HouseholdBalance.studentInterestAt)
       asked about a setting the city has not made.

       At no subsidy a university place costs a diploma-holder most of a
       month's pay and almost nobody attends: a city can own the buildings,
       need the graduates, and produce none of them. At the founding price
       against today's wages that trap is mostly quiet; around three times
       it, it is back.
       ===================================================================== */

    /** The grant in words, for a line that names it: "15% of an unskilled wage a month". */
    static String grantWords(TaxPolicy.GrantBasis basis, double amount) {
        if (basis == null) basis = TaxPolicy.DEFAULT_GRANT_BASIS;
        return switch (basis) {
            case FIXED         -> moneyFull(amount) + " a month";
            case SURPLUS_SHARE -> String.format("%.0f%% of last month's surplus, shared out", amount * 100);
            case TUITION_SHARE -> String.format("%.0f%% of their course's tuition", amount * 100);
            default            -> String.format("%.0f%% of an unskilled wage a month", amount * 100);
        };
    }

    /** What a basis is called on its chip. */
    static String basisName(TaxPolicy.GrantBasis basis) {
        return switch (basis) {
            case FIXED         -> "a fixed amount";
            case SURPLUS_SHARE -> "a share of last month's surplus";
            case TUITION_SHARE -> "a share of tuition";
            default            -> "a share of the unskilled wage";
        };
    }

    /** How a basis's amount reads on its dial: dollars, or a percentage of the thing it is a share of. */
    static String amountWords(TaxPolicy.GrantBasis basis, double amount) {
        return basis == TaxPolicy.GrantBasis.FIXED ? moneyFull(amount)
                : String.format("%.0f%%", amount * 100);
    }

    void schoolsPage(VBox column) {

        Education schools = ui.game.getEducation();
        LabourMarket market = ui.game.getLabourMarket();
        TaxPolicy policy = ui.game.getEconomyManager().getTaxPolicy();
        EconomyManager em = ui.game.getEconomyManager();
        HouseholdBalance bal = ui.game.getHouseholdBalance();
        FamilyModel families = ui.game.getFamilies();

        double share = schools.getTuitionSubsidy();
        double want = staged("tuition", share);
        double scale = policy.getTuitionScale();
        double wantScale = staged("tuitionScale", scale);
        boolean moved = isStaged("tuition") || isStaged("tuitionScale");

        /* -------------------- the five levers, registered for the one bar -------------------- */
        register(new Lever("tuition", "The city's share of tuition", share, 0, 1, .01,
                r -> String.format("%.0f%%", r * 100), schools::setTuitionSubsidy));
        register(new Lever("tuitionScale", "The price of a place", scale,
                0, TaxPolicy.MAX_TUITION_SCALE, .05, v -> String.format("x%.2f", v),
                policy::setTuitionScale));
        register(new Lever("loanRate", "Interest on student loans", policy.getStudentLoanRate(),
                0, TaxPolicy.MAX_STUDENT_LOAN_RATE, .0025, Money::pct2, policy::setStudentLoanRate));

        column.getChildren().add(statementHead("Who pays for school"));
        column.getChildren().add(leverHead(String.format("%.0f%%", share * 100),
                "The city's share of every course fee. Households pay the rest out of a "
                + "month's wages, and nobody enrols in a course that costs more than they "
                + "can carry - so this number decides attendance far more than it decides "
                + "the budget."));

        /* ------------------------ what a family has to find ------------------------ */
        javafx.scene.layout.GridPane burden = grid(
                new double[] {150, 82, 82, 76, 82, 76},
                rightAfterFirst(6));
        gridHead(burden, "", "tuition", "they pay", "of a wage",
                moved ? "then pay" : "", moved ? "after" : "");

        int line = 1;
        for (EducationType type : EducationType.values()) {
            if (type == EducationType.NONE) continue;

            double fee = schools.feeFor(type);
            double pocket = fee * (1 - share);
            WageBand from = type.requires();
            double wage = from == null ? market.getWage(JobType.NO_DIPLOMA)
                                       : bestWageIn(market, from);
            double now = wage > 0 ? pocket / wage : 0;
            double feeThen = schools.feeAtOne(type) * wantScale;
            double pocketThen = feeThen * (1 - want);
            double then = wage > 0 ? pocketThen / wage : 0;

            burden.add(gridCell(type.getLabel(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            burden.add(gridCell(unitPrice(fee), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 1, line);
            burden.add(gridCell(unitPrice(pocket), Palette.TEXT_HEAD,
                    Palette.SIZE_CAPTION, true), 2, line);
            burden.add(gridCell(String.format("%.0f%%", now * 100), burdenTone(now),
                    Palette.SIZE_CAPTION, true), 3, line);
            if (moved) {
                burden.add(gridCell(unitPrice(pocketThen), Palette.ACCENT,
                        Palette.SIZE_CAPTION, true), 4, line);
                burden.add(gridCell(String.format("%.0f%%", then * 100), burdenTone(then),
                        Palette.SIZE_CAPTION, true), 5, line);
            }
            line++;
        }
        column.getChildren().add(burden);
        column.getChildren().add(statementNote(String.format(
                "At %.0f%% of a month's wage nobody enrols at all. The column that matters "
                + "is the last one with a figure in it, and red in it means a course the "
                + "city offers and nobody can take.", Education.MAX_BURDEN * 100)));

        /* ---------------------------- the schools this month ---------------------------- */
        column.getChildren().add(statementHead("The schools this month"));
        column.getChildren().add(statementLine("Staff and buildings",
                signedTight(schools.getPayroll() + schools.getUpkeep(), true),
                Palette.WARN));
        column.getChildren().add(statementLine("Tuition the city covers",
                signedTight(schools.getSubsidy(), true), Palette.WARN));
        column.getChildren().add(statementLine("Tuition households pay",
                signedTight(schools.getFees(), false), Palette.GOOD));
        column.getChildren().add(statementTotal("Net cost to the city",
                signedTight(schools.getNetCost(), true), Palette.WARN));

        column.getChildren().add(stageSlider("tuition", share, 0, 1, .01,
                r -> String.format("%.0f%%", r * 100)));

        /* ---------------------------- the price of a place ---------------------------- */
        column.getChildren().add(statementHead("What a place is priced at"));
        column.getChildren().add(leverHead(String.format("x%.2f", scale),
                "The founding tuition table times this, on every course, before the "
                + "city's share comes off. The table was struck against the wages of "
                + "its day; against today's the trap in the note above is quiet at x1 "
                + "and back around x3. Nothing at 0: a free place, and the city forgoes "
                + "nothing because there is nothing to forgo."));
        column.getChildren().add(stageSlider("tuitionScale", scale,
                0, TaxPolicy.MAX_TUITION_SCALE, .05, v -> String.format("x%.2f", v)));

        if (moved) {
            double billedAtOne = scale > 0 ? (schools.getSubsidy() + schools.getFees()) / scale : 0;
            double billedThen = billedAtOne * wantScale;
            double subsidyThen = billedThen * want;
            double feesThen = billedThen * (1 - want);
            double netThen = schools.getPayroll() + schools.getUpkeep() - feesThen;

            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("The city's share",
                    String.format("%.0f%%", share * 100),
                    String.format("%.0f%%", want * 100),
                    want >= share ? Palette.WARN : Palette.GOOD));
            column.getChildren().add(wouldBe("The price of a place",
                    String.format("x%.2f", scale), String.format("x%.2f", wantScale),
                    wantScale <= scale ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(wouldBe("Tuition the city covers",
                    money(schools.getSubsidy()), money(subsidyThen), Palette.WARN));
            column.getChildren().add(wouldBe("Tuition households pay",
                    money(schools.getFees()), money(feesThen), Palette.GOOD));
            column.getChildren().add(wouldTotal("Net cost to the city",
                    money(schools.getNetCost()), money(netThen),
                    netThen <= schools.getNetCost() ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(statementNote(
                    "Against the courses being taken now" + (scale <= 0
                            ? ", and with the place free this month there is no bill to "
                              + "re-strike from: the figures above are what a price would "
                              + "collect once the students are billed."
                            : " - and that is the half this preview cannot do, because "
                              + "the point of both dials is to change who enrols. A cheaper "
                              + "place fills a course, and the bill arrives with the students.")));
        }

        /* ------------------------------- the grant ------------------------------- */
        TaxPolicy.GrantBasis basis = policy.getGrantBasis();
        double amount = policy.getGrantAmount();
        double students = families.getSeekers(FamilyModel.Seeker.STUDENT);
        double grants = em.getStudentGrants();

        column.getChildren().add(statementHead("What a student is granted"));
        column.getChildren().add(leverHead(grantWords(basis, amount),
                "To every full-time student, to live on"
                + (students > 0 ? String.format(" - %s each this month, %s to %s of them.",
                        moneyFull(ui.game.grantPerStudentUnder(basis, amount)),
                        money(grants), people(students))
                        : "; nobody is studying this month.")
                + " What it does not cover, a student loan does. The unskilled wage it "
                + "can be a share of is " + moneyFull(ui.game.getUnskilledWage())
                + " a month; last month's surplus was " + signedTight(ui.game.getTreasurySurplus(), false)
                + "."));

        // The basis: one chip each, the chosen one lit. Picking one stages
        // it AND stages today's grant re-expressed in its unit, so the
        // switch alone changes nothing until the amount is moved.
        int stagedOrdinal = (int) Math.round(staged("grantBasis", basis.ordinal()));
        TaxPolicy.GrantBasis wantBasis = TaxPolicy.GrantBasis.values()[
                Math.max(0, Math.min(TaxPolicy.GrantBasis.values().length - 1, stagedOrdinal))];
        register(new Lever("grantBasis", "The grant is", basis.ordinal(),
                0, TaxPolicy.GrantBasis.values().length - 1, 1,
                v -> basisName(TaxPolicy.GrantBasis.values()[(int) Math.round(v)]),
                v -> policy.setGrantBasis(TaxPolicy.GrantBasis.values()[(int) Math.round(v)])));

        javafx.scene.layout.FlowPane pick = new javafx.scene.layout.FlowPane(6, 6);
        pick.setMaxWidth(STATEMENT);
        pick.setStyle("-fx-padding: 8 0 6 0;");
        for (TaxPolicy.GrantBasis b : TaxPolicy.GrantBasis.values()) {
            pick.getChildren().add(stepChip(basisName(b), () -> {
                unstage("grantBasis");
                unstage("grantAmount");
                if (b != basis) {
                    stage("grantBasis", b.ordinal());
                    stage("grantAmount", ui.game.grantAmountAs(b));
                }
                showPolicyMenu();
            }, b != wantBasis));
        }
        column.getChildren().add(pick);

        // The amount, in the staged basis's unit: the same dial re-ranged.
        // Its "current" is what today's grant IS in that unit, so a thumb put
        // back is a grant unchanged.
        double amountNow = wantBasis == basis ? amount : ui.game.grantAmountAs(wantBasis);
        double amountMax = policy.maxGrantAmount(wantBasis);
        double amountStep = wantBasis == TaxPolicy.GrantBasis.FIXED
                ? Math.max(1e-9, amountMax / 100) : .01;
        // Applied WITH the basis it was read in, whichever order the bar
        // reaches the two: an amount set under one basis and clamped by
        // another's ceiling would be a different number than the one staged.
        register(new Lever("grantAmount", "...at", amountNow, 0, amountMax, amountStep,
                v -> amountWords(wantBasis, v), v -> policy.setGrant(wantBasis, v)));
        column.getChildren().add(stageSlider("grantAmount", amountNow, 0, amountMax, amountStep,
                v -> amountWords(wantBasis, v)));

        if (isStaged("grantBasis") || isStaged("grantAmount")) {
            double wantAmount = staged("grantAmount", amountNow);
            double billThen = ui.game.studentGrantBillUnder(wantBasis, wantAmount);
            double eachThen = ui.game.grantPerStudentUnder(wantBasis, wantAmount);
            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("A student is granted",
                    grantWords(basis, amount), grantWords(wantBasis, wantAmount), Palette.ACCENT));
            column.getChildren().add(wouldBe("Each, this month",
                    moneyFull(ui.game.grantPerStudentUnder(basis, amount)), moneyFull(eachThen),
                    eachThen >= ui.game.grantPerStudentUnder(basis, amount) ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(wouldTotal("Paid a month",
                    money(grants), money(billThen), billThen <= grants ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(previewCaveat(
                    "Against today's students, wage, surplus and courses. A smaller grant "
                    + "is a bigger student loan, repaid out of wages for nine and a half "
                    + "years" + (wantBasis == TaxPolicy.GrantBasis.SURPLUS_SHARE
                            ? "; and a share of the surplus is nothing in a deficit month, "
                              + "whoever is studying." : ".")));
        }

        /* ------------------------------- the loan ------------------------------- */
        double rate = policy.getStudentLoanRate();
        double owed = bal.totalStudentDebt();
        double owedByGraduates = bal.totalGraduateDebt();
        double interestNow = em.getStudentLoanInterest();
        double principalNow = ui.game.getStudentLoansRepaid();

        column.getChildren().add(statementHead("What the loan costs them afterwards"));
        column.getChildren().add(leverHead(String.format("%.2f%% a year", rate * 100),
                "Charged on a graduate's balance while they repay it, and on nothing while "
                + "they study - the treasury carries the interest until they finish. It "
                + "arrives as revenue; the loan itself is not on the budget, it is lent and "
                + "paid back. A prisoner's loan is frozen with the rest of their debts."));
        column.getChildren().add(statementLine("Owed on student loans today", money(owed), Palette.WARN));
        column.getChildren().add(statementLine("...of it by graduates, repaying",
                money(owedByGraduates), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("Repaid last month", money(principalNow), Palette.GOOD));
        column.getChildren().add(statementTotal("Interest received last month",
                money(interestNow), interestNow > 0 ? Palette.GOOD : Palette.TEXT_SPENT));
        column.getChildren().add(stageSlider("loanRate", rate,
                0, TaxPolicy.MAX_STUDENT_LOAN_RATE, .0025, Money::pct2));
        if (isStaged("loanRate")) {
            double wantRate = staged("loanRate", rate);
            double interestThen = bal.studentInterestAt(wantRate);
            double interestAtNow = bal.studentInterestAt(rate);
            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("Interest a year",
                    String.format("%.2f%%", rate * 100), String.format("%.2f%%", wantRate * 100),
                    wantRate >= rate ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(wouldBe("A month's interest, on today's balances",
                    money(interestAtNow), money(interestThen), Palette.GOOD));
            column.getChildren().add(wouldTotal("The graduates pay a month",
                    money(principalNow + interestAtNow), money(principalNow + interestThen),
                    wantRate <= rate ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(previewCaveat(
                    "Against the balances the graduates owe today. The instalment itself does "
                    + "not change - a 114th of the balance a month - so a rate is money on top "
                    + "of it, out of the same wages the shops are waiting for."));
        }
    }

    /** A dial on this page, registered so the foot bar can name and apply it. */
    Lever register(Lever lever) {
        policyLevers.put(lever.key(), lever);
        return lever;
    }

    /* =====================================================================
       PROMISES - the out of work and the students (2026-09-11)

       TWO DIALS, AND BOTH ARE THE PENSION'S SHAPE AGAIN. Jerus: EI "like
       CPP" - a premium off every wage, a benefit to whoever lost a job, and
       the treasury carrying the gap. Defaults are the real 2026 figures:
       1.63% and 55%. The students' grant was the third dial here until
       2026-09-21, because it is the same kind of promise; it is on the
       Schools page now, with the tuition price and the loan's rate it is
       decided against, and only its cost stays on this page's books.
       ===================================================================== */

    void outOfWorkPage(VBox column) {
        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        Unemployment u = ui.game.getUnemployment();
        FamilyModel families = ui.game.getFamilies();

        double premium = policy.getEiPremiumRate();
        double benefit = policy.getEiBenefitRate();
        double collected = em.getEiPremiums();
        double paid = em.getEiBenefits();
        double grants = em.getStudentGrants();
        double students = families.getSeekers(FamilyModel.Seeker.STUDENT);

        column.getChildren().add(statementHead("The two dials"));
        column.getChildren().add(statementLine("Workers pay for EI",
                String.format("%.2f%% of every wage", premium * 100), Palette.WARN));
        column.getChildren().add(statementLine("EI replaces",
                String.format("%.0f%% of the wage they lost, for twelve months", benefit * 100),
                Palette.GOOD));
        column.getChildren().add(statementLine("A student is granted",
                grantWords(policy.getGrantBasis(), policy.getGrantAmount()) + " - set on the Schools page",
                Palette.TEXT_MUTED));

        column.getChildren().add(statementHead("What it costs the city"));
        column.getChildren().add(statementLine("Premiums collected", money(collected), Palette.GOOD));
        column.getChildren().add(statementLine("EI paid to " + people(u.onEi()) + " claimants",
                signedTight(paid, true), Palette.WARN));
        column.getChildren().add(statementLine("Grants paid to " + people(students) + " students",
                signedTight(grants, true), Palette.WARN));
        column.getChildren().add(statementTotal("Out of the treasury",
                signedTight(paid + grants - collected, true),
                collected >= paid + grants ? Palette.GOOD : Palette.WARN));
        column.getChildren().add(statementNote(
                "EI only pays the first twelve months, so a long bust costs less in EI than "
                + "a short one and more in everything else. Student loans are not on this "
                + "line: they are lent, and graduates pay them back - with interest, if the "
                + "Schools page charges any."));

        /* ---- the premium ---- */
        column.getChildren().add(statementHead("What workers pay for EI"));
        column.getChildren().add(leverHead(String.format("%.2f%%", premium * 100),
                "Off every wage in the city, beside the pension contribution."));
        column.getChildren().add(stageSlider("eiPremium", premium,
                0, TaxPolicy.MAX_EI_PREMIUM, .0005, Money::pct2));
        if (isStaged("eiPremium")) {
            double want = staged("eiPremium", premium);
            double then = premium > 0 ? collected * want / premium : 0;
            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("Workers pay",
                    String.format("%.2f%%", premium * 100), String.format("%.2f%%", want * 100),
                    want >= premium ? Palette.WARN : Palette.GOOD));
            column.getChildren().add(wouldBe("Collected a month", money(collected), money(then), Palette.GOOD));
            column.getChildren().add(wouldTotal("Out of the treasury",
                    signedTight(paid + grants - collected, true), signedTight(paid + grants - then, true),
                    then >= collected ? Palette.GOOD : Palette.BAD));
            column.getChildren().add(previewCaveat("Against last month's payroll."));
            column.getChildren().add(applyBar(String.format("Set the EI premium to %.2f%%", want * 100),
                    () -> policy.setEiPremiumRate(want)));
        }

        /* ---- the benefit ---- */
        column.getChildren().add(statementHead("What EI replaces"));
        column.getChildren().add(leverHead(String.format("%.0f%%", benefit * 100),
                "Of the wage a claimant lost, up to the insured maximum of "
                + moneyFull(u.getInsuredCap()) + " a month."));
        column.getChildren().add(stageSlider("eiBenefit", benefit,
                0, TaxPolicy.MAX_EI_BENEFIT, .01, r -> String.format("%.0f%%", r * 100)));
        if (isStaged("eiBenefit")) {
            double want = staged("eiBenefit", benefit);
            double then = benefit > 0 ? paid * want / benefit : 0;
            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("EI replaces",
                    String.format("%.0f%%", benefit * 100), String.format("%.0f%%", want * 100),
                    want >= benefit ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(wouldBe("Paid a month", money(paid), money(then), Palette.WARN));
            column.getChildren().add(wouldTotal("Out of the treasury",
                    signedTight(paid + grants - collected, true), signedTight(then + grants - collected, true),
                    then <= paid ? Palette.GOOD : Palette.BAD));
            column.getChildren().add(previewCaveat("Against today's claimants."));
            column.getChildren().add(applyBar(String.format("Set EI to %.0f%%", want * 100),
                    () -> policy.setEiBenefitRate(want)));
        }
    }

    static String burdenTone(double share) {
        return share >= Education.MAX_BURDEN ? Palette.BAD
                : share >= Education.MAX_BURDEN * .6 ? Palette.WARN : Palette.GOOD;
    }

    /* =====================================================================
       PROMISES - the clinic's price, and a premium (2026-09-19)

       Jerus: "healthcare should be an adjustable price, all the way to even
       make it a profitable business or the option to make it an obligatory
       insurance payment system." Two dials, in the EI premium's shape: a
       scale on the three care fees, and a premium off every wage. Between
       them the page has three corners, and the kicker says which one the
       player is standing in: free at the point of use and paid from taxes
       (fees 0, premium 0), a business (fees past the break-even), insurance
       (fees 0, a premium).

       AND THE PRICE HAS A COST, which is what makes the fee dial a decision
       rather than a revenue line: a household that cannot pay the fee after
       its savings, its shares and its credit goes without care rather than
       without food, and the page says how many did this month and what it
       did to coverage. See Household.affordCare() and Healthcare.
       ===================================================================== */

    void healthPage(VBox column) {
        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        Healthcare care = ui.game.getHealthcare();

        double scale = policy.getHealthFeeScale();
        double premium = policy.getHealthPremiumRate();
        double gross = care.getGrossCost();
        double fees = care.getTreatmentFees();
        double funerals = care.getFuneralFees();
        double raised = em.getHealthPremiums();
        double treasury = gross - fees - funerals - raised;
        double breakEven = care.breakEvenScale();
        double wages = ui.game.getPopulationManager().getTotalWage();

        /* ---- which corner ---- */
        String corner;
        if (scale <= 0 && premium <= 0) {
            corner = "Free at the point of use, paid from taxes: nobody pays at the door and "
                    + "nobody pays a premium, so the whole cost of the service is the treasury's.";
        } else if (scale <= 0) {
            corner = "Insurance: care is free at the door and every wage pays a premium for it. "
                    + "What the premium does not cover is the treasury's, and what it raises "
                    + "past the cost is the treasury's too.";
        } else if (breakEven > 0 && scale >= breakEven) {
            corner = "A business: at this scale the fees on the people treated meet the "
                    + "service's whole cost, and past it the clinics turn a profit - and turn "
                    + "away whoever cannot pay.";
        } else if (premium > 0) {
            corner = "A mix: patients pay something at the door, every wage pays a premium, "
                    + "and the treasury carries the rest.";
        } else {
            corner = "Fee-funded, in part: patients pay at the door and the treasury carries "
                    + "the rest. A household that cannot pay after its savings, its shares and "
                    + "its credit goes without care rather than without food.";
        }
        column.getChildren().add(statementHead("Where the money comes from"));
        column.getChildren().add(statementNote(corner));

        /* ---- the service this month ---- */
        column.getChildren().add(statementHead("The service this month"));
        column.getChildren().add(statementLine("Staff and buildings", signedTight(gross, true), Palette.WARN));
        column.getChildren().add(statementLine(String.format("Fees, at x%.2f the founding fee", scale),
                signedTight(fees, false), Palette.GOOD));
        column.getChildren().add(statementLine("Funeral fees, unscaled", signedTight(funerals, false), Palette.GOOD));
        column.getChildren().add(statementLine(String.format("Premium at %.2f%% of every wage", premium * 100),
                signedTight(raised, false), Palette.GOOD));
        column.getChildren().add(statementTotal("The treasury's share",
                signedTight(treasury, true), treasury > 0 ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementNote(String.format(
                "Fees cover %.0f%% of the cost%s. A ward is paid for whether or not its patients "
                + "are, so the cost does not move with the price; only who pays it does.",
                care.getCostRecovery() * 100,
                raised > 0 ? String.format(" and the premium another %.0f%%", gross > 0 ? raised / gross * 100 : 0) : "")));

        /* ---- who was priced out ---- */
        column.getChildren().add(statementHead("Who was turned away at the door"));
        double pricedOut = care.getPricedOutTotal();
        for (CareType kind : new CareType[] {CareType.GENERAL, CareType.CHILDCARE, CareType.SENIOR}) {
            double out = care.getPricedOut(kind);
            double could = care.getAffordability(kind);
            column.getChildren().add(statementLine(kind.getLabel(),
                    out > 0 ? String.format("%s priced out - coverage cut to %.0f%% of what the beds could do",
                            people(out), could * 100) : "nobody priced out",
                    out > 0 ? Palette.BAD : Palette.GOOD));
        }
        column.getChildren().add(statementTotal("Priced out of care this month", people(pricedOut),
                pricedOut > 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(statementNote(pricedOut > 0
                ? String.format("They skipped %s of care bills and ate with it. A household pays for "
                        + "care out of what it has after rent, its other bills and a basket for "
                        + "everybody in it - its income, its savings, its paper abroad and the "
                        + "credit still open to it - and what would come out of the food budget "
                        + "is not paid. They are unserved the way people with no clinic are: in "
                        + "the sick rate, in the deaths, and in the births.",
                        money(ui.game.getHouseholdBalance().getCareSkipped()))
                : "Every household that a clinic had room for could pay its fee this month. The "
                        + "rule is a cliff on a household's own means, not a slope on the fee: a "
                        + "household that can still cover its bills out of income, savings, "
                        + "shares or credit pays, and only one that would otherwise eat less skips "
                        + "the clinic first."));

        /* ---- the fee dial ---- */
        column.getChildren().add(statementHead("What a patient pays"));
        column.getChildren().add(leverHead(String.format("x%.2f", scale),
                "The founding fee times this, on general care, childcare and senior care. "
                + (breakEven > 0
                        ? String.format("This city's fees would meet its whole cost at x%.2f%s. ", breakEven,
                                breakEven > TaxPolicy.MAX_HEALTH_FEE_SCALE ? ", beyond the dial's reach" : "")
                        : "")
                + "Funeral fees are not scaled: the cemetery against the crematorium is its own design."));
        // A tenth of the founding fee a step: 150 steps from 0 to 15, few
        // enough for the keys to walk, fine enough to land on a break-even
        // quoted to a tenth. A twentieth was 300 steps and no more precise
        // than the preview beside it.
        column.getChildren().add(stageSlider("healthFee", scale,
                0, TaxPolicy.MAX_HEALTH_FEE_SCALE, .1, v -> String.format("x%.2f", v)));
        if (isStaged("healthFee")) {
            double want = staged("healthFee", scale);
            double feesThen = care.treatmentFeesAtOne() * want;
            double treasuryThen = gross - feesThen - funerals - raised;
            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("A patient pays", String.format("x%.2f", scale),
                    String.format("x%.2f", want), want >= scale ? Palette.WARN : Palette.GOOD));
            for (CareType kind : new CareType[] {CareType.GENERAL, CareType.CHILDCARE, CareType.SENIOR}) {
                double now = care.feeNow(kind);
                double then = care.feeAtOne(kind) * want;
                column.getChildren().add(wouldBe("  " + kind.getLabel().toLowerCase() + ", a head a month",
                        moneyFull(now), moneyFull(then), Palette.TEXT_HEAD));
            }
            column.getChildren().add(wouldBe("Fees a month, if everybody paid",
                    money(care.fullTreatmentFees()), money(feesThen), Palette.GOOD));
            column.getChildren().add(wouldTotal("The treasury's share",
                    signedTight(treasury, true), signedTight(treasuryThen, true),
                    treasuryThen <= treasury ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(previewCaveat(
                    (breakEven > 0 && want >= breakEven
                            ? "Past this city's break-even: a business. "
                            : want <= 0 ? "Free at the point of use. " : "")
                    + "Against this month's patients, and that is the half this preview cannot "
                    + "do: a dearer fee turns some of them away, and they leave the bill and the "
                    + "clinic together."));
            column.getChildren().add(applyBar(String.format("Set the fee to x%.2f", want),
                    () -> policy.setHealthFeeScale(want)));
        }

        /* ---- the premium dial ---- */
        column.getChildren().add(statementHead("What every wage pays"));
        column.getChildren().add(leverHead(String.format("%.2f%%", premium * 100),
                "Off every wage in the city, employee side, beside the EI premium - into the "
                + "treasury as revenue against the service's cost. No employer share, and "
                + "nothing balances it: a shortfall is the treasury's and so is a surplus."));
        column.getChildren().add(stageSlider("healthPremium", premium,
                0, TaxPolicy.MAX_HEALTH_PREMIUM, .0005, Money::pct2));
        if (isStaged("healthPremium")) {
            double want = staged("healthPremium", premium);
            double then = wages * want;
            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("Workers pay",
                    String.format("%.2f%%", premium * 100), String.format("%.2f%%", want * 100),
                    want >= premium ? Palette.WARN : Palette.GOOD));
            column.getChildren().add(wouldBe("Raised a month", money(raised), money(then), Palette.GOOD));
            column.getChildren().add(wouldTotal("The treasury's share",
                    signedTight(treasury, true), signedTight(gross - fees - funerals - then, true),
                    then >= raised ? Palette.GOOD : Palette.BAD));
            column.getChildren().add(previewCaveat(gross > 0
                    ? String.format("Against this month's payroll. The whole cost is %.2f%% of it.",
                            gross / Math.max(1e-9, wages) * 100)
                    : "Against this month's payroll."));
            column.getChildren().add(applyBar(String.format("Set the health premium to %.2f%%", want * 100),
                    () -> policy.setHealthPremiumRate(want)));
        }
    }

    /* =====================================================================
       PROMISES - the standing subsidies

       A TOGGLE, NOT A DIAL, so it has no preview: the cost is whatever the
       sector loses, and nobody - including the model - knows that until the
       month is over. What the screen can say is what it cost last month and
       what it is buying, which is the part a player is deciding on.
       ===================================================================== */

    void subsidyPage(VBox column) {

        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();

        column.getChildren().add(statementHead("What the city will not let fail"));
        column.getChildren().add(sentence(
                "A protected sector is topped up to break-even every month it loses money, "
                + "so it never sells its capacity to pay a bill. The city pays whatever it "
                + "takes - and goes overdrawn if it must, which costs interest.",
                Palette.TEXT_BODY));

        for (Sector sector : ui.game.getSectors().all()) {

            boolean on = ui.game.isAutoSubsidised(sector);
            double paid = ui.game.getSubsidyPaid(sector);
            boolean blocked = credit.isBorrowingBlocked(sector.key());

            Label name = new Label(sector.label());
            name.setStyle(Palette.words(Palette.SIZE_BODY,
                    on ? Palette.GOOD : Palette.TEXT_BODY));

            Label says = new Label(on
                    ? (paid > 0 ? money(paid) + " paid last month"
                                : "protected, and it did not need it last month")
                    : blocked ? "not protected - and it cannot borrow either"
                              : "not protected");
            says.setStyle(Palette.words(Palette.SIZE_CAPTION,
                    blocked && !on ? Palette.BAD : Palette.TEXT_LABEL));

            VBox left = new VBox(0, name, says);
            left.setAlignment(Pos.CENTER_LEFT);

            Region gap = new Region();
            HBox.setHgrow(gap, Priority.ALWAYS);

            Button toggle = new Button(on ? "Stop protecting" : "Protect it");
            toggle.setStyle(on
                    ? Palette.words(Palette.SIZE_LABEL, "white")
                      + " -fx-background-color: " + "#2f7d52" + "; -fx-cursor: hand;"
                    : Palette.words(Palette.SIZE_LABEL, Palette.TEXT_BODY)
                      + " -fx-background-color: " + Palette.CONTROL + "; -fx-cursor: hand;");
            toggle.setOnAction(e -> {
                ui.game.setAutoSubsidised(sector, !ui.game.isAutoSubsidised(sector));
                showPolicyMenu();
            });

            HBox row = new HBox(Palette.GAP, left, gap, toggle);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPrefWidth(STATEMENT);
            row.setMaxWidth(STATEMENT);
            row.setStyle("-fx-padding: 8 12 8 12;" + Palette.block(Palette.CONTROL));
            VBox.setMargin(row, new javafx.geometry.Insets(0, 0, 5, 0));
            column.getChildren().add(row);
        }

        column.getChildren().add(statementTotal("Paid last month",
                signedTight(ui.game.getTotalSubsidyPaid(), true),
                ui.game.getTotalSubsidyPaid() > 0 ? Palette.WARN : Palette.TEXT_SPENT));
        column.getChildren().add(statementNote(
                "A subsidy is the only lever on this tab that reaches a sector already in "
                + "default, because a sector that cannot borrow cannot trade its way out. "
                + "It is also the only one with no natural end: nothing here stops paying."));
    }
}
