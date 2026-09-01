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
    private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        game = new Game();

        //Initialize the core UI once
        this.rootMenu = new VBox(10);
        this.rootMenu.setAlignment(Pos.CENTER);
        this.scene = new Scene(rootMenu);


        
        stage.setTitle("City Simulator");
        stage.setMaximized(true);
        
        stage.setScene(scene);
        stage.show();

        // button actions
        showMainMenu();

        
    }
    
    private void showMainMenu() {
        rootMenu.getChildren().clear();
        
        

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
        rootMenu.getChildren().clear();
        Button b1 = new Button("Confirm");
        Button b0 = new Button("Cancel");
        
        b1.setOnAction(e -> game.saveGame());
        b0.setOnAction(e -> showMainMenu());
        
        rootMenu.getChildren().addAll(b1,b0);
    }

    private void showSettingsMenu() {
        rootMenu.getChildren().clear();

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
    rootMenu.getChildren().clear();
    
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

    // NOTE: these two had no setOnAction at all - silent dead buttons.
    // Population had terminal logic (Game.printPopulationInfo) but no JavaFX
    // screen yet; Simulate Multiple Months (Game.handleMultipleMonths) still
    // relies on the stubbed-out getInput() for its month count, so it can't
    // be wired safely until that's replaced with a JavaFX input control.
    // Disabling both until they're ported rather than leaving them as dead clicks.
    population.setDisable(true);
    simulateMultipleMonths.setDisable(true);

    rootMenu.getChildren().addAll(gameInfo, buildings, economy, population, nextMonth, simulateMultipleMonths, back);

    
}
    
    private void showBuildingsMenu() {
        rootMenu.getChildren().clear();
        
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
            handleAllBuildingMenus("Industrial Buildings",EnumSet.of(BuildingType.INDUSTRIAL,BuildingType.CONSTRUCTION,BuildingType.ELECTRICITY));
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
        rootMenu.getChildren().clear();
        
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
        
        Button b0 = new Button("Back");
        
        b1.setOnAction(e -> {
            // go back to main menu
            showFinanceMenu();
        });
        b3.setOnAction(e -> showDebtInfoMenu());
        b4.setOnAction(e -> showSectorMenu());

        // NOTE: "Restructure" (b2) was never implemented even in the old terminal
        // menu (its case was an empty stub) - leaving disabled. "Sector Info" (b4)
        // is now wired up below.
        b2.setDisable(true);

        b0.setOnAction(e -> {
            // go back to main menu
            showStartMenu();
        });
        

        

        rootMenu.getChildren().addAll(marketStatus,gameInfo,b1,b2,b3,b4, b0);

        
    }
    
    private void showFinanceMenu() {
        rootMenu.getChildren().clear();
        
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
        rootMenu.getChildren().clear();
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
        rootMenu.getChildren().clear();

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
    rootMenu.getChildren().clear();

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
    rootMenu.getChildren().clear();
    
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
    rootMenu.getChildren().clear();
    
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
            executeDebtLogic(type, requestedAmount[0], duration, rounding);
            showFinanceMenu(); // Return after issuing
        }
    });

    Button cancel = new Button("Cancel");
    cancel.setOnAction(e -> showFinanceMenu());

    rootMenu.getChildren().addAll(new Label("Issuing " + type + " (" + duration + " units)"), amountLabel, amountGrid, confirm, cancel);
}
    private void executeDebtLogic(String type, double amount, int duration, double rounding) {
    // These calls now go to your Game instance
    switch (type) {
        case "T-Bill" -> game.handleTBillLogic(amount, duration, rounding);
        case "Medium-Term" -> game.handleMediumBondLogic(amount, duration, rounding);
        case "Long-Term" -> game.handleLongBondLogic(amount, duration, rounding);
    }
}
    private void showSectorMenu() {
        rootMenu.getChildren().clear();

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
        Button utilities = new Button("Municipal Utility Services");
        Button systemOps = new Button("[System Operations]");
        Button back = new Button("Back");

        utilities.setDisable(true);
        systemOps.setDisable(true);

        population.setOnAction(e -> showPopulationInfoMenu());
        privateSector.setOnAction(e -> showPrivateSectorMenu());
        back.setOnAction(e -> showEconomyMenu());

        rootMenu.getChildren().addAll(title, gameInfo, population, privateSector, utilities, systemOps, back);
    }

    private void showPrivateSectorMenu() {
        rootMenu.getChildren().clear();

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

        // NOTE: Industrial/Utility screens aren't built yet.
        industrial.setDisable(true);
        utility.setDisable(true);

        commercial.setOnAction(e -> showCommercialInfoMenu());

        back.setOnAction(e -> showSectorMenu());

        rootMenu.getChildren().addAll(title, gameInfo, commercial, industrial, utility, futureExpansion, back);
    }

    private void showPopulationInfoMenu() {
        rootMenu.getChildren().clear();

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
        rootMenu.getChildren().clear();

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
                String.format("Energy Efficiency:      %.1f%%", ch.getReportEnergyRatio() * 100));

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
                String.format("    Local Imports:               %,d units", ch.getReportLocalImports()),
                String.format("    Global Imports:              %,d units", ch.getReportGlobalImports()),
                String.format("Electricity Expense:            -$%s", formatter.format(ch.getReportElectricityCost())),
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

        Button back = new Button("Back");
        back.setOnAction(e -> showPrivateSectorMenu());

        rootMenu.getChildren().addAll(title, gameInfo, scrollPane, back);
    }

    private void showDebtInfoMenu() {
        rootMenu.getChildren().clear();

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
    
    
    
    
    
    
    
    
    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }

}
