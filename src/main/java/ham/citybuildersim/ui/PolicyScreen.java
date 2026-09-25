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
 * stagedLadder, dropProposal, pinnedBands) because other screens offer the
 * same levers. Every dial here is drawn by one class, Ladder (0.7.6), which
 * replaced the tax pages' taxLadder and everybody else's stageSlider.
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

         TAXES     a base rate per tax (profit, sales and wage were one city
                   rate until 0.7.4), then the wage bands and the sectors that
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

       AND IT IS A CLASS NOW (0.7.6), ui/Ladder: this banner's taxLadder was
       the shape, and Jerus asked for it everywhere - "create an object or
       class of slider, and then whenever you need it you just call that class
       and plug in the specific sensitivity, max, min and steps". What is left
       here is the wiring to the staged set: ladderOf() registers a Lever for
       the foot bar and stages through propose(); stagedLadder(), under THE
       PIECES A LEVER IS MADE OF, stages a dial that keeps its own apply bar.
       ===================================================================== */

    /** A quarter of a point - every rate that moves off the income tax. */
    static final double STEP_INCOME = .0025;

    /** A twentieth of a point - property, where a quarter is a quarter of the tax. */
    static final double STEP_PROPERTY = .0005;

    /** The staged key of "Every tax at once" on the Everything page - the one city rate's key, which is what that rate became in 0.7.4. */
    static final String EVERY_TAX = "income";

    /** The staged key of one income tax's own base (0.7.4): "base:profit", "base:sales" or "base:wage". */
    static String baseKey(String tax) { return "base:" + tax; }

    /**
     * The base a preview prices one income tax at: its own lever if staged,
     * else the every-tax lever if that is, else what it is. The two are never
     * staged together - a proposal is cleared with the page - but the
     * precedence is the specific one either way.
     */
    double stagedBase(String tax, double current) {
        return staged(baseKey(tax), staged(EVERY_TAX, current));
    }

    /** Whether anything staged moves this income tax's base: its own lever, or every tax at once. */
    boolean baseStaged(String tax) {
        return isStaged(baseKey(tax)) || isStaged(EVERY_TAX);
    }

    /**
     * A registered dial's Ladder, wired to the staged set: the lever goes in
     * the register the foot bar names and applies from, the thumb sits at
     * what is staged, and every move goes through propose(). Handed back
     * unbuilt, so a caller can grey it, narrow it or say its step its own
     * way before build(); ladder() is the tax pages' finished one.
     */
    Ladder ladderOf(Lever lever) {
        policyLevers.put(lever.key(), lever);
        return Ladder.of(lever.min(), lever.max(), lever.step(), lever.read())
                .current(lever.current())
                .showing(bounded(staged(lever.key(), lever.current()), lever))
                .offAtCurrent(isStaged(lever.key()) && changesAtCurrent(lever))
                .itIs(nowReads(lever))
                .stages(v -> propose(lever, v));
    }

    /** A tax page's dial, and the farmland relief's: a registered Ladder whose step reads in points. */
    VBox ladder(Lever lever) {
        return ladderOf(lever).stepReads(PolicyScreen::stepWord).build();
    }

    /** How one step reads on a tax page's ends line: in points of the rate. */
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
        if (moved(v, lever.current(), lever.step()) || changesAtCurrent(lever)) stage(lever.key(), v);
        else unstage(lever.key());
        showPolicyMenu();
    }

    /**
     * Whether a lever still changes something at the value it reads as
     * current (0.7.4): "Every tax at once" once the three income bases have
     * parted. Its current is the profit rate, and setting all three to it
     * still moves sales and wage - so it stages there rather than falling
     * back to "nothing to do". The same for "Every school at once" (0.7.6)
     * once the nine school kinds' prices have parted: its current is the
     * first kind's.
     */
    boolean changesAtCurrent(Lever lever) {
        TaxPolicy policy = ui.game.getEconomyManager().getTaxPolicy();
        return (EVERY_TAX.equals(lever.key()) && policy.incomeRatesSplit())
                || (EVERY_SCHOOL.equals(lever.key()) && policy.tuitionScalesSplit());
    }

    /** What a lever reads today, in words: its current value, or "three rates" and "a price per school" for the every-tax and every-school levers once they have parted. */
    String nowReads(Lever lever) {
        if (!changesAtCurrent(lever)) return lever.read().apply(lever.current());
        return EVERY_SCHOOL.equals(lever.key()) ? "a price per school" : "three rates";
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

        // Each income tax at its own base since 0.7.4.
        double profitBase = proposed ? stagedBase("profit", p.getProfitTaxRate()) : p.getProfitTaxRate();
        double salesBase  = proposed ? stagedBase("sales", p.getSalesTaxRate()) : p.getSalesTaxRate();
        double wageBase   = proposed ? stagedBase("wage", p.getWageTaxRate()) : p.getWageTaxRate();
        double prop = proposed ? staged("property", p.getPropertyTaxRate())
                               : p.getPropertyTaxRate();
        double total = 0;

        for (Sector s : ui.game.getSectors().all()) {

            SectorBooks.SectorMonth m = books.get(s);
            double bearing = m == null ? 0 : Math.max(0, m.preTaxIncome());
            double pOff = proposed ? staged("profit:" + s.key(), p.getProfitOffset(s))
                                   : p.getProfitOffset(s);
            total += bearing * clampRate(profitBase + pOff, TaxPolicy.MAX_INCOME_TAX);

            // Sales moves by the RATIO, not recomputed: this sector's input
            // credits are somebody else's rate and those have not moved.
            double net = vat.getNet(s.key());
            double nowRate = p.effectiveSalesRate(s);
            double sOff = proposed ? staged("sales:" + s.key(), p.getSalesOffset(s))
                                   : p.getSalesOffset(s);
            double atRate = clampRate(salesBase + sOff, TaxPolicy.MAX_INCOME_TAX);
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
            total += wages[i] * clampRate(wageBase + off, TaxPolicy.MAX_INCOME_TAX);
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
                    nowReads(lever),
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

       It is split by which tax it is now. Everything reads, bar the one
       lever that sets every income tax at once; the four act.

       AND EACH OF THE FOUR HAS ITS OWN RATE (0.7.4). Profit, sales and wage
       all moved off ONE city rate, so raising sales tax for every sector was
       fifteen offsets or the city rate, which dragged the other two with it.
       Jerus: "what about just increasing sale tax for all at the same time?
       ... i want to be able to do that for every type of tax, even wage tax".
       Each tax page's top lever is that tax's own base now (baseRateLever),
       and his old city rate is the Everything page's "Every tax at once".
       ===================================================================== */

    /**
     * One income tax's own base (0.7.4): the top lever of the profit, sales
     * and wage pages. Every offset on the page moves off it, and nothing on
     * the other two pages does. Staged under baseKey(tax) and applied
     * through the one foot bar like every other dial on the tab.
     */
    void baseRateLever(VBox column, String tax, String title, String offsets,
                       double current, java.util.function.DoubleConsumer apply) {

        column.getChildren().add(statementHead(title));
        column.getChildren().add(leverHead(pct2(current),
                offsets + " moves off this number, so this one dial moves every one of them "
                + "at once - and only them: the other two income taxes have rates of their "
                + "own. \"Every tax at once\", on the Everything page, sets all three."));
        column.getChildren().add(ladder(new Lever(baseKey(tax), title, current,
                0, TaxPolicy.MAX_INCOME_TAX, STEP_INCOME,
                Money::pct2, apply)));
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
                "Last month, by tax. Each has a rate of its own, and three of the four a "
                + "move off it by sector or by band; the fourth is charged on what things "
                + "are worth rather than on anything anybody earned."));

        column.getChildren().add(taxSourceRow("Profit", profit, all,
                pct2(policy.getProfitTaxRate()), "on what the sectors earned", "Profit"));
        column.getChildren().add(taxSourceRow("Sales", sales, all,
                pct2(policy.getSalesTaxRate()), "on the value they add", "Sales"));
        column.getChildren().add(taxSourceRow("Wage", wage, all,
                pct2(policy.getWageTaxRate()), "off every payroll in the city", "Wage"));
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

        /*
         * EVERY TAX AT ONCE (0.7.4) - what the one city rate became once each
         * income tax had its own. The three bases side by side first, so a
         * player sees when they have parted, then the ladder that sets all
         * three to one number (TaxPolicy.setIncomeTaxRate()).
         */
        boolean parted = policy.incomeRatesSplit();
        column.getChildren().add(statementHead("Every tax at once"));
        column.getChildren().add(statementLine("The profit rate",
                pct2(policy.getProfitTaxRate()), parted ? Palette.ACCENT : Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("The sales rate",
                pct2(policy.getSalesTaxRate()), parted ? Palette.ACCENT : Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("The wage rate",
                pct2(policy.getWageTaxRate()), parted ? Palette.ACCENT : Palette.TEXT_HEAD));
        column.getChildren().add(leverHead(parted ? "three rates" : pct2(policy.getIncomeTaxRate()),
                (parted ? "Profit, sales and wage tax have parted - each page moves its own. "
                        : "Profit, sales and wage tax are one rate today. ")
                + "This dial sets all three to one number, which is what the single city rate "
                + "did; every sector's and every band's move rides whichever base is its "
                + "own, so a sector you have customised keeps its treatment."));
        column.getChildren().add(ladder(new Lever(EVERY_TAX, "Every tax at once",
                policy.getIncomeTaxRate(), 0, TaxPolicy.MAX_INCOME_TAX, STEP_INCOME,
                Money::pct2, policy::setIncomeTaxRate)));

        if (isStaged(EVERY_TAX)) {
            double want = staged(EVERY_TAX, policy.getIncomeTaxRate());
            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("Profit tax", money(profit),
                    money(scaled(profit, profitFactor(policy, policy.getProfitTaxRate(), want))), null));
            column.getChildren().add(wouldBe("Sales tax", money(sales),
                    money(scaled(sales, salesFactor(policy, policy.getSalesTaxRate(), want))), null));
            column.getChildren().add(wouldBe("Wage tax", money(wage),
                    money(wageTaxAtBase(policy, want)), null));
            column.getChildren().add(statementNote(
                    "Property is not on this list: it is charged on value rather than on "
                    + "income, and this dial does not reach it."));
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
        double base = policy.getProfitTaxRate();

        column.getChildren().add(statementHead("Profit tax"));
        column.getChildren().add(leverHead(money(na.getTaxBusiness() + na.getTaxIndustrial()),
                "Charged on what each sector earned before tax. A sector that lost money "
                + "pays nothing, so this is the half of the city's revenue that falls "
                + "exactly when the city needs it most."));

        baseRateLever(column, "profit", "The profit rate", "Every sector's profit tax",
                base, policy::setProfitTaxRate);

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
                "A move is in POINTS off the profit rate, so a sector left at zero is taxed "
                + "at exactly " + pct2(base) + " and follows the profit rate wherever it "
                + "goes. The offsets are capped at "
                + String.format("%.0f", TaxPolicy.MAX_OFFSET * 100) + " points either way."));

        for (Sector sector : ui.game.getSectors().all()) {
            SectorBooks.SectorMonth month = books.get(sector);
            double bearing = month == null ? 0 : Math.max(0, month.preTaxIncome());
            double offset = policy.getProfitOffset(sector);
            final Sector s = sector;
            String key = "profit:" + sector.key();
            boolean touched = baseStaged("profit") || isStaged(key);
            double wantBase = stagedBase("profit", base);
            column.getChildren().add(bandLever(sector.label(), pts(offset),
                    arrow(touched, pct2(policy.effectiveProfitRate(sector)),
                            pct2(clampRate(wantBase + staged(key, offset),
                                    TaxPolicy.MAX_INCOME_TAX))),
                    bearing > 0 ? money(month.tax()) + " paid, on " + money(bearing)
                                + " of pre-tax income"
                                : "it made nothing to be taxed on last month",
                    offset));
            column.getChildren().add(ladder(new Lever(key,
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

        double base = policy.getSalesTaxRate();
        baseRateLever(column, "sales", "The sales rate", "Every sector's sales tax",
                base, policy::setSalesTaxRate);

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
            boolean touched = baseStaged("sales") || isStaged(key);
            double wantBase = stagedBase("sales", base);
            column.getChildren().add(bandLever(sector.label(), pts(offset),
                    arrow(touched, pct2(policy.effectiveSalesRate(sector)),
                            pct2(clampRate(wantBase + staged(key, offset),
                                    TaxPolicy.MAX_INCOME_TAX))),
                    sold > 0 ? money(net) + " net, on " + money(sold) + " of taxable sales"
                             : vat.isInRefund(sector.key())
                                     ? "in refund - its credits exceed what it owes"
                                     : "it sold nothing taxable last month",
                    offset));
            column.getChildren().add(ladder(new Lever(key,
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
        double base = policy.getWageTaxRate();

        column.getChildren().add(statementHead("Wage tax"));
        column.getChildren().add(leverHead(money(na.getTaxWage()),
                "Taken off every payroll in the city before the household sees it. The "
                + "jobs are grouped by the education they need, which is the only "
                + "grouping the labour market itself uses."));

        baseRateLever(column, "wage", "The wage rate", "Every band's wage tax",
                base, policy::setWageTaxRate);

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
            boolean touched = baseStaged("wage") || isStaged(key);
            double wantBase = stagedBase("wage", base);
            column.getChildren().add(bandLever(band.label(), pts(offset),
                    arrow(touched, pct2(policy.effectiveWageRate(band)),
                            pct2(clampRate(wantBase + staged(key, offset),
                                    TaxPolicy.MAX_INCOME_TAX))),
                    payroll > 0 ? money(wageTaxWith(policy, null, base, 0, band))
                                + " a month, off " + money(payroll) + " of payroll"
                                : "nobody in the city holds one of these jobs",
                    offset));
            column.getChildren().add(ladder(new Lever(key,
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
        column.getChildren().add(statementLine("Wage tax, at the wage rate",
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
                + "payroll. A band with a move off the wage rate pays that instead of "
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

        column.getChildren().add(ladder(new Lever("property", "The property rate", prop,
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
            column.getChildren().add(ladder(new Lever(key,
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

        // Each tax's own rate since 0.7.4: one income figure while the three
        // are one, all three once they have parted.
        column.getChildren().add(policyRow("Taxes",
                "each tax's own rate, and the moves off it by band and by sector",
                money(taxRaised()),
                policy.incomeRatesSplit()
                        ? String.format("%.1f / %.1f / %.1f%% on income · %.2f%% on property",
                                policy.getProfitTaxRate() * 100, policy.getSalesTaxRate() * 100,
                                policy.getWageTaxRate() * 100, policy.getPropertyTaxRate() * 100)
                        : String.format("%.1f%% on income · %.2f%% a year on property",
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
                        credit.getBlockedMonths(sector) + " more months of it. A sector that "
                        + "went under cannot borrow to build, and a subsidy is the only thing on "
                        + "this tab that reaches it."});
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
            double rule = market.ruleRate(px.inflation());
            if (Math.abs(advised - market.getPolicyRate()) >= .01) {
                // The rule's own figure, and the dial's stop when that is
                // what bounds it - see DebtManager.ruleRate() (2026-09-21).
                out.add(new String[] {Palette.ACCENT, "The rule disagrees with the dial",
                        String.format("The policy rate is %.2f%% and the rule would set it "
                        + "at %.2f%%%s. Inflation is running at %+.1f%% against a %s "
                        + "target.", market.getPolicyRate() * 100, rule * 100,
                        Math.abs(rule - advised) > 1e-9
                                ? String.format(" - the dial stops at %.2f%%", advised * 100) : "",
                        px.inflation() * 100, DebtManager.targetWords(market.getInflationTarget()))});
            }
        }

        /* ---------------------------- the money itself ---------------------------- */
        if (ui.game.canReformCurrency()) {
            out.add(new String[] {Palette.ACCENT, "The money could be reformed",
                    String.format("Prices are %.1f times what they were at founding. A "
                    + "reform restates every number in the city at once and changes "
                    + "nothing else.", ui.game.getPriceIndex().getIndex())});
        }

        /* ------------------------------ at the stops ------------------------------
         * Each income tax at its own stop since 0.7.4, named: one, two, or
         * all three together.
         */
        java.util.List<String> atStop = new java.util.ArrayList<>();
        if (policy.getProfitTaxRate() >= TaxPolicy.MAX_INCOME_TAX - 1e-9) atStop.add("profit");
        if (policy.getSalesTaxRate()  >= TaxPolicy.MAX_INCOME_TAX - 1e-9) atStop.add("sales");
        if (policy.getWageTaxRate()   >= TaxPolicy.MAX_INCOME_TAX - 1e-9) atStop.add("wage");
        if (!atStop.isEmpty()) {
            String which = atStop.size() == 3 ? "Every income tax is"
                    : atStop.size() == 2 ? capitalised(atStop.get(0)) + " and " + atStop.get(1) + " tax are"
                    : capitalised(atStop.get(0)) + " tax is";
            out.add(new String[] {Palette.WARN,
                    which + " at " + (atStop.size() == 1 ? "its" : "their") + " legal maximum",
                    String.format("%.0f%%. There is no more revenue to be had from %s, whatever "
                    + "the budget says.", TaxPolicy.MAX_INCOME_TAX * 100,
                    atStop.size() == 1 ? "this lever" : "these levers")});
        }

        return out;
    }

    /** "sales" to "Sales", for a flag that opens with a tax's name. */
    static String capitalised(String word) {
        return word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1);
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
                // The Schools page registers its dials so one bar applies
                // them (2026-09-21; a price per school kind since 0.7.6); a
                // page that registers nothing draws nothing here, and keeps
                // its own apply bars.
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
     * The lever itself: a Ladder (0.7.6) that STAGES a change rather than
     * making one, for a dial that keeps its own apply bar - the wage floor,
     * the policy rate, the pension's two, EI's two, the clinic's two, and
     * the fare on Services.
     *
     * Jerus: "i think the tax should a slider, as well as the other stuff in
     * policy" - and he is right. A rate is a continuous quantity, and a row of
     * step buttons makes the player do arithmetic to get anywhere they can
     * already see on a scale. Since 0.7.6 it has the step buttons as well,
     * one step each, because a drag alone cannot land on a step reliably.
     *
     * Dropping the thumb back where it started clears the proposal rather than
     * staging a change of nothing, which is what makes the slider its own
     * cancel button - ITS OWN, since 2026-09-21: it unstages its key, as
     * propose() does for a registered lever, where it used to drop the whole
     * set. On a page of one dial that is the same thing; on the Schools
     * page's five it was one thumb put back throwing four other decisions
     * away.
     *
     * THE REDRAW IS THE CALLER'S SCREEN, not this one. The transit fare is a
     * lever on the Services tab drawn with this same dial, and until
     * 2026-09-18 letting go of it redrew the policy screen - the player was
     * carried to a tab the dial is not on. ui.redraw() draws whatever screen
     * registered itself last, which on this tab is showPolicyMenu().
     */
    VBox stagedLadder(String key, double current, double min, double max,
                      double step, java.util.function.DoubleFunction<String> label) {
        return Ladder.of(min, max, step, label)
                .current(current)
                .showing(staged(key, current))
                .stages(v -> {
                    if (moved(v, current, step)) stage(key, v); else unstage(key);
                    ui.redraw();   // whichever screen the dial is on - the fare dial lives on Services
                })
                .build();
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

        column.getChildren().add(ladder(new Lever("farmland", "Farmland relieved",
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
     *        page's BASE rate alone and every row on the page shows what that does to
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

        column.getChildren().add(stagedLadder("floor", floor,
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
       yours, but the screen shows what a Taylor rule would do". Since 0.7.0
       the player can also hand the dial to the rule - the autopilot - and take
       it back; see WHOSE HAND IS ON THE DIAL below.
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
         * the world's 2% while its own paper was quoted a point under it. Hot
         * money reads the city's rate and the bank's deposit rate, not the
         * dial (see Game.nextMonth and CapitalFlows), so this is the difference
         * money actually follows. (The carry trade reads the bank's lending
         * rate, struck on the dial itself since 0.7.0.)
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
                "The one number under every other rate in the city. Raising it past "
                + "inflation supports the currency and makes every borrower pay more - that "
                + "is not a side effect, it is the same act."));
        column.getChildren().add(statementNote(String.format(
                "This is what the central bank pays on the bank's reserves, and so the "
                + "least anybody in the city lends for. The city's own paper is priced "
                + "from it: at the dial when the city owes nothing, and up to %.0f points "
                + "over when it owes too much against its output and its taxes. The "
                + "Finances tab takes that apart.",
                2 * DebtManager.maxSpreadPerMeasure() * 100)));

        /*
         * THE RULE, AND THE DIAL'S STOP, when they are not the same number
         * (2026-09-21). advised is clamped to the dial; the rule is not. At
         * 45% inflation this line read 25.00% as though the rule said so, and
         * the rule says 67.5% - it is the dial that stops. Both, so the player
         * can see which one is the limit. See DebtManager.ruleRate().
         */
        double ruleSays = market.ruleRate(inflation);
        column.getChildren().add(statementLine("What the rule would set",
                pct2(ruleSays), Math.abs(advised - rate) >= .01
                        ? Palette.WARN : Palette.TEXT_MUTED));
        if (Math.abs(ruleSays - advised) > 1e-9) {
            column.getChildren().add(statementLine(ruleSays > advised
                            ? "...but the dial stops at" : "...but the dial cannot go below",
                    pct2(advised), Palette.WARN));
        }
        // Against the player's target since 0.7.4 - the dial under the autopilot below.
        double target = market.getInflationTarget();
        column.getChildren().add(statementLine("Inflation, year on year",
                px.hasRate() ? String.format("%+.1f%%", inflation * 100) : "not yet",
                px.hasRate() && Math.abs(inflation - target) > .01
                        ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("...against a target of",
                DebtManager.targetWords(target), Palette.TEXT_MUTED));
        column.getChildren().add(statementNote(px.hasRate()
                ? market.adviceReason(inflation)
                : "There is not yet a year of prices to measure inflation against. The "
                + "rule has nothing to say until there is."));

        column.getChildren().add(statementHead("What it is costing"));
        column.getChildren().add(statementLine("The city itself borrows at",
                pct2(cityRate), Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("...the dial, plus what it owes",
                pct2(market.floorRate()) + " floor", Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("Savers are paid",
                pct2(bank.depositRate()), Palette.GOOD));
        column.getChildren().add(statementLine("The world's own rate",
                pct2(DebtManager.WORLD_BASE_RATE), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("...so the city pays over it",
                String.format("%+.2f pts", over * 100),
                over > 0 ? Palette.ACCENT : Palette.TEXT_MUTED));
        /*
         * ...AND IN REAL TERMS (0.7.2), which is what the currency answers to
         * now: Game.realRateDifferential(), the figure the month hands it.
         */
        double real = ui.game.realRateDifferential();
        column.getChildren().add(statementLine("...and the dial, less inflation, over the world's",
                String.format("%+.2f pts", real * 100), real >= 0 ? Palette.GOOD : Palette.BAD));
        column.getChildren().add(statementNote(
                "Hot money follows the first difference in, and leaves the day it closes. "
                + "The currency follows the second: a dial under inflation is a real rate "
                + "the world is paid to leave, and it pushes the currency down however high "
                + "the number on the dial is. The first is also what the bank prices every "
                + "loan up from, so the whole city's credit moves with it."));
        /*
         * ...AND WHAT SAVERS EARN IN REAL TERMS, AND WHAT THAT DOES TO WHAT
         * THE HOUSEHOLDS SPEND (0.7.3) - the demand channel. Both are the
         * model's: Game.realDepositRate() and Game.spendFactor(), the factor
         * the next month's plan is struck at. See HouseholdBalance's banner
         * AND WHAT IT SPENDS ANSWERS THE REAL RATE.
         */
        double realDeposit = ui.game.realDepositRate();
        double spend = ui.game.spendFactor();
        column.getChildren().add(statementLine("Savers earn, after inflation",
                String.format("%+.2f%%", realDeposit * 100),
                realDeposit >= 0 ? Palette.GOOD : Palette.BAD));
        column.getChildren().add(statementLine("...so households spend, above a basket a head",
                String.format("%.0f%% of what they would at zero", spend * 100),
                Math.abs(spend - 1) < .005 ? Palette.TEXT_MUTED : spend < 1 ? Palette.ACCENT : Palette.WARN));
        column.getChildren().add(statementNote(String.format(
                "Savers earn %+.1f%% real, so households spend %.0f%% of what they would at zero. "
                + "A deposit rate over inflation pays people to wait, and what they do not spend "
                + "is demand the shops do not see; one under it pays them to buy now. Only what "
                + "is above a basket a head moves, and never below %.0f%% or past %.0f%% of it.",
                realDeposit * 100, spend * 100,
                HouseholdBalance.SPEND_FLOOR * 100, HouseholdBalance.SPEND_CEILING * 100)));

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
            column.getChildren().add(statementLine("The money is the", unit.name(ui.game.getCurrency()),
                    Palette.ACCENT));
            column.getChildren().add(statementLine("...one of which is",
                    String.format("%,.0f founding", unit.getUnit()), Palette.TEXT_MUTED));
            column.getChildren().add(statementNote(
                    "The index is measured against the FOUNDING basket in founding money, "
                    + "and stays comparable across reforms precisely because a reform "
                    + "divides its base too."));
        }

        /*
         * WHOSE HAND IS ON THE DIAL (0.7.0). Jerus's autopilot: the rule can
         * hold the dial, and the page says which hand it is in and what the
         * rule would set. The toggle is the model's (DebtManager's autopilot,
         * saved with the rate); applying a rate by hand below takes the dial
         * back, as it would from any central bank the player owns.
         */
        boolean ruleHasIt = market.isAutopilot();
        column.getChildren().add(statementLine("The dial is in",
                ruleHasIt ? "the rule's hand" : "your hand",
                ruleHasIt ? Palette.ACCENT : Palette.TEXT_HEAD));
        javafx.scene.layout.FlowPane hand = new javafx.scene.layout.FlowPane(6, 6);
        hand.setMaxWidth(STATEMENT);
        hand.getChildren().add(stepChip(ruleHasIt ? "take the dial back" : "hand the dial to the rule",
                () -> { market.setAutopilot(!ruleHasIt); showPolicyMenu(); }, false));
        column.getChildren().add(hand);
        column.getChildren().add(statementNote(ruleHasIt
                ? String.format("Every month, before anything is priced, the central bank sets "
                        + "the dial where the rule says - %s on this month's prices. Setting a "
                        + "rate yourself takes the dial back.", pct2(advised))
                : String.format("The rule would set %s. Hand it the dial and the central bank "
                        + "will set it there every month, until you take it back.", pct2(advised))));

        /*
         * THE TARGET, AS A DIAL (0.7.4), under the hand that holds the rate,
         * because the rule is what it moves. Jerus: "i want to have the dial
         * not target 0 inflation, set it so that you can choose what is your
         * inflation target." DebtManager.getInflationTarget(), in half
         * points to MAX_INFLATION_TARGET: chips for the everyday settings and
         * a ladder for the rest. APPLIED AT ONCE, like the toggle above and
         * the holdings and the ceiling below, not staged - it moves no price
         * this month, only what the rule says, and the rule's line above and
         * the sentence under this redraw on it. Under a head of its own, and
         * the rate's slider under one after it, so neither dial reads as the
         * other's.
         */
        column.getChildren().add(statementHead("What the rule aims at"));
        column.getChildren().add(statementLine("The target",
                DebtManager.targetWords(target) + " a year", Palette.ACCENT));
        String[] targets = new String[TARGET_STEPS.length];
        for (int i = 0; i < TARGET_STEPS.length; i++) targets[i] = TARGET_STEPS[i] + "%";
        column.getChildren().add(chipStrip(targets, DebtManager.targetWords(target),
                Palette.SIZE_CAPTION, picked -> {
                    market.setInflationTarget(Integer.parseInt(picked.replace("%", "")) / 100.0);
                    showPolicyMenu();
                }));
        // The ladder beside the chips (0.7.6): half a point a step across the
        // whole range, applied at once like the chips, landed on the
        // half-point grid so a run of steps reads 3% and not 3.0000000000000004%.
        column.getChildren().add(Ladder.of(DebtManager.MIN_INFLATION_TARGET,
                        DebtManager.MAX_INFLATION_TARGET, TARGET_STEP, DebtManager::targetWords)
                .current(target)
                .appliesAtOnce(v -> { market.setInflationTarget(halfPoint(v)); showPolicyMenu(); })
                .build());
        // Struck from the rule at both targets - DebtManager.ruleRate(inflation,
        // target) - on this month's inflation once there is a year of it, and
        // on 5% as the example until there is.
        double example = px.hasRate() ? inflation : .05;
        double otherTarget = target > 1e-9 ? 0 : DebtManager.DEFAULT_INFLATION_TARGET;
        column.getChildren().add(statementNote(String.format(
                "The rule aims prices here. At %s with inflation at %.1f%% it sets %.1f%%; at %s "
                + "it would set %.1f%%. A point of target is %.1f points off the rule's rate at "
                + "any inflation - the same slope, aimed somewhere else.",
                DebtManager.targetWords(target), example * 100, market.ruleRate(example) * 100,
                DebtManager.targetWords(otherTarget), market.ruleRate(example, otherTarget) * 100,
                DebtManager.TAYLOR_WEIGHT)));

        column.getChildren().add(statementHead("The rate, by hand"));

        /*
         * THE DIAL REACHES 100% (0.7.2): DebtManager.MAX_POLICY_RATE lifted
         * from 25% with the currency's rate channel. A slider across a hundred
         * points puts the everyday range in its first tenth, so the steps a
         * player reaches for are chips beside it - 0 to 3 for a quiet city,
         * 5 to 20 for a warm one, 30 to 100 for a spiral - each staging the
         * rate, which the slider then fine-tunes.
         */
        column.getChildren().add(stagedLadder("policy", rate,
                DebtManager.MIN_POLICY_RATE, DebtManager.MAX_POLICY_RATE, .0005,
                Money::pct2));
        javafx.scene.layout.FlowPane steps = new javafx.scene.layout.FlowPane(6, 6);
        steps.setMaxWidth(STATEMENT);
        for (int pct : DIAL_STEPS) {
            final double at = pct / 100.0;
            steps.getChildren().add(stepChip(pct + "%",
                    () -> { stage("policy", at); showPolicyMenu(); },
                    Math.abs(want - at) > 1e-9));
        }
        column.getChildren().add(steps);

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
                    // The bank's chosen share of the dial at its funding position
                    // today (0.7.7), not the city's rate times a fixed pass-through.
                    pct2(Math.max(0, want) * bank.depositShare()),
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
            // By hand, so it takes the dial from the rule - see DebtManager.takeTheDial().
            column.getChildren().add(applyBar("Set the policy rate to " + pct2(want),
                    () -> market.takeTheDial(want)));
        }

        /* ------------------- THE HOLDINGS DIAL (0.7.1) -------------------
         * Jerus: "the central bank would buy gbonds or sell gbonds from thin
         * air basically ... like QE and QT, just like USA does ... to
         * manipulate the rate accordingly?" Beside the price, the quantity:
         * the share of the city's term paper the central bank aims to hold,
         * bought from the bank with money it makes (CentralBank, THE
         * HOLDINGS DIAL). Applied at once, like the autopilot toggle above; it
         * moves the book a quarter of the way a month from the next press.
         */
        CentralBank cb = ui.game.getCentralBank();
        column.getChildren().add(statementHead("How much of the city's paper it holds"));
        column.getChildren().add(statementLine("The dial",
                String.format("%.0f%% of the term paper", cb.getTargetShare() * 100), Palette.ACCENT));
        column.getChildren().add(statementLine("It holds",
                String.format("%s, %.0f%%", money(cb.getPaperHeld()),
                        market.centralBankShareOfTerm() * 100), Palette.TEXT_HEAD));
        int settings = (int) Math.round(CentralBank.MAX_QE_SHARE * 10) + 1;
        String[] shares = new String[settings];
        for (int i = 0; i < settings; i++) shares[i] = (i * 10) + "%";
        String now = Math.round(cb.getTargetShare() * 100) + "%";
        column.getChildren().add(chipStrip(shares, now, Palette.SIZE_CAPTION, picked -> {
            cb.setTargetShare(Integer.parseInt(picked.replace("%", "")) / 100.0);
            showPolicyMenu();
        }));
        // ...and the ladder under the chips (0.7.6), a chip's ten points a
        // step, applied at once as the chips are.
        column.getChildren().add(Ladder.of(0, CentralBank.MAX_QE_SHARE, HOLDINGS_STEP,
                        v -> String.format("%.0f%%", v * 100))
                .current(cb.getTargetShare())
                .appliesAtOnce(v -> { cb.setTargetShare(Math.round(v * 100) / 100.0); showPolicyMenu(); })
                .build());
        column.getChildren().add(statementNote(String.format(
                "Buying creates the money that pays for it and takes the paper off the bank's "
                + "book; the long end of the curve bends down in proportion - at %.0f%% the "
                + "term premium is gone. The thirty-year rate is %.2f points lower for what it "
                + "holds now. Selling destroys the money again. It never buys from the "
                + "treasury: that is printing, and it is the advances' line.",
                CentralBank.MAX_QE_SHARE * 100, market.compression(360) * 100)));

        /* ------------------- THE CEILING, AS A DIAL (0.7.2) -------------------
         * Batch B: "the ceiling is small in a small city" - six months of
         * revenue bound within months of first drawing, and past it Jerus's
         * rule pays the promises and cuts the rest. The ceiling is the
         * player's now (CentralBank.setAdvancesCeilingMonths()), beside the
         * rate and the holdings: how much printing the treasury may lean on
         * before the arrears rule decides. Applied at once, like the holdings.
         */
        column.getChildren().add(statementHead("How far it will advance the treasury"));
        column.getChildren().add(statementLine("The ceiling",
                String.format("%.0f months of revenue, %s", cb.getAdvancesCeilingMonths(),
                        money(cb.ceiling())), Palette.ACCENT));
        column.getChildren().add(statementLine("The treasury owes it",
                money(cb.getAdvancesToTreasury()),
                cb.ceilingBound() ? Palette.BAD : cb.getAdvancesToTreasury() > 0 ? Palette.WARN
                        : Palette.TEXT_HEAD));
        String[] months = new String[CEILING_STEPS.length];
        for (int i = 0; i < CEILING_STEPS.length; i++) months[i] = CEILING_STEPS[i] + " months";
        String nowMonths = Math.round(cb.getAdvancesCeilingMonths()) + " months";
        column.getChildren().add(chipStrip(months, nowMonths, Palette.SIZE_CAPTION, picked -> {
            cb.setAdvancesCeilingMonths(Integer.parseInt(picked.replace(" months", "")));
            showPolicyMenu();
        }));
        // ...and the ladder under the chips (0.7.6): a month of revenue a
        // step over the whole of CentralBank's range, the chips' settings all
        // on it, applied at once as the chips are.
        column.getChildren().add(Ladder.of(0, CentralBank.MAX_ADVANCES_CEILING, CEILING_STEP,
                        PolicyScreen::monthsWords)
                .current(cb.getAdvancesCeilingMonths())
                .appliesAtOnce(v -> { cb.setAdvancesCeilingMonths(v); showPolicyMenu(); })
                .build());
        column.getChildren().add(statementNote(String.format(
                "When the treasury runs dry the central bank advances the gap in money it "
                + "makes - printing - up to this many months of the treasury's revenue. Past "
                + "it, pensions, EI, health, the schools, wages and debts are still paid, and "
                + "everything else waits for cash and is owed as arrears. A higher ceiling "
                + "keeps the lights on longer and prints more doing it; the most is %.0f "
                + "months.", CentralBank.MAX_ADVANCES_CEILING)));
    }

    /** The policy rates the dial's chips stage, in percent (0.7.2): the everyday range finely, the spiral's coarsely. */
    static final int[] DIAL_STEPS = { 0, 1, 2, 3, 5, 8, 10, 15, 20, 30, 50, 75, 100 };

    /** The advances ceiling's settings, in months of revenue (0.7.2), up to CentralBank.MAX_ADVANCES_CEILING. */
    static final int[] CEILING_STEPS = { 3, 6, 12, 24, 36 };

    /** The inflation targets the chips set at once, in percent (0.7.4): the everyday range, the ladder beside them reaching the rest. */
    static final int[] TARGET_STEPS = { 0, 1, 2, 3, 4, 5 };

    /** One step of the target's ladder (0.7.4): half a point. */
    static final double TARGET_STEP = .005;

    /** One step of the holdings' ladder (0.7.6): ten points of the term paper, the chips' own spacing. */
    static final double HOLDINGS_STEP = .10;

    /** One step of the ceiling's ladder (0.7.6): a month of revenue, the unit the chips are in. */
    static final double CEILING_STEP = 1;

    /** A ceiling as the ladder reads it: "1 month", "6 months". */
    static String monthsWords(double months) {
        return String.format("%.0f month%s", months, Math.abs(months - 1) < 1e-9 ? "" : "s");
    }

    /** A target on the half-point grid, so a run of steps lands on 3% and not on 3.0000000000000004%. */
    static double halfPoint(double target) {
        return Math.round(target / TARGET_STEP) / (1 / TARGET_STEP);
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
                "A reform issues a new " + ui.game.getCurrency().name() + " worth a round number of old "
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
                    "Pick how many of today's " + ui.game.getCurrency().plural() + " one new one should "
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
                fxRate(fx.getRate()),
                fxRate(fx.getRate() / factor), Palette.ACCENT));
        column.getChildren().add(wouldBe("One new one would be worth",
                "1 of today's", String.format("%,.0f of today's", factor),
                Palette.TEXT_HEAD));
        column.getChildren().add(wouldTotal("...and be called",
                unit.name(ui.game.getCurrency()), afterName(unit, factor, ui.game.getCurrency()), Palette.ACCENT));

        column.getChildren().add(statementNote(
                "Everything foreign stays where it is. A debt owed in US dollars is still "
                + "owed in US dollars, and food bought abroad still costs abroad what it "
                + "always did - what changes is the number of " + ui.game.getCurrency().plural()
                + " it takes to buy one."));

        column.getChildren().add(applyBar(
                "Issue the new " + ui.game.getCurrency().name(),
                () -> {
                    if (ui.game.reformCurrency(factor)) {
                        GameLog.note(String.format(
                                "Currency reform: one new %s for %,.0f old ones.",
                                ui.game.getCurrency().name(), factor));
                    }
                }));
    }

    /** What the city's money would be called after lopping by this factor. */
    static String afterName(Denomination unit, double factor, Currency money) {
        Denomination next = new Denomination();
        next.restore(unit.toSaveArray());
        next.lop(factor);
        return next.name(money);
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
        column.getChildren().add(stagedLadder("contrib", contribution,
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
        column.getChildren().add(stagedLadder("pension", replacement,
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

       AND SINCE 0.7.6 THE PRICE IS ONE PER KIND OF SCHOOL. Jerus: "not only
       can you raise prices but also raise the price for a specific
       university, and beside the dial it shows the current space, the
       current students, and the current cost and revenue." The price of a
       place keeps its every-school dial at the top and has a row per kind
       under it - its own dial, and its places, students, cost and revenue
       off the model (schoolPriceRow) - all of them on the one bar. Every
       dial on the page is a Ladder.

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
        boolean split = policy.tuitionScalesSplit();
        boolean scaleStaged = anyScaleStaged();
        boolean moved = isStaged("tuition") || scaleStaged;

        /* -------------------- the levers, registered for the one bar -------------------- */
        Lever shareLever = register(new Lever("tuition", "The city's share of tuition", share, 0, 1, .01,
                r -> String.format("%.0f%%", r * 100), schools::setTuitionSubsidy));
        Lever everySchool = register(new Lever(EVERY_SCHOOL, "The price of a place, every school", scale,
                0, TaxPolicy.MAX_TUITION_SCALE, TUITION_STEP, PolicyScreen::scaleWords,
                policy::setTuitionScale));
        Lever loanLever = register(new Lever("loanRate", "Interest on student loans", policy.getStudentLoanRate(),
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
            // At the kind's own staged price (0.7.6), else every school's, else today's.
            double feeThen = schools.feeAtOne(type) * stagedScaleOf(policy, type);
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

        column.getChildren().add(ladderOf(shareLever).build());

        /* ---------------------------- the price of a place ---------------------------- */
        column.getChildren().add(statementHead("What a place is priced at"));
        column.getChildren().add(leverHead(split ? "a price per school" : scaleWords(scale),
                "The founding tuition table times this, before the city's share comes off. "
                + "The table was struck against the wages of its day; against today's the "
                + "trap in the note above is quiet at x1 and back around x3. Nothing at 0: a "
                + "free place, and the city forgoes nothing because there is nothing to "
                + "forgo. The first dial sets every school at once; each kind below has its "
                + "own, and the first dial puts them back to one price."));
        column.getChildren().add(schoolPriceHead("Every school at once"));
        // Moving every school at once takes back whatever one kind had staged:
        // it is a price for all nine, and the preview and the bar should say so.
        column.getChildren().add(ladderOf(everySchool)
                .stages(v -> {
                    for (EducationType kind : EducationType.values()) {
                        if (kind != EducationType.NONE) unstage(schoolKey(kind));
                    }
                    propose(everySchool, v);
                })
                .build());

        /*
         * ONE ROW PER KIND (0.7.6). Jerus: "raise the price for a specific
         * university, and beside the dial it shows the current space, the
         * current students, and the current cost and revenue." Each kind's
         * own ladder, staged like the rest, and beside it four figures off the
         * model: the places its standing buildings seat, the students
         * Education enrolled in it this month (the whole body in flight, for
         * an adult course), what its buildings cost the treasury - staffed
         * payroll and upkeep, Education.getCostOf() - and the tuition its
         * students paid, getFeesOf(). A kind with nothing standing says so and
         * its dial is greyed: a price for a school the city does not have
         * moves nothing today, though every school at once still sets it.
         */
        column.getChildren().add(statementNote(
                "Each kind of school at its own price. Beside each: the places its buildings "
                + "seat, the students in it this month, what its staff and buildings cost the "
                + "treasury, and the tuition its students paid."));
        double[] places = ui.game.getBuildingManager().getBuiltEducationPlaces();
        for (EducationType kind : EducationType.values()) {
            if (kind == EducationType.NONE) continue;
            column.getChildren().add(schoolPriceRow(kind, policy, schools, places[kind.ordinal()]));
        }

        if (moved) {
            double billedNow = schools.getSubsidy() + schools.getFees();
            double billedThen = schools.billedAt(kind -> stagedScaleOf(policy, kind));
            double subsidyThen = billedThen * want;
            double feesThen = billedThen * (1 - want);
            double netThen = schools.getPayroll() + schools.getUpkeep() - feesThen;

            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("The city's share",
                    String.format("%.0f%%", share * 100),
                    String.format("%.0f%%", want * 100),
                    want >= share ? Palette.WARN : Palette.GOOD));
            // Every school at once as one line, then each kind moved on its own.
            if (isStaged(EVERY_SCHOOL)) {
                double every = staged(EVERY_SCHOOL, scale);
                column.getChildren().add(wouldBe(everySchool.name(), nowReads(everySchool),
                        scaleWords(every), every <= scale ? Palette.GOOD : Palette.WARN));
            }
            for (EducationType kind : EducationType.values()) {
                if (kind == EducationType.NONE || !isStaged(schoolKey(kind))) continue;
                double priceNow = policy.tuitionScaleOf(kind), priceThen = stagedScaleOf(policy, kind);
                column.getChildren().add(wouldBe("The price of a place, " + kind.getLabel(),
                        scaleWords(priceNow), scaleWords(priceThen),
                        priceThen <= priceNow ? Palette.GOOD : Palette.WARN));
            }
            column.getChildren().add(wouldBe("Billed a month, before the city's share",
                    money(billedNow), money(billedThen), Palette.TEXT_HEAD));
            column.getChildren().add(wouldBe("Tuition the city covers",
                    money(schools.getSubsidy()), money(subsidyThen), Palette.WARN));
            column.getChildren().add(wouldBe("Tuition households pay",
                    money(schools.getFees()), money(feesThen), Palette.GOOD));
            column.getChildren().add(wouldTotal("Net cost to the city",
                    money(schools.getNetCost()), money(netThen),
                    netThen <= schools.getNetCost() ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(statementNote(
                    "Against the courses being taken now, each kind's students at its own "
                    + "price - and that is the half this preview cannot do, because the point "
                    + "of these dials is to change who enrols. A cheaper place fills a course, "
                    + "and the bill arrives with the students."));
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
        column.getChildren().add(ladderOf(new Lever("grantAmount", "...at", amountNow, 0, amountMax, amountStep,
                v -> amountWords(wantBasis, v), v -> policy.setGrant(wantBasis, v))).build());

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
        column.getChildren().add(ladderOf(loanLever).build());
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

    /** The staged key of "Every school at once" on the Schools page - the one tuition scale's key, which is what that scale became in 0.7.6. */
    static final String EVERY_SCHOOL = "tuitionScale";

    /** One step of every price-of-a-place ladder: a twentieth of the founding table. */
    static final double TUITION_STEP = .05;

    /** The staged key of one school kind's own price (0.7.6): "tuitionScale:UNIVERSITY". */
    static String schoolKey(EducationType kind) { return EVERY_SCHOOL + ":" + kind.name(); }

    /** A tuition scale as the page writes it: "x1.00". */
    static String scaleWords(double scale) { return String.format("x%.2f", scale); }

    /**
     * The scale a preview prices one kind of school at: its own lever if
     * staged, else every school at once if that is, else what it is (0.7.6).
     * Moving every school at once unstages the kinds, so the two meet only
     * when a kind was moved after it - and then the kind is the later word.
     */
    double stagedScaleOf(TaxPolicy policy, EducationType kind) {
        return staged(schoolKey(kind), staged(EVERY_SCHOOL, policy.tuitionScaleOf(kind)));
    }

    /** Whether anything staged moves a price of a place: every school at once, or any kind's own. */
    boolean anyScaleStaged() {
        if (isStaged(EVERY_SCHOOL)) return true;
        for (EducationType kind : EducationType.values()) {
            if (kind != EducationType.NONE && isStaged(schoolKey(kind))) return true;
        }
        return false;
    }

    /** The small label over a price-of-a-place ladder: which school it prices. */
    static Label schoolPriceHead(String name) {
        Label head = new Label(name);
        head.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_HEAD) + " -fx-padding: 6 0 0 0;");
        return head;
    }

    /** How wide the four figures beside a school kind's ladder are held. */
    static final double SCHOOL_FIGURES = 150;

    /**
     * One school kind's row: its name and its own ladder on the left, and on
     * the right the four figures the model holds for it - places, students,
     * cost and revenue - or "no school" and a greyed dial when nothing of
     * that kind is standing.
     */
    HBox schoolPriceRow(EducationType kind, TaxPolicy policy, Education schools, double places) {

        boolean standing = places > 0;
        Lever own = new Lever(schoolKey(kind), "The price of a place, " + kind.getLabel(),
                policy.tuitionScaleOf(kind), 0, TaxPolicy.MAX_TUITION_SCALE, TUITION_STEP,
                PolicyScreen::scaleWords, v -> policy.setTuitionScaleOf(kind, v));
        double ladderWide = STATEMENT - SCHOOL_FIGURES - Palette.GAP - 20;
        /*
         * AGAINST WHAT THE KIND WOULD OTHERWISE BE CHARGED. With every school
         * at once staged, a kind left alone takes that price - so its thumb
         * sits there, and putting it back at today's price is a decision to
         * stage (this kind stays where it is while the rest move), where
         * putting it at every school's price is no decision of its own.
         */
        double otherwise = staged(EVERY_SCHOOL, own.current());
        VBox left = new VBox(0, schoolPriceHead(kind.getLabel()),
                ladderOf(own)
                        .showing(stagedScaleOf(policy, kind))
                        .stages(v -> {
                            if (moved(v, otherwise, TUITION_STEP)) stage(own.key(), v); else unstage(own.key());
                            showPolicyMenu();
                        })
                        .greyed(!standing).wide(ladderWide).build());
        left.setPrefWidth(ladderWide);
        left.setMaxWidth(ladderWide);

        VBox right = new VBox(1);
        right.setPrefWidth(SCHOOL_FIGURES);
        right.setMinWidth(SCHOOL_FIGURES);
        right.setMaxWidth(SCHOOL_FIGURES);
        right.setAlignment(Pos.CENTER_LEFT);
        if (!standing) {
            right.getChildren().add(schoolFigure("no school", "", Palette.TEXT_SPENT));
        } else {
            right.getChildren().add(schoolFigure("places", people(places), Palette.TEXT_BODY));
            right.getChildren().add(schoolFigure("students", people(schools.getEnrolled(kind)), Palette.TEXT_BODY));
            right.getChildren().add(schoolFigure("cost", money(schools.getCostOf(kind)), Palette.WARN));
            right.getChildren().add(schoolFigure("revenue", money(schools.getFeesOf(kind)), Palette.GOOD));
        }

        HBox row = new HBox(Palette.GAP, left, right);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefWidth(STATEMENT);
        row.setMaxWidth(STATEMENT);
        row.setStyle("-fx-padding: 2 10 2 10;" + Palette.block(Palette.PANEL));
        VBox.setMargin(row, new javafx.geometry.Insets(0, 0, 5, 0));
        return row;
    }

    /** One of the four figures beside a school kind's ladder: its word on the left, the figure on the right. */
    static HBox schoolFigure(String word, String figure, String tone) {
        Label name = new Label(word);
        name.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label value = new Label(figure);
        value.setStyle(Palette.figure(Palette.SIZE_CAPTION, tone));
        HBox line = new HBox(4, name, gap, value);
        line.setAlignment(Pos.CENTER_LEFT);
        line.setPrefWidth(SCHOOL_FIGURES);
        line.setMaxWidth(SCHOOL_FIGURES);
        return line;
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
        column.getChildren().add(stagedLadder("eiPremium", premium,
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
        column.getChildren().add(stagedLadder("eiBenefit", benefit,
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
        column.getChildren().add(stagedLadder("healthFee", scale,
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
        column.getChildren().add(stagedLadder("healthPremium", premium,
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
