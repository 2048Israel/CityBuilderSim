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
     * What the city has had to say for itself. See Inbox.
     *
     * Beside the history rather than anywhere else, because the two are the
     * same kind of thing: a record of months that have already happened, kept
     * whether or not a window is open to see it.
     */
    private Inbox inbox = new Inbox();

    /*
     * The sector statements. Beside the inbox because it is the same kind of
     * thing: something the city writes down once a month whether or not
     * anybody is looking at the screen that shows it.
     */
    private SectorBooks sectorBooks = new SectorBooks();

    /**
     * Where saves live and how they are written. One instance for the whole
     * game: the path used to be spelled out separately in DataSave, HistorySave
     * and twice more down in the load methods, which is four places to get it
     * wrong and no way to notice when one of them drifts.
     */
    private final GameFiles gameFiles;
    private HistoryGrapher historyGrapher;
    private DebtManager debtManager;
    
    private SimulationEngine simulationEngine;
    
    int materialsConsumed =0;
    
    //construction UI logic
    double totalMaterialsImported = 0;
    double totalBuildingCost = 0;
    private boolean hasNewReceipt = false;
    String lastBuildingName;

    /**
     * How MANY of them, so the receipt can say "3 x Walk-in Clinic".
     *
     * The receipt already carried the total cost and the imported materials but
     * not the count, which made the two numbers it did carry unreadable: $4,200
     * means nothing until you know whether it bought one clinic or three.
     */
    int lastBuildQuantity = 0;

    /**
     * WHICH receipt this is, counted up forever.
     *
     * The UI flashes an indicator when there is a build the player has not
     * looked at yet, and "not looked at yet" cannot be answered from the receipt
     * contents: build the same three clinics twice in a row and every field is
     * identical, so a UI comparing contents would decide the second one was old
     * news. A serial makes the question answerable - the UI remembers the number
     * it last showed and compares - and it belongs here rather than in the UI
     * because which receipt this is, is a fact about the game, not about how it
     * happens to be drawn.
     */
    private int receiptSerial = 0;
    
    /**
     * How much more a long-term bond costs in total than an equivalent
     * medium-term bond. The player buys lower monthly payments with a higher
     * all-in cost; this is the size of that trade.
     */
    /*
     * RETIRED. Long bonds used to be grossed up by this over an equivalent
     * medium bond, on a simple-interest relation that did not survive being
     * discounted properly - see faceValueOfLongBond(). The instrument is priced
     * at present value now and is still dearer all-in, because it runs longer
     * at a real rate rather than because a constant said so.
     *
     * private static final double LONG_BOND_COST_MULTIPLIER = 1.15;
     */

    //settings
    /*
     * Console output per month: seven sector income statements and three 12x60
     * ASCII graphs. Off by default since 2026-09-06. GameLog tees everything
     * that reaches System.out into log.txt and stops writing at its cap, so
     * with these on a long session filled the log with graphs and the one line
     * you wanted - the crash - landed after the cap. Both stay switchable from
     * the Settings screen for anyone reading the log on purpose.
     */
    boolean reports = false;
    boolean graphs = false;
    
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
        // Rebuilt here rather than reset, for the reason buildWorld exists: a
        // new city has to be identical to a freshly started one by
        // construction, and a cleared list is one more thing to remember.
        inbox = new Inbox();
        sectorBooks = new SectorBooks();
        historyGrapher = new HistoryGrapher();
        debtManager = new DebtManager();
        businessInvestment = new BusinessInvestment(buildingManager, economyManager);

        // The economy needs to reach construction's books: it is a business with
        // cash, credit and an income statement now, but it lives under
        // ServicesManager with the other municipal services.
        economyManager.setConstructionHandler(servicesManager.getConstructionHandler());
        economyManager.setOutwardInvestment(outward);
        economyManager.setEquity(equity);
        
        simulationEngine = new SimulationEngine(
                economyManager,
                populationManager,
                servicesManager,
                buildingManager,
                debtManager);
        
        landManager = new LandManager();
        demolitionLog = new DemolitionLog();
        buildLog = new BuildLog();
        cohorts = new PopulationCohorts();
        families = new FamilyModel();
        migration = new Migration();
        labourMarket = new LabourMarket();
        education = new Education();
        skipReport = new TimeSkipReport();
        households = new HouseholdAccounts();
        householdBalance.reset();
        bank.reset();
        foreign.reset();
        hotMoney.reset();
        outward.reset();
        equity.reset();
        priceIndex.reset();
        world.reset();
        lastInvestment = new java.util.LinkedHashMap<>();

        this.isRunning = true;
        this.month = 1;
        /*
         * THE FOUNDING ENDOWMENT: 300,000 to 500,000, on 2026-09-09.
         *
         * In thousands, like every money field here - so $300M to $500M. See
         * BuildingsTemplate's header, or claude/reading-the-numbers.md.
         *
         * Jerus's call, alongside quadrupling the municipal works and giving the
         * city a bank on day one. All four are the same decision seen from four
         * sides: the opening was a city that could not afford to do anything
         * with its first decade. $300M bought about forty Gravel Roads ($6.0M
         * each) OR one Coal Power Plant ($206M), the works department put up 100
         * points a month against a Construction Depot costing 3,000 points, and
         * every dollar borrowed in the meantime carried the eighteen-point
         * premium a city with no bank pays.
         *
         * ...AND 500,000 TO 3,500,000 THE SAME NIGHT, WHICH IS THE SAME
         * DECISION AGAIN RATHER THAN A NEW ONE.
         *
         * Rebalance stage two put every building in the game on its real
         * capital cost, and almost all of them went up between four and seven
         * times. A Coal Power Plant is $1.43B now (EIA's $4,390/kW on the 325 MW
         * this plant actually is) against $206M before. Left at $500M the
         * endowment would not have bought the ONE building a city cannot start
         * without, and the harnesses said so immediately: ForeignCheck's
         * devaluation city could no longer afford its own build list, so it
         * built no Textile Mill, so it exported NOTHING, and a test about
         * elasticities was measuring a city with no trade.
         *
         * THE ENDOWMENT IS NOT A REAL-WORLD NUMBER. Every other figure in this
         * pass has a source; this one has a job, which is to buy the opening.
         * So it is set to hold the opening where Jerus put it: the power plant
         * was 41% of the endowment at $500M and is 41% of it at $3.5B. The
         * founding city can do exactly what it could do this morning, in money
         * that now means what it says.
         */
        this.cash = 3500000;
        this.population = 0;
        this.jobs = new int[JobType.values().length];

        this.materialsConsumed = 0;
        this.totalMaterialsImported = 0;
        this.totalBuildingCost = 0;
        this.hasNewReceipt = false;
        this.lastBuildingName = null;
        this.lastBuildQuantity = 0;
        this.receiptSerial = 0;
        this.cityInterestPaid = 0;
        this.monthsSinceAutosave = 0;

        this.lastSaveResult = null;
        this.loadFailure = null;
        this.skipFailure = null;

        /*
         * The retainer and the warning about needing one.
         *
         * buildWorld() rebuilds every MANAGER, which is what made the
         * twenty-three-field reset bug go away - but these three live on Game
         * itself, so nothing was clearing them. A new game inherited the
         * previous city's construction subsidy and went on paying it every
         * month, and inherited its shedding warning too.
         *
         * NewGameCheck did not catch it because its snapshot did not reach
         * these fields. It does now, which is the actual fix - the list being
         * short is the bug that keeps recurring here, not any one field on it.
         */
        this.constructionSubsidy = 0;

        // Policy is the player's, so a new city starts with none of it: no
        // sector protected, no offsets, both rates at their defaults. Leaving
        // any of these behind is the leak New Game has produced twice already.
        java.util.Arrays.fill(autoSubsidy, false);
        java.util.Arrays.fill(subsidyPaid, 0);
        economyManager.getTaxPolicy().reset();
        economyManager.getSalesTaxLedger().reset();
        this.constructionShedMonth = -1;
        this.constructionShedPoints = 0;

        this.reports = false;
        this.graphs = false;
    }
    
    public void run() {
        initialize();
        foundingBank();
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
        /*
         * One month of the works yard's own output, in the yard on day one.
         * Reads the constant rather than repeating it: the two were the same
         * number by coincidence for as long as nobody moved either, and on
         * 2026-09-09 somebody moved one.
         */
        buildingManager.setConstructionMaterials(BuildingManager.BASE_MATERIALS);

        // Has to happen before anything loads or saves, and this is the one
        // place guaranteed to run first: both resumeGame() and loadGameSave()
        // call initialize(), and it is guarded so it only ever runs once.
        // Copies, never moves - the old folder is left where it is.
        for (String file : gameFiles.migrateLegacy()) {
            System.out.println("Brought " + file
                    + " over from the old save folder into " + gameFiles.getDirectory());
        }

        // Ten plots have to be on offer before the player's first turn, not
        // after their first month.
        landManager.updateMarket(populationManager.getPopulation());

        initialized = true;
        }
 
    }
    
    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
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
        foundingBank();
    }

    /**
     * The city opens with a bank already standing.
     *
     * WHY. `Bank.ratePremium()` charges the full eighteen points to any city
     * with a loan book and no branches, because borrowing without a banking
     * system is borrowing from strangers who do not know you. That is right,
     * and it lasted about forty years: a Commercial Bank costs $9.0M to build
     * plus $32.0M of shareholders' capital, and nothing in the model would put
     * one up until the city could carry both. So the founding decades were
     * played at the most expensive credit the model can quote, at exactly the
     * moment a city can least afford it - filed as finding #11, and Jerus's
     * answer is to hand the city one rather than to soften the premium.
     *
     * PLACED, NOT BUILT - `true` is noConstruction - so it does not consume a
     * decade of the works department's output before it exists. It is part of
     * the endowment, like the hundred people the city houses for nothing.
     *
     * The shareholders' capital arrives on its own: `openBranches()` counts
     * standing bank buildings every month, sees one it has not capitalised, and
     * takes the paid-in capital from outside the city - declared, so the
     * balance of payments knows. The founding branch also gets the clean set of
     * books that method already gives a city's first bank.
     *
     * CALLED FROM newGame() AND run(), NOT FROM initialize(). initialize() also
     * runs on the load path, and the load path RESTORES buildings by adding to
     * whatever is standing - so founding a bank there would give every loaded
     * city one branch more than it was saved with, compounding on every load.
     */
    private void foundingBank() {
        BuildingsTemplate branch = buildingManager.getTemplateByName("Commercial Bank");
        if (branch == null) return;                              // no such template
        if (buildingManager.countByName("Commercial Bank") > 0) return;   // already has one

        buildingManager.addStack(branch, 1, true);

        /*
         * AND THE GROUND UNDER IT, which is not a detail.
         *
         * addStack() puts a building up; it does not tell the land office. Every
         * other path into it - processBuildOrder(), buildFor() - calls
         * landManager.allocate() itself, and the first version of this method
         * did not, so the city stood a 45,000 sq ft bank on ground the ledger
         * still thought was free.
         *
         * It showed up as a SAVE bug, which is the interesting part. The load
         * path does not restore the allocation: it derives it from what is
         * standing, deliberately, so that any drift heals on the next load. So
         * the live city read 1,397,000 sq ft allocated and its own reload read
         * 1,442,000 - the reload was right - and a 1.3% difference in the land
         * price walked through the building costs into the treasury. Caught by
         * SaveFileCheck as $13 adrift on a $690.7M treasury, which is what that
         * assertion is for.
         *
         * No money changes hands: the founding endowment already owns the
         * ground, exactly as it already houses a hundred people.
         */
        landManager.allocate(branch.getLandSqFt());
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

        /*
         * REBUILD THE WORLD FIRST, exactly as newGame() does.
         *
         * This used to be a bare initialize(), and initialize() is guarded by
         * `initialized` - so once any game was running it did NOTHING. The reset
         * that should have cleared the old city was commented out inside
         * loadGame(), and BuildingManager.addStack() matches on name and ADDS.
         *
         * Measured: load a save, keep playing, load it again, and the city's
         * house capacity went 2,720 -> 2,720 -> 5,340. Every building count
         * doubled. Debt is replaced rather than accumulated, so the result was a
         * city with twice the buildings and the right debt - solvent-looking,
         * corrupt, and reported as a successful load. Load is on the pause menu
         * beside Resume and Save, so this is a thing players do.
         *
         * No harness could see it. Every one of the twenty-three builds a fresh
         * Game per case, so the same Game object is never loaded into twice -
         * a whole blind quadrant, now covered by ConservationCheck.
         *
         * Rebuilding rather than resetting is the same argument buildWorld()
         * makes for newGame(): a list of fields to clear is a list somebody will
         * forget to extend, and the old one had fallen twenty-three fields
         * behind. A loaded city is now identical to one loaded into a fresh
         * process BY CONSTRUCTION, and no field added in future can leak across.
         */
        buildWorld();

        // buildWorld() made a new BuildingManager, so the templates have to be
        // reloaded into it - and initialize() is guarded, so the flag has to
        // drop first or loadGame()'s per-index template lookups hit an empty
        // list. Same two lines, same reason, as newGame().
        initialized = false;
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
    
    /**
     * True once a city exists to go back to.
     *
     * buildWorld() sets it, and buildWorld() is the one door into a playable
     * city - newGame() and loadGameSave() both go through it. So at the title
     * screen this is false and after either of those it is true, which is
     * exactly the question the main menu's Resume button has to ask.
     */
    public boolean isRunning() { return isRunning; }

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
    
    public int getMonth(){
        return month;
    }
    public double getCash(){
        return cash;
    }
    /* ======================= THE CONSTRUCTION SUBSIDY =======================
     *
     * A monthly retainer that keeps builders on the books between projects.
     *
     * The 4,000-month playtest found that idle construction is loss-making, so
     * it sheds capacity in any lull - including the capacity a player just paid
     * for. Buy four depots, watch the population double, watch the sector scrap
     * all four sixty months later, and the city falls back to where it started
     * for the next eight centuries.
     *
     * This is the lever against that, and it is deliberately not free and not
     * absolute. The money is real, it is paid every month whether anything is
     * being built or not, and it protects exactly as much capacity as it covers
     * the payroll of. Protecting more costs more.
     * ==================================================================== */

    private double constructionSubsidy;

    public double getConstructionSubsidy(){ return constructionSubsidy; }

    public void setConstructionSubsidy(double monthlyAmount){
        this.constructionSubsidy = Math.max(0, monthlyAmount);
    }

    /* ====================================================================
       STANDING POLICY: NEVER LET THIS SECTOR SHRINK

       Jerus's brief: "always subsidize this industry if negative net income,
       aka if you want some industry to never downsize."

       WHAT IT PAYS. Exactly enough to reach zero, and no cap - his call, and
       the right one: the fixed construction retainer that this generalises had
       to be re-set five times in a single playtest because a number picked once
       goes stale the moment the sector grows. Covering the loss self-scales.

       WHAT IT COSTS. Real money, and the city will go overdrawn to pay it - also
       his call. An overdraft is priced as debt principal now, so the bill for an
       over-generous policy arrives as a worse interest rate rather than as a
       silent failure to pay. A policy that quietly stops working when it matters
       most is the failure mode the old retainer already had.

       WHY IT IS A CAPITAL CONTRIBUTION, NOT REVENUE. It moves cash from the
       city to the sector and touches neither income statement. The sector's
       books still say it lost money, because it did - what changed is that
       somebody else absorbed it. Booking it as sector revenue would have been
       easier and would have made every subsidised sector report a permanent
       break-even, hiding the very thing the player needs to see to decide
       whether to keep paying.
       ==================================================================== */

    private final boolean[] autoSubsidy = new boolean[PolicySector.values().length];
    private final double[] subsidyPaid = new double[PolicySector.values().length];

    public boolean isAutoSubsidised(PolicySector sector){
        return autoSubsidy[sector.ordinal()];
    }

    public void setAutoSubsidised(PolicySector sector, boolean on){
        autoSubsidy[sector.ordinal()] = on;
    }

    /** What this sector was paid this month. Zero when it did not need it. */
    public double getSubsidyPaid(PolicySector sector){
        return subsidyPaid[sector.ordinal()];
    }

    public double getTotalSubsidyPaid(){
        double total = 0;
        for (double d : subsidyPaid) total += d;
        return total;
    }

    /**
     * Tops a protected sector up to break-even.
     *
     * @return what was paid, so the caller can hand the loss counter the figure
     *         AFTER support rather than before it
     */
    /**
     * One subsidy payment against a stated loss, for PolicyCheck.
     *
     * Package-private and named for what it is. It calls the real method rather
     * than reproducing it, because a test helper that does its own arithmetic
     * agrees with itself and proves nothing about the code that ships.
     */
    double subsidiseForTest(PolicySector sector, double netIncome){
        return paySubsidyIfOwed(sector, netIncome);
    }

    /**
     * Puts the treasury at a stated figure, for a fixture that needs to CAUSE a
     * condition rather than wait for one.
     *
     * Package-private, and the only reason it exists: RestructureCheck has to
     * put a city in front of a bond it cannot afford, and has to give another
     * one enough cash to attempt eight round trips. Playing a city into either
     * state would make the test about the trajectory instead of the rule.
     */
    void setCashForTest(double amount){
        this.cash = amount;
    }

    private double paySubsidyIfOwed(PolicySector sector, double netIncome){

        subsidyPaid[sector.ordinal()] = 0;

        if (!autoSubsidy[sector.ordinal()] || netIncome >= 0) {
            return 0;
        }

        double owed = -netIncome;

        cash -= owed;                       // overdrawn if it must be
        creditSectorCash(sector, owed);
        subsidyPaid[sector.ordinal()] = owed;
        return owed;
    }

    /**
     * Moves cash into a sector's own books.
     *
     * The six sectors keep cash in five different places - Retail and Real
     * Estate share CommercialHandler - so this switch is the one place that has
     * to know which. Every branch is a plain add: nothing here may compute an
     * amount, or the money the city spent and the money the sector received
     * could differ, which is precisely the money-from-nowhere this codebase has
     * produced before.
     */
    private void creditSectorCash(PolicySector sector, double amount){

        CommercialHandler ch = economyManager.getCommercialHandler();

        switch (sector) {
            case RETAIL -> ch.setCommercialCash(ch.getCommercialCash() + amount);
            case REAL_ESTATE -> ch.setRealEstateCash(ch.getRealEstateCash() + amount);
            case INDUSTRY -> {
                IndustrialHandler ih = economyManager.getIndustrialHandler();
                ih.setIndustrialCash(ih.getIndustrialCash() + amount);
            }
            case CONSTRUCTION -> {
                ConstructionHandler c = servicesManager.getConstructionHandler();
                c.setCash(c.getCash() + amount);
            }
            case HEAVY_INDUSTRY -> {
                HeavyIndustryHandler h = economyManager.getHeavyIndustryHandler();
                h.setCash(h.getCash() + amount);
            }
            case MINING -> {
                MiningHandler m = economyManager.getMiningHandler();
                m.setCash(m.getCash() + amount);
            }
        }
    }

    /**
     * Construction capacity the current subsidy keeps alive.
     *
     * What the player actually wants to know when setting the figure: not "how
     * much am I spending" but "how many depots does that keep". Capped at the
     * capacity that exists, because a subsidy cannot protect plant nobody owns.
     */
    public double getSubsidisedCapacity(){
        return protectedConstructionCapacity();
    }

    /**
     * Construction capacity the standing policy keeps alive.
     *
     * ALL of it, when the policy is on. The old retainer bought a slice - a
     * dollar figure divided by what a point of capacity costs to keep - which is
     * why it went stale: the slice shrank every time the sector grew. Protecting
     * the sector means protecting the sector.
     *
     * Counted as DEMAND rather than bolted on as a floor, which is what it is:
     * the city has undertaken to keep those crews available, so from the
     * sector's side that capacity is spoken for, and every rule downstream keeps
     * working untouched.
     */
    private double protectedConstructionCapacity(){
        return isAutoSubsidised(PolicySector.CONSTRUCTION)
                ? buildingManager.getTotalConstructionCapacity()
                : 0;
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

    public double getRoadRatio(){
        return servicesManager.getRoadRatio();
    }

    /** The ore market, for the mining screen. */
    public IronMarket getIronMarket(){
        return economyManager.getIronMarket();
    }

    public MiningHandler getMiningHandler(){
        return economyManager.getMiningHandler();
    }

    /** The road network itself, for the infrastructure screen. */
    public InfrastructureManager getInfrastructureManager(){
        return servicesManager.getInfrastructureManager();
    }

    public LandManager getLandManager(){
        return landManager;
    }

    /** Buys the cheapest plot on offer, if the city can afford it. */
    public boolean buyLandBlock(){
        double cost = landManager.buyBlock(cash, populationManager.getPopulation());
        if (cost <= 0) {
            return false;
        }
        cash -= cost;
        return true;
    }

    /**
     * Buys one specific listed plot - the land office screen's action.
     *
     * @return true if it was bought. False means it was not listed or not
     *         affordable, and nothing changed.
     */
    public boolean buyLandParcel(int parcelId){
        double cost = landManager.buyParcel(
                parcelId, cash, populationManager.getPopulation());
        if (cost <= 0) {
            return false;
        }
        cash -= cost;
        return true;
    }

    /** The plots on offer. */
    public java.util.List<LandParcel> getLandListing(){
        return landManager.getListing();
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

        // This month's answer, not last month's. Cleared here rather than in
        // consider() because consider() runs once per sector - clearing it there
        // would leave only whichever sector planned last.
        landBlockedSectors.clear();

        // Shrinking is decided before growing. A sector cannot sensibly do both
        // in one month, and running retirement first means a firm that has just
        // sold capacity is not immediately asked whether it wants more.
        runRetirement();
        lastWriteOff = economyManager.settleInsolvency();

        /*
         * AND THE BANK EATS IT.
         *
         * A restructure writes a sector's debt down to what its assets support,
         * and until now that loss was written off against nothing at all - the
         * money had been lent by a lender who was nobody. It is the bank's loan,
         * so it is the bank's loss, which is what Jerus asked for when he said
         * the bank should be "the one getting billed the horrible bankrupcies".
         *
         * Found by the bank's own balance sheet: equity was moving by more than
         * the month's net income, worst case $23.5M in a single month, because
         * the book shrank without anything recording why. A set of books that
         * articulates is a set of books that catches this.
         */
        bank.writeOff(lastWriteOff);

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
        /*
         * The bank first, and separately - see BusinessInvestment.planBank().
         * Retail's money, retail's investor, but not retail's one decision:
         * asked here so that wanting a branch cannot stop the city building
         * shops for the years it takes to afford one.
         */
        consider(businessInvestment.planBank(),
                sectorInvestor(BusinessDebtManager.RETAIL), "Bank");

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
                buildingManager.getUnderConstructionByCategory(BuildingType.INDUSTRIAL),
                // The shops' import bill is this sector's missed sales.
                ch.getReportGlobalImports()),
                sectorInvestor(BusinessDebtManager.INDUSTRY));

        /*
         * Mines before mills, because a mill's business case is the ore.
         *
         * planHeavyIndustry() refuses to build into ore that does not exist, so
         * running it first would have it decline every month and the cluster
         * would never start. This ordering is the whole reason the two sectors
         * can bootstrap each other: the city buys a deposit, a mine opens on it,
         * the spare ore shows up as a reason to smelt, and a mill follows.
         */
        IronMarket ore = economyManager.getIronMarket();

        refreshLand();
        consider(businessInvestment.planMining(
                landManager.getIronDeposits() - minesCommitted(),
                landManager.getIronReserveTonnes(),
                constructionOutput,
                buildingManager.getUnderConstructionByCategory(BuildingType.MINING)),
                sectorInvestor(BusinessDebtManager.MINING));

        refreshLand();
        consider(businessInvestment.planHeavyIndustry(
                economyManager.getHeavyIndustryHandler().getOreDemand(),
                economyManager.getMiningHandler().getPotentialOutput(),
                constructionOutput,
                buildingManager.getUnderConstructionByCategory(BuildingType.HEAVY_INDUSTRY)),
                sectorInvestor(BusinessDebtManager.HEAVY_INDUSTRY));

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
        syncHouseholdAccounts(true);
    }

    /**
     * The same figures, for the load path, without booking a month twice.
     *
     * rebuildSimulationState() has to repopulate the residents' statement or
     * every row on the People screen reads $0 after a reload - but
     * cumulativeSaving was restored from the save, and update() would add this
     * month to it a second time. One definition of the month, two callers, one
     * of which accrues.
     */
    void refreshHouseholdAccounts(){
        syncHouseholdAccounts(false);
    }

    private void syncHouseholdAccounts(boolean accrue){

        NationalAccounts na = economyManager.getNationalAccounts();

        double wages = populationManager.getTotalWage();

        /*
         * THE BANDED CALCULATION, not a flat rate on the total.
         *
         * This line used to read `wages * economyManager.getTaxRate()`, under a
         * comment claiming it was "the identical formula EconomyManager uses".
         * It was, until the Policy tab added per-band wage offsets in TaxPolicy
         * and nothing updated the copy. From then on the residents were shown
         * paying a different tax from the one the city collected the moment a
         * player touched the wage bands - measured at 23% on a city with one
         * band lowered ten points and another raised ten. With every offset at
         * zero the two agreed exactly, which is why it survived a year.
         *
         * Called fresh rather than read from EconomyManager's stored total,
         * which would import a one-month lag: that field is assigned during the
         * PREVIOUS month's finalUpdateEconomy(), so it would pair this month's
         * wages with last month's tax. Same function, current inputs.
         */
        double[] staffedPerType = populationManager.getStaffedWagePerType();
        TaxPolicy tax = economyManager.getTaxPolicy();
        double wageTax = tax.wageTaxOn(staffedPerType, null);

        /*
         * The pension flows, both directions, from the same source the city's
         * own books use - so the money the workers are shown losing is exactly
         * the money the city is shown collecting, and the same for what the
         * pensioners receive. Two copies of either figure is how this codebase
         * has produced money from nowhere four times.
         */
        double contributions = economyManager.getContributions();
        double pensions = economyManager.getPensionsPaid();

        /*
         * What the people paid the health service, taken from the SAME figure
         * the city is shown collecting rather than recomputed here. Fee revenue
         * credited to the city and debited to nobody would be money from
         * nowhere, which is the exact failure these books exist to catch - and
         * two copies of one number is how this codebase has produced it before.
         */
        double healthFees = healthcare.getFees();

        /*
         * ...and what they paid the schools, from the same figure the city is
         * shown collecting. Tuition was real money leaving real households and
         * appeared on nobody's statement - the city banked it and the people
         * who paid it were never debited, which is the same money-from-nowhere
         * the health fees were fixed for.
         */
        double schoolFees = education.getFees();

        /* ---- the rows, needed by the balance sheet and the split alike ---- */
        int rowCount = HouseholdAccounts.RETIRED + 1;
        double[] rowPeople = new double[rowCount];
        double[] rowHomes  = new double[rowCount];
        for (PayTier t : PayTier.values()) {
            rowPeople[t.ordinal()] = families.peopleIn(t);
            rowHomes[t.ordinal()]  = families.workingHouseholdsIn(t);
        }
        rowPeople[HouseholdAccounts.RETIRED] = families.retiredPeople();
        rowHomes[HouseholdAccounts.RETIRED]  = families.retiredHouseholds();

        double interestPaid = householdBalance.totalInterest();
        households.setPensionPerSenior(tax.pensionPerSenior());

        /*
         * WHAT THE PEOPLE PAID, READ FROM WHO TOOK IT.
         *
         * These were na.getConsumptionHousing() and na.getConsumptionGoods(),
         * which are the same two figures - NationalAccounts.update() is handed
         * exactly these - but only once the accounts have been re-struck. On
         * the load path they have NOT been: rebuildSimulationState() rebuilds
         * the residents' statement before the national accounts, so a reloaded
         * city fed its households a month of zero rent and zero shopping. It
         * did not matter while nothing read the result; the budget constraint
         * reads it, and SaveFileCheck caught the difference at eight cents.
         *
         * One source, both paths, and it is the one that took the money.
         */
        CommercialHandler shopsAndFlats = economyManager.getCommercialHandler();
        double rentPaid = shopsAndFlats.getReportRentIncome();
        double retailSales = shopsAndFlats.getGrossRevenue();

        if (accrue) {
            households.update(
                    wages, wageTax,
                    rentPaid, retailSales,
                    contributions, pensions, healthFees, schoolFees, interestPaid,
                    populationManager.getPopulation(),
                    populationManager.getWorkforce(),
                    populationManager.getJobsFilled());
        } else {
            households.refresh(
                    wages, wageTax,
                    rentPaid, retailSales,
                    contributions, pensions, healthFees, schoolFees, interestPaid,
                    populationManager.getPopulation(),
                    populationManager.getWorkforce(),
                    populationManager.getJobsFilled());
        }

        /* ---- and the same month, split seven ways ---- */
        double[] wagePerTier = populationManager.getStaffedWagePerTier();
        double[] taxPerTier = tax.wageTaxPerTier(staffedPerType, null);

        double[] interestPerRow = new double[rowCount];
        for (int r = 0; r < rowCount; r++) {
            interestPerRow[r] = householdBalance.getInterest(r) * rowHomes[r];
        }

        households.updateByTier(wagePerTier, taxPerTier, rowPeople, rowHomes,
                householdBalance.plannedShare(), interestPerRow);

        /* =================== AND THE BALANCE SHEET ===================
         *
         * Settles the month against what was actually spent, then works out
         * what the households can afford next month - which CommercialHandler
         * reads at the top of it as the cap on retail demand. The one place a
         * budget constraint can live without a circular dependency: the shops
         * cannot know what people can spend until people have been paid, and
         * people cannot be paid until the shops have sold.
         *
         * Not run on the load path. rebuildSimulationState() calls this with
         * accrue false, and settling a month that has already been settled
         * would draw a second month of savings out of the same people.
         */
        double[] disposable = new double[rowCount];
        double[] fees = new double[rowCount];
        double[] actualShopping = new double[rowCount];
        for (int r = 0; r < rowCount; r++) {
            disposable[r] = households.getRowDisposable(r);
            fees[r] = households.getRowHealthcare(r) + households.getRowTuition(r);
            actualShopping[r] = households.getRowShopping(r);
        }
        CommercialHandler shops = economyManager.getCommercialHandler();

        /*
         * PER CELL, since 2026-09-10. The balance is handed the census - who
         * lives in each shape at each tier, straight off the family model -
         * and the ROW money above, and splits the money across the cells
         * itself by the one rule that makes the cells sum to the rows. The
         * row arrays are still what the books show; the cells are what the
         * households are.
         */
        if (accrue) {
            householdBalance.advanceMonth(families::get, disposable,
                    households.rentPerHousehold(), fees, actualShopping,
                    shops.getStoreSellPrice(), debtManager.getRate(),
                    shops.getSupplyRatio());
        } else {
            /*
             * The plan only. The savings and the debt came out of the save;
             * settling the month again would draw a second time on the same
             * money - and NOT re-striking the plan leaves the tiers' shopping
             * split by headcount for a month, which is the load-path parity
             * bug SaveFileCheck catches to the cent.
             */
            householdBalance.planOnly(families::get, disposable,
                    households.rentPerHousehold(), fees,
                    shops.getStoreSellPrice(), debtManager.getRate());

            /*
             * ...AND THE TIERS SPLIT AGAIN, ON THE PLAN THAT WAS JUST STRUCK.
             *
             * updateByTier() ran twenty lines above this with plannedShare() as
             * it stood BEFORE planOnly() - which on a freshly loaded city is all
             * zeros, because the plan is a flow and the save does not carry it.
             * A zero share is not a fallback: rowShopping[r] = shopping * 0, so
             * every pay tier's shopping column read $0 while the same screen's
             * per-shape grid showed $216-$1,793. The unskilled row then footed
             * to "+$674 left over" on a tier that is actually underwater, which
             * is finding #4 in claude/simulation-findings.md and was filed as a
             * separate bug from #15. It is #15: one read, one line too early.
             *
             * The second call is not a re-derivation of the month - updateByTier
             * is a plain split of figures it is handed, and it is handed exactly
             * the same ones. Only the share has changed, from a zero that meant
             * "not struck yet" to the plan the load path exists to re-strike.
             */
            households.updateByTier(wagePerTier, taxPerTier, rowPeople, rowHomes,
                    householdBalance.plannedShare(), interestPerRow);
        }

        shops.setSpendingCapacity(householdBalance.getSpendingCapacity());
        shops.setWantedSpend(householdBalance.getWantedSpend());

        if (accrue) {
            /*
             * What the bank lent the families, what came back, what they paid
             * for it, and what it will never see again.
             *
             * These four cross the audit's boundary in both directions, because
             * households are outside the pools and always have been - so unlike
             * a business loan, this money really does leave and arrive. The
             * write-off is not among them: it costs the bank its BOOK, not its
             * cash, since the money went out the door in some earlier month.
             */
            bank.lendToHouseholds(householdBalance.totalBorrowed());
            bank.takeFromHouseholds(householdBalance.totalRepaid(),
                    householdBalance.totalInterest());
            bank.writeOff(householdBalance.getWrittenOff());
        }
    }

    /**
     * Sells the bank's paid-in capital as shares.
     *
     * The households first, out of their savings, the world for the rest -
     * "specially the bank, they need equity to avoid rough start". A founding
     * branch is bought on prospects; a later one on the bank's record, and a
     * bank the world will not fund opens its branch under-capitalised, which
     * is what a bank nobody will fund is.
     */
    private void capitaliseBank(double wanted) {
        if (wanted <= 0) return;
        double before = equity.getRaisedHomeThisMonth(Equity.BANK);
        double beforeAbroad = equity.getRaisedAbroadThisMonth(Equity.BANK);
        equity.offer(Equity.BANK, wanted, Math.max(0, bank.equity()),
                householdBalance, DebtManager.WORLD_BASE_RATE);
        bank.injectCapital(equity.getRaisedHomeThisMonth(Equity.BANK) - before,
                equity.getRaisedAbroadThisMonth(Equity.BANK) - beforeAbroad);
    }

    /**
     * Pays every company's owners on its last closed month.
     *
     * A fixed share of a positive net income - Jerus: "net income, not cash,
     * and only if it's positive" - from the till, and from what the company
     * holds abroad when the till is short. Nothing is borrowed for it, and a
     * bank short of capital pays nothing. The households' part lands in their
     * savings this month; the world's leaves on the income account.
     */
    private void payDividends() {
        // Whoever left this month took their shares with them.
        equity.followEmigrants(householdBalance);
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            double paid;
            if (c == Equity.BANK) {
                double due = bank.isInsolvent() || bank.recapitalisationNeeded() > 0 ? 0
                        : equity.dividendDue(c, bank.getProfitLastMonth());
                paid = Math.min(due, Math.max(0, bank.getCash()));
                if (paid > 0) bank.payDividend(paid);
            } else {
                String sector = Equity.COMPANIES[c];
                SectorBooks.SectorMonth m = sectorBooks.get(PolicySector.byCreditName(sector));
                double due = equity.dividendDue(c, m.netIncome());
                if (due <= 0) continue;
                double till = economyManager.getSectorCash(sector);
                if (till < due) till += outward.recall(sector, due - till, economyManager);
                paid = Math.min(due, Math.max(0, till));
                if (paid > 0) {
                    economyManager.setSectorCash(sector, till - paid);
                    economyManager.recordDividendPaid(sector, paid);
                }
            }
            if (paid > 0) equity.payDividend(c, paid, householdBalance);
        }
    }

    /**
     * Re-reads the bank off the city it is banking.
     *
     * STRUCK AT THE END OF THE MONTH, once, on both paths, and that placement
     * is the whole point of the method existing. It first lived inside the
     * household strike, which runs in the middle of the month - so the bank's
     * loan book was a mid-month snapshot taken before the month's lending had
     * happened, and the figure on the bank screen disagreed with the lender's
     * own principal by whatever was borrowed after it. Everything this reads is
     * settled by the time the month is over: the families have been struck, the
     * sectors have banked, and the debts have been processed.
     *
     * Its deposits are what the city has banked with it and its book is what
     * the city owes it. Both are re-derived rather than accumulated, because
     * neither is a flow: the position is whatever is standing at the end of the
     * month, and a bank that added up its own history would drift from it.
     */
    private void refreshBank() {
        /*
         * WHAT THE SECTORS ARE IN CREDIT FOR, not what they are worth on net.
         *
         * This used to be the plain sum, so a sector deep in overdraft cancelled
         * out another sector's savings and the bank's deposit book came out
         * smaller than the money it was actually holding. An overdraft is not a
         * negative deposit; it is a loan, and it is already on the other side of
         * the balance sheet as one.
         *
         * It also has to be THIS weighting, because it is the weighting
         * printCityStats() uses when it hands the deposit interest back out
         * (each sector in proportion to max(0, its cash)). Charging the interest
         * on one base and paying it on another is how the money went missing -
         * see Bank.fundToCover().
         */
        double sectorCash = 0;
        for (String s : BusinessDebtManager.SECTORS) {
            sectorCash += Math.max(0, economyManager.getSectorCash(s));
        }
        bank.refresh(
                buildingManager.countByName("Commercial Bank"),
                householdBalance.totalSavings(),
                sectorCash,
                economyManager.getBusinessDebtManager().getAllPrincipal(),
                debtManager.getAllPrincipal(),
                householdBalance.bookOwed());

        /*
         * ...AND WHAT THAT BOOK WEIGHS.
         *
         * Walked loan by loan, here rather than in the bank, because the bank
         * cannot see the individual instruments and has no business knowing what
         * a serial bond is. Jerus asked why a treasury bill should tie up the
         * same capacity as a twenty-year industrial mortgage; it does not any
         * more, and this is the loop that works out by how much.
         *
         * Household credit is revolving - no term, never runs off - so it takes
         * the full weight and is not walked.
         */
        double businessWeighted = 0;
        for (BusinessDebt loan : economyManager.getBusinessDebtManager().getLoans()) {
            businessWeighted += loan.getOutstandingPrincipal()
                    * Bank.RISK_BUSINESS * Bank.maturityWeight(loan.getRemainingMonths());
        }
        double cityWeighted = 0;
        for (Debt paper : debtManager.getDebt()) {
            cityWeighted += paper.getOustandingPrincipal()
                    * Bank.RISK_CITY * Bank.maturityWeight(paper.getRemainingMonths());
        }
        bank.setWeightedBook(businessWeighted, cityWeighted,
                householdBalance.bookOwed() * Bank.RISK_HOUSEHOLD);
    }

    /**
     * The treasury puts capital into its bank.
     *
     * An insolvent bank has no capacity, so it cannot lend, so every borrower in
     * the city pays the maximum premium and nothing gets built. This is the
     * lever out of that, and it is a real one - the money leaves the treasury
     * and does not come back.
     *
     * SEE Bank.receiveBailout() for the hole in this that Jerus spotted while it
     * was being written: the city's own borrowing is funded BY this bank, so a
     * treasury that borrows in order to do this has the bank capitalise itself
     * with its own loan. Waiting on a source of funds that is not this bank.
     *
     * @return what was actually put in, which is nothing if the city cannot pay
     */
    public double recapitaliseBank(double amount) {
        double put = Math.max(0, Math.min(amount, cash));
        if (put <= 0) return 0;
        cash -= put;
        bank.receiveBailout(put);
        GameLog.note(String.format("The city put $%,.0fk of capital into the bank.", put));
        return put;
    }

    /** What it would cost to put the bank back on its feet, right now. */
    public double bankRecapitalisationNeeded() {
        return bank.recapitalisationNeeded();
    }

    /**
     * The treasury buys foreign currency, adding to the city's reserves.
     *
     * Real money: the local cash leaves the city to pay for it. Reserves bought
     * today are what defends the currency tomorrow, and they cost exactly what
     * anything else the treasury might have bought would have cost.
     *
     * @return what was actually bought, which is nothing the city cannot pay for
     */
    public double buyForeignCurrency(double amount) {
        double spend = Math.max(0, Math.min(amount, cash));
        if (spend <= 0) return 0;
        cash -= spend;
        foreign.buyReserves(spend);
        GameLog.note(String.format("The city bought $%,.0fk of foreign currency.", spend));
        return spend;
    }

    /**
     * ...and sells it, which is what defending a currency actually consists of.
     *
     * Selling reserves puts local currency back in the treasury and takes the
     * city's foreign position down - which lowers its import cover, which raises
     * the pressure on the rate. A defence that does not fix the trade balance
     * underneath it makes the next month worse, and that is not a bug in this
     * model, it is the entire history of currency defences.
     */
    public double sellForeignCurrency(double amount) {
        double sold = foreign.sellReserves(Math.max(0, amount));
        if (sold <= 0) return 0;
        cash += sold;
        GameLog.note(String.format("The city sold $%,.0fk of its reserves.", sold));
        return sold;
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

        /*
         * Standing policy first: a sector the city has undertaken to protect is
         * brought up to break-even BEFORE the loss counter sees the month, which
         * is the whole point - six consecutive losses is what makes a sector
         * start selling its capacity, so a subsidy that arrives after the count
         * protects nothing.
         */
        double[] net = {
            ch.getReportRetailNetIncome(),
            ch.getReportRealEstateNetIncome(),
            ih.getNetIncome(),
            construction.getNetIncome(),
            economyManager.getHeavyIndustryHandler().getNetIncome(),
            economyManager.getMiningHandler().getNetIncome()
        };

        for (PolicySector sector : PolicySector.values()) {
            double covered = paySubsidyIfOwed(sector, net[sector.ordinal()]);
            businessInvestment.recordSectorResult(sector.creditName(),
                    net[sector.ordinal()] + covered);
        }

        // Housing: demand is people actually living in it. Empty units can go;
        // occupied ones can never be scrapped out from under anyone.
        int shed = retire(businessInvestment.planRetirement(
                BusinessDebtManager.REAL_ESTATE, BuildingType.RESIDENTIAL,
                population, getHouseholdCapacity(),
                buildingManager.getUnderConstructionByCategory(BuildingType.RESIDENTIAL)),
                sectorInvestor(BusinessDebtManager.REAL_ESTATE));
        if (shed == 0) distress(BusinessDebtManager.REAL_ESTATE, BuildingType.RESIDENTIAL);

        shed = retire(businessInvestment.planRetirement(
                BusinessDebtManager.RETAIL, BuildingType.COMMERCIAL,
                population, storeCoverage,
                buildingManager.getUnderConstructionByCategory(BuildingType.COMMERCIAL)),
                sectorInvestor(BusinessDebtManager.RETAIL));
        if (shed == 0) distress(BusinessDebtManager.RETAIL, BuildingType.COMMERCIAL);

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
        shed = retire(businessInvestment.planRetirement(
                BusinessDebtManager.INDUSTRY, BuildingType.INDUSTRIAL,
                Math.min(storeCoverage, population),
                buildingManager.getFoodProduction(),
                buildingManager.getUnderConstructionByCategory(BuildingType.INDUSTRIAL)),
                sectorInvestor(BusinessDebtManager.INDUSTRY));
        if (shed == 0) distress(BusinessDebtManager.INDUSTRY, BuildingType.INDUSTRIAL);

        // Construction's demand is the queue: work ordered and not yet done,
        // capped at what its plant could deliver in a month. An empty queue
        // means every depot it owns is spare.
        double capacity = buildingManager.getTotalConstructionCapacity();
        double workAvailable = Math.min(
                buildingManager.getRemainingConstructionPoints(), capacity);

        /*
         * A subsidised depot has a customer: the city.
         *
         * Rather than bolting a floor onto planRetirement(), the subsidy simply
         * counts as demand - which is what it is. The city is paying to keep
         * those crews available, so from the sector's side that capacity is
         * spoken for, and every rule downstream (the headroom, the gradual
         * shedding, the loss counter) keeps working untouched.
         */
        double demand = Math.max(workAvailable, protectedConstructionCapacity());

        shed = retire(businessInvestment.planRetirement(
                BusinessDebtManager.CONSTRUCTION, BuildingType.CONSTRUCTION,
                demand, capacity,
                buildingManager.getUnderConstructionByCategory(BuildingType.CONSTRUCTION)),
                sectorInvestor(BusinessDebtManager.CONSTRUCTION));
        if (shed == 0) distress(BusinessDebtManager.CONSTRUCTION, BuildingType.CONSTRUCTION);

        /*
         * THE TWO SECTORS THAT COULD NOT SHRINK. Until 2026-09-10 there was no
         * call here for either - four planRetirement() calls, four sectors -
         * and the two without one were the two that ended every long run
         * ruined. A price-taking exporter always sells what it makes, so the
         * spare-capacity rule above has nothing to measure for it; what it has
         * is the distress rule, which asks only whether it can pay its way.
         */
        distress(BusinessDebtManager.HEAVY_INDUSTRY, BuildingType.HEAVY_INDUSTRY);
        distress(BusinessDebtManager.MINING, BuildingType.MINING);
    }

    /**
     * The fallback for a sector the spare-capacity rule would not touch: if it
     * is overdrawn after the credit desk has had its turn, it sheds anyway.
     * See BusinessInvestment.planDistressRetirement().
     */
    private void distress(String sector, BuildingType category) {
        retire(businessInvestment.planDistressRetirement(
                sector, category,
                economyManager.getSectorCash(sector),
                buildingManager.getUnderConstructionByCategory(category)),
                sectorInvestor(sector));
    }

    /**
     * Scraps what the decision named, and sells the plot back to the city.
     *
     * @return how many buildings went, so a caller can tell whether to try
     *         the distress rule next
     */
    private int retire(BusinessInvestment.Decision decision, Investor seller){

        if (decision == null || !decision.build) {
            return 0;   // planRetirement's reasons are noise on the sector screens
        }

        int scrapped = buildingManager.retire(decision.template, decision.quantity);
        if (scrapped <= 0) {
            return 0;
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

        /*
         * Construction shedding capacity is the one retirement worth
         * interrupting the player about.
         *
         * Every other sector shrinking is the market working: too many shops,
         * fewer shops. Construction shrinking is different because it is the
         * bottleneck on everything else - the 4,000-month playtest showed a city
         * buy four depots, double its population, and lose all four sixty months
         * later, and the only signal was a number on a screen nobody had open.
         *
         * Recorded rather than popped up, because it happens inside a skip. The
         * UI decides when to say it.
         */
        if (BusinessDebtManager.CONSTRUCTION.equals(decision.sector)) {
            constructionShedMonth = month;
            constructionShedPoints += decision.template.getProduction1() * scrapped;
        }
        return scrapped;
    }

    /* =================== THE CONSTRUCTION WARNING ===================
     *
     * Two facts and a question. The facts: when construction last sold
     * capacity, and how much it has sold since anyone last looked. The
     * question the player actually needs asked is "do you want to pay to
     * stop this", and the answer is one screen away.
     * ============================================================== */

    private int constructionShedMonth = -1;
    private double constructionShedPoints;

    /**
     * Whether the city should be told construction is dismantling itself.
     *
     * Only while it is RECENT and the retainer is not already covering the
     * capacity that is left. A player who has set a subsidy has answered the
     * question and should not keep being asked it.
     */
    /**
     * Puts the warning back where it stood.
     *
     * The load path's entry point, and the one place that decides what an
     * absent or nonsensical month means. Months are 1-based, so anything at or
     * below zero is "no warning on file" - which covers a save written before
     * this was carried, and any future decoder that zero-fills rather than
     * running the field initialiser.
     */
    public void restoreConstructionShedding(int month, double points){
        this.constructionShedMonth = (month > 0) ? month : -1;
        this.constructionShedPoints = Math.max(0, points);
    }

    public boolean isConstructionShedding(){

        if (constructionShedMonth < 0 || month - constructionShedMonth > 24) {
            return false;
        }
        return getSubsidisedCapacity()
                < buildingManager.getTotalConstructionCapacity() - 1e-9;
    }

    public int getConstructionShedMonth()    { return constructionShedMonth; }
    public double getConstructionShedPoints(){ return constructionShedPoints; }

    /** The player has seen it. Stops the banner nagging about old news. */
    public void acknowledgeConstructionShedding(){
        constructionShedMonth = -1;
        constructionShedPoints = 0;
    }

    /* =====================================================================
       PRIVATE INVESTMENT WITH NOWHERE TO GO
       ===================================================================== */

    /**
     * Sectors that wanted to build this month and had no land to build on.
     *
     * Measured over 1,951 months of an ordinary game, real estate spent 12.2%
     * of every month in this state and once sat in it for 120 CONSECUTIVE
     * MONTHS - ten years wanting to build houses with nowhere to put them, in a
     * city whose job market could have filled them. Nothing said so. The player
     * finds out when the city stops growing, and by then the only visible
     * symptom is a number that has stopped moving.
     *
     * Land is deliberately the player's to buy - that is the game - which is
     * exactly why this has to be visible. A constraint the player is meant to
     * clear, and cannot see, is not a decision; it is a trap.
     */
    private final java.util.Set<String> landBlockedSectors = new java.util.LinkedHashSet<>();

    public java.util.Set<String> getLandBlockedSectors(){
        return java.util.Collections.unmodifiableSet(landBlockedSectors);
    }

    /**
     * Whether to warn that the private sector is out of room.
     *
     * DERIVED, NOT CARRIED, and that is the whole reason it needs no save
     * format. The shedding warning had to be saved because it reports an EVENT
     * - crews were laid off in some month, and the month it happened in is not
     * recoverable from the city that is left. This reports a STANDING CONDITION:
     * somebody wants to build and there is no land. It is recomputed from the
     * month's own decisions, so a reloaded city re-derives it, and if the
     * condition is still true the warning is still true - which is correct,
     * not a bug. Acknowledging it is likewise per-session: the player dismissed
     * the situation as it stood, and on a fresh look the situation is asked
     * again.
     */
    private int landWarningAcknowledged = -1;

    public boolean isPrivateInvestmentLandLocked(){
        if (landBlockedSectors.isEmpty()) {
            return false;
        }
        return landWarningAcknowledged < 0 || month - landWarningAcknowledged > 24;
    }

    /** The player has seen it. */
    public void acknowledgeLandLock(){
        landWarningAcknowledged = month;
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
     *
     * TWO SIDES OF THE SAME SCALE, AND THEY USED NOT TO MATCH.
     *
     * getCostOf() takes a quantity; estimatedMonthlyProfit() does not - it is
     * documented as what "a finished building" earns, singular. This method
     * passed the first against the second, so the brake compared ONE building's
     * rent with the interest on the WHOLE order, and the mismatch got worse
     * exactly as the order got bigger.
     *
     * That is not a rounding error, it is a deadlock. Measured on a city at 15%
     * job fill with 3,024 unhoused demand: real estate asked for 98 houses,
     * $9,825k, borrowing $6,162k at 2.28% - $11.71k of interest a month against
     * the $1.40k of rent ONE house pays. Declined. With the order's own rent,
     * $137.20k, it clears by nine times. It stalled 34 straight months and only
     * built again once the city had collapsed far enough for the order to shrink
     * - the shortage tightening the very brake that was stopping it being fixed.
     *
     * AND IT NEVER SETTLES FOR LESS. 42 of those 98 houses would have passed.
     * The old code took the order or left it, so it built nothing. Now the
     * project is trimmed to the largest slice that carries itself, because a
     * business short of credit builds four floors instead of ten - it does not
     * go home.
     *
     * Scanning down from the requested quantity rather than solving for it: the
     * test is very nearly monotone in quantity but not exactly, since the
     * materials shortfall kinks once the yard is empty. The first size that
     * passes on the way down is the largest that passes, whatever the shape.
     */
    private void consider(BusinessInvestment.Decision decision, Investor payer){
        consider(decision, payer, null);
    }

    /**
     * @param label where to file the advisor's line, when the sector is asked
     *              more than one question a month. The bank branch is retail's
     *              money but not retail's shop decision, and filing both under
     *              "Retail" meant whichever ran second erased the other.
     */
    private void consider(BusinessInvestment.Decision decision, Investor payer, String label){

        String slot = label != null ? label
                : (decision == null ? "?" : decision.sector);

        if (decision == null || !decision.build) {
            if (decision != null && decision.landBlocked) {
                landBlockedSectors.add(decision.sector);
            }
            lastInvestment.put(slot, decision == null ? "" : "Holding: " + decision.reason);
            return;
        }

        BusinessDebtManager credit = economyManager.getBusinessDebtManager();

        double rate = credit.getRate(decision.sector);
        double perUnitProfit = businessInvestment.estimatedMonthlyProfit(
                decision.sector, decision.template);

        /*
         * THE OWNERS FIRST, since 2026-09-10 (evening). Before a sector reads
         * its till and borrows the rest, it asks the register whether this is
         * a plan it should sell shares for - every plan while it is new, the
         * horizon's worth in a good year, nothing in a bad one - and puts the
         * offering to the households, then the world. What is raised is in
         * the till by the time the cash-then-debt split below runs; what is
         * not is borrowed as it always was. See Equity.raiseFor().
         *
         * And what the sector holds ABROAD comes home before it borrows a
         * cent: a war chest parked at the world's rate was invisible to this
         * method, which read the till and borrowed against an empty one.
         */
        int company = Equity.indexOf(decision.sector);
        if (company >= 0) {
            SectorBooks.SectorMonth books = sectorBooks.get(PolicySector.byCreditName(decision.sector));
            double planCost = businessInvestment.getCostOf(decision.template, decision.quantity);
            double ask = equity.raiseFor(company, books.totalAssets(), books.equity(), planCost);
            if (ask > 0) {
                double raised = equity.offer(company, ask, books.equity(),
                        householdBalance, DebtManager.WORLD_BASE_RATE);
                if (raised > 0) {
                    economyManager.setSectorCash(decision.sector,
                            economyManager.getSectorCash(decision.sector) + raised);
                    economyManager.recordEquityRaised(decision.sector, raised);
                }
            }
            double short_ = planCost - payer.getCash();
            if (short_ > 0) outward.recall(decision.sector, short_, economyManager);
        }

        double cash = payer.getCash();

        // A banned sector can still build what its own cash covers; it cannot
        // borrow the difference. Said here, in the same words the People and
        // credit screens use, rather than discovered as a silent buildFor()
        // refusal that would have read as "land went" below.
        if (credit.isBorrowingBlocked(decision.sector)
                && businessInvestment.getCostOf(decision.template, 1) > cash) {
            lastInvestment.put(slot,
                    String.format("Holding: borrowing ban, %d more months - %s would need credit",
                            credit.getBlockedMonths(decision.sector),
                            decision.template.getName()));
            return;
        }

        boolean banned = credit.isBorrowingBlocked(decision.sector);
        int affordable = 0;
        for (int n = decision.quantity; n >= 1; n--) {
            double cost = businessInvestment.getCostOf(decision.template, n);
            double borrowed = Math.max(cost - cash, 0);
            // Under a ban the largest slice is the one its own cash pays for.
            if (banned && borrowed > 0) continue;
            if (businessInvestment.servicesItsOwnDebt(perUnitProfit * n, borrowed, rate)) {
                affordable = n;
                break;
            }
        }

        if (affordable <= 0) {
            lastInvestment.put(slot,
                    String.format("Declined %s - not even one would cover its interest",
                            decision.template.getName()));
            return;
        }

        int quantity = affordable;
        String trimmed = quantity < decision.quantity
                ? String.format(" (trimmed from %,d - could not carry the interest)",
                        decision.quantity)
                : "";

        if (buildFor(payer, decision.template, quantity)) {
            lastInvestment.put(slot,
                    String.format("Built %,d %s%s - %s",
                            quantity, decision.template.getName(), trimmed, decision.reason));
        } else {
            // The plan cleared every test and the build still did not happen,
            // which now has exactly one cause: the land went between planning
            // and buying. Saying so beats the silence this used to leave.
            landBlockedSectors.add(decision.sector);
            lastInvestment.put(slot,
                    String.format("Could not build %s - needs %,.0f sq ft, %,.0f free",
                            decision.template.getName(),
                            decision.template.getLandSqFt() * (double) quantity,
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
                /*
                 * RECORDED, because this is the fourth way a sector's cash
                 * moves and it was the one nothing wrote down. Businesses buy
                 * their own premises out of their own cash, and receive() is
                 * spend() with the sign flipped - so this one line catches both
                 * the buying and the selling back. Without it the cash flow
                 * statement on the sector screen could not close, and a cash
                 * flow statement that does not close is not a statement.
                 */
                sectorInvested.merge(sector, amount, Double::sum);
            }

            // Businesses borrow freely here - the rate rises with leverage and
            // stops at its cap. What stops a spiral is the project having to
            // service its own debt, checked in consider() above.
            //
            // EXCEPT during a borrowing ban. A sector that was just written
            // down is barred from shortfall loans for twelve months
            // (BusinessDebtManager.coverShortfall), and until 2026-09-06 that
            // ban stopped at the shortfall desk: this method said yes to any
            // positive amount, so the same sector could not borrow to keep the
            // lights on but could borrow to expand. The ban is one ban.
            @Override public boolean canBorrow(double amount) {
                // ...AND THE LINE. The ban stopped here from 2026-09-06; the
                // insolvency line did not until 2026-09-10, so a sector could
                // be lent the money to expand straight past the point the
                // lender would write it down. canFundProject() tests the
                // balance sheet after the deal, with the building counted, and
                // is false while barred and while the bank itself is shut.
                return credit.canFundProject(sector, amount);
            }

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

    /**
     * This month's effective construction output: the sector's capacity scaled
     * by how well it is staffed and by what the roads will carry.
     *
     * THE ONE DEFINITION. SimulationEngine.simulateMonth() calls this rather
     * than working it out again, and startOfMonthUpdate() hands the same figure
     * to ConstructionHandler.recogniseWork() as the month's revenue. Those three
     * numbers have to be the same number: the second is what the sites actually
     * advance by and the third is what the sector is paid for, so a copy that
     * drifts from the original books revenue for work nobody did.
     *
     * It very nearly did. The road throttle went into simulateMonth() alone,
     * which left the screen and the revenue line both quoting an uncongested
     * build rate while the sites crawled - construction paid in full for a
     * third of a month's work.
     */
    public int getConstructionOutput(){
        double constructionFillRate = servicesManager.getConstructionHandler().getAverageFill();
        double roadRatio = servicesManager.getRoadRatio();

        /*
         * Sickness slows the sites too, and construction is where a player
         * NOTICES it - a mill producing 8% less food is a number on a report,
         * while a hospital that takes an extra month to open during the
         * epidemic it was meant to end is a story.
         *
         * Same three-way agreement as roads: this figure is the screen's, the
         * sites' and the sector's revenue, so it belongs here rather than being
         * applied in one of the three.
         */
        return (int) Math.round(buildingManager.getTotalConstructionCapacity()
                * constructionFillRate * roadRatio * health.getWorkRatio());
    }

    /**
     * Construction points the housing stock consumes just standing there.
     *
     * A PURE FUNCTION of what is built, deliberately - no field, nothing
     * saved, nothing to go stale. A loaded city computes the same number the
     * saved one did, because the same buildings are standing.
     *
     * MAINTENANCE_PER_YEAR of each residential building's own construction
     * points, which is what makes an apartment block cost more to keep up
     * than a house without anybody having to write that down anywhere.
     */
    public double getHousingMaintenancePoints(){
        return buildingManager.getTotalByCategoryDouble(
                BuildingType.RESIDENTIAL, BuildingsTemplate::getConstructionPoints)
                * CommercialHandler.MAINTENANCE_PER_YEAR / 12;
    }

    /**
     * The same figure for EVERY category, which is what comes off the month.
     *
     * getHousingMaintenancePoints() above is kept because the Real estate
     * screen wants the landlords' share on its own, but the builders' time is
     * not divisible by who is paying for it - a crew repointing a tenement and
     * a crew resurfacing a road are both off the sites.
     *
     * Still a PURE FUNCTION of the standing stock: nothing cached, nothing
     * saved, nothing to go stale.
     */
    public double getMaintenancePoints(){
        double points = 0;
        for (BuildingType category : BuildingType.values()) {
            points += economyManager.maintenancePointsFor(category);
        }
        return points;
    }

    /**
     * What is left of the month's output for the SITES.
     *
     * getConstructionOutput() is still the one definition of what the sector
     * can do in a month, and this is what it can do on NEW work once the
     * standing stock has had its repairs. Two figures, and they are different
     * questions: the screen wants the first (that is the sector's capacity),
     * while advanceConstruction() and recogniseWork() want the second, because
     * a crew repointing a tenement is not advancing a site and is not being
     * paid out of a site's contract.
     *
     * Passing the net figure to recogniseWork() also gets UTILISATION right,
     * which is the part that would have gone quietly wrong: utilisation is
     * work-done over output-available, and a sector whose repairs filled half
     * its month was not half idle.
     *
     * IT STOPPED BEING A ROUNDING ERROR ON 2026-09-09. While only residential
     * paid, repairs were 0.3% to 0.8% of output across 600 months. Now that
     * every category pays, a mature city spends about FORTY PERCENT of its
     * builders on keeping what it already has.
     *
     * That number is not a mistake and it is not tuned. It is forced: this
     * city's standing stock embodies roughly forty years of its own annual
     * construction output, and one percent a year of a forty-year stock is
     * forty percent of a year's output. It is also close to what real
     * economies do - repair and maintenance is something like 40-50% of all
     * construction activity in a developed country. A big city has to run to
     * stand still, and now it does.
     *
     * The floor at zero was written for the day this stopped being small. That
     * day has arrived, and without it a city whose stock had outgrown its
     * builders would advance its sites by a negative number.
     */
    public int getBuildingOutput(){
        return (int) Math.max(0,
                Math.round(getConstructionOutput() - getMaintenancePoints()));
    }

    /**
     * The month's repairs: real estate pays, construction is paid, and the
     * materials are actually consumed.
     *
     * Jerus: "they cost a tiny fraction of construction points and materials,
     * which the real estate company pays to the construcitn company."
     *
     * THE THREE HALVES OF ONE ORDER, and all three have to happen or it is
     * not an order at all:
     *
     *   money      real estate's expense line, construction's turnover
     *   materials  out of the yard, and imported when the yard is short -
     *              exactly what a build does, through the same method
     *   points     off the top of the month's output, in getBuildingOutput()
     *
     * WHY IT IS PRICED ON THE BUILDING AND NOT ON THE RENT. Rent is what the
     * company can charge; repairs are what the building needs. Tying the
     * second to the first would have made a slum free to own, which is the
     * behaviour this is here to stop - an empty home now costs its owner
     * money every month, so building doors nobody wants finally has a price.
     *
     * The materials half is charged at TODAY's material price rather than at
     * what they cost when the building went up, so the bill inflates on its
     * own. A constant in absolute money is the same bug as a cached figure.
     */
    private void chargeBuildingMaintenance(){

        ConstructionHandler builders = servicesManager.getConstructionHandler();
        if (builders == null) return;

        double price = Math.max(0, buildingManager.getConstructionMaterialPrice());

        /*
         * WHO PAYS IS ECONOMYMANAGER'S QUESTION, shaped like the property tax.
         * This method owns the other half: the materials are really consumed,
         * the builders are really paid, and the treasury really settles the
         * share of the bill that belongs to buildings nobody else owns.
         */
        economyManager.chargeMaintenance(price);

        double bill = economyManager.getMaintenanceBillTotal();
        if (!Double.isFinite(bill) || bill <= 0) return;

        /*
         * The materials, for real. handleConstructionMaterials() is the same
         * method every build goes through: it takes what the yard has and
         * imports the rest, and the import is what construction's own
         * materialsExpense - and therefore the trade balance - is built from.
         *
         * The shortfall is worked out BEFORE the call because the call does
         * not report it, and monthlyMaterialImports has to know.
         */
        int wanted = (int) Math.round(economyManager.getMaintenanceMaterialsTotal());
        if (wanted > 0) {
            int shortfall = Math.max(0, wanted - buildingManager.getConstructionMaterials());
            buildingManager.handleConstructionMaterials(wanted);
            materialsConsumed += wanted;
            monthlyMaterialImports += shortfall;
            monthlyMaterialImportBill += shortfall * price;
        }

        /*
         * AND THE CITY SETTLES ITS OWN. Roads, schools, hospitals and the two
         * plants have no company behind them, so the treasury is the owner and
         * the treasury pays - out of cash, here, because unlike a sector's
         * expense there is no income statement between the charge and the
         * money. This is the leg that makes the whole bill real rather than
         * half of it: without it the builders would be paid for work five
         * categories never commissioned, which is money from nowhere.
         */
        double cityShare = economyManager.getCityMaintenanceBill();
        if (cityShare > 0) {
            cash -= cityShare;
            cityMaintenancePaid = cityShare;
        } else {
            cityMaintenancePaid = 0;
        }

        builders.receiveMaintenance(bill);
    }

    /**
     * What the treasury paid this month to keep the city's own buildings up.
     *
     * A FLOW, so it is carried rather than rebuilt - the standing stock says
     * what the bill WOULD be, not what was actually settled, and those differ
     * for a month after anything is built or demolished.
     */
    private double cityMaintenancePaid;

    public double getCityMaintenancePaid() { return cityMaintenancePaid; }

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
                        servicesManager.getRoadRatio(),
                        landManager.getAvailableSqFt(),
                        households.isLivingBeyondIncome(),
                        !buildingManager.getStacksUnderConstruction().isEmpty(),
                        populationManager.getPopulation(),
                        health.getWorkRatio(),
                        health.isOutbreak(),
                        healthcare.getUnburied());
            }

        } catch (RuntimeException e) {
            /*
             * A skip that throws halfway leaves the city part-simulated, and
             * before this the window simply stopped responding: the exception
             * went to the FX thread's default handler and a stderr that does
             * not exist in a packaged build.
             *
             * Swallowed rather than rethrown, deliberately. The months that DID
             * complete are real - cash moved, buildings finished, the autosave
             * before the skip is on disk - so the honest thing is to stop where
             * it stopped, report how far it got, and let the player save or
             * reload. Rethrowing would lose a working city over one bad month.
             */
            GameLog.failure("The simulation failed during month " + (month)
                    + " of a " + months + "-month skip, after " + completed
                    + " completed", e);
            skipFailure = "The simulation stopped after " + completed
                    + " of " + months + " months. The details are in the log:\n"
                    + (GameLog.file() == null ? "(no log file)" : GameLog.file());

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
    public boolean hasNewReceipt(){return hasNewReceipt;}
    public void clearReceipt(){ hasNewReceipt = false;}
    
    public double calculateTotalCost(BuildingsTemplate selected, int quantity) {
        return quoteBuild(selected, quantity).total;
    }

    /**
     * Everything the build screen shows about an order, worked out ONCE.
     *
     * The screen used to re-derive the materials shortage, the import bill
     * and the build time beside calculateTotalCost() - four lines of the same
     * arithmetic in a second file, agreeing with this one right up until the
     * first time either changed. This codebase has been bitten by that shape
     * five times (see processBuildOrder). The quote is the one definition;
     * calculateTotalCost() is its total, and the screen prints its fields.
     */
    public static final class BuildQuote {
        public final int quantity;
        public final double sticker;
        public final double materialsNeeded;
        public final double materialsInStock;
        public final double materialsImported;
        public final double materialsPrice;
        public final double importCost;
        public final double total;
        public final double landNeeded;
        public final double landFree;
        /** Months at today's construction output, or NaN when there is none. */
        public final double months;

        BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock,
                   double materialsPrice, double landNeeded, double landFree, double output,
                   double points) {
            this.quantity = quantity;
            this.sticker = sticker;
            this.materialsNeeded = materialsNeeded;
            this.materialsInStock = materialsInStock;
            this.materialsImported = Math.max(materialsNeeded - materialsInStock, 0);
            this.materialsPrice = materialsPrice;
            this.importCost = materialsImported * materialsPrice;
            this.total = sticker + importCost;
            this.landNeeded = landNeeded;
            this.landFree = landFree;
            this.months = output > 0 ? points / output : Double.NaN;
        }
    }

    public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity) {
        return new BuildQuote(
                quantity,
                selected.getCashCost() * quantity,
                selected.constructionMaterials * (double) quantity,
                buildingManager.getConstructionMaterials(),
                buildingManager.getConstructionMaterialPrice(),
                selected.getLandSqFt() * (double) quantity,
                landManager.getAvailableSqFt(),
                getConstructionOutput(),
                selected.getConstructionPoints() * (double) quantity);
    }
    /**
     * @param noConstruction put the buildings up immediately instead of queueing
     *                       them, and charge nothing for the construction.
     *
     * WHAT THE FLAG ACTUALLY MEANS (backlog item 24)
     *
     * buildStack() has always taken this parameter and this method has always
     * ignored it, hardcoding false. Nothing in the game passed true, so nothing
     * broke - which is exactly what makes a parameter that does nothing worth
     * fixing rather than leaving: the first caller to trust it would have got
     * silently queued buildings and no way to tell.
     *
     * It follows BuildingManager.addStack()'s existing meaning of the same flag:
     * no construction process at all. So no materials are drawn (they are an
     * input to building, not to existing), the construction sector is not paid
     * (it did no work), and the order costs the cash price only. It is a
     * scenario and fixture path - the harnesses that want a city standing on
     * month one - and it now goes through the land and cash checks like any
     * other order rather than around them.
     */
    private void processBuildOrder(BuildingsTemplate selected, int quantity,
                                   boolean noConstruction) {
        /*
         * QUOTED AND CHARGED BY THE SAME METHOD.
         *
         * These five lines used to be a re-typed copy of calculateTotalCost(),
         * which buildStack() uses for the affordability check - so the city
         * quoted with one copy and debited with the other. They agreed exactly,
         * and would have kept agreeing right up until the first time anything
         * was added to a build price in one place and not the other. A permit
         * fee, a sector materials markup, a land charge on city builds: any of
         * them would have let a player be quoted one number and charged another.
         *
         * The materials figure is still needed separately for the receipt and
         * the import count, so it stays - but the money comes from one place.
         */
        double materialsPrice = buildingManager.getConstructionMaterialPrice();
        double currentMaterials = buildingManager.getConstructionMaterials();
        double totalMaterialsRequired = noConstruction
                ? 0
                : selected.constructionMaterials * quantity;

        double neededMaterials = Math.max(totalMaterialsRequired - currentMaterials, 0);

        double totalCost = noConstruction
                ? selected.getCashCost() * quantity
                : calculateTotalCost(selected, quantity);

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
            this.receiptSerial++;
            lastBuildingName = buildingManager.getName(selected);
            lastBuildQuantity = quantity;

            buildingManager.addStack(selected, quantity, noConstruction);
            materialsConsumed += totalMaterialsRequired;

            // 4. Subtract everything at once
            cash -= totalCost;
            cityCapitalSpending += totalCost;
            monthlyMaterialImports += neededMaterials;
            monthlyMaterialImportBill += neededMaterials * materialsPrice;

            // ...and it lands somewhere now. The construction sector did the
            // work; this is what it gets paid for doing it - recognised as the
            // work is actually delivered, not all on the order month.
            //
            // Unless there was no work: an instant build skips the queue, so
            // paying construction for it would be revenue against nothing
            // delivered, and recogniseWork() would never have the points to
            // earn it back.
            if (!noConstruction) {
                // The bought-in material is earned on delivery, which is now;
                // the work over the points. See ConstructionHandler.bill().
                servicesManager.getConstructionHandler().bill(totalCost,
                        selected.getConstructionPoints() * (double) quantity,
                        neededMaterials * materialsPrice);
            }

            System.out.println(quantity + " " + selected.getName()
                    + " construction started. $" + totalCost + " cash used");
        } else {
            // buildStack() only calls this once the price is covered, so this
            // branch is unreachable. It used to fall into quickIssueDebt(), a
            // console-era T-Bill issuer with its own stale pricing and no land
            // allocation; the JavaFX path offers the note BEFORE building, on
            // showQuickDebtMenu(). Loud rather than silent if that ever changes.
            throw new IllegalStateException("processBuildOrder() called with $"
                    + formatter.format(totalCost) + " due and $"
                    + formatter.format(cash) + " on hand");
        }
    }

    
    
    public enum BuildResult {SUCCESS, NEEDS_FUNDING, NO_LAND, NO_DEPOSIT, FAILED}

    /**
     * Mines standing, being built, or already ordered.
     *
     * A deposit supports one mine, and the count has to include work in progress
     * or the player could queue five mines against one deposit and have four of
     * them open onto nothing.
     */
    public int minesCommitted() {
        int committed = 0;
        int[] underConstruction = buildingManager.getUnderConstructionById();
        for (BuildingsTemplate t : buildingManager.getTemplates()) {
            if (t.getCategory() != BuildingType.MINING) continue;
            committed += buildingManager.getQuantity(t.getId());
            if (t.getId() < underConstruction.length) {
                committed += underConstruction[t.getId()];
            }
        }
        return committed;
    }

    /**
     * Whether this order can go ahead on the ore the city owns.
     *
     * A mine needs ground with iron under it. Buying a land parcel with a
     * deposit is what unlocks one, which is the whole reason the listing has
     * deposits in it - and why the parcels that carry them cost more.
     */
    public boolean hasDepositFor(BuildingsTemplate template, int quantity) {
        if (template == null || template.getCategory() != BuildingType.MINING) {
            return true;
        }
        return landManager.getIronDeposits() >= minesCommitted() + quantity
                && landManager.getIronReserveTonnes() > 0;
    }

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

        if (!hasDepositFor(template, quantity)) {
            return false;   // no ground with ore in it to sell them
        }

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
        monthlyMaterialImportBill += neededMaterials * materialsPrice;

        // Construction is paid for the building work only - the land was the
        // city's, not theirs to be paid for.
        servicesManager.getConstructionHandler().bill(totalCost - landPrice,
                template.getConstructionPoints() * (double) quantity,
                neededMaterials * materialsPrice);

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
    
    public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction){
        double totalCost = noConstruction
                ? template.getCashCost() * quantity
                : calculateTotalCost(template, quantity);

        // Land before money. processBuildOrder() checks it too, but by then the
        // caller has already been offered a T-Bill: without this the player can
        // borrow three hundred million to fund a building the city has nowhere
        // to put, and only find out after the debt is issued.
        // Ore before land and before money: a mine with nowhere to dig is not a
        // funding problem, and offering the player a bond to fix it would be a
        // lie about what is wrong.
        if (!hasDepositFor(template, quantity)) {
            this.hasNewReceipt = false;
            return BuildResult.NO_DEPOSIT;
        }

        if (!landManager.canAllocate(template.getLandSqFt() * quantity)) {
            this.hasNewReceipt = false;
            return BuildResult.NO_LAND;
        }

        if(totalCost <= cash){
            processBuildOrder(template, quantity, noConstruction);
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
    public int getBuildQuantity(){
        return lastBuildQuantity;
    }

    /** Which receipt this is. See receiptSerial - the UI compares it to remember. */
    public int getReceiptSerial(){
        return receiptSerial;
    }
    
    
    /**
     * What a T-Bill of this size would cost. Books nothing.
     *
     * Priced with itself included. A bill discounts by the rate, so its face
     * value is request/(1-rate) - which means the face depends on the rate and,
     * now, the rate depends on the face. quoteRate() walks that fixed point; the
     * lambda is this instrument's half of it.
     */
    public DebtQuote quoteTBill(double amount, int duration, double rounding) {

        priceTheDebtMarket();
        double before = debtManager.getRate();

        if (amount <= 0) {
            return new DebtQuote("Note", duration, 0, before, before, 0, 0, 0, 0);
        }

        final double requested = amount;
        final int months = duration;

        /*
         * DISCOUNTED ON ITS OWN TERM, which it was not.
         *
         * The face used to be requested/(1 - rate) with the full ANNUAL rate
         * applied whatever the duration, so a three-month note and a two-year
         * note were priced identically. A quarter's borrowing was charged a
         * year of interest and two years' borrowing was charged one.
         *
         * ShortTermTBill.faceFor() owns the convention now, so the quote, the
         * rate fixed point and the emergency path in nextMonth() cannot each
         * have their own version of it - which is exactly what they had.
         */
        /*
         * SIZED SO THE CITY ACTUALLY RECEIVES WHAT IT ASKED FOR.
         *
         * ShortTermTBill.faceFor() inverts the DISCOUNT and nothing else, so the
         * face it returns raises `requested` gross - and then the underwriter
         * and bond counsel are paid out of the proceeds, and the city banks
         * less than it asked for. Rounding the face up to the next granule
         * covered the fee often enough to look like it worked, which is what
         * made this so hard to see: the slack from rounding is at most one
         * granule and the fee grows with the face, so the shortfall appeared
         * and disappeared with the size of the ask and always arrived for good
         * once the face passed about $130,000.
         *
         * Jerus found it from the far end - "the tbill is inacted but the roads
         * are not built and you are just left with the cash unspent". Roads are
         * simply the first thing expensive enough to land in the failing range:
         * the emergency path borrows the exact shortfall, receives a few
         * hundred less than that, and buildStack() re-tests the price against
         * cash and refuses. Debt issued, nothing built.
         *
         * THE BONDS ARE NOT THE SAME BUG and are deliberately left alone. For a
         * serial or a term bond the face value IS the request - the player asks
         * for an amount of paper and receives par less fees, which is what
         * issuing at par means and what those quotes say. It is only the note
         * whose whole job is "how much paper do I need to RAISE this cash", and
         * only the note that was answering it wrong.
         */
        double rate = debtManager.quoteRate(requested,
                r -> Math.ceil(faceForNetProceeds(requested, r, months) / rounding) * rounding);

        double faceValue = Math.ceil(
                faceForNetProceeds(requested, rate, months) / rounding) * rounding;

        double received = netProceeds(faceValue, rate, months);

        /*
         * ...and the proceeds are rounded to the cent, which can still land a
         * fraction under the ask when the face lands exactly on a granule. A
         * quote that does not cover what was asked for is not a quote for it,
         * so it buys one more granule. Each one adds at least
         * rounding x 4.25% to the proceeds, so this cannot spin.
         */
        while (received < requested) {
            faceValue += rounding;
            received = netProceeds(faceValue, rate, months);
        }

        // A note pays no coupon; the discount IS the whole cost of the credit.
        return new DebtQuote("Note", duration, requested, rate, before,
                faceValue, received, 0, faceValue - received);
    }

    /**
     * Books a T-Bill on exactly the terms quoted.
     *
     * Every figure comes off the quote. Nothing is recalculated here, because a
     * booking that does its own arithmetic is free to disagree with the screen
     * that promised it.
     */
    public String handleTBillLogic(double amount, int duration, double rounding) {

        DebtQuote quote = quoteTBill(amount, duration, rounding);
        if (quote.isEmpty()) return "Nothing issued.";

        debtManager.addShortTermTBill(quote.faceValue(), duration, month);
        this.cash += quote.cashReceived();
       cityDebtRaisedThisMonth += quote.cashReceived();
       cityDiscountThisMonth += quote.faceValue() - quote.cashReceived();

        debtManager.updateInterest();
        return "Note issued.\n" + quote.summary();
    }

    /**
     * What a medium-term bond of this size would cost. Books nothing.
     *
     * Face value is the request here, so there is no fixed point to walk - but
     * the loan still has to be priced with itself on the books.
     */
    public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding) {

        priceTheDebtMarket();
        double before = debtManager.getRate();

        if (requestedAmount <= 0) {
            return new DebtQuote("Serial", duration, 0, before, before, 0, 0, 0, 0);
        }

        double faceValue = Math.ceil(requestedAmount / rounding) * rounding;
        double annualRate = debtManager.quoteRate(faceValue);
        double monthlyInterest = faceValue * (annualRate / 12.0);
        double received = Math.round((faceValue - costOfIssuance(faceValue)) * 100) / 100.0;

        /*
         * Issued at par, so the face IS the principal - but the city receives
         * less, because the underwriter and bond counsel are paid first.
         *
         * The all-in cost is now the fees plus every coupon the SERIAL schedule
         * actually charges, which is materially less than face x rate x years:
         * principal amortises, so the coupon falls each year and roughly half
         * the old bullet's interest simply never accrues. That is the trade
         * against the term bond, and it should show on the quote.
         */
        double coupons = 0;
        MediumTermBond shape = new MediumTermBond(
                faceValue, duration * 12, month, annualRate);
        for (double cf : shape.remainingCashFlows()) coupons += cf;
        coupons -= faceValue;   // strip the principal back out

        return new DebtQuote("Serial", duration, requestedAmount, annualRate, before,
                faceValue, received, monthlyInterest,
                (faceValue - received) + coupons);
    }

    /** Books a medium-term bond on exactly the terms quoted. */
    public String handleMediumBondLogic(double requestedAmount, int duration, double rounding) {

        DebtQuote quote = quoteMediumBond(requestedAmount, duration, rounding);
        if (quote.isEmpty()) return "Nothing issued.";

        debtManager.addMediumTermBond(quote.faceValue(), duration * 12, month, quote.marketRate());
        this.cash += quote.cashReceived();
       cityDebtRaisedThisMonth += quote.cashReceived();
       cityDiscountThisMonth += quote.faceValue() - quote.cashReceived();

        debtManager.updateInterest();
        return "Serial bond issued.\n" + quote.summary();
    }

    /**
     * Long bonds pair a LOW monthly coupon with a redemption premium: you repay
     * more than you borrowed, but your monthly payment is well below a medium
     * bond's. The premium is what that cash-flow relief costs.
     *
     * PRICED AS A PRESENT VALUE, WHICH IT WAS NOT BEFORE.
     *
     * The old premium solved a SIMPLE-interest relation over the bond's life:
     *
     *     premium + (1 + premium) * couponYield * duration
     *         = LONG_BOND_COST_MULTIPLIER * marketRate * duration
     *
     * That is fine as a balance knob and wrong as a price. Over twenty-five
     * years simple and compound interest are not close - 7.6% simple is 2.9x,
     * compound is 6.4x - so the face it produced was far too small for the cash
     * it handed over. Discounted properly, a 25-year bond that raised $200,000
     * was a promise worth only $174,548.
     *
     * Nobody noticed while debt could only be issued. The moment it could also
     * be BOUGHT BACK at market value, the gap became an infinite money button:
     * issue, repurchase, pocket $25,453, repeat. Measured at every duration -
     * $2,145 at ten years rising to $28,059 at thirty - and caught by
     * RestructureCheck section 5, which exists for exactly this.
     *
     * So the face value is now SOLVED from the present value instead. With
     * n = duration * 12 months, r = marketRate / 12 and d = (1 + r)^-n:
     *
     *     PV = F * [ (couponYield / 12) * (1 - d) / r  +  d ]
     *
     * and the face is whatever makes that PV equal the cash the player asked
     * for. Issue price and repurchase price are now the same function of the
     * same inputs, so a round trip is neutral BY CONSTRUCTION rather than by a
     * rule bolted on to forbid it - the same reason DebtQuote exists at all.
     *
     * LONG_BOND_COST_MULTIPLIER is retired with it. The instrument is still
     * dearer all-in than a medium bond, but now because it runs for longer at a
     * real rate rather than because a constant said so.
     *
     * The coupon curve is untouched: long money still carries a low coupon
     * (about a third of the medium-term rate), which is the whole point of the
     * instrument. Only what the city receives for it has changed.
     */
    private static double longBondCouponYield(double marketRate, int duration) {
        return (marketRate / 3) + (.00667 * duration) / (duration + 30);
    }

    /**
     * The present value of one dollar of long-bond face, at a given rate.
     *
     * Shared by the face solver and the quote so they cannot drift, and it is
     * deliberately the SAME shape as Debt.getMarketValue() - coupon annuity plus
     * discounted principal - because it has to be: that method is what will
     * price this bond back.
     */
    private double longBondPvPerFace(double marketRate, int duration) {

        int n = duration * 12;
        double r = marketRate / 12.0;
        double monthlyCoupon = longBondCouponYield(marketRate, duration) / 12.0;

        if (r <= 1e-9) {
            return monthlyCoupon * n + 1;
        }

        double d = Math.pow(1 + r, -n);
        return monthlyCoupon * (1 - d) / r + d;
    }

    /**
     * What a long bond of this size would put on the books at a given rate.
     *
     * Extracted so quoteRate() and the issuance below run the SAME arithmetic.
     * A quote that is computed differently from the deal it quotes is just a
     * second definition waiting to drift from the first.
     */
    private double faceValueOfLongBond(double amount, int duration,
                                       double rounding, double marketRate) {

        double pvPerFace = longBondPvPerFace(marketRate, duration);

        // A coupon at or above the market rate would price at or over par and
        // the instrument would stop being a discount bond. The curve puts the
        // coupon at about a third of market so this cannot happen in play, but
        // a fixture is free to hand in anything and dividing by ~0 is not a
        // failure mode worth leaving open.
        if (pvPerFace <= 1e-9) {
            return Math.ceil(amount / rounding) * rounding;
        }

        return Math.ceil((amount / pvPerFace) / rounding) * rounding;
    }

    /**
     * What a long bond of this size would cost. Books nothing.
     *
     * Priced with itself included, and this is the instrument where that bites
     * hardest: the redemption premium grosses the face value up, and the premium
     * is a function of the market rate. So a long bond taken at a stretched rate
     * puts far more debt on the books than the cash it hands over, and the rate
     * has to know that before it is struck.
     *
     * The lambda calls faceValueOfLongBond(), which is also what sizes the deal
     * below - one definition, so the quote and the booking cannot disagree.
     */
    public DebtQuote quoteLongBond(double amount, int duration, double rounding) {

        priceTheDebtMarket();
        double before = debtManager.getRate();

        if (amount <= 0) {
            return new DebtQuote("Term", duration, 0, before, before, 0, 0, 0, 0);
        }

        final double requested = amount;
        double marketRate = debtManager.quoteRate(requested,
                r -> faceValueOfLongBond(requested, duration, rounding, r));

        // Yield curve: long money carries a lower coupon than the medium-term
        // market rate. Small monthly payments are the point of the instrument.
        double couponYield = longBondCouponYield(marketRate, duration);

        double faceValue = faceValueOfLongBond(requested, duration, rounding, marketRate);

        // What that face is actually worth today. Equal to the request before
        // rounding; a little above it after, because the face rounds UP to the
        // instrument's granularity and the proceeds follow the face.
        double received = Math.round(
                (faceValue * longBondPvPerFace(marketRate, duration)
                        - costOfIssuance(faceValue)) * 100) / 100.0;

        double monthlyInterest = (faceValue * couponYield) / 12;
        double totalCost = (faceValue - received) + (monthlyInterest * duration * 12);

        return new DebtQuote("Term", duration, requested, marketRate, before,
                faceValue, received, monthlyInterest, totalCost);
    }

    /** Books a long bond on exactly the terms quoted. */
    public String handleLongBondLogic(double amount, int duration, double rounding) {

        DebtQuote quote = quoteLongBond(amount, duration, rounding);
        if (quote.isEmpty()) return "Nothing issued.";

        // The coupon, not the market rate - a long bond's whole shape is a low
        // monthly payment bought with a redemption premium.
        debtManager.addLongTermBond(quote.faceValue(), duration * 12, month, quote.couponRate());
        this.cash += quote.cashReceived();
       cityDebtRaisedThisMonth += quote.cashReceived();
       cityDiscountThisMonth += quote.faceValue() - quote.cashReceived();

        debtManager.updateInterest();
        return "Term bond issued.\n" + quote.summary();
    }

    /**
     * The quote for whichever instrument, by the name the menus use.
     *
     * Mirrors the dispatch in the issuance screens so a screen can ask "what
     * would this cost" without knowing which instrument it is looking at.
     */
    /* =======================================================================
       BORROWING IN SOMEBODY ELSE'S MONEY
       =======================================================================

       Three instruments, the same three shapes, written in USD. What differs
       from the domestic pair above is small in code and enormous in play:

         - the rate comes off DebtManager.foreignRate(), which is the world's
           base plus what the world charges THIS city - and starts below the
           domestic floor
         - every figure in the quote is in DOLLARS. The screen converts for
           display; the contract does not
         - the proceeds do not reach the bank, so the treasury has a funding
           source that is not the institution it is recapitalising. That is the
           point of the whole exercise
         - if the window is shut, there is no quote at all

       Note the ASYMMETRY that makes this dangerous and is not modelled as a
       special case: the coupon and the principal are fixed in dollars, and the
       city's ability to pay them is fixed in nothing at all.
       ======================================================================= */

    /**
     * WALKING AWAY FROM THE DOLLARS.
     *
     * Every piece of foreign paper comes off the books, and the creditors have
     * no further claim. No cash moves - which is the point, and why MoneyAudit
     * is untouched by it - so the city's position improves by the whole of what
     * it repudiated, instantly, on the valuation line.
     *
     * That flattering is the trap in the other direction. Debt-to-exports
     * collapses, the premium looks wonderful, and none of it matters, because
     * nobody abroad will lend a defaulter a dollar for five years however good
     * the ratios look. The scar on the price outlasts the closed window by
     * years more.
     *
     * A player's decision, and also the city's fate if it simply cannot pay -
     * see checkForeignSolvency().
     */
    public double defaultOnForeignDebt(String because) {
        double written = debtManager.repudiateForeignDebt();
        if (written <= 0) return 0;
        foreign.forgiveDebt(written);
        foreign.takeForeignDebt(0, foreign.getRate());
        debtManager.markForeignDefault();
        debtManager.updateInterest();
        GameLog.note(String.format(
                "THE CITY HAS DEFAULTED ON ITS FOREIGN DEBT - $%,.0fk repudiated (%s). "
                + "No lender abroad will touch it for five years.", written, because));
        return written;
    }

    /** How deep the city may go before its foreign creditors are not paid. */
    public static final double DEFAULT_OVERDRAFT_YEARS = 1.0;

    /**
     * The city cannot pay, so it does not.
     *
     * Deliberately measured against a YEAR OF TAX REVENUE rather than against
     * zero. A treasury dipping into overdraft for a month is ordinary municipal
     * finance and the overdraft is already priced for it; a treasury a full
     * year of its own revenue in the red, with dollars falling due it has no
     * way of earning, is insolvent, and pretending otherwise would let a city
     * carry foreign debt for ever on an overdraft that costs it nothing it
     * cannot borrow again.
     */
    private void checkForeignSolvency() {
        if (!debtManager.hasForeignDebt()) return;
        double annualRevenue = debtManager.getMonthlyTaxRevenue() * 12;
        if (annualRevenue <= 0) return;
        if (cash < -annualRevenue * DEFAULT_OVERDRAFT_YEARS) {
            defaultOnForeignDebt("the treasury could not find the dollars");
        }
    }

    /** True if anybody abroad will lend the city a dollar today. */
    public boolean foreignWindowOpen() { return debtManager.foreignWindowOpen(); }

    /** Why not, in words, or null. */
    public String foreignWindowReason() { return debtManager.foreignWindowReason(); }

    /**
     * What a USD bond of this size would cost. Books nothing.
     *
     * @param requestedUsd what the city wants to raise, in dollars
     */
    public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding) {

        double before = debtManager.foreignRate();

        if (requestedUsd <= 0 || !debtManager.foreignWindowOpen()) {
            return new DebtQuote(type, duration, 0, before, before, 0, 0, 0, 0);
        }

        return switch (type) {
            case "Note" -> {
                double face = Math.ceil(requestedUsd / rounding) * rounding;
                double rate = debtManager.quoteForeignRate(face);
                double gross = face * (1 - ShortTermTBill.discountFraction(rate, duration));
                double received = Math.round((gross - costOfIssuance(face)) * 100) / 100.0;
                yield new DebtQuote("Note", duration, requestedUsd, rate, before,
                        face, received, 0, face - received);
            }
            case "Serial" -> {
                double face = Math.ceil(requestedUsd / rounding) * rounding;
                double rate = debtManager.quoteForeignRate(face);
                double received = Math.round((face - costOfIssuance(face)) * 100) / 100.0;
                double coupons = 0;
                MediumTermBond shape = new MediumTermBond(face, duration * 12, month, rate);
                for (double cf : shape.remainingCashFlows()) coupons += cf;
                coupons -= face;
                yield new DebtQuote("Serial", duration, requestedUsd, rate, before,
                        face, received, face * (rate / 12.0),
                        (face - received) + coupons);
            }
            case "Term" -> {
                double face = Math.ceil(requestedUsd / rounding) * rounding;
                double rate = debtManager.quoteForeignRate(face);
                double coupon = longBondCouponYield(rate, duration);
                double received = Math.round(
                        (face * longBondPvPerFace(rate, duration)
                                - costOfIssuance(face)) * 100) / 100.0;
                double monthly = (face * coupon) / 12;
                yield new DebtQuote("Term", duration, requestedUsd, rate, before,
                        face, received, monthly,
                        (face - received) + monthly * duration * 12);
            }
            default -> throw new IllegalArgumentException("No such instrument: " + type);
        };
    }

    /**
     * Books a USD bond on exactly the terms quoted.
     *
     * @param holdAsReserves true to keep the dollars as reserves; false to sell
     *        them for local money at today's rate and spend them at home
     */
    public String handleForeignLogic(String type, double requestedUsd, int duration,
                                     double rounding, boolean holdAsReserves) {

        if (!debtManager.foreignWindowOpen()) {
            return "No lender abroad will take this paper: " + debtManager.foreignWindowReason();
        }

        DebtQuote quote = quoteForeign(type, requestedUsd, duration, rounding);
        if (quote.isEmpty()) return "Nothing issued.";

        switch (type) {
            case "Note"   -> debtManager.addShortTermTBill(
                    quote.faceValue(), duration, month, true);
            case "Serial" -> debtManager.addMediumTermBond(
                    quote.faceValue(), duration * 12, month, quote.marketRate(), true);
            case "Term"   -> debtManager.addLongTermBond(
                    quote.faceValue(), duration * 12, month, quote.couponRate(), true);
            default -> throw new IllegalArgumentException("No such instrument: " + type);
        }

        /*
         * THE DOLLARS ARRIVE. WHERE THEY GO IS THE PLAYER'S DECISION.
         *
         * Held as reserves, the position improves and the debt is matched by an
         * asset - which is what a prudent treasury does and what makes the next
         * bond cheaper, since cover feeds the liquidity term.
         *
         * Converted, the treasury gets local money it can spend on a hospital
         * today, and carries an unhedged foreign liability against nothing at
         * all. That is the trade, and it is the same one every finance ministry
         * in the literature has made.
         *
         * Either way the money crossed the city's edge, so it is a financial
         * inflow on the balance of payments, and either way it never touches
         * bank.lend().
         */
        double local = quote.cashReceived() * foreign.getRate();
        this.cash += local;
        foreignDebtRaisedThisMonth += local;

        /*
         * "Hold as reserves" is not a different kind of accounting. It is the
         * treasury taking the local money it just raised and buying foreign
         * currency back with it - which is precisely what parking the proceeds
         * abroad IS, and which the intervention screen has been able to do
         * since phase 2. One path, already audited, already harnessed.
         */
        if (holdAsReserves) buyForeignCurrency(local);

        /*
         * ...AND THE DEBT IS ON THE SCREEN THE SAME INSTANT THE MONEY IS.
         *
         * The stock is normally restruck at the end of the month, which left a
         * gap a player could see: park $7,742 of proceeds in reserves and the
         * trade screen showed a net position of +$7,251 - reserves counted, the
         * $20,000 of paper that paid for them not yet. Measured; it read as a
         * city that had just got richer by borrowing.
         *
         * Same rate, so takeForeignDebt() books no revaluation - it only moves
         * the stock forward to include the bond just signed.
         */
        foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());

        debtManager.updateInterest();
        return (holdAsReserves
                ? "Issued abroad; the dollars are in reserve.\n"
                : "Issued abroad and converted.\n") + quote.summary();
    }

    public DebtQuote quoteDebt(String type, double amount, int duration, double rounding) {
        return switch (type) {
            case "Note" -> quoteTBill(amount, duration, rounding);
            case "Serial" -> quoteMediumBond(amount, duration, rounding);
            case "Term" -> quoteLongBond(amount, duration, rounding);
            // Loudly, rather than handing a screen a null to dereference three
            // frames later where the cause is invisible.
            default -> throw new IllegalArgumentException("No such instrument: " + type);
        };
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
            final double gap = -cash; // The actual negative amount

            /*
             * Priced like any other borrowing, which it now is.
             *
             * The market has to see the hole before it quotes: priceTheDebtMarket()
             * puts the overdraft on the books, and quoteRate() then walks the
             * face-value/rate fixed point exactly as the finance screen does.
             * The bill's proceeds close the gap, so quoteRate() nets the overdraft
             * back out and prices against bonds-plus-bill - the honest position
             * a moment after the money lands.
             *
             * This is the deliberate consequence of "no free money": an overdraft
             * is the most desperate borrowing there is, and from the first month
             * it costs what desperate borrowing costs.
             */
            /*
             * ONE DEFINITION, and this used not to be.
             *
             * These twenty lines were a hand-rolled second copy of
             * issueEmergencyDebt(): its own face formula, its own rounding, its
             * own four-month term, and - once the note learned to discount on
             * its own duration - its own now-wrong pricing. The finance screen
             * and the automatic path would have quoted the same city two
             * different numbers for the same borrowing.
             *
             * Deleted in favour of the real method. This codebase has now been
             * bitten by a duplicated calculation four separate times (the
             * materials quote, the business tax, the wage sync, this), and the
             * pattern is always the same: the copy is fine on the day it is
             * written and wrong the first time the original changes.
             *
             * SIX MONTHS, which is Jerus's call and the right one: long enough
             * that a city has a real chance to fix the underlying problem or
             * refinance into something longer, short enough that it still hurts
             * and still has to be dealt with.
             */
            issueEmergencyDebt(gap, EMERGENCY_NOTE_MONTHS);
        }
        month++;
        monthsSinceAutosave++;

        // The register's month: nothing offered, nothing paid, until it is.
        equity.startMonth();
        economyManager.clearEquityFlows();

        if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {
            autosave("month " + month);
        }

        // Where every dollar in the city is right now, before anything moves.
        // Compared at the bottom against every dollar that crossed the city's
        // boundary this month - see MoneyAudit.
        /*
         * THE WINDOW IS PRESS TO PRESS, not tick to tick.
         *
         * Where the LAST month closed, not where cash stands now - because
         * between the two the player has been buying land, paying for
         * buildings, issuing paper and buying bonds back, and every one of
         * those moved the balance they are watching. A window that opened here
         * would report a month with none of the player's own decisions in it,
         * which is precisely the money Jerus noticed going missing.
         */
        treasuryOpening = treasuryRecorded ? treasuryClosing : cash;
        cityDebtRaisedThisMonth = 0;
        cityDiscountThisMonth = 0;
        cityPrincipalRepaidThisMonth = 0;
        foreignDebtRaisedThisMonth = 0;
        foreignPrincipalRepaidThisMonth = 0;
        foreignInterestPaidThisMonth = 0;
        // Its domestic twin, cleared in the same breath. This used to be zeroed
        // inside startOfMonthUpdate(), half a tick earlier than the foreign one,
        // which is why the government's books needed a stash to see a whole
        // month's coupon. See strikeGovernmentBooks().
        cityInterestPaid = 0;
        economyManager.getBusinessDebtManager().startAuditMonth();
        sectorInvested.clear();
        double[] poolsBefore = MoneyAudit.pools(this);
        double pooledBefore = 0;
        for (double p : poolsBefore) pooledBefore += p;
        double interestDue = economyManager.getInterestAccrued();

        bank.startMonth();
        foreign.startMonth();

        /*
         * THE BANK PAYS ITS PROFIT TAX, on the month that has just finished.
         *
         * Here, at the top, and not with the rest of the bank's settlement at
         * the bottom - because the city's tax take is struck inside
         * finalUpdateEconomy(), which runs before the bank knows what it made.
         * See Bank.chargeTax() for why arrears is the only ordering that keeps
         * the money in one channel.
         *
         * At the RETAIL rate: a Commercial Bank is a commercial building, its
         * property tax already goes through that category, and its jobs already
         * sit in that sector's headcount. If it ever wants a dial of its own,
         * the seam is a new PolicySector - which would resize the tax policy's
         * offset arrays and the save that carries them, so it is deliberately
         * not done for free here.
         */
        economyManager.setBankTax(bank.chargeTax(
                economyManager.getTaxPolicy().effectiveProfitRate(PolicySector.RETAIL)));
        cityDebtRaisedForBank = cityDebtRaisedThisMonth;
        cityDiscountForBank = cityDiscountThisMonth;

        startOfMonthUpdate();
        simulationEngine.simulateMonth(this);
        finalUpdateEconomy();
        economyManager.setPreviousGdp(historySave);
        priceTheDebtMarket();
        debtManager.processAllDebts(this);

        // The government's books, over the same window as the treasury bridge
        // and after the month's coupon has actually been paid.
        strikeGovernmentBooks();

        /*
         * THE BANK SETTLES, BEFORE THE AUDIT LOOKS.
         *
         * Every loan in the city is its money now, so the four flows that used
         * to run to and from nowhere - lending, repayment, interest, the
         * treasury's coupons - move between its cash and the borrower's. Struck
         * here, once, off the same figures the audit reads, so the two cannot
         * disagree about a month.
         */
        BusinessDebtManager lender = economyManager.getBusinessDebtManager();
        bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank);
        bank.takeRepayment(lender.getRepaidThisMonth() + cityPrincipalRepaidThisMonth);
        /*
         * The discount on this month's issuance, recognised as it is earned.
         * Non-cash - the bank's book already carries the paper at face - and
         * internal, because it is the city that is paying it.
         */
        bank.takeDiscount(cityDiscountForBank);

        bank.takeInterest(interestDue
                + economyManager.getCommercialHandler().getReportRetailInterest()
                + economyManager.getCommercialHandler().getReportRealEstateInterest()
                + economyManager.getIndustrialHandler().getReportInterestExpense()
                + economyManager.getHeavyIndustryHandler().getReportInterestExpense()
                + economyManager.getMiningHandler().getReportInterestExpense()
                + servicesManager.getConstructionHandler().getReportInterestExpense());

        refreshBank();

        /*
         * AND THE BANK PAYS FOR ITS OWN MONEY.
         *
         * Last, after every loan has moved and the position has been re-read,
         * because what it needs to fund is whatever it is short by once the
         * month is done. Before the audit looks, because the money it raises
         * comes from outside the city and the audit has to see it arrive.
         */
        /*
         * Its own staff, on its own books.
         *
         * A bank is a COMMERCIAL building, so its tellers were in the shops'
         * payroll - which was a stated simplification while the bank had no
         * books, and indefensible the moment it had a set of financial
         * statements with an operating-expense line on them. The wages are
         * carved out in CommercialHandler and charged here.
         */
        bank.payRunning(economyManager.getCommercialHandler().getReportBankPayroll(), 0);

        /*
         * Capital for whatever branches opened this month. Shareholders' money,
         * from outside the city - a bank with no capital has no capacity, and a
         * bank with no capacity can never earn any, so without this a first
         * branch could never begin lending.
         */
        capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));

        /*
         * WHAT THE WORLD WOULD PAY TO PARK HERE, handed to the bank as a
         * function rather than as five numbers.
         *
         * The bank prices its own deposits now - see Bank.chooseDepositRate() -
         * and to do that it has to be able to ask what a better rate would
         * bring in. It must not have to know what a carry trade is to ask,
         * which is the same rule that keeps the loan book's maturity weighting
         * in refreshBank() rather than inside the bank: this class can see the
         * world rate, the country premium and the month's GDP, and the bank
         * cannot and should not.
         *
         * CapitalFlows answers with its OWN arithmetic (stockAt/arrivalsAt are
         * takeMonth's formula, lifted out and made side-effect-free), so the
         * forecast cannot drift away from the mechanic it forecasts.
         */
        final double cityRateNow = debtManager.getRate();
        final double premiumNow = debtManager.countryPremium();
        final double gdpNow = economyManager.getMonthGdp();
        bank.setDepositMarket(rate -> hotMoney.arrivalsAt(
                rate, cityRateNow, DebtManager.WORLD_BASE_RATE, premiumNow, gdpNow));

        bank.fundToCover(debtManager.getRate());

        // ...and if that left it owing more than it owns, it has failed. Its
        // creditors take the hole; the city has a decision to make.
        bank.resolveIfFailed();

        // The month is final, so the figure next month's tax is charged on is
        // final too. Carried in the save - see Bank.getProfitLastMonth().
        bank.closeMonth();

        /*
         * ...AND THE SAVERS ARE PAID.
         *
         * Struck inside fundToCover() above; handed out here, because the bank
         * has no idea who its depositors are. The households' share is credited
         * to their accounts and the sectors' to their tills, each in proportion
         * to what they had banked.
         */
        householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());
        double sectorInterest = bank.getDepositInterestToSectors();
        /*
         * Cleared before it is handed out, not after, so a month in which the
         * bank pays nothing leaves zeroes behind rather than last month's
         * figures. SectorBooks reads these a few lines below, in the same
         * month - see EconomyManager.recordDepositInterest().
         */
        economyManager.clearDepositInterest();
        if (sectorInterest > 0) {
            double totalSectorCash = 0;
            for (String s : BusinessDebtManager.SECTORS) {
                totalSectorCash += Math.max(0, economyManager.getSectorCash(s));
            }
            if (totalSectorCash > 0) {
                for (String s : BusinessDebtManager.SECTORS) {
                    double held = Math.max(0, economyManager.getSectorCash(s));
                    if (held <= 0) continue;
                    double share = sectorInterest * held / totalSectorCash;
                    economyManager.setSectorCash(s, economyManager.getSectorCash(s) + share);
                    economyManager.recordDepositInterest(s, share);
                }
            }
        }

        /*
         * ...AND THE SECTORS DECIDE WHERE TO KEEP THEIR MONEY.
         *
         * After the bank has said what it pays and before the month is
         * struck, so the move is inside the audited window and the balance of
         * payments reads it in the same Result as the trade it offsets. At the
         * rate the month was traded at - the currency reprices below, off this
         * Result, and a flow priced at the rate it moves is the rule this file
         * has been caught breaking before. See OutwardInvestment.
         */
        /*
         * ...AND THE OWNERS ARE PAID, before the sectors decide where to keep
         * what is left. On the last closed month's result, from the till,
         * and from abroad if the till is short. See payDividends().
         */
        payDividends();

        outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE,
                foreign.getRate(), economyManager);

        lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);

        /*
         * ...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS.
         *
         * Handed the audit's own Result rather than the city, deliberately: the
         * Result is the figure that has already been reconciled, and re-reading
         * the underlying getters here would be a second definition of one month.
         * Nothing is calculated twice, so nothing can disagree.
         */
        foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());

        /*
         * ...and the currency reprices on it. After the month is taken, so it
         * moves on a month that has closed rather than one still in progress,
         * and before the next month reads it at the top of startOfMonthUpdate().
         */
        /*
         * WHERE THE CURRENCY OUGHT TO BE, before it is asked to move.
         *
         * Relative PPP: the city's basket against the world's. Set before
         * repriceCurrency() reads it, and after both levels have been struck
         * for the month.
         */
        /*
         * THE SAME INSTRUMENT FOR BOTH HALVES. Parity is struck from the two
         * LEVELS, so the drift has to be struck from what the two levels are
         * DOING - and the world's headline rate is not that; the level is
         * pulled back to trend and the headline is not. See
         * WorldEconomy.realisedInflation() for what handing it the headline
         * did to the exchange rate, and through it to every exporter.
         */
        foreign.setParity(priceIndex.getIndex(), world.getPriceLevel(),
                priceIndex.inflation(), world.realisedInflation());
        /*
         * ...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD.
         *
         * The policy rate against the world's base rate. This is the channel
         * that makes the dial a defence: raise it and the currency is supported
         * because money comes to be lent here, at the cost of every borrower in
         * the city paying more. Asia 1997, as a lever.
         */
        foreign.setRateDifferential(
                debtManager.getPolicyRate() - DebtManager.WORLD_BASE_RATE);
        foreign.repriceCurrency();

        /*
         * ...AND THE CITY'S DOLLAR DEBT IS WORTH WHAT IT IS WORTH.
         *
         * After the reprice, so the debt is valued at the rate the month
         * actually ended on, and after the audit, because a revaluation is not
         * a cash flow and must not be inside one. See
         * ForeignAccounts.takeForeignDebt().
         *
         * The instruments are told first: everything downstream of here - the
         * debt screen, debt-to-GDP, the credit rating, the country premium on
         * the next bond - reads getOustandingPrincipal(), and every one of them
         * would otherwise be a month behind the currency.
         */
        debtManager.setExchangeRate(foreign.getRate());
        foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());
        debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());
        debtManager.ageForeignStanding();
        checkForeignSolvency();

        /*
         * ...AND WHAT THE MONTH COST A FAMILY.
         *
         * Priced on the month that has closed, from the shelf price and the
         * rent it actually charged and what households actually paid for each.
         * Read at the top of NEXT month by the wage drift, which is what makes
         * the wage-price loop a loop with a lag in it rather than a
         * simultaneous equation.
         */
        priceIndex.takeMonth(
                economyManager.getCommercialHandler().getStoreSellPrice(),
                /*
                 * The AVERAGE actually paid, not the family price. Rent is
                 * two prices now and the cost of living is what households
                 * handed over across both. Continuous with what the index
                 * used before the split: the two prices open equal, so the
                 * average opens on the same number the base was struck at.
                 */
                economyManager.getCommercialHandler().getAverageRentPaid(),
                economyManager.getCommercialHandler().getGrossRevenue(),
                economyManager.getCommercialHandler().getReportRentIncome());

        /* =================================================================
           AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD.

           Last, after the currency has repriced and the debt has been revalued,
           because every input this reads is a figure the month has just
           finished settling. A carry trader reads the month that closed, not
           the one in progress.
           ================================================================= */
        double before = hotMoney.getStock();
        hotMoney.setMonth(month);
        hotMoney.takeMonth(
                bank.depositRate(),
                debtManager.getRate(),
                DebtManager.WORLD_BASE_RATE,
                debtManager.countryPremium(),
                economyManager.getMonthGdp(),
                foreign.getReserves(),
                yearlyDepreciation(),
                bank.isInsolvent(),
                debtManager.getMonthsSinceForeignDefault() >= 0
                        && debtManager.getMonthsSinceForeignDefault() < 24,
                month);

        /*
         * THE MONEY MOVES FOR REAL. It lands in the bank's cash, because that
         * is what funding IS - and it crosses the city's edge to get there, so
         * MoneyAudit sees both directions. A stop is the bank's cash leaving.
         */
        double moved = hotMoney.getStock() - before;
        if (moved > 0)      bank.receiveHotMoney(moved);
        else if (moved < 0) bank.returnHotMoney(-moved);
        bank.setForeignDeposits(hotMoney.getStock());

        // A year of the rate, so next year can tell a drift from a run.
        rateHistory[month % 12] = foreign.getRate();
        if (rateHistoryFilled < 12) rateHistoryFilled++;

        takeTreasuryMonth();

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
        // The land market re-prices first: what businesses pay this month, and
        /*
         * THE EXCHANGE RATE, BEFORE ANYTHING FOREIGN IS PRICED.
         *
         * Every world price in the game - food, scrap, ore, steel, building
         * materials - is quoted by the world in its own money and converted at
         * this rate. Set at the very top of the month so nothing is priced at
         * last month's rate and settled at this month's.
         */
        /*
         * THE RATE TIMES THE WORLD'S OWN PRICE LEVEL.
         *
         * Every consumer of this already multiplies a constant world price by
         * whatever it is handed - food at $0.20, materials at $2.00, ore at
         * $0.20 - so world inflation rides in on the same factor without any of
         * them needing to know about it. What a foreign thing costs in Danzik
         * dollars is its world price, times what the world charges for it now,
         * times what a dollar costs.
         *
         * getRate() stays the pure exchange rate everywhere it is displayed or
         * used to convert a currency; this is the only place the two are
         * multiplied together, and that is deliberate - a rate that quietly had
         * inflation baked into it would be unreadable on every screen.
         */
        world.advanceMonth(month);
        economyManager.setExchangeRate(foreign.getRate() * world.getPriceLevel());

        /*
         * ...and wages start chasing what the world now charges.
         *
         * The exchange rate IS the price index here: the world's own prices do
         * not move, so a rate of 1.4 means everything imported costs 40% more
         * than it did at founding. See LabourMarket.updateCostOfLiving() for why
         * a third, and why slowly.
         */
        labourMarket.updateCostOfLiving(priceIndex.getIndex());
        /*
         * ...AND THE FLOOR HOLDS ITS WORTH. The minimum wage is a standard of
         * living now, so the cash figure is restruck from the index every month
         * rather than sitting where the player last typed it while prices moved
         * out from under it. See LabourMarket.reindexMinimumWage().
         */
        /*
         * THE FLOOR NEEDS NO SEPARATE INDEXATION. baseWage() already multiplies
         * it by costOfLiving, which updateCostOfLiving() has just walked a
         * twenty-fourth of the way toward this month's prices - so the floor
         * holds its real worth over about two years without a second
         * mechanism, and adding one put the price level into every wage twice.
         * See LabourMarket.baseWage() for the eight years of rent that took to
         * find. The cash value is LabourMarket.cashMinimumWage().
         */

        // what the next parcel costs, are both inputs to everything below.
        landManager.updateMarket(populationManager.getPopulation());

        economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());

        /*
         * THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT.
         *
         * Every rate in the city is the borrower's own risk plus what the bank
         * is charging for funds, so the premium has to be set before the debt
         * market re-prices and before the sectors are handed their bills.
         */
        debtManager.setBankPremium(bank.ratePremium());
        // A failed bank lends nothing. With no branches there is no bank and
        // the lending comes from outside the city, at the full premium - that
        // path stays open; it is the frozen INSTITUTION that is shut.
        economyManager.getBusinessDebtManager().setLendingOpen(
                !(bank.getBranches() > 0 && bank.isInsolvent()));
        economyManager.updateBusinessCredit(debtManager.getRate());

        // Property tax with the interest bill, and for the same reason: both are
        // owed before the month's statements run, so what each sector banks is
        // already net of them.
        economyManager.chargePropertyTax();

        // The repair bill on the housing stock, before EITHER statement runs -
        // it is an expense on one of them and revenue on the other.
        chargeBuildingMaintenance();

        // Recognise the month's construction work before construction's income
        // statement runs, so the revenue and the payroll describe the same month.
        //
        // The BUILDING output, not the gross: the crews that spent part of the
        // month on repairs did not spend it on sites. See getBuildingOutput().
        ConstructionHandler construction = servicesManager.getConstructionHandler();
        construction.recogniseWork(getBuildingOutput());

        /*
         * The fixed construction retainer used to be paid here.
         *
         * RETIRED. It was a dollar figure the player picked once, so it went
         * stale the moment the sector grew - five re-settings in a single
         * playtest - and it protected a SLICE of capacity rather than the
         * sector. The per-sector standing policy in runRetirement() replaces it
         * and covers construction like any other sector: it self-scales, because
         * it is measured against the loss rather than against a number.
         *
         * The field survives only to carry old saves across; loadGame() turns a
         * non-zero retainer into the toggle and then leaves it alone.
         */

        // Grab the recognised figure before updateConstructionReport() banks the
        // month and clears it - that value IS this month's investment.
        double constructionWorkDone = construction.getRevenue();

        /*
         * What the builders' imports cost this month, handed over before the
         * strike so the statement carries it. The same window the national
         * accounts read a few lines down, and the same figure: the shortfall
         * at each draw, at the price it was charged at. See
         * ConstructionHandler.materialsImportBill for what this replaced.
         */
        construction.setMaterialsImportBill(monthlyMaterialImportBill);

        economyManager.updateCommercialReport();
        economyManager.updateIndustrialReport();
        economyManager.updateHeavyIndustryReport();
        economyManager.updateMiningReport();
        economyManager.updateConstructionReport();

        // The month's VAT, struck once, after every sector has reported and
        // before anything reads the tax total. See settleSalesTax().
        economyManager.settleSalesTax();

        economyManager.updateNationalAccounts(
                constructionWorkDone,
                /*
                 * GOVERNMENT CONSUMPTION, and healthcare is most of it now.
                 *
                 * This was the utility payroll alone, so a city that staffed a
                 * 996-person Regional Medical Centre added nothing whatever to
                 * measured output - while every real national accounts adds
                 * government healthcare to GDP at exactly what it costs to
                 * provide, because there is no market price to value it at.
                 * The gross cost is the right figure: what the city buys is the
                 * wages and the running of the buildings, and the fees are a
                 * transfer from households, not a second lot of output.
                 */
                servicesManager.getUtilitiesHandler().getUtilityPayroll()
                        + healthcare.getGrossCost(),
                /*
                 * The bill as it was charged, not the count times today's
                 * price: an order placed last month was priced at last month's
                 * rate, and this is the same figure the builders' statement
                 * carries, so the two accounts agree to the dollar.
                 */
                monthlyMaterialImportBill,
                /*
                 * The yard AND the materials already embedded in unfinished
                 * buildings. Both are stock the city has bought and not yet
                 * turned into output - and the second is much the larger, because
                 * an order's materials are imported in full up front and consumed
                 * into the backlog immediately rather than sitting in the yard.
                 * Counting only the yard left the import unmatched, which is the
                 * whole of why GDP went negative on a big build.
                 */
                buildingManager.getConstructionMaterials(),
                buildingManager.getConstructionMaterialPrice(),
                // Work in hand: contracts placed and not yet delivered. The
                // materials for a whole order are imported the moment it is
                // placed, so without this the import lands months before the
                // output it pays for and a big build reads as negative.
                construction.getUnearnedRevenue(),
                // The full interest bill, foreign coupons included - the
                // government's books should show what it paid, not only the
                // part its own bank collected. See payForeignInterest().
                cityInterestPaid + foreignInterestPaidThisMonth,
                cityCapitalSpending,
                landManager.getLandSalesThisMonth(),
                landManager.getLandPurchasesThisMonth(),
                economyManager.getTotalPropertyTax());

        /*
         * THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE.
         *
         * They used to be, and the government's books were struck from a stash
         * taken a line above the clear - which made the SURPLUS/DEFICIT line one
         * month older than the cash it was supposed to explain. The treasury's
         * window is press to press: it opens where the last month closed and
         * shuts at the bottom of this tick, so it contains the player's own turn
         * AND everything the tick then does. The accumulators, cleared here,
         * spanned start-of-tick to start-of-tick instead - the same length, half
         * a month out of phase - so a building the advisor bought during the tick
         * landed in next month's budget while its cash left in this one.
         *
         * Measured before the fix, on a city played from new with no player
         * actions at all: month 5 moved the treasury by +119.75 and reported a
         * surplus of +1.33; month 6 moved it by +31.61 and reported +119.58.
         * Every row was the previous row's cash, one month late.
         *
         * They are read and cleared together now, at the bottom of the month,
         * by strikeGovernmentBooks() - which is also after processAllDebts(), so
         * the interest is this month's too and nothing needs stashing at all.
         *
         * monthlyMaterialImports stays here: it feeds GDP, not the government
         * block, and updateNationalAccounts() has just read it.
         */
        monthlyMaterialImports = 0;
        monthlyMaterialImportBill = 0;

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
            debtManager.printDebtInfo(month);
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
    
    
    
    /**
     * Hands the debt market everything it prices against.
     *
     * Three inputs, and the overdraft is the one that used to be missing: the
     * market could not see that the city was in the red, so a city $1.1M
     * overdrawn with no bonds left outstanding was quoted the floor rate. All
     * three have to be current before updateInterest() or any quote, which is
     * why this is one call rather than three scattered ones.
     */
    /* =====================================================================
       WHAT IT COSTS TO GO TO MARKET AT ALL

       Underwriting, bond counsel, the rating agency, printing the official
       statement. Real municipal issues pay this before a dollar arrives, and it
       is the reason small issues are rare: the legal and rating work costs
       roughly the same whether you raise one million or fifty.

       So it is a fixed component plus a percentage, not a flat rate. That shape
       is the whole point - it makes churning debt genuinely expensive, it makes
       a tiny issue absurd rather than merely unwise, and it is what motivates
       the minimum issue size below instead of that minimum being an arbitrary
       constant nobody can justify.
       ===================================================================== */

    /** Bond counsel, rating and printing. Payable however small the deal is. */
    /** Term of the note the city is forced into when it cannot pay its bills. */
    public static final int EMERGENCY_NOTE_MONTHS = 6;

    /**
     * Bond counsel, rating and printing. Payable however small the deal is.
     *
     * IN FOUNDING DOLLARS, like every money constant in this codebase, so it is
     * divided by the unit wherever it is used rather than read raw - see
     * issuanceFee(). A flat fee read raw is the same bug as a flat threshold
     * read raw: after a hundred-to-one reform a $12k fee would quietly become
     * $1,200k of founding money, and every small deal in the city would stop
     * being worth doing. Found by the same audit that caught
     * CapitalFlows.MIN_STOCK; it is not currently large enough to move a
     * measured trajectory, and it would have been the moment somebody reformed
     * twice.
     */
    private static final double FIXED_ISSUE_COST = 12;

    /** The fee in today's money. See FIXED_ISSUE_COST. */
    private double issuanceFee() {
        double unit = denomination.getUnit();
        return FIXED_ISSUE_COST / (unit > 0 ? unit : 1);
    }

    /** Underwriter's spread, as a fraction of face. */
    private static final double UNDERWRITING_SPREAD = .0075;

    /**
     * Taken off the proceeds at issue, never added to what is owed.
     *
     * The city borrows the face value and receives less; it does not borrow more
     * to cover its own fees. That keeps the arithmetic honest against
     * getMarketValue(), which prices the FACE - if fees were rolled into the
     * face, a bond would be worth more than it raised the instant it was signed
     * and the buyback would be an arbitrage again.
     */
    public double costOfIssuance(double faceValue) {
        if (faceValue <= 0) return 0;
        return issuanceFee() + faceValue * UNDERWRITING_SPREAD;
    }

    /**
     * What the city actually banks for a note of this face.
     *
     * The discount and the fees in one place, because the quote used to compute
     * them inline and the sizing had no way to ask what a face was worth
     * without restating the same three lines.
     */
    private double netProceeds(double faceValue, double annualRate, int months) {
        double gross = faceValue * (1 - ShortTermTBill.discountFraction(annualRate, months));
        return Math.round((gross - costOfIssuance(faceValue)) * 100) / 100.0;
    }

    /**
     * Face value whose NET proceeds cover cashNeeded - fees included.
     *
     * ShortTermTBill.faceFor() answers a subtly different question: face such
     * that the DISCOUNTED gross is cashNeeded, before anyone is paid. That is
     * the right question for the instrument, which knows nothing about this
     * city's underwriter, and the wrong one for a caller who needs a specific
     * sum in the bank. So the gross-up lives here, where the fee does.
     *
     *     face x (1 - discount) - FIXED - face x SPREAD  >=  cashNeeded
     *
     * solved for face. The denominator is (1 - discount) - SPREAD, and since
     * the discount is capped at 95% it is never below about 4.25%, so it cannot
     * go to zero or turn negative.
     */
    private double faceForNetProceeds(double cashNeeded, double annualRate, int months) {
        double perDollarOfFace =
                (1 - ShortTermTBill.discountFraction(annualRate, months)) - UNDERWRITING_SPREAD;
        if (perDollarOfFace <= 0) {
            return ShortTermTBill.faceFor(cashNeeded, annualRate, months);
        }
        return (cashNeeded + issuanceFee()) / perDollarOfFace;
    }

    /**
     * The smallest deal worth doing, which grows with the city.
     *
     * Replaces the silent $100,000k floor the long-bond screen used to pass as a
     * rounding factor - a $100M minimum face on a city with a $4M budget, with
     * nothing anywhere saying so (design queue F2). It was not wrong to have a
     * minimum; it was wrong to have one nobody could see and that never moved.
     *
     * Tied to a year of tax revenue, because that is what makes the fixed costs
     * proportionate: a city that collects $4M a year has no business arranging a
     * $50k issue, and a city collecting $400M has no business arranging a $400k
     * one. Floored so a brand new city can still borrow at all.
     */
    public double minimumIssueSize() {
        double annualRevenue = economyManager.getTaxIncome() * 12;
        return Math.max(50, annualRevenue * .05);
    }

    /* =====================================================================
       THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE

       The rate curve already knows exactly how sound the city is. It just says
       so as "4.27%", which tells a player what they are paying and nothing at
       all about whether that is good. A letter says the second thing, and it is
       how every real borrower experiences its own credit.

       Measured off position within the market's own band rather than typed-in
       percentages, for the reason the band assertions kept teaching: a rate
       threshold written as a number is wrong the next time the curve is
       reshaped, and the curve has been reshaped twice already.
       ===================================================================== */

    public String getCreditRating() {

        double floor = debtManager.floorRate();
        double ceiling = debtManager.ceilingRate();
        if (ceiling <= floor) return "AAA";

        double position = (debtManager.getRate() - floor) / (ceiling - floor);

        if (position < .05) return "AAA";
        if (position < .15) return "AA";
        if (position < .30) return "A";
        if (position < .50) return "BBB";
        if (position < .70) return "BB";
        if (position < .88) return "B";
        return "CCC";
    }

    /** What the rating means, in one line, for the screen. */
    public String getCreditOutlook() {
        return switch (getCreditRating()) {
            case "AAA" -> "Impeccable - the market will lend you anything";
            case "AA"  -> "Very strong";
            case "A"   -> "Strong";
            case "BBB" -> "Adequate - the lowest rating still called investment grade";
            case "BB"  -> "Speculative - you are paying for the doubt";
            case "B"   -> "Highly speculative";
            default    -> "Distressed - lenders expect not to be repaid in full";
        };
    }

    private void priceTheDebtMarket(){
        debtManager.setGDP(economyManager.getMonthGdp());
        debtManager.setTaxRevenue(economyManager.getTaxIncome());
        debtManager.setCashPosition(cash);
    }

    /**
     * The government's books, struck once, at the bottom of the month.
     *
     * AFTER processAllDebts(), so this month's coupon is in it, and over the
     * accumulators live rather than a snapshot of them - which is only possible
     * because the clear happens here too, a line later. The window is then
     * exactly the treasury bridge's: opens where the last month closed, shuts
     * where this one does. See startOfMonthUpdate() for what it was before.
     *
     * updateNationalAccounts() still calls updateGovernment() at the top of the
     * month, against fields that are mostly last month's. That call is
     * provisional and this one overwrites it whole - updateGovernment() is a
     * plain setter, so the second strike wins and nothing accumulates twice.
     */
    private void strikeGovernmentBooks() {
        // What the dial cost the city this month, handed over before the strike
        // reads it. Paid inside paySubsidyIfOwed(), which moved the cash then
        // and there; this is the books catching up with it.
        economyManager.setSubsidiesPaid(getTotalSubsidyPaid());

        economyManager.refreshGovernmentAccounts(
                landManager.getLandSalesThisMonth(),
                cityCapitalSpending,
                landManager.getLandPurchasesThisMonth(),
                cityInterestPaid + foreignInterestPaidThisMonth);

        // Read and cleared in one place, a line apart, so nothing in between
        // can see a half-cleared month and nothing needs a second copy of them.
        // What a SAVE needs is a different question and has a different answer:
        // the struck block itself is carried - see
        // NationalAccounts.governmentToSave().
        cityCapitalSpending = 0;
        landManager.clearMonth();
    }

    private void finalUpdateEconomy(){
        economyManager.setDebt(debtManager.getAllPrincipal());
        double tempCash = cash;
        tempCash += economyManager.getTotalIncome();
        // The utility books what its customers were charged - see
        // UtilitiesHandler.setBilledRevenue().
        servicesManager.getUtilitiesHandler().setBilledRevenue(
                economyManager.getSectorElectricityCharges(),
                economyManager.getSectorWaterCharges());
        economyManager.setUtilityIncome(servicesManager.getServiceNetIncome());
        tempCash += servicesManager.getServiceNetIncome();
        economyManager.finalEconUpdate();

        // The ore, after the rest of the month. Lifting it needs the ground,
        // which EconomyManager has no business knowing about - LandManager owns
        // the reserves and decides how much was actually there.
        economyManager.mineIron(landManager);

        servicesManager.updateServices();
        economyManager.setPricePerWatt(servicesManager.getPricePerWatt());
        economyManager.setPricePerWaterUnit(servicesManager.getPricePerWaterUnit());
        
        if (Double.isFinite(tempCash)) {
            cash = tempCash;
        } else {
            System.out.println("Cash update blocked due to invalid value.");
        }

        /*
         * The government's books used to be re-struck HERE, and that was still
         * too early: processAllDebts() has not run, so the month's coupon was
         * not known and had to come from a stash taken a month before. The
         * strike now happens at the bottom of nextMonth() instead - see
         * strikeGovernmentBooks(). The reason it needed re-striking at all is
         * unchanged and worth keeping: updateNationalAccounts() runs at the top
         * of the month and reads the tax fields as the PREVIOUS month's
         * getTaxIncome() left them, while salesTax and propertyTax, charged on
         * the same pass, are current - so the surplus mixed two months.
         */

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

    /**
     * Recounts the posts on offer. Does NOT decide how many people live here -
     * that is advanceDemographics(), which runs after this and needs the job
     * count to work out what the city is pulling.
     */
    void updatePopulation() {
        refreshJobs();
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
    private BuildLog buildLog = new BuildLog();

    /* =====================================================================
       DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH

       These three decide how many people live here. The pyramid is the stock,
       Migration is what moves it, and FamilyModel arranges whoever is here into
       households and crowds them into the homes that exist.

       WHAT THE SWITCH REPLACED. The population was min(housing, jobs * 2.25),
       recomputed from nothing every month. It had no memory: finish a tower and
       it filled that same month, demolish one and its residents ceased to exist,
       and a city could double between two ticks. That expression survives, in
       Migration, as the TARGET a city moves toward - which is the whole change.
       Housing and jobs now say how attractive the city is, not how many people
       are in it.

       WHAT THAT COST. Two batches of this model ran deliberately inert, with a
       harness playing two identical cities and requiring every figure to match,
       because everything downstream keys off the population - the workforce, the
       fill rate, every sector's output, the wage bill, the wage tax, GDP. That
       assertion is gone now, on purpose, and what replaced it is in
       PopulationCheck: tests that the city fills toward its jobs, keeps taking
       arrivals while crowded, stops before anybody is homeless, and sheds people
       only after a pay tier has been dying for a year.
       ===================================================================== */

    private PopulationCohorts cohorts = new PopulationCohorts();
    private FamilyModel families = new FamilyModel();
    private Migration migration = new Migration();

    /**
     * What labour costs. See LabourMarket - wages used to be six constants and
     * are now a price that moves with how hard the city is to staff.
     */
    private LabourMarket labourMarket = new LabourMarket();

    /**
     * The schools. See Education - the other half of the labour market, and the
     * only thing in the game that can make a skilled worker rather than hire one.
     */
    private Education education = new Education();

    /**
     * How much of the workforce is off sick.
     *
     * Sits with the demographics because that is what it is about, but note it
     * does NOT move anybody: the pyramid, the workforce and the payroll are all
     * exactly what they would have been. See Health.
     */
    private Health health = new Health();

    /**
     * The health SERVICE - its payroll, its fees, and its cemeteries.
     *
     * Distinct from `health` above, which is the sick rate it buys. One is the
     * cause and the bill; the other is the effect.
     */
    private Healthcare healthcare = new Healthcare();

    public PopulationCohorts getCohorts() { return cohorts; }
    public FamilyModel getFamilies()      { return families; }
    public HouseholdBalance getHouseholdBalance() { return householdBalance; }
    public Migration getMigration()       { return migration; }
    public Health getHealth()             { return health; }
    public Healthcare getHealthcare()     { return healthcare; }
    /**
     * Turns the placeholder off, for the harness that proves it is a placeholder.
     *
     * PopulationCheck plays two identical cities with this set either way and
     * requires every live figure to match. Package-private and static because it
     * is a property of the build under test rather than of any one city, and
     * because the alternative - threading a flag through the constructor - would
     * put a permanent seam in Game for a temporary claim.
     */
    /**
     * Ages the city, moves people in and out, and rebuilds the households.
     *
     * THIS IS NOW THE MONTH'S POPULATION STEP. It used to be a placeholder that
     * ran after the population was settled and fed nothing; it now decides the
     * figure, which is why SimulationEngine calls it where it does. The order
     * inside is the model:
     *
     *   1. File the month's wage bill per tier, so the twelve-month decline test
     *      has this month in it before it is asked a question about this month.
     *      Read from the arrays SimulationEngine has just refreshed, and staffed
     *      by LAST month's workforce - which is correct, because last month's
     *      residents are who worked.
     *   2. Age, give birth, die.
     *   3. Migrate, which is by far the biggest of the three.
     *   4. Hand the total to PopulationManager, which derives the workforce from
     *      the population as it stood BEFORE the arrivals - so somebody who moves
     *      in this month starts work next month.
     *   5. Rebuild households and crowd them into whatever homes exist.
     *
     * Steps 4 and 5 must stay in that order: the household rebuild reads the job
     * fill, and the job fill reads the workforce that step 4 sets.
     */
    void advanceDemographics() {

        // In REAL terms - see Migration.recordWages(). A city with mild deflation
        // and indexed wages posts a falling cash bill every month while nothing
        // about it is declining, and the twelve-month gate cannot tell.
        migration.recordWages(populationManager.getStaffedWagePerTier(),
                priceIndex.getIndex());

        /*
         * WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before
         * anybody ages, is born, dies or moves.
         *
         * Both halves of that sentence matter. STAFFED, because a General
         * Hospital with no doctors was treating forty thousand people and every
         * other sector in the game has its output cut by its fill rate. And
         * BEFORE, because these beds served the people who were living here -
         * computing coverage against the population the month ended with would
         * credit a hospital for treating somebody who moved in after the fact.
         */
        double[] fill = populationManager.getJobFillRate();
        double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);
        double seniorCoverage    = careCoverage(CareType.SENIOR, fill);
        double generalCoverage   = careCoverage(CareType.GENERAL, fill);
        double generalCapacity   =
                buildingManager.getStaffedCareCapacity(CareType.GENERAL, fill);
        double servedThisMonth   = cohorts.total();

        double[] served = new double[CareType.values().length];
        for (CareType care : CareType.values()) {
            if (!care.servesTheLiving()) continue;
            served[care.ordinal()] = Math.min(
                    buildingManager.getStaffedCareCapacity(care, fill),
                    care.populationServed(cohorts));
        }

        /*
         * BOTH ENDS OF A LIFE, and now the beginning of one too.
         *
         * Childcare swings infant mortality forty-fold and doubles the birth
         * rate; general care swings the adult band three-fold; senior care moves
         * the seniors' gently. Today's rates are what a HALF-served city gets, so
         * building care does better than the game has ever done and building none
         * does very much worse. The multipliers and the reasoning live in
         * Healthcare.
         */
        double[] mortalityFactors = Healthcare.mortalityFactors(
                childcareCoverage, generalCoverage, seniorCoverage);
        cohorts.advanceMonth(mortalityFactors, Healthcare.birthFactor(childcareCoverage));
        // Kept for the skills step below: the graduates die at the rate the
        // adults do, and that rate depends on the clinics.
        lastAdultMortality = AgeBand.monthlyFromAnnual(
                AgeBand.ADULT.getAnnualMortality()
                        * Math.max(0, mortalityFactors[AgeBand.ADULT.ordinal()]));

        /*
         * Who works this month: the adults who were already living here, read
         * off before migration moves anybody. Captured HERE rather than after
         * the arrivals because somebody who moves in this month starts work next
         * month - the same property PopulationManager.applyPopulation() used to
         * get by reading last month's population, now got honestly.
         */
        double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);

        /*
         * The families the bank discharged last month are leaving. Set before
         * the month's migration rather than folded into it, because it is a
         * push from the balance sheet and has nothing to say about wages.
         */
        migration.setBankruptcyDepartures(householdBalance.getLeavingCity()
                * Math.max(1, families.averageHouseholdSize()));

        /*
         * ...and what a flat costs here, which is a PULL rather than a push and
         * so is set in the same place for a different reason. See
         * Migration.affordabilityPull(): the discharged leave, and the people
         * who would have arrived simply do not.
         */
        migration.setRentBurden(households.getRentBurden());

        cohorts.migrate(migration.monthlyNet(
                population,
                populationManager.getTotalJobs(),
                getHouseholdCapacity(),
                buildingManager.getTotalHomes(),
                families,
                cohorts.share(AgeBand.ADULT),
                // A city that looks after its parents is a city people move to.
                // Seniors are otherwise pure burden here - pension, home, the
                // dearest care in the game, no work - so this is what makes an
                // ageing pyramid something to manage rather than merely endure.
                seniorCoverage,
                labourMarket,
                populationManager));

        population = populationManager.applyPopulation(
                (int) Math.round(cohorts.total()), adultsAlreadyHere);

        /*
         * AND THE SKILLS MOVE WITH THE PEOPLE.
         *
         * The pyramid owns the headcount; this owns the mix. Arrivals bring
         * whatever the world sent, departures take whatever was surplus, and
         * everyone born here enters unskilled - because until schools exist,
         * the only source of a skill in this game is somebody who already had
         * one moving in.
         */
        applyMigrationSkills();

        double[] jobsByTier = new double[PayTier.values().length];
        double[] fillRate = populationManager.getJobFillRate();
        int[] posts = populationManager.getJobs();

        for (JobType type : JobType.values()) {
            int i = type.ordinal();
            jobsByTier[PayTier.of(type).ordinal()] += posts[i] * fillRate[i];
        }

        families.rebuild(cohorts, jobsByTier);

        // One household, one home - and if there are not enough homes, they
        // crowd rather than sleep outside. See FamilyModel.squeeze().
        /*
         * HOMES HAVE SIZES NOW, so this is a match rather than a count.
         *
         * Two passes on purpose. The first says who could not be housed; the
         * crowding valves then turn some of those singles into flatshares,
         * which CHANGES what fits where - five adults sharing need one door,
         * not five - so the second pass is what the landlords actually bill.
         */
        int[] stock = buildingManager.homesBySize();
        double unplaced = families.house(stock);
        families.squeezeUnplaced(unplaced);
        families.noteUnplaced(families.house(stock));

        // What the landlords can bill, off the match rather than off an
        // average, and split by which segment the door was in. See
        // CommercialHandler.getRentIncome() and FamilyModel's TWO SEGMENTS.
        economyManager.getCommercialHandler().setRentWeight(
                families.studioRentWeight(), families.familyRentWeight());

        // And the advisor prices a new home on who would move into it.
        businessInvestment.setFamilies(families);
        businessInvestment.setBank(bank);

        /*
         * ...and the ones who cannot afford one either.
         *
         * squeeze() above is the housing shortage; this is the poverty. A tier
         * whose single wage does not cover a home and a basket puts that share
         * of its single adults into flatshares - which is what people actually
         * do, and it is the response the model had no answer to at all. Before
         * this, an unskilled single adult short of rent went on being short of
         * it until the credit ran out and the hunger started.
         *
         * Reads LAST month's books, which is the only honest source: this
         * month's have not been struck yet, and a household decides where to
         * live on the payslip it has already had.
         */
        families.shareByAffordability(households.livingAlonePressure(families));

        /*
         * 6. WHO IS TOO ILL TO WORK.
         *
         * Last, and after the population is settled, because coverage is beds
         * divided by the people who need them and both halves have just moved.
         * It changes no figure above this line - not the pyramid, not the
         * workforce, not one wage - which is the whole specification: it
         * modifies the fill rate, it does not reduce the workforce.
         *
         * The result reaches the sectors through SimulationEngine.updateEconomy,
         * one line below setRoadRatio, because it is the fourth ratio and
         * travels the same road as the other three.
         */
        /*
         * 6. THE HEALTH SERVICE'S OWN MONTH: what it cost, what it collected,
         *    and what it did with the people who died in step 2.
         *
         * Runs after the population is settled because the fees are charged on
         * who was actually treated, and the funerals on who actually died. The
         * bill it produces reaches the treasury through EconomyManager, whose
         * getExpenses() finalUpdateEconomy() moves the cash by - which is the
         * whole of the funding fix.
         *
         * PLOTS ARE NOMINAL AND CREMATION IS STAFFED, deliberately. A plot is a
         * piece of ground: it exists whether or not anybody is on the payroll,
         * and it is consumed permanently. A crematorium is a machine, and a
         * machine with nobody to run it handles nobody.
         */
        healthcare.advanceMonth(
                buildingManager.getCategoryPayroll(BuildingType.HEALTHCARE,
                        populationManager.getWagesPerType(), fill),
                buildingManager.getUpkeepByCategory(BuildingType.HEALTHCARE),
                served,
                cohorts.getLastDeaths(),
                burialShare(),
                buildingManager.getCareCapacity(CareType.BURIAL),
                buildingManager.getStaffedCareCapacity(CareType.CREMATION, fill));

        economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());

        /*
         * 6b. AND THE SCHOOLS.
         *
         * After the population is settled, like healthcare, and for the same
         * reason: enrolment is measured against the children and the workforce
         * that actually exist this month. Before the labour reprice, because a
         * graduate produced now is in the supply the wage is struck against
         * next month - a person who finished a degree this month is looking for
         * work, not still studying.
         */
        education.advanceMonth(
                buildingManager.getStaffedEducationPlaces(fill),
                cohorts,
                populationManager,
                labourMarket,
                buildingManager.getCategoryPayroll(BuildingType.EDUCATION,
                        populationManager.getWagesPerType(), fill),
                buildingManager.getUpkeepByCategory(BuildingType.EDUCATION),
                // The students thin at the rate the adults do - deaths, ageing
                // out, and this month's share of leavers.
                lastAdultMortality + AgeBand.ADULT.monthlyOutflowRate()
                        + (population > 0 ? migration.getLastDepartures() / population : 0));

        populationManager.applyBandFlow(education.getGraduates());
        populationManager.addLicences(education.getLicences());
        // And whoever is in a lecture theatre is out of the labour supply
        // until they come out of it - see PopulationManager.workforceByBand().
        populationManager.setStudying(education.getStudying());
        // A licence holder is a graduate first, and the graduate count has just
        // moved. See trimLicencesToBand().
        populationManager.trimLicencesToBand();

        economyManager.setEducation(education.getGrossCost(), education.getFees());

        /*
         * 7. And who is too ill to work. Last, because the dead nobody buried
         *    are one of the three things that decide it.
         */
        health.advanceMonth(generalCapacity, servedThisMonth, month,
                healthcare.getUnburied(),
                // The last step of the household waterfall, arriving as a health
                // problem: savings gone, credit gone, so they eat less.
                householdBalance.getHungerRate());
    }

    /**
     * How much of the people who need a kind of care the city can actually give.
     *
     * Zero denominators read as fully covered, not as a crisis: a city with no
     * children does not have a childcare shortage. See Health.coverageOf().
     */
    private double careCoverage(CareType care, double[] fill) {
        return Health.coverageOf(
                buildingManager.getStaffedCareCapacity(care, fill),
                care.populationServed(cohorts));
    }

    /**
     * The share of the dead whose families would choose a plot over an urn.
     *
     * Jerus: "if there is excess savings then people prefer cemetery, otherwise
     * crematorium". A plot is saved up for rather than paid out of one month's
     * income, so the test is whether a household's monthly surplus would cover
     * one over ten years - and the answer is yes for almost anybody who is not
     * running a deficit, which is the intended reading.
     *
     * Weighted by HOUSEHOLDS rather than by people, because a funeral is bought
     * by a family rather than per head. Read from last month's statement, which
     * is the same one-month lag the pension figures on that statement already
     * carry: the books are struck at the top of the month and this runs in the
     * middle of it.
     *
     * A city whose books are empty - the first month of a new game - buries
     * nobody, which is correct rather than convenient. It has no cemetery
     * either.
     */
    private double burialShare() {

        double threshold = Healthcare.BURIAL_FEE / Healthcare.BURIAL_SAVING_MONTHS;
        double afford = 0, all = 0;

        for (int row = 0; row < households.getRowCount(); row++) {
            double homes = households.getRowHouseholds(row);
            if (homes <= 0) continue;

            all += homes;
            if (households.getRowSaving(row) / homes >= threshold) afford += homes;
        }

        return all > 0 ? afford / all : 0;
    }

    /**
     * Files the month's finished buildings.
     *
     * Called from SimulationEngine with whatever advanceConstruction() handed
     * back, and stamped with the month HERE rather than there because the clock
     * lives on this side. Note the month it stamps is the one that is still
     * running: advanceConstruction() is the first thing the month does, so a
     * building that opens is recorded against the month it opened in, which is
     * the same month the player sees it appear.
     */
    public void recordCompletions(java.util.List<BuildingManager.Completion> finished) {
        if (finished == null) return;
        for (BuildingManager.Completion done : finished) {
            buildLog.record(done.building, done.quantity, month);
        }
    }

    public BuildLog getBuildLog(){
        return buildLog;
    }

    /** What happened during the last fast-forward. See TimeSkipReport. */
    private TimeSkipReport skipReport = new TimeSkipReport();

    public TimeSkipReport getSkipReport(){
        return skipReport;
    }

    /** The residents' own books. See HouseholdAccounts. */
    private HouseholdAccounts households = new HouseholdAccounts();

    /**
     * What the households have saved and what they owe.
     *
     * The stock beside the flow. HouseholdAccounts is a month's statement and
     * can be rebuilt from the month; this is a balance sheet and cannot, so it
     * is saved. See HouseholdBalance.
     */
    private final HouseholdBalance householdBalance = new HouseholdBalance();

    /**
     * The city's commercial bank - every loan in it, and every default.
     *
     * Owned here rather than by EconomyManager because it lends to all three of
     * them: the sectors, the treasury and the households. See Bank.
     */
    /**
     * The city's account with the rest of the world. Phase one: measured, not
     * yet acted on. See ForeignAccounts.
     */
    private final ForeignAccounts foreign = new ForeignAccounts();

    /**
     * Hot money, and the run on it. See CapitalFlows.
     *
     * Kept beside the foreign accounts rather than inside them because it is a
     * different kind of thing: ForeignAccounts records what happened at the
     * city's edge, and this decides what happens next. One is a ledger, the
     * other is a crowd.
     */
    private final CapitalFlows hotMoney = new CapitalFlows();

    /**
     * The city's own savings abroad, the mirror of the crowd above. See
     * OutwardInvestment for why a surplus economy needs one.
     */
    private final OutwardInvestment outward = new OutwardInvestment();

    /**
     * Who owns the city's companies. See Equity: the households hold their
     * shares per cell, the world the rest; offerings run inside consider()
     * and capitaliseBank(), dividends in payDividends().
     */
    private final Equity equity = new Equity();
    public Equity getEquity() { return equity; }

    public OutwardInvestment getOutwardInvestment() { return outward; }

    /**
     * What a month costs a household, against founding. See PriceIndex.
     *
     * Priced at the END of the month, from the prices the month actually
     * charged, and READ at the top of the next one - which is why wages chase
     * it with a lag rather than reacting to a number struck the same instant.
     */
    private final PriceIndex priceIndex = new PriceIndex();

    /** The rest of the world, which has its own inflation. See WorldEconomy. */
    private final WorldEconomy world = new WorldEconomy();

    public WorldEconomy getWorldEconomy() { return world; }

    public PriceIndex getPriceIndex() { return priceIndex; }

    public CapitalFlows getCapitalFlows() { return hotMoney; }

    /** The rate a year ago, for spotting a run. Twelve months, kept as a ring. */
    private final double[] rateHistory = new double[12];
    private int rateHistoryFilled;

    /** How far the currency has fallen over the last year, as a share. */
    public double yearlyDepreciation() {
        if (rateHistoryFilled < 12) return 0;
        double then = rateHistory[month % 12];
        if (then <= 0) return 0;
        return foreign.getRate() / then - 1;
    }

    public ForeignAccounts getForeignAccounts() { return foreign; }

    private final Bank bank = new Bank();
    public Bank getBank() { return bank; }

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

    /**
     * The same imports in MONEY, at the price each was charged at.
     *
     * Two readers, one figure: the builders' statement (their materials
     * expense, a TRADE debit in the money audit, so the balance of payments
     * sees it) and the national accounts (imports). Kept beside the count
     * rather than derived from it because the count is bought at two prices
     * across its window - last month's rate for the private orders that
     * follow the accounts, this month's for the repairs that precede them -
     * and a bill re-struck at today's price is a different bill. Money, so
     * it scales in a reform; the count does not.
     */
    private double monthlyMaterialImportBill;

    /**
     * What a save carried about next month's shopping and rent.
     *
     * PUT BACK AFTER THE REBUILD, not before it, and that is the whole point.
     * rebuildSimulationState() re-derives a great deal of the month, and it
     * does not reproduce every input exactly - the health service's and the
     * schools' fees come back within a few thousandths, which never mattered
     * while nothing downstream read them. The budget constraint reads them:
     * fees feed the household plan, the plan caps the shops, and the shops feed
     * the rent through who can afford to live alone. So a rounding difference
     * five layers up arrived as a different month's cash.
     *
     * These three are the interface between one month and the next. Carried
     * whole and re-applied last, they make the seam exact rather than nearly
     * exact - which is the standard SaveFileCheck holds, to the cent.
     */
    private double carriedRentWeight;

    /** Doors let, as the save recorded them. -1 when the save did not carry it. */
    private double carriedOccupiedHomes = -1;
    /** ...and the studio half of it. See DataSave.getRentWeightStudio(). */
    private double carriedStudioWeight;
    private double carriedRetailCapacity;
    private double carriedRetailWant;

    /** Interest the city paid this month, accumulated by InterestExpense(). */
    private double cityInterestPaid;
    private java.util.Map<String, String> lastInvestment = new java.util.LinkedHashMap<>();

    /** What each sector spent on buildings this month, less what it sold back. */
    private final java.util.Map<String, Double> sectorInvested =
            new java.util.LinkedHashMap<>();

    public double getInvestedThisMonth(String sector) {
        return sectorInvested.getOrDefault(sector, 0.0);
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }
    public PopulationManager getPopulationManager() {
        return populationManager; //PopulationManager;
    }

    public LabourMarket getLabourMarket() { return labourMarket; }
    public Education getEducation() { return education; }

    /**
     * Re-prices labour, then hands the new wages to everyone who pays them.
     *
     * ORDER MATTERS AND IT IS NARROW. It has to run after updateJobs() - the
     * market prices against the posts that exist THIS month - and before
     * UpdateTotalWagePerType(), which multiplies posts by wage to get the bill.
     * Between those two calls is the only place it can go, which is why it is a
     * method here rather than three lines inlined in SimulationEngine.
     *
     * The city's own payroll tracks the market with everybody else's. It
     * competes for the same doctors, so it pays what they cost - which means a
     * doctor shortage raises the hospital bill whether the player chose it or
     * not. That is the pressure that will make a medical school worth building.
     */
    public void repriceLabour() {
        labourMarket.advanceMonth(
                // STAFFABLE, not raw. A band is priced on the work its own
                // members can take; the posts only a licence holder can fill
                // are priced separately, as a licence premium on that job.
                populationManager.staffablePostsByBand(),
                populationManager.supplyByBand(),
                populationManager.getJobs(),
                populationManager.getLicensedHeads());
        populationManager.takeWagesFrom(labourMarket);
    }

    /**
     * Moves the workforce's skill mix by who arrived and who left.
     *
     * Only the SKILLED counts move, and only by migration. The unskilled band
     * is not stored at all - it is whatever is left of the workforce - so
     * births, deaths and ageing move it for free and a child growing into work
     * is unskilled by construction rather than by a formula remembering to
     * dilute them. The first version multiplied shares by a grown workforce and
     * quietly bred graduates out of the city's own births.
     */
    /** This month's adult death rate with the clinics applied. Set by advanceDemographics(). */
    private double lastAdultMortality = AgeBand.ADULT.monthlyMortality();

    private void applyMigrationSkills() {

        /*
         * FIRST, THE PEOPLE WHO LEFT THE WORKFORCE ALTOGETHER.
         *
         * Before migration, because this month's arrivals have not worked a day
         * yet and should not be dying of old age on the way in.
         *
         * At the rate the adults actually died this month - the healthcare-
         * modified one the cohorts were just advanced with - not the table's
         * base rate. Reading the base rate made an unserved city's graduates
         * immortal relative to its labourers, so it drifted skill-heavy for
         * no reason anyone had built.
         */
        populationManager.retireSkilled(
                lastAdultMortality + AgeBand.ADULT.monthlyOutflowRate());

        /*
         * ADULTS ONLY. The arrival and departure mixes each sum to the whole
         * headcount that moved, and cohorts.migrate() spreads that headcount
         * across every age band in the city's own shape - so roughly forty
         * per cent of the people in each mix are children and seniors, who
         * hold no skill and do no work. Until 2026-09-06 the whole mix was
         * added to the ADULT skilled counts, booking every arriving child as
         * a graduate, and the only thing stopping the skilled from
         * outnumbering the workforce was a clip in PopulationManager. Scaled
         * by the adult share the migrants arrived in, which is the share the
         * cohorts gave them.
         */
        double adultShare = cohorts.share(AgeBand.ADULT);
        populationManager.applySkilledFlows(
                scaled(migration.getLastArrivalMix(), adultShare),
                scaled(migration.getLastDepartureMix(), adultShare));

        // Some of the graduates who moved in were already doctors. See
        // Migration.getLastArrivalLicences() - without this a city with no
        // medical school could never have one at all, which is not "expensive",
        // it is a wall, and the world-supply design exists to avoid walls.
        populationManager.addLicences(scaled(migration.getLastArrivalLicences(), adultShare));
        populationManager.trimLicencesToBand();
    }

    private static double[] scaled(double[] values, double by) {
        if (values == null) return null;
        double[] out = new double[values.length];
        for (int i = 0; i < values.length; i++) out[i] = values[i] * by;
        return out;
    }


    
    /**
     * Files this month in the graph history.
     *
     * The rounding used to live here, in a call of eight positional numbers.
     * Both moved into HistorySave when the series went from eight to
     * twenty-three: a twenty-three-argument call is a machine for transposing
     * two of them silently, and the recorder is the thing that should know what
     * precision each series is kept at.
     */
    public void recordMonth() {
        historySave.recordMonth(this);
        // Same call site as the graph, and for the same reason: a warning
        // raised in the middle of a fifty-month skip has to be raised by the
        // city, not by whichever screen the player comes back to.
        inbox.takeMonth(this);
        sectorBooks.takeMonth(this);

        /*
         * ...and the register reads each company's closed month, which is
         * what its next offering will be judged on: profitable or not, steady
         * or not, building or not. The bank's month closed in closeMonth().
         */
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            if (c == Equity.BANK) {
                equity.recordMonth(c, bank.getProfitLastMonth(), 0);
            } else {
                SectorBooks.SectorMonth m = sectorBooks.get(PolicySector.byCreditName(Equity.COMPANIES[c]));
                equity.recordMonth(c, m.netIncome(), m.spentOnBuildings());
            }
        }
    }

    public Inbox getInbox() { return inbox; }
    public SectorBooks getSectorBooks() { return sectorBooks; }
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

        // Accrued after this month's income was banked, so it is charged next
        // month. Nothing can rederive it from the debt book, because whether it
        // has been charged yet is a fact about where in the month we are.
        // getInterestAccrued(), NOT getExpenses(): the field is named for the
        // interest and the load path feeds it straight back to setInterest().
        // Saving the whole expense line double-counted pensions on load.
        dataSave.setCityInterestAccrued(economyManager.getInterestAccrued());
        dataSave.setPropertyTaxCharges(economyManager.getPropertyTaxCharges());
        dataSave.setInterestCharges(economyManager.getInterestCharges());

        // The month's flows. Balances alone cannot reconstruct a month's
        // income statement - see DataSave for which ones and why.
        // The local/imported split behind that cost. Set beside it because it
        // is the same fact about the same month.
        dataSave.setRetailLocalPurchase(economyManager.getRetailLocalPurchase());
        dataSave.setRetailImportPurchase(economyManager.getRetailImportPurchase());

        dataSave.setMonthFlows(
                economyManager.getRetailCostOfGoods(),
                economyManager.getRetailLocalImports(),
                economyManager.getRetailGlobalImports(),
                economyManager.getRetailFillBasis(),
                economyManager.getRetailImportTax(),
                economyManager.getIndustryDemand(),
                economyManager.getIndustryUnitsSold(),
                economyManager.getIndustryUnitsImported());
        dataSave.setIndustryLocalSalesValue(economyManager.getIndustryLocalSalesValue());

        // The utilisation those statements were written against - see DataSave.
        dataSave.setRatioBasis(
                economyManager.getEnergyRatioBasis(),
                economyManager.getWaterRatioBasis(),
                economyManager.getRoadRatioBasis(),
                economyManager.getHealthRatioBasis());

        // ...and the statements themselves, which is what makes the line above
        // a fallback rather than the fix. See CommercialHandler.getReportState().
        dataSave.setReportState(
                economyManager.getCommercialReportState(),
                economyManager.getIndustrialReportState(),
                economyManager.getHeavyIndustryReportState(),
                economyManager.getMiningReportState());

        // Policy: the rates, every offset, the protected sectors, and the
        // month's VAT ledger.
        dataSave.setTaxPolicyState(economyManager.getTaxPolicy().getPolicyState());
        dataSave.setHouseholdBalance(householdBalance.toSaveArray());
        dataSave.setHouseholdCells(householdBalance.cellKeys(),
                householdBalance.toCellSaveArray());
        dataSave.setBankCash(bank.getCash());
        dataSave.setBankBranchesCapitalised(bank.getBranchesCapitalised());
        dataSave.setBankProfitLastMonth(bank.getProfitLastMonth());
        dataSave.setBankDepositRate(bank.depositRate());
        dataSave.setBankSolvency(bank.solvencyToSave());
        dataSave.setBankLastMonth(bank.lastMonthToSave());
        dataSave.setHousingOccupancy(new double[]{
                economyManager.getCommercialHandler().getOccupiedHomes() });
        dataSave.setForeignAccounts(foreign.toSaveArray());
        dataSave.setForeignStanding(debtManager.foreignStandingToSave());
        dataSave.setCapitalFlows(hotMoney.toSaveArray());
        dataSave.setOutwardInvestment(outward.toSaveArray());
        dataSave.setEquity(equity.keys(), equity.toSaveArray());
        dataSave.setPriceIndex(priceIndex.toSaveArray());
        dataSave.setWorldEconomy(world.toSaveArray());
        dataSave.setTradedExchangeRate(economyManager.getExchangeRate());
        dataSave.setPolicyRate(debtManager.getPolicyRate());
        dataSave.setCostOfLiving(labourMarket.getCostOfLiving());
        dataSave.setStoreSellPrice(economyManager.getCommercialHandler().getStoreSellPrice());
        dataSave.setRentPrice(economyManager.getCommercialHandler().getRentPrice());
        dataSave.setStudioRentPrice(
                economyManager.getCommercialHandler().getStudioRentPrice());
        dataSave.setDenomination(denomination.toSaveArray());
        dataSave.setBankTaxCharged(economyManager.getBankTax());
        dataSave.setRentWeight(families.rentWeight());
        dataSave.setRentWeightStudio(families.studioRentWeight());
        dataSave.setRetailCapacity(economyManager.getCommercialHandler().getSpendingCapacity());
        dataSave.setRetailWant(economyManager.getCommercialHandler().getWantedSpend());
        dataSave.setAutoSubsidy(autoSubsidy.clone());
        dataSave.setSalesTaxLedger(economyManager.getSalesTaxLedger().getLedgerState());

        // The land office's window, the ore under the city, and the retainer.
        dataSave.setLandState(
                landManager.getMarket().getListingState(),
                landManager.getIronDeposits(),
                landManager.getIronReserveTonnes());
        dataSave.setLandMarketPrices(landManager.getMarket().getPriceState());
        dataSave.setIronLocalPrice(economyManager.getIronLocalPrice());
        dataSave.setFoodLocalPrice(economyManager.getFoodLocalPrice());
        dataSave.setMiningCash(economyManager.getMiningCash());
        dataSave.setConstructionSubsidy(constructionSubsidy);
        dataSave.setConstructionShedding(constructionShedMonth, constructionShedPoints);

        dataSave.setNationalAccounts(economyManager.getNationalAccountsState());

        // History, not state: what the city lost and what its lenders wrote off.
        dataSave.setDemolitions(demolitionLog.all());
        dataSave.setBuilds(buildLog.all());
        dataSave.setCohorts(cohorts.toSaveArray());
        dataSave.setFamilies(families.toSaveArray());
        dataSave.setMigration(migration.toSaveArray());
        dataSave.setHealth(health.getState());
        dataSave.setHealthcare(healthcare.getState());
        dataSave.setLabour(labourMarket.state());
        dataSave.setSkilledWorkforce(populationManager.getSkilledHeads());
        dataSave.setLicences(populationManager.getLicensedHeads());
        dataSave.setEducation(education.getState());

        /*
         * The private sector's memory and the player's own turn. Both are
         * HISTORY - what has happened - and neither can be read off the balances
         * a month ended in. See the note in DataSave.
         */
        dataSave.setSectorLossMonths(businessInvestment.getLossMonthsState());
        dataSave.setPopulationTrend(businessInvestment.getPopulationHistory());
        dataSave.setCityCapitalSpending(cityCapitalSpending);
        dataSave.setCityMaintenancePaid(cityMaintenancePaid);
        dataSave.setSubsidyPaid(subsidyPaid.clone());
        dataSave.setHouseholdStatement(households.getStatementState());
        dataSave.setMonthlyMaterialImports(monthlyMaterialImports);
        dataSave.setMonthlyMaterialImportBill(monthlyMaterialImportBill);
        dataSave.setMaterialsConsumed(materialsConsumed);
        dataSave.setWriteOffTotals(
                economyManager.getBusinessDebtManager().getWriteOffTotals());
        dataSave.setRestructureCounts(
                economyManager.getBusinessDebtManager().getRestructureCounts());
        dataSave.setBlockedMonths(
                economyManager.getBusinessDebtManager().getBlockedMonthsAll());

        ConstructionHandler builders = servicesManager.getConstructionHandler();
        dataSave.setConstructionBooks(builders.getCash(),
                builders.getUnearnedRevenue(), builders.getBacklogPoints(),
                builders.getMaterialsPending());
        dataSave.setConstructionStatement(builders.getNetIncome(), builders.getReportProfitTax());
        dataSave.setConstructionMaterials(buildingManager.getConstructionMaterials());
        dataSave.setStoreInventory(economyManager.getStoreInventory());
        dataSave.setStoreLastMonthSales(economyManager.getCommercialHandler().getLastMonthSales());
        dataSave.setIndustryFoodInventory(economyManager.getIndustryFoodInventory());
        dataSave.setPopulation(populationManager.getPopulation());

        // Not derivable from the population beside it: the month was worked by
        // the people who lived here when it started. See
        // PopulationManager.restoreWorkforce().
        dataSave.setWorkforce(populationManager.getWorkforceForSave());
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
        dataSave.setNotices(inbox.all());
        dataSave.setSectorBooks(sectorBooks.thisMonth());
        dataSave.setSectorBooksBefore(sectorBooks.lastMonth());
        dataSave.setTreasuryMonth(treasuryMonthToSave());
        dataSave.setGovernmentMonth(economyManager.governmentMonthToSave());
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

    /**
     * Why the last fast-forward stopped early, or null.
     *
     * Read and cleared by the UI, so a skip that broke says so on screen once
     * instead of leaving the player wondering why the month counter stopped.
     */
    private String skipFailure;

    public String takeSkipFailure() {
        String failure = skipFailure;
        skipFailure = null;
        return failure;
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
        cityPrincipalRepaidThisMonth += amount;
    }

    /* =======================================================================
       PAYING THE WORLD BACK
       =======================================================================

       WHY THESE ARE NOT subtractCash() AND InterestExpense(), which is the
       whole structural difference between foreign and domestic paper:

       The city's bonds are bought BY ITS OWN BANK. cityDebtRaisedThisMonth is
       handed to bank.lend() and cityPrincipalRepaidThisMonth to
       bank.takeRepayment(), so a domestic bond is a loan on the bank's book,
       the interest is the bank's income, and none of it crosses the city's
       edge. That is exactly the circular-capital hole: a treasury that can only
       borrow from the institution it is recapitalising.

       Foreign paper is bought by somebody else. It must never reach the bank's
       book - so it gets its own counters, which nothing hands to the bank - and
       the money genuinely leaves, so MoneyAudit has to see it crossing the
       boundary. Both facts follow from using these two methods instead.

       Kept in LOCAL money, because that is the currency MoneyAudit is written
       in; the USD figure lives on the instrument.
       ======================================================================= */

    private double foreignDebtRaisedThisMonth;
    private double foreignPrincipalRepaidThisMonth;
    private double foreignInterestPaidThisMonth;

    double getForeignDebtRaisedThisMonth()      { return foreignDebtRaisedThisMonth; }
    double getForeignPrincipalRepaidThisMonth() { return foreignPrincipalRepaidThisMonth; }
    double getForeignInterestPaidThisMonth()    { return foreignInterestPaidThisMonth; }

    /**
     * A slice of USD principal, repaid.
     *
     * @param usd what the contract says, in the currency it says it in
     */
    public void repayForeignPrincipal(double usd) {
        double local = usd * foreign.getRate();
        cash -= local;
        foreignPrincipalRepaidThisMonth += local;
    }

    /**
     * A USD coupon.
     *
     * NOT through InterestExpense(), and it took a $69.50 audit residual to
     * find out why. That method feeds economyManager's interest accrual, and
     * the whole of that accrual is handed to bank.takeInterest() at the bottom
     * of the month - because the bank is who holds the city's paper and
     * therefore who receives its coupons. A foreign coupon routed through it
     * was paid to the bank AND declared as leaving the country: the same $69.50
     * counted twice, once inside the pools and once out of them.
     *
     * So it leaves cash directly, like the principal does. What the city's
     * income statement would have lost by that is put back where the month's
     * interest is struck - see strikeGovernmentBooks(). The statement gets the
     * full interest bill, and only the bank's share reaches the bank.
     */
    public void payForeignInterest(double usd) {
        double local = usd * foreign.getRate();
        cash -= local;
        foreignInterestPaidThisMonth += local;
    }

    /*
     * THE WORLD'S SIDE OF THE CITY'S DEBT, for MoneyAudit.
     *
     * Bonds and bills are the one place the treasury's cash moves against the
     * outside world inside a month tick without going through a statement:
     * an emergency note raises cash, a maturing slice repays it. Counted here,
     * zeroed at the top of nextMonth(), never saved.
     */
    private double cityDebtRaisedThisMonth;

    /**
     * Face value less cash paid, on everything the city issued this month.
     *
     * THE BANK BUYS THE PAPER AT A DISCOUNT. The city receives par less fees and
     * owes par, and the difference is neither a leak nor a fee to nobody - it is
     * what the buyer makes on the deal. Until this existed the bank's cash fell
     * by what it paid while its book rose by what it was owed, and the gap came
     * out in the wash as equity that had appeared from nowhere: $101.76 in one
     * month of a small city, found by asserting that equity moves by net income
     * and nothing else.
     */
    private double cityDiscountThisMonth;
    private double cityPrincipalRepaidThisMonth;

    /**
     * What the treasury raised, as it stood when the month began.
     *
     * cityDebtRaisedThisMonth is cleared at the top of nextMonth(), so by the
     * time the bank settles at the bottom it holds this month's issuance and
     * not the one the bank is being told about. Snapshotted rather than read.
     */
    private double cityDebtRaisedForBank;
    private double cityDiscountForBank;
    double getCityDebtRaisedThisMonth()      { return cityDebtRaisedThisMonth; }
    double getCityPrincipalRepaidThisMonth() { return cityPrincipalRepaidThisMonth; }

    /* =======================================================================
       WHAT THE TREASURY ACTUALLY DID
       =======================================================================

       Jerus: "show how much was the actual month change, like in the next month
       button it shows 3k but sometimes cause of land buybacks or sales it was
       actually more or less."

       He is right, and the reason is not a bug. The surplus is an INCOME
       measure: revenue less expenses. The treasury balance is a CASH measure.
       Two things move one without moving the other, and both are ordinary:

         - issuing paper raises cash and is not revenue
         - repaying principal spends cash and is not an expense (only the
           coupon is - which is why the interest line can read zero while
           millions leave for the lenders)

       So a month can close with a $3,056 surplus and a treasury that fell by
       $200,000, and until now the game showed the first figure and left the
       player to discover the second by watching the number.

       REPORTING ONLY. Nothing reads these; they are written once at the bottom
       of the month and read by screens. Saved, because a reconciliation that is
       blank until you have played a month is blank exactly when a returning
       player looks at it.

       AND THE RESIDUAL IS NOT SWALLOWED. treasuryUnexplained() is whatever the
       three named flows do not account for, and the screen prints it as its own
       line. A breakdown that quietly absorbs its own gap is worse than no
       breakdown - the same rule the sector cash flow and the sick rate follow.
       ======================================================================= */

    private double treasuryOpening;
    private double treasuryClosing;
    private double treasuryRaised;
    private double treasuryRepaid;
    private double treasurySurplus;
    private boolean treasuryRecorded;

    private void takeTreasuryMonth() {
        treasuryClosing = cash;
        treasuryRaised  = cityDebtRaisedThisMonth + foreignDebtRaisedThisMonth;
        treasuryRepaid  = cityPrincipalRepaidThisMonth + foreignPrincipalRepaidThisMonth;
        treasurySurplus = economyManager.getNationalAccounts().getBalance();
        treasuryRecorded = true;
    }

    /** True once a month has closed. False on a city that has never ticked. */
    public boolean hasTreasuryMonth() { return treasuryRecorded; }

    public double getTreasuryOpening() { return treasuryOpening; }
    public double getTreasuryClosing() { return treasuryClosing; }
    public double getTreasuryRaised()  { return treasuryRaised; }
    public double getTreasuryRepaid()  { return treasuryRepaid; }
    public double getTreasurySurplus() { return treasurySurplus; }

    /** What the balance actually did, which is the figure a player watches. */
    public double getTreasuryChange() { return treasuryClosing - treasuryOpening; }

    /**
     * Everything the three named flows do not explain.
     *
     * NOT A RESIDUAL TO BE HIDDEN, and not zero in this model. It is the rest
     * of what the treasury did: land the city bought or sold, buildings it paid
     * for, reserves, capital put into the bank, bonds bought back - none of
     * which is a budget line - PLUS whatever the government's books date to a
     * different month from the money. The screen prints it as its own row with
     * its own name, which is the only honest way to show a total that does not
     * foot.
     */
    public double getTreasuryUnexplained() {
        return getTreasuryChange()
                - (treasurySurplus + treasuryRaised - treasuryRepaid);
    }

    double[] treasuryMonthToSave() {
        return new double[] { treasuryOpening, treasuryClosing, treasuryRaised,
                              treasuryRepaid, treasurySurplus,
                              treasuryRecorded ? 1 : 0 };
    }

    void restoreTreasuryMonth(double[] saved) {
        if (saved == null || saved.length < 6) return;
        treasuryOpening  = saved[0];
        treasuryClosing  = saved[1];
        treasuryRaised   = saved[2];
        treasuryRepaid   = saved[3];
        treasurySurplus  = saved[4];
        treasuryRecorded = saved[5] != 0;
    }

    /**
     * The government's month as the save carried it, waiting for the rebuild.
     *
     * Read where the rest of the save is read and applied after
     * rebuildSimulationState(), because the rebuild's last act is to re-strike
     * this block from state that cannot reproduce it. Nulled once applied so
     * nothing can put a stale month back a second time.
     */
    private double[] loadedGovernmentMonth;

    /** Last month's money-conservation residual. See MoneyAudit. */
    private MoneyAudit.Result lastMoneyAudit = MoneyAudit.Result.NONE;
    public MoneyAudit.Result getLastMoneyAudit() { return lastMoneyAudit; }
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
    
    
   /**
    * The build-funding bill: raises a stated amount of CASH, not face value.
    *
    * NOTE: this used to take a face value already grossed up by the caller off
    * debtManager.getRate() - the STANDING rate, struck before this bill was on
    * the books. Two things were wrong with that. It priced the loan against a
    * balance sheet that stopped existing the moment the money arrived, which is
    * exactly what the repricing was meant to end; and because the screen did the
    * same sum separately, the number shown to the player and the number booked
    * were two independent calculations that only happened to agree.
    *
    * It takes the cash the city needs now and quotes it like any other bill.
    *
    * @param cashNeeded what has to reach the treasury
    * @return the terms actually struck, so a caller can show them
    */
    /* =====================================================================
       BUYING YOUR OWN DEBT BACK

       The other half of the finance menu. Issuing turns future payments into
       cash now; this turns cash now into no future payments, and the price is
       whatever the paper is actually worth rather than what it says on it.
       ===================================================================== */

    /** What one bond would cost to clear right now, at the standing rate. */
    public double quoteRepurchase(Debt debt) {
        if (debt == null) return 0;
        priceTheDebtMarket();
        debtManager.updateInterest();
        return debt.getMarketValue(debtManager.getRate());
    }

    /** What the city would book as a gain (positive) or loss (negative). */
    public double repurchaseGain(Debt debt) {
        if (debt == null) return 0;
        return debt.getOustandingPrincipal() - quoteRepurchase(debt);
    }

    /**
     * Buys one bond back and takes it off the books.
     *
     * PAID FOR IN CASH, AND ONLY IN CASH. A city that cannot afford this is
     * refused rather than allowed to go overdrawn for it, and that is a rule
     * about what the action MEANS, not a safety check: the overdraft is priced
     * as principal by the same market (getPricedDebt) and charged the emergency
     * rate, so borrowing at the worst rate available in order to retire cheaper
     * paper is strictly worse than doing nothing. Letting the button do it would
     * be handing the player a trap with a green tick on it.
     *
     * THE PRICE IS STRUCK ONCE, and the quote the player was shown is the quote
     * they get. Re-pricing after taking the cash would move the rate (cash
     * position feeds the market) and change the answer between reading it and
     * paying it - the exact "asking the price changes the price" bug that
     * calculateTotalCost() had.
     *
     * @return what it cost, or 0 if nothing happened
     */
    public double repurchaseDebt(Debt debt) {

        if (debt == null) return 0;

        double price = quoteRepurchase(debt);

        if (price <= 0 || price > cash) {
            return 0;
        }

        if (!debtManager.retire(debt)) {
            return 0;   // not on the books; do not charge for it
        }

        cash -= price;

        // Both sides moved - one bond fewer, and less cash - so the market has
        // to be told before anything reads the rate again.
        priceTheDebtMarket();
        debtManager.updateInterest();

        return price;
    }

   public DebtQuote issueEmergencyDebt(double cashNeeded, int duration){

       DebtQuote quote = quoteTBill(cashNeeded, duration, 1000.0);
       if (quote.isEmpty()) return quote;

       System.out.println("A note of $" + formatter.format(quote.faceValue())
               + " with duration of " + duration
               + " was issued for $" + formatter.format(quote.cashReceived())
               + String.format(" at %.2f%%.", quote.marketRate() * 100));

       debtManager.addShortTermTBill(quote.faceValue(), duration, month);
       cash += quote.cashReceived();
       cityDebtRaisedThisMonth += quote.cashReceived();
       cityDiscountThisMonth += quote.faceValue() - quote.cashReceived();

       // The books have changed, so the standing rate has too. Leaving this out
       // let a city borrow and go on being quoted its pre-loan rate until the
       // next month tick.
       debtManager.updateInterest();
       return quote;
   }
    
   /**
    * Set by loadGame() from the save, consumed by the next
    * rebuildSimulationState(). -1 means "recompute", which is what a save from
    * before the workforce was carried gets, and what newGame() and every other
    * caller of the rebuild gets too.
    */
   private int pendingWorkforce = -1;

   /**
    * What it costs to supply one more person of dwelling capacity, today.
    *
    * THE LONG-RUN SUPPLY PRICE OF HOUSING, and the floor under the rent. Nobody
    * rationally supplies capacity for more than the cheapest way of supplying
    * it, so this walks the residential templates and takes the lowest cost per
    * head - which is the House by a wide margin at founding ($8.94 against
    * $21.07 for a studio block and $44.16 for a low-rise) and stays the House
    * unless somebody re-costs the catalogue.
    *
    * PRICED AT MARKET, ALWAYS, which is the one place this deliberately
    * disagrees with BusinessInvestment.totalCostOf(). That method charges only
    * the materials the depot cannot supply from stock, because it is answering
    * "what cheque does the player write". This is answering "what does housing
    * cost to make", and a full warehouse does not make concrete free - it means
    * somebody already paid for it. Reading the shortfall here would have rent
    * lurching every time the depot filled or emptied, which is inventory noise
    * wearing a price's clothes.
    *
    * NO LAND. See the block above - it is the most important comment attached
    * to this mechanic.
    */
   /* =====================================================================
      WHY LAND IS NOT IN THE RENT FLOOR
      ---------------------------------------------------------------------
      The first version of this method put land in at today's market price,
      because a developer plainly does pay for the ground. It measured
      catastrophically and the reason it measured catastrophically is a
      modelling error, not a tuning one, so it is written down here.

      WHAT HAPPENED. Land in this game gets dearer with population and with
      how much the city has annexed - both premiums multiply and neither is
      bounded - so over a 4,001-month run the price per square foot went from
      $0.000455 to $0.2585, a factor of 568. With land in the floor, the cost
      of supplying one person of capacity went 8.94 -> 94.55, rent went 0.113
      -> 1.217, and since rent is about three quarters of the price-index
      basket the whole price level went with it: index 8.387, wages "lifted"
      736%. Downstream: households spent their money on rent instead of food,
      so the shops delivered 40% of what was planned against 54% before,
      hunger went 42% -> 49%, the bank's capital ratio fell from 10.8% to 2.2%
      and it failed 179 times, real GDP roughly halved, and the city ended
      144,635 people instead of 223,777.

      WHY IT WAS WRONG, and this is the part worth keeping. LAND PRICE IS A
      CAPITALISED RENT. What a plot is worth is the discounted value of what
      can be earned on it; it is an OUTPUT of the rental market, not an input
      to it. Feeding it back in as a cost is reverse causation, and reverse
      causation in a loop is how you get a number that grows because it grew.
      It is also why real cities have their LOWEST rental yields exactly where
      land is dearest - Tokyo and Hong Kong run about 2% gross - which the
      16% this game's founding numbers imply cannot express.

      WHAT IS LEFT IS RIGHT. The floor is the STRUCTURE: the cash the building
      costs and the materials it eats, at today's material price. That is a
      real cost, it moves with the industrial economy, and it does not
      capitalise anything. Land still reaches rent, but through QUANTITY
      rather than price - dear or scarce land means fewer homes get built,
      fewer doors against the same households is a higher scarcity multiple,
      and that channel is bounded because dear rent empties the city.
      Charging land in the floor AND counting its scarcity in the multiple was
      charging for it twice.

      NOTE WHAT THIS DOES NOT CHANGE: BusinessInvestment still pays the full
      market price for land when it decides what to build
      (totalCostOf() includes it), so dense housing still wins when ground is
      expensive. Only the rent FLOOR is structure-only.
      ===================================================================== */

   public double marginalHousingCost() {

       double materialPrice = buildingManager.getConstructionMaterialPrice();
       double cheapest = 0;

       for (BuildingsTemplate t : buildingManager.getTemplates()) {
           if (t == null || t.getCategory() != BuildingType.RESIDENTIAL) continue;
           if (t.getCapacity() <= 0) continue;

           double cost = t.getCashCost()
                   + t.getConstructionMaterials() * Math.max(0, materialPrice);
           double perCapacity = cost / t.getCapacity();
           if (perCapacity <= 0) continue;
           if (cheapest <= 0 || perCapacity < cheapest) cheapest = perCapacity;
       }
       return cheapest;
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
    /*
     * The workforce the month was worked by, if the save carries it.
     *
     * recomputeWorkforce() derives it from the population the save was taken
     * WITH; updatePop() derives it from the population the month STARTED with.
     * In a growing city those differ by a month of arrivals, and everything
     * below this line - the fill rate, four payrolls, the wage tax, the
     * construction discount - is priced off whichever one wins. Restoring is
     * right; recomputing is the fallback for saves written before it was
     * carried, where it is still far better than the zero it replaced.
     */
    if (pendingWorkforce >= 0) {
        populationManager.restoreWorkforce(pendingWorkforce);
        pendingWorkforce = -1;
    } else {
        populationManager.recomputeWorkforce();
    }
    populationManager.updateJobs(jobs);

    /*
     * THE WAGES THE MONTH WAS WORKED AT, not the ones the constants say.
     *
     * Mirrors the monthly path exactly - SimulationEngine.updatePopulation()
     * puts this same call between updateJobs() and the wage bill - and it is
     * take, not reprice. advanceMonth() would walk every wage another step
     * toward its target, which is running a month of the labour market with the
     * calendar standing still: the same trap refreshEconPrices() exists to
     * avoid.
     *
     * Without it the load path silently used PayTier's constants while the live
     * city used its market, and the two disagreed on the wage tax from the
     * first month back. Caught as "next-month income is identical across a
     * save" - which is exactly what that assertion is for.
     */
    populationManager.takeWagesFrom(labourMarket);
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
    economyManager.setOccupiedHomes(carriedOccupiedHomes >= 0
            ? carriedOccupiedHomes : families.homesNeeded());
    economyManager.setHouseholdCount(families.totalHouseholds());
    economyManager.setHousingSeekers(families.studioSeekers(), families.familySeekers(),
            families.studioSeekerHeads(), families.familySeekerHeads());
    economyManager.setMarginalHousingCost(marginalHousingCost());

    /*
     * AND THE MATCH, which the rent is billed off.
     *
     * The households are restored, not rebuilt, so nothing here has run
     * house() - and rentWeight would have been zero, dropping the landlords
     * back onto the pre-2026-09-07 average-home formula for exactly one month
     * after every load. Caught by SaveFileCheck ("a month later both cities
     * have paid the same bill") to the cent, which is what that assertion is
     * for. The same load-path-parity bug class, seventh sighting.
     */
    /*
     * ONE PASS, NOT THE LIVE PATH'S TWO. The households came out of the save
     * already squeezed - the shares and the doubling-up are in the restored
     * matrix - so running the valves again would convert a second batch of
     * single adults into flatshares that never existed.
     */
    // The full sequence, not just the match - or stillUnplaced keeps whatever
    // the live path last left in it, which is a figure about a different month.
    families.noteUnplaced(families.house(buildingManager.homesBySize()));
    // ...and the two-pass figure the save carried wins over that one pass,
    // because migration reads it. See FamilyModel.adoptCarriedUnplaced().
    families.adoptCarriedUnplaced();
    if (carriedRentWeight > 0) {
        /*
         * BOTH HALVES, RESTORED, not one total re-split.
         *
         * The saved figures are what the landlords were actually paid and must
         * not move - and with two prices the SPLIT is load-bearing too, so it
         * is saved rather than reconstructed. A save from before the split
         * carries a studio half of zero, which is exactly right for it: it had
         * one price, rentPrice is still that price, and the whole of its weight
         * belongs to the family half. No version test needed.
         */
        double studio = Math.max(0, Math.min(carriedStudioWeight, carriedRentWeight));
        families.setRentWeight(studio, carriedRentWeight - studio);
    }
    economyManager.getCommercialHandler().setRentWeight(
            families.studioRentWeight(), families.familyRentWeight());
    businessInvestment.setFamilies(families);
    economyManager.setSeniors(cohorts.get(AgeBand.SENIOR));
    economyManager.setTotalJobs(populationManager.getTotalJobs());
    economyManager.setTotalWage(populationManager.getTotalWage());

    // The split behind that total, because the wage tax is banded now and a
    // single figure cannot be charged at four different rates.
    economyManager.setWageDetail(populationManager.getStaffedWagePerType());
    economyManager.setEnergyRatio(servicesManager.getEnergyRatio());
    economyManager.setWaterRatio(servicesManager.getWaterRatio());
    economyManager.setRoadRatio(servicesManager.getRoadRatio());

    /*
     * The fourth ratio, on the load path.
     *
     * Its sibling above sits in SimulationEngine.updateEconomy, and this whole
     * class of bug is a line that made it into one of those two places and not
     * the other - mining's wages went that way, and the property-tax charge
     * before it. Health is restored above, so this hands the sectors the sick
     * rate the save was taken with rather than a healthy month they never had.
     */
    economyManager.setHealthRatio(health.getWorkRatio());

    /*
     * And the health service's books, on the load path.
     *
     * Same class of gap as the health ratio above and as mining's wages before
     * it: set in advanceDemographics() on the monthly path and nowhere here, so
     * a reloaded city would show a healthcare bill of zero, collect no fees,
     * and hand the treasury a month of free hospitals. Restored above, applied
     * here.
     */
    economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());

    economyManager.updateIndustrialWages(populationManager.getWagesPerType());
    economyManager.updateStoreWages(
            populationManager.getWagesPerType(),
            buildingManager.getJobArrayPerCategory(BuildingType.COMMERCIAL),
            buildingManager.getJobArrayByName("Commercial Bank")
    );

    economyManager.updateJobFillRate(populationManager.getJobFillRate());
    economyManager.updateHeavyIndustryWages(populationManager.getWagesPerType());

    /*
     * MINING'S WAGES, which this path had never set.
     *
     * Its only call site was SimulationEngine.updateEconomy, so after a load the
     * mine's wage array was all zeros - and startOfMonthUpdate() runs
     * updateMiningReport() BEFORE simulateMonth() gets a chance to fill it in.
     * So the first month after any reload the mine booked its full revenue
     * against a payroll of zero, banked the inflated net income to its own cash,
     * and the city collected profit tax on money nobody earned.
     *
     * The three sibling sectors were all here already; mining was simply
     * forgotten. That is the shape of this whole class of bug - not a wrong
     * calculation, a missing call on one of two paths that have to agree.
     */
    economyManager.updateMiningWages(populationManager.getWagesPerType());

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
    economyManager.refreshMiningReport();

    // GDP reads commercialHandler.getNetIncome(), so it has to come after the
    // refresh above to see this month's figure rather than a stale one.
    priceTheDebtMarket();
    debtManager.updateInterest();

    // Was only done by the second rebuild pass in loadGameSave(). It has to
    // happen somewhere, and this is the one rebuild there is now.
    ConstructionHandler construction = servicesManager.getConstructionHandler();
    servicesManager.updateFromGame(construction::setMaterialsInventory,
            buildingManager.getConstructionMaterials());
    servicesManager.updateFromGame(construction::setMaterialsPrice,
            buildingManager.getConstructionMaterialPrice());
    servicesManager.updateFromGameInt(construction::setMaterialsConsumed, materialsConsumed);
    construction.setMaterialsImportBill(monthlyMaterialImportBill);

    /* ---------------- the three the monthly path sets and this did not ----------------
     *
     * All found in one sweep, all the same shape: a setter with exactly one call
     * site, on the month loop, so a reloaded city carried a zero where the live
     * one carried a figure. None of them is a wrong calculation.
     */

    // Utilities read $0 on the finances screen after every load.
    servicesManager.getUtilitiesHandler().setBilledRevenue(
            economyManager.getSectorElectricityCharges(),
            economyManager.getSectorWaterCharges());
    economyManager.setUtilityIncome(servicesManager.getServiceNetIncome());

    // The residents' statement, without booking the month onto the running
    // total a second time - see refreshHouseholdAccounts().
    refreshHouseholdAccounts();

    /*
     * And the government's own books. updateGovernment() is called from inside
     * updateNationalAccounts() and nowhere else, so a reloaded city showed every
     * tax line, both pension lines, both pie charts and the SURPLUS/DEFICIT as
     * zero.
     *
     * THIS IS THE FLOOR, NOT THE ANSWER. It re-derives what it can from live
     * state, which is four of the ten revenue lines short - the wage tax, the
     * contributions, the utility income and the profit taxes are all struck
     * inside a tick and cannot be rebuilt from a city standing still. loadGame()
     * restores the saved block over the top of this a moment later; what is left
     * here is what a save too old to carry one gets, and what a brand new city
     * gets, both of which are cities with no month behind them.
     */
    economyManager.refreshGovernmentAccounts(
            landManager.getLandSalesThisMonth(),
            cityCapitalSpending,
            landManager.getLandPurchasesThisMonth(),
            economyManager.getInterestAccrued());
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
        double restoredCityInterest = 0;
        double[] restoredPropertyTaxCharges = null;
        DataSave restoredFlows = null;
        double[] restoredInterestCharges = null;

        // The ore price the month traded at. Restored AFTER the rebuild, which
        // re-prices the market from current supply and demand and would
        // otherwise overwrite it - the same trap the interest charges sit in.
        double restoredOrePrice = 0;
        double restoredFoodPrice = 0;

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

            // An empty file parses to null rather than throwing. Caught by the
            // RuntimeException handler below either way, but a NullPointerException
            // in the log is a worse explanation than this sentence.
            if (loaded == null) {
                GameLog.note(GameFiles.slotLabel(slot) + " is empty or unreadable: " + path);
                loadFailure = "This save file is empty.";
                return;
            }

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

            /*
             * The reset that used to be commented out here is gone for good.
             * loadGameSave() now calls buildWorld() before reaching this, so
             * there is nothing left to clear - the managers are new objects.
             * Clearing two of them by hand was the wrong shape anyway: it is a
             * list, and a list falls behind.
             */
            // Load simple fields
            this.cash = loaded.getCash();
            this.month = loaded.getMonth();
            /*
             * THE YARD IS IN UNITS, AND THE UNIT CHANGED (save format 20).
             *
             * A unit of construction material was $2,000 through format 19
             * and is $18,000 now, with every template's count re-derived to
             * match. The yard's stock is a count of those units, so a
             * format-19 yard read as it stands is nine times the material it
             * was: 2,000 units that were $4M of aggregate become $36M of it,
             * and the next order the city places is free. It is read at what
             * it was WORTH instead - the count in old units times the old
             * price, divided by the new one. See GameVersion.
             */
            int yard = loaded.getConstructionMaterials();
            if (loaded.getSaveFormat() < 20) {
                yard = (int) Math.round(yard * GameVersion.MATERIALS_UNIT_BEFORE_20
                        / BuildingManager.MATERIALS_WORLD_PRICE);
            }
            buildingManager.setConstructionMaterials(yard);
            economyManager.setStoreInventory(loaded.getStoreInventory());
            economyManager.getCommercialHandler().setLastMonthSales(loaded.getStoreLastMonthSales());
            economyManager.setIndustryFoodInventory(loaded.getIndustryFoodInventory());
            populationManager.setPopulation(loaded.getPopulation());
            this.population = loaded.getPopulation();

            /*
             * The land office and the ore, before rebuildSimulationState().
             *
             * The listing has to be back before anything re-prices the market,
             * or the window refills with parcels 1-10 all over again and the
             * player's plot - possibly the deposit they were saving for - is
             * replaced by a different one at a different price.
             *
             * A save from before parcels carries no listing, and updateMarket()
             * fills a fresh one, which is the only sensible answer for a city
             * that never had a window.
             */
            if (loaded.getLandListing() != null) {
                landManager.getMarket().restoreListingState(loaded.getLandListing());
            }
            // ...and the prices it was quoting, which the listing does not hold.
            landManager.getMarket().restorePriceState(loaded.getLandMarketPrices());
            landManager.restoreIron(loaded.getIronDeposits(), loaded.getIronReserveTonnes());

            /*
             * Policy. Each piece is restored only if the save carries it in this
             * build's shape - a save from before the Policy tab existed simply
             * keeps the defaults, which are the single city rate applied to
             * everything, which is exactly what that save meant.
             */
            economyManager.getTaxPolicy().restorePolicyState(loaded.getTaxPolicyState());

            // Format 18 and earlier carry nothing here, and a null restores as a
            // no-op - which leaves a founding city's opening buffer, the same
            // position those saves already behaved as having.
            //
            // The rows first, then the cells over the top when the save has
            // them. The family model is not back yet, so a row-only save
            // leaves the cells' counts for the plan to fill and the second
            // restore below to put right; a save with cells carries its own.
            householdBalance.restore(loaded.getHouseholdBalance());
            householdBalance.restoreCells(loaded.getHouseholdCellKeys(),
                    loaded.getHouseholdCells());
            bank.setCash(loaded.getBankCash());
            bank.setBranchesCapitalised(loaded.getBankBranchesCapitalised());
            bank.setProfitLastMonth(loaded.getBankProfitLastMonth());
            bank.setDepositRate(loaded.getBankDepositRate());
            bank.restoreSolvency(loaded.getBankSolvency());
            bank.restoreLastMonth(loaded.getBankLastMonth());
            /*
             * ...and the doors that were LET.
             *
             * Read here and used by rebuildSimulationState() further down,
             * which would otherwise re-derive it by clamping what the family
             * model needs against the stock the month ENDED with - a bigger
             * number than the one the live city clamped against mid-month, so
             * a reloaded city let more flats than the saved one did. See
             * DataSave.getHousingOccupancy(). Left at -1, and therefore
             * re-derived exactly as before, on a save that does not carry it.
             */
            double[] occupancy = loaded.getHousingOccupancy();
            if (occupancy != null && occupancy.length > 0) {
                carriedOccupiedHomes = occupancy[0];
            }
            economyManager.setBankTax(loaded.getBankTaxCharged());
            foreign.restore(loaded.getForeignAccounts());
            /*
             * LOAD-PATH PARITY, for the third time on this class.
             *
             * A city reloaded with USD paper on its books values that paper at
             * whatever rate the instruments were built with - 1.00 - until the
             * first month ticks. Long enough for the debt screen, the credit
             * rating and the player's next decision to all be wrong, and a
             * whole month of a save-and-reload comparison to disagree.
             */
            debtManager.setExchangeRate(foreign.getRate());
            debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());
            debtManager.restoreForeignStanding(loaded.getForeignStanding());
            priceIndex.restore(loaded.getPriceIndex());
            world.restore(loaded.getWorldEconomy());
            /*
             * ...AND FOR THE FOURTH TIME, INTO THE ECONOMY. The line above
             * this block restores the rate into the debt market. This one
             * restores it into everything that prices an import - food,
             * materials, ore, the book value of every building - which is the
             * sibling that was missing. A cache holds the price the month was
             * traded at, so it is carried rather than re-derived; the product
             * is the fallback for a save that does not carry it.
             */
            double traded = loaded.getTradedExchangeRate();
            economyManager.setExchangeRate(
                    traded > 0 ? traded : foreign.getRate() * world.getPriceLevel());
            /*
             * The player's own decision, and the one thing on the debt market
             * that is not derived from the city's books. A reload that dropped
             * it would quietly reset monetary policy to 3%.
             */
            if (loaded.getPolicyRate() > 0) debtManager.setPolicyRate(loaded.getPolicyRate());
            hotMoney.restore(loaded.getCapitalFlows());
            hotMoney.setMonth(month);
            bank.setForeignDeposits(hotMoney.getStock());
            // Absent from an older save: a city that never invested abroad.
            outward.restore(loaded.getOutwardInvestment());
            // ...or whose companies had no owners yet.
            equity.restore(loaded.getEquityKeys(), loaded.getEquity());
            labourMarket.setCostOfLiving(loaded.getCostOfLiving());
            economyManager.getCommercialHandler().setStoreSellPrice(loaded.getStoreSellPrice());
            // Zero means a save written before rent became a lagged price; the
            // setter refuses it and the founding value stands, which is the
            // right answer for a city that was charging the formula anyway.
            economyManager.getCommercialHandler().setRentPrice(loaded.getRentPrice());
            /*
             * A save from before the split carries no studio price and comes
             * back as zero, which setStudioRentPrice() refuses - so the
             * studio market opens at whatever the constructor put there and
             * walks to its own target within a lease. Restoring a zero would
             * hand every studio tenant a free flat for a year.
             */
            economyManager.getCommercialHandler()
                    .setStudioRentPrice(loaded.getStudioRentPrice());

            /*
             * THE UNIT, AND THEN THE CONSTANTS THAT DEPEND ON IT.
             *
             * Every money constant in this codebase - the price of a House, a
             * shop's opening price, what a bank branch gathers, what a burial
             * costs - is seeded from a compile-time figure in FOUNDING dollars
             * at construction, and this game object was constructed a moment
             * ago. The saved BALANCES are already in the reformed unit; the
             * constants are not. Re-seeding them here, once, at the restored
             * unit is what closes that gap - and it is idempotent, so a save
             * loaded twice lands in the same place.
             */
            denomination.restore(loaded.getDenomination());
            double unit = denomination.getUnit();
            if (unit != 1) {
                buildingManager.seedConstants(unit);
                economyManager.getCommercialHandler().seedConstants(unit);
                labourMarket.seedConstants(unit);
                bank.seedConstants(unit);
                landManager.seedConstants(unit);
                healthcare.seedConstants(unit);
                foreign.seedConstants(unit);
                economyManager.getTaxPolicy().seedConstants(unit);
                education.seedConstants(unit);
                hotMoney.seedConstants(unit);
            }
            carriedRentWeight = loaded.getRentWeight();
            carriedStudioWeight = loaded.getRentWeightStudio();

            /*
             * Before rebuildSimulationState() re-runs the retail report, or it
             * runs it with no budget constraint and the shops sell what a
             * headcount wanted - which is the model as it stood before the
             * constraint existed, for exactly one month after every load.
             */
            carriedRetailCapacity = loaded.getRetailCapacity();
            carriedRetailWant = loaded.getRetailWant();

            boolean[] savedSubsidy = loaded.getAutoSubsidy();
            if (savedSubsidy != null && savedSubsidy.length == autoSubsidy.length) {
                System.arraycopy(savedSubsidy, 0, autoSubsidy, 0, autoSubsidy.length);
            }

            economyManager.restoreSalesTaxLedger(loaded.getSalesTaxLedger());
            economyManager.setMiningCash(loaded.getMiningCash());
            restoredOrePrice = loaded.getIronLocalPrice();
            restoredFoodPrice = loaded.getFoodLocalPrice();
            /*
             * A save from before the standing policy existed. A city that was
             * paying a retainer had decided its builders were worth keeping, so
             * that decision is carried across as the toggle rather than dropped
             * - the retainer itself no longer does anything.
             */
            this.constructionSubsidy = Math.max(0, loaded.getConstructionSubsidy());
            if (this.constructionSubsidy > 0) {
                setAutoSubsidised(PolicySector.CONSTRUCTION, true);
            }

            // The warning, restored with the crisis that caused it. A save from
            // before this was carried decodes to -1, which isConstructionShedding()
            // reads as "nothing to warn about" - correct for a save that has no
            // record of one.
            restoreConstructionShedding(loaded.getConstructionShedMonth(),
                    loaded.getConstructionShedPoints());

            // Read here, consumed by rebuildSimulationState() below. It has to
            // be in place BEFORE the rebuild rather than corrected after it:
            // the fill rate, every sector's payroll, the wage tax and the
            // construction discount are all priced off the workforce during
            // that call, so putting it right afterwards fixes the number and
            // leaves everything derived from it wrong.
            pendingWorkforce = loaded.getWorkforce();
            economyManager.setCommercialCash(loaded.getCommercialCash());
            economyManager.setRealEstateCash(loaded.getRealEstateCash());
            economyManager.setIndustrialCash(loaded.getIndustrialCash());
            economyManager.getHeavyIndustryHandler()
                    .setCash(loaded.getHeavyIndustryCash());
            households.setCumulativeSaving(loaded.getHouseholdSavings());
            this.reports = loaded.getReports();
            this.graphs = loaded.getGraphs();
            inbox.restoreFrom(loaded.getNotices());
            sectorBooks.restoreFrom(loaded.getSectorBooks(),
                    loaded.getSectorBooksBefore());
            restoreTreasuryMonth(loaded.getTreasuryMonth());
            // Held, not applied: rebuildSimulationState() has not run yet and
            // it ends by re-striking this block. Put back below it.
            loadedGovernmentMonth = loaded.getGovernmentMonth();

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
             * buildings[] already is - has since been made: the by-id arrays
             * below are what a current save carries, and this positional path
             * is read only for saves from before it.
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
            restoredCityInterest = loaded.getCityInterestAccrued();
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

                    /*
                     * THE OLD NAMES ARE STILL LISTED, AND MUST STAY LISTED.
                     *
                     * This switch is a FORMAT KEY, not a label. Renaming the
                     * instruments to Note / Serial / Term without adding the new
                     * strings here dropped every debt in every save on the floor
                     * - silently, because an unmatched case just falls through
                     * and the city reloads owing nothing. A player would have
                     * seen their debt vanish and called it a gift.
                     *
                     * Same lesson as buildings.json: the display name is free to
                     * change, the id is not. Anything that ever appears in a save
                     * file is an id whatever it is called in the UI.
                     */
                    switch (type) {

                        case "NOTE":
                        case "T-BILL":              // pre-rename saves
                            loadedDebts.add(gson.fromJson(obj, ShortTermTBill.class));
                            break;

                        case "SERIAL":
                        case "MEDIUM-BOND":         // pre-serial saves; see repairAfterLoad
                            MediumTermBond serial = gson.fromJson(obj, MediumTermBond.class);
                            serial.repairAfterLoad();
                            loadedDebts.add(serial);
                            break;

                        case "TERM":
                        case "LONG-BOND":           // pre-rename saves
                            loadedDebts.add(gson.fromJson(obj, LongTermBond.class));
                            break;

                        default:
                            GameLog.note("Save contains an unknown debt type '"
                                    + type + "' - it was not restored.");
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
            GameLog.failure("Could not read " + GameFiles.slotLabel(slot), e);
            loadFailure = "The save file could not be read: " + e.getMessage();
            return;

        } catch (RuntimeException e) {
            /*
             * A truncated or corrupt save throws JsonSyntaxException, which is a
             * RuntimeException and sailed straight through the IOException catch
             * above - the same shape as the IllegalArgumentException that used
             * to abandon half a load in silence.
             *
             * In the packaged build that meant clicking Load did nothing at all:
             * no message, no error, no load, because the exception reached the
             * FX thread's default handler and a stderr that does not exist.
             *
             * Returning before the rebuild is deliberate. A half-applied save is
             * worse than none - the city would be part this save and part
             * whatever was loaded before it, with no way for the player to tell.
             */
            GameLog.failure("Corrupt save in " + GameFiles.slotLabel(slot), e);
            loadFailure = "This save file is damaged and could not be read.";
            return;
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

        /*
         * The city's own accrued interest, which nothing was restoring at all.
         *
         * Unconditional, including when it is zero: a city that has paid its
         * bonds off really does owe nothing, and treating 0 as "absent" would
         * leave whatever the fresh Game happened to be carrying. A save from
         * before this field simply reads 0, which is what those saves already
         * behaved as.
         */
        economyManager.setInterest(restoredCityInterest);

        /*
         * THE HOUSEHOLDS, BEFORE THE REBUILD RATHER THAN AFTER IT.
         *
         * These three used to be restored further down, with the rest of the
         * carried flows, and that was fine while nothing read them. Rent is now
         * charged per occupied HOME, and rebuildSimulationState() re-runs the
         * commercial sector - so a FamilyModel still empty at that moment made a
         * reloaded city collect zero rent for its first month. SaveFileCheck
         * caught it as "both cities have paid the same bill", which is exactly
         * what that assertion is for.
         *
         * Safe to hoist: all three are plain array copies with no dependency on
         * anything the rebuild does, and the rebuild very much depends on them.
         */
        if (restoredFlows != null) {
            cohorts.restore(restoredFlows.getCohorts());
            families.restore(restoredFlows.getFamilies());
            migration.restore(restoredFlows.getMigration());
            health.restore(restoredFlows.getHealth());
            healthcare.restore(restoredFlows.getHealthcare());

            labourMarket.restore(restoredFlows.getLabour());
            populationManager.restoreLicensed(restoredFlows.getLicences());
            education.restore(restoredFlows.getEducation());
            // The students in flight are out of the supply, on the load path
            // exactly as on the monthly one - or a reloaded city has more
            // workers than the live one until its first month tick. Caught by
            // EducationCheck ("out of the supply on both sides").
            populationManager.setStudying(education.getStudying());

            /*
             * SKILLS, OR AN INFERENCE OF THEM.
             *
             * A save written before skill existed has a workforce and no record
             * of what any of them can do. Resetting them all to unskilled would
             * close every hospital in the city on load - the doctors were there
             * a moment ago and the player did nothing. So the skills are read
             * off the posts those workers are demonstrably filling, which is
             * the only inference that leaves the city exactly as it was left.
             *
             * The carried case is a plain array copy and belongs here with the
             * others; the INFERENCE needs the jobs, which do not exist until
             * rebuildSimulationState() has run, so it waits below.
             */
            populationManager.restoreSkilledHeads(restoredFlows.getSkilledWorkforce());

            // Hoisted here with the rest: rebuildSimulationState() reads
            // cityCapitalSpending when it refreshes the government's books.
            businessInvestment.restoreLossMonths(restoredFlows.getSectorLossMonths());
            businessInvestment.restorePopulationHistory(restoredFlows.getPopulationTrend());
            cityCapitalSpending = restoredFlows.getCityCapitalSpending();
            cityMaintenancePaid = restoredFlows.getCityMaintenancePaid();
            /*
             * What the dial paid out last month. A flow, and one the load path
             * cannot re-derive: paySubsidyIfOwed() decides it from a net income
             * that has already been banked. Without it the Policies tab showed
             * a protected sector being supported by nothing at all until the
             * next month ticked.
             */
            double[] paid = restoredFlows.getSubsidyPaid();
            if (paid != null && paid.length == subsidyPaid.length) {
                System.arraycopy(paid, 0, subsidyPaid, 0, subsidyPaid.length);
            }
            monthlyMaterialImports = restoredFlows.getMonthlyMaterialImports();
            monthlyMaterialImportBill = restoredFlows.getMonthlyMaterialImportBill();
            materialsConsumed = restoredFlows.getMaterialsConsumed();
            /*
             * Both counts are units too, and both feed one month's figures -
             * the import line on the Trade screen and the materials line on
             * the construction report. Same conversion as the yard, for the
             * same reason. The bill is money and needs none; a save from
             * before it existed carries no bill, and the count at the old
             * price is what that month's accounts were struck from.
             */
            if (restoredFlows.getSaveFormat() < 20) {
                double k = GameVersion.MATERIALS_UNIT_BEFORE_20
                        / BuildingManager.MATERIALS_WORLD_PRICE;
                if (monthlyMaterialImportBill <= 0) {
                    monthlyMaterialImportBill = monthlyMaterialImports
                            * GameVersion.MATERIALS_UNIT_BEFORE_20
                            * Math.max(0, economyManager.getExchangeRate());
                }
                monthlyMaterialImports *= k;
                materialsConsumed = (int) Math.round(materialsConsumed * k);
            }
        }

        rebuildSimulationState();

        /*
         * The seam between the saved month and the next one, put back last.
         *
         * See carriedRetailCapacity: the rebuild re-derives these and does not
         * reproduce them to the cent, and everything downstream of the budget
         * constraint amplifies the difference. Carried whole, applied after.
         */
        if (carriedRetailCapacity > 0 || carriedRetailWant > 0) {
            economyManager.getCommercialHandler().setSpendingCapacity(carriedRetailCapacity);
            economyManager.getCommercialHandler().setWantedSpend(carriedRetailWant);
        }
        if (carriedRentWeight > 0) {
            // Both halves, and the family one by subtraction so the two always
            // sum to the figure the landlords were paid. See the note at the
            // other restore site.
            double studio = Math.max(0, Math.min(carriedStudioWeight, carriedRentWeight));
            families.setRentWeight(studio, carriedRentWeight - studio);
            economyManager.getCommercialHandler()
                    .setRentWeight(studio, carriedRentWeight - studio);
        }

        /*
         * THE PRICE OF MONEY, BEFORE ANYTHING IS PRICED OFF IT.
         *
         * BusinessDebtManager's risk-free rate is set once a month, at the top
         * of nextMonth(), from the city's own curve - and nothing on the load
         * path set it at all, so a reloaded city sat on the 1% default until
         * its next tick. Everything a household or a business borrowed in the
         * month after a load was priced off that: measured, two cities that
         * were otherwise identical came out 0.13% apart on the household book
         * and $88.90 apart on the bank's interest, and the bank's profit and
         * tax followed.
         *
         * NOT CARRIED, because it does not need to be: it is a pure function
         * of state the save already restores - the city's debt, its rating and
         * the bank's premium. Re-derived here, from the same two calls
         * nextMonth() makes and in the same order, and AFTER the debt is back,
         * which is why it is here rather than inside the rebuild.
         */
        debtManager.setBankPremium(bank.ratePremium());
        economyManager.getBusinessDebtManager().setRiskFreeRate(debtManager.getRate());

        /*
         * The government's month, put back over the top of the rebuild's
         * best guess.
         *
         * After rebuildSimulationState(), because that ends by calling
         * refreshGovernmentAccounts() and would otherwise overwrite this with
         * the partial block it can derive. Same shape as the three flows above,
         * and the same reason: a month is a set of flows, and the rebuild
         * re-derives them from a city that has stopped moving.
         */
        if (loadedGovernmentMonth != null) {
            economyManager.restoreGovernmentMonth(loadedGovernmentMonth);
            loadedGovernmentMonth = null;
        }

        /*
         * The skills of a city that predates skills.
         *
         * Below the rebuild because it reads the posts, and those are put back
         * by the rebuild. A save with no skilled-workforce array is a save from
         * before this existed: its doctors were real and its hospitals were
         * running, so they are inferred from the jobs those workers are
         * demonstrably filling rather than reset to unskilled, which would shut
         * every hospital in the city on load for no reason the player could see.
         */
        if (restoredFlows == null || restoredFlows.getSkilledWorkforce() == null) {
            populationManager.inferBandShareFromJobs();
        }

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
            economyManager.restoreRetailPurchases(
                    restoredFlows.getRetailLocalPurchase(),
                    restoredFlows.getRetailImportPurchase());
            economyManager.restoreMonthFlows(
                    restoredFlows.getRetailCostOfGoods(),
                    restoredFlows.getRetailLocalImports(),
                    restoredFlows.getRetailGlobalImports(),
                    restoredFlows.getRetailFillBasis(),
                    restoredFlows.getRetailImportTax(),
                    restoredFlows.getIndustryDemand(),
                    restoredFlows.getIndustryLocalSalesValue(),
                    restoredFlows.getIndustryUnitsSold(),
                    restoredFlows.getIndustryUnitsImported(),
                    // A save from before roads carries no basis. Falling back to
                    // what the city looks like now is exactly what those saves
                    // used to do, and their ratios were 1, so it costs nothing.
                    restoredFlows.hasRatioBasis()
                            ? restoredFlows.getEnergyRatioBasis() : getEnergyRatio(),
                    restoredFlows.hasRatioBasis()
                            ? restoredFlows.getWaterRatioBasis() : getWaterRatio(),
                    restoredFlows.hasRatioBasis()
                            ? restoredFlows.getRoadRatioBasis() : getRoadRatio(),
                    // Sickness needs no hasRatioBasis test: a save from before
                    // it carries 1, and 1 is what those cities ran at.
                    restoredFlows.getHealthRatioBasis());

            economyManager.restoreNationalAccounts(restoredFlows.getNationalAccounts());

            demolitionLog.restore(restoredFlows.getDemolitions());
            buildLog.restore(restoredFlows.getBuilds());
            // cohorts, families and migration are restored ABOVE, before
            // rebuildSimulationState() re-runs the economy off them.
            economyManager.getBusinessDebtManager()
                    .restoreWriteOffs(restoredFlows.getWriteOffTotals());
            economyManager.getBusinessDebtManager().restoreCreditRecord(
                    restoredFlows.getRestructureCounts(),
                    restoredFlows.getBlockedMonths());

            servicesManager.getConstructionHandler().restoreOrderBook(
                    restoredFlows.getConstructionCash(),
                    restoredFlows.getConstructionUnearnedRevenue(),
                    restoredFlows.getConstructionBacklogPoints(),
                    restoredFlows.getConstructionMaterialsPending());
            servicesManager.getConstructionHandler().restoreStatement(
                    restoredFlows.getConstructionNetIncome(),
                    restoredFlows.getConstructionProfitTax());

            /*
             * Last, and it has to be last.
             *
             * Everything above rebuilds the sectors' statements from restored
             * inputs - restoreMonthFlows() recomputes all three of them - and
             * this puts the statements the save was actually taken with back
             * over the top. Move it earlier and it is simply overwritten, which
             * is the same trap restoreInterestCharges() above is sitting in.
             *
             * The recompute above is not wasted work: it sets the handlers'
             * non-report state (imports, demand, units sold, the inventory cost)
             * which the next month reads, and it is the fallback when a save
             * carries no statements or ones of a different shape.
             */
            economyManager.restoreIronMarket(restoredOrePrice);
            economyManager.restoreFoodMarket(restoredFoodPrice);

            economyManager.restoreReportState(
                    restoredFlows.getCommercialReport(),
                    restoredFlows.getIndustrialReport(),
                    restoredFlows.getHeavyIndustryReport(),
                    restoredFlows.getMiningReport());

            // And the utility's bill, which is read off those statements, has
            // to be re-struck now that they are the carried ones rather than
            // the recompute rebuildSimulationState() struck it from. Found
            // by LongPlaytest as "utility income differs across a save" in
            // five months of four thousand - the months a ratio moved.
            servicesManager.getUtilitiesHandler().setBilledRevenue(
                    economyManager.getSectorElectricityCharges(),
                    economyManager.getSectorWaterCharges());
            economyManager.setUtilityIncome(servicesManager.getServiceNetIncome());

            /*
             * ...AND THE RESIDENTS' STATEMENT, for exactly the same reason.
             *
             * It is struck inside rebuildSimulationState(), which runs long
             * before this - so it was struck against the RECOMPUTED commercial
             * report, and a recomputed report on a city that has not traded yet
             * has a gross revenue of zero. The households were therefore handed
             * a month in which nobody bought anything, and every pay tier's
             * shopping column on the Household cash flow screen read $0 while
             * the same screen's per-shape grid showed $216-$1,793. The unskilled
             * row then footed to "+$674 left over" on a tier that is underwater.
             *
             * That is finding #4 in claude/simulation-findings.md, filed as its
             * own high-severity bug and suspected of being an instance of #15.
             * It is: one statement read one step too early.
             *
             * The retail seam is re-applied after it, because this call
             * re-derives the spending capacity the seam exists to carry - see
             * carriedRetailCapacity.
             */
            refreshHouseholdAccounts();
            if (carriedRetailCapacity > 0 || carriedRetailWant > 0) {
                economyManager.getCommercialHandler()
                        .setSpendingCapacity(carriedRetailCapacity);
                economyManager.getCommercialHandler()
                        .setWantedSpend(carriedRetailWant);
            }

            /*
             * ...and then the statement the save was taken with, over the top of
             * the one just rebuilt.
             *
             * The rebuild above is not wasted: it re-strikes HouseholdBalance's
             * plan and the shops' spending capacity, which the NEXT month reads,
             * and it is the fallback for a save too old to carry a statement.
             * But it cannot reproduce the split across the tiers - that needs a
             * plan struck from a disposable income the rebuild is in the middle
             * of computing - so the statement itself is carried. See
             * HouseholdAccounts.getStatementState().
             */
            households.restoreStatement(restoredFlows.getHouseholdStatement());

            /*
             * ...and the household counts the savings and the debt are PER, for
             * the same reason and over the top of the same rebuild.
             *
             * HouseholdBalance.restore() has already run once, with the rest of
             * the save; rebuildSimulationState() then re-strikes the rows
             * against a month that has not happened yet and overwrites
             * lastHouseholds with it. That array is the denominator of
             * totalSavings(), totalDebt() and totalInterest(), so the city's
             * whole stock of household money read a third out for one month
             * after every load - $1.29M against $966k, measured. Nothing had
             * noticed because nothing read the total until the bank did.
             */
            //
            // The cells carry their own counts; a row-only save from before
            // them is seeded from the family model, which is back by now.
            householdBalance.restore(restoredFlows.getHouseholdBalance(), families::get);
            householdBalance.restoreCells(restoredFlows.getHouseholdCellKeys(),
                    restoredFlows.getHouseholdCells());

            /*
             * ...and the households' placement, for the third time and the same
             * reason. FamilyModel.restore() runs with the rest of the save, and
             * then rebuildSimulationState() re-houses everybody against a stock
             * it is still in the middle of putting back - so doubledUp came out
             * different from the figure the save was taken with, and the two
             * counters that explain WHY people are doubled up were still at
             * zero. Put back last, like the statement above it.
             */
            families.restore(restoredFlows.getFamilies());
        }

        /*
         * AND THE BANK, LAST OF ALL, FOR THE SAME REASON THE STATEMENTS ARE.
         *
         * Its deposits are every sector's cash and its book is every loan, so
         * it cannot be re-read until all of them are back. Placed earlier - one
         * block up, above the order book - it read the construction sector's
         * cash as zero and came back believing a city with $90M banked had
         * $7.5M, which put a strained bank's premium on a bank that was not
         * strained. A freshly loaded city then quoted five points over what the
         * same city had quoted a moment before it was saved.
         *
         * AND THE DEBT MARKET WITH IT, which was wrong before the bank went
         * anywhere near it and is the eighth sighting of the same shape. The
         * city's GDP, its tax base and its cash are DebtManager's inputs and
         * are pushed into it by priceTheDebtMarket(), which only nextMonth()
         * calls - and the standing rate is struck at the end of
         * processAllDebts(), which only nextMonth() calls either. So a freshly
         * loaded city priced every bond against a GDP of zero until the player
         * clicked next month: measured at five points over what the same city
         * had quoted a moment before it was saved. The rate is not carried in
         * the save because it is derived; deriving it needs these three lines.
         *
         * The live path does all of this at the end of nextMonth(); this is
         * that same sequence, in that same order, at the end of the load.
         */
        priceTheDebtMarket();
        refreshBank();
        debtManager.setBankPremium(bank.ratePremium());
        debtManager.updateInterest();
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

            // ONE CALL, not eight assignments. Copying the lists out here meant
            // every series added had to be remembered in two files, and one
            // forgotten would vanish on reload while looking perfectly fine in a
            // live game. HistoryCheck saves a played city and compares every
            // series after a reload, so a line missing from restoreFrom() fails
            // out loud rather than quietly losing a decade of a graph.
            historySave.restoreFrom(loaded);

            

            System.out.println("History loaded successfully.");

        } catch (IOException | RuntimeException e) {
            // Survivable on its own: the city loads and the graphs start empty.
            // loadHistory() runs INSIDE loadGame(), so throwing here would take
            // a whole city down over a graph.
            GameLog.failure("Could not read the history for "
                    + GameFiles.slotLabel(slot) + " - graphs will start empty", e);
        }

        // NOTE: this used to call rebuildSimulationState() a second time.
        // loadHistory() runs inside loadGame(), which rebuilds immediately
        // afterwards, so every load rebuilt the city twice - the same duplicate
        // pass that was removed from loadGameSave().
    }

    /* =====================================================================
       THE CURRENCY REFORM
       ===================================================================== */

    private final Denomination denomination = new Denomination();

    public Denomination getDenomination() { return denomination; }

    /** Whether the reform button should be showing. */
    public boolean canReformCurrency() {
        return denomination.unlocked(priceIndex.getIndex());
    }

    /**
     * Lops zeros off the currency: one new dollar for `factor` old ones.
     *
     * A CHANGE OF UNITS AND NOTHING ELSE. Every nominal quantity in the city -
     * every price, wage, balance, loan, reserve, tax charge, the exchange rate
     * and the whole of the city's recorded history - is divided by the same
     * factor in the same instant, so that no ratio moves and no real quantity
     * moves. DenominationCheck runs a reformed city beside an unreformed one
     * and requires that they stay the same city; that assertion is what makes
     * this safe, because a reform that missed one balance would be a reform
     * that quietly created or destroyed money.
     *
     * WHAT IS NOT TOUCHED, deliberately:
     *
     *   - anything quoted in FOREIGN money. Food at $0.20 abroad, materials at
     *     $2.00, a bond issued in US dollars: none of them are this city's to
     *     redenominate. They reach the city through the exchange rate, and the
     *     exchange rate is divided, so what they cost here falls with
     *     everything else while what they cost THERE does not move at all.
     *   - anything that is a ratio, a rate, a count or a physical quantity. The
     *     price index, the interest rate, the population, the tonnes in the
     *     ground and the square feet of land are the same numbers afterwards.
     *
     * Called by the player, never by the game.
     *
     * @return true if the reform happened
     */
    public boolean reformCurrency(double factor) {

        if ((!forcedReform && !canReformCurrency()) || !denomination.canLop(factor)) return false;

        double scale = 1.0 / factor;

        // The unit first, so anything that asks mid-reform gets the new answer.
        denomination.lop(factor);

        cash *= scale;
        constructionSubsidy *= scale;
        constructionShedPoints *= scale;
        lastWriteOff *= scale;
        cityCapitalSpending *= scale;
        /*
         * monthlyMaterialImports IS NOT SCALED, and used to be.
         *
         * It counts UNITS of construction material, not dollars - the national
         * accounts multiply it by the material price to get the import value,
         * so scaling it as well divided that value twice. This method's own
         * doc says physical quantities do not move; this was one, and it moved.
         *
         * It was invisible for as long as the field happened to be zero
         * whenever anyone reformed. Housing maintenance drains the yard every
         * month, so the private builds that follow import more, so the field
         * is now reliably non-zero at the player's turn - and DenominationCheck
         * put a reformed city's GDP 56% away from its twin's within one month.
         * A quantity treated as money, which is the same bug as a constant in
         * absolute money, seen from the other side.
         *
         * The BILL beside it is money, and does move.
         */
        monthlyMaterialImportBill *= scale;
        carriedRetailCapacity *= scale;
        carriedRetailWant *= scale;
        cityInterestPaid *= scale;
        foreignDebtRaisedThisMonth *= scale;
        foreignPrincipalRepaidThisMonth *= scale;
        foreignInterestPaidThisMonth *= scale;
        cityDebtRaisedThisMonth *= scale;
        cityDiscountThisMonth *= scale;
        cityPrincipalRepaidThisMonth *= scale;
        cityDebtRaisedForBank *= scale;
        cityDiscountForBank *= scale;

        economyManager.redenominate(scale);
        bank.redenominate(scale);
        foreign.redenominate(scale);
        hotMoney.redenominate(scale);
        outward.redenominate(scale);
        equity.redenominate(scale);
        // The last closed month is what the next dividend is paid on.
        sectorBooks.redenominate(scale);
        priceIndex.redenominate(scale);
        householdBalance.redenominate(scale);
        households.redenominate(scale);
        labourMarket.redenominate(scale);
        populationManager.redenominate(scale);
        migration.redenominate(scale);
        landManager.redenominate(scale);
        debtManager.redenominate(scale);
        buildingManager.redenominate(scale);
        healthcare.redenominate(scale);
        education.redenominate(scale);
        if (servicesManager != null && servicesManager.getUtilitiesHandler() != null) {
            servicesManager.getUtilitiesHandler().redenominate(scale);
        }
        // NOT the construction handler: ServicesManager owns it and
        // EconomyManager holds the same object (Game wires them together at
        // startup), so economyManager.redenominate() has already scaled it.
        // Scaling it here would halve the builders' cash twice.
        if (historySave != null) historySave.redenominate(scale);

        /*
         * ...and the derived figures re-struck, so the screens are in the new
         * money the moment the player closes the dialog rather than a month
         * later. These are the same refreshers the load path uses: they price
         * and re-report, they do not run a month.
         */
        /*
         * THE CACHED EXCHANGE RATES ARE SCALED, NOT RE-PUSHED.
         *
         * Four modules hold their own copy of the rate so they can price
         * imports without asking: FoodMarket, BuildingManager, EconomyManager
         * and every Debt instrument. Missing them was the first thing this got
         * wrong - for one month the city bought its food at the OLD rate in NEW
         * money, a hundredfold import shock that took the currency to its
         * ceiling inside a decade (food at $26.99 against $0.29).
         *
         * The obvious fix, re-pushing the live rate, was the second thing it
         * got wrong and is subtler. Those caches hold the rate the month was
         * TRADED at, which is the rate at the START of the month; re-pushing
         * hands them the rate as it stands now, after the month has moved it.
         * That is a real repricing, not a change of units, and it showed up as
         * a materials price 0.62% away from the unreformed city's - small,
         * permanent, and enough to make the two cities different cities.
         *
         * So each cache is divided where it sits, in each module's own
         * redenominate(). A flow cannot be reconstructed from the state a month
         * ended in, and neither can the price a month was traded at.
         */
        /*
         * AND NOTHING IS RE-DERIVED. NOT refreshEconPrices(), NOT
         * refreshCommercialReport(), NOT refreshBank().
         *
         * Each of the three looked like tidying up and each is a small economic
         * event. They re-strike a figure from the state as it stands NOW rather
         * than as it stood when the month's number was struck: the food market
         * clears again at today's rate rather than the one the month traded at
         * (measured: the materials price 0.62% adrift, permanently), and
         * refreshBank() re-reads the sector tills as they stand after the month
         * rather than as they stood when the deposit book was last set
         * (measured: deposits 1.8e-5 adrift, which is a rounding error until
         * it is a different deposit rate, which is a different interest bill
         * for everybody, which is a different city a decade later).
         *
         * A reform divides what is there. It does not recompute anything, and
         * that includes the sector statements: refreshCommercialReport() and
         * its three siblings were tried and they do not re-report the month,
         * they re-DERIVE it from live state - measured, industry's output came
         * out at 1,168 units against 918 for the same month. Every derived
         * figure is re-derived next month in its proper place.
         */

        return true;
    }


    /**
     * The reform without the price-level gate, for a harness.
     *
     * DenominationCheck needs a city that has been reformed in order to test
     * what a reform does; it does not need that city to have inflated tenfold
     * first, and making it do so would be testing the threshold and the reform
     * at the same time. The threshold is tested on Denomination, where it can
     * be held still.
     */
    boolean reformCurrencyForTest(double factor) {
        forcedReform = true;
        try { return reformCurrency(factor); } finally { forcedReform = false; }
    }

    private boolean forcedReform;

}
 


 
   