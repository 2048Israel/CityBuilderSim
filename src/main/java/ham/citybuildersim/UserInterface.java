/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Jerus
 */
public class UserInterface extends Application {

    private Game game;
    private Stage stage;
    private VBox rootMenu;
    private VBox constructionPanel;
    private VBox cityPanel;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        game = new Game();

        //Initialize the core UI once
        this.rootMenu = new VBox(10);
        this.rootMenu.setAlignment(Pos.CENTER);

        // Persistent construction panel down the right-hand side. The menu system
        // swaps the contents of rootMenu constantly, so the panel lives outside it
        // in a BorderPane and survives every screen change.
        this.constructionPanel = new VBox(8);
        this.constructionPanel.setPrefWidth(280);
        this.constructionPanel.setStyle(
                "-fx-padding: 16; -fx-background-color: #f4f4f4;"
                + " -fx-border-color: #cccccc; -fx-border-width: 0 0 0 1;");

        // City overview down the left. Same reasoning as the construction panel:
        // it lives outside rootMenu so the menu system can't clear it away.
        this.cityPanel = new VBox(8);
        this.cityPanel.setPrefWidth(250);
        this.cityPanel.setStyle(
                "-fx-padding: 16; -fx-background-color: #f4f4f4;"
                + " -fx-border-color: #cccccc; -fx-border-width: 0 1 0 0;");

        BorderPane root = new BorderPane();
        root.setCenter(rootMenu);
        root.setLeft(cityPanel);
        root.setRight(constructionPanel);
        this.scene = new Scene(root);


        
        // The window title is the cheapest possible bug report: whatever a
        // player screenshots now says which build produced it.
        stage.setTitle(GameVersion.title());
        stage.setMaximized(true);
        
        stage.setScene(scene);
        stage.show();

        // button actions
        showMainMenu();

        
    }
    
    /**
     * Clears the menu area and refreshes the construction panel. Every screen
     * calls this instead of rootMenu.getChildren().clear() directly, so the panel
     * is always showing current data no matter which screen you're on.
     */
    private void clearMenu() {
        rootMenu.getChildren().clear();
        refreshCityPanel();
        refreshConstructionPanel();
    }

    private void showMainMenu() {
        clearMenu();
        
        

        Button startNewGame = new Button("Start New Game");
        Button resumeGame = new Button("Resume Game");
        Button loadGameSave = new Button("Load Game");
        Button saveGame = new Button("Save Game");
        Button settings = new Button("Settings");
        Button quit = new Button("Quit");
        
        startNewGame.setOnAction(e -> {
            // NOTE: this used to call showStartMenu() BEFORE game.newGame(),
            // so the screen was drawn using pre-init/stale game state and
            // never refreshed again. Init the game first, then draw the screen.
            game.newGame();
            showStartMenu();
        });
        resumeGame.setOnAction(e -> {
            game.resumeGame();
            showStartMenu();
        });
        loadGameSave.setOnAction(e -> showLoadMenu());
        saveGame.setOnAction(e -> showSavingMenu());
        quit.setOnAction(e -> game.toggleQuit());

        settings.setOnAction(e -> showSettingsMenu());
        

        

        // Small, grey, always there. The window title carries it too, but a
        // screenshot of the menu is what people actually send you.
        Label version = new Label(GameVersion.title());
        version.setStyle("-fx-font-size: 9px; -fx-text-fill: #9e9e9e; -fx-padding: 12 0 0 0;");

        // Where the log is, in the one place everyone can find. A bug report
        // that arrives with this file attached is worth ten that do not.
        Label logLine = new Label(GameLog.file() == null
                ? "(logging is not running)"
                : "Log: " + GameLog.file());
        logLine.setStyle("-fx-font-size: 9px; -fx-text-fill: #9e9e9e;");
        logLine.setWrapText(true);
        logLine.setMaxWidth(320);

        rootMenu.getChildren().addAll(
                startNewGame,
                resumeGame,
                loadGameSave,
                saveGame,
                settings,
                quit,
                version,
                logLine
        );

        
    }
    
    /* =========================================================================
       THE SAVE SYSTEM

       Ten numbered slots and an autosave. Every slot is labelled from the city
       inside it - month, people, money, when it was written - because that is
       what a player actually recognises a save by. A name is optional on top,
       for the ones worth remembering ("before the steel mill").

       The autosave appears on the load list and not the save list. That is the
       point of it: it cannot be spent on the city you were about to abandon.
       ========================================================================= */

    private static final java.time.format.DateTimeFormatter SAVED_AT =
            java.time.format.DateTimeFormatter.ofPattern("d MMM HH:mm");

    /** One line describing what is in a slot, or that it is empty. */
    private String slotSummary(int slot) {

        GameFiles files = game.getGameFiles();

        // Empty and unreadable are different things, and saying "Empty" for
        // both was the bug: the button stayed enabled because the FILE existed,
        // so clicking Load on a slot labelled Empty did nothing at all.
        if (files.slotIsEmpty(slot)) return "Empty";

        SaveHeader header = files.readHeader(slot);
        if (header == null) return "Damaged - this file cannot be read";

        if (header.isFromNewerBuild()) {
            return "From a newer version (" + header.getGameVersion() + ")";
        }

        String when = java.time.Instant.ofEpochMilli(header.getSavedAt())
                .atZone(java.time.ZoneId.systemDefault())
                .format(SAVED_AT);

        return String.format("Month %d  -  %s people  -  %s  -  %s",
                header.getMonth(),
                formatter.format(header.getPopulation()),
                money(header.getCash()),
                when);
    }

    private String slotTitle(int slot) {
        SaveHeader header = game.getGameFiles().readHeader(slot);
        String base = GameFiles.slotLabel(slot);
        return (header != null && header.hasName())
                ? base + " - " + header.getSlotName()
                : base;
    }

    /** A slot row: the label on the button, the city underneath it. */
    private VBox slotRow(int slot, java.util.function.IntConsumer onPick, boolean disableEmpty) {

        VBox row = new VBox(1);
        row.setAlignment(Pos.CENTER);

        GameFiles files = game.getGameFiles();
        boolean empty = files.slotIsEmpty(slot);
        boolean broken = files.slotIsUnreadable(slot);

        Button pick = new Button(slotTitle(slot));
        pick.setMaxWidth(300);

        // On the LOAD list, a slot is clickable only if it can actually be
        // loaded. A damaged file is not an empty slot and must not offer a
        // button that silently does nothing.
        pick.setDisable(disableEmpty && !files.slotIsLoadable(slot));
        pick.setOnAction(e -> onPick.accept(slot));

        Label detail = new Label(slotSummary(slot));
        detail.setStyle("-fx-font-size: 10px; -fx-text-fill: "
                + (broken ? "#c62828" : empty ? "#9e9e9e" : "#555555") + ";");

        row.getChildren().addAll(pick, detail);
        return row;
    }

    private void showSavingMenu() {
        clearMenu();

        Label heading = new Label("SAVE GAME");
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label hint = new Label("Choose a slot. The autosave is not in this list "
                + "on purpose - it is written for you.");
        hint.setStyle("-fx-font-size: 10px; -fx-text-fill: #555555;");
        hint.setWrapText(true);
        hint.setMaxWidth(320);

        rootMenu.getChildren().addAll(heading, hint);

        for (int slot = 1; slot <= GameFiles.SLOT_COUNT; slot++) {
            rootMenu.getChildren().add(slotRow(slot, this::showSaveSlotConfirm, false));
        }

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> showMainMenu());
        rootMenu.getChildren().add(cancel);
    }

    /**
     * Confirms one slot, and takes the optional name.
     *
     * The name field is prefilled with whatever the slot already carried, so
     * overwriting a save keeps its name unless the player chooses otherwise.
     */
    private void showSaveSlotConfirm(int slot) {
        clearMenu();

        boolean occupied = !game.getGameFiles().slotIsEmpty(slot);
        SaveHeader header = game.getGameFiles().readHeader(slot);

        Label heading = new Label("SAVE TO " + GameFiles.slotLabel(slot).toUpperCase());
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label current = new Label(slotSummary(slot));
        current.setStyle("-fx-font-size: 10px; -fx-text-fill: #555555;");

        TextField name = new TextField(
                (header != null && header.hasName()) ? header.getSlotName() : "");
        name.setPromptText("Name this save (optional)");
        name.setMaxWidth(300);

        rootMenu.getChildren().addAll(heading, current, name);

        if (occupied) {
            Label warn = new Label("This slot already has a city in it. Saving replaces it.");
            warn.setStyle("-fx-font-size: 10px; -fx-text-fill: #ef6c00;");
            warn.setWrapText(true);
            warn.setMaxWidth(320);
            rootMenu.getChildren().add(warn);
        }

        Button confirm = new Button(occupied ? "Overwrite" : "Save");
        Button cancel = new Button("Back");

        confirm.setOnAction(e -> {
            String typed = name.getText();
            showSaveResult(game.saveGame(slot,
                    (typed == null || typed.isBlank()) ? null : typed.trim()));
        });
        cancel.setOnAction(e -> showSavingMenu());

        rootMenu.getChildren().addAll(confirm, cancel);
    }

    private void showLoadMenu() {
        clearMenu();

        Label heading = new Label("LOAD GAME");
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        rootMenu.getChildren().add(heading);

        // Autosave first: it is the most recent thing the game wrote, so it is
        // what someone recovering from a crash is looking for.
        rootMenu.getChildren().add(
                slotRow(GameFiles.AUTOSAVE_SLOT, this::loadSlot, true));

        for (int slot = 1; slot <= GameFiles.SLOT_COUNT; slot++) {
            rootMenu.getChildren().add(slotRow(slot, this::loadSlot, true));
        }

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> showMainMenu());
        rootMenu.getChildren().add(cancel);
    }

    private void loadSlot(int slot) {

        game.loadGameSave(slot);

        String failure = game.getLoadFailure();
        if (failure != null) {
            clearMenu();
            Label outcome = new Label("Could not load.");
            outcome.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;"
                    + " -fx-text-fill: #c62828;");
            Label why = new Label(failure);
            why.setStyle("-fx-font-size: 10px; -fx-text-fill: #c62828;");
            why.setWrapText(true);
            why.setMaxWidth(320);
            Button back = new Button("Back");
            back.setOnAction(e -> showLoadMenu());
            rootMenu.getChildren().addAll(outcome, why, back);
            return;
        }

        showStartMenu();
    }

    private void showSaveResult(GameFiles.Result result) {
        clearMenu();

        Label outcome = new Label(result.ok ? "Saved." : "Save failed.");
        outcome.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: "
                + (result.ok ? "#2e7d32" : "#c62828") + ";");

        Label detail = new Label(result.ok
                ? result.file.toString()
                : result.error);
        detail.setStyle("-fx-font-size: 10px; -fx-text-fill: "
                + (result.ok ? "#555555" : "#c62828") + ";");
        detail.setWrapText(true);
        detail.setMaxWidth(320);

        Button back = new Button("Back");
        back.setOnAction(e -> showMainMenu());

        rootMenu.getChildren().addAll(outcome, detail, back);

        if (!result.ok) {
            // Worth saying out loud: the usual causes are a full disk or a
            // folder the player has no permission to write to, and neither is
            // something the game can fix for them.
            Label advice = new Label(
                    "The city is still running - nothing has been lost yet. "
                    + "Check there is free disk space, then try again.");
            advice.setStyle("-fx-font-size: 10px; -fx-text-fill: #555555;");
            advice.setWrapText(true);
            advice.setMaxWidth(320);
            rootMenu.getChildren().add(2, advice);
        }
    }

    private void showSettingsMenu() {
        clearMenu();

        Button graphs = new Button("Graphs: " + (game.isGraphsEnabled() ? "ON" : "OFF"));
        Button reports = new Button("Reports: " + (game.isReportsEnabled() ? "ON" : "OFF"));
        Button back = new Button("Back");

        // actions for the settings menu
        graphs.setOnAction(e -> {
            // toggle graphs in game, then redraw so the button label updates
            game.toggleGraphs();
            showSettingsMenu();
        });

        reports.setOnAction(e -> {
            // toggle reports in game, then redraw so the button label updates
            game.toggleReports();
            showSettingsMenu();
        });

        back.setOnAction(e -> {
            // go back to main menu
            showStartMenu();
        });

        rootMenu.getChildren().addAll(graphs, reports, back);

        
    }
    
    private void showStartMenu() {
    // clear old buttons/labels
    clearMenu();
    
    int month = game.getMonth();
    double cash = game.getCash();
    double income = game.getIncome();
    
    Label gameInfo = new Label("Month: " + month + " | Cash: $" + formatter.format(cash));

    Button buildings = new Button("Buildings");
    Button economy = new Button("Economy");
    Button policy = new Button("Policy");
    Button population = new Button("Population");
    Button nextMonth = new Button("Next Month: $" + formatter.format(income));
    Button simulateMultipleMonths = new Button("Simulate Multiple Months");
    Button back = new Button("Back");

    buildings.setOnAction(e -> showBuildingsMenu()); // current menu becomes previousMenu
    economy.setOnAction(e -> showEconomyMenu());
    policy.setOnAction(e -> showPolicyMenu());
    nextMonth.setOnAction(e -> {
        game.toggleNextMonth();
        showStartMenu();
    });
    back.setOnAction(e -> showMainMenu()); // go back to previous menu

    // NOTE: Population still has no JavaFX screen of its own - the demographics
    // report is reachable through Economy > Sector Info instead - so it stays
    // disabled. Simulate Multiple Months is now wired to Game.simulateMonths().
    population.setDisable(true);

    simulateMultipleMonths.setOnAction(e -> showSimulateMonthsMenu());

    rootMenu.getChildren().addAll(gameInfo, buildings, economy, policy, population, nextMonth, simulateMultipleMonths, back);

    /*
     * The one thing that interrupts the main screen.
     *
     * Construction dismantling itself is the single most expensive thing that
     * happens quietly in this game - it is what froze every city in the
     * 4,000-month playtest at 660 people - and it happens inside a skip, where
     * nothing is on screen at all. The fix exists and costs money, so the
     * banner asks the question rather than just reporting the fact, and the
     * button goes straight to the control.
     */
    if (game.isConstructionShedding()) {
        rootMenu.getChildren().add(constructionSheddingBanner(this::showStartMenu));
    }

    
}
    
    /**
     * "Your builders are being laid off. Do you want to pay to keep them?"
     *
     * Shared by the main screen and the skip report, so the wording and the
     * button are the same in both places - a player who sees it after a skip
     * and a player who sees it on the main menu are being asked one question,
     * not two.
     *
     * @param andThen where to go back to after the retainer is set, so the
     *                banner does not dump the player somewhere they did not
     *                come from
     */
    private VBox constructionSheddingBanner(Runnable andThen) {

        double capacity = game.getBuildingManager().getTotalConstructionCapacity();
        boolean protectedNow = game.isAutoSubsidised(PolicySector.CONSTRUCTION);
        double lost = game.getConstructionShedPoints();

        VBox banner = criticalSection("[!] YOUR BUILDERS ARE BEING LAID OFF",
                String.format("Construction sold %s points of capacity - it has no",
                        formatter.format(lost)),
                "orders, so it is losing money and shrinking to fit.",
                "",
                String.format("Capacity left:      %s pts", formatter.format(capacity)),
                protectedNow
                        ? "Standing policy:    ON - the city covers its losses"
                        : "Standing policy:    off - nothing is protecting them",
                "",
                "Everything you build runs through these crews. Once they are",
                "gone, rebuilding them is the slowest thing in the game.");

        // NOTE: this button used to set a fixed dollar retainer sized to today's
        // capacity, which is why it had to be pressed again every time the sector
        // grew - five times in one playtest. It turns the standing policy on now,
        // and the policy is measured against the loss rather than against a
        // number, so it does not go stale.
        Button pay = new Button("Keep them all - cover their losses");
        pay.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");
        pay.setOnAction(e -> {
            game.setAutoSubsidised(PolicySector.CONSTRUCTION, true);
            game.acknowledgeConstructionShedding();
            andThen.run();
        });

        Button manage = new Button("Set it myself");
        manage.setOnAction(e -> {
            game.acknowledgeConstructionShedding();
            showConstructionInfoMenu();
        });

        Button dismiss = new Button("Let them go");
        dismiss.setOnAction(e -> {
            game.acknowledgeConstructionShedding();
            andThen.run();
        });

        javafx.scene.layout.FlowPane actions = new javafx.scene.layout.FlowPane(8, 8);
        actions.setAlignment(Pos.CENTER);
        actions.setPrefWrapLength(340);
        actions.getChildren().addAll(pay, manage, dismiss);
        actions.setStyle("-fx-padding: 8 0 0 0;");

        banner.getChildren().add(actions);
        return banner;
    }

    private void showBuildingsMenu() {
        clearMenu();
        
        int constructionMaterials = game.getConstructionMaterials();
        double cash = game.getCash();
        
        Label gameInfo = new Label("Buildings Menu\nConstruction Materials: " + constructionMaterials + " | Cash: $" + formatter.format(cash));

        Button b1 = new Button("Residential");
        Button b2 = new Button("Commercial");
        Button b3 = new Button("Industrial");
        Button b4 = new Button("Other");
        Button b5 = new Button("Land");
        Button b6 = new Button("Infrastructure");
        Button b0 = new Button("Return to menu");
        
        b1.setOnAction(e-> {
            handleAllBuildingMenus("Residential Buildings",EnumSet.of(BuildingType.RESIDENTIAL));
        });
        b2.setOnAction(e-> {
            handleAllBuildingMenus("Commercial Buildings",EnumSet.of(BuildingType.COMMERCIAL));
        });
        b3.setOnAction(e-> {
            handleAllBuildingMenus("Industrial Buildings",EnumSet.of(BuildingType.INDUSTRIAL,BuildingType.HEAVY_INDUSTRY,BuildingType.MINING,BuildingType.CONSTRUCTION,BuildingType.ELECTRICITY,BuildingType.WATER));
        });

        // Its own button rather than a line in Industrial, because roads are the
        // one thing here the city builds for itself and gets no revenue from.
        // A player looking for the fix to congestion should not have to find it
        // filed under factories.
        b6.setOnAction(e-> {
            handleAllBuildingMenus("Infrastructure", EnumSet.of(BuildingType.INFRASTRUCTURE));
        });

        // NOTE: "Other" has no JavaFX screen yet (was buildingManager.displayAllBuildings()
        // printed to console in the old terminal build). Disabled rather than left as a
        // dead click until that screen is ported.
        b4.setDisable(true);

        b5.setOnAction(e -> showLandMenu());

        b0.setOnAction(e -> {
            // go back to main menu
            showStartMenu();
        });
        

        

        rootMenu.getChildren().addAll(gameInfo,b1,b2,b3,b6,b4,b5,b0);

        
    }
    
    /**
     * The land office: what the city owns, what it is buying, what it charges.
     *
     * Prices are held internally per square foot in thousands, because every
     * figure in the game is - but $0.009 is unreadable as a price, so this
     * screen works in whole dollars per square foot and converts on the way in
     * and out. The player thinks "nine dollars a foot"; the model stores .009.
     */
    private void showLandMenu() {
        clearMenu();

        LandManager land = game.getLandManager();

        Label title = new Label("LAND OFFICE");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        Label gameInfo = new Label("Month: " + game.getMonth()
                + " | Cash: $" + formatter.format(game.getCash()));

        VBox column = new VBox(0);

        /* -------------------------- what we hold -------------------------- */
        VBox holding = reportSection("CITY LAND",
                String.format("Owned:                  %s sq ft (%.1f blocks)",
                        formatter.format(land.getOwnedSqFt()),
                        land.getOwnedSqFt() / LandManager.BLOCK_SQ_FT),
                String.format("Built on:               %s sq ft",
                        formatter.format(land.getAllocatedSqFt())),
                String.format("Available:              %s sq ft (%.1f blocks)",
                        formatter.format(land.getAvailableSqFt()),
                        land.getAvailableBlocks()));

        // The number that decides whether anything can be built. Coloured,
        // because running out is silent otherwise - buildings simply stop
        // appearing and nothing on screen says why.
        double used = land.getUtilisation();
        Label utilisation = monoLabel(String.format("%-24s%.1f%%", "Utilisation:", used * 100));
        utilisation.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: "
                + (used >= .90 ? "#c62828" : used >= .75 ? "#ef6c00" : "#2e7d32") + ";");
        holding.getChildren().add(utilisation);

        if (used >= .90) {
            Label warning = monoLabel("  the city is nearly full - businesses will stop building");
            warning.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #c62828;");
            holding.getChildren().add(warning);
        }
        column.getChildren().add(holding);

        /* ------------------------- the listing ------------------------- */
        //
        // Ten plots, all different, some with ore under them. This replaced a
        // button with a price on it, which was a slider rather than a decision:
        // the price only ever went up, so the answer was always "now or later".
        column.getChildren().add(reportSection("ON OFFER",
                String.format("Market rate:            $%.2f /sq ft",
                        land.getAcquisitionCostPerSqFt() * 1000),
                "  (rises with the size of the city - land, and people)",
                String.format("Smallest plot sold:     %.0f block%s",
                        land.getMarket().getMinBlocks(),
                        land.getMarket().getMinBlocks() == 1 ? "" : "s"),
                "  (the office stops splitting lots as the city grows)",
                String.format("Iron deposits owned:    %d, %s tonnes in the ground",
                        land.getIronDeposits(),
                        formatter.format(land.getIronReserveTonnes())),
                String.format("Mines on them:          %d", game.minesCommitted())));

        VBox offers = new VBox(4);
        offers.setStyle("-fx-padding: 6 0 0 0;");

        for (LandParcel parcel : land.getListing()) {

            boolean affordable = parcel.getPrice() <= game.getCash();

            // The SITE COUNT leads on a multi-deposit plot, because that is what
            // decides how many mines it is worth. The tonnage is centuries deep
            // either way, so it is the smaller of the two numbers in practice.
            String ore = !parcel.hasIron() ? ""
                    : parcel.getDeposits() > 1
                        ? String.format("IRON x%d  %,.0fk t",
                                parcel.getDeposits(), parcel.getIronTonnes() / 1000)
                        : String.format("IRON %,.0fk t", parcel.getIronTonnes() / 1000);

            Label detail = monoLabel(String.format("%,10.0f sq ft  %5.1f blk   $%,10.0f  %s",
                    parcel.getSizeSqFt(), parcel.getBlocks(), parcel.getPrice(), ore));
            detail.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px; -fx-text-fill: "
                    + (parcel.hasIron() ? "#6a1b9a" : "#555555") + ";");

            Button buy = new Button("Buy");
            buy.setDisable(!affordable);
            buy.setOnAction(e -> {
                game.buyLandParcel(parcel.getId());
                showLandMenu();
            });

            javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(8, buy, detail);
            row.setAlignment(Pos.CENTER_LEFT);
            offers.getChildren().add(row);
        }
        column.getChildren().add(offers);

        // The old button, kept: sometimes a player just wants some land and
        // does not want to read ten rows to get it.
        javafx.scene.layout.FlowPane buying = new javafx.scene.layout.FlowPane(8, 8);
        buying.setAlignment(Pos.CENTER);
        buying.setPrefWrapLength(340);

        LandParcel cheapest = land.getMarket().cheapest();
        Button quick = new Button("Buy the cheapest");
        quick.setDisable(cheapest == null || cheapest.getPrice() > game.getCash());
        quick.setOnAction(e -> {
            game.buyLandBlock();
            showLandMenu();
        });
        buying.getChildren().add(quick);

        /* --------------------------- selling --------------------------- */
        double price = land.getPricePerSqFt();
        double margin = land.getMarginPerSqFt();

        VBox selling = reportSection("SELLING TO BUSINESSES",
                String.format("Your price:             $%.2f /sq ft", price * 1000),
                String.format("Costs you:              $%.2f /sq ft",
                        land.getAcquisitionCostPerSqFt() * 1000));

        Label marginLine = monoLabel(String.format("%-24s$%.2f /sq ft", "Margin:", margin * 1000));
        marginLine.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: "
                + (margin < 0 ? "#c62828" : "#2e7d32") + ";");
        selling.getChildren().add(marginLine);

        // What the price actually means to a buyer, since "per square foot" is
        // not a number anyone can price a decision from.
        BuildingsTemplate house = game.getBuildingManager().getTemplateByName("House");
        BuildingsTemplate plant = game.getBuildingManager().getTemplateByName("Food Processing Plant");

        if (house != null) {
            selling.getChildren().add(monoLabel(String.format("%-24s$%s on top of $%s to build",
                    "A house plot:", formatter.format(land.priceFor(house.getLandSqFt())),
                    formatter.format(house.getCashCost()))));
        }
        if (plant != null) {
            selling.getChildren().add(monoLabel(String.format("%-24s$%s on top of $%s to build",
                    "A food plant plot:", formatter.format(land.priceFor(plant.getLandSqFt())),
                    formatter.format(plant.getCashCost()))));
        }

        selling.getChildren().add(monoLabel(String.format("%-24s%s sq ft for $%s",
                "Sold this month:", formatter.format(land.getSqFtSoldThisMonth()),
                formatter.format(land.getLandSalesThisMonth()))));
        column.getChildren().add(selling);

        /*
         * No buttons here any more.
         *
         * What businesses pay used to be a dial the player turned. It is a
         * market now: supply against demand inside the city limits, so a city
         * with empty blocks sells cheap and a full one sells dear. The way to
         * make land cheap for your builders is to go and buy some, which is a
         * better decision than a slider was.
         */
        selling.getChildren().add(monoLabel(String.format("%-24s%.0f%% full",
                "Set by demand:", land.getUtilisation() * 100)));

        /* ------------------- who is waiting on land ------------------- */
        // Last month's decisions, not this instant's: the sectors only decide
        // once a month, so buying land now shows up in these lines next month.
        VBox waiting = reportSection("WHO IS WAITING (as of last month)");
        boolean anyone = false;

        String[] sectors = {BusinessDebtManager.REAL_ESTATE, BusinessDebtManager.RETAIL,
                BusinessDebtManager.INDUSTRY, BusinessDebtManager.CONSTRUCTION};

        for (String sector : sectors) {
            String last = game.getLastInvestment(sector);
            if (last != null && last.contains("no land")) {
                Label line = monoLabel("  " + sector + ": " + last.replace("Holding: ", ""));
                line.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #c62828;");
                waiting.getChildren().add(line);
                anyone = true;
            }
        }

        if (!anyone) {
            waiting.getChildren().add(monoLabel("  nobody - every sector has room to build"));
        }
        column.getChildren().add(waiting);

        VBox content = new VBox(0);
        content.setAlignment(Pos.CENTER);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        content.getChildren().add(column);

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(420);
        scroll.setStyle("-fx-background-color:transparent;");

        // No price controls any more - what businesses pay is the market's, not
        // the player's. What is left to do on this screen is buy ground.
        Label buyLabel = new Label("Or take the cheapest thing on offer");

        Button back = new Button("Back");
        back.setOnAction(e -> showBuildingsMenu());

        rootMenu.getChildren().addAll(title, gameInfo, scroll, buyLabel, buying, back);
    }

    private void showEconomyMenu() {
        clearMenu();
        
        int month = game.getMonth();
        double cash = game.getCash();
        double rate = game.getDebtManager().getRate();
    double totalDebt = game.getDebtManager().getAllPrincipal();
    
    // A quick-glance status bar
    Label marketStatus = new Label(String.format(
        "Market Rate: %.2f%% | Total Debt: $%s", 
        rate * 100, formatter.format(totalDebt)
    ));
    marketStatus.setStyle("-fx-text-fill: #1a237e; -fx-font-weight: bold; -fx-background-color: #e8eaf6; -fx-padding: 10;");
        
        Label gameInfo = new Label("Month: " + month + " | Cash: $" + formatter.format(cash));

        Button b1 = new Button("Finance");
        Button b2 = new Button("Restructure");
        Button b3 = new Button("Debt Info");
        Button b4 = new Button("Sector Info");
        Button b5 = new Button("Government & National Accounts");
        // NOTE: "Tax Policy" used to sit here. It moved to the Policy tab, with
        // the standing subsidies, because they are the same decision seen from
        // two sides - what you charge a sector and what you are prepared to pay
        // to keep it - and having them two menus apart made that invisible.
        
        Button b0 = new Button("Back");
        
        b1.setOnAction(e -> {
            // go back to main menu
            showFinanceMenu();
        });
        b3.setOnAction(e -> showDebtInfoMenu());
        b4.setOnAction(e -> showSectorMenu());
        b5.setOnAction(e -> showGovernmentMenu());

        // NOTE: "Restructure" (b2) was never implemented even in the old terminal
        // menu (its case was an empty stub) - leaving disabled. "Sector Info" (b4)
        // is now wired up below.
        b2.setDisable(true);

        b0.setOnAction(e -> {
            // go back to main menu
            showStartMenu();
        });
        

        

        rootMenu.getChildren().addAll(marketStatus,gameInfo,b1,b2,b3,b4,b5, b0);

        
    }
    
    /**
     * The two rates, together, because they are only interesting against each
     * other: income tax takes a share of what a business earned, property tax
     * takes a share of what it owns whether it earned anything or not.
     *
     * The screen shows what each is currently raising and what every sector
     * owes, so the choice between them is made against real numbers rather than
     * in the abstract.
     */
    private void showTaxPolicyMenu() {
        clearMenu();

        EconomyManager em = game.getEconomyManager();
        TaxPolicy policy = em.getTaxPolicy();
        NationalAccounts na = em.getNationalAccounts();

        Label title = new Label("TAX POLICY");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        Label gameInfo = new Label("Month: " + game.getMonth()
                + " | Cash: $" + formatter.format(game.getCash()));

        VBox column = new VBox(0);

        /* --------------------------- income tax --------------------------- */
        column.getChildren().add(reportSection(
                String.format("INCOME TAX  -  %.0f%% of what a business earns",
                        policy.getIncomeTaxRate() * 100),
                String.format("Business Tax:           $%s", formatter.format(na.getTaxBusiness())),
                String.format("Industrial Tax:         $%s", formatter.format(na.getTaxIndustrial())),
                String.format("Sales Tax:              $%s", formatter.format(na.getTaxSales())),
                String.format("Wage Tax:               $%s", formatter.format(na.getTaxWage())),
                "---------------------------------------------------",
                String.format("Raised last month:      $%s", formatter.format(
                        na.getTaxBusiness() + na.getTaxIndustrial()
                                + na.getTaxSales() + na.getTaxWage())),
                "  a sector that loses money pays none of this"));

        /* -------------------------- property tax -------------------------- */
        VBox property = reportSection(
                String.format("PROPERTY TAX  -  %.2f%% a year on land and buildings",
                        policy.getPropertyTaxRate() * 100),
                String.format("Charged monthly at:     %.4f%% of assessed value",
                        policy.getMonthlyPropertyTaxRate() * 100),
                String.format("Raised last month:      $%s", formatter.format(na.getPropertyTax())),
                "");

        // What each sector is assessed on and owes. This is where the interaction
        // with the land price becomes visible: raising what land sells for
        // raises what every existing owner is assessed at.
        BuildingType[] taxed = {
                BuildingType.RESIDENTIAL, BuildingType.COMMERCIAL,
                BuildingType.INDUSTRIAL, BuildingType.CONSTRUCTION,
                BuildingType.HEAVY_INDUSTRY };
        String[] names = { "Real Estate", "Retail", "Industry", "Construction",
                "Heavy Industry" };

        property.getChildren().add(monoLabel(
                String.format("%-16s%14s%14s", "", "ASSESSED", "PER MONTH")));

        for (int i = 0; i < taxed.length; i++) {
            property.getChildren().add(monoLabel(String.format("%-16s%14s%14s",
                    names[i],
                    formatter.format(em.getAssessedValue(taxed[i])),
                    formatter.format(em.getPropertyTaxFor(taxed[i])))));
        }

        property.getChildren().add(monoLabel(
                "  the power and water plants are the city's own and exempt"));
        property.getChildren().add(monoLabel(
                "  charged whether or not the business earned anything"));
        column.getChildren().add(property);

        /* ------------------------- who is in trouble ------------------------- */
        BusinessDebtManager credit = em.getBusinessDebtManager();

        VBox distress = reportSection("SOLVENCY");
        boolean anyTrouble = false;

        for (String sector : BusinessDebtManager.SECTORS) {

            if (credit.isBorrowingBlocked(sector) || credit.getWrittenOffTotal(sector) > 0) {
                Label line = monoLabel(String.format("  %-14s %s",
                        sector,
                        credit.isBorrowingBlocked(sector)
                                ? "in default - cannot borrow for "
                                        + credit.getBlockedMonths(sector) + " more months"
                                : String.format("restructured %d time(s), $%s written off",
                                        credit.getRestructureCount(sector),
                                        formatter.format(credit.getWrittenOffTotal(sector)))));
                line.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: "
                        + (credit.isBorrowingBlocked(sector) ? "#c62828" : "#ef6c00") + ";");
                distress.getChildren().add(line);
                anyTrouble = true;
            }
        }

        if (!anyTrouble) {
            distress.getChildren().add(monoLabel("  every sector is servicing its debt"));
        }
        column.getChildren().add(distress);

        VBox content = new VBox(0);
        content.setAlignment(Pos.CENTER);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        content.getChildren().add(column);

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(380);
        scroll.setStyle("-fx-background-color:transparent;");

        /* ----------------------------- the dials ----------------------------- */
        Label incomeLabel = new Label("Income tax");
        javafx.scene.layout.FlowPane incomeDial = new javafx.scene.layout.FlowPane(8, 8);
        incomeDial.setAlignment(Pos.CENTER);

        double[] incomeSteps = { -5, -1, 1, 5 };
        for (double step : incomeSteps) {
            final double delta = step / 100;
            Button button = new Button((step > 0 ? "+" : "") + String.format("%.0f%%", step));
            button.setPrefWidth(66);
            button.setOnAction(e -> {
                policy.setIncomeTaxRate(policy.getIncomeTaxRate() + delta);
                showTaxPolicyMenu();
            });
            incomeDial.getChildren().add(button);
        }

        Label propertyLabel = new Label("Property tax (per year)");
        javafx.scene.layout.FlowPane propertyDial = new javafx.scene.layout.FlowPane(8, 8);
        propertyDial.setAlignment(Pos.CENTER);

        // Quarter-point steps, because the whole range that matters is 0-4%.
        double[] propertySteps = { -1, -.25, .25, 1 };
        for (double step : propertySteps) {
            final double delta = step / 100;
            Button button = new Button((step > 0 ? "+" : "") + String.format("%.2f%%", step));
            button.setPrefWidth(72);
            button.setOnAction(e -> {
                policy.setPropertyTaxRate(policy.getPropertyTaxRate() + delta);
                showTaxPolicyMenu();
            });
            propertyDial.getChildren().add(button);
        }

        Button toLand = new Button("Land Office");
        toLand.setOnAction(e -> showLandMenu());

        Button back = new Button("Back");
        back.setOnAction(e -> showPolicyMenu());

        rootMenu.getChildren().addAll(title, gameInfo, scroll,
                incomeLabel, incomeDial, propertyLabel, propertyDial, toLand, back);
    }

    /* =====================================================================
       THE POLICY TAB

       Three screens, because there are three different decisions and putting
       them on one page made none of them readable: what the city charges people
       (Wages), what it charges businesses (Business), and what it is prepared to
       pay to keep a sector alive (Subsidies).

       Every rate below is an OFFSET from a city rate, so each row prints the
       offset AND what it resolves to. A screen that showed only "-3%" would be
       asking the player to do the arithmetic that TaxPolicy already did.
       ===================================================================== */

    private void showPolicyMenu() {
        clearMenu();

        TaxPolicy policy = game.getEconomyManager().getTaxPolicy();

        Label title = new Label("POLICY");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        Label rates = monoLabel(String.format(
                "City income tax %.1f%%     City property tax %.2f%%/yr",
                policy.getIncomeTaxRate() * 100, policy.getPropertyTaxRate() * 100));

        int protectedCount = 0;
        for (PolicySector sector : PolicySector.values()) {
            if (game.isAutoSubsidised(sector)) protectedCount++;
        }

        Label standing = monoLabel(String.format(
                "%d of %d sectors protected     paid last month $%s",
                protectedCount, PolicySector.values().length,
                formatter.format(game.getTotalSubsidyPaid())));
        standing.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px; -fx-text-fill: "
                + (protectedCount > 0 ? "#2e7d32" : "#555555") + ";");

        Button taxes = new Button("City Tax Rates");
        taxes.setOnAction(e -> showTaxPolicyMenu());

        Button wages = new Button("Wages - by education");
        wages.setOnAction(e -> showWagePolicyMenu());

        Button business = new Button("Business - by sector");
        business.setOnAction(e -> showBusinessPolicyMenu());

        Button subsidies = new Button("Subsidies - what you will protect");
        subsidies.setOnAction(e -> showSubsidyPolicyMenu());

        Button back = new Button("Back");
        back.setOnAction(e -> showStartMenu());

        rootMenu.getChildren().addAll(title, rates, standing,
                taxes, wages, business, subsidies, back);
    }

    /** One row of -/+ buttons that move an offset and redraw. */
    private javafx.scene.layout.FlowPane offsetDial(double[] steps, String format,
                                                    java.util.function.DoubleConsumer move,
                                                    Runnable redraw) {
        javafx.scene.layout.FlowPane dial = new javafx.scene.layout.FlowPane(6, 6);
        dial.setAlignment(Pos.CENTER_LEFT);
        for (double step : steps) {
            final double delta = step / 100;
            Button b = new Button((step > 0 ? "+" : "") + String.format(format, step));
            b.setPrefWidth(58);
            b.setOnAction(e -> { move.accept(delta); redraw.run(); });
            dial.getChildren().add(b);
        }
        return dial;
    }

    private void showWagePolicyMenu() {
        clearMenu();

        TaxPolicy policy = game.getEconomyManager().getTaxPolicy();

        Label title = new Label("WAGE TAX");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        VBox column = new VBox(6);
        column.setAlignment(Pos.TOP_LEFT);

        column.getChildren().add(monoLabel(String.format(
                "City income tax %.1f%% - each band is set as a move from it.",
                policy.getIncomeTaxRate() * 100)));
        column.getChildren().add(monoLabel(
                "The eleven job types are grouped by the education they need."));

        for (WageBand band : WageBand.values()) {
            double offset = policy.getWageOffset(band);
            double effective = policy.effectiveWageRate(band);

            Label row = monoLabel(String.format("%-12s  %+5.1f pts  ->  %5.2f%%",
                    band.label(), offset * 100, effective * 100));
            row.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px; -fx-text-fill: "
                    + (offset < 0 ? "#2e7d32" : offset > 0 ? "#b00020" : "#555555") + ";");

            column.getChildren().add(row);
            column.getChildren().add(offsetDial(new double[]{ -5, -1, 1, 5 }, "%.0f",
                    d -> policy.setWageOffset(band, policy.getWageOffset(band) + d),
                    this::showWagePolicyMenu));
        }

        Button back = new Button("Back");
        back.setOnAction(e -> showPolicyMenu());

        rootMenu.getChildren().addAll(title, scrolled(column), back);
    }

    private void showBusinessPolicyMenu() {
        clearMenu();

        TaxPolicy policy = game.getEconomyManager().getTaxPolicy();

        Label title = new Label("BUSINESS TAX BY SECTOR");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        VBox column = new VBox(4);
        column.setAlignment(Pos.TOP_LEFT);
        column.getChildren().add(monoLabel(
                "Profit and sales move from the city income tax; property from"));
        column.getChildren().add(monoLabel(
                "the property rate. Sales tax is charged on VALUE ADDED - what a"));
        column.getChildren().add(monoLabel(
                "sector sells, less the tax it already paid on what it bought."));

        SalesTaxLedger vat = game.getEconomyManager().getSalesTaxLedger();

        for (PolicySector sector : PolicySector.values()) {

            Label head = monoLabel(String.format("%s   (net sales tax last month $%s)",
                    sector.label().toUpperCase(), formatter.format(vat.getNet(sector))));
            head.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px; "
                    + "-fx-font-weight: bold; -fx-padding: 8 0 0 0;");
            column.getChildren().add(head);

            column.getChildren().add(sectorRow("profit  ",
                    policy.getProfitOffset(sector), policy.effectiveProfitRate(sector)));
            column.getChildren().add(offsetDial(new double[]{ -5, -1, 1, 5 }, "%.0f",
                    d -> policy.setProfitOffset(sector, policy.getProfitOffset(sector) + d),
                    this::showBusinessPolicyMenu));

            column.getChildren().add(sectorRow("sales   ",
                    policy.getSalesOffset(sector), policy.effectiveSalesRate(sector)));
            column.getChildren().add(offsetDial(new double[]{ -5, -1, 1, 5 }, "%.0f",
                    d -> policy.setSalesOffset(sector, policy.getSalesOffset(sector) + d),
                    this::showBusinessPolicyMenu));

            column.getChildren().add(sectorRow("property",
                    policy.getPropertyOffset(sector), policy.effectivePropertyRate(sector)));
            column.getChildren().add(offsetDial(new double[]{ -1, -.25, .25, 1 }, "%.2f",
                    d -> policy.setPropertyOffset(sector, policy.getPropertyOffset(sector) + d),
                    this::showBusinessPolicyMenu));
        }

        Button back = new Button("Back");
        back.setOnAction(e -> showPolicyMenu());

        rootMenu.getChildren().addAll(title, scrolled(column), back);
    }

    private Label sectorRow(String what, double offset, double effective) {
        Label row = monoLabel(String.format("   %s  %+6.2f pts  ->  %5.2f%%",
                what, offset * 100, effective * 100));
        row.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px; -fx-text-fill: "
                + (offset < 0 ? "#2e7d32" : offset > 0 ? "#b00020" : "#555555") + ";");
        return row;
    }

    private void showSubsidyPolicyMenu() {
        clearMenu();

        Label title = new Label("STANDING SUBSIDIES");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        VBox column = new VBox(6);
        column.setAlignment(Pos.TOP_LEFT);
        column.getChildren().add(monoLabel(
                "A protected sector is topped up to break-even every month it"));
        column.getChildren().add(monoLabel(
                "loses money, so it never sells its capacity. The city pays what"));
        column.getChildren().add(monoLabel(
                "it takes - and goes overdrawn if it must, which costs interest."));

        for (PolicySector sector : PolicySector.values()) {

            boolean on = game.isAutoSubsidised(sector);
            double paid = game.getSubsidyPaid(sector);

            Label row = monoLabel(String.format("%-16s %-4s   paid last month $%s",
                    sector.label(), on ? "ON" : "off", formatter.format(paid)));
            row.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px; -fx-text-fill: "
                    + (on ? "#2e7d32" : "#555555") + ";");

            Button toggle = new Button(on ? "Stop protecting" : "Protect this sector");
            if (on) {
                toggle.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");
            }
            toggle.setOnAction(e -> {
                game.setAutoSubsidised(sector, !game.isAutoSubsidised(sector));
                showSubsidyPolicyMenu();
            });

            javafx.scene.layout.HBox line = new javafx.scene.layout.HBox(10, toggle, row);
            line.setAlignment(Pos.CENTER_LEFT);
            column.getChildren().add(line);
        }

        column.getChildren().add(monoLabel(String.format(
                "%nTotal paid last month: $%s", formatter.format(game.getTotalSubsidyPaid()))));

        Button back = new Button("Back");
        back.setOnAction(e -> showPolicyMenu());

        rootMenu.getChildren().addAll(title, scrolled(column), back);
    }

    /** A left-aligned column inside a scroll pane, which these screens all want. */
    private javafx.scene.control.ScrollPane scrolled(VBox column) {
        VBox content = new VBox(0);
        content.setAlignment(Pos.CENTER);
        column.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        content.getChildren().add(column);

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(400);
        scroll.setStyle("-fx-background-color:transparent;");
        return scroll;
    }

    private void showFinanceMenu() {
        clearMenu();
        
        int month = game.getMonth();
        double cash = game.getCash();
        double interest = game.getInterestRate();
        
        Label gameInfo = new Label("Month: " + month + " | Cash: $" + formatter.format(cash)+ "\nInterestRate: " + formatter.format(interest*100)+"%");

        Button b1 = new Button("Short Term T-Bills");
        Button b2 = new Button("Medium Term Bonds");
        Button b3 = new Button("Long Term Bonds");
        
        ;
        Button b0 = new Button("Back");
        
        b1.setOnAction(e -> showDebtIssuanceMenu("T-Bill", 3, 12, 1000));
        b2.setOnAction(e -> showDebtIssuanceMenu("Medium-Term", 1, 10, 10000));
        b3.setOnAction(e -> showDebtIssuanceMenu("Long-Term", 10, 50, 100000));
        
        b0.setOnAction(e -> {
            // go back to main menu
            showEconomyMenu();
        });
        

        

        rootMenu.getChildren().addAll(gameInfo,b1,b2,b3,b0);

        
    }
    
    private void handleAllBuildingMenus(String menuTitle, EnumSet<BuildingType> categories) {
        clearMenu();
        BuildingManager buildingManager = game.getBuildingManager();

        // 1. General Info Label
        Label gameInfo = new Label("    " + menuTitle + "    \n"
                + "Cash: $" + formatter.format(game.getCash()) + "\n"
                + "ConstructionMaterials: " + game.getConstructionMaterials());

        // 2. The Building Selection Buttons
        List<BuildingsTemplate> buildings = buildingManager.getTemplatesByCategory(categories);
        VBox buildingsBox = new VBox(5); // Keep buttons organized
        buildingsBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < buildings.size(); i++) {
            final int index = i;
            Button b = new Button(buildings.get(i).getName());
            b.setOnAction(e -> {
                handleBuildingTextBox(buildings.get(index),menuTitle, categories, quantity -> {
                    game.buildStack(buildings.get(index), quantity, false);
                    handleAllBuildingMenus(menuTitle, categories); // Refresh shows the receipt
                });
            });
            buildingsBox.getChildren().add(b);
        }

        // 3. The "Back" Button - Clears the receipt logic
        Button b0 = new Button("Back");
        b0.setOnAction(e -> {
            game.clearReceipt(); // Reset the flag so it's gone next time
            showBuildingsMenu();
        });

        // 4. THE RECEIPT (Only adds if a build just happened)
        rootMenu.getChildren().addAll(gameInfo, buildingsBox, b0);

        if (game.hasNewReceipt()) {
            Label buildInfo = new Label("\n--- LAST TRANSACTION ---"
                    + "\n" + game.getBuildingName()
                    + "\nMaterials Imported: " + formatter.format(game.getMaterialsUsed())
                    + "\nTotal Cost: $" + formatter.format(game.getTotalBuildingCost()));
            buildInfo.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-border-color: #2e7d32; -fx-padding: 5;");
            rootMenu.getChildren().add(buildInfo);
        }
    }

    public void handleBuildingTextBox(BuildingsTemplate template, String menuTitle, EnumSet<BuildingType> categories, Consumer<Integer> onConfirm) {
        clearMenu();

        // 1. Local state for this menu session
        // We use a 1-element array because local variables used in lambdas must be final
        final int[] runningTotal = {0};

        // 2. Display the current selection
        Label totalLabel = new Label("Total Quantity: 0");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // 3. Create the increment buttons
        int[] increments = {1, 5, 10, 20, 50, 100, 500, 1000};
        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.CENTER);

        // We'll put buttons in rows of 4 for better layout
        javafx.scene.layout.FlowPane buttonGrid = new javafx.scene.layout.FlowPane(10, 10);
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setPrefWrapLength(300);

        for (int amount : increments) {
            Button btn = new Button("+" + amount);
            btn.setPrefWidth(60);
            btn.setOnAction(e -> {
                runningTotal[0] += amount;
                totalLabel.setText("Total Quantity: " + runningTotal[0]);
            });
            buttonGrid.getChildren().add(btn);
        }

        // 4. Utility Buttons (Reset, Confirm, Back)
        Button reset = new Button("Reset");
        reset.setOnAction(e -> {
            runningTotal[0] = 0;
            totalLabel.setText("Total Quantity: 0");
        });

        Button confirm = new Button("Confirm Purchase");
        confirm.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        confirm.setOnAction(e -> {
            if (runningTotal[0] > 0) {
                // NOW we use the 'template' passed into the method
                Game.BuildResult result = game.buildStack(template, runningTotal[0], false);

                if (result == Game.BuildResult.SUCCESS) {
                    handleAllBuildingMenus(menuTitle, categories);
                } else if (result == Game.BuildResult.NEEDS_FUNDING) {
                    showQuickDebtMenu(template, runningTotal[0], menuTitle, categories);
                } else if (result == Game.BuildResult.NO_LAND) {
                    showNoLandMenu(template, runningTotal[0], menuTitle, categories);
                } else if (result == Game.BuildResult.NO_DEPOSIT) {
                    showNoDepositMenu(template, runningTotal[0], menuTitle, categories);
                }
            }
        });

        Button back = new Button("Cancel");
        back.setOnAction(e -> handleAllBuildingMenus(menuTitle, categories));

        // 5. Assemble everything
        rootMenu.getChildren().addAll(totalLabel, buttonGrid, reset, confirm, back);
    }
    
    /**
     * The city has the money and nowhere to put the building.
     *
     * Its own separate screen rather than a line on the funding screen, because
     * the two refusals have opposite answers: no cash is solved by borrowing,
     * no land only by annexing, and offering a T-Bill for a land shortage would
     * sell debt that cannot fix the problem.
     */
    /**
     * The city has the money, the land, and nothing to dig.
     *
     * Its own refusal for the same reason no-land has one: the answer is
     * specific. A mine is not short of cash or short of space, it is short of
     * ore, and the only thing that fixes that is a land parcel with iron under
     * it. Sending the player to the funding screen would sell them a bond that
     * cannot help.
     */
    private void showNoDepositMenu(BuildingsTemplate selected, int quantity,
                                   String menuTitle, EnumSet<BuildingType> categories) {
        clearMenu();

        LandManager land = game.getLandManager();

        Label title = new Label("NO IRON DEPOSIT");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        VBox explanation = reportSection("WHY",
                quantity + " x " + selected.getName() + " needs "
                        + (game.minesCommitted() + quantity) + " deposit(s).",
                "The city owns " + land.getIronDeposits()
                        + ", with " + game.minesCommitted() + " already spoken for.",
                "",
                "A mine has to stand on ground with ore under it. Deposits come",
                "with land: some parcels in the land office have iron, and they",
                "cost more because of it.");

        Label reserves = monoLabel(String.format(
                "Ore still in the ground: %,.0f tonnes", land.getIronReserveTonnes()));
        reserves.setStyle("-fx-font-family: 'Courier New'; -fx-padding: 6 0 0 0;");

        Button toLand = new Button("Go to the Land Office");
        toLand.setOnAction(e -> showLandMenu());

        Button back = new Button("Back");
        back.setOnAction(e -> handleAllBuildingMenus(menuTitle, categories));

        rootMenu.getChildren().addAll(title, explanation, reserves, toLand, back);
    }

    private void showNoLandMenu(BuildingsTemplate selected, int quantity,
                                String prevTitle, EnumSet<BuildingType> prevCats) {
        clearMenu();

        LandManager land = game.getLandManager();
        double needed = game.landNeededFor(selected, quantity);
        double have = land.getAvailableSqFt();
        double short_ = Math.max(needed - have, 0);
        double blocks = Math.ceil(short_ / LandManager.BLOCK_SQ_FT);

        Label warning = new Label("NOT ENOUGH LAND");
        warning.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label details = new Label(String.format(
                "%,d x %s needs %s sq ft%n"
                        + "The city has %s sq ft free%n"
                        + "Short by %s sq ft - about %.0f block%s",
                quantity, selected.getName(), formatter.format(needed),
                formatter.format(have), formatter.format(short_),
                blocks, blocks == 1 ? "" : "s"));

        Label cost = new Label(String.format(
                "Buying %.0f block%s costs roughly $%s",
                blocks, blocks == 1 ? "" : "s",
                formatter.format(land.getNextBlockCost() * blocks)));
        cost.setStyle("-fx-text-fill: #555555;");

        Button toLand = new Button("Go to the Land Office");
        toLand.setOnAction(e -> showLandMenu());

        Button back = new Button("Back");
        back.setOnAction(e -> handleAllBuildingMenus(prevTitle, prevCats));

        rootMenu.getChildren().addAll(warning, details, cost, toLand, back);
    }

    /**
     * "You cannot afford this - borrow for it?" with the terms on the screen.
     *
     * NOTE: this used to gross the gap up itself using getRate(), the standing
     * rate, and hand the resulting face value to issueEmergencyDebt() - which
     * then grossed it up a second time off the same stale rate. The quote does
     * both now, priced with the bill included, and the button books precisely
     * what is printed above it.
     */
    private void showQuickDebtMenu(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats) {
    clearMenu();

    double totalCost = game.calculateTotalCost(selected, quantity);
    double gap = totalCost - game.getCash();

    // Three months, matching what issueEmergencyDebt books.
    DebtQuote quote = game.quoteTBill(gap, 3, 1000.0);

    Label warning = new Label("INSUFFICIENT FUNDS");
    warning.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

    Label details = new Label(String.format(
        "Funding Required: $%s%n%s",
        formatter.format(gap), quote.summary()));
    details.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

    Label impact = new Label(quote.creditImpact());
    impact.setStyle(rateStyle(quote));

    Button confirmDebt = new Button("Issue T-Bill");
    confirmDebt.setOnAction(e -> {
        game.issueEmergencyDebt(gap, 3);            // quotes it again, identically
        game.buildStack(selected, quantity, false);
        handleAllBuildingMenus(prevTitle, prevCats);
    });

    Button cancel = new Button("Cancel Build");
    cancel.setOnAction(e -> handleAllBuildingMenus(prevTitle, prevCats));

    rootMenu.getChildren().addAll(warning, details, impact, confirmDebt, cancel);
}

    /**
     * Colours a quoted rate by how punishing it is.
     *
     * Not decoration. The whole point of showing the quote is that a player can
     * see they are being charged for the size of the ask, and a number that
     * looks the same at 1% and at 20% does not communicate that at a glance.
     */
    private String rateStyle(DebtQuote quote) {

        // Measured against the market's OWN band rather than typed-in numbers,
        // so re-shaping the curve cannot leave this colouring behind. When the
        // spread ran 1%-20% a flat "red above 15%" was about right; the moment
        // the curve was made gentler the same thresholds would have painted
        // ordinary municipal leverage green and nothing else anything at all.
        DebtManager market = game.getDebtManager();
        double floor = market.floorRate();
        double span = Math.max(1e-9, market.ceilingRate() - floor);
        double howFarUp = (quote.marketRate() - floor) / span;

        String colour;
        if (howFarUp >= .55)      colour = "#B00020";   // deep into the expensive half
        else if (howFarUp >= .25) colour = "#C77700";   // getting dear
        else                      colour = "#2E7D32";   // ordinary money
        return "-fx-text-fill: " + colour + "; -fx-font-weight: bold; -fx-padding: 4 0 0 0;";
    }
    
    private void showDebtIssuanceMenu(String type, int minDur, int maxDur, double roundingFactor) {
    clearMenu();
    
    String timeUnit = type.equals("T-Bill") ? "months" : "years";
    Label title = new Label("Issue " + type + "\nSelect Duration (" + timeUnit + "):");
    title.setStyle("-fx-font-weight: bold; -fx-text-alignment: center;");

    // Container for duration buttons
    javafx.scene.layout.FlowPane durationGrid = new javafx.scene.layout.FlowPane(10, 10);
    durationGrid.setAlignment(Pos.CENTER);

    // Create buttons for valid durations
    for (int d = minDur; d <= maxDur; d++) {
        final int duration = d;
        Button durBtn = new Button(String.valueOf(duration));
        durBtn.setPrefWidth(50);
        durBtn.setOnAction(e -> showDebtAmountMenu(type, duration, roundingFactor));
        durationGrid.getChildren().add(durBtn);
    }

    Button back = new Button("Back");
    back.setOnAction(e -> showFinanceMenu());

    rootMenu.getChildren().addAll(title, durationGrid, back);
}
    
    /**
     * Pick an amount, and see what it costs BEFORE agreeing to it.
     *
     * The market prices a loan with the loan itself on the books, so the rate is
     * a function of how much you ask for - ask for twice as much and you are not
     * charged twice as much, you are charged more than twice as much. That was
     * true and invisible: the player added increments blind, hit Confirm, and
     * found out the price on the results screen, one action too late.
     *
     * So every click re-quotes. The terms panel below the buttons is the actual
     * quote from Game.quoteDebt() - the same call the booking makes - and it
     * moves as the number moves.
     *
     * The minus buttons exist for the same reason. When the amount was purely
     * additive an overshoot meant cancelling and starting over, which was merely
     * annoying while every loan cost 1% and is a real cost now that overshooting
     * is what moves the rate.
     */
    private void showDebtAmountMenu(String type, int duration, double rounding) {
    clearMenu();

    final double[] requestedAmount = {0};

    Label heading = new Label(String.format("Issuing %s (%d %s)",
            type, duration, type.equals("T-Bill") ? "months" : "years"));
    heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    Label amountLabel = new Label("Amount Requested: $0");
    amountLabel.setStyle("-fx-font-size: 16px;");

    Label terms = new Label();
    terms.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

    Label impact = new Label();

    Button confirm = new Button("Confirm Issuance");

    // One updater, called by every button, so no control can change the amount
    // without the quote following it.
    Runnable reprice = () -> {
        amountLabel.setText("Amount Requested: $" + formatter.format(requestedAmount[0]));

        if (requestedAmount[0] <= 0) {
            terms.setText("Add an amount to see what it would cost.");
            impact.setText(String.format("Market rate right now: %.2f%%",
                    game.getDebtManager().getRate() * 100));
            impact.setStyle("-fx-text-fill: #555555; -fx-padding: 4 0 0 0;");
            confirm.setDisable(true);
            return;
        }

        DebtQuote quote = game.quoteDebt(type, requestedAmount[0], duration, rounding);
        terms.setText(quote.summary());
        impact.setText(quote.creditImpact());
        impact.setStyle(rateStyle(quote));
        confirm.setDisable(false);
    };

    javafx.scene.layout.FlowPane amountGrid = new javafx.scene.layout.FlowPane(10, 10);
    amountGrid.setAlignment(Pos.CENTER);

    // Increments based on the scale of the instrument (its rounding factor).
    double[] increments = {rounding, rounding * 5, rounding * 10, rounding * 50};

    for (double inc : increments) {
        Button b = new Button("+$" + formatter.format(inc));
        b.setOnAction(e -> {
            requestedAmount[0] += inc;
            reprice.run();
        });
        amountGrid.getChildren().add(b);
    }

    javafx.scene.layout.FlowPane downGrid = new javafx.scene.layout.FlowPane(10, 10);
    downGrid.setAlignment(Pos.CENTER);

    for (double inc : increments) {
        Button b = new Button("-$" + formatter.format(inc));
        b.setOnAction(e -> {
            requestedAmount[0] = Math.max(0, requestedAmount[0] - inc);
            reprice.run();
        });
        downGrid.getChildren().add(b);
    }

    Button reset = new Button("Reset to $0");
    reset.setOnAction(e -> {
        requestedAmount[0] = 0;
        reprice.run();
    });
    downGrid.getChildren().add(reset);

    confirm.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    confirm.setOnAction(e -> {
        if (requestedAmount[0] > 0) {
            // NOTE: the handle*Logic methods already returned a summary of the
            // terms; the UI was discarding it, so the player never saw what they
            // had actually agreed to.
            String summary = executeDebtLogic(type, requestedAmount[0], duration, rounding);
            showDebtResultMenu(summary);
        }
    });

    Button cancel = new Button("Cancel");
    cancel.setOnAction(e -> showFinanceMenu());

    reprice.run();      // so the screen opens with the standing rate on it

    rootMenu.getChildren().addAll(heading, amountLabel, amountGrid, downGrid,
            terms, impact, confirm, cancel);
}
    private String executeDebtLogic(String type, double amount, int duration, double rounding) {
        return switch (type) {
            case "T-Bill" -> game.handleTBillLogic(amount, duration, rounding);
            case "Medium-Term" -> game.handleMediumBondLogic(amount, duration, rounding);
            case "Long-Term" -> game.handleLongBondLogic(amount, duration, rounding);
            default -> "Unknown instrument.";
        };
    }

    /** Shows the terms the player just agreed to. */
    private void showDebtResultMenu(String summary) {
        clearMenu();

        Label title = new Label("ISSUANCE CONFIRMED");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10;");

        Label terms = new Label(summary);
        terms.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        Label rate = new Label(String.format("New market rate: %.2f%%",
                game.getInterestRate() * 100));
        rate.setStyle("-fx-text-fill: #555555; -fx-padding: 10 0 0 0;");

        Button back = new Button("Back to Finance");
        back.setOnAction(e -> showFinanceMenu());

        rootMenu.getChildren().addAll(title, terms, rate, back);
    }
    private void showSectorMenu() {
        clearMenu();

        Label title = new Label("SECTOR OVERVIEW");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        Label gameInfo = new Label("Month: " + game.getMonth() + " | Cash: $" + formatter.format(game.getCash()));

        Button population = new Button("Demographics & Labor Pool");
        Button privateSector = new Button("Private Enterprise Sector");
        // NOTE: "Municipal Utility Services" here was a dead label even in the
        // terminal version - handleSectorMenu()'s case 3 was an empty stub.
        // Utility Services is (and always was) reachable through the Private
        // Enterprise Sector submenu instead, so this button is disabled rather
        // than left silently doing nothing.
        // NOTE: this slot used to read "Municipal Utility Services" and was dead -
        // utilities are reachable through the Private Enterprise submenu. Reused
        // for the construction authority, which had a full report in the terminal
        // build (ConstructionHandler.printConstructionInfo) but no menu entry at all.
        Button construction = new Button("Municipal Construction Authority");
        Button systemOps = new Button("[System Operations]");
        Button back = new Button("Back");

        systemOps.setDisable(true);

        population.setOnAction(e -> showPopulationInfoMenu());
        privateSector.setOnAction(e -> showPrivateSectorMenu());
        construction.setOnAction(e -> showConstructionInfoMenu());
        back.setOnAction(e -> showEconomyMenu());

        rootMenu.getChildren().addAll(title, gameInfo, population, privateSector, construction, systemOps, back);
    }

    private void showPrivateSectorMenu() {
        clearMenu();

        Label title = new Label("PRIVATE ENTERPRISE SECTOR");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        Label gameInfo = new Label("Month: " + game.getMonth() + " | Cash: $" + formatter.format(game.getCash()));

        Button commercial = new Button("Retail & Consumer Services (Commercial)");
        Button industrial = new Button("Resource Production (Industrial)");
        // NOTE: the terminal menu's label for this option said "[Future
        // Expansion]", but its handler actually called printUtilityInfo() - the
        // label was just stale. Relabeled to match what it actually shows.
        Button utility = new Button("Utility Services");
        Button heavy = new Button("Heavy Industry (Steel)");
        Button mining = new Button("Mining (Iron Ore)");
        Button back = new Button("Back");

        commercial.setOnAction(e -> showCommercialInfoMenu());
        industrial.setOnAction(e -> showIndustrialInfoMenu());
        utility.setOnAction(e -> showUtilityInfoMenu());
        heavy.setOnAction(e -> showHeavyIndustryMenu());
        mining.setOnAction(e -> showMiningMenu());

        back.setOnAction(e -> showSectorMenu());

        rootMenu.getChildren().addAll(title, gameInfo, commercial, industrial,
                utility, heavy, mining, back);
    }

    /**
     * The mining books, and the ore market both sides trade on.
     *
     * The market is at the top because it is the only number that matters to
     * either sector: where the ore clears inside its band decides whether the
     * mines or the mills are taking the gain, and a player who cannot see it
     * cannot tell why their steel is suddenly worth building.
     */
    private void showMiningMenu() {
        clearMenu();

        MiningHandler mh = game.getEconomyManager().getMiningHandler();
        IronMarket ore = game.getIronMarket();
        LandManager land = game.getLandManager();

        VBox column = new VBox(0);

        /* ---------------------------- the market ---------------------------- */
        column.getChildren().add(sectionHeading("=== THE ORE MARKET ==="));

        VBox band = reportSection("PRICE",
                String.format("Local ore:              $%.2f /tonne", ore.getLocalPrice() * 1000),
                String.format("  a mine could export at $%.2f - it will not sell below that",
                        ore.getExportPrice() * 1000),
                String.format("  a mill could import scrap at $%.2f - it will not pay above that",
                        ore.getScrapPrice() * 1000),
                "",
                String.format("Mines can lift:         %s tonnes/mo", formatter.format(ore.getSupply())),
                String.format("Mills want:             %s tonnes/mo", formatter.format(ore.getDemand())));

        band.getChildren().add(statusLabel("Who is winning:",
                ore.getPriceIndex() < .5, "THE MILLS", "THE MINES"));
        column.getChildren().add(band);

        /* --------------------------- the ground --------------------------- */
        column.getChildren().add(reportSection("THE GROUND",
                String.format("Deposits owned:         %d", land.getIronDeposits()),
                String.format("Mines on them:          %d", game.minesCommitted()),
                String.format("Ore remaining:          %s tonnes",
                        formatter.format(land.getIronReserveTonnes())),
                String.format("Lifted this month:      %s tonnes",
                        formatter.format(mh.getReportOreLifted())),
                "",
                "Deposits come with land. Buy a parcel with iron under it in",
                "the Land Office, and one deposit supports one mine."));

        if (mh.getCapacityTonnes() <= 0) {
            column.getChildren().add(reportSection("NO MINES YET",
                    "A mine is the biggest employer in the game - 376 jobs, more",
                    "than a food processing plant - and population here is capped",
                    "at jobs times 2.25. One mine is worth about 850 residents to",
                    "a city that can house them.",
                    "",
                    "It pays on its own, exporting ore. It pays considerably",
                    "better next to a steel mill that wants the ore instead."));
            showSectorReport("MINING", column, this::showPrivateSectorMenu);
            return;
        }

        /* --------------------------- the books --------------------------- */
        column.getChildren().add(sectionHeading("=== THE SECTOR ==="));

        column.getChildren().add(reportSection("OUTPUT",
                String.format("Capacity:               %s tonnes/mo",
                        formatter.format(mh.getReportCapacity())),
                String.format("Running at:             %.1f%%", mh.getReportOperatingRate() * 100),
                String.format("Sold to local mills:    %s tonnes",
                        formatter.format(mh.getReportOreSoldLocally())),
                String.format("Exported:               %s tonnes",
                        formatter.format(mh.getReportOreExported()))));

        VBox statement = reportSection("INCOME STATEMENT",
                String.format("Ore Sales:                       $%s",
                        formatter.format(mh.getReportRevenue())),
                String.format("Payroll:                        -$%s",
                        formatter.format(mh.getReportPayroll())),
                String.format("Operating Costs:                -$%s",
                        formatter.format(mh.getReportOperatingCost())),
                "---------------------------------------------------");
        addNetIncomeLine(statement, "NET INCOME:", mh.getReportNetIncome());
        column.getChildren().add(statement);

        if (land.getIronReserveTonnes() <= 0) {
            column.getChildren().add(criticalSection("[CRITICAL] THE ORE IS GONE",
                    "Every deposit the city owns is worked out.",
                    "The mines still draw their payroll and lift nothing."));
        }

        showSectorReport("MINING", column, this::showPrivateSectorMenu);
    }

    /**
     * The steel books.
     *
     * Laid out so the conversion margin is the first thing visible, because it
     * is the whole business: everything below it - wages, power, water, interest,
     * tax - has to fit inside the gap between what scrap costs and what steel
     * fetches, and it deliberately only just does.
     */
    private void showHeavyIndustryMenu() {
        clearMenu();

        HeavyIndustryHandler hi = game.getEconomyManager().getHeavyIndustryHandler();
        BalanceSheet bs = hi.getBalanceSheet();
        BusinessDebtManager credit = game.getEconomyManager().getBusinessDebtManager();

        VBox column = new VBox(0);

        if (hi.getOutputCapacity() <= 0) {
            column.getChildren().add(reportSection("NO MILLS BUILT",
                    "A steel mill buys scrap abroad and ships steel abroad. It",
                    "will never make much money - the margin is a few percent in",
                    "a good month and negative in a bad one.",
                    "",
                    "Build one anyway. A mini-mill employs 130 people, those",
                    "wages are spent in the city's shops and taxed, and the",
                    "people who earn them need housing. That is the return."));
            showSectorReport("HEAVY INDUSTRY", column, this::showPrivateSectorMenu);
            return;
        }

        /* --------------------------- the plant --------------------------- */
        VBox plant = reportSection("THE MILLS",
                String.format("Capacity:               %s tonnes/month",
                        formatter.format(hi.getOutputCapacity())),
                String.format("Produced:               %s tonnes",
                        formatter.format(hi.getReportOutput())),
                String.format("Jobs:                   %,d", hi.getTotalJobs()));

        double rate = hi.getReportOperatingRate();
        Label running = monoLabel(String.format("%-24s%.1f%%", "Running at:", rate * 100));
        running.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: "
                + (rate < .9 ? "#c62828" : "#2e7d32") + ";");
        plant.getChildren().add(running);

        if (rate < .9) {
            plant.getChildren().add(monoLabel(
                    "  short of staff or power - the bills do not fall with output"));
        }
        column.getChildren().add(plant);

        /* --------------------------- the margin --------------------------- */
        VBox margin = reportSection("THE WORLD MARKET",
                String.format("Steel sells for:        $%s /tonne",
                        formatter.format(hi.getExportPrice())),
                String.format("Scrap costs:            $%s /tonne",
                        formatter.format(hi.getImportPrice())),
                String.format("Scrap per tonne:        %.2f tonnes",
                        hi.getOutputCapacity() > 0
                                ? hi.getInputTonnes() / hi.getOutputCapacity() : 0));

        double conversion = hi.getConversionMargin();
        Label marginLine = monoLabel(String.format("%-24s$%s /tonne",
                "Conversion margin:", formatter.format(conversion)));
        marginLine.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: "
                + (conversion <= 0 ? "#c62828" : "#2e7d32") + ";");
        margin.getChildren().add(marginLine);
        margin.getChildren().add(monoLabel(
                "  wages, power and water all come out of this"));
        column.getChildren().add(margin);

        /* ----------------------- income statement ----------------------- */
        column.getChildren().add(sectionHeading("=== INCOME STATEMENT (month just closed) ==="));

        column.getChildren().add(reportSection("EXPORT REVENUE",
                String.format("Steel shipped:          $%s",
                        formatter.format(hi.getReportRevenue()))));

        column.getChildren().add(reportSection("COSTS",
                String.format("Scrap imported:        -$%s", formatter.format(hi.getReportInputCost())),
                String.format("Payroll:               -$%s", formatter.format(hi.getReportPayroll())),
                String.format("Electricity:           -$%s", formatter.format(hi.getReportElectricityCost())),
                String.format("Water:                 -$%s", formatter.format(hi.getReportWaterCost())),
                "---------------------------------------------------",
                String.format("Total Costs:           -$%s", formatter.format(hi.getReportOperatingCost()))));

        VBox result = reportSection("RESULT");
        addNetIncomeLine(result, "OPERATING INCOME:", hi.getReportOperatingIncome());
        result.getChildren().add(monoLabel(String.format("%-32s-$%s",
                "Interest:", formatter.format(hi.getReportInterestExpense()))));
        result.getChildren().add(monoLabel(String.format("%-32s-$%s",
                "Property Tax:", formatter.format(hi.getReportPropertyTaxExpense()))));
        addNetIncomeLine(result, "NET INCOME:", hi.getReportNetIncome());
        column.getChildren().add(result);

        /* ------------------- what it is actually worth ------------------- */
        VBox worth = reportSection("WHAT THE CITY GETS");

        // The honest accounting: the mill's own profit is nearly nothing, and
        // the payroll it pays out is the actual point of the building.
        double wageTax = hi.getReportPayroll() * game.getEconomyManager().getTaxRate();

        worth.getChildren().add(monoLabel(String.format("%-32s$%s",
                "Wages paid into the city:", formatter.format(hi.getReportPayroll()))));
        worth.getChildren().add(monoLabel(String.format("%-32s$%s",
                String.format("Wage tax on them @ %.0f%%:",
                        game.getEconomyManager().getTaxRate() * 100),
                formatter.format(wageTax))));
        worth.getChildren().add(monoLabel(String.format("%-32s%,d",
                "Residents supported:", (int) (hi.getTotalJobs() * 2.25))));
        worth.getChildren().add(monoLabel(
                "  those wages are spent in the shops and taxed again"));
        column.getChildren().add(worth);

        /* --------------------------- balance sheet --------------------------- */
        column.getChildren().add(sectionHeading("=== BALANCE SHEET ==="));

        column.getChildren().add(reportSection("ASSETS",
                String.format("Cash:                   $%s", formatter.format(bs.getCash())),
                String.format("Land:                   $%s", formatter.format(bs.getLand())),
                String.format("Plant:                  $%s", formatter.format(bs.getBuildings())),
                "---------------------------------------------------",
                String.format("Total Assets:           $%s", formatter.format(bs.getTotalAssets()))));

        column.getChildren().add(reportSection("LIABILITIES & EQUITY",
                String.format("Loans Outstanding:      $%s", formatter.format(bs.getBondsPayable())),
                String.format("Equity:                 $%s", formatter.format(bs.getEquity())),
                "---------------------------------------------------",
                String.format("Total:                  $%s",
                        formatter.format(bs.getTotalLiabilitiesAndEquity())),
                "",
                String.format("Borrowing Rate:         %.2f%%",
                        credit.getRate(BusinessDebtManager.HEAVY_INDUSTRY) * 100),
                String.format("Leverage:               %.2f",
                        credit.getLeverage(BusinessDebtManager.HEAVY_INDUSTRY))));

        if (credit.isBorrowingBlocked(BusinessDebtManager.HEAVY_INDUSTRY)) {
            VBox distress = reportSection("IN DEFAULT");
            Label line = monoLabel("  cannot borrow for "
                    + credit.getBlockedMonths(BusinessDebtManager.HEAVY_INDUSTRY)
                    + " more months");
            line.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #c62828;");
            distress.getChildren().add(line);
            column.getChildren().add(distress);
        }

        showSectorReport("HEAVY INDUSTRY", column, this::showPrivateSectorMenu);
    }

    private void showPopulationInfoMenu() {
        clearMenu();

        Label title = new Label("DEMOGRAPHIC & LABOR REPORT");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        PopulationManager pm = game.getPopulationManager();

        int population = pm.getPopulation();
        double adultPercent = pm.getAdultPercent();
        int workforce = pm.getWorkforce();
        int totalJobs = pm.getTotalJobs();
        int[] vacancies = pm.getJobVacancy();
        double[] fillRates = pm.getJobFillRate();
        int[] jobs = pm.getJobs();
        double[] jobWage = pm.getWagesPerType();

        int totalVacancies = 0;
        for (int v : vacancies) {
            totalVacancies += v;
        }

        VBox overview = new VBox(4);
        overview.setAlignment(Pos.CENTER);
        overview.getChildren().addAll(
                new Label("POPULATION OVERVIEW"),
                new Label(String.format("Total Population:      %,d citizens", population)),
                new Label(String.format("Workforce Share:        %.1f%%", adultPercent * 100)),
                new Label(String.format("Total Workforce:        %,d workers", workforce))
        );

        VBox laborSummary = new VBox(4);
        laborSummary.setAlignment(Pos.CENTER);
        laborSummary.setStyle("-fx-padding: 15 0 0 0;");
        laborSummary.getChildren().addAll(
                new Label("LABOR MARKET SUMMARY"),
                new Label(String.format("Total Jobs Available:   %,d positions", totalJobs)),
                new Label(String.format("Total Vacancies:        %,d positions", totalVacancies))
        );

        // Job distribution table
        VBox jobTable = new VBox(3);
        jobTable.setAlignment(Pos.CENTER);
        jobTable.setStyle("-fx-padding: 15 0 0 0;");

        Label header = new Label(String.format("%-16s | %-8s | %-9s | %-9s | %-14s",
                "Job Type", "Jobs", "Vacancies", "Fill Rate", "Payroll"));
        header.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold;");
        jobTable.getChildren().add(header);

        JobType[] jobTypes = JobType.values();
        for (int i = 0; i < jobs.length; i++) {
            if (jobs[i] > 0) {
                double payroll = jobWage[i] * jobs[i];
                Label row = new Label(String.format("%-16s | %,8d | %,9d | %8.1f%% | $%-13s",
                        jobTypes[i].name(), jobs[i], vacancies[i], fillRates[i] * 100,
                        formatter.format(payroll)));
                row.setStyle("-fx-font-family: 'Courier New';");
                jobTable.getChildren().add(row);
            }
        }

        VBox status = new VBox(4);
        status.setAlignment(Pos.CENTER);
        status.setStyle("-fx-padding: 15; -fx-border-color: black; -fx-border-width: 1 0 0 0;");

        if (workforce > totalJobs) {
            Label statusLabel = new Label("STATUS: LABOR SURPLUS");
            statusLabel.setStyle("-fx-font-weight: bold;");
            status.getChildren().addAll(
                    statusLabel,
                    new Label(String.format("%,d citizens are currently seeking employment.", workforce - totalJobs))
            );
        } else if (totalVacancies > 0) {
            Label statusLabel = new Label("WARNING: LABOR SHORTAGE");
            statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
            status.getChildren().addAll(
                    statusLabel,
                    new Label(String.format("%,d positions across the city remain unfilled.", totalVacancies))
            );
        }

        VBox content = new VBox(0);
        content.getChildren().addAll(overview, laborSummary, jobTable, status);

        Button cashflow = new Button("Household Cash Flow");
        cashflow.setOnAction(e -> showHouseholdMenu());

        Button back = new Button("Back");
        back.setOnAction(e -> showSectorMenu());

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle("-fx-background-color:transparent;");

        rootMenu.getChildren().addAll(title, scrollPane, cashflow, back);
    }

    /**
     * The residents' own books - the last participant in this economy that did
     * not have any.
     *
     * The line that matters is the bottom one. Retail spending is currently
     * driven by how many people there are rather than by what they earn, so
     * nothing in the model stops households being made to spend more than they
     * take home. If that happens it is money arriving from nowhere, and this is
     * the screen where it becomes visible instead of invisible.
     */
    private void showHouseholdMenu() {
        clearMenu();

        HouseholdAccounts hh = game.getHouseholds();

        // No title/header built here: showSectorReport() at the bottom supplies
        // both, the same as every other sector screen.
        VBox column = new VBox(0);

        column.getChildren().add(reportSection("INCOME",
                String.format("Wages earned:           $%s", formatter.format(hh.getWages())),
                String.format("Wage tax @ %.0f%%:        -$%s",
                        hh.getEffectiveTaxRate() * 100, formatter.format(hh.getWageTax())),
                "---------------------------------------------------",
                String.format("Take-home pay:          $%s",
                        formatter.format(hh.getDisposableIncome()))));

        column.getChildren().add(reportSection("OUTGOINGS",
                String.format("Rent to landlords:     -$%s", formatter.format(hh.getRent())),
                String.format("Spent in the shops:    -$%s", formatter.format(hh.getShopping())),
                "---------------------------------------------------",
                String.format("Total spending:        -$%s", formatter.format(hh.getSpending()))));

        VBox result = reportSection("WHAT IS LEFT");
        addNetIncomeLine(result, hh.isLivingBeyondIncome() ? "SHORTFALL:" : "SAVED THIS MONTH:",
                hh.getNetSaving());

        Label rate = monoLabel(String.format("%-32s%.1f%%", "Saving rate:",
                hh.getSavingRate() * 100));
        rate.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: "
                + (hh.getSavingRate() < 0 ? "#c62828"
                        : hh.getSavingRate() < .03 ? "#ef6c00" : "#2e7d32") + ";");
        result.getChildren().add(rate);

        if (hh.isLivingBeyondIncome()) {
            Label warn = monoLabel("  the people are spending more than they earn");
            warn.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #c62828;");
            result.getChildren().add(warn);
            result.getChildren().add(monoLabel(
                    "  nothing in the model funds this - it is money from nowhere"));
        }

        result.getChildren().add(monoLabel(String.format("%-32s$%s",
                "Saved since founding:", formatter.format(hh.getCumulativeSaving()))));
        result.getChildren().add(monoLabel(
                "  a record, not a pot - nobody can spend it"));
        column.getChildren().add(result);

        /* ---------------------------- affordability ---------------------------- */
        VBox afford = reportSection("AFFORDABILITY");

        double burden = hh.getRentBurden();
        Label rentLine = monoLabel(String.format("%-32s%.1f%% of take-home",
                "Rent:", burden * 100));
        rentLine.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: "
                + (burden > .35 ? "#c62828" : burden > .25 ? "#ef6c00" : "#2e7d32") + ";");
        afford.getChildren().add(rentLine);

        if (burden > .35) {
            afford.getChildren().add(monoLabel("  over a third of income - rent-burdened"));
        }

        afford.getChildren().add(monoLabel(String.format("%-32s$%s",
                "Income per resident:", formatter.format(hh.getIncomePerResident()))));
        afford.getChildren().add(monoLabel(String.format("%-32s$%s",
                "Spending per resident:", formatter.format(hh.getSpendingPerResident()))));
        afford.getChildren().add(monoLabel(String.format("%-32s$%s",
                "Average filled job pays:", formatter.format(hh.getAverageWage()))));
        column.getChildren().add(afford);

        /* ------------------------------- who works ------------------------------- */
        column.getChildren().add(reportSection("WHO IS EARNING IT",
                String.format("Population:             %,d", hh.getPopulation()),
                String.format("Workforce:              %,d", hh.getWorkforce()),
                String.format("Jobs actually filled:   %,d", hh.getJobsFilled()),
                String.format("People per worker:      %.2f", hh.getDependencyRatio()),
                "",
                "one household for now; income groups come much later"));

        showSectorReport("HOUSEHOLD CASH FLOW", column, this::showPopulationInfoMenu);
    }

    /* ---------------------------------------------------------------------
       Small helpers so the report screens stay readable. Shared by the sector
       screens - Industrial and Utility can reuse these when they get ported.
       --------------------------------------------------------------------- */

    private Label monoLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family: 'Courier New';");
        return label;
    }

    private VBox reportSection(String heading, String... rows) {
        VBox box = new VBox(3);
        // CENTER_LEFT, not CENTER: each row used to be centred individually, which
        // threw away the %-24s padding and left the value column ragged. Left-
        // aligning inside a centred fixed-width column makes the padding line up.
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-padding: 14 0 0 0;");

        /*
         * ...and hug the content, so the box can be centred by whatever holds it.
         *
         * A VBox child stretches to its parent's full width by default. Combined
         * with CENTER_LEFT above, that put every row hard against the left edge
         * of whatever pane the section landed in - invisible inside the report
         * columns, which are pref-sized already, and glaring on the construction
         * shedding banner, which is added straight to the centred root menu. It
         * read as detached from the menu because it was the only thing on the
         * screen not lining up with it.
         *
         * Fixed here rather than at the banner so any other section added
         * directly to a pane gets it too. Sections inside a pref-sized column are
         * unaffected: they were already only as wide as their widest row.
         */
        box.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);

        Label headingLabel = new Label(heading);
        headingLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold;");
        box.getChildren().add(headingLabel);

        for (String row : rows) {
            box.getChildren().add(monoLabel(row));
        }

        return box;
    }

    /**
     * JavaFX port of CommercialHandler.printCommercialInfo().
     *
     * NOTE: this screen is a pure reader. The monthly income statement is
     * calculated once per month by CommercialHandler.calculateCommercialResults()
     * (driven from Game.startOfMonthUpdate()), so opening this screen any number
     * of times has no effect on the economy. Wiring the button straight to the
     * old printer would have banked another month of net income on every click.
     */
    private void showCommercialInfoMenu() {
        clearMenu();

        CommercialHandler ch = game.getEconomyManager().getCommercialHandler();

        Label title = new Label("COMMERCIAL SECTOR REPORT");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        Label gameInfo = new Label("Month: " + game.getMonth()
                + " | Cash: $" + formatter.format(game.getCash()));

        /* ---------------- RETAIL / COMMERCIAL COMPANY ---------------- */

        VBox market = reportSection("RETAIL OPERATIONS - MARKET OVERVIEW",
                String.format("City Population:        %,d people", ch.getReportPopulation()),
                String.format("Store Market Coverage:  %,d customers", ch.getReportStoreCoverage()),
                String.format("Store Capacity:         %,d units", ch.getReportStoreCapacity()),
                String.format("Current Inventory:      %,d units", ch.getReportStoreInventory()));

        VBox utilization = reportSection("RESOURCE UTILIZATION",
                String.format("Labor Fill Rate:        %.1f%%", ch.getReportAverageStoreFill() * 100),
                String.format("Energy Efficiency:      %.1f%%", ch.getReportEnergyRatio() * 100),
                String.format("Water Efficiency:       %.1f%%", ch.getReportWaterRatio() * 100));

        VBox sales = reportSection("SALES PERFORMANCE",
                String.format("Market Demand:          %,d units", ch.getReportDemand()),
                String.format("Units Sold:             %,d units", ch.getReportProductsSold()),
                String.format("Average Sell Price:     $%s per unit", formatter.format(ch.getStoreSellPrice())),
                String.format("Gross Revenue:          $%s", formatter.format(ch.getGrossRevenue())));

        VBox retailStatement = reportSection("INCOME STATEMENT (RETAIL COMPANY)",
                String.format("Retail Sales Revenue:            $%s", formatter.format(ch.getGrossRevenue())),
                "",
                String.format("Payroll Expense:                -$%s", formatter.format(ch.getReportPayroll())),
                String.format("Inventory Procurement:          -$%s", formatter.format(ch.getReportInventoryCost())),
                String.format("    Local @ $%s:              %,d units",
                        formatter.format(game.getEconomyManager().getFoodMarket().getLocalPrice()),
                        ch.getReportLocalImports()),
                String.format("    Imported @ $%s:           %,d units",
                        formatter.format(ch.getImportPrice()), ch.getReportGlobalImports()),
                String.format("Electricity Expense:            -$%s", formatter.format(ch.getReportElectricityCost())),
                String.format("Water Expense:                  -$%s", formatter.format(ch.getReportWaterCost())),
                String.format("Interest Expense:               -$%s", formatter.format(ch.getReportRetailInterest())),
                "---------------------------------------------------",
                String.format("Total Operating Expenses:       -$%s", formatter.format(ch.getReportRetailOperatingCost())));

        Label retailNet = monoLabel(String.format("NET INCOME (RETAIL):             $%s",
                formatter.format(ch.getReportRetailNetIncome())));
        retailNet.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: "
                + (ch.getReportRetailNetIncome() < 0 ? "#c62828" : "#2e7d32") + ";");
        retailStatement.getChildren().add(retailNet);

        /* ---------------- REAL ESTATE COMPANY ---------------- */

        VBox property = reportSection("REAL ESTATE OPERATIONS - PROPERTY OVERVIEW",
                String.format("Total Housing Units:    %,d units", ch.getReportHousehold()),
                String.format("Occupied Units:         %,d units", ch.getReportOccupiedUnits()),
                String.format("Vacant Units:           %,d units", ch.getReportVacantUnits()));

        VBox realEstateStatement = reportSection("INCOME STATEMENT (REAL ESTATE COMPANY)",
                String.format("Rental Income:                   $%s", formatter.format(ch.getReportRentIncome())),
                "",
                String.format("Property Maintenance:           -$%s", formatter.format(ch.getReportPropertyMaintenance())),
                String.format("Property Tax Expense:           -$%s", formatter.format(ch.getReportPropertyTaxExpense())),
                String.format("Interest Expense:               -$%s", formatter.format(ch.getReportRealEstateInterest())),
                "---------------------------------------------------",
                String.format("Total Operating Expenses:       -$%s", formatter.format(ch.getReportRealEstateExpenses())));

        Label realEstateNet = monoLabel(String.format("NET INCOME (REAL ESTATE):        $%s",
                formatter.format(ch.getReportRealEstateNetIncome())));
        realEstateNet.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: "
                + (ch.getReportRealEstateNetIncome() < 0 ? "#c62828" : "#2e7d32") + ";");
        realEstateStatement.getChildren().add(realEstateNet);

        /* ---------------- CONSOLIDATED ---------------- */

        VBox consolidated = new VBox(4);
        consolidated.setAlignment(Pos.CENTER_LEFT);
        consolidated.setStyle("-fx-padding: 15 0 0 0; -fx-border-color: black; -fx-border-width: 1 0 0 0;");

        Label consolidatedHeading = new Label("CONSOLIDATED SUMMARY");
        consolidatedHeading.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold;");

        Label totalNet = monoLabel(String.format("TOTAL NET INCOME:                $%s",
                formatter.format(ch.getReportTotalNetIncome())));
        totalNet.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: "
                + (ch.getReportTotalNetIncome() < 0 ? "#c62828" : "#2e7d32") + ";");

        consolidated.getChildren().addAll(
                consolidatedHeading,
                monoLabel(String.format("Retail Net Income:               $%s", formatter.format(ch.getReportRetailNetIncome()))),
                monoLabel(String.format("Real Estate Net Income:          $%s", formatter.format(ch.getReportRealEstateNetIncome()))),
                monoLabel("---------------------------------------------------"),
                totalNet,
                monoLabel(String.format("Tax Revenue @ %.1f%%:             $%s",
                        ch.getReportTaxRate() * 100, formatter.format(ch.getReportTotalTax()))),
                monoLabel(""),
                monoLabel(String.format("Retail Cash Reserves:            $%s", formatter.format(ch.getCommercialCash()))),
                monoLabel(String.format("Real Estate Cash Reserves:       $%s", formatter.format(ch.getRealEstateCash()))));

        /* ---------------- ASSEMBLE ---------------- */

        // One left-aligned column, centred as a unit. Every section stretches to the
        // column's width (VBox fills its children by default), so all rows share a
        // single left edge and the monospace padding actually aligns the columns.
        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        column.getChildren().addAll(
                market,
                utilization,
                sales,
                retailStatement,
                property,
                realEstateStatement,
                consolidated);

        VBox content = new VBox(0);
        content.setAlignment(Pos.CENTER);
        content.getChildren().add(column);

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setStyle("-fx-background-color:transparent;");

        Button financials = new Button("Financial Statements");
        financials.setOnAction(e -> showCommercialFinancialsMenu());

        Button back = new Button("Back");
        back.setOnAction(e -> showPrivateSectorMenu());

        rootMenu.getChildren().addAll(title, gameInfo, scrollPane, financials, back);
    }

    /**
     * Retail and real estate side by side: balance sheet and credit position for
     * each. They share a screen because they share the commercial report, and
     * because the contrast is the point - real estate owns the housing stock and
     * is the richest business in the city, retail runs on inventory and a thin
     * margin.
     */
    private void showCommercialFinancialsMenu() {
        clearMenu();

        CommercialHandler ch = game.getEconomyManager().getCommercialHandler();
        BusinessDebtManager credit = game.getEconomyManager().getBusinessDebtManager();

        VBox column = new VBox(0);

        column.getChildren().add(sectionHeading("=== RETAIL ==="));
        addBalanceSheet(column, ch.getRetailBalanceSheet());
        addCreditBlock(column, credit, BusinessDebtManager.RETAIL,
                ch.getReportRetailInterest(), ch.getReportRetailNetIncome());

        column.getChildren().add(sectionHeading("=== REAL ESTATE ==="));
        addBalanceSheet(column, ch.getRealEstateBalanceSheet());
        addCreditBlock(column, credit, BusinessDebtManager.REAL_ESTATE,
                ch.getReportRealEstateInterest(), ch.getReportRealEstateNetIncome());

        showSectorReport("COMMERCIAL - FINANCIAL STATEMENTS", column, this::showCommercialInfoMenu);
    }

    /**
     * One balance sheet, laid out the same way wherever it appears. Extracted so
     * the three sectors cannot drift apart in presentation.
     */
    private void addBalanceSheet(VBox column, BalanceSheet bs) {

        column.getChildren().add(reportSection("CURRENT ASSETS",
                String.format("Cash:                            $%s", formatter.format(bs.getCash())),
                String.format("Inventory:                       $%s", formatter.format(bs.getInventory())),
                bs.getInventoryUnits() > 0
                        ? String.format("  %,d units @ $%s (market)", bs.getInventoryUnits(),
                                formatter.format(bs.getInventoryUnitPrice()))
                        : "",
                "---------------------------------------------------",
                String.format("Total Current Assets:            $%s", formatter.format(bs.getCurrentAssets()))));

        column.getChildren().add(reportSection("NON-CURRENT ASSETS",
                String.format("Land:                            $%s", formatter.format(bs.getLand())),
                String.format("Buildings, at cost:              $%s", formatter.format(bs.getBuildings())),
                "---------------------------------------------------",
                String.format("Total Non-Current Assets:        $%s", formatter.format(bs.getNonCurrentAssets()))));

        VBox assets = reportSection("");
        Label totalAssets = monoLabel(String.format("%-32s $%s", "TOTAL ASSETS:",
                formatter.format(bs.getTotalAssets())));
        totalAssets.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        assets.getChildren().add(totalAssets);
        column.getChildren().add(assets);

        column.getChildren().add(reportSection("LIABILITIES & EQUITY",
                String.format("Loans Payable:                   $%s", formatter.format(bs.getBondsPayable())),
                String.format("Total Liabilities:               $%s", formatter.format(bs.getTotalLiabilities())),
                "",
                String.format("Owner's Equity:                  $%s", formatter.format(bs.getEquity())),
                "  (balancing figure: assets less liabilities)",
                "---------------------------------------------------",
                String.format("TOTAL LIABILITIES + EQUITY:      $%s",
                        formatter.format(bs.getTotalLiabilitiesAndEquity()))));
    }

    /**
     * A sector's credit standing: what it owes, what it is paying, and what the
     * next loan would cost it.
     *
     * The rate is broken into government + spread on purpose. The government
     * rate is the risk-free floor everyone borrows above, and the spread is the
     * part this sector earned for itself - so a rate that jumps because city
     * debt rose reads differently from one that jumps because the business
     * levered up, and the screen should say which.
     */
    private void addCreditBlock(VBox column, BusinessDebtManager credit, String sector,
                                double interestExpense, double netIncome) {

        double principal = credit.getPrincipal(sector);

        VBox box = reportSection("CREDIT",
                String.format("Outstanding Principal:  $%s", formatter.format(principal)),
                String.format("Loans Outstanding:      %,d", credit.getLoanCount(sector)),
                String.format("Interest This Month:    $%s", formatter.format(interestExpense)),
                "",
                String.format("Government Rate:        %.2f%%", credit.getRiskFreeRate() * 100),
                String.format("Credit Spread:          %.2f%%", credit.getSpread(sector) * 100),
                String.format("New Borrowing Rate:     %.2f%%", credit.getRate(sector) * 100),
                String.format("Rate on Existing Debt:  %.2f%%", credit.getEffectiveRate(sector) * 100),
                String.format("Leverage (debt/assets): %.2f", credit.getLeverage(sector)));

        if (credit.getSpread(sector) >= .0799) {
            Label maxed = monoLabel("Credit spread is at its ceiling.");
            maxed.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px; -fx-text-fill: #c62828;");
            box.getChildren().add(maxed);
        }

        if (principal > 0 && netIncome < 0) {
            Label spiral = monoLabel("Losing money while servicing debt - borrowing again next month.");
            spiral.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px; -fx-text-fill: #c62828;");
            box.getChildren().add(spiral);
        }

        column.getChildren().add(box);
    }

    /* =====================================================================
       SIMULATE MULTIPLE MONTHS

       The terminal version asked "How many months?" and read the answer from
       getInput(), which is stubbed to return 0 - so the loop never ran. This is
       the same increment-button pattern the build and debt screens use.
       ===================================================================== */

    private void showSimulateMonthsMenu() {
        clearMenu();

        final int[] months = {0};

        Label title = new Label("SIMULATE MULTIPLE MONTHS");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10;");

        Label info = new Label("Month " + game.getMonth()
                + " | Cash: $" + formatter.format(game.getCash()));

        Label totalLabel = new Label("Months to simulate: 0");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label note = new Label("Reports and graphs are muted while fast-forwarding.");
        note.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

        javafx.scene.layout.FlowPane grid = new javafx.scene.layout.FlowPane(10, 10);
        grid.setAlignment(Pos.CENTER);
        grid.setPrefWrapLength(320);

        int[] increments = {1, 5, 10, 25, 50, 100};
        for (int amount : increments) {
            final int step = amount;
            Button button = new Button("+" + amount);
            button.setPrefWidth(60);
            button.setOnAction(e -> {
                months[0] += step;
                totalLabel.setText("Months to simulate: " + months[0]);
            });
            grid.getChildren().add(button);
        }

        Button reset = new Button("Reset");
        reset.setOnAction(e -> {
            months[0] = 0;
            totalLabel.setText("Months to simulate: 0");
        });

        Button run = new Button("Run");
        run.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        run.setOnAction(e -> {
            if (months[0] > 0) {
                int completed = game.simulateMonths(months[0]);
                showSimulateResultMenu(months[0], completed);
            }
        });

        Button back = new Button("Cancel");
        back.setOnAction(e -> showStartMenu());

        rootMenu.getChildren().addAll(title, info, totalLabel, grid, note, reset, run, back);
    }

    /**
     * What happened while the player was not watching.
     *
     * Fast-forwarding is how this game is actually played, and this screen used
     * to say "100 of 100 months simulated" and nothing else. Anything that
     * reports itself and then expires - the demolition log keeps entries for
     * two years - could happen and vanish entirely inside one skip, which is
     * exactly how a demolition went unnoticed through several test runs.
     *
     * Ordered worst-first: what went wrong, then what changed, then the detail.
     */
    private void showSimulateResultMenu(int requested, int completed) {
        clearMenu();

        TimeSkipReport skip = game.getSkipReport();

        Label title = new Label("SIMULATION COMPLETE");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10;");

        Label result = new Label(String.format("Month %,d to %,d  -  %d month%s",
                skip.getStartMonth(), game.getMonth(),
                completed, completed == 1 ? "" : "s"));
        result.setStyle("-fx-font-size: 14px;");

        VBox column = new VBox(0);

        /*
         * If the simulation itself broke, say so first and say where to look.
         *
         * Before this the window simply stopped responding partway through a
         * skip: the exception reached the FX thread's default handler and a
         * stderr that does not exist in a packaged build, so the player got a
         * month counter that stopped and no explanation at all.
         */
        String failure = game.takeSkipFailure();
        if (failure != null) {
            VBox problem = reportSection("SOMETHING WENT WRONG");
            for (String line : failure.split("\n")) {
                Label item = monoLabel("  " + line);
                item.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #c62828;");
                problem.getChildren().add(item);
            }
            Label advice = monoLabel("  The months that did run are real. "
                    + "Save or reload before continuing.");
            advice.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #555555;");
            problem.getChildren().add(advice);
            column.getChildren().add(problem);
        }

        /* ---------------------------- headlines ---------------------------- */
        VBox headlines = reportSection("WHAT HAPPENED");

        for (String line : skip.getHeadlines()) {

            // The only cheerful headline is the "nothing went wrong" one, which
            // is the single line the list contains when it contains nothing else.
            boolean good = line.startsWith("Nothing went wrong");

            Label item = monoLabel("  " + line);
            item.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: "
                    + (good ? "#2e7d32" : "#c62828") + ";");
            headlines.getChildren().add(item);
        }
        column.getChildren().add(headlines);

        /* ------------------------------ deltas ------------------------------ */
        VBox changes = reportSection("THE CITY");
        addSkipLine(changes, "Population", skip.getStartPopulation(), skip.getEndPopulation(),
                skip.getPopulationChange(), false);
        addSkipLine(changes, "Cash", skip.getStartCash(), skip.getEndCash(),
                skip.getCashChange(), true);
        changes.getChildren().add(monoLabel(String.format("%-20s%+,.1f a month",
                "", skip.getCashPerMonth())));

        // Only when there was something to grow FROM. A city that went 0 -> 192
        // has no meaningful rate, and printing "+0.0% a year" beside "+192"
        // reads like a contradiction rather than a division guard.
        if (skip.getStartPopulation() > 0 && skip.getPopulationChange() != 0) {
            changes.getChildren().add(monoLabel(String.format("%-20s%+.1f%% a year",
                    "Growth", skip.getPopulationGrowthRate() * 100)));
        }
        column.getChildren().add(changes);

        VBox econ = reportSection("ECONOMY");
        addChangeLine(econ, "Monthly GDP", skip.getMonthlyGdpChange(), true, true);
        addChangeLine(econ, "Jobs", skip.getJobsChange(), false, true);
        addChangeLine(econ, "Housing", skip.getHousingChange(), false, true);

        // Debt is the one place where up is bad. Negating the VALUE to get the
        // colour right would print "-$48,074" on a city whose debt rose by
        // exactly that much, so the sign stays honest and only the colour flips.
        addChangeLine(econ, "City debt", skip.getCityDebtChange(), true, false);
        addChangeLine(econ, "Business debt", skip.getBusinessDebtChange(), true, false);

        if (skip.getWriteOffsDuringSkip() > 0) {
            Label wo = monoLabel(String.format("%-20s%s written off by lenders",
                    "Restructuring", money(skip.getWriteOffsDuringSkip())));
            wo.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #ef6c00;");
            econ.getChildren().add(wo);
        }
        column.getChildren().add(econ);

        /* ------------------------------- land ------------------------------- */
        VBox land = reportSection("LAND",
                String.format("%-20s%+.0f blocks", "Bought", skip.getLandBlocksBought()),
                String.format("%-20s%.1f%% used at the end",
                        "Utilisation", skip.getEndLandUtilisation() * 100));
        column.getChildren().add(land);

        /* ----------------------------- buildings ----------------------------- */
        java.util.List<TimeSkipReport.BuildingChange> built = skip.getBuildingChanges();

        VBox buildings = reportSection("BUILDINGS");
        if (built.isEmpty()) {
            buildings.getChildren().add(monoLabel("  nothing was built or lost"));
        } else {
            for (TimeSkipReport.BuildingChange change : built) {
                Label line = monoLabel(String.format("  %+d  %s", change.change, change.name));
                line.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: "
                        + (change.isGain() ? "#2e7d32" : "#c62828") + ";");
                buildings.getChildren().add(line);
            }
        }
        column.getChildren().add(buildings);

        /* ---------------------------- demolitions ---------------------------- */
        // Pulled from the log rather than the snapshots, because only the log
        // knows WHEN each one happened - and a hundred-month skip is long enough
        // that they would otherwise have aged off the side panel unseen.
        java.util.List<DemolitionLog.Entry> lost = new java.util.ArrayList<>();
        for (DemolitionLog.Entry entry : game.getDemolitionLog().all()) {
            if (entry.month > skip.getStartMonth()) {
                lost.add(entry);
            }
        }

        if (!lost.isEmpty()) {
            VBox demolished = reportSection("DEMOLISHED DURING THE SKIP");
            for (DemolitionLog.Entry entry : lost) {
                Label line = monoLabel(String.format("  month %,d: %,d x %s (%s)",
                        entry.month, entry.quantity, entry.building, entry.sector));
                line.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #c62828;");
                demolished.getChildren().add(line);
            }
            demolished.getChildren().add(monoLabel(
                    "  their owners could not afford to keep them"));
            column.getChildren().add(demolished);
        }

        /* ---------------------------- households ---------------------------- */
        column.getChildren().add(reportSection("HOUSEHOLDS",
                String.format("%-20s%.1f%% of take-home", "Rent",
                        skip.getEndRentBurden() * 100),
                String.format("%-20s%.1f%%  (was %.1f%%)", "Saving rate",
                        skip.getEndSavingRate() * 100, skip.getStartSavingRate() * 100)));

        VBox content = new VBox(0);
        content.setAlignment(Pos.CENTER);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        content.getChildren().add(column);

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(520);
        scroll.setStyle("-fx-background-color:transparent;");

        Button again = new Button("Simulate more");
        again.setOnAction(e -> showSimulateMonthsMenu());

        Button done = new Button("Done");
        done.setOnAction(e -> showStartMenu());

        rootMenu.getChildren().addAll(title, result, scroll, again, done);
    }

    /** "Population  192 -> 664  (+472)", coloured by direction. */
    private void addSkipLine(VBox section, String label,
                             double start, double end, double change, boolean isMoney) {

        String text = isMoney
                ? String.format("%-20s%s -> %s", label, money(start), money(end))
                : String.format("%-20s%,.0f -> %,.0f", label, start, end);

        section.getChildren().add(monoLabel(text));
        addChangeLine(section, "", change, isMoney, true);
    }

    /**
     * A signed change, coloured by whether it is good news.
     *
     * The sign always tells the truth about the direction; `higherIsBetter`
     * only decides the colour. Debt is the case that forces the distinction -
     * more of it is worse, but a line reading "-$48,074" on a city whose debt
     * went UP by that much is simply a lie in service of a colour.
     */
    private void addChangeLine(VBox section, String label, double change,
                               boolean isMoney, boolean higherIsBetter) {

        // Anything that rounds away to nothing IS nothing. Without this a change
        // of -0.004 prints as a red "-$0", which reads like a problem rather
        // than the rounding artefact it is.
        if (Math.abs(change) < .005) {
            change = 0;
        }

        String value = isMoney
                ? (change >= 0 ? "+" : "-") + "$" + formatter.format(Math.abs(change))
                : String.format("%+,.0f", change);

        boolean good = higherIsBetter ? change > 0 : change < 0;
        boolean bad = higherIsBetter ? change < 0 : change > 0;

        Label line = monoLabel(String.format("%-20s%s", label, value));
        line.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: "
                + (bad ? "#c62828" : good ? "#2e7d32" : "#666666") + ";");
        section.getChildren().add(line);
    }

    /** Shared scaffolding for the sector report screens. */
    private void showSectorReport(String title, VBox column, Runnable back) {
        showSectorReport(title, column, back, null);
    }

    /**
     * @param extra an optional button shown above Back, for screens that lead
     *              somewhere else - e.g. the industrial report linking to its
     *              financial statements. rootMenu is a VBox, so the buttons
     *              stack, which matches every other menu in the game.
     */
    private void showSectorReport(String title, VBox column, Runnable back, Button extra) {
        Label heading = new Label(title);
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        Label gameInfo = new Label("Month: " + game.getMonth()
                + " | Cash: $" + formatter.format(game.getCash()));

        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);

        VBox content = new VBox(0);
        content.setAlignment(Pos.CENTER);
        content.getChildren().add(column);

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setStyle("-fx-background-color:transparent;");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> back.run());

        rootMenu.getChildren().addAll(heading, gameInfo, scrollPane);
        if (extra != null) {
            rootMenu.getChildren().add(extra);
        }
        rootMenu.getChildren().add(backButton);
    }

    /** Colours a net-income line red or green and appends it to a section. */
    private void addNetIncomeLine(VBox section, String label, double value) {
        Label line = monoLabel(String.format("%-32s $%s", label, formatter.format(value)));
        line.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: "
                + (value < 0 ? "#c62828" : "#2e7d32") + ";");
        section.getChildren().add(line);
    }

    /**
     * JavaFX port of IndustrialHandler.printIndustrialInfo().
     *
     * Pure reader - the monthly figures are calculated once per month by
     * IndustrialHandler.calculateIndustrialResults(), driven from
     * Game.startOfMonthUpdate(). The old printer banked cash as a side effect,
     * so wiring this button straight to it would have paid the industrial sector
     * again on every click.
     */
    private void showIndustrialInfoMenu() {
        clearMenu();

        IndustrialHandler ih = game.getEconomyManager().getIndustrialHandler();

        VBox column = new VBox(0);
        column.getChildren().addAll(
                reportSection("FACILITY OVERVIEW",
                        String.format("Storage Capacity:       %,d units", ih.getReportFoodCapacity()),
                        String.format("Current Inventory:      %,d units", ih.getReportFoodInventory())),

                reportSection("RESOURCE UTILIZATION",
                        String.format("Labor Fill Rate:        %.1f%%", ih.getReportAverageFill() * 100),
                        String.format("Energy Efficiency:      %.1f%%", ih.getReportEnergyRatio() * 100),
                        String.format("Water Efficiency:       %.1f%%", ih.getReportWaterRatio() * 100)),

                reportSection("PRODUCTION ANALYSIS",
                        String.format("Base Potential:         %,d units", ih.getReportBaseProduction()),
                        String.format("Actual Output:          %,.0f units", ih.getReportActualProduction())),

                reportSection("MARKET PERFORMANCE",
                        String.format("Market Demand:          %,.0f units", ih.getReportDemand()),
                        String.format("Units Sold:             %,d units", ih.getReportUnitsSold()),
                        String.format("Average Market Price:   $%s per unit", formatter.format(ih.getReportSellPrice())),
                        String.format("Gross Revenue:          $%s", formatter.format(ih.getGrossRevenue()))));

        VBox statement = reportSection("INCOME STATEMENT (INDUSTRIAL COMPANY)",
                String.format("Industrial Goods Sales:          $%s", formatter.format(ih.getGrossRevenue())),
                "",
                String.format("Payroll Expense:                -$%s", formatter.format(ih.getReportPayroll())),
                String.format("Electricity Expense:            -$%s", formatter.format(ih.getReportElectricityCost())),
                String.format("Water Expense:                  -$%s", formatter.format(ih.getReportWaterCost())),
                String.format("Interest Expense:               -$%s", formatter.format(ih.getReportInterestExpense())),
                String.format("Property Tax:                   -$%s", formatter.format(ih.getReportPropertyTaxExpense())),
                "---------------------------------------------------",
                String.format("Total Operating Expenses:       -$%s", formatter.format(ih.getReportOperatingCost())));
        addNetIncomeLine(statement, "NET INCOME (INDUSTRIAL):", ih.getNetIncome());
        column.getChildren().add(statement);

        FoodMarket market = game.getEconomyManager().getFoodMarket();
        VBox marketSection = reportSection("FOOD MARKET",
                String.format("Local Price:            $%s /unit", formatter.format(market.getLocalPrice())),
                String.format("Import Price (ceiling): $%s /unit", formatter.format(market.getImportPrice())),
                String.format("Break-even Cost:        $%s /unit", formatter.format(ih.getReportCostPerUnit())),
                String.format("Offered to Market:      %,.0f units", ih.getReportOffered()),
                String.format("Withheld:               %,.0f units", ih.getReportWithheld()));

        if (ih.getReportWithheld() > 0) {
            Label held = monoLabel("Price is below cost - holding stock back.");
            held.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px; -fx-text-fill: #c62828;");
            marketSection.getChildren().add(held);
        } else if (market.isShortage()) {
            Label tight = monoLabel("Local supply short - buyers are importing.");
            tight.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px; -fx-text-fill: #2e7d32;");
            marketSection.getChildren().add(tight);
        }
        column.getChildren().add(marketSection);

        column.getChildren().add(reportSection("TAX SUMMARY",
                String.format("Tax Rate:               %.1f%%", ih.getReportTaxRate() * 100),
                String.format("Government Tax Revenue: $%s", formatter.format(ih.getReportTaxIncome())),
                String.format("Business Cash Reserves: $%s", formatter.format(ih.getIndustrialCash()))));

        if (ih.getReportFoodCapacity() > 0
                && ih.getReportFoodInventory() >= ih.getReportFoodCapacity() * 0.9) {
            VBox warning = reportSection("[WARNING] WAREHOUSE ABOVE 90%",
                    "Production may stall due to limited storage.");
            warning.getChildren().get(0).setStyle(
                    "-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #c62828;");
            column.getChildren().add(warning);
        }

        Button financials = new Button("Financial Statements");
        financials.setOnAction(e -> showIndustrialFinancialsMenu());

        showSectorReport("INDUSTRIAL SECTOR REPORT", column, this::showPrivateSectorMenu, financials);
    }

    /**
     * The food industry's books: income statement for the month just closed,
     * balance sheet as of now.
     *
     * Kept separate from showIndustrialInfoMenu() on purpose. That screen answers
     * "how is the factory running" - fill rates, output, market price. This one
     * answers "what is this business worth and did it make money", which is a
     * different question asked at a different moment.
     *
     * Pure reader. Every figure comes from r-fields already computed once per
     * month by calculateIndustrialResults(), or from live balance-sheet state.
     */
    private void showIndustrialFinancialsMenu() {
        clearMenu();

        IndustrialHandler ih = game.getEconomyManager().getIndustrialHandler();
        BalanceSheet bs = ih.getBalanceSheet();

        BusinessDebtManager credit = game.getEconomyManager().getBusinessDebtManager();

        double revenue = ih.getGrossRevenue();
        double operatingIncome = ih.getReportOperatingIncome();
        double interest = ih.getReportInterestExpense();
        double preTax = ih.getNetIncome();
        double tax = ih.getReportTaxIncome();
        double netAfterTax = ih.getReportNetIncomeAfterTax();

        VBox column = new VBox(0);

        /* ------------------------- INCOME STATEMENT ------------------------- */
        column.getChildren().add(sectionHeading("=== INCOME STATEMENT (month just closed) ==="));

        column.getChildren().add(reportSection("REVENUE",
                String.format("Goods Sales:                     $%s", formatter.format(revenue))));

        column.getChildren().add(reportSection("OPERATING EXPENSES",
                String.format("Payroll:                        -$%s", formatter.format(ih.getReportPayroll())),
                String.format("Electricity:                    -$%s", formatter.format(ih.getReportElectricityCost())),
                String.format("Water:                          -$%s", formatter.format(ih.getReportWaterCost())),
                String.format("Property Tax:                   -$%s", formatter.format(ih.getReportPropertyTaxExpense())),
                "---------------------------------------------------",
                String.format("Total Operating Expenses:       -$%s", formatter.format(ih.getReportOperatingCost()))));

        VBox bottomLine = reportSection("RESULT");
        addNetIncomeLine(bottomLine, "OPERATING INCOME:", operatingIncome);
        bottomLine.getChildren().add(monoLabel(
                String.format("%-32s-$%s", "Interest Expense:", formatter.format(interest))));
        addNetIncomeLine(bottomLine, "PRE-TAX INCOME:", preTax);
        bottomLine.getChildren().add(monoLabel(
                String.format("%-32s-$%s", String.format("Business Tax @ %.0f%%:", ih.getReportTaxRate() * 100),
                        formatter.format(tax))));
        addNetIncomeLine(bottomLine, "NET INCOME (AFTER TAX):", netAfterTax);

        // Worth surfacing rather than quietly presenting a tidy statement: the
        // cash reserve is credited with the PRE-tax figure while the city also
        // collects the tax, so the same money is counted twice.
        Label taxNote = monoLabel("Note: cash is credited with the pre-tax figure.");
        taxNote.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px; -fx-text-fill: #c62828;");
        bottomLine.getChildren().add(taxNote);
        column.getChildren().add(bottomLine);

        /* -------------------------- BALANCE SHEET --------------------------- */
        column.getChildren().add(sectionHeading("=== BALANCE SHEET (as of now) ==="));

        column.getChildren().add(reportSection("CURRENT ASSETS",
                String.format("Cash:                            $%s", formatter.format(bs.getCash())),
                String.format("Inventory:                       $%s", formatter.format(bs.getInventory())),
                String.format("  %,d units @ $%s (market)", bs.getInventoryUnits(),
                        formatter.format(bs.getInventoryUnitPrice())),
                "---------------------------------------------------",
                String.format("Total Current Assets:            $%s", formatter.format(bs.getCurrentAssets()))));

        column.getChildren().add(reportSection("NON-CURRENT ASSETS",
                String.format("Land:                            $%s", formatter.format(bs.getLand())),
                "  (land ownership not modelled yet)",
                String.format("Buildings, at cost:              $%s", formatter.format(bs.getBuildings())),
                "  (cash + materials; excludes construction labour)",
                "---------------------------------------------------",
                String.format("Total Non-Current Assets:        $%s", formatter.format(bs.getNonCurrentAssets()))));

        VBox assets = reportSection("");
        Label totalAssets = monoLabel(String.format("%-32s $%s", "TOTAL ASSETS:",
                formatter.format(bs.getTotalAssets())));
        totalAssets.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        assets.getChildren().add(totalAssets);
        column.getChildren().add(assets);

        column.getChildren().add(reportSection("LIABILITIES",
                String.format("Loans Payable:                   $%s", formatter.format(bs.getBondsPayable())),
                "---------------------------------------------------",
                String.format("Total Liabilities:               $%s", formatter.format(bs.getTotalLiabilities()))));

        column.getChildren().add(reportSection("EQUITY",
                String.format("Owner's Equity:                  $%s", formatter.format(bs.getEquity())),
                "  (balancing figure: assets less liabilities)"));

        VBox tie = reportSection("");
        Label totalLE = monoLabel(String.format("%-32s $%s", "TOTAL LIABILITIES + EQUITY:",
                formatter.format(bs.getTotalLiabilitiesAndEquity())));
        totalLE.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        tie.getChildren().add(totalLE);
        column.getChildren().add(tie);

        /* ----------------------------- CREDIT ------------------------------ */
        addCreditBlock(column, credit, BusinessDebtManager.INDUSTRY, interest, preTax);

        /* ----------------------------- RATIOS ------------------------------ */
        column.getChildren().add(reportSection("KEY RATIOS",
                String.format("Net Margin:             %.1f%%",
                        (revenue > 0 ? netAfterTax / revenue : 0) * 100),
                String.format("Return on Assets:       %.2f%% /month",
                        bs.getReturnOnAssets(netAfterTax) * 100),
                String.format("Inventory / Assets:     %.1f%%",
                        bs.getInventoryShareOfAssets() * 100),
                String.format("Debt / Assets:          %.1f%%", bs.getDebtToAssets() * 100),
                String.format("Interest Coverage:      %s",
                        interest > 0
                                ? String.format("%.2fx", operatingIncome / interest)
                                : "n/a (no debt)")));

        showSectorReport("FOOD INDUSTRY - FINANCIAL STATEMENTS", column, this::showIndustrialInfoMenu);
    }

    /** JavaFX port of UtilitiesHandler.printUtilitiesInfo(). That printer is already pure. */
    /**
     * Two utilities, one report. Electricity and water each get their own status,
     * load analysis and income statement, and then they total - they share a
     * workforce and a balance sheet, but a player deciding whether to build a
     * water plant needs to see water's own numbers, not a blended figure.
     */
    private void showUtilityInfoMenu() {
        clearMenu();

        UtilitiesHandler uh = game.getServicesManager().getUtilitiesHandler();

        double energyRatio = uh.getEnergyRatio();
        double waterRatio = uh.getWaterRatio();

        double elecRevenue = uh.getElectricityRevenue();
        double elecPayroll = uh.getElectricityPayroll();
        double waterRevenue = uh.getWaterRevenue();
        double waterPayroll = uh.getWaterPayroll();

        VBox column = new VBox(0);

        /* ------------------------------ ELECTRICITY ------------------------------ */
        column.getChildren().add(sectionHeading("=== ELECTRIC POWER ==="));

        VBox grid = reportSection("GRID STATUS",
                String.format("Grid Satisfaction:      %.1f%%", energyRatio * 100));
        grid.getChildren().add(statusLabel("System Stability:", energyRatio >= 1.0, "STABLE", "BROWNOUT"));
        column.getChildren().add(grid);

        column.getChildren().add(reportSection("ENERGY LOAD ANALYSIS",
                String.format("Total Consumption:      %s W", formatter.format(uh.getConsumption())),
                String.format("Maximum Generation:     %s W", formatter.format(uh.getBaseProduction())),
                String.format("Current Output:         %s W", formatter.format(uh.getProduction())),
                String.format("Price per Watt:         $%s", formatter.format(uh.getPricerPerWatt()))));

        VBox elecStatement = reportSection("INCOME STATEMENT (ELECTRIC POWER)",
                String.format("Electricity Sales:               $%s", formatter.format(elecRevenue)),
                String.format("Payroll Expense:                -$%s", formatter.format(elecPayroll)),
                "---------------------------------------------------");
        addNetIncomeLine(elecStatement, "NET INCOME (ELECTRIC):", elecRevenue - elecPayroll);
        column.getChildren().add(elecStatement);

        if (energyRatio < 1.0) {
            column.getChildren().add(criticalSection("[CRITICAL] GRID SHORTAGE",
                    String.format("Additional capacity needed: %s W",
                            formatter.format(uh.getConsumption() - uh.getProduction())),
                    "Industrial and commercial output is reduced."));
        }

        /* --------------------------------- WATER --------------------------------- */
        column.getChildren().add(sectionHeading("=== WATER SUPPLY ==="));

        VBox supply = reportSection("SUPPLY STATUS",
                String.format("Supply Satisfaction:    %.1f%%", waterRatio * 100));
        supply.getChildren().add(statusLabel("System Status:", waterRatio >= 1.0, "ADEQUATE", "RATIONING"));
        column.getChildren().add(supply);

        // Splitting resident from building draw is the useful part: it is the
        // difference between "stop building housing" and "stop building food plants".
        column.getChildren().add(reportSection("WATER LOAD ANALYSIS",
                String.format("Resident Draw:          %s units", formatter.format(uh.getResidentWaterDraw())),
                String.format("Building Draw:          %s units", formatter.format(uh.getBuildingWaterDraw())),
                String.format("Total Draw:             %s units", formatter.format(uh.getWaterConsumption())),
                String.format("  billed:               %s units", formatter.format(uh.getBilledWaterDraw())),
                String.format("  unbilled:             %s units", formatter.format(uh.getUnbilledWaterDraw())),
                String.format("Maximum Capacity:       %s units", formatter.format(uh.getBaseWaterProduction())),
                String.format("Current Output:         %s units", formatter.format(uh.getWaterProduction())),
                String.format("Price per Unit:         $%s", formatter.format(uh.getPricePerWaterUnit()))));

        VBox waterStatement = reportSection("INCOME STATEMENT (WATER)",
                "Only commercial and industrial draw is invoiced;",
                "households have no cash, so resident water is unbilled.",
                "",
                String.format("Water Sales:                     $%s", formatter.format(waterRevenue)),
                String.format("Payroll Expense:                -$%s", formatter.format(waterPayroll)),
                "---------------------------------------------------");
        addNetIncomeLine(waterStatement, "NET INCOME (WATER):", waterRevenue - waterPayroll);
        column.getChildren().add(waterStatement);

        if (waterRatio < 1.0) {
            column.getChildren().add(criticalSection("[CRITICAL] WATER SHORTAGE",
                    String.format("Additional capacity needed: %s units",
                            formatter.format(uh.getWaterConsumption() - uh.getWaterProduction())),
                    "Industrial and commercial output is reduced."));
        }

        /* ------------------------------ CONSOLIDATED ----------------------------- */
        column.getChildren().add(sectionHeading("=== CONSOLIDATED ==="));

        column.getChildren().add(reportSection("RESOURCE UTILIZATION",
                String.format("Labor Fill Rate:        %.1f%%", uh.getAverageUtilityFill() * 100),
                "One workforce runs both utilities."));

        double totalRevenue = elecRevenue + waterRevenue;
        double totalPayroll = elecPayroll + waterPayroll;

        VBox consolidated = reportSection("INCOME STATEMENT (ALL UTILITIES)",
                String.format("Electricity Sales:               $%s", formatter.format(elecRevenue)),
                String.format("Water Sales:                     $%s", formatter.format(waterRevenue)),
                String.format("Total Revenue:                   $%s", formatter.format(totalRevenue)),
                "",
                String.format("Total Payroll Expense:          -$%s", formatter.format(totalPayroll)),
                "---------------------------------------------------");
        addNetIncomeLine(consolidated, "NET INCOME (UTILITIES):", totalRevenue - totalPayroll);
        column.getChildren().add(consolidated);

        /* ------------------------------ ROAD NETWORK -----------------------------
           Last, and after the consolidated totals rather than inside them: roads
           bill nobody, so they have no income statement to add. What they have
           is a capacity, a load, and a bill the whole city pays in throughput.
           ---------------------------------------------------------------------- */
        InfrastructureManager roads = game.getInfrastructureManager();

        column.getChildren().add(sectionHeading("=== ROAD NETWORK ==="));

        VBox network = reportSection("NETWORK STATUS",
                String.format("Throughput:             %.1f%%", roads.getThroughputRatio() * 100),
                String.format("Traffic:                %s of %s trips",
                        formatter.format(roads.getLoad()),
                        formatter.format(roads.getCapacity())),
                String.format("Utilisation:            %.1f%%", roads.getUtilisation() * 100));
        network.getChildren().add(statusLabel("Traffic Status:",
                !roads.isCongested(), roads.getStatus(), "CONGESTED"));
        column.getChildren().add(network);

        column.getChildren().add(reportSection("CAPACITY",
                String.format("Built by the city:      %s",
                        formatter.format(roads.getBuiltCapacity())),
                String.format("Existing streets:       %s",
                        formatter.format(InfrastructureManager.BASE_CAPACITY)),
                String.format("Room before it slows:   %s trips",
                        formatter.format(roads.getHeadroom())),
                "Traffic flows freely up to "
                        + Math.round(InfrastructureManager.FREE_FLOW * 100)
                        + "% of capacity."));

        if (roads.isCongested()) {
            // The number a player can act on: not "you are congested" but "this
            // much more capacity and you are not".
            double needed = roads.getLoad() / InfrastructureManager.FREE_FLOW
                    - roads.getCapacity();
            column.getChildren().add(criticalSection("[CRITICAL] TRAFFIC CONGESTION",
                    String.format("Additional capacity needed: %s trips",
                            formatter.format(Math.max(0, needed))),
                    "Retail sales, industrial output and construction are all reduced."));
        } else if (roads.isStrained()) {
            column.getChildren().add(reportSection("[NOTICE] APPROACHING CAPACITY",
                    String.format("Room for %s more trips before traffic slows.",
                            formatter.format(roads.getHeadroom())),
                    "Order roads before the next expansion, not after."));
        }

        showSectorReport("MUNICIPAL SERVICES REPORT", column, this::showPrivateSectorMenu);
    }

    /**
     * Roads on the city overview, in one cell.
     *
     * Shows utilisation while there is room and throughput once there is not,
     * because those are the two different questions a player is asking: "how
     * much more can I build" until it jams, and "how much is this costing me"
     * after.
     */
    private String roadSummary() {
        InfrastructureManager roads = game.getInfrastructureManager();
        if (roads.isCongested()) {
            return formatter.format(roads.getThroughputRatio() * 100) + "% flow";
        }
        return formatter.format(roads.getUtilisation() * 100) + "% used";
    }

    /** A bold green/red status row, e.g. "System Stability:  STABLE". */
    private Label statusLabel(String label, boolean good, String goodText, String badText) {
        Label line = monoLabel(String.format("%-24s%s", label, good ? goodText : badText));
        line.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: "
                + (good ? "#2e7d32" : "#c62828") + ";");
        return line;
    }

    /** A report section whose heading is styled as a red warning. */
    private VBox criticalSection(String heading, String... rows) {
        VBox box = reportSection(heading, rows);
        box.getChildren().get(0).setStyle(
                "-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #c62828;");
        return box;
    }

    /**
     * JavaFX port of ConstructionHandler.printConstructionInfo(). That report had
     * no menu entry at all in the terminal build - it was only ever printed as
     * part of the monthly report block.
     */
    private void showConstructionInfoMenu() {
        clearMenu();

        ConstructionHandler ch = game.getServicesManager().getConstructionHandler();

        VBox column = new VBox(0);
        column.getChildren().addAll(
                reportSection("CONSTRUCTION CAPACITY",
                        String.format("Effective Output:       %s pts/mo", formatter.format(game.getConstructionOutput())),
                        String.format("Sector Production:      %s pts", formatter.format(ch.getConstructionOutput())),
                        String.format("Sites in Progress:      %,d", game.getBuildingManager().getUnderConstruction())),

                reportSection("MATERIALS",
                        String.format("Production:             %s /mo", formatter.format(ch.getMaterialsProduction())),
                        String.format("Inventory:              %s", formatter.format(ch.getMaterialsInventory())),
                        String.format("Consumed:               %,d", ch.getMaterialsConsumed()),
                        String.format("Market Price:           $%s", formatter.format(ch.getMaterialsPrice()))),

                reportSection("LABOR UTILIZATION",
                        String.format("Workforce Fill:         %.1f%%", ch.getAverageFill() * 100)));

        VBox statement = reportSection("OPERATING COSTS",
                String.format("Wage Expense:                   -$%s", formatter.format(ch.getWageExpense())),
                String.format("Materials Expense:              -$%s", formatter.format(ch.getMaterialsExpense())),
                "---------------------------------------------------");
        addNetIncomeLine(statement, "TOTAL OPERATING COSTS:", -ch.getExpenses());
        column.getChildren().add(statement);

        /* ---------------------------- the retainer ----------------------------
         *
         * The one control on this screen, and the reason it exists: idle
         * construction is loss-making, so it scraps capacity in any lull -
         * including the capacity the city just paid for. Over four thousand
         * months of playtesting that cost one city 187 depots.
         *
         * Shown as capacity protected rather than as money, because that is the
         * decision. The money is the price of it.
         * ------------------------------------------------------------------- */
        boolean protectedNow = game.isAutoSubsidised(PolicySector.CONSTRUCTION);
        double capacity = game.getBuildingManager().getTotalConstructionCapacity();

        /*
         * NOTE: this was a dollar retainer with -$100 / +$100 buttons, and the
         * number it needed went stale every time the sector grew - it had to be
         * re-set five times in a single playtest. It is the standing policy now,
         * measured against the loss rather than against a figure picked once,
         * and it is set here or on the Policy tab; both are the same switch.
         */
        VBox retainer = reportSection("STANDING POLICY",
                String.format("Protected:              %s",
                        protectedNow ? "YES - the city covers this sector's losses" : "no"),
                String.format("Capacity at stake:      %s pts", formatter.format(capacity)),
                String.format("Paid last month:        $%s",
                        formatter.format(game.getSubsidyPaid(PolicySector.CONSTRUCTION))),
                "",
                "Builders you are not using will be let go. Protecting the sector",
                "tops it up to break-even so it never has to sell its crews.");
        column.getChildren().add(retainer);

        Button toggle = new Button(protectedNow
                ? "Stop protecting construction" : "Protect construction");
        if (protectedNow) {
            toggle.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");
        }
        toggle.setOnAction(e -> {
            game.setAutoSubsidised(PolicySector.CONSTRUCTION,
                    !game.isAutoSubsidised(PolicySector.CONSTRUCTION));
            showConstructionInfoMenu();
        });
        column.getChildren().add(toggle);

        showSectorReport("MUNICIPAL CONSTRUCTION AUTHORITY", column, this::showSectorMenu);
    }

    /**
     * The city's own view of the economy: what was produced, how fast that is
     * changing, and what the government took and spent.
     *
     * GDP is broken into C / I / G / NX rather than shown as one number, because
     * one number cannot tell you whether a city is growing because people are
     * buying more or because it is building more - and those call for completely
     * different responses from the player.
     */
    private void showGovernmentMenu() {
        clearMenu();

        EconomyManager em = game.getEconomyManager();
        NationalAccounts na = em.getNationalAccounts();

        int population = game.getPopulationManager().getPopulation();
        double debt = game.getDebtManager().getAllPrincipal();
        double rate = game.getDebtManager().getRate();

        VBox column = new VBox(0);

        /* ------------------------- OUTPUT ------------------------- */
        column.getChildren().add(sectionHeading("=== GROSS DOMESTIC PRODUCT ==="));

        VBox headline = reportSection("THIS MONTH",
                String.format("GDP:                    $%s", formatter.format(na.getGdp())),
                String.format("Trend (12-mo average):  $%s", formatter.format(na.getTrendGdp())),
                String.format("Annual (last 12 mo):    $%s", formatter.format(na.getAnnualGdp())),
                String.format("Per Capita (annual):    $%s thousand",
                        formatter.format(na.getGdpPerCapita(population))));
        column.getChildren().add(headline);

        // C + I + G + NX, each with its parts, so the total is checkable by eye.
        column.getChildren().add(reportSection("CONSUMPTION (C)",
                String.format("Goods bought in shops:  $%s", formatter.format(na.getConsumptionGoods())),
                String.format("Housing (rent paid):    $%s", formatter.format(na.getConsumptionHousing())),
                "---------------------------------------------------",
                String.format("Total Consumption:      $%s", formatter.format(na.getConsumption()))));

        column.getChildren().add(reportSection("INVESTMENT (I)",
                String.format("Construction put up:    $%s", formatter.format(na.getInvestmentConstruction())),
                String.format("Change in stock:        $%s", formatter.format(na.getInvestmentInventories())),
                "  (stock built up is output not yet sold)",
                "---------------------------------------------------",
                String.format("Total Investment:       $%s", formatter.format(na.getInvestment()))));

        column.getChildren().add(reportSection("GOVERNMENT (G)",
                String.format("Services provided:      $%s", formatter.format(na.getGovernment())),
                "  (cost of running the city's own utilities)",
                "  land trading is not output, so it is not counted here"));

        column.getChildren().add(reportSection("NET EXPORTS (NX)",
                String.format("Steel exported:         $%s", formatter.format(na.getExports())),
                String.format("Food imported:         -$%s", formatter.format(na.getImportsFood())),
                String.format("Materials imported:    -$%s", formatter.format(na.getImportsMaterials())),
                String.format("Scrap imported:        -$%s", formatter.format(na.getImportsRawMaterial())),
                "---------------------------------------------------",
                String.format("Net Exports:            $%s", formatter.format(na.getNetExports())),
                "  a mill that ships out more than the scrap it buys in",
                "  is the only thing here that adds to output"));

        VBox total = reportSection("");
        Label gdpTotal = monoLabel(String.format("%-32s $%s", "GDP = C + I + G + NX:",
                formatter.format(na.getGdp())));
        gdpTotal.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        total.getChildren().add(gdpTotal);
        column.getChildren().add(total);

        /* ------------------------- GROWTH ------------------------- */
        column.getChildren().add(sectionHeading("=== GROWTH ==="));

        VBox growth = reportSection("RATES");
        addGrowthLine(growth, "Month on month (annualised):", na.getMonthlyGrowthAnnualised());

        if (na.getMonthsRecorded() >= 13) {
            addGrowthLine(growth, "Year on year:", na.getYearOnYearGrowth());
        } else {
            growth.getChildren().add(monoLabel(String.format("%-32s%s", "Year on year:",
                    "needs " + (13 - na.getMonthsRecorded()) + " more months")));
        }
        growth.getChildren().add(monoLabel(String.format("%-32s%,d months", "History recorded:",
                na.getMonthsRecorded())));
        column.getChildren().add(growth);

        /* ---------------------- GOVERNMENT BOOKS ---------------------- */
        column.getChildren().add(sectionHeading("=== GOVERNMENT ACCOUNTS ==="));

        column.getChildren().add(reportSection(
                String.format("REVENUE  (tax rate %.1f%%)", em.getTaxRate() * 100),
                String.format("Business Tax:           $%s", formatter.format(na.getTaxBusiness())),
                String.format("Industrial Tax:         $%s", formatter.format(na.getTaxIndustrial())),
                String.format("Sales Tax:              $%s", formatter.format(na.getTaxSales())),
                String.format("Wage Tax:               $%s", formatter.format(na.getTaxWage())),
                String.format("Utility Net Income:     $%s", formatter.format(na.getUtilityIncome())),
                String.format("Property Tax:           $%s", formatter.format(na.getPropertyTax())),
                String.format("Land Sold:              $%s", formatter.format(na.getLandSales())),
                "---------------------------------------------------",
                String.format("Total Revenue:          $%s", formatter.format(na.getTotalRevenue()))));

        column.getChildren().add(reportSection("EXPENDITURE",
                String.format("Debt Interest:         -$%s", formatter.format(na.getInterestExpense())),
                String.format("Buildings (capital):   -$%s", formatter.format(na.getCapitalSpending())),
                String.format("Land Bought:           -$%s", formatter.format(na.getLandPurchases())),
                "---------------------------------------------------",
                String.format("Total Expenditure:     -$%s", formatter.format(na.getTotalExpenses()))));

        VBox balance = reportSection("BALANCE");
        addNetIncomeLine(balance, na.getBalance() < 0 ? "DEFICIT:" : "SURPLUS:", na.getBalance());
        balance.getChildren().add(monoLabel(String.format("%-32s $%s", "Cash Reserves:",
                formatter.format(game.getCash()))));
        column.getChildren().add(balance);

        /* ------------------------- DEBT ------------------------- */
        column.getChildren().add(reportSection("PUBLIC DEBT",
                String.format("Outstanding:            $%s", formatter.format(debt)),
                String.format("Borrowing Rate:         %.2f%%", rate * 100),
                String.format("Debt to Annual GDP:     %.1f%%", na.getDebtToGdp(debt) * 100),
                String.format("Revenue to GDP:         %.1f%%", na.getRevenueToGdp() * 100),
                "",
                String.format("Private Sector Debt:    $%s",
                        formatter.format(em.getBusinessDebtManager().getTotalPrincipal()))));

        showSectorReport("GOVERNMENT & NATIONAL ACCOUNTS", column, this::showEconomyMenu);
    }

    /** A growth rate, green when positive and red when the city is shrinking. */
    private void addGrowthLine(VBox section, String label, double rate) {
        Label line = monoLabel(String.format("%-32s%+.2f%%", label, rate * 100));
        line.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: "
                + (rate < 0 ? "#c62828" : "#2e7d32") + ";");
        section.getChildren().add(line);
    }

    private void showDebtInfoMenu() {
        clearMenu();

        Label title = new Label("DEBT PORTFOLIO");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");

        // Container for the list of debts
        VBox debtList = new VBox(5);
        debtList.setAlignment(Pos.CENTER);

        // Get and sort the data (Logic from your printDebtInfo)
        List<Debt> sortedDebts = new ArrayList<>(game.getDebtManager().getDebt());
        sortedDebts.sort(java.util.Comparator.comparingInt(Debt::getMaturityMonth));

        double totalPrincipal = 0;
        double totalMonthlyInterest = 0;
        int currentMonth = game.getMonth();

        // Create a Header row
        Label header = new Label(String.format("%-10s | %-15s | %-12s | %-15s",
                "Maturity", "Type", "Principal", "Interest"));
        header.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold;");
        debtList.getChildren().add(header);

        for (Debt debt : sortedDebts) {
            double principal = debt.getOustandingPrincipal();
            double interest = debt.getMonthlyInterestExpense();
            totalPrincipal += principal;
            totalMonthlyInterest += interest;

            String status = (debt.getMaturityMonth() == currentMonth) ? " [DUE NOW]" : "";

            // Create a label for each debt entry
            Label debtRow = new Label(String.format("Month %-4d | %-15s | %-12s | %-15s%s",
                    debt.getMaturityMonth(),
                    debt.getType(),
                    formatter.format(principal),
                    formatter.format(interest),
                    status));

            debtRow.setStyle("-fx-font-family: 'Courier New';");
            if (!status.isEmpty()) {
                debtRow.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: red;");
            }

            debtList.getChildren().add(debtRow);
        }

        // Totals Section
        VBox totalsBox = new VBox(5);
        totalsBox.setAlignment(Pos.CENTER);
        totalsBox.setStyle("-fx-padding: 20; -fx-border-color: black; -fx-border-width: 1 0 0 0;");

        Label totalP = new Label("TOTAL OUTSTANDING PRINCIPAL: $" + formatter.format(totalPrincipal));
        Label totalI = new Label("TOTAL MONTHLY INTEREST COST: $" + formatter.format(totalMonthlyInterest));
        totalP.setStyle("-fx-font-weight: bold;");

        totalsBox.getChildren().addAll(totalP, totalI);

        Button back = new Button("Back");
        back.setOnAction(e -> showEconomyMenu());

        // Wrap the list in a ScrollPane in case you have 50+ bonds
        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(debtList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background-color:transparent;");

        rootMenu.getChildren().addAll(title, scrollPane, totalsBox, back);
    }
    
    
    
    
    
    
    
    
    /* =====================================================================
       CONSTRUCTION PANEL

       Ports the terminal build's per-stack construction readout - the
       "0/1 Coal Power Plant(s) finished construction. 177 month(s)." line - into
       a panel that's visible from every screen.

       It also surfaces something that was previously invisible: construction
       capacity is divided evenly between *sites* (stacks), not weighted by work
       remaining, so every extra building type you queue slows down everything
       already in progress. The "N sites, X pts each" line makes that legible.
       ===================================================================== */

    private void refreshConstructionPanel() {
        constructionPanel.getChildren().clear();

        Label header = new Label("UNDER CONSTRUCTION");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        constructionPanel.getChildren().add(header);

        BuildingManager buildingManager = game.getBuildingManager();
        List<BuildingsStacks> sites = buildingManager.getStacksUnderConstruction();

        int output = game.getConstructionOutput();
        int siteCount = buildingManager.getUnderConstruction();
        double perSite = (siteCount > 0) ? (double) output / siteCount : output;

        Label capacity = monoLabel("Output: " + formatter.format(output) + " pts/mo");
        capacity.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #555555;");
        constructionPanel.getChildren().add(capacity);

        if (sites.isEmpty()) {
            Label idle = new Label("Nothing being built.");
            idle.setStyle("-fx-text-fill: #888888; -fx-padding: 8 0 0 0;");
            constructionPanel.getChildren().add(idle);
            addDemolitionLog();
            return;
        }

        Label split = monoLabel(siteCount + " site(s), " + formatter.format(perSite) + " each");
        split.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #555555;");
        constructionPanel.getChildren().add(split);

        VBox list = new VBox(12);
        list.setStyle("-fx-padding: 10 0 0 0;");

        for (BuildingsStacks site : sites) {

            int remaining = site.getUnderConstruction();
            int built = site.getQuantity();
            int pointsEach = site.getBuilding().getConstructionPoints();
            double progress = site.getConstructionProgress();

            // fraction of the NEXT building that's complete
            double fraction = (pointsEach > 0)
                    ? Math.max(0, Math.min(progress / pointsEach, 1.0))
                    : 0;

            Label name = new Label(site.getName());
            // explicit fill: without it the default Label colour renders almost
            // invisibly against the panel's light background
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #1a237e;");

            ProgressBar bar = new ProgressBar(fraction);
            bar.setPrefWidth(240);

            // Same months-remaining calculation the console prints, guarded for the
            // zero-output case (fully unstaffed construction) that would otherwise
            // divide by zero and render as 2147483647.
            String eta;
            if (perSite <= 0) {
                // Two things can stall a site now, and they want different
                // words: nobody to do the work, or nothing able to reach it.
                eta = game.getInfrastructureManager().isCongested()
                        ? "stalled - gridlocked"
                        : "stalled - no workers";
            } else {
                double monthsLeft = Math.ceil((remaining * (double) pointsEach - progress) / perSite);
                eta = "~" + (int) monthsLeft + " mo";
            }

            Label detail = monoLabel(remaining + " left / " + built + " built - " + eta);
            detail.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px; -fx-text-fill: #555555;");

            VBox row = new VBox(3);
            row.getChildren().addAll(name, bar, detail);
            list.getChildren().add(row);
        }

        javafx.scene.control.ScrollPane scroller = new javafx.scene.control.ScrollPane(list);
        scroller.setFitToWidth(true);
        scroller.setPrefHeight(560);
        scroller.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        constructionPanel.getChildren().add(scroller);

        addDemolitionLog();
    }

    /**
     * What the city has lost lately, under what it is building.
     *
     * Deliberately in the same panel as construction rather than on a screen of
     * its own: these are the two halves of the same thing, and a player watching
     * their city go up should see it come down in the same place. Entries stay
     * for two years of turns because someone fast-forwarding fifty months
     * otherwise has to reconstruct what happened from a building count that went
     * down while they were not looking.
     */
    private void addDemolitionLog() {

        java.util.List<DemolitionLog.Entry> lost =
                game.getDemolitionLog().recent(game.getMonth());

        if (lost.isEmpty()) {
            return;
        }

        Label header = new Label("RECENTLY DEMOLISHED");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;"
                + " -fx-text-fill: #c62828; -fx-padding: 14 0 2 0;");
        constructionPanel.getChildren().add(header);

        VBox list = new VBox(6);

        for (DemolitionLog.Entry entry : lost) {

            Label what = new Label(String.format("%,d x %s", entry.quantity, entry.building));
            what.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #7f1d1d;");

            // Fading with age, so the eye goes to what just happened without the
            // older entries disappearing entirely.
            int ago = entry.monthsAgo(game.getMonth());
            String shade = (ago <= 1) ? "#c62828" : (ago <= 6) ? "#8d6e63" : "#9e9e9e";

            Label when = monoLabel("  " + entry.sector + ", " + entry.when(game.getMonth()));
            when.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;"
                    + " -fx-text-fill: " + shade + ";");

            Label how = monoLabel(entry.wasPaidFor()
                    ? String.format("  plot sold back for $%s", formatter.format(entry.proceeds))
                    : "  plot abandoned");
            how.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;"
                    + " -fx-text-fill: #9e9e9e;");

            VBox row = new VBox(1);
            row.getChildren().addAll(what, when, how);
            list.getChildren().add(row);
        }

        javafx.scene.control.ScrollPane scroller = new javafx.scene.control.ScrollPane(list);
        scroller.setFitToWidth(true);
        scroller.setPrefHeight(Math.min(190, 62 * lost.size() + 8));
        scroller.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        constructionPanel.getChildren().add(scroller);
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

    private String money(double value) {
        return (value < 0)
                ? "-$" + formatter.format(-value)
                : "$" + formatter.format(value);
    }

    private Label statLine(String label, String value) {
        Label row = new Label(String.format("%-13s%12s", label, value));
        row.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px; -fx-text-fill: #333333;");
        return row;
    }

    private Label sectionHeading(String text) {
        Label heading = new Label(text);
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;"
                + " -fx-text-fill: #1a237e; -fx-padding: 10 0 2 0;");
        return heading;
    }

    private void refreshCityPanel() {
        cityPanel.getChildren().clear();

        Label title = new Label("CITY OVERVIEW");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label subtitle = new Label("Month " + game.getMonth());
        subtitle.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px; -fx-text-fill: #555555;");

        VBox body = new VBox(1);

        EconomyManager economyManager = game.getEconomyManager();
        PopulationManager populationManager = game.getPopulationManager();
        BuildingManager buildingManager = game.getBuildingManager();

        /* ---------------- ECONOMY ---------------- */
        double debt = game.getDebtManager().getAllPrincipal();
        double annualGdp = economyManager.getYearGdp();

        body.getChildren().addAll(
                sectionHeading("ECONOMY"),
                statLine("Cash", money(game.getCash())),
                statLine("Net income", money(game.getIncome())),
                // NOTE: this was getGDP() / 12. The GDP field used to hold an
                // annual-ish figure, so the divide made sense then; it now holds
                // the month, and dividing it again showed $64 on a city whose
                // monthly output was $772.
                statLine("Monthly GDP", money(economyManager.getMonthGdp())),
                statLine("Annual GDP", money(annualGdp)));

        int population = populationManager.getPopulation();
        if (population > 0 && annualGdp != 0) {
            body.getChildren().add(
                    statLine("GDP/capita", money((annualGdp / population) * 1000)));
        }

        body.getChildren().addAll(
                statLine("Debt", money(debt)),
                statLine("Biz debt", money(
                        game.getEconomyManager().getBusinessDebtManager().getTotalPrincipal())),
                statLine("Interest", formatter.format(game.getInterestRate() * 100) + "%"));

        if (annualGdp != 0) {
            body.getChildren().add(
                    statLine("Debt/GDP", formatter.format((debt / annualGdp) * 100) + "%"));
        }

        /* ---------------- TAX ---------------- */
        double businessTax = economyManager.getBusinessTax();
        double industrialTax = economyManager.getIndustrialTax();
        double salesTax = economyManager.getSalesTax();
        double wageTax = economyManager.getWageTax();

        body.getChildren().addAll(
                sectionHeading("TAX REVENUE @ " + formatter.format(economyManager.getTaxRate() * 100) + "%"),
                statLine("Business", money(businessTax)),
                statLine("Industrial", money(industrialTax)),
                statLine("Sales", money(salesTax)),
                statLine("Wage", money(wageTax)),
                statLine("Total", money(businessTax + industrialTax + salesTax + wageTax)));

        /* ---------------- POPULATION & LABOUR ---------------- */
        int workforce = populationManager.getWorkforce();
        int totalJobs = populationManager.getTotalJobs();
        int[] vacancies = populationManager.getJobVacancy();
        int totalVacancies = 0;
        for (int v : vacancies) {
            totalVacancies += v;
        }
        int housing = game.getHouseholdCapacity();

        body.getChildren().addAll(
                sectionHeading("POPULATION & LABOUR"),
                statLine("Population", String.format("%,d", population)),
                statLine("Workforce", String.format("%,d", workforce)),
                statLine("Jobs", String.format("%,d", totalJobs)),
                statLine("Vacancies", String.format("%,d", totalVacancies)));

        if (totalJobs > 0) {
            double fill = (double) (totalJobs - totalVacancies) / totalJobs;
            Label fillRow = statLine("Fill rate", String.format("%.1f%%", fill * 100));
            if (fill < 0.75) {
                fillRow.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;"
                        + " -fx-text-fill: #c62828; -fx-font-weight: bold;");
            }
            body.getChildren().add(fillRow);
        }

        body.getChildren().add(
                statLine("Housing", String.format("%,d/%,d", population, housing)));

        /* ---------------- RESOURCES ---------------- */
        body.getChildren().addAll(
                sectionHeading("RESOURCES"),
                statLine("Materials", String.format("%,d", game.getConstructionMaterials())),
                statLine("Store stock", String.format("%,d", economyManager.getStoreInventory())),
                statLine("Food stock", String.format("%,d", economyManager.getIndustryFoodInventory())),
                statLine("Energy", formatter.format(game.getEnergyRatio() * 100) + "%"),
                statLine("Water", formatter.format(game.getWaterRatio() * 100) + "%"),
                statLine("Roads", roadSummary()));

        /* ---------------- LAND ---------------- */
        LandManager land = game.getLandManager();
        double landUsed = land.getUtilisation();

        body.getChildren().addAll(
                sectionHeading("LAND"),
                statLine("Owned", String.format("%.0f blocks",
                        land.getOwnedSqFt() / LandManager.BLOCK_SQ_FT)),
                statLine("Free", String.format("%.1f blocks", land.getAvailableBlocks())));

        Label usedRow = statLine("Used", String.format("%.1f%%", landUsed * 100));
        if (landUsed >= .90) {
            usedRow.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;"
                    + " -fx-text-fill: #c62828; -fx-font-weight: bold;");
        }
        body.getChildren().add(usedRow);

        body.getChildren().add(statLine("Price/sq ft",
                String.format("$%.2f", land.getPricePerSqFt() * 1000)));

        /* ---------------- SECTOR CASH ---------------- */
        body.getChildren().addAll(
                sectionHeading("SECTOR CASH"),
                statLine("Retail", money(economyManager.getCommercialCash())),
                statLine("Real estate", money(economyManager.getRealEstateCash())),
                statLine("Industry", money(economyManager.getIndustrialCash())),
                statLine("Utilities", money(economyManager.getUtilityIncome())));

        /* ---------------- BUILDINGS ---------------- */
        // Fills the gap left by the still-disabled "Other" buildings screen.
        VBox owned = new VBox(1);
        for (int i = 0; i < buildingManager.getTemplateCount(); i++) {
            BuildingsTemplate template = buildingManager.getTemplate(i);
            if (template == null) {
                continue;
            }
            int quantity = buildingManager.getQuantity(i);
            if (quantity > 0) {
                owned.getChildren().add(
                        statLine(shorten(template.getName()), String.format("%,d", quantity)));
            }
        }

        body.getChildren().add(sectionHeading("BUILDINGS"));
        if (owned.getChildren().isEmpty()) {
            Label none = new Label("None built yet.");
            none.setStyle("-fx-font-size: 10px; -fx-text-fill: #888888;");
            body.getChildren().add(none);
        } else {
            body.getChildren().add(owned);
        }

        javafx.scene.control.ScrollPane scroller = new javafx.scene.control.ScrollPane(body);
        scroller.setFitToWidth(true);
        scroller.setPrefHeight(700);
        scroller.setStyle("-fx-background-color:transparent; -fx-background:transparent;");

        cityPanel.getChildren().addAll(title, subtitle, scroller);
    }

    /** Keeps building names inside the panel's fixed-width column. */
    private String shorten(String name) {
        return (name.length() <= 13) ? name : name.substring(0, 12) + ".";
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }

}
