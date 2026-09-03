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

    /**
     * Where saves live and how they are written. One instance for the whole
     * game: the path used to be spelled out separately in DataSave, HistorySave
     * and twice more down in the load methods, which is four places to get it
     * wrong and no way to notice when one of them drifts.
     */
    private final GameFiles gameFiles;
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
        this(new GameFiles());
    }

    /**
     * Lets a test point the game at a temporary folder.
     *
     * Not a nicety: without it, any check that exercises save and load would
     * write over the player's real city, which means the save path - the one
     * place where a bug is unrecoverable - would be the one place nothing dared
     * test end to end.
     */
    public Game(GameFiles gameFiles) {
        this.gameFiles = gameFiles;
        buildWorld();
    }

    /**
     * Builds the entire simulation from nothing.
     *
     * Called by the constructor AND by newGame(), which is the whole point.
     *
     * "Start New Game" used to call a resetGame() that cleared the fields
     * somebody had remembered to add to it, and the list had fallen a long way
     * behind: a new city inherited $81,777k of construction cash, $15,402k of
     * business debt, 1,868 units of the previous city's food sitting in shops
     * that no longer existed, 122 months of someone else's graph history, and a
     * GDP of $590/month with nothing built. Twenty-three fields in all.
     *
     * That is not a bug you fix by extending the list, because the list is the
     * bug - every field added anywhere in the game is a new chance to forget.
     * Rebuilding the object graph means a new game is identical to a freshly
     * started one BY CONSTRUCTION, and no field added in future can leak.
     *
     * Safe because the UI holds a reference to Game and nothing below it - every
     * screen reads through getters when it draws.
     */
    private void buildWorld() {

        buildingManager = new BuildingManager();
        economyManager = new EconomyManager(buildingManager);
        populationManager = new PopulationManager();
        servicesManager = new ServicesManager(buildingManager);
        dataSave = new DataSave();
        historySave = new HistorySave();
        historyGrapher = new HistoryGrapher();
        menuManager = new MenuManager();
        debtManager = new DebtManager();
        businessInvestment = new BusinessInvestment(buildingManager, economyManager);

        // The economy needs to reach construction's books: it is a business with
        // cash, credit and an income statement now, but it lives under
        // ServicesManager with the other municipal services.
        economyManager.setConstructionHandler(servicesManager.getConstructionHandler());
        
        simulationEngine = new SimulationEngine(
                economyManager,
                populationManager,
                servicesManager,
                buildingManager,
                debtManager);
        
        landManager = new LandManager();
        demolitionLog = new DemolitionLog();
        skipReport = new TimeSkipReport();
        households = new HouseholdAccounts();
        lastInvestment = new java.util.LinkedHashMap<>();

        this.isRunning = true;
        this.month = 1;
        this.cash = 300000;
        this.population = 0;
        this.jobs = new int[JobType.values().length];

        this.materialsConsumed = 0;
        this.totalMaterialsImported = 0;
        this.totalBuildingCost = 0;
        this.hasNewReceipt = false;
        this.lastBuildingName = null;
        this.cityInterestPaid = 0;
        this.monthsSinceAutosave = 0;

        this.lastSaveResult = null;
        this.loadFailure = null;

        this.reports = true;
        this.graphs = true;
    }
    
    public void run() {
        initialize();
    }
        
    private void initialize() {
        
        //initialize
        if(!initialized){
        buildingManager.initializeTemplates();

        // Sized by the highest id, not by how many templates there are. The two
        // are equal today only because buildings.json happens to number 0..12
        // with none missing; delete one building from that file and the count
        // drops while the ids do not, and setBuildingQuantity() starts throwing
        // on the highest id mid-save.
        dataSave.setBuildingNum(buildingManager.getMaxTemplateId() + 1);
        populationManager.setWagesPerType();
        buildingManager.setConstructionMaterials(80);

        // Has to happen before anything loads or saves, and this is the one
        // place guaranteed to run first: both resumeGame() and loadGameSave()
        // call initialize(), and it is guarded so it only ever runs once.
        // Copies, never moves - the old folder is left where it is.
        for (String file : gameFiles.migrateLegacy()) {
            System.out.println("Brought " + file
                    + " over from the old save folder into " + gameFiles.getDirectory());
        }

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

        // Rebuild rather than reset. See buildWorld() for the twenty-three
        // fields the old reset was missing and why the list itself was the bug.
        buildWorld();

        // buildWorld() made a new BuildingManager, so the templates have to be
        // loaded into it. initialize() is guarded, so the flag has to drop first
        // or it would quietly do nothing and leave a city with no buildings in
        // its catalogue.
        initialized = false;
        initialize();
    }
    public void resumeGame(){
        // Just make sure templates/state are initialized; the JavaFX screen
        // that follows this call is responsible for displaying the game.
        // NOTE: this used to call handleStartGame(), a terminal-menu loop
        // that reads via getInput() (which always returns 0) - that froze
        // the JavaFX Application Thread forever the moment this button was clicked.
        initialize();
    }
    public void loadGameSave(int slot){
        // NOTE: my earlier fix to resumeGame() (above) added initialize() so
        // buildingManager's templates exist before use, but I missed doing the
        // same here. If "Load Game" is the very first thing clicked in a session
        // (no New Game / Resume Game first), buildingManager.templates was never
        // populated, so loadGame()'s per-index template lookups would hit an
        // empty list. initialize() is idempotent (guarded by `initialized`), so
        // this is safe even if a game was already started.
        initialize();

        /*
         * loadGame() is the whole load, rebuild included. There used to be a
         * second pass here - originally simulateMonth(), which also advanced
         * construction and so handed every building site a month of free work
         * every time a save was opened; then a construction-free version of the
         * same thing, which still re-derived everything loadGame() had just
         * finished restoring and quietly overwrote the month's flows with
         * figures recomputed from the closing balances.
         *
         * Two rebuild passes were always one too many. There is one now.
         */
        loadGame(slot);
        // NOTE: previously also called handleStartGame() here, which caused
        // the same infinite-loop freeze described above.
    }
    /**
     * Returns what actually happened rather than announcing success regardless.
     *
     * The old version called save() and then printed "Game successfuly saved."
     * unconditionally - including on the path where DataSave had caught an
     * IOException and given up. A player told their city is safe stops making
     * their own copies, so a save that lies is worse than one that plainly
     * fails.
     */
    public GameFiles.Result saveGame(int slot, String slotName){
        save(slot, slotName);
        GameFiles.Result result = getLastSaveResult();
        System.out.println(GameFiles.slotLabel(slot) + ": " + result.message());
        return result;
    }

    /** Saves to a slot, keeping whatever name that slot already carried. */
    public GameFiles.Result saveGame(int slot){
        SaveHeader existing = gameFiles.readHeader(slot);
        return saveGame(slot, (existing == null) ? null : existing.getSlotName());
    }
    
    public void toggleQuit(){
        // Before the window goes, not after. This is the accident that actually
        // costs people cities.
        autosave("on quit");
        System.exit(0);
    }

    /** Why the last load did not happen, or null. Read by the UI. */
    private String loadFailure;

    public String getLoadFailure() { return loadFailure; }
    
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

    public double getWaterRatio(){
        return servicesManager.getWaterRatio();
    }

    public LandManager getLandManager(){
        return landManager;
    }

    /** Annexes one block if the city can afford it. @return true if bought. */
    public boolean buyLandBlock(){
        double cost = landManager.buyBlock(cash);
        if (cost <= 0) {
            return false;
        }
        cash -= cost;
        return true;
    }

    public BusinessInvestment getBusinessInvestment(){
        return businessInvestment;
    }

    public String getLastInvestment(String sector){
        return lastInvestment.getOrDefault(sector, "");
    }

    /* =======================================================================
       PRIVATE INVESTMENT

       Once a month, each business decides whether to expand. The order matters:
       this runs AFTER settleBusinessCredit(), so a sector's cash is settled and
       its credit repriced before it commits to anything.

       Nothing here can build utilities or water. Those are municipal, they have
       no cash of their own, and they are the player's job.
       ======================================================================= */
    private void runPrivateInvestment(){

        businessInvestment.recordMonth(populationManager.getPopulation());

        // Shrinking is decided before growing. A sector cannot sensibly do both
        // in one month, and running retirement first means a firm that has just
        // sold capacity is not immediately asked whether it wants more.
        runRetirement();
        lastWriteOff = economyManager.settleInsolvency();

        double constructionOutput = getConstructionOutput();
        BusinessDebtManager credit = economyManager.getBusinessDebtManager();
        CommercialHandler ch = economyManager.getCommercialHandler();
        IndustrialHandler ih = economyManager.getIndustrialHandler();

        // Refreshed before each sector, not once for all four: consider() builds
        // immediately, so real estate taking the last of the land has to be
        // visible to retail when retail plans a moment later. Reading it once
        // would have the later sectors planning against land that is already
        // spoken for, and then failing silently inside buildFor().
        refreshLand();
        consider(businessInvestment.planRealEstate(
                populationManager.getTotalJobs(),
                getHouseholdCapacity(),
                ch.getRentPrice(),
                constructionOutput,
                buildingManager.getUnderConstructionByCategory(BuildingType.RESIDENTIAL)),
                sectorInvestor(BusinessDebtManager.REAL_ESTATE));

        refreshLand();
        consider(businessInvestment.planRetail(
                populationManager.getPopulation(),
                buildingManager.getTotalStoreCoverage(),
                constructionOutput,
                buildingManager.getUnderConstructionByCategory(BuildingType.COMMERCIAL)),
                sectorInvestor(BusinessDebtManager.RETAIL));

        refreshLand();
        consider(businessInvestment.planIndustry(
                populationManager.getPopulation(),
                buildingManager.getTotalStoreCoverage(),
                ih.getMonthlyOutput(),
                constructionOutput,
                buildingManager.getUnderConstructionByCategory(BuildingType.INDUSTRIAL)),
                sectorInvestor(BusinessDebtManager.INDUSTRY));

        // Construction last: it reads the queue everyone else just added to.
        refreshLand();
        consider(businessInvestment.planConstruction(
                buildingManager.getRemainingConstructionPoints(),
                constructionOutput,
                buildingManager.getUnderConstructionByCategory(BuildingType.CONSTRUCTION)),
                sectorInvestor(BusinessDebtManager.CONSTRUCTION));
    }

    /**
     * The residents' side of the month.
     *
     * Runs after the national accounts, because household spending IS
     * consumption - the same rent and till receipts, read from the other end.
     * Taking them from NationalAccounts rather than recomputing means the
     * people cannot be shown paying a different figure from the one the
     * landlords and shops were shown receiving.
     *
     * The wage tax is computed here from this month's wage bill at the current
     * rate, which is the identical formula EconomyManager uses. Reading its
     * stored total instead would import a one-month lag: that field is assigned
     * during the PREVIOUS month's finalUpdateEconomy(), so it would pair this
     * month's wages with last month's tax.
     */
    private void updateHouseholdAccounts(){

        NationalAccounts na = economyManager.getNationalAccounts();

        double wages = populationManager.getTotalWage();
        double wageTax = Math.max(wages * economyManager.getTaxRate(), 0);

        households.update(
                wages,
                wageTax,
                na.getConsumptionHousing(),
                na.getConsumptionGoods(),
                populationManager.getPopulation(),
                populationManager.getWorkforce(),
                populationManager.getJobsFilled());
    }

    /** Tells the investment engine what land is left to sell, right now. */
    private void refreshLand(){
        businessInvestment.setLandAvailable(landManager.getAvailableSqFt(),
                landManager.getPricePerSqFt());
    }

    /* =======================================================================
       SHRINKING

       Everything in this game could grow and nothing could shrink, which is why
       construction's debt could only ever go one way. A sector that has lost
       money for months and is sitting on capacity nobody wants now sells that
       capacity: the buildings are scrapped, and the plot goes back to the city,
       which pays the going rate for it if it can afford to.

       The city buying the land back is the part that makes this a loop rather
       than a cheat. Squeezing businesses with land prices and property tax until
       they fold means buying their land back at the price you set - so the
       policy that maximises revenue this month is not the one that maximises it
       over a decade.

       If the treasury cannot pay, the land is simply abandoned: the city takes
       it back for nothing and the business gets no relief. That is the harsher
       outcome and it belongs to a city that has run itself dry.
       ======================================================================= */
    private void runRetirement(){

        CommercialHandler ch = economyManager.getCommercialHandler();
        IndustrialHandler ih = economyManager.getIndustrialHandler();
        ConstructionHandler construction = servicesManager.getConstructionHandler();

        int population = populationManager.getPopulation();
        int storeCoverage = buildingManager.getTotalStoreCoverage();

        businessInvestment.recordSectorResult(BusinessDebtManager.RETAIL,
                ch.getReportRetailNetIncome());
        businessInvestment.recordSectorResult(BusinessDebtManager.REAL_ESTATE,
                ch.getReportRealEstateNetIncome());
        businessInvestment.recordSectorResult(BusinessDebtManager.INDUSTRY,
                ih.getNetIncome());
        businessInvestment.recordSectorResult(BusinessDebtManager.CONSTRUCTION,
                construction.getNetIncome());

        // Housing: demand is people actually living in it. Empty units can go;
        // occupied ones can never be scrapped out from under anyone.
        retire(businessInvestment.planRetirement(
                BusinessDebtManager.REAL_ESTATE, BuildingType.RESIDENTIAL,
                population, getHouseholdCapacity(),
                buildingManager.getUnderConstructionByCategory(BuildingType.RESIDENTIAL)),
                sectorInvestor(BusinessDebtManager.REAL_ESTATE));

        retire(businessInvestment.planRetirement(
                BusinessDebtManager.RETAIL, BuildingType.COMMERCIAL,
                population, storeCoverage,
                buildingManager.getUnderConstructionByCategory(BuildingType.COMMERCIAL)),
                sectorInvestor(BusinessDebtManager.RETAIL));

        /*
         * Both of these pass NOMINAL capacity, not this month's actual output.
         *
         * planRetirement() works out how many buildings to scrap by dividing
         * spare capacity by what one building provides, and what one building
         * provides is its nameplate figure - a depot is 400 points whatever the
         * fill rate. Handing it the fill-rate-discounted output instead
         * compared two different units and systematically under-shed: a city at
         * an 11% fill rate had 324 points of "capacity" against a 400-point
         * depot, so the division came out at zero and ten idle depots were
         * never sold, however much money construction was losing.
         *
         * Staffing decides how much a firm PRODUCES. It does not change how
         * much plant the firm owns, which is what it is deciding whether to
         * keep.
         */
        retire(businessInvestment.planRetirement(
                BusinessDebtManager.INDUSTRY, BuildingType.INDUSTRIAL,
                Math.min(storeCoverage, population),
                buildingManager.getFoodProduction(),
                buildingManager.getUnderConstructionByCategory(BuildingType.INDUSTRIAL)),
                sectorInvestor(BusinessDebtManager.INDUSTRY));

        // Construction's demand is the queue: work ordered and not yet done,
        // capped at what its plant could deliver in a month. An empty queue
        // means every depot it owns is spare.
        double capacity = buildingManager.getTotalConstructionCapacity();
        double workAvailable = Math.min(
                buildingManager.getRemainingConstructionPoints(), capacity);

        retire(businessInvestment.planRetirement(
                BusinessDebtManager.CONSTRUCTION, BuildingType.CONSTRUCTION,
                workAvailable, capacity,
                buildingManager.getUnderConstructionByCategory(BuildingType.CONSTRUCTION)),
                sectorInvestor(BusinessDebtManager.CONSTRUCTION));
    }

    /** Scraps what the decision named, and sells the plot back to the city. */
    private void retire(BusinessInvestment.Decision decision, Investor seller){

        if (decision == null || !decision.build) {
            return;   // planRetirement's reasons are noise on the sector screens
        }

        int scrapped = buildingManager.retire(decision.template, decision.quantity);
        if (scrapped <= 0) {
            return;
        }

        double landFreed = decision.template.getLandSqFt() * scrapped;
        landManager.release(landFreed);

        double proceeds = landManager.priceFor(landFreed);

        if (proceeds > 0 && proceeds <= cash) {
            cash -= proceeds;
            seller.receive(proceeds);
            landManager.recordBuyback(landFreed);
        } else {
            proceeds = 0;   // abandoned - the city takes it back for nothing
        }

        lastInvestment.put(decision.sector,
                String.format("Sold %,d %s - %s%s",
                        scrapped, decision.template.getName(), decision.reason,
                        proceeds > 0
                                ? String.format(" (plot back to the city for $%s)",
                                        formatter.format(proceeds))
                                : " (plot abandoned - the city could not pay)"));

        // The sector screen carries the reasoning; this is so the loss shows up
        // where the player is actually looking, and keeps showing up for a
        // while afterwards. A city fast-forwarding fifty months should not have
        // to reconstruct what it lost from a building count going down.
        demolitionLog.record(decision.template.getName(), scrapped,
                decision.sector, month, proceeds);
    }

    /** Written off in the most recent insolvency sweep, for the credit screen. */
    private double lastWriteOff;

    public double getLastWriteOff(){
        return lastWriteOff;
    }

    /**
     * Applies the brake, then builds.
     *
     * The plan says demand justifies the capacity. This asks the separate
     * question of whether the business can carry what it would have to borrow -
     * a lender here will fund anything, so the discipline has to come from the
     * borrower.
     */
    private void consider(BusinessInvestment.Decision decision, Investor payer){

        if (decision == null || !decision.build) {
            lastInvestment.put(decision == null ? "?" : decision.sector,
                    decision == null ? "" : "Holding: " + decision.reason);
            return;
        }

        BusinessDebtManager credit = economyManager.getBusinessDebtManager();

        double cost = businessInvestment.getCostOf(decision.template, decision.quantity);
        double borrowed = Math.max(cost - payer.getCash(), 0);
        double profit = businessInvestment.estimatedMonthlyProfit(
                decision.sector, decision.template);

        if (!businessInvestment.servicesItsOwnDebt(
                profit, borrowed, credit.getRate(decision.sector))) {
            lastInvestment.put(decision.sector,
                    String.format("Declined %s - would not cover its interest",
                            decision.template.getName()));
            return;
        }

        if (buildFor(payer, decision.template, decision.quantity)) {
            lastInvestment.put(decision.sector,
                    String.format("Built %,d %s - %s",
                            decision.quantity, decision.template.getName(), decision.reason));
        } else {
            // The plan cleared every test and the build still did not happen,
            // which now has exactly one cause: the land went between planning
            // and buying. Saying so beats the silence this used to leave.
            lastInvestment.put(decision.sector,
                    String.format("Could not build %s - needs %,.0f sq ft, %,.0f free",
                            decision.template.getName(),
                            decision.template.getLandSqFt() * (double) decision.quantity,
                            landManager.getAvailableSqFt()));
        }
    }

    /** Wraps one sector's cash and credit line as a payer. */
    private Investor sectorInvestor(final String sector){

        final BusinessDebtManager credit = economyManager.getBusinessDebtManager();

        return new Investor() {

            @Override public String getName() { return sector; }

            @Override public double getCash() {
                return economyManager.getSectorCash(sector);
            }

            @Override public void spend(double amount) {
                economyManager.setSectorCash(sector,
                        economyManager.getSectorCash(sector) - amount);
            }

            // Businesses borrow freely here - the rate rises with leverage and
            // stops at its cap. What stops a spiral is the project having to
            // service its own debt, checked in consider() above.
            @Override public boolean canBorrow(double amount) { return amount > 0; }

            @Override public void borrow(double amount, int month) {
                credit.issueLoan(sector, amount, month);
                economyManager.setSectorCash(sector,
                        economyManager.getSectorCash(sector) + amount);
            }
        };
    }

    /** Read-only access for the utilities and construction screens. */
    public ServicesManager getServicesManager(){
        return servicesManager;
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

        /*
         * A skip is one click that can undo a hundred months of decisions, and
         * unlike a crash it is a mistake the player makes deliberately and then
         * regrets. The autosave written here is the point they come back to.
         *
         * Only for real skips: autosaving before every single month would write
         * the file twelve times as often as asked and make the interval
         * meaningless.
         */
        if (months > 1) {
            autosave("before skipping " + months + " months");
        }

        boolean previousGraphs = graphs;
        boolean previousReports = reports;

        graphs = false;
        reports = false;

        int completed = 0;

        // Snapshot before anything moves. Everything the summary shows is a diff
        // against this or a count taken month by month below - see TimeSkipReport.
        skipReport.beginSkip(months);
        captureSkipSnapshot(true);

        try {
            for (int i = 0; i < months; i++) {
                if (cash <= 0) {
                    System.out.println("Treasury empty - simulated " + completed
                            + " of " + months + " months.");
                    break;
                }
                nextMonth();
                completed++;

                // Sampled here rather than inferred from the endpoints, because
                // a city that starved for forty months and recovered looks
                // identical at both ends to one that never had a problem.
                skipReport.sampleMonth(
                        servicesManager.getEnergyRatio(),
                        servicesManager.getWaterRatio(),
                        landManager.getAvailableSqFt(),
                        households.isLivingBeyondIncome(),
                        !buildingManager.getStacksUnderConstruction().isEmpty(),
                        populationManager.getPopulation());
            }
        } finally {
            graphs = previousGraphs;
            reports = previousReports;

            // In the finally block so a skip that stops early - or throws - still
            // produces a readable summary rather than a half-filled one.
            captureSkipSnapshot(false);
        }

        return completed;
    }

    /** One end of the fast-forward diff. Reads finished figures; moves nothing. */
    private void captureSkipSnapshot(boolean atStart){

        java.util.Map<String, Integer> owned = new java.util.LinkedHashMap<>();

        for (BuildingsTemplate template : buildingManager.getTemplates()) {
            if (template == null) {
                continue;
            }
            int quantity = buildingManager.getQuantity(template.getId());
            if (quantity > 0) {
                owned.put(template.getName(), quantity);
            }
        }

        skipReport.snapshot(atStart, month, cash,
                populationManager.getPopulation(),
                getHouseholdCapacity(),
                populationManager.getTotalJobs(),
                economyManager.getNationalAccounts().getGdp(),
                economyManager.getNationalAccounts().getAnnualGdp(),
                debtManager.getAllPrincipal(),
                economyManager.getBusinessDebtManager().getTotalPrincipal(),
                landManager.getOwnedSqFt() / LandManager.BLOCK_SQ_FT,
                landManager.getUtilisation(),
                households.getSavingRate(),
                households.getRentBurden(),
                economyManager.getBusinessDebtManager().getTotalWrittenOff(),
                owned);
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
        //
        // Land first: the city cannot build on ground it does not own, and
        // checking it before the cash check means a refusal says which of the
        // two was actually missing.
        double landNeeded = selected.getLandSqFt() * quantity;

        if (!landManager.canAllocate(landNeeded)) {
            this.hasNewReceipt = false;
            System.out.println("Not enough land (need "
                    + formatter.format(landNeeded) + " sq ft, have "
                    + formatter.format(landManager.getAvailableSqFt()) + ")");
            return;
        }

        if (totalCost <= cash) {
            landManager.allocate(landNeeded);
            this.hasNewReceipt = true;
            lastBuildingName = buildingManager.getName(selected);

            buildingManager.addStack(selected, quantity, false);
            materialsConsumed += totalMaterialsRequired;

            // 4. Subtract everything at once
            cash -= totalCost;
            cityCapitalSpending += totalCost;
            monthlyMaterialImports += neededMaterials;

            // ...and it lands somewhere now. The construction sector did the
            // work; this is what it gets paid for doing it - recognised as the
            // work is actually delivered, not all on the order month.
            servicesManager.getConstructionHandler().bill(totalCost,
                    selected.getConstructionPoints() * (double) quantity);

            System.out.println(quantity + " " + selected.getName()
                    + " construction started. $" + totalCost + " cash used");
        } else {
            // Now the debt issued will actually cover the FULL price including the setup fee
            System.out.println("Not enough Cash (Need " + formatter.format(totalCost)
                    + ", have " + formatter.format(cash) + ")");
            quickIssueDebt(selected, quantity, totalCost);
        }
    }

    
    
    public enum BuildResult {SUCCESS, NEEDS_FUNDING, NO_LAND, FAILED}

    /**
     * A build order paid for by someone other than the city.
     *
     * Everything except the payment is identical to a government build - same
     * cost, same materials, same construction queue - because none of that ever
     * cared who was buying. The order goes into the same queue and competes for
     * the same construction capacity, which is the point: a city whose builders
     * are busy on private work builds its own power station more slowly.
     */
    public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity) {

        double materialsPrice = buildingManager.getConstructionMaterialPrice();
        double currentMaterials = buildingManager.getConstructionMaterials();
        double totalMaterialsRequired = template.getConstructionMaterials() * quantity;

        double neededMaterials = Math.max(totalMaterialsRequired - currentMaterials, 0);

        double landNeeded = template.getLandSqFt() * quantity;

        if (!landManager.canAllocate(landNeeded)) {
            return false;   // the city has no plot to sell them
        }

        // A business buys its plot from the city. This is the one part of a
        // private build the player actually receives.
        double landPrice = landManager.priceFor(landNeeded);

        double totalCost = template.getCashCost() * quantity
                + neededMaterials * materialsPrice
                + landPrice;

        double shortfall = totalCost - payer.getCash();

        if (shortfall > 0) {
            if (!payer.canBorrow(shortfall)) {
                return false;
            }
            payer.borrow(shortfall, month);
        }

        payer.spend(totalCost);

        // The land money goes to the city; the rest goes to construction below.
        landManager.allocate(landNeeded);
        landManager.recordSale(landNeeded);
        cash += landPrice;

        buildingManager.addStack(template, quantity, false);
        materialsConsumed += totalMaterialsRequired;
        monthlyMaterialImports += neededMaterials;

        // Construction is paid for the building work only - the land was the
        // city's, not theirs to be paid for.
        servicesManager.getConstructionHandler().bill(totalCost - landPrice,
                template.getConstructionPoints() * (double) quantity);

        return true;
    }

    /** The city itself, as a payer. Keeps its existing behaviour exactly. */
    private final Investor government = new Investor() {
        @Override public String getName()  { return "City"; }
        @Override public double getCash()  { return cash; }
        @Override public void spend(double amount) { cash -= amount; }
        @Override public boolean canBorrow(double amount) { return false; }
        @Override public void borrow(double amount, int month) { }
    };

    public Investor getGovernmentInvestor() { return government; }
    
    public BuildResult buildStack(BuildingsTemplate template, int quantity,boolean noConstuction){
        double totalCost = calculateTotalCost(template,quantity);

        // Land before money. processBuildOrder() checks it too, but by then the
        // caller has already been offered a T-Bill: without this the player can
        // borrow three hundred million to fund a building the city has nowhere
        // to put, and only find out after the debt is issued.
        if (!landManager.canAllocate(template.getLandSqFt() * quantity)) {
            this.hasNewReceipt = false;
            return BuildResult.NO_LAND;
        }

        if(totalCost <= cash){
            processBuildOrder(template,quantity);
            return BuildResult.SUCCESS;
        } else{
            return BuildResult.NEEDS_FUNDING;
        }
        
        
    }
    /** Square feet a build order of this size would need. For the refusal screen. */
    public double landNeededFor(BuildingsTemplate template, int quantity){
        return template.getLandSqFt() * (double) quantity;
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
        monthsSinceAutosave++;

        if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {
            autosave("month " + month);
        }
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
        // Price this month's business credit and hand the sectors their interest
        // bill BEFORE the statements run, so the figures they bank are net of it.
        // The assessor works off the price the player is currently charging, so
        // it has to know it before anything is valued or billed.
        economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());

        economyManager.updateBusinessCredit(debtManager.getRate());

        // Property tax with the interest bill, and for the same reason: both are
        // owed before the month's statements run, so what each sector banks is
        // already net of them.
        economyManager.chargePropertyTax();

        // Recognise the month's construction work before construction's income
        // statement runs, so the revenue and the payroll describe the same month.
        ConstructionHandler construction = servicesManager.getConstructionHandler();
        construction.recogniseWork(getConstructionOutput());

        // Grab the recognised figure before updateConstructionReport() banks the
        // month and clears it - that value IS this month's investment.
        double constructionWorkDone = construction.getRevenue();

        economyManager.updateCommercialReport();
        economyManager.updateIndustrialReport();
        economyManager.updateHeavyIndustryReport();
        economyManager.updateConstructionReport();

        economyManager.updateNationalAccounts(
                constructionWorkDone,
                servicesManager.getUtilitiesHandler().getUtilityPayroll(),
                monthlyMaterialImports * buildingManager.getConstructionMaterialPrice(),
                cityInterestPaid,
                cityCapitalSpending,
                landManager.getLandSalesThisMonth(),
                landManager.getLandPurchasesThisMonth(),
                economyManager.getTotalPropertyTax());

        cityCapitalSpending = 0;
        cityInterestPaid = 0;
        monthlyMaterialImports = 0;

        // Cleared only after the accounts have read them, or a month's land
        // trading would vanish before it was ever reported.
        landManager.clearMonth();

        updateHouseholdAccounts();

        // Then advance the loans, take back matured principal, and lend to
        // whichever sector the month left short.
        economyManager.settleBusinessCredit(month);

        // Businesses get their turn: look at demand, forecast it forward, and
        // expand if the new capacity would carry its own debt.
        runPrivateInvestment();

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
        economyManager.setPricePerWaterUnit(servicesManager.getPricePerWaterUnit());
        
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
    /**
     * Recounts the jobs the city's buildings offer. Does not touch population.
     *
     * Split out because the load path needs the jobs refreshed but must NOT
     * recompute how many people live here - that is saved state, and
     * recomputing it discarded the saved figure. See updatePopulation().
     */
    void refreshJobs() {
        for (JobType job : JobType.values()) {
            jobs[job.ordinal()] = buildingManager.getTotalJobs(job);
        }
    }

    void updatePopulation() {
        //TBE
        refreshJobs();
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
    private BusinessInvestment businessInvestment;
    private LandManager landManager = new LandManager();

    /** What businesses have scrapped, so the panel can say what went and when. */
    private DemolitionLog demolitionLog = new DemolitionLog();

    /** What happened during the last fast-forward. See TimeSkipReport. */
    private TimeSkipReport skipReport = new TimeSkipReport();

    public TimeSkipReport getSkipReport(){
        return skipReport;
    }

    /** The residents' own books. See HouseholdAccounts. */
    private HouseholdAccounts households = new HouseholdAccounts();

    /** For tests and for the graph screen. */
    public HistorySave getHistorySave(){
        return historySave;
    }

    public DemolitionLog getDemolitionLog(){
        return demolitionLog;
    }

    public HouseholdAccounts getHouseholds(){
        return households;
    }

    /** What the CITY spent on buildings this month - its own capital budget. */
    private double cityCapitalSpending;

    /**
     * Construction materials bought in from outside this month, in units.
     *
     * NOT totalMaterialsImported, which is a receipt field for the build screen:
     * it is assigned on a build order and never cleared, so it holds the last
     * order's figure forever. Subtracting that as imports charged the city
     * $1,240 of imports every month of a two-hundred-month stretch in which
     * nothing was built at all, and dragged GDP to -$468 on a city whose shops
     * were turning over $773.
     */
    private double monthlyMaterialImports;

    /** Interest the city paid this month, accumulated by InterestExpense(). */
    private double cityInterestPaid;
    private java.util.Map<String, String> lastInvestment = new java.util.LinkedHashMap<>();

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
    /* ============================ the save system ============================
     *
     * Ten numbered slots plus an autosave. The slot is chosen by the caller;
     * nothing in here assumes there is only one city.
     * ======================================================================== */

    /** How many months between autosaves. */
    public static final int AUTOSAVE_MONTHS = 12;

    private int monthsSinceAutosave;

    public int getMonthsUntilAutosave() {
        return Math.max(0, AUTOSAVE_MONTHS - monthsSinceAutosave);
    }

    /**
     * Writes the autosave slot, if there is a city to write.
     *
     * Called on three occasions, and each covers a different way of losing a
     * city: every twelve months for the ordinary case, before a multi-month skip
     * because that is a single click that can undo a hundred months of decisions,
     * and on quit because the window closing is the most common accident of all.
     *
     * Failures are reported and swallowed. An autosave that cannot write must
     * not stop a month advancing or a player quitting - it is a safety net, and
     * a safety net that throws is worse than none.
     */
    public void autosave(String reason) {

        if (!initialized) return;

        GameFiles.Result result = saveGame(GameFiles.AUTOSAVE_SLOT, "Autosave - " + reason);
        monthsSinceAutosave = 0;

        if (!result.ok) {
            System.out.println("Autosave failed: " + result.error);
        }
    }

    public void save(int slot, String slotName){
        
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
        dataSave.setBusinessDebt(economyManager.getBusinessDebtManager().getLoans());
        // Keyed by template id, not by stack position - see the note on
        // BuildingManager.getConstructionProgressById(). The old positional
        // arrays are no longer written; loadGame() still reads them so saves
        // made before this change are no worse off than they were.
        dataSave.setConstructionById(
                buildingManager.getUnderConstructionById(),
                buildingManager.getConstructionProgressById());

        // Charged during the month rather than derived from state, so nothing
        // can recompute it on load. Without this the freshly loaded city showed
        // a next-month income missing its whole property-tax line.
        dataSave.setPropertyTaxCharged(economyManager.getTotalPropertyTax());
        dataSave.setPropertyTaxCharges(economyManager.getPropertyTaxCharges());
        dataSave.setInterestCharges(economyManager.getInterestCharges());

        // The month's flows. Balances alone cannot reconstruct a month's
        // income statement - see DataSave for which ones and why.
        dataSave.setMonthFlows(
                economyManager.getRetailCostOfGoods(),
                economyManager.getRetailLocalImports(),
                economyManager.getRetailGlobalImports(),
                economyManager.getRetailFillBasis(),
                economyManager.getRetailImportTax(),
                economyManager.getIndustryDemand(),
                economyManager.getIndustryUnitsSold(),
                economyManager.getIndustryUnitsImported());

        dataSave.setNationalAccounts(economyManager.getNationalAccountsState());

        ConstructionHandler builders = servicesManager.getConstructionHandler();
        dataSave.setConstructionBooks(builders.getCash(),
                builders.getUnearnedRevenue(), builders.getBacklogPoints());
        dataSave.setConstructionMaterials(buildingManager.getConstructionMaterials());
        dataSave.setStoreInventory(economyManager.getStoreInventory());
        dataSave.setIndustryFoodInventory(economyManager.getIndustryFoodInventory());
        dataSave.setPopulation(populationManager.getPopulation());
        dataSave.setCommercialCash(economyManager.getCommercialCash());
        dataSave.setRealEstateCash(economyManager.getRealEstateCash());
        dataSave.setIndustrialCash(economyManager.getIndustrialCash());
        dataSave.setHeavyIndustryCash(
                economyManager.getHeavyIndustryHandler().getCash());
        dataSave.setHouseholdSavings(households.getCumulativeSaving());
        // NOTE: reports/graphs settings were never saved at all - they'd silently
        // reset to their true/true defaults on every load.
        dataSave.setLandOwned(landManager.getOwnedSqFt());
        dataSave.setLandBlocksPurchased(landManager.getBlocksPurchased());
        dataSave.setLandPricePerSqFt(landManager.getPricePerSqFt());
        dataSave.setIncomeTaxRate(economyManager.getTaxPolicy().getIncomeTaxRate());
        dataSave.setPropertyTaxRate(economyManager.getTaxPolicy().getPropertyTaxRate());
        dataSave.setReports(reports);
        dataSave.setGraphs(graphs);
        dataSave.setSlotName(slotName);
        dataSave.stamp(GameVersion.VERSION, GameVersion.SAVE_FORMAT,
                System.currentTimeMillis());

        lastSaveResult = dataSave.saveGame(gameFiles, slot);

        // The history is written even when the save failed, on purpose: the two
        // files are independent, and one of them landing is strictly better
        // than neither. The reported outcome is the save's, because that is the
        // file the player would actually mourn.
        GameFiles.Result history = historySave.saveHistory(gameFiles, slot);
        if (!history.ok) {
            System.out.println(history.message());
        }
    }

    /** What the last write attempt did. Null until something has been saved. */
    private GameFiles.Result lastSaveResult;

    public GameFiles.Result getLastSaveResult() { return lastSaveResult; }

    public GameFiles getGameFiles() { return gameFiles; }
    
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

    /*
     * resetGame() used to live here, along with thin wrappers around each
     * manager's own reset. It is gone rather than fixed: newGame() rebuilds the
     * object graph now (see buildWorld()), so nothing calls it - and leaving a
     * public method NAMED resetGame that resets about two thirds of the game is
     * a trap for whoever reaches for it next.
     *
     * The managers keep their own reset methods. They are unused today; the
     * point is that none of them is on the path a new game takes.
     */
    
    //calculations
    
    public void subtractCash(double amount){
        cash -= amount;
    }
    public void InterestExpense(double amount){
        economyManager.updateInterestExpense(amount);
        cityInterestPaid += amount;
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
    // must precede updateServices(): the residents' water draw is part of the
    // demand the ratio is computed against
    servicesManager.setPopulation(populationManager.getPopulation());
    servicesManager.updateServices();

    // economy sync
    economyManager.setPopulation(populationManager.getPopulation());
    economyManager.setHouseholds(getHouseholdCapacity());
    economyManager.setTotalJobs(populationManager.getTotalJobs());
    economyManager.setTotalWage(populationManager.getTotalWage());
    economyManager.setEnergyRatio(servicesManager.getEnergyRatio());
    economyManager.setWaterRatio(servicesManager.getWaterRatio());

    economyManager.updateIndustrialWages(populationManager.getWagesPerType());
    economyManager.updateStoreWages(
            populationManager.getWagesPerType(),
            buildingManager.getJobArrayPerCategory(BuildingType.COMMERCIAL)
    );

    economyManager.updateJobFillRate(populationManager.getJobFillRate());
    economyManager.updateHeavyIndustryWages(populationManager.getWagesPerType());

    economyManager.updateEcon();

    servicesManager.updateServices();

    economyManager.setPricePerWatt(servicesManager.getPricePerWatt());
    economyManager.setPricePerWaterUnit(servicesManager.getPricePerWaterUnit());

    // refreshEconPrices(), NOT finalEconUpdate() - the latter PRODUCES food,
    // moves inventory and has the shops trade. Calling it here ran a month of
    // the economy with the calendar standing still. See its note in EconomyManager.
    economyManager.refreshEconPrices();

    // Price business credit off the restored balance sheets so a freshly loaded
    // save shows real rates and interest rather than zeroes.
    economyManager.updateBusinessCredit(debtManager.getRate());

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
    economyManager.refreshIndustrialReport();
    economyManager.refreshHeavyIndustryReport();

    // GDP reads commercialHandler.getNetIncome(), so it has to come after the
    // refresh above to see this month's figure rather than a stale one.
    debtManager.setGDP(economyManager.getMonthGdp());
    debtManager.updateInterest();

    // Was only done by the second rebuild pass in loadGameSave(). It has to
    // happen somewhere, and this is the one rebuild there is now.
    ConstructionHandler construction = servicesManager.getConstructionHandler();
    servicesManager.updateFromGame(construction::setMaterialsInventory,
            buildingManager.getConstructionMaterials());
    servicesManager.updateFromGame(construction::setMaterialsPrice,
            buildingManager.getConstructionMaterialPrice());
    servicesManager.updateFromGameInt(construction::setMaterialsConsumed, materialsConsumed);
}
   
   
   
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // Load game
    public void loadGame(int slot) {

        /*
         * Applied after rebuildSimulationState() rather than inside the try,
         * because that rebuild runs finalEconUpdate() and the month's charged
         * figures are exactly the kind of thing it resets. Restoring it here
         * and then rebuilding would put the bug straight back.
         */
        double restoredPropertyTax = 0;
        double[] restoredPropertyTaxCharges = null;
        DataSave restoredFlows = null;
        double[] restoredInterestCharges = null;

        try {
            Path path = gameFiles.saveFile(slot);

            if (!Files.exists(path)) {
                System.out.println(GameFiles.slotLabel(slot) + " is empty.");
                return;
            }

            String json = Files.readString(path);
            Gson gson = new Gson();

            // Deserialize normally
            DataSave loaded = gson.fromJson(json, DataSave.class);

            /*
             * Stop here rather than half-reading it. An older build silently
             * ignores fields it does not recognise, so a save from a newer one
             * loads looking fine and is missing whatever that build added -
             * and the player finds out later, having played on top of it.
             */
            if (GameVersion.isFromNewerBuild(loaded.getSaveFormat())) {
                System.out.println(GameFiles.slotLabel(slot)
                        + " was written by a newer version of the game ("
                        + loaded.getGameVersion() + ", save format "
                        + loaded.getSaveFormat() + "; this build reads up to "
                        + GameVersion.SAVE_FORMAT + "). Not loaded.");
                loadFailure = "This save is from a newer version of the game.";
                return;
            }
            loadFailure = null;

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
            this.population = loaded.getPopulation();
            economyManager.setCommercialCash(loaded.getCommercialCash());
            economyManager.setRealEstateCash(loaded.getRealEstateCash());
            economyManager.setIndustrialCash(loaded.getIndustrialCash());
            economyManager.getHeavyIndustryHandler()
                    .setCash(loaded.getHeavyIndustryCash());
            households.setCumulativeSaving(loaded.getHouseholdSavings());
            this.reports = loaded.getReports();
            this.graphs = loaded.getGraphs();

            /*
             * Buildings, and the work still on their sites.
             *
             * A stack is now created when a template has EITHER completed
             * buildings or work in progress. It used to be completed-only,
             * which is half of why in-progress construction never came back:
             * a player whose first two depots were still being built had no
             * stack to restore them onto, so they simply ceased to exist.
             *
             * Both loops are indexed by template id - getTemplate() and
             * getQuantity() both look up by id, and buildings[] is written by
             * id, so this is one key throughout.
             */
            int highestId = Math.max(loaded.getBuildingsLength(),
                    loaded.getConstructionByIdLength());

            for (int id = 0; id < highestId; id++) {

                int quantity = (id < loaded.getBuildingsLength())
                        ? loaded.getBuildingQuantity(id) : 0;
                int building = loaded.hasConstructionById()
                        ? loaded.getUnderConstructionById(id) : 0;

                if (quantity <= 0 && building <= 0) continue;

                BuildingsTemplate template = buildingManager.getTemplate(id);
                if (template == null) {
                    // The save names a building this catalogue no longer has -
                    // buildings.json was edited between saving and loading.
                    // Skipping it loses those buildings, which is bad, but
                    // guessing which template was meant would be worse.
                    System.out.println("Save contains building id " + id
                            + ", which is not in buildings.json - skipped.");
                    continue;
                }

                buildingManager.addStack(template, quantity, true);

                if (building > 0 || loaded.getConstructionProgressById(id) > 0) {
                    buildingManager.restoreConstruction(id, building,
                            loaded.getConstructionProgressById(id));
                }
            }

            //Load construction progress
            double[] progress = new double[loaded.getProgressLength()];
            for (int i = 0; i < loaded.getProgressLength(); i++) {

                progress[i] = loaded.getProgress(i);

            }
            
            //Load under Construction
            int[] quantity = new int[loaded.getUnderConstructionLength()];
            for (int i = 0; i < loaded.getUnderConstructionLength(); i++) {

                quantity[i] = loaded.getUnderConstruction(i);

            }

            /*
             * Both setters throw when the array does not match the stacks, and
             * that mismatch is a REAL and known bug in the save format: progress
             * and under-construction are stored per stack in creation order,
             * while the load recreates a stack only for templates with a
             * completed quantity - so a building that is purely under
             * construction leaves no stack to line up against.
             *
             * The throw is what makes it dangerous. IllegalArgumentException is
             * not IOException, so it escaped the catch below and abandoned the
             * rest of loadGame() silently: government debts, business loans,
             * history and (as of this pass) the city's land were all simply not
             * restored, on a load that reported no error and showed the right
             * cash. Skipping the two arrays loses in-progress construction,
             * which is bad; losing half the save without saying so is worse.
             *
             * The format fix - keying both arrays by template id, the way
             * buildings[] already is - is the actual repair and is still owed.
             */
            boolean stacksLineUp = progress.length == buildingManager.getStackCount()
                    && quantity.length == buildingManager.getStackCount();

            if (loaded.hasConstructionById()) {
                // Already restored above, by id. Nothing to do here.
            } else if (stacksLineUp) {
                buildingManager.setConstructionProgress(progress);
                buildingManager.setUnderConstructionArray(quantity);
            } else if (progress.length > 0) {
                System.out.println("Save has " + progress.length
                        + " construction records against " + buildingManager.getStackCount()
                        + " stacks - in-progress construction not restored."
                        + " (Save written in the old positional format.)");
            }

            /*
             * Land, after the buildings, because the allocation is derived from
             * them rather than stored. A save from before land existed has
             * landOwned 0; that is not a city with no land, it is a save that
             * never knew about land, so it keeps the opening allocation - and
             * if what is already built exceeds that, the city is loaded owning
             * exactly what it stands on rather than being retroactively
             * bankrupted by a mechanic added after the save was written.
             */
            double built = buildingManager.getTotalLandFootprint();
            double owned = loaded.getLandOwned();

            if (owned <= 0) {
                owned = Math.max(LandManager.STARTING_SQ_FT, built);
                landManager.setBlocksPurchased(0);
                landManager.setPricePerSqFt(loaded.getLandPricePerSqFt() > 0
                        ? loaded.getLandPricePerSqFt()
                        : new LandManager().getPricePerSqFt());
            } else {
                landManager.setBlocksPurchased(loaded.getLandBlocksPurchased());
                landManager.setPricePerSqFt(loaded.getLandPricePerSqFt());
            }

            landManager.setOwnedSqFt(owned);
            landManager.setAllocatedSqFt(built);
            landManager.clearMonth();

            TaxPolicy policy = economyManager.getTaxPolicy();
            if (loaded.getIncomeTaxRate() > 0) {
                policy.setIncomeTaxRate(loaded.getIncomeTaxRate());
            }
            if (loaded.getPropertyTaxRate() > 0) {
                policy.setPropertyTaxRate(loaded.getPropertyTaxRate());
            }

            restoredPropertyTax = loaded.getPropertyTaxCharged();
            restoredPropertyTaxCharges = loaded.getPropertyTaxCharges();
            restoredFlows = loaded.hasMonthFlows() ? loaded : null;
            restoredInterestCharges = loaded.getInterestCharges();
            

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

            // Load business loans. Same shape as the government debts above, in
            // its own array so the two hierarchies never have to be told apart
            // by type string alone.
            JsonArray businessArray = loaded.getBusinessDebt();
            List<BusinessDebt> loadedLoans = new ArrayList<>();

            if (businessArray != null) {
                for (JsonElement element : businessArray) {

                    JsonObject obj = element.getAsJsonObject();
                    String type = obj.get("type").getAsString();

                    switch (type) {

                        case "BUSINESS-LOAN":
                            loadedLoans.add(gson.fromJson(obj, BusinessLoan.class));
                            break;
                    }

                    // future business instrument types go here
                }
            }

            economyManager.getBusinessDebtManager().setLoans(loadedLoans);

            loadHistory(slot);

            System.out.println("Game loaded successfully.");

        } catch (IOException e) {
            System.out.println("Failed to load save file.");
        }

        /*
         * Re-charge property tax before the rebuild, not after.
         *
         * Saving the aggregate figure was only half of it. chargePropertyTax()
         * is also the only thing that tells each sector what IT owes -
         * retailPropertyTax, realEstatePropertyTax, industrial and heavy and
         * construction - and none of those were restored either. So a loaded
         * city had every sector's income statement missing its property-tax
         * expense line, which made retail and real estate look more profitable
         * than they were and pushed the business tax up with them.
         *
         * Recomputed rather than saved per sector, because it is a pure
         * function of restored state: the building stock, the land price and
         * the tax rate all came back from the save, so this produces the same
         * figures that were charged. That also means saves written before any
         * of this existed are repaired on load rather than left at zero.
         *
         * No money moves here. chargePropertyTax() only assigns expense
         * figures; the cash was borne when the sectors' statements ran, and
         * those balances came back from the save already net of it.
         *
         * Before the rebuild because the rebuild recomputes every sector
         * report, and those reports have to see the expense.
         */
        economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());

        if (restoredPropertyTaxCharges != null) {
            economyManager.restorePropertyTaxCharges(restoredPropertyTaxCharges);
        } else {
            /*
             * A save from before the charges were recorded. Recomputing is a
             * month stale - property tax is charged early in the month and
             * buildings finish after it, so the assessed value has moved on -
             * but a stale figure beats every sector reporting no property-tax
             * expense at all, which is what these saves did before.
             */
            economyManager.chargePropertyTax();
            if (restoredPropertyTax > 0) {
                economyManager.setTotalPropertyTax(restoredPropertyTax);
            }
        }

        rebuildSimulationState();

        /*
         * After the rebuild, because the rebuild recomputes every sector report
         * from current state - which is precisely the thing that cannot see last
         * month's trading. Restoring first would just be overwritten.
         */
        // Same reason as the flows below: rebuildSimulationState() re-prices
        // business credit off the restored balance sheets, so this has to land
        // after it or it is simply overwritten.
        if (restoredInterestCharges != null) {
            economyManager.restoreInterestCharges(restoredInterestCharges);
        }

        if (restoredFlows != null) {
            economyManager.restoreMonthFlows(
                    restoredFlows.getRetailCostOfGoods(),
                    restoredFlows.getRetailLocalImports(),
                    restoredFlows.getRetailGlobalImports(),
                    restoredFlows.getRetailFillBasis(),
                    restoredFlows.getRetailImportTax(),
                    restoredFlows.getIndustryDemand(),
                    restoredFlows.getIndustryUnitsSold(),
                    restoredFlows.getIndustryUnitsImported());

            economyManager.restoreNationalAccounts(restoredFlows.getNationalAccounts());

            servicesManager.getConstructionHandler().restoreOrderBook(
                    restoredFlows.getConstructionCash(),
                    restoredFlows.getConstructionUnearnedRevenue(),
                    restoredFlows.getConstructionBacklogPoints());
        }
    }


    public void loadHistory(int slot) {

        try {
            Path path = gameFiles.historyFile(slot);

            if (!Files.exists(path)) {
                System.out.println("No history for " + GameFiles.slotLabel(slot) + ".");
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
 


 
   