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
 * The build tab: the Overview it opens on and the city's five categories
 * opened on their needs (both 0.7.24), the market's nine in their groups
 * (0.7.25), the strip of categories, the constraints bar that says what
 * stops a build, the line that says who builds these, and every building as
 * the one card (0.7.25) - its head, hero, bars, investors' line, needs,
 * order line and stats - with the stat card's own vocabulary for what a
 * building does and what care it gives.
 *
 * Split out of UserInterface on 2026-09-18, the shell's members reached
 * through ui: BUILD: THE CATEGORY SCREEN IS GONE TOO, THE HALF OF THE
 * CATALOGUE THAT BUILDS ITSELF, THE BUILD MENU, ONE BUILDING, AS A CARD and
 * THE STAT CARD came over then; THE OVERVIEW and A CITY CATEGORY, OPENED ON
 * ITS NEEDS joined them in 0.7.24, and A MARKET CATEGORY, IN ITS GROUPS in
 * 0.7.25, when ONE BUILDING, AS A CARD became the one card for every
 * building. The shell still reads
 * which category is open (buildCategory) for the rail and the scroll memory,
 * and the inbox and the panels send the player into a category through
 * openCategory(), which lands in handleAllBuildingMenus(). BuildMenuCheck
 * reads the three card methods, and since 0.7.24 the pages through the
 * window's buildPages() and buildOpensOn().
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
       worth a click. The strip said it by putting them last; since 0.7.24
       the city's five come first (buildCategories()).
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
     * The strip, since 0.7.24 in BuildAdvice's order: the five only the city
     * builds - Utilities, Roads & transit, Healthcare, Education, Safety -
     * then the nine investors build too - Homes, Shops, Industry, Offices,
     * Farms, Rail, Vehicles, Luxury shops, Restaurants - under the names
     * Jerus chose ("Yes, rename them": Residential, Commercial, Industrial,
     * Infrastructure and Services until then). The list lives in the model
     * (BuildAdvice.categories()) so the harness can hold that every building
     * sits in exactly one.
     *
     * It ran in the order a city is built until 0.7.24, homes first. Jerus:
     * "when you start the game you start in residential so the player
     * without reading thinks he needs to building houses" - so the city's
     * own works come first, and Build opens on its Overview (BUILD_HOME).
     *
     * Several have a row of their own for a reason the old strip gave
     * beside each: Offices (business services) because its customer is not
     * in the city; Farms because a farm competes with the whole
     * neighbourhood; Rail because its product is a price on every other
     * screen; Vehicles because an assembly plant cannot run on imported
     * parts; Luxury shops and Restaurants because they exist so a rich city
     * has somewhere to spend (BuildingType's notes on each).
     */
    static BuildCategory[] buildCategories() {
        List<BuildAdvice.Category> all = BuildAdvice.categories();
        BuildCategory[] out = new BuildCategory[all.size()];
        for (int i = 0; i < out.length; i++) out[i] = new BuildCategory(all.get(i).name(), all.get(i).types());
        return out;
    }

    /**
     * Where Build opens (BUILD_HOME), and which category the player was last
     * looking at (buildCategory).
     *
     * Remembered because Build is a tab now rather than a screen you arrive at
     * from somewhere: a player who is halfway through putting up a school and
     * goes to check the land price should come back to the schools, not to
     * the front of the catalogue - so within a session Build returns to the
     * category the player was last in (0.7.20; Jerus's play-through came back
     * to Residential every time).
     *
     * THE OVERVIEW IS WHERE IT STARTS (0.7.24): a fresh start, a new city and
     * a load open Build on the Overview (UserInterface.anotherCity() puts it
     * back), and the Overview is a place the player can be "last in" too.
     */
    static final String BUILD_HOME    = BuildAdvice.OVERVIEW;
    String buildCategory = BUILD_HOME;

    /** The Build tab: the Overview, or whichever category you were last in. */
    void showBuildMenu() {
        openCategory(buildCategory);
    }

    /**
     * A category, by its name - or by its label before 0.7.24, which lands in
     * the same place - or the Overview. Arriving from anywhere (the strip, the
     * Overview's tiles, NEEDS YOU's rows, a refusal's way to the schools) the
     * page opens on its worst measure, not the one picked when the player was
     * last there (A CITY CATEGORY, OPENED ON ITS NEEDS).
     */
    void openCategory(String name) {
        BuildAdvice.Category c = BuildAdvice.category(name);
        if (c == null) {
            showOverview();
            return;
        }
        measurePicked.remove(c.name());
        handleAllBuildingMenus(c.name(), c.types());
    }

    /**
     * A city category opened on one ring (0.7.28): the Services screen's
     * "Build for it ›" doors - opened as openCategory() opens it, with this
     * measure picked rather than its worst. The burial plots are the death
     * care ring; a measure no city category has opens the Overview.
     */
    void openOn(BuildAdvice.Measure m) {
        BuildAdvice.Category c = m == null ? null : BuildAdvice.category(m.category());
        if (c == null) {
            showOverview();
            return;
        }
        measurePicked.put(c.name(), m.kind() == BuildAdvice.Kind.PLOTS ? BuildAdvice.Measure.of(BuildAdvice.Kind.DEATH) : m);
        showCityCategory(c);
    }

    /**
     * ...and the way back to the why (0.7.28): the "why ›" at a ring's
     * heading opens the Services page that explains it - the road and
     * transit rings, Infrastructure's, behind "what is on the road" and
     * "who rides" since 0.7.29.
     */
    void why(BuildAdvice.Measure m) {
        switch (m.kind()) {
            case ROADS:   ui.infrastructureScreen.open("Roads");   return;
            case TRANSIT: ui.infrastructureScreen.open("Transit"); return;
            default: {
                String[] at = ServicesScreen.pageFor(m);
                ui.servicesScreen.open(at[0], at[1]);
            }
        }
    }

    /**
     * The strip itself.
     *
     * A FlowPane rather than an HBox, because seven categories at this width fit
     * on one line on a maximised window and wrap rather than clip on a small
     * one - and the one thing this strip must never do is hide a category.
     *
     * A DOT, NOT AN ORANGE WORD (0.7.21). The investors' categories were
     * written in amber, which said "investors build these too" to nobody: amber
     * is a verdict colour, and nothing on the page said what it meant here.
     * Each tab carries a small dot now - blue for the city's own, violet
     * (the business colour) for what investors build too - and the key sits at
     * the right of the page's title (keyLine()). The open tab is raised, with
     * the building colour's underline.
     */
    /*
     * SINCE 0.7.24 it shows on a category page only, not on the Overview,
     * and reads "‹ Overview", then the city's five with their blue dots, a
     * rule, then the market's nine with their violet ones, left-aligned under
     * the page's head as the round-2 mockups draw it.
     */
    javafx.scene.layout.FlowPane buildStrip(String current) {

        javafx.scene.layout.FlowPane strip = new javafx.scene.layout.FlowPane(6, 6);
        strip.setAlignment(Pos.CENTER_LEFT);
        strip.prefWrapLengthProperty().bind(ui.menuScroller.widthProperty().subtract(60));
        strip.setStyle("-fx-padding: 0 18 4 18;");

        Button overview = new Button("‹ Overview");
        overview.setGraphic(icon(Icons.OVERVIEW, Palette.TEXT_LABEL, 13));
        overview.setGraphicTextGap(6);
        overview.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_LABEL)
                + " -fx-background-color: transparent; -fx-border-color: transparent;"
                + " -fx-padding: 6 10 6 6; -fx-cursor: hand;");
        overview.setTooltip(new Tooltip("The city's job, what would help most, and what the market builds"));
        overview.setOnAction(e -> showOverview());
        strip.getChildren().add(overview);

        boolean ruled = false;
        for (BuildCategory category : buildCategories()) {
            boolean on = category.name().equals(current);
            if (investorBuilt(category.types()) && !ruled) {
                // The rule between the city's five and the market's nine.
                Region rule = new Region();
                rule.setMinSize(1, 18);
                rule.setPrefSize(1, 18);
                rule.setMaxSize(1, 18);
                rule.setStyle("-fx-background-color: " + Palette.EDGE + ";");
                strip.getChildren().add(rule);
                ruled = true;
            }
            /*
             * THE DOT SAYS WHO BUILDS IT, open or not - a legend that only
             * appears once you are inside is a legend that never answers
             * "which of these do I actually have to do".
             */
            boolean theirs = investorBuilt(category.types());
            Button tab = new Button(category.name());
            tab.setGraphic(whoDot(theirs));
            tab.setGraphicTextGap(6);
            tab.setStyle(Palette.words(Palette.SIZE_BODY, on ? Palette.TEXT_HEAD : Palette.TEXT_LABEL)
                    + " -fx-background-color: " + (on ? Palette.PINNED : "transparent") + ";"
                    + " -fx-background-radius: " + Palette.RADIUS + ";"
                    + " -fx-border-color: " + (on ? Palette.BUILDING : "transparent") + ";"
                    + " -fx-border-width: 0 0 2 0; -fx-padding: 6 10 6 10; -fx-cursor: hand;");
            Tooltip tip = new Tooltip(theirs
                    ? "Investors build these themselves. You can build them too."
                    : "Nobody builds these but the city.");
            tip.setShowDelay(Duration.millis(250));
            tab.setTooltip(tip);
            tab.setOnAction(e -> openCategory(category.name()));
            strip.getChildren().add(tab);
        }
        return strip;
    }

    /** The colour of "only the city builds these": the money blue - the city's own account. */
    static final String CITY_DOT = Palette.MONEY;

    /** The colour of "investors build these too": the business violet. */
    static final String INVESTOR_DOT = Palette.BUSINESS;

    /** A tab's dot: who builds what is behind it (0.7.21). */
    static Region whoDot(boolean investors) {
        Region dot = new Region();
        dot.setMinSize(6, 6);
        dot.setPrefSize(6, 6);
        dot.setMaxSize(6, 6);
        dot.setStyle("-fx-background-color: " + (investors ? INVESTOR_DOT : CITY_DOT)
                + "; -fx-background-radius: 3;");
        return dot;
    }

    /** The key to the dots, at the right of the page's title: "● only the city builds  ● investors build too". */
    HBox keyLine() {
        Label city = new Label("only the city builds");
        city.setGraphic(whoDot(false));
        city.setGraphicTextGap(6);
        city.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        Label them = new Label("investors build too");
        them.setGraphic(whoDot(true));
        them.setGraphicTextGap(6);
        them.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        HBox key = new HBox(Palette.GAP_LOOSE, city, them);
        key.setAlignment(Pos.CENTER_RIGHT);
        key.setMinWidth(Region.USE_PREF_SIZE);
        return key;
    }

    /**
     * The page's head (0.7.21): its title with the building area's swatch,
     * the key to the tabs' dots at the right, the way to the construction page
     * (0.7.22), and the receipt's dot beyond it.
     * The page had no title before; the strip was its first line.
     *
     * "Build", and on a category "Build › Healthcare" since 0.7.24, the
     * mockups' title - "Build" a way back to the Overview.
     */
    HBox buildHead(String menuTitle, EnumSet<BuildingType> categories) {
        Label title = ui.pageTitle("Build");
        HBox titled = new HBox(10, title);
        titled.setAlignment(Pos.CENTER_LEFT);
        if (!BuildAdvice.OVERVIEW.equals(menuTitle)) {
            title.setStyle(title.getStyle() + " -fx-cursor: hand;");
            title.setOnMouseClicked(e -> showOverview());
            Label sep = new Label("›");
            sep.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_MUTED) + " -fx-padding: 8 0 2 0;");
            Label where = new Label(menuTitle);
            where.setStyle(Palette.strong(Palette.SIZE_TITLE, Palette.TEXT_HEAD) + " -fx-padding: 8 0 2 0;");
            titled.getChildren().addAll(sep, where);
        }
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        // ...and the way to the construction page (0.7.22): what is on site, and the hand on it.
        Label sites = stepChip("Construction ›", () -> ui.constructionScreen.show(), true);
        HBox head = new HBox(Palette.GAP_LOOSE, titled, gap, keyLine(), sites, receiptCorner(menuTitle, categories));
        // ...and, while it is on, automatic building's chip (0.7.73): the way to its cards on the Overview.
        if (ui.game.getAutoBuilder().isOn() && !BuildAdvice.OVERVIEW.equals(menuTitle)) {
            Label auto = stepChip(AUTO_CHIP, this::showOverview, true);
            auto.setStyle(auto.getStyle() + " -fx-text-fill: " + Palette.GOOD + ";");
            head.getChildren().add(head.getChildren().indexOf(sites), auto);
        }
        head.setAlignment(Pos.CENTER_LEFT);
        head.setMaxWidth(Double.MAX_VALUE);
        head.setStyle("-fx-padding: 0 18 4 18;");
        return head;
    }


    /* =====================================================================
       THE BUILD MENU

       The catalogue screen itself: where a page's name lands
       (handleAllBuildingMenus()), the constraints bar across the top and the
       line that says who builds these. The 0.7.21 page's grid of tiles was
       drawn here too; since 0.7.24 and 0.7.25 the pages are drawn under THE
       OVERVIEW and the two CATEGORY banners below, on the card of ONE
       BUILDING, AS A CARD. It sat inside BORROW, where it landed when the
       build menu was rewritten around the debt screen's tiles; it is its own
       section since 2026-09-18 so that the interface split can file it with
       the cards, not the loans.
       ===================================================================== */

    /**
     * A Build page by its name: the Overview, one of the city's five opened
     * on its needs (showCityCategory()), or one of the market's nine in its
     * groups (showMarketCategory(), 0.7.25). Every way into Build lands here - the strip, the
     * inbox, NEEDS YOU, a refusal's Back, an order placed - and a label from
     * before 0.7.24 lands where it always did, under its new name.
     */
    void handleAllBuildingMenus(String asked, EnumSet<BuildingType> categories) {
        if (asked == null || BuildAdvice.OVERVIEW.equals(asked)) {
            showOverview();
            return;
        }
        BuildAdvice.Category known = BuildAdvice.category(asked);
        if (known != null && known.cityBuilds()) {
            showCityCategory(known);
            return;
        }
        if (known == null) {
            // A name that is no category - nothing passes one since 0.7.24 -
            // lands on the Overview rather than on an empty page.
            showOverview();
            return;
        }
        showMarketCategory(known);
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
     * The private line says - behind its (i), since 0.7.21 - what building
     * one anyway actually DOES, because
     * "you don't need to" on its own reads as "don't", and that is not true
     * either: an investor who is broke, cautious or short of land will not move
     * on a shortage the city can see, and paying for the first block out of the
     * treasury is a real and sometimes correct policy.
     *
     * ONE SHORT LINE AND AN (i) (0.7.21). It was a paragraph, in amber; the
     * paragraph is the (i)'s now, in the mockups' words (TextLayers.dc.html),
     * and the line is grey - it is not a warning.
     */
    HBox whoBuildsThis(String menuTitle, EnumSet<BuildingType> categories) {

        boolean theirs = investorBuilt(categories);

        HBox note = theirs
                ? infoLine("Investors build these on their own. Building one yourself is optional.",
                        "Investors put these up whenever they pay. Building one yourself spends the "
                        + "city's cash and land on a business someone else runs and keeps the earnings "
                        + "from. Worth it when investors are too broke, too cautious or short of land.",
                        true, Palette.SIZE_BODY, Palette.TEXT_LABEL, Palette.BUILD_ROW + 120)
                : infoLine("Only the city builds these.",
                        "Nobody builds these but the city. However badly they are needed, no "
                        + "investor will put one up.",
                        false, Palette.SIZE_BODY, Palette.TEXT_LABEL, Palette.BUILD_ROW + 120);
        note.setStyle("-fx-padding: 6 0 2 0;");
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
     * bar of four grey numbers is a bar nobody reads, and a red one on a city
     * with no ground free is the warning that used to arrive as a rejection.
     * (Red from 95% of the land used until 0.7.26, which a city sits at for
     * centuries with nothing wrong; the free ground's own line since.)
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
                // Coloured by NEEDS YOU's GROUND row since 0.7.26 (CityNeeds.ground(): a
                // block free or less amber, none red), not by the share used, which a city
                // sits at 95-100% of for centuries with nothing wrong - it read 51.0M sq ft
                // free in red. The land office and the left panel take the same row.
                landFreeCell(free, used),
                lastCell);
        return bar;
    }

    /* ----------------------------- THE BUILD SHORTCUT (0.7.61, spec-land star 14) -----------------------------
     *
     * LAND FREE says the ground free in km2 (sq ft until 0.7.61; under a
     * hundredth of one in m2 since 0.7.68, LandManager.areaWords()) and gains
     * "Buy the best: North 3 · 3.4 km² · US$12.1M ›" (the offer's place
     * since 0.7.69, as the land office's rows and the refusal pages name it;
     * "Buy the best land · ..." until then), and the refusal pages each
     * gain a Buy for what they are short of - each Game.bestOffer() of its
     * need: for room, the most dry ground a dollar among the offers the city
     * can afford that are not mostly sea; on the no-land page the cheapest
     * offer of BARE GROUND that covers the shortfall (0.7.58, batch J1c: a
     * building short of ground has not asked for ore, so an offer holding a
     * priced resource is passed over - the button says so - and with none big
     * enough, the best value, bought again until it is covered); on the
     * no-deposit page the cheapest offer holding the deposit (0.7.64; the most
     * iron sites a dollar until then); on the no-coast page the
     * cheapest offer with sea. Bought as the land office's Buy buys it, paid
     * the way its toggle says, or - short - the land office's funding page,
     * sized to that offer.
     * ------------------------------------------------------------------------------------------------------- */

    /** LAND FREE's width with its shortcut under it: the cell's own 190 and room for the shortcut's words on one line. */
    static final double LAND_FREE_CELL = 236;

    /** LAND FREE: the ground free in km2, the share used, a door to the land office, and the shortcut to buy the best land. */
    VBox landFreeCell(double free, double used) {
        VBox cell = limitCell("LAND FREE", LandManager.areaWords(free), String.format("%.0f%% of the city used", used * 100),
                groundTone(CityNeeds.ground(ui.game, SummaryScreen.WORDS).level()),
                "Go to the land office and buy more", () -> ui.landScreen.showLandMenu());
        LandParcel best = ui.game.bestOffer(Game.LandNeed.room());
        if (best == null) return cell;
        boolean funding = ui.game.landNeedsFunding(List.of(best.getId()));
        Label shortcut = new Label(bestLandWords(best));
        shortcut.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.BUILDING) + " -fx-cursor: hand; -fx-padding: 2 0 0 0;");
        shortcut.setMaxWidth(LAND_FREE_CELL - 28);
        shortcut.setOnMouseClicked(e -> {
            e.consume();
            buyBestLand(best, this::showBuildMenu);
        });
        Tooltip tip = new Tooltip(bestLandTip(best, funding));
        tip.setShowDelay(Duration.millis(250));
        Tooltip.install(shortcut, tip);
        cell.getChildren().add(shortcut);
        cell.setPrefWidth(LAND_FREE_CELL);
        return cell;
    }

    /** The shortcut's words: "Buy the best: North 3 · 0.0288 km² · US$12.1M ›" - the offer's place (0.7.69, spec-grid 2.5), its size and its listed price. */
    static String bestLandWords(LandParcel p) {
        return "Buy the best: " + p.where() + " · " + LandMap.area(p.getKm2()) + " · " + usd(p.getPriceUsd()) + " ›";
    }

    /** ...and its tooltip: which offer, why it is the best, and what a short city's click opens. */
    static String bestLandTip(LandParcel p, boolean funding) {
        return p.where() + ": the most dry ground a dollar the city can afford, not mostly sea - "
                + LandMap.area(p.getDryKm2()) + " dry." + (funding
                        ? "\nShort of the money: opens the land office's funding page, sized to it."
                        : "\nBought at once, paid the way the land office's toggle says.");
    }

    /** Buys an offer as the land office's Buy would, then `after`; short, the land office's funding page for it. */
    void buyBestLand(LandParcel p, Runnable after) {
        List<Integer> ids = List.of(p.getId());
        if (ui.game.landNeedsFunding(ids)) {
            ui.landScreen.showLandFunding(ids);
            return;
        }
        ui.game.buyLandParcels(ids);
        after.run();
    }

    /** A refusal page's Buy: Pieces' action button, outlined and "on credit" when the city is short. */
    Pieces.ActionButton bestLandButton(LandParcel p, String words, String sub, Runnable after) {
        boolean funding = ui.game.landNeedsFunding(List.of(p.getId()));
        Pieces.Press press = new Pieces.Press(funding ? Pieces.Look.CREDIT : Pieces.Look.GO, words,
                funding ? sub + " · short: ways to pay" : sub);
        Pieces.ActionButton b = actionButton(Icons.MAP, Palette.BUILDING, ACTION_TALL, press, () -> buyBestLand(p, after));
        b.setMaxWidth(Region.USE_PREF_SIZE);
        return b;
    }

    /** LAND FREE's colour: the GROUND row's verdict, amber or red, and the strip's plain figure while it is fine. */
    static String groundTone(int level) {
        return level >= 2 ? Palette.BAD : level == 1 ? Palette.WARN : Palette.TEXT_HEAD;
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

       ONE CARD FOR ALL 73 (0.7.25). Jerus, after seeing 0.7.24: "also the
       build card for every building, i think the card itself needs a
       redesign dont you think?" The market's nine had kept the 0.7.21 card
       - a name, two prices, the land and the running cost, a stepper - while
       the city's five had one that said what a building gives and set it
       beside its neighbours, and had lost the (i), the running cost and +100
       on the way. Every building has the one card now (the project's design
       note for 0.7.25), NEED_CARD wide and as tall as what it says:
         the head - an icon square in who-builds-it's colour, the name (faint
           when one costs more than the cash), "you have N" and what is on
           site with its wait, a link to the site;
         the tags - the best of its group on each bar;
         the hero - what it gives the city, in its own verb and unit, and a
           detail line under it;
         the price - all in, green or red against the cash, the sticker smaller;
         two bars - money per unit of the hero, and the category's scarce
           resource: land on the market's cards, the posts the city cannot
           fill on the city's, the posts it could not staff on an office's -
           scaled within its group, with no track in a group of one;
         the investors' line, on the market's cards;
         what it needs - its posts, the staffing, the land and how much of
           what is free, what it costs to run;
         the stepper and Build, and the quote with its verdict, which warns
           of the deposit and the licence before the click, in buildStack()'s
           order - Build the card's width since 0.7.34, its label the order
           (orderPress()).
       The figures are BuildCard's, which BuildCardCheck holds; this keeps the
       words and the layout. The (i) and its cover, the stepper, the reprice
       in place, the keyboard and placing the order are the 0.7.21 card's
       (the order placed as a run of one since 0.7.40, placeRun()).
       ===================================================================== */

    /** Which cards are showing their stats, by building name. */
    final java.util.Set<String> pinnedStats = new java.util.HashSet<>();

    /** How many of each the player has dialled up, by building name. */
    final java.util.Map<String, Integer> orderQty = new java.util.HashMap<>();

    /**
     * One building as a card, on a city category's page (its picked ring's
     * group) or a market category's (its owning sector's group).
     *
     * @param f     its figures (BuildCard.of())
     * @param group what it is compared within: its bars' scale and its tags
     * @param c     the category whose page it is on
     * @param m     the picked ring on a city page; null on a market page
     */
    StackPane card(BuildCard.Figures f, BuildCard.Group group, BuildAdvice.Category c, BuildAdvice.Measure m) {
        BuildingsTemplate template = f.template();
        String key = template.getName();
        boolean afford = f.price() <= ui.game.getCash();

        VBox face = new VBox(6);
        face.setStyle("-fx-padding: 10 12 10 12;");
        face.getChildren().add(cardHead(f, Icons.ofCategory(c.name()), f.market() ? INVESTOR_DOT : CITY_DOT, afford));
        javafx.scene.layout.FlowPane tags = cardTags(f, group);
        if (!tags.getChildren().isEmpty()) face.getChildren().add(tags);
        face.getChildren().addAll(heroRow(f), priceRow(f, afford));
        face.getChildren().addAll(cardBars(f, group, m));
        if (f.market()) face.getChildren().add(investorsLine(template));
        face.getChildren().add(needsLine(f));

        StackPane card = new StackPane();
        // The border turns the building colour while an order is pending.
        Runnable dress = () -> card.setStyle("-fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: "
                + (orderQty.getOrDefault(key, 0) > 0 ? Palette.BUILDING : Palette.EDGE) + ";");
        Order order = orderControls(template, key, c.name(), c.types(), dress);
        face.getChildren().addAll(order.steps(), order.build(), order.quoted());
        // ...and on the Gravel Road's, paving them (0.7.70; ConstructionControl, F).
        if (m != null && m.kind() == BuildAdvice.Kind.ROADS && ConstructionControl.paves(template) && f.owned() > 0) {
            VBox pave = pavingBlock(template, c.name(), c.types());
            if (pave != null) face.getChildren().add(pave);
        }

        VBox cover = statCover(template, f);
        cover.setVisible(pinnedStats.contains(key));
        Label info = infoDot(key, cover);
        card.getChildren().addAll(face, cover, info);
        StackPane.setAlignment(info, Pos.TOP_RIGHT);
        StackPane.setMargin(info, new javafx.geometry.Insets(8, 8, 0, 0));
        card.setPrefWidth(NEED_CARD);
        card.setMinWidth(NEED_CARD);
        card.setMaxWidth(NEED_CARD);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        pageCards.add(new PageCard(template, order.reprice()));
        order.reprice().run();
        return card;
    }

    /* ----- PAVING, ON THE GRAVEL ROAD'S CARD (0.7.70) ----- */

    /** How many gravel roads the card's paving is set to: apart from orderQty, so Enter and the order bar never place it. */
    int paveQty;

    /**
     * PAVE THEM (0.7.70; ConstructionControl, F). Jerus: "make it an option
     * to upgrade from gravel to paved ... and that the build menu allows and
     * recommends this if better". Under the Gravel Road card's Build: what
     * paving one does and costs (pavingWords()), the tag "paving beats a new
     * Paved Road" when it does over its life a trip - the advice's own test,
     * BuildAdvice.pavingBeatsPaved(), which BuildCard.paving() reads - and
     * the two figures either way; its
     * own stepper, up to the gravel roads there are to pave, and its button
     * (pavePress()), which pays out of the treasury and puts the paving on
     * the Paved Road site (Game.paveRoads()). Null with nothing to say.
     */
    VBox pavingBlock(BuildingsTemplate gravel, String page, EnumSet<BuildingType> types) {
        BuildCard.Paving p0 = BuildCard.paving(ui.game, gravel);
        if (p0 == null) return null;
        paveQty = Math.max(0, Math.min(paveQty, p0.paveable()));
        Label head = new Label(PAVE_HEAD);
        head.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.ACCENT) + " -fx-font-weight: bold; -fx-padding: 6 0 0 0;");
        String[] said = pavingWords(p0, paveQty);
        Label what = new Label(said[0]);
        what.setWrapText(true);
        what.setStyle(wordsAt(9.5, Palette.TEXT_LABEL));
        Label against = new Label(said[1]);
        against.setWrapText(true);
        against.setStyle(wordsAt(9.5, Palette.TEXT_MUTED));
        Tooltip.install(against, new Tooltip(PAVE_INFO));
        VBox block = new VBox(4, head);
        if (p0.beatsPaved()) block.getChildren().add(tag(PAVE_TAG, Palette.GOOD));
        block.getChildren().addAll(what, against);

        Label count = new Label("0");
        count.setMinWidth(30);
        count.setAlignment(Pos.CENTER);
        Label of = new Label(said[3]);
        of.setStyle(wordsAt(9.5, Palette.TEXT_MUTED));
        Label quoted = new Label(" ");
        quoted.setWrapText(true);
        quoted.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        Pieces.ActionButton pave = actionButton(Icons.BUILD, Palette.BUILDING, ACTION_TALL, pavePress(p0, 0), null);
        Runnable reprice = () -> {
            BuildCard.Paving p = BuildCard.paving(ui.game, gravel);
            paveQty = Math.max(0, Math.min(paveQty, p.paveable()));
            count.setText(String.valueOf(paveQty));
            count.setStyle(Palette.figure(Palette.SIZE_BODY, paveQty > 0 ? Palette.BUILDING : Palette.TEXT_HEAD));
            String[] words = pavingWords(p, paveQty);
            pave.show(pavePress(p, paveQty));
            quoted.setText(words[2]);
            quoted.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        };
        Button less = stepper("−", 26, () -> { paveQty = Math.max(0, paveQty - 1); reprice.run(); });
        Button more = stepper("+", 26, () -> { paveQty++; reprice.run(); });
        Button ten = stepper("+10", 36, () -> { paveQty += 10; reprice.run(); });
        Button all = stepper("all", 30, () -> { paveQty = Integer.MAX_VALUE; reprice.run(); });
        HBox steps = new HBox(4, less, count, more, ten, all, of);
        steps.setAlignment(Pos.CENTER_LEFT);
        pave.onPress(() -> {
            if (paveQty <= 0) { paveQty = 1; reprice.run(); return; }
            if (ui.game.paveRoads(paveQty)) {
                paveQty = 0;
                handleAllBuildingMenus(page, types);
            } else {
                quoted.setText("Not paved: " + ui.game.getLastHandRefusal() + ".");
                quoted.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.BAD));
            }
        });
        reprice.run();
        block.getChildren().addAll(steps, pave, quoted);
        return block;
    }

    /** The paving's heading on the Gravel Road card. */
    static final String PAVE_HEAD = "PAVE TO A PAVED ROAD";

    /** ...its tag, when paving beats a new Paved Road over its life a trip. */
    static final String PAVE_TAG = "paving beats a new Paved Road";

    /** ...and the (i) on its two figures: what it costs and why it may win. */
    static final String PAVE_INFO = "Paving lays a Paved Road on a gravel road's ground: a Paved Road's price, less the"
            + " gravel road's material, which goes into its bed, plus taking up the old surface - so a gravel road and its"
            + " paving cost more than a Paved Road built outright. The road carries its traffic until its paving opens, and"
            + " the ground a Paved Road does not need comes back free. Each figure is over the road's life, a trip it takes"
            + " off the road, its land in it, as the road cards' first bar.";

    /**
     * The paving's words (0.7.70), worked out without drawing them (a probe
     * reads them): {what one does and costs, its figure over its life
     * against a new Paved Road's, the quote's line for n, "of N to pave"}.
     */
    String[] pavingWords(BuildCard.Paving p, int n) {
        String does = "Each adds " + formatter.format(Math.round(p.unit())) + " trips off the road and frees "
                + LandManager.areaWords(p.frees()) + " · " + money(p.one().total) + " a road.";
        String against = "Over its life " + money(p.perTrip()) + " a trip, against " + money(p.pavedPerTrip())
                + " for a new Paved Road.";
        String quoted;
        if (n <= 0) {
            quoted = p.paving() > 0 ? formatter.format(p.paving()) + " being paved." : " ";
        } else {
            Game.BuildQuote q = ui.game.quotePave(n);
            quoted = money(q.total) + " with " + money(q.salesTax) + " sales tax  ·  " + atTodaysQueue(q.months)
                    + (p.paving() > 0 ? "  ·  " + formatter.format(p.paving()) + " being paved" : "");
        }
        String of = "of " + formatter.format(p.paveable()) + " to pave";
        return new String[] { does, against, quoted, of };
    }

    /**
     * The paving's button (0.7.70): "Pave" and "choose how many" with none
     * chosen; "Pave 3 · $85.1k" ready; held with nothing to pave, or short of
     * the cash - paving is paid out of the treasury (Game.paveRoads()). A
     * pure read.
     */
    Pieces.Press pavePress(BuildCard.Paving p, int n) {
        if (p.paveable() <= 0) return new Pieces.Press(Pieces.Look.HELD, "None to pave",
                p.paving() > 0 ? "every gravel road is being paved" : "no gravel road standing");
        if (n <= 0) return new Pieces.Press(Pieces.Look.CHOOSE, "Pave", "choose how many");
        Game.BuildQuote q = ui.game.quotePave(n);
        String words = "Pave " + formatter.format(n) + " · " + money(q.total);
        if (q.total > ui.game.getCash()) return new Pieces.Press(Pieces.Look.HELD, words,
                "short " + money(q.total - ui.game.getCash()) + ": paving is paid from the treasury");
        return new Pieces.Press(Pieces.Look.GO, words, null);
    }

    /** A run of words in a TextFlow, at a size and in a colour - so a line wraps where it must and never ends in "...". */
    static javafx.scene.text.Text textRun(String s, String font, double size, String colour) {
        javafx.scene.text.Text run = new javafx.scene.text.Text(s);
        run.setStyle((font == null ? "" : font + " ") + "-fx-font-size: " + size + "px;");
        run.setFill(javafx.scene.paint.Color.web(colour));
        return run;
    }

    /** ...and a TextFlow of them, as wide as a card's face. */
    static javafx.scene.text.TextFlow flow(javafx.scene.Node... runs) {
        javafx.scene.text.TextFlow line = new javafx.scene.text.TextFlow(runs);
        line.setMinWidth(0);
        line.setPrefWidth(NEED_CARD - 24);
        line.setMaxWidth(NEED_CARD - 24);
        return line;
    }

    /**
     * The head: an icon square in who-builds-it's colour - the strip's dots,
     * blue for the city's five, violet for the market's nine - the name, faint
     * when one costs more than the cash, "you have N" or "none built", and
     * what is on site for anyone's order with its wait (0.7.20), a link to the
     * site on the construction page (0.7.22). Room is left at the right for
     * the (i), which sits over the card.
     */
    HBox cardHead(BuildCard.Figures f, String svg, String dot, boolean afford) {
        BuildingsTemplate t = f.template();
        int owned = f.owned();
        Label name = new Label(t.getName());
        name.setWrapText(true);
        name.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, afford ? Palette.TEXT_HEAD : Palette.TEXT_FAINT));
        javafx.scene.text.Text have = textRun(owned > 0 ? "you have " + formatter.format(owned) : "none built",
                null, Palette.SIZE_LABEL, owned > 0 ? Palette.ACCENT : Palette.TEXT_LABEL);
        int onSite = f.onSite();
        javafx.scene.text.TextFlow haveLine = new javafx.scene.text.TextFlow(have);
        if (onSite > 0) {
            javafx.scene.text.Text coming = textRun(" · " + formatter.format(onSite) + " on site · "
                    + atTodaysQueue(f.siteMonths()), null, Palette.SIZE_LABEL, Palette.BUILDING);
            coming.setUnderline(true);
            coming.setCursor(javafx.scene.Cursor.HAND);
            coming.setOnMouseClicked(e -> ui.constructionScreen.showSite(ConstructionControl.keyOf(t)));
            haveLine.getChildren().add(coming);
        }
        haveLine.setMinWidth(0);
        VBox who = new VBox(1, name, haveLine);
        HBox.setHgrow(who, Priority.ALWAYS);
        who.setMinWidth(0);
        who.setPadding(new javafx.geometry.Insets(0, 20, 0, 0));
        HBox top = new HBox(10, iconSquare(svg, dot, 34, 18), who);
        top.setAlignment(Pos.TOP_LEFT);
        return top;
    }

    /**
     * The tags: the best of the group on each bar (BuildCard.Group.best1()
     * and best2()), in a flow so two never cut each other.
     */
    javafx.scene.layout.FlowPane cardTags(BuildCard.Figures f, BuildCard.Group group) {
        javafx.scene.layout.FlowPane tags = new javafx.scene.layout.FlowPane(6, 4);
        tags.setPrefWrapLength(NEED_CARD - 24);
        for (String words : tagWords(f, group)) tags.getChildren().add(tag(words, Palette.GOOD));
        return tags;
    }

    /** A road card's first tag (0.7.70): the cheapest a trip off the road over its life, its ground in it - BuildCard.ROAD_PER, shortened. */
    static final String ROAD_TAG = "cheapest per trip over its life";

    /** ...and what its land bar is per: a trip it takes off the road (BuildAdvice.unit()). */
    static final String ROAD_OFF = "trip off the road";

    /** The tags' words, for the bars the card is the best of: "cheapest per resident", "adds the most per $". */
    static List<String> tagWords(BuildCard.Figures f, BuildCard.Group group) {
        String one, two;
        switch (f.kind()) {
            // A road's (0.7.70): its cost over its life with its ground, and its ground, a trip it takes off the road.
            case CITY:   one = f.bar2Kind() == BuildCard.Bar2.LAND ? ROAD_TAG : "cheapest per " + f.perTag();
                         two = f.bar2Kind() == BuildCard.Bar2.LAND ? "least land per " + f.perTag()
                                 : "fewest unfilled posts per " + f.perTag(); break;
            case MAKER:  one = "adds the most per $"; two = "adds the most per m²"; break;
            case OFFICE: one = "exports the most per $"; two = "easiest to staff"; break;
            default:     one = "cheapest per " + f.perTag(); two = "least land per " + f.perTag();
        }
        List<String> out = new ArrayList<>();
        if (group.best1(f)) out.add(one);
        if (group.best2(f)) out.add(two);
        return out;
    }

    /**
     * The hero: what the building gives the city, in its own verb, a big
     * figure and its unit - "houses 252 residents", "makes 1,200 t of steel a
     * month", "puts 87 officers on the street · 120 fully staffed" - and a
     * smaller line under it (heroDetail()).
     */
    VBox heroRow(BuildCard.Figures f) {
        // A power plant's figure is kilowatts, written scaled with its unit (0.7.28).
        boolean kw = BuildCard.KILOWATTS.equals(f.words());
        javafx.scene.text.TextFlow line = flow(
                textRun(f.verb(), null, Palette.SIZE_LABEL, Palette.TEXT_MUTED),
                textRun(kw ? power(f.figure()) : shortOrWhole(f.figure()), Palette.Fonts.monoSemiBold(), Palette.SIZE_SECTION + 2, Palette.TEXT_HEAD),
                textRun(kw ? "" : f.words(), null, Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        if (f.kind() == BuildCard.Kind.CITY && f.unit() < f.built() - .5) {
            line.getChildren().add(textRun(" · " + (kw ? power(f.built()) : shortOrWhole(f.built())) + " fully staffed", null,
                    Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        }
        VBox hero = new VBox(2, line);
        String detail = heroDetail(f);
        if (!detail.isEmpty()) {
            Label more = new Label(detail);
            more.setWrapText(true);
            more.setStyle(wordsAt(9.5, Palette.TEXT_MUTED));
            hero.getChildren().add(more);
        }
        return hero;
    }

    /**
     * The hero's smaller line, by kind: a home's doors and who may live
     * behind them; the branch's capital; the builders' output; an office's
     * exports at the price of a seat; a maker's other goods, what it uses and
     * what it adds at today's prices - or that it adds nothing - and a mine's
     * deposit.
     */
    String heroDetail(BuildCard.Figures f) {
        BuildingsTemplate t = f.template();
        double[] d = f.detail();
        switch (f.kind()) {
            case HOMES:
                return "in " + formatter.format(d[0]) + (d[0] == 1 ? " home" : " homes") + " for "
                        + formatter.format(d[1]) + (d[2] > 0 ? " · adults only" : " · children welcome");
            case BRANCH:
                return "brings " + money(d[0]) + " of shareholders' capital";
            case POINTS:
                return "the builders make " + formatter.format(d[0]) + " pts a month";
            case PUMP:
                // A filling station (0.7.83): the pump price on today's wholesale.
                return d.length < 2 || !(d[0] > 0) ? "no petrol priced yet"
                        : "at " + unitPrice(d[0]) + " a litre on " + unitPrice(d[1]) + " wholesale";
            case BERTHS:
                // A sea terminal (0.7.86): the kind's month - what could go by sea, the berths, the share at sea.
                if (d.length < 3) return "";
                return "the city's · last month " + shortNumber(d[0]) + " t of it could go by sea; the berths take "
                        + shortNumber(d[1]) + " t, " + Math.round(d[2] * 100) + "% of it";
            case SLOTS:
                // An offshore platform (0.7.91): the city's shallow sea sites, the slots standing, the wells in them.
                if (d.length < 3) return "";
                return "on a field 150 m deep at most · the city's shallow sea sites " + formatter.format(d[0]) + ", "
                        + formatter.format(d[1]) + " slotted, " + formatter.format(d[2]) + " wells lifting";
            case PIPE:
                // A kilometre of crude pipeline (0.7.91): the kilometres standing, the fields piped, the shuttle's tonnes.
                if (d.length < 3) return "";
                return "a field's whole pipe ends its shuttle tankers · " + formatter.format(d[0]) + " km standing, "
                        + formatter.format(d[1]) + " field(s) piped, " + shortNumber(d[2]) + " t by tanker this month";
            case TANKS:
                // Oil storage (0.7.85): what is in store now, against what may be.
                if (d.length < 2) return "";
                return StrategicReserve.isReserve(t)
                        ? "the city's crude, filled and released on your order · " + shortNumber(d[0]) + " t held of "
                                + shortNumber(d[1]) + " t of room"
                        : "the refiners keep a month of their crude in it, their products the rest · "
                                + shortNumber(d[0]) + " t on hand of " + shortNumber(d[1]) + " t";
            case OFFICE:
                return f.addsNothing() ? "the world pays nothing for it at today's prices"
                        : "worth " + money(f.valueAdded()) + " a month at " + unitPrice(d[0]) + " a seat-month";
            case MAKER: {
                List<String> parts = new ArrayList<>();
                if (ham.citybuildersim.sectors.Refining.isConversionUnit(t)) {
                    // A conversion unit (0.7.80): the hero names its feed, and this what the feed becomes.
                    parts.add("into " + goodsList(ham.citybuildersim.sectors.Refining.madeBy(t)) + " from the crude units' run"
                            + (t.refineryUnit() == ham.citybuildersim.sectors.RefineryFlow.Kind.HYDROCRACKER
                            ? " and a reformer's hydrogen" : ""));
                } else if (ham.citybuildersim.sectors.Refining.isCrudeUnit(t)) {
                    // A crude unit (0.7.76): the hero names its crude, and this what the crude becomes.
                    parts.add(crudeUnitWords(ham.citybuildersim.sectors.Refining.madeBy(t)));
                } else {
                    java.util.Map<Good, Double> others = new java.util.LinkedHashMap<>(t.goodsMade());
                    others.remove(f.good());
                    if (!others.isEmpty()) parts.add("and " + goodsList(others));
                    if (!t.goodsUsed().isEmpty()) parts.add("from " + goodsList(t.goodsUsed()));
                }
                parts.add(f.addsNothing() ? "adds nothing at today's prices: its inputs cost more than it makes"
                        : "adds " + money(f.valueAdded()) + " a month at today's prices");
                if (d.length == 3 && t.isPlatformWell()) {
                    // ...a platform's well (0.7.91): a slot on a platform standing, not a deposit of its own; its oil the offshore pool's (0.7.93).
                    parts.add("needs a platform's slot: " + formatter.format(d[0]) + " standing, "
                            + formatter.format(d[1]) + " spoken for, " + shortNumber(d[2]) + " t under the sea");
                } else if (d.length == 3) {
                    parts.add("needs a deposit: the city owns " + formatter.format(d[0]) + ", "
                            + formatter.format(d[1]) + " spoken for, " + shortNumber(d[2]) + " t in the ground");
                }
                return String.join(" · ", parts);
            }
            default:
                return "";
        }
    }

    /**
     * What a crude unit's crude becomes, for its card's line (0.7.76): the
     * petrol and diesel the city burns, and the rest it ships, in litres -
     * "into 580,170 L of petrol and 1,402,078 L of diesel, and 7.7M L of four
     * products it ships". Off Refining.madeBy(), the model's slate.
     */
    static String crudeUnitWords(java.util.Map<Good, Double> made) {
        double petrol = made.getOrDefault(Good.PETROL, 0.0), diesel = made.getOrDefault(Good.DIESEL, 0.0), rest = 0;
        int others = 0;
        for (java.util.Map.Entry<Good, Double> e : made.entrySet()) {
            if (e.getKey() == Good.PETROL || e.getKey() == Good.DIESEL || !(e.getValue() > 0)) continue;
            rest += e.getValue();
            others++;
        }
        String words = "into " + formatter.format(Math.round(petrol)) + " L of petrol and "
                + formatter.format(Math.round(diesel)) + " L of diesel";
        if (others > 0) words += ", and " + shortNumber(rest) + " L of " + (others == 1 ? "one product" : others + " products")
                + " it ships";
        return words;
    }

    /**
     * What a refinery's conversion unit is, in sentences, before its products
     * (0.7.80, batch O4): its size and its feed, where the feed comes from,
     * what its kind needs or does that the others do not, and when it runs -
     * off the template and RefineryFlow's own figures.
     */
    static List<String> conversionUnitLines(BuildingsTemplate t) {
        ham.citybuildersim.sectors.RefineryFlow.Kind k = t.refineryUnit();
        List<String> out = new ArrayList<>();
        out.add(String.format("A %s-barrel-a-day %s: it takes %s litres of %s a month from the refinery's crude units, not"
                        + " from the market, and makes what follows of it.",
                formatter.format(Math.round(t.feedPerMonth() / ham.citybuildersim.sectors.RefineryFlow.LITRES_A_MONTH_PER_BARREL_A_DAY)),
                k.unitName().toLowerCase(), formatter.format(Math.round(t.feedPerMonth())), k.feed().words()));
        switch (k) {
            case REFORMER:
                out.add("It also makes the hydrogen a hydrocracker needs - the only unit that does.");
                break;
            case HYDROCRACKER:
                out.add(String.format("It needs hydrogen, which only a reformer makes: a barrel of heavy naphtha reformed treats"
                                + " %.2f of a barrel of gas oil here, and with no reformer running it runs nothing.",
                        ham.citybuildersim.sectors.RefineryFlow.HYDROGEN_MADE / ham.citybuildersim.sectors.RefineryFlow.HYDROGEN_USED));
                break;
            case ALKYLATION:
                out.add("Its feed is the cracked gas a cracking unit or a coker makes: with neither running it has none.");
                break;
            case COKER:
                out.add("Residue the refinery's diesel cannot cut into fuel oil is burned for nothing; a coker makes lighter"
                        + " products and coke of it, which is why heavy crude wants one.");
                break;
            case ASPHALT:
                out.add("It takes only the residue of heavy crude, from the city's own heavy fields: on imported crude it has none.");
                break;
            default:
                break;
        }
        out.add("It runs only while what it makes is worth more than its feed would fetch unworked, and the units that share"
                + " a stream take it widest margin first.");
        return out;
    }

    /** Goods and their counts: "1,320 t of iron ore", "5,000 kg of dairy and eggs and 1,400 kg of meat". */
    static String goodsList(java.util.Map<Good, Double> goods) {
        List<String> out = new ArrayList<>();
        for (java.util.Map.Entry<Good, Double> e : goods.entrySet()) {
            out.add(formatter.format(Math.round(e.getValue())) + BuildCard.goodWords(e.getKey()));
        }
        if (out.size() <= 1) return out.isEmpty() ? "" : out.get(0);
        return String.join(", ", out.subList(0, out.size() - 1)) + " and " + out.get(out.size() - 1);
    }

    /** The price all in, green when the cash covers it and red when it does not, with the sticker smaller. */
    HBox priceRow(BuildCard.Figures f, boolean afford) {
        Label price = new Label(money(f.price()));
        price.setStyle(Palette.figure(Palette.SIZE_SECTION, afford ? Palette.GOOD : Palette.BAD));
        price.setMinWidth(Region.USE_PREF_SIZE);
        Label allIn = new Label(" all in · sticker " + money(f.sticker()));
        allIn.setStyle(wordsAt(9.5, Palette.TEXT_MUTED));
        allIn.setWrapText(true);
        allIn.setMinWidth(0);
        HBox row = new HBox(0, price, allIn);
        row.setAlignment(Pos.BASELINE_LEFT);
        return row;
    }

    /**
     * The two bars. Bar 1 is money per unit of the hero - the price per
     * resident, per customer, per thousand meals, per tonne a month, per
     * patient - or for a maker and an office the price in months of what it
     * adds or exports. Bar 2 is the category's scarce resource: land per unit
     * on a market card, the posts the city likely cannot fill per 10,000
     * served on a city card (none for one with no posts), the posts the city
     * could not staff of every 100 on an office's. Both read "—" with no
     * track when there is nothing to price against.
     */
    List<javafx.scene.Node> cardBars(BuildCard.Figures f, BuildCard.Group group, BuildAdvice.Measure m) {
        double wide = NEED_CARD - 26;
        boolean track = group.track() && !f.addsNothing();
        String[] words = barWords(f, m);
        List<javafx.scene.Node> out = new ArrayList<>();
        VBox money = barRow(words[0], words[1], group.share1(f), Palette.MONEY, wide, null, track);
        // ...and what its money is struck at (0.7.45; the UI spec's 2.10), behind an (i) after its words.
        if (!money.getChildren().isEmpty() && money.getChildren().get(0) instanceof HBox head) {
            boolean road = f.kind() == BuildCard.Kind.CITY && f.bar2Kind() == BuildCard.Bar2.LAND;
            head.getChildren().add(1, infoButton(road ? roadBarInfo() : moneyBarInfo(), false));
        }
        out.add(money);
        // None for a city building with no posts: its needs line says so.
        if (words[2] == null) return out;
        boolean land = f.bar2Kind() == BuildCard.Bar2.LAND;
        out.add(barRow(words[2], words[3], group.share2(f), land ? Palette.BUILDING : Palette.PEOPLE, wide,
                land ? Icons.LAND : Icons.STAFF, track));
        return out;
    }

    /** The bars' labels and figures: {label 1, figure 1, label 2, figure 2}, label 2 null for no second bar. */
    static String[] barWords(BuildCard.Figures f, BuildAdvice.Measure m) {
        String label1, value1;
        if (f.inMonths()) {
            label1 = f.kind() == BuildCard.Kind.OFFICE ? "its price, in months of its exports" : "its price, in months of what it adds";
            value1 = Double.isFinite(f.bar1()) ? String.format("%.1f mo", f.bar1()) : "—";
        } else {
            label1 = "cost per " + f.per();
            value1 = Double.isFinite(f.bar1()) ? money(f.bar1()) : "—";
        }
        switch (f.bar2Kind()) {
            case LAND:
                return new String[] {label1, value1,
                        f.inMonths() ? "land per $1k it adds a month"
                                : f.kind() == BuildCard.Kind.CITY ? "land per " + ROAD_OFF : "land per " + f.per(),
                        landWords(f.bar2())};
            case UNSTAFFABLE:
                return new String[] {label1, value1, "posts the city couldn't staff, of 100",
                        Double.isFinite(f.bar2()) ? String.format("%.0f", f.bar2()) : "—"};
            case UNFILLED:
                return new String[] {label1, value1, "posts the city can't fill, per 10,000 " + BuildCard.perPlural(m),
                        perTenThousand(f.bar2())};
            default:
                return new String[] {label1, value1, null, null};
        }
    }

    /** The land a bar reads, kept in square feet, as every area reads since 0.7.68 (LandManager.areaWords(): "23.2 m²", "0.0743 km²"); a dash for none. */
    static String landWords(double sqFt) {
        if (!Double.isFinite(sqFt)) return "—";
        return LandManager.areaWords(sqFt);
    }

    /**
     * THE INVESTORS' LINE, on a market card: if investors have an order on
     * its site, that, with the count and the wait; otherwise the sector's own
     * word for the month (Game.getLastInvestment(), filed per sector, so it
     * can name another building) - and then, in amber, "this one:" and the
     * first gate this building fails for them now, unless the word already
     * names it (BuildCard.Investors). The word is not saved, so after a load
     * the line says nothing was recorded until a month runs. A click opens
     * the owner's Investors page, which keeps the rules it builds by;
     * the tooltip carries the whole word and the investors' estimate.
     */
    HBox investorsLine(BuildingsTemplate t) {
        BuildCard.Investors inv = BuildCard.investors(ui.game, t);
        String word = inv.word();
        String[] said = investorsWords(inv);
        double size = 9.5;
        javafx.scene.text.TextFlow line = new javafx.scene.text.TextFlow(
                textRun("Investors", Palette.Fonts.sansSemiBold(), size, Palette.BUSINESS),
                textRun(said[0], null, size, Palette.TEXT_MUTED));
        if (said[1] != null) line.getChildren().add(textRun(said[1], null, size, Palette.WARN));
        line.setMinWidth(0);
        HBox.setHgrow(line, Priority.ALWAYS);
        HBox row = new HBox(5, icon(Icons.SECTOR, Palette.BUSINESS, 12), line);
        row.setAlignment(Pos.TOP_LEFT);
        row.setStyle("-fx-cursor: hand;");
        Tooltip tip = new Tooltip((word.isEmpty() ? "Nothing recorded since the city was loaded or founded: a month has to run."
                : word) + "\nInvestors' estimate: " + money(inv.estimate()) + " a month to its owner."
                + "\nClick for " + inv.owner().key() + "'s Investors page.");
        tip.setWrapText(true);
        tip.setMaxWidth(420);
        tip.setShowDelay(Duration.millis(300));
        Tooltip.install(row, tip);
        row.setOnMouseClicked(e -> ui.sectorScreen.openSectorBooks(inv.owner(), "Investors"));
        return row;
    }

    /**
     * The line's words after "Investors": {what they are doing or last said,
     * " · this one: " and the gate - or null}.
     */
    String[] investorsWords(BuildCard.Investors inv) {
        String word = inv.word();
        String rest;
        if (inv.theirs()) {
            rest = " are building · " + formatter.format(inv.onSite()) + " on site · " + atTodaysQueue(inv.months())
                    + (inv.yoursToo() ? ", yours among them" : "");
        } else if (word.isEmpty()) {
            rest = ": nothing recorded since the city was loaded or founded";
        } else if (word.startsWith("Holding: ")) {
            rest = " holding: " + word.substring("Holding: ".length());
        } else {
            rest = " " + Character.toLowerCase(word.charAt(0)) + word.substring(1);
        }
        return new String[] {rest, inv.showOwn() ? " · this one: " + gateWords(inv.own()) : null};
    }

    /** A gate, in the words its own refusal uses: the deposit, the licence, the staffing test's why, landReason()'s land, the estimate's loss. */
    String gateWords(BuildCard.Gate gate) {
        switch (gate.kind()) {
            case DEPOSIT:
                return "no " + (gate.why() == null ? "iron" : gate.why()) + " deposit free (the city owns "
                        + formatter.format(gate.a()) + ", " + formatter.format(gate.b()) + " spoken for)";
            case LICENCE:
                return "needs " + formatter.format(Math.ceil(gate.a() - 1e-9)) + " spare " + jobPlural(gate.licence())
                        + "; the city has " + formatter.format(Math.floor(gate.b() + 1e-9));
            case STAFFING:
                return gate.why();
            case LAND:
                return "no land - needs " + LandManager.areaWords(gate.a()) + ", "
                        + LandManager.areaWords(gate.b()) + " free";
            default:
                return gate.a() > 0 ? "would lose " + money(gate.a()) + " a month" : "would make nothing";
        }
    }

    /**
     * What it needs: its posts by job - or "needs no staff" - then, on a
     * market card, the share of them the owner's staffing test says the
     * city could fill, below 99.5%; then its land, red with " - more than is
     * free" when it is, else the share of what is free from 1% up (the
     * 0.7.21 card's); then what it costs to run (BuildCard.runningCost()),
     * which for a city building is the treasury's bill.
     */
    Label needsLine(BuildCard.Figures f) {
        Label needs = new Label(needsWords(f));
        needs.setWrapText(true);
        needs.setStyle(wordsAt(9.5, f.template().getLandSqFt() > f.landFree() ? Palette.BAD : Palette.TEXT_MUTED));
        return needs;
    }

    /** ...its words. */
    String needsWords(BuildCard.Figures f) {
        BuildingsTemplate t = f.template();
        StringBuilder line = new StringBuilder();
        for (JobType job : JobType.values()) {
            int n = t.getJobs(job);
            if (n == 0) continue;
            if (line.length() > 0) line.append(" · ");
            line.append(formatter.format(n)).append(" ").append(n == 1 ? jobLabel(job) : jobPlural(job));
        }
        if (line.length() == 0) line.append("needs no staff");
        else if (f.market() && f.staffable() < .995) line.append(String.format(" · the city could staff %.0f%%", f.staffable() * 100));
        double free = f.landFree();
        double land = t.getLandSqFt();
        boolean noRoom = land > free;
        line.append(" · ").append(LandManager.areaWords(land));
        if (noRoom) line.append(" - more than is free");
        else if (free > 0 && land / free >= .01) line.append(String.format(" · %.1f%% of what is free", land / free * 100));
        line.append(" · ").append(f.running() > 0 ? "runs " + money(f.running()) + "/mo" : "nothing to run");
        return line.toString();
    }

    /** A card's order line: the stepper, Build under it (0.7.34), the quote under that, and how they reprice in place. */
    record Order(HBox steps, Pieces.ActionButton build, Label quoted, Runnable reprice) { }

    /**
     * − N + +10 +100 and ↺, then Build, and the quote under them - as the
     * 0.7.21 card had them, on every card now (the city's had lost +100).
     *
     * BUILD ASKS TO BE PRESSED (0.7.34). Jerus: "everywhere you have build,
     * like the build button, it should be more intuitive aka like an actual
     * button that is basically asking to be pressed, cause currently its a
     * tiny text". It was a small grey button beside "+100", disabled at 0.
     * It is Pieces' action button now, the card's width under the stepper,
     * and says the order: "Build 3 · $37.5M", on credit, or why it cannot
     * go ahead (orderPress()). At 0 it is still a button - "Build", "choose
     * how many" - and a press sets the count to one and prices it; it never
     * orders from 0, and the second click of a double-click is no press
     * (Pieces.ActionButton), so a double-click on it cannot become an order.
     * A press with a count places the order - since 0.7.40 a run of one
     * (placeRun()), taken off the card once it is placed: the refusals'
     * pages are the same doors, and short of cash the funding page, Build ›
     * Funding (showBuildFunding()), where the credit page was.
     *
     * REPRICED IN PLACE, not by redrawing the screen. Every press used to be
     * a screen change; here it is a few labels. That matters for more than
     * speed - a rebuild would throw away every pinned stat card and put the
     * page back at the top, so comparing two buildings while pricing an order
     * would be impossible. The figures come from Game.quoteBuild(), the
     * method that CHARGES, through BuildCard.verdict(), so the card cannot
     * quote a price the city then declines to honour.
     *
     * @param after run after every reprice: the card's border
     */
    Order orderControls(BuildingsTemplate template, String key, String page, EnumSet<BuildingType> types, Runnable after) {
        Label quoted = new Label(" ");
        quoted.setWrapText(true);
        quoted.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        Label count = new Label("0");
        count.setMinWidth(30);
        count.setAlignment(Pos.CENTER);
        count.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.TEXT_HEAD));
        Pieces.ActionButton build = actionButton(Icons.BUILD, Palette.BUILDING, ACTION_TALL, orderPress(null, 0), null);
        /*
         * The reset button is built below but referenced above, because
         * reprice is what decides whether it is on screen. One-element array:
         * a lambda can only close over something final.
         */
        final javafx.scene.Node[] resetHolder = new javafx.scene.Node[1];
        Runnable reprice = () -> {
            int n = orderQty.getOrDefault(key, 0);
            count.setText(String.valueOf(n));
            count.setStyle(Palette.figure(Palette.SIZE_BODY, n > 0 ? Palette.BUILDING : Palette.TEXT_HEAD));
            showIf(resetHolder[0], n > 0);
            showPendingHint();
            if (n <= 0) {
                build.show(orderPress(null, 0));
                quoted.setText(" ");
                quoted.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            } else {
                BuildCard.Verdict v = BuildCard.verdict(ui.game, template, n);
                build.show(orderPress(v, n));
                String[] said = quoteVerdict(v);
                // The city's rebate is the tax coming home (revised 0.7.19; EconomyManager,
                // THE REBATES ON A NEW HOME, AND THE CITY'S): the builders remit it to the
                // treasury as they bill the work.
                quoted.setText(money(v.quote().total) + " with " + money(v.quote().salesTax)
                        + " sales tax, back to the treasury as it is built  ·  " + said[0]);
                quoted.setStyle(Palette.words(Palette.SIZE_CAPTION, said[1]));
            }
            refreshOrderBar();
            after.run();
        };
        Button less = stepper("−", 26, () -> {
            // compute, not merge: merge() STORES the -1 when the card has no
            // entry yet, so a "-" on an empty card read "-1" until 0.7.5.
            orderQty.compute(key, (k, v) -> Math.max(0, (v == null ? 0 : v) - 1));
            reprice.run();
        });
        Button more = stepper("+", 26, () -> {
            orderQty.merge(key, 1, Integer::sum);
            reprice.run();
        });
        /*
         * TEN AND A HUNDRED, not just ten. Jerus: "+10 sometimes isnt
         * enough". It is not - housing goes up in hundreds in a city this
         * size, and the advice suggests forty clinics and eighty-eight gravel
         * roads. A hundred is the order of magnitude above ten, and anything
         * larger is a decision worth making a hundred at a time anyway.
         */
        Button ten = stepper("+10", 36, () -> {
            orderQty.merge(key, 10, Integer::sum);
            reprice.run();
        });
        Button hundred = stepper("+100", 42, () -> {
            orderQty.merge(key, 100, Integer::sum);
            reprice.run();
        });
        /*
         * AND A WAY BACK TO ZERO: with a +100 on the card, overshooting is one
         * click away. Hidden until there is something to clear.
         */
        Button reset = stepper("↺", 24, () -> {
            orderQty.remove(key);
            reprice.run();
        });
        Tooltip clear = new Tooltip("Back to none");
        clear.setShowDelay(Duration.millis(300));
        reset.setTooltip(clear);
        resetHolder[0] = reset;
        build.onPress(() -> {
            int n = orderQty.getOrDefault(key, 0);
            // From none, a press chooses one and prices it; it never orders.
            if (n <= 0) {
                orderQty.put(key, 1);
                reprice.run();
                return;
            }
            // A run of one, off this card once placed (0.7.40): refused or not yet funded, it stays on it.
            placeRun(runOf(template, n), new RunFrom(page, types, true));
        });
        // The shortcut, said on the button it stands in for.
        Tooltip keys = new Tooltip("Enter builds every pending order on this page; Backspace clears them");
        keys.setShowDelay(Duration.millis(300));
        Tooltip.install(build, keys);
        Region spread = new Region();
        HBox.setHgrow(spread, Priority.ALWAYS);
        HBox steps = new HBox(4, less, count, more, ten, hundred, spread, reset);
        steps.setAlignment(Pos.CENTER_LEFT);
        return new Order(steps, build, quoted, reprice);
    }

    /**
     * What a Build button says for n of a building (0.7.34), off the verdict
     * the quote line reads (BuildCard.verdict(), Game.quoteBuild()): with
     * none chosen, "Build" and "choose how many"; ready, "Build 3 ·
     * $37.5M", the count and the all-in total; short of cash, "Build 3 on
     * credit · $37.5M" - the press opens Build › Funding (0.7.40; the credit
     * page until then); and
     * where buildStack() would refuse - no deposit, nobody licensed, short
     * of land - why, on the button, and the way out under it, which the
     * press's page offers. A pure read: no node, so a probe reads it.
     */
    Pieces.Press orderPress(BuildCard.Verdict v, int n) {
        if (n <= 0 || v == null) return new Pieces.Press(Pieces.Look.CHOOSE, "Build", "choose how many");
        String count = formatter.format(n), total = money(v.quote().total);
        switch (v.kind()) {
            case NO_DEPOSIT: return new Pieces.Press(Pieces.Look.HELD, "No " + BuildCard.depositWord(v.site()) + " deposit",
                    "whole fields of " + (v.site() == Resource.OIL ? "oil" : "ore") + " come with land: the Land office ›");
            // ...a desalination plant's or, since 0.7.86, a sea terminal's: the words fit both.
            case NO_COAST:   return new Pieces.Press(Pieces.Look.HELD, "No sea owned",
                    "sea comes with land: the Land office ›");
            case NO_LICENCE: return new Pieces.Press(Pieces.Look.HELD, "Nobody licensed to work in it",
                    "a school licenses them: Education ›");
            case NO_LAND:    return new Pieces.Press(Pieces.Look.HELD, v.quote().landFree <= 0 ? "No land free"
                    : "Short " + LandManager.areaWords(v.figure()) + " of land", "more ground: the Land office ›");
            case BILL:       return new Pieces.Press(Pieces.Look.CREDIT, "Build " + count + " on credit · " + total, null);
            default:         return new Pieces.Press(Pieces.Look.GO, "Build " + count + " · " + total, null);
        }
    }

    /**
     * The quote's verdict and its colour, in buildStack()'s order: no
     * deposit, no coast (0.7.59) and nobody licensed in red - warned here
     * since 0.7.25, where they were found only after the click - then short
     * of land in red,
     * short of cash in amber (the bill and the credit offers follow the
     * click), else the wait at today's queue in green.
     */
    String[] quoteVerdict(BuildCard.Verdict v) {
        switch (v.kind()) {
            case NO_DEPOSIT: return new String[] {"no " + BuildCard.depositWord(v.site()) + " deposit", Palette.BAD};
            case NO_COAST:   return new String[] {"no sea owned", Palette.BAD};
            case NO_LICENCE: return new String[] {"nobody licensed", Palette.BAD};
            case NO_LAND:    return new String[] {"short " + LandManager.areaWords(v.figure()) + " of land", Palette.BAD};
            case BILL:       return new String[] {"short " + money(v.figure()) + " — you will be offered a bill", Palette.WARN};
            default:         return new String[] {atTodaysQueue(v.figure()), Palette.GOOD};
        }
    }

    // The stepper's buttons are Pieces.stepper() since 0.7.26, which the land office draws too.

    /** The (i): hover shows the stat cover over the whole card, a click keeps it there (the dot turns blue), a second puts it away. */
    Label infoDot(String key, VBox cover) {
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
        info.setOnMouseEntered(e -> cover.setVisible(true));
        info.setOnMouseExited(e -> cover.setVisible(pinnedStats.contains(key)));
        info.setOnMouseClicked(e -> {
            if (!pinnedStats.remove(key)) pinnedStats.add(key);
            dress.run();
            cover.setVisible(true);
        });
        Tooltip pin = new Tooltip("Hover to read, click to keep it open");
        pin.setShowDelay(Duration.millis(400));
        Tooltip.install(info, pin);
        return info;
    }

    /**
     * The stat cover, as tall as the card it covers: what the building does
     * in sentences (whatItDoes(), which BuildMenuCheck holds for all 91), the
     * figures the face does not say - materials, build points, road load,
     * electricity, water, the wage bill and the job mix - and on a market
     * card the investors' estimate of what one would make its owner.
     * Scrollable, so a building with a long staff list loses nothing.
     *
     * SIZED BY THE CARD, NOT BY ITSELF (0.7.25): the 0.7.21 tile was a fixed
     * 232 px and its cover was too; a card is as tall as what it says now, so
     * the cover asks for no height of its own and the card's stack stretches
     * it over the face.
     */
    VBox statCover(BuildingsTemplate t, BuildCard.Figures f) {

        VBox body = new VBox(1);
        body.setStyle("-fx-padding: 8 10 8 10;");

        Label title = new Label(t.getName().toUpperCase());
        title.setWrapText(true);
        title.setMaxWidth(NEED_CARD - 46);
        title.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.ACCENT) + " -fx-font-weight: bold;");
        body.getChildren().add(title);

        for (String line : whatItDoes(t)) {
            Label l = new Label(line);
            l.setWrapText(true);
            l.setMaxWidth(NEED_CARD - 28);
            l.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_BODY)
                    + " -fx-padding: 3 0 0 0;");
            body.getChildren().add(l);
        }

        /*
         * WHAT THE FACE DOES NOT ALREADY SAY. The cash cost, the land and the
         * upkeep are on the card underneath it - so covering the card with
         * them would tell the player three things they had just read. A cover
         * that repeats the face is a cover that wastes the space it took.
         */
        body.getChildren().add(cardGap());
        body.getChildren().addAll(
                statPair("Materials", formatter.format(t.getConstructionMaterials())),
                statPair("Build points", formatter.format(t.getConstructionPoints())),
                statPair("Road load", formatter.format(t.getRoadLoad())),
                statPair("Electricity", power(t.getElectricityConsumption())),
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
            who.setMaxWidth(NEED_CARD - 28);
            who.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
            body.getChildren().add(who);
        }

        // ...and on a market card, what the investors reckon one would make
        // its owner a month (BusinessInvestment.estimatedMonthlyProfit()):
        // after its wages, so it can be a loss where the value added is not.
        if (f.market()) {
            body.getChildren().add(cardGap());
            Label est = new Label("Investors' estimate: " + money(BuildCard.investors(ui.game, t).estimate())
                    + " a month to its owner.");
            est.setWrapText(true);
            est.setMaxWidth(NEED_CARD - 28);
            est.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_BODY));
            body.getChildren().add(est);
        }

        javafx.scene.control.ScrollPane scroller =
                new javafx.scene.control.ScrollPane(body);
        scroller.setFitToWidth(true);
        scroller.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroller.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setMinHeight(0);
        VBox.setVgrow(scroller, Priority.ALWAYS);

        VBox cover = new VBox(scroller);
        cover.setMinSize(0, 0);
        cover.setPrefSize(NEED_CARD, 0);
        cover.setMaxSize(NEED_CARD, Double.MAX_VALUE);
        cover.setStyle(Palette.block(Palette.FIELD, Palette.ACCENT) + " -fx-background-radius: 8; -fx-border-radius: 8;");
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
     * that explains the refusal and offers the way out of it. The card was a new
     * way to reach this; it was not a new way to buy.
     *
     * True when it was built and the page is drawn again; false when the city
     * said no and the refusal's screen is up instead, or the funding page.
     * Since 0.7.40 a run of one (placeRun(), ONE FUNDING PAGE FOR THE RUN),
     * and a suggestion's Build is its one caller: a card's own Build, the
     * order bar and Enter hand placeRun() their orders themselves.
     */
    boolean placeOrder(BuildingsTemplate template, int quantity,
                            String menuTitle, EnumSet<BuildingType> categories) {
        return placeRun(runOf(template, quantity), new RunFrom(menuTitle, categories, false));
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
     * dialled up on Homes and left there are not built by an Enter
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
     * One run since 0.7.40 (placeRun()): funded whole when it is more than
     * the cash, then placed one card at a time, so each order meets the city
     * as the one before it left it - what the first spent is not there for
     * the second. The first refusal stops the run - its screen explains it
     * and offers the way out - and every order not placed stays on its card
     * for when the player comes back (each card was emptied before it was
     * placed until 0.7.40, so the refused one lost its count). True when
     * there was anything to build, which is when the key is spent.
     */
    boolean buildPending() {
        java.util.LinkedHashMap<BuildingsTemplate, Integer> run = new java.util.LinkedHashMap<>();
        for (PageCard card : pageCards) {
            int n = orderQty.getOrDefault(card.template().getName(), 0);
            if (n > 0) run.putIfAbsent(card.template(), n);
        }
        if (run.isEmpty()) return false;
        placeRun(run, new RunFrom(pageTitle, pageCategories, true));
        return true;
    }

    /**
     * Every quantity on the page back to none - the ↺ on every card at once,
     * and like it, each card repriced in place rather than the page redrawn
     * (see REPRICED IN PLACE, in orderControls()). True when something pending
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

    /**
     * The caption under the grid, shown while any card on the page has a
     * quantity - and its line kept while it is not (0.7.20): it was taken out
     * of the layout, so pressing "+" made the page a line taller and moved it.
     */
    void showPendingHint() {
        boolean any = false;
        for (PageCard card : pageCards) {
            if (orderQty.getOrDefault(card.template().getName(), 0) > 0) {
                any = true;
                break;
            }
        }
        if (pendingHint == null) return;
        pendingHint.setManaged(true);
        pendingHint.setVisible(any);
    }


    /* =====================================================================
       THE OVERVIEW (0.7.24)

       Jerus: "when you start the game you start in residential so the player
       without reading thinks he needs to building houses". Build opens here
       now, as the round-2 mockups draw it (BuildB, "Keep all three rows"):

         one line - the city's own works are the player's job, and investors
         build homes, shops and industry by themselves;
         THE CITY'S JOB - a tile for each of the five only the city builds,
         its ring the category's worst need from NEEDS YOU (CityNeeds.worst(),
         the panel's own verdict, not a second scoring), a caption, a smaller
         line and what is on site;
         WHAT WOULD HELP MOST - up to three orders by BuildAdvice's rule, each
         with the need, before and after, the count and the building, three
         lines (a line until 0.7.51: what it does, then when it opens and what
         it is sized for, and its ground - cardWords()), the quote its button
         charges, Show and the button (Show and Order until 0.7.34: a pill
         now, and Build's action button saying the order); over them what the
         run charges and "Build all three", read off the run the model places;
         AUTOMATIC BUILDING (0.7.73) - its two dials and what it has done,
         and the switch: see that section below;
         THE MARKET BUILDS THESE - the nine the investors build, quieter, with
         what stands and what is on site.

       The drawn city strip the mockups tried is saved for the city view, as
       Jerus chose; it is not here. Every figure is the model's.
       ===================================================================== */

    /** A ring's size on the Overview's tiles (its stroke is 6 px). */
    static final double JOB_RING = 58;

    /** The Build tab's front page. */
    void showOverview() {
        ui.clearMenu("handleAllBuildingMenus", this::showOverview);
        buildCategory = BuildAdvice.OVERVIEW;
        // The keyboard's page: nothing on it to build or clear.
        pageCards.clear();
        pageTitle = BuildAdvice.OVERVIEW;
        pageCategories = EnumSet.noneOf(BuildingType.class);
        pendingHint = null;

        List<CityNeeds.Need> all = CityNeeds.measure(ui.game, SummaryScreen.WORDS);
        List<BuildAdvice.Suggestion> advice = BuildAdvice.suggest(ui.game, all);

        VBox page = widePage();
        HBox head = buildHead(BuildAdvice.OVERVIEW, EnumSet.noneOf(BuildingType.class));
        head.setStyle("-fx-padding: 0 0 4 0;");
        page.getChildren().addAll(head, overviewLead(),
                sectionHead("THE CITY'S JOB", hint("tap one to build for it")),
                cityJob(all),
                sectionHead("WHAT WOULD HELP MOST", adviceTotal(advice)),
                adviceRow(advice),
                sectionHead("AUTOMATIC BUILDING", AUTO_INFO, hint(autoHint(ui.game.getAutoBuilder()))),
                autoRow(),
                sectionHead("THE MARKET BUILDS THESE", hint("investors decide; you can add")),
                marketRow());
        ui.rootMenu.getChildren().add(page);
    }

    /** The one line: whose job the city's works are. */
    HBox overviewLead() {
        javafx.scene.text.Text first = new javafx.scene.text.Text("Your job is the city's own works. ");
        first.setStyle(Palette.Fonts.sansSemiBold() + " -fx-font-size: 12.5px;");
        first.setFill(javafx.scene.paint.Color.web(Palette.TEXT_HEAD));
        javafx.scene.text.Text rest = new javafx.scene.text.Text(
                "Investors build homes, shops and industry by themselves; you can add to theirs.");
        rest.setStyle("-fx-font-size: 12.5px;");
        rest.setFill(javafx.scene.paint.Color.web(Palette.TEXT_LABEL));
        javafx.scene.text.TextFlow words = new javafx.scene.text.TextFlow(first, rest);
        HBox.setHgrow(words, Priority.ALWAYS);
        HBox line = new HBox(10, icon(Icons.OVERVIEW, Palette.ACCENT, 16), words);
        line.setAlignment(Pos.CENTER_LEFT);
        line.setStyle("-fx-padding: 9 12 9 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + Palette.EDGE + ";"
                + " -fx-border-width: 1 1 1 3; -fx-border-color: " + Palette.EDGE + " " + Palette.EDGE + " "
                + Palette.EDGE + " " + Palette.ACCENT + ";");
        return line;
    }

    /*
     * The page, the section's heading and its hint, and the grid of equal
     * columns are Pieces' since 0.7.26 (widePage(), sectionHead(), hint(),
     * equalColumns(), PAGE_WIDE): the land office is laid out with them too.
     */

    /* ----------------------------- the city's job ----------------------------- */

    /** The five, a tile each. */
    javafx.scene.layout.GridPane cityJob(List<CityNeeds.Need> all) {
        List<BuildAdvice.Category> mine = new ArrayList<>();
        for (BuildAdvice.Category c : BuildAdvice.categories()) if (c.cityBuilds()) mine.add(c);
        javafx.scene.layout.GridPane grid = equalColumns(mine.size(), 10);
        for (int i = 0; i < mine.size(); i++) grid.add(jobTile(mine.get(i), all), i, 0);
        return grid;
    }

    /** Which of NEEDS YOU's doors a city category is. */
    static CityNeeds.Go goOf(String category) {
        switch (category) {
            case BuildAdvice.UTILITIES:  return CityNeeds.Go.UTILITIES;
            case BuildAdvice.ROADS:      return CityNeeds.Go.ROADS;
            case BuildAdvice.HEALTHCARE: return CityNeeds.Go.HEALTHCARE;
            case BuildAdvice.EDUCATION:  return CityNeeds.Go.EDUCATION;
            case BuildAdvice.SAFETY:     return CityNeeds.Go.SAFETY;
            default:                     return CityNeeds.Go.HOMES;
        }
    }

    /** A verdict's colour: green, amber or red, as NEEDS YOU colours its rows; -1, no verdict, is the city's blue. */
    static String verdict(int level) {
        return level >= 2 ? Palette.BAD : level == 1 ? Palette.WARN : level == 0 ? Palette.GOOD : Palette.ACCENT;
    }

    /**
     * What a ring says: its figure, how much of it is drawn, a caption of a
     * few words and a smaller line.
     */
    record Shown(String figure, double arc, String caption, String detail) { }

    /**
     * A need, as a ring shows it: the figure NEEDS YOU judged, worded for the
     * ring. A SERVED ROW (0.7.41) shows what it serves - the row's own share
     * (Need.served()), its arc that share held to a whole ring, and its
     * name, "served" and the verdict's word: "general care served, short".
     */
    Shown shown(CityNeeds.Need n) {
        BuildAdvice.Measure m = BuildAdvice.measureOf(n);
        double[] sd = m == null ? new double[] {0, 0} : BuildAdvice.supplyDemand(ui.game, m, java.util.Map.of());
        double share = sd[1] > 0 ? Math.max(0, Math.min(1, sd[0] / sd[1])) : 1;
        double v = n.value();
        CityNeeds.Served s = n.served();
        String said = s == null || m == null ? null : m.label().toLowerCase() + " " + servedWords(s);
        switch (n.kind()) {
            case POWER: case WATER: {
                boolean power = n.kind() == CityNeeds.Kind.POWER;
                double gap = sd[1] - sd[0];
                // Power in kW scaled, a rate (0.7.28: "units short a month" until then).
                return new Shown(CityNeeds.servedPct(s.share()), arc(s), said,
                        power ? (gap > 0 ? power(gap) + " short" : power(-gap) + " spare")
                              : gap > 0 ? shortNumber(gap) + " units short a month" : shortNumber(-gap) + " units spare");
            }
            case ROADS:
                // The road's pair (0.7.29), served since 0.7.41: "62%" "road capacity served, short · 56% flow".
                return new Shown(CityNeeds.servedPct(s.share()), arc(s),
                        said + " · " + pct(ui.game.getInfrastructureManager().getThroughputRatio()) + " flow",
                        shortNumber(sd[1]) + " trips on " + shortNumber(sd[0]) + " of road");
            case CARE:
                return new Shown(CityNeeds.servedPct(s.share()), arc(s), said,
                        sd[1] - sd[0] >= .5 ? people(sd[1] - sd[0]) + " people without" : "everyone has a place");
            case DEAD:
                return new Shown(people(v), share, "dead with nowhere to go",
                        people(ui.game.getHealthcare().getDeaths()) + " die a month");
            case PLOTS:
                return new Shown(String.format("%.0f mo", v), Math.min(1, v / CityNeeds.PLOTS_YELLOW),
                        "of burial plots left",
                        shortNumber(Healthcare.plotsRemaining(ui.game.getBuildingManager().getCareCapacity(CareType.BURIAL),
                                ui.game.getHealthcare().getPlotsUsed())) + " plots free");
            case BASIC_SCHOOLS:
                return new Shown(CityNeeds.servedPct(s.share()), arc(s), said,
                        sd[1] - sd[0] >= .5 ? "short " + people(sd[1] - sd[0]) + " places" : "a place for every child");
            case HIGHER_SCHOOL:
                // Seats over who would come, served (0.7.41): it read "of would-be students seated", held to 100%.
                // ...and be hired (0.7.51): the row's second number is CityNeeds.wanted() now.
                return new Shown(CityNeeds.servedPct(s.share()), arc(s), said,
                        people(sd[0]) + " seats · " + people(sd[1]) + " would come and be hired");
            case CRIME:
                return new Shown(String.format("%.1f×", v), v > 0 ? Math.min(1, 1 / v) : 1, "Canada's crime rate",
                        String.format("police at %.0f%% of full cover", share * 100));
            case CELLS:
                return new Shown(pct(share), share, "of the caught held", n.reading());
            default:
                return new Shown("", 1, n.label().toLowerCase(), n.reading());
        }
    }

    /** A share as a whole per cent; a dash for one that is not a number (a network with nothing supplying it). */
    static String pct(double share) { return Double.isFinite(share) ? String.format("%.0f%%", share * 100) : "—"; }

    /** A served gauge's words after its figure (0.7.41), the same on every screen: "served, short", "served, tight", "served, enough" - or "served" alone where no line judges it (transit). */
    static String servedWords(CityNeeds.Served s) {
        return s == null || s.word() == null ? CityNeeds.SERVED : CityNeeds.SERVED + ", " + s.word();
    }

    /** ...its colour: the one verdict's, or the city's blue where no line judges it. */
    static String servedTone(CityNeeds.Served s) {
        return verdict(s == null ? -1 : s.level());
    }

    /** ...and its ring's arc: the share held to a whole ring, so a ring is full when the need is met (0.7.41: a load's ring was full when it was over). */
    static double arc(CityNeeds.Served s) {
        double v = s == null ? 0 : s.share();
        return Double.isNaN(v) ? 0 : Math.max(0, Math.min(1, v));
    }

    /** A category with nothing near its line: a few words, and the figure nearest one. */
    Shown fine(String category, List<CityNeeds.Need> all) {
        CityNeeds.Go go = goOf(category);
        CityNeeds.Need nearest = null;
        for (CityNeeds.Need n : all) {
            if (n.go() == go && (nearest == null || n.near() > nearest.near())) nearest = n;
        }
        String caption;
        switch (category) {
            case BuildAdvice.UTILITIES:  caption = "power and water reach everyone"; break;
            case BuildAdvice.ROADS:      caption = "the roads flow"; break;
            case BuildAdvice.HEALTHCARE: caption = "care for everyone who needs it"; break;
            case BuildAdvice.EDUCATION:  caption = "every school has room"; break;
            default:                     caption = "crime is in hand"; break;
        }
        return new Shown(null, 1, caption,
                nearest == null ? "" : nearest.label().toLowerCase() + " · " + nearest.reading());
    }

    /** Buildings on site in a category, for anybody's order. */
    int onSiteIn(EnumSet<BuildingType> types) {
        int n = 0;
        for (BuildingsStacks s : ui.game.getBuildingManager().getStacksUnderConstruction()) {
            if (types.contains(s.getBuilding().getCategory())) n += s.getUnderConstruction();
        }
        return n;
    }

    /** ...and standing. */
    int standingIn(EnumSet<BuildingType> types) {
        BuildingManager bm = ui.game.getBuildingManager();
        int n = 0;
        for (BuildingsTemplate t : bm.getTemplatesByCategory(types)) n += bm.getQuantity(t.getId());
        return n;
    }

    /** A crane and "N on site", in the building colour, or a quiet word when there is nothing. */
    HBox onSiteLine(int n, String none) {
        Label words = new Label(n > 0 ? formatter.format(n) + " on site" : none);
        words.setStyle(Palette.words(Palette.SIZE_LABEL, n > 0 ? Palette.BUILDING : Palette.TEXT_MUTED));
        HBox line = new HBox(5);
        if (n > 0) line.getChildren().add(icon(Icons.CRANE, Palette.BUILDING, 13));
        line.getChildren().add(words);
        line.setAlignment(Pos.CENTER_LEFT);
        return line;
    }

    /**
     * One of the city's five: its worst need as a ring, a caption, a line, and
     * what is on site. NOTHING LISTED IS NOT ALWAYS ENOUGH (0.7.41): with no
     * NEEDS YOU row of its own, a tile shows a served row that is still short
     * or tight (CityNeeds.worstUnlisted()) before it shows its tick - Jerus:
     * "shows check no issues yet if you click general care is at 90%". The
     * ring's colour is the row's verdict (Need.verdictLevel()).
     */
    VBox jobTile(BuildAdvice.Category c, List<CityNeeds.Need> all) {
        CityNeeds.Need worst = CityNeeds.worst(all, goOf(c.name()));
        if (worst == null) worst = CityNeeds.worstUnlisted(all, goOf(c.name()));
        String tone = verdict(worst == null ? 0 : worst.verdictLevel());
        Shown s = worst == null ? fine(c.name(), all) : shown(worst);

        Label name = new Label(c.name());
        name.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label go = new Label("›");
        go.setStyle(Palette.words(13, Palette.TEXT_MUTED));
        HBox top = new HBox(8, iconSquare(Icons.ofCategory(c.name()), CITY_DOT, 28, 15), name, gap, go);
        top.setAlignment(Pos.CENTER_LEFT);

        Label caption = new Label(s.caption());
        caption.setWrapText(true);
        caption.setStyle(Palette.words(12, Palette.TEXT_HEAD));
        Label detail = new Label(s.detail());
        detail.setWrapText(true);
        detail.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        VBox words = new VBox(2, caption, detail);
        words.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(words, Priority.ALWAYS);
        words.setMinWidth(0);
        HBox middle = new HBox(10, ring(s.arc(), tone, JOB_RING, 6, s.figure(), 13), words);
        middle.setAlignment(Pos.CENTER_LEFT);

        int onSite = onSiteIn(c.types());
        VBox tile = new VBox(10, top, middle, onSiteLine(onSite, worst == null ? "nothing needed" : "nothing on site"));
        tile.setMaxWidth(Double.MAX_VALUE);
        tile.setMinHeight(150);
        String rest = "-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-width: 3 1 1 1; -fx-cursor: hand;"
                + " -fx-border-color: " + tone + " ";
        tile.setStyle(rest + Palette.EDGE + " " + Palette.EDGE + " " + Palette.EDGE + ";");
        tile.setOnMouseEntered(e -> tile.setStyle(rest + Palette.ACCENT + " " + Palette.ACCENT + " " + Palette.ACCENT + ";"));
        tile.setOnMouseExited(e -> tile.setStyle(rest + Palette.EDGE + " " + Palette.EDGE + " " + Palette.EDGE + ";"));
        Tooltip tip = new Tooltip(worst == null ? c.name() + ": nothing near its line. Click to build for it."
                : c.name() + ": " + worst.label().toLowerCase() + " · " + worst.reading() + ". Click to build for it.");
        tip.setShowDelay(Duration.millis(300));
        Tooltip.install(tile, tip);
        tile.setOnMouseClicked(e -> openCategory(c.name()));
        return tile;
    }

    /* ----------------------------- what would help most ----------------------------- */

    /**
     * "all three ≈ $X of your $Y", and the button that orders them ("Build
     * all three", 0.7.34; a step chip, "Order all three", before). X is what
     * placing the run charges (0.7.51, Game.buildRunInvoice() of the run
     * orderAll() places, BuildAdvice.run()): each card's quote added up was
     * $85.5M where the run charged $98.4M (the founded playtest city at
     * month 24, 0.7.49).
     */
    javafx.scene.Node adviceTotal(List<BuildAdvice.Suggestion> cards) {
        // ...of the build orders (0.7.70): a paving is not one, and has its card's own button.
        List<BuildAdvice.Suggestion> advice = BuildAdvice.builds(cards);
        if (advice.isEmpty()) return null;
        double sum = ui.game.buildRunInvoice(BuildAdvice.run(advice));
        String all = advice.size() == 3 ? "all three" : advice.size() == 2 ? "both" : "it";
        Label what = new Label(all + " ≈ ");
        what.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        Label total = new Label(money(sum));
        total.setStyle(Palette.figure(Palette.SIZE_BODY, sum > ui.game.getCash() ? Palette.WARN : Palette.TEXT_HEAD));
        Label of = new Label(" of your ");
        of.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        Label cash = new Label(money(ui.game.getCash()));
        cash.setStyle(Palette.figure(Palette.SIZE_BODY, ui.game.getCash() < 0 ? Palette.BAD : Palette.TEXT_HEAD));
        HBox row = new HBox(0, what, total, of, cash);
        row.setAlignment(Pos.CENTER_RIGHT);
        if (advice.size() > 1) {
            // The cards' button, inline (0.7.34): it orders every suggestion, as each card's would.
            Pieces.ActionButton order = actionButton(Icons.BUILD, Palette.BUILDING, ACTION_INLINE,
                    allPress(advice), () -> orderAll(advice));
            order.setMinWidth(Region.USE_PREF_SIZE);
            HBox.setMargin(order, new javafx.geometry.Insets(0, 0, 0, 10));
            row.getChildren().add(order);
        }
        Tooltip.install(row, new Tooltip("What placing them in this order charges now, each on the yard the ones "
                + "before it leave. Land is not in it: the city builds on its own ground."));
        return row;
    }

    /**
     * "Build all three" or "Build both" (0.7.34), read off the run the model
     * will place (0.7.51; each card's own look, until then, said GO for runs
     * the model stopped): held when Game.buildRunAhead() stops it short,
     * with the card it stops at and why; on credit when the run's invoice is
     * more than the cash (Game.buildFundingGap()), with by how much.
     */
    Pieces.Press allPress(List<BuildAdvice.Suggestion> cards) {
        Game g = ui.game;
        List<BuildAdvice.Suggestion> advice = BuildAdvice.builds(cards);
        java.util.LinkedHashMap<BuildingsTemplate, Integer> run = BuildAdvice.run(advice);
        String all = advice.size() == 3 ? "Build all three" : "Build both";
        int ahead = g.buildRunAhead(run);
        if (ahead < run.size()) {
            BuildingsTemplate at = null;
            int i = 0;
            for (BuildingsTemplate t : run.keySet()) if (i++ == ahead) { at = t; break; }
            int card = 0;
            // ...its place among the cards on the page, a paving's included (0.7.70).
            while (card < cards.size() - 1 && (cards.get(card).paving() || cards.get(card).template() != at)) card++;
            String ordinal = card == 0 ? "first" : card == 1 ? "second" : "third";
            BuildingsTemplate stopAt = at;
            String why = switch (g.buildRunStop(run)) {
                case NO_DEPOSIT -> Game.siteOf(stopAt) == Resource.OIL ? "no oil left to drill" : "no ore left to dig";
                case NO_COAST   -> "no sea owned for it";
                case NO_LICENCE -> "nobody licensed to practise";
                default         -> "short of land - buy it at the land office first";
            };
            return new Pieces.Press(Pieces.Look.HELD, all, "it stops at the " + ordinal + ": " + why);
        }
        double gap = g.buildFundingGap(run);
        if (gap > 0) return new Pieces.Press(Pieces.Look.CREDIT, all + " on credit",
                "short " + money(gap) + ": Build offers a loan");
        return new Pieces.Press(Pieces.Look.GO, all, null);
    }

    /** The suggestions as one run (0.7.40, placeRun(); BuildAdvice.run() since 0.7.51): funded whole when it is more than the cash, each placed in turn by its own button's path; the first refusal stops the run, as Enter's does. */
    void orderAll(List<BuildAdvice.Suggestion> advice) {
        placeRun(BuildAdvice.run(BuildAdvice.builds(advice)), new RunFrom(BuildAdvice.OVERVIEW, EnumSet.noneOf(BuildingType.class), false));
    }

    /** Up to three cards, or a line saying there is nothing to suggest. */
    javafx.scene.Node adviceRow(List<BuildAdvice.Suggestion> advice) {
        if (advice.isEmpty()) {
            Label none = new Label("Nothing the city builds is past its line, or what is on site already answers it, or no building can move it: nothing to suggest.");
            none.setWrapText(true);
            none.setStyle(Palette.words(12, Palette.TEXT_LABEL) + " -fx-padding: 4 0 4 0;");
            return none;
        }
        javafx.scene.layout.GridPane grid = equalColumns(3, 10);
        for (int i = 0; i < advice.size(); i++) grid.add(suggestionCard(advice.get(i)), i, 0);
        return grid;
    }

    /** A measure's gauge with these buildings standing as well, as its ring writes it (0.7.41): what a served measure serves, else figureText() of its figure. */
    String gaugeText(BuildAdvice.Measure m, java.util.Map<BuildingsTemplate, Integer> added) {
        return BuildAdvice.isServed(m) ? CityNeeds.servedPct(BuildAdvice.served(ui.game, m, added))
                : figureText(m, BuildAdvice.figure(ui.game, m, added));
    }

    /** A measure's figure, worded as its ring words it: a share, a load, months, people, or a multiple of Canada's crime - for a served measure (0.7.41) gaugeText() reads what it serves instead. */
    String figureText(BuildAdvice.Measure m, double f) {
        // A network drawing something with nothing supplying it has no load to print.
        if (Double.isNaN(f) || Double.isInfinite(f)) return "—";
        switch (m.kind()) {
            case DEATH:  return people(f) + " unburied";
            case PLOTS:  return f >= 1e6 ? "plenty" : String.format("%.0f mo", f);
            case POLICE: return String.format("%.1f×", f);
            case CELLS:  return people(f) + " not held";
            case SCHOOL:
                if (!m.school().isBasic()) return pct(f > 0 ? Math.min(1, 1 / f) : 1);
                return pct(f);
            default:     return pct(f);
        }
    }

    /**
     * What a suggested order does, in a line. A served measure (0.7.41) goes
     * from what its NEEDS YOU row serves to what the order and what is on
     * site leave it serving (BuildAdvice.verdictAfter()): "brings power from
     * 92% to 140% served" - it was the load, "of its capacity".
     */
    String doesWhat(BuildAdvice.Suggestion s) {
        BuildAdvice.Measure m = s.measure();
        boolean served = BuildAdvice.isServed(m);
        String from = served ? CityNeeds.servedPct(s.need().served().share()) : figureText(m, s.before());
        String to = served ? CityNeeds.servedPct(BuildAdvice.verdictAfter(ui.game, s).share()) : figureText(m, s.after());
        String line;
        switch (m.kind()) {
            case POWER:  line = "brings power from " + from + " to " + to + " " + CityNeeds.SERVED; break;
            case WATER:  line = "brings water from " + from + " to " + to + " " + CityNeeds.SERVED; break;
            case ROADS:  line = "takes the road from " + from + " to " + to + " " + CityNeeds.SERVED; break;
            case CARE:   line = careHeading(m.care()).split("  -  ")[0].toLowerCase() + " from " + from + " to " + to
                    + " " + CityNeeds.SERVED; break;
            case DEATH:  line = "plots and ovens for the dead: " + from + " to " + to; break;
            case PLOTS:  line = "burial plots from " + from + " to " + to + " left"; break;
            case SCHOOL: line = m.school().getLabel().toLowerCase() + " from " + from + " to " + to + " " + CityNeeds.SERVED; break;
            case POLICE: line = "crime from " + from + " to " + to + " Canada's"; break;
            case CELLS:  line = "the caught not held: " + from.replace(" not held", "") + " to " + to; break;
            default:     line = from + " to " + to;
        }
        if (s.onSite() > 0) line += ", once the " + formatter.format(s.onSite()) + " on site open";
        return line;
    }

    /** The measure Show opens a category on, for a suggestion: transit's ring for a line, death care's for the plots. */
    static BuildAdvice.Measure showOn(BuildAdvice.Suggestion s) {
        BuildAdvice.Measure m = s.measure();
        if (m.kind() == BuildAdvice.Kind.ROADS && s.template().getTransitCapacity() > 0) return BuildAdvice.Measure.of(BuildAdvice.Kind.TRANSIT);
        if (m.kind() == BuildAdvice.Kind.PLOTS) return BuildAdvice.Measure.of(BuildAdvice.Kind.DEATH);
        return m;
    }

    /** One suggested order: the need, before and after, the count and the building, its three lines (cardWords(), 0.7.51), its price, Show, and its Build button. */
    VBox suggestionCard(BuildAdvice.Suggestion s) {
        CityNeeds.Need need = s.need();
        // SERVED (0.7.41): a served need's chip, its before and its after read what it serves, each in
        // the one verdict's colour - an order that stops at NEEDS YOU's line short of 100% reads amber.
        CityNeeds.Served afterServed = BuildAdvice.verdictAfter(ui.game, s);
        String now = afterServed != null ? CityNeeds.servedPct(need.served().share()) : figureText(s.measure(), s.before());
        String tone = verdict(need.verdictLevel());
        Label chip = new Label(need.label().charAt(0) + need.label().substring(1).toLowerCase() + " "
                + now + (afterServed != null ? " " + CityNeeds.SERVED : ""));
        chip.setStyle(Palette.words(Palette.SIZE_LABEL, tone) + " -fx-padding: 1 7 1 7; -fx-border-color: " + tone + ";"
                + " -fx-border-radius: 9; -fx-background-radius: 9; -fx-background-color: " + tone + "22;");
        Label before = new Label(now + " → ");
        before.setStyle(Palette.figure(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        boolean clears = BuildAdvice.clear(s.measure(), s.after());
        Label after = new Label(afterServed != null ? CityNeeds.servedPct(afterServed.share()) : figureText(s.measure(), s.after()));
        after.setStyle(Palette.figure(Palette.SIZE_LABEL, afterServed != null ? servedTone(afterServed)
                : clears ? Palette.GOOD : Palette.WARN));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox top = new HBox(0, chip, gap, before, after);
        top.setAlignment(Pos.CENTER_LEFT);

        Label count = new Label(formatter.format(s.count()) + " × ");
        count.setStyle(Palette.figure(Palette.SIZE_HEADING + 1, Palette.BUILDING));
        Label name = new Label(suggestionName(s));
        name.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        HBox what = new HBox(0, count, name);
        what.setAlignment(Pos.BASELINE_LEFT);
        CardWords cw = cardWords(s);
        Label line = new Label(cw.does());
        line.setWrapText(true);
        line.setStyle(wordsAt(10.5, Palette.TEXT_LABEL));
        Label sized = new Label(cw.sized());
        sized.setWrapText(true);
        sized.setStyle(wordsAt(10.5, Palette.TEXT_LABEL));
        Label land = new Label(cw.land());
        land.setWrapText(true);
        land.setStyle(wordsAt(10.5, s.landShort() > 0 ? Palette.WARN : Palette.TEXT_LABEL));
        Tooltip.install(land, new Tooltip(cw.landTip()));
        VBox words = new VBox(2, what, line, sized, land);
        HBox.setHgrow(words, Priority.ALWAYS);
        words.setMinWidth(0);
        HBox middle = new HBox(10, iconSquare(Icons.ofCategory(s.measure().category()), CITY_DOT, 34, 18), words);
        middle.setAlignment(Pos.TOP_LEFT);

        Label price = new Label(money(s.price()));
        price.setStyle(Palette.figure(Palette.SIZE_SECTION, s.needsCredit() ? Palette.WARN : Palette.TEXT_HEAD));
        Label allIn = new Label(" all in");
        allIn.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        HBox cost = new HBox(0, price, allIn);
        cost.setAlignment(Pos.BASELINE_LEFT);
        Region spread = new Region();
        HBox.setHgrow(spread, Priority.ALWAYS);
        // A door to the category (0.7.34: a pill), and the order itself as the card's button under it.
        HBox show = doorPill("Show", Icons.BUILD, Palette.BUILDING, () -> {
            // A paving's count on the Gravel Road card's own stepper (0.7.70), not as a build order.
            if (s.paving()) paveQty = s.count();
            else orderQty.put(s.template().getName(), s.count());
            BuildAdvice.Category c = BuildAdvice.category(s.measure().category());
            measurePicked.put(c.name(), showOn(s));
            handleAllBuildingMenus(c.name(), c.types());
        });
        Tooltip.install(show, new Tooltip("Open " + s.measure().category() + " with " + formatter.format(s.count()) + " dialled up"));
        Pieces.ActionButton order = actionButton(Icons.BUILD, Palette.BUILDING, ACTION_TALL, suggestionPress(s),
                () -> {
                    if (!s.paving()) placeOrder(s.template(), s.count(), BuildAdvice.OVERVIEW, EnumSet.noneOf(BuildingType.class));
                    else if (ui.game.paveRoads(s.count())) showOverview();
                });
        Tooltip orderTip = new Tooltip(s.paving()
                ? "Paves " + formatter.format(s.count()) + " " + ConstructionControl.PAVE_FROM + "s, as the Gravel Road card's Pave would"
                : s.needsCredit()
                ? "short " + money(s.price() - ui.game.getCash()) + " — you will be offered a bill"
                : "Orders " + formatter.format(s.count()) + " × " + s.template().getName() + " as its Build button would");
        Tooltip.install(order, orderTip);
        HBox bottom = new HBox(6, cost, spread, show);
        bottom.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(10, top, middle, bottom, order);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return card;
    }

    /**
     * A suggestion card's three lines (0.7.51), worked out without drawing
     * them (a probe reads them): what the order does - and, on credit, by
     * how much the cash the cards before leave falls short; when it opens
     * and what it is sized for; and its ground - what it is worth, any of
     * it to buy first, and with more than one, why so many - with the
     * ranking behind the choice in the land line's tooltip.
     */
    record CardWords(String does, String sized, String land, String landTip) { }

    CardWords cardWords(BuildAdvice.Suggestion s) {
        String name = s.template().getName();
        String does = doesWhat(s) + (s.closes() ? "." : s.paving() ? " - as far as the gravel roads go."
                : " - as far as " + name + "s go.");
        if (s.needsCredit()) does += s.paving()
                ? " More than the treasury holds after the ones before, by " + money(s.credit()) + ": paving is paid from it."
                : " More than the treasury holds after the ones before, by " + money(s.credit()) + ": Build offers a loan.";

        BuildAdvice.Ahead a = s.ahead();
        String growth = a.k() > 1 ? "people +" + pct1(a.k() - 1) + " on the trend"
                : ui.game.getBusinessInvestment().getPopulationGrowth() <= 0 ? "no growth on the trend"
                : "people held at the homes there are";
        String sized = "Opens " + monthsWait(s.lead()) + "; sized " + formatter.format(Math.round(a.months()))
                + " mo out: " + growth + ", " + pct(BuildAdvice.SLACK) + " to spare.";

        String per = BuildCard.perWords(s.measure());
        // Every card's figure is its order over its life since 0.7.101 (BuildAdvice.lifePerServed()): its quote, its
        // land and its running for BUILD_BOND_YEARS, a paving's with the ground it frees taken off - for power, water,
        // the roads and care over what the city will use of it as it grows, so a building far past the need pays for
        // what stands idle. (A road's was over its life from 0.7.70, a care building's its order over the places
        // lacking from 0.7.71, and the rest their price a unit with their land.)
        boolean used = BuildAdvice.weighsServed(s.measure());
        String priced = (s.paving() ? " over its life, the ground it frees taken off" : " over its life, with its land")
                + (used ? ", for what the city will use of it" : "");
        // ...the card's line says the short of it, the tooltip the whole (four lines at most, 0.7.71's words probe).
        String pricedShort = used ? " used, over its life" : " over its life";
        String land = s.paving()
                ? "Frees " + LandManager.areaWords(-s.landSqFt()) + " ≈ " + money(-s.landValue()) + "."
                : "Land " + LandManager.areaWords(s.landSqFt()) + " ≈ " + money(s.landValue())
                + (s.landShort() > 0 ? " · " + LandManager.areaWords(s.landShort()) + " more than is free: buy it first" : "") + ".";
        // HOW MANY, AND WHY (0.7.51): a big count of a small building - 133 home daycares, until 0.7.71
        // resized them - is the cheapest per unit with its ground, at what one of them serves.
        // ...OF THOSE IN ITS RANK. The rule ranks a building that keeps the need ahead over one that
        // cannot, and one that fits the land left over one that does not, before the price
        // (BuildAdvice.suggestFor()), so the card is the cheapest of those, and its own two say which:
        // city600's 11 gravel roads, $8,200 a trip, were "the cheapest" beside 2 bus networks at $3,431
        // that cannot keep the road ahead (the docs pass, 0.7.51; the runner-up alone cannot tell).
        String which = s.paving() ? (s.closes() ? ", that keeps it ahead" : "")
                : s.closes() ? (s.landShort() > 0 ? " that keeps it ahead" : " that keeps it ahead and fits the land left")
                : s.landShort() > 0 ? "" : " that fits the land left";
        if (!which.isEmpty() && !which.startsWith(",")) which = "," + which;
        if (s.count() > 1) {
            land += " " + formatter.format(s.count()) + " of them: the cheapest per " + per + pricedShort + which
                    + ", at " + shortNumber(s.unit()) + " " + BuildCard.perPlural(s.measure()) + " each.";
        }
        String next = s.runnerUp() == null ? ""
                : switch (s.runnerUpLost()) {
                    case DEARER       -> "; next, " + s.runnerUp().getName() + " at " + money(s.runnerUpPer());
                    case CANNOT_CLOSE -> "; " + s.runnerUp().getName() + " cannot keep it ahead";
                    case NO_ROOM      -> "; " + s.runnerUp().getName() + " needs more land than is left";
                };
        String tip = "Cheapest per " + per + priced + which + ": " + money(s.pricePerUnit()) + next
                + ". Land at the land office's " + groundPrice(ui.game.getLandManager().getOfficePricePerSqFt()) + "/m².";
        return new CardWords(does, sized, land, tip);
    }

    /** A suggestion's building as its card names it (0.7.70): its own name, or the gravel roads a paving paves. */
    static String suggestionName(BuildAdvice.Suggestion s) {
        return s.paving() ? ConstructionControl.PAVE_FROM + ", paved" : s.template().getName();
    }

    /** A share as a percentage to one place: .0135 reads "1.4%" (the card's growth line puts its own "+"). */
    static String pct1(double share) { return Double.isFinite(share) ? String.format("%.1f%%", share * 100) : "—"; }

    /**
     * A suggestion's button (0.7.34): a card's words for its count, off the
     * same verdict (orderPress()) - on credit as well when its quote is more
     * than the cash the suggestions before it leave (Suggestion.needsCredit(),
     * the old "Order on credit"; until 0.7.51 a card was cut to that cash
     * first, and on credit only when it afforded none).
     */
    Pieces.Press suggestionPress(BuildAdvice.Suggestion s) {
        // A paving's (0.7.70): the Gravel Road card's own Pave.
        if (s.paving()) {
            BuildCard.Paving p = BuildCard.paving(ui.game, ui.game.getBuildingManager().getTemplateByName(ConstructionControl.PAVE_FROM));
            if (p != null) return pavePress(p, s.count());
        }
        BuildCard.Verdict v = BuildCard.verdict(ui.game, s.template(), s.count());
        Pieces.Press p = orderPress(v, s.count());
        if (s.needsCredit() && p.look() == Pieces.Look.GO) {
            return new Pieces.Press(Pieces.Look.CREDIT, "Build " + formatter.format(s.count()) + " on credit · "
                    + money(v.quote().total), null);
        }
        return p;
    }

    /* -----------------------------------------------------------------------
       AUTOMATIC BUILDING (0.7.73)

       Jerus: "an automatic build and acquire debt button for basically
       automatic building, with a required slack button that you add, aka
       maintain say 15% surplus service of everything ... right in the build
       menu, and on/off, so that one can focus on other things." Under WHAT
       WOULD HELP MOST, because it builds what that row advises: three cards
       in its columns - the spare margin and the debt limit, each a Ladder
       set at once as every dial is, and what it has done with the switch
       under it. The model is AutoBuilder; every figure is its or the
       Finances tab's. The words are worked out apart from the nodes
       (autoMarginWords(), autoLimitWords(), autoStatusWords(), autoPress(),
       and since 0.7.81 autoCashWords()), so a probe measures them at the
       1,389 window. Since 0.7.81 (batch N6) the limit is the city's debt over
       a year of GDP, its "now" mark the city's own ratio, and under it the
       choice Jerus asked for beside it: "with an optional button of if cash
       available build regardless" - over the limit, build nothing, or build
       from cash anyway.
       ----------------------------------------------------------------------- */

    /** Build's heading chip on every page but the Overview while it is on: the way back to its cards. */
    static final String AUTO_CHIP = "Automatic building on ›";

    /** The section's (i). */
    static final String AUTO_INFO = "Turned on, it orders every month what this page advises for the city's works - "
            + "power, water, roads and transit, care, schools, police, cells - kept ahead of demand with the spare margin "
            + "on top - a first school, station or prison where there is none, from the cash alone. It pays from the cash "
            + "over a month's tax, then borrows on Build's 20-year bond while all the city owes stays under the limit, a "
            + "share of what it produced in the last year; over it, it builds nothing, or only from cash. It buys only its "
            + "orders' own land, orders what the builders open in a year, and takes the next choice where the money will "
            + "not pay for the first.";

    /** The heading's quiet words: on or off. */
    static String autoHint(AutoBuilder ab) {
        return ab.isOn() ? "on: it builds the city's works every month" : "off: you build them";
    }

    /** The two dials and what it has done, a card each. */
    javafx.scene.layout.GridPane autoRow() {
        AutoBuilder ab = ui.game.getAutoBuilder();
        javafx.scene.layout.GridPane grid = equalColumns(3, 10);
        grid.add(autoMarginCard(ab), 0, 0);
        grid.add(autoLimitCard(ab), 1, 0);
        grid.add(autoStatusCard(ab), 2, 0);
        return grid;
    }

    /** A dial's width in its card at the 1,389 window: a third of the page less the card's padding. */
    static final double AUTO_DIAL = (1389 - Palette.RAIL - 36 - 20) / 3.0 - 24;

    /** The spare margin's card's two lines: its title and what it means. */
    static String[] autoMarginWords(double slack) {
        return new String[] { "Spare margin",
                "Every service it keeps is built ahead of " + pct(slack) + " more than the city asks of it today, and "
                        + "off Needs you. A network keeps the quarter in hand Needs you asks of it as well." };
    }

    /** The debt limit's card's two lines, and the mark under its slider: where the city stands now (0.7.81: its debt over a year of GDP; since 0.7.101 all it owes over the output of its last twelve months). */
    static String[] autoLimitWords(AutoBuilder ab, Game g) {
        double now = AutoBuilder.ratio(g);
        String stands = Double.isFinite(now) ? "Now " + AutoBuilder.gdpShare(now) + (ab.within(g) ? "." : ", over it.")
                : "No GDP recorded yet to set its debt against.";
        return new String[] { "Debt limit",
                "All the city owes - bonds and bills, its overdraft, its central bank's advances - at most "
                        + AutoBuilder.gdpShare(ab.getDebtLimit()) + " of what it produced in the last year; over it, it builds"
                        + " nothing. " + stands,
                Double.isFinite(now) ? "now " + AutoBuilder.gdpShare(now) : null };
    }

    /** "Build from cash anyway" (0.7.81): the lead, the two chips, and each chip's tooltip. */
    static String[] autoCashWords() {
        return new String[] { "Over the limit", "Build nothing", "Build from cash anyway",
                "Over the limit it builds nothing and borrows nothing.",
                "Over the limit it builds what the cash over a month's tax pays for, and never borrows." };
    }

    /**
     * The third card's words: on or off, what it has done all told, its
     * latest orders (each with why, for its tooltip), and what holds it back
     * - {state, line, totals, held, held tooltip, then order and why in pairs}.
     */
    static List<String> autoStatusWords(AutoBuilder ab, int month) {
        List<String> w = new ArrayList<>();
        w.add(ab.isOn() ? "On" : "Off");
        w.add(ab.isOn() ? "Every month it orders what this page advises for the city's works."
                : "Turn it on and every month it orders what this page advises for the city's works, within the two dials.");
        w.add(ab.getOrders() == 0 ? "Nothing ordered yet."
                : formatter.format(ab.getOrders()) + (ab.getOrders() == 1 ? " order, " : " orders, ")
                + formatter.format(ab.getBuildings()) + (ab.getBuildings() == 1 ? " building, " : " buildings, ")
                + money(ab.getSpent())
                + (ab.getLandOffers() > 0 ? " and " + money(ab.getLandSpent()) + " for land" : "")
                + (ab.getBorrowed() > 0 ? ", " + money(ab.getBorrowed()) + " of it borrowed" : "") + " so far.");
        List<String> held = ab.held();
        if (ab.isOn() && !held.isEmpty()) {
            List<String> names = new ArrayList<>();
            for (String h : held) names.add(h.substring(0, Math.max(0, h.indexOf(':'))));
            w.add("Held back: " + String.join(", ", names) + ". The inbox says why.");
            w.add(String.join("\n", held));
        } else {
            w.add(null);
            w.add(null);
        }
        for (AutoBuilder.Entry e : ab.latest(3)) {
            int ago = Math.max(0, month - e.month());
            String when = ago == 0 ? "This month" : ago == 1 ? "Last month" : ago + " months ago";
            w.add(when + ": " + formatter.format(e.count()) + " × " + e.building() + ", " + money(e.cost())
                    + (e.landCost() > 0 ? " + " + money(e.landCost()) + " of land" : "")
                    + (e.borrowed() > 0 ? " (" + money(e.borrowed()) + " borrowed)" : ""));
            w.add(e.why());
        }
        return w;
    }

    /** The switch, as its button says it. */
    static Pieces.Press autoPress(boolean on) {
        return on ? new Pieces.Press(Pieces.Look.HELD, "Turn automatic building off", "you build the city's works again")
                : new Pieces.Press(Pieces.Look.GO, "Turn automatic building on", "every month, within the two dials");
    }

    /** A card in the row: its title, a dial and its words. */
    VBox autoCard(String title, javafx.scene.Node dial, String words) {
        Label head = new Label(title);
        head.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        Label line = new Label(words);
        line.setWrapText(true);
        line.setStyle(wordsAt(10.5, Palette.TEXT_LABEL));
        VBox card = new VBox(4, head, dial, line);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return card;
    }

    VBox autoMarginCard(AutoBuilder ab) {
        String[] w = autoMarginWords(ab.getSlack());
        VBox dial = Ladder.of(0, AutoBuilder.SLACK_MOST * 100, AutoBuilder.STEP * 100, v -> String.format("%.0f%%", v))
                .current(ab.getSlack() * 100)
                .appliesAtOnce(v -> { ab.setSlack(v / 100, ui.game.getDecisions()); showOverview(); })
                .wide(AUTO_DIAL).build();
        return autoCard(w[0], dial, w[1]);
    }

    VBox autoLimitCard(AutoBuilder ab) {
        String[] w = autoLimitWords(ab, ui.game);
        double now = AutoBuilder.ratio(ui.game);
        Ladder ladder = Ladder.of(0, AutoBuilder.DEBT_LIMIT_MOST * 100, AutoBuilder.DEBT_STEP * 100, v -> String.format("%.0f%%", v))
                .current(ab.getDebtLimit() * 100)
                .appliesAtOnce(v -> { ab.setDebtLimit(v / 100, ui.game.getDecisions()); showOverview(); })
                .wide(AUTO_DIAL);
        if (w[2] != null && now * 100 <= AutoBuilder.DEBT_LIMIT_MOST * 100) {
            ladder.marks(new double[] { now * 100 }, new String[] { w[2] });
        }
        VBox card = autoCard(w[0], ladder.build(), w[1]);
        // ...and Build from cash anyway (0.7.81), under the words: two chips, applied at once.
        String[] c = autoCashWords();
        javafx.scene.layout.FlowPane chips = chipStrip(new String[] { c[1], c[2] }, ab.isCashAnyway() ? c[2] : c[1],
                Palette.SIZE_LABEL, name -> { ab.setCashAnyway(c[2].equals(name), ui.game.getDecisions()); showOverview(); });
        chips.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < chips.getChildren().size(); i++) {
            if (chips.getChildren().get(i) instanceof Button b) {
                Tooltip tip = new Tooltip(c[3 + i]);
                tip.setShowDelay(Duration.millis(300));
                b.setTooltip(tip);
            }
        }
        Label lead = new Label(c[0]);
        lead.setStyle(wordsAt(10.5, Palette.TEXT_MUTED));
        lead.setMinWidth(Region.USE_PREF_SIZE);
        HBox row = new HBox(8, lead, chips);
        row.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().add(row);
        return card;
    }

    VBox autoStatusCard(AutoBuilder ab) {
        List<String> w = autoStatusWords(ab, ui.game.getMonth());
        Label state = new Label(w.get(0));
        state.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, ab.isOn() ? Palette.GOOD : Palette.TEXT_MUTED));
        Label line = new Label(w.get(1));
        line.setWrapText(true);
        line.setStyle(wordsAt(10.5, Palette.TEXT_LABEL));
        Label totals = new Label(w.get(2));
        totals.setWrapText(true);
        totals.setStyle(wordsAt(10.5, Palette.TEXT_LABEL));
        VBox card = new VBox(4, state, line, totals);
        if (w.get(3) != null) {
            Label held = new Label(w.get(3));
            held.setWrapText(true);
            held.setStyle(wordsAt(10.5, Palette.WARN));
            Tooltip tip = new Tooltip(w.get(4));
            tip.setShowDelay(Duration.millis(300));
            Tooltip.install(held, tip);
            card.getChildren().add(held);
        }
        for (int i = 5; i + 1 < w.size(); i += 2) {
            Label order = new Label(w.get(i));
            order.setWrapText(true);
            order.setStyle(wordsAt(10, Palette.TEXT_BODY));
            Tooltip tip = new Tooltip(w.get(i + 1));
            tip.setShowDelay(Duration.millis(300));
            Tooltip.install(order, tip);
            card.getChildren().add(order);
        }
        Region push = new Region();
        VBox.setVgrow(push, Priority.ALWAYS);
        Pieces.ActionButton toggle = actionButton(Icons.BUILD, Palette.BUILDING, ACTION_TALL, autoPress(ab.isOn()),
                () -> { ab.setOn(!ab.isOn(), ui.game.getDecisions()); showOverview(); });
        card.getChildren().addAll(push, toggle);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMaxHeight(Double.MAX_VALUE);
        card.setStyle("-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: "
                + (ab.isOn() ? Palette.GOOD : Palette.EDGE) + ";");
        return card;
    }

    /* ----------------------------- the market builds these ----------------------------- */

    /** The nine, quieter. */
    javafx.scene.layout.GridPane marketRow() {
        List<BuildAdvice.Category> theirs = new ArrayList<>();
        for (BuildAdvice.Category c : BuildAdvice.categories()) if (!c.cityBuilds()) theirs.add(c);
        javafx.scene.layout.GridPane grid = equalColumns(theirs.size(), 8);
        for (int i = 0; i < theirs.size(); i++) grid.add(marketTile(theirs.get(i)), i, 0);
        return grid;
    }

    /** One of the market's: its icon, its name, what stands and what is on site. */
    HBox marketTile(BuildAdvice.Category c) {
        // Nine to a row: at 1,280 a column is about 118 px, and "Luxury shops"
        // wraps to two lines there rather than running into its neighbour.
        Label name = new Label(c.name());
        name.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_LABEL));
        name.setWrapText(true);
        name.setMinWidth(0);
        Label standing = new Label(formatter.format(standingIn(c.types())));
        standing.setStyle(Palette.figure(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        int onSite = onSiteIn(c.types());
        Label under = new Label(onSite > 0 ? formatter.format(onSite) + " on site" : "standing");
        under.setStyle(wordsAt(9.5, onSite > 0 ? Palette.BUILDING : Palette.TEXT_MUTED));
        VBox words = new VBox(0, name, standing, under);
        words.setMinWidth(0);
        HBox.setHgrow(words, Priority.ALWAYS);
        HBox tile = new HBox(8, iconSquare(Icons.ofCategory(c.name()), INVESTOR_DOT, 28, 15), words);
        tile.setAlignment(Pos.CENTER_LEFT);
        tile.setMaxWidth(Double.MAX_VALUE);
        String rest = "-fx-padding: 8 8 8 8; -fx-background-color: " + Palette.PANEL + ";"
                + " -fx-background-radius: 6; -fx-border-radius: 6; -fx-cursor: hand; -fx-border-color: ";
        tile.setStyle(rest + Palette.EDGE + ";");
        tile.setOnMouseEntered(e -> tile.setStyle(rest + Palette.BUSINESS + ";"));
        tile.setOnMouseExited(e -> tile.setStyle(rest + Palette.EDGE + ";"));
        Tooltip.install(tile, new Tooltip(c.name() + ": investors build these themselves. You can build them too."));
        tile.setOnMouseClicked(e -> openCategory(c.name()));
        return tile;
    }

    /* =====================================================================
       A CITY CATEGORY, OPENED ON ITS NEEDS (0.7.24)

       Healthcare as the round-2 mockups draw it (BuildHealthB), and the same
       shape for Utilities, Roads & transit, Education and Safety: a ring per
       measure the category serves (BuildAdvice.measuresOf()) - its figure,
       how far short in people or places, and what is on site with its wait
       - the worst picked first, a click on another showing its buildings;
       then the buildings that serve the picked measure as cards, each with
       what it does at today's staffing in the measure's own verb ("seats",
       "puts N officers on the street" - BuildCard.doesWords()), what the city has and
       has on site, the all-in price with the sticker smaller, a bar for the
       cost per unit served and one for the posts the city likely cannot fill
       per 10,000 served (none for a building with no posts), a tag on the
       cheapest per unit and on the fewest unfilled posts, its staff and land,
       and the stepper and Build as before, with every warning the card gave
       (land, the bill, the queue's months, what is on site). With a stepper
       above zero, an order bar at the foot: the order, its price against the
       cash, the measure now, when what is on site opens and with the order,
       what it needs, and one Build for all of it - Enter's own path.

       Since 0.7.25 every card is the one card (ONE BUILDING, AS A CARD),
       its figures BuildCard's, and the market's nine are drawn the same way
       in their groups (A MARKET CATEGORY, IN ITS GROUPS). The city's cards
       got back what 0.7.24 had dropped - the (i) and its cover, "runs $X/mo",
       +100 and the share of the land free - and a ring with one building
       draws its bars' figures with no track.
       ===================================================================== */

    /** Which ring each city category has picked, by name; none picks its worst. Forgotten on arriving (openCategory()). */
    final java.util.Map<String, BuildAdvice.Measure> measurePicked = new java.util.HashMap<>();

    /** A card's width, on every Build page since 0.7.25 (a city category's only, in 0.7.24). */
    static final double NEED_CARD = 300;

    /** The order bar, refilled in place as a stepper moves. */
    private VBox orderBar;

    /** The measure the order bar reads, the picked one; null on a market page (orderMarket). */
    private BuildAdvice.Measure orderMeasure;

    /** The worst of a category's measures: its NEEDS YOU row first in the panel's order, else the first ring. */
    BuildAdvice.Measure worstMeasure(BuildAdvice.Category c, List<BuildAdvice.Measure> measures, List<CityNeeds.Need> all) {
        CityNeeds.Need worst = CityNeeds.worst(all, goOf(c.name()));
        BuildAdvice.Measure m = worst == null ? null : BuildAdvice.measureOf(worst);
        if (m != null && m.kind() == BuildAdvice.Kind.PLOTS) m = BuildAdvice.Measure.of(BuildAdvice.Kind.DEATH);
        return m != null && measures.contains(m) ? m : measures.get(0);
    }

    /**
     * A measure's verdict. Since 0.7.41 a served measure's is the one verdict
     * on what it serves (BuildAdvice.verdict()) - transit's and a school's
     * NEEDS YOU would not list (CityNeeds.listsSchool()) none, -1 - which
     * also reads a stage of the basic ladder that is not the bottleneck on
     * the SCHOOLS row's lines, as this did; the dead, the plots, the police
     * and the cells, their NEEDS YOU row's level, or -1 with none.
     */
    int levelOf(BuildAdvice.Measure m, List<CityNeeds.Need> all) {
        if (BuildAdvice.isServed(m)) return BuildAdvice.verdict(ui.game, m, java.util.Map.of()).level();
        CityNeeds.Need n = BuildAdvice.needFor(all, m);
        return n != null ? n.level() : -1;
    }

    /** A city category's page. */
    void showCityCategory(BuildAdvice.Category c) {
        ui.clearMenu("handleAllBuildingMenus", () -> showCityCategory(c));
        buildCategory = c.name();
        pageCards.clear();
        pageTitle = c.name();
        pageCategories = c.types();
        pendingHint = new Label("↵ builds what is pending · ⌫ clears it");
        pendingHint.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED) + " -fx-padding: 6 0 0 0;");

        List<CityNeeds.Need> all = CityNeeds.measure(ui.game, SummaryScreen.WORDS);
        List<BuildAdvice.Measure> measures = BuildAdvice.measuresOf(c.name());
        BuildAdvice.Measure picked = measurePicked.get(c.name());
        if (picked == null || !measures.contains(picked)) picked = worstMeasure(c, measures, all);
        final BuildAdvice.Measure on = picked;
        orderMeasure = on;
        orderMarket = false;

        VBox page = widePage();
        HBox head = buildHead(c.name(), c.types());
        head.setStyle("-fx-padding: 0 0 4 0;");
        javafx.scene.layout.FlowPane strip = buildStrip(c.name());
        strip.setStyle("-fx-padding: 0;");

        // The rings: one per measure; Education's nine smaller, in one row.
        boolean small = measures.size() > 4;
        javafx.scene.layout.GridPane rings = equalColumns(measures.size(), small ? 6 : 10);
        for (int i = 0; i < measures.size(); i++) rings.add(measureCard(c, measures.get(i), all, measures.get(i).equals(on), small), i, 0);

        // The picked measure's buildings, as BuildCard groups them: one group,
        // the ring's, its bars scaled and tagged within it (0.7.25).
        BuildCard.Group group = BuildCard.groups(ui.game, c.name(), on).get(0);
        List<BuildingsTemplate> shown = new ArrayList<>();
        for (BuildCard.Figures f : group.cards()) shown.add(f.template());
        double[] fill = ui.game.getPopulationManager().getJobFillRate();
        // The order bar first: each card reprices into it as it is drawn.
        orderBar = new VBox();
        javafx.scene.layout.FlowPane cards = new javafx.scene.layout.FlowPane(TILE_GAP, TILE_GAP);
        cards.setAlignment(Pos.TOP_LEFT);
        cards.prefWrapLengthProperty().bind(ui.menuScroller.widthProperty().subtract(60));
        for (BuildCard.Figures f : group.cards()) cards.getChildren().add(card(f, group, c, on));

        // The four limits every category page has shown (constraintsBar()):
        // the materials in the yard, their import price, the land free and the
        // builders - the states a card's warnings come from.
        HBox limits = constraintsBar();

        // ...and at the heading's right, the way to the why (0.7.28): the Services page behind this ring -
        // for the road and transit, Infrastructure's, said for what it shows (0.7.29). A road has no
        // posts, so its "these need no staff" gives way to the door.
        HBox headRight = new HBox(Palette.GAP_LOOSE);
        if (on.kind() == BuildAdvice.Kind.ROADS) {
            headRight.getChildren().add(door("what is on the road: Infrastructure", Palette.PEOPLE, () -> why(on)));
        } else if (on.kind() == BuildAdvice.Kind.TRANSIT) {
            headRight.getChildren().addAll(scarceNote(shown, fill),
                    door("who rides: Infrastructure", Palette.PEOPLE, () -> why(on)));
        } else {
            headRight.getChildren().addAll(scarceNote(shown, fill), door("why", Palette.PEOPLE, () -> why(on)));
        }
        headRight.setAlignment(Pos.CENTER_RIGHT);
        page.getChildren().addAll(head, strip, limits, rings,
                sectionHead(on.label().toUpperCase() + " · " + measureSubtitle(on), headRight),
                cards, orderBar, pendingHint);
        showPendingHint();
        refreshOrderBar();
        ui.rootMenu.getChildren().add(page);
    }

    /** A few words on what a measure is for, beside its heading. */
    String measureSubtitle(BuildAdvice.Measure m) {
        switch (m.kind()) {
            case POWER:   return "generation for every building and home";
            case WATER:   return "treatment for every tap";
            case ROADS:   return "the trips the city's buildings make";
            case TRANSIT: return "commuters off the road";
            case CARE:    return m.care() == CareType.GENERAL ? "somewhere to see a doctor"
                               : m.care() == CareType.CHILDCARE ? "babies and children" : "the over-seventies";
            case DEATH:   return "plots and ovens for the dead";
            case SCHOOL: {
                String[] head = schoolHeading(m.school()).split("  -  ");
                return head.length > 1 ? head[1] : m.school().getLabel().toLowerCase();
            }
            case POLICE:  return "officers on the street";
            default:      return "somewhere to hold the caught";
        }
    }

    /** At the heading's right: which of these buildings' staff the city fills worst. */
    Label scarceNote(List<BuildingsTemplate> shown, double[] fill) {
        Label note = new Label(scarceWords(shown, fill));
        note.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        return note;
    }

    /** ...its words, worked out without the label (0.7.28: the Services care heading says them too). */
    String scarceWords(List<BuildingsTemplate> shown, double[] fill) {
        JobType worst = null;
        double at = 2;
        for (BuildingsTemplate t : shown) {
            JobType j = BuildAdvice.scarceJob(t, fill);
            if (j == null) continue;
            double f = j.ordinal() < fill.length ? fill[j.ordinal()] : 1;
            if (f < at) { at = f; worst = j; }
        }
        return worst == null ? "these need no staff"
                : at >= .995 ? "every post these need is filling"
                : String.format("%s are the scarcest: %.0f%% of their posts filled", jobPlural(worst), at * 100);
    }

    /** A job type in plain words, plural: "doctors", "nurses", "unskilled workers". */
    String jobPlural(JobType job) {
        switch (job) {
            case NO_DIPLOMA:          return "unskilled workers";
            case DIPLOMA:             return "diploma holders";
            case COLLEGE_HEALTH:      return "nurses";
            case COLLEGE_BUSINESS:    return "business-college graduates";
            case COLLEGE_ENGINEERING: return "technicians";
            case UNIV_DOCTOR:         return "doctors";
            case UNIV_LAW:            return "lawyers";
            case UNIV_FINANCE:        return "finance graduates";
            case UNIV_SCIENCE:        return "scientists";
            case UNIV_HIGHTECH_ENG:   return "high-tech engineers";
            case UNIV_POLICY:         return "policy graduates";
            default:                  return jobLabel(job);
        }
    }

    /** What a measure is counted in, for a ring's "short by N ..." - only care reaches it since 0.7.25, when the cards' "serves N ..." moved to BuildCard.doesWords(). */
    static String unitWords(BuildAdvice.Measure m) {
        switch (m.kind()) {
            case POWER:   return "kW";
            case WATER:   return "units a month";
            case ROADS:   return "trips a month";
            case TRANSIT: return "riders";
            case CARE:    return m.care() == CareType.CHILDCARE ? "children" : m.care() == CareType.SENIOR ? "seniors" : "people";
            case DEATH:   return "of the dead";
            case SCHOOL:  return "places";
            case POLICE:  return "officers";
            default:      return "cells";
        }
    }

    /** The staff bar's figure: a tenth below a hundred, whole and grouped above. */
    static String perTenThousand(double staff) {
        return !Double.isFinite(staff) ? "—" : staff >= 100 ? formatter.format(Math.round(staff)) : String.format("%.1f", staff);
    }

    /**
     * What a ring says, worked out without drawing it: its figure, how much
     * of it is drawn, its verdict's colour, how far short in people or
     * places, and what is on site with its wait. measureCard() draws it; a
     * probe can read it without the toolkit.
     */
    record RingWords(String figure, double arc, String tone, String shortLine, String onSite, int units,
                     CityNeeds.Need need) { }

    RingWords ringWords(BuildAdvice.Measure m, List<CityNeeds.Need> all) {
        Game game = ui.game;
        CityNeeds.Need need = BuildAdvice.needFor(all, m);
        int level = levelOf(m, all);
        String tone = verdict(level);
        double[] sd = BuildAdvice.supplyDemand(game, m, java.util.Map.of());
        String figure, shortLine;
        double arc;
        double gap = sd[1] - sd[0];
        // SERVED (0.7.41): the figure is what the measure serves, unclamped, the arc that held to a
        // whole ring, and the line opens with "served" and the verdict's word - for power, water and
        // the road it was the load, a ring full when it was over.
        CityNeeds.Served served = BuildAdvice.verdict(game, m, java.util.Map.of());
        String says = served == null ? null : servedWords(served) + " · ";
        switch (m.kind()) {
            case POWER: case WATER: {
                figure = CityNeeds.servedPct(served.share());
                arc = arc(served);
                shortLine = says + (m.kind() == BuildAdvice.Kind.POWER
                        ? (gap > 0 ? power(gap) + " short" : power(-gap) + " spare")
                        : gap > 0 ? shortNumber(gap) + " units short" : shortNumber(-gap) + " units spare");
                break;
            }
            case ROADS: {
                figure = CityNeeds.servedPct(served.share());
                arc = arc(served);
                // ...with the flow beside it (0.7.29): the ring is what it serves, the flow what it costs.
                shortLine = says + pct(game.getInfrastructureManager().getThroughputRatio()) + " flow · "
                        + (gap > 0 ? shortNumber(gap) + " trips over capacity" : shortNumber(-gap) + " trips spare");
                break;
            }
            case TRANSIT:
                figure = CityNeeds.servedPct(served.share());
                arc = arc(served);
                // ROOM, NOT RIDERS (0.7.29): the ring is what the stock could carry, and it
                // said "carries 62.5k" where 41.4k rode - the riders under it are the car-less
                // in reach and the owners who chose the bus since 0.7.49 (Infrastructure ›
                // Transit draws them by reason).
                shortLine = sd[0] > 0 ? says + "room for " + shortNumber(Math.min(sd[0], sd[1])) + " of " + shortNumber(sd[1])
                        + " · " + shortNumber(game.getInfrastructureManager().getTransitRiders()) + " ride"
                        : "no transit yet";
                break;
            case DEATH:
                if (need != null && need.kind() == CityNeeds.Kind.PLOTS) {
                    figure = String.format("%.0f mo", need.value());
                    arc = Math.min(1, need.value() / CityNeeds.PLOTS_YELLOW);
                    shortLine = "of burial plots left";
                } else {
                    double waiting = game.getHealthcare().getUnburied();
                    double toHandle = game.getHealthcare().getDeaths() + waiting;
                    arc = toHandle > 0 ? Math.max(0, 1 - waiting / toHandle) : 1;
                    figure = pct(arc);
                    shortLine = waiting >= .5 ? people(waiting) + " unburied" : "every funeral has a place";
                }
                break;
            case SCHOOL:
                arc = arc(served);
                figure = CityNeeds.servedPct(served.share());
                shortLine = says + (m.school().isBasic()
                        ? (gap >= .5 ? "short " + people(gap) + " places" : "a place for every child")
                        : sd[1] < CityNeeds.SEATS_FLOOR ? people(sd[1]) + " would come and be hired"
                        : people(sd[0]) + " seats · " + people(sd[1]) + " would come and be hired");
                break;
            case POLICE: {
                double vs = need != null ? need.value() : BuildAdvice.figure(game, m, java.util.Map.of());
                figure = String.format("%.1f×", vs);
                arc = vs > 0 ? Math.min(1, 1 / vs) : 1;
                shortLine = gap >= .5 ? people(gap) + " officers short of full cover" : "full cover";
                break;
            }
            case CELLS: {
                arc = BuildAdvice.cover(game, m, java.util.Map.of());
                figure = pct(arc);
                double unheld = game.getCrime().getNotHeld();
                shortLine = unheld >= 1 ? people(unheld) + " caught, not held" : "a cell for everyone caught";
                break;
            }
            default: {
                arc = arc(served);
                figure = CityNeeds.servedPct(served.share());
                shortLine = says + (gap >= .5 ? "short by " + people(gap) + " " + unitWords(m) : "everyone has a place");
            }
        }
        java.util.Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(game, m);
        int units = BuildAdvice.units(site);
        double soonest = Double.NaN;
        for (BuildingsTemplate t : site.keySet()) {
            double months = game.onSiteMonths(t);
            if (!Double.isNaN(months) && !(months >= soonest)) soonest = months;
        }
        String onSite = units > 0 ? formatter.format(units) + " on site · " + monthsWait(soonest) : "nothing on site";
        return new RingWords(figure, arc, tone, shortLine, onSite, units, need);
    }

    /** One ring: its figure, how far short, and what is on site with its wait; a click picks it. */
    HBox measureCard(BuildAdvice.Category c, BuildAdvice.Measure m, List<CityNeeds.Need> all, boolean picked, boolean small) {
        RingWords w = ringWords(m, all);
        String figure = w.figure(), tone = w.tone(), shortLine = w.shortLine(), onSite = w.onSite();
        double arc = w.arc();
        int units = w.units();
        CityNeeds.Need need = w.need();

        String tip = m.label() + ": " + shortLine + (need != null ? "\nNEEDS YOU: " + need.label().toLowerCase()
                + " · " + need.reading() : "") + "\nClick for its buildings.";
        if (!small) {
            // The card is Pieces.ringCard() since 0.7.27, which the People page's care row draws too.
            return ringCard(figure, arc, tone, m.label(), null, shortLine, onSite,
                    units > 0 ? Palette.BUILDING : Palette.TEXT_MUTED, 56, picked, null, () -> {
                        measurePicked.put(c.name(), m);
                        showCityCategory(c);
                    }, tip);
        }
        Label name = new Label(m.label());
        name.setStyle(Palette.strong(small ? Palette.SIZE_BODY : Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        name.setWrapText(true);
        Label sl = new Label(shortLine);
        sl.setWrapText(true);
        sl.setStyle(wordsAt(small ? 9 : 10.5, Palette.TEXT_LABEL));
        Label os = new Label(onSite);
        os.setWrapText(true);
        os.setStyle(Palette.words(small ? Palette.SIZE_CAPTION : Palette.SIZE_LABEL,
                units > 0 ? Palette.BUILDING : Palette.TEXT_MUTED));
        VBox words = new VBox(1, name, sl, os);
        words.setMinWidth(0);
        HBox.setHgrow(words, Priority.ALWAYS);
        double size = small ? 42 : 56;
        HBox card = new HBox(small ? 6 : 10, ring(arc, tone, size, small ? 5 : 6, figure, small ? 10.5 : 12.5), words);
        if (small) {
            // Nine in a row: the ring above its words.
            card = new HBox(0);
            VBox stacked = new VBox(4, ring(arc, tone, size, 5, figure, 10.5), name, sl, os);
            stacked.setAlignment(Pos.TOP_LEFT);
            card.getChildren().add(stacked);
        }
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(Double.MAX_VALUE);
        String rest = "-fx-padding: " + (small ? "8 8 8 8" : "10 12 10 12") + "; -fx-background-radius: 8; -fx-border-radius: 8;"
                + " -fx-cursor: hand; -fx-background-color: " + (picked ? Palette.PINNED : Palette.RAISED) + "; -fx-border-color: ";
        String edge = picked ? Palette.BUILDING : Palette.EDGE;
        final HBox shownCard = card;
        shownCard.setStyle(rest + edge + ";");
        shownCard.setOnMouseEntered(e -> shownCard.setStyle(rest + (picked ? Palette.BUILDING : Palette.ACCENT) + ";"));
        shownCard.setOnMouseExited(e -> shownCard.setStyle(rest + edge + ";"));
        Tooltip tipped = new Tooltip(tip);
        tipped.setShowDelay(Duration.millis(300));
        Tooltip.install(shownCard, tipped);
        shownCard.setOnMouseClicked(e -> {
            measurePicked.put(c.name(), m);
            showCityCategory(c);
        });
        return shownCard;
    }

    /** Words at a size between Palette's steps (0.7.24's cards and rings), in a colour. */
    static String wordsAt(double size, String colour) {
        return "-fx-font-size: " + size + "px; -fx-text-fill: " + colour + ";";
    }

    /** A figure at a size between Palette's steps: Palette.figure()'s face. */
    static String figureAt(double size, String colour) {
        return Palette.Fonts.monoSemiBold() + " -fx-font-size: " + size + "px; -fx-text-fill: " + colour + ";";
    }

    /** A figure that may be large: "2,500", "120k", "1.2M". */
    static String shortOrWhole(double v) {
        return v >= 100_000 ? shortNumber(v) : formatter.format(Math.round(v));
    }

    // A card's tag - the best of these on one count - is Pieces.tag() in green since 0.7.26.

    /** The money bar's (i) (0.7.45): what a build's money is struck at this month. */
    String moneyBarInfo() {
        return String.format("Cash costs and upkeep are struck at ×%.3f their founding figures this month (what money"
                + " constants are struck at: what people expect prices to be); the builders' labour at today's wage.",
                ui.game.getExpectations().getStruckLevel());
    }

    /** A road card's money bar's (i) (0.7.70): what its cost over its life is made of - BuildAdvice.lifetime(), the advice's own figure. */
    String roadBarInfo() {
        return String.format("What a road costs over %d years, a trip it takes off the road past its strained line (a"
                + " paved road's or a highway's grade for lorries in it): its price, its land at the land office's"
                + " price, and its repairs (1%% of its price a year) and power at today's prices, discounted at %.1f%%"
                + " a year, the city's %d-year rate less the inflation it expects. The advice ranks the roads by it.",
                BuildAdvice.LIFE_MONTHS / 12, BuildAdvice.lifeRate(ui.game) * 100, Game.BUILD_BOND_YEARS);
    }

    /**
     * One of the card's two bars: what it measures, its figure, and the bar
     * scaled across its group - with no track when `track` is false (0.7.25):
     * a group of one, where a full bar compares nothing, or a card with
     * nothing to price against.
     */
    VBox barRow(String label, String value, double share, String colour, double wide, String svg, boolean track) {
        Label what = new Label(label);
        what.setStyle(wordsAt(9.5, Palette.TEXT_LABEL));
        // "posts the city can't fill, per 10,000 patients" nearly fills the
        // card: it wraps to a second line rather than losing its end.
        what.setWrapText(true);
        what.setMinWidth(0);
        if (svg != null) {
            what.setGraphic(icon(svg, colour, 11));
            what.setGraphicTextGap(4);
        }
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label figure = new Label(value);
        figure.setStyle(figureAt(9.5, Palette.TEXT_HEAD));
        figure.setMinWidth(Region.USE_PREF_SIZE);
        HBox row = new HBox(4, what, gap, figure);
        row.setAlignment(Pos.CENTER_LEFT);
        return track ? new VBox(2, row, bar(share, colour, wide, 4)) : new VBox(row);
    }

    /**
     * The order bar: what the page's steppers add up to, its price against
     * the cash, the picked measure now, when what is on site opens and with
     * the order - as a stacked bar and in figures - what the order needs,
     * and one Build for all of it (buildPending(), Enter's path). On a
     * market page (0.7.25) it has no measure: the order, its price, what it
     * needs, and Build.
     */
    void refreshOrderBar() {
        if (orderBar == null || (orderMeasure == null && !orderMarket)) return;
        Game game = ui.game;
        java.util.Map<BuildingsTemplate, Integer> order = new java.util.LinkedHashMap<>();
        for (PageCard card : pageCards) {
            int n = orderQty.getOrDefault(card.template().getName(), 0);
            if (n > 0) order.put(card.template(), n);
        }
        orderBar.getChildren().clear();
        if (order.isEmpty()) {
            showIf(orderBar, false);
            return;
        }
        showIf(orderBar, true);
        BuildAdvice.Measure m = orderMeasure;

        int units = 0;
        StringBuilder names = new StringBuilder();
        for (java.util.Map.Entry<BuildingsTemplate, Integer> e : order.entrySet()) {
            units += e.getValue();
            if (names.length() > 0) names.append(" + ");
            names.append(formatter.format(e.getValue())).append(" × ").append(e.getKey().getName());
        }
        double total = BuildAdvice.quoteTotal(game, order);
        Label what = new Label(names.toString());
        what.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        what.setWrapText(true);
        Label price = new Label(money(total) + " all in, of your " + money(game.getCash()));
        price.setStyle(Palette.figure(Palette.SIZE_LABEL, total > game.getCash() ? Palette.WARN : Palette.TEXT_LABEL));
        double[] needs = BuildAdvice.needs(order);
        StringBuilder staff = new StringBuilder();
        for (JobType job : JobType.values()) {
            double n = needs[job.ordinal()];
            if (n <= 0) continue;
            if (staff.length() > 0) staff.append(" · ");
            staff.append(formatter.format(Math.round(n))).append(" ").append(n == 1 ? jobLabel(job) : jobPlural(job));
        }
        if (staff.length() > 0) staff.append(" · ");
        double land = needs[needs.length - 1];
        staff.append(LandManager.areaWords(land));
        Label wants = new Label(staff.toString());
        wants.setWrapText(true);
        wants.setStyle(wordsAt(9.5,
                land > game.getLandManager().getAvailableSqFt() ? Palette.BAD : Palette.TEXT_MUTED));
        VBox left = new VBox(2, what, price, wants);
        left.setPrefWidth(300);
        left.setMinWidth(220);

        // The cards' button (0.7.34), saying the whole order: on credit when it is more than the cash,
        // where the funding page opens for the whole run (0.7.40; for the first order short of it before).
        Pieces.ActionButton go = actionButton(Icons.BUILD, Palette.BUILDING, ACTION_TALL,
                orderBarPress(units, total, game.getCash()), this::buildPending);
        go.setMinWidth(Region.USE_PREF_SIZE);
        Tooltip.install(go, new Tooltip("Places every order on this page, as each card's Build would (Enter does the same)"));

        // A market page (0.7.25): the order and Build, no measure between them.
        if (m == null) {
            HBox.setHgrow(left, Priority.ALWAYS);
            left.setMaxWidth(Double.MAX_VALUE);
            HBox bar = new HBox(18, left, go);
            bar.setAlignment(Pos.CENTER_LEFT);
            bar.setStyle("-fx-padding: 12 14 12 14; -fx-background-color: " + Palette.RAISED + ";"
                    + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.BUILDING + ";");
            VBox.setMargin(bar, new javafx.geometry.Insets(4, 0, 0, 0));
            orderBar.getChildren().add(bar);
            return;
        }

        java.util.Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(game, m);
        java.util.Map<BuildingsTemplate, Integer> withOrder = BuildAdvice.plus(site, order);
        double now = BuildAdvice.cover(game, m, java.util.Map.of());
        double whenOpen = BuildAdvice.cover(game, m, site);
        double with = BuildAdvice.cover(game, m, withOrder);
        Label measure = new Label(m.label().toLowerCase());
        measure.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        int onSiteUnits = BuildAdvice.units(site);
        // SERVED (0.7.41): a served measure's three figures are what it serves, "served 92% now · ...", and
        // they turn green when the order leaves it enough (the one verdict) - not merely off NEEDS YOU's list.
        CityNeeds.Served withServed = BuildAdvice.verdict(game, m, withOrder);
        Label figures = new Label((withServed != null ? CityNeeds.SERVED + " " : "")
                + gaugeText(m, java.util.Map.of()) + " now · "
                + (onSiteUnits > 0 ? gaugeText(m, site) + " when the "
                        + formatter.format(onSiteUnits) + " on site open · " : "")
                + gaugeText(m, withOrder) + " with " + (units == 1 ? "this" : "these"));
        figures.setStyle(Palette.figure(Palette.SIZE_LABEL, (withServed != null ? withServed.enough()
                : BuildAdvice.clear(m, BuildAdvice.figure(game, m, withOrder))) ? Palette.GOOD : Palette.TEXT_HEAD));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox labels = new HBox(6, measure, gap, figures);
        labels.setAlignment(Pos.CENTER_LEFT);
        // The stacked bar is the house's segment bar (0.7.40): a Pane that lays its parts out at the width
        // it is given, its minimum nothing. It was an HBox of three Regions whose minimum widths were bound
        // to a share of the HBox's own width, so rounding up made them add to more than it - the bar
        // widened, the shares followed, and the order bar crept right while nothing moved (Jerus).
        SegmentBar stacked = segmentBar(List.of(Segment.of(Math.max(0, Math.min(1, now)), Palette.MONEY),
                Segment.of(Math.max(0, Math.min(1, whenOpen - now)), Palette.BUILDING_DARK),
                Segment.of(Math.max(0, Math.min(1, with - Math.max(now, whenOpen))), Palette.BUILDING)),
                1, List.of(), 0, 10);
        HBox key = new HBox(Palette.GAP_LOOSE, keySwatch(Palette.MONEY, withServed != null ? "served" : "covered"),
                keySwatch(Palette.BUILDING_DARK, "on site"), keySwatch(Palette.BUILDING, "this order"));
        VBox middle = new VBox(4, labels, stacked, key);
        HBox.setHgrow(middle, Priority.ALWAYS);

        HBox bar = new HBox(18, left, middle, go);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-padding: 12 14 12 14; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.BUILDING + ";");
        VBox.setMargin(bar, new javafx.geometry.Insets(4, 0, 0, 0));
        orderBar.getChildren().add(bar);
    }

    /** The order bar's Build (0.7.34): "Build 5 · $X" with the order's total, or "Build 5 on credit · $X" past the cash. */
    Pieces.Press orderBarPress(int units, double total, double cash) {
        String words = "Build " + formatter.format(units) + (total > cash ? " on credit · " : " · ") + money(total);
        return new Pieces.Press(total > cash ? Pieces.Look.CREDIT : Pieces.Look.GO, words, null);
    }

    /* =====================================================================
       A MARKET CATEGORY, IN ITS GROUPS (0.7.25)

       The nine investors build - Homes, Shops, Industry, Offices, Farms,
       Rail, Vehicles, Luxury shops, Restaurants - on the one card the city's
       five have (ONE BUILDING, AS A CARD), and under their owning sectors
       rather than in one grid: Industry is seven groups (food mills, food
       processing, steel, fabrication and machinery, iron, building
       materials, builders) and Shops two (the groceries and the bank's
       branches), so a card's bars and tags compare it with the buildings it
       competes with, not with everything on the page. Jerus asked it of the
       healthcare list when it was one column of buttons - "group it further
       cause its hard to know whats for what" - and the old page grouped
       care and the schools for it, until 0.7.24 opened the city's five on
       their needs; the market's groups are the same answer. Each group's
       heading says what it is for and, at its right where there is one, the
       sector's own figure the group answers to (BuildCard.Note).

       The page keeps what the 0.7.21 page had: the head and the strip, the
       limits (constraintsBar()), whose job these are (whoBuildsThis() and its
       (i)), the receipt and the keys' caption - and gains the order bar's
       left half and its Build: what the steppers add up to, its price
       against the cash, the posts and the land. No measure in the middle:
       nothing on a market page is judged against a need.
       ===================================================================== */

    /** Whether the order bar is a market page's, which has no measure to draw. */
    private boolean orderMarket;

    /** A market category's page. */
    void showMarketCategory(BuildAdvice.Category c) {
        ui.clearMenu("handleAllBuildingMenus", () -> showMarketCategory(c));

        /*
         * WHICH CATEGORY THIS IS, remembered for the Build tab - set here
         * rather than only in the strip's own handler, because the inbox's
         * "go and build" lands here directly too.
         */
        buildCategory = c.name();

        /*
         * AND WHICH CARDS ARE ON IT, for the keyboard (see "the keyboard",
         * after placeOrder). Started again before a card is drawn, so each
         * card files itself in the order it is laid out.
         */
        pageCards.clear();
        pageTitle = c.name();
        pageCategories = c.types();
        pendingHint = new Label("↵ builds what is pending · ⌫ clears it");
        pendingHint.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED) + " -fx-padding: 6 0 0 0;");
        orderMeasure = null;
        orderMarket = true;

        VBox page = widePage();
        HBox head = buildHead(c.name(), c.types());
        head.setStyle("-fx-padding: 0 0 4 0;");
        javafx.scene.layout.FlowPane strip = buildStrip(c.name());
        strip.setStyle("-fx-padding: 0;");
        HBox who = whoBuildsThis(c.name(), c.types());
        page.getChildren().addAll(head, strip, constraintsBar(), who);

        // The order bar first: each card reprices into it as it is drawn.
        orderBar = new VBox();
        for (BuildCard.Group group : BuildCard.groups(ui.game, c.name(), null)) {
            page.getChildren().add(groupHead(group));
            javafx.scene.layout.FlowPane cards = new javafx.scene.layout.FlowPane(TILE_GAP, TILE_GAP);
            cards.setAlignment(Pos.TOP_LEFT);
            cards.prefWrapLengthProperty().bind(ui.menuScroller.widthProperty().subtract(60));
            for (BuildCard.Figures f : group.cards()) cards.getChildren().add(card(f, group, c, null));
            page.getChildren().add(cards);
        }

        page.getChildren().addAll(orderBar, pendingHint);
        showPendingHint();
        refreshOrderBar();
        ui.rootMenu.getChildren().add(page);
    }

    /**
     * A market group's heading - "FOOD MILLS · bread and bakery goods from
     * crops", or "STEEL · from iron ore" where the goods would repeat the
     * name - and at its right the sector's figure the group answers to, both
     * wrapping rather than cutting.
     */
    HBox groupHead(BuildCard.Group group) {
        String about = groupSubtitle(group);
        Label head = new Label(group.title().toUpperCase() + (about.isEmpty() ? "" : " · " + about));
        head.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        head.setWrapText(true);
        head.setMinWidth(0);
        Label note = new Label(groupNote(group.note()));
        note.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        note.setWrapText(true);
        note.setMinWidth(0);
        note.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox row = new HBox(Palette.GAP, head, gap, note);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 6 0 0 0;");
        return row;
    }

    /**
     * What a market group is for, beside its name: the goods a maker group
     * makes and what from, off the templates' own goods; a few words for the
     * rest.
     */
    String groupSubtitle(BuildCard.Group group) {
        BuildCard.Figures first = group.cards().get(0);
        switch (first.kind()) {
            case HOMES:     return "a door for every household";
            case CUSTOMERS: return first.template().makes(Good.LUXURY_TRADE) > 0
                                   ? "somewhere for the well-off to spend" : "food for every household";
            case MEALS:     return "the city's food, cooked";
            case BRANCH:    return "the city's bank, a counter at a time";
            case RAIL:      return "freight across the city's boundary";
            case POINTS:    return "the crews every site waits on";
            case PUMP:      return "the drivers' petrol, at the pump";
            case TANKS:     return "crude and products in store";
            case BERTHS:    return "the city's trade by sea";
            case SLOTS:     return "the slots a platform's wells stand in";
            case PIPE:      return "the sea's crude, ashore by pipe";
            case OFFICE:    return "work the world buys";
            default: {
                java.util.Set<String> made = new java.util.LinkedHashSet<>(), used = new java.util.LinkedHashSet<>();
                for (BuildCard.Figures f : group.cards()) {
                    for (Good g : ham.citybuildersim.sectors.Refining.madeBy(f.template()).keySet()) made.add(g.label().toLowerCase());
                    for (Good g : f.template().goodsUsed().keySet()) used.add(g.label().toLowerCase());
                }
                used.removeAll(made);
                String what = andList(new ArrayList<>(made));
                if (what.equalsIgnoreCase(group.title())) what = "";
                if (!used.isEmpty()) what += (what.isEmpty() ? "" : " ") + "from " + andList(new ArrayList<>(used));
                return what;
            }
        }
    }

    /** "a, b and c". */
    static String andList(List<String> words) {
        if (words.size() <= 1) return words.isEmpty() ? "" : words.get(0);
        return String.join(", ", words.subList(0, words.size() - 1)) + " and " + words.get(words.size() - 1);
    }

    /** A group's note, in words (BuildCard.NoteKind). */
    String groupNote(BuildCard.Note n) {
        switch (n.kind()) {
            case DOORS:
                return "studios: " + doors(n.a()) + " · family homes: " + doors(n.b());
            case SHOPS:
                return "the shops serve " + formatter.format(Math.round(n.a())) + " a month; "
                        + formatter.format(Math.round(n.b())) + " people";
            case MADE:
                return "the city used " + formatter.format(Math.round(n.a())) + BuildCard.goodWords(n.good())
                        + "; " + (n.good() == Good.CROPS ? "farms here grow " : "plants here make ")
                        + formatter.format(Math.round(n.b()));
            case RAIL:
                return "the city trades " + formatter.format(Math.round(n.a())) + " t a month; the network carries "
                        + formatter.format(Math.round(n.b()));
            case PUMP:
                return "the drivers bought " + formatter.format(Math.round(n.a())) + " L; the stations sell "
                        + formatter.format(Math.round(n.b()));
            case LUXURY:
                // Not counted yet since the load (BuildCard.Note): what the counters serve alone.
                return Double.isNaN(n.a()) ? "the counters serve " + formatter.format(Math.round(n.b()))
                        : formatter.format(Math.round(n.a())) + " customers came; the counters serve "
                        + formatter.format(Math.round(n.b()));
            case MEALS:
                return Double.isNaN(n.a()) ? "the kitchens serve " + formatter.format(Math.round(n.b()))
                        : formatter.format(Math.round(n.a())) + " meals wanted; the kitchens serve "
                        + formatter.format(Math.round(n.b()));
            default:
                return "";
        }
    }

    /** Households of a segment without a door, or the doors to spare (RealEstate.doorShortfall(), negative when there are). */
    static String doors(double shortfall) {
        if (shortfall >= .5) return formatter.format(Math.round(shortfall)) + " households without a door";
        if (shortfall <= -.5) return formatter.format(Math.round(-shortfall)) + " to spare";
        return "none short";
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

    /** A kind of cargo's goods, in words (0.7.86; Good.cargo()). */
    static String cargoWords(ham.citybuildersim.Ports.Cargo k) {
        switch (k) {
            case LIQUID:    return "crude, petrol, diesel and the refinery's other products";
            case DRY_BULK:  return "iron ore, crops, grain, building materials and coke";
            case CONTAINER: return "the shelf's goods, drinks and watches, in boxes";
            default:        return "steel, machinery, wagon sets, cars and vans";
        }
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
    // each category - and the only way to hold that is to print every one and
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
        // ...a filling station (0.7.83): what it sells and at what, the grocers' forecourts' rule (sectors.Retail, THE FORECOURTS).
        if (ham.citybuildersim.sectors.Retail.isStation(t)) {
            ham.citybuildersim.sectors.Retail grocers = ui.game.getSectors().retail();
            double pump = grocers.pumpPriceToday(ui.game);
            out.add(String.format("Sells up to %s litres of petrol a month to the city's drivers, at the pump: what the"
                    + " grocers paid a litre wholesale, %.0f%% more, with the sales tax on top%s.",
                    formatter.format(t.pumpLitres()), ham.citybuildersim.sectors.Retail.PUMP_MARGIN * 100,
                    pump > 0 ? " - " + unitPrice(pump) + " a litre today" : ""));
            out.add(String.format("The grocers buy it off the city's refineries first and from the world for the rest."
                    + " Past what the stations can sell the drivers queue and pay %.0f%% more, and the grocers build"
                    + " another.", ham.citybuildersim.sectors.Retail.QUEUE_MARGIN * 100));
            return out;
        }
        // ...oil storage (0.7.85): the refiners' Tank Farm and the city's Strategic Reserve (sectors.Refining, THE TANK FARM; StrategicReserve).
        if (ham.citybuildersim.sectors.Refining.isTankFarm(t)) {
            out.add(String.format("Holds %s litres: the refiners keep a month of their crude units' crude in it, and"
                    + " their products share the rest of the room.", formatter.format(t.getStock())));
            out.add("While the refiners keep crude, they buy it as stock and run on what they have and what they"
                    + " bought. A port's tankers will land their cargo in it.");
            return out;
        }
        // ...a sea terminal (0.7.86): its berth, its kind's goods, and what a tonne by sea costs (Ports).
        if (ham.citybuildersim.Ports.isPort(t)) {
            ham.citybuildersim.Ports.Cargo k = t.berthCargo();
            out.add(String.format("One berth on the city's coast for %s t a year of %s - %s. Those goods go by sea in the"
                    + " share the berths cover, their freight at %.0f%% of what a lorry charges.",
                    formatter.format(t.berthTonnesAYear()), k.label().toLowerCase(), cargoWords(k), k.seaFreightShare() * 100));
            out.add("The city builds it and pays its crews with transit's. It needs the city to own some sea"
                    + (k == ham.citybuildersim.Ports.Cargo.LIQUID
                            ? ", and crude lands only where a Tank Farm has room for a tanker's cargo." : "."));
            return out;
        }
        // ...the oil at sea (0.7.91): a platform's jacket and a kilometre of pipeline (sectors.Oil, THE OIL AT SEA).
        if (t.isPlatform()) {
            out.add(String.format("A steel jacket on a shallow oil field the city owns in the sea, %.0f m deep at most, with"
                    + " slots for %d wells - one a sea site of the field. It lifts nothing itself: its Platform Wells do.",
                    ham.citybuildersim.sectors.Oil.PLATFORM_MAX_DEPTH_M, t.platformSlots()));
            out.add("Its crude goes ashore by shuttle tanker at the boundary's freight a tonne, paid abroad, until a"
                    + " Crude Pipeline from its field stands.");
            return out;
        }
        if (t.isPipeline()) {
            out.add(String.format("A kilometre of pipe, %s at sea and %s on land: a field's pipe runs straight from the"
                    + " field to the city.", money(t.getCashCost()), money(t.onshoreCashPerKm())));
            out.add(String.format("Once a field's whole pipe stands, its platforms' crude comes ashore without the shuttle"
                    + " tankers. The wells build one only where the freight it saves over %d years repays it %.2f times.",
                    ham.citybuildersim.sectors.Oil.PIPE_LIFE_MONTHS / 12, ham.citybuildersim.sectors.Oil.PIPE_PAYBACK));
            return out;
        }
        if (StrategicReserve.isReserve(t)) {
            out.add(String.format("Holds %s litres of crude oil, %s t, for the city.", formatter.format(t.getStock()),
                    formatter.format(Math.round(t.getStock() / ham.citybuildersim.sectors.Refining.CRUDE_LITRES_PER_TONNE))));
            out.add("The city fills it in the month's crude market, from its wells first and the world for the rest,"
                    + " and releases it there to the refiners, shipping what they do not take. Paid at the next strike.");
            return out;
        }
        // ...what it makes: a crude unit's slate since 0.7.76 (Refining.madeBy()), a conversion unit's products of its feed since 0.7.80.
        java.util.Map<Good, Double> makesNow = ham.citybuildersim.sectors.Refining.madeBy(t);
        if (t.isOwnedBySector() && !makesNow.isEmpty()) {
            if (ham.citybuildersim.sectors.Refining.isConversionUnit(t)) out.addAll(conversionUnitLines(t));
            for (java.util.Map.Entry<Good, Double> e : makesNow.entrySet()) {
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
                    String made = String.format("Makes %s %s of %s a month",
                            formatter.format(e.getValue()), Formats.plural(g.unit()), g.label().toLowerCase());
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
                out.add(String.format("Uses %s %s of %s a month%s, bought on the market"
                        + " or imported.",
                        formatter.format(e.getValue()), Formats.plural(g.unit()), g.label().toLowerCase(),
                        price > 0 ? " - " + money(e.getValue() * price) + " at today's price" : ""));
            }
            if (t.isPlatformWell()) {
                // ...a platform's well (0.7.91): at sea, in a platform's slot.
                out.add("Stands in a free slot on an Offshore Platform, one a sea site of its field - cash alone will not"
                        + " put one up. Its crude goes ashore by shuttle tanker, or by a field's pipe once one stands.");
            } else if (Game.siteOf(t) != null) {
                out.add("Needs land with an " + (Game.siteOf(t) == Resource.OIL ? "oil" : "iron")
                        + " field under it - cash and space alone will not put one up.");
            }
            return out;
        }

        switch (t.getCategory()) {

            case COMMERCIAL:
                // The one commercial building no sector owns: the bank's counters.
                out.add(String.format("A branch of the city's bank. Another opens only while there are "
                        + "more than %,.0f customers for each one standing, and each brings %s of "
                        + "shareholders' capital with it, which is what lets the bank lend.",
                        // In today's money (0.7.45; the UI spec's B12): the bank's paid-in is struck at the
                        // expected price level; PAID_IN_PER_BRANCH is its founding figure.
                        Bank.CUSTOMERS_PER_BRANCH, money(ui.game.getBank().getPaidInPerBranch())));
                out.add("Every branch after the first has to be paid for by its customers' "
                        + "account fees - its staff, its repairs and its running costs - or it closes.");
                break;

            case ELECTRICITY:
                out.add(String.format("Generates %s of electricity.", power(t.getProduction1())));
                break;

            case WATER:
                // Where it draws from (0.7.59): fresh water up to the city's
                // limit, or the sea, which the limit does not reach.
                if (t.isSeaWater()) {
                    out.add(String.format("Desalinates %s units of seawater a month, and draws %s of power doing it.",
                            formatter.format(t.getProduction1()), power(t.getElectricityConsumption())));
                    out.add("The fresh water limit does not reach it, but it has to stand on the coast: the city"
                            + " must own some sea.");
                } else {
                    out.add(String.format("Treats %s units of fresh water a month.",
                            formatter.format(t.getProduction1())));
                    out.add(String.format("The plants together treat no more than the city's lakes and river yield:"
                            + " %s units a month for each km² of them it owns.",
                            formatter.format(UtilitiesHandler.FRESH_UNITS_PER_KM2)));
                }
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
                 * These two numbers were the choice, ground or labour, until
                 * 0.7.70: the card now weighs both in money over the road's
                 * life (BuildAdvice.lifetime(), its first bar), and the
                 * sentence says so.
                 */
                if (t.getCapacity() > 0) {
                    // ...weighed since 0.7.70 by money over its life, its ground in it: the card's first bar
                    // (BuildAdvice.lifetime()), where "the whole choice" was ground against points.
                    out.add(String.format("Per 1,000 trips carried that is %s of"
                            + " ground and %s construction points. The card's first bar"
                            + " weighs the roads by what each costs over %d years with its"
                            + " ground, a trip it takes off the road.",
                            LandManager.areaWords(t.getLandSqFt() * 1000.0 / t.getCapacity()),
                            formatter.format(Math.round(
                                    t.getConstructionPoints() * 1000.0 / t.getCapacity())),
                            BuildAdvice.LIFE_MONTHS / 12));
                }
                // PAVING (0.7.70; ConstructionControl, F): said on both roads it joins.
                if (ConstructionControl.paves(t)) {
                    out.add("It can be paved to a Paved Road where it stands, under its card: a Paved"
                            + " Road's price less its own material, which goes into the new bed, and the"
                            + " taking-up of its surface. It carries its traffic until the paving opens, and"
                            + " the ground a Paved Road does not need comes back free.");
                } else if (ConstructionControl.PAVE_TO.equals(t.getName())) {
                    out.add("A Gravel Road can be paved to one where it stands; a Paved Road is not raised"
                            + " to an Elevated Highway.");
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
     * The receipt dot, top-right of the menu, and the popover it opens with
     * the last purchases (0.7.20; the card was in the page and showed one).
     *
     * It flashes only while there is a receipt this window has not shown yet -
     * compared by serial, not by contents, because building the same three
     * clinics twice in a row produces two identical receipts and the second one
     * is still news. Once opened it goes solid: the flash means "something
     * happened", and a permanent flash means nothing at all.
     */
    HBox receiptCorner(String menuTitle, EnumSet<BuildingType> categories) {

        HBox corner = new HBox();
        corner.setAlignment(Pos.CENTER_RIGHT);
        // At the end of the page's head since 0.7.21 (buildHead()): the inbox's
        // envelope that shared this corner is the header's now. Its room is
        // kept whether or not there is a dot (0.7.20), so the first purchase
        // moves nothing.
        corner.setMinSize(18, 18);
        corner.setPrefSize(18, 18);
        if (receipts.isEmpty()) return corner;

        boolean unseen = ui.game.getReceiptSerial() != ui.receiptSeen;

        Button dot = new Button();
        dot.setMinSize(18, 18);
        dot.setPrefSize(18, 18);
        dot.setMaxSize(18, 18);
        dot.setStyle("-fx-background-color: " + Palette.CONFIRM + "; -fx-background-radius: 50%;"
                + " -fx-border-color: " + Palette.CONFIRM + "; -fx-border-width: 1;"
                + " -fx-border-radius: 50%; -fx-cursor: hand; -fx-padding: 0;");
        Tooltip dotTip = new Tooltip(unseen
                ? "A purchase went through - click for the receipts"
                : "Click for the last purchases");
        dotTip.setShowDelay(Duration.millis(200));
        Tooltip.install(dot, dotTip);

        /*
         * A POPOVER, NOT A ROW (0.7.20). The receipt card used to open in the
         * page, at its top, and push the strip and the whole grid down while
         * it was open - one more thing moving under the pointer. It is a
         * popup under the dot now, over the page, pushing nothing: a click
         * anywhere else or Esc closes it (the popup keeps Esc for closing
         * itself), and it goes when the build page does (UserInterface
         * .clearMenu() -> closeReceipt()).
         */
        dot.setOnAction(e -> {
            ui.receiptSeen = ui.game.getReceiptSerial();
            if (ui.receiptPulse != null) {
                ui.receiptPulse.stop();
                ui.receiptPulse = null;
            }
            dot.setOpacity(1);
            if (receiptPopup != null) closeReceipt(); else openReceipt(dot);
        });

        if (unseen && receiptPopup == null) {
            ui.receiptPulse = new FadeTransition(Duration.millis(600), dot);
            ui.receiptPulse.setFromValue(1.0);
            ui.receiptPulse.setToValue(.15);
            ui.receiptPulse.setAutoReverse(true);
            ui.receiptPulse.setCycleCount(Animation.INDEFINITE);
            ui.receiptPulse.play();
        }
        // A redraw while it is open shows the newest purchases in it.
        if (receiptPopup != null) receiptPopup.getContent().setAll(receiptCard());

        corner.getChildren().add(dot);
        return corner;
    }

    /* ----- the last purchases (0.7.20) ----- */

    /**
     * One purchase, as the receipt shows it: what and how many, what it cost
     * all in, the sales tax in that, and when.
     */
    record Receipt(int serial, String name, int quantity, double total, double salesTax, int month) { }

    /** How many purchases the receipt keeps. */
    static final int RECEIPTS = 5;

    /**
     * The last RECEIPTS purchases, newest first. This window's, for the
     * session, like receiptSeen: the model keeps only the last order's
     * receipt (Game.getTotalBuildingCost() and the rest), and the play-through
     * found the receipt showing the school and not the wind farm bought just
     * before it. Emptied when another city is founded or loaded
     * (forgetReceipts()), not when the player comes back to the same one.
     */
    final java.util.ArrayDeque<Receipt> receipts = new java.util.ArrayDeque<>();

    /** The receipt's popover while it is open, or null. */
    private javafx.stage.Popup receiptPopup;

    /**
     * After an order went through: the receipt the model wrote for it, with
     * the sales tax off the quote it was charged on - quoted just before the
     * order, against the city the order was then placed in, as
     * Game.processBuildOrder() quotes it, so it is the same quote.
     */
    void noteReceipt(Game.BuildQuote quote) {
        int serial = ui.game.getReceiptSerial();
        if (!receipts.isEmpty() && receipts.peekFirst().serial() == serial) return;
        receipts.addFirst(new Receipt(serial, ui.game.getBuildingName(), ui.game.getBuildQuantity(),
                ui.game.getTotalBuildingCost(), quote.salesTax, ui.game.getMonth()));
        while (receipts.size() > RECEIPTS) receipts.removeLast();
    }

    /** Another city, founded or loaded (UserInterface.anotherCity()): its purchases are not this one's. */
    void forgetReceipts() {
        closeReceipt();
        receipts.clear();
    }

    private void openReceipt(Button dot) {
        javafx.geometry.Bounds at = dot.localToScreen(dot.getBoundsInLocal());
        if (at == null) return;
        javafx.stage.Popup pop = new javafx.stage.Popup();
        pop.setAutoHide(true);
        pop.setHideOnEscape(true);
        pop.getContent().add(receiptCard());
        pop.setAnchorLocation(javafx.stage.PopupWindow.AnchorLocation.CONTENT_TOP_RIGHT);
        pop.setOnHidden(e -> {
            if (receiptPopup == pop) {
                receiptPopup = null;
                ui.receiptOpen = false;
            }
        });
        receiptPopup = pop;
        ui.receiptOpen = true;
        pop.show(dot, at.getMaxX(), at.getMaxY() + 6);
    }

    /** Close the receipt, if it is open. */
    void closeReceipt() {
        javafx.stage.Popup pop = receiptPopup;
        receiptPopup = null;
        ui.receiptOpen = false;
        if (pop != null) pop.hide();
    }

    /** The card in the popover: the last purchases, newest first, each with its total and the tax in it. */
    private VBox receiptCard() {
        VBox card = new VBox(4);
        card.setStyle("-fx-background-color: " + Palette.CONFIRM_GROUND + "; -fx-border-color: " + Palette.CONFIRM + ";"
                + " -fx-border-width: 1; -fx-background-radius: 4;"
                + " -fx-border-radius: 4; -fx-padding: 8 12 9 12;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 12, 0, 0, 3);");

        Label head = new Label(receipts.size() == 1 ? "LAST PURCHASE" : "LAST " + receipts.size() + " PURCHASES");
        head.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + Palette.GOOD + ";");
        card.getChildren().add(head);

        for (Receipt r : receipts) {
            Label what = new Label(formatter.format(r.quantity()) + " \u00d7  " + r.name());
            what.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + Palette.GOOD + ";");
            Label paid = receiptLine(money(r.total()) + "  with " + money(r.salesTax()) + " sales tax",
                    CityCalendar.format(r.month()));
            card.getChildren().add(new VBox(0, what, paid));
        }
        return card;
    }

    Label receiptLine(String label, String value) {
        Label l = new Label(String.format("%-30s %s", label, value));
        l.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 10px;"
                + " -fx-text-fill: #8bc34a;");
        return l;
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
     * cannot help. Since 0.7.61 it buys the most iron sites a dollar (THE
     * BUILD SHORTCUT). Since 0.7.64 (batch L) it says plainly that an offer
     * holds every field centred in it whole, and its Buy names the sites and
     * the tonnes: the founding field of the default world is 35 sites and
     * 449 Mt, about US$180M, which the land office's funding page sizes a
     * bond to when the city is short.
     *
     * ...AND AN OIL WELL'S (0.7.62, batch K): the same page on its own
     * resource - an Oil Well is short of an oil site - and the same Buy
     * (Game.bestOffer(LandNeed.deposit(OIL))).
     *
     * ...THE CHEAPEST FIELD (0.7.64, batch L2): the Buy offers what the test
     * player buys (LongPlaytest.ironWhenNeeded()) - the cheapest offer holding
     * the deposit, LandMarket.cheapestWith() - where it offered the most sites
     * a dollar, which on the default world is the 35-site founding field at
     * about US$180M while a one-site field lies further out for a thirtieth of
     * that. Its words name the offer, its sites, tonnes and price; short of
     * cash, the land office's funding page, sized to it, as before. The land
     * office still lists every offer.
     */
    void showNoDepositMenu(BuildingsTemplate selected, int quantity,
                                   String menuTitle, EnumSet<BuildingType> categories) {
        ui.clearMenu("showNoDepositMenu", () -> showNoDepositMenu(selected, quantity, menuTitle, categories));

        Resource site = Game.siteOf(selected) == null ? Resource.IRON : Game.siteOf(selected);
        // ...counted against its own kind (0.7.91, Game.committedFor()): a land well the land wells, a platform's well the slots;
        // the tonnes its own pool's (0.7.93, Game.remainingFor()): a land well the ground's, the sea's buildings the sea's.
        String[] words = selected.standsAtSea()
                ? noDepositAtSea(selected.isPlatform(), selected.getName(), quantity, ui.game.sitesFor(selected),
                        ui.game.committedFor(selected), ui.game.remainingFor(selected))
                : noDepositPage(site, selected.getName(), quantity, ui.game.sitesFor(selected),
                        ui.game.committedFor(selected), ui.game.remainingFor(selected));

        Label title = new Label(words[0]);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        VBox explanation = reportSection("WHY", words[1], words[2], "", words[3], words[4], words[5]);

        Label reserves = monoLabel(words[6]);
        reserves.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-padding: 6 0 0 0;");

        HBox toLand = doorPill("Go to the Land Office", Icons.LAND, Palette.BUILDING, () -> ui.landScreen.showLandMenu());

        Button back = new Button("Back");
        back.setOnAction(e -> handleAllBuildingMenus(menuTitle, categories));

        ui.rootMenu.getChildren().addAll(title, explanation, reserves);
        // The Build shortcut (0.7.61, star 14): since 0.7.64 the cheapest offer holding the resource, then back to the order.
        LandParcel cheapest = ui.game.bestOffer(Game.LandNeed.deposit(site));
        if (cheapest != null) {
            ui.rootMenu.getChildren().add(bestLandButton(cheapest, noDepositWords(cheapest, site), words[7],
                    () -> handleAllBuildingMenus(menuTitle, categories)));
            // ...and whether an iron field pays back here (0.7.101, Jerus's decision B): the test player's own rule.
            if (site == Resource.IRON && !selected.standsAtSea()) {
                BuildAdvice.Payback field = BuildAdvice.ironPayback(ui.game, cheapest);
                Label pays = new Label(ironPaybackWords(field));
                pays.setWrapText(true);
                pays.setMaxWidth(PAYBACK_LINE);
                pays.setStyle(wordsAt(10.5, field.pays() ? Palette.TEXT_LABEL : Palette.WARN) + " -fx-padding: 2 0 6 0;");
                ui.rootMenu.getChildren().add(pays);
            }
        }
        ui.rootMenu.getChildren().addAll(toLand, back);
    }

    /**
     * The no-deposit page's words, for iron or oil (0.7.62; pure, so a probe
     * measures them): the title, the WHY section's five lines, the reserve
     * line and the Buy's sub-line.
     */
    static String[] noDepositPage(Resource site, String building, int quantity, int owned, int committed, double left) {
        boolean oil = site == Resource.OIL;
        String word = BuildCard.depositWord(site);
        return new String[] {
            "NO " + word.toUpperCase(java.util.Locale.ROOT) + " DEPOSIT",
            quantity + " x " + building + " needs " + (committed + quantity) + (oil ? " oil site(s)." : " deposit(s)."),
            "The city owns " + owned + ", with " + committed + " already spoken for.",
            oil ? "A well has to stand on ground with oil under it. Sites are" : "A mine has to stand on ground with ore under it. Deposits are",
            oil ? "the world's oil fields, sold whole: an offer holds every" : "the world's iron fields, sold whole: an offer holds every",
            "field centred in it, all its sites and tonnes, in its price.",
            String.format(oil ? "Oil still in the ground: %,.0f tonnes" : "Ore still in the ground: %,.0f tonnes", left),
            oil ? "the cheapest offer holding oil: whole fields, the oil in its price"
                : "the cheapest offer holding iron: whole fields, the ore in its price" };
    }

    /**
     * ...and for the oil at sea (0.7.91; pure, as above): an Offshore
     * Platform with no shallow sea field to take it, or a Platform Well with
     * no free slot - `owned` is Game.sitesFor()'s, the platforms the fields
     * could still take or the slots standing; `left` the offshore pool's
     * tonnes (0.7.93).
     */
    static String[] noDepositAtSea(boolean platform, String building, int quantity, int owned, int committed, double left) {
        return new String[] {
            "NO OIL DEPOSIT",
            platform ? quantity + " x " + building + " needs a shallow sea field with room."
                     : quantity + " x " + building + " needs " + (committed + quantity) + " platform slot(s).",
            platform ? "The city's sea fields could take " + owned + " more, " + committed + " on site."
                     : "The platforms standing hold " + owned + ", " + committed + " spoken for.",
            platform ? "A platform stands on an oil field in the sea, 150 m deep"
                     : "A platform's well stands in one of its slots, one a sea",
            platform ? "at most, its wells one a sea site. Sites are the world's"
                     : "site of its field: an Offshore Platform on a shallow sea",
            platform ? "oil fields, sold whole: an offer holds every field in it."
                     : "field the city owns brings its slots with it.",
            String.format("Oil still under the sea: %,.0f tonnes", left),
            "the cheapest offer holding oil: whole fields, the oil in its price" };
    }

    /** The pay-back line's width on the no-deposit page (0.7.101): two of Build's need cards, about as wide as the WHY section's hand-wrapped lines above it. */
    static final double PAYBACK_LINE = 2 * NEED_CARD;

    /**
     * Whether the cheapest iron field pays back here, as the no-deposit page
     * says it under its Buy (0.7.101, Jerus's decision B: "the build advice
     * says when an iron field won't pay back for a small town"): the test
     * player's rule since 0.7.67 (BuildAdvice.ironPayback()) - the mines the
     * city could staff on it, what they would earn a month, and the month's
     * payment that repays its price over Game.BUILD_BOND_YEARS. Pure, so a
     * probe measures it.
     */
    static String ironPaybackWords(BuildAdvice.Payback p) {
        String repays = money(p.payment()) + " a month that repays its price over " + Game.BUILD_BOND_YEARS + " years";
        if (p.pays()) return "It pays back: the " + p.mines() + (p.mines() == 1 ? " mine" : " mines") + " the city could staff"
                + " on it would earn " + money(p.earns()) + " a month, over the " + repays + ".";
        if (p.mines() == 0) return "It would not pay back for a city this size: none of the mines it could staff on it would"
                + " pay yet, against the " + repays + ".";
        return "It would not pay back for a city this size: the " + p.mines() + (p.mines() == 1 ? " mine" : " mines")
                + " it could staff on it would earn " + money(p.earns()) + " a month, under the " + repays + ".";
    }

    /** The no-deposit page's Buy: "Buy the cheapest: East 7 · 1 iron site, 12.8 Mt for US$6.16M" (or oil sites, 0.7.62) - its sites and, since 0.7.64, its tonnes: the whole of every field centred in it; "the cheapest" since 0.7.64 ("the best" was the most sites a dollar). */
    static String noDepositWords(LandParcel p, Resource site) {
        Resource r = site == null ? Resource.IRON : site;
        int sites = p.getSites(r);
        String word = BuildCard.depositWord(r);
        return "Buy the cheapest: " + p.where() + " · " + sites + " " + word + (sites == 1 ? " site, " : " sites, ")
                + LandMap.tonnes(p.getAmount(r)) + " for " + usd(p.getPriceUsd());
    }

    /**
     * The city has the money and the land, and no sea to draw (0.7.59).
     *
     * Its own refusal for the reason the deposit has one: a desalination
     * plant is not short of cash or ground, it is short of a coast, and the
     * only thing that fixes that is an offer that runs out to the sea. The
     * page names the cheapest one standing (Game.bestOffer(LandNeed.coast())),
     * and since 0.7.61 buys it (THE BUILD SHORTCUT), or sends the player to
     * the land office.
     */
    void showNoCoastMenu(BuildingsTemplate selected, int quantity,
                         String menuTitle, EnumSet<BuildingType> categories) {
        ui.clearMenu("showNoCoastMenu", () -> showNoCoastMenu(selected, quantity, menuTitle, categories));

        LandParcel coast = ui.game.bestOffer(Game.LandNeed.coast());

        Label title = new Label("NO COAST");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        // ...or a sea terminal's quay (0.7.86): the same refusal, its own why.
        boolean port = ham.citybuildersim.Ports.isPort(selected);
        VBox explanation = reportSection("WHY",
                quantity + " x " + selected.getName() + (port ? " needs the sea for its quay." : " needs the sea to draw."),
                "The city owns none: its land is all dry ground, lake and river.",
                "",
                port ? "A terminal's berth stands on the coast, and its ships come in" : "A desalination plant stands on the coast and draws seawater, so",
                port ? "from the open sea. Offers in the land office" : "the fresh water limit does not reach it. Offers in the land office",
                "that run out to the sea carry some, its square kilometre priced",
                String.format("at %.0f%% of dry ground's.", LandMarket.SEA_PRICE_SHARE * 100));

        Label cheapest = monoLabel(coast == null
                ? "No offer standing reaches the sea."
                : String.format("Cheapest with sea: %s, %s of sea for %s",
                        coast.where(), LandManager.areaWords(LandManager.sqFt(coast.getKm2(CityLand.SEA))),
                        usd(coast.getPriceUsd())));
        cheapest.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-padding: 6 0 0 0;");

        HBox toLand = doorPill("Go to the Land Office", Icons.LAND, Palette.BUILDING, () -> ui.landScreen.showLandMenu());

        Button back = new Button("Back");
        back.setOnAction(e -> handleAllBuildingMenus(menuTitle, categories));

        ui.rootMenu.getChildren().addAll(title, explanation, cheapest);
        // The Build shortcut (0.7.61, star 14): the cheapest offer with sea, then back to the order.
        if (coast != null) {
            ui.rootMenu.getChildren().add(bestLandButton(coast, "Buy the cheapest with sea: " + coast.where() + " for "
                            + usd(coast.getPriceUsd()), LandManager.areaWords(LandManager.sqFt(coast.getKm2(CityLand.SEA)))
                            + (port ? " of sea for the quay" : " of sea for the plant to draw"),
                    () -> handleAllBuildingMenus(menuTitle, categories)));
        }
        ui.rootMenu.getChildren().addAll(toLand, back);
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

        HBox toSchools = doorPill("Go to Build · Education", Icons.BUILD, Palette.BUILDING, () -> {
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
     * sell debt that cannot fix the problem. Since 0.7.61 it buys the
     * cheapest bare ground that covers the shortfall (THE BUILD SHORTCUT);
     * its "Buying N costs roughly" line, priced a block at a time, went with
     * the blocks. Its areas in square kilometres since 0.7.64.
     */
    void showNoLandMenu(BuildingsTemplate selected, int quantity,
                                String prevTitle, EnumSet<BuildingType> prevCats) {
        ui.clearMenu("showNoLandMenu", () -> showNoLandMenu(selected, quantity, prevTitle, prevCats));

        LandManager land = ui.game.getLandManager();
        double needed = ui.game.landNeededFor(selected, quantity);
        double have = land.getAvailableSqFt();
        double short_ = Math.max(needed - have, 0);

        Label warning = new Label("NOT ENOUGH LAND");
        warning.setStyle("-fx-text-fill: " + Palette.BAD + "; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label details = new Label(String.format(
                "%,d x %s needs %s%n"
                        + "The city has %s free%n"
                        + "Short by %s",
                quantity, selected.getName(), LandManager.areaWords(needed),
                LandManager.areaWords(have), LandManager.areaWords(short_)));

        HBox toLand = doorPill("Go to the Land Office", Icons.LAND, Palette.BUILDING, () -> ui.landScreen.showLandMenu());

        Button back = new Button("Back");
        back.setOnAction(e -> handleAllBuildingMenus(prevTitle, prevCats));

        ui.rootMenu.getChildren().addAll(warning, details);
        // The Build shortcut (0.7.61, star 14): the cheapest bare ground that covers the shortfall - back to the
        // order once it is covered, this page again (its next best) while it is not.
        LandParcel best = ui.game.bestOffer(Game.LandNeed.shortfall(short_));
        if (best != null) {
            ui.rootMenu.getChildren().add(bestLandButton(best, noLandWords(best), noLandSub(best, short_), () -> {
                if (ui.game.landNeededFor(selected, quantity) <= ui.game.getLandManager().getAvailableSqFt()) {
                    handleAllBuildingMenus(prevTitle, prevCats);
                } else {
                    showNoLandMenu(selected, quantity, prevTitle, prevCats);
                }
            }));
        }
        ui.rootMenu.getChildren().addAll(toLand, back);
    }

    /** The no-land page's Buy: "Buy the best: 3.4 km² for US$12.1M" (spec-land 2.8). */
    static String noLandWords(LandParcel p) {
        return "Buy the best: " + LandMap.area(p.getKm2()) + " for " + usd(p.getPriceUsd());
    }

    /** ...and what it is: bare ground that covers the shortfall, the ore left to the deposit's own page (batch J1c) - or, with no bare offer big enough, the best value, and buy again. */
    static String noLandSub(LandParcel p, double shortSqFt) {
        return p.getSizeSqFt() >= shortSqFt && LandMarket.bareGround(p)
                ? p.where() + ", bare ground with no ore to pay for, covers the " + LandManager.areaWords(shortSqFt) + " short"
                : p.where() + ", the best value: no bare offer covers it all, so buy again after";
    }

    /* =====================================================================
       ONE FUNDING PAGE FOR THE RUN (0.7.40)

       Jerus, playing 0.7.39: "when you click build all and you dont have
       the credit it just builds one not all", and "when you buy land on
       credit a new issuance screen appears, but when you buy buildings on
       credit its still the old one."

       Both were the old page. Every Build press - a card's, the order bar's
       and Enter's (buildPending()), "Build all three" (orderAll()) and a
       suggestion's - placed its orders one at a time, and the first the
       cash could not cover opened INSUFFICIENT FUNDS with a loan sized for
       that order alone; the loan built it and went back to the page, and
       the rest of the run never happened.

       A RUN IS FUNDED WHOLE NOW. A press hands placeRun() its orders in the
       page's order, and before anything is placed the model says how far
       the run can go - the orders, from the first, that pass the checks
       money cannot fix (Game.buildRunAhead()) - and what those are short of
       (Game.buildFundingGap(run): their invoice, each order priced on the
       yard the ones before it leave, less the cash). Short of nothing, the
       run goes ahead as it always did. Short of something, this page comes
       first, in the land office's shape (LandScreen.showLandFunding()):
       "Build › Funding", the run's price against the cash as a bar with
       what it is short by, and the two offers as cards side by side - the
       Game.BUILD_BOND_YEARS bond first, then the Game.BUILD_NOTE_MONTHS
       note, the old page's offers and calls, each on the gap and each
       button saying the run. A press books exactly its card's quote and
       places every order in turn (goAhead()). An order the city refuses on
       the way shows its own page; a run that will stop at an order short of
       ore, licences or ground is funded only up to the order before it, and
       this page says so - the city is never sold a loan for a building it
       has nowhere to put (buildStack()'s rule). If an order still comes up
       short of money after the loan - which the exact invoice should make
       impossible - a card in the same frame says what was built and what
       was not, and never offers a second loan (showBuildFellShort()). An
       order not placed stays on its card.
       ===================================================================== */

    /** Where a run came from: the page to come back to, and whether its orders are that page's cards - each taken off its card once placed, the rest left on theirs. */
    record RunFrom(String title, EnumSet<BuildingType> categories, boolean cards) { }

    /** One order as a run of one. */
    static java.util.LinkedHashMap<BuildingsTemplate, Integer> runOf(BuildingsTemplate t, int n) {
        java.util.LinkedHashMap<BuildingsTemplate, Integer> run = new java.util.LinkedHashMap<>();
        run.put(t, n);
        return run;
    }

    /** The first `n` orders of a run. */
    static java.util.LinkedHashMap<BuildingsTemplate, Integer> firstOf(java.util.Map<BuildingsTemplate, Integer> run, int n) {
        java.util.LinkedHashMap<BuildingsTemplate, Integer> part = new java.util.LinkedHashMap<>();
        for (java.util.Map.Entry<BuildingsTemplate, Integer> e : run.entrySet()) {
            if (part.size() >= n) break;
            part.put(e.getKey(), e.getValue());
        }
        return part;
    }

    /** A run's orders in words, the order bar's way: "5 × Walk-in Clinic + 3 × Paved Road". */
    static String runNames(java.util.Map<BuildingsTemplate, Integer> run) {
        StringBuilder names = new StringBuilder();
        for (java.util.Map.Entry<BuildingsTemplate, Integer> e : run.entrySet()) {
            if (names.length() > 0) names.append(" + ");
            names.append(formatter.format(e.getValue())).append(" × ").append(e.getKey().getName());
        }
        return names.toString();
    }

    /**
     * A run placed (0.7.40): funded first when the orders that can go ahead
     * cost more than the cash (showBuildFunding()), otherwise placed in turn
     * as they always were. True when every order was placed and the page is
     * drawn again.
     */
    boolean placeRun(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from) {
        if (run.isEmpty()) return false;
        Game g = ui.game;
        int ahead = g.buildRunAhead(run);
        if (ahead > 0 && g.buildFundingGap(firstOf(run, ahead)) > 0) {
            showBuildFunding(run, from);
            return false;
        }
        return goAhead(run, from, null);
    }

    /**
     * Every order of the run in turn, through buildStack() - the path each
     * card's Build always took - each placed one taken off its card. The
     * first the city refuses stops the run and shows its own page; one short
     * of money shows the funding page for what is left, or, with `paper`
     * already issued for it, the fell-short card.
     *
     * THE RESULT IS LOOKED AT, AND A LOAN IS NEVER OFFERED TWICE - 0.7.10's
     * rule, from the page this one replaced: a bare call threw the answer
     * away, so a city could take on debt, build nothing and come back to a
     * page that said nothing - Jerus: "the tbill is inacted but the roads
     * are not built and you are just left with the cash unspent" - and going
     * back to the funding page after a loan would loop the player through
     * the same button for ever, borrowing every time.
     *
     * @param paper the loan just issued for the run, in words ("20-year bond"), or null for none
     */
    private boolean goAhead(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from, String paper) {
        Placed p = placeInTurn(run, from);
        if (p.stop() == Game.BuildResult.SUCCESS) {
            handleAllBuildingMenus(from.title(), from.categories());
            return true;
        }
        BuildingsTemplate t = p.left().keySet().iterator().next();
        int n = p.left().get(t);
        switch (p.stop()) {
            case NO_LAND    -> showNoLandMenu(t, n, from.title(), from.categories());
            case NO_DEPOSIT -> showNoDepositMenu(t, n, from.title(), from.categories());
            case NO_COAST   -> showNoCoastMenu(t, n, from.title(), from.categories());
            case NO_LICENCE -> showNoLicenceMenu(t, n, from.title(), from.categories());
            default -> {
                if (paper == null) showBuildFunding(p.left(), from);
                else showBuildFellShort(p.built(), p.left(), from, paper);
            }
        }
        return false;
    }

    /** What placing a run in turn did: the orders placed, the orders left - the one that stopped it first - and buildStack()'s answer there, SUCCESS when nothing stopped it. */
    record Placed(java.util.LinkedHashMap<BuildingsTemplate, Integer> built,
                  java.util.LinkedHashMap<BuildingsTemplate, Integer> left, Game.BuildResult stop) { }

    /** goAhead()'s placing, without the page it then draws (a probe runs it): each order through buildStack() until one is not placed, each placed one's receipt noted and, from the cards, its card emptied. */
    Placed placeInTurn(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from) {
        java.util.LinkedHashMap<BuildingsTemplate, Integer> built = new java.util.LinkedHashMap<>();
        java.util.LinkedHashMap<BuildingsTemplate, Integer> left = new java.util.LinkedHashMap<>(run);
        for (java.util.Map.Entry<BuildingsTemplate, Integer> e : run.entrySet()) {
            BuildingsTemplate t = e.getKey();
            int n = e.getValue();
            // The quote it will be charged on, for the receipt's sales tax (see noteReceipt).
            Game.BuildQuote quote = ui.game.quoteBuild(t, n);
            Game.BuildResult result = ui.game.buildStack(t, n, false);
            if (result != Game.BuildResult.SUCCESS) return new Placed(built, left, result);
            noteReceipt(quote);
            if (from.cards()) orderQty.remove(t.getName());
            built.put(t, n);
            left.remove(t);
        }
        return new Placed(built, left, Game.BuildResult.SUCCESS);
    }

    /**
     * What the funding page says about a run, worked out without drawing it
     * (a probe reads it): the orders that can go ahead, their invoice and
     * the gap, the summary's line, what the treasury holds and the bar's
     * part for it, the gap in money, the offers' button, and - when the run
     * stops at an order the city would refuse - which and why.
     */
    record RunWords(java.util.LinkedHashMap<BuildingsTemplate, Integer> funded, double invoice, double gap,
                    String priceLine, String heldWords, double held, String shortBy, String button, String stops) { }

    RunWords runWords(java.util.Map<BuildingsTemplate, Integer> run) {
        Game g = ui.game;
        int ahead = g.buildRunAhead(run);
        java.util.LinkedHashMap<BuildingsTemplate, Integer> funded = firstOf(run, ahead);
        double invoice = g.buildRunInvoice(funded);
        double gap = g.buildFundingGap(funded);
        double cash = g.getCash();
        // "Build 5 · $X" for one order, "Build 3 orders · $X" for several: the run, at its invoice.
        String button = "Build " + (funded.size() == 1 ? formatter.format(funded.values().iterator().next())
                : funded.size() + " orders") + " · " + money(invoice);
        String stops = null;
        if (ahead < run.size()) {
            java.util.Map.Entry<BuildingsTemplate, Integer> at = null;
            int i = 0;
            for (java.util.Map.Entry<BuildingsTemplate, Integer> e : run.entrySet()) {
                if (i++ == ahead) { at = e; break; }
            }
            String why = switch (g.buildRunStop(run)) {
                case NO_DEPOSIT -> "no " + BuildCard.depositWord(Game.siteOf(at.getKey())) + " deposit is free for it";
                case NO_COAST   -> "the city owns no sea for it";
                case NO_LICENCE -> "nobody is licensed to work in it";
                default         -> "not enough ground is free for it";
            };
            stops = "Then " + formatter.format(at.getValue()) + " × " + at.getKey().getName() + " cannot go ahead - "
                    + why + " - so the run stops there: this borrows only for the "
                    + (ahead == 1 ? "order" : ahead + " orders") + " before it.";
        }
        return new RunWords(funded, invoice, gap, runNames(funded) + " · " + money(invoice) + " all in",
                cash < 0 ? "the treasury is overdrawn by " + money(g.cashShortfall()) : "the treasury holds " + money(cash),
                Math.max(0, Math.min(cash, invoice)), money(gap), button, stops);
    }

    /** One loan the funding page offers, worded - what Pieces.offerCard() draws, as the land office's Offer is: its name, quote, rate's words and colour, ending, button; what booking it does to the model (a probe runs it); and what the button does - books it, then places the run. */
    record RunOffer(String name, DebtQuote quote, String rate, String tone, String ending, Pieces.Press action,
                    Runnable book, Runnable issue) { }

    /**
     * The two offers, the old page's on the run's gap: the bond first -
     * what the page recommends, since a building outlives either loan and
     * the matching principle pays for a long-lived asset with long-lived
     * debt - then the note, for a gap the next months' revenue will cover.
     * Each button books exactly its quote (the call quotes it again,
     * identically), then places the whole run.
     */
    List<RunOffer> runOffers(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from) {
        Game g = ui.game;
        RunWords w = runWords(run);
        double gap = w.gap();
        DebtQuote bond = g.quoteLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        DebtQuote note = g.quoteTBill(gap, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
        String bondName = Game.BUILD_BOND_YEARS + "-year bond", noteName = Game.BUILD_NOTE_MONTHS + "-month note";
        // Each quotes its paper again, identically, and books it.
        Runnable bookBond = () -> ui.game.handleLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        Runnable bookNote = () -> ui.game.handleTBillLogic(gap, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
        List<RunOffer> out = new ArrayList<>();
        out.add(new RunOffer(bondName, bond,
                pct2(bond.marketRate()) + " yield  ·  " + pct2(bond.couponRate()) + " coupon",
                rateColour(bond, g.getDebtManager()),
                String.format("Paid over %d years: the coupon every month, then the whole %s at the end.",
                        bond.duration(), money(bond.faceValue())),
                new Pieces.Press(Pieces.Look.GO, w.button(), "issues the " + bondName + ", then builds"),
                bookBond, () -> {
                    bookBond.run();
                    goAhead(run, from, bondName);
                }));
        out.add(new RunOffer(noteName, note,
                pct2(note.marketRate()) + " a year, taken as a discount",
                rateColour(note, g.getDebtManager()),
                String.format(g.getRollover().getMode() == Rollover.Mode.MANUAL
                                ? "Falls due in %d months: the whole %s at once, out of the treasury."
                                : "Falls due in %d months: the whole %s at once, refinanced then by the treasury's rollover.",
                        note.duration(), money(note.faceValue())),
                new Pieces.Press(Pieces.Look.GO, w.button(), "issues the " + noteName + ", then builds"),
                bookNote, () -> {
                    bookNote.run();
                    goAhead(run, from, noteName);
                }));
        return out;
    }

    /** The run's pages' head: "Build › Funding", the title a way back to the page the run came from, and "‹ <that page>" at the right - the land office's head() on Build. */
    HBox runHead(String sub, RunFrom from) {
        String back = from.title() == null ? BuildAdvice.OVERVIEW : from.title();
        Label title = ui.pageTitle("Build");
        title.setStyle(title.getStyle() + " -fx-cursor: hand;");
        title.setOnMouseClicked(e -> handleAllBuildingMenus(from.title(), from.categories()));
        Label sep = new Label("›");
        sep.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_MUTED) + " -fx-padding: 8 0 2 0;");
        Label where = new Label(sub);
        where.setStyle(Palette.strong(Palette.SIZE_TITLE, Palette.TEXT_HEAD) + " -fx-padding: 8 0 2 0;");
        HBox titled = new HBox(10, title, sep, where);
        titled.setAlignment(Pos.CENTER_LEFT);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox head = new HBox(Palette.GAP_LOOSE, titled, gap,
                stepChip("‹ " + back, () -> handleAllBuildingMenus(from.title(), from.categories()), true));
        head.setAlignment(Pos.CENTER_LEFT);
        head.setMaxWidth(Double.MAX_VALUE);
        head.setStyle("-fx-padding: 0 0 4 0;");
        return head;
    }

    /**
     * The funding page for a run. A redraw - a month landing - re-quotes
     * it; one that finds nothing to fund any more, or nothing that can go
     * ahead, goes back to the page the run came from, its orders still on
     * their cards, rather than quoting a loan of nothing.
     */
    void showBuildFunding(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from) {
        RunWords w = runWords(run);
        if (w.funded().isEmpty() || !(w.gap() > 0)) {
            handleAllBuildingMenus(from.title(), from.categories());
            return;
        }
        ui.clearMenu("showBuildFunding", () -> showBuildFunding(run, from));

        VBox page = widePage();
        page.getChildren().add(runHead("Funding", from));

        Label shortChip = tag("short by " + w.shortBy(), Palette.BAD);
        shortChip.setMinWidth(Region.USE_PREF_SIZE);
        Label title = new Label(w.priceLine());
        title.setWrapText(true);
        title.setMinWidth(0);
        title.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        HBox.setHgrow(title, Priority.ALWAYS);
        HBox top = new HBox(Palette.GAP, title, shortChip);
        top.setAlignment(Pos.CENTER_LEFT);
        SegmentBar bar = segmentBar(List.of(
                new Segment(w.held(), Palette.MONEY, false, null, null, w.heldWords(), null),
                new Segment(w.gap(), Palette.BAD, true, null, null, "short " + w.shortBy(), null)), 0, null, 0, 14);
        Label held = new Label(w.heldWords());
        held.setWrapText(true);
        held.setMinWidth(0);
        held.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        Label gapWords = new Label("short " + w.shortBy());
        gapWords.setMinWidth(Region.USE_PREF_SIZE);
        gapWords.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.BAD));
        Region spread = new Region();
        HBox.setHgrow(spread, Priority.ALWAYS);
        HBox under = new HBox(6, held, spread, gapWords);
        under.setAlignment(Pos.CENTER_LEFT);
        VBox summary = new VBox(8, top, bar, under);
        if (w.stops() != null) {
            Label stops = new Label(w.stops());
            stops.setWrapText(true);
            stops.setMinWidth(0);
            stops.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.BAD));
            summary.getChildren().add(stops);
        }
        summary.setStyle("-fx-padding: 12 14 12 14;" + Palette.block(Palette.PANEL, Palette.EDGE));
        summary.setMaxWidth(Double.MAX_VALUE);
        page.getChildren().addAll(summary, sectionHead("TWO WAYS TO BORROW IT",
                hint("each books exactly the quote on its card, then builds")));

        javafx.scene.layout.GridPane offers = equalColumns(3, TILE_GAP);
        List<RunOffer> loans = runOffers(run, from);
        for (int i = 0; i < loans.size(); i++) {
            RunOffer o = loans.get(i);
            offers.add(offerCard(o.name(), null, o.quote(), o.rate(), o.tone(), o.ending(), o.action(),
                    Icons.BUILD, Palette.BUILDING, o.issue(), Money::money), i, 0);
        }
        page.getChildren().add(offers);

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> handleAllBuildingMenus(from.title(), from.categories()));
        page.getChildren().add(cancel);
        ui.rootMenu.getChildren().add(page);
    }

    /**
     * What the fell-short card says, worked out without drawing it: its
     * heading, then what was built, what was not and what it is still
     * short of - the model's gap for the orders left, as it stands now.
     */
    String[] fellShortWords(java.util.Map<BuildingsTemplate, Integer> built, java.util.Map<BuildingsTemplate, Integer> left,
                            RunFrom from, String paper) {
        double still = ui.game.buildFundingGap(left);
        StringBuilder s = new StringBuilder("The " + paper + " was issued and its cash is in the treasury. ");
        if (!built.isEmpty()) s.append("Built: ").append(runNames(built)).append(". ");
        s.append("Not built: ").append(runNames(left)).append(still > 0
                ? (left.size() == 1 ? " - it still costs " : " - they still cost ") + money(still) + " more than the city holds. "
                : " - the cash covers " + (left.size() == 1 ? "it" : "them") + " now. ");
        s.append("Nothing beyond the ").append(paper).append(built.isEmpty() ? "" : " and what was built").append(" was spent")
                .append(from.cards() ? "; what was not built is still on its card." : ".");
        return new String[] {
                built.isEmpty() ? "The money is in, the building is not" : "The money is in, the run is not all built",
                s.toString() };
    }

    /**
     * The loan went through and an order still did not: one red card in
     * Build's frame, as the land office's "Not bought" (0.7.40; it was
     * THE MONEY IS IN, THE BUILDING IS NOT, a page of its own). Should not
     * be reached - the invoice the loan is sized to is what the run is
     * charged - and kept because the state it describes, debt on the books
     * and something not built, is one the player CAN end up in and must be
     * told about.
     */
    void showBuildFellShort(java.util.Map<BuildingsTemplate, Integer> built, java.util.Map<BuildingsTemplate, Integer> left,
                            RunFrom from, String paper) {
        ui.clearMenu("showBuildFellShort", () -> showBuildFellShort(built, left, from, paper));
        String[] w = fellShortWords(built, left, from, paper);
        VBox page = widePage();
        page.getChildren().add(runHead("Not built", from));
        Button back = new Button("Back to " + (from.title() == null ? BuildAdvice.OVERVIEW : from.title()));
        back.setOnAction(e -> handleAllBuildingMenus(from.title(), from.categories()));
        page.getChildren().addAll(alert(w[0], w[1]), back);
        ui.rootMenu.getChildren().add(page);
    }
}
