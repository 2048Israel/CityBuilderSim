package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Pieces.*;

/**
 * The construction page (0.7.22): the builders' gauge, every site with its
 * order, its crews, its time and its money and the player's hand on it -
 * priority, rush, cancel, restart - a timeline of when each finishes, and
 * the Demolish tab, where the city's buildings come down and a business's
 * or a landlord's are bought out first.
 *
 * WHY. Jerus, on the play-through of 0.7.19: "whats getting built and all,
 * and what you already are building, and like not just a blue loading
 * screen" - the right panel's list of progress bars was all a player had -
 * and then "not only repirotize and cancel but also destroy buildings, like
 * you yourself destroy buildings". Built to the mockups he saw
 * (Construction.dc.html, Demolish.dc.html); every figure the mockups left
 * in brackets is the model's, read through its public getters: the plan
 * the month will apply (BuildingManager.plan()), each site's time at
 * today's queue (siteMonths()), and the quotes (Game.quoteDemolition(),
 * quoteBuyOut(), quoteRestart()). The rules and their sources are
 * ConstructionControl's; the page says them in a line and an (i).
 *
 * Reached from the right panel's "Open" and from Build's head; a build
 * card's "N on site", or a site's name on the right panel, opens it at that
 * site. The cancel, the demolition and the buy-out ask first, with the
 * money in the dialog (UserInterface.confirm()).
 */
final class ConstructionScreen {

    /** The screen's name, for clearMenu() and the rail (the Build tab owns it). */
    static final String SCREEN = "showConstruction";

    private final UserInterface ui;

    ConstructionScreen(UserInterface ui) { this.ui = ui; }

    /** The open tab - Sites, Timeline or Demolish - kept for the session. */
    private String tab = "Sites";

    /** The site the page was opened at from a build card or the right panel's site name, drawn raised until another tab is opened. */
    private String focus;

    /* The Demolish tab's staging: what is picked - a template's name, or a shell's - and how many. */
    private String pick;
    private boolean pickShell;
    private int count = 1;
    private String filter = "";

    /** The page, on the tab it was left on. */
    void show() { draw(); }

    /** ...at one site, from a build card's "N on site" or a site's name on the right panel. */
    void showSite(String key) {
        focus = key;
        tab = "Sites";
        draw();
    }

    void showTab(String which) {
        tab = which;
        focus = null;
        draw();
    }

    private void draw() {
        ui.clearMenu(SCREEN, this::draw);
        VBox page = new VBox(Palette.GAP_LOOSE);
        page.setAlignment(Pos.TOP_LEFT);
        page.setPadding(new Insets(0, 18, UserInterface.PAGE_FOOT, 18));
        page.maxWidthProperty().bind(ui.menuScroller.widthProperty().subtract(24));
        page.prefWidthProperty().bind(ui.menuScroller.widthProperty().subtract(24));
        page.getChildren().add(head());
        switch (tab) {
            case "Timeline":
                page.getChildren().addAll(gauge(), timeline());
                break;
            case "Demolish":
                page.getChildren().add(demolish());
                break;
            default:
                page.getChildren().addAll(gauge(), sites());
        }
        ui.rootMenu.getChildren().add(page);
    }

    /* =====================================================================
       THE HEAD: the title with the building area's swatch, the three tabs,
       and the way back to Build.
       ===================================================================== */

    private HBox head() {
        Label title = ui.pageTitle("CONSTRUCTION");
        HBox tabs = new HBox(4);
        tabs.setAlignment(Pos.CENTER_LEFT);
        for (String name : new String[] { "Sites", "Timeline", "Demolish" }) {
            boolean on = name.equals(tab);
            Button b = new Button(name);
            b.setStyle(Palette.words(Palette.SIZE_BODY, on ? Palette.TEXT_HEAD : Palette.TEXT_LABEL)
                    + " -fx-background-color: " + (on ? Palette.PINNED : "transparent") + ";"
                    + " -fx-background-radius: " + Palette.RADIUS + ";"
                    + " -fx-border-color: " + (on ? Palette.BUILDING : "transparent") + ";"
                    + " -fx-border-width: 0 0 2 0; -fx-padding: 6 12 6 12; -fx-cursor: hand;");
            b.setOnAction(e -> showTab(name));
            tabs.getChildren().add(b);
        }
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label back = stepChip("‹ Build", () -> ui.buildScreen.showBuildMenu(), true);
        HBox head = new HBox(18, title, tabs, gap, back);
        head.setAlignment(Pos.CENTER_LEFT);
        head.setMaxWidth(Double.MAX_VALUE);
        return head;
    }

    /* =====================================================================
       THE BUILDERS' GAUGE: what they do a month, everything owed on site,
       the queue that makes in months against the landlords' twelve, and the
       material - and the bar that draws the queue.
       ===================================================================== */

    private VBox gauge() {
        Game g = ui.game;
        BuildingManager bm = g.getBuildingManager();
        int output = g.getBuildingOutput();
        double atEveryPost = g.getBuildingOutputAtEveryPost();
        double owed = bm.pointsOwedOnSite();
        int sites = 0, buildings = 0;
        for (BuildingsStacks s : bm.getStacksUnderConstruction()) { sites++; buildings += s.getUnderConstruction(); }
        for (ConstructionControl.Demolition d : bm.getControl().demolitions()) { sites++; buildings += d.buildings; }
        double queue = atEveryPost > 0 ? owed / atEveryPost : Double.NaN;
        double limit = BusinessInvestment.MAX_ORDER_MONTHS;
        double theirs = g.getBusinessInvestment().monthsOfWorkOnSite(Sectors.REAL_ESTATE, atEveryPost);

        VBox builders = limitCell("BUILDERS", String.format("%,d pts", output), "a month, after the repairs",
                Palette.TEXT_HEAD);
        VBox onSite = limitCell("OWED ON SITE", String.format("%,.0f pts", owed),
                String.format("across %d site%s, %,d building%s", sites, sites == 1 ? "" : "s",
                        buildings, buildings == 1 ? "" : "s"), Palette.TEXT_HEAD);
        VBox months = limitCell("QUEUE", Double.isNaN(queue) ? "stalled" : String.format("≈ %.1f months", queue),
                "at today's crews",
                Double.isNaN(queue) ? Palette.BAD : queue > limit ? Palette.WARN : Palette.TEXT_HEAD);
        VBox material = limitCell("MATERIAL", String.format("%,d in the yard", bm.getConstructionMaterials()),
                "the rest bought as they build; the world's at " + money(bm.getConstructionMaterialPrice())
                        + " a unit", Palette.TEXT_HEAD);
        HBox cells = vitalsBar(builders, onSite, months, material);

        // The bar: the queue over two years, a mark at the landlords' twelve.
        double span = limit * 2;
        SegmentBar bar = segmentBar(List.of(Segment.of(Double.isNaN(queue) ? 1 : Math.min(1, queue / span),
                        Double.isNaN(queue) || queue > limit ? Palette.WARN : Palette.BUILDING)), 1,
                List.of(new Tick(limit / span, Palette.TEXT_LABEL, 2, null, null)), 0, BAR_BAND);
        Label zero = caption("0");
        Label mark = caption(String.format("%.0f months: landlords stop ordering past their own%s", limit,
                Double.isNaN(theirs) ? "" : String.format(" (theirs ≈ %.1f)", theirs)));
        Label end = caption(String.format("%.0f months", span));
        Region a = new Region(), b = new Region();
        HBox.setHgrow(a, Priority.ALWAYS);
        HBox.setHgrow(b, Priority.ALWAYS);
        HBox scale = new HBox(0, zero, a, mark, b, end);
        HBox whole = new HBox(Palette.GAP, infoButton(
                "The queue is everything owed on site over what the builders' crews would do a month with every "
                + "post filled, after the repairs - the reading every order's wait is quoted at. The landlords "
                + "keep ordering homes while what their own sites owe is under " + (int) limit + " months of it; "
                + "every other sector waits for its one order to finish. "
                + "The yard's material is free to the city's orders; the crews buy the rest as they build, from "
                + "what they salvaged, then the plant, then the world - so no site in this model is ever short "
                + "of it.", false));
        VBox box = new VBox(6, cells, bar, scale);
        box.setStyle("-fx-padding: 10 12 10 12;" + Palette.block(Palette.PANEL, Palette.EDGE));
        box.setMaxWidth(Double.MAX_VALUE);
        HBox headLine = new HBox(Palette.GAP, sectionCaption("THE BUILDERS"), whole);
        headLine.setAlignment(Pos.CENTER_LEFT);
        return new VBox(4, headLine, box);
    }

    /**
     * How tall the page's bars are: the gauge's queue and each site's and
     * stopped shell's progress. They are Pieces.segmentBar() since 0.7.26 -
     * this page's own bar (a track, a fill and one mark, filling its width),
     * generalised when the land office needed it with ghosts and several
     * ticks - at this band, its track and its 2 px mark as they were.
     */
    private static final double BAR_BAND = 10;

    /* =====================================================================
       THE SITES

       One row a site: the city's own first, in the order its crews serve
       them, with the arrows that reorder them; then every other owner's,
       shared by the crews' rule; then the stopped shells. Each row: what it
       is and who ordered it, how far along, this month's crews, the time
       left at today's queue, what was paid and what the builders still owe
       on it, its status, and what the player can do to it.
       ===================================================================== */

    /** The columns, by width: the order, the building, its progress, crews and time, money, status and the hand. */
    private static final double COL_RANK = 34, COL_PROGRESS = 140, COL_CREWS = 100, COL_MONEY = 104,
            COL_STATUS = 168;

    private VBox sites() {
        Game g = ui.game;
        BuildingManager bm = g.getBuildingManager();
        ConstructionControl control = bm.getControl();
        double output = g.getBuildingOutput();
        BuildingManager.Plan plan = bm.plan(output);
        double atEveryPost = g.getBuildingOutputAtEveryPost();

        VBox table = new VBox(0);
        table.setStyle(Palette.block(Palette.PANEL, Palette.EDGE));
        table.setMaxWidth(Double.MAX_VALUE);

        Label caption = sectionCaption("SITES");
        Label says = new Label(control.isPrioritySet()
                ? "The city's sites take its share of the crews in your order."
                : "Crews are shared by what each building can use. Order the city's own to serve them first.");
        says.setWrapText(true);
        says.setMinWidth(0);
        says.setMaxWidth(420);
        says.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        HBox top = new HBox(Palette.GAP, caption, says, infoButton(
                "THE CREWS. Every site's share of the builders' month is the crew each of its buildings can use - "
                + "a bigger building a bigger crew, less than in proportion (Bromilow's law) - and no site takes "
                + "more than it still owes. That stays the rule for everyone.\n\n"
                + "PRIORITY. Once you set an order, the share the rule gives the city's own sites, together, is "
                + "handed out top down: the first takes what it still owes, then the next. Nobody else's site "
                + "gains or loses a point by it, and it costs nothing. A city site you order while it is set "
                + "joins it at the bottom; once none of its sites is left, it clears itself.\n\n"
                + "RUSH. A city's site on a 50-hour week: about 1.14 times a month's work the first month, 1.02 "
                + "the second and 0.94 from the third (The Business Roundtable, Report C-2, 1980), for 1.375 "
                + "times its crews' wages - hours past 40 at time and a half (Canada Labour Code, s. 174). The "
                + "city pays the 0.375, and the crews are paid it.\n\n"
                + "CANCEL. Termination for convenience (FAR 52.249-2): this month's work is done and billed, "
                + "and what the city prepaid that is not earned comes back - the work not done, the material "
                + "not drawn, and the tax on both. The half-built shell keeps its work and its ground until you "
                + "restart it at today's quote or demolish it.", true));
        top.setAlignment(Pos.CENTER_LEFT);
        Region fill = new Region();
        HBox.setHgrow(fill, Priority.ALWAYS);
        top.getChildren().add(fill);
        if (control.isPrioritySet()) {
            top.getChildren().add(stepChip("Back to the crews' rule", () -> { g.resetSiteOrder(); draw(); }, true));
        }
        top.setStyle("-fx-padding: 10 12 6 12;");
        table.getChildren().add(top);
        table.getChildren().add(headerRow());

        List<String> cityOrder = bm.cityOrder();
        int rank = 0;
        for (String key : cityOrder) {
            table.getChildren().add(siteRow(key, ++rank, cityOrder.size(), plan, output, atEveryPost));
        }
        for (String key : bm.siteKeysOnSite()) {
            if (cityOrder.contains(key)) continue;
            table.getChildren().add(siteRow(key, 0, 0, plan, output, atEveryPost));
        }
        for (ConstructionControl.Shell shell : control.shells()) {
            table.getChildren().add(shellRow(shell));
        }
        if (bm.siteKeysOnSite().isEmpty() && control.shells().isEmpty()) {
            Label none = new Label("Nothing on site. Order from Build, or demolish from the Demolish tab.");
            none.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_MUTED) + " -fx-padding: 14 12 14 12;");
            table.getChildren().add(none);
        }
        return table;
    }

    private HBox headerRow() {
        HBox row = new HBox(Palette.GAP,
                cell(head(""), COL_RANK),
                grow(head("BUILDING")),
                cell(head("PROGRESS"), COL_PROGRESS),
                cell(head("CREWS · TIME LEFT"), COL_CREWS),
                cell(head("PAID · OWED"), COL_MONEY),
                cell(head("STATUS"), COL_STATUS));
        row.setStyle("-fx-padding: 2 12 4 12;");
        return row;
    }

    /** One site on site: a stack or a demolition. `rank` is its place in the city's order, 0 for another owner's. */
    private Node siteRow(String key, int rank, int ranks, BuildingManager.Plan plan, double output, double atEveryPost) {
        Game g = ui.game;
        BuildingManager bm = g.getBuildingManager();
        ConstructionControl control = bm.getControl();
        ConstructionControl.Demolition demolition = control.demolitionOf(key);
        BuildingsStacks stack = demolition == null ? bm.stackOfKey(key) : null;
        boolean mine = rank > 0;

        // ---- the order: the arrows on the city's own ----
        VBox order = new VBox(0);
        order.setAlignment(Pos.CENTER);
        if (mine) {
            order.getChildren().addAll(
                    arrow("▲", rank > 1, () -> { g.moveSite(key, -1); draw(); }),
                    figureLabel(String.valueOf(rank), Palette.TEXT_LABEL),
                    arrow("▼", rank < ranks, () -> { g.moveSite(key, 1); draw(); }));
        }

        // ---- what it is, and who ordered it ----
        String name = bm.nameOfSite(key);
        Label title = new Label(name);
        title.setWrapText(true);
        title.setStyle(Palette.strong(Palette.SIZE_BODY, Palette.TEXT_HEAD));
        HBox who = new HBox(6);
        who.setAlignment(Pos.CENTER_LEFT);
        if (demolition != null) {
            who.getChildren().add(chip("City", Palette.MONEY));
            if (!"City".equals(demolition.from) && !"Shell".equals(demolition.from)) {
                who.getChildren().add(caption("bought from " + payerLabel(g, demolition.from)));
            }
        } else if (stack != null) {
            List<String> payers = new ArrayList<>();
            for (BuildingsStacks.Contract c : stack.getContracts()) payers.add(c.payer);
            if (payers.isEmpty()) payers.add("City");
            for (String p : payers) who.getChildren().add(chip(payerLabel(g, p), "City".equals(p) ? Palette.MONEY : Palette.BUSINESS));
        }
        VBox what = new VBox(2, title, who);

        // ---- progress: done of total, and the work ----
        int done, total;
        double fraction;
        String doneWord;
        if (demolition != null) {
            done = 0;
            total = demolition.buildings;
            fraction = demolition.points > 0 ? demolition.progress / demolition.points : 1;
            doneWord = "down";
        } else {
            ConstructionControl.Run run = control.runOf(stack.getBuilding().getId());
            done = run == null ? 0 : run.built;
            total = done + stack.getUnderConstruction();
            double p = stack.getBuilding().getConstructionPoints();
            fraction = total > 0 && p > 0 ? (done * p + stack.getConstructionProgress()) / (total * p) : 0;
            doneWord = "built";
        }
        Label count = new Label(String.format("%,d of %,d %s", done, total, doneWord));
        count.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Label pct = figureLabel(String.format("%.0f%%", fraction * 100), Palette.TEXT_LABEL);
        Region spread = new Region();
        HBox.setHgrow(spread, Priority.ALWAYS);
        HBox countLine = new HBox(4, count, spread, pct);
        SegmentBar progress = segmentBar(List.of(Segment.of(fraction, Palette.BUILDING)), 1, null, 0, BAR_BAND);
        progress.setPrefHeight(12);
        progress.setMinHeight(12);
        VBox progressBox = new VBox(3, countLine, progress);

        // ---- this month's crews, and the time left ----
        double share = plan.shareOf(key), work = plan.workOf(key);
        ConstructionControl.Overtime overtime = plan.overtimeOf(key);
        String crewsText = output > 0 ? String.format("%.1f%% of crews", share / output * 100) : "no crews";
        Label crews = figureLabel(crewsText, share > 0 ? Palette.TEXT_BODY : Palette.WARN);
        // The one wait every screen reads (Game.siteMonths(), after the docs
        // pass): the right panel, a build card and the Needs-you line give this
        // site the same months; a site the order starves says so in its status.
        double months = g.siteMonths(key);
        String left = demolition != null && demolition.closing ? "closes, then " + monthsWait(months)
                : monthsWait(months);
        Label time = figureLabel(left, Palette.TEXT_BODY);
        VBox crewsBox = new VBox(2, crews, time);
        if (overtime != null && share > 0) {
            double more = (work / share - 1) * 100;
            Label extra = caption(more >= 0 ? String.format("+%.0f%% on overtime", more)
                    : String.format("%.0f%% on overtime: tired crews", more));
            if (more < 0) extra.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.WARN));
            crewsBox.getChildren().add(extra);
        }

        // ---- paid and owed ----
        double paid, owedMoney;
        if (demolition != null) {
            paid = demolition.paid;
            owedMoney = demolition.value;
        } else {
            ConstructionControl.Run run = control.runOf(stack.getBuilding().getId());
            paid = run == null ? Double.NaN : run.billed;
            owedMoney = stack.getContractValue();
        }
        Label paidLabel = figureLabel(Double.isNaN(paid) ? "—" : money(paid), Palette.TEXT_LABEL);
        Label owedLabel = figureLabel(money(owedMoney) + " owed", Palette.TEXT_MUTED);
        VBox moneyBox = new VBox(2, paidLabel, owedLabel);

        // ---- the status, and the hand ----
        VBox statusBox = new VBox(4);
        String status;
        String tone;
        boolean rushed = control.isRushed(key);
        if (control.isCancelling(key)) { status = "stops at month's end"; tone = Palette.WARN; }
        else if (demolition != null && demolition.closing) { status = "closes as the month starts"; tone = Palette.WARN; }
        else if (rushed) { status = String.format("rushed (month %d)", control.monthsOnOvertime(key) + 1); tone = Palette.BUILDING; }
        else if (share <= 0 && plan.owedOf(key) > 0) { status = "waiting for crews"; tone = Palette.WARN; }
        else if (demolition != null) { status = "demolishing"; tone = Palette.GOOD; }
        else { status = "building"; tone = Palette.GOOD; }
        statusBox.getChildren().add(chip(status, tone));
        HBox hand = new HBox(4);
        if (mine) {
            if (rushed) {
                double[] next = g.rushPreview(key);
                hand.getChildren().add(action("Stop rush", Palette.TEXT_BODY, () -> { g.rushSite(key, false); draw(); }));
                Label numbers = caption(String.format("next: ×%.2f the work, +%s", next[1], money(next[2])));
                if (next[1] < 1) numbers.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.WARN));
                statusBox.getChildren().add(numbers);
            } else if (plan.owedOf(key) > share) {
                double[] next = g.rushPreview(key);
                Button rush = action("Rush", Palette.TEXT_BODY, () -> { g.rushSite(key, true); draw(); });
                tip(rush, String.format("Overtime, month %.0f: %.2f times a month's work for %.3f times the crews' "
                        + "wages - about %s more this month. From the third month a month on overtime does less "
                        + "than a normal one.", next[0], next[1], ConstructionControl.OVERTIME_WAGE_BILL, money(next[2])));
                hand.getChildren().add(rush);
            }
            if (demolition == null) {
                if (control.isCancelling(key)) {
                    hand.getChildren().add(action("Keep", Palette.TEXT_BODY, () -> { g.cancelSite(key, false); draw(); }));
                } else if (g.isPavingSite(key)) {
                    // A Paved Road site that paves gravel roads is not stopped (0.7.70; ConstructionControl, F).
                    Label paving = caption(String.format(PAVING_WORDS, g.pavingNow()));
                    tip(paving, PAVING_TIP);
                    hand.getChildren().add(paving);
                } else {
                    hand.getChildren().add(action("Cancel", Palette.BAD, () -> confirmCancel(key)));
                }
            }
        } else if (demolition == null) {
            hand.getChildren().add(caption("their order"));
        }
        statusBox.getChildren().add(hand);

        HBox row = new HBox(Palette.GAP,
                cell(order, COL_RANK),
                grow(what),
                cell(progressBox, COL_PROGRESS),
                cell(crewsBox, COL_CREWS),
                cell(moneyBox, COL_MONEY),
                cell(statusBox, COL_STATUS));
        row.setAlignment(Pos.CENTER_LEFT);
        boolean focused = key.equals(focus);
        row.setStyle("-fx-padding: 8 12 8 12; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 1 0 0 0;"
                + (focused ? " -fx-background-color: " + Palette.RAISED + ";" : ""));
        return row;
    }

    /** A stopped shell: what it holds, and Restart or Demolish. */
    private Node shellRow(ConstructionControl.Shell shell) {
        Game g = ui.game;
        BuildingsTemplate t = g.getBuildingManager().getTemplate(shell.templateId);
        double p = t == null ? 0 : t.getConstructionPoints();
        double fraction = p > 0 && shell.buildings > 0 ? shell.progress / (shell.buildings * p) : 0;

        Label title = new Label(shell.building);
        title.setStyle(Palette.strong(Palette.SIZE_BODY, Palette.TEXT_LABEL));
        VBox what = new VBox(2, title, new HBox(6, chip("City", Palette.MONEY),
                caption(String.format("%,d half-built, stopped", shell.buildings))));
        Label count = new Label(String.format("stopped at %.0f%%", fraction * 100));
        count.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        SegmentBar bar = segmentBar(List.of(Segment.of(fraction, Palette.TEXT_SPENT)), 1, null, 0, BAR_BAND);
        bar.setPrefHeight(12);
        bar.setMinHeight(12);
        VBox progressBox = new VBox(3, count, bar);
        VBox crewsBox = new VBox(2, figureLabel("no crews", Palette.TEXT_MUTED), figureLabel("—", Palette.TEXT_MUTED));
        VBox moneyBox = new VBox(2, figureLabel(money(shell.refunded) + " back", Palette.TEXT_LABEL),
                caption("holds its ground"));
        Game.BuildQuote restart = g.quoteRestart(shell);
        Game.DemolitionQuote down = g.quoteShellDemolition(shell);
        HBox hand = new HBox(4);
        if (restart != null) {
            // Asks first, with the money in it, as a cancel and a demolition do
            // (after the docs pass: it spent the quote at a click).
            Button again = action("Restart", Palette.TEXT_BODY, () -> confirmRestart(shell));
            tip(again, String.format("Back on site for its remaining work at today's quote: %s - the work at today's "
                    + "wages, the %,.0f units of material it still has to draw, and the tax.", money(restart.total),
                    restart.materialsNeeded));
            hand.getChildren().add(again);
        }
        if (down != null) {
            hand.getChildren().add(action("Demolish", Palette.BAD, () -> confirmShellDemolition(shell)));
        }
        VBox statusBox = new VBox(4, chip("stopped", Palette.TEXT_MUTED), hand);
        HBox row = new HBox(Palette.GAP, cell(new VBox(), COL_RANK), grow(what), cell(progressBox, COL_PROGRESS),
                cell(crewsBox, COL_CREWS), cell(moneyBox, COL_MONEY), cell(statusBox, COL_STATUS));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 8 12 8 12; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 1 0 0 0;");
        return row;
    }

    /* =====================================================================
       THE TIMELINE: a bar a site, from now to when it finishes at today's
       queue, the city's in its blue and everybody else's in the business
       violet; a stopped shell has no finish to draw.
       ===================================================================== */

    private VBox timeline() {
        Game g = ui.game;
        BuildingManager bm = g.getBuildingManager();
        double atEveryPost = g.getBuildingOutputAtEveryPost();
        List<String> keys = new ArrayList<>(bm.cityOrder());
        for (String k : bm.siteKeysOnSite()) if (!keys.contains(k)) keys.add(k);
        List<String> names = new ArrayList<>();
        List<Double> months = new ArrayList<>();
        List<String> colours = new ArrayList<>();
        double longest = 0;
        for (String k : keys) {
            double m = g.siteMonths(k);
            names.add(bm.nameOfSite(k));
            months.add(m);
            colours.add(bm.isCitySite(k) ? Palette.MONEY : Palette.BUSINESS);
            if (!Double.isNaN(m) && m < 1e6) longest = Math.max(longest, m);
        }
        double axis = Math.min(TIMELINE_MAX, Math.max(12, Math.ceil(longest / 12) * 12));

        Label caption = sectionCaption("WHEN EACH FINISHES");
        HBox key = new HBox(Palette.GAP_LOOSE, keySwatch(Palette.MONEY, "the city"),
                keySwatch(Palette.BUSINESS, "investors and landlords"));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox top = new HBox(Palette.GAP, caption, infoButton("Each bar runs from now to when the site's last "
                + "building finishes at today's queue: this month's crews, held steady - orders placed later "
                + "take crews from it, and a site finishing frees crews for the rest. The city's sites with an "
                + "order set are timed in that order; a rushed one at each month's overtime.", false), gap, key);
        top.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(6, top);
        box.setStyle("-fx-padding: 10 12 12 12;" + Palette.block(Palette.PANEL, Palette.EDGE));
        if (keys.isEmpty()) {
            box.getChildren().add(caption("Nothing on site."));
        } else {
            box.getChildren().add(new Timeline(names, months, colours, axis));
        }
        if (!bm.getControl().shells().isEmpty()) {
            box.getChildren().add(caption(bm.getControl().shells().size() + " stopped shell(s): no finish until restarted."));
        }
        return box;
    }

    /** The longest the timeline's axis runs, in months: fifty years; a site later than that runs off its end. */
    static final double TIMELINE_MAX = 600;

    /** The bars, laid out to the width they are given. */
    private static final class Timeline extends Pane {
        /** The names' column, a row, and the band the years are labelled in above the bars, in pixels. */
        private static final double NAME = 210, ROW = 26, TOP = 18;
        private final List<Double> months;
        private final double axis;
        private final List<Region> bars = new ArrayList<>();
        private final List<Label> names = new ArrayList<>(), ends = new ArrayList<>();
        private final List<Region> ticks = new ArrayList<>();
        private final List<Label> tickNames = new ArrayList<>();

        Timeline(List<String> siteNames, List<Double> months, List<String> colours, double axis) {
            this.months = months;
            this.axis = axis;
            int years = (int) Math.round(axis / 12);
            int every = years <= 6 ? 1 : years <= 15 ? 2 : years <= 30 ? 5 : 10;
            for (int y = 0; y <= years; y += every) {
                Region tick = new Region();
                tick.setStyle("-fx-background-color: " + Palette.HAIRLINE + ";");
                ticks.add(tick);
                Label n = new Label(y == 0 ? "now" : "+" + y + " yr");
                n.setStyle(Palette.figureRegular(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
                n.setUserData((double) y * 12);
                tickNames.add(n);
            }
            getChildren().addAll(ticks);
            getChildren().addAll(tickNames);
            for (int i = 0; i < siteNames.size(); i++) {
                Label name = new Label(siteNames.get(i));
                name.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));
                name.setMaxWidth(NAME - 10);
                Region bar = new Region();
                bar.setStyle("-fx-background-color: " + colours.get(i) + "; -fx-opacity: 0.85;"
                        + " -fx-background-radius: 4;");
                double m = months.get(i);
                Label end = new Label(Double.isNaN(m) ? "stalled" : m > axis ? monthsWait(m) + " ›"
                        : monthsWait(m));
                end.setStyle(Palette.figureRegular(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
                names.add(name);
                bars.add(bar);
                ends.add(end);
            }
            getChildren().addAll(names);
            getChildren().addAll(bars);
            getChildren().addAll(ends);
            double h = TOP + ROW * siteNames.size() + 6;
            setMinHeight(h);
            setPrefHeight(h);
        }

        @Override protected double computePrefWidth(double height) { return 600; }

        @Override protected void layoutChildren() {
            double w = getWidth();
            double x0 = NAME, span = Math.max(40, w - NAME - 90);
            double bottom = TOP + ROW * names.size();
            for (int i = 0; i < ticks.size(); i++) {
                double m = (Double) tickNames.get(i).getUserData();
                double x = x0 + span * m / axis;
                ticks.get(i).resizeRelocate(Math.round(x), TOP - 2, 1, bottom - TOP + 2);
                Label n = tickNames.get(i);
                double nw = n.prefWidth(-1);
                n.resizeRelocate(x - nw / 2, 0, nw, 14);
            }
            for (int i = 0; i < names.size(); i++) {
                double y = TOP + ROW * i + 4;
                Label name = names.get(i);
                name.resizeRelocate(0, y, NAME - 10, 18);
                double m = months.get(i);
                double len = Double.isNaN(m) ? 0 : span * Math.min(1, m / axis);
                bars.get(i).resizeRelocate(x0, y, Math.max(2, len), 18);
                Label end = ends.get(i);
                end.resizeRelocate(x0 + len + 6, y + 1, end.prefWidth(-1), 16);
            }
        }
    }

    /* =====================================================================
       DEMOLISH: the city's buildings by type, the stopped shells, and the
       businesses' and landlords' for a buy-out; pick one, set the number,
       and the staging card says what it would do - the tax dial's pattern:
       nothing happens until "Demolish N", and Discard puts it back.
       ===================================================================== */

    private HBox demolish() {
        Game g = ui.game;
        BuildingManager bm = g.getBuildingManager();

        // ---- the picker ----
        TextField find = new TextField(filter);
        find.setPromptText("Find a building");
        find.setMaxWidth(Double.MAX_VALUE);
        VBox list = new VBox(2);
        find.textProperty().addListener((o, was, now) -> {
            filter = now == null ? "" : now;
            fillPicker(list);
        });
        fillPicker(list);
        javafx.scene.control.ScrollPane scroller = new javafx.scene.control.ScrollPane(list);
        scroller.setFitToWidth(true);
        scroller.setPrefHeight(460);
        scroller.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox picker = new VBox(6, sectionCaption("WHAT TO PULL DOWN"), find, scroller);
        picker.setPrefWidth(290);
        picker.setMinWidth(250);
        picker.setStyle("-fx-padding: 10 10 10 10;" + Palette.block(Palette.PANEL, Palette.EDGE));

        // ---- the staging card ----
        VBox stage = stagingCard();
        HBox.setHgrow(stage, Priority.ALWAYS);
        HBox both = new HBox(Palette.GAP_LOOSE, picker, stage);
        both.setMaxWidth(Double.MAX_VALUE);
        return both;
    }

    /** The picker's rows: the city's buildings, the shells, then the businesses' and the landlords'. */
    private void fillPicker(VBox list) {
        Game g = ui.game;
        BuildingManager bm = g.getBuildingManager();
        list.getChildren().clear();
        String f = filter == null ? "" : filter.trim().toLowerCase();
        List<Node> city = new ArrayList<>(), theirs = new ArrayList<>(), shells = new ArrayList<>();
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (t == null) continue;
            int n = g.demolishable(t);
            if (n <= 0 || (!f.isEmpty() && !t.getName().toLowerCase().contains(f))) continue;
            if (g.isCitysToDemolish(t)) city.add(pickRow(t.getName(), n, false));
            else if (g.isBuyOutable(t)) theirs.add(pickRow(t.getName(), n, false));
        }
        for (ConstructionControl.Shell s : bm.getControl().shells()) {
            if (!f.isEmpty() && !s.building.toLowerCase().contains(f)) continue;
            shells.add(pickRow(s.building, s.buildings, true));
        }
        list.getChildren().add(listHead("THE CITY'S BUILDINGS"));
        if (city.isEmpty()) list.getChildren().add(caption("none standing"));
        list.getChildren().addAll(city);
        if (!shells.isEmpty()) {
            list.getChildren().add(listHead("STOPPED SHELLS"));
            list.getChildren().addAll(shells);
        }
        list.getChildren().add(listHead("BUSINESSES' AND LANDLORDS' - BUY OUT FIRST"));
        if (theirs.isEmpty()) list.getChildren().add(caption("none standing"));
        list.getChildren().addAll(theirs);
    }

    private Label listHead(String text) {
        Label l = new Label(text);
        l.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.TEXT_LABEL) + " -fx-padding: 8 0 2 2;");
        return l;
    }

    private Node pickRow(String name, int n, boolean shell) {
        boolean on = name.equals(pick) && shell == pickShell;
        Label label = new Label(name);
        label.setStyle(Palette.words(Palette.SIZE_BODY, on ? Palette.TEXT_HEAD : Palette.TEXT_LABEL));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label howMany = figureLabel(String.format("%,d", n), Palette.TEXT_MUTED);
        HBox row = new HBox(6, label, gap, howMany);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 6 8 6 8; -fx-cursor: hand; -fx-background-radius: " + Palette.RADIUS + ";"
                + (on ? " -fx-background-color: " + Palette.PINNED + "; -fx-border-color: " + Palette.BUILDING
                        + "; -fx-border-width: 0 0 0 3;" : ""));
        row.setOnMouseClicked(e -> {
            pick = name;
            pickShell = shell;
            count = 1;
            draw();
        });
        return row;
    }

    private VBox stagingCard() {
        Game g = ui.game;
        BuildingManager bm = g.getBuildingManager();
        VBox card = new VBox(Palette.GAP);
        card.setStyle("-fx-padding: 14 16 14 16;" + Palette.block(Palette.PANEL, Palette.EDGE));
        card.setMaxWidth(Double.MAX_VALUE);
        BuildingsTemplate t = pick == null ? null : bm.getTemplateByName(pick);
        ConstructionControl.Shell shell = pickShell && t != null ? bm.getControl().shellOf(t.getId()) : null;
        if (t == null || (pickShell && shell == null) || (!pickShell && g.demolishable(t) <= 0)) {
            pick = null;
            Label none = new Label("Pick a building on the left.");
            none.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_MUTED));
            card.getChildren().addAll(sectionCaption("WHAT IT WOULD DO"), none, rulesLine());
            return card;
        }
        boolean buyOut = !pickShell && g.isBuyOutable(t);
        int most = pickShell ? shell.buildings : g.demolishable(t);
        count = Math.max(1, Math.min(count, most));
        int n = pickShell ? shell.buildings : count;

        Label title = new Label((buyOut ? "Buy out and demolish " : "Demolish ") + t.getName()
                + (pickShell ? " (the shell)" : ""));
        title.setStyle(Palette.strong(Palette.SIZE_SECTION, Palette.TEXT_HEAD));
        title.setWrapText(true);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox head = new HBox(Palette.GAP, title, gap);
        head.setAlignment(Pos.CENTER_LEFT);
        if (!pickShell) {
            Button less = action("−", Palette.TEXT_BODY, () -> { count = Math.max(1, count - 1); draw(); });
            Button more = action("+", Palette.TEXT_BODY, () -> { count = Math.min(most, count + 1); draw(); });
            less.setDisable(count <= 1);
            more.setDisable(count >= most);
            head.getChildren().addAll(less, figureLabel(String.valueOf(count), Palette.TEXT_HEAD), more,
                    caption("of " + most));
        }
        card.getChildren().add(head);
        card.getChildren().add(sectionCaption("WHAT IT WOULD DO"));

        Game.DemolitionQuote q;
        Game.BuyOutQuote b = null;
        if (pickShell) q = g.quoteShellDemolition(shell);
        else if (buyOut) { b = g.quoteBuyOut(t, n); q = b == null ? g.quoteDemolition(t, n) : b.demolition(); }
        else q = g.quoteDemolition(t, n);
        if (q == null) {
            card.getChildren().add(caption("It cannot be priced now."));
            return card;
        }
        if (buyOut && b == null) {
            card.getChildren().add(effect("A buy-out", "not now",
                    "the builders have no output to time a replacement by, so its business loss cannot be struck",
                    Palette.BAD));
            card.getChildren().add(rulesLine());
            return card;
        }

        if (b != null) {
            card.getChildren().add(effect("The owner is paid", money(b.compensation()),
                    payerLabel(g, b.sector()) + " keep their debts; the cash is theirs", Palette.TEXT_HEAD));
            card.getChildren().add(effect("   the building, at its value to them", money(b.buildingValue()),
                    "its cost and its material at today's price", Palette.TEXT_LABEL));
            card.getChildren().add(effect("   its ground, at the land market's price", money(b.ground()),
                    LandManager.areaWords(q.landSqFt()), Palette.TEXT_LABEL));
            card.getChildren().add(effect("   their business loss", money(b.businessLoss()),
                    String.format("%.1f%% of their operating profit, for the %s a replacement would take",
                            b.profitShare() * 100, monthsWait(b.replacementMonths())), Palette.TEXT_LABEL));
        }
        card.getChildren().add(effect("Ground freed", LandManager.areaWords(q.landSqFt()),
                "back on the land office's books when the work is done", Palette.TEXT_HEAD));
        if (q.posts() > 0) {
            card.getChildren().add(effect("Staff let go", String.format("%,d", q.posts()),
                    "the month starts without their posts", Palette.WARN));
        }
        if (q.homes() > 0) {
            card.getChildren().add(effect("Homes closed", String.format("%,d", q.homes()),
                    String.format("about %,.0f households move into what stands, crowded if they must, "
                            + "unhoused past that", q.households()), Palette.WARN));
        }
        if (q.places() > 0) {
            card.getChildren().add(effect(placesWord(t), String.format("%,.0f", q.places()),
                    "gone with the building", Palette.WARN));
        }
        if (q.runningCost() > 0) {
            card.getChildren().add(effect("Running cost saved", money(q.runningCost()) + " a month",
                    "its staff and its upkeep", Palette.TEXT_HEAD));
        }
        card.getChildren().add(effect("Material back", String.format("%,.0f units", q.salvageUnits()),
                q.salvageAffordable() + 1e-9 < q.salvageUnits()
                        ? String.format("the builders could pay for %,.0f of them today, %s", q.salvageAffordable(),
                                money(q.salvageProceeds()))
                        : "the builders would pay " + money(q.salvageProceeds()) + " for it today, to the treasury",
                Palette.TEXT_HEAD));
        card.getChildren().add(effect("Cost to clear the site", money(q.price().total),
                String.format("%,.0f pts of work, 5%% of %s, with the builders' tax", q.points(),
                        pickShell ? "the work it holds" : "what it took to build"), Palette.WARN));
        card.getChildren().add(effect("Takes", atTodaysQueue(q.months()),
                "the builders' crews, beside everything on site", Palette.TEXT_HEAD));

        double total = b != null ? b.total() : q.price().total;
        String verb = buyOut ? "Buy out and demolish " + n : "Demolish " + n;
        Button go = new Button(verb);
        go.setStyle(Palette.words(Palette.SIZE_BODY, "white") + " -fx-font-weight: bold;"
                + " -fx-background-color: " + Palette.ALERT_EDGE + "; -fx-padding: 8 16 8 16; -fx-cursor: hand;");
        go.setDisable(total > g.getCash());
        final Game.BuyOutQuote bq = b;
        final Game.DemolitionQuote dq = q;
        go.setOnAction(e -> {
            if (pickShell) confirmShellDemolition(shell);
            else if (bq != null) confirmBuyOut(t, n, bq);
            else confirmDemolition(t, n, dq);
        });
        Button discard = new Button("Discard");
        discard.setOnAction(e -> { pick = null; count = 1; draw(); });
        Label sure = caption(total > g.getCash()
                ? "The treasury is short of " + money(total - g.getCash()) + "."
                : "Nothing happens until you press it.");
        HBox buttons = new HBox(Palette.GAP, go, discard, sure);
        buttons.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(buttons, new Insets(8, 0, 0, 0));
        card.getChildren().addAll(buttons, rulesLine());
        return card;
    }

    /** The rules behind the card, a line and an (i). */
    private HBox rulesLine() {
        return infoLine("The city's own come down; a business's is bought first.",
                "DEMOLITION. A site of 5% of the building's construction points - Detroit's average demolition of "
                + "July 2015, $14,855 (SIGTARP, 2017), over the average new home of 2015, $289,415 (NAHB) - "
                + "priced as any order: the work at the builders' rate today, no material, and the tax. The building "
                + "closes as the month starts: its staff are let go, its households move into the homes that "
                + "stand (crowded, or unhoused, never deleted), its service goes. When the work is done the "
                + "builders buy its material at the materials market's price, as much as their cash covers, and "
                + "its ground is the city's free land again.\n\n"
                + "BUY-OUTS. A business's or a landlord's building is bought by compulsory purchase first "
                + "(Expropriations Act, R.S.O. 1990, c. E.26, ss. 13, 14 and 19): its market value - the building at "
                + "the value its owner's books and the property tax carry, and its ground at the land market's "
                + "price - and the business loss: its share of the owner's operating profit for the months a "
                + "replacement would take at today's queue, nothing for a loss. No owner here lives in what it "
                + "owns, so the 5% residential allowance of s. 18 does not apply. The owner keeps its loans, "
                + "bonds and mortgages - they are the business's, not the building's - and the cash.",
                true, Palette.SIZE_CAPTION, Palette.TEXT_MUTED, 520);
    }

    private static String placesWord(BuildingsTemplate t) {
        if (t.getTeaches() != null && t.getTeaches() != EducationType.NONE) return "School places lost";
        if (t.getCare() != null && t.getCare() != CareType.NONE) return "Places of care lost";
        if (t.getSafety() == SafetyType.POLICE) return "Officers' places lost";
        if (t.getSafety() != null && t.getSafety() != SafetyType.NONE) return "Cells lost";
        return "Places lost";
    }

    /** One line of the staging card: what, its figure, and a note under it. */
    private HBox effect(String label, String value, String note, String tone) {
        Label what = new Label(label);
        what.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));
        Label says = new Label(note);
        says.setWrapText(true);
        says.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        VBox left = new VBox(1, what, says);
        HBox.setHgrow(left, Priority.ALWAYS);
        Label figure = figureLabel(value, tone);
        figure.setMinWidth(Region.USE_PREF_SIZE);
        HBox row = new HBox(Palette.GAP, left, figure);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 6 0 6 0; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 0 1 0;");
        return row;
    }

    /* =====================================================================
       THE CONFIRMATIONS, with the money in them (UserInterface.confirm()).
       ===================================================================== */

    /** The Paved Road site's words while it paves gravel roads (0.7.70), where Cancel would be: how many, and that it runs on. */
    static final String PAVING_WORDS = "%,d paving · no stop";

    /** ...and why. */
    static final String PAVING_TIP = "Some of these Paved Roads pave gravel roads, which carry their traffic until each"
            + " opens. A road dug a half at a time is not left as a shell, so the site runs until they are done.";

    private void confirmCancel(String key) {
        Game g = ui.game;
        BuildingsStacks s = g.getBuildingManager().stackOfKey(key);
        if (s == null) return;
        double[] back = g.cancelRefundNow(key);
        List<String[]> lines = new ArrayList<>();
        lines.add(new String[] { "Back to the treasury, at most", money(back[0]) });
        lines.add(new String[] { "...of which material not drawn", money(back[1]) });
        ui.confirm(String.format("Stop %,d %s at the month's end?", s.getUnderConstruction(), s.getName()),
                "This month's work is done and billed; the rest of the prepayment comes back. The half-built shell "
                + "keeps its work and its ground.",
                lines, "Stop it", () -> { g.cancelSite(key, true); draw(); });
    }

    private void confirmDemolition(BuildingsTemplate t, int n, Game.DemolitionQuote q) {
        Game g = ui.game;
        List<String[]> lines = new ArrayList<>();
        lines.add(new String[] { "Cost to clear", money(q.price().total) });
        lines.add(new String[] { "Material back, at today's price", money(q.salvageProceeds()) });
        lines.add(new String[] { "Ground freed", LandManager.areaWords(q.landSqFt()) });
        ui.confirm(String.format("Demolish %,d %s?", n, t.getName()),
                "They close as the month starts. This cannot be undone.",
                lines, "Demolish " + n, () -> {
                    if (!g.demolish(t, n)) refused(g.getLastHandRefusal());
                    pick = null;
                    count = 1;
                    draw();
                });
    }

    private void confirmBuyOut(BuildingsTemplate t, int n, Game.BuyOutQuote q) {
        Game g = ui.game;
        List<String[]> lines = new ArrayList<>();
        lines.add(new String[] { "To " + payerLabel(g, q.sector()), money(q.compensation()) });
        lines.add(new String[] { "   building", money(q.buildingValue()) });
        lines.add(new String[] { "   ground", money(q.ground()) });
        lines.add(new String[] { "   business loss", money(q.businessLoss()) });
        lines.add(new String[] { "Cost to clear", money(q.demolition().price().total) });
        lines.add(new String[] { "Altogether", money(q.total()) });
        ui.confirm(String.format("Buy out and demolish %,d %s?", n, t.getName()),
                "A compulsory purchase at market value and the business loss, then the demolition. "
                + "This cannot be undone.",
                lines, "Buy out and demolish " + n, () -> {
                    if (!g.buyOutAndDemolish(t, n)) refused(g.getLastHandRefusal());
                    pick = null;
                    count = 1;
                    draw();
                });
    }

    private void confirmRestart(ConstructionControl.Shell shell) {
        Game g = ui.game;
        Game.BuildQuote q = g.quoteRestart(shell);
        if (q == null) return;
        List<String[]> lines = new ArrayList<>();
        lines.add(new String[] { "Today's quote", money(q.total) });
        lines.add(new String[] { "   the work left, at today's wages", money(q.sticker) });
        lines.add(new String[] { "   its material, at today's price", money(q.boughtInCost())
                + (q.materialsInStock > 0 ? String.format(" (+%,.0f units from the yard)", q.materialsInStock) : "") });
        lines.add(new String[] { "   the builders' sales tax", money(q.salesTax) });
        lines.add(new String[] { "Takes", atTodaysQueue(q.months) });
        ui.confirm(String.format("Restart %,d %s?", shell.buildings, shell.building),
                "Back on site for its remaining work at today's quote, paid now as any order is.",
                lines, "Restart", () -> {
                    if (!g.restartShell(shell.templateId)) refused(g.getLastHandRefusal());
                    draw();
                });
    }

    private void confirmShellDemolition(ConstructionControl.Shell shell) {
        Game g = ui.game;
        Game.DemolitionQuote q = g.quoteShellDemolition(shell);
        if (q == null) return;
        List<String[]> lines = new ArrayList<>();
        lines.add(new String[] { "Cost to clear", money(q.price().total) });
        lines.add(new String[] { "Material back, at today's price", money(q.salvageProceeds()) });
        lines.add(new String[] { "Ground freed", LandManager.areaWords(q.landSqFt()) });
        ui.confirm(String.format("Demolish the shell of %,d %s?", shell.buildings, shell.building),
                "Its work is lost; its material and its ground come back when the demolition is done.",
                lines, "Demolish it", () -> {
                    if (!g.demolishShell(shell.templateId)) refused(g.getLastHandRefusal());
                    pick = null;
                    draw();
                });
    }

    /** A hand the model refused, said where the player is looking. */
    private void refused(String why) {
        if (why != null) ui.confirm("Not done", "The city could not: " + why + ".", null, null, null);
    }

    /* =====================================================================
       THE PIECES this page is drawn with.
       ===================================================================== */

    /** Who placed an order, in the player's words: the city, the landlords, or the business by its name. */
    static String payerLabel(Game g, String payer) {
        if (payer == null || "City".equals(payer)) return "City";
        if (Sectors.REAL_ESTATE.equals(payer)) return "Landlords";
        Sector s = g.getSectors().byKey(payer);
        return s == null ? payer : s.label();
    }

    private static Label sectionCaption(String text) {
        Label l = new Label(text);
        l.setStyle(Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_HEAD) + " -fx-letter-spacing: 1;");
        return l;
    }

    private static Label head(String text) {
        Label l = new Label(text);
        l.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        return l;
    }

    private static Label caption(String text) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        return l;
    }

    private static Label figureLabel(String text, String tone) {
        Label l = new Label(text);
        l.setStyle(Palette.figureRegular(Palette.SIZE_BODY, tone));
        return l;
    }

    // The chip and its tint are Pieces.chip() and Pieces.tint() since 0.7.27.

    private static Button action(String text, String tone, Runnable act) {
        Button b = new Button(text);
        b.setStyle(Palette.words(Palette.SIZE_CAPTION, tone) + " -fx-background-color: transparent;"
                + " -fx-border-color: " + tone + "; -fx-border-radius: " + Palette.RADIUS + ";"
                + " -fx-padding: 3 9 3 9; -fx-cursor: hand;");
        b.setOnAction(e -> act.run());
        return b;
    }

    private static Label arrow(String glyph, boolean live, Runnable act) {
        Label l = new Label(glyph);
        l.setStyle(Palette.words(Palette.SIZE_CAPTION, live ? Palette.TEXT_LABEL : Palette.TEXT_SPENT)
                + (live ? " -fx-cursor: hand;" : ""));
        if (live) l.setOnMouseClicked(e -> act.run());
        return l;
    }

    private static void tip(Node node, String text) {
        Tooltip t = new Tooltip(text);
        t.setShowDelay(Duration.millis(250));
        Tooltip.install(node, t);
    }

    private static Region cell(Node content, double width) {
        StackPane box = new StackPane(content);
        StackPane.setAlignment(content, Pos.CENTER_LEFT);
        box.setMinWidth(width);
        box.setPrefWidth(width);
        box.setMaxWidth(width);
        return box;
    }

    private static Region grow(Node content) {
        StackPane box = new StackPane(content);
        StackPane.setAlignment(content, Pos.CENTER_LEFT);
        box.setMinWidth(110);
        box.setPrefWidth(200);
        box.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }
}
