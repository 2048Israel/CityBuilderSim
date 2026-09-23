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
 * folded sections - the problem list that decides what goes red, and every
 * row's own reading of the city, seats against who would come.
 *
 * Split out of UserInterface on 2026-09-18: the six banners CITY OVERVIEW
 * PANEL, THE LEFT PANEL, SUMMARY, OR DASHBOARD, HEADROOM, NOT SATISFACTION,
 * THE SUMMARY IS A PROBLEM LIST NOW and SEATS AGAINST WHO WOULD COME exactly
 * as they were, the shell's members reached through ui. The shell owns the
 * panel itself (cityPanel) and its scroller, and calls refreshCityPanel() on
 * the clock; this class owns what is drawn into it, and which sections the
 * player has opened (panelOpen).
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

       So the panel shows the other side of the same fraction: how much of what
       the city can generate is being drawn. That number climbs steadily and
       visibly, which is what makes it a warning rather than an obituary.

       Past 100% it keeps counting rather than clamping, because how far over
       you are is exactly what you need to know while fixing it - 118% and 190%
       want very different responses, and both would read as "0% spare".
       ===================================================================== */

    /** Amber from three-quarters, red once there is no headroom left. */
    HBox utilityLine(String label, double consumption, double production) {

        if (production <= 0) {
            // No plant at all. Not a shortage until something actually draws.
            return statLine(label, consumption > 0 ? "no supply" : "-",
                    consumption > 0 ? PANEL_BAD : null);
        }

        double used = consumption / production;

        String colour = (used >= 1.0) ? PANEL_BAD
                      : (used >= .75) ? PANEL_WARN
                                      : PANEL_GOOD;
        return statLine(label, formatter.format(used * 100) + "% used", colour);
    }

    /**
     * Roads, red once traffic is actually being held up.
     *
     * The two thresholds Jerus asked for - "above 90%" and "restricting flow" -
     * turn out to be the SAME line: InfrastructureManager.FREE_FLOW is 0.9, so
     * throughput starts falling at exactly 90% utilisation. Written against
     * isCongested() rather than a typed-in .90 so that stays true if FREE_FLOW
     * is ever retuned; hardcoding the number here is the same mistake the debt
     * band assertions kept making.
     *
     * STRAINED (0.85) gives the amber step, which is the useful one: it is the
     * last point at which building more roads is cheaper than the congestion.
     */
    HBox roadLine() {

        InfrastructureManager roads = ui.game.getInfrastructureManager();
        return statLine("Roads", roadSummary(),
                roads.isCongested() ? PANEL_BAD
                        : roads.isStrained() ? PANEL_WARN : PANEL_GOOD);
    }

    /**
     * Roads on the city overview, in one cell.
     *
     * Shows utilisation while there is room and throughput once there is not,
     * because those are the two different questions a player is asking: "how
     * much more can I build" until it jams, and "how much is this costing me"
     * after.
     */
    String roadSummary() {
        InfrastructureManager roads = ui.game.getInfrastructureManager();
        if (roads.isCongested()) {
            return formatter.format(roads.getThroughputRatio() * 100) + "% flow";
        }
        return formatter.format(roads.getUtilisation() * 100) + "% used";
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

    static final String PANEL_LABEL = "#78909c";
    static final String PANEL_VALUE = "#eceff1";
    static final String PANEL_GOOD  = "#5fd68a";
    static final String PANEL_WARN  = "#ffb454";
    static final String PANEL_BAD   = "#ff6b6b";

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
        figure.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;"
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
                + (open ? "#5cb8ff" : "#b0bec5") + ";");

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label figure = new Label(summary == null ? "" : summary);
        figure.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;"
                + " -fx-font-weight: bold; -fx-text-fill: "
                + (tone == null ? PANEL_VALUE : tone) + ";");

        HBox header = new HBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setStyle("-fx-padding: 4 2 4 0; -fx-cursor: hand;"
                + (open ? " -fx-background-color: #26343b; -fx-background-radius: 3;" : ""));
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
            body.setStyle("-fx-padding: 2 0 6 8; -fx-border-color: #37474f;"
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
       saying something - the bank's premium and how far the currency has run -
       because both are quiet taxes on everything the city does and neither is
       an emergency, so the red alert block above will never carry them. A
       player who lives in Summary would otherwise never learn that every loan
       in the city got dearer.
       ===================================================================== */

    HBox panelModeSwitch() {

        HBox row = new HBox(4);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 0 0 8 2;");
        row.getChildren().addAll(
                panelModeChip("Summary",   !ui.prefs.isPanelDashboard(), false),
                panelModeChip("Dashboard",  ui.prefs.isPanelDashboard(), true));
        return row;
    }

    Label panelModeChip(String text, boolean on, boolean dashboard) {

        Label chip = new Label(text);
        chip.setStyle("-fx-font-size: 9px; -fx-padding: 2 8 3 8; -fx-cursor: hand;"
                + " -fx-background-radius: 3;"
                + " -fx-background-color: " + (on ? "#26343b" : "transparent") + ";"
                + " -fx-border-color: " + (on ? "#5cb8ff" : "transparent") + ";"
                + " -fx-border-width: 0 0 2 0;"
                + " -fx-text-fill: " + (on ? "#eceff1" : PANEL_LABEL) + ";");
        chip.setOnMouseClicked(e -> {
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
                + " -fx-text-fill: #8fa3b0;");

        Label figure = new Label(value);
        figure.setWrapText(true);
        figure.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;"
                + " -fx-font-weight: bold; -fx-text-fill: "
                + (tone == null ? PANEL_VALUE : tone) + ";");

        VBox row = new VBox(-1, name, figure);
        row.setMaxWidth(Double.MAX_VALUE);
        String rest = "-fx-padding: 4 2 5 4; -fx-cursor: hand;";
        row.setStyle(rest);
        row.setOnMouseClicked(e -> go.run());
        row.setOnMouseEntered(e -> row.setStyle(rest
                + " -fx-background-color: #26343b; -fx-background-radius: 3;"));
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
        link.setStyle("-fx-font-size: 9px; -fx-text-fill: #5cb8ff; -fx-cursor: hand;"
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
        note.setStyle("-fx-font-size: 9px; -fx-text-fill: #8fa3b0; -fx-padding: 3 0 1 4;");
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
       a city at 90% of its water is not. They are all in watchAll() in one
       block, in the order they are read, so they can be tuned in one place.
       ===================================================================== */

    /**
     * One thing being watched.
     *
     * @param level 0 fine, 1 near the line, 2 past it
     * @param near  how close to the yellow line, 0 to 1, and only meaningful at
     *              level 0 - it is what picks the "next to watch" line on a
     *              city with nothing wrong.
     */
    record Watch(String label, String reading, int level, double near, Runnable go) { }

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

    /** A thing that is simply true or not. */
    void flag(java.util.List<Watch> out, String label, String reading,
                      boolean bad, boolean severe, Runnable go) {
        out.add(new Watch(label, reading, bad ? (severe ? 2 : 1) : 0, bad ? 1 : 0, go));
    }

    /* =====================================================================
       SEATS AGAINST WHO WOULD COME.

       Jerus: "for university, college and all that above, how do i know? since
       there might be 400 seats, 380 students, but if i build another uni there
       is 800 seats and it jumps to 700 students, not full but technically it
       was."

       Exactly right, and coverage cannot answer it. For the schools above the
       basic ladder the model sets
       coverage = seats / everybody ELIGIBLE - every worker in the band who
       could in principle study - so a university in a city of four thousand
       graduates reads 10% covered whether or not a single one of them wants to
       go. And the student body is bounded by the seats, so a full school always
       looks full whatever the queue behind it.

       THE MODEL ALREADY KNOWS THE ANSWER. Intake is
       min(free seats, eligible x willing x ENROLMENT_RATE): the second half of
       that min is demand with no reference to the building. Multiply it by the
       length of the course and you have the student body this city would
       sustain if seats were free - which is precisely the question "if I put up
       another one, will it fill?"

       So the row is TWO numbers, not a percentage: what the schools hold, and
       what would come. Bigger second number means another building fills.
       Smaller means the gate is not the seats - it is what a degree returns or
       what it costs, and both of those are dials rather than buildings.

       ONE ROW PER SCHOOL, because each is a different building and "higher
       education is short" does not tell anybody what to build. In practice one
       or two bind at a time.
       ===================================================================== */
    void seatsWanted(java.util.List<Watch> out) {

        Education schools = ui.game.getEducation();
        PopulationManager pm = ui.game.getPopulationManager();
        LabourMarket market = ui.game.getLabourMarket();
        double[] seatsBy = ui.game.getBuildingManager()
                .getStaffedEducationPlaces(pm.getJobFillRate());

        for (EducationType type : EducationType.values()) {
            if (type == EducationType.NONE || type.isBasic()) continue;

            double wanted = schools.eligibleFor(type, pm)
                    * schools.willingShare(type, market) * Education.ENROLMENT_RATE;
            double couldHold = wanted * type.months();

            /*
             * A CLASS'S WORTH, or it is not a building.
             *
             * MEASURED: a city of 1,650 with no university has 372 people who
             * would attend one - a real row. It also has six who would read
             * medicine and three who would read law, and a red row saying "0
             * seats, 3 would come" is asking the player to put up a law school
             * for three students. The floor is what separates a shortage from a
             * rounding error; below it a city is not failing to teach anybody,
             * it is simply too small to have one yet.
             */
            if (couldHold < 25) continue;

            double seats = seatsBy[type.ordinal()];
            over(out, type.getLabel().toUpperCase(),
                    String.format("%s seats, %s would come",
                            people(seats), people(couldHold)),
                    couldHold / Math.max(seats, 1), 1.05, 2,
                    () -> ui.buildScreen.handleAllBuildingMenus("Education",
                            EnumSet.of(BuildingType.EDUCATION)));
        }
    }

    /**
     * One network: how much of its capacity is spoken for, and whether it is
     * still meeting demand.
     *
     * @param ratio min(supply/demand, 1) - under one the city is being
     *              throttled, and the load figure has stopped being the news.
     */
    void network(java.util.List<Watch> out, String label,
                         double demand, double supply, double ratio) {
        if (supply <= 0) {
            /*
             * Nothing built. A city drawing nothing is not short of anything -
             * a ratio of nothing over nothing reads 0.00 for power and 1.00 for
             * water, and watching that opened every new city on a red POWER
             * row. A city drawing something with no plant is very short indeed.
             */
            if (demand > 0) {
                flag(out, label, "nothing supplying it", true, true,
                        () -> ui.buildScreen.handleAllBuildingMenus("Utilities", ui.buildScreen.utilityTypes()));
            }
            return;
        }
        double load = demand / supply;
        over(out, label, ratio < .99
                        ? String.format("only %.0f%% supplied", ratio * 100)
                        : String.format("%.0f%% of capacity", load * 100),
                load, .75, 1,
                () -> ui.buildScreen.handleAllBuildingMenus("Utilities", ui.buildScreen.utilityTypes()));
    }

    /**
     * Everything with a lever, measured against its own line.
     *
     * Returns the whole set INCLUDING the ones that are fine, because the
     * renderer needs the fine ones to answer "what is closest" on a healthy
     * city. It filters; this only measures.
     */
    java.util.List<Watch> watchAll() {

        java.util.List<Watch> out = new java.util.ArrayList<>();

        EconomyManager economy = ui.game.getEconomyManager();
        UtilitiesHandler utilities = ui.game.getServicesManager().getUtilitiesHandler();
        InfrastructureManager roads = ui.game.getInfrastructureManager();
        Healthcare care = ui.game.getHealthcare();
        Education schools = ui.game.getEducation();
        Crime crime = ui.game.getCrime();
        FamilyModel families = ui.game.getFamilies();
        LandManager land = ui.game.getLandManager();
        Bank bank = ui.game.getBank();
        PopulationCohorts cohorts = ui.game.getCohorts();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();
        int population = ui.game.getPopulationManager().getPopulation();

        /* ---------------------------- the networks ---------------------------- */
        // A ratio under one is output being throttled somewhere in the city this
        // month, so the yellow line sits just under one rather than at .85.
        /*
         * HOW FULL, NOT HOW SHORT - and that is the correction.
         *
         * getEnergyRatio() and getWaterRatio() are min(supply/demand, 1), so
         * they sit at exactly 1.00 right up until the city is already being
         * throttled. Watching them meant the row could only appear once the
         * lights were out, which is the opposite of this panel's rule. Jerus:
         * "water doesnt appear, it appears on the top, it should appear in the
         * summary same as the others" - the dashboard's RESOURCES line has
         * always counted a network "tight" at 75% of capacity, and it was the
         * only thing in the game saying so.
         *
         * So the reading is the LOAD, on the same .75 that line uses, and it
         * goes red when the ratio finally breaks - at which point the reading
         * says what is actually being delivered instead.
         */
        network(out, "POWER", utilities.getConsumption(), utilities.getProduction(),
                utilities.getEnergyRatio());
        network(out, "WATER", utilities.getWaterConsumption(),
                utilities.getWaterProduction(), utilities.getWaterRatio());

        // Its own constants: STRAINED is .85 and free flow ends at .90.
        double traffic = roads.getUtilisation();
        over(out, "ROADS", String.format("%.0f%% of capacity", traffic * 100),
                traffic, InfrastructureManager.STRAINED, InfrastructureManager.FREE_FLOW,
                () -> ui.buildScreen.handleAllBuildingMenus("Infrastructure",
                        EnumSet.of(BuildingType.INFRASTRUCTURE)));

        /* ------------------------------- the care ------------------------------- */
        // General care is the one that moves the sick rate, so it is watched
        // hardest; the other two kill at the ends of life rather than in the
        // middle, and a young city legitimately has neither for a while.
        double general = ui.servicesScreen.careCover(CareType.GENERAL, cohorts, staffing);
        under(out, "GENERAL CARE", String.format("%.0f%% covered", general * 100),
                general, .80, .50,
                () -> ui.buildScreen.handleAllBuildingMenus("Healthcare", EnumSet.of(BuildingType.HEALTHCARE)));

        double childcare = ui.servicesScreen.careCover(CareType.CHILDCARE, cohorts, staffing);
        under(out, "CHILDCARE", String.format("%.0f%% covered", childcare * 100),
                childcare, .70, .40,
                () -> ui.buildScreen.handleAllBuildingMenus("Healthcare", EnumSet.of(BuildingType.HEALTHCARE)));

        double senior = ui.servicesScreen.careCover(CareType.SENIOR, cohorts, staffing);
        under(out, "SENIOR CARE", String.format("%.0f%% covered", senior * 100),
                senior, .70, .40,
                () -> ui.buildScreen.handleAllBuildingMenus("Healthcare", EnumSet.of(BuildingType.HEALTHCARE)));

        // The dead are a STOCK: a backlog does not clear itself and the plots do
        // not come back, so this one is red the moment anybody is waiting.
        double unburied = care.getUnburied();
        flag(out, "THE DEAD", unburied > 0
                        ? people(unburied) + " unburied" : "all dealt with",
                unburied > 0, unburied > 0,
                () -> ui.buildScreen.handleAllBuildingMenus("Healthcare", EnumSet.of(BuildingType.HEALTHCARE)));

        /*
         * MEASURED: with nobody dying this returns Double.MAX_VALUE - not
         * infinity, so isFinite() lets it through - and a founding city holds
         * 2,500 plots, which reads as "6500 months left" for three centuries.
         * A decade of headroom is not news; the row appears when it stops being
         * true.
         */
        double plots = ui.game.getBuildingManager().getCareCapacity(CareType.BURIAL);
        double monthsLeft = care.monthsOfPlotsLeft(plots);
        if (monthsLeft < 120) {
            under(out, "BURIAL PLOTS", String.format("%.0f months left", monthsLeft),
                    monthsLeft, 24, 6,
                    () -> ui.buildScreen.handleAllBuildingMenus("Healthcare",
                            EnumSet.of(BuildingType.HEALTHCARE)));
        }

        /* ------------------------------ the schools ------------------------------ */
        /*
         * AND IT SAYS WHICH ONE.
         *
         * basicCoverage() IS the bottleneck's coverage - it returns
         * coverage[basicBottleneck()], the minimum of the three rungs - so the
         * number was already about one specific school and the row simply did
         * not say which. Jerus: "just the schools one, it doesnt tell me
         * which". A percentage with no building attached is a percentage you
         * cannot act on, and the model has named it all along.
         */
        double basic = schools.basicCoverage();
        if (population > 0) {
            String thin = schools.basicBottleneck().getLabel().toLowerCase();
            under(out, "SCHOOLS", String.format("%s %.0f%% taught", thin, basic * 100),
                    basic, .90, .60,
                    () -> ui.buildScreen.handleAllBuildingMenus("Education",
                            EnumSet.of(BuildingType.EDUCATION)));
        }

        /* ------------------------ and the schools above them ------------------------ */
        seatsWanted(out);

        /* ------------------------------- the police ------------------------------- */
        double vsCanada = crime.getRateVsCanada();
        over(out, "CRIME", String.format("%.1fx Canada's", vsCanada),
                vsCanada, 1.2, 1.5,
                () -> ui.buildScreen.handleAllBuildingMenus("Safety", EnumSet.of(BuildingType.SAFETY)));

        double unheld = crime.getNotHeld();
        over(out, "CELLS", unheld >= 1
                        ? people(unheld) + " caught, not held" : "enough for the caught",
                unheld, 1, 25,
                () -> ui.buildScreen.handleAllBuildingMenus("Safety", EnumSet.of(BuildingType.SAFETY)));

        /* ------------------------------- the housing ------------------------------- */
        // Nobody at all with a door is the worst thing on this list: it is past
        // both squeeze valves, so the model has already tried flatshares and
        // doubling up and still has households left over.
        double unplaced = families.getStillUnplaced();
        over(out, "HOMES", unplaced >= .5
                        ? people(unplaced) + " with nowhere to live" : "everybody housed",
                unplaced, .5, 25,
                () -> ui.buildScreen.handleAllBuildingMenus("Residential",
                        EnumSet.of(BuildingType.RESIDENTIAL)));

        /* -------------------------------- the ground -------------------------------- */
        /*
         * GROUND THE CITY OWNS AND HAS NOT BUILT ON, which is the thing an
         * investor needs before it can break ground.
         *
         * NOT isPrivateInvestmentLandLocked(), and that is a measurement rather
         * than a preference. Probed on a founding city it goes true at month 12
         * - with 1,974,000 sq ft still free - and is still true at month 300.
         * Its set of blocked sectors does not appear to clear, and it carries a
         * 24-month acknowledgement snooze that only the inbox knows how to
         * press. A row that never goes away is a row nobody reads, and this
         * panel's whole promise is that solving something removes it.
         *
         * Free ground clears the moment a plot is bought and comes back when it
         * is built on, which is exactly the shape of the decision. A block is
         * 100,000 sq ft.
         */
        double free = land.getAvailableSqFt();
        under(out, "GROUND TO BUILD ON",
                free > 0 ? shortNumber(free) + " sq ft free" : "none - nobody can break ground",
                free, 100_000, 0, ui.landScreen::showLandMenu);

        flag(out, "BUILDERS", ui.game.isConstructionShedding()
                        ? "being laid off" : "in work",
                ui.game.isConstructionShedding(), false,
                () -> ui.sectorScreen.openSectorBooks(ui.game.getSectors().construction(), "Investors"));

        /* -------------------------------- the money -------------------------------- */
        double cash = ui.game.getCash();
        double spending = Math.max(1, ui.policyScreen.taxRaised());
        flag(out, "TREASURY", cash < 0 ? "overdrawn" : "in hand",
                cash < spending, cash < 0, ui.financesScreen::showFinanceMenu);

        flag(out, "THE BANK", bank.isInsolvent() ? "failed"
                        : bank.getBranches() <= 0 ? "there is none"
                        : bank.ratePremium() > 0
                                ? String.format("+%.0f pts on every rate", bank.ratePremium() * 100)
                                : "lending",
                bank.isInsolvent() || bank.getBranches() <= 0 || bank.ratePremium() > 0,
                bank.isInsolvent() || bank.getBranches() <= 0,
                ui.bankScreen::showBankMenu);

        flag(out, "BORROWING", ui.game.getDebtManager().atCeiling()
                        ? "priced out of the market" : "the market is open",
                ui.game.getDebtManager().atCeiling(), true, ui.financesScreen::showFinanceMenu);

        /* -------------------------------- the promises -------------------------------- */
        /*
         * MEASURED: the gap grows with the pensioner count in every city, and
         * against a young city's tax take it is a fifth of revenue by month
         * 300 - on a city running a comfortable surplus the whole time. An
         * unfunded promise the city is paying without noticing is not this
         * panel's business; one it cannot pay is. So it is gated on the budget,
         * and THE BUDGET below carries the deficit itself.
         */
        double gap = economy.getPensionShortfall();
        double balanceNow = economy.getNationalAccounts().getBalance();
        over(out, "PENSIONS", gap > 0 ? money(gap) + " short a month" : "funded",
                balanceNow < 0 ? gap / spending : 0, .05, .20, () -> {
                    ui.policyScreen.policyArea = "Promises";
                    ui.policyScreen.policyPage = "Pensions";
                    ui.policyScreen.dropProposal();
                    ui.policyScreen.showPolicyMenu();
                });

        // MEASURED: the unskilled band sits on the floor in month one of every
        // city and again whenever the city stalls. One band pinned is the
        // minimum wage doing its job; three of four is the wage ladder
        // collapsing onto it.
        int pinned = population > 0 ? ui.policyScreen.pinnedBands() : 0;
        over(out, "WAGES", pinned > 0
                        ? pinned + (pinned == 1 ? " band" : " bands") + " pinned to the floor"
                        : "no band is pinned",
                pinned, 2, 4, () -> {
                    ui.policyScreen.policyArea = "Wages";
                    ui.policyScreen.policyPage = PolicyScreen.POLICY_WAGE_PAGES[0];
                    ui.policyScreen.dropProposal();
                    ui.policyScreen.showPolicyMenu();
                });

        double balance = balanceNow;
        over(out, "THE BUDGET", balance < 0 ? money(-balance) + " short a month" : "in surplus",
                balance < 0 ? -balance / spending : 0, .05, .20, () -> {
                    ui.policyScreen.policyArea = "Taxes";
                    ui.policyScreen.policyPage = PolicyScreen.POLICY_HOME;
                    ui.policyScreen.dropProposal();
                    ui.policyScreen.showPolicyMenu();
                });

        return out;
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

        double sick = health.getSickRate();
        over(out, "OFF SICK", String.format("%.1f%%", sick * 100),
                sick, .06, .12, ui.servicesScreen::showServicesStatsMenu);

        double hungry = health.getHungerRate();
        over(out, "HUNGRY", hungry > 0 ? String.format("%.1f%%", hungry * 100) : "nobody",
                hungry, .005, .03, ui.peopleScreen::showHouseholdMenu);

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
        over(out, "THE CURRENCY", fx.isPinned() ? "pinned"
                        : String.format("%.0f%% %s than parity", drift * 100,
                                fx.deviationFromParity() > 0 ? "weaker" : "stronger"),
                fx.isPinned() ? 0 : drift, .25, .50, ui.tradeScreen::showForeignMenu);

        /*
         * THE SHAPE OF THE HOUSING STOCK IS A READING, NOT A LEVER.
         *
         * Jerus: "its a residential thing, so the business takes care of it, so
         * it should be a symptom not a lever." He is right, and it is the rule
         * the Build tab already draws in amber - investors put up housing on
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
                rows.getChildren().add(summaryRow("NEXT TO WATCH",
                        next.label().toLowerCase() + " · " + next.reading(),
                        PANEL_GOOD, next.go()));
            }
        } else {
            for (Watch w : biting) {
                rows.getChildren().add(summaryRow(w.label(), w.reading(),
                        w.level() >= 2 ? PANEL_BAD : PANEL_WARN, w.go()));
            }
        }

        /* ------------------------------ the symptoms ------------------------------ */
        rows.getChildren().add(panelHeading("HOW THE CITY IS"));
        for (Watch w : citySymptoms()) {
            rows.getChildren().add(summaryRow(w.label(), w.reading(),
                    w.level() >= 2 ? PANEL_BAD : w.level() == 1 ? PANEL_WARN : null,
                    w.go()));
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
        double annualGdp = economy.getYearGdp();
        double debt = ui.game.getDebtManager().getAllPrincipal();

        /* ================= ECONOMY ================= */
        body.getChildren().add(panelSection("econ", "ECONOMY",
                money(economy.getMonthGdp()) + "/mo", null,
                () -> {
                    VBox b = panelBody(
                            statLine("Monthly GDP", money(economy.getMonthGdp())),
                            statLine("Annual GDP", money(annualGdp)));
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
         * Under ECONOMY because it is the price of money, and its collapsed
         * line is the only thing about it that matters at a glance: what it is
         * adding to every rate in the city. Red when it is adding anything at
         * all, which is the whole signal - a strained bank taxes every borrower
         * quietly, and before this the player's only clue was that everything
         * had got dearer at once.
         */
        Bank bankPanel = ui.game.getBank();
        double bankPremium = bankPanel.ratePremium();
        double bankCapacity = bankPanel.capacity();
        body.getChildren().add(panelSection("bank", "BANK",
                bankPanel.isInsolvent() ? "INSOLVENT"
                        : bankPanel.getBranches() <= 0
                        ? "none  \u00b7  +" + formatter.format(bankPremium * 100) + " pts"
                        : String.format("%.0f%% lent  \u00b7  %s", bankCapacity > 0
                                ? bankPanel.getWeightedBook() / bankCapacity * 100 : 0,
                                bankPremium > 0
                                        ? "+" + formatter.format(bankPremium * 100) + " pts"
                                        : "no premium"),
                bankPremium > 0 ? "#ff8a7a" : null,
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
                                    Math.abs(fxDrift) > .15 ? PANEL_WARN : null));
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
                        // Game's THE FOUNDING RESERVE.
                        if (ui.game.getMonth() <= Game.FOUNDERS_NOTE_MONTHS) {
                            double cover = fxPanel.importCover();
                            b.getChildren().add(panelNote(String.format(
                                    "the founders left %s here%s", usd(Game.FOUNDING_RESERVE_USD),
                                    fxPanel.monthlyImports() <= 0 ? ""
                                            : cover >= 120 ? " - over ten years of imports"
                                            : String.format(" - %.1f months of imports", cover))));
                        }
                        if (fxPanel.monthlyImports() > 0) {
                            b.getChildren().add(statLine("Import cover",
                                    String.format("%.1f mo",
                                            Math.min(9999, fxPanel.importCover())),
                                    fxPanel.importCover() < 3 ? PANEL_WARN : null));
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

        body.getChildren().add(panelSection("tax", "TAX", money(taxTotal), null,
                () -> panelBody(
                        panelNote("at " + formatter.format(economy.getTaxRate() * 100) + "%"),
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
                            money(market.getMinimumWage())));

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
                     * The other half of the same question, and the one the new
                     * arrival rules make askable: the unskilled band is no
                     * longer an import at all. Nobody moves here without a
                     * diploma, so a big No-diploma row is this city's own
                     * children, and it is a school problem rather than a
                     * migration one.
                     */
                    double unskilled = own[WageBand.NONE.ordinal()];
                    double banded = 0;
                    for (double v : own) banded += v;
                    if (banded > 0 && unskilled / banded > .2) {
                        b.getChildren().add(panelNote(String.format(
                                "%.0f%% have no diploma — nobody arrives that way,"
                                + " so these are children the schools missed",
                                unskilled / banded * 100)));
                    }
                    return b;
                }));

        /* ================= SCHOOLS ================= */
        Education schools = ui.game.getEducation();

        body.getChildren().add(panelSection("school", "SCHOOLS",
                String.format("%.0f%% taught", schools.basicCoverage() * 100),
                schools.basicCoverage() < .5 ? PANEL_BAD
                        : schools.basicCoverage() < .9 ? PANEL_WARN : PANEL_GOOD,
                () -> {
                    VBox b = panelBody();
                    for (EducationType type : EducationType.values()) {
                        if (type == EducationType.NONE) continue;
                        double seats = schools.getEnrolled(type);
                        if (seats <= 0 && !type.isBasic()) continue;
                        b.getChildren().add(statLine(type.getLabel(),
                                type.isBasic()
                                        ? String.format("%.0f%%  %s", 
                                                schools.getCoverage(type) * 100,
                                                shortNumber(seats))
                                        : shortNumber(seats) + " studying",
                                type.isBasic() && schools.getCoverage(type) < .9
                                        ? PANEL_WARN : null));
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
        int tight = 0;
        if (utilities.getProduction() > 0
                && utilities.getConsumption() / utilities.getProduction() >= .75) tight++;
        if (utilities.getWaterProduction() > 0
                && utilities.getWaterConsumption() / utilities.getWaterProduction() >= .75) tight++;
        if (ui.game.getInfrastructureManager().isStrained()) tight++;
        final int strained = tight;

        body.getChildren().add(panelSection("res", "RESOURCES",
                strained == 0 ? "all clear" : strained + " tight",
                strained == 0 ? PANEL_GOOD : PANEL_WARN,
                () -> panelBody(
                        utilityLine("Energy",
                                utilities.getConsumption(), utilities.getProduction()),
                        utilityLine("Water",
                                utilities.getWaterConsumption(), utilities.getWaterProduction()),
                        roadLine(),
                        statLine("Materials", String.format("%,d",
                                ui.game.getConstructionMaterials())),
                        statLine("Store stock", String.format("%,d",
                                ui.game.getSectors().retail().getStoreInventory())),
                        statLine("Food stock", String.format("%,.0f",
                                ui.game.getSectors().industry().getStock(Good.BREAD))))));

        /* ================= LAND ================= */
        double landUsed = land.getUtilisation();
        /*
         * THE PRICE ON THE COLLAPSED HEADER, not only inside.
         *
         * "% used" says how full the city is; the price is what the player is
         * actually deciding against - every building's all-in cost is its cash
         * plus its footprint at this figure, and the three road types are
         * costed so that which one wins depends on it. A number that decides
         * every purchase should not need a click.
         */
        body.getChildren().add(panelSection("land", "LAND",
                String.format("%.0f%% used  ·  $%.2f/sq ft",
                        landUsed * 100, land.getPricePerSqFt() * 1000),
                landUsed >= .95 ? PANEL_BAD : landUsed >= .85 ? PANEL_WARN : null,
                () -> panelBody(
                        statLine("Owned", String.format("%.0f blocks",
                                land.getOwnedSqFt() / LandManager.BLOCK_SQ_FT)),
                        statLine("Free", String.format("%.1f blocks",
                                land.getAvailableBlocks())),
                        statLine("Price/sq ft", String.format("$%.2f",
                                land.getPricePerSqFt() * 1000)))));

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
                + " -fx-text-fill: #eceff1; -fx-padding: 0 0 1 2;");

        Label subtitle = new Label(CityCalendar.format(ui.game.getMonth())
                + "   ·   month " + ui.game.getMonth());
        subtitle.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 9px;"
                + " -fx-text-fill: #8fa3b0; -fx-padding: 0 0 6 2;");

        VBox body = new VBox(0);
        // The ScrollPane's viewport paints its own ground, and on a dark panel
        // an unpainted one shows through as a white sliver down the side of
        // every section. Cheaper to state it than to fight the skin.
        body.setStyle("-fx-background-color: #1c262b;");

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
                statLine("Net income", money(income), income < 0 ? PANEL_BAD : PANEL_GOOD),
                statLine("Population", String.format("%,d", population)));
        vitals.setStyle("-fx-padding: 6 4 6 2; -fx-background-color: #223038;"
                + " -fx-background-radius: 4;");
        VBox.setMargin(vitals, new javafx.geometry.Insets(0, 0, 8, 0));

        /* =============================================================
           AND WHATEVER IS ACTUALLY WRONG.

           An alert earns its place by being ABSENT most of the time. These are
           the five conditions that quietly cost the city output or people, each
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
            alerts.getChildren().add(statLine("Roads", roadSummary(), PANEL_BAD));
        }
        if (utilities.getProduction() > 0
                && utilities.getConsumption() > utilities.getProduction()) {
            alerts.getChildren().add(statLine("Power", "over capacity", PANEL_BAD));
        }
        if (utilities.getWaterProduction() > 0
                && utilities.getWaterConsumption() > utilities.getWaterProduction()) {
            alerts.getChildren().add(statLine("Water", "over capacity", PANEL_BAD));
        }
        if (land.getUtilisation() >= .95) {
            alerts.getChildren().add(statLine("Land",
                    String.format("%.0f%% used", land.getUtilisation() * 100), PANEL_BAD));
        }

        if (!alerts.getChildren().isEmpty()) {
            alerts.setStyle("-fx-padding: 4 2 4 4; -fx-background-color: #331d1d;"
                    + " -fx-background-radius: 3; -fx-border-color: #c0392b;"
                    + " -fx-border-width: 0 0 0 2;");
            VBox spacer = new VBox(alerts);
            spacer.setStyle("-fx-padding: 6 0 2 0;");
            body.getChildren().add(spacer);
        }

        if (ui.prefs.isPanelDashboard()) {
            body.getChildren().add(panelFoldAll());
            panelDashboardSections(body);
        } else {
            panelSummaryRows(body);
        }

        javafx.scene.control.ScrollPane scroller = ui.keptPanelScroller("city", body);
        scroller.setFitToWidth(true);
        scroller.setPrefHeight(700);
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
     * it reads below the two numbers beside it, which is the point.
     */
    HBox careLine(String label, CareType care, double needed, double[] staffing) {

        double places = ui.game.getBuildingManager().getStaffedCareCapacity(care, staffing);
        double cover = ui.game.getHealthcare().getCoverage(care);

        return statLine(label, String.format("%.0f%%  %s/%s", cover * 100,
                        shortNumber(places), shortNumber(needed)),
                cover < .5 ? PANEL_BAD : cover < .9 ? PANEL_WARN : null);
    }

    /** Keeps building names inside the panel's fixed-width column. */
    String shorten(String name) {
        return (name.length() <= 13) ? name : name.substring(0, 12) + ".";
    }
}
