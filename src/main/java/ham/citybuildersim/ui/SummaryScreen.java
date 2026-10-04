package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javafx.geometry.Pos;
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
 * The left panel's content: the summary and the dashboard - the vitals, the
 * alert block, the six lines that are always worth a glance or the thirteen
 * folded sections - and the problem list that decides what goes red, drawn
 * from CityNeeds.
 *
 * Split out of UserInterface on 2026-09-18, with the banners CITY OVERVIEW
 * PANEL, THE LEFT PANEL, SUMMARY, OR DASHBOARD, HEADROOM, NOT SATISFACTION,
 * THE SUMMARY IS A PROBLEM LIST NOW and SEATS AGAINST WHO WOULD COME exactly
 * as they were, the shell's members reached through ui; five since 0.7.24,
 * when the last went to the model (below). The shell owns the
 * panel itself (cityPanel) and calls refreshCityPanel() on the clock; this
 * class owns what is drawn into it, and which sections the player has opened
 * (panelOpen). Since 0.7.24 the panel is a drawer the header's "Needs you"
 * chip opens (UserInterface, THE FRAME FOLDS AWAY) - always on NEEDS YOU
 * (needsView) - and NEEDS YOU is measured in the
 * model, CityNeeds - the list, its lines and SEATS AGAINST WHO WOULD COME
 * moved there whole - so the Build tab and the chip read the same verdicts.
 */
final class SummaryScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    SummaryScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       HEADROOM, NOT SATISFACTION

       getEnergyRatio() and getWaterRatio() are min(supply/demand, 1) - they are
       the multiplier the economy is throttled by, and as a READOUT they are
       nearly useless: they sit at exactly 100% right up until the moment the
       city browns out, then fall off a cliff. "Everything is fine" and "you are
       one factory away from a blackout" print identically.

       So the panel shows the same fraction UNCLAMPED: what the city can
       generate over what it draws - "served" since 0.7.41, Jerus's one rule for
       every gauge (it showed the other side, "% used", until then). That
       number falls steadily and visibly towards 100%, which is what makes it a
       warning rather than an obituary, and under 100% it keeps counting: how
       far short you are is exactly what you need to know while fixing it.
       ===================================================================== */

    /** What a network serves (0.7.41; it was "% used"), in the one verdict: amber from a third in hand, red once it serves no more than it is asked. */
    HBox utilityLine(String label, double served, double consumption, double production) {

        if (production <= 0) {
            // No plant at all. Not a shortage until something actually draws.
            return statLine(label, consumption > 0 ? "no supply" : "-",
                    consumption > 0 ? PANEL_BAD : null);
        }

        CityNeeds.Served s = CityNeeds.verdict(CityNeeds.Kind.POWER, CareType.NONE, served);
        HBox row = statLine(label, CityNeeds.servedPct(s.share()) + " " + CityNeeds.SERVED, BuildScreen.servedTone(s));
        ((Label) row.getChildren().get(2)).setMinWidth(Region.USE_PREF_SIZE);
        return row;
    }

    /**
     * Roads, in NEEDS YOU's colour for them (0.7.29) - in the one verdict on
     * what the road serves since 0.7.41.
     *
     * The two thresholds Jerus asked for - "above 90%" and "restricting flow" -
     * turn out to be the SAME line: InfrastructureManager.FREE_FLOW is 0.9, so
     * throughput starts falling at exactly 90% utilisation; and STRAINED (0.85)
     * gives the amber step, the last point at which building more roads is
     * cheaper than the congestion. Both are NEEDS YOU's ROADS row's lines, and
     * from 0.7.29 the row's own level was the colour: it was red here on
     * isCongested() while Build's tile, on the same row, was amber with road
     * sites on the way - one road, two verdicts. One judge since (the
     * project's spec-infra-0729.md, D3), and since 0.7.41 that judge is the
     * one verdict on what the road serves - the same two lines turned over,
     * red at or under 1/FREE_FLOW, green past 1/STRAINED - which road sites
     * on the way no longer soften.
     */
    HBox roadLine() {
        HBox row = statLine("Roads", roadSummary(), roadColour());
        // The pair is longer than the one figure it replaced: it is never cut to "…".
        ((Label) row.getChildren().get(2)).setMinWidth(Region.USE_PREF_SIZE);
        return row;
    }

    /** The road's verdict: since 0.7.41 the one verdict on what it serves, as every road gauge has it (it was NEEDS YOU's ROADS row's level, amber while road sites were on the way). */
    String roadColour() {
        InfrastructureManager roads = ui.game.getInfrastructureManager();
        return BuildScreen.servedTone(CityNeeds.verdict(CityNeeds.Kind.ROADS, CareType.NONE, roads.getServed()));
    }

    /**
     * Roads on the city overview, in one cell: "62% served · 56% flow" (0.7.29;
     * "162% full" until 0.7.41).
     *
     * Both figures, always, in whole per cents. It used to show how full the
     * road was while there was room and the flow once there was not, which
     * changed what the row meant at the congestion line without saying so -
     * and printed up to three decimals ("55.694% flow"). Served is the
     * capacity over the load the curve reads, flow what every business gets
     * through it; the Infrastructure tab's Roads page draws the curve that
     * links them.
     */
    String roadSummary() {
        InfrastructureManager roads = ui.game.getInfrastructureManager();
        return CityNeeds.servedPct(roads.getServed()) + " " + CityNeeds.SERVED + " · "
                + BuildScreen.pct(roads.getThroughputRatio()) + " flow";
    }

    /* =====================================================================
       CITY OVERVIEW PANEL

       Everything the player should be able to see without navigating: the
       economy, the labour market, what they own, and what's in the warehouses.

       Every value here is a pure read of an already-computed field. Nothing on
       this path recalculates anything - in particular it deliberately avoids
       EconomyManager.getMonthGdp(), which reassigns the GDP field as a side
       effect and would make simply looking at a screen change the simulation.
       ===================================================================== */

    /* =====================================================================
       THE LEFT PANEL

       Rebuilt from a seventy-row wall into a dozen. Jerus: "have it so it shows
       the main stuff, clicking shows more info ... also make it not white, and
       make the numbers bolder".

       THE NUMBERS ARE THE CONTENT AND THE LABELS ARE THE INDEX. Every row used
       to be one monospaced Label - "Cash        $1,204,300" - which gives the
       word and the figure identical weight, so reading the panel meant reading
       all of it. They are two labels now: the name small and grey, the figure
       bigger, brighter and bold. A player scanning for a number finds a column
       of numbers instead of a column of text that contains numbers.

       AND MOST OF IT IS FOLDED AWAY. The panel carried around seventy rows,
       which is not an overview, it is a report that happens to be narrow -
       and the eight rows that actually matter were buried among sixty that
       matter once a decade. Now: the vitals always, an alert when something is
       wrong, and eight collapsed sections each showing the one figure that
       says whether it is worth opening.
       ===================================================================== */

    /** Which sections and rows the player has opened. Survives every redraw. */
    final java.util.Set<String> panelOpen = new java.util.HashSet<>();

    static final String PANEL_LABEL = Palette.TEXT_LABEL;
    static final String PANEL_VALUE = Palette.TEXT_HEAD;
    static final String PANEL_GOOD  = Palette.GOOD;
    static final String PANEL_WARN  = Palette.WARN;
    static final String PANEL_BAD   = Palette.BAD;

    HBox statLine(String label, String value) {
        return statLine(label, value, null);
    }

    /**
     * One row: what it is on the left, what it reads on the right.
     *
     * @param tone a hex colour for the FIGURE when it is saying something -
     *             a shortage, an overdraft, a coverage gap. The label never
     *             changes colour, because the label is never the news.
     */
    HBox statLine(String label, String value, String tone) {
        Label name = new Label(label);
        name.setStyle("-fx-font-size: 10px; -fx-text-fill: " + PANEL_LABEL + ";");

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label figure = new Label(value);
        figure.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 11px;"
                + " -fx-font-weight: bold; -fx-text-fill: "
                + (tone == null ? PANEL_VALUE : tone) + ";");

        HBox row = new HBox(6);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);
        row.setStyle("-fx-padding: 1 2 1 4;");
        row.getChildren().addAll(name, gap, figure);
        return row;
    }

    /**
     * A row that hides something, and says so.
     *
     * The whole redesign in one method. A section is a headline figure the
     * player can read without opening anything - "LABOUR 94%" answers the
     * question most months - and the detail behind it only costs screen space
     * on the months it is actually wanted. Jerus's example was the fill rate:
     * one number normally, the whole skill ladder when you ask.
     *
     * State lives in panelOpen and not on the node, because this panel is
     * rebuilt from scratch on every screen change - anything remembered by the
     * widget would be forgotten the next time the player pressed a button.
     */
    VBox panelSection(String key, String heading, String summary,
                              String tone, java.util.function.Supplier<VBox> detail) {

        boolean open = panelOpen.contains(key);

        Label caret = new Label(open ? "\u25be" : "\u25b8");
        caret.setStyle("-fx-font-size: 9px; -fx-text-fill: " + PANEL_LABEL + ";");

        Label name = new Label(heading);
        name.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: "
                + (open ? Palette.ACCENT : Palette.TEXT_LABEL) + ";");

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label figure = new Label(summary == null ? "" : summary);
        figure.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 11px;"
                + " -fx-font-weight: bold; -fx-text-fill: "
                + (tone == null ? PANEL_VALUE : tone) + ";");

        HBox header = new HBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setStyle("-fx-padding: 4 2 4 0; -fx-cursor: hand;"
                + (open ? " -fx-background-color: " + Palette.RAISED + "; -fx-background-radius: 3;" : ""));
        header.getChildren().addAll(caret, name, gap, figure);
        header.setOnMouseClicked(e -> {
            if (!panelOpen.remove(key)) panelOpen.add(key);
            refreshCityPanel();
        });

        VBox box = new VBox(0);
        box.setMaxWidth(Double.MAX_VALUE);
        box.getChildren().add(header);

        if (open) {
            VBox body = detail.get();
            body.setStyle("-fx-padding: 2 0 6 8; -fx-border-color: " + Palette.EDGE + ";"
                    + " -fx-border-width: 0 0 0 1;");
            box.getChildren().add(body);
        }
        return box;
    }

    /** A section's detail, built from rows. Sugar, to keep the sections short. */
    VBox panelBody(javafx.scene.Node... rows) {
        VBox body = new VBox(0);
        body.getChildren().addAll(rows);
        return body;
    }

    /* =====================================================================
       SUMMARY, OR DASHBOARD

       Jerus: "i think you can switch from summary and dashboard, like at a
       switch, so uh both?"

       The panel had grown into a twelfth rail you read instead of navigate:
       twelve headline rows, each unfolding into a small statement, all of which
       now have a real screen behind them that did not exist when the panel was
       written. That is a good dashboard and a poor summary, and the two are
       genuinely different jobs:

         SUMMARY   is what you read WHILE doing something else. Six lines that
                   are always true and always worth a glance, no carets, nothing
                   to open - and a click goes to the tab that owns the number
                   rather than unfolding it, because in this mode there is
                   nothing to unfold and a dead click is worse than no click.

         DASHBOARD is the instrument set: all thirteen sections, folded the way
                   you left them, with open all / close all so the whole wall is
                   one click away when you actually want to read it.

       TWO THINGS ARE IN BOTH: the vitals, and the alert block. Cash, income and
       population are the panel's reason to exist, and an outbreak is not
       something a display mode gets to hide.

       SUMMARY IS NOT A FIXED SIX. Two of them earn a place only when they are
       saying something - the bank (its premium until 0.7.7, its strain and
       its failure since) and how far the currency has run - because neither
       is an emergency, so the red alert block above will never carry them.
       Both went in as quiet taxes on everything the city does: a player who
       lives in Summary would otherwise never learn that every loan in the
       city got dearer. The bank's tax was its premium, which 0.7.7 took out.
       ===================================================================== */

    /**
     * THE CHIP OPENS ON NEEDS YOU (0.7.24, after the PC check). Jerus's
     * settings had the panel on Dashboard, which has no NEEDS YOU list, so
     * the "Needs you" chip opened a drawer that did not show what it
     * counts. True from the chip's click (UserInterface.openOnNeeds()) until
     * the player picks a mode in the drawer: the panel draws as Summary,
     * whatever is stored, and the stored mode (GamePrefs.isPanelDashboard())
     * is changed only by that pick.
     */
    boolean needsView;

    /** Whether the panel draws as Dashboard now: the stored mode, unless the chip opened it on NEEDS YOU. */
    boolean dashboardShown() {
        return ui.prefs.isPanelDashboard() && !needsView;
    }

    HBox panelModeSwitch() {

        HBox row = new HBox(4);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 0 0 8 2;");
        row.getChildren().addAll(
                panelModeChip("Summary",   !dashboardShown(), false),
                panelModeChip("Dashboard",  dashboardShown(), true));
        return row;
    }

    Label panelModeChip(String text, boolean on, boolean dashboard) {

        Label chip = new Label(text);
        chip.setStyle("-fx-font-size: 9px; -fx-padding: 2 8 3 8; -fx-cursor: hand;"
                + " -fx-background-radius: 3;"
                + " -fx-background-color: " + (on ? Palette.RAISED : "transparent") + ";"
                + " -fx-border-color: " + (on ? Palette.ACCENT : "transparent") + ";"
                + " -fx-border-width: 0 0 2 0;"
                + " -fx-text-fill: " + (on ? Palette.TEXT_HEAD : PANEL_LABEL) + ";");
        chip.setOnMouseClicked(e -> {
            // The player's pick: the chip's Summary gives way, and the pick is kept.
            needsView = false;
            ui.prefs.setPanelDashboard(dashboard);
            ui.prefs.save(ui.game.getGameFiles());
            refreshCityPanel();
        });
        return chip;
    }

    /**
     * One row of the summary: what it is, and what it reads.
     *
     * TWO LINES, AND THAT IS THE FIX. It was a name on the left and a figure on
     * the right of one line, in a panel 290px wide - which was fine for "LAND"
     * against "87% used" and is not fine for "UNIVERSITY" against "0 seats, 372
     * would come". JavaFX does what it does to text that will not fit, and this
     * panel has no folds, so Jerus: "alot are still '...' which is not good,
     * specially since the player can never expand it."
     *
     * A truncated figure is worse than a missing one: it looks like a reading
     * and is not one. The label takes the first line and the reading takes the
     * whole width of the second, wrapping if it has to, so there is no string
     * this panel can be handed that it cannot show.
     */
    VBox summaryRow(String heading, String value, String tone, Runnable go) {

        Label name = new Label(heading);
        name.setStyle("-fx-font-size: 9px; -fx-font-weight: bold;"
                + " -fx-text-fill: " + Palette.TEXT_MUTED + ";");

        Label figure = new Label(value);
        figure.setWrapText(true);
        figure.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 11px;"
                + " -fx-font-weight: bold; -fx-text-fill: "
                + (tone == null ? PANEL_VALUE : tone) + ";");

        VBox row = new VBox(-1, name, figure);
        row.setMaxWidth(Double.MAX_VALUE);
        String rest = "-fx-padding: 4 2 5 4; -fx-cursor: hand;";
        row.setStyle(rest);
        row.setOnMouseClicked(e -> go.run());
        row.setOnMouseEntered(e -> row.setStyle(rest
                + " -fx-background-color: " + Palette.RAISED + "; -fx-background-radius: 3;"));
        row.setOnMouseExited(e -> row.setStyle(rest));
        return row;
    }

    /** Open everything, or close it. Dashboard only - Summary has no folds. */
    HBox panelFoldAll() {

        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 6 0 2 4;");
        row.getChildren().addAll(
                foldLink("open all", () -> {
                    panelOpen.addAll(java.util.Arrays.asList(PANEL_SECTIONS));
                    refreshCityPanel();
                }),
                foldLink("close all", () -> {
                    panelOpen.clear();
                    refreshCityPanel();
                }));
        return row;
    }

    Label foldLink(String text, Runnable act) {
        Label link = new Label(text);
        link.setStyle("-fx-font-size: 9px; -fx-text-fill: " + Palette.ACCENT + "; -fx-cursor: hand;"
                + " -fx-underline: true;");
        link.setOnMouseClicked(e -> act.run());
        return link;
    }

    /** Every section key, so open-all does not have to be kept in step by hand. */
    static final String[] PANEL_SECTIONS = {
        "econ", "bank", "trade", "tax", "labour", "school",
        "people", "health", "safety", "res", "land", "sectors", "built"
    };

    /** A caption inside an open section - a sub-heading, or a note. */
    Label panelNote(String text) {
        Label note = new Label(text);
        note.setWrapText(true);
        note.setMaxWidth(240);
        note.setStyle("-fx-font-size: 9px; -fx-text-fill: " + Palette.TEXT_MUTED + "; -fx-padding: 3 0 1 4;");
        return note;
    }

    /* =====================================================================
       THE SUMMARY IS A PROBLEM LIST NOW.

       Jerus: "if its near limit but not at limit, it will be yellow and it will
       appear in the summary, the summary will only have that, and basically you
       can click it, and it will take you to the respective location, and it
       turns red as well, and when solved it disappears."

       The six fixed rows were a dashboard in miniature - economy, tax, labour,
       health, land, people, every month, whatever the city was doing. Of the
       sixteen things a player can BUILD their way out of, those six could show
       exactly one, and only as a sick rate, which is a consequence rather than
       a cause. A city with no water, no cells and nowhere to bury its dead read
       exactly like a city with none of those problems.

       TWO HALVES, and the difference between them is whether there is a lever
       at the end of the row:

         NEEDS YOU   - a condition with a fix. Yellow near the line, red past
                       it, gone the moment it is solved. Empty is the goal, and
                       an empty list still names whatever is closest to a line
                       so the panel is never a blank.

         HOW THE CITY IS - the readings that have no dial of their own:
                       unemployment, hunger, sickness, who is arriving. Always
                       there, coloured when they are bad, and the door goes to
                       the screen that DIAGNOSES them rather than pretending
                       there is a lever. Jerus: "at the bottom of the summary
                       area, is all the stuff that is symptoms."

       THRESHOLDS ARE PER CONDITION, not one rule, because the same percentage
       means different things: a city at 90% of its burial capacity is fine and
       a city at 90% of its water is not. Since 0.7.24 they are measured in the
       model, CityNeeds.measure() - every line, threshold and reading as they
       stood in watchAll() here - because the Build tab's overview and its
       suggestions (BuildAdvice) read the same verdicts, and the header's
       "Needs you" chip counts the same list. This class maps each need to the
       screen that answers it (goTo(), and since 0.7.32 financesDoor() for
       the three Finances rows) and draws it.
       ===================================================================== */

    /**
     * One thing being watched.
     *
     * @param level 0 fine, 1 near the line, 2 past it
     * @param near  how close to the yellow line, 0 to 1, and only meaningful at
     *              level 0 - it is what picks the "next to watch" line on a
     *              city with nothing wrong.
     * @param tip   the row's tooltip, or null for none (0.7.27: HUNGRY's, which
     *              gives the points hunger adds to the sick rate)
     * @param verdict the level its colour is read at (0.7.41): a served row's
     *              one verdict (CityNeeds.Need.verdictLevel()), any other row's
     *              level
     */
    record Watch(String label, String reading, int level, double near, Runnable go, String tip, int verdict) {
        Watch(String label, String reading, int level, double near, Runnable go) {
            this(label, reading, level, near, go, null, level);
        }
        Watch(String label, String reading, int level, double near, Runnable go, String tip) {
            this(label, reading, level, near, go, tip, level);
        }
    }

    /** Higher is worse. */
    void over(java.util.List<Watch> out, String label, String reading,
                      double value, double yellow, double red, Runnable go) {
        int level = value >= red ? 2 : value >= yellow ? 1 : 0;
        out.add(new Watch(label, reading, level,
                yellow <= 0 ? 1 : Math.max(0, Math.min(1, value / yellow)), go));
    }

    /** Lower is worse. */
    void under(java.util.List<Watch> out, String label, String reading,
                       double value, double yellow, double red, Runnable go) {
        int level = value <= red ? 2 : value <= yellow ? 1 : 0;
        out.add(new Watch(label, reading, level,
                value <= 0 ? 1 : Math.max(0, Math.min(1, yellow / value)), go));
    }

    /** The interface's own words for a figure, which the needs are read in (CityNeeds.Words). */
    static final CityNeeds.Words WORDS = new CityNeeds.Words() {
        @Override public String people(double count)      { return Money.people(count); }
        @Override public String money(double thousands)   { return Money.money(thousands); }
        @Override public String shortNumber(double value) { return Money.shortNumber(value); }
        @Override public String monthsWait(double months) { return Pieces.monthsWait(months); }
    };

    /**
     * Everything with a lever, measured against its own line: CityNeeds'
     * list (0.7.24; it was measured here), each need with the door to the
     * screen that answers it. Returns the whole set INCLUDING the ones that
     * are fine, because the renderer needs the fine ones to answer "what is
     * closest" on a healthy city. It filters; this only measures.
     */
    java.util.List<Watch> watchAll() {
        java.util.List<Watch> out = new java.util.ArrayList<>();
        for (CityNeeds.Need n : CityNeeds.measure(ui.game, WORDS)) {
            // Listed and ordered by its level; coloured by its verdict (0.7.41, Need.verdictLevel()),
            // so a served row reads the colour its gauges do.
            out.add(new Watch(n.label(), n.reading(), n.level(), n.near(),
                    n.go() == CityNeeds.Go.FINANCES ? financesDoor(n.kind()) : goTo(n.go()), null, n.verdictLevel()));
        }
        return out;
    }

    /**
     * Where a Finances row goes (0.7.32, the Finances spec's D17), through
     * FinancesScreen.open() so it lands where it says rather than on the last
     * page the tab had open (its B8): TREASURY on the hub, FALLS DUE on the
     * hub's ladder, BORROWING on Your rate.
     */
    Runnable financesDoor(CityNeeds.Kind kind) {
        return switch (kind) {
            case BORROWING -> () -> ui.financesScreen.open("The position", "Your rate", null);
            case FALLS_DUE -> () -> ui.financesScreen.open(null, null, FinancesScreen.LADDER);
            default        -> () -> ui.financesScreen.open(null, null, null);
        };
    }

    /**
     * Where a need's row goes: the Build category that answers it, the land
     * office, the builders' books, Finances, the bank, or the Policy page of
     * the promise, the wage floor or the taxes - the doors watchAll() opened
     * before 0.7.24, with the Build categories by their new names.
     */
    Runnable goTo(CityNeeds.Go go) {
        switch (go) {
            case UTILITIES:  return () -> ui.buildScreen.openCategory(BuildAdvice.UTILITIES);
            case ROADS:      return () -> ui.buildScreen.openCategory(BuildAdvice.ROADS);
            case HEALTHCARE: return () -> ui.buildScreen.openCategory(BuildAdvice.HEALTHCARE);
            case EDUCATION:  return () -> ui.buildScreen.openCategory(BuildAdvice.EDUCATION);
            case SAFETY:     return () -> ui.buildScreen.openCategory(BuildAdvice.SAFETY);
            case HOMES:      return () -> ui.buildScreen.openCategory(BuildAdvice.HOMES);
            case LAND:       return ui.landScreen::showLandMenu;
            case BUILDERS:   return () -> ui.sectorScreen.openSectorBooks(ui.game.getSectors().construction(), "Investors");
            case FINANCES:   return () -> ui.financesScreen.open(null, null, null);
            case BANK:       return ui.bankScreen::showBankMenu;
            case PENSIONS:   return () -> {
                ui.policyScreen.policyArea = "Promises";
                ui.policyScreen.policyPage = "Pensions";
                ui.policyScreen.dropProposal();
                ui.policyScreen.showPolicyMenu();
            };
            case WAGES:      return () -> {
                ui.policyScreen.policyArea = "Wages";
                ui.policyScreen.policyPage = PolicyScreen.POLICY_WAGE_PAGES[0];
                ui.policyScreen.dropProposal();
                ui.policyScreen.showPolicyMenu();
            };
            default:         return () -> {
                ui.policyScreen.policyArea = "Taxes";
                ui.policyScreen.policyPage = PolicyScreen.POLICY_HOME;
                ui.policyScreen.dropProposal();
                ui.policyScreen.showPolicyMenu();
            };
        }
    }

    /**
     * The readings with no dial of their own.
     *
     * Every one of these is the RESULT of something on the list above, which is
     * why they are separated rather than mixed in: a row that says "40% out of
     * work" and offers no fix is a row a player learns to scroll past. The door
     * goes to the screen that explains the cause.
     */
    java.util.List<Watch> citySymptoms() {

        java.util.List<Watch> out = new java.util.ArrayList<>();
        PopulationManager people = ui.game.getPopulationManager();
        Health health = ui.game.getHealth();
        PopulationCohorts pyramid = ui.game.getCohorts();

        double jobless = people.getUnemploymentRate();
        over(out, "OUT OF WORK", String.format("%.1f%%", jobless * 100),
                jobless, .12, .20, ui.peopleScreen::showPopulationInfoMenu);

        // Its lines are CityNeeds' since 0.7.28, which Services colours the same
        // figure by; and it opens Health's Overview, not whatever Services showed last.
        double sick = health.getSickRate();
        over(out, "OFF SICK", String.format("%.1f%%", sick * 100),
                sick, CityNeeds.SICK_YELLOW, CityNeeds.SICK_RED, () -> ui.servicesScreen.open("Health", "Overview"));

        /*
         * THE SHARE OF PEOPLE, AS THE PAGE IT OPENS SAYS IT (0.7.27). It read
         * Health.getHungerRate(), the points hunger adds to the sick rate
         * (6.3), and opened Household money, whose GOING SHORT is the share
         * of the city eating less than a basket (42.2%) - two figures under
         * one word. It reads the share now, on GOING SHORT's own lines; the
         * points are in its tooltip.
         */
        double hungry = ui.game.getHouseholdBalance().getHungerRate();
        over(out, "HUNGRY", hungry > 0 ? String.format("%.1f%% of people", hungry * 100) : "nobody",
                hungry, PeopleScreen.GOING_SHORT_WARN, PeopleScreen.GOING_SHORT_BAD, ui.peopleScreen::showHouseholdMenu);
        Watch last = out.remove(out.size() - 1);
        out.add(new Watch(last.label(), last.reading(), last.level(), last.near(), last.go(), String.format(
                "%.1f%% of the city ate less than a basket this month.%nHunger adds %.1f points to the sick rate.",
                hungry * 100, health.getHungerRate() * 100)));

        double net = ui.game.getMigration().getLastNet()
                + pyramid.getLastBirths() - pyramid.getLastDeaths();
        under(out, "PEOPLE", String.format("%+,.0f a month", net),
                net, 0, -Math.max(1, people.getPopulation() * .005),
                ui.peopleScreen::showPopulationInfoMenu);

        // MEASURED: a city buys ground as it needs it, so utilisation sits at
        // 100% for centuries with nothing wrong - it is what the city is doing,
        // not a thing to fix. The problem it becomes is LAND, above, which
        // fires when an investor actually cannot break ground.
        double used = ui.game.getLandManager().getUtilisation();
        // Thresholds above 1 on purpose: a fraction cannot reach them, so this
        // row never colours. It is here to be read, not to raise an alarm.
        over(out, "GROUND USED", String.format("%.0f%% of what the city owns", used * 100),
                used, 2, 3, ui.landScreen::showLandMenu);

        /*
         * AND THE CURRENCY IS A READING TOO. Measured at 38% off parity by
         * month 300 on a city with nothing wrong, and this project established
         * why: about 70% of the drift is the WORLD inflating rather than the
         * city deflating, and no outflow the player can cause closes a PPP gap.
         * See why-there-is-no-inflation.md. A red row with no fix at the end of
         * it is a row players learn to ignore.
         */
        ForeignAccounts fx = ui.game.getForeignAccounts();
        double drift = Math.abs(fx.deviationFromParity());
        // ...ON THE ONE PARITY RULE (0.7.35): the Trade tab and the header's rate
        // line read the same two lines, and the row opens The currency rather
        // than whatever Trade page was last open (the Trade spec's B9, B18).
        over(out, "THE CURRENCY", fx.isPinned() ? "pinned"
                        : String.format("%.0f%% %s than parity", drift * 100,
                                fx.deviationFromParity() > 0 ? "weaker" : "stronger"),
                fx.isPinned() ? 0 : drift, ForeignAccounts.PARITY_WATCH, ForeignAccounts.PARITY_FAR,
                () -> ui.tradeScreen.open(TradeScreen.CURRENCY));

        /*
         * THE SHAPE OF THE HOUSING STOCK IS A READING, NOT A LEVER.
         *
         * Jerus: "its a residential thing, so the business takes care of it, so
         * it should be a symptom not a lever." He is right, and it is the rule
         * the Build tab already draws (in amber until 0.7.21, a violet dot on the
         * tab since) - investors put up housing on
         * their own whenever it pays, so "too many of them are studios" is not
         * an instruction to the player, it is a fact about what the landlords
         * chose to build. The door goes to the screen that explains it.
         */
        double refused = ui.game.getFamilies().getRefusedByStudio();
        over(out, "TURNED AWAY", refused >= 1
                        ? people(refused) + " need a bigger home" : "nobody",
                refused, 1, Math.max(100, people.getPopulation() * .02),
                ui.peopleScreen::showHouseholdMenu);

        double doubled = ui.game.getFamilies().getDoubledUpHouseholds();
        over(out, "DOUBLED UP", doubled >= 1 ? people(doubled) + " households" : "nobody",
                doubled, 1, Math.max(100, people.getPopulation() * .05),
                ui.peopleScreen::showHouseholdMenu);

        return out;
    }

    void panelSummaryRows(VBox body) {

        VBox rows = new VBox(0);
        rows.setStyle("-fx-padding: 4 0 0 0;");

        /* ----------------------------- what needs you ----------------------------- */
        java.util.List<Watch> all = watchAll();
        java.util.List<Watch> biting = new java.util.ArrayList<>();
        for (Watch w : all) if (w.level() > 0) biting.add(w);

        // Red above yellow; inside a tier the order they were measured in, which
        // is the order a city is actually built. A list that re-sorts itself
        // every month is a list nobody can learn the shape of.
        biting.sort((a, b) -> Integer.compare(b.level(), a.level()));

        rows.getChildren().add(panelHeading(biting.isEmpty()
                ? "NOTHING NEEDS YOU" : "NEEDS YOU"));

        if (biting.isEmpty()) {
            Watch next = null;
            for (Watch w : all) if (next == null || w.near() > next.near()) next = w;
            if (next != null) {
                // Green while it is enough; a served row short of 100% is amber though not listed (0.7.41).
                rows.getChildren().add(summaryRow("NEXT TO WATCH",
                        next.label().toLowerCase() + " · " + next.reading(),
                        next.verdict() >= 2 ? PANEL_BAD : next.verdict() == 1 ? PANEL_WARN : PANEL_GOOD, next.go()));
            }
        } else {
            for (Watch w : biting) {
                rows.getChildren().add(summaryRow(w.label(), w.reading(),
                        w.verdict() >= 2 ? PANEL_BAD : PANEL_WARN, w.go()));
            }
        }

        /* ------------------------------ the symptoms ------------------------------ */
        rows.getChildren().add(panelHeading("HOW THE CITY IS"));
        for (Watch w : citySymptoms()) {
            VBox row = summaryRow(w.label(), w.reading(),
                    w.level() >= 2 ? PANEL_BAD : w.level() == 1 ? PANEL_WARN : null,
                    w.go());
            if (w.tip() != null) javafx.scene.control.Tooltip.install(row, new javafx.scene.control.Tooltip(w.tip()));
            rows.getChildren().add(row);
        }

        body.getChildren().add(rows);
        body.getChildren().add(panelNote(biting.isEmpty()
                ? "Nothing is near a limit. The readings below are what the city is "
                  + "doing about it; Dashboard has every figure."
                : "Everything above is near a limit or past one, and every one of them "
                  + "has a fix. Click to go there; they leave this list when they are "
                  + "solved."));
    }

    /** A rule and a caption, dividing the panel's two halves. */
    VBox panelHeading(String text) {

        Label head = new Label(text);
        head.setStyle("-fx-font-size: 9px; -fx-font-weight: bold;"
                + " -fx-text-fill: " + Palette.TEXT_LABEL + ";");

        Region rule = new Region();
        rule.setMinHeight(1);
        rule.setPrefHeight(1);
        rule.setMaxHeight(1);
        rule.setStyle("-fx-background-color: " + Palette.EDGE + ";");

        VBox box = new VBox(2, head, rule);
        box.setStyle("-fx-padding: 10 2 4 4;");
        return box;
    }

    /**
     * The thirteen sections, folded the way the player left them.
     *
     * Lifted out of refreshCityPanel() whole when the Summary switch went in -
     * it re-reads what it needs from the game rather than taking a dozen
     * parameters, because the alternative was a signature nobody could call
     * without checking, for a method with exactly one caller.
     */
    void panelDashboardSections(VBox body) {

        EconomyManager economy = ui.game.getEconomyManager();
        PopulationManager people = ui.game.getPopulationManager();
        BuildingManager buildings = ui.game.getBuildingManager();
        UtilitiesHandler utilities = ui.game.getServicesManager().getUtilitiesHandler();
        LabourMarket market = ui.game.getLabourMarket();
        Health health = ui.game.getHealth();
        Healthcare service = ui.game.getHealthcare();
        LandManager land = ui.game.getLandManager();
        PopulationCohorts pyramid = ui.game.getCohorts();

        int population = people.getPopulation();
        // Annualised from the months there are until a year has been recorded
        // (0.7.20): getYearGdp() summed however many months there were and
        // called it a year, so GDP per head and debt to GDP ran 12/n too high.
        double annualGdp = annualGdp(economy.getNationalAccounts());
        boolean scaled = gdpEstimated(economy.getNationalAccounts());
        double debt = ui.game.getDebtManager().getAllPrincipal();

        /* ================= ECONOMY ================= */
        body.getChildren().add(panelSection("econ", "ECONOMY",
                money(economy.getMonthGdp()) + "/mo", null,
                () -> {
                    VBox b = panelBody(
                            statLine("Monthly GDP", money(economy.getMonthGdp())),
                            statLine(scaled ? "GDP, annualised" : "Annual GDP", money(annualGdp)));
                    if (population > 0 && annualGdp != 0) {
                        b.getChildren().add(statLine("GDP/capita",
                                money(annualGdp / population)));
                    }
                    b.getChildren().addAll(
                            statLine("Debt", money(debt)),
                            statLine("Biz debt", money(
                                    economy.getBusinessDebtManager().getTotalPrincipal())),
                            statLine("Interest",
                                    formatter.format(ui.game.getInterestRate() * 100) + "%"),
                            statLine("Rating", ui.game.getCreditRating()));
                    if (annualGdp != 0) {
                        b.getChildren().add(statLine("Debt/GDP",
                                formatter.format((debt / annualGdp) * 100) + "%"));
                    }
                    return b;
                }));

        /* ================= BANK =================
         *
         * Under ECONOMY because it is the price of money. Its collapsed line
         * was what the bank added to every rate in the city until 0.7.7, when
         * the strain premium went (Bank, WHAT A LOAN COSTS); now it is how
         * full the bank is and its prime, red when the book is past capacity.
         */
        Bank bankPanel = ui.game.getBank();
        double bankPrime = bankPanel.prime(ui.game.getDebtManager().getPolicyRate());
        double bankCapacity = bankPanel.capacity();
        body.getChildren().add(panelSection("bank", "BANK",
                bankPanel.isInsolvent() ? "INSOLVENT"
                        : bankPanel.getBranches() <= 0
                        ? "none  \u00b7  prime " + String.format("%.2f%%", bankPrime * 100)
                        : String.format("%.0f%% lent  \u00b7  prime %.2f%%", bankCapacity > 0
                                ? bankPanel.getWeightedBook() / bankCapacity * 100 : 0,
                                bankPrime * 100),
                bankPanel.strain() > 1 ? Palette.BAD_SOFT : null,
                () -> {
                    VBox b = panelBody(
                            statLine("Branches", formatter.format(bankPanel.getBranches())),
                            statLine("Deposits", money(bankPanel.depositsGathered())),
                            statLine("Lent out", money(bankPanel.getBook())),
                            statLine("Capacity", money(bankCapacity)));
                    b.getChildren().add(panelNote(bankPanel.capitalBound()
                            ? "capital is the limit"
                            : "deposits are the limit"));
                    b.getChildren().addAll(
                            statLine("Equity", money(bankPanel.equity())),
                            statLine("Interest", money(bankPanel.getInterestEarned())),
                            statLine("Paid savers", money(bankPanel.depositInterest())),
                            statLine("Written off", money(bankPanel.getWriteOffs())),
                            statLine("Profit", money(bankPanel.getNetIncome())));
                    return b;
                }));

        /* =============================================================
           TRADE - the city's edge, in one line.

           The rate is the headline because it is the number that reprices every
           import and every export at once, and the player has no other view of
           it while the map is up. Red when the currency is weakening, which is
           the condition that makes a shop's stock dearer next month.
           ============================================================= */
        ForeignAccounts fxPanel = ui.game.getForeignAccounts();
        double fxDrift = fxPanel.deviationFromParity();
        double fxCa = fxPanel.monthlyCurrentAccount();
        body.getChildren().add(panelSection("trade", "TRADE",
                String.format("%s  \u00b7  %s", fxRate(fxPanel.getRate()),
                        fxCa < 0 ? "deficit" : "surplus"),
                fxCa < 0 ? PANEL_WARN : null,
                () -> {
                    VBox b = panelBody(
                            statLine("Rate", fxRate(fxPanel.getRate())),
                            statLine("vs parity",
                                    String.format("%+.1f%%", fxDrift * 100),
                                    // The one parity rule (0.7.38), which the watch list's THE CURRENCY, the header and Trade read too.
                                    fxPanel.isPinned() ? null
                                            : ForeignAccounts.parityLevel(fxDrift) >= 2 ? PANEL_BAD
                                            : ForeignAccounts.parityLevel(fxDrift) == 1 ? PANEL_WARN : null));
                    b.getChildren().add(panelNote(fxPanel.isPinned()
                            ? "held fixed"
                            : fxDrift > .005 ? "weaker \u2014 imports cost more"
                            : fxDrift < -.005 ? "stronger \u2014 exporters squeezed"
                            : "at parity"));
                    b.getChildren().addAll(
                            statLine("Sold abroad", money(fxPanel.getExports())),
                            statLine("Bought abroad", money(fxPanel.tradeImports())),
                            statLine("Current a/c", money(fxPanel.currentAccount()),
                                    fxPanel.currentAccount() < 0 ? PANEL_BAD : PANEL_GOOD));

                    /* =====================================================
                       THE TWO POCKETS, AND WHICH MONEY EACH IS IN.

                       Jerus, after parking borrowed dollars in reserves and
                       going looking for them: "add USD cash so that the player
                       knows". The panel had one line called Reserves, in local
                       money, and nothing anywhere said the city was holding
                       foreign currency at all - so money that plainly existed
                       had no visible home and no visible way back.

                       Both figures now, one under the other, because the local
                       one is what it is worth and the USD one is what it IS -
                       and a line saying it has to be exchanged before it can be
                       spent, because that is the question the panel raised and
                       did not answer.
                       ===================================================== */
                    b.getChildren().add(statLine("Held abroad",
                            money(fxPanel.getReserves())));
                    if (fxPanel.getReserves() > 0) {
                        b.getChildren().add(statLine("  in " + Currency.FOREIGN_CODE,
                                usd(fxPanel.getReservesUsd())));
                        /*
                         * The USD figure is the vault itself since 2026-09-21
                         * and the local one is what it fetches today; this is
                         * what the currency did to the second this month.
                         */
                        if (Math.abs(fxPanel.getLastVaultRevaluation()) > .005) {
                            b.getChildren().add(statLine("  currency did",
                                    (fxPanel.getLastVaultRevaluation() > 0 ? "+" : "−")
                                            + money(Math.abs(fxPanel.getLastVaultRevaluation())),
                                    fxPanel.getLastVaultRevaluation() > 0 ? PANEL_GOOD : null));
                        }
                        b.getChildren().add(panelNote("exchange it to spend it at home"));
                        // Where it came from, while the city is young - see
                        // Game's THE FOUNDING RESERVE. THIS city's founders'
                        // dollars (0.7.10), which the founding chose, and
                        // nothing said when they left none.
                        if (ui.game.getMonth() <= Game.FOUNDERS_NOTE_MONTHS
                                && ui.game.getFoundingReserveUsd() > 0) {
                            double cover = fxPanel.importCover();
                            b.getChildren().add(panelNote(String.format(
                                    "the founders left %s here%s", usd(ui.game.getFoundingReserveUsd()),
                                    fxPanel.monthlyImports() <= 0 ? ""
                                            : " - " + coverMonths(cover) + " of imports")));
                        }
                        if (fxPanel.monthlyImports() > 0) {
                            // Trade's words and verdict (0.7.38): ForeignAccounts.coverLevel().
                            int coverLevel = ForeignAccounts.coverLevel(fxPanel.importCover());
                            b.getChildren().add(statLine("Import cover",
                                    coverMonths(fxPanel.importCover()),
                                    coverLevel >= 2 ? PANEL_BAD : coverLevel == 1 ? PANEL_WARN : null));
                        }
                    } else {
                        b.getChildren().add(panelNote("the treasury holds no foreign money"));
                    }
                    CapitalFlows hotPanel = ui.game.getCapitalFlows();
                    if (hotPanel.getStock() > 0 || hotPanel.isStopped()) {
                        b.getChildren().add(statLine("Hot money",
                                money(hotPanel.getStock()),
                                hotPanel.isStopped() ? PANEL_BAD : null));
                        double backed = hotPanel.getStock() > 0
                                ? fxPanel.getReserves() / hotPanel.getStock() : 1;
                        b.getChildren().add(statLine("  backed",
                                String.format("%.0f%%", Math.min(999, backed * 100)),
                                backed < CapitalFlows.PANIC_BACKING ? PANEL_WARN : null));
                        if (hotPanel.isStopped()) {
                            b.getChildren().add(panelNote("it is leaving \u2014 "
                                    + hotPanel.stopMonthsLeft() + " months to run"));
                        }
                    }
                    b.getChildren().add(statLine("Trade record",
                            money(fxPanel.getCumulativeBalance()),
                            fxPanel.getCumulativeBalance() < 0 ? PANEL_WARN : PANEL_GOOD));
                    b.getChildren().add(panelNote("since founding; a record, not a stock"));
                    /*
                     * POSITIVE PRESSURE IS A CURRENCY ABOUT TO WEAKEN, because
                     * pressure() is depreciation pressure and carries the minus
                     * sign off the current account inside it. Written as
                     * `< -.25` first, which lit the warning on a city running a
                     * surplus. Shown with the word rather than the sign.
                     */
                    double press = fxPanel.getLastPressure();
                    if (fxPanel.getForeignDebt() > 0) {
                        b.getChildren().add(statLine("Owed in " + Currency.FOREIGN_CODE,
                                usd(fxPanel.getForeignDebtUsd()), PANEL_BAD));
                        b.getChildren().add(statLine("  costing",
                                money(fxPanel.getForeignDebt()), PANEL_BAD));
                        if (Math.abs(fxPanel.getLastRevaluation()) > .005) {
                            b.getChildren().add(statLine("Currency did",
                                    money(-fxPanel.getLastRevaluation()),
                                    fxPanel.getLastRevaluation() > 0 ? PANEL_BAD : PANEL_GOOD));
                        }
                        b.getChildren().add(statLine("Net position",
                                money(fxPanel.netForeignPosition()),
                                fxPanel.netForeignPosition() < 0 ? PANEL_BAD : null));
                    }
                    b.getChildren().add(statLine("Pressure",
                            String.format("%.0f%% %s", Math.abs(press) * 100,
                                    press > 0 ? "weaker" : press < 0 ? "stronger" : ""),
                            press > .25 ? PANEL_WARN : null));
                    return b;
                }));

        /* ================= TAX ================= */
        double businessTax = economy.getBusinessTax();
        double industrialTax = economy.getIndustrialTax();
        double salesTax = economy.getSalesTax();
        double wageTax = economy.getWageTax();
        double taxTotal = businessTax + industrialTax + salesTax + wageTax;
        // One income rate while the three are one; each once they have parted (0.7.4).
        TaxPolicy rates = economy.getTaxPolicy();
        String atRates = rates.incomeRatesSplit()
                ? "profit " + formatter.format(rates.getProfitTaxRate() * 100)
                        + "% · sales " + formatter.format(rates.getSalesTaxRate() * 100)
                        + "% · wage " + formatter.format(rates.getWageTaxRate() * 100) + "%"
                : "at " + formatter.format(economy.getTaxRate() * 100) + "%";

        body.getChildren().add(panelSection("tax", "TAX", money(taxTotal), null,
                () -> panelBody(
                        panelNote(atRates),
                        statLine("Business", money(businessTax)),
                        statLine("Industrial", money(industrialTax)),
                        statLine("Sales", money(salesTax)),
                        statLine("Wage", money(wageTax)))));

        /* =============================================================
           LABOUR - and this is the one Jerus asked for by name.

           "clicking on fill rate shows the fill rate for each". Closed it is a
           single percentage, which is the honest answer most months. Open, it
           is the skill ladder: who the city has, what it has posts for, and
           what it is paying to get them - which is the only view in which an
           unfilled post has a reason rather than just a number.
           ============================================================= */
        int workforce = people.getWorkforce();
        int totalJobs = people.getTotalJobs();
        int[] vacancies = people.getJobVacancy();
        int open = 0;
        for (int v : vacancies) open += v;
        final int totalVacancies = open;
        double fill = totalJobs > 0 ? (double) (totalJobs - totalVacancies) / totalJobs : 1;

        body.getChildren().add(panelSection("labour", "LABOUR",
                totalJobs > 0 ? String.format("%.0f%% filled", fill * 100) : "no jobs",
                fill < .75 ? PANEL_BAD : fill < .95 ? PANEL_WARN : null,
                () -> {
                    VBox b = panelBody(
                            statLine("Workforce", String.format("%,d", workforce)),
                            statLine("Posts", String.format("%,d", totalJobs)),
                            statLine("Unfilled", String.format("%,d", totalVacancies),
                                    totalVacancies > 0 ? PANEL_WARN : null),
                            statLine("Unemployed",
                                    String.format("%,d  (%.0f%%)", people.getUnemployed(),
                                            people.getUnemploymentRate() * 100),
                                    people.getUnemploymentRate() > .15 ? PANEL_WARN : null));

                    b.getChildren().add(panelNote("by skill — workers / open posts / pay"));

                    double[] own = people.workforceByBand();
                    // Staffable, and read off an ungated job. This walked the
                    // job list and took the FIRST match, which for the
                    // university band is the doctor - so a hospital shortage
                    // painted the whole graduate row red. bandPremium() exists
                    // for exactly that and this had not been switched to it.
                    double[] posts = people.staffablePostsByBand();
                    for (WageBand band : WageBand.values()) {
                        int i = band.ordinal();
                        double premium = market.bandPremium(band);
                        b.getChildren().add(statLine(band.label(),
                                String.format("%s/%s  %.2fx",
                                        shortNumber(own[i]), shortNumber(posts[i]), premium),
                                premium > 1.05 ? PANEL_BAD
                                        : own[i] > posts[i] * 1.2 ? PANEL_WARN : null));
                    }
                    b.getChildren().add(statLine("Min wage",
                            money(market.cashMinimumWage())));

                    /*
                     * WHERE THEY CAME FROM, because otherwise it reads as a bug.
                     *
                     * Jerus: "i had no schools, yet there was an over supply of
                     * skilled and above workers". They were right to ask - the
                     * panel showed graduates in a city that had never taught
                     * anybody, and nothing on it said those people had moved
                     * here. A number with no visible cause is indistinguishable
                     * from a number that is wrong.
                     */
                    double[] built = buildings.getBuiltEducationPlaces();
                    double seats = 0;
                    for (double p : built) seats += p;
                    double trained = 0;
                    for (int i = 1; i < own.length; i++) trained += own[i];

                    if (seats <= 0 && trained > 0) {
                        b.getChildren().add(panelNote(
                                "no schools - every trained worker here moved in,"
                                + " and only as many as the jobs attract"));
                    }
                    /*
                     * The other half of the same question, and the one the
                     * arrival rules make askable: the unskilled band is mostly
                     * not an import. Nobody moves here without a diploma at the
                     * going rate (0.7.18: only for a dear unskilled wage), so a
                     * big No-diploma row is mostly this city's own children,
                     * and a school problem rather than a migration one.
                     */
                    double unskilled = own[WageBand.NONE.ordinal()];
                    double banded = 0;
                    for (double v : own) banded += v;
                    if (banded > 0 && unskilled / banded > .2) {
                        b.getChildren().add(panelNote(String.format(
                                "%.0f%% have no diploma — only a dear unskilled wage"
                                + " brings them in, so most are children the schools missed",
                                unskilled / banded * 100)));
                    }
                    return b;
                }));

        /* ================= SCHOOLS ================= */
        Education schools = ui.game.getEducation();

        // Served (0.7.41), in the one verdict - "taught" on lines of its own (.5, .9) until then.
        body.getChildren().add(panelSection("school", "SCHOOLS",
                CityNeeds.servedPct(schools.basicCoverage()) + " " + CityNeeds.SERVED,
                BuildScreen.servedTone(CityNeeds.verdict(CityNeeds.Kind.BASIC_SCHOOLS, CareType.NONE,
                        schools.basicCoverage())),
                () -> {
                    VBox b = panelBody();
                    for (EducationType type : EducationType.values()) {
                        if (type == EducationType.NONE) continue;
                        double seats = schools.getEnrolled(type);
                        if (seats <= 0 && !type.isBasic()) continue;
                        // A stage reads as served, coloured while it is not enough (0.7.41; amber under .9 until then).
                        CityNeeds.Served stage = CityNeeds.verdict(CityNeeds.Kind.BASIC_SCHOOLS, CareType.NONE,
                                schools.getCoverage(type));
                        b.getChildren().add(statLine(type.getLabel(),
                                type.isBasic()
                                        ? CityNeeds.servedPct(stage.share()) + " " + CityNeeds.SERVED
                                                + "  " + shortNumber(seats)
                                        : shortNumber(seats) + " studying",
                                type.isBasic() && stage.level() > 0 ? BuildScreen.servedTone(stage) : null));
                    }
                    // The bottleneck names a building, which is the only
                    // actionable thing on this section.
                    if (schools.basicCoverage() < .95) {
                        b.getChildren().add(panelNote("short of "
                                + schools.basicBottleneck().getLabel().toLowerCase()
                                + " places"));
                    }
                    b.getChildren().addAll(
                            statLine("Tuition paid", String.format("%.0f%% by city",
                                    schools.getTuitionSubsidy() * 100)),
                            statLine("School bill", money(schools.getNetCost())));
                    return b;
                }));

        /* ================= PEOPLE ================= */
        body.getChildren().add(panelSection("people", "PEOPLE",
                String.format("%+,.0f/mo", ui.game.getMigration().getLastNet()
                        + pyramid.getLastBirths() - pyramid.getLastDeaths()),
                null,
                () -> panelBody(
                        statLine("Born", formatter.format(pyramid.getLastBirths())),
                        statLine("Died", formatter.format(pyramid.getLastDeaths())),
                        statLine("Moved in", formatter.format(
                                ui.game.getMigration().getLastArrivals())),
                        statLine("Moved out", formatter.format(
                                ui.game.getMigration().getLastDepartures())),
                        statLine("Housing",
                                String.format("%,d/%,d", population,
                                        ui.game.getHouseholdCapacity()),
                                population > ui.game.getHouseholdCapacity()
                                        ? PANEL_WARN : null))));

        /* ================= HEALTH ================= */
        double[] staffing = people.getJobFillRate();

        body.getChildren().add(panelSection("health", "HEALTH",
                String.format("%.0f%% sick", health.getSickRate() * 100),
                health.getSickRate() > Health.WELL_SERVED_RATE * 2 ? PANEL_BAD
                        : health.getSickRate() > Health.WELL_SERVED_RATE * 1.5 ? PANEL_WARN
                        : PANEL_GOOD,
                () -> {
                    VBox b = panelBody(
                            careLine("General care", CareType.GENERAL,
                                    pyramid.total(), staffing),
                            careLine("Childcare", CareType.CHILDCARE,
                                    CareType.CHILDCARE.populationServed(pyramid), staffing),
                            careLine("Senior care", CareType.SENIOR,
                                    CareType.SENIOR.populationServed(pyramid), staffing),
                            statLine("Death care", service.getStatus(),
                                    service.isOverwhelmed() ? PANEL_BAD
                                            : service.isStrained() ? PANEL_WARN : null));
                    if (service.getUnburied() <= 0) {
                        b.getChildren().add(statLine("Plots left", formatter.format(
                                Healthcare.plotsRemaining(
                                        buildings.getCareCapacity(CareType.BURIAL),
                                        service.getPlotsUsed()))));
                    }
                    b.getChildren().add(statLine("Health bill", money(service.getNetCost())));
                    return b;
                }));

        /* ================= SAFETY (2026-09-11) ================= */
        Crime crime = ui.game.getCrime();
        double crimeVs = crime.getRateVsCanada();
        body.getChildren().add(panelSection("safety", "SAFETY",
                String.format("%.1fx crime", crimeVs),
                crimeVs > 1.5 ? PANEL_BAD : crimeVs > 1 ? PANEL_WARN : PANEL_GOOD,
                () -> {
                    VBox b = panelBody(
                            statLine("Crime /100k/yr", formatter.format(Math.round(crime.getRatePer100k()))),
                            statLine("Police cover", String.format("%.0f%%", crime.getCoverage() * 100),
                                    crime.getCoverage() < .25 ? PANEL_BAD
                                            : crime.getCoverage() < .5 ? PANEL_WARN : null),
                            statLine("In prison", formatter.format(Math.round(crime.prisoners()))),
                            statLine("Not held", formatter.format(Math.round(crime.getNotHeld())),
                                    crime.getNotHeld() >= .5 ? PANEL_WARN : null),
                            statLine("Killed", String.format("%.1f", crime.getKilled())),
                            statLine("Stolen", money(crime.getStolen())));
                    // The biggest reason, named: the only thing that removes it.
                    Crime.Cause top = null;
                    for (Crime.Cause c : Crime.Cause.values()) {
                        if (c == Crime.Cause.NO_CAUSE) continue;
                        if (top == null || crime.getCrimes(c) > crime.getCrimes(top)) top = c;
                    }
                    if (top != null && crime.getCrimes(top) > 0) {
                        b.getChildren().add(panelNote("most of it: " + top.label().toLowerCase()));
                    }
                    b.getChildren().add(statLine("Safety bill", money(crime.getGrossCost())));
                    return b;
                }));

        /* ================= RESOURCES ================= */
        // How many of the three are not enough, by the one verdict (0.7.41): "1 short · 1 tight", in the
        // worst one's colour. It counted the loads past .75 and the road while busy, but not congested.
        int shortOnes = 0, tightOnes = 0, worstLevel = 0;
        for (CityNeeds.Served s : new CityNeeds.Served[] {
                CityNeeds.verdict(CityNeeds.Kind.POWER, CareType.NONE, utilities.getPowerServed()),
                CityNeeds.verdict(CityNeeds.Kind.WATER, CareType.NONE, utilities.getWaterServed()),
                CityNeeds.verdict(CityNeeds.Kind.ROADS, CareType.NONE, ui.game.getInfrastructureManager().getServed())}) {
            if (s.level() <= 0) continue;
            if (CityNeeds.SHORT.equals(s.word())) shortOnes++; else tightOnes++;
            worstLevel = Math.max(worstLevel, s.level());
        }
        String notEnough = (shortOnes > 0 ? shortOnes + " " + CityNeeds.SHORT : "")
                + (shortOnes > 0 && tightOnes > 0 ? " · " : "") + (tightOnes > 0 ? tightOnes + " " + CityNeeds.TIGHT : "");

        body.getChildren().add(panelSection("res", "RESOURCES",
                notEnough.isEmpty() ? "all clear" : notEnough,
                BuildScreen.verdict(worstLevel),
                () -> panelBody(
                        utilityLine("Energy", utilities.getPowerServed(),
                                utilities.getConsumption(), utilities.getProduction()),
                        utilityLine("Water", utilities.getWaterServed(),
                                utilities.getWaterConsumption(), utilities.getWaterProduction()),
                        roadLine(),
                        statLine("Materials", String.format("%,d",
                                ui.game.getConstructionMaterials())),
                        statLine("Store stock", String.format("%,d",
                                ui.game.getSectors().retail().getStoreInventory())),
                        statLine("Food stock", String.format("%,.0f",
                                ui.game.getSectors().industry().getStock(Good.BREAD))))));

        /* ================= LAND ================= */
        /*
         * THE PRICE ON THE COLLAPSED HEADER, not only inside.
         *
         * The price is what the player is actually deciding against - every
         * building's all-in cost is its cash plus its footprint at this
         * figure, and the three road types are costed so that which one wins
         * depends on it. A number that decides every purchase should not
         * need a click.
         *
         * THE GROUND FREE BESIDE IT, COLOURED AS NEEDS YOU COLOURS IT
         * (0.7.26). It read "95% used" in red from 95%, and a city buys
         * ground as it needs it, so the share used sits at 95-100% for
         * centuries with nothing wrong (GROUND USED, citySymptoms()): the
         * playtest's 2,400-month city was over 95% in 120 months of 120 with
         * no sector waiting. The colour is the GROUND row's
         * (CityNeeds.ground()), which the land office's GROUND FREE and
         * Build's LAND FREE read too, so the three and NEEDS YOU agree; the
         * share used is a line inside, in no colour. The price is the city's
         * money (Money.unitPrice()); it printed a bare "$".
         */
        CityNeeds.Need ground = CityNeeds.ground(ui.game, WORDS);
        String perSqFt = marked(ui.game.getCurrency().qualifiedSymbol(), unitPrice(land.getPricePerSqFt()));
        body.getChildren().add(panelSection("land", "LAND",
                shortNumber(land.getAvailableSqFt()) + " free  ·  " + perSqFt + "/sq ft",
                ground.level() >= 2 ? PANEL_BAD : ground.level() == 1 ? PANEL_WARN : null,
                () -> panelBody(
                        statLine("Owned", LandManager.km2Words(land.getOwnedSqFt())),
                        statLine("Free", LandManager.km2Words(land.getAvailableSqFt())),
                        statLine("Used", String.format("%.0f%%", land.getUtilisation() * 100)),
                        statLine("Price/sq ft", perSqFt))));

        /* ================= SECTOR CASH ================= */
        double sectorCash = ui.game.getSectors().totalCash();
        body.getChildren().add(panelSection("sectors", "SECTORS", money(sectorCash), null,
                () -> {
                    VBox b = panelBody();
                    for (Sector s : ui.game.getSectors().all()) {
                        b.getChildren().add(statLine(s.label(), money(s.getCash())));
                    }
                    b.getChildren().add(statLine("Utilities", money(economy.getUtilityIncome())));
                    return b;
                }));

        /* =============================================================
           BUILDINGS, and this is where the folding pays for itself.

           A grown city owns thirty kinds of building, which was thirty rows of
           the panel - nearly half of it - describing something that changes
           once every few months and can be read in full on the build screens.
           ============================================================= */
        int kinds = 0, structures = 0;
        for (int i = 0; i < buildings.getTemplateCount(); i++) {
            int q = buildings.getQuantity(i);
            if (q > 0) { kinds++; structures += q; }
        }
        final int builtKinds = kinds;
        final int builtTotal = structures;

        body.getChildren().add(panelSection("built", "BUILDINGS",
                builtTotal == 0 ? "none" : String.format("%,d", builtTotal), null,
                () -> {
                    VBox b = panelBody();
                    if (builtTotal == 0) {
                        b.getChildren().add(panelNote("Nothing built yet."));
                        return b;
                    }
                    b.getChildren().add(panelNote(builtKinds + " kinds standing"));
                    for (int i = 0; i < buildings.getTemplateCount(); i++) {
                        BuildingsTemplate template = buildings.getTemplate(i);
                        if (template == null) continue;
                        int quantity = buildings.getQuantity(i);
                        if (quantity > 0) {
                            b.getChildren().add(statLine(shorten(template.getName()),
                                    String.format("%,d", quantity)));
                        }
                    }
                    return b;
                }));
    }

    void refreshCityPanel() {
        ui.cityPanel.getChildren().clear();

        EconomyManager economy = ui.game.getEconomyManager();
        PopulationManager people = ui.game.getPopulationManager();
        BuildingManager buildings = ui.game.getBuildingManager();
        UtilitiesHandler utilities = ui.game.getServicesManager().getUtilitiesHandler();
        LabourMarket market = ui.game.getLabourMarket();
        Health health = ui.game.getHealth();
        Healthcare service = ui.game.getHealthcare();
        LandManager land = ui.game.getLandManager();
        PopulationCohorts pyramid = ui.game.getCohorts();

        int population = people.getPopulation();
        double annualGdp = economy.getYearGdp();
        double debt = ui.game.getDebtManager().getAllPrincipal();

        Label title = new Label("CITY OVERVIEW");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;"
                + " -fx-text-fill: " + Palette.TEXT_HEAD + "; -fx-padding: 0 0 1 2;");

        Label subtitle = new Label(CityCalendar.format(ui.game.getMonth())
                + "   ·   month " + ui.game.getMonth());
        subtitle.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 9px;"
                + " -fx-text-fill: " + Palette.TEXT_MUTED + "; -fx-padding: 0 0 6 2;");

        VBox body = new VBox(0);
        // The ScrollPane's viewport paints its own ground, and on a dark panel
        // an unpainted one shows through as a white sliver down the side of
        // every section. Cheaper to state it than to fight the skin.
        body.setStyle("-fx-background-color: " + Palette.PANEL + ";");

        /* =============================================================
           THE VITALS, which are never folded away.

           Three figures and nothing else. If the panel showed only these it
           would still be worth having, and that is the test for what belongs
           here: cash says whether the city can act, income says which way it
           is going, and population is the score.
           ============================================================= */
        double cash = ui.game.getCash();
        double income = ui.game.getIncome();

        /*
         * AND THEY ARE NOT IN THE SCROLLER ANY MORE.
         *
         * Jerus: "the vitals i think should purely be their own thing."
         *
         * They were the first three rows of a column forty rows long, which
         * meant that scrolling down to the buildings list scrolled the city's
         * three most important figures off the top of the panel - and the
         * panel's whole job is to be the thing you can read while looking at
         * something else. Pinned above the scroller they are always on screen,
         * and the sections below them can be as long as they like.
         */
        VBox vitals = new VBox(0);
        vitals.getChildren().addAll(
                statLine("Cash", money(cash), cash < 0 ? PANEL_BAD : null),
                statLine("Earned", money(income), income < 0 ? PANEL_BAD : PANEL_GOOD),
                statLine("Population", String.format("%,d", population)));
        vitals.setStyle("-fx-padding: 6 4 6 2; -fx-background-color: " + Palette.PINNED + ";"
                + " -fx-background-radius: 4;");
        VBox.setMargin(vitals, new javafx.geometry.Insets(0, 0, 8, 0));

        /* =============================================================
           AND WHATEVER IS ACTUALLY WRONG.

           An alert earns its place by being ABSENT most of the time. These are
           the six conditions that quietly cost the city output or people, each
           of which used to be a row indistinguishable from the forty around it -
           an outbreak read exactly like the store stock.
           ============================================================= */
        VBox alerts = new VBox(0);

        if (health.isOutbreak()) {
            alerts.getChildren().add(statLine("OUTBREAK",
                    String.format("month %d",
                            Math.max(1, ui.game.getMonth() - health.getOutbreakStarted() + 1)),
                    PANEL_BAD));
        }
        if (service.getUnburied() > 0) {
            alerts.getChildren().add(statLine("Unburied",
                    formatter.format(service.getUnburied()), PANEL_BAD));
        }
        if (ui.game.getInfrastructureManager().isCongested()) {
            alerts.getChildren().add(roadLine());
        }
        // Under 100% served (0.7.41: it read "over capacity").
        if (utilities.getProduction() > 0
                && utilities.getConsumption() > utilities.getProduction()) {
            alerts.getChildren().add(statLine("Power", CityNeeds.servedPct(utilities.getPowerServed()) + " " + CityNeeds.SERVED, PANEL_BAD));
        }
        if (utilities.getWaterProduction() > 0
                && utilities.getWaterConsumption() > utilities.getWaterProduction()) {
            alerts.getChildren().add(statLine("Water", CityNeeds.servedPct(utilities.getWaterServed()) + " " + CityNeeds.SERVED, PANEL_BAD));
        }
        // NOBODY CAN BREAK GROUND (0.7.26): NEEDS YOU's GROUND row at red, none
        // free. It was "95% used", which a city sits at for centuries with
        // nothing wrong - see LAND in the dashboard's sections.
        if (CityNeeds.ground(ui.game, WORDS).level() >= 2) {
            alerts.getChildren().add(statLine("Land", "none free", PANEL_BAD));
        }

        if (!alerts.getChildren().isEmpty()) {
            alerts.setStyle("-fx-padding: 4 2 4 4; -fx-background-color: " + Palette.ALERT_GROUND + ";"
                    + " -fx-background-radius: 3; -fx-border-color: " + Palette.ALERT_EDGE + ";"
                    + " -fx-border-width: 0 0 0 2;");
            VBox spacer = new VBox(alerts);
            spacer.setStyle("-fx-padding: 6 0 2 0;");
            body.getChildren().add(spacer);
        }

        if (dashboardShown()) {
            body.getChildren().add(panelFoldAll());
            panelDashboardSections(body);
        } else {
            panelSummaryRows(body);
        }

        javafx.scene.control.ScrollPane scroller = ui.keptPanelScroller("city", body);
        scroller.setFitToWidth(true);
        scroller.setPrefHeight(700);
        // The drawer's full height (0.7.24): it is as tall as the stage it opens over.
        VBox.setVgrow(scroller, Priority.ALWAYS);
        scroller.setStyle("-fx-background-color:transparent; -fx-background:transparent;");

        ui.cityPanel.getChildren().addAll(title, subtitle, panelModeSwitch(),
                vitals, scroller);
    }

    /**
     * One coverage row: the percentage, and the two numbers behind it.
     *
     * STAFFED, not built, which is the whole reason it is worth a line - a
     * hospital with no doctors is on the BUILDINGS list looking like an asset
     * while treating nobody, and this is the row that says so. The percentage
     * is the model's own, Healthcare.getCoverage() (2026-09-19): the places
     * over the people, less whoever the fee turned away, so on a dear month
     * it reads below the two numbers beside it, which is the point. Read as
     * served since 0.7.41, coloured by the one verdict on it (by lines of its
     * own, .5 and .9, until then) - and plain while it is enough.
     */
    HBox careLine(String label, CareType care, double needed, double[] staffing) {

        double places = ui.game.getBuildingManager().getStaffedCareCapacity(care, staffing);
        double cover = ui.game.getHealthcare().getCoverage(care);
        CityNeeds.Served s = CityNeeds.verdict(CityNeeds.Kind.CARE, care, cover);

        HBox row = statLine(label, CityNeeds.servedPct(cover) + " " + CityNeeds.SERVED + "  "
                        + shortNumber(places) + "/" + shortNumber(needed),
                s.level() > 0 ? BuildScreen.servedTone(s) : null);
        ((Label) row.getChildren().get(2)).setMinWidth(Region.USE_PREF_SIZE);
        return row;
    }

    /** Keeps building names inside the panel's fixed-width column. */
    String shorten(String name) {
        return (name.length() <= 13) ? name : name.substring(0, 12) + ".";
    }
}
