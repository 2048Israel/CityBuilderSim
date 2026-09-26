package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
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
 * The build tab: the strip of categories across the top, the constraints bar
 * that says what stops a build, the line that says who builds these, and every
 * building as a card - its face, its order line, its stats - with the stat
 * card's own vocabulary for what a building does and what care it gives.
 *
 * Split out of UserInterface on 2026-09-18: the five banners BUILD: THE
 * CATEGORY SCREEN IS GONE TOO, THE HALF OF THE CATALOGUE THAT BUILDS ITSELF,
 * THE BUILD MENU, ONE BUILDING, AS A CARD and THE STAT CARD exactly as they
 * were, the shell's members reached through ui. The shell still reads which
 * category is open (buildCategory) for the rail and the scroll memory, and the
 * inbox and the panels send the player into a category through
 * handleAllBuildingMenus(). BuildMenuCheck reads the three card methods.
 * Since 0.7.5 the shell's key filter also calls buildPending() and
 * clearPending(): Enter, and Backspace or Delete, on the page showing.
 */
final class BuildScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    BuildScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       BUILD: THE CATEGORY SCREEN IS GONE TOO.

       It was a label and seven identical grey buttons whose only job was to
       route you one level down - and the list it routed you to is the best
       looking screen in the game, priced rows with a card behind every one.
       So the game opened on its worst screen to reach its best, and switching
       from housing to roads cost three clicks: back, chooser, list.

       The categories are a strip across the top of the LIST now. Build lands
       straight on rows you can act on, and moving between categories is one
       click. Two levels of menu became none.

       Healthcare and Education come up to the strip at the same time. They were
       behind a "Services" submenu that existed because they are built for the
       people rather than for the economy - a true distinction, and not one
       worth a click. The strip says it by putting them last.
       ===================================================================== */

    /** One tab on the build strip: what it is called and what it contains. */
    record BuildCategory(String name, EnumSet<BuildingType> types) { }

    /* =====================================================================
       THE HALF OF THE CATALOGUE THAT BUILDS ITSELF.

       Jerus: "add a little disclaimer or make the categories orange or
       something idk, to let the player know that industrial, commercial, and
       residential build themselves, so well business will love if you build it,
       but you dont need to."

       This is the single biggest thing the Build tab was not saying. Seven
       categories are laid out identically and three of them are OPTIONAL in a
       way the other four are not: BusinessInvestment puts up housing, shops,
       mills, steel, mines and depots on its own, out of its own money, whenever
       they pay. Nothing puts up a power station, a road, a clinic or a school
       except the city. A new player reading that strip has no way to tell, and
       the failure mode is expensive in both directions - spending the treasury
       on flats that were coming anyway, or waiting for a hospital nobody is
       going to build.

       The list is BusinessInvestment's own, and it is the six types that class
       actually invests in. Derived from the types rather than the category
       NAMES, so a category that later mixes private and public buildings stops
       claiming to be private the moment it does.
       ===================================================================== */
    static EnumSet<BuildingType> investorTypes() {
        return EnumSet.of(BuildingType.RESIDENTIAL, BuildingType.COMMERCIAL,
                BuildingType.INDUSTRIAL, BuildingType.HEAVY_INDUSTRY,
                BuildingType.MINING, BuildingType.CONSTRUCTION,
                BuildingType.BUSINESS_SERVICES, BuildingType.AGRICULTURE,
                BuildingType.RAIL, BuildingType.AUTOMOTIVE,
                BuildingType.LUXURY, BuildingType.HOSPITALITY);
    }

    /** True when everything in this category is something investors put up. */
    static boolean investorBuilt(EnumSet<BuildingType> types) {
        return !types.isEmpty() && investorTypes().containsAll(types);
    }


    /**
     * What a private business builds to sell something.
     *
     * A FRESH SET EVERY CALL, because EnumSet is mutable and these are handed
     * to screens that have no reason to know they were sharing one.
     */
    static EnumSet<BuildingType> industrialTypes() {
        return EnumSet.of(BuildingType.INDUSTRIAL, BuildingType.HEAVY_INDUSTRY,
                BuildingType.MINING, BuildingType.CONSTRUCTION);
    }

    /**
     * ...and what the city runs because everything else needs it.
     *
     * SPLIT OUT OF INDUSTRIAL - Jerus: "coal power plant and water treatment to
     * a utilities not industrial". He is right, and the old grouping was a
     * filing decision rather than a description: a power plant is not a
     * business the city hopes will turn a profit, it is a network with a
     * coverage figure, and the game already treats it that way everywhere else.
     * The Services tab has had a Utilities section for as long as it has
     * existed; this makes the shelf you buy them from agree with it.
     */
    static EnumSet<BuildingType> utilityTypes() {
        return EnumSet.of(BuildingType.ELECTRICITY, BuildingType.WATER);
    }

    /**
     * The strip, in the order a city is actually built.
     *
     * Housing first because a city with no homes employs nobody, then the shops
     * that feed them, then the industry that employs them, then the power and
     * water that run it, then the roads that carry it, then the two the city
     * provides rather than sells.
     */
    static BuildCategory[] buildCategories() {
        return new BuildCategory[] {
            new BuildCategory("Residential",    EnumSet.of(BuildingType.RESIDENTIAL)),
            new BuildCategory("Commercial",     EnumSet.of(BuildingType.COMMERCIAL)),
            new BuildCategory("Industrial",     industrialTypes()),
            new BuildCategory("Utilities",      utilityTypes()),
            new BuildCategory("Infrastructure", EnumSet.of(BuildingType.INFRASTRUCTURE)),
            new BuildCategory("Healthcare",     EnumSet.of(BuildingType.HEALTHCARE)),
            new BuildCategory("Education",      EnumSet.of(BuildingType.EDUCATION)),
            new BuildCategory("Safety",         EnumSet.of(BuildingType.SAFETY)),
            // Its own row rather than folded into Industrial, because the thing
            // a player needs to understand about these is the one thing they do
            // not share with a mill: the customer is not in the city.
            new BuildCategory("Services",       EnumSet.of(BuildingType.BUSINESS_SERVICES)),
            // Its own row for the same reason, and a different one: what a
            // player has to understand about a farm is that it competes with
            // the whole neighbourhood rather than with one lot. See
            // BuildingType.AGRICULTURE.
            new BuildCategory("Farms",          EnumSet.of(BuildingType.AGRICULTURE)),
            // Its own row because it is the one thing in this strip that is
            // neither a business the city hopes will employ people nor a
            // network the city owns: it is a private company whose product is
            // a price on every other screen. See BuildingType.RAIL.
            new BuildCategory("Rail",           EnumSet.of(BuildingType.RAIL)),
            // ...and its own row for the reason Farms and Services have one:
            // what a player has to understand about an assembly plant is the
            // one thing it does not share with a foundry - it cannot be run on
            // imported parts, at any price. See BuildingType.AUTOMOTIVE.
            new BuildCategory("Vehicles",       EnumSet.of(BuildingType.AUTOMOTIVE)),
            // ...and its own row because of what it is FOR. Every other
            // category here is something the city needs; this is the one that
            // exists so a rich city has somewhere to spend, and a player who
            // does not build it will watch its mark-up climb and its citizens
            // save money they cannot use. See BuildingType.LUXURY.
            new BuildCategory("Luxury shops",   EnumSet.of(BuildingType.LUXURY)),
            // ...and the kitchens, which are the other half of the same idea
            // and the only one of the two that feeds anybody. See Restaurants.
            new BuildCategory("Restaurants",    EnumSet.of(BuildingType.HOSPITALITY)),
        };
    }

    /**
     * Which category the player was last looking at.
     *
     * Remembered because Build is a tab now rather than a screen you arrive at
     * from somewhere: a player who is halfway through putting up housing and
     * goes to check the land price should come back to housing, not to the
     * front of the catalogue.
     */
    static final String BUILD_HOME    = "Residential";
    String buildCategory = BUILD_HOME;

    /** The Build tab: the list, in whichever category you were last in. */
    void showBuildMenu() {
        for (BuildCategory category : buildCategories()) {
            if (category.name().equals(buildCategory)) {
                handleAllBuildingMenus(category.name(), category.types());
                return;
            }
        }
        BuildCategory first = buildCategories()[0];
        handleAllBuildingMenus(first.name(), first.types());
    }

    /**
     * The strip itself.
     *
     * A FlowPane rather than an HBox, because seven categories at this width fit
     * on one line on a maximised window and wrap rather than clip on a small
     * one - and the one thing this strip must never do is hide a category.
     */
    javafx.scene.layout.FlowPane buildStrip(String current) {

        javafx.scene.layout.FlowPane strip = new javafx.scene.layout.FlowPane(6, 6);
        strip.setAlignment(Pos.CENTER);
        strip.setPrefWrapLength(Palette.BUILD_ROW + 120);

        for (BuildCategory category : buildCategories()) {
            boolean on = category.name().equals(current);
            /*
             * AMBER MEANS SOMEBODY ELSE WILL DO IT. The colour is carried by
             * the tab's own text and by the bar under the open one, so it reads
             * whether the category is open or not - a legend that only appears
             * once you are inside is a legend that never answers "which of
             * these seven do I actually have to do".
             */
            boolean theirs = investorBuilt(category.types());
            Button tab = new Button(category.name());
            tab.setStyle(Palette.words(Palette.SIZE_BODY,
                        on ? Palette.TEXT_HEAD : theirs ? Palette.WARN : Palette.TEXT_BODY)
                    + " -fx-background-color: " + (on ? Palette.RAISED : Palette.CONTROL) + ";"
                    + " -fx-background-radius: " + Palette.RADIUS_TIGHT + ";"
                    + " -fx-border-color: "
                    + (on ? (theirs ? Palette.WARN : Palette.ACCENT) : "transparent") + ";"
                    + " -fx-border-width: 0 0 2 0; -fx-cursor: hand;");
            Tooltip tip = new Tooltip(theirs
                    ? "Investors build these themselves. You can build them too."
                    : "Nobody builds these but the city.");
            tip.setShowDelay(Duration.millis(250));
            tab.setTooltip(tip);
            tab.setOnAction(e -> {
                buildCategory = category.name();
                handleAllBuildingMenus(category.name(), category.types());
            });
            strip.getChildren().add(tab);
        }
        return strip;
    }


    /* =====================================================================
       THE BUILD MENU

       The catalogue screen itself: the strip of categories, the tiles, the
       constraints bar across the top and the line that says who builds these.
       It sat inside BORROW, where it landed when the build menu was rewritten
       around the debt screen's tiles; it is its own section since 2026-09-18
       so that the interface split can file it with the cards, not the loans.
       ===================================================================== */

    void handleAllBuildingMenus(String menuTitle, EnumSet<BuildingType> categories) {
        ui.clearMenu("handleAllBuildingMenus", () -> handleAllBuildingMenus(menuTitle, categories));
        BuildingManager buildingManager = ui.game.getBuildingManager();

        /*
         * WHICH CATEGORY THIS IS, remembered for the Build tab.
         *
         * Set here rather than only in the strip's own handler, because this
         * method is also entered from outside the strip - the inbox's "go and
         * build healthcare" lands here directly - and a player sent to
         * Healthcare by a warning should find Healthcare when they come back.
         */
        for (BuildCategory category : buildCategories()) {
            if (category.name().equals(menuTitle)) buildCategory = menuTitle;
        }

        /*
         * AND WHICH CARDS ARE ON IT, for the keyboard (see "the keyboard",
         * after placeOrder). Started again here, before a card is drawn, so
         * each card below files itself in the order it is laid out and the
         * caption it keeps up to date is this page's, not the last one's.
         */
        pageCards.clear();
        pageTitle = menuTitle;
        pageCategories = categories;
        pendingHint = new Label("↵ builds what is pending · ⌫ clears it");
        pendingHint.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)
                + " -fx-padding: 6 0 0 0;");

        // 1. The four things that decide whether anything you click will happen.
        HBox limits = constraintsBar();

        /* ---------------------------------------------------------------
           2. THE BUILDINGS, GROUPED BY WHAT THEY ACTUALLY DO

           Fourteen buttons in one column told a player nothing - "Home Care
           Service" and "Home Daycare" sit next to each other and do opposite
           things to opposite ends of the pyramid, and the only way to tell was
           to already know. Jerus: "group it further cause its hard to know
           whats for what".

           Grouped on the CareType field rather than on the names, which is the
           whole reason that field exists. It is also general: every menu that
           contains no care types at all - which is every other menu in the game
           - falls through the loop below unchanged and comes out as the same
           flat column it always was.
           --------------------------------------------------------------- */
        List<BuildingsTemplate> buildings = buildingManager.getTemplatesByCategory(categories);
        VBox buildingsBox = new VBox(5);
        buildingsBox.setAlignment(Pos.CENTER);

        /*
         * GROUPED ON WHAT THE BUILDING IS FOR, which is a field and not a name.
         *
         * Started as a CareType loop for the healthcare menu. Education needed
         * exactly the same treatment - nine schools in one column, with
         * "Middle School" and "Medical School" sitting next to each other doing
         * entirely different things - so the loop reads whichever of the two
         * fields the building has. A menu with neither falls through unchanged
         * and comes out as the flat column it always was.
         */
        java.util.List<Object> keys = new ArrayList<>();
        keys.add(CareType.NONE);
        for (CareType care : CareType.values()) if (care != CareType.NONE) keys.add(care);
        for (EducationType type : EducationType.values()) {
            if (type != EducationType.NONE) keys.add(type);
        }

        for (Object key : keys) {

            List<BuildingsTemplate> group = new ArrayList<>();
            for (BuildingsTemplate template : buildings) {
                if (groupKeyOf(template).equals(key)) group.add(template);
            }
            if (group.isEmpty()) continue;

            if (!CareType.NONE.equals(key)) {
                Label heading = new Label(groupHeading(key));
                heading.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;"
                        + " -fx-text-fill: #5cb8ff; -fx-padding: 10 0 0 0;");
                buildingsBox.getChildren().add(heading);

                Label what = new Label(groupSubtitle(key));
                what.setWrapText(true);
                what.setMaxWidth(TILE_WIDTH * 3 + TILE_GAP * 2);
                what.setStyle("-fx-font-size: 10px; -fx-text-fill: #8fa3b0;"
                        + " -fx-padding: 0 0 3 0;");
                buildingsBox.getChildren().add(what);
            }

            /*
             * A GRID, THREE ACROSS. A column of rows was right when a row was a
             * name and a price; each one carries five figures and a quantity
             * stepper now, and fourteen of those in one column is a screen you
             * scroll rather than read.
             */
            javafx.scene.layout.FlowPane grid =
                    new javafx.scene.layout.FlowPane(TILE_GAP, TILE_GAP);
            grid.setAlignment(Pos.CENTER);
            /*
             * BOUND TO THE WINDOW rather than fixed at three.
             *
             * Three across is the layout on a maximised window and the
             * arithmetic said it fitted - and it laid out two, because the
             * stage is not as wide as the arithmetic assumed once the two side
             * panels and the rail have taken theirs. Asking the viewport how
             * much room there actually is gets three on a big screen and two on
             * a small one, which is what a grid is for.
             */
            grid.prefWrapLengthProperty().bind(
                    ui.menuScroller.widthProperty().subtract(TILE_GAP * 4));
            grid.maxWidthProperty().bind(
                    ui.menuScroller.widthProperty().subtract(TILE_GAP * 4));
            for (BuildingsTemplate template : group) {
                grid.getChildren().add(buildingTile(template, menuTitle, categories));
            }
            buildingsBox.getChildren().add(grid);
        }

        // The two keys, said once, under the grid - and only while something
        // on the page is pending for them to act on.
        buildingsBox.getChildren().add(pendingHint);
        showPendingHint();

        /*
         * 3. NO BACK BUTTON. The strip is where you were going, and the rail is
         *    how you leave - Back had nowhere left to go the moment the
         *    category chooser stopped existing.
         */
        ui.rootMenu.getChildren().addAll(receiptCorner(menuTitle, categories),
                limits, buildStrip(menuTitle), whoBuildsThis(menuTitle, categories),
                buildingsBox);
    }

    /**
     * One line under the strip saying whether this is your job.
     *
     * Deliberately not a warning and not a disclaimer in the legal sense - a
     * player who wants to build flats should build flats, and the model lets
     * them for good reasons. What it must not do is let somebody spend forty
     * million on housing that was already coming, without ever having been told
     * that it was coming.
     *
     * The private line says what building one anyway actually DOES, because
     * "you don't need to" on its own reads as "don't", and that is not true
     * either: an investor who is broke, cautious or short of land will not move
     * on a shortage the city can see, and paying for the first block out of the
     * treasury is a real and sometimes correct policy.
     */
    Label whoBuildsThis(String menuTitle, EnumSet<BuildingType> categories) {

        boolean theirs = investorBuilt(categories);

        Label note = new Label(theirs
                ? "Investors build these themselves \u2014 they go up on their own "
                  + "whenever they pay, and you never have to. Building one anyway "
                  + "spends the city's cash and land on capacity a business will run "
                  + "and keep the earnings from, which is worth doing when they are too "
                  + "broke, too cautious or too short of land to move on their own."
                : "Nobody builds these but the city. However badly they are needed, no "
                  + "investor will put one up.");
        note.setWrapText(true);
        note.setMaxWidth(Palette.BUILD_ROW + 120);
        note.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        note.setAlignment(Pos.CENTER);
        note.setStyle(Palette.words(Palette.SIZE_LABEL,
                    theirs ? Palette.WARN : Palette.TEXT_MUTED)
                + " -fx-padding: 6 0 2 0;");
        return note;
    }

    /**
     * WHAT STOPS A BUILD, across the top, above the categories.
     *
     * Four figures, and the reason they are here is that until now a player
     * found out about three of them by being REFUSED. The screen said cash and
     * materials; it never said how much ground was left, what importing a
     * material costs today, or how fast the city can build - so "no land" and
     * "that will take nine years" were both surprises delivered after the
     * decision.
     *
     * They colour when they are about to bite, and that is the whole point: a
     * bar of four grey numbers is a bar nobody reads, and a red one on a city at
     * 95% land is the warning that used to arrive as a rejection.
     */
    HBox constraintsBar() {

        LandManager land = ui.game.getLandManager();
        BuildingManager buildings = ui.game.getBuildingManager();

        double free = land.getAvailableSqFt();
        double used = land.getUtilisation();
        double materials = ui.game.getConstructionMaterials();
        double price = buildings.getConstructionMaterialPrice();
        int output = ui.game.getConstructionOutput();
        int sites = buildings.getUnderConstruction();

        HBox bar = new HBox(0);
        bar.setAlignment(Pos.CENTER);
        bar.setMaxWidth(Region.USE_PREF_SIZE);
        bar.setStyle("-fx-padding: 8 6 8 6;" + Palette.block(Palette.PANEL));

        VBox lastCell = limitCell("BUILDERS", formatter.format(output) + " pts",
                sites == 0 ? "a month, nothing queued"
                           : "a month, " + sites + " site" + (sites == 1 ? "" : "s") + " running",
                output <= 0 ? Palette.BAD : Palette.TEXT_HEAD);
        lastCell.setStyle("-fx-padding: 0 14 0 14;");

        bar.getChildren().addAll(
                limitCell("MATERIALS", formatter.format(materials),
                        "in the yard",
                        materials <= 0 ? Palette.WARN : Palette.TEXT_HEAD),
                limitCell("PER UNIT", unitPrice(price),
                        "to import more", Palette.TEXT_HEAD),
                limitCell("LAND FREE", shortNumber(free) + " sq ft",
                        String.format("%.0f%% of the city used", used * 100),
                        used >= .95 ? Palette.BAD : used >= .85 ? Palette.WARN : Palette.TEXT_HEAD,
                        "Go to the land office and buy more",
                        () -> ui.landScreen.showLandMenu()),
                lastCell);
        return bar;
    }

    /* =====================================================================
       ONE BUILDING, AS A CARD.

       The row this replaces said a name and a price. Everything else about a
       building - what it costs to RUN, how much ground it eats, how long it
       takes, and how many the city already has - was either behind a hover or
       nowhere at all. So a player comparing two kinds of housing was comparing
       the one number that matters least: a General Hospital's upkeep is a
       rounding error against 363 salaries, and the buy price hides that
       completely.

       HOW MANY YOU ALREADY OWN is the line I would keep if I could keep only
       one. The decision on this screen is almost never "what is a house" - it
       is "do I need ANOTHER house", and the stock is the whole of that
       question. It was on the city panel and not here.

       THE CARD FLIPS RATHER THAN FLOATING. Hovering the i covers this card with
       its own stat block; clicking pins it there. Pinned cards stay pinned while
       you open others, so two buildings can be compared side by side - which a
       tooltip, by construction, can never do.
       ===================================================================== */

    /** Which cards are showing their stats, by building name. */
    final java.util.Set<String> pinnedStats = new java.util.HashSet<>();

    /** How many of each the player has dialled up, by building name. */
    final java.util.Map<String, Integer> orderQty = new java.util.HashMap<>();

    StackPane buildingTile(BuildingsTemplate template, String menuTitle,
                                   EnumSet<BuildingType> categories) {

        String key = template.getName();
        double sticker = template.getCashCost();
        double allIn = ui.game.calculateTotalCost(template, 1);
        boolean importing = allIn > sticker + .5;
        boolean afford = allIn <= ui.game.getCash();

        int owned = ui.game.getBuildingManager().getQuantity(template.getId());
        double landFree = ui.game.getLandManager().getAvailableSqFt();

        /* ------------------------------ the face ------------------------------ */
        Label name = new Label(template.getName());
        name.setWrapText(true);
        name.setMaxWidth(TILE_WIDTH - 46);
        name.setStyle(Palette.words(Palette.SIZE_HEADING,
                afford ? Palette.TEXT_HEAD : Palette.TEXT_FAINT) + " -fx-font-weight: bold;");

        Label have = new Label(owned > 0
                ? "you have " + formatter.format(owned)
                : "none built");
        have.setStyle(Palette.words(Palette.SIZE_CAPTION,
                owned > 0 ? Palette.ACCENT : Palette.TEXT_LABEL));

        HBox priceRow = new HBox(4);
        priceRow.setAlignment(Pos.BASELINE_LEFT);
        if (importing) {
            Label was = new Label(money(sticker));
            was.setStyle(Palette.figure(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
            Label arrow = new Label("›");
            arrow.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
            priceRow.getChildren().addAll(was, arrow);
        }
        Label price = new Label(money(allIn));
        price.setStyle(Palette.figure(Palette.SIZE_SECTION,
                afford ? Palette.GOOD : Palette.BAD));
        priceRow.getChildren().add(price);

        Label ground = new Label(String.format("%s sq ft  ·  %s of what is free",
                formatter.format(template.getLandSqFt()),
                landFree > 0
                        ? String.format("%.1f%%", template.getLandSqFt() / landFree * 100)
                        : "no land left"));
        ground.setStyle(Palette.words(Palette.SIZE_CAPTION,
                template.getLandSqFt() > landFree ? Palette.BAD : Palette.TEXT_MUTED));

        Label running = new Label(runningCost(template));
        running.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));

        /* --------------------------- the order line --------------------------- */
        Label quoted = new Label(" ");
        quoted.setWrapText(true);
        quoted.setMaxWidth(TILE_WIDTH - 110);
        quoted.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));

        Label count = new Label("0");
        count.setMinWidth(30);
        count.setAlignment(Pos.CENTER);
        count.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.TEXT_HEAD));

        Button build = new Button("Build");
        build.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                + " -fx-background-color: " + Palette.CONTROL + ";");
        build.setDisable(true);

        /*
         * REPRICED IN PLACE, not by redrawing the screen.
         *
         * Every press used to be a screen change; here it is four labels. That
         * matters for more than speed - a rebuild would throw away every pinned
         * stat card and put the grid back at the top, so comparing two
         * buildings while pricing an order would be impossible.
         *
         * The figures come from Game.quoteBuild, which is the method that
         * CHARGES. Nothing here is arithmetic of its own, so the card cannot
         * quote a price the city then declines to honour.
         */
        /*
         * The reset button is built below but referenced above, because reprice
         * is what decides whether it is on screen. One-element array, the same
         * trick the old quantity screen used for its running total: a lambda
         * can only close over something final.
         */
        final javafx.scene.Node[] resetHolder = new javafx.scene.Node[1];

        Runnable reprice = () -> {
            int n = orderQty.getOrDefault(key, 0);
            count.setText(String.valueOf(n));
            build.setDisable(n <= 0);
            showIf(resetHolder[0], n > 0);
            showPendingHint();
            build.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                    + " -fx-background-color: "
                    + (n > 0 ? Palette.CONFIRM : Palette.CONTROL) + ";");

            if (n <= 0) {
                quoted.setText(" ");
                quoted.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
                return;
            }

            Game.BuildQuote q = ui.game.quoteBuild(template, n);
            String eta = Double.isNaN(q.months) ? "stalled"
                    : q.months < 1 ? "under a month"
                    : "~" + formatter.format(Math.round(q.months)) + " mo";

            String tone;
            String verdict;
            if (q.landNeeded > q.landFree) {
                tone = Palette.BAD;
                verdict = "short " + shortNumber(q.landNeeded - q.landFree) + " sq ft of land";
            } else if (q.total > ui.game.getCash()) {
                tone = Palette.WARN;
                verdict = "short " + money(q.total - ui.game.getCash())
                        + " — you will be offered a bill";
            } else {
                tone = Palette.GOOD;
                verdict = eta;
            }
            quoted.setText(money(q.total) + "  ·  " + verdict);
            quoted.setStyle(Palette.words(Palette.SIZE_CAPTION, tone));
        };

        Button less = stepper("−", 24, () -> {
            // compute, not merge: merge() STORES the -1 when the card has no
            // entry yet, so a "-" on an empty card read "-1" until 0.7.5.
            orderQty.compute(key, (k, v) -> Math.max(0, (v == null ? 0 : v) - 1));
            reprice.run();
        });
        Button more = stepper("+", 24, () -> {
            orderQty.merge(key, 1, Integer::sum);
            reprice.run();
        });

        /*
         * TEN AND A HUNDRED, not just ten.
         *
         * Jerus: "+10 sometimes isnt enough". It is not - housing goes up in
         * hundreds in a city this size, and forty presses to reach four hundred
         * is a control that punishes the thing it exists for. A hundred is the
         * right second step: it is the order of magnitude above ten, and
         * anything larger than a few hundred is a decision worth making one
         * hundred at a time anyway.
         */
        Button ten = stepper("+10", 34, () -> {
            orderQty.merge(key, 10, Integer::sum);
            reprice.run();
        });
        Button hundred = stepper("+100", 40, () -> {
            orderQty.merge(key, 100, Integer::sum);
            reprice.run();
        });

        /*
         * AND A WAY BACK TO ZERO. Also asked for, and obvious in hindsight:
         * with a +100 on the card, overshooting is one click away and unwinding
         * it was a hundred. Hidden until there is something to clear, because a
         * Reset on an empty order is a button that does nothing.
         */
        Button reset = stepper("↺", 24, () -> {
            orderQty.remove(key);
            reprice.run();
        });
        Tooltip clear = new Tooltip("Back to none");
        clear.setShowDelay(Duration.millis(300));
        reset.setTooltip(clear);
        resetHolder[0] = reset;

        build.setOnAction(e -> {
            int n = orderQty.getOrDefault(key, 0);
            if (n <= 0) return;
            orderQty.remove(key);
            placeOrder(template, n, menuTitle, categories);
        });

        /*
         * THE SHORTCUT, SAID ON THE BUTTON IT STANDS IN FOR. A disabled
         * control shows no tooltip, so this is read only while the card has
         * something for Enter to build.
         */
        Tooltip keys = new Tooltip("Enter builds every pending order on this page; Backspace clears them");
        keys.setShowDelay(Duration.millis(300));
        build.setTooltip(keys);

        HBox steps = new HBox(4, less, count, more, ten, hundred);
        steps.setAlignment(Pos.CENTER_LEFT);

        Region spread = new Region();
        HBox.setHgrow(spread, Priority.ALWAYS);
        HBox commit = new HBox(4, quoted, spread, reset, build);
        commit.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(quoted, Priority.ALWAYS);

        VBox order = new VBox(4, steps, commit);

        Region push = new Region();
        VBox.setVgrow(push, Priority.ALWAYS);

        VBox face = new VBox(1, name, have, priceRow, ground, running, push, order);
        face.setStyle("-fx-padding: 8 10 8 10;");

        /* ----------------------------- the stats ----------------------------- */
        VBox stats = tileStats(template);
        stats.setVisible(pinnedStats.contains(key));

        Label info = new Label("i");
        info.setAlignment(Pos.CENTER);
        info.setMinSize(18, 18);
        info.setPrefSize(18, 18);
        info.setMaxSize(18, 18);
        Runnable dress = () -> info.setStyle("-fx-background-radius: 50%;"
                + " -fx-background-color: "
                + (pinnedStats.contains(key) ? Palette.ACCENT_FILL : Palette.CONTROL) + ";"
                + " -fx-text-fill: " + Palette.TEXT_BODY + "; -fx-font-size: 10px;"
                + " -fx-font-weight: bold; -fx-font-family: 'Georgia'; -fx-cursor: hand;");
        dress.run();
        info.setOnMouseEntered(e -> stats.setVisible(true));
        info.setOnMouseExited(e -> stats.setVisible(pinnedStats.contains(key)));
        info.setOnMouseClicked(e -> {
            if (!pinnedStats.remove(key)) pinnedStats.add(key);
            dress.run();
            stats.setVisible(true);
        });
        Tooltip pin = new Tooltip("Hover to read, click to keep it open");
        pin.setShowDelay(Duration.millis(400));
        Tooltip.install(info, pin);

        StackPane tile = new StackPane(face, stats, info);
        StackPane.setAlignment(info, Pos.TOP_RIGHT);
        StackPane.setMargin(info, new javafx.geometry.Insets(7, 7, 0, 0));
        info.setMaxSize(18, 18);
        tile.setPrefSize(TILE_WIDTH, TILE_HEIGHT);
        tile.setMinSize(TILE_WIDTH, TILE_HEIGHT);
        tile.setMaxSize(TILE_WIDTH, TILE_HEIGHT);
        tile.setStyle(Palette.block(Palette.CONTROL, Palette.HAIRLINE));

        pageCards.add(new PageCard(template, reprice));
        reprice.run();
        return tile;
    }

    /** A small square button in the quantity stepper. */
    Button stepper(String glyph, double width, Runnable go) {
        Button b = new Button(glyph);
        b.setMinSize(width, 22);
        b.setPrefSize(width, 22);
        /*
         * -fx-padding: 0 IN THE STYLE, not just setPadding.
         *
         * The theme's .button rule carries "-fx-padding: 6 14 6 14", and on a
         * button pinned to 24px wide that leaves the label a content box 4px
         * narrower than nothing - so JavaFX drew three empty grey squares where
         * the minus, the plus and the +10 should have been. Play-tested.
         *
         * An inline style beats a stylesheet, which setPadding did not.
         */
        b.setPadding(javafx.geometry.Insets.EMPTY);
        b.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY)
                + " -fx-padding: 0; -fx-background-color: " + Palette.FIELD + ";"
                + " -fx-border-color: transparent;"
                + " -fx-background-radius: " + Palette.RADIUS_TIGHT + "; -fx-cursor: hand;");
        b.setOnAction(e -> go.run());
        return b;
    }

    /**
     * What one costs to keep, which is not what it costs to buy.
     *
     * Upkeep plus today's wage bill, because for anything with staff the upkeep
     * is the smaller half and the template only carries the upkeep.
     */
    String runningCost(BuildingsTemplate t) {
        double[] wages = ui.game.getPopulationManager().getWagesPerType();
        double bill = 0;
        int staff = 0;
        for (JobType job : JobType.values()) {
            int n = t.getJobs(job);
            if (n == 0) continue;
            staff += n;
            if (wages != null && job.ordinal() < wages.length) bill += n * wages[job.ordinal()];
        }
        double all = t.getUpkeep() + bill;
        if (all <= 0) return "nothing to run";
        return staff > 0
                ? String.format("runs %s/mo  ·  %s staff", money(all), formatter.format(staff))
                : String.format("runs %s/mo", money(all));
    }

    /**
     * The stat block, sized to cover its own card.
     *
     * The same figures the hover card carried, in one column instead of two,
     * because 250px does not take two. Scrollable, so a building with a long
     * staff list loses nothing.
     */
    VBox tileStats(BuildingsTemplate t) {

        VBox body = new VBox(1);
        body.setStyle("-fx-padding: 6 8 6 8;");

        Label title = new Label(t.getName().toUpperCase());
        title.setWrapText(true);
        title.setMaxWidth(TILE_WIDTH - 46);
        title.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.ACCENT) + " -fx-font-weight: bold;");
        body.getChildren().add(title);

        for (String line : whatItDoes(t)) {
            Label l = new Label(line);
            l.setWrapText(true);
            l.setMaxWidth(TILE_WIDTH - 24);
            l.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_BODY)
                    + " -fx-padding: 3 0 0 0;");
            body.getChildren().add(l);
        }

        /*
         * WHAT THE FACE DOES NOT ALREADY SAY.
         *
         * The cash cost, the land and the upkeep were all on this list and are
         * all on the card underneath it - so covering the card with them told
         * the player three things they had just read, and pushed the four they
         * had not off the bottom of a 212px box. A cover that repeats the face
         * is a cover that wastes the space it took.
         */
        body.getChildren().add(cardGap());
        body.getChildren().addAll(
                statPair("Materials", formatter.format(t.getConstructionMaterials())),
                statPair("Build points", formatter.format(t.getConstructionPoints())),
                statPair("Road load", formatter.format(t.getRoadLoad())),
                statPair("Electricity", formatter.format(t.getElectricityConsumption())),
                statPair("Water", formatter.format(t.getWaterConsumption())));

        double[] wages = ui.game.getPopulationManager().getWagesPerType();
        StringBuilder mix = new StringBuilder();
        double bill = 0;
        int staff = 0;
        for (JobType job : JobType.values()) {
            int n = t.getJobs(job);
            if (n == 0) continue;
            staff += n;
            if (wages != null && job.ordinal() < wages.length) bill += n * wages[job.ordinal()];
            if (mix.length() > 0) mix.append(", ");
            mix.append(n).append(" ").append(jobLabel(job));
        }
        if (staff > 0) {
            body.getChildren().add(cardGap());
            body.getChildren().add(statPair("Wages", money(bill) + "/mo"));
            Label who = new Label(mix.toString());
            who.setWrapText(true);
            who.setMaxWidth(TILE_WIDTH - 24);
            who.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
            body.getChildren().add(who);
        }

        javafx.scene.control.ScrollPane scroller =
                new javafx.scene.control.ScrollPane(body);
        scroller.setFitToWidth(true);
        scroller.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroller.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);

        VBox cover = new VBox(scroller);
        cover.setPrefSize(TILE_WIDTH, TILE_HEIGHT);
        cover.setMaxSize(TILE_WIDTH, TILE_HEIGHT);
        cover.setStyle(Palette.block(Palette.FIELD, Palette.ACCENT));
        return cover;
    }

    /** A label and a figure, on one line, inside a stat cover. */
    HBox statPair(String label, String value) {
        Label what = new Label(label);
        what.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label figure = new Label(value);
        figure.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_BODY));
        HBox row = new HBox(6, what, gap, figure);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * Placing the order, and everything the city can say back.
     *
     * Lifted out of the old quantity screen unchanged: buildStack is the method
     * that decides, and the four refusals it can return each have a screen
     * that explains the refusal and offers the way out of it. The card is a new
     * way to reach this; it is not a new way to buy.
     *
     * True when it was built and the page is drawn again; false when the city
     * said no and the refusal's screen is up instead - which is where Enter's
     * run of orders stops (buildPending).
     */
    boolean placeOrder(BuildingsTemplate template, int quantity,
                            String menuTitle, EnumSet<BuildingType> categories) {

        Game.BuildResult result = ui.game.buildStack(template, quantity, false);

        if (result == Game.BuildResult.SUCCESS) {
            handleAllBuildingMenus(menuTitle, categories);
        } else if (result == Game.BuildResult.NEEDS_FUNDING) {
            showQuickDebtMenu(template, quantity, menuTitle, categories);
        } else if (result == Game.BuildResult.NO_LAND) {
            showNoLandMenu(template, quantity, menuTitle, categories);
        } else if (result == Game.BuildResult.NO_DEPOSIT) {
            showNoDepositMenu(template, quantity, menuTitle, categories);
        } else if (result == Game.BuildResult.NO_LICENCE) {
            showNoLicenceMenu(template, quantity, menuTitle, categories);
        }
        return result == Game.BuildResult.SUCCESS;
    }

    /* ---------------------------- the keyboard ---------------------------- */

    /*
     * ENTER BUILDS WHAT IS PENDING, BACKSPACE CLEARS IT (0.7.5). Jerus: "in
     * the building rail, when you have lets say 3 ready to build, i want to be
     * able to press enter to build, instead of having to click the green
     * button, you can still click it, but just a short cut, and
     * backspace/delete to reset it."
     *
     * The keys arrive through the window's key filter (UserInterface.start),
     * which calls the two methods below only while this page is the screen.
     * They act on the page the player is LOOKING AT and on nothing else:
     * orderQty is kept by name across every category, so a hundred flats
     * dialled up on Residential and left there are not built by an Enter
     * pressed on Healthcare. Each card files itself in pageCards as it is
     * drawn, so the page's order is the order it is laid out in - under its
     * group headings, not the catalogue's.
     */

    /** One card on the page showing now: what it builds, and how it reprices itself in place. */
    record PageCard(BuildingsTemplate template, Runnable reprice) { }

    /** The cards on the category page showing now, in the order they are laid out; each draw starts it again. */
    final List<PageCard> pageCards = new ArrayList<>();

    /** Which page those are on, so an order placed from the keyboard comes back to it. */
    private String pageTitle = BUILD_HOME;
    private EnumSet<BuildingType> pageCategories = EnumSet.noneOf(BuildingType.class);

    /** The caption under the grid that says the two keys; shown only while something on the page is pending. */
    private Label pendingHint;

    /**
     * Every pending order on the page, placed as its own Build button would
     * place it, in the page's order.
     *
     * Through placeOrder, one card at a time, so each order meets the city as
     * the one before it left it: what the first spent is not there for the
     * second. The first refusal stops the run - placeOrder has put up the
     * screen that explains it and offers the way out, and the orders after it
     * stay on their cards for when the player comes back. True when there was
     * anything to build, which is when the key is spent.
     */
    boolean buildPending() {
        // A copy, and the page remembered: every success draws the page
        // again, and pageCards with it.
        List<PageCard> page = new ArrayList<>(pageCards);
        String title = pageTitle;
        EnumSet<BuildingType> categories = pageCategories;

        boolean any = false;
        for (PageCard card : page) {
            String key = card.template().getName();
            int n = orderQty.getOrDefault(key, 0);
            if (n <= 0) continue;
            any = true;
            orderQty.remove(key);
            if (!placeOrder(card.template(), n, title, categories)) break;
        }
        return any;
    }

    /**
     * Every quantity on the page back to none - the ↺ on every card at once,
     * and like it, each card repriced in place rather than the page redrawn
     * (see REPRICED IN PLACE, in buildingTile). True when something pending
     * was cleared.
     */
    boolean clearPending() {
        boolean cleared = false;
        for (PageCard card : pageCards) {
            Integer n = orderQty.remove(card.template().getName());
            if (n == null) continue;
            if (n > 0) cleared = true;
            card.reprice().run();
        }
        return cleared;
    }

    /** The caption under the grid, shown while any card on the page has a quantity. */
    void showPendingHint() {
        boolean any = false;
        for (PageCard card : pageCards) {
            if (orderQty.getOrDefault(card.template().getName(), 0) > 0) {
                any = true;
                break;
            }
        }
        showIf(pendingHint, any);
    }


    /* =====================================================================
       THE STAT CARD

       EVERY FIGURE ON IT IS READ OFF THE MODEL. There is no description field
       on a building and none was added, because a sentence typed into a data
       file is a claim that stops being checked the moment somebody rebalances
       the building it describes - and this game rebalances constantly. What the
       card says a building does, it works out from what the building is: the
       wage bill from today's wages, the all-in price from today's materials
       market, the revenue from the fee the service actually charges. A card
       that cannot go stale is worth more than a card that reads better.
       ===================================================================== */


    /** A hairline of space, used where a blank line would be too much. */
    Region cardGap() {
        Region r = new Region();
        r.setMinHeight(7);
        return r;
    }


    /**
     * What this building actually does for the city, worked out from its own
     * numbers.
     *
     * Every branch reads the field the SIMULATION reads for that category -
     * production1 is tonnes for a mine and construction points for a depot and
     * kilowatts for a power plant, and capacity is people for a house, shelf
     * space for a shop, warehouse space for a mill and treatments for a clinic.
     * Getting that mapping wrong would produce a confident sentence about the
     * wrong number, which is worse than no sentence, so each one is taken from
     * the handler that consumes it rather than from the field's name.
     */
    // Package-private, not private, so BuildMenuCheck can read the sentences
    // back. A description that is derived can still be derived WRONGLY - the
    // whole risk of reading production1 is that it means a different thing in
    // each category - and the only way to hold that is to print all 29 and
    // assert on them. Same for the two below.
    public List<String> whatItDoes(BuildingsTemplate t) {
        List<String> out = new ArrayList<>();

        /*
         * A SECTOR'S BUILDING SAYS WHAT IT MAKES AND USES, off the template's
         * own goods (2026-09-11, the sector template): the same two maps the
         * simulation reads, priced at today's market. That replaced five
         * category branches each reading production1 as a different thing.
         * The homes are the exception - a home "makes" housing, and nobody
         * needs telling that.
         */
        if (t.getCategory() == BuildingType.RESIDENTIAL) {
            out.add(String.format("%s %s for up to %s residents.",
                    formatter.format(Math.max(t.getDwellings(), 1)),
                    t.getDwellings() == 1 ? "home" : "homes",
                    formatter.format(t.getCapacity())));
            return out;
        }
        if (t.isOwnedBySector() && !t.goodsMade().isEmpty()) {
            for (java.util.Map.Entry<Good, Double> e : t.goodsMade().entrySet()) {
                Good g = e.getKey();
                if (g == Good.GROCERIES) {
                    // capacity is shelf stock; coverage is what it sells in a month.
                    out.add(String.format("Sells %s units a month and holds %s"
                            + " units of stock on the shelves.",
                            formatter.format(t.getCoverage()),
                            formatter.format(t.getStock())));
                } else if (g == Good.BUILDING_WORK) {
                    out.add(String.format("Adds %s construction points a month - the rate"
                            + " everything in the city goes up at.",
                            formatter.format(e.getValue())));
                } else if (g.traded()) {
                    double price = ui.game.getMarkets().get(g).getLocalPrice();
                    String made = String.format("Makes %s %ss of %s a month",
                            formatter.format(e.getValue()), g.unit(), g.label().toLowerCase());
                    if (price > 0) made += String.format(", worth %s at today's price of %s a %s",
                            money(e.getValue() * price), unitPrice(price), g.unit());
                    out.add(made + (t.getStock() > 0
                            ? String.format(", and warehouses %s.", formatter.format(t.getStock()))
                            : "."));
                }
            }
            for (java.util.Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) {
                Good g = e.getKey();
                if (!g.traded()) continue;
                double price = ui.game.getMarkets().get(g).getLocalPrice();
                out.add(String.format("Uses %s %ss of %s a month%s, bought on the market"
                        + " or imported.",
                        formatter.format(e.getValue()), g.unit(), g.label().toLowerCase(),
                        price > 0 ? " - " + money(e.getValue() * price) + " at today's price" : ""));
            }
            if (t.getCategory() == BuildingType.MINING) {
                out.add("Needs a land parcel with iron under it - cash and space alone"
                        + " will not put one up.");
            }
            return out;
        }

        switch (t.getCategory()) {

            case COMMERCIAL:
                // The one commercial building no sector owns: the bank's counters.
                out.add(String.format("A branch of the city's bank. Gathers up to %s of "
                        + "deposits and brings %s of shareholders' capital with it, which "
                        + "is what lets the bank lend.",
                        money(Bank.DEPOSITS_PER_BRANCH), money(Bank.PAID_IN_PER_BRANCH)));
                out.add("A bank past its capacity has no room for the city's next "
                        + "borrower; a branch is what makes room.");
                break;

            case ELECTRICITY:
                out.add(String.format("Generates %s units of electricity a month.",
                        formatter.format(t.getProduction1())));
                break;

            case WATER:
                out.add(String.format("Treats %s units of water a month.",
                        formatter.format(t.getProduction1())));
                break;

            case INFRASTRUCTURE: {
                out.add(String.format("Carries %s trips a month. Every building in the"
                        + " city puts load on the same network.",
                        formatter.format(t.getCapacity())));
                /*
                 * PER TRIP CARRIED, because that is the only way three roads of
                 * different sizes can be compared at all. A Gravel Road is
                 * cheaper than an Elevated Highway in every single column and
                 * carries 40% of the traffic - so a player comparing sticker
                 * prices would conclude the gravel road simply wins, which is
                 * the opposite of true in a city that has run out of room.
                 *
                 * These two numbers ARE the choice: ground, or labour. Every
                 * other figure on the card follows from which of the two the
                 * city has less of.
                 */
                if (t.getCapacity() > 0) {
                    out.add(String.format("Per 1,000 trips carried that is %s sq ft of"
                            + " ground and %s construction points - the whole choice"
                            + " between the roads is which of those two you have less"
                            + " of.",
                            formatter.format(Math.round(
                                    t.getLandSqFt() * 1000.0 / t.getCapacity())),
                            formatter.format(Math.round(
                                    t.getConstructionPoints() * 1000.0 / t.getCapacity()))));
                }
                break;
            }

            case RAIL: {
                /*
                 * THE ONE BUILDING WHOSE VALUE IS A PRICE SOMEWHERE ELSE. A
                 * player can read tonnes off a line and still have no idea what
                 * it is for, because what it buys is a narrower import and
                 * export band on every screen in the game. So the card says
                 * what the freight costs today and what the railway charges,
                 * and leaves the tonnage as the second sentence.
                 */
                ham.citybuildersim.sectors.Rail rail = ui.game.getSectors().rail();
                out.add(String.format("Hauls up to %s tonnes a month of the city's trade "
                        + "to and from the world. Everything it does not carry goes by "
                        + "lorry, at the price the world charges.",
                        formatter.format(t.getRailCapacity())));
                double lorry = rail.lorryRatePerTonne();
                if (lorry > 0) {
                    out.add(String.format("A lorry charges about %s a tonne today and the "
                            + "railway quotes %.0f%% of that - freight money that stays in "
                            + "the city instead of leaving with the cargo.",
                            unitPrice(lorry), rail.getQuote() * 100));
                }
                out.add(String.format("The city trades %s tonnes a month and the network "
                        + "can reach %s of them.",
                        formatter.format(rail.getTradeTonnes()),
                        formatter.format(Math.min(rail.getTradeTonnes(), rail.getCapacityTonnes()))));
                out.add("Owned by the rail sector, which builds it out of its own money "
                        + "when the freight pays for it. The lorries between the siding "
                        + "and the works are still on the road.");
                break;
            }

            case HEALTHCARE:
                out.addAll(whatCareItGives(t));
                break;

            case EDUCATION: {
                EducationType teaches = t.getTeaches();
                double perMonth = t.getCapacity() / (double) teaches.months();

                out.add(String.format("%s places, and a course that runs %d years - so"
                        + " about %s people finish here every month once it is full.",
                        formatter.format(t.getCapacity()), teaches.months() / 12,
                        formatter.format(Math.max(1, Math.round(perMonth)))));

                if (teaches.isProfessional()) {
                    /*
                     * The whole point of a professional school, and it has to be
                     * the first thing on the card: this is not a bigger version
                     * of a university, it is the only way the city will ever
                     * have one of these people who was not already one.
                     */
                    out.add(String.format("Without this school, no resident can ever hold"
                            + " a %s post - the city can only import them.",
                            jobLabel(teaches.licenses())));
                    out.add("Takes university graduates. It raises nobody's level; what"
                            + " they leave with is permission to practise.");
                } else if (teaches.isBasic()) {
                    out.add(String.format("Part of the schooling every child needs before"
                            + " a diploma. The three stages are a pipeline, so the city"
                            + " produces as many diplomas as its NARROWEST stage seats -"
                            + " here that is %s.",
                            teaches.servesAges() == null ? "adults"
                                    : teaches.servesAges().getLabel().toLowerCase()));
                } else {
                    out.add(String.format("Takes people with a %s and turns them into a"
                            + " %s.", teaches.requires().label().toLowerCase(),
                            teaches.produces().label().toLowerCase()));
                }

                out.add(String.format("Tuition is %s a month per student; the city pays"
                        + " whatever share the education policy says.",
                        unitPrice(ui.game.getEducation().feeFor(teaches))));
                break;
            }

            case SAFETY:
                out.addAll(whatSafetyItGives(t));
                break;

            default:
                break;
        }
        return out;
    }

    /**
     * The police and the prisons (2026-09-11). What the card has to say is the
     * one thing a player would get wrong: police cut crime a great deal and
     * never to nothing, and a cell is only worth building if the police are
     * catching people. See Crime.
     */
    List<String> whatSafetyItGives(BuildingsTemplate t) {
        List<String> out = new ArrayList<>();
        Crime crime = ui.game.getCrime();
        double cap = t.getCapacity();
        if (t.getSafety() == SafetyType.POLICE) {
            double fullFor = cap * 100_000.0 / Crime.FULL_OFFICERS_PER_100K;
            out.add(String.format("%s officers when fully staffed: full coverage for %s people,"
                    + " Canada's level for %s.", formatter.format(cap),
                    formatter.format(Math.round(fullFor)), formatter.format(Math.round(2 * fullFor))));
            out.add("Full coverage takes 90% off the crime the city's reasons make - never all"
                    + " of it. The reasons are the out of work, the unhoused, the crowded and"
                    + " the households short of money; only fixing those removes it.");
            out.add(String.format("Police catch people: %.1f%% of crimes end in a six-month"
                    + " sentence at full coverage, none with no police. Somebody has to hold them.",
                    100 * Crime.CAUGHT_AT_FULL));
            out.add(String.format("The city is at %.0f%% coverage now, with %s crimes a year per"
                    + " 100,000 people (%.1fx Canada's).", 100 * crime.getCoverage(),
                    formatter.format(Math.round(crime.getRatePer100k())), crime.getRateVsCanada()));
        } else if (t.getSafety() == SafetyType.PRISON) {
            out.add(String.format("%s cells when fully staffed; a cell holds one prisoner for"
                    + " the six months of a sentence.", formatter.format(cap)));
            out.add("A prisoner is out of work and out of the shops; the city feeds and houses"
                    + " them, and their savings wait for them. When they come out they look"
                    + " for work like anyone.");
            out.add(String.format("Anybody the police catch with no cell free is caught but not"
                    + " held - %s last month.", formatter.format(Math.round(crime.getNotHeld()))));
        }
        return out;
    }

    /**
     * The healthcare version, which needs the care type and not the category.
     *
     * A cemetery and a hospital are the same BuildingType and could not be less
     * alike: one sells a permanent asset once, the other runs a monthly service
     * that shuts if the doctors do not turn up. That distinction is exactly what
     * CareType exists to carry.
     */
    public List<String> whatCareItGives(BuildingsTemplate t) {
        List<String> out = new ArrayList<>();
        CareType care = t.getCare();
        double cap = t.getCapacity();
        double fee = ui.game.getHealthcare().feeNow(care);

        switch (care) {
            case CHILDCARE:
                out.add(String.format("Looks after %s children a month when fully staffed.",
                        formatter.format(cap)));
                out.add(String.format("Childcare is the strongest lever in the game:"
                        + " full coverage cuts infant deaths by a factor of %.0f,"
                        + " doubles the birth rate, and stops babies falling ill twice"
                        + " as often as everybody else.", Healthcare.CHILDCARE_SWING));
                break;

            case GENERAL:
                out.add(String.format("Treats %s people a month when fully staffed.",
                        formatter.format(cap)));
                out.add(String.format("General care sets how much of the city is off"
                        + " sick - %.0f%% with none, %.0f%% with enough - and how fast the"
                        + " sick get better: %.0f%% a month with none, %.0f%% with enough."
                        + " Anyone ill for more than two months can die of it.",
                        Health.UNTREATED_RATE * 100, Health.WELL_SERVED_RATE * 100,
                        Sickness.RECOVERY_UNTREATED * 100, Sickness.RECOVERY_SERVED * 100));
                break;

            case SENIOR:
                out.add(String.format("Cares for %s seniors a month when fully staffed.",
                        formatter.format(cap)));
                out.add(String.format("Cuts senior deaths, stops seniors falling ill twice"
                        + " as often as everybody else, and full coverage draws %.0f%%"
                        + " more people to the city.", Migration.SENIOR_CARE_PULL * 100));
                break;

            case BURIAL:
                out.add(String.format("%s plots. They are consumed permanently and the"
                        + " land never comes back.", formatter.format(cap)));
                out.add(String.format("At %s a burial that is %s of revenue over the"
                        + " life of the ground - a fixed asset that pays for itself over"
                        + " decades, not a monthly service.",
                        unitPrice(fee), money(cap * fee)));
                break;

            case CREMATION:
                out.add(String.format("Handles %s cremations a month when fully staffed,"
                        + " on almost no land and a great deal of electricity.",
                        formatter.format(cap)));
                out.add(String.format("At %s a body it earns %s a month FLAT OUT,"
                        + " which is roughly what it costs to run - an underused one"
                        + " loses money.", unitPrice(fee), money(cap * fee)));
                break;

            default:
                break;
        }

        /*
         * The revenue line, for the three that run as a monthly service.
         *
         * Deliberately the TOTAL and not the per-head fee. A general treatment
         * fee is $0.010 in the game's units, and a card that says "charges
         * $0.01 a head" beside a building that costs $1,400 reads as a bug -
         * the units are consistent, but nothing else on the screen is small
         * enough for the player to have calibrated on them. The monthly figure
         * is in the same register as the upkeep and the wages directly above
         * it, which is the comparison that actually matters: this is what it
         * brings in, that is what it costs.
         */
        if (care.servesTheLiving() && fee > 0) {
            out.add(String.format("Brings in %s a month at full use, charged to the"
                    + " households that use it.", money(cap * fee)));
        }
        return out;
    }

    /** JobType, in words a player reads rather than the enum constant. */
    public String jobLabel(JobType job) {
        switch (job) {
            case NO_DIPLOMA:          return "unskilled";
            case DIPLOMA:             return "diploma";
            case COLLEGE_HEALTH:      return "health college";
            case COLLEGE_BUSINESS:    return "business college";
            case COLLEGE_ENGINEERING: return "eng. college";
            case UNIV_DOCTOR:         return "doctor";
            case UNIV_LAW:            return "lawyer";
            case UNIV_FINANCE:        return "finance";
            case UNIV_SCIENCE:        return "scientist";
            case UNIV_HIGHTECH_ENG:   return "high-tech eng.";
            case UNIV_POLICY:         return "policy";
            default:                  return job.name().toLowerCase();
        }
    }

    /**
     * The receipt dot, top-right of the menu, and the card it opens.
     *
     * It flashes only while there is a receipt this window has not shown yet -
     * compared by serial, not by contents, because building the same three
     * clinics twice in a row produces two identical receipts and the second one
     * is still news. Once opened it goes solid: the flash means "something
     * happened", and a permanent flash means nothing at all.
     */
    HBox receiptCorner(String menuTitle, EnumSet<BuildingType> categories) {

        HBox corner = new HBox();
        corner.setAlignment(Pos.TOP_RIGHT);
        corner.setMaxWidth(Double.MAX_VALUE);
        // Room on the right for the inbox envelope, which is pinned to the same
        // corner of the stage. Two things in one corner is a collision; the
        // receipt sits to the left of it, on the same line.
        corner.setStyle("-fx-padding: 0 70 0 0;");
        if (!ui.game.hasNewReceipt()) return corner;

        boolean unseen = ui.game.getReceiptSerial() != ui.receiptSeen;

        Button dot = new Button();
        dot.setMinSize(18, 18);
        dot.setPrefSize(18, 18);
        dot.setMaxSize(18, 18);
        dot.setStyle("-fx-background-color: #2f7d52; -fx-background-radius: 50%;"
                + " -fx-border-color: #2f7d52; -fx-border-width: 1;"
                + " -fx-border-radius: 50%; -fx-cursor: hand; -fx-padding: 0;");
        Tooltip dotTip = new Tooltip(unseen
                ? "A purchase went through - click for the receipt"
                : "Click for the last receipt");
        dotTip.setShowDelay(Duration.millis(200));
        Tooltip.install(dot, dotTip);

        dot.setOnAction(e -> {
            ui.receiptOpen = !ui.receiptOpen;
            ui.receiptSeen = ui.game.getReceiptSerial();
            handleAllBuildingMenus(menuTitle, categories);
        });

        if (unseen && !ui.receiptOpen) {
            ui.receiptPulse = new FadeTransition(Duration.millis(600), dot);
            ui.receiptPulse.setFromValue(1.0);
            ui.receiptPulse.setToValue(.15);
            ui.receiptPulse.setAutoReverse(true);
            ui.receiptPulse.setCycleCount(Animation.INDEFINITE);
            ui.receiptPulse.play();
        }

        if (!ui.receiptOpen) {
            corner.getChildren().add(dot);
            return corner;
        }

        VBox card = new VBox(1);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #13291d; -fx-border-color: #2f7d52;"
                + " -fx-border-width: 1; -fx-background-radius: 3;"
                + " -fx-border-radius: 3; -fx-padding: 7 11 7 11;");

        Label head = new Label("LAST TRANSACTION");
        head.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #5fd68a;");

        Label what = new Label(formatter.format(ui.game.getBuildQuantity())
                + " \u00d7  " + ui.game.getBuildingName());
        what.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #5fd68a;");

        card.getChildren().addAll(head, what,
                receiptLine("Materials imported", formatter.format(ui.game.getMaterialsUsed())),
                receiptLine("Total cost", money(ui.game.getTotalBuildingCost())));

        HBox stack = new HBox(6);
        stack.setAlignment(Pos.TOP_RIGHT);
        stack.getChildren().addAll(card, dot);
        corner.getChildren().add(stack);
        return corner;
    }

    Label receiptLine(String label, String value) {
        Label l = new Label(String.format("%-20s%12s", label, value));
        l.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;"
                + " -fx-text-fill: #8bc34a;");
        return l;
    }

    /** Which heading a building belongs under: its care type, or what it teaches. */
    Object groupKeyOf(BuildingsTemplate template) {
        if (template.getCare() != CareType.NONE) return template.getCare();
        if (template.getTeaches() != EducationType.NONE) return template.getTeaches();
        return CareType.NONE;
    }

    String groupHeading(Object key) {
        if (key instanceof CareType care) return careHeading(care);
        if (key instanceof EducationType type) return schoolHeading(type);
        return "";
    }

    String groupSubtitle(Object key) {
        if (key instanceof CareType care) return careSubtitle(care);
        if (key instanceof EducationType type) return schoolSubtitle(type);
        return "";
    }

    /**
     * A school's heading, and what it is actually for.
     *
     * The professional schools get the blunt version because the mechanic is
     * not guessable from the name: nothing else in the game makes a job type
     * possible, and a player who does not know that will read a Medical School
     * as an expensive university and never build one.
     */
    String schoolHeading(EducationType type) {
        switch (type) {
            case ELEMENTARY: return "ELEMENTARY  -  ages 6 to 10";
            case MIDDLE:     return "MIDDLE SCHOOL  -  ages 10 to 13";
            case HIGH:       return "HIGH SCHOOL  -  the diploma";
            case COLLEGE:    return "COLLEGE  -  a trade or a technical qualification";
            case UNIVERSITY: return "UNIVERSITY  -  degrees";
            default:         return type.getLabel().toUpperCase()
                                     + "  -  makes " + jobLabel(type.licenses())
                                     + "s possible at all";
        }
    }

    /**
     * One line, like the care subtitles beside it.
     *
     * The first version wrote a paragraph per group. Nine paragraphs plus nine
     * headings plus nine rows is a screen and a half, which is how the Back
     * button ended up off the bottom - a menu is an index, and the full
     * explanation is one hover away on every row.
     */
    String schoolSubtitle(EducationType type) {
        if (type.isProfessional()) {
            return "Without one, no resident can ever hold a "
                    + jobLabel(type.licenses()) + " post - only migrants.";
        }
        switch (type) {
            case ELEMENTARY:
            case MIDDLE:
                return "The pipeline is only as wide as its narrowest stage.";
            case HIGH:
                return "Where the diploma comes from.";
            case COLLEGE:
                return "Diploma in, college tier out. Cheaper and faster than a degree.";
            case UNIVERSITY:
                return "Degrees - and the entry requirement for all four schools below.";
            default:
                return "";
        }
    }

    /** The group's name, in the player's words rather than the enum's. */
    String careHeading(CareType care) {
        switch (care) {
            case CHILDCARE: return "CHILDCARE  -  babies and children";
            case GENERAL:   return "GENERAL CARE  -  everybody";
            case SENIOR:    return "SENIOR CARE  -  the over-seventies";
            case BURIAL:    return "CEMETERIES  -  land, permanently";
            case CREMATION: return "CREMATORIA  -  power, not land";
            default:        return care.getLabel();
        }
    }

    /**
     * What building one of these actually gets you.
     *
     * The numbers are read off the model rather than written down, because a
     * menu that describes yesterday's balance is worse than one that describes
     * nothing - and every one of these figures has already moved once.
     */
    String careSubtitle(CareType care) {
        switch (care) {
            case CHILDCARE:
                return String.format("Full coverage cuts infant deaths to 1/%.0f and doubles"
                        + " the birth rate.", Healthcare.CHILDCARE_SWING);
            case GENERAL:
                return String.format("Sets how much of the city is off sick (%.0f%% to"
                        + " %.0f%%) and how fast the sick get better - the long sick can die.",
                        Health.UNTREATED_RATE * 100, Health.WELL_SERVED_RATE * 100);
            case SENIOR:
                return String.format("Cuts senior deaths, and draws up to %.0f%% more people"
                        + " to the city.", Migration.SENIOR_CARE_PULL * 100);
            case BURIAL:
                return "Plots are consumed forever and the land never comes back."
                        + " Turns a profit.";
            case CREMATION:
                return "Almost no land, a great deal of electricity, and breaks even"
                        + " only when busy.";
            default:
                return "";
        }
    }

    /**
     * The city has the money, the land, and nothing to dig.
     *
     * Its own refusal for the same reason no-land has one: the answer is
     * specific. A mine is not short of cash or short of space, it is short of
     * ore, and the only thing that fixes that is a land parcel with iron under
     * it. Sending the player to the funding screen would sell them a bond that
     * cannot help.
     */
    void showNoDepositMenu(BuildingsTemplate selected, int quantity,
                                   String menuTitle, EnumSet<BuildingType> categories) {
        ui.clearMenu("showNoDepositMenu", () -> showNoDepositMenu(selected, quantity, menuTitle, categories));

        LandManager land = ui.game.getLandManager();

        Label title = new Label("NO IRON DEPOSIT");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        VBox explanation = reportSection("WHY",
                quantity + " x " + selected.getName() + " needs "
                        + (ui.game.minesCommitted() + quantity) + " deposit(s).",
                "The city owns " + land.getIronDeposits()
                        + ", with " + ui.game.minesCommitted() + " already spoken for.",
                "",
                "A mine has to stand on ground with ore under it. Deposits come",
                "with land: some parcels in the land office have iron, and they",
                "cost more because of it.");

        Label reserves = monoLabel(String.format(
                "Ore still in the ground: %,.0f tonnes", land.getIronReserveTonnes()));
        reserves.setStyle("-fx-font-family: 'Courier New'; -fx-padding: 6 0 0 0;");

        Button toLand = new Button("Go to the Land Office");
        toLand.setOnAction(e -> ui.landScreen.showLandMenu());

        Button back = new Button("Back");
        back.setOnAction(e -> handleAllBuildingMenus(menuTitle, categories));

        ui.rootMenu.getChildren().addAll(title, explanation, reserves, toLand, back);
    }

    /**
     * Nobody licensed to practise in it.
     *
     * The second refusal of its kind - a mine with no deposit was the first -
     * and the sentence that matters is the last one: this is not a money
     * problem, so the screen must not offer a bond to fix it.
     */
    void showNoLicenceMenu(BuildingsTemplate selected, int quantity,
                                   String menuTitle, EnumSet<BuildingType> categories) {
        ui.clearMenu("showNoLicenceMenu", () -> showNoLicenceMenu(selected, quantity, menuTitle, categories));

        JobType licence = selected.getRequiresLicence();
        double need = ui.game.licencesNeededFor(selected, quantity);
        double have = ui.game.getPopulationManager().spareLicences(licence);
        String what = licence == null ? "licences" : jobLabel(licence);

        Label title = new Label("NOBODY QUALIFIED TO WORK IN IT");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        VBox explanation = reportSection("WHY",
                quantity + " x " + selected.getName() + " needs "
                        + String.format("%,.0f", Math.ceil(need)) + " " + what
                        + " not already at work.",
                "The city has " + String.format("%,.0f", Math.floor(have)) + " spare.",
                "",
                "A practice opens when it can staff its core and hire the rest.",
                "Half the licensed posts is the bar - the other half can arrive",
                "or graduate later.",
                "",
                "Two ways to get them: a school that licenses this profession,",
                "or migration, which brings a few already qualified when the city",
                "pays over the going rate for them.",
                "",
                "This is not a funding problem. A bond would not fix it.");

        Button toSchools = new Button("Go to Build - Education");
        toSchools.setOnAction(e -> {
            buildCategory = "Education";
            showBuildMenu();
        });

        Button toPeople = new Button("Who the city has");
        toPeople.setOnAction(e -> ui.peopleScreen.showPopulationInfoMenu());

        Button back = new Button("Back");
        back.setOnAction(e -> handleAllBuildingMenus(menuTitle, categories));

        ui.rootMenu.getChildren().addAll(title, explanation, toSchools, toPeople, back);
    }

    /**
     * The city has the money and nowhere to put the building.
     *
     * Its own separate screen rather than a line on the funding screen, because
     * the two refusals have opposite answers: no cash is solved by borrowing,
     * no land only by annexing, and offering a T-Bill for a land shortage would
     * sell debt that cannot fix the problem.
     */
    void showNoLandMenu(BuildingsTemplate selected, int quantity,
                                String prevTitle, EnumSet<BuildingType> prevCats) {
        ui.clearMenu("showNoLandMenu", () -> showNoLandMenu(selected, quantity, prevTitle, prevCats));

        LandManager land = ui.game.getLandManager();
        double needed = ui.game.landNeededFor(selected, quantity);
        double have = land.getAvailableSqFt();
        double short_ = Math.max(needed - have, 0);
        double blocks = Math.ceil(short_ / LandManager.BLOCK_SQ_FT);

        Label warning = new Label("NOT ENOUGH LAND");
        warning.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label details = new Label(String.format(
                "%,d x %s needs %s sq ft%n"
                        + "The city has %s sq ft free%n"
                        + "Short by %s sq ft - about %s",
                quantity, selected.getName(), formatter.format(needed),
                formatter.format(have), formatter.format(short_),
                LandManager.km2Words(short_)));

        Label cost = new Label(String.format(
                "Buying %s costs roughly %s",
                LandManager.km2Words(blocks * LandManager.BLOCK_SQ_FT),
                money(land.getNextBlockCost() * blocks)));
        cost.setStyle("-fx-text-fill: #8fa3b0;");

        Button toLand = new Button("Go to the Land Office");
        toLand.setOnAction(e -> ui.landScreen.showLandMenu());

        Button back = new Button("Back");
        back.setOnAction(e -> handleAllBuildingMenus(prevTitle, prevCats));

        ui.rootMenu.getChildren().addAll(warning, details, cost, toLand, back);
    }

    /**
     * "You cannot afford this - borrow for it?" with the terms on the screen.
     *
     * NOTE: this used to gross the gap up itself using getRate(), the standing
     * rate, and hand the resulting face value to the emergency note (gone
     * since 0.7.0) - which then grossed it up a second time off the same
     * stale rate. The quote does both now, priced with the bill included, and
     * the button books precisely what is printed above it.
     *
     * TWO OFFERS SINCE 0.7.10: a bond for Game.BUILD_BOND_YEARS beside the
     * note, each sized so the cash it brings covers the gap, each with its
     * rate, its face, its cash, what it costs a month and in all, and what
     * happens at the end. Every figure is the model's - the gap is
     * Game.buildFundingGap(), the rest is on the two quotes - and each button
     * books exactly the quote printed above it.
     */
    void showQuickDebtMenu(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats) {
        ui.clearMenu("showQuickDebtMenu", () -> showQuickDebtMenu(selected, quantity, prevTitle, prevCats));

        double gap = ui.game.buildFundingGap(selected, quantity);

        // Matching what the button below books - the SAME duration, not just the
        // same method. This quoted a 3-month bill while the button booked the
        // 6-month emergency note, and quoteTBill() discounts by duration, so the
        // price on screen was not the price paid. The emergency note itself is
        // gone (0.7.0: the central bank advances a broke treasury); this is the
        // screen's own note on Game.BUILD_NOTE_MONTHS.
        DebtQuote note = ui.game.quoteTBill(gap, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
        DebtQuote bond = ui.game.quoteLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);

        Label warning = new Label("INSUFFICIENT FUNDS");
        warning.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        VBox need = new VBox(0,
                statementLine("Funding required", money(gap), Palette.BAD),
                statementNote(String.format("%,d x %s costs %s, and the treasury holds %s.",
                        quantity, selected.getName(),
                        money(ui.game.calculateTotalCost(selected, quantity)),
                        money(ui.game.getCash()))));

        /*
         * THE BOND FIRST, because it is the offer this page recommends. What
         * the page sells is always a building, and a building outlives either
         * loan: the matching principle says a long-lived asset is paid for with
         * long-lived debt, so the people who use it over the years pay for it
         * over the years. The note asks for the whole face back in six months,
         * out of a treasury that was short of the price to begin with - on the
         * D$100M founding (0.7.10) a water plant bought that way left the
         * treasury D$16.2M overdrawn when the note matured, ten months on the
         * central bank's advances, where on this bond it never fell below
         * D$0.5M and never touched them. The note stays, second, for a gap
         * the next six months' revenue will cover, and both offers print their
         * whole cost so the player can weigh six months' credit against
         * twenty years'.
         */
        VBox bondOffer = fundingOffer(Game.BUILD_BOND_YEARS + "-year bond", bond,
                pct2(bond.marketRate()) + " yield  ·  " + pct2(bond.couponRate()) + " coupon",
                String.format("Paid over %d years: the coupon every month, then the whole %s at the end.",
                        bond.duration(), money(bond.faceValue())),
                "Issue the " + Game.BUILD_BOND_YEARS + "-year bond",
                () -> {
                    ui.game.handleLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);   // quotes it again, identically
                    buildOnTheLoan(selected, quantity, prevTitle, prevCats, "bond");
                });

        VBox noteOffer = fundingOffer(Game.BUILD_NOTE_MONTHS + "-month note", note,
                pct2(note.marketRate()) + " a year, taken as a discount",
                String.format(ui.game.getRollover().getMode() == Rollover.Mode.MANUAL
                                ? "Falls due in %d months: the whole %s at once, out of the treasury."
                                : "Falls due in %d months: the whole %s at once, refinanced then by the treasury's rollover.",
                        note.duration(), money(note.faceValue())),
                "Issue the " + Game.BUILD_NOTE_MONTHS + "-month note",
                () -> {
                    ui.game.handleTBillLogic(gap, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);   // quotes it again, identically
                    buildOnTheLoan(selected, quantity, prevTitle, prevCats, "note");
                });

        Button cancel = new Button("Cancel Build");
        cancel.setOnAction(e -> handleAllBuildingMenus(prevTitle, prevCats));

        ui.rootMenu.getChildren().addAll(warning, need, bondOffer, noteOffer, cancel);
    }

    /**
     * One of the funding page's offers: its rate, its face, the cash it
     * brings, what it costs a month and in all, and what happens at the end -
     * every figure off the quote - then what asking this much does to the
     * city's rate, and its button.
     */
    VBox fundingOffer(String name, DebtQuote quote, String rate, String atTheEnd,
                      String action, Runnable issue) {
        return fundingOffer(name, quote, rate, atTheEnd, action, issue, Money::money);
    }

    /**
     * ...with its figures written by `written` - the land office's dollar
     * offers (0.7.13) print theirs in US dollars, since a dollar quote's
     * every figure is in dollars (Game.quoteForeign()). Package-private since
     * 0.7.13, so the land office's funding page is this page's pieces and not
     * a second copy of them.
     */
    VBox fundingOffer(String name, DebtQuote quote, String rate, String atTheEnd,
                      String action, Runnable issue, java.util.function.DoubleFunction<String> written) {

        Label impact = new Label(quote.creditImpact());
        impact.setStyle(rateStyle(quote));

        Button go = new Button(action);
        go.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                + " -fx-background-color: " + Palette.CONFIRM + ";");
        go.setOnAction(e -> issue.run());

        VBox offer = new VBox(0,
                statementHead(name),
                statementLine("Rate", rate),
                statementLine("Face - what the city owes", written.apply(quote.faceValue())),
                statementLine("Cash it brings", written.apply(quote.cashReceived()), Palette.GOOD),
                statementLine("Monthly cost", quote.monthlyInterest() > 0
                        ? written.apply(quote.monthlyInterest()) + " a month"
                        : "none - it pays no coupon"),
                statementLine("Cost of the credit, all in", written.apply(quote.totalCost())),
                statementNote(atTheEnd),
                impact,
                go);
        return offer;
    }

    /**
     * Either offer's money is in: the order is placed again, and whatever it
     * answers is shown.
     *
     * THE RESULT IS LOOKED AT NOW, and that is the more important half of
     * this fix.
     *
     * This line used to be a bare call. The note was issued, the build was
     * asked for, and whatever it answered was thrown away - so when the
     * proceeds came up a few hundred short of the price (see
     * Game.faceForNetProceeds) the city took on debt, built nothing, and
     * returned to a menu that said nothing at all. Jerus found it in play:
     * "the tbill is inacted but the roads are not built and you are just
     * left with the cash unspent."
     *
     * The sizing bug is fixed, so the last branch should now be
     * unreachable - for the bond too (Game.quoteLongBondForCash). It stays
     * anyway. A refusal that is not read is a refusal that is silent, and
     * silence is what made a plain arithmetic error look like a mystery.
     */
    private void buildOnTheLoan(BuildingsTemplate selected, int quantity,
                                String prevTitle, EnumSet<BuildingType> prevCats, String paper) {
        switch (ui.game.buildStack(selected, quantity, false)) {
            case SUCCESS    -> handleAllBuildingMenus(prevTitle, prevCats);
            case NO_LAND    -> showNoLandMenu(selected, quantity, prevTitle, prevCats);
            case NO_DEPOSIT -> showNoDepositMenu(selected, quantity, prevTitle, prevCats);
            case NO_LICENCE -> showNoLicenceMenu(selected, quantity, prevTitle, prevCats);
            // NOT back to the funding page. Re-offering a loan to a city that
            // has just taken one and is still short would loop the player
            // through the same button forever, borrowing every time.
            default         -> showFundingFellShortMenu(selected, quantity, prevTitle, prevCats, paper);
        }
    }

    /**
     * The loan went through and the building still did not.
     *
     * Should be unreachable. It exists because the state it describes - debt on
     * the books, nothing built, cash sitting in the treasury - is a state the
     * player CAN end up in and could not previously be told about, and a screen
     * that says what happened is worth more than an assertion that it cannot.
     *
     * @param paper which of the page's two offers was taken, "note" or "bond"
     */
    void showFundingFellShortMenu(BuildingsTemplate selected, int quantity,
                                          String prevTitle, EnumSet<BuildingType> prevCats, String paper) {
        ui.clearMenu("showFundingFellShortMenu", () -> showFundingFellShortMenu(selected, quantity, prevTitle, prevCats, paper));

        Label heading = new Label("THE MONEY IS IN, THE BUILDING IS NOT");
        heading.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold; -fx-font-size: 14px;");

        double price = ui.game.calculateTotalCost(selected, quantity);

        Label what = new Label(String.format(
                "The " + paper + " was issued and the cash is in the treasury, but %d x %s"
                + " still costs more than the city is holding.%n%n"
                + "  Price now      %s%n"
                + "  Cash on hand   %s%n"
                + "  Still short    %s%n%n"
                + "Nothing was built and nothing beyond the " + paper + " was spent. Order a"
                + " smaller batch, or borrow again from the finance screen where you"
                + " can choose the size yourself.",
                quantity, selected.getName(),
                money(price), money(ui.game.getCash()),
                money(ui.game.buildFundingGap(selected, quantity))));
        what.setWrapText(true);
        what.setMaxWidth(460);
        what.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");

        Button back = new Button("Back to the build menu");
        back.setOnAction(e -> handleAllBuildingMenus(prevTitle, prevCats));

        ui.rootMenu.getChildren().addAll(heading, what, back);
    }

    /**
     * Colours a quoted rate by how punishing it is.
     *
     * Not decoration. The whole point of showing the quote is that a player can
     * see they are being charged for the size of the ask, and a number that
     * looks the same at 1% and at 20% does not communicate that at a glance.
     */
    String rateStyle(DebtQuote quote) {

        // Measured against the market's OWN band rather than typed-in numbers,
        // so re-shaping the curve cannot leave this colouring behind. When the
        // spread ran 1%-20% a flat "red above 15%" was about right; the moment
        // the curve was made gentler the same thresholds would have painted
        // ordinary municipal leverage green and nothing else anything at all.
        DebtManager market = ui.game.getDebtManager();
        double floor = market.floorRate();
        double span = Math.max(1e-9, market.ceilingRate() - floor);
        double howFarUp = (quote.marketRate() - floor) / span;

        String colour;
        if (howFarUp >= .55)      colour = "#ff6b6b";   // deep into the expensive half
        else if (howFarUp >= .25) colour = "#ffb454";   // getting dear
        else                      colour = "#5fd68a";   // ordinary money
        return "-fx-text-fill: " + colour + "; -fx-font-weight: bold; -fx-padding: 4 0 0 0;";
    }
}
