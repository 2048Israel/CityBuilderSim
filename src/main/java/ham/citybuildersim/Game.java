package ham.citybuildersim;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Scanner;
import java.util.List;
import java.util.EnumSet;
import java.util.ArrayList;
import java.util.Locale;

/**
 *
 * @author Jerus
 */
public class Game {
    
    private boolean isRunning;
    //private Scanner scanner;
    
    //dont use fields
    
    
    //Game state fields
    private int month;
    private double cash;
    private int population;   
    private int[] jobs = new int[JobType.values().length];
    private BuildingManager buildingManager;
    private EconomyManager economyManager;
    private PopulationManager populationManager;
    private ServicesManager servicesManager;
    private DataSave dataSave;
    private HistorySave historySave;
    private HistoryGrapher historyGrapher;
    private MenuManager menuManager;
    private DebtManager debtManager;
    
    private SimulationEngine simulationEngine;
    
    int materialsConsumed =0;
    
    //construction UI logic
    double totalMaterialsImported = 0;
    double totalBuildingCost = 0;
    private boolean hasNewReceipt = false;
    String lastBuildingName;
    
    /**
     * How much more a long-term bond costs in total than an equivalent
     * medium-term bond. The player buys lower monthly payments with a higher
     * all-in cost; this is the size of that trade.
     */
    private static final double LONG_BOND_COST_MULTIPLIER = 1.15;

    //settings
    boolean reports = true;
    boolean graphs = true;
    
    //boolean
    boolean initialized = false;
    
    public Game() {
        buildingManager = new BuildingManager();
        economyManager = new EconomyManager(buildingManager);
        populationManager = new PopulationManager();
        servicesManager = new ServicesManager(buildingManager);
        dataSave = new DataSave();
        historySave = new HistorySave();
        historyGrapher = new HistoryGrapher();
        menuManager = new MenuManager();
        debtManager = new DebtManager();
        
        simulationEngine = new SimulationEngine(
                economyManager,
                populationManager,
                servicesManager,
                buildingManager,
                debtManager);
        
        this.isRunning = true;
        //this.scanner = new Scanner(System.in);
        this.month = 1;
        this.cash = 300000;
        

    }
    
    public void run() {
        initialize();
    }
        
    private void initialize() {
        
        //initialize
        if(!initialized){
        buildingManager.initializeTemplates();
        dataSave.setBuildingNum(buildingManager.getTemplateCount());
        populationManager.setWagesPerType();
        buildingManager.setConstructionMaterials(80);
        initialized = true;
        }
 
    }
    
    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }
 
    private int getInput() {
         return 0;
    }
    

    //buttons
    public void newGame(){
        initialize();
        resetGame();
        
    }
    public void resumeGame(){
        // Just make sure templates/state are initialized; the JavaFX screen
        // that follows this call is responsible for displaying the game.
        // NOTE: this used to call handleStartGame(), a terminal-menu loop
        // that reads via getInput() (which always returns 0) - that froze
        // the JavaFX Application Thread forever the moment this button was clicked.
        initialize();
    }
    public void loadGameSave(){
        // NOTE: my earlier fix to resumeGame() (above) added initialize() so
        // buildingManager's templates exist before use, but I missed doing the
        // same here. If "Load Game" is the very first thing clicked in a session
        // (no New Game / Resume Game first), buildingManager.templates was never
        // populated, so loadGame()'s per-index template lookups would hit an
        // empty list. initialize() is idempotent (guarded by `initialized`), so
        // this is safe even if a game was already started.
        initialize();
        loadGame();
        simulationEngine.simulateMonth(this);
        // NOTE: previously also called handleStartGame() here, which caused
        // the same infinite-loop freeze described above.
    }
    public void saveGame(){
        save();
        System.out.println("Game successfuly saved.");
    }
    
    public void toggleQuit(){
        System.exit(0);
    }
    
    public void toggleGraphs(){
        // NOTE: previously called handleGraphSettings(), a terminal menu loop
        // that could never receive input and froze the app. This is a simple
        // on/off toggle, so it doesn't need a sub-menu at all.
        graphs = !graphs;
        System.out.println("Monthly graphs " + (graphs ? "enabled" : "disabled"));
    }
    public void toggleReports(){
        // NOTE: previously called handleReportSettings() - see toggleGraphs() above.
        reports = !reports;
        System.out.println("Monthly reports " + (reports ? "enabled" : "disabled"));
    }
    
    public void toggleNextMonth(){
        nextMonth();
    }
 
    private void handleStartGame() {
        boolean inMainMenu = true;

        while (inMainMenu) {
            menuManager.showMainMenu(month,cash,economyManager.getTotalIncome()+servicesManager.getServiceNetIncome());

            int choice = getInput();
            switch (choice) {
                case 1:
                    
                    break;
                case 2:
                    handleEconomyMenu();
                    break;
                case 3:
                    printPopulationInfo();
                    break;
                case 4:
                    nextMonth();
                    break;
                case 5:
                    handleMultipleMonths();
                    break;
                case 6:
                    inMainMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    public int getMonth(){
        return month;
    }
    public double getCash(){
        return cash;
    }
    public double getIncome(){
        double income = economyManager.getTotalIncome()+servicesManager.getServiceNetIncome();
        return income;
    }
    /**
     * This month's effective construction output, matching exactly what
     * SimulationEngine.simulateMonth() feeds to advanceConstruction() - base
     * capacity scaled by the construction sector's labour fill rate. Exposed so
     * the UI can show the player what their real build rate is, and how it gets
     * divided between concurrent sites.
     */
    /** Read-only passthrough for the city overview panel. */
    public double getEnergyRatio(){
        return servicesManager.getEnergyRatio();
    }

    public int getConstructionOutput(){
        double constructionFillRate = servicesManager.getConstructionHandler().getAverageFill();
        return (int) Math.round(buildingManager.getTotalConstructionCapacity() * constructionFillRate);
    }

    public int getConstructionMaterials(){
        int constructionMaterials = buildingManager.getConstructionMaterials();
        return constructionMaterials;
    }
    public double getInterestRate(){
        double interest = debtManager.getRate();
        return interest;
    }
    public boolean isGraphsEnabled(){
        return graphs;
    }
    public boolean isReportsEnabled(){
        return reports;
    }
    
    
    /**
     * Runs up to {@code months} monthly cycles, stopping early if the treasury is
     * empty. Returns how many months actually ran.
     *
     * NOTE: replaces the terminal-era handleMultipleMonths(), which read its month
     * count from the stubbed getInput() (always 0, so the loop never executed) and
     * then set graphs/reports back to true unconditionally - silently overwriting
     * whatever the player had chosen in Settings. This restores the previous
     * values in a finally block instead, so the setting survives even if a month
     * throws.
     *
     * Console output stays off for the duration: several hundred months of
     * reports and ASCII graphs is slow and unreadable.
     */
    public int simulateMonths(int months) {

        boolean previousGraphs = graphs;
        boolean previousReports = reports;

        graphs = false;
        reports = false;

        int completed = 0;

        try {
            for (int i = 0; i < months; i++) {
                if (cash <= 0) {
                    System.out.println("Treasury empty - simulated " + completed
                            + " of " + months + " months.");
                    break;
                }
                nextMonth();
                completed++;
            }
        } finally {
            graphs = previousGraphs;
            reports = previousReports;
        }

        return completed;
    }

    private void handleMultipleMonths() {
        System.out.println("How many months? ");
        int number = getInput();
        

        graphs = false;
        reports = false;
        for (int i = 0; i < number; i++) {
            if (cash > 0) {
                nextMonth();
            } else {
                
                System.out.println("out of cash, only simulated " + i + " months instead of " + number + " months.");
                break;
            }

        }
        graphs = true;
        reports = true;
        
    }
    private void handleBuildingsMenu() {
        boolean inBuildingsMenu = true;

        while (inBuildingsMenu) {
            menuManager.showBuildingsMenu(cash,buildingManager.getConstructionMaterials());

            int choice = getInput();

            switch (choice) {
                case 1:
                    handleResidentialMenu();
                    break;
                case 2:
                    handleCommercialMenu();
                    break;
                case 3:
                    handleIndustrialMenu();
                    break;
                case 4:
                    handleOtherMenu();
                    break;
                case 5:
                    inBuildingsMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    private void handleResidentialMenu(){
        handleBuildingMenu("Residential Buildings",EnumSet.of(BuildingType.RESIDENTIAL));
    }
    private void handleCommercialMenu(){
        handleBuildingMenu("Commercial Buildings",EnumSet.of(BuildingType.COMMERCIAL));
    }
    private void handleIndustrialMenu(){
        handleBuildingMenu("Industrial Buildings",EnumSet.of(BuildingType.INDUSTRIAL,BuildingType.CONSTRUCTION,BuildingType.ELECTRICITY));
    }
    
    //universal building Method
    private void handleBuildingMenu(String menuTitle, EnumSet<BuildingType> categories) {
        boolean inMenu = true;

        while (inMenu) {
            // Now it uses the categories passed into the method
            List<BuildingsTemplate> buildings = buildingManager.getTemplatesByCategory(categories);

            System.out.println("\n--- " + menuTitle + " ---");
            System.out.print("Cash: ");
            System.out.printf("%.2f%n", cash);
            System.out.println("Construction Materials: " + buildingManager.getConstructionMaterials());

            // Dynamic List based on the category
            for (int i = 0; i < buildings.size(); i++) {
                System.out.println((i + 1) + ". " + buildings.get(i).getName());
            }

            System.out.println((buildings.size() + 1) + ". Back");

            int choice = getInput();
            if (choice == (buildings.size() + 1)) {
                inMenu = false;
                continue; // Exit the loop
            }

            if (choice >= 1 && choice <= buildings.size()) {
                handleBuildingSelection(buildings.get(choice - 1));
            }
        }
    }
    
    private void handleBuildingSelection(BuildingsTemplate selected) {
        System.out.println("\n--- " + selected.getName() + " info ---");
        System.out.println("1. Build - Cost: " + selected.getCashCost() + " Materials Cost: " + selected.getConstructionMaterials());
        System.out.println("2. Info");
        System.out.println("3. Back");

        int choice = getInput();

        switch (choice) {
            case 1:
                System.out.print("How many? ");
                int quantity = getInput();
                processBuildOrder(selected, quantity); // Extracted one step further for clarity
                break;
            case 2:
                printBuildingInfo(selected.getName());
                break;
            case 3:
                return; // Just goes back to the previous list
            default:
                System.out.println("Invalid choice.");
        }
    }
    public boolean hasNewReceipt(){return hasNewReceipt;}
    public void clearReceipt(){ hasNewReceipt = false;}
    
    public double calculateTotalCost(BuildingsTemplate selected, int quantity) {
        double materialsPrice = buildingManager.getConstructionMaterialPrice();
        double constructionMaterials = buildingManager.getConstructionMaterials();
        double totalMaterials = selected.constructionMaterials * quantity;
        double neededMaterials = 0;

        if (constructionMaterials < totalMaterials) {
            neededMaterials = totalMaterials - constructionMaterials;
        }

        double totalCost = (selected.getCashCost() * quantity);
        totalCost += (neededMaterials * materialsPrice);

        // CRITICAL: Must include the manager's setup cost
        totalCost += buildingManager.getSetCost();

        return totalCost;
    }
    private void processBuildOrder(BuildingsTemplate selected, int quantity) {
        // 1. Calculate costs accurately
        double materialsPrice = buildingManager.getConstructionMaterialPrice();
        double currentMaterials = buildingManager.getConstructionMaterials();
        double totalMaterialsRequired = selected.constructionMaterials * quantity;

        double neededMaterials = 0;
        if (currentMaterials < totalMaterialsRequired) {
            neededMaterials = totalMaterialsRequired - currentMaterials;
        }

        // Combine EVERYTHING into totalCost
        double totalCost = (selected.getCashCost() * quantity);
        totalCost += (neededMaterials * materialsPrice);
        totalCost += buildingManager.getSetCost(); // Move this HERE

        // 2. Set stats for the UI Receipt
        this.totalMaterialsImported = neededMaterials;
        this.totalBuildingCost = totalCost;

        // 3. The Check
        if (totalCost <= cash) {
            this.hasNewReceipt = true;
            lastBuildingName = buildingManager.getName(selected);

            buildingManager.addStack(selected, quantity, false);
            materialsConsumed += totalMaterialsRequired;

            // 4. Subtract everything at once
            cash -= totalCost;

            System.out.println(quantity + " " + selected.getName()
                    + " construction started. $" + totalCost + " cash used");
        } else {
            // Now the debt issued will actually cover the FULL price including the setup fee
            System.out.println("Not enough Cash (Need " + formatter.format(totalCost)
                    + ", have " + formatter.format(cash) + ")");
            quickIssueDebt(selected, quantity, totalCost);
        }
    }

    
    
    public enum BuildResult {SUCCESS, NEEDS_FUNDING, FAILED}
    
    public BuildResult buildStack(BuildingsTemplate template, int quantity,boolean noConstuction){
        double totalCost = calculateTotalCost(template,quantity);
        
        if(totalCost <= cash){
            processBuildOrder(template,quantity);
            return BuildResult.SUCCESS;
        } else{
            return BuildResult.NEEDS_FUNDING;
        }
        
        
    }
    public double getMaterialsUsed(){
        return totalMaterialsImported;
    }
    public double getTotalBuildingCost(){
        return totalBuildingCost;
    }
    public String getBuildingName(){
        return lastBuildingName;
    }
    
    private void handleOtherMenu() {
        buildingManager.displayAllBuildings();
        
        
    }
    
    private void handleEconomyMenu() {
        boolean inEconomyMenu = true;
        
        while (inEconomyMenu) {
            menuManager.showEconomyMenu(month, cash);
            
            int choice = getInput();

            switch (choice) {
                case 1:
                    handleFinanceMenu();
                    break;
                case 2:
                    break;
                case 3:
                    FinanceInfo();
                    break;
                case 4:
                    handleSectorMenu();
                    break;
                case 5:
                    inEconomyMenu = false;
                    break;
                default:
                    System.out.print("Invalid choice.");
            }
        }
    }
    
    private void handleFinanceMenu(){
        boolean inFinanceMenu = true;
        
        while (inFinanceMenu) {
            menuManager.showFinanceMenu(month, cash,debtManager.getRate());
            int choice = getInput();
            

            switch (choice) {
                case 1:
                    addShortTermTBill();
                    break;
                case 2:
                    addMediumTermBond();
                    break;
                case 3:
                    addLongTermBond();
                    break;
                case 4:
                    inFinanceMenu = false;
                    break;
                default:
                    System.out.print("Invalid choice.");
            }
        }
    }
    private void handleSectorMenu(){
        boolean inSectorMenu = true;
        
        while (inSectorMenu) {
            menuManager.showSectorMenu(month, cash);
            int choice = getInput();
            
            switch (choice) {
                case 1:
                    printPopulationInfo();
                    break;
                case 2:
                    handlePrivateBusinesMenu();
                    printCommercialInfo();
                    break;
                case 3:
                    ;
                    break;
                case 4:
                    ;
                    break;
                case 5:
                    inSectorMenu = false;
                    break;
                default:
                    System.out.print("Invalid choice.");
            }
        }
    }
    
    private void handlePrivateBusinesMenu(){
        boolean inMenu = true;
        
        while (inMenu) {
            menuManager.showPrivateSectorMenu(month, cash);
            int choice = getInput();
            
            switch (choice) {
                case 1:
                    printCommercialInfo();
                    break;
                case 2:
                    printIndustrialInfo();
                    break;
                case 3:
                    printUtilityInfo();
                    break;
                case 4:
                    break;
                case 5:
                    inMenu = false;
                    break;
                default:
                    System.out.print("Invalid choice.");
            }
        }
    }
    
    private void addShortTermTBill() {
        issueDebtInstrument("T-Bill", 3, 12, 1000);
    }
    
    private void addMediumTermBond(){
        issueDebtInstrument("Medium-Term", 1, 10, 10000);
    }
    
    private void addLongTermBond() {
        issueDebtInstrument("Long-Term", 10, 50, 100000);

    }
    private void issueDebtInstrument(String type, int minDur, int maxDur, double roundingFactor) {
        System.out.printf("Must be %d to %d %s in length. Press 0 to exit.%nDuration: ",
                minDur, maxDur, (type.equals("T-Bill") ? "months" : "years"));

        int duration = 0;
        while (true) {
            duration = getInput();
            if (duration == 0) {
                System.out.println("Cancelled.");
                return;
            }
            if (duration >= minDur && duration <= maxDur) {
                break;
            }
            System.out.printf("Invalid. Must be %d to %d: ", minDur, maxDur);
        }

        System.out.print("How much: ");
        double requestedAmount = getInput();

        // Logic branches based on type
        switch (type) {
            case "T-Bill" ->
                handleTBillLogic(requestedAmount, duration, roundingFactor);
            case "Medium-Term" ->
                handleMediumBondLogic(requestedAmount, duration, roundingFactor);
            case "Long-Term" ->
                handleLongBondLogic(requestedAmount, duration, roundingFactor);
        }
    }
    
    
    public String handleTBillLogic(double amount, int duration, double rounding) {
        double rate = debtManager.getRate();
        // Discounting logic: The user wants $X, so we calculate the Face Value (amount) 
        // needed to get $X after the discount is taken.
        amount = Math.ceil((amount / (1 - rate)) / rounding) * rounding;
        double received = Math.round(amount * (1 - rate) * 100) / 100.0;

        debtManager.addShortTermTBill(amount, duration, month);
        this.cash += received;

        debtManager.updateInterest();
        return String.format("T-Bill Issued!\nFace Value: $%s\nCash Received: $%s\nTerm: %d months",
                formatter.format(amount), formatter.format(received), duration);
    }

    public String handleMediumBondLogic(double requestedAmount, int duration, double rounding) {
        double faceValue = Math.ceil(requestedAmount / rounding) * rounding;
        double annualRate = debtManager.getRate();
        double monthlyInterest = faceValue * (annualRate / 12.0);

        debtManager.addMediumTermBond(faceValue, duration * 12, month, annualRate);
        this.cash += faceValue;

        debtManager.updateInterest();
        return String.format("Medium Bond Issued!\nPrincipal: $%s\nMonthly Interest: $%s\nTerm: %d years",
                formatter.format(faceValue), formatter.format(monthlyInterest), duration);
    }

    /**
     * Long bonds pair a LOW monthly coupon with a redemption premium: you repay
     * more than you borrowed, but your monthly payment is well below a medium
     * bond's. The premium is what that cash-flow relief costs.
     *
     * The premium is solved so the all-in cost lands LONG_BOND_COST_MULTIPLIER
     * above an equivalent medium-term bond:
     *
     *     premium + (1 + premium) * couponYield * duration
     *         = LONG_BOND_COST_MULTIPLIER * marketRate * duration
     *
     * NOTE: the previous version grossed the face up by (1 + yield)^duration -
     * that discount already priced the full compounded interest - and then
     * LongTermBond.processMonth() charged a coupon on the grossed-up face on top.
     * The same interest was billed twice: a 30-year bond paid out $573,464 and
     * repaid $840,036, a 46% cost on a "0.67%" instrument, making long bonds
     * strictly worse than medium at every duration.
     */
    public String handleLongBondLogic(double amount, int duration, double rounding) {

        double marketRate = debtManager.getRate();

        // Yield curve: long money carries a lower coupon than the medium-term
        // market rate. Small monthly payments are the point of the instrument.
        double curveSlope = .00667;
        double smoothing = 30;
        double couponYield = (marketRate / 3) + (curveSlope * duration) / (duration + smoothing);

        double premium = (duration * (LONG_BOND_COST_MULTIPLIER * marketRate - couponYield))
                / (1 + couponYield * duration);
        premium = Math.max(premium, 0);

        double faceValue = Math.ceil((amount * (1 + premium)) / rounding) * rounding;
        double received = Math.round((faceValue / (1 + premium)) * 100) / 100.0;
        double monthlyInterest = (faceValue * couponYield) / 12;
        double totalCost = (faceValue - received) + (monthlyInterest * duration * 12);

        debtManager.addLongTermBond(faceValue, duration * 12, month, couponYield);
        this.cash += received;

        debtManager.updateInterest();

        return String.format(
                "Long Bond Issued!%nCash Received: $%s%nRepay at Maturity: $%s%n"
                + "Monthly Interest: $%s%nTotal Cost of Credit: $%s%nTerm: %d years @ %.2f%%",
                formatter.format(received), formatter.format(faceValue),
                formatter.format(monthlyInterest), formatter.format(totalCost),
                duration, couponYield * 100);
    }
    
    /*
    private void handleTBillLogic(double amount, int duration, double rounding) {
        double rate = debtManager.getRate();
        amount = Math.ceil((amount / (1 - rate)) / rounding) * rounding;
        double received = Math.round(amount * (1 - rate) * 100) / 100.0;

        System.out.println(
                "A T-Bill of " + formatter.format(amount)
                + " with duration of " + duration
                + " months was issued for " + formatter.format(received));
        debtManager.addShortTermTBill(amount, duration, month);
        cash += received;
    }
    
    private void handleMediumBondLogic(double requestedAmount, int duration, double rounding) {
        // 1. Standardize the Face Value by rounding up (e.g., to the nearest 10,000)
        double faceValue = Math.ceil(requestedAmount / rounding) * rounding;

        // 2. Calculate the monthly interest payment for the user's information
        double annualRate = debtManager.getRate();
        double monthlyInterest = faceValue * (annualRate / 12.0);

        System.out.println(
                "A Medium Term Bond of " + formatter.format(faceValue)
                + " with duration of " + duration
                + " years was issued for " + formatter.format(faceValue)
                + ", monthly interest: " + formatter.format(monthlyInterest));

        debtManager.addMediumTermBond(faceValue, duration * 12, month, annualRate);

        
        cash += faceValue;

        System.out.println("Funds of " + formatter.format(faceValue) + " added to treasury.");
    }

    private void handleLongBondLogic(double amount, int duration, double rounding) {
        // Your specific Yield Curve math here
        double curveSlope = .00667;
        double smoothing = 30;
        double yield = (debtManager.getRate() / 3) + (curveSlope * duration) / (duration + smoothing);
        double multiplier = Math.pow(yield + 1, duration);

        amount = Math.ceil((amount * multiplier) / rounding) * rounding;
        double received = Math.round((amount / multiplier) * 100) / 100.0;

        System.out.println(
                "A Long Term Bond of " + formatter.format(amount)
                + " with duration of " + duration
                + " years was issued for " + formatter.format(received)
                + ", monthly interest: " + formatter.format((amount*yield)/12));
        debtManager.addLongTermBond(amount, duration * 12, month, yield);
        cash += received;
    }
    */
    
    private void FinanceInfo(){
        debtManager.printDebtInfo(month);
    }
    
    
    //printers
    private void printPopulationInfo(){
        populationManager.printPopulationInfo();
    }
    
    private void printCommercialInfo(){
        economyManager.printCommercialInfo();
    }
    
    private void printIndustrialInfo(){
        economyManager.printIndustrialInfo();
    }
    
    private void printUtilityInfo(){
        servicesManager.printUtilityInfo();
    }
    
    private void printConstructionInfo(){
        servicesManager.printConstructionInfo();
    }
    
    private void printCityStats(){
        economyManager.printCityStats();
    }

    
    
    
    private void nextMonth() {
        updateConstructionCost();

        if (cash < 0) {
            double gap = -cash; // The actual negative amount
            double rate = debtManager.getRate();

            // 1. Calculate Face Value needed so the cash received covers the gap
            // Formula: Face Value = Gap / (1 - rate)
            double faceValue = gap / (1 - rate);

            // 2. Round up to nearest 1000 for market standardization
            faceValue = Math.ceil(faceValue / 1000.0) * 1000;

            // 3. Calculate actual cash hitting the bank (Face Value minus the "Interest/Discount")
            double cashReceived = faceValue * (1 - rate);

            // Record the debt (The city owes the full faceValue in 4 months)
            debtManager.addShortTermTBill(faceValue, 4, month);

            // Add the discounted cash to the wallet
            cash += cashReceived;

            System.out.println("Emergency Funding: Issued $" + faceValue + " T-Bill to cover deficit.");
        }
        month++;
        startOfMonthUpdate();
        simulationEngine.simulateMonth(this);
        finalUpdateEconomy();
        economyManager.setPreviousGdp(historySave);
        debtManager.setGDP(economyManager.getMonthGdp());
        debtManager.processAllDebts(this);
        
        dataSave.setCash(cash);
        printEndOfTurn();
        recordMonth();
        
    }
    
    private void startOfMonthUpdate(){

        // NOTE: the commercial sector's monthly income statement used to be
        // calculated as a side effect of printCommercialInfo(), which
        // printStartOfMonth() only calls `if(reports)`. So switching reports OFF
        // - which handleMultipleMonths() does automatically for batch sims -
        // silently zeroed commercial sales tax and the commercial contribution
        // to GDP, and left commercialCash/realEstateCash frozen.
        //
        // The calculation now runs here, unconditionally, before anything reads
        // it (calculateSalesTax() and getMonthGdp() both do). printCommercialInfo()
        // is a pure printer now, so the reports flag only controls output.
        economyManager.updateCommercialReport();

        printStartOfMonth();
        updateConstruction();
        economyManager.startOfMontEconUpdate();
    }

    private void handleGraphSettings(){
        
        boolean inMenu = true;
        while(inMenu){
            System.out.println("Show monthly graphs?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            System.out.print("choose");
            int choice = getInput();
            switch(choice){
                case 1:
                    graphs = true;
                    System.out.println("Monthly graphs enabled");
                    inMenu = false;
                    break;
                case 2:
                    graphs = false;
                    System.out.println("Monthly graphs disabled");
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid Input");
                    
                    
            }
        }
    }
    private void handleReportSettings(){
        
        boolean inMenu = true;
        while(inMenu){
            System.out.println("Show reports graphs?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            System.out.print("choose");
            int choice = getInput();
            switch(choice){
                case 1:
                    reports = true;
                    System.out.println("Monthly reports enabled");
                    inMenu = false;
                    break;
                case 2:
                    reports = false;
                    System.out.println("Monthly reports disabled");
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid Input");
                    
                    
            }
        }
    }
    private void printStartOfMonth() {
        System.out.println("\n================================================================================================================================================================");
        System.out.println("\n================================================================================================================================================================");
        System.out.println("\n================================================================================================================================================================");
        
        if(reports){
            printPopulationInfo();
            economyManager.printWageTaxInfo();
            printCommercialInfo();
            printIndustrialInfo();
            printUtilityInfo();
            printConstructionInfo();
            FinanceInfo();
        }
        printCityStats();
        System.out.println("Interest Rate: %" + formatter.format(debtManager.getRate()*100));
        if(graphs){
            historyGrapher.printLineGraph(historySave.getDebt(),historySave.getMonth(), "DEBT");
            historyGrapher.printLineGraph(historySave.getInterestRate(),historySave.getMonth(), "INTEREST RATE");
            //historyGrapher.printLineGraph(historySave.getJobs(),historySave.getMonth(), "Jobs");
            //historyGrapher.printLineGraph(historySave.getPopulation(),historySave.getMonth(), "POPULATION");
            //historyGrapher.printLineGraph(historySave.getWorkforce(),historySave.getMonth(), "WORKFORCE");
            historyGrapher.printLineGraph(historySave.getGdp(),historySave.getMonth(), "GDP HISTORY");
            
            
        }
        
    }
    
    
    
    private void finalUpdateEconomy(){
        economyManager.setDebt(debtManager.getAllPrincipal());
        double tempCash = cash;
        tempCash += economyManager.getTotalIncome();
        economyManager.setUtilityIncome(servicesManager.getServiceNetIncome());
        tempCash += servicesManager.getServiceNetIncome();
        economyManager.finalEconUpdate();
        servicesManager.updateServices();
        economyManager.setPricePerWatt(servicesManager.getPricePerWatt());
        
        if (Double.isFinite(tempCash)) {
            cash = tempCash;
        } else {
            System.out.println("Cash update blocked due to invalid value.");
        }
        materialsConsumed = 0;
    }
    
    
    private void updateConstructionCost(){
        buildingManager.finalUpdateBuildings();
        
    }
    private void updateConstruction() {
        // NOTE: this used to call buildingManager.advanceConstruction() here too,
        // duplicating the call that SimulationEngine.simulateMonth() makes right
        // after this method runs (both in the same nextMonth() cycle) - so every
        // month's construction capacity was being applied twice, making buildings
        // finish roughly 2x faster than their construction points implied.
        // Removed; SimulationEngine.simulateMonth() is now the single source of
        // truth for advancing construction each month.
    }
    void updatePopulation() {
        //TBE
        for (JobType job : JobType.values()) {
            jobs[job.ordinal()] = buildingManager.getTotalJobs(job);
        }
        population = populationManager.updatePop(getHouseholdCapacity());
        
    }
    
    private void shutdown() {
        System.out.println("Game exited.");
        isRunning = false;
    }
    
    private void printBuildingInfo(String name){
        BuildingsTemplate selected = buildingManager.getTemplateByName(name);
        if (selected != null){
            System.out.println("Name: " + selected.getName());
            System.out.println("Category: " + selected.getCategory());
            System.out.println("Cost: " + selected.getCashCost());
            System.out.println("Construction Cost: " + selected.getConstructionPoints());
            System.out.println("Upkeep: " + selected.getUpkeep());
            System.out.println("Construction Materials: " + selected.getConstructionMaterials());
            System.out.println("Capacity: " + selected.getCapacity());
            System.out.println("Coverage: " + selected.getCoverage());
            System.out.println("Total Jobs: " + selected.getTotalJobs());
            System.out.println("Production 1: " + selected.getProduction1());
            System.out.println("Production 2: " + selected.getProduction2());
            System.out.println("Production Modifier 1: " + selected.getProductionModifier1());
            System.out.println("Production Modifier 2: " + selected.getProductionModifier2());
            System.out.println("Nationalized: " + selected.getNationalized());
            System.out.println("Electricity Consumption: " + selected.getElectricityConsumption());
            
            
        }
    }
    
    public int getHouseholdCapacity() {
        return buildingManager.getTotalHouseCapacity();

    }
    public int getStoreCapacity() {
        return buildingManager.getTotalStoreCoverage();

    }
    public int[] getJobs(){
        return buildingManager.getTotalJobs();
    }
   
    
    public BuildingManager getBuildingManager() {
        return buildingManager;
    }
    // Needed by the JavaFX sector screens. Safe to expose now that the report
    // getters on CommercialHandler are all pure reads - the UI cannot mutate
    // economy state through this.
    public EconomyManager getEconomyManager() {
        return economyManager;
    }
    public PopulationManager getPopulationManager() {
        return populationManager; //PopulationManager;
    }


    
    //save stuff
    public void recordMonth() {
    historySave.recordMonth(
            month, // int, no rounding needed
            Math.round(cash * 100.0) / 100.0, // round to 2 decimals
            Math.round(economyManager.getMonthGdp() * 100.0) / 100.0,
            Math.round(debtManager.getAllPrincipal() * 100.0) / 100.0,
            Math.round(debtManager.getRate() * 10000.0) / 10000.0, // store rate as 2 decimals
            populationManager.getTotalJobs(), // int, no rounding
            populationManager.getWorkforce(), // int, no rounding
            populationManager.getPopulation() // int, no rounding
    );
}
    public void save(){
        
        sendBuildingSave();
        // NOTE: cash wasn't explicitly set here - it only ended up correct because
        // nextMonth() happens to call dataSave.setCash(cash) as a side effect on
        // its way through. That means saving right after starting a new game,
        // before ever advancing a month, would have saved cash as 0/default
        // instead of the real starting cash. Setting it explicitly here so save()
        // doesn't depend on an unrelated method having run first.
        dataSave.setCash(cash);
        dataSave.setMonth(month);
        dataSave.setDebt(debtManager.getDebt());
        dataSave.setProgress(buildingManager.getConstructionProgress());
        dataSave.setUnderConstruction(buildingManager.getUnderConstructionArray());
        dataSave.setConstructionMaterials(buildingManager.getConstructionMaterials());
        dataSave.setStoreInventory(economyManager.getStoreInventory());
        dataSave.setIndustryFoodInventory(economyManager.getIndustryFoodInventory());
        dataSave.setPopulation(populationManager.getPopulation());
        dataSave.setCommercialCash(economyManager.getCommercialCash());
        dataSave.setRealEstateCash(economyManager.getRealEstateCash());
        dataSave.setIndustrialCash(economyManager.getIndustrialCash());
        // NOTE: reports/graphs settings were never saved at all - they'd silently
        // reset to their true/true defaults on every load.
        dataSave.setReports(reports);
        dataSave.setGraphs(graphs);
        dataSave.saveGame();
        historySave.saveHistory();
    }
    
    public void sendBuildingSave() {

    int totalTemplates = buildingManager.getTemplateCount();

    for (int i = 0; i < totalTemplates; i++) {

        BuildingsTemplate template = buildingManager.getTemplate(i);

        if (template != null) {
            dataSave.setBuildingQuantity(
                template.getId(),
                buildingManager.getQuantity(i)
            );

           
        }
    }
}

    
    
    public void loadBuildings() {

        int totalTemplates = buildingManager.getTemplateCount();

        for (int i = 0; i < totalTemplates; i++) {
            int quantity = 0;

            if (i < dataSave.getBuildingsLength()) {
                quantity = dataSave.getBuildingQuantity(i);
            }

            buildingManager.addStack(
                    buildingManager.getTemplate(i),
                    quantity, true);
        }
    }

    public void resetBuildingManager() {
        buildingManager.resetBuildingManager();
        
    }
    
    public void resetEconomyManager(){
        economyManager.resetEconomyManager();
    }
    
    public void resetPopulationManager(){
        populationManager.resetPopulationManager();
    }
    
    public void unloadDebt(){
        debtManager.clearDebts();
    }
    
    public void resetGame(){
        cash = 300000;
        month = 1;
        population = 0;
        resetBuildingManager();
        resetEconomyManager();
        resetPopulationManager();
        unloadDebt();
        
    }
    
    //calculations
    
    public void subtractCash(double amount){
        cash -= amount;
    }
    public void InterestExpense(double amount){
        economyManager.updateInterestExpense(amount);
    }
    
    public DebtManager getDebtManager(){
        return debtManager;
    }
    
    
    public void printEndOfTurn(){
        System.out.println("\n--- Month " + month + "---");
        System.out.println("Construction Capacity: " + buildingManager.getTotalConstructionCapacity());
    }
    
    public void quickIssueDebt(BuildingsTemplate selected, int quantity, double totalCost) {
        // 1. Calculate the actual cash gap (How much we are short)
        double gap = totalCost - cash;

        System.out.println("Issue T-Bill to obtain funding? \nFunding required: " + formatter.format(gap));
        System.out.println("1. Yes\n2. No.");

        if (getInput() == 1) {
            int duration = 3;
            double rate = debtManager.getRate();

            // 2. Calculate Face Value needed to cover the gap after the discount
            double faceValue = gap / (1 - rate);

            // 3. Round up to nearest 1000
            faceValue = Math.ceil(faceValue / 1000.0) * 1000;

            // 4. Actual cash hitting the bank account
            double cashReceivedFromBill = faceValue * (1 - rate);

            System.out.println("A T-Bill of $" + formatter.format(faceValue)
                    + " with duration of " + duration
                    + " was issued for $" + formatter.format(cashReceivedFromBill) + ".");

            // Record the debt
            debtManager.addShortTermTBill(faceValue, duration, month);

            // Add the new money to the wallet
            cash += cashReceivedFromBill;

            // 5. NOW perform the build action since we have the funds
            buildingManager.addStack(selected, quantity, false);
            cash -= totalCost; // This should now work without going negative

            System.out.println(quantity + " " + selected.getName() + " construction started.");
        }
    }
    
    
   public void issueEmergencyDebt(double faceValue, int duration){
       double rate = debtManager.getRate();

            

            // 3. Round up to nearest 1000
            faceValue = Math.ceil(faceValue / 1000.0) * 1000;

            // 4. Actual cash hitting the bank account
            double cashReceivedFromBill = faceValue * (1 - rate);

            System.out.println("A T-Bill of $" + formatter.format(faceValue)
                    + " with duration of " + duration
                    + " was issued for $" + formatter.format(cashReceivedFromBill) + ".");

            // Record the debt
            debtManager.addShortTermTBill(faceValue, duration, month);

            // Add the new money to the wallet
            cash += cashReceivedFromBill;
   }
    
   private void rebuildSimulationState() {

    // rebuild jobs from buildings
    for (JobType job : JobType.values()) {
        jobs[job.ordinal()] = buildingManager.getTotalJobs(job);
    }

    // population sync
    // NOTE: this line used to be setPopulation(getPopulation()) - a no-op that
    // assigned the field to itself. It looked like it was restoring population
    // state but did nothing, and left workforce at 0 for the whole load path.
    // Must run before the getJobFillRate() reads below, which depend on workforce.
    populationManager.recomputeWorkforce();
    populationManager.updateJobs(jobs);
    populationManager.UpdateTotalWagePerType();

    // services sync
    servicesManager.updateServiceWages(populationManager.getWagesPerType());
    servicesManager.updateJobFillRate(populationManager.getJobFillRate());
    servicesManager.updateServices();

    // economy sync
    economyManager.setPopulation(populationManager.getPopulation());
    economyManager.setHouseholds(getHouseholdCapacity());
    economyManager.setTotalJobs(populationManager.getTotalJobs());
    economyManager.setTotalWage(populationManager.getTotalWage());
    economyManager.setEnergyRatio(servicesManager.getEnergyRatio());

    economyManager.updateIndustrialWages(populationManager.getWagesPerType());
    economyManager.updateStoreWages(
            populationManager.getWagesPerType(),
            buildingManager.getJobArrayPerCategory(BuildingType.COMMERCIAL)
    );

    economyManager.updateJobFillRate(populationManager.getJobFillRate());

    economyManager.updateEcon();

    servicesManager.updateServices();

    economyManager.setPricePerWatt(servicesManager.getPricePerWatt());

    economyManager.finalEconUpdate();

    // Recompute the commercial report from the restored state so a freshly loaded
    // save shows real numbers on the sector screen before the first month is
    // simulated. computeMonthlyReport() deliberately does NOT accumulate
    // commercialCash/realEstateCash - those were already restored from the save,
    // and banking another month here would drift them.
    //
    // NOTE: this must run LAST. It was originally placed right after updateEcon(),
    // where averageStoreFill is still 0 - and since both gross revenue and payroll
    // are multiplied by it, a freshly loaded game reported $0 revenue on a city
    // that was plainly selling 169 units. finalEconUpdate() is also what populates
    // the import counts and electricity draw that the expense lines need.
    economyManager.refreshCommercialReport();

    // GDP reads commercialHandler.getNetIncome(), so it has to come after the
    // refresh above to see this month's figure rather than a stale one.
    debtManager.setGDP(economyManager.getMonthGdp());
    debtManager.updateInterest();
}
   
   
   
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // Load game
    public void loadGame() {

        try {
            String userHome = System.getProperty("user.home");
            Path path = Path.of(userHome, "YourGame", "save.json");

            if (!Files.exists(path)) {
                System.out.println("Not found");
                return;
            }

            String json = Files.readString(path);
            Gson gson = new Gson();

            // Deserialize normally
            DataSave loaded = gson.fromJson(json, DataSave.class);

            /* Reset current state
            buildingManager.clearStacks();
            debtManager.clearDebts();
            */
            // Load simple fieldsS
            this.cash = loaded.getCash();
            this.month = loaded.getMonth();
            buildingManager.setConstructionMaterials(loaded.getConstructionMaterials());
            economyManager.setStoreInventory(loaded.getStoreInventory());
            economyManager.setIndustryFoodInventory(loaded.getIndustryFoodInventory());
            populationManager.setPopulation(loaded.getPopulation());
            economyManager.setCommercialCash(loaded.getCommercialCash());
            economyManager.setRealEstateCash(loaded.getRealEstateCash());
            economyManager.setIndustrialCash(loaded.getIndustrialCash());
            this.reports = loaded.getReports();
            this.graphs = loaded.getGraphs();

            // Load buildings
            for (int i = 0; i < loaded.getBuildingsLength(); i++) {

                int quantity = loaded.getBuildingQuantity(i);

                if (quantity > 0) {
                    BuildingsTemplate template = buildingManager.getTemplate(i);
                    buildingManager.addStack(template, quantity, true);
                }
            }
            
            //Load construction progress
            double[] progress = new double[loaded.getProgressLength()];
            for (int i = 0; i < loaded.getProgressLength(); i++) {

                progress[i] = loaded.getProgress(i);

            }
            
            buildingManager.setConstructionProgress(progress);
            //Load under Construction
            int[] quantity = new int[loaded.getUnderConstructionLength()];
            for (int i = 0; i < loaded.getUnderConstructionLength(); i++) {

                quantity[i] = loaded.getUnderConstruction(i);

            }
            
            buildingManager.setUnderConstructionArray(quantity);
            

            // Load debts manually
            JsonArray debtArray = loaded.getDebt();
            List<Debt> loadedDebts = new ArrayList<>();

            if (debtArray != null) {
                for (JsonElement element : debtArray) {

                    JsonObject obj = element.getAsJsonObject();
                    String type = obj.get("type").getAsString();

                    switch (type) {

                        case "T-BILL":
                            loadedDebts.add(gson.fromJson(obj, ShortTermTBill.class));
                            break;
                        case "MEDIUM-BOND":
                            loadedDebts.add(gson.fromJson(obj, MediumTermBond.class));
                            break;
                        case "LONG-BOND":
                            loadedDebts.add(gson.fromJson(obj, LongTermBond.class));
                            break;
                    }

                    // future types go here
                }
            }

            debtManager.setDebt(loadedDebts);
            loadHistory();

            System.out.println("Game loaded successfully.");

        } catch (IOException e) {
            System.out.println("Failed to load save file.");
        }
        
        rebuildSimulationState();
    }


    public void loadHistory() {

        try {
            String userHome = System.getProperty("user.home");
            Path path = Path.of(userHome, "YourGame", "history.json");

            if (!Files.exists(path)) {
                System.out.println("Not found");
                return;
            }

            String json = Files.readString(path);
            Gson gson = new Gson();

            // Deserialize normally
            HistorySave loaded = gson.fromJson(json, HistorySave.class);

            historySave.setGdp(loaded.getGdp());
            historySave.setCash(loaded.getCash());
            historySave.setMonth(loaded.getMonth());
            historySave.setPopulation(loaded.getPopulation());
            historySave.setWorkforce(loaded.getWorkforce());
            historySave.setJobs(loaded.getJobs());
            historySave.setInterestRate(loaded.getInterestRate());
            historySave.setDebt(loaded.getDebt());

            

            System.out.println("History loaded successfully.");

        } catch (IOException e) {
            System.out.println("Failed to load save file.");
        }
        
        rebuildSimulationState();
    }
}
 


 
   