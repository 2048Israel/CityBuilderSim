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


        
        stage.setTitle("City Simulator");
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
        loadGameSave.setOnAction(e -> {
            game.loadGameSave();
            showStartMenu();
        });
        saveGame.setOnAction(e -> showSavingMenu());
        quit.setOnAction(e -> game.toggleQuit());

        settings.setOnAction(e -> showSettingsMenu());
        

        

        rootMenu.getChildren().addAll(
                startNewGame,
                resumeGame,
                loadGameSave,
                saveGame,
                settings,
                quit
        );

        
    }
    
    private void showSavingMenu() {
        clearMenu();
        Button b1 = new Button("Confirm");
        Button b0 = new Button("Cancel");
        
        b1.setOnAction(e -> game.saveGame());
        b0.setOnAction(e -> showMainMenu());
        
        rootMenu.getChildren().addAll(b1,b0);
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
    Button population = new Button("Population");
    Button nextMonth = new Button("Next Month: $" + formatter.format(income));
    Button simulateMultipleMonths = new Button("Simulate Multiple Months");
    Button back = new Button("Back");

    buildings.setOnAction(e -> showBuildingsMenu()); // current menu becomes previousMenu
    economy.setOnAction(e -> showEconomyMenu());
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

    rootMenu.getChildren().addAll(gameInfo, buildings, economy, population, nextMonth, simulateMultipleMonths, back);

    
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
        Button b0 = new Button("Return to menu");
        
        b1.setOnAction(e-> {
            handleAllBuildingMenus("Residential Buildings",EnumSet.of(BuildingType.RESIDENTIAL));
        });
        b2.setOnAction(e-> {
            handleAllBuildingMenus("Commercial Buildings",EnumSet.of(BuildingType.COMMERCIAL));
        });
        b3.setOnAction(e-> {
            handleAllBuildingMenus("Industrial Buildings",EnumSet.of(BuildingType.INDUSTRIAL,BuildingType.CONSTRUCTION,BuildingType.ELECTRICITY,BuildingType.WATER));
        });

        // NOTE: "Other" has no JavaFX screen yet (was buildingManager.displayAllBuildings()
        // printed to console in the old terminal build). Disabled rather than left as a
        // dead click until that screen is ported.
        b4.setDisable(true);

        b0.setOnAction(e -> {
            // go back to main menu
            showStartMenu();
        });
        

        

        rootMenu.getChildren().addAll(gameInfo,b1,b2,b3,b4,b0);

        
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
                }
            }
        });

        Button back = new Button("Cancel");
        back.setOnAction(e -> handleAllBuildingMenus(menuTitle, categories));

        // 5. Assemble everything
        rootMenu.getChildren().addAll(totalLabel, buttonGrid, reset, confirm, back);
    }
    
    private void showQuickDebtMenu(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats) {
    clearMenu();

    double totalCost = game.calculateTotalCost(selected, quantity); // You'll need a getter for this
    double gap = totalCost - game.getCash();
    double rate = game.getDebtManager().getRate();
    double faceValue = Math.ceil((gap / (1 - rate)) / 1000.0) * 1000;
    double cashReceived = faceValue * (1 - rate);

    Label warning = new Label("INSUFFICIENT FUNDS");
    warning.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

    Label details = new Label(String.format(
        "Funding Required: $%s\nIssue T-Bill for: $%s\nCash to be Received: $%s\nRate: %.2f%%",
        formatter.format(gap), formatter.format(faceValue), formatter.format(cashReceived), rate * 100
    ));

    Button confirmDebt = new Button("Issue T-Bill");
    confirmDebt.setOnAction(e -> {
        // Move the logic from your old quickIssueDebt here
        game.issueEmergencyDebt(faceValue, 3); // Tell game to add the cash/debt
        game.buildStack(selected, quantity, false); // Try building again
        handleAllBuildingMenus(prevTitle, prevCats); // Go back to building list
    });

    Button cancel = new Button("Cancel Build");
    cancel.setOnAction(e -> handleAllBuildingMenus(prevTitle, prevCats));

    rootMenu.getChildren().addAll(warning, details, confirmDebt, cancel);
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
    
    private void showDebtAmountMenu(String type, int duration, double rounding) {
    clearMenu();
    
    final double[] requestedAmount = {0};
    Label amountLabel = new Label("Amount Requested: $0");
    amountLabel.setStyle("-fx-font-size: 16px;");

    javafx.scene.layout.FlowPane amountGrid = new javafx.scene.layout.FlowPane(10, 10);
    amountGrid.setAlignment(Pos.CENTER);

    // Increments based on the scale of the debt (roundingFactor)
    double[] increments = {rounding, rounding * 5, rounding * 10, rounding * 50};
    
    for (double inc : increments) {
        Button b = new Button("+$" + formatter.format(inc));
        b.setOnAction(e -> {
            requestedAmount[0] += inc;
            amountLabel.setText("Amount Requested: $" + formatter.format(requestedAmount[0]));
        });
        amountGrid.getChildren().add(b);
    }

    Button confirm = new Button("Confirm Issuance");
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

    rootMenu.getChildren().addAll(new Label("Issuing " + type + " (" + duration + " units)"), amountLabel, amountGrid, confirm, cancel);
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
        Button futureExpansion = new Button("[Future Expansion]");
        Button back = new Button("Back");

        futureExpansion.setDisable(true);

        commercial.setOnAction(e -> showCommercialInfoMenu());
        industrial.setOnAction(e -> showIndustrialInfoMenu());
        utility.setOnAction(e -> showUtilityInfoMenu());

        back.setOnAction(e -> showSectorMenu());

        rootMenu.getChildren().addAll(title, gameInfo, commercial, industrial, utility, futureExpansion, back);
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

        Button back = new Button("Back");
        back.setOnAction(e -> showSectorMenu());

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle("-fx-background-color:transparent;");

        rootMenu.getChildren().addAll(title, scrollPane, back);
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

    private void showSimulateResultMenu(int requested, int completed) {
        clearMenu();

        Label title = new Label("SIMULATION COMPLETE");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10;");

        Label result = new Label(completed + " of " + requested + " months simulated.");
        result.setStyle("-fx-font-size: 14px;");

        Label info = new Label("Now month " + game.getMonth()
                + " | Cash: $" + formatter.format(game.getCash()));

        rootMenu.getChildren().addAll(title, result, info);

        if (completed < requested) {
            Label warning = new Label("Stopped early - the treasury was empty.");
            warning.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
            rootMenu.getChildren().add(warning);
        }

        Button again = new Button("Simulate more");
        again.setOnAction(e -> showSimulateMonthsMenu());

        Button done = new Button("Done");
        done.setOnAction(e -> showStartMenu());

        rootMenu.getChildren().addAll(again, done);
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

        showSectorReport("MUNICIPAL UTILITIES REPORT", column, this::showPrivateSectorMenu);
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
                "  (cost of running the city's own utilities)"));

        column.getChildren().add(reportSection("NET EXPORTS (NX)",
                String.format("Food imported:         -$%s", formatter.format(na.getImportsFood())),
                String.format("Materials imported:    -$%s", formatter.format(na.getImportsMaterials())),
                "---------------------------------------------------",
                String.format("Net Exports:            $%s", formatter.format(na.getNetExports())),
                "  (nothing is exported yet, so this only subtracts)"));

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
                "---------------------------------------------------",
                String.format("Total Revenue:          $%s", formatter.format(na.getTotalRevenue()))));

        column.getChildren().add(reportSection("EXPENDITURE",
                String.format("Debt Interest:         -$%s", formatter.format(na.getInterestExpense())),
                String.format("Buildings (capital):   -$%s", formatter.format(na.getCapitalSpending())),
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
                eta = "stalled - no workers";
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
                statLine("Water", formatter.format(game.getWaterRatio() * 100) + "%"));

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
