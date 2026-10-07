package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import ham.citybuildersim.ui.Pieces.Slice;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
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
 * The government tab: where the city's money came from and went, the road
 * from what it EARNED through the budget's SURPLUS to what the cash BANKED,
 * who pays every line and where each is decided, what the city owes and has
 * promised, and what its economy makes - four pages under five figures.
 *
 * WHY, and why it looks like this (0.7.31). Split out of UserInterface on
 * 2026-09-18 with its banners verbatim, it was a 560 px statement column
 * of rows and two dozen paragraphs; Jerus, on the screens not yet redone:
 * "the others are still full of text and the design could be more intuitive
 * and fun", and on the header: "the money one has is barely visible to see
 * as well as ones income" - where "+$1.5B a month" stood by the cash while
 * the cash grew $2.3B and no screen said why. Redrawn in Build's style (the
 * project's spec-government-0731.md): the two rings - the walkthrough's
 * keeper - with the balance between them; the three figures named the same
 * way everywhere, EARNED, SURPLUS and BANKED, and the steps between them as
 * one card the model reads end to end (Game.getEarnedToBudget() and the
 * treasury's bridge); Revenue and Spending as ranked bars that open into who
 * pays, each with a door to where the line is decided; the debt, the
 * services that charge and the pensions as cards; Output led by History's
 * GDP layers. The old statements are behind "details" and the paragraphs
 * behind an (i). The rail opens showGovernmentMenu(); open() is every other
 * door in.
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

       SO THE QUESTIONS THIS TAB ANSWERS, a page each (0.7.31):

         Overview  where does the money come from and go, and what did the
                   cash actually do? The two rings with the balance between
                   them; then FROM EARNED TO BANKED: the header's figure, the
                   budget and the cash, with every step between them named.
         Revenue   who pays for the city, and at what rate? A ranked bar a
                   line; a line opens into the sectors, tiers or kinds that
                   paid it, at the payer's own rate.
         Spending  what does it spend on, and what is it committed to? The
                   same, then the debt, the services that charge for
                   themselves and the pensions as three cards.
         Output    how big is the economy, what is it made of, and is it
                   growing? History's GDP layers, the four parts, the growth.

       EVERYTHING IS ALSO A SHARE OF ANNUAL GDP, because that is the only unit
       in which a budget can be compared with anything - another city, another
       year, or a rule of thumb.

       ONE VERDICT EACH (0.7.31, the spec's D7). The SURPLUS is coloured by
       NEEDS YOU's THE BUDGET row, OWED by its BORROWING row and the pension
       chip by its PENSIONS row - the screen keeps no thresholds of its own.
       EARNED, BANKED, the bars, the rings and the growth are readings, not
       verdicts, and are drawn in the area colours and their steps (the money
       blue's most of all), the rings' sand and the ordinary text colours.
       ===================================================================== */

    /** The tab's four pages, in the strip's order; the first is the one it starts on, and falls back to for a page it does not know. */
    static final String[] GOV_PAGES =
            {"Overview", "Revenue", "Spending", "Output"};

    /** Each page's icon on its chip (0.7.31). */
    static final String[] GOV_ICONS = {Icons.OVERVIEW, Icons.COIN, Icons.FINANCES, Icons.REPORTS};

    String govPage = "Overview";

    /**
     * What is standing open - a line's payers, a "details" fold, a bridge
     * column's "N more" - by key. Kept on the screen rather than on the row,
     * because the panel is rebuilt under the player every month the clock
     * ticks and a row that shut itself each time would be unreadable at
     * speed (the old bridge's bridgeOpen is one key of it now).
     */
    private final java.util.Set<String> open = new java.util.HashSet<>();

    /** Where the page is to be scrolled to once it is drawn: a line's key, BRIDGE or BALANCE; null for the top. */
    private String scrollTarget;

    /** The nodes a door on this tab can scroll to, by the same keys, as the page draws them. */
    private final java.util.Map<String, Node> targets = new java.util.HashMap<>();

    /** The page's scroller. */
    private javafx.scene.control.ScrollPane body;

    /** How much of the stage the fixed frame takes above the page's scroller - the head, the five figures with SURPLUS's change, the pages, and their gaps - until the frame is laid out and its own height is read (0.7.31). */
    static final double FRAME_CHROME = 232;

    /** What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - SectorScreen's, for the same frame (0.7.31). */
    static final double STAGE_REST = 36;

    /** The scroll targets on the Overview: the bridge card and the balance. */
    static final String BRIDGE = "#bridge", BALANCE = "#balance";

    /**
     * The tab opened on one of its pages (0.7.31): at its top, or with `line`
     * - a budget line's name on Revenue or Spending, which opens it and
     * scrolls to it, or BRIDGE or BALANCE on the Overview - in view. The
     * rings' slices and keys, the five figures and the "outside the total"
     * lines come in here.
     */
    void open(String page, String line) {
        govPage = page;
        scrollTarget = line == null ? null : line.startsWith("#") ? line : lineKey(page, line);
        if (line != null && !line.startsWith("#")) open.add(lineKey(page, line));
        ui.innerScrollAt.remove("showGovernmentMenu:body");
        showGovernmentMenu();
    }

    /** A line's key on its page: what its open state and its scroll target are kept under. */
    static String lineKey(String page, String line) { return page + ":" + line; }

    void showGovernmentMenu() {
        ui.clearMenu("showGovernmentMenu", () -> showGovernmentMenu());

        boolean known = false;
        for (String p : GOV_PAGES) if (p.equals(govPage)) known = true;
        if (!known) govPage = GOV_PAGES[0];

        EconomyManager em = ui.game.getEconomyManager();
        NationalAccounts na = em.getNationalAccounts();
        List<CityNeeds.Need> all = CityNeeds.measure(ui.game, SummaryScreen.WORDS);
        targets.clear();

        VBox page = widePage();
        switch (govPage) {
            case "Revenue"  -> revenuePage(page, em, na);
            case "Spending" -> spendingPage(page, em, na, all);
            case "Output"   -> outputPage(page, na);
            default         -> overviewPage(page, em, na, all);
        }

        javafx.scene.layout.FlowPane strip =
                chipStrip(GOV_PAGES, GOV_ICONS, govPage, Palette.SIZE_LABEL, name -> open(name, null));
        VBox frame = new VBox(Palette.GAP, head(), vitals(em, na, all), strip);
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

    /** The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (SectorScreen's, 0.7.30). */
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

    /** The Overview's bridge or balance in view: scrolled to where it is, or the Overview opened on it. */
    void showOnOverview(String target) {
        if ("Overview".equals(govPage) && targets.get(target) != null) scrollTo(targets.get(target));
        else open("Overview", target);
    }

    /** The tab's (i): what the five figures are, in a breath. */
    static final String HEAD_INFO = "What the city took in and paid out last month, and what its money did. "
            + "EARNED is the header's figure, the budget's SURPLUS is what it took in less what it paid out, "
            + "and BANKED is what the cash actually did between two presses - the Overview walks from one to the "
            + "next, step by step. Most lines open into who paid them, and every line has a door to where it is "
            + "decided.";

    /**
     * The head: "Government" with the money blue's swatch, and at its right
     * the two doors every page has - the taxes, on Policy, and the revenue
     * and the surplus over the years, in City History.
     */
    HBox head() {
        return pageHead("Government", Palette.MONEY, null, null, HEAD_INFO,
                door("Taxes on Policy", Palette.ACCENT, () -> openPolicy("Taxes", PolicyScreen.POLICY_HOME)),
                door("Over the years", Palette.ACCENT, () -> ui.historyScreen.openOn("revenue", "surplus")));
    }

    /* ---------------------------------------------------------------------
       THE FIVE FIGURES (0.7.31; four until then)

       EARNED, SURPLUS and BANKED first, in that order and by those names
       everywhere (the spec's D2): the header's figure, the budget and the
       cash. Then what the city owes and its take of the economy. Only the
       SURPLUS and OWED carry a verdict colour, NEEDS YOU's (D7); EARNED and
       BANKED print their sign in the headings' ink. Each is a door: the three
       to the Overview's bridge or balance, OWED to Finances, TAX TAKE to
       Revenue.
       --------------------------------------------------------------------- */

    /** One of the five figures, worked out without drawing it: its label, its figure, its note, its colour and where its click goes. */
    record Kpi(String label, String value, String note, String tone, String where) { }

    /** The five figures (pure: the probe reads them as the strip shows them). */
    List<Kpi> kpis(NationalAccounts na, List<CityNeeds.Need> all) {
        double income = ui.game.getIncome();
        double revenue = na.getTotalRevenue();
        double balance = na.getBalance();
        double annual = annualGdp(na);
        double owed = ui.game.getDebtManager().getAllPrincipal();
        boolean banked = ui.game.hasTreasuryMonth();
        String rated = "rated " + ui.game.getCreditRating();
        return List.of(
                new Kpi("EARNED", s(income), "the header's figure · running lines", Palette.TEXT_HEAD,
                        "From EARNED to BANKED, step by step"),
                new Kpi(balance >= 0 ? "SURPLUS" : "DEFICIT", s(balance),
                        revenue > 0 ? String.format("%.0f%% of what it took in", Math.abs(balance) / revenue * 100)
                                    : "nothing came in",
                        needTone(all, CityNeeds.Kind.BUDGET, Palette.TEXT_HEAD),
                        "The budget: what it took in against what it paid out"),
                new Kpi("BANKED", banked ? s(ui.game.getTreasuryChange()) : "—",
                        banked ? m(ui.game.getTreasuryOpening()) + " → " + m(ui.game.getTreasuryClosing())
                               : "no month closed yet",
                        Palette.TEXT_HEAD, "What the cash did, press to press"),
                new Kpi("OWED", m(owed),
                        owed < .5 ? "nothing owed · " + rated
                                : annual > 0 ? String.format("%.0f%% %s · %s", owed / annual * 100, ofAnnualGdp(na), rated)
                                : rated,
                        needLevel(all, CityNeeds.Kind.BORROWING) >= 2 ? Palette.BAD : Palette.TEXT_HEAD,
                        "Finances: the debt, and what it costs"),
                new Kpi("TAX TAKE", annual > 0 ? String.format("%.1f%%", yearOf(revenue, REVENUE_KEYS) / annual * 100) : "—",
                        annual > 0 ? ofAnnualGdp(na) + (trailing(REVENUE_KEYS) ? ", the last twelve months" : "")
                                : "no output to compare yet", Palette.TEXT_HEAD,
                        "Revenue: who pays it, line by line"));
    }

    HBox vitals(EconomyManager em, NationalAccounts na, List<CityNeeds.Need> all) {
        List<Kpi> k = kpis(na, all);
        Runnable[] go = {
                () -> showOnOverview(BRIDGE),
                () -> showOnOverview(BALANCE),
                () -> showOnOverview(BRIDGE),
                () -> openFinances(null, FinancesScreen.FINANCE_HOME),
                () -> open("Revenue", null)};
        VBox[] cells = new VBox[k.size()];
        for (int i = 0; i < cells.length; i++) {
            Kpi c = k.get(i);
            cells[i] = limitCell(c.label(), c.value(), c.note(), c.tone(), c.where(), go[i]);
        }
        withSpark(cells[1], "surplus");
        return vitalsBar(cells);
    }

    /** A limit cell with a year of a History series as a sparkline at the right of its figure, and its change on last month under the note (ServicesScreen.kpiCell()'s shape). */
    void withSpark(VBox cell, String series) {
        HistorySave history = ui.game.getHistorySave();
        double[] all = history.aligned(series);
        List<Integer> months = history.getMonth();
        int from = Math.max(0, all.length - 12);
        double[] year = java.util.Arrays.copyOfRange(all, from, all.length);
        List<Integer> when = months.size() == all.length ? months.subList(from, all.length) : null;
        Node figure = cell.getChildren().get(1);
        cell.getChildren().remove(1);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox top = new HBox(6, figure, gap, sparkline(year, when, Palette.MONEY, 64, 16));
        top.setAlignment(Pos.CENTER_LEFT);
        cell.getChildren().add(1, top);
        String d = change(series);
        if (d != null) {
            Label change = new Label(d);
            change.setWrapText(true);
            change.setMaxWidth(LIMIT_CELL - 28);
            change.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            cell.getChildren().add(change);
        }
    }

    /** A money series' change on last month, from its last two points in History: "▲ $1.4M on last month"; null with fewer than two. */
    String change(String series) {
        double[] s = ui.game.getHistorySave().aligned(series);
        double last = Double.NaN, before = Double.NaN;
        for (int i = s.length - 1; i >= 0; i--) {
            if (!Double.isFinite(s[i])) continue;
            if (Double.isNaN(last)) last = s[i];
            else { before = s[i]; break; }
        }
        if (!Double.isFinite(last) || !Double.isFinite(before)) return null;
        double d = last - before;
        if (Math.abs(d) < .5) return "no change on last month";
        return (d > 0 ? "▲ " : "▼ ") + money(Math.abs(d)) + " on last month";
    }

    /** NEEDS YOU's row of a kind - the one judge of it - or null when it is not measured. */
    static CityNeeds.Need need(List<CityNeeds.Need> all, CityNeeds.Kind kind) {
        for (CityNeeds.Need n : all) if (n.kind() == kind) return n;
        return null;
    }

    /** ...its level: 0 green, 1 amber, 2 red, -1 not measured. */
    static int needLevel(List<CityNeeds.Need> all, CityNeeds.Kind kind) {
        CityNeeds.Need n = need(all, kind);
        return n == null ? -1 : n.level();
    }

    /** ...and its colour, or `none` when it is not measured. */
    static String needTone(List<CityNeeds.Need> all, CityNeeds.Kind kind, String none) {
        CityNeeds.Need n = need(all, kind);
        return n == null ? none : BuildScreen.verdict(n.level());
    }

    /* ----------------------------- the screen's own pieces ----------------------------- */

    /** Money with a true minus (B15: tightMoney() writes a hyphen, signedTight() a minus, and both stood on one page). */
    static String m(double thousands) {
        String shown = money(Math.abs(thousands));
        return thousands < 0 && !"$0".equals(shown) ? "−" + shown : shown;
    }

    /** Money signed, as a movement: "+$1.2M", "−$3.5M", "$0". */
    static String s(double thousands) { return signedTight(thousands, false); }

    /**
     * Every "of GDP" on this tab reads the trailing year of its line where
     * History records it (B7, 0.7.47): the last TRAILING_MONTHS months, once
     * the line has that many. Until then, and for a line History does not
     * record, the month × 12 - which read a month that bought a lot of
     * buildings as a year of them, and still does for those lines.
     */
    static final int TRAILING_MONTHS = 12;

    /** The History series each budget line is, where it records one - the line's own figure each month (a series of something else, like the care's and the schools' net cost against their gross lines, is not one). */
    static final java.util.Map<String, List<String>> LINE_KEYS = java.util.Map.ofEntries(
            java.util.Map.entry("Business tax", List.of("taxBusiness", "taxIndustrial")),
            java.util.Map.entry("Sales tax", List.of("taxSales")),
            java.util.Map.entry("Wage tax", List.of("taxWage")),
            java.util.Map.entry("Property tax", List.of("taxProperty")),
            java.util.Map.entry("Pension contributions", List.of("contributions")),
            java.util.Map.entry("EI premiums", List.of("eiPremiums")),
            java.util.Map.entry("Health premiums", List.of("healthPremiums")),
            java.util.Map.entry("Student loan interest", List.of("studentLoanInterest")),
            java.util.Map.entry("Central bank remittance", List.of("remittance")),
            java.util.Map.entry("Pensions", List.of("pensionBill")),
            java.util.Map.entry("EI", List.of("eiPaid")),
            java.util.Map.entry("Student grants", List.of("studentGrants")),
            java.util.Map.entry("Police and prisons", List.of("safetyBill")),
            java.util.Map.entry("Food assistance", List.of("foodAssistance")));

    /** What was taken in: History's revenue. */
    static final List<String> REVENUE_KEYS = List.of("revenue");
    /** ...the balance: History's surplus, negative for a deficit. */
    static final List<String> SURPLUS_KEYS = List.of("surplus");

    /** Whether History has TRAILING_MONTHS months of every one of these series (and there is one). */
    boolean trailing(List<String> keys) {
        if (keys == null || keys.isEmpty()) return false;
        HistorySave h = ui.game.getHistorySave();
        for (String k : keys) if (h.monthsRecorded(k) < TRAILING_MONTHS) return false;
        return true;
    }

    /** A line's year: the sum of its series' last TRAILING_MONTHS months where trailing(), the month × 12 where not. */
    double yearOf(double monthly, List<String> keys) {
        if (!trailing(keys)) return monthly * 12;
        HistorySave h = ui.game.getHistorySave();
        double year = 0;
        for (String k : keys) year += h.recentTotal(k, TRAILING_MONTHS);
        return year;
    }

    /** ...what was paid out, which History records as what came in less the balance (NationalAccounts.getBalance() is the one less the other): their trailing years' difference, or the month × 12. */
    double spendingYear(double monthly) {
        return trailing(REVENUE_KEYS) && trailing(SURPLUS_KEYS)
                ? yearOf(0, REVENUE_KEYS) - yearOf(0, SURPLUS_KEYS) : monthly * 12;
    }

    /** A line's share of a year's output, as every "of GDP" on this tab writes it - its trailing year where History records it - or a dash with no year to compare. */
    String ofGdpYear(List<String> keys, double monthly, double annual) {
        return annual > 0 ? minus(String.format("%.2f%%", yearOf(monthly, keys) / annual * 100)) : "—";
    }

    /** A formatted figure's hyphen as a minus (B15). */
    static String minus(String figure) { return figure.replace('-', '\u2212'); }

    /**
     * The amounts a ring and a list draw: a line under half a thousand ($500)
     * as nothing - no slice, no bar, and it joins the list's "nothing this
     * month". Half a thousand is where signedTight() starts writing "$0";
     * money(), which the rings and lists print with, writes "$400".
     * TODO(docs): whether $500, rather than what money() prints as "$0"
     * (under half a dollar), is the threshold meant.
     */
    static List<Double> drawn(List<Double> amounts) {
        List<Double> out = new ArrayList<>();
        for (double v : amounts) out.add(Math.abs(v) < .5 ? 0.0 : v);
        return out;
    }

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

    /** A picture's caption: a few words in capitals, with an (i) after them when `info` is not null. */
    static HBox caption(String text, String info) {
        Label l = new Label(text);
        l.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        l.setMinWidth(Region.USE_PREF_SIZE);
        HBox row = new HBox(Palette.GAP, l);
        if (info != null) row.getChildren().add(infoButton(info, true));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** A card's head: its icon in a square tinted in its colour, its name, its (i), and a door at its right (each of the last two may be null). */
    static HBox cardHead(String svg, String colour, String name, String info, Node door) {
        Label n = new Label(name);
        n.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        n.setWrapText(true);
        HBox head = new HBox(8, iconSquare(svg, colour, 28, 15), n);
        if (info != null) head.getChildren().add(infoButton(info, true));
        if (door != null) {
            Region gap = new Region();
            HBox.setHgrow(gap, Priority.ALWAYS);
            head.getChildren().addAll(gap, door);
        }
        head.setAlignment(Pos.CENTER_LEFT);
        return head;
    }

    /** A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 14. */
    static VBox card(Node... rows) {
        VBox c = new VBox(8);
        for (Node n : rows) if (n != null) c.getChildren().add(n);
        c.setMaxWidth(Double.MAX_VALUE);
        c.setStyle("-fx-padding: 14; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
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

    /** A statement column, for a fold or an opened line: the old rows at their old width (D14). */
    static VBox column(Node... rows) {
        VBox c = new VBox(0);
        for (Node n : rows) if (n != null) c.getChildren().add(n);
        c.setMaxWidth(STATEMENT);
        return c;
    }

    /* ----------------------------- the doors (D15) ----------------------------- */

    /** Policy, on one of its areas and pages, at its top (SectorScreen.openPolicy()'s). */
    void openPolicy(String area, String page) {
        ui.policyScreen.policyArea = area;
        ui.policyScreen.policyPage = page;
        ui.policyScreen.dropProposal();
        ui.innerScrollAt.remove("showPolicyMenu:body");
        ui.policyScreen.showPolicyMenu();
    }

    /** Finances, on one of its subjects and pages. */
    void openFinances(String area, String page) {
        ui.financesScreen.financeArea = area;
        ui.financesScreen.financePage = page;
        ui.innerScrollAt.remove("showFinanceMenu:body");
        ui.financesScreen.showFinanceMenu();
    }

    /**
     * Where a budget line is decided (0.7.31, the spec's D15), as Policy's
     * {area, page} - each a page in PolicyScreen's own lists - or null for a
     * line no dial on Policy sets.
     */
    static String[] policyPlace(String line) {
        return switch (line) {
            case "Business tax"                     -> new String[] {"Taxes", "Profit"};
            case "Sales tax"                        -> new String[] {"Taxes", "Sales"};
            case "Wage tax"                         -> new String[] {"Taxes", "Wage"};
            case "Property tax"                     -> new String[] {"Taxes", "Property"};
            case "Pension contributions", "Pensions" -> new String[] {"Promises", "Pensions"};
            case "EI premiums", "EI"                -> new String[] {"Promises", "Out of work"};
            case "Healthcare fees", "Health premiums", "Healthcare"
                                                    -> new String[] {"Promises", "Health"};
            case "School fees", "Student loan interest", "Student grants", "Education"
                                                    -> new String[] {"Promises", "Schools"};
            case "Subsidies"                        -> new String[] {"Promises", "Subsidies"};
            case "Food assistance"                  -> new String[] {"Promises", "Food"};
            case "Central bank remittance", "Interest to the central bank"
                                                    -> new String[] {"Money", "The policy rate"};
            default -> null;
        };
    }

    /** A budget line's door: its words and where it goes - a dial on Policy, or the screen the line is decided on; null for none. */
    record Door(String words, Runnable go) { }

    Door doorOf(String line) {
        String[] place = policyPlace(line);
        if (place != null) return new Door("set it", () -> openPolicy(place[0], place[1]));
        return switch (line) {
            case "Land sold", "Land bought"   -> new Door("Land office", () -> ui.landScreen.showLandMenu());
            case "Buildings"                  -> new Door("Build", () -> ui.constructionScreen.show());
            case "Mortgage insurance premiums", "Mortgage insurance claims"
                                              -> new Door("Bank", () -> ui.bankScreen.showBankMenu());
            case "Transfer from the fund"     -> new Door("Finances", () -> openFinances("The city's fund", "Holdings"));
            case "Debt interest"              -> new Door("Finances", () -> openFinances("The position", "Debt service"));
            case "Police and prisons"         -> new Door("Services", () -> ui.servicesScreen.open("Safety", ServicesScreen.OVERVIEW));
            case "Utility income"             -> new Door("Services", () -> ui.servicesScreen.open("Utilities", ServicesScreen.OVERVIEW));
            case "Transit fares", "Transit"   -> new Door("Infrastructure", () -> ui.infrastructureScreen.open("Transit"));
            default -> null;
        };
    }

    /** Each budget line's icon (the spec's section 6): every one an icon the rail and Build already draw. */
    static String iconOf(String line) {
        return switch (line) {
            case "Business tax"                      -> Icons.OFFICES;
            case "Sales tax"                         -> Icons.SHOPS;
            case "Wage tax"                          -> Icons.STAFF;
            case "Property tax"                      -> Icons.HOMES;
            case "Pension contributions", "Pensions" -> Icons.POPULATION;
            case "EI premiums", "EI"                 -> Icons.STAFF;
            case "Healthcare fees", "Health premiums", "Healthcare" -> Icons.HEALTH;
            case "School fees", "Student grants", "Student loan interest", "Education" -> Icons.EDUCATION;
            case "Police and prisons"                -> Icons.SAFETY;
            case "Land sold", "Land bought"          -> Icons.LAND;
            case "Buildings"                         -> Icons.CRANE;
            case "Repairs"                           -> Icons.BUILD;
            case "Utility income"                    -> Icons.UTILITIES;
            case "Transfer from the fund", "Debt interest" -> Icons.FINANCES;
            case "Central bank remittance", "Interest to the central bank" -> Icons.BANK;
            case "Mortgage insurance premiums", "Mortgage insurance claims" -> Icons.HOMES;
            case "Subsidies"                         -> Icons.POLICY;
            case "Food assistance"                   -> Icons.FOOD;
            case "Transit fares", "Transit"          -> Icons.BUS;
            default                                  -> Icons.COIN;
        };
    }

    /* =====================================================================
       THE OVERVIEW (0.7.31)

       The question: where does the money come from and go, and what did the
       cash actually do? The two rings with the balance between them, as one
       card; then FROM EARNED TO BANKED, the three figures with every step
       between them named; then the central bank, when the city owes it or
       has ever printed, and the budget against the size of the economy.
       ===================================================================== */

    void overviewPage(VBox page, EconomyManager em, NationalAccounts na, List<CityNeeds.Need> all) {
        page.getChildren().add(budgetCard(em, na, all));
        VBox bridge = bridgeCard(na);
        targets.put(BRIDGE, bridge);
        page.getChildren().add(bridge);

        VBox economy = economyCard(na);
        CentralBank cb = ui.game.getCentralBank();
        if (cb.getAdvancesToTreasury() > 0 || cb.getPrintedLifetime() > 0 || ui.game.hasArrears()) {
            javafx.scene.layout.GridPane two = equalColumns(2, TILE_GAP);
            VBox bank = centralBankCard(cb);
            javafx.scene.layout.GridPane.setFillHeight(bank, true);
            javafx.scene.layout.GridPane.setFillHeight(economy, true);
            two.add(bank, 0, 0);
            two.add(economy, 1, 0);
            page.getChildren().add(two);
        } else {
            page.getChildren().add(economy);
        }
    }

    /* ------------------------------ the rings and the balance ------------------------------ */

    /**
     * WHERE IT COMES FROM · THE BALANCE · WHERE IT GOES: the two rings - five
     * slices and a grey rest each, the walkthrough's keeper - with their keys,
     * and between them the balance. A slice or a key row opens its line on
     * Revenue or Spending; "Everything else" opens the page.
     */
    HBox budgetCard(EconomyManager em, NationalAccounts na, List<CityNeeds.Need> all) {
        double revenue = na.getTotalRevenue();
        double spending = na.getTotalExpenses();
        List<String> inNames = revenueNames(), outNames = spendingNames();
        List<Double> inAmounts = drawn(revenueAmounts(na)), outAmounts = drawn(spendingAmounts(na));

        // Colours told apart (0.7.21): the revenue ring's three biggest were
        // three steps of one blue. See Palette.CATEGORIES.
        List<Slice> inSlices = topSlices(inNames, inAmounts, Palette.CATEGORIES);
        List<Slice> outSlices = topSlices(outNames, outAmounts, Palette.CATEGORIES);

        List<Node> inFoot = new ArrayList<>(netted(inNames, inAmounts));
        List<Node> outFoot = new ArrayList<>(netted(outNames, outAmounts));
        double repairs = ui.game.getCityMaintenancePaid();
        if (Math.abs(repairs) >= .5) {
            outFoot.add(door("outside the total: repairs " + s(-repairs), Palette.TEXT_MUTED,
                    () -> showOnOverview(BRIDGE)));
        }

        VBox left = ringBlock("WHERE IT COMES FROM", inSlices, "taken in", m(revenue), inFoot, "Revenue");
        VBox right = ringBlock("WHERE IT GOES", outSlices, "paid out", m(spending), outFoot, "Spending");
        VBox centre = balanceBlock(na, all);
        targets.put(BALANCE, centre);

        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        HBox card = new HBox(16, left, centre, right);
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-padding: 14; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return card;
    }

    /** One ring's block: its caption, the ring and its key side by side, and the netted and outside lines under them. */
    VBox ringBlock(String title, List<Slice> slices, String top, String total, List<Node> foot, String page) {
        java.util.function.Consumer<Slice> pick = sl ->
                open(page, EVERYTHING_ELSE.equals(sl.name()) ? null : sl.name());
        VBox key = ringKey(slices, 260, pick);
        HBox.setHgrow(key, Priority.ALWAYS);
        HBox ringAndKey = new HBox(16, splitRing(slices, top, total, Palette.TEXT_HEAD, 180, pick), key);
        ringAndKey.setAlignment(Pos.CENTER_LEFT);
        VBox block = new VBox(8, caption(title, null), ringAndKey);
        if (slices.isEmpty()) {
            block.getChildren().add(words("nothing this month", Palette.SIZE_LABEL, Palette.TEXT_SPENT));
        }
        VBox under = new VBox(2);
        under.getChildren().addAll(foot);
        if (!foot.isEmpty()) block.getChildren().add(under);
        block.setMinWidth(380);
        block.setPrefWidth(460);
        return block;
    }

    /**
     * A ring's netted lines: every line below nothing, which the ring cannot
     * draw as an arc and the total under the ring's caption nets - "less
     * utility income −$471k" (the spec's B14).
     */
    static List<Node> netted(List<String> names, List<Double> amounts) {
        List<Node> out = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            if (amounts.get(i) <= -.5) {
                out.add(words("less " + names.get(i).toLowerCase() + " " + s(amounts.get(i)),
                        Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            }
        }
        return out;
    }

    /** P1's rest: what a surplus is for. */
    static final String SURPLUS_INFO = "It took in more than it spent. A surplus pays down debt or buys the next "
            + "thing without borrowing for it. The colour is NEEDS YOU's THE BUDGET row's, the one judge of it.";

    /** ...and what a deficit costs. */
    static final String DEFICIT_INFO = "It spent more than it took in. That gap is borrowed, and next month's "
            + "interest is charged on it. The colour is NEEDS YOU's THE BUDGET row's, the one judge of it: amber "
            + "or red once the deficit is a large enough share of a month's tax.";

    /**
     * THE BALANCE: the verdict word in NEEDS YOU's colour, the figure, what
     * share of the take it is, and the two bars on one scale - taken in in
     * the money blue, paid out in its darker step (the spec's D6; the paid
     * out bar was the spending ramp's amber, a verdict colour drawn as a
     * category, B4) - the part of the longer one past the other outlined in
     * the verdict's colour and named: kept, or short.
     */
    VBox balanceBlock(NationalAccounts na, List<CityNeeds.Need> all) {
        double revenue = na.getTotalRevenue();
        double spending = na.getTotalExpenses();
        double balance = na.getBalance();
        double annual = annualGdp(na);
        String tone = needTone(all, CityNeeds.Kind.BUDGET, Palette.TEXT_HEAD);

        Label verdict = new Label(balance >= 0 ? "SURPLUS" : "DEFICIT");
        verdict.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, tone));
        VBox block = new VBox(4, caption("THE BALANCE", null), verdict, figure(s(balance), 28, Palette.TEXT_HEAD),
                infoLine(revenue > 0
                                ? String.format("%.0f%% of what it took in", Math.abs(balance) / revenue * 100)
                                : "nothing came in",
                        balance >= 0 ? SURPLUS_INFO : DEFICIT_INFO, true, Palette.SIZE_LABEL, Palette.TEXT_LABEL, 230));

        double scale = Math.max(revenue, spending);
        block.getChildren().add(balanceBar("Taken in", revenue, spending, scale, Palette.MONEY, tone, true));
        block.getChildren().add(balanceBar("Paid out", spending, revenue, scale, Palette.MONEY_DARK, tone, false));
        if (Math.abs(balance) >= .5) {
            Region swatch = new Region();
            swatch.setMinSize(12, 8);
            swatch.setPrefSize(12, 8);
            swatch.setMaxSize(12, 8);
            swatch.setStyle("-fx-border-color: " + tone + "; -fx-border-width: 1.5; -fx-border-radius: 2;");
            Label kept = words((balance >= 0 ? "kept " : "short ") + m(Math.abs(balance)), Palette.SIZE_LABEL, tone);
            HBox line = new HBox(6, swatch, kept);
            line.setAlignment(Pos.CENTER_LEFT);
            block.getChildren().add(line);
        }
        if (annual > 0) {
            // The last twelve months' balance where History has them (B7): a busy month is not a year.
            double year = yearOf(balance, SURPLUS_KEYS);
            block.getChildren().add(words(trailing(SURPLUS_KEYS)
                            ? String.format("a year's %s, %.1f%% %s", year >= 0 ? "surplus" : "deficit",
                                    Math.abs(year) / annual * 100, ofAnnualGdp(na))
                            : String.format("%.1f%% %s", Math.abs(balance) * 12 / annual * 100, ofAnnualGdp(na)),
                    Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        }
        block.getChildren().add(words("tap a slice or a line for who pays", Palette.SIZE_LABEL, Palette.TEXT_SPENT));
        block.setMinWidth(254);
        block.setPrefWidth(254);
        block.setMaxWidth(254);
        return block;
    }

    /**
     * One balance bar: its name and figure over a bar of 230 x 12 on the
     * scale both share, filled in its colour to the shorter of the two
     * figures and - when it is the longer - outlined in `tone` past that.
     */
    static VBox balanceBar(String name, double value, double other, double scale, String colour, String tone,
                           boolean first) {
        double w = 230, h = 12;
        double unit = scale > 0 ? w / scale : 0;
        double filled = Math.min(Math.max(0, value), Math.max(0, other)) * unit;
        double over = Math.max(0, value - Math.max(0, other)) * unit;
        javafx.scene.layout.Pane bar = new javafx.scene.layout.Pane();
        bar.setMinSize(w, h);
        bar.setPrefSize(w, h);
        bar.setMaxSize(w, h);
        Region track = new Region();
        track.setStyle("-fx-background-color: " + Palette.EDGE + "; -fx-background-radius: 3;");
        place(track, 0, w, h);
        Region fill = new Region();
        fill.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 3 0 0 3;");
        place(fill, 0, Math.max(value > 0 ? 2 : 0, filled), h);
        bar.getChildren().addAll(track, fill);
        if (over >= 1) {
            Region past = new Region();
            past.setStyle("-fx-background-color: " + colour + "; -fx-border-color: " + tone + ";"
                    + " -fx-border-width: 1.5; -fx-border-radius: 0 3 3 0; -fx-background-radius: 0 3 3 0;");
            place(past, filled, over, h);
            bar.getChildren().add(past);
        }
        Label what = words(name, Palette.SIZE_LABEL, Palette.TEXT_LABEL);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox head = new HBox(6, what, gap, figure(m(value), Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD));
        head.setAlignment(Pos.CENTER_LEFT);
        head.setMaxWidth(w);
        VBox box = new VBox(2, head, bar);
        box.setStyle(first ? "-fx-padding: 6 0 0 0;" : "");
        return box;
    }

    /** A region held at a size, at x along a plain Pane (which sizes its children to their preferred size). */
    static void place(Region r, double x, double w, double h) {
        r.setMinSize(w, h);
        r.setPrefSize(w, h);
        r.setMaxSize(w, h);
        r.relocate(x, 0);
    }

    /* ------------------------------ from EARNED to BANKED ------------------------------ */

    /** The bridge card's (i): P2, rewritten, and P3. */
    static final String BRIDGE_INFO = "EARNED is the header's figure: taxes and fees less the running programmes "
            + "- interest, pensions, EI, the grants, care, schools, the police and transit - plus the utilities' net, at "
            + "today's tax rates. The budget adds land, buildings and the smaller lines; the cash adds borrowing "
            + "and your own moves.\n\n"
            + "So the middle tile is the budget's surplus or deficit, and the last is what the balance actually "
            + "did between two presses of the arrow. The steps between them are the money that moved without being "
            + "a budget line: paper raised and repaid, reserves, capital put into the bank, bonds bought back, the "
            + "students' loans, and the one the budget leaves out (the city's own repairs). "
            + "“Not accounted for” is what is left after them - timing between the books and the money, "
            + "such as a coupon booked the month it is charged and paid the month after, and anything not yet "
            + "journalled - printed rather than folded in, because a bridge that hides its own gap is not a bridge.";

    /** The dials' step's tooltip (the spec's B7). */
    static final String DIALS_INFO = "EARNED is read at today's tax rates; the budget was struck at the month's. "
            + "A dial moved since the last press moves EARNED and not the budget, by exactly this much. The next "
            + "press strikes the month at the new rates and it goes.";

    /** The "not accounted for" step's tooltip. */
    static final String RESIDUAL_INFO = "What the named steps do not explain: timing between the books and the money, "
            + "and anything not yet journalled. Printed, not folded in.";

    /**
     * FROM EARNED TO BANKED: three tiles - the header's figure, the budget,
     * the cash - and the steps between each pair, every one a model figure
     * by name (Game.getEarnedToBudget() and its residual; the bridge's
     * raised and repaid, the journal and its residual), so the screen adds
     * nothing up. A step opens where it is decided.
     */
    VBox bridgeCard(NationalAccounts na) {
        boolean banked = ui.game.hasTreasuryMonth();
        double income = ui.game.getIncome();
        double balance = na.getBalance();

        List<Anchor> anchors = new ArrayList<>();
        anchors.add(new Anchor("EARNED", Icons.COIN, s(income), "the header's figure", null, null));
        anchors.add(new Anchor(balance >= 0 ? "SURPLUS" : "DEFICIT", Icons.GOVERNMENT, s(balance), "the budget",
                null, () -> showOnOverview(BALANCE)));
        List<Node> chips = new ArrayList<>();
        if (banked) {
            BridgeStep most = mostly(balance);
            if (most != null) chips.add(chip("mostly: " + most.label() + " " + s(most.amount()), Palette.TEXT_LABEL));
            double printed = ui.game.getCentralBank().getAdvancedToTreasury();
            if (printed > 0) {
                Label p = chip("printed " + m(printed), Palette.MONEY);
                p.setGraphic(icon(Icons.BANK, Palette.MONEY, 11));
                p.setGraphicTextGap(4);
                chips.add(p);
            }
        }
        anchors.add(new Anchor("BANKED", Icons.FINANCES, banked ? s(ui.game.getTreasuryChange()) : "—",
                banked ? m(ui.game.getTreasuryOpening()) + " → " + m(ui.game.getTreasuryClosing())
                       : "after the first month closes",
                chips, null));

        List<List<BridgeStep>> steps = List.of(earnedSteps(), banked ? cashSteps() : List.of());

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox head = new HBox(Palette.GAP, caption("FROM EARNED TO BANKED", BRIDGE_INFO), gap,
                hint(banked ? "last month, press to press" : "the cash after the first month closes"));
        head.setAlignment(Pos.CENTER_LEFT);

        VBox card = card(head,
                bridge(anchors, steps, GovernmentScreen::s, Palette.MONEY, Palette.MONEY_DARK, 5, open, "bridge",
                        ui::redraw));
        if (banked) {
            card.getChildren().add(details("treasury", "the treasury's month, line by line", open, ui::redraw,
                    this::treasuryStatement));
        }
        return card;
    }

    /** The steps from EARNED to the budget: Game.getEarnedToBudget() a line each, and what they leave - today's dials. */
    List<BridgeStep> earnedSteps() {
        List<BridgeStep> out = new ArrayList<>();
        for (TreasuryJournal.Entry e : ui.game.getEarnedToBudget()) {
            Door d = doorOf(e.label());
            BridgeStep step = BridgeStep.of(e.label(), e.amount(), iconOf(e.label()));
            if (d != null) step = step.go(d.go());
            out.add(step);
        }
        out.add(BridgeStep.of("Today's dials, not the month's", ui.game.getEarnedResidual(), Icons.POLICY)
                .tip(DIALS_INFO).go(() -> openPolicy("Taxes", PolicyScreen.POLICY_HOME)));
        return out;
    }

    /** The steps from the budget to the cash: paper raised and repaid, the journal's lines in the player's words, and what is not accounted for. */
    List<BridgeStep> cashSteps() {
        List<BridgeStep> out = new ArrayList<>();
        Runnable borrow = () -> openFinances("Borrow", "At home");
        out.add(BridgeStep.of("Raised by issuing paper", ui.game.getTreasuryRaised(), Icons.FINANCES).go(borrow));
        out.add(BridgeStep.of("Principal repaid", -ui.game.getTreasuryRepaid(), Icons.FINANCES).go(borrow)
                .tip("Not spending: the money went out and the debt went down by the same amount."));
        for (TreasuryJournal.Entry e : ui.game.getTreasuryJournal()) {
            BridgeStep step = BridgeStep.of(e.label(), e.amount(), journalIcon(e.label()));
            Runnable go = journalDoor(e.label());
            if (go != null) step = step.go(go);
            out.add(step);
        }
        out.add(BridgeStep.of("Not accounted for", ui.game.getTreasuryResidual(), null).tip(RESIDUAL_INFO).always());
        return out;
    }

    /** The biggest step from the budget to the cash, when the cash moved more by those steps than by the budget (P4's alert, as a chip) - or null. */
    BridgeStep mostly(double balance) {
        double change = ui.game.getTreasuryChange();
        if (!(Math.abs(change - balance) > Math.abs(balance))) return null;
        BridgeStep most = null;
        for (BridgeStep st : cashSteps()) {
            if (most == null || Math.abs(st.amount()) > Math.abs(most.amount())) most = st;
        }
        return most == null || Math.abs(most.amount()) < .5 ? null : most;
    }

    /** A journal line's icon, by the words TreasuryJournal records it in. */
    static String journalIcon(String label) {
        if (label.contains("reserves") && !label.startsWith("Bought land")) return Icons.TRADE;
        if (label.startsWith("Bought land")) return Icons.LAND;
        if (label.contains("fund")) return Icons.FINANCES;
        if (label.contains("bank") && !label.contains("central")) return Icons.BANK;
        if (label.contains("central bank") || label.contains("arrears")) return Icons.BANK;
        if (label.contains("bond")) return Icons.FINANCES;
        if (label.contains("students")) return Icons.EDUCATION;
        if (label.contains("Repaired") || label.contains("demolition")) return Icons.BUILD;
        if (label.contains("transit")) return Icons.ROADS;
        return Icons.COIN;
    }

    /** ...and where it is decided (the spec's section 4): reserves on Trade, the fund on Finances, the bank's shares on Bank, the central bank on Policy, repairs and salvage on Build's construction page, fares on Infrastructure (no journal line names transit since 0.7.49, when the fares became a budget line, so that branch and journalIcon()'s are reached by nothing). */
    Runnable journalDoor(String label) {
        if (label.startsWith("Bought land")) return () -> ui.landScreen.showLandMenu();
        if (label.contains("reserves")) return () -> ui.tradeScreen.showForeignMenu();
        if (label.contains("fund")) return () -> openFinances("The city's fund", "Holdings");
        if (label.contains("central bank") || label.contains("arrears")) return () -> openPolicy("Money", "The policy rate");
        if (label.contains("bank")) return () -> ui.bankScreen.showBankMenu();
        if (label.contains("bond")) return () -> openFinances("The book", "Buy back");
        if (label.contains("students")) return () -> openPolicy("Promises", "Schools");
        if (label.contains("Repaired") || label.contains("demolition")) return () -> ui.constructionScreen.show();
        if (label.contains("transit")) return () -> ui.infrastructureScreen.open("Transit");
        return null;
    }

    /**
     * The treasury's month as the old page said it, in the fold: opened
     * with, closed with, grew or fell by; the budget, the paper and every
     * journal line with what was not accounted for; and the change. The
     * "of the change" column is gone (B6: a step bigger than the change it
     * explains read 1,038%).
     */
    VBox treasuryStatement() {
        double opening = ui.game.getTreasuryOpening();
        double closing = ui.game.getTreasuryClosing();
        double change  = ui.game.getTreasuryChange();
        double booked  = ui.game.getTreasurySurplus();

        VBox c = column(
                statementLine("Opened the month with", moneyFull(opening)),
                statementLine("Closed the month with", moneyFull(closing)),
                statementTotal(change >= 0 ? "The balance GREW by" : "The balance FELL by",
                        moneyFull(Math.abs(change)), null));
        javafx.scene.layout.GridPane grid = grid(new double[] {380, 160}, rightAfterFirst(2));
        gridHead(grid, "", "moved the balance");
        int row = 1;
        row = moved(grid, row, booked >= 0 ? "Surplus on the budget" : "Deficit on the budget", booked, false);
        row = moved(grid, row, "Raised by issuing paper", ui.game.getTreasuryRaised(), false);
        row = moved(grid, row, "Principal repaid to lenders", -ui.game.getTreasuryRepaid(), false);
        row = moved(grid, row, "Everything else the treasury did", ui.game.getTreasuryUnexplained(), false);
        for (TreasuryJournal.Entry e : ui.game.getTreasuryJournal()) row = moved(grid, row, e.label(), e.amount(), true);
        moved(grid, row, "Not accounted for", ui.game.getTreasuryResidual(), true);
        c.getChildren().add(grid);
        c.getChildren().add(statementTotal("Which is the change", s(change), null));
        return c;
    }

    /** One row of the fold's grid: a movement, signed, and a zero written as one. */
    static int moved(javafx.scene.layout.GridPane grid, int row, String label, double amount, boolean under) {
        Label what = gridCell(label, under ? Palette.TEXT_MUTED : Palette.TEXT_BODY, Palette.SIZE_CAPTION, false);
        if (under) what.setStyle(what.getStyle() + " -fx-padding: 0 0 0 16;");
        grid.add(what, 0, row);
        grid.add(gridCell(s(amount), Math.abs(amount) < .5 ? Palette.TEXT_SPENT : Palette.TEXT_BODY,
                Palette.SIZE_CAPTION, true), 1, row);
        return row + 1;
    }

    /* ------------------------------ the central bank ------------------------------ */

    /** P5: the advances. */
    String advancesInfo() {
        return String.format("When the treasury is below zero at the top of a month, the central bank advances the gap "
                + "in money it makes, at the policy rate; cash above zero repays it before anything else. It will "
                + "advance up to %.0f months of the treasury's revenue. Past that, pensions, EI, health, the schools, "
                + "the city's own wages and its debts are still paid, and everything else is paid only from cash the "
                + "treasury actually has.", (double) ui.game.getCentralBank().getAdvancesCeilingMonths());
    }

    /** P6: the arrears. */
    static final String ARREARS_INFO = "What the ceiling cut: owed, with no interest on it, and paid down out of the "
            + "first cash above zero once the central bank has been repaid - before anything discretionary is paid again.";

    /**
     * CENTRAL BANK (the old page's "What it owes its central bank"), shown
     * when the city owes it, has ever been advanced anything, or has
     * arrears: a ring of the advances against the ceiling - red once the
     * ceiling binds, a verdict - what was printed this month and since
     * founding and repaid, and what is owed and unpaid by line, in red.
     */
    VBox centralBankCard(CentralBank cb) {
        double owes = cb.getAdvancesToTreasury(), ceiling = cb.ceiling();
        String tone = cb.ceilingBound() ? Palette.BAD : Palette.MONEY;
        Region ring = ring(ceiling > 0 ? owes / ceiling : 0, tone, 64, 8,
                ceiling > 0 ? String.format("%.0f%%", owes / ceiling * 100) : "—", 13);
        VBox words = new VBox(4,
                words("owes " + m(owes) + " of a " + m(ceiling) + " ceiling ("
                        + cb.getAdvancesCeilingMonths() + " months of revenue)", Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL),
                cardLine("Printed this month", m(cb.getAdvancedToTreasury()), null),
                cardLine("...since founding", m(cb.getPrintedLifetime()), Palette.TEXT_MUTED),
                cardLine("Repaid this month", m(cb.getRepaidByTreasury()), Palette.TEXT_MUTED));
        HBox.setHgrow(words, Priority.ALWAYS);
        HBox top = new HBox(14, ring, words);
        top.setAlignment(Pos.CENTER_LEFT);
        VBox card = card(cardHead(Icons.BANK, Palette.MONEY, "CENTRAL BANK", advancesInfo(),
                door("Policy › Money", Palette.ACCENT, () -> openPolicy("Money", "The policy rate"))), top);

        java.util.Map<TreasuryLine, Double> owed = ui.game.getArrearsByLine();
        if (!owed.isEmpty()) {
            card.getChildren().add(caption("OWED AND UNPAID, BY LINE", ARREARS_INFO));
            for (java.util.Map.Entry<TreasuryLine, Double> line : owed.entrySet()) {
                card.getChildren().add(cardLine(line.getKey().label, m(line.getValue()), Palette.BAD));
            }
            card.getChildren().add(cardLine("Owed altogether", m(ui.game.getArrearsTotal()), Palette.BAD));
        }
        return card;
    }

    /* ------------------------------ against the economy ------------------------------ */

    /** P8: why a month against a year. */
    static final String ECONOMY_INFO = "A year of revenue against a year of output is the only honest way to "
            + "compare a budget with anything - another city, another year, or a rule of thumb. What came in, what "
            + "went out and the balance are their last twelve months once City History has them; until then, and "
            + "for the staff's wages, the month is annualised, so a month that bought a lot of buildings reads as a year of them. "
            + "The staff are the care and schools staff; the police's and transit's wages are their own lines under Spending.";

    /** P7: no month of output yet. */
    static final String NO_YEAR = "There is no output recorded yet, so nothing here can be put in "
            + "proportion. A month played and this fills in, scaled up to a year until there are twelve.";

    /**
     * AGAINST THE ECONOMY: what was taken in, paid out and kept or short - the surplus or deficit - and
     * the care and schools staff's wages, as bars on one scale of per cent of
     * a year's GDP - a month and a year in their tooltips - and the year and
     * a head under them. "The city's own staff" was care and schools only,
     * and said so now (B12, D18: police, the utilities and transit staff
     * wait for a payroll of every city post, with the model batch). Since
     * 0.7.49 (B9) transit's wages are paid, on Spending's "Transit" line, and
     * the (i) says where the police's and transit's are.
     */
    VBox economyCard(NationalAccounts na) {
        double annual = annualGdp(na);
        VBox card = card(cardHead(Icons.REPORTS, Palette.MONEY, "AGAINST THE ECONOMY", ECONOMY_INFO,
                door("Output", Palette.ACCENT, () -> open("Output", null))));
        if (annual <= 0) {
            card.getChildren().add(words(NO_YEAR, Palette.SIZE_LABEL + 1, Palette.TEXT_MUTED));
            return card;
        }
        double revenue = na.getTotalRevenue(), spending = na.getTotalExpenses(), balance = na.getBalance();
        double staff = ui.game.getHealthcare().getPayroll() + ui.game.getEducation().getPayroll();
        // Each bar's year (B7): the last twelve months where History records the line, the month × 12 where not.
        double balanceYear = yearOf(balance, SURPLUS_KEYS);
        double[] monthly = {revenue, spending, balance, staff};
        double[] yearly = {yearOf(revenue, REVENUE_KEYS), spendingYear(spending), Math.abs(balanceYear), staff * 12};
        boolean[] measured = {trailing(REVENUE_KEYS), trailing(REVENUE_KEYS) && trailing(SURPLUS_KEYS), trailing(SURPLUS_KEYS), false};
        String[] names = {"Taken in", "Paid out", balanceYear >= 0 ? "Surplus" : "Deficit", "Care and schools staff"};
        String[] colours = {Palette.MONEY, Palette.MONEY_DARK, Palette.MONEY_LIGHT, Palette.PEOPLE};
        double top = 0;
        for (double v : yearly) top = Math.max(top, v / annual * 100);
        List<ScaleRow> rows = new ArrayList<>();
        for (int i = 0; i < yearly.length; i++) {
            double pc = yearly[i] / annual * 100;
            String month = i == 2 ? s(monthly[i]) : m(monthly[i]);
            rows.add(ScaleRow.of(names[i], String.format("%.2f%%", pc),
                    List.of(Run.of(0, pc, colours[i]).tip(names[i] + "\n" + (measured[i]
                            ? m(yearly[i]) + " over the last twelve months · " + month + " this month"
                            : month + " a month · " + m(yearly[i]) + " a year")))));
        }
        card.getChildren().add(scaleRows(rows, Math.max(top, 1e-9), List.of(), 150, 64, 10));
        long heads = ui.game.getPopulationManager().getPopulation();
        String line = (gdpEstimated(na) ? "GDP, annualised " : "annual GDP ") + m(annual)
                + (heads > 0 ? " · " + moneyFull(annual / heads) + " a head" : "");
        card.getChildren().add(gdpEstimated(na)
                ? noteLine(line, String.format("This city has only %d months of output recorded, so the year is "
                        + "scaled up from those rather than measured. It settles as the twelfth month goes by.",
                        na.getMonthsRecorded()), 560)
                : words(line, Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        return card;
    }

    /* =====================================================================
       THE TWO LISTS (0.7.31: ranked bars).

       Most lines open. Jerus: "you can for example click business tax and it
       expands to show all the info, same with sales, wages, property etc" -
       "by all the info i mean which sector is giving how much".

       That is the question the old screen could not answer at all. Business tax
       was one figure; it is every business in the city and the bank, each at
       its own rate, and which of them is carrying the city is a thing a player
       would change a policy over. Ranked by size now, a bar each in its ring's
       colour, the lines at nothing folded into one muted row, and a door on
       every line to where it is decided (the spec's D9, D15).
       ===================================================================== */

    static List<String> revenueNames() {
        return List.of("Business tax", "Sales tax", "Wage tax",
                "Property tax", "Pension contributions", "EI premiums", "Utility income",
                "Healthcare fees", "School fees", "Land sold", "Health premiums",
                "Student loan interest", "Central bank remittance", "Mortgage insurance premiums",
                "Transfer from the fund", "Transit fares");
    }

    List<Double> revenueAmounts(NationalAccounts na) {
        return List.of(
                na.getTaxBusiness() + na.getTaxIndustrial(),
                na.getTaxSales(),
                na.getTaxWage(),
                na.getPropertyTax(),
                na.getContributions(),
                na.getEiPremiums(),
                na.getUtilityIncome(),
                na.getHealthFees(),
                na.getEducationFees(),
                na.getLandSales(),
                // ...and the health premium off every wage (2026-09-19), on the
                // end so the list and the ring keep their order.
                na.getHealthPremiums(),
                // ...and the interest the graduates pay on their student
                // loans (2026-09-21), on the end for the same reason; the
                // principal they repay is not revenue and is on the bridge.
                na.getStudentLoanInterest(),
                // ...and the central bank's profit, remitted (0.7.0): what the
                // city paid itself in interest, less what reserves cost.
                na.getCentralBankRemittance(),
                // ...and the premiums the landlords paid on the mortgages the
                // city insures (0.7.11), on the end for the same reason.
                na.getMortgagePremiums(),
                // ...and the city's fund's withdrawal, a share of its worth a month (0.7.14; the dial 0.7.48).
                na.getFundTransfer(),
                // ...and the transit fares (0.7.49, B9): under the total since, where
                // they were named outside it.
                na.getTransitFares());
    }

    /*
     * REPAIRS LEFT THIS LIST IN 0.7.31, and stayed on the page. It joined on
     * 2026-09-09 - Jerus: "all buildings need maintenance, and make sure they
     * get billed" - as a real line: the city owns the roads, the schools, the
     * hospitals and both utility plants, so the treasury pays their repair
     * bill. But NationalAccounts.getTotalExpenses() does not carry it, so the
     * list summed to more than "Paid out altogether" and the ring's key to
     * 111% (the spec's B2). The rings and lists show the budget exactly as
     * NationalAccounts strikes it now, and repairs are named under the total
     * as "outside the budget's total", and on the bridge, where the cash pays
     * them (D4). Carrying them in NationalAccounts is a model batch for Jerus.
     */
    static List<String> spendingNames() {
        return List.of("Pensions", "EI", "Student grants", "Healthcare", "Education",
                "Police and prisons", "Buildings", "Land bought", "Debt interest",
                "Interest to the central bank", "Subsidies", "Mortgage insurance claims", "Food assistance",
                "Transit");
    }

    List<Double> spendingAmounts(NationalAccounts na) {
        return List.of(
                na.getPensions(),
                na.getEiBenefits(),
                na.getStudentGrants(),
                na.getHealthSpending(),
                na.getEducationSpending(),
                na.getSafetySpending(),
                na.getCapitalSpending(),
                na.getLandPurchases(),
                na.getInterestExpense(),
                // ...and on the central bank's advances (0.7.0), on the end so
                // the list and the ring keep their order.
                na.getCentralBankInterest(),
                // ...and the standing policy's subsidies (0.7.1): in
                // getTotalExpenses() since they were first paid, and on no line
                // of this tab until then, so the lines did not add up to the total
                // under them in any month a sector was topped up.
                na.getSubsidies(),
                // ...and what the mortgage insurance paid the bank (0.7.11), on
                // the end.
                na.getMortgageClaims(),
                // ...and the food vouchers (0.7.45; the UI spec's B2): in getTotalExpenses() since
                // 0.7.43 and on no line here, so the list and the ring stopped adding up to the
                // total under them once the dial was on - 0.7.31's B2 again.
                na.getFoodAssistance(),
                // ...and transit's wages and upkeep, paid by the treasury since 0.7.49 (B9).
                na.getTransitSpending());
    }

    /** The column heads' widths: a month, of the budget, of GDP. */
    static final double[] LIST_COLUMNS = {100, 60, 70};

    /** ...and the door's at the end of a row: "set it ›", "Land office ›". */
    static final double LIST_DOOR = 96;

    /** P19, on the "of GDP" head while the year is scaled up. */
    String scaledYearInfo(NationalAccounts na) {
        return String.format("The GDP column is against a year scaled up from the %d months of output recorded so "
                + "far, not a year measured. It settles as the twelfth month goes by.", na.getMonthsRecorded());
    }

    /**
     * A list page: its head line ("$162.5M taken in · 34.4% of annual GDP ·
     * the month"), the column heads, a ranked bar a line - every line with a
     * door, the ones that open with their payers - the lines at nothing
     * folded into one muted row, the total, neutral (B4), and what the
     * budget's total leaves out.
     */
    void listPage(VBox page, String pageName, String verb, List<String> names, List<Double> amounts,
                  double total, NationalAccounts na, java.util.function.Function<String, java.util.function.Supplier<Node>> opens,
                  java.util.function.Function<String, String> word, String totalWords, Node outside) {
        double annual = annualGdp(na);
        // The list's year (B7): what came in, or what went out, over the last twelve months once History has them.
        double totalYear = "Spending".equals(pageName) ? spendingYear(total) : yearOf(total, REVENUE_KEYS);

        javafx.scene.layout.FlowPane lead = new javafx.scene.layout.FlowPane(8, 0);
        lead.setRowValignment(javafx.geometry.VPos.BASELINE);
        lead.getChildren().addAll(figure(m(total), 22, Palette.TEXT_HEAD),
                words(verb + (annual > 0 ? String.format(" · %.1f%% %s", totalYear / annual * 100, ofAnnualGdp(na)) : "")
                        + " · " + CityCalendar.format(ui.game.getMonth()), Palette.SIZE_BODY + 1, Palette.TEXT_LABEL));
        page.getChildren().add(lead);

        // Coloured to match the ring on the overview, so a line a player picked
        // out of the picture is the same colour when they come here for the
        // detail. Anything outside the top five is the ring's grey rest.
        java.util.Map<String, String> colours = new java.util.HashMap<>();
        for (Slice sl : topSlices(names, drawn(amounts), Palette.CATEGORIES)) colours.put(sl.name(), sl.colour());

        List<Integer> order = new ArrayList<>();
        List<String> nothing = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            if (Math.abs(amounts.get(i)) < .5) nothing.add(names.get(i));
            else order.add(i);
        }
        order.sort((a, b) -> Double.compare(amounts.get(b), amounts.get(a)));

        List<RankRow> rows = new ArrayList<>();
        for (int i : order) {
            String name = names.get(i);
            double amount = amounts.get(i);
            Door d = doorOf(name);
            List<String> keys = LINE_KEYS.getOrDefault(name, List.of());
            rows.add(new RankRow(lineKey(pageName, name), iconOf(name), name, amount,
                    colours.getOrDefault(name, Palette.RAMP_REST),
                    List.of(m(amount), minus(String.format("%.1f%%", total != 0 ? amount / total * 100 : 0)), ofGdpYear(keys, amount, annual)),
                    name + "\n" + m(amount) + " a month · " + m(yearOf(amount, keys))
                            + (trailing(keys) ? " over the last twelve months" : " a year"),
                    opens.apply(name), word.apply(name), d == null ? null : d.words(), d == null ? null : d.go()));
        }

        page.getChildren().add(listHead(na));
        VBox list = rankBars(rows, total, LIST_COLUMNS, LIST_DOOR, open, ui::redraw);
        for (Node n : list.getChildren()) {
            Object key = n.getProperties().get(RANK_KEY);
            if (key != null) targets.put(key.toString(), n);
        }
        page.getChildren().add(list);
        if (!nothing.isEmpty()) {
            page.getChildren().add(words("nothing this month: " + String.join(" · ", nothing),
                    Palette.SIZE_LABEL + 1, Palette.TEXT_SPENT));
        }

        Label t = words(totalWords, Palette.SIZE_BODY + 1, Palette.TEXT_HEAD);
        t.setStyle(Palette.strong(Palette.SIZE_BODY + 1, Palette.TEXT_HEAD));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox totalRow = new HBox(Palette.GAP, t, gap, figure(m(total), Palette.SIZE_BODY + 3, Palette.TEXT_HEAD));
        totalRow.setAlignment(Pos.CENTER_LEFT);
        totalRow.setStyle("-fx-padding: 4 6 0 6; -fx-border-color: " + Palette.EDGE + "; -fx-border-width: 1 0 0 0;");
        page.getChildren().add(totalRow);
        if (outside != null) page.getChildren().add(outside);
    }

    /** The column heads over a list, in the rows' own columns; the "of GDP" head carries P19 while the year is scaled. */
    HBox listHead(NationalAccounts na) {
        Region lead = new Region();
        lead.setMinWidth(236);
        lead.setPrefWidth(236);
        Region grow = new Region();
        HBox.setHgrow(grow, Priority.ALWAYS);
        HBox row = new HBox(10, lead, grow);
        String[] heads = {"a month", "of the budget", "of GDP"};
        for (int c = 0; c < heads.length; c++) {
            Label h = new Label(heads[c]);
            h.setWrapText(true);
            h.setAlignment(Pos.CENTER_RIGHT);
            h.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);
            h.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
            Node cell = h;
            if (c == 2 && gdpEstimated(na)) {
                HBox withInfo = new HBox(3, h, infoButton(scaledYearInfo(na), true));
                withInfo.setAlignment(Pos.CENTER_RIGHT);
                cell = withInfo;
            }
            HBox slot = new HBox(cell);
            slot.setAlignment(Pos.CENTER_RIGHT);
            slot.setMinWidth(LIST_COLUMNS[c]);
            slot.setPrefWidth(LIST_COLUMNS[c]);
            row.getChildren().add(slot);
        }
        Region door = new Region();
        door.setMinWidth(LIST_DOOR);
        door.setPrefWidth(LIST_DOOR);
        row.getChildren().add(door);
        row.setAlignment(Pos.BOTTOM_LEFT);
        row.setStyle("-fx-padding: 0 6 0 6;");
        return row;
    }

    /* ----------------------------- who pays what ----------------------------- */

    /** One payer inside an opened line. */
    record Payer(String who, double amount, String rate) { }

    /**
     * A line opened (0.7.31): its payers as one bar - the row's colour at two
     * strengths, alternating - then a payer row each, and the old note behind
     * the (i) on the panel's first line. At the statement's width (D14).
     */
    VBox payers(String caption, String note, List<Payer> who, double total, String colour) {
        VBox box = new VBox(2);
        box.setMaxWidth(STATEMENT);
        box.getChildren().add(noteLine(caption, note, STATEMENT - 22));
        List<Slice> parts = new ArrayList<>();
        int k = 0;
        for (Payer p : who) {
            if (p.amount() <= 0) continue;
            parts.add(new Slice(p.who(), p.amount(), k++ % 2 == 0 ? colour : tint(colour, .55)));
        }
        if (!parts.isEmpty()) box.getChildren().add(stackedBar(parts, STATEMENT - 22, v -> m(v)));
        for (Payer p : who) box.getChildren().add(payerRow(p.who(), p.amount(), total, p.rate()));
        return box;
    }

    /** A line's colour as the list draws it: its ring slice's, or the grey rest. */
    String colourOf(String line, List<String> names, List<Double> amounts) {
        for (Slice sl : topSlices(names, drawn(amounts), Palette.CATEGORIES)) if (sl.name().equals(line)) return sl.colour();
        return Palette.RAMP_REST;
    }

    /** Business tax, by the companies that pay it - every sector, and the bank. */
    VBox businessTaxDetail(double total, String colour) {
        SectorBooks books = ui.game.getSectorBooks();
        TaxPolicy policy = ui.game.getEconomyManager().getTaxPolicy();
        List<Payer> who = new ArrayList<>();
        for (Sector sector : ui.game.getSectors().all()) {
            who.add(new Payer(sector.label(), books.get(sector).tax(),
                    String.format("%.1f%%", policy.effectiveProfitRate(sector) * 100)));
        }
        // The rate Game charges the bank at - retail's profit rate, its
        // counters being retail's - rather than the income rate, which
        // since 0.7.4 is three rates and missed retail's offset before that.
        who.add(new Payer("Bank", ui.game.getEconomyManager().getBankTax(),
                String.format("%.1f%%", policy.effectiveProfitRate(ui.game.getSectors().retail()) * 100)));
        return payers("by company, at its profit rate",
                "Charged on each company's own profit and never refunded on a loss, so a "
                + "sector that lost money this month simply paid nothing.", who, total, colour);
    }

    /** Sales tax, by the sector that remitted it. */
    VBox salesTaxDetail(double total, String colour) {
        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        List<Payer> who = new ArrayList<>();
        for (Sector sector : ui.game.getSectors().all()) {
            who.add(new Payer(sector.label(), em.getSectorSalesTax(sector),
                    String.format("%.1f%%", policy.effectiveSalesRate(sector) * 100)));
        }
        return payers("by the sector that remitted it, at its rate",
                "The rate follows the producer, so the producer remits - what it owes on "
                + "what it sold, less the credit on what it bought. A sector in a refund "
                + "position shows as a negative.", who, total, colour);
    }

    /** Wage tax, by the pay tier that earned the wages. */
    VBox wageTaxDetail(double total, String colour) {
        HouseholdAccounts hh = ui.game.getHouseholds();
        List<Payer> who = new ArrayList<>();
        for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
            who.add(new Payer(hh.getRowLabel(t), hh.getRowTax(t),
                    hh.getRowWages(t) > 0 ? String.format("%.1f%%", hh.getRowTax(t) / hh.getRowWages(t) * 100) : "—"));
        }
        return payers("by pay tier, at the rate it paid", String.format(
                "Banded, so the rate on the right is what each tier actually paid rather "
                + "than the headline %.0f%% wage rate. The bands are set per skill level on "
                + "the tax policy screen.",
                ui.game.getEconomyManager().getTaxPolicy().getWageTaxRate() * 100), who, total, colour);
    }

    /** Pension contributions, by the tier that paid them. */
    VBox contributionsDetail(double total, String colour) {
        HouseholdAccounts hh = ui.game.getHouseholds();
        List<Payer> who = new ArrayList<>();
        for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
            who.add(new Payer(hh.getRowLabel(t), hh.getRowContributions(t), null));
        }
        return payers("by pay tier", String.format(
                "%.2f%% of every wage in the city, at one rate for everybody. It is not a "
                + "fund - this month's contributions pay this month's pensions.",
                ui.game.getEconomyManager().getTaxPolicy().getContributionRate() * 100), who, total, colour);
    }

    /** Property tax, by the sector it is assessed on. */
    VBox propertyTaxDetail(double total, String colour) {
        EconomyManager em = ui.game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        List<Payer> who = new ArrayList<>();
        for (Sector sector : ui.game.getSectors().all()) {
            who.add(new Payer(sector.label(), em.getPropertyTaxFor(sector),
                    String.format("%.2f%%", policy.effectivePropertyRate(sector) * 100)));
        }
        return payers("by the sector it is assessed on, at its rate",
                "Assessed on land plus buildings at what that sector's own balance sheet "
                + "claims they are worth - a business is taxed on the value it books. The "
                + "city's own buildings are exempt: taxing them would move money from one "
                + "pocket to the other and make the utilities look worse for nothing.", who, total, colour);
    }

    /** Healthcare fees, by the kind of care that charged them - and the burials and cremations, which charge with no care bill of their own. */
    VBox healthFeeDetail(double total, String colour) {
        Healthcare service = ui.game.getHealthcare();
        List<Payer> who = new ArrayList<>();
        for (CareType care : new CareType[]{CareType.GENERAL, CareType.CHILDCARE, CareType.SENIOR}) {
            who.add(new Payer(care.getLabel(), service.feesFrom(care), cash(service.feeNow(care))));
        }
        who.add(new Payer("Burials", service.getBurials() * service.feeNow(CareType.BURIAL),
                cash(service.feeNow(CareType.BURIAL))));
        who.add(new Payer("Cremations", service.getCremations() * service.feeNow(CareType.CREMATION),
                cash(service.feeNow(CareType.CREMATION))));
        return payers("by kind of care, at its fee",
                "The rate column is the fee each one charges; a burial and a cremation are charged too. The "
                + "doctor, the nursery, the almshouse and the churchyard the city was founded with charge their "
                + "fees and cost it nothing, so a city with no care buildings of its own takes these with no care "
                + "bill. Care runs at a deficit by design - the Services tab has what the rest of it buys.",
                who, total, colour);
    }

    /** School fees, by the course. */
    VBox schoolFeeDetail(double total, String colour) {
        Education schools = ui.game.getEducation();
        double subsidy = schools.getTuitionSubsidy();
        List<Payer> who = new ArrayList<>();
        for (EducationType course : EducationType.values()) {
            if (course == EducationType.NONE) continue;
            double students = schools.getEnrolled(course);
            if (students < .5) continue;
            who.add(new Payer(course.getLabel(), schools.feeFor(course) * students * (1 - subsidy),
                    cash(schools.outOfPocket(course))));
        }
        return payers("by course, at a seat's price to a family", String.format(
                "What households paid after the city's %.0f%% subsidy. The rate column is "
                + "what one seat costs a family a month.", subsidy * 100), who, total, colour);
    }

    /** P20: the fund's transfer. */
    static final String FUND_INFO = String.format("The withdrawal dial's share of everything the city's fund holds, a "
            + "month - by default a twelfth of %.0f%% a year, Norway's fiscal rule - from its cash, and over the default "
            + "from what it sells. The Finances tab has the fund and its dial.", TreasuryFund.TRANSFER_RATE * 100);

    /** THE TRANSFER FROM THE CITY'S FUND (0.7.14), opened from its line: due, paid and short this month, and the fund it is struck on. Every figure a getter. */
    VBox fundTransferDetail() {
        TreasuryFund fund = ui.game.getFund();
        return column(noteLine("due, paid and short this month", FUND_INFO, STATEMENT - 22),
                statementLine("Due on what the fund was worth", moneyFull(fund.getTransferDue())),
                statementLine("Paid from its cash", moneyFull(fund.getTransferPaid())),
                // Over the default withdrawal (0.7.48): the short is what the step sells for, and last month's sale paid late.
                fund.getToRaise() > 0 ? statementLine("Sold for, to pay next month", moneyFull(fund.getToRaise()))
                        : statementLine("Not paid, for want of cash", moneyFull(fund.getTransferShort()),
                                fund.getTransferShort() > 0 ? Palette.WARN : null),
                fund.getTransferPaidLate() > 0 ? statementLine("Paid from last month's sale", moneyFull(fund.getTransferPaidLate())) : null,
                statementLine("What the fund is worth now", moneyFull(ui.game.fundValue())));
    }

    /** P21: the mortgage insurance. */
    static String insuranceInfo() {
        return String.format("The landlords pay the city %.2f%% of every mortgage it insures - CMHC's premium on "
                + "rental housing, added to the loan - and when a landlord's debt is written down the city "
                + "pays the bank what came off its mortgages, whatever the treasury holds: a guarantee is "
                + "a promise. A premium is booked the month it is paid, where CMHC would earn it over "
                + "the life of the loan.", Mortgage.premiumRate() * 100);
    }

    /**
     * THE CITY'S MORTGAGE INSURANCE (0.7.11), opened from either of its two
     * lines: the premiums taken this month and what they were on, what the
     * insurance paid the bank, and the book over the city's life - does the
     * city make or lose money insuring housing. Every figure a getter; the
     * claims are a line, not a verdict (B4), and the book's ahead or behind
     * is one.
     */
    VBox mortgageInsuranceDetail() {
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        NationalAccounts na = ui.game.getEconomyManager().getNationalAccounts();
        double net = credit.getPremiumsTotal() - credit.getInsuredWrittenOffTotal();
        return column(noteLine("this month and since founding", insuranceInfo(), STATEMENT - 22),
                statementLine("Mortgages written this month", String.valueOf(credit.getMortgagesWrittenThisMonth())),
                statementLine("Premiums taken", moneyFull(na.getMortgagePremiums())),
                statementLine("Claims paid the bank", moneyFull(na.getMortgageClaims())),
                statementLine("Premiums since founding", moneyFull(credit.getPremiumsTotal())),
                statementLine("Claims since founding", moneyFull(credit.getInsuredWrittenOffTotal())),
                statementLine(net >= 0 ? "The insurance is ahead by" : "The insurance is behind by",
                        moneyFull(Math.abs(net)), net >= 0 ? Palette.GOOD : Palette.BAD),
                statementLine("Insured mortgages outstanding", moneyFull(credit.getInsuredPrincipal())));
    }

    /* ------------------------------ what it spends ------------------------------ */

    /** Healthcare spending, by the kind of care it is spent on. */
    VBox healthSpendDetail(double total, String colour) {
        BuildingManager bm = ui.game.getBuildingManager();
        double[] wages = ui.game.getPopulationManager().getWagesPerType();
        double[] fill = ui.game.getPopulationManager().getJobFillRate();
        List<Payer> who = new ArrayList<>();
        for (CareType care : new CareType[]{CareType.GENERAL, CareType.CHILDCARE,
                CareType.SENIOR, CareType.BURIAL, CareType.CREMATION}) {
            double cost = bm.getCarePayroll(care, wages, fill) + bm.getCareUpkeep(care);
            if (cost <= 0) continue;
            who.add(new Payer(care.getLabel(), cost, null));
        }
        return payers("by kind of care", String.format(
                "%s of it is wages and %s is keeping the buildings standing. Wages are "
                + "discounted by how many of the posts are actually filled, which is why "
                + "an understaffed ward is cheap and useless at once.",
                m(ui.game.getHealthcare().getPayroll()), m(ui.game.getHealthcare().getUpkeep())), who, total, colour);
    }

    /** Education spending, by the school it is spent on. */
    VBox educationSpendDetail(double total, String colour) {
        BuildingManager bm = ui.game.getBuildingManager();
        double[] wages = ui.game.getPopulationManager().getWagesPerType();
        double[] fill = ui.game.getPopulationManager().getJobFillRate();
        List<Payer> who = new ArrayList<>();
        for (EducationType course : EducationType.values()) {
            if (course == EducationType.NONE) continue;
            double cost = bm.getSchoolPayroll(course, wages, fill) + bm.getSchoolUpkeep(course);
            if (cost <= 0) continue;
            who.add(new Payer(course.getLabel(), cost, null));
        }
        who.add(new Payer("Tuition the city covers", ui.game.getEducation().getSubsidy(), null));
        return payers("by school, and the tuition the city covers",
                "The last line is the share of tuition the subsidy dial forgives. Note "
                + "that the city is already paying the teachers above it - see the note on "
                + "the education books about that being charged twice.", who, total, colour);
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
       same amount, so nothing was consumed. It gets its own line on THE DEBT
       card, clearly marked, because a player watching the treasury does not
       care which of the two took their money.
       =================================================================== */

    /** One kind of paper, totalled: its pieces, principal, coupons a month, the discounts it was sold at, and the soonest of its maturities. */
    record PaperKind(String name, int count, double principal,
                             double coupon, String note, double discount, int soonest) { }

    java.util.List<PaperKind> paperKinds() {

        java.util.Map<String, double[]> tally = new java.util.LinkedHashMap<>();
        for (String t : new String[] {"NOTE", "SERIAL", "TERM"}) {
            tally.put(t, new double[] {0, 0, 0, 0, Integer.MAX_VALUE});
        }

        for (Debt d : ui.game.getDebtManager().getDebt()) {
            double[] row = tally.get(d.getType());
            if (row == null) continue;
            row[0]++;
            row[1] += d.getOustandingPrincipal();
            row[2] += d.getMonthlyInterestExpense();
            row[3] += d.getIssueDiscount();
            row[4] = Math.min(row[4], d.getRemainingMonths());
        }

        java.util.List<PaperKind> kinds = new java.util.ArrayList<>();
        kinds.add(kind("Notes", tally.get("NOTE"), "discounted at issue — no coupon"));
        kinds.add(kind("Serial bonds", tally.get("SERIAL"), "coupon monthly, principal yearly"));
        kinds.add(kind("Term loans", tally.get("TERM"), "coupon monthly, face at the end"));
        return kinds;
    }

    static PaperKind kind(String name, double[] t, String note) {
        return new PaperKind(name, (int) t[0], t[1], t[2], note, t[3], t[0] > 0 ? (int) t[4] : 0);
    }

    /** One kind's row, or an empty one. Never null, so callers can just read it. */
    PaperKind paperKind(String name) {
        for (PaperKind k : paperKinds()) {
            if (k.name().equals(name)) return k;
        }
        return new PaperKind(name, 0, 0, 0, "", 0, 0);
    }

    /** P24: interest is the only part of debt service that is an expense. */
    String interestInfo(double repaid) {
        return repaid > 0
                ? String.format("Interest is the only part of debt service that is an expense, so it is the only "
                        + "part on the list above. %s of principal also left the treasury this month, and that is "
                        + "not spending — the money went out and the debt went down by the same amount. The "
                        + "bridge on Overview is where the two meet.", m(repaid))
                : "Interest is the only part of debt service that is an expense, so it is the only part on the "
                        + "list above. No principal fell due this month; when it does, it leaves the treasury "
                        + "without appearing here, and the bridge on Overview is where the two meet.";
    }

    /**
     * The interest line, opened into the paper it is charged on.
     *
     * @param total this month's whole interest bill, so each row can carry its
     *              share of it
     */
    VBox debtServiceDetail(double total, String colour) {
        double repaid = ui.game.getCityPrincipalRepaidThisMonth() + ui.game.getForeignPrincipalRepaidThisMonth();
        List<Payer> who = new ArrayList<>();
        for (PaperKind k : paperKinds()) {
            if (k.count() == 0) continue;
            who.add(new Payer(k.name() + "  ×" + k.count(), k.coupon(), m(k.principal()) + " owed"));
        }
        if (who.isEmpty()) return column(statementNote("The city owes nothing."));
        VBox box = payers("by the paper it is charged on", interestInfo(repaid), who, total, colour);
        /* --------------- and the money that is not on this statement --------------- */
        if (repaid > 0) box.getChildren().add(payerRow("Principal repaid — not an expense", repaid, 0, "left the treasury"));
        double foreignInterest = ui.game.getForeignInterestPaidThisMonth();
        if (foreignInterest > 0) box.getChildren().add(payerRow("...of which paid abroad", foreignInterest, total, "in dollars"));
        return box;
    }

    /**
     * Food assistance, by who it was paid to (0.7.45): each kind of
     * household's vouchers at the last sale (HouseholdBalance.foodAssistanceByRow()),
     * which add to the treasury's line - not the households' own books, which
     * carry it a month later.
     */
    VBox foodAssistanceDetail(double total, String colour) {
        HouseholdAccounts hh = ui.game.getHouseholds();
        HouseholdBalance bal = ui.game.getHouseholdBalance();
        double[] byRow = bal.foodAssistanceByRow();
        List<Payer> who = new ArrayList<>();
        for (int row = 0; row < Household.ROWS; row++) {
            if (byRow[row] > 0) who.add(new Payer(hh.getRowLabel(row), byRow[row], null));
        }
        who.sort((a, b) -> Double.compare(b.amount(), a.amount()));
        return payers("to whom, by kind of household", String.format("Vouchers toward the groceries of %s households whose"
                + " baskets take more than half of what they have after their bills, paid on the baskets they got at the"
                + " last sale - %s baskets' worth at the price charged. The households' own books carry it a month later,"
                + " as People's month shows.",
                people(bal.getHouseholdsAssisted()), people(bal.getFedByAssistance())), who, total, colour);
    }

    /** Pensions, and who they go to. */
    VBox pensionDetail(double total, String colour) {
        EconomyManager em = ui.game.getEconomyManager();
        HouseholdAccounts hh = ui.game.getHouseholds();
        return payers("to whom, at the pension a senior", String.format(
                "%s seniors at %s each. Contributions brought in %s, so the scheme covers "
                + "%.0f%% of itself and the rest comes out of general revenue.",
                people(em.getSeniors()), cash(em.getTaxPolicy().pensionPerSenior()),
                m(em.getContributions()), em.getPensionCoverage() * 100),
                List.of(new Payer("Paid to seniors", hh.getRowPensions(HouseholdAccounts.RETIRED),
                        cash(em.getTaxPolicy().pensionPerSenior()))), total, colour);
    }

    /**
     * The land line, opened (0.7.6): the land office is paid in US dollars,
     * and the budget carries it at what it cost in local money on the day -
     * paid by converting cash, or out of the vault with no cash moving at all
     * (the land office's toggle). The vault's part is the one the bridge's
     * journal carries back as "Bought land with US$... of reserves". The rest
     * of the line is plots bought back from businesses, in local money.
     */
    VBox landSpendDetail(double total, String colour) {
        ForeignAccounts fx = ui.game.getForeignAccounts();
        double abroad = fx.getLandLocalThisMonth();
        double fromVault = fx.getLandLocalFromVaultThisMonth();
        return payers("three ways", String.format(
                "The land office is paid in US dollars: %s this month, at the rate of the day "
                + "each plot was bought. Out of the vault, no cash moved - the vault's dollars "
                + "went instead, and the bridge on Overview carries that part back. Plots "
                + "bought back from businesses that scrapped what stood on them are paid in "
                + "local money, as they always were.", usdFull(fx.getLandUsdThisMonth())),
                List.of(new Payer("Abroad, cash converted", abroad - fromVault,
                                usd(fx.getLandUsdThisMonth() - fx.getLandUsdFromVaultThisMonth())),
                        new Payer("Abroad, out of the vault", fromVault, usd(fx.getLandUsdFromVaultThisMonth())),
                        new Payer("Bought back from businesses", Math.max(0, total - abroad), null)),
                total, colour);
    }

    /* =====================================================================
       REVENUE AND SPENDING (0.7.31)
       ===================================================================== */

    void revenuePage(VBox page, EconomyManager em, NationalAccounts na) {
        List<String> names = revenueNames();
        List<Double> amounts = revenueAmounts(na);
        double total = na.getTotalRevenue();
        listPage(page, "Revenue", "taken in", names, amounts, total, na,
                name -> {
                    double amount = amounts.get(names.indexOf(name));
                    String c = colourOf(name, names, amounts);
                    return switch (name) {
                        case "Business tax"          -> () -> businessTaxDetail(amount, c);
                        case "Sales tax"             -> () -> salesTaxDetail(amount, c);
                        case "Wage tax"              -> () -> wageTaxDetail(amount, c);
                        case "Property tax"          -> () -> propertyTaxDetail(amount, c);
                        case "Pension contributions" -> () -> contributionsDetail(amount, c);
                        case "Healthcare fees"       -> () -> healthFeeDetail(amount, c);
                        case "School fees"           -> () -> schoolFeeDetail(amount, c);
                        case "Mortgage insurance premiums" -> this::mortgageInsuranceDetail;
                        case "Transfer from the fund" -> this::fundTransferDetail;
                        default                      -> null;
                    };
                },
                name -> "who", "Taken in altogether", null);
    }

    void spendingPage(VBox page, EconomyManager em, NationalAccounts na, List<CityNeeds.Need> all) {
        List<String> names = spendingNames();
        List<Double> amounts = spendingAmounts(na);
        double total = na.getTotalExpenses();
        double repairs = ui.game.getCityMaintenancePaid();
        Node outside = Math.abs(repairs) < .5 ? null
                : door("Outside the budget's total: repairs " + s(-repairs) + ", paid from the cash",
                        Palette.TEXT_MUTED, () -> showOnOverview(BRIDGE));
        listPage(page, "Spending", "paid out", names, amounts, total, na,
                name -> {
                    double amount = amounts.get(names.indexOf(name));
                    String c = colourOf(name, names, amounts);
                    return switch (name) {
                        case "Healthcare"    -> () -> healthSpendDetail(amount, c);
                        case "Education"     -> () -> educationSpendDetail(amount, c);
                        case "Pensions"      -> () -> pensionDetail(amount, c);
                        case "Debt interest" -> () -> debtServiceDetail(amount, c);
                        case "Land bought"   -> amount > 0 ? () -> landSpendDetail(amount, c) : null;
                        case "Mortgage insurance claims" -> this::mortgageInsuranceDetail;
                        case "Food assistance" -> amount > 0 ? () -> foodAssistanceDetail(amount, c) : null;
                        default              -> null;
                    };
                },
                name -> "Debt interest".equals(name) ? "on what" : "who", "Paid out altogether", outside);

        javafx.scene.layout.GridPane three = equalColumns(3, TILE_GAP);
        VBox debt = debtCard(na, total), services = servicesCard(em), pensions = pensionsCard(em, all);
        for (VBox c : new VBox[] {debt, services, pensions}) javafx.scene.layout.GridPane.setFillHeight(c, true);
        three.add(debt, 0, 0);
        three.add(services, 1, 0);
        three.add(pensions, 2, 0);
        page.getChildren().add(three);
    }

    /* ------------------------------ the three cards ------------------------------ */

    /** P27: principal is not spending. */
    static final String PRINCIPAL_INFO = "Not on the list above, and deliberately: repaying principal is not "
            + "spending. The money leaves and the debt falls by the same amount, so nothing was consumed - but the "
            + "treasury is lighter by it either way. The bridge on Overview reconciles the two.";

    /** P28: term loans. */
    static final String TERM_INFO = "A term loan pays its coupon every month and the whole face at the end, so the "
            + "monthly cost is small, does not fall, and takes nothing off the principal. The Finances tab's ladder "
            + "says when the face is due.";

    /** P29: serial bonds. */
    static final String SERIAL_INFO = "Serial bonds amortise - a slice of principal falls due every anniversary, so "
            + "both the debt and this interest line shrink on their own as long as nothing new is issued.";

    /** P31: notes. */
    static final String NOTES_INFO = "Notes carry no interest at all - the lender's return was the discount, taken "
            + "out of the proceeds when the note was issued. So the interest line understates what the borrowing "
            + "costs, and the whole face falls due as a lump rather than in slices.";

    /** P30: the coupon and the budget disagreeing. */
    static final String COUPON_INFO = "The interest on the budget is struck from what the city actually paid over a "
            + "completed month, so the paper's coupons and the budget's line part company on the first month after "
            + "loading a save, and again in any month a bond was issued or retired part-way through. Advance a month "
            + "and they meet.";

    /**
     * THE DEBT: the principal by paper as one bar in the maturity ladder's
     * three steps, what is owed and what it costs a month, a line a kind -
     * the notes' "no coupon: the discount was the price" - the principal
     * repaid ("not spending"), and the coupon chip on a month the budget's
     * interest and the paper's disagree. The old grid in its fold, its
     * interest column plain (B4).
     */
    VBox debtCard(NationalAccounts na, double spending) {
        VBox card = card(cardHead(Icons.FINANCES, Palette.MONEY, "THE DEBT", null,
                door("Finances", Palette.ACCENT, () -> openFinances("The book", "Every piece"))));
        PaperKind notes = paperKind("Notes"), serial = paperKind("Serial bonds"), term = paperKind("Term loans");
        if (notes.count() + serial.count() + term.count() == 0) {
            card.getChildren().add(words("The city owes nothing.", Palette.SIZE_BODY + 1, Palette.TEXT_LABEL));
            return card;
        }
        List<Slice> parts = new ArrayList<>();
        PaperKind[] kinds = {notes, serial, term};
        for (int i = 0; i < kinds.length; i++) {
            if (kinds[i].principal() > 0) parts.add(new Slice(kinds[i].name(), kinds[i].principal(), Palette.LADDER[i]));
        }
        if (!parts.isEmpty()) card.getChildren().add(keyedBar(parts, 360, v -> m(v)));
        double booked = na.getInterestExpense();
        List<String[]> lines = debtLines(na);
        card.getChildren().add(words(lines.get(0)[0], Palette.SIZE_BODY + 1, Palette.TEXT_HEAD));
        for (String[] line : lines.subList(1, lines.size())) card.getChildren().add(noteLine(line[0], line[1], 360));
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
        if (coupons > 0 && Math.abs(coupons - booked) > coupons * .02) {
            HBox c = new HBox(4, chip("coupon " + m(coupons) + " a month, booked " + m(booked)
                    + ": not struck yet", Palette.TEXT_LABEL), infoButton(COUPON_INFO, true));
            c.setAlignment(Pos.CENTER_LEFT);
            card.getChildren().add(c);
        }
        card.getChildren().add(details("debt", "the paper, line by line", open, ui::redraw, () -> debtGrid(spending)));
        return card;
    }

    /** THE DEBT card's lines, {words, (i)} each, the first the total with no (i) (pure: the probe reads them). */
    List<String[]> debtLines(NationalAccounts na) {
        PaperKind notes = paperKind("Notes"), serial = paperKind("Serial bonds"), term = paperKind("Term loans");
        List<String[]> out = new ArrayList<>();
        out.add(new String[] {"owed " + m(notes.principal() + serial.principal() + term.principal())
                + " · interest " + m(na.getInterestExpense()) + " a month", null});
        if (notes.count() > 0) {
            out.add(new String[] {"notes " + m(notes.principal()) + " · no coupon: the discount ("
                    + m(notes.discount()) + ") was the price · " + (notes.count() == 1 ? "falls due whole in "
                    : "the first falls due whole in ") + notes.soonest() + " mo", NOTES_INFO});
        }
        if (serial.count() > 0) {
            out.add(new String[] {"serial bonds " + m(serial.principal()) + " · " + m(serial.coupon())
                    + " a month · a slice every anniversary", SERIAL_INFO});
        }
        if (term.count() > 0) {
            out.add(new String[] {"term loans " + m(term.principal()) + " · " + m(term.coupon())
                    + " a month · the whole face at the end", TERM_INFO});
        }
        double repaid = ui.game.getCityPrincipalRepaidThisMonth() + ui.game.getForeignPrincipalRepaidThisMonth();
        if (repaid > 0) out.add(new String[] {"principal repaid this month " + m(repaid) + ": not spending", PRINCIPAL_INFO});
        return out;
    }

    /** S6's grid: pieces, owed, interest a month, of spending - plain figures. */
    Node debtGrid(double spending) {
        javafx.scene.layout.GridPane table = grid(new double[] {120, 50, 90, 90, 80}, rightAfterFirst(5));
        gridHead(table, "", "pieces", "owed", "interest /mo", "of spending");
        int line = 1;
        for (PaperKind k : paperKinds()) {
            if (k.count() == 0) continue;
            table.add(gridCell(k.name(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            table.add(gridCell(String.valueOf(k.count()), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 1, line);
            table.add(gridCell(m(k.principal()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 2, line);
            table.add(gridCell(m(k.coupon()), k.coupon() > 0 ? Palette.TEXT_BODY : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 3, line);
            table.add(gridCell(spending > 0 ? String.format("%.2f%%", k.coupon() / spending * 100) : "—",
                    Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        return table;
    }

    /** P32, as it is true: both cost the city more than they charge. */
    static final String BOTH_DEFICIT_INFO = "Both run at a deficit on purpose. What the deficit buys is on the "
            + "Services tab, in deaths avoided and diplomas issued rather than in this column.";

    /** ...and when one does not (B16: fees with no bill). */
    static final String NOT_BOTH_INFO = "Care and the schools are meant to run at a deficit: what it buys is on the "
            + "Services tab, in deaths avoided and diplomas issued. A service whose fees ran over its bill this month "
            + "made the treasury money. Care can charge with no bill at all: the doctor, the nursery, the almshouse "
            + "and the churchyard the city was founded with treat and bury at no cost to it, and charge their fees "
            + "like any clinic - so a city with no care buildings of its own takes care fees and pays no care bill.";

    /**
     * THE SERVICES THAT CHARGE: care and the schools, each a bar of its fees
     * against its bill and one line - what the fees cover and the net cost,
     * or what they ran over by, or that there was no bill. The net's sign is
     * right now (B3: a cost was printed negative, a profit as a cost), and it
     * is a reading, not a verdict (B4).
     */
    VBox servicesCard(EconomyManager em) {
        boolean both = em.getHealthcareNet() > 0 && em.getEducationNet() > 0;
        VBox card = card(cardHead(Icons.HEALTH, Palette.PEOPLE, "THE SERVICES THAT CHARGE",
                both ? BOTH_DEFICIT_INFO : NOT_BOTH_INFO,
                door("Services", Palette.ACCENT, () -> ui.servicesScreen.open("Health", ServicesScreen.OVERVIEW))));
        card.getChildren().add(serviceLine("care", em.getHealthcareBill(), em.getHealthcareFees(), em.getHealthcareNet()));
        card.getChildren().add(serviceLine("schools", em.getEducationBill(), em.getEducationFees(), em.getEducationNet()));
        return card;
    }

    /** One service's words: what its fees cover and its net cost, what they ran over by, or that there was no bill (B3, B16). */
    static String serviceWords(String name, double bill, double fees, double net) {
        if (bill < .5 && fees < .5) return name + ": no bill this month";
        if (bill < .5) return name + ": fees " + m(fees) + " with no bill this month";
        if (net > 0) return String.format("%s: fees cover %.0f%% of %s · net cost %s", name, fees / bill * 100, m(bill), m(net));
        return String.format("%s: fees ran %s over its %s bill", name, m(-net), m(bill));
    }

    /** One service: its fees against its bill as a bar, and its words. */
    static VBox serviceLine(String name, double bill, double fees, double net) {
        VBox box = new VBox(3, words(serviceWords(name, bill, fees, net), Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        if (bill >= .5 || fees >= .5) box.getChildren().add(bar(bill > 0 ? fees / bill : 1, Palette.MONEY, 300, 8));
        return box;
    }

    /** P33: pay-as-you-go. */
    static final String PENSIONS_INFO = "It is pay-as-you-go: this month's workers pay this month's pensioners, so "
            + "the gap widens with the dependency ratio rather than with anything you set. NEEDS YOU lists it only "
            + "when the budget is in deficit too - an unfunded promise the city is paying is not news; one it cannot "
            + "pay is.";

    /**
     * PENSIONS: contributions against what the pensions cost as a bar, the
     * seniors and what each is paid, and what came out of general revenue -
     * with a chip in NEEDS YOU's PENSIONS colour only while NEEDS YOU lists
     * it (B11: the old alert fired every month the scheme fell short, which
     * is every month in both cities measured).
     */
    VBox pensionsCard(EconomyManager em, List<CityNeeds.Need> all) {
        VBox card = card(cardHead(Icons.POPULATION, Palette.PEOPLE, "PENSIONS", PENSIONS_INFO,
                door("set it", Palette.ACCENT, () -> openPolicy("Promises", "Pensions"))));
        double paid = em.getPensionsPaid(), in = em.getContributions(), gap = em.getPensionShortfall();
        card.getChildren().add(words(paid > 0
                        ? String.format("contributions cover %.0f%% of %s", em.getPensionCoverage() * 100, m(paid))
                        : "no pensions paid this month",
                Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        if (paid > 0) card.getChildren().add(bar(in / paid, Palette.PEOPLE, 300, 8));
        card.getChildren().add(words(people(em.getSeniors()) + " seniors at " + cash(em.getTaxPolicy().pensionPerSenior()),
                Palette.SIZE_LABEL + 1, Palette.TEXT_MUTED));
        card.getChildren().add(words(gap > .5 ? m(gap) + " from general revenue" : "contributions pay for all of it",
                Palette.SIZE_LABEL + 1, Palette.TEXT_MUTED));
        CityNeeds.Need row = need(all, CityNeeds.Kind.PENSIONS);
        if (row != null && row.level() >= 1) card.getChildren().add(chip("NEEDS YOU: " + row.reading(),
                BuildScreen.verdict(row.level())));
        return card;
    }

    /* =====================================================================
       THE OUTPUT (0.7.31)

       The question: how big is the economy, what is it made of, and is it
       growing? History's GDP in layers leads - a rolling year in founding
       money, the header's real growth beside it - then the four parts as
       cards with this month's nominal figures, this month's bar, and the
       growth as five readings with no verdict tone: the header's GDP tile
       owns the verdict (D12).
       ===================================================================== */

    void outputPage(VBox page, NationalAccounts na) {
        page.getChildren().add(outputHero(na));

        javafx.scene.layout.GridPane four = equalColumns(4, TILE_GAP);
        VBox[] cards = {consumptionCard(na), investmentCard(na), governmentCard(na), tradeCard(na)};
        for (int i = 0; i < cards.length; i++) {
            javafx.scene.layout.GridPane.setFillHeight(cards[i], true);
            four.add(cards[i], i, 0);
        }
        page.getChildren().add(four);

        /*
         * A STACKED BAR, NOT A RING, and the reason is arithmetic rather than
         * taste: net exports can be negative, and a ring cannot draw a wedge
         * that subtracts. A bar draws each part at its size - a negative one
         * too, its sign in the key and the tooltip - and the parts still read
         * as one. Net exports in the muted ink, not green or red (B4).
         */
        String[] names = {"Consumption", "Investment", "Government", "Net exports"};
        double[] values = {na.getConsumption(), na.getInvestment(), na.getGovernment(), na.getNetExports()};
        String[] colours = {Palette.GDP_LAYERS[0], Palette.GDP_LAYERS[1], Palette.GDP_LAYERS[2], Palette.TEXT_MUTED};
        List<Segment> parts = new ArrayList<>();
        javafx.scene.layout.FlowPane key = new javafx.scene.layout.FlowPane(14, 2);
        for (int i = 0; i < names.length; i++) {
            parts.add(new Segment(Math.abs(values[i]), colours[i], false, null, null,
                    names[i] + "\n" + s(values[i]), null));
            key.getChildren().add(keySwatch(colours[i], names[i] + " " + s(values[i])));
        }
        page.getChildren().add(new VBox(4, caption("THIS MONTH, AS ONE BAR", null),
                segmentBar(parts, 0, List.of(), 0, 14), key));

        page.getChildren().add(growthStrip(na));
        page.getChildren().add(details("output", "how many months are recorded", open, ui::redraw,
                () -> column(statementLine("Months recorded", people(na.getMonthsRecorded())),
                        statementNote(na.getMonthsRecorded() >= 12
                                ? "A year or more, so the annual figures are measured."
                                : String.format("Fewer than twelve, so the annual figure is %d months scaled up "
                                        + "rather than a year measured.", na.getMonthsRecorded())))));
    }

    /** The layers' caption's (i). */
    static final String LAYERS_INFO = "What GDP is made of over the years, as City History draws it in layers: "
            + "consumption, investment and government stacked, each a rolling year in founding money, and the line "
            + "real GDP - the gap between the stack and the line is net exports, negative below it. The cards "
            + "under it are this month's, in today's money.";

    /** WHAT THE CITY MAKES: History's layers chart at the left, this month's GDP, the year, a head and the real growth at the right. */
    HBox outputHero(NationalAccounts na) {
        HistorySave h = ui.game.getHistorySave();
        HistoryScreen.Trace trace = ui.historyScreen.traceFor(HistoryScreen.LAYERED);
        String unit = trace.unit();
        TimeChart.Stack stack = ui.historyScreen.gdpStack(h, unit);
        TimeChart chart = new TimeChart(new ChartModel(), java.util.Set.of(), false);
        chart.setData(h.getMonth(),
                List.of(new TimeChart.Line(HistoryScreen.LAYERED, trace.label(), HistoryScreen.LAYERED_LINE, 0,
                        ui.historyScreen.historyValues(h, HistoryScreen.LAYERED),
                        v -> HistoryScreen.plotScale(unit, v), v -> ui.historyScreen.fmtUnit(unit, v), "")),
                HistoryScreen.axisFor(unit), null, false, false, stack, List.of(), List.of(), List.of(),
                "from the end of the first year");
        chart.setSize(880, 220);
        VBox left = new VBox(4, caption("WHAT THE CITY MAKES · a rolling year, in founding money", LAYERS_INFO), chart,
                ui.historyScreen.layersKey(stack.layers(), 880));
        left.setMinWidth(0);
        HBox.setHgrow(left, Priority.ALWAYS);

        double annual = annualGdp(na);
        long heads = ui.game.getPopulationManager().getPopulation();
        double[] growth = YearBook.realGrowth(h);
        double grew = growth.length == 0 ? Double.NaN : growth[growth.length - 1];
        VBox right = new VBox(6,
                words("GDP this month", Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL),
                figure(m(na.getGdp()), 28, Palette.TEXT_HEAD),
                words((gdpEstimated(na) ? "a year, annualised " : "a year ") + m(annual)
                        + (heads > 0 && annual > 0 ? " · " + moneyFull(annual / heads) + " a head" : ""),
                        Palette.SIZE_BODY + 1, Palette.TEXT_LABEL),
                words(realGrowthWords(na.getMonthsRecorded(), grew), Palette.SIZE_BODY + 1, Palette.TEXT_LABEL),
                door("Over the years", Palette.ACCENT, () -> ui.historyScreen.openOn(HistoryScreen.LAYERED)));
        right.setMinWidth(240);
        right.setPrefWidth(280);
        right.setMaxWidth(280);

        HBox card = new HBox(24, left, right);
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-padding: 14 16 14 16; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return card;
    }

    /** The header GDP tile's real growth, in its words (UserInterface's tile), without its verdict colour - that is the tile's. */
    static String realGrowthWords(int recorded, double grew) {
        if (recorded < 12) return "real growth from the second year";
        if (Double.isNaN(grew)) return "real growth from month 24";
        if (Math.abs(grew) < .0005) return "flat, real, 12 mo";
        return String.format("%s %.1f%% real, 12 mo", grew > 0 ? "▲" : "▼", Math.abs(grew) * 100);
    }

    /** One of the four parts' cards: its name, its figure, and its lines. */
    static VBox partCard(String name, String colour, String amount, String info, Node... lines) {
        Region swatch = new Region();
        swatch.setMinSize(10, 10);
        swatch.setPrefSize(10, 10);
        swatch.setMaxSize(10, 10);
        swatch.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 3;");
        HBox head = new HBox(6, swatch, caption(name, info));
        head.setAlignment(Pos.CENTER_LEFT);
        VBox card = card(head, figure(amount, 22, Palette.TEXT_HEAD));
        for (Node n : lines) if (n != null) card.getChildren().add(n);
        return card;
    }

    VBox consumptionCard(NationalAccounts na) {
        return partCard("CONSUMPTION", Palette.GDP_LAYERS[0], m(na.getConsumption()), null,
                cardLine("goods bought in shops", m(na.getConsumptionGoods()), null),
                cardLine("rent paid", m(na.getConsumptionHousing()), null));
    }

    /** P34: stock built up. */
    static final String STOCK_INFO = "Stock built up is output that has been made and not yet sold, so it counts "
            + "the month it was produced rather than the month it clears.";

    VBox investmentCard(NationalAccounts na) {
        return partCard("INVESTMENT", Palette.GDP_LAYERS[1], m(na.getInvestment()), STOCK_INFO,
                cardLine("construction put up", m(na.getInvestmentConstruction()), null),
                cardLine("change in stock", s(na.getInvestmentInventories()), null));
    }

    /** P35, rewritten (B5): what G is, as the model counts it. */
    static final String GOVERNMENT_INFO = "Services with no market price, valued at cost: the utilities' staff, "
            + "care, the schools, police and prisons, and the buses' and trains' staff and upkeep. The fees and fares "
            + "are transfers, not a second lot of output. Land trading is not output.";

    VBox governmentCard(NationalAccounts na) {
        return partCard("GOVERNMENT", Palette.GDP_LAYERS[2], m(na.getGovernment()), GOVERNMENT_INFO,
                words("the city's own services, at cost", Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
    }

    /**
     * NET EXPORTS: what was sold abroad against what was bought, as two bars
     * on one scale - exports in the business violet, imports in its darker
     * step in three parts - with the parts named for what they are (B5:
     * "Steel exported" was every sector's exports, "Scrap imported" every
     * sector's imports but the shops' food and the builders' materials).
     */
    VBox tradeCard(NationalAccounts na) {
        double exports = na.getExports();
        double food = na.getImportsFood(), materials = na.getImportsMaterials(), rest = na.getImportsRawMaterial();
        double imports = food + materials + rest;
        double scale = Math.max(Math.max(exports, imports), 1e-9);
        List<ScaleRow> rows = List.of(
                ScaleRow.of("exports", m(exports), List.of(Run.of(0, exports, Palette.BUSINESS)
                        .tip("Exports (every sector)\n" + m(exports)))),
                ScaleRow.of("imports", m(imports), List.of(
                        Run.of(0, food, Palette.BUSINESS_DARK).tip("Imports: food (the shops)\n" + m(food)),
                        Run.of(food, materials, tint(Palette.BUSINESS_DARK, .7))
                                .tip("Imports: building materials (the builders)\n" + m(materials)),
                        Run.of(food + materials, rest, tint(Palette.BUSINESS_DARK, .45))
                                .tip("Imports: everyone else's\n" + m(rest)))));
        return partCard("NET EXPORTS", Palette.TEXT_MUTED, s(na.getNetExports()), null,
                scaleRows(rows, scale, List.of(), 56, 64, 8),
                words("exports: every sector's · imports: food " + m(food) + " (the shops) · building materials "
                        + m(materials) + " (the builders) · everyone else's " + m(rest),
                        Palette.SIZE_LABEL, Palette.TEXT_MUTED));
    }

    /** P37, on the strip's month on month. */
    static final String MOM_INFO = "Month-on-month annualised is one month multiplied up - compounded, so a city "
            + "growing 2% a month is growing 27% a year - and it swings hard on a city this size. The year-on-year "
            + "figure is the one to steer by.";

    /** P36's point, on the strip's year on year: nominal, where the header's tile is real. */
    static final String YOY_INFO = "This month's output against the same month a year ago, in today's money - "
            + "nominal, so a rise in prices reads as growth. The header's GDP tile reads the real figure, prices "
            + "taken out, over a rolling year.";

    /** The growth strip: five readings, no verdict tone (B4: MoM and YoY were coloured on thresholds of the screen's own). */
    HBox growthStrip(NationalAccounts na) {
        double annual = annualGdp(na);
        long heads = ui.game.getPopulationManager().getPopulation();
        int recorded = na.getMonthsRecorded();
        VBox year = limitCell(gdpEstimated(na) ? "GDP, ANNUALISED" : "ANNUAL GDP", m(annual),
                gdpEstimated(na) ? recorded + " months scaled up" : "the last twelve months", Palette.TEXT_HEAD);
        VBox trend = limitCell("TREND", m(na.getTrendGdp()), "a month, twelve-month average", Palette.TEXT_HEAD);
        VBox head = limitCell("PER HEAD", heads > 0 ? moneyFull(annual / heads) : "—", "a year", Palette.TEXT_HEAD);
        VBox yoy = limitCell("YEAR ON YEAR", recorded >= 13 ? minus(String.format("%+.1f%%", na.getYearOnYearGrowth() * 100))
                : "—", recorded >= 13 ? "nominal, this month on a year ago" : "needs " + (13 - recorded) + " more months",
                Palette.TEXT_HEAD);
        withInfo(yoy, YOY_INFO);
        VBox mom = limitCell("MONTH ON MONTH", minus(String.format("%+.0f%%", na.getMonthlyGrowthAnnualised() * 100)),
                "annualised", Palette.TEXT_HEAD);
        withInfo(mom, MOM_INFO);
        HBox strip = vitalsBar(year, trend, head, yoy, mom);
        strip.setMaxWidth(Double.MAX_VALUE);
        return strip;
    }

    /** A limit cell's note with an (i) after it. */
    static void withInfo(VBox cell, String info) {
        Node note = cell.getChildren().get(cell.getChildren().size() - 1);
        cell.getChildren().remove(note);
        HBox row = new HBox(4, note, infoButton(info, true));
        row.setAlignment(Pos.CENTER_LEFT);
        cell.getChildren().add(row);
    }
}
