package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import ham.citybuildersim.ui.Levers.DialCard;
import ham.citybuildersim.ui.Pieces.Effect;
import ham.citybuildersim.ui.Pieces.Look;
import ham.citybuildersim.ui.Pieces.Press;
import ham.citybuildersim.ui.Pieces.Rule;
import ham.citybuildersim.ui.Pieces.Run;
import ham.citybuildersim.ui.Pieces.ScaleRow;
import ham.citybuildersim.ui.Pieces.Segment;
import ham.citybuildersim.ui.Pieces.Slice;
import ham.citybuildersim.ui.Pieces.Step;
import ham.citybuildersim.ui.Pieces.Tick;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleFunction;
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
import static ham.citybuildersim.ui.Levers.*;

/**
 * The Policy tab: every number the city sets for itself - the taxes, the
 * wage floor, the price of money and the promises - each a dial with what
 * it would do beside it, on a hub of four area cards and the levers that are
 * biting, and the pages behind them, a picture a page.
 *
 * WHY THIS SHAPE (0.7.36). It was a 560 px statement column in a 1,270 px
 * centre: a landing of four rows and a list of flags, and behind it thirteen
 * pages, each a lever's figure in 20 px type, a paragraph, the ladder, and
 * once moved a statement of what it would do - the paragraphs most of the
 * screen. Jerus, on the screens not yet redone: "the others are still full
 * of text and the design could be more intuitive and fun". Redrawn in
 * Build's style (the project's spec-policy-0735.md): the hub's four AREA
 * CARDS, each a door with its figure and a small picture, over WHAT IS
 * BITING, a card a lever forcing someone's hand, and the decisions lately
 * made; four areas with a chip each in the head and their pages as tabs;
 * every page a picture first - the tax take as a bar, the payers ranked, the
 * wage ladder against the floor, the rates on one line, the pension's cover
 * beside a pensioner's month - and every dial a DIAL CARD (Levers.dialCard())
 * with what it does beside it, before and after, following the thumb. Every
 * paragraph is behind an (i); every old table behind "details".
 *
 * EVERY "AFTER" IS THE MODEL'S (the spec's D2). A staged set goes through a
 * detached copy of the city's policy (TaxPolicy.copy()) and its own setters,
 * and each figure is the owner's read of the copy - PolicyPreview's tax take
 * and THE BUDGET, EconomyManager's payroll lines, Unemployment's pool, the
 * bank's own rule for its savers. The screen adds nothing up.
 *
 * AND EVERY DIAL IS STILL A PROPOSAL UNTIL IT IS APPLIED (the spec's D4,
 * unchanged): the taxes' and the schools' dials stage into the tray at the
 * foot of the stage and one press applies them; the wage floor, the policy
 * rate and the promises' dials keep an Apply on their own card; the target,
 * the central bank's holdings and ceiling, the hand on the dial and the
 * subsidies apply at once; and a page change throws the staged set away.
 *
 * Split out of UserInterface on 2026-09-18. The shell reads which area and
 * page are open (policyArea, policyPage) for the rail and the scroll memory,
 * and other screens open a page by setting them and calling showPolicyMenu();
 * the transit fare on Infrastructure is staged with ownLadder() and
 * applied with applyFoot(), which is why they stay here. Every dial is drawn
 * by one class, Ladder (0.7.6).
 */
final class PolicyScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    PolicyScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       POLICY (0.7.36): THE FRAME

       The head ("Policy", or the breadcrumb "Policy › Taxes", "Policy" a
       way back to the hub) with the four areas as chips at its right; the
       area's pages as tabs under it; the four figures across every page -
       TAX A MONTH, THE FLOOR, THE POLICY RATE, PROMISES - each a door; the
       page, which alone scrolls (BankScreen's frame, 0.7.33); and on the
       pages whose dials batch, the staged tray at the foot of the stage.
       ===================================================================== */

    /** The four areas, by what kind of lever each is - Jerus picked the grouping. */
    static final String TAXES = "Taxes", WAGES = "Wages", MONEY = "Money", PROMISES = "Promises";

    /** ...as the head's chips list them. */
    static final String[] AREAS = {TAXES, WAGES, MONEY, PROMISES};

    /** ...and each one's icon: the coin, the staff, the bank, the people. */
    static final String[] AREA_ICONS = {Icons.COIN, Icons.STAFF, Icons.BANK, Icons.POPULATION};

    /** The area open, one of AREAS, or null for the hub. */
    String policyArea = null;                  // null is the hub
    /** The Taxes area's first page: where a door to the taxes lands, and the page the shell resets to. */
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

    /** Each page's icon on its tab, in the pages' order. */
    static final String[] TAX_ICONS = {Icons.OVERVIEW, Icons.SECTOR, Icons.SHOPS, Icons.STAFF, Icons.HOMES};
    /** ...Wages' one, the staff. */
    static final String[] WAGE_ICONS = {Icons.STAFF};
    /** ...Money's: the bank for the policy rate, the banknote for the currency reform. */
    static final String[] MONEY_ICONS = {Icons.BANK, Icons.BANKNOTE};
    /** ...and the promises': the cane, the staff, health, education and the sector icon for the subsidies. */
    static final String[] PROMISE_ICONS = {Icons.CANE, Icons.STAFF, Icons.HEALTH, Icons.EDUCATION, Icons.SECTOR};

    /** What the player has opened - a fold, a payer's own dial, the folded zero payers - by key, so a redraw on the clock leaves it open (Land's rule: kept while the game runs, not saved; the spec's D15). */
    final Set<String> openLines = new HashSet<>();

    /** The month each biting flag was first drawn in, by its heading: a flag first seen this month carries NEW until the month turns. */
    private final Map<String, Integer> flagSeen = new HashMap<>();

    /** The page's scroller. */
    private javafx.scene.control.ScrollPane body;

    /** How much of the stage the fixed frame takes above the page's scroller - the head, the tabs and the four figures - until it is laid out and its own height read. */
    static final double FRAME_CHROME = 200;

    /** What the menu spends around the frame and the page: its padding over and under them and the gap between, and four pixels of slack (BankScreen's, for the same frame). */
    static final double STAGE_REST = 36;

    /** How tall the staged tray is taken to be until it is laid out. */
    static final double TRAY_CHROME = 90;

    /** The pages of an area, in their tabs' order. */
    static String[] pagesOf(String area) {
        if (area == null) return new String[0];
        return switch (area) {
            case WAGES    -> POLICY_WAGE_PAGES;
            case MONEY    -> POLICY_MONEY_PAGES;
            case PROMISES -> POLICY_PROMISE_PAGES;
            default       -> POLICY_TAX_PAGES;
        };
    }

    /** ...and their icons. */
    static String[] iconsOf(String area) {
        if (area == null) return new String[0];
        return switch (area) {
            case WAGES    -> WAGE_ICONS;
            case MONEY    -> MONEY_ICONS;
            case PROMISES -> PROMISE_ICONS;
            default       -> TAX_ICONS;
        };
    }

    /**
     * The tab's entry point: the hub, or the area and page open. Named for
     * the shell, which calls it from the rail, and for every screen whose
     * door sets policyArea and policyPage first.
     */
    void showPolicyMenu() {
        ui.clearMenu("showPolicyMenu", () -> showPolicyMenu());

        // Redrawn from scratch every pass, so the register of what is on
        // screen is too - a lever from the page before is not one to apply.
        policyLevers.clear();
        if (policyArea != null) {
            boolean known = false;
            for (String a : AREAS) known |= a.equals(policyArea);
            if (!known) policyArea = TAXES;
            String[] pages = pagesOf(policyArea);
            boolean page = false;
            for (String p : pages) page |= p.equals(policyPage);
            if (!page) policyPage = pages[0];
        }

        VBox page = widePage();
        if (policyArea == null) {
            hubPage(page);
        } else {
            switch (policyArea) {
                case WAGES -> floorPage(page);
                case MONEY -> {
                    if ("Currency reform".equals(policyPage)) reformPage(page);
                    else                                      ratePage(page);
                }
                case PROMISES -> {
                    switch (policyPage) {
                        case "Schools"     -> schoolsPage(page);
                        case "Subsidies"   -> subsidyPage(page);
                        case "Out of work" -> outOfWorkPage(page);
                        case "Health"      -> healthPage(page);
                        default            -> pensionPage(page);
                    }
                }
                default -> {
                    switch (policyPage) {
                        case "Profit", "Sales", "Wage", "Property" -> taxPage(page, policyPage);
                        default -> everythingPage(page);
                    }
                }
            }
        }

        VBox frame = new VBox(Palette.GAP, head());
        String[] pages = pagesOf(policyArea);
        // A strip of one tab is a tab that does nothing, so Wages has none.
        if (pages.length > 1) {
            frame.getChildren().add(chipStrip(pages, iconsOf(policyArea), policyPage, Palette.SIZE_LABEL, this::openPage));
        }
        frame.getChildren().add(vitals());
        frameOver(frame, page, tray());
    }

    /** The hub, at its top. */
    void openHub() {
        policyArea = null;
        dropProposal();
        ui.innerScrollAt.remove("showPolicyMenu:body");
        showPolicyMenu();
    }

    /** An area, on its first page or the one named, at its top (every door on this tab, and the shell's). */
    void open(String area, String page) {
        policyArea = area;
        policyPage = page == null ? pagesOf(area)[0] : page;
        dropProposal();
        ui.innerScrollAt.remove("showPolicyMenu:body");
        showPolicyMenu();
    }

    /** An area's first page: a chip in the head. */
    void openArea(String area) { open(area, null); }

    /** A page of the open area: a tab. A proposal is about the page it was made on, so it goes. */
    void openPage(String page) { open(policyArea == null ? TAXES : policyArea, page); }

    /** The fixed frame over a scrolling page and, when one is given, the tray under it: the page as tall as what is left between them (BankScreen's frame, with a foot). */
    private void frameOver(VBox frame, VBox page, Region tray) {
        frame.setMaxWidth(PAGE_WIDE);
        frame.setFillWidth(true);
        frame.setPadding(new javafx.geometry.Insets(0, 18, 4, 18));
        body = ui.scrolled(page, FRAME_CHROME);
        body.prefHeightProperty().unbind();
        javafx.beans.value.ObservableValue<?> trayHeight = tray == null ? frame.heightProperty() : tray.heightProperty();
        body.prefHeightProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                () -> Math.max(220, ui.menuScroller.getHeight()
                        - (frame.getHeight() > 0 ? frame.getHeight() + STAGE_REST : FRAME_CHROME)
                        - (tray == null ? 0 : (tray.getHeight() > 0 ? tray.getHeight() : TRAY_CHROME) + 10)),
                ui.menuScroller.heightProperty(), frame.heightProperty(), trayHeight));
        final javafx.scene.control.ScrollPane scroller = body;
        page.prefWidthProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                () -> Math.min(PAGE_WIDE, Math.max(320, scroller.getViewportBounds().getWidth())),
                scroller.viewportBoundsProperty()));
        ui.rootMenu.getChildren().addAll(frame, body);
        if (tray != null) {
            tray.setMaxWidth(PAGE_WIDE);
            ui.rootMenu.getChildren().add(tray);
        }
    }

    /** The tab's (i): the landing's lead, and the banner's line that every dial is a proposal (P1). */
    static final String LEAD_INFO = "Every number the city sets for itself, and what each one is doing this month. "
            + "Every dial is a proposal until it is applied: moving one stages a change, the page draws what it "
            + "would do against last month's books, and nothing reaches the model until Apply. The four cards open "
            + "the taxes, the wage floor, the price of money and the promises; under them, the levers that are "
            + "forcing somebody's hand.";

    /** Each area's (i): what it holds (the landing's row blurbs until 0.7.36). */
    static String areaInfo(String area) {
        return switch (area) {
            case WAGES -> "The floor under every wage in the city, including the city's own. Every other wage is a "
                    + "multiple of it, so moving it moves all of them - and it is why unemployment exists in this "
                    + "game at all: a surplus at the floor cannot be priced away, so it leaves.";
            case MONEY -> "The price of money, and the units it is counted in: the policy rate under every other "
                    + "rate in the city, the rule beside it, the central bank's holdings and its ceiling, and the "
                    + "currency reform.";
            case PROMISES -> "Pensions, EI, the clinic's price, school fees, and the sectors the city will not let "
                    + "fail: what the city has promised, what it collects for it and what it costs.";
            default -> "Each tax's own rate, and the moves off it by band and by sector. Profit, sales and wage tax "
                    + "each move off a base of their own; property is charged on what things are worth rather "
                    + "than on anything anybody earned.";
        };
    }

    /**
     * The head: "Policy" with the money blue's swatch, or the breadcrumb
     * "Policy › Taxes" with "Policy" a way back to the hub; the four areas
     * as chips at its right, the open one lit (the spec's D14: the
     * breadcrumb and the chips replace "All of policy").
     */
    HBox head() {
        FlowPane areas = chipStrip(AREAS, AREA_ICONS, policyArea == null ? "" : policyArea, Palette.SIZE_LABEL, this::openArea);
        if (policyArea == null) return pageHead("Policy", Palette.MONEY, null, null, LEAD_INFO, areas);
        return pageHead("Policy", Palette.MONEY, this::openHub, policyArea, areaInfo(policyArea), areas);
    }

    /* ------------------------------ the four figures ------------------------------ */

    /** One figure of the strip, worked out without drawing it: its label, its figure, its note, its colour, and where its click goes. */
    record Cell(String label, String value, String note, String tone, String where) { }

    /**
     * THE FOUR (pure: the probe reads them as the strip shows them). Each
     * figure's colour is its NEEDS YOU row's verdict and nothing else (the
     * spec's D5): TAX A MONTH THE BUDGET's, THE FLOOR WAGES', PROMISES
     * PENSIONS'; THE POLICY RATE is plain. THE FLOOR is in today's money
     * (B7: it printed the founding figure as a wage).
     */
    List<Cell> vitalCells() {
        TaxPolicy policy = policy();
        DebtManager market = ui.game.getDebtManager();
        LabourMarket labour = ui.game.getLabourMarket();
        PriceIndex px = ui.game.getPriceIndex();
        List<CityNeeds.Need> needs = CityNeeds.measure(ui.game, CityNeeds.PLAIN);
        List<Cell> out = new ArrayList<>();
        out.add(new Cell("TAX A MONTH", money(taxRaised()), ratesWords(policy),
                levelTone(needLevel(needs, CityNeeds.Kind.BUDGET)), "Taxes: where the money comes from"));
        double floor = labour.cashMinimumWage();
        double unskilled = labour.getWage(JobType.NO_DIPLOMA);
        out.add(new Cell("THE FLOOR", moneyFull(floor), "a month · " + (unskilled <= floor * (1 + LabourMarket.PINNED_TOLERANCE)
                        ? "the unskilled job is paid it" : "the unskilled job is paid " + moneyFull(unskilled)),
                levelTone(needLevel(needs, CityNeeds.Kind.WAGES)), "Wages: the ladder against the floor"));
        String savers = "savers " + ratePct(ui.game.getBank().depositRate());
        out.add(new Cell("THE POLICY RATE", pct2(market.getPolicyRate()), px.hasRate()
                        ? "the rule says " + pct2(market.advisedPolicyRate(px.inflation())) + " · " + savers : savers,
                Palette.TEXT_HEAD, "Money: the rate line, the rule and the central bank"));
        PolicyPreview.Promises p = PolicyPreview.promises(ui.game);
        out.add(new Cell("PROMISES", money(p.total()), p.pensionGap() > 0
                        ? money(p.pensionGap()) + " of it the pension gap" : "a month, out of the treasury",
                levelTone(needLevel(needs, CityNeeds.Kind.PENSIONS)), "Promises: pensions, EI, care, schools, subsidies"));
        return out;
    }

    /** The strip: the four as limit cells, each a door. */
    HBox vitals() {
        List<Cell> four = vitalCells();
        Runnable[] go = {() -> open(TAXES, POLICY_HOME), () -> open(WAGES, null),
                () -> open(MONEY, "The policy rate"), () -> open(PROMISES, "Pensions")};
        VBox[] cells = new VBox[four.size()];
        for (int i = 0; i < cells.length; i++) {
            Cell c = four.get(i);
            cells[i] = limitCell(c.label(), c.value(), c.note(), c.tone(), c.where(), go[i]);
        }
        return vitalsBar(cells);
    }

    /** The rates in words: "15.00% on income · 1.50% a year on property", or the three once they have parted (B2: two places, as Money.pct2() says). */
    static String ratesWords(TaxPolicy p) {
        String income = p.incomeRatesSplit()
                ? String.format("%.2f · %.2f · %.2f%% on income", p.getProfitTaxRate() * 100,
                        p.getSalesTaxRate() * 100, p.getWageTaxRate() * 100)
                : pct2(p.getIncomeTaxRate()) + " on income";
        return income + " · " + pct2(p.getPropertyTaxRate()) + " a year on property";
    }

    /** A NEEDS YOU row's level by its kind, in the list measured once a draw: 0 when it is not listed. */
    static int needLevel(List<CityNeeds.Need> needs, CityNeeds.Kind kind) {
        for (CityNeeds.Need n : needs) if (n.kind() == kind) return n.level();
        return 0;
    }

    /** A level's colour: plain, amber near the line, red past it. */
    static String levelTone(int level) {
        return level >= 2 ? Palette.BAD : level == 1 ? Palette.WARN : Palette.TEXT_HEAD;
    }

    /** The city's policy. */
    TaxPolicy policy() { return ui.game.getEconomyManager().getTaxPolicy(); }

    /** Every tax line the city collected last month - CityNeeds' own sum since 0.7.24, which NEEDS YOU's TREASURY line reads too. */
    double taxRaised() {
        return CityNeeds.taxRaised(ui.game);
    }

    /** Wage bands pinned to the floor with people spare in them - CityNeeds' count since 0.7.24, NEEDS YOU's WAGES line's. */
    int pinnedBands() {
        return CityNeeds.pinnedBands(ui.game);
    }

    /* =====================================================================
       THE STAGED SET.

       It was ONE proposal - a key and a value - and moving a second dial threw
       the first away. That was right while a tax page was one lever at a time;
       it is wrong now that a page is a whole tax, because a budget is not one
       rate moved in isolation. Jerus: "stage several and apply together".

       A LinkedHashMap, so the tray lists them in the order they were moved.
       Cleared on every page and area change for the same reason it always
       was: a proposal is about the page you are looking at, and one carried
       to another page is one nobody can see.
       ===================================================================== */
    final java.util.LinkedHashMap<String, Double> policyStaged =
            new java.util.LinkedHashMap<>();

    /**
     * Every dial DRAWN this pass, by key.
     *
     * The tray is drawn after the page, so by the time it needs to name a
     * staged change and know how to apply it, the lever that owns that key
     * has registered itself. That is what lets the tray apply a mixed batch -
     * the dial carries its own setter, so there is exactly one place that
     * knows how to set each rate on the city.
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
       assessed value is one in four. The ends line says which step it is.

       AND IT IS A CLASS (0.7.6), ui/Ladder: Jerus asked for it everywhere -
       "create an object or class of slider, and then whenever you need it you
       just call that class and plug in the specific sensitivity, max, min and
       steps". What is left here is the wiring to the staged set: ladderOf()
       registers a Lever for the tray and stages through propose();
       ownLadder() stages a dial that keeps its own Apply on its card.
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
     * A registered dial's Ladder, wired to the staged set: the lever goes in
     * the register the tray names and applies from, the thumb sits at what is
     * staged, and every move goes through propose(). Handed back unbuilt, so
     * a caller can grey it, mark it or say its step its own way first.
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
     * staging a no-op - otherwise the tray would list "20.00% to 20.00%" as a
     * pending change and Apply would offer to do nothing.
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
        TaxPolicy policy = policy();
        return (EVERY_TAX.equals(lever.key()) && policy.incomeRatesSplit())
                || (EVERY_SCHOOL.equals(lever.key()) && policy.tuitionScalesSplit());
    }

    /** What a lever reads today, in words: its current value, or "three rates" and "a price per school" for the every-tax and every-school levers once they have parted. */
    String nowReads(Lever lever) {
        if (!changesAtCurrent(lever)) return lever.read().apply(lever.current());
        return EVERY_SCHOOL.equals(lever.key()) ? "a price per school" : "three rates";
    }

    /* ----------------------- the staged set, through a copy ----------------------- */

    /**
     * What a staged key sets, on any policy: the one place a key is read as
     * a dial for the preview (0.7.36). The city's own setters are the
     * levers' (Lever.apply); this puts the same move through a COPY
     * (TaxPolicy.copy()), whose setters clamp it as the city's would, so
     * the previews are the model's reads of the copy - and it is keyed rather
     * than carried on the lever because a page draws a card's preview before
     * every lever on it has registered. A key that is not the policy's (the
     * city's share of tuition is Education's; the grant's basis and amount
     * go together, in stagedPolicy()) sets nothing here.
     */
    static void onCopy(TaxPolicy p, String key, double v) {
        if (key == null) return;
        if (EVERY_TAX.equals(key))            { p.setIncomeTaxRate(v); return; }
        if (baseKey("profit").equals(key))    { p.setProfitTaxRate(v); return; }
        if (baseKey("sales").equals(key))     { p.setSalesTaxRate(v); return; }
        if (baseKey("wage").equals(key))      { p.setWageTaxRate(v); return; }
        if ("property".equals(key))           { p.setPropertyTaxRate(v); return; }
        if ("farmland".equals(key))           { p.setFarmlandRelief(v); return; }
        if (key.startsWith("profit:"))        { p.setProfitOffset(key.substring(7), v); return; }
        if (key.startsWith("sales:"))         { p.setSalesOffset(key.substring(6), v); return; }
        if (key.startsWith("prop:"))          { p.setPropertyOffset(key.substring(5), v); return; }
        if (key.startsWith("wage:")) {
            for (WageBand b : WageBand.values()) if (b.name().equals(key.substring(5))) p.setWageOffset(b, v);
            return;
        }
        if (EVERY_SCHOOL.equals(key))         { p.setTuitionScale(v); return; }
        if (key.startsWith(EVERY_SCHOOL + ":")) {
            for (EducationType k : EducationType.values()) if (k.name().equals(key.substring(EVERY_SCHOOL.length() + 1))) p.setTuitionScaleOf(k, v);
            return;
        }
        switch (key) {
            case "loanRate"      -> p.setStudentLoanRate(v);
            case "contrib"       -> p.setContributionRate(v);
            case "pension"       -> p.setPensionReplacement(v);
            case "eiPremium"     -> p.setEiPremiumRate(v);
            case "eiBenefit"     -> p.setEiBenefitRate(v);
            case "healthFee"     -> p.setHealthFeeScale(v);
            case "healthPremium" -> p.setHealthPremiumRate(v);
            default -> { }
        }
    }

    /** The city's policy with everything staged put through a copy - every key in the order it was moved, and the grant's basis and amount together. */
    TaxPolicy stagedPolicy() { return stagedPolicyWith(null, 0); }

    /** ...and one key at a value of the caller's on top (a dial card's thumb, while it is dragged). */
    TaxPolicy stagedPolicyWith(String key, double value) {
        TaxPolicy live = policy();
        TaxPolicy copy = live.copy();
        for (Map.Entry<String, Double> e : policyStaged.entrySet()) {
            if (!e.getKey().equals(key)) onCopy(copy, e.getKey(), e.getValue());
        }
        if (key != null) onCopy(copy, key, value);
        boolean basis = isStaged("grantBasis") || "grantBasis".equals(key);
        boolean amount = isStaged("grantAmount") || "grantAmount".equals(key);
        if (basis || amount) {
            TaxPolicy.GrantBasis want = stagedBasis(live);
            double a = "grantAmount".equals(key) ? value : staged("grantAmount", want == live.getGrantBasis()
                    ? live.getGrantAmount() : ui.game.grantAmountAs(want));
            copy.setGrant(want, a);
        }
        return copy;
    }

    /** The city's policy with one key moved and nothing else: a dial with its own Apply previews its own move. */
    TaxPolicy with(String key, double value) {
        TaxPolicy copy = policy().copy();
        onCopy(copy, key, value);
        return copy;
    }

    /** The grant's basis as staged, else the city's. */
    TaxPolicy.GrantBasis stagedBasis(TaxPolicy live) {
        int ordinal = (int) Math.round(staged("grantBasis", live.getGrantBasis().ordinal()));
        return TaxPolicy.GrantBasis.values()[Math.max(0, Math.min(TaxPolicy.GrantBasis.values().length - 1, ordinal))];
    }

    /** The city's share of tuition as staged (Education's dial, not the policy's). */
    double stagedShare() { return staged("tuition", ui.game.getEducation().getTuitionSubsidy()); }

    /** THE BUDGET under everything staged: PolicyPreview's (M8). */
    PolicyPreview.Budget stagedBudget() {
        return PolicyPreview.budget(ui.game, stagedPolicy(), stagedShare());
    }

    /** THE BUDGET as an effect row, before and after a policy and a share of the caller's. */
    Effect budgetEffect(TaxPolicy after, double share) {
        PolicyPreview.Budget b = PolicyPreview.budget(ui.game, after, share);
        return Effect.of("THE BUDGET, a month", b.before(), b.after(), v -> signedTight(v, false))
                .delta(d -> signedTight(d, false)).info(BUDGET_INFO);
    }

    /** THE BUDGET's (i). */
    static final String BUDGET_INFO = "Last month's surplus or deficit, and what it would be with this change: every "
            + "line the change reaches, struck the same way at the city's dials and at the staged ones on this month's "
            + "books, and the difference added to the balance. The same way twice, so the difference is the answer even "
            + "where a line is not last month's to the dollar - the bank's tax is next month's, charged in arrears, and "
            + "the sales tax is scaled.";

    /* -------------------------------- the tray -------------------------------- */

    /** P20, the tray's (i). */
    static final String TRAY_INFO = "Everything staged on this page, applied together by one press. THE BUDGET is "
            + "struck the same way twice against this month's books - the profit each sector made, the value each added, "
            + "the payroll each paid and what each is assessed at - so the DIFFERENCE is the answer even where neither "
            + "figure matches last month's actual revenue to the dollar. Nothing here knows that a new rate changes what "
            + "anybody does next month - a business taxed harder earns less, and that arrives in its own books rather "
            + "than in this preview. A staged change goes when the page does.";

    /**
     * The staged tray (the spec's 3.0): on the pages whose dials batch - the
     * taxes' and the schools' - once anything is staged: a chip a change
     * that comes out with its ×, THE BUDGET before and after the set, and
     * Apply and Discard. Null with nothing to show.
     */
    Region tray() {
        if (policyStaged.isEmpty()) return null;
        boolean batches = TAXES.equals(policyArea) || (PROMISES.equals(policyArea) && "Schools".equals(policyPage));
        if (!batches) return null;
        List<Node> chips = new ArrayList<>();
        for (TrayItem item : trayItems()) {
            chips.add(stagedChip(item.words(), () -> {
                unstage(item.key());
                if ("grantBasis".equals(item.key())) unstage("grantAmount");
                showPolicyMenu();
            }));
        }
        if (chips.isEmpty()) return null;
        int count = chips.size();
        Pieces.ActionButton apply = actionButton(Icons.TICK, Palette.MONEY, ACTION_INLINE,
                new Press(Look.GO, count == 1 ? "Apply it" : "Apply all " + count, null),
                () -> { applyStaged(); showPolicyMenu(); });
        Pieces.ActionButton discard = actionButton(Icons.CLOSE, Palette.TEXT_LABEL, ACTION_INLINE,
                new Press(Look.CHOOSE, "Discard", null), () -> { dropProposal(); showPolicyMenu(); });
        PolicyPreview.Budget b = stagedBudget();
        Effect budget = Effect.of("THE BUDGET, a month", b.before(), b.after(), v -> signedTight(v, false))
                .delta(d -> signedTight(d, false)).info(BUDGET_INFO);
        return stagedTray(chips, budget, apply, discard, TRAY_INFO);
    }

    /** One staged change as the tray lists it: its key, and "The wage rate 15.00% → 16.00%". */
    record TrayItem(String key, String words) { }

    /** Each staged change the tray lists, in the order moved (pure: the probe reads them). */
    List<TrayItem> trayItems() {
        List<TrayItem> out = new ArrayList<>();
        for (Map.Entry<String, Double> e : policyStaged.entrySet()) {
            Lever lever = policyLevers.get(e.getKey());
            if (lever == null) continue;   // not on this page; cannot be described
            out.add(new TrayItem(e.getKey(), lever.name() + " " + nowReads(lever) + " → " + lever.read().apply(e.getValue())));
        }
        return out;
    }

    /** Each staged value through its own dial's setter, then the set is empty. */
    void applyStaged() {
        for (Map.Entry<String, Double> entry : new ArrayList<>(policyStaged.entrySet())) {
            Lever lever = policyLevers.get(entry.getKey());
            if (lever != null) lever.apply().accept(entry.getValue());
        }
        dropProposal();
    }

    /* ----------------------- a dial that keeps its own Apply ----------------------- */

    /**
     * The lever itself: a Ladder (0.7.6) that STAGES a change rather than
     * making one, for a dial that keeps its own Apply - the wage floor, the
     * policy rate, the pension's two, EI's two, the clinic's two, and the
     * fare on Infrastructure (on its own dial card since 0.7.38). Handed
     * back unbuilt for a dial card to mark, track and build.
     *
     * Dropping the thumb back where it started clears the proposal rather than
     * staging a change of nothing, which is what makes the slider its own
     * cancel button - ITS OWN, since 2026-09-21: it unstages its key, as
     * propose() does for a registered lever, where it used to drop the whole
     * set.
     *
     * THE REDRAW IS THE CALLER'S SCREEN, not this one. The transit fare is a
     * lever on the Infrastructure tab drawn with this same dial, and until
     * 2026-09-18 letting go of it redrew the policy screen - the player was
     * carried to a tab the dial is not on. ui.redraw() draws whatever screen
     * registered itself last, which on this tab is showPolicyMenu().
     */
    Ladder ownLadder(String key, double current, double min, double max,
                     double step, java.util.function.DoubleFunction<String> label) {
        return Ladder.of(min, max, step, label)
                .current(current)
                .showing(staged(key, current))
                .stages(v -> {
                    if (moved(v, current, step)) stage(key, v); else unstage(key);
                    ui.redraw();   // whichever screen the dial is on - the fare dial lives on Infrastructure
                });
    }

    /**
     * Apply, or put it back (0.7.34's action button since 0.7.36: a GO in
     * the money blue that says what it sets, and "Leave it as it is" beside it). The
     * fare on Infrastructure has it too.
     */
    HBox applyBar(String label, Runnable apply) {
        Pieces.ActionButton go = actionButton(Icons.TICK, Palette.MONEY, ACTION_INLINE,
                new Press(Look.GO, label, null), () -> {
                    apply.run();
                    dropProposal();
                    ui.redraw();
                });
        go.setMinWidth(240);
        Pieces.ActionButton no = actionButton(Icons.CLOSE, Palette.TEXT_LABEL, ACTION_INLINE,
                new Press(Look.CHOOSE, "Leave it as it is", null), () -> { dropProposal(); ui.redraw(); });
        no.setMinWidth(170);
        HBox bar = new HBox(8, go, no);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-padding: 6 0 2 0;");
        return bar;
    }

    /** The foot of a card whose dial keeps its own Apply: the bar once its key is staged, nothing before. */
    List<Node> applyFoot(String key, String label, Runnable apply) {
        return isStaged(key) ? List.of(applyBar(label, apply)) : List.of();
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

    /** A card's head: its icon in a square tinted in its colour, its title in capitals, an (i) when `info` is not null, and at its right `right` (null: nothing) - BankScreen's. */
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

    /** A plain line of words in a card, wrapping, with its whole behind an (i) when `whole` is not null. */
    static Node line(String text, String whole) {
        if (whole == null) return words(text, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL);
        return infoLine(text, whole, true, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL, 1100);
    }

    /** ...muted. */
    static Label muted(String text) { return words(text, Palette.SIZE_LABEL, Palette.TEXT_MUTED); }

    /** A heading's quiet words at its right, never cut. */
    static Label quiet(String text) {
        Label l = hint(text);
        l.setMinWidth(Region.USE_PREF_SIZE);
        return l;
    }

    /** A row of chips, wrapping. */
    static FlowPane chips(Node... cs) {
        FlowPane row = new FlowPane(8, 6);
        for (Node n : cs) if (n != null) row.getChildren().add(n);
        return row;
    }

    /** A statement column, for a fold: the old rows at their old width. */
    static VBox column(Node... rows) {
        VBox c = new VBox(0);
        for (Node n : rows) if (n != null) c.getChildren().add(n);
        c.setMaxWidth(STATEMENT);
        return c;
    }

    /** A fold kept on this screen: "details ▸ caption". */
    VBox fold(String key, String caption, java.util.function.Supplier<Node> inside) {
        return details(key, caption, openLines, this::showPolicyMenu, inside);
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

    /** Two cards side by side, the first `share` of the width (the floor's dial and the city's payroll). */
    static HBox split(Region first, Region second, double share) {
        HBox both = new HBox(TILE_GAP, first, second);
        first.prefWidthProperty().bind(both.widthProperty().subtract(TILE_GAP).multiply(share));
        second.prefWidthProperty().bind(both.widthProperty().subtract(TILE_GAP).multiply(1 - share));
        first.setMinWidth(0);
        second.setMinWidth(0);
        HBox.setHgrow(second, Priority.ALWAYS);
        both.setFillHeight(true);
        return both;
    }

    /** The last `n` months of a History series, for a sparkline - BankScreen's. */
    Region spark(double[] all, int n, String colour, double width, double height) {
        List<Integer> months = ui.game.getHistorySave().getMonth();
        int from = Math.max(0, all.length - n);
        double[] part = java.util.Arrays.copyOfRange(all, from, all.length);
        List<Integer> when = months.size() == all.length ? months.subList(from, all.length) : null;
        return sparkline(part, when, colour, width, height);
    }

    /** A sector by its key, or null. */
    Sector sector(String key) { return ui.game.getSectors().byKey(key); }

    /** A signed share in points: "+1.00 pts". */
    static String ptsMove(double d) { return pts(d); }

    /** Money with its sign, as a move: "+$2.5M", "−$1.2M", "$0". */
    static String moneyMove(double d) { return signedTight(d, false); }

    /** ...every digit, for one household's or one person's money: "+$173", "−$41", "$0" under half a dollar. */
    static String moneyMoveFull(double d) {
        if (Math.abs(d) * 1000 < .5) return "$0";
        return (d < 0 ? "\u2212" : "+") + moneyFull(Math.abs(d));
    }

    /** Money that can be below nothing - a sector in refund, a surplus where a cost was - with a true minus: "$2.5M", "−$365k". */
    static String amount(double v) {
        String shown = money(Math.abs(v));
        return (v < 0 && !"$0".equals(shown) ? "\u2212" : "") + shown;
    }

    /** A rate's move in points: "+0.25 points". */
    static String rateMove(double d) { return points(d); }

    /** A percentage to none: "45%". */
    static String pct0(double share) { return String.format("%.0f%%", unsigned0(share * 100, 0)); }

    /** "x1.00". */
    static String times(double scale) { return String.format("x%.2f", scale); }

    /* =====================================================================
       THE HUB (0.7.36; the landing's four rows until then)

       What has the city set, what does each part raise or cost, and is any
       of it biting? Four AREA CARDS - Taxes, Wages, Money, Promises - each a
       door with its figure, a small picture, a line and a foot (on Taxes,
       the last tax decision); then WHAT IS BITING, a card a lever forcing
       somebody's hand, each with a door to where it is undone (or one card
       with a tick when nothing is); then the decisions lately made, a chip
       each.
       ===================================================================== */

    void hubPage(VBox page) {
        page.getChildren().add(row(taxesCard(), wagesCard(), moneyCard(), promisesCard()));

        page.getChildren().add(sectionHead("WHAT IS BITING", null, quiet("the dials that are forcing someone's hand")));
        List<Flag> flags = policyFlags();
        if (flags.isEmpty()) {
            page.getChildren().add(ringCard(null, 1, Palette.GOOD, "Nothing is binding", NOTHING_BINDING_INFO,
                    firstWords(NOTHING_BINDING_INFO), null, Palette.TEXT_MUTED, 56, false, null, null, null));
        } else {
            GridPane grid = equalColumns(2, TILE_GAP);
            int month = ui.game.getMonth();
            for (int i = 0; i < flags.size(); i++) {
                Flag f = flags.get(i);
                int seen = flagSeen.computeIfAbsent(f.heading(), k -> month);
                VBox c = flagCard(f, seen == month);
                GridPane.setFillHeight(c, true);
                c.setMaxHeight(Double.MAX_VALUE);
                grid.add(c, i % 2, i / 2);
            }
            page.getChildren().add(grid);
        }
        // A flag that stopped biting is forgotten, so it is NEW again if it comes back.
        java.util.Set<String> now = new HashSet<>();
        for (Flag f : flags) now.add(f.heading());
        flagSeen.keySet().retainAll(now);

        List<DecisionLog.Entry> recent = recentDecisions(5);
        page.getChildren().add(sectionHead("RECENT DECISIONS", null,
                doorPill("History", Icons.REPORTS, Palette.ACCENT, () -> ui.historyScreen.openOn("revenue", "surplus"))));
        if (recent.isEmpty()) {
            page.getChildren().add(muted("No tax, promise, central bank or currency decision yet: every dial is as the city was founded."));
        } else {
            FlowPane row = new FlowPane(8, 6);
            for (DecisionLog.Entry e : recent) row.getChildren().add(chip(decisionWords(e), Palette.TEXT_LABEL));
            page.getChildren().add(row);
        }
    }

    /** P2: what the hub says when nothing is binding. */
    static final String NOTHING_BINDING_INFO = "Nothing is binding. Every lever has room to move, no sector is shut "
            + "out of credit, and no wage has run out of room to fall.";

    /** The last `n` decisions of the kinds this tab makes - taxes, promises, the central bank, the currency - newest first. */
    List<DecisionLog.Entry> recentDecisions(int n) {
        List<DecisionLog.Entry> all = ui.game.getDecisions().entries();
        List<DecisionLog.Entry> out = new ArrayList<>();
        for (int i = all.size() - 1; i >= 0 && out.size() < n; i--) {
            DecisionLog.Entry e = all.get(i);
            if (isPolicyKind(e.kind())) out.add(e);
        }
        return out;
    }

    /** The last decision of one kind, or null. */
    DecisionLog.Entry lastDecision(String kind) {
        List<DecisionLog.Entry> all = ui.game.getDecisions().entries();
        for (int i = all.size() - 1; i >= 0; i--) if (kind.equals(all.get(i).kind())) return all.get(i);
        return null;
    }

    static boolean isPolicyKind(String kind) {
        return DecisionLog.TAX.equals(kind) || DecisionLog.PROMISE.equals(kind)
                || DecisionLog.CENTRAL_BANK.equals(kind) || DecisionLog.CURRENCY.equals(kind);
    }

    /** A decision as a chip: "m1,204 · Taxes to 15%". */
    static String decisionWords(DecisionLog.Entry e) {
        return String.format("m%,d · %s", e.month(), e.label());
    }

    /* ------------------------------ the area cards ------------------------------ */

    /** One area card: its icon and title with "›", its figure, a small picture, one line and a foot - the whole a door. */
    VBox areaCard(String svg, String title, String figure, Node picture, String line, String foot, Runnable go) {
        Label arrow = new Label("›");
        arrow.setStyle(Palette.strong(Palette.SIZE_HEADING + 2, Palette.TEXT_MUTED));
        VBox c = card(cardHead(svg, Palette.MONEY, title, null, arrow),
                figure(figure, Palette.SIZE_LEAD, Palette.TEXT_HEAD), picture,
                words(line, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL),
                foot == null ? null : muted(foot));
        return doorCard(c, go);
    }

    /** The money ramp the four taxes are drawn in, profit to property (the spec's 3.1): darker to lighter, then the rest. */
    static final String[] TAX_RAMP = {Palette.MONEY_DARK, Palette.MONEY, Palette.MONEY_LIGHT, Palette.RAMP_REST};

    /** The four taxes last month, profit · sales · wage · property, as NationalAccounts booked them. */
    double[] taxesBooked() {
        NationalAccounts na = ui.game.getEconomyManager().getNationalAccounts();
        return new double[] {na.getTaxBusiness() + na.getTaxIndustrial(), na.getTaxSales(), na.getTaxWage(), na.getPropertyTax()};
    }

    /** The four taxes by name, profit to property: their cards, the bar's parts and their pages. */
    static final String[] TAX_NAMES = {"Profit", "Sales", "Wage", "Property"};

    VBox taxesCard() {
        double[] t = taxesBooked();
        List<Slice> parts = new ArrayList<>();
        for (int i = 0; i < 4; i++) parts.add(new Slice(TAX_NAMES[i] + " tax", t[i], TAX_RAMP[i]));
        DecisionLog.Entry last = lastDecision(DecisionLog.TAX);
        return areaCard(Icons.COIN, "TAXES", money(taxRaised()) + " a month", sliceBar(parts),
                ratesWords(policy()), last == null ? "rates as founded" : "last moved " + decisionWords(last),
                () -> open(TAXES, POLICY_HOME));
    }

    VBox wagesCard() {
        LabourMarket labour = ui.game.getLabourMarket();
        double floor = labour.cashMinimumWage();
        int pinned = pinnedBands();
        double payroll = ui.game.getEducation().getPayroll() + ui.game.getHealthcare().getPayroll();
        return areaCard(Icons.STAFF, "WAGES", moneyFull(floor) + " a month",
                bulletBar(labour.getWage(JobType.NO_DIPLOMA), floor, Palette.MONEY, 0,
                        "the unskilled wage, " + moneyFull(labour.getWage(JobType.NO_DIPLOMA)) + ", against the floor's tick"),
                "every wage is a multiple of it · " + (pinned == 0 ? "no band is pinned"
                        : pinned + (pinned == 1 ? " band is" : " bands are") + " pinned to it"),
                "the city's own payroll " + money(payroll), () -> open(WAGES, null));
    }

    VBox moneyCard() {
        DebtManager market = ui.game.getDebtManager();
        PriceIndex px = ui.game.getPriceIndex();
        double dial = market.getPolicyRate(), city = market.getRate(), world = DebtManager.WORLD_BASE_RATE;
        double rule = px.hasRate() ? market.advisedPolicyRate(px.inflation()) : Double.NaN;
        double top = Math.max(.03, 1.25 * Math.max(Math.max(dial, city), Math.max(world, Double.isFinite(rule) ? rule : 0)));
        List<Tick> ticks = new ArrayList<>();
        ticks.add(new Tick(world, Palette.TEXT_SPENT, 2, null, "the world's rate " + pct2(world)));
        ticks.add(new Tick(city, Palette.TEXT_LABEL, 2, null, "the city borrows at " + pct2(city)));
        if (Double.isFinite(rule)) ticks.add(new Tick(rule, Palette.TEXT_MUTED, 2, null, "the rule says " + pct2(rule)));
        ticks.add(new Tick(dial, Palette.MONEY, 3, null, "the dial " + pct2(dial)));
        VBox picture = new VBox(3, segmentBar(List.of(), top, ticks, 0, 6),
                muted("dial " + pct2(dial) + (Double.isFinite(rule) ? " · rule " + pct2(rule) : "")
                        + " · city " + pct2(city) + " · world " + pct2(world)));
        String line = px.hasRate()
                ? "the rule says " + pct2(rule) + " · inflation " + signedPct1(px.inflation()) + " against "
                  + DebtManager.targetWords(market.getInflationTarget())
                : "no year of prices yet, so the rule has nothing to say";
        return areaCard(Icons.BANK, "MONEY", pct2(dial), picture, line,
                market.isAutopilot() ? "in the rule's hand" : "in your hand", () -> open(MONEY, "The policy rate"));
    }

    VBox promisesCard() {
        PolicyPreview.Promises p = PolicyPreview.promises(ui.game);
        EconomyManager em = ui.game.getEconomyManager();
        List<Slice> parts = List.of(
                new Slice("The pension gap", p.pensionGap(), Palette.MONEY_DARK),
                new Slice("EI past its premiums", p.eiPastPremiums(), Palette.MONEY),
                new Slice("Sectors kept alive", p.subsidies(), Palette.MONEY_LIGHT),
                new Slice("Students' grants", p.grants(), Palette.RAMP_REST));
        int protectedCount = 0;
        for (Sector s : ui.game.getSectors().all()) if (ui.game.isAutoSubsidised(s)) protectedCount++;
        return areaCard(Icons.POPULATION, "PROMISES", money(p.total()) + " a month", sliceBar(parts),
                em.getPensionsPaid() > 0 ? "contributions cover " + pct0(em.getPensionCoverage()) + " of pensions"
                        : "no pension is being paid",
                protectedCount == 0 ? "no sector protected" : protectedCount + (protectedCount == 1 ? " sector" : " sectors") + " protected",
                () -> open(PROMISES, "Pensions"));
    }

    /** Slices as one bar filling its card, each part's name and money its tooltip (a part below nothing draws nothing). */
    static SegmentBar sliceBar(List<Slice> slices) {
        List<Segment> parts = new ArrayList<>();
        for (Slice s : slices) {
            parts.add(new Segment(Math.max(0, s.amount()), s.colour(), false, null, null,
                    s.name() + "\n" + money(s.amount()), null));
        }
        return segmentBar(parts, 0, List.of(), 0, 12);
    }

    /** An area's icon, as its chip has it. */
    static String areaIcon(String area) {
        for (int i = 0; i < AREAS.length; i++) if (AREAS[i].equals(area)) return AREA_ICONS[i];
        return Icons.POLICY;
    }

    /** Inflation as a signed share to one place, with a true minus: "+0.2%". */
    static String signedPct1(double v) {
        double shown = unsigned0(v * 100, 1);
        return (shown < 0 ? "−" : "+") + String.format("%.1f%%", Math.abs(shown));
    }

    /* ------------------------------ what is biting ------------------------------ */

    /**
     * A lever that is forcing something: its colour (a verdict), its heading
     * and what it means, and where it is undone - the area and page its door
     * opens, and the door's words.
     */
    record Flag(String tone, String heading, String body, String area, String page, String door) { }

    /**
     * The levers that are currently forcing something.
     *
     * Never a lever that is merely set to something, only one whose setting
     * is having an effect somebody would want to know about. Jerus asked for
     * flags on the landing rather than a second summary: a lever that is
     * merely set is not news, and a lever that is forcing somebody's hand is.
     * Every one of these is a consequence the player can undo from this tab.
     */
    List<Flag> policyFlags() {

        List<Flag> out = new ArrayList<>();

        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        LabourMarket labour = ui.game.getLabourMarket();
        PopulationManager people = ui.game.getPopulationManager();
        Education schools = ui.game.getEducation();
        DebtManager market = ui.game.getDebtManager();
        PriceIndex px = ui.game.getPriceIndex();

        /* --------------------------- the wage floor ---------------------------
         * isPinned() as the model has it (the spec's D7: until B8 is decided,
         * the screen does not disagree with the model).
         */
        for (WageBand band : WageBand.values()) {
            double surplus = people.surplusInBand(band);
            if (labour.isPinned(band) && surplus > 0) {
                out.add(new Flag(Palette.WARN, band.label() + " cannot get any cheaper",
                        String.format("%,.0f more of them than there is work, and their wage "
                        + "is already at the floor. They leave the city instead of taking "
                        + "less.", surplus), WAGES, "The floor", "The floor"));
            }
        }

        /* ---------------------------- who is shut out ---------------------------- */
        BusinessDebtManager credit = em.getBusinessDebtManager();
        for (String sector : Sectors.KEYS) {
            if (credit.isBorrowingBlocked(sector)) {
                out.add(new Flag(Palette.BAD, sector + " cannot borrow",
                        credit.getBlockedMonths(sector) + " more months of it. A sector that "
                        + "went under cannot borrow to build, and a subsidy is the only thing on "
                        + "this tab that reaches it.", PROMISES, "Subsidies", "Subsidies"));
            }
        }

        /* ------------------------------- tuition ------------------------------- */
        int shut = 0;
        String worst = null;
        for (EducationType type : EducationType.values()) {
            if (type == EducationType.NONE) continue;
            double burden = PolicyPreview.schoolBurden(ui.game, type, schools.getTuitionScaleOf(type),
                    schools.getTuitionSubsidy());
            if (burden >= Education.MAX_BURDEN) {
                shut++;
                if (worst == null) worst = type.getLabel();
            }
        }
        if (shut > 0) {
            out.add(new Flag(Palette.BAD,
                    shut == 1 ? "Nobody can afford " + worst
                              : shut + " courses cost more than anybody can carry",
                    String.format("At %.0f%% of a month's wage nobody enrols at all, and the "
                    + "city's share of tuition is %.0f%%. The buildings can be there and "
                    + "produce nobody.",
                    Education.MAX_BURDEN * 100, schools.getTuitionSubsidy() * 100), PROMISES, "Schools", "Schools"));
        }

        /* ------------------------------- pensions ------------------------------- */
        double coverage = em.getPensionCoverage();
        if (coverage < .5 && em.getPensionsPaid() > 0) {
            out.add(new Flag(Palette.WARN, "The pension is mostly general revenue",
                    String.format("Workers' contributions cover %.0f%% of what seniors are "
                    + "paid. The other %s a month comes out of the same pot as everything "
                    + "else.", coverage * 100, money(em.getPensionShortfall())), PROMISES, "Pensions", "Pensions"));
        }

        /* ------------------------------ subsidies ------------------------------ */
        int protectedCount = 0;
        for (Sector sector : ui.game.getSectors().all()) {
            if (ui.game.isAutoSubsidised(sector)) protectedCount++;
        }
        if (protectedCount > 0 && ui.game.getTotalSubsidyPaid() > 0) {
            out.add(new Flag(Palette.WARN,
                    protectedCount + " sector" + (protectedCount == 1 ? " is" : "s are")
                            + " being kept alive",
                    money(ui.game.getTotalSubsidyPaid()) + " last month. A protected sector "
                    + "never sells its capacity, which is the point, and it never has to "
                    + "fix itself either.", PROMISES, "Subsidies", "Subsidies"));
        }

        /* --------------------------- the price of money --------------------------- */
        if (px.hasRate()) {
            double advised = market.advisedPolicyRate(px.inflation());
            double rule = market.ruleRate(px.inflation());
            if (Math.abs(advised - market.getPolicyRate()) >= .01) {
                // The rule's own figure, and the dial's stop when that is
                // what bounds it - see DebtManager.ruleRate() (2026-09-21).
                out.add(new Flag(Palette.ACCENT, "The rule disagrees with the dial",
                        String.format("The policy rate is %.2f%% and the rule would set it "
                        + "at %.2f%%%s. Inflation is running at %+.1f%% against a %s "
                        + "target.", market.getPolicyRate() * 100, rule * 100,
                        Math.abs(rule - advised) > 1e-9
                                ? String.format(" - the dial stops at %.2f%%", advised * 100) : "",
                        px.inflation() * 100, DebtManager.targetWords(market.getInflationTarget())),
                        MONEY, "The policy rate", "The policy rate"));
            }
        }

        /* ---------------------------- the money itself ---------------------------- */
        if (ui.game.canReformCurrency()) {
            out.add(new Flag(Palette.ACCENT, "The money could be reformed",
                    String.format("Prices are %.1f times what they were at founding. A "
                    + "reform restates every number in the city at once and changes "
                    + "nothing else.", ui.game.getPriceIndex().getIndex()), MONEY, "Currency reform", "Currency reform"));
        }

        /* ------------------------------ at the stops ------------------------------
         * Each income tax at its own stop since 0.7.4, named: one, two, or
         * all three together.
         */
        List<String> atStop = new ArrayList<>();
        if (policy.getProfitTaxRate() >= TaxPolicy.MAX_INCOME_TAX - 1e-9) atStop.add("profit");
        if (policy.getSalesTaxRate()  >= TaxPolicy.MAX_INCOME_TAX - 1e-9) atStop.add("sales");
        if (policy.getWageTaxRate()   >= TaxPolicy.MAX_INCOME_TAX - 1e-9) atStop.add("wage");
        if (!atStop.isEmpty()) {
            String which = atStop.size() == 3 ? "Every income tax is"
                    : atStop.size() == 2 ? capitalised(atStop.get(0)) + " and " + atStop.get(1) + " tax are"
                    : capitalised(atStop.get(0)) + " tax is";
            out.add(new Flag(Palette.WARN,
                    which + " at " + (atStop.size() == 1 ? "its" : "their") + " legal maximum",
                    String.format("%.0f%%. There is no more revenue to be had from %s, whatever "
                    + "the budget says.", TaxPolicy.MAX_INCOME_TAX * 100,
                    atStop.size() == 1 ? "this lever" : "these levers"),
                    TAXES, atStop.size() == 1 ? capitalised(atStop.get(0)) : POLICY_HOME,
                    atStop.size() == 1 ? capitalised(atStop.get(0)) + " tax" : "Taxes"));
        }

        return out;
    }

    /** "sales" to "Sales", for a flag that opens with a tax's name. */
    static String capitalised(String word) {
        return word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    /** A biting lever as a card: its alert in its verdict's colour, its heading (NEW the month it is first seen), its first sentence with the rest behind an (i), and a door to where it is undone. */
    VBox flagCard(Flag f, boolean isNew) {
        Label t = new Label(f.heading());
        t.setWrapText(true);
        t.setMinWidth(0);
        t.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        HBox head = new HBox(Palette.GAP, iconSquare(Icons.ALERT, f.tone(), 28, 15), t);
        if (isNew) head.getChildren().add(tag("NEW", Palette.BUILDING));
        head.setAlignment(Pos.CENTER_LEFT);
        VBox c = new VBox(8, head, noteLine(firstWords(f.body()), f.body(), 560),
                doorPill(f.door(), areaIcon(f.area()), Palette.MONEY, () -> open(f.area(), f.page())));
        c.setMaxWidth(Double.MAX_VALUE);
        c.setStyle("-fx-padding: 12 14 12 14; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + f.tone() + " "
                + Palette.EDGE + " " + Palette.EDGE + " " + Palette.EDGE + "; -fx-border-width: 3 1 1 1;");
        return c;
    }

    /* =====================================================================
       TAXES (0.7.36; THE FOUR TAXES until then)

       The area used to be split by where a modifier LIVED - "the two rates",
       "by wage band", "by sector" - and that is a fact about the code rather
       than about the money. It is split by which tax it is: Everything, and
       the four. And each of the four has its own rate (0.7.4): profit, sales
       and wage all moved off ONE city rate until Jerus asked "what about just
       increasing sale tax for all at the same time? ... i want to be able to
       do that for every type of tax, even wage tax"; his old city rate is the
       Everything page's "Every tax at once".

       Everything answers where the tax comes from and what the staged rates
       would raise: THE TAX TAKE as one bar of the four (and a second, at the
       staged rates, with what moved as a ghost), a card a tax with its ten
       years, and the dial that sets the three income taxes together. Each
       tax's page answers who pays it, at what rate, and what a move would do
       to each payer and to the take: its base on a dial card, the payers
       ranked by what they paid - a row opens into its own move - and the old
       table behind "details".
       ===================================================================== */

    /** What every preview on the tab owes the player, in one sentence (P21, the one every preview ended with until 0.7.36): behind each card's (i), once. */
    static final String CAVEAT = "Nothing here knows that the new rate changes what anybody does next month - a "
            + "business taxed harder earns less, and that arrives in its own books rather than in this preview.";

    /** The caveat's line on a card. */
    static final String CAVEAT_LINE = "Struck on this month's books; behaviour is not projected.";

    /** The policy a card previews at a thumb's value: everything staged, with the card's key at `v` - or exactly what is staged while the thumb rests where nothing of its own is staged (a parted every-tax dial at its current would otherwise preview setting the three to one). */
    TaxPolicy previewFor(String key, double v, double current, double step) {
        if (!isStaged(key) && !moved(v, current, step)) return stagedPolicy();
        return stagedPolicyWith(key, v);
    }

    /* ------------------------------ EVERYTHING ------------------------------ */

    /** P3, THE TAX TAKE's (i). */
    static final String TAKE_INFO = "Last month, by tax. Each has a rate of its own, and three of the four a "
            + "move off it by sector or by band; the fourth is charged on what things "
            + "are worth rather than on anything anybody earned. Under it, once anything is staged, the same four "
            + "at the staged rates: what moved is a ghost, with its move over it.";

    void everythingPage(VBox page) {
        TaxPolicy live = policy();
        NationalAccounts na = ui.game.getEconomyManager().getNationalAccounts();
        double[] booked = taxesBooked();
        double all = 0;
        for (double v : booked) all += v;

        double annual = annualGdp(na);
        String share = annual > 0 ? String.format("%.1f%% %s", all * 12 / annual * 100, ofAnnualGdp(na))
                                  : "no output to compare";
        page.getChildren().add(sectionHead("THE TAX TAKE", TAKE_INFO, quiet(money(all) + " last month · " + share)));
        page.getChildren().add(takeCard(booked, live));

        double[][] history = taxHistory();
        boolean[] stop = {live.getProfitTaxRate() >= TaxPolicy.MAX_INCOME_TAX - 1e-9,
                live.getSalesTaxRate() >= TaxPolicy.MAX_INCOME_TAX - 1e-9,
                live.getWageTaxRate() >= TaxPolicy.MAX_INCOME_TAX - 1e-9, false};
        String[] rates = {pct2(live.getProfitTaxRate()), pct2(live.getSalesTaxRate()), pct2(live.getWageTaxRate()),
                pct2(live.getPropertyTaxRate()) + " a year"};
        String[] infos = {PROFIT_INFO + " " + LOSS_INFO, SALES_INFO, WAGE_INFO, PROPERTY_INFO};
        Node[] cards = new Node[4];
        for (int i = 0; i < 4; i++) cards[i] = taxCard(i, booked[i], all, rates[i], history[i], stop[i], infos[i]);
        page.getChildren().add(row(cards));

        page.getChildren().add(everyTaxCard(live));
    }

    /** THE TAX TAKE: last month's four as one bar, a click on a part its page; and once anything is staged, the four at the staged rates, a moved part a ghost with its move over it. */
    VBox takeCard(double[] booked, TaxPolicy live) {
        boolean any = !policyStaged.isEmpty();
        PolicyPreview.TaxTake now = PolicyPreview.taxTake(ui.game, live);
        PolicyPreview.TaxTake then = PolicyPreview.taxTake(ui.game, stagedPolicy());
        double[] a = {now.profitTotal(), now.salesTotal(), now.wageTotal(), now.propertyTotal()};
        double[] b = {then.profitTotal(), then.salesTotal(), then.wageTotal(), then.propertyTotal()};
        double all = 0, staged = 0;
        for (int i = 0; i < 4; i++) { all += Math.max(0, booked[i]); staged += Math.max(0, b[i]); }
        double scale = Math.max(all, any ? staged : 0);

        List<Segment> last = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final String page = TAX_NAMES[i];
            last.add(new Segment(Math.max(0, booked[i]), TAX_RAMP[i], false, null, null,
                    TAX_NAMES[i] + " tax " + money(booked[i]) + " · open its page", () -> open(TAXES, page)));
        }
        VBox c = card(muted("last month"), segmentBar(last, scale, List.of(), 0, 18));
        if (any) {
            List<Segment> next = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                boolean moved = !money(a[i]).equals(money(b[i]));
                next.add(new Segment(Math.max(0, b[i]), TAX_RAMP[i], moved, null, moved ? moneyMove(b[i] - a[i]) : null,
                        TAX_NAMES[i] + " tax at the staged rates " + money(b[i])
                                + (moved ? " (" + moneyMove(b[i] - a[i]) + ")" : ""), null));
            }
            c.getChildren().addAll(muted("at the staged rates"), segmentBar(next, scale, List.of(), 0, 18));
        }
        FlowPane key = new FlowPane(16, 4);
        for (int i = 0; i < 4; i++) {
            key.getChildren().add(keySwatch(TAX_RAMP[i], TAX_NAMES[i] + " " + money(booked[i])
                    + (all > 0 ? " · " + pct0(booked[i] / all) : "")));
        }
        c.getChildren().add(key);
        return c;
    }

    /** Ten years of each tax from City History, profit · sales · wage · property; the profit line is the business and the food industry together, as the take's first part is. */
    double[][] taxHistory() {
        HistorySave h = ui.game.getHistorySave();
        double[] business = h.aligned("taxBusiness"), industrial = h.aligned("taxIndustrial");
        double[] profit = new double[business.length];
        for (int i = 0; i < profit.length; i++) {
            double x = i < industrial.length && Double.isFinite(industrial[i]) ? industrial[i] : 0;
            profit[i] = business[i] + x;
        }
        return new double[][] {profit, h.aligned("taxSales"), h.aligned("taxWage"), h.aligned("taxProperty")};
    }

    /** One tax as a card: its icon and name, its rate, what it raised and its share, its ten years, a chip at its legal stop - the whole a door to its page. */
    VBox taxCard(int i, double raised, double all, String rate, double[] history, boolean atStop, String info) {
        Label arrow = new Label("›");
        arrow.setStyle(Palette.strong(Palette.SIZE_HEADING + 2, Palette.TEXT_MUTED));
        VBox c = card(cardHead(TAX_ICONS[i + 1], Palette.MONEY, TAX_NAMES[i].toUpperCase() + " TAX", info, arrow),
                figure(rate, Palette.SIZE_LEAD, Palette.TEXT_HEAD),
                words(money(raised) + (all > 0 ? " · " + pct0(raised / all) + " of the take" : ""),
                        Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL),
                spark(history, 120, Palette.MONEY, 240, 26));
        if (atStop) c.getChildren().add(tag("at its legal maximum", Palette.WARN));
        final String page = TAX_NAMES[i];
        return doorCard(c, () -> open(TAXES, page));
    }

    /** P5: every tax at once, its (i). */
    static final String EVERY_TAX_INFO = "Moves all three rates to one number. A sector or wage band you've set apart "
            + "keeps its own offset on top of the new rate. While the three have parted, the dial's thumb sits on the "
            + "profit rate - what the save calls the income rate - with the three marked on its track, and its reading "
            + "says \"three rates\" until it is moved: an average of the three would be a rate nobody charges.";

    /** P6. */
    static final String PROPERTY_NOT = "Property is not on this dial: it is charged on value rather than on income, "
            + "and this dial does not reach it.";

    /**
     * EVERY TAX AT ONCE (0.7.4; a dial card since 0.7.36) - what the one
     * city rate became once each income tax had its own. Its head is the
     * three rates as chips; while they have parted the thumb sits on the
     * model's income rate with the three marked on its track and the reading
     * says "three rates" until it moves (the spec's D1: the giant "three
     * rates" over a reading of the profit rate in white was B1).
     */
    VBox everyTaxCard(TaxPolicy live) {
        boolean parted = live.incomeRatesSplit();
        Lever lever = new Lever(EVERY_TAX, "Every tax at once", live.getIncomeTaxRate(), 0, TaxPolicy.MAX_INCOME_TAX,
                STEP_INCOME, Money::pct2, live::setIncomeTaxRate);
        Ladder ladder = ladderOf(lever).stepReads(PolicyScreen::stepWord);
        if (parted) {
            ladder.marks(new double[] {live.getProfitTaxRate(), live.getSalesTaxRate(), live.getWageTaxRate()},
                    new String[] {"profit", "sales", "wage"}).idle("three rates");
        }
        FlowPane three = chips(chip("Profit " + pct2(live.getProfitTaxRate()), Palette.TEXT_LABEL),
                chip("Sales " + pct2(live.getSalesTaxRate()), Palette.TEXT_LABEL),
                chip("Wage " + pct2(live.getWageTaxRate()), Palette.TEXT_LABEL));
        DoubleFunction<List<Effect>> effects = everyTaxEffects(live);
        return dialCard(new DialCard(Icons.COIN, Palette.MONEY, "EVERY TAX AT ONCE", EVERY_TAX_INFO, null,
                parted ? "They have parted. This sets profit, sales and wage tax to one number. Your per-sector changes stay."
                       : "Sets profit, sales and wage tax together. Your per-sector changes stay.",
                List.of(three), ladder, staged(EVERY_TAX, live.getIncomeTaxRate()), effects,
                "Property is not on this dial.", PROPERTY_NOT + " " + CAVEAT, null), 520, false);
    }

    /** Every tax at once's effects at any value of its thumb (pure: the probe reads them): the three income taxes and THE BUDGET. */
    DoubleFunction<List<Effect>> everyTaxEffects(TaxPolicy live) {
        PolicyPreview.TaxTake now = PolicyPreview.taxTake(ui.game, live);
        return v -> {
            TaxPolicy after = previewFor(EVERY_TAX, v, live.getIncomeTaxRate(), STEP_INCOME);
            PolicyPreview.TaxTake then = PolicyPreview.taxTake(ui.game, after);
            return List.of(
                    Effect.of("Profit tax, a month", now.profitTotal(), then.profitTotal(), PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                    Effect.of("Sales tax", now.salesTotal(), then.salesTotal(), PolicyScreen::amount).delta(PolicyScreen::moneyMove)
                            .info(SALES_SCALED),
                    Effect.of("Wage tax", now.wageTotal(), then.wageTotal(), PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                    budgetEffect(after, stagedShare()));
        };
    }

    /* ---------------------------- PROFIT, SALES, WAGE, PROPERTY ---------------------------- */

    /** P8, the profit tax's head. */
    static final String PROFIT_INFO = "Charged on what each sector earned before tax. A sector that lost money "
            + "pays nothing, so this is the half of the city's revenue that falls "
            + "exactly when the city needs it most.";

    /** P4. */
    static final String LOSS_INFO = "A sector that lost money paid no profit tax at all, which is why the "
            + "first line falls in a bad month with nobody having touched a rate.";

    /** P10, the sales tax's head. */
    static final String SALES_INFO = "Charged on VALUE ADDED - what a sector sells, less the tax it already "
            + "paid on what it bought. A sector that only assembles somebody else's "
            + "parts pays on the assembly, which is why this raises money from a "
            + "chain without charging the same dollar twice.";

    /** P12, the wage tax's head. */
    static final String WAGE_INFO = "Taken off every payroll in the city before the household sees it. The "
            + "jobs are grouped by the education they need, which is the only "
            + "grouping the labour market itself uses.";

    /** P14, the property tax's head. */
    static final String PROPERTY_INFO = "Charged on what land and buildings are assessed at, whether or not the "
            + "owner earned anything. That is what makes it the steady half of the "
            + "city's revenue and the unpopular half.";

    /** P15. */
    static final String PROPERTY_STEP_INFO = "A twentieth of a point a step rather than the quarter the other three "
            + "get, because the same step is a very different change: a quarter point "
            + "on a rate of one is a quarter of the tax.";

    /** P11. */
    static final String REFUND_INFO = "\"In refund\" means a sector's credits on what it bought exceed the tax "
            + "on what it sold, so the city owes it rather than the other way round. "
            + "That is the mechanism working, not a fault - it happens to anybody "
            + "building stock or plant faster than they are selling.";

    /** P16. */
    static final String ROLL_INFO = "The power and water plants are the city's own and exempt. Raising what "
            + "land sells for raises what every existing owner is assessed at, so the "
            + "Land Office moves this line without anybody touching this rate.";

    /** The sales tax's preview, scaled (the spec's D11, B6). */
    static final String SALES_SCALED = "Scaled, not recomputed: each sector's net remittance moves by the ratio of its "
            + "new rate to its old. Exact while every sector's sales rate moves together - a move of the sales base with "
            + "no offset moved beside it - because its credits are what its suppliers charged it, at their own rates; "
            + "an estimate when only some move.";

    /** P7, a base rate's (i): what moves off it. */
    static String baseInfo(String offsets) {
        return offsets + " moves off this number, so this one dial moves every one of them "
                + "at once - and only them: the other two income taxes have rates of their "
                + "own. \"Every tax at once\", on the Everything page, sets all three.";
    }

    /** One tax's page: its base on a dial card, who pays it ranked with each row's own move, the payslip (wage) or the farmland (property), and the old table under "details". */
    void taxPage(VBox page, String which) {
        TaxPolicy live = policy();
        page.getChildren().add(baseCard(which, live));
        String info = switch (which) {
            case "Profit" -> PROFIT_INFO + " " + LOSS_INFO + " " + OFFSET_INFO;
            case "Sales" -> SALES_INFO + " " + REFUND_INFO + " " + OFFSET_INFO;
            case "Wage" -> WAGE_INFO + " " + OFFSET_INFO;
            default -> PROPERTY_INFO + " " + ROLL_INFO;
        };
        page.getChildren().add(sectionHead(switch (which) {
            case "Wage" -> "WHO PAYS: THE ELEVEN JOBS, IN FOUR BANDS";
            case "Property" -> "WHO PAYS: EVERY SECTOR, AND WHAT IT IS ON THE ROLL FOR";
            default -> "WHO PAYS";
        }, info, quiet("click a row for its own move")));
        page.getChildren().add(payers(which, live));
        if ("Wage".equals(which)) page.getChildren().add(payslipCard(live));
        if ("Property".equals(which)) {
            VBox farm = farmlandCard(live);
            if (farm != null) page.getChildren().add(farm);
        }
        page.getChildren().add(fold("table:" + which, "the table, as it was", () -> taxTable(which, live)));
    }

    /** P9, the offsets' note. */
    static final String OFFSET_INFO = "A move is in POINTS off the tax's own rate, so a row left at zero is taxed "
            + "at exactly that rate and follows it wherever it goes. The offsets are capped at "
            + String.format("%.0f", TaxPolicy.MAX_OFFSET * 100) + " points either way; a property offset's dial stops at "
            + String.format("%.0f", TaxPolicy.MAX_PROPERTY_TAX * 100) + ".";

    /** A tax's base on a dial card: what it raises and THE BUDGET before and after (and, for profit, the bank at Retail's rate - B5). */
    VBox baseCard(String which, TaxPolicy live) {
        boolean property = "Property".equals(which);
        String key = property ? "property" : baseKey(which.toLowerCase());
        double current = switch (which) {
            case "Profit" -> live.getProfitTaxRate();
            case "Sales" -> live.getSalesTaxRate();
            case "Wage" -> live.getWageTaxRate();
            default -> live.getPropertyTaxRate();
        };
        java.util.function.DoubleConsumer apply = switch (which) {
            case "Profit" -> live::setProfitTaxRate;
            case "Sales" -> live::setSalesTaxRate;
            case "Wage" -> live::setWageTaxRate;
            default -> live::setPropertyTaxRate;
        };
        double step = property ? STEP_PROPERTY : STEP_INCOME;
        Lever lever = new Lever(key, "The " + which.toLowerCase() + " rate", current, 0,
                property ? TaxPolicy.MAX_PROPERTY_TAX : TaxPolicy.MAX_INCOME_TAX, step, Money::pct2, apply);
        Ladder ladder = ladderOf(lever).stepReads(PolicyScreen::stepWord);
        DoubleFunction<List<Effect>> effects = baseEffects(which, key, current, step, live);
        String status = switch (which) {
            case "Profit" -> "Moves every sector's profit tax, and only those.";
            case "Sales" -> "Moves every sector's sales tax, and only those.";
            case "Wage" -> "Moves every band's wage tax, and only those.";
            default -> "Billed " + String.format("%.4f%%", live.getMonthlyPropertyTaxRate() * 100)
                    + " of assessed value a month, on land and buildings, earning or not.";
        };
        String info = switch (which) {
            case "Profit" -> PROFIT_INFO + " " + baseInfo("Every sector's profit tax");
            case "Sales" -> SALES_INFO + " " + baseInfo("Every sector's sales tax");
            case "Wage" -> WAGE_INFO + " " + baseInfo("Every band's wage tax");
            default -> PROPERTY_INFO + " " + PROPERTY_STEP_INFO;
        };
        return dialCard(new DialCard(TAX_ICONS[java.util.Arrays.asList(POLICY_TAX_PAGES).indexOf(which)], Palette.MONEY,
                "THE " + which.toUpperCase() + " RATE", info, null, status, null, ladder, staged(key, current), effects,
                "Sales".equals(which) ? "Scaled: exact while every sector's sales rate moves together." : CAVEAT_LINE,
                ("Sales".equals(which) ? SALES_SCALED + " " : "") + CAVEAT, null), 520, false);
    }

    /** A tax's base's effects at any value of its thumb (pure: the probe reads them): the tax a month, the bank on Profit, THE BUDGET. */
    DoubleFunction<List<Effect>> baseEffects(String which, String key, double current, double step, TaxPolicy live) {
        PolicyPreview.TaxTake now = PolicyPreview.taxTake(ui.game, live);
        return v -> {
            TaxPolicy after = previewFor(key, v, current, step);
            PolicyPreview.TaxTake then = PolicyPreview.taxTake(ui.game, after);
            List<Effect> out = new ArrayList<>();
            out.add(switch (which) {
                case "Profit" -> Effect.of("Profit tax, a month", now.profitTotal(), then.profitTotal(), PolicyScreen::amount);
                case "Sales" -> Effect.of("Sales tax, a month", now.salesTotal(), then.salesTotal(), PolicyScreen::amount).info(SALES_SCALED);
                case "Wage" -> Effect.of("Wage tax, a month", now.wageTotal(), then.wageTotal(), PolicyScreen::amount);
                default -> Effect.of("Property tax, a month", now.propertyTotal(), then.propertyTotal(), PolicyScreen::amount);
            });
            out.set(0, out.get(0).delta(PolicyScreen::moneyMove));
            if ("Profit".equals(which)) {
                out.add(Effect.of("The bank, at Retail's rate, next month", now.bank(), then.bank(), PolicyScreen::amount)
                        .delta(PolicyScreen::moneyMove).info(BANK_INFO));
            }
            out.add(budgetEffect(after, stagedShare()));
            return out;
        };
    }

    /** The bank's line (B5, D12). */
    static final String BANK_INFO = "The commercial bank's profit is taxed at Retail's rate - a Commercial Bank is a "
            + "commercial building - so Retail's offset moves it, and it has no dial of its own. It pays in arrears: "
            + "the bill at the top of next month is struck on the month just closed, so this line is next month's, not "
            + "the one last month booked.";

    /** One payer of a tax: its row's key and lever (null: no dial - the bank), its icon and name, what it paid last month, its figure's words, its rate now and staged, its offset, a chip (null: none), and its line of the take at the city's dials and the staged ones. */
    record Payer(String key, Lever lever, String svg, String name, double paid, String figure,
                 double rateNow, double rateThen, double offset, String chip, double before, double after,
                 DoubleFunction<String> rate, DoubleFunction<String> offsetWords) { }

    /** The payers of a tax (pure: the probe reads them): every sector, or every band, with what the take strikes for each at the city's dials and the staged ones; the bank last on Profit. */
    List<Payer> payersOf(String which, TaxPolicy live) {
        EconomyManager em = ui.game.getEconomyManager();
        SalesTaxLedger vat = em.getSalesTaxLedger();
        TaxPolicy staged = stagedPolicy();
        PolicyPreview.TaxTake now = PolicyPreview.taxTake(ui.game, live);
        PolicyPreview.TaxTake then = PolicyPreview.taxTake(ui.game, staged);
        List<Payer> out = new ArrayList<>();
        DoubleFunction<String> percent = Money::pct2;
        DoubleFunction<String> annual = r -> String.format("%.2f%%", r * 100);
        DoubleFunction<String> propPts = o -> String.format("%+.2f pts", o * 100).replace('-', '−');
        if ("Wage".equals(which)) {
            for (WageBand band : WageBand.values()) {
                double payroll = em.payrollIn(band);
                double paid = now.wage(band);
                String key = "wage:" + band.name();
                Lever lever = new Lever(key, band.label() + ", wage", live.getWageOffset(band),
                        -TaxPolicy.MAX_OFFSET, TaxPolicy.MAX_OFFSET, STEP_INCOME, Money::pts, v -> live.setWageOffset(band, v));
                out.add(new Payer(key, lever, Icons.STAFF, band.label(), paid,
                        payroll > 0 ? money(paid) + " off " + money(payroll) + " of payroll"
                                    : "nobody in the city holds one of these jobs",
                        live.effectiveWageRate(band), staged.effectiveWageRate(band), live.getWageOffset(band), null,
                        now.wage(band), then.wage(band), percent, Money::pts));
            }
            return out;
        }
        for (Sector s : ui.game.getSectors().all()) {
            String sk = s.key();
            switch (which) {
                case "Profit" -> {
                    SectorBooks.SectorMonth m = ui.game.getSectorBooks().get(s);
                    double bearing = Math.max(0, m.preTaxIncome());
                    String key = "profit:" + sk;
                    Lever lever = new Lever(key, s.label() + ", profit", live.getProfitOffset(s), -TaxPolicy.MAX_OFFSET,
                            TaxPolicy.MAX_OFFSET, STEP_INCOME, Money::pts, v -> live.setProfitOffset(s, v));
                    out.add(new Payer(key, lever, Icons.ofSector(s), s.label(), m.tax(),
                            bearing > 0 ? money(m.tax()) + " on " + money(bearing) + " of pre-tax income"
                                        : "it made nothing to be taxed on last month",
                            live.effectiveProfitRate(s), staged.effectiveProfitRate(s), live.getProfitOffset(s), null,
                            now.profit(sk), then.profit(sk), percent, Money::pts));
                }
                case "Sales" -> {
                    double sold = vat.getTaxableSales(sk);
                    double net = vat.getNet(sk);
                    boolean refund = vat.isInRefund(sk);
                    String key = "sales:" + sk;
                    Lever lever = new Lever(key, s.label() + ", sales", live.getSalesOffset(s), -TaxPolicy.MAX_OFFSET,
                            TaxPolicy.MAX_OFFSET, STEP_INCOME, Money::pts, v -> live.setSalesOffset(s, v));
                    out.add(new Payer(key, lever, Icons.ofSector(s), s.label(), net,
                            refund ? "credit " + money(vat.getCredit(sk)) + " against " + money(vat.getPayable(sk)) + " owed"
                                   : sold > 0 ? money(net) + " net on " + money(sold) + " of taxable sales"
                                              : "it sold nothing taxable last month",
                            live.effectiveSalesRate(s), staged.effectiveSalesRate(s), live.getSalesOffset(s),
                            refund ? "in refund" : null, now.sales(sk), then.sales(sk), percent, Money::pts));
                }
                default -> {
                    double assessed = em.getAssessedValue(s);
                    String key = "prop:" + sk;
                    Lever lever = new Lever(key, s.label() + ", property", live.getPropertyOffset(s),
                            -TaxPolicy.MAX_PROPERTY_TAX, TaxPolicy.MAX_PROPERTY_TAX, STEP_PROPERTY, propPts,
                            v -> live.setPropertyOffset(s, v));
                    double paid = em.getPropertyTaxCharged(sk);
                    out.add(new Payer(key, lever, Icons.ofSector(s), s.label(), paid,
                            assessed > 0 ? money(paid) + " on " + money(assessed) + " assessed"
                                         : "it owns nothing the city can assess",
                            live.effectivePropertyRate(s), staged.effectivePropertyRate(s), live.getPropertyOffset(s), null,
                            now.property(sk), then.property(sk), annual, propPts));
                }
            }
        }
        out.sort((x, y) -> Double.compare(y.paid(), x.paid()));
        if ("Profit".equals(which)) {
            out.add(new Payer("bank", null, Icons.BANK, "The bank", em.getBankTax(), "taxed at Retail's rate, in arrears",
                    live.effectiveProfitRate(ui.game.getSectors().retail()),
                    staged.effectiveProfitRate(ui.game.getSectors().retail()), 0, null, now.bank(), then.bank(),
                    percent, Money::pts));
        }
        return out;
    }

    /** Whether a payer paid nothing to speak of and is folded into one row (a sector in refund is not: it is owed). */
    static boolean paidNothing(Payer p) {
        return Math.abs(p.paid()) < .0005 && p.chip() == null && !"bank".equals(p.key());
    }

    /** WHO PAYS: the payers ranked by what they paid, a row opening into its own move; those that paid nothing folded into one muted row that opens to the same rows (the spec's D15). */
    VBox payers(String which, TaxPolicy live) {
        List<Payer> all = payersOf(which, live);
        // Every payer's lever registered, open or not, so the tray can name and apply a move made on a row now shut.
        for (Payer p : all) if (p.lever() != null) policyLevers.put(p.lever().key(), p.lever());
        double scale = 0;
        for (Payer p : all) scale = Math.max(scale, Math.max(Math.max(0, p.paid()), Math.max(0, p.after())));
        VBox list = card();
        list.setSpacing(2);
        List<Payer> zero = new ArrayList<>();
        for (Payer p : all) {
            if (paidNothing(p)) { zero.add(p); continue; }
            addPayer(list, p, scale, which);
        }
        if (!zero.isEmpty()) {
            String key = "zero:" + which;
            boolean shown = openLines.contains(key);
            StringBuilder names = new StringBuilder();
            for (Payer p : zero) names.append(names.length() == 0 ? "" : ", ").append(p.name());
            Label fold = words((shown ? "▾ " : "▸ ") + zero.size() + " paid nothing: " + names, Palette.SIZE_LABEL + 1, Palette.TEXT_MUTED);
            fold.setStyle(fold.getStyle() + " -fx-cursor: hand; -fx-padding: 6 6 6 6;");
            fold.setOnMouseClicked(e -> { if (!openLines.remove(key)) openLines.add(key); showPolicyMenu(); });
            list.getChildren().add(fold);
            if (shown) for (Payer p : zero) addPayer(list, p, scale, which);
        }
        return list;
    }

    /** A payer's row, and its own dial under it while it is open. */
    void addPayer(VBox list, Payer p, double scale, String which) {
        String open = "payer:" + p.key();
        boolean shown = p.lever() != null && openLines.contains(open);
        list.getChildren().add(payerRow(p, scale, shown, p.lever() == null ? null
                : () -> { if (!openLines.remove(open)) openLines.add(open); showPolicyMenu(); }));
        if (shown) list.getChildren().add(offsetCard(p, which));
    }

    /**
     * One payer: its icon and name ("▸ its own move" under a name that
     * opens), what it paid as a bar - with the staged take as a ghost bar
     * under it once anything staged reaches it - its figure, and its rate
     * as a chip ("15.00% → 16.00%" in the accent when staged) beside its
     * offset (neutral: a discount is not good news nor a surcharge bad, B14).
     */
    HBox payerRow(Payer p, double scale, boolean shown, Runnable toggle) {
        HBox lead = new HBox(8, iconSquare(p.svg(), Palette.MONEY, 24, 13));
        Label name = words(p.name(), Palette.SIZE_BODY + 1, p.paid() == 0 ? Palette.TEXT_MUTED : Palette.TEXT_BODY);
        VBox named = new VBox(0, name);
        if (toggle != null) {
            Label mark = new Label((shown ? "▾ " : "▸ ") + "its own move");
            mark.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.ACCENT));
            named.getChildren().add(mark);
        }
        HBox.setHgrow(named, Priority.ALWAYS);
        lead.getChildren().add(named);
        lead.setAlignment(Pos.CENTER_LEFT);
        lead.setMinWidth(180);
        lead.setPrefWidth(220);
        lead.setMaxWidth(220);

        boolean moved = !money(p.before()).equals(money(p.after()));
        VBox bars = new VBox(2, segmentBar(List.of(Segment.of(Math.max(0, p.paid()), Palette.MONEY)), scale > 0 ? scale : 1,
                List.of(), 0, 8));
        if (moved) {
            bars.getChildren().add(segmentBar(List.of(new Segment(Math.max(0, p.after()), Palette.ACCENT, true, null, null,
                    "at the staged rates " + money(p.after()) + " (" + moneyMove(p.after() - p.before()) + ")", null)),
                    scale > 0 ? scale : 1, List.of(), 0, 6));
        }
        bars.setAlignment(Pos.CENTER_LEFT);
        bars.setMinWidth(120);
        HBox.setHgrow(bars, Priority.ALWAYS);

        Label figure = words(p.figure(), Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL);
        figure.setPrefWidth(230);
        figure.setMinWidth(160);
        figure.setMaxWidth(230);

        boolean rateMoved = !p.rate().apply(p.rateNow()).equals(p.rate().apply(p.rateThen()));
        FlowPane chipRow = new FlowPane(6, 4);
        chipRow.setPrefWrapLength(190);
        chipRow.setMinWidth(150);
        chipRow.setPrefWidth(190);
        chipRow.setMaxWidth(190);
        chipRow.getChildren().add(chip(rateMoved ? p.rate().apply(p.rateNow()) + " → " + p.rate().apply(p.rateThen())
                : p.rate().apply(p.rateNow()), rateMoved ? Palette.ACCENT : Palette.TEXT_LABEL));
        if (Math.abs(p.offset()) > 1e-12) chipRow.getChildren().add(chip(p.offsetWords().apply(p.offset()), Palette.TEXT_LABEL));
        if (p.chip() != null) chipRow.getChildren().add(chip(p.chip(), Palette.TEXT_MUTED));

        HBox row = new HBox(10, lead, bars, figure, chipRow);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(40);
        String rest = "-fx-padding: 4 6 4 6; -fx-background-radius: 6;"
                + " -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 0 1 0;";
        row.setStyle(rest);
        if (toggle != null) {
            row.setStyle(rest + " -fx-cursor: hand;");
            row.setOnMouseEntered(e -> row.setStyle(rest + " -fx-cursor: hand; -fx-background-color: " + Palette.CONTROL + ";"));
            row.setOnMouseExited(e -> row.setStyle(rest + " -fx-cursor: hand;"));
            row.setOnMouseClicked(e -> toggle.run());
        }
        return row;
    }

    /** A payer's own move, opened in place: its offset's dial, and that payer's tax and THE BUDGET before and after. */
    VBox offsetCard(Payer p, String which) {
        Lever lever = p.lever();
        Ladder ladder = ladderOf(lever).stepReads(PolicyScreen::stepWord);
        DoubleFunction<List<Effect>> effects = offsetEffects(p, which);
        VBox c = dialCard(new DialCard(p.svg(), Palette.MONEY, p.name().toUpperCase() + ": ITS MOVE OFF THE RATE",
                OFFSET_INFO, null, "Taxed at " + p.rate().apply(p.rateNow()) + " now.", null, ladder,
                staged(lever.key(), lever.current()), effects, CAVEAT_LINE,
                ("Sales".equals(which) ? SALES_SCALED + " " : "") + CAVEAT, null), 460, false);
        VBox.setMargin(c, new javafx.geometry.Insets(2, 0, 8, 34));
        return c;
    }

    /** A payer's own move's effects at any value of its thumb (pure: the probe reads them): its tax, the bank beside Retail's, THE BUDGET. */
    DoubleFunction<List<Effect>> offsetEffects(Payer p, String which) {
        Lever lever = p.lever();
        PolicyPreview.TaxTake now = PolicyPreview.taxTake(ui.game, policy());
        String sk = p.key().substring(p.key().indexOf(':') + 1);
        return v -> {
            TaxPolicy after = previewFor(lever.key(), v, lever.current(), lever.step());
            PolicyPreview.TaxTake then = PolicyPreview.taxTake(ui.game, after);
            double b, a;
            switch (which) {
                case "Profit" -> { b = now.profit(sk); a = then.profit(sk); }
                case "Sales" -> { b = now.sales(sk); a = then.sales(sk); }
                case "Wage" -> {
                    WageBand band = WageBand.valueOf(sk);
                    b = now.wage(band);
                    a = then.wage(band);
                }
                default -> { b = now.property(sk); a = then.property(sk); }
            }
            List<Effect> out = new ArrayList<>();
            out.add(Effect.of(p.name() + "'s " + which.toLowerCase() + " tax, a month", b, a, PolicyScreen::amount).delta(PolicyScreen::moneyMove));
            if ("Profit".equals(which) && Sectors.RETAIL.equals(sk)) {
                out.add(Effect.of("The bank, at Retail's rate, next month", now.bank(), then.bank(), PolicyScreen::amount)
                        .delta(PolicyScreen::moneyMove).info(BANK_INFO));
            }
            out.add(budgetEffect(after, stagedShare()));
            return out;
        };
    }

    /** P13. */
    static final String PAYSLIP_INFO = "Only the first is a tax and only the first is set here - the other three "
            + "are promises the city has made and they are charged on the same "
            + "payroll. A band with a move off the wage rate pays that instead of "
            + "the first line; the other three are flat across every band. Promises owns those three dials; a second "
            + "copy of a lever is how two screens start disagreeing about what the city charges.";

    /** THE PAYSLIP (T12): what a wage taxed at the wage rate loses, as a bar of the wage in four parts, and each band moved off the rate with its own total; the door to the promises' dials. */
    VBox payslipCard(TaxPolicy live) {
        double[] parts = {live.getWageTaxRate(), live.getContributionRate(), live.getEiPremiumRate(), live.getHealthPremiumRate()};
        String[] names = {"Wage tax, at the wage rate", "Pension contribution", "EI premium", "Health premium"};
        List<Segment> segs = new ArrayList<>();
        for (int i = 0; i < 4; i++) segs.add(new Segment(parts[i], TAX_RAMP[i], false, null, null, names[i] + " " + pct2(parts[i]), null));
        VBox c = card(cardHead(Icons.STAFF, Palette.MONEY, "WHAT A PAYSLIP LOSES", PAYSLIP_INFO,
                        figure(pct2(live.payslipShare(null)), Palette.SIZE_LEAD, Palette.TEXT_HEAD)),
                segmentBar(segs, 1, List.of(), 0, 14),
                muted("of a wage taxed at the wage rate: the bar is the whole wage"));
        FlowPane key = new FlowPane(16, 4);
        for (int i = 0; i < 4; i++) key.getChildren().add(keySwatch(TAX_RAMP[i], names[i] + " " + pct2(parts[i])));
        c.getChildren().add(key);
        StringBuilder bands = new StringBuilder();
        for (WageBand band : WageBand.values()) {
            if (Math.abs(live.getWageOffset(band)) < 1e-12) continue;
            bands.append(bands.length() == 0 ? "" : " · ").append(band.label()).append(" ").append(pct2(live.payslipShare(band)));
        }
        if (bands.length() > 0) c.getChildren().add(muted("A band moved off the wage rate loses its own: " + bands + "."));
        c.getChildren().add(doorPill("Set the pension contribution and the premiums", Icons.CANE, Palette.MONEY,
                () -> open(PROMISES, "Pensions")));
        return c;
    }

    /** P17. */
    static final String FARMLAND_INFO = "A field is worth what a developer would pay for it and grows what a "
            + "farmer can grow on it. Tax the first and you lose the second, which is "
            + "what happened to every market garden that was ever within a cart ride "
            + "of a growing town. Relieving the land half is what real jurisdictions "
            + "do; the cost is the tax you do not collect.";

    /** P19. */
    static final String FARMLAND_CAVEAT = "Exact against today's land price. What it actually decides is whether "
            + "the next field is worth sinking, which shows up years later.";

    /**
     * The one dial that decides whether the city keeps its fields (T15).
     *
     * A farm is the only building in the game whose cost is the GROUND rather
     * than the structure, and the property tax is struck on what that ground
     * would FETCH. In a young city that is nothing; in a grown one it is more
     * than the field can possibly grow. Assessing farmland at USE value instead
     * is what Ontario's Farm Property Class, Nova Scotia's resource rate and
     * California's Williamson Act all do, for exactly this reason, and this is
     * the same lever with the same cost stated in the same place: this many
     * dollars a month of tax the city is choosing not to collect, against the
     * fields it would otherwise lose.
     */
    VBox farmlandCard(TaxPolicy live) {
        Sector fields = ui.game.getSectors().byKey(Sectors.AGRICULTURE);
        if (fields == null) return null;
        double relief = live.getFarmlandRelief();
        double ground = ui.game.getEconomyManager().landValueOf(fields);
        Lever lever = new Lever("farmland", "Farmland relieved", relief, 0, 1, .05, PolicyScreen::pct0, live::setFarmlandRelief);
        Ladder ladder = ladderOf(lever).stepReads(PolicyScreen::stepWord);
        DoubleFunction<List<Effect>> effects = farmlandEffects(live);
        return dialCard(new DialCard(Icons.FARMS, Palette.MONEY, "FARMLAND, AND WHAT IT IS ASSESSED AT", FARMLAND_INFO,
                pct0(relief) + " relieved", ground > 0 ? "The ground under the fields " + money(ground) + " at today's land price."
                        : "Nothing under cultivation, so the dial costs nothing today. It decides "
                          + "whether a field sunk now survives the city reaching it.",
                null, ladder, staged("farmland", relief), effects, FARMLAND_CAVEAT, FARMLAND_CAVEAT + " " + CAVEAT, null), 520, false);
    }

    /** The farmland relief's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> farmlandEffects(TaxPolicy live) {
        double relief = live.getFarmlandRelief();
        PolicyPreview.TaxTake now = PolicyPreview.taxTake(ui.game, live);
        return v -> {
            TaxPolicy after = previewFor("farmland", v, relief, .05);
            PolicyPreview.TaxTake then = PolicyPreview.taxTake(ui.game, after);
            return List.of(
                    Effect.of("Farmland on the roll", PolicyPreview.farmlandOnRoll(ui.game, live),
                            PolicyPreview.farmlandOnRoll(ui.game, after), PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                    Effect.of("Agriculture's property tax, a month", now.property(Sectors.AGRICULTURE),
                            then.property(Sectors.AGRICULTURE), PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                    Effect.of("Tax forgone, a month", PolicyPreview.farmlandForgone(ui.game, live),
                            PolicyPreview.farmlandForgone(ui.game, after), PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                    budgetEffect(after, stagedShare()));
        };
    }

    /** The old table of a tax's page (T9-T11, T14), behind "details": every sector or band with its figures as they were drawn. */
    Node taxTable(String which, TaxPolicy policy) {
        EconomyManager em = ui.game.getEconomyManager();
        SalesTaxLedger vat = em.getSalesTaxLedger();
        GridPane table = grid(new double[] {170, 130, 120, 130}, rightAfterFirst(4));
        int line = 1;
        switch (which) {
            case "Profit" -> {
                gridHead(table, "", "pre-tax income", "taxed at", "paid");
                for (Sector sector : ui.game.getSectors().all()) {
                    SectorBooks.SectorMonth month = ui.game.getSectorBooks().get(sector);
                    double bearing = Math.max(0, month.preTaxIncome());
                    table.add(gridCell(sector.label(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
                    table.add(gridCell(bearing > 0 ? money(bearing) : "—", bearing > 0 ? Palette.TEXT_MUTED : Palette.TEXT_SPENT,
                            Palette.SIZE_CAPTION, true), 1, line);
                    table.add(gridCell(pct2(policy.effectiveProfitRate(sector)), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, line);
                    table.add(gridCell(money(month.tax()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 3, line);
                    line++;
                }
            }
            case "Sales" -> {
                gridHead(table, "", "taxable sales", "charged at", "net");
                for (Sector sector : ui.game.getSectors().all()) {
                    double sold = vat.getTaxableSales(sector.key());
                    table.add(gridCell(sector.label(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
                    table.add(gridCell(sold > 0 ? money(sold) : "—", sold > 0 ? Palette.TEXT_MUTED : Palette.TEXT_SPENT,
                            Palette.SIZE_CAPTION, true), 1, line);
                    table.add(gridCell(pct2(policy.effectiveSalesRate(sector)), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, line);
                    table.add(gridCell(vat.isInRefund(sector.key()) ? "in refund" : money(vat.getNet(sector.key())),
                            vat.isInRefund(sector.key()) ? Palette.TEXT_MUTED : Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 3, line);
                    line++;
                }
            }
            case "Wage" -> {
                gridHead(table, "", "payroll", "taxed at", "raised");
                for (WageBand band : WageBand.values()) {
                    double payroll = em.payrollIn(band);
                    table.add(gridCell(band.label(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
                    table.add(gridCell(payroll > 0 ? money(payroll) : "—", payroll > 0 ? Palette.TEXT_MUTED : Palette.TEXT_SPENT,
                            Palette.SIZE_CAPTION, true), 1, line);
                    table.add(gridCell(pct2(policy.effectiveWageRate(band)), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, line);
                    table.add(gridCell(payroll > 0 ? money(em.wageTaxUnder(band, policy)) : "—", Palette.TEXT_HEAD,
                            Palette.SIZE_CAPTION, true), 3, line);
                    line++;
                }
            }
            default -> {
                gridHead(table, "", "assessed", "a year", "billed");
                for (Sector sector : ui.game.getSectors().all()) {
                    double value = em.getAssessedValue(sector);
                    if (value <= 0) continue;
                    table.add(gridCell(sector.label(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
                    table.add(gridCell(money(value), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 1, line);
                    table.add(gridCell(String.format("%.2f%%", policy.effectivePropertyRate(sector) * 100), Palette.TEXT_HEAD,
                            Palette.SIZE_CAPTION, true), 2, line);
                    table.add(gridCell(money(em.getPropertyTaxFor(sector)), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 3, line);
                    line++;
                }
            }
        }
        VBox c = column(table);
        if ("Property".equals(which)) {
            // The column bills at TODAY'S roll and the take's head is history, so the two differ the moment the
            // rate or the roll moves. Footing the column makes that a comparison rather than a contradiction.
            c.getChildren().add(statementTotal("Which comes to at today's rate",
                    money(PolicyPreview.taxTake(ui.game, policy).propertyTotal()), Palette.TEXT_HEAD));
        }
        return c;
    }

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

       Where is the floor, who sits on it, and what does moving it cost? THE
       WAGE LADDER (0.7.36): the eleven jobs under their four bands, each a
       bar of what it pays, against a rule at the floor IN TODAY'S MONEY (the
       spec's D6 and B7: the tab printed the founding figure as a wage - the
       floor read $3,460 where the unskilled job is paid $3,827); each band
       says whether it is pinned, as the model reads it (D7: LabourMarket
       .isPinned() is left as it is until B8 is decided). A staged floor
       moves a ghost rule, and each job's ghost bar is where its wage heads
       at the new floor. Then the dial on its card, beside the city's own
       payroll.
       ===================================================================== */

    /** P22, the floor's (i). */
    static final String FLOOR_INFO = "Every wage in the city is a multiple of this number, so moving it moves "
            + "all of them - including the doctors and nurses the city pays out of its "
            + "own treasury. Wages walk to their new level over about a year rather "
            + "than jumping, so a change made now is a bill met slowly.";

    /** P23, both forms. */
    static final String PINNED_WORDS = "At least one skill level is oversupplied AND cannot get any cheaper, so "
            + "those workers are leaving the city rather than taking a pay cut. That is "
            + "what a binding minimum wage does: the market clears in people instead of "
            + "in price. Lowering the floor would keep them, and pay them less.";
    /** ...the other: no band pinned. */
    static final String FREE_WORDS = "No skill level is pinned against the floor, so the labour market is "
            + "clearing on price alone and the minimum wage is not currently binding "
            + "on anybody.";

    /** P24. */
    static final String FLOOR_CAVEAT = "Once wages have walked there, which takes about a year. Nothing about "
            + "this preview knows who leaves or who arrives because of it - a "
            + "higher floor is a bill AND a reason for skilled people to come.";

    void floorPage(VBox page) {
        LabourMarket market = ui.game.getLabourMarket();
        PopulationManager people = ui.game.getPopulationManager();
        double floor = market.getMinimumWage();
        double want = staged("floor", floor);
        boolean moved = isStaged("floor");

        boolean anyPinned = false;
        for (WageBand band : WageBand.values()) anyPinned |= market.isPinned(band) && people.surplusInBand(band) > 0;
        String says = anyPinned ? PINNED_WORDS : FREE_WORDS;

        page.getChildren().add(sectionHead("THE WAGE LADDER", FLOOR_INFO, quiet(moved
                ? "the ghosts: once wages have walked there (about a year)" : "what every job pays, against the floor in today's money")));
        page.getChildren().add(card(wageLadder(market, people, floor, want, moved), noteLine(firstWords(says), says, 1100)));
        page.getChildren().add(split(floorCard(market, floor), cityPayrollCard(), 2.0 / 3));
        page.getChildren().add(fold("floor:table", "the ladder as a table, in today's money", () -> floorTable(market, want, moved)));
    }

    /** The eleven jobs under their bands on one scale, the floor a rule across them; a band's chip says whether it is pinned (LabourMarket.isPinned(), as the model has it). */
    Node wageLadder(LabourMarket market, PopulationManager people, double floor, double want, boolean moved) {
        double cash = market.cashMinimumWage(), cashThen = market.cashAt(want);
        List<ScaleRow> rows = new ArrayList<>();
        double scale = Math.max(cash, cashThen);
        for (WageBand band : WageBand.values()) {
            boolean pinned = market.isPinned(band);
            double surplus = people.surplusInBand(band);
            String chip = pinned && surplus > 0 ? "pinned · " + people(surplus) + " spare"
                    : pinned ? "at the floor" : "has room" + (surplus > 0 ? " · " + people(surplus) + " spare" : "");
            rows.add(ScaleRow.of(band.label(), null, List.of()).strong()
                    .tag(chip, pinned && surplus > 0 ? Palette.WARN : Palette.TEXT_LABEL));
            for (JobType job : JobType.values()) {
                if (WageBand.of(job) != band) continue;
                double wage = market.getWage(job);
                double then = market.targetWageAt(job, want);
                scale = Math.max(scale, Math.max(wage, moved ? then : 0));
                List<Run> runs = new ArrayList<>();
                runs.add(Run.of(0, wage, Palette.MONEY).tip(ui.buildScreen.jobLabel(job) + " is paid " + moneyFull(wage) + " a month"));
                if (moved) runs.add(Run.of(0, then, Palette.ACCENT).outlined()
                        .tip("once wages have walked there: " + moneyFull(then) + " a month"));
                rows.add(ScaleRow.of(ui.buildScreen.jobLabel(job), moved ? moneyFull(wage) + " → " + moneyFull(then) : moneyFull(wage), runs));
            }
        }
        List<Rule> rules = new ArrayList<>();
        rules.add(new Rule(cash, Palette.TEXT_HEAD, false, "the floor " + moneyFull(cash),
                "The floor in today's money: " + moneyFull(market.getMinimumWage()) + " at founding prices, lifted by the cost of living.", null));
        if (moved) rules.add(new Rule(cashThen, Palette.ACCENT, true, "staged " + moneyFull(cashThen), "The floor as staged, in today's money.", null));
        return scaleRows(rows, scale * 1.05, rules, 220, 170, 12);
    }

    /** THE FLOOR on its dial card: read in today's money, set in founding money (D6); the floor, the unskilled wage, every wage and the city's payroll before and after. */
    VBox floorCard(LabourMarket market, double floor) {
        double min = market.getMinSettable(), max = market.getMaxSettable(), step = (max - min) / 200;
        DoubleFunction<String> today = v -> moneyFull(market.cashAt(v));
        Ladder ladder = ownLadder("floor", floor, min, max, step, today).stepReads(today);
        double want = staged("floor", floor);
        double payroll = ui.game.getEducation().getPayroll() + ui.game.getHealthcare().getPayroll();
        DoubleFunction<List<Effect>> effects = floorEffects(market);
        String info = FLOOR_INFO + " The dial is set in founding money - " + moneyFull(floor) + " a month at founding "
                + "prices - and read in today's: the cost of living and the "
                + String.format("%+.0f%%", market.getMinimumWageAdjustment() * 100) + " nudge make it "
                + moneyFull(market.cashMinimumWage()) + ". One step is a two-hundredth of the dial's range.";
        return dialCard(new DialCard(Icons.STAFF, Palette.MONEY, "THE FLOOR", info,
                moneyFull(market.cashMinimumWage()) + " a month", "Every wage in the city is a multiple of this number.",
                null, ladder, want, effects, "Once wages have walked there, about a year.", FLOOR_CAVEAT,
                applyFoot("floor", "Set the floor to " + moneyFull(market.cashAt(want)), () -> market.setMinimumWage(want))),
                380, false);
    }

    /** The floor's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> floorEffects(LabourMarket market) {
        double payroll = ui.game.getEducation().getPayroll() + ui.game.getHealthcare().getPayroll();
        return v -> List.of(
                Effect.of("The floor, in today's money", market.cashMinimumWage(), market.cashAt(v), Money::moneyFull)
                        .delta(PolicyScreen::moneyMoveFull),
                Effect.of("The unskilled job's wage", market.getWage(JobType.NO_DIPLOMA),
                        market.targetWageAt(JobType.NO_DIPLOMA, v), Money::moneyFull).delta(PolicyScreen::moneyMoveFull),
                Effect.of("Every wage, once walked", 1, PolicyPreview.wageMoveAt(ui.game, v),
                        x -> String.format("%+.1f%%", unsigned0((x - 1) * 100, 1)).replace('-', '−')),
                Effect.of("The city's own payroll", payroll, PolicyPreview.cityPayrollAt(ui.game, v), PolicyScreen::amount)
                        .delta(PolicyScreen::moneyMove));
    }

    /** What the city itself pays (W4), neutral (B14: a payroll is not bad news by being one). */
    VBox cityPayrollCard() {
        Education schools = ui.game.getEducation();
        Healthcare hospitals = ui.game.getHealthcare();
        return card(cardHead(Icons.GOVERNMENT, Palette.MONEY, "WHAT THE CITY ITSELF PAYS",
                        "The teachers, doctors and nurses on the city's own payroll: every one of their wages is a multiple "
                        + "of the floor too, so the floor is a bill the treasury pays as well as one it sets.", null),
                cardLine("Teachers", money(schools.getPayroll()), null),
                cardLine("Doctors and nurses", money(hospitals.getPayroll()), null),
                cardLine("Its own payroll", money(schools.getPayroll() + hospitals.getPayroll()), Palette.TEXT_HEAD));
    }

    /** The ladder as a table (W2), behind "details", in today's money: each job's multiple of the floor, what it is paid, and where it heads at a staged floor. */
    Node floorTable(LabourMarket market, double want, boolean moved) {
        GridPane ladder = grid(new double[] {220, 100, 120, 120},
                new javafx.geometry.HPos[] {javafx.geometry.HPos.LEFT, javafx.geometry.HPos.RIGHT,
                        javafx.geometry.HPos.RIGHT, javafx.geometry.HPos.RIGHT});
        gridHead(ladder, "", "multiple", "paid", moved ? "heads for" : "");
        int line = 1;
        for (JobType job : JobType.values()) {
            ladder.add(gridCell(ui.buildScreen.jobLabel(job), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            ladder.add(gridCell(String.format("%.2fx", LabourMarket.ratioOf(job)), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 1, line);
            ladder.add(gridCell(moneyFull(market.getWage(job)), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, line);
            if (moved) ladder.add(gridCell(moneyFull(market.targetWageAt(job, want)), Palette.ACCENT, Palette.SIZE_CAPTION, true), 3, line);
            line++;
        }
        return column(ladder);
    }

    /* =====================================================================
       MONEY - the policy rate

       THE DIAL IS THE PLAYER'S. The rule sits beside it saying what it would
       do and why, because a central bank is the most jargon-dense thing in
       this game and a number that moves without a reason on the screen is a
       number that punishes the player for not having read a textbook.

       Jerus picked this over an independent bank with a mandate: "the dial is
       yours, but the screen shows what a Taylor rule would do". Since 0.7.0
       the player can also hand the dial to the rule - the autopilot - and take
       it back.

       Where is the dial against the rule and inflation, and what does a move
       do to every rate in the city? THE RATE LINE (0.7.36): one scale with
       every rate on it as a named rule - inflation and the target, the dial,
       the rule, what savers are paid, what the city borrows at, prime and the
       world's - and a staged dial's rates as ghosts; under it what the city
       pays over the world, plain and real. Then three cards: THE DIAL, THE
       RULE, THE CENTRAL BANK. Then the prices.
       ===================================================================== */

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

    /** P25, the dial's line, and P26 corrected (the spec's B16: the city's paper is priced from the higher of the dial and the bank's cost of funds, not "at the dial"). */
    String rateInfo() {
        DebtManager market = ui.game.getDebtManager();
        return "The one number under every other rate in the city. Raising it past "
                + "inflation supports the currency and makes every borrower pay more - that "
                + "is not a side effect, it is the same act. "
                + String.format("This is what the central bank pays on the bank's reserves, and so the "
                + "least anybody in the city lends for. The city's own paper is priced from "
                + "the higher of it and what the bank's own money costs it with its margin - the floor, %s now - "
                + "and up to %.0f points over that when it owes too much against its output and its taxes. The "
                + "Finances tab takes that apart.", pct2(market.floorRate()), 2 * DebtManager.maxSpreadPerMeasure() * 100);
    }

    /** P28. */
    static final String OVER_INFO = "Hot money follows this difference in, and leaves the day it closes. "
            + "It is also what the bank prices every loan up from, so the whole city's credit moves with it. "
            + "Against what the city actually pays (2026-09-12), not against the dial: hot money reads the city's "
            + "rate and the bank's deposit rate.";

    /** P28's second half: the real differential. */
    static final String REAL_INFO = "The currency follows this one: a dial under inflation is a real rate "
            + "the world is paid to leave, and it pushes the currency down however high "
            + "the number on the dial is. The dial less inflation, against the world's rate less its own.";

    void ratePage(VBox page) {
        DebtManager market = ui.game.getDebtManager();
        PriceIndex px = ui.game.getPriceIndex();
        double rate = market.getPolicyRate();
        double want = staged("policy", rate);
        boolean moved = isStaged("policy");

        page.getChildren().add(sectionHead("THE RATE LINE", rateInfo(), quiet(moved
                ? "the ghosts: at the staged dial, from next month" : "every rate in the city on one scale")));
        page.getChildren().add(card(rateLine(rate, want, moved), row(
                rateCell("OVER THE WORLD", points(market.overTheWorld()), moved
                        ? "→ " + points(market.overTheWorldAt(want)) + " at the staged dial" : "the city's rate less the world's " + pct2(DebtManager.WORLD_BASE_RATE), OVER_INFO),
                rateCell("REAL, AGAINST THE WORLD", points(ui.game.realRateDifferential()),
                        "the dial less inflation, against the world's", REAL_INFO))));

        VBox dial = theDialCard(market, px, rate, want);
        VBox rule = ruleCard(market, px);
        VBox bank = new VBox(TILE_GAP, holdingsCard(market), ceilingCard());
        page.getChildren().add(row(dial, rule, bank));

        page.getChildren().add(pricesCard(px));
        page.getChildren().add(fold("rate:history", "the price level and the policy rate, since founding", this::priceRateChart));
    }

    /**
     * Behind the PRICES strip's "details" (the spec's R4): City History's
     * price level (the index, left axis) and policy rate (percent, right
     * axis) on one small chart - no legend, so the key under it names them.
     * The rate reads to two places, as the dial does (History's percent has one,
     * which reads a 0.13% dial as 0.1%).
     */
    Node priceRateChart() {
        HistorySave h = ui.game.getHistorySave();
        if (h.months() < 2) return quiet("A line needs two months; come back next month.");
        TimeChart chart = new TimeChart(new ChartModel(), Set.of(), false);
        chart.setData(h.getMonth(), List.of(
                        new TimeChart.Line("priceIndex", "price level", Palette.MONEY_LIGHT, 0, h.aligned("priceIndex"),
                                v -> HistoryScreen.plotScale("index", v), v -> ui.historyScreen.fmtUnit("index", v), ""),
                        new TimeChart.Line("policyRate", "the policy rate", Palette.MONEY, 1, h.aligned("policyRate"),
                                v -> HistoryScreen.plotScale("percent", v), Money::pct2, "")),
                HistoryScreen.axisFor("index"), HistoryScreen.axisFor("percent"), false, false, null, List.of(),
                List.of(), List.of(), "no months recorded yet");
        chart.setSize(1100, 180);
        FlowPane key = new FlowPane(14, 4, keySwatch(Palette.MONEY_LIGHT, "the price level, founding = 1 (left)"),
                keySwatch(Palette.MONEY, "the policy rate (right)"));
        return new VBox(4, chart, key);
    }

    /** A cell under the rate line: its label, its figure, a line, and the paragraph behind an (i). */
    static VBox rateCell(String label, String value, String line, String info) {
        Label l = new Label(label);
        l.setStyle(Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        HBox head = new HBox(Palette.GAP_TIGHT, l, infoButton(info, true));
        head.setAlignment(Pos.CENTER_LEFT);
        VBox c = new VBox(2, head, figure(value, Palette.SIZE_HEADING + 2, Palette.TEXT_HEAD), muted(line));
        c.setStyle("-fx-padding: 8 10 8 10; -fx-background-color: " + Palette.PANEL + "; -fx-background-radius: 6;");
        return c;
    }

    /** One rate on the line, worked out without drawing it: where, its name and colour, dashed or not, and its tooltip. */
    record RateMark(double at, String name, String colour, boolean dashed, String tip) { }

    /**
     * THE RATE LINE's marks (pure: the probe reads them): inflation (amber
     * only past a point from the target, the old page's line), the target,
     * the dial, the rule, savers, the city, prime and the world - and, staged,
     * the dial, savers, the city and prime at the new dial.
     */
    List<RateMark> rateMarks(double rate, double want, boolean moved) {
        DebtManager market = ui.game.getDebtManager();
        PriceIndex px = ui.game.getPriceIndex();
        Bank bank = ui.game.getBank();
        List<RateMark> out = new ArrayList<>();
        double target = market.getInflationTarget();
        if (px.hasRate()) {
            double inflation = px.inflation();
            out.add(new RateMark(inflation, "inflation " + signedPct1(inflation),
                    Math.abs(inflation - target) > .01 ? Palette.WARN : Palette.TEXT_LABEL, false,
                    "Inflation, year on year: " + signedPct1(inflation)));
            out.add(new RateMark(market.advisedPolicyRate(inflation), "rule " + pct2(market.advisedPolicyRate(inflation)),
                    Palette.TEXT_LABEL, true, "What the rule would set at this inflation"
                    + (Math.abs(market.ruleRate(inflation) - market.advisedPolicyRate(inflation)) > 1e-9
                        ? " - it says " + pct2(market.ruleRate(inflation)) + ", and the dial stops there" : "")));
        }
        out.add(new RateMark(target, "target " + DebtManager.targetWords(target), Palette.TEXT_MUTED, true,
                "The inflation target the rule aims at"));
        out.add(new RateMark(rate, "dial " + pct2(rate), Palette.MONEY, false, "The policy rate: your dial"));
        out.add(new RateMark(bank.depositRate(), "savers " + ratePct(bank.depositRate()), Palette.PEOPLE, false,
                "What savers were paid last month"));
        out.add(new RateMark(market.getRate(), "city " + pct2(market.getRate()), Palette.TEXT_HEAD, false,
                "What the city itself borrows at: its short-end rate"));
        out.add(new RateMark(bank.prime(rate), "prime " + pct2(bank.prime(rate)), Palette.BUSINESS, false,
                "What a good credit pays the bank to borrow: prime"));
        out.add(new RateMark(DebtManager.WORLD_BASE_RATE, "world " + pct2(DebtManager.WORLD_BASE_RATE), Palette.TEXT_SPENT, false,
                "The world's own rate"));
        if (moved) {
            out.add(new RateMark(want, "dial → " + pct2(want), Palette.ACCENT, true, "The dial as staged"));
            out.add(new RateMark(bank.depositRateAt(want), "savers → " + ratePct(bank.depositRateAt(want)), Palette.ACCENT, true,
                    "What the bank would choose for its savers next month at the staged dial"));
            out.add(new RateMark(market.rateAtPolicy(want), "city → " + pct2(market.rateAtPolicy(want)), Palette.ACCENT, true,
                    "What the city would be quoted at the staged dial"));
            out.add(new RateMark(bank.prime(want), "prime → " + pct2(bank.prime(want)), Palette.ACCENT, true,
                    "Prime at the staged dial"));
        }
        return out;
    }

    /** THE RATE LINE: the dial as a bar, every other rate a named rule across it, on 0 to a quarter past the highest (never under 3%). */
    Node rateLine(double rate, double want, boolean moved) {
        List<RateMark> marks = rateMarks(rate, want, moved);
        double top = 0;
        for (RateMark m : marks) top = Math.max(top, m.at());
        double scale = Math.max(.03, top * 1.25);
        List<Rule> rules = new ArrayList<>();
        for (RateMark m : marks) rules.add(new Rule(m.at(), m.colour(), m.dashed(), m.name(), m.tip(), null));
        List<Run> runs = new ArrayList<>();
        runs.add(Run.of(0, rate, Palette.MONEY).tip("The policy rate " + pct2(rate)));
        if (moved) runs.add(Run.of(0, want, Palette.ACCENT).outlined().tip("Staged " + pct2(want)));
        List<ScaleRow> rows = List.of(ScaleRow.of("the dial", moved ? pct2(rate) + " → " + pct2(want) : pct2(rate), runs));
        VBox line = new VBox(4, scaleRows(rows, scale, rules, 90, 140, 14));
        if (ui.game.getPriceIndex().hasRate() && ui.game.getPriceIndex().inflation() < 0) {
            line.getChildren().add(muted("Prices are falling, " + signedPct1(ui.game.getPriceIndex().inflation())
                    + " a year: under the line's nothing, so not drawn on it."));
        }
        return line;
    }

    /** P32. */
    static final String HAND_INFO = "Jerus's autopilot: the rule can hold the dial. Every month, before anything is "
            + "priced, the central bank sets the dial where the rule says. Setting a rate yourself takes it back, as "
            + "it would from any central bank the player owns.";
    /** P34: the dial card's caveat. */
    static final String REPRICE_INFO = "This does not reprice a single bond the city has already sold - every "
            + "coupon on the book was struck on the day it was issued. It changes "
            + "what the NEXT one costs, what every business and household is "
            + "charged, and what savers are paid, all from next month.";

    /** The spend factor to one place: the dial reaches it only through the savers' rate, a few tenths of a point at most, which a whole percent would round away. */
    static String spendPct(double v) { return String.format("%.1f%%", unsigned0(v * 100, 1)); }

    /** P29. */
    String spendInfo() {
        return String.format("Households spend %.0f%% of what they would at zero real interest on what is above a "
                + "basket a head. A deposit rate over inflation pays people to wait, and what they do not spend "
                + "is demand the shops do not see; one under it pays them to buy now. Only what "
                + "is above a basket a head moves, and never below %.0f%% or past %.0f%% of it. Savers earn %s "
                + "after inflation now.", ui.game.spendFactor() * 100,
                HouseholdBalance.SPEND_FLOOR * 100, HouseholdBalance.SPEND_CEILING * 100,
                signedPct1(ui.game.realDepositRate()));
    }

    /**
     * THE DIAL (R5, R7): the ladder with the rule's rate marked on it, the
     * thirteen chips and "do what the rule says", whose hand it is in; what
     * a move does to the city's rate, the savers', prime and what households
     * spend - the savers' as the bank would choose them at the dial
     * (Bank.depositRateAt(), M7: the old page set a share of the dial
     * against the rate the bank had chosen, B12); and its own Apply, which
     * takes the dial from the rule (DebtManager.takeTheDial()).
     */
    VBox theDialCard(DebtManager market, PriceIndex px, double rate, double want) {
        Bank bank = ui.game.getBank();
        Ladder ladder = ownLadder("policy", rate, DebtManager.MIN_POLICY_RATE, DebtManager.MAX_POLICY_RATE, .0005, Money::pct2);
        double advised = px.hasRate() ? market.advisedPolicyRate(px.inflation()) : Double.NaN;
        if (Double.isFinite(advised)) ladder.marks(new double[] {advised}, new String[] {"rule"});
        FlowPane steps = new FlowPane(6, 6);
        for (int pct : DIAL_STEPS) {
            final double at = pct / 100.0;
            steps.getChildren().add(stepChip(pct + "%", () -> {
                if (moved(at, rate, .0005)) stage("policy", at); else unstage("policy");
                showPolicyMenu();
            }, Math.abs(want - at) > 1e-9));
        }
        if (Double.isFinite(advised) && Math.abs(advised - rate) > 1e-9) {
            steps.getChildren().add(stepChip("do what the rule says", () -> { stage("policy", advised); showPolicyMenu(); }, true));
        }
        boolean ruleHasIt = market.isAutopilot();
        HBox hand = new HBox(8, words(ruleHasIt ? "In the rule's hand: it sets " + pct2(Double.isFinite(advised) ? advised : rate)
                        + " every month." : "In your hand." + (Double.isFinite(advised) ? " The rule would set " + pct2(advised) + "." : ""),
                Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL),
                stepChip(ruleHasIt ? "take the dial back" : "hand the dial to the rule",
                        () -> { market.setAutopilot(!ruleHasIt); showPolicyMenu(); }, false), infoButton(HAND_INFO, true));
        hand.setAlignment(Pos.CENTER_LEFT);
        DoubleFunction<List<Effect>> effects = dialEffects(rate);
        return dialCard(new DialCard(Icons.BANK, Palette.MONEY, "THE DIAL", rateInfo(), pct2(rate),
                "The one number under every other rate in the city.", List.of(steps, hand), ladder, want, effects,
                "It reprices no bond already sold.", REPRICE_INFO,
                applyFoot("policy", "Set the policy rate to " + pct2(want), () -> market.takeTheDial(want))), 360, true);
    }

    /** The policy rate's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> dialEffects(double rate) {
        DebtManager market = ui.game.getDebtManager();
        Bank bank = ui.game.getBank();
        return v -> List.of(
                Effect.of("The city borrows at", market.rateAtPolicy(rate), market.rateAtPolicy(v), Money::pct2)
                        .delta(Money::points).scale(Math.max(.01, market.rateAtPolicy(Math.max(v, rate)) * 1.1)),
                Effect.of("Savers are paid, next month", bank.depositRateAt(rate), bank.depositRateAt(v), Money::ratePct)
                        .delta(Money::points).info("What the bank would choose for its savers next month at this dial: "
                                + "the rate its funding asks for, moved a sixth of the way from what it paid last month."),
                Effect.of("Prime", bank.prime(rate), bank.prime(v), Money::pct2).delta(Money::points),
                Effect.of("Households spend, of what they would at zero", PolicyPreview.spendFactorAt(ui.game, rate),
                        PolicyPreview.spendFactorAt(ui.game, v), PolicyScreen::spendPct).info(spendInfo()));
    }

    /**
     * THE RULE (R6): what it aims at - the target, set at once by its chips
     * and its ladder (0.7.4: it moves no price this month, only what the rule
     * says) - and what it says at two targets, and inflation against the
     * target as a bar in its band.
     */
    VBox ruleCard(DebtManager market, PriceIndex px) {
        double target = market.getInflationTarget();
        double inflation = px.inflation();
        String[] targets = new String[TARGET_STEPS.length];
        for (int i = 0; i < TARGET_STEPS.length; i++) targets[i] = TARGET_STEPS[i] + "%";
        Node chips = chipStrip(targets, DebtManager.targetWords(target), Palette.SIZE_CAPTION, picked -> {
            market.setInflationTarget(Integer.parseInt(picked.replace("%", "")) / 100.0);
            showPolicyMenu();
        });
        VBox ladder = Ladder.of(DebtManager.MIN_INFLATION_TARGET, DebtManager.MAX_INFLATION_TARGET, TARGET_STEP,
                        DebtManager::targetWords)
                .current(target)
                .appliesAtOnce(v -> { market.setInflationTarget(halfPoint(v)); showPolicyMenu(); })
                .wide(360).build();
        // Struck from the rule at both targets - DebtManager.ruleRate(inflation, target) - on this month's
        // inflation once there is a year of it, and on 5% as the example until there is.
        double example = px.hasRate() ? inflation : .05;
        double otherTarget = target > 1e-9 ? 0 : DebtManager.DEFAULT_INFLATION_TARGET;
        String reason = px.hasRate() ? market.adviceReason(inflation)
                : "There is not yet a year of prices to measure inflation against. The rule has nothing to say until there is.";
        String whole = reason + " " + String.format(
                "The rule aims prices here. At %s with inflation at %.1f%% it sets %.1f%%; at %s "
                + "it would set %.1f%%. A point of target is %.1f points off the rule's rate at "
                + "any inflation - the same slope, aimed somewhere else.",
                DebtManager.targetWords(target), example * 100, market.ruleRate(example) * 100,
                DebtManager.targetWords(otherTarget), market.ruleRate(example, otherTarget) * 100,
                DebtManager.TAYLOR_WEIGHT);
        VBox c = card(cardHead(Icons.POLICY, Palette.MONEY, "THE RULE", whole, null),
                figure(DebtManager.targetWords(target) + " a year", Palette.SIZE_LEAD, Palette.TEXT_HEAD),
                muted("the inflation target it aims at · set at once"),
                line(firstWords(reason), reason));
        if (px.hasRate()) {
            double advised = market.advisedPolicyRate(inflation), ruleSays = market.ruleRate(inflation);
            c.getChildren().add(cardLine("What the rule would set", pct2(ruleSays), null));
            if (Math.abs(ruleSays - advised) > 1e-9) {
                c.getChildren().add(cardLine(ruleSays > advised ? "...but the dial stops at" : "...but the dial cannot go below",
                        pct2(advised), null));
            }
            c.getChildren().add(cardLine("Inflation, year on year", signedPct1(inflation), null));
            c.getChildren().add(bandBar(inflation, target - .01, target, target + .01,
                    Math.abs(inflation - target) > .01 ? Palette.WARN : Palette.MONEY, 0, 10, true,
                    "Inflation " + signedPct1(inflation) + " against the target " + DebtManager.targetWords(target)
                    + " and a point either side of it"));
        }
        c.getChildren().add(muted(String.format("at %s it sets %s · at %s %s", DebtManager.targetWords(target),
                pct2(market.ruleRate(example)), DebtManager.targetWords(otherTarget), pct2(market.ruleRate(example, otherTarget)))
                + (px.hasRate() ? "" : " (on 5% inflation, as an example)")));
        c.getChildren().addAll(chips, ladder);
        return c;
    }

    /** P35, both layers. */
    String holdingsInfo() {
        DebtManager market = ui.game.getDebtManager();
        return "Buying makes money and bends the long rates down; selling destroys it. " + String.format(
                "Buying creates the money that pays for it and takes the paper off the bank's "
                + "book, then off the households' once the bank has none left to sell; the long "
                + "end of the curve bends down in proportion - at %.0f%% the term premium is "
                + "gone, and holding more takes no more off it. The thirty-year rate is %.2f "
                + "points lower for what it holds now. Selling destroys the money again, and "
                + "sells to the bank. From the treasury it buys only to replace what it holds of "
                + "a maturing piece, par for par, on top of what the market is sold and at the "
                + "market's price, as the Fed rolls its own. What it holds past its dial is repaid "
                + "instead, and so is what last year's surplus pays off. It never lends the treasury "
                + "new money that way: that is the advances' line. The book moves a quarter of the way to the dial "
                + "a month, from the next press.",
                CentralBank.FULL_COMPRESSION_SHARE * 100, market.compression(360) * 100);
    }

    /** THE CENTRAL BANK's holdings (R8): the share of the term paper it aims to hold, set at once; the thirty-year rate once the book has moved there (M9). */
    VBox holdingsCard(DebtManager market) {
        CentralBank cb = ui.game.getCentralBank();
        int settings = (int) Math.round(CentralBank.MAX_QE_SHARE * 10) + 1;
        String[] shares = new String[settings];
        for (int i = 0; i < settings; i++) shares[i] = (i * 10) + "%";
        Node chips = chipStrip(shares, Math.round(cb.getTargetShare() * 100) + "%", Palette.SIZE_CAPTION, picked -> {
            cb.setTargetShare(Integer.parseInt(picked.replace("%", "")) / 100.0);
            showPolicyMenu();
        });
        Ladder ladder = Ladder.of(0, CentralBank.MAX_QE_SHARE, HOLDINGS_STEP, PolicyScreen::pct0)
                .current(cb.getTargetShare())
                .appliesAtOnce(v -> { cb.setTargetShare(Math.round(v * 100) / 100.0); showPolicyMenu(); });
        DoubleFunction<List<Effect>> effects = holdingsEffects();
        return dialCard(new DialCard(Icons.SAFE, Palette.MONEY, "THE CENTRAL BANK: ITS HOLDINGS", holdingsInfo(),
                pct0(cb.getTargetShare()) + " of the term paper",
                "holds " + money(cb.getPaperHeld()) + ", " + pct0(market.centralBankShareOfTerm()) + " of it · set at once",
                List.of(chips), ladder, cb.getTargetShare(), effects, null, null, null), 360, true);
    }

    /** The holdings' effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> holdingsEffects() {
        DebtManager market = ui.game.getDebtManager();
        return v -> List.of(
                Effect.of("The thirty-year rate, once the book has moved", market.curveRate(360),
                        market.curveRateAtShare(360, v), Money::pct2).delta(Money::points));
    }

    /** P36. */
    static final String CEILING_INFO = String.format(
            "When the treasury runs dry the central bank advances the gap in money it "
            + "makes - printing - up to this many months of the treasury's revenue. Past "
            + "it, pensions, EI, health, the schools, wages and debts are still paid, and "
            + "everything else waits for cash and is owed as arrears. A higher ceiling "
            + "keeps the lights on longer and prints more doing it; the most is %.0f "
            + "months.", CentralBank.MAX_ADVANCES_CEILING);

    /** THE CENTRAL BANK's ceiling (R9): how far it will advance the treasury, set at once; the ceiling in money at another setting (M10); what is owed, red only when it binds (ceilingBound(): a verdict). */
    VBox ceilingCard() {
        CentralBank cb = ui.game.getCentralBank();
        String[] months = new String[CEILING_STEPS.length];
        for (int i = 0; i < CEILING_STEPS.length; i++) months[i] = CEILING_STEPS[i] + " months";
        Node chips = chipStrip(months, Math.round(cb.getAdvancesCeilingMonths()) + " months", Palette.SIZE_CAPTION, picked -> {
            cb.setAdvancesCeilingMonths(Integer.parseInt(picked.replace(" months", "")));
            showPolicyMenu();
        });
        Ladder ladder = Ladder.of(0, CentralBank.MAX_ADVANCES_CEILING, CEILING_STEP, PolicyScreen::monthsWords)
                .current(cb.getAdvancesCeilingMonths())
                .appliesAtOnce(v -> { cb.setAdvancesCeilingMonths(v); showPolicyMenu(); });
        DoubleFunction<List<Effect>> effects = ceilingEffects();
        HBox owed = cardLine("The treasury owes it", money(cb.getAdvancesToTreasury()),
                cb.ceilingBound() ? Palette.BAD : Palette.TEXT_HEAD);
        return dialCard(new DialCard(Icons.BANKNOTE, Palette.MONEY, "THE CENTRAL BANK: HOW FAR IT ADVANCES THE TREASURY",
                CEILING_INFO, monthsWords(cb.getAdvancesCeilingMonths()) + " of revenue = " + money(cb.ceiling()),
                "set at once", List.of(owed, chips), ladder, cb.getAdvancesCeilingMonths(), effects, null, null, null), 360, true);
    }

    /** The ceiling's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> ceilingEffects() {
        CentralBank cb = ui.game.getCentralBank();
        return v -> List.of(
                Effect.of("The ceiling", cb.ceiling(), cb.ceilingAt(v), PolicyScreen::amount).delta(PolicyScreen::moneyMove));
    }

    /** P30. */
    static final String SWING_INFO = "Prices here have more than doubled and come back at some point. "
            + "Wages, rents and every debt in the city were struck against "
            + "those levels as they passed, and the people who lived through "
            + "it did not get that back. Today's number does not show it.";
    /** P31, once the money has been reformed. */
    static final String REFORMED_INFO = "The index is measured against the FOUNDING basket in founding money, "
            + "and stays comparable across reforms precisely because a reform "
            + "divides its base too.";

    /**
     * THE PRICES (R4): the index since founding; where it has been - the
     * dearest and the cheapest, on a bar, today marked (Jerus, 2026-09-14:
     * "something as well that stores the highest price index and lowest") -
     * once the two marks have parted; the world's; the money's name.
     */
    VBox pricesCard(PriceIndex px) {
        WorldEconomy world = ui.game.getWorldEconomy();
        Denomination unit = ui.game.getDenomination();
        String info = "Today's level is one sample of a path that moves: cities that finish near founding prices "
                + "can have been to three times them and back, and those are not the same place to live. "
                + (px.swing() >= 2 ? SWING_INFO + " " : "") + (unit.getReforms() > 0 ? REFORMED_INFO : "");
        VBox c = card(cardHead(Icons.COIN, Palette.MONEY, "PRICES", info,
                figure(String.format("%.3f", px.getIndex()) + "× founding", Palette.SIZE_HEADING + 2, Palette.TEXT_HEAD)));
        if (px.swing() > 1.005) {
            List<Tick> ticks = List.of(
                    new Tick(px.getTrough(), Palette.TEXT_MUTED, 2, String.format("cheapest %.3f, m%,d", px.getTrough(), px.getTroughMonth()), null),
                    new Tick(px.getIndex(), Palette.MONEY, 3, null, "today " + String.format("%.3f", px.getIndex())),
                    new Tick(px.getPeak(), Palette.TEXT_MUTED, 2, String.format("dearest %.3f, m%,d", px.getPeak(), px.getPeakMonth()), null));
            c.getChildren().add(segmentBar(List.of(Segment.of(px.getIndex(), Palette.MONEY_LIGHT)), px.getPeak() * 1.1, ticks, 0, 8));
            c.getChildren().add(muted(String.format("swung %.2fx between its cheapest and its dearest", px.swing())));
        } else {
            c.getChildren().add(muted("The dearest and the cheapest it has been are still today's: no range yet."));
        }
        List<Node> lines = new ArrayList<>();
        if (world != null) lines.add(cardLine("The world's, likewise", String.format("%.3f", world.getPriceLevel()), null));
        if (unit.getReforms() > 0) {
            lines.add(cardLine("The money is the", unit.name(ui.game.getCurrency()), null));
            lines.add(cardLine("...one of which is", String.format("%,.0f founding", unit.getUnit()), null));
        }
        c.getChildren().addAll(lines);
        return c;
    }

    /* =====================================================================
       MONEY - the currency reform

       THE BUTTON EXISTS BEFORE IT WORKS, and that is deliberate. Jerus asked
       for it to "unlock past a threshold" and be the player's decision, which
       is also how it works in life: a currency reform is an act with a date on
       it, not something that happens to you. Below the threshold the page is
       shown with the reason on it rather than hidden - a control that appears
       from nowhere after two hundred years is a control nobody finds.

       Can the money be reformed yet, and what would it look like? Locked, a
       bar of prices against the unlock (0.7.36); open, the factor's chips and
       six price tags before and after - the shelf, the rent, the floor, the
       cash, a US dollar and what one new one is worth - and Apply.
       ===================================================================== */

    /** P37. */
    String reformInfo() {
        return "A reform issues a new " + ui.game.getCurrency().name() + " worth a round number of old "
                + "ones and restates every price, wage, balance and debt in the city at the "
                + "same moment. Nobody gains and nobody loses: the same wage buys the same "
                + "bread. It is what France did in 1960 and Turkey in 2005, and it is the "
                + "only thing in this game that is purely a change of units.";
    }

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
     * preview shows a before-and-after of things they know the price of,
     * and the wording is about ZEROS rather than about value.
     */
    void reformPage(VBox page) {
        Denomination unit = ui.game.getDenomination();
        double index = ui.game.getPriceIndex().getIndex();
        VBox head = card(cardHead(Icons.BANKNOTE, Palette.MONEY, "LOPPING THE ZEROS OFF", reformInfo(), null),
                line(firstWords(reformInfo()), reformInfo()),
                figure(String.format("%.2f× of %.0f×", index, Denomination.UNLOCK_AT), Palette.SIZE_LEAD, Palette.TEXT_HEAD),
                bulletBar(index, Denomination.UNLOCK_AT, Palette.MONEY, 0, "prices since founding, against where a reform unlocks"));
        page.getChildren().add(head);
        if (!ui.game.canReformCurrency()) {
            String notYet = String.format("The numbers are not hard to read yet. At %.0f times "
                    + "founding prices this becomes available, and until then a reform "
                    + "would be a change of units nobody asked for.", Denomination.UNLOCK_AT);
            head.getChildren().add(noteLine("Not yet: " + firstWords(notYet), notYet, 1100));
            return;
        }

        double factor = staged("reform", 0);
        FlowPane pick = new FlowPane(6, 6);
        for (double f : Denomination.FACTORS) {
            boolean can = unit.canLop(f);
            final double chosen = f;
            pick.getChildren().add(stepChip(String.format("%,.0f to 1", f),
                    () -> { if (can) { stage("reform", chosen); showPolicyMenu(); } },
                    !can || factor != f));
        }
        page.getChildren().add(sectionHead("HOW MANY OLD FOR ONE NEW", foreignInfo(), quiet(factor > 0
                ? "one new one for " + String.format("%,.0f", factor) + " of today's" : "pick one; nothing happens until you do")));
        page.getChildren().add(pick);
        if (factor <= 0) {
            page.getChildren().add(muted("Pick how many of today's " + ui.game.getCurrency().plural() + " one new one should "
                    + "be worth. Nothing happens until you do."));
            return;
        }
        ham.citybuildersim.sectors.Retail shops = ui.game.getSectors().retail();
        ham.citybuildersim.sectors.RealEstate landlords = ui.game.getSectors().realEstate();
        LabourMarket labour = ui.game.getLabourMarket();
        ForeignAccounts fx = ui.game.getForeignAccounts();
        page.getChildren().add(row(
                priceTag("A unit on the shelf", unitPrice(shops.getStoreSellPrice()), unitPrice(shops.getStoreSellPrice() / factor)),
                priceTag("Rent, per person housed", unitPrice(landlords.getRentPrice()), unitPrice(landlords.getRentPrice() / factor)),
                priceTag("The minimum wage", unitPrice(labour.cashMinimumWage()), unitPrice(labour.cashMinimumWage() / factor)),
                priceTag("The city's cash", money(ui.game.getCash()), money(ui.game.getCash() / factor)),
                // A RATE, NOT A SUM: local money per US dollar, so it is divided, never multiplied into dollars.
                priceTag("US$1 costs", fxRate(fx.getRate()), fxRate(fx.getRate() / factor)),
                priceTag("One new one is", "1 of today's", String.format("%,.0f of today's", factor))));
        String now = unit.name(ui.game.getCurrency()), then = afterName(unit, factor, ui.game.getCurrency());
        Pieces.ActionButton go = actionButton(Icons.BANKNOTE, Palette.MONEY, ACTION_TALL,
                new Press(Look.GO, "Issue the new " + ui.game.getCurrency().name(),
                        "the " + now + " becomes the " + then + ": one for " + String.format("%,.0f", factor)),
                () -> {
                    if (ui.game.reformCurrency(factor)) {
                        GameLog.note(String.format("Currency reform: one new %s for %,.0f old ones.",
                                ui.game.getCurrency().name(), factor));
                    }
                    dropProposal();
                    ui.redraw();
                });
        go.setMaxWidth(420);
        Pieces.ActionButton no = actionButton(Icons.CLOSE, Palette.TEXT_LABEL, ACTION_INLINE,
                new Press(Look.CHOOSE, "Leave it as it is", null), () -> { dropProposal(); ui.redraw(); });
        no.setMaxWidth(200);
        HBox foot = new HBox(10, go, no);
        foot.setAlignment(Pos.CENTER_LEFT);
        page.getChildren().add(foot);
    }

    /** P40. */
    String foreignInfo() {
        return "Everything foreign stays where it is. A debt owed in US dollars is still "
                + "owed in US dollars, and food bought abroad still costs abroad what it "
                + "always did - what changes is the number of " + ui.game.getCurrency().plural()
                + " it takes to buy one.";
    }

    /** One price tag: what it is, today, and after the reform in the accent. */
    static VBox priceTag(String what, String before, String after) {
        VBox c = card(words(what, Palette.SIZE_LABEL, Palette.TEXT_LABEL),
                words(before, Palette.SIZE_BODY + 1, Palette.TEXT_MUTED),
                words("→ " + after, Palette.SIZE_HEADING + 1, Palette.ACCENT));
        c.setSpacing(4);
        return c;
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

       Who pays for the pension, and can a pensioner live on it? (0.7.36) THE
       PENSION COVER - contributions against pensions paid, as a bullet bar -
       beside A PENSIONER HOUSEHOLD's month as a waterfall: what it has, what
       its rent and bills take, what is left against the basket. Then the two
       dials on their cards, the pension's with what one household would have
       (PolicyPreview.pensionerHasUnder(), M11).
       ===================================================================== */

    /** P41 and P47. */
    static final String COVER_INFO = "The rest is general revenue - the same pot the schools and the hospitals "
            + "come out of, which is what makes an ageing city a budget problem before "
            + "it is anything else. Raising the contribution closes the gap out of workers' pay, and a worker "
            + "short of money stops at the shop rather than at the landlord - so it "
            + "arrives as hunger somewhere else. Cutting the pension closes it out of "
            + "the seniors, who have no other income at all. There is no setting where "
            + "nobody pays; the screen is which of them does.";

    /** What a household's month means, in one of three forms (0.7.27, P42): its chip's word, its sentence and its verdict colour. */
    record Month(String word, String says, String tone) { }

    /** A pensioner household's month (pure: the probe reads it). */
    Month pensionerMonth() {
        HouseholdBalance bal = ui.game.getHouseholdBalance();
        int retired = HouseholdAccounts.RETIRED;
        double left = bal.getAfterFixed(retired), basket = bal.getSubsistence(retired);
        if (bal.isGoingShort(retired)) {
            return new Month("going short", "They are eating less than they need, and that is in the sick rate.", Palette.BAD_TEXT);
        }
        if (left < basket) {
            double drawn = bal.getDrawn(retired), borrowed = bal.getBorrowed(retired);
            return new Month("eating out of savings", String.format("The pension does not cover the basket: they are eating out of savings "
                    + "(%s drawn this month%s).", moneyFull(drawn), borrowed > 0 ? ", " + moneyFull(borrowed) + " borrowed" : ""),
                    Palette.WARN);
        }
        return new Month("covered", "The pension covers rent, bills and the basket.", Palette.GOOD);
    }

    void pensionPage(VBox page) {
        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        HouseholdAccounts hh = ui.game.getHouseholds();
        HouseholdBalance bal = ui.game.getHouseholdBalance();
        double collected = em.getContributions(), paid = em.getPensionsPaid();

        VBox cover = card(cardHead(Icons.CANE, Palette.MONEY, "THE PENSION COVER", COVER_INFO, null),
                figure(paid > 0 ? "covered " + pct0(em.getPensionCoverage()) : "no pension paid", Palette.SIZE_LEAD, Palette.TEXT_HEAD),
                bulletBar(collected, paid, Palette.MONEY, 0, "contributions " + money(collected) + " against pensions paid "
                        + money(paid) + " (the tick)"),
                cardLine("From general revenue, a month", money(em.getPensionShortfall()), null),
                cardLine(people(em.getSeniors()) + " seniors, each", moneyFull(policy.pensionPerSenior()) + " a month", null));

        int retired = HouseholdAccounts.RETIRED;
        VBox household;
        if (hh.getRowHouseholds(retired) >= .5) {
            /*
             * ONE HOUSEHOLD, NOT ONE CITY: every figure with its every digit,
             * where a city ledger prints "$0" below half a thousand. AND ONE
             * SOURCE (0.7.27): what the household had to spend, what its rent
             * and bills took and what was left for the shop are all its own
             * ledger's, so the three foot.
             */
            double has = bal.getDisposable(retired), fixed = bal.getFixedCosts(retired);
            double left = bal.getAfterFixed(retired), basket = bal.getSubsistence(retired);
            Month m = pensionerMonth();
            List<Step> steps = List.of(
                    Step.total("has to spend", has, Palette.MONEY),
                    Step.of("rent and bills", -fixed, Palette.RAMP_REST),
                    Step.total("left for the shop", left, Palette.MONEY_DARK));
            household = card(cardHead(Icons.HOMES, Palette.MONEY, "A PENSIONER HOUSEHOLD",
                            "One household of the retired row, from its own ledger: what it had to spend this month, what "
                            + "its rent, fees and interest took, and what was left for the shop - against a basket for "
                            + "everybody in it. " + m.says(), chip(m.word(), m.tone())),
                    waterfall(steps, Money::moneyFull, 0, 120),
                    bulletBar(left, basket, Palette.MONEY_DARK, 0, "left " + moneyFull(left) + " against a basket costing "
                            + moneyFull(basket) + " (the tick)"),
                    words(m.says(), Palette.SIZE_LABEL + 1, m.tone()));
        } else {
            household = card(cardHead(Icons.HOMES, Palette.MONEY, "A PENSIONER HOUSEHOLD", null, null),
                    muted("No pensioner household in the city this month."));
        }
        page.getChildren().add(row(cover, household));
        page.getChildren().add(row(contributionCard(em, policy), pensionDialCard(em, policy, bal)));
    }

    /** WHAT WORKERS PAY IN (P-4): the contribution, its own Apply; collected, covered, out of the treasury and a payslip before and after (M3: struck on the wage bill, so a dial at nothing previews something - B9). */
    VBox contributionCard(EconomyManager em, TaxPolicy policy) {
        double contribution = policy.getContributionRate();
        Ladder ladder = ownLadder("contrib", contribution, 0, TaxPolicy.MAX_CONTRIBUTION, .0025, Money::pct2);
        double want = staged("contrib", contribution);
        DoubleFunction<List<Effect>> effects = contributionEffects(policy);
        return dialCard(new DialCard(Icons.STAFF, Palette.MONEY, "WHAT WORKERS PAY IN",
                "Off every wage in the city, before the worker sees it - the pension contribution, charged on the same "
                + "payroll as the wage tax and the two premiums.", pct2(contribution) + " of every wage",
                "Off every wage in the city, before the worker sees it.", null, ladder, want, effects,
                "Against last month's payroll.", "Against last month's payroll, which is the base it is charged on. " + CAVEAT,
                applyFoot("contrib", "Set contributions to " + pct2(want), () -> policy.setContributionRate(want))), 380, true);
    }

    /** The contribution's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> contributionEffects(TaxPolicy policy) {
        EconomyManager em = ui.game.getEconomyManager();
        double contribution = policy.getContributionRate();
        return v -> {
            TaxPolicy after = with("contrib", v);
            return List.of(
                    Effect.of("Collected a month", em.contributionsAt(contribution), em.contributionsAt(v), PolicyScreen::amount)
                            .delta(PolicyScreen::moneyMove),
                    Effect.of("Covered", PolicyPreview.pensionCoverageUnder(ui.game, policy),
                            PolicyPreview.pensionCoverageUnder(ui.game, after), PolicyScreen::pct0).scale(1),
                    Effect.of("Out of the treasury, a month", PolicyPreview.pensionGapUnder(ui.game, policy),
                            PolicyPreview.pensionGapUnder(ui.game, after), PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                    Effect.of("A payslip loses, at the wage rate", policy.payslipShare(null), after.payslipShare(null), Money::pct2)
                            .delta(Money::points));
        };
    }

    /** WHAT SENIORS RECEIVE (P-5): the pension, of the FOUNDING unskilled wage (B10, D9), its own Apply; each senior, paid, out of the treasury and what one pensioner household would have. */
    VBox pensionDialCard(EconomyManager em, TaxPolicy policy, HouseholdBalance bal) {
        double replacement = policy.getPensionReplacement();
        Ladder ladder = ownLadder("pension", replacement, 0, TaxPolicy.MAX_REPLACEMENT, .01, PolicyScreen::pct0);
        double want = staged("pension", replacement);
        DoubleFunction<List<Effect>> effects = pensionEffects(policy);
        return dialCard(new DialCard(Icons.CANE, Palette.MONEY, "WHAT SENIORS RECEIVE",
                "Of the founding unskilled wage, paid flat to every senior in the city: " + pct0(replacement) + " of "
                + moneyFull(policy.pensionPerSeniorAt(1)) + " a month. The founding wage is carried in today's money and "
                + "does not move with the labour market, so the pension is frozen in real terms - a design question "
                + "the model has filed and not yet answered.",
                pct0(replacement) + " of the founding unskilled wage",
                "Of the founding unskilled wage, paid flat to every senior in the city.", null, ladder, want, effects,
                "Against today's number of seniors.", "Against today's number of seniors. " + CAVEAT,
                applyFoot("pension", "Set the pension to " + pct0(want), () -> policy.setPensionReplacement(want))), 380, true);
    }

    /** The pension's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> pensionEffects(TaxPolicy policy) {
        EconomyManager em = ui.game.getEconomyManager();
        HouseholdBalance bal = ui.game.getHouseholdBalance();
        return v -> {
            TaxPolicy after = with("pension", v);
            List<Effect> out = new ArrayList<>();
            out.add(Effect.of("Each senior gets, a month", policy.pensionPerSenior(), after.pensionPerSenior(), Money::moneyFull)
                    .delta(PolicyScreen::moneyMoveFull));
            out.add(Effect.of("Paid altogether", em.getPensionsPaid(), em.pensionsPaidUnder(after), PolicyScreen::amount)
                    .delta(PolicyScreen::moneyMove));
            out.add(Effect.of("Out of the treasury, a month", PolicyPreview.pensionGapUnder(ui.game, policy),
                    PolicyPreview.pensionGapUnder(ui.game, after), PolicyScreen::amount).delta(PolicyScreen::moneyMove));
            if (ui.game.getHouseholds().getRowHouseholds(HouseholdAccounts.RETIRED) >= .5) {
                out.add(Effect.of("A pensioner household has", bal.getDisposable(HouseholdAccounts.RETIRED),
                        PolicyPreview.pensionerHasUnder(ui.game, after), Money::moneyFull).delta(PolicyScreen::moneyMoveFull)
                        .info("What one household of the retired row would have to spend, its income being the pension: "
                                + "the pension bill moved and split across its households by the month's own rule. What it "
                                + "would have left after rent and bills is not projected - its interest is struck on its income."));
            }
            return out;
        };
    }

    /* =====================================================================
       PROMISES - the out of work and the students (2026-09-11)

       TWO DIALS, AND BOTH ARE THE PENSION'S SHAPE AGAIN. Jerus: EI "like
       CPP" - a premium off every wage, a benefit to whoever lost a job, and
       the treasury carrying the gap. Defaults are the real 2026 figures:
       1.63% and 55%. The students' grant is on the Schools page, with the
       tuition price and the loan's rate it is decided against; only its cost
       stays on this page's books.

       What does EI cost, and who draws it? (0.7.36) THE EI COVER - the
       premiums against what EI paid - and the two dials, the benefit struck
       on the pool as it stands (Unemployment.benefitsAt(), M5: it scaled
       the bill the treasury paid at the top of the month, B11).
       ===================================================================== */

    /** P57. */
    static final String EI_INFO = "EI only pays the first twelve months, so a long bust costs less in EI than "
            + "a short one and more in everything else. Student loans are not on this "
            + "line: they are lent, and graduates pay them back - with interest, if the "
            + "Schools page charges any.";

    /** Money a line takes out of the treasury, a surplus with a true minus: "$2.5M", "−$1.0M". */
    static String cost(double v) {
        return v < 0 && !"$0".equals(money(-v)) ? "−" + money(-v) : money(Math.max(0, v));
    }

    void outOfWorkPage(VBox page) {
        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        Unemployment u = ui.game.getUnemployment();
        FamilyModel families = ui.game.getFamilies();
        PolicyPreview.Promises p = PolicyPreview.promises(ui.game);
        double collected = em.getEiPremiums(), paid = em.getEiBenefits();
        double students = families.getSeekers(FamilyModel.Seeker.STUDENT);

        VBox cover = card(cardHead(Icons.STAFF, Palette.MONEY, "THE EI COVER", EI_INFO, null),
                figure(paid > 0 ? "premiums cover " + pct0(em.getEiCoverage()) : "nobody drew EI", Palette.SIZE_LEAD, Palette.TEXT_HEAD),
                bulletBar(collected, paid, Palette.MONEY, 0, "premiums " + money(collected) + " against EI paid "
                        + money(paid) + " (the tick)"),
                cardLine(people(u.onEi()) + " on EI", "12 months a claim", null),
                cardLine("Insured to", moneyFull(u.getInsuredCap()) + " a month", null),
                cardLine("EI past its premiums, a month", money(p.eiPastPremiums()), null),
                cardLine("Grants to " + people(students) + " students", money(p.grants()), null),
                doorPill("A student is granted " + grantWords(policy.getGrantBasis(), policy.getGrantAmount()) + " · Schools",
                        Icons.EDUCATION, Palette.MONEY, () -> open(PROMISES, "Schools")));
        page.getChildren().add(cover);
        page.getChildren().add(row(eiPremiumCard(em, policy), eiBenefitCard(em, policy, u)));
    }

    /** What workers pay for EI (E-3), its own Apply: raised, and what EI and the grants take from the treasury. */
    VBox eiPremiumCard(EconomyManager em, TaxPolicy policy) {
        double premium = policy.getEiPremiumRate(), benefit = policy.getEiBenefitRate();
        Ladder ladder = ownLadder("eiPremium", premium, 0, TaxPolicy.MAX_EI_PREMIUM, .0005, Money::pct2);
        double want = staged("eiPremium", premium);
        DoubleFunction<List<Effect>> effects = eiPremiumEffects(policy);
        return dialCard(new DialCard(Icons.COIN, Palette.MONEY, "WHAT WORKERS PAY FOR EI",
                "Off every wage in the city, beside the pension contribution.", pct2(premium) + " of every wage",
                "Off every wage in the city, beside the pension contribution.", null, ladder, want, effects,
                "Against last month's payroll.", "Against last month's payroll. " + CAVEAT,
                applyFoot("eiPremium", "Set the EI premium to " + pct2(want), () -> policy.setEiPremiumRate(want))), 380, true);
    }

    /** The EI premium's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> eiPremiumEffects(TaxPolicy policy) {
        EconomyManager em = ui.game.getEconomyManager();
        double premium = policy.getEiPremiumRate(), benefit = policy.getEiBenefitRate();
        return v -> List.of(
                Effect.of("Raised a month", em.premiumAt(premium), em.premiumAt(v), PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                Effect.of("EI and the grants, less the premiums", PolicyPreview.eiTreasuryAt(ui.game, premium, benefit),
                        PolicyPreview.eiTreasuryAt(ui.game, v, benefit), PolicyScreen::cost).delta(PolicyScreen::moneyMove));
    }

    /** What EI replaces (E-3), its own Apply: the pool's bill, a claimant's cheque and the treasury's share, struck on the pool (M5). */
    VBox eiBenefitCard(EconomyManager em, TaxPolicy policy, Unemployment u) {
        double premium = policy.getEiPremiumRate(), benefit = policy.getEiBenefitRate();
        Ladder ladder = ownLadder("eiBenefit", benefit, 0, TaxPolicy.MAX_EI_BENEFIT, .01, PolicyScreen::pct0);
        double want = staged("eiBenefit", benefit);
        DoubleFunction<List<Effect>> effects = eiBenefitEffects(policy);
        String status = "Of the wage a claimant lost, up to the insured maximum of " + moneyFull(u.getInsuredCap()) + " a month.";
        return dialCard(new DialCard(Icons.STAFF, Palette.MONEY, "WHAT EI REPLACES",
                status + " The bill is struck on the claimants as they stand - every cohort's insured wage, capped, at the "
                + "rate - which is what the treasury pays at the top of next month.",
                pct0(benefit) + " of the wage they lost, for twelve months", status, null, ladder, want, effects,
                "Against today's claimants.", "Against today's claimants. " + CAVEAT,
                applyFoot("eiBenefit", "Set EI to " + pct0(want), () -> policy.setEiBenefitRate(want))), 380, true);
    }

    /** The EI benefit's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> eiBenefitEffects(TaxPolicy policy) {
        Unemployment u = ui.game.getUnemployment();
        double premium = policy.getEiPremiumRate(), benefit = policy.getEiBenefitRate();
        return v -> List.of(
                Effect.of("Paid a month, on the pool", u.benefitsAt(benefit), u.benefitsAt(v), PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                Effect.of("A claimant's cheque, on average", u.benefitPerClaimantAt(benefit), u.benefitPerClaimantAt(v), Money::moneyFull)
                        .delta(PolicyScreen::moneyMoveFull),
                Effect.of("EI and the grants, less the premiums", PolicyPreview.eiTreasuryAt(ui.game, premium, benefit),
                        PolicyPreview.eiTreasuryAt(ui.game, premium, v), PolicyScreen::cost).delta(PolicyScreen::moneyMove));
    }

    /* =====================================================================
       PROMISES - the clinic's price, and a premium (2026-09-19)

       Jerus: "healthcare should be an adjustable price, all the way to even
       make it a profitable business or the option to make it an obligatory
       insurance payment system." Two dials, in the EI premium's shape: a
       scale on the three care fees, and a premium off every wage. Between
       them the page has three corners, and a chip says which one the player
       is standing in: free at the point of use and paid from taxes (fees 0,
       premium 0), a business (fees past the break-even), insurance (fees 0,
       a premium).

       AND THE PRICE HAS A COST, which is what makes the fee dial a decision
       rather than a revenue line: a household that cannot pay the fee after
       its savings, its shares and its credit goes without care rather than
       without food, and the page says how many did this month and what it
       did to coverage. See Household.affordCare() and Healthcare.

       Which corner is the city in, and who pays for care? (0.7.36) WHO PAYS
       FOR CARE as one bar of the service's cost in its four payers, the
       turned-away as three cells (red is a verdict there), and the two dials
       - the fee's "treasury's share" struck at full service both sides (B13).
       ===================================================================== */

    /** The corner the city is in (P62): its chip's word, and its sentence. */
    record Corner(String word, String says) { }

    Corner corner() {
        TaxPolicy policy = policy();
        double scale = policy.getHealthFeeScale(), premium = policy.getHealthPremiumRate();
        double breakEven = ui.game.getHealthcare().breakEvenScale();
        if (scale <= 0 && premium <= 0) {
            return new Corner("free at the point of use", "Free at the point of use, paid from taxes: nobody pays at the door and "
                    + "nobody pays a premium, so the whole cost of the service is the treasury's.");
        } else if (scale <= 0) {
            return new Corner("insurance", "Insurance: care is free at the door and every wage pays a premium for it. "
                    + "What the premium does not cover is the treasury's, and what it raises "
                    + "past the cost is the treasury's too.");
        } else if (breakEven > 0 && scale >= breakEven) {
            return new Corner("a business", "A business: at this scale the fees on the people treated meet the "
                    + "service's whole cost, and past it the clinics turn a profit - and turn "
                    + "away whoever cannot pay.");
        } else if (premium > 0) {
            return new Corner("a mix", "A mix: patients pay something at the door, every wage pays a premium, "
                    + "and the treasury carries the rest.");
        }
        return new Corner("fee-funded, in part", "Fee-funded, in part: patients pay at the door and the treasury carries "
                + "the rest. A household that cannot pay after its savings, its shares and "
                + "its credit goes without care rather than without food.");
    }

    /** P63's line: what the fees and the premium cover - or that the service cost nothing (B19: it read "Fees cover 0% of the cost" in a city with fees and no cost). */
    String coverWords() {
        Healthcare care = ui.game.getHealthcare();
        double gross = care.getGrossCost(), raised = ui.game.getEconomyManager().getHealthPremiums();
        if (gross <= 0) return "The service cost nothing this month: there are no staff or buildings to pay, and what the fees "
                + "and funerals bring in is the treasury's.";
        return String.format("Fees cover %.0f%% of the cost%s.", care.getCostRecovery() * 100,
                raised > 0 ? String.format(" and the premium another %.0f%%", raised / gross * 100) : "");
    }

    void healthPage(VBox page) {
        EconomyManager em = ui.game.getEconomyManager();
        Healthcare care = ui.game.getHealthcare();
        TaxPolicy policy = em.getTaxPolicy();
        Corner corner = corner();
        double gross = care.getGrossCost(), fees = care.getTreatmentFees(), funerals = care.getFuneralFees();
        double raised = em.getHealthPremiums(), treasury = PolicyPreview.careTreasuryShare(ui.game);

        List<Slice> parts = List.of(
                new Slice("Treatment fees", fees, Palette.MONEY),
                new Slice("Funeral fees", funerals, Palette.MONEY_LIGHT),
                new Slice("The health premium", raised, Palette.MONEY_DARK),
                new Slice("The treasury", Math.max(0, treasury), Palette.RAMP_REST));
        String whole = coverWords() + " A ward is paid for whether or not its patients "
                + "are, so the cost does not move with the price; only who pays it does.";
        HBox cornerChip = new HBox(Palette.GAP_TIGHT, chip(corner.word(), Palette.TEXT_LABEL), infoButton(corner.says(), true));
        cornerChip.setAlignment(Pos.CENTER_LEFT);
        FlowPane key = new FlowPane(16, 4);
        for (Slice s : parts) key.getChildren().add(keySwatch(s.colour(), s.name() + " " + money(s.amount())));
        page.getChildren().add(card(cardHead(Icons.HEALTH, Palette.MONEY, "WHO PAYS FOR CARE", whole, cornerChip),
                figure(money(gross) + " a month", Palette.SIZE_LEAD, Palette.TEXT_HEAD),
                muted("what the clinics' staff and buildings cost this month"),
                sliceBar(parts), key,
                cardLine("The treasury's share", cost(treasury), null),
                noteLine(coverWords(), whole, 1100)));

        page.getChildren().add(sectionHead("WHO WAS TURNED AWAY AT THE DOOR", turnedAwayInfo(), quiet(
                care.getPricedOutTotal() > 0 ? people(care.getPricedOutTotal()) + " priced out of care this month"
                        : "nobody priced out this month")));
        List<Node> cells = new ArrayList<>();
        for (CareType kind : new CareType[] {CareType.GENERAL, CareType.CHILDCARE, CareType.SENIOR}) {
            double out = care.getPricedOut(kind);
            cells.add(card(words(kind.getLabel(), Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL),
                    figure(people(out), Palette.SIZE_LEAD, out > 0 ? Palette.BAD : Palette.TEXT_HEAD),
                    muted(out > 0 ? String.format("priced out - coverage cut to %.0f%% of what the beds could do",
                            care.getAffordability(kind) * 100) : "nobody priced out")));
        }
        page.getChildren().add(row(cells.toArray(new Node[0])));
        page.getChildren().add(row(feeCard(care, policy), healthPremiumCard(em, care, policy)));
    }

    /** P64, both layers. */
    String turnedAwayInfo() {
        Healthcare care = ui.game.getHealthcare();
        return care.getPricedOutTotal() > 0
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
                        + "the clinic first.";
    }

    /** WHAT A PATIENT PAYS (S-4), its own Apply: the break-even marked on the dial, the three fees, the fees at full and the treasury's share - at full service both sides (B13). */
    VBox feeCard(Healthcare care, TaxPolicy policy) {
        double scale = policy.getHealthFeeScale(), premium = policy.getHealthPremiumRate();
        double breakEven = care.breakEvenScale();
        // A tenth of the founding fee a step: 150 steps from 0 to 15, few enough for the keys to walk, fine
        // enough to land on a break-even quoted to a tenth.
        Ladder ladder = ownLadder("healthFee", scale, 0, TaxPolicy.MAX_HEALTH_FEE_SCALE, .1, PolicyScreen::times);
        if (breakEven > 0 && breakEven <= TaxPolicy.MAX_HEALTH_FEE_SCALE) {
            ladder.marks(new double[] {breakEven}, new String[] {"break-even " + times(breakEven)});
        }
        double want = staged("healthFee", scale);
        DoubleFunction<List<Effect>> effects = feeEffects(policy);
        String status = "The founding fee times this, on general care, childcare and senior care."
                + (breakEven > 0 ? String.format(" This city's fees would meet its whole cost at x%.2f%s.", breakEven,
                        breakEven > TaxPolicy.MAX_HEALTH_FEE_SCALE ? ", beyond the dial's reach" : "") : "");
        String caveat = (breakEven > 0 && want >= breakEven ? "Past this city's break-even: a business. "
                : want <= 0 ? "Free at the point of use. " : "")
                + "Against this month's patients, and that is the half this preview cannot "
                + "do: a dearer fee turns some of them away, and they leave the bill and the "
                + "clinic together.";
        return dialCard(new DialCard(Icons.HEALTH, Palette.MONEY, "WHAT A PATIENT PAYS",
                status + " Funeral fees are not scaled: the cemetery against the crematorium is its own design.",
                times(scale) + " the founding fee", status, null, ladder, want, effects,
                "Who it turns away is not projected.", caveat + " " + CAVEAT,
                applyFoot("healthFee", "Set the fee to " + times(want), () -> policy.setHealthFeeScale(want))), 380, true);
    }

    /** The care fee's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> feeEffects(TaxPolicy policy) {
        Healthcare care = ui.game.getHealthcare();
        double scale = policy.getHealthFeeScale(), premium = policy.getHealthPremiumRate();
        return v -> {
            List<Effect> out = new ArrayList<>();
            for (CareType kind : new CareType[] {CareType.GENERAL, CareType.CHILDCARE, CareType.SENIOR}) {
                out.add(Effect.of(kind.getLabel() + ", a head a month", care.feeNow(kind), care.feeAtOne(kind) * v, Money::moneyFull));
            }
            out.add(Effect.of("Fees a month, if everybody paid", care.fullTreatmentFees(), care.treatmentFeesAtOne() * v, PolicyScreen::amount)
                    .delta(PolicyScreen::moneyMove));
            out.add(Effect.of("The treasury's share", PolicyPreview.careTreasuryShareAt(ui.game, scale, premium),
                    PolicyPreview.careTreasuryShareAt(ui.game, v, premium), PolicyScreen::cost).delta(PolicyScreen::moneyMove)
                    .info("What the treasury would pay towards care a month with every patient paying the fee, at the city's "
                            + "scale and at this one: struck at full service on both sides, so the move is the dial's and "
                            + "not who was priced out this month."));
            return out;
        };
    }

    /** WHAT EVERY WAGE PAYS (S-5), its own Apply: raised, and the treasury's share. */
    VBox healthPremiumCard(EconomyManager em, Healthcare care, TaxPolicy policy) {
        double scale = policy.getHealthFeeScale(), premium = policy.getHealthPremiumRate();
        double wages = em.getWageBill(), gross = care.getGrossCost();
        Ladder ladder = ownLadder("healthPremium", premium, 0, TaxPolicy.MAX_HEALTH_PREMIUM, .0005, Money::pct2);
        double want = staged("healthPremium", premium);
        DoubleFunction<List<Effect>> effects = healthPremiumEffects(policy);
        String status = "Off every wage in the city, employee side, beside the EI premium - into the "
                + "treasury as revenue against the service's cost. No employer share, and "
                + "nothing balances it: a shortfall is the treasury's and so is a surplus.";
        return dialCard(new DialCard(Icons.COIN, Palette.MONEY, "WHAT EVERY WAGE PAYS", status,
                pct2(premium) + " of every wage", firstWords(status), null, ladder, want, effects,
                "Against this month's payroll.", (gross > 0 && wages > 0
                        ? String.format("Against this month's payroll. The whole cost is %.2f%% of it.", gross / wages * 100)
                        : "Against this month's payroll.") + " " + CAVEAT,
                applyFoot("healthPremium", "Set the health premium to " + pct2(want), () -> policy.setHealthPremiumRate(want))),
                380, true);
    }

    /** The health premium's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> healthPremiumEffects(TaxPolicy policy) {
        EconomyManager em = ui.game.getEconomyManager();
        double scale = policy.getHealthFeeScale(), premium = policy.getHealthPremiumRate();
        return v -> List.of(
                Effect.of("Raised a month", em.premiumAt(premium), em.premiumAt(v), PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                Effect.of("The treasury's share", PolicyPreview.careTreasuryShareAt(ui.game, scale, premium),
                        PolicyPreview.careTreasuryShareAt(ui.game, scale, v), PolicyScreen::cost).delta(PolicyScreen::moneyMove));
    }

    /* =====================================================================
       PROMISES - the schools: the price of a place, who pays it, and the
       loan that covers the rest (2026-09-21; the tuition page until then)

       THE SUBSIDY DOES TWO OPPOSITE THINGS AND THAT IS THE DECISION. Every
       point of subsidy is tuition the city forgoes and a few more people who
       can afford to enrol - and the second effect is far larger than the
       first, because tuition is small against what a school costs to run. So
       this is not "how much do we spend on education", it is "who is allowed
       to go", and the budget line is the smaller half of the answer.

       AND SINCE 2026-09-21 THE PRICE IS A DIAL TOO, WITH THE GRANT AND THE
       LOAN BESIDE IT. Jerus: "grants its just a menu where you can choose
       between a fixed amount, or a percentage of last month's surplus, or a
       % as it is now of living costs, or a % of tuition. and then another
       slider which is the interest rate for the student loans ... and also
       make it so that you can tweak the price of tuition as well." The dials
       are one decision: what a seat costs, what share of that the city
       forgives, what a student is handed to live on, and what the loan for
       the rest costs them afterwards. All stage into the tray and one press
       applies them; the previews are the model's own rules
       (Education.billedAt, Game.studentGrantBillUnder,
       HouseholdBalance.studentInterestAt) asked about a setting the city has
       not made. AND SINCE 0.7.6 THE PRICE IS ONE PER KIND OF SCHOOL (Jerus:
       "raise the price for a specific university, and beside the dial it
       shows the current space, the current students, and the current cost
       and revenue").

       Who can afford a place, and what does the city forgo? (0.7.36) WHO CAN
       AFFORD A PLACE: the nine courses, each a bar of what a place costs a
       family as a share of a month's wage, red past Education.MAX_BURDEN
       where nobody enrols (a verdict: the model's line), the staged price as
       a ghost, each opening into its own price. Then the share, the schools'
       month - which foots now: the share the city waives is revenue it does
       not collect, not a cost (the spec's B17, D13) - and every school's
       price; then the grant and the loan.

       At no subsidy a university place costs a diploma-holder most of a
       month's pay and almost nobody attends: a city can own the buildings,
       need the graduates, and produce none of them. At the founding price
       against today's wages that trap is mostly quiet; around three times
       it, it is back.
       ===================================================================== */

    /** The staged key of "Every school at once" on the Schools page - the one tuition scale's key, which is what that scale became in 0.7.6. */
    static final String EVERY_SCHOOL = "tuitionScale";

    /** One step of every price-of-a-place ladder: a twentieth of the founding table. */
    static final double TUITION_STEP = .05;

    /** The staged key of one school kind's own price (0.7.6): "tuitionScale:UNIVERSITY". */
    static String schoolKey(EducationType kind) { return EVERY_SCHOOL + ":" + kind.name(); }

    /** A tuition scale as the page writes it: "x1.00". */
    static String scaleWords(double scale) { return times(scale); }

    /** The grant in words, for a line that names it: "15% of an unskilled wage a month". */
    static String grantWords(TaxPolicy.GrantBasis basis, double amount) {
        if (basis == null) basis = TaxPolicy.DEFAULT_GRANT_BASIS;
        return switch (basis) {
            case FIXED         -> moneyFull(amount) + " a month at founding prices, kept up with the cost of living";
            case SURPLUS_SHARE -> String.format("%.0f%% of last month's surplus, shared out", amount * 100);
            case TUITION_SHARE -> String.format("%.0f%% of their course's tuition", amount * 100);
            default            -> String.format("%.0f%% of an unskilled wage a month", amount * 100);
        };
    }

    /** What a basis is called on its chip. */
    static String basisName(TaxPolicy.GrantBasis basis) {
        return switch (basis) {
            case FIXED         -> "a fixed amount, kept up with prices";
            case SURPLUS_SHARE -> "a share of last month's surplus";
            case TUITION_SHARE -> "a share of tuition";
            default            -> "a share of the unskilled wage";
        };
    }

    /** How a basis's amount reads on its dial: dollars, or a percentage of the thing it is a share of. */
    static String amountWords(TaxPolicy.GrantBasis basis, double amount) {
        return basis == TaxPolicy.GrantBasis.FIXED ? moneyFull(amount) + " at founding prices"
                : String.format("%.0f%%", amount * 100);
    }

    /** P48. */
    static final String SHARE_INFO = "The city's share of every course fee. Households pay the rest out of a "
            + "month's wages, and nobody enrols in a course that costs more than they "
            + "can carry - so this number decides attendance far more than it decides "
            + "the budget. What the city waives is tuition it does not collect - revenue forgone, not money out "
            + "of the treasury.";

    /** P49. */
    static final String BURDEN_INFO = String.format("At %.0f%% of a month's wage nobody enrols at all: a red bar is a "
            + "course the city offers and nobody can take. Each bar is a place's fee at its price, less the city's "
            + "share, against the best wage somebody who could take the course earns - the unskilled job's for a "
            + "course anybody can take.", Education.MAX_BURDEN * 100);

    /** P50. */
    static final String PRICE_INFO = "The founding tuition table times this, before the city's share comes off. "
            + "The table was struck against the wages of its day; against today's the "
            + "trap is quiet at x1 and back around x3. Nothing at 0: a "
            + "free place, and the city forgoes nothing because there is nothing to "
            + "forgo. This dial sets every school at once; each kind has its "
            + "own (open its row above), and this one puts them back to one price.";

    /** P51/P52: the schools' preview caveat. */
    static final String SCHOOL_CAVEAT = "Against the courses being taken now, each kind's students at its own "
            + "price - and that is the half this preview cannot do, because the point "
            + "of these dials is to change who enrols. A cheaper place fills a course, "
            + "and the bill arrives with the students.";

    /** The scale a preview prices one kind of school at: its own lever if staged, else every school at once if that is, else what it is (0.7.6). */
    double stagedScaleOf(TaxPolicy policy, EducationType kind) {
        return staged(schoolKey(kind), staged(EVERY_SCHOOL, policy.tuitionScaleOf(kind)));
    }

    void schoolsPage(VBox page) {
        Education schools = ui.game.getEducation();
        TaxPolicy policy = policy();
        double share = schools.getTuitionSubsidy();
        double[] places = ui.game.getBuildingManager().getBuiltEducationPlaces();
        boolean anyStanding = false;
        for (EducationType kind : schoolKinds()) anyStanding |= places[kind.ordinal()] > 0;

        // Every dial on the page registered first, open or not, so the tray can name and apply them all.
        Lever shareLever = register(new Lever("tuition", "The city's share of tuition", share, 0, 1, .01,
                PolicyScreen::pct0, schools::setTuitionSubsidy));
        Lever everySchool = register(new Lever(EVERY_SCHOOL, "The price of a place, every school", policy.getTuitionScale(),
                0, TaxPolicy.MAX_TUITION_SCALE, TUITION_STEP, PolicyScreen::scaleWords, policy::setTuitionScale));
        for (EducationType kind : schoolKinds()) register(kindLever(kind, policy));
        Lever loanLever = register(new Lever("loanRate", "Interest on student loans", policy.getStudentLoanRate(),
                0, TaxPolicy.MAX_STUDENT_LOAN_RATE, .0025, Money::pct2, policy::setStudentLoanRate));

        if (!anyStanding) {
            HBox banner = statusBanner("No school yet", Palette.TEXT_LABEL,
                    "No school is standing yet. The dials still set the price, the share and the grant for the first one.",
                    Icons.EDUCATION);
            banner.getChildren().add(doorPill("Build · Education", Icons.BUILD, Palette.BUILDING,
                    () -> ui.buildScreen.openCategory(BuildAdvice.EDUCATION)));
            page.getChildren().add(banner);
        }

        page.getChildren().add(sectionHead("WHO CAN AFFORD A PLACE", BURDEN_INFO,
                quiet("a place's fee after the city's share, against a month's wage · click a row for its price")));
        page.getChildren().add(burdenRows(schools, policy, places));

        page.getChildren().add(row(shareCard(shareLever, schools, policy), schoolsMonthCard(schools),
                everySchoolCard(everySchool, policy)));
        page.getChildren().add(row(grantCard(policy), loanCard(loanLever, policy)));
    }

    /** The nine kinds a school can be, in EducationType order: everything bar NONE (TaxPolicy's own list, which it keeps to itself). */
    static List<EducationType> schoolKinds() {
        List<EducationType> out = new ArrayList<>();
        for (EducationType k : EducationType.values()) if (k != EducationType.NONE) out.add(k);
        return out;
    }

    /** A dial on this page, registered so the tray can name and apply it. */
    Lever register(Lever lever) {
        policyLevers.put(lever.key(), lever);
        return lever;
    }

    /** One kind's own price, as a lever. */
    Lever kindLever(EducationType kind, TaxPolicy policy) {
        return new Lever(schoolKey(kind), "The price of a place, " + kind.getLabel(), policy.tuitionScaleOf(kind), 0,
                TaxPolicy.MAX_TUITION_SCALE, TUITION_STEP, PolicyScreen::scaleWords, v -> policy.setTuitionScaleOf(kind, v));
    }

    /** One course's row, worked out without drawing it: its burden now and staged, whether a school of it stands, and its four figures. */
    record Course(EducationType kind, double now, double then, boolean standing, double places, double students,
                  double cost, double revenue) { }

    /** The nine courses (pure: the probe reads them): what a place costs a family as a share of a month's wage, at the city's price and share and at the staged ones (PolicyPreview.schoolBurden()). */
    List<Course> courses(Education schools, TaxPolicy policy, double[] places) {
        List<Course> out = new ArrayList<>();
        double share = schools.getTuitionSubsidy(), shareThen = stagedShare();
        for (EducationType kind : schoolKinds()) {
            out.add(new Course(kind, PolicyPreview.schoolBurden(ui.game, kind, schools.getTuitionScaleOf(kind), share),
                    PolicyPreview.schoolBurden(ui.game, kind, stagedScaleOf(policy, kind), shareThen),
                    places[kind.ordinal()] > 0, places[kind.ordinal()], schools.getEnrolled(kind),
                    schools.getCostOf(kind), schools.getFeesOf(kind)));
        }
        return out;
    }

    /** WHO CAN AFFORD A PLACE: the nine as bars on 0 to 100% of a month's wage with a tick at MAX_BURDEN, red past it; each opens into its own price. */
    VBox burdenRows(Education schools, TaxPolicy policy, double[] places) {
        List<Course> all = courses(schools, policy, places);
        double scale = 1;
        for (Course c : all) scale = Math.max(scale, Math.max(c.now(), c.then()) * 1.05);
        VBox list = card();
        list.setSpacing(2);
        for (Course c : all) {
            String key = "course:" + c.kind().name();
            boolean shown = openLines.contains(key);
            list.getChildren().add(courseRow(c, scale, shown, () -> { if (!openLines.remove(key)) openLines.add(key); showPolicyMenu(); }));
            if (shown) list.getChildren().add(kindCard(c, policy));
        }
        return list;
    }

    /** One course: its name ("▸ its price"), its bar - red past the line where nobody enrols - with the staged burden a ghost, its figure, and "no school" when none stands. */
    HBox courseRow(Course c, double scale, boolean shown, Runnable toggle) {
        Label name = words(c.kind().getLabel(), Palette.SIZE_BODY + 1, c.standing() ? Palette.TEXT_BODY : Palette.TEXT_MUTED);
        Label mark = new Label((shown ? "▾ " : "▸ ") + "its price");
        mark.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.ACCENT));
        VBox named = new VBox(0, name, mark);
        HBox lead = new HBox(8, iconSquare(Icons.EDUCATION, Palette.MONEY, 24, 13), named);
        HBox.setHgrow(named, Priority.ALWAYS);
        lead.setAlignment(Pos.CENTER_LEFT);
        lead.setMinWidth(180);
        lead.setPrefWidth(220);
        lead.setMaxWidth(220);
        boolean over = c.now() >= Education.MAX_BURDEN, overThen = c.then() >= Education.MAX_BURDEN;
        List<Tick> line = List.of(new Tick(Education.MAX_BURDEN, Palette.BAD, 2, null,
                String.format("%.0f%% of a month's wage: nobody enrols", Education.MAX_BURDEN * 100)));
        VBox bars = new VBox(2, segmentBar(List.of(Segment.of(Math.min(c.now(), scale), over ? Palette.BAD : Palette.MONEY)),
                scale, line, 0, 8));
        boolean moved = !pct0(c.now()).equals(pct0(c.then()));
        if (moved) {
            bars.getChildren().add(segmentBar(List.of(new Segment(Math.min(c.then(), scale), overThen ? Palette.BAD : Palette.ACCENT,
                    true, null, null, "at the staged price and share " + pct0(c.then()), null)), scale, List.of(), 0, 6));
        }
        bars.setMinWidth(120);
        HBox.setHgrow(bars, Priority.ALWAYS);
        Label figure = words((moved ? pct0(c.now()) + " → " + pct0(c.then()) : pct0(c.now())) + " of a month's wage",
                Palette.SIZE_LABEL + 1, over || overThen ? Palette.BAD_TEXT : Palette.TEXT_LABEL);
        figure.setPrefWidth(200);
        figure.setMinWidth(150);
        figure.setMaxWidth(200);
        Label status = words(c.standing() ? people(c.students()) + " students in " + people(c.places()) + " places" : "no school",
                Palette.SIZE_LABEL, Palette.TEXT_MUTED);
        status.setPrefWidth(190);
        status.setMinWidth(140);
        status.setMaxWidth(190);
        HBox row = new HBox(10, lead, bars, figure, status);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(40);
        String rest = "-fx-padding: 4 6 4 6; -fx-background-radius: 6; -fx-cursor: hand;"
                + " -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 0 1 0;";
        row.setStyle(rest);
        row.setOnMouseEntered(e -> row.setStyle(rest + " -fx-background-color: " + Palette.CONTROL + ";"));
        row.setOnMouseExited(e -> row.setStyle(rest));
        row.setOnMouseClicked(e -> toggle.run());
        return row;
    }

    /**
     * One kind's own price, opened in place (K-3): its dial - greyed with
     * nothing standing, since a price for a school the city does not have
     * moves nothing today, though every school at once still sets it - its
     * places, students, cost and revenue off the model, and its burden and
     * what the courses are billed before and after.
     */
    VBox kindCard(Course c, TaxPolicy policy) {
        Lever own = policyLevers.get(schoolKey(c.kind()));
        double otherwise = staged(EVERY_SCHOOL, own.current());
        Ladder ladder = ladderOf(own)
                .showing(stagedScaleOf(policy, c.kind()))
                .stages(v -> {
                    // Against what the kind would otherwise be charged: with every school at once staged, putting
                    // this kind back at today's price is a decision of its own; at every school's price, none.
                    if (moved(v, otherwise, TUITION_STEP)) stage(own.key(), v); else unstage(own.key());
                    showPolicyMenu();
                })
                .greyed(!c.standing())
                .stepReads(PolicyScreen::scaleWords);
        Education schools = ui.game.getEducation();
        DoubleFunction<List<Effect>> effects = kindEffects(c, policy);
        FlowPane figures = chips(chip("places " + people(c.places()), Palette.TEXT_LABEL),
                chip("students " + people(c.students()), Palette.TEXT_LABEL),
                chip("cost " + money(c.cost()), Palette.TEXT_LABEL), chip("revenue " + money(c.revenue()), Palette.TEXT_LABEL));
        VBox card = dialCard(new DialCard(Icons.EDUCATION, Palette.MONEY, c.kind().getLabel().toUpperCase() + ": ITS OWN PRICE",
                "Each kind of school at its own price. Beside it: the places its buildings "
                + "seat, the students in it this month, what its staff and buildings cost the "
                + "treasury, and the tuition its students paid.",
                null, c.standing() ? "Its own price, against the founding table." : "No school of this kind stands; every school at once still sets its price.",
                List.of(figures), ladder, stagedScaleOf(policy, c.kind()), effects, CAVEAT_LINE, SCHOOL_CAVEAT + " " + CAVEAT, null),
                440, false);
        VBox.setMargin(card, new javafx.geometry.Insets(2, 0, 8, 34));
        return card;
    }

    /** A kind's own price's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> kindEffects(Course c, TaxPolicy policy) {
        Lever own = policyLevers.get(schoolKey(c.kind()));
        Education schools = ui.game.getEducation();
        return v -> {
            TaxPolicy after = stagedPolicyWith(own.key(), v);
            return List.of(
                    Effect.of("A place, of a month's wage", c.now(),
                            PolicyPreview.schoolBurden(ui.game, c.kind(), v, stagedShare()), PolicyScreen::pct0)
                            .scale(Math.max(1, c.now()))
                            .verdict(PolicyPreview.schoolBurden(ui.game, c.kind(), v, stagedShare()) >= Education.MAX_BURDEN ? Palette.BAD : null)
                            .delta(d -> points(d).replace(" points", " pts")),
                    Effect.of("Billed a month, every course", schools.billedAt(policy::tuitionScaleOf),
                            schools.billedAt(after::tuitionScaleOf), PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                    budgetEffect(after, stagedShare()));
        };
    }

    /** The city's share of tuition (K-1, registered): what households pay and what the city waives before and after, and THE BUDGET. */
    VBox shareCard(Lever lever, Education schools, TaxPolicy policy) {
        Ladder ladder = ladderOf(lever);
        DoubleFunction<List<Effect>> effects = shareEffects(policy);
        return dialCard(new DialCard(Icons.EDUCATION, Palette.MONEY, "WHO PAYS FOR SCHOOL", SHARE_INFO,
                pct0(schools.getTuitionSubsidy()) + " the city's share", "of every course fee", null, ladder,
                stagedShare(), effects, CAVEAT_LINE, SCHOOL_CAVEAT + " " + CAVEAT, null), 360, true);
    }

    /** The city's share's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> shareEffects(TaxPolicy policy) {
        Education schools = ui.game.getEducation();
        return v -> {
            TaxPolicy after = stagedPolicy();
            double before = schools.getTuitionSubsidy();
            return List.of(
                    Effect.of("Households pay a month", PolicyPreview.tuitionPaid(ui.game, policy, before),
                            PolicyPreview.tuitionPaid(ui.game, after, v), PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                    Effect.of("The city waives, a month", PolicyPreview.tuitionWaived(ui.game, policy, before),
                            PolicyPreview.tuitionWaived(ui.game, after, v), PolicyScreen::amount).colour(Palette.TEXT_SPENT)
                            .info("Tuition the city does not collect: revenue forgone, not money out of the treasury."),
                    budgetEffect(after, v));
        };
    }

    /** THE SCHOOLS THIS MONTH (K-2), footing: staff and buildings, less what households paid, is the net cost; the tuition waived beside it in grey, outside the sum (B17, D13). */
    VBox schoolsMonthCard(Education schools) {
        return card(cardHead(Icons.EDUCATION, Palette.MONEY, "THE SCHOOLS THIS MONTH",
                        "What the schools cost the city this month, footing: their staff and buildings, less the tuition "
                        + "households paid, is the net cost. The tuition the city covers is not in the sum - it is revenue "
                        + "the city chose not to collect, not money it paid out (it was drawn as a cost until 0.7.36, and "
                        + "the four lines did not foot).", null),
                cardLine("Staff and buildings", signedTight(schools.getPayroll() + schools.getUpkeep(), true), null),
                cardLine("Tuition households pay", signedTight(schools.getFees(), false), null),
                cardLine("Net cost to the city", signedTight(schools.getNetCost(), true), Palette.TEXT_HEAD),
                muted("The city waived " + money(schools.getSubsidy()) + " of tuition: forgone, outside the sum."));
    }

    /** Every school at once (K-3, registered): its dial ("a price per school" until moved, once the nine have parted), what the courses are billed and households pay, and THE BUDGET. */
    VBox everySchoolCard(Lever lever, TaxPolicy policy) {
        Education schools = ui.game.getEducation();
        boolean split = policy.tuitionScalesSplit();
        Ladder ladder = ladderOf(lever).stages(v -> {
            // Moving every school at once takes back whatever one kind had staged: it is a price for all nine.
            for (EducationType kind : schoolKinds()) unstage(schoolKey(kind));
            propose(lever, v);
        });
        if (split) ladder.idle("a price per school");
        DoubleFunction<List<Effect>> effects = everySchoolEffects(policy);
        return dialCard(new DialCard(Icons.EDUCATION, Palette.MONEY, "WHAT A PLACE IS PRICED AT", PRICE_INFO,
                split ? "a price per school" : scaleWords(policy.getTuitionScale()), "Every school at once.", null, ladder,
                staged(EVERY_SCHOOL, lever.current()), effects, CAVEAT_LINE, SCHOOL_CAVEAT + " " + CAVEAT, null), 360, true);
    }

    /** Every school's price's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> everySchoolEffects(TaxPolicy policy) {
        Education schools = ui.game.getEducation();
        Lever lever = policyLevers.get(EVERY_SCHOOL);
        return v -> {
            TaxPolicy after = previewFor(EVERY_SCHOOL, v, lever.current(), TUITION_STEP);
            return List.of(
                    Effect.of("Billed a month, before the city's share", schools.billedAt(policy::tuitionScaleOf),
                            schools.billedAt(after::tuitionScaleOf), PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                    Effect.of("Households pay a month", PolicyPreview.tuitionPaid(ui.game, policy, schools.getTuitionSubsidy()),
                            PolicyPreview.tuitionPaid(ui.game, after, stagedShare()), PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                    budgetEffect(after, stagedShare()));
        };
    }

    /** P53, with its figures. */
    String grantInfo(TaxPolicy policy) {
        double students = ui.game.getFamilies().getSeekers(FamilyModel.Seeker.STUDENT);
        return "To every full-time student, to live on"
                + (students > 0 ? String.format(" - %s each this month, %s to %s of them.",
                        moneyFull(ui.game.grantPerStudentUnder(policy.getGrantBasis(), policy.getGrantAmount())),
                        money(ui.game.getEconomyManager().getStudentGrants()), people(students))
                        : "; nobody is studying this month.")
                + " What it does not cover, a student loan does. The unskilled wage it "
                + "can be a share of is " + moneyFull(ui.game.getUnskilledWage())
                + " a month; last month's surplus was " + signedTight(ui.game.getTreasurySurplus(), false) + ".";
    }

    /**
     * What a student is granted (K-5, registered): the basis as chips - a
     * chip stages the basis AND today's grant re-expressed in its unit, so
     * the switch alone changes nothing until the amount moves - and the
     * amount's dial in the staged basis's unit; each student and the month's
     * bill before and after (Game's own rule), and THE BUDGET.
     */
    VBox grantCard(TaxPolicy policy) {
        TaxPolicy.GrantBasis basis = policy.getGrantBasis();
        double amount = policy.getGrantAmount();
        TaxPolicy.GrantBasis wantBasis = stagedBasis(policy);
        // The basis alone only when the amount is not staged with it (0.7.23, after the docs pass): a basis chip
        // stages both, and the amount's lever applies the pair in one setGrant() - one decision in the log, at
        // the figure the player chose.
        register(new Lever("grantBasis", "The grant is", basis.ordinal(), 0, TaxPolicy.GrantBasis.values().length - 1, 1,
                v -> basisName(TaxPolicy.GrantBasis.values()[(int) Math.round(v)]),
                v -> { if (!isStaged("grantAmount")) policy.setGrantBasis(TaxPolicy.GrantBasis.values()[(int) Math.round(v)]); }));
        FlowPane pick = new FlowPane(6, 6);
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
        // The amount, in the staged basis's unit: the same dial re-ranged. Its "current" is what today's grant IS
        // in that unit, so a thumb put back is a grant unchanged; applied WITH the basis it was read in.
        double amountNow = wantBasis == basis ? amount : ui.game.grantAmountAs(wantBasis);
        double amountMax = policy.maxGrantAmount(wantBasis);
        double amountStep = wantBasis == TaxPolicy.GrantBasis.FIXED ? Math.max(1e-9, amountMax / 100) : .01;
        Lever amountLever = new Lever("grantAmount", "...at", amountNow, 0, amountMax, amountStep,
                v -> amountWords(wantBasis, v), v -> policy.setGrant(wantBasis, v));
        Ladder ladder = ladderOf(amountLever);
        double each = ui.game.grantPerStudentUnder(basis, amount);
        DoubleFunction<List<Effect>> effects = grantEffects(policy);
        String caveat = "Against today's students, wage, surplus and courses. A smaller grant "
                + "is a bigger student loan, repaid out of wages for nine and a half years"
                + (wantBasis == TaxPolicy.GrantBasis.SURPLUS_SHARE
                        ? "; and a share of the surplus is nothing in a deficit month, whoever is studying." : ".");
        return dialCard(new DialCard(Icons.EDUCATION, Palette.MONEY, "WHAT A STUDENT IS GRANTED", grantInfo(policy),
                null, grantWords(basis, amount), List.of(pick), ladder, staged("grantAmount", amountNow), effects,
                firstWords(caveat), caveat + " " + CAVEAT, null), 380, true);
    }

    /** The grant's amount's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> grantEffects(TaxPolicy policy) {
        TaxPolicy.GrantBasis basis = policy.getGrantBasis();
        double amount = policy.getGrantAmount();
        TaxPolicy.GrantBasis wantBasis = stagedBasis(policy);
        double each = ui.game.grantPerStudentUnder(basis, amount);
        return v -> {
            TaxPolicy after = stagedPolicyWith("grantAmount", v);
            return List.of(
                    Effect.of("Each student, this month", each, ui.game.grantPerStudentUnder(wantBasis, v), Money::moneyFull)
                            .delta(PolicyScreen::moneyMoveFull),
                    Effect.of("Paid a month", ui.game.studentGrantBillUnder(basis, amount), ui.game.studentGrantBillUnder(wantBasis, v),
                            PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                    budgetEffect(after, stagedShare()));
        };
    }

    /** P55. */
    static final String LOAN_INFO = "Charged on a graduate's balance while they repay it, and on nothing while "
            + "they study - the treasury carries the interest until they finish. It "
            + "arrives as revenue; the loan itself is not on the budget, it is lent and "
            + "paid back. A prisoner's loan is frozen with the rest of their debts.";

    /** P56. */
    static final String LOAN_CAVEAT = "Against the balances the graduates owe today. The instalment itself does "
            + "not change - a 114th of the balance a month - so a rate is money on top "
            + "of it, out of the same wages the shops are waiting for.";

    /** What the loan costs them afterwards (K-6, registered): what is owed and repaid, and a month's interest and THE BUDGET before and after (HouseholdBalance.studentInterestAt()). */
    VBox loanCard(Lever lever, TaxPolicy policy) {
        HouseholdBalance bal = ui.game.getHouseholdBalance();
        double rate = policy.getStudentLoanRate();
        Ladder ladder = ladderOf(lever);
        List<Node> lines = List.of(
                cardLine("Owed on student loans today", money(bal.totalStudentDebt()), null),
                cardLine("...of it by graduates, repaying", money(bal.totalGraduateDebt()), null),
                cardLine("Repaid last month", money(ui.game.getStudentLoansRepaid()), null),
                cardLine("Interest received last month", money(ui.game.getEconomyManager().getStudentLoanInterest()), null));
        DoubleFunction<List<Effect>> effects = loanEffects(policy);
        return dialCard(new DialCard(Icons.BANKNOTE, Palette.MONEY, "WHAT THE LOAN COSTS THEM AFTERWARDS", LOAN_INFO,
                pct2(rate) + " a year", "on a graduate's balance while they repay it", lines, ladder,
                staged("loanRate", rate), effects, firstWords(LOAN_CAVEAT), LOAN_CAVEAT + " " + CAVEAT, null), 380, true);
    }

    /** The loan rate's effects at any value of its thumb (pure: the probe reads them). */
    DoubleFunction<List<Effect>> loanEffects(TaxPolicy policy) {
        HouseholdBalance bal = ui.game.getHouseholdBalance();
        double rate = policy.getStudentLoanRate();
        return v -> List.of(
                Effect.of("A month's interest, on today's balances", bal.studentInterestAt(rate), bal.studentInterestAt(v),
                        PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                budgetEffect(stagedPolicyWith("loanRate", v), stagedShare()));
    }

    /* =====================================================================
       PROMISES - the standing subsidies

       A TOGGLE, NOT A DIAL, so it has no preview: the cost is whatever the
       sector loses, and nobody - including the model - knows that until the
       month is over. What the screen can say is what it cost last month and
       what it is buying, which is the part a player is deciding on.

       Whom is the city keeping alive, and what does it cost? (0.7.36) A card
       a sector, five across: protected or not, what it was paid, a red
       "cannot borrow" (a verdict: the model's lockout), and its toggle,
       which applies at once as it always has.
       ===================================================================== */

    /** P69. */
    static final String SUBSIDY_INFO = "A protected sector is topped up to break-even every month it loses money, "
            + "so it never sells its capacity to pay a bill. The city pays whatever it "
            + "takes - and goes overdrawn if it must, which costs interest. "
            + "A subsidy is the only lever on this tab that reaches a sector already in "
            + "default, because a sector that cannot borrow cannot trade its way out. "
            + "It is also the only one with no natural end: nothing here stops paying.";

    void subsidyPage(VBox page) {
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        page.getChildren().add(sectionHead("WHAT THE CITY WILL NOT LET FAIL", SUBSIDY_INFO,
                quiet("paid last month " + money(ui.game.getTotalSubsidyPaid()) + " · a toggle applies at once")));
        page.getChildren().add(noteLine(firstWords(SUBSIDY_INFO), SUBSIDY_INFO, 1100));
        GridPane grid = equalColumns(5, TILE_GAP);
        int i = 0;
        for (Sector sector : ui.game.getSectors().all()) {
            boolean on = ui.game.isAutoSubsidised(sector);
            double paid = ui.game.getSubsidyPaid(sector);
            boolean blocked = credit.isBorrowingBlocked(sector.key());
            VBox c = card(cardHead(Icons.ofSector(sector), Palette.BUSINESS, sector.label(), null, null),
                    words(on ? (paid > 0 ? "protected · " + money(paid) + " paid last month"
                                         : "protected, and it did not need it last month")
                             : "not protected", Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
            if (blocked) c.getChildren().add(tag("cannot borrow", Palette.BAD));
            c.getChildren().add(actionButton(Icons.SECTOR, Palette.MONEY, ACTION_INLINE,
                    new Press(Look.CHOOSE, on ? "Stop protecting" : "Protect it", null), () -> {
                        ui.game.setAutoSubsidised(sector, !ui.game.isAutoSubsidised(sector));
                        showPolicyMenu();
                    }));
            GridPane.setFillHeight(c, true);
            c.setMaxHeight(Double.MAX_VALUE);
            grid.add(c, i % 5, i / 5);
            i++;
        }
        page.getChildren().add(grid);
    }
}
